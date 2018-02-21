package com.aimir.fep.meter.parser.DLMSGtypeOmniTable;

import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.command.conf.DLMSMeta.LOAD_CONTROL_STATUS;
import com.aimir.fep.meter.parser.DLMSGtypeOmniTable.DLMSGtypeVARIABLE.DLMS_CLASS;
import com.aimir.fep.meter.parser.DLMSGtypeOmniTable.DLMSGtypeVARIABLE.DLMS_CLASS_ATTR;
import com.aimir.fep.meter.parser.DLMSGtypeOmniTable.DLMSGtypeVARIABLE.DLMS_TAG_TYPE;
import com.aimir.fep.meter.parser.DLMSGtypeOmniTable.DLMSGtypeVARIABLE.EVENT;
import com.aimir.fep.meter.parser.DLMSGtypeOmniTable.DLMSGtypeTag;
import com.aimir.fep.meter.parser.DLMSGtypeOmniTable.DLMSGtypeVARIABLE.METER_CONSTANT;
import com.aimir.fep.meter.parser.DLMSGtypeOmniTable.DLMSGtypeVARIABLE.METER_INFORMATION;
import com.aimir.fep.meter.parser.DLMSGtypeOmniTable.DLMSGtypeVARIABLE.MONTHLY_DEMAND_PROFILE;
import com.aimir.fep.meter.parser.DLMSGtypeOmniTable.DLMSGtypeVARIABLE.OBIS;
import com.aimir.fep.meter.parser.DLMSGtypeOmniTable.DLMSGtypeVARIABLE.POWER_QUALITY_AVG_PROFILE;
import com.aimir.fep.meter.parser.DLMSGtypeOmniTable.DLMSGtypeVARIABLE.POWER_QUALITY_PROFILE;
import com.aimir.fep.meter.parser.DLMSGtypeOmniTable.DLMSGtypeVARIABLE.TDU_PROFILE;
import com.aimir.fep.meter.parser.DLMSGtypeOmniTable.DLMSGtypeVARIABLE.LOAD_PROFILE;
import com.aimir.fep.meter.parser.DLMSGtypeOmniTable.DLMSGtypeVARIABLE.LOAD_STATUS_PROFILE;
import com.aimir.fep.meter.parser.DLMSGtypeOmniTable.DLMSGtypeVARIABLE.MEASUREMENT_DATE_INFO;
import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;


public class DLMSGtypeTable {
	
    private static Log log = LogFactory.getLog(DLMSGtypeTable.class);
    public final static String UNDEFINED = "undefined";
    private DLMSGtypeHeader dlmsHeader = new DLMSGtypeHeader();
    private List<DLMSGtypeTag> dlmsTags = new ArrayList<DLMSGtypeTag>();
    
