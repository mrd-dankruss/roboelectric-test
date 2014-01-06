package fi.gfarr.mrd.objects;

import java.util.ArrayList;

public class Item {

	// Serial number of item
	private String waybill;

	// Consignment number to which this item belongs
	private String consignment_number;

	// Number of item (if more than one)
	private String number;

	private String volume;// -----Volumetrics
	private String weight; // Weight

	public Item(String cons_no, String wayb) {
		setWaybill(wayb);
		setConsignmentNumber(cons_no);
		setVolume("0x0x0");
		setWeight("0KG");
		setNumber("1 of 1");
	}

	// Mutator methods

	/**
	 * Set the serial number of this item.
	 * 
	 * @param way
	 */
	public void setWaybill(String way) {
		waybill = way;
	}

	/**
	 * Set the number of this item.
	 * 
	 * @param dest
	 */
	public void setNumber(String no) {
		number = no;
	}

	/**
	 * Set volumetric of item
	 * 
	 * @param width
	 */
	public void setVolume(String vol) {
		volume = vol;
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
	 * Set consignment number of item.
	 * 
	 * @param w
	 */
	public void setConsignmentNumber(String c) {
		consignment_number = c;
	}

	// Accessor methods

	/**
	 * Return waybill of item.
	 * 
	 * @return
	 */
	public String getWaybill() {
		return waybill;
	}

	/**
	 * Return waybill of item.
	 * 
	 * @return
	 */
	public String getNumber() {
		return number;
	}

	/**
	 * Return volume of item.
	 * 
	 * @return
	 */
	public String getVolume() {
		return volume;
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
	 * Returns consignment number of item.
	 * 
	 * @return
	 */
	public String getConsignmentNumber() {
		return consignment_number;
	}

}
