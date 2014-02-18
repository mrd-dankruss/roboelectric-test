package com.mrdexpress.paperless.adapters;

public class EntryItem implements Item {

	// Consignment number
	public final String cons_number;

	// number of items in consignment
	public final String cons_number_items;

	public EntryItem(String num, String num_items) {
		this.cons_number = num;
		this.cons_number_items = num_items;
	}

	@Override
	public boolean isSection() {
		return false;
	}
}