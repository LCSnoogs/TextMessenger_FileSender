package android.TextMessenger.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;

import android.TextMessenger.model.pdu.FileFragment;
import android.TextMessenger.model.pdu.Msg;
import android.TextMessenger.model.pdu.PduInterface;
import android.TextMessenger.view.ObserverConst;

public class Chat extends Observable {
	private HashMap<Integer, String> contacts;
//	private ArrayList<Msg> messages;
	private ArrayList<PduInterface> messages;
	private boolean newMsg;
	private boolean active;  // Dr. Byun: Indicate if this chat is visible through "ChatScreen.java" 
	                         //           activity to user or not.
	private int chatID, myContactID;
	private Integer messageNum;
	private String myDisplayName;
	private FileManager receivedPicture;

	public Chat(HashMap<Integer, String> contacts, int chatID, int myContactID, String myDisplayName) {
		this.chatID = chatID;
		this.contacts = contacts;
		this.myContactID = myContactID;
		this.myDisplayName = myDisplayName;
//		messages = new ArrayList<Msg>();
		messages = new ArrayList<PduInterface>();
		newMsg = false;
		messageNum = 0;
		active = false;
	}

	
	public boolean getActive()
	{
		return active;
	}
	
	
	public void setActive(boolean flag)
	{
		this.active = flag;
	}
	
	public HashMap<Integer, String> getContacts() {
		return new HashMap<Integer, String>(contacts);
	}

	
	public boolean addMsgForTextReceived(Msg msg) {
		synchronized (messages) {
			String message;
			
			messages.add(msg);
			newMsg = true;
			if (msg.getContactID() == myContactID) {
				message = ("I (" + myDisplayName + ") write at " + msg.getTime() + ":\n");
			} else {
				message = (contacts.get(msg.getContactID()) + " writes at " + msg.getTime() + ":\n");
			}
			message += (msg.getText() + "\n\n");
			setChanged();
			notifyObservers(new ObjToObsever(message, ObserverConst.TEXT_RECIVED));
			
			return true;
		}
	}
	
	public boolean addMsgForFileReceived(FileFragment pictureFragment) {
		synchronized (messages) {
			String message;
			messages.add(pictureFragment);
			newMsg = true;
			if (pictureFragment.getOwnerContactID() == myContactID) {
				message = ("I (" + myDisplayName + ") write at " + pictureFragment.getTime() + ":\n");
			} else {
				message = "" + (pictureFragment.getFragmentNumber()*100/pictureFragment.getTotalFragments()) + ";sdcard/" + pictureFragment.getFilename();
			}
			
			if (receivedPicture == null)
			{
				receivedPicture = new FileManager(myContactID);
			}
			
			receivedPicture.addFileFragment(pictureFragment);
			
			if (receivedPicture.isComplete())
			{
				receivedPicture = null;
			} 
			
			setChanged();
			notifyObservers(new ObjToObsever(message, ObserverConst.FILE_RECEIVED));
			
			return true;
		}
	}
	
	public boolean addMsgForTextToBeSent(Msg msg) {
		synchronized (messages) {
			String message;
			
			messages.add(msg);
			newMsg = true;
			
			if (msg.getContactID() == myContactID) {
				message = ("I (" + myDisplayName + ") write at " + msg.getTime() + ":\n");
			} else {
				message = (contacts.get(msg.getContactID()) + " writes at " + msg.getTime() + ":\n");
			}
			message += (msg.getText() + "\n\n");
			setChanged();
			notifyObservers(new ObjToObsever(message, ObserverConst.TEXT_TO_BE_SENT));
			
			return true;
		}
	}
	
	public boolean addMsgForFileToBeSent(FileFragment pictureFragment) {
		synchronized (messages) {
			String message = new String();
			
			messages.add(pictureFragment);
			newMsg = true;
			
			if (pictureFragment.getOwnerContactID() != myContactID){
				message = (contacts.get(pictureFragment.getOwnerContactID()) + " writes at " + pictureFragment.getTime() + ":\n");
				setChanged();
				notifyObservers(new ObjToObsever(message, ObserverConst.FILE_TO_BE_SENT));
			}
			
			
			
			return true;
		}
	}
	
	// Dr. Byun - Not used!
	/***********************************************************************************************
	private void notTextToObserver(Msg msg) {
		String message;
		if (msg.getContactID() == myContactID) {
			message = ("I (" + myDisplayName + ") write at " + msg.getTime() + ":\n");
		} else {
			message = (contacts.get(msg.getContactID()) + " writes at " + msg.getTime() + ":\n");
		}
		message += (msg.getText() + "\n\n");
		setChanged();
		notifyObservers(new ObjToObsever(message, ObserverConst.TEXT_TO_BE_SENT));
	}
	**********************************************************************************************/

	/**
	 * Sets the hasBeanViewed when the Chat is displayed or closed.
	 * 
	 * @param newMsg
	 */
	public void setHaveBeenViewed() {
		newMsg = false;
	}

	public boolean isThereNewMsg() {
		return newMsg;
	}

	
	// Dr. Byun - Not used
	/*****************************************************************************
	public void getTextHistory() {
		// Dr. Byun
	    Log.i("ChatScreen", "Chat.java - getTextHistory() invoked.");
	    
		synchronized (messages) {
			for (Msg msg : messages) {
				notTextToObserver(msg);
			}
		}
	}
	*****************************************************************************/
	

	public String getDisplayname(int contactID) {
		if (contactID == myContactID) {
			return myDisplayName;
		} else {
			return contacts.get(contactID);
		}
	}

	public int getNextMessageNum() {
		synchronized (messageNum) {
			messageNum++;
			return messageNum;
		}
	}

	public int getID() {
		return chatID;
	}

	public void notifyTextNotSent(Msg msg) {
		setChanged();
		notifyObservers(new ObjToObsever(msg, ObserverConst.TEXT_NOT_SENT));
	}
}
