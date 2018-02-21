package com.aimir.fep.trap.actions.ZV;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;

import com.aimir.dao.device.MCUDao;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.EventUtil;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.MCU;
import com.aimir.notification.FMPTrap;
import com.aimir.util.TimeUtil;

/*
 * Event ID : 200.12.0 Processing Class
 *  evtIpChange - DCU IP Change detect
  
    2.2.5	sysState	        WORD	2				System status (표 5)
	2.5.1	ntwType	            BYTE	1				Network type (0: static, 1: DHCP, 3: PPP)
	2.5.2	ntwApnName	        STRING	32				APN Name
	2.5.3	ntwState	        WORD	2				Network status (표 7)	
	2.5.4	ntwGateway	        UINT	4				Default gateway
	2.5.5	ntwEthIp	        UINT	4				Ethernet IP address
	2.5.6	ntwEthSubnetMask	UINT	4				Ethernet subnet mask
	2.5.7	ntwEthPhy	        BYTE	6				Ethernet Phy address
	2.5.8	ntwPppIp	        UINT	4				PPP IP address
	2.5.9	ntwPppSubnetMask	UINT	4				PPP subnet mask

 */
@Component
public class EV_ZV_200_12_0_Action implements EV_Action {
	private static Log log = LogFactory.getLog(EV_ZV_200_12_0_Action.class);

	@Resource(name = "transactionManager")
	JpaTransactionManager txmanager;

	@Autowired
	MCUDao mcuDao;

	/**
	 * execute event action
	 *
	 * @param trap
	 *            - FMP Trap(MCU Event)
	 * @param event
	 *            - Event Alert Log Data
	 */
	public void execute(FMPTrap trap, EventAlertLog event) throws Exception {
		log.debug("EventName[evtIpChange] " + " EventCode[" + trap.getCode() + "] Modem[" + trap.getSourceId() + "]");

		String ipAddr = trap.getIpAddr();
		String modemSerial = trap.getSourceId();

		String sysState = event.getEventAttrValue("sysState") == null ? "" : event.getEventAttrValue("sysState");
		String ntwType = event.getEventAttrValue("ntwType") == null ? "" : event.getEventAttrValue("ntwType");
		String ntwApnName = event.getEventAttrValue("ntwApnName") == null ? "" : event.getEventAttrValue("ntwApnName");
		String ntwState = event.getEventAttrValue("ntwState") == null ? "" : event.getEventAttrValue("ntwState");
		String ntwGateway = event.getEventAttrValue("ntwGateway") == null ? "" : event.getEventAttrValue("ntwGateway");
		String ntwEthIp = event.getEventAttrValue("ntwEthIp") == null ? "" : event.getEventAttrValue("ntwEthIp");
		String ntwEthSubnetMask = event.getEventAttrValue("ntwEthSubnetMask") == null ? "" : event.getEventAttrValue("ntwEthSubnetMask");
		String ntwEthGateway = event.getEventAttrValue("ntwEthGateway") == null ? "" : event.getEventAttrValue("ntwEthGateway");
		String ntwPppIp = event.getEventAttrValue("ntwPppIp") == null ? "" : event.getEventAttrValue("ntwPppIp");
		String ntwPppSubnetMask = event.getEventAttrValue("ntwPppSubnetMask") == null ? "" : event.getEventAttrValue("ntwPppSubnetMask");

		log.debug("ipAddr[" + ipAddr + "]");
		log.debug("modemSerial[" + modemSerial + "]");
		log.debug("sysState[" + sysState + "]");
		log.debug("ntwType[" + ntwType + "]");
		log.debug("ntwApnName[" + ntwApnName + "]");
		log.debug("ntwState[" + ntwState + "]");
		log.debug("ntwGateway[" + ntwGateway + "]");
		log.debug("ntwEthIp[" + ntwEthIp + "]");
		log.debug("ntwEthSubnetMask[" + ntwEthSubnetMask + "]");
		log.debug("ntwEthGateway[" + ntwEthGateway + "]");
		log.debug("ntwPppIp[" + ntwPppIp + "]");
		log.debug("ntwPppSubnetMask[" + ntwPppSubnetMask + "]");

		log.debug("### ntwEthIp HEX Test = " + event.getEventAttrValue("ntwEthIp.hex"));
		log.debug("### mcuId[" + trap.getMcuId() + "]");

		//JpaTransactionManager txmanager = null;
		TransactionStatus txstatus = null;

		try {
			//txmanager = (JpaTransactionManager)DataUtil.getBean("transactionManager");   
			//MCUDao mcuDao = DataUtil.getBean(MCUDao.class);

			txstatus = txmanager.getTransaction(null);
			String mcuId = trap.getMcuId();

			if (mcuId == null) {
				log.error("empty target mcu id");
				return;
			}

			MCU mcu = mcuDao.get(mcuId);
			String orgIp = mcu.getIpAddr();

			log.debug("EventName[evtIpChange] IP Change [" + orgIp + "] ==> [" + ipAddr + "]");

			if (mcu == null) {
				log.error("does not exist target Mcu");
				return;
			} else {
				mcu.setIpAddr(ipAddr);
				mcu.setLastCommDate(TimeUtil.getCurrentTime());
				mcuDao.update(mcu);
			}

			event.append(EventUtil.makeEventAlertAttr("oldIp", "java.lang.String", orgIp));
			event.append(EventUtil.makeEventAlertAttr("newIp", "java.lang.String", ipAddr));
			event.setSupplier(mcu.getSupplier());
			event.setActivatorIp(ipAddr);

		} catch (Exception e) {
			log.error(e, e);
		} finally {
			if (txstatus != null)
				txmanager.commit(txstatus);
		}
	}
}
