package android.TextMessenger.model;

public interface Constants {
	
	//valid sequence number interval
	public static final int MIN_VALID_SEQ_NUM = 0;
	public static final int MAX_VALID_SEQ_NUM = Integer.MAX_VALUE;
	public static final int MAX_RESENDS = 2;
	
	// Time to wait before a sent pdu is timed out. Application may re-send the message.
	public static final int MESSAGE_ALIVE_TIME = 5000;
	
	// Dr. Byun - A contact will stay on the application for at least CONTACT_ALIVE_TIME.
	// If an online contact doesn't receive any message for CONTACT_ALIVE_TIME, its status will be offline.
	// If an offline contact doesn't receive any message for CONTACT_ALIVE_TIME, it will disappear.
	public static final int CONTACT_ALIVE_TIME = 8000;
	
	//pdu types
	public static final byte PDU_ACK = 1;
	public static final byte PDU_CHAT_REQUEST = 2;
	public static final byte PDU_HELLO = 3;
	public static final byte PDU_MSG = 4;
	public static final byte PDU_NO_SUCH_CHAT = 5;
	public static final byte PDU_QUIT_CHAT = 6;
	public static final byte PDU_FILE = 7;
}
