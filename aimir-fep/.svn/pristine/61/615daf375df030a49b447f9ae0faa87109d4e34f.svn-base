/**
 * Copyright (c) 2015, Sang June Lee < blue4444eye@hanmail.net > < blue4444eye@gmail.com >.
   All rights reserved.

   Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
   1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
   2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
   3. The name of the author may not be used to endorse or promote products derived from this software without specific prior written permission.
   
   THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


package com.aimir.fep.iot.domain.resources;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * eventNotificationCriteria domain.
 * @author <ul>
 *         <li>Sang June Lee < blue4444eye@hanmail.net > < blue4444eye@gmail.com ></li>
 *         </ul>
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "eventNotificationCriteria", propOrder = {
    "createdBefore",
    "createdAfter",
    "modifiedSince",
    "unmodifiedSince",
    "stateTagSmaller",
    "stateTagBigger",
    "expireBefore",
    "expireAfter",
    "sizeAbove",
    "sizeBelow",
    "resourceStatus",
    "operationMonitor",
    "attribute"
})
public class EventNotificationCriteria {

	@XmlElement(name = "crb")
    protected String createdBefore;
	@XmlElement(name = "cra")
    protected String createdAfter;
	@XmlElement(name = "ms")
    protected String modifiedSince;
	@XmlElement(name = "us")
    protected String unmodifiedSince;
	@XmlElement(name = "sts")
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger stateTagSmaller;
	@XmlElement(name = "stb")
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger stateTagBigger;
	@XmlElement(name = "exb")
    protected String expireBefore;
	@XmlElement(name = "exa")
    protected String expireAfter;
	@XmlElement(name = "sza")
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger sizeAbove;
	@XmlElement(name = "szb")
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger sizeBelow;
	@XmlElement(name = "rss")
    protected List<BigInteger> resourceStatus;
	@XmlElement(name = "om")
    protected List<BigInteger> operationMonitor;
	@XmlElement(name = "atr")
    protected List<Attribute> attribute;

    /**
     * Gets the value of the createdBefore property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreatedBefore() {
        return createdBefore;
    }

    /**
     * Sets the value of the createdBefore property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreatedBefore(String value) {
        this.createdBefore = value;
    }

    /**
     * Gets the value of the createdAfter property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreatedAfter() {
        return createdAfter;
    }

    /**
     * Sets the value of the createdAfter property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreatedAfter(String value) {
        this.createdAfter = value;
    }

    /**
     * Gets the value of the modifiedSince property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getModifiedSince() {
        return modifiedSince;
    }

    /**
     * Sets the value of the modifiedSince property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setModifiedSince(String value) {
        this.modifiedSince = value;
    }

    /**
     * Gets the value of the unmodifiedSince property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUnmodifiedSince() {
        return unmodifiedSince;
    }

    /**
     * Sets the value of the unmodifiedSince property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUnmodifiedSince(String value) {
        this.unmodifiedSince = value;
    }

    /**
     * Gets the value of the stateTagSmaller property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getStateTagSmaller() {
        return stateTagSmaller;
    }

    /**
     * Sets the value of the stateTagSmaller property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setStateTagSmaller(BigInteger value) {
        this.stateTagSmaller = value;
    }

    /**
     * Gets the value of the stateTagBigger property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getStateTagBigger() {
        return stateTagBigger;
    }

    /**
     * Sets the value of the stateTagBigger property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setStateTagBigger(BigInteger value) {
        this.stateTagBigger = value;
    }

    /**
     * Gets the value of the expireBefore property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExpireBefore() {
        return expireBefore;
    }

    /**
     * Sets the value of the expireBefore property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExpireBefore(String value) {
        this.expireBefore = value;
    }

    /**
     * Gets the value of the expireAfter property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExpireAfter() {
        return expireAfter;
    }

    /**
     * Sets the value of the expireAfter property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExpireAfter(String value) {
        this.expireAfter = value;
    }

    /**
     * Gets the value of the sizeAbove property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSizeAbove() {
        return sizeAbove;
    }

    /**
     * Sets the value of the sizeAbove property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSizeAbove(BigInteger value) {
        this.sizeAbove = value;
    }

    /**
     * Gets the value of the sizeBelow property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSizeBelow() {
        return sizeBelow;
    }

    /**
     * Sets the value of the sizeBelow property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSizeBelow(BigInteger value) {
        this.sizeBelow = value;
    }

    /**
     * Gets the value of the resourceStatus property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the resourceStatus property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getResourceStatus().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BigInteger }
     * 
     * 
     */
    public List<BigInteger> getResourceStatus() {
        if (resourceStatus == null) {
            resourceStatus = new ArrayList<BigInteger>();
        }
        return this.resourceStatus;
    }

    /**
     * Gets the value of the operationMonitor property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the operationMonitor property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOperationMonitor().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BigInteger }
     * 
     * 
     */
    public List<BigInteger> getOperationMonitor() {
        if (operationMonitor == null) {
            operationMonitor = new ArrayList<BigInteger>();
        }
        return this.operationMonitor;
    }

    /**
     * Gets the value of the attribute property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the attribute property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAttribute().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Attribute }
     * 
     * 
     */
    public List<Attribute> getAttribute() {
        if (attribute == null) {
            attribute = new ArrayList<Attribute>();
        }
        return this.attribute;
    }

}
