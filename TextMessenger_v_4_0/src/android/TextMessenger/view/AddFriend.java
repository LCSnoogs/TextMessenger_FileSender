package android.TextMessenger.view;

import android.TextMessenger.control.ButtonListner;
import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;


public class AddFriend extends Activity {
	private Button add;
	private ButtonListner listener;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_friend);
		
		add = (Button) findViewById(R.id.find);
		listener = new ButtonListner(this);
		add.setOnClickListener(listener);
		
	}
	
	public String getContactId(){
		EditText name = (EditText) findViewById(R.id.friendID);
		return name.getText().toString();
	}

}
