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
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.Duration;


/**
 * execInstance domain.
 * @author <ul>
 *         <li>Sang June Lee < blue4444eye@hanmail.net > < blue4444eye@gmail.com ></li>
 *         </ul>
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "execStatus",
    "execResult",
    "execDisable",
    "execTarget",
    "execMode",
    "execFrequency",
    "execDelay",
    "execNumber",
    "execReqArgs",
    "childResource",
    "subscription"
})
@XmlRootElement(name = "exin", namespace="http://www.onem2m.org/xml/protocols")
public class ExecInstance
    extends RegularResource
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@XmlElement(name = "exs", required = true)
    protected BigInteger execStatus;
    @XmlElement(name = "exr", required = true)
    protected BigInteger execResult;
    @XmlElement(name = "exd")
    protected Boolean execDisable;
    @XmlElement(name = "ext", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String execTarget;
    @XmlElement(name = "exm")
    protected BigInteger execMode;
    @XmlElement(name = "exf")
    protected Duration execFrequency;
    @XmlElement(name = "exy")
    protected Duration execDelay;
    @XmlElement(name = "exn")
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger execNumber;
    @XmlElement(name = "exra")
    //protected ExecReqArgsListType execReqArgs;
    protected String execReqArgs;
    @XmlElement(name = "ch")
    protected List<ChildResourceRef> childResource;
    @XmlElement(name = "sub", namespace = "http://www.onem2m.org/xml/protocols")
    protected List<Subscription> subscription;

    /**
     * Gets the value of the execStatus property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getExecStatus() {
        return execStatus;
    }

    /**
     * Sets the value of the execStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setExecStatus(BigInteger value) {
        this.execStatus = value;
    }

    /**
     * Gets the value of the execResult property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getExecResult() {
        return execResult;
    }

    /**
     * Sets the value of the execResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setExecResult(BigInteger value) {
        this.execResult = value;
    }

    /**
     * Gets the value of the execDisable property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isExecDisable() {
        return execDisable;
    }

    /**
     * Sets the value of the execDisable property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setExecDisable(Boolean value) {
        this.execDisable = value;
    }

    /**
     * Gets the value of the execTarget property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExecTarget() {
        return execTarget;
    }

    /**
     * Sets the value of the execTarget property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExecTarget(String value) {
        this.execTarget = value;
    }

    /**
     * Gets the value of the execMode property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getExecMode() {
        return execMode;
    }

    /**
     * Sets the value of the execMode property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setExecMode(BigInteger value) {
        this.execMode = value;
    }

    /**
     * Gets the value of the execFrequency property.
     * 
     * @return
     *     possible object is
     *     {@link Duration }
     *     
     */
    public Duration getExecFrequency() {
        return execFrequency;
    }

    /**
     * Sets the value of the execFrequency property.
     * 
     * @param value
     *     allowed object is
     *     {@link Duration }
     *     
     */
    public void setExecFrequency(Duration value) {
        this.execFrequency = value;
    }

    /**
     * Gets the value of the execDelay property.
     * 
     * @return
     *     possible object is
     *     {@link Duration }
     *     
     */
    public Duration getExecDelay() {
        return execDelay;
    }

    /**
     * Sets the value of the execDelay property.
     * 
     * @param value
     *     allowed object is
     *     {@link Duration }
     *     
     */
    public void setExecDelay(Duration value) {
        this.execDelay = value;
    }

    /**
     * Gets the value of the execNumber property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getExecNumber() {
        return execNumber;
    }

    /**
     * Sets the value of the execNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setExecNumber(BigInteger value) {
        this.execNumber = value;
    }

    /**
     * Gets the value of the execReqArgs property.
     * 
     * @return
     *     possible object is
     *     //{@link ExecReqArgsListType }
     *     {@link String }
     *     
     */
    public String getExecReqArgs() {
        return execReqArgs;
    }

    /**
     * Sets the value of the execReqArgs property.
     * 
     * @param value
     *     allowed object is
     *     //{@link ExecReqArgsListType }
     *     {@link String }
     *     
     */
    public void setExecReqArgs(String value) {
        this.execReqArgs = value;
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
