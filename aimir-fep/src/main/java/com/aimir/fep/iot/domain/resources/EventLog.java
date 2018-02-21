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
import javax.xml.bind.annotation.XmlType;


/**
 * eventLog domain.
 * @author <ul>
 *         <li>Sang June Lee < blue4444eye@hanmail.net > < blue4444eye@gmail.com ></li>
 *         </ul>
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "logTypeId",
    "logData",
    "logStatus",
    "logStart",
    "logStop",
    "childResource",
    "subscription"
})
@XmlRootElement(name = "evl")
public class EventLog
    extends MgmtResource
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@XmlElement(name = "lgt", required = true)
    protected BigInteger logTypeId;
    @XmlElement(name = "lgd", required = true)
    protected String logData;
    @XmlElement(name = "lgs", required = true)
    protected BigInteger logStatus;
    @XmlElement(name = "lga")
    protected Boolean logStart;
    @XmlElement(name = "lgo")
    protected Boolean logStop;
    @XmlElement(name = "ch")
    protected List<ChildResourceRef> childResource;
    @XmlElement(name = "sub", namespace = "http://www.onem2m.org/xml/protocols")
    protected List<Subscription> subscription;

    /**
     * Gets the value of the logTypeId property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getLogTypeId() {
        return logTypeId;
    }

    /**
     * Sets the value of the logTypeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setLogTypeId(BigInteger value) {
        this.logTypeId = value;
    }

    /**
     * Gets the value of the logData property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLogData() {
        return logData;
    }

    /**
     * Sets the value of the logData property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLogData(String value) {
        this.logData = value;
    }

    /**
     * Gets the value of the logStatus property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getLogStatus() {
        return logStatus;
    }

    /**
     * Sets the value of the logStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setLogStatus(BigInteger value) {
        this.logStatus = value;
    }

    /**
     * Gets the value of the logStart property.
     * 
     */
    public Boolean isLogStart() {
        return logStart;
    }

    /**
     * Sets the value of the logStart property.
     * 
     */
    public void setLogStart(Boolean value) {
        this.logStart = value;
    }

    /**
     * Gets the value of the logStop property.
     * 
     */
    public Boolean isLogStop() {
        return logStop;
    }

    /**
     * Sets the value of the logStop property.
     * 
     */
    public void setLogStop(Boolean value) {
        this.logStop = value;
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
