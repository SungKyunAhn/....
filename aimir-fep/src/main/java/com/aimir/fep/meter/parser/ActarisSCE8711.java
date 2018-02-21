package com.aimir.fep.meter.parser;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.meter.data.Instrument;
import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.meter.data.PSTNLogData;
import com.aimir.fep.meter.data.TOU_BLOCK;
import com.aimir.fep.meter.parser.actarisSCE8711Table.SCE8711_EV;
import com.aimir.fep.meter.parser.actarisSCE8711Table.SCE8711_IS;
import com.aimir.fep.meter.parser.actarisSCE8711Table.SCE8711_LP;
import com.aimir.fep.meter.parser.actarisSCE8711Table.SCE8711_LP2;
import com.aimir.fep.meter.parser.actarisSCE8711Table.SCE8711_MDM;
import com.aimir.fep.meter.parser.actarisSCE8711Table.SCE8711_MTR;
import com.aimir.fep.meter.parser.pstnErrorTable.PstnCommStat;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Hex;
import com.aimir.fep.util.Util;
import com.aimir.model.system.Supplier;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeLocaleUtil;

/**
 * parsing Actaris SCE8711 Meter Data
 *
 * @author Kang, Soyi
 * @version $Rev: 1 $, $Date: 2009-03-17 12:00:15 +0900 $,
 */
public class ActarisSCE8711 extends MeterDataParser implements java.io.Serializable
{
	private static final long serialVersionUID = 6826513631279609119L;

	private Log log = LogFactory.getLog(ActarisSCE8711.class);
    
    private byte[] rawData = null;
    private int lpcount;
    private Double lp = null;
    private Double lpValue = null;
    private String meterId = null;
    private int flag = 0;    
    private int channelCnt = 6;
    
    private byte[] mdm = null;
    private byte[] mtr = null;
    private byte[] LPD = null;
    private byte[] LPL = null;
    private byte[] ev = null;
    private byte[] is = null;
    private byte[] com = null;
    
    private SCE8711_MDM nuri_mdm = null;
    private SCE8711_MTR nuri_mtr = null;
    private SCE8711_LP nuri_lp = null;
    private SCE8711_LP2 nuri_lpl = null;
    private SCE8711_EV nuri_ev = null;
    private SCE8711_IS nuri_is = null;
    private PstnCommStat nuri_com = null;
    
    private int resolution =5;
    /**
     * constructor
     */
    public ActarisSCE8711()
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
    
