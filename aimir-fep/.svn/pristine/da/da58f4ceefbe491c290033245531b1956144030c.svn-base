package com.aimir.fep.meter.parser.elsterA1140Table;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.constants.CommonConstants.LineType;
import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.meter.data.PowerAlarmLogData;
import com.aimir.fep.meter.parser.ElsterA1140;
import com.aimir.fep.meter.parser.elsterA1140Table.EVENT_ATTRIBUTE.EVENTATTRIBUTE;
import com.aimir.fep.util.DataFormat;

/**
 * 
 * @author choiEJ
 *
 */
public class A1140_EVENT_LOG {    
	private Log log = LogFactory.getLog(A1140_EVENT_LOG.class);
    
    
    public static final int OFS_REVERSE_RUNNING = 0;
    public static final int OFS_PHASE_FAILURE   = 14;
    public static final int OFS_POWER_FAIL      = 31;
    public static final int OFS_POWER_DOWN      = 45;
    
    
    public static final int LEN_REVERSE_RUNNING = 14;
    public static final int LEN_PHASE_FAILURE   = 17;
    
    public static final int LEN_POWER_FAIL      = 14;
    public static final int LEN_POWER_DOWN      = 14;
    
    
    public static final int LEN_EVENT_COUNT      = 2;
    public static final int LEN_EVENT_TIMESTAMP  = 4;
    
	private byte[] rawData = null;
    
	List<EventLogData>      meterEventList = new ArrayList<EventLogData>();
	List<PowerAlarmLogData> powerAlarmList = new ArrayList<PowerAlarmLogData>();
	
	/**
	 * Constructor
	 */
	public A1140_EVENT_LOG(byte[] rawData) {
        this.rawData = rawData;
        
        try {
            parseMeterEventLog();
            parsePowerAlarmLog();
        }
        catch (Exception e) {
            log.error(e, e);
        }
	}
	
	public static void main(String[] args) throws Exception {
		A1140_TEST_DATA testData = new A1140_TEST_DATA();
		A1140_EVENT_LOG elster = new A1140_EVENT_LOG(testData.getTestData_event());
		elster.parseMeterEventLog();
		//elster.parsePowerAlarmLog();
		System.out.println(elster.toString());
/*		PowerAlarmLog powerAlarmLog = new PowerAlarmLog();
		powerAlarmLog.setLineType("A");
		System.out.println(powerAlarmLog.getLineType().getName());*/
	}
	
	public List<EventLogData> getMeterEventLog() {
		return meterEventList;
	}
	
	public List<PowerAlarmLogData> getPowerAlarmLog() throws Exception {
//		parsePowerAlarmLog();		
		if (powerAlarmList != null) {
			return powerAlarmList;
		} else {
			return null;
		}
	}
	
	private void parseMeterEventLog() throws Exception {
		getReverseRunning();
		getPhaseFailure();	//46Page
		getPowerFail();
		//getPowerDown();	powerDown이 아니라 Programming Cumulative Event
	}
	
	private void parsePowerAlarmLog() throws Exception {
		getPhaseFailure();
		getPowerFail();
		//getPowerDown();	powerDown이 아니라 Programming Cumulative Event
		
	}
	
