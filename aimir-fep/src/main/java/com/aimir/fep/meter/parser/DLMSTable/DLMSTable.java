package com.aimir.fep.meter.parser.DLMSTable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.parser.DLMSTable.DLMSTag;
import com.aimir.fep.meter.parser.DLMSTable.DLMSVARIABLE.DLMS_CLASS;
import com.aimir.fep.meter.parser.DLMSTable.DLMSVARIABLE.DLMS_CLASS_ATTR;
import com.aimir.fep.meter.parser.DLMSTable.DLMSVARIABLE.DLMS_TAG_TYPE;
import com.aimir.fep.meter.parser.DLMSTable.DLMSVARIABLE.KEPCO_CURRENT_MAX_DEMAND;
import com.aimir.fep.meter.parser.DLMSTable.DLMSVARIABLE.KEPCO_METER_INFO;
import com.aimir.fep.meter.parser.DLMSTable.DLMSVARIABLE.KEPCO_PREVIOUS_MAX_DEMAND;
import com.aimir.fep.meter.parser.DLMSTable.DLMSVARIABLE.METER_CONSTANT;
import com.aimir.fep.meter.parser.DLMSTable.DLMSVARIABLE.MONTHLY_DEMAND_PROFILE;
import com.aimir.fep.meter.parser.DLMSTable.DLMSVARIABLE.OBIS;
import com.aimir.fep.meter.parser.DLMSTable.DLMSVARIABLE.EVENT_LOG;
import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

public class DLMSTable {
    private static Log log = LogFactory.getLog(DLMSTable.class);
    
    private DLMSHeader dlmsHeader = new DLMSHeader();
    private List<DLMSTag> dlmsTags = new ArrayList<DLMSTag>();
    
    public DLMSHeader getDlmsHeader() {
        return dlmsHeader;
    }
    public void setDlmsHeader(DLMSHeader dlmsHeader) {
        this.dlmsHeader = dlmsHeader;
    }
    public List<DLMSTag> getDlmsTags() {
        return dlmsTags;
    }
    public void setDlmsTags(List<DLMSTag> dlmsTags) {
        this.dlmsTags = dlmsTags;
    }
    public void addDlmsTag(DLMSTag tag) {
        this.dlmsTags.add(tag);
    }
    
    public void setObis(String obisCode) {
        this.dlmsHeader.setObis(obisCode);
    }
    
    public void setClazz(int clazz) {
        this.dlmsHeader.setClazz(clazz);
    }
    
    public void setAttr(int attr) {
        this.dlmsHeader.setAttr(attr);
    }
    
    public void setLength(int length) {
        this.dlmsHeader.setLength(length);
    }
    
    public void parseDlmsTag(byte[] data) throws Exception {
        
        int len = DLMS_TAG_TYPE.Null.getLenth() - 1;
        int pos = 0;
        DLMSTag dlmsTag = null;
        byte[] bx = null;
        
        while (pos != data.length) {
            log.debug("LEN[" + data.length + "] POS[" + pos + "]");
            dlmsTag = new DLMSTag();
            dlmsTag.setTag(data[pos]);
            pos += 1;
            
            /*
             * BitString, OctetString, VisibleString 태그에 대해서만 data[1] 바이트를
             * 확장 길이로 사용하고 데이타에 대한 길이이다.
             * 단, 0x80 으로 and연산으로 0x80 이 되면 0x7F 로 and을 하여 길이를 구한다.
             */
            if (data.length > pos) {
	            if ((data[pos] & 0x80) == 0x80 ) {
	                len = (data[pos] & 0x7F);
	                pos += 1;
	            }
	            else {
	                if (dlmsTag.getTag() == DLMS_TAG_TYPE.OctetString ||
	                                dlmsTag.getTag() == DLMS_TAG_TYPE.VisibleString) {
	                    len = DataUtil.getIntToByte(data[pos]);
	                    pos += 1;
	                }else if(dlmsTag.getTag() == DLMS_TAG_TYPE.BitString){
	                	int bitLen = DataUtil.getIntToByte(data[pos]);
	                	
	                	if(bitLen > 0){
	                		if(bitLen % 8 == 0){
	                			len = bitLen /8;
	                		}else{
	                			len = (bitLen /8) + 1 ;
	                		}
	                	}else{
	                		len = 0;
	                	}
	                    pos += 1;
	                }
	                else len = dlmsTag.getTag().getLenth();
	            }
            }
            else continue;
            /*
            if (dlmsTag.getTag() == DLMS_TAG_TYPE.OctetString ||
                    dlmsTag.getTag() == DLMS_TAG_TYPE.VisibleString) {
                len = DataUtil.getIntToByte(data[pos]);
                pos += 1;
            }
            else len = dlmsTag.getTag().getLenth();
            */
            
            bx = new byte[len];
            
            // pos와 data의 길이 합이 data.length를 넘어갈 수도 있기 때문에 비교를 한 후에 arrayindexout 예외가 발생하지 않도록 한다.
            if (pos + len > data.length)
                break;
            
            System.arraycopy(data, pos, bx, 0, bx.length);
            pos += bx.length;

            dlmsTag.setLength(len);
            dlmsTag.setData(bx);
            log.debug(dlmsTag.toString());
            // 예외로 LOAD_PROFILE이고 태그가 Array인 경우는 추가하지 않는다.
            if (dlmsHeader.getObis() == OBIS.LOAD_PROFILE && dlmsTag.getTag() == DLMS_TAG_TYPE.Array)
                continue;
            dlmsTags.add(dlmsTag);
        }
    }
    
