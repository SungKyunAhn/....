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
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonRootName;


/**
 * container domain.
 * @author <ul>
 *         <li>Sang June Lee < blue4444eye@hanmail.net > < blue4444eye@gmail.com ></li>
 *         </ul>
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "stateTag",
    "creator",
    "maxNrOfInstances",
    "maxByteSize",
    "maxInstanceAge",
    "currentNrOfInstances",
    "currentByteSize",
    "locationID",
    "ontologyRef",
    "containerType",
    "heartbeatPeriod",
    "childResource",
    "contentInstance",
    "container",
    "subscription"
})
@JsonRootName("cnt")
@XmlRootElement(name = "cnt", namespace="http://www.onem2m.org/xml/protocols")
public class Container
    extends AnnounceableResource
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@XmlElement(name = "st", required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger stateTag;
    @XmlElement(name = "cr", required = true)
    protected String creator;
    @XmlElement(name = "mni")
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger maxNrOfInstances;
    @XmlElement(name = "mbs")
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger maxByteSize;
    @XmlElement(name = "mia")
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger maxInstanceAge;
    @XmlElement(name = "cni", required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger currentNrOfInstances;
    @XmlElement(name = "cbs", required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger currentByteSize;
    @XmlElement(name = "li")
    @XmlSchemaType(name = "anyURI")
    protected String locationID;
    @XmlElement(name = "or")
    @XmlSchemaType(name = "anyURI")
    protected String ontologyRef;
    @XmlTransient
    protected String latest;
    @XmlTransient
    protected String oldest;
    protected String containerType;
    protected String heartbeatPeriod;
    @XmlElement(name = "ch")
    protected List<ChildResourceRef> childResource;
    @XmlElement(name = "cin", namespace = "http://www.onem2m.org/xml/protocols")
    protected List<ContentInstance> contentInstance;
    @XmlElement(name = "cnt", namespace = "http://www.onem2m.org/xml/protocols")
    protected List<Container> container;
    @XmlElement(name = "sub", namespace = "http://www.onem2m.org/xml/protocols")
    protected List<Subscription> subscription;
    /*
    @XmlElements({
        @XmlElement(name = "contentInstance", namespace = "http://www.onem2m.org/xml/protocols", type = ContentInstance.class),
        @XmlElement(name = "container", namespace = "http://www.onem2m.org/xml/protocols", type = Container.class),
        @XmlElement(name = "subscription", namespace = "http://www.onem2m.org/xml/protocols", type = Subscription.class)
    })
    protected List<Resource> contentInstanceOrContainerOrSubscription;
    */

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
     * Gets the value of the maxNrOfInstances property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getMaxNrOfInstances() {
        return maxNrOfInstances;
    }

    /**
     * Sets the value of the maxNrOfInstances property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setMaxNrOfInstances(BigInteger value) {
        this.maxNrOfInstances = value;
    }

    /**
     * Gets the value of the maxByteSize property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getMaxByteSize() {
        return maxByteSize;
    }

    /**
     * Sets the value of the maxByteSize property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setMaxByteSize(BigInteger value) {
        this.maxByteSize = value;
    }

    /**
     * Gets the value of the maxInstanceAge property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getMaxInstanceAge() {
        return maxInstanceAge;
    }

    /**
     * Sets the value of the maxInstanceAge property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setMaxInstanceAge(BigInteger value) {
        this.maxInstanceAge = value;
    }

    /**
     * Gets the value of the currentNrOfInstances property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCurrentNrOfInstances() {
        return currentNrOfInstances;
    }

    /**
     * Sets the value of the currentNrOfInstances property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCurrentNrOfInstances(BigInteger value) {
        this.currentNrOfInstances = value;
    }

    /**
     * Gets the value of the currentByteSize property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCurrentByteSize() {
        return currentByteSize;
    }

    /**
     * Sets the value of the currentByteSize property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCurrentByteSize(BigInteger value) {
        this.currentByteSize = value;
    }

    /**
     * Gets the value of the locationID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocationID() {
        return locationID;
    }

    /**
     * Sets the value of the locationID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocationID(String value) {
        this.locationID = value;
    }

    /**
     * Gets the value of the ontologyRef property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOntologyRef() {
        return ontologyRef;
    }

    /**
     * Sets the value of the ontologyRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOntologyRef(String value) {
        this.ontologyRef = value;
    }

    /**
     * Gets the value of the latest property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLatest() {
        return latest;
    }

    /**
     * Sets the value of the latest property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLatest(String value) {
        this.latest = value;
    }

    /**
     * Gets the value of the oldest property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOldest() {
        return oldest;
    }

	public String getContainerType() {
		return containerType;
	}

	public void setContainerType(String containerType) {
		this.containerType = containerType;
	}

	public String getHeartbeatPeriod() {
		return heartbeatPeriod;
	}

	public void setHeartbeatPeriod(String heartbeatPeriod) {
		this.heartbeatPeriod = heartbeatPeriod;
	}

	/**
     * Sets the value of the oldest property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOldest(String value) {
        this.oldest = value;
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

    public List<ContentInstance> getContentInstance() {
        if (contentInstance == null) {
        	contentInstance = new ArrayList<ContentInstance>();
        }
        return this.contentInstance;
    }
    
    public List<Container> getContainer() {
        if (container == null) {
        	container = new ArrayList<Container>();
        }
        return this.container;
    }
    
    public List<Subscription> getSubscription() {
        if (subscription == null) {
        	subscription = new ArrayList<Subscription>();
        }
        return this.subscription;
    }
    
    /**
     * Gets the value of the contentInstanceOrContainerOrSubscription property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the contentInstanceOrContainerOrSubscription property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getContentInstanceOrContainerOrSubscription().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ContentInstance }
     * {@link Container }
     * {@link Subscription }
     * 
     * 
     */
    /*
    public List<Resource> getContentInstanceOrContainerOrSubscription() {
        if (contentInstanceOrContainerOrSubscription == null) {
            contentInstanceOrContainerOrSubscription = new ArrayList<Resource>();
        }
        return this.contentInstanceOrContainerOrSubscription;
    }
     */
}
