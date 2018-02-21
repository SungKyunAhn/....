package com.aimir.fep.command.ws.datatype;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

import com.aimir.constants.CommonConstants;

/**
 * Code for LocationType
 *
 */
@XmlType(name = "LocationType", namespace = "http://ws.command.fep.aimir.com/xsd/CommonTypes")
@XmlEnum(Short.class)
public enum LocationType {
	@XmlEnumValue("101") ELECTRICITY_CONSUMPTION_LOCATION((short) CommonConstants.LOCATIONTYPE.ELECTRICITY_CONSUMPTION_LOCATION.getValue(), CommonConstants.LOCATIONTYPE.ELECTRICITY_CONSUMPTION_LOCATION.getDocumentation()),
	@XmlEnumValue("102") DISTRICT_HEATING_CONSUMPTION_LOCATION((short) CommonConstants.LOCATIONTYPE.DISTRICT_HEATING_CONSUMPTION_LOCATION.getValue(), CommonConstants.LOCATIONTYPE.DISTRICT_HEATING_CONSUMPTION_LOCATION.getDocumentation()),
	@XmlEnumValue("103") DISTRICT_COOLING_CONSUMPTION_LOCATION((short) CommonConstants.LOCATIONTYPE.DISTRICT_COOLING_CONSUMPTION_LOCATION.getValue(), CommonConstants.LOCATIONTYPE.DISTRICT_COOLING_CONSUMPTION_LOCATION.getDocumentation()),
	@XmlEnumValue("104") GAS_CONSUMPTION_LOCATION((short) CommonConstants.LOCATIONTYPE.GAS_CONSUMPTION_LOCATION.getValue(), CommonConstants.LOCATIONTYPE.GAS_CONSUMPTION_LOCATION.getDocumentation()),
	@XmlEnumValue("105") WATER_CONSUMPTION_LOCATION((short) CommonConstants.LOCATIONTYPE.WATER_CONSUMPTION_LOCATION.getValue(), CommonConstants.LOCATIONTYPE.WATER_CONSUMPTION_LOCATION.getDocumentation()),
	@XmlEnumValue("106") ELECTRICITY_CONSUMPTION_LOCATION_INDIVIDUAL_MEASUREMENT((short) CommonConstants.LOCATIONTYPE.ELECTRICITY_CONSUMPTION_LOCATION_INDIVIDUAL_MEASUREMENT.getValue(), CommonConstants.LOCATIONTYPE.ELECTRICITY_CONSUMPTION_LOCATION_INDIVIDUAL_MEASUREMENT.getDocumentation()),
	@XmlEnumValue("107") HOT_WATER_CONSUMPTION_LOCATION_INDIVIDUAL_MEASUREMENT((short) CommonConstants.LOCATIONTYPE.HOT_WATER_CONSUMPTION_LOCATION_INDIVIDUAL_MEASUREMENT.getValue(), CommonConstants.LOCATIONTYPE.HOT_WATER_CONSUMPTION_LOCATION_INDIVIDUAL_MEASUREMENT.getDocumentation()),
	@XmlEnumValue("108") COLD_WATER_CONSUMPTION_LOCATION_INDIVIDUAL_MEASUREMENT((short) CommonConstants.LOCATIONTYPE.COLD_WATER_CONSUMPTION_LOCATION_INDIVIDUAL_MEASUREMENT.getValue(), CommonConstants.LOCATIONTYPE.COLD_WATER_CONSUMPTION_LOCATION_INDIVIDUAL_MEASUREMENT.getDocumentation()),
	@XmlEnumValue("109") TEMPERATURE_LOCATION_INDIVIDUAL_MEASUREMENT((short) CommonConstants.LOCATIONTYPE.TEMPERATURE_LOCATION_INDIVIDUAL_MEASUREMENT.getValue(), CommonConstants.LOCATIONTYPE.TEMPERATURE_LOCATION_INDIVIDUAL_MEASUREMENT.getDocumentation()),
	@XmlEnumValue("121") ELECTRICITY_PRODUCTION_LOCATION((short) CommonConstants.LOCATIONTYPE.ELECTRICITY_PRODUCTION_LOCATION.getValue(), CommonConstants.LOCATIONTYPE.ELECTRICITY_PRODUCTION_LOCATION.getDocumentation()),
	@XmlEnumValue("122") DISTRICT_HEATING_PRODUCTION_LOCATION((short) CommonConstants.LOCATIONTYPE.DISTRICT_HEATING_PRODUCTION_LOCATION.getValue(), CommonConstants.LOCATIONTYPE.DISTRICT_HEATING_PRODUCTION_LOCATION.getDocumentation()),
	@XmlEnumValue("123") DISTRICT_COOLING_PRODUCTION_LOCATION((short) CommonConstants.LOCATIONTYPE.DISTRICT_COOLING_PRODUCTION_LOCATION.getValue(), CommonConstants.LOCATIONTYPE.DISTRICT_COOLING_PRODUCTION_LOCATION.getDocumentation()),
	@XmlEnumValue("124") GAS_PRODUCTION_LOCATION((short) CommonConstants.LOCATIONTYPE.GAS_PRODUCTION_LOCATION.getValue(), CommonConstants.LOCATIONTYPE.GAS_PRODUCTION_LOCATION.getDocumentation()),
	@XmlEnumValue("125") WATER_PRODUCTION_LOCATION((short) CommonConstants.LOCATIONTYPE.WATER_PRODUCTION_LOCATION.getValue(), CommonConstants.LOCATIONTYPE.WATER_PRODUCTION_LOCATION.getDocumentation()),
	@XmlEnumValue("201") LARGE_MCU_OUTDOOR((short) CommonConstants.LOCATIONTYPE.LARGE_MCU_OUTDOOR.getValue(), CommonConstants.LOCATIONTYPE.LARGE_MCU_OUTDOOR.getDocumentation()),
	@XmlEnumValue("202") SMALL_MCU_INDOOR((short) CommonConstants.LOCATIONTYPE.SMALL_MCU_INDOOR.getValue(), CommonConstants.LOCATIONTYPE.SMALL_MCU_INDOOR.getDocumentation()),
	@XmlEnumValue("203") LARGE_REPEATER_OUTDOOR((short) CommonConstants.LOCATIONTYPE.LARGE_REPEATER_OUTDOOR.getValue(), CommonConstants.LOCATIONTYPE.LARGE_REPEATER_OUTDOOR.getDocumentation()),
	@XmlEnumValue("204") SMALL_REPEATER_INDOOR((short) CommonConstants.LOCATIONTYPE.SMALL_REPEATER_INDOOR.getValue(), CommonConstants.LOCATIONTYPE.SMALL_REPEATER_INDOOR.getDocumentation());

	short value;
	String documentation;

	LocationType(short value, String documentation) {
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
		return LocationType.class.getSimpleName() + " [ value:" + this.value + "  documentation:" + this.getDocumentation() + "]";
	}

	public static LocationType getLocationType(int value) {
		for(LocationType os : LocationType.values()) {
			if(os.getValue() == value) {
				return os;
			}
		}
		return null;
	}
}