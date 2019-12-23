package wallet.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wallet.model.Transaction;

/**
 * @author Masoud Taghavian (https://github.com/mtaghavian)
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    public List<Transaction> findAllByUserId(UUID id);

    public void deleteAllByUserId(UUID id);

}
