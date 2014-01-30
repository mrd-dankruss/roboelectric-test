package fi.gfarr.mrd.datatype;

public class DeliveryHandoverDataObject
{

	private String parcelID;
	private boolean parcelScanned;

	public DeliveryHandoverDataObject(String parcelID, boolean parcelScanned)
	{
		this.parcelID = parcelID;
		this.parcelScanned = parcelScanned;
	}

	public String getParcelID()
	{
		return parcelID;
	}

	public boolean isParcelScanned()
	{
		return parcelScanned;
	}

	public void setParcelScanned(boolean newScannedStatus)
	{
		parcelScanned = newScannedStatus;
	}

}
