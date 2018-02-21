package com.aimir.fep.meter.parser.DLMSWtypeTable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.command.conf.DLMSMeta.LOAD_CONTROL_STATUS;
import com.aimir.fep.meter.parser.DLMSWtypeTable.DLMSWtypeVARIABLE.DLMS_CLASS;
import com.aimir.fep.meter.parser.DLMSWtypeTable.DLMSWtypeVARIABLE.DLMS_CLASS_ATTR;
import com.aimir.fep.meter.parser.DLMSWtypeTable.DLMSWtypeVARIABLE.DLMS_TAG_TYPE;
import com.aimir.fep.meter.parser.DLMSWtypeTable.DLMSWtypeVARIABLE.LOAD_PROFILE;
import com.aimir.fep.meter.parser.DLMSWtypeTable.DLMSWtypeVARIABLE.METER_CONSTANT;
import com.aimir.fep.meter.parser.DLMSWtypeTable.DLMSWtypeVARIABLE.OBIS;
import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

public class DLMSWtypeTable {
    private static Log log = LogFactory.getLog(DLMSWtypeTable.class);
    
    private DLMSWtypeHeader dlmsHeader = new DLMSWtypeHeader();
    private List<DLMSWtypeTag> dlmsTags = new ArrayList<DLMSWtypeTag>();
    
