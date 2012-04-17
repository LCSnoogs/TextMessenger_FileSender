package android.TextMessenger.control;

import android.TextMessenger.view.R;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class CheckBoxListner implements OnCheckedChangeListener {
	int ChatID;
	boolean isChecked;
	
	public CheckBoxListner(int ChatID){
		this.ChatID = ChatID;
	}

	// @Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		this.isChecked = isChecked;
	}

	public boolean isChecked(){
		return isChecked;
	}
	
	public Integer getChatID(){
		return ChatID;
	}
}
