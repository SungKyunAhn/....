package com.aimir.fep.command.mbean;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.aimir.constants.CommonConstants.MeterProgramKind;
import com.aimir.constants.CommonConstants.OTAType;
import com.aimir.fep.meter.data.MeterData;
import com.aimir.fep.meter.data.Response;
import com.aimir.fep.meter.parser.plc.PLCDataFrame;
import com.aimir.fep.modem.BatteryLog;
import com.aimir.fep.modem.EventLog;
import com.aimir.fep.modem.ModemROM;
import com.aimir.fep.protocol.fmp.common.Target;
import com.aimir.fep.protocol.fmp.datatype.SMIValue;
import com.aimir.fep.protocol.fmp.exception.FMPException;
import com.aimir.fep.protocol.fmp.exception.FMPMcuException;
import com.aimir.fep.protocol.fmp.frame.service.Entry;
import com.aimir.fep.protocol.fmp.frame.service.entry.codiBindingEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.codiDeviceEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.codiEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.codiMemoryEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.codiNeighborEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.drLevelEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.endDeviceEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.ffdEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.gpioEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.idrEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.loadControlSchemeEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.loadLimitPropertyEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.loadLimitSchemeEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.loadShedSchemeEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.memEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.meterDataEntry2;
import com.aimir.fep.protocol.fmp.frame.service.entry.meterEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.mobileEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.modemG3Entry;
import com.aimir.fep.protocol.fmp.frame.service.entry.pluginEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.procEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.sensorInfoNewEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.sysEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.timeEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.trInfoEntry;
import com.aimir.fep.util.FileInfo;
import com.aimir.fep.util.GroupInfo;
import com.aimir.fep.util.GroupTypeInfo;
import com.aimir.model.device.Modem;
import com.aimir.model.system.MeterProgram;

