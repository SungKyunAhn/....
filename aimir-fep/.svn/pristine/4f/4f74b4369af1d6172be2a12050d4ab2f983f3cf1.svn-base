package com.aimir.fep.trap.actions;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.DataSVC;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.EventUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.model.device.EnergyMeter;
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
import com.aimir.model.device.ZEUPLS;
import com.aimir.model.device.ZRU;
import com.aimir.model.system.Code;
import com.aimir.model.system.DeviceVendor;
import com.aimir.notification.FMPTrap;
import com.aimir.util.DateTimeUtil;

/**
 * Event ID : 203.101.0
 *
 * @author J.S Park
 * @version $Rev: 1 $, $Date: 2007-05-30 15:59:15 +0900 $,
 */
@Component
public class EV_203_101_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_203_101_0_Action.class);
    
    @Resource(name="transactionManager")
    JpaTransactionManager txmanager;
    
    @Autowired
    MCUDao mcuDao;
    
    @Autowired
    ModemDao modemDao;
    
    @Autowired
    MeterDao meterDao;
    
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
        log.debug("EventName[eventSensorJoin] "+" EventCode[" + trap.getCode()+"] MCU["+trap.getMcuId()+"]");

        TransactionStatus txstatus = null;
        DeviceVendor devicevendor = null;
        String mcuId = trap.getMcuId();
        String meterId = event.getEventAttrValue("mdsId");
        try {
            txstatus = txmanager.getTransaction(null);
            
            MCU mcu = mcuDao.get(mcuId);
            
            String newModemId = event.getEventAttrValue("sensorID");
            ModemType modemType = CommonConstants.getModemType(Integer.parseInt(event.getEventAttrValue("modemType")));
            String installedDate = event.getEventAttrValue("sensorInstallDate");
            int svcType = Integer.parseInt(event.getEventAttrValue("sensorServiceType"));
            // int sensorIdx = Integer.parseInt(event.getEventAttrValue("byteEntry"));
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
            
            // get modem
            Modem modem = modemDao.get(newModemId);
            
            if (modem == null) {
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
                modem.setDeviceSerial(newModemId);
                modem.setMcu(mcu);
                modem.setInstallDate(DateTimeUtil.getDST(null, installedDate));
                if(mcu == null || mcu.getSupplier() == null){
                	// GPRS모뎀인 경우 집중기 정보가 없기때문에 디폴트 공급사 정보를 설정한다.
                	modem.setSupplier(supplierDao.getAll().get(0));
                }else {
                    modem.setSupplier(mcu.getSupplier());
                }

                // TODO modem.setDeviceModel();
                modem.setModemType(modemType.name());
                modemDao.add(modem);
                
                EventUtil.sendEvent("Equipment Registration",
                        TargetClass.Modem,
                        newModemId,
                        trap.getTimeStamp(), new String[][] {},
                        event);
            }
            else {
                if (!mcu.getSysID().equals(modem.getMcu().getSysID()))
                    modem.setMcu(mcu);
            }
            
            String strModemPort = event.getEventAttrValue("modemPort");
            if (strModemPort == null || "".equals(strModemPort))
                strModemPort = "0";
            int modemPort = Integer.parseInt(strModemPort);
            
            if (meterId != null && !"".equals(meterId)) {
                // 모뎀의 0포트에 해당하는 미터를 가져와서 meterId가 다르면 미터의 모뎀 관계를 없애야 한다.
                Meter meter = meterDao.getMeterByModemDeviceSerial(newModemId, modemPort);
                if (meter != null && !meter.getMdsId().equals(meterId)) {
                    meter.setModem(null);
                }
                
                // 미터의 모뎀과 입력받은 모뎀이 다르면 관계를 생성한다.
                meter = meterDao.get(meterId);
                if (meter != null) {
                    if (!modem.getDeviceSerial().equals(meter.getModem().getDeviceSerial())) {
                        meter.setModem(modem);
                        meter.setModemPort(modemPort);
                    }
                }
                else {
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
                    // TODO meter.setDeviceModel();
                    // TODO meter.setLpInterval();
                    // TODO meter.setPulseConstants();
                    meterDao.add(meter);
                    
                    EventUtil.sendEvent("Equipment Registration",
                            TargetClass.valueOf(meterType.name()),
                            meterId,
                            trap.getTimeStamp(), new String[][] {},
                            event);
                }
                
                devicevendor = meter.getModel().getDeviceVendor();
            }
            else log.warn("Meter of Modem[" + newModemId + "] is NULL!");
            
            event.setMessage(EventUtil.buildEventMessage(event, "join in a MCU[" + mcuId + "]"));
        }
        catch (Exception e) {
            throw e;
        }
        finally {
            if (txstatus != null) txmanager.commit(txstatus);
        }
        
        if (devicevendor != null && devicevendor.getName().equals("GE")) {
            CommandGW gw = new CommandGW();
            gw.cmdMeterTimeSync(mcuId, meterId);
            // a lastmodifieddate of meter should be updated with return value.
        }
    }
    
    private void setSensorConfig(CommandGW gw, String mcuId, int modemType, String sensorId)
    throws Exception
    {
        int alarmFlag = Integer.parseInt(FMPProperty.getProperty("aimir.join.zeupls.defAlarmflag"));
        int lpPeriod = Integer.parseInt(FMPProperty.getProperty("aimir.join.zeupls.defLPPeriod"));
        int testFlag = Integer.parseInt(FMPProperty.getProperty("aimir.join.zru.defTestflag"));
        //TODO IMPLEMENT
        /*
        if(modemType==AimirModel.ZRU)
        {
            if (testFlag != -1) {
                byte[] val = new byte[] { DataUtil.getByteToInt(testFlag) };
                gw.cmdSetSensorROM(mcuId, sensorId, SensorROM.OFFSET_TEST_FLAG, val);
            }
        }else if(modemType==AimirModel.ZEU_PLS)
        {
            SensorCommandData data = new SensorCommandData();
            byte[] val = null;
            if (lpPeriod != 0) {
                data.setCmdType(SensorCommandData.CMD_TYPE_LP_PERIOD);
                val = new byte[] { DataUtil.getByteToInt(lpPeriod) };
                data.setData(val);
                if (data.getCmdType()!=(byte)0x99)
                {
                    log.debug("cmdType[" + Hex.decode(new byte[]{data.getCmdType()}) + "] DataStream["
                              + Hex.decode(data.getData()) + "]");
                    gw.cmdCommandSensor(mcuId, sensorId, data.getCmdType(), data.getData());
                    data.setCmdType(data.CMD_TYPE_NONE);
                }
            }
            if (alarmFlag != -1) {
                data.setCmdType(SensorCommandData.CMD_TYPE_ALARM_FLAG);
                val = new byte[] { DataUtil.getByteToInt(alarmFlag) };
                data.setData(val);
                if (data.getCmdType()!=(byte)0x99)
                {
                    log.debug("cmdType[" + Hex.decode(new byte[]{data.getCmdType()}) + "] DataStream["
                              + Hex.decode(data.getData()) + "]");
                    gw.cmdCommandSensor(mcuId, sensorId, data.getCmdType(), data.getData());
                }
            }
        }
        */
            
    }
    
}
