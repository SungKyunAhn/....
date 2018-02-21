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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.Duration;


/**
 * locationPolicy domain.
 * @author <ul>
 *         <li>Sang June Lee < blue4444eye@hanmail.net > < blue4444eye@gmail.com ></li>
 *         </ul>
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "locationSource",
    "locationUpdatePeriod",
    "locationTargetID",
    "locationServer",
    "locationContainerID",
    "locationContainerName",
    "locationStatus",
    "childResource",
    "subscription"
})
@XmlRootElement(name = "lcp", namespace="http://www.onem2m.org/xml/protocols")
public class LocationPolicy
    extends AnnounceableResource
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@XmlElement(name = "los", required = true)
    protected BigInteger locationSource;
	@XmlElement(name = "lou")
    protected Duration locationUpdatePeriod;
	@XmlElement(name = "lot")
	@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String locationTargetID;
	@XmlElement(name = "lor")
	@XmlSchemaType(name = "anyURI")
    protected String locationServer;
	@XmlElement(name = "loi")
	@XmlSchemaType(name = "anyURI")
    protected String locationContainerID;
	@XmlElement(name = "lon")
    protected String locationContainerName;
    @XmlElement(name = "lost", required = true)
    protected BigInteger locationStatus;
    @XmlElement(name = "ch")
    protected List<ChildResourceRef> childResource;
    @XmlElement(name = "sub", namespace = "http://www.onem2m.org/xml/protocols")
    protected List<Subscription> subscription;

    /**
     * Gets the value of the locationSource property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getLocationSource() {
        return locationSource;
    }

    /**
     * Sets the value of the locationSource property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setLocationSource(BigInteger value) {
        this.locationSource = value;
    }

    /**
     * Gets the value of the locationUpdatePeriod property.
     * 
     * @return
     *     possible object is
     *     {@link Duration }
     *     
     */
    public Duration getLocationUpdatePeriod() {
        return locationUpdatePeriod;
    }

    /**
     * Sets the value of the locationUpdatePeriod property.
     * 
     * @param value
     *     allowed object is
     *     {@link Duration }
     *     
     */
    public void setLocationUpdatePeriod(Duration value) {
        this.locationUpdatePeriod = value;
    }

    /**
     * Gets the value of the locationTargetID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocationTargetID() {
        return locationTargetID;
    }

    /**
     * Sets the value of the locationTargetID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocationTargetID(String value) {
        this.locationTargetID = value;
    }

    /**
     * Gets the value of the locationServer property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocationServer() {
        return locationServer;
    }

    /**
     * Sets the value of the locationServer property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocationServer(String value) {
        this.locationServer = value;
    }

    /**
     * Gets the value of the locationContainerID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocationContainerID() {
        return locationContainerID;
    }

    /**
     * Sets the value of the locationContainerID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocationContainerID(String value) {
        this.locationContainerID = value;
    }

    /**
     * Gets the value of the locationContainerName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocationContainerName() {
        return locationContainerName;
    }

    /**
     * Sets the value of the locationContainerName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocationContainerName(String value) {
        this.locationContainerName = value;
    }

    /**
     * Gets the value of the locationStatus property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getLocationStatus() {
        return locationStatus;
    }

    /**
     * Sets the value of the locationStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setLocationStatus(BigInteger value) {
        this.locationStatus = value;
    }

	/**
     * Gets the value of the childResource property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the childResource property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getChildResource().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ChildResourceRef }
     * 
     * 
     */
    public List<ChildResourceRef> getChildResource() {
        if (childResource == null) {
            childResource = new ArrayList<ChildResourceRef>();
        }
        return this.childResource;
    }

    /**
     * Gets the value of the subscription property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the subscription property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSubscription().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Subscription }
     * 
     * 
     */
    public List<Subscription> getSubscription() {
        if (subscription == null) {
            subscription = new ArrayList<Subscription>();
        }
        return this.subscription;
    }

}
