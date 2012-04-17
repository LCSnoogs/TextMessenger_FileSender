package android.TextMessenger.control;

import android.TextMessenger.model.ClassConstants;
import android.TextMessenger.model.ContactManager;
import android.TextMessenger.view.AddChat;
import android.TextMessenger.view.AddFriend;
import android.TextMessenger.view.ChatScreen;
import android.TextMessenger.view.ChatsView;
import android.TextMessenger.view.Connect;
import android.TextMessenger.view.ContactsView;
import android.TextMessenger.view.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;

public class ButtonListner implements OnClickListener{
	Activity parent;

	public ButtonListner(Activity parent) {
		this.parent = parent;
	}

	// @Override
	public void onClick(View v) {
		if(v.equals(parent.findViewById(R.id.connectButton))){

			Connect c = (Connect)parent;
			c.clickConnect();
		
		}
		else if(v.equals(parent.findViewById(R.id.smallAreaRdb)))
		{
			Connect c = (Connect)parent;
			c.clickRadioButton();
		}
		else if(v.equals(parent.findViewById(R.id.largeAreaRdb)))
		{
			Connect c = (Connect)parent;
			c.clickRadioButton();
		}
		else if(v.equals(parent.findViewById(R.id.sendMessageButton))){
			ChatScreen chatS = (ChatScreen)parent;
			chatS.sendMessage();
		}
		else if(v.equals(parent.findViewById(R.id.quitChatButton))){			
			AlertDialog alertDialog = new AlertDialog.Builder((ChatScreen)parent).create();
			alertDialog.setMessage("Do you want to quit the chat?");
			alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", 
			    new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int which) {
				    	// ChatScreen chatS = (ChatScreen)parent;
						// chatS.finish();
				    	parent.finish();
				    } 
			    });

			alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "No", 
				new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int which) {
				    	;   // Nothing to do.
				    } 
			    });
			
			alertDialog.show();
			
			
		}
		// Dr. Byun
		// else if (v.equals(parent.findViewById(R.id.addchat))){
		// 	((ChatsView) parent).newChat();
		// }
		else if (v.equals(parent.findViewById(R.id.startChat))){
			AddChat addChat = (AddChat) parent;
			addChat.createChat();
			addChat.finish();
		}
		
		// Dr. Byun - added
		else if (v.equals(parent.findViewById(R.id.addcontact))) {
			((ContactsView) parent).addContactButton();
		}
		else if (v.equals(parent.findViewById(R.id.find))){
			ContactManager contactManager = ClassConstants.getInstance().getContactManager();
			int myContactID = contactManager.getMyContactID();
			
			AddFriend add = (AddFriend) parent;
			String friendID = add.getContactId();
			
			// Start searching if the search field is not empty and the contact ID is not me.		    
			if ((!friendID.equals("")) && (myContactID != (Integer.parseInt(friendID))))
			{			    
				contactManager.addContact(Integer.parseInt(friendID), "Searching for " + friendID, true);
			}
			add.finish();
		}
	}
}
