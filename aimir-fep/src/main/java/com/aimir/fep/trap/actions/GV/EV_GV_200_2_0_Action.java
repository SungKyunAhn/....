package com.aimir.fep.trap.actions.GV;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants.EventStatus;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.EventAlertDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.EventUtil;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.MMIU;
import com.aimir.notification.FMPTrap;

/*
 * eventModemPowerFail(200.2.0) 
 */
@Component
public class EV_GV_200_2_0_Action implements EV_Action {
	private static Log log = LogFactory.getLog(EV_GV_200_2_0_Action.class);
	private final String PowerSatusNormal = "1";
	
	@Autowired
	EventAlertDao eventAlertDao;
	
	@Autowired
	ModemDao modemDao;
	
	@Resource(name = "transactionManager")
	JpaTransactionManager txmanager;
	
	@Override
	public void execute(FMPTrap trap, EventAlertLog event) throws Exception {
		log.debug("EventName[eventModemPowerFail] " + " EventCode[" + trap.getCode() + "] Modem[" + trap.getSourceId() + "]");

		String ipAddr = trap.getIpAddr();
		String modemSerial = trap.getSourceId();
		log.info("ipAddr[" + ipAddr + "] , Modem[" + modemSerial + "]");
		
		String modemPowerStatus = event.getEventAttrValue("ModemPowerStatus") == null ? "" : event.getEventAttrValue("ModemPowerStatus");
		log.debug("modemPowerStatus: " + modemPowerStatus);
		
		String modemRssi = event.getEventAttrValue("modemRssi") == null ? "" : event.getEventAttrValue("modemRssi");
		
		if(Integer.parseInt(modemRssi) > 127)
			modemRssi = ((256 - Integer.parseInt(modemRssi)) * -1) +"";

		log.debug("modem RSSI: " + modemRssi);
		
		TransactionStatus txstatus = null; 
		
		try{
			txstatus = txmanager.getTransaction(null);
			MMIU mmiuModem = (MMIU)modemDao.get(modemSerial);
			if(mmiuModem !=null){
				mmiuModem.setRSSI(Integer.parseInt(modemRssi));
				modemDao.update(mmiuModem);
			}
		
			// RSSI Event 저장
			EventAlertLog eventAlertLog = new EventAlertLog();
			eventAlertLog.setStatus(EventStatus.Open);
		    eventAlertLog.setOpenTime(trap.getTimeStamp());
			EventUtil.sendEvent("GPRS Modem RSSI", TargetClass.MMIU, modemSerial, trap.getTimeStamp(), new String[][] { { "message", "RSSI: "+modemRssi } }, eventAlertLog);
			
			if(modemPowerStatus.equals(PowerSatusNormal)){ // 모뎀의 power가 정상인 경우에는 Power Fail Evnet를 저장하지 않는다.
				log.debug("Power Fail Event is not saved, Because modemPowerStatus is normal");
				event.setStatus(EventStatus.NoSaveEvent);
			}else{
				log.debug("Power Fail");
			}
			txmanager.commit(txstatus);
		}catch (Exception e) {
					log.error(e, e);
					if (txstatus != null)
						txmanager.rollback(txstatus);
					throw e;
		}
	}
}
