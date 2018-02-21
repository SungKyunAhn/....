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
 * deviceCapabilityAnnc domain.
 * @author <ul>
 *         <li>Sang June Lee < blue4444eye@hanmail.net > < blue4444eye@gmail.com ></li>
 *         </ul>
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "capabilityName",
    "attached",
    "capabilityActionStatus",
    "currentState",
    "enable",
    "disable",
    "childResource",
    "subscription"
})
@XmlRootElement(name = "deviceCapabilityAnnc")
public class DeviceCapabilityAnnc
    extends AnnouncedMgmtResource
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected String capabilityName;
    protected Boolean attached;
    protected ActionStatus capabilityActionStatus;
    protected Boolean currentState;
    protected Boolean enable;
    protected Boolean disable;
    protected List<ChildResourceRef> childResource;
    @XmlElement(namespace = "http://www.onem2m.org/xml/protocols")
    protected List<Subscription> subscription;

    /**
     * Gets the value of the capabilityName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCapabilityName() {
        return capabilityName;
    }

    /**
     * Sets the value of the capabilityName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCapabilityName(String value) {
        this.capabilityName = value;
    }

    /**
     * Gets the value of the attached property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAttached() {
        return attached;
    }

    /**
     * Sets the value of the attached property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAttached(Boolean value) {
        this.attached = value;
    }

    /**
     * Gets the value of the capabilityActionStatus property.
     * 
     * @return
     *     possible object is
     *     {@link ActionStatus }
     *     
     */
    public ActionStatus getCapabilityActionStatus() {
        return capabilityActionStatus;
    }

    /**
     * Sets the value of the capabilityActionStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link ActionStatus }
     *     
     */
    public void setCapabilityActionStatus(ActionStatus value) {
        this.capabilityActionStatus = value;
    }

    /**
     * Gets the value of the currentState property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCurrentState() {
        return currentState;
    }

    /**
     * Sets the value of the currentState property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCurrentState(Boolean value) {
        this.currentState = value;
    }

    /**
     * Gets the value of the enable property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isEnable() {
        return enable;
    }

    /**
     * Sets the value of the enable property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setEnable(Boolean value) {
        this.enable = value;
    }

    /**
     * Gets the value of the disable property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDisable() {
        return disable;
    }

    /**
     * Sets the value of the disable property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDisable(Boolean value) {
        this.disable = value;
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
