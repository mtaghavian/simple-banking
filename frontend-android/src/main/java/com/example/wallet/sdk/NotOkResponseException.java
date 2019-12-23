package com.example.wallet.sdk;

/**
 * @author Masoud Taghavian (https://github.com/mtaghavian)
 */

public class NotOkResponseException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public NotOkResponseException(String msg) {
		super(msg);
	}
}
