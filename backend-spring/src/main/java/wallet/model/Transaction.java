package wallet.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import wallet.model.enums.TransactionStatus;
import wallet.util.ByteUtils;

/**
 * @author Masoud Taghavian (https://github.com/mtaghavian)
 */
@Entity
@NoArgsConstructor
@Getter
@Setter
public class Transaction extends BaseModel implements Comparable<Transaction> {

    @Column
    private Long date;

    @Column(length = 50)
    private String sourceCard;

    @Column(length = 50)
    private String targetCard;

    @Column(length = 50)
    private String targetName;

    @Column(length = 30)
    private String password;

    @Column
    private Double amount;

    @Column
    private Boolean canceled;

    @Column
    private TransactionStatus status;

    @Column(length = 100)
    private String msg;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("userId")
    private User user;

    @Override
    public int compareTo(Transaction t) {
        return t.date.compareTo(date);
    }

    public static String validate(Transaction c) {
        String p = ByteUtils.checkLengthAndIllegalCharacters("Source card number", c.sourceCard, 1, 50);
        if (p == null) {
            p = ByteUtils.checkLengthAndIllegalCharacters("Destination card number", c.targetCard, 1, 50);
        }
        if (p == null) {
            if ((c.amount == null) || (c.amount <= 0.0)) {
                p = "Amound has to be a positive number";
            }
        }
        return p;
    }

}
