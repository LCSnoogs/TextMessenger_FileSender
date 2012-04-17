package android.TextMessenger.model.pdu;

import adhoc.aodv.exception.BadPduFormatException;
import android.TextMessenger.model.Constants;
import android.util.Log;

public class NoSuchChat implements PduInterface{
	private int retries = Constants.MAX_RESENDS;
	private byte pduType;
	private int sequenceNumber, chatID;
	private long aliveTimeLeft;
	
	public NoSuchChat() {
	
	}
	
	public NoSuchChat(int chatID){
		this.chatID = chatID;
		pduType = Constants.PDU_NO_SUCH_CHAT;
	}


	// @Override
	public byte getPduType() {
		return pduType;
	}

	// @Override
	public int getSequenceNumber() {
		return sequenceNumber;
	}

	// @Override
	public void parseBytes(byte[] dataToParse) throws BadPduFormatException {
		String[] s = new String(dataToParse).split(";",3);
		if(s.length != 3){
			throw new BadPduFormatException(	"NoSuchChat: could not split " +
												"the expected # of arguments from bytes. " +
												"Expecteded 2 args but were given "+s.length	);
		}
		try {
			sequenceNumber = Integer.parseInt(s[1]);
			chatID = Integer.parseInt(s[2]);
		} catch (NumberFormatException e) {
			throw new BadPduFormatException("NoSuchChat: falied parsing arguments to the desired types");
		}
		
	}

	// @Override
	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;		
	}
	
	// @Override
	public long getAliveTime() {
		return aliveTimeLeft;
	}

	// @Override
	public void setTimer() {
		aliveTimeLeft = Constants.MESSAGE_ALIVE_TIME + System.currentTimeMillis();
	}

	// @Override
	public byte[] toBytes() {
		return (pduType+";"+sequenceNumber+";"+chatID).getBytes();
	}
	
	public int getChatID(){
		return chatID;
	}
	
	// @Override
	public boolean resend() {
		retries--;
		if(retries <= 0){
			return false;
		}
		return true;
	}

}
