
package com.aimir.fep.command.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for cmdGetApn complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cmdGetApn">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MdsId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cmdGetApn", propOrder = {
    "mdsId"
})
public class CmdGetApn {

    @XmlElement(name = "MdsId")
    protected String mdsId;

    /**
     * Gets the value of the mdsId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMdsId() {
        return mdsId;
    }

    /**
     * Sets the value of the mdsId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMdsId(String value) {
        this.mdsId = value;
    }

}
