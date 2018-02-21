package com.aimir.fep.meter.parser.rdata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

/**
 * BPList (검침 데이터 포맷의 BPCount 부분 객체)
 * 
 * @author kaze
 * 
 */
public class BPList implements Serializable {
	private static Log log = LogFactory.getLog(BPList.class);
	private int pos;
	private byte[] BPLISTPAYLOAD;
	private byte[] LASTTIME = new byte[5];
	private byte[] BASETIME = new byte[5];
	private byte[] LASTVALUE = new byte[8];
	private byte[] BASEVALUE = new byte[8];

	private String lastTime;
	private String baseTime;
	private List<Long> lastValues = new ArrayList<Long>();// chCount의 크기
	private List<Long> baseValues = new ArrayList<Long>();// chCount의 크기

	/**
	 * 
	 * @param pos
	 *            - 각각의 LogCategory가 시작하는 인덱스
	 * @param payLoad
	 *            - LPCount 전체 데이터의 Raw Data
	 */
	public BPList(int pos, byte[] payLoad) {
		super();
		this.pos = pos;
		BPLISTPAYLOAD = payLoad;
	}

	/**
	 * @param bpCount
	 * @param chCount
	 * @return 전해 받은 전체 raw의 pos를 리턴
	 * @throws Exception
	 */
	public int parsingPayLoad(int chCount, boolean littleEndian) throws Exception {
		System.arraycopy(BPLISTPAYLOAD, pos, LASTTIME, 0, LASTTIME.length);
		pos += LASTTIME.length;
		lastTime = DataUtil.getTimeStamp5(LASTTIME);
		System.arraycopy(BPLISTPAYLOAD, pos, BASETIME, 0, BASETIME.length);
		pos += BASETIME.length;
		baseTime = DataUtil.getTimeStamp5(BASETIME);
		long lastValue = 0;
		long baseValue = 0;
		for (int j = 0; j < chCount; j++) {
			System.arraycopy(BPLISTPAYLOAD, pos, LASTVALUE, 0, LASTVALUE.length);
			if (littleEndian) DataUtil.convertEndian(LASTVALUE);
			pos += LASTVALUE.length;
			lastValue = DataUtil.getLongToBytes(LASTVALUE);
			lastValues.add(lastValue);
			System.arraycopy(BPLISTPAYLOAD, pos, BASEVALUE, 0, BASEVALUE.length);
			if (littleEndian) DataUtil.convertEndian(BASEVALUE);
			pos += BASEVALUE.length;
			baseValue = DataUtil.getLongToBytes(BASEVALUE);
			baseValues.add(baseValue);
			log.debug("LASTTIME[" + Hex.decode(LASTTIME) + ","+lastTime+"] LASTVALUE[" +
			Hex.decode(LASTVALUE) + ","+lastValue+"] BASETIME[" + Hex.decode(BASETIME) + 
			","+baseTime+"] BASEVALUE[" + Hex.decode(BASEVALUE) + ","+baseValue+"]");
		}
		return pos;
	}

    public List<Long> getLastValues() {
        return lastValues;
    }

    public void setLastValues(List<Long> lastValues) {
        this.lastValues = lastValues;
    }

    public List<Long> getBaseValues() {
        return baseValues;
    }

    public void setBaseValues(List<Long> baseValues) {
        this.baseValues = baseValues;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public String getBaseTime() {
        return baseTime;
    }

    public void setBaseTime(String baseTime) {
        this.baseTime = baseTime;
    }
}
