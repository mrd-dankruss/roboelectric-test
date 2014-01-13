package fi.gfarr.mrd.db;

import java.util.ArrayList;

public class Bag {

	// Serial number of consignment bag
	private String bag_number;

	// Destination of consignment
	private String destination;

	/*
	 * Has bag been scanned? used to move consignments to bottom of list as they
	 * are scanned.
	 */
	private boolean scanned;

	// Has bag been assigned?
	private boolean assigned;

	// Creation date/time
	private String creation_time;

	// number of items in consignment manifest
	private int number_items;

	/**
	 * A bag, containing several waybills, belonging to a milkrun enroute to a branch. Also referred to as a 'consignment'.
	 *  
	 * @param num Bag number (Primary key)
	 * @param dest Destination branch/hub.
	 */
	public Bag(String num, String dest) {
		setBagNumber(num);
		setDestination(dest);
		setScanned(false);
		setAssigned(false);
		setCreationTime("");
		number_items = 0;
	}

	// Mutator methods

	/**
	 * Set the serial number of this bag.
	 * 
	 * @param number
	 */
	public void setBagNumber(String number) {
		bag_number = number;
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
	public void setScanned(boolean scan) {
		scanned = scan;
	}

	/**
	 * Sets whether the consignment has been assigned or not.
	 * 
	 * @param scan
	 */
	public void setAssigned(boolean ass) {
		assigned = ass; // Haha
	}

	/**
	 * Sets creation date/time of consignment/bag in long date format. E.g.
	 * 2014-04-14T02:15:15Z
	 * 
	 * @param time
	 */
	public void setCreationTime(String time) {
		creation_time = time;
	}

	/**
	 * Set number of items in this consignment's manifest.
	 * 
	 * @param i
	 */
	public void setNumberItems(int i) {
		number_items = i;
	}

	// Accessor methods
	/**
	 * Returns consignment number.
	 * 
	 * @return
	 */
	public String getBagNumber() {
		return bag_number;
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
	 * 
	 * @return
	 */
	public boolean getScanned() {
		return scanned;
	}

	/**
	 * Returns whether the bag has been assigned or not.
	 * 
	 * @return
	 */
	public boolean getAssigned() {
		return assigned;
	}

	/**
	 * Returns creation date/time of consignment/bag in long date format. E.g.
	 * 2014-04-14T02:15:15Z
	 */
	public String getCreationTime() {
		return creation_time;
	}

}
