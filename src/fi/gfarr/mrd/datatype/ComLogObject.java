/**
 * 
 */
package fi.gfarr.mrd.datatype;

/**
 * @author greg
 * 
 */
public class ComLogObject
{
	private String timestamp;
	private String user;
	private String note;

	public ComLogObject(String timestamp, String user, String note)
	{
		setTimestamp(timestamp);
		setUser(user);
		setNote(note);
	}

	/**
	 * @return the timestamp
	 */
	public String getTimestamp()
	{
		return timestamp;
	}

	/**
	 * @param timestamp
	 *            the timestamp to set
	 */
	public void setTimestamp(String timestamp)
	{
		this.timestamp = timestamp;
	}

	/**
	 * @return the user
	 */
	public String getUser()
	{
		return user;
	}

	/**
	 * @param user
	 *            the user to set
	 */
	public void setUser(String user)
	{
		this.user = user;
	}

	/**
	 * @return the note
	 */
	public String getNote()
	{
		return note;
	}

	/**
	 * @param note
	 *            the note to set
	 */
	public void setNote(String note)
	{
		this.note = note;
	}

}
