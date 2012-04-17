package android.TextMessenger.model.pdu;

import adhoc.aodv.exception.BadPduFormatException;
import android.TextMessenger.model.Constants;
import android.util.Log;

public class Ack implements PduInterface{
	private int retries = Constants.MAX_RESENDS;
	private int sequenceNumber;
	private byte type = Constants.PDU_ACK;

	public Ack() {
	
	}
	public Ack(int sequenceNumber){
		this.sequenceNumber = sequenceNumber;
	}
	
	// @Override
	public void parseBytes(byte[] bytes) throws BadPduFormatException {
		String[] s = new String(bytes).split(";",2);
		if(s.length != 2){
			throw new BadPduFormatException(	"Ack: could not split " +
												"the expected # of arguments from bytes. " +
												"Expecteded 2 args but were given "+s.length	);
		}
		try {
			sequenceNumber = Integer.parseInt(s[1]);
		} catch (NumberFormatException e) {
			throw new BadPduFormatException("Ack: falied parsing arguments to the desired types");
		}
	}

	// @Override
	public byte[] toBytes() {
		return (type+";"+sequenceNumber).getBytes();
	}

	// @Override
	public long getAliveTime() {
		//alive time not used
		return Long.MAX_VALUE;
	}

	// @Override
	public void setTimer() {
		//an ack message should not be in the timer
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
	// @Override
	public boolean resend() {
		retries--;
		if(retries <= 0){
			return false;
		}
		return true;
	}

	
}
