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

import com.fasterxml.jackson.annotation.JsonRootName;


/**
 * contentInstance domain.
 * @author <ul>
 *         <li>Sang June Lee < blue4444eye@hanmail.net > < blue4444eye@gmail.com ></li>
 *         </ul>
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "stateTag",
    "creator",
    "contentInfo",
    "contentSize",
    "ontologyRef",
    "content",
    "linkType"
})
@JsonRootName("cin")
@XmlRootElement(name = "cin", namespace="http://www.onem2m.org/xml/protocols")
public class ContentInstance
    extends AnnounceableSubordinateResource
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@XmlElement(name = "st", required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger stateTag;
	@XmlElement(name = "cr")
	protected String creator;
	@XmlElement(name = "cnf")
    protected String contentInfo;
    @XmlElement(name = "cs", required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger contentSize;
    @XmlElement(name = "or")
    @XmlSchemaType(name = "anyURI")
    protected String ontologyRef;
    @XmlElement(name = "con", required = true)
    protected String content;
    protected String linkType;

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
     * Gets the value of the contentInfo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContentInfo() {
        return contentInfo;
    }

    /**
     * Sets the value of the contentInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContentInfo(String value) {
        this.contentInfo = value;
    }

    /**
     * Gets the value of the contentSize property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getContentSize() {
        return contentSize;
    }

    /**
     * Sets the value of the contentSize property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setContentSize(BigInteger value) {
        this.contentSize = value;
    }

    /**
     * Gets the value of the ontologyRef property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOntologyRef() {
        return ontologyRef;
    }

    /**
     * Sets the value of the ontologyRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOntologyRef(String value) {
        this.ontologyRef = value;
    }

    /**
     * Gets the value of the content property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the value of the content property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setContent(String value) {
        this.content = value;
    }

	public String getLinkType() {
		return linkType;
	}

	public void setLinkType(String linkType) {
		this.linkType = linkType;
	}

}
