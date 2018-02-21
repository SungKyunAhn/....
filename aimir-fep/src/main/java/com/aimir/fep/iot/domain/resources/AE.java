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
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonRootName;


/**
 * AE domain.
 * @author <ul>
 *         <li>Sang June Lee < blue4444eye@hanmail.net > < blue4444eye@gmail.com ></li>
 *         </ul>
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "appName",
    "appID",
    "aeID",
    "pointOfAccess",
    "ontologyRef",
    "nodeLink",
    "childResource",
    "container",
    "subscription",
    "group",
    "accessControlPolicy",
    "pollingChannel"
})
@JsonRootName("ae")
@XmlRootElement(name = "ae", namespace="http://www.onem2m.org/xml/protocols")
public class AE
    extends AnnounceableResource
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@XmlElement(name = "apn")
	protected String appName;
    @XmlElement(name = "api", required = true)
    protected String appID;
    @XmlElement(name = "aei", required = true)
    protected String aeID;
    @XmlElement(name = "poa")
    @XmlList
    protected List<String> pointOfAccess;
    @XmlElement(name = "or")
    @XmlSchemaType(name = "anyURI")
    protected String ontologyRef;
    @XmlElement(name = "nl")
    @XmlSchemaType(name = "anyURI")
    protected String nodeLink;
    @XmlTransient
    protected String aKey;
    @XmlTransient
    protected String passCode;
    @XmlElement(name = "ch")
    protected List<ChildResourceRef> childResource;
    @XmlElement(name = "cnt", namespace = "http://www.onem2m.org/xml/protocols")
    protected List<Container> container;
    @XmlElement(name = "sub", namespace = "http://www.onem2m.org/xml/protocols")
    protected List<Subscription> subscription;
    @XmlElement(name = "grp", namespace = "http://www.onem2m.org/xml/protocols")
    protected List<Group> group;
    @XmlElement(name = "acp", namespace = "http://www.onem2m.org/xml/protocols")
    protected List<AccessControlPolicy> accessControlPolicy;
    @XmlElement(name = "pch", namespace = "http://www.onem2m.org/xml/protocols")
    protected List<PollingChannel> pollingChannel;
    /*
    @XmlElements({
        @XmlElement(name = "container", namespace = "http://www.onem2m.org/xml/protocols", type = Container.class),
        @XmlElement(name = "subscription", namespace = "http://www.onem2m.org/xml/protocols", type = Subscription.class),
        @XmlElement(name = "group", namespace = "http://www.onem2m.org/xml/protocols", type = Group.class),
        @XmlElement(name = "accessControlPolicy", namespace = "http://www.onem2m.org/xml/protocols", type = AccessControlPolicy.class),
        @XmlElement(name = "pollingChannel", namespace = "http://www.onem2m.org/xml/protocols", type = PollingChannel.class)
    })
    protected List<Resource> containerOrGroupOrAccessControlPolicy;
    */

    /**
     * Gets the value of the appName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAppName() {
        return appName;
    }

    /**
     * Sets the value of the appName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAppName(String value) {
        this.appName = value;
    }

    /**
     * Gets the value of the appID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAppID() {
        return appID;
    }

    /**
     * Sets the value of the appID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAppID(String value) {
        this.appID = value;
    }

    /**
     * Gets the value of the aeid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAEID() {
        return aeID;
    }

    /**
     * Sets the value of the aeid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAEID(String value) {
        this.aeID = value;
    }

    /**
     * Gets the value of the pointOfAccess property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the pointOfAccess property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPointOfAccess().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getPointOfAccess() {
        if (pointOfAccess == null) {
            pointOfAccess = new ArrayList<String>();
        }
        return this.pointOfAccess;
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
     * Gets the value of the nodeLink property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNodeLink() {
        return nodeLink;
    }

    /**
     * Sets the value of the nodeLink property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNodeLink(String value) {
        this.nodeLink = value;
    }

	public String getAKey() {
		return aKey;
	}

	public void setAKey(String aKey) {
		this.aKey = aKey;
	}

	public String getPassCode() {
		return passCode;
	}

	public void setPassCode(String passCode) {
		this.passCode = passCode;
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
    
    public List<Group> getGroup() {
        if (group == null) {
        	group = new ArrayList<Group>();
        }
        return this.group;
    }
    
    public List<AccessControlPolicy> getAccessControlPolicy() {
        if (accessControlPolicy == null) {
        	accessControlPolicy = new ArrayList<AccessControlPolicy>();
        }
        return this.accessControlPolicy;
    }
    
    public List<PollingChannel> getPollingChannel() {
        if (pollingChannel == null) {
        	pollingChannel = new ArrayList<PollingChannel>();
        }
        return this.pollingChannel;
    }

    /**
     * Gets the value of the containerOrGroupOrAccessControlPolicy property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the containerOrGroupOrAccessControlPolicy property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getContainerOrGroupOrAccessControlPolicy().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Container }
     * {@link Subscription }
     * {@link Group }
     * {@link AccessControlPolicy }
     * {@link PollingChannel }
     * 
     * 
     */
    /*
    public List<Resource> getContainerOrGroupOrAccessControlPolicy() {
        if (containerOrGroupOrAccessControlPolicy == null) {
            containerOrGroupOrAccessControlPolicy = new ArrayList<Resource>();
        }
        return this.containerOrGroupOrAccessControlPolicy;
    }
     */
}
