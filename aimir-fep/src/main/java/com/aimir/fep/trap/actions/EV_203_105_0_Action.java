package com.aimir.fep.trap.actions;

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
import com.aimir.constants.CommonConstants.DataSVC;
import com.aimir.constants.CommonConstants.HomeDeviceCategoryType;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.constants.CommonConstants.ModemNetworkType;
import com.aimir.constants.CommonConstants.ModemPowerType;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.ChangeLogDao;
import com.aimir.dao.device.EndDeviceDao;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.DeviceModelDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.modem.EventLog;
import com.aimir.fep.protocol.fmp.frame.service.entry.sensorInfoNewEntry;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.EventUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.model.device.EnergyMeter;
import com.aimir.model.device.EventAlertAttr;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.GasMeter;
import com.aimir.model.device.HeatMeter;
import com.aimir.model.device.IEIU;
import com.aimir.model.device.MCU;
import com.aimir.model.device.MMIU;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.device.VolumeCorrector;
import com.aimir.model.device.WaterMeter;
import com.aimir.model.device.ZBRepeater;
import com.aimir.model.device.ZEUPLS;
import com.aimir.model.device.ZRU;
import com.aimir.model.system.Code;
import com.aimir.model.system.DeviceModel;
import com.aimir.notification.FMPTrap;
import com.aimir.util.DateTimeUtil;

/**
 * Event ID : 203.105.0 (Sensor Install)
 * <br>MCU
 *
 * @author J.S Park
 * @version $Rev: 1 $, $Date: 2007-05-30 15:59:15 +0900 $,
 */
