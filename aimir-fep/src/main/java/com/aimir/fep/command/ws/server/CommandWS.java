package com.aimir.fep.command.ws.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants.MeterProgramKind;
import com.aimir.constants.CommonConstants.OTATargetType;
import com.aimir.dao.device.MeterDao;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.meter.AbstractMDSaver;
import com.aimir.fep.meter.data.MeterData;
import com.aimir.fep.meter.data.Response;
import com.aimir.fep.meter.file.CSV;
import com.aimir.fep.meter.file.DisplayItemSettingCSV;
import com.aimir.fep.meter.parser.MX2Table.DisplayItemSettingBuilder;
import com.aimir.fep.meter.parser.MX2Table.SummerTimeBuilder;
import com.aimir.fep.meter.parser.MX2Table.TOUCalendarBuilder;
import com.aimir.fep.meter.parser.MX2Table.VOBuilder;
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
import com.aimir.fep.protocol.fmp.frame.service.entry.mobileEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.modemG3Entry;
import com.aimir.fep.protocol.fmp.frame.service.entry.pluginEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.procEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.sensorInfoNewEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.sysEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.timeEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.trInfoEntry;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FileInfo;
import com.aimir.fep.util.GroupInfo;
import com.aimir.fep.util.GroupTypeInfo;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.system.MeterProgram;

/**
 * Command Gateway WebService
 *
 * @author elevas
 * @version $Rev: 1 $, $Date: 2013-01-03 15:59:15 +0900 $,
 */
@WebService(serviceName="CommandWS")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
@Service(value="commandWS")
public class CommandWS
{
    private static Log log = LogFactory.getLog(CommandWS.class);
    
    @Autowired
    private CommandGW command;
    
    @Autowired
    private MeterDao meterDao;
    
    @Resource(name="transactionManager")
    JpaTransactionManager txmanager;
    
    /**
     * MCU Diagnosis
     *
     * @param mcuId MCU Indentifier
     * @return status (MCU status, GSM status, Sink 1 status, Sink 2 status)
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public @WebResult(name="response") ResponseMap cmdMcuDiagnosis(@WebParam(name="McuId") String mcuId)
            throws Exception
    {
        ResponseMap response = new ResponseMap();
        response.setResponse(command.cmdMcuDiagnosis(mcuId));
        return response;
    }

    /**
     * MCU Reset
     *
     * @param mcuId MCU Indentifier
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public void cmdMcuReset(@WebParam(name="McuId") String mcuId) throws Exception
    {
        command.cmdMcuReset(mcuId);
    }

    /**
     * MCU Shutdown
     *
     * @param mcuId MCU Indentifier
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public void cmdMcuShutdown(@WebParam(name="McuId") String mcuId) throws Exception
    {
        command.cmdMcuShutdown(mcuId);
    }

    /**
     * MCU FactoryDefault
     *
     * @param mcuId MCU Indentifier
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public void cmdMcuFactoryDefault(@WebParam(name="McuId") String mcuId) throws Exception
    {
        command.cmdMcuFactoryDefault(mcuId);
    }

    /**
     * MCU Get Time
     *
     * @param mcuId MCU Indentifier
     * @return time String (yyyymmddhhmmss)
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public String cmdMcuGetTime(@WebParam(name="McuId") String mcuId) throws Exception
    {
        return command.cmdMcuGetTime(mcuId);
    }

    /**
     * MCU Set Time
     *
     * @param mcuId MCU Indentifier
     * @param time MCU Time (yyyymmddhhmmss)
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public void cmdMcuSetTime(@WebParam(name="McuId") String mcuId, @WebParam(name="mcuTime") String time)
            throws Exception
    {
        command.cmdMcuSetTime(mcuId, time);
    }

    /**
     * MCU Set GMT
     *
     * @param mcuId MCU Indentifier
     * @return GMT
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public long cmdMcuSetGMT(@WebParam(name="McuId") String mcuId) throws Exception
    {
        return command.cmdMcuSetGMT(mcuId);
    }

    /**
     * MCU Set DST
     *
     * @param mcuId MCU Indentifier
     * @param fileName
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public void cmdMcuSetDST(@WebParam(name="McuId") String mcuId, @WebParam(name="fileName4DST") String fileName)
            throws Exception
    {
        command.cmdMcuSetDST(mcuId, fileName);
    }

    /**
     * MCU Get State
     *
     * @param mcuId MCU Indentifier
     * @return mcu status
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public String cmdMcuGetState(@WebParam(name="McuId") String mcuId) throws Exception
    {
        return command.cmdMcuGetState(mcuId);
    }

    /**
     * MCU Loopback
     *
     * @param mcuId MCU Indentifier
     * @return result (true, false)
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public boolean cmdMcuLoopback(@WebParam(name="McuId") String mcuId) throws Exception
    {
        return command.cmdMcuLoopback(mcuId);
    }

    /**
     * MCU Clear Static
     *
     * @param mcuId MCU Indentifier
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public void cmdMcuClearStatic(@WebParam(name="McuId") String mcuId) throws Exception
    {
        command.cmdMcuClearStatic(mcuId);
    }

    /**
     * MCU Get System Info (Not Available)
     *
     * @param mcuId MCU Indentifier
     * @return mcu system infomation entry
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public sysEntry cmdMcuGetSystemInfo(@WebParam(name="McuId") String mcuId) throws Exception
    {
        return command.cmdMcuGetSystemInfo(mcuId);
    }

    /**
     * MCU Get Environment (Not Available)
     *
     * @param mcuId MCU Indentifier
     * @return Environment entrys (varEntry, varValueEntry, varSubValueEntry)
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public Entry[] cmdMcuGetEnvironment(@WebParam(name="McuId") String mcuId) throws Exception
    {
        return command.cmdMcuGetEnvironment(mcuId);
    }

    /**
     * MCU Get GPIO (Not Available)
     *
     * @param mcuId MCU Indentifier
     * @return GIO Entry
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public gpioEntry cmdMcuGetGpio(@WebParam(name="McuId") String mcuId) throws Exception
    {
        return command.cmdMcuGetGpio(mcuId);
    }

    /**
     * MCU Set Gpio (Not Available)
     *
     * @param mcuId MCU Indentifier
     * @param portNumber GPIO Port Number
     * @param value GPIO Value(0/1)
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public void cmdMcuSetGpio(@WebParam(name="McuId") String mcuId, 
            @WebParam(name="GPIOPortNumber") int portNumber, @WebParam(name="GPIOValue") int value)
                    throws Exception
    {
        command.cmdMcuSetGpio(mcuId, portNumber, value);
    }

    /**
     * MCU Get Ip
     *
     * @param mcuId MCU Indentifier
     * @return ipaddr
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public ResponseMap cmdMcuGetIp(@WebParam(name="McuId") String mcuId) throws Exception
    {
        ResponseMap response = new ResponseMap();
        response.setResponse(command.cmdMcuGetIp(mcuId));
        return response;
    }

    /**
     *
     * --added
     * MCU Get Memory
     *
     * @param mcuId
     * @return memEntry
     * @throws FMPException, Exception
     */
    @WebMethod
    public memEntry cmdMcuGetMemory(@WebParam(name="McuId") String mcuId) throws Exception
    {
        return command.cmdMcuGetMemory(mcuId);
    }

    /**
     * --add
     * Mcu Get Process
     * @param mcuId
     * @return procEntry
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public procEntry[] cmdMcuGetProcess(@WebParam(name="McuId") String mcuId) throws Exception
    {
        return command.cmdMcuGetProcess(mcuId);
    }

    /**
     * --add
     * MCU Get File System
     * @param mcuId
     * @return
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public ResponseMap cmdMcuGetFileSystem(@WebParam(name="McuId") String mcuId) throws Exception
    {
        ResponseMap response = new ResponseMap();
        response.setResponse(command.cmdMcuGetFileSystem(mcuId));
        return response;
    }

    /**
     * --add
     * MCU Get Plugin
     * @param mcuId
     * @return pluginEntry
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public pluginEntry[] cmdMcuGetPlugin(@WebParam(name="McuId") String mcuId) throws Exception
    {
        return command.cmdMcuGetPlugin(mcuId);
    }

    /**
     * --add
     * MCU Get Mobile
     * @param mcuId
     * @return mobileEntry
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public mobileEntry cmdMcuGetMobile(@WebParam(name="McuId") String mcuId) throws Exception
    {
        return command.cmdMcuGetMobile(mcuId);
    }

    /**
     * MCU Reset Device
     *
     * @param mcuId MCU Indentifier
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public void cmdMcuResetDevice(@WebParam(name="McuId") String mcuId, @WebParam(name="Device") int device)
            throws Exception
    {
        command.cmdMcuResetDevice(mcuId, device);
    }

    /**
     * Add Modem
     *
     * @param mcuId MCU Indentifier
     * @param modem Modem
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public void cmdAddModem(@WebParam(name="McuId") String mcuId, @WebParam(name="ModemObject") Modem modem)
            throws Exception
    {
        command.cmdAddModem(mcuId, modem);
    }

    /**
     * Add Sensor MO
     *
     * @param mcuId MCU Indentifier
     * @param modems Modem List
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public void cmdAddModems(@WebParam(name="McuId") String mcuId, @WebParam(name="ModemObjects") Modem[] modems)
            throws Exception
    {
        command.cmdAddModems(mcuId, modems);
    }

    /**
     * MCU Delete Modem
     *
     * @param mcuId MCU Indentifier
     * @param propName property name
     * @param propValue property value
     * @throws FMPMcuException, Exception
     */
    @WebMethod(operationName="cmdDeleteModemByProperty")
    public void cmdDeleteModemByProperty(@WebParam(name="McuId") String mcuId, 
            @WebParam(name="PropertyName") String propName, @WebParam(name="PropertyValue") String propValue)
                    throws Exception
    {
        command.cmdDeleteModem(mcuId, propName, propValue);
    }

    /**
     * MCU Delete Sensor
     *
     * @param mcuId MCU Indentifier
     * @param modem modem id
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public void cmdDeleteModem(@WebParam(name="McuId") String mcuId, @WebParam(name="ModemId") String modemId)
            throws Exception
    {
        command.cmdDeleteModem(mcuId, modemId);
    }

    /**
     * MCU Delete Sensor
     *
     * @param mcuId MCU Indentifier
     * @throws FMPMcuException, Exception
     */
    @WebMethod(operationName="cmdDeleteModemByChannelPan")
    public void cmdDeleteModemByChannelPan(@WebParam(name="McuId") String mcuId,
            @WebParam(name="ModemId") String modemId,
            @WebParam(name="ChannelId") int channelId,
            @WebParam(name="PanId") int panId)
                    throws Exception
    {
        command.cmdDeleteModem(mcuId, modemId, channelId, panId);
    }
    
    /**
     * MCU Delete Sensor All
     *
     * @param mcuId MCU Indentifier
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public void cmdDeleteModemAll(@WebParam(name="McuId") String mcuId) throws Exception
    {
        command.cmdDeleteModemAll(mcuId);
    }
    
    /**
     * MCU Delete Modem All
     *
     * @param mcuId MCU Indentifier
     * @throws FMPMcuException, Exception
     */
    @WebMethod(operationName="cmdDeleteModelAllByChannelPan")
    public void cmdDeleteModemAllByChannelPan(@WebParam(name="McuId") String mcuId,
            @WebParam(name="ChannelId") int channelId, @WebParam(name="PanId") int panId)
                    throws Exception
    {
        command.cmdDeleteModemAll(mcuId, channelId, panId);
    }
    
    /**
     * MCU Delete Member All(SAT2)
     *
     * @param mcuId MCU Indentifier
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public void cmdDeleteMemberAll(@WebParam(name="McuId") String mcuId) throws Exception
    {
        command.cmdDeleteMemberAll(mcuId);
    }
    
    /**
     * Reset Modem
     *
     * @param mcuId MCU Indentifier
     * @param modemId Modem Indentifier
     * @param resetTime 무슨값인지 모름(조차장님한테 확인)
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public void cmdResetModem(@WebParam(name="McuId") String mcuId,
            @WebParam(name="ModemId") String modemId,
            @WebParam(name="ResetTime") int resetTime)
                    throws Exception
    {
        command.cmdResetModem(mcuId, modemId, resetTime);
    }
    
    /**
     * Get New Modem Info List
     *
     * @param mcuId MCU Indentifier
     * @return new modem entry list
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public sensorInfoNewEntry[] cmdGetModemAllNew(@WebParam(name="McuId") String mcuId)
            throws Exception
    {
        return command.cmdGetModemAllNew(mcuId);
    }
    
    /**
     * Get New Modem Info
     *
     * @param modemId Modem Indentifier
     * @return new modem entry
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public sensorInfoNewEntry cmdGetModemInfoNew(@WebParam(name="ModemId") String modemId)
            throws Exception
    {
        return command.cmdGetModemInfoNew(modemId);
    }
    
    /**
     * Get Modem Count
     * --add
     * @param mcuId MCU Indentifier
     * @throws FMPMcuException, Exception
     * @return sensor count
     */
    @WebMethod
    public int cmdGetModemCount(@WebParam(name="McuId") String mcuId) throws Exception
    {
        return command.cmdGetModemCount(mcuId);
    }
    
    /**
     * Clear Communication Log
     *
     * @param mcuId MCU Indentifier
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public void cmdClearCommLog(@WebParam(name="McuId") String mcuId) throws Exception
    {
        command.cmdClearCommLog(mcuId);
    }
    
    /**
     * Clear Command History Log
     *
     * @param mcuId MCU Indentifier
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public void cmdClearCmdHistLog(@WebParam(name="McuId") String mcuId) throws Exception
    {
        command.cmdClearCmdHistLog(mcuId);
    }
    
    /**
     * Clear Mcu Event Log
     *
     * @param mcuId MCU Indentifier
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public void cmdClearEventLog(@WebParam(name="McuId") String mcuId) throws Exception
    {
        command.cmdClearEventLog(mcuId);
    }
    
    /**
     * Clear Metering Log
     *
     * @param mcuId MCU Indentifier
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public void cmdClearMeterLog(@WebParam(name="McuId") String mcuId) throws Exception
    {
        command.cmdClearMeterLog(mcuId);
    }
    
    /**
     * Get Cmd Hist Log List
     *
     * @param mcuId MCU Indentifier
     * @param fromTime From Time
     * @param toTime To Time
     * @return file information list
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public FileInfo[] cmdGetCmdHistList(@WebParam(name="McuId") String mcuId,
            @WebParam(name="FromTime") String fromTime, @WebParam(name="ToTime") String toTime)
                    throws Exception
    {
        return command.cmdGetCmdHistList(mcuId, fromTime, toTime);
    }
    
    /**
     * Get Meter Log List
     *
     * @param mcuId MCU Indentifier
     * @param fromTime From Time
     * @param toTime To Time
     * @return file information list
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public FileInfo[] cmdGetMeterLogList(@WebParam(name="McuId") String mcuId,
            @WebParam(name="FromTime") String fromTime,
            @WebParam(name="ToTime") String toTime)
                    throws Exception
    {
        return command.cmdGetMeterLogList(mcuId, fromTime, toTime);
    }
    
    /**
     * Get Event Log List
     *
     * @param mcuId MCU Indentifier
     * @param fromTime From Time
     * @param toTime To Time
     * @return file information list
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public FileInfo[] cmdGetEventLogList(@WebParam(name="McuId") String mcuId,
            @WebParam(name="FromTime") String fromTime,
            @WebParam(name="ToTime") String toTime)
                    throws Exception
    {
        return command.cmdGetEventLogList(mcuId, fromTime, toTime);
    }
    
    /**
     * Get Communication Log List
     *
     * @param mcuId MCU Indentifier
     * @param fromTime From Time
     * @param toTime To Time
     * @return file information list
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public FileInfo[] cmdGetCommLogList(@WebParam(name="McuId") String mcuId,
            @WebParam(name="FromTime") String fromTime,
            @WebParam(name="ToTime") String toTime)
                    throws Exception
    {
        return command.cmdGetCommLogList(mcuId, fromTime, toTime);
    }
    
    /**
     * Get Mobile Log List
     *
     * @param mcuId MCU Indentifier
     * @param fromTime From Time
     * @param toTime To Time
     * @return file information list
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public FileInfo[] cmdGetMobileLogList(@WebParam(name="McuId") String mcuId,
            @WebParam(name="FromTime") String fromTime,
            @WebParam(name="ToTime") String toTime)
                    throws Exception
    {
        return command.cmdGetMobileLogList(mcuId, fromTime, toTime);
    }
    
    /**
     * Get All Log List
     *
     * @param mcuId MCU Indentifier
     * @param fromTime From Time
     * @param toTime To Time
     * @return file information list
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public FileInfo[] cmdGetAllLogList(@WebParam(name="McuId") String mcuId,
            @WebParam(name="FromTime") String fromTime, @WebParam(name="ToTime") String toTime)
                    throws Exception
    {
        return command.cmdGetAllLogList(mcuId, fromTime, toTime);
    }
    
    /**
     * Metering
     *
     * @param mcuId MCU Indentifier
     * @param propName property name
     * @param propValue property value
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public void cmdMetering(@WebParam(name="McuId") String mcuId, 
            @WebParam(name="PropertyName") String propName,
            @WebParam(name="PropertyValue") String propValue)
                    throws Exception
    {
        command.cmdMetering(mcuId, propName, propValue);
    }
    
    /**
     * Metering All
     *
     * @param mcuId MCU Indentifier
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public void cmdMeteringAll(@WebParam(name="McuId") String mcuId) throws Exception
    {
        command.cmdMeteringAll(mcuId);
    }
    
    /**
     * Metering Recovery
     *
     * @param mcuId MCU Indentifier
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public void cmdRecovery(@WebParam(name="McuId") String mcuId) throws Exception
    {
        command.cmdRecovery(mcuId);
    }
    
    /**
     * Metering Recovery
     *
     * @param mcuId MCU Indentifier
     * @throws FMPMcuException, Exception
     */
    @WebMethod(operationName="cmdRecoveryByTargetTime")
    public void cmdRecoveryByTargetTime(@WebParam(name="McuId") String mcuId, 
            @WebParam(name="TargetTime") String targetTime)
                    throws Exception
    {
        command.cmdRecovery(mcuId, targetTime);
    }
    
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
    @WebMethod
    public MeterData[] cmdGetMeter(@WebParam(name="McuId") String mcuId,
            @WebParam(name="MeterSerialNo") String meterId, 
            @WebParam(name="FromTime") String fromTime,
            @WebParam(name="ToTime") String toTime)
                    throws Exception
    {
        return command.cmdGetMeter(mcuId, meterId, fromTime, toTime);
    }

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
    @WebMethod
    public MeterData cmdGetMeterInfo(@WebParam(name="McuId") String mcuId, 
            @WebParam(name="MeterSerialNo") String meterId)
                    throws Exception
    {
        return command.cmdGetMeterInfo(mcuId, meterId);
    }
    
