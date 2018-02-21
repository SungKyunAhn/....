package com.aimir.fep.meter.saver;

import org.springframework.stereotype.Service;

import com.aimir.constants.CommonConstants.MeteringType;
import com.aimir.fep.meter.AbstractMDSaver;
import com.aimir.fep.meter.data.LPData;
import com.aimir.model.device.Meter;
import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.fep.meter.entry.IMeasurementData;

@Service
public class KaifaMBusMDSaver extends AbstractMDSaver {
	
	private LPData[] mbusLplist;
	protected Meter		mbusMeter;
	protected String		mbusDeviceId;
	protected DeviceType	mbusDeviceType;
	protected String		mbusMdevId;
	protected DeviceType	mbusMdevType;
	

	@Override
	protected boolean save(IMeasurementData md) throws Exception {
//        MBus parser = (MBus)md.getMeterDataParser();
//        
//        HMData[] lplist = parser.getLPHMData();
//        HMData[] daylist = parser.getDayData();
//        
//        HMData[] currentData = parser.getCurrentData();
//
//        long diffTime = 0;
//        String meterTime = md.getTimeStamp();
//        long systemTime = DateTimeUtil.getDateFromYYYYMMDDHHMMSS(meterTime).getTime();
//        long limitTime = Long.parseLong(FMPProperty.getProperty("metertime.diff.limit.forcertain")) * 1000;
//        boolean isCorrectTime = true;
//        
//        if(currentData != null && currentData.length > 0){
//            saveMeteringData(MeteringType.Normal, md.getTimeStamp().substring(0,8),
//                    md.getTimeStamp().substring(8, 14), currentData[0].getCh()[0],
//                    parser.getMeter(), parser.getDeviceType(), parser.getDeviceId(),
//                    parser.getMDevType(), parser.getMDevId(), parser.getMeterTime());
//        }

        if(mbusLplist == null){
            log.debug("LPSIZE => 0");
        }
        else
        {
            log.debug("LPSIZE => "+ mbusLplist.length);
            String yyyymmdd = mbusLplist[0].getDatetime().substring(0, 8);
            String hhmm = mbusLplist[0].getDatetime().substring(8, 12);
            //double basePulse = daylist[0].getCh()[0];//해당 날짜가 맞는가 모르겠네 일별 데이터에서 
            
            double[][] lpValues = new double[mbusLplist[0].getCh().length][mbusLplist.length];
            int[] flaglist = new int[mbusLplist.length];
            
            for (int ch = 0; ch < lpValues.length; ch++) {
                for (int lpcnt = 0; lpcnt < lpValues[ch].length; lpcnt++) {
                    lpValues[ch][lpcnt] = mbusLplist[lpcnt].getCh()[ch];
                    log.debug("lpValues[" + ch + "][" + lpcnt + "] = " + lpValues[ch][lpcnt]);
                }
            }
            
            for (int i = 0; i < flaglist.length; i++) {
                flaglist[i] = mbusLplist[i].getFlag();
                log.debug("flaglist[" + i + "] = " + flaglist[i]);
            }
            
            // TODO Flag처리해야 함.
            saveLPData(MeteringType.Normal, yyyymmdd, hhmm, lpValues, flaglist, 0, //basePulse -> Temporary 0,
                    mbusMeter, mbusDeviceType, mbusDeviceId, mbusMdevType, mbusMdevId);

        }
        

        return true;
    }
	
	public void setMeterInfo(Meter meter, String devId, DeviceType devType, String mdevId, DeviceType mdevType) {
		mbusMeter = meter;
		mbusDeviceId = devId;
		mbusDeviceType = devType;
		mbusMdevId = mdevId;
		mbusMdevType = mdevType;
	}
	
	public void setLpData(LPData[] lplist) {
		mbusLplist = lplist;
	}

}
