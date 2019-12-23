package wallet.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import wallet.util.ByteUtils;

/**
 * @author Masoud Taghavian (https://github.com/mtaghavian)
 */

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "Users") // because a table with this class name may exist (reserved names)
public class User extends BaseModel implements Comparable<User> {

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    public void setUsername(String username) {
        this.username = username.toLowerCase().trim();
    }

    @Column(length = 100)
    private String password;

    @Column(length = 100)
    private String firstname;

    @Column(length = 100)
    private String lastname;

    public static String validate(User user) {
        String p = ByteUtils.checkLengthAndIllegalCharacters("Firstname", user.firstname, 1, 100);
        if (p == null) {
            p = ByteUtils.checkLengthAndIllegalCharacters("Lastname", user.lastname, 1, 100);
        }
        if (p == null) {
            p = ByteUtils.checkLengthAndIllegalCharacters("Username", user.username, 1, 100);
        }
        if (p == null) {
            p = validatePassword(user.password);
        }
        return p;
    }

    public static String validatePassword(String pass) {
        String p = ByteUtils.checkLengthAndIllegalCharacters("Password", pass, 8, 100);
        if (p == null) {
            boolean hasDigit = false, hasAlphabetic = false;
            for (int i = 0; i < pass.length(); i++) {
                if (Character.isDigit(pass.charAt(i)) && !hasDigit) {
                    hasDigit = true;
                }
                if (Character.isAlphabetic(pass.charAt(i)) && !hasAlphabetic) {
                    hasAlphabetic = true;
                }
            }
            if (!hasAlphabetic || !hasDigit) {
                p = "Password must contain alphabetic and digit characters";
            }
        }
        return p;
    }

    @Override
    public int compareTo(User user) {
        return getPresentation().compareTo(user.getPresentation());
    }

    public String getPresentation() {
        return firstname + " " + lastname;
    }
}
