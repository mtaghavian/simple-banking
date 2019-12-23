package wallet.model.enums;

/**
 * @author Masoud Taghavian (https://github.com/mtaghavian)
 */
public enum TransactionStatus {
    Verifying, // The first stage is verifying destination card number and resolve its name
    Verified, // When verification is successfull
    Performing, // When transaction is queued for performing
    Failed, // Verification or performing stages failure
    Succeeded, // Done successfully
    Canceled // Canceled by user
}
