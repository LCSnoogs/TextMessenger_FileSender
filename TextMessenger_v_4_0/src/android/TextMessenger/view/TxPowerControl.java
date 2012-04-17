// How to get root access and execute commands in your Android application ? 
// http://muzikant-android.blogspot.com/2011/02/how-to-get-root-access-and-execute.html

package android.TextMessenger.view;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import android.TextMessenger.control.ButtonListner;

public class TxPowerControl {    
 /** Called when the activity is first created. */
 public boolean canRunRootCommands()
 {
   boolean retval = false;
   Process suProcess;
   
   try
   {
     suProcess = Runtime.getRuntime().exec("su");
     
     DataOutputStream os = new DataOutputStream(suProcess.getOutputStream());
     DataInputStream osRes = new DataInputStream(suProcess.getInputStream());
     
     if (null != os && null != osRes)
     {
       // Getting the id of the current user to check if this is root
       os.writeBytes("id\n");
       os.flush();

       String currUid = osRes.readLine();
       boolean exitSu = false;
       if (null == currUid)
       {
         retval = false;
         exitSu = false;
         Log.d("ROOT", "Can't get root access or denied by user");
       }
       else if (true == currUid.contains("uid=0"))
       {
         retval = true;
         exitSu = true;
         Log.d("ROOT", "Root access granted");
         
         os.writeBytes("iwconfig eth0 txpower 100mW\n");
         os.flush();
         
       }
       else
       {
         retval = false;
         exitSu = true;
         Log.d("ROOT", "Root access rejected: " + currUid);
       }

       if (exitSu)
       {
         os.writeBytes("exit\n");
         os.flush();
       }
     }
   }
   catch (Exception e)
   {
     // Can't get root !
     // Probably broken pipe exception on trying to write to output stream (os) after su failed, meaning that the device is not rooted
     
     retval = false;
     Log.d("ROOT", "Root access rejected [" + e.getClass().getName() + "] : " + e.getMessage());
   }

   return retval;
 }
 
 
 
 
 public boolean canRunRootCommands2()
 {
   boolean retval = false;
   Process suProcess;
   
   try
   {
     suProcess = Runtime.getRuntime().exec("su");
     
     DataOutputStream os = new DataOutputStream(suProcess.getOutputStream());
     DataInputStream osRes = new DataInputStream(suProcess.getInputStream());
     
     if (null != os && null != osRes)
     {
       // Getting the id of the current user to check if this is root
       os.writeBytes("id\n");
       os.flush();

       String currUid = osRes.readLine();
       boolean exitSu = false;
       if (null == currUid)
       {
         retval = false;
         exitSu = false;
         Log.d("ROOT", "Can't get root access or denied by user");
       }
       else if (true == currUid.contains("uid=0"))
       {
         retval = true;
         exitSu = true;
         Log.d("ROOT", "Root access granted");
         
         os.writeBytes("iwconfig eth0 txpower 1mW\n");
         os.flush();
         
       }
       else
       {
         retval = false;
         exitSu = true;
         Log.d("ROOT", "Root access rejected: " + currUid);
       }

       if (exitSu)
       {
         os.writeBytes("exit\n");
         os.flush();
       }
     }
   }
   catch (Exception e)
   {
     // Can't get root !
     // Probably broken pipe exception on trying to write to output stream (os) after su failed, meaning that the device is not rooted
     
     retval = false;
     Log.d("ROOT", "Root access rejected [" + e.getClass().getName() + "] : " + e.getMessage());
   }

   return retval;
 }
 
