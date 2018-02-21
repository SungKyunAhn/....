// SP-193
package com.aimir.fep.command.ws.client;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CmdSendEvent complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CmdSendEvent">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="EventAlertName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TargeClassName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ActivatorId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Params" type="{http://jaxb.dev.java.net/array}stringArray" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CmdSendEvent", propOrder = {
    "eventAlertName",
    "target",
    "activatorId",
    "params"
})
public class CmdSendEvent {

    @XmlElement(name = "EventAlertName")
    protected String eventAlertName;
    @XmlElement(name = "TargeClassName")
    protected String target;
    @XmlElement(name = "ActivatorId")
    protected String activatorId;    
    @XmlElement(name = "Params")
    protected List<StringArray> params;
    
    
    /**
     * Gets the value of the eventAlertName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEventAlertName() {
        return eventAlertName;
    }

    /**
     * Sets the value of the eventAlertName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEventAlertName(String value) {
        this.eventAlertName = value;
    }

    /**
     * Gets the value of the target property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTarget() {
        return target;
    }

    /**
     * Sets the value of the target property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTarget(String value) {
        this.target = value;
    }
    
    /**
     * Gets the value of the activatorId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getActivatorID() {
        return activatorId;
    }

    /**
     * Sets the value of the activatorId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setActivatorID(String value) {
        this.activatorId = value;
    }    
    
    
    /**
     * Gets the value of the schedule list property.
     * 
     */
    public List<StringArray> getParams() {
        if (params == null) {
        	params = new ArrayList<StringArray>();
        }
        return this.params;
    }
}
