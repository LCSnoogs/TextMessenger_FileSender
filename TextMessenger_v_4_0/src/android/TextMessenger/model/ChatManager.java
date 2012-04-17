package android.TextMessenger.model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;

import adhoc.aodv.Node;
import android.TextMessenger.exceptions.ContactOfflineException;
import android.TextMessenger.model.pdu.Ack;
import android.TextMessenger.model.pdu.ChatRequest;
import android.TextMessenger.model.pdu.Msg;
import android.TextMessenger.model.pdu.NoSuchChat;
import android.TextMessenger.model.pdu.FileFragment;
import android.TextMessenger.view.ObserverConst;
import android.util.Log;

public class ChatManager extends Observable {
	private HashMap<Integer, Chat> chats;
	private Sender sender;
	private ContactManager contactManager;
	private int myContactID;
	private String myDisplayName;
	private FileManager sendingFile;

	public ChatManager(String myDisplayName, int myContactID, Node node) {
		this.myContactID = myContactID;
		this.myDisplayName = myDisplayName;
		chats = new HashMap<Integer, Chat>();	
		contactManager = new ContactManager(myDisplayName, myContactID, this, node);
		sender = contactManager.getSender();
		
		ClassConstants classConstant = ClassConstants.getInstance();
		classConstant.setContactManager(contactManager);
		classConstant.setChatManager(this);
	}

	public void sendText(String text, int chatID) throws ContactOfflineException {
		Chat chat = chats.get(chatID);
		if (chat != null) {
			//adds the messages to its own chat
			Msg msg = new Msg(chat.getNextMessageNum(), myContactID, chatID, text);
			chat.addMsgForTextToBeSent(msg);
			
			// Sends a message to all other contacts in the chat.
			// Note that I can't send the message to mySelf (= myContactID)
			for (int contact : chat.getContacts().keySet()) {
				if ((contact != myContactID) && contactManager.isContactOnline(contact)) {
					sender.sendPDU(msg, contact);

					// Dr. Byun
				    Log.i("ChatScreen", "ChatManager.java - sendText() sent " + text + " to " + contact);
				} 
				else if ((contact != myContactID) && !(contactManager.isContactOnline(contact))) {
					// Dr. Byun
				    // Log.i("ChatScreen", "ChatManager.java - sendText(): " + contact + " is not online. removeChat()...");
				    
					removeChat(chatID);
					Log.d("ChatManager", "sendText");
					throw new ContactOfflineException();
				}
			}
		}
	}
	
	public void createFile(String filename, int chatID)
	{
		sendingFile = null;
		sendingFile = new FileManager(myContactID, chatID, filename);
	}
	
	public FileFragment sendFragment(int chatID) throws ContactOfflineException
	{
		Chat chat = chats.get(chatID);
		if (chat != null && sendingFile != null) {
			//adds the messages to its own chat
			FileFragment packet = sendingFile.getFragment();
			// Sends a message to all other contacts in the chat.
			// Note that I can't send the message to mySelf (= myContactID)
			for (int contact : chat.getContacts().keySet()) {
				if ((contact != myContactID) && contactManager.isContactOnline(contact)) {

					if (packet != null)
					{
						packet.setSequenceNumber(chat.getNextMessageNum());
						chat.addMsgForFileToBeSent(packet);
						sender.sendPDU(packet, contact);
					}
					
				} 
				else if ((contact != myContactID) && !(contactManager.isContactOnline(contact))) {
					// Dr. Byun
				    // Log.i("ChatScreen", "ChatManager.java - sendText(): " + contact + " is not online. removeChat()...");
				    
					removeChat(chatID);
					throw new ContactOfflineException();
				}
			}
			return packet;
		}
		return null;
	}
	
	public void textReceived(Msg msg, int sourceContact){
		Chat chat = chats.get(msg.getChatID());
		Ack ack = new Ack(msg.getSequenceNumber());
		sender.resendPDU(ack, sourceContact);
		
		if(chat != null) {
			chat.addMsgForTextReceived(msg);
		}
		else {
			sender.sendPDU(new NoSuchChat(msg.getChatID()), sourceContact);

			// Dr. Byun
		    Log.i("ChatScreen", "ChatManager.java -  textReceived() sent NoSuchChat(). It's a BUG!");
		}
		
	}
	
