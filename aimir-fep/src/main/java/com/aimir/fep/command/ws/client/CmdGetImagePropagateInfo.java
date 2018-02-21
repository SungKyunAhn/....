package com.aimir.fep.command.ws.client;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aimir.fep.protocol.fmp.exception.FMPMcuException;

/**
 * <p>
 * Java class for CmdGetImagePropagateInfo complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 *
 * <pre>
 * &lt;complexType name="cmdGetImagePropagateInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;element name="McuId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;element name="UpgradeType" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cmdGetImagePropagateInfo", propOrder = {
	    "mcuId",
	    "upgradeType"
})

public class CmdGetImagePropagateInfo {
	@XmlElement(name = "McuId")
	protected String mcuId;	
	@XmlElement(name = "UpgradeType")
	protected int upgradeType;		
	
	public String getMcuId() {
		return mcuId;
	}
	public void setMcuId(String mcuId) {
		this.mcuId = mcuId;
	}

	public int getUpgradeType() {
		return upgradeType;
	}
	
	public void setUpgradeType(int upgradeType) {
		this.upgradeType = upgradeType;
	}	

}
