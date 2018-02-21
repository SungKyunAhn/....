package com.aimir.fep.trap.actions.SP;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.MeterStatus;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.DeviceModelDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.Hex;
import com.aimir.model.device.EnergyMeter;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.GasMeter;
import com.aimir.model.device.HeatMeter;
import com.aimir.model.device.MCU;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.device.WaterMeter;
import com.aimir.model.system.Code;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.Location;
import com.aimir.notification.FMPTrap;

/**
 * Event ID : 220.1.0 evtInstallMeter
 *
 * @author Elevas Park
 * @version $Rev: 1 $, $Date: 2016-05-24 15:59:15 +0900 $,
 */
@Service
public class EV_SP_220_1_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_SP_220_1_0_Action.class);
    
    @Autowired
    MCUDao mcuDao;
    
    @Autowired
    LocationDao locationDao;
    
    @Autowired
    SupplierDao supplierDao;
    
    @Autowired
    CodeDao codeDao;
    
    @Autowired
    MeterDao meterDao;
    
    @Autowired
    ModemDao modemDao;
    
    @Autowired
    DeviceModelDao modelDao;
    
    /**
     * execute event action
     *
     * @param trap - FMP Trap(MCU Event)
     * @param event - Event Data
     */
    public void execute(FMPTrap trap, EventAlertLog event) throws Exception
    {
        log.debug("EventCode[" + trap.getCode()
                +"] MCU["+trap.getMcuId()+"]");

        // Initialize
        String mcuId = trap.getMcuId();
        String ipAddr = trap.getIpAddr();
        MCU mcu = mcuDao.get(mcuId);

        log.debug("IP[" + ipAddr + "]");
        String meterId = event.getEventAttrValue("meterId");
        log.debug("METER_ID[" + meterId + "]");
        String meterModel = event.getEventAttrValue("meterModel");
        log.debug("METER_MODEL[" + meterModel + "]");
        String meterVendor = event.getEventAttrValue("meterVendor");
        log.debug("METER_VENDOR[" + meterVendor + "]");
        int meterType = 1;
        String _meterType = event.getEventAttrValue("meterType");
        if (_meterType != null && !"".equals(_meterType)) meterType = Integer.parseInt(_meterType);
        log.debug("METER_TYPE[" + meterType + "]");
        int meterPhase = Integer.parseInt(event.getEventAttrValue("meterPhase"));
        log.debug("METER_PHASE[" + meterPhase + "]");
        String nodeKind = event.getEventAttrValue("moSPNodeKind");
        log.debug("NODE_KIND[" + nodeKind + "]");
        String fwVer =  Hex.decode(DataUtil.get2ByteToInt(Integer.parseInt(event.getEventAttrValue("moSPFwVer"))));
        fwVer = Double.parseDouble(fwVer.substring(0, 2) + "." + fwVer.substring(2, 4)) + "";
        log.debug("FW_VER[" + fwVer + "]");
        String fwBuild = event.getEventAttrValue("moSPFwBuild");
        log.debug("FW_BUILD[" + fwBuild + "]");
        String hwVer = Hex.decode(DataUtil.get2ByteToInt(Integer.parseInt(event.getEventAttrValue("moSPHwVer"))));
        hwVer = Double.parseDouble(hwVer.substring(0, 2) + "." + hwVer.substring(2, 4)) + "";
        log.debug("HW_VER[" + hwVer + "]");
        
        // if not mcu, it's is created and installed.
        Modem modem = modemDao.get(trap.getSourceId());
        Meter meter = meterDao.get(meterId);        
        if (meter == null) {
            /*
            try {
                EventUtil.sendEvent("Equipment Registration",
                                    TargetClass.MCU,
                                    mcuId,
                                    trap.getTimeStamp(),
                                    new String[][] {},
                                    event                
                );                
            }catch(Exception e) {
                log.error("can't send event["+e.getMessage()+"]",e);
            }
            */
            Code meterTypeCode  = null;
            switch (meterType) {
                case 1 : 
                    meter = new EnergyMeter();
                    meterTypeCode = CommonConstants.getMeterTypeByName("EnergyMeter");
                    break;
                case 2 :
                    meter = new GasMeter();
                    meterTypeCode = CommonConstants.getMeterTypeByName("GasMeter");
                    break;
                case 3 :
                    meter = new WaterMeter();
                    meterTypeCode = CommonConstants.getMeterTypeByName("WaterMeter");
                    break;
                case 6 :
                    meter = new HeatMeter();
                    meterTypeCode = CommonConstants.getMeterTypeByName("HeatMeter");
                    break;
            }
            // meter = new EnergyMeter();

            meter.setMdsId(meterId);
            meter.setMeterType(meterTypeCode);
            //TODO Vendor and model
            meter.setSupplier(mcu.getSupplier());
            //=> UPDATE START 2016.11.28 SP-384
            //meter.setLocation(mcu.getLocation());
            //Set Default Location            
            String defaultLocGeocoe = FMPProperty.getProperty("default.location.geocode");
            if(defaultLocGeocoe != null && !"".equals(defaultLocGeocoe)){
                Location defaultLocation = locationDao.findByCondition("geocode", defaultLocGeocoe);
                if(defaultLocation!=null) {
                    log.info("meter Set Default Location["+defaultLocation.getId()+"]");
                    meter.setLocation(defaultLocation);
                }else {
                    log.info("meter Default Location["+defaultLocGeocoe+"] is Not Exist In DB, Set First Location["+locationDao.getAll().get(0).getId()+"]");
                    meter.setLocation(mcu.getLocation());
                }
            }else{
                log.info("meter Default Location is Not Exist In Properties, Set First Location["+locationDao.getAll().get(0).getId()+"]");
                meter.setLocation(mcu.getLocation());
            }
            //=> UPDATE START 2016.11.28 SP-384
            meter.setInstallDate(trap.getTimeStamp());
            meter.setMeterStatus(CommonConstants.getMeterStatusByName(MeterStatus.NewRegistered.name()));
            // meter.setSwVersion(fwVer);	// DELETE SP-707
            
            // get model
            if (meterModel == null || "".equals(meterModel)) {
                meterModel = "MA105H";
            }
            
            DeviceModel model = modelDao.findByCondition("name", meterModel);
            if (model != null)
                meter.setModel(model);

            if (modem != null)
                meter.setModem(modem);

            meterDao.add(meter);
        }else{
            // 이미 미터가 있고, 모뎀이 다르면 교체 시나리오
            if (modem != null && meter.getModem() != null 
                    && !modem.getDeviceSerial().equals(meter.getModem().getDeviceSerial())) {
                // meter.setSwVersion(fwVer);	// DELETE SP-707
                meter.setModem(modem);
                // INSERT START 2016.12.07 SP-400
                if (meterModel != null || ! "".equals(meterModel)) {
                	DeviceModel model = modelDao.findByCondition("name", meterModel);
	                if (model != null)
	                    meter.setModel(model);
                }
                // INSERT END   2016.12.07 SP-400
                meterDao.update(meter);
            }
        }
        
        // 미터 인스톨로 올라온 것은 마스터이기 때문에 모뎀에 두개의 마스터 미터가 있을 수가 없다.
        // 따라서, 다른 마스터 미터를 찾아서 모뎀과의 연결을 해제한다.
        if (modem != null) {
            // 만약 모뎀이 이미 미터를 가지고 있으면 모뎀 포트가 0인 것만 새로 설치된 미터로 교체한다.
            Set<Meter> meters = new HashSet<Meter>();
            Set<Meter> _modemMeters = modem.getMeter();
            boolean isExist = false;
            for (Meter m : _modemMeters) {
                if (!m.getMdsId().equals(meter.getMdsId())) {
                    if (m.getModemPort() == null || m.getModemPort() == 0)
                        continue;
                }
                else
                    isExist = true;
                
                meters.add(m);
            }
            if (!isExist) meters.add(meter);
            
            modem.setMeter(meters);
            modemDao.update(modem);
        }
        
        event.setActivatorId(meter.getMdsId());
        event.setActivatorType(TargetClass.EnergyMeter);
        event.setSupplier(meter.getSupplier());
        event.setLocation(meter.getLocation());
    
        log.debug("Meter Install Event Action Compelte");
    }
}
