package com.aimir.fep.meter.parser.MX2Table;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

/**
 * @author jiae
 */
@XmlAccessorType(XmlAccessType.FIELD)
 @XmlType(name = "DisplayItemsSelect", propOrder = {
		   "displayItem"
		})
public class DisplayItemsSelect implements java.io.Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7499839843839604455L;

	String displayItem;
	
	public DisplayItemsSelect(){}
	
	public String getDisplayItem() {
		return displayItem;
	}

	public void setDisplayItem(String displayItem) {
		this.displayItem = displayItem;
	}

	public byte[] toByteArray() throws Exception {

		if(this.displayItem == null  )
			throw new Exception("Can not found Display Items Select");
		
		byte[] bcd = DataUtil.getBCD(displayItem);
		
		return bcd;
	}
	
}
