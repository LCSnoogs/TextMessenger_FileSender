package android.TextMessenger.exceptions;

import android.util.Log;

public class ContactOfflineException extends Exception{

	private static final long serialVersionUID = 1L;
	
	public ContactOfflineException() {
	}
	
	public ContactOfflineException(String message) {
		super(message);

	}
}
