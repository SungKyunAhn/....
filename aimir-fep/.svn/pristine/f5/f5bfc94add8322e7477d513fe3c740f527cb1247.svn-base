package com.aimir.fep.meter.data;

import java.text.DecimalFormat;


/**
 * LP Data Class
 *
 */
public class LPData implements java.io.Serializable
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 4096036495449665993L;
	private String datetime = null;//yyyymmddhhmm
    private int    lpChannelCnt = 0;
    private Double lp = null;
    private Double lpValue = null;
    private Double ch[] = null;
    private Double v[] = null;
    private long basePulse = -1;
    private double baseValue = -1;
    private Double pf = null;
    private int   flag = 0;
    private String status = null;
    final static DecimalFormat dformat = new DecimalFormat("#0.0000");
    
    /**
     * Constructor
     */
    public LPData()
    {
    }

    /**
     * Constructor
     *
     * @param datetime -  date(yyyymmddhhmm)
     * @param lp -  load profile(pulse data)
     * @param lpValue -  load profile value
     */
    public LPData(String datetime,
                  Double lp, Double lpValue)
    {
        this.datetime = datetime;
        this.lp = lp;
        this.lpValue = lpValue;
    }

    /**
     * Constructor
     *
     * @param datetime -  date(yyyymmddhhmm)
     * @param lp -  load profile(pulse data)
     */
    public LPData(String datetime, Double lp)
    {
        this.datetime = datetime;
        this.lp = lp;
    }

    /**
     * get date
     * @return date - meter measurement date(mcu time : yyyymmddhhmm)
     */
    public String getDatetime()
    {
        return this.datetime;
    }
    /**
     * set datetime
     * @param datetime - meter measurement date(mcu time : yyyymmddhhmm)
     */
    public void setDatetime(String datetime)
    {
        this.datetime = datetime;
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
        this.lp = new Double(dformat.format(lp));
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
        this.lpValue = new Double(dformat.format(lpValue));
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

    public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void resetData()
    {
        datetime = null;
        lp = null;
        lpValue = null;
        ch = null;
        v = null;
        pf = null;
        flag = 0;
        status = null;
    }

    /**
     * get string
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("datetime=[").append(datetime).append("],");
        sb.append("lp=[").append(lp).append("],");
        sb.append("lpValue=[").append(lpValue).append("],");
        for(int i = 0; i < ch.length;i++){
            if (ch[i] instanceof Number)
                sb.append("ch"+(i+1)+"=[").append(dformat.format(ch[i])).append("],");
            else
                sb.append("ch"+(i+1)+"=[").append(ch[i]).append("],");
        }
        if (pf != null && pf instanceof Double)
        	sb.append("pf=[").append(dformat.format(pf)).append("],");
        sb.append("flag=[").append(flag).append("],");
        sb.append("status=[").append(status).append("]\n");

        return sb.toString();
    }

    /**
     * @return the basePulse
     */
    public long getBasePulse()
    {
        return basePulse;
    }

    /**
     * @param basePulse the basePulse to set
     */
    public void setBasePulse(long basePulse)
    {
        this.basePulse = basePulse;
    }

	public double getBaseValue() {
		return baseValue;
	}

	public void setBaseValue(double baseValue) {
		this.baseValue = baseValue;
	}
    
    
}
