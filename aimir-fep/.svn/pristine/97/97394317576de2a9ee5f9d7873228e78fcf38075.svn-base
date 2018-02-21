package com.aimir.fep.command.ws.data;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import com.aimir.fep.command.ws.datatype.MeasurementUnit;
import com.aimir.fep.command.ws.datatype.MeterValueMeasurementType;

/**
 * Meter value type
 * 
 * <p>
 * Java class for MeterValue complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 */
@XmlType(name = "MeterValue", namespace = "http://ws.command.fep.aimir.com/xsd/CommonTypes", propOrder = {
		"meterValueMeasurementType", "meterReading", "measurementUnit" })
@XmlAccessorType(XmlAccessType.FIELD)
public class MeterValue {
	@XmlElement(nillable = false, required = true)
	private MeterValueMeasurementType meterValueMeasurementType;

	@XmlElement(nillable = false, required = true)
	@XmlSchemaType(name = "decimal")
	private BigDecimal meterReading;

	@XmlElement(nillable = false, required = true)
	private MeasurementUnit measurementUnit;

	public MeterValue() {
	}

	public MeterValue(MeterValueMeasurementType meterValueMeasurementType,
			BigDecimal meterReading, MeasurementUnit measurementUnit) {
		this.meterValueMeasurementType = meterValueMeasurementType;
		this.meterReading = meterReading;
		this.measurementUnit = measurementUnit;
	}

	/**
	 * Gets the value of the meterValueMeasurementType property.
	 * 
	 * @return possible object is {@link MeterValueMeasurementType }
	 */
	public MeterValueMeasurementType getMeterValueMeasurementType() {
		return meterValueMeasurementType;
	}

	/**
	 * Sets the value of the meterValueMeasurementType property.
	 * 
	 * @param value
	 *            allowed object is {@link MeterValueMeasurementType }
	 * 
	 */
	public void setMeterValueMeasurementType(
			MeterValueMeasurementType meterValueMeasurementType) {
		this.meterValueMeasurementType = meterValueMeasurementType;
	}

	/**
	 * Gets the value of the meterReading property.
	 * 
	 * @return possible object is {@link BigDecimal }
	 */
	public BigDecimal getMeterReading() {
		return meterReading;
	}

	/**
	 * Sets the value of the meterReading property.
	 * 
	 * @param value
	 *            allowed object is {@link BigDecimal }
	 * 
	 */
	public void setMeterReading(BigDecimal meterReading) {
		this.meterReading = meterReading;
	}

	/**
	 * Gets the value of the measurementUnit property.
	 * 
	 * @return possible object is {@link MeasurementUnit }
	 */
	public MeasurementUnit getMeasurementUnit() {
		return measurementUnit;
	}

	/**
	 * Sets the value of the measurementUnit property.
	 * 
	 * @param value
	 *            allowed object is {@link MeasurementUnit }
	 * 
	 */
	public void setMeasurementUnit(MeasurementUnit measurementUnit) {
		this.measurementUnit = measurementUnit;
	}

	@Override
	public String toString() {
		return "MeterValue [meterValueMeasurementType="
				+ meterValueMeasurementType + ", meterReading=" + meterReading
				+ ", measurementUnit=" + measurementUnit + "]";
	}

}