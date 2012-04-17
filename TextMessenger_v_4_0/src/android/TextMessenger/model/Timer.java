package android.TextMessenger.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

import adhoc.aodv.Node;
import android.TextMessenger.model.pdu.ChatRequest;
import android.TextMessenger.model.pdu.Msg;
import android.TextMessenger.model.pdu.PduInterface;
import android.util.Log;

public class Timer extends Thread {
	private ChatManager chatManager;
	private ContactManager contactManager;
	private Sender sender;
	private AODVObserver aodvobs;
	private volatile boolean keepRunning = true;
	private HashMap<Integer, Integer> pduIdentifiers = new HashMap<Integer, Integer>();
	private LinkedList<PduInterface> aliveQueue = new LinkedList<PduInterface>();
	//TODO sync??
	//private final Object timerLock = new Integer(0);
	
	public Timer (Node node, String myDisplayName, int myContactID, ContactManager contactManager, ChatManager chatManager) {
		this.sender = new Sender(node, this);
		this.contactManager = contactManager;
		this.chatManager = chatManager;
		this.aodvobs = new AODVObserver(node, myDisplayName, myContactID, this, contactManager, chatManager);
		this.start();
	}
	
	public Sender getSender() {
		return sender;
	}
		
	public boolean setTimer(PduInterface pdu, int destContactID) {
		Integer pduExists;
		pdu.setTimer();
		
		synchronized (pduIdentifiers) {
			pduExists = pduIdentifiers.put(pdu.getSequenceNumber(), destContactID);
			// Dr. Byun
	    	// Log.i("AODVObserver", "Timer.java: Put to hash (Key: " + pdu.getSequenceNumber() + ", Value: " + destContactID + ")");
	    	
			if (pduExists != null) {
				return false;
			}
			aliveQueue.addLast(pdu);
		}
		synchronized (aliveQueue) {
			aliveQueue.notify();
		}
		
		return true;
	}
	
	
	public void run() {
		while(keepRunning) {
			try {
				synchronized (aliveQueue) {
					while(aliveQueue.isEmpty()){
						aliveQueue.wait();
					}	
				}
				
				PduInterface pdu = aliveQueue.peek();
				if(pdu != null) {
					long timeToDie = pdu.getAliveTime();
					long sleepTime = timeToDie - System.currentTimeMillis();
					if(sleepTime > 0){
						sleep(sleepTime);
					}
					
					while(timeToDie <= System.currentTimeMillis()) {
						try {
							synchronized (pduIdentifiers) {
								if(pduIdentifiers.containsKey(pdu.getSequenceNumber())) {
									
									// Dr. Byun: uncoment 6 lines to avoid resetTimer. 
									// if(resetTimer(pdu)){
									//     aliveQueue.remove();
									//     sender.resendPDU(pdu,pduIdentifiers.get(pdu.getSequenceNumber()));
									//     pdu.setTimer();
									//     aliveQueue.addLast(pdu);
									// }
							
									
									if(!pdu.resend()){
										if(pdu.getPduType() == Constants.PDU_MSG){
											// Dr. Byun
										    // Log.i("ContactManager", "Timer thread will invoke setContactOnlineStatus().");
										    
											contactManager.setContactOnlineStatus(pduIdentifiers.get(((Msg)pdu).getSequenceNumber()),false);
											chatManager.removeChatsWhereContactIsIn(pduIdentifiers.get(((Msg)pdu).getSequenceNumber()));
										}
										if(pdu.getPduType() == Constants.PDU_CHAT_REQUEST){
											chatManager.removeChatsWhereContactIsIn(pduIdentifiers.get(((Msg)pdu).getSequenceNumber()));
										}
										removePDU(pdu.getSequenceNumber());
									}
								} 
								else {
									aliveQueue.remove();
								}
							}
						} 
						catch (Exception e) {
							// ...
						}
						pdu = aliveQueue.peek();
						if(null == pdu) {
							break;
						}
						timeToDie = pdu.getAliveTime();
					} // end of while()
			    } // end of "if(pdu != null)"
		    } catch (InterruptedException e) {
				// thread stopped
			}
		}
	}
	
	public void stopThread(){
		keepRunning = false;
		this.interrupt();
	}
	
	/**
	 * Method used whenever a message in the timer has received an corresponding ACK message
	 * @param sequenceNumber is the message to be removed from the timer
	 * @return returns true if the pdu successfully removed.
	 */
	public boolean removePDU(int sequenceNumber){
		synchronized (pduIdentifiers) {
			Integer pdu = pduIdentifiers.get(sequenceNumber);
			if (pdu != null){
				pduIdentifiers.remove(sequenceNumber);

				// Dr. Byun
		    	// Log.i("AODVObserver", "Timer.java removePDU() - sequenceNumber: " + sequenceNumber);

				return true;
			}
		}
		 return false;
	}
	
	
	public void removeAllPDUForContact(int contactID){
		synchronized (pduIdentifiers) {
			pduIdentifiers.values().remove(contactID);
		}
	}
}
