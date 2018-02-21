
package com.aimir.fep.command.ws.client;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CmdTCPTrigger complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CmdLineModemEVN">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Cmd" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ModemId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Params" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CmdLineModemEVN", propOrder = {
    "cmd",
    "modemId",
    "params"
})
public class CmdLineModemEVN {

	@XmlElement(name = "Cmd")
	protected String cmd;
	@XmlElement(name = "ModemId")
	protected String modemId;	    
	@XmlElement(name = "Params")
	protected List<String> params;
	
	/**
     * Gets the value of the cmd property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	public String getCmd() {
		return cmd;
	}
	
	/**
     * Sets the value of the cmd property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
	public void setCmd(String cmd) {
		this.cmd = cmd;
	}
	
	/**
     * Gets the value of the modemId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	public String getModemId() {
		return modemId;
	}
	
	/**
     * Sets the value of the modemId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
	public void setModemId(String modemId) {
		this.modemId = modemId;
	}
	
	/**
     * Gets the value of the datas property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	public List<String> getParams() {
		return params;
	}
	
	/**
     * Sets the value of the datas property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
	public void setParams(List<String> params) {
		this.params = params;
	}
    
    
}
