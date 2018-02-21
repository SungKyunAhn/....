package com.aimir.fep.trap.actions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.dao.device.AsyncCommandLogDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.fep.modem.AmrData;
import com.aimir.fep.modem.ModemNetwork;
import com.aimir.fep.modem.ModemNode;
import com.aimir.fep.modem.ModemROM;
import com.aimir.fep.protocol.fmp.datatype.FMPVariable;
import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.CmdUtil;
import com.aimir.fep.util.EventUtil;
import com.aimir.fep.util.Hex;
import com.aimir.model.device.AsyncCommandLog;
import com.aimir.model.device.AsyncCommandParam;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.device.ZBRepeater;
import com.aimir.model.device.ZEUMBus;
import com.aimir.model.device.ZEUPLS;
import com.aimir.model.device.ZRU;
import com.aimir.notification.FMPTrap;
import com.aimir.util.Condition;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.Condition.Restriction;

/**
 * Event ID : 215.1.0 Processing Class
 *
 * @author J.S Park
 * @version $Rev: 1 $, $Date: 2009-04-16 11:59:15 +0900 $,
 */
@Component
public class EV_215_1_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_215_1_0_Action.class);

    @Autowired
    AsyncCommandLogDao asyncCommandLogDao;
    @Autowired
    ModemDao modemDao;
    @Autowired
    MeterDao meterDao;

    /**
     * execute event action
     *
     * @param trap - FMP Trap(MCU Event)
     * @param event - Event Alert Log Data
     */
    public void execute(FMPTrap trap, EventAlertLog event) throws Exception
    {
        log.debug("EventName[eventTransaction] "
                +" EventCode[" + trap.getCode()
                +"] MCU["+trap.getMcuId()+"]");

        String trId = event.getEventAttrValue("wordEntry");
        
        if(trId != null){
        	log.debug("trId["+trId+"]");
        }
        int eventType = Integer.parseInt(event.getEventAttrValue("byteEntry"));
        int transactionOption = Integer.parseInt(event.getEventAttrValue("byteEntry.2"));
        String createTime = event.getEventAttrValue("timeEntry");
        String lastTime = event.getEventAttrValue("timeEntry.4");
        int errorCode = Integer.parseInt(event.getEventAttrValue("intEntry"));
        int resultCnt = Integer.parseInt(event.getEventAttrValue("uintEntry"));
        String[] oid = new String[resultCnt];
        int[] resLength = new int[resultCnt];
        FMPVariable[] resData = new FMPVariable[resultCnt];

        for (int i = 0; i < resultCnt; i++) {
            oid[i] = event.getEventAttrValue("oidEntry" + (i==0? "":"."+(i*3+7)));
            resLength[i] = Integer.parseInt(event.getEventAttrValue("intEntry." + (i*3+8)));
            resData[i] = (FMPVariable)trap.getVarBinds().get("1.12.0" + (i==0? "":"."+(i*3+9)));
        }

        CmdUtil.updateAyncTr(Long.parseLong(trId), trap.getMcuId(), 0, 0, 0, createTime, lastTime,
                CommonConstants.TR_STATE.Terminate.getCode(), errorCode, eventType,
                resultCnt, oid, resLength, resData);

        Map<String, String> map = getTr(Long.parseLong(trId), trap.getMcuId());
        
        if(map == null) {
        	return;
        }
        String command = (String)map.get("command");
        if (command.equals("cmdGetSensorROM")) {
            saveSensorROM(Long.parseLong(trId), trap.getMcuId(), (String)map.get("deviceId"), oid, resData);
        }
        else if (command.equals("cmdOnDemandMeter")) {
            saveOnDemandMeter(trap.getMcuId(), (String)map.get("deviceId"), resData);
        }
        
        event.append(EventUtil.makeEventAlertAttr("trID", "java.lang.String", trId));
        event.append(EventUtil.makeEventAlertAttr("eventType",
                "java.lang.String",
                CommonConstants.getTrEvent(eventType).name()));
        String trOptionName = "";
        CommonConstants.TR_OPTION[] trOptions = CommonConstants.getTrOption(transactionOption);
        for (int i = 0; i < trOptions.length; i++) {
            if (i != 0)
                trOptionName += ",";
            trOptionName += trOptions[i].name();
        }
        
        /*
        event.append(EventUtil.makeEventAttr("trOption",
                "java.lang.String", trOptionName));
        event.append(EventUtil.makeEventAttr("createTime", "java.lang.String", TimeZoneDST.getYYYYMMDDHHMMSS(createTime, null)));
        event.append(EventUtil.makeEventAttr("lastTime", "java.lang.String", TimeZoneDST.getYYYYMMDDHHMMSS(lastTime, null)));
        event.append(EventUtil.makeEventAttr("error", "java.lang.String", ErrorCode.getMessage(errorCode)));
        event.append(EventUtil.makeEventAttr("resultCnt", "java.lang.String", ""+resultCnt));
        event.append(EventUtil.makeEventAttr("command", "java.lang.String", map.get("command")));
        event.append(EventUtil.makeEventAttr("deviceType", "java.lang.String", map.get("deviceType")));
        event.append(EventUtil.makeEventAttr("deviceId", "java.lang.String", map.get("deviceId")));
        
        */
    }
    
    private Map<String, String> getTr(long trId, String mcuId)
    throws Exception {

		Set<Condition> set = new HashSet<Condition>();
		Condition cdt1 = new Condition("id.trId", new Object[] { trId }, null, Restriction.EQ);
		set.add(cdt1);
		Condition cdt2 = new Condition("id.mcuId", new Object[] { mcuId }, null,Restriction.EQ);
		set.add(cdt2);
    	List<AsyncCommandLog> asyncCommandLog = asyncCommandLogDao.findByConditions(set);

        Map<String, String> map = new HashMap<String, String>();
        if (asyncCommandLog != null && asyncCommandLog.size() > 0) {
            map.put("deviceType", asyncCommandLog.get(0).getDeviceType());
            map.put("deviceId", asyncCommandLog.get(0).getDeviceId());
            map.put("command", asyncCommandLog.get(0).getCommand());
        }
        return map;

    }
    
    private void saveSensorROM(long trId, String mcuId, String modemId, String[] oid, FMPVariable[] data)
    throws Exception {

        Modem modem = modemDao.get(modemId);
        if(modem == null){
        	return;
        }
        ModemType modemType = modem.getModemType();
        String fwVersion = modem.getFwVer();
        String fwBuild = modem.getFwRevision();

        if (fwVersion == null || "".equals(fwVersion)) {
            fwVersion = "1.0";
        }
        if (fwVersion == null || "".equals(fwVersion)) {
            fwVersion = "1";
        }
        
        ModemROM modemROM = new ModemROM(fwVersion, fwBuild);
        /*
        if (modemType.equals(ModemType.ZRU)){
        	modemROM = new ZRUROM(fwVersion, fwBuild);
        }
        else if (modemType.equals(ModemType.ZEUPLS)) {
        	modemROM = new ZEUPLSROM(fwVersion, fwBuild);
        	
        	ModemNetworkType networkType = CommonConstants.getModemNetworkType(modemROM.getNetworkType());
        	((ZEUPLS)modem).setNetworkType(networkType.name());
        }
        else if (modemType.equals(ModemType.ZMU)) {
        	modemROM = new ZMUROM(fwVersion, fwBuild);
        }
        else if (modemType.equals(ModemType.ZBRepeater)) {
        	modemROM = new ZEUPLSROM(fwVersion, fwBuild);
        	ModemNetworkType networkType = CommonConstants.getModemNetworkType(modemROM.getNetworkType());
        	((ZBRepeater)modem).setNetworkType(networkType.name());
        }
        */
        
		Set<Condition> set = new HashSet<Condition>();
		Condition cdt1 = new Condition("id.trId", new Object[] { trId }, null, Restriction.EQ);
		set.add(cdt1);
		Condition cdt2 = new Condition("id.mcuId", new Object[] { mcuId }, null,Restriction.EQ);
		set.add(cdt2);
    	List<AsyncCommandLog> asyncCommandLog = asyncCommandLogDao.findByConditions(set);
    	
    	if(asyncCommandLog == null || asyncCommandLog.size() < 1){
    		return;
    	}
    	List<?> asyncParams = asyncCommandLog.get(0).getParams();
    	Iterator<?> it = asyncParams.iterator();
        int[] args = new int[data.length];
    	for(int i = 0; it != null && it.hasNext(); i++){
    		AsyncCommandParam param = (AsyncCommandParam) it.next();
            if (param.getNum() == (2+(2*i)))
                args[i++] = Integer.parseInt(param.getParamValue()); 
    	}

        for (int i = 0; i < args.length; i++) {
            log.debug(Hex.decode(((OCTET)data[i]).getValue()));
            modemROM.parse(args[i], ((OCTET)data[i]).getValue());
        }
        ModemNetwork sn = modemROM.getModemNetwork();
        ModemNode sNode = modemROM.getModemNode();
        AmrData amrData = modemROM.getAmrData();
  

        // install date
        if (modem.getInstallDate() == null ||
                "".equals(modem.getInstallDate())) {
          
            modem.setInstallDate(DateTimeUtil.getCurrentDateTimeByFormat(null));
        }
        
        if(sn != null){

        	if(modem.getModemType().equals(ModemType.ZRU)){
            	((ZRU)modem).setChannelId(sn.getChannel());
            	((ZRU)modem).setManualEnable((sn.getManualEnable()==255? false:true));
            	((ZRU)modem).setPanId(sn.getPanId());
            	((ZRU)modem).setSecurityEnable((sn.getSecurityEnable()==255? false:true));
            	((ZRU)modem).setLinkKey(sn.getLinkKey());
            	((ZRU)modem).setNetworkKey(sn.getNetworkKey());
            	((ZRU)modem).setExtPanId(sn.getExtPanId());
        	}
        	if(modem.getModemType().equals(ModemType.ZEUPLS)){
            	((ZEUPLS)modem).setChannelId(sn.getChannel());
            	((ZEUPLS)modem).setManualEnable((sn.getManualEnable()==255? false:true));
            	((ZEUPLS)modem).setPanId(sn.getPanId());
            	((ZEUPLS)modem).setSecurityEnable((sn.getSecurityEnable()==255? false:true));
            	((ZEUPLS)modem).setLinkKey(sn.getLinkKey());
            	((ZEUPLS)modem).setNetworkKey(sn.getNetworkKey());
            	((ZEUPLS)modem).setExtPanId(sn.getExtPanId());
        	}
        	if(modem.getModemType().equals(ModemType.ZBRepeater)){
            	((ZBRepeater)modem).setChannelId(sn.getChannel());
            	((ZBRepeater)modem).setManualEnable((sn.getManualEnable()==255? false:true));
            	((ZBRepeater)modem).setPanId(sn.getPanId());
            	((ZBRepeater)modem).setSecurityEnable((sn.getSecurityEnable()==255? false:true));
            	((ZBRepeater)modem).setLinkKey(sn.getLinkKey());
            	((ZBRepeater)modem).setNetworkKey(sn.getNetworkKey());
            	((ZBRepeater)modem).setExtPanId(sn.getExtPanId());
        	}
        	if(modem.getModemType().equals(ModemType.ZEUMBus)){
            	((ZEUMBus)modem).setChannelId(sn.getChannel());
            	((ZEUMBus)modem).setManualEnable((sn.getManualEnable()==255? false:true));
            	((ZEUMBus)modem).setPanId(sn.getPanId());
            	((ZEUMBus)modem).setSecurityEnable((sn.getSecurityEnable()==255? false:true));
            	((ZEUMBus)modem).setLinkKey(sn.getLinkKey());
            	((ZEUMBus)modem).setNetworkKey(sn.getNetworkKey());
            	((ZEUMBus)modem).setExtPanId(sn.getExtPanId());
        	}

        }
        if(sNode != null){
        	
        	modem.setHwVer(sNode.getHardwareVersion()+"");
        	modem.setNodeKind(sNode.getNodeKind()+"");
        	modem.setProtocolVersion(sNode.getProtocolVersion()+"");
        	modem.setResetCount(sNode.getResetCount());
        	modem.setLastResetCode(sNode.getResetReason());
        	modem.setSwVer(sNode.getSoftwareVersion());
        	modem.setZdzdIfVersion(sNode.getZdzdInterfaceVersion());
        	modem.setFwVer(sNode.getFirmwareVersion());
        	modem.setFwRevision(sNode.getFirmwareBuild());
            
          	if (modemType.equals(ModemType.ZEUPLS)&&
                        fwVersion.compareTo("2.1") >= 0 && fwBuild.compareTo("18") >= 0) {	
            	((ZEUPLS)modem).setSolarADV(sNode.getSolarADVolt());
            	((ZEUPLS)modem).setSolarBDCV(sNode.getSolarChgBattVolt());
            	((ZEUPLS)modem).setSolarChgBV(sNode.getSolarBDCVolt());
            }
            
          	if (modemType.equals(ModemType.ZBRepeater)&&
                    fwVersion.compareTo("2.1") >= 0 && fwBuild.compareTo("18") >= 0) {	
          		((ZBRepeater)modem).setSolarADV(sNode.getSolarADVolt());
          		((ZBRepeater)modem).setSolarBDCV(sNode.getSolarChgBattVolt());
          		((ZBRepeater)modem).setSolarChgBV(sNode.getSolarBDCVolt());
          	}

        }else{
            log.debug("sNode is null!!");
        }
        if(amrData != null){
            // sensor.setProperty(new MOPROPERTY("vendor",amrData.getVendor()));
            ((ZEUPLS)modem).setTestFlag(amrData.getTestFlag() == 1 ? true : false);
            ((ZEUPLS)modem).setFixedReset(amrData.getFixedReset()+"");
            
            StringBuffer mask = new StringBuffer();
            for (int i = 0; i < amrData.getMeteringDay().length; i++) {
                mask.append(""+amrData.getMeteringDay()[i]);
            }
            
            ((ZEUPLS)modem).setMeteringDay(mask.toString());

            mask.setLength(0);
            for (int i = 0; i < amrData.getMeteringHour().length; i++) {
                mask.append("" + amrData.getMeteringHour()[i]);
            }
            
            ((ZEUPLS)modem).setMeteringHour(mask.toString());

            //sensor.setProperty(new MOPROPERTY("unitSerial",amrData.getMeterSerialNumber()));
            
            ((ZEUPLS)modem).setLpChoice(amrData.getLpChoice());
            
            if (fwVersion.compareTo("2.1")>=0 && fwBuild.compareTo("18")>=0) {
                ((ZEUPLS)modem).setAlarmFlag(amrData.getAlarmFlag());
                ((ZEUPLS)modem).setPermitMode(amrData.getPermitMode());
                ((ZEUPLS)modem).setPermitState(amrData.getPermitState());
                ((ZEUPLS)modem).setAlarmMask(amrData.getAlarmMask());
                
            }

            
            String meterId = amrData.getMeterSerialNumber();
            log.info("METERID[" + meterId + "]");
            if (meterId != null && !"".equals(meterId)) {
                Meter meter = meterDao.get(meterId);
                
                if (meter != null) {
                    meter.setModem(modem);//update association
                }
            }
        }
        
        modemDao.update(modem);

    }
    
    private void saveOnDemandMeter(String mcuId, String modemId, FMPVariable[] data)
    throws Exception
    {
    	
    	/*
        MOINSTANCE sensor = EMUtil.getSensorMO(modemId);
        MOINSTANCE meter = EMUtil.getMeterMOBySensorId(modemId, 0);
        
        EnergyMeterData ed = null;
        EnergyMeterDataParser edp = null;
        String meterId = meter.getPropertyValueString("id");
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        byte[] timestamp = new byte[7];
        OPAQUE opaque = null;
        for (int i = 0; i < data.length; i++) {
            opaque = new OPAQUE(meterLPEntry.class.getName());
            opaque.decode(((OCTET) data[i]).getValue(), 0);
            meterLPEntry value = (meterLPEntry)opaque.getValue();
            byte[] bx = value.getMlpData().getValue();
            log.info("bx: "+Hex.decode(bx));
            
            bao.write(bx, 0, timestamp.length);
            timestamp = bao.toByteArray();
            bao.flush();
            
            bao = new ByteArrayOutputStream();
            bao.write(bx,7,bx.length-7);
            log.info("bao: "+Hex.decode(bao.toByteArray()));
            
            edp = CmdUtil.getParser(meterId);
            edp.parse(bao.toByteArray(), -1);
            ed = new EnergyMeterData();
            ed.setTime(new TIMESTAMP(timestamp).getValue());
            ed.setType(value.getMlpType().toString());
            ed.setVendor(value.getMlpVendor().toString());
            ed.setServiceType(value.getMlpServiceType().toString());
            ed.setParser(edp);
            bao.flush();
            bao.close();
            
            try{
                int vendor = Integer.parseInt(meter.getPropertyValueString("vendor"));
                log.debug("vendor : "+ vendor);
                int svcType = Integer.parseInt(sensor.getPropertyValueString("svcType"));
                log.debug("svcType :"+ svcType);

                if(svcType == Integer.parseInt(CommonConstants.getDataSvcCode(DataSVC.Gas))
                		|| svcType == Integer.parseInt(CommonConstants.getDataSvcCode(DataSVC.Water)) ){
                    EMHistoryMgr hdm = EMUtil.getEMHM();
                    hdm.saveGetMeterData(mcuId,meterId,new EnergyMeterData[]{ed});
                }else{
                    if (vendor == AimirModel.MT_VENDOR_GE || vendor == AimirModel.MT_VENDOR_LSIS
                            || vendor ==AimirModel.MT_VENDOR_WIZIT || vendor ==AimirModel.MT_VENDOR_EDMI ){
                        EMHistoryMgr hdm = EMUtil.getEMHM();
                        hdm.saveGetMeterData(mcuId,meterId,new EnergyMeterData[]{ed});
                    }else if(vendor == AimirModel.MT_VENDOR_KAMSTRUP){
                        EMDataLogger emDataLogger = new EMDataLogger();
                        LPData[] lpData =(LPData[])((EnergyMeterDataParser) ed.getParser()).getData().get("lplist");
                        emDataLogger.save(meter, lpData);
                    }
                }
            } catch(Exception e){
                log.warn("cmdOnDemandMeter save error",e);
                log.error(e);
            }
        }
        
        */
    }
}
