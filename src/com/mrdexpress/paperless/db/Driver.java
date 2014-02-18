package com.mrdexpress.paperless.db;

public class Driver
{

	private int id; // Database row id
	private String name; // Name of driver
	private String pin; // Hash of driver pin

	public Driver()
	{
		setId(-1);
		setName("");
		setPIN("");
	}

	public Driver(String n)
	{
		setId(-1);
		setName(n);
		setPIN("");
	}

	public Driver(int i, String n, String pin)
	{
		setId(i);
		setName(n);
		setPIN(pin);
	}

	// Mutator methods
	/**
	 * Returns database field's primary key of Driver
	 * 
	 * @return
	 */
	public int getId()
	{
		return id;
	}

	/**
	 * Returns name of driver
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Returns the PIN.
	 * 
	 * @return
	 */
	public String getPin()
	{
		return pin;
	}

	// Accessor methods
	/**
	 * Sets primary key of driver
	 */
	public void setId(int i)
	{
		id = i;
	}

	/**
	 * Sets name of driver
	 * 
	 * @param n
	 *            Driver's name
	 */
	public void setName(String n)
	{
		name = n;
	}

	/**
	 * Set PIN
	 * 
	 * @param p
	 *            PIN
	 */
	public void setPIN(String p)
	{
		pin = p;
	}

}
