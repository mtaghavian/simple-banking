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
import wallet.util.ByteUtils;

/**
 * @author Masoud Taghavian (https://github.com/mtaghavian)
 */
@Entity
@NoArgsConstructor
@Getter
@Setter
public class Card extends BaseModel implements Comparable<Card> {

    @Column(length = 50)
    private String name;

    @Column(length = 50)
    private String number;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("userId")
    private User user;

    @Override
    public int compareTo(Card o) {
        return name.compareTo(o.name);
    }

    public static String validate(Card c) {
        String p = ByteUtils.checkLengthAndIllegalCharacters("Name", c.name, 1, 50);
        if (p == null) {
            p = ByteUtils.checkLengthAndIllegalCharacters("Number", c.number, 1, 50);
        }
        return p;
    }

    public static void copy(Card from, Card to) {
        to.setId(from.getId());
        to.setName(from.getName());
        to.setNumber(from.getNumber());
        to.setUser(from.getUser());
    }
}
