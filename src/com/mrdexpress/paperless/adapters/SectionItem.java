package com.mrdexpress.paperless.adapters;

public class SectionItem implements Item {

	// Consignment number
	private final String cons_number;

	// number of items in consignment
	private final String cons_number_items;
	
	//number of items already scanned
	private String cons_number_items_scanned;

	public SectionItem(String num, String num_items, String scanned) {
		this.cons_number = num;
		this.cons_number_items = num_items;
		cons_number_items_scanned = scanned;
	}

	public String getConsignmentNumber() {
		return cons_number;
	}

	public String getConsignmentNumberItems() {
		return cons_number_items;
	}
	
	public String getConsignmentNumberItemsScanned() {
		return cons_number_items_scanned;
	}

	@Override
	public boolean isSection() {
		return true;
	}

}