@Component
public class EV_203_105_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_203_105_0_Action.class);
    
    @Resource(name="transactionManager")
    JpaTransactionManager txmanager;
    
    @Autowired
    MCUDao mcuDao;
    
    @Autowired
    ModemDao modemDao;
    
    @Autowired
    MeterDao meterDao;
    
    @Autowired
    EndDeviceDao endDeviceDao;
    
    @Autowired
    CodeDao codeDao;
    
    @Autowired
    CommandGW commandGW;
    
    @Autowired
    ChangeLogDao clDao;
    
    @Autowired
    DeviceModelDao dmDao;
    
    @Autowired
    SupplierDao supplierDao;
    
    /**
     * execute event action
     *
     * @param trap - FMP Trap(MCU Event)
     * @param event - Event Alert Log Data
     */
    public void execute(FMPTrap trap, EventAlertLog event) throws Exception
    {
        log.debug("EventName[eventSensorInstall] "+" EventCode[" + trap.getCode()+"] MCU["+trap.getMcuId()+"]");
        
        TransactionStatus txstatus = null;
        try {
            txstatus = txmanager.getTransaction(null);
            
            String mcuId = trap.getMcuId();
            MCU mcu = mcuDao.get(mcuId);
            String newModemId = event.getEventAttrValue("sensorID");
            ModemType modemType = CommonConstants.getModemType(Integer.parseInt(event.getEventAttrValue("sensorType")));
            String installedDate = trap.getTimeStamp();
            int svcType = Integer.parseInt(event.getEventAttrValue("sensorServiceType"));
            String meterId = event.getEventAttrValue("mlpMid");
            
            EventAlertAttr ea = EventUtil.makeEventAlertAttr("message",
                    "java.lang.String",
                    "ModemType[" + modemType + "] SVCType[" + svcType + "] MeterSerial[" + meterId + "]");
            event.append(ea);

            ModemPowerType powerType = ModemPowerType.Battery;
            ModemNetworkType networkType = ModemNetworkType.FFD;
    
            if(event.getEventAttrValue("sensorAttr")!=null && event.getEventAttrValue("sensorAttr").length()>0){
                int sensorAttr = Integer.parseInt(event.getEventAttrValue("sensorAttr"));
    
            	//power Type
            	powerType = CommonConstants.getModemPowerType(sensorAttr);
            	networkType = CommonConstants.getModemNetworkType(sensorAttr);
            	log.debug("sensorAttr: "+sensorAttr+" powerType: "+powerType+" networkType: "+networkType);
            }
    
            MeterType meterType = null;
            
            Code svcTypeCode = CommonConstants.getDataSvc(svcType+"");
            if (DataSVC.Electricity.name().equals(svcTypeCode.getName()))
                meterType = MeterType.EnergyMeter;
            else if (DataSVC.Gas.name().equals(svcTypeCode.getName()))
                meterType = MeterType.GasMeter;
            else if (DataSVC.Water.name().equals(svcTypeCode.getName()))
                meterType = MeterType.WaterMeter;
            else if (DataSVC.Heating.name().equals(svcTypeCode.getName()))
                meterType = MeterType.HeatMeter;
            else if (DataSVC.Volume.name().equals(svcTypeCode.getName()))
                meterType = MeterType.VolumeCorrector;
    
            EventAlertAttr[] eventAttr = event.getEventAlertAttrs().toArray(new EventAlertAttr[0]);
    
            List<String> str = new ArrayList<String>();
            for (int i = 0; i < eventAttr.length; i++) {
                if (eventAttr[i].getAttrName().indexOf("stringEntry") != -1) {
    				str.add(eventAttr[i].getValue());
    			}
            }
            String str1 = str.get(0);
            String str2 = str.get(1);
            String vendor = "";
            String vendorList = FMPProperty.getProperty("vendor");
    
            if (vendorList.indexOf(str1) != -1) {
    			vendor = str1 + " " + str2;
    		}
    		else {
    			vendor = str2 + " " + str1;
    		}
    
            if (vendor.indexOf("REPEATER") != -1) {
                modemType = ModemType.ZBRepeater;
            }

         // get modem
            Modem modem = modemDao.get(newModemId);
            Meter meter = null;
            if (modem == null) {
                List<DeviceModel> dmList = null;
                // TODO 전기의 경우 계량기 모델에 따라서 모뎀 모델도 달라진다. 
                if (modemType == ModemType.ZRU)
                    dmList = dmDao.getDeviceModelByName(mcu.getSupplier().getId(), FMPProperty.getProperty("install.modem.zru.model.name"));
                else if (modemType == ModemType.ZBRepeater)
                    dmList = dmDao.getDeviceModelByName(mcu.getSupplier().getId(), FMPProperty.getProperty("install.modem.zbrepeater.model.name"));
                else if (modemType == ModemType.ACD)
                    dmList = dmDao.getDeviceModelByName(mcu.getSupplier().getId(), FMPProperty.getProperty("install.modem.acd.model.name"));
                else if (modemType == ModemType.HMU)
                    dmList = dmDao.getDeviceModelByName(mcu.getSupplier().getId(), FMPProperty.getProperty("install.modem.hmu.model.name"));
                else if (modemType == ModemType.ZEUMBus)
                    dmList = dmDao.getDeviceModelByName(mcu.getSupplier().getId(), FMPProperty.getProperty("install.modem.zeumbus.model.name"));
                else if (modemType == ModemType.ZEUPLS) {
                    if (meterType == MeterType.EnergyMeter)
                        dmList = dmDao.getDeviceModelByName(mcu.getSupplier().getId(), FMPProperty.getProperty("install.modem.zeupls.energy.model.name"));
                    else if (meterType == MeterType.GasMeter)
                        dmList = dmDao.getDeviceModelByName(mcu.getSupplier().getId(), FMPProperty.getProperty("install.modem.zeupls.gas.model.name"));
                    else if (meterType == MeterType.WaterMeter)
                        dmList = dmDao.getDeviceModelByName(mcu.getSupplier().getId(), FMPProperty.getProperty("install.modem.zeupls.water.model.name"));
                }
                
                if (modemType == ModemType.ZBRepeater) {
                    modem = new ZBRepeater();
                }
                else {
                    switch (meterType) {
                    case EnergyMeter :
                        modem = new ZRU();
                        break;
                    case GasMeter :
                    case WaterMeter :
                        modem = new ZEUPLS();
                        break;
                    case HeatMeter :
                        modem = new IEIU();
                        break;
                    case VolumeCorrector :
                        modem = new MMIU();
                        break;
                    }
                }
                
                modem.setDeviceSerial(newModemId);
                modem.setMcu(mcu);
                modem.setInstallDate(DateTimeUtil.getDST(null, installedDate));
                if(mcu == null || mcu.getSupplier() == null){
                	// GPRS모뎀인 경우 집중기 정보가 없기때문에 디폴트 공급사 정보를 설정한다.
                	modem.setSupplier(supplierDao.getAll().get(0));
                }else {
                    modem.setSupplier(mcu.getSupplier());
                }

                modem.setModemType(modemType.name());
                modem.setLocation(mcu.getLocation());
                
                if (dmList != null && dmList.size() == 1)
                    modem.setModel(dmList.get(0));
                
                modemDao.add(modem);
                
                /*
                EventUtil.sendEvent("Equipment Registration",
                        TargetClass.Modem,
                        newModemId,
                        installedDate, new String[][] {},
                        event);
                        */
            }
            else {
                if (modem.getMcu() != null && !mcu.getSysID().equals(modem.getMcu().getSysID())) {
                    // 실패하더라도 경고만 출력하고 계속 진행할 수 있도록 한다.
                    try {
                        // commandGW.cmdDeleteModem(modem.getMcu().getSysID(), newModemId);
                        // 큐로 던지고 나중에 처리하는 것으로 변경
                    }
                    catch (Exception e) {
                        log.warn(e);
                    }
                    modem.setMcu(mcu);
                }
                else if (modem.getMcu() == null) {
                    modem.setMcu(mcu);
                }
            }
            
            String strModemPort = event.getEventAttrValue("modemPort");
            if (strModemPort == null || "".equals(strModemPort))
                strModemPort = "0";
            int modemPort = Integer.parseInt(strModemPort);
            
            if (meterId != null && !"".equals(meterId)) {
                // 모뎀의 0포트에 해당하는 미터를 가져와서 meterId가 다르면 미터의 모뎀 관계를 없애야 한다.
                meter = meterDao.getMeterByModemDeviceSerial(newModemId, modemPort);
                
                if (meter != null && !meter.getMdsId().equals(meterId)) {
                    meter.setModem(null);
                }
                
                // 미터의 모뎀과 입력받은 모뎀이 다르면 관계를 생성한다.
                meter = meterDao.get(meterId);
                if (meter != null) {
                    if (!modem.getDeviceSerial().equals(meter.getModem().getDeviceSerial())) {
                        meter.setModem(modem);
                        
                        if (meter.getModemPort() != null && meter.getModemPort() != modemPort) {
                            meter.setModemPort(modemPort);
                        }
                    }
                }
                else {
                    // modemType으로 pulseConst를 유추한다.
                    double pulseConstant = 1.0;
                    if (modemType == ModemType.ZEUPLS)
                        pulseConstant = 100.0;
                    else if (modemType == ModemType.ZRU)
                        pulseConstant = 0.01;
                    
                    switch (meterType) {
                    case EnergyMeter : 
                        meter = new EnergyMeter();
                        break;
                    case GasMeter :
                        meter = new GasMeter();
                        break;
                    case WaterMeter :
                        meter = new WaterMeter();
                        break;
                    case HeatMeter :
                        meter = new HeatMeter();
                        break;
                    case VolumeCorrector :
                        meter = new VolumeCorrector();
                    }
                    
                    meter.setMdsId(meterId);
                    meter.setInstallDate(DateTimeUtil.getDST(null, installedDate));
                    meter.setMeterType(CommonConstants.getMeterTypeByName(meterType.name()));
                    meter.setLocation(mcu.getLocation());
                    meter.setSupplier(mcu.getSupplier());
                    meter.setModem(modem);
                    meter.setModemPort(modemPort);
                    meter.setPulseConstant(pulseConstant);
                    // TODO meter.setDeviceModel();
                    // TODO meter.setLpInterval();
                    meterDao.add(meter);
                    
                    EventUtil.sendEvent("Equipment Registration",
                            TargetClass.valueOf(meterType.name()),
                            meterId,
                            installedDate, new String[][] {},
                            event);
                }
                
                /*
                if (meter.getModel() != null) {
                    DeviceVendor devicevendor = meter.getModel().getDeviceVendor();
                    if (devicevendor != null && devicevendor.getName().equals("GE")) {
                        meter.setWriteDate("");
                        // commandGW.cmdMeterTimeSync(mcuId, meterId);
                        // a lastmodifieddate of meter should be updated with return value.
                    }
                }
                else {
                    log.warn("Meter[" + meter.getMdsId() + "] model is NULL!!");
                }
                */
            }
            else log.warn("Meter of Modem[" + newModemId + "] is NULL!");
            
            // send install alarm unit
            // 2009.11.26 by jspark
            boolean sendToAlarmSW = Boolean.parseBoolean(FMPProperty.getProperty("send.to.alarmsw"));
            if (modemType == ModemType.ZMU && sendToAlarmSW) {
                try {
                    EventUtil.sendEvent("Equipment Registration",
                                    TargetClass.Modem,
                                    newModemId,
                                    installedDate,
                                    new String[][] {},
                                    event);
                }
                catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
            
            //Set powerType and networkType of modem
            if (modem instanceof ZEUPLS) {
                ((ZEUPLS)modem).setPowerType(powerType.name());
                ((ZEUPLS)modem).setNetworkType(networkType.name());
            }
            
            // update install status of the EndDevice if modem type is acd
            if(modemType == ModemType.ACD && newModemId.length() > 0) {
            	endDeviceDao.updateEndDeviceInstallStatus(codeDao.getCodeIdByCode(Code.COMPLETED)
            											, newModemId
            											, codeDao.getCodeIdByCode(HomeDeviceCategoryType.SMART_CONCENT.getCode()));

            	// mcuID가 다를경우 modem 갱신
            	modemDao.saveOrUpdate(modem);           	
            }

            boolean scanning = Boolean.parseBoolean(FMPProperty.getProperty("install.unit.scanning"));

            if(scanning){
                log.warn("Implement unit scanning command");
	            // TODO execute unit scanning
                /*
	            try{
	                log.debug("start sensor install unit scanning,"+newModemId);
	                if(newSensorInst.getClassName().equals(AimirModel.MI_ZRU))
	                {
	                    CmdOperationUtil.doZRUScanning(newSensorInst, 1, "event");
	                }
	                else if(newSensorInst.getClassName().equals(AimirModel.MI_ZMU))
	                {
	                    CmdOperationUtil.doZMUScanning(newSensorInst, 1, "event");
	                }
	                else if(newSensorInst.getClassName().equals(AimirModel.MI_ZEUPLS))
	                {
	                    CmdOperationUtil.doZEUPLSScanning(newSensorInst, 1, "event");
	                }
	                else if(newSensorInst.getClassName().equals(AimirModel.MI_ZBREPEATER))
	                {
	                    CmdOperationUtil.doRepeaterScanning(newSensorInst, 1, "event");
	                }
	                else
	                {
	                    CmdOperationUtil.doZRUScanning(newSensorInst, 1, "event");
	                }
	                log.debug("end sensor install unit scanning,"+newModemId);
	            }
	            catch(Exception e){
	                log.error(e.getMessage());
	            }
	            */
            }

            event.setActivatorId(newModemId);
            event.setActivatorType(TargetClass.Modem);
            event.append(EventUtil.makeEventAlertAttr("mcuID",
                                                 "java.lang.String", mcuId));
            event.append(EventUtil.makeEventAlertAttr("message",
                                                 "java.lang.String",
                                                 "Modem is connected with a Meter[" + vendor + "," + meterId + "]"));
            
            // setModemConfig(modem, meter);
        }
        catch (Exception e) {
            throw e;
        }
        finally {
            if (txstatus != null) txmanager.commit(txstatus);
        }
    }

    private void setModemConfig(Modem modem, Meter meter)
    {
        try {
        	sensorInfoNewEntry entry = commandGW.cmdGetModemInfoNew(modem.getDeviceSerial());
        	byte[] bFW = entry.getSensorFwVersion().encode();
            DataUtil.convertEndian(bFW);
            String fw = String.format("%d.%d", (int)bFW[0], (int)bFW[1]);
            byte[] bHW = entry.getSensorHwVersion().encode();
            DataUtil.convertEndian(bHW);
            String hw = String.format("%d.%d", (int)bHW[0], (int)bHW[1]);
            int build = entry.getSensorFwBuild().getValue();
            String otaLastUpdate = entry.getSensorLastOTATime().getValue();
            String meterModel = new String(entry.getSensorModel().getValue()).trim();
        	
            modem.setFwVer(fw);
            modem.setHwVer(hw);
            modem.setFwRevision(build+"");
            modemDao.saveOrUpdate(modem);
            
            if (meter != null) {
                // Kamstrup의 경우 fw가 3.0 이상인 경우 신규 포맷을 적용해야 되기 때문에 v2 모델을 가져오도록 예외로 처리한다.
                if (Double.parseDouble(fw) >= 3.0) {
                    String _meterModel = null;
                    if (meterModel.contains("K382M")) {
                        _meterModel = "K382M";
                    }
                    else if (meterModel.contains("K351C")) {
                        _meterModel = "K351C";
                    }
                    else if (meterModel.contains("K162M")) {
                        _meterModel = "K162M";
                    }
                    _meterModel += "v2";
                    
                    List<DeviceModel> models = dmDao.getDeviceModelByName(meter.getSupplierId(), _meterModel);
                    if (models.size() == 1) {
                        meter.setModel(models.get(0));
                        meterDao.saveOrUpdate(meter);
                        return;
                    }
                }
                List<DeviceModel> models = dmDao.getAll();
                for (DeviceModel m : models.toArray(new DeviceModel[0])) {
                    if (meterModel.contains(m.getName())) {
                        meter.setModel(m);
                        meterDao.saveOrUpdate(meter);
                        break;
                    }
                }
            }
        }
        catch (Exception e) {
            log.warn(e, e);
        }
    }

}
