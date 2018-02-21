package com.aimir.fep.meter.parser.DLMSEMnVGTypeTable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.fep.meter.parser.DLMSEMnVGTypeTable.DLMSEMnVGtypeVARIABLE.CURRENT_MAX_DEMAND;
import com.aimir.fep.meter.parser.DLMSEMnVGTypeTable.DLMSEMnVGtypeVARIABLE.DLMS_CLASS;
import com.aimir.fep.meter.parser.DLMSEMnVGTypeTable.DLMSEMnVGtypeVARIABLE.DLMS_CLASS_ATTR;
import com.aimir.fep.meter.parser.DLMSEMnVGTypeTable.DLMSEMnVGtypeVARIABLE.DLMS_TAG_TYPE;
import com.aimir.fep.meter.parser.DLMSEMnVGTypeTable.DLMSEMnVGtypeVARIABLE.EVENT;
import com.aimir.fep.meter.parser.DLMSEMnVGTypeTable.DLMSEMnVGtypeVARIABLE.EVENT_LOG;
import com.aimir.fep.meter.parser.DLMSEMnVGTypeTable.DLMSEMnVGtypeVARIABLE.LOAD_PROFILE;
import com.aimir.fep.meter.parser.DLMSEMnVGTypeTable.DLMSEMnVGtypeVARIABLE.METER_CONSTANT;
import com.aimir.fep.meter.parser.DLMSEMnVGTypeTable.DLMSEMnVGtypeVARIABLE.MONTHLY_DEMAND_PROFILE;
import com.aimir.fep.meter.parser.DLMSEMnVGTypeTable.DLMSEMnVGtypeVARIABLE.MONTHLY_ENERGY_PROFILE;
import com.aimir.fep.meter.parser.DLMSEMnVGTypeTable.DLMSEMnVGtypeVARIABLE.MONTHLY_RE_ENERGY_PROFILE;
import com.aimir.fep.meter.parser.DLMSEMnVGTypeTable.DLMSEMnVGtypeVARIABLE.OBIS;
import com.aimir.fep.meter.parser.DLMSEMnVGTypeTable.DLMSEMnVGtypeVARIABLE.PREVIOUS_MAX_DEMAND;
import com.aimir.fep.protocol.emnv.exception.EMnVSystemException;
import com.aimir.fep.protocol.emnv.exception.EMnVSystemException.EMnVExceptionReason;
import com.aimir.fep.protocol.emnv.frame.EMnVConstants.EMnVMeteringDataType;
import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

public class DLMSEMnVGtypeTable {
	private static Logger log = LoggerFactory.getLogger(DLMSEMnVGtypeTable.class);

	private DLMSEMnVGtypeHeader dlmsHeader = new DLMSEMnVGtypeHeader();
	private List<DLMSEMnVGtypeTag> dlmsTags = new ArrayList<DLMSEMnVGtypeTag>();
	Map<String, Object> obisMap = new LinkedHashMap<String, Object>();
	
	//private EMnVMeteringDataType meteringDataType;

	public DLMSEMnVGtypeHeader getDlmsHeader() {
		return dlmsHeader;
	}

	//	public void setMeteringDataType(EMnVMeteringDataType meteringDataType) {
	//		this.meteringDataType = meteringDataType;
	//	}
	public void setDlmsHeader(DLMSEMnVGtypeHeader dlmsHeader) {
		this.dlmsHeader = dlmsHeader;
	}

	public List<DLMSEMnVGtypeTag> getDlmsTags() {
		return dlmsTags;
	}

	public void setDlmsTags(List<DLMSEMnVGtypeTag> dlmsTags) {
		this.dlmsTags = dlmsTags;
	}

	public void addDlmsTag(DLMSEMnVGtypeTag tag) {
		this.dlmsTags.add(tag);
	}

	public void setObis(EMnVMeteringDataType type, String obisCode) {
		this.dlmsHeader.setObis(type, obisCode);
	}

	public void setClazz(int clazz) {
		this.dlmsHeader.setClazz(clazz);
	}

	public void setAttr(int attr) {
		this.dlmsHeader.setAttr(attr);
	}

	//    public void setLength(int length) {
	//        this.dlmsHeader.setLength(length);
	//    }

	//    public void parseDlmsTag(byte[] data) throws Exception {
	//        
	//        int len = DLMS_TAG_TYPE.Null.getLenth() - 1;
	//        int pos = 0;
	//        DLMSEMnVGtypeTag dlmsTag = null;
	//        byte[] bx = null;
	//        
	//        while (pos != data.length) {
	//            // log.debug("LEN[" + data.length + "] POS[" + pos + "]");
	//            dlmsTag = new DLMSEMnVGtypeTag();
	//            dlmsTag.setTag(data[pos]);
	//            pos += 1;
	//            
	//            /*
	//             * BitString, OctetString, VisibleString 태그에 대해서만 data[1] 바이트를
	//             * 확장 길이로 사용하고 데이타에 대한 길이이다.
	//             * 단, 0x80 으로 and연산으로 0x80 이 되면 0x7F 로 and을 하여 길이를 구한다.
	//             */
	//            int len_len = 0;
	//            /*
	//            if ((data[pos] & 0x80) == 0x80 ) {
	//                len_len = (data[pos] & 0x7F);
	//                log.debug("LEN BYTE[" + Hex.decode(new byte[] {data[pos]}) + "] LEN[" + len_len + "]");
	//                pos += 1;
	//                byte[] b_len = new byte[len_len];
	//                System.arraycopy(data, pos, b_len, 0, b_len.length);
	//                len = DataUtil.getIntToBytes(b_len);
	//                pos += len_len;
	//            }
	//            else {
	//            */
	//                if (dlmsTag.getTag() == DLMS_TAG_TYPE.OctetString || dlmsTag.getTag() == DLMS_TAG_TYPE.VisibleString) {
	//                    len = DataUtil.getIntToByte(data[pos]);
	//                    pos += 1;
	//                }
	//                else if (dlmsTag.getTag() == DLMS_TAG_TYPE.BitString) {
	//                	len = DataUtil.getIntToByte(data[pos]);
	//                	len /= 8;
	//                	pos += 1;
	//                }
	//                else len = dlmsTag.getTag().getLenth();
	//            // }
	//            
	//            bx = new byte[len];
	//            
	//            // pos와 data의 길이 합이 data.length를 넘어갈 수도 있기 때문에 비교를 한 후에 arrayindexout 예외가 발생하지 않도록 한다.
	//            if (pos + len > data.length)
	//                break;
	//            
	//            System.arraycopy(data, pos, bx, 0, bx.length);
	//            pos += bx.length;
	//
	//            dlmsTag.setLength(len);
	//            dlmsTag.setData(bx);
	//            if (dlmsTag.getTag() != DLMS_TAG_TYPE.Null)
	//            	log.debug(dlmsTag.toString());
	//            // 예외로 LOAD_PROFILE이고 태그가 Array인 경우는 추가하지 않는다.
	//            if (dlmsHeader.getObis() == OBIS.LOAD_PROFILE && dlmsTag.getTag() == DLMS_TAG_TYPE.Array)
	//                continue;
	//            if (dlmsTag.getTag() == DLMS_TAG_TYPE.Null)
	//            	continue;
	//            
	//            dlmsTags.add(dlmsTag);
	//        }
	//    }

//	// TLV 추가
//	public void setDlmsTag(DLMSEMnVGtypeTag dlmsTag) {
//		dlmsTags.add(dlmsTag);
//	}



	/*
	 * 하나의 OBIS에 여러 개의 태그를 가지는 경우 그 순서에 따라 데이타 유형이 결정된다.
	 */
	private void putData(List<DLMSEMnVGtypeTag> tags, Map<String, Object> ret, OBIS obis) {
		String name = null;

		DLMSEMnVGtypeTag tag = null;

		if (obis == OBIS.LOAD_PROFILE) {
			int structSize = 0;
			int structIdx = 0;
			for (int i = 0; i < tags.size();) {
				tag = tags.get(i++);
				if (tag.getTag() == DLMS_TAG_TYPE.Structure) {
					structSize = DataUtil.getIntToBytes(tag.getOCTET().getValue());
					log.debug("Structure Size[" + structSize + "]");
					structIdx = LOAD_PROFILE.Structure.getCode() + 1;
					for (int s = 0; s < structSize && i < tags.size(); s++, i++) {
						name = DLMSEMnVGtypeVARIABLE.getDataName(obis, structIdx++);
						putData(ret, obis, name, tags.get(i));
					}
				}
			}
		} /*else if (obis == OBIS.POWER_FAILURE || obis == OBIS.POWER_RESTORE || obis == OBIS.TIME_CHANGE_FROM || obis == OBIS.TIME_CHANGE_TO || obis == OBIS.DEMAND_RESET || obis == OBIS.MANUAL_DEMAND_RESET || obis == OBIS.SELF_READ || obis == OBIS.PROGRAM_CHANGE || obis == OBIS.SAG || obis == OBIS.SWELL) {
			int array = 0;
			int structSize = 0;
			for (int i = 0; i < tags.size();) {
				tag = tags.get(i++);
				if (tag.getTag() == DLMS_TAG_TYPE.Array) {
					array = (Integer) tag.getValue();
					log.debug(obis.getName() + "_EVENT_ARRAY[" + array + "]");
					for (int j = 0; j < array; j++) {
						tag = tags.get(i++);
						if (tag.getTag() == DLMS_TAG_TYPE.Structure) {
							structSize = DataUtil.getIntToBytes(tag.getOCTET().getValue());
							log.debug(obis.getName() + "_EVENT_STRUCTURE[" + structSize + "]");
							for (int k = 0; k < structSize; k++, i++) {
								name = DLMSEMnVGtypeVARIABLE.getDataName(obis, k);
								putData(ret, obis, name, tags.get(i));
							}
						}
					}
				}
			}
		} */else {
			for (int i = 0; i < tags.size(); i++) {
				name = DLMSEMnVGtypeVARIABLE.getDataName(obis, i);
				putData(ret, obis, name, tags.get(i));
			}
		}
	}

