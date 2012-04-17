package android.TextMessenger.view;

public interface ObserverConst {
	
	public static final int NEW_CONTACT = 1;
	public static final int CONTACT_ONLINE_STATUS_CHANGED = 2;
	public static final int REMOVE_CONTACT = 4;
	public static final int REMOVE_CHAT = 5;
	public static final int NEW_CHAT = 6;
	public static final int TEXT_RECIVED = 7;
	public static final int TEXT_NOT_SENT = 8;
	public static final int TEXT_TO_BE_SENT = 9;    // Dr. Byun
	public static final int QUIT_CHAT_RECEIVED = 10;    // Dr. Byun
	public static final int FILE_RECEIVED = 11;
	public static final int FILE_TO_BE_SENT = 12;
}
