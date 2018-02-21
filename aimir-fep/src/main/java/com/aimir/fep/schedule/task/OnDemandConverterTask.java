package com.aimir.fep.schedule.task;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.protocol.fmp.exception.FMPMcuException;
import com.aimir.model.device.MCU;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;

/**
 * Converter Type 의 Modem에 on demand 명령을 날리는 Task
 * 
 * @author kskim
 * 
 */
public class OnDemandConverterTask {

	private static Log log = LogFactory.getLog(OnDemandConverterTask.class);

	@Autowired
	private ModemDao modemDao;

	@Autowired
	private MeterDao meterDao;

	@Autowired
	private MCUDao mcuDao;

	@Autowired
	CommandGW commandGw;

	public OnDemandConverterTask() {

	}

	public void OnDemand() throws FMPMcuException, Exception {
		Set<Condition> condition = new HashSet<Condition>();
		condition.add(new Condition("modemType",
				new Object[] { ModemType.Converter }, null, Restriction.EQ));
		condition.add(new Condition("protocolType",
				new Object[] { Protocol.LAN }, null, Restriction.EQ));
		List<Modem> modems = modemDao.findByConditions(condition);
		log.debug("modems count : " + modems.size());

		for (Modem modem : modems) {
			Meter meter = meterDao.findByCondition("modem", modem);
			log.debug(String.format("meterid : %s, modem serial : %s", meter
					.getMdsId(), modem.getDeviceSerial()));

			// 주기적인 검침 이기때문에 날짜 조건을 뺀다.
			commandGw.cmdOnDemandMeter(null, meter.getMdsId(), modem
					.getDeviceSerial(), "0", "", "");

		}

	}

}
