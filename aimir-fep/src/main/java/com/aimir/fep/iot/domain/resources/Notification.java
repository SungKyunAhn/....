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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * notification domain.
 * @author <ul>
 *         <li>Sang June Lee < blue4444eye@hanmail.net > < blue4444eye@gmail.com ></li>
 *         </ul>
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "notification", propOrder = {
    "notificationEvent",
    "verificationRequest",
    "subscriptionDeletion",
    "subscriptionReference",
    "creator",
    "notificationForwardingURI"
})
public class Notification {

	@XmlElement(name = "nev")
    protected Notification.NotificationEvent notificationEvent;
	@XmlElement(name = "vrq")
    protected Boolean verificationRequest;
	@XmlElement(name = "sud")
    protected Boolean subscriptionDeletion;
	@XmlElement(name = "sur")
	@XmlSchemaType(name = "anyURI")
    protected String subscriptionReference;
    @XmlElement(name = "cr")
    protected String creator;
    @XmlElement(name = "nfu")
    @XmlSchemaType(name = "anyURI")
    protected String notificationForwardingURI;

    /**
     * Gets the value of the notificationEvent property.
     * 
     * @return
     *     possible object is
     *     {@link Notification.NotificationEvent }
     *     
     */
    public Notification.NotificationEvent getNotificationEvent() {
        return notificationEvent;
    }

    /**
     * Sets the value of the notificationEvent property.
     * 
     * @param value
     *     allowed object is
     *     {@link Notification.NotificationEvent }
     *     
     */
    public void setNotificationEvent(Notification.NotificationEvent value) {
        this.notificationEvent = value;
    }

    /**
     * Gets the value of the verificationRequest property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isVerificationRequest() {
        return verificationRequest;
    }

    /**
     * Sets the value of the verificationRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setVerificationRequest(Boolean value) {
        this.verificationRequest = value;
    }

    /**
     * Gets the value of the subscriptionDeletion property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSubscriptionDeletion() {
        return subscriptionDeletion;
    }

    /**
     * Sets the value of the subscriptionDeletion property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSubscriptionDeletion(Boolean value) {
        this.subscriptionDeletion = value;
    }

    /**
     * Gets the value of the subscriptionReference property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubscriptionReference() {
        return subscriptionReference;
    }

    /**
     * Sets the value of the subscriptionReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubscriptionReference(String value) {
        this.subscriptionReference = value;
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
     * Gets the value of the notificationForwardingURI property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNotificationForwardingURI() {
        return notificationForwardingURI;
    }

    /**
     * Sets the value of the notificationForwardingURI property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNotificationForwardingURI(String value) {
        this.notificationForwardingURI = value;
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
     *         &lt;element name="representation" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
     *         &lt;element name="resourceStatus" type="{http://www.onem2m.org/xml/protocols}resourceStatus" minOccurs="0"/>
     *         &lt;element name="operationMonitor" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="operation" type="{http://www.onem2m.org/xml/protocols}operation" minOccurs="0"/>
     *                   &lt;element name="originator" type="{http://www.onem2m.org/xml/protocols}ID" minOccurs="0"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
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
        "representation",
        "resourceStatus",
        "operationMonitor"
    })
    public static class NotificationEvent {

        protected Object representation;
        protected BigInteger resourceStatus;
        protected Notification.NotificationEvent.OperationMonitor operationMonitor;

        /**
         * Gets the value of the representation property.
         * 
         * @return
         *     possible object is
         *     {@link Object }
         *     
         */
        public Object getRepresentation() {
            return representation;
        }

        /**
         * Sets the value of the representation property.
         * 
         * @param value
         *     allowed object is
         *     {@link Object }
         *     
         */
        public void setRepresentation(Object value) {
            this.representation = value;
        }

        /**
         * Gets the value of the resourceStatus property.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getResourceStatus() {
            return resourceStatus;
        }

        /**
         * Sets the value of the resourceStatus property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setResourceStatus(BigInteger value) {
            this.resourceStatus = value;
        }

        /**
         * Gets the value of the operationMonitor property.
         * 
         * @return
         *     possible object is
         *     {@link Notification.NotificationEvent.OperationMonitor }
         *     
         */
        public Notification.NotificationEvent.OperationMonitor getOperationMonitor() {
            return operationMonitor;
        }

        /**
         * Sets the value of the operationMonitor property.
         * 
         * @param value
         *     allowed object is
         *     {@link Notification.NotificationEvent.OperationMonitor }
         *     
         */
        public void setOperationMonitor(Notification.NotificationEvent.OperationMonitor value) {
            this.operationMonitor = value;
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
         *         &lt;element name="operation" type="{http://www.onem2m.org/xml/protocols}operation" minOccurs="0"/>
         *         &lt;element name="originator" type="{http://www.onem2m.org/xml/protocols}ID" minOccurs="0"/>
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
            "operation",
            "originator"
        })
        public static class OperationMonitor {

            protected BigInteger operation;
            protected String originator;

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

        }

    }

}
