package com.aimir.fep.meter.data;


/**
 * Measurement Data Class
 *
 */
public class AMUMDHistoryData implements java.io.Serializable
{
    private String sourceAddr = null;
    private String destAddr = null;
    private int entryCount = 0;
    private byte[] mdData = null;

    /**
     * Constructor
     */
    public AMUMDHistoryData()
    {
    }
    /**
     * Constructor
     *
     * @param entryCount - MeasurementDataEntry Count
     * @param mdData - Measurement Data 
     */
    public AMUMDHistoryData(String sourceAddr, String destAddr,int entryCount, byte[] mdData)
    {
        this.sourceAddr = sourceAddr;
        this.destAddr = destAddr;
        this.entryCount = entryCount;
        this.mdData = mdData;
    }

    /**
     * Constructor
     *
     * @param entryCount - MeasurementDataEntry Count
     * @param mdData - Measurement Data 
     */
    public AMUMDHistoryData(int entryCount, byte[] mdData)
    {
        this.entryCount = entryCount;
        this.mdData = mdData;
    }
    
    public String getSourceAddr() {
        return sourceAddr;
    }
    public void setSourceAddr(String sourceAddr) {
        this.sourceAddr = sourceAddr;
    }
    public String getDestAddr() {
        return destAddr;
    }
    public void setDestAddr(String destAddr) {
        this.destAddr = destAddr;
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
}
