package android.TextMessenger.model;

import adhoc.aodv.Node;
import adhoc.aodv.exception.BadPduFormatException;
import android.TextMessenger.model.pdu.ChatRequest;
import android.TextMessenger.model.pdu.Hello;
import android.TextMessenger.model.pdu.Msg;
import android.TextMessenger.model.pdu.NoSuchChat;
import android.TextMessenger.model.pdu.PduInterface;
import android.util.Log;

public class Sender {
	private Node node;
	private Timer timer;
	private volatile int sequenceNumber = Constants.MIN_VALID_SEQ_NUM;
	public Sender(Node node, Timer timer){
		this.node = node;
		this.timer = timer;
	}
	
	public synchronized void sendPDU(PduInterface pdu, int destinationContactID){
		pdu.setSequenceNumber(getNextSequenceNumber());
		if(timer.setTimer(pdu, destinationContactID)){
			node.sendData(pdu.getSequenceNumber(), destinationContactID, pdu.toBytes());
			
			// Dr. Byun
			String my_data = new String(pdu.toBytes());
	    	Log.i("AODVObserver", "Sender.java: Sent a data to " + destinationContactID + ": " + my_data);
		} else {
			//TWO MESSAGES HAVE THE SAME SEQ NUMBER!
			
			// Dr. Byun
			String my_data = new String(pdu.toBytes());
	    	Log.i("AODVObserver", "Sender.java: Do not send a data to " + destinationContactID + ": " + my_data);
		}
	}
	
	/**
	 * Method used if a pdu by some reason didn't receive an ACK message. Resends a message with the same sequence number.
	 */
	public synchronized void resendPDU(PduInterface pdu, int destinationContactID){
			node.sendData(pdu.getSequenceNumber(), destinationContactID, pdu.toBytes());
			
			// Dr. Byun
			String my_data = new String(pdu.toBytes());
	    	Log.i("AODVObserver", "Sender.java: Resent a data to " + destinationContactID + ": " + my_data);

	}

	private int getNextSequenceNumber(){
		if(sequenceNumber < Constants.MAX_VALID_SEQ_NUM){
			return (sequenceNumber++);
		}
		return (sequenceNumber = Constants.MIN_VALID_SEQ_NUM);
	}

}