    /**
     * class, attr에 따라 데이타의 개수와 유형이 달라진다.
     * @return
     */
    public Map<String, Object> getData() {
        Map<String, Object> ret = new LinkedHashMap<String, Object>(16, 0.75f, false);
        DLMS_CLASS clazz = dlmsHeader.getClazz();
        DLMS_CLASS_ATTR attr = dlmsHeader.getAttr();
        OBIS obis = dlmsHeader.getObis();
        log.debug("CLAZZ[" + clazz + "] ATTR[" + attr + "] OBIS[" + obis + "] TAG_SIZE[" + dlmsTags.size() + "]");
        switch (clazz) {
        case DATA:
            switch (attr) {
            case DATA_ATTR01:
                if (obis == OBIS.DEVICE_INFO && dlmsTags.size() != 0) {
                    ret.put("DeviceName", dlmsTags.get(0).getOCTET().toString());
                } else if (obis == OBIS.VZ && dlmsTags.size() != 0) {
                    ret.put("VZ", dlmsTags.get(0).getUint8());
                } else if (obis == OBIS.BILLING_PERIOD && dlmsTags.size() != 0) {
                    ret.put("BillingPeroid", dlmsTags.get(0).getUint8());
                } else if (obis == OBIS.MANUFACTURER_METER_ID && dlmsTags.size() != 0) {
                    ret.put("ManufacturerMeterId", dlmsTags.get(0).getOCTET().toString());
                } else if (obis == OBIS.POWER_FAILURE_COUNT && dlmsTags.size() != 0) {
                    ret.put("NumberOfPowerFailures", dlmsTags.get(0).getValue());
                } else if (obis == OBIS.PROGRAM_CHANGE_COUNT && dlmsTags.size() != 0) {
                    ret.put("ProgramChangeCount", dlmsTags.get(0).getValue());
                } else if (obis == OBIS.PROGRAM_CHANGE_DATE && dlmsTags.size() != 0) {
                    ret.put("ProgramChangeDate", dlmsTags.get(0).getOCTET().toString());
                } else if (obis == OBIS.PROGRAM_CHANGE_RESERVEDATE && dlmsTags.size() != 0) {
                    ret.put("ProgramChangeReserveDate", dlmsTags.get(0).getOCTET().toString());
                } else if (obis == OBIS.BATTERY_USE_TIME && dlmsTags.size() != 0) {
                    ret.put("BatteryUseTime", dlmsTags.get(0).getValue());
                } else if (obis == OBIS.SELF_CHECK_BATTERY && dlmsTags.size() != 0) {
                    ret.put("SelfCheckBatteryWarning", dlmsTags.get(0).getOCTET().toString());
                } else if (obis == OBIS.SELF_CHECK_MEMORY && dlmsTags.size() != 0) {
                    ret.put("SelfCheckMemoryWarning", dlmsTags.get(0).getOCTET().toString());
                } else if (obis == OBIS.SELF_CHECK_POWER && dlmsTags.size() != 0) {
                    ret.put("SelfCheckPowerWarning", dlmsTags.get(0).getOCTET().toString());
                }
            }
            break;
        case REGISTER:
            switch (attr) {
            case REGISTER_ATTR02: // value
                if (obis == OBIS.METER_CONSTANT_ACTIVE && dlmsTags.size() != 0) {
                    for (int i = 0; i < dlmsTags.size(); i++) {
                        putData(ret, OBIS.METER_CONSTANT_ACTIVE, METER_CONSTANT.ActiveC.name(), dlmsTags.get(i));
                    }
                } else if (obis == OBIS.METER_CONSTANT_REACTIVE && dlmsTags.size() != 0) {
                    for (int i = 0; i < dlmsTags.size(); i++) {
                        putData(ret, OBIS.METER_CONSTANT_REACTIVE, METER_CONSTANT.ReactiveC.name(), dlmsTags.get(i));
                    }
                }
                break;
            case REGISTER_ATTR03: // scaler_unit
                if (obis == OBIS.METER_CONSTANT_ACTIVE && dlmsTags.size() > 2) {
                    ret.put("Scaler", dlmsTags.get(0).getInt8());
                    ret.put("unit", dlmsTags.get(1).getUint8());
                }
                break;
            }
            break;
        case PROFILE_GENERIC:
            switch (attr) {
            case PROFILE_GENERIC_ATTR02: // buffer
                putData(dlmsTags, ret, obis);
                break;
			case PROFILE_GENERIC_ATTR04: // capture period
				Object o = dlmsTags.get(0).getValue();
				if (o instanceof Long) {
					Long val = (Long) o;
					val = val.longValue() / 60;//sec -> min
					ret.put("LpInterval", val);
				} else if (o instanceof Integer) {
					Integer val = (Integer) o;
					val = val.intValue() / 60;//sec -> min
					ret.put("LpInterval", val);
				} else {
					ret.put("LpInterval", dlmsTags.get(0).getValue());
				}
            	break;
            case PROFILE_GENERIC_ATTR07: // entries_in_use
                if (dlmsTags.size() == 0)
                    break;
                ret.put("Entry", dlmsTags.get(0).getValue());
                break;
            }
            break;
        case EXTEND_REGISTER :
            switch (attr) {
            	case EXTEND_REGISTER_ATTR02 :
            		 ret.put("Billing Total Energy", dlmsTags.get(0).getValue());
            		break;
            	case EXTEND_REGISTER_ATTR04 :
            		ret.put("Billing Total Energy Status", dlmsTags.get(0).getValue());
            		break;
            	case EXTEND_REGISTER_ATTR05 : 
            		
            		OCTET octet = (OCTET)dlmsTags.get(0).getValue();
                    byte[] data = octet.getValue();
                    if (data.length >= 12) {
        	            int year = 2000;
						try {
							year = DataFormat.getIntTo2Byte(DataFormat.select(data, 0, 2));
						} catch (Exception e) {
							e.printStackTrace();
						}
        	            int month = DataFormat.getIntToByte(data[2]);
        	            int day = DataFormat.getIntToByte(data[3]);
        	            int week = DataFormat.getIntToByte(data[4]);
        	            int hour = DataFormat.getIntToByte(data[5]);
        	            int min = DataFormat.getIntToByte(data[6]);
        	            int sec = DataFormat.getIntToByte(data[7]);
        	            DecimalFormat df = new DecimalFormat("00");
        	            String str = year + df.format(month) + df.format(day)
        	                    + df.format(hour) + df.format(min)+df.format(sec);
                		ret.put("Billing Date Time", str);
                    }
            		break;
            }
            break;
        }
        
        return ret;
    }
    
