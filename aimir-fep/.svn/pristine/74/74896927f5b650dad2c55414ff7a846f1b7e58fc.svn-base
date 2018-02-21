package com.aimir.fep.meter.parser;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.meter.data.HMData;
import com.aimir.fep.meter.parser.kamstrup601Table.DataBlock2;
import com.aimir.fep.meter.parser.kamstrup601Table.TMTR_CUMM;
import com.aimir.fep.meter.parser.kamstrup601Table.TMTR_CURRENT;
import com.aimir.fep.meter.parser.kamstrup601Table.TMTR_DAY;
import com.aimir.fep.meter.parser.kamstrup601Table.TMTR_EVENT;
import com.aimir.fep.meter.parser.kamstrup601Table.TMTR_INFO;
import com.aimir.fep.meter.parser.kamstrup601Table.TMTR_LP;
import com.aimir.fep.meter.parser.kamstrup601Table.TMTR_MONTH;
import com.aimir.fep.meter.parser.kamstrup601Table.TMTR_VALUE;
import com.aimir.fep.meter.parser.kdhTable.TMTR_MDM;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Util;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;

/**
 * parsing NURI_Kamstrup601 Meter Data
 * implemented in Daehan gas
 *
 * @author Yeon Kyoung Park (goodjob@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2009-01-19 12:00:15 +0900 $,
 */
public class NURI_Kamstrup601 extends MeterDataParser implements java.io.Serializable
{
	private static final long serialVersionUID = -3682319889045923864L;
	private static Log log = LogFactory.getLog(NURI_Kamstrup601.class);
    private byte[] rawData = null;
    private int lpcount;
    private Double lp = null;
    private Double lpValue = null;
    private String meterId = null;
    private int flag = 0;

    private byte[] mdm = null;
    private byte[] rmtr_info = null;
    private byte[] rmtr_current = null;
    private byte[] rmtr_lp = null;
    private byte[] rmtr_day = null;
    private byte[] rmtr_month = null;
    private byte[] rmtr_event = null;
    private byte[] rmtr_cumm = null;
    private byte[] rmtr_value = null;
    private byte lp_sign_exp = 0x43;
    private byte resolution = 60;
    
    private TMTR_MDM tmtr_mdm = null;    
    private TMTR_INFO tmtr_info = null;
    private TMTR_CURRENT tmtr_current = null;
    private TMTR_LP tmtr_lp = null;
    private TMTR_DAY tmtr_day = null;    
    private TMTR_MONTH tmtr_month = null;
    private TMTR_EVENT tmtr_event = null;
    private TMTR_CUMM tmtr_cumm = null;
    private TMTR_VALUE tmtr_value = null;
    
    private ArrayList LPList = null;

    DecimalFormat dformat = new DecimalFormat("#0.000000");
    
    HMData[] days = null;
    HMData[] months = null;
    HMData[] lps = null;
    HMData[] current = null;
    		
    String chUnit =null;
    String chMap =null;
    /**
     * constructor
     */
    public NURI_Kamstrup601()
    {
    }

    /**
     * get LP
     */
    public Double getLp()
    {
        return this.lp;
    }

    /**
     * get LP Value ( lp * pulse divider )
     */
    public Double getLpValue()
    {
        return this.lpValue;
    }
    /**
     * get meter id
     * @return meter id
     */
    public String getMeterId()
    {
        return meterId;
    }

    /**
     * get raw Data
     */
    public byte[] getRawData()
    {
        return this.rawData;
    }

    public int getLength()
    {
        return this.rawData.length;
    }
    
    public int getLPCount(){
        return this.lpcount;
    }
    
