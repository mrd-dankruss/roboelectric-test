package com.mrdexpress.paperless.datatype;

public class ReasonPartialDeliveryItem
{

	private String group_name;
	private String reason_ID;
	private String reason_title;
	private boolean is_selected;
    public int parcelid;
    public String waybill_id;

	/**
	 * 
	 * @param group_name
	 *            Waybill barcode
	 * @param reason_ID
	 *            Status code from API
	 * @param reason_title
	 *            Reason name from API
	 * @param is_selected
	 */
	public ReasonPartialDeliveryItem( int parcelid, String group_name, String reason_ID, String reason_title,
			boolean is_selected , String wid)
	{
        this.parcelid = parcelid;
		this.group_name = group_name;
		this.reason_ID = reason_ID;
		this.reason_title = reason_title;
		this.is_selected = is_selected;
        this.waybill_id = wid;
	}

	public String getGroupName()
	{
		return group_name;
	}

	public void setGroupName(String group_name)
	{
		this.group_name = group_name;
	}

	public String getReasonID()
	{
		return reason_ID;
	}

	public void setReasonID(String new_reason_ID)
	{
		reason_ID = new_reason_ID;
	}

	public String getReasonTitle()
	{
		return reason_title;
	}

	public void setReasonTitle(String new_reason_title)
	{
		reason_title = new_reason_title;
	}

	public boolean isSelected()
	{
		return is_selected;
	}

	public void setIsSelected(boolean new_is_selected)
	{
		is_selected = new_is_selected;
	}

    public String getWaybill_id() { return this.waybill_id; }

    public void setWaybill_id(String wid){ this.waybill_id = wid; }

}