    /*
     * 하나의 OBIS에 여러 개의 태그를 가지는 경우 그 순서에 따라 데이타 유형이 결정된다.
     */
    private void putData(List<DLMSTag> tags, Map<String, Object> ret, OBIS obis) {

        String name = null;
        int channelCount = 0;
        int channelIndex = 1;
        if(obis.equals(OBIS.LOAD_PROFILE)){
            name = "";
            
            for(DLMSTag tag : tags){
            	if(tag.getTag().equals(DLMS_TAG_TYPE.Structure)){                	
                	
            		OCTET octet = (OCTET)tag.getValue();
            		channelCount = DataUtil.getIntToBytes(octet.getValue());
                	channelIndex = 1;
            	}else if(tag.getTag().equals(DLMS_TAG_TYPE.Array)){
            		// skip
            	}else{

            		if(tag.getTag().equals(DLMS_TAG_TYPE.OctetString)){
            			name = "DateTime";
            		}else if(tag.getTag().equals(DLMS_TAG_TYPE.Datetime)){
            			name = "DateTime";
					}
					else{
            			name = "Channel["+channelIndex+"]";
            			channelIndex++;
            		}
            		putData(ret, obis, name, tag);
            	}
            }

        }else{
            for (int i = 0; i < tags.size(); i++) {
                name = DLMSVARIABLE.getDataName(obis, i);
                putData(ret, obis, name, tags.get(i));
            }
        }

    }
    