	private void getPhaseFailure() throws Exception {
		byte[] data = DataFormat.select(rawData, OFS_PHASE_FAILURE, LEN_PHASE_FAILURE);
		
		int offset = 0;
		String dateTime = "";
		
		EventLogData      eventLog      = new EventLogData();
		PowerAlarmLogData powerAlarmLog = new PowerAlarmLogData();
		
		int count = convertInt2("PHASE_FAILURE_COUNT", DataFormat.select(data, offset, LEN_EVENT_COUNT));

		//이벤트가 없을시 리턴
		if(count < 1) return;
		
		offset += LEN_EVENT_COUNT;
		
		eventLog.setFlag(EVENTATTRIBUTE.PHASE_FAILURE.getCode());
		eventLog.setMsg(EVENTATTRIBUTE.PHASE_FAILURE.getName());
		eventLog.setAppend(EVENTATTRIBUTE.PHASE_FAILURE.getName());
		
		powerAlarmLog.setFlag(EVENTATTRIBUTE.PHASE_FAILURE.getCode());
		powerAlarmLog.setMsg(EVENTATTRIBUTE.PHASE_FAILURE.getName());
				
		dateTime = ElsterA1140.convertTimestamp("PHASE_FAILURE_TIMESTAMP",DataFormat.select(data, offset, LEN_EVENT_TIMESTAMP));
		offset += LEN_EVENT_TIMESTAMP * 3;       // 뒤로 이전 4개의 시간 데이터가 오지만 필요없음
		
		int phaseType = DataFormat.getIntToBytes(DataFormat.select(data, offset, 1));
		
		eventLog.setDate(dateTime.substring(0, 8));               // yyyymmdd
		eventLog.setTime(dateTime.substring(8));                  // hhmmss
		powerAlarmLog.setDate(dateTime.substring(0, 8));          // yyyymmdd
		powerAlarmLog.setTime(dateTime.substring(8));             // hhmmss
		//Phase Failure, LineType Save
		if (phaseType == 1) {
			powerAlarmLog.setLineType(LineType.A);
		} else if (phaseType == 2) {
			powerAlarmLog.setLineType(LineType.B);
		} else if (phaseType == 3) {
			powerAlarmLog.setLineType(LineType.AB);
		} else if (phaseType == 4) {
			powerAlarmLog.setLineType(LineType.C);
		} else if (phaseType == 5) {
			powerAlarmLog.setLineType(LineType.AC);
		} else if (phaseType == 6) {
			powerAlarmLog.setLineType(LineType.BC);
		} else if (phaseType == 7) {
			powerAlarmLog.setLineType(LineType.ABC);
		}
		
		meterEventList.add(eventLog);
		powerAlarmList.add(powerAlarmLog);
	}
	
	private void getReverseRunning() throws Exception {
		log.debug("START-----getReverseRunning()");
		
		byte[] data = DataFormat.select(rawData, OFS_REVERSE_RUNNING, LEN_REVERSE_RUNNING);
		
		int offset = 0;
		String dateTime = "";
		
		EventLogData      eventLog      = new EventLogData();
		PowerAlarmLogData powerAlarmLog = new PowerAlarmLogData();

		int count = convertInt2("REVERSE_RUNNING_EVENT_COUNT", DataFormat.select(data, offset, LEN_EVENT_COUNT));
		
		//이벤트가 없을시 리턴
		if(count < 1) return;
		
		offset += LEN_EVENT_COUNT;
		
		eventLog.setFlag(EVENTATTRIBUTE.REVERSE_RUNNING.getCode());
		eventLog.setMsg(EVENTATTRIBUTE.REVERSE_RUNNING.getName());
		eventLog.setAppend(EVENTATTRIBUTE.REVERSE_RUNNING.getName());
		
		powerAlarmLog.setFlag(EVENTATTRIBUTE.REVERSE_RUNNING.getCode());
		powerAlarmLog.setMsg(EVENTATTRIBUTE.REVERSE_RUNNING.getName());

		dateTime = ElsterA1140.convertTimestamp("REVERSE_RUN_TIMESTAMP", DataFormat.select(data, offset, LEN_EVENT_TIMESTAMP));
		offset += LEN_EVENT_TIMESTAMP * 3;       // 뒤로 이전 4개의 시간 데이터가 오지만 필요없음
		
		eventLog.setDate(dateTime.substring(0, 8));               // yyyymmdd
		eventLog.setTime(dateTime.substring(8));                  // hhmmss
		powerAlarmLog.setDate(dateTime.substring(0, 8));          // yyyymmdd
		powerAlarmLog.setTime(dateTime.substring(8));             // hhmmss
		
		meterEventList.add(eventLog);
		powerAlarmList.add(powerAlarmLog);
	}
	
