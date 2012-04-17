package android.TextMessenger.model;

/*
 * Dr. Byun: Send a HELLO PDU to all associated contacts periodically.
 */
import java.util.HashMap;
import android.TextMessenger.model.pdu.Hello;
import android.util.Log;

public class HelloSender extends Thread {
	private String myDisplayName;
	private ContactManager contactManager;
	private Sender sender;
	private HashMap<Integer, Contact> contacts;
	private volatile boolean keepRunning = true;
	
	public HelloSender (String myDisplayName, int myContactID, ContactManager contactManager, ChatManager chatManager, Sender sender) {
		this.myDisplayName = myDisplayName;
		this.sender = sender;
		this.contactManager = contactManager;
		this.start();
	}
	
	
	public void run() {
		int cID;
		contacts = contactManager.getContacts();		
		while(keepRunning) {
			try {
				// Dr. Byun
				Log.i("HelloSender", "==========================================================");
				
				Thread.sleep(4000);  // sleep for 4 seconds
				synchronized (contacts) 
				{
					// Send HELLO PDU to all contacts
					for(Contact c : contacts.values()) 
					{
						cID = c.getID();
						Hello hello = new Hello(myDisplayName, false);			
						sender.sendPDU(hello, cID);
						Log.i("HelloSender", "Sent a HELLO to " + cID);
					}
				}
				
				synchronized (contacts) 
				{
					for(Contact c : contacts.values()) 
					{
						cID = c.getID();
						long timeToDie = c.getAliveTime();
						
						if(c.getIsOnline()) 
						{
							if (timeToDie <= System.currentTimeMillis()) 
							{
								Log.i("HelloSender", cID + " becomes offline");
								contactManager.setContactOnlineStatus(cID, false);
								c.resetAliveTimer();
							}
							else
							{
								Log.i("HelloSender", cID + " is online");
							}
						}
						else
						{
							if (timeToDie <= System.currentTimeMillis()) 
							{
								contactManager.removeContact(cID);
								
								// Note: This "break" is important. We can't continue the
								// iteration of HashMap if we removed an item at HashMap.
								break;
							}
							else
							{
								Log.i("HelloSender", cID + " is offline");
							}
						}
					}
				}
				
		    } catch (InterruptedException e) {
				// thread stopped
			}
		}
	}
	
	public void stopThread()
	{
		keepRunning = false;
		this.interrupt();
	}
}
