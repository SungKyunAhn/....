package com.aimir.fep.command.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for CmdExtCommand complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="cmdExtCommand">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;element name="McuId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;element name="GeneralStream" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CmdExtCommand", propOrder = {
        "mcuId",
        "generalStream"
})
public class CmdExtCommand {
    @XmlElement(name = "McuId")
    protected String mcuId;

    @XmlElement(name = "GeneralStream")
    protected byte[] generalStream;


    /**
     * Gets the value of the mcuId property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMcuId() {
        return mcuId;
    }

    /**
     * Sets the value of the mcuId property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMcuId(String value) {
        this.mcuId = value;
    }

    /**
     * Gets the value of the getGeneralStream property.
     *
     * @return
     *     possible object is
     *     byte[]
     *
     */
    public byte[] getGeneralStream() {
        return generalStream;
    }

    /**
     * Sets the value of the getGeneralStream property.
     *
     * @param generalStream
     *     allowed object is
     *     byte[]
     *
     */
    public void setGeneralStream(byte[] generalStream) {
        this.generalStream = generalStream;
    }
}
