package com.aimir.fep.meter.parser;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.constants.CommonConstants;
import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.meter.data.TOU_BLOCK;
import com.aimir.fep.meter.parser.amuLsrwRs232Table.LSRWRS232_ERROR;
import com.aimir.fep.meter.parser.amuLsrwRs232Table.LSRWRS232_INFO;
import com.aimir.fep.meter.parser.amuLsrwRs232Table.LSRWRS232_LP;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Util;
import com.aimir.model.system.Supplier;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeLocaleUtil;

/**
 * parsing AMU LSRW_RS232 Meter Data
 * 참고 Source nuri.aimir.service.hdm.em.parser.LGRW3410
 * @author  : taeho Park
 * @version : $REV $1, 2010. 3. 3. 오전 10:34:55$
 */
public class AMULSRW_RS232 extends MeterDataParser implements java.io.Serializable {

	private static Log log = LogFactory.getLog(AMULSRW_RS232.class);

	private static final int 	LEN_TYPE_NAME 		= 1;
	private static final int 	LEN_TYPE_LENGTH 	= 2;

	/* implements requisite  member variable */
	private byte[] 				rawData 			= null;
	private Double 				lp 					= null;
	private Double 				lpValue 			= null;
	private String 				meterId 			= null;
	private int 				flag 				= 0;

	LSRWRS232_INFO				lsrw_info			= null;
	LSRWRS232_LP				lsrw_lp				= null;
	LSRWRS232_ERROR				lsrw_error			= null;

	private int 				regK 				= 1;
	private double 				billing_ke 			= 0.05;
	private double 				lp_ke 				= 1;
	private int 				LPChannelCount		= 9;
	private String 				sourceAddr			= null;

	/**
	 * constructor
	 */
	public AMULSRW_RS232(){
	}

	/**
	 * constructor
	 * @param mcuId
	 */
	public AMULSRW_RS232(String sourceAddr){
		this.sourceAddr = sourceAddr;
	}

	/**
	 * constructor
	 *
	 * @param rawData
	 * @param pulseConst
	 * @throws Exception
	 */
    public AMULSRW_RS232(byte[] rawData , String sourceAddr) throws Exception{
        this.rawData 	= rawData;
        this.sourceAddr		= sourceAddr;
        try{
        	parse(rawData);
        }catch(Exception e){
        	throw e;
        }
    }
	/**
     * get raw Data
     * @return rawData
     */
    public byte[] getRawData(){
        return rawData;
    }

    /**
     * set raw Data
     * @param rawData
     */
    public void setrawData(byte[] rawData){
    	this.rawData = rawData;
    }

    /**
     * get Meter ID
     * @return rawData
     */
    public String getMeterId() {
		return meterId;
	}

    /**
     * set Meter ID
     * @param meterId
     */
    public void setMeterId(String meterId) {
    	this.meterId = meterId;
	}

    /**
     * get flag
     * @return flag
     */
	public int getFlag() {
		return flag;
	}

	/**
     * set flag
     * @param flag
     */
	public void setFlag(int flag){
		this.flag = flag;
	}

	/**
     * get data length
     * @return length
     */
	public int getLength() {
		if(rawData == null)
            return 0;
        return rawData.length;
	}

	/**
	 * get LP
	 * @return lp
	 */
	public Double getLp() {
		return lp;
	}

	/**
	 * get LP Value
	 * @return lpValue
	 */
	public Double getLpValue() {
		return lpValue;
	}


	/**
	 * get LP Count
	 * @return
	 */
	public int getLPCount(){

		int lpCount = 0;
		try{
			if(lsrw_lp !=null){
				lpCount = lsrw_lp.getLpCount();
			}
		}catch(Exception e){
			log.debug("get LP Count Failed!");
		}

		return lpCount;
    }