	private Object putData(Map<String, Object> ret, OBIS obis, String dataName, DLMSEMnVGtypeTag tag) {
		try {
//			switch (obis) {
//			case METER_TIME:
//				// getOBIS_CODE_METER_TIME(map, dataName, tag);
//				break;
//			case METER_INFO:
//				getOBIS_CODE_METER_INFO(ret, dataName, tag);
//				break;
//			case METER_CONSTANT_ACTIVE:
//			case METER_CONSTANT_REACTIVE:
//				getOBIS_CODE_METER_CONSTANT_ACTIVE(ret, dataName, tag);
//				break;
//			case CURRENT_MAX_DEMAND:
//				getOBIS_CODE_KEPCO_CURRENT_MAX_DEMAND(ret, dataName, tag);
//				break;
//			case PREVIOUS_MAX_DEMAND:
//				getOBIS_CODE_KEPCO_PREVIOUS_MAX_DEMAND(ret, dataName, tag);
//				break;
//			case MONTHLY_ENERGY_PROFILE:
//				getOBIS_CODE_MONTHLY_ENERY_PROFILE(ret, dataName, tag);
//				break;
//			case MONTHLY_DEMAND_PROFILE:
//				getOBIS_CODE_MONTHLY_DEMAND_PROFILE(ret, dataName, tag);
//				break;
//			case POWER_FAILURE:
//			case POWER_RESTORE:
//			case TIME_CHANGE_FROM:
//			case TIME_CHANGE_TO:
//			case DEMAND_RESET:
//			case MANUAL_DEMAND_RESET:
//			case SELF_READ:
//			case PROGRAM_CHANGE:
//			case SAG:
//			case SWELL:
//				getOBIS_CODE_EVENT(ret, dataName, tag);
//				break;
//			case LOAD_PROFILE:
//				getOBIS_CODE_LOAD_PROFILE(ret, dataName, tag);
//				break;
//			case POWER_QUALITY:
//				getOBIS_CODE_POWER_QUALITY(ret, dataName, tag);
//				break;
//			case LOAD_CONTROL_STATUS:
//				getOBIS_CODE_LOAD_CONTROL_STATUS(ret, dataName, tag);
//			}
		} catch (Exception e) {
			log.error("obis:" + obis + ":dataName:" + dataName + ":tag:" + tag);
		}
		return tag.getValue();
	}

	private void getOBIS_CODE_LOAD_CONTROL_STATUS(Map<String, Object> map, String dataName, DLMSEMnVGtypeTag tag) throws Exception {
		log.debug("DATA_NAME[" + dataName + "] VALUE[" + (tag.getValue() instanceof OCTET ? Hex.decode(((OCTET) tag.getValue()).getValue()) : tag.getValue()) + "]");
		String key = dataName;
		for (int cnt = 0;; cnt++) {
			key = dataName + "-" + cnt;
			if (!map.containsKey(key)) {
				map.put(key, tag.getValue());
				break;
			}
		}
	}

	private void getOBIS_CODE_LOAD_PROFILE(Map<String, Object> map, String dataName, DLMSEMnVGtypeTag tag) throws Exception {
		log.debug("DATA_NAME[" + dataName + "] VALUE[" + (tag.getValue() instanceof OCTET ? Hex.decode(((OCTET) tag.getValue()).getValue()) : tag.getValue()) + "]");
		if (dataName.equals(LOAD_PROFILE.Date.name())) {
			if (Hex.decode(tag.getData()).equals("000000000000000000000000"))
				return;

			String str = makeDateTime(tag.getData());
			String key = dataName;
			for (int cnt = 0;; cnt++) {
				key = dataName + "-" + cnt;
				if (!map.containsKey(key)) {
					map.put(key, str);
					break;
				}
			}
		} else if (dataName.equals(LOAD_PROFILE.Status.name())) {
			byte[] data = tag.getData();
			int value = DataUtil.getIntToBytes(data);

			String binaryString = Integer.toBinaryString(value);

			while (binaryString.length() % 8 != 0) {
				binaryString = "0" + binaryString;
			}
			// log.debug("binaryString:"+binaryString);
			String str = binaryString;
			String key = dataName;
			for (int cnt = 0;; cnt++) {
				key = dataName + "-" + cnt;
				if (!map.containsKey(key)) {
					map.put(key, str);
					break;
				}
			}
		} else {
			String key = dataName;
			for (int cnt = 0;; cnt++) {
				key = dataName + "-" + cnt;
				if (!map.containsKey(key)) {
					map.put(key, tag.getValue());
					break;
				}
			}
		}
	}

	private void getOBIS_CODE_EVENT(Map<String, Object> map, String dataName, DLMSEMnVGtypeTag tag) throws Exception {
		String key = dataName;
		for (int cnt = 0;; cnt++) {
			key = dataName + "-" + cnt;
			if (!map.containsKey(key))
				break;
		}

		for (EVENT e : EVENT.values()) {
			if (key.startsWith(e.name()) && tag.getTag() == DLMS_TAG_TYPE.OctetString) {
				log.debug("DataName[" + key + "] Data[" + makeDateTime4week(tag.getData()) + "]");
				map.put(key, makeDateTime4week(tag.getData()));
			}
			// else map.put(key, tag.getValue()); 
		}
	}

	private void getOBIS_CODE_EVENT_LOG(Map<String, Object> map, String dataName, DLMSEMnVGtypeTag tag) throws Exception {
		// log.debug("DATA_NAME[" + dataName + "] VALUE[" + (tag.getValue() instanceof OCTET? Hex.decode(((OCTET)tag.getValue()).getValue()):tag.getValue()) + "]");
		for (EVENT_LOG el : EVENT_LOG.values()) {
			if (dataName.startsWith(el.name()) && tag.getTag() == DLMS_TAG_TYPE.OctetString)
				map.put(dataName, makeDateTime4week(tag.getData()));
			else
				map.put(dataName, tag.getValue());
		}
	}

