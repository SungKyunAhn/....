/**
 * 	NAPC_W201Saver.java
 * 2011. 11. 2.
 * Copyright (c) 2008-2009 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 * @author kskim
 */
package com.aimir.fep.meter.saver;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aimir.constants.CommonConstants;
import com.aimir.dao.device.WaterMeterDao;
import com.aimir.fep.meter.entry.IMeasurementData;
import com.aimir.fep.meter.parser.NAPC_W202;
import com.aimir.model.device.WaterMeter;

/**
 * @author kskim
 */
public class NAPC_W202Saver extends ZEUPLS2MDSaver {
	private static Log log = LogFactory.getLog(NAPC_W202Saver.class);
	
	@Autowired
	private WaterMeterDao waterMeterDao;
	
    @Override
    protected boolean save(IMeasurementData md) throws Exception 
    {
        super.save(md);
        
        // ZEUPLS2 검침값 외에 온디맨드시 미터 정보가 더 있다.
        NAPC_W202 parser = (NAPC_W202)md.getMeterDataParser();
        WaterMeter meter = null;
        
        log.debug("NAPC_W201 parser : "+parser);
        log.debug("meter : " + parser.getMeter());
        if (parser.isOnDemand()) {
            Double currentPulse = parser.getCurrentPulse();
            String serialNumber = parser.getSerialNumber();
            byte alarmStatus = parser.getAlarmStatus();
            byte meterStatus = parser.getMeterStatus();
            // meter version
            String functionTestResult = parser.getFunctionTestResult();
            String meterHwVerison = parser.getHwVersion();
            String meterSwVersion = parser.getSwVersion();
            
            meter = (WaterMeter)parser.getMeter();
            meter.setAlarmStatus((int)alarmStatus);
            meter.setMeterStatus(CommonConstants.getWaterMeterStatus((int)meterStatus+""));
            meter.setHwVersion(meterHwVerison);
            meter.setSwVersion(meterSwVersion);
            meter.setMeterCaution(functionTestResult);
            meter.setLastMeteringValue(currentPulse);
            
            //serialNumber 값이 없을경우 설정된 값을 보존한다.
            if(serialNumber!=null && !serialNumber.equals("")) 
            	meter.setValveSerial(serialNumber);
        }
        else {
        	log.debug("is not ondemand");
            byte alarmStatus = parser.getAlarmStatus();
            byte meterStatus = parser.getMeterStatus();
            
            meter = (WaterMeter)parser.getMeter();
            meter.setAlarmStatus((int)alarmStatus);
            meter.setMeterStatus(CommonConstants.getWaterMeterStatus((int)meterStatus+""));
        }
        log.debug(meter.toString());
        
        waterMeterDao.saveOrUpdate(meter);
        waterMeterDao.flushAndClear();
        
        return true;
    }
}
