package fi.gfarr.mrd.db;

import fi.gfarr.mrd.datatype.DialogDataObject;

/**
 * Stores contact details for individual bags.
 * 
 * @author greg
 * 
 */
public class Contact extends DialogDataObject
{
	private String name;
	private String number;

	public Contact()
	{
		this.setName("");
		this.setNumber("");
	}

	public Contact(String name, String phone_number)
	{
		this.setName(name);
		this.setNumber(phone_number);
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @return the number
	 */
	public String getNumber()
	{
		return number;
	}

	/**
	 * @param number
	 *            the number to set
	 */
	public void setNumber(String number)
	{
		this.number = number;
	}

}
