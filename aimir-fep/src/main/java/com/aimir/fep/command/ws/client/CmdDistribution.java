
package com.aimir.fep.command.ws.client;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for cmdDistribution complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cmdDistribution">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="McuId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TriggerId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="EquipKind" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Model" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TransferType" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="OTAStep" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="MultiWriteCount" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="MaxRetryCount" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="OTAThreadCount" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="InstallType" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="OldHwVersion" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="OldFwVersion" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="OldBuild" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="NewHwVersion" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="NewFwVersion" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="NewBuild" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="BinaryURL" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="BinaryMD5" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DiffURL" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DiffMD5" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="EquipIdList" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cmdDistribution", propOrder = {
    "mcuId",
    "triggerId",
    "equipKind",
    "model",
    "transferType",
    "otaStep",
    "multiWriteCount",
    "maxRetryCount",
    "otaThreadCount",
    "installType",
    "oldHwVersion",
    "oldFwVersion",
    "oldBuild",
    "newHwVersion",
    "newFwVersion",
    "newBuild",
    "binaryURL",
    "binaryMD5",
    "diffURL",
    "diffMD5",
    "equipIdList"
})
public class CmdDistribution {

    @XmlElement(name = "McuId")
    protected String mcuId;
    @XmlElement(name = "TriggerId")
    protected String triggerId;
    @XmlElement(name = "EquipKind")
    protected int equipKind;
    @XmlElement(name = "Model")
    protected String model;
    @XmlElement(name = "TransferType")
    protected int transferType;
    @XmlElement(name = "OTAStep")
    protected int otaStep;
    @XmlElement(name = "MultiWriteCount")
    protected int multiWriteCount;
    @XmlElement(name = "MaxRetryCount")
    protected int maxRetryCount;
    @XmlElement(name = "OTAThreadCount")
    protected int otaThreadCount;
    @XmlElement(name = "InstallType")
    protected int installType;
    @XmlElement(name = "OldHwVersion")
    protected int oldHwVersion;
    @XmlElement(name = "OldFwVersion")
    protected int oldFwVersion;
    @XmlElement(name = "OldBuild")
    protected int oldBuild;
    @XmlElement(name = "NewHwVersion")
    protected int newHwVersion;
    @XmlElement(name = "NewFwVersion")
    protected int newFwVersion;
    @XmlElement(name = "NewBuild")
    protected int newBuild;
    @XmlElement(name = "BinaryURL")
    protected String binaryURL;
    @XmlElement(name = "BinaryMD5")
    protected String binaryMD5;
    @XmlElement(name = "DiffURL")
    protected String diffURL;
    @XmlElement(name = "DiffMD5")
    protected String diffMD5;
    @XmlElement(name = "EquipIdList")
    protected List<Object> equipIdList;

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
     * Gets the value of the equipKind property.
     * 
     */
    public int getEquipKind() {
        return equipKind;
    }

    /**
     * Sets the value of the equipKind property.
     * 
     */
    public void setEquipKind(int value) {
        this.equipKind = value;
    }

    /**
     * Gets the value of the model property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getModel() {
        return model;
    }

    /**
     * Sets the value of the model property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setModel(String value) {
        this.model = value;
    }

    /**
     * Gets the value of the transferType property.
     * 
     */
    public int getTransferType() {
        return transferType;
    }

    /**
     * Sets the value of the transferType property.
     * 
     */
    public void setTransferType(int value) {
        this.transferType = value;
    }

    /**
     * Gets the value of the otaStep property.
     * 
     */
    public int getOTAStep() {
        return otaStep;
    }

    /**
     * Sets the value of the otaStep property.
     * 
     */
    public void setOTAStep(int value) {
        this.otaStep = value;
    }

    /**
     * Gets the value of the multiWriteCount property.
     * 
     */
    public int getMultiWriteCount() {
        return multiWriteCount;
    }

    /**
     * Sets the value of the multiWriteCount property.
     * 
     */
    public void setMultiWriteCount(int value) {
        this.multiWriteCount = value;
    }

    /**
     * Gets the value of the maxRetryCount property.
     * 
     */
    public int getMaxRetryCount() {
        return maxRetryCount;
    }

    /**
     * Sets the value of the maxRetryCount property.
     * 
     */
    public void setMaxRetryCount(int value) {
        this.maxRetryCount = value;
    }

    /**
     * Gets the value of the otaThreadCount property.
     * 
     */
    public int getOTAThreadCount() {
        return otaThreadCount;
    }

    /**
     * Sets the value of the otaThreadCount property.
     * 
     */
    public void setOTAThreadCount(int value) {
        this.otaThreadCount = value;
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
     * Gets the value of the oldHwVersion property.
     * 
     */
    public int getOldHwVersion() {
        return oldHwVersion;
    }

    /**
     * Sets the value of the oldHwVersion property.
     * 
     */
    public void setOldHwVersion(int value) {
        this.oldHwVersion = value;
    }

    /**
     * Gets the value of the oldFwVersion property.
     * 
     */
    public int getOldFwVersion() {
        return oldFwVersion;
    }

    /**
     * Sets the value of the oldFwVersion property.
     * 
     */
    public void setOldFwVersion(int value) {
        this.oldFwVersion = value;
    }

    /**
     * Gets the value of the oldBuild property.
     * 
     */
    public int getOldBuild() {
        return oldBuild;
    }

    /**
     * Sets the value of the oldBuild property.
     * 
     */
    public void setOldBuild(int value) {
        this.oldBuild = value;
    }

    /**
     * Gets the value of the newHwVersion property.
     * 
     */
    public int getNewHwVersion() {
        return newHwVersion;
    }

    /**
     * Sets the value of the newHwVersion property.
     * 
     */
    public void setNewHwVersion(int value) {
        this.newHwVersion = value;
    }

    /**
     * Gets the value of the newFwVersion property.
     * 
     */
    public int getNewFwVersion() {
        return newFwVersion;
    }

    /**
     * Sets the value of the newFwVersion property.
     * 
     */
    public void setNewFwVersion(int value) {
        this.newFwVersion = value;
    }

    /**
     * Gets the value of the newBuild property.
     * 
     */
    public int getNewBuild() {
        return newBuild;
    }

    /**
     * Sets the value of the newBuild property.
     * 
     */
    public void setNewBuild(int value) {
        this.newBuild = value;
    }

    /**
     * Gets the value of the binaryURL property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBinaryURL() {
        return binaryURL;
    }

    /**
     * Sets the value of the binaryURL property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBinaryURL(String value) {
        this.binaryURL = value;
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
     * Gets the value of the diffURL property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDiffURL() {
        return diffURL;
    }

    /**
     * Sets the value of the diffURL property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDiffURL(String value) {
        this.diffURL = value;
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
     * Gets the value of the equipIdList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the equipIdList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEquipIdList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getEquipIdList() {
        if (equipIdList == null) {
            equipIdList = new ArrayList<Object>();
        }
        return this.equipIdList;
    }

}
