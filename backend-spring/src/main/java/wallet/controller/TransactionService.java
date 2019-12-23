package wallet.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wallet.model.BaseResponse;
import wallet.model.Transaction;

/**
 * @author Masoud Taghavian (https://github.com/mtaghavian)
 */
@RestController
@RequestMapping("/transaction")
public interface TransactionService {

    // Lists all transactions associated with current user
    @GetMapping("/list")
    public BaseResponse<List<Transaction>> list(HttpServletRequest request);

    // Creates a transaction
    @PostMapping("/create")
    public BaseResponse<Transaction> create(HttpServletRequest request, @RequestBody Transaction trans);

    // Issues a cancel command for a transaction
    @PostMapping("/cancel")
    public BaseResponse<Transaction> cancel(HttpServletRequest request, @RequestBody Transaction trans);

    // Issues performs command for a verified transaction
    @PostMapping("/perform")
    public BaseResponse<Transaction> perform(HttpServletRequest request, @RequestBody Transaction trans);

}
