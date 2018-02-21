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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import com.aimir.fep.iot.utils.CommonUtil;


/**
 * filterCriteria domain.
 * @author <ul>
 *         <li>Sang June Lee < blue4444eye@hanmail.net > < blue4444eye@gmail.com ></li>
 *         </ul>
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "filterCriteria", propOrder = {
    "crb",
    "cra",
    "ms",
    "us",
    "sts",
    "stb",
    "exb",
    "exa",
    "lbl",
    "rty",
    "sza",
    "szb",
    "cty",
    "atr",
    "fu",
    "lim"
})
public class FilterCriteria {

    protected String crb;		// createdBefore
    protected String cra;		// createdAfter
    protected String ms;		// modifiedSince
    protected String us;		// unmodifiedSince
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger sts;	// stateTagSmaller
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger stb;	// stateTagBigger
    protected String exb;		// expireBefore
    protected String exa;		// expireAfter
    protected String[] lbl;		// labels
    protected BigInteger[] rty;	// resourceType
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger sza;	// sizeAbove
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger szb;	// sizeBelow
    protected String[] cty;		// contentType
    protected Attribute[] atr;	// attribute
    protected BigInteger fu;	// filterUsage
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger lim;	// limit
    
    public Boolean isFilterCriteria() {
    	boolean isFilterCriteria = true;
    	
    	if (	CommonUtil.isEmpty(crb) && CommonUtil.isEmpty(cra) && CommonUtil.isEmpty(ms) && CommonUtil.isEmpty(us) && CommonUtil.isEmpty(sts)
    		 && CommonUtil.isEmpty(stb) && CommonUtil.isEmpty(exb) && CommonUtil.isEmpty(exa) && CommonUtil.isEmpty(lbl) && CommonUtil.isEmpty(rty)
    		 && CommonUtil.isEmpty(sza) && CommonUtil.isEmpty(szb) && CommonUtil.isEmpty(cty) && CommonUtil.isEmpty(atr) && CommonUtil.isEmpty(lim)) {
    		
    		isFilterCriteria = false;
    	}
    	
    	return isFilterCriteria;
    }
    
    /**
     * Gets the value of the createdBefore property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	public String getCrb() {
		return crb;
	}
	
	/**
     * Sets the value of the createdBefore property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
	public void setCrb(String crb) {
		this.crb = crb;
	}
	
	/**
     * Gets the value of the createdAfter property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	public String getCra() {
		return cra;
	}
	
	/**
     * Sets the value of the createdAfter property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
	public void setCra(String cra) {
		this.cra = cra;
	}
	
	/**
     * Gets the value of the modifiedSince property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	public String getMs() {
		return ms;
	}
	
	/**
     * Sets the value of the modifiedSince property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
	public void setMs(String ms) {
		this.ms = ms;
	}
	
	/**
     * Gets the value of the unmodifiedSince property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	public String getUs() {
		return us;
	}
	
	/**
     * Sets the value of the unmodifiedSince property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
	public void setUs(String us) {
		this.us = us;
	}
	
	/**
     * Gets the value of the stateTagSmaller property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
	public BigInteger getSts() {
		return sts;
	}
	
	/**
     * Sets the value of the stateTagSmaller property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
	public void setSts(BigInteger sts) {
		this.sts = sts;
	}
	
	/**
     * Gets the value of the stateTagBigger property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
	public BigInteger getStb() {
		return stb;
	}
	
	/**
     * Sets the value of the stateTagBigger property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
	public void setStb(BigInteger stb) {
		this.stb = stb;
	}
	
	/**
     * Gets the value of the expireBefore property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	public String getExb() {
		return exb;
	}
	
	/**
     * Sets the value of the expireBefore property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
	public void setExb(String exb) {
		this.exb = exb;
	}
	
	/**
     * Gets the value of the expireAfter property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	public String getExa() {
		return exa;
	}
	
	/**
     * Sets the value of the expireAfter property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
	public void setExa(String exa) {
		this.exa = exa;
	}
	
	/**
     * Gets the value of the labels property.
     * 
     * @return
     *     possible object[] is
     *     {@link String[] }
     * 
     * 
     */
	public String[] getLbl() {
		return lbl;
	}
	
	/**
     * Sets the value of the labels property.
     * 
     * @param value
     *     allowed object[] is
     *     {@link String[] }
     *     
     */
	public void setLbl(String[] lbl) {
		this.lbl = lbl;
	}
	
	/**
     * Gets the value of the resourceType property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
	public BigInteger[] getRty() {
		return rty;
	}
	
	/**
     * Sets the value of the resourceType property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
	public void setRty(BigInteger[] rty) {
		this.rty = rty;
	}
	
	/**
     * Gets the value of the sizeAbove property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
	public BigInteger getSza() {
		return sza;
	}
	
	/**
     * Sets the value of the sizeAbove property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
	public void setSza(BigInteger sza) {
		this.sza = sza;
	}
	
	/**
     * Gets the value of the sizeBelow property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
	public BigInteger getSzb() {
		return szb;
	}
	
	/**
     * Sets the value of the sizeBelow property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
	public void setSzb(BigInteger szb) {
		this.szb = szb;
	}
	
	/**
     * Gets the value of the contentType property.
     * 
     * @return
     *     possible object[] is
     *     {@link String[] }
     * 
     * 
     */
	public String[] getCty() {
		return cty;
	}
	
	/**
     * Sets the value of the contentType property.
     * 
     * @param value
     *     allowed object[] is
     *     {@link String[] }
     *     
     */
	public void setCty(String[] cty) {
		this.cty = cty;
	}
	
	/**
     * Gets the value of the attribute property.
     * 
     * @return
     *     possible object[] is
     *     {@link Attribute[] }
     * 
     * 
     */
	public Attribute[] getAtr() {
		return atr;
	}
	
	/**
     * Sets the value of the attribute property.
     * 
     * @param value
     *     allowed object[] is
     *     {@link Attribute[] }
     *     
     */
	public void setAtr(Attribute[] atr) {
		this.atr = atr;
	}
	
	/**
     * Gets the value of the filterUsage property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
	public BigInteger getFu() {
		return fu;
	}
	
	/**
     * Sets the value of the filterUsage property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
	public void setFu(BigInteger fu) {
		this.fu = fu;
	}
	
	/**
     * Gets the value of the limit property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
	public BigInteger getLim() {
		return lim;
	}
	
	/**
     * Sets the value of the limit property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
	public void setLim(BigInteger lim) {
		this.lim = lim;
	}
}