    public void parse_NC5K1(byte[] data, int offset, String fwVersion) throws Exception {
        
    	byte[] b = null;
        while(offset < data.length){
            
            log.debug("[Offset] =["+offset+"] data=>"+(int)(data[offset]&0xFF));
            
            switch((int)(data[offset++] & 0xFF))
            {
            case TMTR_LP.TABLE_CODE:
            	log.debug("[TMTR_LP]");
                int LEN_TMTR_LP = 0;
                LEN_TMTR_LP = DataFormat.hex2dec(DataFormat.select(data,offset,2));
                log.debug("LEN_TMTR_LP :"+LEN_TMTR_LP);
                offset +=2;
                b = new byte[LEN_TMTR_LP];
                System.arraycopy(data,offset,b,0,LEN_TMTR_LP);
                rmtr_lp = b;
                offset += LEN_TMTR_LP;
                
                log.debug("[TMTR_LP] len=["+LEN_TMTR_LP+"] data=>"+Util.getHexString(rmtr_lp));
                tmtr_lp = new TMTR_LP(rmtr_lp,TMTR_LP.TABLE_KIND);
                log.debug(tmtr_lp);
                break;
            case TMTR_MONTH.TABLE_CODE:
                int LEN_TMTR_MONTH = 0;
                LEN_TMTR_MONTH = DataFormat.hex2dec(DataFormat.select(data,offset,2));
                offset +=2;
                b = new byte[LEN_TMTR_MONTH];
                System.arraycopy(data,offset,b,0,LEN_TMTR_MONTH);
                rmtr_month = b;
                offset += LEN_TMTR_MONTH;
                
                log.debug("[TMTR_MONTH] len=["+LEN_TMTR_MONTH+"] data=>"+Util.getHexString(rmtr_month));
                tmtr_month = new TMTR_MONTH(rmtr_month,TMTR_MONTH.TABLE_KIND);
                log.debug(tmtr_month);
                break;
        	case TMTR_CURRENT.TABLE_CODE:
        		log.debug("[TMTR_CURRENT]");
        		int LEN_TMTR_CURRENT = 0;
        		LEN_TMTR_CURRENT = DataFormat.hex2dec(DataFormat.select(data,offset,2));
        		offset +=2;
        		b = new byte[LEN_TMTR_CURRENT];
        		System.arraycopy(data,offset,b,0,LEN_TMTR_CURRENT);
        		rmtr_current = b;
        		offset += LEN_TMTR_CURRENT;

        		log.debug("[TMTR_CURRENT] len=["+LEN_TMTR_CURRENT+"] data=>"+Util.getHexString(rmtr_current));
        		tmtr_current = new TMTR_CURRENT(rmtr_current,TMTR_CURRENT.TABLE_KIND);
        		lp_sign_exp = rmtr_current[24];
        		log.debug(tmtr_current);
        		break;
    		case TMTR_DAY.TABLE_CODE:
    			int LEN_TMTR_DAY = 0;
    			LEN_TMTR_DAY = DataFormat.hex2dec(DataFormat.select(data,offset,2));
    			log.debug("[LEN_TMTR_DAY]" + LEN_TMTR_DAY);
    			offset +=2;
    			b = new byte[LEN_TMTR_DAY];
    			System.arraycopy(data,offset,b,0,LEN_TMTR_DAY);
    			rmtr_day = b;
    			offset += LEN_TMTR_DAY;
        
    			log.debug("[TMTR_DAY] len=["+LEN_TMTR_DAY+"] data=>"+Util.getHexString(rmtr_day));
    			tmtr_day = new TMTR_DAY(rmtr_day,TMTR_DAY.TABLE_KIND);
    			log.debug(tmtr_day);
    			break;
    		case TMTR_EVENT.TABLE_CODE:
    			int LEN_TMTR_EVENT = 0;
    			LEN_TMTR_EVENT = DataFormat.hex2dec(DataFormat.select(data,offset,2));
    			log.debug("[LEN_TMTR_EVENT]" + LEN_TMTR_EVENT);
    			offset +=2;
    			b = new byte[LEN_TMTR_EVENT];
    			System.arraycopy(data,offset,b,0,LEN_TMTR_EVENT);
    			rmtr_event = b;
    			offset += LEN_TMTR_EVENT;
    			log.debug("[TMTR_EVENT] len=["+LEN_TMTR_EVENT+"] data=>"+Util.getHexString(rmtr_event));
    			tmtr_event = new TMTR_EVENT(rmtr_event, TMTR_EVENT.TABLE_KIND);
    			log.debug(tmtr_event);
    			break;
    		case TMTR_CUMM.TABLE_CODE:
    			int LEN_TMTR_CUMM = 0;
    			LEN_TMTR_CUMM = DataFormat.hex2dec(DataFormat.select(data,offset,2));
				byte flow_sign_exp = 0x43;
				if(tmtr_current != null){
					flow_sign_exp = tmtr_current.getFlowSIGNEXP();
				}
    			log.debug("[LEN_TMTR_CUMM]" + LEN_TMTR_CUMM);
    			offset +=2;
    			if(LEN_TMTR_CUMM>0){
	    			b = new byte[LEN_TMTR_CUMM];
	    			System.arraycopy(data,offset,b,0,LEN_TMTR_CUMM);
	    			rmtr_cumm = b;
	    			offset += LEN_TMTR_CUMM;
	    			log.debug("[TMTR_CUMM] len=["+LEN_TMTR_CUMM+"] data=>"+Util.getHexString(rmtr_cumm));
	    			tmtr_cumm = new TMTR_CUMM(rmtr_cumm, getSignEX(lp_sign_exp),getSignEX(flow_sign_exp));
	    			log.debug(tmtr_cumm);
    			}
    			break;
    		case TMTR_VALUE.TABLE_CODE:
    			int LEN_TMTR_VALUE = 0;
    			LEN_TMTR_VALUE = DataFormat.hex2dec(DataFormat.select(data,offset,2));
    			log.debug("[LEN_TMTR_VALUE]" + LEN_TMTR_VALUE);
    			offset +=2;
    			if(LEN_TMTR_VALUE>0){
	    			b = new byte[LEN_TMTR_VALUE];
	    			System.arraycopy(data,offset,b,0,LEN_TMTR_VALUE);
	    			rmtr_value = b;
	    			offset += LEN_TMTR_VALUE;
	    			log.debug("[TMTR_VALUE] len=["+LEN_TMTR_VALUE+"] data=>"+Util.getHexString(rmtr_value));
	    			tmtr_value = new TMTR_VALUE(rmtr_cumm, resolution, getSignEX(lp_sign_exp));
	    			log.debug(rmtr_value);
    			}
    			break;
    		 default :
    			 offset = data.length;
    			 break;
            }
        }
    }
    
