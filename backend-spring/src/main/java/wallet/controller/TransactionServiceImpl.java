package wallet.controller;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import wallet.config.KafkaAdapter;
import wallet.model.BaseResponse;
import wallet.model.Transaction;
import wallet.model.User;
import wallet.model.enums.ResponseStatus;
import wallet.model.enums.TransactionStatus;
import wallet.repository.SessionRepository;
import wallet.repository.TransactionRepository;

/**
 * @author Masoud Taghavian (https://github.com/mtaghavian)
 */
@Component
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private KafkaAdapter kafkaAdapter;

    private User getCurrentUser(HttpSession httpSession) {
        return sessionRepository.findById(httpSession.getId()).get().getUser();
    }

    @Override
    public BaseResponse<List<Transaction>> list(HttpServletRequest request) {
        User user = getCurrentUser(request.getSession());
        List<Transaction> list = transactionRepository.findAllByUserId(user.getId());
        Collections.sort(list);
        return new BaseResponse<>(list, ResponseStatus.Ok, null);
    }

    @Override
    public BaseResponse<Transaction> create(HttpServletRequest request, Transaction trans) {
        User user = getCurrentUser(request.getSession());
        trans.setUser(user);
        // Validate
        String problem = Transaction.validate(trans);
        if (problem == null) {
            trans.setCanceled(false);
            trans.setDate(System.currentTimeMillis());
            trans.setStatus(TransactionStatus.Verifying);
            trans = transactionRepository.saveAndFlush(trans);
            kafkaAdapter.send(KafkaAdapter.Topic.verifying, trans);
            return new BaseResponse<>(trans, ResponseStatus.Ok, null);
        } else {
            return new BaseResponse<>(trans, ResponseStatus.NotOk, problem);
        }
    }

    @Override
    public BaseResponse<Transaction> perform(HttpServletRequest request, Transaction trans) {
        try {
            Optional<Transaction> dbTransOptional = transactionRepository.findById(trans.getId());
            if (dbTransOptional.isPresent()) {
                Transaction dbTrans = dbTransOptional.get();
                if (TransactionStatus.Verified.equals(dbTrans.getStatus())) {
                    dbTrans.setStatus(TransactionStatus.Performing);
                    dbTrans.setPassword(trans.getPassword());
                    transactionRepository.save(dbTrans);
                    kafkaAdapter.send(KafkaAdapter.Topic.performing, dbTrans);
                    return new BaseResponse<>(dbTrans, ResponseStatus.Ok, null);
                } else {
                    return new BaseResponse<>(dbTrans, ResponseStatus.NotOk, "Transaction could not be performed");
                }
            } else {
                return new BaseResponse<>(trans, ResponseStatus.NotOk, "Transaction not found");
            }
        } catch (ObjectOptimisticLockingFailureException ex) {
            // Its ok since cancelation is a suggestion!
            return new BaseResponse<>(trans, ResponseStatus.NotOk, "Transaction could not be performed, try again");
        }
    }

    @Override
    public BaseResponse<Transaction> cancel(HttpServletRequest request, Transaction trans) {
        try {
            Optional<Transaction> dbTransOptional = transactionRepository.findById(trans.getId());
            if (dbTransOptional.isPresent()) {
                Transaction dbTrans = dbTransOptional.get();
                dbTrans.setCanceled(true);
                transactionRepository.save(dbTrans);
                return new BaseResponse<>(dbTrans, ResponseStatus.Ok, null);
            } else {
                return new BaseResponse<>(trans, ResponseStatus.NotOk, "Transaction not found");
            }
        } catch (ObjectOptimisticLockingFailureException ex) {
            // Its ok since cancelation is a suggestion!
            return new BaseResponse<>(trans, ResponseStatus.NotOk, "Transaction could not be canceled, try again");
        }
    }

}
