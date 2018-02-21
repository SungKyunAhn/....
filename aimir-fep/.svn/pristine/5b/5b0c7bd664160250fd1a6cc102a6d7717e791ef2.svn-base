package com.aimir.fep.meter.parser;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.meter.data.Instrument;
import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.meter.data.TOU_BLOCK;
import com.aimir.fep.meter.parser.Mk6NTable.DateTimeFormat;
import com.aimir.fep.meter.parser.Mk6NTable.Mk6N_BTB;
import com.aimir.fep.meter.parser.Mk6NTable.Mk6N_CB;
import com.aimir.fep.meter.parser.Mk6NTable.Mk6N_EV;
import com.aimir.fep.meter.parser.Mk6NTable.Mk6N_IS;
import com.aimir.fep.meter.parser.Mk6NTable.Mk6N_LP;
import com.aimir.fep.meter.parser.Mk6NTable.Mk6N_MDM;
import com.aimir.fep.meter.parser.Mk6NTable.Mk6N_MTR;
import com.aimir.fep.meter.parser.Mk6NTable.Mk6N_PB;
import com.aimir.fep.meter.parser.Mk6NTable.Mk6N_PQ;
import com.aimir.fep.meter.parser.Mk6NTable.Mk6N_TTB;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Util;
import com.aimir.model.system.Supplier;
import com.aimir.util.TimeLocaleUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * parsing Mk6N Meter Data
 * implemented in Egypt
 *
 * @author kaze (kaze@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2008-08-18 12:00:15 +0900 $,
 */
public class Mk6N extends MeterDataParser implements java.io.Serializable {

	private static final long serialVersionUID = 4846783121864549918L;
	private static Log log = LogFactory.getLog(Mk6N.class);
    private byte[] rawData = null;
    private int lpcount;
    private Double lp = null;
    private Double lpValue = null;
    private String meterId = null;
    private int flag = 0;
    private int resolution =15;

    private byte[] mdm = null;
    private byte[] mtr = null;
    private byte[] pb = null;
    private byte[] btb = null;
    private byte[] cb = null;
    private byte[] ttb = null;
    private byte[] LPD = null;
    private byte[] is = null;
    private byte[] ev = null;
    private byte[] pq = null;

    private Mk6N_MDM mk6n_mdm = null;
    private Mk6N_MTR mk6n_mtr = null;
    private Mk6N_PB mk6n_pb = null;
    private Mk6N_BTB mk6n_btb=null;
    private Mk6N_CB mk6n_cb = null;
    private Mk6N_TTB mk6n_ttb = null;
    private Mk6N_LP mk6n_lp = null;
    private Mk6N_IS mk6n_is = null;
    private Mk6N_EV mk6n_ev = null;
    private Mk6N_PQ mk6n_pq = null;

    final static DecimalFormat dformat = new DecimalFormat("#0.000000");

    /**
     * constructor
     */
    public Mk6N()
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

    public static void main(String args[]){
        System.out.println("test");
        /*
        Mk6N mk6n=new Mk6N();
        try{
            mk6n.parse(Hex.encode("4e47344131003031363737373737373700000000000000871600000000004d54496d003d323030302d3036585800323037373430373532000000002e2e2e2e2e2e2e2e2e2e2e2e2e2e2e2e2e2e2e2e522e432e2e482e2e2e4e442e0f01090b2e020f01090b2e02003f8000003f8000003f8000003f800000030000000000510f01090739170f010908251c54504298040d01091605150177070001e3970001e3930001e39f0001e39b0001e3e70001e3e30001e3ef000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000010160000000010160000000010160000000010160000000010160000000010160000000010160000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000010160000000010160000000010160000000010160000000010160000000010160000000010160000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000010160000000010160000000010160000000010160000000010160000000010160000000010160000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000010160000000010160000000010160000000010160000000010160000000010160000000010160000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000010160000000010160000000010160000000010160000000010160000000010160000000010160000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000010160000000010160000000010160000000010160000000010160000000010160000000010160000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000010160000000010160000000010160000000010160000000010160000000010160000000010160000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000010160000000010160000000010160000000010160000000010160000000010160000000010160000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000010160000000010160000000010160000000010160000000010160000000010160000000010160000000425442130401070001e3970001e3930001e39f0001e39b0001e3e70001e3e30001e3ef40d45ee527428900000000000000000040c002d349e8708040b0ba8d8782478040e1c3ae0cff1f903f66052520000000000000000000000040a34a4274650e0000000000000000004080159b955f08004090d545ddc2d00040b258dd1d6139003f7605252000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000040d45ee527428900000000000000000040c002d349e8708040b0ba8d8782478040e1c3ae0cff1f903f66052520000000000000000000000040a34a4274650e0000000000000000004080159b955f08004090d545ddc2d00040b258dd1d6139003f7605252000000000000000000000004c504434018403000003000e00040f01090a2d00000000050000ffff0002484e0000003f8000000311000000044f580002203ab029290311000100044f590006203ab029290311000200044f58000a203ab029294578706f727420576820546f74616c30303030303030303030303030303030303030303030303030303030303030303030304578706f7274207661726820546f74616c3030303030303030303030303030303030303030303030303030303030303030304578706f727420576820546f74616c303030303030303030303030303030303030303030303030303030303030303030303000000005000800000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000004953545900bc0679003d0657404247d0184358e3474358cbbb4358da1d000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000454c44b000020000ffff0002484e00000001fc690004544e00020000fc680028414e0006000300001887fcd44d6f64656d3a204c6f676f6e3a20557365722045444d49000000000000000000000000000000000000001887fcfa4d6f64656d3a204c6f676f66663a205573657220726571756573740000000000000000000000000000001887fdf84d6f64656d3a204c6f676f6e3a20557365722045444d49000000000000000000000000000000000050514d650000000000000000000000000042c8000042c8000042c80000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000"),1);
        }catch(Exception e){
            e.printStackTrace();
        }
        */
    }

    /**
     * parseing Energy Meter Data of Mk6N Meter
     * @param data stream of result command
     */
    public void parse(byte[] data) throws Exception
    {
    	int offset = 0;
        int LEN_MDM_INFO = 30;

        String resetTime="";
		int currentSeason = 0;
		int resetCnt = 0;
		int channelCnt = 0;
		int activeChannelCnt = 0;
    	int reActiveChannelCnt = 0;
        log.error("[TOTAL] len=["+data.length+"] data=>"+Util.getHexString(data));
        if (data.length < 139) {

            if(data.length == 30)
            {
                byte[] b = new byte[LEN_MDM_INFO];
                System.arraycopy(data,offset,b,0,LEN_MDM_INFO);
                mdm = b;
                log.info("[Mk6N_MDM] len=["+LEN_MDM_INFO+"] data=>"+Util.getHexString(mdm));
                mk6n_mdm = new Mk6N_MDM(mdm);
                log.info(mk6n_mdm);
            }
            else
            {
                log.error("[Mk6N] Data total length[" + data.length + "] is invalid");
            }

            return;
        }

        byte[] b = new byte[LEN_MDM_INFO];
        System.arraycopy(data,offset,b,0,LEN_MDM_INFO);
        mdm = b;
        offset += LEN_MDM_INFO;
        //log.info("[Mk6N_MDM] len=["+LEN_MDM_INFO+"] data=>"+Util.getHexString(mdm));

        String tbName = new String(data,offset,3);
        int totlen = data.length;
    	int len=0;
        while(offset < totlen){
            tbName = new String(data,offset,3);
            offset += 3;
            len = 0;//msb ->lsb
            //get Field Length
            len = DataFormat.hex2unsigned16(
                      DataFormat.LSB2MSB(
                          DataFormat.select(data,offset,2)));
            offset += 2;
            len -= 5;

            b = new byte[len];
            System.arraycopy(data,offset,b,0,len);

            if(tbName.equals("MTI"))
            {
                mtr = b;
                //log.info("[Mk6N_MTR] len=["+len+"] data=>"+Util.getHexString(mtr));
            }
            //Privious Billing
            else if(tbName.equals("TPB"))
            {
            	byte[] temp = b;

            	final int OFS_RESET_TIME=0;
            	final int OFS_CURRENT_SEASON=6;
            	final int OFS_RESET_CNT=7;
            	final int OFS_CHANNEL_CNT=8;

                final int LEN_RESET_TIME=6;
            	final int LEN_CURRENT_SEASON=1;
            	final int LEN_RESET_CNT=1;
            	final int LEN_CHANNEL_CNT=1;

            	final int ACTIVE_CHANNEL_REG=151;
            	final int REACTIVE_CHANNEL_REG=155;

            	int tempOffset = 0;
            	resetTime=(new DateTimeFormat(DataFormat.select(temp,OFS_RESET_TIME,LEN_RESET_TIME))).getDateTime();
        		currentSeason=temp[OFS_CURRENT_SEASON];
        		resetCnt=temp[OFS_RESET_CNT];
        		channelCnt=temp[OFS_CHANNEL_CNT];

        		tempOffset=tempOffset+LEN_RESET_TIME+LEN_CURRENT_SEASON+LEN_RESET_CNT+LEN_CHANNEL_CNT;
        		for(int i=0;i<channelCnt;i++){
        			int regDemand=DataFormat.hex2dec(temp, tempOffset+3, 1);
        			if(regDemand==ACTIVE_CHANNEL_REG){
        				activeChannelCnt=i;
        			}else if(regDemand==REACTIVE_CHANNEL_REG){
        				reActiveChannelCnt=i;
        			}
        			tempOffset+=4;
        		}

        		b=new byte[len-tempOffset];
            	System.arraycopy(data,offset+tempOffset,b,0,len-tempOffset);
                pb = b;
                //log.info("[Mk6N_PB] len=["+len+"] offset["+offset+"] tempOffset["+tempOffset+"] offset+tempOffset["+(offset+tempOffset)+"] len-tempOffset["+(len-tempOffset)+"] data=>"+Util.getHexString(b));
            }
            //Current Billing
            else if(tbName.equals("TCB"))
            {
            	byte[] temp = b;
            	final int OFS_CURRENT_SEASON=0;
            	final int OFS_CHANNEL_CNT=1;

            	final int LEN_CURRENT_SEASON=1;
            	final int LEN_CHANNEL_CNT=1;

            	final int ACTIVE_CHANNEL_REG=151;
            	final int REACTIVE_CHANNEL_REG=155;

            	int tempOffset = 0;
        		currentSeason=temp[OFS_CURRENT_SEASON];
        		channelCnt=temp[OFS_CHANNEL_CNT];

        		tempOffset=tempOffset+LEN_CURRENT_SEASON+LEN_CHANNEL_CNT;
        		for(int i=0;i<channelCnt;i++){
        			int regDemand=DataFormat.hex2dec(temp, tempOffset+3, 1);
        			if(regDemand==ACTIVE_CHANNEL_REG){
        				activeChannelCnt=i;
        			}else if(regDemand==REACTIVE_CHANNEL_REG){
        				reActiveChannelCnt=i;
        			}
        			tempOffset+=4;
        		}

        		b=new byte[len-tempOffset];
            	System.arraycopy(data,offset+tempOffset,b,0,len-tempOffset);
                cb = b;
                //log.info("[Mk6N_CB] len=["+len+"] offset["+offset+"] tempOffset["+tempOffset+"] offset+tempOffset["+(offset+tempOffset)+"] len-tempOffset["+(len-tempOffset)+"] data=>"+Util.getHexString(b));
            }
            //Billing Total
            else if(tbName.equals("BTB"))
            {
                byte[] temp = b;
                final int OFS_CURRENT_SEASON=0;
                final int OFS_CHANNEL_CNT=1;

                final int LEN_CURRENT_SEASON=1;
                final int LEN_CHANNEL_CNT=1;

                final int ACTIVE_CHANNEL_REG=239;//Total fund export Wh (table 3-22)
                final int REACTIVE_CHANNEL_REG=159;//Total export varh (table 3-22)

                int tempOffset = 0;
                currentSeason=temp[OFS_CURRENT_SEASON];
                channelCnt=temp[OFS_CHANNEL_CNT];

                tempOffset=tempOffset+LEN_CURRENT_SEASON+LEN_CHANNEL_CNT;
                for(int i=0;i<channelCnt;i++){
                    int regDemand=DataFormat.hex2dec(temp, tempOffset+3, 1);
                    if(regDemand==ACTIVE_CHANNEL_REG){
                        activeChannelCnt=i;
                    }else if(regDemand==REACTIVE_CHANNEL_REG){
                        reActiveChannelCnt=i;
                    }
                    tempOffset+=4;
                }

                b=new byte[len-tempOffset];
                System.arraycopy(data,offset+tempOffset,b,0,len-tempOffset);
                btb = b;
                //log.info("[Mk6N_BTB] len=["+len+"] offset["+offset+"] tempOffset["+tempOffset+"] offset+tempOffset["+(offset+tempOffset)+"] len-tempOffset["+(len-tempOffset)+"] data=>"+Util.getHexString(b));
            }
            //Total
            else if(tbName.equals("TTB"))
            {
                byte[] temp = b;
                final int OFS_CURRENT_SEASON=0;
                final int OFS_CHANNEL_CNT=1;

                final int LEN_CURRENT_SEASON=1;
                final int LEN_CHANNEL_CNT=1;

                final int ACTIVE_CHANNEL_REG=239;//Total fund export Wh (table 3-22)
                final int REACTIVE_CHANNEL_REG=159;//Total export varh (table 3-22)

                int tempOffset = 0;
                currentSeason=temp[OFS_CURRENT_SEASON];
                channelCnt=temp[OFS_CHANNEL_CNT];

                tempOffset=tempOffset+LEN_CURRENT_SEASON+LEN_CHANNEL_CNT;
                for(int i=0;i<channelCnt;i++){
                    int regDemand=DataFormat.hex2dec(temp, tempOffset+3, 1);
                    if(regDemand==ACTIVE_CHANNEL_REG){
                        activeChannelCnt=i;
                    }else if(regDemand==REACTIVE_CHANNEL_REG){
                        reActiveChannelCnt=i;
                    }
                    tempOffset+=4;
                }

                b=new byte[len-tempOffset];
                System.arraycopy(data,offset+tempOffset,b,0,len-tempOffset);
                ttb = b;
                //log.info("[Mk6N_TTB] len=["+len+"] offset["+offset+"] tempOffset["+tempOffset+"] offset+tempOffset["+(offset+tempOffset)+"] len-tempOffset["+(len-tempOffset)+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("LPD"))
            {
                LPD = b;
                //log.info("[Mk6N_LP] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("IST"))
            {
                is = b;
                //log.info("[Mk6N_IS] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("ELD"))
            {
                ev = b;
                //log.info("[Mk6N_EV] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("PQM"))
            {
                pq = b;
                //log.info("[Mk6N_PQ] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else
            {
                log.info("unknown table=>"+tbName);
            }
            offset += len;
        }

        mk6n_mdm = new Mk6N_MDM(mdm);
        mk6n_mtr = new Mk6N_MTR(mtr);

        if(mtr != null){
            this.meterId = mk6n_mtr.getMETER_ID();
            this.meterTime = mk6n_mtr.getCURR_DATE_TIME();
        }
        if(pb != null){
        	mk6n_pb = new Mk6N_PB("TPB",meterId, pb,resetTime, currentSeason, resetCnt, channelCnt, activeChannelCnt, reActiveChannelCnt);
        }
        if(cb!= null){
        	mk6n_cb = new Mk6N_CB("TCB",meterId, cb,currentSeason, channelCnt, activeChannelCnt, reActiveChannelCnt);
        }

        if(btb!= null){
            mk6n_btb = new Mk6N_BTB("BTB",meterId, mk6n_pb.getTOU_BLOCK() , btb,resetTime, currentSeason, resetCnt, channelCnt, activeChannelCnt, reActiveChannelCnt);
        }
        if(ttb!= null){
            mk6n_ttb = new Mk6N_TTB("TTB",meterId, mk6n_cb.getTOU_BLOCK(), ttb,currentSeason, channelCnt, activeChannelCnt, reActiveChannelCnt);
        }

        if(LPD != null){
        	mk6n_lp = new Mk6N_LP(LPD);
        }

        if(is != null){
        	mk6n_is = new Mk6N_IS(is);
        }
        if(ev != null){
        	mk6n_ev = new Mk6N_EV(ev,meterId);
        }

        if(pq != null){
        	mk6n_pq = new Mk6N_PQ(pq);
        }
        log.info("NURI_Mk6N Data Parse Finished :: DATA["+toString()+"]");
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


    public HashMap<String, String> getMdmData(){

        HashMap<String, String> map = null;

        try{
            if(mdm != null){
                map = new HashMap<String, String>();
                map.put("mcuType",""+ ModemType.MMIU.name());

                if(this.mk6n_mdm.getFW_VER().startsWith("NG")){
                    map.put("protocolType","2");
                }else{
                    map.put("protocolType","1");
                }

                map.put("sysPhoneNumber", this.mk6n_mdm.getPHONE_NUM());
                map.put("id", this.mk6n_mdm.getPHONE_NUM());
                map.put("swVersion", this.mk6n_mdm.getFW_VER());
                map.put("networkStatus", "1");
                map.put("csq", this.mk6n_mdm.getCSQ_LEVEL()+"");
            }
        }catch(Exception e){

        }
        return map;
    }

    public TOU_BLOCK[] getPrevBilling(){

    	if(pb != null){
            try{
                TOU_BLOCK[] blk = mk6n_pb.getTOU_BLOCK();
                return blk;
            }catch(Exception e){
                log.error("prev error",e);
                return null;
            }
        }
    	else
    		return null;
    }
    public TOU_BLOCK[] getBillingTotalBilling(){

        if(btb != null){
            try{
                TOU_BLOCK[] blk = mk6n_btb.getTOU_BLOCK();
                return blk;
            }catch(Exception e){
                log.error("Billing Total error",e);
                return null;
            }
        }
        else
            return null;
    }

    public TOU_BLOCK[] getCurrBilling(){
    	if(cb != null){
            try{
                TOU_BLOCK[] blk = mk6n_cb.getTOU_BLOCK();
                return blk;
            }catch(Exception e){
                log.error("curr error",e);
                return null;
            }
        }
    	else
    		return null;
    }

    public TOU_BLOCK[] getTotalBilling(){
        if(ttb != null){
            try{
                TOU_BLOCK[] blk = mk6n_ttb.getTOU_BLOCK();
                return blk;
            }catch(Exception e){
                log.error("ttb error",e);
                return null;
            }
        }
        else
            return null;
    }


    public LPData[] getLPData(){

        try{
            if(LPD != null)
                return mk6n_lp.lpDataSet;
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
                 resolution = mk6n_lp.getInterval();
            }
        } catch(Exception e){
        }
        return resolution;
    }

    public String getTimeDiff() throws Exception {

        long systime = System.currentTimeMillis();
        long metertime = Util.getMilliTimes(getMeterTime());

        return (int)((systime-metertime)/1000)+"";
    }

    public String getLPChannelMap(){
        String res ="";
        /*
        try{
            if(LPD != null){
                res = mk6n_lp.getChannelMap();
            }
        }catch(Exception e){
            log.warn(e);
        }
        log.debug("channel map : "+ res);
        */

            res+="ch1=Active Energy[kWh],v1=Active Power[kW],";
            res+="ch2=Reactive Energy[kVarh],v2=Reactive Power[kVar],";
            res+="ch3=Apparent Energy[kVAh],v3=Apparent Power[kVA],";
            res+="pf=PF";
        return res;

    }

    public int getDstApplyOn() throws Exception
    {
        int dst=0;
        if(mk6n_mtr!= null){
            if(mk6n_mtr.getDST_ACTIVE()){
                dst=1;
            }
            return dst;
        }else{
            return dst;
        }

    }

    public int getLPChannelCount(){
    	int channelCnt=0;
    	try{
            if(LPD != null){
            	channelCnt= mk6n_lp.getChannelCnt()*2+1;
            }
            else{
            	channelCnt= 7;
            }
        } catch(Exception e){
        }
		return channelCnt;
    }

    public static Double dformat(Double n){
    	if(n==null)
    		return 0d;
    	return new Double(dformat.format(n));
    }

    public Instrument[] getInstrument(){

        Instrument[] insts = new Instrument[1];
        try {

            if(is != null)
            {
                log.debug(mk6n_is);
                insts[0] = new Instrument();
                insts[0].setLINE_FREQUENCY(dformat(mk6n_is.getFREQUENCY()));
                insts[0].setVOL_A(dformat(mk6n_is.getPH_A_VOLTAGE()));
                insts[0].setVOL_B(dformat(mk6n_is.getPH_B_VOLTAGE()));
                insts[0].setVOL_C(dformat(mk6n_is.getPH_C_VOLTAGE()));
                insts[0].setCURR_A(dformat(mk6n_is.getPH_A_CURRENT()));
                insts[0].setCURR_B(dformat(mk6n_is.getPH_B_CURRENT()));
                insts[0].setCURR_C(dformat(mk6n_is.getPH_C_CURRENT()));
                insts[0].setVOL_ANGLE_A(dformat(mk6n_is.getPH_A_ANGLE()));
                insts[0].setVOL_ANGLE_B(dformat(mk6n_is.getPH_B_ANGLE()));
                insts[0].setVOL_ANGLE_C(dformat(mk6n_is.getPH_C_ANGLE()));
                insts[0].setKW_A(dformat(mk6n_is.getPH_A_WATT()));
                insts[0].setKW_B(dformat(mk6n_is.getPH_B_WATT()));
                insts[0].setKW_C(dformat(mk6n_is.getPH_C_WATT()));
                insts[0].setKVAR_A(dformat(mk6n_is.getPH_A_VAR()));
                insts[0].setKVAR_B(dformat(mk6n_is.getPH_B_VAR()));
                insts[0].setKVAR_C(dformat(mk6n_is.getPH_C_VAR()));
                insts[0].setKVA_A(dformat(mk6n_is.getPH_A_VA()));
                insts[0].setKVA_B(dformat(mk6n_is.getPH_B_VA()));
                insts[0].setKVA_C(dformat(mk6n_is.getPH_C_VA()));
                if(pq != null){
                	insts[0].setPH_FUND_VOL_A(dformat(mk6n_pq.getPH_A_FUND_VOLTAGE()));
                	insts[0].setPH_FUND_VOL_B(dformat(mk6n_pq.getPH_B_FUND_VOLTAGE()));
                	insts[0].setPH_FUND_VOL_C(dformat(mk6n_pq.getPH_C_FUND_VOLTAGE()));
                	insts[0].setPH_VOL_PQM_A(dformat(mk6n_pq.getPH_A_VOLTAGE_PQM()));
                	insts[0].setPH_VOL_PQM_B(dformat(mk6n_pq.getPH_B_VOLTAGE_PQM()));
                	insts[0].setPH_VOL_PQM_C(dformat(mk6n_pq.getPH_C_VOLTAGE_PQM()));
                	insts[0].setVOL_SEQ_Z(dformat(mk6n_pq.getVOLTAGE_Z_SEQ()));
                	insts[0].setVOL_SEQ_P(dformat(mk6n_pq.getVOLTAGE_P_SEQ()));
                	insts[0].setVOL_SEQ_N(dformat(mk6n_pq.getVOLTAGE_N_SEQ()));

                	insts[0].setPH_FUND_CURR_A(dformat(mk6n_pq.getPH_A_FUND_CURRENT()));
                	insts[0].setPH_FUND_CURR_B(dformat(mk6n_pq.getPH_B_FUND_CURRENT()));
                	insts[0].setPH_FUND_CURR_C(dformat(mk6n_pq.getPH_C_FUND_CURRENT()));
                	insts[0].setPH_CURR_PQM_A(dformat(mk6n_pq.getPH_A_CURRENT_PQM()));
                	insts[0].setPH_CURR_PQM_B(dformat(mk6n_pq.getPH_B_CURRENT_PQM()));
                	insts[0].setPH_CURR_PQM_C(dformat(mk6n_pq.getPH_C_CURRENT_PQM()));
                	insts[0].setCURR_SEQ_Z(dformat(mk6n_pq.getCURRENT_Z_SEQ()));
                	insts[0].setCURR_SEQ_P(dformat(mk6n_pq.getCURRENT_P_SEQ()));
                	insts[0].setCURR_SEQ_N(dformat(mk6n_pq.getCURRENT_N_SEQ()));

                	insts[0].setCURR_THD_A(dformat(mk6n_pq.getTHD_PH_A_CURRENT()));
                	insts[0].setCURR_THD_B(dformat(mk6n_pq.getTHD_PH_B_CURRENT()));
                	insts[0].setCURR_THD_C(dformat(mk6n_pq.getTHD_PH_C_CURRENT()));
                	insts[0].setVOL_THD_A(dformat(mk6n_pq.getTHD_PH_A_VOLTAGE()));
                	insts[0].setVOL_THD_B(dformat(mk6n_pq.getTHD_PH_B_VOLTAGE()));
                	insts[0].setVOL_THD_C(dformat(mk6n_pq.getTHD_PH_C_VOLTAGE()));
                }

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
            current_tou_block = getTotalBilling();
            previous_tou_block = getBillingTotalBilling();
            lplist = getLPData();
            evlog = getEventLog();
            inst = getInstrument();
            
            //날짜 포멧팅
			SimpleDateFormat normalDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            
			log.debug("==================Mk6N getData Start====================");
			res.put("<b>[Meter Configuration Data]</b>", "");
	        if(mk6n_mtr != null){
	        	res.put("Meter ID",mk6n_mtr.getMETER_ID()+"");
	        	res.put("Model ID No",mk6n_mtr.getMODEL_ID_NO()+"");
	        	res.put("EZIO STATUS",mk6n_mtr.getEZIO_STATUS()+"");
	        	res.put("CURRENT STATUS",mk6n_mtr.getCURRENT_STATUS()+"");
	        	
	        	Date currDate = normalDateFormat.parse(mk6n_mtr.getCURR_DATE_TIME());
	        	Date dstDate =  normalDateFormat.parse(mk6n_mtr.getDST_DATE_TIME());
	        	
	        	res.put("CURRENT DATETIME",destFormat.format(currDate));
	        	res.put("DST STATUS",destFormat.format(dstDate));
	        	res.put("DST ACTIVE",mk6n_mtr.getDST_ACTIVE()+"");
	        	res.put("CT_RATIO_DIV",mk6n_mtr.getCT_RATIO_DIV()+"");
	            res.put("CT_RATIO_MUL",mk6n_mtr.getCT_RATIO_MUL()+"");
	            res.put("PT_RATIO_DIV",mk6n_mtr.getPT_RATIO_DIV()+"");
	            res.put("PT_RATIO_MUL",mk6n_mtr.getPT_RATIO_MUL()+"");
	            res.put("MEASURE METHOD",mk6n_mtr.getMEASURE_METHOD()+"");
	            res.put("MEASURE OPTION",mk6n_mtr.getMEASURE_OPTION()+"");
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

                lpValue = new Double(df3.format(current_tou_block[1].getSummation(0)));
                res.put("Rate A Active Energy(kWh)"             ,df3.format(current_tou_block[1].getSummation(0))+"");
                res.put("Rate A Active Power Max.Demand(kW)"    ,df3.format(current_tou_block[1].getCurrDemand(0))+"");
                res.put("Rate A Active Power Max.Demand Time"   ,(String)current_tou_block[1].getEventTime(0));
                res.put("Rate A Reactive Energy(kVarh)"          ,df3.format(current_tou_block[1].getSummation(1))+"");
                res.put("Rate A Reactive Power Max.Demand(kW)"    ,df3.format(current_tou_block[1].getCurrDemand(1))+"");
                res.put("Rate A Reactive Power Max.Demand Time"   ,(String)current_tou_block[1].getEventTime(1));

                res.put("Rate B Active Energy(kWh)"             ,df3.format(current_tou_block[2].getSummation(0))+"");
                res.put("Rate B Active Power Max.Demand(kW)"    ,df3.format(current_tou_block[2].getCurrDemand(0))+"");
                res.put("Rate B Active Power Max.Demand Time"   ,(String)current_tou_block[2].getEventTime(0));
                res.put("Rate B Reactive Energy(kVarh)"           ,df3.format(current_tou_block[2].getSummation(1))+"");
                res.put("Rate B Reactive Power Max.Demand(kW)"    ,df3.format(current_tou_block[2].getCurrDemand(1))+"");
                res.put("Rate B Reactive Power Max.Demand Time"   ,(String)current_tou_block[2].getEventTime(1));

                res.put("Rate C Active Energy(kWh)"             ,df3.format(current_tou_block[3].getSummation(0))+"");
                res.put("Rate C Active Power Max.Demand(kW)"    ,df3.format(current_tou_block[3].getCurrDemand(0))+"");
                res.put("Rate C Active Power Max.Demand Time"   ,(String)current_tou_block[3].getEventTime(0));
                res.put("Rate C Reactive Energy(kVarh)"           ,df3.format(current_tou_block[3].getSummation(1))+"");
                res.put("Rate C Reactive Power Max.Demand(kW)"    ,df3.format(current_tou_block[3].getCurrDemand(1))+"");
                res.put("Rate C Reactive Power Max.Demand Time"   ,(String)current_tou_block[3].getEventTime(1));
            }

            if(previous_tou_block != null){
                res.put("<b>[Previous Billing Data]</b>", "");
            	res.put("Billing Date"              ,previous_tou_block[0].getResetTime()+"");
                res.put("Total Active Energy(kWh)"              ,df3.format(previous_tou_block[0].getSummation(0))+"");
                res.put("Total Active Power Max.Demand(kW)"     ,df3.format(previous_tou_block[0].getCurrDemand(0))+"");
                res.put("Total Active Power Max.Demand Time"    ,(String)previous_tou_block[0].getEventTime(0));
                res.put("Total Reactive Energy(kVarh)"          ,df3.format(previous_tou_block[0].getSummation(1))+"");
                res.put("Total Reactive Power Max.Demand(kW)"   ,df3.format(previous_tou_block[0].getCurrDemand(1))+"");
                res.put("Total Reactive Power Max.Demand Time"  ,(String)previous_tou_block[0].getEventTime(1));

                res.put("Rate A Active Energy(kWh)"             ,df3.format(previous_tou_block[1].getSummation(0))+"");
                res.put("Rate A Active Power Max.Demand(kW)"    ,df3.format(previous_tou_block[1].getCurrDemand(0))+"");
                res.put("Rate A Active Power Max.Demand Time"   ,(String)previous_tou_block[1].getEventTime(0));
                res.put("Rate A Reactive Energy(kVarh)"          ,df3.format(previous_tou_block[1].getSummation(1))+"");
                res.put("Rate A Reactive Power Max.Demand(kW)"    ,df3.format(previous_tou_block[1].getCurrDemand(1))+"");
                res.put("Rate A Reactive Power Max.Demand Time"   ,(String)previous_tou_block[1].getEventTime(1));

                res.put("Rate B Active Energy(kWh)"             ,df3.format(previous_tou_block[2].getSummation(0))+"");
                res.put("Rate B Active Power Max.Demand(kW)"    ,df3.format(previous_tou_block[2].getCurrDemand(0))+"");
                res.put("Rate B Active Power Max.Demand Time"   ,(String)previous_tou_block[2].getEventTime(0));
                res.put("Rate B Reactive Energy(kVarh)"           ,df3.format(previous_tou_block[2].getSummation(1))+"");
                res.put("Rate B Reactive Power Max.Demand(kW)"    ,df3.format(previous_tou_block[2].getCurrDemand(1))+"");
                res.put("Rate B Reactive Power Max.Demand Time"   ,(String)previous_tou_block[2].getEventTime(1));

                res.put("Rate C Active Energy(kWh)"             ,df3.format(previous_tou_block[3].getSummation(0))+"");
                res.put("Rate C Active Power Max.Demand(kW)"    ,df3.format(previous_tou_block[3].getCurrDemand(0))+"");
                res.put("Rate C Active Power Max.Demand Time"   ,(String)previous_tou_block[3].getEventTime(0));
                res.put("Rate C Reactive Energy(kVarh)"           ,df3.format(previous_tou_block[3].getSummation(1))+"");
                res.put("Rate C Reactive Power Max.Demand(kW)"    ,df3.format(previous_tou_block[3].getCurrDemand(1))+"");
                res.put("Rate C Reactive Power Max.Demand Time"   ,(String)previous_tou_block[3].getEventTime(1));
            }

            if(evlog != null && evlog.length > 0){
                res.put("[Event Log]", "");
                for(int i = 0; i < evlog.length; i++){
                    String datetime = evlog[i].getDate() + evlog[i].getTime();
                    Date eventDate =  normalDateFormat.parse(datetime);
                    if(!datetime.startsWith("0000") && !datetime.equals("")){
                        res.put("("+i+")"+destFormat.format(eventDate), evlog[i].getMsg());
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
                    res.put("Line Frequency",inst[i].getLINE_FREQUENCY()+"");
                }
            }
            if(lplist != null && lplist.length > 0){
                res.put("[Load Profile Data(kWh)]", "");
                int nbr_chn = 3;//ch1,ch2, ch3
                if(mk6n_lp!= null){
                    nbr_chn = (getLPChannelCount()-1)/2;//TODO FIX HARD CODE
                }

                ArrayList chartData0 = new ArrayList();//time chart
                ArrayList[] chartDatas = new ArrayList[nbr_chn]; //channel chart(ch1,ch2,...)
                for(int k = 0; k < nbr_chn ; k++){
                    chartDatas[k] = new ArrayList();
                }

                //ArrayList lpDataTime = new ArrayList();
                for(int i = 0; i < lplist.length; i++){
                    String datetime = lplist[i].getDatetime();
                    String tempDateTime = lplist[i].getDatetime();
                    String val = "";
                    Double[] ch = lplist[i].getCh();
                    for(int k = 0; k < ch.length ; k++){
                        val += "ch"+(k+1)+"="+df3.format(ch[k])+"  ";
                    }
                    
                    Date lpDate = normalDateFormat.parse(datetime);
                    
                    res.put("LP "+destFormat.format(lpDate), val);

                    chartData0.add(tempDateTime.substring(6,8)
                                  +tempDateTime.substring(8,10)
                                  +tempDateTime.substring(10,12));
                    for(int k = 0; k < ch.length ; k++){
                        chartDatas[k].add(ch[k].doubleValue());
                    }
                    //lpDataTime.add(convertTimeFormat(destFormat,(String)lplist[i].getDatetime()));
                }

                //res.put("chartData0", chartData0);
                //for(int k = 0; k < chartDatas.length ; k++){
                //    res.put("chartData"+(k+1), chartDatas[k]);
                //}
                //res.put("lpDataTime", lpDataTime);
                //res.put("chartDatas", chartDatas);
                res.put("[ChannelCount]", nbr_chn+"");
            }
            res.put("LP Channel Information", getLPChannelMap());



	        log.debug("==================Mk6N getData End====================");
	    }
	    catch (Exception e)
	    {
	        log.warn("Get Data Error=>",e);
	    }
		return res;
	}
    public EventLogData[] getEventLog(){
    	if(ev != null){
    		return mk6n_ev.getEvent();
    	}else{
    		return null;
    	}
    }
	public String getMeterLog(){

        StringBuffer sb = new StringBuffer();
        if(mtr != null){
            try
            {
            	sb.append(mk6n_mtr.getCURRENT_STATUS().getLog());

            }
            catch (Exception e){log.warn("Get Mtr meter log error",e);}
        }

        return sb.toString();
    }

	@Override
	public Double getMeteringValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}
}