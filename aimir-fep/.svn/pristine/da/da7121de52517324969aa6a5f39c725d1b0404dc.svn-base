package com.aimir.fep.command.ws.datatype;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * A code describing how the power operation was done
 * 
 */
@XmlEnum(Byte.class)
@XmlType(name = "PowerOperation", namespace = "http://ws.command.fep.aimir.com/xsd/CommonTypes")
public enum PowerOperation {
	@XmlEnumValue("1")
	POWER_ON_DIRECTLY((byte) 0x01, "Power on directly"), 
	@XmlEnumValue("2")
	POWER_ON_DEBLOCKING((byte) 0x02, "Power on deblocking"),
	@XmlEnumValue("3")
	POWER_OFF((byte) 0x03, "Power off");

	byte value;
	String documentation;

	PowerOperation(byte value, String documentation) {
		this.value = value;
		this.documentation = documentation;
	}

	public byte getValue() {
		return value;
	}

	public String getDocumentation() {
		return documentation;
	}

	@Override
	public String toString() {
		return PowerOperation.class.getSimpleName() + " [ value:" + this.value
				+ "  documentation:" + this.getDocumentation() + "]";
	}

	public static PowerOperation getPowerOperation(int value) {
		for (PowerOperation os : PowerOperation.values()) {
			if (os.getValue() == value) {
				return os;
			}
		}
		return null;
	}
}
