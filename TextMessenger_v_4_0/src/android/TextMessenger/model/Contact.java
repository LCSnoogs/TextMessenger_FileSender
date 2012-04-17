package android.TextMessenger.model;

import java.util.Observable;

import android.util.Log;

public class Contact extends Observable{
	private int ID;
	private String displayName;
	private volatile boolean isOnline = false;
	private long aliveTimeLeft;    // Dr. Byun
	
	public Contact(int ID, String displayName){
		this.ID = ID;	
		this.displayName = displayName;
		this.aliveTimeLeft = Constants.CONTACT_ALIVE_TIME + System.currentTimeMillis();  // Dr. Byun  
	}
	
	public int getID(){
		return ID;
	}
	
	public String getDisplayName(){
		synchronized (displayName) {
			return displayName;	
		}
	}
	
	public void setDisplayName(String name){
		synchronized (displayName) {
			displayName = name;	
		}
	}
	
	public void setIsOnline(boolean flag){
		//TODO notify
		this.isOnline = flag;
	}
	
	public boolean getIsOnline() {
		return isOnline;
	}

	public long getAliveTime() {
		return aliveTimeLeft;
	}
	
	public void resetAliveTimer() {
		aliveTimeLeft = Constants.CONTACT_ALIVE_TIME + System.currentTimeMillis();
	}
}
