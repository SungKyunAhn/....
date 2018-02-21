package com.aimir.fep.command.ws.data;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Response to a SearchReadingOrdersRequest
 * 
 * <p>
 * Java class for SearchReadingOrdersResponse complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 */
@XmlType(name = "SearchReadingOrdersResponse", namespace = "http://ws.command.fep.aimir.com/xsd/OnDemandReading", propOrder = { "readingOrder" })
@XmlAccessorType(XmlAccessType.FIELD)
public class SearchReadingOrdersResponse {
	@XmlElement(nillable = false, required = false, namespace = "http://ws.command.fep.aimir.com/xsd/OnDemandReading")
	private ArrayList<ReadingOrder> readingOrder;

	public SearchReadingOrdersResponse() {
	}

	public SearchReadingOrdersResponse(ArrayList<ReadingOrder> readingOrder) {
		this.readingOrder = readingOrder;
	}

	/**
	 * Gets the value of the readingOrder property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the readingOrder property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getReadingOrder().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link ReadingOrder }
	 * 
	 * 
	 */
	public List<ReadingOrder> getReadingOrder() {
        if (readingOrder == null) {
        	readingOrder = new ArrayList<ReadingOrder>();
        }
        return this.readingOrder;
    }

	@Override
	public String toString() {
		return "SearchReadingOrdersResponse [readingOrder=" + readingOrder
				+ "]";
	}
}