    public static void main(String args[]){
    	ActarisSCE8711 acta = new ActarisSCE8711();
    	try {
			acta.parse(Hex.encode("4b50583130003031303436313939363432000000000000000000a51f004b4d54492500000202800000000101000000010001050000000007da0c1e040a321aff8000004c504c3d0706ff1eff1eff20ff20ff20ff200107da0c1effffffffff8000ff00820005000000000000000000000000000a000000000000000000000000000f00000000000000000000000000140000000000000000000000000019000000000000000000000000001e00000000000000000000000000230000000000000000000000000028000000000000000000000000002d0000000000000000000000000032000000000000000000000000003700000000000000000000000001000000000000000000000000000105000000000000000000000000010a000000000000000000000000010f00000000000000000000000001140000000000000000000000000119000000000000000000000000011e00000000000000000000000001230000000000000000000000000128000000000000000000000000012d0000000000000000000000000132000000000000000000000000013700000000000000000000000002000000000000000000000000000205000000000000000000000000020a000000000000000000000000020f00000000000000000000000002140000000000000000000000000219000000000000000000000000021e00000000000000000000000002230000000000000000000000000228000000000000000000000000022d0000000000000000000000000232000000000000000000000000023700000000000000000000000003000000000000000000000000000305000000000000000000000000030a000000000000000000000000030f00000000000000000000000003140000000000000000000000000319000000000000000000000000031e00000000000000000000000003230000000000000000000000000328000000000000000000000000032d0000000000000000000000000332000000000000000000000000033700000000000000000000000004000000000000000000000000000405000000000000000000000000040a000000000000000000000000040f00000000000000000000000004140000000000000000000000000419000000000000000000000000041e00000000000000000000000004230000000000000000000000000428000000000000000000000000042d0000000000000000000000000432000000000000000000000000043700000000000000000000000005000000000000000000000000000505000000000000000000000000050a000000000000000000000000050f00000000000000000000000005140000000000000000000000000519000000000000000000000000051e00000000000000000000000005230000000000000000000000000528000000000000000000000000052d0000000000000000000000000532000000000000000000000000053700000000000000000000000006000000000000000000000000000605000000000000000000000000060a000000000000000000000000060f00000000000000000000000006140000000000000000000000000619000000000000000000000000061e00000000000000000000000006230000000000000000000000000628000000000000000000000000062d0000000000000000000000000632000000000000000000000000063700000000000000000000000007000000000000000000000000000705000000000000000000000000070a000000000000000000000000070f00000000000000000000000007140000000000000000000000000719000000000000000000000000071e00000000000000000000000007230000000000000000000000000728000000000000000000000000072d0000000000000000000000000732000000000000000000000000073700000000000000000000000008000000000000000000000000000805000000000000000000000000080a000000000000000000000000080f00000000000000000000000008140000000000000000000000000819000000000000000000000000081e00000000000000000000000008230000000000000000000000000828000000000000000000000000082d0000000000000000000000000832000000000000000000000000083700000000000000000000000009000000000000000000000000000905000000000000000000000000090a000000000000000000000000090f00000000000000000000000009140000000000000000000000000919000000000000000000000000091e00000000000000000000000009230000000000000000000000000928000000000000000000000000092d000000000000000000000000093200000000000000000000000009370000000000000000000000000a000000000000000000000000000a050000000000000000000000000a0a0000000000000000000000000a0f0000000000000000000000000a140000000000000000000000000a190000000000000000000000000a1e0000000000000000000000000a230000000000000000000000000a280000000000000000000000000a2d0000000000000000000000000a320000000000000000000000004953541100041104110411000000000000"));
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    /**
     * parseing Energy Meter Data of ActarisACE8711 Meter
     * @param data stream of result command
     */
    public void parse(byte[] data) throws Exception
    {
        int offset = 0;
        int LEN_MDM_INFO = 30;
        log.debug("[TOTAL] len=["+data.length+"] data=>"+Util.getHexString(data));
        if (data.length < 31) {

            if(data.length >= 30)
            {
                byte[] b = new byte[LEN_MDM_INFO];
                System.arraycopy(data,offset,b,0,LEN_MDM_INFO);
                mdm = b;
                log.debug("[SCE8711_MDM] len=["+LEN_MDM_INFO+"] data=>"+Util.getHexString(mdm));
                nuri_mdm = new SCE8711_MDM(mdm);
                log.debug(nuri_mdm);
            }
            else
            {
                log.error("[ActarisSCE8711] Data total length[" + data.length + "] is invalid");
            }
            
            return;
        }

        byte[] b = new byte[LEN_MDM_INFO];
        System.arraycopy(data,offset,b,0,LEN_MDM_INFO);
        mdm = b;
        offset += LEN_MDM_INFO;
        log.debug("[SCE8711_MDM] len=["+LEN_MDM_INFO+"] data=>"+Util.getHexString(mdm));
        String tbName = new String(data,offset,3);
        int totlen = data.length;
        int len=0;
        while(offset < totlen){
            tbName = new String(data,offset,3);
            offset += 3;
            len = 0;//msb ->lsb
            len = DataFormat.hex2unsigned16(
                      DataFormat.LSB2MSB(
                          DataFormat.select(data,offset,2)));
            len -= 5;
            offset += 2;
            log.debug("offset=["+offset+"]");
            log.debug("len=["+len+"]");

            b = new byte[len];
            System.arraycopy(data,offset,b,0,len);
            offset += len;

            if(tbName.equals("MTI"))
            {
            	mtr = b;
                log.debug("[NURI_MTI] len=["+len+"] data=>"+Util.getHexString(b));
            	log.info("[NURI_MTI] len=["+len+"]");
            }
            else if(tbName.equals("LPD"))
            {
                LPD = b;
                log.debug("[NURI_LPD] len=["+len+"] data=>"+Util.getHexString(b));
                log.info("[NURI_LPD] len=["+len+"]");
            }
            else if(tbName.equals("LPL"))
            {
                LPL = b;
                log.debug("[NURI_LPL] len=["+len+"] data=>"+Util.getHexString(b));
                log.info("[NURI_LPL] len=["+len+"]");
            }
            else if(tbName.equals("IST"))
            {
                is = b;
                log.debug("[NURI_IST] len=["+len+"] data=>"+Util.getHexString(b));
                log.info("[NURI_IST] len=["+len+"]");
            }
            else if(tbName.equals("ELD"))
            {
                ev = b;
                log.debug("[NURI_EV] len=["+len+"] data=>"+Util.getHexString(b));
                log.info("[NURI_ELD] len=["+len+"]");
            }
            else if(tbName.equals("COM"))
            {
            	com = b;
                log.debug("[NURI_COM] len=["+len+"] data=>"+Util.getHexString(b));
            	log.info("[NURI_COM] len=["+len+"]");
            }
            else 
            {
                log.debug("unknown table=>"+tbName);
            }
        }
        
        nuri_mdm = new SCE8711_MDM(mdm);
        log.debug(nuri_mdm);
        
        
        if(mtr != null){
        	nuri_mtr = new SCE8711_MTR(mtr);
            log.debug(nuri_mtr);
            this.meterId = nuri_mtr.getMETER_ID();
            this.resolution  = nuri_mtr.getLP_PERIOD();
    //        this.regK = nuri_mtr.getREG_K();
        }
        if(ev != null){
            nuri_ev = new SCE8711_EV(ev); 
        }
        if(LPD != null){
            nuri_lp = new SCE8711_LP(LPD, resolution, meter);
        }
        if(LPL != null){
            nuri_lpl = new SCE8711_LP2(LPL, resolution, meter);
        }
        if(is != null){
            nuri_is = new SCE8711_IS(is);
        }
        if(com != null){
        	nuri_com = new PstnCommStat(com);
        }
        log.debug("SCE8711 Data Parse Finished :: DATA["+toString()+"]");
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
       
        sb.append("SCE8711 Meter DATA[");
        sb.append("]\n");

        return sb.toString();
    }
    
    public String getLPChannelMap(){
        String res ="";
      
        return res;
        
    }
    
    public int getLPChannelCount(){
        try{
            if(LPD != null){
            	channelCnt =  nuri_lp.getChannelCnt();
            }
            else if (LPL != null){
            	channelCnt =  nuri_lpl.getChannelCnt();
            }
        } catch(Exception e){
        }
        return channelCnt;//
    }

    public int getResolution(){

        return this.resolution;
    }
    
    public int getCT1(){
    	int ct =0;
    	try{
            if(mtr!= null){
            	ct =  (int)nuri_mtr.getCT1_RATIO().doubleValue();
            }
    	}catch(Exception e){
    		
        }
    	return ct;
    }
    
    public int getVT1(){
    	int vt =0;
    	try{
            if(mtr!= null){
            	vt =  (int)nuri_mtr.getVT1_RATIO().doubleValue();
            }
    	}catch(Exception e){
    		
        }
    	return vt;
    }
    
    public int getCT2(){
    	int ct =0;
    	try{
            if(mtr!= null){
            	ct =  (int)nuri_mtr.getCT2_RATIO().doubleValue();
            }
    	}catch(Exception e){
    		
        }
    	return ct;
    }
    
    public int getVT2(){
    	int vt =0;
    	try{
            if(mtr!= null){
            	vt =  (int)nuri_mtr.getVT2_RATIO().doubleValue();
            }
    	}catch(Exception e){
    		
        }
    	return vt;
    }
    
    public String getMeterLog(){
        
        StringBuffer sb = new StringBuffer();
        if(mtr != null){
            try
            {
       //     	sb.append(nuri_mtr.getMeterErrorFlag().getLog());
            }
            catch (Exception e){log.warn("get meter log error",e);}
        }
        log.debug("getMeterLog ["+sb.toString()+"]");
        return sb.toString();
    }
    
    public String getMeterTime(){
    	  String meterTime = "";
    	 if(mtr != null){
             try
             {
            	 meterTime =  nuri_mtr.getMeterDateTime();
             }catch (Exception e){log.warn("get meterTime error",e);}
    	 }
    	 log.info("getMeterTime() :"+meterTime);
         return meterTime;
    }
    
    /*
     * 
	public BatteryLogData[] getBatteryLog(){		
		return null;
	}
	*/
	
	public PSTNLogData getCommStat(){
    	
        try{
            if(com != null){         
                return nuri_com.getData();
            }else{
                return null;
            }
        }catch(Exception e){
            log.error("comm stat. parse error",e);
        }
        return null;
    }

    public int getMeterStatusCode() {
        return this.nuri_mdm.getERROR_STATUS();
    }
    
    public String getBillingDay() throws Exception {
    	
         return "";
    }
        
    @SuppressWarnings("unchecked")
    public LPData[] getLPData(){
        
        try{
            if(LPD != null)
                return nuri_lp.parse();
            if(LPL != null)
                return nuri_lpl.parse();
            else
                return null;
        }catch(Exception e){
            log.error("lp parse error",e);
        }
        return null;
    }
    
    public TOU_BLOCK[] getPrevBilling(){
    	
    	return null;    	
    }
    
    public TOU_BLOCK[] getCurrBilling(){
    	return null;    	
    }
    
    public Instrument[] getInstrument(){

        Instrument[] insts = new Instrument[1];
        try {

            if(is != null)
            {
                log.debug(nuri_is);
                insts[0] = new Instrument();
                insts[0].setVOL_A(nuri_is.getVOLTAGE_PHA());
                insts[0].setVOL_B(nuri_is.getVOLTAGE_PHB());
                insts[0].setVOL_C(nuri_is.getVOLTAGE_PHC());
                insts[0].setCURR_A(nuri_is.getCURRENT_PHA());
                insts[0].setCURR_B(nuri_is.getCURRENT_PHB());
                insts[0].setCURR_C(nuri_is.getCURRENT_PHC());
                
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
        
    public EventLogData[] getEventLog(){
    	if(ev != null){
    		return nuri_ev.getEvent();
    	}else{
    		return null;
    	}
    }
    
    public HashMap<String, String> getMdmData(){
        
        HashMap<String, String> map = null;
        
        try{
            if(mdm != null){
                map = new HashMap<String, String>();
                map.put("mcuType","5"); //
                
                if(this.nuri_mdm.getFW_VER().startsWith("NG")){
                    map.put("protocolType","2");
                }else{
                    map.put("protocolType","1");
                }

                map.put("sysPhoneNumber", this.nuri_mdm.getPHONE_NUM());
                map.put("id", this.nuri_mdm.getPHONE_NUM());
                map.put("swVersion", this.nuri_mdm.getFW_VER());
                map.put("networkStatus", "1");
                map.put("csq", this.nuri_mdm.getCSQ_LEVEL()+"");
            }
        }catch(Exception e){
            
        }
        return map;
    }
    

    /**
     * get Data
     */
    @Override
    public LinkedHashMap getData()
    {
        LinkedHashMap<String, Serializable> res = new LinkedHashMap(16,0.75f,false);
        LPData[] lplist = null;
        EventLogData[] evlog = null;
        Instrument[] inst = null;
        PSTNLogData commlog = null; 
        
        DecimalFormat df3 = TimeLocaleUtil.getDecimalFormat(meter.getSupplier());

        try
        {
            log.debug("==================LP3410CP_005 getData start()====================");
            lplist = getLPData();
            evlog = getEventLog();
            inst = getInstrument();
            commlog =  getCommStat();
            
			res.put("<b>[Meter Configuration Data]</b>", "");
            if(nuri_mtr != null){          
                res.put("CT1",nuri_mtr.getCT1_RATIO()+"");
                res.put("VT1",nuri_mtr.getVT1_RATIO()+"");
                res.put("CT2",nuri_mtr.getCT2_RATIO()+"");
                res.put("VT2",nuri_mtr.getVT2_RATIO()+"");
                res.put("LP PERIOD",nuri_mtr.getLP_PERIOD()+"");
                res.put("DEMAND FACTOR",nuri_mtr.getDEMAND_FACTOR()+"");
                res.put("ENERGY FACTOR",nuri_mtr.getENERGY_FACTOR()+"");
                res.put("Current Meter Time",nuri_mtr.getMeterDateTime());
           /*     res.put("MeterErrorFlag",nuri_mtr.getMeterErrorFlag().toString());
                res.put("MeterEventFlag",nuri_mtr.getMeterEventFlag().toString());
                res.put("MeterCautionFlag",nuri_mtr.getMeterCautionFlag().toString());
            */
            }
            if(commlog != null){
                res.put("<b>[Comm log Data]</b>", "");    
            
                res.put("PROTOCOL_TYPE =", commlog.getProtocolType());
                res.put("OPERATION_DATETIME =", commlog.getOperationDateTime());
                res.put("PORT_NUMBER =", commlog.getPortNo());
                res.put("CALL_DATETIME =", commlog.getCallDateTime());
                res.put("PHONE_NUMBER =", commlog.getPhoneNumber());
                res.put("CALL_RESULT =", commlog.getCallResult());
                res.put("CALL_RESULT_DATETIME =", commlog.getCallResultDateTime());                
            }
            if(lplist != null && lplist.length > 0){
                res.put("<b>[Load Profile Data(kWh)]</b>", "");
                int nbr_chn = 4;//ch1,ch2
                if(nuri_lp!= null){
                    nbr_chn = 4;//TODO FIX HARD CODE
                }
                ArrayList chartData0 = new ArrayList();//time chart
                ArrayList[] chartDatas = new ArrayList[nbr_chn]; //channel chart(ch1,ch2,...)
                for(int k = 0; k < nbr_chn ; k++){
                    chartDatas[k] = new ArrayList();                    
                }

                DecimalFormat decimalf=null;
                SimpleDateFormat datef14=null;
                ArrayList lpDataTime = new ArrayList();
                for(int i = 0; i < lplist.length; i++){
                    String datetime = lplist[i].getDatetime();

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
                    String date;
                	date = datef14.format(DateTimeUtil.getDateFromYYYYMMDDHHMMSS(datetime+"00"));
                   
                    String tempDateTime = lplist[i].getDatetime();
                    String val = "";
                    Double[] ch = lplist[i].getCh();
                    for(int k = 0; k < ch.length ; k++){
                        val += "<span style='margin-right: 40px;'>ch"+(k+1)+"="+df3.format(ch[k])+"</span>";
                    }
                    res.put("LP"+" "+date, val);

                    chartData0.add(tempDateTime.substring(6,8)
                                  +tempDateTime.substring(8,10)
                                  +tempDateTime.substring(10,12));
                    for(int k = 0; k < ch.length ; k++){                        
                        chartDatas[k].add(ch[k].doubleValue());
                    }
                    lpDataTime.add((String)lplist[i].getDatetime());
                }
                
                //res.put("chartData0", chartData0);
                //for(int k = 0; k < chartDatas.length ; k++){
                //    res.put("chartData"+(k+1), chartDatas[k]);
                //}
                //res.put("lpDataTime", lpDataTime);
                //res.put("chartDatas", chartDatas);
                res.put("[ChannelCount]", nbr_chn+"");
            }
            
            if(evlog != null && evlog.length > 0){
                res.put("<b>[Event Log]</b>", "");
                int idx = 0;
                for(int i = 0; i < evlog.length; i++){
                    String datetime = evlog[i].getDate() + evlog[i].getTime();
                    if(!datetime.startsWith("0000") && !datetime.equals("")){
                        res.put("("+i+")"+datetime+"00", evlog[i].getMsg());
                    }
                }
            }
            
            if(inst != null && inst.length > 0){
                res.put("<b>[Instrument Data]</b>", "");
                for(int i = 0; i < inst.length; i++){
                    res.put("Voltage(A)",df3.format(inst[i].getVOL_A())+"");
                    res.put("Voltage(B)",df3.format(inst[i].getVOL_B())+"");
                    res.put("Voltage(C)",df3.format(inst[i].getVOL_C())+"");
                    res.put("Current(A)",df3.format(inst[i].getCURR_A())+"");
                    res.put("Current(B)",df3.format(inst[i].getCURR_B())+"");
                    res.put("Current(C)",df3.format(inst[i].getCURR_C())+"");
                    res.put("VOLTAGE_ANGLE(A)",df3.format(inst[i].getVOL_ANGLE_A())+"");
                    res.put("VOLTAGE_ANGLE(B)",df3.format(inst[i].getVOL_ANGLE_B())+"");
                    res.put("VOLTAGE_ANGLE(C)",df3.format(inst[i].getVOL_ANGLE_C())+"");
                    res.put("CURRENT_ANGLE(A)",df3.format(inst[i].getCURR_ANGLE_A())+"");
                    res.put("CURRENT_ANGLE(B)",df3.format(inst[i].getCURR_ANGLE_B())+"");
                    res.put("CURRENT_ANGLE(C)",df3.format(inst[i].getCURR_ANGLE_C())+"");
                    res.put("PF(A)",df3.format(inst[i].getPF_A())+"");
                    res.put("PF(B)",df3.format(inst[i].getPF_B())+"");
                    res.put("PF(C)",df3.format(inst[i].getPF_C())+"");
                }
            }
            
            res.put("LP Channel Information", getLPChannelMap());
            
            log.debug("==================LP3410CP_005 getData End()====================");
        }
        catch (Exception e)
        {
            log.warn("Get Data Error=>",e);
        }

        return res;
    }

	/* (non-Javadoc)
	 * @see com.aimir.fep.meter.parser.MeterDataParser#getMeteringValue()
	 */
	@Override
	public Double getMeteringValue() {
		// TODO 기존 kpx 포맷에는 빌링 데이터가 없었다 누적값을 어떻게 넣어줄 것인지 논의 필요
		return 0.0d;
	}
}