    private Object putData(Map<String, Object> map, OBIS obis, String dataName, DLMSTag tag) {
        try {
            switch (obis) {
            case METER_TIME :
            	// getOBIS_CODE_METER_TIME(map, dataName, tag);
            	break;
            case KEPCO_METER_INFO :
                getOBIS_CODE_KEPCO_METER_INFO(map, dataName, tag);
                break;
            case METER_CONSTANT_ACTIVE :
            case METER_CONSTANT_REACTIVE :
                getOBIS_CODE_METER_CONSTANT_ACTIVE(map, dataName, tag);
                break;
            case KEPCO_CURRENT_MAX_DEMAND :
                getOBIS_CODE_KEPCO_CURRENT_MAX_DEMAND(map, dataName, tag);
                break;
            case KEPCO_PREVIOUS_MAX_DEMAND :
                getOBIS_CODE_KEPCO_PREVIOUS_MAX_DEMAND(map, dataName, tag);
                break;
            case MONTHLY_ENERGY_PROFILE :
                getOBIS_CODE_MONTHLY_ENERY_PROFILE(map, dataName, tag);
                break;
            case MONTHLY_DEMAND_PROFILE :
                getOBIS_CODE_MONTHLY_DEMAND_PROFILE(map, dataName, tag);
                break;
            case BILLING_ENERGY :
            	getOBIS_CODE_BILLING_ENERY_PROFILE(map, dataName, tag);
            	break;
            case BILLING_MAX_DEMAND :
            	getOBIS_CODE_BILLING_MAX_DEMAND_PROFILE(map, dataName, tag);
            	break;
            case POWER_FAILURE :
            case POWER_RESTORE :
            case TIME_CHANGE_FROM :
            case TIME_CHANGE_TO :
            case DEMAND_RESET :
            case MANUAL_DEMAND_RESET :
            case SELF_READ :
            case PROGRAM_CHANGE :
                getOBIS_CODE_EVENT_LOG(map, dataName, tag);
                break;
            case LOAD_PROFILE :
                getOBIS_CODE_LOAD_PROFILE(map, dataName, tag);
            }
        } catch (Exception e) {
            e.printStackTrace();
           log.error("obis:"+obis+":dataName:"+dataName+":tag:"+tag);
        }
        return tag.getValue();
    }

