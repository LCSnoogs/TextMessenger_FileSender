package android.TextMessenger.model.pdu;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import adhoc.aodv.exception.BadPduFormatException;
import android.TextMessenger.model.Constants;
import android.util.Base64;
import android.util.Log;

public class FileFragment implements PduInterface {

	private int retries = Constants.MAX_RESENDS;
	private int sequenceNumber = -1, chatID, messageNumber, ownerContactID;
	private byte type = Constants.PDU_FILE;
	private String time;
	private String filename;													// The name of the file.
	private int fileSize;														// The size of the full file in bytes.
	private long aliveTimeLeft = Long.MAX_VALUE;
	private byte[] fragmentBytes;												// The bytes of the fragment. A fraction of bytes for the full file.
	private int fragmentNumber;													// The number of the fragment.
	private int totalFragments;													// The total amount of fragments that make up the full file.
	private final int totalFields = 11;											// The total number of fields that make up the message.
	private int fragmentDataSize;												// The size of the data section of the fragment in bytes.
	
	public FileFragment(int ownerContactID)
	{
		this.ownerContactID = ownerContactID;
	}
	
	public FileFragment(int ownerContactID, int chatID, String filename, int fileSize, byte[] fragmentBytes, int fragmentNumber, int totalFragments)
	{
		this.ownerContactID = ownerContactID;
		this.chatID = chatID;
		this.filename = filename;
		this.fileSize = fileSize;
		this.fragmentBytes = fragmentBytes;
		fragmentDataSize = fragmentBytes.length;
		this.fragmentNumber = fragmentNumber;
		this.totalFragments = totalFragments;
		time = new SimpleDateFormat("HH:mm:ss").format(new Date());
	}
	
	public byte[] toBytes() {
		// TODO Auto-generated method stub
		
		byte[] headerData = (type+";"+sequenceNumber+";"+chatID+";"+messageNumber+";"+time+";"+fileSize+";"+filename+";"+fragmentNumber+";"+totalFragments+";"+fragmentDataSize+";").getBytes();
		byte[] data = new byte[headerData.length + fragmentBytes.length];
		
		// First adds the header data to the byte array and then adds the file data to the byte array
		for (int i = 0; i < data.length; i++)
		{
			if (i < headerData.length)
			{
				data[i] = headerData[i];
			} else
			{
				data[i] = fragmentBytes[i - headerData.length];
			}
		}
		Log.d("PictureFragment", "" + data.length);
		return data;
	}

	public void parseBytes(byte[] dataToParse) throws BadPduFormatException {
		// TODO Auto-generated method stub
		String[] s;
		try {
			s = new String(dataToParse, "US-ASCII").split(";",totalFields);
			if(s.length != totalFields){
				throw new BadPduFormatException(	"Msg: could not split " +
													"the expected # of arguments from bytes. " +
													"Expecteded 6 args but were given "+s.length	);
			}
			try {
				sequenceNumber = Integer.parseInt(s[1]);
				chatID = Integer.parseInt(s[2]);
				messageNumber =Integer.parseInt(s[3]);
				time = new String(s[4]);
				fileSize = Integer.parseInt(s[5]);
				filename = new String(s[6]);
				fragmentNumber = Integer.parseInt(s[7]);
				totalFragments = Integer.parseInt(s[8]);
				fragmentDataSize = Integer.parseInt(s[9]);
				fragmentBytes = Base64.decode(s[10], 0);		// decoded to get the original bytes
				
			} catch (NumberFormatException e) {
				throw new BadPduFormatException("Msg: falied parsing arguments to the desired types");
			}
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public long getAliveTime() {
		// TODO Auto-generated method stub
		return aliveTimeLeft;
	}

	public void setTimer() {
		// TODO Auto-generated method stub
		aliveTimeLeft = Constants.MESSAGE_ALIVE_TIME + System.currentTimeMillis();
	}

	public byte getPduType() {
		// TODO Auto-generated method stub
		return type;
	}

	public int getSequenceNumber() {
		// TODO Auto-generated method stub
		return sequenceNumber;
	}

	public void setSequenceNumber(int sequenceNumber) {
		// TODO Auto-generated method stub
		this.sequenceNumber = sequenceNumber;
	}

	public boolean resend() {
		// TODO Auto-generated method stub
		retries--;
		if(retries <= 0){
			return false;
		}
		return true;
	}

	public byte[] getFragmentBytes() {
		return fragmentBytes;
	}

	public void setFragmentBytes(byte[] fragmentBytes) {
		this.fragmentBytes = fragmentBytes;
	}

	public int getFragmentNumber() {
		return fragmentNumber;
	}

	public void setFragmentNumber(int fragmentNumber) {
		this.fragmentNumber = fragmentNumber;
	}

	public int getTotalFragments() {
		return totalFragments;
	}

	public void setTotalFragments(int totalFragments) {
		this.totalFragments = totalFragments;
	}

	public int getChatID() {
		return chatID;
	}

	public void setChatID(int chatID) {
		this.chatID = chatID;
	}

	public int getOwnerContactID() {
		return ownerContactID;
	}

	public void setOwnerContactID(int ownerContactID) {
		this.ownerContactID = ownerContactID;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public int getFileSize() {
		return fileSize;
	}

	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

}
