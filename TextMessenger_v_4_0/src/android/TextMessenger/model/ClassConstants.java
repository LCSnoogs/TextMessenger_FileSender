package android.TextMessenger.model;

import android.util.Log;

public class ClassConstants {
	private ContactManager contactManager;
	private ChatManager chatManager;
	private static final ClassConstants INSTANCE = new ClassConstants();
	 
	   // Private constructor prevents instantiation from other classes
	   private ClassConstants() {}
	 
	   public static ClassConstants getInstance() {
	      return INSTANCE;
	   }
	   
	   public void setContactManager(ContactManager contactManager){
		   this.contactManager = contactManager;
	   }
	   
	   public void setChatManager(ChatManager chatManager){
		   this.chatManager = chatManager;
	   }
	   
	   public ContactManager getContactManager(){
		   return contactManager;
	   }
	   
	   public ChatManager getChatmanager(){
		   return chatManager;
	   }


}