    private void getOBIS_CODE_LOAD_PROFILE(Map<String, Object> map, String dataName, DLMSTag tag)
            throws Exception {
    	log.debug("DATA_NAME[" + dataName + "] VALUE[" + (tag.getValue() instanceof OCTET? Hex.decode(((OCTET)tag.getValue()).getValue()):tag.getValue()) + "]");
    	
    	/*
        if (dataName.equals(LOAD_PROFILE.Date.name())) {
            byte[] data = tag.getData();
            if (data.length == 12) {
	            int year = DataFormat.getIntTo2Byte(DataFormat.select(data, 0, 2));
	            int month = DataFormat.getIntToByte(data[2]);
	            int day = DataFormat.getIntToByte(data[3]);
	            int week = DataFormat.getIntToByte(data[4]);
	            int hour = DataFormat.getIntToByte(data[5]);
	            int min = DataFormat.getIntToByte(data[6]);
	            DecimalFormat df = new DecimalFormat("00");
	            String str = year + df.format(month) + df.format(day)
	                    + df.format(hour) + df.format(min);
	            String key = dataName;
	            for (int cnt = 0; ;cnt++) {
	                key = dataName + "-" + cnt;
	                if (!map.containsKey(key)) {
	                    map.put(key, str);
	                    break;
	                }
	            }
            }
        }
        else if (dataName.equals(LOAD_PROFILE.Status.name())) {
            byte[] data = tag.getData();
            int value = DataUtil.getIntToBytes(data);

            String binaryString = Integer.toBinaryString(value);

            while (binaryString.length() % 8 != 0) {
                binaryString = "0" + binaryString;
            }
            // log.debug("binaryString:"+binaryString);
            String str = binaryString;
            String key = dataName;
            for (int cnt = 0; ;cnt++) {
                key = dataName + "-" + cnt;
                if (!map.containsKey(key)) {
                    map.put(key, str);
                    break;
                }
            }
        }
        else {
            String key = dataName;
            for (int cnt = 0; ;cnt++) {
                key = dataName + "-" + cnt;
                if (!map.containsKey(key)) {
                    map.put(key, tag.getValue());
                    break;
                }
            }
        } 
        */
    	
        if (dataName.equals("DateTime")) {
            byte[] data = tag.getData();
            if (data.length == 12) {
	            int year = DataFormat.getIntTo2Byte(DataFormat.select(data, 0, 2));
	            int month = DataFormat.getIntToByte(data[2]);
	            int day = DataFormat.getIntToByte(data[3]);
	            int week = DataFormat.getIntToByte(data[4]);
	            int hour = DataFormat.getIntToByte(data[5]);
	            int min = DataFormat.getIntToByte(data[6]);
	            DecimalFormat df = new DecimalFormat("00");
	            String str = year + df.format(month) + df.format(day)
	                    + df.format(hour) + df.format(min);
	            String key = dataName;
	            for (int cnt = 0; ;cnt++) {
	                key = dataName + "-" + cnt;
	                if (!map.containsKey(key)) {
	                    map.put(key, str);
	                    break;
	                }
	            }
            }
        }
        /*
        else if (dataName.equals(ENERGY_LOAD_PROFILE.Status.name())) {
            byte[] data = tag.getData();
            int value = DataUtil.getIntToBytes(data);

            String binaryString = Integer.toBinaryString(value);

            while (binaryString.length() % 8 != 0) {
                binaryString = "0" + binaryString;
            }
            // log.debug("binaryString:"+binaryString);
            String str = binaryString;
            String key = dataName;
            for (int cnt = 0; ;cnt++) {
                key = dataName + "-" + cnt;
                if (!map.containsKey(key)) {
                    map.put(key, str);
                    break;
                }
            }
        }
        */
        else {
            String key = dataName;
            for (int cnt = 0; ;cnt++) {
                key = dataName + "-" + cnt;
                if (!map.containsKey(key)) {
                    map.put(key, tag.getValue());
                    break;
                }
            }
        }
    }

    private void getOBIS_CODE_EVENT_LOG(Map<String, Object> map,
            String dataName, DLMSTag tag) throws Exception {
    	log.debug("DATA_NAME[" + dataName + "] VALUE[" + (tag.getValue() instanceof OCTET? Hex.decode(((OCTET)tag.getValue()).getValue()):tag.getValue()) + "]");
        for (EVENT_LOG el : EVENT_LOG.values()) {
            if (dataName.startsWith(el.name()))
                map.put(dataName, makeDateTime4week(tag.getData()));
            // else map.put(dataName, tag.getValue()); 
        }
    }