	/**
     * parseing Meter Data of LSRW-RS232 Meter
     * @param data stream of result command
     */
	public void parse(byte[] data) throws Exception {

		log.debug("[TOTAL] len=["+data.length+"] data=>"+Util.getHexString(data));
		int pos = 0;
		int loopCnt = 0;
		while( pos < data.length){
			/* ********************************************************************
			 *  Loop는 최대 3회 =>  field가 모두 존재 할 경우 (Information , LP DATA , Error )
			 *  무한 루프를 방지하기 위한 Flag --> loopCnt
			 ******************************************************************** */
			if(loopCnt == 3){
				log.error("Loop Count Over :" + loopCnt);
				break;
			}
			loopCnt++;

			// TYPE NAME
			log.debug("TypeName(Hex) : " + Util.getHexString(DataFormat.select(data, pos, LEN_TYPE_NAME)));
			String TypeName	= new String(DataFormat.select(data, pos, LEN_TYPE_NAME));
			pos += LEN_TYPE_NAME;
			// TYPE LENGTH
			log.debug("TypeLength(Hex) : " + Util.getHexString(DataFormat.select(data, pos, LEN_TYPE_LENGTH)));
			byte[] TypeLength = new byte[LEN_TYPE_LENGTH];
			System.arraycopy(data, pos, TypeLength, 0, LEN_TYPE_LENGTH);
			pos += LEN_TYPE_LENGTH;

			int TypeLen = DataUtil.getIntTo2Byte(TypeLength);

			log.debug("[TYPE : "+TypeName +"]" + "[TYPE LENGTH : "+ TypeLen +"]");
			// field Length > 3
			if(TypeLen  > LEN_TYPE_NAME + LEN_TYPE_LENGTH){

				byte[] iData = new byte[TypeLen-LEN_TYPE_NAME-LEN_TYPE_LENGTH];
				System.arraycopy(data, pos, iData, 0, iData.length);
				pos += iData.length;

				// log.debug("[iData] len=["+(iData.length)+"] data=>"+Util.getHexString(iData));
				log.debug("TypeName[" + TypeName + "]");

				if("I".equals(TypeName)){
					lsrw_info 		= new LSRWRS232_INFO(iData , billing_ke);
					this.meterId 	= this.lsrw_info.getMeterSerial();
					this.meterTime =  lsrw_info.getLsRwPB().getMeterTime();
					//--------------------------------------
					//  AMU는 검침 데이터안에 Meter 시리얼이 들어 있으므로
					// 파서 내부에서 미터를 설정해준다
					//--------------------------------------
					if(meter==null) {
						log.error("AMU Meter["+meterId+"] Dose not Exist in DB, Please Insert Meter Information!!");
						break;
					}else {
						log.debug("AMU Parser Meter is Set");
						setMeter(meter);
					}
				}else if("L".equals(TypeName)){
					lsrw_lp		=	new LSRWRS232_LP(iData , regK , lp_ke);
				}else if("E".equals(TypeName)){
					lsrw_error	= 	new LSRWRS232_ERROR(iData);
				}else{
					log.error(" [AMU LSRW_RS 232] parse Type Nmae is invalid " + TypeName);
				}
			// field Length < 3
			}else{
				log.debug("this Field only exist Type Name and Type Length ");
			}

		}// end of while
	}

	/**
	 * get LP Channel Map
	 * @return
	 */
	public String getLPChannelMap(){

        String res ="";

        res+="ch1=Active Energy[kWh],v1=Active Power[kW],";
        res+="ch2=Lag Reactive Energy[kVarh],v2=Lag Reactive Power[kVar],";
        res+="ch3=Lead Reactive Energy[kVarh],v3=Lead Reactive Power[kVar],";
        res+="ch4=Phase Energy[kVah],v4=Phase Power[kVA],";
        res+="pf=PF";

        return res;

    }

	/**
     * get LP Channel Count
     * @return
     */
    public int getLPChannelCount(){
        return LPChannelCount;//ch1- ch4
    }

    /**
     * set LP Channel Count
     * @param lpCount
     */
    public void setLPChannelCount(int lpChCount){
    	this.LPChannelCount = lpChCount;
    }

    /**
	 * get LP Period (Resolution)
	 * @return
	 */
	 public int getResolution(){

        int period = 15;
        try{
            if(lsrw_lp!= null){
            	period = lsrw_lp.getResolution();
            }
            else{
            	  return period;
            }
        } catch(Exception e){
            log.warn("get Resolution Error ",e);
        }
        return period;
    }

