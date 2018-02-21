
package com.aimir.fep.command.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aimir.fep.protocol.fmp.frame.service.entry.meterDataEntry2;


/**
 * <p>Java class for cmdGetMeterListResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cmdGetMeterListResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://server.ws.command.fep.aimir.com/}meterData" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cmdGetMeterListResponse", propOrder = {
    "_return"
})
public class CmdGetMeterListResponse {

    @XmlElement(name = "return")
    protected meterDataEntry2[] _return;

    /**
     * Gets the value of the return property.
     * Objects of the following type(s) are allowed in the list
     * {@link meterEntry }
     * 
     * 
     */
    public meterDataEntry2[] getReturn() {
        if (_return == null) {
            _return = new meterDataEntry2[0];
        }
        return this._return;
    }

}