	/**
	 * DLMS 12bytes OCTET 시간 포맷 년 : 0,1 월 : 2 일 : 3 시 : 5 분 : 6
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	private String makeDateTime4week(byte[] data) throws Exception {
		int year = DataFormat.getIntTo2Byte(DataFormat.select(data, 0, 2));
		int month = DataFormat.getIntToByte(data[2]);
		int day = DataFormat.getIntToByte(data[3]);
		int hour = DataFormat.getIntToByte(data[5]);
		int min = DataFormat.getIntToByte(data[6]);

		DecimalFormat df = new DecimalFormat("00");
		String str = ":date=" + year + df.format(month) + df.format(day) + df.format(hour) + df.format(min);

		return str;
	}

	private String makeDateTime(byte[] data) throws Exception {
		int year = DataFormat.getIntTo2Byte(DataFormat.select(data, 0, 2));
		int month = DataFormat.getIntToByte(data[2]);
		int day = DataFormat.getIntToByte(data[3]);
		int week = DataFormat.getIntToByte(data[4]);
		int hour = DataFormat.getIntToByte(data[5]);
		int min = DataFormat.getIntToByte(data[6]);
		String str = String.format("%4d%02d%02d%02d%02d", year, month, day, hour, min);
		return str;
	}

	private void getOBIS_CODE_MONTHLY_DEMAND_PROFILE(Map<String, Object> map, String dataName, DLMSEMnVGtypeTag tag) throws Exception {

		if (dataName.equals(MONTHLY_DEMAND_PROFILE.ActiveDate.name()) || dataName.equals(MONTHLY_DEMAND_PROFILE.ApparentDate.name()) || dataName.equals(MONTHLY_DEMAND_PROFILE.T1ActiveDate.name()) || dataName.equals(MONTHLY_DEMAND_PROFILE.T1ApparentDate.name()) || dataName.equals(MONTHLY_DEMAND_PROFILE.T2ActiveDate.name()) || dataName.equals(MONTHLY_DEMAND_PROFILE.T2ApparentDate.name())
				|| dataName.equals(MONTHLY_DEMAND_PROFILE.T3ActiveDate.name()) || dataName.equals(MONTHLY_DEMAND_PROFILE.T3ApparentDate.name()) || dataName.equals(MONTHLY_DEMAND_PROFILE.T4ActiveDate.name()) || dataName.equals(MONTHLY_DEMAND_PROFILE.T4ApparentDate.name())) {
			if (Hex.decode(tag.getData()).equals("FFFFFFFFFFFFFFFFFF800000")) {
				// 데이타가 기록되기 전이므로 저장하지 않는다.
				return;
			}
			map.put(dataName, makeDateTime4week(tag.getData()));
		} else {
			map.put(dataName, tag.getValue());
		}
	}

	private void getOBIS_CODE_MONTHLY_ENERY_PROFILE(Map<String, Object> map, String dataName, DLMSEMnVGtypeTag tag) throws Exception {
		map.put(dataName, tag.getValue());
	}

	private void getOBIS_CODE_POWER_QUALITY(Map<String, Object> map, String dataName, DLMSEMnVGtypeTag tag) throws Exception {
		map.put(dataName, tag.getValue());
	}

	private void getOBIS_CODE_KEPCO_PREVIOUS_MAX_DEMAND(Map<String, Object> map, String dataName, DLMSEMnVGtypeTag tag) throws Exception {
		if (dataName.equals(PREVIOUS_MAX_DEMAND.T1PreviousActiveMaxDate.name()) || dataName.equals(PREVIOUS_MAX_DEMAND.T2PreviousActiveMaxDate.name()) || dataName.equals(PREVIOUS_MAX_DEMAND.T3PreviousActiveMaxDate.name())) {
			map.put(dataName, makeDateTime4week(tag.getData()));
			log.debug("DATA_NAME[" + dataName + "] DATA[" + map.get(dataName) + "]");
		} else {
			map.put(dataName, tag.getValue());
		}
	}

	public void getOBIS_CODE_KEPCO_CURRENT_MAX_DEMAND(Map<String, Object> map, String dataName, DLMSEMnVGtypeTag tag) throws Exception {
		if (dataName.equals(CURRENT_MAX_DEMAND.T1CurrentActiveMaxDate.name()) || dataName.equals(CURRENT_MAX_DEMAND.T2CurrentActiveMaxDate.name()) || dataName.equals(CURRENT_MAX_DEMAND.T3CurrentActiveMaxDate.name())) {
			map.put(dataName, makeDateTime4week(tag.getData()));
			// log.debug("DATA_NAME[" + dataName + "] DATA[" + map.get(dataName) + "]");
		} else {
			map.put(dataName, tag.getValue());
		}
	}

	public void getOBIS_CODE_METER_CONSTANT_ACTIVE(Map<String, Object> map, String dataName, DLMSEMnVGtypeTag tag) throws Exception {
		map.put(dataName, tag.getValue());
	}

//	public void getOBIS_CODE_METER_INFO(Map<String, Object> map, String dataName, DLMSEMnVGtypeTag tag) throws Exception {
//
//		String str = "";
//		if (dataName.equals(KEPCO_METER_INFO.MeterKind.name())) {
//			int value = DataUtil.getIntToBytes(((OCTET) tag.getValue()).getValue());
//
//			for (int i = 4; i < 8; i++) {
//				if ((value >> i & 1) == 1) {
//					if (i == 4) {
//						str = "1-phase,2-wire," + (value & 0x0F);
//						map.put(dataName, str);
//
//					} else if (i == 5) {
//						str = "1-phase,3-wire," + (value & 0x0F);
//						map.put(dataName, str);
//
//					} else if (i == 6) {
//						str = "3-phase,3-wire," + (value & 0x0F);
//						map.put(dataName, str);
//
//					} else if (i == 7) {
//						str = "3-phase,4-wire," + (value & 0x0F);
//						map.put(dataName, str);
//
//					}
//				}
//			}
//			if (!map.containsKey(KEPCO_METER_INFO.MeterKind.name())) {
//				str = (value & 0x0F) + "";
//				map.put(dataName, str);
//			}
//
//		} else if (dataName.equals(KEPCO_METER_INFO.MeterDate.name())) {
//			str = makeDateTime(tag.getData());
//			map.put(dataName, str);
//		} else if (dataName.equals(KEPCO_METER_INFO.MeterStatusError.name())) {
//			int value = DataUtil.getIntToBytes(((OCTET) tag.getValue()).getValue());
//			String binaryString = Integer.toBinaryString(value);
//			while (binaryString.length() % 8 != 0) {
//				binaryString = "0" + binaryString;
//			}
//
//			str = binaryString;
//			map.put(dataName, str);
//		} else if (dataName.equals(KEPCO_METER_INFO.MeterStatusCaution.name())) {
//			int value = DataUtil.getIntToBytes(((OCTET) tag.getValue()).getValue());
//			String binaryString = Integer.toBinaryString(value);
//			while (binaryString.length() % 8 != 0) {
//				binaryString = "0" + binaryString;
//			}
//			str = binaryString;
//			map.put(dataName, str);
//		} else if (dataName.equals(KEPCO_METER_INFO.RecentReadLoadProfileDate.name())) {
//			byte[] data = tag.getData();
//			DecimalFormat df = new DecimalFormat("00");
//			int year = DataFormat.getIntTo2Byte(DataFormat.select(data, 0, 2));
//			int month = DataFormat.getIntToByte(data[2]);
//			int day = DataFormat.getIntToByte(data[3]);
//			int hour = DataFormat.getIntToByte(data[4]);
//			int min = DataFormat.getIntToByte(data[5]);
//			str = year + df.format(month) + df.format(day) + df.format(hour) + df.format(min);
//			map.put(dataName, str);
//		} else if (dataName.equals(KEPCO_METER_INFO.LastReadInfo.name())) {
//			byte[] data = tag.getData();
//			int value = DataUtil.getIntToByte(data[1]);
//			String binaryString = Integer.toBinaryString(value);
//			while (binaryString.length() % 8 != 0) {
//				binaryString = "0" + binaryString;
//			}
//			int year = DataFormat.getIntTo2Byte(DataFormat.select(data, 2, 2));
//			int month = DataFormat.getIntToByte(data[4]);
//			int day = DataFormat.getIntToByte(data[5]);
//			int hour = DataFormat.getIntToByte(data[6]);
//			int min = DataFormat.getIntToByte(data[7]);
//			int sec = DataFormat.getIntToByte(data[8]);
//			int week = DataFormat.getIntToByte(data[9]);
//			DecimalFormat df = new DecimalFormat("00");
//
//			str = "reason:" + binaryString + ":date=" + year + df.format(month) + df.format(day) + df.format(hour) + df.format(min) + df.format(sec); //  + " " + week; 주는 제거한다.
//			map.put(dataName, str);
//		} else {
//			map.put(dataName, tag.getValue());
//		}
//	}
	

	/**
	 * 
	 * @param pos
	 * @param data
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings("rawtypes")
	public int setData(int pos, byte[] data) throws Exception {
		int lastPos = pos;
		DLMS_CLASS clazz = dlmsHeader.getClazz();
		DLMS_CLASS_ATTR attr = dlmsHeader.getAttr();
		OBIS obis = dlmsHeader.getObis();
		

		log.info("CLAZZ[" + clazz + "] ATTR[" + attr + "] OBIS[" + obis + "] TAG_SIZE[" + dlmsTags.size() + "]");
		printHexByteString(data, pos, 20);
		
		switch (clazz) {
		case DATA: // 1
	        DLMSEMnVGtypeTag dlmsTag = new DLMSEMnVGtypeTag();   
			
			byte[] t = new byte[1];    // T
			System.arraycopy(data, pos, t, 0, t.length);
			lastPos += t.length;
			dlmsTag.setTag(t[0]);

			int tlvLenth = 0;
			byte[] l = new byte[1];    // L
			
			if (dlmsTag.getTag() == DLMS_TAG_TYPE.OctetString || dlmsTag.getTag() == DLMS_TAG_TYPE.VisibleString) {
    			System.arraycopy(data, lastPos, l, 0, l.length);
    			lastPos += l.length;
    			tlvLenth = DataUtil.getIntToByte(l[0]);
            } else if (dlmsTag.getTag() == DLMS_TAG_TYPE.BitString) {
    			System.arraycopy(data, lastPos, l, 0, l.length);
    			tlvLenth = DataUtil.getIntToByte(l[0]);
    			lastPos += l.length;
    			tlvLenth /= 8;
    			//tlvLenth += 1;
            }else{
            	tlvLenth = dlmsTag.getTag().getLenth();
            }

			byte[] v = new byte[tlvLenth];   //V
            System.arraycopy(data, lastPos, v, 0, v.length);
            lastPos += v.length;
            
            dlmsTag.setLength(tlvLenth);
            dlmsTag.setData(v);            
            Object dlmsValue = dlmsTag.getValue();
            
			switch (attr) {
			case DATA_ATTR02:// value, CHOICE
				/**
				 * Billing OBIS
				 */
	            switch (obis) {
				case MANUFACTURER_METER_ID:/* Manufacturer meter ID */
					obisMap.put(OBIS.MANUFACTURER_METER_ID.name(),(dlmsValue instanceof OCTET? ((OCTET)dlmsValue).toString().trim() : dlmsValue));
					break;
				case CUSTOMER_METER_ID:/* Customer meter ID */
					obisMap.put(OBIS.CUSTOMER_METER_ID.name(),(dlmsValue instanceof OCTET? ((OCTET)dlmsValue).toString().trim() : dlmsValue));
					break;
				case LP_COUNT: /* 검침 횟수 */
					obisMap.put(OBIS.LP_COUNT.name(),(dlmsValue instanceof OCTET? ((OCTET)dlmsValue).toString().trim() : dlmsValue));
					break;
				case AVAILABLE_LP_COUNT:/* 사용가능한 검침 자료 갯수 */
					obisMap.put(OBIS.AVAILABLE_LP_COUNT.name(),(dlmsValue instanceof OCTET? ((OCTET)dlmsValue).toString().trim() : dlmsValue));
					break;
				case LPTIME_CURRENT_MONTH:/* 현월 검침일 */
					obisMap.put(OBIS.LPTIME_CURRENT_MONTH.name(), makeDateTime(v));
					break;
				case LPTIME_LAST_MONTH: /* 전월 검침일 */
					obisMap.put(OBIS.LPTIME_LAST_MONTH.name(), makeDateTime(v));
					break;
				case SELF_CHECK_BATTERY: /*자가 진단[배터리 이상]*/
					//obisMap.put(OBIS.SELF_CHECK_BATTERY.name(),(dlmsValue instanceof OCTET? ((OCTET)dlmsValue).toString() : dlmsValue));
					obisMap.put(OBIS.SELF_CHECK_BATTERY.name(), DataUtil.getIntToBytes(v));
					break;
				case SELF_CHECK_MEMORY:  /*자가 진단[메모리 이상]*/
					//obisMap.put(OBIS.SELF_CHECK_MEMORY.name(),(dlmsValue instanceof OCTET? ((OCTET)dlmsValue).toString() : dlmsValue));
					obisMap.put(OBIS.SELF_CHECK_MEMORY.name(), DataUtil.getIntToBytes(v));
					break;
				case SELF_CHECK_POWER: /*자가 진단[전압결상]*/
					//obisMap.put(OBIS.SELF_CHECK_POWER.name(),(dlmsValue instanceof OCTET? ((OCTET)dlmsValue).toString() : dlmsValue));
					obisMap.put(OBIS.SELF_CHECK_POWER.name(), DataUtil.getIntToBytes(v));
					break;
				case BATTERY_USE_TIME:  /*배터리 사용시간*/
					obisMap.put(OBIS.BATTERY_USE_TIME.name(),(dlmsValue instanceof OCTET? ((OCTET)dlmsValue).toString().trim() : dlmsValue));
					break;
				case BATTERY_INSTALL_TIME: /*배터리 설치 일자/시간*/
					//obisMap.put(OBIS.BATTERY_INSTALL_TIME.name(),(dlmsValue instanceof OCTET? ((OCTET)dlmsValue).toString() : dlmsValue));
					//obisMap.put(OBIS.BATTERY_INSTALL_TIME.name(), makeDateTime(v));
					obisMap.put(OBIS.BATTERY_INSTALL_TIME.name(), DataUtil.getDateTimeByDLMS_OCTETSTRING_Length(v));  // 어떤 타입인지 모르겠음. ff ff ff ff ff ff ff ff ff 80 00 00
					break;
				case POWER_FAILURE_COUNT:  /* 정전횟수 Data */
					obisMap.put(OBIS.POWER_FAILURE_COUNT.name(),(dlmsValue instanceof OCTET? ((OCTET)dlmsValue).toString().trim() : dlmsValue));
					break;
				case PROGRAM_ID:  /* 프로그램 ID */
					obisMap.put(OBIS.PROGRAM_ID.name(),(dlmsValue instanceof OCTET? ((OCTET)dlmsValue).toString().trim() : dlmsValue));
					break;
				case OVERLAP_LP_ENTRY_NUMBER:  /* 최대 엔트리 수 이후에 오버랩되어 저장되는 LP엔트리 번호. */
					obisMap.put(OBIS.OVERLAP_LP_ENTRY_NUMBER.name(),(dlmsValue instanceof OCTET? ((OCTET)dlmsValue).toString().trim() : dlmsValue));
					break;
				case MAGNETIC_SENSING_COUNT:  /* 자계 감지횟수 */
					obisMap.put(OBIS.MAGNETIC_SENSING_COUNT.name(),(dlmsValue instanceof OCTET? ((OCTET)dlmsValue).toString().trim() : dlmsValue));
					break;
				case SAG_COUNT:  /* SAG 발생횟수 */
					obisMap.put(OBIS.SAG_COUNT.name(),(dlmsValue instanceof OCTET? ((OCTET)dlmsValue).toString().trim() : dlmsValue));
					break;
				case SWELL_COUNT:  /* Swell 발생횟수 */
					obisMap.put(OBIS.SWELL_COUNT.name(),(dlmsValue instanceof OCTET? ((OCTET)dlmsValue).toString().trim() : dlmsValue));
					break;
				case TERMINAL_COVER_OPEN_COUNT:  /* 터미널 커버 Open 횟수 */
					obisMap.put(OBIS.TERMINAL_COVER_OPEN_COUNT.name(),(dlmsValue instanceof OCTET? ((OCTET)dlmsValue).toString().trim() : dlmsValue));
					break;
				default:
					break;
				}
				break;