	/**
	 * get Meter Status
	 * @return
	 * @throws Exception
	 */
	public String getMeterStatus()throws Exception {

		if(lsrw_info.getLsRwPB()!= null){
    		try{
    			return lsrw_info.getLsRwPB().getMeterStatus().getLog();
            }catch (Exception e){
            	log.warn("get Meter Status error",e);
            }
    	}
		return "";
    }

	/**
	 * get CT_PT (Billing Data)
	 * 전력량계 변성기 배수
	 * @return
	 * @throws Exception
	 */
	public int getTrans() throws Exception {
		int vt = 1;
    	if(lsrw_info.getLsRwPB() != null){
    		try{
    			vt =  lsrw_info.getLsRwPB().getMeterCtpt();
            }catch (Exception e){
            	log.warn("get trans error",e);
            }
    	}
    	log.debug("getCT_PT() :"+vt);
        return vt;
    }


	/**
	 * get Billing Day
	 * 전력량계 정기 검침일
	 * @return
	 * @throws Exception
	 */
	public String getBillingDay() throws Exception {

		String billday = "";
		if(lsrw_info.getLsRwPB() != null){
			try{
				billday =  ""+lsrw_info.getLsRwPB().getMeterBillingDay();
			} catch (Exception e){log.warn("get BillingDay error",e);}
		}

		return billday;
	}

	/**
	 * get System Status
	 * @return
	 * @throws Exception
	 */
	public String getSystemStatus()throws Exception {

		try{
			if(lsrw_error != null){
				return lsrw_error.getSystemStatus().getLog();
			}else{
                return "";
            }
        }catch (Exception e){
            log.warn("get System Status error",e);
    	}
		return "";
    }

	/**
	 * get LPDATA
	 * @return
	 */
    public LPData[] getLPData(){

        try{
            if(lsrw_lp != null)
                return lsrw_lp.parse();
            else
                return null;
        }catch(Exception e){
            log.error("lp parse error",e);
        }
        return null;
    }

	/**
	 * get Prev Billing
	 * @return
	 */
	public TOU_BLOCK[] getPrevBilling(){

		if(lsrw_info.getLsRwPB() != null){
			try{
				TOU_BLOCK[] blk = lsrw_info.getLsRwPB().getTou_block();
				return blk;
			}catch(Exception e){
				log.error("prev error",e);
				return null;
			}
		}
		else
			return null;
	}

