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
 * cmdhDefEcValue domain.
 * @author <ul>
 *         <li>Sang June Lee < blue4444eye@hanmail.net > < blue4444eye@gmail.com ></li>
 *         </ul>
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "order",
    "defEcValue",
    "requestOrigin",
    "requestContext",
    "requestContextNotification",
    "requestCharacteristics"
})
@XmlRootElement(name = "cmdv")
public class CmdhDefEcValue
    extends MgmtResource
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@XmlElement(name = "od", required = true)
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger order;
    @XmlElement(name = "dev", required = true)
    protected String defEcValue;
    @XmlList
    @XmlElement(name = "ror", required = true)
    protected List<String> requestOrigin;
    @XmlElement(name = "rct")
    protected Object requestContext;
    @XmlElement(name = "rcn")
    protected Boolean requestContextNotification;
    @XmlElement(name = "rch")
    protected Object requestCharacteristics;

    /**
     * Gets the value of the order property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getOrder() {
        return order;
    }

    /**
     * Sets the value of the order property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setOrder(BigInteger value) {
        this.order = value;
    }

    /**
     * Gets the value of the defEcValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDefEcValue() {
        return defEcValue;
    }

    /**
     * Sets the value of the defEcValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefEcValue(String value) {
        this.defEcValue = value;
    }

    /**
     * Gets the value of the requestOrigin property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the requestOrigin property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRequestOrigin().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getRequestOrigin() {
        if (requestOrigin == null) {
            requestOrigin = new ArrayList<String>();
        }
        return this.requestOrigin;
    }

    /**
     * Gets the value of the requestContext property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getRequestContext() {
        return requestContext;
    }

    /**
     * Sets the value of the requestContext property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setRequestContext(Object value) {
        this.requestContext = value;
    }

    /**
     * Gets the value of the requestContextNotification property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isRequestContextNotification() {
        return requestContextNotification;
    }

    /**
     * Sets the value of the requestContextNotification property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRequestContextNotification(Boolean value) {
        this.requestContextNotification = value;
    }

    /**
     * Gets the value of the requestCharacteristics property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getRequestCharacteristics() {
        return requestCharacteristics;
    }

    /**
     * Sets the value of the requestCharacteristics property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setRequestCharacteristics(Object value) {
        this.requestCharacteristics = value;
    }

}
