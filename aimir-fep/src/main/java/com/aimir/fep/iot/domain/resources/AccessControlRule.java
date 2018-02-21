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
import javax.xml.bind.annotation.XmlType;


/**
 * accessControlRule domain.
 * @author <ul>
 *         <li>Sang June Lee < blue4444eye@hanmail.net > < blue4444eye@gmail.com ></li>
 *         </ul>
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "accessControlRule", propOrder = {
    "accessControlOriginators",
    "accessControlOperations",
    "accessControlContexts"
})
public class AccessControlRule {

    @XmlList
    @XmlElement(required = true)
    protected List<String> accessControlOriginators;
    @XmlElement(required = true)
    protected BigInteger accessControlOperations;
    protected List<AccessControlRule.AccessControlContexts> accessControlContexts;

    /**
     * Gets the value of the accessControlOriginators property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the accessControlOriginators property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAccessControlOriginators().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getAccessControlOriginators() {
        if (accessControlOriginators == null) {
            accessControlOriginators = new ArrayList<String>();
        }
        return this.accessControlOriginators;
    }

    /**
     * Gets the value of the accessControlOperations property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getAccessControlOperations() {
        return accessControlOperations;
    }

    /**
     * Sets the value of the accessControlOperations property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setAccessControlOperations(BigInteger value) {
        this.accessControlOperations = value;
    }

    /**
     * Gets the value of the accessControlContexts property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the accessControlContexts property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAccessControlContexts().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AccessControlRule.AccessControlContexts }
     * 
     * 
     */
    public List<AccessControlRule.AccessControlContexts> getAccessControlContexts() {
        if (accessControlContexts == null) {
            accessControlContexts = new ArrayList<AccessControlRule.AccessControlContexts>();
        }
        return this.accessControlContexts;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="accessControlWindow" type="{http://www.onem2m.org/xml/protocols}scheduleEntry" maxOccurs="unbounded" minOccurs="0"/>
     *         &lt;element name="accessControlIpAddresses" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="ipv4Addresses" minOccurs="0">
     *                     &lt;simpleType>
     *                       &lt;list itemType="{http://www.onem2m.org/xml/protocols}ipv4" />
     *                     &lt;/simpleType>
     *                   &lt;/element>
     *                   &lt;element name="ipv6Addresses" minOccurs="0">
     *                     &lt;simpleType>
     *                       &lt;list itemType="{http://www.onem2m.org/xml/protocols}ipv6" />
     *                     &lt;/simpleType>
     *                   &lt;/element>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="accessControlLocationRegion" type="{http://www.onem2m.org/xml/protocols}locationRegion" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "accessControlWindow",
        "accessControlIpAddresses",
        "accessControlLocationRegion"
    })
    public static class AccessControlContexts {

        protected List<String> accessControlWindow;
        protected AccessControlRule.AccessControlContexts.AccessControlIpAddresses accessControlIpAddresses;
        protected LocationRegion accessControlLocationRegion;

        /**
         * Gets the value of the accessControlWindow property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the accessControlWindow property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getAccessControlWindow().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link String }
         * 
         * 
         */
        public List<String> getAccessControlWindow() {
            if (accessControlWindow == null) {
                accessControlWindow = new ArrayList<String>();
            }
            return this.accessControlWindow;
        }

        /**
         * Gets the value of the accessControlIpAddresses property.
         * 
         * @return
         *     possible object is
         *     {@link AccessControlRule.AccessControlContexts.AccessControlIpAddresses }
         *     
         */
        public AccessControlRule.AccessControlContexts.AccessControlIpAddresses getAccessControlIpAddresses() {
            return accessControlIpAddresses;
        }

        /**
         * Sets the value of the accessControlIpAddresses property.
         * 
         * @param value
         *     allowed object is
         *     {@link AccessControlRule.AccessControlContexts.AccessControlIpAddresses }
         *     
         */
        public void setAccessControlIpAddresses(AccessControlRule.AccessControlContexts.AccessControlIpAddresses value) {
            this.accessControlIpAddresses = value;
        }

        /**
         * Gets the value of the accessControlLocationRegion property.
         * 
         * @return
         *     possible object is
         *     {@link LocationRegion }
         *     
         */
        public LocationRegion getAccessControlLocationRegion() {
            return accessControlLocationRegion;
        }

        /**
         * Sets the value of the accessControlLocationRegion property.
         * 
         * @param value
         *     allowed object is
         *     {@link LocationRegion }
         *     
         */
        public void setAccessControlLocationRegion(LocationRegion value) {
            this.accessControlLocationRegion = value;
        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="ipv4Addresses" minOccurs="0">
         *           &lt;simpleType>
         *             &lt;list itemType="{http://www.onem2m.org/xml/protocols}ipv4" />
         *           &lt;/simpleType>
         *         &lt;/element>
         *         &lt;element name="ipv6Addresses" minOccurs="0">
         *           &lt;simpleType>
         *             &lt;list itemType="{http://www.onem2m.org/xml/protocols}ipv6" />
         *           &lt;/simpleType>
         *         &lt;/element>
         *       &lt;/sequence>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "ipv4Addresses",
            "ipv6Addresses"
        })
        public static class AccessControlIpAddresses {

            @XmlList
            protected List<String> ipv4Addresses;
            @XmlList
            protected List<String> ipv6Addresses;

            /**
             * Gets the value of the ipv4Addresses property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the ipv4Addresses property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getIpv4Addresses().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link String }
             * 
             * 
             */
            public List<String> getIpv4Addresses() {
                if (ipv4Addresses == null) {
                    ipv4Addresses = new ArrayList<String>();
                }
                return this.ipv4Addresses;
            }

            /**
             * Gets the value of the ipv6Addresses property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the ipv6Addresses property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getIpv6Addresses().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link String }
             * 
             * 
             */
            public List<String> getIpv6Addresses() {
                if (ipv6Addresses == null) {
                    ipv6Addresses = new ArrayList<String>();
                }
                return this.ipv6Addresses;
            }

        }

    }

}
