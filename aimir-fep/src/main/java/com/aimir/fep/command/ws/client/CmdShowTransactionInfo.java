
package com.aimir.fep.command.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for cmdShowTransactionInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cmdShowTransactionInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="McuId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TransactionId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="Option" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cmdShowTransactionInfo", propOrder = {
    "mcuId",
    "transactionId",
    "option"
})
public class CmdShowTransactionInfo {

    @XmlElement(name = "McuId")
    protected String mcuId;
    @XmlElement(name = "TransactionId")
    protected long transactionId;
    @XmlElement(name = "Option")
    protected int option;

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
     * Gets the value of the transactionId property.
     * 
     */
    public long getTransactionId() {
        return transactionId;
    }

    /**
     * Sets the value of the transactionId property.
     * 
     */
    public void setTransactionId(long value) {
        this.transactionId = value;
    }

    /**
     * Gets the value of the option property.
     * 
     */
    public int getOption() {
        return option;
    }

    /**
     * Sets the value of the option property.
     * 
     */
    public void setOption(int value) {
        this.option = value;
    }

}
