package fi.gfarr.mrd.objects;

import java.util.ArrayList;

public class Consignment {

	// Serial number of consignment bag
	private String consingment_number;

	// Destination of consignment
	private String destination;

	/*
	 * Has bag been scanned? used to move consignments to bottom of list as they
	 * are scanned.
	 */
	private boolean scanned;
	
	// number of items in consignment manifest
	private int number_items;

	public Consignment(String num, String dest) {
		setConsignmentNumber(num);
		setDestination(dest);
		setScanned(false);
		number_items=0;
	}

	// Mutator methods

	/**
	 * Set the serial number of this consignment.
	 * 
	 * @param number
	 */
	public void setConsignmentNumber(String number) {
		consingment_number = number;
	}

	/**
	 * Set the destination of this consignment.
	 * 
	 * @param dest
	 */
	public void setDestination(String dest) {
		destination = dest;
	}
	
	/**
	 * Sets whether the consignment has been scanned or not.
	 * 
	 * @param scan
	 */
	public void setScanned(boolean scan)
	{
		scanned = scan;
	}

	/**
	 * Set number of items in this consignment's manifest.
	 * 
	 * @param i
	 */
	public void setNumberItems(int i) {
		number_items=i;
	}


	// Accessor methods
	/**
	 * Returns consignment number.
	 * 
	 * @return
	 */
	public String getConsignmentNumber() {
		return consingment_number;
	}

	/**
	 * Returns destination.
	 * 
	 * @return
	 */
	public String getDestination() {
		return destination;
	}

	/**
	 * Returns list of items
	 * 
	 * @return
	 */
	public int getNumberItems() {
		return number_items;
	}
	
	/**
	 * Returns whether the consignment has been scanned or not.
	 * @return
	 */
	public boolean getScanned()
	{
		return scanned;
	}

}
