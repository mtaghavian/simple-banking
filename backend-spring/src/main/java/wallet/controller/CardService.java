package wallet.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wallet.model.BaseResponse;
import wallet.model.Card;

/**
 * @author Masoud Taghavian (https://github.com/mtaghavian)
 */
@RestController
@RequestMapping("/card")
public interface CardService {

    // Lists all cards belonging to the current user
    @GetMapping("/list")
    public BaseResponse<List<Card>> list(HttpServletRequest request);

    // Creates a new card
    @PostMapping("/create")
    public BaseResponse<Card> create(HttpServletRequest request, @RequestBody Card card);

    // Reads a card with its associated id
    @PostMapping("/read")
    public BaseResponse<Card> read(HttpServletRequest request, @RequestBody Card card);

    // Updates the fields of a card
    @PostMapping("/update")
    public BaseResponse<Card> update(HttpServletRequest request, @RequestBody Card card);

    // Deletes a card with its associated id
    @PostMapping("/delete")
    public BaseResponse<Card> delete(HttpServletRequest request, @RequestBody Card card);
}
