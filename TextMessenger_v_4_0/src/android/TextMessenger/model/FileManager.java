package android.TextMessenger.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.TextMessenger.model.pdu.FileFragment;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

public class FileManager {

	private int chatID, ownerContactID;
	private String filename;
	private int fileSize;
	private int totalFragments;
	private boolean isComplete = false;				// When this is true, all fragments have been added to the FileManager.
	private final int maxPacketDataSize = 35000;	// The max size in bytes that a fragment can be. Set this low to account for the header size and Base64 encoding. Could be set higher.
	private int counter = 0;						// Marks the starting point where bytes from fileBytes should be added to a fragment.
	private int fragmentNumber = 0;
	private FileOutputStream fOut = null;			// Used to get bytes from a file
	private File destinationFile;					// Bytes will be saved to this file
	private byte[] fileBytes;						// Holds the bytes of the file that is being sent.
	
	public FileManager(int ownerContactID){
		this.ownerContactID = ownerContactID;
	}
	
	public FileManager(int ownerContactID, int chatID, String filename){

		this.ownerContactID = ownerContactID;
		this.filename = filename.split("/")[filename.split("/").length - 1];
		this.chatID = chatID;
		File file = new File(filename);
		
		FileInputStream fIn = null;
		try {
			fIn = new FileInputStream(file);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		fileBytes = new byte[(int)file.length()];
		try {
			fIn.read(fileBytes);
			fIn.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		fileSize = fileBytes.length;
		totalFragments = fileSize / maxPacketDataSize;
		
		// If fileSize is not evenly divided by maxPacketDataSize, then one extra fragment is needed to hold the full file.
		if (fileSize % maxPacketDataSize > 0)
		{
			totalFragments++;
		}
	}
	
	// Returns one fragment at a time. Done this way to control the flow of fragments being sent. Returns null when there are no more fragments to send.
	public FileFragment getFragment()
	{
		if (fragmentNumber < totalFragments)
		{
			byte[] fragmentBytes;
			int size;
			
			// Makes sure size does not exceed the maxPacketDataSize
			if (fileSize - counter >= maxPacketDataSize)
			{
				size = maxPacketDataSize;
			} else
			{
				size = fileSize - counter;
			}
			
			fragmentBytes = new byte[size];
			for (int k = 0; k < size; k++)
			{
				if (counter < fileSize)
				{
					fragmentBytes[k] = fileBytes[counter];
					counter++;
				} else
				{
					break;
				}
			}
			
			fragmentBytes = Base64.encode(fragmentBytes, 0);	// Base64 encodes the bytes so they won't be lost when they are made into a String object
			Log.d("File", "" + fragmentBytes.length);
			fragmentNumber++;
			return new FileFragment(ownerContactID, chatID, filename, fileSize, fragmentBytes, fragmentNumber, totalFragments);
		}
		return null;
	}
	
	// writes fragment bytes to a file
	private void saveBytes(FileFragment fragment) throws IOException
	{
		if (fOut == null)
		{
			String path = Environment.getExternalStorageDirectory().toString();
			destinationFile = new File(path + "/" + filename);
			destinationFile.createNewFile();
			fOut = new FileOutputStream(destinationFile);
		}
		
		Log.d("Chat", "Compressing File");
		fOut.write(fragment.getFragmentBytes());
		fOut.flush();			
		
		if (isComplete)
		{
			fOut.close();
			fOut = null;
		}
	}

	// Gets information from fragment and then saves the fragment
	public void addFileFragment(FileFragment fragment)
	{
		if (filename == null)
		{
			totalFragments = fragment.getTotalFragments();
			filename = fragment.getFilename();
		}
		
		// Checks if the fragment is the last fragment. Does not take packet loss into account.
		if (fragment.getFragmentNumber() == totalFragments)
		{
			isComplete = true;
		}
		try {
			saveBytes(fragment);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int getChatID(){
		return chatID;
	}
	
	public int getContactID(){
		return ownerContactID;
	}
	
	// @Override
	public String getFilename()
	{
		return filename;
	}

	public void setComplete(boolean isComplete) {
		this.isComplete = isComplete;
	}

	public boolean isComplete() {
		return isComplete;
	}

	public int getTotalFragments() {
		return totalFragments;
	}

	public void setTotalFragments(int totalFragments) {
		this.totalFragments = totalFragments;
	}
}