    /**
     * DLMS 12bytes OCTET 시간 포맷
     * 년 : 0,1
     * 월 : 2
     * 일 : 3
     * 시 : 5
     * 분 : 6
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
        String str = ":date=" + year + df.format(month) + df.format(day)
                + df.format(hour) + df.format(min);
        
        return str;
    }
    
    private void getOBIS_CODE_MONTHLY_DEMAND_PROFILE(Map<String, Object> map,
            String dataName, DLMSTag tag) throws Exception {

        if (dataName.equals(MONTHLY_DEMAND_PROFILE.ActiveDate.name()) ||
                dataName.equals(MONTHLY_DEMAND_PROFILE.ReActiveDate.name()) ||
                dataName.equals(MONTHLY_DEMAND_PROFILE.T1ActiveDate.name()) ||
                dataName.equals(MONTHLY_DEMAND_PROFILE.T1ReActiveDate.name()) ||
                dataName.equals(MONTHLY_DEMAND_PROFILE.T2ActiveDate.name()) ||
                dataName.equals(MONTHLY_DEMAND_PROFILE.T2ReActiveDate.name()) ||
                dataName.equals(MONTHLY_DEMAND_PROFILE.T3ActiveDate.name()) ||
                dataName.equals(MONTHLY_DEMAND_PROFILE.T3ReActiveDate.name())
                ) {
            map.put(dataName, makeDateTime4week(tag.getData()));
        }
        else {
            map.put(dataName, tag.getValue());
        }
    }

    private void getOBIS_CODE_MONTHLY_ENERY_PROFILE(Map<String, Object> map,
            String dataName, DLMSTag tag) throws Exception {
        map.put(dataName,  tag.getValue());
    }
    
    private void getOBIS_CODE_BILLING_MAX_DEMAND_PROFILE(Map<String, Object> map,
            String dataName, DLMSTag tag) throws Exception {
        if (dataName.equals(MONTHLY_DEMAND_PROFILE.ActiveDate.name()) ||
                dataName.equals(MONTHLY_DEMAND_PROFILE.ReActiveDate.name()) ||
                dataName.equals(MONTHLY_DEMAND_PROFILE.T1ActiveDate.name()) ||
                dataName.equals(MONTHLY_DEMAND_PROFILE.T1ReActiveDate.name()) ||
                dataName.equals(MONTHLY_DEMAND_PROFILE.T2ActiveDate.name()) ||
                dataName.equals(MONTHLY_DEMAND_PROFILE.T2ReActiveDate.name()) ||
                dataName.equals(MONTHLY_DEMAND_PROFILE.T3ActiveDate.name()) ||
                dataName.equals(MONTHLY_DEMAND_PROFILE.T3ReActiveDate.name())
                ) {
            map.put(dataName, makeDateTime4week(tag.getData()));
        }
        else {
            map.put(dataName, tag.getValue());
        }
    }
    
    private void getOBIS_CODE_BILLING_ENERY_PROFILE (Map<String, Object> map,
            String dataName, DLMSTag tag) throws Exception {
        map.put(dataName,  tag.getValue());
    }

    private void getOBIS_CODE_KEPCO_PREVIOUS_MAX_DEMAND(Map<String, Object> map,
            String dataName, DLMSTag tag) throws Exception {
        if (dataName.equals(KEPCO_PREVIOUS_MAX_DEMAND.T1PreviousActiveMaxDate.name()) ||
                dataName.equals(KEPCO_PREVIOUS_MAX_DEMAND.T2PreviousActiveMaxDate.name()) ||
                dataName.equals(KEPCO_PREVIOUS_MAX_DEMAND.T3PreviousActiveMaxDate.name())) {
            map.put(dataName, makeDateTime4week(tag.getData()));
            log.debug("DATA_NAME[" + dataName + "] DATA[" + map.get(dataName) + "]");
        }
        else {
            map.put(dataName, tag.getValue());
        }
    }

    public void getOBIS_CODE_KEPCO_CURRENT_MAX_DEMAND(Map<String, Object> map,
            String dataName, DLMSTag tag) throws Exception {
        if (dataName.equals(KEPCO_CURRENT_MAX_DEMAND.T1CurrentActiveMaxDate.name()) ||
                dataName.equals(KEPCO_CURRENT_MAX_DEMAND.T2CurrentActiveMaxDate.name()) ||
                dataName.equals(KEPCO_CURRENT_MAX_DEMAND.T3CurrentActiveMaxDate.name())) {
            map.put(dataName, makeDateTime4week(tag.getData()));
            log.debug("DATA_NAME[" + dataName + "] DATA[" + map.get(dataName) + "]");
        }
        else {
            map.put(dataName, tag.getValue());
        }
    }

    public void getOBIS_CODE_METER_CONSTANT_ACTIVE(Map<String, Object> map,
            String dataName, DLMSTag tag) throws Exception {
        map.put(dataName, tag.getValue());
    }

    public void getOBIS_CODE_KEPCO_METER_INFO(Map<String, Object> map,
            String dataName, DLMSTag tag) throws Exception {
        
        String str = "";
        if (dataName.equals(KEPCO_METER_INFO.MeterKind.name())) {
            int value = DataUtil.getIntToBytes(((OCTET)tag.getValue()).getValue());

            for (int i = 4; i < 8; i++) {
                if ((value >> i & 1) == 1) {
                    if (i == 4) {
                        str = "1-phase,2-wire," + (value & 0x0F);
                        map.put(dataName, str);

                    } else if (i == 5) {
                        str = "1-phase,3-wire," + (value & 0x0F);
                        map.put(dataName, str);

                    } else if (i == 6) {
                        str = "3-phase,3-wire," + (value & 0x0F);
                        map.put(dataName, str);

                    } else if (i == 7) {
                        str = "3-phase,4-wire," + (value & 0x0F);
                        map.put(dataName, str);

                    }
                }
            }
            if (!map.containsKey(KEPCO_METER_INFO.MeterKind.name())) {
                str = (value & 0x0F) + "";
                map.put(dataName, str);
            }

        } 
        else if (dataName.equals(KEPCO_METER_INFO.MeterDate.name())) {
            byte[] data = tag.getData();
            DecimalFormat df = new DecimalFormat("00");
            int year = DataFormat.getIntTo2Byte(DataFormat.select(data, 0, 2));
            int month = DataFormat.getIntToByte(data[2]);
            int day = DataFormat.getIntToByte(data[3]);
            int hour = DataFormat.getIntToByte(data[4]);
            int min = DataFormat.getIntToByte(data[5]);
            int sec = DataFormat.getIntToByte(data[6]);
            int week = DataFormat.getIntToByte(data[7]);
            str = year + df.format(month) + df.format(day) + df.format(hour)
                    + df.format(min) + df.format(sec); //  + " " + week; 주는 표현하지 않는다.
            map.put(dataName, str);
        }
        else if (dataName.equals(KEPCO_METER_INFO.MeterStatusError.name())) {
            int value = DataUtil.getIntToBytes(((OCTET)tag.getValue()).getValue());
            String binaryString = Integer.toBinaryString(value);
            while (binaryString.length() % 8 != 0) {
                binaryString = "0" + binaryString;
            }

            str = binaryString;
            map.put(dataName, str);
        }
        else if (dataName.equals(KEPCO_METER_INFO.MeterStatusCaution.name())) {
            int value = DataUtil.getIntToBytes(((OCTET)tag.getValue()).getValue());
            String binaryString = Integer.toBinaryString(value);
            while (binaryString.length() % 8 != 0) {
                binaryString = "0" + binaryString;
            }
            str = binaryString;
            map.put(dataName, str);
        }
        else if (dataName.equals(KEPCO_METER_INFO.RecentReadLoadProfileDate.name())) {
            byte[] data = tag.getData();
            DecimalFormat df = new DecimalFormat("00");
            int year = DataFormat.getIntTo2Byte(DataFormat.select(data, 0, 2));
            int month = DataFormat.getIntToByte(data[2]);
            int day = DataFormat.getIntToByte(data[3]);
            int hour = DataFormat.getIntToByte(data[4]);
            int min = DataFormat.getIntToByte(data[5]);
            str = year + df.format(month) + df.format(day) + df.format(hour)
                    + df.format(min);
            map.put(dataName, str);
        }
        else if (dataName.equals(KEPCO_METER_INFO.LastReadInfo.name())) {
            byte[] data = tag.getData();
            int value = DataUtil.getIntToByte(data[1]);
            String binaryString = Integer.toBinaryString(value);
            while (binaryString.length() % 8 != 0) {
                binaryString = "0" + binaryString;
            }
            int year = DataFormat.getIntTo2Byte(DataFormat.select(data, 2, 2));
            int month = DataFormat.getIntToByte(data[4]);
            int day = DataFormat.getIntToByte(data[5]);
            int hour = DataFormat.getIntToByte(data[6]);
            int min = DataFormat.getIntToByte(data[7]);
            int sec = DataFormat.getIntToByte(data[8]);
            int week = DataFormat.getIntToByte(data[9]);
            DecimalFormat df = new DecimalFormat("00");

            str = "reason:" + binaryString + ":date=" + year + df.format(month)
                    + df.format(day) + df.format(hour) + df.format(min)
                    + df.format(sec); //  + " " + week; 주는 제거한다.
            map.put(dataName, str);
        }
        else {
            map.put(dataName, tag.getValue());
        }

    }
}
