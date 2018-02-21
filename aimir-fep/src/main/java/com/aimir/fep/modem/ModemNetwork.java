package com.aimir.fep.modem;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * <pre>
 * &lt;complexType name="modemNetwork">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="channel" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="extPanId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="linkKey" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="manualEnable" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="networkKey" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="panId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="securityEnable" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="txPower" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "modemNetwork", propOrder = {
    "channel",
    "extPanId",
    "linkKey",
    "manualEnable",
    "networkKey",
    "panId",
    "securityEnable",
    "txPower"
})
public class ModemNetwork implements java.io.Serializable
{

	private static final long serialVersionUID = -2751264284193399283L;
	private int manualEnable;
    private int channel;
    private int panId;
    private int txPower;
    private int securityEnable;
    private String linkKey;
    private String networkKey;
    private String extPanId;
    
    public int getManualEnable()
    {
        return manualEnable;
    }
    public void setManualEnable(int manualEnable)
    {
        this.manualEnable = manualEnable;
    }
    public int getChannel()
    {
        return channel;
    }
    public void setChannel(int channel)
    {
        this.channel = channel;
    }
    public int getPanId()
    {
        return panId;
    }
    public void setPanId(int panId)
    {
        this.panId = panId;
    }
    public int getTxPower()
    {
        return txPower;
    }
    public void setTxPower(int txPower)
    {
        this.txPower = txPower;
    }
    public int getSecurityEnable()
    {
        return securityEnable;
    }
    public void setSecurityEnable(int securityEnable)
    {
        this.securityEnable = securityEnable;
    }
    
    public String getLinkKey()
    {
        return linkKey;
    }
    public void setLinkKey(String linkKey)
    {
        this.linkKey = linkKey;
    }
    public String getNetworkKey()
    {
        return networkKey;
    }
    public void setNetworkKey(String networkKey)
    {
        this.networkKey = networkKey;
    }
    public String getExtPanId()
    {
        return extPanId;
    }
    public void setExtPanId(String extPanId)
    {
        this.extPanId = extPanId;
    }
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("MANUAL_ENABLE[" + manualEnable + "]"
                   + ", CHANNEL[" + channel + "]"
                   + ", PANID[" + panId + "]"
                   + ", TXPOWER[" + txPower + "]"
                   + ", SECURITY_ENABLE[" + securityEnable + "]"
                   + ", LINK_KEY[" + linkKey + "]"
                   + ", NETWORK_KEY[" + networkKey + "]"
                   + ", EXT_PANID[" + extPanId + "]");
        
        return buf.toString();
    }
}
