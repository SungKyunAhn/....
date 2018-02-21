package com.aimir.fep.meter.data;

import java.text.DecimalFormat;

public class EnvData implements java.io.Serializable {

	private static final long serialVersionUID = 3170426746381652068L;
	final static DecimalFormat dformat = new DecimalFormat("#0.0000");

	private String sensorId = null;    
	private String datetime = null;//yyyymmddhhmm
	private int    channelCnt = 0;
    private Double ch[] = null;
    
    /**
     * Constructor
     */
    public EnvData()
    {
    }

	public String getSensorId() {
		return sensorId;
	}

	public void setSensorId(String sensorId) {
		this.sensorId = sensorId;
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
}
