package com.aimir.fep.trap.actions.ZV;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.dao.device.ChangeLogDao;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.SNRLogDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.DeviceModelDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.SNRLog;
import com.aimir.notification.FMPTrap;

/**
 * Event ID : ZV_210.3.0 (ZRU Modem SNR)
 * <br>ZRU Modem
 */
@Component
public class EV_ZV_210_3_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_ZV_210_3_0_Action.class);
    
    @Autowired
    MCUDao dcuDao;
    
    @Autowired
    CodeDao codeDao;
    
    @Autowired
    CommandGW commandGW;
    
    @Autowired
    ChangeLogDao clDao;
    
    @Autowired
    DeviceModelDao deviceModelDao;
    
    @Autowired
    SupplierDao supplierDao;
    
    @Autowired
    LocationDao locationDao;
    
    @Autowired
    SNRLogDao snrLogDao;
    
    /**
     * execute event action
     *
     * @param trap - FMP Trap(DCU Event)
     * @param event - Event Alert Log Data
     * 
     * 31.3.2	moG3ShortId	WORD	2			Modem Short ID
     * lqi
     */
    public void execute(FMPTrap trap, EventAlertLog event) throws Exception
    {

        log.debug("EventName[eventLQI] "+" EventCode[" + trap.getCode()+"] Modem["+trap.getSourceId()+"]");
        String mcuId = trap.getMcuId();
        String ipAddr = trap.getIpAddr();
        String modemSerial = trap.getSourceId();  
        String timeStamp = trap.getTimeStamp();
        String srcType = trap.getSourceType();
        int val =  Byte.parseByte(event.getEventAttrValue("byteEntry")) & 0xFF;
        Double lqi = getG3_PLC_SNR(val);

        try{
            SNRLog snrLog = new SNRLog();            
            snrLog.setDcuid(mcuId);
            snrLog.setDeviceId(modemSerial);
            snrLog.setDeviceType(ModemType.ZRU.name());
            snrLog.setYyyymmdd(timeStamp.substring(0,8));
            snrLog.setHhmmss(timeStamp.substring(8,14));
            snrLog.setSnr(lqi);
            snrLogDao.add(snrLog);
        }catch(Exception e){
            log.warn(e,e);
        }
        
        log.debug("ZRU Modem LQI Event Action Compelte");
    }
    
    public Double getG3_PLC_SNR(int val){
    	int min = 0;
    	int min_value = -10;
    	double steps = 0.25;
    	double cal = 0d;
    	
    	cal = (val - min)*steps + min_value;
    	return cal;    	
    }

}
