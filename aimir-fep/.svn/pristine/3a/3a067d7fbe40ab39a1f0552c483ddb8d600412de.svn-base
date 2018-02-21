package com.aimir.fep.meter.data;


/**
 * MeteringFail Data Class
 *
 * @author Yeon Kyoung Park (goodjob@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2007-10-15 15:59:15 +0900 $,
 */
public class MeteringFail implements java.io.Serializable
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 5754679390605271418L;
	private String timestamp = null;
    String modemId = null;
    private int modemErrCode = 0;
    private int meterErrCode = 0;
    private String modemErrCodeName = null;
    private String meterErrCodeName = null;
    private String msg = null;

    /**
     * Constructor
     */
    public MeteringFail()
    {
    }


    /**
     * get time
     * @return time - meter measurement time(meter time : hhmmss)
     */
    public String getTimestamp()
    {
        return this.timestamp;
    }
    /**
     * set time
     * @param time - meter measurement time(meter time : hhmmss)
     */
    public void setTimestamp(String timestamp)
    {
        this.timestamp = timestamp;
    }
    
    /**
     * set modemErrCode
     * @param modemErrCode - Measurment Data modemErrCode
     */
    public void setModemErrCode(int modemErrCode)
    {
        this.modemErrCode = modemErrCode;
    }    
    
    /**
     * set meterErrCode
     * @param meterErrCode - Measurment Data meterErrCode
     */
    public void setMeterErrCode(int meterErrCode)
    {
        this.meterErrCode = meterErrCode;
    } 
 
    /**
     * get modemErrCode
     * @return modemErrCode - Measurment Data modemErrCode
     */
    public int getModemErrCode()
    {
        return this.modemErrCode;
    }
    
    /**
     * get meterErrCode
     * @return meterErrCode - Measurment Data meterErrCode
     */
    public int getMeterErrCode()
    {
        return this.meterErrCode;
    }
    
    /**
     * set modemErrCodeName
     * @param modemErrCodeName - Measurment Data modemErrCodeName
     */
    public void setModemErrCodeName(String modemErrCodeName)
    {
        this.modemErrCodeName = modemErrCodeName;
    }    
    
    /**
     * set meterErrCodeName
     * @param meterErrCodeName - Measurment Data meterErrCodeName
     */
    public void setMeterErrCodeName(String meterErrCodeName)
    {
        this.meterErrCodeName = meterErrCodeName;
    } 
    
    /**
     * get modemErrCodeName
     * @return modemErrCodeName - Measurment Data modemErrCode
     */
    public String getModemErrCodeName()
    {
        return this.modemErrCodeName;
    }
    
    /**
     * get meterErrCodeName
     * @return meterErrCodeName - Measurment Data meterErrCode
     */
    public String getMeterErrCodeName()
    {
        return this.meterErrCodeName;
    }
    
    public void setMsg(String msg)
    {
    	this.msg = msg;
    }
    
    public String getMsg()
    {
    	return this.msg;
    }
    
    public String getModemId()
    {
        return this.modemId;
    }
    
    public void setModemId(String modemId)
    {
        this.modemId = modemId;
    }
    
    /**
     * get string
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("timestamp=[").append(timestamp).append("],");
        sb.append("modemId=[").append(modemId).append("],");
        sb.append("modemErrCode=[").append(modemErrCode).append("],");
        sb.append("modemErrCodeName=[").append(modemErrCodeName).append("],");
        sb.append("meterErrCode=[").append(meterErrCode).append("],");
        sb.append("meterErrCodeName=[").append(meterErrCodeName).append("],");
        sb.append("msg=[").append(msg).append(']');

        return sb.toString();
    }
}
