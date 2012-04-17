package android.TextMessenger.view;


import java.io.File;
import java.util.Observable;
import java.util.Observer;

import android.TextMessenger.control.ButtonListner;
import android.TextMessenger.exceptions.ContactOfflineException;
import android.TextMessenger.model.Chat;
import android.TextMessenger.model.ChatManager;
import android.TextMessenger.model.ClassConstants;
import android.TextMessenger.model.ObjToObsever;
import android.TextMessenger.model.pdu.FileFragment;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;


public class ChatScreen extends Activity implements Observer{
	private EditText messageText;
	private EditText messageHistoryText;
	private Button sendMessageButton;
	private Button quitChatButton;
	private Chat chat;
	private int chatID;
	private ButtonListner listener;
	private ChatManager chatManager;
	private String filepath;
	private String filename;
	private ProgressDialog progressDialog;
	private final int SENDING_DIALOG = 0;
	private final int RECEIVING_DIALOG = 1;
	private ProgressThread progressThread;
	private Handler handler;
	private MediaPlayer player;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.messaging_screen);
		
		chatID = getIntent().getIntExtra("chatID", 0);
		
		// Dr. Byun
	    Log.i("ChatScreen", "onCreate() - chatID: " + chatID);
		
		messageHistoryText = (EditText) findViewById(R.id.messageHistory);
		messageText = (EditText) findViewById(R.id.message);
		messageText.requestFocus();	
		sendMessageButton = (Button) findViewById(R.id.sendMessageButton);
		listener = new ButtonListner(this);
		sendMessageButton.setOnClickListener(listener);
		quitChatButton = (Button) findViewById(R.id.quitChatButton);
		quitChatButton.setOnClickListener(listener);
		
		chatManager = ClassConstants.getInstance().getChatmanager(); 
		// chat = ClassConstants.getInstance().getChatmanager().getChat(chatID);	
		chat = chatManager.getChat(chatID);
		
		if(chat == null){
			this.finish();
		}
		
		chat.setActive(true);
		chat.addObserver(this);
		chatManager.addObserver(this);
		
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				addMessageToHistory((String)msg.getData().getString("msg"));
			}
		};
		player = MediaPlayer.create(this, Settings.System.DEFAULT_NOTIFICATION_URI);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		// chat.setActive(true);
		// chatManager.addObserver(this); 
		// chat.addObserver(this);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		// chat.setActive(false);
		// chatManager.deleteObserver(this);  // Dr. Byun
		// chat.deleteObserver(this);
		// chat.setHaveBeenViewed();
	}
	 	    
	    
	private void addMessageToHistory(String message) {
		if (message != null) {								
			messageHistoryText.append(message);	
		}
	}
	
	// Dr. Byun - Do not allow a user to go to the previous activity using the back key.
	public void onBackPressed ()
	{
		;
	}
	
	public void sendMessage(){
		try {
			// Dr. Byun
		    Log.i("ChatScreen", "sendMessage() will send " + messageText.getText().toString() + " through " + chatID);
		    chatManager.sendText(messageText.getText().toString(), chatID);
		    
			messageText.setText("");
			messageText.requestFocus();	
		} catch (ContactOfflineException e) {
			//Close chat
		}
	}
	
	
	 // @Override
	public void update(Observable observable, Object arg) {
		Message m;
		Bundle b;
		ObjToObsever msg = (ObjToObsever)arg;
		
		int type = msg.getMessageType();
		switch (type) {
		case ObserverConst.NEW_CHAT:
			// Dr. Byun
		    Log.i("ChatScreen", "Received ObserverConst.NEW_CHAT from ChatManager.");
			break;

		case ObserverConst.TEXT_TO_BE_SENT:
			// Dr. Byun
		    Log.i("ChatScreen", "Got ObserverConst.TEXT_TO_BE_SENT");
		    
			m = new Message();
			b = new Bundle();
			b.putString("msg", (String)msg.getContainedData());
			m.setData(b);
			
			//LLK, Fatal error randomly caused a crash in chat session was fixed by putting in a try/catch.
			try {
				handler.sendMessage(m);		
			}
			catch(Exception e){
				Log.i("Error", "Error e:"+e);
			}
			break;

		case ObserverConst.FILE_TO_BE_SENT:
			// Dr. Byun
		    Log.i("ChatScreen", "Got ObserverConst.FILE_TO_BE_SENT");
		    
			m = new Message();
			b = new Bundle();
			b.putString("msg", (String)msg.getContainedData());
			m.setData(b);
			
			//LLK, Fatal error randomly caused a crash in chat session was fixed by putting in a try/catch.
			try {
				handler.sendMessage(m);		
			}
			catch(Exception e){
				Log.i("Error", "Error e:"+e);
			}
			break;

		case ObserverConst.TEXT_RECIVED:
			// Dr. Byun
		    Log.i("ChatScreen", "Got ObserverConst.TEXT_RECIVED.");
			m = new Message();
			b = new Bundle();
			b.putString("msg", (String)msg.getContainedData());
			m.setData(b);

			player.start();
			//LLK, Fatal error randomly caused a crash in chat session was fixed by putting in a try/catch.
			try {
				handler.sendMessage(m);		
			}
			catch(Exception e){
				Log.i("Error", "Error e:"+e);
			}
			break;
			
		case ObserverConst.FILE_RECEIVED:
			// Dr. Byun
		    Log.i("ChatScreen", "Got ObserverConst.FILE_RECIVED.");
			m = new Message();
			b = new Bundle();
			
			m.setData(b);
			String message = (String)msg.getContainedData();
			m.arg1 = Integer.parseInt(message.split(";", 2)[0]);
			filepath = message.split(";", 2)[1];
			filename = filepath.split("/")[filepath.split("/").length - 1];
			m.arg2 = RECEIVING_DIALOG;	
			
			//LLK, Fatal error randomly caused a crash in chat session was fixed by putting in a try/catch.
			try {
				receiverProgressHandler.sendMessage(m);		
			}
			catch(Exception e){
				Log.i("Error", "Error e:"+e);
			}
			break;
			
		case ObserverConst.REMOVE_CHAT:
			// Dr. Byun
		    Log.i("ChatScreen", "ObserverConst.REMOVE_CHAT");
		    
			this.finish();
			break;
			
		default:
			// Dr. Byun
		    Log.i("ChatScreen", "UNKNOWN EVENT!!!");
			break;
		}
	}
	
	private void CreateMenu(Menu menu)
	{
		MenuItem sendFileItem = menu.add(0, 0, 0, "Send File...");
	}
	
	private boolean MenuChoice(MenuItem item)
	{
		switch(item.getItemId())
		{
		case 0:
			// Starts the File Manager activity where the user can pick a file to send.
			Intent fileManagerIntent = new Intent("org.openintents.action.PICK_FILE", Uri.parse("file:///sdcard"));
			fileManagerIntent.putExtra("org.openintents.extra.TITLE", "Please select a file");
			startActivityForResult(fileManagerIntent, 1);
			
			
			return true;
		}
		
		return false;
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == 1)
		{
			if (resultCode == RESULT_OK)
			{
				filepath = data.getData().toString().split("///", 2)[1];			// separates the file path from the scheme
				filename = filepath.split("/")[filepath.split("/").length - 1];		// separates the file name from the file path
				chatManager.createFile(filepath, chatID);
				showDialog(SENDING_DIALOG);
			}
		}
	}
	
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);
		CreateMenu(menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item)
	{
		return MenuChoice(item);
	}
	
	protected Dialog onCreateDialog(int id) {
		
		switch(id) {
        case SENDING_DIALOG:
        	progressDialog = new ProgressDialog(this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.setMessage("Sending " + filename + "...");
			progressDialog.setCancelable(false);
            progressThread = new ProgressThread(senderProgressHandler);
            progressThread.start();
            return progressDialog;
            
        case RECEIVING_DIALOG:
        	progressDialog = new ProgressDialog(this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.setMessage("Receiving File: " + filename + "\nSaving to SD Card...");
			progressDialog.setCancelable(false);
            return progressDialog;
        default:
            return null;
        }
    }
	
	// Updates progress dialog for receiver
	final Handler receiverProgressHandler = new Handler() {
        public void handleMessage(Message msg) {
            int total = msg.arg1;
            if (progressDialog == null)
            {
            	showDialog(msg.arg2);
            } else if (progressDialog != null)
            {
            	progressDialog.setProgress(total);
            }
            if (total >= 100){
            	player.start();
                removeDialog(msg.arg2);
                progressDialog = null;
                openFileDialog();
            }
        }
    };
    
    // Creates a dialog that asks the user if they want to open the file they received. Add in code for other types of files.
    private void openFileDialog()
    {
    	AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setMessage("Do you want to open " + filename + "?");
		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", 
		    new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int which) {
			    	Intent i = new Intent();
			    	i.setAction(android.content.Intent.ACTION_VIEW);
			    	i.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/" + filename)), "image/*");
			    	startActivity(i);
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
    
    // Updates progress dialog for sender
    final Handler senderProgressHandler = new Handler() {
        public void handleMessage(Message msg) {
            int total = msg.arg1;
            if (progressDialog != null)
            {
            	progressDialog.setProgress(total);
            }
            if (total >= 100){
                removeDialog(msg.arg2);
                progressThread = null;
                progressDialog = null;
            }
        }
    };
    
	private class ProgressThread extends Thread {
        Handler mHandler;
        final static int STATE_RUNNING = 1;
        int mState;
        FileFragment packet;
       
        ProgressThread(Handler h) {
            mHandler = h;
        }
       
        // Used to send a fragment every 500 milliseconds without interfering with the main thread.
        public void run() {
        	mState = STATE_RUNNING;
            while (mState == STATE_RUNNING) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Log.e("ERROR", "Thread Interrupted");
                }
                try {
                	packet = chatManager.sendFragment(chatID);
				} catch (ContactOfflineException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                Message msg = mHandler.obtainMessage();
                if (packet != null)
                {
                	msg.arg1 = packet.getFragmentNumber()*100/packet.getTotalFragments();	// Gets a percentage
                	msg.arg2 = SENDING_DIALOG;
                	mHandler.sendMessage(msg);
                }
            }
            
        }
    }
}