    public DLMSWtypeHeader getDlmsHeader() {
        return dlmsHeader;
    }
    public void setDlmsHeader(DLMSWtypeHeader dlmsHeader) {
        this.dlmsHeader = dlmsHeader;
    }
    public List<DLMSWtypeTag> getDlmsTags() {
        return dlmsTags;
    }
    public void setDlmsTags(List<DLMSWtypeTag> dlmsTags) {
        this.dlmsTags = dlmsTags;
    }
    public void addDlmsTag(DLMSWtypeTag tag) {
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
        DLMSWtypeTag dlmsTag = null;
        byte[] bx = null;
        
        while (pos != data.length) {
            // log.debug("LEN[" + data.length + "] POS[" + pos + "]");
            dlmsTag = new DLMSWtypeTag();
            dlmsTag.setTag(data[pos]);
            pos += 1;
            
            /*
             * BitString, OctetString, VisibleString 태그에 대해서만 data[1] 바이트를
             * 확장 길이로 사용하고 데이타에 대한 길이이다.
             * 단, 0x80 으로 and연산으로 0x80 이 되면 0x7F 로 and을 하여 길이를 구한다.
             */
            int len_len = 0;
            /*
            if ((data[pos] & 0x80) == 0x80 ) {
                len_len = (data[pos] & 0x7F);
                log.debug("LEN BYTE[" + Hex.decode(new byte[] {data[pos]}) + "] LEN[" + len_len + "]");
                pos += 1;
                byte[] b_len = new byte[len_len];
                System.arraycopy(data, pos, b_len, 0, b_len.length);
                len = DataUtil.getIntToBytes(b_len);
                pos += len_len;
            }
            else {
            */
                if (dlmsTag.getTag() == DLMS_TAG_TYPE.OctetString ||
                        dlmsTag.getTag() == DLMS_TAG_TYPE.VisibleString) {
                    len = DataUtil.getIntToByte(data[pos]);
                    pos += 1;
                }
                else if (dlmsTag.getTag() == DLMS_TAG_TYPE.BitString) {
                	len = DataUtil.getIntToByte(data[pos]);
                	len /= 8;
                	pos += 1;
                }
                else len = dlmsTag.getTag().getLenth();
            // }
            
            bx = new byte[len];
            
            // pos와 data의 길이 합이 data.length를 넘어갈 수도 있기 때문에 비교를 한 후에 arrayindexout 예외가 발생하지 않도록 한다.
            if (pos + len > data.length)
                break;
            
            System.arraycopy(data, pos, bx, 0, bx.length);
            pos += bx.length;

            dlmsTag.setLength(len);
            dlmsTag.setData(bx);
            if (dlmsTag.getTag() != DLMS_TAG_TYPE.Null)
            	log.debug(dlmsTag.toString());
            // 예외로 LOAD_PROFILE이고 태그가 Array인 경우는 추가하지 않는다.
            if (dlmsHeader.getObis() == OBIS.LOAD_PROFILE && dlmsTag.getTag() == DLMS_TAG_TYPE.Array)
                continue;
            if (dlmsTag.getTag() == DLMS_TAG_TYPE.Null)
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
        case SCRIPT_TABLE :
            switch (attr) {
            case SCRIPT_TABLE_ATTR01 :
                if (obis == OBIS.LOAD_CONTROL && dlmsTags.size() != 0) {
                    ret.put("LoadControl", LOAD_CONTROL_STATUS.getValue(Integer.parseInt(Hex.decode(dlmsTags.get(0).getData()))));
                }
            }
            break;
        case CLOCK :
        	try {
    			ret.put("MeterTime", makeDateTime(dlmsTags.get(0).getData()));
        	}
        	catch (Exception e) {}
    		break;
        case DATA:
            switch (attr) {
            case DATA_ATTR01:
                if (obis == OBIS.DEVICE_INFO && dlmsTags.size() != 0) {
                    ret.put("DeviceName", dlmsTags.get(0).getOCTET().toString());
                }
                else if (obis == OBIS.VZ && dlmsTags.size() != 0) {
                    ret.put("VZ", dlmsTags.get(0).getUint8());
                }
                else if (obis == OBIS.BILLING_PERIOD && dlmsTags.size() != 0) {
                    ret.put("BillingPeroid", dlmsTags.get(0).getUint8());
                }
                else if (obis == OBIS.CUSTOMER_METER_ID && dlmsTags.size() != 0) {
                    ret.put(OBIS.CUSTOMER_METER_ID.getName(), dlmsTags.get(0).getOCTET().toString());
                }
                else if (obis == OBIS.MANUFACTURER_METER_ID && dlmsTags.size() != 0) {
                    ret.put("ManufacturerMeterId", dlmsTags.get(0).getOCTET().toString());
                }
                else if (obis == OBIS.LP_INTERVAL && dlmsTags.size() != 0) {
                    ret.put(OBIS.LP_INTERVAL.getName(), dlmsTags.get(0).getValue());
                }
                else if (obis == OBIS.POWER_FAILURE_COUNT && dlmsTags.size() != 0) {
                    ret.put("NumberOfPowerFailures", dlmsTags.get(0).getValue());
                }
                else if (obis == OBIS.PROGRAM_CHANGE_COUNT && dlmsTags.size() != 0) {
                    ret.put("ProgramChangeCount", dlmsTags.get(0).getValue());
                }
                else if (obis == OBIS.PROGRAM_CHANGE_DATE && dlmsTags.size() != 0) {
                    ret.put("ProgramChangeDate", dlmsTags.get(0).getOCTET().toString());
                }
                else if (obis == OBIS.PROGRAM_CHANGE_RESERVEDATE && dlmsTags.size() != 0) {
                    ret.put("ProgramChangeReserveDate", dlmsTags.get(0).getOCTET().toString());
                }
                else if (obis == OBIS.BATTERY_USE_TIME && dlmsTags.size() != 0) {
                    ret.put("BatteryUseTime", dlmsTags.get(0).getValue());
                }
                else if (obis == OBIS.SELF_CHECK_BATTERY && dlmsTags.size() != 0) {
                    ret.put("SelfCheckBatteryWarning", dlmsTags.get(0).getOCTET().toString());
                }
                else if (obis == OBIS.SELF_CHECK_MEMORY && dlmsTags.size() != 0) {
                    ret.put("SelfCheckMemoryWarning", dlmsTags.get(0).getOCTET().toString());
                }
                else if (obis == OBIS.SELF_CHECK_POWER && dlmsTags.size() != 0) {
                    ret.put("SelfCheckPowerWarning", dlmsTags.get(0).getOCTET().toString());
                }
                else if (obis == OBIS.OUTPUT_SIGNAL && dlmsTags.size() != 0) {
                	 ret.put("OutputSignal", DLMSWtypeVARIABLE.OUTPUT_SIGNAL.getValue(Integer.parseInt(Hex.decode(dlmsTags.get(0).getData()))));
                }
                else if (obis == OBIS.LOAD_CONTROL_STATUS && dlmsTags.size() != 0) {
                    ret.put("LoadControlStatus", LOAD_CONTROL_STATUS.getValue(Integer.parseInt(Hex.decode(dlmsTags.get(0).getData()))));
                }
                else if (obis == OBIS.COLD_WATER && dlmsTags.size() != 0) {
                    ret.put(OBIS.COLD_WATER.getName(), dlmsTags.get(0).getData());
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
                }
                else if (obis == OBIS.METER_CONSTANT_REACTIVE && dlmsTags.size() != 0) {
                    for (int i = 0; i < dlmsTags.size(); i++) {
                        putData(ret, OBIS.METER_CONSTANT_REACTIVE, METER_CONSTANT.ReactiveC.name(), dlmsTags.get(i));
                    }
                }
                else if (obis == OBIS.LP_INTERVAL && dlmsTags.size() != 0) {
                    for (int i = 0; i < dlmsTags.size(); i++) {
                        putData(ret, OBIS.LP_INTERVAL, OBIS.LP_INTERVAL.getName(), dlmsTags.get(i));
                    }
                }
                else if (obis == OBIS.IMPORT_ACTIVE_ENERGY && dlmsTags.size() != 0) {
                    for (int i = 0; i < dlmsTags.size(); i++) {
                        putData(ret, OBIS.IMPORT_ACTIVE_ENERGY, OBIS.IMPORT_ACTIVE_ENERGY.getName(), dlmsTags.get(i));
                    }
                }
                break;
            case REGISTER_ATTR03: // scaler_unit
                if (obis == OBIS.METER_CONSTANT_ACTIVE && dlmsTags.size() > 2) {
                    if (dlmsTags.get(0).getValue() instanceof OCTET) {
                        log.debug(dlmsTags.get(0).getOCTET().toString());
                    }
                    else {
                        ret.put("Scaler", dlmsTags.get(0).getInt8());
                        ret.put("unit", dlmsTags.get(1).getUint8());
                    }
                }
                break;
            }
            break;
        case PROFILE_GENERIC:
            switch (attr) {
            case PROFILE_GENERIC_ATTR02: // buffer
                /*
                if (obis == OBIS.MONTHLY_ENERGY_PROFILE && dlmsTags.size() < 14) {
                    break;
                } else if (obis == OBIS.MONTHLY_DEMAND_PROFILE && dlmsTags.size() < 26) {
                    break;
                } else if (obis == OBIS.KEPCO_CURRENT_MAX_DEMAND && dlmsTags.size() < 17) {
                    break;
                } else if (obis == OBIS.KEPCO_PREVIOUS_MAX_DEMAND && dlmsTags.size() < 17) {
                    break;
                } else if (obis == OBIS.KEPCO_METER_INFO && dlmsTags.size() < 14) {
                    break;
                } else if (obis == OBIS.LOAD_PROFILE && dlmsTags.size() < 7) {
                    break;
                } else if (obis == OBIS.POWER_FAILURE) {

                } else if (obis == OBIS.POWER_RESTORE) {

                }
                */
                putData(dlmsTags, ret, obis);
                break;
            case PROFILE_GENERIC_ATTR03:
            	log.debug("LP Schema[" + Hex.decode(dlmsTags.get(0).getData())+ "]");
            	break;
            case PROFILE_GENERIC_ATTR04:
            	log.debug("LP Interval[" + dlmsTags.get(0).getValue() + "]");
            	ret.put("LpInterval", dlmsTags.get(0).getValue());
            	break;
            case PROFILE_GENERIC_ATTR07: // entries_in_use
                if (dlmsTags.size() == 0)
                    break;
                log.debug(dlmsTags.get(0).getValue());
                ret.put("Entry", dlmsTags.get(0).getValue());
                break;
            }
            break;
        case DEMAND_REGISTER :
        }
        
        return ret;
    }
    
