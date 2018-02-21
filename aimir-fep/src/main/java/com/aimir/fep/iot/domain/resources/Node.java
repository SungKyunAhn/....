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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * node domain.
 * @author <ul>
 *         <li>Sang June Lee < blue4444eye@hanmail.net > < blue4444eye@gmail.com ></li>
 *         </ul>
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "nodeID",
    "hostedCSELink",
    "childResource",
    "firmware",
    "areaNwkInfo",
    "deviceCapability",
    "software",
    "subscription",
    "cmdhPolicy",
    "areaNwkDeviceInfo",
    "reboot",
    "activeCmdhPolicy",
    "eventLog",
    "deviceInfo",
    "battery",
    "memory"
})
@XmlRootElement(name = "nod", namespace="http://www.onem2m.org/xml/protocols")
public class Node
    extends AnnounceableResource
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@XmlElement(name = "ni", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String nodeID;
	@XmlElement(name = "hcl")
    protected String hostedCSELink;
	@XmlElement(name = "ch")
    protected List<ChildResourceRef> childResource;
    @XmlElement(name = "fwr", namespace = "http://www.onem2m.org/xml/protocols")
    protected List<Firmware> firmware;
    @XmlElement(name = "ani", namespace = "http://www.onem2m.org/xml/protocols")
    protected List<AreaNwkInfo> areaNwkInfo;
    @XmlElement(name = "dvc", namespace = "http://www.onem2m.org/xml/protocols")
    protected List<DeviceCapability> deviceCapability;
    @XmlElement(name = "swr", namespace = "http://www.onem2m.org/xml/protocols")
    protected List<Software> software;
    @XmlElement(name = "sub", namespace = "http://www.onem2m.org/xml/protocols")
    protected List<Subscription> subscription;
    @XmlElement(name = "cmp", namespace = "http://www.onem2m.org/xml/protocols")
    protected List<CmdhPolicy> cmdhPolicy;
    @XmlElement(name = "andi", namespace = "http://www.onem2m.org/xml/protocols")
    protected List<AreaNwkDeviceInfo> areaNwkDeviceInfo;
    @XmlElement(name = "rbt", namespace = "http://www.onem2m.org/xml/protocols")
    protected List<Reboot> reboot;
    @XmlElement(name = "acmp", namespace = "http://www.onem2m.org/xml/protocols")
    protected List<ActiveCmdhPolicy> activeCmdhPolicy;
    @XmlElement(name = "evl", namespace = "http://www.onem2m.org/xml/protocols")
    protected List<EventLog> eventLog;
    @XmlElement(name = "dvi", namespace = "http://www.onem2m.org/xml/protocols")
    protected List<DeviceInfo> deviceInfo;
    @XmlElement(name = "bat", namespace = "http://www.onem2m.org/xml/protocols")
    protected List<Battery> battery;
    @XmlElement(name = "mem", namespace = "http://www.onem2m.org/xml/protocols")
    protected List<Memory> memory;
    /*
    @XmlElements({
        @XmlElement(name = "firmware", namespace = "http://www.onem2m.org/xml/protocols", type = Firmware.class),
        @XmlElement(name = "areaNwkInfo", namespace = "http://www.onem2m.org/xml/protocols", type = AreaNwkInfo.class),
        @XmlElement(name = "deviceCapability", namespace = "http://www.onem2m.org/xml/protocols", type = DeviceCapability.class),
        @XmlElement(name = "software", namespace = "http://www.onem2m.org/xml/protocols", type = Software.class),
        @XmlElement(name = "subscription", namespace = "http://www.onem2m.org/xml/protocols", type = Subscription.class),
        @XmlElement(name = "cmdhPolicy", namespace = "http://www.onem2m.org/xml/protocols", type = CmdhPolicy.class),
        @XmlElement(name = "areaNwkDeviceInfo", namespace = "http://www.onem2m.org/xml/protocols", type = AreaNwkDeviceInfo.class),
        @XmlElement(name = "reboot", namespace = "http://www.onem2m.org/xml/protocols", type = Reboot.class),
        @XmlElement(name = "activeCmdhPolicy", namespace = "http://www.onem2m.org/xml/protocols", type = ActiveCmdhPolicy.class),
        @XmlElement(name = "eventLog", namespace = "http://www.onem2m.org/xml/protocols", type = EventLog.class),
        @XmlElement(name = "deviceInfo", namespace = "http://www.onem2m.org/xml/protocols", type = DeviceInfo.class),
        @XmlElement(name = "battery", namespace = "http://www.onem2m.org/xml/protocols", type = Battery.class),
        @XmlElement(name = "memory", namespace = "http://www.onem2m.org/xml/protocols", type = Memory.class)
    })
    protected List<RegularResource> memoryOrBatteryOrAreaNwkInfo;
    */

    /**
     * Gets the value of the nodeID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNodeID() {
        return nodeID;
    }

    /**
     * Sets the value of the nodeID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNodeID(String value) {
        this.nodeID = value;
    }

    /**
     * Gets the value of the hostedCSELink property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHostedCSELink() {
        return hostedCSELink;
    }

    /**
     * Sets the value of the hostedCSELink property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHostedCSELink(String value) {
        this.hostedCSELink = value;
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

    public List<Firmware> getFirmware() {
        if (firmware == null) {
        	firmware = new ArrayList<Firmware>();
        }
        return this.firmware;
    }
    
    public List<AreaNwkInfo> getAreaNwkInfo() {
        if (areaNwkInfo == null) {
        	areaNwkInfo = new ArrayList<AreaNwkInfo>();
        }
        return this.areaNwkInfo;
    }
    
    public List<DeviceCapability> getDeviceCapability() {
        if (deviceCapability == null) {
        	deviceCapability = new ArrayList<DeviceCapability>();
        }
        return this.deviceCapability;
    }
    
    public List<Software> getSoftware() {
        if (software == null) {
        	software = new ArrayList<Software>();
        }
        return this.software;
    }
    
    public List<Subscription> getSubscription() {
        if (subscription == null) {
        	subscription = new ArrayList<Subscription>();
        }
        return this.subscription;
    }
    
    public List<CmdhPolicy> getCmdhPolicy() {
        if (cmdhPolicy == null) {
        	cmdhPolicy = new ArrayList<CmdhPolicy>();
        }
        return this.cmdhPolicy;
    }
    
    public List<AreaNwkDeviceInfo> getAreaNwkDeviceInfo() {
        if (areaNwkDeviceInfo == null) {
        	areaNwkDeviceInfo = new ArrayList<AreaNwkDeviceInfo>();
        }
        return this.areaNwkDeviceInfo;
    }
    
    public List<Reboot> getReboot() {
        if (reboot == null) {
        	reboot = new ArrayList<Reboot>();
        }
        return this.reboot;
    }
    
    public List<ActiveCmdhPolicy> getActiveCmdhPolicy() {
        if (activeCmdhPolicy == null) {
        	activeCmdhPolicy = new ArrayList<ActiveCmdhPolicy>();
        }
        return this.activeCmdhPolicy;
    }
    
    public List<EventLog> getEventLog() {
        if (eventLog == null) {
        	eventLog = new ArrayList<EventLog>();
        }
        return this.eventLog;
    }
    
    public List<DeviceInfo> getDeviceInfo() {
        if (deviceInfo == null) {
        	deviceInfo = new ArrayList<DeviceInfo>();
        }
        return this.deviceInfo;
    }
    
    public List<Battery> getBattery() {
        if (battery == null) {
        	battery = new ArrayList<Battery>();
        }
        return this.battery;
    }
    
    public List<Memory> getMemory() {
        if (memory == null) {
        	memory = new ArrayList<Memory>();
        }
        return this.memory;
    }
    
    /**
     * Gets the value of the memoryOrBatteryOrAreaNwkInfo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the memoryOrBatteryOrAreaNwkInfo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMemoryOrBatteryOrAreaNwkInfo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Firmware }
     * {@link AreaNwkInfo }
     * {@link DeviceCapability }
     * {@link Software }
     * {@link Subscription }
     * {@link CmdhPolicy }
     * {@link AreaNwkDeviceInfo }
     * {@link Reboot }
     * {@link ActiveCmdhPolicy }
     * {@link EventLog }
     * {@link DeviceInfo }
     * {@link Battery }
     * {@link Memory }
     * 
     * 
     */
    /*
    public List<RegularResource> getMemoryOrBatteryOrAreaNwkInfo() {
        if (memoryOrBatteryOrAreaNwkInfo == null) {
            memoryOrBatteryOrAreaNwkInfo = new ArrayList<RegularResource>();
        }
        return this.memoryOrBatteryOrAreaNwkInfo;
    }
     */
}
