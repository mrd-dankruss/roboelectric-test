package fi.gfarr.mrd.datatype;

public class UserItem
{
	// TODO: Add id, name, pin (maybe), type (driver or manager)
	private String person_id;
	private String person_name;
	// private String person_pin;
	private PersonType person_type;

	public enum PersonType
	{
		DRIVER, MANAGER
	}

	public UserItem(String person_id, String person_name, PersonType person_type)
	{
		this.person_id = person_id;
		this.person_name = person_name;
		this.person_type = person_type;
	}

	public String getPersonID()
	{
		return person_id;
	}

	public void setPersonID(String person_id)
	{
		this.person_id = person_id;
	}

	public String getPersonName()
	{
		return person_name;
	}

	public void setPersonName(String person_name)
	{
		this.person_name = person_name;
	}

	public PersonType getPersonType()
	{
		return person_type;
	}

	public void setPersonType(PersonType person_type)
	{
		this.person_type = person_type;
	}
	
	
}
