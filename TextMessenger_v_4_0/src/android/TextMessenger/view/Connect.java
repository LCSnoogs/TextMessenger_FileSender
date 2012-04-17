package android.TextMessenger.view;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.BindException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Random;

import adhoc.aodv.Node;
import adhoc.aodv.exception.InvalidNodeAddressException;
import adhoc.etc.Debug;
import adhoc.setup.AdhocManager;
import adhoc.setup.PhoneType;
import android.TextMessenger.control.ButtonListner;
import android.TextMessenger.model.ChatManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

public class Connect extends Activity {
	private Button connect;
	private RadioButton signal1;
	private RadioButton signal2;
	private ButtonListner listener;
	private int myContactID;
	private Node node;
	private ChatManager chatManager;
	AdhocManager adHoc;
	String ip;
	int phoneType;
	private TxPowerControl test;

	public void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.connect);
		listener = new ButtonListner(this);
		connect = (Button) findViewById(R.id.connectButton);
		connect.setOnClickListener(listener);
		
		signal1 = (RadioButton) findViewById(R.id.smallAreaRdb);
		signal1.setOnClickListener(listener);
		signal1.setChecked(true);
		
		signal2 = (RadioButton) findViewById(R.id.largeAreaRdb);
		signal2.setOnClickListener(listener);
		
		setup();
	}
	
	private void setup()
	{
		phoneType = getPhoneType();
		ip = "192.168.2." + myContactID;
		test = new TxPowerControl();
		test.runStartCommands1(ip);
		test.canRunRootCommands2();
	}
	
	private int getPhoneType(){
		String model = Build.MODEL;
		Random rand = new Random();

		// This block may possibly be deleted if all phones are equipped with Cyanogen mod, since they would all then use the same script.
		// added HTC EVO as a potential phone.
//		myContactID = rand.nextInt(256);
		if(model.contains("Nexus")){
			myContactID = 3;
			return PhoneType.NEXUS_ONE;
		}
		if(model.contains("Hero")){
			myContactID = 3;
			return PhoneType.HTC_HERO;
		}	
		if(model.contains("PC36100")){
			myContactID = 3;
			return PhoneType.HTC_EVO;
		}
		
		
		return -1;
	}

	public static native int runCommand(String command);

	static {		
		System.loadLibrary("adhocsetup");	
	}

	/**
	 * When connect is clicked, an ad-hoc network is started
	 */
	
	public void clickConnect() {
		
		EditText name = (EditText) findViewById(R.id.displayName);
		String myDisplayName = name.getText().toString();
		if (myDisplayName == "") {
			return;
		}
		try {
			
//			phoneType = getPhoneType();
//			ip = "192.168.2." + myContactID;
			if(phoneType == -1){
				Log.d("PHONE", "No such phoneType");
				return;
			}
						
			WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
			adHoc = new AdhocManager(this, wifi);
		
			String myString = "su startstopadhoc start " + phoneType + " " + ip;
			int result = Connect.runCommand(myString);

			//Starting the routing protocol
			node = new Node(myContactID); 
			
			Debug.setDebugStream(System.out);
			
			chatManager = new ChatManager(myDisplayName, myContactID, node);
			
			node.startThread();

			Intent i = new Intent(this, TabView.class);
			startActivity(i);
		} catch (BindException e) {
			e.printStackTrace();
		} catch (InvalidNodeAddressException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	public void clickRadioButton()
	{
		if (signal1.isChecked())
		{
			test.canRunRootCommands2();
		}
		
		if (signal2.isChecked())
		{
			test.canRunRootCommands();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		if(node != null){
			node.stopThread();
		}
		super.onDestroy();
		if (adHoc != null) {
			//runCommand("su -c \"" + " startstopadhoc stop " + phoneType + " " + ip + "\"");
		}
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
}
