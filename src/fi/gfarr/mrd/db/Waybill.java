package fi.gfarr.mrd.db;

import java.util.ArrayList;

public class Waybill {

	// Serial number of item
	private String waybill_no;

	// Bag number to which this item belongs
	private String bag_number;

	// Nation of Desti
	private String destination;

	private String dimensions;// -----Volumetrics

	private String weight; // Weight

	// Name of customer/recipient
	private String customer_name;

	// Phone number of customer
	private String telephone;

	// Email of customer
	private String email;

	// Paragraph log
	private String comlog;

	// Parcel count
	private int parcel_count;

	// Parcel sequence number
	private int parcel_seq;

	public Waybill(String bag, String wayb) {
		setWaybillNumber(wayb);
		setBagNumber(bag);
		setDimensions("0x0x0");
		setWeight("0KG");
		setEmail("No e-mail address set");
		setTelephone("000 000 0000");
		setComLog("Empty");
		setParcelCount(0);
		setParcelSeq(0);
		setCustomerName("Unknown Customer");
		setDestination("Unknown destination");
	}

	// Mutator methods

	/**
	 * Set the serial number of this item.
	 * 
	 * @param way
	 */
	public void setWaybillNumber(String way) {
		waybill_no = way;
	}

	/**
	 * Set the parcel count of this waybill.
	 * 
	 * @param count
	 */
	public void setParcelCount(int count) {
		parcel_count = count;
	}

	/**
	 * Set the sequence number of the package. E.g. Package number 1 of 2.
	 * 
	 * @param number
	 *            Order in sequence of packages.
	 */
	public void setParcelSeq(int number) {
		parcel_seq = number;
	}

	/**
	 * Set destination of item
	 * 
	 * @param dest
	 *            Recipient's address
	 */
	public void setDestination(String dest) {
		destination = dest;
	}

	/**
	 * Set volumetric of item
	 * 
	 * @param width
	 */
	public void setDimensions(String vol) {
		dimensions = vol;
	}

	/**
	 * Set weight of item.
	 * 
	 * @param w
	 */
	public void setWeight(String w) {
		weight = w;
	}

	/**
	 * Set customer's email address
	 * 
	 * @param eml
	 */
	public void setEmail(String eml) {
		email = eml;
	}

	/**
	 * Set customer's name
	 * 
	 * @param eml
	 */
	public void setCustomerName(String name) {
		customer_name = name;
	}

	/**
	 * Set customer's telephone number
	 * 
	 * @param eml
	 */
	public void setTelephone(String tel) {
		telephone = tel;
	}

	/**
	 * Set waybill's comlog
	 * 
	 * @param eml
	 */
	public void setComLog(String log) {
		comlog = log;
	}

	/**
	 * Set consignment number of item.
	 * 
	 * @param w
	 */
	public void setBagNumber(String c) {
		bag_number = c;
	}

	// Accessor methods

	/**
	 * Return waybill of item.
	 * 
	 * @return
	 */
	public String getWaybill() {
		return waybill_no;
	}

	/**
	 * Return parcel count of waybill
	 * 
	 * @return Parcel count
	 */
	public int getParcelCount() {
		return parcel_count;
	}

	/**
	 * Return the sequence number of the package. E.g. Package number 1 of 2.
	 */
	public int getParcelSeq() {
		return parcel_seq;
	}

	/**
	 * Return volume of item.
	 * 
	 * @return
	 */
	public String getDimensions() {
		return dimensions;
	}

	/**
	 * Return destination of item.
	 * 
	 * @return destination Recipient's address.
	 */
	public String getDestination() {
		return destination;
	}

	/**
	 * Return customer's name.
	 * 
	 * @return Customer's name.
	 */
	public String getCustomerName() {
		return customer_name;
	}

	/**
	 * Returns customer's email address
	 * 
	 * @return Customer's email address
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Returns customer's telephone number
	 * 
	 * @return Customer's telephone number
	 */
	public String getTelephone() {
		return telephone;
	}

	/**
	 * Returns waybill's comlog
	 * 
	 * @return Waybill's comlog
	 */
	public String getComLog() {
		return comlog;
	}

	/**
	 * Returns weight of item.
	 * 
	 * @return
	 */
	public String getWeight() {
		return weight;
	}

	/**
	 * Returns bag number of item.
	 * 
	 * @return
	 */
	public String getBagNumber() {
		return bag_number;
	}

}
