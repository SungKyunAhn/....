package com.aimir.fep.trap.actions.SP;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.EventStatus;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.MCUDao;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.EventUtil;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.device.EventAlertDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.notification.FMPTrap;

/**
 * Event ID : EV_SP_220.2.0 (Alarm Meter Event)
 *
 * @author 
 * @version $Rev: 1 $, $Date: 2016-05-13 10:00:00 +0900 $,
 */
@Component
public class EV_SP_220_2_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_SP_220_2_0_Action.class);

    @Resource(name="transactionManager")
    JpaTransactionManager txmanager;
    
    @Autowired
    SupplierDao supplierDao;

    @Autowired
    MCUDao mcuDao;
    
    @Autowired
    ModemDao modemDao;
    
    @Autowired
    MeterDao meterDao;
    
    @Autowired
    EventAlertDao eaDao;
    
   /**
     * execute event action
     *
     * @param trap - FMP Trap(Alarm Meter Event)
     * @param event - Event Alert Log Data
     */
    public void execute(FMPTrap trap, EventAlertLog event) throws Exception
    {
        log.debug("EV_SP_220_2_0_Action : EventName[evtAlarmMeter] "
                +" EventCode[" + trap.getCode()
                +"] MCU["+trap.getMcuId()+"] TargetClass[" + event.getActivatorType() + "]");

        TransactionStatus txstatus = null;
        try { 
            txstatus = txmanager.getTransaction(null);
            

            log.debug("EV_SP_220_2_0_Action : event[" + event.toString() + "]");

            String ipAddr = trap.getIpAddr();
            String meterId = event.getEventAttrValue("meterId");
            
            /*
             * meterAlarmData is one or more. it has to be taken as hex.
             */
            List<byte[]> alarmDataList = new ArrayList<byte[]>();
            String alarmDataHex = null;
            for (int i = 0; ; i++) {
                if (i > 0)
                    alarmDataHex = event.getEventAttrValue("streamEntry."+i+".hex");
                else
                    alarmDataHex = event.getEventAttrValue("streamEntry.hex");
                
                if (alarmDataHex == null || "".equals(alarmDataHex))
                    break;
                else
                    alarmDataList.add(Hex.encode(alarmDataHex));
            }
            
            log.debug("METER_ID[" + meterId + "]");
            Meter meter = null;
            Modem modem = null;
            if (meterId == null || "".equals(meterId)) {
                modem = modemDao.get(trap.getSourceId());
                if (modem != null && modem.getMeter().size() > 0) {
                    for (Meter m : modem.getMeter()) {
                        if (m.getModemPort() == null || m.getModemPort() == 0) {
                            meter = m;
                            break;
                        }
                    }
                }
            }
            else {
                meter = meterDao.get(meterId);
            }
            
            if (meter != null)
            {
	            event.setActivatorId(meterId);
	            event.setActivatorType(meter.getMeterType().getName());
	            event.setSupplier(meter.getSupplier());
	            event.setLocation(meter.getLocation());
            }
            else {
                if (modem != null) {
                    event.setActivatorId(trap.getSourceId());
                    event.setActivatorType(trap.getSourceType());
                    event.setSupplier(modem.getSupplier());
                    event.setLocation(modem.getLocation());
                }
            }
            
            doAlarm(alarmDataList, meter, event);
            /*
            byte[]  _timestamp = Hex.encode(event.getEventAttrValue("meterAlarmReceivedTime"));
            String meterAlarmReceivedTime = String.format("%4d%02d%02d%02d%02d%02d", 
                    DataUtil.getIntTo2Byte(new byte[]{_timestamp[0], _timestamp[1]}),
                    DataUtil.getIntToByte(_timestamp[2]),
                    DataUtil.getIntToByte(_timestamp[3]),
                    DataUtil.getIntToByte(_timestamp[4]),
                    DataUtil.getIntToByte(_timestamp[5]),
                    DataUtil.getIntToByte(_timestamp[6]));
            log.debug("METER_ALARM_RECEICED_TIME[" + meterAlarmReceivedTime + "]");
            */
            
            // byte[] _meterAlarmData =  Hex.encode(event.getEventAttrValue("meterAlarmData"));    
            
            
//
//            byte[] _alarmDateTime = new byte[14];
//            System.arraycopy(_meterAlarmData, 1+4, _alarmDateTime, 0, 14);
//            String alarmDateTime = new String(_alarmDateTime); //???????????
//            log.debug("METER_ALARM_DATA_DATE_TIME[" + alarmDateTime + "]");
//            
//            byte[] value = new byte[4];
//            System.arraycopy(_meterAlarmData, 1+4+14+1+1+8 + 1, value, 0, 4);
//            
//            String group = null;
//            String alarm = null;
//            if ( (alarm = getAlarmByte0(value[0])) != null ){
//            	group = "Other Alarms";
//            }
//            else if ( (alarm = getAlarmByte1(value[1])) != null ){ 
//            	group = "Critical Alarms";
//            }
//            else if ( (alarm = getAlarmByte2(value[2])) != null ){
//            	group = "M-Bus Alarms";
//            }
//            Meter meter = meterDao.get(meterId);
//            if (meter != null)
//            {
//	            event.setActivatorId(meterId);
//	            event.setActivatorType(meter.getMeterType().getName());
//	            event.setSupplier(meter.getSupplier());
//	            event.setLocation(meter.getLocation());
//            }
//	        log.debug("ALARM_GROUP[" + group + "] ALARM[" + alarm + "]");
            
        }
        catch(Exception ex) {
            log.error(ex,ex);
        }
        finally {
            if (txstatus != null) txmanager.commit(txstatus);
        }

        log.debug("Meter Event Action Compelte");
    }
    
    public void doAlarm(List<byte[]> alarmDataList, Meter meter, EventAlertLog event) 
    throws Exception
    {
        for(byte[] _meterAlarmData : alarmDataList) {
            doAlarm(_meterAlarmData, meter, event);
        }
    }
    
    public void doAlarm(String modemId, String _meterAlarmData) 
    throws Exception
    {
        Modem modem = modemDao.get(modemId);
        Meter meter = null;
        for (Meter m : modem.getMeter()) {
            if (m.getModemPort() == null || m.getModemPort() == 0) {
                meter = m;
                break;
            }
        }
        doAlarm(Hex.encode(_meterAlarmData), meter, null);
    }
    
    public void doAlarm(byte[] _meterAlarmData, Meter meter, EventAlertLog _event) 
    throws Exception
    {
        if (meter == null) {
            log.warn("Meter is NULL!!");
            return;
        }
        
        byte[] bx;
        int pos = 0;
            
        /*
        MeterEvent meterPayload = new MeterEvent();
        meterPayload.decode(_meterAlarmData);
        
        MeterEventFrame[] meterEventFrame = meterPayload.getMeterEventFrame();
        for ( int i=0; i<meterPayload.getCount(); i++ )
        {
            String alarmDateTime = meterEventFrame[i].getTime();
            String alarmValue = meterEventFrame[i].getValue();
            log.debug("ALARMDATATIME[" + alarmDateTime + "],VALUE[" +alarmValue+"]");
            byte[] _alarmValue = DataUtil.get4ByteToInt(Integer.parseInt(alarmValue));
            check1stByte(_alarmValue[0], meter, event);
            check2stByte(_alarmValue[1], meter, event);
       }
       */
       // OF
       bx = new byte[1];
       System.arraycopy(_meterAlarmData, pos, bx, 0, bx.length);
       pos += bx.length;
       
       // 40 00 00 00
       bx = new byte[4];
       System.arraycopy(_meterAlarmData, pos, bx, 0, bx.length);
       pos += bx.length;
       
       // 2 바이트 사용할 필요 없음.
       bx = new byte[14];
       System.arraycopy(_meterAlarmData, pos, bx, 0, bx.length);
       pos += bx.length;
       // 6th : week. skip
       String etime = String.format("%4d%02d%02d%02d%02d%02d", 
               DataUtil.getIntTo2Byte(new byte[]{bx[2], bx[3]}),
               DataUtil.getIntToByte(bx[4]), DataUtil.getIntToByte(bx[5]),
               DataUtil.getIntToByte(bx[7]), DataUtil.getIntToByte(bx[8]),
               DataUtil.getIntToByte(bx[9]));
       log.debug("EventTime[" + etime + "]");
       
       //structure
       bx = new byte[1];
       System.arraycopy(_meterAlarmData, pos, bx, 0, bx.length);
       pos += bx.length;
       
       bx = new byte[1];
       System.arraycopy(_meterAlarmData, pos, bx, 0, bx.length);
       pos += bx.length;
       // length - needless
       int length = DataUtil.getIntToBytes(bx);
       
       if (_meterAlarmData.length <= 21) {
           log.warn("AlarmData length is short [" + Hex.decode(_meterAlarmData) + "]");
           _event.setEventAlert(eaDao.findByCondition("name", "Meter Alarm"));
           _event.append(EventUtil.makeEventAlertAttr("message", "java.lang.String", Hex.decode(_meterAlarmData)));
           return;
       }
       
       byte[] obisCode = new byte[8];
       byte[] value = new byte[5];
       
       String eventClassName = "Power Alarm";
       String message = "Power Restore";
       String activatorId = null;
       
       System.arraycopy(_meterAlarmData, pos, obisCode, 0, obisCode.length);
       pos += obisCode.length;
       log.debug("OBIS[" + Hex.decode(obisCode) + "]");
       if (Hex.decode(obisCode).equals("09060000616200FF")) {
           System.arraycopy(_meterAlarmData, pos, value, 0, value.length);
           pos += value.length;
           log.debug("VALUE[" + Hex.decode(value) + "]");
           
           boolean lineAlarm = false;
           // Clock invalid
           if ((value[4] & 0x01) != 0x00) {
               eventClassName = "Meter Alarm";
               message = "Clock Invalid";
               log.debug(message);
               activatorId = meter.getMdsId();
               
               EventUtil.sendEvent(eventClassName,
                       TargetClass.EnergyMeter,
                       activatorId,
                       etime,
                       new String[][] {{"message", message}}, 
                       new EventAlertLog());
           }
           // Replace battery
           if ((value[4] & 0x02) != 0x00) {
               eventClassName = "Meter Alarm";
               message = "Replace Battery";
               log.debug(message);
                   
               EventUtil.sendEvent(eventClassName,
                       TargetClass.EnergyMeter,
                       activatorId,
                       etime,
                       new String[][] {{"message", message}}, 
                       new EventAlertLog());
           }
           // Power Up
           if ((value[4] & 0x04) != 0x00) {
               eventClassName = "Power Alarm";
               message = "Power Restore";
               log.debug(message);
               activatorId = meter.getMdsId();
               
               EventAlertLog event = new EventAlertLog();
               event.setStatus(EventStatus.Cleared);
               
               EventUtil.sendEvent(eventClassName,
                       TargetClass.EnergyMeter,
                       activatorId,
                       etime,
                       new String[][] {{"message", message}},
                       event);
                   
               try {
                   if (meter.getMeterStatus().getName().equals("PowerDown")) {
                       meter.setMeterStatus(CommonConstants.getMeterStatusByName("Normal"));
                       meterDao.update(meter);
                   }
               }
               catch (Exception e) {
                   log.error(e, e);
               }
           }

           message = "";
           
           // l1 missing
           if ((value[4] & 0x08) != 0x00) {
               lineAlarm = true;
               message = "L1";
           }
           // l2 missing
           if ((value[4] & 0x10) != 0x00) {
               lineAlarm = true;
               if (message != null && !"".equals(message))
                   message += ",";
               message += "L2";
           }
           // l3 missing
           if ((value[4] & 0x20) != 0x00) {
               lineAlarm = true;
               if (message != null && !"".equals(message))
                   message += ",";
               message += "L3";
           }
           
           if (lineAlarm) {
               eventClassName = "Power Alarm";
               message += " Missing";
               if (meter != null) {
                   activatorId = meter.getMdsId();
                   
                   log.debug(message);
                   
                   EventUtil.sendEvent(eventClassName,
                           TargetClass.EnergyMeter,
                           activatorId,
                           etime,
                           new String[][] {{"message", message}}, 
                           new EventAlertLog());
               }
           }
           // Program memory error
           if ((value[3] & 0x01) != 0x00) {
               eventClassName = "Malfunction Warning";
               message = "Program Memory Error";
               log.debug(message);
               
               activatorId = meter.getMdsId();
               
               EventUtil.sendEvent(eventClassName,
                       TargetClass.EnergyMeter,
                       activatorId,
                       etime,
                       new String[][] {{"message", message}},
                       new EventAlertLog());
           }
           // RAM error
           if ((value[3] & 0x02) != 0x00) {
               eventClassName = "Malfunction Warning";
               message = "RAM Error";
               log.debug(message);
               
               activatorId = meter.getMdsId();
               
               EventUtil.sendEvent(eventClassName,
                       TargetClass.EnergyMeter,
                       activatorId,
                       etime,
                       new String[][] {{"message", message}},
                       new EventAlertLog());
           }
           // NV memory error
           if ((value[3] & 0x04) != 0x00) {
               eventClassName = "Malfunction Warning";
               message = "NV Memory Error";
               log.debug(message);
               
               activatorId = meter.getMdsId();
               
               EventUtil.sendEvent(eventClassName,
                       TargetClass.EnergyMeter,
                       activatorId,
                       etime,
                       new String[][] {{"message", message}},
                       new EventAlertLog());
           }
           // Measurement System Error
           if ((value[3] & 0x08) != 0x00) {
               eventClassName = "Meter Alarm";
               message = "Measurement System Error";
               log.debug(message);
               
               activatorId = meter.getMdsId();
               
               EventUtil.sendEvent(eventClassName,
                       TargetClass.EnergyMeter,
                       activatorId,
                       etime,
                       new String[][] {{"message", message}},
                       new EventAlertLog());
           }
           // Watchdong error
           if ((value[3] & 0x10) != 0x00) {
               eventClassName = "Meter Alarm";
               message = "Watchdog Error";
               log.debug(message);
               
               activatorId = meter.getMdsId();
               
               EventUtil.sendEvent(eventClassName,
                       TargetClass.EnergyMeter,
                       activatorId,
                       etime,
                       new String[][] {{"message", message}},
                       new EventAlertLog());
           }
           // cover alarm
           if ((value[3] & 0x20) != 0x00) {
               eventClassName = "Cover Alarm";
               message = "Cover Open";
               log.debug(message);
               
               activatorId = meter.getMdsId();
               
               EventUtil.sendEvent(eventClassName,
                       TargetClass.EnergyMeter,
                       activatorId,
                       etime,
                       new String[][] {{"message", message}, {"caseState", "Status : Open"}},
                       new EventAlertLog());
           }
           // strong magnet field detected
           if ((value[3] & 0x40) != 0x00) {
               eventClassName = "Meter Alarm";
               message = "Strong magnet field detected";
               log.debug(message);
               
               activatorId = meter.getMdsId();
               
               EventUtil.sendEvent(eventClassName,
                       TargetClass.EnergyMeter,
                       activatorId,
                       etime,
                       new String[][] {{"message", message}},
                       new EventAlertLog());
           }
           // cover alarm
           if ((value[3] & 0x80) != 0x00) {
               eventClassName = "Cover Alarm";
               message = "Cover Close";
               log.debug(message);
               
               activatorId = meter.getMdsId();
               
               EventAlertLog event = new EventAlertLog();
               event.setStatus(EventStatus.Cleared);
               
               EventUtil.sendEvent(eventClassName,
                       TargetClass.EnergyMeter,
                       activatorId,
                       etime,
                       new String[][] {{"message", message}, {"caseState", "Status : Close"}},
                       event);
           }
           // communication error m-bus
           if ((value[2] & 0x01) != 0x00) {
               eventClassName = "Communication Alarm";
               message = "M-Bus Channel 1";
               log.debug(message);
               
               activatorId = meter.getMdsId();
               
               EventUtil.sendEvent(eventClassName,
                       TargetClass.EnergyMeter,
                       activatorId,
                       etime,
                       new String[][] {{"message", message}},
                       new EventAlertLog());
           }
           // communication error m-bus
           if ((value[2] & 0x02) != 0x00) {
               eventClassName = "Communication Alarm";
               message = "M-Bus Channel 2";
               log.debug(message);
               
               activatorId = meter.getMdsId();
               
               EventUtil.sendEvent(eventClassName,
                       TargetClass.EnergyMeter,
                       activatorId,
                       etime,
                       new String[][] {{"message", message}},
                       new EventAlertLog());
           }
           // communication error m-bus
           if ((value[2] & 0x04) != 0x00) {
               eventClassName = "Communication Alarm";
               message = "M-Bus Channel 3";
               log.debug(message);
               
               activatorId = meter.getMdsId();
               
               EventUtil.sendEvent(eventClassName,
                       TargetClass.EnergyMeter,
                       activatorId,
                       etime,
                       new String[][] {{"message", message}},
                       new EventAlertLog());
           }
           // communication error m-bus
           if ((value[2] & 0x08) != 0x00) {
               eventClassName = "Communication Alarm";
               message = "M-Bus Channel 4";
               log.debug(message);
               
               activatorId = meter.getMdsId();
               
               EventUtil.sendEvent(eventClassName,
                       TargetClass.EnergyMeter,
                       activatorId,
                       etime,
                       new String[][] {{"message", message}},
                       new EventAlertLog());
           }
           // new m-bus device discovered channel 1
           if ((value[1] & 0x01) != 0x00) {
               eventClassName = "Meter Alarm";
               message = "New M-Bus Channel 1";
               log.debug(message);
               
               activatorId = meter.getMdsId();
               
               EventUtil.sendEvent(eventClassName,
                       TargetClass.EnergyMeter,
                       activatorId,
                       etime,
                       new String[][] {{"message", message}},
                       new EventAlertLog());
           }
           // new m-bus device discovered channel 2
           if ((value[1] & 0x02) != 0x00) {
               eventClassName = "Meter Alarm";
               message = "New M-Bus Channel 2";
               log.debug(message);
               
               activatorId = meter.getMdsId();

               EventUtil.sendEvent(eventClassName,
                       TargetClass.EnergyMeter,
                       activatorId,
                       etime,
                       new String[][] {{"message", message}},
                       new EventAlertLog());
           }
           // new m-bus device discovered channel 3
           if ((value[1] & 0x04) != 0x00) {
               eventClassName = "Meter Alarm";
               message = "New M-Bus Channel 3";
               log.debug(message);
               
               activatorId = meter.getMdsId();
               
               EventUtil.sendEvent(eventClassName,
                       TargetClass.EnergyMeter,
                       activatorId,
                       etime,
                       new String[][] {{"message", message}},
                       new EventAlertLog());
           }
           // new m-bus device discovered channel 4
           if ((value[1] & 0x08) != 0x00) {
               eventClassName = "Meter Alarm";
               message = "New M-Bus Channel 4";
               log.debug(message);
               
               activatorId = meter.getMdsId();
               
               EventUtil.sendEvent(eventClassName,
                       TargetClass.EnergyMeter,
                       activatorId,
                       etime,
                       new String[][] {{"message", message}},
                       new EventAlertLog());
           }
           // modem communication ok
           if ((value[1] & 0x10) != 0x00) {
               eventClassName = "Meter Alarm";
               message = "Modem Communication OK";
               log.debug(message);
               
               activatorId = meter.getMdsId();
               
               EventUtil.sendEvent(eventClassName,
                       TargetClass.EnergyMeter,
                       activatorId,
                       etime,
                       new String[][] {{"message", message}},
                       new EventAlertLog());
           }
       }
    }
    
    void check1stByte(byte value, Meter meter, EventAlertLog event ){
    	try { 
	    	int iVal = (int)value & 0xff;
	    	// POWER UP
	    	if ( (iVal & 4) != 0 )
	    	{
	    	    event.setStatus(EventStatus.Cleared);
	    		EventUtil.sendEvent("Power Alarm",
	    				TargetClass.valueOf(meter.getMeterType().getName()), 
	    				meter.getMdsId(),
	    				new String[][]{{"message","Power Restore"}}, event);
	    		log.debug("sendEvent [Meter Power Alarm],PARAM[message:Power Restore]");
	    	}
	    	// L1 connected incorrectly
	    	if ( (iVal & 8) != 0 )
	    	{
	    		EventUtil.sendEvent("Power Alarm",
	    				TargetClass.valueOf(meter.getMeterType().getName()), 
	    				meter.getMdsId(),
	    				new String[][]{{"message","Meter L1 connected incorrectly"}}, event);
	    		log.debug("sendEvent [Meter Power Alarm],PARAM[message:L1 connected incorrectly]");
	    	}
	    	// L2 connected incorrectly
	    	if ( (iVal & 16) != 0 )
	    	{
	    		EventUtil.sendEvent("Power Alarm",
	    				TargetClass.valueOf(meter.getMeterType().getName()), 
	    				meter.getMdsId(),
	    				new String[][]{{"message","Meter L2 connected incorrectly"}}, event);
	    		log.debug("sendEvent [Meter Power Alarm],PARAM[message:L2 connected incorrectly]");
	    	}
	    	// L3 connected incorrectly
	    	if ( (iVal & 32) != 0 )
	    	{
	    		EventUtil.sendEvent("Power Alarm",
	    				TargetClass.valueOf(meter.getMeterType().getName()), 
	    				meter.getMdsId(),
	    				new String[][]{{"message","Meter L3 connected incorrectly"}}, event);
	    		log.debug("sendEvent [Meter Power Alarm],PARAM[message:L3 connected incorrectly]");
	    	}
        }
        catch(Exception ex) {
            log.error(ex,ex);
        }
   }
    
    void check2stByte(byte value, Meter meter, EventAlertLog event ){
    	try { 
	    	int iVal = (int)value & 0xff;
	    	// Fraud attempt
	    	if ( (iVal & 32) != 0 )
	    	{
	    		EventUtil.sendEvent("Cover Alarm",
	    				TargetClass.valueOf(meter.getMeterType().getName()), 
	    				meter.getMdsId(),
	    				new String[][]{{"message","Meter Fraud attempt"}}, event);
	    		log.debug("sendEvent [Meter Cover Alarm],PARAM[message:Fraud attempt]");
	    	}
        }
        catch(Exception ex) {
            log.error(ex,ex);
        }
   }

