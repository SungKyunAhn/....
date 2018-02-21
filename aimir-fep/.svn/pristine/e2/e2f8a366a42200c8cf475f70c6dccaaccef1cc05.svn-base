package com.aimir.fep.meter.data;

import com.aimir.constants.CommonConstants.LineType;

/**
 * 
 * @author choiEJ
 *
 */
public class PowerAlarmLogData extends EventLogData {
    /**
	 * 
	 */
	private static final long serialVersionUID = 3915130120878174756L;
	private String   closeDate = null;
    private String   closeTime = null;
    private LineType lineType  = null;

    /**
     * Constructor
     */
    public PowerAlarmLogData() { }

    public String getCloseDate() {
		return closeDate;
	}

	public void setCloseDate(String closeDate) {
		this.closeDate = closeDate;
	}

	public String getCloseTime() {
		return closeTime;
	}

	public void setCloseTime(String closeTime) {
		this.closeTime = closeTime;
	}

	public LineType getLineType() {
		return lineType;
	}

	public void setLineType(LineType lineType) {
		this.lineType = lineType;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
        
        try {
            sb.append("POWER_ALARM_LOG_DATA[")
              .append("  (CLOSE_DATE=" ).append(getCloseDate()).append("\n")
              .append("  (CLOSE_TIME=" ).append(getCloseTime()).append("\n")
              .append("  (LINE_TYPE="  ).append(getLineType().getName()).append("\n")
              .append("]");
        } catch (Exception e) { }
        
        return sb.toString();
    }
}
