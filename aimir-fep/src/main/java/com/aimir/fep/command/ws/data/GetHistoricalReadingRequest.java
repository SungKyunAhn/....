package com.aimir.fep.command.ws.data;

import java.math.BigInteger;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Request a historical meter reading
 * 
 * <p>
 * Java class for GetHistoricalReadingRequest complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 */
@XmlType(name = "GetHistoricalReadingRequest", namespace = "http://ws.command.fep.aimir.com/xsd/OnDemandReading", propOrder = {
		"referenceId", "meterSerialNumber", "meterValueDate" })
@XmlAccessorType(XmlAccessType.FIELD)
public class GetHistoricalReadingRequest {

    @XmlElement(nillable = false, required = true, namespace = "http://ws.command.fep.aimir.com/xsd/OnDemandReading")
    @XmlSchemaType(name = "positiveInteger")
    private BigInteger referenceId;

	@XmlElement(nillable = false, required = true, namespace = "http://ws.command.fep.aimir.com/xsd/OnDemandReading")
	private String meterSerialNumber = null;

	@XmlElement(nillable = true, required = true, namespace = "http://ws.command.fep.aimir.com/xsd/OnDemandReading")
	@XmlSchemaType(name = "dateTime")
	private XMLGregorianCalendar meterValueDate;

	public GetHistoricalReadingRequest() {
	}

    public GetHistoricalReadingRequest(BigInteger referenceId,
            String meterSerialNumber,
            XMLGregorianCalendar meterValueDate) {
        this.referenceId = referenceId;
        this.meterSerialNumber = meterSerialNumber;
        this.meterValueDate = meterValueDate;
    }

    /**
     * Gets the value of the referenceId property.
     * 
     * @return possible object is {@link BigInteger }
     */
    public BigInteger getReferenceId() {
        return referenceId;
    }

    /**
     * Sets the value of the referenceId property.
     * 
     * @param value
     *            allowed object is {@link BigInteger }
     */
    public void setReferenceId(BigInteger referenceId) {
        this.referenceId = referenceId;
    }

	/**
	 * Gets the value of the meterSerialNumber property.
	 * 
	 * @return possible object is {@link String }
	 */
	public String getMeterSerialNumber() {
		return meterSerialNumber;
	}

	/**
	 * Sets the value of the meterSerialNumber property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 */
	public void setMeterSerialNumber(String meterSerialNumber) {
		this.meterSerialNumber = meterSerialNumber;
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
	public void setMeterValueDate(XMLGregorianCalendar meterValueDate) {
		this.meterValueDate = meterValueDate;
	}

    @Override
    public String toString() {
        return "GetHistoricalReadingRequest [meterSerialNumber="
                + meterSerialNumber + ", meterValueDate=" + meterValueDate
                + "]";
    }
}