    /**
     * Get Meter
     *
     * @param mcuId MCU Indentifier
     * @param modemId Modem Indentifier
     * @return energy meter data list
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public MeterData cmdGetMeterInfoFromModem(@WebParam(name="McuId") String mcuId,
            @WebParam(name="ModemId") String modemId)
                    throws Exception
    {
        return command.cmdGetMeterInfoFromModem(mcuId, modemId);
    }
    
    /**
     * Read Table
     *
     * @param mcuId MCU Indentifier
     * @param meterId
     * @param tablenum
     * @return energy meter data list
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public byte[] cmdReadTable(@WebParam(name="McuId") String mcuId,
            @WebParam(name="MeterSerialNo") String meterId, @WebParam(name="TableNo") int tablenum)
                    throws Exception
    {
        return command.cmdReadTable(mcuId, meterId, tablenum);
    }
    
    /**
     * Read Table
     *
     * @param mcuId MCU Indentifier
     * @param sensorId
     * @param tablenum
     * @param data stream
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public byte[] cmdWriteTable(@WebParam(name="McuId") String mcuId,
            @WebParam(name="MeterSerialNo") String meterId, 
            @WebParam(name="TableNo") int tablenum,
            @WebParam(name="DataStream") byte[] stream)
                    throws Exception
    {
        return command.cmdWriteTable(mcuId, meterId, tablenum, stream);
    }
    
    /**
     * Read Table Relay Switch Status(GE SM110)
     *
     * @param mcuId MCU Identifier
     * @param meterId Meter Identifier
     * @throws Exception
     */
    @WebMethod
    public ResponseMap getRelaySwitchStatus( @WebParam(name="McuId") String mcuId, 
            @WebParam(name="MeterSerialNo") String meterId )
                    throws Exception
    {
        ResponseMap response = new ResponseMap();
        response.setResponse(command.getRelaySwitchStatus(mcuId, meterId));
        return response;
    }
    
    /**
     * Write Table Relay Switch and Activate On/Off
     *
     * @param mcuId MCU Identifier
     * @param meterId meter Identifier
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public ResponseMap cmdRelaySwitchAndActivate( @WebParam(name="McuId") String mcuId,
            @WebParam(name="MeterSerialNo") String meterId, 
            @WebParam(name="CommandNo") String cmdNum)
                    throws Exception
    {
        ResponseMap response = new ResponseMap();
        response.setResponse(command.cmdRelaySwitchAndActivate(mcuId, meterId, cmdNum));
        return response;
    }
    
    /**
     * Aidon MCCB
     *
     * @param mcuId MCU Indentifier
     * @param meterId Meter Indentifier
     * @param req mccb control request
     * @return response message
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public String cmdAidonMCCB(@WebParam(name="McuId") String mcuId, 
            @WebParam(name="MeterSerialNo") String meterId, 
            @WebParam(name="ControlRequest") String req)
                    throws Exception
    {
        return command.cmdAidonMCCB(mcuId, meterId, req);
    }
    
    /**
     * Kamstrup CID
     *
     * @param mcuId MCU Indentifier
     * @param sensorId Sensor Indentifier
     * @param req mccb control request
     * @return response message
     * @throws FMPMcuException, Exception
     */
    @WebMethod(operationName="cmdKamstrupCID1")
    public byte[] cmdKamstrupCID1(@WebParam(name="McuId") String mcuId, 
            @WebParam(name="MeterSerialNo") String meterId, 
            @WebParam(name="ControlRequests") String[] req)
                    throws Exception
    {
        return command.cmdKamstrupCID(mcuId, meterId, req);
    }
    
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
    @WebMethod
    public byte[] cmdKamstrupCID(@WebParam(name="McuId") String mcuId, 
            @WebParam(name="MeterSerialNo") String meterId, 
            @WebParam(name="CommandKind") String kind,
            @WebParam(name="ControlRequests") String[] param)
                    throws Exception
    {
        return command.cmdKamstrupCID(mcuId, meterId, kind, param);
    }
    
    /**
     * Get Meter All
     *
     * @param mcuId MCU Indentifier
     * @param fromTime from Time
     * @param toTime from Time
     * @return energy meter data list
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public MeterData[] cmdGetMeterAll(@WebParam(name="McuId") String mcuId,
            @WebParam(name="FromTime") String fromTime,
            @WebParam(name="ToTime") String toTime)
                    throws Exception
    {
        return command.cmdGetMeterAll(mcuId, fromTime, toTime);
    }
    
    /**
     * On-demand Meter
     *
     * @param mcuId MCU Indentifier
     * @param meterId Sensor Indentifier
     * @return energy meter data
     * @throws FMPMcuException, Exception
     */
    @WebMethod(operationName="cmdOnDemandMeter2")
    public MeterData cmdOnDemandMeter2(@WebParam(name="McuId") String mcuId,
            @WebParam(name="MeterSerialNo") String meterId,
            @WebParam(name="ModemId") String modemId,
            @WebParam(name="nOption") String nOption,
            @WebParam(name="FromDate") String fromDate, 
            @WebParam(name="ToDate") String toDate)
                    throws Exception
    {
        log.info("mcuId[" + mcuId + "] meterId[" + meterId+"] modemId[" + modemId+"] nOption[" + nOption + "] fromDate[" + fromDate + "] toDate[" + toDate +"]");
        try {
            MeterData md = command.cmdOnDemandMeter(mcuId, meterId, modemId, nOption, fromDate, toDate);
            return md;
        }
        catch (Exception e) {
            throw e;
        }
    }
    
    @WebMethod(operationName="cmdOnDemandMeter3")
    public String[] cmdOnDemandMeter3(@WebParam(name="McuId") String mcuId, 
            @WebParam(name="MeterId") String meterId, @WebParam(name="ModemId") String modemId,
            @WebParam(name="Kind") String kind, @WebParam(name="Params") String[] param)
    throws Exception
    {
        try {
            String[] ret = command.cmdOnDemandMeter(mcuId, meterId, modemId, kind, param);
            return ret;
        }
        catch (Exception e) {
            throw e;
        }
    }
    
    /**
     * On-demand Meter
     *
     * @param mcuId MCU Indentifier
     * @param meterId Sensor Indentifier
     * @return energy meter data
     * @throws FMPMcuException, Exception
     */
    @WebMethod(operationName="cmdOnDemandMeterByPass")
    public MeterData cmdOnDemandMeterByPass(@WebParam(name="McuId") String mcuId,
            @WebParam(name="MeterSerialNo") String meterId,
            @WebParam(name="ModemId") String modemId,
            @WebParam(name="nOption") String nOption,
            @WebParam(name="FromDate") String fromDate, 
            @WebParam(name="ToDate") String toDate)
                    throws Exception
    {
        log.info("mcuId[" + mcuId + "] meterId[" + meterId+"] modemId[" + modemId+"] nOption[" + nOption + "] fromDate[" + fromDate + "] toDate[" + toDate +"]");
        TransactionStatus txstatus = null;
        Class clazz = null;
        try {
            txstatus = txmanager.getTransaction(null);
            Meter meter = meterDao.get(meterId);
            clazz = Class.forName(meter.getModel().getDeviceConfig().getSaverName());
            txmanager.commit(txstatus);
        }
        catch (Exception e) {
            if (txstatus != null) txmanager.rollback(txstatus);
            throw e;
        }
        AbstractMDSaver saver = (AbstractMDSaver)DataUtil.getBean(clazz);
        
        MeterData md = saver.onDemandMeterBypass(mcuId, meterId, modemId, nOption, fromDate, toDate);
        return md;
    }
    
    
    /**
     * On-demand Meter for kamstrup mmiu meter
     *
     * @param mcuId MCU Indentifier
     * @param meterId Sensor Indentifier
     * @return no return data (async data request)
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public void cmdOnDemandMeterAsync(@WebParam(name="McuId") String mcuId, 
            @WebParam(name="MeterSerialNo") String meterId, 
            @WebParam(name="ModemId") String modemId,
            @WebParam(name="Kind") String kind, 
            @WebParam(name="Param") String[] param)
                    throws Exception
    {
        command.cmdOnDemandMeterAsync(mcuId, meterId, modemId, kind, param);
    }
    
    /**
     * set conf for ieiu modem
     *
     * @param mcuId MCU Indentifier
     * @param meterId Sensor Indentifier
     * @return no return data (async data request)
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public byte[] cmdSetIEIUConf(@WebParam(name="McuId") String mcuId, 
            @WebParam(name="MeterSerialNo") String meterId, 
            @WebParam(name="ModemId") String modemId, 
            @WebParam(name="Kind") String kind,
            @WebParam(name="Param") String[] param)
                    throws Exception
    {
        return command.cmdSetIEIUConf(mcuId, meterId, modemId, kind, param);
    }
    
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
    @WebMethod
    public MeterData cmdOnDemandMBus(@WebParam(name="McuId") String mcuId, 
            @WebParam(name="MeterSerialNo") String meterId,
            @WebParam(name="ModemId") String modemId,
            @WebParam(name="nPort") String nPort, 
            @WebParam(name="nOption") String nOption,
            @WebParam(name="FromDate") String fromDate, 
            @WebParam(name="ToDate") String toDate)
                    throws Exception
    {
        JpaTransactionManager txManager = null;
        TransactionStatus txStatus = null;
        try {
            txManager =
                (JpaTransactionManager)DataUtil.getBean("transactionManager");

            txStatus = txManager.getTransaction(null);
            MeterData md = command.cmdOnDemandMBus(mcuId, meterId, modemId, nPort, nOption, fromDate, toDate);
            
            txManager.commit(txStatus);
            return md;
        }
        catch (Exception e) {
            txManager.rollback(txStatus);
            throw e;
        }
    }
    
    /**
     * Request PLC Data Frame
     * @param mcuId
     * @param pdf
     * @return
     * @throws Exception
     */
    @WebMethod
    public PLCDataFrame requestPLCDataFrame(@WebParam(name="McuId") String mcuId,
            @WebParam(name="PLCDataFrame") PLCDataFrame pdf)
                    throws Exception
    {
        return command.requestPLCDataFrame(mcuId, pdf);
    }

    /**
     * On-demand Meter All
     *
     * @param mcuId MCU Indentifier
     * @return energy meter data list
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public MeterData[] cmdOnDemandMeterAll(@WebParam(name="McuId") String mcuId)
            throws Exception
    {
        JpaTransactionManager txManager = null;
        TransactionStatus txStatus = null;
        try {
            txManager =
                (JpaTransactionManager)DataUtil.getBean("transactionManager");

            txStatus = txManager.getTransaction(null);
            MeterData[] mds = command.cmdOnDemandMeterAll(mcuId);
            
            txManager.commit(txStatus);
            return mds;
        }
        catch (Exception e) {
            txManager.rollback(txStatus);
            throw e;
        }
    }
    
    /**
     * Get Current Meter
     *
     * @param mcuId MCU Indentifier
     * @param isMeter meter(true) or sensor(false)
     * @param deviceId meter serial number or sensor id
     * @return energy meter data
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public MeterData cmdGetCurMeter(@WebParam(name="McuId") String mcuId, 
            @WebParam(name="DeviceId") String deviceId,
            @WebParam(name="IsMeter") boolean isMeter)
                    throws Exception
    {
        JpaTransactionManager txManager = null;
        TransactionStatus txStatus = null;
        try {
            txManager =
                (JpaTransactionManager)DataUtil.getBean("transactionManager");

            txStatus = txManager.getTransaction(null);
            MeterData md = command.cmdGetCurMeter(mcuId, deviceId, isMeter);
            
            txManager.commit(txStatus);
            return md;
        }
        catch (Exception e) {
            txManager.rollback(txStatus);
            throw e;
        }
    }
    
    /**
     * Get Current Meter All
     *
     * @param mcuId MCU Indentifier
     * @return energy meter data list
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public MeterData[] cmdGetCurMeterAll(@WebParam(name="McuId") String mcuId)
            throws Exception
    {
        JpaTransactionManager txManager = null;
        TransactionStatus txStatus = null;
        try {
            txManager =
                (JpaTransactionManager)DataUtil.getBean("transactionManager");

            txStatus = txManager.getTransaction(null);
            MeterData[] mds = command.cmdGetCurMeterAll(mcuId);
            
            txManager.commit(txStatus);
            return mds;
        }
        catch (Exception e) {
            txManager.rollback(txStatus);
            throw e;
        }
    }
    
    /**
     * Report Current Meter (Retry)
     *
     * @param mcuId MCU Indentifier
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public void cmdReportCurMeter(@WebParam(name="McuId") String mcuId) throws Exception
    {
        command.cmdReportCurMeter(mcuId);
    }
    
    /**
     * --add
     * Get Meter Time
     *
     * @param mcuId
     * @param sensorId
     * @return timeEntry
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public timeEntry cmdGetMeterTime(@WebParam(name="McuId") String mcuId, @WebParam(name="ModemId") String modemId)
            throws Exception
    {
        return command.cmdGetMeterTime(mcuId, modemId);
    }
    
    /**
     * --add
     *
     * Set Meter Time
     * @param mcuId MCU Identifier
     * @param meterId Meter Identifier
     * @param time
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public void cmdSetMeterTime(@WebParam(name="McuId") String mcuId,
            @WebParam(name="MeterSerialNo") String meterId,
            @WebParam(name="Time") String time)
                    throws Exception
    {
        command.cmdSetMeterTime(mcuId, meterId, time);
    }
    
    /**
     * --add
     * Get Phone List
     * @param mcuId
     * @return stringEntry
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public String[] cmdGetPhoneList(@WebParam(name="McuId") String mcuId)
            throws Exception
    {
        return command.cmdGetPhoneList(mcuId);
    }
    
    /**
     * --add
     * Set Phone List
     *
     * @param mcuId
     * @param phoneNumber
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public void cmdSetPhoneList(@WebParam(name="McuId") String mcuId,
            @WebParam(name="PhoneNumberList") String[] phoneNumber)
                    throws Exception
    {
        command.cmdSetPhoneList(mcuId, phoneNumber);
    }
    
    /**
     * -add
     * Meter Upload Range
     *
     * @param mcuId
     * @param startTime
     * @param endTime
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public void cmdMeterUploadRange(@WebParam(name="McuId") String mcuId,
            @WebParam(name="StartTime") String startTime,
            @WebParam(name="EndTime") String endTime)
                    throws Exception
    {
        command.cmdMeterUploadRange(mcuId, startTime, endTime);
    }
    
    /**
     * Install File
     *
     * @param mcuId MCU Indentifier
     * @param filename filename
     * @param type install type
     * @param reservationTime reservation time
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public void cmdInstallFile(@WebParam(name="McuId") String mcuId,
            @WebParam(name="Filename") String filename)
                    throws Exception
    {
        command.cmdInstallFile(mcuId, filename);
    }
    
    @WebMethod
    public Response cmdBypassTimeSync(@WebParam(name="ModemSerialNo") String modemSerial,
            @WebParam(name="LoginId") String loginId) throws Exception {
        return command.cmdBypassTimeSync(modemSerial, loginId);
    }
    
    /**
     * notification firmware update
     *
     * @param mcuId MCU Indentifier
     * @param filename filename
     * @param type install type
     * @param reservationTime reservation time
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public void cmdFirmwareUpdate(@WebParam(name="McuId") String mcuId) throws Exception
    {
        command.cmdFirmwareUpdate(mcuId);
    }

    /**
     * Install File
     *
     * @param mcuId MCU Indentifier
     * @param filename filename
     * @param type install type
     * @param reservationTime reservation time
     * @throws FMPMcuException, Exception
     */
    @WebMethod(operationName="cmdInstallFile1")
    public void cmdInstallFile1(@WebParam(name="McuId") String mcuId,
            @WebParam(name="Filename") String filename, @WebParam(name="InstallType") int type,
            @WebParam(name="ReservationTime") String reservationTime)
                    throws Exception
    {
        command.cmdInstallFile(mcuId, filename, type, reservationTime);
    }

