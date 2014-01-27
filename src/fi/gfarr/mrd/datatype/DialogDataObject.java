package fi.gfarr.mrd.datatype;

public class DialogDataObject
{
	
	private String longDisplayTime;
	private String shortDisplayTime;
	private String phoneNumber;
	
	public DialogDataObject(String longDisplayTime, String shortDisplayTime) {
		this(longDisplayTime, shortDisplayTime, null);
	}
	
	public DialogDataObject(String longDisplayTime, String shortDisplayTime, String phoneNumber) {
		this.longDisplayTime = longDisplayTime;
		this.shortDisplayTime = shortDisplayTime;
		this.phoneNumber = phoneNumber;
	}
	
	public String getLongDisplayTime() {
		return longDisplayTime;
	}
	
	public String getShortDisplayTime() {
		return shortDisplayTime;
	}
	
	public String getPhoneNumber() {
		return phoneNumber;
	}
	
	public void setShortDisplayTime(String newShortTime) {
		shortDisplayTime = newShortTime;
	}
}
