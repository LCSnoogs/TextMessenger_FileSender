package android.TextMessenger.control;

import android.TextMessenger.view.ChatsView;
import android.TextMessenger.view.ContactsView;
import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;

public class ItemClickListener implements OnItemLongClickListener 
{
	int parrentType; 
	Activity parrent;
	
	public ItemClickListener(Activity parrent, int parrentType)
	{
		this.parrentType = parrentType;
		this.parrent = parrent;
	}

	// @Override
	public boolean onItemLongClick(AdapterView<?> arg0, View view, int position, long id) 
	{
		if(parrentType == 1){
			((ContactsView)parrent).longPress(position);
			//ClassConstants.getInstance().getContactManager().addContact(position, "olgabolga"+position, false);
		}
		else if(parrentType == 2){
			((ChatsView)parrent).longPress(position);
			//((ChatsView)parrent).openChat(position);
		}
		return false;
	}
}