    public DLMSGtypeHeader getDlmsHeader() {
        return dlmsHeader;
    }
    public void setDlmsHeader(DLMSGtypeHeader dlmsHeader) {
        this.dlmsHeader = dlmsHeader;
    }
    public List<DLMSGtypeTag> getDlmsTags() {
        return dlmsTags;
    }
    public void setDlmsTags(List<DLMSGtypeTag> dlmsTags) {
        this.dlmsTags = dlmsTags;
    }
    public void addDlmsTag(DLMSGtypeTag tag) {
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
        DLMSGtypeTag dlmsTag = null;
        byte[] bx = null;
        
        while (pos != data.length) {
            log.debug("LEN[" + data.length + "] POS[" + pos + "]");
            dlmsTag = new DLMSGtypeTag();
            dlmsTag.setTag(data[pos]);
            pos += 1;
            
            /*
             * BitString, OctetString, VisibleString 태그에 대해서만 data[1] 바이트를
             * 확장 길이로 사용하고 데이타에 대한 길이이다.
             * BitString : 8bit -> 1byte
             */
            if (data.length > pos) {
                if ( dlmsTag.getTag() == DLMS_TAG_TYPE.OctetString || dlmsTag.getTag() == DLMS_TAG_TYPE.VisibleString) {
                    len = DataUtil.getIntToByte(data[pos]);
                    pos += 1;
                } else if (dlmsTag.getTag() == DLMS_TAG_TYPE.BitString) {                	
                	len = DataUtil.getIntToByte(data[pos]) / 8; // bit length
                    pos += 1;                	
                }
                else len = dlmsTag.getTag().getLenth();
            }
            else continue;
            
            bx = new byte[len];
            
            // pos와 data의 길이 합이 data.length를 넘어갈 수도 있기 때문에 비교를 한 후에 arrayindexout 예외가 발생하지 않도록 한다.
            if (pos + len > data.length)
                break;
            
            System.arraycopy(data, pos, bx, 0, bx.length);
            pos += bx.length;

            dlmsTag.setLength(len);
            dlmsTag.setData(bx);
            log.debug(dlmsTag.toString());
           
            // 태그가 Array인 경우는 추가하지 않는다.
            if ((dlmsHeader.getObis() == OBIS.LOAD_PROFILE
            		|| dlmsHeader.getObis() == OBIS.CURRENT_ENERGY_PROFILE
            		|| dlmsHeader.getObis() == OBIS.MONTHLY_ENERGY_PROFILE
            		|| dlmsHeader.getObis() == OBIS.MONTHLY_DEMAND_PROFILE
            		|| dlmsHeader.getObis() == OBIS.POWER_FAILURE
            		|| dlmsHeader.getObis() == OBIS.POWER_RESTORE
            		|| dlmsHeader.getObis() == OBIS.CURRENT_VOLTAGE
            		|| dlmsHeader.getObis() == OBIS.AVG_CURRENT_VOLTAGE
            		|| dlmsHeader.getObis() == OBIS.TDU_MEASURMENT_DATA
            		) && dlmsTag.getTag() == DLMS_TAG_TYPE.Array)
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
		                if (obis == OBIS.MANUFACTURER_METER_ID && dlmsTags.size() != 0) {		                	
		                	ByteBuffer byteBuffer = ByteBuffer.wrap(dlmsTags.get(0).getOCTET().getValue());
		                	byte[] _byte = new byte[7]; // 번호
		                	byteBuffer.get(_byte);
		                    ret.put(OBIS.MANUFACTURER_METER_ID.getName(), new String(_byte));
		                } 
		                if (obis == OBIS.CUSTOMER_METER_ID && dlmsTags.size() != 0) {
		                  
		                	ByteBuffer byteBuffer = ByteBuffer.wrap(dlmsTags.get(0).getOCTET().getValue());
		                	
		            		byte[] byteCM1 = new byte[3]; // 고유코드
		                	byte[] byteCM2 = new byte[1]; // 공백
		                	byte[] byteCM3 = new byte[7]; // 제조일자
		                	byte[] byteCM4 = new byte[3]; // 공백
		                	byte[] byteCM5 = new byte[2]; // 규격버전
		                	
		                	byteBuffer.get(byteCM1);
		                	byteBuffer.get(byteCM2);
		                	byteBuffer.get(byteCM3);
		                	byteBuffer.get(byteCM4);
		                	byteBuffer.get(byteCM5);
		                	
		                	Map<String, Object> map = new HashMap<String, Object>();
		                	map.put(METER_INFORMATION.COSEM_ID.name(), new String(byteCM1));
		                	map.put(METER_INFORMATION.MANUFACTURE_DATE.name(), new String(byteCM3));
		                	map.put(METER_INFORMATION.FW_VERSION.name(), new String(byteCM5));
		                	
		                	ret.put(OBIS.CUSTOMER_METER_ID.getName(), map);
		                } 
		                
		                if (obis == OBIS.METER_RELAY_STATUS && dlmsTags.size() != 0) {
		                	byte[] data = dlmsTags.get(0).getOCTET().getValue();
		                	
		            		System.out.println(new String(data));
		                	ret.put(OBIS.METER_RELAY_STATUS.getName(), DataFormat.getIntToByte(data[0]));
		                }
		                
		                break;
					default:
						break;
	            }
        	break;
        	case REGISTER: 
	    		switch (attr) {
		    		case REGISTER_ATTR02: // value
		    			if (obis == OBIS.ACTIVEPOWER_CONSTANT && dlmsTags.size() != 0) {
		                    ret.put(OBIS.ACTIVEPOWER_CONSTANT.getName(), dlmsTags.get(0).getValue());
		                }		    			
		    			if (obis == OBIS.REACTIVEPOWER_CONSTANT && dlmsTags.size() != 0) {
		                    ret.put(OBIS.REACTIVEPOWER_CONSTANT.getName(), dlmsTags.get(0).getValue());
		                }		    			
		    			if (obis == OBIS.APPRENTPOWER_CONSTANT && dlmsTags.size() != 0) {
		                    ret.put(OBIS.APPRENTPOWER_CONSTANT.getName(), dlmsTags.get(0).getValue());
		                }
		    			if (obis == OBIS.LP_CYCLE && dlmsTags.size() != 0) {
		                    ret.put(OBIS.LP_CYCLE.getName(), dlmsTags.get(0).getValue());
		                }
		    			break;
		    		case REGISTER_ATTR03: // scaler_uni
		    			break;
					default:
						break;
	            }
        	break;
        	case CLOCK: 
	    		switch (attr) {
		    		case CLOCK_ATTR02: // TIME
		    			if (obis == OBIS.METER_TIME && dlmsTags.size() != 0) {
		    				byte[] data = dlmsTags.get(0).getOCTET().getValue();
		    				if (data.length >= 12) {		        				
		        				try {
			    	            	String str = getDateTime(data);
			    	            	ret.put(OBIS.METER_TIME.getName(), str);
		        				} catch (Exception e) {
		        					log.warn(e,e);
		        				}
		        			}
		                }	    			
		    			break;
					default:
						break;
	            }
        	break;
        	case PROFILE_GENERIC: 
	    		switch (attr) {
		    		case PROFILE_GENERIC_ATTR02: // value
		    			if (dlmsTags.size() == 0)
		                    break;
		                putData(dlmsTags, ret, obis);
		                break;
					default:
						break;
	            } // end switch
        	break;
        	case SINGLE_ACTION_SCHEDULE: //22
    			switch (attr) {
    			case SINGLE_ACTION_SCHEDULE_ATTR04: // execution_time, array
    				if (dlmsTags.size() == 0)
	                    break;
	                putData(dlmsTags, ret, obis);
	                break;
    			default:
					break;
    			}
    			break;
        	case TDU_DATA: //0
    			switch (attr) {
    			case TDU_ATTR01: // 0
    				if (dlmsTags.size() == 0)
	                    break;	                
    				putData(dlmsTags, ret, obis);
	                break;
    			default:
					break;
    			}
    		break;
        }
        return ret;
    }
    
    /*
     * 하나의 OBIS에 여러 개의 태그를 가지는 경우 그 순서에 따라 데이타 유형이 결정된다.
     */
    private void putData(List<DLMSGtypeTag> tags, Map<String, Object> ret, OBIS obis) {
    	
    	String name = null;
        String structure = tags.get(0).getValue() instanceof OCTET? Hex.decode(((OCTET)tags.get(0).getValue()).getValue()):tags.get(0).getValue().toString();
        
    	for (int i = 0; i < tags.size(); i++) {
       		name = DLMSGtypeVARIABLE.getDataName(obis, i,
       				Integer.parseInt(structure == null || structure.equals("") ? "0" : structure, 16));
       		
       		putData(ret, obis, name, tags.get(i));
       	}
    }
    
    private Object putData(Map<String, Object> map, OBIS obis, String dataName, DLMSGtypeTag tag) {
        try {
            switch (obis) {
	            case METER_TIME :
	            case MANUFACTURER_METER_ID :
	            case CUSTOMER_METER_ID :	
	            case ACTIVEPOWER_CONSTANT :	            	
	            case REACTIVEPOWER_CONSTANT :	            	
	            case APPRENTPOWER_CONSTANT :
	            case LP_CYCLE :
	            	break;
	            case MEASUREMENT_DATE :
	            	getOBIS_CODE_MEASUREMENT_DATE(map, dataName, tag);
	            	break;
	            case LOAD_PROFILE :
	                getOBIS_CODE_LOAD_PROFILE(map, dataName, tag);
	                break;            	
	            case CURRENT_ENERGY_PROFILE :
	            	getOBIS_CODE_CURRENT_ENERGY_PROFILE(map, dataName, tag);
	                break;
	            case MONTHLY_ENERGY_PROFILE :
	            	getOBIS_CODE_ENERGY_PROFILE(map, dataName, tag);
	                break;    
	            case MONTHLY_DEMAND_PROFILE :	            	
	            	getOBIS_CODE_DEMAND_PROFILE(map, dataName, tag);
	                break;
	            case POWER_FAILURE :                
	            case POWER_RESTORE :	            	
	            	getOBIS_CODE_EVENT(map, dataName, tag);
	                break;                
	            case CURRENT_VOLTAGE :
	            	getOBIS_CODE_POWER_QUALITY(map, dataName, tag);
	                break;                                
	            case AVG_CURRENT_VOLTAGE :
	                getOBIS_CODE_POWER_QUALITY_AVG(map, dataName, tag);
	                break;
	            case TDU_MEASURMENT_DATA :
	                getOBIS_CODE_TDU_DATA(map, dataName, tag);
	                break; 
            }
        } catch (Exception e) {
           log.error("obis:"+obis+":dataName:"+dataName+":tag:"+tag);
        }
        return tag.getValue();
    }
    
    private void getOBIS_CODE_LOAD_PROFILE(Map<String, Object> map, String dataName, DLMSGtypeTag tag)
            throws Exception {
    	
    	log.debug("DATA_NAME[" + dataName + "] VALUE[" + (tag.getValue() instanceof OCTET? Hex.decode(((OCTET)tag.getValue()).getValue()):tag.getValue()) + "]");
        
    	if(!UNDEFINED.equals(dataName)) {
	    	if (dataName.equals(LOAD_PROFILE.Date.name())) {
	            byte[] data = tag.getData();
	            if (data.length == 12) {	            	
	            	
	            	String str = getDateTime(data);	            	            	
	            	String key = dataName;
//	            	String pstr = getDateTime(data, false); // persian
	            	
		            for (int cnt = 0; ;cnt++) {
		                key = dataName + "-" + cnt;
		                String pkey = LOAD_PROFILE.PersianDate.name() + "-" + cnt;
		                
		                if (!map.containsKey(key)) {
		                    map.put(key, str);
//		                    map.put(pkey, pstr); // 페르시안날짜
		                    break;
		                }
		            }
	            }
	        } else if (dataName.equals(LOAD_PROFILE.Status.name())) {
	        	
	        	byte[] data = tag.getData();
	        	String status = Hex.decode(data);
	        	
        		String key = dataName;
	            for (int cnt = 0; ;cnt++) {
	                key = dataName + "-" + cnt;
	                if (!map.containsKey(key)) {
	                    map.put(key, status);
	                    break;
	                }
	            }
	        } else {	        
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
    }
    
    // 현재검침
    private void getOBIS_CODE_CURRENT_ENERGY_PROFILE(Map<String, Object> map, String dataName, DLMSGtypeTag tag)
            throws Exception {    	
    	log.debug("DATA_NAME[" + dataName + "] VALUE[" + (tag.getValue() instanceof OCTET? Hex.decode(((OCTET)tag.getValue()).getValue()):tag.getValue()) + "]");
    	if(!UNDEFINED.equals(dataName)) {
	    	String key = dataName;    
            if (!map.containsKey(key)) {
                map.put(key, tag.getValue());	                
            }
    	}
    }
    
    // 정기검침
    private void getOBIS_CODE_ENERGY_PROFILE(Map<String, Object> map, String dataName, DLMSGtypeTag tag)
            throws Exception {    	
    	log.debug("DATA_NAME[" + dataName + "] VALUE[" + (tag.getValue() instanceof OCTET? Hex.decode(((OCTET)tag.getValue()).getValue()):tag.getValue()) + "]");
    	if(!UNDEFINED.equals(dataName)) {
	    	String key = dataName;    
            if (!map.containsKey(key)) {
                map.put(key, tag.getValue());	                
            }
    	}
    }
    
    private void getOBIS_CODE_DEMAND_PROFILE(Map<String, Object> map, String dataName, DLMSGtypeTag tag)
            throws Exception {
    	
    	log.debug("DATA_NAME[" + dataName + "] VALUE[" + (tag.getValue() instanceof OCTET? Hex.decode(((OCTET)tag.getValue()).getValue()):tag.getValue()) + "]");
    	String key = dataName;
    	
    	if(!UNDEFINED.equals(dataName)) {
	    	if (dataName.equals(MONTHLY_DEMAND_PROFILE.ImportDmdActiveEnergyDate.name())
	    		 || dataName.equals(MONTHLY_DEMAND_PROFILE.ImportDmdActiveEnergyDateA.name())
	    		 || dataName.equals(MONTHLY_DEMAND_PROFILE.ImportDmdActiveEnergyDateB.name())
	    		 || dataName.equals(MONTHLY_DEMAND_PROFILE.ImportDmdActiveEnergyDateC.name())
	    		 || dataName.equals(MONTHLY_DEMAND_PROFILE.ImportDmdActiveEnergyDateD.name())
	    		 || dataName.equals(MONTHLY_DEMAND_PROFILE.ImportDmdApparentEnergyDate.name())
	    		 || dataName.equals(MONTHLY_DEMAND_PROFILE.ImportDmdApparentEnergyDateA.name())
	    		 || dataName.equals(MONTHLY_DEMAND_PROFILE.ImportDmdApparentEnergyDateB.name())
	    		 || dataName.equals(MONTHLY_DEMAND_PROFILE.ImportDmdApparentEnergyDateC.name())
	    		 || dataName.equals(MONTHLY_DEMAND_PROFILE.ImportDmdApparentEnergyDateD.name())) {	    		
        		byte[] data = tag.getData();
                if (data.length == 12) {    	            
                	String str = getDateTime(data);    	                
	                if (!map.containsKey(key)) {
	                    map.put(key, str);    	     
	                }
	            }                
        	} else {
	            if (!map.containsKey(key)) {
	                map.put(key, tag.getValue());                        
	            }
        	}
    	}
    }
    
    private void getOBIS_CODE_EVENT(Map<String, Object> map, String dataName, DLMSGtypeTag tag)
            throws Exception {
    	
    	log.debug("DATA_NAME[" + dataName + "] VALUE[" + (tag.getValue() instanceof OCTET? Hex.decode(((OCTET)tag.getValue()).getValue()):tag.getValue()) + "]");
        if(!UNDEFINED.equals(dataName)) {
        	String key = dataName;
        	
        	if (dataName.equals(EVENT.EventTime.name())) {
        		byte[] data = tag.getData();
                if (data.length == 12) {    	            
                	String str = getDateTime(data);
    	            for (int cnt = 0; ;cnt++) {
    	                key = dataName + "-" + cnt;
    	                if (!map.containsKey(key)) {
    	                    map.put(key, str);
    	                    break;
    	                }
    	            }
                }
        	} else {
        		for (int cnt = 0; ;cnt++) {
                    key = dataName + "-" + cnt;
                    if (!map.containsKey(key)) {
                        map.put(key, tag.getValue());
                        break;
                    }
                }
        	}
        }
    }
    
    private void getOBIS_CODE_POWER_QUALITY(Map<String, Object> map, String dataName, DLMSGtypeTag tag)
            throws Exception {
    	
    	log.debug("DATA_NAME[" + dataName + "] VALUE[" + (tag.getValue() instanceof OCTET? Hex.decode(((OCTET)tag.getValue()).getValue()):tag.getValue()) + "]");
    	
        if(!UNDEFINED.equals(dataName)) {
        	String key = dataName;
        	if (!map.containsKey(key)) {
                map.put(key, tag.getValue());                        
            }
        }
    }
    
    private void getOBIS_CODE_POWER_QUALITY_AVG(Map<String, Object> map, String dataName, DLMSGtypeTag tag)
            throws Exception {
    	
    	log.debug("DATA_NAME[" + dataName + "] VALUE[" + (tag.getValue() instanceof OCTET? Hex.decode(((OCTET)tag.getValue()).getValue()):tag.getValue()) + "]");
        if(!UNDEFINED.equals(dataName)) {
        	String key = dataName;
        	
        	if (dataName.equals(POWER_QUALITY_AVG_PROFILE.Date.name())) {
        		byte[] data = tag.getData();
                if (data.length == 12) {    	            
                	String str = getDateTime(data);
    	            for (int cnt = 0; ;cnt++) {
    	                key = dataName + "-" + cnt;
    	                if (!map.containsKey(key)) {
    	                    map.put(key, str);
    	                    break;
    	                }
    	            }
                }
        	} else {
        		for (int cnt = 0; ;cnt++) {
                    key = dataName + "-" + cnt;
                    if (!map.containsKey(key)) {
                        map.put(key, tag.getValue());
                        break;
                    }
                }
        	}
        }
    }
    
    private void getOBIS_CODE_MEASUREMENT_DATE(Map<String, Object> map, String dataName, DLMSGtypeTag tag)
            throws Exception {    	
    	log.debug("DATA_NAME[" + dataName + "] VALUE[" + (tag.getValue() instanceof OCTET? Hex.decode(((OCTET)tag.getValue()).getValue()):tag.getValue()) + "]");
    	
    	if (dataName.equals(MEASUREMENT_DATE_INFO.TIME.name())) {
    		byte[] data = tag.getData();
            if (data.length == 4) {            	
            	String dateTime = DataUtil.getTimeByDLMS_OCTETSTRING4(data);
            	String key = dataName;
            	if (!map.containsKey(key)) {
                    map.put(key, dateTime);  
                }
            }
    	} else if (dataName.equals(MEASUREMENT_DATE_INFO.DATE.name())) {
    		byte[] data = tag.getData();
            if (data.length == 5) {
            	String dateTime = DataUtil.getDateByDLMS_OCTETSTRING5(data);
            	String key = dataName;
            	if (!map.containsKey(key)) {
                    map.put(key, dateTime);
                }
            }
    	}    	
    }
    
    private void getOBIS_CODE_TDU_DATA(Map<String, Object> map, String dataName, DLMSGtypeTag tag) throws Exception {
    	
    	log.debug("DATA_NAME[" + dataName + "] VALUE[" + (tag.getValue() instanceof OCTET? Hex.decode(((OCTET)tag.getValue()).getValue()):tag.getValue()) + "]");
    	
    	if(!UNDEFINED.equals(dataName)) {
        	String key = dataName;
        	
        	if (dataName.equals(TDU_PROFILE.RTime.name())) {
        		byte[] data = tag.getData();
                if (data.length == 12) {    	            
                	String dateTime = getDateTime(data);
    	            for (int cnt = 0; ;cnt++) {
    	                key = dataName + "-" + cnt;
    	                if (!map.containsKey(key)) {
    	                    map.put(key, dateTime);
    	                    break;
    	                }
    	            }
                }
        	} else {
        		for (int cnt = 0; ;cnt++) {
                    key = dataName + "-" + cnt;
                    if (!map.containsKey(key)) {
                        map.put(key, tag.getValue());
                        break;
                    }
                }
        	}
        }
    }
    
    public String getDateTime(byte[] data) throws Exception {
    	
    	int year = DataFormat.getIntTo2Byte(DataFormat.select(data, 0, 2));
        int month = DataFormat.getIntToByte(data[2]);
        int day = DataFormat.getIntToByte(data[3]);
        int week = DataFormat.getIntToByte(data[4]);
        int hour = DataFormat.getIntToByte(data[5]);
        int min = DataFormat.getIntToByte(data[6]);
        
        DecimalFormat ydf = new DecimalFormat("0000");
        DecimalFormat df = new DecimalFormat("00");
        
        if(year >= 65535) {        	
        	year = 0;
        }
        if(month >= 255)
        	month = 0;
        if(day >= 255) 
        	day = 0;
        if(hour >= 255) 
        	hour = 0;
        if(min >= 255) 
        	min = 0;
        	
        String str = ydf.format(year) + df.format(month) + df.format(day)
                + df.format(hour) + df.format(min);
        
        return str;
    }
}