    /*
     * 하나의 OBIS에 여러 개의 태그를 가지는 경우 그 순서에 따라 데이타 유형이 결정된다.
     */
    private void putData(List<DLMSWtypeTag> tags, Map<String, Object> ret, OBIS obis) {
        String name = null;
        
        DLMSWtypeTag tag = null;
        
        if (obis == OBIS.LOAD_PROFILE) {
        	int structSize = 0;
        	int structIdx = 0;
        	for (int i = 0; i < tags.size(); ) {
	        	tag = tags.get(i++);
	        	if (tag.getTag() == DLMS_TAG_TYPE.Structure) {
	        		structSize = DataUtil.getIntToBytes(tag.getOCTET().getValue());
	        		log.debug("Structure Size[" + structSize + "]");
	        		structIdx = LOAD_PROFILE.Structure.getCode()+1;
	        		for (int s = 0; s < structSize && i < tags.size(); s++, i++) {
	        			name = DLMSWtypeVARIABLE.getDataName(obis, structIdx++);
	    	            putData(ret, obis, name, tags.get(i));
	        		}
	        	}
        	}
        }
        else if (obis == OBIS.POWER_FAILURE || obis == OBIS.POWER_RESTORE
                || obis == OBIS.TIME_CHANGE_FROM || obis == OBIS.TIME_CHANGE_TO
                || obis == OBIS.DEMAND_RESET || obis == OBIS.MANUAL_DEMAND_RESET
                || obis == OBIS.SELF_READ || obis == OBIS.PROGRAM_CHANGE
                || obis == OBIS.SAG || obis == OBIS.SWELL) {
            int array = 0;
            int structSize = 0;
            for (int i = 0; i < tags.size(); ) {
                tag = tags.get(i++);
                if (tag.getTag() == DLMS_TAG_TYPE.Array) {
                    array = (Integer)tag.getValue();
                    log.debug(obis.getName() + "_EVENT_ARRAY[" + array + "]");
                    for (int j = 0; j < array; j++) {
                        tag = tags.get(i++);
                        if (tag.getTag() == DLMS_TAG_TYPE.Structure) {
                            structSize = DataUtil.getIntToBytes(tag.getOCTET().getValue());
                            log.debug(obis.getName() + "_EVENT_STRUCTURE[" + structSize + "]");
                            for (int k = 0; k < structSize; k++, i++) {
                                name = DLMSWtypeVARIABLE.getDataName(obis, k);
                                putData(ret, obis, name, tags.get(i));
                            }
                        }
                    }
                }
            }
        }
        else {
        	for (int i = 0; i < tags.size(); i++) {
        		name = DLMSWtypeVARIABLE.getDataName(obis, i);
	            putData(ret, obis, name, tags.get(i));
        	}
        }
    }
    
