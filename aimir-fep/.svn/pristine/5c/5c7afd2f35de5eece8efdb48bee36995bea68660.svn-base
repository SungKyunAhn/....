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
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonRootName;


/**
 * remoteCSE domain.
 * @author <ul>
 *         <li>Sang June Lee < blue4444eye@hanmail.net > < blue4444eye@gmail.com ></li>
 *         </ul>
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "cseType",
    "pointOfAccess",
    "cseBase",
    "cseid",
    "m2MExtID",
    "triggerRecipientID",
    "requestReachability",
    "nodeLink",
    "childResource",
    "schedule",
    "pollingChannel",
    "AE",
    "subscription",
    "container",
    "mgmtCmd",
    "accessControlPolicy",
    "group"
})
@JsonRootName("csr")
@XmlRootElement(name = "csr", namespace="http://www.onem2m.org/xml/protocols")
public class RemoteCSE
    extends AnnounceableResource
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@XmlElement(name = "cst")
	protected BigInteger cseType;
	@XmlElement(name = "poa")
	@XmlList
    protected List<String> pointOfAccess;
    @XmlElement(name = "cb", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String cseBase;
    @XmlElement(name = "csi", required = true)
    protected String cseid;
    @XmlElement(name = "mei")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String m2MExtID;
    @XmlElement(name = "tri")
    protected Long triggerRecipientID;
    @XmlElement(name = "rr")
    protected Boolean requestReachability;
    @XmlElement(name = "nl")
    @XmlSchemaType(name = "anyURI")
    protected String nodeLink;
    @XmlTransient
    protected String dKey;
    @XmlTransient
    protected String passCode;
    @XmlTransient
    protected String mappingYn;
    @XmlElement(name = "ch")
    protected List<ChildResourceRef> childResource;
    @XmlElement(name = "sch", namespace = "http://www.onem2m.org/xml/protocols")
    protected List<Schedule> schedule;
    @XmlElement(name = "pch", namespace = "http://www.onem2m.org/xml/protocols")
    protected List<PollingChannel> pollingChannel;
    @XmlElement(name = "ae", namespace = "http://www.onem2m.org/xml/protocols")
    protected List<AE> AE;
    @XmlElement(name = "sub", namespace = "http://www.onem2m.org/xml/protocols")
    protected List<Subscription> subscription;
    @XmlElement(name = "cnt", namespace = "http://www.onem2m.org/xml/protocols")
    protected List<Container> container;
    @XmlElement(name = "mgc", namespace = "http://www.onem2m.org/xml/protocols")
    protected List<MgmtCmd> mgmtCmd;
    @XmlElement(name = "acp", namespace = "http://www.onem2m.org/xml/protocols")
    protected List<AccessControlPolicy> accessControlPolicy;
    @XmlElement(name = "grp", namespace = "http://www.onem2m.org/xml/protocols")
    protected List<Group> group;
    /*
    @XmlElements({
        @XmlElement(name = "schedule", namespace = "http://www.onem2m.org/xml/protocols", type = Schedule.class),
        @XmlElement(name = "pollingChannel", namespace = "http://www.onem2m.org/xml/protocols", type = PollingChannel.class),
        @XmlElement(name = "AE", namespace = "http://www.onem2m.org/xml/protocols", type = AE.class),
        @XmlElement(name = "subscription", namespace = "http://www.onem2m.org/xml/protocols", type = Subscription.class),
        @XmlElement(name = "container", namespace = "http://www.onem2m.org/xml/protocols", type = Container.class),
        @XmlElement(name = "accessControlPolicy", namespace = "http://www.onem2m.org/xml/protocols", type = AccessControlPolicy.class),
        @XmlElement(name = "group", namespace = "http://www.onem2m.org/xml/protocols", type = Group.class)
    })
    protected List<Resource> aeOrContainerOrGroup;
    */
    
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
     * Gets the value of the cseBase property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCSEBase() {
        return cseBase;
    }

    /**
     * Sets the value of the cseBase property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCSEBase(String value) {
        this.cseBase = value;
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
     * Gets the value of the m2MExtID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getM2MExtID() {
        return m2MExtID;
    }

    /**
     * Sets the value of the m2MExtID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setM2MExtID(String value) {
        this.m2MExtID = value;
    }

    /**
     * Gets the value of the triggerRecipientID property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getTriggerRecipientID() {
        return triggerRecipientID;
    }

    /**
     * Sets the value of the triggerRecipientID property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setTriggerRecipientID(Long value) {
        this.triggerRecipientID = value;
    }

    /**
     * Gets the value of the requestReachability property.
     * 
     */
    public Boolean isRequestReachability() {
        return requestReachability;
    }

    /**
     * Sets the value of the requestReachability property.
     * 
     */
    public void setRequestReachability(Boolean value) {
        this.requestReachability = value;
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
    
	public String getDKey() {
		return dKey;
	}

	public void setDKey(String dKey) {
		this.dKey = dKey;
	}

	public String getPassCode() {
		return passCode;
	}

	public void setPassCode(String passCode) {
		this.passCode = passCode;
	}

	public String getMappingYn() {
		return mappingYn;
	}

	public void setMappingYn(String mappingYn) {
		this.mappingYn = mappingYn;
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
	
    public List<Schedule> getSchedule() {
        if (schedule == null) {
        	schedule = new ArrayList<Schedule>();
        }
        return this.schedule;
    }

    public List<PollingChannel> getPollingChannel() {
        if (pollingChannel == null) {
        	pollingChannel = new ArrayList<PollingChannel>();
        }
        return this.pollingChannel;
    }
    
    public List<AE> getAE() {
        if (AE == null) {
        	AE = new ArrayList<AE>();
        }
        return this.AE;
    }
    
    public List<Subscription> getSubscription() {
        if (subscription == null) {
        	subscription = new ArrayList<Subscription>();
        }
        return this.subscription;
    }
    
    public List<Container> getContainer() {
        if (container == null) {
        	container = new ArrayList<Container>();
        }
        return this.container;
    }
    
    public List<MgmtCmd> getMgmtCmd() {
        if (mgmtCmd == null) {
        	mgmtCmd = new ArrayList<MgmtCmd>();
        }
        return this.mgmtCmd;
    }
    
    public List<AccessControlPolicy> getAccessControlPolicy() {
        if (accessControlPolicy == null) {
        	accessControlPolicy = new ArrayList<AccessControlPolicy>();
        }
        return this.accessControlPolicy;
    }
    
    public List<Group> getGroup() {
        if (group == null) {
        	group = new ArrayList<Group>();
        }
        return this.group;
    }

	@Override
	public String toString() {
		return "RemoteCSE [cseType=" + cseType + ", pointOfAccess="
				+ pointOfAccess + ", cseBase=" + cseBase + ", cseid=" + cseid
				+ ", m2MExtID=" + m2MExtID + ", triggerRecipientID="
				+ triggerRecipientID + ", requestReachability="
				+ requestReachability + ", nodeLink=" + nodeLink + ", dKey="
				+ dKey + ", passCode=" + passCode + ", mappingYn=" + mappingYn
				+ ", childResource=" + childResource + ", schedule=" + schedule
				+ ", pollingChannel=" + pollingChannel + ", AE=" + AE
				+ ", subscription=" + subscription + ", container=" + container
				+ ", accessControlPolicy=" + accessControlPolicy + ", group="
				+ group + "]";
	}

    /**
     * Gets the value of the aeOrContainerOrGroup property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the aeOrContainerOrGroup property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAEOrContainerOrGroup().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Schedule }
     * {@link PollingChannel }
     * {@link AE }
     * {@link Subscription }
     * {@link Container }
     * {@link AccessControlPolicy }
     * {@link Group }
     * 
     * 
     */
    /*
    public List<Resource> getAEOrContainerOrGroup() {
        if (aeOrContainerOrGroup == null) {
            aeOrContainerOrGroup = new ArrayList<Resource>();
        }
        return this.aeOrContainerOrGroup;
    }
    */

}
