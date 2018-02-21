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

import com.aimir.fep.command.ws.datatype.MeterValueStatus;
import com.aimir.fep.command.ws.datatype.OrderStatus;
import com.aimir.fep.command.ws.datatype.PowerOperation;

/**
 * Response to a power on/off event request
 * 
 * <p>
 * Java class for HandlePowerOnOffRequest complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HandlePowerOnOffRequest", namespace = "http://ws.command.fep.aimir.com/xsd/PowerOnOffCallback", propOrder = {
		"referenceId", "meterSerialNumber", "powerOperation",
		"powerOperationDate", "meterValue", "meterValueStatus",
		"userReference", "userCreateDate", "orderStatus", "applicationFault" })
public class HandlePowerOnOffRequest {

	@XmlElement(namespace = "http://ws.command.fep.aimir.com/xsd/PowerOnOffCallback")
	@XmlSchemaType(name = "positiveInteger")
	private BigInteger referenceId;

	@XmlElement(required = true, namespace = "http://ws.command.fep.aimir.com/xsd/PowerOnOffCallback")
	protected String meterSerialNumber;

	@XmlElement(namespace = "http://ws.command.fep.aimir.com/xsd/PowerOnOffCallback")
	protected PowerOperation powerOperation;

	@XmlElement(required = true, namespace = "http://ws.command.fep.aimir.com/xsd/PowerOnOffCallback")
	@XmlSchemaType(name = "dateTime")
	protected XMLGregorianCalendar powerOperationDate;

	@XmlElement(namespace = "http://ws.command.fep.aimir.com/xsd/PowerOnOffCallback")
	protected ArrayList<MeterValue> meterValue;

	@XmlElement(namespace = "http://ws.command.fep.aimir.com/xsd/PowerOnOffCallback")
	protected MeterValueStatus meterValueStatus;

	@XmlElement(required = true, namespace = "http://ws.command.fep.aimir.com/xsd/PowerOnOffCallback")
	protected String userReference;

	@XmlElement(required = true, namespace = "http://ws.command.fep.aimir.com/xsd/PowerOnOffCallback")
	@XmlSchemaType(name = "dateTime")
	protected XMLGregorianCalendar userCreateDate;

	@XmlElement(namespace = "http://ws.command.fep.aimir.com/xsd/PowerOnOffCallback")
	protected OrderStatus orderStatus;

	@XmlElement(required = true, nillable = true, namespace = "http://ws.command.fep.aimir.com/xsd/PowerOnOffCallback")
	protected ApplicationFault applicationFault;

	public HandlePowerOnOffRequest() {
	}

	public HandlePowerOnOffRequest(com.aimir.model.system.PowerOnOffOrder ro) {
		this.referenceId = new BigInteger(String.valueOf(ro.getReferenceId()));
		this.meterSerialNumber = ro.getMeterSerialNumber();
		this.powerOperation = PowerOperation.getPowerOperation(ro
				.getPowerOperation());
		if (ro.getPowerOperationDate() != null) {
			GregorianCalendar gcal = new GregorianCalendar();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			try {
				Date d = sdf.parse(ro.getPowerOperationDate());
				gcal.setTime(d);
				this.powerOperationDate = (DatatypeFactory.newInstance()
						.newXMLGregorianCalendar(gcal));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.userReference = ro.getUserReference();
		if (ro.getUserCreateDate() != null) {
			GregorianCalendar gcal = new GregorianCalendar();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			try {
				Date d = sdf.parse(ro.getUserCreateDate());
				gcal.setTime(d);
				this.userCreateDate = (DatatypeFactory.newInstance()
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
	 * Gets the value of the powerOperation property.
	 * 
	 * @return possible object is {@link PowerOperation }
	 * 
	 */
	public PowerOperation getPowerOperation() {
		return powerOperation;
	}

	/**
	 * Sets the value of the powerOperation property.
	 * 
	 * @param value
	 *            allowed object is {@link PowerOperation }
	 * 
	 */
	public void setPowerOperation(PowerOperation value) {
		this.powerOperation = value;
	}

	/**
	 * Gets the value of the powerOperationDate property.
	 * 
	 * @return possible object is {@link XMLGregorianCalendar }
	 * 
	 */
	public XMLGregorianCalendar getPowerOperationDate() {
		return powerOperationDate;
	}

	/**
	 * Sets the value of the powerOperationDate property.
	 * 
	 * @param value
	 *            allowed object is {@link XMLGregorianCalendar }
	 * 
	 */
	public void setPowerOperationDate(XMLGregorianCalendar value) {
		this.powerOperationDate = value;
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
		return this.meterValue;
	}

	/**
	 * Gets the value of the meterValueStatus property.
	 * 
	 * @return possible object is {@link MeterValueStatus }
	 * 
	 */
	public MeterValueStatus getMeterValueStatus() {
		return meterValueStatus;
	}

	/**
	 * Sets the value of the meterValueStatus property.
	 * 
	 * @param value
	 *            allowed object is {@link MeterValueStatus }
	 * 
	 */
	public void setMeterValueStatus(MeterValueStatus value) {
		this.meterValueStatus = value;
	}

	/**
	 * Gets the value of the userReference property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getUserReference() {
		return userReference;
	}

	/**
	 * Sets the value of the userReference property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setUserReference(String value) {
		this.userReference = value;
	}

	/**
	 * Gets the value of the userCreateDate property.
	 * 
	 * @return possible object is {@link XMLGregorianCalendar }
	 * 
	 */
	public XMLGregorianCalendar getUserCreateDate() {
		return userCreateDate;
	}

	/**
	 * Sets the value of the userCreateDate property.
	 * 
	 * @param value
	 *            allowed object is {@link XMLGregorianCalendar }
	 * 
	 */
	public void setUserCreateDate(XMLGregorianCalendar value) {
		this.userCreateDate = value;
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
	 * @param value
	 *            allowed object is {@link OrderStatus }
	 * 
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
		return "HandlePowerOnOffRequest [referenceId=" + referenceId
				+ ", meterSerialNumber=" + meterSerialNumber
				+ ", powerOperation=" + powerOperation
				+ ", powerOperationDate=" + powerOperationDate
				+ ", meterValue=" + meterValue + ", meterValueStatus="
				+ meterValueStatus + ", userReference=" + userReference
				+ ", userCreateDate=" + userCreateDate + ", orderStatus="
				+ orderStatus + ", applicationFault=" + applicationFault + "]";
	}
}
