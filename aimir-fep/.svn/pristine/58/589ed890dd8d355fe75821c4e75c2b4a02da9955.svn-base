package com.aimir.fep.meter.parser.elsterA1700Table;

import java.util.HashMap;

public class A1700_LP_DATA_MARKER {

	private String type;
	private String dateTime; // yyyymmddhhmmss
	private int channel;
	private int period;
	
	private HashMap<String, String> periodTable = new HashMap<String, String>();

	public A1700_LP_DATA_MARKER() {
		setPeriodTable();
	}

	public void setPeriodTable() {
		periodTable.put("00", "1");
		periodTable.put("11", "2");
		periodTable.put("22", "3");
		periodTable.put("33", "4");
		periodTable.put("44", "5");
		periodTable.put("55", "6");
		periodTable.put("66", "10");
		periodTable.put("77", "15");
		periodTable.put("88", "20");
		periodTable.put("99", "30");
		periodTable.put("AA", "60");
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		this.channel = channel;
	}

	public int getPeriod() {
		return period;
	}

	public void setPeriod(int period) {
		this.period = period;
	}
	
	public HashMap<String, String> getLpPeriodTable() {
		return periodTable;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
        
        try {
            sb.append("("    )
              .append("TYPE="     ).append(getType()).append("/")
              .append("DATETIME=" ).append(getDateTime()).append("/")
              .append("CHANNEL="  ).append(getChannel()).append("/")
              .append("PERIOD="   ).append(getPeriod())
              .append(")");
        } catch (Exception e) { }
        
        return sb.toString();
	}
}
