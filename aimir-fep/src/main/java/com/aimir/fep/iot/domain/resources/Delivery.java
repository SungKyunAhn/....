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


/**
 * delivery domain.
 * @author <ul>
 *         <li>Sang June Lee < blue4444eye@hanmail.net > < blue4444eye@gmail.com ></li>
 *         </ul>
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "stateTag",
    "source",
    "target",
    "lifespan",
    "eventCat",
    "deliveryMetaData",
    "aggregatedRequest",
    "childResource",
    "subscription"
})
@XmlRootElement(name = "dlv", namespace="http://www.onem2m.org/xml/protocols")
public class Delivery
    extends RegularResource
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@XmlElement(name = "st", required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger stateTag;
    @XmlElement(name = "sr", required = true)
    protected String source;
    @XmlElement(name = "tg", required = true)
    protected String target;
    @XmlElement(name = "ls", required = true)
    protected String lifespan;
    @XmlElement(name = "ec", required = true)
    protected String eventCat;
    @XmlElement(name = "dmd", required = true)
    protected DeliveryMetaData deliveryMetaData;
    @XmlElement(name = "arq", required = true)
    protected AggregatedRequest aggregatedRequest;
    @XmlElement(name = "ch")
    protected List<ChildResourceRef> childResource;
    @XmlElement(name = "sub", namespace = "http://www.onem2m.org/xml/protocols")
    protected List<Subscription> subscription;

    /**
     * Gets the value of the stateTag property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getStateTag() {
        return stateTag;
    }

    /**
     * Sets the value of the stateTag property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setStateTag(BigInteger value) {
        this.stateTag = value;
    }

    /**
     * Gets the value of the source property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSource() {
        return source;
    }

    /**
     * Sets the value of the source property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSource(String value) {
        this.source = value;
    }

    /**
     * Gets the value of the target property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTarget() {
        return target;
    }

    /**
     * Sets the value of the target property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTarget(String value) {
        this.target = value;
    }

    /**
     * Gets the value of the lifespan property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLifespan() {
        return lifespan;
    }

    /**
     * Sets the value of the lifespan property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLifespan(String value) {
        this.lifespan = value;
    }

    /**
     * Gets the value of the eventCat property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEventCat() {
        return eventCat;
    }

    /**
     * Sets the value of the eventCat property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEventCat(String value) {
        this.eventCat = value;
    }

    /**
     * Gets the value of the deliveryMetaData property.
     * 
     * @return
     *     possible object is
     *     {@link DeliveryMetaData }
     *     
     */
    public DeliveryMetaData getDeliveryMetaData() {
        return deliveryMetaData;
    }

    /**
     * Sets the value of the deliveryMetaData property.
     * 
     * @param value
     *     allowed object is
     *     {@link DeliveryMetaData }
     *     
     */
    public void setDeliveryMetaData(DeliveryMetaData value) {
        this.deliveryMetaData = value;
    }

    /**
     * Gets the value of the aggregatedRequest property.
     * 
     * @return
     *     possible object is
     *     {@link AggregatedRequest }
     *     
     */
    public AggregatedRequest getAggregatedRequest() {
        return aggregatedRequest;
    }

    /**
     * Sets the value of the aggregatedRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link AggregatedRequest }
     *     
     */
    public void setAggregatedRequest(AggregatedRequest value) {
        this.aggregatedRequest = value;
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
