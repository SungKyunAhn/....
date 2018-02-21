package com.aimir.fep.meter.data;


/**
 * Energy Measurement Data Class
 *
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-12-13 15:59:15 +0900 $,
 */
public class NDHistoryData implements java.io.Serializable
{
    /**
     * 
     */
    //private static final long serialVersionUID = 1L;
    private String mcuId = null;
    private int entryCount = 0;
    private byte[] mdData = null;

    /**
     * Constructor
     */
    public NDHistoryData()
    {
    }
    /**
     * Constructor
     *
     * @param entryCount - MeasurementDataEntry Count
     * @param mdData - Measurement Data 
     */
    public NDHistoryData(String mcuId,int entryCount, byte[] mdData)
    {
        this.mcuId = mcuId;
        this.entryCount = entryCount;
        this.mdData = mdData;
    }

    /**
     * Constructor
     *
     * @param entryCount - MeasurementDataEntry Count
     * @param mdData - Measurement Data 
     */
    public NDHistoryData(int entryCount, byte[] mdData)
    {
        this.entryCount = entryCount;
        this.mdData = mdData;
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