/**
 * Command Proxy MBean which execute to MCU
 *
 * @author Y.S Kim
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public interface CommandGWMBean
{
    /**
     * start CommandGW
     */
    public void start() throws Exception;

    /**
     * get CommandGW ObjectName String
     */
    public String getName();

    /**
     * stop CommandGW
     */
    public void stop();


    /**
     * MCU Diagnosis
     *
     * @param mcuId MCU Indentifier
     * @return status (MCU status, GSM status, Sink 1 status, Sink 2 status)
     * @throws FMPMcuException, Exception
     */
    public Hashtable cmdMcuDiagnosis(String mcuId)
        throws FMPMcuException, Exception;

    /**
     * MCU Reset
     *
     * @param mcuId MCU Indentifier
     * @throws FMPMcuException, Exception
     */
    public void cmdMcuReset(String mcuId)
        throws FMPMcuException, Exception;

    /**
     * MCU Shutdown
     *
     * @param mcuId MCU Indentifier
     * @throws FMPMcuException, Exception
     */
    public void cmdMcuShutdown(String mcuId)
        throws FMPMcuException, Exception;

    /**
     * MCU FactoryDefault
     *
     * @param mcuId MCU Indentifier
     * @throws FMPMcuException, Exception
     */
    public void cmdMcuFactoryDefault(String mcuId)
        throws FMPMcuException, Exception;

    /**
     * MCU Get Time
     *
     * @param mcuId MCU Indentifier
     * @return time String (yyyymmddhhmmss)
     * @throws FMPMcuException, Exception
     */
    public String cmdMcuGetTime(String mcuId)
        throws FMPMcuException, Exception;

    /**
     * MCU Set Time
     *
     * @param mcuId MCU Indentifier
     * @param time MCU Time (yyyymmddhhmmss)
     * @throws FMPMcuException, Exception
     */
    public void cmdMcuSetTime(String mcuId, String time)
        throws FMPMcuException, Exception;

    /**
     * MCU Set GMT
     *
     * @param mcuId MCU Indentifier
     * @return GMT
     * @throws FMPMcuException, Exception
     */
    public long cmdMcuSetGMT(String mcuId)
        throws FMPMcuException, Exception;

    /**
     * MCU Set DST
     *
     * @param mcuId MCU Indentifier
     * @param fileName
     * @throws FMPMcuException, Exception
     */
    public void cmdMcuSetDST(String mcuId, String fileName)
        throws FMPMcuException, Exception;

    /**
     * MCU Get State
     *
     * @param mcuId MCU Indentifier
     * @return mcu status
     * @throws FMPMcuException, Exception
     */
    public String cmdMcuGetState(String mcuId)
        throws FMPMcuException, Exception;

    /**
     * MCU Loopback
     *
     * @param mcuId MCU Indentifier
     * @return result (true, false)
     * @throws FMPMcuException, Exception
     */
    public boolean cmdMcuLoopback(String mcuId)
        throws FMPMcuException, Exception;

    /**
     * MCU Clear Static
     *
     * @param mcuId MCU Indentifier
     * @throws FMPMcuException, Exception
     */
    public void cmdMcuClearStatic(String mcuId)
        throws FMPMcuException, Exception;

    /**
     * MCU Get System Info (Not Available)
     *
     * @param mcuId MCU Indentifier
     * @return mcu system infomation entry
     * @throws FMPMcuException, Exception
     */
    public sysEntry cmdMcuGetSystemInfo(String mcuId)
        throws FMPMcuException, Exception;

    /**
     * MCU Get Environment (Not Available)
     *
     * @param mcuId MCU Indentifier
     * @return Environment entrys (varEntry, varValueEntry, varSubValueEntry)
     * @throws FMPMcuException, Exception
     */
    public Entry[] cmdMcuGetEnvironment(String mcuId)
        throws FMPMcuException, Exception;

    /**
     * MCU Get GPIO (Not Available)
     *
     * @param mcuId MCU Indentifier
     * @return GIO Entry
     * @throws FMPMcuException, Exception
     */
    public gpioEntry cmdMcuGetGpio(String mcuId)
        throws FMPMcuException, Exception;

    /**
     * MCU Set Gpio (Not Available)
     *
     * @param mcuId MCU Indentifier
     * @param portNumber GPIO Port Number
     * @param value GPIO Value(0/1)
     * @throws FMPMcuException, Exception
     */
    public void cmdMcuSetGpio(String mcuId, int portNumber, int value)
        throws FMPMcuException, Exception;

    /**
     * MCU Get Ip
     *
     * @param mcuId MCU Indentifier
     * @return ipaddr
     * @throws FMPMcuException, Exception
     */
    public Hashtable cmdMcuGetIp(String mcuId) throws FMPMcuException, Exception;

    /**
     *
     * --added
     * MCU Get Memory
     *
     * @param mcuId
     * @return memEntry
     * @throws FMPException, Exception
     */
    public memEntry cmdMcuGetMemory(String mcuId)
        throws FMPMcuException, Exception;

    /**
     * --add
     * Mcu Get Process
     * @param mcuId
     * @return procEntry
     * @throws FMPMcuException, Exception
     */
    public procEntry[] cmdMcuGetProcess(String mcuId)
        throws FMPMcuException, Exception;

    /**
     * --add
     * MCU Get File System
     * @param mcuId
     * @return
     * @throws FMPMcuException, Exception
     */
    public Hashtable cmdMcuGetFileSystem(String mcuId)
        throws FMPMcuException, Exception;

    /**
     * --add
     * MCU Get Plugin
     * @param mcuId
     * @return pluginEntry
     * @throws FMPMcuException, Exception
     */
    public pluginEntry[] cmdMcuGetPlugin(String mcuId)
        throws FMPMcuException, Exception;

    /**
     * --add
     * MCU Get Mobile
     * @param mcuId
     * @return mobileEntry
     * @throws FMPMcuException, Exception
     */
    public mobileEntry cmdMcuGetMobile(String mcuId)
        throws FMPMcuException, Exception;

    /**
     * MCU Reset Device
     *
     * @param mcuId MCU Indentifier
     * @throws FMPMcuException, Exception
     */
    public void cmdMcuResetDevice(String mcuId, int device)
        throws FMPMcuException, Exception;

    /**
     * Add Modem
     *
     * @param mcuId MCU Indentifier
     * @param modem Modem
     * @throws FMPMcuException, Exception
     */
    public void cmdAddModem(String mcuId, Modem modem)
        throws FMPMcuException, Exception;

    /**
     * Add Sensor MO
     *
     * @param mcuId MCU Indentifier
     * @param modems Modem List
     * @throws FMPMcuException, Exception
     */
    public void cmdAddModems(String mcuId, Modem[] modem)
        throws FMPMcuException, Exception;

    /**
     * MCU Delete Modem
     *
     * @param mcuId MCU Indentifier
     * @param propName property name
     * @param propValue property value
     * @throws FMPMcuException, Exception
     */
    public void cmdDeleteModem(String mcuId, String propName, String propValue)
        throws FMPMcuException, Exception;

    /**
     * MCU Delete Sensor
     *
     * @param mcuId MCU Indentifier
     * @param modem modem id
     * @throws FMPMcuException, Exception
     */
    public void cmdDeleteModem(String mcuId, String modemId)
        throws FMPMcuException, Exception;

    /**
     * MCU Delete Sensor
     *
     * @param mcuId MCU Indentifier
     * @param modemId modem id
     * @throws FMPMcuException, Exception
     */
    public void cmdDeleteModem(String mcuId, String modemId, int channelId, int panId)
        throws FMPMcuException, Exception;

    /**
     * MCU Delete Sensor All
     *
     * @param mcuId MCU Indentifier
     * @throws FMPMcuException, Exception
     */
    public void cmdDeleteModemAll(String mcuId)
        throws FMPMcuException, Exception;

    /**
     * MCU Delete Modem All
     *
     * @param mcuId MCU Indentifier
     * @throws FMPMcuException, Exception
     */
    public void cmdDeleteModemAll(String mcuId, int channelId, int panId)
        throws FMPMcuException, Exception;

    /**
     * MCU Delete Member All(SAT2)
     *
     * @param mcuId MCU Indentifier
     * @throws FMPMcuException, Exception
     */
    public void cmdDeleteMemberAll(String mcuId)
        throws FMPMcuException, Exception;

    /**
     * Reset Modem
     *
     * @param mcuId MCU Indentifier
     * @param modemId Modem Indentifier
     * @param resetTime 무슨값인지 모름(조차장님한테 확인)
     * @throws FMPMcuException, Exception
     */
    public void cmdResetModem(String mcuId, String modemId, int resetTime)
        throws FMPMcuException, Exception;

    /**
     * Get New Modem Info List
     *
     * @param mcuId MCU Indentifier
     * @return new modem entry list
     * @throws FMPMcuException, Exception
     */
    public sensorInfoNewEntry[] cmdGetModemAllNew(String mcuId)
        throws FMPMcuException, Exception;

    /**
     * Get New Modem Info
     *
     * @param mcuId MCU ID
     * @return new modem
     * @throws FMPMcuException, Exception
     */
    public sensorInfoNewEntry cmdGetModemInfoNew(String modemId)
            throws FMPMcuException, Exception;
    
    /**
     * Get Modem Count
     * --add
     * @param mcuId MCU Indentifier
     * @throws FMPMcuException, Exception
     * @return sensor count
     */
    public int cmdGetModemCount(String mcuId)
        throws FMPMcuException, Exception;

    /**
     * Clear Communication Log
     *
     * @param mcuId MCU Indentifier
     * @throws FMPMcuException, Exception
     */
    public void cmdClearCommLog(String mcuId)
        throws FMPMcuException, Exception;

    /**
     * Clear Command History Log
     *
     * @param mcuId MCU Indentifier
     * @throws FMPMcuException, Exception
     */
    public void cmdClearCmdHistLog(String mcuId)
        throws FMPMcuException, Exception;

    /**
     * Clear Mcu Event Log
     *
     * @param mcuId MCU Indentifier
     * @throws FMPMcuException, Exception
     */
    public void cmdClearEventLog(String mcuId)
        throws FMPMcuException, Exception;

    /**
     * Clear Metering Log
     *
     * @param mcuId MCU Indentifier
     * @throws FMPMcuException, Exception
     */
    public void cmdClearMeterLog(String mcuId)
        throws FMPMcuException, Exception;

    /**
     * Get Cmd Hist Log List
     *
     * @param mcuId MCU Indentifier
     * @param fromTime From Time
     * @param toTime To Time
     * @return file information list
     * @throws FMPMcuException, Exception
     */
    public FileInfo[] cmdGetCmdHistList(String mcuId, String fromTime,
            String toTime) throws FMPMcuException, Exception;

    /**
     * Get Meter Log List
     *
     * @param mcuId MCU Indentifier
     * @param fromTime From Time
     * @param toTime To Time
     * @return file information list
     * @throws FMPMcuException, Exception
     */
    public FileInfo[] cmdGetMeterLogList(String mcuId, String fromTime,
            String toTime) throws FMPMcuException, Exception;

    /**
     * Get Event Log List
     *
     * @param mcuId MCU Indentifier
     * @param fromTime From Time
     * @param toTime To Time
     * @return file information list
     * @throws FMPMcuException, Exception
     */
    public FileInfo[] cmdGetEventLogList(String mcuId, String fromTime,
            String toTime) throws FMPMcuException, Exception;

    /**
     * Get Communication Log List
     *
     * @param mcuId MCU Indentifier
     * @param fromTime From Time
     * @param toTime To Time
     * @return file information list
     * @throws FMPMcuException, Exception
     */
    public FileInfo[] cmdGetCommLogList(String mcuId, String fromTime,
            String toTime) throws FMPMcuException, Exception;

    /**
     * Get Mobile Log List
     *
     * @param mcuId MCU Indentifier
     * @param fromTime From Time
     * @param toTime To Time
     * @return file information list
     * @throws FMPMcuException, Exception
     */
    public FileInfo[] cmdGetMobileLogList(String mcuId, String fromTime,
            String toTime) throws FMPMcuException, Exception;

    /**
     * Get All Log List
     *
     * @param mcuId MCU Indentifier
     * @param fromTime From Time
     * @param toTime To Time
     * @return file information list
     * @throws FMPMcuException, Exception
     */
    public FileInfo[] cmdGetAllLogList(String mcuId, String fromTime,
            String toTime) throws FMPMcuException, Exception;

    /**
     * Metering
     *
     * @param mcuId MCU Indentifier
     * @param propName property name
     * @param propValue property value
     * @throws FMPMcuException, Exception
     */
    public void cmdMetering(String mcuId, String propName, String propValue)
        throws FMPMcuException, Exception;    
    
    /**
     * Metering All
     *
     * @param mcuId MCU Indentifier
     * @throws FMPMcuException, Exception
     */
    public void cmdMeteringAll(String mcuId, int nOption, int offset, int count)
            throws FMPMcuException, Exception;

    /**
     * Metering All
     *
     * @param mcuId MCU Indentifier
     * @throws FMPMcuException, Exception
     */
    public void cmdMeteringAll(String mcuId)
        throws FMPMcuException, Exception;
    
    /**
     * Metering Recovery
     *
     * @param mcuId MCU Indentifier
     * @throws FMPMcuException, Exception
     */
    public void cmdRecovery(String mcuId, int nOption, int offset, int count)
        throws FMPMcuException, Exception;

    /**
     * Metering Recovery
     *
     * @param mcuId MCU Indentifier
     * @throws FMPMcuException, Exception
     */
    public void cmdRecovery(String mcuId)
        throws FMPMcuException, Exception;

    /**
     * Metering Recovery
     *
     * @param mcuId MCU Indentifier
     * @throws FMPMcuException, Exception
     */
    public void cmdRecovery(String mcuId, String targetTime)
        throws FMPMcuException, Exception;

    /**
     * Get Meter
     *
     * @param mcuId MCU Indentifier
     * @param sensorId Sensor Indentifier
     * @param fromTime From Time
     * @param toTime To Time
     * @return energy meter data list
     * @throws FMPMcuException, Exception
     */
    public MeterData[] cmdGetMeter(String mcuId, String meterId,
            String fromTime, String toTime) throws FMPMcuException, Exception;
    
    /**
     * Get Meter
     *
     * @param mcuId MCU Indentifier
     * @return energy meter data list
     * @throws FMPMcuException, Exception
     */
    public meterEntry[] cmdGetMeterList(String mcuId, int nOption, String value) 
            throws FMPMcuException, Exception;

    /**
     * Get Meter
     *
     * @param mcuId MCU Indentifier
     * @return energy meter data list
     * @throws FMPMcuException, Exception
     */
    public meterDataEntry2[] cmdGetMeterList(String mcuId, int nOption, String[] meterId) 
            throws FMPMcuException, Exception;
    
    /**
     * Get Meter
     *
     * @param mcuId MCU Indentifier
     * @param meterId Meter Indentifier
     * @param fromTime From Time
     * @param toTime To Time
     * @return energy meter data list
     * @throws FMPMcuException, Exception
     */
    public MeterData cmdGetMeterInfo(String mcuId, String meterId)
        throws FMPMcuException, Exception;

    /**
     * Get Meter
     *
     * @param mcuId MCU Indentifier
     * @param modemId Modem Indentifier
     * @return energy meter data list
     * @throws FMPMcuException, Exception
     */
    public MeterData cmdGetMeterInfoFromModem(String mcuId, String modemId)
        throws FMPMcuException, Exception;

    /**
     * Read Table
     *
     * @param mcuId MCU Indentifier
     * @param meterId
     * @param tablenum
     * @return energy meter data list
     * @throws FMPMcuException, Exception
     */
    public byte[] cmdReadTable(String mcuId, String meterId, int tablenum)
        throws FMPMcuException, Exception;

    /**
     * Read Table
     *
     * @param mcuId MCU Indentifier
     * @param sensorId
     * @param tablenum
     * @param data stream
     * @throws FMPMcuException, Exception
     */
    public byte[] cmdWriteTable(String mcuId, String meterId, int tablenum, byte[] stream)
        throws FMPMcuException, Exception;

    /**
     * Read Table Relay Switch Status(GE SM110)
     *
     * @param mcuId MCU Identifier
     * @param meterId Meter Identifier
     * @throws Exception
     */
    public Map<String,String> getRelaySwitchStatus( String mcuId, String meterId )
        throws Exception;

    /**
     * Write Table Relay Switch and Activate On/Off
     *
     * @param mcuId MCU Identifier
     * @param meterId meter Identifier
     * @throws FMPMcuException, Exception
     */
    public Map<String,String> cmdRelaySwitchAndActivate( String mcuId, String meterId, String cmdNum)
        throws FMPMcuException, Exception;
    /**
     * Aidon MCCB
     *
     * @param mcuId MCU Indentifier
     * @param meterId Meter Indentifier
     * @param req mccb control request
     * @return response message
     * @throws FMPMcuException, Exception
     */
    public String cmdAidonMCCB(String mcuId, String meterId, String req)
        throws FMPMcuException, Exception;

    /**
     * Kamstrup CID
     *
     * @param mcuId MCU Indentifier
     * @param sensorId Sensor Indentifier
     * @param req mccb control request
     * @return response message
     * @throws FMPMcuException, Exception
     */
    public byte[] cmdKamstrupCID(String mcuId, String meterId, String[] req)
        throws FMPMcuException, Exception;

    /**
     * Kamstrup CID for Kamstrup GPRS Modem
     *
     * @param mcuId MCU Indentifier
     * @param meterId Meter Indentifier
     * @param kind commandKind
     * @param param mccb control request
     * @return response message
     * @throws FMPMcuException, Exception
     */
    public byte[] cmdKamstrupCID(String mcuId, String meterId, String kind, String[] param)
        throws FMPMcuException, Exception;

    /**
     * Get Meter All
     *
     * @param mcuId MCU Indentifier
     * @param fromTime from Time
     * @param toTime from Time
     * @return energy meter data list
     * @throws FMPMcuException, Exception
     */
    public MeterData[] cmdGetMeterAll(String mcuId, String fromTime, String toTime)
        throws FMPMcuException, Exception;

    /**
     * On-demand Meter
     *
     * @param mcuId MCU Indentifier
     * @param meterId Sensor Indentifier
     * @return energy meter data
     * @throws FMPMcuException, Exception
     */
    public MeterData cmdOnDemandMeter(String mcuId, String meterId,
                                            String sensorId, String nOption,
                                            String fromDate, String toDate)
        throws FMPMcuException, Exception;

    /**
     * cmdOnDemandMeter
     *
     * @param mcuId MCU Indentifier
     * @param meterId
     * @param nOption (3~7)
     * @return resultData
     * @throws FMPMcuException, Exception
     */
    public Map cmdOnDemandMeter(String mcuId, String meterId, int nOption)
        throws FMPMcuException, Exception;

    /**
     * On-demand Meter for kamstrup mmiu meter
     *
     * @param mcuId MCU Indentifier
     * @param meterId Sensor Indentifier
     * @return energy meter data
     * @throws FMPMcuException, Exception
     */
    public String[] cmdOnDemandMeter(String mcuId, String meterId, String sensorId,
            String kind, String[] param)
        throws FMPMcuException, Exception;

    /**
     * On-demand Meter for kamstrup mmiu meter
     *
     * @param mcuId MCU Indentifier
     * @param meterId Sensor Indentifier
     * @return no return data (async data request)
     * @throws FMPMcuException, Exception
     */
    public void cmdOnDemandMeterAsync(String mcuId, String meterId, String sensorId,
            String kind, String[] param)
        throws FMPMcuException, Exception;

    /**
     * set conf for ieiu modem
     *
     * @param mcuId MCU Indentifier
     * @param meterId Sensor Indentifier
     * @return no return data (async data request)
     * @throws FMPMcuException, Exception
     */
    public byte[] cmdSetIEIUConf(String mcuId, String meterId, String sensorId, String kind, String[] param)
        throws FMPMcuException, Exception;

    /**
     * On-demand Meter
     * @param mcuId MCU Indentifier
     * @param meterId Sensor Indentifier
     * @param sensorId
     * @param nPort
     * @param nOption
     * @param fromDate
     * @param toDate
     * @return energy meter data
     * @throws FMPMcuException
     * @throws Exception
     */
    public MeterData cmdOnDemandMBus(String mcuId, String meterId,
                                            String sensorId, String nPort, String nOption,
                                            String fromDate, String toDate)
        throws FMPMcuException, Exception;

    /**
     * Request PLC Data Frame
     * @param mcuId
     * @param pdf
     * @return
     * @throws Exception
     */
    public PLCDataFrame requestPLCDataFrame(String mcuId, PLCDataFrame pdf) throws Exception;

    /**
     * On-demand Meter All
     *
     * @param mcuId MCU Indentifier
     * @return energy meter data list
     * @throws FMPMcuException, Exception
     */
    public MeterData[] cmdOnDemandMeterAll(String mcuId)
        throws FMPMcuException, Exception;

    /**
     * Get Current Meter
     *
     * @param mcuId MCU Indentifier
     * @param isMeter meter(true) or sensor(false)
     * @param deviceId meter serial number or sensor id
     * @return energy meter data
     * @throws FMPMcuException, Exception
     */
    public MeterData cmdGetCurMeter(String mcuId, String deviceId, boolean isMeter)
        throws FMPMcuException, Exception;

    /**
     * Get Current Meter All
     *
     * @param mcuId MCU Indentifier
     * @return energy meter data list
     * @throws FMPMcuException, Exception
     */
    public MeterData[] cmdGetCurMeterAll(String mcuId)
        throws FMPMcuException, Exception;

    /**
     * Report Current Meter (Retry)
     *
     * @param mcuId MCU Indentifier
     * @throws FMPMcuException, Exception
     */
    public void cmdReportCurMeter(String mcuId)
        throws FMPMcuException, Exception;

    /**
     * --add
     * Get Meter Time
     *
     * @param mcuId
     * @param sensorId
     * @return timeEntry
     * @throws FMPMcuException, Exception
     */
    public timeEntry cmdGetMeterTime(String mcuId, String sensorId)
        throws FMPMcuException, Exception;

    /**
     * --add
     *
     * Set Meter Time
     * @param mcuId MCU Identifier
     * @param meterId Meter Identifier
     * @param time
     * @throws FMPMcuException, Exception
     */
    public void cmdSetMeterTime(String mcuId, String meterId, String time)
        throws FMPMcuException, Exception;

    /**
     * --add
     * Get Phone List
     * @param mcuId
     * @return stringEntry
     * @throws FMPMcuException, Exception
     */
    public String[] cmdGetPhoneList(String mcuId)
        throws FMPMcuException, Exception;

    /**
     * --add
     * Set Phone List
     *
     * @param mcuId
     * @param phoneNumber
     * @throws FMPMcuException, Exception
     */
    public void cmdSetPhoneList(String mcuId, String[] phoneNumber)
        throws FMPMcuException, Exception;

    /**
     * -add
     * Meter Upload Range
     *
     * @param mcuId
     * @param startTime
     * @param endTime
     * @throws FMPMcuException, Exception
     */
    public void cmdMeterUploadRange(String mcuId, String startTime, String endTime)
        throws FMPMcuException, Exception;

    /**
     * Install File
     *
     * @param mcuId MCU Indentifier
     * @param filename filename
     * @param type install type
     * @param reservationTime reservation time
     * @throws FMPMcuException, Exception
     */
    public void cmdInstallFile(String mcuId, String filename)
        throws FMPMcuException, Exception;

    /**
     * notification firmware update
     *
     * @param mcuId MCU Indentifier
     * @param filename filename
     * @param type install type
     * @param reservationTime reservation time
     * @throws FMPMcuException, Exception
     */
    public void cmdFirmwareUpdate(String mcuId) throws FMPMcuException, Exception;

    /**
     * Install File
     *
     * @param mcuId MCU Indentifier
     * @param filename filename
     * @param type install type
     * @param reservationTime reservation time
     * @throws FMPMcuException, Exception
     */
    public void cmdInstallFile(String mcuId, String filename, int type,
            String reservationTime) throws FMPMcuException, Exception;

    /**
     * Put File
     *
     * @param mcuId MCU Indentifier
     * @param filename filename
     * @throws FMPMcuException, Exception
     */
    public void cmdPutFile(String mcuId, String filename)
                throws FMPMcuException, Exception;

    /**
     * Get File
     *
     * @param mcuId MCU Indentifier
     * @param filename filename
     * @throws FMPMcuException, Exception
     */
    public void cmdGetFile(String mcuId, String filename)
                throws FMPMcuException, Exception;

    /**
     * Set Request Config Node(Upgrade)
     *
     * @param mcuId MCU Indentifier
     * @param upgradeType upgradeType
     * @param controlCode controlCode
     * @param imageKey imageKey
     * @param imageUrl imageUrl
     * @param checkSum checkSum
     * @param filter filter
     * @param filterValue filterValue
     * @return moproperty
     * @throws FMPMcuException, Exception
     */
	public Hashtable cmdReqNodeUpgradeEVN(String mcuId, int upgradeType,
			   int controlCode, String imageKey,
			   String imageUrl, String checkSum,
			   int[] filter, String[] filterValue) 
		throws FMPMcuException, Exception;
	
    /**
     * Change Device Node Status 
     *
     * @param mcuId MCU Indentifier
     * @param int requestId
     * @param int opCode
     * @throws FMPMcuException, Exception
     */
	public void cmdCtrlUpgradeRequestEVN(String mcuId, int requestId, int opCode) 
		throws FMPMcuException, Exception;
	
    /**
     *  Create Bypass Tunnel
     *
     * @param mcuId MCU Indentifier
     * @param String meterId
     * @param int msgTimeout
     * @param int tunnelTimeout
     * @return moproperty
     * @throws FMPMcuException, Exception
     */
	public Hashtable cmdCreateTunnel(String mcuId, String meterId, 
			   int msgTimeout, int tunnelTimeout) 
		throws FMPMcuException, Exception;
		
    /**
     * Connect Bypass Tunnel
     *
     * @param mcuId MCU Indentifier
     * @throws FMPMcuException, Exception
     */
	public Hashtable cmdConnectByPass(String mcuId, int portNumber, String[] param) 
			throws FMPMcuException, Exception;
	
    /**
     * Delete Bypass Tunnel
     *
     * @param mcuId MCU Indentifier
     * @param String meterId
     * @throws FMPMcuException, Exception
     */
	public void cmdDeleteTunnel(String mcuId, String meterId) 
			throws FMPMcuException, Exception;
    
    /**
     * MCU Standard Get
     *
     * @param mcuId MCU Indentifier
     * @param mop MOPROPERTY
     * @return moproperty
     * @throws FMPMcuException, Exception
     */
    public Hashtable cmdStdGet(String mcuId, String propName)
        throws FMPMcuException, Exception;

    /**
     * MCU Standard Get (Many Properties)
     *
     * @param mcuId MCU Indentifier
     * @param mop MOPROPERTYs
     * @return moproperties
     * @throws FMPMcuException, Exception
     */
    public Hashtable cmdStdGet(String mcuId, String[] propNames)
        throws FMPMcuException, Exception;
    
    /**
     * MCU Standard Get
     */
	public Map<String, Object> cmdMcuStdGet(String mcuId, String oid) 
		throws FMPMcuException, Exception;
    
    /**
     * MCU Entry Standard Get
     *
     * @param mcuId MCU Indentifier
     * @param index Index
     * @param propNames properties
     * @return moproperties
     * @throws FMPMcuException, Exception
     */
    public Hashtable cmdEntryStdGet(String mcuId, String index,
            String[] propNames) throws FMPMcuException, Exception;

    /**
     * MCU Standard Get Child
     *
     * @param mcuId MCU Indentifier
     * @param oid OID
     * @return moproperties
     * @throws FMPMcuException, Exception
     */
    public Hashtable cmdStdGetChild(String mcuId, String oid)
        throws FMPMcuException, Exception;

    /**
     * MCU Standard Set
     *
     * @param mcuId MCU Indentifier
     * @param propName property name
     * @param propValue property value
     * @throws FMPMcuException, Exception
     */
    public void cmdStdSet(String mcuId, String propName, String propValue)
        throws FMPMcuException, Exception;

    /**
     * MCU Standard Set (Many Properties)
     *
     * @param mcuId MCU Indentifier
     * @param propNames properties
     * @param propValues property values
     * @throws FMPMcuException, Exception
     */
    public void cmdStdSet(String mcuId, String[] propNames, String[] propValues)
        throws FMPMcuException, Exception;

    /**
     * MCU Entry Standard Set
     *
     * @param mcuId MCU Indentifier
     * @param index Index
     * @param propNames properties
     * @param propValues property value
     * @throws FMPMcuException, Exception
     */
    public void cmdEntryStdSet(String mcuId, String index, String[] propNames, String[] propValues)
        throws FMPMcuException, Exception;

    /**
     * --added 2007.05.22
     * Write Table Meter Time Sync to MCU
     *
     * @param mcuId MCU Identifier
     * @param meterId meter Identifier
     * @throws FMPMcuException, Exception
     */
    public byte[] cmdMeterTimeSync( String mcuId, String meterId)
        throws FMPMcuException, Exception;

    /**
     * @param mcuId
     * @param modemId
     * @param nOption
     * @param startDate
     * @param endDate
     * @return
     * @throws FMPMcuException
     * @throws Exception
     */
    public Object cmdGetMeterSchedule(String mcuId,
            String modemId, int nOption, String fromDate, String toDate)
    throws FMPMcuException, Exception;
    
    /**
     * --added 2007.06.05
     *
     * @param mcuId MCU Identifier
     * @param sensorId sensor Identifier
     * @throws FMPMcuException, Exception
     */
    public Object cmdGetMeterSchedule( String mcuId, String sensorId, int nOption,
                                       int nOffset, int nCount)
        throws FMPMcuException, Exception;

    /**
     * --added 2007.05.26
     * Get mcu configuration file(OID 100.26)
     *
     * @param mcuId MCU Identifier
     * @throws FMPMcuException, Exception
     */
    public String[]  cmdGetConfiguration(String mcuId)
        throws FMPMcuException, Exception;

    /**
     * --added 2007.05.26
     * mcu configuration file setting(OID 100.27)
     *
     * @param mcuId MCU Identifier
     * @throws FMPMcuException, Exception
     */
    public void cmdSetConfiguration(String mcuId)
        throws FMPMcuException, Exception;

    /**
     * --added 2007.09.06
     * Modem ROM Read
     *
     * @param mcuId MCU Identifier
     * @param modemId Modem Identifier
     * @param args
     * @return modem rom information
     * @throws FMPMcuException, Exception
     */
    public ModemROM cmdGetModemROM(String mcuId, String modemId, int[][] args)
    throws FMPMcuException, Exception;

    /**
     * --added 2007.09.06
     * Modem ROM Read
     *
     * @param mcuId MCU Identifier
     * @param modemId Modem Identifier
     * @param address rom address
     * @param length
     * @return sensor rom information
     * @throws FMPMcuException, Exception
     */
    public byte[] cmdGetModemROM(String mcuId, String modemId, int address, int length)
    throws FMPMcuException, Exception;

    /**
     * --added 2007.09.06
     * Modem ROM write
     *
     * @param mcuId MCU Identifier
     * @param modemId Modem Identifier
     * @param address rom address
     * @param data
     * @throws FMPMcuException, Exception
     */
    public void cmdSetModemROM(String mcuId, String modemId, int address, byte[] data)
    throws FMPMcuException, Exception;

    /**
     * --added 2007.10.02
     * Get Codi List
     *
     * @param mcuId MCU Indentifier
     * @throws FMPMcuException, Exception
     */
    public codiEntry[] cmdGetCodiList(String mcuId)
        throws FMPMcuException, Exception;

    /**
     * --added 2007.10.02
     * Get Codi Info
     *
     * @param mcuId MCU Indentifier
     * @param codiIndex codi Indentifier
     * @throws FMPMcuException, Exception
     */
    public codiEntry cmdGetCodiInfo(String mcuId, int codiIndex)
        throws FMPMcuException, Exception;

    /**
     * --added 2007.10.02
     * Get Codi Info
     *
     * @param mcuId MCU Indentifier
     * @throws FMPMcuException, Exception
     */
    public codiEntry cmdGetCodiInfo(String mcuId)
        throws FMPMcuException, Exception;

    /**
     * --added 2007.10.02
     * Get Codi Device
     *
     * @param mcuId MCU Indentifier
     * @throws FMPMcuException, Exception
     */
    public codiDeviceEntry cmdGetCodiDevice(String mcuId)
        throws FMPMcuException, Exception;

    /**
     * --added 2007.10.02
     * Get Codi Device
     *
     * @param mcuId MCU Indentifier
     * @param codiIndex codi Indentifier
     * @throws FMPMcuException, Exception
     */
    public codiDeviceEntry cmdGetCodiDevice(String mcuId, int codiIndex)
        throws FMPMcuException, Exception;

    /**
     * --added 2007.10.02
     * Get Codi Device
     *
     * @param mcuId MCU Indentifier
     * @throws FMPMcuException, Exception
     */
    public codiBindingEntry cmdGetCodiBinding(String mcuId)
        throws FMPMcuException, Exception;

    /**
     * --added 2007.10.02
     * Get Codi Binding
     *
     * @param mcuId MCU Indentifier
     * @param codiIndex codi Indentifier
     * @throws FMPMcuException, Exception
     */
    public codiBindingEntry cmdGetCodiBinding(String mcuId, int codiIndex)
        throws FMPMcuException, Exception;

    /**
     * --added 2007.10.02
     * Get Codi Neighbor node
     *
     * @param mcuId MCU Indentifier
     * @throws FMPMcuException, Exception
     */
    public codiNeighborEntry cmdGetCodiNeighbor(String mcuId)
        throws FMPMcuException, Exception;

    /**
     * --added 2007.10.02
     * Get Codi Neighbor node
     *
     * @param mcuId MCU Indentifier
     * @param codiIndex codi Indentifier
     * @throws FMPMcuException, Exception
     */
    public codiNeighborEntry cmdGetCodiNeighbor(String mcuId, int codiIndex)
        throws FMPMcuException, Exception;

    /**
     * --added 2007.10.02
     * Get Codi Memory
     *
     * @param mcuId MCU Indentifier
     * @throws FMPMcuException, Exception
     */
    public codiMemoryEntry cmdGetCodiMemory(String mcuId)
        throws FMPMcuException, Exception;

    /**
     * --added 2007.10.02
     * Get Codi Memory
     *
     * @param mcuId MCU Indentifier
     * @param codiIndex codi Indentifier
     * @throws FMPMcuException, Exception
     */
    public codiMemoryEntry cmdGetCodiMemory(String mcuId, int codiIndex)
        throws FMPMcuException, Exception;

    /**
     * --added 2007.10.02
     * Get Codi Permit
     *
     * @param mcuId MCU Indentifier
     * @param codiIndex codi Indentifier
     * @throws FMPMcuException, Exception
     */
    public void cmdSetCodiPermit(String mcuId, int codiIndex, int codiPermit)
        throws FMPMcuException, Exception;

    /**
     * --added 2007.10.02
     * Get Codi FormNetwork
     *
     * @param mcuId MCU Indentifier
     * @param codiIndex codi Indentifier
     * @param value set value
     * @throws FMPMcuException, Exception
     */
    public void cmdSetCodiFormNetwork(String mcuId, int codiIndex, int value)
        throws FMPMcuException, Exception;

    /**
     * --added 2007.10.02
     * Set Codi Reset
     *
     * @param mcuId MCU Indentifier
     * @throws FMPMcuException, Exception
     */
    public void cmdSetCodiReset(String mcuId)
        throws FMPMcuException, Exception;

    /**
     * --added 2007.10.02
     * Set Codi Reset
     *
     * @param mcuId MCU Indentifier
     * @param codiIndex codi Indentifier
     * @throws FMPMcuException, Exception
     */
    public void cmdSetCodiReset(String mcuId, int codiIndex)
        throws FMPMcuException, Exception;

    /**
     * --added 2007.10.22
     * Correct modem pulse
     *
     * @param mcuId MCU Indentifier
     * @param modemIds Modem Id Array
     * @param adjPulse adjustment pulse value array
     * @throws FMPMcuException, Exception
     */
    public void cmdCorrectModemPulse(String mcuId, String[] modemIds, int[] adjPulse)
    throws FMPMcuException, Exception;

    /**
     * cmdSetModemAmrData
     *
     * @param mcuId MCU Indentifier
     * @param modemId Modem Indentifier
     * @param data
     * @throws FMPMcuException, Exception
     */
    public void cmdSetModemAmrData(String mcuId, String modemId, byte[] amrMask, byte[] data)
    throws FMPMcuException, Exception;

    /**
     * cmdGetModemAmrData
     *
     * @param mcuId MCU Indentifier
     * @param modemId Modem Indentifier
     * @param data
     * @throws FMPMcuException, Exception
     */
    public byte[] cmdGetModemAmrData(String mcuId, String modemId)
    throws FMPMcuException, Exception;

    /**
     * cmdGetSensorAmrData
     *
     * @param mcuId MCU Identifier
     * @param modemId Modem Identifier
     * @param data
     * @throws FMPMcuException, Exception
     */
    public byte[] cmdCommandModem(String mcuId, String modemId, byte cmdType, byte[] data)
    throws FMPMcuException, Exception;

    /**
     * cmdExecuteCommand
     * @param mcuId
     * @param modemId
     * @param modemCommand
     * @param rwFlag
     * @param commandData
     * @return
     * @throws FMPMcuException
     * @throws Exception
     */
    public byte[] cmdExecuteCommand(String mcuId, String modemId, byte modemCommand, byte rwFlag, byte[] commandData)
    throws FMPMcuException, Exception;

    /**
     * cmdCommandSensor

     * @param mcuId MCU Identifier
     * @param modemId Modem Identifier
     * @param data
     * @throws FMPMcuException, Exception
     */
    public byte[] cmdCommandModem(String mcuId, String modemId, byte cmdType,
                                   int fw, int buildnum, boolean force,
                                   byte[] data)
    throws FMPMcuException, Exception;

    /**
     * Sensor Firmware update(OTA)
     *
     * @param modemId Modem Indentifier
     * @param filename filename
     * @throws FMPMcuException, Exception
     */
    public void cmdUpdateModemFirmware(String mcuId, String modemId, String fileName)
                throws FMPMcuException, Exception;

    /**
     * It's a command to notify the MCU to update a firmware of equipment.
     *
     * @param mcuId MCU Indentifier
     * @param equipType 0:MCU 1:Modem 2:Codi
     * @param triggerId trigger id
     * @param oldHwVersion old hw version
     * @param oldSwVersion old sw version
     * @param oldBuildNumber old build number
     * @param newHwVersion new hw version
     * @param newSwVersion new sw version
     * @param newBuildNUmber new build number
     * @param binaryUrl location of firmware binary
     * @param diffUrl location of firmware diff
     * @param equipList list for equipment id
     *
     * @param fileName
     * @throws FMPMcuException, Exception
     */
    @Deprecated
    public void cmdPackageDistribution(String mcuId, int equipType,
                                       String triggerId, String oldHwVersion,
                                       String oldSwVersion, String oldBuildNumber,
                                       String newHwVersion, String newSwVersion,
                                       String newBuildNumber, String binaryMD5,
                                       String binaryUrl, String diffMD5,
                                       String diffUrl, String[] equipList,
                                       int otaType, int modemType,String modemTypeStr,
                                       int dataType, int otaLevel, int otaRetry)
        throws FMPMcuException, Exception;

    /**
     * @param mcuId
     * @param triggerId
     * @param equipKind
     * @param model
     * @param transferType
     * @param otaStep
     * @param maxRetryCount
     * @param otaThreadCount
     * @param installType
     * @param oldHwVersion
     * @param oldFwVersion
     * @param oldBuild
     * @param newHwVersion
     * @param newFwVersion
     * @param newBuild
     * @param binaryURL
     * @param binaryMD5
     * @param diffURL
     * @param diffMD5
     * @param equipIdList
     * @throws FMPMcuException
     * @throws Exception
     */
    public void cmdDistribution(String mcuId,
                                String triggerId,
                                int equipKind,
                                String model,
                                int transferType,
                                int otaStep,
                                int multiWriteCount,
                                int maxRetryCount,
                                int otaThreadCount,
                                int installType,
                                int oldHwVersion,
                                int oldFwVersion,
                                int oldBuild,
                                int newHwVersion,
                                int newFwVersion,
                                int newBuild,
                                String binaryURL,
                                String binaryMD5,
                                String diffURL,
                                String diffMD5,
                                List equipIdList)
        throws FMPMcuException, Exception;
    
    /**
     * @param mcuId
     * @param triggerId
     * @param equipKind
     * @param model
     * @param transferType
     * @param otaStep
     * @param maxRetryCount
     * @param otaThreadCount
     * @param installType
     * @param oldHwVersion
     * @param oldFwVersion
     * @param oldBuild
     * @param equipIdList
     * @throws FMPMcuException
     * @throws Exception
     */
    public void cmdDistributionSensor(String mcuId,
                                String triggerId,
                                int equipKind,
                                String model,
                                int transferType,
                                int otaStep,
                                int multiWriteCount,
                                int maxRetryCount,
                                int otaThreadCount,
                                int installType,
                                int oldHwVersion,
                                int oldFwVersion,
                                int oldBuild,
                                List equipIdList)
        throws FMPMcuException, Exception;
    
            
    /**
     * cmdDistributionState
     * @param mcuId
     * @param triggerId
     * @return
     * @throws FMPMcuException
     * @throws Exception
     */
    public List cmdDistributionState(String mcuId,
                                String triggerId)
        throws FMPMcuException, Exception;

    /**
     * cmdDistributionCancel
     * @param mcuId
     * @param triggerId
     * @throws FMPMcuException
     * @throws Exception
     */
    public void cmdDistributionCancel(String mcuId,
                                     String triggerId)
     throws FMPMcuException, Exception;

    /**
     * cmdBypassSensor(102.27)
     *
     * @param mcuId MCU Indentifier
     * @param sensorId Sensor Indentifier
     * @param isLinkSkip Link Frame send true
     * @param data data stream
     * @throws FMPMcuException, Exception
     */
    public void cmdBypassSensor(String mcuId, String sensorId, boolean isLinkSkip,
                                byte[] data)
    throws FMPMcuException, Exception;

    /**
     * cmdBypassSensor(102.27)
     *
     * @param mcuId MCU Indentifier
     * @param sensorId Sensor Indentifier
     * @param isLinkSkip Link Frame send true
     * @param data multi frame data stream
     * @throws FMPMcuException, Exception
     */
    public void cmdBypassSensor(String mcuId, String sensorId, boolean isLinkSkip,
                                ArrayList data)
    throws FMPMcuException, Exception;

    /**
     * cmdGetFFDList(102.38)
     *
     * @param mcuId MCU Indentifier
     * @param parser parser name
     * @param fwversion firmware version
     * @param fwbuild firmware build
     * @throws FMPMcuException, Exception
     */
    public ffdEntry[] cmdGetFFDList(String mcuId, String parser, String fwVersion, String fwBuild)
    throws FMPMcuException, Exception;

    /**
     * command
     * @param mcuId
     * @param miuType Device
     * @param miuClassName ZEUPLS, ZRU, EnergyMeter
     * @param miuId   Device
     * @param command
     * @param option - 0x01 : ASYNC_OPT_RETURN_CODE_EVT
                     - 0x02 : ASYNC_OPT_RESULT_DATA_EVT
                     - 0x10 : ASYNC_OPT_RETURN_CODE_SAVE
                     - 0x20 : ASYNC_OPT_RESULT_DATA_SAVE
     * @param day Keep Option
     * @param nice Request
     * @param ntry
     * @param args CommandOID Parameter
     * @param serviceType 1:NMS 2:MTR
     * @param operator
     * @return trId
     * @throws Exception
     */
    public long cmdAsynchronousCall(String mcuId, String miuType, String miuClassName,
            String miuId, String command, int option,
            int day, int nice, int ntry, String[][] args, int serviceType, String operator)
    throws Exception;

    /**
     * (102.41)
     * @param mcuId
     * @param miuType Device
     * @param miuId   Device
     * @param parser
     * @param version
     * @param build
     * @return trInfoEntry(6.1)
     * @throws Exception
     */
    public trInfoEntry cmdShowTransactionList(String mcuId, String miuType, String miuId,
            String parser, String version, String build)
    throws Exception;

    /**
     * (102.42)
     * @param mcuId
     * @param trId
     * @param option - 0x01 : ASYNC_OPT_RETURN_CODE_EVT
                     - 0x02 : ASYNC_OPT_RESULT_DATA_EVT
                     - 0x10 : ASYNC_OPT_RETURN_CODE_SAVE
                     - 0x20 : ASYNC_OPT_RESULT_DATA_SAVE
     * @return Transaction Information(trInfoEntry), Parameter MIBValue, Result MIBValue, Transaction History
     * @throws Exception
     */
    public Map<String, Object> cmdShowTransactionInfo(String mcuId, long trId, int option)
    throws Exception ;

    /**
     * (102.43)
     * @param mcuId
     * @param trId
     * @param state  - 0x08 : TR_STATE_TERMINATE : Transaction
                     - 0x10 : TR_STATE_DELETE    : Transaction
     * @throws Exception
     */
    public void cmdModifyTransaction(String mcuId, long[] trId, int[] state)
    throws Exception;

    /**
     * (102.44)
     * @param mcuId
     * @param modemId
     * @param count
     * @return EventLog[] - aimir-service-common/EventLog
     * @throws Exception
     */
    public EventLog[] cmdGetModemEvent(String mcuId, String modemId, int count)
    throws Exception;

    /**
     * (102.44)
     * @param mcuId
     * @param modemId
     * @param count
     * @return BatteryLog - aimir-service-common/BatteryLog
     * @throws Exception
     */
    public BatteryLog cmdGetModemBattery(String mcuId, String modemId, int count)
    throws Exception;

    /**
     * (102.49)
     * @param mcuId
     * @param modemId
     * @param direction
     * @param mask
     * @param value
     * @return
     * @throws FMPMcuException
     * @throws Exception
     */
    public byte[] cmdDigitalInOut(String mcuId, String modemId, byte direction, byte mask, byte value)
    throws FMPMcuException, Exception;

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
     *          IF4ERR_INVALID_PARAM        : 잘못된 Parameter가 전달
     *          IF4ERR_INVALID_ID           : miuID가 관리 목록에 없음
     *          IF4ERR_INVALID_TARGET       : 대상 장비가 AsyncrhronousCall을 지원하지 않음
     *          IF4ERR_INVALID_COMMAND      : 지원하지 않는 Command
     *          IF4ERR_OUT_OF_RANGE     : nDay, nNice, nRetry가 유효값 범위 밖에 값으로 설정
     *          IF4ERR_TRANSACTION_CREATE_FAIL  : Transaction 생성 실패
     *          IF4ERR_INVALID_GROUP        : 존재하지 않는 그룹 이름
     *          IF4ERR_GROUP_DB_OPEN_FAIL       : 그룹 DB의 Open 실패
     */
    public long cmdGroupAsyncCall(String mcuId, int groupKey, String command, int nOption, int nDay, int nNice, int nTry, List<SMIValue> param)
    throws FMPMcuException, Exception;

    /**
     * (102.54) 그룹을 추가한다
     * @param mcuId
     * @param groupKey
     * @return groupName
     * @throws FMPMcuException
     * @throws Exception
     *          IF4ERR_GROUP_DB_OPEN_FAIL       : 그룹 DB의 Open 실패
     *          IF4ERR_GROUP_NAME_DUPLICATE     : 중복된 그룹 이름
     *          IF4ERR_GROUP_STORE_ERROR        : 그룹 DB에 저장 에러
     */
    public int cmdGroupAdd(String mcuId, String groupName)
    throws FMPMcuException, Exception;

    /**
     * (102.55) 그룹을 삭제한다
     * @param mcuId
     * @param groupKey
     * @throws FMPMcuException
     * @throws Exception
     *          IF4ERR_GROUP_DB_OPEN_FAIL       : 그룹 DB의 Open 실패
     *          IF4ERR_GROUP_NAME_NOT_EXIST     : 그룹 이름이 존재하지 않음
     *          IF4ERR_GROUP_DELETE_ERROR       : 그룹 DB에 삭제 에러
     */
    public void cmdGroupDelete(String mcuId, int groupKey)
    throws FMPMcuException, Exception;

    /**
     * (102.56) 그룹에 멤버를 추가한다
     * @param mcuId
     * @param groupKey
     * @param modemId
     * @throws FMPMcuException
     * @throws Exception
     *          IF4ERR_INVALID_PARAM        : 잘못된 Parameter가 전달
     *          IF4ERR_GROUP_DB_OPEN_FAIL       : 그룹 DB의 Open 실패
     *          IF4ERR_TRANSACTION_CREATE_FAIL  : Transaction 생성 실패
     */
    public void cmdGroupAddMember(String mcuId, int groupKey, String modemId)
    throws FMPMcuException, Exception;

    /**
     * (102.57) 그룹에 멤버를 삭제한다
     * @param mcuId
     * @param groupKey
     * @param modemId
     * @throws FMPMcuException
     * @throws Exception
     *          IF4ERR_INVALID_PARAM        : 잘못된 Parameter가 전달
     *          IF4ERR_GROUP_DB_OPEN_FAIL       : 그룹 DB의 Open 실패
     *          IF4ERR_TRANSACTION_UPDATE_FAIL  : Transaction 갱신 실패
     *          IF4ERR_TRANSACTION_DELETE_FAIL  : Transaction 삭제 실패
     *          IF4ERR_GROUP_INVALID_MEMBER : 해당 그룹에 멤버가 존재하지 않음
     */
    public void cmdGroupDeleteMember(String mcuId, int groupKey, String modemId)
    throws FMPMcuException, Exception;


    /**
     * (102.58) 현재 그룹 정보 전체를 조회한다
     * @param mcuId
     * @throws FMPMcuException
     * @throws Exception
     *    IF4ERR_GROUP_DB_OPEN_FAIL     : 그룹 DB의 Open 실패
     */
    public GroupInfo[] cmdGroupInfo(String mcuId) throws FMPMcuException, Exception;

    /**
     * (102.58) 현재 그룹 정보를 그룹명으로 조회한다
     * @param mcuId
     * @param groupKey
     * @param modemId
     * @throws FMPMcuException
     * @throws Exception
     *    IF4ERR_GROUP_DB_OPEN_FAIL     : 그룹 DB의 Open 실패
     */
    public GroupInfo[] cmdGroupInfo(String mcuId, int groupKey) throws FMPMcuException, Exception;

    /**
     * (102.58) 현재 그룹 정보를 모뎀이 속한 정보로 검색한다.
     * @param mcuId
     * @param groupName
     * @param modemId
     * @param bSearchId - 파라미터가 true이면 모뎀이 속한 그룹을 조회
     * @throws FMPMcuException
     * @throws Exception
     *    IF4ERR_GROUP_DB_OPEN_FAIL     : 그룹 DB의 Open 실패
     */
    public GroupInfo[] cmdGroupInfo(String mcuId, String modemId, boolean bSearchId) throws FMPMcuException, Exception;

    /**
     * cmdGetLoadControlScheme(108.1)
     *
     * @param sensorId sensor id
     * @return 11.1
     * @throws FMPMcuException, Exception
     */
    public loadControlSchemeEntry[] cmdGetLoadControlScheme(String mcuId, String modemId)
    throws FMPMcuException, Exception;

    /**
     * cmdSetLoadControlScheme(108.2)
     *
     * @param sensorId
     * @param entryNumber
     * @param command
     * @param delayTime
     * @param scheduleType
     * @param startTime
     * @param endTime
     * @param weekly
     * @throws FMPMcuException, Exception
     */
    public void cmdSetLoadControlScheme(String mcuId, String sensorId, int entryNumber,
            int command, int delayTime, int scheduleType,
            String startTime, String endTime, int weekly)
    throws FMPMcuException, Exception;

    /**
     * cmdGetLoadLimitProperty(108.3)
     *
     * @param sensorId sensor id
     * @return 11.2
     * @throws FMPMcuException, Exception
     */
    public loadLimitPropertyEntry cmdGetLoadLimitProperty(String mcuId, String sensorId)
    throws FMPMcuException, Exception;

    /**
     * cmdSetLoadLimitProperty(108.4)
     *
     * @param sensorId sensor identification
     * @param limitType 0:voltage 1:current
     * @param limit
     * @param intervalNumber
     * @param openPeriod
     * @throws FMPMcuException, Exception
     */
    public void cmdSetLoadLimitProperty(String mcuId, String sensorId, int limitType,
            long limit, int intervalNumber, int openPeriod)
    throws FMPMcuException, Exception;

    /**
     * cmdGetLoadLimitScheme(108.5)
     *
     * @param sensorId sensor id
     * @return 11.3
     * @throws FMPMcuException, Exception
     */
    public loadLimitSchemeEntry[] cmdGetLoadLimitScheme(String mcuId, String sensorId)
    throws FMPMcuException, Exception;

    /**
     * cmdSetLoadLimitScheme(108.6)
     *
     * @param sensorId
     * @param entryNumber
     * @param limitType
     * @param limit
     * @param intervalNumber
     * @param openPeriod
     * @param scheduleType
     * @param startTime
     * @param endTime
     * @param weekly
     * @throws FMPMcuException, Exception
     */
    public void cmdSetLoadLimitScheme(String mcuId, String sensorId, int entryNumber,
            int limitType, long limit, int intervalNumber, int openPeriod,
            int scheduleType, String startTime, String endTime, int weekly)
    throws FMPMcuException, Exception;

    /**
     * cmdGetLoadShedSchedule(108.7)
     *
     * @param mcuId
     * @return 11.4
     * @throws FMPMcuException, Exception
     */
    public loadShedSchemeEntry[] cmdGetLoadShedSchedule(String mcuId)
    throws FMPMcuException, Exception;

    /**
     * cmdSetLoadShedSchedule(108.8)
     *
     * @param mcuId
     * @param entryNumber
     * @param supply
     * @param supplyThreshold
     * @param checkInterval
     * @param scheduleType
     * @param startTime
     * @param endTime
     * @param weekly
     * @throws FMPMcuException, Exception
     */
    public void cmdSetLoadShedSchedule(String mcuId, int entryNumber, int checkInterval,
            int scheduleType, String startTime, String endTime, int weekly)
    throws FMPMcuException, Exception;

    /**
     * cmdDeleteLoadControlScheme(108.9)
     *
     * @param mcuId
     * @paran sensorId
     * @param entryNumber
     * @throws FMPMcuException, Exception
     */
    public void cmdDeleteLoadControlScheme(String mcuId, String sensorId, int entryNumber)
    throws FMPMcuException, Exception;

    /**
     * cmdDeleteLoadLimitScheme(108.10)
     *
     * @param mcuId
     * @paran sensorId
     * @param entryNumber
     * @throws FMPMcuException, Exception
     */
    public void cmdDeleteLoadLimitScheme(String mcuId, String sensorId, int entryNumber)
    throws FMPMcuException, Exception;

    /**
     * cmdDeleteLoadShedScheme(108.11)
     *
     * @param mcuId
     * @param entryNumber
     * @throws FMPMcuException, Exception
     */
    public void cmdDeleteLoadShedScheme(String mcuId, int entryNumber)
    throws FMPMcuException, Exception;

    /**
     * cmdSetCiuLCD (109.1)
     * @param sensorId
     * @param ledNumber
     * @param pageIdx
     * @param data
     */
    public void cmdSetCiuLCD(String mcuId, String sensorId, int ledNumber, int pageIdx, String data)
    throws FMPMcuException, Exception;

    /**
     * cmdACD(105.9)
     *
     * @param sensorId Sensor Indentifier
     * @param onoff on/off
     * @param delayTime on/off delaytime
     * @throws FMPMcuException, Exception
     */
    public void cmdACD(String mcuId, String sensorId, int onoff, int delayTime, int randomTime)
    throws FMPMcuException, Exception;

    /**
     * On-demand meter 104.6.0
     * @param modemId Modem Indentifier
     * @param kmcs kmcEntry list
     * @return result
     * @throws FMPMcuException, Exception
     */
    public java.lang.String cmdOnDemandMeter( java.lang.String meterId,java.lang.String fromDateTime,java.lang.String toDateTime )
       throws FMPMcuException, java.lang.Exception, java.rmi.RemoteException;

    /**
     * MCU Scanning
     *
     * @param mcuId MCU Indentifier
     * @throws FMPMcuException, Exception
     */
    public Hashtable cmdMcuScanning(String mcuId,String[] property)
        throws FMPMcuException, Exception;

    public String cmdSensorLPLogRecovery(String mdsId, String dcuNo, String modemNo, double meteringValue,
               int lpInterval, int[] lplist)
        throws FMPMcuException, Exception;

    public String doGetModemROM(String mcuId, String modemId,String modemType, int serviceType, String operator)
    throws Exception;

    /**
     * 모뎀의 롬 정보 읽기(펌웨어 버전,메모리정보,검침주기 등의 정보를 취득)
     * 모뎀 타입이 지그비 타입인 경우 PLC, SUBGIGA,Zigbee
     * @param gw
     * @param sensor
     * @param mcuId
     * @param modemId
     * @param serviceType
     * @param operator
     * @return
     * @throws Exception
     */
    public String doGetModemCluster( String mcuId, String modemId,String modemType, int serviceType, String operator)
    throws Exception;

    /**
     * (130.1)DR 프로그램 참여 유도
     * @param mcuId
     * @param drLevelEntry
     * @throws FMPMcuException
     * @throws Exception
     */
    public void cmdDRAgreement(String mcuId, drLevelEntry drLevelEntry) throws FMPMcuException,Exception;

    /**
     * (130.2)DR 취소 메시지
     * @param mcuId
     * @param deviceId
     * @throws FMPMcuException
     * @throws Exception
     */
    public void cmdDRCancel(String mcuId, String deviceId) throws FMPMcuException,Exception;

    /**
     * (130.3)Incentive DR Start
     * @param mcuId
     * @param idrEntry
     * @throws FMPMcuException
     * @throws Exception
     */
    public void cmdIDRStart(String mcuId, idrEntry idrEntry) throws FMPMcuException,Exception;

    /**
     * (130.4)Incentive DR Cancel
     * @param mcuId
     * @param eventId
     * @throws FMPMcuException
     * @throws Exception
     */
    public void cmdIDRCancel(String mcuId, String eventId) throws FMPMcuException,Exception;

    /**
     * (130.5)DR Level Monitoring 장비의 DR Level 조회
     * @param mcuId
     * @param deviceId
     * @return
     * @throws FMPMcuException
     * @throws Exception
     */
    public endDeviceEntry cmdGetDRLevel(String mcuId, String deviceId) throws FMPMcuException,Exception;

    /**
     * (130.6)DR Level Control DR Level 제어
     * @param mcuId
     * @param endDeviceEntry
     * @throws FMPMcuException
     * @throws Exception
     */
    public void cmdSetDRLevel(String mcuId, endDeviceEntry endDeviceEntry) throws FMPMcuException,Exception;

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
    public void cmdEndDeviceControl(String mcuId, String serviceId, String deviceId, String eventId, String drLevel) throws FMPMcuException, Exception;

    //TODO 파라미터가 deviceId인 경우가 있고, groupName인 경우가 있는데 어떻게 구분할까?
    /**
     * (130.8)DR 자원 정보 요청
     * @param mcuId
     * @param deviceId
     * @return
     * @throws FMPMcuException
     * @throws Exception
     */
    public endDeviceEntry cmdGetDRAsset(String mcuId, String deviceId) throws FMPMcuException, Exception;
    //TODO 구현 필요
    /*
     *cmdSetBillingInfo
     *cmdSetPriceInfo
     */

    /**
     * @param mcuId
     * @param modemId
     * @param nOption
     * @param offSet
     * @param count
     * @return
     * @throws FMPMcuException
     * @throws Exception
     */
    public MeterData cmdOnRecoveryDemandMeter(String mcuId, String modemId, String nOption, int offSet, int count)
            throws FMPMcuException, Exception;
    
    
    /**
     * Broadcast start
     * @param mcuId
     * @param deviceId
     * @throws FMPMcuException
     * @throws Exception
     */
    public void cmdBroadcast(String mcuId, String deviceId, String message) 
        throws FMPMcuException,Exception;
    
    /**
     * Command Valve Control(OID : 105.9)
     * 
     * @param mcuId
     *            MCU ID. <br>loging only.
     * @param modemId
     *            Modem ID. <br>get a target of infomation.
     * @param valveStatus
     *            Valve On:0, Valve Off:1, Valve Standby:2 , orders get meter
     *            status.
     * @return
     * @throws Exception
     * @ejb.interface-method view-type="both"
     */
    public int cmdValveControl(String mcuId, String modemId, int valveStatus) throws Exception;
    
    /**
     * Command Valve Status
     * 
     * @return
     * @throws Exception
     * @ejb.interface-method view-type="both"
     */
    public Map<String, Object> cmdValveStatusByGW(String mcuId, String meterId) throws Exception;

    
    /**
     * @param mcuId
     * @param modemId
     * @param mask
     * @param currentPulse
     * @param serialNumber
     * @return
     * @throws Exception
     */
    public int cmdSetMeterConfig(String mcuId, String modemId, int mask,
            int currentPulse, String serialNumber) throws Exception;
    
    /**
     * @param mcuId
     * @param modemId
     * @return
     * @throws Exception
     */
    public Map<String, Object> cmdGetMeterVersion(String mcuId, String modemId) throws Exception;


    /**
     * TOU Calendar 를 설정한다.<br>kskim.
     * @param meterSerial
     * @param touProfileId TouProfile Table ID
     * @return
     * @throws Exception 
     */
    public Response cmdSetTOUCalendar(String meterSerial, int touProfileId) throws Exception;
    
    /**
     * @param sensorId
     * @param mcuId
     * @return
     * @throws Exception
     */
    public byte cmdGetEnergyLevel(String mcuId, String sensorId) throws Exception;
    
    /**
     * @param sensorId
     * @param mcuId
     * @param level
     * @return
     * @throws Exception
     */
    public void cmdSetEnergyLevel(String mcuId, String sensorId, String level) throws Exception;
    
    /**
     * @param sensorId
     * @param mcuId
     * @param level
     * @param meterSerial
     * @return
     * @throws Exception
     */
    public void cmdSetEnergyLevel(String mcuId, String sensorId, String level, String meterSerial) throws Exception;
    
    /**
     * @param sensorId
     * @param mcuId
     * @return
     * @throws Exception
     */
    public List cmdGetDRAssetInfo(String mcuId, String sensorId, String parser) throws Exception;
    
    /**
     * @param sensorId
     * @param mcuId
     * @return
     * @throws Exception
     */
    public void cmdSendMessage(String mcuId, String sensorId, int messageId, int messageType, int duration,  int errorHandler, int preHandler, int postHandler, int userData, String pszData ) throws Exception;
    
    /**
     * Demand 를 reset한다.<br>
     * 
     * @param meterId
     * @return
     * @throws Exception
     */
    public Response cmdDemandReset(Integer meterId) throws Exception;
    
    /**
     * SMS 보내는 기능.
     * @param target
     * @param params
     * @return
     * @throws Exception
     */
    public Object cmdSendSMS(Target target, String ... params) throws Exception;
    public Object cmdSendSMS(String modemSerial, String ... params) throws Exception;

    /**
     * Bypass 모드로 실행되는 Time sync 기능
     * @param modemSerial
     * @return
     * @throws Exception 
     */
    public Response cmdBypassTimeSync(String modemSerial, String loginId)
            throws Exception;

    /**
     * DOTA 명령을 SMS 메시지 로 보내는 기능.
     * @param modemSerial
     * @param filePath
     * @return
     */
    public Response cmdSmsFirmwareUpdate(String modemSerial, String filePath) throws Exception;

    /**
     * Meter Table 에 값을 기록하는 명령어
     * @param meterSerial
     * @param valueObject
     * @param mp
     * @return
     * @throws Exception 
     */
    public Response cmdMeterProgram(String meterSerial, Object valueObject,
            MeterProgram mp) throws Exception;

    /**
     * Bypass 모드로 Meter Program 기능 실행.
     * @param meterSerial
     * @param kind
     * @return
     */
    public Response cmdBypassMeterProgram(String meterSerial,
            MeterProgramKind kind)throws Exception;
    
    
    /**
     * 펌웨어 롤백 기능.
     * @param model
     * @param sensors
     */
    public void cmdMeterFactoryReset(String mcuId, String sensorId) throws FMPMcuException, Exception;

    /**
     * INSTALL CODI Information Set.
     * @param mcuId
     * @param sensorID
     * @return
     * @throws FMPMcuException
     * @throws Exception
     */
    public String cmdSetIHDTable(String mcuId, String sensorID) throws FMPMcuException, Exception;

    /**
     * INSTALL CODI Information Del.
     * @param mcuId
     * @param sensorID
     * @return
     * @throws FMPMcuException
     * @throws Exception
     */
    public String cmdDelIHDTable(String mcuId, String sensorID) throws FMPMcuException, Exception;

    /**
     * Send IHD Data to MCU
     * @param mcuId
     * @param sensorId
     * @param IHDData
     * @throws Exception
     */
    public void cmdSendIHDData(String mcuId, String sensorId, byte[] IHDData) throws Exception;
    
    /**
     * (102.70) Type을 지원하는 Group을 추가/갱신한다.
     * 만약 해당 Type, Name의 Group이 없다면 신규 생성되고 있을 경우 Sensor ID가 추가되게 한다.
     * @param mcuId
     * @param groupType
     * @param groupName
     * @param modemId
     * @throws FMPMcuException
     * @throws Exception
     *          IF4ERR_INVALID_ID           : 잘못 모뎀 아이디
     *          IF4ERR_INVALID_PARAM        : 잘못된 Parameter가 전달
     */
    public void cmdUpdateGroup(String mcuId, String groupType, String groupName, String[] modemId)
            throws FMPMcuException, Exception;
    
    /**
     * (102.71) 지정된 ID를 Group정보에서 삭제한다.
     * 만약 ID를 삭제 후 해당 Group의 Member가 더 이상 없을 경우 Group 정보도 삭제된다.
     * @param mcuId
     * @param groupType 널값이 들어올 수 있다.
     * @param groupName 널값이 들어올 수 있다.
     * @param modemId
     * @throws FMPMcuException
     * @throws Exception
     *         IF4ERR_INVALID_ID
     *         IF4ERR_INVALID_PARAM
     */
    public void cmdDeleteGroup(String mcuId, String groupType, String groupName, String[] modemId)
            throws FMPMcuException, Exception;
    
    /**
     * (102.72) Type을 지원하는 Group정보를 조회한다.
     * 첫번째 Parameter가 EUI64일 경우 해당 ID의 모든 Group 정보를 조회하게 되고 첫번째 Parameter가
     * STRING일 경우 type, name, id를 입력 받아 조건에 맞는 Group 정보를 조회하게 된다. 이때 type, name은 생략 가능하다.
     * @param mcuId
     * @param groupType 널값이 들어올 수 있다.
     * @param groupName 널값이 들어올 수 있다.
     * @param modemId
     * @return list[ type(1.11 STRING)
     *               name(1.11 STRING)
     *               id (1.11 STRING)
     *               ]
     * @throws FMPMcuException
     * @throws Exception
     *         IF4ERR_INVALID_ID
     */
    public List<GroupTypeInfo> cmdGetGroup(String mcuId, String groupType, String groupName, String modemId)
            throws FMPMcuException, Exception;
    
    
    /**
     * FIXME : 바이페스 테스트용
     * method name : cmdBypassTest
     * method Desc : 
     */
    public String sendBypassOpenSMS(String modemSerial);

    public void cmdSetMeterSecurity(String mcuId, String type, String name, String key)
            throws Exception;

    public Map<String, Object> cmdGetMeterSecurity(String mcuId, String type,
            String name) throws Exception;
    
    /**
     * (105.11) 우즈벡용 극동 미터에서 사용하고 있는 OID 중복으로 인한 재정의된 OID Command
     * 
     * @param modemId                           
     * @param valueStatus                       : Value Status(On/Off)
     * 
     * @return None
     * 
     * @throws Exception
     *         IF4ERR_INVALID_PARAM                잘못된 인자 전달
     *         IF4ERR_CANCNOT_CONNECT              Connection 실패
     *         IF4ERR_CANNOT_GET                   처리 중 오류
     *         IF4ERR_INVALID_TYPE                 극동 미터가 아닐 경우
     */
    public void cmdKDValveControl(String modemId, int valueStatus) throws Exception;
    
    /**
     * (105.12) 우즈벡용 극동 미터에서 사용하고 있는 OID 중복으로 인한 재정의된 OID Command
     * 
     * @param modemId
     * 
     * @return ResponseMap(
     *                      cp(UNIT 1.3)            : Current Pulse (Little endian)
     *                      serial(STRING 1.11)     : Meter Serial
     *                      astatus(BYTE 1.4)       : Alarm status
     *                      mstatus(BYTE 1.4)       : Meter status)
     * 
     * @throws Exception
     *         IF4ERR_INVALID_PARAM                 잘못된 인자 전달
     *         IF4ERR_CANCNOT_CONNECT               Connection 실패
     *         IF4ERR_CANNOT_GET                    처리 중 오류
     *         IF4ERR_INVALID_TYPE                  극동 미터가 아닐 경우
     */
    public Map<String, Object> cmdKDGetMeterStatus(String modemId) throws Exception;
    
    /**
     * (105.13) 우즈벡용 극동 미터에서 사용하고 있는 OID 중복으로 인한 재정의된 OID Command
     * 
     * @param modemId
     * 
     * @return ResponseMap(
     *                      date(STRING 1.11)       : Test date & accept
     *                      hwver(STRING 1.11)      : HW Version
     *                      swver(STRING 1.11)      : SW Version)
     * @throws Exception
     *         IF4ERR_INVALID_PARAM                  잘못된 인자 전달
     *         IF4ERR_CANCNOT_CONNECT               Connection 실패
     *         IF4ERR_CANNOT_GET                    처리 중 오류
     *         IF4ERR_INVALID_TYPE                  극동 미터가 아닐 경우
     */
    public Map<String, Object> cmdKDGetMeterVersion(String modemId) throws Exception;
    
    /**
     * (105.14) 우즈벡용 극동 미터에서 사용하고 있는 OID 중복으로 인한 재정의된 OID Command
     * 
     * @param modemId
     * @param mask                                  mask가 0x00일 경우 Meter Serial을 사용하지 않는다.
     * @param cp                                    Meter Pulse(Little endia)
     * @param meterId                               Meter Serial(Optional) : mask가 0x00이 아닐 경우에는 반드시 전달 되어야 한다.
     * 
     * @return None
     * 
     * @throws Exception
     *         IF4ERR_INVALID_PARAM                 잘못된 인자 전달
     *         IF4ERR_CANCNOT_CONNECT               Connection 실패
     *         IF4ERR_CANNOT_GET                    처리 중 오류
     *         IF4ERR_INVALID_TYPE                  극동 미터가 아닐 경우
     */
    public void cmdKDSetMeterConfig(String modemId, byte mask, int cp, String meterId) throws Exception;
    
    /**
     * cmdOnDemandMeter
     *
     * @param mcuId MCU Indentifier
     * @param meterId
     * @param nOption (3~7)
     * @return resultData
     * @throws FMPMcuException, Exception
     */
    public MeterData cmdOnDemandByMeter(String mcuId, String meterId,
            String modemId, String nOption, String fromDate, String toDate)
                    throws FMPMcuException, Exception;
    
    /**
     * cmdOnDemandMeter
     *
     * @param mcuId MCU Indentifier
     * @param meterId
     * @param nOption (3~7)
     * @return resultData
     * @throws FMPMcuException, Exception
     */
    public Map cmdOnDemandByMeter(String mcuId, String meterId,
            String modemId, int nOption)
                    throws FMPMcuException, Exception;
    
    /**
     * GD 100.2.1,Upload Metering Data
     * @param meterSerial                           Meter Serial
     * @throws Exception
     *         IF4ERR_INVALID_PARAM                 잘못된 인자 전달
     *         IF4ERR_CANCNOT_CONNECT               Connection 실패
     *         IF4ERR_CANNOT_GET                    처리 중 오류
     */
    public void cmdUploadMeteringData(String meterSerial) throws Exception;
    
    /**
     * NG 110.1.1,Upload metering by modem
     * @param mcuId                             mcuId
     * @param option
     * @param offset
     * @param count
     * @param modemId list
     * @throws Exception
     *         IF4ERR_INVALID_PARAM                 잘못된 인자 전달
     *         IF4ERR_CANCNOT_CONNECT               Connection 실패
     *         IF4ERR_CANNOT_GET                    처리 중 오류
     */
    public void cmdMeteringByModem(String mcuId, int nOption, int offset, int count, String[] modemId)
            throws FMPMcuException, Exception;
    
    /**
     * NG 110.1.2,Upload metering by meter
     * @param mcuId                             mcuId
     * @param option
     * @param offset
     * @param count
     * @param meterId list
     * @throws Exception
     *         IF4ERR_INVALID_PARAM                 잘못된 인자 전달
     *         IF4ERR_CANCNOT_CONNECT               Connection 실패
     *         IF4ERR_CANNOT_GET                    처리 중 오류
     */
    public void cmdMeteringByMeter(String mcuId, int nOption, int offset, int count, String[] meterId)
            throws FMPMcuException, Exception;
           
    /**
     * NG 200.1.1, DCU Assemble Test Operation
     * @param mcuId                             mcuId
     * @throws Exception
     *         IF4ERR_INVALID_PARAM                 잘못된 인자 전달
     *         IF4ERR_CANCNOT_CONNECT               Connection 실패
     *         IF4ERR_CANNOT_GET                    처리 중 오류
     */
    public void cmdAssembleTestStart(String mcuId)
            throws FMPMcuException, Exception;

    // DELETE START SP-681
