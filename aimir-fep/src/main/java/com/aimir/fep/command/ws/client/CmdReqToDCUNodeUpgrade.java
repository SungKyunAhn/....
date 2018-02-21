package com.aimir.fep.command.ws.client;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for cmdReqToDCUNodeUpgrade complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cmdReqToDCUNodeUpgrade">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="McuId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="UpgradeType" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="ImageKey" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ImageUrl" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CheckSum" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/> *         
 *         &lt;element name="FilterValue" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cmdReqToDCUNodeUpgrade", propOrder = {
	    "mcuId",
	    "upgradeType",
	    "imageKey",
	    "imageUrl",
	    "checkSum",
	    "filterValue"
})

public class CmdReqToDCUNodeUpgrade {

	@XmlElement(name = "McuId")
	protected String mcuId;	
	@XmlElement(name = "UpgradeType")
	protected int upgradeType;	
	@XmlElement(name = "ImageKey")
	protected String imageKey;	
	@XmlElement(name = "ImageUrl")
	protected String imageUrl;	
	@XmlElement(name = "CheckSum")
	protected String checkSum;	
    @XmlElement(name = "FilterValue")
    protected List<java.lang.String> filterValue;
	
	
	public String getMcuId() {
		return mcuId;
	}
	public void setMcuId(String mcuId) {
		this.mcuId = mcuId;
	}

	public String getImageKey() {
		return imageKey;
	}
	
	public void setImageKey(String imageKey) {
		this.imageKey = imageKey;
	}
	
	public String getImageUrl() {
		return imageUrl;
	}
	
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
	public String getCheckSum() {
		return checkSum;
	}
	
	public void setCheckSum(String checkSum) {
		this.checkSum = checkSum;
	}
	
	public int getUpgradeType() {
		return upgradeType;
	}
	
	public void setUpgradeType(int upgradeType) {
		this.upgradeType = upgradeType;
	}
	
	public List<java.lang.String> getFilterValue() {
		return filterValue;
	}
	
	public void setFilterValue(List<java.lang.String> filterValue) {
		this.filterValue = filterValue;
	}
}
