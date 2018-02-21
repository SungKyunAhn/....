package com.aimir.fep.event;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.fep.meter.link.MeterEventLink;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.MeterEventLog;
import com.aimir.util.DateTimeUtil;

public class MeterEventTest {
    private static Log log = LogFactory.getLog(MeterEventTest.class);
    private static String meterId = "3";
    
    static {
        DataUtil.setApplicationContext(new ClassPathXmlApplicationContext(new String[]{"/config/spring.xml"}));
        }
    
    @Test
    public void test_lowbattery() {
        log.info("Low Battery STS.GE.SM110.20");
        MeterEventLog mel = new MeterEventLog();
        mel.setActivatorId(meterId);
        mel.setActivatorType(TargetClass.EnergyMeter.name());
        mel.setMeterEventId("STS.GE.SM110.20");
        mel.setOpenTime(DateTimeUtil.getDateString(new Date()));
        mel.setYyyymmdd(mel.getOpenTime().substring(0,8));
        mel.setWriteTime(mel.getOpenTime());
        
        try {
            MeterEventLink link = (MeterEventLink)DataUtil.getBean("meterEventLink");
            link.execute(mel);
        }
        catch (Exception e) {
            log.error(e);
        }
    }
    
    @Test
    public void test_demandresetdetect() {
        log.info("Demand Reset Detect STE.GE.SM110.20");
        MeterEventLog mel = new MeterEventLog();
        mel.setActivatorId(meterId);
        mel.setActivatorType(TargetClass.EnergyMeter.name());
        mel.setMeterEventId("STE.GE.SM110.20");
        mel.setOpenTime(DateTimeUtil.getDateString(new Date()));
        mel.setYyyymmdd(mel.getOpenTime().substring(0,8));
        mel.setWriteTime(mel.getOpenTime());
        
        try {
            MeterEventLink link = (MeterEventLink)DataUtil.getBean("meterEventLink");
            link.execute(mel);
        }
        catch (Exception e) {
            log.error(e);
        }
    }
    
    @Test
    public void test_meterramerror() {
        log.info("Meter RAM Error STS.GE.SM110.13");
        MeterEventLog mel = new MeterEventLog();
        mel.setActivatorId(meterId);
        mel.setActivatorType(TargetClass.EnergyMeter.name());
        mel.setMeterEventId("STS.GE.SM110.13");
        mel.setOpenTime(DateTimeUtil.getDateString(new Date()));
        mel.setYyyymmdd(mel.getOpenTime().substring(0,8));
        mel.setWriteTime(mel.getOpenTime());
        
        try {
            MeterEventLink link = (MeterEventLink)DataUtil.getBean("meterEventLink");
            link.execute(mel);
        }
        catch (Exception e) {
            log.error(e);
        }
    }
    
    @Test
    public void test_meterromerror() {
        log.info("Meter ROM Error STS.GE.SM110.14");
        MeterEventLog mel = new MeterEventLog();
        mel.setActivatorId(meterId);
        mel.setActivatorType(TargetClass.EnergyMeter.name());
        mel.setMeterEventId("STS.GE.SM110.14");
        mel.setOpenTime(DateTimeUtil.getDateString(new Date()));
        mel.setYyyymmdd(mel.getOpenTime().substring(0,8));
        mel.setWriteTime(mel.getOpenTime());
        
        try {
            MeterEventLink link = (MeterEventLink)DataUtil.getBean("meterEventLink");
            link.execute(mel);
        }
        catch (Exception e) {
            log.error(e);
        }
    }
    
    @Test
    public void test_testmodestart() {
        log.info("Test Mode Start STE.GE.SM110.32");
        MeterEventLog mel = new MeterEventLog();
        mel.setActivatorId(meterId);
        mel.setActivatorType(TargetClass.EnergyMeter.name());
        mel.setMeterEventId("STE.GE.SM110.32");
        mel.setOpenTime(DateTimeUtil.getDateString(new Date()));
        mel.setYyyymmdd(mel.getOpenTime().substring(0,8));
        mel.setWriteTime(mel.getOpenTime());
        
        try {
            MeterEventLink link = (MeterEventLink)DataUtil.getBean("meterEventLink");
            link.execute(mel);
        }
        catch (Exception e) {
            log.error(e);
        }
    }
    
    @Test
    public void test_testmodeend() {
        log.info("Test Mode End STS.GE.SM110.33");
        MeterEventLog mel = new MeterEventLog();
        mel.setActivatorId(meterId);
        mel.setActivatorType(TargetClass.EnergyMeter.name());
        mel.setMeterEventId("STE.GE.SM110.33");
        mel.setOpenTime(DateTimeUtil.getDateString(new Date()));
        mel.setYyyymmdd(mel.getOpenTime().substring(0,8));
        mel.setWriteTime(mel.getOpenTime());
        
        try {
            MeterEventLink link = (MeterEventLink)DataUtil.getBean("meterEventLink");
            link.execute(mel);
        }
        catch (Exception e) {
            log.error(e);
        }
    }
    
    @Test
    public void test_lowvoltage() {
        log.info("Low Voltage MFE.GE.SM110.16");
        MeterEventLog mel = new MeterEventLog();
        mel.setActivatorId(meterId);
        mel.setActivatorType(TargetClass.EnergyMeter.name());
        mel.setMeterEventId("MFE.GE.SM110.16");
        mel.setOpenTime(DateTimeUtil.getDateString(new Date()));
        mel.setYyyymmdd(mel.getOpenTime().substring(0,8));
        mel.setWriteTime(mel.getOpenTime());
        
        try {
            MeterEventLink link = (MeterEventLink)DataUtil.getBean("meterEventLink");
            link.execute(mel);
        }
        catch (Exception e) {
            log.error(e);
        }
    }
}
