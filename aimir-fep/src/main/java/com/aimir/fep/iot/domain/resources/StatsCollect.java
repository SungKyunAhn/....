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
import javax.xml.bind.annotation.XmlType;


/**
 * statsCollect domain.
 * @author <ul>
 *         <li>Sang June Lee < blue4444eye@hanmail.net > < blue4444eye@gmail.com ></li>
 *         </ul>
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "creator",
    "statsCollectID",
    "collectingEntityID",
    "collectedEntityID",
    "statsRuleStatus",
    "statModel",
    "collectPeriod",
    "eventID",
    "childResource",
    "subscription"
})
@XmlRootElement(name = "stcl", namespace="http://www.onem2m.org/xml/protocols")
public class StatsCollect
    extends RegularResource
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@XmlElement(name = "cr", required = true)
    protected String creator;
    @XmlElement(name = "sci", required = true)
    protected String statsCollectID;
    @XmlElement(name = "cei", required = true)
    protected String collectingEntityID;
    @XmlElement(name = "cdi", required = true)
    protected String collectedEntityID;
    @XmlElement(name = "srs", required = true)
    protected BigInteger statsRuleStatus;
    @XmlElement(name = "sm", required = true)
    protected BigInteger statModel;
    @XmlElement(name = "cp")
    protected ScheduleEntries collectPeriod;
    @XmlElement(name = "evi")
    protected String eventID;
    @XmlElement(name = "ch")
    protected List<ChildResourceRef> childResource;
    @XmlElement(name = "sub", namespace = "http://www.onem2m.org/xml/protocols")
    protected List<Subscription> subscription;

    /**
     * Gets the value of the creator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreator() {
        return creator;
    }

    /**
     * Sets the value of the creator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreator(String value) {
        this.creator = value;
    }

    /**
     * Gets the value of the statsCollectID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatsCollectID() {
        return statsCollectID;
    }

    /**
     * Sets the value of the statsCollectID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatsCollectID(String value) {
        this.statsCollectID = value;
    }

    /**
     * Gets the value of the collectingEntityID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCollectingEntityID() {
        return collectingEntityID;
    }

    /**
     * Sets the value of the collectingEntityID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCollectingEntityID(String value) {
        this.collectingEntityID = value;
    }

    /**
     * Gets the value of the collectedEntityID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCollectedEntityID() {
        return collectedEntityID;
    }

    /**
     * Sets the value of the collectedEntityID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCollectedEntityID(String value) {
        this.collectedEntityID = value;
    }

    /**
     * Gets the value of the statsRuleStatus property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getStatsRuleStatus() {
        return statsRuleStatus;
    }

    /**
     * Sets the value of the statsRuleStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setStatsRuleStatus(BigInteger value) {
        this.statsRuleStatus = value;
    }

    /**
     * Gets the value of the statModel property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getStatModel() {
        return statModel;
    }

    /**
     * Sets the value of the statModel property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setStatModel(BigInteger value) {
        this.statModel = value;
    }

    /**
     * Gets the value of the collectPeriod property.
     * 
     * @return
     *     possible object is
     *     {@link ScheduleEntries }
     *     
     */
    public ScheduleEntries getCollectPeriod() {
        return collectPeriod;
    }

    /**
     * Sets the value of the collectPeriod property.
     * 
     * @param value
     *     allowed object is
     *     {@link ScheduleEntries }
     *     
     */
    public void setCollectPeriod(ScheduleEntries value) {
        this.collectPeriod = value;
    }

    /**
     * Gets the value of the eventID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEventID() {
        return eventID;
    }

    /**
     * Sets the value of the eventID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEventID(String value) {
        this.eventID = value;
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
