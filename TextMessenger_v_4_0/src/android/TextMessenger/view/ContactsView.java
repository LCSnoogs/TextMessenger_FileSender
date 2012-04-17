package android.TextMessenger.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import adhoc.aodv.Node;              // Dr. Byun - To get the forward route info - Just for debugging purpose.
import adhoc.aodv.RouteTableManager; // Dr. Byun - To get the forward route info - Just for debugging purpose.
import adhoc.aodv.exception.NoSuchRouteException;
import adhoc.aodv.exception.RouteNotValidException;

import android.TextMessenger.control.ButtonListner;
import android.TextMessenger.control.ItemClickListener;
import android.TextMessenger.model.Chat;
import android.TextMessenger.model.ChatManager;
import android.TextMessenger.model.ClassConstants;
import android.TextMessenger.model.Contact;
import android.TextMessenger.model.ContactManager;
import android.TextMessenger.model.ObjToObsever;
import android.app.ListActivity;
import android.content.Context;    
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;      // Dr. Byun: For toast() method.
import android.widget.AdapterView.OnItemClickListener;

public class ContactsView extends ListActivity implements Observer {
	private ContactManager contactManager;
	private ChatManager chatManager;
	private ItemClickListener itemlisterner;
	private IconicAdapter ica ;
	private ArrayList<String> olga;
	private Button addContactButton;
	private ButtonListner listner;
	private Handler newContactHandler, removeContactHandler, updateContactHandler;
	private TextView t;
	
