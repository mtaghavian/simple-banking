package wallet.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import wallet.model.enums.ResponseStatus;

/**
 * @author Masoud Taghavian (https://github.com/mtaghavian)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse<T> {

    private T content;
    private ResponseStatus status;
    private String msg;
}
