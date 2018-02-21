package com.aimir.fep.meter.saver;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.aimir.constants.CommonConstants.MeteringType;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.meter.AbstractMDSaver;
import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.meter.data.Instrument;
import com.aimir.fep.meter.data.BillingData;
import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.meter.data.PowerAlarmLogData;
import com.aimir.fep.meter.entry.IMeasurementData;
import com.aimir.fep.meter.parser.Kamstrup351B;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.Meter;
import com.aimir.util.Condition;
import com.aimir.util.TimeUtil;


@Service
public class Kamstrup351BMDSaver extends AbstractMDSaver {

    @Override
    protected boolean save(IMeasurementData md) throws Exception {
    	Kamstrup351B parser = (Kamstrup351B)md.getMeterDataParser();
    	LPData[] 	 lplist = parser.getLpData();
    	
    	log.debug("MeterInfo id    "+parser.getMeter().getId());
    	log.debug("MeterInfo MdsId "+parser.getMeter().getMdsId());
    	log.debug("parser.getMDevId() "+parser.getMDevId());
		
        int moduleTime 	= Integer.parseInt(parser.getModuletime().substring(0,10));
        int meterTime 	= Integer.parseInt(parser.getMeterTime().substring(0,10));
        long timeDiff 	= moduleTime - meterTime; //시간차 
        parser.getMeter().setTimeDiff(timeDiff);
    	/**
    	 * 
    	 * Meter INfo 저장
    	 * 
    	 * **/
        log.debug("meterInfoSaver start!");
        meterInfoSave( parser );
        log.debug("meterInfoSaver End!");
        /**
         * 
         * Billing Data 저장
         * 
         * **/
        //TODO BILLING 여기서는 month만 저장된다.

        // 월별 DATA저장
        BillingData lastmonth 	= parser.getBillingData();
        BillingData getLasMonthBillingData 	= parser.getLasMonthBillingData();
        
        BillingData[] 	month 		= 	new BillingData[2];
        int b=0; 
        if(lastmonth != null){
        	month[b++]	=	lastmonth;	
        }
        
        if(getLasMonthBillingData != null){
        	month[b++]	=	getLasMonthBillingData;	
        }
        log.debug("BillingData start!");
        if(month != null){ 
        	try{
        		for(int i=0 ; i< month.length ; i++ ){
        			if( month[i].getActivePowerDemandMaxTimeRateTotal() !=null){
        				saveMonthlyBilling(month[i], parser.getMeter(),parser.getDeviceType(), parser.getDeviceId(), parser.getMDevType(), parser.getMDevId());
        			}
        		}
        	}catch(Exception e){
        		e.printStackTrace();
        		log.debug("bill error "+e.getMessage());
        	}
        }
        log.debug("BillingData End!");
        /**
         * 
         * LP Data저장 
         *
         */        
        log.debug("lpSave start!");
        if(lplist.length > 0){
        	lpSave(md, lplist, parser);
        }
        log.debug("lpSave End!");
        /**
         * 
         * Meter Event Log저장
         * 
         * **/
        EventLogData[] 		meterEventLog = parser.getEventLog();
        PowerAlarmLogData[] powerAlarmLog = parser.getPowerAlarmLogDataList();
        log.debug("EventLogData start!");
        if(meterEventLog != null){
        	parser.getMeter().getModel().setName("351B");
        	saveMeterEventLog(parser.getMeter(),meterEventLog);
        	savePowerAlarmLog(parser.getMeter(),powerAlarmLog);
        }
        log.debug("EventLogData end!");
        return true;
    }
    
    //TODO
    private boolean meterInfoSave(Kamstrup351B parser)throws Exception{
    	// 순시값 (Voltage,Current) 데이터 저장
        Instrument[] instrument = parser.getInstrument();
        String time 		= parser.getMeterTime();
        if(instrument != null){
            savePowerQuality(parser.getMeter(), time, instrument, parser.getDeviceType(), parser.getDeviceId(), parser.getMDevType(), parser.getMDevId());
        }
   		return true;
	}
    
