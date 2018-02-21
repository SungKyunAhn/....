package com.aimir.fep.meter.parser.rdata;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.parser.rdata.RDataConstant.HeaderType;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.DataUtil;

/**
 * RData Format
 * 
 * @author kaze
 * 
 */
public abstract class RData {
	private static Log log = LogFactory.getLog(RData.class);
	/**
	 * 0x01:Inventory 0x02:Meter Configuration, 0x03:Metering Data
	 */
	HeaderType type;
	/**
	 * Payload Length
	 */
	Integer length;

	/**
	 * Inventory, MeterConfiguration, Metering Data의 Raw Data
	 * payLoad raw data
	 */
	byte[] payload;

	/**
	 * 하나의 R Data를 파싱해 지정된 count 만큼의 rData를 파싱해옴
	 * 
	 * @param svcd
	 *            - IF4 프레임의 Header(8byte)와 SDH(4byte), SVCH(2byte),
	 *            Tail(2byte)를 제외한 Body 전체
	 * @param idx
	 *            - rdata의 시작점을 찾기 위한 변수
	 * @param mcuId
	 * @return 전달 받은 data raw에서 몇 번째 바이트까지 파싱을 진행했는지 index를 리턴한다.
	 * @throws Exception
	 */
	public int decode(byte[] data, int idx, String mcuId) throws Exception {
		int pos = idx;
		setType(parsingHeaderType(data[idx++]));
		byte[] length = new byte[2];
		System.arraycopy(data, idx, length, 0, length.length);
		setLength(parsingLength(length));
		idx += length.length;
		payload = new byte[getLength()];
		System.arraycopy(data, idx, payload, 0, payload.length);
		idx += payload.length;
		return idx - pos;
	}
	
	/**
	 * RData를 구현한 InventoryRData, MeterConfigurationRData, MeteringDataRData에 실제 파서가 구현되어 있음
	 * @return 파싱한 RData를 리턴함
	 */
	abstract void parsingPayLoad() throws Exception;

	/**
	 * raw byte를 받아서 해당하는 header 타입을 파싱하여 리턴함
	 * 
	 * @param type
	 * @return
	 */
	public static RDataConstant.HeaderType parsingHeaderType(byte type) {
		if (type == RDataConstant.HeaderType.Inventory.getCode()) {
			return RDataConstant.HeaderType.Inventory;
		} else if (type == RDataConstant.HeaderType.MeterConfiguration
				.getCode()) {
			return RDataConstant.HeaderType.MeterConfiguration;
		} else if (type == RDataConstant.HeaderType.MeteringData
				.getCode()) {
			return RDataConstant.HeaderType.MeteringData;
		}
		return null;
	}

	/**
	 * length 2byte를 파싱해 payload의 length를 반환한다
	 * 
	 * @param length
	 *            raw 2byte
	 * @return
	 */
	private int parsingLength(byte[] length) {
		return DataFormat.getIntTo2Byte(length);
	}

	public HeaderType getType() {
		return type;
	}

	public void setType(HeaderType type) {
		this.type = type;
	}

	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public byte[] getPayload() {
		return payload;
	}

	public void setPayload(byte[] payload) {
		this.payload = payload;
	}	
}
