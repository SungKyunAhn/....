package com.aimir.fep.protocol.fmp.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.aimir.constants.CommonConstants.Interface;
import com.aimir.constants.CommonConstants.McuType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.fep.util.FMPProperty;
import com.aimir.model.system.Location;
import com.aimir.model.system.TimeZone;

/**
 * this class is representd for target information 
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 * <pre>
 * &lt;complexType name="target">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="groupNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="interfaceType" type="{http://server.ws.command.fep.aimir.com/}interface" minOccurs="0"/>
 *         &lt;element name="ipAddr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ipv6Addr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="location" type="{http://server.ws.command.fep.aimir.com/}location" minOccurs="0"/>
 *         &lt;element name="fwRevision" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="fwVer" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="memberNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="meterModel" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="phoneNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="port" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="protocol" type="{http://server.ws.command.fep.aimir.com/}protocol" minOccurs="0"/>
 *         &lt;element name="retry" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="supplierId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="targetId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="targetType" type="{http://server.ws.command.fep.aimir.com/}mcuType" minOccurs="0"/>
 *         &lt;element name="timeout" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="version" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nameSpace" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="modemId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="meterId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "target", propOrder = {
    "groupNumber",
    "interfaceType",
    "ipAddr",
    "ipv6Addr",
    "location",
    "fwRevision",
    "fwVer",
    "memberNumber",
    "meterModel",
    "nameSpace",
    "phoneNumber",
    "port",
    "protocol",
    "retry",
    "supplierId",
    "targetId",
    "targetType",
    "timeout",
    "version",
    "receiverType",
    "receiverId",
    "modemId",
    "meterId",
    "timeZoneId"
})
public class Target
{
    protected String targetId = null;
    protected int port = Integer.parseInt(FMPProperty.getProperty("protocol.mcu.listenport","8000"));
    protected String version = FMPProperty.getProperty("protocol.version","0100");
    protected int timeout = Integer.parseInt(FMPProperty.getProperty("protocol.response.timeout","15"));
    protected int retry = Integer.parseInt(FMPProperty.getProperty("protocol.retry","3"));

    //default tcp
    protected Protocol protocol = Protocol.GPRS;
    protected McuType targetType = McuType.Indoor;
    protected String ipAddr = null;
    protected String ipv6Addr = null;
    protected String phoneNumber = null;
    // 모자계기용
    protected String groupNumber = null;
    protected String memberNumber = null;
    
    protected int meterModel;
    protected Location location = null;
    
    protected String fwVer = "";
    protected String fwRevision = "";
    
    protected String supplierId;
    
    protected Interface interfaceType = Interface.IF4;
    
    protected String receiverType = null;
    protected String receiverId = null;
    
    protected String nameSpace = null;
    protected String modemId = null;
    protected String meterId = null;
    
    protected String timeZoneId = null;
    
    /**
     * constructor
     */
    public Target()
    {
    }

    /**
     * set Target ID (MCU, Modem)
     *
     * @param targetId <code>String</code> Target ID
     */
    public void setTargetId(String targetId)
    {
        this.targetId = targetId;
    }
    /**
     * get Target ID
     *
     * @return targetId <code>String</code> Target ID
     */
    public String getTargetId()
    {
        return this.targetId;
    }

    /**
     * set Target listen port
     *
     * @param port <code>int</code> Target listen port 
     */
    public void setPort(int port)
    {
        this.port = port;
    }
    /**
     * get Target listen port
     *
     * @return port <code>int</code> Target listen port
     */
    public int getPort()
    {
        return this.port;
    }

    /**
     * set Protocol Version
     *
     * @return version <code>String</code> Protocol Version
     */
    public void setVersion(String version)
    {
        this.version = version;
    }
    /**
     * get Protocol Version
     *
     * @param version <code>String</code> Protocol Version
     */
    public String getVersion()
    {
        return this.version;
    } 

    /**
     * get Response Timeout(seconds)
     *
     * @return timeout <code>int</code> response timeout
     */
    public int getTimeout()
    {
        return this.timeout;
    }
    /**
     * set Response Timeout(seconds)
     *
     * @param timeout <code>int</code> response timeout
     */
    public void setTimeout(int timeout)
    {
        this.timeout = timeout;
    }

    /**
     * get Response Timeout(seconds)
     *
     * @return retry <code>int</code> retry
     */
    public int getRetry()
    {
        return this.retry;
    }

    /**
     * set Response Timeout(seconds)
     *
     * @param retry <code>int</code> retry
     */
    public void setRetry(int retry)
    {
        this.retry = retry;
    }

    /**
     * get protocol
     *
     * @return protocol <code>Protocol</code>
     */
    public Protocol getProtocol()
    {
        return this.protocol;
    }

    /**
     * set protocol
     *
     * @param transportType <code>Protocol</code>
     */
    public void setProtocol(Protocol protocol)
    {
        this.protocol = protocol;
    }
    
    /**
     * get target Type
     * @return targetId <code>String</code> target type
     */
    public McuType getTargetType()
    {
        return targetType;
    }
    
    /**
     * set target type
     * @param targetId <code>String</code> target type
     */
    public void setTargetType(McuType mcuType)
    {
        this.targetType = mcuType;
    }
    
    /**
     * set meter model
     * @param meterModel <code>int</code> meter model
     */
    public void setMeterModel(int meterModel)
    {
        this.meterModel = meterModel;
    }
    
    /**
     * get meter model
     * @return meterModel <code>int</code> meter model
     */
    public int getMeterModel()
    {
        return this.meterModel;
    }
    
    /**
     * get location
     * @return location <code>Location</code> location
     */
    public Location getLocation(){
    	return this.location;
    }
    
    /**
     * set location
     * @param location <code>Location</code> location
     */
    public void setLocation(Location location){
    	this.location = location;
    }
    
    /**
     * set FwVersion
     *
     * @param fwVersion <code>String</code> fwVer
     */
    public void setFwVer(String fwVer)
    {
        if (fwVer != null)
            this.fwVer = fwVer;
        else
            this.fwVer = FMPProperty.getProperty("pana.modem.fw.ver");
    }
    /**
     * get fwVer
     *
     * @return mcuSwVersion <code>String</code> McuSwVersion
     */
    public String getFwVer()
    {
        return this.fwVer;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getGroupNumber() {
        return groupNumber;
    }

    public void setGroupNumber(String groupNumber) {
        this.groupNumber = groupNumber;
    }

    public String getMemberNumber() {
        return memberNumber;
    }

    public void setMemberNumber(String memberNumber) {
        this.memberNumber = memberNumber;
    }

    public String getFwRevision() {
        return fwRevision;
    }

    public void setFwRevision(String fwRevision) {
        if (fwRevision != null)
            this.fwRevision = fwRevision;
        else
            this.fwRevision = "";
    }

    public Interface getInterfaceType() {
        return interfaceType;
    }

    public void setInterfaceType(Interface interfaceType) {
        this.interfaceType = interfaceType;
    }

	public String getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
	}

    public String getReceiverType() {
        return receiverType;
    }

    public void setReceiverType(String receiverType) {
        this.receiverType = receiverType;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

	public String getNameSpace() {
		return nameSpace;
	}

	public void setNameSpace(String nameSpace) {
		this.nameSpace = nameSpace;
	}

    public String getIpv6Addr() {
        return ipv6Addr;
    }

    public void setIpv6Addr(String ipv6Addr) {
        this.ipv6Addr = ipv6Addr;
    }
    
    
    public String getModemId() {
		return modemId;
	}

	public void setModemId(String modemId) {
		this.modemId = modemId;
	}
	
	public String getMeterId() {
		return meterId;
	}

	public void setMeterId(String meterId) {
		this.meterId = meterId;
	}
	
	public String getTimeZoneId() {
        return timeZoneId;
    }

    public void setTimeZoneId(String timeZoneId) {
        this.timeZoneId = timeZoneId;
    }

    @Override
    public String toString() {
    	return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
