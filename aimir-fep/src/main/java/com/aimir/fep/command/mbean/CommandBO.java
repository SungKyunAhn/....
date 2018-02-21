package com.aimir.fep.command.mbean;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.constants.CommonConstants.FW_EQUIP;
import com.aimir.constants.CommonConstants.McuType;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.constants.CommonConstants.TR_OPTION;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.fep.command.conf.DefaultConf;
import com.aimir.fep.command.conf.KamstrupCIDMeta;
import com.aimir.fep.meter.data.MeterData;
import com.aimir.fep.modem.AmrData;
import com.aimir.fep.modem.BatteryLog;
import com.aimir.fep.modem.EventLog;
import com.aimir.fep.modem.LPData;
import com.aimir.fep.modem.ModemCommandData;
import com.aimir.fep.modem.ModemNetwork;
import com.aimir.fep.modem.ModemNode;
import com.aimir.fep.modem.ModemROM;
import com.aimir.fep.protocol.fmp.datatype.SMIValue;
import com.aimir.fep.protocol.fmp.exception.FMPMcuException;
import com.aimir.fep.protocol.fmp.frame.service.entry.codiBindingEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.codiDeviceEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.codiEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.codiMemoryEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.codiNeighborEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.drLevelEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.endDeviceEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.idrEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.sensorInfoNewEntry;
import com.aimir.fep.protocol.fmp.log.AlarmLogger;
import com.aimir.fep.util.ByteArray;
import com.aimir.fep.util.CmdUtil;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.GroupInfo;
import com.aimir.fep.util.Hex;
import com.aimir.model.device.EventAlertAttr;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.MCU;
import com.aimir.model.device.MCUCodi;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.device.ZRU;
import com.aimir.model.system.DeviceModel;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeUtil;

/**
 * Unit Operation Utility Class
 *
 * @author D.J Park
 * @version $Rev: 1 $, $Date: 2005-12-07 15:59:15 +0900 $,
 */
@Service
public class CommandBO implements CommandBOMBean, MBeanRegistration
{
    private static Log log = LogFactory.getLog(CommandBO.class);
    private ObjectName objectName = null;
    
    @Autowired
    private AlarmLogger alarmLogger;
    
    public AlarmLogger getAlarmLogger() {
        return alarmLogger;
    }

    public void setAlarmLogger(AlarmLogger alarmLogger) {
        this.alarmLogger = alarmLogger;
    }
    
    @Autowired
    private MCUDao mcuDao;
    
    public MCUDao getMcuDao() {
        return mcuDao;
    }

    public void setMcuDao(MCUDao mcuDao) {
        this.mcuDao = mcuDao;
    }
    
    @Autowired
    private ModemDao modemDao;
    
    public ModemDao getModemDao() {
        return modemDao;
    }
    
    public void setModemDao(ModemDao modemDao) {
        this.modemDao = modemDao;
    }

    @Autowired
    private MeterDao meterDao;
    
    public CommandBO() {
    }
    
    /**
     * get CommandProxy ObjectName String
     *
     * @return name <code>String</code> objectName String
     */
    public String getName() {
        return this.objectName.toString();
    }

    /**
     * stop CommandProxy
     */
    public void stop()
    {
        log.debug(this.objectName + " stop");
    }
    
    /**
     * method of interface MBeanRegistration
     *
     * @param server <code>MBeanServer</code> MBeanServer
     * @param name <code>ObjectName</code> MBean Object Name
     * @throws java.lang.Exception
     */
    public ObjectName preRegister(MBeanServer server,
            ObjectName name) throws java.lang.Exception
    {
        if (name == null)
        {
            name = new ObjectName(server.getDefaultDomain()
                    + ":service=" + this.getClass().getName());
        }

        this.objectName = name;

        return name;
    }

    /**
     * method of interface MBeanRegistration
     *
     * @param registrationDone <code>Boolean</code>
     * @throws java.lang.Exception
     */
    public void postRegister(Boolean registrationDone) { }
    /**
     * method of interface MBeanRegistration
     *
     * @throws java.lang.Exception
     */
    public void preDeregister() throws java.lang.Exception { }
    /**
     * method of interface MBeanRegistration
     *
     * @throws java.lang.Exception
     */
    public void postDeregister() { }

    /**
     * start CommandProxy
     *
     * @throws java.lang.Exception
     */
    public void start() throws Exception
    {
        log.debug(this.objectName + " start");
    }
    
    public CommandGW getCommandGW() throws Exception
    {
        return new CommandGW();
    }

    @Override
    public String cmdAidonMccb(String mcuId, String meterId, String req)
    throws Exception
    {
        log.info("cmdAidonMccb[" + mcuId + "," + meterId + "," + req + "]");
        CommandGW gw = getCommandGW();
        try {
            return gw.cmdAidonMCCB(mcuId, meterId, req);
        }
        finally {
            gw.close();
        }
    }

    @Override
    public Object[] cmdKamstrupCID(String mcuId, String meterId, String[] req)
    throws Exception
    {
        StringBuffer buf = new StringBuffer();
        buf.append("MCUID[" + mcuId + "] METERID[" + meterId + "] REQ[");
        for (int i = 0; req != null && i < req.length; i++) {
            buf.append(req[i] + ",");
        }
        buf.append(']');

        log.info(buf.toString());
        CommandGW gw = getCommandGW();
        try {
            Meter meter = meterDao.get(meterId);
            return KamstrupCIDMeta.getResult(gw.cmdKamstrupCID(mcuId, meterId, req));
        }
        finally {
            gw.close();
        }
    }

    @Override
    public Object[] cmdKamstrupCID(String mcuId, String meterId, String kind, String[] req)
    throws Exception
    {
        StringBuffer buf = new StringBuffer();
        buf.append("MCUID[" + mcuId + "] METERID[" + meterId + "] KIND[" + kind + "] REQ[");
        for (int i = 0; req != null && i < req.length; i++) {
            buf.append(req[i] + ",");
        }
        buf.append(']');

        log.info(buf.toString());
        CommandGW gw = getCommandGW();
        try {
            Meter meter = meterDao.get(meterId);
            return KamstrupCIDMeta.getResult(gw.cmdKamstrupCID(mcuId, meterId, kind, req));
        }
        finally {
            gw.close();
        }
    }

    @Override
    public Map<String, String> getRelaySwitchStatus(String mcuId, String meterId)
            throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("getRelaySwitchStatus MCUID[" + mcuId + "] METERID[" + meterId + "]");

