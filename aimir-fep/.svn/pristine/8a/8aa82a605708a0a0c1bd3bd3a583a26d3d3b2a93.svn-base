package com.aimir.fep.meter.saver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.aimir.constants.CommonConstants.MeteringType;
import com.aimir.fep.meter.AbstractMDSaver;
import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.meter.data.Instrument;
import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.meter.data.MeterTimeSyncData;
import com.aimir.fep.meter.data.TOU_BLOCK;
import com.aimir.fep.meter.entry.IMeasurementData;
import com.aimir.fep.meter.parser.SM110;
import com.aimir.fep.util.FMPProperty;
import com.aimir.model.device.Meter;
import com.aimir.util.DateTimeUtil;

@Service
public class SM110EXgineMDSaver extends AbstractMDSaver {

    @SuppressWarnings("unchecked")
	@Override
	protected boolean save(IMeasurementData md) throws Exception {
        SM110 parser = (SM110)md.getMeterDataParser();
        LPData[] lplist = parser.getLPData();
        
        long diffTime = 0;
        String meterTime = md.getTimeStamp();
        long systemTime = DateTimeUtil.getDateFromYYYYMMDDHHMMSS(meterTime).getTime();
        long limitTime = Long.parseLong(FMPProperty.getProperty("metertime.diff.limit.forcertain")) * 1000;
        boolean isCorrectTime = true;
        
        if(parser.getMeterTime() != null){
            diffTime = systemTime - DateTimeUtil.getDateFromYYYYMMDDHHMMSS((parser).getMeterTime()).getTime();
            if (diffTime < 0) {
                diffTime *= -1;
            }
            if (limitTime < diffTime) {
                isCorrectTime = false;
            }
        }
        
        if(lplist == null){
            log.debug("LPSIZE => 0");
        }
        else
        {
            log.debug("LPSIZE => "+lplist.length);
            String yyyymmdd = lplist[0].getDatetime().substring(0,8);
            String hhmm = lplist[0].getDatetime().substring(8,12);
            String mdevId=parser.getMDevId();
            int hh=new Integer(lplist[0].getDatetime().substring(8,10));
            int mm=new Integer(lplist[0].getDatetime().substring(10,12));
            //int interval=15;//lp 검침 데이터 주기
            int interval = parser.getResolution() != 0? parser.getResolution():15;
            if (interval != parser.getMeter().getLpInterval())
                parser.getMeter().setLpInterval(interval);
            //double basePulse = lplist[0].getBasePulse();                                    			
            //DB에서 lp 시작 일자 까지의 누적값을 뽑아 오기 전에 올라온 Self Read에 있는 해당일의 누적값이 있는지 체크 
			ArrayList<TOU_BLOCK[]> list = parser.getSelfReads();
			Double energyRateTotal=0d;
			double basePulse = lplist[0].getBasePulse();
			
	        for(int i = 0; list!= null && i < list.size(); i++) {
	        	TOU_BLOCK[] tou_day = list.get(i);
	            if(tou_day != null){ 
	            	String billResetTime=tou_day[0].getResetTime().substring(0,8);
	            	log.info("First LP Date["+yyyymmdd+"] Self Read Reset Date["+billResetTime+"]");
	            	if(yyyymmdd.equals(billResetTime)) {
	            		energyRateTotal=(Double)tou_day[0].getSummation(0);//energyratetotal
	            		log.info("Self Read EnergyRateTotal["+energyRateTotal+"]");
	            	}	                	                
	            }
	        }
	        //올라온 Self Read에 해당일의 누적값이 있을 경우 - self read를 db에서 구해오지 않도록 함
	        if(energyRateTotal>0) {
	        	if(hh==0) {
	        		if(mm==0) {
	        			basePulse=energyRateTotal;//0시 0분 lp 부터 들어오면 self read값이 해당 lp의 누적값이다.
	        		}
	        		else {
		        		List<Object> minCummulValuelist=lpEMDao.getMinCummulValueNoSelf(mdevId, yyyymmdd, hh, mm, interval);
		        		if(minCummulValuelist!=null && minCummulValuelist.size()>0) {
							Map<String,Object> minCummulValueMap = (Map<String,Object>)minCummulValuelist.get(0);
							Double minCummulValue=(Double)minCummulValueMap.get("MINCUMMULVALUE");
							log.info("minCummulValue["+minCummulValue+"]");
							basePulse=energyRateTotal+minCummulValue;//0시 xx분 lp이면 self read값에 분 누적값을 더해준다.
		        		}else {
		        			log.error("mdevId["+mdevId+"] yyyymmdd["+yyyymmdd+"] hh["+hh+"] mm["+mm+"] minCummulValue is Null");
		        		}
	        		}
	        	}else {
	        		List<Object> hourCummulValuelist=lpEMDao.getHourCummulValueNoSelf(mdevId, yyyymmdd, hh, mm, interval);
	        		if(hourCummulValuelist!=null && hourCummulValuelist.size()>0) {
						Map<String,Object> hourCummulValueMap = (Map<String,Object>)hourCummulValuelist.get(0);
						Double hourCummulValue=(Double)hourCummulValueMap.get("HOURCUMMULVALUE");
						log.info("hourCummulValue["+hourCummulValue+"]");
						basePulse=energyRateTotal+hourCummulValue;//xx시 xx분이면 self read값에 시간과 분 누적값을 더해준다.
	        		}else {
	        			log.error("mdevId["+mdevId+"] yyyymmdd["+yyyymmdd+"] hh["+hh+"] mm["+mm+"] hourCummulValue is Null");
	        		}
	        	}
	        }
	        //올라온 Self Read에 해당일의 누적값이 없는 경우 - DB에서 해당일의 Self Read 값을 검색한다.
	        else {
	        	List<Object> totalCummulValuelist=lpEMDao.getTotalCummulValue(mdevId, yyyymmdd, hh, mm, interval);
	        	if(totalCummulValuelist!=null && totalCummulValuelist.size()>0) {
					Map<String,Object> totalCummulValueMap = (Map<String,Object>)totalCummulValuelist.get(0);
					if(totalCummulValueMap.get("TOTALCUMMULVALUE")!=null) {
						Double totalCummulValue=(Double)totalCummulValueMap.get("TOTALCUMMULVALUE");
						basePulse=totalCummulValue;
						log.info("TotalCummulValue["+totalCummulValue+"]");
					}
	        	}else {
	        		log.error("mdevId["+mdevId+"] yyyymmdd["+yyyymmdd+"] hh["+hh+"] mm["+mm+"] totalCummulValue is Null");
	        	}
	        }            
            log.info("mdevId["+mdevId+"] yyyymmdd["+yyyymmdd+"] hh["+hh+"] mm["+mm+"] basePulse["+basePulse+"]");
            double[][] lpValues = new double[lplist[0].getCh().length][lplist.length];
            int[] flaglist = new int[lplist.length];
            double[] pflist = new double[lplist.length];
            
            for (int ch = 0; ch < lpValues.length; ch++) {
                for (int lpcnt = 0; lpcnt < lpValues[ch].length; lpcnt++) {
                    lpValues[ch][lpcnt] = lplist[lpcnt].getCh()[ch];
                }
            }
            
            for (int i = 0; i < flaglist.length; i++) {
                flaglist[i] = lplist[i].getFlag();
                pflist[i] = lplist[i].getPF();
            }
            
            // TODO Flag, PF 처리해야 함.
            saveLPData(MeteringType.Normal, yyyymmdd, hhmm, lpValues, flaglist, basePulse,
                    parser.getMeter(), parser.getDeviceType(), parser.getDeviceId(), 
                    parser.getMDevType(), parser.getMDevId());
        }
        
        
        log.debug("####################%%%%%%%%%%%%%%%%%%%%%%%%%%%%parser.getMeteringValue():"+parser.getMeteringValue());
        if( parser.getMeteringValue()!= null){
            saveMeteringData(MeteringType.Normal, md.getTimeStamp().substring(0,8),
                    md.getTimeStamp().substring(8, 14), parser.getMeteringValue(),
                    parser.getMeter(), parser.getDeviceType(), parser.getDeviceId(),
                    parser.getMDevType(), parser.getMDevId(), parser.getMeterTime());
            
            saveExgine(parser.getMeter(),parser.getMeteringValue(),md.getTimeStamp().substring(0,14));
        }

        
        // 순시값 (Voltage,Current) 데이터 저장
        Instrument[] instrument = parser.getInstrument();
        if(instrument != null){
            savePowerQuality(parser.getMeter(), parser.getMeterTime(), instrument,
            		parser.getDeviceType(), parser.getDeviceId(), parser.getMDevType(), parser.getMDevId());
        }
        
        // 현재 시점 검침 사용량 및 Demand Power
        TOU_BLOCK[] tou_curr = parser.getCurrBilling(); 
        if(tou_curr != null){ 
        	saveCurrentBilling(tou_curr, parser.getMeter(), 
            		parser.getDeviceType(), parser.getDeviceId(), parser.getMDevType(), parser.getMDevId());
        }
        
        // 월별 TOU DATA저장
        TOU_BLOCK[] tou_month = parser.getPrevBilling();
        if(tou_month != null){ 
            saveMonthlyBilling(tou_month, parser.getMeter(), 
            		parser.getDeviceType(), parser.getDeviceId(), parser.getMDevType(), parser.getMDevId());
        }
        // 셀프 리드 테이블 저장 (일별 정기 검침 누적 사용량)
        ArrayList<TOU_BLOCK[]> list = parser.getSelfReads();
        for(int i = 0; list!= null && i < list.size(); i++) {
        	TOU_BLOCK[] tou_day = list.get(i);
            if(tou_day != null){ 
                saveDayBilling(tou_day, parser.getMeter(), 
                		parser.getDeviceType(), parser.getDeviceId(), parser.getMDevType(), parser.getMDevId());
            }
        }
        
        //미터 타임싱크 로그 저장 (집중기로 부터 자동 타임싱크 한 이력)
        MeterTimeSyncData meterTimeSyncData = parser.getMeterTimeSync();
        if(meterTimeSyncData != null){
        	saveMeterTimeSyncLog(parser.getMeter(), meterTimeSyncData);
        }
        
        //Meter Event Log저장
        EventLogData[] meterEventLog = parser.getEventLog();
        if(meterEventLog != null){
        	saveMeterEventLog(parser.getMeter(), meterEventLog);
        }

        //Meter Status(Warning, Error)정보를 이벤트 화 하여 미터 이벤트 로그로 저장
        EventLogData[] meterStatusLog = parser.getMeterStatusLog();
        if( meterStatusLog != null){
        	saveMeterEventLog(parser.getMeter(),  meterStatusLog);
        }
        return true;
    }
    
    protected boolean saveExgine(Meter meter,Double meteringValue,String time) throws Exception {
       
       
        StringBuffer sb = new StringBuffer();
        sb.append("exgine_metering");
        if (meter!=null && meter.getContract() != null) {
        	sb.append("^customerNo:"+meter.getContract().getCustomer().getCustomerNo());
        	sb.append("^customerAddress:"+meter.getContract().getCustomer().getAddress());
        	sb.append("^customerName:"+meter.getContract().getCustomer().getName());
 	     }
        
        String dsttime = DateTimeUtil.getDST(null, time);
      
        if (meter!=null ) {
        	sb.append("^meterId:"+meter.getMdsId());
        	sb.append("^meterType:"+meter.getMeterType().getName());
        	sb.append("^meteringTime:"+dsttime);
        	sb.append("^meteringValue:"+ meteringValue);
 	    }
        
        if (meter!=null && meter.getModem() != null) {
        	sb.append("^modemId:"+meter.getModem().getDeviceSerial());
        	sb.append("^modemAddress:"+meter.getModem().getAddress()+"^");
        
 	    }
        log.debug("####################%%%%%%%%%%%%%%%%%%%%%%%%%%%%saveExgine sb.toString():"+sb.toString());
        return true;
       
    }

}