    /**
     * Put File
     *
     * @param mcuId MCU Indentifier
     * @param filename filename
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public void cmdPutFile(@WebParam(name="McuId") String mcuId,
            @WebParam(name="Filename") String filename)
                    throws Exception
    {
        command.cmdPutFile(mcuId, filename);
    }
    
    /**
     * Get File
     *
     * @param mcuId MCU Indentifier
     * @param filename filename
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public void cmdGetFile(@WebParam(name="McuId") String mcuId, 
            @WebParam(name="Filename") String filename)
                    throws Exception
    {
        command.cmdGetFile(mcuId, filename);
    }
    
    /**
     * MCU Standard Get
     *
     * @param mcuId MCU Indentifier
     * @param mop MOPROPERTY
     * @return moproperty
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public ResponseMap cmdStdGet(@WebParam(name="McuId") String mcuId, 
            @WebParam(name="PropertyName") String propName)
                    throws Exception
    {
        ResponseMap response = new ResponseMap();
        response.setResponse(command.cmdStdGet(mcuId, propName));
        return response;
    }
    
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
    @WebMethod(operationName="cmdReqNodeUpgradeEVN")
    public ResponseMap cmdReqNodeUpgradeEVN(@WebParam(name="McuId") String mcuId,
    		@WebParam(name="UpgradeType") int upgradeType,
    		@WebParam(name="ControlCode") int controlCode,    		 
    		@WebParam(name="ImageKey") String imageKey,
    		@WebParam(name="ImageUrl") String imageUrl,
    		@WebParam(name="CheckSum") String checkSum,    
    		@WebParam(name="Filter") int[] filter,
			@WebParam(name="FilterValue") String[] filterValue)    
    				throws Exception
	{
		ResponseMap response = new ResponseMap();
		response.setResponse(this.command.cmdReqNodeUpgradeEVN(mcuId, upgradeType, controlCode, 
											imageKey, imageUrl, checkSum, filter, filterValue));
		return response; 
	}
    
    /**
     * Change Device Node Status 
     *
     * @param mcuId MCU Indentifier
     * @param int requestId
     * @param int opCode
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public void cmdCtrlUpgradeRequestEVN(@WebParam(name="McuId") String mcuId,
    		@WebParam(name="RequestId") int requestId,
    		@WebParam(name="OpCode") int opCode)
    		throws Exception
    {
    	this.command.cmdCtrlUpgradeRequestEVN(mcuId, requestId, opCode);
    }
    
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
    @WebMethod(operationName="cmdCreateTunnel")
    public ResponseMap cmdCreateTunnel(@WebParam(name="McuId") String mcuId,
    		@WebParam(name="MeterId") String meterId,
    		@WebParam(name="MsgTimeout") int msgTimeout,
    		@WebParam(name="TunnelTimeout") int tunnelTimeout    		 
    		)    
    				throws Exception
	{
		ResponseMap response = new ResponseMap();
		response.setResponse(this.command.cmdCreateTunnel(mcuId, meterId, msgTimeout, tunnelTimeout));
		return response; 
	}
    
    /**
     * Connect Bypass tunnel
     *
     * @param mcuId MCU Indentifier
     * @param String modemId
     * @throws FMPMcuException, Exception
     */   
    public ResponseMap cmdConnectByPass(@WebParam(name="McuId") String mcuId,
    		@WebParam(name="PortNumber") int portNumber,
    		@WebParam(name="Param") String[] param
    		)throws Exception
    {
    	ResponseMap response = new ResponseMap();
    	response.setResponse(this.command.cmdConnectByPass(mcuId, portNumber, param));
    	return response;    	
    }
    
    /**
     * Delete Bypass Tunnel
     *
     * @param mcuId MCU Indentifier
     * @param String meterId
     * @throws FMPMcuException, Exception
     */    
    @WebMethod
    public void cmdDeleteTunnel(@WebParam(name="McuId") String mcuId,
    		@WebParam(name="MeterId") String meterId
    		)
    		throws Exception
    {
    	this.command.cmdDeleteTunnel(mcuId, meterId);
    }
    
    /**
     * MCU Standard Get (Many Properties)
     *
     * @param mcuId MCU Indentifier
     * @param mop MOPROPERTYs
     * @return moproperties
     * @throws FMPMcuException, Exception
     */
    @WebMethod(operationName="cmdStdGet1")
    public ResponseMap cmdStdGet1(@WebParam(name="McuId") String mcuId, 
            @WebParam(name="PropertyNameList")  String[] propNames)
                    throws Exception
    {
        ResponseMap response = new ResponseMap();
        response.setResponse(command.cmdStdGet(mcuId, propNames));
        return response;
    }
    
    /**
     * MCU Entry Standard Get
     *
     * @param mcuId MCU Indentifier
     * @param index Index
     * @param propNames properties
     * @return moproperties
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public ResponseMap cmdEntryStdGet(@WebParam(name="McuId") String mcuId,
            @WebParam(name="Index") String index, @WebParam(name="PropertyNameList") String[] propNames)
                    throws Exception
    {
        ResponseMap response = new ResponseMap();
        response.setResponse(command.cmdEntryStdGet(mcuId, index, propNames));
        return response;
    }
    
    /**
     * MCU Standard Get Child
     *
     * @param mcuId MCU Indentifier
     * @param oid OID
     * @return moproperties
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public ResponseMap cmdStdGetChild(@WebParam(name="McuId") String mcuId,
            @WebParam(name="ObjectId") String oid)
                    throws Exception
    {
        ResponseMap response = new ResponseMap();
        response.setResponse(command.cmdStdGetChild(mcuId, oid));
        return response;
    }
    
    /**
     * MCU Standard Set
     *
     * @param mcuId MCU Indentifier
     * @param propName property name
     * @param propValue property value
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public void cmdStdSet(@WebParam(name="McuId") String mcuId, 
            @WebParam(name="PropertyName") String propName, 
            @WebParam(name="PropertyValue") String propValue)
                    throws Exception
    {
        command.cmdStdSet(mcuId, propName, propValue);
    }
    
    /**
     * MCU Standard Set (Many Properties)
     *
     * @param mcuId MCU Indentifier
     * @param propNames properties
     * @param propValues property values
     * @throws FMPMcuException, Exception
     */
    @WebMethod(operationName="cmdStdSet1")
    public void cmdStdSet1(@WebParam(name="McuId") String mcuId,
            @WebParam(name="PropertyNameList") String[] propNames,
            @WebParam(name="PropertyValueList") String[] propValues)
                    throws Exception
    {
        command.cmdStdSet(mcuId, propNames, propValues);
    }
    
    /**
     * MCU Entry Standard Set
     *
     * @param mcuId MCU Indentifier
     * @param index Index
     * @param propNames properties
     * @param propValues property value
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public void cmdEntryStdSet(@WebParam(name="McuId") String mcuId,
            @WebParam(name="Index") String index,
            @WebParam(name="PropertyNameList") String[] propNames,
            @WebParam(name="PropertyValueList") String[] propValues)
                    throws Exception
    {
        command.cmdEntryStdSet(mcuId, index, propNames, propValues);
    }
    
    /**
     * --added 2007.05.22
     * Write Table Meter Time Sync to MCU
     *
     * @param mcuId MCU Identifier
     * @param meterId meter Identifier
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public byte[] cmdMeterTimeSync( @WebParam(name="McuId") String mcuId,
            @WebParam(name="MeterSerialNo") String meterId)
                    throws Exception
    {
        return command.cmdMeterTimeSync(mcuId, meterId);
    }
    
    /**
     * --added 2013.11.19
     * Write Table Meter Time Sync to MCU (Gtype Meter)
     *
     * @param mcuId MCU Identifier
     * @param meterId meter Identifier
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public ResponseMap cmdMeterTimeSyncByGtype( @WebParam(name="McuId") String mcuId,
            @WebParam(name="MeterSerialNo") String meterId)
                    throws Exception
    {
    	ResponseMap response = new ResponseMap();
        response.setResponse(command.cmdMeterTimeSyncByGtype(mcuId, meterId));
        return response;
    }
    
    /**
     * --added 2007.06.05
     *
     * @param mcuId MCU Identifier
     * @param sensorId sensor Identifier
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public Object cmdGetMeterSchedule( @WebParam(name="McuId") String mcuId,
            @WebParam(name="ModemId") String modemId,
            @WebParam(name="nOption") int nOption,
            @WebParam(name="nOffset") int nOffset,
            @WebParam(name="nCount") int nCount)
                    throws Exception
    {
        return command.cmdGetMeterSchedule(mcuId, modemId, nOption, nOffset, nCount);
    }
    
    /**
     * --added 2007.05.26
     * Get mcu configuration file(OID 100.26)
     *
     * @param mcuId MCU Identifier
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public String[] cmdGetConfiguration(@WebParam(name="McuId") String mcuId)
            throws Exception
    {
        return command.cmdGetConfiguration(mcuId);
    }
    
    /**
     * --added 2007.05.26
     * mcu configuration file setting(OID 100.27)
     *
     * @param mcuId MCU Identifier
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public void cmdSetConfiguration(@WebParam(name="McuId") String mcuId)
            throws Exception
    {
        command.cmdSetConfiguration(mcuId);
    }
    
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
    @WebMethod(operationName="cmdGetModemROM1")
    public ModemROM cmdGetModemROM1(@WebParam(name="McuId") String mcuId, 
            @WebParam(name="ModemId") String modemId,
            @WebParam(name="ROMAddressList") int[][] args)
                    throws Exception
    {
        return command.cmdGetModemROM(mcuId, modemId, args);
    }
    
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
    @WebMethod
    public byte[] cmdGetModemROM(@WebParam(name="McuId") String mcuId, 
            @WebParam(name="ModemId") String modemId, 
            @WebParam(name="ROMAddress") int address,
            @WebParam(name="Length") int length)
                    throws Exception
    {
        return command.cmdGetModemROM(mcuId, modemId, address, length);
    }
    
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
    @WebMethod
    public void cmdSetModemROM(@WebParam(name="McuId") String mcuId,
            @WebParam(name="ModemId") String modemId,
            @WebParam(name="ROMAddress") int address,
            @WebParam(name="Data") byte[] data)
                    throws Exception
    {
        command.cmdSetModemROM(mcuId, modemId, address, data);
    }
    
    /**
     * --added 2007.10.02
     * Get Codi List
     *
     * @param mcuId MCU Indentifier
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public codiEntry[] cmdGetCodiList(@WebParam(name="McuId") String mcuId)
            throws Exception
    {
        return command.cmdGetCodiList(mcuId);
    }
    
    /**
     * --added 2007.10.02
     * Get Codi Info
     *
     * @param mcuId MCU Indentifier
     * @param codiIndex codi Indentifier
     * @throws FMPMcuException, Exception
     */
    @WebMethod(operationName="cmdGetCodiInfo1")
    public codiEntry cmdGetCodiInfo1(@WebParam(name="McuId") String mcuId,
            @WebParam(name="CodiId") int codiIndex)
                    throws Exception
    {
        return command.cmdGetCodiInfo(mcuId, codiIndex);
    }
    
    /**
     * --added 2007.10.02
     * Get Codi Info
     *
     * @param mcuId MCU Indentifier
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public codiEntry cmdGetCodiInfo(@WebParam(name="McuId") String mcuId)
            throws Exception
    {
        return command.cmdGetCodiInfo(mcuId);
    }
    
    /**
     * --added 2007.10.02
     * Get Codi Device
     *
     * @param mcuId MCU Indentifier
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public codiDeviceEntry cmdGetCodiDevice(@WebParam(name="McuId") String mcuId)
            throws Exception
    {
        return command.cmdGetCodiDevice(mcuId);
    }
    
    /**
     * --added 2007.10.02
     * Get Codi Device
     *
     * @param mcuId MCU Indentifier
     * @param codiIndex codi Indentifier
     * @throws FMPMcuException, Exception
     */
    @WebMethod(operationName="cmdGetCodiDevice1")
    public codiDeviceEntry cmdGetCodiDevice1(@WebParam(name="McuId") String mcuId,
            @WebParam(name="CodiId") int codiIndex)
                    throws Exception
    {
        return command.cmdGetCodiDevice(mcuId, codiIndex);
    }
    
    /**
     * --added 2007.10.02
     * Get Codi Device
     *
     * @param mcuId MCU Indentifier
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public codiBindingEntry cmdGetCodiBinding(@WebParam(name="McuId") String mcuId)
            throws Exception
    {
        return command.cmdGetCodiBinding(mcuId);
    }
    
    /**
     * --added 2007.10.02
     * Get Codi Binding
     *
     * @param mcuId MCU Indentifier
     * @param codiIndex codi Indentifier
     * @throws FMPMcuException, Exception
     */
    @WebMethod(operationName="cmdGetCodiBinding1")
    public codiBindingEntry cmdGetCodiBinding1(@WebParam(name="McuId") String mcuId,
            @WebParam(name="CodiId") int codiIndex)
                    throws Exception
    {
        return command.cmdGetCodiBinding(mcuId, codiIndex);
    }
    
    /**
     * --added 2007.10.02
     * Get Codi Neighbor node
     *
     * @param mcuId MCU Indentifier
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public codiNeighborEntry cmdGetCodiNeighbor(@WebParam(name="McuId") String mcuId)
            throws Exception
    {
        return command.cmdGetCodiNeighbor(mcuId);
    }
    
    /**
     * --added 2007.10.02
     * Get Codi Neighbor node
     *
     * @param mcuId MCU Indentifier
     * @param codiIndex codi Indentifier
     * @throws FMPMcuException, Exception
     */
    @WebMethod(operationName="cmdGetCodiNeighbor1")
    public codiNeighborEntry cmdGetCodiNeighbor1(@WebParam(name="McuId") String mcuId,
            @WebParam(name="CodiId") int codiIndex)
                    throws Exception
    {
        return command.cmdGetCodiNeighbor(mcuId, codiIndex);
    }
    
    /**
     * --added 2007.10.02
     * Get Codi Memory
     *
     * @param mcuId MCU Indentifier
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public codiMemoryEntry cmdGetCodiMemory(@WebParam(name="McuId") String mcuId)
            throws Exception
    {
        return command.cmdGetCodiMemory(mcuId);
    }
    
    /**
     * --added 2007.10.02
     * Get Codi Memory
     *
     * @param mcuId MCU Indentifier
     * @param codiIndex codi Indentifier
     * @throws FMPMcuException, Exception
     */
    @WebMethod(operationName="cmdGetCodiMemory1")
    public codiMemoryEntry cmdGetCodiMemory1(@WebParam(name="McuId") String mcuId,
            @WebParam(name="CodiId") int codiIndex)
                    throws Exception
    {
        return command.cmdGetCodiMemory(mcuId, codiIndex);
    }
    
    /**
     * --added 2007.10.02
     * Get Codi Permit
     *
     * @param mcuId MCU Indentifier
     * @param codiIndex codi Indentifier
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public void cmdSetCodiPermit(@WebParam(name="McuId") String mcuId,
            @WebParam(name="CodiId") int codiIndex, 
            @WebParam(name="CodiPermission")  int codiPermit)
                    throws Exception
    {
        command.cmdSetCodiPermit(mcuId, codiIndex, codiPermit);
    }
    
    /**
     * --added 2007.10.02
     * Get Codi FormNetwork
     *
     * @param mcuId MCU Indentifier
     * @param codiIndex codi Indentifier
     * @param value set value
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public void cmdSetCodiFormNetwork(@WebParam(name="McuId") String mcuId,
            @WebParam(name="CodiId") int codiIndex,
            @WebParam(name="Value") int value)
                    throws Exception
    {
        command.cmdSetCodiFormNetwork(mcuId, codiIndex, value);
    }
    
    /**
     * --added 2007.10.02
     * Set Codi Reset
     *
     * @param mcuId MCU Indentifier
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public void cmdSetCodiReset(@WebParam(name="McuId") String mcuId)
            throws Exception
    {
        command.cmdSetCodiReset(mcuId);
    }
    
    /**
     * --added 2007.10.02
     * Set Codi Reset
     *
     * @param mcuId MCU Indentifier
     * @param codiIndex codi Indentifier
     * @throws FMPMcuException, Exception
     */
    @WebMethod(operationName="cmdSetCodiReset1")
    public void cmdSetCodiReset1(@WebParam(name="McuId") String mcuId,
            @WebParam(name="CodiId") int codiIndex)
                    throws Exception
    {
        command.cmdSetCodiReset(mcuId, codiIndex);
    }
    
    /**
     * --added 2007.10.22
     * Correct modem pulse
     *
     * @param mcuId MCU Indentifier
     * @param modemIds Modem Id Array
     * @param adjPulse adjustment pulse value array
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public void cmdCorrectModemPulse(@WebParam(name="McuId") String mcuId,
            @WebParam(name="ModemIdList") String[] modemIds,
            @WebParam(name="AdjustmenetPulse") int[] adjPulse)
                    throws Exception
    {
        command.cmdCorrectModemPulse(mcuId, modemIds, adjPulse);
    }
    
    /**
     * cmdSetModemAmrData
     *
     * @param mcuId MCU Indentifier
     * @param modemId Modem Indentifier
     * @param data
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public void cmdSetModemAmrData(@WebParam(name="McuId") String mcuId,
            @WebParam(name="ModemId") String modemId,
            @WebParam(name="AMRMask") byte[] amrMask,
            @WebParam(name="AMRData") byte[] data)
                    throws Exception
    {
        command.cmdSetModemAmrData(mcuId, modemId, amrMask, data);
    }
    
    /**
     * cmdGetModemAmrData
     *
     * @param mcuId MCU Indentifier
     * @param modemId Modem Indentifier
     * @param data
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public byte[] cmdGetModemAmrData(@WebParam(name="McuId") String mcuId, 
            @WebParam(name="ModemId") String modemId)
                    throws Exception
    {
        return command.cmdGetModemAmrData(mcuId, modemId);
    }
    
    /**
     * cmdGetSensorAmrData
     *
     * @param mcuId MCU Identifier
     * @param modemId Modem Identifier
     * @param data
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public byte[] cmdCommandModem(@WebParam(name="McuId") String mcuId, 
            @WebParam(name="ModemId") String modemId,
            @WebParam(name="CommandType") byte cmdType,
            @WebParam(name="CommandData") byte[] data)
                    throws Exception
    {
        return command.cmdCommandModem(mcuId, modemId, cmdType, data);
    }
    
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
    @WebMethod
    public byte[] cmdExecuteCommand(@WebParam(name="McuId") String mcuId, 
            @WebParam(name="ModemId") String modemId,
            @WebParam(name="ModemCommand") byte modemCommand,
            @WebParam(name="RWFlag") byte rwFlag,
            @WebParam(name="CommandData") byte[] commandData)
                    throws Exception
    {
        return command.cmdExecuteCommand(mcuId, modemId, modemCommand, rwFlag, commandData);
    }
    
    /**
     * cmdCommandSensor

     * @param mcuId MCU Identifier
     * @param modemId Modem Identifier
     * @param data
     * @throws FMPMcuException, Exception
     */
    @WebMethod(operationName="cmdCommandModem1")
    public byte[] cmdCommandModem1(@WebParam(name="McuId") String mcuId, 
            @WebParam(name="ModemId") String modemId, @WebParam(name="CommandType") byte cmdType,
            @WebParam(name="FW") int fw, @WebParam(name="BuildNo") int buildnum, 
            @WebParam(name="Force") boolean force, @WebParam(name="Data") byte[] data)
                    throws Exception
    {
        return command.cmdCommandModem(mcuId, modemId, cmdType, fw, buildnum, force, data);
    }
    
