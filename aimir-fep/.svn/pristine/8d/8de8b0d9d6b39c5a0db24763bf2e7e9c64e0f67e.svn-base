package com.aimir.fep.meter.parser.rdata;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * R Metering Data Interface
 * MCU에서 올라온 하나의 검침 데이터 안에 들어있는 여러개의 RData를 포함하는 Data
 * @author kaze
 * 
 */
public class RDataList {
	private static Log log = LogFactory.getLog(RDataList.class);
	/**
	 * mcuId
	 */
	String mcuId;

	/**
	 * rDatas count
	 */
	Integer cnt;
	byte[] rDataRaw;
	List<RData> rDatas = new ArrayList<RData>();

	/**
	 * R Data의 Raw Data를 count 만큼 파싱해
	 * 해당 타입에 맞춰 RData를 만든다.
	 * @throws Exception
	 */
	public void decode() throws Exception {
		int pos = 0;
		RData rData = null;
		log.info("cnt=[" + cnt + "]");
		int offset = 0;
		for (int i = 0; i < cnt; i++) {
			try {				
				//DataType에 따라 인스턴스 생성
				if (RData.parsingHeaderType(getrDataRaw()[pos]) == RDataConstant.HeaderType.Inventory) {
					rData = new InventoryRData();					
				} else if (RData.parsingHeaderType(getrDataRaw()[pos]) == RDataConstant.HeaderType.MeterConfiguration) {
					rData = new MeterConfigurationRData();					
				} else if (RData.parsingHeaderType(getrDataRaw()[pos]) == RDataConstant.HeaderType.MeteringData) {
					rData = new MeteringDataRData();					
				}else {
					log.error("RealMetering dataType["+rData.getType()+"] is unsupport!!");
				}
				offset = rData.decode(getrDataRaw(), pos, mcuId);
				rData.parsingPayLoad();
				if (offset == -1 || offset == 0)
					break;
				pos += offset;
				rDatas.add(rData);
			} catch (Exception e) {
				log.error(e, e);
			}
		}
	}

	public String getMcuId() {
		return mcuId;
	}

	public void setMcuId(String mcuId) {
		this.mcuId = mcuId;
	}

	public Integer getCnt() {
		return cnt;
	}

	public void setCnt(Integer cnt) {
		this.cnt = cnt;
	}

	public byte[] getrDataRaw() {
		return rDataRaw;
	}

	public void setrDataRaw(byte[] rDataRaw) {
		this.rDataRaw = rDataRaw;
	}

	public List<RData> getrDatas() {
		return rDatas;
	}

	public void setrDatas(List<RData> rDatas) {
		this.rDatas = rDatas;
	}

}
