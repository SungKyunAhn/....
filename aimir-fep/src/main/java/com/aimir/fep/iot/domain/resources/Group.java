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
 * group domain.
 * @author <ul>
 *         <li>Sang June Lee < blue4444eye@hanmail.net > < blue4444eye@gmail.com ></li>
 *         </ul>
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "creator",
    "memberType",
    "currentNrOfMembers",
    "maxNrOfMembers",
    "memberIDs",
    "membersAccessControlPolicyIDs",
    "memberTypeValidated",
    "consistencyStrategy",
    "groupName",
    "fanOutPoint",
    "childResource",
    "subscription"
})
@XmlRootElement(name = "grp", namespace="http://www.onem2m.org/xml/protocols")
public class Group
    extends AnnounceableResource
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@XmlElement(name = "cr")
	protected String creator;
    @XmlElement(name = "mt", required = true)
    protected BigInteger memberType;
    @XmlElement(name = "cnm", required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger currentNrOfMembers;
    @XmlElement(name = "mnm", required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger maxNrOfMembers;
    @XmlList
    @XmlElement(name = "mid", required = true)
    protected List<String> memberIDs;
    @XmlElement(name = "macp")
    @XmlList
    protected List<String> membersAccessControlPolicyIDs;
    @XmlElement(name = "mtv")
    protected Boolean memberTypeValidated;
    @XmlElement(name = "csy")
    protected BigInteger consistencyStrategy;
    @XmlElement(name = "gn")
    protected String groupName;
    @XmlElement(name = "fopt", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String fanOutPoint;
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
     * Gets the value of the memberType property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getMemberType() {
        return memberType;
    }

    /**
     * Sets the value of the memberType property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setMemberType(BigInteger value) {
        this.memberType = value;
    }

    /**
     * Gets the value of the currentNrOfMembers property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCurrentNrOfMembers() {
        return currentNrOfMembers;
    }

    /**
     * Sets the value of the currentNrOfMembers property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCurrentNrOfMembers(BigInteger value) {
        this.currentNrOfMembers = value;
    }

    /**
     * Gets the value of the maxNrOfMembers property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getMaxNrOfMembers() {
        return maxNrOfMembers;
    }

    /**
     * Sets the value of the maxNrOfMembers property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setMaxNrOfMembers(BigInteger value) {
        this.maxNrOfMembers = value;
    }

    /**
     * Gets the value of the memberIDs property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the memberIDs property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMemberIDs().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getMemberIDs() {
        if (memberIDs == null) {
            memberIDs = new ArrayList<String>();
        }
        return this.memberIDs;
    }

    /**
     * Gets the value of the membersAccessControlPolicyIDs property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the membersAccessControlPolicyIDs property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMembersAccessControlPolicyIDs().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getMembersAccessControlPolicyIDs() {
        if (membersAccessControlPolicyIDs == null) {
            membersAccessControlPolicyIDs = new ArrayList<String>();
        }
        return this.membersAccessControlPolicyIDs;
    }

    /**
     * Gets the value of the memberTypeValidated property.
     * 
     */
    public Boolean isMemberTypeValidated() {
        return memberTypeValidated;
    }

    /**
     * Sets the value of the memberTypeValidated property.
     * 
     */
    public void setMemberTypeValidated(Boolean value) {
        this.memberTypeValidated = value;
    }

    /**
     * Gets the value of the consistencyStrategy property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getConsistencyStrategy() {
        return consistencyStrategy;
    }

    /**
     * Sets the value of the consistencyStrategy property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setConsistencyStrategy(BigInteger value) {
        this.consistencyStrategy = value;
    }

    /**
     * Gets the value of the groupName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * Sets the value of the groupName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGroupName(String value) {
        this.groupName = value;
    }

    /**
     * Gets the value of the fanOutPoint property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFanOutPoint() {
        return fanOutPoint;
    }

    /**
     * Sets the value of the fanOutPoint property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFanOutPoint(String value) {
        this.fanOutPoint = value;
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
