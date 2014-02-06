package fi.gfarr.mrd.datatype;

public class DialogDataObject
{

	private String main_text;
	private String sub_text;
	private String third_text;

	public DialogDataObject()
	{
		this(null, null, null);
	}

	public DialogDataObject(String longDisplayTime, String shortDisplayTime)
	{
		this(longDisplayTime, shortDisplayTime, null);
	}

	public DialogDataObject(String longDisplayTime, String shortDisplayTime, String thirdtext)
	{
		this.main_text = longDisplayTime;
		this.sub_text = shortDisplayTime;
		setThirdText("");
	}

	public String getMainText()
	{
		return main_text;
	}

	public String getSubText()
	{
		return sub_text;
	}

	public void setSubText(String newShortTime)
	{
		sub_text = newShortTime;
	}

	public void setMainText(String text)
	{
		main_text = text;
	}

	/**
	 * @return the delay_id
	 */
	public String getThirdText()
	{
		return third_text;
	}

	/**
	 * @param delay_id
	 *            the delay_id to set
	 */
	public void setThirdText(String delay_id)
	{
		this.third_text = delay_id;
	}
}
