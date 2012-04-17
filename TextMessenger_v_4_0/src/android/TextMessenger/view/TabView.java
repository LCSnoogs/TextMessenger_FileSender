package android.TextMessenger.view;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;

public class TabView extends TabActivity {
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.tab);
		
	    Resources res = getResources(); // Resource object to get Drawables
	    TabHost tabHost = getTabHost();  // The activity TabHost
	    TabHost.TabSpec spec;  // Resusable TabSpec for each tab
	    Intent intent;  // Reusable Intent for each tab

	    // Create an Intent to launch an Activity for the tab (to be reused)
	    intent = new Intent().setClass(this, ContactsView.class);
	    // Initialize a TabSpec for each tab and add it to the TabHost
	    spec = tabHost.newTabSpec("contacts").setIndicator("Contacts",res.getDrawable(R.drawable.ic_tab_contacts)).setContent(intent);
	    tabHost.addTab(spec);

	    // Do the same for the other tabs
	    intent = new Intent().setClass(this, ChatsView.class);
	    spec = tabHost.newTabSpec("chats").setIndicator("Chats", res.getDrawable(R.drawable.ic_tab_artists)).setContent(intent);
	    tabHost.addTab(spec);

	    tabHost.setCurrentTab(2);
	}

}
