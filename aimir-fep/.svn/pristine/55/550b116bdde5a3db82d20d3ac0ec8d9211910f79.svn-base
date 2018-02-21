package com.aimir.fep.meter.data;


/**
 * Heat Measurement Data Class
 * Volume Corrector using too.
 * @author Y.K Park (goodjob@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2008-05-16 09:59:15 +0900 $,
 */
public class HMData implements java.io.Serializable
{
    private String customerId = null;
    private String meterId = null;
    private String date = null;
    private String time = null;
    private int    channelCnt = 0;
    private String kind = null;
    private Double ch[] = null;
    private int   flag = 0;

    /**
     * Constructor
     */
    public HMData()
    {
        resetData();
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
    public HMData(String customerId, String meterId,
                  String date,String time,
                  int channelCnt, String kind,
                  Double[] ch, int flag)
    {
        this.customerId = customerId;
        this.meterId = meterId;
        this.date = date;
        this.time = time;        
        this.channelCnt = channelCnt;
        this.kind = kind;
        this.ch = ch;
        this.flag = flag;
    }

    public void resetData()
    {
        customerId = null;
        meterId = null;
        date = null;
        time = null;
        channelCnt = 0;
        kind = null;
        ch = null;
        flag = 0;
    }
    
    public int getChannelCnt()
    {
        return channelCnt;
    }

    public void setChannelCnt(int channelCnt)
    {
        this.channelCnt = channelCnt;
        ch = new Double[this.channelCnt];
        for(int i = 0; i < this.channelCnt; i++)
        {
            ch[i] = new Double(0);
        }
    }

    public String getCustomerId()
    {
        return customerId;
    }

    public void setCustomerId(String customerId)
    {
        this.customerId = customerId;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public int getFlag()
    {
        return flag;
    }

    public void setFlag(int flag)
    {
        this.flag = flag;
    }

    public String getKind()
    {
        return kind;
    }

    public void setKind(String kind)
    {
        this.kind = kind;
    }

    public Double[] getCh()
    {
        return ch;
    }

    public void setCh(Double[] ch)
    {
        this.ch = ch;
    }
    
    public void setCh(int chNo, Double chValue)
    {
        ch[chNo-1] = chValue;
    }

    public String getMeterId()
    {
        return meterId;
    }

    public void setMeterId(String meterId)
    {
        this.meterId = meterId;
    }

    public String getTime()
    {
        return time;
    }

    public void setTime(String time)
    {
        this.time = time;
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
        sb.append("channelCnt=[").append(channelCnt).append("],");
        sb.append("kind=[").append(kind).append("],");
        if (ch != null) {
	        for(int i = 0; i < ch.length;i++){
	            sb.append("ch"+(i+1)+"=[").append(ch[i]).append("],");
	        }
        }
        else sb.append("ch=[null]");
        sb.append("flag=[").append(flag).append("]\n");

        return sb.toString();
    }
}
