package com.aimir.fep.meter.data;


/**
 * Energy Measurement Data Class
 *
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-12-13 15:59:15 +0900 $,
 */
public class EMData implements java.io.Serializable
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 7099209257574446227L;
	private String customerId = null;
    private String meterId = null;
    private String date = null;
    private String time = null;
    private int    lpChannelCnt = 0;
    private Double lp = null;
    private Double lpValue = null;
    private Double ch[] = null;
    private Double v[] = null;
    private Double pf = null;
    private int   flag = 0;

    /**
     * Constructor
     */
    public EMData()
    {
    }

    /**
     * Constructor
     *
     * @param customerid - customer identifier
     * @param date -  date(yyyymmdd)
     * @param time -  time(hhmmss)
     * @param lp -  load profile(pulse data)
     * @param lpValue -  load profile value
     */
    public EMData(String customerId, String meterId,String date,
            String time, Double lp, Double lpValue)
    {
        this.customerId = customerId;
        this.meterId = meterId;
        this.date = date;
        this.time = time;
        this.lp = lp;
        this.lpValue = lpValue;
    }
    
    /**
     * Constructor
     *
     * @param customerid - customer identifier
     * @param date -  date(yyyymmdd)
     * @param time -  time(hhmmss)
     * @param lp -  load profile(pulse data)
     * @param lpValue -  load profile value
     */
    public EMData(String customerId, String meterId,String date,
            String time, Double[] ch, Double[] v)
    {
        this.customerId = customerId;
        this.meterId = meterId;
        this.date = date;
        this.time = time;
        this.ch = ch;
        this.v = v;
    }

    /**
     * Constructor
     *
     * @param date -  date(yyyymmdd)
     * @param time -  time(hhmmss)
     * @param lp -  load profile(pulse data)
     * @param lpValue -  load profile value
     */
    public EMData(String date,String time, Double lp, Double lpValue)
    {
        this.date = date;
        this.time = time;
        this.lp = lp;
        this.lpValue = lpValue;
    }
        
    /**
     * Constructor
     *
     * @param date -  date(yyyymmdd)
     * @param time -  time(hhmmss)
     * @param lp -  load profile(pulse data)
     * @param lpValue -  load profile value
     */
    public EMData(String date,String time, Double[] ch, Double[] v)
    {
        this.date = date;
        this.time = time;
        this.ch  = ch;
        this.v   = v;
    }

    /**
     * Constructor
     *
     * @param date -  date(yyyymmdd)
     * @param time -  time(hhmmss)
     * @param lp -  load profile(pulse data)
     */
    public EMData(String date, String time, Double lp)
    {
        this.date = date;
        this.time = time;
        this.lp = lp;
    }

    /**
     * get Customer ID
     * @return customerId - customer identifier 
     */
    public String getCustomerId()
    {
        return this.customerId;
    }

    /**
     * set Customer ID
     * @param customerId - customer identifier 
     */
    public void setCustomerId(String customerId)
    {
        this.customerId = customerId;
    }

    /**
     * get Meter Id
     * @return meterId - meter identifier(default : meter number) 
     */
    public String getMeterId()
    {
        return this.meterId;
    }
    /**
     * set Meter Id
     * @param meterId - meter identifier(default : meter number) 
     */
    public void setMeterId(String meterId)
    {
        this.meterId = meterId;
    }

    /**
     * get date
     * @return date - meter measurement date(mcu time : yyyymmdd)
     */
    public String getDate()
    {
        return this.date;
    }
    /**
     * set date
     * @param date - meter measurement date(mcu time : yyyymmdd)
     */
    public void setDate(String date)
    {
        this.date = date;
    }

    /**
     * get time
     * @return time - meter measurement time(mcu time : hhmmss)
     */
    public String getTime()
    {
        return this.time;
    }
    /**
     * set time
     * @param time - meter measurement time(mcu time : hhmmss)
     */
    public void setTime(String time)
    {
        this.time = time;
    }
    
    /**
     * get lp
     * @return lp - load profile (pulse data)
     */
    public Double getLp()
    {
        return this.lp;
    }

    /**
     * set lp
     * @param lp - load profile (pulse data)
     */
    public void setLp(Double lp)
    {
        this.lp = lp;
    }

    /**
     * get lp value
     * @return lpValue- load profile value(lp * pulse divider)
     */
    public Double getLpValue()
    {
        return this.lpValue;
    }

    /**
     * set lp value
     * @param lpValue- load profile value(lp * pulse divider)
     */
    public void setLpValue(Double lpValue)
    {
        this.lpValue = lpValue;
    }
    
    public int getLPChannelCnt()
    {
        return this.lpChannelCnt;
    }
    
    public void setLPChannelCnt(int lpChannelCnt)
    {
        this.lpChannelCnt = lpChannelCnt;
    }
    
    public Double[] getCh()
    {
    	return this.ch;
    }
    
    public void setCh(Double[] ch)
    {
    	this.ch = ch;
    }
    
    public Double[] getV()
    {
    	return this.v;
    }
    
    public void setV(Double[] v)
    {
    	this.v = v;
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

    public void setPF(Double pf)
    {
        this.pf = pf;
    }

    public Double getPF()
    {
        return this.pf;
    }


    public void resetData()
    {
        customerId = null;
        meterId = null;
        date = null;
        time = null;
        lp = null;
        lpValue = null;
        ch = null;
        v = null;
        pf = null;
        flag = 0;
    }

    /**
     * get string
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("cutomerId=[").append(customerId).append("],");
        sb.append("meterId=[").append(meterId).append("],");
        sb.append("date=[").append(date).append("],");
        sb.append("time=[").append(time).append("],");
        sb.append("lp=[").append(lp).append("],");
        sb.append("lpValue=[").append(lpValue).append("],");
        for(int i = 0; i < ch.length;i++){
            sb.append("ch"+(i+1)+"=[").append(ch[i]).append("],");
            sb.append("v"+(i+1)+"=[").append(v[i]).append("],");
        }
        sb.append("pf=[").append(pf).append("],");
        sb.append("flag=[").append(flag).append("]\n");

        return sb.toString();
    }
}
