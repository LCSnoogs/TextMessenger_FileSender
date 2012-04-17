package android.TextMessenger.model;

import java.util.Observable;
import java.util.Observer;

import adhoc.aodv.Node;
import adhoc.aodv.ObserverConst;
import adhoc.aodv.Node.MessageToObserver;
import adhoc.aodv.Node.PacketToObserver;
import adhoc.aodv.exception.BadPduFormatException;
import android.TextMessenger.model.pdu.ChatRequest;
import android.TextMessenger.model.pdu.FileFragment;
import android.TextMessenger.model.pdu.Hello;
import android.TextMessenger.model.pdu.Msg;
import android.TextMessenger.model.pdu.NoSuchChat;
import android.util.Log;

public class AODVObserver implements Observer {
	private Timer timer;
	private ContactManager contactManager;
	private ChatManager chatManager;
	
	public AODVObserver(Node node, String myDisplayName, int myContactID, Timer timer, ContactManager contactManager ,ChatManager chatManager) {
		this.chatManager = chatManager;
		this.timer = timer;
		this.contactManager = contactManager;
		node.addObserver(this);
	}

	// @Override
	public void update(Observable o, Object arg) {
		MessageToObserver msg = (MessageToObserver)arg;
		int userPacketID, destination, type = msg.getMessageType();
		
		switch (type) {
		case ObserverConst.ROUTE_ESTABLISHMENT_FAILURE:			
			int unreachableDestinationAddrerss  = (Integer)msg.getContainedData();
			
	    	// Dr. Byun
	    	Log.i("AODVObserver", "Received ROUTE_ESTABLISHMENT_FAILURE to " + unreachableDestinationAddrerss);
	    	
	    	// Dr. Byun - uncommented.
			contactManager.routeEstablishmentFailurRecived(unreachableDestinationAddrerss);
			break;
			
		case ObserverConst.DATA_RECEIVED:	    	
			parseMessage(	(Integer)((PacketToObserver)msg).getSenderNodeAddress(),
							(byte[])msg.getContainedData()	);
			break;
			
		case ObserverConst.INVALID_DESTINATION_ADDRESS:
	    	// Dr. Byun
	    	Log.i("AODVObserver", "Received INVALID_DESTINATION_ADDRESS");
	    	
			userPacketID = (Integer)msg.getContainedData();
			break;
			
		case ObserverConst.DATA_SIZE_EXCEEDES_MAX:
	    	// Dr. Byun
	    	Log.i("AODVObserver", "Received DATA_SIZE_EXCEEDES_MAX");
	    	
			userPacketID = (Integer)msg.getContainedData();
			break;
			
		case ObserverConst.ROUTE_INVALID:
			destination  = (Integer)msg.getContainedData();
			
	    	// Dr. Byun
	    	Log.i("AODVObserver", "Received ROUTE_INVALID with " + destination);
	    	
	    	// Dr. Byun - uncomment
			contactManager.routeInvalidRecived(destination);
			break;
			
		case ObserverConst.ROUTE_CREATED:
			destination = (Integer)msg.getContainedData();
	    	
			// Dr. Byun
	    	Log.i("AODVObserver", "Received ROUTE_CREATED with " + destination);
	    	
			contactManager.routeEstablishedRecived(destination);
			break;
		
		case ObserverConst.DATA_SENT_SUCCESS:
			// Dr. Byun
	    	// Log.i("AODVObserver", "Received DATA_SENT_SUCCESS");
			break;
			
		default:
	    	// Dr. Byun
	    	Log.i("AODVObserver", "Received UNKNOWN MESSAGE");
			break;
		}
	}
	
	
	private void parseMessage(int senderID, byte[] data){
		// Dr. Byun
		String my_data = new String(data);
		Log.i("AODVObserver", "===============================================================");
		Log.i("AODVObserver", "Received raw data: " + my_data);
		
		String[] split = new String(data).split(";",2);
		try {
			int type = Integer.parseInt(split[0]);
			switch (type) {
			
			case Constants.PDU_MSG:
				System.out.println("Recived: Msg");
				Msg msg = new Msg(senderID);
				msg.parseBytes(data);
				// Dr. Byun
		    	Log.i("AODVObserver", "Received DATA_RECEIVED (PDU_MSG) from " + senderID + ", msg: " + msg);
		    	Log.i("ChatScreen", "AODVObserver.java - Received DATA_RECEIVED (PDU_MSG) from " + senderID + ", msg: " + msg);

		    	chatManager.textReceived(msg, senderID);
				break;
			
			case Constants.PDU_FILE:
				System.out.println("Recived: Picture");
				FileFragment fileFragment= new FileFragment(senderID);
				fileFragment.parseBytes(data);
				
				// Dr. Byun
		    	Log.i("AODVObserver", "Received DATA_RECEIVED (PDU_PICTURE) from " + senderID + ", file: " + fileFragment);
		    	Log.i("ChatScreen", "AODVObserver.java - Received DATA_RECEIVED (PDU_PICTURE) from " + senderID + ", file: " + fileFragment);

		    	chatManager.fileReceived(fileFragment, senderID);
				break;
				
			case Constants.PDU_ACK:
				System.out.println("Recived: ACK");
				
				// Dr. Byun
		    	Log.i("AODVObserver", "Received DATA_RECEIVED (PDU_ACK) from " + senderID);
		    	Log.i("ContactManager", "Received DATA_RECEIVED (PDU_ACK) from " + senderID);
		    	Log.i("ChatScreen", "AODVObserver.java - Received DATA_RECEIVED (PDU_ACK) from " + senderID);
		    	
				timer.removePDU(Integer.parseInt(split[1]));
				break;
				
			case Constants.PDU_CHAT_REQUEST:
				// Dr. Byun
		    	Log.i("AODVObserver", "Received DATA_RECEIVED (PDU_CHAT_REQUEST) from " + senderID);
		    	Log.i("ChatScreen", "AODVObserver.java - Received DATA_RECEIVED (PDU_CHAT_REQUEST) from " + senderID);
		    	
				ChatRequest chatReq = new ChatRequest();
				chatReq.parseBytes(data);
				chatManager.chatRequestReceived(chatReq, senderID);
				break;
				
			case Constants.PDU_HELLO:
				Hello hello = new Hello();
				hello.parseBytes(data);
				System.out.println("TxtMsg - Reciver: Hello from ID: "+senderID+", Return: " + hello.replyThisMessage());
				
				// Dr. Byun
		    	Log.i("AODVObserver", "Received DATA_RECEIVED (PDU_HELLO) from " + senderID + ", replyThisMessage: " + hello.replyThisMessage());
		    	
				contactManager.helloRecived(hello, senderID);
				break;
				
			case Constants.PDU_NO_SUCH_CHAT:
				// Dr. Byun
		    	Log.i("AODVObserver", "Received DATA_RECEIVED (PDU_NO_SUCH_CHAT) from " + senderID);
		    	Log.i("ChatScreen", "AODVObserver.java - Received DATA_RECEIVED (PDU_NO_SUCH_CHAT) from " + senderID);
		    	
				NoSuchChat noSuchChat = new NoSuchChat();
				noSuchChat.parseBytes(data);
				chatManager.noSuchChatRecived(noSuchChat,senderID);
				break;
			
			default:
				// Dr. Byun
		    	Log.i("AODVObserver", "UNKNOWN PDU");
				break;
			}
		} catch (NumberFormatException e) {
			// Dr. Byun
	    	Log.i("AODVObserver", "NumberFormatException error. Discard it.");
		} catch (BadPduFormatException e) {
			// Dr. Byun
	    	Log.i("AODVObserver", "BadPduFormatException error. Discard it.");
		}
	}
}
