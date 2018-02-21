package com.aimir.fep.command.ws.datatype;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * A code describing how the quality status was done
 * 
 */
@XmlEnum(Byte.class)
@XmlType(name = "QualityStatus", namespace = "http://ws.command.fep.aimir.com/xsd/CommonTypes")
public enum QualityStatus {
	@XmlEnumValue("1")
	ACCEPTED_METERVALUE((byte) 0x01, "Accepted metervalue"), 
	@XmlEnumValue("2")
	INCORRECT_METERVALUE_DUE_TO_FAILED_VALIDATION((byte) 0x02, "Incorrect metervalue due to failed validation"), 
	@XmlEnumValue("3")
	UNCERTAIN_METERVALUE((byte) 0x03, "Uncertain metervalue");

	byte value;
	String documentation;

	QualityStatus(byte value, String documentation) {
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
		return QualityStatus.class.getSimpleName() + " [ value:" + this.value
				+ "  documentation:" + this.getDocumentation() + "]";
	}

	public static QualityStatus getQualityStatus(int value) {
		for (QualityStatus os : QualityStatus.values()) {
			if (os.getValue() == value) {
				return os;
			}
		}
		return null;
	}
}