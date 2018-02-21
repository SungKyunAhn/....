package com.aimir.fep.meter.data;

import java.text.DecimalFormat;

public class MultiPulseSensorData implements java.io.Serializable {

	private static final long serialVersionUID = 3170426746381652068L;
	final static DecimalFormat dformat = new DecimalFormat("#0.0000");
  
    private Double pulseValue = null;
    private Double pulse = null;
	private String datetime = null;//yyyymmddhhmm
	private int    channelCnt = 0;
    private Double ch[] = null;
    private int   flag = 0;
    
    /**
     * Constructor
     */
    public MultiPulseSensorData()
    {
    }

	public Double getPulseValue() {
		return pulseValue;
	}

	public void setPulseValue(Double pulseValue) {
		this.pulseValue = pulseValue;
	}

	public Double getPulse() {
		return pulse;
	}

	public void setPulse(Double pulse) {
		this.pulse = pulse;
	}

	public String getDatetime() {
		return datetime;
	}

	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}

	public int getChannelCnt() {
		return channelCnt;
	}

	public void setChannelCnt(int channelCnt) {
		this.channelCnt = channelCnt;
	}

	public Double[] getCh() {
		return ch;
	}

	public void setCh(Double[] ch) {
		this.ch = ch;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}
	
	
}
