package com.aimir.fep.meter.parser.elsterA1140Table;

public class A1140_BILLING_DATA_VO {

	private long timestamp;
	private int source; // yyyymmddhhmm
	private Double register;

	public A1140_BILLING_DATA_VO() { }

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public int getSource() {
		return source;
	}

	public void setSource(int source) {
		this.source = source;
	}

	public Double getRegister() {
		return register;
	}

	public void setRegister(Double register) {
		this.register = register;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
        
        try {
            sb.append("("    )
              .append("TIMESTAMP=").append(getTimestamp()).append("/")
              .append("SOURCE="   ).append(getSource()).append("/")
              .append("REGISTER=" ).append(getRegister()).append("/")
              .append(")");
        } catch (Exception e) { }
        
        return sb.toString();
	}
}
