package com.aimir.fep.meter.data;


/**
 * meter event log Data Class
 *
 * @author Yeon Kyoung Park (goodjob@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2007-01-29 15:59:15 +0900 $,
 */
public class EventLogData implements java.io.Serializable
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -2501315156322564474L;
	private String date = null;
    private String time = null;
    private String kind = "STE";
    private int   flag = 0;
    private String msg = null;
    private String append = null;

    /**
     * Constructor
     */
    public EventLogData()
    {
    }

    /**
     * get date
     * @return date - meter measurement date(meter time : yyyymmdd)
     */
    public String getDate()
    {
        return this.date;
    }
    /**
     * set date
     * @param date - meter measurement date(meter time : yyyymmdd)
     */
    public void setDate(String date)
    {
        this.date = date;
    }

    /**
     * get time
     * @return time - meter measurement time(meter time : hhmmss)
     */
    public String getTime()
    {
        return this.time;
    }
    /**
     * set time
     * @param time - meter measurement time(meter time : hhmmss)
     */
    public void setTime(String time)
    {
        this.time = time;
    }
    
    /**
     * set flag
     * @param flag - Measurment Data Flag
     */
    public void setFlag(int flag)
    {
        this.flag = flag;
    }    
 
    /**
     * get flag
     * @return flag - Measurment Data Flag
     */
    public int getFlag()
    {
        return this.flag;
    }
    
    public void setMsg(String msg)
    {
    	this.msg = msg;
    }
    
    public String getMsg()
    {
    	return this.msg;
    }
    
    public String getKind()
    {
        return this.kind;
    }
    
    public void setKind(String kind)
    {
        this.kind = kind;
    }

    public String getAppend()
    {
        return this.append;
    }
    
    public void setAppend(String append)
    {
        this.append = append;
    }
    
    /**
     * get string
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("date=[").append(date).append("],");
        sb.append("time=[").append(time).append("],");
        sb.append("kind=[").append(kind).append("],");
        sb.append("flag=[").append(flag).append("],");
        sb.append("msg=[").append(msg).append(']');
        sb.append("append=[").append(append).append(']');

        return sb.toString();
    }
}
