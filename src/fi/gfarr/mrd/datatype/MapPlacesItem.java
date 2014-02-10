package fi.gfarr.mrd.datatype;

public class MapPlacesItem
{
	private String location_name;
	private String location_reference;

	public MapPlacesItem(String location_name, String location_reference)
	{
		this.location_name = location_name;
		this.location_reference = location_reference;
	}

	public String getLocationName()
	{
		return location_name;
	}

	public void setLocationName(String location_name)
	{
		this.location_name = location_name;
	}

	public String getLocationReference()
	{
		return location_reference;
	}

	public void setLocationReference(String location_reference)
	{
		this.location_reference = location_reference;
	}

	@Override
	public String toString()
	{
		return location_name;

	}

}
