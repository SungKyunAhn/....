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


/**
 * request domain.
 * @author <ul>
 *         <li>Sang June Lee < blue4444eye@hanmail.net > < blue4444eye@gmail.com ></li>
 *         </ul>
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "stateTag",
    "operation",
    "target",
    "originator",
    "requestID",
    "metaInformation",
    "content",
    "requestStatus",
    "operationResult",
    "childResource",
    "subscription"
})
@XmlRootElement(name = "req", namespace="http://www.onem2m.org/xml/protocols")
public class Request
    extends RegularResource
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@XmlElement(name = "st", required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger stateTag;
    @XmlElement(name = "opn", required = true)
    protected BigInteger operation;
    @XmlElement(name = "tg", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String target;
    @XmlElement(name = "og", required = true)
    protected String originator;
    @XmlElement(name = "rid", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String requestID;
    @XmlElement(name = "mi", required = true)
    protected MetaInformation metaInformation;
    @XmlElement(name = "con", required = true)
    protected PrimitiveContent content;
    @XmlElement(name = "rs", required = true)
    protected BigInteger requestStatus;
    @XmlElement(name = "ol", required = true)
    protected OperationResult operationResult;
    @XmlElement(name = "ch")
    protected List<ChildResourceRef> childResource;
    @XmlElement(name = "sub", namespace = "http://www.onem2m.org/xml/protocols")
    protected List<Subscription> subscription;

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
     * Gets the value of the operation property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getOperation() {
        return operation;
    }

    /**
     * Sets the value of the operation property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setOperation(BigInteger value) {
        this.operation = value;
    }

    /**
     * Gets the value of the target property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTarget() {
        return target;
    }

    /**
     * Sets the value of the target property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTarget(String value) {
        this.target = value;
    }

    /**
     * Gets the value of the originator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOriginator() {
        return originator;
    }

    /**
     * Sets the value of the originator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOriginator(String value) {
        this.originator = value;
    }

    /**
     * Gets the value of the requestID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequestID() {
        return requestID;
    }

    /**
     * Sets the value of the requestID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequestID(String value) {
        this.requestID = value;
    }

    /**
     * Gets the value of the metaInformation property.
     * 
     * @return
     *     possible object is
     *     {@link MetaInformation }
     *     
     */
    public MetaInformation getMetaInformation() {
        return metaInformation;
    }

    /**
     * Sets the value of the metaInformation property.
     * 
     * @param value
     *     allowed object is
     *     {@link MetaInformation }
     *     
     */
    public void setMetaInformation(MetaInformation value) {
        this.metaInformation = value;
    }

    /**
     * Gets the value of the content property.
     * 
     * @return
     *     possible object is
     *     {@link PrimitiveContent }
     *     
     */
    public PrimitiveContent getContent() {
        return content;
    }

    /**
     * Sets the value of the content property.
     * 
     * @param value
     *     allowed object is
     *     {@link PrimitiveContent }
     *     
     */
    public void setContent(PrimitiveContent value) {
        this.content = value;
    }

    /**
     * Gets the value of the requestStatus property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getRequestStatus() {
        return requestStatus;
    }

    /**
     * Sets the value of the requestStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setRequestStatus(BigInteger value) {
        this.requestStatus = value;
    }

    /**
     * Gets the value of the operationResult property.
     * 
     * @return
     *     possible object is
     *     {@link OperationResult }
     *     
     */
    public OperationResult getOperationResult() {
        return operationResult;
    }

    /**
     * Sets the value of the operationResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link OperationResult }
     *     
     */
    public void setOperationResult(OperationResult value) {
        this.operationResult = value;
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
