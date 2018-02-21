package com.aimir.fep.command.ws.datatype;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * A code describing how the meter value status was done
 * 
 */
@XmlEnum(Byte.class)
@XmlType(name = "MeterValueStatus", namespace = "http://ws.command.fep.aimir.com/xsd/CommonTypes")
public enum MeterValueStatus {
	@XmlEnumValue("1")
	METER_VALUE_COLLECTED((byte) 0x01, "Accepted metervalue."), 
	@XmlEnumValue("2")
	METER_VALUE_NOT_BE_COLLECTED_NOT_ENERGIZED_METER((byte) 0x02, "Meter value could not be collected due to not energized meter."), 
	@XmlEnumValue("3")
	METER_VALUE_NOT_BE_COLLECTED_NO_ANSWER_METER((byte) 0x03, "Meter value could not be collected, no answer from meter."),
	@XmlEnumValue("4")
	METER_VALUE_NOT_COLLECTED_ORDER_CANCELD((byte) 0x04, "Meter value not collected, order cancelled.");

	byte value;
	String documentation;

	MeterValueStatus(byte value, String documentation) {
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
		return MeterValueStatus.class.getSimpleName() + " [ value:" + this.value
				+ "  documentation:" + this.getDocumentation() + "]";
	}

	public static MeterValueStatus MeterValueStatus(int value) {
		for (MeterValueStatus os : MeterValueStatus.values()) {
			if (os.getValue() == value) {
				return os;
			}
		}
		return null;
	}
}