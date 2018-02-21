package com.aimir.fep.meter.parser.a3rlnqTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.meter.data.PowerAlarmLogData;

public class A3_EV implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1495353583656818787L;
	private Log log = LogFactory.getLog(A3_EV.class);
	private byte[] rawData = null;

	private ST76 st76 = null;

	public A3_EV(byte[] rawData) {
		this.rawData = rawData;
		parse();
	}

	public void parse() {
		this.st76 = new ST76(rawData);
	}

	public EventLogData[] getEventLogData() {
		return this.st76.getEvent();
	}

	public PowerAlarmLogData[] getPowerAlarmLogData() {
		return null;
	}
}