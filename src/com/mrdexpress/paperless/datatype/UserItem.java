package com.mrdexpress.paperless.datatype;

public class UserItem
{
	// TODO: Add id, name, pin (maybe), type (driver or manager)
	private String person_id;
	private String person_name;
	// private String person_pin;
	private UserType person_type;

	public enum UserType
	{
		DRIVER, MANAGER
	}

	public UserItem(String person_id, String person_name, UserType person_type)
	{
		this.person_id = person_id;
		this.person_name = person_name;
		this.person_type = person_type;
	}

	public String getUserID()
	{
		return person_id;
	}

	public void setUserID(String person_id)
	{
		this.person_id = person_id;
	}

	public String getUserName()
	{
		return person_name;
	}

	public void setUserName(String person_name)
	{
		this.person_name = person_name;
	}

	public UserType getUserType()
	{
		return person_type;
	}

	public void setUserType(UserType person_type)
	{
		this.person_type = person_type;
	}
	
	@Override
	public String toString()
	{
		return person_name;
	}
}