    public void parse_NC5K2(byte[] data, int pos) throws Exception {
    	DataBlock2 db = null;
        byte[] YEAR = new byte[2];
        byte[] MONTH = new byte[1];
        byte[] DAY = new byte[1];
    	byte[] ALMONTH = new byte[14];
    	byte[] LMONTH = new byte[14];
    	byte[] BASERECORD_1 = new byte[31];
    	byte[] BASERECORD = new byte[31];
    	byte[] LPLENGTH = new byte[2];
    	byte[] LPRECORD = null;
    	byte[] EVDATA = null;
    	
        System.arraycopy(data, pos, YEAR, 0, YEAR.length);
        pos += YEAR.length;
        DataUtil.convertEndian(YEAR);
        int year = DataUtil.getIntTo2Byte(YEAR);
        log.debug("YEAR[" + year + "]");
        
        System.arraycopy(data, pos, MONTH, 0, MONTH.length);
        pos += MONTH.length;
        int month = DataUtil.getIntToBytes(MONTH);
        log.debug("MONTH[" + month + "]");
        
        System.arraycopy(data, pos, DAY, 0, DAY.length);
        pos += DAY.length;
        int day = DataUtil.getIntToBytes(DAY);
        log.debug("DAY[" + day + "]");

        int hour = 0;
        
        String date = Integer.toString(year)
        + (month < 10? "0"+month:""+month)
        + (day < 10? "0"+day:""+day);
        
        
        System.arraycopy(data, pos, ALMONTH, 0, ALMONTH.length);
        pos += ALMONTH.length;
        
        double almonth_data = 0d;
        double almonth_flow = 0d;
        double lmonth_data = 0d;
        double lmonth_flow = 0d;
        
        db = new DataBlock2(ALMONTH,0,7);
        if(db.getValue() != null && db.getValue().size() > 0){
            almonth_data = ((Double)(db.getValue().get(0))).doubleValue();
        }

        db = new DataBlock2(ALMONTH,7,7);
        if(db.getValue() != null && db.getValue().size() > 0){
            almonth_flow = ((Double)(db.getValue().get(0))).doubleValue();
        }
       
        System.arraycopy(data, pos, LMONTH, 0, LMONTH.length);
        pos += LMONTH.length;
        
        db = new DataBlock2(LMONTH,0,7);
        if(db.getValue() != null && db.getValue().size() > 0){
            lmonth_data = ((Double)(db.getValue().get(0))).doubleValue();
        }

        db = new DataBlock2(LMONTH,7,7);
        if(db.getValue() != null && db.getValue().size() > 0){
            lmonth_flow = ((Double)(db.getValue().get(0))).doubleValue();
        }

        
        this.months = new HMData[1];
        months[0] = new HMData();
        months[0].setKind("MONTH"); 
        months[0].setDate(date.substring(0,6)+"01");
        months[0].setTime("000000");
        months[0].setChannelCnt(4);
        months[0].setCh( 1, new Double(lmonth_data-almonth_data));
        months[0].setCh( 2, new Double(lmonth_data));
        months[0].setCh( 3, new Double(lmonth_flow-almonth_flow));
        months[0].setCh( 4, new Double(lmonth_flow));        
        
        System.arraycopy(data, pos, BASERECORD_1, 0, BASERECORD_1.length);
        pos += BASERECORD_1.length;
        
        double base_1_ch1 = 0d;
        double base_1_ch2 = 0d;
        double base_1_ch3 = 0d;
        double base_1_ch4 = 0d;
        double base_1_ch5 = 0d;
        
        double base_ch1 = 0d;
        double base_ch2 = 0d;
        double base_ch3 = 0d;
        double base_ch4 = 0d;
        double base_ch5 = 0d;
        
        db = new DataBlock2(BASERECORD_1,0);
        if(db.getValue() != null && db.getValue().size() > 0){base_1_ch1 = ((Double)(db.getValue().get(0))).doubleValue();}
        db = new DataBlock2(BASERECORD_1,7);
        if(db.getValue() != null && db.getValue().size() > 0){base_1_ch2 = ((Double)(db.getValue().get(0))).doubleValue();}
        db = new DataBlock2(BASERECORD_1,14);
        if(db.getValue() != null && db.getValue().size() > 0){base_1_ch3 = ((Double)(db.getValue().get(0))).doubleValue();}
        db = new DataBlock2(BASERECORD_1,21);
        if(db.getValue() != null && db.getValue().size() > 0){base_1_ch4 = ((Double)(db.getValue().get(0))).doubleValue();}
        db = new DataBlock2(BASERECORD_1,26);
        if(db.getValue() != null && db.getValue().size() > 0){base_1_ch5 = ((Double)(db.getValue().get(0))).doubleValue();}
        
        System.arraycopy(data, pos, BASERECORD, 0, BASERECORD.length);
        pos += BASERECORD.length;
        
        db = new DataBlock2(BASERECORD,0);
        if(db.getValue() != null && db.getValue().size() > 0){base_ch1 = ((Double)(db.getValue().get(0))).doubleValue();}
        db = new DataBlock2(BASERECORD,7);
        if(db.getValue() != null && db.getValue().size() > 0){base_ch2 = ((Double)(db.getValue().get(0))).doubleValue();}
        db = new DataBlock2(BASERECORD,14);
        if(db.getValue() != null && db.getValue().size() > 0){base_ch3 = ((Double)(db.getValue().get(0))).doubleValue();}
        db = new DataBlock2(BASERECORD,21);
        if(db.getValue() != null && db.getValue().size() > 0){base_ch4 = ((Double)(db.getValue().get(0))).doubleValue();}
        db = new DataBlock2(BASERECORD,26);
        if(db.getValue() != null && db.getValue().size() > 0){base_ch5 = ((Double)(db.getValue().get(0))).doubleValue();}
        
        this.days = new HMData[1];
        days[0] = new HMData();
        days[0].setKind("DAY"); 
        days[0].setDate(date.substring(0,8));
        days[0].setTime("000000");
        days[0].setChannelCnt(10);
        days[0].setCh( 1, new Double(base_ch1-base_1_ch1));
        days[0].setCh( 2, new Double(base_ch1));
        days[0].setCh( 3, new Double(base_ch2-base_1_ch2));
        days[0].setCh( 4, new Double(base_ch2));
        days[0].setCh( 5, new Double(base_ch3));
        days[0].setCh( 8, new Double(base_ch4));
        days[0].setCh( 9, new Double(base_ch5));
        days[0].setCh(10, new Double(base_ch5-base_ch4));
        
        System.arraycopy(data, pos, LPLENGTH, 0, LPLENGTH.length);
        pos += LPLENGTH.length;
        DataUtil.convertEndian(LPLENGTH);
        int lpcount = DataUtil.getIntTo2Byte(LPLENGTH);
        log.debug("LPCOUNT[" + lpcount + "]");
        
        LPRECORD = new byte[31*lpcount];
        System.arraycopy(data, pos, LPRECORD, 0, LPRECORD.length);
        pos += LPRECORD.length;
        
        ArrayList lpDBList = new ArrayList();
        double last_gcal = base_ch1;
        double last_flow = base_ch2;
        
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR,year);
        cal.set(Calendar.MONTH,month-1);
        cal.set(Calendar.DATE,day);
        cal.set(Calendar.HOUR,hour);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);
        
        int ofs = 0;
        double lp_gcal = 0d;
        double lp_flow = 0d;
        double lp_pres = 0d;
        double lp_st = 0d;
        double lp_rt = 0d;
        HMData lp = null;
        
        for(int i = 0; i < lpcount; i++){
        	hour = i;        	
            cal.set(year,month-1,day,hour,0,0);
            String datetime = TimeUtil.getFormatTime(cal);
            log.debug("datetime["+datetime+"]");
            
            db = new DataBlock2(LPRECORD,ofs,7);
            ofs += 7;
            if(db.getValue() != null && db.getValue().size() > 0){
            	lp_gcal = ((Double)(db.getValue().get(0))).doubleValue();
            	chUnit = db.getUnit();
            	log.debug("chUnit :"+ chUnit);
            }else{
            	lp_gcal = 0d;
            }
            db = new DataBlock2(LPRECORD,ofs,7);
            ofs += 7;
            if(db.getValue() != null && db.getValue().size() > 0){lp_flow = ((Double)(db.getValue().get(0))).doubleValue();}else{lp_flow = 0d;}
            db = new DataBlock2(LPRECORD,ofs,7);
            ofs += 7;
            if(db.getValue() != null && db.getValue().size() > 0){lp_pres = ((Double)(db.getValue().get(0))).doubleValue();}else{lp_pres = 0d;}
            db = new DataBlock2(LPRECORD,ofs,5);//+5
            ofs += 5;
            if(db.getValue() != null && db.getValue().size() > 0){lp_st = ((Double)(db.getValue().get(0))).doubleValue();}else{lp_st = 0d;}
            db = new DataBlock2(LPRECORD,ofs,5);// +5
            ofs += 5;
            if(db.getValue() != null && db.getValue().size() > 0){lp_rt = ((Double)(db.getValue().get(0))).doubleValue();}else{lp_rt = 0d;}
            
            double lp_gcal_usage = lp_gcal - last_gcal;
            last_gcal = lp_gcal;
            double lp_flow_usage = lp_flow - last_flow;
            last_flow = lp_flow;

            if(db.getValue() != null && db.getValue().size() > 0){
                lp = new HMData();
                lp.setKind("LP"); 
                lp.setDate(datetime.substring(0,8));
                lp.setTime(datetime.substring(8,14));
                lp.setChannelCnt(10);
                lp.setCh( 1, new Double(lp_gcal_usage));
                lp.setCh( 2, new Double(lp_gcal));
                lp.setCh( 3, new Double(lp_flow_usage));
                lp.setCh( 4, new Double(lp_flow));
                lp.setCh( 5, new Double(lp_pres));
                lp.setCh( 8, new Double(lp_st));
                lp.setCh( 9, new Double(lp_rt));
                lp.setCh(10, new Double(lp_st-lp_rt));
                lpDBList.add(lp);
                
                if(i == (lpcount-1)){
                    current = new HMData[1];
                    current[0] = new HMData();
                    current[0].setKind("CURRENT"); 
                    current[0].setDate(datetime.substring(0,8));
                    current[0].setTime(datetime.substring(8,12));
                    current[0].setChannelCnt(10);
                    current[0].setCh( 1, new Double(lp_gcal_usage));
                    current[0].setCh( 2, new Double(lp_gcal));
                    current[0].setCh( 3, new Double(lp_flow_usage));
                    current[0].setCh( 4, new Double(lp_flow));
                    current[0].setCh( 5, new Double(lp_pres));
                    current[0].setCh( 8, new Double(lp_st));
                    current[0].setCh( 9, new Double(lp_rt));
                    current[0].setCh(10, new Double(lp_st-lp_rt));
                }
            }

        }
        
        if(lpDBList != null && lpDBList.size() > 0){
            Object[] obj = lpDBList.toArray();        
            lps = new HMData[obj.length];
            for(int i = 0; i < obj.length; i++){
            	lps[i] = (HMData)obj[i];
            }
        }

        try{
            int code = (int)(data[pos++] & 0xFF);
            
            
    		int LEN_TMTR_EVENT = 0;
    		LEN_TMTR_EVENT = DataFormat.hex2dec(DataFormat.LSB2MSB(DataFormat.select(data,pos,2)));
    		log.debug("[LEN_TMTR_EVENT]" + LEN_TMTR_EVENT);
    		pos +=2;
    		byte[] b = new byte[LEN_TMTR_EVENT];
    		System.arraycopy(data,pos,b,0,LEN_TMTR_EVENT);
    		rmtr_event = b;
    		pos += LEN_TMTR_EVENT;
    		log.debug("[TMTR_EVENT] len=["+LEN_TMTR_EVENT+"] data=>"+Util.getHexString(rmtr_event));
    		tmtr_event = new TMTR_EVENT(rmtr_event, TMTR_EVENT.TABLE_KIND);
    		log.debug(tmtr_event);
        }catch(Exception e){
        	//log.error(e,e);
        }

    }
    
    public void parse_NC5K3(byte[] data, int offset)throws Exception {
    	parse_NC5K2(data,offset);
    }

    /**
     * parseing Energy Meter Data of NURI_Kamstrup601 Meter
     * @param data stream of result command
     */
    public void parse(byte[] data) throws Exception
    {
        int offset = 0;
        int LEN_MDM_INFO = 38;
        
        log.debug("[TOTAL] len=["+data.length+"] data=>"+Util.getHexString(data));
        if (data.length < 43) {

            if(data.length == LEN_MDM_INFO)
            {
                byte[] b = new byte[LEN_MDM_INFO];
                System.arraycopy(data,offset,b,0,LEN_MDM_INFO);
                mdm = b;
                log.debug("[NURI_Kamstrup601_MDM] len=["+LEN_MDM_INFO+"] data=>"+Util.getHexString(mdm));
                tmtr_mdm = new TMTR_MDM(mdm);
                log.debug(tmtr_mdm);
            }
            else
            {
                log.error("[NURI_Kamstrup601] Data total length[" + data.length + "] is invalid");
            }
            
            return;
        }
        
        byte[] b = new byte[LEN_MDM_INFO];
        System.arraycopy(data,offset,b,0,LEN_MDM_INFO);
        mdm = b;
        offset += LEN_MDM_INFO;
        log.debug("[NURI_Kamstrup601_MDM] len=["+LEN_MDM_INFO+"] data=>"+Util.getHexString(mdm));
        tmtr_mdm = new TMTR_MDM(mdm);
        log.debug(tmtr_mdm);
   
        int totlen = data.length;

        int len =0;
        log.debug("offset=["+offset+"]");

        b = new byte[TMTR_INFO.LEN_TMTR_INFO];
        System.arraycopy(data,offset,b,0,TMTR_INFO.LEN_TMTR_INFO);
        rmtr_info = b;
        offset += TMTR_INFO.LEN_TMTR_INFO;
        log.debug("[TMTR_INFO] len=["+TMTR_INFO.LEN_TMTR_INFO+"] data=>"+Util.getHexString(rmtr_info));
        tmtr_info = new TMTR_INFO(rmtr_info);
        this.meterId = tmtr_info.getMETER_ID();
        log.debug(tmtr_info);
        
        if(tmtr_mdm.getFW_VER().equals("NC5K1") || tmtr_mdm.getFW_VER().equals("NC5K4")){
        	parse_NC5K1(data,offset,tmtr_mdm.getFW_VER() );
        }
        
        if(tmtr_mdm.getFW_VER().equals("NC5K2")){
        	parse_NC5K2(data,offset);
        }
        
        if(tmtr_mdm.getFW_VER().equals("NC5K3")){
        	parse_NC5K3(data,offset);
        }


        log.debug("NURI_Kamstrup601 Data Parse Finished :: DATA["+toString()+"]");
    }

    public double getSignEX(byte byteSiEx){
    	double siEx = 0.0;
    	try{
			
		//	int intSiEx = DataUtil.getIntToBytes(byteSiEx);
		    double signInt=(byteSiEx & 128)/128;
		    double signExp=(byteSiEx & 64)/64;
		    double exp=((byteSiEx&32) + (byteSiEx&16) + (byteSiEx&8) + (byteSiEx&4) + (byteSiEx&2) + (byteSiEx&1));
		    siEx=Math.pow(-1, signInt)*Math.pow(10, Math.pow(-1, signExp)*exp);//-1^SI*-1^SE*exponent
		    return siEx;
    	}catch(Exception e){
    		log.error("get Sign EX=>",e);
    		return siEx;
    	}
    }
    /**
     * get flag
     * @return flag measurement flag
     */
    public int getFlag()
    {
        return this.flag;
    }

    /**
     * set flag
     * @param flag measurement flag
     */
    public void setFlag(int flag)
    {
        this.flag = flag;
    }

    
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
       
        sb.append("NURI_Kamstrup601 Meter DATA[");
        sb.append("(meterId=").append(meterId).append("),");
        sb.append("]\n");

        return sb.toString();
    }
    
    public String getLPChannelMap(){
    	
    	if(chUnit==null && tmtr_lp!=null)
    		chUnit = tmtr_lp.getChUnit();
    	
    	log.debug("chUnit :"+chUnit);
    	
    	if(chUnit!=null && chUnit.length()>0)
    		return "ch1=HourlyHeat["+chUnit+"],ch2=CumulativeHeat["+chUnit+"],ch3=InstantFlow[m3],ch4=CumulativeFlow[m3]," +
        		"ch5=InstantPressure,ch6=m3T1,ch7=m3T2,ch8=SupplyTemperature,ch9=RetrieveTemperature,ch10=TemperatureDifference";
    	else return "";
    }
    
    public String getMeteringUnit(){
    	
    	if(this.chUnit==null && this.tmtr_lp!=null)
    		this.chUnit = tmtr_lp.getChUnit();
    	else this.chUnit = "";
    	
    	return this.chUnit;
    }
       
    public int getLPChannelCount(){
        return 10;//ch1- ch10
    }

    public int getResolution(){
        return 60;
    }
    
    public String getMeterLog(){
    	
        try{
            if(tmtr_info != null){
                return tmtr_info.getMeterCautionFlag().getLog();
            }else{
                return "";
            }
        }catch(Exception e){
            
        }
        return "";
    }
    
    public EventLogData[] getMeterStatusLog(){
        return null;
    }

    public int getMeterStatusCode() {
        try{
            if (tmtr_info != null) {
                return tmtr_info.getMeterCautionFlag().getStatusCode();
            }
            else {
                return 0;
            }
        }catch(Exception e){
            
        }
        return 0;
    }

    public HMData[] getLPHMData(){
    	
    	HMData[] lp = null;
    	if(lps != null && lps.length > 0){
    		return this.lps;
    	}
    	lps = null;
    	int chCnt =  1;
    	try{
	        if(tmtr_lp != null){
	        	lp =  tmtr_lp.getData();
	        	if(tmtr_cumm != null){
	        		HMData cumm = tmtr_cumm.getData()[0];
	        		String cummDateTime = (String) cumm.getDate()+"000000";
	        		log.debug("cummDateTime :"+ cummDateTime);
	        		
	        		int idxOf0am =-1;
	        		//find the lp data at 0 a.m
	        		for(int i=0; i<lp.length; i++){
	        			if(((String)lp[i].getDate()+""+(String)lp[i].getTime()).equals(cummDateTime)){
	        				idxOf0am =i;
	        				break;
	        			}
	        		}
	        		log.debug("idxOf0am :"+idxOf0am);
	        		
	        		if(idxOf0am>=0){
		        		Double[] currentCh = cumm.getCh();
		        		chCnt = currentCh.length;
		        		
		        		double basePulse2 = 0d;
		        		double basePulse1 = currentCh[0].doubleValue();
		        		if(chCnt == 2){
			        		basePulse2 = currentCh[1].doubleValue();
			        		log.debug("basePulse1="+basePulse1);
			        		log.debug("basePulse2="+basePulse2);
		        		}
		        		for(int i=(idxOf0am); i>=0; i--){
		        			
		        			if(((String)lp[i].getDate()+""+(String)lp[i].getTime()).equals(TimeUtil.getPreHour(cummDateTime, (i-idxOf0am)))){
		        				Double[] lpData = lp[i].getCh();
		        				lp[i].setCh(2, new Double(dformat.format(basePulse1)));
								basePulse1 += lpData[0].doubleValue();
		        				if(chCnt == 2){
			        				lp[i].setCh(4, new Double(dformat.format(basePulse2)));
									basePulse2 += lpData[2].doubleValue();
		        				}
		        			}
		        		}		        		

		        		basePulse1 = currentCh[0].doubleValue();
		        		if(chCnt == 2){
			        		basePulse2 = currentCh[1].doubleValue();
		        		}
		        		log.debug("lp.length : "+ lp.length);
		        		for(int i=idxOf0am+1; i<lp.length; i++){
		        			if(((String)lp[i].getDate()+""+(String)lp[i].getTime()).equals(TimeUtil.getPreHour(cummDateTime, (i-idxOf0am)))){
		        				Double[] lpData = lp[i].getCh();
		        				lp[i].setCh(2, new Double(dformat.format(basePulse1)));
								basePulse1 -= lpData[0].doubleValue();
		        				if(chCnt == 2){
			        				lp[i].setCh(4, new Double(dformat.format(basePulse2)));
									basePulse2 -= lpData[2].doubleValue();
		        				}
		        			}
		        		}

	        		}
	        	}
	        	
	        	if(chCnt == 1 && tmtr_current != null){//temporary souce
	        		HMData curr = tmtr_current.getData()[0];
	        		String currDateTime = (String) curr.getDate()+curr.getTime().substring(0,2)+"0000";
	        		log.debug("currDateTime :"+ currDateTime);
	        		
	        		int idxOf0am =-1;
	        		//find the lp data at 0 a.m
	        		for(int i=0; i<lp.length; i++){
	        			if(((String)lp[i].getDate()+""+(String)lp[i].getTime()).equals(currDateTime)){
	        				idxOf0am =i;
	        				break;
	        			}
	        		}
	        		log.debug("idxOf0am :"+idxOf0am);
	        		
	        		if(idxOf0am>=0){
		        		Double[] currentCh = curr.getCh();
		        		double nowCurrent = currentCh[3].doubleValue();
		        		for(int i=(idxOf0am); i>=0; i--){
		        			
		        			if(((String)lp[i].getDate()+""+(String)lp[i].getTime()).equals(TimeUtil.getPreHour(currDateTime, (i-idxOf0am)))){
		        				Double[] lpData = lp[i].getCh();
		        				lp[i].setCh(4, new Double(dformat.format(nowCurrent)));
		        				nowCurrent += lpData[2].doubleValue();
		        			}
		        		}

		        		nowCurrent = currentCh[3].doubleValue();

		        		for(int i=idxOf0am+1; i<lp.length; i++){
		        			if(((String)lp[i].getDate()+""+(String)lp[i].getTime()).equals(TimeUtil.getPreHour(currDateTime, (i-idxOf0am)))){
		        				Double[] lpData = lp[i].getCh();
		        				nowCurrent -= lpData[2].doubleValue();
		        				lp[i].setCh(4, new Double(dformat.format(nowCurrent)));
		        			}
		        		}

	        		}
	        	}
	        }
	        
	        return lp;
    	}catch(Exception e){
    		log.warn("Get Hourly Data Error=>",e);
    		return null;
    	}        
    }
    
    public HMData[] getCurrentData(){
    	
    	if(current != null){
    		return this.current;
    	}
    	
        if(tmtr_current != null)
            return tmtr_current.getData();
        else
            return null;
    }
    
    public HMData[] getDayData(){
/*
        if(tmtr_day != null){
            log.debug("NURI_Kamstrup601["+tmtr_day.toString());
            return tmtr_day.getData();
        }
        else
            return null;
  */      
    	if(days != null && days.length > 0){
    		return this.days;
    	}
    	days = null;
    	int chCnt = 1;
       	try{
	        if(tmtr_day != null){
	        	days =  tmtr_day.getData();
	        	
	        	if(tmtr_cumm != null){
	        		HMData cumm = tmtr_cumm.getData()[0];
	        		String cummDateTime = (String) cumm.getDate();
	        		log.debug("cummDateTime :"+ cummDateTime);
	        		
	        		boolean hasToday =false;
	        		String preCummDateTime = TimeUtil.getPreDay(cummDateTime).substring(0,8);
	        		
	        		int idxOf0am =-1;
	        		//find the data at cummDateTime
	        		for(int i=0; i<days.length; i++){
	        			if(((String)days[i].getDate().substring(0,8)).equals(preCummDateTime)){
	        				idxOf0am =i;
	        				hasToday = true;
	        				break;
	        			}
	        		}

					if(idxOf0am  < 0){
						for(int i=0; i<days.length; i++){
	        				if(((String)days[i].getDate().substring(0,8)).equals(preCummDateTime)){
	        					idxOf0am =i;
	        					break;
	        				}
	        			}
					}
	        		
	        		log.debug("idxOf0am :"+idxOf0am);
	        		log.debug("hasToday :"+hasToday);
	        
	        		if(idxOf0am>=0){
		        		Double[] currentCh = cumm.getCh();
		        		chCnt = currentCh.length;
		        		double basePulse1 = currentCh[0].doubleValue();
		        		double basePulse2 = 0d;
		        		if(chCnt == 2){
		        			basePulse2 = currentCh[1].doubleValue();
		        		}
		        		log.debug("day basePulse1 :"+basePulse1);
		        		log.debug("day basePulse2 :"+basePulse2);
		        		log.debug("days.length : "+ days.length);
		        		
		        		//if(!hasToday){
		        		//	cummDateTime = preCummDateTime;
		        		//	log.debug("cummDateTime is Predate:"+ cummDateTime);
		        		//}
		        		
		        		for(int i=idxOf0am; i<days.length; i++){
		        			if(i==idxOf0am && ((String)days[i].getDate()).equals(preCummDateTime)){
		        				days[i].setCh(2, new Double(dformat.format(basePulse1)));
		        				if(chCnt ==2 ){
		        					days[i].setCh(4, new Double(dformat.format(basePulse2)));
		        				}
		        			} else{
			        			if(((String)days[i].getDate()).equals(TimeUtil.getPreDay(preCummDateTime, i-idxOf0am).substring(0,8))){
			        				Double[] dayData = days[i].getCh();
									basePulse1 -= dayData[0].doubleValue();
			        				log.debug("basePulse1 :"+basePulse1);
			        				days[i].setCh(2, new Double(dformat.format(basePulse1)));

			        				if(chCnt ==2 &&  dayData.length >=4 ){
										basePulse2 -= dayData[2].doubleValue();
										log.debug("basePulse2 :"+basePulse2);
			        					days[i].setCh(4, new Double(dformat.format(basePulse2)));
			        				}
			        			}else if(((String)days[i].getDate()).equals(TimeUtil.getPreDay(preCummDateTime, idxOf0am-i).substring(0,8))){
			        				Double[] dayData = days[i].getCh();
									basePulse1 += dayData[0].doubleValue();
			        				log.debug("basePulse1 :"+basePulse1);
			        				days[i].setCh(2, new Double(dformat.format(basePulse1)));

			        				if(chCnt ==2 &&  dayData.length >=4 ){
										basePulse2 += dayData[2].doubleValue();
										log.debug("basePulse2 :"+basePulse2);
			        					days[i].setCh(4, new Double(dformat.format(basePulse2)));
			        				}
								}
		        			}
		        		}
	        		}

	        	}
	        }
	        return days;
	    }catch(Exception e){
			log.warn("Get Daily Data Error=>",e);
			return null;
		}        
		
    }
    
    public HMData[] getMonthData(){
    	
    	if(months != null && months.length > 0){
    		return this.months;
    	}
    	months = null;
    	
        if(tmtr_month != null){
            log.debug("NURI_Kamstrup601["+tmtr_month.toString());
            return tmtr_month.getData();
        }
        else
            return null;
    }
    
    public EventLogData[] getEventLog(){
    	if(tmtr_event != null){	    		
    		return tmtr_event.getData();
    	}else{
    		return null;
    	}
    }

    /**
     * get Data
     */
    @SuppressWarnings("unchecked")
    @Override
    public LinkedHashMap getData()
    {
        LinkedHashMap<String, Serializable> res = new LinkedHashMap(16,0.75f,false);
        
        HMData[] lp = null;
        HMData[] current = null;
        HMData[] day = null;
        HMData[] month = null;

        EventLogData[] eventlogdata = null;
        DecimalFormat df3 = TimeLocaleUtil.getDecimalFormat(meter.getSupplier());
        
        try
        {
            lp = getLPHMData();
            current = getCurrentData();
            day = getDayData();
            month = getMonthData();
            eventlogdata = getEventLog();
            
			res.put("<b>[Meter Configuration Data]</b>", "");
            if(tmtr_info != null){
                //res.put("Meter Type",tmtr_info.getMETER_TYPE_NAME());
                res.put("Meter Log",tmtr_info.getMeterCautionFlag().getLog());
                //res.put("Meter Time",tmtr_info.getDateTime());
            }  
            
            if(lp !=null && lp.length>0){
            	res.put("[LP - Hourly Data]", "");
            	for(int i = 0; i < lp.length; i++){
	            	String datetime = lp[i].getDate()+""+lp[i].getTime();
	            	String val = "";
	            	Double[] ch = lp[i].getCh();
	                for(int k = 0; k < ch.length ; k++){
	                    val += "ch"+(k+1)+"="+df3.format(ch[k])+"  ";
	                }
	                res.put("LP"+datetime, val);
            	}
            }
			
			if(month !=null && month.length>0){
				res.put("[LP - Monthly Data]", "");
				for(int i = 0; i < month.length; i++){
	            	String datetime = month[i].getDate();
	            	String val = "";
	            	Double[] ch = month[i].getCh();
	                for(int k = 0; k < ch.length ; k++){
	                    val += "ch"+(k+1)+"="+df3.format(ch[k])+"  ";
	                }
	                res.put("MONTH"+datetime, val);
            	}
			}
			if(day !=null && day.length>0){
				res.put("[Day Data]", "");
            	for(int i = 0; i < day.length; i++){
	            	String datetime = day[i].getDate();
	            	String val = "";
	            	Double[] ch = day[i].getCh();
	                for(int k = 0; k < ch.length ; k++){
	                    val += "ch"+(k+1)+"="+df3.format(ch[k])+"  ";
	                }
	                res.put("Day"+datetime, val);
            	}
			}
			if(current !=null && current.length>0){
				res.put("[Current Data]", "");
            	for(int i = 0; i < current.length; i++){
	            	String datetime = current[i].getDate()+""+current[i].getTime();
	            	this.meterTime = datetime;
	            	String val = "";
	            	Double[] ch = current[i].getCh();
	                for(int k = 0; k < ch.length ; k++){
	                    val += "ch"+(k+1)+"="+df3.format(ch[k])+"  ";
	                }
	                res.put("Current"+datetime, val);
            	}
			}
			if(eventlogdata !=null && eventlogdata.length>0){
				res.put("[EVENT LOG]", "");
				for(int i = 0; i < eventlogdata.length; i++)
	            {
		            res.put(""+eventlogdata[i].getDate()+""+eventlogdata[i].getTime(), ""+eventlogdata[i].getMsg());
	            }
			}
        }
        catch (Exception e)
        {
            log.warn("Get Data Error=>",e);
        }

        return res;
    }
    
    public HashMap<String, String> getMdmData(){
        
        HashMap<String, String> map = null;
        
        try{
            if(mdm != null){
                map = new HashMap<String, String>();
                map.put("mcuType", ""+ModemType.IEIU.name());
                
                if(this.tmtr_mdm.getFW_VER().startsWith("NG")){
                    map.put("protocolType",Protocol.GPRS.name());
                }else{
                    map.put("protocolType",Protocol.CDMA.name());
                }

                map.put("sysPhoneNumber", this.tmtr_mdm.getPHONE_NUM());
                map.put("id", this.tmtr_mdm.getPHONE_NUM());
                map.put("swVersion", this.tmtr_mdm.getFW_VER());
                map.put("networkStatus", "1");
                map.put("csq", this.tmtr_mdm.getCSQ_LEVEL()+"");
                map.put("modemStatus",this.tmtr_mdm.getERROR_STATUS_STRING());
            }
        }catch(Exception e){
            
        }
        return map;
    }

	@Override
	public Double getMeteringValue() {
		// TODO Auto-generated method stub
		return null;
	}

}