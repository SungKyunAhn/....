/**
 * 
 */
package com.aimir.fep.tool.batch.job;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.constants.CommonConstants.OTAType;
import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.tool.batch.excutor.IBatchRunnable;
import com.aimir.fep.trap.actions.ZV.EV_ZV_223_2_0_Action;
import com.aimir.fep.trap.common.EV_Action.OTA_UPGRADE_RESULT_CODE;
import com.aimir.fep.util.FMPProperty;
import com.aimir.util.DateTimeUtil;

/**
 * @author simhanger
 *
 */
public class DcuOTARunnableJobForEVN implements IBatchRunnable {
	private static Logger logger = LoggerFactory.getLogger(DcuOTARunnableJobForEVN.class);

	private String name;
	private final int METER_FILTER = 1;
	private final int MODEM_FILTER = 2;
	private final int CONTROL_CODE = 0;
	private final int OPERATION_CODE = 1;

	private CommandGW gw;
	private String mcuSysId;
	private OTAType otaType;
	private String imageKey;
	private String fwURL;
	private String checkSum;
	private List<String> deviceList;
	private int filterType;
	private int nextCommandWaitTime = 5; 
	
	TargetClass targetClass;

	public DcuOTARunnableJobForEVN(long id, CommandGW gw, String mcuSysId, OTAType otaType, String imageKey, String fwURL, String checkSum, List<String> deviceList) {
		this.gw = gw;
		this.mcuSysId = mcuSysId;
		this.otaType = otaType;
		this.imageKey = imageKey;
		this.fwURL = fwURL;
		this.checkSum = checkSum;
		this.deviceList = deviceList;

		switch (otaType) {
		case DCU:
			//filterType = DCU의 경우 DCU측에서 개발이 완료된이후 추가할것.
			//targetClass = TargetClass.MCU;
			break;
		case DCU_COORDINATE:
			//filterType = DCU의 경우 DCU측에서 개발이 완료된이후 추가할것.
			//targetClass = TargetClass.MCU;
			break;
		case DCU_KERNEL:
			//filterType = DCU의 경우 DCU측에서 개발이 완료된이후 추가할것.
			//targetClass = TargetClass.MCU;
			break;
		case MODEM_ZIGBEE_BY_DCU:
			filterType = MODEM_FILTER;
			targetClass = TargetClass.Modem;
			break;
		case METER_ZIGBEE_BY_DCU:
			filterType = METER_FILTER; //추후 Meter OTA가 추가될경우 사용할것.
			targetClass = TargetClass.EnergyMeter;
			break;
		default:
			break;
		}

		name = id + "-RUN-" + mcuSysId + "-" + deviceList.size();
		
		nextCommandWaitTime = Integer.parseInt(FMPProperty.getProperty("ota.req.command.wait.time", "5000"));

		logger.debug("# DcuOTARunnableJobForEVN Name={}, OtaType={}, Target={}", name, otaType.name(), deviceList.toArray(new String[deviceList.size()]).toString());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		logger.debug("#### DcuOTARunnableJobForEVN Start - name={}", name);

		int[] filter = new int[deviceList.size()];
		List<String> filterValue = new ArrayList<String>();

		for (int i = 0; i < deviceList.size(); i++) {
			filter[i] = filterType;
			filterValue.add(deviceList.get(i));

			logger.info("[{}] id = {}, filterType = {}", otaType.name(), deviceList.get(i), filterType);
		}

		Hashtable<String, String> resultTable = null;
		try {
			logger.debug("### Request Node Upgrade => Mcu={}, OTAType={}, FwURL={}, Checksum={}, device={}", mcuSysId, otaType.name(), fwURL, filterValue.toArray(new String[filterValue.size()]));
			
			resultTable = gw.cmdReqNodeUpgradeEVN(mcuSysId, otaType.getTypeCode(), CONTROL_CODE, imageKey, fwURL, checkSum, filter, filterValue.toArray(new String[filterValue.size()]));

			logger.debug("### Request Node Upgrade Result => {}", resultTable.toString());		
			
			logger.debug("### Wait {}seconds for send next command.", nextCommandWaitTime);
			Thread.sleep(nextCommandWaitTime * 1000);
			
			if (resultTable != null && resultTable.size() > 0) {
				Iterator<String> keys = resultTable.keySet().iterator();
				String keyVal = null;
				int requestId = -1;

				logger.debug("### Request Ctrl Upgrade Target[Size=" + resultTable.size() + " , List=" + resultTable.toString() + "]");
				
				while (keys.hasNext()) {
					keyVal = (String) keys.next();
					requestId = Integer.parseInt(resultTable.get(keyVal).toString());

					logger.debug("### Request Ctrl Upgrade => Mcu={}, RequestId={}", mcuSysId, requestId);
					gw.cmdCtrlUpgradeRequestEVN(mcuSysId, requestId, OPERATION_CODE);		
				}
			}			
			
		} catch (Exception e) {
			String openTime = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");

			EV_ZV_223_2_0_Action action3 = new EV_ZV_223_2_0_Action();
			action3.makeEvent(TargetClass.DCU, mcuSysId, targetClass, openTime, OTA_UPGRADE_RESULT_CODE.OTAERR_REQ_UPGRADE, "OTA excute Fail(" + deviceList.toString() + ") by DCU - Result=" + e.getMessage(), "DCU");

			//action3.updateOTAHistory(mSession.getBypassDevice().getModemId(), DeviceType.Modem, openTime, OTA_UPGRADE_RESULT_CODE.OTAERR_TRN_FAIL);

			logger.error("### DCU OTARunnable excute Error - " + e.getMessage(), e);		
		}
			




	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void printResult(String title, ResultStatus status, String desc) {
		logger.info(title + "," + status.name() + "," + desc);
	}

}
