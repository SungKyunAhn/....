package com.aimir.fep.command.ws.datatype;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * Meter stand (cumulative) or consumption. In the first version of
 * the service the provider should answer with a fault (not
 * implemented) if consumption is requested.
 * @author Administrator
 */
@XmlType(name="MeterValueType", namespace = "http://ws.command.fep.aimir.com/xsd/CommonTypes")
@XmlEnum(Byte.class)
public enum MeterValueType {
	@XmlEnumValue("1") CUMULATIVE((byte)0x01, "Meter Stand, cumulative"),
	@XmlEnumValue("2") CONSUMPTION((byte)0x02, "Consumption");

	byte value;
	String documentation;

	MeterValueType(byte value, String document) {
		this.value = value;
		this.documentation = document;
	}

	public byte getValue() {
		return value;
	}

	public String getDocumentation() {
		return documentation;
	}

	@Override
	public String toString() {
		return MeterValueType.class.getSimpleName() + " [ value:" + this.value + "  documentation:" + this.getDocumentation() + "]";
	}

	public static MeterValueType getMeterValueType(int value) {
		for(MeterValueType os : MeterValueType.values()) {
			if(os.getValue() == value) {
				return os;
			}
		}
		return null;
	}
}