    /**
     * Sensor Firmware update(OTA)
     *
     * @param modemId Modem Indentifier
     * @param filename filename
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public void cmdUpdateModemFirmware(@WebParam(name="McuId") String mcuId,
            @WebParam(name="ModemId") String modemId, @WebParam(name="Filename") String fileName)
                    throws Exception
    {
        command.cmdUpdateModemFirmware(mcuId, modemId, fileName);
    }
    
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
    @WebMethod
    public void cmdPackageDistribution(@WebParam(name="McuId") String mcuId,
            @WebParam(name="EquipType") int equipType,
            @WebParam(name="TriggerId") String triggerId,
            @WebParam(name="OldHwVersion") String oldHwVersion,
            @WebParam(name="OldSwVersion") String oldSwVersion,
            @WebParam(name="OldBuildNo") String oldBuildNumber,
            @WebParam(name="NewHwVersion") String newHwVersion,
            @WebParam(name="NewSwVersion") String newSwVersion,
            @WebParam(name="NewBuildNo") String newBuildNumber,
            @WebParam(name="BinaryMD5") String binaryMD5,
            @WebParam(name="BinaryUrl") String binaryUrl,
            @WebParam(name="DiffMD5") String diffMD5,
            @WebParam(name="DiffUrl") String diffUrl,
            @WebParam(name="EquipList") String[] equipList,
            @WebParam(name="OTAType") int otaType,
            @WebParam(name="ModemType") int modemType,
            @WebParam(name="ModemTypeStr") String modemTypeStr,
            @WebParam(name="DataType") int dataType,
            @WebParam(name="OTALevel") int otaLevel,
            @WebParam(name="OTARetry") int otaRetry)
                    throws Exception
    {
        command.cmdPackageDistribution(mcuId, equipType, triggerId, oldHwVersion,
                oldSwVersion, oldBuildNumber, newHwVersion, newSwVersion, 
                newBuildNumber, binaryMD5, binaryUrl, diffMD5, diffUrl,
                equipList, otaType, modemType, modemTypeStr, dataType, otaLevel, otaRetry);
    }
    
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
    @WebMethod
    public void cmdDistribution(@WebParam(name="McuId") String mcuId,
                                @WebParam(name="TriggerId") String triggerId,
                                @WebParam(name="EquipKind") int equipKind,
                                @WebParam(name="Model") String model,
                                @WebParam(name="TransferType") int transferType,
                                @WebParam(name="OTAStep") int otaStep,
                                @WebParam(name="MultiWriteCount") int multiWriteCount,
                                @WebParam(name="MaxRetryCount") int maxRetryCount,
                                @WebParam(name="OTAThreadCount") int otaThreadCount,
                                @WebParam(name="InstallType") int installType,
                                @WebParam(name="OldHwVersion") int oldHwVersion,
                                @WebParam(name="OldFwVersion") int oldFwVersion,
                                @WebParam(name="OldBuild") int oldBuild,
                                @WebParam(name="NewHwVersion") int newHwVersion,
                                @WebParam(name="NewFwVersion") int newFwVersion,
                                @WebParam(name="NewBuild") int newBuild,
                                @WebParam(name="BinaryURL") String binaryURL,
                                @WebParam(name="BinaryMD5") String binaryMD5,
                                @WebParam(name="DiffURL") String diffURL,
                                @WebParam(name="DiffMD5") String diffMD5,
                                @WebParam(name="EquipIdList") List equipIdList)
                                        throws Exception
    {
        command.cmdDistribution(mcuId, triggerId, equipKind, model, transferType,
                otaStep, multiWriteCount, maxRetryCount, otaThreadCount,
                installType, oldHwVersion, oldFwVersion, oldBuild,
                newHwVersion, newFwVersion, newBuild, binaryURL, binaryMD5, diffURL, diffMD5, equipIdList);
    }
    
            
    /**
     * cmdDistributionState
     * @param mcuId
     * @param triggerId
     * @return
     * @throws FMPMcuException
     * @throws Exception
     */
    @WebMethod
    public List cmdDistributionState(@WebParam(name="McuId") String mcuId, 
            @WebParam(name="TriggerId") String triggerId)
                    throws Exception
    {
        return command.cmdDistributionState(mcuId, triggerId);
    }
    
    /**
     * cmdDistributionCancel
     * @param mcuId
     * @param triggerId
     * @throws FMPMcuException
     * @throws Exception
     */
    @WebMethod
    public void cmdDistributionCancel(@WebParam(name="McuId") String mcuId, 
            @WebParam(name="TriggerId") String triggerId)
                    throws Exception
    {
        command.cmdDistributionCancel(mcuId, triggerId);
    }
    
    /**
     * cmdBypassSensor(102.27)
     *
     * @param mcuId MCU Indentifier
     * @param sensorId Sensor Indentifier
     * @param isLinkSkip Link Frame send true
     * @param data data stream
     * @throws FMPMcuException, Exception
     */
    @WebMethod(operationName="cmdBypassSensor1")
    public void cmdBypassSensor1(@WebParam(name="McuId") String mcuId, 
            @WebParam(name="ModemId") String modemId,
            @WebParam(name="IsLinkSkip") boolean isLinkSkip,
            @WebParam(name="DataStream") byte[] data)
                    throws Exception
    {
        command.cmdBypassSensor(mcuId, modemId, isLinkSkip, data);
    }
    
    /**
     * cmdBypassSensor(102.27)
     *
     * @param mcuId MCU Indentifier
     * @param sensorId Sensor Indentifier
     * @param isLinkSkip Link Frame send true
     * @param data multi frame data stream
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public void cmdBypassSensor(@WebParam(name="McuId") String mcuId, 
            @WebParam(name="ModemId") String modemId, 
            @WebParam(name="IsLinkSkip") boolean isLinkSkip,
            @WebParam(name="MultiFrameDataStream") ArrayList data)
                    throws Exception
    {
        command.cmdBypassSensor(mcuId, modemId, isLinkSkip, data);
    }
    
    /**
     * cmdGetFFDList(102.38)
     *
     * @param mcuId MCU Indentifier
     * @param parser parser name
     * @param fwversion firmware version
     * @param fwbuild firmware build
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public ffdEntry[] cmdGetFFDList(@WebParam(name="McuId") String mcuId, 
            @WebParam(name="ParserName") String parser,
            @WebParam(name="FwVersion") String fwVersion,
            @WebParam(name="FwBuild") String fwBuild)
                    throws Exception
    {
        return command.cmdGetFFDList(mcuId, parser, fwVersion, fwBuild);
    }
    
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
    @WebMethod
    public long cmdAsynchronousCall(@WebParam(name="McuId") String mcuId, 
            @WebParam(name="MIUType") String miuType,
            @WebParam(name="MIUClassName") String miuClassName,
            @WebParam(name="MIUId") String miuId,
            @WebParam(name="Command") String command,
            @WebParam(name="Option") int option,
            @WebParam(name="Day") int day,
            @WebParam(name="Nice") int nice,
            @WebParam(name="NTry") int ntry,
            @WebParam(name="CommandOIDArgs") String[][] args, 
            @WebParam(name="ServiceType") int serviceType, 
            @WebParam(name="Operator") String operator)
                    throws Exception
    {
        return this.command.cmdAsynchronousCall(mcuId, miuType, miuClassName, 
                miuId, command, option, day, nice, ntry, args, serviceType, operator);
    }
    
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
    @WebMethod
    public trInfoEntry cmdShowTransactionList(@WebParam(name="McuId") String mcuId,
            @WebParam(name="MIUType") String miuType, @WebParam(name="MIUId") String miuId,
            @WebParam(name="ParserName") String parser, @WebParam(name="Version") String version,
            @WebParam(name="Build") String build)
                    throws Exception
    {
        return command.cmdShowTransactionList(mcuId, miuType, miuId, parser, version, build);
    }
    
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
    @WebMethod
    public ResponseMap cmdShowTransactionInfo(@WebParam(name="McuId") String mcuId,
            @WebParam(name="TransactionId") long trId, @WebParam(name="Option") int option)
                    throws Exception
    {
        ResponseMap response = new ResponseMap();
        response.setResponse(command.cmdShowTransactionInfo(mcuId, trId, option));
        return response;
    }
    
    /**
     * (102.43)
     * @param mcuId
     * @param trId
     * @param state  - 0x08 : TR_STATE_TERMINATE : Transaction
                     - 0x10 : TR_STATE_DELETE    : Transaction
     * @throws Exception
     */
    @WebMethod
    public void cmdModifyTransaction(@WebParam(name="McuId") String mcuId, 
            @WebParam(name="TransactionId") long[] trId, @WebParam(name="State") int[] state)
                    throws Exception
    {
        command.cmdModifyTransaction(mcuId, trId, state);
    }
    
    /**
     * (102.44)
     * @param mcuId
     * @param modemId
     * @param count
     * @return EventLog[] - aimir-service-common/EventLog
     * @throws Exception
     */
    @WebMethod
    public EventLog[] cmdGetModemEvent(@WebParam(name="McuId") String mcuId, 
            @WebParam(name="ModemId") String modemId, @WebParam(name="Count") int count)
                    throws Exception
    {
        return command.cmdGetModemEvent(mcuId, modemId, count);
    }
    
