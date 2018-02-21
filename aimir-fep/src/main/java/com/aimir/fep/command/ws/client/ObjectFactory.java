
package com.aimir.fep.command.ws.client;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

import com.aimir.fep.meter.data.MeterData;
import com.aimir.fep.meter.parser.plc.PLCDataFrame;
import com.aimir.fep.modem.AmrData;
import com.aimir.fep.modem.BatteryLog;
import com.aimir.fep.modem.EventLog;
import com.aimir.fep.modem.LPData;
import com.aimir.fep.modem.ModemNetwork;
import com.aimir.fep.modem.ModemNode;
import com.aimir.fep.protocol.fmp.common.Target;
import com.aimir.fep.protocol.fmp.datatype.BOOL;
import com.aimir.fep.protocol.fmp.datatype.BYTE;
import com.aimir.fep.protocol.fmp.datatype.CHAR;
import com.aimir.fep.protocol.fmp.datatype.HEX;
import com.aimir.fep.protocol.fmp.datatype.INT;
import com.aimir.fep.protocol.fmp.datatype.IPADDR;
import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.protocol.fmp.datatype.OID;
import com.aimir.fep.protocol.fmp.datatype.SHORT;
import com.aimir.fep.protocol.fmp.datatype.SMIValue;
import com.aimir.fep.protocol.fmp.datatype.TIMESTAMP;
import com.aimir.fep.protocol.fmp.datatype.UINT;
import com.aimir.fep.protocol.fmp.datatype.WORD;
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
import com.aimir.fep.protocol.fmp.frame.service.entry.mobileEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.pluginEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.procEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.sensorInfoNewEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.sysEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.timeEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.trInfoEntry;
import com.aimir.fep.util.FileInfo;
import com.aimir.fep.util.GroupInfo;
import com.aimir.fep.util.MemberInfo;
import com.aimir.model.device.Modem;
import com.aimir.model.system.Location;
import com.aimir.model.system.MeterProgram;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.aimir.fep.command.ws.client package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _CmdMcuFactoryDefaultResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdMcuFactoryDefaultResponse");
    private final static QName _CmdDeleteModemByChannelPanResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdDeleteModemByChannelPanResponse");
    private final static QName _CmdMcuGetTimeResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdMcuGetTimeResponse");
    private final static QName _CmdDistributionState_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdDistributionState");
    private final static QName _CmdSetMeterConfig_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdSetMeterConfig");
    private final static QName _CmdGroupDeleteResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGroupDeleteResponse");
    private final static QName _CmdSetTOUCalendar_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdSetTOUCalendar");
    private final static QName _CmdUpdateGroup_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdUpdateGroup");
    private final static QName _CmdSetCodiReset1Response_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdSetCodiReset1Response");
    private final static QName _CmdIDRStart_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdIDRStart");
    private final static QName _CmdGetDRAssetInfoResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetDRAssetInfoResponse");
    private final static QName _CmdMcuDiagnosisResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdMcuDiagnosisResponse");
    private final static QName _CmdDigitalInOutResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdDigitalInOutResponse");
    private final static QName _CmdDistributionStateResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdDistributionStateResponse");
    private final static QName _CmdGetCodiDevice_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetCodiDevice");
    private final static QName _CmdEndDeviceControl_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdEndDeviceControl");
    private final static QName _CmdClearCommLog_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdClearCommLog");
    private final static QName _CmdMcuGetFileSystem_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdMcuGetFileSystem");
    private final static QName _CmdDeleteLoadControlSchemeResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdDeleteLoadControlSchemeResponse");
    private final static QName _CmdSetCodiResetResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdSetCodiResetResponse");
    private final static QName _CmdRelaySwitchAndActivateResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdRelaySwitchAndActivateResponse");
    private final static QName _CmdMcuSetGMT_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdMcuSetGMT");
    private final static QName _CmdAidonMCCB_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdAidonMCCB");
    private final static QName _CmdGroupAddResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGroupAddResponse");
    private final static QName _CmdSetLoadLimitScheme_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdSetLoadLimitScheme");
    private final static QName _CmdSetTOUCalendarResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdSetTOUCalendarResponse");
    private final static QName _CmdClearMeterLog_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdClearMeterLog");
    private final static QName _CmdDeleteLoadShedScheme_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdDeleteLoadShedScheme");
    private final static QName _CmdCorrectModemPulse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdCorrectModemPulse");
    private final static QName _CmdGetDRAssetResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetDRAssetResponse");
    private final static QName _CmdDRAgreement_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdDRAgreement");
    private final static QName _CmdGroupAsyncCallResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGroupAsyncCallResponse");
    private final static QName _CmdGroupDeleteMemberResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGroupDeleteMemberResponse");
    private final static QName _CmdGetPhoneList_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetPhoneList");
    private final static QName _CmdGetMeterLogListResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetMeterLogListResponse");
    private final static QName _CmdDeleteLoadLimitSchemeResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdDeleteLoadLimitSchemeResponse");
    private final static QName _CmdGetDRLevelResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetDRLevelResponse");
    private final static QName _CmdSetMeterSecurityResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdSetMeterSecurityResponse");
    private final static QName _CmdUpdateModemFirmwareResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdUpdateModemFirmwareResponse");
    private final static QName _CmdEndDeviceControlResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdEndDeviceControlResponse");
    private final static QName _DoGetModemROM_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "doGetModemROM");
    private final static QName _CmdGetModemAmrDataResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetModemAmrDataResponse");
    private final static QName _CmdGetCodiDevice1Response_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetCodiDevice1Response");
    private final static QName _CmdDeleteGroupResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdDeleteGroupResponse");
    private final static QName _CmdGetCodiMemory1Response_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetCodiMemory1Response");
    private final static QName _CmdRecovery_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdRecovery");
    private final static QName _CmdMcuLoopback_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdMcuLoopback");
    private final static QName _CmdMcuGetMemoryResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdMcuGetMemoryResponse");
    private final static QName _CmdBypassMeterProgramResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdBypassMeterProgramResponse");
    private final static QName _CmdGetFile_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetFile");
    private final static QName _CmdInstallFileResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdInstallFileResponse");
    private final static QName _CmdGetModemCount_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetModemCount");
    private final static QName _CmdSetLoadLimitSchemeResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdSetLoadLimitSchemeResponse");
    private final static QName _Exception_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "Exception");
    private final static QName _CmdOnDemandMeterAllResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdOnDemandMeterAllResponse");
    private final static QName _CmdEntryStdSetResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdEntryStdSetResponse");
    private final static QName _CmdGetEventLogListResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetEventLogListResponse");
    private final static QName _CmdModifyTransactionResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdModifyTransactionResponse");
    private final static QName _CmdDeleteModemAll_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdDeleteModemAll");
    private final static QName _CmdMcuClearStaticResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdMcuClearStaticResponse");
    private final static QName _CmdMcuShutdownResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdMcuShutdownResponse");
    private final static QName _CmdSetLoadControlScheme_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdSetLoadControlScheme");
    private final static QName _CmdSetEnergyLevel_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdSetEnergyLevel");
    private final static QName _CmdMeteringAll_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdMeteringAll");
    private final static QName _CmdSetIEIUConfResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdSetIEIUConfResponse");
    private final static QName _CmdOnDemandMeterAsyncResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdOnDemandMeterAsyncResponse");
    private final static QName _CmdGetConfiguration_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetConfiguration");
    private final static QName _CmdAsynchronousCall_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdAsynchronousCall");
    private final static QName _CmdGetModemROMResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetModemROMResponse");
    private final static QName _CmdMcuResetResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdMcuResetResponse");
    private final static QName _CmdEntryStdGetResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdEntryStdGetResponse");
    private final static QName _CmdMcuGetIpResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdMcuGetIpResponse");
    private final static QName _CmdSetModemAmrData_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdSetModemAmrData");
    private final static QName _CmdOnDemandMeter2_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdOnDemandMeter2");
    private final static QName _CmdMcuClearStatic_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdMcuClearStatic");
    private final static QName _CmdGetCodiNeighbor_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetCodiNeighbor");
    private final static QName _CmdDigitalInOut_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdDigitalInOut");
    private final static QName _CmdOnDemandMeter3_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdOnDemandMeter3");
    private final static QName _CmdMcuGetProcess_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdMcuGetProcess");
    private final static QName _CmdGetCodiBinding1Response_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetCodiBinding1Response");
    private final static QName _CmdMcuGetPluginResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdMcuGetPluginResponse");
    private final static QName _CmdGetModemAmrData_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetModemAmrData");
    private final static QName _CmdGetCodiBindingResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetCodiBindingResponse");
    private final static QName _CmdGetLoadLimitPropertyResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetLoadLimitPropertyResponse");
    private final static QName _CmdWriteTableResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdWriteTableResponse");
    private final static QName _CmdMeterFactoryReset_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdMeterFactoryReset");
    private final static QName _CmdGetDRAsset_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetDRAsset");
    private final static QName _CmdGetMeterScheduleResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetMeterScheduleResponse");
    private final static QName _CmdDeleteLoadShedSchemeResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdDeleteLoadShedSchemeResponse");
    private final static QName _CmdDeleteModemByPropertyResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdDeleteModemByPropertyResponse");
    private final static QName _CmdGetMeter_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetMeter");
    private final static QName _CmdFirmwareUpdate_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdFirmwareUpdate");
    private final static QName _CmdReadTable_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdReadTable");
    private final static QName _CmdSensorLPLogRecoveryResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdSensorLPLogRecoveryResponse");
    private final static QName _DoGetModemCluster_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "doGetModemCluster");
    private final static QName _CmdDeleteModemByProperty_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdDeleteModemByProperty");
    private final static QName _CmdGetFFDList_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetFFDList");
    private final static QName _CmdGetLoadShedScheduleResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetLoadShedScheduleResponse");
    private final static QName _CmdPutFileResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdPutFileResponse");
    private final static QName _CmdSetIHDTable_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdSetIHDTable");
    private final static QName _CmdMcuGetSystemInfoResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdMcuGetSystemInfoResponse");
    private final static QName _CmdBypassSensor_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdBypassSensor");
    private final static QName _CmdGetAllLogList_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetAllLogList");
    private final static QName _CmdGroupInfo2_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGroupInfo2");
    private final static QName _CmdSetIEIUConf_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdSetIEIUConf");
    private final static QName _CmdGroupInfo1_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGroupInfo1");
    private final static QName _RequestPLCDataFrame_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "requestPLCDataFrame");
    private final static QName _CmdMcuGetProcessResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdMcuGetProcessResponse");
    private final static QName _CmdCommandModem1Response_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdCommandModem1Response");
    private final static QName _CmdMcuGetPlugin_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdMcuGetPlugin");
    private final static QName _CmdGetMeterSecurityResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetMeterSecurityResponse");
    private final static QName _CmdShowTransactionListResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdShowTransactionListResponse");
    private final static QName _CmdMcuGetFileSystemResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdMcuGetFileSystemResponse");
    private final static QName _CmdMcuSetTime_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdMcuSetTime");
    private final static QName _CmdMcuSetGMTResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdMcuSetGMTResponse");
    private final static QName _CmdMcuResetDevice_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdMcuResetDevice");
    private final static QName _CmdGetMeterLogList_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetMeterLogList");
    private final static QName _CmdDRAgreementResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdDRAgreementResponse");
    private final static QName _CmdGetCodiInfoResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetCodiInfoResponse");
    private final static QName _CmdSetLoadControlSchemeResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdSetLoadControlSchemeResponse");
    private final static QName _CmdMcuSetGpioResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdMcuSetGpioResponse");
    private final static QName _CmdSetCodiReset_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdSetCodiReset");
    private final static QName _CmdSetEnergyLevel1Response_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdSetEnergyLevel1Response");
    private final static QName _CmdReportCurMeter_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdReportCurMeter");
    private final static QName _CmdGetLoadLimitScheme_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetLoadLimitScheme");
    private final static QName _CmdGetMeterInfoFromModemResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetMeterInfoFromModemResponse");
    private final static QName _CmdGetModemEventResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetModemEventResponse");
    private final static QName _CmdGetLoadControlSchemeResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetLoadControlSchemeResponse");
    private final static QName _CmdSetPhoneListResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdSetPhoneListResponse");
    private final static QName _CmdGroupInfo_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGroupInfo");
    private final static QName _CmdGroupDelete_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGroupDelete");
    private final static QName _CmdPackageDistribution_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdPackageDistribution");
    private final static QName _CmdKamstrupCIDResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdKamstrupCIDResponse");
    private final static QName _CmdClearEventLogResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdClearEventLogResponse");
    private final static QName _CmdValveControlResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdValveControlResponse");
    private final static QName _CmdSetLoadLimitPropertyResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdSetLoadLimitPropertyResponse");
    private final static QName _CmdSetDRLevel_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdSetDRLevel");
    private final static QName _CmdAddModems_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdAddModems");
    private final static QName _CmdMcuSetGpio_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdMcuSetGpio");
    private final static QName _CmdDRCancel_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdDRCancel");
    private final static QName _CmdMcuSetTimeResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdMcuSetTimeResponse");
    private final static QName _CmdGetCommLogListResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetCommLogListResponse");
    private final static QName _CmdCommandModemResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdCommandModemResponse");
    private final static QName _CmdGetModemAllNewResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetModemAllNewResponse");
    private final static QName _CmdGetMeterAll_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetMeterAll");
    private final static QName _CmdCommandModem1_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdCommandModem1");
    private final static QName _CmdGetGroupResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetGroupResponse");
    private final static QName _CmdGetCurMeterAll_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetCurMeterAll");
    private final static QName _CmdGetCodiNeighbor1_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetCodiNeighbor1");
    private final static QName _CmdClearCommLogResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdClearCommLogResponse");
    private final static QName _SendBypassOpenSMS_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "sendBypassOpenSMS");
    private final static QName _CmdGroupAsyncCall_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGroupAsyncCall");
    private final static QName _CmdSendSMSResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdSendSMSResponse");
    private final static QName _CmdGetMobileLogListResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetMobileLogListResponse");
    private final static QName _CmdGroupInfoResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGroupInfoResponse");
    private final static QName _CmdGetCodiBinding_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetCodiBinding");
    private final static QName _CmdDistributionResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdDistributionResponse");
    private final static QName _CmdGetLoadLimitSchemeResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetLoadLimitSchemeResponse");
    private final static QName _CmdACD_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdACD");
    private final static QName _CmdInstallFile1Response_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdInstallFile1Response");
    private final static QName _CmdGetCommLogList_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetCommLogList");
    private final static QName _CmdGetCodiMemory_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetCodiMemory");
    private final static QName _CmdSetCodiFormNetworkResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdSetCodiFormNetworkResponse");
    private final static QName _CmdGetDRLevel_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetDRLevel");
    private final static QName _CmdKamstrupCID1Response_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdKamstrupCID1Response");
    private final static QName _CmdDistributionCancel_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdDistributionCancel");
    private final static QName _CmdEntryStdSet_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdEntryStdSet");
    private final static QName _GetRelaySwitchStatus_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "getRelaySwitchStatus");
    private final static QName _CmdOnDemandMeterAll_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdOnDemandMeterAll");
    private final static QName _CmdExecuteCommandResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdExecuteCommandResponse");
    private final static QName _CmdOnDemandMeterResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdOnDemandMeterResponse");
    private final static QName _CmdSetCodiPermit_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdSetCodiPermit");
    private final static QName _CmdModifyTransaction_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdModifyTransaction");
    private final static QName _CmdGetCurMeterAllResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetCurMeterAllResponse");
    private final static QName _CmdValveControl_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdValveControl");
    private final static QName _CmdOnDemandMeter_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdOnDemandMeter");
    private final static QName _CmdGroupInfo1Response_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGroupInfo1Response");
    private final static QName _CmdGetCodiNeighbor1Response_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetCodiNeighbor1Response");
    private final static QName _CmdGetCodiListResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetCodiListResponse");
    private final static QName _CmdMcuGetEnvironmentResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdMcuGetEnvironmentResponse");
    private final static QName _CmdSmsFirmwareUpdateResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdSmsFirmwareUpdateResponse");
    private final static QName _CmdMcuDiagnosis_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdMcuDiagnosis");
    private final static QName _CmdMeterProgram_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdMeterProgram");
    private final static QName _CmdSetLoadShedSchedule_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdSetLoadShedSchedule");
    private final static QName _CmdAidonMCCBResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdAidonMCCBResponse");
    private final static QName _CmdSetEnergyLevel1_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdSetEnergyLevel1");
    private final static QName _CmdMeterProgramResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdMeterProgramResponse");
    private final static QName _CmdOnDemandMeter2Response_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdOnDemandMeter2Response");
    private final static QName _CmdStdGet1_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdStdGet1");
    private final static QName _CmdGetMeterVersion_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetMeterVersion");
    private final static QName _CmdShowTransactionList_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdShowTransactionList");
    private final static QName _RequestPLCDataFrameResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "requestPLCDataFrameResponse");
    private final static QName _CmdAddModemsResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdAddModemsResponse");
    private final static QName _CmdClearCmdHistLog_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdClearCmdHistLog");
    private final static QName _CmdGetCodiInfo_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetCodiInfo");
    private final static QName _CmdGetModemBatteryResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetModemBatteryResponse");
    private final static QName _CmdSetMeterTime_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdSetMeterTime");
    private final static QName _CmdGetMeterResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetMeterResponse");
    private final static QName _CmdResetModem_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdResetModem");
    private final static QName _CmdDeleteModemResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdDeleteModemResponse");
    private final static QName _CmdGroupAdd_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGroupAdd");
    private final static QName _CmdRecoveryResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdRecoveryResponse");
    private final static QName _CmdGetCodiMemory1_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetCodiMemory1");
    private final static QName _CmdPackageDistributionResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdPackageDistributionResponse");
    private final static QName _CmdSetModemAmrDataResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdSetModemAmrDataResponse");
    private final static QName _CmdSendIHDData_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdSendIHDData");
    private final static QName _CmdIDRCancel_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdIDRCancel");
    private final static QName _CmdOnDemandMBus_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdOnDemandMBus");
    private final static QName _CmdGetGroup_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetGroup");
    private final static QName _CmdCommandModem_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdCommandModem");
    private final static QName _CmdClearEventLog_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdClearEventLog");
    private final static QName _CmdDemandReset_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdDemandReset");
    private final static QName _CmdUpdateModemFirmware_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdUpdateModemFirmware");
    private final static QName _CmdGetCodiBinding1_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetCodiBinding1");
    private final static QName _CmdStdSetResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdStdSetResponse");
    private final static QName _CmdMeterUploadRangeResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdMeterUploadRangeResponse");
    private final static QName _CmdSetCodiPermitResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdSetCodiPermitResponse");
    private final static QName _CmdStdGetResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdStdGetResponse");
    private final static QName _CmdSendIHDDataResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdSendIHDDataResponse");
    private final static QName _CmdMcuScanningResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdMcuScanningResponse");
    private final static QName _CmdMcuGetGpioResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdMcuGetGpioResponse");
    private final static QName _CmdCorrectModemPulseResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdCorrectModemPulseResponse");
    private final static QName _CmdGetMeterInfo_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetMeterInfo");
    private final static QName _CmdSetModemROMResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdSetModemROMResponse");
    private final static QName _CmdEntryStdGet_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdEntryStdGet");
    private final static QName _GetRelaySwitchStatusResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "getRelaySwitchStatusResponse");
    private final static QName _CmdMcuGetState_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdMcuGetState");
    private final static QName _CmdSetConfiguration_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdSetConfiguration");
    private final static QName _CmdGetMeterAllResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetMeterAllResponse");
    private final static QName _CmdSetCiuLCDResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdSetCiuLCDResponse");
    private final static QName _CmdGetDRAssetInfo_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetDRAssetInfo");
    private final static QName _CmdOnDemandMBusResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdOnDemandMBusResponse");
    private final static QName _CmdGetModemROM_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetModemROM");
    private final static QName _CmdGetMeterTime_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetMeterTime");
    private final static QName _CmdGetMeterSecurity_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetMeterSecurity");
    private final static QName _CmdGetCodiDevice1_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetCodiDevice1");
    private final static QName _CmdDelIHDTableResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdDelIHDTableResponse");
    private final static QName _CmdGetCmdHistList_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetCmdHistList");
    private final static QName _CmdInstallFile1_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdInstallFile1");
    private final static QName _CmdBypassSensor1Response_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdBypassSensor1Response");
    private final static QName _CmdInstallFile_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdInstallFile");
    private final static QName _CmdMeterTimeSyncResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdMeterTimeSyncResponse");
    private final static QName _CmdGetModemEvent_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetModemEvent");
    private final static QName _CmdSetConfigurationResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdSetConfigurationResponse");
    private final static QName _CmdGetMeterSchedule_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetMeterSchedule");
    private final static QName _CmdDeleteModemAllResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdDeleteModemAllResponse");
    private final static QName _CmdOnRecoveryDemandMeter_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdOnRecoveryDemandMeter");
    private final static QName _CmdMeterTimeSync_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdMeterTimeSync");
    private final static QName _CmdMetering_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdMetering");
    private final static QName _CmdSendSMS_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdSendSMS");
    private final static QName _CmdGetModemROM1Response_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetModemROM1Response");
    private final static QName _CmdMcuShutdown_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdMcuShutdown");
    private final static QName _CmdSmsFirmwareUpdate_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdSmsFirmwareUpdate");
    private final static QName _CmdOnDemandMeter3Response_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdOnDemandMeter3Response");
    private final static QName _CmdStdSet1Response_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdStdSet1Response");
    private final static QName _CmdIDRCancelResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdIDRCancelResponse");
    private final static QName _CmdGetMeterVersionResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetMeterVersionResponse");
    private final static QName _CmdDemandResetResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdDemandResetResponse");
    private final static QName _CmdGetCurMeterResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetCurMeterResponse");
    private final static QName _CmdAddModemResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdAddModemResponse");
    private final static QName _CmdSetIHDTableResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdSetIHDTableResponse");
    private final static QName _CmdMcuResetDeviceResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdMcuResetDeviceResponse");
    private final static QName _CmdStdGet_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdStdGet");
    private final static QName _CmdSetMeterTimeResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdSetMeterTimeResponse");
    private final static QName _CmdClearMeterLogResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdClearMeterLogResponse");
    private final static QName _CmdMcuLoopbackResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdMcuLoopbackResponse");
    private final static QName _CmdUpdateGroupResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdUpdateGroupResponse");
    private final static QName _DoGetModemClusterResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "doGetModemClusterResponse");
    private final static QName _CmdGroupInfo2Response_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGroupInfo2Response");
    private final static QName _CmdMcuGetMobileResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdMcuGetMobileResponse");
    private final static QName _CmdAsynchronousCallResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdAsynchronousCallResponse");
    private final static QName _CmdPutFile_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdPutFile");
    private final static QName _CmdSetCodiReset1_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdSetCodiReset1");
    private final static QName _CmdWriteTable_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdWriteTable");
    private final static QName _CmdClearCmdHistLogResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdClearCmdHistLogResponse");
    private final static QName _CmdKamstrupCID_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdKamstrupCID");
    private final static QName _CmdMcuReset_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdMcuReset");
    private final static QName _CmdGetLoadLimitProperty_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetLoadLimitProperty");
    private final static QName _DoGetModemROMResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "doGetModemROMResponse");
    private final static QName _CmdGetCurMeter_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetCurMeter");
    private final static QName _CmdMcuGetMemory_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdMcuGetMemory");
    private final static QName _CmdSetCiuLCD_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdSetCiuLCD");
    private final static QName _CmdMcuSetDST_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdMcuSetDST");
    private final static QName _CmdMcuGetStateResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdMcuGetStateResponse");
    private final static QName _CmdReadTableResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdReadTableResponse");
    private final static QName _CmdMcuScanning_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdMcuScanning");
    private final static QName _CmdExecuteCommand_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdExecuteCommand");
    private final static QName _CmdMcuGetEnvironment_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdMcuGetEnvironment");
    private final static QName _CmdFirmwareUpdateResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdFirmwareUpdateResponse");
    private final static QName _CmdGetFFDListResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetFFDListResponse");
    private final static QName _CmdBroadcastResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdBroadcastResponse");
    private final static QName _CmdBroadcast_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdBroadcast");
    private final static QName _CmdGetCodiInfo1_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetCodiInfo1");
    private final static QName _CmdSetLoadShedScheduleResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdSetLoadShedScheduleResponse");
    private final static QName _CmdGetCodiMemoryResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetCodiMemoryResponse");
    private final static QName _CmdGetCodiDeviceResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetCodiDeviceResponse");
    private final static QName _CmdRelaySwitchAndActivate_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdRelaySwitchAndActivate");
    private final static QName _CmdAddModem_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdAddModem");
    private final static QName _CmdMcuGetMobile_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdMcuGetMobile");
    private final static QName _CmdRecoveryByTargetTimeResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdRecoveryByTargetTimeResponse");
    private final static QName _CmdSendMessageResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdSendMessageResponse");
    private final static QName _CmdGetModemBattery_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetModemBattery");
    private final static QName _CmdGetEnergyLevel_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetEnergyLevel");
    private final static QName _CmdGetCmdHistListResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetCmdHistListResponse");
    private final static QName _CmdSetPhoneList_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdSetPhoneList");
    private final static QName _CmdMcuFactoryDefault_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdMcuFactoryDefault");
    private final static QName _CmdSetDRLevelResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdSetDRLevelResponse");
    private final static QName _CmdSetEnergyLevelResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdSetEnergyLevelResponse");
    private final static QName _CmdGetLoadControlScheme_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetLoadControlScheme");
    private final static QName _CmdShowTransactionInfo_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdShowTransactionInfo");
    private final static QName _CmdGetConfigurationResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetConfigurationResponse");
    private final static QName _CmdGetMeterInfoFromModem_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetMeterInfoFromModem");
    private final static QName _CmdOnRecoveryDemandMeterResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdOnRecoveryDemandMeterResponse");
    private final static QName _CmdResetModemResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdResetModemResponse");
    private final static QName _CmdDeleteLoadLimitScheme_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdDeleteLoadLimitScheme");
    private final static QName _CmdMcuGetSystemInfo_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdMcuGetSystemInfo");
    private final static QName _CmdDeleteModelAllByChannelPan_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdDeleteModelAllByChannelPan");
    private final static QName _CmdDistribution_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdDistribution");
    private final static QName _CmdStdGetChild_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdStdGetChild");
    private final static QName _CmdStdGetChildResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdStdGetChildResponse");
    private final static QName _CmdDeleteLoadControlScheme_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdDeleteLoadControlScheme");
    private final static QName _CmdGetCodiList_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetCodiList");
    private final static QName _CmdACDResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdACDResponse");
    private final static QName _CmdShowTransactionInfoResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdShowTransactionInfoResponse");
    private final static QName _CmdIDRStartResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdIDRStartResponse");
    private final static QName _SendBypassOpenSMSResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "sendBypassOpenSMSResponse");
    private final static QName _CmdSetModemROM_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdSetModemROM");
    private final static QName _CmdGetLoadShedSchedule_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetLoadShedSchedule");
    private final static QName _CmdMeterFactoryResetResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdMeterFactoryResetResponse");
    private final static QName _CmdDeleteGroup_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdDeleteGroup");
    private final static QName _CmdGetPhoneListResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetPhoneListResponse");
    private final static QName _CmdGetFileResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetFileResponse");
    private final static QName _CmdStdSet_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdStdSet");
    private final static QName _CmdMcuGetTime_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdMcuGetTime");
    private final static QName _CmdGetModemROM1_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetModemROM1");
    private final static QName _CmdSensorLPLogRecovery_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdSensorLPLogRecovery");
    private final static QName _CmdGetMeterTimeResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetMeterTimeResponse");
    private final static QName _CmdBypassSensor1_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdBypassSensor1");
    private final static QName _CmdMcuGetIp_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdMcuGetIp");
    private final static QName _CmdSetCodiFormNetwork_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdSetCodiFormNetwork");
    private final static QName _CmdSetMeterConfigResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdSetMeterConfigResponse");
    private final static QName _CmdSetLoadLimitProperty_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdSetLoadLimitProperty");
    private final static QName _CmdGetCodiNeighborResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetCodiNeighborResponse");
    private final static QName _CmdDeleteMemberAllResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdDeleteMemberAllResponse");
    private final static QName _CmdBypassMeterProgram_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdBypassMeterProgram");
    private final static QName _CmdDistributionCancelResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdDistributionCancelResponse");
    private final static QName _CmdDRCancelResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdDRCancelResponse");
    private final static QName _CmdReportCurMeterResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdReportCurMeterResponse");
    private final static QName _CmdStdGet1Response_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdStdGet1Response");
    private final static QName _CmdGetMobileLogList_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetMobileLogList");
    private final static QName _CmdSetMeterSecurity_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdSetMeterSecurity");
    private final static QName _CmdRecoveryByTargetTime_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdRecoveryByTargetTime");
    private final static QName _CmdDeleteMemberAll_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdDeleteMemberAll");
    private final static QName _CmdBypassSensorResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdBypassSensorResponse");
    private final static QName _CmdDeleteModelAllByChannelPanResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdDeleteModelAllByChannelPanResponse");
    private final static QName _CmdMeteringResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdMeteringResponse");
    private final static QName _CmdGroupAddMember_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGroupAddMember");
    private final static QName _CmdKamstrupCID1_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdKamstrupCID1");
    private final static QName _CmdOnDemandMeterAsync_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdOnDemandMeterAsync");
    private final static QName _CmdMeteringAllResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdMeteringAllResponse");
    private final static QName _CmdSendMessage_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdSendMessage");
    private final static QName _CmdGroupDeleteMember_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGroupDeleteMember");
    private final static QName _CmdGetAllLogListResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetAllLogListResponse");
    private final static QName _CmdGetEnergyLevelResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetEnergyLevelResponse");
    private final static QName _CmdGetEventLogList_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetEventLogList");
    private final static QName _CmdMcuSetDSTResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdMcuSetDSTResponse");
    private final static QName _CmdDeleteModemByChannelPan_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdDeleteModemByChannelPan");
    private final static QName _CmdGetCodiInfo1Response_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetCodiInfo1Response");
    private final static QName _CmdGetModemAllNew_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetModemAllNew");
    private final static QName _CmdSendSMS1_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdSendSMS1");
    private final static QName _CmdMeterUploadRange_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdMeterUploadRange");
    private final static QName _CmdMcuGetGpio_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdMcuGetGpio");
    private final static QName _CmdSendSMS1Response_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdSendSMS1Response");
    private final static QName _CmdDelIHDTable_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdDelIHDTable");
    private final static QName _CmdStdSet1_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdStdSet1");
    private final static QName _CmdGroupAddMemberResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGroupAddMemberResponse");
    private final static QName _CmdGetMeterInfoResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetMeterInfoResponse");
    private final static QName _CmdDeleteModem_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdDeleteModem");
    private final static QName _CmdGetModemCountResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdGetModemCountResponse");
    private final static QName _CmdKDValveControl_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdKDValveControl");
    private final static QName _CmdKDValveControlResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdKDValveControlResponse");
    private final static QName _CmdKDGetMeterStatus_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdKDGetMeterStatus");
    private final static QName _CmdKDGetMeterStatusResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdKDGetMeterStatusResponse");
    private final static QName _CmdKDGetMeterVersion_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdKDGetMeterVersion");
    private final static QName _CmdKDGetMeterVersionResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdKDGetMeterVersionResponse");
    private final static QName _CmdKDSetMeterConfig_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdKDSetMeterConfig");
    private final static QName _CmdKDSetMeterConfigResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdKDSetMeterConfigResponse");
	private final static QName _CmdMcuGetLog_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdMcuGetLog");
	private final static QName _CmdMcuGetLogResponse_QNAME = new QName("http://server.ws.command.fep.aimir.com/", "cmdMcuGetLogResponse");
    
    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.aimir.fep.command.ws.client
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link MeterData }
     * 
     */
    public MeterData createMeterData() {
        return new MeterData();
    }

    /**
     * Create an instance of {@link MeterData.Map }
     * 
     */
    public MeterData.Map createMeterDataMap1() {
        return new MeterData.Map();
    }

    /**
     * Create an instance of {@link ResponseMap }
     * 
     */
    public ResponseMap createResponseMap() {
        return new ResponseMap();
    }

    /**
     * Create an instance of {@link ResponseMap.Response }
     * 
     */
    public ResponseMap.Response createResponseMapResponse() {
        return new ResponseMap.Response();
    }

    /**
     * Create an instance of {@link CmdMcuGetPlugin }
     * 
     */
    public CmdMcuGetPlugin createCmdMcuGetPlugin() {
        return new CmdMcuGetPlugin();
    }

    /**
     * Create an instance of {@link CmdCommandModem1Response }
     * 
     */
    public CmdCommandModem1Response createCmdCommandModem1Response() {
        return new CmdCommandModem1Response();
    }

    /**
     * Create an instance of {@link CmdShowTransactionListResponse }
     * 
     */
    public CmdShowTransactionListResponse createCmdShowTransactionListResponse() {
        return new CmdShowTransactionListResponse();
    }

    /**
     * Create an instance of {@link CmdGetMeterSecurityResponse }
     * 
     */
    public CmdGetMeterSecurityResponse createCmdGetMeterSecurityResponse() {
        return new CmdGetMeterSecurityResponse();
    }

    /**
     * Create an instance of {@link CmdMcuGetFileSystemResponse }
     * 
     */
    public CmdMcuGetFileSystemResponse createCmdMcuGetFileSystemResponse() {
        return new CmdMcuGetFileSystemResponse();
    }

    /**
     * Create an instance of {@link CmdMcuSetTime }
     * 
     */
    public CmdMcuSetTime createCmdMcuSetTime() {
        return new CmdMcuSetTime();
    }

    /**
     * Create an instance of {@link CmdSetIHDTable }
     * 
     */
    public CmdSetIHDTable createCmdSetIHDTable() {
        return new CmdSetIHDTable();
    }

    /**
     * Create an instance of {@link CmdGetAllLogList }
     * 
     */
    public CmdGetAllLogList createCmdGetAllLogList() {
        return new CmdGetAllLogList();
    }

    /**
     * Create an instance of {@link CmdMcuGetSystemInfoResponse }
     * 
     */
    public CmdMcuGetSystemInfoResponse createCmdMcuGetSystemInfoResponse() {
        return new CmdMcuGetSystemInfoResponse();
    }

    /**
     * Create an instance of {@link CmdBypassSensor }
     * 
     */
    public CmdBypassSensor createCmdBypassSensor() {
        return new CmdBypassSensor();
    }

    /**
     * Create an instance of {@link CmdSetIEIUConf }
     * 
     */
    public CmdSetIEIUConf createCmdSetIEIUConf() {
        return new CmdSetIEIUConf();
    }

    /**
     * Create an instance of {@link CmdGroupInfo1 }
     * 
     */
    public CmdGroupInfo1 createCmdGroupInfo1() {
        return new CmdGroupInfo1();
    }

    /**
     * Create an instance of {@link CmdGroupInfo2 }
     * 
     */
    public CmdGroupInfo2 createCmdGroupInfo2() {
        return new CmdGroupInfo2();
    }

    /**
     * Create an instance of {@link CmdMcuGetProcessResponse }
     * 
     */
    public CmdMcuGetProcessResponse createCmdMcuGetProcessResponse() {
        return new CmdMcuGetProcessResponse();
    }

    /**
     * Create an instance of {@link RequestPLCDataFrame }
     * 
     */
    public RequestPLCDataFrame createRequestPLCDataFrame() {
        return new RequestPLCDataFrame();
    }

    /**
     * Create an instance of {@link CmdSetPhoneListResponse }
     * 
     */
    public CmdSetPhoneListResponse createCmdSetPhoneListResponse() {
        return new CmdSetPhoneListResponse();
    }

    /**
     * Create an instance of {@link CmdGetLoadControlSchemeResponse }
     * 
     */
    public CmdGetLoadControlSchemeResponse createCmdGetLoadControlSchemeResponse() {
        return new CmdGetLoadControlSchemeResponse();
    }

    /**
     * Create an instance of {@link CmdGroupInfo }
     * 
     */
    public CmdGroupInfo createCmdGroupInfo() {
        return new CmdGroupInfo();
    }

    /**
     * Create an instance of {@link CmdGroupDelete }
     * 
     */
    public CmdGroupDelete createCmdGroupDelete() {
        return new CmdGroupDelete();
    }

    /**
     * Create an instance of {@link CmdKamstrupCIDResponse }
     * 
     */
    public CmdKamstrupCIDResponse createCmdKamstrupCIDResponse() {
        return new CmdKamstrupCIDResponse();
    }

    /**
     * Create an instance of {@link CmdPackageDistribution }
     * 
     */
    public CmdPackageDistribution createCmdPackageDistribution() {
        return new CmdPackageDistribution();
    }

    /**
     * Create an instance of {@link CmdMcuSetGMTResponse }
     * 
     */
    public CmdMcuSetGMTResponse createCmdMcuSetGMTResponse() {
        return new CmdMcuSetGMTResponse();
    }

    /**
     * Create an instance of {@link CmdSetLoadControlSchemeResponse }
     * 
     */
    public CmdSetLoadControlSchemeResponse createCmdSetLoadControlSchemeResponse() {
        return new CmdSetLoadControlSchemeResponse();
    }

    /**
     * Create an instance of {@link CmdGetCodiInfoResponse }
     * 
     */
    public CmdGetCodiInfoResponse createCmdGetCodiInfoResponse() {
        return new CmdGetCodiInfoResponse();
    }

    /**
     * Create an instance of {@link CmdDRAgreementResponse }
     * 
     */
    public CmdDRAgreementResponse createCmdDRAgreementResponse() {
        return new CmdDRAgreementResponse();
    }

    /**
     * Create an instance of {@link CmdMcuResetDevice }
     * 
     */
    public CmdMcuResetDevice createCmdMcuResetDevice() {
        return new CmdMcuResetDevice();
    }

    /**
     * Create an instance of {@link CmdGetMeterLogList }
     * 
     */
    public CmdGetMeterLogList createCmdGetMeterLogList() {
        return new CmdGetMeterLogList();
    }

    /**
     * Create an instance of {@link CmdSetCodiReset }
     * 
     */
    public CmdSetCodiReset createCmdSetCodiReset() {
        return new CmdSetCodiReset();
    }

    /**
     * Create an instance of {@link CmdMcuSetGpioResponse }
     * 
     */
    public CmdMcuSetGpioResponse createCmdMcuSetGpioResponse() {
        return new CmdMcuSetGpioResponse();
    }

    /**
     * Create an instance of {@link CmdGetLoadLimitScheme }
     * 
     */
    public CmdGetLoadLimitScheme createCmdGetLoadLimitScheme() {
        return new CmdGetLoadLimitScheme();
    }

    /**
     * Create an instance of {@link CmdGetMeterInfoFromModemResponse }
     * 
     */
    public CmdGetMeterInfoFromModemResponse createCmdGetMeterInfoFromModemResponse() {
        return new CmdGetMeterInfoFromModemResponse();
    }

    /**
     * Create an instance of {@link CmdGetModemEventResponse }
     * 
     */
    public CmdGetModemEventResponse createCmdGetModemEventResponse() {
        return new CmdGetModemEventResponse();
    }

    /**
     * Create an instance of {@link CmdSetEnergyLevel1Response }
     * 
     */
    public CmdSetEnergyLevel1Response createCmdSetEnergyLevel1Response() {
        return new CmdSetEnergyLevel1Response();
    }

    /**
     * Create an instance of {@link CmdReportCurMeter }
     * 
     */
    public CmdReportCurMeter createCmdReportCurMeter() {
        return new CmdReportCurMeter();
    }

    /**
     * Create an instance of {@link CmdMcuSetGpio }
     * 
     */
    public CmdMcuSetGpio createCmdMcuSetGpio() {
        return new CmdMcuSetGpio();
    }

    /**
     * Create an instance of {@link CmdDRCancel }
     * 
     */
    public CmdDRCancel createCmdDRCancel() {
        return new CmdDRCancel();
    }

    /**
     * Create an instance of {@link CmdGetCommLogListResponse }
     * 
     */
    public CmdGetCommLogListResponse createCmdGetCommLogListResponse() {
        return new CmdGetCommLogListResponse();
    }

    /**
     * Create an instance of {@link CmdMcuSetTimeResponse }
     * 
     */
    public CmdMcuSetTimeResponse createCmdMcuSetTimeResponse() {
        return new CmdMcuSetTimeResponse();
    }

    /**
     * Create an instance of {@link CmdClearEventLogResponse }
     * 
     */
    public CmdClearEventLogResponse createCmdClearEventLogResponse() {
        return new CmdClearEventLogResponse();
    }

    /**
     * Create an instance of {@link CmdSetLoadLimitPropertyResponse }
     * 
     */
    public CmdSetLoadLimitPropertyResponse createCmdSetLoadLimitPropertyResponse() {
        return new CmdSetLoadLimitPropertyResponse();
    }

    /**
     * Create an instance of {@link CmdSetDRLevel }
     * 
     */
    public CmdSetDRLevel createCmdSetDRLevel() {
        return new CmdSetDRLevel();
    }

    /**
     * Create an instance of {@link CmdValveControlResponse }
     * 
     */
    public CmdValveControlResponse createCmdValveControlResponse() {
        return new CmdValveControlResponse();
    }

    /**
     * Create an instance of {@link CmdAddModems }
     * 
     */
    public CmdAddModems createCmdAddModems() {
        return new CmdAddModems();
    }

    /**
     * Create an instance of {@link CmdGetCodiNeighbor1 }
     * 
     */
    public CmdGetCodiNeighbor1 createCmdGetCodiNeighbor1() {
        return new CmdGetCodiNeighbor1();
    }

    /**
     * Create an instance of {@link CmdClearCommLogResponse }
     * 
     */
    public CmdClearCommLogResponse createCmdClearCommLogResponse() {
        return new CmdClearCommLogResponse();
    }

    /**
     * Create an instance of {@link SendBypassOpenSMS }
     * 
     */
    public SendBypassOpenSMS createSendBypassOpenSMS() {
        return new SendBypassOpenSMS();
    }

    /**
     * Create an instance of {@link CmdGetModemAllNewResponse }
     * 
     */
    public CmdGetModemAllNewResponse createCmdGetModemAllNewResponse() {
        return new CmdGetModemAllNewResponse();
    }

    /**
     * Create an instance of {@link CmdCommandModemResponse }
     * 
     */
    public CmdCommandModemResponse createCmdCommandModemResponse() {
        return new CmdCommandModemResponse();
    }

    /**
     * Create an instance of {@link CmdGetCurMeterAll }
     * 
     */
    public CmdGetCurMeterAll createCmdGetCurMeterAll() {
        return new CmdGetCurMeterAll();
    }

    /**
     * Create an instance of {@link CmdGetGroupResponse }
     * 
     */
    public CmdGetGroupResponse createCmdGetGroupResponse() {
        return new CmdGetGroupResponse();
    }

    /**
     * Create an instance of {@link CmdCommandModem1 }
     * 
     */
    public CmdCommandModem1 createCmdCommandModem1() {
        return new CmdCommandModem1();
    }

    /**
     * Create an instance of {@link CmdGetMeterAll }
     * 
     */
    public CmdGetMeterAll createCmdGetMeterAll() {
        return new CmdGetMeterAll();
    }

    /**
     * Create an instance of {@link CmdGetCodiBinding }
     * 
     */
    public CmdGetCodiBinding createCmdGetCodiBinding() {
        return new CmdGetCodiBinding();
    }

    /**
     * Create an instance of {@link CmdGroupInfoResponse }
     * 
     */
    public CmdGroupInfoResponse createCmdGroupInfoResponse() {
        return new CmdGroupInfoResponse();
    }

    /**
     * Create an instance of {@link CmdInstallFile1Response }
     * 
     */
    public CmdInstallFile1Response createCmdInstallFile1Response() {
        return new CmdInstallFile1Response();
    }

    /**
     * Create an instance of {@link CmdACD }
     * 
     */
    public CmdACD createCmdACD() {
        return new CmdACD();
    }

    /**
     * Create an instance of {@link CmdGetLoadLimitSchemeResponse }
     * 
     */
    public CmdGetLoadLimitSchemeResponse createCmdGetLoadLimitSchemeResponse() {
        return new CmdGetLoadLimitSchemeResponse();
    }

    /**
     * Create an instance of {@link CmdDistributionResponse }
     * 
     */
    public CmdDistributionResponse createCmdDistributionResponse() {
        return new CmdDistributionResponse();
    }

    /**
     * Create an instance of {@link CmdGetCommLogList }
     * 
     */
    public CmdGetCommLogList createCmdGetCommLogList() {
        return new CmdGetCommLogList();
    }

    /**
     * Create an instance of {@link CmdSetCodiFormNetworkResponse }
     * 
     */
    public CmdSetCodiFormNetworkResponse createCmdSetCodiFormNetworkResponse() {
        return new CmdSetCodiFormNetworkResponse();
    }

    /**
     * Create an instance of {@link CmdGetCodiMemory }
     * 
     */
    public CmdGetCodiMemory createCmdGetCodiMemory() {
        return new CmdGetCodiMemory();
    }

    /**
     * Create an instance of {@link CmdGroupAsyncCall }
     * 
     */
    public CmdGroupAsyncCall createCmdGroupAsyncCall() {
        return new CmdGroupAsyncCall();
    }

    /**
     * Create an instance of {@link CmdSendSMSResponse }
     * 
     */
    public CmdSendSMSResponse createCmdSendSMSResponse() {
        return new CmdSendSMSResponse();
    }

    /**
     * Create an instance of {@link CmdGetMobileLogListResponse }
     * 
     */
    public CmdGetMobileLogListResponse createCmdGetMobileLogListResponse() {
        return new CmdGetMobileLogListResponse();
    }

    /**
     * Create an instance of {@link GetRelaySwitchStatus }
     * 
     */
    public GetRelaySwitchStatus createGetRelaySwitchStatus() {
        return new GetRelaySwitchStatus();
    }

    /**
     * Create an instance of {@link CmdOnDemandMeterAll }
     * 
     */
    public CmdOnDemandMeterAll createCmdOnDemandMeterAll() {
        return new CmdOnDemandMeterAll();
    }

    /**
     * Create an instance of {@link CmdSetCodiPermit }
     * 
     */
    public CmdSetCodiPermit createCmdSetCodiPermit() {
        return new CmdSetCodiPermit();
    }

    /**
     * Create an instance of {@link CmdOnDemandMeterResponse }
     * 
     */
    public CmdOnDemandMeterResponse createCmdOnDemandMeterResponse() {
        return new CmdOnDemandMeterResponse();
    }

    /**
     * Create an instance of {@link CmdExecuteCommandResponse }
     * 
     */
    public CmdExecuteCommandResponse createCmdExecuteCommandResponse() {
        return new CmdExecuteCommandResponse();
    }

    /**
     * Create an instance of {@link CmdModifyTransaction }
     * 
     */
    public CmdModifyTransaction createCmdModifyTransaction() {
        return new CmdModifyTransaction();
    }

    /**
     * Create an instance of {@link CmdKamstrupCID1Response }
     * 
     */
    public CmdKamstrupCID1Response createCmdKamstrupCID1Response() {
        return new CmdKamstrupCID1Response();
    }

    /**
     * Create an instance of {@link CmdGetDRLevel }
     * 
     */
    public CmdGetDRLevel createCmdGetDRLevel() {
        return new CmdGetDRLevel();
    }

    /**
     * Create an instance of {@link CmdEntryStdSet }
     * 
     */
    public CmdEntryStdSet createCmdEntryStdSet() {
        return new CmdEntryStdSet();
    }

    /**
     * Create an instance of {@link CmdDistributionCancel }
     * 
     */
    public CmdDistributionCancel createCmdDistributionCancel() {
        return new CmdDistributionCancel();
    }

    /**
     * Create an instance of {@link CmdSetLoadShedSchedule }
     * 
     */
    public CmdSetLoadShedSchedule createCmdSetLoadShedSchedule() {
        return new CmdSetLoadShedSchedule();
    }

    /**
     * Create an instance of {@link CmdMeterProgram }
     * 
     */
    public CmdMeterProgram createCmdMeterProgram() {
        return new CmdMeterProgram();
    }

    /**
     * Create an instance of {@link CmdMcuDiagnosis }
     * 
     */
    public CmdMcuDiagnosis createCmdMcuDiagnosis() {
        return new CmdMcuDiagnosis();
    }

    /**
     * Create an instance of {@link CmdSmsFirmwareUpdateResponse }
     * 
     */
    public CmdSmsFirmwareUpdateResponse createCmdSmsFirmwareUpdateResponse() {
        return new CmdSmsFirmwareUpdateResponse();
    }

    /**
     * Create an instance of {@link CmdMcuGetEnvironmentResponse }
     * 
     */
    public CmdMcuGetEnvironmentResponse createCmdMcuGetEnvironmentResponse() {
        return new CmdMcuGetEnvironmentResponse();
    }

    /**
     * Create an instance of {@link CmdGetCodiListResponse }
     * 
     */
    public CmdGetCodiListResponse createCmdGetCodiListResponse() {
        return new CmdGetCodiListResponse();
    }

    /**
     * Create an instance of {@link CmdGetCodiNeighbor1Response }
     * 
     */
    public CmdGetCodiNeighbor1Response createCmdGetCodiNeighbor1Response() {
        return new CmdGetCodiNeighbor1Response();
    }

    /**
     * Create an instance of {@link CmdMeterProgramResponse }
     * 
     */
    public CmdMeterProgramResponse createCmdMeterProgramResponse() {
        return new CmdMeterProgramResponse();
    }

    /**
     * Create an instance of {@link CmdSetEnergyLevel1 }
     * 
     */
    public CmdSetEnergyLevel1 createCmdSetEnergyLevel1() {
        return new CmdSetEnergyLevel1();
    }

    /**
     * Create an instance of {@link CmdAidonMCCBResponse }
     * 
     */
    public CmdAidonMCCBResponse createCmdAidonMCCBResponse() {
        return new CmdAidonMCCBResponse();
    }

    /**
     * Create an instance of {@link CmdStdGet1 }
     * 
     */
    public CmdStdGet1 createCmdStdGet1() {
        return new CmdStdGet1();
    }

    /**
     * Create an instance of {@link CmdOnDemandMeter2Response }
     * 
     */
    public CmdOnDemandMeter2Response createCmdOnDemandMeter2Response() {
        return new CmdOnDemandMeter2Response();
    }

    /**
     * Create an instance of {@link CmdAddModemsResponse }
     * 
     */
    public CmdAddModemsResponse createCmdAddModemsResponse() {
        return new CmdAddModemsResponse();
    }

    /**
     * Create an instance of {@link RequestPLCDataFrameResponse }
     * 
     */
    public RequestPLCDataFrameResponse createRequestPLCDataFrameResponse() {
        return new RequestPLCDataFrameResponse();
    }

    /**
     * Create an instance of {@link CmdShowTransactionList }
     * 
     */
    public CmdShowTransactionList createCmdShowTransactionList() {
        return new CmdShowTransactionList();
    }

    /**
     * Create an instance of {@link CmdGetMeterVersion }
     * 
     */
    public CmdGetMeterVersion createCmdGetMeterVersion() {
        return new CmdGetMeterVersion();
    }

    /**
     * Create an instance of {@link CmdOnDemandMeter }
     * 
     */
    public CmdOnDemandMeter createCmdOnDemandMeter() {
        return new CmdOnDemandMeter();
    }

    /**
     * Create an instance of {@link CmdValveControl }
     * 
     */
    public CmdValveControl createCmdValveControl() {
        return new CmdValveControl();
    }

    /**
     * Create an instance of {@link CmdGetCurMeterAllResponse }
     * 
     */
    public CmdGetCurMeterAllResponse createCmdGetCurMeterAllResponse() {
        return new CmdGetCurMeterAllResponse();
    }

    /**
     * Create an instance of {@link CmdGroupInfo1Response }
     * 
     */
    public CmdGroupInfo1Response createCmdGroupInfo1Response() {
        return new CmdGroupInfo1Response();
    }

    /**
     * Create an instance of {@link CmdRecoveryResponse }
     * 
     */
    public CmdRecoveryResponse createCmdRecoveryResponse() {
        return new CmdRecoveryResponse();
    }

    /**
     * Create an instance of {@link CmdGroupAdd }
     * 
     */
    public CmdGroupAdd createCmdGroupAdd() {
        return new CmdGroupAdd();
    }

    /**
     * Create an instance of {@link CmdGetCodiMemory1 }
     * 
     */
    public CmdGetCodiMemory1 createCmdGetCodiMemory1() {
        return new CmdGetCodiMemory1();
    }

    /**
     * Create an instance of {@link CmdPackageDistributionResponse }
     * 
     */
    public CmdPackageDistributionResponse createCmdPackageDistributionResponse() {
        return new CmdPackageDistributionResponse();
    }

    /**
     * Create an instance of {@link CmdSetModemAmrDataResponse }
     * 
     */
    public CmdSetModemAmrDataResponse createCmdSetModemAmrDataResponse() {
        return new CmdSetModemAmrDataResponse();
    }

    /**
     * Create an instance of {@link CmdSendIHDData }
     * 
     */
    public CmdSendIHDData createCmdSendIHDData() {
        return new CmdSendIHDData();
    }

    /**
     * Create an instance of {@link CmdIDRCancel }
     * 
     */
    public CmdIDRCancel createCmdIDRCancel() {
        return new CmdIDRCancel();
    }

    /**
     * Create an instance of {@link CmdGetCodiInfo }
     * 
     */
    public CmdGetCodiInfo createCmdGetCodiInfo() {
        return new CmdGetCodiInfo();
    }

    /**
     * Create an instance of {@link CmdGetModemBatteryResponse }
     * 
     */
    public CmdGetModemBatteryResponse createCmdGetModemBatteryResponse() {
        return new CmdGetModemBatteryResponse();
    }

    /**
     * Create an instance of {@link CmdClearCmdHistLog }
     * 
     */
    public CmdClearCmdHistLog createCmdClearCmdHistLog() {
        return new CmdClearCmdHistLog();
    }

    /**
     * Create an instance of {@link CmdSetMeterTime }
     * 
     */
    public CmdSetMeterTime createCmdSetMeterTime() {
        return new CmdSetMeterTime();
    }

    /**
     * Create an instance of {@link CmdGetMeterResponse }
     * 
     */
    public CmdGetMeterResponse createCmdGetMeterResponse() {
        return new CmdGetMeterResponse();
    }

    /**
     * Create an instance of {@link CmdResetModem }
     * 
     */
    public CmdResetModem createCmdResetModem() {
        return new CmdResetModem();
    }

    /**
     * Create an instance of {@link CmdDeleteModemResponse }
     * 
     */
    public CmdDeleteModemResponse createCmdDeleteModemResponse() {
        return new CmdDeleteModemResponse();
    }

    /**
     * Create an instance of {@link CmdDistributionState }
     * 
     */
    public CmdDistributionState createCmdDistributionState() {
        return new CmdDistributionState();
    }

    /**
     * Create an instance of {@link CmdSetTOUCalendar }
     * 
     */
    public CmdSetTOUCalendar createCmdSetTOUCalendar() {
        return new CmdSetTOUCalendar();
    }

    /**
     * Create an instance of {@link CmdSetMeterConfig }
     * 
     */
    public CmdSetMeterConfig createCmdSetMeterConfig() {
        return new CmdSetMeterConfig();
    }

    /**
     * Create an instance of {@link CmdGroupDeleteResponse }
     * 
     */
    public CmdGroupDeleteResponse createCmdGroupDeleteResponse() {
        return new CmdGroupDeleteResponse();
    }

    /**
     * Create an instance of {@link CmdUpdateGroup }
     * 
     */
    public CmdUpdateGroup createCmdUpdateGroup() {
        return new CmdUpdateGroup();
    }

    /**
     * Create an instance of {@link CmdDeleteModemByChannelPanResponse }
     * 
     */
    public CmdDeleteModemByChannelPanResponse createCmdDeleteModemByChannelPanResponse() {
        return new CmdDeleteModemByChannelPanResponse();
    }

    /**
     * Create an instance of {@link CmdMcuFactoryDefaultResponse }
     * 
     */
    public CmdMcuFactoryDefaultResponse createCmdMcuFactoryDefaultResponse() {
        return new CmdMcuFactoryDefaultResponse();
    }

    /**
     * Create an instance of {@link CmdMcuGetTimeResponse }
     * 
     */
    public CmdMcuGetTimeResponse createCmdMcuGetTimeResponse() {
        return new CmdMcuGetTimeResponse();
    }

    /**
     * Create an instance of {@link CmdDigitalInOutResponse }
     * 
     */
    public CmdDigitalInOutResponse createCmdDigitalInOutResponse() {
        return new CmdDigitalInOutResponse();
    }

    /**
     * Create an instance of {@link CmdMcuDiagnosisResponse }
     * 
     */
    public CmdMcuDiagnosisResponse createCmdMcuDiagnosisResponse() {
        return new CmdMcuDiagnosisResponse();
    }

    /**
     * Create an instance of {@link CmdGetCodiDevice }
     * 
     */
    public CmdGetCodiDevice createCmdGetCodiDevice() {
        return new CmdGetCodiDevice();
    }

    /**
     * Create an instance of {@link CmdEndDeviceControl }
     * 
     */
    public CmdEndDeviceControl createCmdEndDeviceControl() {
        return new CmdEndDeviceControl();
    }

    /**
     * Create an instance of {@link CmdClearCommLog }
     * 
     */
    public CmdClearCommLog createCmdClearCommLog() {
        return new CmdClearCommLog();
    }

    /**
     * Create an instance of {@link CmdDistributionStateResponse }
     * 
     */
    public CmdDistributionStateResponse createCmdDistributionStateResponse() {
        return new CmdDistributionStateResponse();
    }

    /**
     * Create an instance of {@link CmdMcuGetFileSystem }
     * 
     */
    public CmdMcuGetFileSystem createCmdMcuGetFileSystem() {
        return new CmdMcuGetFileSystem();
    }

    /**
     * Create an instance of {@link CmdDeleteLoadControlSchemeResponse }
     * 
     */
    public CmdDeleteLoadControlSchemeResponse createCmdDeleteLoadControlSchemeResponse() {
        return new CmdDeleteLoadControlSchemeResponse();
    }

    /**
     * Create an instance of {@link CmdSetCodiReset1Response }
     * 
     */
    public CmdSetCodiReset1Response createCmdSetCodiReset1Response() {
        return new CmdSetCodiReset1Response();
    }

    /**
     * Create an instance of {@link CmdIDRStart }
     * 
     */
    public CmdIDRStart createCmdIDRStart() {
        return new CmdIDRStart();
    }

    /**
     * Create an instance of {@link CmdGetDRAssetInfoResponse }
     * 
     */
    public CmdGetDRAssetInfoResponse createCmdGetDRAssetInfoResponse() {
        return new CmdGetDRAssetInfoResponse();
    }

    /**
     * Create an instance of {@link CmdDeleteLoadShedScheme }
     * 
     */
    public CmdDeleteLoadShedScheme createCmdDeleteLoadShedScheme() {
        return new CmdDeleteLoadShedScheme();
    }

    /**
     * Create an instance of {@link CmdSetTOUCalendarResponse }
     * 
     */
    public CmdSetTOUCalendarResponse createCmdSetTOUCalendarResponse() {
        return new CmdSetTOUCalendarResponse();
    }

    /**
     * Create an instance of {@link CmdClearMeterLog }
     * 
     */
    public CmdClearMeterLog createCmdClearMeterLog() {
        return new CmdClearMeterLog();
    }

    /**
     * Create an instance of {@link CmdSetCodiResetResponse }
     * 
     */
    public CmdSetCodiResetResponse createCmdSetCodiResetResponse() {
        return new CmdSetCodiResetResponse();
    }

    /**
     * Create an instance of {@link CmdRelaySwitchAndActivateResponse }
     * 
     */
    public CmdRelaySwitchAndActivateResponse createCmdRelaySwitchAndActivateResponse() {
        return new CmdRelaySwitchAndActivateResponse();
    }

    /**
     * Create an instance of {@link CmdMcuSetGMT }
     * 
     */
    public CmdMcuSetGMT createCmdMcuSetGMT() {
        return new CmdMcuSetGMT();
    }

    /**
     * Create an instance of {@link CmdAidonMCCB }
     * 
     */
    public CmdAidonMCCB createCmdAidonMCCB() {
        return new CmdAidonMCCB();
    }

    /**
     * Create an instance of {@link CmdGroupAddResponse }
     * 
     */
    public CmdGroupAddResponse createCmdGroupAddResponse() {
        return new CmdGroupAddResponse();
    }

    /**
     * Create an instance of {@link CmdSetLoadLimitScheme }
     * 
     */
    public CmdSetLoadLimitScheme createCmdSetLoadLimitScheme() {
        return new CmdSetLoadLimitScheme();
    }

    /**
     * Create an instance of {@link CmdUpdateModemFirmwareResponse }
     * 
     */
    public CmdUpdateModemFirmwareResponse createCmdUpdateModemFirmwareResponse() {
        return new CmdUpdateModemFirmwareResponse();
    }

    /**
     * Create an instance of {@link CmdEndDeviceControlResponse }
     * 
     */
    public CmdEndDeviceControlResponse createCmdEndDeviceControlResponse() {
        return new CmdEndDeviceControlResponse();
    }

    /**
     * Create an instance of {@link CmdSetMeterSecurityResponse }
     * 
     */
    public CmdSetMeterSecurityResponse createCmdSetMeterSecurityResponse() {
        return new CmdSetMeterSecurityResponse();
    }

    /**
     * Create an instance of {@link CmdGetCodiDevice1Response }
     * 
     */
    public CmdGetCodiDevice1Response createCmdGetCodiDevice1Response() {
        return new CmdGetCodiDevice1Response();
    }

    /**
     * Create an instance of {@link CmdDeleteGroupResponse }
     * 
     */
    public CmdDeleteGroupResponse createCmdDeleteGroupResponse() {
        return new CmdDeleteGroupResponse();
    }

    /**
     * Create an instance of {@link CmdGetCodiMemory1Response }
     * 
     */
    public CmdGetCodiMemory1Response createCmdGetCodiMemory1Response() {
        return new CmdGetCodiMemory1Response();
    }

    /**
     * Create an instance of {@link DoGetModemROM }
     * 
     */
    public DoGetModemROM createDoGetModemROM() {
        return new DoGetModemROM();
    }

    /**
     * Create an instance of {@link CmdGetModemAmrDataResponse }
     * 
     */
    public CmdGetModemAmrDataResponse createCmdGetModemAmrDataResponse() {
        return new CmdGetModemAmrDataResponse();
    }

    /**
     * Create an instance of {@link CmdMcuLoopback }
     * 
     */
    public CmdMcuLoopback createCmdMcuLoopback() {
        return new CmdMcuLoopback();
    }

    /**
     * Create an instance of {@link CmdRecovery }
     * 
     */
    public CmdRecovery createCmdRecovery() {
        return new CmdRecovery();
    }

    /**
     * Create an instance of {@link CmdMcuGetMemoryResponse }
     * 
     */
    public CmdMcuGetMemoryResponse createCmdMcuGetMemoryResponse() {
        return new CmdMcuGetMemoryResponse();
    }

    /**
     * Create an instance of {@link CmdCorrectModemPulse }
     * 
     */
    public CmdCorrectModemPulse createCmdCorrectModemPulse() {
        return new CmdCorrectModemPulse();
    }

    /**
     * Create an instance of {@link CmdDRAgreement }
     * 
     */
    public CmdDRAgreement createCmdDRAgreement() {
        return new CmdDRAgreement();
    }

    /**
     * Create an instance of {@link CmdGroupAsyncCallResponse }
     * 
     */
    public CmdGroupAsyncCallResponse createCmdGroupAsyncCallResponse() {
        return new CmdGroupAsyncCallResponse();
    }

    /**
     * Create an instance of {@link CmdGroupDeleteMemberResponse }
     * 
     */
    public CmdGroupDeleteMemberResponse createCmdGroupDeleteMemberResponse() {
        return new CmdGroupDeleteMemberResponse();
    }

    /**
     * Create an instance of {@link CmdGetDRAssetResponse }
     * 
     */
    public CmdGetDRAssetResponse createCmdGetDRAssetResponse() {
        return new CmdGetDRAssetResponse();
    }

    /**
     * Create an instance of {@link CmdGetMeterLogListResponse }
     * 
     */
    public CmdGetMeterLogListResponse createCmdGetMeterLogListResponse() {
        return new CmdGetMeterLogListResponse();
    }

    /**
     * Create an instance of {@link CmdGetPhoneList }
     * 
     */
    public CmdGetPhoneList createCmdGetPhoneList() {
        return new CmdGetPhoneList();
    }

    /**
     * Create an instance of {@link CmdDeleteLoadLimitSchemeResponse }
     * 
     */
    public CmdDeleteLoadLimitSchemeResponse createCmdDeleteLoadLimitSchemeResponse() {
        return new CmdDeleteLoadLimitSchemeResponse();
    }

    /**
     * Create an instance of {@link CmdGetDRLevelResponse }
     * 
     */
    public CmdGetDRLevelResponse createCmdGetDRLevelResponse() {
        return new CmdGetDRLevelResponse();
    }

    /**
     * Create an instance of {@link CmdOnDemandMeterAllResponse }
     * 
     */
    public CmdOnDemandMeterAllResponse createCmdOnDemandMeterAllResponse() {
        return new CmdOnDemandMeterAllResponse();
    }

    /**
     * Create an instance of {@link CmdEntryStdSetResponse }
     * 
     */
    public CmdEntryStdSetResponse createCmdEntryStdSetResponse() {
        return new CmdEntryStdSetResponse();
    }

    /**
     * Create an instance of {@link CmdGetEventLogListResponse }
     * 
     */
    public CmdGetEventLogListResponse createCmdGetEventLogListResponse() {
        return new CmdGetEventLogListResponse();
    }

    /**
     * Create an instance of {@link CmdSetLoadLimitSchemeResponse }
     * 
     */
    public CmdSetLoadLimitSchemeResponse createCmdSetLoadLimitSchemeResponse() {
        return new CmdSetLoadLimitSchemeResponse();
    }

    /**
     * Create an instance of {@link Exception }
     * 
     */
    public Exception createException() {
        return new Exception();
    }

    /**
     * Create an instance of {@link CmdModifyTransactionResponse }
     * 
     */
    public CmdModifyTransactionResponse createCmdModifyTransactionResponse() {
        return new CmdModifyTransactionResponse();
    }

    /**
     * Create an instance of {@link CmdInstallFileResponse }
     * 
     */
    public CmdInstallFileResponse createCmdInstallFileResponse() {
        return new CmdInstallFileResponse();
    }

    /**
     * Create an instance of {@link CmdBypassMeterProgramResponse }
     * 
     */
    public CmdBypassMeterProgramResponse createCmdBypassMeterProgramResponse() {
        return new CmdBypassMeterProgramResponse();
    }

    /**
     * Create an instance of {@link CmdGetFile }
     * 
     */
    public CmdGetFile createCmdGetFile() {
        return new CmdGetFile();
    }

    /**
     * Create an instance of {@link CmdGetModemCount }
     * 
     */
    public CmdGetModemCount createCmdGetModemCount() {
        return new CmdGetModemCount();
    }

    /**
     * Create an instance of {@link CmdGetConfiguration }
     * 
     */
    public CmdGetConfiguration createCmdGetConfiguration() {
        return new CmdGetConfiguration();
    }

    /**
     * Create an instance of {@link CmdOnDemandMeterAsyncResponse }
     * 
     */
    public CmdOnDemandMeterAsyncResponse createCmdOnDemandMeterAsyncResponse() {
        return new CmdOnDemandMeterAsyncResponse();
    }

    /**
     * Create an instance of {@link CmdAsynchronousCall }
     * 
     */
    public CmdAsynchronousCall createCmdAsynchronousCall() {
        return new CmdAsynchronousCall();
    }

    /**
     * Create an instance of {@link CmdGetModemROMResponse }
     * 
     */
    public CmdGetModemROMResponse createCmdGetModemROMResponse() {
        return new CmdGetModemROMResponse();
    }

    /**
     * Create an instance of {@link CmdMcuResetResponse }
     * 
     */
    public CmdMcuResetResponse createCmdMcuResetResponse() {
        return new CmdMcuResetResponse();
    }

    /**
     * Create an instance of {@link CmdDeleteModemAll }
     * 
     */
    public CmdDeleteModemAll createCmdDeleteModemAll() {
        return new CmdDeleteModemAll();
    }

    /**
     * Create an instance of {@link CmdMcuShutdownResponse }
     * 
     */
    public CmdMcuShutdownResponse createCmdMcuShutdownResponse() {
        return new CmdMcuShutdownResponse();
    }

    /**
     * Create an instance of {@link CmdMcuClearStaticResponse }
     * 
     */
    public CmdMcuClearStaticResponse createCmdMcuClearStaticResponse() {
        return new CmdMcuClearStaticResponse();
    }

    /**
     * Create an instance of {@link CmdSetIEIUConfResponse }
     * 
     */
    public CmdSetIEIUConfResponse createCmdSetIEIUConfResponse() {
        return new CmdSetIEIUConfResponse();
    }

    /**
     * Create an instance of {@link CmdSetLoadControlScheme }
     * 
     */
    public CmdSetLoadControlScheme createCmdSetLoadControlScheme() {
        return new CmdSetLoadControlScheme();
    }

    /**
     * Create an instance of {@link CmdSetEnergyLevel }
     * 
     */
    public CmdSetEnergyLevel createCmdSetEnergyLevel() {
        return new CmdSetEnergyLevel();
    }

    /**
     * Create an instance of {@link CmdMeteringAll }
     * 
     */
    public CmdMeteringAll createCmdMeteringAll() {
        return new CmdMeteringAll();
    }

    /**
     * Create an instance of {@link CmdMcuGetPluginResponse }
     * 
     */
    public CmdMcuGetPluginResponse createCmdMcuGetPluginResponse() {
        return new CmdMcuGetPluginResponse();
    }

    /**
     * Create an instance of {@link CmdGetCodiBindingResponse }
     * 
     */
    public CmdGetCodiBindingResponse createCmdGetCodiBindingResponse() {
        return new CmdGetCodiBindingResponse();
    }

    /**
     * Create an instance of {@link CmdGetLoadLimitPropertyResponse }
     * 
     */
    public CmdGetLoadLimitPropertyResponse createCmdGetLoadLimitPropertyResponse() {
        return new CmdGetLoadLimitPropertyResponse();
    }

    /**
     * Create an instance of {@link CmdGetModemAmrData }
     * 
     */
    public CmdGetModemAmrData createCmdGetModemAmrData() {
        return new CmdGetModemAmrData();
    }

    /**
     * Create an instance of {@link CmdMeterFactoryReset }
     * 
     */
    public CmdMeterFactoryReset createCmdMeterFactoryReset() {
        return new CmdMeterFactoryReset();
    }

    /**
     * Create an instance of {@link CmdWriteTableResponse }
     * 
     */
    public CmdWriteTableResponse createCmdWriteTableResponse() {
        return new CmdWriteTableResponse();
    }

    /**
     * Create an instance of {@link CmdEntryStdGetResponse }
     * 
     */
    public CmdEntryStdGetResponse createCmdEntryStdGetResponse() {
        return new CmdEntryStdGetResponse();
    }

    /**
     * Create an instance of {@link CmdMcuGetIpResponse }
     * 
     */
    public CmdMcuGetIpResponse createCmdMcuGetIpResponse() {
        return new CmdMcuGetIpResponse();
    }

    /**
     * Create an instance of {@link CmdMcuClearStatic }
     * 
     */
    public CmdMcuClearStatic createCmdMcuClearStatic() {
        return new CmdMcuClearStatic();
    }

    /**
     * Create an instance of {@link CmdGetCodiNeighbor }
     * 
     */
    public CmdGetCodiNeighbor createCmdGetCodiNeighbor() {
        return new CmdGetCodiNeighbor();
    }

    /**
     * Create an instance of {@link CmdSetModemAmrData }
     * 
     */
    public CmdSetModemAmrData createCmdSetModemAmrData() {
        return new CmdSetModemAmrData();
    }

    /**
     * Create an instance of {@link CmdOnDemandMeter2 }
     * 
     */
    public CmdOnDemandMeter2 createCmdOnDemandMeter2() {
        return new CmdOnDemandMeter2();
    }

    /**
     * Create an instance of {@link CmdOnDemandMeter3 }
     * 
     */
    public CmdOnDemandMeter3 createCmdOnDemandMeter3() {
        return new CmdOnDemandMeter3();
    }

    /**
     * Create an instance of {@link CmdMcuGetProcess }
     * 
     */
    public CmdMcuGetProcess createCmdMcuGetProcess() {
        return new CmdMcuGetProcess();
    }

    /**
     * Create an instance of {@link CmdGetCodiBinding1Response }
     * 
     */
    public CmdGetCodiBinding1Response createCmdGetCodiBinding1Response() {
        return new CmdGetCodiBinding1Response();
    }

    /**
     * Create an instance of {@link CmdDigitalInOut }
     * 
     */
    public CmdDigitalInOut createCmdDigitalInOut() {
        return new CmdDigitalInOut();
    }

    /**
     * Create an instance of {@link CmdSensorLPLogRecoveryResponse }
     * 
     */
    public CmdSensorLPLogRecoveryResponse createCmdSensorLPLogRecoveryResponse() {
        return new CmdSensorLPLogRecoveryResponse();
    }

    /**
     * Create an instance of {@link CmdReadTable }
     * 
     */
    public CmdReadTable createCmdReadTable() {
        return new CmdReadTable();
    }

    /**
     * Create an instance of {@link CmdDeleteModemByProperty }
     * 
     */
    public CmdDeleteModemByProperty createCmdDeleteModemByProperty() {
        return new CmdDeleteModemByProperty();
    }

    /**
     * Create an instance of {@link DoGetModemCluster }
     * 
     */
    public DoGetModemCluster createDoGetModemCluster() {
        return new DoGetModemCluster();
    }

    /**
     * Create an instance of {@link CmdGetFFDList }
     * 
     */
    public CmdGetFFDList createCmdGetFFDList() {
        return new CmdGetFFDList();
    }

    /**
     * Create an instance of {@link CmdPutFileResponse }
     * 
     */
    public CmdPutFileResponse createCmdPutFileResponse() {
        return new CmdPutFileResponse();
    }

    /**
     * Create an instance of {@link CmdGetLoadShedScheduleResponse }
     * 
     */
    public CmdGetLoadShedScheduleResponse createCmdGetLoadShedScheduleResponse() {
        return new CmdGetLoadShedScheduleResponse();
    }

    /**
     * Create an instance of {@link CmdGetDRAsset }
     * 
     */
    public CmdGetDRAsset createCmdGetDRAsset() {
        return new CmdGetDRAsset();
    }

    /**
     * Create an instance of {@link CmdDeleteLoadShedSchemeResponse }
     * 
     */
    public CmdDeleteLoadShedSchemeResponse createCmdDeleteLoadShedSchemeResponse() {
        return new CmdDeleteLoadShedSchemeResponse();
    }

    /**
     * Create an instance of {@link CmdGetMeterScheduleResponse }
     * 
     */
    public CmdGetMeterScheduleResponse createCmdGetMeterScheduleResponse() {
        return new CmdGetMeterScheduleResponse();
    }

    /**
     * Create an instance of {@link CmdDeleteModemByPropertyResponse }
     * 
     */
    public CmdDeleteModemByPropertyResponse createCmdDeleteModemByPropertyResponse() {
        return new CmdDeleteModemByPropertyResponse();
    }

    /**
     * Create an instance of {@link CmdFirmwareUpdate }
     * 
     */
    public CmdFirmwareUpdate createCmdFirmwareUpdate() {
        return new CmdFirmwareUpdate();
    }

    /**
     * Create an instance of {@link CmdGetMeter }
     * 
     */
    public CmdGetMeter createCmdGetMeter() {
        return new CmdGetMeter();
    }

    /**
     * Create an instance of {@link CmdSendMessageResponse }
     * 
     */
    public CmdSendMessageResponse createCmdSendMessageResponse() {
        return new CmdSendMessageResponse();
    }

    /**
     * Create an instance of {@link CmdGetEnergyLevel }
     * 
     */
    public CmdGetEnergyLevel createCmdGetEnergyLevel() {
        return new CmdGetEnergyLevel();
    }

    /**
     * Create an instance of {@link CmdGetModemBattery }
     * 
     */
    public CmdGetModemBattery createCmdGetModemBattery() {
        return new CmdGetModemBattery();
    }

    /**
     * Create an instance of {@link CmdSetPhoneList }
     * 
     */
    public CmdSetPhoneList createCmdSetPhoneList() {
        return new CmdSetPhoneList();
    }

    /**
     * Create an instance of {@link CmdGetCmdHistListResponse }
     * 
     */
    public CmdGetCmdHistListResponse createCmdGetCmdHistListResponse() {
        return new CmdGetCmdHistListResponse();
    }

    /**
     * Create an instance of {@link CmdSetEnergyLevelResponse }
     * 
     */
    public CmdSetEnergyLevelResponse createCmdSetEnergyLevelResponse() {
        return new CmdSetEnergyLevelResponse();
    }

    /**
     * Create an instance of {@link CmdSetDRLevelResponse }
     * 
     */
    public CmdSetDRLevelResponse createCmdSetDRLevelResponse() {
        return new CmdSetDRLevelResponse();
    }

    /**
     * Create an instance of {@link CmdMcuFactoryDefault }
     * 
     */
    public CmdMcuFactoryDefault createCmdMcuFactoryDefault() {
        return new CmdMcuFactoryDefault();
    }

    /**
     * Create an instance of {@link CmdRelaySwitchAndActivate }
     * 
     */
    public CmdRelaySwitchAndActivate createCmdRelaySwitchAndActivate() {
        return new CmdRelaySwitchAndActivate();
    }

    /**
     * Create an instance of {@link CmdMcuGetMobile }
     * 
     */
    public CmdMcuGetMobile createCmdMcuGetMobile() {
        return new CmdMcuGetMobile();
    }

    /**
     * Create an instance of {@link CmdAddModem }
     * 
     */
    public CmdAddModem createCmdAddModem() {
        return new CmdAddModem();
    }

    /**
     * Create an instance of {@link CmdRecoveryByTargetTimeResponse }
     * 
     */
    public CmdRecoveryByTargetTimeResponse createCmdRecoveryByTargetTimeResponse() {
        return new CmdRecoveryByTargetTimeResponse();
    }

    /**
     * Create an instance of {@link CmdResetModemResponse }
     * 
     */
    public CmdResetModemResponse createCmdResetModemResponse() {
        return new CmdResetModemResponse();
    }

    /**
     * Create an instance of {@link CmdOnRecoveryDemandMeterResponse }
     * 
     */
    public CmdOnRecoveryDemandMeterResponse createCmdOnRecoveryDemandMeterResponse() {
        return new CmdOnRecoveryDemandMeterResponse();
    }

    /**
     * Create an instance of {@link CmdGetMeterInfoFromModem }
     * 
     */
    public CmdGetMeterInfoFromModem createCmdGetMeterInfoFromModem() {
        return new CmdGetMeterInfoFromModem();
    }

    /**
     * Create an instance of {@link CmdDeleteLoadLimitScheme }
     * 
     */
    public CmdDeleteLoadLimitScheme createCmdDeleteLoadLimitScheme() {
        return new CmdDeleteLoadLimitScheme();
    }

    /**
     * Create an instance of {@link CmdMcuGetSystemInfo }
     * 
     */
    public CmdMcuGetSystemInfo createCmdMcuGetSystemInfo() {
        return new CmdMcuGetSystemInfo();
    }

    /**
     * Create an instance of {@link CmdDeleteModelAllByChannelPan }
     * 
     */
    public CmdDeleteModelAllByChannelPan createCmdDeleteModelAllByChannelPan() {
        return new CmdDeleteModelAllByChannelPan();
    }

    /**
     * Create an instance of {@link CmdShowTransactionInfo }
     * 
     */
    public CmdShowTransactionInfo createCmdShowTransactionInfo() {
        return new CmdShowTransactionInfo();
    }

    /**
     * Create an instance of {@link CmdGetLoadControlScheme }
     * 
     */
    public CmdGetLoadControlScheme createCmdGetLoadControlScheme() {
        return new CmdGetLoadControlScheme();
    }

    /**
     * Create an instance of {@link CmdGetConfigurationResponse }
     * 
     */
    public CmdGetConfigurationResponse createCmdGetConfigurationResponse() {
        return new CmdGetConfigurationResponse();
    }

    /**
     * Create an instance of {@link CmdShowTransactionInfoResponse }
     * 
     */
    public CmdShowTransactionInfoResponse createCmdShowTransactionInfoResponse() {
        return new CmdShowTransactionInfoResponse();
    }

    /**
     * Create an instance of {@link CmdSetModemROM }
     * 
     */
    public CmdSetModemROM createCmdSetModemROM() {
        return new CmdSetModemROM();
    }

    /**
     * Create an instance of {@link SendBypassOpenSMSResponse }
     * 
     */
    public SendBypassOpenSMSResponse createSendBypassOpenSMSResponse() {
        return new SendBypassOpenSMSResponse();
    }

    /**
     * Create an instance of {@link CmdIDRStartResponse }
     * 
     */
    public CmdIDRStartResponse createCmdIDRStartResponse() {
        return new CmdIDRStartResponse();
    }

    /**
     * Create an instance of {@link CmdDeleteLoadControlScheme }
     * 
     */
    public CmdDeleteLoadControlScheme createCmdDeleteLoadControlScheme() {
        return new CmdDeleteLoadControlScheme();
    }

    /**
     * Create an instance of {@link CmdStdGetChildResponse }
     * 
     */
    public CmdStdGetChildResponse createCmdStdGetChildResponse() {
        return new CmdStdGetChildResponse();
    }

    /**
     * Create an instance of {@link CmdStdGetChild }
     * 
     */
    public CmdStdGetChild createCmdStdGetChild() {
        return new CmdStdGetChild();
    }

    /**
     * Create an instance of {@link CmdDistribution }
     * 
     */
    public CmdDistribution createCmdDistribution() {
        return new CmdDistribution();
    }

    /**
     * Create an instance of {@link CmdGetCodiList }
     * 
     */
    public CmdGetCodiList createCmdGetCodiList() {
        return new CmdGetCodiList();
    }

    /**
     * Create an instance of {@link CmdACDResponse }
     * 
     */
    public CmdACDResponse createCmdACDResponse() {
        return new CmdACDResponse();
    }

    /**
     * Create an instance of {@link CmdMcuGetTime }
     * 
     */
    public CmdMcuGetTime createCmdMcuGetTime() {
        return new CmdMcuGetTime();
    }

    /**
     * Create an instance of {@link CmdStdSet }
     * 
     */
    public CmdStdSet createCmdStdSet() {
        return new CmdStdSet();
    }

    /**
     * Create an instance of {@link CmdGetModemROM1 }
     * 
     */
    public CmdGetModemROM1 createCmdGetModemROM1() {
        return new CmdGetModemROM1();
    }

    /**
     * Create an instance of {@link CmdBypassSensor1 }
     * 
     */
    public CmdBypassSensor1 createCmdBypassSensor1() {
        return new CmdBypassSensor1();
    }

    /**
     * Create an instance of {@link CmdGetMeterTimeResponse }
     * 
     */
    public CmdGetMeterTimeResponse createCmdGetMeterTimeResponse() {
        return new CmdGetMeterTimeResponse();
    }

    /**
     * Create an instance of {@link CmdSensorLPLogRecovery }
     * 
     */
    public CmdSensorLPLogRecovery createCmdSensorLPLogRecovery() {
        return new CmdSensorLPLogRecovery();
    }

    /**
     * Create an instance of {@link CmdMcuGetIp }
     * 
     */
    public CmdMcuGetIp createCmdMcuGetIp() {
        return new CmdMcuGetIp();
    }

    /**
     * Create an instance of {@link CmdMeterFactoryResetResponse }
     * 
     */
    public CmdMeterFactoryResetResponse createCmdMeterFactoryResetResponse() {
        return new CmdMeterFactoryResetResponse();
    }

    /**
     * Create an instance of {@link CmdGetLoadShedSchedule }
     * 
     */
    public CmdGetLoadShedSchedule createCmdGetLoadShedSchedule() {
        return new CmdGetLoadShedSchedule();
    }

    /**
     * Create an instance of {@link CmdGetFileResponse }
     * 
     */
    public CmdGetFileResponse createCmdGetFileResponse() {
        return new CmdGetFileResponse();
    }

    /**
     * Create an instance of {@link CmdGetPhoneListResponse }
     * 
     */
    public CmdGetPhoneListResponse createCmdGetPhoneListResponse() {
        return new CmdGetPhoneListResponse();
    }

    /**
     * Create an instance of {@link CmdDeleteGroup }
     * 
     */
    public CmdDeleteGroup createCmdDeleteGroup() {
        return new CmdDeleteGroup();
    }

    /**
     * Create an instance of {@link CmdGetCodiNeighborResponse }
     * 
     */
    public CmdGetCodiNeighborResponse createCmdGetCodiNeighborResponse() {
        return new CmdGetCodiNeighborResponse();
    }

    /**
     * Create an instance of {@link CmdSetMeterConfigResponse }
     * 
     */
    public CmdSetMeterConfigResponse createCmdSetMeterConfigResponse() {
        return new CmdSetMeterConfigResponse();
    }

    /**
     * Create an instance of {@link CmdSetCodiFormNetwork }
     * 
     */
    public CmdSetCodiFormNetwork createCmdSetCodiFormNetwork() {
        return new CmdSetCodiFormNetwork();
    }

    /**
     * Create an instance of {@link CmdSetLoadLimitProperty }
     * 
     */
    public CmdSetLoadLimitProperty createCmdSetLoadLimitProperty() {
        return new CmdSetLoadLimitProperty();
    }

    /**
     * Create an instance of {@link CmdRecoveryByTargetTime }
     * 
     */
    public CmdRecoveryByTargetTime createCmdRecoveryByTargetTime() {
        return new CmdRecoveryByTargetTime();
    }

    /**
     * Create an instance of {@link CmdSetMeterSecurity }
     * 
     */
    public CmdSetMeterSecurity createCmdSetMeterSecurity() {
        return new CmdSetMeterSecurity();
    }

    /**
     * Create an instance of {@link CmdDeleteMemberAll }
     * 
     */
    public CmdDeleteMemberAll createCmdDeleteMemberAll() {
        return new CmdDeleteMemberAll();
    }

    /**
     * Create an instance of {@link CmdMeteringResponse }
     * 
     */
    public CmdMeteringResponse createCmdMeteringResponse() {
        return new CmdMeteringResponse();
    }

    /**
     * Create an instance of {@link CmdDeleteModelAllByChannelPanResponse }
     * 
     */
    public CmdDeleteModelAllByChannelPanResponse createCmdDeleteModelAllByChannelPanResponse() {
        return new CmdDeleteModelAllByChannelPanResponse();
    }

    /**
     * Create an instance of {@link CmdBypassSensorResponse }
     * 
     */
    public CmdBypassSensorResponse createCmdBypassSensorResponse() {
        return new CmdBypassSensorResponse();
    }

    /**
     * Create an instance of {@link CmdOnDemandMeterAsync }
     * 
     */
    public CmdOnDemandMeterAsync createCmdOnDemandMeterAsync() {
        return new CmdOnDemandMeterAsync();
    }

    /**
     * Create an instance of {@link CmdKamstrupCID1 }
     * 
     */
    public CmdKamstrupCID1 createCmdKamstrupCID1() {
        return new CmdKamstrupCID1();
    }

    /**
     * Create an instance of {@link CmdGroupAddMember }
     * 
     */
    public CmdGroupAddMember createCmdGroupAddMember() {
        return new CmdGroupAddMember();
    }

    /**
     * Create an instance of {@link CmdDistributionCancelResponse }
     * 
     */
    public CmdDistributionCancelResponse createCmdDistributionCancelResponse() {
        return new CmdDistributionCancelResponse();
    }

    /**
     * Create an instance of {@link CmdBypassMeterProgram }
     * 
     */
    public CmdBypassMeterProgram createCmdBypassMeterProgram() {
        return new CmdBypassMeterProgram();
    }

    /**
     * Create an instance of {@link CmdDeleteMemberAllResponse }
     * 
     */
    public CmdDeleteMemberAllResponse createCmdDeleteMemberAllResponse() {
        return new CmdDeleteMemberAllResponse();
    }

    /**
     * Create an instance of {@link CmdDRCancelResponse }
     * 
     */
    public CmdDRCancelResponse createCmdDRCancelResponse() {
        return new CmdDRCancelResponse();
    }

    /**
     * Create an instance of {@link CmdStdGet1Response }
     * 
     */
    public CmdStdGet1Response createCmdStdGet1Response() {
        return new CmdStdGet1Response();
    }

    /**
     * Create an instance of {@link CmdReportCurMeterResponse }
     * 
     */
    public CmdReportCurMeterResponse createCmdReportCurMeterResponse() {
        return new CmdReportCurMeterResponse();
    }

    /**
     * Create an instance of {@link CmdGetMobileLogList }
     * 
     */
    public CmdGetMobileLogList createCmdGetMobileLogList() {
        return new CmdGetMobileLogList();
    }

    /**
     * Create an instance of {@link CmdMcuSetDSTResponse }
     * 
     */
    public CmdMcuSetDSTResponse createCmdMcuSetDSTResponse() {
        return new CmdMcuSetDSTResponse();
    }

    /**
     * Create an instance of {@link CmdGetEventLogList }
     * 
     */
    public CmdGetEventLogList createCmdGetEventLogList() {
        return new CmdGetEventLogList();
    }

    /**
     * Create an instance of {@link CmdGetCodiInfo1Response }
     * 
     */
    public CmdGetCodiInfo1Response createCmdGetCodiInfo1Response() {
        return new CmdGetCodiInfo1Response();
    }

    /**
     * Create an instance of {@link CmdDeleteModemByChannelPan }
     * 
     */
    public CmdDeleteModemByChannelPan createCmdDeleteModemByChannelPan() {
        return new CmdDeleteModemByChannelPan();
    }

    /**
     * Create an instance of {@link CmdSendSMS1 }
     * 
     */
    public CmdSendSMS1 createCmdSendSMS1() {
        return new CmdSendSMS1();
    }

    /**
     * Create an instance of {@link CmdGetModemAllNew }
     * 
     */
    public CmdGetModemAllNew createCmdGetModemAllNew() {
        return new CmdGetModemAllNew();
    }

    /**
     * Create an instance of {@link CmdMeteringAllResponse }
     * 
     */
    public CmdMeteringAllResponse createCmdMeteringAllResponse() {
        return new CmdMeteringAllResponse();
    }

    /**
     * Create an instance of {@link CmdSendMessage }
     * 
     */
    public CmdSendMessage createCmdSendMessage() {
        return new CmdSendMessage();
    }

    /**
     * Create an instance of {@link CmdGroupDeleteMember }
     * 
     */
    public CmdGroupDeleteMember createCmdGroupDeleteMember() {
        return new CmdGroupDeleteMember();
    }

    /**
     * Create an instance of {@link CmdGetEnergyLevelResponse }
     * 
     */
    public CmdGetEnergyLevelResponse createCmdGetEnergyLevelResponse() {
        return new CmdGetEnergyLevelResponse();
    }

    /**
     * Create an instance of {@link CmdGetAllLogListResponse }
     * 
     */
    public CmdGetAllLogListResponse createCmdGetAllLogListResponse() {
        return new CmdGetAllLogListResponse();
    }

    /**
     * Create an instance of {@link CmdDelIHDTable }
     * 
     */
    public CmdDelIHDTable createCmdDelIHDTable() {
        return new CmdDelIHDTable();
    }

    /**
     * Create an instance of {@link CmdStdSet1 }
     * 
     */
    public CmdStdSet1 createCmdStdSet1() {
        return new CmdStdSet1();
    }

    /**
     * Create an instance of {@link CmdGetModemCountResponse }
     * 
     */
    public CmdGetModemCountResponse createCmdGetModemCountResponse() {
        return new CmdGetModemCountResponse();
    }

    /**
     * Create an instance of {@link CmdDeleteModem }
     * 
     */
    public CmdDeleteModem createCmdDeleteModem() {
        return new CmdDeleteModem();
    }

    /**
     * Create an instance of {@link CmdKDValveControl }
     * 
     */
    public CmdKDValveControl createCmdKDValveControl() {
        return new CmdKDValveControl();
    }
    
    /**
     * Create an instance of {@link CmdKDGetMeterStatus }
     * 
     */
    public CmdKDGetMeterStatus createCmdKDGetMeterStatus() {
        return new CmdKDGetMeterStatus();
    }
    
    /**
     * Create an instance of {@link CmdKDGetMeterVersion }
     * 
     */
    public CmdKDGetMeterVersion createCmdKDGetMeterVersion() {
        return new CmdKDGetMeterVersion();
    }
    
    /**
     * Create an instance of {@link CmdKDSetMeterConfig }
     * 
     */
    public CmdKDSetMeterConfig createCmdKDSetMeterConfig() {
        return new CmdKDSetMeterConfig();
    }
    
    /**
     * Create an instance of {@link CmdGetMeterInfoResponse }
     * 
     */
    public CmdGetMeterInfoResponse createCmdGetMeterInfoResponse() {
        return new CmdGetMeterInfoResponse();
    }
    
    /**
     * Create an instance of {@link CmdKDValveControlResponse }
     * 
     */
    public CmdKDValveControlResponse createCmdKDValveControlResponse() {
        return new CmdKDValveControlResponse();
    }
    
    /**
     * Create an instance of {@link CmdKDGetMeterStatusResponse }
     * 
     */
    public CmdKDGetMeterStatusResponse createCmdKDGetMeterStatusResponse() {
        return new CmdKDGetMeterStatusResponse();
    }
    
    /**
     * Create an instance of {@link CmdKDGetMeterVersionResponse }
     * 
     */
    public CmdKDGetMeterVersionResponse createCmdKDGetMeterVersionResponse() {
        return new CmdKDGetMeterVersionResponse();
    }
    
    /**
     * Create an instance of {@link CmdKDSetMeterConfigResponse }
     * 
     */
    public CmdKDSetMeterConfigResponse createCmdKDSetMeterConfigResponse() {
        return new CmdKDSetMeterConfigResponse();
    }

    /**
     * Create an instance of {@link CmdGroupAddMemberResponse }
     * 
     */
    public CmdGroupAddMemberResponse createCmdGroupAddMemberResponse() {
        return new CmdGroupAddMemberResponse();
    }

    /**
     * Create an instance of {@link CmdMeterUploadRange }
     * 
     */
    public CmdMeterUploadRange createCmdMeterUploadRange() {
        return new CmdMeterUploadRange();
    }

    /**
     * Create an instance of {@link CmdSendSMS1Response }
     * 
     */
    public CmdSendSMS1Response createCmdSendSMS1Response() {
        return new CmdSendSMS1Response();
    }

    /**
     * Create an instance of {@link CmdMcuGetGpio }
     * 
     */
    public CmdMcuGetGpio createCmdMcuGetGpio() {
        return new CmdMcuGetGpio();
    }

    /**
     * Create an instance of {@link CmdUpdateModemFirmware }
     * 
     */
    public CmdUpdateModemFirmware createCmdUpdateModemFirmware() {
        return new CmdUpdateModemFirmware();
    }

    /**
     * Create an instance of {@link CmdDemandReset }
     * 
     */
    public CmdDemandReset createCmdDemandReset() {
        return new CmdDemandReset();
    }

    /**
     * Create an instance of {@link CmdClearEventLog }
     * 
     */
    public CmdClearEventLog createCmdClearEventLog() {
        return new CmdClearEventLog();
    }

    /**
     * Create an instance of {@link CmdCommandModem }
     * 
     */
    public CmdCommandModem createCmdCommandModem() {
        return new CmdCommandModem();
    }

    /**
     * Create an instance of {@link CmdGetGroup }
     * 
     */
    public CmdGetGroup createCmdGetGroup() {
        return new CmdGetGroup();
    }

    /**
     * Create an instance of {@link CmdGetCodiBinding1 }
     * 
     */
    public CmdGetCodiBinding1 createCmdGetCodiBinding1() {
        return new CmdGetCodiBinding1();
    }

    /**
     * Create an instance of {@link CmdOnDemandMBus }
     * 
     */
    public CmdOnDemandMBus createCmdOnDemandMBus() {
        return new CmdOnDemandMBus();
    }

    /**
     * Create an instance of {@link CmdMcuScanningResponse }
     * 
     */
    public CmdMcuScanningResponse createCmdMcuScanningResponse() {
        return new CmdMcuScanningResponse();
    }

    /**
     * Create an instance of {@link CmdSendIHDDataResponse }
     * 
     */
    public CmdSendIHDDataResponse createCmdSendIHDDataResponse() {
        return new CmdSendIHDDataResponse();
    }

    /**
     * Create an instance of {@link CmdMcuGetGpioResponse }
     * 
     */
    public CmdMcuGetGpioResponse createCmdMcuGetGpioResponse() {
        return new CmdMcuGetGpioResponse();
    }

    /**
     * Create an instance of {@link CmdCorrectModemPulseResponse }
     * 
     */
    public CmdCorrectModemPulseResponse createCmdCorrectModemPulseResponse() {
        return new CmdCorrectModemPulseResponse();
    }

    /**
     * Create an instance of {@link CmdSetModemROMResponse }
     * 
     */
    public CmdSetModemROMResponse createCmdSetModemROMResponse() {
        return new CmdSetModemROMResponse();
    }

    /**
     * Create an instance of {@link CmdGetMeterInfo }
     * 
     */
    public CmdGetMeterInfo createCmdGetMeterInfo() {
        return new CmdGetMeterInfo();
    }

    /**
     * Create an instance of {@link CmdMeterUploadRangeResponse }
     * 
     */
    public CmdMeterUploadRangeResponse createCmdMeterUploadRangeResponse() {
        return new CmdMeterUploadRangeResponse();
    }

    /**
     * Create an instance of {@link CmdStdSetResponse }
     * 
     */
    public CmdStdSetResponse createCmdStdSetResponse() {
        return new CmdStdSetResponse();
    }

    /**
     * Create an instance of {@link CmdStdGetResponse }
     * 
     */
    public CmdStdGetResponse createCmdStdGetResponse() {
        return new CmdStdGetResponse();
    }

    /**
     * Create an instance of {@link CmdSetCodiPermitResponse }
     * 
     */
    public CmdSetCodiPermitResponse createCmdSetCodiPermitResponse() {
        return new CmdSetCodiPermitResponse();
    }

    /**
     * Create an instance of {@link CmdSetConfiguration }
     * 
     */
    public CmdSetConfiguration createCmdSetConfiguration() {
        return new CmdSetConfiguration();
    }

    /**
     * Create an instance of {@link CmdMcuGetState }
     * 
     */
    public CmdMcuGetState createCmdMcuGetState() {
        return new CmdMcuGetState();
    }

    /**
     * Create an instance of {@link CmdGetMeterAllResponse }
     * 
     */
    public CmdGetMeterAllResponse createCmdGetMeterAllResponse() {
        return new CmdGetMeterAllResponse();
    }

    /**
     * Create an instance of {@link CmdSetCiuLCDResponse }
     * 
     */
    public CmdSetCiuLCDResponse createCmdSetCiuLCDResponse() {
        return new CmdSetCiuLCDResponse();
    }

    /**
     * Create an instance of {@link CmdGetDRAssetInfo }
     * 
     */
    public CmdGetDRAssetInfo createCmdGetDRAssetInfo() {
        return new CmdGetDRAssetInfo();
    }

    /**
     * Create an instance of {@link GetRelaySwitchStatusResponse }
     * 
     */
    public GetRelaySwitchStatusResponse createGetRelaySwitchStatusResponse() {
        return new GetRelaySwitchStatusResponse();
    }

    /**
     * Create an instance of {@link CmdEntryStdGet }
     * 
     */
    public CmdEntryStdGet createCmdEntryStdGet() {
        return new CmdEntryStdGet();
    }

    /**
     * Create an instance of {@link CmdInstallFile1 }
     * 
     */
    public CmdInstallFile1 createCmdInstallFile1() {
        return new CmdInstallFile1();
    }

    /**
     * Create an instance of {@link CmdGetCmdHistList }
     * 
     */
    public CmdGetCmdHistList createCmdGetCmdHistList() {
        return new CmdGetCmdHistList();
    }

    /**
     * Create an instance of {@link CmdDelIHDTableResponse }
     * 
     */
    public CmdDelIHDTableResponse createCmdDelIHDTableResponse() {
        return new CmdDelIHDTableResponse();
    }

    /**
     * Create an instance of {@link CmdInstallFile }
     * 
     */
    public CmdInstallFile createCmdInstallFile() {
        return new CmdInstallFile();
    }

    /**
     * Create an instance of {@link CmdBypassSensor1Response }
     * 
     */
    public CmdBypassSensor1Response createCmdBypassSensor1Response() {
        return new CmdBypassSensor1Response();
    }

    /**
     * Create an instance of {@link CmdGetModemEvent }
     * 
     */
    public CmdGetModemEvent createCmdGetModemEvent() {
        return new CmdGetModemEvent();
    }

    /**
     * Create an instance of {@link CmdMeterTimeSyncResponse }
     * 
     */
    public CmdMeterTimeSyncResponse createCmdMeterTimeSyncResponse() {
        return new CmdMeterTimeSyncResponse();
    }

    /**
     * Create an instance of {@link CmdOnDemandMBusResponse }
     * 
     */
    public CmdOnDemandMBusResponse createCmdOnDemandMBusResponse() {
        return new CmdOnDemandMBusResponse();
    }

    /**
     * Create an instance of {@link CmdGetCodiDevice1 }
     * 
     */
    public CmdGetCodiDevice1 createCmdGetCodiDevice1() {
        return new CmdGetCodiDevice1();
    }

    /**
     * Create an instance of {@link CmdGetMeterSecurity }
     * 
     */
    public CmdGetMeterSecurity createCmdGetMeterSecurity() {
        return new CmdGetMeterSecurity();
    }

    /**
     * Create an instance of {@link CmdMcuGetLog }
     * 
     */
    public CmdMcuGetLog createCmdMcuGetLog() {
        return new CmdMcuGetLog();
    }

    /**
     * Create an instance of {@link CmdMcuGetLogResponse }
     * 
     */
    public CmdMcuGetLogResponse createCmdMcuGetLogResponse() {
        return new CmdMcuGetLogResponse();
    }

    /**
     * Create an instance of {@link CmdGetMeterTime }
     * 
     */
    public CmdGetMeterTime createCmdGetMeterTime() {
        return new CmdGetMeterTime();
    }

    /**
     * Create an instance of {@link CmdGetModemROM }
     * 
     */
    public CmdGetModemROM createCmdGetModemROM() {
        return new CmdGetModemROM();
    }

    /**
     * Create an instance of {@link CmdIDRCancelResponse }
     * 
     */
    public CmdIDRCancelResponse createCmdIDRCancelResponse() {
        return new CmdIDRCancelResponse();
    }

    /**
     * Create an instance of {@link CmdStdSet1Response }
     * 
     */
    public CmdStdSet1Response createCmdStdSet1Response() {
        return new CmdStdSet1Response();
    }

    /**
     * Create an instance of {@link CmdOnDemandMeter3Response }
     * 
     */
    public CmdOnDemandMeter3Response createCmdOnDemandMeter3Response() {
        return new CmdOnDemandMeter3Response();
    }

    /**
     * Create an instance of {@link CmdGetMeterVersionResponse }
     * 
     */
    public CmdGetMeterVersionResponse createCmdGetMeterVersionResponse() {
        return new CmdGetMeterVersionResponse();
    }

    /**
     * Create an instance of {@link CmdAddModemResponse }
     * 
     */
    public CmdAddModemResponse createCmdAddModemResponse() {
        return new CmdAddModemResponse();
    }

    /**
     * Create an instance of {@link CmdGetCurMeterResponse }
     * 
     */
    public CmdGetCurMeterResponse createCmdGetCurMeterResponse() {
        return new CmdGetCurMeterResponse();
    }

    /**
     * Create an instance of {@link CmdDemandResetResponse }
     * 
     */
    public CmdDemandResetResponse createCmdDemandResetResponse() {
        return new CmdDemandResetResponse();
    }

    /**
     * Create an instance of {@link CmdOnRecoveryDemandMeter }
     * 
     */
    public CmdOnRecoveryDemandMeter createCmdOnRecoveryDemandMeter() {
        return new CmdOnRecoveryDemandMeter();
    }

    /**
     * Create an instance of {@link CmdDeleteModemAllResponse }
     * 
     */
    public CmdDeleteModemAllResponse createCmdDeleteModemAllResponse() {
        return new CmdDeleteModemAllResponse();
    }

    /**
     * Create an instance of {@link CmdGetMeterSchedule }
     * 
     */
    public CmdGetMeterSchedule createCmdGetMeterSchedule() {
        return new CmdGetMeterSchedule();
    }

    /**
     * Create an instance of {@link CmdSetConfigurationResponse }
     * 
     */
    public CmdSetConfigurationResponse createCmdSetConfigurationResponse() {
        return new CmdSetConfigurationResponse();
    }

    /**
     * Create an instance of {@link CmdSendSMS }
     * 
     */
    public CmdSendSMS createCmdSendSMS() {
        return new CmdSendSMS();
    }

    /**
     * Create an instance of {@link CmdMetering }
     * 
     */
    public CmdMetering createCmdMetering() {
        return new CmdMetering();
    }

    /**
     * Create an instance of {@link CmdMeterTimeSync }
     * 
     */
    public CmdMeterTimeSync createCmdMeterTimeSync() {
        return new CmdMeterTimeSync();
    }

    /**
     * Create an instance of {@link CmdSmsFirmwareUpdate }
     * 
     */
    public CmdSmsFirmwareUpdate createCmdSmsFirmwareUpdate() {
        return new CmdSmsFirmwareUpdate();
    }

    /**
     * Create an instance of {@link CmdMcuShutdown }
     * 
     */
    public CmdMcuShutdown createCmdMcuShutdown() {
        return new CmdMcuShutdown();
    }

    /**
     * Create an instance of {@link CmdGetModemROM1Response }
     * 
     */
    public CmdGetModemROM1Response createCmdGetModemROM1Response() {
        return new CmdGetModemROM1Response();
    }

    /**
     * Create an instance of {@link CmdSetCodiReset1 }
     * 
     */
    public CmdSetCodiReset1 createCmdSetCodiReset1() {
        return new CmdSetCodiReset1();
    }

    /**
     * Create an instance of {@link CmdPutFile }
     * 
     */
    public CmdPutFile createCmdPutFile() {
        return new CmdPutFile();
    }

    /**
     * Create an instance of {@link CmdAsynchronousCallResponse }
     * 
     */
    public CmdAsynchronousCallResponse createCmdAsynchronousCallResponse() {
        return new CmdAsynchronousCallResponse();
    }

    /**
     * Create an instance of {@link CmdKamstrupCID }
     * 
     */
    public CmdKamstrupCID createCmdKamstrupCID() {
        return new CmdKamstrupCID();
    }

    /**
     * Create an instance of {@link CmdClearCmdHistLogResponse }
     * 
     */
    public CmdClearCmdHistLogResponse createCmdClearCmdHistLogResponse() {
        return new CmdClearCmdHistLogResponse();
    }

    /**
     * Create an instance of {@link CmdWriteTable }
     * 
     */
    public CmdWriteTable createCmdWriteTable() {
        return new CmdWriteTable();
    }

    /**
     * Create an instance of {@link DoGetModemROMResponse }
     * 
     */
    public DoGetModemROMResponse createDoGetModemROMResponse() {
        return new DoGetModemROMResponse();
    }

    /**
     * Create an instance of {@link CmdGetLoadLimitProperty }
     * 
     */
    public CmdGetLoadLimitProperty createCmdGetLoadLimitProperty() {
        return new CmdGetLoadLimitProperty();
    }

    /**
     * Create an instance of {@link CmdMcuReset }
     * 
     */
    public CmdMcuReset createCmdMcuReset() {
        return new CmdMcuReset();
    }

    /**
     * Create an instance of {@link CmdMcuResetDeviceResponse }
     * 
     */
    public CmdMcuResetDeviceResponse createCmdMcuResetDeviceResponse() {
        return new CmdMcuResetDeviceResponse();
    }

    /**
     * Create an instance of {@link CmdSetIHDTableResponse }
     * 
     */
    public CmdSetIHDTableResponse createCmdSetIHDTableResponse() {
        return new CmdSetIHDTableResponse();
    }

    /**
     * Create an instance of {@link CmdSetMeterTimeResponse }
     * 
     */
    public CmdSetMeterTimeResponse createCmdSetMeterTimeResponse() {
        return new CmdSetMeterTimeResponse();
    }

    /**
     * Create an instance of {@link CmdStdGet }
     * 
     */
    public CmdStdGet createCmdStdGet() {
        return new CmdStdGet();
    }

    /**
     * Create an instance of {@link DoGetModemClusterResponse }
     * 
     */
    public DoGetModemClusterResponse createDoGetModemClusterResponse() {
        return new DoGetModemClusterResponse();
    }

    /**
     * Create an instance of {@link CmdUpdateGroupResponse }
     * 
     */
    public CmdUpdateGroupResponse createCmdUpdateGroupResponse() {
        return new CmdUpdateGroupResponse();
    }

    /**
     * Create an instance of {@link CmdMcuLoopbackResponse }
     * 
     */
    public CmdMcuLoopbackResponse createCmdMcuLoopbackResponse() {
        return new CmdMcuLoopbackResponse();
    }

    /**
     * Create an instance of {@link CmdClearMeterLogResponse }
     * 
     */
    public CmdClearMeterLogResponse createCmdClearMeterLogResponse() {
        return new CmdClearMeterLogResponse();
    }

    /**
     * Create an instance of {@link CmdMcuGetMobileResponse }
     * 
     */
    public CmdMcuGetMobileResponse createCmdMcuGetMobileResponse() {
        return new CmdMcuGetMobileResponse();
    }

    /**
     * Create an instance of {@link CmdGroupInfo2Response }
     * 
     */
    public CmdGroupInfo2Response createCmdGroupInfo2Response() {
        return new CmdGroupInfo2Response();
    }

    /**
     * Create an instance of {@link CmdMcuScanning }
     * 
     */
    public CmdMcuScanning createCmdMcuScanning() {
        return new CmdMcuScanning();
    }

    /**
     * Create an instance of {@link CmdMcuGetEnvironment }
     * 
     */
    public CmdMcuGetEnvironment createCmdMcuGetEnvironment() {
        return new CmdMcuGetEnvironment();
    }

    /**
     * Create an instance of {@link CmdExecuteCommand }
     * 
     */
    public CmdExecuteCommand createCmdExecuteCommand() {
        return new CmdExecuteCommand();
    }

    /**
     * Create an instance of {@link CmdMcuGetMemory }
     * 
     */
    public CmdMcuGetMemory createCmdMcuGetMemory() {
        return new CmdMcuGetMemory();
    }

    /**
     * Create an instance of {@link CmdGetCurMeter }
     * 
     */
    public CmdGetCurMeter createCmdGetCurMeter() {
        return new CmdGetCurMeter();
    }

    /**
     * Create an instance of {@link CmdSetCiuLCD }
     * 
     */
    public CmdSetCiuLCD createCmdSetCiuLCD() {
        return new CmdSetCiuLCD();
    }

    /**
     * Create an instance of {@link CmdMcuSetDST }
     * 
     */
    public CmdMcuSetDST createCmdMcuSetDST() {
        return new CmdMcuSetDST();
    }

    /**
     * Create an instance of {@link CmdReadTableResponse }
     * 
     */
    public CmdReadTableResponse createCmdReadTableResponse() {
        return new CmdReadTableResponse();
    }

    /**
     * Create an instance of {@link CmdMcuGetStateResponse }
     * 
     */
    public CmdMcuGetStateResponse createCmdMcuGetStateResponse() {
        return new CmdMcuGetStateResponse();
    }

    /**
     * Create an instance of {@link CmdSetLoadShedScheduleResponse }
     * 
     */
    public CmdSetLoadShedScheduleResponse createCmdSetLoadShedScheduleResponse() {
        return new CmdSetLoadShedScheduleResponse();
    }

    /**
     * Create an instance of {@link CmdGetCodiDeviceResponse }
     * 
     */
    public CmdGetCodiDeviceResponse createCmdGetCodiDeviceResponse() {
        return new CmdGetCodiDeviceResponse();
    }

    /**
     * Create an instance of {@link CmdGetCodiMemoryResponse }
     * 
     */
    public CmdGetCodiMemoryResponse createCmdGetCodiMemoryResponse() {
        return new CmdGetCodiMemoryResponse();
    }

    /**
     * Create an instance of {@link CmdFirmwareUpdateResponse }
     * 
     */
    public CmdFirmwareUpdateResponse createCmdFirmwareUpdateResponse() {
        return new CmdFirmwareUpdateResponse();
    }

    /**
     * Create an instance of {@link CmdBroadcastResponse }
     * 
     */
    public CmdBroadcastResponse createCmdBroadcastResponse() {
        return new CmdBroadcastResponse();
    }

    /**
     * Create an instance of {@link CmdGetFFDListResponse }
     * 
     */
    public CmdGetFFDListResponse createCmdGetFFDListResponse() {
        return new CmdGetFFDListResponse();
    }

    /**
     * Create an instance of {@link CmdGetCodiInfo1 }
     * 
     */
    public CmdGetCodiInfo1 createCmdGetCodiInfo1() {
        return new CmdGetCodiInfo1();
    }

    /**
     * Create an instance of {@link CmdBroadcast }
     * 
     */
    public CmdBroadcast createCmdBroadcast() {
        return new CmdBroadcast();
    }

    /**
     * Create an instance of {@link CodiBindingEntry }
     * 
     */
    public codiBindingEntry createCodiBindingEntry() {
        return new codiBindingEntry();
    }

    /**
     * Create an instance of {@link LoadControlSchemeEntry }
     * 
     */
    public loadControlSchemeEntry createLoadControlSchemeEntry() {
        return new loadControlSchemeEntry();
    }

    /**
     * Create an instance of {@link Oid }
     * 
     */
    public OID createOid() {
        return new OID();
    }

    /**
     * Create an instance of {@link ProcEntry }
     * 
     */
    public procEntry createProcEntry() {
        return new procEntry();
    }

    /**
     * Create an instance of {@link GroupInfo }
     * 
     */
    public GroupInfo createGroupInfo() {
        return new GroupInfo();
    }

    /**
     * Create an instance of {@link PluginEntry }
     * 
     */
    public pluginEntry createPluginEntry() {
        return new pluginEntry();
    }

    /**
     * Create an instance of {@link LpData }
     * 
     */
    public LPData createLpData() {
        return new LPData();
    }

    /**
     * Create an instance of {@link com.aimir.fep.command.ws.client.Response }
     * 
     */
    public com.aimir.fep.command.ws.client.Response createResponse() {
        return new com.aimir.fep.command.ws.client.Response();
    }

    /**
     * Create an instance of {@link SysEntry }
     * 
     */
    public sysEntry createSysEntry() {
        return new sysEntry();
    }

    /**
     * Create an instance of {@link PlcDataFrame }
     * 
     */
    public PLCDataFrame createPlcDataFrame() {
        return new PLCDataFrame();
    }

    /**
     * Create an instance of {@link Hex }
     * 
     */
    public HEX createHex() {
        return new HEX();
    }

    /**
     * Create an instance of {@link Byte }
     * 
     */
    public BYTE createByte() {
        return new BYTE();
    }

    /**
     * Create an instance of {@link CodiDeviceEntry }
     * 
     */
    public codiDeviceEntry createCodiDeviceEntry() {
        return new codiDeviceEntry();
    }

    /**
     * Create an instance of {@link FileInfo }
     * 
     */
    public FileInfo createFileInfo() {
        return new FileInfo();
    }

    /**
     * Create an instance of {@link BatteryLog }
     * 
     */
    public BatteryLog createBatteryLog() {
        return new BatteryLog();
    }

    /**
     * Create an instance of {@link Short }
     * 
     */
    public SHORT createShort() {
        return new SHORT();
    }

    /**
     * Create an instance of {@link LoadLimitPropertyEntry }
     * 
     */
    public loadLimitPropertyEntry createLoadLimitPropertyEntry() {
        return new loadLimitPropertyEntry();
    }

    /**
     * Create an instance of {@link FfdEntry }
     * 
     */
    public ffdEntry createFfdEntry() {
        return new ffdEntry();
    }

    /**
     * Create an instance of {@link SensorInfoNewEntry }
     * 
     */
    public sensorInfoNewEntry createSensorInfoNewEntry() {
        return new sensorInfoNewEntry();
    }

    /**
     * Create an instance of {@link Octet }
     * 
     */
    public OCTET createOctet() {
        return new OCTET();
    }

    /**
     * Create an instance of {@link Location }
     * 
     */
    public Location createLocation() {
        return new Location();
    }

    /**
     * Create an instance of {@link Char }
     * 
     */
    public CHAR createChar() {
        return new CHAR();
    }

    /**
     * Create an instance of {@link LoadShedSchemeEntry }
     * 
     */
    public loadShedSchemeEntry createLoadShedSchemeEntry() {
        return new loadShedSchemeEntry();
    }

    /**
     * Create an instance of {@link Int }
     * 
     */
    public INT createInt() {
        return new INT();
    }

    /**
     * Create an instance of {@link AmrData }
     * 
     */
    public AmrData createAmrData() {
        return new AmrData();
    }

    /**
     * Create an instance of {@link CodiNeighborEntry }
     * 
     */
    public codiNeighborEntry createCodiNeighborEntry() {
        return new codiNeighborEntry();
    }

    /**
     * Create an instance of {@link EventLog }
     * 
     */
    public EventLog createEventLog() {
        return new EventLog();
    }

    /**
     * Create an instance of {@link CodiEntry }
     * 
     */
    public codiEntry createCodiEntry() {
        return new codiEntry();
    }

    /**
     * Create an instance of {@link TimeEntry }
     * 
     */
    public timeEntry createTimeEntry() {
        return new timeEntry();
    }

    /**
     * Create an instance of {@link DrLevelEntry }
     * 
     */
    public drLevelEntry createDrLevelEntry() {
        return new drLevelEntry();
    }

    /**
     * Create an instance of {@link MobileEntry }
     * 
     */
    public mobileEntry createMobileEntry() {
        return new mobileEntry();
    }

    /**
     * Create an instance of {@link Uint }
     * 
     */
    public UINT createUint() {
        return new UINT();
    }

    /**
     * Create an instance of {@link MemberInfo }
     * 
     */
    public MemberInfo createMemberInfo() {
        return new MemberInfo();
    }

    /**
     * Create an instance of {@link TrInfoEntry }
     * 
     */
    public trInfoEntry createTrInfoEntry() {
        return new trInfoEntry();
    }

    /**
     * Create an instance of {@link CodiMemoryEntry }
     * 
     */
    public codiMemoryEntry createCodiMemoryEntry() {
        return new codiMemoryEntry();
    }

    /**
     * Create an instance of {@link LoadLimitSchemeEntry }
     * 
     */
    public loadLimitSchemeEntry createLoadLimitSchemeEntry() {
        return new loadLimitSchemeEntry();
    }

    /**
     * Create an instance of {@link Timestamp }
     * 
     */
    public TIMESTAMP createTimestamp() {
        return new TIMESTAMP();
    }

    /**
     * Create an instance of {@link SmiValue }
     * 
     */
    public SMIValue createSmiValue() {
        return new SMIValue();
    }

    /**
     * Create an instance of {@link Modem }
     * 
     */
    public Modem createModem() {
        return new Modem();
    }

    /**
     * Create an instance of {@link ModemNetwork }
     * 
     */
    public ModemNetwork createModemNetwork() {
        return new ModemNetwork();
    }

    /**
     * Create an instance of {@link Ipaddr }
     * 
     */
    public IPADDR createIpaddr() {
        return new IPADDR();
    }

    /**
     * Create an instance of {@link ModemNode }
     * 
     */
    public ModemNode createModemNode() {
        return new ModemNode();
    }

    /**
     * Create an instance of {@link MeterProgram }
     * 
     */
    public MeterProgram createMeterProgram() {
        return new MeterProgram();
    }

    /**
     * Create an instance of {@link MemEntry }
     * 
     */
    public memEntry createMemEntry() {
        return new memEntry();
    }

    /**
     * Create an instance of {@link GpioEntry }
     * 
     */
    public gpioEntry createGpioEntry() {
        return new gpioEntry();
    }

    /**
     * Create an instance of {@link IdrEntry }
     * 
     */
    public idrEntry createIdrEntry() {
        return new idrEntry();
    }

    /**
     * Create an instance of {@link EndDeviceEntry }
     * 
     */
    public endDeviceEntry createEndDeviceEntry() {
        return new endDeviceEntry();
    }

    /**
     * Create an instance of {@link Word }
     * 
     */
    public WORD createWord() {
        return new WORD();
    }

    /**
     * Create an instance of {@link Target }
     * 
     */
    public Target createTarget() {
        return new Target();
    }

    /**
     * Create an instance of {@link Bool }
     * 
     */
    public BOOL createBool() {
        return new BOOL();
    }

    /**
     * Create an instance of {@link IntArray }
     * 
     */
    public IntArray createIntArray() {
        return new IntArray();
    }

    /**
     * Create an instance of {@link StringArray }
     * 
     */
    public StringArray createStringArray() {
        return new StringArray();
    }

    /**
     * Create an instance of {@link MeterData.Map.Entry }
     * 
     */
    public MeterData.Map.Entry createMeterDataMapEntry1() {
        return new MeterData.Map.Entry();
    }

    /**
     * Create an instance of {@link ResponseMap.Response.Entry }
     * 
     */
    public ResponseMap.Response.Entry createResponseMapResponseEntry() {
        return new ResponseMap.Response.Entry();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdMcuFactoryDefaultResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdMcuFactoryDefaultResponse")
    public JAXBElement<CmdMcuFactoryDefaultResponse> createCmdMcuFactoryDefaultResponse(CmdMcuFactoryDefaultResponse value) {
        return new JAXBElement<CmdMcuFactoryDefaultResponse>(_CmdMcuFactoryDefaultResponse_QNAME, CmdMcuFactoryDefaultResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdDeleteModemByChannelPanResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdDeleteModemByChannelPanResponse")
    public JAXBElement<CmdDeleteModemByChannelPanResponse> createCmdDeleteModemByChannelPanResponse(CmdDeleteModemByChannelPanResponse value) {
        return new JAXBElement<CmdDeleteModemByChannelPanResponse>(_CmdDeleteModemByChannelPanResponse_QNAME, CmdDeleteModemByChannelPanResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdMcuGetTimeResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdMcuGetTimeResponse")
    public JAXBElement<CmdMcuGetTimeResponse> createCmdMcuGetTimeResponse(CmdMcuGetTimeResponse value) {
        return new JAXBElement<CmdMcuGetTimeResponse>(_CmdMcuGetTimeResponse_QNAME, CmdMcuGetTimeResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdDistributionState }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdDistributionState")
    public JAXBElement<CmdDistributionState> createCmdDistributionState(CmdDistributionState value) {
        return new JAXBElement<CmdDistributionState>(_CmdDistributionState_QNAME, CmdDistributionState.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdSetMeterConfig }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdSetMeterConfig")
    public JAXBElement<CmdSetMeterConfig> createCmdSetMeterConfig(CmdSetMeterConfig value) {
        return new JAXBElement<CmdSetMeterConfig>(_CmdSetMeterConfig_QNAME, CmdSetMeterConfig.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGroupDeleteResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGroupDeleteResponse")
    public JAXBElement<CmdGroupDeleteResponse> createCmdGroupDeleteResponse(CmdGroupDeleteResponse value) {
        return new JAXBElement<CmdGroupDeleteResponse>(_CmdGroupDeleteResponse_QNAME, CmdGroupDeleteResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdSetTOUCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdSetTOUCalendar")
    public JAXBElement<CmdSetTOUCalendar> createCmdSetTOUCalendar(CmdSetTOUCalendar value) {
        return new JAXBElement<CmdSetTOUCalendar>(_CmdSetTOUCalendar_QNAME, CmdSetTOUCalendar.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdUpdateGroup }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdUpdateGroup")
    public JAXBElement<CmdUpdateGroup> createCmdUpdateGroup(CmdUpdateGroup value) {
        return new JAXBElement<CmdUpdateGroup>(_CmdUpdateGroup_QNAME, CmdUpdateGroup.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdSetCodiReset1Response }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdSetCodiReset1Response")
    public JAXBElement<CmdSetCodiReset1Response> createCmdSetCodiReset1Response(CmdSetCodiReset1Response value) {
        return new JAXBElement<CmdSetCodiReset1Response>(_CmdSetCodiReset1Response_QNAME, CmdSetCodiReset1Response.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdIDRStart }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdIDRStart")
    public JAXBElement<CmdIDRStart> createCmdIDRStart(CmdIDRStart value) {
        return new JAXBElement<CmdIDRStart>(_CmdIDRStart_QNAME, CmdIDRStart.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetDRAssetInfoResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetDRAssetInfoResponse")
    public JAXBElement<CmdGetDRAssetInfoResponse> createCmdGetDRAssetInfoResponse(CmdGetDRAssetInfoResponse value) {
        return new JAXBElement<CmdGetDRAssetInfoResponse>(_CmdGetDRAssetInfoResponse_QNAME, CmdGetDRAssetInfoResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdMcuDiagnosisResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdMcuDiagnosisResponse")
    public JAXBElement<CmdMcuDiagnosisResponse> createCmdMcuDiagnosisResponse(CmdMcuDiagnosisResponse value) {
        return new JAXBElement<CmdMcuDiagnosisResponse>(_CmdMcuDiagnosisResponse_QNAME, CmdMcuDiagnosisResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdDigitalInOutResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdDigitalInOutResponse")
    public JAXBElement<CmdDigitalInOutResponse> createCmdDigitalInOutResponse(CmdDigitalInOutResponse value) {
        return new JAXBElement<CmdDigitalInOutResponse>(_CmdDigitalInOutResponse_QNAME, CmdDigitalInOutResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdDistributionStateResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdDistributionStateResponse")
    public JAXBElement<CmdDistributionStateResponse> createCmdDistributionStateResponse(CmdDistributionStateResponse value) {
        return new JAXBElement<CmdDistributionStateResponse>(_CmdDistributionStateResponse_QNAME, CmdDistributionStateResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetCodiDevice }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetCodiDevice")
    public JAXBElement<CmdGetCodiDevice> createCmdGetCodiDevice(CmdGetCodiDevice value) {
        return new JAXBElement<CmdGetCodiDevice>(_CmdGetCodiDevice_QNAME, CmdGetCodiDevice.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdEndDeviceControl }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdEndDeviceControl")
    public JAXBElement<CmdEndDeviceControl> createCmdEndDeviceControl(CmdEndDeviceControl value) {
        return new JAXBElement<CmdEndDeviceControl>(_CmdEndDeviceControl_QNAME, CmdEndDeviceControl.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdClearCommLog }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdClearCommLog")
    public JAXBElement<CmdClearCommLog> createCmdClearCommLog(CmdClearCommLog value) {
        return new JAXBElement<CmdClearCommLog>(_CmdClearCommLog_QNAME, CmdClearCommLog.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdMcuGetFileSystem }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdMcuGetFileSystem")
    public JAXBElement<CmdMcuGetFileSystem> createCmdMcuGetFileSystem(CmdMcuGetFileSystem value) {
        return new JAXBElement<CmdMcuGetFileSystem>(_CmdMcuGetFileSystem_QNAME, CmdMcuGetFileSystem.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdDeleteLoadControlSchemeResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdDeleteLoadControlSchemeResponse")
    public JAXBElement<CmdDeleteLoadControlSchemeResponse> createCmdDeleteLoadControlSchemeResponse(CmdDeleteLoadControlSchemeResponse value) {
        return new JAXBElement<CmdDeleteLoadControlSchemeResponse>(_CmdDeleteLoadControlSchemeResponse_QNAME, CmdDeleteLoadControlSchemeResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdSetCodiResetResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdSetCodiResetResponse")
    public JAXBElement<CmdSetCodiResetResponse> createCmdSetCodiResetResponse(CmdSetCodiResetResponse value) {
        return new JAXBElement<CmdSetCodiResetResponse>(_CmdSetCodiResetResponse_QNAME, CmdSetCodiResetResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdRelaySwitchAndActivateResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdRelaySwitchAndActivateResponse")
    public JAXBElement<CmdRelaySwitchAndActivateResponse> createCmdRelaySwitchAndActivateResponse(CmdRelaySwitchAndActivateResponse value) {
        return new JAXBElement<CmdRelaySwitchAndActivateResponse>(_CmdRelaySwitchAndActivateResponse_QNAME, CmdRelaySwitchAndActivateResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdMcuSetGMT }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdMcuSetGMT")
    public JAXBElement<CmdMcuSetGMT> createCmdMcuSetGMT(CmdMcuSetGMT value) {
        return new JAXBElement<CmdMcuSetGMT>(_CmdMcuSetGMT_QNAME, CmdMcuSetGMT.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdAidonMCCB }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdAidonMCCB")
    public JAXBElement<CmdAidonMCCB> createCmdAidonMCCB(CmdAidonMCCB value) {
        return new JAXBElement<CmdAidonMCCB>(_CmdAidonMCCB_QNAME, CmdAidonMCCB.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGroupAddResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGroupAddResponse")
    public JAXBElement<CmdGroupAddResponse> createCmdGroupAddResponse(CmdGroupAddResponse value) {
        return new JAXBElement<CmdGroupAddResponse>(_CmdGroupAddResponse_QNAME, CmdGroupAddResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdSetLoadLimitScheme }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdSetLoadLimitScheme")
    public JAXBElement<CmdSetLoadLimitScheme> createCmdSetLoadLimitScheme(CmdSetLoadLimitScheme value) {
        return new JAXBElement<CmdSetLoadLimitScheme>(_CmdSetLoadLimitScheme_QNAME, CmdSetLoadLimitScheme.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdSetTOUCalendarResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdSetTOUCalendarResponse")
    public JAXBElement<CmdSetTOUCalendarResponse> createCmdSetTOUCalendarResponse(CmdSetTOUCalendarResponse value) {
        return new JAXBElement<CmdSetTOUCalendarResponse>(_CmdSetTOUCalendarResponse_QNAME, CmdSetTOUCalendarResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdClearMeterLog }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdClearMeterLog")
    public JAXBElement<CmdClearMeterLog> createCmdClearMeterLog(CmdClearMeterLog value) {
        return new JAXBElement<CmdClearMeterLog>(_CmdClearMeterLog_QNAME, CmdClearMeterLog.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdDeleteLoadShedScheme }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdDeleteLoadShedScheme")
    public JAXBElement<CmdDeleteLoadShedScheme> createCmdDeleteLoadShedScheme(CmdDeleteLoadShedScheme value) {
        return new JAXBElement<CmdDeleteLoadShedScheme>(_CmdDeleteLoadShedScheme_QNAME, CmdDeleteLoadShedScheme.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdCorrectModemPulse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdCorrectModemPulse")
    public JAXBElement<CmdCorrectModemPulse> createCmdCorrectModemPulse(CmdCorrectModemPulse value) {
        return new JAXBElement<CmdCorrectModemPulse>(_CmdCorrectModemPulse_QNAME, CmdCorrectModemPulse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetDRAssetResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetDRAssetResponse")
    public JAXBElement<CmdGetDRAssetResponse> createCmdGetDRAssetResponse(CmdGetDRAssetResponse value) {
        return new JAXBElement<CmdGetDRAssetResponse>(_CmdGetDRAssetResponse_QNAME, CmdGetDRAssetResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdDRAgreement }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdDRAgreement")
    public JAXBElement<CmdDRAgreement> createCmdDRAgreement(CmdDRAgreement value) {
        return new JAXBElement<CmdDRAgreement>(_CmdDRAgreement_QNAME, CmdDRAgreement.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGroupAsyncCallResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGroupAsyncCallResponse")
    public JAXBElement<CmdGroupAsyncCallResponse> createCmdGroupAsyncCallResponse(CmdGroupAsyncCallResponse value) {
        return new JAXBElement<CmdGroupAsyncCallResponse>(_CmdGroupAsyncCallResponse_QNAME, CmdGroupAsyncCallResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGroupDeleteMemberResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGroupDeleteMemberResponse")
    public JAXBElement<CmdGroupDeleteMemberResponse> createCmdGroupDeleteMemberResponse(CmdGroupDeleteMemberResponse value) {
        return new JAXBElement<CmdGroupDeleteMemberResponse>(_CmdGroupDeleteMemberResponse_QNAME, CmdGroupDeleteMemberResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetPhoneList }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetPhoneList")
    public JAXBElement<CmdGetPhoneList> createCmdGetPhoneList(CmdGetPhoneList value) {
        return new JAXBElement<CmdGetPhoneList>(_CmdGetPhoneList_QNAME, CmdGetPhoneList.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetMeterLogListResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetMeterLogListResponse")
    public JAXBElement<CmdGetMeterLogListResponse> createCmdGetMeterLogListResponse(CmdGetMeterLogListResponse value) {
        return new JAXBElement<CmdGetMeterLogListResponse>(_CmdGetMeterLogListResponse_QNAME, CmdGetMeterLogListResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdDeleteLoadLimitSchemeResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdDeleteLoadLimitSchemeResponse")
    public JAXBElement<CmdDeleteLoadLimitSchemeResponse> createCmdDeleteLoadLimitSchemeResponse(CmdDeleteLoadLimitSchemeResponse value) {
        return new JAXBElement<CmdDeleteLoadLimitSchemeResponse>(_CmdDeleteLoadLimitSchemeResponse_QNAME, CmdDeleteLoadLimitSchemeResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetDRLevelResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetDRLevelResponse")
    public JAXBElement<CmdGetDRLevelResponse> createCmdGetDRLevelResponse(CmdGetDRLevelResponse value) {
        return new JAXBElement<CmdGetDRLevelResponse>(_CmdGetDRLevelResponse_QNAME, CmdGetDRLevelResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdSetMeterSecurityResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdSetMeterSecurityResponse")
    public JAXBElement<CmdSetMeterSecurityResponse> createCmdSetMeterSecurityResponse(CmdSetMeterSecurityResponse value) {
        return new JAXBElement<CmdSetMeterSecurityResponse>(_CmdSetMeterSecurityResponse_QNAME, CmdSetMeterSecurityResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdUpdateModemFirmwareResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdUpdateModemFirmwareResponse")
    public JAXBElement<CmdUpdateModemFirmwareResponse> createCmdUpdateModemFirmwareResponse(CmdUpdateModemFirmwareResponse value) {
        return new JAXBElement<CmdUpdateModemFirmwareResponse>(_CmdUpdateModemFirmwareResponse_QNAME, CmdUpdateModemFirmwareResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdEndDeviceControlResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdEndDeviceControlResponse")
    public JAXBElement<CmdEndDeviceControlResponse> createCmdEndDeviceControlResponse(CmdEndDeviceControlResponse value) {
        return new JAXBElement<CmdEndDeviceControlResponse>(_CmdEndDeviceControlResponse_QNAME, CmdEndDeviceControlResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DoGetModemROM }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "doGetModemROM")
    public JAXBElement<DoGetModemROM> createDoGetModemROM(DoGetModemROM value) {
        return new JAXBElement<DoGetModemROM>(_DoGetModemROM_QNAME, DoGetModemROM.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetModemAmrDataResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetModemAmrDataResponse")
    public JAXBElement<CmdGetModemAmrDataResponse> createCmdGetModemAmrDataResponse(CmdGetModemAmrDataResponse value) {
        return new JAXBElement<CmdGetModemAmrDataResponse>(_CmdGetModemAmrDataResponse_QNAME, CmdGetModemAmrDataResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetCodiDevice1Response }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetCodiDevice1Response")
    public JAXBElement<CmdGetCodiDevice1Response> createCmdGetCodiDevice1Response(CmdGetCodiDevice1Response value) {
        return new JAXBElement<CmdGetCodiDevice1Response>(_CmdGetCodiDevice1Response_QNAME, CmdGetCodiDevice1Response.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdDeleteGroupResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdDeleteGroupResponse")
    public JAXBElement<CmdDeleteGroupResponse> createCmdDeleteGroupResponse(CmdDeleteGroupResponse value) {
        return new JAXBElement<CmdDeleteGroupResponse>(_CmdDeleteGroupResponse_QNAME, CmdDeleteGroupResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetCodiMemory1Response }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetCodiMemory1Response")
    public JAXBElement<CmdGetCodiMemory1Response> createCmdGetCodiMemory1Response(CmdGetCodiMemory1Response value) {
        return new JAXBElement<CmdGetCodiMemory1Response>(_CmdGetCodiMemory1Response_QNAME, CmdGetCodiMemory1Response.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdRecovery }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdRecovery")
    public JAXBElement<CmdRecovery> createCmdRecovery(CmdRecovery value) {
        return new JAXBElement<CmdRecovery>(_CmdRecovery_QNAME, CmdRecovery.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdMcuLoopback }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdMcuLoopback")
    public JAXBElement<CmdMcuLoopback> createCmdMcuLoopback(CmdMcuLoopback value) {
        return new JAXBElement<CmdMcuLoopback>(_CmdMcuLoopback_QNAME, CmdMcuLoopback.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdMcuGetMemoryResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdMcuGetMemoryResponse")
    public JAXBElement<CmdMcuGetMemoryResponse> createCmdMcuGetMemoryResponse(CmdMcuGetMemoryResponse value) {
        return new JAXBElement<CmdMcuGetMemoryResponse>(_CmdMcuGetMemoryResponse_QNAME, CmdMcuGetMemoryResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdBypassMeterProgramResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdBypassMeterProgramResponse")
    public JAXBElement<CmdBypassMeterProgramResponse> createCmdBypassMeterProgramResponse(CmdBypassMeterProgramResponse value) {
        return new JAXBElement<CmdBypassMeterProgramResponse>(_CmdBypassMeterProgramResponse_QNAME, CmdBypassMeterProgramResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetFile }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetFile")
    public JAXBElement<CmdGetFile> createCmdGetFile(CmdGetFile value) {
        return new JAXBElement<CmdGetFile>(_CmdGetFile_QNAME, CmdGetFile.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdInstallFileResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdInstallFileResponse")
    public JAXBElement<CmdInstallFileResponse> createCmdInstallFileResponse(CmdInstallFileResponse value) {
        return new JAXBElement<CmdInstallFileResponse>(_CmdInstallFileResponse_QNAME, CmdInstallFileResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetModemCount }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetModemCount")
    public JAXBElement<CmdGetModemCount> createCmdGetModemCount(CmdGetModemCount value) {
        return new JAXBElement<CmdGetModemCount>(_CmdGetModemCount_QNAME, CmdGetModemCount.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdSetLoadLimitSchemeResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdSetLoadLimitSchemeResponse")
    public JAXBElement<CmdSetLoadLimitSchemeResponse> createCmdSetLoadLimitSchemeResponse(CmdSetLoadLimitSchemeResponse value) {
        return new JAXBElement<CmdSetLoadLimitSchemeResponse>(_CmdSetLoadLimitSchemeResponse_QNAME, CmdSetLoadLimitSchemeResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Exception }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "Exception")
    public JAXBElement<Exception> createException(Exception value) {
        return new JAXBElement<Exception>(_Exception_QNAME, Exception.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdOnDemandMeterAllResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdOnDemandMeterAllResponse")
    public JAXBElement<CmdOnDemandMeterAllResponse> createCmdOnDemandMeterAllResponse(CmdOnDemandMeterAllResponse value) {
        return new JAXBElement<CmdOnDemandMeterAllResponse>(_CmdOnDemandMeterAllResponse_QNAME, CmdOnDemandMeterAllResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdEntryStdSetResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdEntryStdSetResponse")
    public JAXBElement<CmdEntryStdSetResponse> createCmdEntryStdSetResponse(CmdEntryStdSetResponse value) {
        return new JAXBElement<CmdEntryStdSetResponse>(_CmdEntryStdSetResponse_QNAME, CmdEntryStdSetResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetEventLogListResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetEventLogListResponse")
    public JAXBElement<CmdGetEventLogListResponse> createCmdGetEventLogListResponse(CmdGetEventLogListResponse value) {
        return new JAXBElement<CmdGetEventLogListResponse>(_CmdGetEventLogListResponse_QNAME, CmdGetEventLogListResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdModifyTransactionResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdModifyTransactionResponse")
    public JAXBElement<CmdModifyTransactionResponse> createCmdModifyTransactionResponse(CmdModifyTransactionResponse value) {
        return new JAXBElement<CmdModifyTransactionResponse>(_CmdModifyTransactionResponse_QNAME, CmdModifyTransactionResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdDeleteModemAll }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdDeleteModemAll")
    public JAXBElement<CmdDeleteModemAll> createCmdDeleteModemAll(CmdDeleteModemAll value) {
        return new JAXBElement<CmdDeleteModemAll>(_CmdDeleteModemAll_QNAME, CmdDeleteModemAll.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdMcuClearStaticResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdMcuClearStaticResponse")
    public JAXBElement<CmdMcuClearStaticResponse> createCmdMcuClearStaticResponse(CmdMcuClearStaticResponse value) {
        return new JAXBElement<CmdMcuClearStaticResponse>(_CmdMcuClearStaticResponse_QNAME, CmdMcuClearStaticResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdMcuShutdownResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdMcuShutdownResponse")
    public JAXBElement<CmdMcuShutdownResponse> createCmdMcuShutdownResponse(CmdMcuShutdownResponse value) {
        return new JAXBElement<CmdMcuShutdownResponse>(_CmdMcuShutdownResponse_QNAME, CmdMcuShutdownResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdSetLoadControlScheme }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdSetLoadControlScheme")
    public JAXBElement<CmdSetLoadControlScheme> createCmdSetLoadControlScheme(CmdSetLoadControlScheme value) {
        return new JAXBElement<CmdSetLoadControlScheme>(_CmdSetLoadControlScheme_QNAME, CmdSetLoadControlScheme.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdSetEnergyLevel }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdSetEnergyLevel")
    public JAXBElement<CmdSetEnergyLevel> createCmdSetEnergyLevel(CmdSetEnergyLevel value) {
        return new JAXBElement<CmdSetEnergyLevel>(_CmdSetEnergyLevel_QNAME, CmdSetEnergyLevel.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdMeteringAll }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdMeteringAll")
    public JAXBElement<CmdMeteringAll> createCmdMeteringAll(CmdMeteringAll value) {
        return new JAXBElement<CmdMeteringAll>(_CmdMeteringAll_QNAME, CmdMeteringAll.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdSetIEIUConfResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdSetIEIUConfResponse")
    public JAXBElement<CmdSetIEIUConfResponse> createCmdSetIEIUConfResponse(CmdSetIEIUConfResponse value) {
        return new JAXBElement<CmdSetIEIUConfResponse>(_CmdSetIEIUConfResponse_QNAME, CmdSetIEIUConfResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdOnDemandMeterAsyncResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdOnDemandMeterAsyncResponse")
    public JAXBElement<CmdOnDemandMeterAsyncResponse> createCmdOnDemandMeterAsyncResponse(CmdOnDemandMeterAsyncResponse value) {
        return new JAXBElement<CmdOnDemandMeterAsyncResponse>(_CmdOnDemandMeterAsyncResponse_QNAME, CmdOnDemandMeterAsyncResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetConfiguration }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetConfiguration")
    public JAXBElement<CmdGetConfiguration> createCmdGetConfiguration(CmdGetConfiguration value) {
        return new JAXBElement<CmdGetConfiguration>(_CmdGetConfiguration_QNAME, CmdGetConfiguration.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdAsynchronousCall }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdAsynchronousCall")
    public JAXBElement<CmdAsynchronousCall> createCmdAsynchronousCall(CmdAsynchronousCall value) {
        return new JAXBElement<CmdAsynchronousCall>(_CmdAsynchronousCall_QNAME, CmdAsynchronousCall.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetModemROMResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetModemROMResponse")
    public JAXBElement<CmdGetModemROMResponse> createCmdGetModemROMResponse(CmdGetModemROMResponse value) {
        return new JAXBElement<CmdGetModemROMResponse>(_CmdGetModemROMResponse_QNAME, CmdGetModemROMResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdMcuResetResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdMcuResetResponse")
    public JAXBElement<CmdMcuResetResponse> createCmdMcuResetResponse(CmdMcuResetResponse value) {
        return new JAXBElement<CmdMcuResetResponse>(_CmdMcuResetResponse_QNAME, CmdMcuResetResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdEntryStdGetResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdEntryStdGetResponse")
    public JAXBElement<CmdEntryStdGetResponse> createCmdEntryStdGetResponse(CmdEntryStdGetResponse value) {
        return new JAXBElement<CmdEntryStdGetResponse>(_CmdEntryStdGetResponse_QNAME, CmdEntryStdGetResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdMcuGetIpResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdMcuGetIpResponse")
    public JAXBElement<CmdMcuGetIpResponse> createCmdMcuGetIpResponse(CmdMcuGetIpResponse value) {
        return new JAXBElement<CmdMcuGetIpResponse>(_CmdMcuGetIpResponse_QNAME, CmdMcuGetIpResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdSetModemAmrData }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdSetModemAmrData")
    public JAXBElement<CmdSetModemAmrData> createCmdSetModemAmrData(CmdSetModemAmrData value) {
        return new JAXBElement<CmdSetModemAmrData>(_CmdSetModemAmrData_QNAME, CmdSetModemAmrData.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdOnDemandMeter2 }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdOnDemandMeter2")
    public JAXBElement<CmdOnDemandMeter2> createCmdOnDemandMeter2(CmdOnDemandMeter2 value) {
        return new JAXBElement<CmdOnDemandMeter2>(_CmdOnDemandMeter2_QNAME, CmdOnDemandMeter2.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdMcuClearStatic }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdMcuClearStatic")
    public JAXBElement<CmdMcuClearStatic> createCmdMcuClearStatic(CmdMcuClearStatic value) {
        return new JAXBElement<CmdMcuClearStatic>(_CmdMcuClearStatic_QNAME, CmdMcuClearStatic.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetCodiNeighbor }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetCodiNeighbor")
    public JAXBElement<CmdGetCodiNeighbor> createCmdGetCodiNeighbor(CmdGetCodiNeighbor value) {
        return new JAXBElement<CmdGetCodiNeighbor>(_CmdGetCodiNeighbor_QNAME, CmdGetCodiNeighbor.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdDigitalInOut }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdDigitalInOut")
    public JAXBElement<CmdDigitalInOut> createCmdDigitalInOut(CmdDigitalInOut value) {
        return new JAXBElement<CmdDigitalInOut>(_CmdDigitalInOut_QNAME, CmdDigitalInOut.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdOnDemandMeter3 }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdOnDemandMeter3")
    public JAXBElement<CmdOnDemandMeter3> createCmdOnDemandMeter3(CmdOnDemandMeter3 value) {
        return new JAXBElement<CmdOnDemandMeter3>(_CmdOnDemandMeter3_QNAME, CmdOnDemandMeter3 .class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdMcuGetProcess }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdMcuGetProcess")
    public JAXBElement<CmdMcuGetProcess> createCmdMcuGetProcess(CmdMcuGetProcess value) {
        return new JAXBElement<CmdMcuGetProcess>(_CmdMcuGetProcess_QNAME, CmdMcuGetProcess.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetCodiBinding1Response }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetCodiBinding1Response")
    public JAXBElement<CmdGetCodiBinding1Response> createCmdGetCodiBinding1Response(CmdGetCodiBinding1Response value) {
        return new JAXBElement<CmdGetCodiBinding1Response>(_CmdGetCodiBinding1Response_QNAME, CmdGetCodiBinding1Response.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdMcuGetPluginResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdMcuGetPluginResponse")
    public JAXBElement<CmdMcuGetPluginResponse> createCmdMcuGetPluginResponse(CmdMcuGetPluginResponse value) {
        return new JAXBElement<CmdMcuGetPluginResponse>(_CmdMcuGetPluginResponse_QNAME, CmdMcuGetPluginResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetModemAmrData }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetModemAmrData")
    public JAXBElement<CmdGetModemAmrData> createCmdGetModemAmrData(CmdGetModemAmrData value) {
        return new JAXBElement<CmdGetModemAmrData>(_CmdGetModemAmrData_QNAME, CmdGetModemAmrData.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetCodiBindingResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetCodiBindingResponse")
    public JAXBElement<CmdGetCodiBindingResponse> createCmdGetCodiBindingResponse(CmdGetCodiBindingResponse value) {
        return new JAXBElement<CmdGetCodiBindingResponse>(_CmdGetCodiBindingResponse_QNAME, CmdGetCodiBindingResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetLoadLimitPropertyResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetLoadLimitPropertyResponse")
    public JAXBElement<CmdGetLoadLimitPropertyResponse> createCmdGetLoadLimitPropertyResponse(CmdGetLoadLimitPropertyResponse value) {
        return new JAXBElement<CmdGetLoadLimitPropertyResponse>(_CmdGetLoadLimitPropertyResponse_QNAME, CmdGetLoadLimitPropertyResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdWriteTableResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdWriteTableResponse")
    public JAXBElement<CmdWriteTableResponse> createCmdWriteTableResponse(CmdWriteTableResponse value) {
        return new JAXBElement<CmdWriteTableResponse>(_CmdWriteTableResponse_QNAME, CmdWriteTableResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdMeterFactoryReset }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdMeterFactoryReset")
    public JAXBElement<CmdMeterFactoryReset> createCmdMeterFactoryReset(CmdMeterFactoryReset value) {
        return new JAXBElement<CmdMeterFactoryReset>(_CmdMeterFactoryReset_QNAME, CmdMeterFactoryReset.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetDRAsset }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetDRAsset")
    public JAXBElement<CmdGetDRAsset> createCmdGetDRAsset(CmdGetDRAsset value) {
        return new JAXBElement<CmdGetDRAsset>(_CmdGetDRAsset_QNAME, CmdGetDRAsset.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetMeterScheduleResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetMeterScheduleResponse")
    public JAXBElement<CmdGetMeterScheduleResponse> createCmdGetMeterScheduleResponse(CmdGetMeterScheduleResponse value) {
        return new JAXBElement<CmdGetMeterScheduleResponse>(_CmdGetMeterScheduleResponse_QNAME, CmdGetMeterScheduleResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdDeleteLoadShedSchemeResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdDeleteLoadShedSchemeResponse")
    public JAXBElement<CmdDeleteLoadShedSchemeResponse> createCmdDeleteLoadShedSchemeResponse(CmdDeleteLoadShedSchemeResponse value) {
        return new JAXBElement<CmdDeleteLoadShedSchemeResponse>(_CmdDeleteLoadShedSchemeResponse_QNAME, CmdDeleteLoadShedSchemeResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdDeleteModemByPropertyResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdDeleteModemByPropertyResponse")
    public JAXBElement<CmdDeleteModemByPropertyResponse> createCmdDeleteModemByPropertyResponse(CmdDeleteModemByPropertyResponse value) {
        return new JAXBElement<CmdDeleteModemByPropertyResponse>(_CmdDeleteModemByPropertyResponse_QNAME, CmdDeleteModemByPropertyResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetMeter }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetMeter")
    public JAXBElement<CmdGetMeter> createCmdGetMeter(CmdGetMeter value) {
        return new JAXBElement<CmdGetMeter>(_CmdGetMeter_QNAME, CmdGetMeter.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdFirmwareUpdate }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdFirmwareUpdate")
    public JAXBElement<CmdFirmwareUpdate> createCmdFirmwareUpdate(CmdFirmwareUpdate value) {
        return new JAXBElement<CmdFirmwareUpdate>(_CmdFirmwareUpdate_QNAME, CmdFirmwareUpdate.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdReadTable }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdReadTable")
    public JAXBElement<CmdReadTable> createCmdReadTable(CmdReadTable value) {
        return new JAXBElement<CmdReadTable>(_CmdReadTable_QNAME, CmdReadTable.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdSensorLPLogRecoveryResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdSensorLPLogRecoveryResponse")
    public JAXBElement<CmdSensorLPLogRecoveryResponse> createCmdSensorLPLogRecoveryResponse(CmdSensorLPLogRecoveryResponse value) {
        return new JAXBElement<CmdSensorLPLogRecoveryResponse>(_CmdSensorLPLogRecoveryResponse_QNAME, CmdSensorLPLogRecoveryResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DoGetModemCluster }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "doGetModemCluster")
    public JAXBElement<DoGetModemCluster> createDoGetModemCluster(DoGetModemCluster value) {
        return new JAXBElement<DoGetModemCluster>(_DoGetModemCluster_QNAME, DoGetModemCluster.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdDeleteModemByProperty }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdDeleteModemByProperty")
    public JAXBElement<CmdDeleteModemByProperty> createCmdDeleteModemByProperty(CmdDeleteModemByProperty value) {
        return new JAXBElement<CmdDeleteModemByProperty>(_CmdDeleteModemByProperty_QNAME, CmdDeleteModemByProperty.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetFFDList }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetFFDList")
    public JAXBElement<CmdGetFFDList> createCmdGetFFDList(CmdGetFFDList value) {
        return new JAXBElement<CmdGetFFDList>(_CmdGetFFDList_QNAME, CmdGetFFDList.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetLoadShedScheduleResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetLoadShedScheduleResponse")
    public JAXBElement<CmdGetLoadShedScheduleResponse> createCmdGetLoadShedScheduleResponse(CmdGetLoadShedScheduleResponse value) {
        return new JAXBElement<CmdGetLoadShedScheduleResponse>(_CmdGetLoadShedScheduleResponse_QNAME, CmdGetLoadShedScheduleResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdPutFileResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdPutFileResponse")
    public JAXBElement<CmdPutFileResponse> createCmdPutFileResponse(CmdPutFileResponse value) {
        return new JAXBElement<CmdPutFileResponse>(_CmdPutFileResponse_QNAME, CmdPutFileResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdSetIHDTable }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdSetIHDTable")
    public JAXBElement<CmdSetIHDTable> createCmdSetIHDTable(CmdSetIHDTable value) {
        return new JAXBElement<CmdSetIHDTable>(_CmdSetIHDTable_QNAME, CmdSetIHDTable.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdMcuGetSystemInfoResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdMcuGetSystemInfoResponse")
    public JAXBElement<CmdMcuGetSystemInfoResponse> createCmdMcuGetSystemInfoResponse(CmdMcuGetSystemInfoResponse value) {
        return new JAXBElement<CmdMcuGetSystemInfoResponse>(_CmdMcuGetSystemInfoResponse_QNAME, CmdMcuGetSystemInfoResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdBypassSensor }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdBypassSensor")
    public JAXBElement<CmdBypassSensor> createCmdBypassSensor(CmdBypassSensor value) {
        return new JAXBElement<CmdBypassSensor>(_CmdBypassSensor_QNAME, CmdBypassSensor.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetAllLogList }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetAllLogList")
    public JAXBElement<CmdGetAllLogList> createCmdGetAllLogList(CmdGetAllLogList value) {
        return new JAXBElement<CmdGetAllLogList>(_CmdGetAllLogList_QNAME, CmdGetAllLogList.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGroupInfo2 }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGroupInfo2")
    public JAXBElement<CmdGroupInfo2> createCmdGroupInfo2(CmdGroupInfo2 value) {
        return new JAXBElement<CmdGroupInfo2>(_CmdGroupInfo2_QNAME, CmdGroupInfo2 .class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdSetIEIUConf }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdSetIEIUConf")
    public JAXBElement<CmdSetIEIUConf> createCmdSetIEIUConf(CmdSetIEIUConf value) {
        return new JAXBElement<CmdSetIEIUConf>(_CmdSetIEIUConf_QNAME, CmdSetIEIUConf.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGroupInfo1 }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGroupInfo1")
    public JAXBElement<CmdGroupInfo1> createCmdGroupInfo1(CmdGroupInfo1 value) {
        return new JAXBElement<CmdGroupInfo1>(_CmdGroupInfo1_QNAME, CmdGroupInfo1 .class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RequestPLCDataFrame }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "requestPLCDataFrame")
    public JAXBElement<RequestPLCDataFrame> createRequestPLCDataFrame(RequestPLCDataFrame value) {
        return new JAXBElement<RequestPLCDataFrame>(_RequestPLCDataFrame_QNAME, RequestPLCDataFrame.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdMcuGetProcessResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdMcuGetProcessResponse")
    public JAXBElement<CmdMcuGetProcessResponse> createCmdMcuGetProcessResponse(CmdMcuGetProcessResponse value) {
        return new JAXBElement<CmdMcuGetProcessResponse>(_CmdMcuGetProcessResponse_QNAME, CmdMcuGetProcessResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdCommandModem1Response }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdCommandModem1Response")
    public JAXBElement<CmdCommandModem1Response> createCmdCommandModem1Response(CmdCommandModem1Response value) {
        return new JAXBElement<CmdCommandModem1Response>(_CmdCommandModem1Response_QNAME, CmdCommandModem1Response.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdMcuGetPlugin }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdMcuGetPlugin")
    public JAXBElement<CmdMcuGetPlugin> createCmdMcuGetPlugin(CmdMcuGetPlugin value) {
        return new JAXBElement<CmdMcuGetPlugin>(_CmdMcuGetPlugin_QNAME, CmdMcuGetPlugin.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetMeterSecurityResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetMeterSecurityResponse")
    public JAXBElement<CmdGetMeterSecurityResponse> createCmdGetMeterSecurityResponse(CmdGetMeterSecurityResponse value) {
        return new JAXBElement<CmdGetMeterSecurityResponse>(_CmdGetMeterSecurityResponse_QNAME, CmdGetMeterSecurityResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdMcuGetLog }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdCmdMcuGetLog")
    public JAXBElement<CmdMcuGetLog> createCmdMcuGetLog(CmdMcuGetLog value) {
        return new JAXBElement<CmdMcuGetLog>(_CmdMcuGetLog_QNAME, CmdMcuGetLog.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdMcuGetLogResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdCmdMcuGetLogResponse")
    public JAXBElement<CmdMcuGetLogResponse> createCmdMcuGetLogResponse(CmdMcuGetLogResponse value) {
        return new JAXBElement<CmdMcuGetLogResponse>(_CmdMcuGetLogResponse_QNAME, CmdMcuGetLogResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdShowTransactionListResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdShowTransactionListResponse")
    public JAXBElement<CmdShowTransactionListResponse> createCmdShowTransactionListResponse(CmdShowTransactionListResponse value) {
        return new JAXBElement<CmdShowTransactionListResponse>(_CmdShowTransactionListResponse_QNAME, CmdShowTransactionListResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdMcuGetFileSystemResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdMcuGetFileSystemResponse")
    public JAXBElement<CmdMcuGetFileSystemResponse> createCmdMcuGetFileSystemResponse(CmdMcuGetFileSystemResponse value) {
        return new JAXBElement<CmdMcuGetFileSystemResponse>(_CmdMcuGetFileSystemResponse_QNAME, CmdMcuGetFileSystemResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdMcuSetTime }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdMcuSetTime")
    public JAXBElement<CmdMcuSetTime> createCmdMcuSetTime(CmdMcuSetTime value) {
        return new JAXBElement<CmdMcuSetTime>(_CmdMcuSetTime_QNAME, CmdMcuSetTime.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdMcuSetGMTResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdMcuSetGMTResponse")
    public JAXBElement<CmdMcuSetGMTResponse> createCmdMcuSetGMTResponse(CmdMcuSetGMTResponse value) {
        return new JAXBElement<CmdMcuSetGMTResponse>(_CmdMcuSetGMTResponse_QNAME, CmdMcuSetGMTResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdMcuResetDevice }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdMcuResetDevice")
    public JAXBElement<CmdMcuResetDevice> createCmdMcuResetDevice(CmdMcuResetDevice value) {
        return new JAXBElement<CmdMcuResetDevice>(_CmdMcuResetDevice_QNAME, CmdMcuResetDevice.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetMeterLogList }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetMeterLogList")
    public JAXBElement<CmdGetMeterLogList> createCmdGetMeterLogList(CmdGetMeterLogList value) {
        return new JAXBElement<CmdGetMeterLogList>(_CmdGetMeterLogList_QNAME, CmdGetMeterLogList.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdDRAgreementResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdDRAgreementResponse")
    public JAXBElement<CmdDRAgreementResponse> createCmdDRAgreementResponse(CmdDRAgreementResponse value) {
        return new JAXBElement<CmdDRAgreementResponse>(_CmdDRAgreementResponse_QNAME, CmdDRAgreementResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetCodiInfoResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetCodiInfoResponse")
    public JAXBElement<CmdGetCodiInfoResponse> createCmdGetCodiInfoResponse(CmdGetCodiInfoResponse value) {
        return new JAXBElement<CmdGetCodiInfoResponse>(_CmdGetCodiInfoResponse_QNAME, CmdGetCodiInfoResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdSetLoadControlSchemeResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdSetLoadControlSchemeResponse")
    public JAXBElement<CmdSetLoadControlSchemeResponse> createCmdSetLoadControlSchemeResponse(CmdSetLoadControlSchemeResponse value) {
        return new JAXBElement<CmdSetLoadControlSchemeResponse>(_CmdSetLoadControlSchemeResponse_QNAME, CmdSetLoadControlSchemeResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdMcuSetGpioResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdMcuSetGpioResponse")
    public JAXBElement<CmdMcuSetGpioResponse> createCmdMcuSetGpioResponse(CmdMcuSetGpioResponse value) {
        return new JAXBElement<CmdMcuSetGpioResponse>(_CmdMcuSetGpioResponse_QNAME, CmdMcuSetGpioResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdSetCodiReset }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdSetCodiReset")
    public JAXBElement<CmdSetCodiReset> createCmdSetCodiReset(CmdSetCodiReset value) {
        return new JAXBElement<CmdSetCodiReset>(_CmdSetCodiReset_QNAME, CmdSetCodiReset.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdSetEnergyLevel1Response }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdSetEnergyLevel1Response")
    public JAXBElement<CmdSetEnergyLevel1Response> createCmdSetEnergyLevel1Response(CmdSetEnergyLevel1Response value) {
        return new JAXBElement<CmdSetEnergyLevel1Response>(_CmdSetEnergyLevel1Response_QNAME, CmdSetEnergyLevel1Response.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdReportCurMeter }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdReportCurMeter")
    public JAXBElement<CmdReportCurMeter> createCmdReportCurMeter(CmdReportCurMeter value) {
        return new JAXBElement<CmdReportCurMeter>(_CmdReportCurMeter_QNAME, CmdReportCurMeter.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetLoadLimitScheme }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetLoadLimitScheme")
    public JAXBElement<CmdGetLoadLimitScheme> createCmdGetLoadLimitScheme(CmdGetLoadLimitScheme value) {
        return new JAXBElement<CmdGetLoadLimitScheme>(_CmdGetLoadLimitScheme_QNAME, CmdGetLoadLimitScheme.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetMeterInfoFromModemResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetMeterInfoFromModemResponse")
    public JAXBElement<CmdGetMeterInfoFromModemResponse> createCmdGetMeterInfoFromModemResponse(CmdGetMeterInfoFromModemResponse value) {
        return new JAXBElement<CmdGetMeterInfoFromModemResponse>(_CmdGetMeterInfoFromModemResponse_QNAME, CmdGetMeterInfoFromModemResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetModemEventResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetModemEventResponse")
    public JAXBElement<CmdGetModemEventResponse> createCmdGetModemEventResponse(CmdGetModemEventResponse value) {
        return new JAXBElement<CmdGetModemEventResponse>(_CmdGetModemEventResponse_QNAME, CmdGetModemEventResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetLoadControlSchemeResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetLoadControlSchemeResponse")
    public JAXBElement<CmdGetLoadControlSchemeResponse> createCmdGetLoadControlSchemeResponse(CmdGetLoadControlSchemeResponse value) {
        return new JAXBElement<CmdGetLoadControlSchemeResponse>(_CmdGetLoadControlSchemeResponse_QNAME, CmdGetLoadControlSchemeResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdSetPhoneListResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdSetPhoneListResponse")
    public JAXBElement<CmdSetPhoneListResponse> createCmdSetPhoneListResponse(CmdSetPhoneListResponse value) {
        return new JAXBElement<CmdSetPhoneListResponse>(_CmdSetPhoneListResponse_QNAME, CmdSetPhoneListResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGroupInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGroupInfo")
    public JAXBElement<CmdGroupInfo> createCmdGroupInfo(CmdGroupInfo value) {
        return new JAXBElement<CmdGroupInfo>(_CmdGroupInfo_QNAME, CmdGroupInfo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGroupDelete }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGroupDelete")
    public JAXBElement<CmdGroupDelete> createCmdGroupDelete(CmdGroupDelete value) {
        return new JAXBElement<CmdGroupDelete>(_CmdGroupDelete_QNAME, CmdGroupDelete.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdPackageDistribution }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdPackageDistribution")
    public JAXBElement<CmdPackageDistribution> createCmdPackageDistribution(CmdPackageDistribution value) {
        return new JAXBElement<CmdPackageDistribution>(_CmdPackageDistribution_QNAME, CmdPackageDistribution.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdKamstrupCIDResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdKamstrupCIDResponse")
    public JAXBElement<CmdKamstrupCIDResponse> createCmdKamstrupCIDResponse(CmdKamstrupCIDResponse value) {
        return new JAXBElement<CmdKamstrupCIDResponse>(_CmdKamstrupCIDResponse_QNAME, CmdKamstrupCIDResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdClearEventLogResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdClearEventLogResponse")
    public JAXBElement<CmdClearEventLogResponse> createCmdClearEventLogResponse(CmdClearEventLogResponse value) {
        return new JAXBElement<CmdClearEventLogResponse>(_CmdClearEventLogResponse_QNAME, CmdClearEventLogResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdValveControlResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdValveControlResponse")
    public JAXBElement<CmdValveControlResponse> createCmdValveControlResponse(CmdValveControlResponse value) {
        return new JAXBElement<CmdValveControlResponse>(_CmdValveControlResponse_QNAME, CmdValveControlResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdSetLoadLimitPropertyResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdSetLoadLimitPropertyResponse")
    public JAXBElement<CmdSetLoadLimitPropertyResponse> createCmdSetLoadLimitPropertyResponse(CmdSetLoadLimitPropertyResponse value) {
        return new JAXBElement<CmdSetLoadLimitPropertyResponse>(_CmdSetLoadLimitPropertyResponse_QNAME, CmdSetLoadLimitPropertyResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdSetDRLevel }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdSetDRLevel")
    public JAXBElement<CmdSetDRLevel> createCmdSetDRLevel(CmdSetDRLevel value) {
        return new JAXBElement<CmdSetDRLevel>(_CmdSetDRLevel_QNAME, CmdSetDRLevel.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdAddModems }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdAddModems")
    public JAXBElement<CmdAddModems> createCmdAddModems(CmdAddModems value) {
        return new JAXBElement<CmdAddModems>(_CmdAddModems_QNAME, CmdAddModems.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdMcuSetGpio }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdMcuSetGpio")
    public JAXBElement<CmdMcuSetGpio> createCmdMcuSetGpio(CmdMcuSetGpio value) {
        return new JAXBElement<CmdMcuSetGpio>(_CmdMcuSetGpio_QNAME, CmdMcuSetGpio.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdDRCancel }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdDRCancel")
    public JAXBElement<CmdDRCancel> createCmdDRCancel(CmdDRCancel value) {
        return new JAXBElement<CmdDRCancel>(_CmdDRCancel_QNAME, CmdDRCancel.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdMcuSetTimeResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdMcuSetTimeResponse")
    public JAXBElement<CmdMcuSetTimeResponse> createCmdMcuSetTimeResponse(CmdMcuSetTimeResponse value) {
        return new JAXBElement<CmdMcuSetTimeResponse>(_CmdMcuSetTimeResponse_QNAME, CmdMcuSetTimeResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetCommLogListResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetCommLogListResponse")
    public JAXBElement<CmdGetCommLogListResponse> createCmdGetCommLogListResponse(CmdGetCommLogListResponse value) {
        return new JAXBElement<CmdGetCommLogListResponse>(_CmdGetCommLogListResponse_QNAME, CmdGetCommLogListResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdCommandModemResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdCommandModemResponse")
    public JAXBElement<CmdCommandModemResponse> createCmdCommandModemResponse(CmdCommandModemResponse value) {
        return new JAXBElement<CmdCommandModemResponse>(_CmdCommandModemResponse_QNAME, CmdCommandModemResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetModemAllNewResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetModemAllNewResponse")
    public JAXBElement<CmdGetModemAllNewResponse> createCmdGetModemAllNewResponse(CmdGetModemAllNewResponse value) {
        return new JAXBElement<CmdGetModemAllNewResponse>(_CmdGetModemAllNewResponse_QNAME, CmdGetModemAllNewResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetMeterAll }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetMeterAll")
    public JAXBElement<CmdGetMeterAll> createCmdGetMeterAll(CmdGetMeterAll value) {
        return new JAXBElement<CmdGetMeterAll>(_CmdGetMeterAll_QNAME, CmdGetMeterAll.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdCommandModem1 }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdCommandModem1")
    public JAXBElement<CmdCommandModem1> createCmdCommandModem1(CmdCommandModem1 value) {
        return new JAXBElement<CmdCommandModem1>(_CmdCommandModem1_QNAME, CmdCommandModem1 .class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetGroupResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetGroupResponse")
    public JAXBElement<CmdGetGroupResponse> createCmdGetGroupResponse(CmdGetGroupResponse value) {
        return new JAXBElement<CmdGetGroupResponse>(_CmdGetGroupResponse_QNAME, CmdGetGroupResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetCurMeterAll }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetCurMeterAll")
    public JAXBElement<CmdGetCurMeterAll> createCmdGetCurMeterAll(CmdGetCurMeterAll value) {
        return new JAXBElement<CmdGetCurMeterAll>(_CmdGetCurMeterAll_QNAME, CmdGetCurMeterAll.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetCodiNeighbor1 }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetCodiNeighbor1")
    public JAXBElement<CmdGetCodiNeighbor1> createCmdGetCodiNeighbor1(CmdGetCodiNeighbor1 value) {
        return new JAXBElement<CmdGetCodiNeighbor1>(_CmdGetCodiNeighbor1_QNAME, CmdGetCodiNeighbor1 .class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdClearCommLogResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdClearCommLogResponse")
    public JAXBElement<CmdClearCommLogResponse> createCmdClearCommLogResponse(CmdClearCommLogResponse value) {
        return new JAXBElement<CmdClearCommLogResponse>(_CmdClearCommLogResponse_QNAME, CmdClearCommLogResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SendBypassOpenSMS }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "sendBypassOpenSMS")
    public JAXBElement<SendBypassOpenSMS> createSendBypassOpenSMS(SendBypassOpenSMS value) {
        return new JAXBElement<SendBypassOpenSMS>(_SendBypassOpenSMS_QNAME, SendBypassOpenSMS.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGroupAsyncCall }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGroupAsyncCall")
    public JAXBElement<CmdGroupAsyncCall> createCmdGroupAsyncCall(CmdGroupAsyncCall value) {
        return new JAXBElement<CmdGroupAsyncCall>(_CmdGroupAsyncCall_QNAME, CmdGroupAsyncCall.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdSendSMSResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdSendSMSResponse")
    public JAXBElement<CmdSendSMSResponse> createCmdSendSMSResponse(CmdSendSMSResponse value) {
        return new JAXBElement<CmdSendSMSResponse>(_CmdSendSMSResponse_QNAME, CmdSendSMSResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetMobileLogListResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetMobileLogListResponse")
    public JAXBElement<CmdGetMobileLogListResponse> createCmdGetMobileLogListResponse(CmdGetMobileLogListResponse value) {
        return new JAXBElement<CmdGetMobileLogListResponse>(_CmdGetMobileLogListResponse_QNAME, CmdGetMobileLogListResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGroupInfoResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGroupInfoResponse")
    public JAXBElement<CmdGroupInfoResponse> createCmdGroupInfoResponse(CmdGroupInfoResponse value) {
        return new JAXBElement<CmdGroupInfoResponse>(_CmdGroupInfoResponse_QNAME, CmdGroupInfoResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetCodiBinding }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetCodiBinding")
    public JAXBElement<CmdGetCodiBinding> createCmdGetCodiBinding(CmdGetCodiBinding value) {
        return new JAXBElement<CmdGetCodiBinding>(_CmdGetCodiBinding_QNAME, CmdGetCodiBinding.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdDistributionResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdDistributionResponse")
    public JAXBElement<CmdDistributionResponse> createCmdDistributionResponse(CmdDistributionResponse value) {
        return new JAXBElement<CmdDistributionResponse>(_CmdDistributionResponse_QNAME, CmdDistributionResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetLoadLimitSchemeResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetLoadLimitSchemeResponse")
    public JAXBElement<CmdGetLoadLimitSchemeResponse> createCmdGetLoadLimitSchemeResponse(CmdGetLoadLimitSchemeResponse value) {
        return new JAXBElement<CmdGetLoadLimitSchemeResponse>(_CmdGetLoadLimitSchemeResponse_QNAME, CmdGetLoadLimitSchemeResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdACD }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdACD")
    public JAXBElement<CmdACD> createCmdACD(CmdACD value) {
        return new JAXBElement<CmdACD>(_CmdACD_QNAME, CmdACD.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdInstallFile1Response }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdInstallFile1Response")
    public JAXBElement<CmdInstallFile1Response> createCmdInstallFile1Response(CmdInstallFile1Response value) {
        return new JAXBElement<CmdInstallFile1Response>(_CmdInstallFile1Response_QNAME, CmdInstallFile1Response.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetCommLogList }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetCommLogList")
    public JAXBElement<CmdGetCommLogList> createCmdGetCommLogList(CmdGetCommLogList value) {
        return new JAXBElement<CmdGetCommLogList>(_CmdGetCommLogList_QNAME, CmdGetCommLogList.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetCodiMemory }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetCodiMemory")
    public JAXBElement<CmdGetCodiMemory> createCmdGetCodiMemory(CmdGetCodiMemory value) {
        return new JAXBElement<CmdGetCodiMemory>(_CmdGetCodiMemory_QNAME, CmdGetCodiMemory.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdSetCodiFormNetworkResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdSetCodiFormNetworkResponse")
    public JAXBElement<CmdSetCodiFormNetworkResponse> createCmdSetCodiFormNetworkResponse(CmdSetCodiFormNetworkResponse value) {
        return new JAXBElement<CmdSetCodiFormNetworkResponse>(_CmdSetCodiFormNetworkResponse_QNAME, CmdSetCodiFormNetworkResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetDRLevel }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetDRLevel")
    public JAXBElement<CmdGetDRLevel> createCmdGetDRLevel(CmdGetDRLevel value) {
        return new JAXBElement<CmdGetDRLevel>(_CmdGetDRLevel_QNAME, CmdGetDRLevel.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdKamstrupCID1Response }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdKamstrupCID1Response")
    public JAXBElement<CmdKamstrupCID1Response> createCmdKamstrupCID1Response(CmdKamstrupCID1Response value) {
        return new JAXBElement<CmdKamstrupCID1Response>(_CmdKamstrupCID1Response_QNAME, CmdKamstrupCID1Response.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdDistributionCancel }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdDistributionCancel")
    public JAXBElement<CmdDistributionCancel> createCmdDistributionCancel(CmdDistributionCancel value) {
        return new JAXBElement<CmdDistributionCancel>(_CmdDistributionCancel_QNAME, CmdDistributionCancel.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdEntryStdSet }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdEntryStdSet")
    public JAXBElement<CmdEntryStdSet> createCmdEntryStdSet(CmdEntryStdSet value) {
        return new JAXBElement<CmdEntryStdSet>(_CmdEntryStdSet_QNAME, CmdEntryStdSet.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetRelaySwitchStatus }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "getRelaySwitchStatus")
    public JAXBElement<GetRelaySwitchStatus> createGetRelaySwitchStatus(GetRelaySwitchStatus value) {
        return new JAXBElement<GetRelaySwitchStatus>(_GetRelaySwitchStatus_QNAME, GetRelaySwitchStatus.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdOnDemandMeterAll }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdOnDemandMeterAll")
    public JAXBElement<CmdOnDemandMeterAll> createCmdOnDemandMeterAll(CmdOnDemandMeterAll value) {
        return new JAXBElement<CmdOnDemandMeterAll>(_CmdOnDemandMeterAll_QNAME, CmdOnDemandMeterAll.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdExecuteCommandResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdExecuteCommandResponse")
    public JAXBElement<CmdExecuteCommandResponse> createCmdExecuteCommandResponse(CmdExecuteCommandResponse value) {
        return new JAXBElement<CmdExecuteCommandResponse>(_CmdExecuteCommandResponse_QNAME, CmdExecuteCommandResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdOnDemandMeterResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdOnDemandMeterResponse")
    public JAXBElement<CmdOnDemandMeterResponse> createCmdOnDemandMeterResponse(CmdOnDemandMeterResponse value) {
        return new JAXBElement<CmdOnDemandMeterResponse>(_CmdOnDemandMeterResponse_QNAME, CmdOnDemandMeterResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdSetCodiPermit }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdSetCodiPermit")
    public JAXBElement<CmdSetCodiPermit> createCmdSetCodiPermit(CmdSetCodiPermit value) {
        return new JAXBElement<CmdSetCodiPermit>(_CmdSetCodiPermit_QNAME, CmdSetCodiPermit.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdModifyTransaction }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdModifyTransaction")
    public JAXBElement<CmdModifyTransaction> createCmdModifyTransaction(CmdModifyTransaction value) {
        return new JAXBElement<CmdModifyTransaction>(_CmdModifyTransaction_QNAME, CmdModifyTransaction.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetCurMeterAllResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetCurMeterAllResponse")
    public JAXBElement<CmdGetCurMeterAllResponse> createCmdGetCurMeterAllResponse(CmdGetCurMeterAllResponse value) {
        return new JAXBElement<CmdGetCurMeterAllResponse>(_CmdGetCurMeterAllResponse_QNAME, CmdGetCurMeterAllResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdValveControl }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdValveControl")
    public JAXBElement<CmdValveControl> createCmdValveControl(CmdValveControl value) {
        return new JAXBElement<CmdValveControl>(_CmdValveControl_QNAME, CmdValveControl.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdOnDemandMeter }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdOnDemandMeter")
    public JAXBElement<CmdOnDemandMeter> createCmdOnDemandMeter(CmdOnDemandMeter value) {
        return new JAXBElement<CmdOnDemandMeter>(_CmdOnDemandMeter_QNAME, CmdOnDemandMeter.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGroupInfo1Response }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGroupInfo1Response")
    public JAXBElement<CmdGroupInfo1Response> createCmdGroupInfo1Response(CmdGroupInfo1Response value) {
        return new JAXBElement<CmdGroupInfo1Response>(_CmdGroupInfo1Response_QNAME, CmdGroupInfo1Response.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetCodiNeighbor1Response }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetCodiNeighbor1Response")
    public JAXBElement<CmdGetCodiNeighbor1Response> createCmdGetCodiNeighbor1Response(CmdGetCodiNeighbor1Response value) {
        return new JAXBElement<CmdGetCodiNeighbor1Response>(_CmdGetCodiNeighbor1Response_QNAME, CmdGetCodiNeighbor1Response.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetCodiListResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetCodiListResponse")
    public JAXBElement<CmdGetCodiListResponse> createCmdGetCodiListResponse(CmdGetCodiListResponse value) {
        return new JAXBElement<CmdGetCodiListResponse>(_CmdGetCodiListResponse_QNAME, CmdGetCodiListResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdMcuGetEnvironmentResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdMcuGetEnvironmentResponse")
    public JAXBElement<CmdMcuGetEnvironmentResponse> createCmdMcuGetEnvironmentResponse(CmdMcuGetEnvironmentResponse value) {
        return new JAXBElement<CmdMcuGetEnvironmentResponse>(_CmdMcuGetEnvironmentResponse_QNAME, CmdMcuGetEnvironmentResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdSmsFirmwareUpdateResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdSmsFirmwareUpdateResponse")
    public JAXBElement<CmdSmsFirmwareUpdateResponse> createCmdSmsFirmwareUpdateResponse(CmdSmsFirmwareUpdateResponse value) {
        return new JAXBElement<CmdSmsFirmwareUpdateResponse>(_CmdSmsFirmwareUpdateResponse_QNAME, CmdSmsFirmwareUpdateResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdMcuDiagnosis }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdMcuDiagnosis")
    public JAXBElement<CmdMcuDiagnosis> createCmdMcuDiagnosis(CmdMcuDiagnosis value) {
        return new JAXBElement<CmdMcuDiagnosis>(_CmdMcuDiagnosis_QNAME, CmdMcuDiagnosis.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdMeterProgram }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdMeterProgram")
    public JAXBElement<CmdMeterProgram> createCmdMeterProgram(CmdMeterProgram value) {
        return new JAXBElement<CmdMeterProgram>(_CmdMeterProgram_QNAME, CmdMeterProgram.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdSetLoadShedSchedule }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdSetLoadShedSchedule")
    public JAXBElement<CmdSetLoadShedSchedule> createCmdSetLoadShedSchedule(CmdSetLoadShedSchedule value) {
        return new JAXBElement<CmdSetLoadShedSchedule>(_CmdSetLoadShedSchedule_QNAME, CmdSetLoadShedSchedule.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdAidonMCCBResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdAidonMCCBResponse")
    public JAXBElement<CmdAidonMCCBResponse> createCmdAidonMCCBResponse(CmdAidonMCCBResponse value) {
        return new JAXBElement<CmdAidonMCCBResponse>(_CmdAidonMCCBResponse_QNAME, CmdAidonMCCBResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdSetEnergyLevel1 }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdSetEnergyLevel1")
    public JAXBElement<CmdSetEnergyLevel1> createCmdSetEnergyLevel1(CmdSetEnergyLevel1 value) {
        return new JAXBElement<CmdSetEnergyLevel1>(_CmdSetEnergyLevel1_QNAME, CmdSetEnergyLevel1 .class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdMeterProgramResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdMeterProgramResponse")
    public JAXBElement<CmdMeterProgramResponse> createCmdMeterProgramResponse(CmdMeterProgramResponse value) {
        return new JAXBElement<CmdMeterProgramResponse>(_CmdMeterProgramResponse_QNAME, CmdMeterProgramResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdOnDemandMeter2Response }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdOnDemandMeter2Response")
    public JAXBElement<CmdOnDemandMeter2Response> createCmdOnDemandMeter2Response(CmdOnDemandMeter2Response value) {
        return new JAXBElement<CmdOnDemandMeter2Response>(_CmdOnDemandMeter2Response_QNAME, CmdOnDemandMeter2Response.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdStdGet1 }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdStdGet1")
    public JAXBElement<CmdStdGet1> createCmdStdGet1(CmdStdGet1 value) {
        return new JAXBElement<CmdStdGet1>(_CmdStdGet1_QNAME, CmdStdGet1 .class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetMeterVersion }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetMeterVersion")
    public JAXBElement<CmdGetMeterVersion> createCmdGetMeterVersion(CmdGetMeterVersion value) {
        return new JAXBElement<CmdGetMeterVersion>(_CmdGetMeterVersion_QNAME, CmdGetMeterVersion.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdShowTransactionList }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdShowTransactionList")
    public JAXBElement<CmdShowTransactionList> createCmdShowTransactionList(CmdShowTransactionList value) {
        return new JAXBElement<CmdShowTransactionList>(_CmdShowTransactionList_QNAME, CmdShowTransactionList.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RequestPLCDataFrameResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "requestPLCDataFrameResponse")
    public JAXBElement<RequestPLCDataFrameResponse> createRequestPLCDataFrameResponse(RequestPLCDataFrameResponse value) {
        return new JAXBElement<RequestPLCDataFrameResponse>(_RequestPLCDataFrameResponse_QNAME, RequestPLCDataFrameResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdAddModemsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdAddModemsResponse")
    public JAXBElement<CmdAddModemsResponse> createCmdAddModemsResponse(CmdAddModemsResponse value) {
        return new JAXBElement<CmdAddModemsResponse>(_CmdAddModemsResponse_QNAME, CmdAddModemsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdClearCmdHistLog }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdClearCmdHistLog")
    public JAXBElement<CmdClearCmdHistLog> createCmdClearCmdHistLog(CmdClearCmdHistLog value) {
        return new JAXBElement<CmdClearCmdHistLog>(_CmdClearCmdHistLog_QNAME, CmdClearCmdHistLog.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetCodiInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetCodiInfo")
    public JAXBElement<CmdGetCodiInfo> createCmdGetCodiInfo(CmdGetCodiInfo value) {
        return new JAXBElement<CmdGetCodiInfo>(_CmdGetCodiInfo_QNAME, CmdGetCodiInfo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetModemBatteryResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetModemBatteryResponse")
    public JAXBElement<CmdGetModemBatteryResponse> createCmdGetModemBatteryResponse(CmdGetModemBatteryResponse value) {
        return new JAXBElement<CmdGetModemBatteryResponse>(_CmdGetModemBatteryResponse_QNAME, CmdGetModemBatteryResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdSetMeterTime }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdSetMeterTime")
    public JAXBElement<CmdSetMeterTime> createCmdSetMeterTime(CmdSetMeterTime value) {
        return new JAXBElement<CmdSetMeterTime>(_CmdSetMeterTime_QNAME, CmdSetMeterTime.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetMeterResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetMeterResponse")
    public JAXBElement<CmdGetMeterResponse> createCmdGetMeterResponse(CmdGetMeterResponse value) {
        return new JAXBElement<CmdGetMeterResponse>(_CmdGetMeterResponse_QNAME, CmdGetMeterResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdResetModem }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdResetModem")
    public JAXBElement<CmdResetModem> createCmdResetModem(CmdResetModem value) {
        return new JAXBElement<CmdResetModem>(_CmdResetModem_QNAME, CmdResetModem.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdDeleteModemResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdDeleteModemResponse")
    public JAXBElement<CmdDeleteModemResponse> createCmdDeleteModemResponse(CmdDeleteModemResponse value) {
        return new JAXBElement<CmdDeleteModemResponse>(_CmdDeleteModemResponse_QNAME, CmdDeleteModemResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGroupAdd }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGroupAdd")
    public JAXBElement<CmdGroupAdd> createCmdGroupAdd(CmdGroupAdd value) {
        return new JAXBElement<CmdGroupAdd>(_CmdGroupAdd_QNAME, CmdGroupAdd.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdRecoveryResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdRecoveryResponse")
    public JAXBElement<CmdRecoveryResponse> createCmdRecoveryResponse(CmdRecoveryResponse value) {
        return new JAXBElement<CmdRecoveryResponse>(_CmdRecoveryResponse_QNAME, CmdRecoveryResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetCodiMemory1 }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetCodiMemory1")
    public JAXBElement<CmdGetCodiMemory1> createCmdGetCodiMemory1(CmdGetCodiMemory1 value) {
        return new JAXBElement<CmdGetCodiMemory1>(_CmdGetCodiMemory1_QNAME, CmdGetCodiMemory1 .class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdPackageDistributionResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdPackageDistributionResponse")
    public JAXBElement<CmdPackageDistributionResponse> createCmdPackageDistributionResponse(CmdPackageDistributionResponse value) {
        return new JAXBElement<CmdPackageDistributionResponse>(_CmdPackageDistributionResponse_QNAME, CmdPackageDistributionResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdSetModemAmrDataResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdSetModemAmrDataResponse")
    public JAXBElement<CmdSetModemAmrDataResponse> createCmdSetModemAmrDataResponse(CmdSetModemAmrDataResponse value) {
        return new JAXBElement<CmdSetModemAmrDataResponse>(_CmdSetModemAmrDataResponse_QNAME, CmdSetModemAmrDataResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdSendIHDData }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdSendIHDData")
    public JAXBElement<CmdSendIHDData> createCmdSendIHDData(CmdSendIHDData value) {
        return new JAXBElement<CmdSendIHDData>(_CmdSendIHDData_QNAME, CmdSendIHDData.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdIDRCancel }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdIDRCancel")
    public JAXBElement<CmdIDRCancel> createCmdIDRCancel(CmdIDRCancel value) {
        return new JAXBElement<CmdIDRCancel>(_CmdIDRCancel_QNAME, CmdIDRCancel.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdOnDemandMBus }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdOnDemandMBus")
    public JAXBElement<CmdOnDemandMBus> createCmdOnDemandMBus(CmdOnDemandMBus value) {
        return new JAXBElement<CmdOnDemandMBus>(_CmdOnDemandMBus_QNAME, CmdOnDemandMBus.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetGroup }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetGroup")
    public JAXBElement<CmdGetGroup> createCmdGetGroup(CmdGetGroup value) {
        return new JAXBElement<CmdGetGroup>(_CmdGetGroup_QNAME, CmdGetGroup.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdCommandModem }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdCommandModem")
    public JAXBElement<CmdCommandModem> createCmdCommandModem(CmdCommandModem value) {
        return new JAXBElement<CmdCommandModem>(_CmdCommandModem_QNAME, CmdCommandModem.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdClearEventLog }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdClearEventLog")
    public JAXBElement<CmdClearEventLog> createCmdClearEventLog(CmdClearEventLog value) {
        return new JAXBElement<CmdClearEventLog>(_CmdClearEventLog_QNAME, CmdClearEventLog.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdDemandReset }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdDemandReset")
    public JAXBElement<CmdDemandReset> createCmdDemandReset(CmdDemandReset value) {
        return new JAXBElement<CmdDemandReset>(_CmdDemandReset_QNAME, CmdDemandReset.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdUpdateModemFirmware }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdUpdateModemFirmware")
    public JAXBElement<CmdUpdateModemFirmware> createCmdUpdateModemFirmware(CmdUpdateModemFirmware value) {
        return new JAXBElement<CmdUpdateModemFirmware>(_CmdUpdateModemFirmware_QNAME, CmdUpdateModemFirmware.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetCodiBinding1 }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetCodiBinding1")
    public JAXBElement<CmdGetCodiBinding1> createCmdGetCodiBinding1(CmdGetCodiBinding1 value) {
        return new JAXBElement<CmdGetCodiBinding1>(_CmdGetCodiBinding1_QNAME, CmdGetCodiBinding1 .class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdStdSetResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdStdSetResponse")
    public JAXBElement<CmdStdSetResponse> createCmdStdSetResponse(CmdStdSetResponse value) {
        return new JAXBElement<CmdStdSetResponse>(_CmdStdSetResponse_QNAME, CmdStdSetResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdMeterUploadRangeResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdMeterUploadRangeResponse")
    public JAXBElement<CmdMeterUploadRangeResponse> createCmdMeterUploadRangeResponse(CmdMeterUploadRangeResponse value) {
        return new JAXBElement<CmdMeterUploadRangeResponse>(_CmdMeterUploadRangeResponse_QNAME, CmdMeterUploadRangeResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdSetCodiPermitResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdSetCodiPermitResponse")
    public JAXBElement<CmdSetCodiPermitResponse> createCmdSetCodiPermitResponse(CmdSetCodiPermitResponse value) {
        return new JAXBElement<CmdSetCodiPermitResponse>(_CmdSetCodiPermitResponse_QNAME, CmdSetCodiPermitResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdStdGetResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdStdGetResponse")
    public JAXBElement<CmdStdGetResponse> createCmdStdGetResponse(CmdStdGetResponse value) {
        return new JAXBElement<CmdStdGetResponse>(_CmdStdGetResponse_QNAME, CmdStdGetResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdSendIHDDataResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdSendIHDDataResponse")
    public JAXBElement<CmdSendIHDDataResponse> createCmdSendIHDDataResponse(CmdSendIHDDataResponse value) {
        return new JAXBElement<CmdSendIHDDataResponse>(_CmdSendIHDDataResponse_QNAME, CmdSendIHDDataResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdMcuScanningResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdMcuScanningResponse")
    public JAXBElement<CmdMcuScanningResponse> createCmdMcuScanningResponse(CmdMcuScanningResponse value) {
        return new JAXBElement<CmdMcuScanningResponse>(_CmdMcuScanningResponse_QNAME, CmdMcuScanningResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdMcuGetGpioResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdMcuGetGpioResponse")
    public JAXBElement<CmdMcuGetGpioResponse> createCmdMcuGetGpioResponse(CmdMcuGetGpioResponse value) {
        return new JAXBElement<CmdMcuGetGpioResponse>(_CmdMcuGetGpioResponse_QNAME, CmdMcuGetGpioResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdCorrectModemPulseResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdCorrectModemPulseResponse")
    public JAXBElement<CmdCorrectModemPulseResponse> createCmdCorrectModemPulseResponse(CmdCorrectModemPulseResponse value) {
        return new JAXBElement<CmdCorrectModemPulseResponse>(_CmdCorrectModemPulseResponse_QNAME, CmdCorrectModemPulseResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetMeterInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetMeterInfo")
    public JAXBElement<CmdGetMeterInfo> createCmdGetMeterInfo(CmdGetMeterInfo value) {
        return new JAXBElement<CmdGetMeterInfo>(_CmdGetMeterInfo_QNAME, CmdGetMeterInfo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdSetModemROMResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdSetModemROMResponse")
    public JAXBElement<CmdSetModemROMResponse> createCmdSetModemROMResponse(CmdSetModemROMResponse value) {
        return new JAXBElement<CmdSetModemROMResponse>(_CmdSetModemROMResponse_QNAME, CmdSetModemROMResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdEntryStdGet }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdEntryStdGet")
    public JAXBElement<CmdEntryStdGet> createCmdEntryStdGet(CmdEntryStdGet value) {
        return new JAXBElement<CmdEntryStdGet>(_CmdEntryStdGet_QNAME, CmdEntryStdGet.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetRelaySwitchStatusResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "getRelaySwitchStatusResponse")
    public JAXBElement<GetRelaySwitchStatusResponse> createGetRelaySwitchStatusResponse(GetRelaySwitchStatusResponse value) {
        return new JAXBElement<GetRelaySwitchStatusResponse>(_GetRelaySwitchStatusResponse_QNAME, GetRelaySwitchStatusResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdMcuGetState }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdMcuGetState")
    public JAXBElement<CmdMcuGetState> createCmdMcuGetState(CmdMcuGetState value) {
        return new JAXBElement<CmdMcuGetState>(_CmdMcuGetState_QNAME, CmdMcuGetState.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdSetConfiguration }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdSetConfiguration")
    public JAXBElement<CmdSetConfiguration> createCmdSetConfiguration(CmdSetConfiguration value) {
        return new JAXBElement<CmdSetConfiguration>(_CmdSetConfiguration_QNAME, CmdSetConfiguration.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetMeterAllResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetMeterAllResponse")
    public JAXBElement<CmdGetMeterAllResponse> createCmdGetMeterAllResponse(CmdGetMeterAllResponse value) {
        return new JAXBElement<CmdGetMeterAllResponse>(_CmdGetMeterAllResponse_QNAME, CmdGetMeterAllResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdSetCiuLCDResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdSetCiuLCDResponse")
    public JAXBElement<CmdSetCiuLCDResponse> createCmdSetCiuLCDResponse(CmdSetCiuLCDResponse value) {
        return new JAXBElement<CmdSetCiuLCDResponse>(_CmdSetCiuLCDResponse_QNAME, CmdSetCiuLCDResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetDRAssetInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetDRAssetInfo")
    public JAXBElement<CmdGetDRAssetInfo> createCmdGetDRAssetInfo(CmdGetDRAssetInfo value) {
        return new JAXBElement<CmdGetDRAssetInfo>(_CmdGetDRAssetInfo_QNAME, CmdGetDRAssetInfo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdOnDemandMBusResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdOnDemandMBusResponse")
    public JAXBElement<CmdOnDemandMBusResponse> createCmdOnDemandMBusResponse(CmdOnDemandMBusResponse value) {
        return new JAXBElement<CmdOnDemandMBusResponse>(_CmdOnDemandMBusResponse_QNAME, CmdOnDemandMBusResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetModemROM }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetModemROM")
    public JAXBElement<CmdGetModemROM> createCmdGetModemROM(CmdGetModemROM value) {
        return new JAXBElement<CmdGetModemROM>(_CmdGetModemROM_QNAME, CmdGetModemROM.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetMeterTime }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetMeterTime")
    public JAXBElement<CmdGetMeterTime> createCmdGetMeterTime(CmdGetMeterTime value) {
        return new JAXBElement<CmdGetMeterTime>(_CmdGetMeterTime_QNAME, CmdGetMeterTime.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetMeterSecurity }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetMeterSecurity")
    public JAXBElement<CmdGetMeterSecurity> createCmdGetMeterSecurity(CmdGetMeterSecurity value) {
        return new JAXBElement<CmdGetMeterSecurity>(_CmdGetMeterSecurity_QNAME, CmdGetMeterSecurity.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetCodiDevice1 }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetCodiDevice1")
    public JAXBElement<CmdGetCodiDevice1> createCmdGetCodiDevice1(CmdGetCodiDevice1 value) {
        return new JAXBElement<CmdGetCodiDevice1>(_CmdGetCodiDevice1_QNAME, CmdGetCodiDevice1 .class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdDelIHDTableResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdDelIHDTableResponse")
    public JAXBElement<CmdDelIHDTableResponse> createCmdDelIHDTableResponse(CmdDelIHDTableResponse value) {
        return new JAXBElement<CmdDelIHDTableResponse>(_CmdDelIHDTableResponse_QNAME, CmdDelIHDTableResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetCmdHistList }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetCmdHistList")
    public JAXBElement<CmdGetCmdHistList> createCmdGetCmdHistList(CmdGetCmdHistList value) {
        return new JAXBElement<CmdGetCmdHistList>(_CmdGetCmdHistList_QNAME, CmdGetCmdHistList.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdInstallFile1 }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdInstallFile1")
    public JAXBElement<CmdInstallFile1> createCmdInstallFile1(CmdInstallFile1 value) {
        return new JAXBElement<CmdInstallFile1>(_CmdInstallFile1_QNAME, CmdInstallFile1 .class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdBypassSensor1Response }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdBypassSensor1Response")
    public JAXBElement<CmdBypassSensor1Response> createCmdBypassSensor1Response(CmdBypassSensor1Response value) {
        return new JAXBElement<CmdBypassSensor1Response>(_CmdBypassSensor1Response_QNAME, CmdBypassSensor1Response.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdInstallFile }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdInstallFile")
    public JAXBElement<CmdInstallFile> createCmdInstallFile(CmdInstallFile value) {
        return new JAXBElement<CmdInstallFile>(_CmdInstallFile_QNAME, CmdInstallFile.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdMeterTimeSyncResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdMeterTimeSyncResponse")
    public JAXBElement<CmdMeterTimeSyncResponse> createCmdMeterTimeSyncResponse(CmdMeterTimeSyncResponse value) {
        return new JAXBElement<CmdMeterTimeSyncResponse>(_CmdMeterTimeSyncResponse_QNAME, CmdMeterTimeSyncResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetModemEvent }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetModemEvent")
    public JAXBElement<CmdGetModemEvent> createCmdGetModemEvent(CmdGetModemEvent value) {
        return new JAXBElement<CmdGetModemEvent>(_CmdGetModemEvent_QNAME, CmdGetModemEvent.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdSetConfigurationResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdSetConfigurationResponse")
    public JAXBElement<CmdSetConfigurationResponse> createCmdSetConfigurationResponse(CmdSetConfigurationResponse value) {
        return new JAXBElement<CmdSetConfigurationResponse>(_CmdSetConfigurationResponse_QNAME, CmdSetConfigurationResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetMeterSchedule }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetMeterSchedule")
    public JAXBElement<CmdGetMeterSchedule> createCmdGetMeterSchedule(CmdGetMeterSchedule value) {
        return new JAXBElement<CmdGetMeterSchedule>(_CmdGetMeterSchedule_QNAME, CmdGetMeterSchedule.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdDeleteModemAllResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdDeleteModemAllResponse")
    public JAXBElement<CmdDeleteModemAllResponse> createCmdDeleteModemAllResponse(CmdDeleteModemAllResponse value) {
        return new JAXBElement<CmdDeleteModemAllResponse>(_CmdDeleteModemAllResponse_QNAME, CmdDeleteModemAllResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdOnRecoveryDemandMeter }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdOnRecoveryDemandMeter")
    public JAXBElement<CmdOnRecoveryDemandMeter> createCmdOnRecoveryDemandMeter(CmdOnRecoveryDemandMeter value) {
        return new JAXBElement<CmdOnRecoveryDemandMeter>(_CmdOnRecoveryDemandMeter_QNAME, CmdOnRecoveryDemandMeter.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdMeterTimeSync }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdMeterTimeSync")
    public JAXBElement<CmdMeterTimeSync> createCmdMeterTimeSync(CmdMeterTimeSync value) {
        return new JAXBElement<CmdMeterTimeSync>(_CmdMeterTimeSync_QNAME, CmdMeterTimeSync.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdMetering }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdMetering")
    public JAXBElement<CmdMetering> createCmdMetering(CmdMetering value) {
        return new JAXBElement<CmdMetering>(_CmdMetering_QNAME, CmdMetering.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdSendSMS }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdSendSMS")
    public JAXBElement<CmdSendSMS> createCmdSendSMS(CmdSendSMS value) {
        return new JAXBElement<CmdSendSMS>(_CmdSendSMS_QNAME, CmdSendSMS.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetModemROM1Response }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetModemROM1Response")
    public JAXBElement<CmdGetModemROM1Response> createCmdGetModemROM1Response(CmdGetModemROM1Response value) {
        return new JAXBElement<CmdGetModemROM1Response>(_CmdGetModemROM1Response_QNAME, CmdGetModemROM1Response.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdMcuShutdown }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdMcuShutdown")
    public JAXBElement<CmdMcuShutdown> createCmdMcuShutdown(CmdMcuShutdown value) {
        return new JAXBElement<CmdMcuShutdown>(_CmdMcuShutdown_QNAME, CmdMcuShutdown.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdSmsFirmwareUpdate }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdSmsFirmwareUpdate")
    public JAXBElement<CmdSmsFirmwareUpdate> createCmdSmsFirmwareUpdate(CmdSmsFirmwareUpdate value) {
        return new JAXBElement<CmdSmsFirmwareUpdate>(_CmdSmsFirmwareUpdate_QNAME, CmdSmsFirmwareUpdate.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdOnDemandMeter3Response }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdOnDemandMeter3Response")
    public JAXBElement<CmdOnDemandMeter3Response> createCmdOnDemandMeter3Response(CmdOnDemandMeter3Response value) {
        return new JAXBElement<CmdOnDemandMeter3Response>(_CmdOnDemandMeter3Response_QNAME, CmdOnDemandMeter3Response.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdStdSet1Response }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdStdSet1Response")
    public JAXBElement<CmdStdSet1Response> createCmdStdSet1Response(CmdStdSet1Response value) {
        return new JAXBElement<CmdStdSet1Response>(_CmdStdSet1Response_QNAME, CmdStdSet1Response.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdIDRCancelResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdIDRCancelResponse")
    public JAXBElement<CmdIDRCancelResponse> createCmdIDRCancelResponse(CmdIDRCancelResponse value) {
        return new JAXBElement<CmdIDRCancelResponse>(_CmdIDRCancelResponse_QNAME, CmdIDRCancelResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetMeterVersionResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetMeterVersionResponse")
    public JAXBElement<CmdGetMeterVersionResponse> createCmdGetMeterVersionResponse(CmdGetMeterVersionResponse value) {
        return new JAXBElement<CmdGetMeterVersionResponse>(_CmdGetMeterVersionResponse_QNAME, CmdGetMeterVersionResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdDemandResetResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdDemandResetResponse")
    public JAXBElement<CmdDemandResetResponse> createCmdDemandResetResponse(CmdDemandResetResponse value) {
        return new JAXBElement<CmdDemandResetResponse>(_CmdDemandResetResponse_QNAME, CmdDemandResetResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetCurMeterResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetCurMeterResponse")
    public JAXBElement<CmdGetCurMeterResponse> createCmdGetCurMeterResponse(CmdGetCurMeterResponse value) {
        return new JAXBElement<CmdGetCurMeterResponse>(_CmdGetCurMeterResponse_QNAME, CmdGetCurMeterResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdAddModemResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdAddModemResponse")
    public JAXBElement<CmdAddModemResponse> createCmdAddModemResponse(CmdAddModemResponse value) {
        return new JAXBElement<CmdAddModemResponse>(_CmdAddModemResponse_QNAME, CmdAddModemResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdSetIHDTableResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdSetIHDTableResponse")
    public JAXBElement<CmdSetIHDTableResponse> createCmdSetIHDTableResponse(CmdSetIHDTableResponse value) {
        return new JAXBElement<CmdSetIHDTableResponse>(_CmdSetIHDTableResponse_QNAME, CmdSetIHDTableResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdMcuResetDeviceResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdMcuResetDeviceResponse")
    public JAXBElement<CmdMcuResetDeviceResponse> createCmdMcuResetDeviceResponse(CmdMcuResetDeviceResponse value) {
        return new JAXBElement<CmdMcuResetDeviceResponse>(_CmdMcuResetDeviceResponse_QNAME, CmdMcuResetDeviceResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdStdGet }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdStdGet")
    public JAXBElement<CmdStdGet> createCmdStdGet(CmdStdGet value) {
        return new JAXBElement<CmdStdGet>(_CmdStdGet_QNAME, CmdStdGet.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdSetMeterTimeResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdSetMeterTimeResponse")
    public JAXBElement<CmdSetMeterTimeResponse> createCmdSetMeterTimeResponse(CmdSetMeterTimeResponse value) {
        return new JAXBElement<CmdSetMeterTimeResponse>(_CmdSetMeterTimeResponse_QNAME, CmdSetMeterTimeResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdClearMeterLogResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdClearMeterLogResponse")
    public JAXBElement<CmdClearMeterLogResponse> createCmdClearMeterLogResponse(CmdClearMeterLogResponse value) {
        return new JAXBElement<CmdClearMeterLogResponse>(_CmdClearMeterLogResponse_QNAME, CmdClearMeterLogResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdMcuLoopbackResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdMcuLoopbackResponse")
    public JAXBElement<CmdMcuLoopbackResponse> createCmdMcuLoopbackResponse(CmdMcuLoopbackResponse value) {
        return new JAXBElement<CmdMcuLoopbackResponse>(_CmdMcuLoopbackResponse_QNAME, CmdMcuLoopbackResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdUpdateGroupResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdUpdateGroupResponse")
    public JAXBElement<CmdUpdateGroupResponse> createCmdUpdateGroupResponse(CmdUpdateGroupResponse value) {
        return new JAXBElement<CmdUpdateGroupResponse>(_CmdUpdateGroupResponse_QNAME, CmdUpdateGroupResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DoGetModemClusterResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "doGetModemClusterResponse")
    public JAXBElement<DoGetModemClusterResponse> createDoGetModemClusterResponse(DoGetModemClusterResponse value) {
        return new JAXBElement<DoGetModemClusterResponse>(_DoGetModemClusterResponse_QNAME, DoGetModemClusterResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGroupInfo2Response }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGroupInfo2Response")
    public JAXBElement<CmdGroupInfo2Response> createCmdGroupInfo2Response(CmdGroupInfo2Response value) {
        return new JAXBElement<CmdGroupInfo2Response>(_CmdGroupInfo2Response_QNAME, CmdGroupInfo2Response.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdMcuGetMobileResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdMcuGetMobileResponse")
    public JAXBElement<CmdMcuGetMobileResponse> createCmdMcuGetMobileResponse(CmdMcuGetMobileResponse value) {
        return new JAXBElement<CmdMcuGetMobileResponse>(_CmdMcuGetMobileResponse_QNAME, CmdMcuGetMobileResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdAsynchronousCallResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdAsynchronousCallResponse")
    public JAXBElement<CmdAsynchronousCallResponse> createCmdAsynchronousCallResponse(CmdAsynchronousCallResponse value) {
        return new JAXBElement<CmdAsynchronousCallResponse>(_CmdAsynchronousCallResponse_QNAME, CmdAsynchronousCallResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdPutFile }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdPutFile")
    public JAXBElement<CmdPutFile> createCmdPutFile(CmdPutFile value) {
        return new JAXBElement<CmdPutFile>(_CmdPutFile_QNAME, CmdPutFile.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdSetCodiReset1 }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdSetCodiReset1")
    public JAXBElement<CmdSetCodiReset1> createCmdSetCodiReset1(CmdSetCodiReset1 value) {
        return new JAXBElement<CmdSetCodiReset1>(_CmdSetCodiReset1_QNAME, CmdSetCodiReset1 .class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdWriteTable }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdWriteTable")
    public JAXBElement<CmdWriteTable> createCmdWriteTable(CmdWriteTable value) {
        return new JAXBElement<CmdWriteTable>(_CmdWriteTable_QNAME, CmdWriteTable.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdClearCmdHistLogResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdClearCmdHistLogResponse")
    public JAXBElement<CmdClearCmdHistLogResponse> createCmdClearCmdHistLogResponse(CmdClearCmdHistLogResponse value) {
        return new JAXBElement<CmdClearCmdHistLogResponse>(_CmdClearCmdHistLogResponse_QNAME, CmdClearCmdHistLogResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdKamstrupCID }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdKamstrupCID")
    public JAXBElement<CmdKamstrupCID> createCmdKamstrupCID(CmdKamstrupCID value) {
        return new JAXBElement<CmdKamstrupCID>(_CmdKamstrupCID_QNAME, CmdKamstrupCID.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdMcuReset }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdMcuReset")
    public JAXBElement<CmdMcuReset> createCmdMcuReset(CmdMcuReset value) {
        return new JAXBElement<CmdMcuReset>(_CmdMcuReset_QNAME, CmdMcuReset.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetLoadLimitProperty }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetLoadLimitProperty")
    public JAXBElement<CmdGetLoadLimitProperty> createCmdGetLoadLimitProperty(CmdGetLoadLimitProperty value) {
        return new JAXBElement<CmdGetLoadLimitProperty>(_CmdGetLoadLimitProperty_QNAME, CmdGetLoadLimitProperty.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DoGetModemROMResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "doGetModemROMResponse")
    public JAXBElement<DoGetModemROMResponse> createDoGetModemROMResponse(DoGetModemROMResponse value) {
        return new JAXBElement<DoGetModemROMResponse>(_DoGetModemROMResponse_QNAME, DoGetModemROMResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetCurMeter }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetCurMeter")
    public JAXBElement<CmdGetCurMeter> createCmdGetCurMeter(CmdGetCurMeter value) {
        return new JAXBElement<CmdGetCurMeter>(_CmdGetCurMeter_QNAME, CmdGetCurMeter.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdMcuGetMemory }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdMcuGetMemory")
    public JAXBElement<CmdMcuGetMemory> createCmdMcuGetMemory(CmdMcuGetMemory value) {
        return new JAXBElement<CmdMcuGetMemory>(_CmdMcuGetMemory_QNAME, CmdMcuGetMemory.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdSetCiuLCD }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdSetCiuLCD")
    public JAXBElement<CmdSetCiuLCD> createCmdSetCiuLCD(CmdSetCiuLCD value) {
        return new JAXBElement<CmdSetCiuLCD>(_CmdSetCiuLCD_QNAME, CmdSetCiuLCD.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdMcuSetDST }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdMcuSetDST")
    public JAXBElement<CmdMcuSetDST> createCmdMcuSetDST(CmdMcuSetDST value) {
        return new JAXBElement<CmdMcuSetDST>(_CmdMcuSetDST_QNAME, CmdMcuSetDST.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdMcuGetStateResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdMcuGetStateResponse")
    public JAXBElement<CmdMcuGetStateResponse> createCmdMcuGetStateResponse(CmdMcuGetStateResponse value) {
        return new JAXBElement<CmdMcuGetStateResponse>(_CmdMcuGetStateResponse_QNAME, CmdMcuGetStateResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdReadTableResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdReadTableResponse")
    public JAXBElement<CmdReadTableResponse> createCmdReadTableResponse(CmdReadTableResponse value) {
        return new JAXBElement<CmdReadTableResponse>(_CmdReadTableResponse_QNAME, CmdReadTableResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdMcuScanning }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdMcuScanning")
    public JAXBElement<CmdMcuScanning> createCmdMcuScanning(CmdMcuScanning value) {
        return new JAXBElement<CmdMcuScanning>(_CmdMcuScanning_QNAME, CmdMcuScanning.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdExecuteCommand }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdExecuteCommand")
    public JAXBElement<CmdExecuteCommand> createCmdExecuteCommand(CmdExecuteCommand value) {
        return new JAXBElement<CmdExecuteCommand>(_CmdExecuteCommand_QNAME, CmdExecuteCommand.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdMcuGetEnvironment }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdMcuGetEnvironment")
    public JAXBElement<CmdMcuGetEnvironment> createCmdMcuGetEnvironment(CmdMcuGetEnvironment value) {
        return new JAXBElement<CmdMcuGetEnvironment>(_CmdMcuGetEnvironment_QNAME, CmdMcuGetEnvironment.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdFirmwareUpdateResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdFirmwareUpdateResponse")
    public JAXBElement<CmdFirmwareUpdateResponse> createCmdFirmwareUpdateResponse(CmdFirmwareUpdateResponse value) {
        return new JAXBElement<CmdFirmwareUpdateResponse>(_CmdFirmwareUpdateResponse_QNAME, CmdFirmwareUpdateResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetFFDListResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetFFDListResponse")
    public JAXBElement<CmdGetFFDListResponse> createCmdGetFFDListResponse(CmdGetFFDListResponse value) {
        return new JAXBElement<CmdGetFFDListResponse>(_CmdGetFFDListResponse_QNAME, CmdGetFFDListResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdBroadcastResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdBroadcastResponse")
    public JAXBElement<CmdBroadcastResponse> createCmdBroadcastResponse(CmdBroadcastResponse value) {
        return new JAXBElement<CmdBroadcastResponse>(_CmdBroadcastResponse_QNAME, CmdBroadcastResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdBroadcast }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdBroadcast")
    public JAXBElement<CmdBroadcast> createCmdBroadcast(CmdBroadcast value) {
        return new JAXBElement<CmdBroadcast>(_CmdBroadcast_QNAME, CmdBroadcast.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetCodiInfo1 }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetCodiInfo1")
    public JAXBElement<CmdGetCodiInfo1> createCmdGetCodiInfo1(CmdGetCodiInfo1 value) {
        return new JAXBElement<CmdGetCodiInfo1>(_CmdGetCodiInfo1_QNAME, CmdGetCodiInfo1 .class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdSetLoadShedScheduleResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdSetLoadShedScheduleResponse")
    public JAXBElement<CmdSetLoadShedScheduleResponse> createCmdSetLoadShedScheduleResponse(CmdSetLoadShedScheduleResponse value) {
        return new JAXBElement<CmdSetLoadShedScheduleResponse>(_CmdSetLoadShedScheduleResponse_QNAME, CmdSetLoadShedScheduleResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetCodiMemoryResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetCodiMemoryResponse")
    public JAXBElement<CmdGetCodiMemoryResponse> createCmdGetCodiMemoryResponse(CmdGetCodiMemoryResponse value) {
        return new JAXBElement<CmdGetCodiMemoryResponse>(_CmdGetCodiMemoryResponse_QNAME, CmdGetCodiMemoryResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetCodiDeviceResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetCodiDeviceResponse")
    public JAXBElement<CmdGetCodiDeviceResponse> createCmdGetCodiDeviceResponse(CmdGetCodiDeviceResponse value) {
        return new JAXBElement<CmdGetCodiDeviceResponse>(_CmdGetCodiDeviceResponse_QNAME, CmdGetCodiDeviceResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdRelaySwitchAndActivate }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdRelaySwitchAndActivate")
    public JAXBElement<CmdRelaySwitchAndActivate> createCmdRelaySwitchAndActivate(CmdRelaySwitchAndActivate value) {
        return new JAXBElement<CmdRelaySwitchAndActivate>(_CmdRelaySwitchAndActivate_QNAME, CmdRelaySwitchAndActivate.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdAddModem }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdAddModem")
    public JAXBElement<CmdAddModem> createCmdAddModem(CmdAddModem value) {
        return new JAXBElement<CmdAddModem>(_CmdAddModem_QNAME, CmdAddModem.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdMcuGetMobile }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdMcuGetMobile")
    public JAXBElement<CmdMcuGetMobile> createCmdMcuGetMobile(CmdMcuGetMobile value) {
        return new JAXBElement<CmdMcuGetMobile>(_CmdMcuGetMobile_QNAME, CmdMcuGetMobile.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdRecoveryByTargetTimeResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdRecoveryByTargetTimeResponse")
    public JAXBElement<CmdRecoveryByTargetTimeResponse> createCmdRecoveryByTargetTimeResponse(CmdRecoveryByTargetTimeResponse value) {
        return new JAXBElement<CmdRecoveryByTargetTimeResponse>(_CmdRecoveryByTargetTimeResponse_QNAME, CmdRecoveryByTargetTimeResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdSendMessageResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdSendMessageResponse")
    public JAXBElement<CmdSendMessageResponse> createCmdSendMessageResponse(CmdSendMessageResponse value) {
        return new JAXBElement<CmdSendMessageResponse>(_CmdSendMessageResponse_QNAME, CmdSendMessageResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetModemBattery }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetModemBattery")
    public JAXBElement<CmdGetModemBattery> createCmdGetModemBattery(CmdGetModemBattery value) {
        return new JAXBElement<CmdGetModemBattery>(_CmdGetModemBattery_QNAME, CmdGetModemBattery.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetEnergyLevel }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetEnergyLevel")
    public JAXBElement<CmdGetEnergyLevel> createCmdGetEnergyLevel(CmdGetEnergyLevel value) {
        return new JAXBElement<CmdGetEnergyLevel>(_CmdGetEnergyLevel_QNAME, CmdGetEnergyLevel.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetCmdHistListResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetCmdHistListResponse")
    public JAXBElement<CmdGetCmdHistListResponse> createCmdGetCmdHistListResponse(CmdGetCmdHistListResponse value) {
        return new JAXBElement<CmdGetCmdHistListResponse>(_CmdGetCmdHistListResponse_QNAME, CmdGetCmdHistListResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdSetPhoneList }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdSetPhoneList")
    public JAXBElement<CmdSetPhoneList> createCmdSetPhoneList(CmdSetPhoneList value) {
        return new JAXBElement<CmdSetPhoneList>(_CmdSetPhoneList_QNAME, CmdSetPhoneList.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdMcuFactoryDefault }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdMcuFactoryDefault")
    public JAXBElement<CmdMcuFactoryDefault> createCmdMcuFactoryDefault(CmdMcuFactoryDefault value) {
        return new JAXBElement<CmdMcuFactoryDefault>(_CmdMcuFactoryDefault_QNAME, CmdMcuFactoryDefault.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdSetDRLevelResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdSetDRLevelResponse")
    public JAXBElement<CmdSetDRLevelResponse> createCmdSetDRLevelResponse(CmdSetDRLevelResponse value) {
        return new JAXBElement<CmdSetDRLevelResponse>(_CmdSetDRLevelResponse_QNAME, CmdSetDRLevelResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdSetEnergyLevelResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdSetEnergyLevelResponse")
    public JAXBElement<CmdSetEnergyLevelResponse> createCmdSetEnergyLevelResponse(CmdSetEnergyLevelResponse value) {
        return new JAXBElement<CmdSetEnergyLevelResponse>(_CmdSetEnergyLevelResponse_QNAME, CmdSetEnergyLevelResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetLoadControlScheme }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetLoadControlScheme")
    public JAXBElement<CmdGetLoadControlScheme> createCmdGetLoadControlScheme(CmdGetLoadControlScheme value) {
        return new JAXBElement<CmdGetLoadControlScheme>(_CmdGetLoadControlScheme_QNAME, CmdGetLoadControlScheme.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdShowTransactionInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdShowTransactionInfo")
    public JAXBElement<CmdShowTransactionInfo> createCmdShowTransactionInfo(CmdShowTransactionInfo value) {
        return new JAXBElement<CmdShowTransactionInfo>(_CmdShowTransactionInfo_QNAME, CmdShowTransactionInfo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetConfigurationResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetConfigurationResponse")
    public JAXBElement<CmdGetConfigurationResponse> createCmdGetConfigurationResponse(CmdGetConfigurationResponse value) {
        return new JAXBElement<CmdGetConfigurationResponse>(_CmdGetConfigurationResponse_QNAME, CmdGetConfigurationResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetMeterInfoFromModem }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetMeterInfoFromModem")
    public JAXBElement<CmdGetMeterInfoFromModem> createCmdGetMeterInfoFromModem(CmdGetMeterInfoFromModem value) {
        return new JAXBElement<CmdGetMeterInfoFromModem>(_CmdGetMeterInfoFromModem_QNAME, CmdGetMeterInfoFromModem.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdOnRecoveryDemandMeterResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdOnRecoveryDemandMeterResponse")
    public JAXBElement<CmdOnRecoveryDemandMeterResponse> createCmdOnRecoveryDemandMeterResponse(CmdOnRecoveryDemandMeterResponse value) {
        return new JAXBElement<CmdOnRecoveryDemandMeterResponse>(_CmdOnRecoveryDemandMeterResponse_QNAME, CmdOnRecoveryDemandMeterResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdResetModemResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdResetModemResponse")
    public JAXBElement<CmdResetModemResponse> createCmdResetModemResponse(CmdResetModemResponse value) {
        return new JAXBElement<CmdResetModemResponse>(_CmdResetModemResponse_QNAME, CmdResetModemResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdDeleteLoadLimitScheme }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdDeleteLoadLimitScheme")
    public JAXBElement<CmdDeleteLoadLimitScheme> createCmdDeleteLoadLimitScheme(CmdDeleteLoadLimitScheme value) {
        return new JAXBElement<CmdDeleteLoadLimitScheme>(_CmdDeleteLoadLimitScheme_QNAME, CmdDeleteLoadLimitScheme.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdMcuGetSystemInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdMcuGetSystemInfo")
    public JAXBElement<CmdMcuGetSystemInfo> createCmdMcuGetSystemInfo(CmdMcuGetSystemInfo value) {
        return new JAXBElement<CmdMcuGetSystemInfo>(_CmdMcuGetSystemInfo_QNAME, CmdMcuGetSystemInfo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdDeleteModelAllByChannelPan }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdDeleteModelAllByChannelPan")
    public JAXBElement<CmdDeleteModelAllByChannelPan> createCmdDeleteModelAllByChannelPan(CmdDeleteModelAllByChannelPan value) {
        return new JAXBElement<CmdDeleteModelAllByChannelPan>(_CmdDeleteModelAllByChannelPan_QNAME, CmdDeleteModelAllByChannelPan.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdDistribution }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdDistribution")
    public JAXBElement<CmdDistribution> createCmdDistribution(CmdDistribution value) {
        return new JAXBElement<CmdDistribution>(_CmdDistribution_QNAME, CmdDistribution.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdStdGetChild }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdStdGetChild")
    public JAXBElement<CmdStdGetChild> createCmdStdGetChild(CmdStdGetChild value) {
        return new JAXBElement<CmdStdGetChild>(_CmdStdGetChild_QNAME, CmdStdGetChild.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdStdGetChildResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdStdGetChildResponse")
    public JAXBElement<CmdStdGetChildResponse> createCmdStdGetChildResponse(CmdStdGetChildResponse value) {
        return new JAXBElement<CmdStdGetChildResponse>(_CmdStdGetChildResponse_QNAME, CmdStdGetChildResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdDeleteLoadControlScheme }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdDeleteLoadControlScheme")
    public JAXBElement<CmdDeleteLoadControlScheme> createCmdDeleteLoadControlScheme(CmdDeleteLoadControlScheme value) {
        return new JAXBElement<CmdDeleteLoadControlScheme>(_CmdDeleteLoadControlScheme_QNAME, CmdDeleteLoadControlScheme.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetCodiList }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetCodiList")
    public JAXBElement<CmdGetCodiList> createCmdGetCodiList(CmdGetCodiList value) {
        return new JAXBElement<CmdGetCodiList>(_CmdGetCodiList_QNAME, CmdGetCodiList.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdACDResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdACDResponse")
    public JAXBElement<CmdACDResponse> createCmdACDResponse(CmdACDResponse value) {
        return new JAXBElement<CmdACDResponse>(_CmdACDResponse_QNAME, CmdACDResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdShowTransactionInfoResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdShowTransactionInfoResponse")
    public JAXBElement<CmdShowTransactionInfoResponse> createCmdShowTransactionInfoResponse(CmdShowTransactionInfoResponse value) {
        return new JAXBElement<CmdShowTransactionInfoResponse>(_CmdShowTransactionInfoResponse_QNAME, CmdShowTransactionInfoResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdIDRStartResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdIDRStartResponse")
    public JAXBElement<CmdIDRStartResponse> createCmdIDRStartResponse(CmdIDRStartResponse value) {
        return new JAXBElement<CmdIDRStartResponse>(_CmdIDRStartResponse_QNAME, CmdIDRStartResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SendBypassOpenSMSResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "sendBypassOpenSMSResponse")
    public JAXBElement<SendBypassOpenSMSResponse> createSendBypassOpenSMSResponse(SendBypassOpenSMSResponse value) {
        return new JAXBElement<SendBypassOpenSMSResponse>(_SendBypassOpenSMSResponse_QNAME, SendBypassOpenSMSResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdSetModemROM }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdSetModemROM")
    public JAXBElement<CmdSetModemROM> createCmdSetModemROM(CmdSetModemROM value) {
        return new JAXBElement<CmdSetModemROM>(_CmdSetModemROM_QNAME, CmdSetModemROM.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetLoadShedSchedule }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetLoadShedSchedule")
    public JAXBElement<CmdGetLoadShedSchedule> createCmdGetLoadShedSchedule(CmdGetLoadShedSchedule value) {
        return new JAXBElement<CmdGetLoadShedSchedule>(_CmdGetLoadShedSchedule_QNAME, CmdGetLoadShedSchedule.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdMeterFactoryResetResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdMeterFactoryResetResponse")
    public JAXBElement<CmdMeterFactoryResetResponse> createCmdMeterFactoryResetResponse(CmdMeterFactoryResetResponse value) {
        return new JAXBElement<CmdMeterFactoryResetResponse>(_CmdMeterFactoryResetResponse_QNAME, CmdMeterFactoryResetResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdDeleteGroup }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdDeleteGroup")
    public JAXBElement<CmdDeleteGroup> createCmdDeleteGroup(CmdDeleteGroup value) {
        return new JAXBElement<CmdDeleteGroup>(_CmdDeleteGroup_QNAME, CmdDeleteGroup.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetPhoneListResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetPhoneListResponse")
    public JAXBElement<CmdGetPhoneListResponse> createCmdGetPhoneListResponse(CmdGetPhoneListResponse value) {
        return new JAXBElement<CmdGetPhoneListResponse>(_CmdGetPhoneListResponse_QNAME, CmdGetPhoneListResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetFileResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetFileResponse")
    public JAXBElement<CmdGetFileResponse> createCmdGetFileResponse(CmdGetFileResponse value) {
        return new JAXBElement<CmdGetFileResponse>(_CmdGetFileResponse_QNAME, CmdGetFileResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdStdSet }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdStdSet")
    public JAXBElement<CmdStdSet> createCmdStdSet(CmdStdSet value) {
        return new JAXBElement<CmdStdSet>(_CmdStdSet_QNAME, CmdStdSet.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdMcuGetTime }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdMcuGetTime")
    public JAXBElement<CmdMcuGetTime> createCmdMcuGetTime(CmdMcuGetTime value) {
        return new JAXBElement<CmdMcuGetTime>(_CmdMcuGetTime_QNAME, CmdMcuGetTime.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetModemROM1 }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetModemROM1")
    public JAXBElement<CmdGetModemROM1> createCmdGetModemROM1(CmdGetModemROM1 value) {
        return new JAXBElement<CmdGetModemROM1>(_CmdGetModemROM1_QNAME, CmdGetModemROM1 .class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdSensorLPLogRecovery }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdSensorLPLogRecovery")
    public JAXBElement<CmdSensorLPLogRecovery> createCmdSensorLPLogRecovery(CmdSensorLPLogRecovery value) {
        return new JAXBElement<CmdSensorLPLogRecovery>(_CmdSensorLPLogRecovery_QNAME, CmdSensorLPLogRecovery.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetMeterTimeResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetMeterTimeResponse")
    public JAXBElement<CmdGetMeterTimeResponse> createCmdGetMeterTimeResponse(CmdGetMeterTimeResponse value) {
        return new JAXBElement<CmdGetMeterTimeResponse>(_CmdGetMeterTimeResponse_QNAME, CmdGetMeterTimeResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdBypassSensor1 }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdBypassSensor1")
    public JAXBElement<CmdBypassSensor1> createCmdBypassSensor1(CmdBypassSensor1 value) {
        return new JAXBElement<CmdBypassSensor1>(_CmdBypassSensor1_QNAME, CmdBypassSensor1 .class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdMcuGetIp }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdMcuGetIp")
    public JAXBElement<CmdMcuGetIp> createCmdMcuGetIp(CmdMcuGetIp value) {
        return new JAXBElement<CmdMcuGetIp>(_CmdMcuGetIp_QNAME, CmdMcuGetIp.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdSetCodiFormNetwork }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdSetCodiFormNetwork")
    public JAXBElement<CmdSetCodiFormNetwork> createCmdSetCodiFormNetwork(CmdSetCodiFormNetwork value) {
        return new JAXBElement<CmdSetCodiFormNetwork>(_CmdSetCodiFormNetwork_QNAME, CmdSetCodiFormNetwork.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdSetMeterConfigResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdSetMeterConfigResponse")
    public JAXBElement<CmdSetMeterConfigResponse> createCmdSetMeterConfigResponse(CmdSetMeterConfigResponse value) {
        return new JAXBElement<CmdSetMeterConfigResponse>(_CmdSetMeterConfigResponse_QNAME, CmdSetMeterConfigResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdSetLoadLimitProperty }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdSetLoadLimitProperty")
    public JAXBElement<CmdSetLoadLimitProperty> createCmdSetLoadLimitProperty(CmdSetLoadLimitProperty value) {
        return new JAXBElement<CmdSetLoadLimitProperty>(_CmdSetLoadLimitProperty_QNAME, CmdSetLoadLimitProperty.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetCodiNeighborResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetCodiNeighborResponse")
    public JAXBElement<CmdGetCodiNeighborResponse> createCmdGetCodiNeighborResponse(CmdGetCodiNeighborResponse value) {
        return new JAXBElement<CmdGetCodiNeighborResponse>(_CmdGetCodiNeighborResponse_QNAME, CmdGetCodiNeighborResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdDeleteMemberAllResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdDeleteMemberAllResponse")
    public JAXBElement<CmdDeleteMemberAllResponse> createCmdDeleteMemberAllResponse(CmdDeleteMemberAllResponse value) {
        return new JAXBElement<CmdDeleteMemberAllResponse>(_CmdDeleteMemberAllResponse_QNAME, CmdDeleteMemberAllResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdBypassMeterProgram }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdBypassMeterProgram")
    public JAXBElement<CmdBypassMeterProgram> createCmdBypassMeterProgram(CmdBypassMeterProgram value) {
        return new JAXBElement<CmdBypassMeterProgram>(_CmdBypassMeterProgram_QNAME, CmdBypassMeterProgram.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdDistributionCancelResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdDistributionCancelResponse")
    public JAXBElement<CmdDistributionCancelResponse> createCmdDistributionCancelResponse(CmdDistributionCancelResponse value) {
        return new JAXBElement<CmdDistributionCancelResponse>(_CmdDistributionCancelResponse_QNAME, CmdDistributionCancelResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdDRCancelResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdDRCancelResponse")
    public JAXBElement<CmdDRCancelResponse> createCmdDRCancelResponse(CmdDRCancelResponse value) {
        return new JAXBElement<CmdDRCancelResponse>(_CmdDRCancelResponse_QNAME, CmdDRCancelResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdReportCurMeterResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdReportCurMeterResponse")
    public JAXBElement<CmdReportCurMeterResponse> createCmdReportCurMeterResponse(CmdReportCurMeterResponse value) {
        return new JAXBElement<CmdReportCurMeterResponse>(_CmdReportCurMeterResponse_QNAME, CmdReportCurMeterResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdStdGet1Response }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdStdGet1Response")
    public JAXBElement<CmdStdGet1Response> createCmdStdGet1Response(CmdStdGet1Response value) {
        return new JAXBElement<CmdStdGet1Response>(_CmdStdGet1Response_QNAME, CmdStdGet1Response.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetMobileLogList }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetMobileLogList")
    public JAXBElement<CmdGetMobileLogList> createCmdGetMobileLogList(CmdGetMobileLogList value) {
        return new JAXBElement<CmdGetMobileLogList>(_CmdGetMobileLogList_QNAME, CmdGetMobileLogList.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdSetMeterSecurity }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdSetMeterSecurity")
    public JAXBElement<CmdSetMeterSecurity> createCmdSetMeterSecurity(CmdSetMeterSecurity value) {
        return new JAXBElement<CmdSetMeterSecurity>(_CmdSetMeterSecurity_QNAME, CmdSetMeterSecurity.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdRecoveryByTargetTime }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdRecoveryByTargetTime")
    public JAXBElement<CmdRecoveryByTargetTime> createCmdRecoveryByTargetTime(CmdRecoveryByTargetTime value) {
        return new JAXBElement<CmdRecoveryByTargetTime>(_CmdRecoveryByTargetTime_QNAME, CmdRecoveryByTargetTime.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdDeleteMemberAll }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdDeleteMemberAll")
    public JAXBElement<CmdDeleteMemberAll> createCmdDeleteMemberAll(CmdDeleteMemberAll value) {
        return new JAXBElement<CmdDeleteMemberAll>(_CmdDeleteMemberAll_QNAME, CmdDeleteMemberAll.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdBypassSensorResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdBypassSensorResponse")
    public JAXBElement<CmdBypassSensorResponse> createCmdBypassSensorResponse(CmdBypassSensorResponse value) {
        return new JAXBElement<CmdBypassSensorResponse>(_CmdBypassSensorResponse_QNAME, CmdBypassSensorResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdDeleteModelAllByChannelPanResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdDeleteModelAllByChannelPanResponse")
    public JAXBElement<CmdDeleteModelAllByChannelPanResponse> createCmdDeleteModelAllByChannelPanResponse(CmdDeleteModelAllByChannelPanResponse value) {
        return new JAXBElement<CmdDeleteModelAllByChannelPanResponse>(_CmdDeleteModelAllByChannelPanResponse_QNAME, CmdDeleteModelAllByChannelPanResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdMeteringResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdMeteringResponse")
    public JAXBElement<CmdMeteringResponse> createCmdMeteringResponse(CmdMeteringResponse value) {
        return new JAXBElement<CmdMeteringResponse>(_CmdMeteringResponse_QNAME, CmdMeteringResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGroupAddMember }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGroupAddMember")
    public JAXBElement<CmdGroupAddMember> createCmdGroupAddMember(CmdGroupAddMember value) {
        return new JAXBElement<CmdGroupAddMember>(_CmdGroupAddMember_QNAME, CmdGroupAddMember.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdKamstrupCID1 }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdKamstrupCID1")
    public JAXBElement<CmdKamstrupCID1> createCmdKamstrupCID1(CmdKamstrupCID1 value) {
        return new JAXBElement<CmdKamstrupCID1>(_CmdKamstrupCID1_QNAME, CmdKamstrupCID1 .class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdOnDemandMeterAsync }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdOnDemandMeterAsync")
    public JAXBElement<CmdOnDemandMeterAsync> createCmdOnDemandMeterAsync(CmdOnDemandMeterAsync value) {
        return new JAXBElement<CmdOnDemandMeterAsync>(_CmdOnDemandMeterAsync_QNAME, CmdOnDemandMeterAsync.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdMeteringAllResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdMeteringAllResponse")
    public JAXBElement<CmdMeteringAllResponse> createCmdMeteringAllResponse(CmdMeteringAllResponse value) {
        return new JAXBElement<CmdMeteringAllResponse>(_CmdMeteringAllResponse_QNAME, CmdMeteringAllResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdSendMessage }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdSendMessage")
    public JAXBElement<CmdSendMessage> createCmdSendMessage(CmdSendMessage value) {
        return new JAXBElement<CmdSendMessage>(_CmdSendMessage_QNAME, CmdSendMessage.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGroupDeleteMember }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGroupDeleteMember")
    public JAXBElement<CmdGroupDeleteMember> createCmdGroupDeleteMember(CmdGroupDeleteMember value) {
        return new JAXBElement<CmdGroupDeleteMember>(_CmdGroupDeleteMember_QNAME, CmdGroupDeleteMember.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetAllLogListResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetAllLogListResponse")
    public JAXBElement<CmdGetAllLogListResponse> createCmdGetAllLogListResponse(CmdGetAllLogListResponse value) {
        return new JAXBElement<CmdGetAllLogListResponse>(_CmdGetAllLogListResponse_QNAME, CmdGetAllLogListResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetEnergyLevelResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetEnergyLevelResponse")
    public JAXBElement<CmdGetEnergyLevelResponse> createCmdGetEnergyLevelResponse(CmdGetEnergyLevelResponse value) {
        return new JAXBElement<CmdGetEnergyLevelResponse>(_CmdGetEnergyLevelResponse_QNAME, CmdGetEnergyLevelResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetEventLogList }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetEventLogList")
    public JAXBElement<CmdGetEventLogList> createCmdGetEventLogList(CmdGetEventLogList value) {
        return new JAXBElement<CmdGetEventLogList>(_CmdGetEventLogList_QNAME, CmdGetEventLogList.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdMcuSetDSTResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdMcuSetDSTResponse")
    public JAXBElement<CmdMcuSetDSTResponse> createCmdMcuSetDSTResponse(CmdMcuSetDSTResponse value) {
        return new JAXBElement<CmdMcuSetDSTResponse>(_CmdMcuSetDSTResponse_QNAME, CmdMcuSetDSTResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdDeleteModemByChannelPan }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdDeleteModemByChannelPan")
    public JAXBElement<CmdDeleteModemByChannelPan> createCmdDeleteModemByChannelPan(CmdDeleteModemByChannelPan value) {
        return new JAXBElement<CmdDeleteModemByChannelPan>(_CmdDeleteModemByChannelPan_QNAME, CmdDeleteModemByChannelPan.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetCodiInfo1Response }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetCodiInfo1Response")
    public JAXBElement<CmdGetCodiInfo1Response> createCmdGetCodiInfo1Response(CmdGetCodiInfo1Response value) {
        return new JAXBElement<CmdGetCodiInfo1Response>(_CmdGetCodiInfo1Response_QNAME, CmdGetCodiInfo1Response.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetModemAllNew }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetModemAllNew")
    public JAXBElement<CmdGetModemAllNew> createCmdGetModemAllNew(CmdGetModemAllNew value) {
        return new JAXBElement<CmdGetModemAllNew>(_CmdGetModemAllNew_QNAME, CmdGetModemAllNew.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdSendSMS1 }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdSendSMS1")
    public JAXBElement<CmdSendSMS1> createCmdSendSMS1(CmdSendSMS1 value) {
        return new JAXBElement<CmdSendSMS1>(_CmdSendSMS1_QNAME, CmdSendSMS1 .class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdMeterUploadRange }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdMeterUploadRange")
    public JAXBElement<CmdMeterUploadRange> createCmdMeterUploadRange(CmdMeterUploadRange value) {
        return new JAXBElement<CmdMeterUploadRange>(_CmdMeterUploadRange_QNAME, CmdMeterUploadRange.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdMcuGetGpio }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdMcuGetGpio")
    public JAXBElement<CmdMcuGetGpio> createCmdMcuGetGpio(CmdMcuGetGpio value) {
        return new JAXBElement<CmdMcuGetGpio>(_CmdMcuGetGpio_QNAME, CmdMcuGetGpio.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdSendSMS1Response }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdSendSMS1Response")
    public JAXBElement<CmdSendSMS1Response> createCmdSendSMS1Response(CmdSendSMS1Response value) {
        return new JAXBElement<CmdSendSMS1Response>(_CmdSendSMS1Response_QNAME, CmdSendSMS1Response.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdDelIHDTable }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdDelIHDTable")
    public JAXBElement<CmdDelIHDTable> createCmdDelIHDTable(CmdDelIHDTable value) {
        return new JAXBElement<CmdDelIHDTable>(_CmdDelIHDTable_QNAME, CmdDelIHDTable.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdStdSet1 }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdStdSet1")
    public JAXBElement<CmdStdSet1> createCmdStdSet1(CmdStdSet1 value) {
        return new JAXBElement<CmdStdSet1>(_CmdStdSet1_QNAME, CmdStdSet1 .class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGroupAddMemberResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGroupAddMemberResponse")
    public JAXBElement<CmdGroupAddMemberResponse> createCmdGroupAddMemberResponse(CmdGroupAddMemberResponse value) {
        return new JAXBElement<CmdGroupAddMemberResponse>(_CmdGroupAddMemberResponse_QNAME, CmdGroupAddMemberResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetMeterInfoResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetMeterInfoResponse")
    public JAXBElement<CmdGetMeterInfoResponse> createCmdGetMeterInfoResponse(CmdGetMeterInfoResponse value) {
        return new JAXBElement<CmdGetMeterInfoResponse>(_CmdGetMeterInfoResponse_QNAME, CmdGetMeterInfoResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdDeleteModem }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdDeleteModem")
    public JAXBElement<CmdDeleteModem> createCmdDeleteModem(CmdDeleteModem value) {
        return new JAXBElement<CmdDeleteModem>(_CmdDeleteModem_QNAME, CmdDeleteModem.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdGetModemCountResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdGetModemCountResponse")
    public JAXBElement<CmdGetModemCountResponse> createCmdGetModemCountResponse(CmdGetModemCountResponse value) {
        return new JAXBElement<CmdGetModemCountResponse>(_CmdGetModemCountResponse_QNAME, CmdGetModemCountResponse.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdKDValveControl }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdKDValveControl")
    public JAXBElement<CmdKDValveControl> createCmdKDValveControl(CmdKDValveControl value) {
        return new JAXBElement<CmdKDValveControl>(_CmdKDValveControl_QNAME, CmdKDValveControl.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdKDGetMeterStatus }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdKDGetMeterStatus")
    public JAXBElement<CmdKDGetMeterStatus> createCmdKDGetMeterStatus(CmdKDGetMeterStatus value) {
        return new JAXBElement<CmdKDGetMeterStatus>(_CmdKDGetMeterStatus_QNAME, CmdKDGetMeterStatus.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdKDGetMeterVersion }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdKDGetMeterVersion")
    public JAXBElement<CmdKDGetMeterVersion> createCmdKDGetMeterVersion(CmdKDGetMeterVersion value) {
        return new JAXBElement<CmdKDGetMeterVersion>(_CmdKDGetMeterVersion_QNAME, CmdKDGetMeterVersion.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdKDSetMeterConfig }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdKDSetMeterConfig")
    public JAXBElement<CmdKDSetMeterConfig> createCmdKDSetMeterConfig(CmdKDSetMeterConfig value) {
        return new JAXBElement<CmdKDSetMeterConfig>(_CmdKDSetMeterConfig_QNAME, CmdKDSetMeterConfig.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdKDValveControlResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdKDValveControlResponse")
    public JAXBElement<CmdKDValveControlResponse> createCmdKDValveControlResponse(CmdKDValveControlResponse value) {
        return new JAXBElement<CmdKDValveControlResponse>(_CmdKDValveControlResponse_QNAME, CmdKDValveControlResponse.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdKDGetMeterStatusResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdKDGetMeterStatusResponse")
    public JAXBElement<CmdKDGetMeterStatusResponse> createCmdKDGetMeterStatusResponse(CmdKDGetMeterStatusResponse value) {
        return new JAXBElement<CmdKDGetMeterStatusResponse>(_CmdKDGetMeterStatusResponse_QNAME, CmdKDGetMeterStatusResponse.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdKDGetMeterVersionResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdKDGetMeterVersionResponse")
    public JAXBElement<CmdKDGetMeterVersionResponse> createCmdKDGetMeterVersionResponse(CmdKDGetMeterVersionResponse value) {
        return new JAXBElement<CmdKDGetMeterVersionResponse>(_CmdKDGetMeterVersionResponse_QNAME,CmdKDGetMeterVersionResponse.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmdKDSetMeterConfigResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ws.command.fep.aimir.com/", name = "cmdKDSetMeterConfigResponse")
    public JAXBElement<CmdKDSetMeterConfigResponse> createCmdKDSetMeterConfigResponse(CmdKDSetMeterConfigResponse value) {
        return new JAXBElement<CmdKDSetMeterConfigResponse>(_CmdKDSetMeterConfigResponse_QNAME, CmdKDSetMeterConfigResponse.class, null, value);
    }

}
