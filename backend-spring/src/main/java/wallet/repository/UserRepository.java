package wallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wallet.model.User;

import java.util.UUID;

/**
 * @author Masoud Taghavian (https://github.com/mtaghavian)
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    public User findByUsername(String username);
}