	private void getPowerDown() throws Exception {
		log.debug("START-----getPowerDown()");

		byte[] data = DataFormat.select(rawData, OFS_POWER_DOWN, LEN_POWER_DOWN);
		
		int offset = 0;
		String dateTime = "";
		
		EventLogData      eventLog      = new EventLogData();
		PowerAlarmLogData powerAlarmLog = new PowerAlarmLogData();

		int count = convertInt2("POWER_DOWN_EVENT_COUNT", DataFormat.select(data, offset, LEN_EVENT_COUNT));
		
		//이벤트가 없을시 리턴
		if(count < 1) return;
		
		offset += LEN_EVENT_COUNT;
		
		eventLog.setFlag(EVENTATTRIBUTE.POWER_DOWN.getCode());
		eventLog.setMsg(EVENTATTRIBUTE.POWER_DOWN.getName());
		eventLog.setAppend(EVENTATTRIBUTE.POWER_DOWN.getName());
		
		powerAlarmLog.setFlag(EVENTATTRIBUTE.POWER_DOWN.getCode());
		powerAlarmLog.setMsg(EVENTATTRIBUTE.POWER_DOWN.getName());
		
		dateTime = ElsterA1140.convertTimestamp("POWER_DOWN_EVENT_TIMESTAMP", DataFormat.select(data, offset, LEN_EVENT_TIMESTAMP));
		offset += LEN_EVENT_TIMESTAMP * 3;       // 뒤로 이전 4개의 시간 데이터가 오지만 필요없음
		
		eventLog.setDate(dateTime.substring(0, 8));          // yyyymmdd
		eventLog.setTime(dateTime.substring(8));             // hhmmss
		powerAlarmLog.setDate(dateTime.substring(0, 8));     // yyyymmdd
		powerAlarmLog.setTime(dateTime.substring(8));        // hhmmss
		
		meterEventList.add(eventLog);
		powerAlarmList.add(powerAlarmLog);
	}
	
	private void getPowerFail() throws Exception {
		log.debug("START-----getPowerFail()");

		byte[] data = DataFormat.select(rawData, OFS_POWER_FAIL, LEN_POWER_FAIL);
		
		int offset = 0;
		String dateTime = "";

		EventLogData      eventLog      = new EventLogData();
		PowerAlarmLogData powerAlarmLog = new PowerAlarmLogData();

		int count = convertInt2("POWER_FAIL_EVENT_COUNT", DataFormat.select(data, offset, LEN_EVENT_COUNT));
		
		//이벤트가 없을시 리턴
		if(count < 1) return;
		
		offset += LEN_EVENT_COUNT;
		
		eventLog.setFlag(EVENTATTRIBUTE.POWER_FAIL.getCode());
		eventLog.setMsg(EVENTATTRIBUTE.POWER_FAIL.getName());
		eventLog.setAppend(EVENTATTRIBUTE.POWER_FAIL.getName());
		
		powerAlarmLog.setFlag(EVENTATTRIBUTE.POWER_FAIL.getCode());
		powerAlarmLog.setMsg(EVENTATTRIBUTE.POWER_FAIL.getName());
		
		dateTime = ElsterA1140.convertTimestamp("POWER_FAIL_EVENT_TIMESTAMP", DataFormat.select(data, offset, LEN_EVENT_TIMESTAMP));
		offset += LEN_EVENT_TIMESTAMP * 5;       // 뒤로 이전 4개의 시간 데이터가 오지만 필요없음
		
		eventLog.setDate(dateTime.substring(0, 8));               // yyyymmdd
		eventLog.setTime(dateTime.substring(8));                  // hhmmss
		powerAlarmLog.setDate(dateTime.substring(0, 8));          // yyyymmdd
		powerAlarmLog.setTime(dateTime.substring(8));             // hhmmss
		
		
		powerAlarmLog.setCloseDate(dateTime.substring(0, 8));     // yyyymmdd
		powerAlarmLog.setCloseTime(dateTime.substring(8));        // hhmmss
		

		meterEventList.add(eventLog);
		powerAlarmList.add(powerAlarmLog);
	}
	

	private int convertInt2(String title, byte[] data) {
	    byte[] b = data;
	    DataFormat.convertEndian(b);
	    int i = DataFormat.getIntTo2Byte(b);
	    log.debug(title + "=[" + i + "]");
	    return i;
	}
	
	public String toString() {
	    try {
    		StringBuffer sb = new StringBuffer();
    		Iterator iter = null;
    		
    		sb.append("A1140_EVENT_LOG[\n")
    		  .append("  (METER_EVENT={\n");
    		iter = meterEventList.iterator();
    		while (iter.hasNext()) {
    			sb.append(iter.next());
    		}
    		sb.append("  }),\n");
    		
    		sb.append("  (POWER_EVENT={\n");
    		iter = powerAlarmList.iterator();
    		while (iter.hasNext()) {
    			sb.append(iter.next());
    		}
    		sb.append("  })\n");
    		sb.append("]\n");
    		
    		return sb.toString();
	    }
	    catch (Exception e) {
	        return e.getMessage();
	    }
	}
	
}
