package com.aimir.fep.modem;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for modemNode complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="modemNode">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="firmwareBuild" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="firmwareVersion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="hardwareVersion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nodeKind" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="protocolVersion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="resetCount" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="resetReason" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="softwareVersion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="solarADVolt" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="solarBDCVolt" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="solarChgBattVolt" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="spNetwork" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="zdzdInterfaceVersion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "modemNode", propOrder = {
    "firmwareBuild",
    "firmwareVersion",
    "hardwareVersion",
    "nodeKind",
    "protocolVersion",
    "resetCount",
    "resetReason",
    "softwareVersion",
    "solarADVolt",
    "solarBDCVolt",
    "solarChgBattVolt",
    "spNetwork",
    "zdzdInterfaceVersion"
})
public class ModemNode implements java.io.Serializable
{
	private static final long serialVersionUID = 6473050598432108735L;
	private String nodeKind = null;
    private String firmwareVersion = null;
    private String firmwareBuild = null;
    private String softwareVersion = null;
    private String hardwareVersion = null;
    private String protocolVersion = null;
    private String zdzdInterfaceVersion = null;
    private int resetCount;
    private int resetReason;
    private int spNetwork;
    private double solarADVolt;
    private double solarChgBattVolt;
    private double solarBDCVolt;
    
    public String getNodeKind()
    {
        return nodeKind;
    }
    public void setNodeKind(String nodeKind)
    {
        this.nodeKind = nodeKind;
    }
    public String getSoftwareVersion()
    {
        return softwareVersion;
    }
    public void setSoftwareVersion(String softwareVersion)
    {
        this.softwareVersion = softwareVersion;
    }
    public String getHardwareVersion()
    {
        return hardwareVersion;
    }
    public void setHardwareVersion(String hardwareVersion)
    {
        this.hardwareVersion = hardwareVersion;
    }
    public String getProtocolVersion()
    {
        return protocolVersion;
    }
    public void setProtocolVersion(String protocolVersion)
    {
        this.protocolVersion = protocolVersion;
    }
    public String getZdzdInterfaceVersion()
    {
        return zdzdInterfaceVersion;
    }
    public void setZdzdInterfaceVersion(String zdzdInterfaceVersion)
    {
        this.zdzdInterfaceVersion = zdzdInterfaceVersion;
    }
    public int getResetCount()
    {
        return resetCount;
    }
    public void setResetCount(int resetCount)
    {
        this.resetCount = resetCount;
    }
    public int getResetReason()
    {
        return resetReason;
    }
    public void setResetReason(int resetReason)
    {
        this.resetReason = resetReason;
    }
    public String getFirmwareVersion()
    {
        return firmwareVersion;
    }
    public void setFirmwareVersion(String firmwareVersion)
    {
        this.firmwareVersion = firmwareVersion;
    }
    public String getFirmwareBuild()
    {
        return firmwareBuild;
    }
    public void setFirmwareBuild(String firmwareBuild)
    {
        this.firmwareBuild = firmwareBuild;
    }
    
    public int getSpNetwork()
    {
        return spNetwork;
    }
    public void setSpNetwork(int spNetwork)
    {
        this.spNetwork = spNetwork;
    }
    public double getSolarADVolt()
    {
        return solarADVolt;
    }
    public void setSolarADVolt(double solarADVolt)
    {
        this.solarADVolt = solarADVolt;
    }
    public double getSolarChgBattVolt()
    {
        return solarChgBattVolt;
    }
    public void setSolarChgBattVolt(double solarChgBattVolt)
    {
        this.solarChgBattVolt = solarChgBattVolt;
    }
    public double getSolarBDCVolt()
    {
        return solarBDCVolt;
    }
    public void setSolarBDCVolt(double solarBDCVolt)
    {
        this.solarBDCVolt = solarBDCVolt;
    }
    public String toString() {
        StringBuffer buf = new StringBuffer();
        
        buf.append("NODEKIND[" + nodeKind + "]"
                   + ", FIRMWAREVERSION[" + firmwareVersion + "]"
                   + ", FIRWAREBUILD[" + firmwareBuild + "]"
                   + ", SOFTWAREVERSION[" + softwareVersion + "]"
                   + ", HARDWAREVERSION[" + hardwareVersion + "]"
                   + ", PROTOCOLVERSION[" + protocolVersion + "]"
                   + ", ZDZDINTERFACEVERSION[" + zdzdInterfaceVersion + "]"
                   + ", RESETCOUNT[" + resetCount + "]"
                   + ", RESETREASON[" + resetReason + "]"
                   + ", SPNETWORK[" + spNetwork + "]"
                   + ", SOLARADVOLT[" + solarADVolt + "]"
                   + ", SOLARCHGBATTVOLT[" + solarChgBattVolt + "]"
                   + ", SOLARBDCVOLT[" + solarBDCVolt + "]");
        
        return buf.toString();
    }
}
