
package com.aimir.fep.command.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for cmdInstallFile1 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cmdInstallFile1">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="McuId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Filename" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="InstallType" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="ReservationTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cmdInstallFile1", propOrder = {
    "mcuId",
    "filename",
    "installType",
    "reservationTime"
})
public class CmdInstallFile1 {

    @XmlElement(name = "McuId")
    protected String mcuId;
    @XmlElement(name = "Filename")
    protected String filename;
    @XmlElement(name = "InstallType")
    protected int installType;
    @XmlElement(name = "ReservationTime")
    protected String reservationTime;

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
     * Gets the value of the filename property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Sets the value of the filename property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFilename(String value) {
        this.filename = value;
    }

    /**
     * Gets the value of the installType property.
     * 
     */
    public int getInstallType() {
        return installType;
    }

    /**
     * Sets the value of the installType property.
     * 
     */
    public void setInstallType(int value) {
        this.installType = value;
    }

    /**
     * Gets the value of the reservationTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReservationTime() {
        return reservationTime;
    }

    /**
     * Sets the value of the reservationTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReservationTime(String value) {
        this.reservationTime = value;
    }

}
