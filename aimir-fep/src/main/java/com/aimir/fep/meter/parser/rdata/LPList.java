package com.aimir.fep.meter.parser.rdata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

/**
 * LPList (검침 데이터 포맷의 LPCount 부분 객체)
 * 
 * @author kaze
 * 
 */
public class LPList implements Serializable {
	private static Log log = LogFactory.getLog(LPList.class);
	private int pos;
	private byte[] LPLISTPAYLOAD;
	private byte[] LPTIME = new byte[5];
	private byte[] LPVALUE = new byte[4];	

	private String lpTime;	
	private List<Integer> lpValues = new ArrayList<Integer>();// chCount의 크기	
	
	public LPList() {
	    
	}
	
	/**
	 * 
	 * @param pos
	 *            - 각각의 LogCategory가 시작하는 인덱스
	 * @param payLoad
	 *            - LPCount 전체 데이터의 Raw Data
	 */
	public LPList(int pos, byte[] payLoad) {
		super();
		this.pos = pos;
		LPLISTPAYLOAD = payLoad;
	}

	/**
	 * @param lpCount
	 * @param chCount
	 * @return 전해 받은 전체 raw의 pos를 리턴
	 * @throws Exception
	 */
	public int parsingPayLoad(int chCount, boolean littleEndian) throws Exception {
		System.arraycopy(LPLISTPAYLOAD, pos, LPTIME, 0, LPTIME.length);
		pos += LPTIME.length;
		lpTime = DataUtil.getTimeStamp5(LPTIME);
		
		int lpValue = 0;
		for (int j = 0; j < chCount; j++) {
			System.arraycopy(LPLISTPAYLOAD, pos, LPVALUE, 0, LPVALUE.length);
			if (littleEndian) DataUtil.convertEndian(LPVALUE);
			pos += LPVALUE.length;
			lpValue = DataUtil.getIntToBytes(LPVALUE);
			lpValues.add(lpValue);
			
			log.debug("LPTIME[" + Hex.decode(LPTIME) + ","+lpTime + 
			        "] LPVALUE[" + Hex.decode(LPVALUE) + "," +lpValue +"]");
		}
		return pos;
	}

	/**
	 * yyyymmddhhmm
	 * @return the lpTime
	 */
	public String getLpTime() {
		return lpTime;
	}

	/**
	 * @param lpTime the lpTime to set
	 */
	public void setLpTime(String lpTime) {
		this.lpTime = lpTime;
	}

	/**
	 * @return the lpValues
	 */
	public List<Integer> getLpValues() {
		return lpValues;
	}

	/**
	 * @param lpValues the lpValues to set
	 */
	public void setLpValues(List<Integer> lpValues) {
		this.lpValues = lpValues;
	}
	
	
}
