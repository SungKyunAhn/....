package com.aimir.fep.meter.parser.DLMSEMnVEtype_1_0_Table;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.fep.meter.parser.DLMSEMnVEtype_1_0_Table.DLMSEMnVEType_1_0_VARIABLE.DLMS_CLASS;
import com.aimir.fep.meter.parser.DLMSEMnVEtype_1_0_Table.DLMSEMnVEType_1_0_VARIABLE.DLMS_CLASS_ATTR;
import com.aimir.fep.meter.parser.DLMSEMnVEtype_1_0_Table.DLMSEMnVEType_1_0_VARIABLE.DLMS_TAG_TYPE;
import com.aimir.fep.meter.parser.DLMSEMnVEtype_1_0_Table.DLMSEMnVEType_1_0_VARIABLE.LOAD_PROFILE;
import com.aimir.fep.meter.parser.DLMSEMnVEtype_1_0_Table.DLMSEMnVEType_1_0_VARIABLE.METER_CONSTANT;
import com.aimir.fep.meter.parser.DLMSEMnVEtype_1_0_Table.DLMSEMnVEType_1_0_VARIABLE.OBIS;
import com.aimir.fep.protocol.emnv.exception.EMnVSystemException;
import com.aimir.fep.protocol.emnv.exception.EMnVSystemException.EMnVExceptionReason;
import com.aimir.fep.protocol.emnv.frame.EMnVConstants.EMnVMeteringDataType;
import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

public class DLMSEMnVEType_1_0_Table {
	private static Logger log = LoggerFactory.getLogger(DLMSEMnVEType_1_0_Table.class);
	private DLMSEMnVEType_1_0_Header dlmsHeader = new DLMSEMnVEType_1_0_Header();
	private List<DLMSEMnVEType_1_0_Tag> dlmsTags = new ArrayList<DLMSEMnVEType_1_0_Tag>();
	Map<String, Object> obisMap = new LinkedHashMap<String, Object>();

	public DLMSEMnVEType_1_0_Header getDlmsHeader() {
		return dlmsHeader;
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
			DLMSEMnVEType_1_0_Tag dlmsTag = new DLMSEMnVEType_1_0_Tag();   
			
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
				dlmsTag = new DLMSEMnVEType_1_0_Tag();
				t = new byte[1];    // T
				System.arraycopy(data, pos, t, 0, t.length);
				lastPos += t.length;
				dlmsTag.setTag(t[0]);

				tlvLenth = 0;
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

				v = new byte[tlvLenth];   //V
	            System.arraycopy(data, lastPos, v, 0, v.length);
	            lastPos += v.length;
	            
	            dlmsTag.setLength(tlvLenth);
	            dlmsTag.setData(v);     
	            dlmsValue = dlmsTag.getValue();
	            
				switch (obis) {
				case LP_INTERVAL:  /* Load Profile 기록간격 */
					obisMap.put(OBIS.LP_INTERVAL.name(), (dlmsValue instanceof OCTET? ((OCTET)dlmsValue).toString().trim() : dlmsValue));
					break;
				case METER_CONSTANT_ACTIVE:  /* 유효전력량 계기정수 */
					obisMap.put(METER_CONSTANT.ActiveC.name(),(dlmsValue instanceof OCTET? ((OCTET)dlmsValue).toString().trim() : dlmsValue));
					break;
				case IMPORT_ACTIVE_ENERGY:  /* 유효전력량 (현월) */
					obisMap.put(OBIS.IMPORT_ACTIVE_ENERGY.name(),(dlmsValue instanceof OCTET? ((OCTET)dlmsValue).toString().trim() : dlmsValue));
					break;
				default:
					break;
				}

				break;
			case REGISTER_ATTR03: // scaler_unit, scal_unit_type  : 단위 
				if (obis == OBIS.LP_INTERVAL 
						|| obis == OBIS.METER_CONSTANT_ACTIVE  
						|| obis == OBIS.IMPORT_ACTIVE_ENERGY) {
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
		case PROFILE_GENERIC: //7
			switch (attr) {
			case PROFILE_GENERIC_ATTR02: //  buffer, octet-string : 값
				/* Load Profile */
				if(obis == OBIS.LOAD_PROFILE){
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
							
							/** E-Type저압전자식전력량계(1.0).pdf 참조
							 *  1 일자/시간
								2 순방향 유효전력량[Q1+Q4]
							 */
							obisMap.put(LOAD_PROFILE.Date.name() + "-" + i, values[0]);                    // 일자/시간
							obisMap.put(LOAD_PROFILE.ImportActive.name() + "-" + i , values[1]); // 순방향 유효전력량  
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
			default:
				break;
			} // switch - attr finish
			break;  // CASE - PROFILE_GENERIC Finish
		case CLOCK: //8
			dlmsTag = new DLMSEMnVEType_1_0_Tag();
			t = new byte[1];    // T
			System.arraycopy(data, pos, t, 0, t.length);
			lastPos += t.length;
			dlmsTag.setTag(t[0]);
			tlvLenth = 0;
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

			v = new byte[tlvLenth];   //V
            System.arraycopy(data, lastPos, v, 0, v.length);
            lastPos += v.length;
            
            dlmsTag.setLength(tlvLenth);
            dlmsTag.setData(v);     
            dlmsValue = dlmsTag.getValue();
            
			switch (attr) {
			case CLOCK_ATTR02: // TIME , octet-string
				try {
					/* 일자 / 시간 */
					if (obis == OBIS.METER_TIME) {
						obisMap.put(OBIS.METER_TIME.name(), makeDateTime(dlmsTag.getData()));
					}
				} catch (Exception e) {
				}
				break;
			default:
				break;
			}// switch - attr finish
			break;  // CASE - CLOCK Finish
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
