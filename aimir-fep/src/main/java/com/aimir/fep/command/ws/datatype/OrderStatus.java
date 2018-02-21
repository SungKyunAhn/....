package com.aimir.fep.command.ws.datatype;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * Code for OrderStatus
 */
@XmlType(name="OrderStatus", namespace = "http://ws.command.fep.aimir.com/xsd/CommonTypes")
@XmlEnum(Short.class)
public enum OrderStatus {
	@XmlEnumValue("101") NEW((short) 101, "New"),
	@XmlEnumValue("102") UPDATED((short) 102, "Updated"),
	@XmlEnumValue("103") CANCELED((short) 103, "Cancelled"),
	@XmlEnumValue("201") ORDERED((short) 201, "Ordered"),
	@XmlEnumValue("202") INPROGRESS((short) 202, "In progress"),
	@XmlEnumValue("203") PERFORMED((short) 203, "Performed"),
	@XmlEnumValue("298") REJECTED((short) 298, "Rejected"),
	@XmlEnumValue("299") FAILED((short) 299, "Failed");

	short value;
	String documentation;

	OrderStatus(short value, String documentation) {
		this.value = value;
		this.documentation = documentation;
	}

	public short getValue() {
		return value;
	}

	public String getDocumentation() {
		return documentation;
	}

	@Override
	public String toString() {
		return OrderStatus.class.getSimpleName() + " [ value:" + this.value + "  documentation:" + this.getDocumentation() + "]";
	}

	public static OrderStatus getOrderStatus(int value) {
		for(OrderStatus os : OrderStatus.values()) {
			if(os.getValue() == value) {
				return os;
			}
		}
		return null;
	}
}