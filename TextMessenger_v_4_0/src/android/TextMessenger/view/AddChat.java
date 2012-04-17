package android.TextMessenger.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import android.TextMessenger.control.ButtonListner;
import android.TextMessenger.control.CheckBoxListner;
import android.TextMessenger.model.ClassConstants;
import android.TextMessenger.model.ContactManager;
import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;


public class AddChat extends ListActivity {
	private Button add;
	private ButtonListner listener;
	private CheckBoxListner checkBoxListner;
	private List<String> contacts;
	private checkBoxAdapter cba;
	private ContactManager contactManager;
	private ViewGroup parent2;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_chat);
		
		contacts = new ArrayList<String>();
		addOnlineContacts();
		
		cba = new checkBoxAdapter();
		setListAdapter(cba);
		
		add = (Button) findViewById(R.id.startChat);
		listener = new ButtonListner(this);
		add.setOnClickListener(listener);
	}
	
	private void addOnlineContacts(){
		contactManager = ClassConstants.getInstance().getContactManager();
		contacts = contactManager.getOnlineContactsID();
		
	}
	
	public void createChat(){
		HashMap<Integer, String> chatContact = new HashMap<Integer, String>();
		ArrayList<CheckBoxListner> checkedPositions = cba.getCheckedPositions();
		
		for(CheckBoxListner i : checkedPositions){
			if(i.isChecked()){
				chatContact.put(i.getChatID(), contactManager.getContactDisplayName(i.getChatID()));
			}
		}
		if(!chatContact.isEmpty()){
			// Dr. Byun - We will delete the whole file.
			// ClassConstants.getInstance().getChatmanager().newChat(chatContact);
		}
		else{
			//TODO Dialog
		}		
	}
	
	
	@SuppressWarnings("unchecked")
	class checkBoxAdapter extends ArrayAdapter {
		ArrayList<CheckBoxListner> checkedList = new ArrayList<CheckBoxListner>() ;
		
		checkBoxAdapter() {
			super(AddChat.this, R.layout.chatcheck, contacts);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = getLayoutInflater();
			View chatcheck = inflater.inflate(R.layout.chatcheck, parent, false);
			TextView label = (TextView) chatcheck.findViewById(R.id.checkboxlabel);

			label.setText(contactManager.getContactDisplayName( Integer.parseInt(contacts.get(position)) ));
			//Sets a Listener on checkBox
			CheckBox cBox = (CheckBox) chatcheck.findViewById(R.id.checkbox);
			CheckBoxListner checkBoxListen = new CheckBoxListner(Integer.parseInt(contacts.get(position)));
			cBox.setOnCheckedChangeListener(checkBoxListen);
			checkedList.add(checkBoxListen);
			
			return (chatcheck);
		}
		
		public ArrayList<CheckBoxListner> getCheckedPositions(){
			return checkedList;
		}
	}
}