	/**
	 * get MDM Data ( get Information Data )
	 * @return
	 */
	public HashMap<String, String> getMdmData(){

        HashMap<String, String> map = null;

        try{
            if(lsrw_info != null){
                map = new HashMap<String, String>();
                map.put("interfaceCode", CommonConstants.Interface.AMU.name());
                map.put("mcuType", ""+CommonConstants.ModemType.IEIU.name());

                if(this.lsrw_info.getSwVer().startsWith("NG")){
                    map.put("protocolType","2");
                }else{
                    map.put("protocolType","1");
                }

                map.put("sysPhoneNumber", sourceAddr);
                map.put("id", sourceAddr);
                map.put("networkStatus", "1");
                map.put("csq", "");
                map.put("currentTime" , lsrw_info.getCurrentTime().getTimeStamp());
                map.put("modemStatus",  lsrw_info.getLsRwPB().getMeterStatus().getLog());

            }
        }catch(Exception e){
            log.debug("get MDM Data failed");
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
        TOU_BLOCK[] tou_block = null;
        LPData[] lplist = null;

        DecimalFormat df3 = TimeLocaleUtil.getDecimalFormat(meter.getSupplier());

        try
        {
            log.debug("================	AMU LSRW_RS232 getData start()	==================");
            tou_block = getPrevBilling();
            lplist = getLPData();

			res.put("<b>[Meter Configuration Data]</b>", "");
            if(lsrw_info.getLsRwPB() != null){
            	res.put("ProgramNameVer",lsrw_info.getLsRwPB().getProgramName());
                res.put("CT_PT",lsrw_info.getLsRwPB().getMeterCtpt()+"");
                res.put("Billing Day",lsrw_info.getLsRwPB().getMeterBillingDay());
                res.put("Meter ID", lsrw_info.getMeterSerial());
                res.put("Current Meter Time",lsrw_info.getLsRwPB().getMeterTime());
                res.put("MeterStatus",lsrw_info.getLsRwPB().getMeterStatus().getLog());
            }

            if(tou_block != null){
                res.put("<b>[Previous Billing Data]</b>", "");
                res.put("Total Active Energy(kWh)"              ,df3.format(tou_block[0].getSummation(0))+"");
                res.put("Total Reactive Energy(kVarh)"            ,df3.format(tou_block[0].getSummation(1))+"");
                res.put("Total Active Power Max.Demand(kW)"     ,df3.format(tou_block[0].getCurrDemand(0))+"");
                res.put("Total Active Power Max.Demand Time"    ,(String)tou_block[0].getEventTime(0));
                res.put("Total Reactive Power Max.Demand(kVar)"   ,df3.format(tou_block[0].getCurrDemand(1))+"");
                res.put("Total Reactive Power Max.Demand Time"  ,(String)tou_block[0].getEventTime(1));

                lpValue = new Double(df3.format(tou_block[1].getSummation(0)));
                res.put("Rate A Active Energy(kWh)"             ,df3.format(tou_block[1].getSummation(0))+"");
                res.put("Rate A Reactive Energy(kVarh)"           ,df3.format(tou_block[1].getSummation(1))+"");
                res.put("Rate A Active Power Max.Demand(kW)"    ,df3.format(tou_block[1].getCurrDemand(0))+"");
                res.put("Rate A Active Power Max.Demand Time"   ,(String)tou_block[1].getEventTime(0));
                res.put("Rate A Reactive Power Max.Demand(kVar)"  ,df3.format(tou_block[1].getCurrDemand(1))+"");
                res.put("Rate A Reactive Power Max.Demand Time" ,(String)tou_block[1].getEventTime(1));

                res.put("Rate B Active Energy(kWh)"             ,df3.format(tou_block[2].getSummation(0))+"");
                res.put("Rate B Reactive Energy(kVarh)"           ,df3.format(tou_block[2].getSummation(1))+"");
                res.put("Rate B Active Power Max.Demand(kW)"    ,df3.format(tou_block[2].getCurrDemand(0))+"");
                res.put("Rate B Active Power Max.Demand Time"   ,(String)tou_block[2].getEventTime(0));
                res.put("Rate B Reactive Power Max.Demand(kVar)"  ,df3.format(tou_block[2].getCurrDemand(1))+"");
                res.put("Rate B Reactive Power Max.Demand Time" ,(String)tou_block[2].getEventTime(1));

                res.put("Rate C Active Energy(kWh)"             ,df3.format(tou_block[3].getSummation(0))+"");
                res.put("Rate C Reactive Energy(kVarh)"           ,df3.format(tou_block[3].getSummation(1))+"");
                res.put("Rate C Active Power Max.Demand(kW)"    ,df3.format(tou_block[3].getCurrDemand(0))+"");
                res.put("Rate C Active Power Max.Demand Time"   ,(String)tou_block[3].getEventTime(0));
                res.put("Rate C Reactive Power Max.Demand(kVar)"  ,df3.format(tou_block[3].getCurrDemand(1))+"");
                res.put("Rate C Reactive Power Max.Demand Time" ,(String)tou_block[3].getEventTime(1));

            }

            if(lplist != null && lplist.length > 0){
                res.put("<b>[Load Profile Data(kWh)]</b>", "");
                int nbr_chn = 2;//ch1,ch2

                ArrayList chartData0 = new ArrayList();				//time chart
                ArrayList[] chartDatas = new ArrayList[nbr_chn]; 	//channel chart(ch1,ch2,...)
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
                    lpDataTime.add(lplist[i].getDatetime());
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

            log.debug("==================AMU RSLW_RS232 getData End()====================");
        }
        catch (Exception e){
            log.warn("Get Data Error=>",e);
        }
        return res;
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


