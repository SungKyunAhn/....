
package com.aimir.fep.command.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for coapPing complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="coapPing">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Ipv4" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Ipv6" type="{http://www.w3.org/2001/XMLSchema}String"/>
 *         &lt;element name="Type" type="{http://www.w3.org/2001/XMLSchema}String"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "modemReset", propOrder = {
    "ipv4",
    "ipv6",
    "type",
})
public class ModemReset {

    @XmlElement(name = "Ipv4")
    protected String ipv4;
    @XmlElement(name = "Ipv6")
    protected String ipv6;
    @XmlElement(name = "Type")
    protected String type;


    public String getIpv4() {
        return ipv4;
    }

    public void setIpv4(String ipv4) {
        this.ipv4 = ipv4;
    }
    
    public String getIpv6() {
        return ipv6;
    }

    public void setIpv6(String ipv6) {
        this.ipv6 = ipv6;
    }
    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