 public boolean runStartCommands1(String ip)
 {
   boolean retval = false;
   Process suProcess;
   
   try
   {
     suProcess = Runtime.getRuntime().exec("su");
     
     DataOutputStream os = new DataOutputStream(suProcess.getOutputStream());
     DataInputStream osRes = new DataInputStream(suProcess.getInputStream());
     
     if (null != os && null != osRes)
     {
       // Getting the id of the current user to check if this is root
       os.writeBytes("id\n");
       os.flush();

       String currUid = osRes.readLine();
       boolean exitSu = false;
       if (null == currUid)
       {
         retval = false;
         exitSu = false;
         Log.d("ROOT", "Can't get root access or denied by user");
       }
       else if (true == currUid.contains("uid=0"))
       {
         retval = true;
         exitSu = true;
         Log.d("ROOT", "Root access granted");
         
         os.writeBytes("startstopadhoc start 0 " + ip + "\n");
         os.flush();
         
       }
       else
       {
         retval = false;
         exitSu = true;
         Log.d("ROOT", "Root access rejected: " + currUid);
       }

       if (exitSu)
       {
         os.writeBytes("exit\n");
         os.flush();
       }
     }
   }
   catch (Exception e)
   {
     // Can't get root !
     // Probably broken pipe exception on trying to write to output stream (os) after su failed, meaning that the device is not rooted
     
     retval = false;
     Log.d("ROOT", "Root access rejected [" + e.getClass().getName() + "] : " + e.getMessage());
   }

   return retval;
 }

 
 public boolean runStartCommands2()
 {
   boolean retval = false;
   Process suProcess;
   
   try
   {
     suProcess = Runtime.getRuntime().exec("su");
     
     DataOutputStream os = new DataOutputStream(suProcess.getOutputStream());
     DataInputStream osRes = new DataInputStream(suProcess.getInputStream());
     
     if (null != os && null != osRes)
     {
       // Getting the id of the current user to check if this is root
       os.writeBytes("id\n");
       os.flush();

       String currUid = osRes.readLine();
       boolean exitSu = false;
       if (null == currUid)
       {
         retval = false;
         exitSu = false;
         Log.d("ROOT", "Can't get root access or denied by user");
       }
       else if (true == currUid.contains("uid=0"))
       {
         retval = true;
         exitSu = true;
         Log.d("ROOT", "Root access granted");
         
         os.writeBytes("startstopadhoc start 0 192.168.2.2\n");
         os.flush();
         
       }
       else
       {
         retval = false;
         exitSu = true;
         Log.d("ROOT", "Root access rejected: " + currUid);
       }

       if (exitSu)
       {
         os.writeBytes("exit\n");
         os.flush();
       }
     }
   }
   catch (Exception e)
   {
     // Can't get root !
     // Probably broken pipe exception on trying to write to output stream (os) after su failed, meaning that the device is not rooted
     
     retval = false;
     Log.d("ROOT", "Root access rejected [" + e.getClass().getName() + "] : " + e.getMessage());
   }

   return retval;
 }
 
 
 public boolean runStartCommands3()
 {
   boolean retval = false;
   Process suProcess;
   
   try
   {
     suProcess = Runtime.getRuntime().exec("su");
     
     DataOutputStream os = new DataOutputStream(suProcess.getOutputStream());
     DataInputStream osRes = new DataInputStream(suProcess.getInputStream());
     
     if (null != os && null != osRes)
     {
       // Getting the id of the current user to check if this is root
       os.writeBytes("id\n");
       os.flush();

       String currUid = osRes.readLine();
       boolean exitSu = false;
       if (null == currUid)
       {
         retval = false;
         exitSu = false;
         Log.d("ROOT", "Can't get root access or denied by user");
       }
       else if (true == currUid.contains("uid=0"))
       {
         retval = true;
         exitSu = true;
         Log.d("ROOT", "Root access granted");
         
         os.writeBytes("startstopadhoc start 0 192.168.2.3\n");
         os.flush();
         
       }
       else
       {
         retval = false;
         exitSu = true;
         Log.d("ROOT", "Root access rejected: " + currUid);
       }

       if (exitSu)
       {
         os.writeBytes("exit\n");
         os.flush();
       }
     }
   }
   catch (Exception e)
   {
     // Can't get root !
     // Probably broken pipe exception on trying to write to output stream (os) after su failed, meaning that the device is not rooted
     
     retval = false;
     Log.d("ROOT", "Root access rejected [" + e.getClass().getName() + "] : " + e.getMessage());
   }

   return retval;
 }
 
 
 public boolean runStopCommands()
 {
   boolean retval = false;
   Process suProcess;
   
   try
   {
     suProcess = Runtime.getRuntime().exec("su");
     
     DataOutputStream os = new DataOutputStream(suProcess.getOutputStream());
     DataInputStream osRes = new DataInputStream(suProcess.getInputStream());
     
     if (null != os && null != osRes)
     {
       // Getting the id of the current user to check if this is root
       os.writeBytes("id\n");
       os.flush();

       String currUid = osRes.readLine();
       boolean exitSu = false;
       if (null == currUid)
       {
         retval = false;
         exitSu = false;
         Log.d("ROOT", "Can't get root access or denied by user");
       }
       else if (true == currUid.contains("uid=0"))
       {
         retval = true;
         exitSu = true;
         Log.d("ROOT", "Root access granted");
         
         os.writeBytes("startstopadhoc stop 0\n");
         os.flush();
         
       }
       else
       {
         retval = false;
         exitSu = true;
         Log.d("ROOT", "Root access rejected: " + currUid);
       }

       if (exitSu)
       {
         os.writeBytes("exit\n");
         os.flush();
       }
     }
   }
   catch (Exception e)
   {
     // Can't get root !
     // Probably broken pipe exception on trying to write to output stream (os) after su failed, meaning that the device is not rooted
     
     retval = false;
     Log.d("ROOT", "Root access rejected [" + e.getClass().getName() + "] : " + e.getMessage());
   }

   return retval;
 }
 
}