			default:
				break;
			} // switch - attr finish.
			
			break;  // CASE - DATA Finish
		case REGISTER: //3
			switch (attr) {
			case REGISTER_ATTR02: // value, CHOICE : 값
		        //DLMSEMnVGtypeTag dlmsTag = new DLMSEMnVGtypeTag();   
				dlmsTag = new DLMSEMnVGtypeTag();
				
				//byte[] t = new byte[1];    // T
				t = new byte[1];    // T
				System.arraycopy(data, pos, t, 0, t.length);
				lastPos += t.length;
				dlmsTag.setTag(t[0]);

				//int tlvLenth = 0;
				tlvLenth = 0;
				//byte[] l = new byte[1];    // L
				l = new byte[1];    // L
				
				if (dlmsTag.getTag() == DLMS_TAG_TYPE.OctetString || dlmsTag.getTag() == DLMS_TAG_TYPE.VisibleString) {
	    			System.arraycopy(data, lastPos, l, 0, l.length);
	    			lastPos += l.length;
	    			tlvLenth = DataUtil.getIntToByte(l[0]);
	            } else if (dlmsTag.getTag() == DLMS_TAG_TYPE.BitString) {
	    			System.arraycopy(data, lastPos, l, 0, l.length);
	    			tlvLenth = DataUtil.getIntToByte(l[0]);
	    			lastPos += l.length;
	    			
	    			tlvLenth /= 8;
	    			tlvLenth += 1;
	            }else{
	            	tlvLenth = dlmsTag.getTag().getLenth();
	            }

				//byte[] v = new byte[tlvLenth];   //V
				v = new byte[tlvLenth];   //V
	            System.arraycopy(data, lastPos, v, 0, v.length);
	            lastPos += v.length;
	            
	            dlmsTag.setLength(tlvLenth);
	            dlmsTag.setData(v);     
	            //Object dlmsValue = dlmsTag.getValue();
	            dlmsValue = dlmsTag.getValue();
	            
				switch (obis) {
				case LP_INTERVAL:  /* Load Profile 기록간격 */
					obisMap.put(OBIS.LP_INTERVAL.name(), (dlmsValue instanceof OCTET? ((OCTET)dlmsValue).toString().trim() : dlmsValue));
					break;
				case METER_CONSTANT_ACTIVE:  /* 유효전력량 계기정수 */
					obisMap.put(METER_CONSTANT.ActiveC.name(),(dlmsValue instanceof OCTET? ((OCTET)dlmsValue).toString().trim() : dlmsValue));
					break;
				case METER_CONSTANT_REACTIVE:  /*무효전력량 계기정수*/
					obisMap.put(METER_CONSTANT.ReactiveC.name(),(dlmsValue instanceof OCTET? ((OCTET)dlmsValue).toString().trim() : dlmsValue));
					break;
				case APPARENT_POWER_CONSTANT:  /* 피상전력량 계기정수 */
					obisMap.put(OBIS.APPARENT_POWER_CONSTANT.name(),(dlmsValue instanceof OCTET? ((OCTET)dlmsValue).toString().trim() : dlmsValue));
					break;
				case DEFAULT_VOLTAGE_AB:  /* 평균전압 A-B상 */
					obisMap.put(OBIS.DEFAULT_VOLTAGE_AB.name(), dlmsValue);
					break;
				case DEFAULT_VOLTAGE_BA:  /* 평균전압 B-C상 */
					obisMap.put(OBIS.DEFAULT_VOLTAGE_BA.name(), dlmsValue);
					break;
				case DEFAULT_VOLTAGE_CA:  /* 평균전압 C-A상 */
					obisMap.put(OBIS.DEFAULT_VOLTAGE_CA.name(), dlmsValue);
					break;
				case TEMPERATURE: /* 현재온도 */
					obisMap.put(OBIS.TEMPERATURE.name(), dlmsValue);
					break;
				default:
					break;
				}

				break;
			case REGISTER_ATTR03: // scaler_unit, scal_unit_type  : 단위 
				if (obis == OBIS.LP_INTERVAL 
						|| obis == OBIS.METER_CONSTANT_ACTIVE || obis == OBIS.METER_CONSTANT_REACTIVE 
						|| obis == OBIS.APPARENT_POWER_CONSTANT
						|| obis == OBIS.DEFAULT_VOLTAGE_AB || obis == OBIS.DEFAULT_VOLTAGE_BA || obis == OBIS.DEFAULT_VOLTAGE_CA
						|| obis == OBIS.TEMPERATURE) {
					byte[] ra03T = new byte[1];    // T
					System.arraycopy(data, lastPos, ra03T, 0, ra03T.length);
					lastPos += ra03T.length;
					
					DLMS_TAG_TYPE ra03Type = DLMS_TAG_TYPE.getItem(DataUtil.getIntToByte(ra03T[0]));
					if(ra03Type == DLMS_TAG_TYPE.Structure){
						byte[] structureL = new byte[1];    // Structure의 L     
						System.arraycopy(data, lastPos, structureL, 0, structureL.length);
						lastPos += structureL.length;
						
		    			int strucutureTLVCount = DataUtil.getIntToByte(structureL[0]);  // Structure의 갯수. 2가와야함.
		    			for(int k=0; k<strucutureTLVCount; k++){
		    				byte[] scalerT = new byte[1];      // T : Integer가 와야함.   
							System.arraycopy(data, lastPos, scalerT, 0, scalerT.length);
							lastPos += scalerT.length;					

		    				byte[] scalerV = new byte[1];      // V : enum
							System.arraycopy(data, lastPos, scalerV, 0, scalerV.length);
							lastPos += scalerV.length;	    					

							if(k == 0){
								obisMap.put("Scaler", DataUtil.getIntToBytes(scalerV));
							}else {
								obisMap.put("unit", DataUtil.getIntToBytes(scalerV));
							}
		    			}
					}else{
						log.error("Structure 구조가 와야함.");
					}
				} 
				break;
			default:
				break;
			} // switch - attr finish.
			break;  // CASE - REGISTER Finish
		case EXTENDED_REGISTER: // 4
	       // DLMSEMnVGtypeTag dlmsTag = new DLMSEMnVGtypeTag();   
			switch (attr) {
			case EXTENDED_REGISTER_ATTR02: // value, CHOICE  : 값
				byte[] ra03T = new byte[1];    // T
				System.arraycopy(data, lastPos, ra03T, 0, ra03T.length);
				lastPos += ra03T.length;
				byte[] stv = null;
				DLMS_TAG_TYPE ra03Type = DLMS_TAG_TYPE.getItem(DataUtil.getIntToByte(ra03T[0]));
				if(ra03Type == DLMS_TAG_TYPE.FLOAT32){
					stv = new byte[ra03Type.getLenth()];   				
    				System.arraycopy(data, lastPos, stv, 0, stv.length);
    				lastPos += stv.length;    				
				}else{
					log.error("########### 확인해볼것(EXTENDED_REGISTER_ATTR02) ########, " + obis);
				}
				
				try {
//					/* A상 최대부하전류 */
//					if (obis == OBIS.A_MAX_LOAD_ELECTRIC) {
//	    				obisMap.put(OBIS.A_MAX_LOAD_ELECTRIC.name(), DataUtil.getFloat(stv, 0));
//					}
//					/* B상 최대부하전류 */
//					else if (obis == OBIS.B_MAX_LOAD_ELECTRIC) {
//						obisMap.put(OBIS.B_MAX_LOAD_ELECTRIC.name(), DataUtil.getFloat(stv, 0));
//					}
//					/* C상 최대부하전류 */
//					else if (obis == OBIS.C_MAX_LOAD_ELECTRIC) {
//						obisMap.put(OBIS.C_MAX_LOAD_ELECTRIC.name(), DataUtil.getFloat(stv, 0));
//					}	
					if(obis == OBIS.A_MAX_LOAD_ELECTRIC || obis == OBIS.B_MAX_LOAD_ELECTRIC || obis == OBIS.C_MAX_LOAD_ELECTRIC){
						obisMap.put(obis.name(), DataUtil.getFloat(stv, 0));
					}
				} catch (Exception e) {
					log.error("에러 발생!@", e);
				}
				break;
			case EXTENDED_REGISTER_ATTR03:// scaler_unit, scal_unit_type   : 단위
				/* A상 최대부하전류 */ /* B상 최대부하전류 */ /* C상 최대부하전류 */
				if (obis == OBIS.A_MAX_LOAD_ELECTRIC || obis == OBIS.B_MAX_LOAD_ELECTRIC || obis == OBIS.C_MAX_LOAD_ELECTRIC) {
					ra03T = new byte[1];    // T
					System.arraycopy(data, lastPos, ra03T, 0, ra03T.length);
					lastPos += ra03T.length;
					
					ra03Type = DLMS_TAG_TYPE.getItem(DataUtil.getIntToByte(ra03T[0]));
					if(ra03Type == DLMS_TAG_TYPE.Structure){
						byte[] structureL = new byte[1];    // Structure의 L     
						System.arraycopy(data, lastPos, structureL, 0, structureL.length);
						lastPos += structureL.length;
						
		    			int strucutureTLVCount = DataUtil.getIntToByte(structureL[0]);  // Structure의 갯수. 2가와야함.
		    			for(int k=0; k<strucutureTLVCount; k++){
		    				byte[] scalerT = new byte[1];      // T : Integer가 와야함.   
							System.arraycopy(data, lastPos, scalerT, 0, scalerT.length);
							lastPos += scalerT.length;					

		    				byte[] scalerV = new byte[1];      // V : enum
							System.arraycopy(data, lastPos, scalerV, 0, scalerV.length);
							lastPos += scalerV.length;	    					

							if(k == 0){
								obisMap.put("Scaler", DataUtil.getIntToBytes(scalerV));
							}else {
								obisMap.put("unit", DataUtil.getIntToBytes(scalerV));
							}
		    			}
					}else{
						log.error("########### 확인해볼것 (EXTENDED_REGISTER_ATTR03) ########, " + obis);
					}
				}
				break;
			case EXTENDED_REGISTER_ATTR05:// capture_time, octet-string    : 시간
				ra03T = new byte[1];    // T
				System.arraycopy(data, lastPos, ra03T, 0, ra03T.length);
				lastPos += ra03T.length;
				stv = null;
				ra03Type = DLMS_TAG_TYPE.getItem(DataUtil.getIntToByte(ra03T[0]));
				
				tlvLenth = 0;
				l = new byte[1];    // L
				
				if(ra03Type == DLMS_TAG_TYPE.OctetString){
	    			System.arraycopy(data, lastPos, l, 0, l.length);
	    			lastPos += l.length;
	    			tlvLenth = DataUtil.getIntToByte(l[0]);
				}else{
					log.error("########### 확인해볼것 (EXTENDED_REGISTER_ATTR05) ########, " + obis);
				}
				v = new byte[tlvLenth];   //V
	            System.arraycopy(data, lastPos, v, 0, v.length);
	            lastPos += v.length;
	            
				try {
//					/* A상 최대부하전류 */
//					if (obis == OBIS.A_MAX_LOAD_ELECTRIC) {
//	    				obisMap.put(OBIS.A_MAX_LOAD_ELECTRIC.name(), DataUtil.getDateTimeByDLMS_OCTETSTRING12(v));
//					}
//					/* B상 최대부하전류 */
//					else if (obis == OBIS.B_MAX_LOAD_ELECTRIC) {
//						obisMap.put(OBIS.B_MAX_LOAD_ELECTRIC.name(), DataUtil.getDateTimeByDLMS_OCTETSTRING12(v));
//					}
//					/* C상 최대부하전류 */
//					else if (obis == OBIS.C_MAX_LOAD_ELECTRIC) {
//						obisMap.put(OBIS.C_MAX_LOAD_ELECTRIC.name(), DataUtil.getDateTimeByDLMS_OCTETSTRING12(v));
//					}			
					if(obis == OBIS.A_MAX_LOAD_ELECTRIC || obis == OBIS.B_MAX_LOAD_ELECTRIC || obis == OBIS.C_MAX_LOAD_ELECTRIC){
						obisMap.put("DATE_TIME", DataUtil.getDateTimeByDLMS_OCTETSTRING12(v)); 
					}
				} catch (Exception e) {
					log.error("에러 발생!@", e);
				}
				break;
			default:
				break;
			} // switch - attr finish
			break;  // CASE - EXTENDED_REGISTER Finish
		case PROFILE_GENERIC: //7
