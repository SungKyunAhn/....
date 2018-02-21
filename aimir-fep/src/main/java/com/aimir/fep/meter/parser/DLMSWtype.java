package com.aimir.fep.meter.parser;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.meter.parser.DLMSWtypeTable.DLMSWtypeTable;
import com.aimir.fep.meter.parser.DLMSWtypeTable.DLMSWtypeVARIABLE;
import com.aimir.fep.meter.parser.DLMSWtypeTable.DLMSWtypeVARIABLE.OBIS;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.model.system.Supplier;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeLocaleUtil;

public class DLMSWtype extends MeterDataParser implements java.io.Serializable {

	private static final long serialVersionUID = 8916023997892795309L;

	private static Log log = LogFactory.getLog(DLMSWtype.class);

	LPData[] lpData = null;
    LinkedHashMap<String, Map<String, Object>> result = 
            new LinkedHashMap<String, Map<String, Object>>();

    int meterConstnt = 0;
    String meterID = "";

    int meterActiveConstant = 1;
    double activePulseConstant = 1;
    
    int interval=0;
    
    Double meteringValue= 0.0;
    
    Object beforeTime = null;
    Object afterTime = null;
    
    byte status = 0x00;
    byte dif = 0x00;
    byte vif = 0x00;
    
    @Override
    public LinkedHashMap<String, Map<String, Object>> getData() {
        Map<String, Object> data = new LinkedHashMap<String, Object>(16, 0.75f, false);
        Map<String, Object> resultSubData = null;
        String key = null;
        
        DecimalFormat decimalf=null;
        SimpleDateFormat datef14=null;
         
        if(meter!=null && meter.getSupplier()!=null){
            Supplier supplier = meter.getSupplier();
            if(supplier !=null){
                String lang = supplier.getLang().getCode_2letter();
                String country = supplier.getCountry().getCode_2letter();
                
                decimalf = TimeLocaleUtil.getDecimalFormat(supplier);
                datef14 = new SimpleDateFormat(TimeLocaleUtil.getDateFormat(14, lang, country));
            }
        }else{
            //locail 정보가 없을때는 기본 포멧을 사용한다.
            decimalf = new DecimalFormat();
            datef14 = new SimpleDateFormat();
        }
        
        for (Iterator i = result.keySet().iterator(); i.hasNext(); ) {
            key = (String)i.next();
            resultSubData = result.get(key);
            // 정복전 결과가 많아서 최근적으로만 넣는다.
            if (key.equals(OBIS.TIME.getCode())) {
            	log.debug("METER_TIME[" + (String)resultSubData.get("MeterTime") + "]");
            	try {
            		data.put(OBIS.getObis(key).getName(),
            		        datef14.format(DateTimeUtil.getDateFromYYYYMMDDHHMMSS((String)resultSubData.get("MeterTime"))));
            	}
            	catch (Exception e) {};
            }
            else {
                if (resultSubData != null) {
                    String idx = "";
                    if (key.lastIndexOf("-") != -1) {
                        idx = key.substring(key.lastIndexOf("-")+1);
                        key = key.substring(0, key.lastIndexOf("-"));
                    }
                    else if (key.lastIndexOf(".") != -1) {
                        idx = key.substring(key.lastIndexOf(".")+1);
                        key = key.substring(0, key.lastIndexOf("."));
                    }
                    String subkey = null;
                    Object subvalue = null;
                    for (Iterator subi = resultSubData.keySet().iterator(); subi.hasNext();) {
                        subkey = (String)subi.next();
                        if (!subkey.contains(DLMSWtypeVARIABLE.UNDEFINED)) {
                            subvalue = resultSubData.get(subkey);
                            if (subvalue instanceof String) {
                                if (((String)subvalue).contains(":date=")) {
                                    try {
                                        data.put(OBIS.getObis(key).getName()+idx+" : "+subkey, datef14.format(DateTimeUtil.getDateFromYYYYMMDDHHMMSS(((String)subvalue).substring(6)+"00")));
                                    }
                                    catch (Exception e) {
                                        data.put(OBIS.getObis(key).getName()+idx+" : "+subkey, subvalue);
                                    }
                                }
                                else if (subkey.contains("Date") && !((String)subvalue).contains(":date=") && ((String)subvalue).length()==12) {
                                    try {
                                        data.put(OBIS.getObis(key).getName()+idx+" : "+subkey, datef14.format(DateTimeUtil.getDateFromYYYYMMDDHHMMSS(subvalue+"00")));
                                    }
                                    catch (Exception e) {
                                        data.put(OBIS.getObis(key).getName()+idx+" : "+subkey, subvalue);
                                    }
                                }
                                else data.put(OBIS.getObis(key).getName()+idx+" : "+subkey, subvalue);
                            }
                            else if (subvalue instanceof Number) {
                            	if (OBIS.getObis(key) != OBIS.METER_CONSTANT_ACTIVE && 
                            	        OBIS.getObis(key) != OBIS.METER_CONSTANT_REACTIVE && 
                            	        !subkey.contains("PowerFactor") && !subkey.contains("Interval") &&
                            	        OBIS.getObis(key) != OBIS.POWER_QUALITY && !subkey.contains("Count"))
                            		data.put(OBIS.getObis(key).getName()+idx+" : "+subkey, decimalf.format(((Number) subvalue).doubleValue() / activePulseConstant));
                            	else data.put(OBIS.getObis(key).getName()+idx+" : "+subkey, decimalf.format(subvalue));
                            }
                            else data.put(OBIS.getObis(key).getName()+idx+" : "+subkey, subvalue);
                        }
                    }
                }
            }
        }
        return (LinkedHashMap)data;
    }

