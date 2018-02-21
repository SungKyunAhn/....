package com.aimir.fep.meter.parser;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.meter.data.Instrument;
import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.meter.data.PowerAlarmLogData;
import com.aimir.fep.meter.data.TOU_BLOCK;
import com.aimir.fep.meter.parser.Mk10Table.Mk10_CB;
import com.aimir.fep.meter.parser.Mk10Table.Mk10_EV;
import com.aimir.fep.meter.parser.Mk10Table.Mk10_IS;
import com.aimir.fep.meter.parser.Mk10Table.Mk10_LP;
import com.aimir.fep.meter.parser.Mk10Table.Mk10_MTI;
import com.aimir.fep.meter.parser.Mk10Table.Mk10_PB;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Util;
import com.aimir.model.system.Supplier;
import com.aimir.util.TimeLocaleUtil;

public class Mk10 extends MeterDataParser implements java.io.Serializable {


	private static final long serialVersionUID = 4846783121864549918L;
	private static Log log = LogFactory.getLog(Mk10.class);
    private byte[] rawData = null;
    private int lpcount;
    private Double lp = null;
    private Double lpValue = null;
    private String meterId = null;
    private int flag = 0;
    private int resolution =15;
    private static int TABLE_DATA_LEN = 2;
    public static final int LEN_HEADER   = 2;

    private byte[] md = null;
    private byte[] mt = null;
    private byte[] pb = null;
    private byte[] cb = null;
    private byte[] ld = null;
    private byte[] is = null;
    private byte[] ev = null;

    private Mk10_MTI mk10_mti = null;
    private Mk10_PB mk10_pb = null;
	private Mk10_CB mk10_cb = null;
    private Mk10_LP mk10_lp = null;
    private Mk10_IS mk10_is = null;
    private Mk10_EV mk10_ev = null;

    final static DecimalFormat dformat = new DecimalFormat("#0.000000");

    /**
     * constructor
     */
    public Mk10()
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