//			dlmsTag = new DLMSEMnVGtypeTag();
			switch (attr) {
			case PROFILE_GENERIC_ATTR02: //  buffer, octet-string : 값
				/* 순방향 전력량 (현월) */ /* 순방향 전력량 (전월) */
				if (obis == OBIS.MONTHLY_ENERGY_PROFILE_CURRENT || obis == OBIS.MONTHLY_ENERGY_PROFILE_LAST) {
					Object[] result = parseOBIS_7_2(data, lastPos);
					lastPos = (int) Integer.parseInt((result[0]).toString());
					List arrayList = (List) result[1];
					Object[] values = (Object[]) arrayList.get(0);
					
					obisMap.put(MONTHLY_ENERGY_PROFILE.ActiveEnergy.name(), values[0]); 
					obisMap.put(MONTHLY_ENERGY_PROFILE.ApparentEnergy.name(), values[1]); // 피상
					obisMap.put(MONTHLY_ENERGY_PROFILE.LaggingReactiveEnergy.name(), values[2]); // 지상
					obisMap.put(MONTHLY_ENERGY_PROFILE.LeadingReactiveEnergy.name(), values[3]); // 진상
					obisMap.put(MONTHLY_ENERGY_PROFILE.AveragePowerFactor.name(), values[4]);
					obisMap.put(MONTHLY_ENERGY_PROFILE.T1ActiveEnergy.name(), values[5]); 
					obisMap.put(MONTHLY_ENERGY_PROFILE.T1ApparentEnergy.name(), values[6]); 
					obisMap.put(MONTHLY_ENERGY_PROFILE.T1LaggingReactiveEnergy.name(), values[7]); 
					obisMap.put(MONTHLY_ENERGY_PROFILE.T1LeadingReactiveEnergy.name(), values[8]); 
					obisMap.put(MONTHLY_ENERGY_PROFILE.T1AveragePowerFactor.name(), values[9]);
					obisMap.put(MONTHLY_ENERGY_PROFILE.T2ActiveEnergy.name(), values[10]); 
					obisMap.put(MONTHLY_ENERGY_PROFILE.T2ApparentEnergy.name(), values[11]); 
					obisMap.put(MONTHLY_ENERGY_PROFILE.T2LaggingReactiveEnergy.name(), values[12]); 
					obisMap.put(MONTHLY_ENERGY_PROFILE.T2LeadingReactiveEnergy.name(), values[13]); 
					obisMap.put(MONTHLY_ENERGY_PROFILE.T2AveragePowerFactor.name(), values[14]);
					obisMap.put(MONTHLY_ENERGY_PROFILE.T3ActiveEnergy.name(), values[15]); 
					obisMap.put(MONTHLY_ENERGY_PROFILE.T3ApparentEnergy.name(), values[16]); 
					obisMap.put(MONTHLY_ENERGY_PROFILE.T3LaggingReactiveEnergy.name(), values[17]); 
					obisMap.put(MONTHLY_ENERGY_PROFILE.T3LeadingReactiveEnergy.name(), values[18]); 
					obisMap.put(MONTHLY_ENERGY_PROFILE.T3AveragePowerFactor.name(), values[19]);
					obisMap.put(MONTHLY_ENERGY_PROFILE.T4ActiveEnergy.name(), values[20]); 
					obisMap.put(MONTHLY_ENERGY_PROFILE.T4ApparentEnergy.name(), values[21]); 
					obisMap.put(MONTHLY_ENERGY_PROFILE.T4LaggingReactiveEnergy.name(), values[22]); 
					obisMap.put(MONTHLY_ENERGY_PROFILE.T4LeadingReactiveEnergy.name(), values[23]); 
					obisMap.put(MONTHLY_ENERGY_PROFILE.T4AveragePowerFactor.name(), values[24]);
				}

				/* 순방향 최대 수요전력(현월) */  /* 순방향 최대 수요전력(전월) */
				else if (obis == OBIS.MONTHLY_DEMAND_PROFILE_CURRENT || obis == OBIS.MONTHLY_DEMAND_PROFILE_LAST) {
					Object[] result = parseOBIS_7_2(data, lastPos);
					lastPos = (int) Integer.parseInt((result[0]).toString());
					List arrayList = (List) result[1];
					Object[] values = (Object[]) arrayList.get(0);
					
					
//					obisMap.put(LOAD_PROFILE.Status.name(), values[5] instanceof OCTET ? Hex.decode(((OCTET)values[5]).getValue()) : values[5]); // 상태정보 OCTET

					
					
					obisMap.put(MONTHLY_DEMAND_PROFILE.Active.name(), values[0]);
					obisMap.put(MONTHLY_DEMAND_PROFILE.ActiveDate.name(), values[1]);
					obisMap.put(MONTHLY_DEMAND_PROFILE.CummlativeActive.name(), values[2]); 
					obisMap.put(MONTHLY_DEMAND_PROFILE.Apparent.name(), values[3]);
					obisMap.put(MONTHLY_DEMAND_PROFILE.ApparentDate.name(), values[4]); 
					obisMap.put(MONTHLY_DEMAND_PROFILE.CummlativeApparent.name(), values[5]); 
					obisMap.put(MONTHLY_DEMAND_PROFILE.T1Active.name(), values[6]); 
					obisMap.put(MONTHLY_DEMAND_PROFILE.T1ActiveDate.name(), values[7]); 
					obisMap.put(MONTHLY_DEMAND_PROFILE.T1CummlativeActive.name(), values[8]); 
					obisMap.put(MONTHLY_DEMAND_PROFILE.T1Apparent.name(), values[9]); 
					obisMap.put(MONTHLY_DEMAND_PROFILE.T1ApparentDate.name(), values[10]); 
					obisMap.put(MONTHLY_DEMAND_PROFILE.T1CummlativeApparent.name(), values[11]); 
					obisMap.put(MONTHLY_DEMAND_PROFILE.T2Active.name(), values[12]); 
					obisMap.put(MONTHLY_DEMAND_PROFILE.T2ActiveDate.name(), values[13]); 
					obisMap.put(MONTHLY_DEMAND_PROFILE.T2CummlativeActive.name(), values[14]); 
					obisMap.put(MONTHLY_DEMAND_PROFILE.T2Apparent.name(), values[15]); 
					obisMap.put(MONTHLY_DEMAND_PROFILE.T2ApparentDate.name(), values[16]); 
					obisMap.put(MONTHLY_DEMAND_PROFILE.T2CummlativeApparent.name(), values[17]); 
					obisMap.put(MONTHLY_DEMAND_PROFILE.T3Active.name(), values[18]); 
					obisMap.put(MONTHLY_DEMAND_PROFILE.T3ActiveDate.name(), values[19]); 
					obisMap.put(MONTHLY_DEMAND_PROFILE.T3CummlativeActive.name(), values[20]); 
					obisMap.put(MONTHLY_DEMAND_PROFILE.T3Apparent.name(), values[21]); 
					obisMap.put(MONTHLY_DEMAND_PROFILE.T3ApparentDate.name(), values[22]); 
					obisMap.put(MONTHLY_DEMAND_PROFILE.T3CummlativeApparent.name(), values[23]); 
					obisMap.put(MONTHLY_DEMAND_PROFILE.T4Active.name(), values[24]); 
					obisMap.put(MONTHLY_DEMAND_PROFILE.T4ActiveDate.name(), values[25]); 
					obisMap.put(MONTHLY_DEMAND_PROFILE.T4CummlativeActive.name(), values[26]); 
					obisMap.put(MONTHLY_DEMAND_PROFILE.T4Apparent.name(), values[27]); 
					obisMap.put(MONTHLY_DEMAND_PROFILE.T4ApparentDate.name(), values[28]); 
					obisMap.put(MONTHLY_DEMAND_PROFILE.T4CummlativeApparent.name(), values[29]); 
				}
				/* 역방향 전력량 (현월) */ /* 역방향 전력량 (전월) */
				else if (obis == OBIS.R_MONTHLY_ENERGY_PROFILE_CURRENT || obis == OBIS.R_MONTHLY_ENERGY_PROFILE_LAST) {
					Object[] result = parseOBIS_7_2(data, lastPos);
					lastPos = (int) Integer.parseInt((result[0]).toString());
					List arrayList = (List) result[1];
					Object[] values = (Object[]) arrayList.get(0);
					
					obisMap.put(MONTHLY_RE_ENERGY_PROFILE.RE_ActiveEnergy.name(), values[0]); 
					obisMap.put(MONTHLY_RE_ENERGY_PROFILE.RE_ApparentEnergy.name(), values[1]); // 피상
					obisMap.put(MONTHLY_RE_ENERGY_PROFILE.RE_LaggingReactiveEnergy.name(), values[2]); // 지상
					obisMap.put(MONTHLY_RE_ENERGY_PROFILE.RE_LeadingReactiveEnergy.name(), values[3]); // 진상
					obisMap.put(MONTHLY_RE_ENERGY_PROFILE.RE_AveragePowerFactor.name(), values[4]);
					obisMap.put(MONTHLY_RE_ENERGY_PROFILE.RE_T1ActiveEnergy.name(), values[5]); 
					obisMap.put(MONTHLY_RE_ENERGY_PROFILE.RE_T1ApparentEnergy.name(), values[6]); 
					obisMap.put(MONTHLY_RE_ENERGY_PROFILE.RE_T1LaggingReactiveEnergy.name(), values[7]); 
					obisMap.put(MONTHLY_RE_ENERGY_PROFILE.RE_T1LeadingReactiveEnergy.name(), values[8]); 
					obisMap.put(MONTHLY_RE_ENERGY_PROFILE.RE_T1AveragePowerFactor.name(), values[9]);
					obisMap.put(MONTHLY_RE_ENERGY_PROFILE.RE_T2ActiveEnergy.name(), values[10]); 
					obisMap.put(MONTHLY_RE_ENERGY_PROFILE.RE_T2ApparentEnergy.name(), values[11]); 
					obisMap.put(MONTHLY_RE_ENERGY_PROFILE.RE_T2LaggingReactiveEnergy.name(), values[12]); 
					obisMap.put(MONTHLY_RE_ENERGY_PROFILE.RE_T2LeadingReactiveEnergy.name(), values[13]); 
					obisMap.put(MONTHLY_RE_ENERGY_PROFILE.RE_T2AveragePowerFactor.name(), values[14]);
					obisMap.put(MONTHLY_RE_ENERGY_PROFILE.RE_T3ActiveEnergy.name(), values[15]); 
					obisMap.put(MONTHLY_RE_ENERGY_PROFILE.RE_T3ApparentEnergy.name(), values[16]); 
					obisMap.put(MONTHLY_RE_ENERGY_PROFILE.RE_T3LaggingReactiveEnergy.name(), values[17]); 
					obisMap.put(MONTHLY_RE_ENERGY_PROFILE.RE_T3LeadingReactiveEnergy.name(), values[18]); 
					obisMap.put(MONTHLY_RE_ENERGY_PROFILE.RE_T3AveragePowerFactor.name(), values[19]);
					obisMap.put(MONTHLY_RE_ENERGY_PROFILE.RE_T4ActiveEnergy.name(), values[20]); 
					obisMap.put(MONTHLY_RE_ENERGY_PROFILE.RE_T4ApparentEnergy.name(), values[21]); 
					obisMap.put(MONTHLY_RE_ENERGY_PROFILE.RE_T4LaggingReactiveEnergy.name(), values[22]); 
					obisMap.put(MONTHLY_RE_ENERGY_PROFILE.RE_T4LeadingReactiveEnergy.name(), values[23]); 
					obisMap.put(MONTHLY_RE_ENERGY_PROFILE.RE_T4AveragePowerFactor.name(), values[24]);
				}
				/* 정전(Power failure) */
				else if (obis == OBIS.POWER_FAILURE) {
					Object[] result = parseOBIS_7_2(data, lastPos);
					lastPos = (int) Integer.parseInt((result[0]).toString());
					List arrayList = (List) result[1];
					//Object[] values = (Object[]) arrayList.get(0);

					// 이벤트 등록
					for(int i=0; i<arrayList.size(); i++){
						Object[] ob = (Object[]) arrayList.get(i);

						obisMap.put(EVENT.EventTime.name() + "-" + i, ob[0]);
						obisMap.put(EVENT.EventCount.name() + "-" + i, ob[1]);
					}
				}
				/* Load Profile */
				else if(obis == OBIS.LOAD_PROFILE){
					Object[] result = parseOBIS_7_2(data, lastPos);
					lastPos = (int) Integer.parseInt((result[0]).toString());
					
					@SuppressWarnings("unchecked")
					List<Object[]> arrayList = (List<Object[]>) result[1];
					if(arrayList.size() < 1){
						log.error("LP 데이터가 하나도 없음.");
						log.error("LP 데이터가 하나도 없음.");
						log.error("LP 데이터가 하나도 없음.");
						log.error("LP 데이터가 하나도 없음.");
					}else{
						for(int i=0; i<arrayList.size(); i++){
							Object[] values = (Object[]) arrayList.get(i);
							
							/** G-Type_규격_전문개정_20140201_최종8.hwp 파일 뒤쪽 p.24 참조
							 *  1 순방향 유효전력량[Q1+Q4]
								2 순방향 지상 무효전력량[Q1]
								3 순방향 진상 무효전력량[Q4]
								4 순방향 피상전력량[Q1+Q4]
								5 일자/시간
								6 상태정보
								7 역방향 유효전력량[Q2+Q3]
								8 역방향 진상 무효전력량[Q2]
								9 역방향 지상 무효전력량[Q3]
								10 역방향 피상전력량[Q2+Q3]
							 */
							obisMap.put(LOAD_PROFILE.ImportActive.name() + "-" + i , values[0]); // 순방향 유효전력량  
							obisMap.put(LOAD_PROFILE.ImportLaggingReactive.name() + "-" + i, values[1]); // 순방향 지상 무효전력량
							obisMap.put(LOAD_PROFILE.ImportLeadingReactive.name() + "-" + i, values[2]); // 순방향 진상 무효전력량
							obisMap.put(LOAD_PROFILE.ImportApparentEnergy.name() + "-" + i, values[3]);  // 순방향 피상 전력량 
							obisMap.put(LOAD_PROFILE.Date.name() + "-" + i, values[4]);                    // 일자/시간
							
							//obisMap.put(LOAD_PROFILE.Status.name() + "-" + i, values[5] instanceof OCTET ? Hex.decode(((OCTET)values[5]).getValue()) : values[5]); // 상태정보 OCTET

				            int value = DataUtil.getIntToBytes(((OCTET)values[5]).getValue());
				            String binaryString = Integer.toBinaryString(value);
				            while (binaryString.length() % 8 != 0) {
				                binaryString = "0" + binaryString;
				            }
				            obisMap.put(LOAD_PROFILE.Status.name() + "-" + i, binaryString);
				            
							
							if(6 < values.length){
								log.info("역방향 전력량이 들어옴. 확인해볼것.!!!");
								log.info("역방향 전력량이 들어옴. 확인해볼것.!!!");
								log.info("역방향 전력량이 들어옴. 확인해볼것.!!!");
								
//								obisMap.put(LOAD_PROFILE.ExportActive.name() + "-" + i, values[6]);             // 역방향 유효 전력량
//								obisMap.put(LOAD_PROFILE.ExportLaggingReactive.name() + "-" + i, values[7]);   // 역방향 진상 무효전력량
//								obisMap.put(LOAD_PROFILE.ExportLeadingReactive.name() + "-" + i, values[8]);   // 역방향 지상 무효전력량
//								obisMap.put(LOAD_PROFILE.ExportApparentEnergy.name() + "-" + i, values[9]);	    // 역방향 피상전력량				
							}
						}						
					}
				}
				
				break;
			case PROFILE_GENERIC_ATTR07: // entries_in_use, double-long-unsigned : 개수
				/* Load Profile */ 
				if(obis == OBIS.LOAD_PROFILE){
					byte[] ra03T = new byte[1];    // T
					System.arraycopy(data, lastPos, ra03T, 0, ra03T.length);
					lastPos += ra03T.length;
					byte[] stv = null;
					DLMS_TAG_TYPE ra03Type = DLMS_TAG_TYPE.getItem(DataUtil.getIntToByte(ra03T[0]));
					if(ra03Type == DLMS_TAG_TYPE.UINT32){
						stv = new byte[ra03Type.getLenth()];   				
	    				System.arraycopy(data, lastPos, stv, 0, stv.length);
	    				lastPos += stv.length;    				
	    				
	    				obisMap.put("Entry", DataUtil.getLongToBytes(stv));
					}else{
						log.error("###########  확인해볼것 (PROFILE_GENERIC_ATTR07 - LOAD_PROFILE) ########, " + obis);
						throw new EMnVSystemException(EMnVExceptionReason.INVALID_DLMS_PROTOCOL);
					}
				}else{
					log.error("###########  확인해볼것 (PROFILE_GENERIC_ATTR07) ########, " + obis);	
				}
				break;
			case PROFILE_GENERIC_ATTR08: // profile_entries, doublie-long-unsigned : 최대 저장개수
				/* Load Profile */
				if(obis == OBIS.LOAD_PROFILE){
					byte[] ra03T = new byte[1];    // T
					System.arraycopy(data, lastPos, ra03T, 0, ra03T.length);
					lastPos += ra03T.length;
					byte[] stv = null;
					DLMS_TAG_TYPE ra03Type = DLMS_TAG_TYPE.getItem(DataUtil.getIntToByte(ra03T[0]));
					if(ra03Type == DLMS_TAG_TYPE.UINT32){
						stv = new byte[ra03Type.getLenth()];   				
	    				System.arraycopy(data, lastPos, stv, 0, stv.length);
	    				lastPos += stv.length;    				
	    				
	    				obisMap.put("Max Entry", DataUtil.getLongToBytes(stv));
					}else{
						log.error("###########  확인해볼것 (PROFILE_GENERIC_ATTR08 - LOAD_PROFILE) ########, " + obis);
					}
				}else{
					log.error("###########  확인해볼것 (PROFILE_GENERIC_ATTR08) ########, " + obis);
				}
				break;
			default:
				break;
			} // switch - attr finish
			break;  // CASE - PROFILE_GENERIC Finish
		case CLOCK: //8
	        //DLMSEMnVGtypeTag dlmsTag = new DLMSEMnVGtypeTag();   
			dlmsTag = new DLMSEMnVGtypeTag();
			
			//byte[] t = new byte[1];    // T
			t = new byte[1];    // T
			System.arraycopy(data, pos, t, 0, t.length);
			lastPos += t.length;
			dlmsTag.setTag(t[0]);

			//int tlvLenth = 0;
			tlvLenth = 0;
			//byte[] l = new byte[1];    // L
			l = new byte[1];    // L
			
			if (dlmsTag.getTag() == DLMS_TAG_TYPE.OctetString || dlmsTag.getTag() == DLMS_TAG_TYPE.VisibleString) {
    			System.arraycopy(data, lastPos, l, 0, l.length);
    			lastPos += l.length;
    			tlvLenth = DataUtil.getIntToByte(l[0]);
            } else if (dlmsTag.getTag() == DLMS_TAG_TYPE.BitString) {
    			System.arraycopy(data, lastPos, l, 0, l.length);
    			tlvLenth = DataUtil.getIntToByte(l[0]);
    			lastPos += l.length;
    			
    			tlvLenth /= 8;
    			tlvLenth += 1;
            }else{
            	tlvLenth = dlmsTag.getTag().getLenth();
            }

			//byte[] v = new byte[tlvLenth];   //V
			v = new byte[tlvLenth];   //V
            System.arraycopy(data, lastPos, v, 0, v.length);
            lastPos += v.length;
            
            dlmsTag.setLength(tlvLenth);
            dlmsTag.setData(v);     
            //Object dlmsValue = dlmsTag.getValue();
            dlmsValue = dlmsTag.getValue();
            
			switch (attr) {
			case CLOCK_ATTR02: // TIME , octet-string
				try {
					/* 일자 / 시간 */
					if (obis == OBIS.METER_TIME) {
						//ret.put("MeterTime", makeDateTime(dlmsTags.get(0).getData()));
						obisMap.put(OBIS.METER_TIME.name(), makeDateTime(dlmsTag.getData()));
					}
				} catch (Exception e) {
				}
				break;
			default:
				break;
			}// switch - attr finish
			break;  // CASE - CLOCK Finish
		case SINGLE_ACTION_SCHEDULE: //22
			switch (attr) {
			case SINGLE_ACTION_SCHEDULE_ATTR04: // execution_time, array
				try {
					String dateTime = "";
					
			        int tPos = lastPos;
			        //DLMSEMnVGtypeTag dlmsTag = null;
			     //   dlmsTag = new DLMSEMnVGtypeTag();
					
					byte[] firstT = new byte[1];    // 첫 TLV의 T
					System.arraycopy(data, tPos, firstT, 0, firstT.length);
					DLMS_TAG_TYPE firstTagType = DLMS_TAG_TYPE.getItem(DataUtil.getIntToByte(firstT[0]));
					tPos += firstT.length;
					
					if(firstTagType == DLMS_TAG_TYPE.Array ){  // 배열일 경우
						byte[] ll = new byte[1];    // TLV의 L      일단 1바이트라고 가정한다. 127개(?)이상일경우 다시 수정할것.
						System.arraycopy(data, tPos, ll, 0, ll.length);
						tPos += ll.length;
						
		    			int arrayTLVCount = DataUtil.getIntToByte(ll[0]);  // Array의 갯수
						
		    			for(int j=0; j<arrayTLVCount; j++){
		    //				dlmsTag = new DLMSEMnVGtypeTag();   
		    				
		    				byte[] arrT = new byte[1];    // Array의 T
		    				System.arraycopy(data, tPos, arrT, 0, arrT.length);
		    				tPos += arrT.length;
		    				//dlmsTag.setTag(arrT[0]);
		    				
		    				DLMS_TAG_TYPE arrayTagType = DLMS_TAG_TYPE.getItem(DataUtil.getIntToByte(arrT[0]));
		    				if(arrayTagType == DLMS_TAG_TYPE.Structure){
								byte[] structureL = new byte[1];    // Structure의 L     
								System.arraycopy(data, tPos, structureL, 0, structureL.length);
								tPos += structureL.length;
								
				    			int strucutureTLVCount = DataUtil.getIntToByte(structureL[0]);  // Structure의 갯수. 2가와야함.
				    			
				    			
				    			for(int k=0; k<strucutureTLVCount; k++){
				    				byte[] timeT = new byte[1];            // Octet-string 이 와야함.   
									System.arraycopy(data, tPos, timeT, 0, timeT.length);
									tPos += timeT.length;									
									
				    				byte[] timeL = new byte[1];   
									System.arraycopy(data, tPos, timeL, 0, timeL.length);
									tPos += timeL.length;
									
				    				byte[] timeV = new byte[DataUtil.getIntToByte(timeL[0])];   
									System.arraycopy(data, tPos, timeV, 0, timeV.length);
									tPos += timeV.length;
									
									if(k == 0){
										dateTime = DataUtil.getTimeByDLMS_OCTETSTRING4(timeV);										
									}else {
										dateTime = DataUtil.getDateByDLMS_OCTETSTRING5(timeV) + dateTime;
									}
				    			}
		    				}else{
		    					log.error("Structure 구조가 와야함.");
		    				}
		    			}
		    			
					}else {
		    				log.error("Aray 구조가 와야함.");
		    		}

					lastPos = tPos;
					
					
					/* 정기 검침일 */
					if (obis == OBIS.REGULAR_LP_DATE) {
						obisMap.put(OBIS.REGULAR_LP_DATE.name(), dateTime);
					}
					/* 비정기 검침일 */
					else if (obis == OBIS.NON_REGULAR_LP_DATE) {
						obisMap.put(OBIS.NON_REGULAR_LP_DATE.name(),dateTime);
					}
				} catch (Exception e) {
					log.error("에러발생 - ", e);
				}
				break;

			default:
				break;
			}// switch - attr finish
			break;  // CASE - SINGLE_ACTION_SCHEDULE Finish
		default:
			break;  // CASE - default Finish
		}  // switch - clazz finish.

		log.info("[PROTOCOL][METERING_DATA] SET_DATA: LAST_POS=[{}][{}] ==> {}", new Object[]{lastPos, obis, obisMap.toString()});
    	
		return lastPos;
	}  // setData finish.

	public Object getData() {
		return obisMap;
	}
	
	
	/**
	 * 
	 * @param data 
	 * @param pos
	 * @return
	 * @throws Exception 
	 */
	public Object[] parseOBIS_7_2(byte[] data, int pos) throws Exception{
		Object[] result = new Object[2];
		List<Object> arrayList = new ArrayList<Object>();
		
        int tPos = pos;
        printHexByteString(data, tPos, 160);
		byte[] firstT = new byte[1];    // 첫 TLV의 T
		System.arraycopy(data, tPos, firstT, 0, firstT.length);
		DLMS_TAG_TYPE firstTagType = DLMS_TAG_TYPE.getItem(DataUtil.getIntToByte(firstT[0]));
		tPos += firstT.length;
		
		if(firstTagType == DLMS_TAG_TYPE.Null){  // LOAD PROFILE 할때 00이 나옴. 이유는 모름.
			log.debug("################## 이거아직도나옴 ###########");
			log.debug("################## 이거아직도나옴 ###########");
			log.debug("################## 이거아직도나옴 ###########");
			log.debug("################## 이거아직도나옴 ###########");
			log.debug("################## 이거아직도나옴 ###########");
			log.debug("################## 이거아직도나옴 ###########");
			log.debug("################## 이거아직도나옴 ###########");
			
			throw new EMnVSystemException(EMnVExceptionReason.INVALID_DLMS_PROTOCOL);
			
//			tPos++;
//			
//			firstT = new byte[1];    // 첫 TLV의 T
//			System.arraycopy(data, tPos, firstT, 0, firstT.length);
//			firstTagType = DLMS_TAG_TYPE.getItem(DataUtil.getIntToByte(firstT[0]));
//			tPos += firstT.length;
		}
		
		if(firstTagType == DLMS_TAG_TYPE.Array ){  // 배열일 경우
			byte[] ll = new byte[1];    // TLV의 L      일단 1바이트라고 가정한다. 127개(?)이상일경우 다시 수정할것.
			System.arraycopy(data, tPos, ll, 0, ll.length);
			tPos += ll.length;
			
			int arrayTLVCount = DataUtil.getIntToByte(ll[0]);  // Array의 갯수
			
			// Array다음에 바로 갯수가 안나오고 0x8... 로 시작하는경우 처리
			String hexString = Hex.getHexDump(ll);
			if(hexString.startsWith("8")){
				log.warn("### !!!!!!!!!  array다음에 0x8x나옴  확인해볼것. ####");
				log.warn("### !!!!!!!!!  array다음에 0x8x나옴  확인해볼것. ####");
				log.warn("### !!!!!!!!!  array다음에 0x8x나옴  확인해볼것. ####");
				String binaryString = Integer.toBinaryString(arrayTLVCount);
				binaryString = "0" + binaryString.substring(1, 8);
				int countByteLenth = Integer.parseInt(binaryString, 2);
				
				ll = new byte[countByteLenth];
				System.arraycopy(data, tPos, ll, 0, countByteLenth);
				tPos += countByteLenth;
				arrayTLVCount = DataUtil.getIntToBytes(ll);  // Array의 갯수
			}
			
			for(int j=0; j<arrayTLVCount; j++){
				
				byte[] arrT = new byte[1];    // Array의 T
				System.arraycopy(data, tPos, arrT, 0, arrT.length);
				tPos += arrT.length;
				
				DLMS_TAG_TYPE arrayTagType = DLMS_TAG_TYPE.getItem(DataUtil.getIntToByte(arrT[0]));
				if(arrayTagType == DLMS_TAG_TYPE.Structure){
					byte[] structureL = new byte[1];    // Structure의 L     
					System.arraycopy(data, tPos, structureL, 0, structureL.length);
					tPos += structureL.length;
					
	    			int strucutureTLVCount = DataUtil.getIntToByte(structureL[0]);  // Structure의 갯수.
	    			Object[] structureValues = new Object[strucutureTLVCount];
	    			for(int k=0; k<strucutureTLVCount; k++){
	    				byte[] st = new byte[1];  // T
	    				System.arraycopy(data, tPos, st, 0, st.length);
						tPos += st.length;
						
						byte[] stl = new byte[1]; // L
						int lenth = 0;
						DLMS_TAG_TYPE type = DLMS_TAG_TYPE.getItem(DataUtil.getIntToByte(st[0])); 
	    				if (type == DLMS_TAG_TYPE.OctetString || type == DLMS_TAG_TYPE.VisibleString) {
	    	    			System.arraycopy(data, tPos, stl, 0, stl.length);
	    	    			tPos += stl.length;
	    	    			lenth = DataUtil.getIntToByte(stl[0]);
	    	            } else if (type == DLMS_TAG_TYPE.BitString) {
	    	    			System.arraycopy(data, tPos, stl, 0, stl.length);
	    	    			lenth = DataUtil.getIntToByte(stl[0]);
	    	    			tPos += stl.length;
	    	    			
	    	    			if(8 <= lenth){
	    	    				if(0 < (lenth % 8)){
	    	    	    			lenth /= 8;
	    	    	    			lenth++;
	    	    				}else{
	    	    	    			lenth /= 8;	    	    					
	    	    				}
	    	    			}else{
	    	    				lenth = 1;
	    	    			}
	    	            }else {
	    	            	lenth = type.getLenth();
	    	            }
	    				
	    				byte[] stv = new byte[lenth];   				
	    				System.arraycopy(data, tPos, stv, 0, stv.length);
						tPos += stv.length;
	    				
						if(type == DLMS_TAG_TYPE.OctetString){
							structureValues[k] = DataUtil.getDateTimeByDLMS_OCTETSTRING_Length(stv);	
						}else if(type == DLMS_TAG_TYPE.INT16 || type == DLMS_TAG_TYPE.UINT16 || type == DLMS_TAG_TYPE.INT32 || type == DLMS_TAG_TYPE.UINT32){
							structureValues[k] = new Long(DataUtil.getLongToBytes(stv));
						}else if(type == DLMS_TAG_TYPE.INT8 || type == DLMS_TAG_TYPE.UINT8){          
							structureValues[k] = new Integer(DataUtil.getIntToBytes(stv));
						}
						else if(type == DLMS_TAG_TYPE.FLOAT32){
							structureValues[k] = new Float(DataUtil.getFloat(stv, 0));
						}else if(type == DLMS_TAG_TYPE.Null){
							structureValues[k] = new OCTET(stv);
						}else if(type == DLMS_TAG_TYPE.BitString){
							structureValues[k] = new OCTET(stv);
						}else {
							structureValues[k] = new OCTET(stv); 
						}
						//arrayList.add(structureValues);  // Array 목록		
	    			}
	    			
	    			arrayList.add(structureValues);  // Array 목록
				}else{
					log.error("DLMS_TAG_TYPE {} - Structure 구조가 와야함.", arrayTagType);
				}
				
				
				
			}
			
		}else {
				log.error("Array 구조가 와야함.");
		}
		
		result[0] = tPos;
		result[1] = arrayList;
		
		return result; 
	}

    /**
     * System.out.println() 으로 HEX 출력.
     * @param data
     * @param tPos
     * @param showLength
     */
    public void printHexByteString(byte[] data, int tPos, int showLength){
		int loggingLenth = (data.length - tPos) < showLength ? data.length - tPos : showLength;
		
		byte[] logging = new byte[showLength];
		System.arraycopy(data, tPos, logging, 0, loggingLenth);
		log.info("### SHOW HEX POS[" + tPos + "] 부터 "+ loggingLenth +"byte ==> " + Hex.getHexDump(logging));
    }
}


