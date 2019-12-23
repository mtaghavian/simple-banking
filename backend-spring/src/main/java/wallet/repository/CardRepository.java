package wallet.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wallet.model.Card;

/**
 * @author Masoud Taghavian (https://github.com/mtaghavian)
 */
@Repository
public interface CardRepository extends JpaRepository<Card, UUID> {

    public List<Card> findAllByUserId(UUID id);

    public void deleteAllByUserId(UUID id);
}
