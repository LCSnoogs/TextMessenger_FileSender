package android.TextMessenger.model.pdu;

import adhoc.aodv.exception.BadPduFormatException;
import android.TextMessenger.model.Constants;
import android.util.Log;

public class Hello implements PduInterface {
	private int retries = Constants.MAX_RESENDS;
	private int sequenceNumber = -1;
	private long aliveTimeLeft;
	private byte type = Constants.PDU_HELLO;
	private String myDisplayName;
	private int replyThisMessage = 0;
	
	public Hello() {
	
	}
	
	public Hello(String myDisplayName, boolean replyThisMessage){
		this.myDisplayName = myDisplayName;
		if(true == replyThisMessage){
		    this.replyThisMessage = 1;
		}
	}

	// @Override
	public void parseBytes(byte[] bytes) throws  BadPduFormatException{
		String[] s = new String(bytes).split(";",4);
		if(s.length != 4){
			throw new BadPduFormatException(	"Hello: could not split " +
												"the expected # of arguments from bytes. " +
												"Expecteded 4 args but were given "+s.length	);
		}
		try {
			sequenceNumber = Integer.parseInt(s[1]);
			replyThisMessage = Integer.parseInt(s[2]);
			myDisplayName = new String(s[3]);
		} catch (NumberFormatException e) {
			throw new BadPduFormatException("Hello: falied parsing arguments to the desired types");
		}
	}

	// @Override
	public byte[] toBytes() {
		return (type+";"+sequenceNumber+";"+replyThisMessage+";"+myDisplayName).getBytes();
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
	public byte getPduType() {
		return type;
	}
	
	// @Override
	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}
	
	// @Override
	public int getSequenceNumber() {
		return sequenceNumber;
	}
	
	public boolean replyThisMessage(){
		if(1 == replyThisMessage){
			return true;
		}
		return false;
	}
	
	public String getSourceDisplayName(){
		return myDisplayName;
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
