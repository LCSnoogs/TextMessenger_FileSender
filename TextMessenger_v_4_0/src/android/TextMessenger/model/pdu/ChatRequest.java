package android.TextMessenger.model.pdu;

import java.util.HashMap;

import adhoc.aodv.exception.BadPduFormatException;
import android.TextMessenger.model.Constants;
import android.util.Log;

public class ChatRequest implements PduInterface {
	private int retries = Constants.MAX_RESENDS;
	private int chatID, sequenceNumber = -1;
	private long aliveTimeLeft;
	private HashMap<Integer, String> friends = new HashMap<Integer, String>();
	private byte type = Constants.PDU_CHAT_REQUEST;
	
	public ChatRequest() {
		
	}
	
	public ChatRequest(int chatID, HashMap<Integer, String> friends){
		this.chatID = chatID;
		this.friends = friends;
	}

	// @Override
	public void parseBytes(byte[] bytes) throws BadPduFormatException{
		String[] s = new String(bytes).split(";",4);
		if(s.length != 4){
			throw new BadPduFormatException(	"chatRequest: could not split " +
												"the expected # of arguments from bytes. " +
												"Expecteded 4 args but were given "+s.length	);
		}
		try {
			sequenceNumber = Integer.parseInt(s[1]);
			chatID = Integer.parseInt(s[2]);
			String[] contacts = s[3].split(";");
			for(String contact: contacts){
				String[] contactInfo = contact.split("::",2);
				if(contactInfo.length != 2){
					throw new NumberFormatException();
				} else {
					friends.put(Integer.parseInt(contactInfo[0]), new String(contactInfo[1]));
				}
			}
		} catch (NumberFormatException e) {
			throw new BadPduFormatException("chatRequest: falied parsing arguments to the desired types");
		}
	}

	// @Override
	public byte[] toBytes() {
		String result = type+";"+sequenceNumber+";"+chatID;

		for(int id: friends.keySet()){
			result += ";"+id+"::"+friends.get(id);
		}
		return result.getBytes();
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
	
	public HashMap<Integer,String> getFriends(){
		return friends;
	}
	
	public int getChatID(){
		return chatID;
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
