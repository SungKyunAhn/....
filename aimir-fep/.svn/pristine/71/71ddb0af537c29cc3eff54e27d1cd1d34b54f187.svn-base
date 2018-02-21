package com.aimir.fep.trap.actions.GV;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.MCU;
import com.aimir.model.device.Modem;
import com.aimir.notification.FMPTrap;

@Component
public class EV_GV_200_9_0_Action implements EV_Action {
	private static Log log = LogFactory.getLog(EV_GV_200_1_0_Action.class);

	@Resource(name = "transactionManager")
	JpaTransactionManager txmanager;

	@Autowired
	MCUDao dcuDao;

	@Autowired
	ModemDao modemDao;

	@Override
	public void execute(FMPTrap trap, EventAlertLog event) throws Exception {
		log.debug("EventName[eventModemSMSResponse] " + " EventCode[" + trap.getCode() + "] Modem[" + trap.getSourceId() + "]");

		String ipAddr = trap.getIpAddr();
		String modemSerial = trap.getSourceId();
		String mcuID = trap.getMcuId();

		log.info("ipAddr[" + ipAddr + "] , Modem[" + modemSerial + "] , MCU[" + mcuID + "]");

		String modemId = "";
		String newServerIp = event.getEventAttrValue("resServerIP");
		String resServerPort = event.getEventAttrValue("resServerPort");
		String resGetMeterStatus = event.getEventAttrValue("resGetMeterStatus");
		String resRelayDisconnect = event.getEventAttrValue("resRelayDisconnect");
		String resRelayReconnect = event.getEventAttrValue("resRelayReconnect");

		TransactionStatus txstatus = null;
		MCU mcu = null;

		try {
			txstatus = txmanager.getTransaction(null);

			if (mcuID != null && !mcuID.isEmpty()) {
				mcu = dcuDao.get(mcuID);
			} else {
				Modem modem = modemDao.get(modemSerial);
				if (modem != null) {
					mcu = dcuDao.get(modem.getMcuId());
				}
			}

			event.setActivatorId(modemSerial);
			event.setActivatorType(TargetClass.MMIU);
			if (mcu != null) {
				event.setSupplier(mcu.getSupplier());
			}

			txmanager.commit(txstatus);
		} catch (Exception e) {
			log.error(e, e);
			if (txstatus != null)
				txmanager.rollback(txstatus);
			throw e;
		}
	}

}
