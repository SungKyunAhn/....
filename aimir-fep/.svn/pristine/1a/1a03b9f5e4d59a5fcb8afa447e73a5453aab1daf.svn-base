package com.aimir.fep.meter.data;


/**
 * Energy Measurement Data Class
 *
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-12-13 15:59:15 +0900 $,
 */
public class MDHistoryData implements java.io.Serializable
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -3990400273559950015L;
	private String mcuId = null;
    private int entryCount = 0;
    private byte[] mdData = null;
    private String nameSpace = null;
    private String ipAddr = null;
    private String protocolType = null;
    
    /**
     * Constructor
     */
    public MDHistoryData()
    {
    }
    /**
     * Constructor
     *
     * @param entryCount - MeasurementDataEntry Count
     * @param mdData - Measurement Data 
     */
    public MDHistoryData(String mcuId,int entryCount, byte[] mdData, String ns, String ipAddr, String protocolType)
    {
        this.mcuId = mcuId;
        this.entryCount = entryCount;
        this.mdData = mdData;
        this.nameSpace = ns;
        this.ipAddr = ipAddr;
        this.protocolType = protocolType;
    }

    /**
     * Constructor
     *
     * @param entryCount - MeasurementDataEntry Count
     * @param mdData - Measurement Data 
     */
    public MDHistoryData(int entryCount, byte[] mdData, String ns, String ipAddr, String protocolType)
    {
        this.entryCount = entryCount;
        this.mdData = mdData;
        this.nameSpace = ns;
        this.ipAddr = ipAddr;
        this.protocolType = protocolType;
    }
    
    /**
     * get MCU Id
     *
     */
    public String getMcuId()
    {
        return this.mcuId;
    }
    public void setMcuId(String data)
    {
        this.mcuId = data;
    }

    /**
     * get Entry Count
     * @return entryCount - measurement data entry count 
     */
    public int getEntryCount()
    {
        return this.entryCount;
    }

    /**
     * set Entry Count
     * @param entryCount - measurement data entry count
     */
    public void setEntryCount(int entryCount)
    {
        this.entryCount = entryCount;
    }

    public String getIpAddr() {
        return ipAddr;
    }
    
    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }
    
    public String getProtocolType() {
        return protocolType;
    }
    
    public void setProtocolType(String protocolType) {
        this.protocolType = protocolType;
    }
    /**
     * get Measurement Data 
     * @return mdData - measurement data
     */
    public byte[] getMdData()
    {
        return this.mdData;
    }
    /**
     * set Measurement Data 
     * @param mdData - measurement data
     */
    public void setMdData(byte[] mdData)
    {
        this.mdData = mdData;
    }

    public void reset()
    {
        this.entryCount = 0;
        this.mdData = null;
    }
    public String getNameSpace() {
        return nameSpace;
    }
    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }
    
}
