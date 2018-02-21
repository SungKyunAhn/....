package com.aimir.fep.command.ws.data;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Response to a SearchPowerOnOffOrdersRequest
 * 
 * <p>
 * Java class for SearchPowerOnOffOrdersResponse complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SearchPowerOnOffOrdersResponse", namespace = "http://ws.command.fep.aimir.com/xsd/PowerOnOff", propOrder = { "powerOnOffOrder" })
public class SearchPowerOnOffOrdersResponse {
	@XmlElement(name = "powerOnOffOrder", namespace = "http://ws.command.fep.aimir.com/xsd/PowerOnOff")
	protected List<PowerOnOffOrder> powerOnOffOrder;

	/**
	 * Gets the value of the powerOnOffOrder property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the powerOnOffOrder property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getPowerOnOffOrder().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link PowerOnOffOrder }
	 * 
	 * 
	 */
	public List<PowerOnOffOrder> getPowerOnOffOrder() {
		if (powerOnOffOrder == null) {
			powerOnOffOrder = new ArrayList<PowerOnOffOrder>();
		}
		return this.powerOnOffOrder;
	}

	@Override
	public String toString() {
		return "SearchPowerOnOffOrdersResponse [powerOnOffOrder="
				+ powerOnOffOrder + "]";
	}

}