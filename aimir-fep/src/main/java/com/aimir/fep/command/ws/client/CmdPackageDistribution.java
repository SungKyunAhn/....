
package com.aimir.fep.command.ws.client;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for cmdPackageDistribution complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cmdPackageDistribution">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="McuId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="EquipType" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="TriggerId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OldHwVersion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OldSwVersion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OldBuildNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="NewHwVersion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="NewSwVersion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="NewBuildNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="BinaryMD5" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="BinaryUrl" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DiffMD5" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DiffUrl" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="EquipList" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="OTAType" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="ModemType" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="ModemTypeStr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DataType" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="OTALevel" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="OTARetry" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cmdPackageDistribution", propOrder = {
    "mcuId",
    "equipType",
    "triggerId",
    "oldHwVersion",
    "oldSwVersion",
    "oldBuildNo",
    "newHwVersion",
    "newSwVersion",
    "newBuildNo",
    "binaryMD5",
    "binaryUrl",
    "diffMD5",
    "diffUrl",
    "equipList",
    "otaType",
    "modemType",
    "modemTypeStr",
    "dataType",
    "otaLevel",
    "otaRetry"
})
public class CmdPackageDistribution {

    @XmlElement(name = "McuId")
    protected String mcuId;
    @XmlElement(name = "EquipType")
    protected int equipType;
    @XmlElement(name = "TriggerId")
    protected String triggerId;
    @XmlElement(name = "OldHwVersion")
    protected String oldHwVersion;
    @XmlElement(name = "OldSwVersion")
    protected String oldSwVersion;
    @XmlElement(name = "OldBuildNo")
    protected String oldBuildNo;
    @XmlElement(name = "NewHwVersion")
    protected String newHwVersion;
    @XmlElement(name = "NewSwVersion")
    protected String newSwVersion;
    @XmlElement(name = "NewBuildNo")
    protected String newBuildNo;
    @XmlElement(name = "BinaryMD5")
    protected String binaryMD5;
    @XmlElement(name = "BinaryUrl")
    protected String binaryUrl;
    @XmlElement(name = "DiffMD5")
    protected String diffMD5;
    @XmlElement(name = "DiffUrl")
    protected String diffUrl;
    @XmlElement(name = "EquipList")
    protected List<String> equipList;
    @XmlElement(name = "OTAType")
    protected int otaType;
    @XmlElement(name = "ModemType")
    protected int modemType;
    @XmlElement(name = "ModemTypeStr")
    protected String modemTypeStr;
    @XmlElement(name = "DataType")
    protected int dataType;
    @XmlElement(name = "OTALevel")
    protected int otaLevel;
    @XmlElement(name = "OTARetry")
    protected int otaRetry;

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
     * Gets the value of the equipType property.
     * 
     */
    public int getEquipType() {
        return equipType;
    }

    /**
     * Sets the value of the equipType property.
     * 
     */
    public void setEquipType(int value) {
        this.equipType = value;
    }

    /**
     * Gets the value of the triggerId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTriggerId() {
        return triggerId;
    }

    /**
     * Sets the value of the triggerId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTriggerId(String value) {
        this.triggerId = value;
    }

    /**
     * Gets the value of the oldHwVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOldHwVersion() {
        return oldHwVersion;
    }

    /**
     * Sets the value of the oldHwVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOldHwVersion(String value) {
        this.oldHwVersion = value;
    }

    /**
     * Gets the value of the oldSwVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOldSwVersion() {
        return oldSwVersion;
    }

    /**
     * Sets the value of the oldSwVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOldSwVersion(String value) {
        this.oldSwVersion = value;
    }

    /**
     * Gets the value of the oldBuildNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOldBuildNo() {
        return oldBuildNo;
    }

    /**
     * Sets the value of the oldBuildNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOldBuildNo(String value) {
        this.oldBuildNo = value;
    }

    /**
     * Gets the value of the newHwVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNewHwVersion() {
        return newHwVersion;
    }

    /**
     * Sets the value of the newHwVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNewHwVersion(String value) {
        this.newHwVersion = value;
    }

    /**
     * Gets the value of the newSwVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNewSwVersion() {
        return newSwVersion;
    }

    /**
     * Sets the value of the newSwVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNewSwVersion(String value) {
        this.newSwVersion = value;
    }

    /**
     * Gets the value of the newBuildNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNewBuildNo() {
        return newBuildNo;
    }

    /**
     * Sets the value of the newBuildNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNewBuildNo(String value) {
        this.newBuildNo = value;
    }

    /**
     * Gets the value of the binaryMD5 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBinaryMD5() {
        return binaryMD5;
    }

    /**
     * Sets the value of the binaryMD5 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBinaryMD5(String value) {
        this.binaryMD5 = value;
    }

    /**
     * Gets the value of the binaryUrl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBinaryUrl() {
        return binaryUrl;
    }

    /**
     * Sets the value of the binaryUrl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBinaryUrl(String value) {
        this.binaryUrl = value;
    }

    /**
     * Gets the value of the diffMD5 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDiffMD5() {
        return diffMD5;
    }

    /**
     * Sets the value of the diffMD5 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDiffMD5(String value) {
        this.diffMD5 = value;
    }

    /**
     * Gets the value of the diffUrl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDiffUrl() {
        return diffUrl;
    }

    /**
     * Sets the value of the diffUrl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDiffUrl(String value) {
        this.diffUrl = value;
    }

    /**
     * Gets the value of the equipList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the equipList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEquipList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getEquipList() {
        if (equipList == null) {
            equipList = new ArrayList<String>();
        }
        return this.equipList;
    }

    /**
     * Gets the value of the otaType property.
     * 
     */
    public int getOTAType() {
        return otaType;
    }

    /**
     * Sets the value of the otaType property.
     * 
     */
    public void setOTAType(int value) {
        this.otaType = value;
    }

    /**
     * Gets the value of the modemType property.
     * 
     */
    public int getModemType() {
        return modemType;
    }

    /**
     * Sets the value of the modemType property.
     * 
     */
    public void setModemType(int value) {
        this.modemType = value;
    }

    /**
     * Gets the value of the modemTypeStr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getModemTypeStr() {
        return modemTypeStr;
    }

    /**
     * Sets the value of the modemTypeStr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setModemTypeStr(String value) {
        this.modemTypeStr = value;
    }

    /**
     * Gets the value of the dataType property.
     * 
     */
    public int getDataType() {
        return dataType;
    }

    /**
     * Sets the value of the dataType property.
     * 
     */
    public void setDataType(int value) {
        this.dataType = value;
    }

    /**
     * Gets the value of the otaLevel property.
     * 
     */
    public int getOTALevel() {
        return otaLevel;
    }

    /**
     * Sets the value of the otaLevel property.
     * 
     */
    public void setOTALevel(int value) {
        this.otaLevel = value;
    }

    /**
     * Gets the value of the otaRetry property.
     * 
     */
    public int getOTARetry() {
        return otaRetry;
    }

    /**
     * Sets the value of the otaRetry property.
     * 
     */
    public void setOTARetry(int value) {
        this.otaRetry = value;
    }

}
