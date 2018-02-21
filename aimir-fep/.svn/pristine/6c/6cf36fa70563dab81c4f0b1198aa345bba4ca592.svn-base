package com.aimir.fep.meter.saver;

import java.util.HashMap;

import org.springframework.stereotype.Service;

import com.aimir.constants.CommonConstants.MeteringType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.fep.meter.AbstractMDSaver;
import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.meter.data.Instrument;
import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.meter.entry.IMeasurementData;
import com.aimir.fep.meter.parser.NURI_KamstrupKMP;
import com.aimir.fep.util.FMPProperty;
import com.aimir.model.device.MMIU;
import com.aimir.util.DateTimeUtil;

@Service
public class NURI_KamstrupKMPMDSaver extends AbstractMDSaver {

	@Override
	protected boolean save(IMeasurementData md) throws Exception {
		NURI_KamstrupKMP parser = (NURI_KamstrupKMP)md.getMeterDataParser();
        
        LPData[] lplist = parser.getLPData();

        long diffTime = 0;
        String meterTime = md.getTimeStamp();
        long systemTime = DateTimeUtil.getDateFromYYYYMMDDHHMMSS(meterTime).getTime();
        long limitTime = Long.parseLong(FMPProperty.getProperty("metertime.diff.limit.forcertain")) * 1000;
        boolean isCorrectTime = true;
        
        int interval = parser.getResolution() != 0? parser.getPeriod():60;
        log.debug("interval : "+interval);
        log.debug("dbinterval : "+parser.getMeter().getLpInterval());
        if (parser.getMeter().getLpInterval() == null || parser.getMeter().getLpInterval() == 0){
            log.debug("setinterval : "+interval);
            parser.getMeter().setLpInterval(interval);
        }
    	parser.getMeter().setSwVersion(parser.getSwVersion());
    	parser.getMeter().setSwName(parser.getSwName());
    	
        HashMap<String,String> map = parser.getMdmData();
        if(map != null){

        	if(map.get("protocolType").equals("11")){
        		parser.getMeter().getModem().setProtocolType(Protocol.SMS.name());
        	}
        	if(map.get("protocolType").equals("1")){
        		parser.getMeter().getModem().setProtocolType(Protocol.GSM.name());
        	}
        	parser.getMeter().getModem().setFwVer((String) map.get("swVersion"));
        	parser.getMeter().getModem().setSwVer((String) map.get("swVersion"));
        	parser.getMeter().getModem().setHwVer((String) map.get("hwVersion"));
        	//((MMIU)(parser.getMeter().getModem())).setPhoneNumber((String) map.get("simNumber"));
        	//((MMIU)(parser.getMeter().getModem())).setPhoneNumber((String) map.get("sysPhoneNumber"));
        	((MMIU)(parser.getMeter().getModem())).setRfPower(Long.parseLong(((String) map.get("csq"))));
        	//((MMIU)(parser.getMeter().getModem())).setRfPower(Integer.parseInt(((String) map.get("csq"))));

        }
        
        saveMeteringData(MeteringType.Normal, md.getTimeStamp().substring(0,8),
                md.getTimeStamp().substring(8, 14), parser.getMeteringValue(),
                parser.getMeter(), parser.getDeviceType(), parser.getDeviceId(),
                parser.getMDevType(), parser.getMDevId(), parser.getMeterTime());

        if(lplist == null){
            log.debug("LPSIZE => 0");
        }
        else
        {
        	
            log.debug("LPSIZE => "+lplist.length);
            String yyyymmdd = lplist[0].getDatetime().substring(0,8);

            String hhmm = lplist[0].getDatetime().substring(8,12);
            double basePulse = parser.getLpValue();
            
            
            double[][] lpValues = new double[lplist[0].getCh().length][lplist.length];
            int[] flaglist = new int[lplist.length];
            
            for (int ch = 0; ch < lpValues.length; ch++) {
                for (int lpcnt = 0; lpcnt < lpValues[ch].length; lpcnt++) {
                    lpValues[ch][lpcnt] = lplist[lpcnt].getCh()[ch];
                }
            }
            
            for (int i = 0; i < flaglist.length; i++) {
                flaglist[i] = lplist[i].getFlag();
            }
            
            // TODO Flag처리해야 함.
            saveLPData(MeteringType.Normal, yyyymmdd, hhmm, lpValues, flaglist, basePulse,
                    parser.getMeter(), parser.getDeviceType(), parser.getDeviceId(), 
                    parser.getMDevType(), parser.getMDevId());

        }
        
        // 순시값 (Voltage,Current) 데이터 저장
        Instrument[] instrument = parser.getInstrument();
        if(instrument != null){
            savePowerQuality(parser.getMeter(), parser.getTimestamp(), instrument,
            		parser.getDeviceType(), parser.getDeviceId(), parser.getMDevType(), parser.getMDevId());
        }
        
        //Meter Event Log저장
        EventLogData[] meterEventLog = parser.getEventLog();
        if(meterEventLog != null){
        	saveMeterEventLog(parser.getMeter(), meterEventLog);
        }
        

        return true;
    }

}
