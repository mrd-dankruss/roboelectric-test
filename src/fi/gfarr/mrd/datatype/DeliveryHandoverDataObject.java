package fi.gfarr.mrd.datatype;

public class DeliveryHandoverDataObject
{

	private String parcelID;
	private boolean parcelScanned;
	private String barcode;

	public DeliveryHandoverDataObject(String parcelID, boolean parcelScanned)
	{
		this.parcelID = parcelID;
		this.parcelScanned = parcelScanned;
		setBarcode("No Barcode");
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

	/**
	 * @return the barcode
	 */
	public String getBarcode()
	{
		return barcode;
	}

	/**
	 * @param barcode
	 *            the barcode to set
	 */
	public void setBarcode(String barcode)
	{
		this.barcode = barcode;
	}

}