    /**
     * (102.44)
     * @param mcuId
     * @param modemId
     * @param count
     * @return BatteryLog - aimir-service-common/BatteryLog
     * @throws Exception
     */
    @WebMethod
    public BatteryLog cmdGetModemBattery(@WebParam(name="McuId") String mcuId,
            @WebParam(name="ModemId") String modemId, @WebParam(name="Count") int count)
                    throws Exception
    {
        return command.cmdGetModemBattery(mcuId, modemId, count);
    }
    
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
    @WebMethod
    public byte[] cmdDigitalInOut(@WebParam(name="McuId") String mcuId, 
            @WebParam(name="ModemId") String modemId, @WebParam(name="Direction") byte direction,
            @WebParam(name="Mask") byte mask, @WebParam(name="Value") byte value)
                    throws Exception
    {
        return command.cmdDigitalInOut(mcuId, modemId, direction, mask, value);
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
     *          IF4ERR_INVALID_PARAM        : 잘못된 Parameter가 전달
     *          IF4ERR_INVALID_ID           : miuID가 관리 목록에 없음
     *          IF4ERR_INVALID_TARGET       : 대상 장비가 AsyncrhronousCall을 지원하지 않음
     *          IF4ERR_INVALID_COMMAND      : 지원하지 않는 Command
     *          IF4ERR_OUT_OF_RANGE     : nDay, nNice, nRetry가 유효값 범위 밖에 값으로 설정
     *          IF4ERR_TRANSACTION_CREATE_FAIL  : Transaction 생성 실패
     *          IF4ERR_INVALID_GROUP        : 존재하지 않는 그룹 이름
     *          IF4ERR_GROUP_DB_OPEN_FAIL       : 그룹 DB의 Open 실패
     */
    @WebMethod
    public long cmdGroupAsyncCall(@WebParam(name="McuId") String mcuId,
            @WebParam(name="GroupKey") int groupKey, @WebParam(name="Command") String command,
            @WebParam(name="nOption") int nOption, @WebParam(name="nDay") int nDay, 
            @WebParam(name="nNice") int nNice, @WebParam(name="nTry") int nTry,
            @WebParam(name="SMIValueList") List<SMIValue> param)
                    throws Exception
    {
        return this.command.cmdGroupAsyncCall(mcuId, groupKey, command, nOption,
                nDay, nNice, nTry, param);
    }
    
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
    @WebMethod
    public int cmdGroupAdd(@WebParam(name="McuId") String mcuId, 
            @WebParam(name="GroupName") String groupName)
                    throws Exception
    {
        return command.cmdGroupAdd(mcuId, groupName);
    }
    
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
    @WebMethod
    public void cmdGroupDelete(@WebParam(name="McuId") String mcuId, 
            @WebParam(name="GroupKey") int groupKey)
                    throws Exception
    {
        command.cmdGroupDelete(mcuId, groupKey);
    }
    
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
    @WebMethod
    public void cmdGroupAddMember(@WebParam(name="McuId") String mcuId,
            @WebParam(name="GroupKey") int groupKey, 
            @WebParam(name="ModemId") String modemId)
                    throws Exception
    {
        command.cmdGroupAddMember(mcuId, groupKey, modemId);
    }
    
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
    @WebMethod
    public void cmdGroupDeleteMember(@WebParam(name="McuId") String mcuId,
            @WebParam(name="GroupKey") int groupKey,
            @WebParam(name="ModemId") String modemId)
                    throws Exception
    {
        command.cmdGroupDeleteMember(mcuId, groupKey, modemId);
    }
    

    /**
     * (102.58) 현재 그룹 정보 전체를 조회한다
     * @param mcuId
     * @throws FMPMcuException
     * @throws Exception
     *    IF4ERR_GROUP_DB_OPEN_FAIL     : 그룹 DB의 Open 실패
     */
    @WebMethod
    public GroupInfo[] cmdGroupInfo(@WebParam(name="McuId") String mcuId)
            throws Exception
    {
        return command.cmdGroupInfo(mcuId);
    }

    /**
     * (102.58) 현재 그룹 정보를 그룹명으로 조회한다
     * @param mcuId
     * @param groupKey
     * @param modemId
     * @throws FMPMcuException
     * @throws Exception
     *    IF4ERR_GROUP_DB_OPEN_FAIL     : 그룹 DB의 Open 실패
     */
    @WebMethod(operationName="cmdGroupInfo1")
    public GroupInfo[] cmdGroupInfo1(@WebParam(name="McuId") String mcuId,
            @WebParam(name="GroupKey") int groupKey)
                    throws Exception
    {
        return command.cmdGroupInfo(mcuId, groupKey);
    }

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
    @WebMethod(operationName="cmdGroupInfo2")
    public GroupInfo[] cmdGroupInfo2(@WebParam(name="McuId") String mcuId, 
            @WebParam(name="ModemId") String modemId, @WebParam(name="bSearchId") boolean bSearchId)
                    throws Exception
    {
        return command.cmdGroupInfo(mcuId, modemId, bSearchId);
    }

    /**
     * cmdGetLoadControlScheme(108.1)
     *
     * @param sensorId sensor id
     * @return 11.1
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public loadControlSchemeEntry[] cmdGetLoadControlScheme(@WebParam(name="McuId") String mcuId,
            @WebParam(name="ModemId") String modemId)
                    throws Exception
    {
        return command.cmdGetLoadControlScheme(mcuId, modemId);
    }
    
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
    @WebMethod
    public void cmdSetLoadControlScheme(@WebParam(name="McuId") String mcuId, 
            @WebParam(name="ModemId") String modemId, @WebParam(name="EntryNo")  int entryNumber,
            @WebParam(name="Command") int command, @WebParam(name="DelayTime") int delayTime,
            @WebParam(name="ScheduleType") int scheduleType, @WebParam(name="StartTime") String startTime,
            @WebParam(name="EndTime") String endTime, @WebParam(name="Weekly") int weekly)
                    throws Exception
    {
        this.command.cmdSetLoadControlScheme(mcuId, modemId, entryNumber,
                command, delayTime, scheduleType, startTime, endTime, weekly);
    }
    
    /**
     * cmdGetLoadLimitProperty(108.3)
     *
     * @param sensorId sensor id
     * @return 11.2
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public loadLimitPropertyEntry cmdGetLoadLimitProperty(@WebParam(name="McuId") String mcuId,
            @WebParam(name="ModemId") String modemId)
                    throws Exception
    {
        return command.cmdGetLoadLimitProperty(mcuId, modemId);
    }
    
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
    @WebMethod
    public void cmdSetLoadLimitProperty(@WebParam(name="McuId") String mcuId,
            @WebParam(name="ModemId") String modemId,
            @WebParam(name="LimitType") int limitType,
            @WebParam(name="Limit") long limit,
            @WebParam(name="IntervalNo") int intervalNumber,
            @WebParam(name="OpenPeriod") int openPeriod)
                    throws Exception
    {
        command.cmdSetLoadLimitProperty(mcuId, modemId, limitType, limit, intervalNumber, openPeriod);
    }
    
    /**
     * cmdGetLoadLimitScheme(108.5)
     *
     * @param sensorId sensor id
     * @return 11.3
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public loadLimitSchemeEntry[] cmdGetLoadLimitScheme(@WebParam(name="McuId") String mcuId,
            @WebParam(name="ModemId") String modemId)
                    throws Exception
    {
        return command.cmdGetLoadLimitScheme(mcuId, modemId);
    }
    
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
    @WebMethod
    public void cmdSetLoadLimitScheme(@WebParam(name="McuId") String mcuId,
            @WebParam(name="ModemId") String modemId,
            @WebParam(name="EntryNo") int entryNumber,
            @WebParam(name="LimitType") int limitType,
            @WebParam(name="Limit") long limit,
            @WebParam(name="IntervalNo") int intervalNumber,
            @WebParam(name="OpenPeriod") int openPeriod,
            @WebParam(name="ScheduleType") int scheduleType,
            @WebParam(name="StartTime") String startTime,
            @WebParam(name="EndTime") String endTime, @WebParam(name="Weekly") int weekly)
                    throws Exception
    {
        command.cmdSetLoadLimitScheme(mcuId, modemId, entryNumber, limitType,
                limit, intervalNumber, openPeriod, scheduleType, startTime,
                endTime, weekly);
    }
    
    /**
     * cmdGetLoadShedSchedule(108.7)
     *
     * @param mcuId
     * @return 11.4
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public loadShedSchemeEntry[] cmdGetLoadShedSchedule(@WebParam(name="McuId") String mcuId)
            throws Exception
    {
        return command.cmdGetLoadShedSchedule(mcuId);
    }
    
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
    @WebMethod
    public void cmdSetLoadShedSchedule(@WebParam(name="McuId") String mcuId,
            @WebParam(name="EntryNo") int entryNumber,
            @WebParam(name="CheckInterval") int checkInterval,
            @WebParam(name="ScheduleType") int scheduleType,
            @WebParam(name="StartTime") String startTime,
            @WebParam(name="EndTime") String endTime, @WebParam(name="Weekly") int weekly)
                    throws Exception
    {
        command.cmdSetLoadShedSchedule(mcuId, entryNumber, checkInterval,
                scheduleType, startTime, endTime, weekly);
    }
    
    /**
     * cmdDeleteLoadControlScheme(108.9)
     *
     * @param mcuId
     * @paran sensorId
     * @param entryNumber
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public void cmdDeleteLoadControlScheme(@WebParam(name="McuId") String mcuId, 
            @WebParam(name="ModemId") String modemId,
            @WebParam(name="EntryNo") int entryNumber)
                    throws Exception
    {
        command.cmdDeleteLoadControlScheme(mcuId, modemId, entryNumber);
    }
    
    /**
     * cmdDeleteLoadLimitScheme(108.10)
     *
     * @param mcuId
     * @paran sensorId
     * @param entryNumber
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public void cmdDeleteLoadLimitScheme(@WebParam(name="McuId") String mcuId,
            @WebParam(name="ModemId") String modemId,
            @WebParam(name="EntryNo") int entryNumber)
                    throws Exception
    {
        command.cmdDeleteLoadLimitScheme(mcuId, modemId, entryNumber);
    }
    
    /**
     * cmdDeleteLoadShedScheme(108.11)
     *
     * @param mcuId
     * @param entryNumber
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public void cmdDeleteLoadShedScheme(@WebParam(name="McuId") String mcuId,
            @WebParam(name="EntryNo") int entryNumber)
                    throws Exception
    {
        command.cmdDeleteLoadShedScheme(mcuId, entryNumber);
    }
    
    /**
     * cmdSetCiuLCD (109.1)
     * @param sensorId
     * @param ledNumber
     * @param pageIdx
     * @param data
     */
    @WebMethod
    public void cmdSetCiuLCD(@WebParam(name="McuId") String mcuId, 
            @WebParam(name="ModemId") String modemId, 
            @WebParam(name="LedNo") int ledNumber,
            @WebParam(name="PageIndex") int pageIdx,
            @WebParam(name="Data") String data)
                    throws Exception
    {
        command.cmdSetCiuLCD(mcuId, modemId, ledNumber, pageIdx, data);
    }
    
    /**
     * cmdACD(105.9)
     *
     * @param sensorId Sensor Indentifier
     * @param onoff on/off
     * @param delayTime on/off delaytime
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public void cmdACD(@WebParam(name="McuId") String mcuId, 
            @WebParam(name="ModemId") String modemId,
            @WebParam(name="OnOff") int onoff,
            @WebParam(name="DelayTime") int delayTime,
            @WebParam(name="RandomTime") int randomTime)
                    throws Exception
    {
        command.cmdACD(mcuId, modemId, onoff, delayTime, randomTime);
    }
    
    /**
     * On-demand meter 104.6.0
     * @param modemId Modem Indentifier
     * @param kmcs kmcEntry list
     * @return result
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public String cmdOnDemandMeter( @WebParam(name="MeterSerialNo") String meterId,
            @WebParam(name="FromDate") String fromDate, @WebParam(name="ToDate") String toDate)
                    throws Exception
    {
        return command.cmdOnDemandMeter(meterId, fromDate, toDate);
    }
    
    /**
     * MCU Scanning
     *
     * @param mcuId MCU Indentifier
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public ResponseMap cmdMcuScanning(@WebParam(name="McuId") String mcuId,
            @WebParam(name="PropertyList") String[] property)
                    throws Exception
    {
        ResponseMap response = new ResponseMap();
        response.setResponse(command.cmdMcuScanning(mcuId, property));
        return response;
    }
    
    @WebMethod
    public String cmdSensorLPLogRecovery(@WebParam(name="MeterSerialNo") String mdsId,
            @WebParam(name="McuId") String dcuNo, @WebParam(name="ModemId") String modemNo,
            @WebParam(name="MeteringValue") double meteringValue, @WebParam(name="LpInterval") int lpInterval,
            @WebParam(name="LpList") int[] lplist)
                    throws Exception
    {
        return command.cmdSensorLPLogRecovery(mdsId, dcuNo, modemNo, 
                meteringValue, lpInterval, lplist);
    }
    
    @WebMethod
    public String doGetModemROM(@WebParam(name="McuId") String mcuId,
            @WebParam(name="ModemId") String modemId,
            @WebParam(name="ModemType") String modemType,
            @WebParam(name="ServiceType") int serviceType,
            @WebParam(name="Operator") String operator)
                    throws Exception
    {
        return command.doGetModemROM(mcuId, modemId, modemType, serviceType, operator);
    }
    
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
    @WebMethod
    public String doGetModemCluster( @WebParam(name="McuId") String mcuId,
            @WebParam(name="ModemId") String modemId,
            @WebParam(name="ModemType") String modemType,
            @WebParam(name="ServiceType") int serviceType, 
            @WebParam(name="Operator") String operator)
                    throws Exception
    {
        return command.doGetModemCluster(mcuId, modemId, modemType, serviceType, operator);
    }
    
    /**
     * (130.1)DR 프로그램 참여 유도
     * @param mcuId
     * @param drLevelEntry
     * @throws FMPMcuException
     * @throws Exception
     */
    @WebMethod
    public void cmdDRAgreement(@WebParam(name="McuId") String mcuId,
            @WebParam(name="DRLevelEntry") drLevelEntry drLevelEntry)
                    throws Exception
    {
        command.cmdDRAgreement(mcuId, drLevelEntry);
    }
    

    /**
     * (130.2)DR 취소 메시지
     * @param mcuId
     * @param deviceId
     * @throws FMPMcuException
     * @throws Exception
     */
    @WebMethod
    public void cmdDRCancel(@WebParam(name="McuId") String mcuId,
            @WebParam(name="DeviceId") String deviceId)
                    throws Exception
    {
        command.cmdDRCancel(mcuId, deviceId);
    }

    /**
     * (130.3)Incentive DR Start
     * @param mcuId
     * @param idrEntry
     * @throws FMPMcuException
     * @throws Exception
     */
    @WebMethod
    public void cmdIDRStart(@WebParam(name="McuId") String mcuId,
            @WebParam(name="idrEntry") idrEntry idrEntry)
                    throws Exception
    {
        command.cmdIDRStart(mcuId, idrEntry);
    }

    /**
     * (130.4)Incentive DR Cancel
     * @param mcuId
     * @param eventId
     * @throws FMPMcuException
     * @throws Exception
     */
    @WebMethod
    public void cmdIDRCancel(@WebParam(name="McuId") String mcuId,
            @WebParam(name="EventId") String eventId)
                    throws Exception
    {
        command.cmdIDRCancel(mcuId, eventId);
    }

    /**
     * (130.5)DR Level Monitoring 장비의 DR Level 조회
     * @param mcuId
     * @param deviceId
     * @return
     * @throws FMPMcuException
     * @throws Exception
     */
    @WebMethod
    public endDeviceEntry cmdGetDRLevel(@WebParam(name="McuId") String mcuId, 
            @WebParam(name="DeviceId") String deviceId)
                    throws Exception
    {
        return command.cmdGetDRLevel(mcuId, deviceId);
    }

    /**
     * (130.6)DR Level Control DR Level 제어
     * @param mcuId
     * @param endDeviceEntry
     * @throws FMPMcuException
     * @throws Exception
     */
    @WebMethod
    public void cmdSetDRLevel(@WebParam(name="McuId") String mcuId,
            @WebParam(name="endDeviceEntry") endDeviceEntry endDeviceEntry)
                    throws Exception
    {
        command.cmdSetDRLevel(mcuId, endDeviceEntry);
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
    @WebMethod
    public void cmdEndDeviceControl(@WebParam(name="McuId") String mcuId, 
            @WebParam(name="ServiceId") String serviceId, 
            @WebParam(name="DeviceId") String deviceId,
            @WebParam(name="EventId") String eventId,
            @WebParam(name="DRLevel") String drLevel)
                    throws Exception
    {
        command.cmdEndDeviceControl(mcuId, serviceId, deviceId, eventId, drLevel);
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
    @WebMethod
    public endDeviceEntry cmdGetDRAsset(@WebParam(name="McuId") String mcuId,
            @WebParam(name="DeviceId") String deviceId)
                    throws Exception
    {
        return command.cmdGetDRAsset(mcuId, deviceId);
    }
    
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
    @WebMethod
    public MeterData cmdOnRecoveryDemandMeter(@WebParam(name="McuId") String mcuId, 
            @WebParam(name="ModemId") String modemId, 
            @WebParam(name="nOption") String nOption,
            @WebParam(name="Offset") int offSet, @WebParam(name="Count") int count)
                    throws Exception
    {
        return command.cmdOnRecoveryDemandMeter(mcuId, modemId, nOption, offSet, count);
    }   
    
    /**
     * Broadcast start
     * @param mcuId
     * @param deviceId
     * @throws FMPMcuException
     * @throws Exception
     */
    @WebMethod
    public void cmdBroadcast(@WebParam(name="McuId") String mcuId,
            @WebParam(name="DeviceId") String deviceId, @WebParam(name="Message") String message) 
                    throws Exception
    {
        command.cmdBroadcast(mcuId, deviceId, message);
    }
    
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
    @WebMethod
    public int cmdValveControl(@WebParam(name="McuId") String mcuId, 
            @WebParam(name="ModemId") String modemId, @WebParam(name="ValveStatus") int valveStatus)
                    throws Exception
    {
        return command.cmdValveControl(mcuId, modemId, valveStatus);
    }

    
    /**
     * @param mcuId
     * @param type
     * @param name
     * @return list[type, name, key]
     * @throws Exception
     */
    @WebMethod
    public ResponseMap cmdGetMeterSecurity(@WebParam(name="McuId") String mcuId,
            @WebParam(name="Type") String type, @WebParam(name="name") String name)
                    throws Exception
    {
        ResponseMap response = new ResponseMap();
        response.setResponse(command.cmdGetMeterSecurity(mcuId, type, name));
        return response;
    }
    
    /**
     * @param mcuId
     * @param type
     * @param name
     * @return list[type, name, key]
     * @throws Exception
     */
    @WebMethod
    public void cmdSetMeterSecurity(@WebParam(name="McuId") String mcuId,
            @WebParam(name="Type") String type, @WebParam(name="name") String name,
            @WebParam(name="key") String key)
                    throws Exception
    {
        command.cmdSetMeterSecurity(mcuId, type, name, key);
    }
    
    /**
     * @param mcuId
     * @param modemId
     * @param mask
     * @param currentPulse
     * @param serialNumber
     * @return
     * @throws Exception
     */
    @WebMethod
    public int cmdSetMeterConfig(@WebParam(name="McuId") String mcuId,
            @WebParam(name="ModemId") String modemId, @WebParam(name="Mask") int mask,
            @WebParam(name="CurrentPulse") int currentPulse, @WebParam(name="SerialNo") String serialNumber)
                    throws Exception
    {
        return command.cmdSetMeterConfig(mcuId, modemId, mask, currentPulse, serialNumber);
    }
    
    /**
     * @param mcuId
     * @param modemId
     * @return
     * @throws Exception
     */
    @WebMethod
    public ResponseMap cmdGetMeterVersion(@WebParam(name="McuId") String mcuId,
            @WebParam(name="ModemId") String modemId)
                    throws Exception
    {
        ResponseMap response = new ResponseMap();
        response.setResponse(command.cmdGetMeterVersion(mcuId, modemId));
        return response;
    }

    /**
     * TOU Calendar 를 설정한다.<br>kskim.
     * @param meterSerial
     * @param touProfileId TouProfile Table ID
     * @return
     * @throws Exception 
     */
    @WebMethod
    public Response cmdSetTOUCalendar(@WebParam(name="MerterSerialNo") String meterSerial,
            @WebParam(name="TOUProfileId") int touProfileId)
                    throws Exception
    {
        return command.cmdSetTOUCalendar(meterSerial, touProfileId);
    }
    
    /**
     * @param sensorId
     * @param mcuId
     * @return
     * @throws Exception
     */
    @WebMethod
    public byte cmdGetEnergyLevel(@WebParam(name="McuId") String mcuId,
            @WebParam(name="ModemId") String modemId)
                    throws Exception
    {
        return command.cmdGetEnergyLevel(mcuId, modemId);
    }
    
    /**
     * @param sensorId
     * @param mcuId
     * @param level
     * @return
     * @throws Exception
     */
    @WebMethod
    public void cmdSetEnergyLevel(@WebParam(name="McuId") String mcuId, 
            @WebParam(name="ModemId") String modemId, @WebParam(name="Level") String level)
                    throws Exception
    {
        command.cmdSetEnergyLevel(mcuId, modemId, level);
    }
    
    /**
     * @param sensorId
     * @param mcuId
     * @param level
     * @param meterSerial
     * @return
     * @throws Exception
     */
    @WebMethod(operationName="cmdSetEnergyLevel1")
    public void cmdSetEnergyLevel1(@WebParam(name="McuId") String mcuId, 
            @WebParam(name="ModemId") String modemId, 
            @WebParam(name="Level") String level, @WebParam(name="MeterSerialNo") String meterSerial)
                    throws Exception
    {
        command.cmdSetEnergyLevel(mcuId, modemId, level, meterSerial);
    }
    
    /**
     * @param sensorId
     * @param mcuId
     * @return
     * @throws Exception
     */
    @WebMethod
    public List cmdGetDRAssetInfo(@WebParam(name="McuId") String mcuId, 
            @WebParam(name="ModemId") String modemId, @WebParam(name="ParserName") String parser)
                    throws Exception
    {
        return command.cmdGetDRAssetInfo(mcuId, modemId, parser);
    }
    
    /**
     * @param sensorId
     * @param mcuId
     * @return
     * @throws Exception
     */
    @WebMethod
    public void cmdSendMessage(@WebParam(name="McuId") String mcuId, 
            @WebParam(name="ModemId") String modemId, @WebParam(name="MessageId") int messageId,
            @WebParam(name="MessageType") int messageType, @WebParam(name="Duration") int duration,
            @WebParam(name="ErrorHandler") int errorHandler, @WebParam(name="PreHandler") int preHandler, 
            @WebParam(name="PostHandler") int postHandler, @WebParam(name="UserData") int userData,
            @WebParam(name="pszData") String pszData )
                    throws Exception
    {
        command.cmdSendMessage(mcuId, modemId, messageId, messageType, duration,
                errorHandler, preHandler, postHandler, userData, pszData);
    }
    
    /**
     * Demand 를 reset한다.<br>
     * 
     * @param meterId
     * @return
     * @throws Exception
     */
    @WebMethod
    public Response cmdDemandReset(@WebParam(name="MeterSerialNo") Integer meterId)
            throws Exception
    {
        return command.cmdDemandReset(meterId);
    }
    
    /**
     * SMS 보내는 기능.
     * @param target
     * @param params
     * @return
     * @throws Exception
     */
    @WebMethod(operationName="cmdSendSMS1")
    public Object cmdSendSMS1(@WebParam(name="Target") Target target,
            @WebParam(name="params") String ... params)
                    throws Exception
    {
        return command.cmdSendSMS(target, params);
    }
    
    @WebMethod
    public Object cmdSendSMS(@WebParam(name="ModemId") String modemSerial,
            @WebParam(name="params") String ... params)
                    throws Exception
    {
        return command.cmdSendSMS(modemSerial, params);
    }

    /**
     * DOTA 명령을 SMS 메시지 로 보내는 기능.
     * @param modemSerial
     * @param filePath
     * @return
     */
    @WebMethod
    public Response cmdSmsFirmwareUpdate(@WebParam(name="ModemId") String modemSerial,
            @WebParam(name="FilePath") String filePath)
                    throws Exception
    {
        return command.cmdSmsFirmwareUpdate(modemSerial, filePath);
    }

    /**
     * Meter Table 에 값을 기록하는 명령어
     * @param meterSerial
     * @param settingStr
     * @param mp
     * @return
     * @throws Exception 
     */
    @WebMethod
    public Response cmdMeterProgram(@WebParam(name="MeterSerialNo") String meterSerial,
            @WebParam(name="settingStr") String settingStr, @WebParam(name="MeterProgram") MeterProgram mp)
                    throws Exception
    {
    	//set value object
		//설정파일 읽기
    	Object valueObject = null;

		if(mp.getKind() == MeterProgramKind.DisplayItemSetting) {
			DisplayItemSettingCSV csv = new DisplayItemSettingCSV(settingStr.getBytes()); //settingStr
			DisplayItemSettingBuilder builder = new DisplayItemSettingBuilder();
			valueObject = builder.build(csv);

		} else {
			VOBuilder<?> builder = null;
			CSV csv = new CSV(settingStr.getBytes()); //settingStr
			
			switch(mp.getKind()){
				case DaySavingTime:
					builder = new SummerTimeBuilder();
					break;
				case TOUCalendar:
					builder = new TOUCalendarBuilder();
					break;
					//TODO : 미터 프로그램 추가 될때마다 추가한다.
				default:
					return null;
			}
			valueObject = builder.build(csv);
		}
		
        return command.cmdMeterProgram(meterSerial, valueObject, mp);
    }

    /**
     * Bypass 모드로 Meter Program 기능 실행.
     * @param meterSerial
     * @param kind
     * @return
     */
    @WebMethod
    public Response cmdBypassMeterProgram(@WebParam(name="MeterSerialNo") String meterSerial,
            @WebParam(name="MeterProgramKind") MeterProgramKind kind)
                    throws Exception
    {
        return command.cmdBypassMeterProgram(meterSerial, kind);
    }
    
    
    /**
     * 펌웨어 롤백 기능.
     * @param model
     * @param sensors
     */
    @WebMethod
    public void cmdMeterFactoryReset(@WebParam(name="McuId") String mcuId,
            @WebParam(name="ModemId") String modemId)
                    throws Exception
    {
        command.cmdMeterFactoryReset(mcuId, modemId);
    }

    /**
     * INSTALL CODI Information Set.
     * @param mcuId
     * @param sensorID
     * @return
     * @throws FMPMcuException
     * @throws Exception
     */
    @WebMethod
    public String cmdSetIHDTable(@WebParam(name="McuId") String mcuId, 
            @WebParam(name="ModemId") String modemId)
                    throws Exception
    {
        return command.cmdSetIHDTable(mcuId, modemId);
    }

    /**
     * INSTALL CODI Information Del.
     * @param mcuId
     * @param sensorID
     * @return
     * @throws FMPMcuException
     * @throws Exception
     */
    @WebMethod
    public String cmdDelIHDTable(@WebParam(name="McuId") String mcuId,
            @WebParam(name="ModemId") String modemId)
                    throws Exception
    {
        return command.cmdDelIHDTable(mcuId, modemId);
    }

    /**
     * Send IHD Data to MCU
     * @param mcuId
     * @param sensorId
     * @param IHDData
     * @throws Exception
     */
    @WebMethod
    public void cmdSendIHDData(@WebParam(name="McuId") String mcuId,
            @WebParam(name="ModemId") String modemId,
            @WebParam(name="IHDData") byte[] IHDData)
                    throws Exception
    {
        command.cmdSendIHDData(mcuId, modemId, IHDData);
    }
    
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
    @WebMethod
    public void cmdUpdateGroup(@WebParam(name="McuId") String mcuId,
            @WebParam(name="GroupType") String groupType,
            @WebParam(name="GroupName") String groupName,
            @WebParam(name="ModemIdList") String[] modemId)
                    throws Exception
    {
        command.cmdUpdateGroup(mcuId, groupType, groupName, modemId);
    }
    
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
    @WebMethod
    public void cmdDeleteGroup(@WebParam(name="McuId") String mcuId,
            @WebParam(name="GroupType") String groupType,
            @WebParam(name="GruopName") String groupName,
            @WebParam(name="ModemIdList") String[] modemId)
                    throws Exception
    {
        command.cmdDeleteGroup(mcuId, groupType, groupName, modemId);
    }
    
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
    @WebMethod
    public List<GroupTypeInfo> cmdGetGroup(@WebParam(name="McuId") String mcuId,
            @WebParam(name="GroupType") String groupType,
            @WebParam(name="GroupName") String groupName,
            @WebParam(name="ModemId") String modemId)
                    throws Exception
    {
        return command.cmdGetGroup(mcuId, groupType, groupName, modemId);
    }
    
    /**
     * FIXME : 바이페스 테스트용
     * method name : cmdBypassTest
     * method Desc : 
     */
    @WebMethod
    public String sendBypassOpenSMS(@WebParam(name="ModemId") String modemSerial)
            throws Exception
    {
        return command.sendBypassOpenSMS(modemSerial);
    }
    
    /**
     * (105.11) 우즈벡용 극동 미터에서 사용하고 있는 OID 중복으로 인한 재정의된 OID Command
     * 
     * @param modemId							
     * @param valueStatus 						: Value Status(On/Off)
     * 
     * @return None
     * 
     * @throws Exception
     *         IF4ERR_INVALID_PARAM                잘못된 인자 전달
     *         IF4ERR_CANCNOT_CONNECT         	   Connection 실패
     *         IF4ERR_CANNOT_GET                   처리 중 오류
     *         IF4ERR_INVALID_TYPE                 극동 미터가 아닐 경우
     */
    @WebMethod
    public void cmdKDValveControl (@WebParam(name="ModemId") String modemId,
    								@WebParam(name="ValueStatus") int valueStatus)
            throws Exception
    {
        command.cmdKDValveControl(modemId, valueStatus);
    }
    
    /**
     * (105.12) 우즈벡용 극동 미터에서 사용하고 있는 OID 중복으로 인한 재정의된 OID Command
     * 
     * @param modemId
     * 
     * @return ResponseMap(
     * 						cp(UNIT 1.3)			: Current Pulse (Little endian)
     * 						serial(STRING 1.11)		: Meter Serial
     * 						astatus(BYTE 1.4)		: Alarm status
     * 						mstatus(BYTE 1.4)		: Meter status)
     * 
     * @throws Exception
     *         IF4ERR_INVALID_PARAM                	잘못된 인자 전달
     *         IF4ERR_CANCNOT_CONNECT          		Connection 실패
     *         IF4ERR_CANNOT_GET                    처리 중 오류
     *         IF4ERR_INVALID_TYPE                  극동 미터가 아닐 경우
     */
    @WebMethod
    public ResponseMap cmdKDGetMeterStatus (@WebParam(name="ModemId") String modemId)
            throws Exception
    {
    	ResponseMap response = new ResponseMap();
        response.setResponse(command.cmdKDGetMeterStatus(modemId));
        return response;
    }
    
    /**
     * (105.13) 우즈벡용 극동 미터에서 사용하고 있는 OID 중복으로 인한 재정의된 OID Command
     * 
     * @param modemId
     * 
     * @return ResponseMap(
     * 						date(STRING 1.11)		: Test date & accept
     * 						hwver(STRING 1.11)		: HW Version
     * 						swver(STRING 1.11)		: SW Version)
     * @throws Exception
     *         IF4ERR_INVALID_PARAM               	 잘못된 인자 전달
     *         IF4ERR_CANCNOT_CONNECT          		Connection 실패
     *         IF4ERR_CANNOT_GET                    처리 중 오류
     *         IF4ERR_INVALID_TYPE                  극동 미터가 아닐 경우
     */
    @WebMethod
    public ResponseMap cmdKDGetMeterVersion (@WebParam(name="ModemId") String modemId)
            throws Exception
    {
    	ResponseMap response = new ResponseMap();
        response.setResponse(command.cmdKDGetMeterVersion(modemId));
        return response;
    }
    
    /**
     * (105.14) 우즈벡용 극동 미터에서 사용하고 있는 OID 중복으로 인한 재정의된 OID Command
     * 
     * @param modemId
     * @param mask									mask가 0x00일 경우 Meter Serial을 사용하지 않는다.
     * @param cp									Meter Pulse(Little endia)
     * @param meterId 								Meter Serial(Optional) : mask가 0x00이 아닐 경우에는 반드시 전달 되어야 한다.
     * 
     * @return None
     * 
     * @throws Exception
     *         IF4ERR_INVALID_PARAM               	잘못된 인자 전달
     *         IF4ERR_CANCNOT_CONNECT          		Connection 실패
     *         IF4ERR_CANNOT_GET                    처리 중 오류
     *         IF4ERR_INVALID_TYPE                  극동 미터가 아닐 경우
     */
    @WebMethod
    public void cmdKDSetMeterConfig(@WebParam(name="ModemId") String modemId,
    								@WebParam(name="Mask") byte mask,
    								@WebParam(name="CP") int cp,
    								@WebParam(name="MeterSerialNo") String meterId)
            throws Exception
    {
        command.cmdKDSetMeterConfig(modemId, mask, cp, meterId);
    }
    
    @WebMethod
    public ResponseMap relayValveOn(@WebParam(name="McuId") String mcuId, 
            @WebParam(name="MeterId") String meterId) throws Exception
    {
        TransactionStatus txstatus = null;
        Class clazz = null;
        try {
            txstatus = txmanager.getTransaction(null);
            Meter meter = meterDao.get(meterId);
            clazz = Class.forName(meter.getModel().getDeviceConfig().getSaverName());
            txmanager.commit(txstatus);
        }
        catch (Exception e) {
            if (txstatus != null) txmanager.rollback(txstatus);
            throw e;
        }
        AbstractMDSaver saver = (AbstractMDSaver)DataUtil.getBean(clazz);
        
        ResponseMap map = new ResponseMap();
        Map<String, String> response = new HashMap<String, String>();
        response.put("Response", saver.relayValveOn(mcuId, meterId));
        map.setResponse(response);
        return map;
    }

    @WebMethod
    public ResponseMap relayValveOff(@WebParam(name="McuId") String mcuId,
            @WebParam(name="MeterId") String meterId) throws Exception 
    {
        TransactionStatus txstatus = null;
        Class clazz = null;
        try {
            txstatus = txmanager.getTransaction(null);
            Meter meter = meterDao.get(meterId);
            clazz = Class.forName(meter.getModel().getDeviceConfig().getSaverName());
            txmanager.commit(txstatus);
        }
        catch (Exception e) {
            if (txstatus != null) txmanager.rollback(txstatus);
            throw e;
        }
        AbstractMDSaver saver = (AbstractMDSaver)DataUtil.getBean(clazz);
        
        ResponseMap map = new ResponseMap();
        Map<String, String> response = new HashMap<String, String>();
        response.put("Response", saver.relayValveOff(mcuId, meterId));
        map.setResponse(response);
        return map;
    }

    @WebMethod
    public ResponseMap relayValveStatus(@WebParam(name="McuId") String mcuId,
            @WebParam(name="MeterId") String meterId) throws Exception
    {
        log.info("mcuId[" + mcuId + "] meterId[" + meterId + "]");
        TransactionStatus txstatus = null;
        Class clazz = null;
        try {
            txstatus = txmanager.getTransaction(null);
            Meter meter = meterDao.get(meterId);
            clazz = Class.forName(meter.getModel().getDeviceConfig().getSaverName());
            txmanager.commit(txstatus);
        }
        catch (Exception e) {
            if (txstatus != null) txmanager.rollback(txstatus);
            throw e;
        }
        AbstractMDSaver saver = (AbstractMDSaver)DataUtil.getBean(clazz);
        
        ResponseMap map = new ResponseMap();
        Map<String, String> response = new HashMap<String, String>();
        response.put("Response", saver.relayValveStatus(mcuId, meterId));
        map.setResponse(response);
        return map;
    }

    @WebMethod
    public ResponseMap syncTime(@WebParam(name="McuId") String mcuId,
            @WebParam(name="MeterId") String meterId) throws Exception
    {
        TransactionStatus txstatus = null;
        Class clazz = null;
        try {
            txstatus = txmanager.getTransaction(null);
            Meter meter = null;
            if(meterId.contains(",")) {
               	String[] arrayIds = meterId.split(",");
            	meter = meterDao.get(arrayIds[0]);
            }else {
            	meter = meterDao.get(meterId);
           	}
            
            clazz = Class.forName(meter.getModel().getDeviceConfig().getSaverName());
            txmanager.commit(txstatus);
        }
        catch (Exception e) {
            if (txstatus != null) txmanager.rollback(txstatus);
            throw e;
        }
        AbstractMDSaver saver = (AbstractMDSaver)DataUtil.getBean(clazz);
        
        log.info("[syncTime] mcuId =  " + mcuId + ", meterId = " + meterId);
        log.info("[syncTime] saver =  " + saver.getClass().getName());
        ResponseMap map = new ResponseMap();
        Map<String, String> response = new HashMap<String, String>();
        response.put("Response", saver.syncTime(mcuId, meterId));
        map.setResponse(response);
        return map;
    }

    @WebMethod
    public ResponseMap relayValveActivate(@WebParam(name="McuId") String mcuId,
            @WebParam(name="MeterId") String meterId) throws Exception
    {
        TransactionStatus txstatus = null;
        Class clazz = null;
        try {
            txstatus = txmanager.getTransaction(null);
            Meter meter = meterDao.get(meterId);
            clazz = Class.forName(meter.getModel().getDeviceConfig().getSaverName());
            txmanager.commit(txstatus);
        }
        catch (Exception e) {
            if (txstatus != null) txmanager.rollback(txstatus);
            throw e;
        }
        AbstractMDSaver saver = (AbstractMDSaver)DataUtil.getBean(clazz);
        
        ResponseMap map = new ResponseMap();
        Map<String, String> response = new HashMap<String, String>();
        response.put("Response", saver.relayValveActivate(mcuId, meterId));
        map.setResponse(response);
        return map;
    }

    @WebMethod
    public ResponseMap relayValveDeactivate(@WebParam(name="McuId") String mcuId,
            @WebParam(name="MeterId") String meterId) throws Exception
    {
        TransactionStatus txstatus = null;
        Class clazz = null;
        try {
            txstatus = txmanager.getTransaction(null);
            Meter meter = meterDao.get(meterId);
            clazz = Class.forName(meter.getModel().getDeviceConfig().getSaverName());
            txmanager.commit(txstatus);
        }
        catch (Exception e) {
            if (txstatus != null) txmanager.rollback(txstatus);
            throw e;
        }
        AbstractMDSaver saver = (AbstractMDSaver)DataUtil.getBean(clazz);
        
        ResponseMap map = new ResponseMap();
        Map<String, String> response = new HashMap<String, String>();
        response.put("Response", saver.relayValveDeactivate(mcuId, meterId));
        map.setResponse(response);
        return map;
    }
    
    /**
     * GD 100.2.1,Upload Metering Data
     * @param meterSerial 							Meter Serial
     * @throws Exception
     *         IF4ERR_INVALID_PARAM               	잘못된 인자 전달
     *         IF4ERR_CANCNOT_CONNECT          		Connection 실패
     *         IF4ERR_CANNOT_GET                    처리 중 오류
     */
    @WebMethod
    public void cmdUploadMeteringData(@WebParam(name="meterSerial") String meterSerial) throws Exception{
    	command.cmdUploadMeteringData(meterSerial);
    }
    
    /**
     * On-demand meter NG_110.2.2
     *
     * @param mcuId MCU Indentifier
     * @param meterId Sensor Indentifier
     * @return energy meter data
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public MeterData cmdOnDemandByMeter(@WebParam(name="McuId") String mcuId,
            @WebParam(name="MeterSerialNo") String meterId,
            @WebParam(name="ModemId") String modemId,
            @WebParam(name="nOption") String nOption,
            @WebParam(name="FromDate") String fromDate, 
            @WebParam(name="ToDate") String toDate)
                    throws Exception
    {
        log.info("mcuId[" + mcuId + "] meterId[" + meterId+"] modemId[" + modemId+"] nOption[" + nOption + "] fromDate[" + fromDate + "] toDate[" + toDate +"]");
        try {
            MeterData md = command.cmdOnDemandByMeter(mcuId, meterId, modemId, nOption, fromDate, toDate);
            return md;
        }
        catch (Exception e) {
            throw e;
        }
    }
    
    
    /**
     * NG 110.1.1,Upload metering by modem
     * @param mcuId 							mcuId
     * @param option
     * @param offset
     * @param count
     * @param modemId list
     * @throws Exception
     *         IF4ERR_INVALID_PARAM               	잘못된 인자 전달
     *         IF4ERR_CANCNOT_CONNECT          		Connection 실패
     *         IF4ERR_CANNOT_GET                    처리 중 오류
     */
    @WebMethod
    public void cmdMeteringByModem(
    		@WebParam(name="mcuId") String mcuId, 
    		@WebParam(name="nOption") int nOption, 
    		@WebParam(name="offset") int offset, 
    		@WebParam(name="count") int count, 
    		@WebParam(name="modemIds") String[] modemId)
    		throws Exception{
    	command.cmdMeteringByModem(mcuId, nOption, offset, count, modemId);
    }

    /**
     * Get Meter
     *
     * @param mcuId MCU Indentifier
     * @return energy meter data list
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public meterDataEntry2[] cmdGetMeterList(
    		@WebParam(name="McuId") String mcuId, 
    		@WebParam(name="nOption") int nOption, 
    		@WebParam(name="MeterId") String[] meterId) 
    		throws Exception{
        try {
        	meterDataEntry2[] entry = command.cmdGetMeterList(mcuId, nOption, meterId);
            return entry;
        }
        catch (Exception e) {
            throw e;
        }
    	
    }
    
    /**
     * Metering All
     *
     * @param mcuId MCU Indentifier
     * @throws FMPMcuException, Exception
     */
    @WebMethod(operationName="cmdMeteringAllByOption")
    public void cmdMeteringAllByOption(
    		@WebParam(name="mcuId") String mcuId, 
    		@WebParam(name="nOption") int nOption, 
    		@WebParam(name="offset") int offset, 
    		@WebParam(name="count") int count)
            throws Exception{
    	command.cmdMeteringAll(mcuId, nOption, offset, count);
    }
    
    /**
     * Metering Recovery
     *
     * @param mcuId MCU Indentifier
     * @throws FMPMcuException, Exception
     */
    @WebMethod(operationName="cmdRecoveryByOption")
    public void cmdRecoveryByOption(
    		@WebParam(name="mcuId") String mcuId, 
    		@WebParam(name="nOption") int nOption, 
    		@WebParam(name="offset") int offset, 
    		@WebParam(name="count") int count)
        throws Exception{
    	command.cmdRecovery(mcuId, nOption, offset, count);
    }

    /**
     * Assemble Test
     *
     * @param mcuId MCU Indentifier
     * @throws FMPMcuException, Exception
     */
    @WebMethod(operationName="cmdAssembleTestStart")
    public void cmdAssembleTestStart(
    		@WebParam(name="McuId") String mcuId)
        throws Exception{
    	command.cmdAssembleTestStart(mcuId);
    }
    
    @WebMethod(operationName="icmpPing")
    public String icmpPing (
		@WebParam(name = "Commands", targetNamespace = "")
    	List<String> commands
    ) throws Exception {
    	return command.icmpPing(commands);
    }
    
    @WebMethod(operationName="traceroute")
    public String traceroute (
		@WebParam(name = "Commands", targetNamespace = "")
    	List<String> commands
    ) throws Exception {
    	return command.traceroute(commands);
    }
    
    @WebMethod(operationName="coapPing")
    public String coapPing(
            @WebParam(name = "IpAddress", targetNamespace = "")
            String ipAddress,
            @WebParam(name = "Device", targetNamespace = "")
            String device,
            @WebParam(name = "Type", targetNamespace = "")
            String type
        ) throws Exception {
        return command.coapPing(ipAddress, device, type);
    }
    
    @WebMethod(operationName="coapGetInfo")
    public ResponseMap coapGetInfo(

            @WebParam(name = "Ipv4", targetNamespace = "")
            java.lang.String ipv4,
            @WebParam(name = "Ipv6", targetNamespace = "")
            java.lang.String ipv6,
            @WebParam(name = "Type", targetNamespace = "")
            java.lang.String type
        ) throws Exception {
    	ResponseMap response = new ResponseMap();
    	response.setResponse(command.coapGetInfo(ipv4,ipv6,type));
    	return response;
    }
    
    @WebMethod(operationName="modemReset")
    public ResponseMap modemReset(

            @WebParam(name = "Ipv4", targetNamespace = "")
            java.lang.String ipv4,
            @WebParam(name = "Ipv6", targetNamespace = "")
            java.lang.String ipv6,
            @WebParam(name = "Type", targetNamespace = "")
            java.lang.String type
        ) throws Exception {
    	ResponseMap response = new ResponseMap();
    	response.setResponse(command.modemReset(ipv4,ipv6,type));
        return response;
    }
    
    @WebMethod(operationName="coapBrowser")
    public ResponseMap coapBrowser(
            @WebParam(name = "Ipv4", targetNamespace = "")
            java.lang.String ipv4,
            @WebParam(name = "Ipv6", targetNamespace = "")
            java.lang.String ipv6,
            @WebParam(name = "Uri", targetNamespace = "")
            java.lang.String uri,
            @WebParam(name = "Query", targetNamespace = "")
            java.lang.String query,
            @WebParam(name = "Config", targetNamespace = "")
            java.lang.String config,
            @WebParam(name = "Type", targetNamespace = "")
            java.lang.String type
        ) throws Exception {
    	ResponseMap response = new ResponseMap();
    	response.setResponse(command.coapBrowser(ipv4,ipv6,uri,query,config,type));
        return response;
    }

    /**
     * NI Protocol - Command Meter Baud Rate [get/set]
     * @param mdsId
     * @param requestType
     * @param rateValue
     * @return
     * @throws Exception
     */
    @WebMethod
    public ResponseMap cmdMeterBaudRate(@WebParam(name="MdsId") String mdsId,
                                        @WebParam(name="RequestType") String requestType,
                                        @WebParam(name="RateValue") int rateValue) throws Exception
    {
        log.info("mdsId[" + mdsId + "] requestType[" + requestType + "]");


        ResponseMap response = new ResponseMap();
        response.setResponse(command.cmdMeterBaudRate(mdsId,requestType,rateValue));
        return response;
    }
    
    /**
     * NI Protocol - Real Time Metering [set]
     * @param mdsId
     * @param interval
     * @param duration
     * @return
     * @throws Exception
     */
    @WebMethod
    public ResponseMap cmdRealTimeMetering(@WebParam(name="MdsId") String mdsId,
                                        @WebParam(name="Interval") int interval,
                                        @WebParam(name="Duration") int duration) throws Exception
    {
        log.info("mdsId[" + mdsId + "] interval[" + interval + "]" + "] duration[" + duration + "]");
        ResponseMap response = new ResponseMap();
        response.setResponse(command.cmdRealTimeMetering(mdsId, interval, duration));
        return response;
    }
    
    /**
     * NI Protocol - Command Modem Event Log [get only]
     * @param mdsId
     * @param logCount
     * @return
     * @throws Exception
     */
    @WebMethod
    public ResponseMap getModemEventLog(@WebParam(name="MdsId") String mdsId,
                                        @WebParam(name="LogCount") int logCount,
                                        @WebParam(name="LogOffset") int logOffset) throws Exception
    {
        log.info("mdsId[" + mdsId + "] log count [" + logCount + "] log offset[" + logOffset + "]");


        ResponseMap response = new ResponseMap();
        // UPDATE START SP-681
//        response.setResponse(command.getModemEventLog(mdsId,logCount));
        response.setResponse(command.getModemEventLog(mdsId,logCount,logOffset));
        // UPDATE END SP-681
        return response;
    }

    @WebMethod
	public ResponseMap setCloneOnOff(@WebParam(name = "ModemId") String modemId, @WebParam(name = "Count") int count) throws Exception {
		log.info("modemId[" + modemId + "] count [" + count + "]");

		ResponseMap response = new ResponseMap();
//		response.setResponse(command.getModemEventLog(modemId, count));
		response.setResponse(command.setCloneOnOff(modemId, count));
		return response;
	}

	// INSERT START SP-681
    @WebMethod
	public ResponseMap setCloneOnOffWithTarget(@WebParam(name = "ModemId") String modemId, 
			@WebParam(name = "CloneCode") String cloneCode,
			@WebParam(name = "Count") int count,
			@WebParam(name = "Version") String version,
			@WebParam(name = "EuiCount") int euiCount,
			@WebParam(name="EuiList") List<String> euiList
			) throws Exception {
		log.info("modemId[" + modemId + "] count[" + count + "] version["+ version + "] euiCount[" + euiCount + "]");

		ResponseMap response = new ResponseMap();
		response.setResponse(command.setCloneOnOffWithTarget(modemId, cloneCode, count, version, euiCount, euiList));
		return response;
	}
	// INSERT END SP-681    
    
    /**
     * Command External Command
     * HD, 200.1.1, pre-defined protocol message for external project
     *
     * @return ResponseMap
     * @throws Exception
     */
    @WebMethod
    public ResponseMap cmdExtCommand(@WebParam(name="McuId") String mcuId,
                                        @WebParam(name="GeneralStream") byte[] generalStream) throws Exception
    {
        log.info("mcuId[" + mcuId + "] generalStream[" + generalStream.toString() + "]");


        ResponseMap response = new ResponseMap();
        response.setResponse(command.cmdExtCommand(mcuId,generalStream));
        return response;
    }
    
    /**
     *  Meter FWVersion - Null Bypass
     * @param meterId
     * @return
     * @throws Exception
     */
    @WebMethod
    public ResponseMap cmdGetMeterFWVersion(
    		@WebParam(name="MeterId") String meterId) throws Exception
    {
    	ResponseMap response = new ResponseMap();
    	response.setResponse(command.cmdGetMeterFWVersion(meterId));
    	return response;
    }
    
    /**
     *  Meter Key - Null Bypass
     * @param meterId
     * @return
     * @throws Exception
     */
    @WebMethod
    public ResponseMap cmdSORIAGetMeterKey(
    		@WebParam(name="MeterId") String meterId) throws Exception
    {
    	ResponseMap response = new ResponseMap();
    	response.setResponse(command.cmdSORIAGetMeterKey(meterId));
    	return response;
    }
        
    /**
     * Multi Device F/W OTA
     * @param otaTargetType
     * @param deviceList
     * @param take_over
     * @param useNullBypass
     * @param firmwareId
     * @return
     * @throws Exception
     */
    @WebMethod
    public ResponseMap cmdMultiFirmwareOTA(
    		@WebParam(name="OTATargetType") String otaTargetType,    		
    		@WebParam(name="DeviceList") List<String> deviceList,
    		@WebParam(name="Take_over") String take_over,
    		@WebParam(name="UseNullBypass") boolean useNullBypass,
    		@WebParam(name="FirmwareId") String firmwareId,
    		@WebParam(name="OptVersion") String optVersion,
    		@WebParam(name="OptModel") String optModel,
    		@WebParam(name="OptTime") String optTime    		
    		) throws Exception
    {
        ResponseMap response = new ResponseMap();
        // UPDATE START SP-681
//        response.setResponse(command.cmdMultiFirmwareOTA(OTATargetType.valueOf(otaTargetType), deviceList, take_over, useNullBypass, firmwareId));
        response.setResponse(command.cmdMultiFirmwareOTA(
        		OTATargetType.valueOf(otaTargetType), 
        		deviceList, 
        		take_over, 
        		useNullBypass, 
        		firmwareId,
        		optVersion,
        		optModel,
        		optTime));
        // UPDATE END SP-681
        return response;
    }
    
    // INSERT START SP-681
    @WebMethod
    public ResponseMap cmdReqNodeUpgrade(
		@WebParam(name = "McuId") String mcuId,    		
        @WebParam(name = "UpgradeType") int upgradeType,
        @WebParam(name = "Control") int control,
		@WebParam(name = "ImageKey") String imageKey,    		
		@WebParam(name = "ImageUrl") String imageUrl,    		
		@WebParam(name = "CheckSum") String checkSum,    		
        @WebParam(name = "FilterValue") java.util.List<java.lang.String> filterValue
    ) throws Exception
    {
        ResponseMap response = new ResponseMap();
        
        response.setResponse(command.cmdReqNodeUpgrade(
        		mcuId, 
        		upgradeType, 
        		control, 
        		imageKey, 
        		imageUrl, 
        		checkSum, 
        		filterValue));
        
        return response;
    }
    
    @WebMethod
    public ResponseMap cmdReqImagePropagate(
		@WebParam(name = "McuId") String mcuId,    		
        @WebParam(name = "UpgradeType") int upgradeType,
        @WebParam(name = "Control") int control,
		@WebParam(name = "ImageKey") String imageKey,    		
		@WebParam(name = "ImageUrl") String imageUrl,    		
		@WebParam(name = "CheckSum") String checkSum,    		
		@WebParam(name = "ImageVersion") String imageVersion,    		
		@WebParam(name = "TargetModel") String targetModel,    		
        @WebParam(name = "FilterValue") java.util.List<java.lang.String> filterValue
    ) throws Exception
    {
        ResponseMap response = new ResponseMap();
        
        response.setResponse(command.cmdReqImagePropagate(
        		mcuId, 
        		upgradeType, 
        		control, 
        		imageKey, 
        		imageUrl, 
        		checkSum, 
        		imageVersion, 
        		targetModel, 
        		filterValue));
        
        return response;
    }

    @WebMethod
    public ResponseMap cmdGetImagePropagateInfo(
		@WebParam(name = "McuId") String mcuId,    		
        @WebParam(name = "UpgradeType") int upgradeType
    ) throws Exception
    {
        ResponseMap response = new ResponseMap();
        
        response.setResponse(command.cmdGetImagePropagateInfo(
        		mcuId, 
        		upgradeType));
        
        return response;
    }    
    // INSERT END SP-681    
    
    /**
     * Get Modem
     *
     * @param mcuId MCU Indentifier
     * @return modem data list
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public modemG3Entry[] cmdGetManagedG3ModemList(
    		@WebParam(name="McuId") String mcuId, 
    		@WebParam(name="nOption") int nOption, 
    		@WebParam(name="ModemId") String[] modemId) 
    		throws Exception{
        try {
        	return command.cmdGetManagedG3ModemList(mcuId, nOption, modemId);
        }
        catch (Exception e) {
            throw e;
        }
    }
    
    /**
     * MCU Get Log
     *
     * @param mcuId MCU Indentifier
     * @param count
     * @return logs
     * @throws Exception
     */
    @WebMethod
    public String cmdMcuGetLog(@WebParam(name="McuId") String mcuId,
            @WebParam(name="Count") int count)
                    throws Exception
    {
        return command.cmdMcuGetLog(mcuId, count);
    }
    
    /**
     * MCU Get Schedule
     *
     * @param mcuId MCU ID
     * @param name Schedule Name
     * @return schdule list
     * @throws Exception
     */
    @WebMethod
    public ResponseMap cmdMcuGetSchedule(@WebParam(name="McuId") String mcuId, 
    		@WebParam(name="Name") String name )
        throws Exception
    {
        ResponseMap response = new ResponseMap();
        response.setResponse(command.cmdMcuGetSchedule(mcuId, name));
        return response;
	}
    
	/**
	 * MCU Set Schedule 
	 *
	 * @param mcuId MCU ID
	 * @param args Schedule settings
	 * @throws  Exception
	 */
    @WebMethod
   	public void cmdMcuSetSchedule(@WebParam(name="McuId") String mcuId, 
   			@WebParam(name="ScheduleList") String[][] args) 
    	throws Exception
	{
    	command.cmdMcuSetSchedule(mcuId, args);;
	}
    
    /**
     * MCU Get Property 
     *
     * @param mcuId MCU ID
     * @param name Key Name (network.retry.default)
     * @return mcu retry interval time
     * @throws Exception
     */
    @WebMethod
    public ResponseMap cmdMcuGetProperty(@WebParam(name="McuId") String mcuId, 
    		@WebParam(name="Name") String name )
        throws Exception
    {
        ResponseMap response = new ResponseMap();
        response.setResponse(command.cmdMcuGetProperty(mcuId, name));
        return response;
	}
    
    /**
	 * MCU Set Property 
	 *
	 * @param mcuId MCU ID
	 * @param key  name of property
	 * @param keyValue  value of key which was mapped with key array
	 * @throws  Exception
	 */
    @WebMethod
   	public ResponseMap cmdMcuSetProperty(@WebParam(name="McuId") String mcuId, 
   			@WebParam(name="Key") String[] key,
   			@WebParam(name="KeyValue") String[] keyValue) 
    	throws Exception
	{
    	ResponseMap response = new ResponseMap();
    	response.setResponse(command.cmdMcuSetProperty(mcuId, key, keyValue));
    	return response;
	}
    
    /**
     * MCU StdGet
     *
     * @param mcuId MCU ID
     * @param name Key Name (network.retry.default)
     * @return mcu retry interval time
     * @throws Exception
     */
    @WebMethod
    public ResponseMap cmdMcuStdGet(@WebParam(name="McuId") String mcuId, 
    		@WebParam(name="Name") String name )
        throws Exception
    {
        ResponseMap response = new ResponseMap();
        response.setResponse(command.cmdMcuStdGet(mcuId, name));
        return response;
	}
    
    /**
	 * MCU StdSet
	 *
	 * @param mcuId MCU ID
	 * @param key  name of property
	 * @param keyValue  value of key which was mapped with key array
	 * @throws  Exception
	 */
    @WebMethod
   	public ResponseMap cmdMcuStdSet(@WebParam(name="McuId") String mcuId, 
   			@WebParam(name="Key") String[] key,
   			@WebParam(name="KeyValue") String[] keyValue) 
    	throws Exception
	{
    	ResponseMap response = new ResponseMap();
    	response.setResponse(command.cmdMcuSetProperty(mcuId, key, keyValue));
    	return response;
	}
    
    @WebMethod
    public ResponseMap cmdGetMcuNMSInformation(@WebParam(name="McuId") String mcuId)
        throws Exception
    {
        ResponseMap response = new ResponseMap();
        response.setResponse(command.cmdGetMcuNMSInformation(mcuId));
        return response;
	}
    
    /**
     *  DLMS Meter Parameter Get
     * @param modemId
     * @param param
     * 
     * @return
     * @throws Exception
     */
    @WebMethod
    public ResponseMap cmdMeterParamGet(
    		@WebParam(name="ModemId") String modemId,
    		@WebParam(name="Param") String param) throws Exception
    {
        ResponseMap response = new ResponseMap();
        response.setResponse(command.cmdMeterParamGet(modemId, param));
        return response;
    }
    
    /**
     *  DLMS Meter Parameter Set
     * @param modemId
     * @param param
     * 
     * @return
     * @throws Exception
     */
    @WebMethod
    public ResponseMap cmdMeterParamSet(
    		@WebParam(name="ModemId") String modemId,
    		@WebParam(name="Param") String param) throws Exception
    {
        ResponseMap response = new ResponseMap();
        response.setResponse(command.cmdMeterParamSet(modemId, param));
        return response;
    }
   
    /**
     *  DLMS Meter Parameter Action
     * @param modemId
     * @param param
     * 
     * @return
     * @throws Exception
     */
    @WebMethod
    public ResponseMap cmdMeterParamAct(
    		@WebParam(name="ModemId") String modemId,
    		@WebParam(name="Param") String param) throws Exception
    {
        ResponseMap response = new ResponseMap();
        response.setResponse(command.cmdMeterParamAct(modemId, param));
        return response;
    }
   
    // INSERT START SP-179
    @WebMethod(operationName="cmdGetMeteringData")
    public MeterData cmdGetMeteringData(@WebParam(name="McuId") String mcuId,
            @WebParam(name="MeterSerialNo") String meterId,
            @WebParam(name="ModemId") String modemId,
            @WebParam(name="nOption") String nOption,
            @WebParam(name="FromDate") String fromDate, 
            @WebParam(name="ToDate") String toDate,
            @WebParam(name="ModemArray") String[] modemArray)
                    throws Exception
    {
        log.info("mcuId[" + mcuId + "] meterId[" + meterId+"] modemId[" + modemId+"] nOption[" + nOption + "] fromDate[" + fromDate + "] toDate[" + toDate +"]");
        try {
            MeterData md = command.cmdGetMeteringData(mcuId, meterId, modemId, nOption, fromDate, toDate, modemArray);
            return md;
        }
        catch (Exception e) {
            throw e;
        }
    }

    @WebMethod(operationName="cmdGetROMRead")
    public MeterData cmdGetROMRead(@WebParam(name="McuId") String mcuId,
            @WebParam(name="MeterSerialNo") String meterId,
            @WebParam(name="ModemId") String modemId,
            @WebParam(name="nOption") String nOption,
            @WebParam(name="FromDate") String fromDate, 
            @WebParam(name="ToDate") String toDate)
                    throws Exception
    {
        log.info("mcuId[" + mcuId + "] meterId[" + meterId+"] modemId[" + modemId+"] nOption[" + nOption + "] fromDate[" + fromDate + "] toDate[" + toDate +"]");
        try {
            MeterData md = command.cmdGetROMRead(mcuId, meterId, modemId, nOption, fromDate, toDate);
            return md;
        }
        catch (Exception e) {
            throw e;
        }
    }
    
    @WebMethod
    public ResponseMap cmdSnmpTrapOnOff(
    		@WebParam(name="ModemId") String modemId,
    		@WebParam(name="RequestType") String requestType,
    		@WebParam(name="Param") String param) throws Exception
    {
        ResponseMap response = new ResponseMap();
        response.setResponse(command.cmdSnmpTrapOnOff(modemId,requestType,param));
        return response;
    }
    
    @WebMethod
    public ResponseMap cmdWatchdogTest(
    		@WebParam(name="ModemId") String modemId) throws Exception
    {
    	 ResponseMap response = new ResponseMap();
         response.setResponse(command.cmdWatchdogTest(modemId));
         return response;
    }
    
    
    @WebMethod
    public void cmdAuthSPModem(@WebParam(name="SerialNo") String serialNo,
            @WebParam(name="Status") int status) throws Exception
    {
        command.cmdAuthSPModem(serialNo, status);
    }

    @WebMethod(operationName="cmdSendEventByFep")
    public String cmdSendEventByFep(
            @WebParam(name="LogId") long logId,
            @WebParam(name="EventStatus") String eventStatusName) throws Exception
    {
        return command.cmdSendEventByFep(logId, eventStatusName);
    }
    
    /**
     * NI Protocol - Command APN [get]
     * @param mdsId
     * @param rateValue
     * @return
     * @throws Exception
     */
    @WebMethod
    public ResponseMap cmdGetApn(@WebParam(name="MdsId") String mdsId ) throws Exception
    {
        log.info("mdsId[" + mdsId + "] requestType[GET]");


        ResponseMap response = new ResponseMap();
        response.setResponse(command.cmdGetApn(mdsId));
        return response;
    }

    @WebMethod
    public ResponseMap cmdMeteringInterval(@WebParam(name="ModemId") String modemId,
                                        @WebParam(name="RequestType") String requestType,
                                        @WebParam(name="Interval") int interval) throws Exception
    {
        log.info("mdsId[" + modemId + "] requestType[" + requestType + "]");

        ResponseMap response = new ResponseMap();
        response.setResponse(command.cmdMeteringInterval(modemId,requestType,interval));
        return response;
    }

    @WebMethod
    public ResponseMap cmdRetryCount(@WebParam(name="ModemId") String modemId,
                                           @WebParam(name="RequestType") String requestType,
                                           @WebParam(name="Retry") int retry) throws Exception
    {
        log.info("mdsId[" + modemId + "] requestType[" + requestType + "]");

        ResponseMap response = new ResponseMap();
        response.setResponse(command.cmdRetryCount(modemId,requestType,retry));
        return response;
    }
    
    /**
     *  DLMS Get TamperingLog
     * @param modemId
     * @param param
     * 
     * @return
     * @throws Exception
     */
    @WebMethod
    public ResponseMap cmdGetTamperingLog(
    		@WebParam(name="ModemId") String modemId,
    		@WebParam(name="FromDate") String fromDate,
    		@WebParam(name="ToDate") String toDate ) throws Exception
    {
        ResponseMap response = new ResponseMap();
        response.setResponse(command.cmdGetTamperingLog(modemId, fromDate, toDate ));
        return response;
    }
    
    /**
     *  DLMS Get PoweFailureLog
     * @param modemId
     * @param param
     * 
     * @return
     * @throws Exception
     */
    @WebMethod
    public ResponseMap cmdGetPowerFailureLog(
    		@WebParam(name="ModemId") String modemId,
    		@WebParam(name="FromDate") String fromDate,
    		@WebParam(name="ToDate") String toDate ) throws Exception
    {
        ResponseMap response = new ResponseMap();
        response.setResponse(command.cmdGetPowerFailureLog(modemId, fromDate, toDate ));
        return response;
    }
    
    /**
     *  DLMS Get ControlLog
     * @param modemId
     * @param param
     * 
     * @return
     * @throws Exception
     */
    @WebMethod
    public ResponseMap cmdGetControlLog(
    		@WebParam(name="ModemId") String modemId,
    		@WebParam(name="FromDate") String fromDate,
    		@WebParam(name="ToDate") String toDate ) throws Exception
    {
        ResponseMap response = new ResponseMap();
        response.setResponse(command.cmdGetControlLog(modemId,fromDate,toDate));
        return response;
    }
    
    /**
     *  DLMS Get cmdGetPQLog
     * @param modemId
     * @param param
     * 
     * @return
     * @throws Exception
     */
    @WebMethod
    public ResponseMap cmdGetPQLog(
    		@WebParam(name="ModemId") String modemId,
    		@WebParam(name="FromDate") String fromDate,
    		@WebParam(name="ToDate") String toDate ) throws Exception
    {
        ResponseMap response = new ResponseMap();
        response.setResponse(command.cmdGetPQLog(modemId, fromDate, toDate ));
        return response;
    }
    
    /**
     *  DLMS Get FWUpgradeLog
     * @param modemId
     * @param param
     * 
     * @return
     * @throws Exception
     */
    @WebMethod
    public ResponseMap cmdGetFWUpgradeLog(
    		@WebParam(name="ModemId") String modemId,
    		@WebParam(name="FromDate") String fromDate,
    		@WebParam(name="ToDate") String toDate ) throws Exception
    {
        ResponseMap response = new ResponseMap();
        response.setResponse(command.cmdGetFWUpgradeLog(modemId, fromDate, toDate ));
        return response;
    }
    
    /**
     *  DLMS Get StandardEventLog
     * @param modemId
     * @param param
     * 
     * @return
     * @throws Exception
     */
    @WebMethod
    public ResponseMap cmdGetStandardEventLog(
    		@WebParam(name="ModemId") String modemId,
    		@WebParam(name="FromDate") String fromDate,
    		@WebParam(name="ToDate") String toDate ) throws Exception
    {
        ResponseMap response = new ResponseMap();
        response.setResponse(command.cmdGetStandardEventLog(modemId, fromDate, toDate ));
        return response;
    }

    // INSERT START SP-193
    @WebMethod
   	public String cmdSendEvent(
   			@WebParam(name="EventAlertName") String eventAlertName, 
   			@WebParam(name="TargeClassName") String target, 
   			@WebParam(name="ActivatorId") String activatorId, 
   			@WebParam(name="Params") String[][] params) throws Exception
	{
    	return command.cmdSendEvent(eventAlertName, target, activatorId, params);
	}    
    // INSERT END SP-193    
    
    @WebMethod
	public void cmdSendEvent2(
   			@WebParam(name="EventAlertName") String eventAlertName, 
   			@WebParam(name="ActivatorType") String activatorType, 
   			@WebParam(name = "ActivatorId") String activatorId,
			@WebParam(name = "SupplierId") int supplierId) throws Exception {
		command.cmdSendEvent2(eventAlertName, activatorType, activatorId, supplierId);
	}
    
    /**
     * NI Protocol Command :  0x2002 Modem Reset Time(set)
     * 
     * @param modemId
     * @param requestType
     * @param resetTime
     * @return
     * @throws Exception
     */
    @WebMethod
    public ResponseMap cmdModemResetTime(
    		@WebParam(name="ModemId") String modemId, 
    		@WebParam(name="RequestType") String requestType, 
    		@WebParam(name="ResetTime") int resetTime) throws Exception
    {
        log.info("ModemId[" + modemId + "] requestType[" + requestType + "]");
        ResponseMap response = new ResponseMap();
        response.setResponse(command.cmdModemResetTime(modemId,requestType,resetTime));
        return response;
    }

    /**
     * NI Protocol Command :  0x2003 Modem Mode(set/get)
     * 
     * @param modemId
     * @param requestType
     * @param mode
     * @return
     * @throws Exception
     */
    @WebMethod
    public ResponseMap cmdModemMode(
    		@WebParam(name="ModemId") String modemId, 
    		@WebParam(name="RequestType") String requestType, 
    		@WebParam(name="Mode") int mode) throws Exception
    {
        log.info("ModemId[" + modemId + "] requestType[" + requestType + "]");
        ResponseMap response = new ResponseMap();
        response.setResponse(command.cmdModemMode(modemId,requestType,mode));
        return response;
    }

    /**
     * NI Protocol Command :  0x2010 SNMP Sever IPv6/Port(set/get)
     * 
     * @param modemId
     * @param requestType
     * @param type
     * @param ipAddress
     * @param port
     * @return
     * @throws Exception
     */
    @WebMethod
    public ResponseMap cmdSnmpServerIpv6Port(
    		@WebParam(name="ModemId") String modemId, 
    		@WebParam(name="RequestType") String requestType, 
    		@WebParam(name="Type") int type,
    		@WebParam(name="IpAddress") String ipAddress,
    		@WebParam(name="Port") String port
    		) throws Exception
    {
        log.info("ModemId[" + modemId + "] requestType[" + requestType + "]");
        ResponseMap response = new ResponseMap();
        response.setResponse(command.cmdSnmpServerIpv6Port(modemId,requestType,type,ipAddress,port));
        return response;
    }
    
    /**
     *  NI Protocol Command :  0x2011 Alarm/Event Command ON_OFF(set/get)
     *  
     * @param modemId
     * @param requestType
     * @param count
     * @param cmds
     * @return
     * @throws Exception
     */
    @WebMethod
    public ResponseMap cmdAlarmEventCommandOnOff(
    		@WebParam(name="ModemId") String modemId, 
    		@WebParam(name="RequestType") String requestType, 
    		@WebParam(name="Count") int count,
    		@WebParam(name="Cmds") String cmds
    		) throws Exception
    {
        log.info("ModemId[" + modemId + "] requestType[" + requestType + "]");
        ResponseMap response = new ResponseMap();
        response.setResponse(command.cmdAlarmEventCommandOnOff(modemId,requestType,count, cmds));
        return response;
    }
    
    /**
     * NI Protocol Command : 0x2013 Transmit Frequency(set/get)
     * 
     * @param modemId
     * @param requestType
     * @param second
     * @param cmds
     * @return
     * @throws Exception
     */
    @WebMethod
    public ResponseMap cmdTransmitFrequency(
    		@WebParam(name="ModemId") String modemId, 
    		@WebParam(name="RequestType") String requestType, 
    		@WebParam(name="Second") int second
    		) throws Exception
    {
        log.info("ModemId[" + modemId + "] requestType[" + requestType + "]");
        ResponseMap response = new ResponseMap();
        response.setResponse(command.cmdTransmitFrequency(modemId,requestType,second));
        return response;
    }
    
    @WebMethod(operationName="sendSMS")
    public String sendSMS (
    		@WebParam(name = "CommandName", targetNamespace = "")
    		String commandName, 
    		@WebParam(name = "TrnxId", targetNamespace = "")
    		String trnxId,
    		@WebParam(name = "MessageType", targetNamespace = "")
            String messageType,
            @WebParam(name = "MobliePhNum", targetNamespace = "")
            String mobliePhNum,
            @WebParam(name = "EuiId", targetNamespace = "")
            String euiId,
            @WebParam(name = "CommandCode", targetNamespace = "")
            String commandCode,
    		@WebParam(name = "ParamList", targetNamespace = "")
    		List<String> paramList,
    		@WebParam(name = "CmdMap", targetNamespace = "")
    		String cmdMap
    		) throws Exception {
    	return command.sendSMS(commandName, messageType, mobliePhNum, euiId, commandCode, paramList, cmdMap);
		
    }
    

    @WebMethod
    public ResponseMap cmdModemIpInformation(
    		@WebParam(name="ModemId") String modemId, 
    		@WebParam(name="RequestType") String requestType, 
    		@WebParam(name="TargetType") int targetType,
    		@WebParam(name="IpType") int ipType,
    		@WebParam(name="IpAddress") String ipAddress
    		) throws Exception
    {
        log.info("ModemId[" + modemId + "] requestType[" + requestType + "] targetType[" 
    + targetType + "] ipType[" + ipType +" ipAddress[" + ipAddress +"]"  );
        ResponseMap response = new ResponseMap();
        response.setResponse(command.cmdModemIpInformation(modemId,requestType,targetType,ipType, ipAddress));
        return response;
    }
    
    @WebMethod
    public ResponseMap cmdModemPortInformation(
    		@WebParam(name="ModemId") String modemId, 
    		@WebParam(name="RequestType") String requestType, 
    		@WebParam(name="TargetType") int targetType,
    		@WebParam(name="Port") String port
    		) throws Exception
    {
        log.info("ModemId[" + modemId + "] requestType[" + requestType + "] targetType[" 
    + targetType + "] port[" + port  +"]"  );
        ResponseMap response = new ResponseMap();
        response.setResponse(command.cmdModemPortInformation(modemId,requestType,targetType,port));
        return response;
    }
    
    // INSERT START SP-476
    @WebMethod(operationName="cmdDmdNiGetRomRead")
    public MeterData cmdDmdNiGetRomRead(@WebParam(name="McuId") String mcuId,
            @WebParam(name="MeterSerialNo") String meterId,
            @WebParam(name="ModemId") String modemId,
            @WebParam(name="FromDate") String fromDate, 
            @WebParam(name="ToDate") String toDate,
            @WebParam(name="PollType") int pollType
            )
                    throws Exception
    {
        log.info("mcuId[" + mcuId + "] meterId[" + meterId+"] modemId[" + modemId+"] fromDate[" + fromDate + "] toDate[" + toDate +"] pollType[" + pollType + "]");
        try {
        	// UPDATE START SP-681
//            MeterData md = command.cmdDmdNiGetRomRead(mcuId, meterId, modemId, fromDate, toDate);
            MeterData md = command.cmdDmdNiGetRomRead(mcuId, meterId, modemId, fromDate, toDate, pollType);
            // UPDATE END SP-681
            return md;
        }
        catch (Exception e) {
            throw e;
        }
    }    
    // INSERT END SP-476

    // INSERT START SP-681
    public ResponseMap cmdExecDmdNiCommand(
    		@WebParam(name="ModemId") String modemId, 
    		@WebParam(name="RequestType") String requestType, 
    		@WebParam(name="AttributeId") String attributeId,
    		@WebParam(name="AttributeParam") String attributeParam
    		) throws Exception
    {
        log.info("ModemId[" + modemId + "] requestType[" + requestType + "] attributeId[" 
    + attributeId + "] attributeParam[" + attributeParam  +"]"  );
        ResponseMap response = new ResponseMap();
        response.setResponse(command.cmdExecDmdNiCommand(modemId,requestType,attributeId,attributeParam));
        return response;
    }    
    
    // INSERT END SP-681    
    
    /**
     * SP-677
     * @param mcuId
     * @param modemPort
     * @param meterIds
     * @param fromDate
     * @param toDate
     * @return
     * @throws Exception
     */
    @WebMethod(operationName="cmdDmdNiGetRomReadMulti")
    public ResponseMap cmdDmdNiGetRomReadMulti(@WebParam(name="McuId") String mcuId,
            @WebParam(name="ModemPort") Integer modemPort,
            @WebParam(name="MeterIdList") String[] meterIds,
            @WebParam(name="ModemIdList") String[] modemIds,
            @WebParam(name="FromDate") String fromDate, 
            @WebParam(name="ToDate") String toDate)
                    throws Exception
    {
        log.info("mcuId[" + mcuId + "] modemPort[" + modemPort + "] meterNum[" + String.valueOf(meterIds.length) +"] fromDate[" + fromDate + "] toDate[" + toDate +"]");
        try {
        	ResponseMap response = new ResponseMap();
        	response.setResponse(command.cmdDmdNiGetRomReadMulti(mcuId, modemPort, meterIds, modemIds, fromDate, toDate));
        	return response;
        }
        catch (Exception e) {
            throw e;
        }
    }
    
    /**
     * MCU Get Schedule
     *
     * @param mcuId MCU ID
     * @param name Schedule Name
     * @return schdule list
     * @throws Exception
     */
    @WebMethod
    public ResponseMap cmdMcuGetSchedule_(@WebParam(name="McuId") String mcuId, 
    		@WebParam(name="Name") String name )
        throws Exception
    {
        ResponseMap response = new ResponseMap();
        response.setResponse(command.cmdMcuGetSchedule_(mcuId, name));
        return response;
	}
    
	/**
	 * MCU Set Schedule 
	 *
	 * @param mcuId MCU ID
	 * @param args Schedule settings
	 * @throws  Exception
	 */
    @WebMethod
   	public void cmdMcuSetSchedule_(@WebParam(name="McuId") String mcuId, 
   			@WebParam(name="ScheduleList") String[][] args) 
    	throws Exception
	{
    	command.cmdMcuSetSchedule_(mcuId, args);;
	}
    
    /**
	 * MCU Delete Schedule 
	 *
	 * @param mcuId MCU ID
	 * @param Schedul Name
	 * @throws  Exception
	 */
    @WebMethod
   	public void cmdMcuDeleteSchedule(@WebParam(name="McuId") String mcuId, 
   			@WebParam(name="ScheduleName") String scheduleName) 
    	throws Exception
	{
    	command.cmdMcuDeleteSchedule(mcuId, scheduleName);;
	}
    
    /**
	 * MCU Execute Schedule 
	 *
	 * @param mcuId MCU ID
	 * @param Schedul Name
	 * @throws  Exception
	 */
    @WebMethod
   	public void cmdMcuExecuteSchedule(@WebParam(name="McuId") String mcuId, 
   			@WebParam(name="ScheduleList") String scheduleName) 
    	throws Exception
	{
    	command.cmdMcuExecuteSchedule(mcuId, scheduleName);;
	}
	
	/**
     * GPRS Modem TCP Command Trigger
     * @param cmd
     * @param ipAddr
     * @return isConnect
     * @throws Exception
     */
    @WebMethod
    public boolean cmdTCPTrigger(
    		@WebParam(name="Cmd") String cmd,    		
    		@WebParam(name="IpAddr") String ipAddr) throws Exception
    {
    	boolean isConnect = false;
    	isConnect = command.cmdTCPTrigger(cmd, ipAddr);
    	return isConnect;
    }
    
    /**
     * cmdLineModemEVN
     *
     * @param cmd
     * @param modemId
     * @param params
     * @throws FMPMcuException, Exception
     */
    @WebMethod
    public ResponseMap cmdLineModemEVN(@WebParam(name="Cmd") String cmd,
            @WebParam(name="ModemId") String modemId,
            @WebParam(name="Params") String[] params)
                    throws Exception
    {
        ResponseMap response = new ResponseMap();
        response.setResponse(command.cmdLineModemEVN(cmd, modemId, params));
        return response;
    }
    
}