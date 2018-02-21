package com.aimir.fep.meter.saver;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.aimir.constants.CommonConstants.MeteringType;
import com.aimir.fep.meter.AbstractMDSaver;
import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.meter.entry.IMeasurementData;
import com.aimir.fep.meter.parser.CES_LSMeter;

@Service
public class CES_LSMeterMDSaver extends AbstractMDSaver {
	private static Log log = LogFactory.getLog(CES_LSMeterMDSaver.class);

	@Override
	protected boolean save(IMeasurementData md) throws Exception {
		CES_LSMeter parser = (CES_LSMeter) md.getMeterDataParser();

		LPData[] lplist = parser.getLPData();
		String meterDate = parser.getMeterTime();
       
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
            int interval = parser.getResolution() != 0? parser.getResolution():15;
            if (interval != parser.getMeter().getLpInterval())
                parser.getMeter().setLpInterval(interval);
            
			double basePulse = lplist[0].getBasePulse();				        	        
	                    
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
        
        
        
        if( parser.getMeteringValue()!= null){
            saveMeteringData(MeteringType.Normal, md.getTimeStamp().substring(0,8),
                    md.getTimeStamp().substring(8, 14), parser.getMeteringValue(),
                    parser.getMeter(), parser.getDeviceType(), parser.getDeviceId(),
                    parser.getMDevType(), parser.getMDevId(), parser.getMeterTime());
        }


		return true;
	}

}
