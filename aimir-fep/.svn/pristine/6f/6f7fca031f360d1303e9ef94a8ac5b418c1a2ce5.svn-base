package com.aimir.fep.command.ws.datatype;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * A code describing how the reading was done
 *
 */
@XmlEnum(Byte.class)
@XmlType(name="ReadingStatus", namespace = "http://ws.command.fep.aimir.com/xsd/CommonTypes")
public enum ReadingStatus {
	@XmlEnumValue("0") NORMAL_READING((byte)0x00, "Normal reading"),
	@XmlEnumValue("1") ESTIMATED_VALUE((byte)0x01, "Estimated value"),
	@XmlEnumValue("2") MISSING_VALUE((byte)0x02, "Value missing"),
	@XmlEnumValue("6") INTERPOLATED_VALUE((byte)0x06, "Interpolated value"),
	@XmlEnumValue("7") MANUAL_READING((byte)0x07, "Manual reading"),
	@XmlEnumValue("8") DIRECTLY_READING((byte)0x08, "On-demand reading");

	byte value;
	String documentation;

	ReadingStatus(byte value, String documentation) {
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
		return ReadingStatus.class.getSimpleName() + " [ value:" + this.value + "  documentation:" + this.getDocumentation() + "]";
	}

	public static ReadingStatus getReadingStatus(int value) {
		for(ReadingStatus os : ReadingStatus.values()) {
			if(os.getValue() == value) {
				return os;
			}
		}
		return null;
	}
}