//    /**
//     * NG 181.2.1, Device upgrade Operation
//     * @param mcuId                             mcuId
//     * @param 1.5	WORD	Image Key	OTA용 Firmware Image Key. 동일한 Image는 동일한 Key를 사용하여 이어 전송하기를 할 수 있게 된다. 서로 다른 Image는 다른 Key를 사용해야 한다.
//     * @param 1.11	STRING	File URL	Upgrade Image의 URL
//     * @param 1.11	STRING	Upgrade File Check Sum	Upgrade Image check sum (MD5SUM)
//     * @param 1.11	STRING	Node Kind	Target modem Node kind
//     * @param 1.5	WORD	Target Node FW Version	Target modem FW Version. 예를 들어 FW Version 1.2는 0x0102 의 값을 가져야 한다.
//     * @param 1.5	WORD	Target Node Build Number	Target modem build number
//     * @param 1.4	BYTE	Upgrade policy	Upgrade Request 실행 방법
//     * @param - 0x00: Store (Upgrade request를 저장만 하고 별도의 실행 요청을 기다린다)
//     * @param - 0x01: Immediately (즉시 실행)
//     * @param - 0x02: Lazy (대상 modem에 대한 Join event를 받거나 metering이 성공한 이후에 실행)
//     * @param 1.4	BYTE	Cancel policy	Upgrade Cancel 방법
//     *             - 0x00: None (삭제 명령이 올 때 까지 자동 취소가 없다)
//     *             - 0x01: Complete (대상에 대한 Upgrade가 완료되면 삭제 된다)
//     *             - Other values: 설정된 일자 이후에 자동 삭제
//     * @throws Exception
//     *         IF4ERR_INVALID_PARAM                 잘못된 인자 전달
//     *         IF4ERR_CANCNOT_CONNECT               Connection 실패
//     *         IF4ERR_CANNOT_GET                    처리 중 오류
//     */
//	public void cmdReqNodeUpgrade(String mcuId, 
//			               int imageKey, 
//			               String fileUrl,
//			               String upgradeCheckSum,
//			               String nodeKind,
//			               int newFwVersion,
//			               int newBuild,
//			               int upgradePolicy,
//			               int cancelPolicy) throws FMPMcuException, Exception;
    // DELETE END SP-681
	
	 /**
     * SP 181.2.1  cmdReqNodeUpgrade
     *
     * @param mcuId MCU Indentifier
     * @param upgradeType upgradeType
     * @param imageKey imageKey
     * @param imageUrl imageUrl
     * @param checkSum checkSum
     * @param filterValue filterValue
     * @return moproperty
     * @throws FMPMcuException, Exception
     */
	public Hashtable cmdReqToDCUNodeUpgrade(String mcuId, OTAType upgradeType, String imageKey, String imageUrl, String checkSum, List<String> filterValue) throws FMPMcuException, Exception;

	// INSERT START SP-681
	public Hashtable cmdReqNodeUpgrade(String mcuId, int upgradeType, int control,
			String imageKey, String imageUrl, String upgradeCheckSum, 
			List<String> filterValue) throws FMPMcuException, Exception;
	
	public Hashtable cmdReqImagePropagate(String mcuId, int upgradeType, int control,
			String imageKey, String imageUrl, String upgradeCheckSum, 
			String imageVersion, String targetModel , List<String> filterValue) 
					throws FMPMcuException, Exception;
	
	public Hashtable cmdGetImagePropagateInfo(String mcuId, int upgradeType)  
			throws FMPMcuException, Exception;

	// INSERT END SP-681
		
		
	/**
     * SP 181.2.2 cmdCtrlUpgradeRequest 
     *
     * @param mcuId MCU Indentifier
     * @param int requestId
     * @param int opCode
     * @throws FMPMcuException, Exception
     */
	public void cmdCtrlUpgradeRequest(String mcuId, int requestId, int opCode) 
		throws FMPMcuException, Exception;
	
	
	/**
     * NG 181.3.1, cmdReqCertRenewal
     * @param 1.11	String	DCU Serial Number (deviceSerial)
     * @param 1.11	STRING	File URL (Upgrade Image의 URL)
     * @param 1.11	STRING	Upgrade File Check Sum
     * 
     * @throws Exception
     *         IF4ERR_NOERROR                 
     *         IF4ERR_CANNOT_GET              목록 조회에 실패 했을 때
     *         IF4ERR_DO_NOT_SUPPORT          이 기능이 지원되지 않을 경우
     */
	public void cmdReqCertRenewal(String mcuId, String deviceSerial, String fileUrl, String checkSum) throws FMPMcuException, Exception;  
	
    public String icmpPing(List<String> commands) throws Exception;
    
    public String traceroute(List<String> commands) throws Exception;
    
    public String coapPing(String ipAddress, String device, String type) throws Exception;
    
    public Map<String, String> coapGetInfo (
            java.lang.String ipv4 , java.lang.String ipv6, java.lang.String type
            ) throws Exception;
    
    public Map<String, String> modemReset (
            java.lang.String ipv4 , java.lang.String ipv6, java.lang.String type
            ) throws Exception;
    
    public Map<String, String> coapBrowser (
            java.lang.String ipv4 , java.lang.String ipv6, java.lang.String uri, String query, String config, String type
            ) throws Exception;

    /**
     * NI Protocol
     * 미터 Baud rate value 설정 커맨드
     * @param mdsId 모뎀 아이디 modem.id(modemID)
     * @param requestType 요청 형태 [get/set]
     * @param rateValue SET일 경우 설정할 BaudRate
     * @return
     * @throws Exception
     */
    public Map<String, String> cmdMeterBaudRate(String mdsId,
                                                String requestType,
                                                int rateValue) throws Exception;
    
    /**
     * NI Protocol
     * Real Time Metering
     * @param mdsId 모뎀 아이디 modem.id(modemID)
     * @param interval 
     * @param duration 
     * @return
     * @throws Exception
     */
    public Map<String, String> cmdRealTimeMetering(String mdsId, int interval, int duration) throws Exception;
   
    /**
     * NI Protocol
     * 모뎀의 이벤트 로그 요청 커맨드
     * @param mdsId 모뎀 아이디 modem.id(modemID)
     * @param logCount 요청할 로그의 개수
     * @return
     */
    // UPDATE START SP-681