        log.info(buf.toString());
        CommandGW gw = getCommandGW();
        try {
            return gw.getRelaySwitchStatus(mcuId, meterId);
        }
        finally {
            gw.close();
        }
    }

    @Override
    public Map<String, String> cmdRelaySwitchAndActivate(String mcuId,
            String meterId, int cmdNum) throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("cmdRelaySwitchAndActivate MCUID[" + mcuId + "] METERID[" + meterId + "] cmdNum[" + cmdNum + "]");

        log.info(buf.toString());
        CommandGW gw = getCommandGW();
        try {
            return gw.cmdRelaySwitchAndActivate(mcuId, meterId, ""+cmdNum);
        }
        finally {
            gw.close();
        }
    }

    @Override
    public MCU doMCUScanning(CommandGW gw, MCU mcu) throws Exception
    {
        log.info("doMCUScanning MCU["+mcu.getSysID()+"]");
        if(gw == null) {
			gw = getCommandGW();
		}
        
        String mcuId = mcu.getSysID();
        McuType mcuType = CommonConstants.McuType.valueOf(mcu.getMcuType().getName());

        /*
         * MI_MODELPROPERTY's ObjectId used......
        MOPROPERTY[] props = IUtil.getMOPropertyHasOid(mo);
        */

        DefaultConf defaultConf = DefaultConf.getInstance();
        Hashtable propList = defaultConf.getDefaultProperties(DeviceType.MCU.name());
        String[] propNames = new String[propList.size()];
        
        int idx = 0;
        for (Enumeration e = propList.keys(); e.hasMoreElements(); ) {
            propNames[idx++] = (String)e.nextElement();
        }

        /**
         * For Unit Scanning Debugging
        MOPROPERTY[] res = null;
        for(int i=0;i<props.length;i++)
        {
            log.debug("run "+ props[i].getName());
            res = gw.cmdStdGet(mcuId,new MOPROPERTY[]{props[i]});
        }
         */
        Hashtable res = gw.cmdStdGet(mcuId, propNames);

        String propName;
        String propVal;
        for(Enumeration e = res.keys(); e.hasMoreElements();)
        {
            propName = (String)e.nextElement();
            propVal = (String)res.get(propName);
            
            BeanUtils.setProperty(mcu, propName, propVal);
        }
        
        try {
            findCodi(mcuId);
        }catch(Exception e) { log.error(e,e); }

        // find Modem Information
        /* it's meaningless
        try {
            findModemNew(mo.getName(), mcuId);
        } catch (Exception e) { log.error(e,e); }
        */

        // mcuDao.update(mcu);
        return mcu;
    }

    @Override
    public MCU doMCUScanning(String mcuId) throws Exception
    {
        log.info("doMCUScanning MCU["+mcuId+"]");
        
        CommandGW gw = getCommandGW();
        MCU mcu = mcuDao.get(mcuId);
        
        return doMCUScanning(gw, mcu);
    }

    /**
     * readcount - log count (max 50)
     * @param mcuId
     * @param ModemId
     * @return
     * @throws Exception
     */
    @SuppressWarnings({ "unchecked", "unused"})
    @Override
    public Map getModemBatteryLog(String mcuId, String modemId,
            int readcount, int serviceType, String operator)
    throws Exception
    {
        Map map = new HashMap();
        CommandGW gw = getCommandGW();
        
        try {
            log.debug("getModemBatteryLog,["+mcuId+"],["+modemId+"],["+readcount+"]");
            ModemROM ModemROM = null;
            
            MCU mcu = mcuDao.get(mcuId);
            String revision = mcu.getSysSwRevision();
            if (revision == null || "".equals(revision)) {
				throw new Exception("Check MCU["+mcuId+"] revision!");
			}

            ModemROM = gw.cmdGetModemROM(mcuId, modemId, new int[][]{{ModemROM.OFFSET_BATTERY_POINTER, 1}});

            if (revision.compareTo("2688") >= 0) {
                Modem modem = modemDao.get(modemId);
                
                if (CmdUtil.isAsynch(modem)) {
                    long trId = gw.cmdAsynchronousCall(mcuId, "eui64Entry", modem.getModemType().name(), modemId,
                            "cmdGetModemBattery",
                            (byte)TR_OPTION.ASYNC_OPT_RETURN_DATA_EVT.getCode()|
                            (byte)TR_OPTION.ASYNC_OPT_RETURN_DATA_SAVE.getCode(),
                            0, 0, 2,
                            new String[][] {{"sensorID", modemId},{"byteEntry", ""+readcount}},
                            serviceType, operator);
                    map.put("result", "Success");
                    map.put("commandMethod", "AsynchronousCall");
                    map.put("trId", trId);
                }
                else {
                    BatteryLog log = gw.cmdGetModemBattery(mcuId, modemId, readcount);
                    map.put("result", "Success");
                    map.put("commandMethod", "GetModemEvent");
                    map.put("eventLog", log);
                }
            }
            else {
                int maxlen = 60;
                int len = 5*readcount;
                int pointer =  ModemROM.getPointer();

                int i = 0;
                int newpointer = ModemROM.OFFSET_BATTERY_LOGDATA; //+ ((pointer-(maxlen/5*i))%50)*5;
                log.debug("getModemBatteryLog find pointer=["+pointer+"], address = ["+newpointer+"]");
                ModemROM = gw.cmdGetModemROM(mcuId, modemId, new int[][] {{newpointer, 250*5}});
                map.put("result", "Success");
                map.put("commandMethod", "GetModemROM");
                map.put("batteryLog", ModemROM.getBatteryLog());
            }
        }
        catch (Exception e) {
            map.put("result", "Fail");
            map.put("errorLog", e.getMessage());
        }
        finally {
            gw.close();
        }
        return map;
    }

    /**
     * readcount - event count (max 250)
     * @param mcuId
     * @param modemId
     * @param count
     * @return
     * @throws Exception
     */
    @Override
    public Map getModemEventLog(String mcuId, String modemId, int readcount,
            int serviceType, String operator)
    throws Exception
    {
        Map map = new HashMap();
        CommandGW gw = getCommandGW();
        
        try {
            List<EventLog> list = new ArrayList<EventLog>();
            log.debug("getModemEventLog,["+mcuId+"],["+modemId+"],["+readcount+"]");
            ModemROM ModemROM = null;
            
            MCU mcu = mcuDao.get(mcuId);
            String revision = mcu.getSysSwRevision();
            if (revision == null || "".equals(revision)) {
				throw new Exception("Check MCU["+mcuId+"] revision!");
			}

            if (revision.compareTo("2688") >= 0) {
                Modem modem = modemDao.get(modemId);
                if (CmdUtil.isAsynch(modem)) {
                    long trId = gw.cmdAsynchronousCall(mcuId, "eui64Entry", modem.getModemType().name(), modemId,
                            "cmdGetModemEvent",
                            (byte)TR_OPTION.ASYNC_OPT_RETURN_DATA_EVT.getCode()|
                            (byte)TR_OPTION.ASYNC_OPT_RETURN_DATA_SAVE.getCode(),
                            0, 0, 2,
                            new String[][] {{"sensorID", modemId},{"byteEntry", ""+readcount}},
                            serviceType, operator);
                    map.put("result", "Success");
                    map.put("commandMethod", "AsynchronousCall");
                    map.put("trId", trId);
                }
                else {
                    EventLog[] logs = gw.cmdGetModemEvent(mcuId, modemId, readcount);
                    for (int i = 0; i < logs.length; i++) {
                        list.add(logs[i]);
                    }
                    map.put("result", "Success");
                    map.put("commandMethod", "GetModemEvent");
                    map.put("eventLog", list);
                }
            }
            else {
                ModemROM = gw.cmdGetModemROM(mcuId, modemId, new int[][]{{ModemROM.OFFSET_EVENT_POINTER, 1}});

                int pointer =  ModemROM.getPointer();

                EventLog el = null;
                int newpointer = ModemROM.OFFSET_EVENT_LOGDATA;// + ((250+pointer)%250)*16;
                log.debug("getModemEventLog find pointer=["+pointer+"], address = ["+newpointer+"]");
                ModemROM = gw.cmdGetModemROM(mcuId, modemId, new int[][] {{newpointer, 250*16}});
                for (int i = 0; i < ModemROM.getEventLog().length; i++) {
                    el = ModemROM.getEventLog()[i];
                    if (!el.getGmtTime().startsWith("0") && !el.getGmtTime().startsWith("65535")) {
                        list.add(ModemROM.getEventLog()[i]);
                    }
                }
                map.put("result", "Success");
                map.put("commandMethod", "GetModemROM");
                map.put("eventLog", list);
            }
            gw.close();
        }
        catch (Exception e) {
            map.put("result", "Fail");
            map.put("errorLog", e.getMessage());
        }
        finally {
            gw.close();
        }
        return map;
    }

    /**
     * day lp days
     * ex) day 1 ==> current day
     * ex) day 2 ==> current day, yesterday
     * @param mcuId
     * @param modemId
     * @param day
     * @return
     * @throws Exception
     */
    @Override
    public LPData[] getModemLPLog(String mcuId, String modemId, int day)
        throws Exception
    {
        if(day > 40) {
			day = 40;//max 40 days
		}

        log.debug("getModemLPLog,["+mcuId+"],["+modemId+"],["+day+"]");
        ModemROM ModemROM = null;
        CommandGW gw = getCommandGW();
        try {
            ModemROM = gw.cmdGetModemROM(mcuId, modemId, new int[][]{{ModemROM.OFFSET_METER_LPPERIOD, 2}});
            int lpPeriod = ModemROM.getLpPeriod();
            int lpPointer = ModemROM.getPointer();
            ByteArray ba = new ByteArray();
            //TODO know calculate pointer position
            int pointer = ModemROM.OFFSET_METER_LPLOGDATA+((40+lpPointer-day)%40)*((48*lpPeriod)+8);
            // int len = (8+(48*lpPeriod))*(day+1);
            int len = (8+(48*lpPeriod));
            log.debug("getModemEventLog find lppointer=["+lpPointer+"], address = ["+pointer+"]");
            ModemROM = gw.cmdGetModemROM(mcuId, modemId, new int[][] {{pointer, len}});
            ModemROM.parseLP(lpPeriod);
            return ModemROM.getLpData();
        }
        finally {
            gw.close();
        }
    }

    @Override
    public Hashtable doGetModemROM(CommandGW gw, Modem modem,
            String mcuId, String modemId, int serviceType, String operator)
    throws Exception
    {
        log.info("doGetModemROM Modem["+modem.getDeviceSerial()+"], MCU ID :"+mcuId);

        String fwVersion = modem.getFwVer();
        String fwBuild = modem.getFwRevision();
        ModemROM modemROM = new ModemROM(fwVersion, fwBuild);
        MCU mcu = mcuDao.get(mcuId);
        String revision = mcu.getSysSwRevision();
        ModemType modemType = modem.getModemType();
        Hashtable result = new Hashtable();
        
        try{
            if(gw == null) {
				gw = getCommandGW();
			}

            if (revision.compareTo("2688") >= 0 && CmdUtil.isAsynch(modem)) {
                long trId = gw.cmdAsynchronousCall(mcuId, "eui64Entry", modemType.name(), modemId,
                        "cmdGetSensorROM", (byte)TR_OPTION.ASYNC_OPT_RETURN_DATA_EVT.getCode()|
                        (byte)TR_OPTION.ASYNC_OPT_RETURN_DATA_SAVE.getCode(),
                        0, 0, 2,
                        new String[][] {{"sensorID", modemId},
                                        {"wordEntry", ""+modemROM.OFFSET_MANUAL_ENABLE},
                                        {"wordEntry", ""+modemROM.getNetworkSize()},
                                        {"wordEntry", ""+modemROM.OFFSET_NODEKIND},
                                        {"wordEntry", ""+modemROM.getNodeSize()},
                                        {"wordEntry", ""+modemROM.OFFSET_METER_SERIAL_NUMBER},
                                        {"wordEntry", ""+modemROM.getAmrSize()},
                                        {"wordEntry", ""+modemROM.OFFSET_METER_LPPERIOD},
                                        {"wordEntry", "2"}},
                        serviceType, operator);
                
                result.put("resultStatus", ResultStatus.SUCCESS);
                result.put("commandMethod", "AsynchronousCall");
                result.put("trId", trId);
            }
            else {
                modemROM = gw.cmdGetModemROM(mcuId, modemId,
                        new int[][] {{modemROM.OFFSET_MANUAL_ENABLE, modemROM.getNetworkSize()},
                        {modemROM.OFFSET_NODEKIND, modemROM.getNodeSize()},
                        {modemROM.OFFSET_METER_SERIAL_NUMBER, modemROM.getAmrSize()},
                        {modemROM.OFFSET_METER_LPPERIOD, 2}});

                ModemNetwork sn = modemROM.getModemNetwork();
                ModemNode sNode = modemROM.getModemNode();
                AmrData amrData = modemROM.getAmrData();

                // install date
                if (modem.getInstallDate() == null ||
                        "".equals(modem.getInstallDate())) {
                    modem.setInstallDate(DateTimeUtil.getCurrentDateTimeByFormat(null));
                }

                if(sn != null){
                    if (modem instanceof ZRU) {
                        ZRU zru = (ZRU)modem;
                        zru.setChannelId(sn.getChannel());
                        zru.setManualEnable(sn.getManualEnable()==255? false:true);
                        zru.setPanId(sn.getPanId());
                        zru.setSecurityEnable(sn.getSecurityEnable()==255? false:true);
                        zru.setLinkKey(sn.getLinkKey());
                        zru.setNetworkKey(sn.getNetworkKey());
                        zru.setExtPanId(sn.getExtPanId());
                        // TODO
                        // zru.setTxPower(sn.getTxPower());
                        
                        //TODO update?
                    }
                }
                if(sNode != null){
                    // TODO 코드에서 찾아와야함.
                    modem.setHwVer(sNode.getHardwareVersion());
                    modem.setNodeKind(sNode.getNodeKind());
                    modem.setProtocolVersion(sNode.getProtocolVersion());
                    modem.setResetCount(sNode.getResetCount());
                    modem.setLastResetCode(sNode.getResetReason());
                    modem.setSwVer(sNode.getSoftwareVersion());
                    modem.setFwVer(sNode.getFirmwareVersion());
                    modem.setFwRevision(sNode.getFirmwareBuild());
                    modem.setZdzdIfVersion(sNode.getZdzdInterfaceVersion());
                    
                    if (fwVersion.compareTo("2.1") >= 0 && fwBuild.compareTo("18") >= 0) {
                        BeanUtils.setProperty(modem, "solarADV", sNode.getSolarADVolt());
                        BeanUtils.setProperty(modem, "solarChgBV", sNode.getSolarChgBattVolt());
                        BeanUtils.setProperty(modem, "solarBDCVolt", sNode.getSolarBDCVolt());
                        /*
                        if (modemType == modemType.ZEUPLS){
                            ZEUPLS zeupls = (ZEUPLS)modem;
                            zeupls.setSolarADV(sNode.getSolarADVolt());
                            zeupls.setSolarChgBV(sNode.getSolarChgBattVolt());
                            zeupls.setSolarBDCV(sNode.getSolarBDCVolt());
                        }
                        else if (modemType == modemType.ZBRepeater) {
                            ZBRepeater repeater = (ZBRepeater)modem;
                            repeater.setSolarADV(sNode.getSolarADVolt());
                            repeater.setSolarChgBV(sNode.getSolarChgBattVolt());
                            repeater.setSolarBDCV(sNode.getSolarBDCVolt());
                        }
                        */
                    }
                    // TODO update?
                }
                
                if(amrData != null){
                    BeanUtils.setProperty(modem, "testFlag", amrData.getTestFlag());
                    BeanUtils.setProperty(modem, "fixedReset", amrData.getFixedReset());

                    StringBuffer mask = new StringBuffer();
                    for (int i = 0; i < amrData.getMeteringDay().length; i++) {
                        mask.append(""+amrData.getMeteringDay()[i]);
                    }

                    BeanUtils.setProperty(modem, "meteringDay", mask.toString());

                    mask.setLength(0);
                    for (int i = 0; i < amrData.getMeteringHour().length; i++) {
                        mask.append("" + amrData.getMeteringHour()[i]);
                    }
                    
                    BeanUtils.setProperty(modem, "meteringHour", mask.toString());
                    BeanUtils.setProperty(modem, "lpChoice", amrData.getLpChoice());
                    if (fwVersion.compareTo("2.1")>=0 && fwBuild.compareTo("18")>=0) {
                        BeanUtils.setProperty(modem, "alarmFlag", amrData.getAlarmFlag());
                        BeanUtils.setProperty(modem, "permitMode", amrData.getPermitMode());
                        BeanUtils.setProperty(modem, "permitState", amrData.getPermitState());
                        BeanUtils.setProperty(modem, "alarmMask", amrData.getAlarmMask());
                    }
                    
                    // TODO update? IUtil.setInstance(Modem);

                    String meterId = amrData.getMeterSerialNumber();
                    log.info("METERID[" + meterId + "]");
                }
                
                result.put("commandMethod", "SynchronousCall");
                
                if (sn == null && sNode == null && amrData == null) {
                    result.put("resultStatus", ResultStatus.FAIL);
                    result.put("failReason", "ROM info is null");
                }
                else {
                    result.put("resultStatus", ResultStatus.SUCCESS);
                    result.put("modem", modem);
                }
            }
            
            return result;
        }
        catch(Exception e){
            log.error(e,e);
            throw e;
        }
        finally {
            gw.close();
        }
    }

    @Override
    public void registerMCU(MCU mcu) throws Exception
    {
        log.info("registerMCU MCU["+mcu.getSysID()+"]");
        CommandGW gw = getCommandGW();
        String mcuId = mcu.getSysID();
        McuType mcuType = McuType.valueOf(mcu.getMcuType().getName());

        try {
            // Time Synchronization
            gw.cmdMcuSetTime(mcuId, TimeUtil.getCurrentTime());
    
            mcu = doMCUScanning(gw, mcu);
        }
        finally {
            gw.close();
        }
    }

    @Override
    public void registerModem(Modem modem) throws Exception
    {
        log.info("register Modem["+modem.getDeviceSerial()+
                "] TYPE[" + modem.getModemType().name()+"]");
        String curTime = TimeUtil.getCurrentTime();
        modem.setInstallDate(curTime);
        modem.setLastLinkTime(curTime);
        modem.setCommState(1);
        modemDao.add(modem);
    }

    @Override
    public Hashtable getMCUStatus(String mcuId) throws Exception
    {
        log.info("getMCUStatus MCU["+mcuId+"] ");
        CommandGW gw = getCommandGW();

        try {
            Vector<String> mibPropNames = new Vector<String>();
            // 정전상태
            mibPropNames.add("gpioPowerFail");
            // Low Battery 상태
            mibPropNames.add("gpioLowBattery");
            // 플래쉬 메모리 총 용량
            mibPropNames.add("flashTotalSize");
            // 플래쉬 메모리 사용중인 용량
            mibPropNames.add("flashUseSize");
            // 메모리 총 용량
            mibPropNames.add("memTotalSize");
            // 사용중인 메모리 용량
            mibPropNames.add("memUseSize");
            // 현재 온도
            mibPropNames.add("sysCurTemp");
            // 현재 시각
            mibPropNames.add("sysTime");
            // Coordinator Device 정보
            mibPropNames.add("codiDevice");
    
            Hashtable resProps = 
                    gw.cmdStdGet(mcuId,mibPropNames.toArray(new String[mibPropNames.size()]));
            
            return resProps;
        }
        finally {
            gw.close();
        }
    }

    @Override
    public Hashtable getModemStatus(String mcuId,String modemId, int serviceType, String operator)
    throws Exception
    {
        log.info("getModemStatus MCU["+mcuId+"] Modem["+modemId+"]");
        Modem modem = modemDao.get(modemId);
        return doGetModemROM(getCommandGW(), modem, mcuId, modemId, serviceType, operator);
    }

    @Override
    public void timeSynchronization(String mcuId) throws Exception
    {
        log.info("timeSynchronization MCU["+mcuId+"]");
        CommandGW gw = getCommandGW();
        String time = TimeUtil.getCurrentTime();
        try {
            gw.cmdMcuSetTime(mcuId,time);
        }
        finally {
            gw.close();
        }
    }

    @Override
    public void mcuReset(String mcuId) throws Exception
    {
        log.info("mcuReset MCU["+mcuId+"]");
        CommandGW gw = getCommandGW();
        try {
            gw.cmdMcuReset(mcuId);
        }
        finally {
            gw.close();
        }
    }

    private String toGEStatus(String str)
    {
        if(str.equals("0")) {
			return "Off";
		}
		else if(str.equals("1")) {
			return "On";
		}
		else {
			return "Off";
		}
    }

    @SuppressWarnings("unchecked")
    @Override
    public Hashtable doOnDemand(Meter meter, int serviceType, String operator)
    throws Exception
    {
        log.info("doOnDemand meter["+meter.getMdsId()+"]");
        Hashtable result = new Hashtable();
        CommandGW gw = getCommandGW();
        
        try
        {
            MCU mcu = meter.getModem().getMcu();
            McuType mcuType = McuType.valueOf(mcu.getMcuType().getName());
            DeviceModel model = meter.getModel();
            int lpInterval = meter.getLpInterval();
            
            boolean isAsync = false;
            long trId = 0;

            MeterData emd = null;

            Modem modem = meter.getModem();
            String revision = mcu.getSysSwRevision();
            if (revision.compareTo("2688") >= 0 && CmdUtil.isAsynch(modem)) {
                isAsync = true;
                int[] offsetCount = CmdUtil.convertOffsetCount(model,lpInterval, modem.getModemType(), "", "");
                trId = gw.cmdAsynchronousCall(modem.getMcu().getSysID(), 
                        "eui64Entry", modem.getModemType().name(), modem.getDeviceSerial(),
                        "cmdOnDemandMeter", (byte)TR_OPTION.ASYNC_OPT_RETURN_DATA_EVT.getCode()
                        |(byte)TR_OPTION.ASYNC_OPT_RETURN_DATA_SAVE.getCode(),
                        0, 0, 2,
                        new String[][] {{"sensorID", modem.getDeviceSerial()},
                                        {"intEntry", "0"},
                                        {"intEntry", ""+offsetCount[0]},
                                        {"intEntry", ""+offsetCount[1]}},
                        serviceType, operator);
            }
            else {
                emd = gw.cmdOnDemandMeter(modem.getMcu().getSysID(),
                        meter.getMdsId(), modem.getDeviceSerial(), "0", "", "");
            }

            if (!isAsync) {
                Double meteringValue = emd.getParser().getMeteringValue();
                log.debug("MeteringValue[" + meteringValue + "]");
                result.put("commandMethod", "SynchronousCall");
                result.put("meteringValue", meteringValue.toString());
                result.put("ondemandResult", emd.getParser().getData());
            }
            else {
                result.put("commandMethod", "AsynchronousCall");
                result.put("transactionId", trId);
            }
            result.put("resultStatus", ResultStatus.SUCCESS);
            gw.close();
        }
        catch(Exception ex)
        {
            result.put("resultStatus", ResultStatus.FAIL);
            result.put("failReason", ex.getMessage());
            log.error(ex);
        }
        finally {
            gw.close();
        }
        return result;
    }

    @Override
    public MeterData[] getSavedMeteringDataInMCU(String mcuId, String meterId,
            String fromTime, String toTime)
    throws Exception
    {
        CommandGW gw = getCommandGW();
        try
        {
            MeterData[] emds =
                gw.cmdGetMeter(mcuId, meterId,fromTime,toTime);
            return emds;
        }
        finally {
            gw.close();
        }
    }

    @Override
    public  Hashtable getMCUDiagnosis(String mcuId) throws Exception
    {
        CommandGW gw = getCommandGW();
        try {
            return gw.cmdMcuDiagnosis(mcuId);
        }
        finally {
            gw.close();
        }
    }

    @Override
    public Object cmdGetMeterSchedule(String mcuId, String modemId, int nOption, int nOffset, int nCount)
        throws Exception
    {
        log.info("Meter's schedule["+mcuId+"," + modemId + "," + nOption +
                 "," + nOffset + "," + nCount + "]");
        CommandGW gw = getCommandGW();
        try {
            Object res = gw.cmdGetMeterSchedule( mcuId, modemId, nOption, nOffset, nCount );
            return res;
        }
        finally {
            gw.close();
        }
    }

    @Override
    public void cmdMcuSetDST(String mcuId, String fileName)
        throws Exception
    {
        log.info("Set MCU DST File["+mcuId+"]");
        CommandGW gw = getCommandGW();
        try {
            gw.cmdMcuSetDST(mcuId, fileName);
        }
        finally {
            gw.close();
        }
    }

    @Override
    public long cmdMcuSetGMT(String mcuId)
        throws Exception
    {
        long result = 0;
        log.info("Set MCU GMT Time ["+mcuId+"]");
        CommandGW gw = getCommandGW();
        try {
            result = gw.cmdMcuSetGMT(mcuId);
            return result;
        }
        finally {
            gw.close();
        }
    }

    @Override
    public void cmdMcuSetConfiguration(String mcuId) throws Exception
    {
        String fileName = "/tmp/config.tar.gz";
        log.info("Set MCU Configuration ["+mcuId+"]");
        CommandGW gw = getCommandGW();
        try {
            gw.cmdPutFile(mcuId, fileName);
            gw.cmdSetConfiguration(mcuId);
        }
        finally {
            gw.close();
        }
    }

    @Override
    public Map<String, String> cmdMcuGetConfiguration(String mcuId) throws Exception
    {
        log.info("Get MCU Configuration ["+mcuId+"]");
        String ddir = FMPProperty.getProperty("command.download.dir");
        if(ddir == null || ddir.equals("")){
            ddir = "/home/aimir/mcu/download";
        }
        CommandGW gw = getCommandGW();
        try {
            String[] res = gw.cmdGetConfiguration(mcuId);
            Map<String, String> map = new LinkedHashMap<String, String>();
            if(res == null || res.length < 2){
                throw new Exception("Get MCU Configuration data empty!");
            }else{
                String fileName = res[0];
    
                String len = res[1];
                gw.cmdGetFile(mcuId, fileName);
                map.put("File Name", ddir+"/"+mcuId.substring(9)+"."+res[0].substring(5));
                map.put("File Size", len+"(bytes)");
            }
            return map;
        }
        finally {
            gw.close();
        }
    }

    @Override
    public String[] cmdMeterTimeSync(String mcuId, String meterId)
        throws Exception
    {
        log.info("Energy Meter's sync meter time["+mcuId+"," + meterId + "]");
        CommandGW gw = getCommandGW();
        try {
            byte[] bx = gw.cmdMeterTimeSync( mcuId, meterId );
            
            return new String[]{new String(bx)};
        }
        finally {
            gw.close();
        }
    }

    @Override
    public MCUCodi[] findCodi(String mcuId) throws Exception
    {
        CommandGW gw = getCommandGW();
        try {
            codiEntry[] ces = gw.cmdGetCodiList(mcuId);
            MCUCodi[] codis = new MCUCodi[ces.length];
            
            int codiIndex = 0;
            codiDeviceEntry cde = null;
            codiBindingEntry cbe = null;
            codiNeighborEntry cne = null;
            codiMemoryEntry cme = null;
            for (int i = 0; i < codis.length; i++) {
                codiIndex = ces[i].getCodiIndex().getValue();
                cde = gw.cmdGetCodiDevice(mcuId,codiIndex);
                cbe = gw.cmdGetCodiBinding(mcuId,codiIndex);
                cne = gw.cmdGetCodiNeighbor(mcuId,codiIndex);
                cme = gw.cmdGetCodiMemory(mcuId,codiIndex);
                
                codis[i] = CmdUtil.makeCodi(ces[i]);
                codis[i].setMcuCodiDevice(CmdUtil.makeCodiDevice(cde));
                codis[i].setMcuCodiBinding(CmdUtil.makeCodiBinding(cbe));
                codis[i].setMcuCodiNeighbor(CmdUtil.makeCodiNeighbor(cne));
                codis[i].setMcuCodiMemory(CmdUtil.makeCodiMemory(cme));
            }
            
            return codis;
        }
        finally {
            gw.close();
        }
    }

    @Override
    public void setDefaultMcuConfig(String mcuId) throws Exception
    {
        CommandGW gw = getCommandGW();
        try {
            Hashtable sysProps = getDefaultMcuConfig(DeviceType.MCU.name());
            String[] propNames = new String[sysProps.size()];
            String[] propValues = new String[sysProps.size()];
            
            int idx = 0;
            for (Enumeration e = sysProps.keys(); e.hasMoreElements();) {
                propNames[idx] = (String)e.nextElement();
                propValues[idx] = (String)sysProps.get(propNames[idx++]);
            }
            
            gw.cmdStdSet(mcuId, propNames, propValues);
        }
        finally {
            gw.close();
        }
    }

    @Override
    public Hashtable getDefaultMcuConfig(String className) throws Exception
    {
        DefaultConf dc = DefaultConf.getInstance();
        return dc.getDefaultProperties(className);
    }

    @Override
    public void setInstallDate(MCU m, String time)
        throws Exception
    {
        try
        {
            m.setInstallDate(time);
            m.setNetworkStatus(1);
            mcuDao.update(m);
        }
        catch (Exception e)
        {
            log.error("update install date failed ["+m.getSysID()+"]");
            log.error(e,e);
        }
    }

    @Override
    public void setLastTimeSyncDate(MCU m, String time) throws Exception
    {
        try
        {
            m.setLastTimeSyncDate(time);
            mcuDao.update(m);
        }
        catch (Exception e)
        {
            log.error("update last time sync date failed ["+m.getSysID()+"]");
            log.error(e,e);
        }
    }

    @Override
    public void setInstallDate(MCU[] m, String time)
        throws Exception
    {
        for (int i = 0; i < m.length; i++)
        {
            setInstallDate(m[i], time);
        }
    }

    public String getBooleanString(String val)
    {
        String retval = "false";
        int i = 0;
        try { i = Integer.parseInt(val); } catch(Exception ex) {}
        if(i == 1) {
			retval = "true";
		}
        return retval;
    }

    public void removeEventAttributeAll(EventAlertLog event)
    {
        Set<EventAlertAttr> attrs = event.getEventAlertAttrs();
        attrs.removeAll(attrs);
    }

    @SuppressWarnings("unchecked")
    public String MapToJSON(Map map) throws Exception
    {
        StringBuffer rStr = new StringBuffer();
        Iterator<String> keys = map.keySet().iterator();
        String keyVal = null;
        rStr.append('[');
        while(keys.hasNext())
        {
            keyVal = keys.next();
            rStr.append("{\"name\":\"");
            rStr.append(keyVal);
            rStr.append("\",\"value\":\"");
            rStr.append(map.get(keyVal));
            rStr.append("\"}");
            if(keys.hasNext()) {
				rStr.append(',');
			}
        }
        rStr.append(']');
        return rStr.toString();
    }

    /**
     * get Equip Version Information
     * @param equipKind
     * @param mcuId
     */
    public boolean getEquipVersion(int equipKind,String triggerId, String mcuId)
    {
        log.debug("[getEquipVersion] EquipKind: "+equipKind+" ,triggerId:"+triggerId+" ,mcuId:"+mcuId);
        boolean result = true;
        try{
            FW_EQUIP equip = CommonConstants.getFwEquip(equipKind);
            //All
            if(equip == FW_EQUIP.All){
                try {
                    getMCUVersion(triggerId, mcuId);
                    getCodiVersion(triggerId, mcuId);
                    getModemVersion(triggerId, mcuId);
                }
                catch (Exception e) {
                    result = false;
                    log.error("Can Not Get Modem Version Info :"+e.getMessage(),e);
                }
            }
            //MCU
            else if(equip == FW_EQUIP.MCU){
                try{
                    getMCUVersion(triggerId, mcuId);
                }catch(Exception e){
                    result = false;
                    log.error("Can Not Get MCU Version Info :"+e.getMessage(),e);
                }
            }
            //Codi
            else if(equip == FW_EQUIP.Coordinator){
                try {
                    getCodiVersion(triggerId, mcuId);
                }
                catch (Exception e) {
                    result = false;
                    log.error("Can Not Get Codi Version Info :"+e.getMessage(),e);
                }
            }
            //Modem or Repeater
            else if(equip == FW_EQUIP.Modem){
                try {
                    getModemVersion(triggerId, mcuId);
                }
                catch (Exception e) {
                    result = false;
                    log.error("Can Not Get Modem Version Info :"+e.getMessage(),e);
                }
            }
        }catch(Exception e){
            result = false;
            log.error("Can Not Get Modem Version Info :"+e.getMessage(),e);
        }
        return result;
    }

    /**
     * @param triggerId
     * @param mcuId
     * @result Modem
     * @throws Exception
     */
    public Modem[] getModemVersion(String triggerId, String mcuId) throws Exception{
        CommandGW gw = getCommandGW();
        sensorInfoNewEntry[] sie = gw.cmdGetModemAllNew(mcuId);
        Modem[] modems = new Modem[sie.length];
        
        for (int i = 0; i < sie.length; i++)
        {
            //-------------------
            //Update OTA State
            //-------------------
            if(sie[i].getSensorID()!=null && sie[i].getSensorOTAState()!=null){
                modems[i] = CmdUtil.makeModem(sie[i], mcuId);
                
                /*
                int otaState = sie[i].getSensorOTAState().getValue();
                log.debug("modemId["+sie[i].getSensorID()+"] otaState["+otaState+"]");
                //Init
                if(otaState==0){
                    FirmwareUtil.updateOTAHistory(triggerId, sie[i].getSensorID().toString(),
                            FW_OTA.Init, FW_STATE.Unknown, "");
                }
                //Try_Write
                else if(otaState==1){
                    FirmwareUtil.updateOTAHistory(triggerId, sie[i].getSensorID().toString(),
                            FW_OTA.DataSend, FW_STATE.Unknown, "");
                }
                //Fail_Write
                else if(otaState==2){
                    FirmwareUtil.updateOTAHistory(triggerId, sie[i].getSensorID().toString(),
                            FW_OTA.DataSend, FW_STATE.Fail, "");
                }
                //Succ_Write
                else if(otaState==3){
                    FirmwareUtil.updateOTAHistory(triggerId, sie[i].getSensorID().toString(),
                            FW_OTA.DataSend, FW_STATE.Success, "");
                }
                //Fail_Validation
                else if(otaState==4){
                    FirmwareUtil.updateOTAHistory(triggerId, sie[i].getSensorID().toString(),
                            FW_OTA.Verify, FW_STATE.Fail, "");
                }
                //Succ_Validation
                else if(otaState==5){
                    FirmwareUtil.updateOTAHistory(triggerId, sie[i].getSensorID().toString(),
                            FW_OTA.Verify, FW_STATE.Success, "");
                }
                //Fail_Install
                else if(otaState==6){
                    FirmwareUtil.updateOTAHistory(triggerId, sie[i].getSensorID().toString(),
                            FW_OTA.Install, FW_STATE.Fail, "");
                }
                //Succ_Install
                else if(otaState==7){
                    FirmwareUtil.updateOTAHistory(triggerId, sie[i].getSensorID().toString(),
                            FW_OTA.Install, FW_STATE.Success, "");
                }
                //Scan
                else if(otaState==8){
                    FirmwareUtil.updateOTAHistory(triggerId, sie[i].getSensorID().toString(),
                            FW_OTA.Scan, FW_STATE.Unknown, "");
                }
                */
            }
        }
        return modems;
    }

    /**
     * @param gw
     * @param triggerId
     * @param mcuId
     * @throws Exception
     */
    public MCU getMCUVersion(String triggerId, String mcuId) throws Exception{
        CommandGW gw = getCommandGW();
        try {
            Hashtable mop = gw.cmdStdGetChild(mcuId, "2.1.0");
            MCU mcu = mcuDao.get(mcuId);
            String key = null;
            for(Enumeration e = mop.elements(); e.hasMoreElements(); ){
                key = (String)e.nextElement();
                // sysHwVersion, sysSwVersion, sysSwRevision
                BeanUtils.copyProperty(mcu, key, mop.get(key));
            }
            log.debug(mcu.toString());
            return mcu;
        }
        finally {
            gw.close();
        }
    }

    /**
     * @param gw
     * @param triggerId
     * @param mcuId
     * @throws Exception
     */
    public MCUCodi[] getCodiVersion(String triggerId, String mcuId) throws Exception{
        CommandGW gw = getCommandGW();
        try {
            codiEntry[] codiEntry = gw.cmdGetCodiList(mcuId);
            MCUCodi[] mcuCodis = new MCUCodi[codiEntry.length];
            
            for(int i=0; i<codiEntry.length; i++){
                mcuCodis[i] = CmdUtil.makeCodi(codiEntry[i]);
                log.debug(mcuCodis[i].toString());
            }
            
            return mcuCodis;
        }
        finally {
            gw.close();
        }
    }

    /**
     * send message for alarm unit installation
     * 2009.11.26
     * @param unitId        alarm unit identification
     * @param installDate   installation date
     * @throws JMSException
     */
    public void sendInstallAlarmUnit(String unitId, String installDate)
    throws Exception
    {
        /*
        ActiveMQMapMessage msg = new ActiveMQMapMessage();
        msg.setString(AlarmLogger.MSGPROP.Message.getName(), 
                AlarmLogger.MESSAGE.InstallAlarmUnit.getName());
        msg.setString(AlarmLogger.MSGPROP.Target.getName(), unitId);
        msg.setString(AlarmLogger.MSGPROP.Timestamp.getName(), installDate);
        */
        StringBuffer buf = new StringBuffer();
        buf.append(AlarmLogger.MSGPROP.Message.getName() + "=" +
                AlarmLogger.MESSAGE.InstallAlarmUnit.getName() + ",");
        buf.append(AlarmLogger.MSGPROP.Target.getName() + "=" + unitId + ",");
        buf.append(AlarmLogger.MSGPROP.Timestamp.getName() + "=" + installDate);
        
        TextMessage msg = new ActiveMQTextMessage();
        msg.setIntProperty("content-length", buf.length());
        msg.setText(buf.toString());
        
        sendAlarm(msg, null, false);
    }
    
    /**
     * send event or alarm
     * 2009.12.07
     * 
     * @param unitId        alarm unit identification
     * @param eventId       event id
     * @param eventStatus   event status (4 bytes)
     */
    public void sendAlarmEvent(String unitId, Byte eventId, Integer eventStatus,
            String timestamp) 
    throws Exception
    {
        AlarmLogger.MESSAGE message = null;
        AlarmLogger.ALARMTYPE alarmType = null;
        AlarmLogger.EVENTTYPE eventType = null;
        byte[] es = DataUtil.get4ByteToInt(eventStatus);
        log.info("UNITID[" + unitId + "] EVENTID[" + eventId.byteValue() + 
                "] EVENTSTATUS[" + Hex.decode(es) + "]");
        
        ModemCommandData.Event event = ModemCommandData.getEvent(eventId.byteValue());
		if (ModemCommandData.Event.FireAlarmSmokeDetected == event
				|| ModemCommandData.Event.SystemAlarmSmokeDetector == event) {
			message = AlarmLogger.MESSAGE.Alarm;
			alarmType = AlarmLogger.ALARMTYPE.Smoke;
		} else if (ModemCommandData.Event.FireAlarmTemperatureHigh == event
				|| ModemCommandData.Event.SystemAlarmTemperatureSensor == event) {
            message = AlarmLogger.MESSAGE.Alarm;
            alarmType = AlarmLogger.ALARMTYPE.HighTemperature;
		} else if (ModemCommandData.Event.SilenceAlarm == event) {
            message = AlarmLogger.MESSAGE.Alarm;
            alarmType = AlarmLogger.ALARMTYPE.Silence;
		} else {
			message = AlarmLogger.MESSAGE.Event;
            if (event == ModemCommandData.Event.LowBattery)
                eventType = AlarmLogger.EVENTTYPE.BatteryLow;
            else if (event == ModemCommandData.Event.MagneticTamper)
                eventType = AlarmLogger.EVENTTYPE.Tampered;
            else if (event == ModemCommandData.Event.HeartBeat)
                eventType = AlarmLogger.EVENTTYPE.HeartBeat;
            else if (event == ModemCommandData.Event.CaseOpen)
                eventType = AlarmLogger.EVENTTYPE.CaseOpen;
            else if (event == ModemCommandData.Event.SmokeDetectorTest)
                eventType = AlarmLogger.EVENTTYPE.ButtonPushed;
            else if (event == ModemCommandData.Event.ConnectionFailure)
            	eventType = AlarmLogger.EVENTTYPE.ConnectionFailure;
            else if (event == ModemCommandData.Event.ConnectionRestored)
            	eventType = AlarmLogger.EVENTTYPE.ConnectionRestored;
        }
        
        /*
        ActiveMQMapMessage msg = new ActiveMQMapMessage();
        msg.setString(AlarmLogger.MSGPROP.Message.getName(), message.getName());
        if (message == AlarmLogger.MESSAGE.Event)
            msg.setString(AlarmLogger.MSGPROP.EventType.getName(), eventType.getName());
        else
            msg.setString(AlarmLogger.MSGPROP.AlarmType.getName(), alarmType.getName());
        msg.setString(AlarmLogger.MSGPROP.Source.getName(), unitId);
        msg.setString(AlarmLogger.MSGPROP.Timestamp.getName(), timestamp);
        msg.setString(AlarmLogger.MSGPROP.BatteryLevel.getName(), "");
        msg.setString(AlarmLogger.MSGPROP.Temperature.getName(), 
                ModemCommandData.getTemperatureAlarmLevel(new byte[]{es[2], es[3]}));
        msg.setString(AlarmLogger.MSGPROP.Status.getName(), 
                AlarmLogger.getStatus(
                        ModemCommandData.isEventStatus(eventId, 
                                new byte[]{es[0], es[1]})).getName());
        */
        
        StringBuffer buf = new StringBuffer();
        buf.append(AlarmLogger.MSGPROP.Message.getName() + "=" + message.getName() + ",");
        buf.append(AlarmLogger.MSGPROP.Source.getName() + "=" + unitId + ",");
        buf.append(AlarmLogger.MSGPROP.Timestamp.getName() + "=" + timestamp + ",");
        if(event != ModemCommandData.Event.ConnectionFailure &&
        		event != ModemCommandData.Event.ConnectionRestored) {
	        buf.append(AlarmLogger.MSGPROP.BatteryLevel.getName() + "=" + ",");
	        buf.append(AlarmLogger.MSGPROP.Temperature.getName() + "=" + 
	                ModemCommandData.getTemperatureAlarmLevel(new byte[]{es[2], es[3]}) + ",");
	        buf.append(AlarmLogger.MSGPROP.Status.getName() + "=" +
	                AlarmLogger.getStatus(
	                        ModemCommandData.isEventStatus(eventId, 
	                                new byte[]{es[0], es[1]})).getName() + ",");
        }
        if (message == AlarmLogger.MESSAGE.Event)
            buf.append(AlarmLogger.MSGPROP.EventType.getName() + "=" + eventType.getName());
        else
            buf.append(AlarmLogger.MSGPROP.AlarmType.getName() + "=" + alarmType.getName());
        
        TextMessage msg = new ActiveMQTextMessage();
        msg.setIntProperty("content-length", buf.length());
        msg.setText(buf.toString());
        
        sendAlarm(msg, null, false);
    }
    
    /**
     * send alarm unit connection status
     * 
     * @param unitId        alarm unit identification
     * @param status        false:no connection, true:connection restore
     * @param timestamp     event timestatmp
     * @throws Exception
     */
    public void sendAlarmUnitConnectionStatus(String unitId, boolean status, String timestamp)
    throws Exception
    {
        /*
        ActiveMQMapMessage msg = new ActiveMQMapMessage();
        msg.setString(AlarmLogger.MSGPROP.Message.getName(), 
                AlarmLogger.MESSAGE.SystemError.getName());
        msg.setString(AlarmLogger.MSGPROP.Source.getName(), unitId);
        msg.setString(AlarmLogger.MSGPROP.UnitType.getName(),
                AlarmLogger.UNITTYPE.AlarmUnit.getName());
        msg.setString(AlarmLogger.MSGPROP.Reason.getName(), "");
        msg.setString(AlarmLogger.MSGPROP.Timestamp.getName(), timestamp);
        */
        StringBuffer buf = new StringBuffer();
        buf.append(AlarmLogger.MSGPROP.Message.getName() + "=" + 
                AlarmLogger.MESSAGE.SystemError.getName() + ",");
        buf.append(AlarmLogger.MSGPROP.Source.getName() + "=" + unitId + ",");
        buf.append(AlarmLogger.MSGPROP.UnitType.getName() + "=" +
                AlarmLogger.UNITTYPE.AlarmUnit.getName() + ",");
        buf.append(AlarmLogger.MSGPROP.Reason.getName() + "=" + ",");
        buf.append(AlarmLogger.MSGPROP.Timestamp.getName() + "=" + timestamp);
        
        TextMessage msg = new ActiveMQTextMessage();
        msg.setIntProperty("content-length", buf.length());
        msg.setText(buf.toString());
        
        sendAlarm(msg, null, false);
    }
    
    private void sendAlarm(Message msg, Long logId, boolean retry)
    throws JMSException {
        alarmLogger.sendAlarm(msg, logId, retry);
    }

    /**
     * (102.54) 그룹을 추가한다
     * @param mcuId
     * @param groupName
     * @throws Exception
     */
	@Override
	public void cmdGroupAdd(String mcuId, String groupName) throws Exception {
        log.info("cmdGroupAdd["+mcuId+"], groupName["+groupName+"]");
        CommandGW gw = getCommandGW();
        try {
            gw.cmdGroupAdd(mcuId, groupName);
        }
        finally {
            gw.close();
        }
	}

    /**
     * (102.56) 그룹에 멤버를 추가한다
     * @param mcuId
     * @param groupName
     * @param modemId
     * @throws Exception
     */
	@Override
	public void cmdGroupAddMember(String mcuId, int groupKey, String modemId)
			throws Exception {
        log.info("cmdGroupAddMember["+mcuId+"], groupKey["+groupKey+"]");
        CommandGW gw = getCommandGW();
        try {
            gw.cmdGroupAddMember(mcuId, groupKey, modemId);
        }
        finally {
            gw.close();
        }
		
	}
    
    /**
     * (102.53)
     * 그룹의 멤버에 비동기 명령을 수행한다
     * @param mcuId
     * @param groupKey
     * @param command
     * @param option - 0x01 : ASYNC_OPT_RETURN_CODE_EVT
                     - 0x02 : ASYNC_OPT_RESULT_DATA_EVT
                     - 0x10 : ASYNC_OPT_RETURN_CODE_SAVE
                     - 0x20 : ASYNC_OPT_RESULT_DATA_SAVE
     * @param day Keep Option
     * @param nice Request
     * @param ntry
     * @return trId
     * @throws Exception
     */
	@Override
	public long cmdGroupAsyncCall(String mcuId, int groupKey,
			String command, int option, int day, int nice, int ntry, List<SMIValue> param)
			throws Exception {

        log.info("cmdGroupAsyncCall["+mcuId+"], groupKey["+groupKey+"], command["+command+"], option["+option+"], day["+day+"], nice["+nice+"], ntry["+ntry+"]");
        CommandGW gw = getCommandGW();
        try {
            return gw.cmdGroupAsyncCall(mcuId, groupKey, command, option, day, nice, ntry, param);
        }
        finally {
            gw.close();
        }
	}

    /**
     * (102.55) 그룹을 삭제한다
     * @param mcuId
     * @param groupKey
     * @throws Exception
     */
	@Override
	public void cmdGroupDelete(String mcuId, int groupKey) throws Exception {
		
        log.info("cmdGroupDelete["+mcuId+"], groupKey["+groupKey+"]");
        CommandGW gw = getCommandGW();
        try {
            gw.cmdGroupDelete(mcuId, groupKey);
        }
        finally {
            gw.close();
        }
	}

    /**
     * (102.57) 그룹에 멤버를 삭제한다
     * @param mcuId
     * @param groupKey
     * @param modemId
     * @throws Exception
     */
	@Override
	public void cmdGroupDeleteMember(String mcuId, int groupKey, String modemId) throws Exception {
		
        log.info("cmdGroupDeleteMember["+mcuId+"], groupKey["+groupKey+"], modemId["+modemId+"]");
        CommandGW gw = getCommandGW();
        try {
            gw.cmdGroupDeleteMember(mcuId, groupKey, modemId);
        }
        finally {
            gw.close();
        }
	}

    /**
     * (102.58) 현재 그룹 정보 전체를 조회한다
     * @param mcuId
     * @param modemId
     * @throws Exception
     */
	@Override
	public GroupInfo[] cmdGroupInfo(String mcuId) throws Exception {
        log.info(" cmdGroupInfo["+mcuId+"]");
        CommandGW gw = getCommandGW();
        try {
           return gw.cmdGroupInfo(mcuId);
        }
        finally {
            gw.close();
        }
	}

    /**
     * (102.58) 현재 그룹 정보를 그룹명으로 조회한다
     * @param mcuId
     * @param groupKey
     * @param modemId
     * @throws Exception
     */
	@Override
	public GroupInfo[] cmdGroupInfo(String mcuId, int groupKey)
			throws Exception {
        log.info(" cmdGroupInfo["+mcuId+"], groupKey["+groupKey+"]");
        CommandGW gw = getCommandGW();
        try {
           return gw.cmdGroupInfo(mcuId, groupKey);
        }
        finally {
            gw.close();
        }
	}
	
    /**
     * (102.58) 현재 그룹 정보를 모뎀이 속한 정보로 검색한다.
     * @param mcuId
     * @param groupName
     * @param modemId
     * @param bSearchId - 파라미터가 true이면 모뎀이 속한 그룹을 조회
     * @throws FMPMcuException
     * @throws Exception
	 *	  IF4ERR_GROUP_DB_OPEN_FAIL		: 그룹 DB의 Open 실패
     */
	@Override
	public GroupInfo[] cmdGroupInfo(String mcuId, String modemId, boolean bSearchId)
			throws Exception {
        log.info(" cmdGroupInfo["+mcuId+"], modemId["+modemId+"]");
        CommandGW gw = getCommandGW();
        try {
           return gw.cmdGroupInfo(mcuId, modemId, bSearchId);
        }
        finally {
            gw.close();
        }
	}
	
	/**
     * (104.14)DR 대상 정보 리스트
     * @param mcuId
     * @param sensorId
     * @param parser
     * @throws Exception
     */
	@Override
	public List cmdGetDRAssetInfo(String mcuId, String sensorId, String parser) throws Exception{
		log.info(" cmdGetDRAssetInfo["+mcuId+"], sensorId["+sensorId+"], parser["+parser+"]");
        CommandGW gw = getCommandGW();
        try {
           return gw.cmdGetDRAssetInfo(mcuId, sensorId, parser);
        }
        finally {
           gw.close();
        }
	}
	
	/**
     * (104.15)DR 대상 레벨 요청
     * @param mcuId
     * @param sensorId
     * @throws Exception
     */
	@Override
	public byte cmdGetEnergyLevel(String mcuId, String sensorId) throws Exception{
		log.info(" cmdGetEnergyLevel["+mcuId+"], sensorId["+sensorId+"]");
        CommandGW gw = getCommandGW();
        try {
           return gw.cmdGetEnergyLevel(mcuId, sensorId);
        }
        finally {
           gw.close();
        }
	}
	
	/**
     * (104.16)DR 대상 레벨 수정
     * @param mcuId
     * @param sensorId
     * @throws Exception
     */
	@Override
	public void cmdSetEnergyLevel(String mcuId, String sensorId, String level) throws Exception{
		log.info(" cmdGetEnergyLevel["+mcuId+"], sensorId["+sensorId+"]");
        CommandGW gw = getCommandGW();
        try {
           gw.cmdSetEnergyLevel(mcuId, sensorId, level);
        }
        finally {
           gw.close();
        }
	}
	
	/**
	  * cmdSendMessage
	  * 111.4
	  * @param id TargetId
	  * @param nMessageId Message ID
	  * @param nMessageType Message Type
	  * @param nDuration Lazy, Passive
	  * @param nErrorHandler Error handler Action Code
	  * @param nPreHandler Pre-Action Handler
	  * @param nPostHandler Post-Action Handler
	  * @param nUseData User Data
	  * @param pszData Message
	  * @throws Exception
	*/
	public void cmdSendMessage(String mcuId, String sensorId, int messageId, int messageType, int duration,  int errorHandler, int preHandler, int postHandler, int userData, String pszData ) throws Exception {
		log.info(" cmdSendMessage["+mcuId+"], sensorId["+sensorId+"]");
        CommandGW gw = getCommandGW();
        try {
        	log.info(pszData);
            gw.cmdSendMessage(mcuId, sensorId, messageId, messageType, duration, errorHandler, preHandler, postHandler, userData, pszData);
        }
        finally {
           gw.close();
        }
	}
		
	
	/**
     * (130.1)DR 프로그램 참여 유도
     * @param mcuId
     * @param drLevelEntry
     * @throws FMPMcuException
     * @throws Exception
     */
	@Override
	public void cmdDRAgreement(String mcuId, drLevelEntry drLevelEntry) throws Exception{
		log.info(" cmdDRAgreement["+mcuId+"], drLevelEntry["+drLevelEntry+"]");
        CommandGW gw = getCommandGW();
        try {
           gw.cmdDRAgreement(mcuId, drLevelEntry);
        }
        finally {
            gw.close();
        }
	}
	
	/**
	 * (130.2)DR 취소 메시지
	 * @param mcuId
	 * @param deviceId
	 * @throws FMPMcuException
	 * @throws Exception
	 */
	@Override
	public void cmdDRCancel(String mcuId, String deviceId) throws Exception{
		log.info(" cmdDRCancel["+mcuId+"], deviceId["+deviceId+"]");
        CommandGW gw = getCommandGW();
        try {
           gw.cmdDRCancel(mcuId, deviceId);
        }
        finally {
            gw.close();
        }
	}
	
	/**
	 * (130.3)Incentive DR Start
	 * @param mcuId
	 * @param idrEntry
	 * @throws FMPMcuException
	 * @throws Exception
	 */
	@Override
	public void cmdIDRStart(String mcuId, idrEntry idrEntry)
			throws FMPMcuException, Exception {
		log.info(" cmdIDRStart["+mcuId+"], idrEntry["+idrEntry+"]");
        CommandGW gw = getCommandGW();
        try {
           gw.cmdIDRStart(mcuId, idrEntry);
        }
        finally {
            gw.close();
        }		
	}
	
	/**
	 * (130.4)Incentive DR Cancel
	 * @param mcuId
	 * @param eventId
	 * @throws FMPMcuException
	 * @throws Exception
	 */
	@Override
	public void cmdIDRCancel(String mcuId, String eventId)
			throws FMPMcuException, Exception {
		log.info(" cmdIDRCancel["+mcuId+"], eventId["+eventId+"]");
        CommandGW gw = getCommandGW();
        try {
           gw.cmdIDRCancel(mcuId, eventId);
        }
        finally {
            gw.close();
        }		
	}
	
	/**
	 * (130.5)DR Level Monitoring 장비의 DR Level 조회
	 * @param mcuId
	 * @param deviceId
	 * @return
	 * @throws FMPMcuException
	 * @throws Exception
	 */
	@Override
	public endDeviceEntry cmdGetDRLevel(String mcuId,
			String deviceId) throws FMPMcuException, Exception {
		log.info(" cmdGetDRLevel["+mcuId+"], deviceId["+deviceId+"]");
        CommandGW gw = getCommandGW();
        try {
           return gw.cmdGetDRLevel(mcuId, deviceId);
        }
        finally {
            gw.close();
        }
	}	

	/**
	 * (130.6)DR Level Control DR Level 제어
	 * @param mcuId
	 * @param endDeviceEntry
	 * @throws FMPMcuException
	 * @throws Exception
	 */
	@Override
	public void cmdSetDRLevel(String mcuId, endDeviceEntry endDeviceEntry)
			throws FMPMcuException, Exception {
		log.info(" cmdSetDRLevel["+mcuId+"], endDeviceEntry["+endDeviceEntry+"]");
        CommandGW gw = getCommandGW();
        try {
           gw.cmdSetDRLevel(mcuId, endDeviceEntry);
        }
        finally {
            gw.close();
        }
	}
	
	/**
	 * (130.7)가전기기 스마트 기기 제어
	 * @param mcuId
	 * @param serviceId
	 * @param deviceId
	 * @param eventId
	 * @param drLevel
	 * @throws FMPMcuException
	 * @throws Exception
	 */
	@Override
	public void cmdEndDeviceControl(String mcuId, String serviceId,
			String deviceId, String eventId, String drLevel)
			throws FMPMcuException, Exception {
		log.info(" cmdEndDeviceControl["+mcuId+"], serviceId["+serviceId+"] deviceId["+deviceId+"] eventId["+eventId+"] drLevel["+drLevel+"]");
        CommandGW gw = getCommandGW();
        try {
           gw.cmdEndDeviceControl(mcuId, serviceId, deviceId, eventId, drLevel);
        }
        finally {
            gw.close();
        }		
	}

	//TODO 파라미터가 deviceId인 경우가 있고, groupName인 경우가 있는데 어떻게 구분할까?
	/**
	 * (130.8)DR 자원 정보 요청
	 * @param mcuId
	 * @param deviceId
	 * @return
	 * @throws FMPMcuException
	 * @throws Exception
	 */
	@Override
	public endDeviceEntry cmdGetDRAsset(String mcuId, String deviceId)
			throws FMPMcuException, Exception {
		log.info(" cmdGetDRAsset["+mcuId+"], deviceId["+deviceId+"]");
        CommandGW gw = getCommandGW();
        try {
           return gw.cmdGetDRAsset(mcuId, deviceId);
        }
        finally {
            gw.close();
        }
	}

	

	
}
