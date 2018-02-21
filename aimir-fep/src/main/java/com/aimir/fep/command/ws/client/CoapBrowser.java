
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
 * &lt;complexType name="coapBrowser">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Ipv4" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Ipv6" type="{http://www.w3.org/2001/XMLSchema}String"/>
 *         &lt;element name="Uri" type="{http://www.w3.org/2001/XMLSchema}String"/>
 *         &lt;element name="Query" type="{http://www.w3.org/2001/XMLSchema}String"/>
 *         &lt;element name="Config" type="{http://www.w3.org/2001/XMLSchema}String"/>
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
@XmlType(name = "coapBrowser", propOrder = {
    "ipv4",
    "ipv6",
	"uri",
    "query",
    "config",
    "type",
})
public class CoapBrowser {

    @XmlElement(name = "Ipv4")
    protected String ipv4;
    @XmlElement(name = "Ipv6")
    protected String ipv6;
    @XmlElement(name = "Uri")
    protected String uri;
    @XmlElement(name = "Query")
    protected String query;
    @XmlElement(name = "Config")
    protected String config;
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
    
    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
    
    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }
    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
