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
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * remoteCSEAnnc domain.
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
    "requestReachability",
    "nodeLink",
    "childResource",
    "nodeAnncOrAEOrAEAnnc"
})
@XmlRootElement(name = "remoteCSEAnnc")
public class RemoteCSEAnnc
    extends AnnouncedResource
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected BigInteger cseType;
    @XmlList
    protected List<String> pointOfAccess;
    @XmlElement(name = "CSEBase")
    @XmlSchemaType(name = "anyURI")
    protected String cseBase;
    @XmlElement(name = "CSE-ID")
    protected String cseid;
    protected Boolean requestReachability;
    @XmlSchemaType(name = "anyURI")
    protected String nodeLink;
    protected List<ChildResourceRef> childResource;
    @XmlElements({
        @XmlElement(name = "nodeAnnc", namespace = "http://www.onem2m.org/xml/protocols", type = NodeAnnc.class),
        @XmlElement(name = "subscription", namespace = "http://www.onem2m.org/xml/protocols", type = Subscription.class),
        @XmlElement(name = "container", namespace = "http://www.onem2m.org/xml/protocols", type = Container.class),
        @XmlElement(name = "pollingChannel", namespace = "http://www.onem2m.org/xml/protocols", type = PollingChannel.class),
        @XmlElement(name = "locationPolicyAnnc", namespace = "http://www.onem2m.org/xml/protocols", type = LocationPolicyAnnc.class),
        @XmlElement(name = "accessControlPolicy", namespace = "http://www.onem2m.org/xml/protocols", type = AccessControlPolicy.class),
        @XmlElement(name = "group", namespace = "http://www.onem2m.org/xml/protocols", type = Group.class),
        @XmlElement(name = "groupAnnc", namespace = "http://www.onem2m.org/xml/protocols", type = GroupAnnc.class),
        @XmlElement(name = "scheduleAnnc", namespace = "http://www.onem2m.org/xml/protocols", type = ScheduleAnnc.class),
        @XmlElement(name = "accessControlPolicyAnnc", namespace = "http://www.onem2m.org/xml/protocols", type = AccessControlPolicyAnnc.class),
        @XmlElement(name = "AEAnnc", namespace = "http://www.onem2m.org/xml/protocols", type = AEAnnc.class),
        @XmlElement(name = "AE", namespace = "http://www.onem2m.org/xml/protocols", type = AE.class),
        @XmlElement(name = "containerAnnc", namespace = "http://www.onem2m.org/xml/protocols", type = ContainerAnnc.class)
    })
    protected List<Resource> nodeAnncOrAEOrAEAnnc;

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
     * Gets the value of the requestReachability property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isRequestReachability() {
        return requestReachability;
    }

    /**
     * Sets the value of the requestReachability property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
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
     * Gets the value of the nodeAnncOrAEOrAEAnnc property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the nodeAnncOrAEOrAEAnnc property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNodeAnncOrAEOrAEAnnc().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link NodeAnnc }
     * {@link Subscription }
     * {@link Container }
     * {@link PollingChannel }
     * {@link LocationPolicyAnnc }
     * {@link AccessControlPolicy }
     * {@link Group }
     * {@link GroupAnnc }
     * {@link ScheduleAnnc }
     * {@link AccessControlPolicyAnnc }
     * {@link AEAnnc }
     * {@link AE }
     * {@link ContainerAnnc }
     * 
     * 
     */
    public List<Resource> getNodeAnncOrAEOrAEAnnc() {
        if (nodeAnncOrAEOrAEAnnc == null) {
            nodeAnncOrAEOrAEAnnc = new ArrayList<Resource>();
        }
        return this.nodeAnncOrAEOrAEAnnc;
    }

}
