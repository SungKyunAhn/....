
package com.aimir.fep.command.ws.client;

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
 * &lt;complexType name="CmdTCPTrigger">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Cmd" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Ip Addr" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CmdTCPTrigger", propOrder = {
    "cmd",
    "ipAddr"
})
public class CmdTCPTrigger {

    @XmlElement(name = "Cmd")
    protected String cmd;
    @XmlElement(name = "IpAddr")
    protected String ipAddr;
    
    
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
    public void setCmd(String value) {
        this.cmd = value;
    }

    /**
     * Gets the value of the ip property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIpAddr() {
        return ipAddr;
    }

    /**
     * Sets the value of the ip property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIpAddr(String value) {
        this.ipAddr = value;
    }
}
