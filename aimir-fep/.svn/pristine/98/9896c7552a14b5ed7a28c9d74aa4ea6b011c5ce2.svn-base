package com.aimir.fep.trap.actions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants.FW_EQUIP;
import com.aimir.constants.CommonConstants.FW_OTA;
import com.aimir.constants.CommonConstants.FW_STATE;
import com.aimir.constants.CommonConstants.FW_TRIGGER;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.TR_STATE;
import com.aimir.dao.device.FirmwareHistoryDao;
import com.aimir.dao.device.MCUCodiDao;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.fep.command.conf.DefaultConf;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.protocol.fmp.frame.service.entry.codiEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.sensorInfoNewEntry;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.EventUtil;
import com.aimir.fep.util.FirmwareUtil;
import com.aimir.fep.util.MIBUtil;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.FirmwareHistory;
import com.aimir.model.device.MCU;
import com.aimir.model.device.MCUCodi;
import com.aimir.model.device.Modem;
import com.aimir.notification.FMPTrap;
//import com.aimir.schedule.command.CmdManager;
//import com.aimir.schedule.command.CmdOperationUtil;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;


/**
 * Event ID : 214.3.0 Processing Class
 *
 * @author kaze
 * @version $Rev: 1 $, $Date: 2008-10-16 15:59:15 +0900 $,
 */