	public void fileReceived(FileFragment fileFragment, int sourceContact){
		Chat chat = chats.get(fileFragment.getChatID());
		Ack ack = new Ack(fileFragment.getSequenceNumber());
		sender.resendPDU(ack, sourceContact);
		
		if(chat != null) {
			chat.addMsgForFileReceived(fileFragment);
		}
		else {
			sender.sendPDU(new NoSuchChat(fileFragment.getChatID()), sourceContact);

			// Dr. Byun
		    Log.i("ChatScreen", "ChatManager.java -  textReceived() sent NoSuchChat(). It's a BUG!");
		}
		
	}
	
	public boolean removeChat(int chatID){
		Chat c = null;
		synchronized (chats) {
			c = chats.remove(chatID);	
		}
		if( c != null ){
			c.setActive(false);
			setChanged();
			notifyObservers(new ObjToObsever(c, ObserverConst.REMOVE_CHAT));
			return true;
		}
		return false;
	}
	
	public boolean removeChatsWhereContactIsIn(int contactID) {
		synchronized (chats) {
			ArrayList<Integer> removeChatID = new ArrayList<Integer>();
			for (Chat c : chats.values()) {
				if (c.getContacts().containsKey(contactID)) {
					removeChatID.add(c.getID());
				}
			}
			if (removeChatID.isEmpty()) {
				return false;
			} else {
				for (int i : removeChatID) {
					// Dr. Byun
				    Log.i("ChatScreen", "ChatManager.java - removeChatsWhereContactIsIn() will invokes removeChat()");
				    
					removeChat(i);
					Log.d("ChatManager", "removeChatsWhereContactsIsIn");
				}
				return true;
			}
		}
	}

	/** This method is invoked when a user touches a contact from ContactView.
	 * @param contactIDs
	 * @return returns true if a chat were created, and false if the chat already exists
	 * @throws Exception If one of the Contacts is offline
	 */
	public boolean newChatStartedFromMe(HashMap<Integer, String> contactIDs) {
		// Creates a chat ID
		contactIDs.put(myContactID, myDisplayName);
		int chatID = createChatID(contactIDs.keySet().toArray());

		Chat chat = new Chat(contactIDs, chatID, myContactID, myDisplayName);
		Object returnResult = null;
		synchronized (chats) {
			returnResult = chats.put(chatID, chat);
		}
		if (returnResult == null) {  // "null" means that there was no "chatID" on the "chats" HashMap.
			contactIDs.put(myContactID, myDisplayName);
			for (Integer contactID : contactIDs.keySet()) {
				if(contactID != myContactID){
					ChatRequest chatRequest = new ChatRequest(chatID, contactIDs);
					sender.sendPDU(chatRequest, contactID);
					
					Log.i("ChatScreen", "ChatManager.java - newChatStartedFromMe() sent CHAT_REQUEST - 1");
				}
			}
			// Dr. Byun - Since I started a chat, I don't need to notify it to myself.
			// setChanged();
			// notifyObservers(new ObjToObsever(chat, ObserverConst.NEW_CHAT));
			contactIDs.remove(myContactID);
			return true;
		}
		else {
			// Dr. Byun - To invoke a ChatScreen if it's not active now.
			contactIDs.put(myContactID, myDisplayName);
			for (Integer contactID : contactIDs.keySet()) {
				if(contactID != myContactID){
					ChatRequest chatRequest = new ChatRequest(chatID, contactIDs);
					sender.sendPDU(chatRequest, contactID);
					
					Log.i("ChatScreen", "ChatManager.java - newChatStartedFromMe() sent CHAT_REQUEST - 2");
				}
			}

			contactIDs.remove(myContactID);
			return false;
		}
	}
	
	
	/**
	 * getNewChatID() - Written by Dr. Byun to get the ChatID of the contactIDs.
	 */
	public int getNewChatID(HashMap<Integer, String> contactIDs) {
		// Creates a chat ID
		contactIDs.put(myContactID, myDisplayName);
		int chatID = createChatID(contactIDs.keySet().toArray());
		return chatID;
	}
	
	
	