    //TODO lpSave
    private boolean lpSave(IMeasurementData md, LPData[] validlplist,Kamstrup351B parser) {
    	HashSet<Condition> condition= new HashSet<Condition>();
    	HashSet<Condition> condition2= new HashSet<Condition>();
    	HashSet<Condition> condition3= new HashSet<Condition>();
    	HashSet<Condition> condition4= new HashSet<Condition>();
		String yyyymmdd 			= validlplist[0].getDatetime().substring(0,  8);
		String yyyymmddhh 			= validlplist[0].getDatetime().substring(0,  10);
		String hhmm 				= validlplist[0].getDatetime().substring(8,  12);
		String mm 					= validlplist[0].getDatetime().substring(10, 12);
		try {
			
			double basePulse  = parser.getBasePulse1();
			double basePulse2 = parser.getBasePulse2();
			double basePulse3 = parser.getBasePulse3();
			double basePulse4 = parser.getBasePulse4();
			log.debug("yyyymmdd   "+yyyymmdd);
		    log.debug("basePulse "+basePulse);
		    log.debug("basePulse2 "+basePulse2);
		    log.debug("basePulse3 "+basePulse3);
		    log.debug("basePulse4 "+basePulse4);

			double[][] lpValues = new double[validlplist[0].getCh().length][validlplist.length];
			int[] 	   flaglist = new int[validlplist.length];
			
			for (int ch = 0; ch < lpValues.length; ch++) {
				for (int lpcnt = 0; lpcnt < lpValues[ch].length; lpcnt++) {
					// Kw/h 단위로 저장
						lpValues[ch][lpcnt] = validlplist[lpcnt].getCh()[ch];
				}
			}
			for (int i = 0; i < flaglist.length; i++) {
				try{
					flaglist[i] = validlplist[i].getFlag();
				}catch(Exception e){
					e.printStackTrace();
					log.debug("getMessage ="+e.getMessage());
				}
			}

			parser.getMeter().setLpInterval(parser.getInterval());
			// TODO Flag, PF 처리해야 함.
			saveLPData( MeteringType.Normal, 
						yyyymmdd, 
						hhmm, 
						lpValues, 
						flaglist,
						new double[]{basePulse,basePulse2,basePulse3,basePulse4}, 
						parser.getMeter(), 
						parser.getDeviceType(),
						parser.getDeviceId(), 
						parser.getMDevType(), 
						parser.getMDevId() );
			if (parser.getMeteringValue() != null) {
				saveMeteringData(MeteringType.Normal, 
								 md.getTimeStamp().substring(0, 8), 
						         md.getTimeStamp().substring(8, 14),
								 parser.getMeteringValue(), parser.getMeter(), 
								 parser.getDeviceType(), 
								 parser.getDeviceId(), 
								 parser.getMDevType(), 
								 parser.getMDevId(),
								 parser.getMeterTime());
			}
		} catch (Exception e) {
			log.error(e, e);
		}
		return true;
	}
    
	private Double retValue(String mm,Double value_00,Double value_15,Double value_30,Double value_45){
		
		Double retVal_00 = value_00 == null ? 0d:value_00;
		Double retVal_15 = value_15 == null ? 0d:value_15;
		Double retVal_30 = value_30 == null ? 0d:value_30;
		Double retVal_45 = value_45 == null ? 0d:value_45;
		if("15".equals(mm)){
			return retVal_00;
		}
		
		if("30".equals(mm)){
			return retVal_00+retVal_15;
		}
		
		if("45".equals(mm)){
			return retVal_00+retVal_15+retVal_30;
		}
		
		if("00".equals(mm)){
			return retVal_00+retVal_15+retVal_30+retVal_45;
		}
		
	    return retVal_00+retVal_15;
		
	}
	
	public void lpDataTostring(double[][] lpValues){
//		lpValues[] 	 lplistw = getLpData();
		StringBuffer sb 	 = new StringBuffer();
		log.debug("==============================================");
		for (int ch = 0; ch < lpValues.length; ch++) {
			for (int lpcnt = 0; lpcnt < lpValues[ch].length; lpcnt++) {
				log.debug("lpValues["+ch+"]["+lpcnt+"] "+lpValues[ch][lpcnt]);
			}
		}
		log.debug("==============================================");
	}

	@Override
    public String syncTime(String mcuId, String meterId) {
        Map<String, String> resultMap = new HashMap<String, String>();
        int result = 0;
        try {
            Meter meter = meterDao.get(meterId);
            CommandGW commandGw = DataUtil.getBean(CommandGW.class);
            
            resultMap = commandGw.cmdMeterTimeSyncByGtype(mcuId,meter.getMdsId());
            if(resultMap != null) {
                String before = (String) resultMap.get("beforeTime");
                String after = (String) resultMap.get("afterTime");
                String diff = String.valueOf((TimeUtil.getLongTime(after) - TimeUtil.getLongTime(before))/1000);
                resultMap.put("diff", diff);
                
                saveMeterTimeSyncLog(meter, before, after, result);
            }
        }
        catch (Exception e) {
            result = 1;
            resultMap.put("failReason", e.getMessage());
        }
        return MapToJSON(resultMap);
    }
}
