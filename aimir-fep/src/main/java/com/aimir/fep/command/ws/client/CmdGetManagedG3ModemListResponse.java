
package com.aimir.fep.command.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aimir.fep.protocol.fmp.frame.service.entry.meterDataEntry2;
import com.aimir.fep.protocol.fmp.frame.service.entry.meterEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.modemG3Entry;


/**
 * <p>Java class for cmdGetManagedG3ModemListResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cmdGetManagedG3ModemListResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       	&lt;element name="return" type="{http://server.ws.command.fep.aimir.com/}modemData" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cmdGetManagedG3ModemListResponse", propOrder = {
	    "_return"
})
public class CmdGetManagedG3ModemListResponse {

    @XmlElement(name = "return")
    protected modemG3Entry[] _return;
    
    /**
     * Gets the value of the return property.
     * Objects of the following type(s) are allowed in the list
     * {@link meterEntry }
     * 
     * 
     */
    public modemG3Entry[] getReturn() {
        if (_return == null) {
            _return = new modemG3Entry[0];
        }
        return this._return;
    }

}
