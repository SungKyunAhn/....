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

import com.fasterxml.jackson.annotation.JsonRootName;


/**
 * CSEBase domain.
 * @author <ul>
 *         <li>Sang June Lee < blue4444eye@hanmail.net > < blue4444eye@gmail.com ></li>
 *         </ul>
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "accessControlPolicyIDs",
    "cseType",
    "cseid",
    "supportedResourceType",
    "pointOfAccess",
    "nodeLink",
    "notificationCongestionPolicy",
    "childResource",
    "remoteCSE",
    "node",
    "AE",
    "container",
    "group",
    "accessControlPolicy",
    "subscription",
    "mgmtCmd",
    "locationPolicy",
    "statsConfig",
    "statsCollect",
    "request",
    "delivery",
    "schedule",
    "m2mServiceSubscriptionProfile",
    "serviceSubscribedAppRule"
})
@JsonRootName("csb")
@XmlRootElement(name = "csb", namespace="http://www.onem2m.org/xml/protocols")
public class CSEBase
    extends Resource
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@XmlList
	@XmlElement(name = "acpi")
    protected List<String> accessControlPolicyIDs;
	@XmlElement(name = "cst")
	protected BigInteger cseType;
    @XmlElement(name = "csi", required = true)
    protected String cseid;
    @XmlList
    @XmlElement(name = "srt", required = true)
    protected List<BigInteger> supportedResourceType;
    @XmlList
    @XmlElement(name = "poa", required = true)
    protected List<String> pointOfAccess;
    @XmlElement(name = "nl")
    @XmlSchemaType(name = "anyURI")
    protected String nodeLink;
    @XmlElement(name = "ncp")
    protected String notificationCongestionPolicy;
    @XmlElement(name = "ch")
    protected List<ChildResourceRef> childResource;
    @XmlElement(name = "csr", namespace = "http://www.onem2m.org/xml/protocols")
    protected List<RemoteCSE> remoteCSE;
    @XmlElement(name = "nod", namespace = "http://www.onem2m.org/xml/protocols")
    protected List<Node> node;
    @XmlElement(name = "ae", namespace = "http://www.onem2m.org/xml/protocols")
    protected List<AE> AE;
    @XmlElement(name = "cnt", namespace = "http://www.onem2m.org/xml/protocols")
    protected List<Container> container;
    @XmlElement(name = "grp", namespace = "http://www.onem2m.org/xml/protocols")
    protected List<Group> group;
    @XmlElement(name = "acp", namespace = "http://www.onem2m.org/xml/protocols")
    protected List<AccessControlPolicy> accessControlPolicy;
    @XmlElement(name = "sub", namespace = "http://www.onem2m.org/xml/protocols")
    protected List<Subscription> subscription;
    @XmlElement(name = "mgc", namespace = "http://www.onem2m.org/xml/protocols")
    protected List<MgmtCmd> mgmtCmd;
    @XmlElement(name = "lcp", namespace = "http://www.onem2m.org/xml/protocols")
    protected List<LocationPolicy> locationPolicy;
    @XmlElement(name = "stcg", namespace = "http://www.onem2m.org/xml/protocols")
    protected List<StatsConfig> statsConfig;
    @XmlElement(name = "stcl", namespace = "http://www.onem2m.org/xml/protocols")
    protected List<StatsCollect> statsCollect;
    @XmlElement(name = "req", namespace = "http://www.onem2m.org/xml/protocols")
    protected List<Request> request;
    @XmlElement(name = "dlv", namespace = "http://www.onem2m.org/xml/protocols")
    protected List<Delivery> delivery;
    @XmlElement(name = "sch", namespace = "http://www.onem2m.org/xml/protocols")
    protected List<Schedule> schedule;
    @XmlElement(name = "mssp", namespace = "http://www.onem2m.org/xml/protocols")
    protected List<M2MServiceSubscriptionProfile> m2mServiceSubscriptionProfile;
    @XmlElement(name = "asar", namespace = "http://www.onem2m.org/xml/protocols")
    protected List<ServiceSubscribedAppRule> serviceSubscribedAppRule;
    
    /*@XmlElements({
        @XmlElement(name = "statsCollect", namespace = "http://www.onem2m.org/xml/protocols", type = StatsCollect.class),
        @XmlElement(name = "node", namespace = "http://www.onem2m.org/xml/protocols", type = Node.class),
        @XmlElement(name = "locationPolicy", namespace = "http://www.onem2m.org/xml/protocols", type = LocationPolicy.class),
        @XmlElement(name = "statsConfig", namespace = "http://www.onem2m.org/xml/protocols", type = StatsConfig.class),
        @XmlElement(name = "mgmtCmd", namespace = "http://www.onem2m.org/xml/protocols", type = MgmtCmd.class),
        @XmlElement(name = "delivery", namespace = "http://www.onem2m.org/xml/protocols", type = Delivery.class),
        @XmlElement(name = "m2mServiceSubscriptionProfile", namespace = "http://www.onem2m.org/xml/protocols", type = M2MServiceSubscriptionProfile.class),
        @XmlElement(name = "subscription", namespace = "http://www.onem2m.org/xml/protocols", type = Subscription.class),
        @XmlElement(name = "schedule", namespace = "http://www.onem2m.org/xml/protocols", type = Schedule.class),
        @XmlElement(name = "AE", namespace = "http://www.onem2m.org/xml/protocols", type = AE.class),
        @XmlElement(name = "remoteCSE", namespace = "http://www.onem2m.org/xml/protocols", type = RemoteCSE.class),
        @XmlElement(name = "request", namespace = "http://www.onem2m.org/xml/protocols", type = Request.class),
        @XmlElement(name = "container", namespace = "http://www.onem2m.org/xml/protocols", type = Container.class),
        @XmlElement(name = "accessControlPolicy", namespace = "http://www.onem2m.org/xml/protocols", type = AccessControlPolicy.class),
        @XmlElement(name = "serviceSubscribedAppRule", namespace = "http://www.onem2m.org/xml/protocols", type = ServiceSubscribedAppRule.class),
        @XmlElement(name = "group", namespace = "http://www.onem2m.org/xml/protocols", type = Group.class)
    })
    protected List<Resource> remoteCSEOrNodeOrAE;
    */
    
    /**
     * Gets the value of the accessControlPolicyIDs property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the accessControlPolicyIDs property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAccessControlPolicyIDs().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getAccessControlPolicyIDs() {
        if (accessControlPolicyIDs == null) {
            accessControlPolicyIDs = new ArrayList<String>();
        }
        return this.accessControlPolicyIDs;
    }

    /**
     * Gets the value of the cseType property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCseType() {
        return cseType;
    }

    /**
     * Sets the value of the cseType property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCseType(BigInteger value) {
        this.cseType = value;
    }

    /**
     * Gets the value of the cseid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCSEID() {
        return cseid;
    }

    /**
     * Sets the value of the cseid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCSEID(String value) {
        this.cseid = value;
    }

    /**
     * Gets the value of the supportedResourceType property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the supportedResourceType property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSupportedResourceType().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BigInteger }
     * 
     * 
     */
    public List<BigInteger> getSupportedResourceType() {
        if (supportedResourceType == null) {
            supportedResourceType = new ArrayList<BigInteger>();
        }
        return this.supportedResourceType;
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

    /**
     * Gets the value of the notificationCongestionPolicy property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNotificationCongestionPolicy() {
        return notificationCongestionPolicy;
    }

    /**
     * Sets the value of the notificationCongestionPolicy property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNotificationCongestionPolicy(String value) {
        this.notificationCongestionPolicy = value;
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
    
    public List<RemoteCSE> getRemoteCSE() {
        if (remoteCSE == null) {
            remoteCSE = new ArrayList<RemoteCSE>();
        }
        return this.remoteCSE;
    }

    public List<Node> getNode() {
        if (node == null) {
        	node = new ArrayList<Node>();
        }
        return this.node;
    }
    
    public List<AE> getAE() {
        if (AE == null) {
        	AE = new ArrayList<AE>();
        }
        return this.AE;
    }
    
    public List<Container> getContainer() {
        if (container == null) {
        	container = new ArrayList<Container>();
        }
        return this.container;
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
    
    public List<Subscription> getSubscription() {
        if (subscription == null) {
        	subscription = new ArrayList<Subscription>();
        }
        return this.subscription;
    }
    
    public List<MgmtCmd> getMgmtCmd() {
        if (mgmtCmd == null) {
        	mgmtCmd = new ArrayList<MgmtCmd>();
        }
        return this.mgmtCmd;
    }
    
    public List<LocationPolicy> getLocationPolicy() {
        if (locationPolicy == null) {
        	locationPolicy = new ArrayList<LocationPolicy>();
        }
        return this.locationPolicy;
    }
    
    public List<StatsConfig> getStatsConfig() {
        if (statsConfig == null) {
        	statsConfig = new ArrayList<StatsConfig>();
        }
        return this.statsConfig;
    }
    
    public List<StatsCollect> getStatsCollect() {
        if (statsCollect == null) {
        	statsCollect = new ArrayList<StatsCollect>();
        }
        return this.statsCollect;
    }
    
    public List<Request> getRequest() {
        if (request == null) {
        	request = new ArrayList<Request>();
        }
        return this.request;
    }
    
    public List<Delivery> getDelivery() {
        if (delivery == null) {
        	delivery = new ArrayList<Delivery>();
        }
        return this.delivery;
    }
    
    public List<Schedule> getSchedule() {
        if (schedule == null) {
        	schedule = new ArrayList<Schedule>();
        }
        return this.schedule;
    }
    
    public List<M2MServiceSubscriptionProfile> getM2MServiceSubscriptionProfile() {
        if (m2mServiceSubscriptionProfile == null) {
        	m2mServiceSubscriptionProfile = new ArrayList<M2MServiceSubscriptionProfile>();
        }
        return this.m2mServiceSubscriptionProfile;
    }
    
    public List<ServiceSubscribedAppRule> getServiceSubscribedAppRule() {
        if (serviceSubscribedAppRule == null) {
        	serviceSubscribedAppRule = new ArrayList<ServiceSubscribedAppRule>();
        }
        return this.serviceSubscribedAppRule;
    }

    /**
     * Gets the value of the remoteCSEOrNodeOrAE property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the remoteCSEOrNodeOrAE property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRemoteCSEOrNodeOrAE().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link StatsCollect }
     * {@link Node }
     * {@link LocationPolicy }
     * {@link StatsConfig }
     * {@link MgmtCmd }
     * {@link Delivery }
     * {@link M2MServiceSubscriptionProfile }
     * {@link Subscription }
     * {@link Schedule }
     * {@link AE }
     * {@link RemoteCSE }
     * {@link Request }
     * {@link Container }
     * {@link AccessControlPolicy }
     * {@link ServiceSubscribedAppRule }
     * {@link Group }
     * 
     * 
     */
    /*
    public List<Resource> getRemoteCSEOrNodeOrAE() {
        if (remoteCSEOrNodeOrAE == null) {
            remoteCSEOrNodeOrAE = new ArrayList<Resource>();
        }
        return this.remoteCSEOrNodeOrAE;
    }
    */

}
