package com.aimir.fep.meter.saver;

import java.util.LinkedHashSet;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.constants.CommonConstants.MeteringType;
import com.aimir.dao.mvm.LpTMDao;
import com.aimir.dao.system.DeviceModelDao;
import com.aimir.fep.meter.AbstractMDSaver;
import com.aimir.fep.meter.data.MultiPulseSensorData;
import com.aimir.fep.meter.entry.IMeasurementData;
import com.aimir.fep.meter.parser.MultiPulseSensor;
import com.aimir.model.device.Modem;
import com.aimir.model.mvm.LpTM;
import com.aimir.util.Condition;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.Condition.Restriction;

@Service
public class MultiPulseSensorMDSaver extends AbstractMDSaver {

    @Autowired
    protected DeviceModelDao deviceModelDao;
    
    @Autowired
    protected LpTMDao lpTMDao;
    
    @Override
    protected boolean save(IMeasurementData md) throws Exception
    {
        MultiPulseSensor parser = (MultiPulseSensor)md.getMeterDataParser();
        
        List<MultiPulseSensorData> lpData = parser.getLpData();
        
        if(lpData == null || lpData.size() < 0){
        	log.error("LP SIZE is 0");
        	return false;
        }
        
        if(parser.getDataType() == 4) {
            saveMultiSensorData(parser.getMeter().getModem(),lpData);  
        }else{
            // period를 가져와서 주기를 검사한다.
            int interval = 60 / (parser.getPeriod() != 0? parser.getPeriod():1);
            if (interval != parser.getMeter().getLpInterval())
                parser.getMeter().setLpInterval(interval);
            
            
            if(parser.getMeterTime() == null || "".equals(parser.getMeterTime())){
            	List<MultiPulseSensorData> data = parser.getLpData();
            	if(data != null && data.size() > 0 && data.get(0).getDatetime() != null && !"".equals(data.get(0).getDatetime())){
            		parser.setMeterTime((data.get(0).getDatetime()+"00").substring(0,14));
            	}else{
            		parser.setMeterTime(parser.getMeteringTime());
            	}
            }
            
            // 올라온 검침데이타의 검침값을 저장한다.
            saveMeteringData(MeteringType.Normal, parser.getMeteringTime().substring(0, 8),
            		parser.getMeteringTime().substring(8), parser.getMeteringValue(),
                    parser.getMeter(), parser.getDeviceType(), parser.getDeviceId(),
                    parser.getMDevType(), parser.getMDevId(), parser.getMeterTime());            
            
            if(lpData == null || lpData.size() < 1){
            	return false;
            }

            double[][] lpValues = new double[lpData.get(0).getCh().length][lpData.size()];
            int[] flaglist = new int[lpData.size()];
            double basePulse = lpData.get(0).getPulseValue();
    		String yyyymmdd = lpData.get(0).getDatetime().substring(0, 8);
            String hhmm     = lpData.get(0).getDatetime().substring(8, 12);
    			
            for (MultiPulseSensorData data : lpData) {
                if (data == null || data.getCh().length == 0) {
                    log.warn("LP size is 0 then skip");
                    continue;
                }
                
                for (int ch = 0; ch < lpValues.length; ch++) {
                    for (int lpcnt = 0; lpcnt < lpValues[ch].length; lpcnt++) {
                        lpValues[ch][lpcnt] = lpData.get(lpcnt).getCh()[ch];
                    }
                }

                for (int i = 0; i < flaglist.length; i++) {
                    flaglist[i] = data.getFlag();
                }

            }
            saveLPData(MeteringType.Normal, yyyymmdd, 
            	hhmm,
            	lpValues, flaglist, basePulse,
                parser.getMeter(),  parser.getDeviceType(), parser.getDeviceId(),
                parser.getMDevType(), parser.getMDevId());
        }
        
        return true;
    }
    
    protected void saveMultiSensorData(Modem modem, List<MultiPulseSensorData> lpDatas) throws Exception
    {
        for (MultiPulseSensorData envData: lpDatas) {

        	
            for (int i = 0 ; i < envData.getChannelCnt(); i++) {
            	LpTM lp = new LpTM();   
                String str_mm = "value_"+envData.getDatetime().substring(10,12);              
                BeanUtils.copyProperty(lp, str_mm, dformat(envData.getCh()[i]));
                lp.setChannel(i+1);
                lp.setDeviceId(modem.getDeviceSerial());
                lp.setDeviceType(DeviceType.Modem.getCode());
                lp.setDst(0);
                lp.setYyyymmdd(envData.getDatetime().substring(0,8));
                lp.setYyyymmddhh(envData.getDatetime().substring(0,10));
                lp.setHour(envData.getDatetime().substring(8,10));
                lp.setMDevId(modem.getDeviceSerial());
                lp.setMDevType(DeviceType.Modem.name());
                lp.setMeteringType(0);
                lp.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
                //lp.setModem(dbenvSensor);
                
                log.debug("SENSORID["+modem.getDeviceSerial()+"] YYYYMMDDHHMM["+envData.getDatetime().substring(0,12)+"] CHANNEL["+(i+1)+"] VALUE["+envData.getCh()[i]+"]");
                
                LinkedHashSet<Condition> condition = new LinkedHashSet<Condition>();
                condition.add(new Condition("id.mdevId", new Object[]{modem.getDeviceSerial()}, null, Restriction.EQ));
                condition.add(new Condition("id.dst", new Object[]{0}, null, Restriction.EQ));
                condition.add(new Condition("id.channel", new Object[]{i+1}, null, Restriction.EQ));
                condition.add(new Condition("id.mdevType", new Object[]{DeviceType.Modem}, null, Restriction.EQ));
                condition.add(new Condition("id.yyyymmddhh", new Object[]{envData.getDatetime().substring(0,10)}, null, Restriction.EQ));
                
                List<LpTM> dbList = lpTMDao.findByConditions(condition);
                
                try{

                    if(dbList != null && dbList.size() > 0){
                    	LpTM updateLp = dbList.get(0);
                    	updateLp.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
                    	BeanUtils.copyProperty(updateLp, str_mm, dformat(envData.getCh()[i]));
                    	lpTMDao.update(updateLp);
                    	lpTMDao.flushAndClear();
                    } else{
                    	lpTMDao.add(lp);
                        lpTMDao.flushAndClear();
                    }
                }catch(Exception e){
                	log.warn(e,e);
                }
            }            
        }

    }

}
