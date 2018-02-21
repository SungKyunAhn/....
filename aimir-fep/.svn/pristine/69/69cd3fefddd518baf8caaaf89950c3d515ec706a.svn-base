package com.aimir.fep.command.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for cmdModemPortInformation complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="cmdModemPortInformation">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="modemId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="requestType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="targetType" type="{http://www.w3.org/2001/XMLSchema}int" />  
 *         &lt;element name="port" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cmdModemPortInformation", propOrder = {
        "modemId",
        "requestType",
        "targetType",
        "port"
})
public class CmdModemPortInformation {
    @XmlElement(name = "ModemId")
    protected String modemId;

    @XmlElement(name = "RequestType")
    protected String requestType;

    @XmlElement(name = "TargetType")
    protected int targetType;

    @XmlElement(name = "Port")
    protected String port;
    
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
    
    /**
	 * @return the targetType
	 */
	public int getTargetType() {
		return targetType;
	}

	/**
	 * @param targetType the targetType to set
	 */
	public void setTargetType(int targetType) {
		this.targetType = targetType;
	}

	/**
	 * @return the port
	 */
	public String getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(String port) {
		this.port = port;
	}

}
