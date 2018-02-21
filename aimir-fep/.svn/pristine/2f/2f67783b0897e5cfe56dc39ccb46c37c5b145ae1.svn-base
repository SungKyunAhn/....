package com.aimir.fep.command.ws.datatype;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * A meter may provide multiple measurements of different
 * type, four for electricity and two for district heating.
 */
@XmlType(name="MeterValueMeasurementType", namespace = "http://ws.command.fep.aimir.com/xsd/CommonTypes")
@XmlEnum(Byte.class)
public enum MeterValueMeasurementType {
	@XmlEnumValue("1") ACTIVE_ENERGY_POSITIVE((byte)1, "Active energy positive (consumption) - Electricity consumer consuming electricity (normal case)"),
	@XmlEnumValue("2") ACTIVE_ENERGY_NEGATIVE((byte)2, "Active energy negative (production) - Electricity consumer producing electricity"),
	@XmlEnumValue("3") REACTIVE_ENERGY_POSITIVE((byte)3, "Reactive energy positive (consumption) - Electricity producer consuming electricity"),
	@XmlEnumValue("4") REACTIVE_ENERGY_NEGATIVE((byte)4, "Reactive energy negative (production) - Electricity producer producing electricity"),
	@XmlEnumValue("5") DISTRICT_HEATING_ENERGY((byte)5, "District heating energy"),
	@XmlEnumValue("6") DISTRICT_HEATING_VOLUME((byte)6, "District heating volume");

	byte value;
	String documentation;

	MeterValueMeasurementType(byte value, String documentation) {
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
		return MeterValueMeasurementType.class.getSimpleName() + " [ value:" + this.value + "  documentation:" + this.getDocumentation() + "]";
	}

	public static MeterValueMeasurementType getMeterValueMeasurementType(int value) {
		for(MeterValueMeasurementType os : MeterValueMeasurementType.values()) {
			if(os.getValue() == value) {
				return os;
			}
		}
		return null;
	}
}