//    public Map<String, String> getModemEventLog(String mdsId, int logCount) throws Exception;
    public Map<String, String> getModemEventLog(String mdsId, int logCount, int logOffset) throws Exception;
    // UPDATE END SP-681
    
    public Map<String, Object> setCloneOnOff(String modemId, int count) throws Exception;

	// INSERT START SP-681
	public Map<String, Object> setCloneOnOffWithTarget(String modemId, String cloneCode, int count, String version, int euiCount, List<String> euiList) throws Exception;
	// INSERT END SP-681
    
    /**
     * Command External Command
     * HD, 200.1.1, pre-defined protocol message for external project
     *
     * @return
     * @throws Exception
     * @ejb.interface-method view-type="both"
     */
    public Map<String, String> cmdExtCommand(String mcuId, byte[] generalStream) throws Exception;
    
    public void cmdAuthSPModem(String serialNo, int status, String mcuId) throws FMPMcuException, Exception;
    
    public void cmdAuthSPModem(String serialNo, int status) throws FMPMcuException, Exception;
    
    public void cmdSetMeterSharedKey(String mcuId, String meterId, 
    		String masterKey, 
    		String unicastKey,
    		String multicastKey, 
    		String AuthenticationKey) throws FMPMcuException,Exception;
    
    public void cmdSetKMSNetworkKey(String mcuId, int keyIndex, 
    		String keyStream) throws FMPMcuException,Exception;
    
    /**
     * MCU Get Log 
     *
     * @param mcuId MCU ID
     * @param count get count
     * @return event list
     * @throws FMPMcuException, Exception
     */
    public String cmdMcuGetLog(String mcuId, int count )
        throws FMPMcuException, Exception;        

    /**
     * Get Modem
     *
     * @param mcuId MCU Indentifier
     * @return energy modem data list
     * @throws FMPMcuException, Exception
     */
    public modemG3Entry[] cmdGetManagedG3ModemList(String mcuId, int nOption, String[] modemId) 
            throws FMPMcuException, Exception;
    
    /**
     * MCU Get Schedule
     *
     * @param mcuId MCU ID
     * @param name Schedule Name
     * @return schdule list
     * @throws FMPMcuException, Exception
     */
    public Map<String, Object> cmdMcuGetSchedule(String mcuId, String name )
        throws FMPMcuException, Exception;

	/**
	 * MCU Set Schedule 
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param args
	 *            Schedule settings
	 * @throws FMPMcuException,
	 *             Exception
	 */
	public void cmdMcuSetSchedule(String mcuId, String[][] args) 
			throws FMPMcuException, Exception;
	
	// INSERT START SP-179
	public MeterData cmdGetMeteringData(String mcuId, String meterId, String modemId, String nOption, String fromDate,
			String toDate, String[] modemArray) throws FMPMcuException, Exception;
	
	public MeterData cmdGetROMRead(String mcuId, String meterId, String modemId, String nOption, String fromDate,
			String toDate) throws FMPMcuException, Exception;
	// INSERT END SP-179

    // INSERT START SP-476
	// UPDATE START SP-681