    /**
     * parseing Energy Meter Data of Mk10 Meter
     * @param data stream of result command
     */
    public void parse(byte[] data) throws Exception
    {
    	int offset = 0;
        String resetTime="";
		int currentSeason = 0;
		int resetCnt = 0;
		int channelCnt = 0;
		int activeChannelCnt = 0;
    	int reActiveChannelCnt = 0;


        String tbName = "";
        int totlen = data.length;
        log.debug("totlen=["+totlen+"]");
    	int len=0;
        byte[] b = new byte[0];
        while(offset < totlen){
            tbName = new String(data,offset,LEN_HEADER);
            offset += LEN_HEADER;
            len = 0;//msb ->lsb
            len = DataFormat.hex2unsigned16((
                          DataFormat.select(data,offset,TABLE_DATA_LEN)));
            offset += TABLE_DATA_LEN;
            log.debug("len=["+len+"]");
            b = new byte[len];
            System.arraycopy(data,offset,b,0,len);
            offset += len;

            if(tbName.equals("MD"))
            {
                md = b;
                log.debug("[MD] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("MT"))
            {
                mt = b;
                mk10_mti = new Mk10_MTI(mt);

                if(mt != null){
                    this.meterId = mk10_mti.getMETER_ID();
                    this.meterTime = mk10_mti.getCURR_DATE_TIME();
                }
                log.info("[Mk10_MT] len=["+len+"] data=>"+Util.getHexString(mt));
            }
            //Privious Billing
            else if(tbName.equals("PB"))
            {
                pb = b;
                log.info("[Mk10_PB] len=["+len+"] data=>"+Util.getHexString(b));
            }
            //Current Billing
            else if(tbName.equals("CB"))
            {
            	cb = b;
                log.info("[Mk10_CB] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("LD"))
            {
                ld = b;
                log.info("[Mk10_LP] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("IS"))
            {
                is = b;
                log.info("[Mk10_IS] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("EL"))
            {
                ev = b;
                log.info("[Mk10_EV] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else
            {
                log.info("unknown table=>"+tbName);
            }
            b = null;
        }


        if(pb != null){
        	mk10_pb = new Mk10_PB(pb);
        }
        
        if(cb != null){
        	mk10_cb = new Mk10_CB(cb);
        }

        if(ld != null){
        	mk10_lp = new Mk10_LP(ld);
        }

        if(is != null){
        	mk10_is = new Mk10_IS(is);
        }
        if(ev != null){
        	mk10_ev = new Mk10_EV(ev,meterId);
        }

        log.info("NURI_Mk10 Data Parse Finished :: DATA["+toString()+"]");
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

    public TOU_BLOCK[] getPrevBilling(){

    	if(pb != null){
            try{
                TOU_BLOCK[] blk = mk10_pb.getTOU_BLOCK();
                return blk;
            }catch(Exception e){
                log.error("prev error",e);
                return null;
            }
        }
    	else
    		return null;
    }

    public TOU_BLOCK[] getCurrBilling(){
    	if(cb != null){
            try{
                TOU_BLOCK[] blk = mk10_cb.getTOU_BLOCK();
                if(blk != null && blk.length > 0){
                	this.lpValue = (Double) blk[0].getSummation(0);
                }
                return blk;
            }catch(Exception e){
                log.error("curr error",e);
                return null;
            }
        }
    	else
    		return null;
    }


    public LPData[] getLPData(){

        try{
            if(ld != null)
                return mk10_lp.lpDataSet;
            else
                return null;
        }catch(Exception e){
            log.error("lp parse error",e);
        }
        return null;
    }

    public int getResolution(){

        try{
            if(lp!= null){
                 resolution = mk10_lp.getInterval();
            }
        } catch(Exception e){
        }
        return resolution;
    }

    public String getSwVersion() {
    	String swVersion = "";
    	if(mt!= null){
    		swVersion = mk10_mti.getSW_VER();
    	}
    	return swVersion;
    }
    
    public String getSwRevision(){
    	String swRevision = "";
    	if(mt!= null){
    		swRevision = mk10_mti.getSW_REV_NUMBER();
    	}
    	return swRevision;
    }


    public String getTimeDiff() throws Exception {

        long systime = System.currentTimeMillis();
        long metertime = Util.getMilliTimes(getMeterTime());

        return (int)((systime-metertime)/1000)+"";
    }


    public int getLPChannelCount(){
    	int channelCnt=0;
    	try{
            if(ld != null){
            	channelCnt= mk10_lp.getChannelCnt();
            }
            else{
            	channelCnt= 7;
            }
        } catch(Exception e){
        }
		return channelCnt;
    }


    public Instrument[] getInstrument(){

        Instrument[] insts = new Instrument[1];
        try {

            if(is != null)
            {
                log.debug(mk10_is);
                insts[0] = new Instrument();

                insts[0].setLINE_FREQUENCY(new Double(mk10_is.getFREQUENCY()));
                insts[0].setVOL_A(new Double(mk10_is.getPH_A_VOLTAGE()));
                insts[0].setVOL_B(new Double(mk10_is.getPH_B_VOLTAGE()));
                insts[0].setVOL_C(new Double(mk10_is.getPH_C_VOLTAGE()));
                insts[0].setCURR_A(new Double(mk10_is.getPH_A_CURRENT()));
                insts[0].setCURR_B(new Double(mk10_is.getPH_B_CURRENT()));
                insts[0].setCURR_C(new Double(mk10_is.getPH_C_CURRENT()));
                insts[0].setVOL_ANGLE_A(new Double(mk10_is.getPH_A_ANGLE()));
                insts[0].setVOL_ANGLE_B(new Double(mk10_is.getPH_B_ANGLE()));
                insts[0].setVOL_ANGLE_C(new Double(mk10_is.getPH_C_ANGLE()));
                insts[0].setKW_A(new Double(mk10_is.getPH_A_WATT()));
                insts[0].setKW_B(new Double(mk10_is.getPH_B_WATT()));
                insts[0].setKW_C(new Double(mk10_is.getPH_C_WATT()));
                insts[0].setKVAR_A(new Double(mk10_is.getPH_A_VAR()));
                insts[0].setKVAR_B(new Double(mk10_is.getPH_B_VAR()));
                insts[0].setKVAR_C(new Double(mk10_is.getPH_C_VAR()));
                insts[0].setKVA_A(new Double(mk10_is.getPH_A_VA()));
                insts[0].setKVA_B(new Double(mk10_is.getPH_B_VA()));
                insts[0].setKVA_C(new Double(mk10_is.getPH_C_VA()));
                insts[0].setPH_FUND_VOL_A(new Double(mk10_is.getPH_A_FUND_VOLTAGE()));
            	insts[0].setPH_FUND_VOL_B(new Double(mk10_is.getPH_B_FUND_VOLTAGE()));
            	insts[0].setPH_FUND_VOL_C(new Double(mk10_is.getPH_C_FUND_VOLTAGE()));
            	insts[0].setPH_VOL_PQM_A(new Double(mk10_is.getPH_A_VOLTAGE_PQM()));
            	insts[0].setPH_VOL_PQM_B(new Double(mk10_is.getPH_B_VOLTAGE_PQM()));
            	insts[0].setPH_VOL_PQM_C(new Double(mk10_is.getPH_C_VOLTAGE_PQM()));
            	insts[0].setVOL_SEQ_Z(new Double(mk10_is.getVOLTAGE_Z_SEQ()));
            	insts[0].setVOL_SEQ_P(new Double(mk10_is.getVOLTAGE_P_SEQ()));
            	insts[0].setVOL_SEQ_N(new Double(mk10_is.getVOLTAGE_N_SEQ()));
            	insts[0].setPH_FUND_VOL_A(new Double(mk10_is.getPH_A_FUND_VOLTAGE()));
            	insts[0].setPH_FUND_VOL_B(new Double(mk10_is.getPH_B_FUND_VOLTAGE()));
            	insts[0].setPH_FUND_VOL_C(new Double(mk10_is.getPH_C_FUND_VOLTAGE()));            	
            	insts[0].setPH_VOL_PQM_A(new Double(mk10_is.getPH_A_VOLTAGE_PQM()));
            	insts[0].setPH_VOL_PQM_B(new Double(mk10_is.getPH_B_VOLTAGE_PQM()));
            	insts[0].setPH_VOL_PQM_C(new Double(mk10_is.getPH_C_VOLTAGE_PQM()));

                return insts;
            }
            else
            {
                return null;
            }
        } catch(Exception e){
            log.warn("transform instrument error: "+e.getMessage());
        }
        return null;
    }

	public LinkedHashMap<String, Serializable> getData() {
		LinkedHashMap<String, Serializable> res = new LinkedHashMap<String, Serializable>(16,0.75f,false);

        TOU_BLOCK[] current_tou_block = null;
        TOU_BLOCK[] previous_tou_block = null;
        LPData[] lplist = null;
        EventLogData[] evlog = null;
        Instrument[] inst = null;

        DecimalFormat df3=null;
		SimpleDateFormat destFormat=null;
		 
		if(meter!=null && meter.getSupplier()!=null){
			Supplier supplier = meter.getSupplier();
			if(supplier !=null){
				String lang = supplier.getLang().getCode_2letter();
				String country = supplier.getCountry().getCode_2letter();
				String datePattern = TimeLocaleUtil.getDateFormat(12, lang, country);
				
				df3 = TimeLocaleUtil.getDecimalFormat(supplier);
				
				destFormat = new SimpleDateFormat(datePattern);
			}
		}else{
			//locail 정보가 없을때는 기본 포멧을 사용한다.
			df3 = new DecimalFormat();
			destFormat = new SimpleDateFormat();
		}
        
		try{
            current_tou_block = getCurrBilling();
            previous_tou_block = getPrevBilling();
            lplist = getLPData();
            evlog = getEventLog();
            inst = getInstrument();

            //날짜 포멧팅
			SimpleDateFormat normalDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            
			log.debug("==================Mk10 getData Start====================");
			res.put("<b>[Meter Configuration Data]</b>", "");
	        if(mk10_mti != null){

	        	res.put("Meter ID",mk10_mti.getMETER_ID()+"");
	        	res.put("Model ID No",mk10_mti.getMODEL_ID_NO()+"");
	        	res.put("SW Ver", mk10_mti.getSW_VER());
	        	res.put("SW Revision", mk10_mti.getSW_REV_NUMBER());
	        	res.put("CURRENT STATUS",mk10_mti.getCURRENT_STATUS().getLog()+"");
	        	res.put("Latched Status", mk10_mti.getLATCHED_STATUS().getLog()+"");
	        	
	        	
	        	
				Date meterDate = normalDateFormat.parse(mk10_mti.getCURR_DATE_TIME());
	        	res.put("CURRENT DATETIME",destFormat.format(meterDate));
	        	
	        	
	        	res.put("CT_RATIO_DIV",df3.format(mk10_mti.getCT_RATIO_DIV())+"");
	            res.put("CT_RATIO_MUL",df3.format(mk10_mti.getCT_RATIO_MUL())+"");
	            res.put("PT_RATIO_DIV",df3.format(mk10_mti.getPT_RATIO_DIV())+"");
	            res.put("PT_RATIO_MUL",df3.format(mk10_mti.getPT_RATIO_MUL())+"");
	            res.put("MEASURE METHOD",mk10_mti.getMEASURE_METHOD()+"");
	            res.put("MEASURE OPTION",mk10_mti.getMEASURE_OPTION()+"");
	            res.put("Number Of Power Up", mk10_mti.getNUM_OF_PWR_UP()+"");
	            
	            Date lastOutTime = normalDateFormat.parse(mk10_mti.getLAST_DT_OUTAGE());
	            Date lastRecovTime = normalDateFormat.parse(mk10_mti.getLAST_DT_RECOVERY());
	            
	            res.put("Last Outage Date Time", destFormat.format(lastOutTime));
	            res.put("Last Outate Recovery Time", destFormat.format(lastRecovTime));

	        }

            if(current_tou_block != null){
                res.put("[Current Billing Data]", "");
                res.put("Billing Date"              ,current_tou_block[0].getResetTime()+"");
                res.put("Total Active Energy(kWh)"              ,df3.format(current_tou_block[0].getSummation(0))+"");
                res.put("Total Active Power Max.Demand(kW)"     ,df3.format(current_tou_block[0].getCurrDemand(0))+"");
                res.put("Total Active Power Max.Demand Time"    ,(String)current_tou_block[0].getEventTime(0));
                res.put("Total Reactive Energy(kVarh)"          ,df3.format(current_tou_block[0].getSummation(1))+"");
                res.put("Total Reactive Power Max.Demand(kW)"   ,df3.format(current_tou_block[0].getCurrDemand(1))+"");
                res.put("Total Reactive Power Max.Demand Time"  ,(String)current_tou_block[0].getEventTime(1));

                lpValue = current_tou_block[1].getSummation(0) != null ? (Double)current_tou_block[1].getSummation(0) : 0d;
                res.put("Rate A Active Energy(kWh)"             ,df3.format(current_tou_block[1].getSummation(0))+"");
                res.put("Rate A Active Power Max.Demand(kW)"    ,df3.format(current_tou_block[1].getCurrDemand(0))+"");
                res.put("Rate A Active Power Max.Demand Time"   ,TimeLocaleUtil.getLocaleDate((String)current_tou_block[1].getEventTime(0)));
                res.put("Rate A Reactive Energy(kVarh)"          ,df3.format(current_tou_block[1].getSummation(1))+"");
                res.put("Rate A Reactive Power Max.Demand(kW)"    ,df3.format(current_tou_block[1].getCurrDemand(1))+"");
                res.put("Rate A Reactive Power Max.Demand Time"   ,TimeLocaleUtil.getLocaleDate((String)current_tou_block[1].getEventTime(1)));

                res.put("Rate B Active Energy(kWh)"             ,df3.format(current_tou_block[2].getSummation(0))+"");
                res.put("Rate B Active Power Max.Demand(kW)"    ,df3.format(current_tou_block[2].getCurrDemand(0))+"");
                res.put("Rate B Active Power Max.Demand Time"   ,TimeLocaleUtil.getLocaleDate((String)current_tou_block[2].getEventTime(0)));
                res.put("Rate B Reactive Energy(kVarh)"           ,df3.format(current_tou_block[2].getSummation(1))+"");
                res.put("Rate B Reactive Power Max.Demand(kW)"    ,df3.format(current_tou_block[2].getCurrDemand(1))+"");
                res.put("Rate B Reactive Power Max.Demand Time"   ,TimeLocaleUtil.getLocaleDate((String)current_tou_block[2].getEventTime(1)));

                res.put("Rate C Active Energy(kWh)"             ,df3.format(current_tou_block[3].getSummation(0))+"");
                res.put("Rate C Active Power Max.Demand(kW)"    ,df3.format(current_tou_block[3].getCurrDemand(0))+"");
                res.put("Rate C Active Power Max.Demand Time"   ,TimeLocaleUtil.getLocaleDate((String)current_tou_block[3].getEventTime(0)));
                res.put("Rate C Reactive Energy(kVarh)"           ,df3.format(current_tou_block[3].getSummation(1))+"");
                res.put("Rate C Reactive Power Max.Demand(kW)"    ,df3.format(current_tou_block[3].getCurrDemand(1))+"");
                res.put("Rate C Reactive Power Max.Demand Time"   ,TimeLocaleUtil.getLocaleDate((String)current_tou_block[3].getEventTime(1)));
            }

            if(previous_tou_block != null){
                res.put("<b>[Previous Billing Data]</b>", "");
            	res.put("Billing Date"              ,previous_tou_block[0].getResetTime()+"");
                res.put("Total Active Energy(kWh)"              ,df3.format(previous_tou_block[0].getSummation(0))+"");
                res.put("Total Active Power Max.Demand(kW)"     ,df3.format(previous_tou_block[0].getCurrDemand(0))+"");
                res.put("Total Active Power Max.Demand Time"    ,TimeLocaleUtil.getLocaleDate((String)previous_tou_block[0].getEventTime(0)));
                res.put("Total Reactive Energy(kVarh)"          ,df3.format(previous_tou_block[0].getSummation(1))+"");
                res.put("Total Reactive Power Max.Demand(kW)"   ,df3.format(previous_tou_block[0].getCurrDemand(1))+"");
                res.put("Total Reactive Power Max.Demand Time"  ,TimeLocaleUtil.getLocaleDate((String)previous_tou_block[0].getEventTime(1)));

                res.put("Rate A Active Energy(kWh)"             ,df3.format(previous_tou_block[1].getSummation(0))+"");
                res.put("Rate A Active Power Max.Demand(kW)"    ,df3.format(previous_tou_block[1].getCurrDemand(0))+"");
                res.put("Rate A Active Power Max.Demand Time"   ,TimeLocaleUtil.getLocaleDate((String)previous_tou_block[1].getEventTime(0)));
                res.put("Rate A Reactive Energy(kVarh)"          ,df3.format(previous_tou_block[1].getSummation(1))+"");
                res.put("Rate A Reactive Power Max.Demand(kW)"    ,df3.format(previous_tou_block[1].getCurrDemand(1))+"");
                res.put("Rate A Reactive Power Max.Demand Time"   ,TimeLocaleUtil.getLocaleDate((String)previous_tou_block[1].getEventTime(1)));

                res.put("Rate B Active Energy(kWh)"             ,df3.format(previous_tou_block[2].getSummation(0))+"");
                res.put("Rate B Active Power Max.Demand(kW)"    ,df3.format(previous_tou_block[2].getCurrDemand(0))+"");
                res.put("Rate B Active Power Max.Demand Time"   ,TimeLocaleUtil.getLocaleDate((String)previous_tou_block[2].getEventTime(0)));
                res.put("Rate B Reactive Energy(kVarh)"           ,df3.format(previous_tou_block[2].getSummation(1))+"");
                res.put("Rate B Reactive Power Max.Demand(kW)"    ,df3.format(previous_tou_block[2].getCurrDemand(1))+"");
                res.put("Rate B Reactive Power Max.Demand Time"   ,TimeLocaleUtil.getLocaleDate((String)previous_tou_block[2].getEventTime(1)));

                res.put("Rate C Active Energy(kWh)"             ,df3.format(previous_tou_block[3].getSummation(0))+"");
                res.put("Rate C Active Power Max.Demand(kW)"    ,df3.format(previous_tou_block[3].getCurrDemand(0))+"");
                res.put("Rate C Active Power Max.Demand Time"   ,TimeLocaleUtil.getLocaleDate((String)previous_tou_block[3].getEventTime(0)));
                res.put("Rate C Reactive Energy(kVarh)"           ,df3.format(previous_tou_block[3].getSummation(1))+"");
                res.put("Rate C Reactive Power Max.Demand(kW)"    ,df3.format(previous_tou_block[3].getCurrDemand(1))+"");
                res.put("Rate C Reactive Power Max.Demand Time"   ,TimeLocaleUtil.getLocaleDate((String)previous_tou_block[3].getEventTime(1)));
            }

            if(evlog != null && evlog.length > 0){
                res.put("[Event Log]", "");
                for(int i = 0; i < evlog.length; i++){
                    String datetime = evlog[i].getDate() + evlog[i].getTime();
                    
                    Date eventDate = normalDateFormat.parse(datetime);
                    
                    if(!datetime.startsWith("0000") && !datetime.equals("")){
                        res.put("("+i+") "+destFormat.format(eventDate), evlog[i].getMsg());
                    }
                }
            }

            if(inst != null && inst.length > 0){
                res.put("[Instrument Data]", "");
                for(int i = 0; i < inst.length; i++){
                    res.put("Voltage(A)",df3.format(inst[i].getVOL_A())+"");
                    res.put("Voltage(B)",df3.format(inst[i].getVOL_B())+"");
                    res.put("Voltage(C)",df3.format(inst[i].getVOL_C())+"");
                    res.put("Current(A)",df3.format(inst[i].getCURR_A())+"");
                    res.put("Current(B)",df3.format(inst[i].getCURR_B())+"");
                    res.put("Current(C)",df3.format(inst[i].getCURR_C())+"");
                    res.put("KW(A)",df3.format(inst[i].getKW_A())+"");
                    res.put("KW(B)",df3.format(inst[i].getKW_B())+"");
                    res.put("KW(C)",df3.format(inst[i].getKW_C())+"");
                    res.put("Phase Angle(A)",df3.format(inst[i].getVOL_ANGLE_A())+"");
                    res.put("Phase Angle(B)",df3.format(inst[i].getVOL_ANGLE_B())+"");
                    res.put("Phase Angle(C)",df3.format(inst[i].getVOL_ANGLE_C())+"");
                    res.put("KVAR(A)",df3.format(inst[i].getKVAR_A())+"");
                    res.put("KVAR(B)",df3.format(inst[i].getKVAR_B())+"");
                    res.put("KVAR(C)",df3.format(inst[i].getKVAR_C())+"");
                    res.put("KVA(A)",df3.format(inst[i].getKVA_A())+"");
                    res.put("KVA(B)",df3.format(inst[i].getKVA_B())+"");
                    res.put("KVA(C)",df3.format(inst[i].getKVA_C())+"");
                    res.put("Line Frequency",df3.format(inst[i].getLINE_FREQUENCY())+"");
                    res.put("PH_A_FUND_VOLTAGE", df3.format(inst[i].getPH_FUND_VOL_A())+"");
                    res.put("PH_B_FUND_VOLTAGE", df3.format(inst[i].getPH_FUND_VOL_B())+"");
                    res.put("PH_C_FUND_VOLTAGE", df3.format(inst[i].getPH_FUND_VOL_C())+"");
                    res.put("PH_A_VOLTAGE_PQM", df3.format(inst[i].getPH_VOL_PQM_A())+"");
                    res.put("PH_B_VOLTAGE_PQM", df3.format(inst[i].getPH_VOL_PQM_A())+"");
                    res.put("PH_C_VOLTAGE_PQM", df3.format(inst[i].getPH_VOL_PQM_A())+"");
                    res.put("VOLTAGE_Z_SEQ", df3.format(inst[i].getVOL_SEQ_Z())+"");
                    res.put("VOLTAGE_P_SEQ", df3.format(inst[i].getVOL_SEQ_P())+"");
                    res.put("VOLTAGE_N_SEQ", df3.format(inst[i].getVOL_SEQ_N())+"");
                }
            }
            if(lplist != null && lplist.length > 0){
                res.put("[Load Profile Data(kWh)]", "");
                int nbr_chn = 2;//ch1,ch2, ch3
                if(mk10_lp!= null){
                    nbr_chn = getLPChannelCount();//TODO FIX HARD CODE
                }

                ArrayList chartData0 = new ArrayList();//time chart
                ArrayList[] chartDatas = new ArrayList[nbr_chn]; //channel chart(ch1,ch2,...)
                for(int k = 0; k < nbr_chn ; k++){
                    chartDatas[k] = new ArrayList();
                }

                ArrayList lpDataTime = new ArrayList();
                for(int i = 0; i < lplist.length; i++){
                    String datetime = lplist[i].getDatetime();
                    String tempDateTime = lplist[i].getDatetime();
                    String val = "";
                    Double[] ch = lplist[i].getCh();
                    for(int k = 0; k < ch.length ; k++){
                        val += "ch"+(k+1)+"="+df3.format(ch[k])+"  ";
                    }
                    
                    Date lpDate = normalDateFormat.parse(datetime);
                    
                    res.put("LP "+destFormat.format(lpDate), val+"");

                    chartData0.add(tempDateTime.substring(6,8)
                                  +tempDateTime.substring(8,10)
                                  +tempDateTime.substring(10,12));
                    for(int k = 0; k < ch.length ; k++){
                        chartDatas[k].add(ch[k].doubleValue());
                    }
                    lpDataTime.add((String)lplist[i].getDatetime());
                }


                res.put("[ChannelCount]", nbr_chn+"");
            }


	        log.debug("==================Mk10 getData End====================");
	    }
	    catch (Exception e)
	    {
	        log.error(e,e);
	    }
		return res;
	}
    public EventLogData[] getEventLog(){
    	if(ev != null){
    		return mk10_ev.getEvent();
    	}else{
    		return null;
    	}
    }
    
    public PowerAlarmLogData[] getPowerAlarmLogData(){
    	if(ev != null){
    		return mk10_ev.getPowerAlarmLog();
    	}else{
    		return null;
    	}
    }
    
    
	public String getMeterLog(){

        StringBuffer sb = new StringBuffer();
        if(mt != null){
            try
            {
            	sb.append(mk10_mti.getCURRENT_STATUS().getLog());

            }
            catch (Exception e){log.warn("Get Mtr meter log error",e);}
        }

        return sb.toString();
    }

	@Override
	public Double getMeteringValue() {
		return this.lpValue;
	}

	@Override
	public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append("Mk10 Meter DATA[");
        sb.append("(meterId=").append(meterId).append("),");
        //sb.append("(meterSerial=").append(meterSerial).append(")");
        sb.append("]\n");

        return sb.toString();
	}
	
	public static Double dformat(Double n){
    	if(n==null)
    		return 0d;
    	return new Double(dformat.format(n));
    }
}
