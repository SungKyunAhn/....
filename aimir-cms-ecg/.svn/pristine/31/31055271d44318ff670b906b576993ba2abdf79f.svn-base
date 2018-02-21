package com.aimir.cms.ws.client;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aimir.cms.model.CMSEnt;
import com.aimir.cms.model.ErrorParam;

/**
 * <p>Java class for SearchResp complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SearchResp">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ErrorParam" type="{http://server.ws.cms.aimir.com/}errorParam" minOccurs="0"/>
 *         &lt;element name="SearchResult" type="{http://server.ws.cms.aimir.com/}searchResult" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SearchResp", propOrder = {
    "errorParam", "searchResult"
})
public class SearchResp {

    @XmlElement(name = "ErrorParam")
    protected ErrorParam errorParam;
    
    @XmlElement(name = "SearchResult")
    protected List<CMSEnt> searchResult;

	/**
     * Gets the value of the return property.
     * 
     * @return
     *     possible object is
     *     {@link ErrorParam }
     *     
     */
    public ErrorParam getErrorParam() {
		return errorParam;
	}

    /**
     * Sets the value of the return property.
     * 
     * @param value
     *     allowed object is
     *     {@link ErrorParam }
     *     
     */
	public void setErrorParam(ErrorParam errorParam) {
		this.errorParam = errorParam;
	}

    /**
     * Gets the value of the return property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the return property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReturn().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CMSEnt }
     * 
     * 
     */
    public List<CMSEnt> getSearchResult() {
        if (searchResult == null) {
        	searchResult = new ArrayList<CMSEnt>();
        }
        return this.searchResult;
    }

    /**
     * Sets the value of the return property.
     * 
     * @param value
     *     allowed object is
     *     {@link CMSEnt }
     *     
     */
	public void setSearchResult(List<CMSEnt> searchResult) {
		this.searchResult = searchResult;
	}
    
    
}
