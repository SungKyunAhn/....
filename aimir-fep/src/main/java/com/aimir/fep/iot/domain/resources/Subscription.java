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
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * subscription domain.
 * @author <ul>
 *         <li>Sang June Lee < blue4444eye@hanmail.net > < blue4444eye@gmail.com ></li>
 *         </ul>
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "eventNotificationCriteria",
    "expirationCounter",
    "notificationURI",
    "groupID",
    "notificationForwardingURI",
    "batchNotify",
    "rateLimit",
    "preSubscriptionNotify",
    "pendingNotification",
    "notificationStoragePriority",
    "latestNotify",
    "notificationContentType",
    "notificationEventCat",
    "creator",
    "subscriberURI",
    "childResource",
    "schedule"
})
@XmlRootElement(name = "sub", namespace="http://www.onem2m.org/xml/protocols")
public class Subscription
    extends RegularResource
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@XmlElement(name = "enc")
	protected EventNotificationCriteria eventNotificationCriteria;
	@XmlElement(name = "exc")
	@XmlSchemaType(name = "positiveInteger")
    protected BigInteger expirationCounter;
    @XmlList
    @XmlElement(name = "nu", required = true)
    protected List<String> notificationURI;
    @XmlElement(name = "gid")
    @XmlSchemaType(name = "anyURI")
    protected String groupID;
    @XmlElement(name = "nfu")
    @XmlSchemaType(name = "anyURI")
    protected String notificationForwardingURI;
    @XmlElement(name = "bn")
    protected BatchNotify batchNotify;
    @XmlElement(name = "rl")
    protected RateLimit rateLimit;
    @XmlElement(name = "psn")
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger preSubscriptionNotify;
    @XmlElement(name = "pn")
    protected BigInteger pendingNotification;
    @XmlElement(name = "nsp")
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger notificationStoragePriority;
    @XmlElement(name = "ln")
    protected Boolean latestNotify;
    @XmlElement(name = "nct", required = true)
    protected BigInteger notificationContentType;
    @XmlElement(name = "nec")
    protected String notificationEventCat;
    @XmlElement(name = "cr")
    protected String creator;
    @XmlElement(name = "su")
    @XmlSchemaType(name = "anyURI")
    protected String subscriberURI;
    @XmlElement(name = "ch")
    protected ChildResourceRef childResource;
    @XmlElement(name = "sch", namespace = "http://www.onem2m.org/xml/protocols")
    protected Schedule schedule;

    /**
     * Gets the value of the eventNotificationCriteria property.
     * 
     * @return
     *     possible object is
     *     {@link EventNotificationCriteria }
     *     
     */
    public EventNotificationCriteria getEventNotificationCriteria() {
        return eventNotificationCriteria;
    }

    /**
     * Sets the value of the eventNotificationCriteria property.
     * 
     * @param value
     *     allowed object is
     *     {@link EventNotificationCriteria }
     *     
     */
    public void setEventNotificationCriteria(EventNotificationCriteria value) {
        this.eventNotificationCriteria = value;
    }

    /**
     * Gets the value of the expirationCounter property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getExpirationCounter() {
        return expirationCounter;
    }

    /**
     * Sets the value of the expirationCounter property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setExpirationCounter(BigInteger value) {
        this.expirationCounter = value;
    }

    /**
     * Gets the value of the notificationURI property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the notificationURI property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNotificationURI().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getNotificationURI() {
        if (notificationURI == null) {
            notificationURI = new ArrayList<String>();
        }
        return this.notificationURI;
    }

    /**
     * Gets the value of the groupID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGroupID() {
        return groupID;
    }

    /**
     * Sets the value of the groupID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGroupID(String value) {
        this.groupID = value;
    }

    /**
     * Gets the value of the notificationForwardingURI property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNotificationForwardingURI() {
        return notificationForwardingURI;
    }

    /**
     * Sets the value of the notificationForwardingURI property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNotificationForwardingURI(String value) {
        this.notificationForwardingURI = value;
    }

    /**
     * Gets the value of the batchNotify property.
     * 
     * @return
     *     possible object is
     *     {@link BatchNotify }
     *     
     */
    public BatchNotify getBatchNotify() {
        return batchNotify;
    }

    /**
     * Sets the value of the batchNotify property.
     * 
     * @param value
     *     allowed object is
     *     {@link BatchNotify }
     *     
     */
    public void setBatchNotify(BatchNotify value) {
        this.batchNotify = value;
    }

    /**
     * Gets the value of the rateLimit property.
     * 
     * @return
     *     possible object is
     *     {@link RateLimit }
     *     
     */
    public RateLimit getRateLimit() {
        return rateLimit;
    }

    /**
     * Sets the value of the rateLimit property.
     * 
     * @param value
     *     allowed object is
     *     {@link RateLimit }
     *     
     */
    public void setRateLimit(RateLimit value) {
        this.rateLimit = value;
    }

    /**
     * Gets the value of the preSubscriptionNotify property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getPreSubscriptionNotify() {
        return preSubscriptionNotify;
    }

    /**
     * Sets the value of the preSubscriptionNotify property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setPreSubscriptionNotify(BigInteger value) {
        this.preSubscriptionNotify = value;
    }

    /**
     * Gets the value of the pendingNotification property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getPendingNotification() {
        return pendingNotification;
    }

    /**
     * Sets the value of the pendingNotification property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setPendingNotification(BigInteger value) {
        this.pendingNotification = value;
    }

    /**
     * Gets the value of the notificationStoragePriority property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getNotificationStoragePriority() {
        return notificationStoragePriority;
    }

    /**
     * Sets the value of the notificationStoragePriority property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setNotificationStoragePriority(BigInteger value) {
        this.notificationStoragePriority = value;
    }

    /**
     * Gets the value of the latestNotify property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isLatestNotify() {
        return latestNotify;
    }

    /**
     * Sets the value of the latestNotify property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setLatestNotify(Boolean value) {
        this.latestNotify = value;
    }

    /**
     * Gets the value of the notificationContentType property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getNotificationContentType() {
        return notificationContentType;
    }

    /**
     * Sets the value of the notificationContentType property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setNotificationContentType(BigInteger value) {
        this.notificationContentType = value;
    }

    /**
     * Gets the value of the notificationEventCat property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNotificationEventCat() {
        return notificationEventCat;
    }

    /**
     * Sets the value of the notificationEventCat property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNotificationEventCat(String value) {
        this.notificationEventCat = value;
    }

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
     * Gets the value of the subscriberURI property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubscriberURI() {
        return subscriberURI;
    }

    /**
     * Sets the value of the subscriberURI property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubscriberURI(String value) {
        this.subscriberURI = value;
    }

	/**
     * Gets the value of the childResource property.
     * 
     * @return
     *     possible object is
     *     {@link ChildResourceRef }
     *     
     */
    public ChildResourceRef getChildResource() {
        return childResource;
    }

    /**
     * Sets the value of the childResource property.
     * 
     * @param value
     *     allowed object is
     *     {@link ChildResourceRef }
     *     
     */
    public void setChildResource(ChildResourceRef value) {
        this.childResource = value;
    }

    /**
     * Gets the value of the schedule property.
     * 
     * @return
     *     possible object is
     *     {@link Schedule }
     *     
     */
    public Schedule getSchedule() {
        return schedule;
    }

    /**
     * Sets the value of the schedule property.
     * 
     * @param value
     *     allowed object is
     *     {@link Schedule }
     *     
     */
    public void setSchedule(Schedule value) {
        this.schedule = value;
    }

}