    private Object putData(Map<String, Object> map, OBIS obis, String dataName, DLMSWtypeTag tag) {
        try {
            switch (obis) {
            case TIME :
            	// getOBIS_CODE_METER_TIME(map, dataName, tag);
            	break;
            case METER_INFO :
                getOBIS_CODE_METER_INFO(map, dataName, tag);
                break;
            case METER_CONSTANT_ACTIVE :
            case METER_CONSTANT_REACTIVE :
                getOBIS_CODE_METER_CONSTANT_ACTIVE(map, dataName, tag);
                break;
            case POWER_FAILURE :
            case POWER_RESTORE :
            case TIME_CHANGE_FROM :
            case TIME_CHANGE_TO :
            case DEMAND_RESET :
            case MANUAL_DEMAND_RESET :
            case SELF_READ :
            case PROGRAM_CHANGE :
            case LOAD_PROFILE :
                getOBIS_CODE_LOAD_PROFILE(map, dataName, tag);
                break;
            case LOAD_CONTROL_STATUS :
                getOBIS_CODE_LOAD_CONTROL_STATUS(map, dataName, tag);
            case LP_INTERVAL :
                getOBIS_CODE_METER_INFO(map, dataName, tag);
            case IMPORT_ACTIVE_ENERGY :
                getOBIS_CODE_METER_INFO(map, dataName, tag);
            }
        } catch (Exception e) {
           log.error("obis:"+obis+":dataName:"+dataName+":tag:"+tag);
        }
        return tag.getValue();
    }

    private void getOBIS_CODE_LOAD_CONTROL_STATUS(Map<String, Object> map, String dataName, DLMSWtypeTag tag) 
    throws Exception {
        log.debug("DATA_NAME[" + dataName + "] VALUE[" + (tag.getValue() instanceof OCTET? Hex.decode(((OCTET)tag.getValue()).getValue()):tag.getValue()) + "]");
        String key = dataName;
        for (int cnt = 0; ;cnt++) {
            key = dataName + "-" + cnt;
            if (!map.containsKey(key)) {
                map.put(key, tag.getValue());
                break;
            }
        }
    }
    private void getOBIS_CODE_LOAD_PROFILE(Map<String, Object> map, String dataName, DLMSWtypeTag tag)
            throws Exception {
        log.debug("DATA_NAME[" + dataName + "] VALUE[" + (tag.getValue() instanceof OCTET? Hex.decode(((OCTET)tag.getValue()).getValue()):tag.getValue()) + "]");
        if (dataName.equals(LOAD_PROFILE.Time.name())) {
        	if (Hex.decode(tag.getData()).equals("000000000000000000000000"))
        		return;
        	
            String str = makeDateTime(tag.getData());
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
    
    private String makeDateTime(byte[] data) throws Exception {
    	int year = DataFormat.getIntTo2Byte(DataFormat.select(data, 0, 2));
        int month = DataFormat.getIntToByte(data[2]);
        int day = DataFormat.getIntToByte(data[3]);
        int week = DataFormat.getIntToByte(data[4]);
        int hour = DataFormat.getIntToByte(data[5]);
        int min = DataFormat.getIntToByte(data[6]);
        DecimalFormat df = new DecimalFormat("00");
        String str = String.format("%4d%02d%02d%02d%02d", year, month,day,hour,min);
        return str;
    }
    
    public void getOBIS_CODE_METER_CONSTANT_ACTIVE(Map<String, Object> map,
            String dataName, DLMSWtypeTag tag) throws Exception {
        map.put(dataName, tag.getValue());
    }

    public void getOBIS_CODE_METER_INFO(Map<String, Object> map,
            String dataName, DLMSWtypeTag tag) throws Exception {
        
        String str = "";
        log.debug("DATANAME[" + dataName + "] Value[" + tag.getValue() + "]");
        map.put(dataName, tag.getValue());
    }
}
