package com.mrdexpress.paperless.db;

public class Waybill
{

	private final String TAG = "Waybill";

	// Serial number of item
	private String waybill_no;

	// Bag number to which this item belongs
	private String bag_number;

	// Barcode
	private String barcode;

	// Nation of Desti
	private String delivery_address;
	private String delivery_suburb;
	// private String delivery_town;
	private String delivery_coords_lat;
	private String delivery_coords_long;

	// -----Volumetrics
	private String dimensions;

	// Weight
	private String weight;

	private String status;

	// Name of customer/recipient
	private String customer_name;
	private String customer_id;
	private String customer_contact1;
	private String customer_contact2;
	private String customer_email;

	// Paragraph log
	private String comlog;

	// Parcel count
	private String parcel_count;

	// Parcel sequence number
	private int parcel_seq;

	/**
	 * New Waybill object
	 * 
	 * @param waybill_number
	 * @param bag_id
	 */
	public Waybill(String waybill_number, String bag_id)
	{
		setWaybillNumber(waybill_number);
		setBagNumber(bag_id);
		setBarcode("no barcode");
		setDimensions("0");
		setDeliveryLat("0");
		setDeliveryLong("0");
		setWeight("0KG");
		setEmail("No e-mail address set");
		setCustomerContact1("000 000 0000");
		setCustomerContact2("000 000 0000");
		setComLog("Empty");
		setParcelCount("0");
		setParcelSeq(0);
		setCustomerName("Unknown Customer");
		setDeliveryAddress("Unknown destination");
	}

	// Mutator methods

	/**
	 * Set the serial number of this item.
	 * 
	 * @param way
	 */
	public void setWaybillNumber(String way)
	{
		waybill_no = way;
	}

	/**
	 * Set the parcel count of this waybill.
	 * 
	 * @param count
	 */
	public void setParcelCount(String count)
	{
		parcel_count = count;
	}

	/**
	 * Set the sequence number of the package. E.g. Package number 1 of 2.
	 * 
	 * @param number
	 *            Order in sequence of packages.
	 */
	public void setParcelSeq(int number)
	{
		parcel_seq = number;
	}

	/**
	 * Set destination of item
	 * 
	 * @param dest
	 *            Recipient's address
	 */
	public void setDeliveryAddress(String dest)
	{
		delivery_address = dest;
	}

	/**
	 * Set weight of item.
	 * 
	 * @param w
	 */
	public void setWeight(String w)
	{
		weight = w;
	}

	/**
	 * Set customer's email address
	 * 
	 * @param eml
	 */
	public void setEmail(String eml)
	{
		customer_email = eml;
	}

	/**
	 * Set customer's name
	 * 
	 * @param eml
	 */
	public void setCustomerName(String name)
	{
		customer_name = name;
	}

	/**
	 * Set waybill's comlog
	 * 
	 * @param eml
	 */
	public void setComLog(String log)
	{
		comlog = log;
	}

	/**
	 * Set consignment number of item.
	 * 
	 * @param w
	 */
	public void setBagNumber(String c)
	{
		bag_number = c;
	}

	// Accessor methods

	/**
	 * Return waybill of item.
	 * 
	 * @return
	 */
	public String getWaybill()
	{
		return waybill_no;
	}

	/**
	 * Return parcel count of waybill
	 * 
	 * @return Parcel count
	 */
	public String getParcelCount()
	{
		return parcel_count;
	}

	/**
	 * Return the sequence number of the package. E.g. Package number 1 of 2.
	 */
	public int getParcelSeq()
	{
		return parcel_seq;
	}

	/**
	 * Return destination of item.
	 * 
	 * @return destination Recipient's address.
	 */
	public String getDeliveryAddress()
	{
		return delivery_address;
	}

	/**
	 * Return customer's name.
	 * 
	 * @return Customer's name.
	 */
	public String getCustomerName()
	{
		return customer_name;
	}

	/**
	 * Returns customer's email address
	 * 
	 * @return Customer's email address
	 */
	public String getEmail()
	{
		return customer_email;
	}

	/**
	 * Returns waybill's comlog
	 * 
	 * @return Waybill's comlog
	 */
	public String getComLog()
	{
		return comlog;
	}

	/**
	 * Returns weight of item.
	 * 
	 * @return
	 */
	public String getWeight()
	{
		return weight;
	}

	/**
	 * Returns bag number of item.
	 * 
	 * @return
	 */
	public String getBagNumber()
	{
		return bag_number;
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

	/**
	 * @return the status
	 */
	public String getStatus()
	{
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status)
	{
		this.status = status;
	}

	/**
	 * @return the delivery_suburb
	 */
	public String getDeliverySuburb()
	{
		return delivery_suburb;
	}

	/**
	 * @param delivery_suburb
	 *            the delivery_suburb to set
	 */
	public void setDeliverySuburb(String delivery_suburb)
	{
		this.delivery_suburb = delivery_suburb;
	}

	/*	*//**
	 * @return the delivery_town
	 */
	/*
	public String getDeliveryTown()
	{
	return delivery_town;
	}

	*//**
	 * @param delivery_town
	 *            the delivery_town to set
	 */
	/*
	public void setDeliveryTown(String delivery_town)
	{
	this.delivery_town = delivery_town;
	}*/

	/**
	 * @return the delivery_coords_lat
	 */
	public String getDeliveryLat()
	{
		return delivery_coords_lat;
	}

	/**
	 * @param delivery_coords_lat
	 *            the delivery_coords_lat to set
	 */
	public void setDeliveryLat(String delivery_coords_lat)
	{
		this.delivery_coords_lat = delivery_coords_lat;
	}

	/**
	 * @return the delivery_coords_long
	 */
	public String getDeliveryLong()
	{
		return delivery_coords_long;
	}

	/**
	 * @param delivery_coords_long
	 *            the delivery_coords_long to set
	 */
	public void setDeliveryLong(String delivery_coords_long)
	{
		this.delivery_coords_long = delivery_coords_long;
	}

	/**
	 * @return the customer_id
	 */
	public String getCustomerID()
	{
		return customer_id;
	}

	/**
	 * @param customer_id
	 *            the customer_id to set
	 */
	public void setCustomerID(String customer_id)
	{
		this.customer_id = customer_id;
	}

	/**
	 * @return the customer_contact1
	 */
	public String getCustomerContact1()
	{
		return customer_contact1;
	}

	/**
	 * @param customer_contact1
	 *            the customer_contact1 to set
	 */
	public void setCustomerContact1(String customer_contact1)
	{
		this.customer_contact1 = customer_contact1;
	}

	/**
	 * @return the customer_contact2
	 */
	public String getCustomerContact2()
	{
		return customer_contact2;
	}

	/**
	 * @param customer_contact2
	 *            the customer_contact2 to set
	 */
	public void setCustomerContact2(String customer_contact2)
	{
		this.customer_contact2 = customer_contact2;
	}

	/**
	 * @return the dimensions
	 */
	public String getDimensions()
	{
		return dimensions;
	}

	/**
	 * @param dimensions
	 *            the dimensions to set
	 */
	public void setDimensions(String dimensions)
	{
		this.dimensions = dimensions;
		System.out.println("dimens: " + this.dimensions);
	}

}
