package com.aimir.fep.command.ws.datatype;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * Code for MeasurementUnit
 *
 */
@XmlType(name = "MeasurementUnit", namespace = "http://ws.command.fep.aimir.com/xsd/CommonTypes")
@XmlEnum(Byte.class)
public enum MeasurementUnit {
	@XmlEnumValue("1") KWH((byte) 0x2, "KWH"),
	@XmlEnumValue("2") MWH((byte) 0x1, "MWH"),
	@XmlEnumValue("3") M3((byte) 0x3, "m3"),
	@XmlEnumValue("4") KW((byte) 0x4, "KW"),
	@XmlEnumValue("5") NM3((byte) 0x05, "Nm3"),
	@XmlEnumValue("6") CELCIUS((byte) 0x06, "Celcius"),
	@XmlEnumValue("7") KVARH((byte) 0x07, "KVARH");

	byte value;
	String documentation;

	MeasurementUnit(byte value, String documentation) {
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
		return MeasurementUnit.class.getSimpleName() + " [ value:" + this.value
				+ "  documentation:" + this.getDocumentation() + "]";
	}

	public static MeasurementUnit getMeasurementUnit(int value) {
		for (MeasurementUnit os : MeasurementUnit.values()) {
			if (os.getValue() == value) {
				return os;
			}
		}
		return null;
	}
}