package wallet.controller;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wallet.model.BaseResponse;
import wallet.model.User;

/**
 * @author Masoud Taghavian (https://github.com/mtaghavian)
 */
@RestController
@RequestMapping("/user")
public interface UserService {

    // Authorises current session
    @PostMapping("/signin")
    public BaseResponse<User> signin(HttpServletRequest request, @RequestBody User user);

    // Creates a new user then authorises current session
    // Authorises current session
    @PostMapping("/signup")
    public BaseResponse<User> signup(HttpServletRequest request, @RequestBody User user);

    // Unauthorises current session
    @GetMapping("/signout")
    public BaseResponse signout(HttpServletRequest request);

    // Deletes current user if it is not admin user
    // Unauthorises current session
    @GetMapping("/delete")
    public BaseResponse delete(HttpServletRequest request);

    // Changes password of current user profile
    @PostMapping("changePassword")
    public BaseResponse changePassword(HttpServletRequest request, @RequestBody Map<String, String> params);
}
