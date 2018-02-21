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
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * cmdhEcDefParamValues domain.
 * @author <ul>
 *         <li>Sang June Lee < blue4444eye@hanmail.net > < blue4444eye@gmail.com ></li>
 *         </ul>
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "applicableEventCategory",
    "defaultRequestExpTime",
    "defaultResultExpTime",
    "defaultOpExecTime",
    "defaultRespPersistence",
    "defaultDelAggregation"
})
@XmlRootElement(name = "cmpv")
public class CmdhEcDefParamValues
    extends MgmtResource
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@XmlList
    @XmlElement(name = "aec", required = true)
    protected List<String> applicableEventCategory;
	@XmlElement(name = "dqet")
    protected long defaultRequestExpTime;
	@XmlElement(name = "dset")
    protected long defaultResultExpTime;
	@XmlElement(name = "doet")
    protected long defaultOpExecTime;
	@XmlElement(name = "drp")
    protected long defaultRespPersistence;
	@XmlElement(name = "dda")
    protected Boolean defaultDelAggregation;

    /**
     * Gets the value of the applicableEventCategory property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the applicableEventCategory property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getApplicableEventCategory().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getApplicableEventCategory() {
        if (applicableEventCategory == null) {
            applicableEventCategory = new ArrayList<String>();
        }
        return this.applicableEventCategory;
    }

    /**
     * Gets the value of the defaultRequestExpTime property.
     * 
     */
    public long getDefaultRequestExpTime() {
        return defaultRequestExpTime;
    }

    /**
     * Sets the value of the defaultRequestExpTime property.
     * 
     */
    public void setDefaultRequestExpTime(long value) {
        this.defaultRequestExpTime = value;
    }

    /**
     * Gets the value of the defaultResultExpTime property.
     * 
     */
    public long getDefaultResultExpTime() {
        return defaultResultExpTime;
    }

    /**
     * Sets the value of the defaultResultExpTime property.
     * 
     */
    public void setDefaultResultExpTime(long value) {
        this.defaultResultExpTime = value;
    }

    /**
     * Gets the value of the defaultOpExecTime property.
     * 
     */
    public long getDefaultOpExecTime() {
        return defaultOpExecTime;
    }

    /**
     * Sets the value of the defaultOpExecTime property.
     * 
     */
    public void setDefaultOpExecTime(long value) {
        this.defaultOpExecTime = value;
    }

    /**
     * Gets the value of the defaultRespPersistence property.
     * 
     */
    public long getDefaultRespPersistence() {
        return defaultRespPersistence;
    }

    /**
     * Sets the value of the defaultRespPersistence property.
     * 
     */
    public void setDefaultRespPersistence(long value) {
        this.defaultRespPersistence = value;
    }

    /**
     * Gets the value of the defaultDelAggregation property.
     * 
     */
    public Boolean isDefaultDelAggregation() {
        return defaultDelAggregation;
    }

    /**
     * Sets the value of the defaultDelAggregation property.
     * 
     */
    public void setDefaultDelAggregation(Boolean value) {
        this.defaultDelAggregation = value;
    }

}
