package com.mrdexpress.paperless.db;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import com.commonsware.cwac.loaderex.SQLiteCursorLoader;
import com.mrdexpress.paperless.helper.VariableManager;

import java.util.ArrayList;
import java.util.Date;

public class Bag
{

	private final String TAG = "Bag";

	// Serial number of consignment bag
	private String bag_number;

	// Part of the workflow
	private String bag_stopid;

	// Destination of consignment
	private String dest_hubname;
	private String dest_hubcode;
	private String dest_address;
	private String dest_suburb;
//	private String dest_town;
	private String dest_contact;
	private String dest_coords_lat;
	private String dest_coords_long;

	// Driver ID assigned to bag
	private String driver_id;

	// Barcode
	private String barcode;

	/*
	 * Has bag been scanned? used to move consignments to bottom of list as they
	 * are scanned.
	 */
	private boolean scanned;

	/*
	 * Completed, TO DO, or unsuccessful
	 */
	private String status;
	public static final String STATUS_COMPLETED = "completed";
	public static final String STATUS_PARTIAL = "partial";
	public static final String STATUS_TODO = "incomplete";
	public static final String STATUS_UNSUCCESSFUL = "unsuccessful";

	private Date submission_date; // Date that delivery status was updated (if any)
	private String status_reason;

	// Has bag been assigned?
	private boolean assigned;

	// Creation date/time
	private String creation_time;

	// number of items in consignment manifest (waybill count)
	private int number_items;

	// Contact numbers
	private ArrayList<Contact> contacts = new ArrayList<Contact>();

	/**
	 * A bag, containing several waybills, belonging to a milkrun enroute to a branch. Also referred
	 * to as a 'consignment'.
	 * 
	 * @param num
	 *            Bag number (Primary key)
	 * @param dest
	 *            Destination branch/hub.
	 */
	public Bag(String num)
	{
		setBagNumber(num);
		setDestinationAddress("");
		setDestinationContact("");
		setDestinationHubCode("");
		setDestinationHubName("");
		setDestinationLat("");
		setDestinationLong("");
		setDestinationSuburb("");
//		setDestinationTown("");
		setScanned(false);
		setAssigned(false);
		setCreationTime("");
		setStatus(STATUS_TODO);
		setSubmissionDate(null);
		setStatusReason("");
		setDriverId("");
		setStopId("");
		number_items = 0;
	}

	// Mutator methods

	/**
	 * Set the serial number of this bag.
	 * 
	 * @param number
	 */
	public void setBagNumber(String number)
	{
		bag_number = number;
	}

	/**
	 * Set the Driver ID assigned to this bag.
	 * 
	 * @param id
	 *            Driver ID
	 */
	public void setDriverId(String id)
	{
		driver_id = id;
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
	 * Sets whether the consignment has been assigned or not.
	 * 
	 * @param scan
	 */
	public void setAssigned(boolean ass)
	{
		assigned = ass; // Haha
	}

	/**
	 * Sets creation date/time of consignment/bag in long date format. E.g.
	 * 2014-04-14T02:15:15Z
	 * 
	 * @param time
	 */
	public void setCreationTime(String time)
	{
		creation_time = time;
	}

	/**
	 * Set number of items in this consignment's manifest.
	 * 
	 * @param i
	 */
	public void setNumberItems(int i)
	{
		number_items = i;
	}

	public String getDestinationHubName()
	{
		return dest_hubname;
	}

	public void setDestinationHubName(String dest_hubname)
	{
		this.dest_hubname = dest_hubname;
	}

	// Accessor methods
	/**
	 * Returns consignment number.
	 * 
	 * @return
	 */
	public String getBagNumber()
	{
		return bag_number;
	}

	/**
	 * Returns destination.
	 * 
	 * @return
	 */
	public String getDestination()
	{
		return dest_hubname;
	}

	/**
	 * Return the ID of the driver assigned to this bag.
	 * 
	 * @return driver_id Driver's ID
	 */
	public String getDriverId()
	{
		return driver_id;
	}

	/**
	 * Returns list of items
	 * 
	 * @return
	 */
	public int getNumberItems()
	{
		return number_items;
	}

	/**
	 * Returns whether the consignment has been scanned or not.
	 * 
	 * @return
	 */
	public boolean getScanned()
	{
		return scanned;
	}

	/**
	 * Returns whether the bag has been assigned or not.
	 * 
	 * @return
	 */
	public boolean getAssigned()
	{
		return assigned;
	}

	/**
	 * Returns creation date/time of consignment/bag in long date format. E.g.
	 * 2014-04-14T02:15:15Z
	 */
	public String getCreationTime()
	{
		return creation_time;
	}

	public String getDestinationHubCode()
	{
		return dest_hubcode;
	}

	public void setDestinationHubCode(String dest_hubcode)
	{
		this.dest_hubcode = dest_hubcode;
	}

	public String getDestinationAddress()
	{
		return dest_address;
	}

	public void setDestinationAddress(String dest_address)
	{
		this.dest_address = dest_address;
	}

	public String getDestinationSuburb()
	{
		return dest_suburb;
	}

	public void setDestinationSuburb(String dest_suburb)
	{
		this.dest_suburb = dest_suburb;
	}

/*	public String getDestinationTown()
	{
		return dest_town;
	}

	public void setDestinationTown(String dest_town)
	{
		this.dest_town = dest_town;
	}*/

	public String getDestinationContact()
	{
		return dest_contact;
	}

	public void setDestinationContact(String dest_contact)
	{
		this.dest_contact = dest_contact;
	}

	public String getDestinationLat()
	{
		return dest_coords_lat;
	}

	public void setDestinationLat(String dest_coords_lat)
	{
		this.dest_coords_lat = dest_coords_lat;
	}

	/**
	 * @return the dest_coords_long
	 */
	public String getDestinationLong()
	{
		return dest_coords_long;
	}

	/**
	 * @param dest_coords_long
	 *            the dest_coords_long to set
	 */
	public void setDestinationLong(String dest_coords_long)
	{
		this.dest_coords_long = dest_coords_long;
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
	 * @return the contacts
	 */
	public ArrayList<Contact> getContacts()
	{
		return contacts;
	}

	/**
	 * @param contacts
	 *            the contacts to set
	 */
	public void setContacts(ArrayList<Contact> contacts)
	{
		this.contacts = contacts;
	}

	/**
	 * Add a Contact object to the list of contacts.
	 * 
	 * @param name
	 * @param number
	 */
	public void addContact(String name, String number)
	{
		this.contacts.add(new Contact(name, number));
	}

	/**
	 * @return the submission_date
	 */
	public Date getSubmissionDate()
	{
		return submission_date;
	}

	/**
	 * @param submission_date
	 *            the submission_date to set
	 */
	public void setSubmissionDate(Date submission_date)
	{
		this.submission_date = submission_date;
	}

	/**
	 * @return the status_reason
	 */
	public String getStatusReason()
	{
		return status_reason;
	}

	/**
	 * @param status_reason
	 *            the status_reason to set
	 */
	public void setStatusReason(String status_reason)
	{
		this.status_reason = status_reason;
	}

	/**
	 * @return the bag_stopid
	 */
	public String getStopId()
	{
		return bag_stopid;
	}

	/**
	 * @param bag_stopid
	 *            the bag_stopid to set
	 */
	public void setStopId(String bag_stopid)
	{
		this.bag_stopid = bag_stopid;
	}

}
