package com.aimir.fep.trap.actions.SP;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.aimir.dao.device.EventAlertDao;
import com.aimir.dao.device.FirmwareIssueHistoryDao;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.protocol.fmp.frame.service.entry.opaqueEntry;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.model.device.EventAlertAttr;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.MCU;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.notification.FMPTrap;
import com.aimir.util.DateTimeUtil;

/**
 * Event ID : EV_SP_240.3.0 (DCU NMS Info)
 * 
 * 240.3 This event has modemSPNSMEntry
 *
 * @author elevas
 * @version $Rev: 1 $, $Date: 2016-12-29 10:00:00 +0900 $,
 */
@Component
public class EV_SP_240_3_0_Action implements EV_Action {
	private static Log log = LogFactory.getLog(EV_SP_240_3_0_Action.class);

	@Resource(name = "transactionManager")
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
	 * @param trap
	 *            - FMP Trap(Modem Tamper Event)
	 * @param event
	 *            - Event Alert Log Data
	 */
	public void execute(FMPTrap trap, EventAlertLog event) throws Exception {
		String openTime = DateTimeUtil.getCurrentDateTimeByFormat(null);
		log.debug("EV_SP_240_3_0_Action : EventName[evtNMSInfo] " + 
		" EventCode[" + trap.getCode() + "] MCU[" + trap.getMcuId() + 
		"] TargetClass[" + event.getActivatorType() + "] openTime[" + openTime + "]");

		TransactionStatus txstatus = null;
		//        TargetClass targetClass = event.getActivatorType();
		try {
			txstatus = txmanager.getTransaction(
			        new DefaultTransactionDefinition(TransactionDefinition.ISOLATION_READ_UNCOMMITTED));

			MCU mcu = mcuDao.get(trap.getSourceId());
			if (mcu == null) {
				log.debug("no mcu intance exist mcu[" + trap.getMcuId() + "]");
				return;
			}
			log.debug("EV_SP_240_3_0_Action : event[" + event.toString() + "]");

			/*
			 * modemSPNSMEntry is opaqueEntry, it can be one or more.
			 * opaqueEntry must be taken as opaque.
			 */
			List<opaqueEntry> modemSPNSMEntry = new ArrayList<opaqueEntry>();
			EventAlertAttr attr = null;
			for (int i = 0;; i++) {
				if (i > 0)
					attr = event.getEventAttr("opaqueEntry." + i);
				else
					attr = event.getEventAttr("opaqueEntry");

				if (attr == null)
					break;
				else {
				    log.debug(attr.getValue());
				}
			}

		} catch (Exception ex) {
			log.error(ex, ex);
		} finally {
			if (txstatus != null)
				txmanager.commit(txstatus);
		}

		log.debug("Modem Tamper Action Compelte");
	}

}
