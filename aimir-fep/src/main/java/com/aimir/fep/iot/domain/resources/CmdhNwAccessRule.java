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
 * cmdhNwAccessRule domain.
 * @author <ul>
 *         <li>Sang June Lee < blue4444eye@hanmail.net > < blue4444eye@gmail.com ></li>
 *         </ul>
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "targetNetwork",
    "minReqVolume",
    "backOffParameters",
    "otherConditions",
    "mgmtLink"
})
@XmlRootElement(name = "cmwr")
public class CmdhNwAccessRule
    extends MgmtResource
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@XmlList
    @XmlElement(name = "ttn", required = true)
    protected List<String> targetNetwork;
    @XmlElement(name = "mrv", required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger minReqVolume;
    @XmlList
    @XmlElement(name = "bop", required = true)
    protected List<BigInteger> backOffParameters;
    @XmlElement(name = "ohc", required = true)
    protected Object otherConditions;
    @XmlElement(name = "cmlk", required = true)
    protected MgmtLinkRef mgmtLink;

    /**
     * Gets the value of the targetNetwork property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the targetNetwork property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTargetNetwork().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getTargetNetwork() {
        if (targetNetwork == null) {
            targetNetwork = new ArrayList<String>();
        }
        return this.targetNetwork;
    }

    /**
     * Gets the value of the minReqVolume property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getMinReqVolume() {
        return minReqVolume;
    }

    /**
     * Sets the value of the minReqVolume property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setMinReqVolume(BigInteger value) {
        this.minReqVolume = value;
    }

    /**
     * Gets the value of the backOffParameters property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the backOffParameters property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBackOffParameters().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BigInteger }
     * 
     * 
     */
    public List<BigInteger> getBackOffParameters() {
        if (backOffParameters == null) {
            backOffParameters = new ArrayList<BigInteger>();
        }
        return this.backOffParameters;
    }

    /**
     * Gets the value of the otherConditions property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getOtherConditions() {
        return otherConditions;
    }

    /**
     * Sets the value of the otherConditions property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setOtherConditions(Object value) {
        this.otherConditions = value;
    }

    /**
     * Gets the value of the mgmtLink property.
     * 
     * @return
     *     possible object is
     *     {@link MgmtLinkRef }
     *     
     */
    public MgmtLinkRef getMgmtLink() {
        return mgmtLink;
    }

    /**
     * Sets the value of the mgmtLink property.
     * 
     * @param value
     *     allowed object is
     *     {@link MgmtLinkRef }
     *     
     */
    public void setMgmtLink(MgmtLinkRef value) {
        this.mgmtLink = value;
    }

}
