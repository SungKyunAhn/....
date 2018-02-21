package com.aimir.fep.command.ws.client;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for cmdMcuStdSet complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cmdMcuStdSet">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="McuId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Key" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="KeyValue" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cmdMcuStdSet", propOrder = {
    "mcuId",
    "key",
    "keyValue"
})
public class CmdMcuStdSet {
	@XmlElement(name = "McuId")
    protected String mcuId;
    @XmlElement(name = "Key")
    protected String[] key;
    @XmlElement(name = "KeyValue")
    protected String[] keyValue;
    
	public String getMcuId() {
		return mcuId;
	}
	public void setMcuId(String mcuId) {
		this.mcuId = mcuId;
	}
	public String[] getKey() {
		return key;
	}
	public void setKey(String[] key) {
		this.key = key;
	}
	public String[] getKeyValue() {
		return keyValue;
	}
	public void setKeyValue(String[] keyValue) {
		this.keyValue = keyValue;
	}
}