//    String getAlarmByte0(byte value){
//	    String error = null;
//	    int iVal = (int)value & 0xff;
//	    switch (iVal){
//		    case 0x01: //Bit1
//		    	error = "Clock invalid";
//		    	break;
//		    case 0x02: //Bit2
//		    	error = "Replace battery";
//				break;
//		    case 0x04: //Bit3
//		    	error = "Power Up";
//		    	break;
//		    case 0x08: //Bit4
//		    	error = "L1 connected incorrectly";
//		    	break;
//		    case 0x10: //Bit5
//		    	error = "L2 connected incorrectly";
//		    	break;
//		    case 0x20: //Bit6
//		    	error = "L3 connected incorrectly";
//		    	break;
//		    case 0x40: //Bit7
//		    case 0x80: //Bit8	
//		    	// not used
//		    	break;
//	    }
//	    return error;
//    }
//    String getAlarmByte1(byte value){
//	    String error = null;
//	    int iVal = (int)value & 0xff;
//	    switch (iVal){
//		    case 0x01: //Bit1
//		    	error = "Program memory error";
//		    	break;
//		    case 0x02: //Bit2
//		    	error = "RAM Error";
//				break;
//		    case 0x04: //Bit3
//		    	error = "NV memory Error";
//		    	break;
//		    case 0x08: //Bit4
//		    	error = "Measurement System Error";
//		    	break; 	
//		    case 0x10: //Bit5
//		    	error = "Watchdog error";
//		    	break;
//		    case 0x20: //Bit6
//		    	error = "Fraud attempt";
//		    	break;
//		    case 0x40: //Bit7
//		    case 0x80: //Bit8	
//		    	// not used
//		    	break;
//	    }
//	    return error;
//    }
//    
//    
//    String getAlarmByte2(byte value){
//	    String error = null;
//	    int iVal = (int)value & 0xff;
//	    switch (iVal){
//		    case 0x01: //Bit1
//		    	error = "Communication error M-Bus";
//		    	break;
//		    case 0x02: //Bit2
//		    	error = "New M-Bus device discovered";
//				break;
//		    case 0x04: //Bit3	
//		    case 0x08: //Bit4
//		    case 0x10: //Bit5
//		    case 0x20: //Bit6
//		    case 0x40: //Bit7
//		    case 0x80: //Bit8	
//		    	break;
//	    }
//	    return error;
//    }
}
