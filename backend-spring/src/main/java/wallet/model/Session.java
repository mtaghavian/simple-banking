package wallet.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Masoud Taghavian (https://github.com/mtaghavian)
 */

@NoArgsConstructor
@Getter
@Setter
public class Session {

    private String id;

    private User user;

    private Long lastModified;

    public Session(String id, User user, Long lastModified) {
        this.id = id;
        this.user = user;
        this.lastModified = lastModified;
    }

    public void validate(User user) {
        this.user = user;
    }

    public void invalidate() {
        this.user = null;
    }

    public boolean isValidated() {
        return this.user != null;
    }

}
