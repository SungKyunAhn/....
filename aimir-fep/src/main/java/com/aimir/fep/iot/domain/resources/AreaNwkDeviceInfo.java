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
 * areaNwkDeviceInfo domain.
 * @author <ul>
 *         <li>Sang June Lee < blue4444eye@hanmail.net > < blue4444eye@gmail.com ></li>
 *         </ul>
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "devID",
    "devType",
    "areaNwkId",
    "sleepInterval",
    "sleepDuration",
    "status",
    "listOfNeighbors",
    "childResource",
    "subscription"
})
@XmlRootElement(name = "andi")
public class AreaNwkDeviceInfo
    extends MgmtResource
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@XmlElement(name = "dvd", required = true)
    protected String devID;
    @XmlElement(name = "dvt", required = true)
    protected String devType;
    @XmlElement(name = "awi", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String areaNwkId;
    @XmlElement(name = "sli")
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger sleepInterval;
    @XmlElement(name = "sld")
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger sleepDuration;
    @XmlElement(name = "ss")
    protected String status;
    @XmlList
    @XmlElement(name = "lnh", required = true)
    protected List<String> listOfNeighbors;
    @XmlElement(name = "ch")
    protected List<ChildResourceRef> childResource;
    @XmlElement(name = "sub", namespace = "http://www.onem2m.org/xml/protocols")
    protected List<Subscription> subscription;

    /**
     * Gets the value of the devID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDevID() {
        return devID;
    }

    /**
     * Sets the value of the devID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDevID(String value) {
        this.devID = value;
    }

    /**
     * Gets the value of the devType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDevType() {
        return devType;
    }

    /**
     * Sets the value of the devType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDevType(String value) {
        this.devType = value;
    }

    /**
     * Gets the value of the areaNwkId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAreaNwkId() {
        return areaNwkId;
    }

    /**
     * Sets the value of the areaNwkId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAreaNwkId(String value) {
        this.areaNwkId = value;
    }

    /**
     * Gets the value of the sleepInterval property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSleepInterval() {
        return sleepInterval;
    }

    /**
     * Sets the value of the sleepInterval property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSleepInterval(BigInteger value) {
        this.sleepInterval = value;
    }

    /**
     * Gets the value of the sleepDuration property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSleepDuration() {
        return sleepDuration;
    }

    /**
     * Sets the value of the sleepDuration property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSleepDuration(BigInteger value) {
        this.sleepDuration = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatus(String value) {
        this.status = value;
    }

    /**
     * Gets the value of the listOfNeighbors property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the listOfNeighbors property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getListOfNeighbors().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getListOfNeighbors() {
        if (listOfNeighbors == null) {
            listOfNeighbors = new ArrayList<String>();
        }
        return this.listOfNeighbors;
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