// 	public MeterData cmdDmdNiGetRomRead(String mcuId, String meterId, String modemId, String fromDate, String toDate)
// 			throws FMPMcuException, Exception;
 	public MeterData cmdDmdNiGetRomRead(String mcuId, String meterId, String modemId, String fromDate, String toDate, int pollType)
 			throws FMPMcuException, Exception;
	// UPDATE END SP-681
    // INSERT START SP-476	
	
	/**
     * MCU Get Property 
     *
     * @param mcuId MCU ID
     * @param name Key Name (network.retry.default)
     * @return mcu retry interval time
     * @throws Exception
     */
    public Map<String, Object> cmdMcuGetProperty(String mcuId, String name )
        throws FMPMcuException, Exception;

    /**
	 * MCU Set Property 
	 *
	 * @param mcuId MCU ID
	 * @param key  Argument Name
	 * @param keyValue Each Argument Value (mappping : key[N]=>keyValue[N])
	 * @throws  Exception
	 */
	public Map<String, Object> cmdMcuSetProperty(String mcuId, String[] key, String[] keyValue) 
			throws FMPMcuException, Exception;

    /**
     * Call EventUtil.sendEvent as command
     * WEB에서 EclipseLink를 사용하지 않으니, FEP으로 던짐.
     * @param logId
     * @param eventStatus
     * @return
     * @throws Exception
     */
    public String cmdSendEventByFep(long logId, String eventStatusName)
            throws Exception;


    /**
     * NI Protocol
     * 모뎀의 검침주기 설정
     * @param modemId  modem.id
     * @param requestType  request [get/set]
     * @param interval  interval value to set
     * @return [get : interval data, set : result status]
     */
    public Map<String, String> cmdMeteringInterval(String modemId, String requestType, int interval)
            throws Exception;

    /**
     * NI Protocol
     * 모뎀의 검침데이터 업로드 재시도 횟수 설정
     * @param modemId  modem.id
     * @param requestType  request [get/set]
     * @param retry  number of retry
     * @return  retry count
     */
    public Map<String, String> cmdRetryCount(String modemId, String requestType, int retry)
            throws Exception;

	// INSERT START SP-193
	public String cmdSendEvent(String name, String  target, String id, String[][] params)
			throws Exception;
	// INSERT END SP-193
    
	public void cmdSendEvent2(String eventAlertName, String activatorType, String activatorId, int supplierId) throws Exception;
	
    /**
     * NI Protocol 0x2002 Modem Reset Time
     * 
     * @param modemId  modem.id
     * @param requestType  request [set]
     * @param resetTime    modem reset time [0-23/0xFF]
     * @return [ set : result status]
     */
	public Map<String, String> cmdModemResetTime(String modemId, String requestType, int resetTime) 
			throws Exception;
    /**
     * NI Protocol  0x2003 Modem Mode
     * 
     * @param modemId  modem.id
     * @param requestType  request [set/get]
     * @param type    modem reset time [0x00 : PushMode/0x01 : Poll(Bypass) Mode]
     * @return [get : mode data, set : result status]
     */
	public Map<String, String> cmdModemMode(String modemId, String requestType, int mode )
			throws Exception ;
	
	/**
	 * NI Protocol  0x2010 SNMP Sever IPv6/Port [set/get]
	 * 
	 * @param modemId  modem.id
	 * @param requestType request [set/get]
	 * @param type   type of ipaddress [ 0 : ipv4 / 1 : ipv6 ]
	 * @param ipAddress ipaddress
	 * @return  [SNMP Sever IP/Port data]
	 * @throws Exception
	 */
	public Map<String, String> cmdSnmpServerIpv6Port(String modemId, String requestType, int type , String ipAddress, String port) 
			throws Exception ;
	
	/**
	 *  NI Protocol  0x2013 Transmit Frequency
	 *  
	 * @param modemId modem.id
	 * @param requestType request [set/get]
	 * @param count 
	 * @param cmds array of command
	 * @return [Alarm/Event command data] 
	 * @throws Exception
	 */
	public Map<String, String> cmdAlarmEventCommandOnOff(String modemId, String requestType, int count ,String cmds) 
			throws Exception;
	
	/**
	 * NI Protocol 0x2013 Transmit Frequency 
	 * @param modemId	 modem.id
	 * @param requestType	request [set/get]
	 * @param second　　　　transmit frequency
	 * @return [ get : Transmit Frequency data, set : result status ];
	 * @throws Exception
	 */
	public Map<String, String> cmdTransmitFrequency(String modemId, String requestType, int second) 
			throws Exception ;

	public String sendSMS(String commandName, String messageType, String mobliePhNum, String euiId, String commandCode,	List<String> paramList, String cmdMap) throws Exception;
	
	/**
	 * SP-677
	 * @param mcuId
	 * @param meterIds
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	public  Map<String,Object>  cmdDmdNiGetRomReadMulti(String mcuId, Integer modemPort, String[] meterIds, String[] modemIds, String fromDate, String toDate) throws FMPMcuException, Exception;
	
	/**
     * MCU Get Schedule
     *
     * @param mcuId MCU ID
     * @param name Schedule Name
     * @return schdule list
     * @throws FMPMcuException, Exception
     */
    public Map<String, Object> cmdMcuGetSchedule_(String mcuId, String name )
        throws FMPMcuException, Exception;

	/**
	 * MCU Set Schedule 
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param args
	 *            Schedule settings
	 * @throws FMPMcuException,
	 *             Exception
	 */
	public void cmdMcuSetSchedule_(String mcuId, String[][] args) 
			throws FMPMcuException, Exception;
	
	/**
	 * MCU Delete Schedule 
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param args
	 *            Delete Schedule
	 * @throws FMPMcuException,
	 *             Exception
	 */
	public void cmdMcuDeleteSchedule(String mcuId, String scheduleName) 
			throws FMPMcuException, Exception;
	
	/**
	 * MCU Set Schedule 
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param args
	 *            Schedule settings
	 * @throws FMPMcuException,
	 *             Exception
	 */
	public void cmdMcuExecuteSchedule(String mcuId, String scheduleName) 
			throws FMPMcuException, Exception;
	
	/**
	 * GPRS Modem TCP Command Trigger
	 *
	 * @param cmd Source Command
	 * @param ipAddr IP Address
	 * @return isConnect
	 * @throws  Exception
	 */
	public boolean cmdTCPTrigger(String cmd, String ipAddr)throws Exception;
	
	public Map<String, Object> cmdExecDmdNiCommandMulti(String requestType, String attributeId, String param, String[] modemIds) throws Exception;
	
	
	/**
	 * GPRS Modem cmdLineModemEVN
	 *
	 * @param cmd
	 * @param modemId
	 * @return param
	 * @throws  Exception
	 */
	public Hashtable cmdLineModemEVN(String cmd, String modemId, String[] param)
			throws FMPMcuException, Exception;
}
