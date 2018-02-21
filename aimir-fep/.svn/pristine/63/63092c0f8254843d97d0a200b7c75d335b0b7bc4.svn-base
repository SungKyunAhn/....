package com.aimir.fep.command.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aimir.fep.protocol.nip.command.AlarmEventCmd;

/**
 * <p>Java class for cmdAlarmEventCommandOnOff complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="cmdAlarmEventCommandOnOff">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="modemId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="requestType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="count" type="{http://www.w3.org/2001/XMLSchema}count" />  
 *         &lt;element name="cmds" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cmdAlarmEventCommandOnOff", propOrder = {
        "modemId",
        "requestType",
        "count",
        "cmds"
        
})
public class CmdAlarmEventCommandOnOff {
    @XmlElement(name = "ModemId")
    protected String modemId;

    @XmlElement(name = "RequestType")
    protected String requestType;

    @XmlElement(name = "Count")
    protected int count;

    @XmlElement(name = "Cmds")
    protected String cmds;
    
 
	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @param count the count to set
	 */
	public void setCount(int count) {
		this.count = count;
	}

	/**
	 * @return the cmds
	 */
	public String getCmds() {
		return cmds;
	}

	/**
	 * @param cmds the cmds to set
	 */
	public void setCmds(String cmds) {
		this.cmds = cmds;
	}

	/**
     * Get the value of the modemOd property.
     * @return String
     */
    public String getModemId() {
        return modemId;
    }

    /**
     * Set the value of the modemId property.
     * @param modemId
     */
    public void setModemId(String modemId) {
        this.modemId = modemId;
    }

    /**
     * Get the value of the requestType property.
     * @return String
     */
    public String getRequestType() {
        return requestType;
    }

    /**
     * Set the value of the requestType property.
     * @param requestType
     */
    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }


}