    public static boolean simulateValue = false;  // For menu option

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.contacts);

		contactManager = ClassConstants.getInstance().getContactManager();
		contactManager.addObserver(this);
		chatManager = ClassConstants.getInstance().getChatmanager();
		chatManager.addObserver(this);

	    t = new TextView(this); 
	    t = (TextView)findViewById(R.id.myname); 
	    t.setText("My Name: " + contactManager.getMyDisplayName());
	    
		olga = new ArrayList<String>();
		
		ica = new IconicAdapter();
		setListAdapter(ica);
		addContactButton = (Button) findViewById(R.id.addcontact);
		listner = new ButtonListner(this);
		addContactButton.setOnClickListener(listner);
		itemlisterner = new ItemClickListener(this, 1);
		getListView().setOnItemLongClickListener(itemlisterner);
		
		
		// Dr. Byun: added following handler.
		getListView().setOnItemClickListener(new OnItemClickListener() {
			// @Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
				
				int cID = Integer.parseInt(olga.get(position));	
				
				Context context = getApplicationContext();
				// String myDsiplayName = contactManager.getContactDisplayName(cID);
				// CharSequence text = "Clicked Item: " + position + ", contactID: " + cID + ", displayName: " + myDsiplayName;
				// int duration = Toast.LENGTH_SHORT;
				// Toast toast = Toast.makeText(context, text, duration);
				// toast.show();
				
				HashMap<Integer, String> chatContact = new HashMap<Integer, String>();
				chatContact.put(cID, contactManager.getContactDisplayName(cID));
				
				boolean myResult;
				myResult = chatManager.newChatStartedFromMe(chatContact);
				int myChatID = chatManager.getNewChatID(chatContact);
				
				Intent i = new Intent(context, ChatScreen.class);
				i.putExtra("chatID", myChatID);
				
				CharSequence text = "Point 1";
				int duration = Toast.LENGTH_SHORT;
				Toast toast = Toast.makeText(context, text, duration);
				toast.show();
				
				startActivityForResult(i, 0);	
			}
		});
		
	   
	   newContactHandler = new Handler(){
			@SuppressWarnings("unchecked")
			@Override
			public void handleMessage(Message msg) {
				Bundle d = msg.getData();
				ica.add(d.getInt("CID")+"");
				ica.notifyDataSetChanged();
			}
		};
		
		
		removeContactHandler = new Handler(){
			@SuppressWarnings("unchecked")
			@Override
			public void handleMessage(Message msg) {
				Bundle d = msg.getData();
				ica.remove(d.getInt("CID")+"");
				ica.notifyDataSetChanged();
			}
		};
		
		
		updateContactHandler = new Handler() {
			@SuppressWarnings("unchecked")
			@Override
			public void handleMessage(Message msg) {
				Bundle d = msg.getData();
				
				// Dr. Byun - If the CID doesn't exist on the contact list, 
				//            add it first. Then update the list.
				if( ica.getPosition(d.getInt("CID")+"") < 0)
				{
				    // ica.remove(d.getInt("CID")+"");
				    ica.add(d.getInt("CID")+"");
				}
				
				ica.notifyDataSetChanged();
			}
		};
	}
	
	
    protected void onResume() {
        super.onResume();
		
        // Dr. Byun - To simulate "no wireless signal" simulation.
		simulateValue = Prefs.getSimulate(getApplicationContext());
	    Log.i("ChatScreen", "Simulate Option Value: " + simulateValue);
	    
        // Toast.makeText(this, "onResume", Toast.LENGTH_SHORT).show();
    } 
    
    
	// Dr. Byun - Do not allow a user to go to the previous activity using the back key.
	public void onBackPressed ()
	{
		;
	}
	
	
	public void longPress(int position){
		int cID = Integer.parseInt(olga.get(position));
		contactManager.removeContact(cID);
	}
	
	
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}
	
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.settings:
			startActivity(new Intent(this, Prefs.class));
			return true;
		}
		
		return false;
	}
	   
	   
	public void update(Observable observable, Object arg) {
		Contact c;
		Chat myChat;
		Message m = new Message();
		Bundle b = new Bundle();
		ObjToObsever msg = (ObjToObsever) arg;
		int type = msg.getMessageType();
		
		switch (type) {
		case ObserverConst.NEW_CONTACT:
			// Dr. Byun
		    // Log.i("ContactManager", "ContactsView.java - NEW_CONTACT");
		    Log.i("ContactsView", "NEW_CONTACT");
		    
			c = (Contact)msg.getContainedData();
			b.putInt("CID", c.getID());
			m.setData(b);		
			newContactHandler.sendMessage(m);	
			break;
		
		case ObserverConst.REMOVE_CONTACT:
			// Dr. Byun
		    // Log.i("ContactManager", "ContactsView.java - REMOVE_CONTACT");
		    Log.i("ContactsView", "Received REMOVE_CONTACT");
		    
			c = (Contact)msg.getContainedData();
			b.putInt("CID", c.getID());
			m.setData(b);
			removeContactHandler.sendMessage(m);
			break;
			
		case ObserverConst.CONTACT_ONLINE_STATUS_CHANGED:
			// Dr. Byun
		    // Log.i("ContactManager", "ContactsView.java - CONTACT_ONLINE_STATUS_CHANGED");
		    Log.i("ContactsView", "CONTACT_ONLINE_STATUS_CHANGED");
		    
		    // Dr. Byun - old one!
			// updateContactHandler.sendEmptyMessage(0);
		    
			c = (Contact)msg.getContainedData();
			b.putInt("CID", c.getID());
			m.setData(b);
			updateContactHandler.sendMessage(m);
			break;
			
		case ObserverConst.NEW_CHAT:
			// Dr. Byun
		    Log.i("ContactsView", "NEW_CHAT from ChatManager - chatRequestReceived()");
		    Log.i("ChatScreen", "ContactsView.java - NEW_CHAT from ChatManager - chatRequestReceived()");
		    
			// Dr. Byun - Invoke "ChatScreen" when a user gets the "NEW_CHAT" at the ContactsView.	
			myChat = (Chat) msg.getContainedData();
			
			if(myChat.getActive() == false)
			{
				int myChatID = myChat.getID();
				
				Context context = getApplicationContext();
				Intent i = new Intent(context, ChatScreen.class);
				i.putExtra("chatID", myChatID);
				
				// Dr. Byun - The following toast crashes the app.
				// CharSequence text = "Point 2";
				// int duration = Toast.LENGTH_SHORT;
				// Toast toast = Toast.makeText(context, text, duration);
				// toast.show();
				
				startActivityForResult(i, 0);
			}
			else 
			{
				Log.i("ChatScreen", "ContactsView.java - Point 10");
			}
			break;
			
		case ObserverConst.TEXT_RECIVED:
			// Dr. Byun
		    Log.i("ContactsView", "TEXT_RECIVED");
		    Log.i("ChatScreen", "ContactsView.java - TEXT_RECIVED");
		    
			// Dr. Byun - Invoke "ChatScreen" when a user gets the "TEXT_RECIVED" at the ContactsView.	
		    int myChatID = (Integer) msg.getContainedData();
		    myChat = ClassConstants.getInstance().getChatmanager().getChat(myChatID);
		    
			if((myChat != null) && (myChat.getActive() == false))
			{
				Context context = getApplicationContext();
				Intent i = new Intent(context, ChatScreen.class);
				i.putExtra("chatID", myChatID);
				
				CharSequence text = "Point 3";
				int duration = Toast.LENGTH_SHORT;
				Toast toast = Toast.makeText(context, text, duration);
				toast.show();
				
				startActivityForResult(i, 0);
			}
			break;
			
		default:
			break;
		}
	}
	
	public void addContactButton(){
		Intent i = new Intent(this, AddFriend.class);
		startActivityForResult(i, 0);	
	}
    

	@SuppressWarnings("unchecked")
	class IconicAdapter extends ArrayAdapter {
		
		IconicAdapter() {
			super(ContactsView.this, R.layout.row, olga);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = getLayoutInflater();
			View row = inflater.inflate(R.layout.row, parent, false);
			TextView label = (TextView) row.findViewById(R.id.label);

			// Dr. Byun: Add a forward route number in addition to contact display name.
			// label.setText(contactManager.getContactDisplayName(Integer.parseInt(olga.get(position))));
			int cID = Integer.parseInt(olga.get(position));
			String displayName = contactManager.getContactDisplayName(cID);
			Node node = contactManager.getNode();
			RouteTableManager routeTableManager = node.getRouteTableManager();
			int nextHop = -100;
			try {
				nextHop = routeTableManager.getForwardRouteEntry(cID).getNextHop();
			} catch (NoSuchRouteException e) {
				;
			} catch (RouteNotValidException e) {
				;
			}
			label.setText(displayName + " (" + nextHop + ")");

			ImageView icon = (ImageView) row.findViewById(R.id.icon);

			if (contactManager.isContactOnline(Integer.parseInt(olga.get(position)))) {
				icon.setImageResource(R.drawable.otter);
			} 
			else {
				icon.setImageResource(R.drawable.icon);
			}

			return (row);
		}
	}  // End of Class IconicAdapter
	
	
	// Dr. Byun - From here to the last for testing purpose.
    @Override
    protected void onStart() {
        super.onStart();
        // Toast.makeText(this, "onStart", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // Toast.makeText(this, "onRestart", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        // Toast.makeText(this, "onPause", Toast.LENGTH_SHORT).show();
        super.onPause();
    }

    @Override
    protected void onStop() {
        // Toast.makeText(this, "onStop", Toast.LENGTH_SHORT).show();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // Toast.makeText(this, "onDestroy", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }
    
}
