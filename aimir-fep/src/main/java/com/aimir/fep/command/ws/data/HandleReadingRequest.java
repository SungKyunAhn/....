package com.aimir.fep.command.ws.data;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.aimir.fep.command.ws.datatype.OrderStatus;
import com.aimir.model.system.OnDemandReadingOrder;

/**
 * Response to a historical meter reading request
 * 
 * <p>
 * Java class for HandleReadingRequest complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 */
@XmlType(name = "HandleReadingRequest", namespace = "http://ws.command.fep.aimir.com/xsd/OnDemandReadingCallback", propOrder = {
		"referenceId", "meterSerialNumber", "meterValue",
		"meterValueDate", "orderStatus", "applicationFault" })
@XmlAccessorType(XmlAccessType.FIELD)
public class HandleReadingRequest {

	@XmlElement(nillable = false, required = true, namespace = "http://ws.command.fep.aimir.com/xsd/OnDemandReadingCallback")
	@XmlSchemaType(name = "positiveInteger")
	private BigInteger referenceId;

	@XmlElement(nillable = true, required = true, namespace = "http://ws.command.fep.aimir.com/xsd/OnDemandReadingCallback")
	private String meterSerialNumber;

	@XmlElement(nillable = false, required = false, namespace = "http://ws.command.fep.aimir.com/xsd/OnDemandReadingCallback")
	private ArrayList<MeterValue> meterValue;

	@XmlElement(nillable = false, required = true, namespace = "http://ws.command.fep.aimir.com/xsd/OnDemandReadingCallback")
	@XmlSchemaType(name = "dateTime")
	private XMLGregorianCalendar meterValueDate;

	@XmlElement(nillable = false, required = true, namespace = "http://ws.command.fep.aimir.com/xsd/OnDemandReadingCallback")
	private OrderStatus orderStatus;

	@XmlElement(nillable = true, required = true, namespace = "http://ws.command.fep.aimir.com/xsd/OnDemandReadingCallback")
	private ApplicationFault applicationFault;

	public HandleReadingRequest() {
	}

	public HandleReadingRequest(OnDemandReadingOrder ro) {
		this.referenceId = new BigInteger(String.valueOf(ro.getReferenceId()));
		this.meterSerialNumber = ro.getMeterSerialNumber();

		if (ro.getMeterValueDate() != null) {
			GregorianCalendar gcal = new GregorianCalendar();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			try {
				Date d = sdf.parse(ro.getMeterValueDate());
				gcal.setTime(d);
				this.meterValueDate = (DatatypeFactory.newInstance()
						.newXMLGregorianCalendar(gcal));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Gets the value of the referenceId property.
	 * 
	 * @return possible object is {@link BigInteger }
	 * 
	 */
	public BigInteger getReferenceId() {
		return referenceId;
	}

	/**
	 * Sets the value of the referenceId property.
	 * 
	 * @param value
	 *            allowed object is {@link BigInteger }
	 * 
	 */
	public void setReferenceId(BigInteger value) {
		this.referenceId = value;
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
	 * Gets the value of the orderStatus property.
	 * 
	 * @return possible object is {@link OrderStatus }
	 * 
	 */
	public OrderStatus getOrderStatus() {
		return orderStatus;
	}

	/**
	 * Sets the value of the orderStatus property.
	 * 
	 * * @param value allowed object is {@link OrderStatus }
	 */
	public void setOrderStatus(OrderStatus value) {
		this.orderStatus = value;
	}

	/**
	 * Gets the value of the applicationFault property.
	 * 
	 * @return possible object is {@link ApplicationFault }
	 * 
	 */
	public ApplicationFault getApplicationFault() {
		return applicationFault;
	}

	/**
	 * Sets the value of the applicationFault property.
	 * 
	 * @param value
	 *            allowed object is {@link ApplicationFault }
	 * 
	 */
	public void setApplicationFault(ApplicationFault value) {
		this.applicationFault = value;
	}

	@Override
	public String toString() {
		return "HandleReadingRequest [referenceId=" + referenceId
				+ ", meterSerialNumber=" + meterSerialNumber + ", meterValue="
				+ meterValue + ", meterValueDate=" + meterValueDate
				+ ", orderStatus=" + orderStatus + ", applicationFault="
				+ applicationFault + "]";
	}
}