    @Override
    public void parse(byte[] data) throws Exception {
        log.debug("DLMS parse:"+Hex.decode(data));

        String obisCode = "";
        int clazz = 0;
        int attr = 0;

        int pos = 0;
        int len = 0;
        // DLMS Header OBIS(6), CLASS(2), ATTR(1), LENGTH(2)
        // DLMS Tag Tag(1), DATA or LEN/DATA (*)
        byte[] OBIS = new byte[6];
        byte[] CLAZZ = new byte[2];
        byte[] ATTR = new byte[1];
        byte[] LEN = new byte[2];
        byte[] TAGDATA = null;
        
        DLMSWtypeTable dlms = null;
        while ((pos+OBIS.length) < data.length) {
            log.debug("POS[" + pos + "] Data.LEN[" + data.length + "]");
            dlms = new DLMSWtypeTable();
            System.arraycopy(data, pos, OBIS, 0, OBIS.length);
            pos += OBIS.length;
            obisCode = Hex.decode(OBIS);
            log.debug("OBIS[" + obisCode + "]");
            dlms.setObis(obisCode);
            
            System.arraycopy(data, pos, CLAZZ, 0, CLAZZ.length);
            pos += CLAZZ.length;
            clazz = DataUtil.getIntToBytes(CLAZZ);
            log.debug("CLASS[" + clazz + "]");
            dlms.setClazz(clazz);
            
            System.arraycopy(data, pos, ATTR, 0, ATTR.length);
            pos += ATTR.length;
            attr = DataUtil.getIntToBytes(ATTR);
            log.debug("ATTR[" + attr + "]");
            dlms.setAttr(attr);

            System.arraycopy(data, pos, LEN, 0, LEN.length);
            pos += LEN.length;
            len = DataUtil.getIntTo2Byte(LEN);
            log.debug("LENGTH[" + len + "]");
            dlms.setLength(len);

            TAGDATA = new byte[len];
            if (pos + TAGDATA.length <= data.length) {
            	System.arraycopy(data, pos, TAGDATA, 0, TAGDATA.length);
            	pos += TAGDATA.length;
            }
            else {
            	System.arraycopy(data, pos, TAGDATA, 0, data.length-pos);
            	pos += data.length-pos;
            }
            
            
            dlms.parseDlmsTag(TAGDATA);
            
            // 동일한 obis 코드를 가진 값이 있을 수 있기 때문에 검사해서 _number를 붙인다.
            if (dlms.getDlmsHeader().getObis() == DLMSWtypeVARIABLE.OBIS.LOAD_PROFILE) {
                for (int cnt = 0; ;cnt++) {
                    obisCode = dlms.getDlmsHeader().getObis().getCode() + "-" + cnt;
                    if (!result.containsKey(obisCode)) {
                        result.put(obisCode, dlms.getData());
                        break;
                    }
                }
            }
            else {
            	if (result.containsKey(obisCode)) {
            		result.put(obisCode+".1", dlms.getData());
            	}
            	else {
            		result.put(obisCode, dlms.getData());
            	}
            }
        }

        Map<String, Object> map = (Map<String, Object>) result.get(DLMSWtypeVARIABLE.OBIS.COLD_WATER.getCode());
        byte[] b = (byte[])map.get(DLMSWtypeVARIABLE.OBIS.COLD_WATER.getName());
        byte c_field = b[4];
        if ((c_field & 0x08) == 0x08) {
            byte ci_field = b[6];
            // CI=78h는 M-Bus 프로토콜에서 Data Header가 없음을 의미
            if ((ci_field == 0x78)) {
                // MDH 8 digit BCD
                String meterId = Hex.decode(new byte[]{b[11], b[10], b[9], b[8]});
                log.debug("METER_ID[" + meterId + "]");
                
                status = b[12];
                dif = b[13];
                vif = b[14];
                vif &= 0x0F;
                
                meteringValue = (double)DataUtil.getLongToBytes(new byte[]{b[18], b[17], b[16], b[15]}) / Math.pow(10.0, (double)vif);
                log.debug("METERING_VALUE[" + meteringValue + "]");
            }
        }
        log.debug("DLMS parse result:" + Hex.decode(b));
    }

    public byte getDIF() {
        return this.dif;
    }
    
    public byte getVIF() {
        return this.vif;
    }
    
    public byte getStatus() {
        return this.status;
    }
    
    @Override
    public void setFlag(int flag) {
    }

    @Override
    public String toString() {

        return null;
    }

    public LPData[] getLPData() {

        return lpData;
    }

    public Integer getInterval() {
        return this.interval;
    }
    
    public Double getActivePulseConstant() {
        return this.activePulseConstant;
    }

    public String getMeterID() {
        return this.meter.getMdsId();
    }

    @Override
    public int getFlag() {
        return 0;
    }

    @Override
    public int getLength() {
        return 0;
    }

    @Override
    public Double getMeteringValue() {
        return meteringValue;
    }

    @Override
    public byte[] getRawData() {
        return null;
    }
}
