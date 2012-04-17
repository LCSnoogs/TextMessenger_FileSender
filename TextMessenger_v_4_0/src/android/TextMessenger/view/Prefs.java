package android.TextMessenger.view;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class Prefs extends PreferenceActivity 
{
   private static final String OPT_SIMULATE = "simulate";
   private static final boolean OPT_SIMULATE_DEF = true;

   
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      addPreferencesFromResource(R.xml.settings);
   }
   
   
   public static boolean getSimulate(Context context) {
      return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(OPT_SIMULATE, OPT_SIMULATE_DEF);
   }
   
}