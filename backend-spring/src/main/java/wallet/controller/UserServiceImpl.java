package wallet.controller;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import wallet.WalletApplication;
import wallet.model.BaseResponse;
import wallet.model.Session;
import wallet.model.User;
import wallet.model.enums.ResponseStatus;
import wallet.repository.CardRepository;
import wallet.repository.SessionRepository;
import wallet.repository.TransactionRepository;
import wallet.repository.UserRepository;
import wallet.util.ByteUtils;

/**
 * @author Masoud Taghavian (https://github.com/mtaghavian)
 */
@Component
public class UserServiceImpl implements UserService {

    @Autowired
    private CardRepository cardRepository;
    
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Override
    public BaseResponse<User> signin(HttpServletRequest request, User user) {
        HttpSession httpSession = request.getSession();
        User dbUser = userRepository.findByUsername(user.getUsername());
        if ((dbUser != null) && dbUser.getPassword().equals(ByteUtils.hash(user.getPassword()))) {
            Session session = sessionRepository.findById(httpSession.getId()).get();
            session.validate(dbUser);
            return new BaseResponse(dbUser, ResponseStatus.Ok, null);
        } else {
            return new BaseResponse(null, ResponseStatus.NotOk, "Incorrect username and/or password");
        }
    }

    @Override
    public BaseResponse<User> signup(HttpServletRequest request, User user) {
        User dbUser = userRepository.findByUsername(user.getUsername());
        if (dbUser == null) {
            String problem = User.validate(user);
            if (problem == null) {
                dbUser = new User();
                dbUser.setFirstname(user.getFirstname());
                dbUser.setLastname(user.getLastname());
                dbUser.setUsername(user.getUsername());
                dbUser.setPassword(ByteUtils.hash(user.getPassword()));
                userRepository.saveAndFlush(dbUser);
                return signin(request, user);
            } else {
                return new BaseResponse(null, ResponseStatus.NotOk, problem);
            }
        } else {
            return new BaseResponse(null, ResponseStatus.NotOk, "Duplicate username");
        }
    }

    @Override
    public BaseResponse signout(HttpServletRequest request) {
        HttpSession httpSession = request.getSession();
        Session session = sessionRepository.findById(httpSession.getId()).get();
        session.invalidate();
        return new BaseResponse(null, ResponseStatus.Ok, null);
    }

    @Override
    public BaseResponse delete(HttpServletRequest request) {
        HttpSession httpSession = request.getSession();
        Session session = sessionRepository.findById(httpSession.getId()).get();
        User user = session.getUser();
        BaseResponse r = signout(request);
        sessionRepository.findByUsername(httpSession.getId()).forEach(x -> x.invalidate());
        if (!WalletApplication.adminUsername.equals(user.getUsername())) {
            userRepository.deleteById(user.getId());
        }
        return r;
    }

    @Override
    public BaseResponse changePassword(HttpServletRequest request, Map<String, String> params) {
        String currentPassword = params.get("currentPassword");
        String newPassword = params.get("newPassword");
        HttpSession httpSession = request.getSession();
        User user = sessionRepository.findById(httpSession.getId()).get().getUser();
        if (user.getPassword().equals(ByteUtils.hash(currentPassword))) {
            String problem = User.validatePassword(newPassword);
            if (problem == null) {
                user.setPassword(ByteUtils.hash(newPassword));
                userRepository.save(user);
                return new BaseResponse(null, ResponseStatus.Ok, null);
            } else {
                return new BaseResponse(null, ResponseStatus.NotOk, problem);
            }
        } else {
            return new BaseResponse(null, ResponseStatus.NotOk, "Incorrect current password");
        }
    }

}
