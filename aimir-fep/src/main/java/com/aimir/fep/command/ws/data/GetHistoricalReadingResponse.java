package com.aimir.fep.command.ws.data;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

import com.aimir.fep.command.ws.datatype.LocationType;
import com.aimir.fep.command.ws.datatype.ReadingFrequency;

/**
 * Response to a historical meter reading request
 * 
 * <p>
 * Java class for GetHistoricalReadingResponse complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 */
@XmlType(name = "GetHistoricalReadingResponse", namespace = "http://ws.command.fep.aimir.com/xsd/OnDemandReading", propOrder = {
		"referenceId", "meterSerialNumber", "meterValue",
		"meterValueDate" })
@XmlAccessorType(XmlAccessType.FIELD)
public class GetHistoricalReadingResponse {
	@XmlElement(nillable = false, required = true, namespace = "http://ws.command.fep.aimir.com/xsd/OnDemandReading")
	@XmlSchemaType(name = "positiveInteger")
	private BigInteger referenceId;

	@XmlElement(nillable = false, required = true, namespace = "http://ws.command.fep.aimir.com/xsd/OnDemandReading")
	private String meterSerialNumber;

	@XmlElement(nillable = false, required = true, namespace = "http://ws.command.fep.aimir.com/xsd/OnDemandReading")
	private List<MeterValue> meterValue;

	@XmlElement(nillable = false, required = true, namespace = "http://ws.command.fep.aimir.com/xsd/OnDemandReading")
	@XmlSchemaType(name = "dateTime")
	private XMLGregorianCalendar meterValueDate;

	public GetHistoricalReadingResponse() {
	}

	public GetHistoricalReadingResponse(BigInteger referenceId,
			String meterSerialNumber, LocationType locationType,
			List<MeterValue> meterValue, XMLGregorianCalendar meterValueDate) {
		this.referenceId = referenceId;
		this.meterSerialNumber = meterSerialNumber;
		this.meterValue = meterValue;
		this.meterValueDate = meterValueDate;
	}

	public GetHistoricalReadingResponse(GetHistoricalReadingRequest payload) {
		this.referenceId = payload.getReferenceId();
		this.meterSerialNumber = payload.getMeterSerialNumber();
		this.meterValueDate = payload.getMeterValueDate();
	}

	/**
	 * Gets the value of the referenceId property.
	 * 
	 * * @return possible object is {@link BigInteger }
	 */
	public BigInteger getReferenceId() {
		return referenceId;
	}

	/**
	 * Sets the value of the referenceId property.
	 * 
	 * @return possible object is {@link BigInteger }
	 */
	public void setReferenceId(BigInteger referenceId) {
		this.referenceId = referenceId;
	}

	/**
	 * Gets the value of the meterSerialNumber property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getMeterSerialNumber() {
		return meterSerialNumber;
	}

	/**
	 * Sets the value of the meterSerialNumber property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setMeterSerialNumber(String value) {
		this.meterSerialNumber = value;
	}

	/**
	 * Gets the value of the meterValue property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the meterValue property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getMeterValue().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link MeterValue }
	 * 
	 * 
	 */
	public List<MeterValue> getMeterValue() {
		if (meterValue == null) {
			meterValue = new ArrayList<MeterValue>();
		}
		return meterValue;
	}

	/**
	 * Gets the value of the meterValueDate property.
	 * 
	 * @return possible object is {@link XMLGregorianCalendar }
	 * 
	 */
	public XMLGregorianCalendar getMeterValueDate() {
		return meterValueDate;
	}

	/**
	 * Sets the value of the meterValueDate property.
	 * 
	 * @param value
	 *            allowed object is {@link XMLGregorianCalendar }
	 * 
	 */
	public void setMeterValueDate(XMLGregorianCalendar value) {
		this.meterValueDate = value;
	}

    @Override
    public String toString() {
        return "GetHistoricalReadingResponse [referenceId=" + referenceId
                + ", meterSerialNumber=" + meterSerialNumber + ", meterValue="
                + meterValue + ", meterValueDate=" + meterValueDate + "]";
    }
}
