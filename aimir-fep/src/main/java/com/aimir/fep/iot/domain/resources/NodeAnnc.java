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
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * nodeAnnc domain.
 * @author <ul>
 *         <li>Sang June Lee < blue4444eye@hanmail.net > < blue4444eye@gmail.com ></li>
 *         </ul>
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "nodeID",
    "hostedCSELink",
    "childResource",
    "memoryAnncOrBatteryAnncOrAreaNwkInfoAnnc"
})
@XmlRootElement(name = "nodeAnnc")
public class NodeAnnc
    extends AnnounceableResource
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String nodeID;
    protected String hostedCSELink;
    protected List<ChildResourceRef> childResource;
    @XmlElements({
        @XmlElement(name = "eventLogAnnc", namespace = "http://www.onem2m.org/xml/protocols", type = EventLogAnnc.class),
        @XmlElement(name = "rebootAnnc", namespace = "http://www.onem2m.org/xml/protocols", type = RebootAnnc.class),
        @XmlElement(name = "deviceInfoAnnc", namespace = "http://www.onem2m.org/xml/protocols", type = DeviceInfoAnnc.class),
        @XmlElement(name = "deviceCapabilityAnnc", namespace = "http://www.onem2m.org/xml/protocols", type = DeviceCapabilityAnnc.class),
        @XmlElement(name = "memoryAnnc", namespace = "http://www.onem2m.org/xml/protocols", type = MemoryAnnc.class),
        @XmlElement(name = "softwareAnnc", namespace = "http://www.onem2m.org/xml/protocols", type = SoftwareAnnc.class),
        @XmlElement(name = "areaNwkDeviceInfoAnnc", namespace = "http://www.onem2m.org/xml/protocols", type = AreaNwkDeviceInfoAnnc.class),
        @XmlElement(name = "subscription", namespace = "http://www.onem2m.org/xml/protocols", type = Subscription.class),
        @XmlElement(name = "areaNwkInfoAnnc", namespace = "http://www.onem2m.org/xml/protocols", type = AreaNwkInfoAnnc.class),
        @XmlElement(name = "batteryAnnc", namespace = "http://www.onem2m.org/xml/protocols", type = BatteryAnnc.class),
        @XmlElement(name = "firmwareAnnc", namespace = "http://www.onem2m.org/xml/protocols", type = FirmwareAnnc.class)
    })
    protected List<Resource> memoryAnncOrBatteryAnncOrAreaNwkInfoAnnc;

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

    /**
     * Gets the value of the memoryAnncOrBatteryAnncOrAreaNwkInfoAnnc property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the memoryAnncOrBatteryAnncOrAreaNwkInfoAnnc property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMemoryAnncOrBatteryAnncOrAreaNwkInfoAnnc().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EventLogAnnc }
     * {@link RebootAnnc }
     * {@link DeviceInfoAnnc }
     * {@link DeviceCapabilityAnnc }
     * {@link MemoryAnnc }
     * {@link SoftwareAnnc }
     * {@link AreaNwkDeviceInfoAnnc }
     * {@link Subscription }
     * {@link AreaNwkInfoAnnc }
     * {@link BatteryAnnc }
     * {@link FirmwareAnnc }
     * 
     * 
     */
    public List<Resource> getMemoryAnncOrBatteryAnncOrAreaNwkInfoAnnc() {
        if (memoryAnncOrBatteryAnncOrAreaNwkInfoAnnc == null) {
            memoryAnncOrBatteryAnncOrAreaNwkInfoAnnc = new ArrayList<Resource>();
        }
        return this.memoryAnncOrBatteryAnncOrAreaNwkInfoAnnc;
    }

}
