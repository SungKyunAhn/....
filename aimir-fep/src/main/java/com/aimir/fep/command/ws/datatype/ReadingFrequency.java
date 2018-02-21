package com.aimir.fep.command.ws.datatype;


import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * Code for ReadingFrequency
 *
 */
@XmlType(name = "ReadingFrequency", namespace = "http://ws.command.fep.aimir.com/xsd/CommonTypes")
@XmlEnum(Byte.class)
public enum ReadingFrequency {
	@XmlEnumValue("1") HOURLY_VALUE((byte) 0x01, "Hourly value"),
	@XmlEnumValue("2") DAILY_VALUE((byte) 0x02, "Daily value"),
	@XmlEnumValue("3") MONTHLY_VLUE((byte) 0x02, "Monthly value");

	byte value;
	String documentation;

	ReadingFrequency(byte value, String documentation) {
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
		return ReadingFrequency.class.getSimpleName() + " [ value:" + this.value + "  documentation:" + this.getDocumentation() + "]";
	}

	public static ReadingFrequency getReadingFrequency(int value) {
		for(ReadingFrequency os : ReadingFrequency.values()) {
			if(os.getValue() == value) {
				return os;
			}
		}
		return null;
	}
}