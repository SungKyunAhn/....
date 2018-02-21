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
 * requestPrimitive domain.
 * @author <ul>
 *         <li>Sang June Lee < blue4444eye@hanmail.net > < blue4444eye@gmail.com ></li>
 *         </ul>
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "operation",
    "to",
    "from",
    "requestIdentifier",
    "resourceType",
    "name",
    "content",
    "originatingTimestamp",
    "requestExpirationTimestamp",
    "resultExpirationTimestamp",
    "operationExecutionTime",
    "responseType",
    "resultPersistence",
    "rcn",
    "eventCategory",
    "deliveryAggregation",
    "groupRequestIdentifier",
    "filterCriteria",
    "discoveryResultType"
})
@XmlRootElement(name = "requestPrimitive")
public class RequestPrimitive {

    @XmlElement(required = true)
    protected BigInteger operation;
    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    protected String to;
    @XmlElement(required = true)
    protected String from;
    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String requestIdentifier;
    protected BigInteger resourceType;
    protected String name;
    protected PrimitiveContent content;
    protected String originatingTimestamp;
    protected String requestExpirationTimestamp;
    protected String resultExpirationTimestamp;
    protected String operationExecutionTime;
    protected BigInteger responseType;
    protected Duration resultPersistence;
    protected BigInteger rcn;						// resultContent
    protected String eventCategory;
    protected Boolean deliveryAggregation;
    protected String groupRequestIdentifier;
    protected FilterCriteria filterCriteria;
    protected BigInteger discoveryResultType;

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
     * Gets the value of the to property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTo() {
        return to;
    }

    /**
     * Sets the value of the to property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTo(String value) {
        this.to = value;
    }

    /**
     * Gets the value of the from property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFrom() {
        return from;
    }

    /**
     * Sets the value of the from property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFrom(String value) {
        this.from = value;
    }

    /**
     * Gets the value of the requestIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequestIdentifier() {
        return requestIdentifier;
    }

    /**
     * Sets the value of the requestIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequestIdentifier(String value) {
        this.requestIdentifier = value;
    }

    /**
     * Gets the value of the resourceType property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getResourceType() {
        return resourceType;
    }

    /**
     * Sets the value of the resourceType property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setResourceType(BigInteger value) {
        this.resourceType = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
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
     * Gets the value of the originatingTimestamp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOriginatingTimestamp() {
        return originatingTimestamp;
    }

    /**
     * Sets the value of the originatingTimestamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOriginatingTimestamp(String value) {
        this.originatingTimestamp = value;
    }

    /**
     * Gets the value of the requestExpirationTimestamp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequestExpirationTimestamp() {
        return requestExpirationTimestamp;
    }

    /**
     * Sets the value of the requestExpirationTimestamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequestExpirationTimestamp(String value) {
        this.requestExpirationTimestamp = value;
    }

    /**
     * Gets the value of the resultExpirationTimestamp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResultExpirationTimestamp() {
        return resultExpirationTimestamp;
    }

    /**
     * Sets the value of the resultExpirationTimestamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResultExpirationTimestamp(String value) {
        this.resultExpirationTimestamp = value;
    }

    /**
     * Gets the value of the operationExecutionTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOperationExecutionTime() {
        return operationExecutionTime;
    }

    /**
     * Sets the value of the operationExecutionTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOperationExecutionTime(String value) {
        this.operationExecutionTime = value;
    }

    /**
     * Gets the value of the responseType property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getResponseType() {
        return responseType;
    }

    /**
     * Sets the value of the responseType property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setResponseType(BigInteger value) {
        this.responseType = value;
    }

    /**
     * Gets the value of the resultPersistence property.
     * 
     * @return
     *     possible object is
     *     {@link Duration }
     *     
     */
    public Duration getResultPersistence() {
        return resultPersistence;
    }

    /**
     * Sets the value of the resultPersistence property.
     * 
     * @param value
     *     allowed object is
     *     {@link Duration }
     *     
     */
    public void setResultPersistence(Duration value) {
        this.resultPersistence = value;
    }

    /**
     * Gets the value of the resultContent property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getRcn() {
        return rcn;
    }

    /**
     * Sets the value of the resultContent property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setRcn(BigInteger value) {
        this.rcn = value;
    }

    /**
     * Gets the value of the eventCategory property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEventCategory() {
        return eventCategory;
    }

    /**
     * Sets the value of the eventCategory property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEventCategory(String value) {
        this.eventCategory = value;
    }

    /**
     * Gets the value of the deliveryAggregation property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDeliveryAggregation() {
        return deliveryAggregation;
    }

    /**
     * Sets the value of the deliveryAggregation property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDeliveryAggregation(Boolean value) {
        this.deliveryAggregation = value;
    }

    /**
     * Gets the value of the groupRequestIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGroupRequestIdentifier() {
        return groupRequestIdentifier;
    }

    /**
     * Sets the value of the groupRequestIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGroupRequestIdentifier(String value) {
        this.groupRequestIdentifier = value;
    }

    /**
     * Gets the value of the filterCriteria property.
     * 
     * @return
     *     possible object is
     *     {@link FilterCriteria }
     *     
     */
    public FilterCriteria getFilterCriteria() {
        return filterCriteria;
    }

    /**
     * Sets the value of the filterCriteria property.
     * 
     * @param value
     *     allowed object is
     *     {@link FilterCriteria }
     *     
     */
    public void setFilterCriteria(FilterCriteria value) {
        this.filterCriteria = value;
    }

    /**
     * Gets the value of the discoveryResultType property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getDiscoveryResultType() {
        return discoveryResultType;
    }

    /**
     * Sets the value of the discoveryResultType property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setDiscoveryResultType(BigInteger value) {
        this.discoveryResultType = value;
    }

}
