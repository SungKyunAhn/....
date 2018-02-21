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

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * resource domain.
 * @author <ul>
 *         <li>Sang June Lee < blue4444eye@hanmail.net > < blue4444eye@gmail.com ></li>
 *         </ul>
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "resource", propOrder = {
    "resourceType",
    "resourceID",
    "resourceName",
    "parentID",
    "creationTime",
    "lastModifiedTime",
    "labels"
})
@XmlSeeAlso({
    ResourceWrapper.class,
    AnnouncedResource.class,
    RegularResource.class,
    AnnounceableSubordinateResource.class,
    AnnouncedSubordinateResource.class
})
public class Resource implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@XmlElement(name = "rty", required = true)
    protected BigInteger resourceType;
    @XmlElement(name = "ri", required = true)
    protected String resourceID;
    @XmlElement(name = "rn", required = true)
    protected String resourceName;
    @XmlElement(name = "pi", required = true)
    protected String parentID;
    @XmlElement(name = "ct", required = true)
    protected String creationTime;
    @XmlElement(name = "lt", required = true)
    protected String lastModifiedTime;
    @XmlElement(name = "lbl")
    @XmlList
    protected List<String> labels;
  /*
   @XmlAttribute(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String name;
   */
    @XmlTransient
    protected ResourceRef resourceRef;

    @XmlTransient
    protected String seq;
    
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
     * Gets the value of the resourceID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResourceID() {
        return resourceID;
    }

    /**
     * Sets the value of the resourceID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResourceID(String value) {
        this.resourceID = value;
    }
    
    /**
     * Gets the value of the resourceName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResourceName() {
        return resourceName;
    }

    /**
     * Sets the value of the resourceName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResourceName(String value) {
        this.resourceName = value;
    }

    /**
     * Gets the value of the parentID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParentID() {
        return parentID;
    }

    /**
     * Sets the value of the parentID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParentID(String value) {
        this.parentID = value;
    }

    /**
     * Gets the value of the creationTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreationTime() {
        return creationTime;
    }

    /**
     * Sets the value of the creationTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreationTime(String value) {
        this.creationTime = value;
    }

    /**
     * Gets the value of the lastModifiedTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLastModifiedTime() {
        return lastModifiedTime;
    }

    /**
     * Sets the value of the lastModifiedTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastModifiedTime(String value) {
        this.lastModifiedTime = value;
    }

    /**
     * Gets the value of the labels property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the labels property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLabels().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getLabels() {
        if (labels == null) {
            labels = new ArrayList<String>();
        }
        return this.labels;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
//    public String getName() {
//        return name;
//    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
//    public void setName(String value) {
//        this.name = value;
//    }

	public ResourceRef getResourceRef() {
		return resourceRef;
	}

	public void setResourceRef(ResourceRef resourceRef) {
		this.resourceRef = resourceRef;
	}

	public String getSeq() {
		return seq;
	}

	public void setSeq(String seq) {
		this.seq = seq;
	}

}