@Component
public class EV_214_3_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_214_3_0_Action.class);
    
    @Resource(name="transactionManager")
    JpaTransactionManager txmanager;
    
    @Autowired
    MCUDao mcuDao;
    
    @Autowired
    FirmwareHistoryDao firmwareHistoryDao;

    @Autowired
	ModemDao modemDao;


    @Autowired
    MCUCodiDao codiDao;
    
    @Autowired
    CommandGW cmdgw;
    
    /**
     * execute event action
     *
     * @param trap
     *            - FMP Trap(MCU Event)
     * @param event
     *            - Event Alert Log Data
     */
    public void execute(FMPTrap trap, EventAlertLog event) throws Exception
    {
    	log.debug("EV_214_3_0_Action Start");
    	
        log.debug("Event[eventOTAEnd] EventCode[" + trap.getCode() + "] MCU["
                  + trap.getMcuId() + "] TriggerId["+event.getEventAttrValue("stringEntry")+"] TriggerState["+event.getEventAttrValue("byteEntry")+"]");

        // Initialize
        String triggerId = event.getEventAttrValue("stringEntry");
        // 문자가 섞여 있어 대체한다.
        triggerId = triggerId.replaceAll("[a-zA-Z]", "");
        String mcuId = trap.getMcuId();
        String equipKind="";
        int reason = event.getEventAttrValue("byteEntry") != null ? Integer.parseInt(event.getEventAttrValue("byteEntry")) : FW_STATE.Cancel.getState();
        int triggerState = TR_STATE.Unknown.getCode();

        FW_TRIGGER step = FW_TRIGGER.End;
        FW_STATE state = FW_STATE.Success;
        StringBuffer buf = new StringBuffer();
        
        if(FW_STATE.Success.getState() == reason){
            buf.append("MCU["+mcuId+"] OTA End [Success]");
            state = FW_STATE.Success;
            step = FW_TRIGGER.Success;
            triggerState = TR_STATE.Success.getCode();
        }
        if(FW_STATE.Fail.getState() == reason){
            buf.append("MCU["+mcuId+"] OTA End [Fail]");
            state = FW_STATE.Fail;
            step = FW_TRIGGER.End;
            triggerState = TR_STATE.Terminate.getCode();
        }
        if(FW_STATE.Cancel.getState() == reason){
            buf.append("MCU[" + mcuId + "] OTA End[User Cancel]");
            state = FW_STATE.Cancel;
            step = FW_TRIGGER.End;
            triggerState = TR_STATE.Terminate.getCode();
        }

        event.append(EventUtil.makeEventAlertAttr("message", "java.lang.String", buf.toString()));

        TransactionStatus txstatus = null;
        
        try {
            txstatus  = txmanager.getTransaction(null);
            
            MCU mcu = mcuDao.get(mcuId);
    
            if (mcu != null)
            {
    
    		    Set<Condition> condition = new HashSet<Condition>();
                condition.add(new Condition("id.trId", new Object[]{Long.parseLong(triggerId)}, null, Restriction.EQ));
                
                List<FirmwareHistory> fwHistoryList = firmwareHistoryDao.findByConditions(condition);
                if (fwHistoryList.size() > 0) {
                    FirmwareHistory firmwareHistory = fwHistoryList.get(0);
                    if(firmwareHistory != null){                	
                    	
                        if(firmwareHistory.getEquipKind()!= null && !"".equals(firmwareHistory.getEquipKind())){
                        	equipKind = firmwareHistory.getEquipKind();
                            event.append(EventUtil.makeEventAlertAttr("equipKind",
                                                                 "java.lang.String",
                                                                 firmwareHistory.getEquipKind()));
                        }
                        if(firmwareHistory.getEquipVendor() != null && !"".equals(firmwareHistory.getEquipVendor())){
                            event.append(EventUtil.makeEventAlertAttr("equipType",
                                                                 "java.lang.String",
                                                                 firmwareHistory.getEquipType()));
                        }
                        if(firmwareHistory.getEquipVendor() != null && !"".equals(firmwareHistory.getEquipVendor())){
                            event.append(EventUtil.makeEventAlertAttr("equipVendor",
                                                                 "java.lang.String",
                                                                 firmwareHistory.getEquipVendor()));
                        }
                        if(firmwareHistory.getEquipModel() != null && !"".equals(firmwareHistory.getEquipModel())){
                            event.append(EventUtil.makeEventAlertAttr("equipModel",
                                                                 "java.lang.String",
                                                                 firmwareHistory.getEquipModel()));
                        }
                    }
                }     
    
            }else{
                log.debug("Event[eventOTAEnd] MCU is not exist");
            }        
            
            FirmwareUtil.updateTriggerHistory(triggerId,step.getCode(),triggerState);
            if(equipKind!=null && equipKind.equals("MCU")){
        		
        		if (state.equals(FW_STATE.Success)) {
        			FirmwareUtil.updateOTAHistory(triggerId, mcuId, FW_OTA.All,FW_STATE.Success, "");
        		} else if (state.equals(FW_STATE.Fail)) {
        			FirmwareUtil.updateOTAHistory(triggerId, mcuId, FW_OTA.All,FW_STATE.Fail, "");
        		} else if (state.equals(FW_STATE.Cancel)) {
        			FirmwareUtil.updateOTAHistory(triggerId, mcuId, FW_OTA.All,FW_STATE.Unknown, "");
        		} 
            }
            txmanager.commit(txstatus);
        }
        catch (Exception e) {
            log.error(e, e);
            if (txstatus != null) txmanager.rollback(txstatus);
            return;
        }
    	log.debug("EV_214_3_0_Action End ");
        
        // Thread.sleep(1000*60*10);//집중기 경우 배포 후 바로 리셋 되며 그 시간이 대략 5분정도 경과 된다. 넉넉히 10분 정도 후에 업데이트 하기 위함.

        //Get Equip Version Infomation  
        if(equipKind!=null && equipKind.equals("MCU")){
            settingGetVersionSchedule("0", triggerId, mcuId);//0
        }else if(equipKind!=null && equipKind.equals("MODEM")){
            settingGetVersionSchedule("1", triggerId, mcuId);//1
        }else if(equipKind!=null && equipKind.equals("COORDINATOR")){
            settingGetVersionSchedule("2", triggerId, mcuId);//2
        }else{
            settingGetVersionSchedule("99", triggerId, mcuId);//99
        }        
    }
    
    public void settingGetVersionSchedule(String equipKind,String triggerId, String mcuId) throws Exception {    	
		getEquipVersion(Integer.parseInt(equipKind), triggerId, mcuId);
	}
    /**
	 * get Equip Version Information
	 * 
	 * @param equipKind
	 * @param mcuId
	 */
	public int getEquipVersion(int equipKind, String triggerId, String mcuId) {
		log.debug("[getEquipVersion] EquipKind: " + equipKind + " ,triggerId:"	+ triggerId + " ,mcuId:" + mcuId);
		int result = FW_STATE.Success.getState();
		try {
			// All
			if (equipKind == FW_EQUIP.All.getKind()) {
				try {
					getMCUVersion(triggerId, mcuId);
					getCodiVersion(triggerId, mcuId);
					getModemVersion(triggerId, mcuId);
				} catch (Exception e) {
					result = FW_STATE.Fail.getState();
					log.error("Can Not Get Sensor Version Info :"+ e.getMessage(), e);
				}
			}
			// MCU
			else if (equipKind == FW_EQUIP.MCU.getKind()) {
				try {
					getMCUVersion(triggerId, mcuId);
				} catch (Exception e) {
					result = FW_STATE.Fail.getState();
					log.error("Can Not Get MCU Version Info :" + e.getMessage(),e);
				}
			}
			// Codi
			else if (equipKind == FW_EQUIP.Coordinator.getKind()) {
				try {
					getCodiVersion(triggerId, mcuId);
				} catch (Exception e) {
					result = FW_STATE.Fail.getState();
					log.error("Can Not Get Codi Version Info :" + e.getMessage(), e);
				}
			}
			// Modem or Repeater
			else if (equipKind == FW_EQUIP.Modem.getKind()) {
				try {
					getModemVersion(triggerId, mcuId);
				} catch (Exception e) {
					result = FW_STATE.Fail.getState();
					log.error("Can Not Get Modem Version Info :" + e.getMessage(), e);
				}
			}
		} catch (Exception e) {
			result = FW_STATE.Fail.getState();
			log.error("Can Not Get Sensor Version Info :" + e.getMessage(), e);
		}
		return result;
	}
	
	/**
	 * @param gw
	 * @param triggerId
	 * @param mcuId
	 * @throws Exception
	 */
	public void getMCUVersion(String triggerId, String mcuId)
			throws Exception {
		
		log.debug("getMCUVersion start");

		codiEntry[] codiEntry = null;
		Hashtable res = new Hashtable();
		
		try {		
	        DefaultConf defaultConf = DefaultConf.getInstance();
	        Hashtable props = defaultConf.getDefaultProperties("MCU");

	        log.debug("props size=" + props.size());
	        MIBUtil mibUtil = MIBUtil.getInstance();
	        List<String> property = new ArrayList<String>();
	        Iterator it = props.keySet().iterator();
	        for (int i = 0; it.hasNext(); i++) {
	            try {

	                String key = (String) it.next();
	                property.add(key);
	                log.debug("props[" + i + "] :" + key + " ,oid= " + property.get(i));
	            } catch (Exception e) {
	            }
	        }
	        
			res = cmdgw.cmdMcuScanning(mcuId, property.toArray(new String[0]));

		} catch (Exception e) {
			throw e;
		} finally {
			cmdgw.close();
		}

		String hwVersion = "";
		String fwVersion = "";
		String fwBuild = "";
		String sysID = "";

		Iterator<?> it = res.keySet().iterator();
		while(it.hasNext()) {
			String key = (String) it.next();
			log.debug("res.key = "+key );
			log.debug("String.valueOf(mop.get(key) = "+String.valueOf(res.get(key)));
		}
		hwVersion=String.valueOf(res.get("sysHwVersion")); 
		fwVersion=String.valueOf(res.get("sysSwVersion")); 
		fwBuild=String.valueOf(res.get("sysSwRevision")); 
		sysID = String.valueOf(res.get("sysID"));		
		
		log.debug("hwVersion = "+hwVersion );
		log.debug("fwVersion = "+fwVersion );
		log.debug("fwBuild = "+fwBuild );
		log.debug("mcu.getSysID() = "+sysID );
		String mcuSysId = sysID;

	}
	
	/**
	 * @param gw
	 * @param triggerId
	 * @param mcuId
	 * @throws Exception
	 */
	public void getCodiVersion(String triggerId, String mcuId)
			throws Exception {

		codiEntry[] codiEntry = null;
		try {
			codiEntry = cmdgw.cmdGetCodiList(mcuId);
		} catch (Exception e) {
			throw e;
		} finally {
			cmdgw.close();
		}

		String hwVersion = new String("");
		String fwVersion = new String("");
		String fwBuild = new String("");
		String codiId = "";

		MCU mcu = mcuDao.get(mcuId);

		if (mcu == null) {
			log.error("MCU[" + mcuId
					+ "] is not have a codinator information in DB");
			return;
		}
		MCUCodi codi = mcu.getMcuCodi();

		if (codi == null) {
			log.error("MCU[" + mcuId
					+ "] is not have a codinator information in DB");
			return;
		}
		for (int i = 0; i < codiEntry.length; i++) {
			codiId = codiEntry[i].getCodiID().toString();

			hwVersion = String.valueOf(codiEntry[i].getCodiHwVer().getValue());
			codi.setCodiHwVer(hwVersion);
			fwVersion = String.valueOf(codiEntry[i].getCodiFwVer().getValue());
			codi.setCodiFwVer(fwVersion);
			fwBuild = String.valueOf(codiEntry[i].getCodiFwBuild().getValue());
			codi.setCodiFwBuild(fwBuild);
		}

		log.debug("mcuId[" + mcuId + "] codiId[" + codi.getCodiShortID()
				+ "] hwVersion[" + hwVersion + "] fwVersion[" + fwVersion
				+ "] fwBuild[" + fwBuild + "]");
		codiDao.update(codi);
	}
	
	
	/**
	 * @param gw
	 * @param triggerId
	 * @param mcuId
	 * @throws Exception
	 */
	public void getModemVersion(String triggerId, String mcuId)
			throws Exception {
		log.debug("getModemVersion start...");
		sensorInfoNewEntry[] sie = null;

		codiEntry[] codiEntry = null;
		try {
			sie = cmdgw.cmdGetModemAllNew(mcuId);
		} catch (Exception e) {
			throw e;
		} finally {
			cmdgw.close();
		}

		String hwVersion = "";
		String fwVersion = "";
		String fwBuild = "";
		boolean isArm = false;
		MCU mcu = mcuDao.get(mcuId);
		if (mcu == null) {
			log.debug("MCU not exist! [" + mcuId + "]");
			return;
		}
		for (int i = 0; i < sie.length; i++) {
			// Check Sensor Type
			if (sie[i].getSensorModel().toString().equals("MSTR711")) {
				isArm = true;
			} else {
				isArm = false;
			}

			// -------------------
			// Update OTA State
			// -------------------
			if (sie[i].getSensorID() != null && sie[i].getSensorOTAState() != null) {
				int otaState = sie[i].getSensorOTAState().getValue();
				log.debug("TriggerId[" + triggerId + "] mcuId[" + mcuId + "] modemId[" + sie[i].getSensorID() + "] otaState[" + otaState + "]");

				if ((otaState & FW_OTA.All.getStep()) == FW_OTA.All.getStep()) {
					FirmwareUtil.updateOTAHistory(triggerId, sie[i].getSensorID().toString(), FW_OTA.All,FW_STATE.Unknown, "");
				} else if ((otaState & FW_OTA.Scan.getStep()) == FW_OTA.Scan.getStep()) {
					FirmwareUtil.updateOTAHistory(triggerId, sie[i].getSensorID().toString(), FW_OTA.Scan,FW_STATE.Unknown, "");
				} else if ((otaState & FW_OTA.Install.getStep()) == FW_OTA.Install.getStep()) {
					FirmwareUtil.updateOTAHistory(triggerId, sie[i].getSensorID().toString(), FW_OTA.Install,FW_STATE.Unknown, "");
				} else if ((otaState & FW_OTA.Verify.getStep()) == FW_OTA.Verify.getStep()) {
					FirmwareUtil.updateOTAHistory(triggerId, sie[i].getSensorID().toString(), FW_OTA.Verify,FW_STATE.Unknown, "");
				} else if ((otaState & FW_OTA.DataSend.getStep()) == FW_OTA.DataSend.getStep()) {
					FirmwareUtil.updateOTAHistory(triggerId, sie[i].getSensorID().toString(), FW_OTA.DataSend,FW_STATE.Unknown, "");
				} else if ((otaState & FW_OTA.Check.getStep()) == FW_OTA.Check.getStep()) {
					FirmwareUtil.updateOTAHistory(triggerId, sie[i].getSensorID().toString(), FW_OTA.Check,FW_STATE.Unknown, "");
				} else if ((otaState & FW_OTA.Init.getStep()) == FW_OTA.Init.getStep()) {
					FirmwareUtil.updateOTAHistory(triggerId, sie[i].getSensorID().toString(), FW_OTA.Init,FW_STATE.Unknown, "");
				}
			}

			// -------------------
			// Update Version Info.
			// -------------------
			Modem dummySensor = new Modem();
			ModemType sensorType = getSensorType(sie[i].getSensorModel().toString());
			log.debug("sensorType: " + sensorType);
			if (sensorType == ModemType.ZRU) {
				dummySensor.setModemType(ModemType.ZRU.name());
			} else if (sensorType == ModemType.ZEUPLS) {
				dummySensor.setModemType(ModemType.ZEUPLS.name());
			} else if (sensorType == ModemType.ZBRepeater) {
				dummySensor.setModemType(ModemType.ZBRepeater.name());
			} else if (sensorType == ModemType.ZEUMBus) {
				dummySensor.setModemType(ModemType.ZEUMBus.name());
			} else {
				log.debug("Unkown Sensor Type or Not implemented Sensor Type");
			}

			if (mcuId != null) {
				dummySensor.setMcu(mcu);
			}
			if (sie[i].getSensorID() != null) {
				dummySensor.setDeviceSerial(sie[i].getSensorID().toString());
			}

			if (sie[i].getSensorLastConnect() != null) {
				dummySensor.setLastLinkTime(sie[i].getSensorLastConnect().toString());
			}
			if (sie[i].getSensorInstallDate() != null) {
				dummySensor.setInstallDate(sie[i].getSensorInstallDate().toString());
			}
			if (sie[i].getSensorState() != null) {
				dummySensor.setCommState(sie[i].getSensorState().getValue());
			}

			// ------------
			// Arm
			// ------------
			if (isArm) {
				if (sie[i].getSensorHwVersion() != null) {
					hwVersion = sie[i].getSensorHwVersion().decodeVersion();
					dummySensor.setHwVer(hwVersion);
				}
				if (sie[i].getSensorFwVersion() != null) {
					fwVersion = sie[i].getSensorFwVersion().decodeVersion();
					dummySensor.setFwVer(fwVersion);
				}
				if (sie[i].getSensorFwBuild() != null) {
					fwBuild = sie[i].getSensorFwBuild().getValue() < 9 ? "0"
							+ sie[i].getSensorFwBuild().getValue() : sie[i]
							.getSensorFwBuild().getValue()
							+ "";
					dummySensor.setFwRevision(fwBuild);
				}
				log.debug("Arm - mcuId[" + mcuId + "] modemId["
						+ sie[i].getSensorID() + "] hwVersion[" + hwVersion
						+ "] fwVersion[" + fwVersion + "] fwBuild[" + fwBuild
						+ "]");
			}
			// ------------
			// Zigbee
			// ------------
			else {
				if (sie[i].getSensorHwVersion() != null) {
					hwVersion = sie[i].getSensorHwVersion().decodeVersion();
					dummySensor.setHwVer(hwVersion);
				}
				if (sie[i].getSensorFwVersion() != null) {
					fwVersion = sie[i].getSensorFwVersion().decodeVersion();
					dummySensor.setFwVer(fwVersion);
				}
				if (sie[i].getSensorFwBuild() != null) {
					fwBuild = sie[i].getSensorFwBuild().getValue() < 9 ? "0"
							+ sie[i].getSensorFwBuild().getValue() : sie[i]
							.getSensorFwBuild().getValue()
							+ "";
					dummySensor.setFwRevision(fwBuild);
				}
				log.debug("Zigbee - mcuId[" + mcuId + "] modemId["
						+ sie[i].getSensorID() + "] hwVersion[" + hwVersion
						+ "] fwVersion[" + fwVersion + "] fwBuild[" + fwBuild
						+ "]");
			}

			Modem instance = modemDao.get(sie[i].getSensorID().toString());

			// DB Exist
			if (instance != null) {
				/*FirmwareUtil.checkFirmwareHistory(FW_EQUIP.Modem.getKind(),
						triggerId, mcuId, sie[i].getSensorID().toString(),
						hwVersion, fwVersion, fwBuild, isArm);*/
				/*
				 * 여기서 modem f/w 업데이트
				 * 
				 * */
				
				log.debug("getModemVersion Exist Sensor - isArm["
						+ isArm
						+ "] mcuId["
						+ mcuId
						+ "] modemId["
						+ sie[i].getSensorID()
						+ "] hwVersion["
						+ sie[i].getSensorHwVersion().decodeVersion()
						+ "] fwVersion["
						+ sie[i].getSensorFwVersion().decodeVersion()
						+ "] fwBuild["
						+ (sie[i].getSensorFwBuild().getValue() < 9 ? "0"
								+ sie[i].getSensorFwBuild().getValue() : sie[i]
								.getSensorFwBuild().getValue()
								+ "") + "]");
				dummySensor.setId(instance.getId());
				modemDao.update(dummySensor);
			}
			// Not Exist
			else {
				modemDao.add(dummySensor);
				log.debug("Not Exist Sensor - mcuId["
						+ mcuId
						+ "] modemId["
						+ sie[i].getSensorID()
						+ "] hwVersion["
						+ sie[i].getSensorHwVersion().decodeVersion()
						+ "] fwVersion["
						+ sie[i].getSensorFwVersion().decodeVersion()
						+ "] fwBuild["
						+ (sie[i].getSensorFwBuild().getValue() < 9 ? "0"
								+ sie[i].getSensorFwBuild().getValue() : sie[i]
								.getSensorFwBuild().getValue()
								+ "") + "]");
			}
		}
	}


	public static ModemType getSensorType(String meterModel) {
		ModemType modemType = ModemType.ZEUPLS;

		if (meterModel.equals("Kamstrup 162")
				|| meterModel.equals("Kamstrup 382")
				|| meterModel.equals("Aidon 5520") || meterModel.equals("I210")
				|| meterModel.equals("kV2c") || meterModel.equals("Aidon 5540")
				|| meterModel.equals("Adion 5530")
				|| meterModel.equals("ADN5520") || meterModel.equals("ADN5530")
				|| meterModel.equals("ADN5540")
				|| meterModel.equals("K382B/K382C")
				|| meterModel.equals("K382D/K382E")
				|| meterModel.equals("K162B/K162C")
				|| meterModel.equals("K162D/K162E")
				|| meterModel.equals("K282B/K282C")
				|| meterModel.equals("K282D/K282E")
				|| meterModel.equals("SM110") || meterModel.equals("SM300")
				|| meterModel.equals("GE-SM-XXX") || meterModel.equals("I210P")
				|| meterModel.equals("LK3410CP_005")
				|| meterModel.equals("K382B/K382C( N.1)")				
				|| meterModel.equals("NJ34-212-THI")
				|| meterModel.equals("NJC Meter DLMSUA")
				|| meterModel.equals("K382D/K382E( N.1)")) {
			modemType = ModemType.ZRU;
		} else if (meterModel.equals("Elster M 140")
				|| meterModel.equals("Elster V 220")
				|| meterModel.equals("MD13")) {
			modemType = ModemType.ZEUPLS;
		} else if (meterModel.equals("REPEATER")) {
			modemType = ModemType.ZBRepeater;
		}
		// Arm or 250 is ZEU_MBUS SensorType
		else if (meterModel.equals("NAMR-H101MG")
				|| meterModel.equals("MSTR711")) {
			modemType = ModemType.ZEUMBus;
		}
		return modemType;
	}
}