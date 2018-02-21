
package com.aimir.fep.command.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aimir.fep.modem.BatteryLog;


/**
 * <p>Java class for cmdGetModemBatteryResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cmdGetModemBatteryResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://server.ws.command.fep.aimir.com/}batteryLog" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cmdGetModemBatteryResponse", propOrder = {
    "_return"
})
public class CmdGetModemBatteryResponse {

    @XmlElement(name = "return")
    protected BatteryLog _return;

    /**
     * Gets the value of the return property.
     * 
     * @return
     *     possible object is
     *     {@link BatteryLog }
     *     
     */
    public BatteryLog getReturn() {
        return _return;
    }

    /**
     * Sets the value of the return property.
     * 
     * @param value
     *     allowed object is
     *     {@link BatteryLog }
     *     
     */
    public void setReturn(BatteryLog value) {
        this._return = value;
    }

}
