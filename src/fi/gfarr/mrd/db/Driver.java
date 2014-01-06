package fi.gfarr.mrd.db;

public class Driver {

	private int id; // Database row id
	private String name; // Name of driver

	public Driver() {
		setId(-1);
		setName("");
	}

	public Driver(String n) {
		setId(-1);
		setName(n);
	}
	
	public Driver(int i, String n) {
		setId(i);
		setName(n);
	}

	// Mutator methods
	/**
	 * Returns database field's primary key of Driver
	 * 
	 * @return
	 */
	public int getId() {
		return id;
	}

	/**
	 * Returns name of driver
	 */
	public String getName() {
		return name;
	}

	// Accessor methods
	/**
	 * Sets primary key of driver
	 */
	public void setId(int i) {
		id = i;
	}

	/**
	 * Sets name of driver
	 */
	public void setName(String n) {
		name = n;
	}

}
