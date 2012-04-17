package android.TextMessenger.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;

import adhoc.aodv.Node;
import adhoc.aodv.RouteTableManager;
import android.TextMessenger.model.pdu.Ack;
import android.TextMessenger.model.pdu.Hello;
import android.TextMessenger.view.ObserverConst;
import android.util.Log;


/**
 * Handles all received error messages about contacts: new user 
 * discovered, link breakage with an active user, user goes offline, 
 * user goes online..
 * @author rabie
 *
 */
public class ContactManager extends Observable {
	private HashMap<Integer, Contact> contacts;
	private Sender sender;
	private Timer timer;
	private HelloSender helloSender;  // Dr. Byun
	private String myDisplayName;
	private int myContactID;
	private ChatManager chatManager;
	private Node node;  
	// private CheckOfflineStatus checkOfflineStatus;
	private boolean offlineExists;
	
	public ContactManager(String myDisplayName, int myContactID, ChatManager chatManager, Node node){
		this.myDisplayName = myDisplayName;
		this.myContactID = myContactID;
		this.chatManager = chatManager;
		this.node = node;
		
		contacts = new HashMap<Integer, Contact>();
		
		offlineExists = false;		
		// checkOfflineStatus = new CheckOfflineStatus();
		
		timer = new Timer(node, myDisplayName, myContactID, this, chatManager);
		sender = timer.getSender();
		helloSender = new HelloSender (myDisplayName, myContactID, this, chatManager, sender);  // Dr. Byun
		// checkOfflineStatus.start();
	}
	
	public void stopThread(){
		// checkOfflineStatus.stopCheckOfflineStatusThread();
	}
	
	public Sender getSender(){
		return sender;
	}
	
	public int getMyContactID(){
		return myContactID;
	}
	
	public String getMyDisplayName(){
		return myDisplayName;
	}
	
	public ArrayList<String> getOnlineContactsID(){
		ArrayList<String> onlineContact = new ArrayList<String>();
		for(Contact c : contacts.values()){
			if(c.getIsOnline()){
				onlineContact.add(c.getID()+"");
			}
		}
		return onlineContact;
	}
	
	public HashMap<Integer, Contact> getContacts() {
		return contacts;
	}
	
	
	// Dr. Byun - To get the forward route info at "ContactsView.java"
	public Node getNode()
	{
		return this.node;
	}
	
	
	/**
	 * Creates a new contact or sets an existing contact to online
	 * @param contactID
	 * @param displayName
	 * @return true if new contact was created and false if contact already exists 
	 */
	public boolean addContact(int contactID, String displayName, boolean needSendHello){
		Contact contact = contacts.get(contactID);

		// Check if the added contact is not myself.
		if(this.myContactID == contactID)
		{
			return false;
		}
		
		// New contact.
		if(contact == null) {
			if (displayName == null)
			{
				contact = new Contact(contactID, "connecting with " + contactID + " ...");
			}
			else
			{
				contact = new Contact(contactID, displayName);
			}
			synchronized (contacts) {
				contacts.put(contactID, contact);
			}

			contact.setIsOnline(true);
			setChanged();
			notifyObservers(new ObjToObsever(contact, ObserverConst.NEW_CONTACT));
						
			return true;
		}
		else {
			contact.setDisplayName(displayName);
			contact.resetAliveTimer();
			contact.setIsOnline(true);
			setChanged();
			notifyObservers(new ObjToObsever(contact, ObserverConst.CONTACT_ONLINE_STATUS_CHANGED));
			
			return false;			
		}
	}
	
	
	public void setContactOnlineStatus(int contactID, boolean isOnline) {
		Contact c = contacts.get(contactID);
		if (c != null) {
			// Dr. Byun
		    Log.i("ContactManager", contactID + " Online Contact Status: " + isOnline);

			c.setIsOnline(isOnline);
			if (!isOnline) {			    
				offlineExists = true;
				synchronized (contacts) {
					contacts.notify();
				}
			}
			setChanged();
			
			// Dr. Byun
			// notifyObservers(new ObjToObsever(contactID, ObserverConst.CONTACT_ONLINE_STATUS_CHANGED));
			notifyObservers(new ObjToObsever(c, ObserverConst.CONTACT_ONLINE_STATUS_CHANGED));
		}
	}
	
	public boolean removeContact(int contactID){
		chatManager.removeChatsWhereContactIsIn(contactID);
		timer.removeAllPDUForContact(contactID);
		setChanged();
	    notifyObservers(new ObjToObsever(contacts.get(contactID), ObserverConst.REMOVE_CONTACT));
	    contacts.remove(contactID);

		// Dr. Byun
	    Log.i("ContactManager", contactID + " is removed from contacts.");

	    return true;
	}

	public boolean isContactOnline(int contactID) {
		Contact c = contacts.get(contactID);
		if(c != null){
			return c.getIsOnline();
		}
		return false;
	}
	
	public void routeEstablishmentFailurRecived(int contactID){
		// Dr. Byun
	    Log.i("ContactManager", "routeEstablishmentFailurRecived() is invoked. contactID is " + contactID);
	    
	    Contact c = contacts.get(contactID);
	    if((c != null) && (c.getIsOnline())) {
	    	setContactOnlineStatus(contactID, false);
	    	c.resetAliveTimer();
	    }
	    
		chatManager.removeChatsWhereContactIsIn(contactID);
		timer.removeAllPDUForContact(contactID);
	}
	
	
	public void routeInvalidRecived(int contactID) {
		// FIXME! - What's the difference between ROUTE_INVALID and ROUTE_ESTABLISHMENT_FAILURE?
	    Contact c = contacts.get(contactID);
	    if((c != null) && (c.getIsOnline())) {
	    	setContactOnlineStatus(contactID, false);
	    	c.resetAliveTimer();
	    }
	    
		// Hello hello = new Hello(myDisplayName, false);
		// sender.sendPDU(hello, contactID);
	}
	
	
	public void routeEstablishedRecived(int contactID) {
		if(!contacts.containsKey(contactID)) 
		{
			// Note that the contact's displayName is not known yet.
			addContact(contactID, null, false);
		}
		else 
		{
			addContact(contactID, contacts.get(contactID).getDisplayName(), false);
		}
	}
	
	
	public void helloRecived(Hello hello, int sourceContactID) {
		addContact(sourceContactID, hello.getSourceDisplayName(), hello.replyThisMessage());
	}
	
	
	public String getContactDisplayName(int contactID){
		synchronized(contacts){
			Contact c = contacts.get(contactID);
			if(c != null)
			return c.getDisplayName();
			else return "Contact removed";
		}
	}
	
}