	public Chat getChat(int ID){
		return chats.get(ID);
	}

	/**
	 * 
	 * @param pdu
	 * @param sourceContact
	 */
	public void chatRequestReceived(ChatRequest pdu, int sourceContact) {
		Ack ack = new Ack(pdu.getSequenceNumber());
		sender.resendPDU(ack,sourceContact);
		HashMap<Integer, String> contacts = pdu.getFriends();
		contacts.remove(myContactID);
		Chat chat = new Chat(contacts, pdu.getChatID(), myContactID, myDisplayName);
		Object returnResult = null;
		synchronized (chats) {
			returnResult = chats.put(pdu.getChatID(), chat);
		}
		if (returnResult == null) {  // "null" means there was no "pud.getChatID()" before.
			for(int c : contacts.keySet()){
				contactManager.addContact(c , pdu.getFriends().get(c), false);
			}
			// Notify
			Log.i("ChatScreen", "ChatManager.java notifies NEW_CHAT!");
			setChanged();
			notifyObservers(new ObjToObsever(chat, ObserverConst.NEW_CHAT));
		}
		else
		{
			// Dr. Byun - To invoke "ChatScreen" if it's not active.
			for(int c : contacts.keySet()){
				contactManager.addContact(c , pdu.getFriends().get(c), false);
			}
			// Notify
			Log.i("ChatScreen", "ChatManager.java notifies NEW_CHAT!");
			setChanged();
			notifyObservers(new ObjToObsever(chat, ObserverConst.NEW_CHAT));
		}
	}

	public void notifyTextNotSent(Msg msg) {
		Chat chat = chats.get(msg.getChatID());
		if(chat != null){
			chat.notifyTextNotSent(msg);
			setChanged();
			notifyObservers(new ObjToObsever(msg, ObserverConst.TEXT_NOT_SENT));
		}
	}
/**
 * Creats a unique hash sum from a unique String, with user id's sortet with insert sort
 * @param contactIDs
 * @return Unique ID for chat with contacts
 */
	private int createChatID(Object contactIDs[]) {
		int n = contactIDs.length;
		for (int i = 1; i < n; i++) {
			int value = (Integer) contactIDs[i];
			int j = i;
			while ((j > 0) && (((Integer) contactIDs[j - 1]) > value)) {
				contactIDs[j] = contactIDs[j - 1];
				j--;
			}
			contactIDs[j] = value;
		}
		String chatIDString = contactIDs[0] + "";
		for (int i = 1; i < n; i++) {
			chatIDString += ";" + contactIDs[i];
		}
		return chatIDString.hashCode();
	}
	
	public boolean chatExists(int chatID){
		return chats.containsKey(chatID);
	}
	
	public void noSuchChatRecived(NoSuchChat noChat, int sourceContact){
		Ack ack = new Ack(noChat.getSequenceNumber());
		sender.resendPDU(ack, sourceContact);
		
		// Dr. Byun
	    // Log.i("ContactsView", "ChatManager.java - noSuchChatRecived()");
	    
		removeChat(noChat.getChatID());
		Log.d("ChatManager", "noSuchChatReceived");
	}
	
	// Dr. Byun
	/************************************************************************************
	public void quitChatRecived(QuitChat quitChat, int sourceContact){
		Ack ack = new Ack(quitChat.getSequenceNumber());
		sender.resendPDU(ack, sourceContact);
		
		// Remove the chat
		synchronized (chats) {
			chats.remove(quitChat.getChatID());	
		}
		
		setChanged();
		notifyObservers(new ObjToObsever(quitChat.getChatID(), ObserverConst.QUIT_CHAT_RECEIVED));			
	}
	*************************************************************************************/
}
