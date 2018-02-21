package com.aimir.fep.meter.saver;

import org.springframework.stereotype.Service;

import com.aimir.constants.CommonConstants.MeteringType;
import com.aimir.fep.meter.AbstractMDSaver;
import com.aimir.fep.meter.data.HMData;
import com.aimir.fep.meter.entry.IMeasurementData;
import com.aimir.fep.meter.parser.NURI_Kamstrup601;
import com.aimir.fep.util.FMPProperty;
import com.aimir.util.DateTimeUtil;

@Service
public class NURI_Kamstrup601MDSaver extends AbstractMDSaver {

	@Override
	protected boolean save(IMeasurementData md) throws Exception {
		NURI_Kamstrup601 parser = (NURI_Kamstrup601)md.getMeterDataParser();
        
        HMData[] lplist = parser.getLPHMData();
        HMData[] daylist = parser.getDayData();
        
        HMData[] currentData = parser.getCurrentData();

        long diffTime = 0;
        String meterTime = md.getTimeStamp();
        long systemTime = DateTimeUtil.getDateFromYYYYMMDDHHMMSS(meterTime).getTime();
        long limitTime = Long.parseLong(FMPProperty.getProperty("metertime.diff.limit.forcertain")) * 1000;
        boolean isCorrectTime = true;
        
        if(currentData != null && currentData.length > 0){
            saveMeteringData(MeteringType.Normal, md.getTimeStamp().substring(0,8),
                    md.getTimeStamp().substring(8, 14), currentData[0].getCh()[0],
                    parser.getMeter(), parser.getDeviceType(), parser.getDeviceId(),
                    parser.getMDevType(), parser.getMDevId(), parser.getMeterTime());
        }

        if(lplist == null){
            log.debug("LPSIZE => 0");
        }
        else
        {
            log.debug("LPSIZE => "+lplist.length);
            String yyyymmdd = lplist[0].getDate();

            String hhmm = lplist[0].getTime().substring(0,4);
            double basePulse = daylist[0].getCh()[0];//해당 날짜가 맞는가 모르겠네 일별 데이터에서 
            
            
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
        

        return true;
    }

}
