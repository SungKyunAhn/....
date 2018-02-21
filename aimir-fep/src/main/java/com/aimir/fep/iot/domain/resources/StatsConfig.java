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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * statsConfig domain.
 * @author <ul>
 *         <li>Sang June Lee < blue4444eye@hanmail.net > < blue4444eye@gmail.com ></li>
 *         </ul>
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "creator",
    "childResource",
    "eventConfig",
    "subscription"
})
@XmlRootElement(name = "stcg", namespace="http://www.onem2m.org/xml/protocols")
public class StatsConfig
    extends RegularResource
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@XmlElement(name = "cr", required = true)
    protected String creator;
	@XmlElement(name = "ch")
    protected List<ChildResourceRef> childResource;
    @XmlElement(name = "evcg", namespace = "http://www.onem2m.org/xml/protocols")
    protected List<EventConfig> eventConfig;
    @XmlElement(name = "sub", namespace = "http://www.onem2m.org/xml/protocols")
    protected List<Subscription> subscription;
    /*
    @XmlElements({
        @XmlElement(name = "eventConfig", namespace = "http://www.onem2m.org/xml/protocols", type = EventConfig.class),
        @XmlElement(name = "subscription", namespace = "http://www.onem2m.org/xml/protocols", type = Subscription.class)
    })
    protected List<RegularResource> eventConfigOrSubscription;
    */

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
    
    public List<EventConfig> getEventConfig() {
        if (eventConfig == null) {
        	eventConfig = new ArrayList<EventConfig>();
        }
        return this.eventConfig;
    }
    
    public List<Subscription> getSubscription() {
        if (subscription == null) {
        	subscription = new ArrayList<Subscription>();
        }
        return this.subscription;
    }

    /**
     * Gets the value of the eventConfigOrSubscription property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the eventConfigOrSubscription property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEventConfigOrSubscription().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EventConfig }
     * {@link Subscription }
     * 
     * 
     */
    /*
    public List<RegularResource> getEventConfigOrSubscription() {
        if (eventConfigOrSubscription == null) {
            eventConfigOrSubscription = new ArrayList<RegularResource>();
        }
        return this.eventConfigOrSubscription;
    }
    */

}
