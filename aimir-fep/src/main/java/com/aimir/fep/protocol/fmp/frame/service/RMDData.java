package com.aimir.fep.protocol.fmp.frame.service;

import java.io.ByteArrayOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.fmp.datatype.WORD;
import com.aimir.fep.protocol.fmp.exception.FMPEncodeException;
import com.aimir.fep.util.Hex;

/**
 * 실시간 5분 검침을 지원하기 위한 검침 데이터 객체 SD 데이터 부분의 count를 cnt 필드에 지정 R Data를 rData에 지정
 * (rData Raw 안에는 여러개의 RData가 포함되어 있음)
 * @author kaze
 * 
 */
public class RMDData extends ServiceData {
	private static Log log = LogFactory.getLog(RMDData.class);
	// SD 데이터 부분의 RData Raw 데이터 부분
	private byte[] rData = null;
	// R Data의 반복 갯수
	public WORD cnt = new WORD();
	
	public RMDData() {}

	/**
	 * @param cnt
	 */
	public RMDData(WORD cnt) {
		this.cnt = cnt;
	}

	/**
	 * get srouce type
	 * 
	 * @return srcType <code>WORD</code> source type
	 */
	public WORD getCnt() {
		return this.cnt;
	}

	/**
	 * set srouce type
	 * 
	 * @param srcType
	 *            <code>WORD</code> source type
	 */
	public void setCnt(WORD cnt) {
		this.cnt = cnt;
	}

	/**
	 * SD 데이터 부분의 RData Raw 데이터 부분
	 * 
	 * @return
	 */
	public byte[] getrData() {
		return rData;
	}

	/**
	 * SD 데이터 부분의 RData Raw 데이터 부분
	 * 
	 * @param rData
	 */
	public void setrData(byte[] rData) {
		this.rData = rData;
	}

	/**
	 * encode
	 * 
	 * @return result <code>byte[]</code>
	 */
	public byte[] encode() throws FMPEncodeException {
		try {
			ByteArrayOutputStream bao = new ByteArrayOutputStream();
			byte[] bx = cnt.encode();
			bao.write(bx, 0, bx.length);
			if (cnt.getValue() > 0)
				bao.write(rData, 0, rData.length);
			return bao.toByteArray();
		} catch (Exception ex) {
			log.error("RMDData::encode failed :", ex);
			throw new FMPEncodeException("RMDData::encode failed :"
					+ ex.getMessage());
		}
	}

	/**
	 * get String
	 * 
	 * @param result
	 *            <code>String</code>
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Real Metering Header\n");
		sb.append("cnt=").append(getCnt()).append(',');
		if (rData != null) {
			sb.append("rData=[").append(Hex.getHexDump(rData)).append("]\n");
		} else
			sb.append("rData=").append('\n');

		return sb.toString();
	}
}
