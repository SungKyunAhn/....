/*
 * @(#) CommandGWBean.java  1.0 05/12/03
 *
 * Copyright @ 2005 - 2006 Nuritelecom
 * All rights reserved.
 *
 * this is Command Gate-way class
 */
package com.aimir.fep.command.mbean;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.Base64Utils;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.DataSVC;
import com.aimir.constants.CommonConstants.DefaultCmdResult;
import com.aimir.constants.CommonConstants.EventStatus;
import com.aimir.constants.CommonConstants.McuType;
import com.aimir.constants.CommonConstants.MeterModel;
import com.aimir.constants.CommonConstants.MeterProgramKind;
import com.aimir.constants.CommonConstants.MeterVendor;
import com.aimir.constants.CommonConstants.ModemNetworkType;
import com.aimir.constants.CommonConstants.ModemPowerType;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.MonitorType;
import com.aimir.constants.CommonConstants.OTATargetType;
import com.aimir.constants.CommonConstants.OTAType;
import com.aimir.constants.CommonConstants.OperatorType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.constants.CommonConstants.TR_OPTION;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.EventAlertLogDao;
import com.aimir.dao.device.FirmwareDao;
import com.aimir.dao.device.MCUCodiDao;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.MeterProgramDao;
import com.aimir.dao.system.MeterProgramLogDao;
import com.aimir.fep.bypass.Bypass;
import com.aimir.fep.bypass.BypassRegister;
import com.aimir.fep.command.conf.DLMSMeta.LOAD_CONTROL_STATUS;
import com.aimir.fep.command.conf.KamstrupCIDMeta;
import com.aimir.fep.command.conf.Mccb;
import com.aimir.fep.command.conf.SM110Meta;
import com.aimir.fep.meter.AbstractMDSaver;
import com.aimir.fep.meter.MeterDataSaverMain;
import com.aimir.fep.meter.data.MDHistoryData;
import com.aimir.fep.meter.data.MeterData;
import com.aimir.fep.meter.data.Response;
import com.aimir.fep.meter.data.Response.Type;
import com.aimir.fep.meter.parser.DLMSGtype;
import com.aimir.fep.meter.parser.DLMSKaifa;
import com.aimir.fep.meter.parser.DLMSLSSmartMeterForEVN;
import com.aimir.fep.meter.parser.DLMSNamjun;
import com.aimir.fep.meter.parser.I210PlusCSeries;
import com.aimir.fep.meter.parser.MeterDataParser;
import com.aimir.fep.meter.parser.SM110;
import com.aimir.fep.meter.parser.SX2;
import com.aimir.fep.meter.parser.DLMSKaifaTable.DLMSVARIABLE.DLMS_CLASS;
import com.aimir.fep.meter.parser.DLMSKaifaTable.DLMSVARIABLE.DLMS_CLASS_ATTR;
import com.aimir.fep.meter.parser.DLMSKaifaTable.DLMSVARIABLE.OBIS;
import com.aimir.fep.meter.parser.plc.PLCDataFrame;
import com.aimir.fep.modem.AmrData;
import com.aimir.fep.modem.BatteryLog;
import com.aimir.fep.modem.EventLog;
import com.aimir.fep.modem.ModemNetwork;
import com.aimir.fep.modem.ModemNode;
import com.aimir.fep.modem.ModemROM;
import com.aimir.fep.protocol.coap.client.SyncCoapCommand;
import com.aimir.fep.protocol.coap.frame.GeneralFrame;
import com.aimir.fep.protocol.coap.frame.GeneralFrameForETHERNET;
import com.aimir.fep.protocol.coap.frame.GeneralFrameForMBB;
import com.aimir.fep.protocol.fmp.client.gprs.TCPTriggerClient;
import com.aimir.fep.protocol.fmp.client.sms.SMSClient;
import com.aimir.fep.protocol.fmp.command.CommandProxy;
import com.aimir.fep.protocol.fmp.common.Target;
import com.aimir.fep.protocol.fmp.datatype.BOOL;
import com.aimir.fep.protocol.fmp.datatype.BYTE;
import com.aimir.fep.protocol.fmp.datatype.FMPVariable;
import com.aimir.fep.protocol.fmp.datatype.HEX;
import com.aimir.fep.protocol.fmp.datatype.INT;
import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.protocol.fmp.datatype.OID;
import com.aimir.fep.protocol.fmp.datatype.OPAQUE;
import com.aimir.fep.protocol.fmp.datatype.SMIValue;
import com.aimir.fep.protocol.fmp.datatype.STREAM;
import com.aimir.fep.protocol.fmp.datatype.STRING;
import com.aimir.fep.protocol.fmp.datatype.TIMESTAMP;
import com.aimir.fep.protocol.fmp.datatype.UINT;
import com.aimir.fep.protocol.fmp.datatype.WORD;
import com.aimir.fep.protocol.fmp.exception.FMPException;
import com.aimir.fep.protocol.fmp.exception.FMPMcuException;
import com.aimir.fep.protocol.fmp.frame.ErrorCode;
import com.aimir.fep.protocol.fmp.frame.service.Entry;
import com.aimir.fep.protocol.fmp.frame.service.MDData;
import com.aimir.fep.protocol.fmp.frame.service.entry.cmdHistEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.codiBindingEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.codiDeviceEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.codiEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.codiMemoryEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.codiNeighborEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.commLogEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.drAssetEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.drLevelEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.endDeviceEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.ffdEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.gpioEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.idrEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.loadControlSchemeEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.loadLimitPropertyEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.loadLimitSchemeEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.loadShedSchemeEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.mcuEventEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.memEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.meterDataEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.meterDataEntry2;
import com.aimir.fep.protocol.fmp.frame.service.entry.meterDataEntryNew;
import com.aimir.fep.protocol.fmp.frame.service.entry.meterEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.meterLPEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.meterLogEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.mobileEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.mobileLogEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.modemG3Entry;
import com.aimir.fep.protocol.fmp.frame.service.entry.modemSPNMSEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.pluginEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.procEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.sensorBatteryEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.sensorInfoNewEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.sysEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.timeEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.trInfoEntry;
import com.aimir.fep.protocol.fmp.log.MDLogger;
import com.aimir.fep.protocol.fmp.processor.ProcessorHandler;
import com.aimir.fep.protocol.fmp.server.FMPProtocolHandler;
import com.aimir.fep.protocol.mrp.client.MRPClientProtocolProvider;
import com.aimir.fep.protocol.mrp.client.lan.LANMMIUClient;
import com.aimir.fep.protocol.mrp.command.frame.sms.RequestFrame;
import com.aimir.fep.protocol.nip.CommandNIProxy;
import com.aimir.fep.protocol.nip.client.batch.CallableBatchExcutor;
import com.aimir.fep.protocol.nip.client.batch.CallableBatchExcutor.CBE_RESULT_CONSTANTS;
import com.aimir.fep.protocol.nip.client.batch.CallableBatchExcutor.CBE_STATUS_CONSTANTS;
import com.aimir.fep.protocol.nip.client.batch.IBatchCallable;
import com.aimir.fep.protocol.nip.client.batch.job.SORIADcuOTACallable;
import com.aimir.fep.protocol.nip.client.batch.job.SORIAMeterOTACallable;
import com.aimir.fep.protocol.nip.client.batch.job.SORIAModemOTACallable;
import com.aimir.fep.protocol.nip.client.bypass.BypassClient;
import com.aimir.fep.protocol.nip.client.bypass.BypassResult;
import com.aimir.fep.protocol.nip.command.AlarmEventCmd;
import com.aimir.fep.protocol.nip.command.AlarmEventCommandOnOff;
import com.aimir.fep.protocol.nip.command.Apn;
import com.aimir.fep.protocol.nip.command.CloneOnOff;
import com.aimir.fep.protocol.nip.command.MeterBaud;
import com.aimir.fep.protocol.nip.command.MeteringInterval;
import com.aimir.fep.protocol.nip.command.ModemEventLog;
import com.aimir.fep.protocol.nip.command.ModemIpInformation;
import com.aimir.fep.protocol.nip.command.ModemMode;
import com.aimir.fep.protocol.nip.command.ModemPortInformation;
import com.aimir.fep.protocol.nip.command.ModemResetTime;
import com.aimir.fep.protocol.nip.command.RawRomAccess;
import com.aimir.fep.protocol.nip.command.RealTimeMetering;
import com.aimir.fep.protocol.nip.command.RetryCount;
import com.aimir.fep.protocol.nip.command.RomRead;
import com.aimir.fep.protocol.nip.command.RomReadDataReq;
import com.aimir.fep.protocol.nip.command.SnmpTrapOnOff;
import com.aimir.fep.protocol.nip.command.TransmitFrequency;
import com.aimir.fep.protocol.nip.command.WatchdogTest;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.NIAttributeId;
import com.aimir.fep.protocol.security.OacServerApi;
import com.aimir.fep.protocol.smsp.client.sms.SMS_Client;
import com.aimir.fep.util.ByteArray;
import com.aimir.fep.util.CmdUtil;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.EventUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.FileInfo;
import com.aimir.fep.util.GroupInfo;
import com.aimir.fep.util.GroupTypeInfo;
import com.aimir.fep.util.Hex;
import com.aimir.fep.util.MIBUtil;
import com.aimir.fep.util.MemberInfo;
import com.aimir.fep.util.Message;
import com.aimir.fep.util.sms.SMSServiceClient;
import com.aimir.model.device.EventAlert;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.Firmware;
import com.aimir.model.device.MCU;
import com.aimir.model.device.MCUCodi;
import com.aimir.model.device.MCUCodiBinding;
import com.aimir.model.device.MCUCodiDevice;
import com.aimir.model.device.MCUCodiMemory;
import com.aimir.model.device.MCUCodiNeighbor;
import com.aimir.model.device.MMIU;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.device.NetworkInfoLog;
import com.aimir.model.device.SubGiga;
import com.aimir.model.device.ZBRepeater;
import com.aimir.model.device.ZEUPLS;
import com.aimir.model.device.ZMU;
import com.aimir.model.device.ZRU;
import com.aimir.model.system.Code;
import com.aimir.model.system.DeviceConfig;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.MeterConfig;
import com.aimir.model.system.MeterProgram;
import com.aimir.model.system.MeterProgramLog;
import com.aimir.model.system.ModemConfig;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.IPUtil;
import com.aimir.util.TimeUtil;
import com.google.gson.Gson;

import net.sf.json.JSONObject;

/**
 * This Class is Command Gate-way class
 *
 * @author Y.S Kim
 * @version $revision: 1.1.1.1 $
 **/
@Service
@Scope("prototype")
public class CommandGW implements CommandGWMBean {
	private ObjectName objectName = null;
	private static Log log = LogFactory.getLog(CommandGW.class);
	private static String TARGET_SRC = "com.aimir.fep.protocol.fmp.common.Target";
	private String regexIPv4 = "^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$";
	private String regexIPv6 = "^(([0-9a-fA-F]{1,4}:){7,7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}|::(ffff(:0{1,4}){0,1}:){0,1}((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]).){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]).){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]))$";
	private String regexIPv4andIPv6 = "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)|(([0-9a-fA-F]{1,4}:){7,7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}|::(ffff(:0{1,4}){0,1}:){0,1}((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]).){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]).){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]))$";
	
	@Resource(name="transactionManager")
	private JpaTransactionManager txmanager;
	
	@Autowired
	private MCUDao mcuDao;

	@Autowired
	private MeterDao meterDao;

	@Autowired
	private ModemDao modemDao;

	@Autowired
	private MCUCodiDao mcucodiDao;

	@Autowired
	private MeterDataSaverMain mdsMain;
	
	@Autowired
	private FirmwareDao firmwareDao;
	
	@Autowired
	private CodeDao codeDao;
	
	// @Autowired
	// private OmniMDSaver omniMDSaver;

    @Autowired
    private ProcessorHandler handler;
    
    private CommandProxy commandProxy = null;

	public CommandGW() {
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
	public void stop() {
		log.debug(this.objectName + " stop");
	}

	/**
	 * method of interface MBeanRegistration
	 *
	 * @param server
	 *            <code>MBeanServer</code> MBeanServer
	 * @param name
	 *            <code>ObjectName</code> MBean Object Name
	 * @throws java.lang.Exception
	 */
	public ObjectName preRegister(MBeanServer server, ObjectName name) throws java.lang.Exception {
		if (name == null) {
			name = new ObjectName(server.getDefaultDomain() + ":service=" + this.getClass().getName());
		}

		this.objectName = name;

		return name;
	}

	/**
	 * method of interface MBeanRegistration
	 *
	 * @param registrationDone
	 *            <code>Boolean</code>
	 * @throws java.lang.Exception
	 */
	public void postRegister(Boolean registrationDone) {
	}

	/**
	 * method of interface MBeanRegistration
	 *
	 * @throws java.lang.Exception
	 */
	public void preDeregister() throws java.lang.Exception {
	}

	/**
	 * method of interface MBeanRegistration
	 *
	 * @throws java.lang.Exception
	 */
	public void postDeregister() {
	}

	/**
	 * start CommandProxy
	 *
	 * @throws java.lang.Exception
	 */
	public void start() throws Exception {
		log.debug(this.objectName + " start");
	}

	public enum OnDemandOption {

	    READ_DEFAULT_METERING (0),   //default Metering - iraq support
        READ_CUMMULATIVE_CONSUMPTION(1), //read current(cummulative consumtion)
        READ_OPTION_RELAY     (3),   // Relay switch table read - iraq support
        WRITE_OPTION_RELAYON  (4),   // Relay switch on - iraq support
        WRITE_OPTION_RELAYOFF (5),   // Relay switch off - iraq support
        WRITE_OPTION_ACTON    (6),   // Relay activation on
        WRITE_OPTION_ACTOFF   (7),   // Relay activation off
        WRITE_OPTION_TIMESYNC (8),   // Timesync (Er000002 Non Clear) - iraq support
        READ_EVENT_LOG        (10),
        READ_IDENT_ONLY       (11);   // Meter Identification only - iraq support

        private int code;

        OnDemandOption(int code) {
            this.code = code;
        }

        public int getCode() {
            return this.code;
        }

	}

	private String makeMessage(String message) {

		if (message != null && !message.equals("")) {
			int beginIndex = message.indexOf(":");
			if (beginIndex >= 0 && (message.length() - (beginIndex + 1) > 0)) {
				return message.substring(beginIndex + 1).trim();
			} else {
				return message;
			}
		} else {
			return "Internal Server Error";
		}
	}

	/**
	 * make FMPMcuException
	 */
	private FMPMcuException makeMcuException(int code) throws Exception {
		String msg = ErrorCode.getCodeString(code);
		log.debug("Error Code String =" + msg);
		return new FMPMcuException(msg, code);
	}

	private String getStateStr(byte sval) {
		String state = "Normal";
		if (sval == 1) {
			state = "Abnormal";
		}

		return state;
	}

	public void close() {
		if (commandProxy != null) {
			this.commandProxy.closeClient();
		}
	}

	@SuppressWarnings("unchecked")
	private Object invoke(Object[] params, String[] types) throws Exception {
		log.info(String.format("invoke params[%s], types[%s]", params.toString(), types));
		commandProxy = new CommandProxy();

		log.debug("params length: " + params.length);
		Object ret = null;
		String errorMsg = null;
		try {
    		if (params.length == 3) {
    			if (params[2] != null) {
    				ret = commandProxy.execute((Target) params[0], (String) params[1], (Vector) params[2]);
    			} else {
    				ret = commandProxy.execute((Target) params[0], (String) params[1], null);
    			}
    		} else if (params.length == 3 && params[2] instanceof GeneralFrame) {
    			log.debug("NI Command Execute...");
    
    			CommandNIProxy commandNIProxy = new CommandNIProxy();
    
    			// Target , Command, Parameter
    			ret = commandNIProxy.execute((Target) params[0], NIAttributeId.getItem(String.valueOf(params[1])),
    					(HashMap<String, Object>) params[2], null);
    		} else if (params.length == 2 && params[1] instanceof PLCDataFrame) {
    			log.debug("PLC Command Execute...");
    			ret = commandProxy.execute((Target) params[0], (PLCDataFrame) params[1]);
    		}
    
    		else {
    			if (params[2] != null && params[3] != null) {
    				ret = commandProxy.execute((Target) params[0], (String) params[1], (Vector) params[2],
    						(Vector) params[3]);
    			} else if (params[2] != null && params[3] == null) {
    				ret = commandProxy.execute((Target) params[0], (String) params[1], (Vector) params[2], null);
    			} else {
    				ret = commandProxy.execute((Target) params[0], (String) params[1], null, null);
    			}
    		}
		}
		catch (Exception e) {
		    errorMsg = e.getMessage();
		}

		JpaTransactionManager txmanager = (JpaTransactionManager) DataUtil.getBean("transactionManager");
		TransactionStatus txstatus = null;
		try {
			txstatus = txmanager.getTransaction(null);
			
			Code mcuStatusNormal = codeDao.findByCondition("code", "1.1.4.1");
	        Code mcuStatusFail = codeDao.findByCondition("code", "1.1.4.5");
	        Code modemStatusNormal = codeDao.findByCondition("code", "1.2.7.3");
	        Code modemStatusFail = codeDao.findByCondition("code", "1.2.7.6");
	        
			if (ret != null) {
				Target target = (Target) params[0];
				McuType targetType = target.getTargetType();
				if (targetType == McuType.DCU || targetType == McuType.Indoor || targetType == McuType.Outdoor) {
					MCU mcu = mcuDao.get(target.getTargetId());
					if (mcu != null) {
						mcu.setLastCommDate(DateTimeUtil.getDateString(new Date()));
						mcu.setMcuStatus(mcuStatusNormal);
					}
				} else {
					Modem modem = modemDao.get(target.getTargetId());
					if (modem != null) {
						modem.setLastLinkTime(DateTimeUtil.getDateString(new Date()));
						modem.setModemStatus(modemStatusNormal);
					}
				}
			}
			else {
			    if (errorMsg != null && errorMsg.contains("Can't connect")) {
			        Target target = (Target) params[0];
	                McuType targetType = target.getTargetType();
	                if (targetType == McuType.DCU || targetType == McuType.Indoor || targetType == McuType.Outdoor) {
	                    MCU mcu = mcuDao.get(target.getTargetId());
	                    if (mcu != null) {
	                        mcu.setMcuStatus(mcuStatusFail);
	                    }
	                } else {
	                    Modem modem = modemDao.get(target.getTargetId());
	                    if (modem != null) {
	                        modem.setModemStatus(modemStatusFail);
	                    }
	                }   
			    }
			}
			txmanager.commit(txstatus);
		} catch (Exception e) {
			if (txstatus != null)
				txmanager.rollback(txstatus);
		}

		if (errorMsg != null && !"".equals(errorMsg)) throw new Exception(errorMsg);
		return ret;
	}

	private Object invokeBypass(Target target, Target newtarget, HashMap<String, String> params) 
    throws Exception
    {
        Object ret = null;
        
        commandProxy = new CommandProxy();
        ret = commandProxy.executeByPass(target, newtarget, params);
        
        return ret;
    }
	
	private Object invokeGetFile(Target target, String filename) throws Exception {
		commandProxy = new CommandProxy();
		return commandProxy.executeGetFile(target, filename);
	}

	private Object invokePutFile(Target target, String filename) throws Exception {
		commandProxy = new CommandProxy();
		return commandProxy.executePutFile(target, filename);
	}

	private Object invokeFirmwareUpdate(Target target, String filename, Integer installType, String rsvTime)
			throws Exception {
		commandProxy = new CommandProxy();
		return commandProxy.executeFirmwareUpdate(target, filename, installType, rsvTime);
	}

	private Object invokeNotifyFirmwareUpdate(Target target, String serverIp) throws Exception {
		commandProxy = new CommandProxy();
		return commandProxy.notifyFirmwareUpdate(target, serverIp);
	}

	/**
	 * MCU Diagnosis
	 *
	 * @param mcuId
	 *            MCU ID
	 * @return status (MCU status, GSM status, Sink 1 status, Sink 2 status)
	 * @throws FMPMcuException,
	 *             Exception
	 *
	 * @ejb.interface-method view-type="both"
	 */
	@Override
	public Hashtable cmdMcuDiagnosis(String mcuId) throws FMPMcuException, Exception {
		log.debug("cmdMcuDiagnosis(" + mcuId + ")");

		Target target = CmdUtil.getTarget(mcuId);

		SMIValue smiValue = null;
		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		try {
			Object[] params = new Object[] { target, "cmdMcuDiagnosis", null };

			String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector" };

			Object obj = null;
			try {
				obj = invoke(params, types);
			} catch (Exception e) {
				log.error(e, e);
				throw new Exception(makeMessage(e.getMessage()));
			}

			SMIValue[] smiValues = null;

			if (obj instanceof Integer) {
				log.error("Error Code Return");
				throw makeMcuException(((Integer) obj).intValue());
			} else if (obj instanceof SMIValue[]) {
				smiValues = (SMIValue[]) obj;
			} else {
				log.error("Unknown Return Value");
				throw new Exception("Unknown Return Value");
			}

			Hashtable<String, String> table = new Hashtable<String, String>();
			if (smiValues != null && smiValues.length != 0) {
				byte[] bx = ((OCTET) smiValues[0].getVariable()).getValue();
				for (int i = 0; i < bx.length; i++) {
					if (i == 0) {
						table.put("mcuState", getStateStr(bx[i]));
					} else if (i == 1) {
						table.put("sinkState", getStateStr(bx[i]));
					} else if (i == 3) {
						table.put("powerState", getStateStr(bx[i]));
					} else if (i == 4) {
						table.put("batteryState", getStateStr(bx[i]));
					} else if (i == 5) {
						table.put("temperatureState", getStateStr(bx[i]));
					} else if (i == 6) {
						table.put("memoryState", getStateStr(bx[i]));
					} else if (i == 7) {
						table.put("flashState", getStateStr(bx[i]));
					} else if (i == 8) {
						if (bx[i] == 0) {
							table.put("gsmState", "Normal");
						} else if (bx[i] == 1) {
							table.put("gsmState", "Abnormal(NO MODEM)");
						} else if (bx[i] == 2) {
							table.put("gsmState", "Abnormal(NO SIM CARD)");
						} else if (bx[i] == 3) {
							table.put("gsmState", "Abnormal(NOT READY)");
						} else if (bx[i] == 4) {
							table.put("gsmState", "Abnormal(BAD CSQ)");
						}
					} else if (i == 9) {
						table.put("ethernetState", getStateStr(bx[i]));
					}
				}
				/*
				 * for (int i = 0; i < smiValues.length; i++) { res[i] =
				 * CmdUtil.convSMItoMOP(smiValues[i]);
				 * log.debug(smiValues[i].toString()); }
				 */
			} else {
				log.debug("smiValues is null");
			}

			/*txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");

			txStatus = txManager.getTransaction(null);

			MCU mcu = mcuDao.get(mcuId);

			mcu.setNetworkStatus(1);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			GregorianCalendar cal = new GregorianCalendar(
					TimeZone.getTimeZone(FMPProperty.getProperty("mcu.gmt.timezone")));
			log.debug("GMT0[" + sdf.format(cal.getTime()) + "]");

			mcu.setLastCommDate(sdf.format(cal.getTime()));
			mcuDao.update(mcu);
			txManager.commit(txStatus);*/
			return table;
		} catch (Exception e) {
			if (txManager != null)
				txManager.rollback(txStatus);
			log.error(e, e);
			throw new Exception(e);
		}

	}

	/**
	 * MCU Reset
	 *
	 * @param mcuId
	 *            MCU ID
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public void cmdMcuReset(String mcuId) throws FMPMcuException, Exception {
		log.debug("cmdMcuReset(" + mcuId + ")");

		Target target = CmdUtil.getTarget(mcuId);
		int resetType = 0;// normal
		String command = "cmdMcuReset"; // INSERT SP-112

		Vector<SMIValue> datas = null;
		if (target.getNameSpace() != null && target.getNameSpace().equals("NP")) {
			datas = new Vector<SMIValue>();
			SMIValue smiValue = DataUtil.getSMIValue(new BYTE(resetType));
			datas.add(smiValue);
		}
		// INSERT START SP-112
		if (target.getNameSpace() != null && target.getNameSpace().equals("SP")) {
			datas = new Vector<SMIValue>();
			SMIValue smiValue = DataUtil.getSMIValue(target.getNameSpace(), new BYTE(resetType));
			datas.add(smiValue);
			command = "cmdReset";
		}
		// INSERT END SP-112

		Object[] params = new Object[] { target, command, // UPDATE SP-112
				datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}
	}

	/**
	 * MCU Shutdown
	 *
	 * @param mcuId
	 *            MCU ID
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public void cmdMcuShutdown(String mcuId) throws FMPMcuException, Exception {
		log.debug("cmdMcuShutdown(" + mcuId + ")");

		Target target = CmdUtil.getTarget(mcuId);

		Object[] params = new Object[] { target, "cmdMcuShutdown", null };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}
	}

	/**
	 * MCU FactoryDefault
	 *
	 * @param mcuId
	 *            MCU ID
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public void cmdMcuFactoryDefault(String mcuId) throws FMPMcuException, Exception {
		log.debug("cmdMcuFactoryDefault(" + mcuId + ")");

		Target target = CmdUtil.getTarget(mcuId);

		Object[] params = new Object[] { target, "cmdMcuFactoryDefault", null };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}
	}

	/**
	 * MCU Get Time
	 *
	 * @param mcuId
	 *            MCU ID
	 * @return time MOPROPERTY (yyyymmddhhmmss)
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public String cmdMcuGetTime(String mcuId) throws FMPMcuException, Exception {
		log.debug("cmdMcuGetTime(" + mcuId + ")");

		Target target = CmdUtil.getTarget(mcuId);
		Vector mibPropNames = new Vector();

		Object[] params = new Object[] { target, "cmdMcuGetTime", mibPropNames, };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		String time = null;
		if (smiValues != null && smiValues.length != 0) {
			FMPVariable fmpv = smiValues[0].getVariable();
			if (fmpv != null) {
				time = fmpv.toString();
			}
			log.debug(smiValues[0].toString());
		} else {
			log.debug("smiValues is null");
		}

		return time;
	}

	/**
	 * MCU Set Time
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param time
	 *            MCU Time (yyyymmddhhmmss)
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public void cmdMcuSetTime(String mcuId, String time) throws FMPMcuException, Exception {
		log.debug("cmdMcuSetTime(" + mcuId + ")");

		Target target = CmdUtil.getTarget(mcuId);
		Vector<SMIValue> datas = new Vector<SMIValue>();
		SMIValue smiValue = DataUtil.getSMIValue(new TIMESTAMP(time));
		datas.add(smiValue);

		Object[] params = new Object[] { target, "cmdMcuSetTime", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}
	}

	/**
	 * MCU Set GMT
	 *
	 * @param mcuId
	 *            MCU ID
	 * @return GMT
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public long cmdMcuSetGMT(String mcuId) throws FMPMcuException, Exception {
		log.debug("cmdMcuSetGMT(" + mcuId + ")");

		Target target = CmdUtil.getTarget(mcuId);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		// GregorianCalendar cal = new GregorianCalendar(
		// 		TimeZone.getTimeZone(FMPProperty.getProperty("mcu.gmt.timezone")));
		GregorianCalendar cal = new GregorianCalendar(TimeZone.getTimeZone(target.getTimeZoneId()));
		log.debug("GMT0[" + sdf.format(cal.getTime()) + "]");
		Vector datas = new Vector();
		SMIValue smiValue = DataUtil.getSMIValue(new TIMESTAMP(sdf.format(cal.getTime())));
		;
		datas.add(smiValue);

		Object[] params = new Object[] { target, "cmdMcuSetGMT", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception("Communication error between Server and MCU");
		}

		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		long result = 0L;
		if (smiValues.length > 0) {
			result = ((UINT) smiValues[0].getVariable()).getValue();
		}
		return result;

	}

	/**
	 * MCU Set DST
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param fileName
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public void cmdMcuSetDST(String mcuId, String fileName) throws FMPMcuException, Exception {
		log.debug("cmdMcuSetDST(" + mcuId + ")");

		Target target = CmdUtil.getTarget(mcuId);
		Vector<SMIValue> datas = new Vector<SMIValue>();
		SMIValue smiValue = null;
		smiValue = DataUtil.getSMIValueByObject("stringEntry", fileName);
		datas.add(smiValue);

		Object[] params = new Object[] { target, "cmdMcuSetDST", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}
	}

	/**
	 * MCU Get State
	 *
	 * @param mcuId
	 *            MCU ID
	 * @return mcu status
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public String cmdMcuGetState(String mcuId) throws FMPMcuException, Exception {
		log.debug("cmdMcuGetState(" + mcuId + ")");

		Target target = CmdUtil.getTarget(mcuId);
		Vector mibPropNames = new Vector();

		Object[] params = new Object[] { target, "cmdMcuGetState", mibPropNames };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		String status = null;
		if (smiValues != null && smiValues.length != 0) {
			FMPVariable fmpv = smiValues[0].getVariable();
			if (fmpv != null) {
				status = fmpv.toString();
			}
			log.debug(smiValues[0].toString());
		} else {
			log.debug("smiValues is null");
		}

		return status;
	}

	/**
	 * MCU Loopback
	 *
	 * @param mcuId
	 *            MCU ID
	 * @return result (true, false)
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public boolean cmdMcuLoopback(String mcuId) throws FMPMcuException, Exception {
		log.debug("cmdMcuLoopback(" + mcuId + ")");

		Target target = CmdUtil.getTarget(mcuId);

		Object[] params = new Object[] { target, "cmdMcuLoopback", null, null };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", "java.util.Vector" };

		try {
			Object obj = invoke(params, types);

			if (obj instanceof Integer) {
				log.error("Error Code Return");
				return false;
			} else if (obj instanceof SMIValue[]) {
				return true;
			} else {
				log.error("Unknown Return Value");
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * MCU Clear Static
	 *
	 * @param mcuId
	 *            MCU ID
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public void cmdMcuClearStatic(String mcuId) throws FMPMcuException, Exception {
		log.debug("cmdMcuClearStatic(" + mcuId + ")");

		Target target = CmdUtil.getTarget(mcuId);

		Object[] params = new Object[] { target, "cmdMcuClearStatic", null, null };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}
	}

	/**
	 * MCU Get System Info (Not Available)
	 *
	 * @param mcuId
	 *            MCU ID
	 * @return mcu system infomation entry
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public sysEntry cmdMcuGetSystemInfo(String mcuId) throws FMPMcuException, Exception {
		log.debug("cmdMcuGetSystemInfo(" + mcuId + ")");

		Target target = CmdUtil.getTarget(mcuId);

		Object[] params = new Object[] { target, "cmdMcuGetSystemInfo", null, null };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		sysEntry se = null;
		if (smiValues.length > 0) {
			Object o = smiValues[0].getVariable();
			if (o instanceof OPAQUE) {
				se = (sysEntry) ((OPAQUE) o).getValue();
			} else {
				log.error("Unknown Return Value");
				throw new Exception("Unknown Return Value");
			}
		}
		log.debug(se);

		return se;
	}

	/**
	 * MCU Get Environment (Not Available)
	 *
	 * @param mcuId
	 *            MCU ID
	 * @return Environment entrys (varEntry, varValueEntry, varSubValueEntry)
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public Entry[] cmdMcuGetEnvironment(String mcuId) throws FMPMcuException, Exception {
		log.debug("cmdMcuGetEnvironment(" + mcuId + ")");

		Target target = CmdUtil.getTarget(mcuId);

		Object[] params = new Object[] { target, "cmdMcuGetEnvironment", null, null };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		Entry[] ve = new Entry[smiValues.length];
		for (int i = 0; i < smiValues.length; i++) {
			Object o = smiValues[i].getVariable();
			if (o instanceof OPAQUE) {
				ve[i] = (Entry) ((OPAQUE) o).getValue();
			} else {
				log.error("Unknown Return Value");
				throw new Exception("Unknown Return Value");
			}
		}
		log.debug(ve);

		return ve;
	}

	/**
	 * MCU Get GPIO (Not Available)
	 *
	 * @param mcuId
	 *            MCU ID
	 * @return GIO Entry
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public gpioEntry cmdMcuGetGpio(String mcuId) throws FMPMcuException, Exception {
		log.debug("cmdMcuGetGpio(" + mcuId + ")");

		Target target = CmdUtil.getTarget(mcuId);

		Object[] params = new Object[] { target, "cmdMcuGetGpio", null, null };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		gpioEntry ge = null;
		if (smiValues.length > 0) {
			Object o = smiValues[0].getVariable();
			if (o instanceof OPAQUE) {
				ge = (gpioEntry) ((OPAQUE) o).getValue();
			} else {
				log.error("Unknown Return Value");
				throw new Exception("Unknown Return Value");
			}
		}
		log.debug(ge);

		return ge;
	}

	/**
	 * MCU Set Gpio (Not Available)
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param portNumber
	 *            GPIO Port Number
	 * @param value
	 *            GPIO Value(0/1)
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public void cmdMcuSetGpio(String mcuId, int portNumber, int value) throws FMPMcuException, Exception {
		log.debug("cmdMcuSetGpio(" + mcuId + "," + portNumber + "," + value + ")");

		Target target = CmdUtil.getTarget(mcuId);
		Vector<SMIValue> datas = new Vector<SMIValue>();
		SMIValue smiValue = DataUtil.getSMIValue(new BYTE(portNumber));
		datas.add(smiValue);
		smiValue = DataUtil.getSMIValue(new BYTE(value));
		datas.add(smiValue);

		Object[] params = new Object[] { target, "cmdMcuSetGpio", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}
	}

	/**
	 * MCU Get Ip
	 *
	 * @param mcuId
	 *            MCU ID
	 * @return ipaddr
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	@Deprecated
	public Hashtable cmdMcuGetIp(String mcuId) throws FMPMcuException, Exception {
		log.debug("cmdMcuGetIp(" + mcuId + ")");

		Target target = CmdUtil.getTarget(mcuId);
		Object[] params = new Object[] { target, "cmdMcuGetIp", null, null };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		Hashtable res = new Hashtable();
		if (smiValues != null && smiValues.length != 0) {
			for (int i = 0; i < smiValues.length; i++) {
				res.putAll(CmdUtil.convSMItoMOP(smiValues[i]));
			}
		} else {
			log.debug("smiValues is null");
		}

		return res;
	}

	/**
	 *
	 * --added MCU Get Memory
	 *
	 * @param mcuId
	 * @return memEntry
	 * @throws FMPException,
	 *             Exception
	 */
	@Override
	@Deprecated
	public memEntry cmdMcuGetMemory(String mcuId) throws FMPMcuException, Exception {
		log.debug("cmdMcuGetMemoty(" + mcuId + ")");

		Target target = CmdUtil.getTarget(mcuId);

		Object[] params = new Object[] { target, "cmdMcuGetMemory", null, null };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		if (smiValues.length > 0) {
			SMIValue smiValue = smiValues[0];
			OPAQUE mdv = (OPAQUE) smiValue.getVariable();
			memEntry value = (memEntry) mdv.getValue();
			log.debug(value);

			return value;
		} else {
			log.debug("smiValues size is 0");
			return null;
		}

	}

	/**
	 * --add Mcu Get Process
	 * 
	 * @param mcuId
	 * @return procEntry
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	@Deprecated
	public procEntry[] cmdMcuGetProcess(String mcuId) throws FMPMcuException, Exception {
		log.debug("cmdMcuGetProcess(" + mcuId + ")");

		Target target = CmdUtil.getTarget(mcuId);

		Object[] params = new Object[] { target, "cmdMcuGetProcess", null, null };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}
		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		SMIValue smiValue = null;
		OPAQUE mdv = null;
		procEntry[] values = new procEntry[smiValues.length];
		for (int i = 0; i < smiValues.length; i++) {
			smiValue = smiValues[i];
			log.debug(smiValue.toString());
			mdv = (OPAQUE) smiValue.getVariable();
			log.debug(mdv);
			values[i] = (procEntry) mdv.getValue();
			log.debug(values[i]);
		}
		return values;

	}

	/**
	 * --add MCU Get File System
	 * 
	 * @param mcuId
	 * @return
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	@Deprecated
	public Hashtable cmdMcuGetFileSystem(String mcuId) throws FMPMcuException, Exception {
		log.debug("cmdMcuGetFileSystem(" + mcuId + ")");

		Target target = CmdUtil.getTarget(mcuId);

		Object[] params = new Object[] { target, "cmdMcuGetFileSystem", null, null };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		Hashtable res = new Hashtable();
		if (smiValues != null && smiValues.length != 0) {
			for (int i = 0; i < smiValues.length; i++) {
				res.putAll(CmdUtil.convSMItoMOP(smiValues[i]));
			}
		} else {
			log.debug("smiValues is null");
		}

		return res;

	}

	/**
	 * --add MCU Get Plugin
	 * 
	 * @param mcuId
	 * @return pluginEntry
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	@Deprecated
	public pluginEntry[] cmdMcuGetPlugin(String mcuId) throws FMPMcuException, Exception {
		log.debug("cmdMcuGetPlugin(" + mcuId + ")");

		Target target = CmdUtil.getTarget(mcuId);

		Object[] params = new Object[] { target, "cmdMcuGetPlugin", null, null };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}
		SMIValue smiValue = null;
		OPAQUE mdv = null;
		pluginEntry[] values = new pluginEntry[smiValues.length];
		for (int i = 0; i < smiValues.length; i++) {
			smiValue = smiValues[i];
			log.debug(smiValue.toString());
			mdv = (OPAQUE) smiValue.getVariable();
			log.debug(mdv);
			values[i] = (pluginEntry) mdv.getValue();
			log.debug(values[i]);
		}
		return values;
	}

	/**
	 * --add MCU Get Mobile
	 * 
	 * @param mcuId
	 * @return mobileEntry
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	@Deprecated
	public mobileEntry cmdMcuGetMobile(String mcuId) throws FMPMcuException, Exception {
		log.debug("cmdMcuGetMobile(" + mcuId + ")");

		Target target = CmdUtil.getTarget(mcuId);

		Object[] params = new Object[] { target, "cmdMcuGetMobile", null, null };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		if (smiValues.length > 0) {
			SMIValue smiValue = smiValues[0];
			OPAQUE mdv = (OPAQUE) smiValue.getVariable();
			mobileEntry value = (mobileEntry) mdv.getValue();
			log.debug(value);

			return value;
		} else {
			log.debug("smiValues size is 0");
			return null;
		}
	}

	/**
	 * MCU Reset Device
	 *
	 * @param mcuId
	 *            MCU ID
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public void cmdMcuResetDevice(String mcuId, int device) throws FMPMcuException, Exception {
		log.debug("cmdMcuResetDevice(" + mcuId + ")");

		Target target = CmdUtil.getTarget(mcuId);
		Vector<SMIValue> datas = new Vector<SMIValue>();
		SMIValue smiValue = DataUtil.getSMIValue(new BYTE(device));
		datas.add(smiValue);

		Object[] params = new Object[] { target, "cmdMcuResetDevice", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}
	}

	// cmdSetSink : will be implement
	// cmdSetSinkTime : will be implement
	// cmdGetSinkStatic : will be implement --ing
	// cmdGetSinkState : will be implement --ing

	/**
	 * Add Modem
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param sensors
	 *            Sensor MO
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public void cmdAddModem(String mcuId, Modem modem) throws FMPMcuException, Exception {
		log.debug("cmdAddModem(" + mcuId + ")");

		Target target = CmdUtil.getTarget(mcuId);
		Vector<SMIValue> datas = new Vector<SMIValue>();

		SMIValue[] smvs = null;

		log.debug("sensorInstanceName[" + modem.getDeviceSerial() + "]");
		smvs = CmdUtil.getModemSMIValue(modem);

		for (int j = 0; j < smvs.length; j++) {
			datas.add(smvs[j]);
		}

		Object[] params = new Object[] { target, "cmdAddSensor", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}
	}

	/**
	 * Add Modem to MCU
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param modems
	 *            Modem List
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public void cmdAddModems(String mcuId, Modem[] modems) throws FMPMcuException, Exception {
		log.debug("cmdAddModems(" + mcuId + ")");

		Target target = CmdUtil.getTarget(mcuId);
		Vector<SMIValue> datas = new Vector<SMIValue>();

		SMIValue[] smvs = null;

		for (Modem modem : modems) {
			log.debug("Modem Serial[" + modem.getDeviceSerial() + "]");
			smvs = CmdUtil.getModemSMIValue(modem);

			for (int j = 0; j < smvs.length; j++) {
				datas.add(smvs[j]);
			}
		}

		Object[] params = new Object[] { target, "cmdAddSensor", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}
	}

	/**
	 * MCU Delete Modem
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param propName
	 *            property name
	 * @param propValue
	 *            property value
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public void cmdDeleteModem(String mcuId, String propName, String propValue) throws FMPMcuException, Exception {
		log.debug("cmdDeleteModem(" + mcuId + ")");

		Target target = CmdUtil.getTarget(mcuId);
		Vector<String> mibPropNames = new Vector<String>();
		mibPropNames.add(CmdUtil.getMIBPropertyName(propName));
		Vector<String> mibPropValues = new Vector<String>();
		mibPropValues.add(CmdUtil.getValueString(propValue));

		Object[] params = new Object[] { target, "cmdDeleteSensor", mibPropNames, mibPropValues };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}
	}

	/**
	 * MCU Delete Modem
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param modemId
	 *            modem id
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public void cmdDeleteModem(String mcuId, String modemId) throws FMPMcuException, Exception {
		log.debug("cmdDeleteModem(" + mcuId + "," + modemId + ")");

		Modem modem = modemDao.get(modemId);
		Target target = CmdUtil.getTarget(modem);

		Vector<SMIValue> datas = new Vector<SMIValue>();
		SMIValue smiValue = DataUtil.getSMIValueByObject("sensorID", modemId);
		datas.add(smiValue);

		Object[] params = new Object[] { target, "cmdDeleteSensor", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}
	}

	/**
	 * MCU Delete Sensor
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param modemId
	 *            modem id
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public void cmdDeleteModem(String mcuId, String modemId, int channelId, int panId)
			throws FMPMcuException, Exception {
		log.debug("cmdDeleteModem(" + mcuId + "," + modemId + ")");

		Modem modem = modemDao.get(modemId);
		Target target = CmdUtil.getTarget(modem);

		Vector<SMIValue> datas = new Vector<SMIValue>();
		datas.add(DataUtil.getSMIValueByObject("sensorID", modemId));
		datas.add(DataUtil.getSMIValue(new BYTE(channelId)));
		datas.add(DataUtil.getSMIValue(new WORD(panId)));

		Object[] params = new Object[] { target, "cmdDeleteSensor", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}
	}

	/**
	 * MCU Delete Modem All
	 *
	 * @param mcuId
	 *            MCU ID
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public void cmdDeleteModemAll(String mcuId) throws FMPMcuException, Exception {
		log.debug("cmdDeleteSensorAll(" + mcuId + ")");

		Target target = CmdUtil.getTarget(mcuId);

		Object[] params = new Object[] { target, "cmdDeleteSensorAll", null, null };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}
	}

	/**
	 * MCU Delete Modem All
	 *
	 * @param mcuId
	 *            MCU ID
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public void cmdDeleteModemAll(String mcuId, int channelId, int panId) throws FMPMcuException, Exception {
		log.debug("cmdDeleteModemAll(" + mcuId + ", " + channelId + ", " + panId + ")");

		Target target = CmdUtil.getTarget(mcuId);
		Vector<SMIValue> datas = new Vector<SMIValue>();
		datas.add(DataUtil.getSMIValue(new BYTE(channelId)));
		datas.add(DataUtil.getSMIValue(new WORD(panId)));

		Object[] params = new Object[] { target, "cmdDeleteSensorAll", datas, null };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}
	}

	/**
	 * MCU Delete Member All(SAT2)
	 *
	 * @param mcuId
	 *            MCU ID
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public void cmdDeleteMemberAll(String mcuId) throws FMPMcuException, Exception {
		log.debug("cmdDeleteMemberAll(" + mcuId + ")");

		Target target = CmdUtil.getTarget(mcuId);

		Object[] params = new Object[] { target, "cmdDeleteMemberAll", null, null };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}
	}

	/**
	 * Reset Modem
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param modemId
	 *            Modem ID
	 * @param resetTime
	 *            무슨값인지 모름(조차장님한테 확인)
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public void cmdResetModem(String mcuId, String modemId, int resetTime) throws FMPMcuException, Exception {
		log.debug("cmdResetModem(" + mcuId + "," + modemId + ")");

		Modem modem = modemDao.get(modemId);
		Target target = CmdUtil.getTarget(modem);
		MIBUtil mibUtil = MIBUtil.getInstance();
		Vector datas = new Vector();
		datas.add(DataUtil.getSMIValueByObject("sensorID", modemId));
		datas.add(DataUtil.getSMIValueByObject("byteEntry", String.valueOf(resetTime)));

		Object[] params = new Object[] { target, "cmdResetSensor", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}
	}

	/**
	 * Get New Modem Info List
	 *
	 * @param mcuId
	 *            MCU ID
	 * @return new modem list
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public sensorInfoNewEntry[] cmdGetModemAllNew(String mcuId) throws FMPMcuException, Exception {
		log.debug("cmdGetModemAllNew(" + mcuId + ")");

		Target target = CmdUtil.getTarget(mcuId);

		Object[] params = new Object[] { target, "cmdGetSensorAllNew", null, null };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		SMIValue smiValue = null;
		OPAQUE mdv = null;
		sensorInfoNewEntry[] values = new sensorInfoNewEntry[smiValues.length];
		for (int i = 0; i < smiValues.length; i++) {
			smiValue = smiValues[i];
			log.debug(smiValue.toString());
			mdv = (OPAQUE) smiValue.getVariable();
			log.debug(mdv);
			values[i] = (sensorInfoNewEntry) mdv.getValue();
			log.debug(values[i]);
		}
		return values;
		/*
		 * Modem[] modems = new Modem[values.length]; for (int i = 0; i <
		 * modems.length; i++) { modems[i] = CmdUtil.makeModem(values[i],
		 * mcuId); } return modems;
		 */
	}

	/**
	 * Get New Modem Info
	 *
	 * @param mcuId
	 *            MCU ID
	 * @return new modem
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public sensorInfoNewEntry cmdGetModemInfoNew(String modemId) throws FMPMcuException, Exception {
		log.debug("cmdGetModemInfoNew(" + modemId + ")");

		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");

			txStatus = txManager.getTransaction(null);
			Modem modem = modemDao.get(modemId);

			if (modem == null || modem.getMcu() == null)
				throw new Exception("Check modem[" + modemId + "] or mcu");

			Target target = CmdUtil.getTarget(modem);
			Vector datas = new Vector();
			datas.add(DataUtil.getSMIValueByObject("sensorID", modemId));
			txManager.commit(txStatus);

			Object[] params = new Object[] { target, "cmdGetSensorInfoNew", datas };

			String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector" };

			Object obj = null;
			try {
				obj = invoke(params, types);
			} catch (Exception e) {
				log.error(e, e);
				throw new Exception(makeMessage(e.getMessage()));
			}

			SMIValue smiValue = null;

			if (obj instanceof Integer) {
				log.error("Error Code Return");
				throw makeMcuException(((Integer) obj).intValue());
			} else if (obj instanceof SMIValue[]) {
				smiValue = ((SMIValue[]) obj)[0];
			} else {
				log.error("Unknown Return Value[" + obj.getClass().getName() + "]");
				throw new Exception("Unknown Return Value");
			}

			OPAQUE mdv = (OPAQUE) smiValue.getVariable();
			log.debug(mdv);
			return (sensorInfoNewEntry) mdv.getValue();
			/*
			 * Modem[] modems = new Modem[values.length]; for (int i = 0; i <
			 * modems.length; i++) { modems[i] = CmdUtil.makeModem(values[i],
			 * mcuId); } return modems;
			 */
		} catch (Exception e) {
			if (txManager != null)
				txManager.rollback(txStatus);
			throw e;
		}
	}

	/**
	 * Get Modem Count --add
	 * 
	 * @param mcuId
	 *            MCU ID
	 * @throws FMPMcuException,
	 *             Exception
	 * @return modem count
	 */
	@Override
	public int cmdGetModemCount(String mcuId) throws FMPMcuException, Exception {

		log.debug("cmdGetModemCount(" + mcuId + ")");

		Target target = CmdUtil.getTarget(mcuId);

		Object[] params = new Object[] { target, "cmdGetSensorCount", null, null };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", "java.util.Vector" };
		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		int value = 0;
		if (smiValues.length > 0) {
			value = ((INT) smiValues[0].getVariable()).getValue();
		}

		return value;
	}

	/**
	 * Clear Communication Log
	 *
	 * @param mcuId
	 *            MCU ID
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public void cmdClearCommLog(String mcuId) throws FMPMcuException, Exception {
		log.debug("cmdClearCommLog(" + mcuId + ")");

		Target target = CmdUtil.getTarget(mcuId);
		MIBUtil mibUtil = MIBUtil.getInstance();
		Vector<SMIValue> datas = new Vector<SMIValue>();
		SMIValue sv = new SMIValue(mibUtil.getOid("commLogEntry"), null);
		datas.add(sv);

		Object[] params = new Object[] { target, "cmdClearLog", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}
	}

	/**
	 * Clear Command History Log
	 *
	 * @param mcuId
	 *            MCU ID
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public void cmdClearCmdHistLog(String mcuId) throws FMPMcuException, Exception {
		log.debug("cmdClearCmdHistLog(" + mcuId + ")");

		Target target = CmdUtil.getTarget(mcuId);
		MIBUtil mibUtil = MIBUtil.getInstance();
		Vector<SMIValue> datas = new Vector<SMIValue>();
		SMIValue sv = new SMIValue(mibUtil.getOid("cmdHistEntry"), null);
		datas.add(sv);

		Object[] params = new Object[] { target, "cmdClearLog", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}
	}

	/**
	 * Clear Mcu Event Log
	 *
	 * @param mcuId
	 *            MCU ID
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public void cmdClearEventLog(String mcuId) throws FMPMcuException, Exception {
		log.debug("cmdClearEventLog(" + mcuId + ")");

		Target target = CmdUtil.getTarget(mcuId);
		MIBUtil mibUtil = MIBUtil.getInstance();
		Vector<SMIValue> datas = new Vector<SMIValue>();
		SMIValue sv = new SMIValue(mibUtil.getOid("mcuEventEntry"), null);
		datas.add(sv);

		Object[] params = new Object[] { target, "cmdClearLog", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}
	}

	/**
	 * Clear Metering Log
	 *
	 * @param mcuId
	 *            MCU ID
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public void cmdClearMeterLog(String mcuId) throws FMPMcuException, Exception {
		log.debug("cmdClearMeterLog(" + mcuId + ")");

		Target target = CmdUtil.getTarget(mcuId);
		MIBUtil mibUtil = MIBUtil.getInstance();
		Vector<SMIValue> datas = new Vector<SMIValue>();
		SMIValue sv = new SMIValue(mibUtil.getOid("meterLogEntry"), null);
		datas.add(sv);

		Object[] params = new Object[] { target, "cmdClearLog", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}
	}

	/**
	 * Get Cmd Hist Log List
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param fromTime
	 *            From Time
	 * @param toTime
	 *            To Time
	 * @return file information list
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public FileInfo[] cmdGetCmdHistList(String mcuId, String fromTime, String toTime)
			throws FMPMcuException, Exception {
		log.debug("cmdGetCmdHistList(" + mcuId + ")");

		Target target = CmdUtil.getTarget(mcuId);
		Vector<SMIValue> datas = new Vector<SMIValue>();

		SMIValue smiValue = DataUtil.getSMIValue(new TIMESTAMP(fromTime));
		datas.add(smiValue);
		smiValue = DataUtil.getSMIValue(new TIMESTAMP(toTime));
		datas.add(smiValue);

		Object[] params = new Object[] { target, "cmdGetCmdHistList", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
			log.debug("smiValues length [" + smiValues.length + "]");
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		ArrayList<FileInfo> res = new ArrayList<FileInfo>();

		SMIValue sv = null;
		cmdHistEntry value = null;
		OPAQUE mdv = null;
		for (int i = 0; i < smiValues.length; i++) {
			sv = smiValues[i];
			log.debug(sv.toString());
			mdv = (OPAQUE) sv.getVariable();
			log.debug(mdv);
			value = (cmdHistEntry) mdv.getValue();
			log.debug(value);
			res.add(CmdUtil.getFileInfo(value));
		}
		return res.toArray(new FileInfo[0]);
	}

	/**
	 * Get Meter Log List
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param fromTime
	 *            From Time
	 * @param toTime
	 *            To Time
	 * @return file information list
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public FileInfo[] cmdGetMeterLogList(String mcuId, String fromTime, String toTime)
			throws FMPMcuException, Exception {
		log.debug("cmdGetMeterLogList(" + mcuId + ")");

		Target target = CmdUtil.getTarget(mcuId);
		Vector<SMIValue> datas = new Vector<SMIValue>();

		SMIValue smiValue = DataUtil.getSMIValue(new TIMESTAMP(fromTime));
		datas.add(smiValue);
		smiValue = DataUtil.getSMIValue(new TIMESTAMP(toTime));
		datas.add(smiValue);

		Object[] params = new Object[] { target, "cmdGetMeterLogList", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
			log.debug("smiValues length [" + smiValues.length + "]");
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		ArrayList<FileInfo> res = new ArrayList<FileInfo>();

		SMIValue sv = null;
		meterLogEntry value = null;
		OPAQUE mdv = null;
		for (int i = 0; i < smiValues.length; i++) {
			sv = smiValues[i];
			log.debug(sv.toString());
			mdv = (OPAQUE) sv.getVariable();
			log.debug(mdv);
			value = (meterLogEntry) mdv.getValue();
			log.debug(value);
			res.add(CmdUtil.getFileInfo(value));
		}
		return res.toArray(new FileInfo[0]);
	}

	/**
	 * Get Event Log List
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param fromTime
	 *            From Time
	 * @param toTime
	 *            To Time
	 * @return file information list
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public FileInfo[] cmdGetEventLogList(String mcuId, String fromTime, String toTime)
			throws FMPMcuException, Exception {
		log.debug("cmdGetEventLogList(" + mcuId + ")");

		Target target = CmdUtil.getTarget(mcuId);
		Vector<SMIValue> datas = new Vector<SMIValue>();

		SMIValue smiValue = DataUtil.getSMIValue(new TIMESTAMP(fromTime));
		datas.add(smiValue);
		smiValue = DataUtil.getSMIValue(new TIMESTAMP(toTime));
		datas.add(smiValue);

		Object[] params = new Object[] { target, "cmdGetEventLogList", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
			log.debug("smiValues length [" + smiValues.length + "]");
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		ArrayList<FileInfo> res = new ArrayList<FileInfo>();

		SMIValue sv = null;
		mcuEventEntry value = null;
		OPAQUE mdv = null;
		for (int i = 0; i < smiValues.length; i++) {
			sv = smiValues[i];
			log.debug(sv.toString());
			mdv = (OPAQUE) sv.getVariable();
			log.debug(mdv);
			value = (mcuEventEntry) mdv.getValue();
			log.debug(value);
			res.add(CmdUtil.getFileInfo(value));
		}
		return res.toArray(new FileInfo[0]);
	}

	/**
	 * Get Communication Log List
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param fromTime
	 *            From Time
	 * @param toTime
	 *            To Time
	 * @return file information list
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public FileInfo[] cmdGetCommLogList(String mcuId, String fromTime, String toTime)
			throws FMPMcuException, Exception {
		log.debug("cmdGetCommLogList(" + mcuId + ")");

		Target target = CmdUtil.getTarget(mcuId);
		Vector<SMIValue> datas = new Vector<SMIValue>();

		SMIValue smiValue = DataUtil.getSMIValue(new TIMESTAMP(fromTime));
		datas.add(smiValue);
		smiValue = DataUtil.getSMIValue(new TIMESTAMP(toTime));
		datas.add(smiValue);

		Object[] params = new Object[] { target, "cmdGetCommLogList", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
			log.debug("smiValues length [" + smiValues.length + "]");
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		ArrayList<FileInfo> res = new ArrayList<FileInfo>();

		SMIValue sv = null;
		commLogEntry value = null;
		OPAQUE mdv = null;
		for (int i = 0; i < smiValues.length; i++) {
			sv = smiValues[i];
			log.debug(sv.toString());
			mdv = (OPAQUE) sv.getVariable();
			log.debug(mdv);
			value = (commLogEntry) mdv.getValue();
			log.debug(value);
			res.add(CmdUtil.getFileInfo(value));
		}
		return res.toArray(new FileInfo[0]);
	}

	/**
	 * Get Mobile Log List
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param fromTime
	 *            From Time
	 * @param toTime
	 *            To Time
	 * @return file information list
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public FileInfo[] cmdGetMobileLogList(String mcuId, String fromTime, String toTime)
			throws FMPMcuException, Exception {
		log.debug("cmdGetMobileLogList(" + mcuId + ")");

		Target target = CmdUtil.getTarget(mcuId);
		Vector<SMIValue> datas = new Vector<SMIValue>();

		SMIValue smiValue = DataUtil.getSMIValue(new TIMESTAMP(fromTime));
		datas.add(smiValue);
		smiValue = DataUtil.getSMIValue(new TIMESTAMP(toTime));
		datas.add(smiValue);

		Object[] params = new Object[] { target, "cmdGetMobileLogList", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
			log.debug("smiValues length [" + smiValues.length + "]");
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		ArrayList<FileInfo> res = new ArrayList<FileInfo>();

		SMIValue sv = null;
		mobileLogEntry value = null;
		OPAQUE mdv = null;
		for (int i = 0; i < smiValues.length; i++) {
			sv = smiValues[i];
			log.debug(sv.toString());
			mdv = (OPAQUE) sv.getVariable();
			log.debug(mdv);
			value = (mobileLogEntry) mdv.getValue();
			log.debug(value);
			res.add(CmdUtil.getFileInfo(value));
		}
		return res.toArray(new FileInfo[0]);
	}

	/**
	 * Get All Log List
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param fromTime
	 *            From Time
	 * @param toTime
	 *            To Time
	 * @return file information list
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public FileInfo[] cmdGetAllLogList(String mcuId, String fromTime, String toTime) throws FMPMcuException, Exception {
		log.debug("cmdGetAllLogList(" + mcuId + ")");

		Target target = CmdUtil.getTarget(mcuId);
		Vector<SMIValue> datas = new Vector<SMIValue>();

		SMIValue smiValue = DataUtil.getSMIValue(new TIMESTAMP(fromTime));
		datas.add(smiValue);
		smiValue = DataUtil.getSMIValue(new TIMESTAMP(toTime));
		datas.add(smiValue);

		Object[] params = new Object[] { target, "cmdGetAllLogList", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
			log.debug("smiValues length [" + smiValues.length + "]");
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		ArrayList<FileInfo> res = new ArrayList<FileInfo>();

		SMIValue sv = null;
		Entry value = null;
		OPAQUE mdv = null;
		for (int i = 0; i < smiValues.length; i++) {
			sv = smiValues[i];
			log.debug(sv.toString());
			mdv = (OPAQUE) sv.getVariable();
			log.debug(mdv);
			value = (Entry) mdv.getValue();
			log.debug(value);
			res.add(CmdUtil.getFileInfo(value));
		}
		return res.toArray(new FileInfo[0]);
	}

	/**
	 * Metering
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param mop
	 *            Sensor ID
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public void cmdMetering(String mcuId, String propName, String propValue) throws FMPMcuException, Exception {
		log.debug("cmdMetering(" + mcuId + ")");

		Target target = CmdUtil.getTarget(mcuId);
		Vector<String> mibPropNames = new Vector<String>();
		mibPropNames.add(CmdUtil.getMIBPropertyName(propName));
		Vector<String> mibPropValues = new Vector<String>();
		mibPropValues.add(CmdUtil.getValueString(propValue));

		Object[] params = new Object[] { target, "cmdMetering", mibPropNames, mibPropValues };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}
	}

	/**
	 * Metering All
	 *
	 * @param mcuId
	 *            MCU ID
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public void cmdMeteringAll(String mcuId, int nOption, int offset, int count) throws FMPMcuException, Exception {

		log.debug("cmdMeteringAll(" + mcuId + "," + mcuId + "," + nOption + "," + offset + "," + count + ")");

		Target target = CmdUtil.getTarget(mcuId);

		Vector<SMIValue> datas = new Vector<SMIValue>();
		SMIValue smiValue = null;
		smiValue = DataUtil.getSMIValue(new INT(nOption));
		datas.add(smiValue);
		smiValue = DataUtil.getSMIValue(new INT(offset));
		datas.add(smiValue);
		smiValue = DataUtil.getSMIValue(new INT(count));
		datas.add(smiValue);

		Object[] params = new Object[] { target, "cmdMeteringAll", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}
	}

	/**
	 * Metering All
	 *
	 * @param mcuId
	 *            MCU ID
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public void cmdMeteringAll(String mcuId) throws FMPMcuException, Exception {
		log.debug("cmdMeteringAll(" + mcuId + ")");

		Target target = CmdUtil.getTarget(mcuId);

		Object[] params = new Object[] { target, "cmdMcuMeteringAll", null, null };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}
	}

	/**
	 * Metering Recovery
	 *
	 * @param mcuId
	 *            MCU ID
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public void cmdRecovery(String mcuId, int nOption, int offset, int count) throws FMPMcuException, Exception {

		log.debug("cmdRecovery(" + mcuId + "," + mcuId + "," + nOption + "," + offset + "," + count + ")");

		Target target = CmdUtil.getTarget(mcuId);

		Vector<SMIValue> datas = new Vector<SMIValue>();
		SMIValue smiValue = null;
		smiValue = DataUtil.getSMIValue(new INT(nOption));
		datas.add(smiValue);
		smiValue = DataUtil.getSMIValue(new INT(offset));
		datas.add(smiValue);
		smiValue = DataUtil.getSMIValue(new INT(count));
		datas.add(smiValue);

		Object[] params = new Object[] { target, "cmdRecovery", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}
	}

	/**
	 * Metering Recovery
	 *
	 * @param mcuId
	 *            MCU ID
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public void cmdRecovery(String mcuId) throws FMPMcuException, Exception {
		log.debug("cmdRecovery(" + mcuId + ")");

		String targetTime = TimeUtil.getCurrentTime();

		Target target = CmdUtil.getTarget(mcuId);
		Vector<SMIValue> datas = new Vector<SMIValue>();

		SMIValue smiValue = DataUtil.getSMIValue(new TIMESTAMP(targetTime));
		datas.add(smiValue);

		Object[] params = new Object[] { target, "cmdRecovery", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}
	}

	/**
	 * Metering Recovery
	 *
	 * @param mcuId
	 *            MCU ID
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public void cmdRecovery(String mcuId, String targetTime) throws FMPMcuException, Exception {
		log.debug("cmdRecovery(" + mcuId + "," + targetTime + ")");

		Target target = CmdUtil.getTarget(mcuId);
		Vector<SMIValue> datas = new Vector<SMIValue>();

		SMIValue smiValue = DataUtil.getSMIValue(new TIMESTAMP(targetTime));
		datas.add(smiValue);

		Object[] params = new Object[] { target, "cmdRecovery", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}
	}

	/**
	 * Get Meter
	 *
	 * @param mcuId
	 *            MCU Indentifier
	 * @return energy meter data list
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public meterEntry[] cmdGetMeterList(String mcuId, int nOption, String value) throws FMPMcuException, Exception {
		log.debug("cmdGetMeterList(" + mcuId + "," + nOption + "," + value + ")");

		Target target = CmdUtil.getTarget(mcuId);

		Vector<SMIValue> datas = new Vector<SMIValue>();
		SMIValue smiValue = null;
		smiValue = DataUtil.getSMIValue(new INT(nOption));
		datas.add(smiValue);
		smiValue = DataUtil.getSMIValue(new STRING(value));
		datas.add(smiValue);

		Object[] params = new Object[] { target, "cmdGetMeterList", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return : " + obj);
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			log.info(" Get SMI Value ");
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}
		return null;

	}

	/**
	 * Get Meter
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param sensorId
	 *            Sensor ID
	 * @param fromTime
	 *            From Time
	 * @param toTime
	 *            To Time
	 * @return energy meter data list
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public MeterData[] cmdGetMeter(String mcuId, String meterId, String fromTime, String toTime)
			throws FMPMcuException, Exception {
		log.debug("cmdGetMeter(" + mcuId + "," + meterId + ")");

		Target target = CmdUtil.getTarget(mcuId);
		Vector<SMIValue> datas = new Vector<SMIValue>();

		SMIValue smiValue = null;
		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");

			txStatus = txManager.getTransaction(null);

			Meter meter = meterDao.get(meterId);
			Modem modem = meter.getModem();
			target = CmdUtil.getTarget(modem);

			/*
			 * if (modem.getMcu() != null &&
			 * modem.getMcu().getSysSwRevision().compareTo(mcuRevision) >= 0 ) {
			 * smiValue = DataUtil.getSMIValue("gsiSensorID",
			 * modem.getDeviceSerial()); datas.add(smiValue); } else { smiValue
			 * = DataUtil.getSMIValue("sensorID", modem.getDeviceSerial()); }
			 */
			smiValue = DataUtil.getSMIValueByObject("sensorID", modem.getDeviceSerial());

			smiValue = DataUtil.getSMIValue(new TIMESTAMP(fromTime));
			datas.add(smiValue);
			smiValue = DataUtil.getSMIValue(new TIMESTAMP(toTime));
			datas.add(smiValue);

			Object[] params = new Object[] { target, "cmdGetMeter", datas };

			String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

			Object obj = null;
			try {
				obj = invoke(params, types);
			} catch (Exception e) {
				log.error(e, e);
				throw new Exception(makeMessage(e.getMessage()));
			}

			SMIValue[] smiValues = null;

			if (obj instanceof Integer) {
				log.error("Error Code Return : " + obj);
				throw makeMcuException(((Integer) obj).intValue());
			} else if (obj instanceof SMIValue[]) {
				log.info(" Get SMI Value ");
				smiValues = (SMIValue[]) obj;
			} else {
				log.error("Unknown Return Value");
				throw new Exception("Unknown Return Value");
			}

			// EnergyMeterData[] eds = new EnergyMeterData[smiValues.length];
			MeterData ed = null;
			ArrayList<MeterData> eds = new ArrayList<MeterData>();
			log.debug("return value count [" + smiValues.length + "]");
			OPAQUE mdv = null;
			meterDataEntry value = null;
			meterEntry value2 = null;
			MeterDataParser edp = null;
			byte[] mdb = null;
			int pulseCnt = 0;

			ModemConfig modemConfig = null;
			for (int i = 0; i < smiValues.length; i++) {
				try {
					mdv = (OPAQUE) smiValues[i].getVariable();
					// log.debug("Get Meter : return Class Name = "+className);
					if (mdv.getValue() instanceof meterDataEntry) {
						value = (meterDataEntry) mdv.getValue();
						meterId = value.getMdID().toString();
						mdb = value.getMdData().getValue();

						/**
						 * modified by p.y.k get parser changed
						 */
						modemConfig = (ModemConfig) meter.getModem().getModel().getDeviceConfig();
						edp = (MeterDataParser) Class.forName(modemConfig.getParserName()).newInstance();
						edp.setMeter(meter);
						// mdType = value.getMdType().getValue();

						log.debug(Hex.decode(mdb));

						/*
						 * modify by Hun if(mdType == 5) edp.parse(mdb, 1); else
						 * edp.parse(mdb, -1);
						 */
						edp.parse(mdb);

						ed = new MeterData();
						ed.setMeterId(meterId);
						ed.setTime(value.getMdTime().toString());
						ed.setType(value.getMdType().toString());
						ed.setVendor(value.getMdVendor().toString());
						ed.setServiceType(value.getMdServiceType().toString());
						ed.setParser(edp);
						eds.add(ed);
					} else if (mdv.getValue() instanceof meterEntry) {
						value2 = (meterEntry) mdv.getValue();
						meterId = value2.getMtrSerial().toString();
						mdb = value2.getMtrData().getValue();

						/**
						 * modified by p.y.k get parser changed
						 */
						modemConfig = (ModemConfig) meter.getModem().getModel().getDeviceConfig();
						edp = (MeterDataParser) Class.forName(modemConfig.getParserName()).newInstance();
						edp.setMeter(meter);

						log.debug(Hex.decode(mdb));

						/*
						 * modify by Hun edp.parse(mdb, -1);
						 */
						edp.parse(mdb);

						ed = new MeterData();
						ed.setMeterId(meterId);
						ed.setTime(value2.getMtrTime().toString());
						ed.setParser(edp);
						eds.add(ed);
					}
				} catch (Exception e) {
					log.error(e, e);
				}
			}

			txManager.commit(txStatus);
			return eds.toArray(new MeterData[0]);

		} catch (Exception e) {
			if (txManager != null)
				txManager.rollback(txStatus);
			throw new Exception(e);
		}

	}

	/**
	 * Get Meter
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param meterId
	 *            Meter ID
	 * @param fromTime
	 *            From Time
	 * @param toTime
	 *            To Time
	 * @return energy meter data list
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public MeterData cmdGetMeterInfo(String mcuId, String meterId) throws FMPMcuException, Exception {
		log.debug("cmdGetMeterInfo(" + mcuId + "," + meterId + ")");

		Target target = null;
		Vector<SMIValue> datas = new Vector<SMIValue>();

		SMIValue smiValue = null;
		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");

			txStatus = txManager.getTransaction(null);

			Modem modem = meterDao.get(meterId).getModem();
			target = CmdUtil.getTarget(modem);

			/*
			 * if (modem.getMcu() != null &&
			 * modem.getMcu().getSysSwRevision().compareTo(mcuRevision) >= 0 ) {
			 * smiValue = DataUtil.getSMIValue("gsiSensorID",
			 * modem.getDeviceSerial()); datas.add(smiValue); } else { smiValue
			 * = DataUtil.getSMIValue("sensorID", modem.getDeviceSerial()); }
			 */
			smiValue = DataUtil.getSMIValueByObject("sensorID", modem.getDeviceSerial());

			txManager.commit(txStatus);
		} catch (Exception e) {
			if (txManager != null)
				txManager.rollback(txStatus);
			throw e;
		}

		datas.add(smiValue);

		Object[] params = new Object[] { target, "cmdGetMeterInfo", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return : " + obj);
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		MeterData ed = new MeterData();
		log.debug("return value count [" + smiValues.length + "]");

		try {
			OCTET omdv = (OCTET) smiValues[0].getVariable();
			log.debug(omdv.toString());
			ed.setMeterId(omdv.toString());
			BYTE bmdv = (BYTE) smiValues[1].getVariable();
			log.debug(bmdv.toString());
			// ed.setGrpMemId(Integer.parseInt(bmdv.toString()));
			// analyze sencond value
			omdv = (OCTET) smiValues[2].getVariable();
			log.debug(omdv.toString());
			ed.setVendor(omdv.toString());
		} catch (Exception e) {
			log.error(e, e);
		}

		return ed;
	}

	/**
	 * Get Meter
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param modemId
	 *            Modem ID
	 * @return energy meter data list
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public MeterData cmdGetMeterInfoFromModem(String mcuId, String modemId) throws FMPMcuException, Exception {
		log.debug("cmdGetMeterInfo(" + mcuId + "," + modemId + ")");

		Target target = null;
		Vector<SMIValue> datas = new Vector<SMIValue>();

		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");

			txStatus = txManager.getTransaction(null);

			Modem modem = modemDao.get(modemId);
			target = CmdUtil.getTarget(modem);

			SMIValue smiValue = null;

			/*
			 * if (modem.getMcu() != null &&
			 * modem.getMcu().getSysSwRevision().compareTo(mcuRevision) >= 0 ) {
			 * smiValue = DataUtil.getSMIValue("gsiSensorID",
			 * modem.getDeviceSerial()); datas.add(smiValue); } else { smiValue
			 * = DataUtil.getSMIValue("sensorID", modem.getDeviceSerial()); }
			 */
			smiValue = DataUtil.getSMIValueByObject("sensorID", modem.getDeviceSerial());
			datas.add(smiValue);

			txManager.commit(txStatus);
		} catch (Exception e) {
			if (txManager != null)
				txManager.rollback(txStatus);
			throw e;
		}

		Object[] params = new Object[] { target, "cmdGetMeterInfo", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return : " + obj);
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		MeterData ed = new MeterData();
		log.debug("return value count [" + smiValues.length + "]");

		try {
			OCTET omdv = (OCTET) smiValues[0].getVariable();
			log.debug(omdv.toString());
			ed.setMeterId(omdv.toString());
			BYTE bmdv = (BYTE) smiValues[1].getVariable();
			log.debug(bmdv.toString());
			// ed.setGrpMemId(Integer.parseInt(bmdv.toString()));
			// analyze sencond value
			omdv = (OCTET) smiValues[2].getVariable();
			log.debug(omdv.toString());
			ed.setVendor(omdv.toString());
		} catch (Exception e) {
			log.error(e, e);
		}

		return ed;
	}

	/**
	 * cmdOnDemandMeter
	 *
	 * @param mcuId
	 *            MCU Indentifier
	 * @param meterId
	 * @param nOption
	 *            (3~7)
	 * @return resultData
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public Map cmdOnDemandMeter(String mcuId, String meterId, int nOption) throws FMPMcuException, Exception {
		log.debug("cmdOnDemandMeter(" + mcuId + "," + meterId + "," + nOption + ")");

		Target target = null;
		Vector<SMIValue> datas = new Vector<SMIValue>();

		SMIValue smiValue = null;
		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		String command = "cmdOnDemandMeter";

		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");
			txStatus = txManager.getTransaction(null);

			Meter meter = meterDao.get(meterId.split(",")[0]);
			Modem modem = meter.getModem();
			target = CmdUtil.getTarget(modem);

			if(target.getNameSpace() != null && (target.getNameSpace().equals("NG") || target.getNameSpace().equals("ZV"))){
                if(mcuId != null && !"".equals(mcuId)) {
                    return cmdOnDemandByMeter(mcuId, meterId, "", nOption);
                }
                else {
                    return cmdOnDemandByMeter(mcuId, meterId, modem.getDeviceSerial(), nOption);
                }
            }

			if (target.getNameSpace() != null && target.getNameSpace().equals("SP")) {
				smiValue = DataUtil.getSMIValue(target.getNameSpace(), new INT(nOption));
				datas.add(smiValue);

				smiValue = DataUtil.getSMIValue(target.getNameSpace(), new INT(0x00)); // Offset is 0
				datas.add(smiValue);

				smiValue = DataUtil.getSMIValue(target.getNameSpace(), new INT(0x00)); // Count is 0
				datas.add(smiValue);

				smiValue = DataUtil.getSMIValue(target.getNameSpace(), new INT(0x01)); // Filter is meterID
				datas.add(smiValue);

				smiValue = DataUtil.getSMIValueByObject(target.getNameSpace(), "stringEntry", meterId); // meterID
				datas.add(smiValue);

				command = "cmdOndemand";
			} else {
				smiValue = DataUtil.getSMIValueByObject("sensorID", modem.getDeviceSerial());
				datas.add(smiValue);
				smiValue = DataUtil.getSMIValue(new INT(nOption));
				datas.add(smiValue);
			}

			DeviceConfig deviceConfig = meter.getModel().getDeviceConfig();
			if (deviceConfig == null)
				deviceConfig = meter.getModem().getModel().getDeviceConfig();
			MeterDataParser edp = null;
			if (deviceConfig.getOndemandParserName() != null && !"".equals(deviceConfig.getOndemandParserName()))
				edp = (MeterDataParser) Class.forName(deviceConfig.getOndemandParserName()).newInstance();
			else
				edp = (MeterDataParser) Class.forName(deviceConfig.getParserName()).newInstance();

			edp.setMeter(meter);

			Object[] params = new Object[] { target, command, datas };

			String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

			Object obj = null;
			try {
				obj = invoke(params, types);
			} catch (Exception e) {
				log.error(e, e);
				throw new Exception(makeMessage(e.getMessage()));
			}

			SMIValue[] smiValues = null;

			if (obj instanceof Integer) {
				log.error("Error Code Return");
				throw makeMcuException(((Integer) obj).intValue());
			} else if (obj instanceof SMIValue[]) {
				smiValues = (SMIValue[]) obj;
			} else {
				log.error("Unknown Return Value");
				throw new Exception("Unknown Return Value");
			}

			Map map = null;
			if (smiValues != null && smiValues.length != 0) {
				smiValue = smiValues[0];
				log.debug("smiValues.count="+smiValues.length + ":" +smiValues[0].toString());
				
				if (target.getNameSpace() != null && target.getNameSpace().equals("SP")) {
					log.debug("SMIValue Return Class=" + smiValue.getVariable().getClass().getName());

					if ( smiValues.length==2 ){
						if ( Integer.parseInt(smiValue.getVariable().toString()) != ErrorCode.IF4ERR_NOERROR ) {
							// error
							log.debug("SMIValue Return value=" + smiValue.getVariable().toString() );
						}else{
							// 
							// get meterDataEntry
							smiValue = smiValues[1];
							if (smiValue.getVariable() instanceof OPAQUE) {

								OPAQUE mdv = (OPAQUE) smiValue.getVariable();
								log.debug("Get Meter : return ClassName[" + mdv.getClsName() + "] MIB[" + mdv.getMIBName() + "]");

								obj = mdv.getValue();
								if (obj instanceof meterDataEntry) {
									meterDataEntry value = (meterDataEntry) obj;

									// meter id
									OCTET ocMeterId = new OCTET(20);
									ocMeterId.setIsFixed(true);
									String meterSerial = meter.getMdsId() == null ? "" : meter.getMdsId();
									byte[] bMeterid = DataUtil.fillCopy(meterSerial.getBytes(), (byte) 0x20, 20);
									ocMeterId.setValue(bMeterid);
									value.setMdSerial(ocMeterId);

									edp.parse(value.getMdData().getValue());
									//ed.setTime(value.getMdTime().toString());
									//ed.setType(value.getMdType().toString());
									//ed.setVendor(value.getMdVendor().toString());
									//ed.setServiceType(value.getMdServiceType().toString());
								}

								//ed.setParser(edp);
								//MDData mdData = new MDData(new WORD(1));
								//mdData.setMcuId(mcuId);
								//mdData.setMdData(mdv.encode());
								DLMSKaifa kaifa = (DLMSKaifa) edp;
								map = kaifa.getRelayStatus();

								// handler.putServiceData(ProcessorHandler.SERVICE_MEASUREMENTDATA,
								// mdData);
							}
						}
					}
					return map;
				}
				
				if (smiValues[0].getVariable() instanceof BYTE && 
						((edp instanceof DLMSNamjun)) ) {

					BYTE byteValue = (BYTE) smiValues[0].getVariable();
					map = new LinkedHashMap<String, Object>();
					switch (nOption) {
					case 3: // Relay switch table read

						/*
						 * : 1-ON, 0-Off 
						 * LSB 
						 * 0 : L1 relay status 
						 * 1 : L2 relay status 
						 * 2 : L3 relay status 
						 * 3 : External relay status
						 * 4 : L1 relay error 
						 * 5 : L2 relay error 
						 * 6 : L3 relay error 
						 * 7 : External relay error 
						 * 8 : Open terminal cover 
						 * 9 : Open terminal cover in power off 
						 * 10: Open top cover 
						 * 11: Open top cover in power off 
						 * 12: Magnetic detection 1 
						 * 13:Magnetic detection 2 
						 * 14: Program 
						 * 15: Factory status 
						 * MSB
						 */

						if ((byteValue.getValue() & 0x01) == 1 || (byteValue.getValue() & 0x02) == 1
								|| (byteValue.getValue() & 0x04) == 1) {
							map.put("LoadControlStatus", LOAD_CONTROL_STATUS.CLOSE);
						}
						if ((byteValue.getValue() & 0x01) == 0 && (byteValue.getValue() & 0x02) == 1
								&& (byteValue.getValue() & 0x04) == 0) {
							map.put("LoadControlStatus", LOAD_CONTROL_STATUS.OPEN);
						}
						if (!map.containsKey("LoadControlStatus") && byteValue.getValue() == 0) {
							map.put("LoadControlStatus", LOAD_CONTROL_STATUS.OPEN);
						}
						break;
					case 4: // Relay switch on
						if (byteValue.getValue() == 0) {
							map.put("LoadControlStatus", LOAD_CONTROL_STATUS.CLOSE);
							map.put("Result", ResultStatus.SUCCESS);
						} else {
							map.put("LoadControlStatus", LOAD_CONTROL_STATUS.OPEN);
							map.put("Result", ResultStatus.FAIL);
						}
						break;
					case 5: // Relay switch off
						if (byteValue.getValue() == 0) {
							map.put("LoadControlStatus", LOAD_CONTROL_STATUS.OPEN);
							map.put("Result", ResultStatus.SUCCESS);
						} else {
							map.put("LoadControlStatus", LOAD_CONTROL_STATUS.CLOSE);
							map.put("Result", ResultStatus.FAIL);
						}
						break;
					case 8: // Timesync (Er000002 Non Clear)
						if (byteValue.getValue() == 0) {
							map.put("afterTime", DateTimeUtil.getDateString(new Date()));
							map.put("Result", ResultStatus.SUCCESS);
						} else {
							map = null;
						}
						break;
					}
					return map;
				}

				if (smiValues[0].getVariable() instanceof OPAQUE) {
					OPAQUE mdv = (OPAQUE) smiValues[0].getVariable();
					obj = mdv.getValue();
					if (obj instanceof meterLPEntry) {
						meterLPEntry value = (meterLPEntry) obj;
						ByteArrayOutputStream bao = new ByteArrayOutputStream();
						byte[] bx = value.getMlpData().getValue();
						byte[] timestamp = new byte[7];
						log.info("bx: " + Hex.decode(bx));

						int dataCnt = value.getMlpDataCount().getValue();
						if (dataCnt == 1) {
							System.arraycopy(bx, 0, timestamp, 0, timestamp.length);
							bao.write(bx, timestamp.length, bx.length - timestamp.length);
						} else {
							int pos = 0;
							byte[] blen = new byte[2];
							int ilen = 0;

							for (int i = 0; i < dataCnt; i++) {
								// 데이타의 길이를 가져온다.
								System.arraycopy(bx, pos, blen, 0, blen.length);
								DataUtil.convertEndian(blen);
								ilen = DataUtil.getIntTo2Byte(blen);
								pos += blen.length;
								// 시간을 가져온다.
								System.arraycopy(bx, pos, timestamp, 0, timestamp.length);
								pos += timestamp.length;
								// 데이타 부분만 바이트 배열로 모은다.
								bao.write(bx, pos, ilen - timestamp.length);
								pos += ilen - timestamp.length;
							}
						}
						log.info("bao: " + Hex.decode(bao.toByteArray()));
						edp.setOnDemand(true);
						edp.parse(bao.toByteArray());
						bao.flush();
						bao.close();

						if (edp instanceof SM110) {
							SM110 sm110 = (SM110) edp;
							map = sm110.getRelayStatus();
						} else if (edp instanceof I210PlusCSeries) {
							I210PlusCSeries i210 = (I210PlusCSeries) edp;
							map = i210.getRelayStatus();
						} else if (edp instanceof SX2) {
							SX2 sx2 = (SX2) edp;
							map = sx2.getRelayStatus();
						} else if (edp instanceof DLMSGtype) {
							DLMSGtype gtype = (DLMSGtype) edp;
							if (nOption == OnDemandOption.READ_OPTION_RELAY.getCode()) {
								map = gtype.getRelayStatus();
							} else if (nOption == OnDemandOption.WRITE_OPTION_TIMESYNC.getCode()) {
								map = gtype.getMeterSyncTime();
							} else {
								map = gtype.getData();
							}
						}

						if (map != null)
							map.put("Parser", deviceConfig.getParserName());
					}
				} else if (smiValues[0].getVariable() instanceof OCTET) { // IRAQ
																			// MOE
																			// GPRS
																			// Modem의
																			// 경우
																			// 해당...
					map = new LinkedHashMap<String, Object>();
					switch (nOption) {
					case 3: // Relay Status
						if (smiValues[0].getVariable().toString().equals(Bypass.OPEN)) {
							map.put("SendSMSStatus", ResultStatus.SUCCESS.name());
						} else {
							map.put("SendSMSStatus", "failReason");
						}
						break;
					case 4: // Relay On
						if (smiValues[0].getVariable().toString().equals(Bypass.OPEN)) {
							map.put("SendSMSStatus", ResultStatus.SUCCESS.name());
						} else {
							map.put("SendSMSStatus", "failReason");
						}
						break;
					case 5: // Relay Off
						if (smiValues[0].getVariable().toString().equals(Bypass.OPEN)) {
							map.put("SendSMSStatus", ResultStatus.SUCCESS.name());
						} else {
							map.put("SendSMSStatus", "failReason");
						}
					case 8:
						if (smiValues[0].getVariable().toString().equals(Bypass.OPEN)) {
							map.put("SendSMSStatus", ResultStatus.SUCCESS.name());
						} else {
							map.put("SendSMSStatus", "failReason");
						}
						break;
					default:
						break;
					}
				}
			} else {
				log.debug("smiValues is null");
			}

			return map;

		} catch (Exception e) {
			log.error("[MCU_ID = " + mcuId + "][METER_ID = " + meterId + "] " + e, e);
			throw new Exception(e.getMessage());
		} finally {
			try {
				txManager.commit(txStatus);
			} catch (Exception e) {
				log.error(e, e);
				// if (txManager != null && !txStatus.isCompleted())
				// txManager.rollback(txStatus);
			}

		}

	}

	/**
	 * Read Table
	 *
	 * @param mcuId
	 *            MCU Indentifier
	 * @param meterId
	 * @param tablenum
	 * @return energy meter data list
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public byte[] cmdReadTable(String mcuId, String meterId, int tablenum) throws FMPMcuException, Exception {
		log.debug("cmdReadTable(" + mcuId + "," + meterId + "," + tablenum + ")");

		Target target = null;
		Vector<SMIValue> datas = new Vector<SMIValue>();

		SMIValue smiValue = null;
		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");

			txStatus = txManager.getTransaction(null);

			Modem modem = meterDao.get(meterId).getModem();
			target = CmdUtil.getTarget(modem);

			smiValue = DataUtil.getSMIValueByObject("sensorID", modem.getDeviceSerial());
			datas.add(smiValue);
			smiValue = DataUtil.getSMIValue(new INT(tablenum));
			datas.add(smiValue);

			Object[] params = new Object[] { target, "cmdReadTable", datas };

			String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

			Object obj = null;
			try {
				obj = invoke(params, types);
			} catch (Exception e) {
				log.error(e, e);
				throw new Exception(makeMessage(e.getMessage()));
			}

			SMIValue[] smiValues = null;

			if (obj instanceof Integer) {
				log.error("Error Code Return");
				throw makeMcuException(((Integer) obj).intValue());
			} else if (obj instanceof SMIValue[]) {
				smiValues = (SMIValue[]) obj;
			} else {
				log.error("Unknown Return Value");
				throw new Exception("Unknown Return Value");
			}

			byte[] bx = null;
			if (smiValues != null && smiValues.length != 0) {
				log.debug(smiValues[0].toString());
				bx = ((OCTET) smiValues[0].getVariable()).getValue();
			} else {
				log.debug("smiValues is null");
			}

			txManager.commit(txStatus);
			return bx;

		} catch (Exception e) {
			if (txManager != null)
				txManager.rollback(txStatus);
			log.error(e, e);
			throw new Exception(e);
		}

	}

	/**
	 * Read Table
	 *
	 * @param mcuId
	 *            MCU Indentifier
	 * @param sensorId
	 * @param tablenum
	 * @param data
	 *            stream
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public byte[] cmdWriteTable(String mcuId, String meterId, int tablenum, byte[] stream)
			throws FMPMcuException, Exception {
		log.debug("cmdWriteTable(" + mcuId + "," + meterId + "," + tablenum + "," + Hex.decode(stream) + ")");

		Target target = null;
		Vector<SMIValue> datas = new Vector<SMIValue>();

		SMIValue smiValue = null;
		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");

			txStatus = txManager.getTransaction(null);

			Meter meter = meterDao.get(meterId);
			target = CmdUtil.getTarget(meter.getModem());

			smiValue = DataUtil.getSMIValueByObject("sensorID", meter.getModem().getDeviceSerial());
			datas.add(smiValue);
			smiValue = DataUtil.getSMIValue(new INT(tablenum));
			datas.add(smiValue);
			smiValue = DataUtil.getSMIValue(new OCTET(stream));
			datas.add(smiValue);

			Object[] params = new Object[] { target, "cmdWriteTable", datas };

			String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

			Object obj = null;
			try {
				obj = invoke(params, types);
			} catch (Exception e) {
				log.error(e, e);
				throw new Exception(makeMessage(e.getMessage()));
			}

			SMIValue[] smiValues = null;

			if (obj instanceof Integer) {
				log.error("Error Code Return");
				int errcode = ((Integer) obj).intValue();

				if (ErrorCode.getCodeString(errcode).equals("UKNOWN ERROR CODE")) {
					log.debug("UKNOWN ERROR CODE=>" + errcode);
					new FMPMcuException("UKNOWN ERROR CODE", errcode);
				} else {
					throw makeMcuException(errcode);
				}
			} else if (obj instanceof SMIValue[]) {
				smiValues = (SMIValue[]) obj;
			} else {
				log.error("Unknown Return Value");
				throw new Exception("Unknown Return Value");
			}

			byte[] bx = null;
			if (smiValues != null && smiValues.length != 0) {
				log.debug(smiValues[0].toString());
				bx = ((OCTET) smiValues[0].getVariable()).getValue();
			} else {
				log.debug("smiValues is null");
			}

			txManager.commit(txStatus);
			return bx;

		} catch (Exception e) {
			if (txManager != null)
				txManager.rollback(txStatus);
			log.error(e, e);
			throw new Exception(e);
		}

	}

	/**
	 * Read Table Relay Switch Status(GE I210)
	 *
	 * @param mcuId
	 *            MCU Identifier
	 * @param meterId
	 *            Meter Identifier
	 * @throws Exception
	 */
	@Override
	public Map<String, String> getRelaySwitchStatus(String mcuId, String meterId) throws Exception {
		Map<String, String> map = null;
		int nOption = OnDemandOption.READ_OPTION_RELAY.getCode(); // read table
		// byte[] rtnByteArray
		// = cmdReadTable( mcuId, meterId, tablenum);

		Map resultMap = cmdOnDemandMeter(mcuId, meterId, nOption);

		if (resultMap != null) {
			map = new HashMap<String, String>();

			// SM110 or I210
			String parser = (String) resultMap.get("Parser");
			if (parser.contains("SM110") || parser.contains("I210")) {
				map.put("switchStatus", SM110Meta.getSwitchStatus((String) resultMap.get("relay status")));
				map.put("activateStatus", SM110Meta.getActivateStatus((String) resultMap.get("relay activate status")));
			} else if (parser.contains("SX2")) {
				map.put("switchStatus", (String) resultMap.get("relay status"));
			}
		}

		return map;
	}

	/**
	 * Write Table Relay Switch and Activate On/Off
	 *
	 * @param mcuId
	 *            MCU Identifier
	 * @param meterId
	 *            meter Identifier
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public Map<String, String> cmdRelaySwitchAndActivate(String mcuId, String meterId, String cmdNum)
			throws FMPMcuException, Exception {
		Map<String, String> map = null;
		// int tablenum = 131;
		// byte[] b = new byte[1];

		// activate off :0, activate on :1, relay on:2, relay off:3
		// b[0] = Byte.parseByte(cmdNum);

		// log.debug("cmdWriteTable("+mcuId+","+meterId+","
		// +tablenum+","+ Integer.toHexString(b[0])+")");

		int nOption = 0;
		int cmd = Integer.parseInt(cmdNum);

		switch (cmd) {
		case 0:
			nOption = OnDemandOption.WRITE_OPTION_ACTOFF.getCode();
			break;
		case 1:
			nOption = OnDemandOption.WRITE_OPTION_ACTON.getCode();
			break;
		case 2:
			nOption = OnDemandOption.WRITE_OPTION_RELAYON.getCode();
			break;
		case 3:
			nOption = OnDemandOption.WRITE_OPTION_RELAYOFF.getCode();
			break;
		}

		Map resultMap = cmdOnDemandMeter(mcuId, meterId, nOption);

		if (resultMap != null) {
			map = new HashMap<String, String>();
			// map.put( "switchStatus",
			// SM110Meta.getSwitchStatus((String)resultMap.get("relay status"))
			// );
			// map.put( "activateStatus",
			// SM110Meta.getActivateStatus((String)resultMap.get("relay activate
			// status")) );
			map.put("switchStatus", (String) resultMap.get("relay status"));
			map.put("activateStatus", (String) resultMap.get("relay activate status"));
		}

		// byte[] rtnByteArray = cmdWriteTable( mcuId, meterId, tablenum, b);

		// map = new HashMap<String,String>();
		// map.put( "switchStatus", SM110Meta.getSwitchStatus(Integer.toString(
		// rtnByteArray[6])) );
		// map.put( "activateStatus", Integer.toString( rtnByteArray[7] ) );
		return map;
	}

	/**
	 * Aidon MCCB
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param meterId
	 *            Meter ID
	 * @param req
	 *            mccb control request
	 * @return response message
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public String cmdAidonMCCB(String mcuId, String meterId, String req) throws FMPMcuException, Exception {
		log.debug("cmdAidonMCCB(" + mcuId + "," + meterId + "," + req + ")");

		Target target = null;
		Vector<SMIValue> datas = new Vector<SMIValue>();
		byte reqBit = Mccb.getReqBit(req);

		SMIValue smiValue = null;

		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");

			txStatus = txManager.getTransaction(null);

			Meter meter = meterDao.get(meterId);
			target = CmdUtil.getTarget(meter.getModem());

			/*
			 * if (modem.getMcu() != null &&
			 * modem.getMcu().getSysSwRevision().compareTo(mcuRevision) >= 0 ) {
			 * smiValue = DataUtil.getSMIValue("gsiSensorID",
			 * modem.getDeviceSerial()); datas.add(smiValue); } else { smiValue
			 * = DataUtil.getSMIValue("sensorID", modem.getDeviceSerial()); }
			 */
			smiValue = DataUtil.getSMIValueByObject("sensorID", meter.getModem().getDeviceSerial());

			txManager.commit(txStatus);
		} catch (Exception e) {
			txManager.rollback(txStatus);
			throw e;
		}

		datas.add(smiValue);
		smiValue = DataUtil.getSMIValue(new BYTE(reqBit));
		datas.add(smiValue);

		Object[] params = new Object[] { target, "cmdAidonMCCB", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return : " + obj);
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		log.debug("return value count [" + smiValues.length + "]");

		int res = ((BYTE) smiValues[0].getVariable()).getValue();
		String msg = null;
		try {
			msg = Mccb.getMsg(reqBit, (byte) res);
		} catch (Exception e) {
			log.warn("MCCB Command : " + e.getMessage());
			msg = cmdAidonMCCB(mcuId, meterId, "Get Last Accepted Control Msg");
		}
		return msg;
	}

	/**
	 * Kamstrup CID
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param sensorId
	 *            Sensor ID
	 * @param req
	 *            mccb control request
	 * @return response message
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public byte[] cmdKamstrupCID(String mcuId, String meterId, String[] req) throws FMPMcuException, Exception {
		log.debug("(" + mcuId + "," + meterId + "," + req + ")");

		Target target = null;
		Vector<SMIValue> datas = new Vector<SMIValue>();
		byte[][] reqBit = KamstrupCIDMeta.getRequest(req);

		SMIValue smiValue = null;

		TransactionStatus txstatus = null;
		try {
		    txstatus = txmanager.getTransaction(null);
			Set<Condition> condition = new HashSet<Condition>();
			condition.add(new Condition("meter", new Object[] { "m" }, null, Restriction.ALIAS));
			condition.add(new Condition("m.mdsId", new Object[] { meterId }, null, Restriction.EQ));
			List<Modem> modems = modemDao.findByConditions(condition);
			Modem modem = null;
			if (modems != null && modems.size() == 1)
				modem = modems.get(0);

			txmanager.commit(txstatus);
			
			if (modem.getModemType().equals(ModemType.MMIU)) {

				int seq = new Random().nextInt(100) & 0xFF;
				String[] requestMessage = KamstrupCIDMeta.getRequestValue(req);
				String[] req2 = null;
				String kind = null;
				if (requestMessage != null && requestMessage.length > 0) {
					if (requestMessage[0].equals("cmGetCutOff")) {
						kind = "CM02";
						req2 = new String[2];
						req2[0] = seq + "";
						req2[1] = "F";
					} else if (requestMessage[0].equals("cmSetCutOff")) {
						kind = "CM03";
						req2 = new String[3];
						req2[0] = seq + "";
						req2[1] = "F";
						req2[2] = requestMessage[1];
					}
					return cmdKamstrupCID(mcuId, meterId, kind, req2);
				} else {
					if (KamstrupCIDMeta.CID.SetClock.getCommand().equals(req[0])
							&& Protocol.SMS.equals(modem.getProtocolType())) {
						kind = "CM21";
						req2 = new String[2];
						req2[0] = seq + "";
						req2[1] = "B";
					}
					return cmdKamstrupCID(mcuId, meterId, kind, req2);
				}
			}

			target = CmdUtil.getTarget(modem);
			smiValue = DataUtil.getSMIValueByObject("sensorID", modem.getDeviceSerial());
			datas.add(smiValue);

			log.info("Command[" + Hex.decode(reqBit[0]));
			smiValue = DataUtil.getSMIValue(new BYTE(reqBit[0][0]));
			datas.add(smiValue);
			for (int i = 1; i < reqBit.length; i++) {
				smiValue = DataUtil.getSMIValue(new OCTET(reqBit[i]));
				datas.add(smiValue);
			}

		} catch (Exception e) {
		    if (txstatus != null && !txstatus.isCompleted()) txmanager.rollback(txstatus);
			throw e;
		}

		Object[] params = new Object[] { target, "cmdKamstrupCID", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return : " + obj);
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		log.debug("return value count [" + smiValues.length + "]");

		byte[] res = ((OCTET) smiValues[0].getVariable()).getValue();
		// 2014.02.25 SetClock 응답이 0106으로 와서 예외처리
		if (reqBit[0][0] == KamstrupCIDMeta.CID.SetClock.getCid()) {
			ByteArray ba = new ByteArray();
			ba.append(new byte[] { 0x00, 0x00, 0x00, reqBit[0][0] });
			ba.append(res);
			return ba.toByteArray();
		} else
			return res;
	}

	/**
	 * Kamstrup CID for Kamstrup GPRS Modem
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param meterId
	 *            Meter ID
	 * @param kind
	 *            commandKind
	 * @param param
	 *            mccb control request
	 * @return response message
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public byte[] cmdKamstrupCID(String mcuId, String meterId, String kind, String[] param)
			throws FMPMcuException, Exception {
		log.debug("cmdKamstrupCID(" + mcuId + "," + meterId + "," + kind + ")");
		for (int i = 0; param != null && i < param.length; i++) {
			log.debug("Parameters[" + i + "] contents[" + param[i] + "]");
		}

		Target target = null;
		Vector<SMIValue> datas = null;
		TransactionStatus txstatus = null;
		try {
		    txstatus = txmanager.getTransaction(null);
			Set<Condition> condition = new HashSet<Condition>();
			condition.add(new Condition("meter", new Object[] { "m" }, null, Restriction.ALIAS));
			condition.add(new Condition("m.mdsId", new Object[] { meterId }, null, Restriction.EQ));
			List<Modem> modems = modemDao.findByConditions(condition);
			Modem modem = null;
			if (modems != null && modems.size() == 1)
				modem = modems.get(0);

			txmanager.commit(txstatus);
			
			target = CmdUtil.getTarget(modem);
			datas = new Vector<SMIValue>();
			SMIValue smiValue = null;

			smiValue = DataUtil.getSMIValue(new OCTET(kind));
			datas.add(smiValue);

			for (int i = 0; param != null && i < param.length; i++) {
				smiValue = DataUtil.getSMIValue(new OCTET(param[i]));
				datas.add(smiValue);
			}
		} catch (Exception e) {
		    if (txstatus != null && !txstatus.isCompleted()) txmanager.rollback(txstatus);
			log.error(e, e);
			throw e;
		}

		Object[] params = new Object[] { target, "cmdKamstrupCID", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return : " + obj);
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		log.debug("return value count [" + smiValues.length + "]");

		byte[] res = ((OCTET) smiValues[0].getVariable()).getValue();
		return res;
	}

	/**
	 * Get Meter All
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param fromTime
	 *            from Time
	 * @param toTime
	 *            from Time
	 * @return energy meter data list
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public MeterData[] cmdGetMeterAll(String mcuId, String fromTime, String toTime) throws FMPMcuException, Exception {
		log.debug("cmdGetMeterAll(" + mcuId + ")");

		Target target = CmdUtil.getTarget(mcuId);
		Vector<SMIValue> datas = new Vector<SMIValue>();

		SMIValue smiValue = DataUtil.getSMIValue(new TIMESTAMP(fromTime));
		datas.add(smiValue);
		smiValue = DataUtil.getSMIValue(new TIMESTAMP(toTime));
		datas.add(smiValue);

		Object[] params = new Object[] { target, "cmdGetMeterAll", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		log.debug("return value count [" + smiValues.length + "]");

		// EnergyMeterData[] eds = new EnergyMeterData[smiValues.length];
		MeterData ed = null;
		ArrayList<MeterData> eds = new ArrayList<MeterData>();
		log.debug("return value count [" + smiValues.length + "]");
		OPAQUE mdv = null;
		String className = null;
		String meterId = null;
		meterDataEntry value = null;
		meterEntry value2 = null;
		MeterDataParser edp = null;
		byte[] mdb = null;

		Meter meter = null;
		ModemConfig modemConfig = null;
		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;

		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");

			txStatus = txManager.getTransaction(null);

			for (int i = 0; i < smiValues.length; i++) {
				try {
					mdv = (OPAQUE) smiValues[i].getVariable();
					className = mdv.getClsName();
					log.debug("Get Meter : return Class Name = " + className);
					if (mdv.getValue() instanceof meterDataEntry) {
						value = (meterDataEntry) mdv.getValue();
						meterId = value.getMdID().toString();
						mdb = value.getMdData().getValue();

						meter = meterDao.get(meterId);
						modemConfig = (ModemConfig) meter.getModem().getModel().getDeviceConfig();
						edp = (MeterDataParser) Class.forName(modemConfig.getParserName()).newInstance();
						edp.setMeter(meter);

						log.debug("Meter Data Size [" + mdb.length + "]");
						log.debug(Hex.decode(mdb));
						edp.parse(mdb);

						ed = new MeterData();
						ed.setMeterId(meterId);
						ed.setTime(value.getMdTime().toString());
						ed.setType(value.getMdType().toString());
						ed.setVendor(value.getMdVendor().toString());
						ed.setServiceType(value.getMdServiceType().toString());
						ed.setParser(edp);
						eds.add(ed);
					} else if (mdv.getValue() instanceof meterEntry) {
						value2 = (meterEntry) mdv.getValue();
						meterId = value2.getMtrSerial().toString();
						mdb = value2.getMtrData().getValue();

						/**
						 * modified by p.y.k get parser changed.
						 */
						meter = meterDao.get(meterId);
						modemConfig = (ModemConfig) meter.getModem().getModel().getDeviceConfig();
						edp = (MeterDataParser) Class.forName(modemConfig.getParserName()).newInstance();
						edp.setMeter(meter);

						log.debug("Meter Data Size [" + mdb.length + "]");
						log.debug(Hex.decode(mdb));
						edp.parse(mdb);

						ed = new MeterData();
						ed.setMeterId(meterId);
						ed.setTime(value2.getMtrTime().toString());
						ed.setParser(edp);
						eds.add(ed);
					}
				} catch (Exception e) {
					log.error(e, e);
				}
			}

			txManager.commit(txStatus);
		} catch (Exception e) {

			if (txStatus != null) {
				txManager.rollback(txStatus);
			}
			log.error(e, e);
		}

		return eds.toArray(new MeterData[0]);
	}

	/**
	 * 검침 및 제어 기능
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param meterId
	 *            Meter ID
	 * @param modemId
	 *            Modem Id
	 * @return energy meter data
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public MeterData cmdOnDemandMeter(String mcuId, String meterId, String modemId, String nOption, String fromDate,
			String toDate) throws FMPMcuException, Exception {
		log.debug("cmdOnDemandMeter(" + mcuId + "," + meterId + "," + modemId + "," + nOption + "," + fromDate + ","
				+ toDate + ")");

		Target target = null;
		Meter meter = null;
		JpaTransactionManager txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");
		TransactionStatus txStatus = null;
		String command = "cmdOnDemandMeter"; // INSERT SP-116

		try {
			txStatus = txManager.getTransaction(null);
			meter = meterDao.get(meterId);
			Modem modem = meter.getModem();

			ModemType modemType = modem.getModemType();
			target = CmdUtil.getTarget(modem);
			// MCU mcu = mcuDao.get(mcuId);
			mcuId = target.getTargetId();
			DeviceModel model = meter.getModel();

			log.debug("meterModel=" + model.getId());
			// log.debug("meterVendor="+model.getDeviceVendor().getName());
			int lpInterval = meter.getLpInterval();

			txManager.commit(txStatus);

			int[] offsetCount = CmdUtil.convertOffsetCount(model, lpInterval, modemType, fromDate, toDate);

			if (target.getNameSpace() != null && !"".equals(target.getNameSpace())
					&& target.getNameSpace().equals("NG")) {
				return cmdOnDemandByMeter(mcuId, meterId, modemId, nOption, fromDate, toDate);
			}

			Vector<SMIValue> datas = new Vector<SMIValue>();

			SMIValue smiValue = null;

			if ("NP".equals(target.getNameSpace())) {

				smiValue = DataUtil
						.getSMIValue(new INT(Integer.parseInt(nOption == null || nOption.equals("") ? "0" : nOption)));
				datas.add(smiValue);
				if (offsetCount[0] != 0) {
					smiValue = DataUtil.getSMIValue(new INT(offsetCount[0]));
					datas.add(smiValue);

					if (offsetCount[1] != 0) {
						smiValue = DataUtil.getSMIValue(new INT(offsetCount[1]));
						datas.add(smiValue);
					}
				}
				smiValue = DataUtil.getSMIValue(new INT(0x07));// sensor ID
				datas.add(smiValue);
				smiValue = DataUtil.getSMIValue(new STRING(modem.getDeviceSerial()));
				datas.add(smiValue);

			}
			// INSERT START SP-116
			else if ("SP".equals(target.getNameSpace())) {
				smiValue = DataUtil.getSMIValue(target.getNameSpace(),
						new INT(OnDemandOption.READ_CUMMULATIVE_CONSUMPTION.getCode()));
				datas.add(smiValue);
				smiValue = DataUtil.getSMIValue(target.getNameSpace(), new INT(offsetCount[0]));
				datas.add(smiValue);

				smiValue = DataUtil.getSMIValue(target.getNameSpace(), new INT(offsetCount[1]));
				datas.add(smiValue);

				smiValue = DataUtil.getSMIValue(target.getNameSpace(), new INT(0x01));// meter
																						// ID
																						// filter
				datas.add(smiValue);
				smiValue = DataUtil.getSMIValueByObject(target.getNameSpace(), "stringEntry", meterId); // meter
																										// ID
				datas.add(smiValue);

				command = "cmdOndemand";
			}
			// INSERT END SP-116
			else {
				smiValue = DataUtil.getSMIValueByObject("sensorID", modem.getDeviceSerial());
				datas.add(smiValue);
				smiValue = DataUtil
						.getSMIValue(new INT(Integer.parseInt(nOption == null || nOption.equals("") ? "0" : nOption)));
				datas.add(smiValue);
				if ((modemType == ModemType.MMIU || modemType == ModemType.Converter)
						&& (model.getCode().equals(MeterModel.EDMI_Mk10A.getCode())
								|| model.getCode().equals(MeterModel.EDMI_Mk10E.getCode())
								|| model.getCode().equals(MeterModel.EDMI_Mk6N.getCode()))
						|| model.getCode().equals(MeterModel.DLMSKEPCO.getCode())) {
					smiValue = DataUtil.getSMIValue(new TIMESTAMP(fromDate));
					datas.add(smiValue);
					smiValue = DataUtil.getSMIValue(new TIMESTAMP(toDate));
					datas.add(smiValue);
				} else {
					if (offsetCount[0] != 0) {
						smiValue = DataUtil.getSMIValue(new INT(offsetCount[0]));
						datas.add(smiValue);

						if (offsetCount[1] != 0) {
							smiValue = DataUtil.getSMIValue(new INT(offsetCount[1]));
							datas.add(smiValue);
						}
					}
				}
			}

			Object[] params = new Object[] { target, command, // UPDATE SP-116
					datas };

			String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

			Object obj = null;
			try {
				obj = invoke(params, types);
			} catch (Exception e) {
				log.error(e, e);
				throw new Exception(makeMessage(e.getMessage()));
			}

			SMIValue[] smiValues = null;

			if (obj instanceof Integer) {
				log.error("Error Code Return");
				throw makeMcuException(((Integer) obj).intValue());
			} else if (obj instanceof SMIValue[]) {
				smiValues = (SMIValue[]) obj;
				if (smiValues.length > 0) {
					smiValue = smiValues[0];
				} else {
					log.error("smiValue size 0");
					throw new FMPMcuException("no value exist");
				}
			} else {
				log.error("Unknown Return Value");
				throw new FMPMcuException("Unknown Return Value");
			}

			// 통신시간을 업데이트한다.
			// meter.setLastReadDate(DateTimeUtil.getDateString(new Date()));
			// modem.setLastLinkTime(meter.getLastReadDate());

			txStatus = txManager.getTransaction(null);
			meter = meterDao.get(meterId);
			MeterData ed = new MeterData();
			MeterDataParser edp = null;
			DeviceModel deviceModel = meter.getModel();

			if (deviceModel == null)
				deviceModel = meter.getModem().getModel();

			DeviceConfig deviceConfig = deviceModel.getDeviceConfig();

			if (deviceConfig.getOndemandParserName() != null && !"".equals(deviceConfig.getOndemandParserName()))
				edp = (MeterDataParser) Class.forName(deviceConfig.getOndemandParserName()).newInstance();
			else
				edp = (MeterDataParser) Class.forName(deviceConfig.getParserName()).newInstance();

			edp.setMeter(meter);
			edp.setMeteringTime(DateTimeUtil.getDateString(new Date()));
			edp.setOnDemand(true);
			ed.setMeterId(meterId);

			log.debug("SMIValue Return Class=" + smiValue.getVariable().getClass().getName());

			if (smiValue.getVariable() instanceof OPAQUE) {

				OPAQUE mdv = (OPAQUE) smiValue.getVariable();
				log.debug("Get Meter : return ClassName[" + mdv.getClsName() + "] MIB[" + mdv.getMIBName() + "]");

				obj = mdv.getValue();
				if (obj instanceof meterDataEntry) {
					meterDataEntry value = (meterDataEntry) obj;

					// meter id
					OCTET ocMeterId = new OCTET(20);
					ocMeterId.setIsFixed(true);
					String meterSerial = meter.getMdsId() == null ? "" : meter.getMdsId();
					byte[] bMeterid = DataUtil.fillCopy(meterSerial.getBytes(), (byte) 0x20, 20);
					ocMeterId.setValue(bMeterid);
					value.setMdSerial(ocMeterId);

					edp.parse(value.getMdData().getValue());
					ed.setTime(value.getMdTime().toString());
					ed.setType(value.getMdType().toString());
					ed.setVendor(value.getMdVendor().toString());
					ed.setServiceType(value.getMdServiceType().toString());
				} else if (obj instanceof meterLPEntry) {
					meterLPEntry value = (meterLPEntry) obj;
					ByteArrayOutputStream bao = new ByteArrayOutputStream();
					byte[] bx = value.getMlpData().getValue();
					byte[] timestamp = new byte[7];
					log.debug("bx: " + Hex.decode(bx));

					int dataCnt = value.getMlpDataCount().getValue();
					if (dataCnt == 1) {
						System.arraycopy(bx, 0, timestamp, 0, timestamp.length);
						bao.write(bx, timestamp.length, bx.length - timestamp.length);
					} else {
						int pos = 0;
						byte[] blen = new byte[2];
						int ilen = 0;

						for (int i = 0; i < dataCnt; i++) {
							// 데이타의 길이를 가져온다.
							System.arraycopy(bx, pos, blen, 0, blen.length);
							DataUtil.convertEndian(blen);
							ilen = DataUtil.getIntTo2Byte(blen);
							pos += blen.length;
							// 시간을 가져온다.
							System.arraycopy(bx, pos, timestamp, 0, timestamp.length);
							pos += timestamp.length;
							// 데이타 부분만 바이트 배열로 모은다.
							bao.write(bx, pos, ilen - timestamp.length);
							pos += ilen - timestamp.length;
						}
					}
					log.debug("bao: " + Hex.decode(bao.toByteArray()));
					edp.parse(bao.toByteArray());
					ed.setTime(new TIMESTAMP(timestamp).getValue());
					ed.setType(value.getMlpType().toString());
					ed.setVendor(value.getMlpVendor().toString());
					ed.setServiceType(value.getMlpServiceType().toString());
					bao.flush();
					bao.close();
				}
				ed.setParser(edp);
				MDData mdData = new MDData(new WORD(1));
				mdData.setMcuId(mcuId);
				mdData.setMdData(mdv.encode());
				// handler.putServiceData(ProcessorHandler.SERVICE_MEASUREMENTDATA,
				// mdData);
			}

			if (smiValue.getVariable() instanceof OCTET) {

				OCTET ocMeterId = new OCTET(20);
				ocMeterId.setIsFixed(true);
				String meterSerial = meter.getMdsId() == null ? "" : meter.getMdsId();
				byte[] bMeterid = DataUtil.fillCopy(meterSerial.getBytes(), (byte) 0x20, 20);
				ocMeterId.setValue(bMeterid);

				OCTET ocModemId = new OCTET(8);
				ocModemId.setIsFixed(true);
				byte[] bModemid = Hex.encode(modemId);
				ocModemId.setValue(bModemid);

				OCTET stream = (OCTET) smiValue.getVariable();
				byte[] data = stream.getValue();

				String currTime = null;
				try {
					currTime = TimeUtil.getCurrentTime();
				} catch (Exception e) {
				}
				meterDataEntry value = new meterDataEntry();
				value.setDataCount(1);
				value.setMdData(new OCTET(data));
				value.setMdID(ocModemId);
				value.setMdSerial(ocMeterId);
				value.setMdServiceType(new BYTE(Integer.parseInt(CommonConstants.getDataSvcCode(DataSVC.Electricity))));
				value.setMdTime(new TIMESTAMP(currTime));
				value.setMdType(new BYTE(Integer.parseInt(CommonConstants.getModemTypeCode(ModemType.MMIU))));
				value.setMdVendor(new BYTE(MeterVendor.LSIS.getCode()[0].intValue()));

				int dataLen = 7 + value.getMdData().getValue().length;
				ByteArray ba = new ByteArray();
				ba.append(value.getMdID().getValue());
				ba.append(value.getMdSerial().getValue());
				ba.append(value.getMdType().encode());
				ba.append(value.getMdServiceType().encode());
				ba.append(value.getMdVendor().encode());
				ba.append(value.getDataCount().encode());
				ba.append(DataUtil.get2ByteToInt(true, dataLen)); // Timestamp.length+Data.length
				ba.append(value.getMdTime().encode());
				ba.append(value.getMdData().getValue());

				edp.parse(data);
				ed.setTime(value.getMdTime().toString());
				ed.setType(value.getMdType().toString());
				ed.setVendor(value.getMdVendor().toString());
				ed.setServiceType(value.getMdServiceType().toString());
				ed.setParser(edp);
				MDData mdData = new MDData(new WORD(1));
				mdData.setMcuId(mcuId);
				mdData.setTotalLength(ba.toByteArray().length);
				mdData.setMdData(ba.toByteArray());
				// handler.putServiceData(ProcessorHandler.SERVICE_MEASUREMENTDATA,
				// mdData);

			}
			/*
			 * MDHistoryData mdHistoryData = new MDHistoryData();
			 * mdHistoryData.setEntryCount(1); mdHistoryData.setMcuId(mcuId);
			 * mdHistoryData.setMdData(mdv.encode());
			 * mdsMain.save(mdHistoryData, true);
			 */

			txManager.commit(txStatus);
			return ed;
		} catch (Exception e) {
			log.error(e, e);
			if (txManager != null && !txStatus.isCompleted())
				txManager.rollback(txStatus);
			throw new Exception(e);
		}
	}

	/**
	 * 검침 및 제어 기능
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param meterId
	 *            Meter ID
	 * @param modemId
	 *            Modem Id
	 * @return energy meter data
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public MeterData cmdOnRecoveryDemandMeter(String mcuId, String modemId, String nOption, int offSet, int count)
			throws FMPMcuException, Exception {
		log.debug(
				"cmdOnRecoveryDemandMeter(" + mcuId + "," + modemId + "," + nOption + "," + offSet + "," + count + ")");

		Target target = null;
		Meter meter = null;
		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");

			txStatus = txManager.getTransaction(null);

			Modem modem = modemDao.get(modemId);

			// log.debug("meterVendor="+model.getDeviceVendor().getName());

			target = CmdUtil.getTarget(modem);

			Vector<SMIValue> datas = new Vector<SMIValue>();

			int[] offsetCount = new int[] { offSet, count };

			SMIValue smiValue = null;

			/*
			 * if (modem.getMcu() != null &&
			 * modem.getMcu().getSysSwRevision().compareTo(mcuRevision) >= 0 ) {
			 * smiValue = DataUtil.getSMIValue("gsiSensorID",
			 * modem.getDeviceSerial()); datas.add(smiValue); } else { smiValue
			 * = DataUtil.getSMIValue("sensorID", modem.getDeviceSerial()); }
			 */
			smiValue = DataUtil.getSMIValueByObject("sensorID", modem.getDeviceSerial());

			datas.add(smiValue);
			smiValue = DataUtil.getSMIValue(new INT(Integer.parseInt(nOption)));
			datas.add(smiValue);

			if (offsetCount[0] != 0) {
				smiValue = DataUtil.getSMIValue(new INT(offsetCount[0]));
				datas.add(smiValue);

				if (offsetCount[0] != 0) {
					smiValue = DataUtil.getSMIValue(new INT(offsetCount[1]));
					datas.add(smiValue);
				}
			}

			Object[] params = new Object[] { target, "cmdOnDemandMeter", datas };

			String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

			Object obj = null;
			try {
				obj = invoke(params, types);
			} catch (Exception e) {
				log.error(e, e);
				throw new Exception(makeMessage(e.getMessage()));
			}

			SMIValue[] smiValues = null;

			if (obj instanceof Integer) {
				log.error("Error Code Return");
				throw makeMcuException(((Integer) obj).intValue());
			} else if (obj instanceof SMIValue[]) {
				smiValues = (SMIValue[]) obj;
				if (smiValues.length > 0) {
					smiValue = smiValues[0];
				} else {
					log.error("smiValue size 0");
					throw new FMPMcuException("no value exist");
				}
			} else {
				log.error("Unknown Return Value");
				throw new FMPMcuException("Unknown Return Value");
			}

			MeterData ed = new MeterData();
			OPAQUE mdv = (OPAQUE) smiValue.getVariable();

			log.debug("Get Meter : return ClassName[" + mdv.getClsName() + "] MIB[" + mdv.getMIBName() + "]");
			if (modem != null) {
				Set meterSet = modem.getMeter();
				if (meterSet != null && meterSet.size() > 0) {
					meter = (Meter) meterSet.toArray()[0];
				} else {
					throw new Exception("MCU[" + mcuId + "] - Modem[" + modemId + "] don't have a Meter!!");
				}
			} else {
				throw new Exception("MCU[" + mcuId + "] - Modem[" + modemId + "] does not Exist!!");
			}

			DeviceModel model = meter.getModel();
			log.debug("meterModel=" + model.getId());
			MeterConfig meterConfig = (MeterConfig) meter.getModel().getDeviceConfig();
			MeterDataParser edp = null;
			if (meterConfig.getOndemandParserName() != null && !"".equals(meterConfig.getOndemandParserName()))
				edp = (MeterDataParser) Class.forName(meterConfig.getOndemandParserName()).newInstance();
			else
				edp = (MeterDataParser) Class.forName(meterConfig.getParserName()).newInstance();
			edp.setMeter(meter);

			ed.setMeterId(meter.getMdsId());
			ed.setParser(edp);
			obj = mdv.getValue();
			if (obj instanceof meterDataEntry) {
				meterDataEntry value = (meterDataEntry) obj;
				edp.parse(value.getMdData().getValue());
				ed.setTime(value.getMdTime().toString());
				ed.setType(value.getMdType().toString());
				ed.setVendor(value.getMdVendor().toString());
				ed.setServiceType(value.getMdServiceType().toString());
			} else if (obj instanceof meterLPEntry) {
				meterLPEntry value = (meterLPEntry) obj;
				ByteArrayOutputStream bao = new ByteArrayOutputStream();
				byte[] bx = value.getMlpData().getValue();
				log.debug("bx: " + Hex.decode(bx));
				byte[] timestamp = new byte[7];
				bao.write(bx, 0, timestamp.length);
				timestamp = bao.toByteArray();
				bao.flush();
				bao = new ByteArrayOutputStream();
				bao.write(bx, 7, bx.length - 7);
				log.debug("bao: " + Hex.decode(bao.toByteArray()));
				edp.parse(bao.toByteArray());
				ed.setTime(new TIMESTAMP(timestamp).getValue());
				ed.setType(value.getMlpType().toString());
				ed.setVendor(value.getMlpVendor().toString());
				ed.setServiceType(value.getMlpServiceType().toString());
				bao.flush();
				bao.close();
			}

			MDHistoryData mdHistoryData = new MDHistoryData();
			mdHistoryData.setEntryCount(1);
			mdHistoryData.setMcuId(mcuId);
			mdHistoryData.setMdData(mdv.encode());
			mdsMain.save(mdHistoryData, true);
			txManager.commit(txStatus);
			return ed;
		} catch (Exception e) {
			if (txManager != null)
				txManager.rollback(txStatus);
			throw new Exception(e);
		}
	}

	/**
	 * 사우디 데모용 gsm 온디맨드
	 * 
	 * @param meterId
	 * @param fromDateTime
	 * @param toDateTime
	 * @return
	 * @throws FMPMcuException
	 * @throws Exception
	 */
	public String cmdOnDemandMeter(String meterId, String fromDateTime, String toDateTime)
			throws FMPMcuException, Exception {
		Target target = null;
		log.debug("====cmdOnDemandMeter=====");
		log.debug("METER[" + meterId + "]");
		log.debug("fromDateTime[" + fromDateTime + "]");
		log.debug("toDateTime[" + toDateTime + "]");
		// meterId ="AR0202A03202";
		if (meterId == null || meterId.length() < 1)
			throw new Exception(makeMessage("meter_id is not avalilable!!! "));

		if (fromDateTime == null || fromDateTime.length() < 10)
			throw new Exception(makeMessage("fromDateTime error!!!"));

		if (toDateTime == null || toDateTime.length() < 10)
			throw new Exception(makeMessage("toDateTime error!!!"));

		fromDateTime += "0000";
		toDateTime += "5959";

		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		Vector<SMIValue> datas = new Vector<SMIValue>();
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");

			txStatus = txManager.getTransaction(null);

			Meter meter = meterDao.get(meterId);
			target = CmdUtil.getTarget(meter.getModem());

			datas.add(DataUtil.getSMIValueByObject("intEntry", "0"));
			datas.add(DataUtil.getSMIValueByObject("kmcID", "0"));
			datas.add(DataUtil.getSMIValueByObject("kmcImmediateID", "0"));

			log.debug("fromDateTime :" + fromDateTime);
			log.debug("toDateTime :" + toDateTime);
			datas.add(DataUtil.getSMIValueByObject("timeEntry", fromDateTime));
			datas.add(DataUtil.getSMIValueByObject("timeEntry", toDateTime));

			txManager.commit(txStatus);
		} catch (Exception e) {
			if (txManager != null)
				txManager.rollback(txStatus);
			throw e;
		}

		Object[] params = new Object[] { target, "cmdOnDemandMeter", datas };

		String[] types = new String[] { "java.util.Hashtable", "java.lang.String", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}
		OCTET result = (OCTET) smiValues[0].getVariable();
		log.debug(result.toString());
		return result.toString();
	}

	/**
	 * On-demand Meter for kamstrup mmiu meter
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param meterId
	 *            meter serial number
	 * @param modemId
	 *            modem id
	 * @return energy meter data
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public String[] cmdOnDemandMeter(String mcuId, String meterId, String modemId, String kind, String[] param)
			throws FMPMcuException, Exception {
		log.debug("cmdOnDemandMeter(" + mcuId + "," + meterId + "," + kind + ")");

		for (int i = 0; param != null && i < param.length; i++) {
			log.debug("Parameters[" + i + "] contents[" + param[i] + "]");
		}

		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		Vector<SMIValue> datas = new Vector<SMIValue>();
		SMIValue smiValue = null;
		Target target = null;
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");

			txStatus = txManager.getTransaction(null);

			Meter meter = meterDao.get(meterId);
			target = CmdUtil.getTarget(meter.getModem());

			smiValue = DataUtil.getSMIValue(new OCTET(kind));
			datas.add(smiValue);

			txManager.commit(txStatus);
		} catch (Exception e) {
			if (txManager != null)
				txManager.rollback(txStatus);
			throw e;
		}

		for (int i = 0; param != null && i < param.length; i++) {
			smiValue = DataUtil.getSMIValue(new OCTET(param[i]));
			datas.add(smiValue);
		}

		Object[] params = new Object[] { target, "cmdOnDemandMeter", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
			if (smiValues.length > 0) {
				smiValue = smiValues[0];
			} else {
				log.error("smiValue size 0");
				throw new FMPMcuException("no value exist");
			}
		} else {
			log.error("Unknown Return Value");
			throw new FMPMcuException("Unknown Return Value");
		}

		// EnergyMeterData ed = new EnergyMeterData();
		OPAQUE mdv = (OPAQUE) smiValue.getVariable();

		log.debug("Get Meter : return ClassName[" + mdv.getClsName() + "] MIB[" + mdv.getMIBName() + "]");

		// EnergyMeterDataParser edp = CmdUtil.getParser(meterId);
		// ed.setMeterId(meterId);
		// ed.setParser(edp);
		String[] ret = null;
		obj = mdv.getValue();
		if (obj instanceof meterDataEntry) {
			meterDataEntry value = (meterDataEntry) obj;
			ret = new String[2];
			ret[0] = value.getMdTime().toString();
			ret[1] = DataUtil.getIntTo4Byte(value.getMdData().getValue()) + "";
			// edp.parse(value.getMdData().getValue(), -1);
			// ed.setTime(value.getMdTime().toString());
			// ed.setType(value.getMdType().toString());
			// ed.setVendor(value.getMdVendor().toString());
			// ed.setServiceType(value.getMdServiceType().toString());
		}

		return ret;
	}

	/**
	 * On-demand Meter for kamstrup mmiu meter
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param meterId
	 *            meter serial number
	 * @param modemId
	 *            modem id
	 * @return no return data (async data request)
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public void cmdOnDemandMeterAsync(String mcuId, String meterId, String modemId, String kind, String[] param)
			throws FMPMcuException, Exception {
		log.debug("cmdOnDemandMeterAsync(" + mcuId + "," + meterId + "," + "," + kind + ")");

		for (int i = 0; param != null && i < param.length; i++) {
			log.debug("Parameters[" + i + "] contents[" + param[i] + "]");
		}

		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		Vector<SMIValue> datas = new Vector<SMIValue>();
		SMIValue smiValue = null;
		Target target = null;
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");

			txStatus = txManager.getTransaction(null);
			Meter meter = meterDao.get(meterId);
			target = CmdUtil.getTarget(meter.getModem());

			log.debug("target getPhoneNumber " + target.getPhoneNumber());
			log.debug("target getPort" + target.getPort());

			smiValue = DataUtil.getSMIValue(new OCTET(kind));
			datas.add(smiValue);
			log.debug("smiValue set end");

			txManager.commit(txStatus);
		} catch (Exception e) {
			if (txManager != null)
				txManager.rollback(txStatus);
			throw e;
		}

		for (int i = 0; param != null && i < param.length; i++) {
			smiValue = DataUtil.getSMIValue(new OCTET(param[i]));
			datas.add(smiValue);
		}
		log.debug("datas.add(smiValue).. ok..");
		Object[] params = new Object[] { target, "cmdOnDemandMeter", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			Target tar = (Target) params[0];
			String par2 = (String) params[1];
			log.debug("tar.getPhoneNumber() " + tar.getPhoneNumber());
			log.debug("tar.getPort() " + tar.getPort());
			log.debug("v " + par2);

			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}

	}

	/**
	 * set conf for ieiu modem
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param meterId
	 *            Meter serial number
	 * @param modemId
	 *            Modem ID
	 * @return no return data (async data request)
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public byte[] cmdSetIEIUConf(String mcuId, String meterId, String modemId, String kind, String[] param)
			throws FMPMcuException, Exception {
		log.debug("cmdSetIEIUConf(" + mcuId + "," + meterId + "," + "," + kind + ")");

		for (int i = 0; param != null && i < param.length; i++) {
			log.debug("Parameters[" + i + "] contents[" + param[i] + "]");
		}

		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		Vector<SMIValue> datas = new Vector<SMIValue>();
		SMIValue smiValue = null;
		Target target = null;
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");

			txStatus = txManager.getTransaction(null);

			Meter meter = meterDao.get(meterId);
			target = CmdUtil.getTarget(meter.getModem());

			smiValue = DataUtil.getSMIValue(new OCTET(kind));
			datas.add(smiValue);

			txManager.commit(txStatus);
		} catch (Exception e) {
			if (txManager != null)
				txManager.rollback(txStatus);
			throw e;
		}

		for (int i = 0; param != null && i < param.length; i++) {
			smiValue = DataUtil.getSMIValue(new OCTET(param[i]));
			datas.add(smiValue);
		}

		Object[] params = new Object[] { target, "cmdSetIEIUConf", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
			if (smiValues.length > 0) {
				smiValue = smiValues[0];
			} else {
				log.error("smiValue size 0");
				throw new Exception("no value exist");
			}
		}

		byte[] ret = ((OCTET) smiValues[0].getVariable()).getValue();

		return ret;

	}

	/**
	 * On-demand Meter
	 * 
	 * @param mcuId
	 *            MCU ID
	 * @param meterId
	 *            Meter Serial Number
	 * @param modemId
	 *            Modem ID
	 * @param nPort
	 * @param nOption
	 * @param fromDate
	 * @param toDate
	 * @return energy meter data
	 * @throws FMPMcuException
	 * @throws Exception
	 */
	@Override
	public MeterData cmdOnDemandMBus(String mcuId, String meterId, String modemId, String nPort, String nOption,
			String fromDate, String toDate) throws FMPMcuException, Exception {
		log.debug("cmdOnDemandMBus(" + mcuId + "," + meterId + "," + modemId + "," + nOption + "," + fromDate + ","
				+ toDate + ")");

		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		Vector<SMIValue> datas = new Vector<SMIValue>();
		SMIValue smiValue = null;
		Target target = null;
		int nOffset = 0;
		int nCount = 0;
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");

			txStatus = txManager.getTransaction(null);

			Meter meter = meterDao.get(meterId);
			target = CmdUtil.getTarget(meter.getModem());

			if (!fromDate.equals("") && !toDate.equals("")) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				Calendar today = Calendar.getInstance();
				Calendar from = Calendar.getInstance();
				Calendar to = Calendar.getInstance();
				from.setTime(sdf.parse(fromDate.substring(0, 8)));
				to.setTime(sdf.parse(toDate.substring(0, 8)));

				if (today.before(to)) {
					throw new Exception("request toDate[" + toDate + "] must not be after today");
				}

				if (!today.after(from)) {
					throw new Exception("request fromDate[" + fromDate + "] must be before today");
				}

				while (today.after(from)) {
					from.add(Calendar.DAY_OF_YEAR, 1);
					nOffset++;
				}

				from.setTime(sdf.parse(fromDate));

				while (today.after(to)) {
					to.add(Calendar.DAY_OF_YEAR, 1);
					nCount++;
				}
				nCount = nOffset - nCount + 1;
			}

			log.debug("nOffset[" + nOffset + "] nCount[" + nCount + "]");
			// SensorId
			smiValue = DataUtil.getSMIValueByObject("sensorID", meter.getModem().getDeviceSerial());
			datas.add(smiValue);
			// nPort
			smiValue = DataUtil.getSMIValue(new INT(Integer.parseInt(nPort)));
			datas.add(smiValue);
			// nOption
			smiValue = DataUtil.getSMIValue(new INT(Integer.parseInt(nOption)));
			datas.add(smiValue);
			txManager.commit(txStatus);

			if (nOffset != 0) {
				// nOffset
				smiValue = DataUtil.getSMIValue(new INT(nOffset));
				datas.add(smiValue);

				if (nCount != 0) {
					// nCount
					smiValue = DataUtil.getSMIValue(new INT(nCount));
					datas.add(smiValue);
				}
			}
		} catch (Exception e) {
			if (txManager != null)
				txManager.rollback(txStatus);
			throw e;
		}

		Object[] params = new Object[] { target, "cmdOnDemandMBus", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
			if (smiValues.length > 0) {
				smiValue = smiValues[0];
			} else {
				log.error("smiValue size 0");
				throw new FMPMcuException("no value exist");
			}
		} else {
			log.error("Unknown Return Value");
			throw new FMPMcuException("Unknown Return Value");
		}

		try {
			txStatus = txManager.getTransaction(null);

			Meter meter = meterDao.get(meterId);
			MeterData ed = new MeterData();
			OPAQUE mdv = (OPAQUE) smiValue.getVariable();

			log.debug("Get Meter : return ClassName[" + mdv.getClsName() + "] MIB[" + mdv.getMIBName() + "]");

			ModemConfig modemConfig = (ModemConfig) meter.getModem().getModel().getDeviceConfig();
			MeterDataParser edp = (MeterDataParser) Class.forName(modemConfig.getParserName()).newInstance();
			edp.setMeter(meter);

			ed.setMeterId(meterId);
			ed.setParser(edp);
			obj = mdv.getValue();
			if (obj instanceof meterDataEntry) {
				meterDataEntry value = (meterDataEntry) obj;
				edp.parse(value.getMdData().getValue());
				ed.setTime(value.getMdTime().toString());
				ed.setType(value.getMdType().toString());
				ed.setVendor(value.getMdVendor().toString());
				ed.setServiceType(value.getMdServiceType().toString());
			} else if (obj instanceof meterLPEntry) {
				meterLPEntry value = (meterLPEntry) obj;
				ByteArrayOutputStream bao = new ByteArrayOutputStream();
				byte[] bx = value.getMlpData().getValue();
				log.debug("bx: " + Hex.decode(bx));
				byte[] timestamp = new byte[7];
				bao.write(bx, 0, timestamp.length);
				timestamp = bao.toByteArray();
				bao.flush();
				bao = new ByteArrayOutputStream();
				bao.write(bx, 7, bx.length - 7);
				log.debug("bao: " + Hex.decode(bao.toByteArray()));
				edp.parse(bao.toByteArray());
				ed.setTime(new TIMESTAMP(timestamp).getValue());
				ed.setType(value.getMlpType().toString());
				ed.setVendor(value.getMlpVendor().toString());
				ed.setServiceType(value.getMlpServiceType().toString());
				bao.flush();
				bao.close();
			}

			MDHistoryData mdHistoryData = new MDHistoryData();
			mdHistoryData.setEntryCount(1);
			mdHistoryData.setMcuId(mcuId);
			mdHistoryData.setMdData(mdv.encode());
			mdsMain.save(mdHistoryData, true);

			txManager.commit(txStatus);

			return ed;
		} catch (Exception e) {
			if (txManager != null)
				txManager.rollback(txStatus);
			throw new Exception(e);
		}

	}

	/**
	 * Request PLC Data Frame
	 * 
	 * @param mcuId
	 * @param pdf
	 * @return
	 * @throws Exception
	 */
	@Override
	public PLCDataFrame requestPLCDataFrame(String mcuId, PLCDataFrame pdf) throws Exception {

		log.debug("Pdf: " + pdf);

		Target target = CmdUtil.getTarget(mcuId);

		PLCDataFrame responsePdf = new PLCDataFrame();
		Object[] params = new Object[] { target, pdf };

		String[] types = new String[] { TARGET_SRC, "nuri.aimir.moa.protocol.fmp.frame.PLCDataFrame", };
		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof PLCDataFrame) {
			responsePdf = (PLCDataFrame) obj;
		} else {
			log.error("Unknown Return Value");
			throw new FMPMcuException("Unknown Return Value");
		}
		return responsePdf;
	}

	/**
	 * On-demand Meter All
	 *
	 * @param mcuId
	 *            MCU ID
	 * @return energy meter data list
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public MeterData[] cmdOnDemandMeterAll(String mcuId) throws FMPMcuException, Exception {
		log.debug("cmdOnDemandMeterAll(" + mcuId + ")");

		Target target = CmdUtil.getTarget(mcuId);
		Vector<SMIValue> datas = new Vector<SMIValue>();

		Object[] params = new Object[] { target, "cmdOnDemandMeterAll", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		log.debug("return value count [" + smiValues.length + "]");

		// EnergyMeterData[] eds = new EnergyMeterData[smiValues.length];
		MeterData ed = null;
		ArrayList<MeterData> eds = new ArrayList<MeterData>();
		log.debug("return value count [" + smiValues.length + "]");
		OPAQUE mdv = null;
		String meterId = null;
		meterDataEntry value = null;
		MeterDataParser edp = null;
		byte[] mdb = null;

		Meter meter = null;
		MeterConfig meterConfig = null;

		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");

			txStatus = txManager.getTransaction(null);

			for (int i = 0; i < smiValues.length; i++) {
				try {
					mdv = (OPAQUE) smiValues[i].getVariable();
					// log.debug("Get Meter : return Class Name = "+className);
					value = (meterDataEntry) mdv.getValue();
					meterId = value.getMdID().toString();
					mdb = value.getMdData().getValue();

					meterConfig = (MeterConfig) meter.getModel().getDeviceConfig();

					if (meterConfig.getOndemandParserName() != null && !"".equals(meterConfig.getOndemandParserName()))
						edp = (MeterDataParser) Class.forName(meterConfig.getOndemandParserName()).newInstance();
					else
						edp = (MeterDataParser) Class.forName(meterConfig.getParserName()).newInstance();
					edp.setMeter(meter);

					log.debug("Meter Data Size [" + mdb.length + "]");
					log.debug(Hex.decode(mdb));
					edp.parse(mdb);

					ed = new MeterData();
					ed.setMeterId(meterId);
					ed.setTime(value.getMdTime().toString());
					ed.setType(value.getMdType().toString());
					ed.setVendor(value.getMdVendor().toString());
					ed.setServiceType(value.getMdServiceType().toString());
					ed.setParser(edp);
					eds.add(ed);
				} catch (Exception e) {
					log.error(e, e);
				}
			}

			txManager.commit(txStatus);
		} catch (Exception e) {
			if (txManager != null)
				txManager.rollback(txStatus);
			throw e;
		}

		return eds.toArray(new MeterData[0]);
	}

	/**
	 * Get Current Meter
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param isMeter
	 *            meter(true) or sensor(false)
	 * @param deviceId
	 *            meter serial number or sensor id
	 * @return energy meter data
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public MeterData cmdGetCurMeter(String mcuId, String deviceId, boolean isMeter) throws FMPMcuException, Exception {
		log.debug("cmdGetCurMeter(" + mcuId + "," + isMeter + "," + deviceId + ")");

		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		Target target = null;
		Vector<SMIValue> datas = new Vector<SMIValue>();
		SMIValue smiValue = null;
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");

			txStatus = txManager.getTransaction(null);

			Modem modem = null;
			if (isMeter) {
				Meter meter = meterDao.get(deviceId);
				modem = meter.getModem();
			} else {
				modem = modemDao.get(deviceId);
			}

			target = CmdUtil.getTarget(modem);
			smiValue = DataUtil.getSMIValueByObject(isMeter ? "sensorSerialNumber" : "sensorID", deviceId);
			datas.add(smiValue);

			txManager.commit(txStatus);
		} catch (Exception e) {
			if (txManager != null)
				txManager.rollback(txStatus);
			throw e;
		}

		Object[] params = new Object[] { target, "cmdGetCurMeter", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
			if (smiValues.length > 0) {
				smiValue = smiValues[0];
			} else {
				log.debug("smiValues size is 0");
				throw new Exception("no value exist");
			}
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		MeterData ed = new MeterData();
		OPAQUE mdv = (OPAQUE) smiValue.getVariable();

		// String className = mdv.getClsName();
		// log.debug("Get Meter : return Class Name = "+className);
		meterDataEntry value = (meterDataEntry) mdv.getValue();
		String meterId = value.getMdID().toString();

		try {
			txStatus = txManager.getTransaction(null);

			Meter meter = meterDao.get(meterId);
			ModemConfig modemConfig = (ModemConfig) meter.getModem().getModel().getDeviceConfig();
			MeterDataParser edp = (MeterDataParser) Class.forName(modemConfig.getParserName()).newInstance();
			edp.setMeter(meter);

			edp.parse(value.getMdData().getValue());

			ed.setMeterId(meterId);
			ed.setTime(value.getMdTime().toString());
			ed.setType(value.getMdType().toString());
			ed.setVendor(value.getMdVendor().toString());
			ed.setServiceType(value.getMdServiceType().toString());
			ed.setParser(edp);

			MDHistoryData mdHistoryData = new MDHistoryData();
			mdHistoryData.setEntryCount(1);
			mdHistoryData.setMcuId(mcuId);
			mdHistoryData.setMdData(mdv.encode());
			mdsMain.save(mdHistoryData, true);
			txManager.commit(txStatus);

			return ed;
		} catch (Exception e) {
			if (txManager != null)
				txManager.rollback(txStatus);
			throw e;
		}

	}

	/**
	 * Get Current Meter All
	 *
	 * @param mcuId
	 *            MCU ID
	 * @return energy meter data list
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public MeterData[] cmdGetCurMeterAll(String mcuId) throws FMPMcuException, Exception {
		log.debug("cmdGetCurMeterAll(" + mcuId + ")");

		Target target = CmdUtil.getTarget(mcuId);
		Vector<SMIValue> datas = new Vector<SMIValue>();

		Object[] params = new Object[] { target, "cmdGetCurMeterAll", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		log.debug("return value count [" + smiValues.length + "]");

		// EnergyMeterData[] eds = new EnergyMeterData[smiValues.length];
		MeterData ed = null;
		ArrayList<MeterData> eds = new ArrayList<MeterData>();
		log.debug("return value count [" + smiValues.length + "]");
		OPAQUE mdv = null;
		String meterId = null;
		meterDataEntry value = null;
		MeterDataParser edp = null;
		byte[] mdb = null;

		Meter meter = null;
		ModemConfig modemConfig = null;

		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");

			txStatus = txManager.getTransaction(null);

			for (int i = 0; i < smiValues.length; i++) {
				try {
					mdv = (OPAQUE) smiValues[i].getVariable();
					// log.debug("Get Meter : return Class Name = "+className);
					value = (meterDataEntry) mdv.getValue();
					meterId = value.getMdID().toString();
					mdb = value.getMdData().getValue();

					meter = meterDao.get(meterId);
					modemConfig = (ModemConfig) meter.getModem().getModel().getDeviceConfig();
					edp = (MeterDataParser) Class.forName(modemConfig.getParserName()).newInstance();
					edp.setMeter(meter);

					log.debug("Meter Data Size [" + mdb.length + "]");
					log.debug(Hex.decode(mdb));
					edp.parse(mdb);

					ed = new MeterData();
					ed.setMeterId(meterId);
					ed.setTime(value.getMdTime().toString());
					ed.setType(value.getMdType().toString());
					ed.setVendor(value.getMdVendor().toString());
					ed.setServiceType(value.getMdServiceType().toString());
					ed.setParser(edp);
					eds.add(ed);
				} catch (Exception e) {
					log.error(e, e);
				}
			}

		} catch (Exception e) {
			log.error(e, e);
		}

		return eds.toArray(new MeterData[0]);
	}

	/**
	 * Report Current Meter (Retry)
	 *
	 * @param mcuId
	 *            MCU ID
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public void cmdReportCurMeter(String mcuId) throws FMPMcuException, Exception {
		log.debug("cmdReportCurMeter(" + mcuId + ")");

		Target target = CmdUtil.getTarget(mcuId);

		Object[] params = new Object[] { target, "cmdReportCurMeter", null, null };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}
	}

	/**
	 * --add Get Meter Time
	 *
	 * @param mcuId
	 * @param modemId
	 *            modem id
	 * @return timeEntry
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public timeEntry cmdGetMeterTime(String mcuId, String modemId) throws FMPMcuException, Exception {
		log.debug("cmdGetMeterTime(" + mcuId + ")");

		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		Vector<SMIValue> datas = new Vector<SMIValue>();
		SMIValue smiValue = null;
		Target target = null;
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");

			txStatus = txManager.getTransaction(null);

			Modem modem = modemDao.get(modemId);
			target = CmdUtil.getTarget(modem);

			smiValue = DataUtil.getSMIValueByObject("sensorId", modemId);
			datas.add(smiValue);

			txManager.commit(txStatus);
		} catch (Exception e) {
			if (txManager != null)
				txManager.rollback(txStatus);
			throw e;
		}

		Object[] params = new Object[] { target, "cmdGetMeterTime", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}
		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		timeEntry value = new timeEntry();
		if (smiValues.length > 0) {
			TIMESTAMP t = (TIMESTAMP) smiValues[0].getVariable();
			byte[] b = t.encode();
			value.setTimeYear(new WORD(t.decode(null, b, 0, 2)));
			value.setTimeMon(new BYTE(t.decode(null, b, 2, 1)));
			value.setTimeDay(new BYTE(t.decode(null, b, 3, 1)));
			value.setTimeHour(new BYTE(t.decode(null, b, 4, 1)));
			value.setTimeMin(new BYTE(t.decode(null, b, 5, 1)));
			value.setTimeSec(new BYTE(t.decode(null, b, 6, 1)));
		}
		return value;
	}

	/**
	 * --add
	 *
	 * Set Meter Time
	 * 
	 * @param mcuId
	 *            MCU Identifier
	 * @param meterId
	 *            Meter Identifier
	 * @param time
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public void cmdSetMeterTime(String mcuId, String meterId, String time) throws FMPMcuException, Exception {
		log.debug("cmdSetMeterTime(" + mcuId + ")");

		SMIValue smiValue = null;
		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		Target target = null;
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");

			txStatus = txManager.getTransaction(null);

			Meter meter = meterDao.get(meterId);
			target = CmdUtil.getTarget(meter.getModem());

			/*
			 * if (modem.getMcu() != null &&
			 * modem.getMcu().getSysSwRevision().compareTo(mcuRevision) >= 0 ) {
			 * smiValue = DataUtil.getSMIValue("gsiSensorID",
			 * modem.getDeviceSerial()); datas.add(smiValue); } else { smiValue
			 * = DataUtil.getSMIValue("sensorID", modem.getDeviceSerial()); }
			 */
			smiValue = DataUtil.getSMIValueByObject("sensorID", meter.getModem().getDeviceSerial());

			txManager.commit(txStatus);
		} catch (Exception e) {
			if (txManager != null)
				txManager.rollback(txStatus);
			throw e;
		}

		Vector<SMIValue> datas = new Vector<SMIValue>();
		datas.add(smiValue);
		smiValue = DataUtil.getSMIValue(new TIMESTAMP(time));
		datas.add(smiValue);

		Object[] params = new Object[] { target, "cmdSetMeterTime", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector" };
		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}

	}

	/**
	 * --add Get Phone List
	 * 
	 * @param mcuId
	 * @return stringEntry
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public String[] cmdGetPhoneList(String mcuId) throws FMPMcuException, Exception {
		log.debug("cmdGetPhoneList(" + mcuId + ")");

		Target target = CmdUtil.getTarget(mcuId);

		Object[] params = new Object[] { target, "cmdGetPhoneList", null, null };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		SMIValue smiValue = null;
		String[] values = new String[smiValues.length];
		for (int i = 0; i < smiValues.length; i++) {
			smiValue = smiValues[i];
			log.debug(smiValue.toString());
			values[i] = smiValue.getVariable().toString();
			log.debug(values[i]);
		}
		return values;
	}

	/**
	 * --add Set Phone List
	 *
	 * @param mcuId
	 * @param phoneNumber
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public void cmdSetPhoneList(String mcuId, String[] phoneNumber) throws FMPMcuException, Exception {

		log.debug("cmdSetPhoneList(" + mcuId + ")");

		Target target = CmdUtil.getTarget(mcuId);
		Vector<SMIValue> datas = new Vector<SMIValue>();

		SMIValue smiValue = null;
		for (int i = 0; i < phoneNumber.length; i++) {
			smiValue = DataUtil.getSMIValueByObject("stringEntry", phoneNumber[i]);
			datas.add(smiValue);
		}
		Object[] params = new Object[] { target, "cmdSetPhoneList", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };
		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}
	}

	/**
	 * -add Meter Upload Range
	 *
	 * @param mcuId
	 * @param startTime
	 * @param endTime
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public void cmdMeterUploadRange(String mcuId, String startTime, String endTime) throws FMPMcuException, Exception {
		log.debug("cmdMeterUploadRange(" + mcuId + ")");

		Target target = CmdUtil.getTarget(mcuId);

		Vector<SMIValue> datas = new Vector<SMIValue>();

		SMIValue smiValue = null;
		smiValue = DataUtil.getSMIValue(new TIMESTAMP(startTime));
		datas.add(smiValue);
		smiValue = DataUtil.getSMIValue(new TIMESTAMP(endTime));
		datas.add(smiValue);

		Object[] params = new Object[] { target, "cmdMeterUploadRange", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };
		Object obj = null;

		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

	}

	/**
	 * Install File
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param filename
	 *            filename
	 * @param type
	 *            install type
	 * @param reservationTime
	 *            reservation time
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public void cmdInstallFile(String mcuId, String filename) throws FMPMcuException, Exception {
		log.debug("cmdInstallFile(" + mcuId + ")");
		cmdInstallFile(mcuId, filename, 0, null);
	}

	/**
	 * notification firmware update
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param filename
	 *            filename
	 * @param type
	 *            install type
	 * @param reservationTime
	 *            reservation time
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public void cmdFirmwareUpdate(String mcuId) throws FMPMcuException, Exception {
		log.debug("cmdFirmwareUpdate(" + mcuId + ")");

		Target target = CmdUtil.getTarget(mcuId);
		String serverIp = FMPProperty.getProperty("firmware.localserver.id");

		if (serverIp.indexOf(":") != -1) {
			serverIp = serverIp.substring(0, serverIp.indexOf(":"));
		}

		log.debug("server ip[" + serverIp + "]");

		Object obj = null;
		try {
			obj = invokeNotifyFirmwareUpdate(target, serverIp);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception("Communication error between Server and MCU");
		}

		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}
	}

	/**
	 * Install File
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param filename
	 *            filename
	 * @param type
	 *            install type
	 * @param reservationTime
	 *            reservation time
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public void cmdInstallFile(String mcuId, String filename, int type, String reservationTime)
			throws FMPMcuException, Exception {
		log.debug((new StringBuilder()).append("cmdInstallFile(").append(mcuId).append(")").toString());
		Target target = CmdUtil.getTarget(mcuId);
		if (type == 0) {
			reservationTime = "00000000000000";
		}

		Object obj = null;
		try {
			obj = invokeFirmwareUpdate(target, filename, new Integer(type), reservationTime);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}
		SMIValue smiValues[] = null;
		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}
		if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}
	}

	/**
	 * Put File
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param filename
	 *            filename
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public void cmdPutFile(String mcuId, String filename) throws FMPMcuException, Exception {
		log.debug("cmdPutFile(" + mcuId + "," + filename + ")");

		Target target = CmdUtil.getTarget(mcuId);

		Object obj = null;
		try {
			obj = invokePutFile(target, filename);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}
	}

	/**
	 * Get File
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param filename
	 *            filename
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public void cmdGetFile(String mcuId, String filename) throws FMPMcuException, Exception {
		log.debug("cmdGetFile(" + mcuId + "," + filename + ")");

		Target target = CmdUtil.getTarget(mcuId);

		Object obj = null;
		try {
			obj = invokeGetFile(target, filename);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}
	}

	/**
	 * MCU Standard Get
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param mop
	 *            MOPROPERTY
	 * @return moproperty
	 * @throws FMPMcuException,
	 *             Exception
	 */

	@Override
	public Hashtable cmdStdGet(String mcuId, String propName) throws FMPMcuException, Exception {
		log.debug("cmdStdGet(" + mcuId + ")");

		Target target = CmdUtil.getTarget(mcuId);
		Vector<String> mibPropNames = new Vector<String>();

		if (propName != null && !"".equals(propName))
			mibPropNames.add(CmdUtil.getMIBPropertyName(propName));

		if (log.isDebugEnabled()) {
			for (int i = 0; i < mibPropNames.size(); i++) {
				log.debug("cmdStdGet param[" + mibPropNames.get(i) + "]");
			}
		}
		Object[] params = new Object[] { target, "cmdStdGet", mibPropNames, null };

		String[] types = new String[] { Target.class.getName(), "java.lang.String", "java.util.Vector",
				"java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		Hashtable res = null;
		if (smiValues != null && smiValues.length != 0) {
			res = CmdUtil.convSMItoMOP(smiValues[0]);
			log.debug(smiValues[0].toString());
		} else {
			log.debug("smiValues is null");
		}

		return res;
	}
	
	/**
	 * MCU Standard Get (Many Properties)
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param mop
	 *            MOPROPERTYs
	 * @return moproperties
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public Hashtable cmdStdGet(String mcuId, String[] propNames) throws FMPMcuException, Exception {
		log.debug("cmdStdGet(" + mcuId + ")");

		Target target = CmdUtil.getTarget(mcuId);
		Vector<String> mibPropNames = new Vector<String>();

		log.debug("property length=" + propNames.length);

		for (String propName : propNames) {
			if (propName != null && !"".equals(propName)) {
				log.debug("mib property name=" + propName);
				mibPropNames.add(CmdUtil.getMIBPropertyName(propName));
			}
		}

		Object[] params = new Object[] { target, "cmdStdGet", mibPropNames, null };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", "java.util.Vector" };

		Object obj = null;
		try {

			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		Hashtable res = new Hashtable();
		if (smiValues != null && smiValues.length != 0) {
			for (int i = 0; i < smiValues.length; i++) {
				res.putAll(CmdUtil.convMCUSMItoMOP(smiValues[i]));
			}
		} else {
			log.debug("smiValues is null");
		}

		return res;
	}

	/**
	 * MCU Entry Standard Get
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param index
	 *            Index
	 * @param propNames
	 *            properties
	 * @return moproperties
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public Hashtable cmdEntryStdGet(String mcuId, String index, String[] propNames) throws FMPMcuException, Exception {
		log.debug("cmdStdGet(" + mcuId + ")");

		Target target = CmdUtil.getTarget(mcuId);
		Vector<String> mibPropNames = new Vector<String>();

		log.debug("property length=" + propNames.length);

		mibPropNames.add(CmdUtil.getMIBPropertyName(index));
		log.debug("index name=" + CmdUtil.getMIBPropertyName(index));

		for (String propName : propNames) {
			mibPropNames.add(CmdUtil.getMIBPropertyName(propName));
			log.debug("mib property name=" + CmdUtil.getMIBPropertyName(propName));
		}

		Object[] params = new Object[] { target, "cmdStdGet", mibPropNames };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		Hashtable res = new Hashtable();
		if (smiValues != null && smiValues.length != 0) {
			for (int i = 0; i < smiValues.length; i++) {
				res.putAll(CmdUtil.convSMItoMOP(smiValues[i]));
				log.debug(smiValues[i].toString());
			}
		} else {
			log.debug("smiValues is null");
		}

		return res;
	}

	/**
	 * MCU Standard Get Child
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param oid
	 *            OID
	 * @return moproperties
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public Hashtable cmdStdGetChild(String mcuId, String oid) throws FMPMcuException, Exception {
		log.debug("cmdStdGet(" + mcuId + "," + oid + ")");

		Target target = CmdUtil.getTarget(mcuId);
		Vector<SMIValue> datas = new Vector<SMIValue>();

		MIBUtil mibUtil = MIBUtil.getInstance();

		datas.add(new SMIValue(mibUtil.getMIBNodeByOid(oid).getOid(), null));

		Object[] params = new Object[] { target, "cmdStdGetChild", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		Hashtable res = new Hashtable();
		if (smiValues != null && smiValues.length != 0) {
			for (int i = 0; i < smiValues.length; i++) {
				res.putAll(CmdUtil.convSMItoMOP(smiValues[i]));
				log.debug(smiValues[i].toString());
			}
		} else {
			log.debug("smiValues is null");
		}

		return res;
	}

	/**
	 * MCU Standard Set
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param mop
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public void cmdStdSet(String mcuId, String propName, String propValue) throws FMPMcuException, Exception {
		log.debug("cmdStdSet(" + mcuId + ")");

		Target target = CmdUtil.getTarget(mcuId);
		Vector<String> mibPropNames = new Vector<String>();
		Vector<String> mibPropValues = new Vector<String>();

		String mopName = CmdUtil.getMIBPropertyName(propName);
		if (mopName != null) {
			mibPropNames.add(mopName);
			mibPropValues.add(CmdUtil.getValueString(propValue));
			log.debug("mib property name=" + CmdUtil.getMIBPropertyName(propName) + ",value="
					+ CmdUtil.getValueString(propValue));
		} else {
			log.debug("mo property name[" + propName + "] is not exist in MIB");
		}

		if (mibPropNames.size() == 0) {
			log.debug("no exist property that will be setted in MIB");
			return;
		}

		Object[] params = new Object[] { target, "cmdStdSet", mibPropNames, mibPropValues };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}
	}

	/**
	 * MCU Standard Set (Many Properties)
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param mop
	 *            MOPROPERTYs
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public void cmdStdSet(String mcuId, String[] propNames, String[] propValues) throws FMPMcuException, Exception {
		log.debug("cmdStdSet(" + mcuId + ")");

		Target target = CmdUtil.getTarget(mcuId);
		Vector<String> mibPropNames = new Vector<String>();
		Vector<String> mibPropValues = new Vector<String>();
		for (int i = 0; i < propNames.length; i++) {
			String mopName = CmdUtil.getMIBPropertyName(propNames[i]);
			if (mopName != null) {
				mibPropNames.add(mopName);
				mibPropValues.add(CmdUtil.getValueString(propValues[i]));
				log.debug("mib property name=" + CmdUtil.getMIBPropertyName(propNames[i]) + ",value="
						+ CmdUtil.getValueString(propValues[i]));
			} else {
				log.debug("mo property name[" + propNames[i] + "] is not exist in MIB");
			}

			// mibPropNames.add(CmdUtil.getMIBPropertyName(mop[i]));
			// mibPropValues.add(CmdUtil.getValueString(mop[i]));
			// log.debug("mib property
			// name="+CmdUtil.getMIBPropertyName(mop[i]));
			// log.debug("mib property value="+CmdUtil.getValueString(mop[i]));
		}

		if (mibPropNames.size() == 0) {
			log.debug("no exist property that will be setted in MIB");
			return;
		}

		Object[] params = new Object[] { target, "cmdStdSet", mibPropNames, mibPropValues };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}
	}

	/**
	 * MCU Entry Standard Set
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param index
	 *            Index
	 * @param mop
	 *            MOPROPERTY[]
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public void cmdEntryStdSet(String mcuId, String index, String[] propNames, String[] propValues)
			throws FMPMcuException, Exception {
		log.debug("cmdStdSet(" + mcuId + ")");

		Target target = CmdUtil.getTarget(mcuId);
		Vector<String> mibPropNames = new Vector<String>();
		Vector<String> mibPropValues = new Vector<String>();

		mibPropNames.add(CmdUtil.getMIBPropertyName(index));
		mibPropValues.add(CmdUtil.getValueString(index));

		for (int i = 0; i < propNames.length; i++) {
			String mopName = CmdUtil.getMIBPropertyName(propNames[i]);
			if (mopName != null) {
				mibPropNames.add(mopName);
				mibPropValues.add(CmdUtil.getValueString(propValues[i]));
				log.debug("mib property name=" + CmdUtil.getMIBPropertyName(propNames[i]) + ",value="
						+ CmdUtil.getValueString(propValues[i]));
			} else {
				log.debug("mo property name[" + propNames[i] + "] is not exist in MIB");
			}

			// mibPropNames.add(CmdUtil.getMIBPropertyName(mop[i]));
			// mibPropValues.add(CmdUtil.getValueString(mop[i]));
			// log.debug("mib property
			// name="+CmdUtil.getMIBPropertyName(mop[i]));
			// log.debug("mib property value="+CmdUtil.getValueString(mop[i]));
		}

		if (mibPropNames.size() == 0) {
			log.debug("no exist property that will be setted in MIB");
			return;
		}

		Object[] params = new Object[] { target, "cmdStdSet", mibPropNames, mibPropValues };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}
	}

	/**
	 * --added 2007.05.22 Write Table Meter Time Sync to MCU
	 *
	 * @param mcuId
	 *            MCU Identifier
	 * @param meterId
	 *            meter Identifier
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public byte[] cmdMeterTimeSync(String mcuId, String meterId) throws FMPMcuException, Exception {

		int tablenum = 7;
		log.debug("cmdMeterTimeSync(" + mcuId + "," + meterId + "," + tablenum + ")");

		Target target = null;
		Vector<SMIValue> datas = new Vector<SMIValue>();

		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		SMIValue smiValue = null;
		Meter meter = null;
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");

			txStatus = txManager.getTransaction(null);

			meter = meterDao.get(meterId);
			Modem modem = meter.getModem();

			target = CmdUtil.getTarget(modem);
			log.debug("targetInfo =>" + target.getIpAddr() + ":" + target.getPhoneNumber());
			/*
			 * if (modem.getMcu() != null &&
			 * modem.getMcu().getSysSwRevision().compareTo(mcuRevision) >= 0 ) {
			 * smiValue = DataUtil.getSMIValue("gsiSensorID",
			 * modem.getDeviceSerial()); datas.add(smiValue); } else { smiValue
			 * = DataUtil.getSMIValue("sensorID", modem.getDeviceSerial()); }
			 */
			if (modem.getProtocolType() == Protocol.SMS) {
				smiValue = DataUtil.getSMIValueByObject("stringEntry", RequestFrame.CMD_METERTIMESYNC);
				datas.add(smiValue);
			}
			smiValue = DataUtil.getSMIValueByObject("sensorID", modem.getDeviceSerial());
			datas.add(smiValue);

			txManager.commit(txStatus);
		} catch (Exception e) {
			if (txManager != null)
				txManager.rollback(txStatus);
			throw e;
		}

		Object[] params = new Object[] { target, "cmdSyncMeterTime", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		byte[] bx = null;
		if (smiValues != null && smiValues.length != 0) {
			log.debug(smiValues[0].toString());
			FMPVariable var = smiValues[0].getVariable();

			if (var instanceof OPAQUE) {
				obj = ((OPAQUE) var).getValue();
				if (obj instanceof meterDataEntry) {
					meterDataEntry value = (meterDataEntry) obj;

					// meter id
					OCTET ocMeterId = new OCTET(20);
					ocMeterId.setIsFixed(true);
					String meterSerial = meter.getMdsId() == null ? "" : meter.getMdsId();
					byte[] bMeterid = DataUtil.fillCopy(meterSerial.getBytes(), (byte) 0x20, 20);
					ocMeterId.setValue(bMeterid);
					value.setMdSerial(ocMeterId);

					bx = value.getMdData().getValue();
				} else if (obj instanceof meterLPEntry) {
					meterLPEntry value = (meterLPEntry) obj;
					bx = value.getMlpData().getValue();
				}
			} else if (var instanceof OCTET) {
				bx = ((OCTET) var).getValue();
			}
		} else {
			log.debug("smiValues is null");
		}

		return bx;
		/*
		 * String[] ret = null; if(bx != null){
		 * 
		 * String beforeTime = null; String afterTime = null; // TODO 임시로 처리한다.
		 * // 시간동기화 결과를 처리할 수 있는 파서같은 것이 있어야 한다. if (modelCode ==
		 * CommonConstants.MeterModel.GE_SM110.getCode()) { beforeTime =
		 * CmdUtil.getYymmddhhmmss(bx,51,6); afterTime =
		 * CmdUtil.getYymmddhhmmss(bx,66,6); } else { beforeTime =
		 * CmdUtil.getYymmddhhmmss(bx,6,6); afterTime =
		 * CmdUtil.getYymmddhhmmss(bx,21,6); } long duration =
		 * (TimeUtil.getLongTime(afterTime) -
		 * TimeUtil.getLongTime(beforeTime))/1000;
		 * 
		 * int hour = (int)duration / 3600; int min = (int)duration % 3600 / 60;
		 * int sec = (int)duration % 3600 % 60;
		 * 
		 * ret = new String[3]; ret[0] = beforeTime; ret[1] = afterTime; ret[2]
		 * = duration+"";
		 * 
		 * ret = new String[]{new String(bx)}; } return ret;
		 */
	}

	/**
	 * --added 2013.11.19 Write Table Meter Time Sync to MCU (GType Meter)
	 *
	 * @param mcuId
	 *            MCU Identifier
	 * @param meterId
	 *            meter Identifier
	 * @throws FMPMcuException,
	 *             Exception
	 */
	public Map<String, String> cmdMeterTimeSyncByGtype(String mcuId, String meterId) throws FMPMcuException, Exception {
		Map<String, String> map = null;

		Map resultMap = cmdOnDemandMeter(mcuId, meterId, OnDemandOption.WRITE_OPTION_TIMESYNC.getCode());
		log.debug("Command resultMap : " + resultMap);
		if (resultMap != null) {
			map = new HashMap<String, String>();
			map.put("beforeTime", (String) resultMap.get("beforeTime"));
			map.put("afterTime", (String) resultMap.get("afterTime"));
		}

		return map;
	}

	/**
	 * 검침 및 제어 기능
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param meterId
	 *            Meter ID
	 * @param modemId
	 *            Modem Id
	 * @return energy meter data
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public Object cmdGetMeterSchedule(String mcuId, String modemId, int nOption, String fromDate, String toDate)
			throws FMPMcuException, Exception {
		Modem modem = null;
		Meter meter = null;
		DeviceModel model = null;
		ModemType modemType = null;
		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");

			txStatus = txManager.getTransaction(null);

			modem = modemDao.get(modemId);
			if (modem.getMeter() != null && modem.getMeter().size() == 1)
				meter = modem.getMeter().toArray(new Meter[0])[0];

			if (meter == null)
				return null;

			model = meter.getModel();
			modemType = modem.getModemType();

			txManager.commit(txStatus);
		} catch (Exception e) {
			log.error(e, e);
		}

		if (modem.getNameSpace() != null && !"".equals(modem.getNameSpace())) {
			if ("NG".equals(modem.getNameSpace()))
				return cmdOnDemandByMeter(mcuId, meter.getMdsId(), modemId, nOption + "", fromDate, toDate);
			else {
				int seq = new Random().nextInt(100) & 0xFF;
				return cmdSendSMS(modemId, RequestFrame.CMD_ONDEMAND, String.valueOf(seq), RequestFrame.BG, fromDate,
						toDate);
			}
		}

		log.debug("meterModel=" + model.getId());
		// log.debug("meterVendor="+model.getDeviceVendor().getName());
		int lpInterval = meter.getLpInterval();

		int[] offsetCount = CmdUtil.convertOffsetCount(model, lpInterval, modemType, fromDate, toDate);

		return cmdGetMeterSchedule(mcuId, modemId, nOption, offsetCount[0], offsetCount[1]);
	}

	/**
	 * --added 2007.06.05
	 *
	 * @param mcuId
	 *            MCU Identifier
	 * @param modemId
	 *            modem Identifier
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public Object cmdGetMeterSchedule(String mcuId, String modemId, int nOption, int nOffset, int nCount)
			throws FMPMcuException, Exception {
		log.debug("cmdGetMeterSchedule(" + mcuId + "," + modemId + "," + nOption + "," + nOffset + "," + nCount + ")");

		Target target = null;
		Vector<SMIValue> datas = new Vector<SMIValue>();

		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		SMIValue smiValue = null;
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");

			txStatus = txManager.getTransaction(null);

			Modem modem = modemDao.get(modemId);
			target = CmdUtil.getTarget(modem);

			smiValue = DataUtil.getSMIValueByObject("sensorID", "" + modemId);
			datas.add(smiValue);
			smiValue = DataUtil.getSMIValue(new INT(nOption));
			datas.add(smiValue);
			smiValue = DataUtil.getSMIValue(new INT(nOffset));
			datas.add(smiValue);
			smiValue = DataUtil.getSMIValue(new INT(nCount));
			datas.add(smiValue);

			txManager.commit(txStatus);
		} catch (Exception e) {
			if (txManager != null)
				txManager.commit(txStatus);
			throw e;
		}

		Object[] params = new Object[] { target, "cmdGetMeterSchedule", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception("Communication error between Server and MCU[" + mcuId + "]");
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}

		return obj;
	}

	/**
	 * --added 2007.05.26 Get mcu configuration file(OID 100.26)
	 *
	 * @param mcuId
	 *            MCU Identifier
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public String[] cmdGetConfiguration(String mcuId) throws FMPMcuException, Exception {

		log.debug("cmdGetConfiguration(" + mcuId + ")");

		Target target = CmdUtil.getTarget(mcuId);

		Object[] params = new Object[] { target, "cmdGetConfiguration", null };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		SMIValue smiValue = null;
		String[] values = new String[smiValues.length];
		for (int i = 0; i < smiValues.length; i++) {
			smiValue = smiValues[i];
			log.debug(smiValue.toString());
			values[i] = smiValue.getVariable().toString();
			log.debug(values[i]);
		}
		return values;

	}

	/**
	 * --added 2007.05.26 mcu configuration file setting(OID 100.27)
	 *
	 * @param mcuId
	 *            MCU Identifier
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public void cmdSetConfiguration(String mcuId) throws FMPMcuException, Exception {

		log.debug("cmdSetConfiguration(" + mcuId + ")");

		Target target = CmdUtil.getTarget(mcuId);

		Object[] params = new Object[] { target, "cmdSetConfiguration", null };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			if (e.getMessage().indexOf("IF4ERR_RETURN_DATA_EMPTY") < 0 && e.getMessage().indexOf("Read Time out") < 0) {
				throw new Exception(makeMessage(e.getMessage()));
			}
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			if (((Integer) obj).intValue() != ErrorCode.IF4ERR_RETURN_DATA_EMPTY) {
				throw makeMcuException(((Integer) obj).intValue());
			}

		}
	}

	/**
	 * --added 2007.09.06 Sensor ROM Read
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param address
	 *            rom address
	 * @param length
	 * @return sensor rom information
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public ModemROM cmdGetModemROM(String mcuId, String modemId, int[][] args) throws FMPMcuException, Exception {
		log.debug("MCUID[" + mcuId + "] MODEM[" + modemId + "] ARGS.LENGTH[" + args.length + "] ARGS[" + args + "]");

		Target target = null;
		String str = null;
		Vector<SMIValue> datas = new Vector<SMIValue>();

		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");

			txStatus = txManager.getTransaction(null);

			Modem modem = modemDao.get(modemId);
			target = CmdUtil.getTarget(modem);

			str = target.getFwRevision();

			txManager.commit(txStatus);
		} catch (Exception e) {
			if (txManager != null)
				txManager.rollback(txStatus);
			throw e;
		}

		Object[] params = new Object[] { target, "cmdGetSensorROM", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;

		List<Object> result = new ArrayList<Object>();
		datas.add(DataUtil.getSMIValueByObject("sensorID", "" + modemId));

		if (str != null && str.compareTo("2143") >= 0) {
			for (int i = 0; i < args.length; i++) {
				datas.add(DataUtil.getSMIValue(new WORD(args[i][0])));
				datas.add(DataUtil.getSMIValue(new WORD(args[i][1])));
			}

			try {
				obj = invoke(params, types);
				result.add(obj);
			} catch (Exception e) {
				log.error(e, e);
				throw new Exception(makeMessage(e.getMessage()));
			}
		} else {
			for (int i = 0; i < args.length; i++) {
				datas.add(DataUtil.getSMIValue(new WORD(args[i][0])));
				datas.add(DataUtil.getSMIValue(new WORD(args[i][1])));

				try {
					obj = invoke(params, types);
					result.add(obj);
				} catch (Exception e) {
					log.error(e, e);
					throw new Exception(makeMessage(e.getMessage()));
				}

				datas.remove(2);
				datas.remove(1);
			}
		}

		Modem modem = modemDao.get(modemId);
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
		 * switch (modemType) { case ZRU : modemROM = new ZRUROM(fwVersion,
		 * fwBuild); break; case ZEUPLS : modemROM = new ZEUPLSROM(fwVersion,
		 * fwBuild); break; case ZMU : modemROM = new ZMUROM(fwVersion,
		 * fwBuild); break; case ZBRepeater : modemROM = new
		 * ZEUPLSROM(fwVersion, fwBuild); break; case ZEUMBus : modemROM = new
		 * ZEUMBUSROM(fwVersion, fwBuild); }
		 */

		SMIValue[] smiValues = null;
		int argsIdx = 0;
		byte[] bx = null;
		for (int i = 0; i < result.size(); i++, argsIdx++) {
			obj = result.get(i);
			if (obj instanceof Integer) {
				log.error("Error Code Return");
				throw makeMcuException(((Integer) obj).intValue());
			} else if (obj instanceof SMIValue[]) {
				smiValues = (SMIValue[]) obj;

				if (smiValues != null) {
					if (str != null && str.compareTo("2143") >= 0) {
						for (int j = 0; j < smiValues.length; j++, argsIdx++) {
							if (smiValues[j].getVariable() != null) {
								log.debug(smiValues[j].toString());
								bx = ((OCTET) smiValues[j].getVariable()).getValue();

								modemROM.parse(args[argsIdx][0], bx);
							}
						}
					} else {
						if (smiValues[0].getVariable() != null) {
							log.debug(smiValues[0].toString());
							bx = ((OCTET) smiValues[0].getVariable()).getValue();

							modemROM.parse(args[argsIdx][0], bx);
						}
					}
				}
			} else {
				log.error("Unknown Return Value");
				throw new Exception("Unknown Return Value");
			}
		}
		return modemROM;
	}

	/**
	 * --added 2007.09.06 Modem ROM Read
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param modemId
	 *            Modem Id
	 * @param address
	 *            rom address
	 * @param length
	 * @return sensor rom information
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public byte[] cmdGetModemROM(String mcuId, String modemId, int address, int length)
			throws FMPMcuException, Exception {
		log.debug("MCUID[" + mcuId + "] MODEM[" + modemId + "] ADDRESS[" + address + "] LENGTH[" + length + "]");

		Target target = null;
		Vector<SMIValue> datas = new Vector<SMIValue>();

		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");

			txStatus = txManager.getTransaction(null);

			Modem modem = modemDao.get(modemId);
			target = CmdUtil.getTarget(modem);

			SMIValue smiValue = DataUtil.getSMIValueByObject("sensorID", "" + modemId);
			datas.add(smiValue);
			smiValue = DataUtil.getSMIValue(new WORD(address));
			datas.add(smiValue);
			smiValue = DataUtil.getSMIValue(new WORD(length));
			datas.add(smiValue);

			txManager.commit(txStatus);
		} catch (Exception e) {
			if (txManager != null)
				txManager.rollback(txStatus);
			throw e;
		}

		Object[] params = new Object[] { target, "cmdGetSensorROM", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;
		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		byte[] bx = null;
		if (smiValues != null && smiValues.length != 0) {
			log.debug(smiValues[0].toString());
			bx = ((OCTET) smiValues[0].getVariable()).getValue();

			return bx;
		} else {
			log.debug("smiValues is null");
			return null;
		}
	}

	/**
	 * --added 2007.09.06 Modem ROM write
	 *
	 * @param mcuId
	 *            MCU Identifier
	 * @param modemId
	 *            Modem Identifier
	 * @param address
	 *            rom address
	 * @param data
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public void cmdSetModemROM(String mcuId, String modemId, int address, byte[] data)
			throws FMPMcuException, Exception {
		log.debug(
				"MCUID[" + mcuId + "] MODEM[" + modemId + "] ADDRESS[" + address + "] DATA[" + new String(data) + "]");

		Target target = null;
		byte[] bx = data;
		Vector<SMIValue> datas = new Vector<SMIValue>();

		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");

			txStatus = txManager.getTransaction(null);

			Modem modem = modemDao.get(modemId);
			target = CmdUtil.getTarget(modem);

			SMIValue smiValue = DataUtil.getSMIValueByObject("sensorID", "" + modemId);
			datas.add(smiValue);
			smiValue = DataUtil.getSMIValue(new WORD(address));
			datas.add(smiValue);
			smiValue = DataUtil.getSMIValue(new OCTET(bx));
			datas.add(smiValue);

			txManager.commit(txStatus);
		} catch (Exception e) {
			if (txManager != null)
				txManager.rollback(txStatus);
			throw e;
		}

		Object[] params = new Object[] { target, "cmdSetSensorROM", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;
		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}
	}

	/**
	 * --added 2007.10.02 Get Codi List
	 *
	 * @param mcuId
	 *            MCU ID
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public codiEntry[] cmdGetCodiList(String mcuId) throws FMPMcuException, Exception {
		log.debug("cmdGetCodiList(" + mcuId + ")");

		Target target = CmdUtil.getTarget(mcuId);

		Object[] params = new Object[] { target, "cmdGetCodiList", null, null };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		SMIValue smiValue = null;
		OPAQUE mdv = null;
		codiEntry[] values = new codiEntry[smiValues.length];
		for (int i = 0; i < smiValues.length; i++) {
			smiValue = smiValues[i];
			log.debug(smiValue.toString());
			mdv = (OPAQUE) smiValue.getVariable();
			log.debug(mdv);
			values[i] = (codiEntry) mdv.getValue();
			log.debug(values[i]);
		}
		return values;
	}

	/**
	 * --added 2007.10.02 Get Codi Info
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param codiIndex
	 *            codi ID
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public codiEntry cmdGetCodiInfo(String mcuId, int codiIndex) throws FMPMcuException, Exception {
		log.debug("cmdGetCodiInfo(" + mcuId + "),(" + codiIndex + ")");

		Target target = CmdUtil.getTarget(mcuId);
		Vector<SMIValue> datas = new Vector<SMIValue>();
		SMIValue smiValue = DataUtil.getSMIValueByObject("codiIndex", "" + codiIndex);
		datas.add(smiValue);

		Object[] params = new Object[] { target, "cmdGetCodiInfo", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		if (smiValues.length > 0) {
			smiValue = smiValues[0];
			OPAQUE mdv = (OPAQUE) smiValue.getVariable();
			codiEntry value = (codiEntry) mdv.getValue();
			log.debug(value);

			return value;
		} else {
			log.debug("smiValues size is 0");
			return null;
		}
	}

	/**
	 * --added 2007.10.02 Get Codi Info
	 *
	 * @param mcuId
	 *            MCU ID
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public codiEntry cmdGetCodiInfo(String mcuId) throws FMPMcuException, Exception {
		return cmdGetCodiInfo(mcuId, 0);
	}

	/**
	 * --added 2007.10.02 Get Codi Device
	 *
	 * @param mcuId
	 *            MCU ID
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public codiDeviceEntry cmdGetCodiDevice(String mcuId) throws FMPMcuException, Exception {
		return cmdGetCodiDevice(mcuId, 0);
	}

	/**
	 * --added 2007.10.02 Get Codi Device
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param codiIndex
	 *            codi ID
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public codiDeviceEntry cmdGetCodiDevice(String mcuId, int codiIndex) throws FMPMcuException, Exception {
		log.debug("cmdGetCodiDevice(" + mcuId + "),(" + codiIndex + ")");

		Target target = CmdUtil.getTarget(mcuId);
		Vector<SMIValue> datas = new Vector<SMIValue>();
		SMIValue smiValue = DataUtil.getSMIValue(new BYTE(codiIndex));
		datas.add(smiValue);

		Object[] params = new Object[] { target, "cmdGetCodiDevice", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		if (smiValues.length > 0) {
			smiValue = smiValues[0];
			OPAQUE mdv = (OPAQUE) smiValue.getVariable();
			codiDeviceEntry value = (codiDeviceEntry) mdv.getValue();
			log.debug(value);

			return value;
		} else {
			log.debug("smiValues size is 0");
			return null;
		}
	}

	/**
	 * --added 2007.10.02 Get Codi Device
	 *
	 * @param mcuId
	 *            MCU ID
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public codiBindingEntry cmdGetCodiBinding(String mcuId) throws FMPMcuException, Exception {
		return cmdGetCodiBinding(mcuId, 0);
	}

	/**
	 * --added 2007.10.02 Get Codi Binding
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param codiIndex
	 *            codi ID
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public codiBindingEntry cmdGetCodiBinding(String mcuId, int codiIndex) throws FMPMcuException, Exception {
		log.debug("cmdGetCodiBinding(" + mcuId + "),(" + codiIndex + ")");

		Target target = CmdUtil.getTarget(mcuId);
		Vector<SMIValue> datas = new Vector<SMIValue>();
		SMIValue smiValue = DataUtil.getSMIValue(new BYTE(codiIndex));
		datas.add(smiValue);

		Object[] params = new Object[] { target, "cmdGetCodiBinding", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		if (smiValues.length > 0) {
			smiValue = smiValues[0];
			OPAQUE mdv = (OPAQUE) smiValue.getVariable();
			codiBindingEntry value = (codiBindingEntry) mdv.getValue();
			log.debug(value);

			return value;
		} else {
			log.debug("smiValues size is 0");
			return null;
		}
	}

	/**
	 * --added 2007.10.02 Get Codi Neighbor node
	 *
	 * @param mcuId
	 *            MCU ID
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public codiNeighborEntry cmdGetCodiNeighbor(String mcuId) throws FMPMcuException, Exception {
		return cmdGetCodiNeighbor(mcuId, 0);
	}

	/**
	 * --added 2007.10.02 Get Codi Neighbor node
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param codiIndex
	 *            codi ID
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public codiNeighborEntry cmdGetCodiNeighbor(String mcuId, int codiIndex) throws FMPMcuException, Exception {
		log.debug("cmdGetCodiNeighbor(" + mcuId + "),(" + codiIndex + ")");

		Target target = CmdUtil.getTarget(mcuId);
		Vector<SMIValue> datas = new Vector<SMIValue>();
		SMIValue smiValue = DataUtil.getSMIValue(new BYTE(codiIndex));
		datas.add(smiValue);

		Object[] params = new Object[] { target, "cmdGetCodiNeighbor", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		if (smiValues.length > 0) {
			smiValue = smiValues[0];
			OPAQUE mdv = (OPAQUE) smiValue.getVariable();
			codiNeighborEntry value = (codiNeighborEntry) mdv.getValue();
			log.debug(value);

			return value;
		} else {
			log.debug("smiValues size is 0");
			return null;
		}
	}

	/**
	 * --added 2007.10.02 Get Codi Memory
	 *
	 * @param mcuId
	 *            MCU ID
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public codiMemoryEntry cmdGetCodiMemory(String mcuId) throws FMPMcuException, Exception {
		return cmdGetCodiMemory(mcuId, 0);
	}

	/**
	 * --added 2007.10.02 Get Codi Memory
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param codiIndex
	 *            codi ID
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public codiMemoryEntry cmdGetCodiMemory(String mcuId, int codiIndex) throws FMPMcuException, Exception {
		log.debug("cmdGetCodiMemory(" + mcuId + "),(" + codiIndex + ")");

		Target target = CmdUtil.getTarget(mcuId);
		Vector<SMIValue> datas = new Vector<SMIValue>();
		SMIValue smiValue = DataUtil.getSMIValue(new BYTE(codiIndex));
		datas.add(smiValue);

		Object[] params = new Object[] { target, "cmdGetCodiMemory", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		if (smiValues.length > 0) {
			smiValue = smiValues[0];
			OPAQUE mdv = (OPAQUE) smiValue.getVariable();
			codiMemoryEntry value = (codiMemoryEntry) mdv.getValue();
			log.debug(value);

			return value;
		} else {
			log.debug("smiValues size is 0");
			return null;
		}
	}

	/**
	 * --added 2007.10.02 Get Codi Permit
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param codiIndex
	 *            codi ID
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public void cmdSetCodiPermit(String mcuId, int codiIndex, int codiPermit) throws FMPMcuException, Exception {
		log.debug("cmdSetCodiPermit(" + mcuId + "),(" + codiIndex + "),(" + codiPermit + ")");

		Target target = CmdUtil.getTarget(mcuId);
		Vector<SMIValue> datas = new Vector<SMIValue>();
		SMIValue smiValue = DataUtil.getSMIValue(new BYTE(codiIndex));
		datas.add(smiValue);
		smiValue = DataUtil.getSMIValue(new BYTE(codiPermit));
		datas.add(smiValue);

		Object[] params = new Object[] { target, "cmdSetCodiPermit", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}
	}

	/**
	 * --added 2007.10.02 Get Codi FormNetwork
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param codiIndex
	 *            codi ID
	 * @param value
	 *            set value
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public void cmdSetCodiFormNetwork(String mcuId, int codiIndex, int value) throws FMPMcuException, Exception {
		log.debug("cmdSetCodiFormNetwork(" + mcuId + "),(" + codiIndex + "),(" + value + ")");

		Target target = CmdUtil.getTarget(mcuId);
		Vector<SMIValue> datas = new Vector<SMIValue>();
		SMIValue smiValue = DataUtil.getSMIValue(new BYTE(codiIndex));
		datas.add(smiValue);
		smiValue = DataUtil.getSMIValue(new BYTE(value));
		datas.add(smiValue);

		Object[] params = new Object[] { target, "cmdSetCodiFormNetwork", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}
	}

	/**
	 * --added 2007.10.02 Set Codi Reset
	 *
	 * @param mcuId
	 *            MCU ID
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public void cmdSetCodiReset(String mcuId) throws FMPMcuException, Exception {
		cmdSetCodiReset(mcuId, 0);
	}

	/**
	 * --added 2007.10.02 Set Codi Reset
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param codiIndex
	 *            codi ID
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public void cmdSetCodiReset(String mcuId, int codiIndex) throws FMPMcuException, Exception {
		log.debug("cmdSetCodiReset(" + mcuId + "),(" + codiIndex + ")");

		Target target = CmdUtil.getTarget(mcuId);
		Vector<SMIValue> datas = new Vector<SMIValue>();
		SMIValue smiValue = DataUtil.getSMIValue(new BYTE(codiIndex));
		datas.add(smiValue);

		Object[] params = new Object[] { target, "cmdSetCodiReset", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}
	}

	/**
	 * --added 2007.10.22 Correct sensor pulse
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param sensorId
	 *            Sensor Id Array
	 * @param adjPulse
	 *            adjustment pulse value array
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public void cmdCorrectModemPulse(String mcuId, String[] modemIds, int[] adjPulse)
			throws FMPMcuException, Exception {
		log.debug("cmdCorrectModemPulse(" + mcuId + ")");

		Target target = CmdUtil.getTarget(mcuId);

		Vector<SMIValue> datas = new Vector<SMIValue>();
		SMIValue smiValue = null;

		for (int i = 0; i < modemIds.length; i++) {
			smiValue = DataUtil.getSMIValueByObject("sensorID", modemIds[i]);
			datas.add(smiValue);
			smiValue = DataUtil.getSMIValue(new INT(adjPulse[i]));
			datas.add(smiValue);
		}

		Object[] params = new Object[] { target, "cmdCorrectSensorPulse", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}
	}

	/**
	 * cmdSetModemAmrData
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param modemId
	 *            Modem ID
	 * @param data
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public void cmdSetModemAmrData(String mcuId, String modemId, byte[] amrMask, byte[] data)
			throws FMPMcuException, Exception {
		log.debug("MCUID[" + mcuId + "] MODEM[" + modemId + "] AMRMASK[" + Hex.decode(amrMask) + "] DATA["
				+ Hex.decode(data) + "]");

		Target target = null;
		Vector<SMIValue> datas = new Vector<SMIValue>();

		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");

			txStatus = txManager.getTransaction(null);

			Modem modem = modemDao.get(modemId);
			target = CmdUtil.getTarget(modem);

			txManager.commit(txStatus);
		} catch (Exception e) {
			if (txManager != null)
				txManager.rollback(txStatus);
			throw e;
		}

		SMIValue smiValue = DataUtil.getSMIValueByObject("sensorID", "" + modemId);
		datas.add(smiValue);
		smiValue = DataUtil.getSMIValue(new WORD(amrMask));
		datas.add(smiValue);
		smiValue = DataUtil.getSMIValue(new OCTET(data, data.length, true));
		datas.add(smiValue);

		Object[] params = new Object[] { target, "cmdSetSensorAmrData", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;
		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}
	}

	/**
	 * cmdGetModemAmrData
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param modemId
	 *            Modem ID
	 * @param data
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public byte[] cmdGetModemAmrData(String mcuId, String modemId) throws FMPMcuException, Exception {
		log.debug("MCUID[" + mcuId + "] MODEM[" + modemId + "]");

		Target target = null;
		Vector<SMIValue> datas = new Vector<SMIValue>();

		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");

			txStatus = txManager.getTransaction(null);

			Modem modem = modemDao.get(modemId);
			target = CmdUtil.getTarget(modem);

			SMIValue smiValue = DataUtil.getSMIValueByObject("sensorID", "" + modemId);
			datas.add(smiValue);

			txManager.commit(txStatus);
		} catch (Exception e) {
			if (txManager != null)
				txManager.rollback(txStatus);
			throw e;
		}

		Object[] params = new Object[] { target, "cmdGetSensorAmrData", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;
		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		byte[] bx = null;
		if (smiValues != null && smiValues.length != 0) {
			log.debug(smiValues[0].toString());
			bx = ((OCTET) smiValues[0].getVariable()).getValue();

			return bx;
		} else {
			log.debug("smiValues is null");
			return null;
		}
	}

	/**
	 * cmdGetSensorAmrData
	 *
	 * @param mcuId
	 *            MCU Identifier
	 * @param modemId
	 *            Modem Identifier
	 * @param data
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public byte[] cmdCommandModem(String mcuId, String modemId, byte cmdType, byte[] data)
			throws FMPMcuException, Exception {
		log.debug("MCUID[" + mcuId + "] MODEM[" + modemId + "] CMDTYPE[" + Hex.decode(new byte[] { cmdType })
				+ "] DATA[" + (data != null ? Hex.decode(data) : "") + "]");

		Target target = null;
		Vector<SMIValue> datas = new Vector<SMIValue>();

		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");

			txStatus = txManager.getTransaction(null);

			Modem modem = modemDao.get(modemId);
			target = CmdUtil.getTarget(modem);

			SMIValue smiValue = DataUtil.getSMIValueByObject("sensorID", "" + modemId);
			datas.add(smiValue);
			smiValue = DataUtil.getSMIValue(new BYTE(cmdType));
			datas.add(smiValue);

			/*
			 * 2008.07.18 by js.
			 */
			MCU mcu = modem.getMcu();
			if (mcu != null) {
				String sysSwRevision = mcu.getSysSwRevision();
				if (sysSwRevision != null && !"".equals(sysSwRevision)) {
					if (sysSwRevision.compareTo("1703") >= 0) {
						datas.add(DataUtil.getSMIValue(new WORD(0)));
						datas.add(DataUtil.getSMIValue(new WORD(0)));
						datas.add(DataUtil.getSMIValue(new BYTE(0)));
					}
				}
			}

			if (data != null) {
				smiValue = DataUtil.getSMIValue(new OCTET(data, data.length, true));
				datas.add(smiValue);
			}

			txManager.commit(txStatus);
		} catch (Exception e) {
			if (txManager != null)
				txManager.rollback(txStatus);
			throw e;
		}

		Object[] params = new Object[] { target, "cmdCommandSensor", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;
		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		byte[] bx = null;
		if (smiValues != null && smiValues.length != 0) {
			log.debug(smiValues[0].toString());
			if (smiValues[0].getVariable() == null) {
				return null;
			}
			bx = ((OCTET) smiValues[0].getVariable()).getValue();

			return bx;
		} else {
			log.debug("smiValues is null");
			return null;
		}
	}

	/**
	 * @param mcuId
	 * @param modemId
	 * @param modemCommand
	 * @param rwFlag
	 *            - 0:write, 1:read
	 * @param commandData
	 * @return
	 * @throws FMPMcuException
	 * @throws Exception
	 */
	@Override
	public byte[] cmdExecuteCommand(String mcuId, String modemId, byte modemCommand, byte rwFlag, byte[] commandData)
			throws FMPMcuException, Exception {
		log.debug("MCUID[" + mcuId + "] MODEM[" + modemId + "] CMDTYPE[" + Hex.decode(new byte[] { modemCommand })
				+ "] RWFLAG[" + rwFlag + "] DATA[" + (commandData != null ? Hex.decode(commandData) : "") + "]");

		Target target = null;
		Vector<SMIValue> datas = new Vector<SMIValue>();

		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");

			txStatus = txManager.getTransaction(null);

			Modem modem = modemDao.get(modemId);
			target = CmdUtil.getTarget(modem);

			datas.add(DataUtil.getSMIValueByObject("sensorID", "" + modemId));
			datas.add(DataUtil.getSMIValue(new BYTE(modemCommand)));
			datas.add(DataUtil.getSMIValueByObject("byteEntry", String.valueOf(rwFlag)));
			// Write mode has data
			if (rwFlag == 0) {
				if (commandData != null) {
					datas.add(DataUtil.getSMIValue(new OCTET(commandData, commandData.length, true)));
				} else {
					throw new Exception("cmdExecuteCommand(write) dose not contain commandData!");
				}
			}

			txManager.commit(txStatus);
		} catch (Exception e) {
			if (txManager != null)
				txManager.rollback(txStatus);
			throw e;
		}

		Object[] params = new Object[] { target, "cmdExecuteCommand", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;
		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		byte[] bx = null;
		if (smiValues != null && smiValues.length != 0) {
			log.debug(smiValues[0].toString());
			if (smiValues[0].getVariable() == null) {
				return null;
			}
			bx = ((OCTET) smiValues[0].getVariable()).getValue();

			return bx;
		} else {
			log.debug("smiValues is null");
			return null;
		}
	}

	/**
	 * cmdCommandModem
	 * 
	 * @param mcuId
	 *            MCU ID
	 * @param sensorId
	 *            Sensor ID
	 * @param data
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public byte[] cmdCommandModem(String mcuId, String modemId, byte cmdType, int fw, int buildnum, boolean force,
			byte[] data) throws FMPMcuException, Exception {
		log.debug("MCUID[" + mcuId + "] MODEM[" + modemId + "] CMDTYPE[" + Integer.toHexString(cmdType) + "] DATA["
				+ Hex.decode(data) + "]");

		Target target = null;
		Vector<SMIValue> datas = new Vector<SMIValue>();

		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");

			txStatus = txManager.getTransaction(null);

			Modem modem = modemDao.get(modemId);
			target = CmdUtil.getTarget(modem);

			SMIValue smiValue = DataUtil.getSMIValueByObject("sensorID", "" + modemId);
			datas.add(smiValue);
			smiValue = DataUtil.getSMIValue(new BYTE(cmdType));
			datas.add(smiValue);
			smiValue = DataUtil.getSMIValue(new WORD(fw));
			datas.add(smiValue);
			smiValue = DataUtil.getSMIValue(new WORD(buildnum));
			datas.add(smiValue);
			smiValue = DataUtil.getSMIValue(new BOOL(force));
			datas.add(smiValue);

			if (data != null) {
				smiValue = DataUtil.getSMIValue(new OCTET(data, data.length, true));
				datas.add(smiValue);
			}

			txManager.commit(txStatus);
		} catch (Exception e) {
			if (txManager != null)
				txManager.rollback(txStatus);
			throw e;
		}

		Object[] params = new Object[] { target, "cmdCommandSensor", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;
		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		byte[] bx = null;
		if (smiValues != null && smiValues.length != 0) {
			log.debug(smiValues[0].toString());
			if (smiValues[0].getVariable() == null) {
				return null;
			}
			bx = ((OCTET) smiValues[0].getVariable()).getValue();

			return bx;
		} else {
			log.debug("smiValues is null");
			return null;
		}
	}

	/**
	 * Sensor Firmware update(OTA)
	 *
	 * @param modemId
	 *            Modem ID
	 * @param filename
	 *            filename
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public void cmdUpdateModemFirmware(String mcuId, String modemId, String fileName)
			throws FMPMcuException, Exception {
		log.debug("cmdUpdateModemFirmware(" + mcuId + "," + modemId + "," + fileName + ")");

		Target target = null;
		Vector<SMIValue> datas = new Vector<SMIValue>();

		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");

			txStatus = txManager.getTransaction(null);

			Modem modem = modemDao.get(modemId);
			target = CmdUtil.getTarget(modem);

			SMIValue smiValue = DataUtil.getSMIValueByObject("sensorID", "" + modemId);
			datas.add(smiValue);
			smiValue = DataUtil.getSMIValueByObject("stringEntry", fileName);
			datas.add(smiValue);

			txManager.commit(txStatus);
		} catch (Exception e) {
			if (txManager != null)
				txManager.rollback(txStatus);
			throw e;
		}

		Object[] params = new Object[] { target, "cmdUpdateSensorFirmware", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}
	}

	/**
	 * It's a command to notify the MCU to update a firmware of equipment.
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param equipType
	 *            0:MCU 1:Modem 2:Codi
	 * @param triggerId
	 *            trigger id
	 * @param oldHwVersion
	 *            old hw version
	 * @param oldSwVersion
	 *            old sw version
	 * @param oldBuildNumber
	 *            old build number
	 * @param newHwVersion
	 *            new hw version
	 * @param newSwVersion
	 *            new sw version
	 * @param newBuildNUmber
	 *            new build number
	 * @param binaryUrl
	 *            location of firmware binary
	 * @param diffUrl
	 *            location of firmware diff
	 * @param equipList
	 *            list for equipment id
	 *
	 * @param fileName
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Deprecated
	@Override
	public void cmdPackageDistribution(String mcuId, int equipType, String triggerId, String oldHwVersion,
			String oldSwVersion, String oldBuildNumber, String newHwVersion, String newSwVersion, String newBuildNumber,
			String binaryMD5, String binaryUrl, String diffMD5, String diffUrl, String[] equipList, int otaType,
			int modemType, String modemTypeStr, int dataType, int otaLevel, int otaRetry)
					throws FMPMcuException, Exception {

		Target target = CmdUtil.getTarget(mcuId);

		StringBuffer buf = new StringBuffer();
		buf.append("MCUID[" + mcuId + " build " + target.getFwRevision() + "]\n " + "EQUIP_TYPE[" + equipType
				+ "]\n " + "TRIGGER_ID[" + triggerId + "]\n " + "OLD_HW_VER[" + oldHwVersion + "]\n " + "OLD_SW_VER["
				+ oldSwVersion + "]\n " + "OLD_BUILD_NO[" + oldBuildNumber + "]\n " + "NEW_HW_VER[" + newHwVersion
				+ "]\n " + "NEW_SW_VER[" + newSwVersion + "]\n " + "NEW_BUILD_NO[" + newBuildNumber + "]\n "
				+ "BINARY_MD5[" + binaryMD5 + "]\n " + "BINARY_URL[" + binaryUrl + "]\n " + "DIFF_MD5[" + diffMD5
				+ "]\n " + "DIFF_URL[" + diffUrl + "]\n " + "EQUIP_LIST[");
		for (int i = 0; i < equipList.length; i++) {
			buf.append(equipList[i] + ", ");
		}
		buf.append("]\n " + "OTATYPE[" + otaType + "]\n " + "MODEMTYPE[" + modemType + " " + modemTypeStr + "]\n "
				+ "DATATYPE[" + dataType + "]\n " + "OTALEVEL[" + otaLevel + "]\n " + "OTARETRY[" + otaRetry + "]");

		log.debug(buf.toString());

		Vector<SMIValue> datas = new Vector<SMIValue>();
		datas.add(DataUtil.getSMIValue(new INT(equipType)));
		datas.add(DataUtil.getSMIValueByObject("stringEntry", triggerId));
		datas.add(DataUtil.getSMIValueByObject("stringEntry", oldHwVersion));
		datas.add(DataUtil.getSMIValueByObject("stringEntry", oldSwVersion));
		datas.add(DataUtil.getSMIValueByObject("stringEntry", oldBuildNumber));
		datas.add(DataUtil.getSMIValueByObject("stringEntry", newHwVersion));
		datas.add(DataUtil.getSMIValueByObject("stringEntry", newSwVersion));
		datas.add(DataUtil.getSMIValueByObject("stringEntry", newBuildNumber));
		datas.add(DataUtil.getSMIValueByObject("stringEntry", binaryMD5));
		datas.add(DataUtil.getSMIValueByObject("stringEntry", binaryUrl));
		datas.add(DataUtil.getSMIValueByObject("stringEntry", diffMD5));
		datas.add(DataUtil.getSMIValueByObject("stringEntry", diffUrl));

		buf.setLength(0);
		for (int i = 0; i < equipList.length; i++) {
			buf.append(equipList[i]);
		}

		String strRevision = target.getFwRevision();
		if (strRevision != null && !"".equals(strRevision)) {
			// Int Type
			if (strRevision.compareTo("1703") > 0 && strRevision.compareTo("2013") <= 0) {
				datas.add(DataUtil.getSMIValueByObject("streamEntry", buf.toString()));
				datas.add(DataUtil.getSMIValue(new INT(otaType)));
				datas.add(DataUtil.getSMIValue(new INT(modemType)));
				datas.add(DataUtil.getSMIValue(new INT(dataType)));
				datas.add(DataUtil.getSMIValue(new INT(otaLevel)));
				datas.add(DataUtil.getSMIValue(new INT(otaRetry)));
			}
			// String Type
			else if (strRevision.compareTo("2013") > 0) {
				datas.add(DataUtil.getSMIValueByObject("streamEntry", buf.toString()));
				datas.add(DataUtil.getSMIValue(new INT(otaType)));
				datas.add(DataUtil.getSMIValueByObject("stringEntry", modemTypeStr));
				datas.add(DataUtil.getSMIValue(new INT(dataType)));
				datas.add(DataUtil.getSMIValue(new INT(otaLevel)));
				datas.add(DataUtil.getSMIValue(new INT(otaRetry)));

			}
			// Old type
			else {
				datas.add(DataUtil.getSMIValueByObject("stringEntry", buf.toString()));
			}
		} else {
			datas.add(DataUtil.getSMIValueByObject("stringEntry", buf.toString()));
		}

		Object[] params = new Object[] { target, "cmdPackageDistribution", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}
	}

	@Override
	public void cmdDistribution(String mcuId, String triggerId, int equipKind, String model, int transferType,
			int otaStep, int multiWriteCount, int maxRetryCount, int otaThreadCount, int installType, int oldHwVersion,
			int oldFwVersion, int oldBuild, int newHwVersion, int newFwVersion, int newBuild, String binaryURL,
			String binaryMD5, String diffURL, String diffMD5, List equipIdList) throws FMPMcuException, Exception {
		log.debug("cmdDistribution!");

		Target target = CmdUtil.getTarget(mcuId);

		StringBuffer buf = new StringBuffer();
		buf.append("MCUID[" + mcuId + " build " + target.getFwRevision() + "]\n " + "EQUIP_KIND[" + equipKind
				+ "]\n " + "TRIGGER_ID[" + triggerId + "]\n " + "OLD_HW_VER[" + oldHwVersion + "]\n " + "OLD_FW_VER["
				+ oldFwVersion + "]\n " + "OLD_BUILD_NO[" + oldBuild + "]\n " + "NEW_HW_VER[" + newHwVersion + "]\n "
				+ "NEW_FW_VER[" + newFwVersion + "]\n " + "NEW_BUILD_NO[" + newBuild + "]\n " + "BINARY_MD5["
				+ binaryMD5 + "]\n " + "BINARY_URL[" + binaryURL + "]\n " + "DIFF_MD5[" + diffMD5 + "]\n " + "DIFF_URL["
				+ diffURL + "]\n " + "EQUIP_LIST[");
		for (int i = 0; i < equipIdList.size(); i++) {
			buf.append(equipIdList.get(i) + ", ");
		}
		buf.append("]\n " + "transferType[" + transferType + "]\n " + "otaStep[" + otaStep + "]\n " + "multiWriteCount["
				+ multiWriteCount + "]\n " + "maxRetryCount[" + maxRetryCount + "]\n " + "installType[" + installType
				+ "]\n " + "otaThreadCount[" + otaThreadCount + "]");

		log.debug(buf.toString());

		Vector<SMIValue> datas = new Vector<SMIValue>();
		datas.add(DataUtil.getSMIValueByObject("stringEntry", triggerId));
		datas.add(DataUtil.getSMIValue(new BYTE(equipKind)));
		datas.add(DataUtil.getSMIValueByObject("stringEntry", model));
		datas.add(DataUtil.getSMIValue(new BYTE(transferType)));
		datas.add(DataUtil.getSMIValue(new UINT(otaStep)));
		datas.add(DataUtil.getSMIValue(new BYTE(multiWriteCount)));
		datas.add(DataUtil.getSMIValue(new UINT(maxRetryCount)));
		datas.add(DataUtil.getSMIValue(new BYTE(otaThreadCount)));
		datas.add(DataUtil.getSMIValue(new BYTE(installType)));
		datas.add(DataUtil.getSMIValue(new WORD(oldHwVersion)));
		datas.add(DataUtil.getSMIValue(new WORD(oldFwVersion)));
		datas.add(DataUtil.getSMIValue(new WORD(oldBuild)));
		datas.add(DataUtil.getSMIValue(new WORD(newHwVersion)));
		datas.add(DataUtil.getSMIValue(new WORD(newFwVersion)));
		datas.add(DataUtil.getSMIValue(new WORD(newBuild)));
		datas.add(DataUtil.getSMIValueByObject("stringEntry", binaryURL));
		datas.add(DataUtil.getSMIValueByObject("stringEntry", binaryMD5));
		datas.add(DataUtil.getSMIValueByObject("stringEntry", diffURL));
		datas.add(DataUtil.getSMIValueByObject("stringEntry", diffMD5));
		for (Iterator<?> iter = equipIdList.iterator(); iter.hasNext();) {
			datas.add(DataUtil.getSMIValueByObject("eui64Entry", iter.next()));
		}

		Object[] params = new Object[] { target, "cmdDistribution", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}
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
	 * @param equipIdList
	 * @throws FMPMcuException
	 * @throws Exception
	 */
	public void cmdDistributionSensor(String mcuId, String triggerId, int equipKind, String model, int transferType,
			int otaStep, int multiWriteCount, int maxRetryCount, int otaThreadCount, int installType, int oldHwVersion,
			int oldFwVersion, int oldBuild, List equipIdList) throws FMPMcuException, Exception {

		log.debug("cmdDistributionSensor!");

		Target target = CmdUtil.getTarget(mcuId);

		StringBuffer buf = new StringBuffer();
		buf.append("MCUID[" + mcuId + " build " + target.getFwRevision() + "]\n " + "EQUIP_KIND[" + equipKind
				+ "]\n " + "TRIGGER_ID[" + triggerId + "]\n " + "OLD_HW_VER[" + oldHwVersion + "]\n " + "OLD_FW_VER["
				+ oldFwVersion + "]\n " + "OLD_BUILD_NO[" + oldBuild + "]\n " + "EQUIP_LIST[");
		for (int i = 0; i < equipIdList.size(); i++) {
			buf.append(equipIdList.get(i) + ", ");
		}
		buf.append("]\n " + "transferType[" + transferType + "]\n " + "otaStep[" + otaStep + "]\n " + "multiWriteCount["
				+ multiWriteCount + "]\n " + "maxRetryCount[" + maxRetryCount + "]\n " + "installType[" + installType
				+ "]\n " + "otaThreadCount[" + otaThreadCount + "]");

		log.debug(buf.toString());

		Vector<SMIValue> datas = new Vector<SMIValue>();
		datas.add(DataUtil.getSMIValueByObject("stringEntry", triggerId));
		datas.add(DataUtil.getSMIValue(new BYTE(equipKind)));
		datas.add(DataUtil.getSMIValueByObject("stringEntry", model));
		datas.add(DataUtil.getSMIValue(new BYTE(transferType)));
		datas.add(DataUtil.getSMIValue(new UINT(otaStep)));
		datas.add(DataUtil.getSMIValue(new BYTE(multiWriteCount)));
		datas.add(DataUtil.getSMIValue(new UINT(maxRetryCount)));
		datas.add(DataUtil.getSMIValue(new BYTE(otaThreadCount)));
		datas.add(DataUtil.getSMIValue(new BYTE(installType)));
		datas.add(DataUtil.getSMIValue(new WORD(oldHwVersion)));
		datas.add(DataUtil.getSMIValue(new WORD(oldFwVersion)));
		datas.add(DataUtil.getSMIValue(new WORD(oldBuild)));
		for (Iterator<?> iter = equipIdList.iterator(); iter.hasNext();) {
			datas.add(DataUtil.getSMIValueByObject("eui64Entry", iter.next()));
		}

		Object[] params = new Object[] { target, "cmdDistributionSensor", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}
	}

	/**
	 * cmdDistributionState
	 * 
	 * @param mcuId
	 * @param triggerId
	 * @return
	 * @throws FMPMcuException
	 * @throws Exception
	 */
	@Override
	public List cmdDistributionState(String mcuId, String triggerId) throws FMPMcuException, Exception {
		log.debug("cmdDistributionState MCU[" + mcuId + "] triggerId[" + triggerId + "]");

		Target target = CmdUtil.getTarget(mcuId);

		Vector<SMIValue> datas = new Vector<SMIValue>();
		datas.add(DataUtil.getSMIValueByObject("stringEntry", triggerId));

		Object[] params = new Object[] { target, "cmdDistributionState", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		List<SMIValue> list = new ArrayList<SMIValue>();
		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		if (smiValues != null && smiValues.length != 0) {
			for (int i = 0; i < smiValues.length; i++) {
				list.add(smiValues[i]);
				log.debug(smiValues[i].toString());
			}
		} else {
			log.debug("smiValues is null");
		}

		return list;
	}

	/**
	 * cmdDistributionCancel
	 * 
	 * @param mcuId
	 * @param triggerId
	 * @throws FMPMcuException
	 * @throws Exception
	 */
	@Override
	public void cmdDistributionCancel(String mcuId, String triggerId) throws FMPMcuException, Exception {
		log.debug("cmdDistributionState MCU[" + mcuId + "] triggerId[" + triggerId + "]");

		Target target = CmdUtil.getTarget(mcuId);

		Vector<SMIValue> datas = new Vector<SMIValue>();
		datas.add(DataUtil.getSMIValueByObject("stringEntry", triggerId));

		Object[] params = new Object[] { target, "cmdDistributionCancel", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}
	}

	/**
	 * cmdBypassSensor(102.27)
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param modemId
	 *            Modem ID
	 * @param isLinkSkip
	 *            Link Frame send true
	 * @param data
	 *            data stream
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public void cmdBypassSensor(String mcuId, String modemId, boolean isLinkSkip, byte[] data)
			throws FMPMcuException, Exception {
		log.debug("MCUID[" + mcuId + "] SENSOR[" + modemId + "] ISLINKSKIP[" + isLinkSkip + "] DATA[" + Hex.decode(data)
				+ "]");

		Target target = null;
		Vector<SMIValue> datas = new Vector<SMIValue>();

		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");

			txStatus = txManager.getTransaction(null);

			Modem modem = modemDao.get(modemId);
			target = CmdUtil.getTarget(modem);

			SMIValue smiValue = DataUtil.getSMIValueByObject("sensorID", "" + modemId);
			datas.add(smiValue);
			smiValue = DataUtil.getSMIValue(new BOOL(isLinkSkip));
			datas.add(smiValue);
			smiValue = DataUtil.getSMIValue(new OCTET(data, data.length, true));
			datas.add(smiValue);

			txManager.commit(txStatus);
		} catch (Exception e) {
			if (txManager != null)
				txManager.rollback(txStatus);
			throw e;
		}

		Object[] params = new Object[] { target, "cmdBypassSensor", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;
		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}
	}

	/**
	 * cmdBypassSensor(102.27)
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param modemId
	 *            Modem ID
	 * @param isLinkSkip
	 *            Link Frame send true
	 * @param data
	 *            multi frame data stream
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public void cmdBypassSensor(String mcuId, String modemId, boolean isLinkSkip, ArrayList data)
			throws FMPMcuException, Exception {
		log.debug("MCUID[" + mcuId + "] MODEM[" + modemId + "] ISLINKSKIP[" + isLinkSkip + "]");

		Target target = CmdUtil.getTarget(mcuId);
		Vector<SMIValue> datas = new Vector<SMIValue>();

		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");

			txStatus = txManager.getTransaction(null);

			Modem modem = modemDao.get(modemId);
			target = CmdUtil.getTarget(modem);

			SMIValue smiValue = DataUtil.getSMIValueByObject("sensorID", "" + modemId);
			datas.add(smiValue);
			smiValue = DataUtil.getSMIValue(new BOOL(isLinkSkip));
			datas.add(smiValue);

			if (data == null || data.size() < 1) {
				log.error("Invalid Parameter Data Stream");
				throw new Exception("Invalid Parameter Data Stream");
			}
			for (int i = 0; i < data.size(); i++) {
				byte[] temp = (byte[]) data.get(i);
				smiValue = DataUtil.getSMIValue(new OCTET(temp, temp.length, true));
				datas.add(smiValue);
			}

			txManager.commit(txStatus);
		} catch (Exception e) {
			if (txManager != null) {
				txManager.rollback(txStatus);
			}
			throw e;
		}

		Object[] params = new Object[] { target, "cmdBypassSensor", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;
		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}
	}

	/**
	 * cmdGetFFDList(102.38)
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param parser
	 *            parser name
	 * @param fwversion
	 *            firmware version
	 * @param fwbuild
	 *            firmware build
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public ffdEntry[] cmdGetFFDList(String mcuId, String parser, String fwVersion, String fwBuild)
			throws FMPMcuException, Exception {
		log.debug("MCUID[" + mcuId + "] Parser[" + parser + "] FWVersion[" + fwVersion + "] FWBuild[" + fwBuild + "]");

		Target target = CmdUtil.getTarget(mcuId);
		Vector<SMIValue> datas = new Vector<SMIValue>();

		// check parser
		SMIValue smiValue = null;
		if (parser != null && !"".equals(parser)) {
			smiValue = DataUtil.getSMIValueByObject("stringEntry", "" + parser);
			datas.add(smiValue);

			if (fwVersion != null && !"".equals(fwVersion)) {
				smiValue = DataUtil.getSMIValueByObject("wordEntry", "" + Double.parseDouble(fwVersion) * 10);
				datas.add(smiValue);
			}

			if (fwBuild != null && !"".equals(fwBuild)) {
				smiValue = DataUtil.getSMIValueByObject("wordEntry", "" + fwBuild);
				datas.add(smiValue);
			}
		}

		Object[] params = new Object[] { target, "cmdGetFFDList", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			SMIValue[] smiValues = (SMIValue[]) obj;
			List<ffdEntry> list = new ArrayList<ffdEntry>();
			for (int i = 0; i < smiValues.length; i++) {
				smiValue = smiValues[i];
				OPAQUE mdv = (OPAQUE) smiValue.getVariable();
				ffdEntry value = (ffdEntry) mdv.getValue();
				log.debug(value);
				list.add(value);
			}
			return list.toArray(new ffdEntry[list.size()]);
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}
	}

	/**
	 * command
	 * 
	 * @param mcuId
	 * @param miuType
	 *            Device
	 * @param miuClassName
	 *            ZEUPLS, ZRU, EnergyMeter
	 * @param miuId
	 *            Device
	 * @param command
	 * @param option
	 *            - 0x01 : ASYNC_OPT_RETURN_CODE_EVT - 0x02 :
	 *            ASYNC_OPT_RESULT_DATA_EVT - 0x10 : ASYNC_OPT_RETURN_CODE_SAVE
	 *            - 0x20 : ASYNC_OPT_RESULT_DATA_SAVE
	 * @param day
	 *            Keep Option
	 * @param nice
	 *            Request
	 * @param ntry
	 * @param args
	 *            CommandOID Parameter
	 * @param serviceType
	 *            1:NMS 2:MTR
	 * @param operator
	 * @return trId
	 * @throws Exception
	 */
	@Override
	public long cmdAsynchronousCall(String mcuId, String miuType, String miuClassName, String miuId, String command,
			int option, int day, int nice, int ntry, String[][] args, int serviceType, String operator)
					throws Exception {
		String requestTime = DateTimeUtil.getDateString(new Date());

		Target target = CmdUtil.getTarget(mcuId);
		Vector<SMIValue> datas = new Vector<SMIValue>();

		log.debug("cmdAsynchronousCall : mcuId[" + mcuId + "], miuType[" + miuType + "], command[" + command
				+ "], option[" + option + "], day[" + day + "], nice[" + nice + "], ntry[" + ntry + "], ");
		for (int i = 0; i < args.length; i++) {
			log.debug("args[i][0]:" + args[i][0] + ", args[i][1]:" + args[i][1]);
		}

		datas.add(DataUtil.getSMIValueByObject(miuType, miuId));
		datas.add(DataUtil.getSMIValue(DataUtil.getOIDByMIBName(command)));
		datas.add(DataUtil.getSMIValueByObject("byteEntry", String.valueOf(option)));
		datas.add(DataUtil.getSMIValueByObject("byteEntry", String.valueOf(day)));
		datas.add(DataUtil.getSMIValueByObject("charEntry", String.valueOf((char) nice)));
		datas.add(DataUtil.getSMIValueByObject("byteEntry", String.valueOf(ntry)));
		for (int i = 0; i < args.length; i++) {
			datas.add(DataUtil.getSMIValueByObject(args[i][0], String.valueOf(args[i][1])));
		}

		Object[] params = new Object[] { target, "cmdAsynchronousCall", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		log.debug("obj: " + obj);

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			SMIValue[] smiValues = (SMIValue[]) obj;
			WORD trId = (WORD) smiValues[0].getVariable();
			log.debug("Transaction Id Return: " + trId.getValue());

			CmdUtil.createAsyncTr(trId.getValue(), mcuId, miuClassName, miuId, command, option, day, nice, ntry,
					requestTime, args, serviceType, operator);

			return trId.getValue();
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}
	}

	/**
	 * (102.41)
	 * 
	 * @param mcuId
	 * @param miuType
	 *            Device
	 * @param miuId
	 *            Device
	 * @param parser
	 * @param version
	 * @param build
	 * @return trInfoEntry(6.1)
	 * @throws Exception
	 */
	@Override
	public trInfoEntry cmdShowTransactionList(String mcuId, String miuType, String miuId, String parser, String version,
			String build) throws Exception {

		Target target = CmdUtil.getTarget(mcuId);
		Vector<SMIValue> datas = new Vector<SMIValue>();

		if (miuId != null) {
			datas.add(DataUtil.getSMIValueByObject(miuType, miuId));
		} else if (parser != null) {
			datas.add(DataUtil.getSMIValueByObject("stringEntry", parser));
		}

		datas.add(DataUtil.getSMIValueByObject("wordEntry", (Double.parseDouble(version) * 10) + ""));
		datas.add(DataUtil.getSMIValueByObject("wordEntry", (Double.parseDouble(build) * 10) + ""));

		Object[] params = new Object[] { target, "cmdShowTransactionList", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue) {
			SMIValue smiValue = (SMIValue) obj;
			OPAQUE mdv = (OPAQUE) smiValue.getVariable();
			return (trInfoEntry) mdv.getValue();
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}
	}

	/**
	 * (102.42)
	 * 
	 * @param mcuId
	 * @param trId
	 * @param option
	 *            - 0x01 : ASYNC_OPT_RETURN_CODE_EVT - 0x02 :
	 *            ASYNC_OPT_RESULT_DATA_EVT - 0x10 : ASYNC_OPT_RETURN_CODE_SAVE
	 *            - 0x20 : ASYNC_OPT_RESULT_DATA_SAVE
	 * @return Transaction Information(trInfoEntry), Parameter MIBValue, Result
	 *         MIBValue, Transaction History
	 * @throws Exception
	 */
	@Override
	public Map<String, Object> cmdShowTransactionInfo(String mcuId, long trId, int option) throws Exception {

		Target target = CmdUtil.getTarget(mcuId);
		Vector<SMIValue> datas = new Vector<SMIValue>();

		datas.add(DataUtil.getSMIValueByObject("uintEntry", trId + ""));
		datas.add(DataUtil.getSMIValueByObject("byteEntry", option + ""));

		Object[] params = new Object[] { target, "cmdShowTransactionInfo", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		Map<String, Object> result = new HashMap<String, Object>();
		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			SMIValue[] smiValue = (SMIValue[]) obj;

			int idx = 0;
			OPAQUE mdv = (OPAQUE) smiValue[0].getVariable();
			result.put("trInfoEntry", ((OPAQUE) smiValue[idx++].getVariable()).getValue());
			// param
			int cnt = ((WORD) smiValue[idx++].getVariable()).getValue();
			for (int i = 0; i < cnt; i++) {
				mdv = (OPAQUE) smiValue[idx++].getVariable();
				result.put("param." + i, mdv.getValue());
			}
			// result
			cnt = ((WORD) smiValue[idx++].getVariable()).getValue();
			for (int i = 0; i < cnt; i++) {
				mdv = (OPAQUE) smiValue[idx++].getVariable();
				result.put("result." + i, mdv.getValue());
			}
			// history
			cnt = ((WORD) smiValue[idx++].getVariable()).getValue();
			for (int i = 0; i < cnt; i++) {
				mdv = (OPAQUE) smiValue[idx++].getVariable();
				result.put("history." + i, mdv.getValue());
			}

			return result;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}
	}

	/**
	 * 비동기 트랜잭션 상태를 변경한다.(102.43)
	 * 
	 * @param mcuId
	 * @param trId
	 *            트랜잭션 아이디
	 * @param state
	 *            트랜잭션 상태 - 0x08 : TR_STATE_TERMINATE : Transaction 종료 - 0x10 :
	 *            TR_STATE_DELETE : Transaction 삭제
	 * @throws Exception
	 */
	@Override
	public void cmdModifyTransaction(String mcuId, long[] trId, int[] state) throws Exception {

		Target target = CmdUtil.getTarget(mcuId);
		Vector<SMIValue> datas = new Vector<SMIValue>();

		for (int i = 0; i < trId.length; i++) {
			datas.add(DataUtil.getSMIValueByObject("uintEntry", trId[i]));
			datas.add(DataUtil.getSMIValueByObject("byteEntry", state[i]));
		}

		Object[] params = new Object[] { target, "cmdModifyTransaction", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}
	}

	/**
	 * (102.44)
	 * 
	 * @param mcuId
	 * @param modemId
	 * @param count
	 * @return EventLog[] - aimir-service-common/EventLog
	 * @throws Exception
	 */
	@Override
	public EventLog[] cmdGetModemEvent(String mcuId, String modemId, int count) throws Exception {

		Target target = null;
		Vector<SMIValue> datas = new Vector<SMIValue>();

		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");

			txStatus = txManager.getTransaction(null);

			Modem modem = modemDao.get(modemId);
			target = CmdUtil.getTarget(modem);

			datas.add(DataUtil.getSMIValueByObject("sensorID", modemId));
			datas.add(DataUtil.getSMIValueByObject("byteEntry", "" + count));

			txManager.commit(txStatus);
		} catch (Exception e) {
			if (txManager != null)
				txManager.rollback(txStatus);
			throw e;
		}

		Object[] params = new Object[] { target, "cmdGetSensorEvent", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			SMIValue[] smiValues = (SMIValue[]) obj;
			byte[] bx = null;

			Modem modem = modemDao.get(modemId);
			ModemType modemType = modem.getModemType();
			String fwVersion = modem.getFwVer();
			String fwBuild = modem.getFwRevision();

			ModemROM modemROM = new ModemROM(fwVersion, fwBuild);
			/*
			 * switch (modemType) { case ZRU : modemROM = new ZRUROM(fwVersion,
			 * fwBuild); break; case ZEUPLS : modemROM = new
			 * ZEUPLSROM(fwVersion, fwBuild); break; case ZMU : modemROM = new
			 * ZMUROM(fwVersion, fwBuild); break; case ZBRepeater : modemROM =
			 * new ZEUPLSROM(fwVersion, fwBuild); break; case ZEUMBus : modemROM
			 * = new ZEUMBUSROM(fwVersion, fwBuild); }
			 */

			List<EventLog> eventList = new ArrayList<EventLog>();

			for (int i = 0; i < smiValues.length; i++) {
				if (smiValues[i].getVariable() instanceof OCTET) {
					bx = ((OCTET) smiValues[i].getVariable()).getValue();
					modemROM.parseEventLog(bx);
					for (int j = 0; j < modemROM.getEventLog().length; j++) {
						eventList.add(modemROM.getEventLog()[j]);
					}
				}
			}

			return eventList.toArray(new EventLog[eventList.size()]);
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}
	}

	/**
	 * (102.44)
	 * 
	 * @param mcuId
	 * @param modemId
	 * @param count
	 * @return BatteryLog - aimir-service-common/BatteryLog
	 * @throws Exception
	 */
	@Override
	public BatteryLog cmdGetModemBattery(String mcuId, String modemId, int count) throws Exception {

		Target target = CmdUtil.getTarget(mcuId);
		Vector<SMIValue> datas = new Vector<SMIValue>();

		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");

			txStatus = txManager.getTransaction(null);

			Modem modem = modemDao.get(modemId);
			target = CmdUtil.getTarget(modem);

			datas.add(DataUtil.getSMIValueByObject("sensorID", modemId));
			datas.add(DataUtil.getSMIValueByObject("byteEntry", "" + count));

			txManager.commit(txStatus);
		} catch (Exception e) {
			if (txManager != null)
				txManager.rollback(txStatus);
			throw e;
		}

		Object[] params = new Object[] { target, "cmdGetSensorBattery", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			SMIValue[] smiValues = (SMIValue[]) obj;
			OPAQUE mdv = null;
			sensorBatteryEntry sbe = null;
			BatteryLog batteryLog = new BatteryLog();
			for (int i = 0; i < smiValues.length; i++) {
				mdv = (OPAQUE) smiValues[i].getVariable();
				sbe = (sensorBatteryEntry) mdv.getValue();
				batteryLog.addBatteryVolt(sbe.getBattVolt().getValue());
				batteryLog.addConsumptionCurrent(sbe.getBattCurrent().getValue());
				batteryLog.addOffset(sbe.getBattOffset().getValue());
			}

			return batteryLog;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}
	}

	/**
	 * This command send data to sensor so that use Digital In/Out port.(OID :
	 * 102.49)
	 * 
	 * @param mcuId
	 * @param modemId
	 * @param direction
	 * @param mask
	 * @param value
	 */
	@Override
	public byte[] cmdDigitalInOut(String mcuId, String modemId, byte direction, byte mask, byte value)
			throws FMPMcuException, Exception {

		log.debug("MCUID[" + mcuId + "], MODEM[" + modemId + "], " + "DIRECTION[" + Hex.decode(new byte[] { direction })
				+ "], " + "MASK[" + mask + "], DATA[" + value + "]");

		// Get MCU ID
		Target target = null;

		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		Vector<SMIValue> datas = new Vector<SMIValue>();
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");

			txStatus = txManager.getTransaction(null);
			Modem modem = modemDao.get(modemId);

			target = CmdUtil.getTarget(modem);

			// insert parameters
			datas.add(DataUtil.getSMIValueByObject("sensorID", "" + modemId));
			datas.add(DataUtil.getSMIValueByObject("byteEntry", String.valueOf(direction)));
			datas.add(DataUtil.getSMIValueByObject("byteEntry", String.valueOf(mask)));
			datas.add(DataUtil.getSMIValueByObject("byteEntry", String.valueOf(value)));

			txManager.commit(txStatus);
		} catch (Exception e) {
			if (txManager != null)
				txManager.rollback(txStatus);
			throw e;
		}

		// create parameters
		Object[] params = new Object[] { target, "cmdDigitalInOut", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		// Distinguish return value
		SMIValue[] smiValues = null;
		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());

		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;

		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		// Get return value
		byte[] bx = null;
		if (smiValues != null && smiValues.length != 0) {
			log.debug(smiValues[0].toString());

			if (smiValues[0].getVariable() == null) {
				return null;
			}
			bx = ((OCTET) smiValues[0].getVariable()).getValue();

			return bx;
		} else {
			log.debug("smiValues is null");
			return null;
		}

	}

	/**
	 * (102.54) 그룹을 추가한다
	 * 
	 * @param mcuId
	 * @param groupName
	 * @return greoupKey
	 * @throws FMPMcuException
	 * @throws Exception
	 *             IF4ERR_GROUP_DB_OPEN_FAIL : 그룹 DB의 Open 실패
	 *             IF4ERR_GROUP_NAME_DUPLICATE : 중복된 그룹 이름
	 *             IF4ERR_GROUP_STORE_ERROR : 그룹 DB에 저장 에러
	 */
	@Override
	public int cmdGroupAdd(String mcuId, String groupName) throws FMPMcuException, Exception {

		log.debug("MCUID[" + mcuId + "], GROUPNAME[" + groupName + "]");

		// Get MCU ID
		Target target = CmdUtil.getTarget(mcuId);

		// insert parameters
		Vector<SMIValue> datas = new Vector<SMIValue>();
		datas.add(DataUtil.getSMIValueByObject("stringEntry", groupName));

		// create parameters
		Object[] params = new Object[] { target, "cmdGroupAdd", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		// Distinguish return value
		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		int value = 0;
		if (smiValues.length > 0) {
			value = Integer.parseInt(smiValues[0].getVariable().toString());
		}
		return value;
	}

	/**
	 * (102.56) 그룹에 멤버를 추가한다
	 * 
	 * @param mcuId
	 * @param groupKey
	 * @param modemId
	 * @throws FMPMcuException
	 * @throws Exception
	 *             IF4ERR_INVALID_PARAM : 잘못된 Parameter가 전달
	 *             IF4ERR_GROUP_DB_OPEN_FAIL : 그룹 DB의 Open 실패
	 *             IF4ERR_TRANSACTION_CREATE_FAIL : Transaction 생성 실패
	 */
	@Override
	public void cmdGroupAddMember(String mcuId, int groupKey, String modemId) throws FMPMcuException, Exception {

		log.debug("MCUID[" + mcuId + "], GROUPKey[" + groupKey + "]");

		// Get MCU ID
		Target target = CmdUtil.getTarget(mcuId);

		// insert parameters
		Vector<SMIValue> datas = new Vector<SMIValue>();
		datas.add(DataUtil.getSMIValueByObject("intEntry", "" + groupKey));
		datas.add(DataUtil.getSMIValueByObject("sensorID", modemId));

		// create parameters
		Object[] params = new Object[] { target, "cmdGroupAddMember", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		// Distinguish return value
		SMIValue[] smiValues = null;
		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());

		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;

		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}
	}

	/**
	 * (102.70) Type을 지원하는 Group을 추가/갱신한다. 만약 해당 Type, Name의 Group이 없다면 신규 생성되고
	 * 있을 경우 Sensor ID가 추가되게 한다.
	 * 
	 * @param mcuId
	 * @param groupType
	 * @param groupName
	 * @param modemId
	 * @throws FMPMcuException
	 * @throws Exception
	 *             IF4ERR_INVALID_ID : 잘못 모뎀 아이디 IF4ERR_INVALID_PARAM : 잘못된
	 *             Parameter가 전달
	 */
	@Override
	public void cmdUpdateGroup(String mcuId, String groupType, String groupName, String[] modemId)
			throws FMPMcuException, Exception {
		StringBuffer buf = new StringBuffer();
		buf.append("MCUID[" + mcuId + "], GroupType[" + groupType + "] GroupName[" + groupName + "] ModemId[");
		for (int i = 0; i < modemId.length; i++) {
			buf.append(modemId + ",");
		}
		buf.append("]");
		log.debug(buf.toString());
		// Get MCU ID
		Target target = CmdUtil.getTarget(mcuId);

		// insert parameters
		Vector<SMIValue> datas = new Vector<SMIValue>();
		datas.add(DataUtil.getSMIValueByObject("stringEntry", groupType));
		datas.add(DataUtil.getSMIValueByObject("stringEntry", groupName));
		for (int i = 0; i < modemId.length; i++)
			datas.add(DataUtil.getSMIValueByObject("sensorID", "" + modemId[i]));

		// create parameters
		Object[] params = new Object[] { target, "cmdUpdateGroup", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		// Distinguish return value
		SMIValue[] smiValues = null;
		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());

		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;

		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}
	}

	/**
	 * (102.71) 지정된 ID를 Group정보에서 삭제한다. 만약 ID를 삭제 후 해당 Group의 Member가 더 이상 없을 경우
	 * Group 정보도 삭제된다.
	 * 
	 * @param mcuId
	 * @param groupType
	 *            널값이 들어올 수 있다.
	 * @param groupName
	 *            널값이 들어올 수 있다.
	 * @param modemId
	 * @throws FMPMcuException
	 * @throws Exception
	 *             IF4ERR_INVALID_ID IF4ERR_INVALID_PARAM
	 */
	@Override
	public void cmdDeleteGroup(String mcuId, String groupType, String groupName, String[] modemId)
			throws FMPMcuException, Exception {
		StringBuffer buf = new StringBuffer();
		buf.append("MCUID[" + mcuId + "], GroupType[" + groupType + "] GroupName[" + groupName + "] ModemId[");
		for (int i = 0; i < modemId.length; i++) {
			buf.append(modemId + ",");
		}
		buf.append("]");
		log.debug(buf.toString());
		// Get MCU ID
		Target target = CmdUtil.getTarget(mcuId);

		// insert parameters
		Vector<SMIValue> datas = new Vector<SMIValue>();
		if (groupType != null && !"".equals(groupType))
			datas.add(DataUtil.getSMIValueByObject("stringEntry", groupType));
		if (groupName != null && !"".equals(groupName))
			datas.add(DataUtil.getSMIValueByObject("stringEntry", groupName));
		for (int i = 0; i < modemId.length; i++)
			datas.add(DataUtil.getSMIValueByObject("sensorID", "" + modemId[i]));

		// create parameters
		Object[] params = new Object[] { target, "cmdDeleteGroup", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		// Distinguish return value
		SMIValue[] smiValues = null;
		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());

		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;

		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}
	}

	/**
	 * (102.72) Type을 지원하는 Group정보를 조회한다. 첫번째 Parameter가 EUI64일 경우 해당 ID의 모든
	 * Group 정보를 조회하게 되고 첫번째 Parameter가 STRING일 경우 type, name, id를 입력 받아 조건에 맞는
	 * Group 정보를 조회하게 된다. 이때 type, name은 생략 가능하다.
	 * 
	 * @param mcuId
	 * @param groupType
	 *            널값이 들어올 수 있다.
	 * @param groupName
	 *            널값이 들어올 수 있다.
	 * @param modemId
	 * @return list[ type(1.11 STRING) name(1.11 STRING) id (1.11 STRING) ]
	 * @throws FMPMcuException
	 * @throws Exception
	 *             IF4ERR_INVALID_ID
	 */
	@Override
	public List<GroupTypeInfo> cmdGetGroup(String mcuId, String groupType, String groupName, String modemId)
			throws FMPMcuException, Exception {
		log.debug("MCUID[" + mcuId + "], GroupType[" + groupType + "] GroupName[" + groupName + "] ModemId[" + modemId
				+ "]");
		// Get MCU ID
		Target target = CmdUtil.getTarget(mcuId);

		// insert parameters
		Vector<SMIValue> datas = new Vector<SMIValue>();
		if (groupType != null && !"".equals(groupType))
			datas.add(DataUtil.getSMIValueByObject("stringEntry", groupType));
		if (groupName != null && !"".equals(groupName))
			datas.add(DataUtil.getSMIValueByObject("stringEntry", groupName));
		datas.add(DataUtil.getSMIValueByObject("sensorID", "" + modemId));

		// create parameters
		Object[] params = new Object[] { target, "cmdGetGroup", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		// Distinguish return value
		SMIValue[] smiValues = null;
		List<GroupTypeInfo> returnData = new ArrayList<GroupTypeInfo>();

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());

		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
			// type, name, id가 차례대로 있을거라고 보고. 결과 확인후 수정할 수도 있음.
			String type = null;
			String name = null;
			String id = null;

			for (int i = 0; i < smiValues.length; i += 3) {
				type = ((OCTET) smiValues[i].getVariable()).toString();
				name = ((OCTET) smiValues[i + 1].getVariable()).toString();
				id = ((OCTET) smiValues[i + 2].getVariable()).toString();

				log.debug("TYPE[" + type + "] NAME[" + name + "] ID[" + id + "]");

				GroupTypeInfo data = new GroupTypeInfo();
				data.setGroupType(type);
				data.setGroupName(name);
				data.setMember(id);
				returnData.add(data);
			}

		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		return returnData;
	}

	/**
	 * (102.53) 그룹의 멤버에 비동기 명령을 수행한다
	 * 
	 * @param mcuId
	 * @param groupName
	 * @param command
	 * @param option
	 *            - 0x01 : ASYNC_OPT_RETURN_CODE_EVT - 0x02 :
	 *            ASYNC_OPT_RESULT_DATA_EVT - 0x10 : ASYNC_OPT_RETURN_CODE_SAVE
	 *            - 0x20 : ASYNC_OPT_RESULT_DATA_SAVE
	 * @param day
	 *            Keep Option
	 * @param nice
	 *            Request
	 * @param ntry
	 * @return trId
	 * @throws Exception
	 *             IF4ERR_INVALID_PARAM : 잘못된 Parameter가 전달 IF4ERR_INVALID_ID :
	 *             miuID가 관리 목록에 없음 IF4ERR_INVALID_TARGET : 대상 장비가
	 *             AsyncrhronousCall을 지원하지 않음 IF4ERR_INVALID_COMMAND : 지원하지 않는
	 *             Command IF4ERR_OUT_OF_RANGE : nDay(0이면 집중기 설정값 사용,
	 *             nNice(default 0 작을수록 우선순위가 높다, nRetry가 유효값 범위 밖에 값으로 설정
	 *             IF4ERR_TRANSACTION_CREATE_FAIL : Transaction 생성 실패
	 *             IF4ERR_INVALID_GROUP : 존재하지 않는 그룹 이름
	 *             IF4ERR_GROUP_DB_OPEN_FAIL : 그룹 DB의 Open 실패
	 */
	@Override
	public long cmdGroupAsyncCall(String mcuId, int groupKey, String command, int option, int day, int nice, int ntry,
			List<SMIValue> param) throws FMPMcuException, Exception {

		Target target = CmdUtil.getTarget(mcuId);
		Vector<SMIValue> datas = new Vector<SMIValue>();

		log.debug("cmdGroupAsyncCall : mcuId[" + mcuId + "], groupKey[" + groupKey + "], command[" + command
				+ "], option[" + option + "], day[" + day + "], nice[" + nice + "], ntry[" + ntry + "], ");
		String requestTime = DateTimeUtil.getDateString(new Date());

		datas.add(DataUtil.getSMIValueByObject("intEntry", groupKey));
		datas.add(DataUtil.getSMIValue(DataUtil.getOIDByMIBName(command)));
		datas.add(DataUtil.getSMIValueByObject("byteEntry", String.valueOf(option)));
		datas.add(DataUtil.getSMIValueByObject("byteEntry", String.valueOf(day)));
		datas.add(DataUtil.getSMIValueByObject("charEntry", String.valueOf((char) nice)));
		datas.add(DataUtil.getSMIValueByObject("byteEntry", String.valueOf(ntry)));

		if (param != null && param.size() > 0) {
			for (SMIValue smiValue : param) {
				datas.add(smiValue);
			}
		}

		Object[] params = new Object[] { target, "cmdGroupAsyncCall", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		log.debug("obj: " + obj);

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			SMIValue[] smiValues = (SMIValue[]) obj;
			int idx = 0;
			while (smiValues != null && smiValues.length > 0 && idx < smiValues.length) {

				HEX eui64 = (HEX) smiValues[idx++].getVariable();
				WORD trId = (WORD) smiValues[idx++].getVariable();
				log.debug("Transaction Id Return: " + trId.getValue() + ", Member=" + eui64.getValue());
				String[][] args = null;
				if (param != null && param.size() > 0) {
					args = new String[param.size()][2];
					int k = 0;
					for (SMIValue ar : param) {
						args[k][0] = ar.getOid().getValue();
						args[k][1] = ar.getVariable().toString();
						k++;
					}
				}

				CmdUtil.createAsyncTr(trId.getValue(), mcuId, "", eui64.getValue(), command, option, day, nice, ntry,
						requestTime, args, 0, OperatorType.SYSTEM.name());
			}

			return 0;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}
	}

	/**
	 * (102.55) 그룹을 삭제한다
	 * 
	 * @param mcuId
	 * @param groupKey
	 * @throws FMPMcuException
	 * @throws Exception
	 *             IF4ERR_GROUP_DB_OPEN_FAIL : 그룹 DB의 Open 실패
	 *             IF4ERR_GROUP_NAME_NOT_EXIST : 그룹 이름이 존재하지 않음
	 *             IF4ERR_GROUP_DELETE_ERROR : 그룹 DB에 삭제 에러
	 */
	@Override
	public void cmdGroupDelete(String mcuId, int groupKey) throws FMPMcuException, Exception {
		log.debug("MCUID[" + mcuId + "], GROUPKEY[" + groupKey + "]");

		// Get MCU ID
		Target target = CmdUtil.getTarget(mcuId);

		// insert parameters
		Vector<SMIValue> datas = new Vector<SMIValue>();
		datas.add(DataUtil.getSMIValueByObject("intEntry", "" + groupKey));
		// create parameters
		Object[] params = new Object[] { target, "cmdGroupDelete", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		// Distinguish return value
		SMIValue[] smiValues = null;
		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());

		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;

		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}
	}

	/**
	 * (102.57) 그룹에 멤버를 삭제한다
	 * 
	 * @param mcuId
	 * @param groupKey
	 * @param modemId
	 * @throws FMPMcuException
	 * @throws Exception
	 *             IF4ERR_INVALID_PARAM : 잘못된 Parameter가 전달
	 *             IF4ERR_GROUP_DB_OPEN_FAIL : 그룹 DB의 Open 실패
	 *             IF4ERR_TRANSACTION_UPDATE_FAIL : Transaction 갱신 실패
	 *             IF4ERR_TRANSACTION_DELETE_FAIL : Transaction 삭제 실패
	 *             IF4ERR_GROUP_INVALID_MEMBER : 해당 그룹에 멤버가 존재하지 않음
	 */
	@Override
	public void cmdGroupDeleteMember(String mcuId, int groupKey, String modemId) throws FMPMcuException, Exception {

		log.debug("MCUID[" + mcuId + "], GROUPKEY[" + groupKey + "]");

		// Get MCU ID
		Target target = CmdUtil.getTarget(mcuId);

		// insert parameters
		Vector<SMIValue> datas = new Vector<SMIValue>();
		datas.add(DataUtil.getSMIValueByObject("stringEntry", "" + groupKey));
		datas.add(DataUtil.getSMIValueByObject("sensorID", modemId));

		// create parameters
		Object[] params = new Object[] { target, "cmdGroupDeleteMember", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		// Distinguish return value
		SMIValue[] smiValues = null;
		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());

		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;

		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}
	}

	/**
	 * (102.58) 현재 그룹 정보 전체를 조회한다
	 * 
	 * @param mcuId
	 * @param modemId
	 * @throws FMPMcuException
	 * @throws Exception
	 *             IF4ERR_GROUP_DB_OPEN_FAIL : 그룹 DB의 Open 실패
	 */
	@Override
	public GroupInfo[] cmdGroupInfo(String mcuId) throws FMPMcuException, Exception {

		log.debug("MCUID[" + mcuId + "]");

		// Get MCU ID
		Target target = CmdUtil.getTarget(mcuId);

		// create parameters
		Object[] params = new Object[] { target, "cmdGroupInfo", null };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		ArrayList<GroupInfo> grps = new ArrayList<GroupInfo>();
		GroupInfo[] objs = null;
		int i = 0;
		while (i < smiValues.length) {
			if (smiValues[i].getOid().getValue().equals("1.9.0")) {// groupKey
				int groupKey = (int) ((UINT) (smiValues[i++].getVariable())).getValue();
				if (smiValues[i].getOid().getValue().equals("7.1.1")) {// groupName
					String groupName = ((OCTET) (smiValues[i++].getVariable())).toString();// memberCnt
					if (smiValues[i].getOid().getValue().equals("7.1.2")) {
						int memberCnt = (int) ((UINT) (smiValues[i++].getVariable())).getValue();

						GroupInfo groupInfo = new GroupInfo();
						groupInfo.setGroupName(groupName);
						groupInfo.setMemberCount(memberCnt);
						groupInfo.setGroupKey(groupKey);

						List<MemberInfo> members = new ArrayList<MemberInfo>();
						MemberInfo member = null;

						for (int k = 0; k < memberCnt; k++) {

							member = new MemberInfo();
							byte[] memberInfo = ((OCTET) (smiValues[i++].getVariable())).getValue();

							byte[] eui64 = DataFormat.select(memberInfo, 0, 8);
							byte[] cmd = DataFormat.select(memberInfo, 8, 3);
							byte[] state = DataFormat.select(memberInfo, 11, 1);
							member.setState(new BOOL(state[0]).getValue());
							member.setCmd(new OID(cmd));
							member.setMember(new HEX(eui64).getValue());
							members.add(member);
						}
						groupInfo.setMemberInfo(members);
						grps.add(groupInfo);
					}

				}
			}
			if (grps.size() > 0) {
				objs = grps.toArray(new GroupInfo[0]);
			}
		}
		return objs;
	}

	/**
	 * (102.58) 현재 그룹 정보를 그룹명으로 조회한다
	 * 
	 * @param mcuId
	 * @param groupKey
	 * @param modemId
	 * @throws FMPMcuException
	 * @throws Exception
	 *             IF4ERR_GROUP_DB_OPEN_FAIL : 그룹 DB의 Open 실패
	 */
	@Override
	public GroupInfo[] cmdGroupInfo(String mcuId, int groupKey) throws FMPMcuException, Exception {

		log.debug("MCUID[" + mcuId + "], GROUPNAME[" + groupKey + "]");

		// Get MCU ID
		Target target = CmdUtil.getTarget(mcuId);

		// insert parameters
		Vector<SMIValue> datas = new Vector<SMIValue>();
		datas.add(DataUtil.getSMIValueByObject("intEntry", groupKey));

		// create parameters
		Object[] params = new Object[] { target, "cmdGroupInfo", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		ArrayList<GroupInfo> grps = new ArrayList<GroupInfo>();
		GroupInfo[] objs = null;
		int i = 0;
		while (i < smiValues.length) {
			if (smiValues[i].getOid().getValue().equals("1.9.0")) {// totalMemberCnt
				int totalMemberCnt = (int) ((UINT) (smiValues[i++].getVariable())).getValue();
				if (smiValues[i].getOid().getValue().equals("7.1.2")) {// groupKey
					int receiveGroupKey = (int) ((UINT) (smiValues[i++].getVariable())).getValue();
					if (smiValues[i].getOid().getValue().equals("7.1.3")) {// groupName
						String groupName = ((OCTET) (smiValues[i++].getVariable())).toString();// memberCnt
						if (smiValues[i].getOid().getValue().equals("7.1.4")) {
							int memberCnt = (int) ((UINT) (smiValues[i++].getVariable())).getValue();

							GroupInfo groupInfo = new GroupInfo();
							groupInfo.setGroupName(groupName);
							groupInfo.setMemberCount(memberCnt);
							groupInfo.setGroupKey(receiveGroupKey);

							List<MemberInfo> members = new ArrayList<MemberInfo>();
							MemberInfo member = null;

							for (int k = 0; k < memberCnt; k++) {

								member = new MemberInfo();
								byte[] memberInfo = ((OCTET) (smiValues[i++].getVariable())).getValue();

								byte[] eui64 = DataFormat.select(memberInfo, 0, 8);
								byte[] cmd = DataFormat.select(memberInfo, 8, 3);
								byte[] state = DataFormat.select(memberInfo, 11, 1);
								member.setState(new BOOL(state[0]).getValue());
								member.setCmd(new OID(cmd));
								member.setMember(new HEX(eui64).getValue());
								members.add(member);
							}
							groupInfo.setMemberInfo(members);
							grps.add(groupInfo);
						}

					}
				}
			}
			if (grps.size() > 0) {
				objs = grps.toArray(new GroupInfo[0]);
			}
		}
		return objs;
	}

	/**
	 * (102.58) 현재 그룹 정보를 모뎀이 속한 정보로 검색한다.
	 * 
	 * @param mcuId
	 * @param groupName
	 * @param modemId
	 * @param bSearchId
	 *            - 파라미터가 true이면 모뎀이 속한 그룹을 조회
	 * @throws FMPMcuException
	 * @throws Exception
	 *             IF4ERR_GROUP_DB_OPEN_FAIL : 그룹 DB의 Open 실패
	 */
	@Override
	public GroupInfo[] cmdGroupInfo(String mcuId, String modemId, boolean bSearchId) throws FMPMcuException, Exception {

		log.debug("MCUID[" + mcuId + "], modemId[" + modemId + "]");

		// Get MCU ID
		Target target = CmdUtil.getTarget(mcuId);

		// insert parameters
		Vector<SMIValue> datas = new Vector<SMIValue>();
		datas.add(DataUtil.getSMIValue(new OCTET(modemId)));
		datas.add(DataUtil.getSMIValue(new BOOL(bSearchId)));

		// create parameters
		Object[] params = new Object[] { target, "cmdGroupInfo", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		ArrayList<GroupInfo> grps = new ArrayList<GroupInfo>();
		GroupInfo[] objs = null;
		int i = 0;
		while (i < smiValues.length) {
			if (smiValues[i].getOid().getValue().equals("7.1.1")) {
				String groupName = ((OCTET) (smiValues[i++].getVariable())).toString();

				if (smiValues[i].getOid().getValue().equals("7.1.2")) {
					int memberCnt = (int) ((UINT) (smiValues[i++].getVariable())).getValue();

					GroupInfo groupInfo = new GroupInfo();
					groupInfo.setGroupName(groupName);
					groupInfo.setMemberCount(memberCnt);

					List<MemberInfo> members = new ArrayList<MemberInfo>();
					MemberInfo member = null;

					for (int k = 0; k < memberCnt; k++) {

						member = new MemberInfo();
						byte[] memberInfo = ((OCTET) (smiValues[i++].getVariable())).getValue();

						byte[] eui64 = DataFormat.select(memberInfo, 0, 8);
						byte[] cmd = DataFormat.select(memberInfo, 8, 3);
						byte[] state = DataFormat.select(memberInfo, 11, 1);
						member.setState(new BOOL(state[0]).getValue());
						member.setCmd(new OID(cmd));
						member.setMember(new HEX(eui64).getValue());
						members.add(member);
					}
					groupInfo.setMemberInfo(members);
					grps.add(groupInfo);
				}

			}
			if (grps.size() > 0) {
				objs = grps.toArray(new GroupInfo[0]);
			}
		}
		return objs;

	}

	/**
	 * cmdGetLoadControlScheme(108.1)
	 *
	 * @param sensorId
	 *            sensor id
	 * @return 11.1
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public loadControlSchemeEntry[] cmdGetLoadControlScheme(String mcuId, String modemId)
			throws FMPMcuException, Exception {

		log.debug("MCUID[" + mcuId + "], modemId[" + modemId + "]");

		// Get MCU ID
		Target target = null;
		Vector<SMIValue> datas = new Vector<SMIValue>();

		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");

			txStatus = txManager.getTransaction(null);

			Modem modem = modemDao.get(modemId);
			target = CmdUtil.getTarget(modem);

			datas.add(DataUtil.getSMIValueByObject("lcsID", modemId));

			txManager.commit(txStatus);
		} catch (Exception e) {
			if (txManager != null)
				txManager.rollback(txStatus);
			throw e;
		}

		Object[] params = new Object[] { target, "cmdGetLoadControlScheme", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;
		loadControlSchemeEntry[] lcs = null;
		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
			lcs = new loadControlSchemeEntry[smiValues.length];
			for (int i = 0; i < smiValues.length; i++) {
				lcs[i] = (loadControlSchemeEntry) ((OPAQUE) smiValues[i].getVariable()).getValue();
			}
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}
		return lcs;
	}

	/**
	 * cmdSetLoadControlScheme(108.2)
	 *
	 * @param modemId
	 * @param entryNumber
	 * @param command
	 * @param delayTime
	 * @param scheduleType
	 * @param startTime
	 * @param endTime
	 * @param weekly
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public void cmdSetLoadControlScheme(String mcuId, String modemId, int entryNumber, int command, int delayTime,
			int scheduleType, String startTime, String endTime, int weekly) throws FMPMcuException, Exception {
		log.debug("MODEM[" + modemId + "] ENTRYNUMBER[" + entryNumber + "] COMMAND[" + command + "] DELAYTIME["
				+ delayTime + "] SCHEDULETYPE[" + scheduleType + "] STARTTIME[" + startTime + "] ENDTIME[" + endTime
				+ "] WEEKLY[" + weekly + "]");

		// Get MCU ID
		Target target = null;
		Vector<SMIValue> datas = new Vector<SMIValue>();

		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");

			txStatus = txManager.getTransaction(null);

			Modem modem = modemDao.get(modemId);
			target = CmdUtil.getTarget(modem);

			loadControlSchemeEntry lcs = new loadControlSchemeEntry();
			lcs.setLcsID(new HEX(modemId));
			lcs.setLcsEntryNumber(new BYTE(entryNumber));
			lcs.setLcsCmd(new BYTE(command));
			lcs.setLcsDelayTime(new BYTE(delayTime));
			lcs.setLcsSchemeType(new BYTE(scheduleType));
			lcs.setLcsStartTime(new TIMESTAMP(startTime));
			lcs.setLcsEndTime(new TIMESTAMP(endTime));
			lcs.setLcsWeekly(new BYTE(weekly));

			datas.add(DataUtil.getSMIValueByObject("loadControlSchemeEntry", lcs));

			txManager.commit(txStatus);
		} catch (Exception e) {
			if (txManager != null)
				txManager.rollback(txStatus);
			throw e;
		}

		Object[] params = new Object[] { target, "cmdSetLoadControlScheme", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}
	}

	/**
	 * cmdGetLoadLimitProperty(108.3)
	 *
	 * @param modemId
	 *            modem id
	 * @return 11.2
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public loadLimitPropertyEntry cmdGetLoadLimitProperty(String mcuId, String modemId)
			throws FMPMcuException, Exception {
		log.debug("MODEM[" + modemId + "]");

		// Get MCU ID
		Target target = CmdUtil.getTarget(mcuId);
		Vector<SMIValue> datas = new Vector<SMIValue>();

		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");

			txStatus = txManager.getTransaction(null);

			Modem modem = modemDao.get(modemId);
			target = CmdUtil.getTarget(modem);

			datas.add(DataUtil.getSMIValueByObject("llpID", modemId));

			txManager.commit(txStatus);
		} catch (Exception e) {
			if (txManager != null)
				txManager.rollback(txStatus);
			throw e;
		}

		Object[] params = new Object[] { target, "cmdGetLoadLimitProperty", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;
		loadLimitPropertyEntry llp = null;
		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
			llp = (loadLimitPropertyEntry) ((OPAQUE) smiValues[0].getVariable()).getValue();
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}
		return llp;
	}

	/**
	 * cmdSetLoadLimitProperty(108.4)
	 *
	 * @param modemId
	 *            sensor identification
	 * @param limitType
	 *            0:voltage 1:current
	 * @param limit
	 * @param intervalNumber
	 * @param openPeriod
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public void cmdSetLoadLimitProperty(String mcuId, String modemId, int limitType, long limit, int intervalNumber,
			int openPeriod) throws FMPMcuException, Exception {
		log.debug("MODEM[" + modemId + "] LIMITTYPE[" + limitType + "] LIMIT[" + limit + "] INTERVALNUMBER["
				+ intervalNumber + "] OPENPERIOD[" + openPeriod + "]");

		// Get MCU ID
		Target target = null;
		Vector<SMIValue> datas = new Vector<SMIValue>();

		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");

			txStatus = txManager.getTransaction(null);
			Modem modem = modemDao.get(modemId);
			target = CmdUtil.getTarget(modem);

			loadLimitPropertyEntry llp = new loadLimitPropertyEntry();
			llp.setLlpID(new HEX(modemId));
			llp.setLlpLimitType(new BYTE(limitType));
			llp.setLlpLimit(new UINT(limit));
			llp.setLlpIntervalNumber(new BYTE(intervalNumber));
			llp.setLlpRestorationTime(new BYTE(openPeriod));

			datas.add(DataUtil.getSMIValueByObject("loadLimitPropertyEntry", llp));

			txManager.commit(txStatus);
		} catch (Exception e) {
			if (txManager != null)
				txManager.rollback(txStatus);
			throw e;
		}

		Object[] params = new Object[] { target, "cmdSetLoadLimitProperty", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}
	}

	/**
	 * cmdGetLoadLimitScheme(108.5)
	 *
	 * @param modemId
	 *            modem id
	 * @return 11.3
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public loadLimitSchemeEntry[] cmdGetLoadLimitScheme(String mcuId, String modemId)
			throws FMPMcuException, Exception {
		log.debug("MCUID[" + mcuId + "], modemId[" + modemId + "]");

		// Get MCU ID
		Target target = null;
		Vector<SMIValue> datas = new Vector<SMIValue>();

		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");

			txStatus = txManager.getTransaction(null);
			Modem modem = modemDao.get(modemId);
			target = CmdUtil.getTarget(modem);

			datas.add(DataUtil.getSMIValueByObject("llsID", modemId));

			txManager.commit(txStatus);
		} catch (Exception e) {
			if (txManager != null)
				txManager.rollback(txStatus);
			throw e;
		}

		Object[] params = new Object[] { target, "cmdGetLoadLimitScheme", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;
		loadLimitSchemeEntry[] lls = null;
		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
			lls = new loadLimitSchemeEntry[smiValues.length];
			for (int i = 0; i < smiValues.length; i++) {
				lls[i] = (loadLimitSchemeEntry) ((OPAQUE) smiValues[i].getVariable()).getValue();
			}
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}
		return lls;
	}

	/**
	 * cmdSetLoadLimitScheme(108.6)
	 *
	 * @param modemId
	 * @param entryNumber
	 * @param limitType
	 * @param limit
	 * @param intervalNumber
	 * @param openPeriod
	 * @param scheduleType
	 * @param startTime
	 * @param endTime
	 * @param weekly
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public void cmdSetLoadLimitScheme(String mcuId, String modemId, int entryNumber, int limitType, long limit,
			int intervalNumber, int openPeriod, int scheduleType, String startTime, String endTime, int weekly)
					throws FMPMcuException, Exception {
		log.debug("MODEM[" + modemId + "] ENTRYNUMBER[" + entryNumber + "] LIMITTYPE[" + limitType + "] LIMIT[" + limit
				+ "] INTERVALNUMBER[" + intervalNumber + "] OPENPERIOD[" + openPeriod + "] SCHEDULETYPE[" + scheduleType
				+ "] STARTTIME[" + startTime + "] ENDTIME[" + endTime + "] WEEKLY[" + weekly + "]");

		// Get MCU ID
		Target target = null;
		Vector<SMIValue> datas = new Vector<SMIValue>();

		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");

			txStatus = txManager.getTransaction(null);
			Modem modem = modemDao.get(modemId);
			target = CmdUtil.getTarget(modem);

			loadLimitSchemeEntry lls = new loadLimitSchemeEntry();
			lls.setLlsID(new HEX(modemId));
			lls.setLlsEntryNumber(new BYTE(entryNumber));
			lls.setLlsLimitType(new BYTE(limitType));
			lls.setLlsLimit(new UINT(limit));
			lls.setLlsIntervalNumber(new BYTE(intervalNumber));
			lls.setLlsRestorationTime(new BYTE(openPeriod));
			lls.setLlsSchemeType(new BYTE(scheduleType));
			lls.setLlsStartTime(new TIMESTAMP(startTime));
			lls.setLlsEndTime(new TIMESTAMP(endTime));
			lls.setLlsWeekly(new BYTE(weekly));

			datas.add(DataUtil.getSMIValueByObject("loadLimitSchemeEntry", lls));

			txManager.commit(txStatus);
		} catch (Exception e) {
			if (txManager != null)
				txManager.rollback(txStatus);
			throw e;
		}

		Object[] params = new Object[] { target, "cmdSetLoadLimitScheme", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}
	}

	/**
	 * cmdGetLoadShedSchedule(108.7)
	 *
	 * @param mcuId
	 * @return 11.4
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public loadShedSchemeEntry[] cmdGetLoadShedSchedule(String mcuId) throws FMPMcuException, Exception {
		log.debug("MCUID[" + mcuId + "]");

		// Get MCU ID
		Target target = CmdUtil.getTarget(mcuId);

		Object[] params = new Object[] { target, "cmdGetLoadShedSchedule", null };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;
		loadShedSchemeEntry[] lss = null;
		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
			lss = new loadShedSchemeEntry[smiValues.length];
			for (int i = 0; i < smiValues.length; i++) {
				lss[i] = (loadShedSchemeEntry) ((OPAQUE) smiValues[i].getVariable()).getValue();
			}
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}
		return lss;
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
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public void cmdSetLoadShedSchedule(String mcuId, int entryNumber, int checkInterval, int scheduleType,
			String startTime, String endTime, int weekly) throws FMPMcuException, Exception {
		log.debug("MCU[" + mcuId + "] ENTRYNUMBER[" + entryNumber + "] CHECKINTERVAL[" + checkInterval
				+ "] SCHEDULETYPE[" + scheduleType + "] STARTTIME[" + startTime + "] ENDTIME[" + endTime + "] WEEKLY["
				+ weekly + "]");

		// Get MCU ID
		Target target = CmdUtil.getTarget(mcuId);
		Vector<SMIValue> datas = new Vector<SMIValue>();

		loadShedSchemeEntry lss = new loadShedSchemeEntry();

		lss.setLssEntryNumber(new BYTE(entryNumber));
		// lss.setLssSupply(new UINT(supply));
		// lss.setLssSupplyThreshold(new UINT(supplyThreshold));
		lss.setLssCheckInterval(new BYTE(checkInterval));
		lss.setLssSchemeType(new BYTE(scheduleType));
		lss.setLssStartTime(new TIMESTAMP(startTime));
		lss.setLssEndTime(new TIMESTAMP(endTime));
		lss.setLssWeekly(new BYTE(weekly));

		datas.add(DataUtil.getSMIValueByObject("loadShedSchemeEntry", lss));

		Object[] params = new Object[] { target, "cmdSetLoadShedSchedule", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}
	}

	/**
	 * cmdDeleteLoadControlScheme(108.9)
	 *
	 * @param mcuId
	 * @paran modemId
	 * @param entryNumber
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public void cmdDeleteLoadControlScheme(String mcuId, String modemId, int entryNumber)
			throws FMPMcuException, Exception {
		log.debug("MCU[" + mcuId + "] MODEM [" + modemId + "] ENTRYNUMBER[" + entryNumber + "]");

		// Get MCU ID
		Target target = null;
		Vector<SMIValue> datas = new Vector<SMIValue>();

		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");

			txStatus = txManager.getTransaction(null);
			Modem modem = modemDao.get(modemId);
			target = CmdUtil.getTarget(modem);

			datas.add(DataUtil.getSMIValueByObject("lcsID", modemId));
			SMIValue smiValue = DataUtil.getSMIValue(new BYTE(entryNumber));
			datas.add(smiValue);

			txManager.commit(txStatus);
		} catch (Exception e) {
			if (txManager != null)
				txManager.rollback(txStatus);
			throw e;
		}

		Object[] params = new Object[] { target, "cmdDeleteLoadControlScheme", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}
	}

	/**
	 * cmdDeleteLoadLimitScheme(108.10)
	 *
	 * @param mcuId
	 * @paran modemId
	 * @param entryNumber
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public void cmdDeleteLoadLimitScheme(String mcuId, String modemId, int entryNumber)
			throws FMPMcuException, Exception {
		log.debug("MCU[" + mcuId + "] MODEM [" + modemId + "] ENTRYNUMBER[" + entryNumber + "]");

		// Get MCU ID
		Target target = null;
		Vector<SMIValue> datas = new Vector<SMIValue>();

		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");

			txStatus = txManager.getTransaction(null);
			Modem modem = modemDao.get(modemId);
			target = CmdUtil.getTarget(modem);

			datas.add(DataUtil.getSMIValueByObject("llsID", modemId));
			SMIValue smiValue = DataUtil.getSMIValue(new BYTE(entryNumber));
			datas.add(smiValue);

			txManager.commit(txStatus);
		} catch (Exception e) {
			if (txManager != null)
				txManager.rollback(txStatus);
			throw e;
		}

		Object[] params = new Object[] { target, "cmdDeleteLoadLimitScheme", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}
	}

	/**
	 * cmdDeleteLoadShedScheme(108.11)
	 *
	 * @param mcuId
	 * @param entryNumber
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public void cmdDeleteLoadShedScheme(String mcuId, int entryNumber) throws FMPMcuException, Exception {
		log.debug("MCU[" + mcuId + "] ENTRYNUMBER[" + entryNumber + "]");

		// Get MCU ID
		Target target = CmdUtil.getTarget(mcuId);
		Vector<SMIValue> datas = new Vector<SMIValue>();

		datas.add(DataUtil.getSMIValueByObject("lssEntryNumber", entryNumber + ""));

		Object[] params = new Object[] { target, "cmdDeleteLoadShedScheme", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}
	}

	/**
	 * cmdSetCiuLCD (109.1)
	 * 
	 * @param modemId
	 * @param ledNumber
	 * @param pageIdx
	 * @param data
	 */
	@Override
	public void cmdSetCiuLCD(String mcuId, String modemId, int ledNumber, int pageIdx, String data)
			throws FMPMcuException, Exception {
		log.debug("MODEM[" + modemId + "] LEDNUMBER[" + ledNumber + "] PAGEIDX[" + pageIdx + "] DATA[" + data + "]");

		// Get MCU ID
		Target target = null;
		Vector<SMIValue> datas = new Vector<SMIValue>();

		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");

			txStatus = txManager.getTransaction(null);
			Modem modem = modemDao.get(modemId);
			target = CmdUtil.getTarget(modem);

			datas.add(DataUtil.getSMIValueByObject("eui64Entry", modemId));
			datas.add(DataUtil.getSMIValueByObject("byteEntry", String.valueOf(ledNumber)));
			datas.add(DataUtil.getSMIValueByObject("byteEntry", String.valueOf(pageIdx)));
			// datas.add(DataUtil.getSMIValue(new OCTET(data)));
			datas.add(DataUtil.getSMIValueByObject("streamEntry", data));

			txManager.commit(txStatus);
		} catch (Exception e) {
			if (txManager != null)
				txManager.rollback(txStatus);
			throw e;
		}

		Object[] params = new Object[] { target, "cmdSetCiuLCD", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}
	}

	/**
	 * cmdACD(105.9)
	 *
	 * @param modemId
	 *            Modem Indentifier
	 * @param onoff
	 *            on/off
	 * @param delayTime
	 *            on/off delaytime
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public void cmdACD(String mcuId, String modemId, int onoff, int delayTime, int randomTime)
			throws FMPMcuException, Exception {
		log.debug(
				"MODEM[" + modemId + "] ONOFF[" + onoff + "] DELAY[" + delayTime + "] RANDOMTIME[" + randomTime + "]");

		// Get MCU ID
		Target target = null;
		Vector<SMIValue> datas = new Vector<SMIValue>();

		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");

			txStatus = txManager.getTransaction(null);
			Modem modem = modemDao.get(modemId);
			target = CmdUtil.getTarget(modem);

			datas.add(DataUtil.getSMIValueByObject("sensorID", modemId));
			datas.add(DataUtil.getSMIValueByObject("boolEntry", String.valueOf(onoff)));
			datas.add(DataUtil.getSMIValue(new UINT(delayTime)));
			datas.add(DataUtil.getSMIValue(new UINT(randomTime)));

			txManager.commit(txStatus);
		} catch (Exception e) {
			if (txManager != null)
				txManager.rollback(txStatus);
			throw e;
		}

		Object[] params = new Object[] { target, "cmdACD", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}
	}

	@Override
	public Hashtable cmdMcuScanning(String mcuId, String[] property) throws FMPMcuException, Exception {

		Hashtable res = cmdStdGet(mcuId, property);
		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");

			txStatus = txManager.getTransaction(null);
			MCU mcu = mcuDao.get(mcuId);

			if (mcu == null) {
				log.error("MCU Instancename is not exit in db");
				return null;
			}

			String key = null;
			Object value = null;
			for (Enumeration e = res.keys(); e.hasMoreElements();) {
				key = (String) e.nextElement();
				value = res.get(key);
				if (value != null)
					BeanUtils.copyProperty(mcu, key, res.get(key));
			}

			log.info("doMCUScanning MCU[" + mcu.getSysID() + "] MCUTYPE[" + mcu.getMcuType().getName() + "] hwVersion["
					+ mcu.getSysHwVersion() + "] swVersion[" + mcu.getSysSwVersion() + "] swRevision["
					+ mcu.getSysSwRevision() + "] ipAddr[" + mcu.getIpAddr() + "]");
			String mcuTypeCode = mcu.getMcuType().getCode();
			String val;
			for (int i = 0; i < res.size(); i++) {

				if (res.containsKey("geographicCoordsX")) {
					val = (String) res.get("geographicCoordsX");
					int idx = val.indexOf(",");
					if (idx > 0) {
						mcu.setGpioX(Double.valueOf(val.substring(idx + 1)));
						mcu.setGpioY(Double.valueOf(val.substring(0, idx - 1)));
					}
				}
			}

			// find Codi
			try {

				findCodi(mcuId);
			} catch (Exception e) {
				log.error(e, e);
				return null;
			}

			// find Mobile Port Information
			try {
				findMobilePort(mcuId);
			} catch (Exception e) {
				log.error(e, e);
				return null;
			}
			try {
				findLanPort(mcuId);
			} catch (Exception e) {
				log.error(e, e);
				return null;
			}

			mcuDao.update(mcu);
			txManager.commit(txStatus);

		} catch (Exception e) {
			if (txManager != null)
				txManager.rollback(txStatus);
			log.error(e, e);
			throw new Exception(e);
		}
		return res;

	}

	public void findCodi(String mcuId) throws Exception {
		codiEntry[] ces = null;

		codiEntry[] codiEntry = null;
		try {

			ces = cmdGetCodiList(mcuId);

			String codiInstanceName = null;
			for (int i = 0; i < ces.length; i++) {
				MCU mcu = mcuDao.get(mcuId);
				int codiIndex = ces[i].getCodiIndex().getValue();
				codiDeviceEntry cde = cmdGetCodiDevice(mcuId, codiIndex);
				codiBindingEntry cbe = cmdGetCodiBinding(mcuId, codiIndex);
				codiNeighborEntry cne = cmdGetCodiNeighbor(mcuId, codiIndex);
				codiMemoryEntry cme = cmdGetCodiMemory(mcuId, codiIndex);

				MCUCodiDevice cdevice = new MCUCodiDevice();
				cdevice.setCodiBaudRate(cde.getCodiBaudRate().getValue());
				cdevice.setCodiDataBit(cde.getCodiDataBit().getValue());
				cdevice.setCodiDevice(cde.getCodiDevice().toHexString());
				cdevice.setCodiID(cde.getCodiDevice().toHexString());
				cdevice.setCodiIndex(codiIndex);
				cdevice.setCodiParityBit(cde.getCodiParityBit().getValue());
				cdevice.setCodiRtsCts(cde.getCodiRtsCts().getValue());
				cdevice.setCodiStopBit(cde.getCodiStopBit().getValue());

				MCUCodiBinding cbind = new MCUCodiBinding();
				cbind.setCodiBindID(cbe.getCodiBindID().getValue());
				cbind.setCodiBindIndex(cbe.getCodiBindIndex().getValue());
				cbind.setCodiBindLocal(cbe.getCodiBindLocal().getValue());
				cbind.setCodiBindRemote(cbe.getCodiBindRemote().getValue());
				cbind.setCodiBindType(cbe.getCodiBindType().getValue());
				cbind.setCodiLastHeard(cbe.getCodiLastHeard().getValue());

				MCUCodiNeighbor cnb = new MCUCodiNeighbor();
				cnb.setCodiNeighborAge(cne.getCodiNeighborAge().getValue());
				cnb.setCodiNeighborId(cne.getCodiNeighborID().getValue());
				cnb.setCodiNeighborInCost(cne.getCodiNeighborInCost().getValue());
				cnb.setCodiNeighborIndex(cne.getCodiNeighborIndex().getValue());
				cnb.setCodiNeighborLqi(cne.getCodiNeighborLqi().getValue());
				cnb.setCodiNeighborOutCost(cne.getCodiNeighborOutCost().getValue());
				cnb.setCodiNeighborShortId(cne.getCodiNeighborShortID().getValue());

				MCUCodi codi = null;
				if (mcu.getMcuCodi() != null) {
					codi = mcu.getMcuCodi();
				} else {
					codi = new MCUCodi();
				}

				codi.setCodiMask(ces[i].getCodiMask().getValue());
				codi.setCodiIndex(ces[i].getCodiIndex().getValue());
				codi.setCodiID(ces[i].getCodiID().getValue());
				codi.setCodiType(ces[i].getCodiType().getValue());
				codi.setCodiShortID(ces[i].getCodiShortID().getValue());

				String ver = Hex.decode(ces[i].getCodiFwVer().encode());
				codi.setCodiFwVer(String.format("%s.%s", ver.substring(0, 1), ver.substring(1)));
				ver = Hex.decode(ces[i].getCodiHwVer().encode());
				codi.setCodiHwVer(String.format("%s.%s", ver.substring(0, 1), ver.substring(1)));
				// ver = Hex.decode(ces[i].getCodiZAIfVer().encode());
				// codi.setCodiZAIfVer(String.format("%s.%s", ver.substring(0,
				// 1), ver.substring(1)));
				codi.setCodiZAIfVer(ces[i].getCodiZAIfVer().getValue());
				// ver = Hex.decode(ces[i].getCodiZZIfVer().encode());
				// codi.setCodiZZIfVer(String.format("%s.%s", ver.substring(0,
				// 1), ver.substring(1)));
				codi.setCodiZZIfVer(ces[i].getCodiZZIfVer().getValue());
				codi.setCodiFwBuild(Hex.decode(ces[i].getCodiFwBuild().encode()));

				codi.setCodiResetKind(ces[i].getCodiResetKind().getValue());
				codi.setCodiAutoSetting(ces[i].getCodiAutoSetting().getValue() == 1 ? true : false);
				codi.setCodiChannel(ces[i].getCodiChannel().getValue());
				codi.setCodiPanID(ces[i].getCodiPanID().getValue() + "");
				codi.setCodiExtPanId(new String(ces[i].getCodiExtPanID().getValue()));

				codi.setCodiRfPower(ces[i].getCodiRfPower().getValue());
				codi.setCodiTxPowerMode(ces[i].getCodiTxPowerMode().getValue());
				codi.setCodiPermit(ces[i].getCodiPermit().getValue());
				codi.setCodiEnableEncrypt(ces[i].getCodiEnableEncrypt().getValue());
				codi.setCodiLinkKey(new String(ces[i].getCodiLineKey().getValue()));

				codi.setCodiNetworkKey(new String(ces[i].getCodiNetworkKey().getValue()));

				codi.setMcuCodiBinding(cbind);
				codi.setMcuCodiDevice(cdevice);
				codi.setMcuCodiMemory(new MCUCodiMemory());
				codi.setMcuCodiNeighbor(cnb);

				mcucodiDao.saveOrUpdate(codi);

				mcu.setMcuCodi(codi);
				log.debug("update codi mo [ codi=" + ces[i].getCodiID() + ", mcuid=" + mcuId + "]");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e, e);
			throw e;
		}
	}

	public String cmdSensorLPLogRecovery(String mdsId, String dcuNo, String modemNo, double meteringValue,
			int lpInterval, int[] lplist) throws FMPMcuException, Exception {
		double pulseConstant = Double.parseDouble(FMPProperty.getProperty("meter.pulse.constant.hmu"));

		double basePulse = meteringValue / pulseConstant;
		double lp[] = new double[lplist.length];
		for (int i = 0; i < lplist.length; i++) {
			lp[i] = lplist[i] / pulseConstant;
		}
		String response = null; // this.omniMDSaver.save(mdsId, dcuNo, modemNo,
								// modemNo + "0000", basePulse, lpInterval, lp);
		return response;
	}

	public void findMobilePort(String mcuId) throws Exception {
		log.debug("update mobile port [ mcu=" + mcuId + "]");

		MIBUtil mibUtil = MIBUtil.getInstance();

		// TODO IMPLEMENT
		/*
		 * // MOPROPERTY[] props = IUtil.getMOPropertyHasOid("MCUMobilePort");
		 * MOPROPERTY[] props = new MOPROPERTY[8]; props[0] = new
		 * MOPROPERTY("mobileID", "");
		 * props[0].setObjectId(mibUtil.getOid("mobileID").getValue()); props[1]
		 * = new MOPROPERTY("mobileIpaddr", "");
		 * props[1].setObjectId(mibUtil.getOid("mobileIpaddr").getValue());
		 * props[2] = new MOPROPERTY("mobileRxDbm", "");
		 * props[2].setObjectId(mibUtil.getOid("mobileRxDbm").getValue());
		 * props[3] = new MOPROPERTY("mobileTxDbm", "");
		 * props[3].setObjectId(mibUtil.getOid("mobileTxDbm").getValue());
		 * props[4] = new MOPROPERTY("mobilePacketLiveTime", "");
		 * props[4].setObjectId
		 * (mibUtil.getOid("mobilePacketLiveTime").getValue()); props[5] = new
		 * MOPROPERTY("mobileSendBytes", "");
		 * props[5].setObjectId(mibUtil.getOid("mobileSendBytes").getValue());
		 * props[6] = new MOPROPERTY("mobileRecvBytes", "");
		 * props[6].setObjectId(mibUtil.getOid("mobileRecvBytes").getValue());
		 * props[7] = new MOPROPERTY("mobileLastConnectTime", "");
		 * props[7].setObjectId
		 * (mibUtil.getOid("mobileLastConnectTime").getValue());
		 *
		 * MOINSTANCE mport = IUtil.makeDummyMO("MCUMobilePort");
		 * mport.addProperty(new MOPROPERTY("mcuId", new MOVALUE(mcuId)));
		 * MOPROPERTY[] res = gw.cmdStdGet(mcuId,props); for (int i = 0; i <
		 * res.length; i++) { if (res[i].getName().equals("mobileID")) {
		 * mport.addProperty(new MOPROPERTY("mobileId", res[i].getValue())); }
		 * else if (res[i].getName().equals("mobileIpaddr")) {
		 * mport.addProperty(new MOPROPERTY("packetIpaddr", res[i].getValue()));
		 * } else if (res[i].getName().equals("mobileRxDbm")) {
		 * mport.addProperty(new MOPROPERTY("rcvDBM", res[i].getValue())); }
		 * else if (res[i].getName().equals("mobileTxDbm")) {
		 * mport.addProperty(new MOPROPERTY("sendDBM", res[i].getValue())); }
		 * else if (res[i].getName().equals("mobilePacketLiveTime")) {
		 * mport.addProperty(new MOPROPERTY("pktLiveTime", res[i].getValue()));
		 * } else if (res[i].getName().equals("mobileSendBytes")) {
		 * mport.addProperty(new MOPROPERTY("sendBytes", res[i].getValue())); }
		 * else if (res[i].getName().equals("mobileRecvBytes")) {
		 * mport.addProperty(new MOPROPERTY("rcvBytes", res[i].getValue())); }
		 * else if (res[i].getName().equals("mobileLastConnectTime")) {
		 * mport.addProperty(new MOPROPERTY("lastConnDate", res[i].getValue()));
		 * } } mport.setProperty(new MOPROPERTY("id", mcuId + "_mport"));
		 *
		 * String mportInstanceName = IUtil.createInstance(mport, true);
		 *
		 * if (mcuInstanceName != null && mportInstanceName != null) { try {
		 * IUtil.createAssociation("MCUHasMobilePort", mcuInstanceName,
		 * mportInstanceName); } catch (MIException me) { if (me.getType() == 4)
		 * { log.debug("alread association ["+mcuInstanceName+
		 * ","+mportInstanceName+"]"); } else { log.debug(
		 * "create association failed : "+me); } } catch (Exception e) {
		 * log.debug("create association failed : "+e); } }
		 */
	}

	public void findLanPort(String mcuId) throws Exception {
		// TODO IMPLEMENT
		/*
		 * log.debug("update lan port [ mcu="+mcuId+"]"); CommandGWMBean gw =
		 * getCommandGW(); MIBUtil mibUtil = MIBUtil.getInstance();
		 *
		 * MOPROPERTY[] props = new MOPROPERTY[5]; props[0] = new
		 * MOPROPERTY("ethName", "");
		 * props[0].setObjectId(mibUtil.getOid("ethName").getValue()); props[1]
		 * = new MOPROPERTY("ethPhyAddr", "");
		 * props[1].setObjectId(mibUtil.getOid("ethPhyAddr").getValue());
		 * props[2] = new MOPROPERTY("ethIpAddr", "");
		 * props[2].setObjectId(mibUtil.getOid("ethIpAddr").getValue());
		 * props[3] = new MOPROPERTY("ethSubnetMask", "");
		 * props[3].setObjectId(mibUtil.getOid("ethSubnetMask").getValue());
		 * props[4] = new MOPROPERTY("ethGateway", "");
		 * props[4].setObjectId(mibUtil.getOid("ethGateway").getValue());
		 *
		 * MOINSTANCE lport = IUtil.makeDummyMO("MCULanPort");
		 * lport.addProperty(new MOPROPERTY("mcuId", new MOVALUE(mcuId)));
		 * MOPROPERTY[] res = gw.cmdStdGet(mcuId,props); for (int i = 0; i <
		 * res.length; i++) { if (res[i].getName().equals("ethPhyAddr")){
		 * log.debug("OID:"+res[i].getObjectId()); }
		 *
		 * if (res[i].getName().equals("ethPhyAddr")) { lport.addProperty(new
		 * MOPROPERTY("physicalAddress", new
		 * OCTET(res[i].getValue()).toHexString())); } else if
		 * (res[i].getName().equals("ethIpAddr")) { lport.addProperty(new
		 * MOPROPERTY("address", res[i].getValue())); } else if
		 * (res[i].getName().equals("ethSubnetMask")) { lport.addProperty(new
		 * MOPROPERTY("subnetMask", res[i].getValue())); } else if
		 * (res[i].getName().equals("ethGateway")) { lport.addProperty(new
		 * MOPROPERTY("defualtGw", res[i].getValue())); } }
		 * lport.addProperty(new MOPROPERTY("id",mcuId+"_ppp"));
		 *
		 * String lportInstanceName = IUtil.createInstance(lport, true);
		 *
		 * if (mcuInstanceName != null && lportInstanceName != null) { try {
		 * IUtil.createAssociation("MCUHasLANIF", mcuInstanceName,
		 * lportInstanceName); } catch (MIException me) { if (me.getType() == 4)
		 * { log.debug("already association ["+mcuInstanceName+
		 * ","+lportInstanceName+"]"); } else { log.debug(
		 * "create association failed : "+me); } } catch (Exception e) {
		 * log.debug("create association failed : "+e); } }
		 */
	}

	/**
	 * 모뎀의 롬 정보 읽기(펌웨어 버전,메모리정보,검침주기 등의 정보를 취득) 모뎀 타입이 지그비 타입인 경우 ZRU, 리피터,
	 * ZEUPLS, ZRU, MBUS인 경우 취득 가능
	 * 
	 * @param gw
	 * @param sensor
	 * @param mcuId
	 * @param modemId
	 * @param serviceType
	 * @param operator
	 * @return
	 * @throws Exception
	 */
	public String doGetModemROM(String mcuId, String modemId, String modemType, int serviceType, String operator)
			throws Exception {
		log.info("doGetModemROM sensor[" + modemId + "] sensor type[" + modemType + "], MCU ID :" + mcuId);
		Modem sensor = modemDao.get(Integer.parseInt(modemId));
		String fwVersion = sensor.getFwVer();
		String fwBuild = sensor.getFwRevision();
		ModemROM modemROM = new ModemROM(fwVersion, fwBuild);
		MCU mcu = mcuDao.get(mcuId);
		String revision = mcu.getSysSwRevision();
		ZEUPLS zeupls = null;
		ZRU zru = null;
		ZBRepeater repeater = null;
		ZMU zmu = null;

		if ("ZEUPLS".equalsIgnoreCase(modemType)) {
			zeupls = (ZEUPLS) sensor;
		} else if ("ZRU".equalsIgnoreCase(modemType)) {
			zru = (ZRU) sensor;
		} else if ("ZBRepeater".equalsIgnoreCase(modemType)) {
			repeater = (ZBRepeater) sensor;
		} else if ("ZMU".equalsIgnoreCase(modemType)) {
			zmu = (ZMU) sensor;
		}

		try {

			log.debug("revision: " + (revision.compareTo("2688") >= 0));
			log.debug("isAsync:" + isAsynch(sensor));
			if (revision.compareTo("2688") >= 0 && isAsynch(sensor)) {
				log.debug("here~!");
				long trId = cmdAsynchronousCall(mcuId, "eui64Entry", sensor.getModemType().name(),
						sensor.getDeviceSerial(), "cmdGetModemROM",
						(byte) TR_OPTION.ASYNC_OPT_RETURN_DATA_EVT.getCode()
								| (byte) TR_OPTION.ASYNC_OPT_RETURN_DATA_SAVE.getCode(),
						0, 0, 2,
						new String[][] { { "sensorID", sensor.getDeviceSerial() },
								{ "wordEntry", "" + ModemROM.OFFSET_MANUAL_ENABLE },
								{ "wordEntry", "" + modemROM.getNetworkSize() },
								{ "wordEntry", "" + ModemROM.OFFSET_NODEKIND },
								{ "wordEntry", "" + modemROM.getNodeSize() },
								{ "wordEntry", "" + modemROM.OFFSET_METER_SERIAL_NUMBER },
								{ "wordEntry", "" + modemROM.getAmrSize() },
								{ "wordEntry", "" + modemROM.OFFSET_METER_LPPERIOD }, { "wordEntry", "2" },
								{ "wordEntry", "" + modemROM.OFFSET_NETWORK_TYPE }, { "wordEntry", "1" } },
						serviceType, operator);
				// TODO IMPLEMENT
				// sensor.addProperty(new MOPROPERTY("commandMethod",
				// "AsynchronousCall"));
				// sensor.addProperty(new MOPROPERTY("trId", ""+trId));

			} else {
				modemROM = cmdGetModemROM(mcuId, sensor.getDeviceSerial(),
						new int[][] { { ModemROM.OFFSET_MANUAL_ENABLE, modemROM.getNetworkSize() },
								{ ModemROM.OFFSET_NODEKIND, modemROM.getNodeSize() },
								{ ModemROM.OFFSET_METER_SERIAL_NUMBER, modemROM.getAmrSize() },
								{ ModemROM.OFFSET_METER_LPPERIOD, 2 } });

				ModemNetwork sn = modemROM.getModemNetwork();
				ModemNode sNode = modemROM.getModemNode();
				AmrData amrData = modemROM.getAmrData();

				// install date
				if (sensor.getInstallDate() == null || "".equals(sensor.getInstallDate())) {
					if (zeupls != null) {
						zeupls.setInstallDate(DateTimeUtil.getCurrentDateTimeByFormat(null));
					} else if (zru != null) {
						zru.setInstallDate(DateTimeUtil.getCurrentDateTimeByFormat(null));
					} else if (repeater != null) {
						repeater.setInstallDate(DateTimeUtil.getCurrentDateTimeByFormat(null));
					} else if (zmu != null) {
						zmu.setInstallDate(DateTimeUtil.getCurrentDateTimeByFormat(null));
					}
				}

				if (sn != null) {
					if (zeupls != null) {
						zeupls.setLinkKey(sn.getLinkKey());

						zeupls.setNetworkKey(sn.getNetworkKey());
						zeupls.setExtPanId(sn.getExtPanId());
						// sensor.setProperty(new
						// MOPROPERTY("txPower",sn.getTxPower()+""));
						zeupls.setChannelId(sn.getChannel());
						zeupls.setManualEnable(sn.getManualEnable() == 255 ? false : true);
						zeupls.setPanId(sn.getPanId());
						zeupls.setSecurityEnable(sn.getSecurityEnable() == 255 ? false : true);
					} else if (zru != null) {
						zru.setLinkKey(sn.getLinkKey());
						zru.setNetworkKey(sn.getNetworkKey());
						zru.setExtPanId(sn.getExtPanId());
						zru.setChannelId(sn.getChannel());
						zru.setManualEnable(sn.getManualEnable() == 255 ? false : true);
						zru.setPanId(sn.getPanId());
						zru.setSecurityEnable(sn.getSecurityEnable() == 255 ? false : true);
					} else if (repeater != null) {
						repeater.setLinkKey(sn.getLinkKey());
						repeater.setNetworkKey(sn.getNetworkKey());
						repeater.setExtPanId(sn.getExtPanId());
						repeater.setChannelId(sn.getChannel());
						repeater.setManualEnable(sn.getManualEnable() == 255 ? false : true);
						repeater.setPanId(sn.getPanId());
						repeater.setSecurityEnable(sn.getSecurityEnable() == 255 ? false : true);
					} else if (zmu != null) {
						zmu.setLinkKey(sn.getLinkKey());
						zmu.setNetworkKey(sn.getNetworkKey());
						zmu.setExtPanId(sn.getExtPanId());
						zmu.setChannelId(sn.getChannel());
						zmu.setManualEnable(sn.getManualEnable() == 255 ? false : true);
						zmu.setPanId(sn.getPanId());
						zmu.setSecurityEnable(sn.getSecurityEnable() == 255 ? false : true);
					}

				}
				if (sNode != null) {

					log.debug("protocolVerion[" + sNode.getProtocolVersion() + "]");
					if (zeupls != null) {
						zeupls.setHwVer(sNode.getHardwareVersion());
						zeupls.setNodeKind(sNode.getNodeKind());
						zeupls.setProtocolVersion(sNode.getProtocolVersion());
						zeupls.setResetCount(sNode.getResetCount());
						zeupls.setLastResetCode(sNode.getResetReason());
						zeupls.setSwVer(sNode.getSoftwareVersion());
						zeupls.setFwVer(sNode.getFirmwareVersion());
						zeupls.setFwRevision(sNode.getFirmwareBuild());
						zeupls.setZdzdIfVersion(sNode.getZdzdInterfaceVersion());
					} else if (zru != null) {
						zru.setHwVer(sNode.getHardwareVersion());
						zru.setNodeKind(sNode.getNodeKind());
						zru.setProtocolVersion(sNode.getProtocolVersion());
						zru.setResetCount(sNode.getResetCount());
						zru.setLastResetCode(sNode.getResetReason());
						zru.setSwVer(sNode.getSoftwareVersion());
						zru.setFwVer(sNode.getFirmwareVersion());
						zru.setFwRevision(sNode.getFirmwareBuild());
						zru.setZdzdIfVersion(sNode.getZdzdInterfaceVersion());
					} else if (repeater != null) {
						repeater.setHwVer(sNode.getHardwareVersion());
						repeater.setNodeKind(sNode.getNodeKind());
						repeater.setProtocolVersion(sNode.getProtocolVersion());
						repeater.setResetCount(sNode.getResetCount());
						repeater.setLastResetCode(sNode.getResetReason());
						repeater.setSwVer(sNode.getSoftwareVersion());
						repeater.setFwVer(sNode.getFirmwareVersion());
						repeater.setFwRevision(sNode.getFirmwareBuild());
						repeater.setZdzdIfVersion(sNode.getZdzdInterfaceVersion());
					} else if (zmu != null) {
						zmu.setHwVer(sNode.getHardwareVersion());
						zmu.setNodeKind(sNode.getNodeKind());
						zmu.setProtocolVersion(sNode.getProtocolVersion());
						zmu.setResetCount(sNode.getResetCount());
						zmu.setLastResetCode(sNode.getResetReason());
						zmu.setSwVer(sNode.getSoftwareVersion());
						zmu.setFwVer(sNode.getFirmwareVersion());
						zmu.setFwRevision(sNode.getFirmwareBuild());
						zmu.setZdzdIfVersion(sNode.getZdzdInterfaceVersion());
					}

					if ((sensor.getModemType().equals(ModemType.ZEUPLS)
							|| sensor.getModemType().equals(ModemType.ZBRepeater)) && fwVersion.compareTo("2.1") >= 0
							&& fwBuild.compareTo("18") >= 0) {
						if (zeupls != null) {
							zeupls.setSolarADV(sNode.getSolarADVolt());
							zeupls.setSolarChgBV(sNode.getSolarChgBattVolt());
							zeupls.setSolarBDCV(sNode.getSolarBDCVolt());
						} else if (repeater != null) {
							repeater.setSolarADV(sNode.getSolarADVolt());
							repeater.setSolarChgBV(sNode.getSolarChgBattVolt());
							repeater.setSolarBDCV(sNode.getSolarBDCVolt());
						}

					}

				} else {
					log.debug("sNode is null!!");
				}
				if (amrData != null) {
					// sensor.setProperty(new
					// MOPROPERTY("vendor",amrData.getVendor()));

					if (zeupls != null) {
						zeupls.setTestFlag(Boolean.valueOf(amrData.getTestFlag() + ""));
						zeupls.setFixedReset(amrData.getFixedReset() + "");
					} else if (zru != null) {
						zru.setTestFlag(Boolean.valueOf(amrData.getTestFlag() + ""));
						zru.setFixedReset(amrData.getFixedReset() + "");
					} else if (repeater != null) {
						repeater.setTestFlag(Boolean.valueOf(amrData.getTestFlag() + ""));
						repeater.setFixedReset(amrData.getFixedReset() + "");
					}

					StringBuffer mask = new StringBuffer();
					for (int i = 0; i < amrData.getMeteringDay().length; i++) {
						mask.append("" + amrData.getMeteringDay()[i]);
					}

					if (zeupls != null) {
						zeupls.setMeteringDay(mask.toString());
					} else if (zru != null) {
						zru.setMeteringDay(mask.toString());
					} else if (repeater != null) {
						repeater.setMeteringDay(mask.toString());
					}
					mask.setLength(0);
					for (int i = 0; i < amrData.getMeteringHour().length; i++) {
						mask.append("" + amrData.getMeteringHour()[i]);
					}

					if (zeupls != null) {
						zeupls.setMeteringHour(mask.toString());
						zeupls.setLpChoice(amrData.getLpChoice());
					} else if (zru != null) {
						zru.setMeteringHour(mask.toString());
						zru.setLpChoice(amrData.getLpChoice());
					} else if (repeater != null) {
						repeater.setMeteringHour(mask.toString());
						repeater.setLpChoice(amrData.getLpChoice());
					}
					if (fwVersion.compareTo("2.1") >= 0 && fwBuild.compareTo("18") >= 0) {

						if (zeupls != null) {
							zeupls.setAlarmFlag(amrData.getAlarmFlag());
							zeupls.setPermitMode(amrData.getPermitMode());
							zeupls.setPermitState(amrData.getPermitState());
							zeupls.setAlarmMask(amrData.getAlarmMask());
						}
					}

					String meterId = amrData.getMeterSerialNumber();
					log.info("METERID[" + meterId + "]");
					if (meterId != null && !"".equals(meterId)) {
						Meter meter = meterDao.get(meterId);
						meterDao.get(meterId);

						if (meter != null) {
							meter.setModem(sensor);
						}
					}
				}
			}

			modemDao.update(sensor);

			return "success";
		} catch (Exception e) {
			log.error(e, e);
			throw e;
		}
	}

	/**
	 * 모뎀의 롬 정보 읽기(펌웨어 버전,메모리정보,검침주기 등의 정보를 취득) 모뎀 타입이 지그비 타입인 경우 PLC,
	 * SUBGIGA,Zigbee
	 * 
	 * @param gw
	 * @param sensor
	 * @param mcuId
	 * @param modemId
	 * @param serviceType
	 * @param operator
	 * @return
	 * @throws Exception
	 */
	public String doGetModemCluster(String mcuId, String modemId, String modemType, int serviceType, String operator)
			throws Exception {
		log.info("doGetModemCluster sensor[" + modemId + "] sensor type[" + modemType + "], MCU ID :" + mcuId);
		// MCU mcu = mcuDao.get(mcuId);

		Target target = null;
		Vector<SMIValue> datas = new Vector<SMIValue>();

		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");

			txStatus = txManager.getTransaction(null);

			Modem modem = modemDao.get(modemId);
			target = CmdUtil.getTarget(modem);

			txManager.commit(txStatus);
		} catch (Exception e) {
			if (txManager != null)
				txManager.rollback(txStatus);
			throw e;
		}

		Object[] params = new Object[] { target, "cmdGetSensorROM", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		ModemType modemTypeEnum = CommonConstants.getModemTypeName(modemType);
		datas.add(DataUtil.getSMIValueByObject("sensorID", "" + modemId));
		datas.add(DataUtil.getSMIValueByObject("byteEntry", modemTypeEnum.getIntValue()));

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}
		// Distinguish return value
		SMIValue[] smiValues = null;
		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		return "success";
	}

	public boolean isAsynch(Modem modem) throws Exception {
		if (modem.getModemType() == ModemType.ZEUPLS) {
			com.aimir.model.device.ZEUPLS zeupls = (com.aimir.model.device.ZEUPLS) modem;
			if (zeupls.getPowerType() == ModemPowerType.Battery && zeupls.getNetworkType() == ModemNetworkType.FFD)
				return true;
		} else if (modem.getModemType() == ModemType.ZBRepeater) {
			com.aimir.model.device.ZBRepeater repeater = (com.aimir.model.device.ZBRepeater) modem;
			if (repeater.getPowerType() == ModemPowerType.Battery && repeater.getNetworkType() == ModemNetworkType.FFD)
				return true;
		}
		return false;
	}

	/**
	 * (130.1)DR 프로그램 참여 유도
	 * 
	 * @param mcuId
	 * @param drLevelEntry
	 * @throws FMPMcuException
	 * @throws Exception
	 */
	public void cmdDRAgreement(String mcuId, drLevelEntry drLevelEntry) throws FMPMcuException, Exception {
		log.info(" cmdDRAgreement[" + mcuId + "], drLevelEntry[" + drLevelEntry + "]");

		// Get MCU ID
		Target target = CmdUtil.getTarget(mcuId);

		// insert parameters
		Vector<SMIValue> datas = new Vector<SMIValue>();
		datas.add(DataUtil.getSMIValueByObject("drLevelEntry", drLevelEntry));

		// create parameters
		Object[] params = new Object[] { target, "cmdDRAgreement", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		// Distinguish return value
		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}
	}

	/**
	 * (130.2)DR 취소 메시지
	 * 
	 * @param mcuId
	 * @param deviceId
	 * @throws FMPMcuException
	 * @throws Exception
	 */
	public void cmdDRCancel(String mcuId, String deviceId) throws FMPMcuException, Exception {
		log.info(" cmdDRCancel[" + mcuId + "], deviceId[" + deviceId + "]");

		// Get MCU ID
		Target target = CmdUtil.getTarget(mcuId);

		// insert parameters
		Vector<SMIValue> datas = new Vector<SMIValue>();
		datas.add(DataUtil.getSMIValueByObject("stringEntry", deviceId));

		// create parameters
		Object[] params = new Object[] { target, "cmdDRCancel", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		// Distinguish return value
		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}
	}

	/**
	 * (130.3)Incentive DR Start
	 * 
	 * @param mcuId
	 * @param idrEntry
	 * @throws FMPMcuException
	 * @throws Exception
	 */
	public void cmdIDRStart(String mcuId, idrEntry idrEntry) throws FMPMcuException, Exception {
		log.info(" cmdIDRStart[" + mcuId + "], idrEntry[" + idrEntry + "]");

		// Get MCU ID
		Target target = CmdUtil.getTarget(mcuId);

		// insert parameters
		Vector<SMIValue> datas = new Vector<SMIValue>();
		datas.add(DataUtil.getSMIValueByObject("idrEntry", idrEntry));

		// create parameters
		Object[] params = new Object[] { target, "cmdIDRStart", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		// Distinguish return value
		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}
	}

	/**
	 * (130.4)Incentive DR Cancel
	 * 
	 * @param mcuId
	 * @param eventId
	 * @throws FMPMcuException
	 * @throws Exception
	 */
	public void cmdIDRCancel(String mcuId, String eventId) throws FMPMcuException, Exception {
		log.info(" cmdIDRCancel[" + mcuId + "], eventId[" + eventId + "]");

		// Get MCU ID
		Target target = CmdUtil.getTarget(mcuId);

		// insert parameters
		Vector<SMIValue> datas = new Vector<SMIValue>();
		datas.add(DataUtil.getSMIValueByObject("stringEntry", eventId));

		// create parameters
		Object[] params = new Object[] { target, "cmdIDRCancel", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		// Distinguish return value
		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}
	}

	/**
	 * (130.5)DR Level Monitoring 장비의 DR Level 조회
	 * 
	 * @param mcuId
	 * @param deviceId
	 * @return
	 * @throws FMPMcuException
	 * @throws Exception
	 */
	public endDeviceEntry cmdGetDRLevel(String mcuId, String deviceId) throws FMPMcuException, Exception {
		log.info(" cmdGetDRLevel[" + mcuId + "], deviceId[" + deviceId + "]");

		// Get MCU ID
		Target target = CmdUtil.getTarget(mcuId);

		// insert parameters
		Vector<SMIValue> datas = new Vector<SMIValue>();
		datas.add(DataUtil.getSMIValueByObject("stringEntry", deviceId));

		// create parameters
		Object[] params = new Object[] { target, "cmdGetDRLevel", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		// Distinguish return value
		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		if (smiValues.length > 0) {
			SMIValue smiValue = smiValues[0];
			OPAQUE mdv = (OPAQUE) smiValue.getVariable();
			endDeviceEntry value = (endDeviceEntry) mdv.getValue();
			log.debug(value);

			return value;
		} else {
			log.debug("smiValues size is 0");
			return null;
		}
	}

	/**
	 * (130.6)DR Level Control DR Level 제어
	 * 
	 * @param mcuId
	 * @param endDeviceEntry
	 * @throws FMPMcuException
	 * @throws Exception
	 */
	public void cmdSetDRLevel(String mcuId, endDeviceEntry endDeviceEntry) throws FMPMcuException, Exception {
		log.info(" cmdIDRCancel[" + mcuId + "], endDeviceEntry[" + endDeviceEntry + "]");

		// Get MCU ID
		Target target = CmdUtil.getTarget(mcuId);

		// insert parameters
		Vector<SMIValue> datas = new Vector<SMIValue>();
		datas.add(DataUtil.getSMIValueByObject("endDeviceEntry", endDeviceEntry));

		// create parameters
		Object[] params = new Object[] { target, "cmdSetDRLevel", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}
	}

	/**
	 * (130.7)가전기기 스마트 기기 제어
	 * 
	 * @param mcuId
	 * @param serviceId
	 * @param deviceId
	 * @param eventId
	 * @param drLevel
	 * @throws FMPMcuException
	 * @throws Exception
	 */
	public void cmdEndDeviceControl(String mcuId, String serviceId, String deviceId, String eventId, String drLevel)
			throws FMPMcuException, Exception {
		log.info(" cmdEndDeviceControl[" + mcuId + "], serviceId[" + serviceId + "], deviceId[" + deviceId
				+ "], eventId[" + eventId + "], drLevel[" + drLevel + "]");

		// Get MCU ID
		Target target = CmdUtil.getTarget(mcuId);

		// insert parameters
		Vector<SMIValue> datas = new Vector<SMIValue>();
		datas.add(DataUtil.getSMIValueByObject("stringEntry", serviceId));
		datas.add(DataUtil.getSMIValueByObject("stringEntry", deviceId));
		datas.add(DataUtil.getSMIValueByObject("stringEntry", eventId));
		datas.add(DataUtil.getSMIValueByObject("stringEntry", drLevel));

		// create parameters
		Object[] params = new Object[] { target, "cmdEndDeviceControl", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}
	}

	// TODO 파라미터가 deviceId인 경우가 있고, groupName인 경우가 있는데 어떻게 구분할까?
	/**
	 * (130.8)DR 자원 정보 요청
	 * 
	 * @param mcuId
	 * @param deviceId
	 * @return
	 * @throws FMPMcuException
	 * @throws Exception
	 */
	public endDeviceEntry cmdGetDRAsset(String mcuId, String deviceId) throws FMPMcuException, Exception {
		log.info(" cmdGetDRAsset[" + mcuId + "], deviceId[" + deviceId + "]");

		// Get MCU ID
		Target target = CmdUtil.getTarget(mcuId);

		// insert parameters
		Vector<SMIValue> datas = new Vector<SMIValue>();
		datas.add(DataUtil.getSMIValueByObject("stringEntry", deviceId));

		// create parameters
		Object[] params = new Object[] { target, "cmdGetDRAsset", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		// Distinguish return value
		SMIValue[] smiValues = null;
		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		if (smiValues.length > 0) {
			SMIValue smiValue = smiValues[0];
			OPAQUE mdv = (OPAQUE) smiValue.getVariable();
			endDeviceEntry value = (endDeviceEntry) mdv.getValue();
			log.debug(value);

			return value;
		} else {
			log.debug("smiValues size is 0");
			return null;
		}
	}
	// TODO 구현 필요
	/*
	 * cmdSetBillingInfo cmdSetPriceInfo
	 */

	/**
	 * Broadcast start
	 * 
	 * @param mcuId
	 * @param deviceId
	 * @throws FMPMcuException
	 * @throws Exception
	 */
	public void cmdBroadcast(String mcuId, String deviceId, String message) throws FMPMcuException, Exception {
		log.info(" cmdBroadcast[" + mcuId + "], deviceId[" + deviceId + "]");

		Modem modem = modemDao.get(deviceId);
		Target target = CmdUtil.getTarget(modem);
		IoSession session = null;

		IoConnector connector = new NioSocketConnector();
		MRPClientProtocolProvider provider = new MRPClientProtocolProvider();
		if (!connector.getFilterChain().contains(LANMMIUClient.class.getName()))
			connector.getFilterChain().addLast(LANMMIUClient.class.getName(),
					new ProtocolCodecFilter(provider.getCodecFactory()));
		connector.setHandler(provider.getHandler());
		ConnectFuture future = null;
		for (;;) {
			try {
				connector.setConnectTimeoutMillis(60 * 1000);
				future = connector.connect(new InetSocketAddress(target.getIpAddr(), target.getPort()));
				future.awaitUninterruptibly();

				if (!future.isConnected()) {
					throw new Exception("not yet");
				}

				session = future.getSession();
				log.debug("SESSION CONNECTED[" + session.isConnected() + "]");

				break;
			} catch (Exception e) {
				log.error("Failed to connect. host[" + target.getIpAddr() + "] port[" + target.getPort() + "]", e);
				throw e;
			}
		}

		if (session == null)
			throw new Exception("Failed to connect. host[" + target.getIpAddr() + "] port[" + target.getPort() + "]");

		String sendMessage = message;
		if (sendMessage.length() > 56) {
			sendMessage = sendMessage.substring(0, 56);
		}
		log.debug("sendMessage=" + sendMessage);
		byte[] msg = sendMessage.getBytes();

		IoBuffer buf = IoBuffer.allocate(sendMessage.length() + 2);
		buf.put(new byte[] { 0x2E });
		buf.put(new byte[] { (byte) sendMessage.length() });
		buf.put(msg, 0, msg.length);
		buf.flip();
		Thread.sleep(1500);
		session.write(buf);

		if (session != null) {
			session.closeNow();
			future.awaitUninterruptibly();
		}

		if (connector != null) {
			if (connector.getFilterChain().contains(getClass().getName()))
				connector.getFilterChain().remove(getClass().getName());
			connector.dispose();
		}

		session = null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.aimir.fep.command.mbean.CommandGWMBean#cmdValveControl(java.lang.
	 * String, java.lang.String, int)
	 */
	public int cmdValveControl(String mcuId, String modemId, int valveStatus) throws Exception {

		log.debug("MCU[" + mcuId + "], MODEM[" + modemId + "], VALVESTATUS[" + valveStatus + "]");

		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");

			txStatus = txManager.getTransaction(null);

			Modem modem = modemDao.get(modemId);
			Target target = CmdUtil.getTarget(modem);

			Vector datas = new Vector();
			datas.add(DataUtil.getSMIValueByObject("sensorID", "" + modemId));
			datas.add(DataUtil.getSMIValue(new BYTE(valveStatus)));

			Object[] params = new Object[] { target, "cmdValveControl", datas };

			String[] types = new String[] { "java.util.Hashtable", "java.lang.String", "java.util.Vector" };

			Object obj = null;
			try {
				obj = (Object) invoke(params, types);
			} catch (Exception e) {
				log.error(e, e);
				throw new Exception(makeMessage(e.getMessage()));
			}

			SMIValue[] smiValues = null;
			if (obj instanceof Integer) {
				log.error("Error Code Return");
				throw makeMcuException(((Integer) obj).intValue());
			} else if (obj instanceof SMIValue[]) {
				smiValues = (SMIValue[]) obj;
			} else {
				log.error("Unknown Return Value");
				throw new Exception("Unknown Return Value");
			}

			int result = 0xff;
			if (smiValues != null && smiValues.length != 0) {
				log.debug(smiValues[0].toString());
				result = ((BYTE) smiValues[0].getVariable()).getValue();
			} else {
				log.debug("smiValues is null");
			}
			return result;
		} catch (Exception e) {
			log.error(e);
			throw e;
		}
	}

	@Override
	public Map<String, Object> cmdValveStatusByGW(String mcuId, String meterId) {
		log.info("mcuId[" + mcuId + "] meterId[" + meterId + "]");
		Map<String, Object> response = new HashMap<String, Object>();
		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");
			txStatus = txManager.getTransaction(null);
			MeterDao meterDao = DataUtil.getBean(MeterDao.class);
			Meter meter = meterDao.get(meterId);
			Class clazz = Class.forName(meter.getModel().getDeviceConfig().getSaverName());
			AbstractMDSaver saver = (AbstractMDSaver) DataUtil.getBean(clazz);
			response.put("Response", saver.relayValveStatus(mcuId, meterId));
			txManager.commit(txStatus);
		} catch (Exception e) {
			log.error(e, e);
			if (txManager != null) {
				try {
					txManager.rollback(txStatus);
				} catch (Exception t) {
				}
			}
		}
		return response;
	}

	/**
	 * 미터 보안 정보 조회(105.9)
	 * 
	 * @param mcuId
	 * @param type
	 *            Parser, Node, ID
	 * @param name
	 * @return list[type, name, key]
	 * @throws Exception
	 */
	@Override
	public Map<String, Object> cmdGetMeterSecurity(String mcuId, String type, String name) throws Exception {

		log.debug("MCU [" + mcuId + "], TYPE[" + type + "], NAME[" + name + "]");

		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");

			txStatus = txManager.getTransaction(null);

			Target target = CmdUtil.getTarget(mcuId);

			Vector datas = new Vector();
			if (name != null && !"".equals(name)) {
				datas.add(DataUtil.getSMIValueByObject("stringEntry", type));
				datas.add(DataUtil.getSMIValueByObject("stringEntry", name));
			}

			Object[] params = new Object[] { target, "cmdGetMeterSecurity", datas };

			String[] types = new String[] { "java.util.Hashtable", "java.lang.String", "java.util.Vector" };

			Object obj = null;
			try {
				obj = (Object) invoke(params, types);
			} catch (Exception e) {
				log.error(e, e);
				throw new Exception(makeMessage(e.getMessage()));
			}

			SMIValue[] smiValues = null;
			if (obj instanceof Integer) {
				log.error("Error Code Return");
				throw makeMcuException(((Integer) obj).intValue());
			} else if (obj instanceof SMIValue[]) {
				smiValues = (SMIValue[]) obj;
			} else {
				log.error("Unknown Return Value");
				throw new Exception("Unknown Return Value");
			}

			Map<String, Object> result = new HashMap<String, Object>();
			for (int i = 0; i < smiValues.length; i += 3) {
				result.put("type" + i, smiValues[i].getVariable().toString());
				result.put("name" + i, smiValues[i + 1].getVariable().toString());
				result.put("key" + i, ((OCTET) smiValues[i + 2].getVariable()).getValue());
			}
			return result;
		} catch (Exception e) {
			log.error(e);
			throw e;
		}

	}

	/**
	 * 미터 보안 정보 조회(105.10)
	 * 
	 * @param mcuId
	 * @param type
	 *            Parser, Node, ID
	 * @param name
	 * @param key
	 * @return list[type, name, key]
	 * @throws Exception
	 */
	@Override
	public void cmdSetMeterSecurity(String mcuId, String type, String name, String key) throws Exception {

		log.debug("MCU [" + mcuId + "], TYPE[" + type + "], NAME[" + name + "]");

		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");

			txStatus = txManager.getTransaction(null);

			Target target = CmdUtil.getTarget(mcuId);

			Vector datas = new Vector();
			datas.add(DataUtil.getSMIValueByObject("stringEntry", type));
			datas.add(DataUtil.getSMIValueByObject("stringEntry", name));
			if (key != null && !"".equals(key))
				datas.add(DataUtil.getSMIValueByObject("streamEntry", key));

			Object[] params = new Object[] { target, "cmdSetMeterSecurity", datas };

			String[] types = new String[] { "java.util.Hashtable", "java.lang.String", "java.util.Vector" };

			Object obj = null;
			try {
				obj = (Object) invoke(params, types);
			} catch (Exception e) {
				log.error(e, e);
				throw new Exception(makeMessage(e.getMessage()));
			}

			SMIValue[] smiValues = null;
			if (obj instanceof Integer) {
				log.error("Error Code Return");
				throw makeMcuException(((Integer) obj).intValue());
			} else if (obj instanceof SMIValue[]) {
				smiValues = (SMIValue[]) obj;
			} else {
				log.error("Unknown Return Value");
				throw new Exception("Unknown Return Value");
			}

		} catch (Exception e) {
			log.error(e);
			throw e;
		}

	}

	/**
	 * (105.11)
	 * 
	 * @param modemId
	 * @param mask
	 * @param currentPulse
	 * @param serialNumber
	 * @return result
	 * @throws Exception
	 *
	 * @ejb.interface-method view-type="both"
	 */
	public int cmdSetMeterConfig(String mcuId, String modemId, int mask, int currentPulse, String serialNumber)
			throws Exception {

		log.debug("MCU[" + mcuId + "], MODEM[" + modemId + "], MASK[" + mask + "], " + "CURRENTPULSE[" + currentPulse
				+ "], SERIALNUMBER[" + serialNumber + "]");

		Modem modem = modemDao.get(modemId);
		Target target = CmdUtil.getTarget(modem);

		Vector datas = new Vector();
		datas.add(DataUtil.getSMIValueByObject("sensorID", "" + modemId));
		datas.add(DataUtil.getSMIValue(new BYTE(mask)));
		datas.add(DataUtil.getSMIValue(new UINT(currentPulse)));
		datas.add(DataUtil.getSMIValueByObject("stringEntry", serialNumber));

		Object[] params = new Object[] { target, "cmdSetMeterConfig", datas };

		String[] types = new String[] { "java.util.Hashtable", "java.lang.String", "java.util.Vector" };

		Object obj = null;
		try {
			obj = (Object) invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;
		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		int result = 0xff;
		if (smiValues != null && smiValues.length != 0) {
			log.debug(smiValues[0].toString());
			result = ((BYTE) smiValues[0].getVariable()).getValue();
		} else {
			log.debug("smiValues is null");
		}
		return result;
	}

	/**
	 * (105.12)
	 * 
	 * @param modemId
	 * @param valveStatus
	 * @return function, testResult : YYMMDD + testResult (ex: 100623A)
	 *         hwVersion : (ex : H/W1.01) swVersion : (ex : S/W1.01)
	 * @throws Exception
	 *
	 * @ejb.interface-method view-type="both"
	 */
	@Deprecated
	public Map<String, Object> cmdGetMeterVersion(String mcuId, String modemId) throws Exception {

		log.debug("MCU[" + mcuId + "], MODEM[" + modemId + "]");

		Modem modem = modemDao.get(modemId);
		Target target = CmdUtil.getTarget(modem);

		Vector datas = new Vector();
		datas.add(DataUtil.getSMIValueByObject("sensorID", "" + modemId));

		Object[] params = new Object[] { target, "cmdGetMeterVersion", datas };

		String[] types = new String[] { "java.util.Hashtable", "java.lang.String", "java.util.Vector" };

		Object obj = null;
		try {
			obj = (Object) invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;
		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		Map<String, Object> result = new HashMap<String, Object>();
		if (smiValues != null && smiValues.length == 3) {
			for (int i = 0; i < smiValues.length; i++) {
				log.debug(smiValues[i].toString());
			}

			result.put("testResult", smiValues[0].getVariable().toString());
			result.put("hwVersion", smiValues[1].getVariable().toString());
			result.put("swVerison", smiValues[2].getVariable().toString());
		} else {
			log.debug("smiValues is wrong");
		}
		return result;
	}

	/**
	 * cmdGetEnergyLevel 104.14
	 * 
	 * @param sensorId
	 *            Sensor ID
	 * @throws Exception
	 */
	@Override
	public List cmdGetDRAssetInfo(String mcuId, String sensorId, String parser) throws Exception {
		log.debug("SENSORID[" + sensorId + "]");

		// Get MCU ID
		Target target = CmdUtil.getTarget(mcuId);
		System.out.println(mcuId + ":" + target);
		// insert parameters
		Vector<SMIValue> datas = new Vector<SMIValue>();
		if (sensorId != null) {
			datas.add(DataUtil.getSMIValueByObject("eui64Entry", sensorId));
		} else if (parser != null) {
			datas.add(DataUtil.getSMIValueByObject("stringEntry", parser));
		}
		// create parameters
		Object[] params = new Object[] { target, "cmdGetDRAssetInfo", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };
		Object obj = null;
		try {
			obj = invoke(params, types);
			log.error(obj);////
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}
		// Distinguish return value
		SMIValue[] smiValues = null;
		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		List<drAssetEntry> list = new ArrayList<drAssetEntry>();

		if (smiValues.length > 0) {
			for (int i = 0; i < smiValues.length;) {
				drAssetEntry dr = new drAssetEntry();
				dr.setId((HEX) smiValues[i++].getVariable());
				dr.setbDRAsset((BOOL) smiValues[i++].getVariable());
				dr.setnCurrentLevel((BYTE) smiValues[i++].getVariable());
				dr.setLastUpdate((TIMESTAMP) smiValues[i++].getVariable());
				dr.setnLevelCount((BYTE) smiValues[i++].getVariable());
				if (Integer.parseInt(dr.getnLevelCount().toString()) > 0) {
					dr.setaLevelArray(Hex.decode(((OCTET) smiValues[i++].getVariable()).getValue()));
				}
				list.add(dr);
				log.debug(dr.toString());
			}

			return list;
		} else {
			log.debug("smiValues size is 0");
			return null;
		}
	}

	/**
	 * cmdGetEnergyLevel 104.15
	 * 
	 * @param sensorId
	 *            Sensor ID
	 * @throws Exception
	 */
	@Override
	public byte cmdGetEnergyLevel(String mcuId, String sensorId) throws Exception {
		log.debug("SENSORID[" + sensorId + "]");

		// Get MCU ID
		Target target = CmdUtil.getTarget(mcuId);
		System.out.println(mcuId + ":" + target);
		// insert parameters
		Vector<SMIValue> datas = new Vector<SMIValue>();
		datas.add(DataUtil.getSMIValueByObject("eui64Entry", sensorId));

		// create parameters
		Object[] params = new Object[] { target, "cmdGetEnergyLevel", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };
		Object obj = null;
		try {
			obj = invoke(params, types);
			log.error(obj);////
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}
		// Distinguish return value
		SMIValue[] smiValues = null;
		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		if (smiValues.length > 0) {
			SMIValue smiValue = smiValues[0];
			byte value = (byte) ((BYTE) smiValues[0].getVariable()).getValue();
			log.debug(value);
			log.debug(smiValue.toString());
			return value;
		} else {
			log.debug("smiValues size is 0");
			return (Byte) null;
		}
	}

	/**
	 * cmdGetEnergyLevel 104.16
	 * 
	 * @param sensorId
	 *            Sensor ID
	 * @throws Exception
	 */
	@Override
	public void cmdSetEnergyLevel(String mcuId, String sensorId, String level) throws Exception {
		log.debug("SENSORID[" + sensorId + "]");

		// Get MCU ID
		Target target = CmdUtil.getTarget(mcuId);
		System.out.println(mcuId + ":" + target);
		// insert parameters
		Vector<SMIValue> datas = new Vector<SMIValue>();
		datas.add(DataUtil.getSMIValueByObject("eui64Entry", sensorId));
		datas.add(DataUtil.getSMIValueByObject("byteEntry", level));
		datas.add(DataUtil.getSMIValueByObject("streamEntry", null));

		// create parameters
		Object[] params = new Object[] { target, "cmdSetEnergyLevel", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };
		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}
		// Distinguish return value
		SMIValue[] smiValues = null;
		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}
	}

	/**
	 * cmdGetEnergyLevel 104.16
	 * 
	 * @param sensorId
	 *            Sensor ID
	 * @throws Exception
	 */
	@Override
	public void cmdSetEnergyLevel(String mcuId, String sensorId, String level, String meterSerial) throws Exception {
		log.debug("SENSORID[" + sensorId + "]");

		// Get MCU ID
		Target target = CmdUtil.getTarget(mcuId);
		System.out.println(mcuId + ":" + target);
		// insert parameters
		Vector<SMIValue> datas = new Vector<SMIValue>();
		datas.add(DataUtil.getSMIValueByObject("eui64Entry", sensorId));
		datas.add(DataUtil.getSMIValueByObject("byteEntry", level));
		datas.add(DataUtil.getSMIValueByObject("streamEntry", null));
		SMIValue smiValue = DataUtil.getSMIValueByObject("deviceId", meterSerial);
		datas.add(smiValue);
		// create parameters
		Object[] params = new Object[] { target, "cmdSetEnergyLevel", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };
		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}
		// Distinguish return value
		SMIValue[] smiValues = null;
		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}
	}

	/**
	 * cmdSendMessage 111.4
	 * 
	 * @param id
	 *            TargetId
	 * @param nMessageId
	 *            Message ID
	 * @param nMessageType
	 *            Message Type
	 * @param nDuration
	 *            Lazy, Passive
	 * @param nErrorHandler
	 *            Error handler Action Code
	 * @param nPreHandler
	 *            Pre-Action Handler
	 * @param nPostHandler
	 *            Post-Action Handler
	 * @param nUseData
	 *            User Data
	 * @param pszData
	 *            Message
	 * @throws Exception
	 */
	@Override
	public void cmdSendMessage(String mcuId, String sensorId, int messageId, int messageType, int duration,
			int errorHandler, int preHandler, int postHandler, int userData, String pszData) throws Exception {
		log.debug("SENSORID[" + sensorId + "]");

		// Get MCU ID
		Target target = CmdUtil.getTarget(mcuId);
		System.out.println(mcuId + ":" + target);
		// insert parameters
		Vector<SMIValue> datas = new Vector<SMIValue>();
		datas.add(DataUtil.getSMIValueByObject("eui64Entry", sensorId));
		datas.add(DataUtil.getSMIValueByObject("uintEntry", messageId + ""));
		datas.add(DataUtil.getSMIValueByObject("byteEntry", messageType + ""));
		datas.add(DataUtil.getSMIValueByObject("uintEntry", duration + ""));
		datas.add(DataUtil.getSMIValueByObject("uintEntry", errorHandler + ""));
		datas.add(DataUtil.getSMIValueByObject("uintEntry", preHandler + ""));
		datas.add(DataUtil.getSMIValueByObject("uintEntry", postHandler + ""));
		datas.add(DataUtil.getSMIValueByObject("uintEntry", userData + ""));
		datas.add(DataUtil.getSMIValueByObject("streamEntry", pszData));

		// create parameters
		Object[] params = new Object[] { target, "cmdSendMessage", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };
		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}
		// Distinguish return value
		SMIValue[] smiValues = null;
		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.aimir.fep.command.mbean.CommandGWMBean#cmdSetTOUCalendar(java.lang
	 * .String, java.lang.Object)
	 */
	public Response cmdSetTOUCalendar(String meterSerial, int MeterProgramId) throws Exception {
		// cmdWriteTable,197.101,테이블 쓰기
		log.debug("cmdSetTOUCalendar - Meter Serial [" + meterSerial + "]");

		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");

			txStatus = txManager.getTransaction(null);

			// 미터 handlear 에서 Write 할 Table group을 구분할수 있도록 key를 설정.
			final String tableGroup = "TOU";

			Meter meter = meterDao.get(meterSerial);
			if (meter == null)
				return new Response(Response.Type.ERR, "Invalid meter serial number");

			Modem modem = meter.getModem();
			if (modem == null) {
				return new Response(Response.Type.ERR, "Can not found modem info");
			}

			Target target = CmdUtil.getTarget(modem);

			Vector<Object> datas = new Vector<Object>();

			datas.add(tableGroup);

			// calendar 설정 정보가 있는 데이터
			datas.add(MeterProgramId);

			Object[] params = new Object[] { target, "cmdWriteTable", datas };

			String[] types = new String[] { "java.util.Hashtable", "java.lang.String", "java.util.Vector" };
			Object obj = null;
			try {
				obj = (Object) invoke(params, types);
			} catch (Exception e) {
				log.error(e, e);
				throw new Exception(makeMessage(e.getMessage()));
			}
			Response response = null;
			if (obj instanceof Response) {
				response = (Response) obj;
			} else {
				response = new Response(Type.ERR, "Can not found response message");
			}

			// Log
			MeterProgramLogDao touLogDao = DataUtil.getBean(MeterProgramLogDao.class);
			MeterProgramLog touLog = new MeterProgramLog();

			SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddhhmmss");
			String nowDate = sf.format(new Date());

			MeterProgramDao touProDao = DataUtil.getBean(MeterProgramDao.class);
			MeterProgram MeterProgram = touProDao.get(MeterProgramId);

			touLog.setAppliedDate(nowDate);
			touLog.setMeter(meter);
			if (response.getType() == Type.OK)
				touLog.setResult(DefaultCmdResult.SUCCESS);
			else
				touLog.setResult(DefaultCmdResult.FAILURE);
			touLog.setMeterProgram(MeterProgram);

			touLogDao.saveOrUpdate(touLog);

			return response;

		} catch (Exception e) {
			if (txManager != null)
				txManager.rollback(txStatus);
			log.error(e, e);
			throw new Exception(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aimir.fep.command.mbean.CommandGWMBean#cmdDemandReset(java.lang.
	 * Integer)
	 */
	public Response cmdDemandReset(Integer meterId) throws Exception {
		// cmdDemandReset
		log.debug("cmdDemandReset - Meter ID [" + meterId + "]");

		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;

		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");
			txStatus = txManager.getTransaction(null);
			Meter meter = meterDao.get(meterId);

			if (meter == null) {
				return new Response(Response.Type.ERR, "Invalid meter ID");
			}

			Modem modem = meter.getModem();
			if (modem == null) {
				return new Response(Response.Type.ERR, "Can not found modem info");
			}

			Target target = CmdUtil.getTarget(modem);
			Vector<Object> datas = new Vector<Object>();

			Object[] params = new Object[] { target, "cmdDemandReset", datas };
			String[] types = new String[] { "java.util.Hashtable", "java.lang.String", "java.util.Vector" };
			Object obj = null;

			try {
				obj = (Object) invoke(params, types);
			} catch (Exception e) {
				log.error(e, e);
				throw new Exception(makeMessage(e.getMessage()));
			}

			Response response = null;
			if (obj instanceof Response) {
				response = (Response) obj;
			} else {
				response = new Response(Type.ERR, "Can not found response message");
			}
			return response;

		} catch (Exception e) {
			if (txManager != null)
				txManager.rollback(txStatus);
			log.error(e, e);
			throw new Exception(e);
		}
	}

	@Override
	public Object cmdSendSMS(Target target, String... params) throws Exception {
		log.info(String.format("cmdSendSMS, Target[%s], params[%s]", target, params));

		// SMS 로 보내기 위해 설정
		target.setProtocol(Protocol.SMS);
		target.setTargetType(McuType.MMIU);

		// 명령 코드 설정.
		Vector<Object> datas = new Vector<Object>();
		for (String param : params) {
			datas.add(DataUtil.getSMIValue(new OCTET(param)));
		}

		Object[] oparams = new Object[] { target, "cmdSMS", datas }; // 실제
																		// SMS_Handler
																		// 에서는
																		// 사용하지
																		// 않는다.
		String[] types = new String[] { "java.util.Hashtable", "java.lang.String", "java.util.Vector" };
		Object obj = null;

		obj = (Object) invoke(oparams, types);
		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}

		return new Response(Type.OK, "SMS is sended.");

	}

	@Override
	public Object cmdSendSMS(String modemSerial, String... params) throws Exception {

		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;

		try {

			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");
			txStatus = txManager.getTransaction(null);

			Modem modem = modemDao.get(modemSerial);
			if (modem == null)
				throw new Exception("Invalid Modem serial");

			Target target = CmdUtil.getTarget(modem);
			return cmdSendSMS(target, params);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(e);
		} finally {
			txManager.commit(txStatus);
		}
	}

	@Override
	public Response cmdBypassTimeSync(String modemSerial, String loginId) throws Exception {
		log.info("cmdBypassTimeSync - Modem Serial [" + modemSerial + "]");

		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;

		try {

			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");
			txStatus = txManager.getTransaction(null);

			Modem modem = modemDao.get(modemSerial);
			if (modem.getModemType() != ModemType.MMIU) {
				throw new Exception("Invalid Modem Type");
			}
			Target target = CmdUtil.getTarget(modem);

			Object obj = null;

			try {
				obj = cmdSendSMS(target, RequestFrame.CMD_BYPASS, String.valueOf(SMSClient.getSEQ()), RequestFrame.BG,
						Bypass.OPEN);

			} catch (Exception e) {
				throw new Exception(e);
			}
			if (obj instanceof Integer) {
				log.error("Error Code Return : " + obj);
				throw makeMcuException(((Integer) obj).intValue());
			}

			// bypass명령 등록.
			BypassRegister br = BypassRegister.getInstance();
			// FIXME
			// br.add(modemSerial, bypass);

			return new Response(Type.OK, "Waiting on completion");
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(e);
		} finally {
			txManager.commit(txStatus);
		}
	}

	@Override
	public Response cmdSmsFirmwareUpdate(String modemSerial, String filePath) throws Exception {
		log.info(String.format("cmdSmsFirmwareUpdate - Modem Serial[%s], File Path[%s]", modemSerial, filePath));

		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;

		try {

			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");
			txStatus = txManager.getTransaction(null);

			Modem modem = modemDao.get(modemSerial);
			if (modem.getModemType() != ModemType.MMIU) {
				throw new Exception("Invalid Modem Type");
			}
			Target target = CmdUtil.getTarget(modem);

			Object obj = null;

			try {
				String port = FMPProperty.getProperty("firmware.ftp.port", "21");
				String user = FMPProperty.getProperty("firmware.ftp.user", "aimir");
				String pass = FMPProperty.getProperty("firmware.ftp.pass", "aimir");
				String path = filePath;

				obj = cmdSendSMS(target, RequestFrame.CMD_DOTA, String.valueOf(SMSClient.getSEQ()), RequestFrame.BG,
						port, user, pass, path);

			} catch (Exception e) {
				throw new Exception(e);
			}
			if (obj instanceof Integer) {
				log.error("Error Code Return : " + obj);
				throw makeMcuException(((Integer) obj).intValue());
			}

			return new Response(Type.OK, "Waiting on completion");
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(e);
		} finally {
			txManager.commit(txStatus);
		}
	}

	@Override
	public Response cmdMeterProgram(String meterSerial, Object valueObject, MeterProgram mp) throws Exception {
		// cmdWriteTable,197.101,테이블 쓰기
		log.debug(String.format("cmdMeterWriteTable - Meter Serial:%s, Table type:,%s", meterSerial,
				mp.getKind().getName()));

		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		// Log
		MeterProgramLogDao mpLogDao = DataUtil.getBean(MeterProgramLogDao.class);
		MeterProgramLog mpLog = new MeterProgramLog();

		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");

			txStatus = txManager.getTransaction(null);

			Meter meter = meterDao.get(meterSerial);
			if (meter == null)
				return new Response(Response.Type.ERR, "Invalid meter serial number");

			// meter program Log
			SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddhhmmss");
			String nowDate = sf.format(new Date());
			mpLog.setAppliedDate(nowDate);
			mpLog.setMeter(meter);
			mpLog.setMeterProgram(mp);

			Modem modem = meter.getModem();
			if (modem == null) {
				return new Response(Response.Type.ERR, "Can not found modem info");
			}

			Target target = CmdUtil.getTarget(modem);

			Vector<Object> datas = new Vector<Object>();

			datas.add(mp.getKind());

			// calendar 설정 정보가 있는 데이터
			datas.add(valueObject);

			Object[] params = new Object[] { target, "cmdWriteTable", datas };

			String[] types = new String[] { "java.util.Hashtable", "java.lang.String", "java.util.Vector" };
			Object obj = null;
			try {
				obj = (Object) invoke(params, types);
			} catch (Exception e) {
				log.error(e, e);
				throw new Exception(makeMessage(e.getMessage()));
			}
			Response response = null;
			if (obj instanceof Response) {
				response = (Response) obj;
			} else {
				response = new Response(Type.ERR, "Can not found response message");
			}

			if (response.getType() == Type.OK)
				mpLog.setResult(DefaultCmdResult.SUCCESS);
			else
				mpLog.setResult(DefaultCmdResult.FAILURE);

			return response;

		} catch (Exception e) {
			if (txManager != null)
				txManager.rollback(txStatus);
			mpLog.setResult(DefaultCmdResult.FAILURE);
			log.error(e, e);
			throw new Exception(e);
		} finally {
			log.debug("save meter program log :" + mpLog);
			mpLogDao.saveOrUpdate(mpLog);
			txManager.commit(txStatus);
		}
	}

	@Override
	public Response cmdBypassMeterProgram(String meterSerial, MeterProgramKind kind) throws Exception {

		log.debug(String.format("cmdBypassMeterProgram - Meter Serial:%s, Program:%s", meterSerial, kind.getName()));

		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;

		try {

			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");
			txStatus = txManager.getTransaction(null);

			// Bypass mode 로 전환하기 위해 SMS 명령 보내는 로직.
			Meter meter = meterDao.get(meterSerial);
			Modem modem = meter.getModem();
			if (modem.getModemType() != ModemType.MMIU) {
				throw new Exception("Invalid Modem Type");
			}
			final Target target = CmdUtil.getTarget(modem);

			Object obj = null;

			try {
				obj = cmdSendSMS(target, RequestFrame.CMD_BYPASS, String.valueOf(SMSClient.getSEQ()), RequestFrame.BG,
						Bypass.OPEN);

			} catch (Exception e) {
				log.error(e, e);
			}
			if (obj instanceof Integer) {
				log.error("Error Code Return : " + obj);
				// throw makeMcuException(((Integer) obj).intValue());
			}
			// SMS Response 없다.

			// bypass명령 등록.
			MeterConfig meterConfig = (MeterConfig) meter.getModel().getDeviceConfig();

			MeterProgramDao mpDao = DataUtil.getBean(MeterProgramDao.class);
			MeterProgram mp = mpDao.getMeterConfigId(meterConfig.getId(), kind);

			// Meter Program 이 없으면 예외 처리한다.
			if (mp == null) {
				throw new Exception("Can not found MeterProgram");
			}

			String modemSerial = modem.getDeviceSerial();

			// bypass log(명령) 등록
			BypassRegister bs = BypassRegister.getInstance();
			bs.add(modemSerial, mp);

			log.info("cmdBypassMeterProgram End");
			return new Response(Type.OK, "Waiting on completion");
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(e);
		} finally {
			txManager.commit(txStatus);
		}
	}

	@Override
	public void cmdMeterFactoryReset(String mcuId, String sensorId) throws FMPMcuException, Exception {
		log.info("cmdMeterFactoryReset");

		// reset 명령
		int sensorCommand = (byte) 0x15;
		int firmwareVersion = 0;
		int buildNumber = 0;

		// 강제 여부
		boolean force = false;

		String commandDataStream = new String(new byte[] { 0x00, 0x01 });

		Target target = CmdUtil.getTarget(mcuId);

		Vector<SMIValue> datas = new Vector<SMIValue>();

		// sensor id
		datas.add(DataUtil.getSMIValueByObject("eui64Entry", sensorId));

		// sendsor command
		datas.add(DataUtil.getSMIValueByObject("byteEntry", String.valueOf(sensorCommand)));

		// firmwareVersion
		datas.add(DataUtil.getSMIValueByObject("wordEntry", String.valueOf(firmwareVersion)));

		// buildNumber
		datas.add(DataUtil.getSMIValueByObject("wordEntry", String.valueOf(buildNumber)));

		// force
		datas.add(DataUtil.getSMIValueByObject("boolEntry", force == false ? "0" : "1"));

		// Command Data Stream
		datas.add(DataUtil.getSMIValueByObject("streamEntry", commandDataStream));

		Object[] params = new Object[] { target, "cmdSensorCommand", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}
	}

	/**
	 * INSTALL CODI Information Set.
	 * 
	 * @param mcuId
	 * @param sensorID
	 * @return
	 * @throws FMPMcuException
	 * @throws Exception
	 */
	@Override
	public String cmdSetIHDTable(String mcuId, String sensorId) throws FMPMcuException, Exception {

		log.debug("cmdSetIHDTable(" + mcuId + ")");

		Target target = CmdUtil.getTarget(mcuId);
		Vector<SMIValue> datas = new Vector<SMIValue>();

		SMIValue smiValue = null;

		smiValue = DataUtil.getSMIValueByObject("sensorID", sensorId);
		datas.add(smiValue);

		Object[] params = new Object[] { target, "cmdSetIHDTable", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };
		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		return "";
	}

	/**
	 * INSTALL CODI Information Del.
	 * 
	 * @param mcuId
	 * @param sensorID
	 * @return
	 * @throws FMPMcuException
	 * @throws Exception
	 */
	@Override
	public String cmdDelIHDTable(String mcuId, String sensorId) throws FMPMcuException, Exception {

		log.debug("cmdDelIHDTable(" + mcuId + ")");

		Target target = CmdUtil.getTarget(mcuId);
		Vector<SMIValue> datas = new Vector<SMIValue>();

		SMIValue smiValue = null;

		smiValue = DataUtil.getSMIValueByObject("sensorID", sensorId);
		datas.add(smiValue);

		Object[] params = new Object[] { target, "cmdDelIHDTable", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };
		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		return "";
	}

	/**
	 * cmdSendIHDData 111.4
	 * 
	 * @param id
	 *            TargetId
	 * @param nMessageId
	 *            Message ID
	 * @param IHDData
	 * @throws Exception
	 */
	@Override
	public void cmdSendIHDData(String mcuId, String sensorId, byte[] IHDData) throws Exception {
		log.debug("SENSORID[" + sensorId + "]");

		// Get MCU ID
		Target target = CmdUtil.getTarget(mcuId);
		System.out.println(mcuId + ":" + target);
		// insert parameters
		Vector<SMIValue> datas = new Vector<SMIValue>();
		datas.add(DataUtil.getSMIValueByObject("eui64Entry", sensorId));
		datas.add(DataUtil.getSMIValueByObject("uintEntry", null));
		datas.add(DataUtil.getSMIValueByObject("byteEntry", "1"));
		datas.add(DataUtil.getSMIValueByObject("uintEntry", null));
		datas.add(DataUtil.getSMIValueByObject("uintEntry", null));
		datas.add(DataUtil.getSMIValueByObject("uintEntry", null));
		datas.add(DataUtil.getSMIValueByObject("uintEntry", null));
		datas.add(DataUtil.getSMIValueByObject("uintEntry", null));
		datas.add(DataUtil.getSMIValue(new OCTET(IHDData)));

		// create parameters
		Object[] params = new Object[] { target, "cmdSendMessage", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };
		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}
		// Distinguish return value
		SMIValue[] smiValues = null;
		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.aimir.fep.command.mbean.CommandGWMBean#sendBypassOpenSMS(java.lang.
	 * String) FIXME:나중에 삭제해야됨
	 */
	@Override
	public String sendBypassOpenSMS(String modemSerial) {
		Modem modem = modemDao.get(modemSerial);

		if (modem.getModemType() != ModemType.MMIU) {
			return "this not MMIU";
		}

		try {

			final Target target = CmdUtil.getTarget(modem);

			Object obj = null;

			obj = cmdSendSMS(target, RequestFrame.CMD_BYPASS, String.valueOf(SMSClient.getSEQ()), RequestFrame.BG,
					Bypass.OPEN);

			if (obj instanceof Integer) {
				log.error("Error Code Return : " + obj);
				// throw makeMcuException(((Integer) obj).intValue());
				return "Error Code Return : " + obj;
			}

		} catch (Exception e) {
			log.error(e, e);
			return "error";
		}
		return "Success";
	}

	/**
	 * (105.11) 우즈벡용 극동 미터에서 사용하고 있는 OID 중복으로 인한 재정의된 OID Command
	 * 
	 * @param modemId
	 * @param valueStatus
	 *            : Value Status(On/Off)
	 * 
	 * @return None
	 * 
	 * @throws Exception
	 *             IF4ERR_INVALID_PARAM 잘못된 인자 전달 IF4ERR_CANCNOT_CONNECT
	 *             Connection 실패 IF4ERR_CANNOT_GET 처리 중 오류 IF4ERR_INVALID_TYPE
	 *             극동 미터가 아닐 경우
	 */
	@Override
	public void cmdKDValveControl(String modemId, int valueStatus) throws Exception {
		log.debug("cmdKDValveControl MODEM [" + modemId + "], VALUESTATUS[" + valueStatus + "]");

		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");

			txStatus = txManager.getTransaction(null);

			Modem modem = modemDao.get(modemId);
			Target target = CmdUtil.getTarget(modem);

			Vector datas = new Vector();
			datas.add(DataUtil.getSMIValueByObject("sensorID", modemId));
			datas.add(DataUtil.getSMIValue(new BYTE(valueStatus)));

			Object[] params = new Object[] { target, "cmdKDValveControl", datas };

			String[] types = new String[] { "java.util.Hashtable", "java.lang.String", "java.util.Vector" };

			Object obj = null;
			try {
				obj = (Object) invoke(params, types);
			} catch (Exception e) {
				log.error(e, e);
				throw new Exception(makeMessage(e.getMessage()));
			}

			SMIValue[] smiValues = null;
			if (obj instanceof Integer) {
				log.error("Error Code Return");
				throw makeMcuException(((Integer) obj).intValue());
			} else if (obj instanceof SMIValue[]) {
				smiValues = (SMIValue[]) obj;
			} else {
				log.error("Unknown Return Value");
				throw new Exception("Unknown Return Value");
			}
		} catch (Exception e) {
			log.error(e);
			throw e;
		}
	}

	/**
	 * (105.12) 우즈벡용 극동 미터에서 사용하고 있는 OID 중복으로 인한 재정의된 OID Command
	 * 
	 * @param modemId
	 * 
	 * @return ResponseMap( cp(UNIT 1.3) : Current Pulse (Little endian)
	 *         serial(STRING 1.11) : Meter Serial astatus(BYTE 1.4) : Alarm
	 *         status mstatus(BYTE 1.4) : Meter status)
	 * 
	 * @throws Exception
	 *             IF4ERR_INVALID_PARAM 잘못된 인자 전달 IF4ERR_CANCNOT_CONNECT
	 *             Connection 실패 IF4ERR_CANNOT_GET 처리 중 오류 IF4ERR_INVALID_TYPE
	 *             극동 미터가 아닐 경우
	 */
	@Override
	public Map<String, Object> cmdKDGetMeterStatus(String modemId) throws Exception {
		log.debug("cmdKDGetMeterStatus MODEM [" + modemId + "]");

		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");

			txStatus = txManager.getTransaction(null);

			Modem modem = modemDao.get(modemId);
			Target target = CmdUtil.getTarget(modem);

			Vector datas = new Vector();
			if (modemId != null && !"".equals(modemId)) {
				datas.add(DataUtil.getSMIValueByObject("sensorID", modemId));
			}

			Object[] params = new Object[] { target, "cmdKDGetMeterStatus", datas };

			String[] types = new String[] { "java.util.Hashtable", "java.lang.String", "java.util.Vector" };

			Object obj = null;
			try {
				obj = (Object) invoke(params, types);
			} catch (Exception e) {
				log.error(e, e);
				throw new Exception(makeMessage(e.getMessage()));
			}

			SMIValue[] smiValues = null;
			if (obj instanceof Integer) {
				log.error("Error Code Return");
				throw makeMcuException(((Integer) obj).intValue());
			} else if (obj instanceof SMIValue[]) {
				smiValues = (SMIValue[]) obj;
			} else {
				log.error("Unknown Return Value");
				throw new Exception("Unknown Return Value");
			}

			Map<String, Object> result = new HashMap<String, Object>();

			if (smiValues.length > 0) {
				result.put("cp", ((UINT) smiValues[0].getVariable()).getValue());
				result.put("meterSerial", smiValues[1].getVariable().toString());
				result.put("alarmStatus", ((BYTE) smiValues[2].getVariable()).getValue());
				result.put("meterStatus", ((BYTE) smiValues[3].getVariable()).getValue());
			}
			return result;
		} catch (Exception e) {
			log.error(e);
			throw e;
		}
	}

	/**
	 * (105.13) 우즈벡용 극동 미터에서 사용하고 있는 OID 중복으로 인한 재정의된 OID Command
	 * 
	 * @param modemId
	 * 
	 * @return ResponseMap( date(STRING 1.11) : Test date & accept hwver(STRING
	 *         1.11) : HW Version swver(STRING 1.11) : SW Version)
	 * @throws Exception
	 *             IF4ERR_INVALID_PARAM 잘못된 인자 전달 IF4ERR_CANCNOT_CONNECT
	 *             Connection 실패 IF4ERR_CANNOT_GET 처리 중 오류 IF4ERR_INVALID_TYPE
	 *             극동 미터가 아닐 경우
	 */
	@Override
	public Map<String, Object> cmdKDGetMeterVersion(String modemId) throws Exception {
		log.debug("cmdKDGetMeterVersion MODEM [" + modemId + "]");

		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");

			txStatus = txManager.getTransaction(null);

			Modem modem = modemDao.get(modemId);
			Target target = CmdUtil.getTarget(modem);

			Vector datas = new Vector();
			if (modemId != null && !"".equals(modemId)) {
				datas.add(DataUtil.getSMIValueByObject("sensorID", modemId));
			}

			Object[] params = new Object[] { target, "cmdKDGetMeterVersion", datas };

			String[] types = new String[] { "java.util.Hashtable", "java.lang.String", "java.util.Vector" };

			Object obj = null;
			try {
				obj = (Object) invoke(params, types);
			} catch (Exception e) {
				log.error(e, e);
				throw new Exception(makeMessage(e.getMessage()));
			}

			SMIValue[] smiValues = null;
			if (obj instanceof Integer) {
				log.error("Error Code Return");
				throw makeMcuException(((Integer) obj).intValue());
			} else if (obj instanceof SMIValue[]) {
				smiValues = (SMIValue[]) obj;
			} else {
				log.error("Unknown Return Value");
				throw new Exception("Unknown Return Value");
			}

			Map<String, Object> result = new HashMap<String, Object>();
			if (smiValues.length > 0) {
				result.put("date", smiValues[0].getVariable().toString());
				result.put("hwVer", smiValues[1].getVariable().toString());
				result.put("swVer", smiValues[2].getVariable().toString());
			}

			return result;
		} catch (Exception e) {
			log.error(e);
			throw e;
		}
	}

	/**
	 * (105.14) 우즈벡용 극동 미터에서 사용하고 있는 OID 중복으로 인한 재정의된 OID Command
	 * 
	 * @param modemId
	 * @param mask
	 *            mask가 0x00일 경우 Meter Serial을 사용하지 않는다.
	 * @param cp
	 *            Meter Pulse(Little endia)
	 * @param meterId
	 *            Meter Serial(Optional) : mask가 0x00이 아닐 경우에는 반드시 전달 되어야 한다.
	 * 
	 * @return None
	 * 
	 * @throws Exception
	 *             IF4ERR_INVALID_PARAM 잘못된 인자 전달 IF4ERR_CANCNOT_CONNECT
	 *             Connection 실패 IF4ERR_CANNOT_GET 처리 중 오류 IF4ERR_INVALID_TYPE
	 *             극동 미터가 아닐 경우
	 */
	@Override
	public void cmdKDSetMeterConfig(String modemId, byte mask, int cp, String meterId) throws Exception {
		log.debug("cmdKDSetMeterConfig MODEM [" + modemId + "], MASK[" + mask + "], CP[" + cp + "], METER[" + meterId
				+ "]");

		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");

			txStatus = txManager.getTransaction(null);

			Modem modem = modemDao.get(modemId);
			Target target = CmdUtil.getTarget(modem);

			Vector datas = new Vector();
			if (modemId != null && !"".equals(modemId)) {
				datas.add(DataUtil.getSMIValueByObject("sensorID", modemId));
				datas.add(DataUtil.getSMIValue(new BYTE(mask)));
				datas.add(DataUtil.getSMIValue(new UINT(cp)));
				datas.add(DataUtil.getSMIValueByObject("stringEntry", meterId));
			}

			Object[] params = new Object[] { target, "cmdKDSetMeterConfig", datas };

			String[] types = new String[] { "java.util.Hashtable", "java.lang.String", "java.util.Vector" };

			Object obj = null;
			try {
				obj = (Object) invoke(params, types);
			} catch (Exception e) {
				log.error(e, e);
				throw new Exception(makeMessage(e.getMessage()));
			}

			SMIValue[] smiValues = null;
			if (obj instanceof Integer) {
				log.error("Error Code Return");
				throw makeMcuException(((Integer) obj).intValue());
			} else if (obj instanceof SMIValue[]) {
				smiValues = (SMIValue[]) obj;
			} else {
				log.error("Unknown Return Value");
				throw new Exception("Unknown Return Value");
			}
		} catch (Exception e) {
			log.error(e);
			throw e;
		}
	}

	/**
	 * cmdOnDemandMeter (OID: NG_110.2.2)
	 *
	 * @param mcuId
	 *            MCU Indentifier
	 * @param meterId
	 * @param nOption
	 *            (3~7)
	 * @return resultData
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public MeterData cmdOnDemandByMeter(String mcuId, String meterId, String modemId, String nOption, String fromDate,
			String toDate) throws FMPMcuException, Exception {
		log.debug("cmdOnDemandByMeter(" + mcuId + "," + meterId + "," + modemId + "," + nOption + "," + fromDate + ","
				+ toDate + ")");

		Target target = null;
		Meter meter = null;
		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");
			txStatus = txManager.getTransaction(null);

			meter = meterDao.get(meterId);
			Modem modem = modemDao.get(modemId);
			ModemType modemType = modem.getModemType();
			target = CmdUtil.getTarget(modem);
			mcuId = target.getTargetId();

			DeviceModel model = meter.getModel();
			log.debug("meterModel="+model.getId() + ", lpInterval=" + meter.getLpInterval());
			// log.debug("meterVendor="+model.getDeviceVendor().getName());
			int lpInterval = meter.getLpInterval();

			Vector<SMIValue> datas = new Vector<SMIValue>();
			SMIValue smiValue = null;

			smiValue = DataUtil.getSMIValue(target.getNameSpace(),
					new INT(Integer.parseInt(nOption == null || nOption.equals("") ? "0" : nOption)));
			datas.add(smiValue);

			if (nOption.equals(OnDemandOption.READ_DEFAULT_METERING.getCode() + "") || "".equals(nOption)) {
				int[] offsetCount = CmdUtil.convertOffsetCount(model, lpInterval, modemType, fromDate, toDate);

				if (offsetCount[0] != 0) {
					smiValue = DataUtil.getSMIValue(target.getNameSpace(), new INT(offsetCount[0]));
					datas.add(smiValue);

					if (offsetCount[1] != 0) {
						smiValue = DataUtil.getSMIValue(target.getNameSpace(), new INT(offsetCount[1]));
						datas.add(smiValue);
					}
				}
			} else {
				int[] offsetCount = new int[] { 0, 4 };
				smiValue = DataUtil.getSMIValue(target.getNameSpace(), new INT(offsetCount[0]));
				datas.add(smiValue);
				smiValue = DataUtil.getSMIValue(target.getNameSpace(), new INT(offsetCount[1]));
				datas.add(smiValue);
			}

			// add filter
			smiValue = DataUtil.getSMIValue(target.getNameSpace(), new INT(1)); // 1.9
																				// INT
																				// Filter
																				// Metering
																				// filter
																				// meter
																				// id
			datas.add(smiValue);

			smiValue = DataUtil.getSMIValueByObject(target.getNameSpace(), "stringEntry", meterId); // Metering
																									// filter
																									// meter
																									// id
			datas.add(smiValue);

			Object[] params = new Object[] { target, "cmdOnDemandByMeter", datas };

			String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

			Object obj = null;
			try {
				obj = invoke(params, types);
			} catch (Exception e) {
				log.error(e, e);
				throw new Exception(makeMessage(e.getMessage()));
			}

			SMIValue[] smiValues = null;

			if (obj instanceof SMIValue[]) {
				smiValues = (SMIValue[]) obj;
				if (smiValues.length > 0) {
					if (smiValues.length <= 1) {
						log.error("Error Code Return");
						SMIValue errVal = smiValues[0];
						INT errorCode = (INT) errVal.getVariable();
						throw makeMcuException(errorCode.getValue());
					} else {
						smiValue = smiValues[1];
					}
				} else {
					log.error("smiValue size 0");
					throw new FMPMcuException("no value exist");
				}
			} else {
				log.error("Unknown Return Value");
				throw new FMPMcuException("Unknown Return Value");
			}

			// 통신시간을 업데이트한다.
			meter.setLastReadDate(DateTimeUtil.getDateString(new Date()));
			modem.setLastLinkTime(meter.getLastReadDate());

			MeterData ed = new MeterData();
			OPAQUE mdv = (OPAQUE) smiValue.getVariable();

			log.debug("Get Meter : return ClassName[" + mdv.getClsName() + "] MIB[" + mdv.getMIBName() + "]");

			DeviceConfig deviceConfig = meter.getModel().getDeviceConfig();
			if (deviceConfig == null)
				deviceConfig = meter.getModem().getModel().getDeviceConfig();

			MeterDataParser edp = null;
			if (deviceConfig.getOndemandParserName() != null && !"".equals(deviceConfig.getOndemandParserName()))
				edp = (MeterDataParser) Class.forName(deviceConfig.getOndemandParserName()).newInstance();
			else
				edp = (MeterDataParser) Class.forName(deviceConfig.getParserName()).newInstance();

			edp.setMeter(meter);
			edp.setMeteringTime(DateTimeUtil.getDateString(new Date()));
			edp.setOnDemand(true);
			ed.setMeterId(meterId);
			obj = mdv.getValue();
			if (obj instanceof meterDataEntryNew) {
				meterDataEntryNew value = (meterDataEntryNew) obj;

				// meter id
				OCTET ocMeterId = new OCTET(20);
				ocMeterId.setIsFixed(true);
				String meterSerial = meter.getMdsId() == null ? "" : meter.getMdsId();
				byte[] bMeterid = DataUtil.fillCopy(meterSerial.getBytes(), (byte) 0x20, 20);
				ocMeterId.setValue(bMeterid);
				value.setMdSerial(ocMeterId);

				byte[] md = value.getMdData().getValue();
				if (md == null || md.length <= 7) {
					log.error("Return data is empty.");
					throw makeMcuException(ErrorCode.IF4ERR_CANNOT_CONNECT);
				}
				TIMESTAMP timeStamp = new TIMESTAMP(DataUtil.select(md, 0, 7));
				edp.parse(DataUtil.select(md, 7, md.length - 7));
				ed.setTime(timeStamp.toString());
				ed.setType(value.getMdType().toString());
				ed.setVendor(value.getMdVendor().toString());
				ed.setServiceType(value.getMdServiceType().toString());
				ed.setParser(edp);
			}

			MDData mdData = new MDData(new WORD(1));
			mdData.setMcuId(mcuId);
			mdData.setMdData(mdv.encode());
			// handler.putServiceData(ProcessorHandler.SERVICE_MEASUREMENTDATA,
			// mdData);

			txManager.commit(txStatus);
			return ed;
		} catch (Exception e) {
			if (txManager != null)
				txManager.rollback(txStatus);
			log.error(e, e);
			throw new Exception(e);
		}
	}

	/**
	 * cmdOnDemandMeter (OID: NG_110.2.2)
	 *
	 * @param mcuId
	 *            MCU Indentifier
	 * @param meterId
	 * @param nOption
	 *            (3~7)
	 * @return resultData
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public Map cmdOnDemandByMeter(String mcuId, String meterId, String modemId, int nOption)
			throws FMPMcuException, Exception {
		log.debug("cmdOnDemandByMeter(" + mcuId + "," + meterId + "," + modemId + "," + nOption + ")");

		Target target = null;
		Meter meter = null;
		TransactionStatus txstatus = null;
		try {
			txstatus = txmanager.getTransaction(null);
			String[] meterArray = null;
            if(meterId.contains(",")) {
                meterArray = meterId.split(",");
                meter = meterDao.get(meterArray[0]);
            }else{
                meter = meterDao.get(meterId);  
            }
            
            if(mcuId != null && !"".equals(mcuId)) {
                target = CmdUtil.getTarget(mcuId);
                mcuId = target.getTargetId();
            } else {
                Modem modem = modemDao.get(modemId);
                ModemType modemType = modem.getModemType();
                target = CmdUtil.getTarget(modem);
                mcuId = target.getTargetId();
            }

            //DeviceModel model = meter.getModel();            
            //int lpInterval = meter.getLpInterval();

            Vector<SMIValue> resultDatas = new Vector<SMIValue>();
            Vector<SMIValue> datas = new Vector<SMIValue>();
            SMIValue smiValue = null;
            
            smiValue = DataUtil.getSMIValue(target.getNameSpace(), new INT(nOption));
            datas.add(smiValue);
            
            int[] offsetCount = new int[]{0,1};
            smiValue = DataUtil.getSMIValue(target.getNameSpace(), new INT(offsetCount[0]));
            datas.add(smiValue);
            smiValue = DataUtil.getSMIValue(target.getNameSpace(), new INT(offsetCount[1]));
            datas.add(smiValue);

            //add filter
            if(meterArray != null && meterArray.length > 0) {
                for(int i=0; i<meterArray.length; i++) {
                    smiValue = DataUtil.getSMIValue(target.getNameSpace(), new INT(1)); //1.9   INT Filter  Metering filter meter id
                    datas.add(smiValue);
                    
                    smiValue = DataUtil.getSMIValueByObject(target.getNameSpace(),"stringEntry", meterArray[i]); //Metering filter meter id
                    datas.add(smiValue);
                }   
            }else {
                smiValue = DataUtil.getSMIValue(target.getNameSpace(), new INT(1)); //1.9   INT Filter  Metering filter meter id
                datas.add(smiValue);
                
                smiValue = DataUtil.getSMIValueByObject(target.getNameSpace(),"stringEntry", meterId); //Metering filter meter id
                datas.add(smiValue);
            }
            
            Object[] params =
                new Object[]
                {
                    target,
                    "cmdOnDemandByMeter",
                    datas
                };

            String[] types =
                new String[]
                {
                    TARGET_SRC,
                    "java.lang.String",
                    "java.util.Vector",
                };

            Object obj = null;
            try
            {
                obj = invoke(params, types);
            }
            catch (Exception e)
            {
                log.error(e,e);
                throw new Exception(makeMessage(e.getMessage()));
            }

            SMIValue[] smiValues = null;

            if(obj == null){
                log.error("Can not received value from target");
                throw new FMPMcuException("Can not received value from target");
            }else if (obj instanceof SMIValue[]) {
                smiValues = (SMIValue[]) obj;
                if (smiValues.length > 0)
                {
                    if(smiValues.length <= 1){
                        log.error("Error Code Return");
                        SMIValue errVal = smiValues[0];                        
                        INT errorCode = (INT)errVal.getVariable();
                        throw makeMcuException(errorCode.getValue());
                    }else{
                        //smiValue = smiValues[1];
                        log.debug("### smiValues Length["+smiValues.length+"]");
                        for(int i=0; i<smiValues.length; i++) {
                            if("10.1.0".equals(smiValues[i].getOid().getValue())) {
                                Object mObject = ((OPAQUE)smiValues[i].getVariable()).getValue();
                                if(mObject instanceof meterDataEntryNew) {
                                    resultDatas.add(smiValues[i]);                                                                  
                                } 
                            }
                        }
                    }
                }
                else
                {
                    log.error("smiValue size 0");
                    throw new FMPMcuException("no value exist");
                }
            } else {
                log.error("Unknown Return Value");
                throw new FMPMcuException("Unknown Return Value");
            }
            
            log.debug("### [NameSpace=" + target.getNameSpace() + "]resultDatas Length["+resultDatas.size()+"]");
            Map map = null;
            for(int i=0; i<resultDatas.size(); i++) {
                smiValue = resultDatas.get(i);
                log.debug("### Data index["+i+"] , raw["+smiValue.toString()+"]");
                meter = null;               
                
                OPAQUE mOPAQUE = (OPAQUE)smiValue.getVariable();
                Object mObject = mOPAQUE.getValue();
                
                if (mObject instanceof meterDataEntryNew) {
                    meterDataEntryNew meterEntry = (meterDataEntryNew)mObject;
                    
                    meter = meterDao.get(meterEntry.getMdSerial().toString());
                }
                
                if(meter != null) {
                    DeviceConfig deviceConfig = meter.getModel().getDeviceConfig();
                    if (deviceConfig == null) deviceConfig = meter.getModem().getModel().getDeviceConfig();
                    MeterDataParser edp = null;
                    if (deviceConfig.getOndemandParserName() != null && !"".equals(deviceConfig.getOndemandParserName()))
                        edp = (MeterDataParser)Class.forName(deviceConfig.getOndemandParserName()).newInstance();
                    else edp = (MeterDataParser)Class.forName(deviceConfig.getParserName()).newInstance();
                    
                    edp.setMeter(meter);
                    
                    if (smiValue.getVariable() instanceof BYTE &&
                            (edp instanceof DLMSNamjun || edp instanceof DLMSLSSmartMeterForEVN)){                        
                        BYTE byteValue = (BYTE)smiValue.getVariable();                  
                        map = new LinkedHashMap<String, Object>();
                        switch(nOption){
                        case 3 : // Relay switch table read
                            
                            /*
                            : 1-ON, 0-Off
                            LSB
                            0 : L1 relay status
                            1 : L2 relay status
                            2 : L3 relay status
                            3 : External relay status
                            4 : L1 relay error
                            5 : L2 relay error
                            6 : L3 relay error
                            7 : External relay error
                            8 : Open terminal cover
                            9 : Open terminal cover in power off
                            10: Open top cover
                            11: Open top cover in power off
                            12: Magnetic detection 1
                            13:Magnetic detection 2
                            14: Program
                            15: Factory status
                            MSB
                            */

                            if((byteValue.getValue() & 0x01) == 1 || (byteValue.getValue() & 0x02) == 1 || (byteValue.getValue() & 0x04) == 1){
                                map.put("LoadControlStatus", LOAD_CONTROL_STATUS.CLOSE);
                            }
                            if((byteValue.getValue() & 0x01) == 0 && (byteValue.getValue() & 0x02) == 1 && (byteValue.getValue() & 0x04) == 0){
                                map.put("LoadControlStatus", LOAD_CONTROL_STATUS.OPEN);
                            }
                            if(!map.containsKey("LoadControlStatus") && byteValue.getValue() == 0){
                                map.put("LoadControlStatus", LOAD_CONTROL_STATUS.OPEN);
                            }
                            break;
                        case 4 : // Relay switch on
                            if(byteValue.getValue() == 0){
                                map.put("LoadControlStatus", LOAD_CONTROL_STATUS.CLOSE);
                                map.put("Result", ResultStatus.SUCCESS);
                            }else{
                                map.put("LoadControlStatus", LOAD_CONTROL_STATUS.OPEN);
                                map.put("Result", ResultStatus.FAIL);
                            }
                            break;
                        case 5 : // Relay switch off
                            if(byteValue.getValue() == 0){
                                map.put("LoadControlStatus", LOAD_CONTROL_STATUS.OPEN);
                                map.put("Result", ResultStatus.SUCCESS);
                            }else{
                                map.put("LoadControlStatus", LOAD_CONTROL_STATUS.CLOSE);
                                map.put("Result", ResultStatus.FAIL);
                            }
                            break;                      
                        case 8 : // Timesync (Er000002 Non Clear)
                            if(byteValue.getValue() == 0){
                                map.put("afterTime", DateTimeUtil.getDateString(new Date()));
                                map.put("Result", ResultStatus.SUCCESS);
                            }else{
                                map = null;
                            }
                            break;
                        }
                        return map;
                    }
                    
                    MeterData ed = new MeterData();
                    
                    if (mObject instanceof meterDataEntryNew) {
                        meterDataEntryNew value = (meterDataEntryNew)mObject;
                        
                        // meter id
                        OCTET ocMeterId = new OCTET(20);
                        ocMeterId.setIsFixed(true);
                        String meterSerial = meter.getMdsId()==null ? "" : meter.getMdsId();
                        byte[] bMeterid = DataUtil.fillCopy(meterSerial.getBytes(), (byte) 0x20, 20);
                        ocMeterId.setValue(bMeterid);
                        value.setMdSerial(ocMeterId);
                        
                        byte[] md = value.getMdData().getValue();
                        if(md == null || md.length <= 7){
                            log.error("Return data is empty.");
                            throw makeMcuException(ErrorCode.IF4ERR_CANNOT_CONNECT);
                        }
                        TIMESTAMP timeStamp = new TIMESTAMP(DataUtil.select(md, 0, 7)); 
                        edp.parse(DataUtil.select(md, 7, md.length-7));
                        ed.setTime(timeStamp.toString());
                        ed.setType(value.getMdType().toString());
                        ed.setVendor(value.getMdVendor().toString());
                        ed.setServiceType(value.getMdServiceType().toString());
                        ed.setParser(edp);                
                        MDData mdData = new MDData(new WORD(1));
                        mdData.setMcuId(mcuId);
                        mdData.setMdData(mOPAQUE.encode());
                        mdData.setNS(target.getNameSpace());
                        handler.putServiceData(ProcessorHandler.SERVICE_MEASUREMENTDATA, mdData);
                        
                        
                        if(edp instanceof DLMSLSSmartMeterForEVN){
                            DLMSLSSmartMeterForEVN parser = (DLMSLSSmartMeterForEVN) edp;
                            
                            switch(nOption){
                                case 3 :
                                    map = parser.getRelayStatus();
                                    break;
                                case 4 :
                                    map = parser.getRelayStatus();
                                    map.put("Result", ResultStatus.SUCCESS);
                                    break;
                                case 5 :
                                    map = parser.getRelayStatus();
                                    map.put("Result", ResultStatus.SUCCESS);
                                    break;
                                case 8 : 
                                    if(map == null){
                                        map = new HashMap();
                                    }
                                    //map.put("afterTime", parser.getMeteringTime());                           
                                    map.put("afterTime", DateTimeUtil.getDateString(new Date()));
                                    map.put("Result", ResultStatus.SUCCESS);
                                    break;
                            }                           
                        }
                    }
                }
                if((i + 1) < resultDatas.size()) {
                    Thread.sleep(2000);
                }
            }
            
            return map;

        }catch(Exception e){
            //if (txManager != null){
             //   //txManager.rollback(txStatus);             
             //   TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            //}
            
            log.error("[MCU_ID = " + mcuId + "][METER_ID = "+ meterId + "] " + e, e);
            throw new Exception(e.getMessage());
        }finally{
            try{
                txmanager.commit(txstatus);
            }catch(Exception e){
                log.error(e,e);
                //if (txManager != null && !txStatus.isCompleted())
                //    txManager.rollback(txStatus);
            }
            
        }
	}

	/**
	 * GD 100.2.1,Upload Metering Data
	 * 
	 * @param meterSerial
	 *            Meter Serial
	 * @throws Exception
	 *             IF4ERR_INVALID_PARAM 잘못된 인자 전달 IF4ERR_CANCNOT_CONNECT
	 *             Connection 실패 IF4ERR_CANNOT_GET 처리 중 오류
	 */
	@Override
	public void cmdUploadMeteringData(String meterSerial) throws Exception {

		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;

		try {

			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");
			txStatus = txManager.getTransaction(null);

			Meter meter = meterDao.get(meterSerial);
			Modem modem = meter.getModem();

			if (modem.getModemType() != ModemType.MMIU) {
				throw new Exception("Invalid Modem Type");
			}
			final Target target = CmdUtil.getTarget(modem);

			Object[] params = new Object[] { target, "cmdUploadMeteringData", null };

			String[] types = new String[] { "java.util.Hashtable", "java.lang.String", "java.util.Vector" };

			Object obj = null;
			try {
				obj = (Object) invoke(params, types);
			} catch (Exception e) {
				log.error(e, e);
				throw new Exception(makeMessage(e.getMessage()));
			}

			SMIValue[] smiValues = null;
			if (obj instanceof Integer) {
				log.error("Error Code Return");
				throw makeMcuException(((Integer) obj).intValue());
			} else if (obj instanceof SMIValue[]) {
				smiValues = (SMIValue[]) obj;
			} else {
				log.error("Unknown Return Value");
				throw new Exception("Unknown Return Value");
			}
			// SMS Response 없다.

			log.info("cmdUploadMeteringData End");
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(e);
		} finally {
			txManager.commit(txStatus);
		}

	}

	@Override
	public void cmdMeteringByModem(String mcuId, int nOption, int offset, int count, String[] modemId)
			throws FMPMcuException, Exception {

		log.debug("cmdMeteringByModem(" + mcuId + "," + mcuId + "," + nOption + "," + offset + "," + count + ")");

		Target target = CmdUtil.getTarget(mcuId);

		Vector<SMIValue> datas = new Vector<SMIValue>();
		SMIValue smiValue = null;
		smiValue = DataUtil.getSMIValue(new INT(nOption));
		datas.add(smiValue);
		smiValue = DataUtil.getSMIValue(new INT(offset));
		datas.add(smiValue);
		smiValue = DataUtil.getSMIValue(new INT(count));
		datas.add(smiValue);
		for (int i = 0; i < modemId.length; i++) {
			smiValue = DataUtil.getSMIValueByObject("stringEntry", modemId[i]);
			datas.add(smiValue);
		}

		Object[] params = new Object[] { target, "cmdMeteringByModem", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}
	}

	@Override
	public void cmdMeteringByMeter(String mcuId, int nOption, int offset, int count, String[] meterId)
			throws FMPMcuException, Exception {

		log.debug("cmdMeteringByMeter(" + mcuId + "," + mcuId + "," + nOption + "," + offset + "," + count + ")");

		Target target = CmdUtil.getTarget(mcuId);

		Vector<SMIValue> datas = new Vector<SMIValue>();
		SMIValue smiValue = null;
		smiValue = DataUtil.getSMIValue(new INT(nOption));
		datas.add(smiValue);
		smiValue = DataUtil.getSMIValue(new INT(offset));
		datas.add(smiValue);
		smiValue = DataUtil.getSMIValue(new INT(count));
		datas.add(smiValue);
		for (int i = 0; i < meterId.length; i++) {
			smiValue = DataUtil.getSMIValueByObject("stringEntry", meterId[i]);
			datas.add(smiValue);
		}

		Object[] params = new Object[] { target, "cmdMeteringByMeter", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}
	}

	/**
	 * NG 200.1.1, DCU Assemble Test Operation
	 * 
	 * @param mcuId
	 *            mcuId
	 * @throws Exception
	 *             IF4ERR_INVALID_PARAM 잘못된 인자 전달 IF4ERR_CANCNOT_CONNECT
	 *             Connection 실패 IF4ERR_CANNOT_GET 처리 중 오류
	 */
	@Override
	public void cmdAssembleTestStart(String mcuId) throws FMPMcuException, Exception {

		log.debug("cmdAssembleTestStart(" + mcuId + ")");

		String assembleTestIp = FMPProperty.getProperty("assembletest.ip." + mcuId);
		int assembleTestPort = Integer.parseInt(FMPProperty.getProperty("assembletest.port", "8101"));

		Target target = new Target();
		target.setIpAddr(assembleTestIp);
		target.setTargetId(mcuId);
		McuType mcuType = McuType.DCU;
		target.setTargetType(mcuType);
		target.setPort(assembleTestPort);
		target.setFwVer("0100");
		target.setProtocol(Protocol.LAN);
		target.setFwRevision("4000");
		target.setReceiverType("MCU");
		target.setReceiverId(mcuId);
		target.setNameSpace("NG");

		Vector<SMIValue> datas = null;

		Object[] params = new Object[] { target, "cmdAssembleTestStart", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}

	}

	// DELETE START SP-681
//	/**
//	 * NG 181.2.1, Device upgrade Operation
//	 * 
//	 * @param mcuId
//	 *            mcuId *
//	 * @param 1.5
//	 *            WORD Image Key OTA용 Firmware Image Key. 동일한 Image는 동일한 Key를
//	 *            사용하여 이어 전송하기를 할 수 있게 된다. 서로 다른 Image는 다른 Key를 사용해야 한다.
//	 * @param 1.11
//	 *            STRING File URL Upgrade Image의 URL
//	 * @param 1.11
//	 *            STRING Upgrade File Check Sum Upgrade Image check sum (MD5SUM)
//	 * @param 1.11
//	 *            STRING Node Kind Target modem Node kind
//	 * @param 1.5
//	 *            WORD Target Node FW Version Target modem FW Version. 예를 들어 FW
//	 *            Version 1.2는 0x0102 의 값을 가져야 한다.
//	 * @param 1.5
//	 *            WORD Target Node Build Number Target modem build number
//	 * @param 1.4
//	 *            BYTE Upgrade policy Upgrade Request 실행 방법
//	 * @param -
//	 *            0x00: Store (Upgrade request를 저장만 하고 별도의 실행 요청을 기다린다)
//	 * @param -
//	 *            0x01: Immediately (즉시 실행)
//	 * @param -
//	 *            0x02: Lazy (대상 modem에 대한 Join event를 받거나 metering이 성공한 이후에 실행)
//	 * @param 1.4
//	 *            BYTE Cancel policy Upgrade Cancel 방법 - 0x00: None (삭제 명령이 올 때
//	 *            까지 자동 취소가 없다) - 0x01: Complete (대상에 대한 Upgrade가 완료되면 삭제 된다) -
//	 *            Other values: 설정된 일자 이후에 자동 삭제
//	 * @throws Exception
//	 *             IF4ERR_INVALID_PARAM 잘못된 인자 전달 IF4ERR_CANCNOT_CONNECT
//	 *             Connection 실패 IF4ERR_CANNOT_GET 처리 중 오류
//	 */
//	@Override
//	public void cmdReqNodeUpgrade(String mcuId, int imageKey, String fileUrl, String upgradeCheckSum, String nodeKind,
//			int newFwVersion, int newBuild, int upgradePolicy, int cancelPolicy) throws FMPMcuException, Exception {
//
//		Target target = CmdUtil.getTarget(mcuId);
//
//		StringBuffer buf = new StringBuffer();
//		buf.append("MCUID[" + mcuId + " imageKey " + imageKey + "]\n " + "fileUrl[" + fileUrl + "]\n "
//				+ "upgradeCheckSum[" + upgradeCheckSum + "]\n " + "nodeKind[" + nodeKind + "]\n " + "newFwVersion["
//				+ newFwVersion + "]\n " + "newBuild[" + newBuild + "]\n " + "upgradePolicy[" + upgradePolicy + "]\n "
//				+ "cancelPolicy[" + cancelPolicy + "]");
//
//		log.debug(buf.toString());
//
//		Vector<SMIValue> datas = new Vector<SMIValue>();
//		datas.add(DataUtil.getSMIValue(new WORD(imageKey)));
//		datas.add(DataUtil.getSMIValueByObject("stringEntry", fileUrl));
//		datas.add(DataUtil.getSMIValueByObject("stringEntry", upgradeCheckSum));
//		datas.add(DataUtil.getSMIValueByObject("stringEntry", nodeKind));
//		datas.add(DataUtil.getSMIValue(new WORD(newFwVersion)));
//		datas.add(DataUtil.getSMIValue(new WORD(newBuild)));
//		datas.add(DataUtil.getSMIValue(new BYTE(upgradePolicy)));
//		datas.add(DataUtil.getSMIValue(new BYTE(cancelPolicy)));
//
//		Object[] params = new Object[] { target, "cmdReqNodeUpgrade", datas };
//
//		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };
//
//		Object obj = null;
//		try {
//			obj = invoke(params, types);
//		} catch (Exception e) {
//			log.error(e, e);
//			throw new Exception(makeMessage(e.getMessage()));
//		}
//
//		if (obj instanceof Integer) {
//			log.error("Error Code Return");
//			throw makeMcuException(((Integer) obj).intValue());
//		}
//	}
	// DELETE END SP-681
	
	// INSERT START SP-681

	/**
     * SP 181.2.3  cmdReqNodeUpgrade - 
     *
     * @param mcuId MCU Indentifier
     * @param upgradeType Upgrade Type 0x01:Modem, 0x02:Sensor/Meter, 0x03: DCU, Others:Not supported model
     * @param control
     *        Flow control  0x00:Continue, 0x01:New
     *        Image control 0x00:Normal, 0x01:Force, 0x02:Exist
     *        Request control 0x00:Store, 0x01:Immediately, 0x02:Lazy
     *        Cancel control 0x00:None, 0x01:Complete
     * @param imageKey imageKey
     * @param imageUrl imageUrl
     * @param checkSum checkSum
     * @param filter filter
     * @param filterValue filterValue
     * @throws FMPMcuException, Exception
     */		
	@Override
	public Hashtable cmdReqNodeUpgrade(String mcuId, int upgradeType, int control,
			String imageKey, String imageUrl, String upgradeCheckSum, 
			List<String> filterValue) throws FMPMcuException, Exception {

		Target target = CmdUtil.getTarget(mcuId);

		StringBuffer buf = new StringBuffer();
		buf.append("cmdReqNodeUpgrade() MCUID[" + mcuId + "]\n upgradeType[" + upgradeType +
				"]\n control[" + control + "]\n imageKey[" + imageKey + "]\n " + "imageUrl[" + imageUrl + "]\n "
				+ "upgradeCheckSum[" + upgradeCheckSum + "]\n " );

		log.debug(buf.toString());

		Vector<SMIValue> datas = new Vector<SMIValue>();
		
		datas.add(DataUtil.getSMIValue(new BYTE(upgradeType)));
		datas.add(DataUtil.getSMIValue(new UINT(control)));

		byte[] hexKey = Hex.encode(imageKey);
		datas.add(DataUtil.getSMIValue(new WORD(hexKey)));

		datas.add(DataUtil.getSMIValueByObject("stringEntry", imageUrl));
		datas.add(DataUtil.getSMIValueByObject("stringEntry", upgradeCheckSum));
		
		if (filterValue != null) {
	        for(String entry : filterValue){
	        	datas.add(DataUtil.getSMIValue(new INT(2))); // modem ID
	           	datas.add(DataUtil.getSMIValueByObject("stringEntry", entry)); 
	         }
		}
        
        Object[] params = 
        		new Object[] 
        		{ 
        			target, 
        			"cmdReqNodeUpgrade", 
        			datas 
        		};
         
         String[] types = 
        		 new String[] 
        		 { 
        			TARGET_SRC, 
        			"java.lang.String",
        			"java.util.Vector" 
        		 };
         
         Object obj = null;
         try {
        	 log.debug("#### ====> [cmdReqNodeUpgrade] invoke [" + mcuId + "]");
             obj = invoke(params, types);
             log.debug("#### <==== [cmdReqNodeUpgrade] invoke [" + mcuId + "]");
         } catch (Exception e) {
         	 log.debug("cmdReqNodeUpgrade ERROR : "+e);
             log.error(e, e);
             throw new Exception(makeMessage(e.getMessage()));
         } 

         SMIValue[] smiValues = null;
         
         if (obj instanceof Integer) {
             throw makeMcuException(((Integer) obj).intValue());         	
         }
         else if (obj instanceof SMIValue[])
         {
             smiValues = (SMIValue[]) obj;
         }
		
         Hashtable res = null;
         if (smiValues != null && smiValues.length != 0)
         {
             res = CmdUtil.convSMItoMOP(smiValues[0]);
         }
         else
         {
             log.debug("smiValues is null");
         }

         return res;         
	}	
	
	/**
     * SP 181.2.3  cmdReqImagePropagate - 
     *
     * @param mcuId MCU Indentifier
     * @param upgradeType Upgrade Type 0x06:Third-party Coordinator, 0x07:Third-party Modem, Others: Not supported model
     * @param control
     *        Flow control  0x00:Continue, 0x01:New
     *        Image control 0x00:Normal, 0x01:Force, 0x02:Exist
     *        Request control 0x00:Store, 0x01:Immediately, 0x02:Lazy
     *        Cancel control 0x00:None, 0x01:Complete
     * @param imageKey imageKey
     * @param imageUrl imageUrl
     * @param checkSum checkSum
     * @param imageVersion imageVersion
     * @param targetModel targetModel
     * @param filter filter
     * @param filterValue filterValue
     * @throws FMPMcuException, Exception
     */	
	
	@Override
	public Hashtable cmdReqImagePropagate(String mcuId, int upgradeType, int control,
			String imageKey, String imageUrl, String upgradeCheckSum, 
			String imageVersion, String targetModel , List<String> filterValue) throws FMPMcuException, Exception {

		Target target = CmdUtil.getTarget(mcuId);

		StringBuffer buf = new StringBuffer();
		buf.append("cmdReqImagePropagate() MCUID[" + mcuId + "]\n upgradeType[" + upgradeType +
				"]\n control[" + control + "]\n imageKey[" + imageKey + "]\n " + "imageUrl[" + imageUrl + "]\n "
				+ "upgradeCheckSum[" + upgradeCheckSum + "]\n " + "imageVersion["
				+ imageVersion + "]\n " + "targetModel[" + targetModel + "]\n " );

		log.debug(buf.toString());
        Object obj = null;

		try {
			Vector<SMIValue> datas = new Vector<SMIValue>();
			
			datas.add(DataUtil.getSMIValue(new BYTE(upgradeType)));
			datas.add(DataUtil.getSMIValue(new UINT(control)));
	
			byte[] hexKey = Hex.encode(imageKey);
			datas.add(DataUtil.getSMIValue(new WORD(hexKey)));
	
			datas.add(DataUtil.getSMIValueByObject("stringEntry", imageUrl));
			datas.add(DataUtil.getSMIValueByObject("stringEntry", upgradeCheckSum));
			
			byte[] hexVersion = Hex.encode(imageVersion);
			datas.add(DataUtil.getSMIValue(new WORD(hexVersion)));
			datas.add(DataUtil.getSMIValueByObject("stringEntry", targetModel));
	
			if (filterValue != null) {
		        for(String entry : filterValue){
		//           	datas.add(DataUtil.getSMIValue(new INT(upgradeType.getFilterType())));
		        	datas.add(DataUtil.getSMIValue(new INT(2))); // modem ID
		           	datas.add(DataUtil.getSMIValueByObject("stringEntry", entry)); 
		        }
			}
	        
	        Object[] params = 
	        		new Object[] 
	        		{ 
	        			target, 
	        			"cmdReqImagePropagate", 
	        			datas 
	        		};
	         
	         String[] types = 
	        		 new String[] 
	        		 { 
	        			TARGET_SRC, 
	        			"java.lang.String",
	        			"java.util.Vector" 
	        		 };
	         
         
        	 log.debug("#### ====> [cmdReqImagePropagate] invoke [" + mcuId + "]");
             obj = invoke(params, types);
             log.debug("#### <==== [cmdReqImagePropagate] invoke [" + mcuId + "]");
         } catch (Exception e) {
         	 log.debug("cmdReqImagePropagate ERROR : "+e);
             log.error(e, e);
             throw new Exception(makeMessage(e.getMessage()));
         } 

         SMIValue[] smiValues = null;
         
         if (obj instanceof Integer) {
             throw makeMcuException(((Integer) obj).intValue());         	
         }
         else if (obj instanceof SMIValue[])
         {
             smiValues = (SMIValue[]) obj;
         }
		
         Hashtable res = null;
         if (smiValues != null && smiValues.length != 0)
         {
             res = CmdUtil.convSMItoMOP(smiValues[0]);
         }
         else
         {
             log.debug("smiValues is null");
         }

         return res;         
	}
	
	@Override
	public Hashtable cmdGetImagePropagateInfo(String mcuId, int upgradeType)  throws FMPMcuException, Exception {
		
		Target target = CmdUtil.getTarget(mcuId);
		
		Vector<SMIValue> datas = new Vector<SMIValue>();	
		datas.add(DataUtil.getSMIValue(target.getNameSpace(), new BYTE(upgradeType)));
		
        Object[] params = 
        		new Object[] 
        		{ 
        			target, 
        			"cmdGetImagePropagateInfo", 
        			datas 
        		};
         
         String[] types = 
        		 new String[] 
        		 { 
        			TARGET_SRC, 
        			"java.lang.String",
        			"java.util.Vector" 
        		 };
         
         Object obj = null;
         try {
        	 log.debug("#### ====> [cmdGetImagePropagateInfo] invoke [" + mcuId + "]");
             obj = invoke(params, types);
             log.debug("#### <==== [cmdGetImagePropagateInfo] invoke [" + mcuId + "]");
         } catch (Exception e) {
         	 log.debug("cmdGetImagePropagateInfo ERROR : "+e);
             log.error(e, e);
             throw new Exception(makeMessage(e.getMessage()));
         } 
         
         SMIValue[] smiValues = null;
         
         if (obj instanceof Integer) {
             throw makeMcuException(((Integer) obj).intValue());         	
         }
         else if (obj instanceof SMIValue[])
         {
             smiValues = (SMIValue[]) obj;
         }
		
 		Hashtable res = new Hashtable();
 		if (smiValues != null && smiValues.length != 0) {
 			for (int i = 0; i < smiValues.length; i++) {
 				res.putAll(CmdUtil.convSMItoMOP(smiValues[i]));
 				log.debug(smiValues[i].toString());
 			}
 		} else {
 			log.debug("smiValues is null");
 		} 
        return res;		
		
		
	}
	// INSERT END SP-681
	
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
	@Override
	public void cmdReqCertRenewal(String mcuId, String deviceSerial, String fileUrl, String checksum) throws FMPMcuException, Exception {
		
		Target target = CmdUtil.getTarget(mcuId);

		StringBuffer buf = new StringBuffer();
		buf.append("MCUID[" + mcuId + "]\n  fileUrl[" + fileUrl + "]\n " + "checksum[" + checksum + "]");

		log.debug(buf.toString());

		Vector<SMIValue> datas = new Vector<SMIValue>();
		datas.add(DataUtil.getSMIValueByObject("stringEntry", deviceSerial));
		datas.add(DataUtil.getSMIValueByObject("stringEntry", fileUrl));
		datas.add(DataUtil.getSMIValueByObject("stringEntry", checksum));
		
		Object[] params = new Object[] { target, "cmdReqCertRenewal", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}
	}
	
    /**
     * SP 181.2.1  cmdReqToDCUNodeUpgrade - DCU Node upgrade request.
     *
     * @param mcuId MCU Indentifier
     * @param upgradeType Upgrade Type 01:Modem, 02:Sensor/Meter, 03:DCU-FW, 04:DCU-KERNAL
     * @param controlCode ControlCode Default:0
     * @param imageKey imageKey
     * @param imageUrl imageUrl
     * @param checkSum checkSum
     * @param filter filter
     * @param filterValue filterValue
     * @return moproperty
     * @throws FMPMcuException, Exception
     */
	@Override
    public Hashtable cmdReqToDCUNodeUpgrade(String mcuId, OTAType upgradeType, String imageKey, 
			String imageUrl, String checkSum, List<String> filterValue) throws FMPMcuException, Exception {
    	 Target target = CmdUtil.getTarget(mcuId);
         
         StringBuffer buf = new StringBuffer();
         buf.append("MCUID[" + mcuId + " imageKey "+imageKey+"]\n " +
                 "fileUrl[" + imageUrl + "]\n " +
                 "upgradeCheckSum[" + checkSum + "]" + " targetId[" + target.getTargetId() +"]");
         
         log.debug(buf.toString());
         
         Vector<SMIValue> datas = new Vector<SMIValue>();
      
         datas.add(DataUtil.getSMIValue(new BYTE(upgradeType.getTypeCode())));        		
         datas.add(DataUtil.getSMIValue(new UINT(0)));   // 현재 집중기쪽에서 사용하지 않는 코드 
         datas.add(DataUtil.getSMIValueByObject("stringEntry", imageKey));
         datas.add(DataUtil.getSMIValueByObject("stringEntry", imageUrl));
         datas.add(DataUtil.getSMIValueByObject("stringEntry", checkSum));
         
         for(String entry : filterValue){
           	datas.add(DataUtil.getSMIValue(new INT(upgradeType.getFilterType())));
           	datas.add(DataUtil.getSMIValueByObject("stringEntry", entry)); 
         }
         
         Object[] params = 
        		new Object[] 
        		{ 
        			target, 
        			"cmdReqNodeUpgrade", 
        			datas 
        		};
         
         String[] types = 
        		 new String[] 
        		 { 
        			TARGET_SRC, 
        			"java.lang.String",
        			"java.util.Vector" 
        		 };
         
         Object obj = null;
         try {
        	 log.debug("#### ====> [cmdReqToDCUNodeUpgrade] invoke [" + mcuId + "]");
             obj = invoke(params, types);
             log.debug("#### <==== [cmdReqToDCUNodeUpgrade] invoke [" + mcuId + "]");
         } catch (Exception e) {
         	 log.debug("cmdReqNodeUpgrade ERROR : "+e);
             log.error(e, e);
             throw new Exception(makeMessage(e.getMessage()));
         } 
         
         SMIValue[] smiValues = null;
         
         if (obj instanceof Integer) {
             throw makeMcuException(((Integer) obj).intValue());         	
         }
         else if (obj instanceof SMIValue[])
         {
             smiValues = (SMIValue[]) obj;
         }
		
         Hashtable res = null;
         if (smiValues != null && smiValues.length != 0)
         {
             res = CmdUtil.convSMItoMOP(smiValues[0]);
         }
         else
         {
             log.debug("smiValues is null");
         }

         return res;
	}
    
    /**
     * SP 181.2.2 cmdCtrlUpgradeRequest 
     *
     * @param mcuId MCU Indentifier
     * @param int requestId
     * @param int opCode
     * @throws FMPMcuException, Exception
     */
	@Override
	public void cmdCtrlUpgradeRequest(String mcuId, int requestId, int opCode) throws FMPMcuException, Exception {
	   	 Target target = CmdUtil.getTarget(mcuId);
	        
	     log.debug("MCUID[" + mcuId + " requestId "+requestId+"]\n " +      "opCode[" + opCode + "]");
	        
         Vector<SMIValue> datas = new Vector<SMIValue>();
                       		
         datas.add(DataUtil.getSMIValue(new UINT(requestId))); 
         datas.add(DataUtil.getSMIValue(new BYTE(opCode)));
         
         Object[] params = 
        		new Object[] 
        		 { 
					target, 
					"cmdCtrlUpgradeRequest", 
					datas 
        		 };
         
         String[] types = 
        		new String[] 
        		 { 
        			TARGET_SRC, 
        			"java.lang.String",
        			"java.util.Vector", 
        		 };
         

         Object obj = null;
         try
         {
        	 log.debug("#### ====> [cmdCtrlUpgradeRequest] invoke [" + mcuId + "]");
        	 obj = invoke(params, types);
             log.debug("#### <==== [cmdCtrlUpgradeRequest] invoke [" + mcuId + "]");
         }
         catch (Exception e)
         {
             log.error(e,e);
             throw new Exception(makeMessage(e.getMessage()));
         }

         if (obj instanceof Integer)
         {
             log.error("Error Code Return");
             throw makeMcuException(((Integer) obj).intValue());
         }
	}
	
	private String[] getAddrHops(String targetNode) {
		String addr = "";
		String hops = "";
		String targetType = "Modem";

		MCU mcu = mcuDao.get(targetNode);
		if (mcu == null) {
			Modem modem = modemDao.get(targetNode);

			if (modem != null) {
				addr = modem.getIpAddr();

				// if (modem.getModem() != null)
				// hops = modem.getModem().getNameSpace();
			}
		} else {
			targetType = "MCU";
			addr = mcu.getIpv6Addr();
		}

		return new String[] { addr, hops, targetType };
	}

	public String coapPing(String ipAddress, String device, String type) throws Exception {
		Target target = new Target();
		boolean result = false;
		Pattern pattern;
		
		pattern = Pattern.compile(regexIPv4andIPv6);
        if(ipAddress == null || pattern.matcher(ipAddress).matches() == false){
        	return "FAIL";
        }
		
		pattern = Pattern.compile(regexIPv4);
		if(pattern.matcher(ipAddress).matches() == true){
			target.setIpAddr(ipAddress);
		}
		
		pattern = Pattern.compile(regexIPv6);
		if(pattern.matcher(ipAddress).matches() == true){
			target.setIpv6Addr(ipAddress);
		}
		if(type.equals("mbb"))
			target.setProtocol(Protocol.SMS);
		else
			target.setFwVer(type); // pana적용..일정관계로 이렇게 수정.. 추후 전체적으로 수정필요!!
		
		target.setPort(5683);
		SyncCoapCommand client = new SyncCoapCommand();
		result = client.sendCoapPing(target, device);
		
		if(!result){
			log.info("coap-ping target [" + target + "], result [" + "Request Timeout" + "]");
			return "Request Timeout";
		} else {
			log.info("coap-ping target [" + target + "], result [" + "SUCCESS" + "]");
			return "SUCCESS";
		}
	}
	
	/**
	 *  getInfo (COAP)
	 **/
    public Map<String, String> coapGetInfo (
            java.lang.String ipv4 , java.lang.String ipv6, java.lang.String type
            ) throws Exception {
        log.info("TARGET_NODE[" + ipv4 + " / " + ipv6 +"]" + " type :" + type);
		
        GeneralFrame            frmForRF     =  new GeneralFrame();        
        GeneralFrameForETHERNET frmForETHER  =  new GeneralFrameForETHERNET(); 
        GeneralFrameForMBB      frmForMBB    =  new GeneralFrameForMBB(); 
  
        Target tag =  new Target();
        Map map = new HashMap<String, String>();
        
        if( (ipv4 != null && !"".equals(ipv4)) || (ipv6 != null && !"".equals(ipv6))){ // IPv4, IPv6 둘 중 하나라도 주소 값이 있을경우 동작
	        if (ipv4 != null && !"".equals(ipv4)) {  // IPv4 주소 값이 있을경우
	            tag.setIpAddr(ipv4);
	            if(type.startsWith("rf")){
	            	tag.setFwVer(type.substring(3, type.length()-1));
	            	tag.setProtocol(Protocol.IP);
	            	frmForRF.setUriAddr(frmForRF._uriAddr.MTInfoNetworkManagementSystem.getCode());
	            	frmForRF.setURI_Addr();  
	            	SyncCoapCommand client = new SyncCoapCommand();
	            	frmForRF  = client.sendCoapCommand(tag, frmForRF);
	            	map.put("CPU", frmForRF.getCpuUsage()+"");
		            map.put("Memory", frmForRF.getMemoryUsage()+"");
		            map.put("TxSize", frmForRF.getTotalTxSize()+"");
		            map.put("RSSI", frmForRF.getRssi()+"");
		            map.put("LQI", frmForRF.getLqi()+"");
		            map.put("LastCommDate", frmForRF.getYyyymmddhhmmss()+"");
		            map.put("ParentId", frmForRF.getParentNodeId()+"");
		            map.put("ETX", frmForRF.getEtx()+"");
	            	
	            }else if(type.equals("ethernet")){
	            	tag.setProtocol(Protocol.IP);
	            	frmForETHER.setUriAddr(frmForETHER._uriAddr.MTInfoNetworkManagementSystem.getCode());
	            	frmForETHER.setURI_Addr();  
	            	SyncCoapCommand client = new SyncCoapCommand();
	            	frmForETHER  = client.sendCoapCommand(tag, frmForETHER);
	            	map.put("CPU", frmForETHER.getCpuUsage()+"");
		            map.put("Memory", frmForETHER.getMemoryUsage()+"");
		            map.put("TxSize", frmForETHER.getTotalTxSize()+"");
		            map.put("RSSI", frmForETHER.getRssi()+"");
		            map.put("LQI", frmForETHER.getLqi()+"");
		            map.put("LastCommDate", frmForETHER.getYyyymmddhhmmss()+"");
		            map.put("ParentId", frmForETHER.getParentNodeId()+"");
		            map.put("ETX", frmForETHER.getEtx()+"");
	            	
	            }else if(type.equals("mbb")){
	            	tag.setProtocol(Protocol.SMS);
	            	frmForMBB.setUriAddr(frmForMBB._uriAddr.MTInfoNetworkManagementSystem.getCode());
	            	frmForMBB.setURI_Addr();  
	            	SyncCoapCommand client = new SyncCoapCommand();
	            	frmForMBB  = client.sendCoapCommand(tag, frmForMBB);
	            	map.put("CPU", frmForMBB.getCpuUsage()+"");
		            map.put("Memory", frmForMBB.getMemoryUsage()+"");
		            map.put("TxSize", frmForMBB.getTotalTxSize()+"");
		            map.put("RSSI", frmForMBB.getRssi()+"");
		            map.put("LQI", frmForMBB.getLqi()+"");
		            map.put("LastCommDate", frmForMBB.getYyyymmddhhmmss()+"");
		            map.put("ParentId", frmForMBB.getParentNodeId()+"");
		            map.put("ETX", frmForMBB.getEtx()+"");
	            }
	            
	        }
	        if (ipv6 != null && !"".equals(ipv6)) { // IPv6 주소 값이 있을경우
	            tag.setIpv6Addr(ipv6); 
	            if(type.startsWith("rf")){
	            	tag.setFwVer(type.substring(3, type.length()-1));
	            	tag.setProtocol(Protocol.IP);
	            	frmForRF.setUriAddr(frmForRF._uriAddr.MTInfoNetworkManagementSystem.getCode());
	            	frmForRF.setURI_Addr();
	            	SyncCoapCommand client = new SyncCoapCommand();
	            	frmForRF  = client.sendCoapCommand(tag, frmForRF);
	            	map.put("CPU6", frmForRF.getCpuUsage()+"");
		            map.put("Memory6", frmForRF.getMemoryUsage()+"");
		            map.put("TxSize6", frmForRF.getTotalTxSize()+"");
		            map.put("RSSI6", frmForRF.getRssi()+"");
		            map.put("LQI6", frmForRF.getLqi()+"");
		            map.put("LastCommDate6", frmForRF.getYyyymmddhhmmss()+"");
		            map.put("ParentId6", frmForRF.getParentNodeId()+"");
		            map.put("ETX6", frmForRF.getEtx()+"");
	            }else if(type.equals("ethernet")){
	            	tag.setProtocol(Protocol.IP);
	            	frmForETHER.setUriAddr(frmForETHER._uriAddr.MTInfoNetworkManagementSystem.getCode());
	            	frmForETHER.setURI_Addr();  
	            	SyncCoapCommand client = new SyncCoapCommand();
	            	frmForETHER  = client.sendCoapCommand(tag, frmForETHER);
	            	map.put("CPU6", frmForETHER.getCpuUsage()+"");
		            map.put("Memory6", frmForETHER.getMemoryUsage()+"");
		            map.put("TxSize6", frmForETHER.getTotalTxSize()+"");
		            map.put("RSSI6", frmForETHER.getRssi()+"");
		            map.put("LQI6", frmForETHER.getLqi()+"");
		            map.put("LastCommDate6", frmForETHER.getYyyymmddhhmmss()+"");
		            map.put("ParentId6", frmForETHER.getParentNodeId()+"");
		            map.put("ETX6", frmForETHER.getEtx()+"");
	            }else if(type.equals("mbb")){
	            	tag.setProtocol(Protocol.SMS);
	            	frmForMBB.setUriAddr(frmForMBB._uriAddr.MTInfoNetworkManagementSystem.getCode());
	            	frmForMBB.setURI_Addr();  
	            	SyncCoapCommand client = new SyncCoapCommand();
	            	frmForMBB  = client.sendCoapCommand(tag, frmForMBB);
	            	map.put("CPU6", frmForMBB.getCpuUsage()+"");
		            map.put("Memory6", frmForMBB.getMemoryUsage()+"");
		            map.put("TxSize6", frmForMBB.getTotalTxSize()+"");
		            map.put("RSSI6", frmForMBB.getRssi()+"");
		            map.put("LQI6", frmForMBB.getLqi()+"");
		            map.put("LastCommDate6", frmForMBB.getYyyymmddhhmmss()+"");
		            map.put("ParentId6", frmForMBB.getParentNodeId()+"");
		            map.put("ETX6", frmForMBB.getEtx()+"");
	            }
	            }
	        return map;
        }
        throw new Exception("Empty IP Address !"); // 둘 다 주소값이 없을경우
    }
    
    /**
	 * ModemReset (COAP)
	 **/
    public Map<String, String> modemReset (
            java.lang.String ipv4 , java.lang.String ipv6, java.lang.String type
            ) throws Exception {
        log.info("TARGET_NODE[" + ipv4 + " / " + ipv6 +"]");
		
        GeneralFrame            frmForRF    =  new GeneralFrame();
        GeneralFrameForETHERNET frmForETHER =  new GeneralFrameForETHERNET();
        GeneralFrameForMBB      frmForMBB    =  new GeneralFrameForMBB(); 
  
        Target tag =  new Target();
        Map map = new HashMap<String, String>();
        
        if( (ipv4 != null && !"".equals(ipv4)) || (ipv6 != null && !"".equals(ipv6))){ // IPv4, IPv6 둘 중 하나라도 주소 값이 있을경우 동작
	        if (ipv4 != null && !"".equals(ipv4)) {  // IPv4 주소 값이 있을경우
	            tag.setIpAddr(ipv4);
	            tag.setPort(5683);
	            if(type.startsWith("rf")){
	            	tag.setFwVer(type.substring(3, type.length()-1));
	            	tag.setProtocol(Protocol.IP);
	            	frmForRF.setUriAddr(frmForRF._uriAddr.ModemReset.getCode());
	            	frmForRF.setURI_Addr();  
	            	SyncCoapCommand client = new SyncCoapCommand();
	            	frmForRF  = client.sendCoapCommand(tag, frmForRF);
	            	map.put("status", "success");                 
	            }else if(type.equals("ethernet")){
	            	tag.setProtocol(Protocol.IP);
	            	frmForETHER.setUriAddr(frmForETHER._uriAddr.ModemReset.getCode());
	            	frmForETHER.setURI_Addr(); 
	            	SyncCoapCommand client = new SyncCoapCommand();
	            	frmForETHER  = client.sendCoapCommand(tag, frmForETHER);
	            	map.put("status", "success");
	            }else if(type.equals("mbb")){
	            	tag.setProtocol(Protocol.SMS);
	            	frmForMBB.setUriAddr(frmForMBB._uriAddr.ModemReset.getCode());
	            	frmForMBB.setURI_Addr(); 
	            	SyncCoapCommand client = new SyncCoapCommand();
	            	frmForMBB  = client.sendCoapCommand(tag, frmForMBB);
	            	map.put("status", "success");
	            }
	          }
	        if (ipv6 != null && !"".equals(ipv6)) { // IPv6 주소 값이 있을경우
	            tag.setIpv6Addr(ipv6); 
	            tag.setPort(5683);
	            if(type.startsWith("rf")){
	            	tag.setFwVer(type.substring(3, type.length()-1));
	            	tag.setProtocol(Protocol.IP);
	            	frmForRF.setUriAddr(frmForRF._uriAddr.ModemReset.getCode());
	            	frmForRF.setURI_Addr();  
	            	SyncCoapCommand client = new SyncCoapCommand();
	            	frmForRF  = client.sendCoapCommand(tag, frmForRF);
	            	map.put("status", "success");
	            }else if(type.equals("ethernet")){
	            	tag.setProtocol(Protocol.IP);
	            	frmForETHER.setUriAddr(frmForETHER._uriAddr.ModemReset.getCode());
	            	frmForETHER.setURI_Addr(); 
	            	SyncCoapCommand client = new SyncCoapCommand();
	            	frmForETHER  = client.sendCoapCommand(tag, frmForETHER);
	            	map.put("status", "success");
	            }else if(type.equals("mbb")){
	            	tag.setProtocol(Protocol.SMS);
	            	frmForMBB.setUriAddr(frmForMBB._uriAddr.ModemReset.getCode());
	            	frmForMBB.setURI_Addr(); 
	            	SyncCoapCommand client = new SyncCoapCommand();
	            	frmForMBB  = client.sendCoapCommand(tag, frmForMBB);
	            	map.put("status", "success");
	            }
	          }
	        return map;
        }
        throw new Exception("Empty IP Address !"); // 둘 다 주소값이 없을경우
    }
    
    /**
	 * coapBrowser (COAP)
	 **/
    public Map<String, String> coapBrowser (
            java.lang.String ipv4 , java.lang.String ipv6, java.lang.String uri, String query, String config, String type
            ) throws Exception {
        log.info("TARGET_NODE[" + ipv4 + " / " + ipv6 +"]");
		
        GeneralFrame frm = new GeneralFrame();
        
        Target tag =  new Target();
        Map map = new HashMap<String, String>();
        
        if( (ipv4 != null && !"".equals(ipv4)) || (ipv6 != null && !"".equals(ipv6))){ // IPv4, IPv6 둘 중 하나라도 주소 값이 있을경우 동작
	        if (ipv4 != null && !"".equals(ipv4)) {  // IPv4 주소 값이 있을경우
	        	if(type.equals("mbb"))
	        		tag.setProtocol(Protocol.SMS);
	        	else
	        		tag.setFwVer(type);
	            tag.setIpAddr(ipv4);
	            tag.setPort(5683);
	            frm.setUriAddr(uri);
	            frm.setURI_Addr();  
	            SyncCoapCommand client = new SyncCoapCommand();
	            frm  = client.sendCoapCommand(tag, frm, config, query);
	           
	            if(config.equals("GET"))
	            	map.put("result", frm.getCoapResult());
	            else{
	            	map.put("result", "success");
	            }
	            map.put("status", "success");                 
	      
	          }
	        if (ipv6 != null && !"".equals(ipv6)) { // IPv6 주소 값이 있을경우
	        	if(type.equals("mbb"))
	        		tag.setProtocol(Protocol.SMS);
	        	else
	        		tag.setFwVer(type);
	        	tag.setIpv6Addr(ipv6); 
	            tag.setPort(5683);
	            frm.setUriAddr(uri);
	            frm.setURI_Addr();  
	            SyncCoapCommand client = new SyncCoapCommand();
	            frm  = client.sendCoapCommand(tag, frm, config, query);
	            
	            if(config.equals("GET"))
	            	map.put("result", frm.getCoapResult());
	            else{
	            	map.put("result", "success");
	            }
	            map.put("status", "success");
	            
	          }
	        return map;
        }
        throw new Exception("Empty IP Address !"); // 둘 다 주소값이 없을경우
    }

	/**
	 * NI 프로토콜을 통한 커맨드 전송 : Modem Event Log 요청 모뎀아이디, 요청할 로그의 개수
	 **/
    // UPDATE START SP-681
//	public Map<String, String> getModemEventLog(String mdsId, int logCount) throws Exception {
//		log.debug("## NI command - Modem Event Log ["+mdsId+"],["+logCount+"]");
	// UPDATE END SP-681
	public Map<String, String> getModemEventLog(String mdsId, int logCount, int logOffset) throws Exception {
		log.debug("## NI command - Modem Event Log ["+mdsId+"],["+logCount+"],[" +logOffset+"]");		
		
		
		// 결과 맵
		Map map = new HashMap<String, String>();
		ModemEventLog resultObj = null;

		Target target = new Target();
		// 커맨드 전송할 모뎀 조회
		Modem modem = modemDao.get(Integer.parseInt(mdsId));
	
		try {
			/**
			 * Ni Proxy
 			 */
			HashMap niParam = new HashMap();
			niParam.put("count", logCount);
			niParam.put("offset", logOffset);	// INSERT SP-681
			CommandNIProxy niProxy = new CommandNIProxy();
			
            resultObj = (ModemEventLog)niProxy.execute(modem, NIAttributeId.ModemEventLog, niParam);
            
		} catch (Exception e) {
			log.error(e, e);
		}

		// return map 작성
		if(resultObj!=null){
			if(resultObj.getLogs() == null){
				map.put("cmdResult", "Reponse of ModemEventLog-Command is invalid");
			}else {
				map.put("eventLogs", resultObj.toString());
				map.put("cmdResult", "Execution OK");
			}
		}else{
			map.put("cmdResult", "Failed to sending a command in CommandNIProxy.");
		}
		return map;
	}

	/** NI Protocol: Clone On/Off 	**/
	public Map<String, Object> 	setCloneOnOff(String modemId, int count) throws Exception {
		log.debug("## NI command - Clone On/Off [" + modemId + "], [" + count + "]");
		
		Map map = new HashMap<String, Object>();
		CloneOnOff resultObj = null;
		Target target = new Target();
		
		try {
			Modem modem = modemDao.get(Integer.parseInt(modemId));
			
			// NI proxy
			HashMap niParam = new HashMap();
			
			niParam.put("count", count);
			CommandNIProxy niProxy = new CommandNIProxy();
			
			resultObj = (CloneOnOff)niProxy.execute(modem, NIAttributeId.CloneOnOff, niParam);
		} catch (Exception e) {
			log.debug(e, e);
		}
		
		if (resultObj != null) {
			map.put("cmdResult", "Execution OK");
			log.debug("resultObj.toString(): " + resultObj.toString());
		} else {
			map.put("cmdResult", "Failed to sending a command in CommandNIProxy.");
		}
		
		return map;
		
	}

	// INSERT START SP-681
	public Map<String, Object> 	setCloneOnOffWithTarget(String modemId, String cloneCode, int count, String version, int euiCount, List<String> euiList) throws Exception {
		log.debug("## NI command - Clone On/Off [" + modemId + "], [" + cloneCode + "], [" + count + "], [" + version + "], [" + euiCount + "]");
		
		Map map = new HashMap<String, Object>();
		CloneOnOff resultObj = null;
		Target target = new Target();
		
		try {
			Modem modem = modemDao.get(Integer.parseInt(modemId));
			
			// NI proxy
			HashMap niParam = new HashMap();
			
			niParam.put("code", cloneCode);
			niParam.put("count", count);
			niParam.put("version", version);
			niParam.put("euicount", euiCount);
			niParam.put("euitable", (String[])euiList.toArray(new String[0]));
			
			CommandNIProxy niProxy = new CommandNIProxy();
			
			resultObj = (CloneOnOff)niProxy.execute(modem, NIAttributeId.CloneOnOff, niParam);
		} catch (Exception e) {
			log.debug(e, e);
		}
		
		if (resultObj != null) {
			map.put("cmdResult", "Execution OK");
			log.debug("resultObj.toString(): " + resultObj.toString());
		} else {
			map.put("cmdResult", "Failed to sending a command in CommandNIProxy.");
		}
		
		return map;
		
	}
	// INSERT END SP-681
	
	/**
	 * NI 프로토콜을 통한 커맨드 전송 : Meter Baud Rate 설정 모뎀아이디, 요청타입(get/set),
	 * set인경우 설정할 값
	 **/
	public Map<String, String> cmdMeterBaudRate(String mdsId, String requestType, int rateValue) throws Exception {
		log.debug("## NI command - Meter Baud Rate ["+mdsId+"],["+requestType+"],["+rateValue+"]");
		// 결과 맵
		Map map = new HashMap<String, String>();
		MeterBaud resultObj = null;

		Target target = new Target();
		// 커맨드 전송할 모뎀 조회
		Modem modem = modemDao.get(Integer.parseInt(mdsId));

		try {
			/**
			 * Ni Proxy
			 */
			HashMap niParam = new HashMap();
			niParam.put("baudRate", rateValue);
			CommandNIProxy niProxy = new CommandNIProxy();

			if(requestType.equals("GET")){
				resultObj = (MeterBaud)niProxy.execute(modem, NIAttributeId.MeterBaud_GET, niParam);
			}else{
				resultObj = (MeterBaud)niProxy.execute(modem, NIAttributeId.MeterBaud, niParam);
			}

		} catch (Exception e) {
			log.error(e, e);
		}

		// return map 작성
		if(resultObj!=null){
			if(resultObj.getBaudRate() == null){
				map.put("cmdResult", "Reponse of MeterBuad-Command is invalid");
			}else {
				map.put("baudRate", resultObj.toString());
				map.put("cmdResult", "Execution OK");
			}
		}else{
			map.put("cmdResult", "Failed to sending a command in CommandNIProxy.");
		}
		return map;
	}
	
	/**
	 * Real Time Metering
	 **/
	public Map<String, String> cmdRealTimeMetering(String mdsId, int interval, int duration) throws Exception {
		log.debug("## NI command - Real Time Metering ["+mdsId+"],["+interval+"],["+duration+"]");
		// 결과 맵
		Map map = new HashMap<String, String>();
		RealTimeMetering resultObj = null;

		Target target = new Target();
		// 커맨드 전송할 모뎀 조회
		Meter meter = meterDao.get(mdsId);
		Modem modem = modemDao.get(meter.getModemId());

		try {
			/**
			 * Ni Proxy
			 */
			HashMap niParam = new HashMap();
			niParam.put("interval", interval);
			niParam.put("duration", duration);
			CommandNIProxy niProxy = new CommandNIProxy();
			resultObj = (RealTimeMetering)niProxy.execute(modem, NIAttributeId.RealTimeMetering, niParam);
			

		} catch (Exception e) {
			log.error(e, e);
		}

		// return map 작성
		if(resultObj!=null){
			if(resultObj.getStatus() == null){
				map.put("cmdResult", "Reponse of RealTimeMetering-Command is invalid");
			}else {
				map.put("result", resultObj.getStatus());
				map.put("cmdResult", "Execution OK");
			}
		}else{
			map.put("cmdResult", "Failed to sending a command in CommandNIProxy.");
		}
		return map;
	}

	/**
	 * Command External Command HD, 200.1.1, pre-defined protocol message for
	 * external project
	 *
	 * @return
	 * @throws Exception
	 * @ejb.interface-method view-type="both"
	 */
	@Override
	public Map<String, String> cmdExtCommand(String mcuId, byte[] generalStream) throws Exception {
		log.debug("cmdExtCommand(" + mcuId + ")");

		Target target = CmdUtil.getTarget(mcuId);

		Vector<SMIValue> datas = null;
		// 전달받은 파라미터 SMIValue로 변환
		if (target.getNameSpace() != null && target.getNameSpace().equals("HD")) {
			datas = new Vector<SMIValue>();
			SMIValue smiValue = DataUtil.getSMIValue(new OCTET(generalStream));
			datas.add(smiValue);
		} else {
			log.error("Null or Unsupported Namespace");
			throw new Exception("cmdExtCommand supports only namespace HD.");
		}

		Object[] params = new Object[] { target, "cmdExtCommand", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector" };

		Object obj = null;
		try {
			// 생성한 파라미터를 통해 커맨드 실행
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		// invoke의 리턴값 확인
		SMIValue[] smiValues = null;
		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		// return map 작성
		Map map = new HashMap<String, String>();
		if (smiValues != null && smiValues.length != 0) {
			map.put("NodeResult", smiValues[0].toString());
			// ..
		}

		return map;
	}

	// icmpPing - For Hibernate 
	@Override
	public String icmpPing(List<String> commands) throws Exception {
		
		NetworkInfoLog nlog = new NetworkInfoLog();
		String str = "";
    	String cmdResult = "";
		
    	// 명령 실행 영역 (S)
		ProcessBuilder pb = new ProcessBuilder(commands);
		Process process = pb.start();
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
	        
		try {
			while ((str = stdInput.readLine()) != null) {
				cmdResult += str + "\n";
			}

			if (cmdResult == "") {
				log.debug("Network is unreachable");
				return "FAIL";
			}
		} catch (Exception e) {
			log.error("Exception occurred in icmpPing : " + e,e);
		}
		// 명령 실행 영역 (E)
		
		log.info(cmdResult);
		return cmdResult;
	}
	
	@Override
	public String traceroute(List<String> commands) throws Exception {
		
		NetworkInfoLog nlog = new NetworkInfoLog();
		String str = "";
    	String cmdResult = "";
		
    	// 명령 실행 영역 (S)
		ProcessBuilder pb = new ProcessBuilder(commands);
		Process process = pb.start();
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
	        
		try {
			while ((str = stdInput.readLine()) != null) {
				cmdResult += str + "\n";
			}

			if (cmdResult == "") {
				log.debug("Network is unreachable");
				return "FAIL";
			}
		} catch (Exception e) {
			log.error("Exception occurred in traceroute : " + e,e);
		}
		// 명령 실행 영역 (E)
		
		log.info(cmdResult);
		return cmdResult;
	}
	
	/**
	 * after authentication for modem, hes has to send result to mcu. if modemid
	 * is null, result is not sent.
	 *
	 * @param serialNo
	 *            MCU ID or modem Id
	 * @param status
	 *            0:invalid mac, 1:valid mac, 2:3pass approved
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public void cmdAuthSPModem(String serialNo, int status) throws FMPMcuException, Exception {
		log.debug("cmdAuthSPModem(" + serialNo + "," + status + ")");

		Modem modem = null;
		if (serialNo != null && !"".equals(serialNo))
			modem = modemDao.get(serialNo);

		if (modem == null)
			return;

		if (modem.getMcu() == null)
			return;

		Target target = CmdUtil.getTarget(modem.getMcu().getSysID());

		Vector<SMIValue> datas = new Vector<SMIValue>();
		datas.add(DataUtil.getSMIValueByObject("eui64Entry", modem.getDeviceSerial()));
		datas.add(DataUtil.getSMIValue(new INT(status)));

		Object[] params = new Object[] { target, "cmdAuthorizeSPModem", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}
	}
	
	public void cmdAuthSPModem(String serialNo, int status, String mcuId) throws FMPMcuException, Exception {
        log.debug("cmdAuthSPModem(" + serialNo + "," + status + "," + mcuId + ")");

        Target target = CmdUtil.getTarget(mcuId);

        Vector<SMIValue> datas = new Vector<SMIValue>();
        datas.add(DataUtil.getSMIValueByObject("eui64Entry", serialNo));
        datas.add(DataUtil.getSMIValue(new INT(status)));

        Object[] params = new Object[] { target, "cmdAuthorizeSPModem", datas };

        String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector" };

        Object obj = null;
        try {
            obj = invoke(params, types);
        } catch (Exception e) {
            log.error(e, e);
            throw new Exception(makeMessage(e.getMessage()));
        }

        if (obj instanceof Integer) {
            log.error("Error Code Return");
            throw makeMcuException(((Integer) obj).intValue());
        }
    }

	/**
	 * cmdSetMeterSharedKey 111.1.5
	 *
	 * @param mcuId
	 * @param meterId
	 * @param masterKey
	 * @param unicastKey
	 * @param multicastKey
	 * @param AuthenticationKey
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public void cmdSetMeterSharedKey(String mcuId, String meterId, String masterKey, String unicastKey,
			String multicastKey, String AuthenticationKey) throws FMPMcuException, Exception {
		log.debug("cmdSetMeterSharedKey(" + mcuId + "," + meterId + "," + masterKey + "," + unicastKey + ","
				+ multicastKey + "," + AuthenticationKey + ")");

		Target target = CmdUtil.getTarget(mcuId);

		Vector<SMIValue> datas = new Vector<SMIValue>();
		datas.add(DataUtil.getSMIValueByObject("stringEntry", meterId));
		datas.add(DataUtil.getSMIValue(new OCTET(Hex.encode(masterKey))));
		datas.add(DataUtil.getSMIValue(new OCTET(Hex.encode(multicastKey))));
		datas.add(DataUtil.getSMIValue(new OCTET(Hex.encode(unicastKey))));
		datas.add(DataUtil.getSMIValue(new OCTET(Hex.encode(AuthenticationKey))));

		Object[] params = new Object[] { target, "cmdSetMeterSharedKey", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}
	}
	
    /**
     * Get Standard event log throught DLMS.
     * for Kaifa
     * 
     * modemId is deviceSerial
     * fromDate and toDate format : yyyymmddhhMMss
     * 								When you input empty, date will be set today
     * 
     * @param modemId
     * @param fromDate
     * @param toDate
     * @return Map<String,Object>
     * @throws Exception
     */
    public Map<String,Object> cmdGetStandardEventLog(String modemId, String fromDate, String toDate) throws Exception{
    	Map<String,Object> result = new HashMap<String,Object>();
    	
    	log.debug("cmdGetStandardEventLog(" + modemId + ")");
		
		String obisCode = this.convertObis(OBIS.STANDARD_EVENT.getCode());
		int classId = DLMS_CLASS.PROFILE_GENERIC.getClazz();
		int attrId = DLMS_CLASS_ATTR.PROFILE_GENERIC_ATTR02.getAttr();
		
		Map<String,String> valueMap = eventLogValueByRange(fromDate,toDate);
		Map<String,String> paramMap = new HashMap<String,String>();
		String value = meterParamMapToJSON(valueMap);
		
		log.debug("obisCode => " + obisCode + ", classId => " + classId + ", attributeId => " + attrId);
		String param = obisCode+"|"+classId+"|"+attrId+"|null|null|"+value;
		result = cmdMeterParamGet(modemId,param);
		
		return result;
    }
    
    /**
     * Get Tampering log throught DLMS.
     * for Kaifa
     * 
     * modemId is deviceSerial
     * fromDate and toDate format : yyyymmddhhMMss
     * 								When you input empty, date will be set today
     * 
     * @param modemId
     * @param fromDate
     * @param toDate
     * @return Map<String,Object>
     * @throws Exception
     */
    public Map<String,Object> cmdGetTamperingLog(String modemId, String fromDate, String toDate) throws Exception{
    	Map<String,Object> result = new HashMap<String,Object>();
    
    	log.debug("cmdGetTamperingLog(" + modemId + ")");
		
		String obisCode = this.convertObis(OBIS.TAMPER_EVENT.getCode());
		int classId = DLMS_CLASS.PROFILE_GENERIC.getClazz();
		int attrId = DLMS_CLASS_ATTR.PROFILE_GENERIC_ATTR02.getAttr();
		
		Map<String,String> valueMap = eventLogValueByRange(fromDate,toDate);
		Map<String,String> paramMap = new HashMap<String,String>();
		String value = meterParamMapToJSON(valueMap);
		
		log.debug("obisCode => " + obisCode + ", classId => " + classId + ", attributeId => " + attrId);
		String param = obisCode+"|"+classId+"|"+attrId+"|null|null|"+value;
		result = cmdMeterParamGet(modemId,param);
		
		return result;
    }
    
    /**
     * Get Power failure log throught DLMS.
     * for Kaifa
     * 
     * modemId is deviceSerial
     * fromDate and toDate format : yyyymmddhhMMss
     * 								When you input empty, date will be set today
     * 
     * @param modemId
     * @param fromDate
     * @param toDate
     * @return Map<String,Object>
     * @throws Exception
     */
    public Map<String,Object> cmdGetPowerFailureLog(String modemId, String fromDate, String toDate) throws Exception{
    	Map<String,Object> result = new HashMap<String,Object>();
    	
    	log.debug("cmdGetPowerFailureLog(" + modemId + ")");
		
		String obisCode = this.convertObis(OBIS.POWERFAILURE_LOG.getCode());
		int classId = DLMS_CLASS.PROFILE_GENERIC.getClazz();
		int attrId = DLMS_CLASS_ATTR.PROFILE_GENERIC_ATTR02.getAttr();
		
		Map<String,String> valueMap = eventLogValueByRange(fromDate,toDate);
		Map<String,String> paramMap = new HashMap<String,String>();
		String value = meterParamMapToJSON(valueMap);
		
		log.debug("obisCode => " + obisCode + ", classId => " + classId + ", attributeId => " + attrId);
		String param = obisCode+"|"+classId+"|"+attrId+"|null|null|"+value;
		result = cmdMeterParamGet(modemId,param);
		
		return result;
    }
    
    /**
     * Get Control log throught DLMS.
     * for Kaifa
     * 
     * modemId is deviceSerial
     * fromDate and toDate format : yyyymmddhhMMss
     * 								When you input empty, date will be set today
     * 
     * @param modemId
     * @param fromDate
     * @param toDate
     * @return Map<String,Object>
     * @throws Exception
     */
    public Map<String,Object> cmdGetControlLog(String modemId, String fromDate, String toDate) throws Exception{
    	Map<String,Object> result = new HashMap<String,Object>();
    	
    	log.debug("cmdGetControlLog(" + modemId + ")");
		
		String obisCode = this.convertObis(OBIS.CONTROL_LOG.getCode());
		int classId = DLMS_CLASS.PROFILE_GENERIC.getClazz();
		int attrId = DLMS_CLASS_ATTR.PROFILE_GENERIC_ATTR02.getAttr();
		
		Map<String,String> valueMap = eventLogValueByRange(fromDate,toDate);
		Map<String,String> paramMap = new HashMap<String,String>();
		String value = meterParamMapToJSON(valueMap);
		
		log.debug("obisCode => " + obisCode + ", classId => " + classId + ", attributeId => " + attrId);
		String param = obisCode+"|"+classId+"|"+attrId+"|null|null|"+value;
		result = cmdMeterParamGet(modemId,param);
		
		return result;
    }
    
    /**
     * Get Power Quality log throught DLMS.
     * for Kaifa
     * 
     * modemId is deviceSerial
     * fromDate and toDate format : yyyymmddhhMMss
     * 								When you input empty, date will be set today
     * 
     * @param modemId
     * @param fromDate
     * @param toDate
     * @return Map<String,Object>
     * @throws Exception
     */
    public Map<String,Object> cmdGetPQLog(String modemId, String fromDate, String toDate) throws Exception{
    	Map<String,Object> result = new HashMap<String,Object>();
    	
    	log.debug("cmdGetPQLog(" + modemId + ")");
		
		String obisCode = this.convertObis(OBIS.POWER_QUALITY_LOG.getCode());
		int classId = DLMS_CLASS.PROFILE_GENERIC.getClazz();
		int attrId = DLMS_CLASS_ATTR.PROFILE_GENERIC_ATTR02.getAttr();
		
		Map<String,String> valueMap = eventLogValueByRange(fromDate,toDate);
		Map<String,String> paramMap = new HashMap<String,String>();
		String value = meterParamMapToJSON(valueMap);
		
		log.debug("obisCode => " + obisCode + ", classId => " + classId + ", attributeId => " + attrId);
		String param = obisCode+"|"+classId+"|"+attrId+"|null|null|"+value;
		result = cmdMeterParamGet(modemId,param);
		
		return result;
    }
    
    /**
     * Get Firmware upgrade log throught DLMS.
     * for Kaifa
     * 
     * modemId is deviceSerial
     * fromDate and toDate format : yyyymmddhhMMss
     * 								When you input empty, date will be set today
     * 
     * @param modemId
     * @param fromDate
     * @param toDate
     * @return Map<String,Object>
     * @throws Exception
     */
    public Map<String,Object> cmdGetFWUpgradeLog(String modemId, String fromDate, String toDate) throws Exception{
    	Map<String,Object> result = new HashMap<String,Object>();
    	
    	log.debug("cmdGetFWUpgradeLog(" + modemId + ")");
		
		String obisCode = this.convertObis(OBIS.FIRMWARE_UPGRADE_LOG.getCode());
		int classId = DLMS_CLASS.PROFILE_GENERIC.getClazz();
		int attrId = DLMS_CLASS_ATTR.PROFILE_GENERIC_ATTR02.getAttr();
		
		Map<String,String> valueMap = eventLogValueByRange(fromDate,toDate);
		Map<String,String> paramMap = new HashMap<String,String>();
		String value = meterParamMapToJSON(valueMap);
		
		log.debug("obisCode => " + obisCode + ", classId => " + classId + ", attributeId => " + attrId);
		String param = obisCode+"|"+classId+"|"+attrId+"|null|null|"+value;
		result = cmdMeterParamGet(modemId,param);
		
		return result;
    }
    
    /**
     * Hex ObisCode => x.x.x.x.x.x 
     * 
     * @param obisCode
     * @return
     */
    public String convertObis(String obisCode) {
    	String returnData = "";
    	if(obisCode.length() == 12) {
    		byte[] obisCodeArr = Hex.encode(obisCode);
    		obisCode="";
    		for (int i = 0; i < obisCodeArr.length; i++) {
    			if(i == 0) {
    				obisCode += DataUtil.getIntToByte(obisCodeArr[i]);
    			} else {
    				obisCode += "."+DataUtil.getIntToByte(obisCodeArr[i]);
    			}
			}
    		returnData = obisCode;
    	} else {
    		returnData = "Wrong Obis";
    	}
    	
    	return returnData;
    }
    
    public Map<String,String> eventLogValueByRange(String fromDate, String toDate) throws Exception {
    	Map<String,String> valueMap = new HashMap<String,String>();
    	
    	String clockObis = this.convertObis(OBIS.CLOCK.getCode());
		String option="1";	//option 0 is offset, option 1 is range_descriptor(date). but not yet implement offset.
		
    	valueMap.put("clockObis", clockObis);
		valueMap.put("option", option);
		Calendar fromCal = null;
		if (fromDate != null && !fromDate.equals("")) {
			fromCal = DateTimeUtil.getCalendar(fromDate);
		} else {
			fromCal = Calendar.getInstance();
		}
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		fromDate = formatter.format(fromCal.getTime());
		valueMap.put("fYear", fromDate.substring(0,4));
		valueMap.put("fMonth", fromDate.substring(4,6));
		valueMap.put("fDayOfMonth", fromDate.substring(6,8));
		valueMap.put("fDayOfWeek", String.valueOf(fromCal.get(Calendar.DAY_OF_WEEK)));
		valueMap.put("fHh", fromDate.substring(8,10));
		valueMap.put("fMm", fromDate.substring(10,12));
		valueMap.put("fSs", fromDate.substring(12,14));
		
		Calendar toCal = null;
		if (toDate != null && !toDate.equals("")) {
			toCal = DateTimeUtil.getCalendar(toDate);
		} else {
			toCal = Calendar.getInstance();
		}
		toDate = formatter.format(toCal.getTime());
		
		valueMap.put("tYear", toDate.substring(0,4));
		valueMap.put("tMonth", toDate.substring(4,6));
		valueMap.put("tDayOfMonth", toDate.substring(6,8));
		valueMap.put("tDayOfWeek", String.valueOf(toCal.get(Calendar.DAY_OF_WEEK)));
		valueMap.put("tHh", toDate.substring(8,10));
		valueMap.put("tMm", toDate.substring(10,12));
		valueMap.put("tSs", toDate.substring(12,14));

		return valueMap;
    }

    /**
     * Command 실행 : cmdMeterParamGet
     * @param modemId
     * @return
     * @throws Exception 
     */
    public Map<String,Object> cmdMeterParamGet(String modemId, String param) throws Exception{
    	Map<String,Object> result = new HashMap<String,Object>();
    	
    	log.debug("cmdMeterParamGet(" + modemId + ")");
		Target target = CmdUtil.getNullBypassTarget(modemDao.get(modemId));
		if(target != null){
			log.debug("Target Info => " + target.toString());
			log.debug("param => " + param);
			
			/*
			 * 
			 * 임시코드
			 * Ethernet 모뎀
			 * 
			 */
			
			//target = new Target();
//			target.setNameSpace("SP");
//			target.setIpAddr("187.1.30.190");
//			target.setIpv6Addr("187.1.30.190");
//			target.setPort(9001);
//			target.setReceiverType("MMIU");
//			target.setProtocol(Protocol.IP);
			
			
			/**
			 * Bypass Request
			 */
			if (target.getNameSpace().equals("NG")) { // IRAQ
			} else if (target.getNameSpace().equals("SP")) { // SORIA
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("paramGet", param);
				
				BypassClient bypassClient = new BypassClient(target);
				bypassClient.setParams(params);
				
				BypassResult bypassResult = bypassClient.excute("cmdMeterParamGet");
				log.debug("[cmdMeterParamGet] Excute Result = " + bypassResult.toString());
				
				if(bypassResult.getResultValue() instanceof Map) {
					result = (Map<String, Object>) bypassResult.getResultValue();
				} else {
					Map<String,Object> map = new HashMap<String,Object>();
					result.put("RESULT_VALUE", bypassResult.getResultValue());
				}
			}			
		}else{
			result.put("RESULT_VALUE", "Can not found target. please check Meter & Modem information.");
		}
		
		return result;
    }
    
    /**
     * Command 실행 : cmdMeterParamSet
     * @param modemId
     * @return
     * @throws Exception 
     */
    public Map<String,Object> cmdMeterParamSet(String modemId, String param) throws Exception{
    	Map<String,Object> result = new HashMap<String,Object>();
    	
    	log.debug("cmdMeterParamSet(" + modemId + ")");
        Target target = CmdUtil.getNullBypassTarget(modemDao.get(modemId));
		if(target != null){
			log.debug("Target Info => " + target.toString());
			log.debug("param => " + param);
			/**
			 * Bypass Request
			 */
			if (target.getNameSpace().equals("NG")) { // IRAQ

			} else if (target.getNameSpace().equals("SP")) { // SORIA
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("paramSet", param);
				
				BypassClient bypassClient = new BypassClient(target);
				bypassClient.setParams(params);
				
				BypassResult bypassResult = bypassClient.excute("cmdMeterParamSet");
				log.debug("[cmdMeterParamSet] Excute Result = " + bypassResult.toString());
				
				if(bypassResult.getResultValue() instanceof Map) {
					result = (Map<String, Object>) bypassResult.getResultValue();
				} else {
					Map<String,Object> map = new HashMap<String,Object>();
					result.put("RESULT_VALUE", bypassResult.getResultValue());
				}
			}			
		}else{
			result.put("RESULT_VALUE", "Can not found target. please check Meter & Modem information.");
		}
		
		return result;
    }

    /**
     * Command 실행 : cmdMeterParamAct
     * 
     * @param modemId
     * @return
     * @throws Exception 
     */
    public Map<String,Object> cmdMeterParamAct(String modemId, String param) throws Exception{
    	Map<String,Object> result = new HashMap<String,Object>();
    	
    	log.debug("cmdMeterParamAct(" + modemId + ")");
        Target target = CmdUtil.getNullBypassTarget(modemDao.get(modemId));
        if(target != null){
    		/**
    		 * Bypass Request
    		 */
    		if (target.getNameSpace().equals("NG")) { // IRAQ

    		} else if (target.getNameSpace().equals("SP")) { // SORIA
    			Map<String, Object> params = new HashMap<String, Object>();
    			params.put("paramAct", param);
    			
    			BypassClient bypassClient = new BypassClient(target);
    			bypassClient.setParams(params);
    			
    			BypassResult bypassResult = bypassClient.excute("cmdMeterParamAct");
    			log.debug("[cmdMeterParam] Excute Result = " + bypassResult.toString());
    			
    			if(bypassResult.getResultValue() instanceof Map) {
    				result = (Map<String, Object>) bypassResult.getResultValue();
    			} else {
    				Map<String,Object> map = new HashMap<String,Object>();
    				result.put("RESULT_VALUE", bypassResult.getResultValue());
    			}
    		}        	
        }else{
        	result.put("RESULT_VALUE", "Can not found target. please check Meter & Modem information.");
        }
		
		return result;
    }
    
	/**
	 * Command 실행 : cmdGetMeterFWVersion
	 * 
	 * @param modemId
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> cmdGetMeterFWVersion(String meterId) throws Exception {
		Map<String,Object> resultMap = new HashMap<String,Object>();

		try {
			log.debug("cmdGetMeterFWVersion(" + meterId + ")");

			Target target = CmdUtil.getNullBypassTarget(meterDao.get(meterId));
			if(target != null){
//				/*
//				 * 
//				 * 임시코드
//				 * Ethernet 모뎀
//				 * 
//				 */
//				target = new Target();
//				target.setNameSpace("SP");
//				target.setIpAddr("187.1.30.190");
//				target.setIpv6Addr("187.1.30.190");
//				target.setPort(9001);
//				target.setReceiverType("MMIU");
//				target.setProtocol(Protocol.IP);
				
				log.debug("## Target = " + target.toString());

				/**
				 * Bypass Request
				 */
				if (target.getNameSpace().equals("NG")) { // IRAQ

				} else if (target.getNameSpace().equals("SP")) { // SORIA
					
					BypassClient bypassClient = null;
					try  {
						
						bypassClient = new BypassClient(target);
						BypassResult bypassResult = null;
						int useNiBypass = Integer.parseInt(FMPProperty.getProperty("soria.protocol.modem.nibypass.use" , "0"));
			 			if ( useNiBypass > 0){
			 				log.debug("[cmdGetMeterFWVersion] Using NI Bypass.");
			 				bypassResult = bypassClient.excuteNiBypass("cmdGetMeterFWVersion");
			 			} else {
			 				log.debug("[cmdGetMeterFWVersion] Using Null Bypass.");
			 				bypassResult = bypassClient.excute("cmdGetMeterFWVersion");
			 			}
						
						log.debug("[cmdBypassGetMeterFWVersion] Excute Result = " + bypassResult.toString());

						resultMap = (HashMap<String, Object>)bypassResult.getResultValue();
						log.debug("### cmdBypassGetMeterFWVersion 결과 = " + String.valueOf(resultMap.get("resultValue")));						
					} catch (Exception e) {
						if(bypassClient != null) bypassClient.close();
						log.error("## cmdGetMeterFWVersion Excute Error - " + e.getMessage());
					}
				}							
			}else{
				resultMap.put("result", false);
				resultMap.put("resultValue", "Can not found target. please check MBB Meter & Modem information.");
			}
		} catch (Exception e) {
			log.error("cmdGetMeterFWVersion error - " + e, e);
		}
		
		return resultMap;
	}

	
	/**
	 * Command 실행 : cmdSORIAGetMeterKey
	 * 
	 * @param modemId
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> cmdSORIAGetMeterKey(String meterId) throws Exception {
		Map<String,Object> resultMap = new HashMap<String,Object>();

		try {
			log.debug("cmdSORIAGetMeterKey(" + meterId + ")");

			Target target = CmdUtil.getNullBypassTarget(meterDao.get(meterId));
			if(target != null){
				log.debug("## Target = " + target.toString());

				/**
				 * Bypass Request
				 */
				if (target.getNameSpace().equals("NG")) { // IRAQ

				} else if (target.getNameSpace().equals("SP")) { // SORIA
					
					BypassClient bypassClient = null;
					try  {
						
						bypassClient = new BypassClient(target);
						BypassResult bypassResult = null;
						int useNiBypass = Integer.parseInt(FMPProperty.getProperty("soria.protocol.modem.nibypass.use" , "0"));
			 			if ( useNiBypass > 0){
			 				log.debug("[cmdSORIAGetMeterKey] Using NI Bypass.");
			 				bypassResult = bypassClient.excuteNiBypass("cmdSORIAGetMeterKey");
			 			} else {
			 				log.debug("[cmdSORIAGetMeterKey] Using Null Bypass.");
			 				bypassResult = bypassClient.excute("cmdSORIAGetMeterKey");
			 			}
						
						log.debug("[cmdBypassSORIAGetMeterKey] Excute Result = " + bypassResult.toString());
						resultMap = (HashMap<String, Object>)bypassResult.getResultValue();
						log.debug("### cmdBypassSORIAGetMeterKey 결과22 = " + String.valueOf(resultMap.get("resultValue")));

					} catch (Exception e) {
						if(bypassClient != null) bypassClient.close();
						log.error("## cmdBypassSORIAGetMeterKey Excute Error - " + e.getMessage());
					}
				}							
			}else{
				resultMap.put("result", false);
				resultMap.put("resultValue", "Can not found target. please check MBB Meter & Modem information.");
			}
		} catch (Exception e) {
			log.error("cmdSORIAGetMeterKey error - " + e, e);
		}
		
		return resultMap;
	}
	
	
	/**
	 * Command 실행 : cmdMultiFirmwareOTA
	 * @param otaTargetType
	 * @param deviceList
	 * @param take_over
	 * @param useNullBypass
	 * @param firmwareId
	 * @return
	 * @throws Exception
	 */
// UPDATE START SP-681
//	public Map<String, Object> cmdMultiFirmwareOTA(OTATargetType otaTargetType
//			, List<String> deviceList, String take_over, boolean useNullBypass, String firmwareId) throws Exception {
	public Map<String, Object> cmdMultiFirmwareOTA(OTATargetType otaTargetType
			, List<String> deviceList, String take_over, boolean useNullBypass, String firmwareId
			, String optVersion, String optModel, String optTime) throws Exception {
// UPDATE END SP-681
		
		log.debug("firmwareId => " + firmwareId);
		
		Firmware firmware = firmwareDao.get(Integer.parseInt(firmwareId));
		if(firmware == null){
			throw new Exception("Unknown Firmware");
		}
		String fileURL = firmware.getFileUrlPath();
		String checkSum = firmware.getCheckSum();
		String fw_crc = firmware.getCrc();
		String fw_version = firmware.getFwVersion();
		String imageKey = firmware.getImageKey();
		
		// DCU, MODEM의 경우 Image Kery를 펌웨어버전으로 넣어준다.
		if(imageKey == null || imageKey.equals("")){
			imageKey = firmware.getFwVersion();
		}
		
		
//		String openTime = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");
//		EV_SP_200_64_0_Action action = new EV_SP_200_64_0_Action();
//		action.makeEvent(TargetClass.EnergyMeter, "5100000000000226", TargetClass.EnergyMeter, openTime, "HES");
//		action.updateOTAHistory("5100000000000226", com.aimir.constants.CommonConstants.DeviceType.Meter, openTime);
////		
		
	//	boolean excuteRFMode = false; 
		log.debug("cmdMultiFirmwareOTA(" + deviceList.toString() + ") OTATargetType=" + otaTargetType + ", FileURL=" + fileURL + ", CheckSum=" + checkSum + " , fw_crc=" + fw_crc + ", fw_version=" + fw_version + ", take_over=" + take_over + ", useNullBypass=" + useNullBypass + ", imageKey=" + imageKey);	
		Map<String, Object> result = new HashMap<String, Object>();

		Map<String, List<String>> mcuListMap = new HashMap<String, List<String>>();
		List<Target> targetList = new ArrayList<Target>();
		List<String> mcuList = new ArrayList<String>();
        List<String> invalidTargetList = new ArrayList<String>();
		
		try {
			/**
			 *  1. OTA 대상 산출
			 */
			for(String deviceId : deviceList){
				/*
				 * DCU & DCU Kernel OTA
				 */
				if(otaTargetType == OTATargetType.DCU || otaTargetType == OTATargetType.DCU_KERNEL || otaTargetType == OTATargetType.DCU_COORDINATE){
					if(mcuDao.get(deviceId) != null){
						mcuList.add(deviceId);		
					}else{
						log.debug("UnRegistration MCU. Please check MCU List - " + deviceId);
					}
				}
				
				/*
				 * Meter OTA
				 */
				else if(otaTargetType == OTATargetType.METER){
					Meter meter = meterDao.get(deviceId);
					Modem modem = meter.getModem();
					MCU mcu = modem.getMcu();
					
					if (modem.getModemType() == ModemType.MMIU && modem.getProtocolType() == Protocol.SMS) { // MBB Modem
						useNullBypass = true;
					} else if (modem.getModemType() == ModemType.MMIU && modem.getProtocolType() == Protocol.IP) { // Ethernet Modem
						Target target = CmdUtil.getNullBypassTarget(meter);
						if(target == null){
							invalidTargetList.add(deviceId);
							log.debug("Meter=[" + meter.getMdsId() + "], Modem=[" + modem.getDeviceSerial() + "], ModemType=" + modem.getModemType() + ", Protocol=" + modem.getProtocolType() + " This meter is invalid meter. please check Meter & Modem mapping");
							useNullBypass = true;
						}else{
							targetList.add(target);							
						}

					} else if (modem.getModemType() == ModemType.SubGiga && modem.getProtocolType() == Protocol.IP && (mcu != null && useNullBypass == false)) { // DCU - RF Modem
	            		if(mcuListMap.containsKey(mcu.getSysID())){
	            			List<String> list = mcuListMap.get(mcu.getSysID());
	            			list.add(meter.getMdsId());
	            		}else{
	            			List<String> mList = new ArrayList<>();
	            			mList.add(meter.getMdsId());
	            			mcuListMap.put(mcu.getSysID(), mList);
	            		}
					} else if (modem.getModemType() == ModemType.SubGiga && modem.getProtocolType() == Protocol.IP) { // RF Modem
				//		excuteRFMode = true;
						Target target = CmdUtil.getNullBypassTarget(meter);
						if(target == null){
							invalidTargetList.add(deviceId);
							log.debug("Meter=[" + meter.getMdsId() + "], Modem=[" + modem.getDeviceSerial() + "], ModemType=" + modem.getModemType() + ", Protocol=" + modem.getProtocolType() + " This meter is invalid meter. please check Meter & Modem mapping");
						}else{
							targetList.add(target);							
						}
					}
				}
				
				/*
				 * Modem OTA
				 */
				else if(otaTargetType == OTATargetType.MODEM){
					Modem modem = modemDao.get(deviceId);
					MCU mcu = modem.getMcu();
					
					if (modem.getModemType() == ModemType.MMIU && modem.getProtocolType() == Protocol.SMS) { // MBB Modem
						useNullBypass = true;
					} else if (modem.getModemType() == ModemType.MMIU && modem.getProtocolType() == Protocol.IP) { // Ethernet Modem
						Target target = CmdUtil.getNullBypassTarget(modem);
						if(target == null){
							invalidTargetList.add(deviceId);
							log.debug("Modem=[" + modem.getDeviceSerial() + "], ModemType=" + modem.getModemType() + ", Protocol=" + modem.getProtocolType() + " This modem is invalid meter. please check Meter & Modem mapping");
						}else{
							targetList.add(target);							
						}
						useNullBypass = true;
					} else if (modem.getModemType() == ModemType.SubGiga && modem.getProtocolType() == Protocol.IP && (mcu != null && useNullBypass == false)) { // DCU - RF Modem
	            		if(mcuListMap.containsKey(mcu.getSysID())){
	            			List<String> list = mcuListMap.get(mcu.getSysID());
	            			list.add(modem.getDeviceSerial());
	            		}else{
	            			List<String> mList = new ArrayList<>();
	            			mList.add(modem.getDeviceSerial());
	            			mcuListMap.put(mcu.getSysID(), mList);
	            		}
					} else if (modem.getModemType() == ModemType.SubGiga && modem.getProtocolType() == Protocol.IP) { // RF Modem
//						excuteRFMode = true;
						Target target = CmdUtil.getNullBypassTarget(modem);
						if(target == null){
							invalidTargetList.add(deviceId);
							log.debug("Modem=[" + modem.getDeviceSerial() + "], ModemType=" + modem.getModemType() + ", Protocol=" + modem.getProtocolType() + " This modem is invalid meter. please check Meter & Modem mapping");
						}else{
							targetList.add(target);							
						}
					}
				} else{
					throw new FMPException("Empty device");
				}
			}

			/**
			 * 2. OTA 파일 준비
			 */
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("fw_crc", fw_crc);
			params.put("fw_version", fw_version);
			params.put("image_identifier", imageKey);
			params.put("take_over", take_over);
			
			if(otaTargetType == OTATargetType.DCU || otaTargetType == OTATargetType.DCU_KERNEL || otaTargetType == OTATargetType.DCU_COORDINATE || useNullBypass == false){
				/**
				 * # mode=0 => http, mode=1 => https
				   #ota.firmware.download.mode=1
				   #ota.firmware.download.port=8081
				   #ota.firmware.download.port.ssl=8441
				   #ota.firmware.download.dir=/home/aimir/aimir4/aimiramm/aimir-fep-exec/FEP1/tomcat.8081/webapps
                   #ota.firmware.download.ip=FD40::2
                 *  
				 * MBB DCU, ETH DCU => https://[fd40::2]:8441/ota/fw/dcu/NURITelecom/NDC-I336/06010055/NDC-I336-soria-1.0.0-d-000338.8bfe6c1.050.tar.gz
				 * 
				 */
				int otaMode = Integer.parseInt(FMPProperty.getProperty("ota.firmware.download.mode"));
				
				String otaIp = "";
				String otaPort = "";
				String otaUrl = "";
				String ipAddress = FMPProperty.getProperty("ota.firmware.download.ip");
				boolean isIPv4 = true;
				
				/* Check IPv4/IPv6
				 * 	     isIPv4 is true  => IPv4
				 * 		 isIPv4 is false => IPv6
				 */
				if(IPUtil.checkIPv4(ipAddress))
					isIPv4 = true;
				else if(IPUtil.checkIPv6(ipAddress))
					isIPv4 = false;
				else{
					log.debug ("Firmware download IP is invalid");
					result.put("Firmware download IP is invalid", ipAddress);
					return result;
				}
				
				// Protocol Setting
				if(otaMode == 0){  // HTTP
					if(isIPv4)
						otaIp = "http://" + ipAddress;
					else
						otaIp = "http://[" + ipAddress + "]";
					otaPort = FMPProperty.getProperty("ota.firmware.download.port");
				}else{   // HTTPS
					if(isIPv4)
						otaIp = "https://" + ipAddress;
					else
						otaIp = "https://[" + ipAddress + "]";
					otaPort = FMPProperty.getProperty("ota.firmware.download.port.ssl");
				}
				
				// Make OTA URL
				otaUrl = otaIp + ":" + otaPort + "/" + FMPProperty.getProperty("feph.webservice.ota.context"); 
				
				params.put("fw_path", otaUrl + "/" + fileURL);
			} else{
				params.put("fw_path", FMPProperty.getProperty("ota.firmware.download.dir") + "/" + fileURL);
				
				Path filePath = Paths.get((String)params.get("fw_path"));				
				byte[] fileArray = Files.readAllBytes(filePath);
				
				params.put("image", fileArray);
			}
			log.debug("cmdMultiFirmwareOTA OTA params = " + params.toString());
			
			/**
			 * 3. OTA 진행
			 */
			/*
			 * DCU / DCU Kernel / DCU Codi
			 */
			if(otaTargetType == OTATargetType.DCU || otaTargetType == OTATargetType.DCU_KERNEL || otaTargetType == OTATargetType.DCU_COORDINATE){
				try {
					List<IBatchCallable> callableList = new LinkedList<>();
				
					for(String mcuSysId : mcuList){
						boolean resultState = false;
						List<String> filterList = new ArrayList<String>();
						filterList.add(mcuSysId);
						
						OTAType otaType = null;
						if(otaTargetType == OTATargetType.DCU_KERNEL){
							otaType = OTAType.DCU_KERNEL;
						}else if(otaTargetType == OTATargetType.DCU_COORDINATE){
							otaType = OTAType.DCU_COORDINATE;
						}else{
							otaType = OTAType.DCU;
						}
						
						SORIADcuOTACallable callable = new SORIADcuOTACallable(this
								, mcuSysId
								, otaType 
								, params.get("image_identifier").toString()
								, params.get("fw_path").toString()
								, checkSum
								, filterList);
						callableList.add(callable);
					}
					
					//CallableBatchExcutor cbe = new CallableBatchExcutor(5);
					CallableBatchExcutor cbe = new CallableBatchExcutor();
					boolean excuteResult = cbe.execute(otaTargetType.name(), callableList);
					
					if(excuteResult){
						int count = 1;
						List<Map<CBE_RESULT_CONSTANTS, Object>> resultList = cbe.getAllList();
						for (Map<CBE_RESULT_CONSTANTS, Object> map : resultList) {
							JSONObject jo = new JSONObject();
							jo.put("RESULT", ((CBE_STATUS_CONSTANTS) map.get(CBE_RESULT_CONSTANTS.RESULT_STATE)) == CBE_STATUS_CONSTANTS.SUCCESS  ? true : false);
							jo.put("RESULT_VALUE", map.get(CBE_RESULT_CONSTANTS.RESULT_VALUE));

							log.info(count 
									+ ". TARGET_ID=" + map.get(CBE_RESULT_CONSTANTS.TARGET_ID)
									+ ", RESULT=" + map.get(CBE_RESULT_CONSTANTS.RESULT_STATE) 
									+ ", RESULT_VALUE=" +  map.get(CBE_RESULT_CONSTANTS.RESULT_VALUE));
							
							result.put((String) map.get(CBE_RESULT_CONSTANTS.TARGET_ID), jo.toString());
							count++;
						}
						
						// 잘못된 타겟(MBUS인경우??)의 경우 리스트에 추가
						for(String device : invalidTargetList){
							JSONObject jo = new JSONObject();
							jo.put("RESULT", false);
							jo.put("RESULT_VALUE", "This device is invalid device. please check device information.");
							
							result.put(device, jo.toString());	
						}
					}else{
						result.put("OTA Excute Error", "DCU/DCU_KERNEL OTA Excute Fail");
						log.error("DCU/DCU_KERNEL/DCU_COORDINATE OTABatch Error - DCU/DCU_KERNEL/DCU_COORDINATE OTA Excute Fail");
					}
				} catch (Exception e) {
					result.put("DCU/DCU_KERNEL/DCU_COORDINATE OTA Error", e.getMessage());
					log.error("DCU/DCU_KERNEL/DCU_COORDINATE OTABatch Error -" + e, e);
				}
			} 
			/*
			 * Meter
			 */
			else if(otaTargetType == OTATargetType.METER){
				try {
					List<IBatchCallable> callableList = new LinkedList<>();
					
					/*
					 * DCU 통해서 OTA 하는경우 
					 */
					if(mcuListMap != null && 0 < mcuListMap.size()){
						Iterator<String> it = mcuListMap.keySet().iterator();
						while(it.hasNext()){
							String mcuSysId = it.next();
							
							SORIADcuOTACallable callable = new SORIADcuOTACallable(this
									, mcuSysId
									, OTAType.METER_RF_BY_DCU 
									, params.get("image_identifier").toString()
									, params.get("fw_path").toString()
									, checkSum
									, mcuListMap.get(mcuSysId));
							callableList.add(callable);
						}
					}
					
					/*
					 * HES통해서 OTA 하는경우
					 */
					if(targetList != null && 0 < targetList.size()){
						for(Target target : targetList){
							SORIAMeterOTACallable callable = new SORIAMeterOTACallable(target, params);
							callableList.add(callable);
						}						
					}

					if(0 < callableList.size()){
//						CallableBatchExcutor cbe = null;
//						if(excuteRFMode){
//							cbe = new CallableBatchExcutor(1);	
//						}else{
//							cbe = new CallableBatchExcutor(5);
//						}
						CallableBatchExcutor cbe = new CallableBatchExcutor();
						
						
						boolean excuteResult = cbe.execute(otaTargetType.name(), callableList);
						
						if(excuteResult){
							int count = 1;
							List<Map<CBE_RESULT_CONSTANTS, Object>> resultList = cbe.getAllList();
							for (Map<CBE_RESULT_CONSTANTS, Object> map : resultList) {
								JSONObject jo = new JSONObject();
								jo.put("RESULT", ((CBE_STATUS_CONSTANTS) map.get(CBE_RESULT_CONSTANTS.RESULT_STATE)) == CBE_STATUS_CONSTANTS.SUCCESS  ? true : false);
								jo.put("RESULT_VALUE", map.get(CBE_RESULT_CONSTANTS.RESULT_VALUE));

								String targetId = map.get(CBE_RESULT_CONSTANTS.TARGET_ID).toString();
								// DCU 통해서 처리한 결과
								if(mcuListMap != null && 0 < mcuListMap.size() && mcuListMap.containsKey(targetId)){
									List<String> filterList = mcuListMap.get(targetId);
									for(String mdsId : filterList){
										log.info(count 
												+ ". TARGET_ID=" + mdsId
												+ ", RESULT=" + map.get(CBE_RESULT_CONSTANTS.RESULT_STATE) 
												+ ", RESULT_VALUE=" +  map.get(CBE_RESULT_CONSTANTS.RESULT_VALUE));
										
										result.put(mdsId, jo.toString());										
									}
								}
								// HES 통해서 처리한 결과
								else{
									log.info(count 
											+ ". TARGET_ID=" + map.get(CBE_RESULT_CONSTANTS.TARGET_ID)
											+ ", RESULT=" + map.get(CBE_RESULT_CONSTANTS.RESULT_STATE) 
											+ ", RESULT_VALUE=" +  map.get(CBE_RESULT_CONSTANTS.RESULT_VALUE));
									
									result.put((String) map.get(CBE_RESULT_CONSTANTS.TARGET_ID), jo.toString());									
								}

								count++;
							}
							
							// 잘못된 타겟(MBUS인경우??)의 경우 리스트에 추가
							for(String device : invalidTargetList){
								JSONObject jo = new JSONObject();
								jo.put("RESULT", false);
								jo.put("RESULT_VALUE", "This meter is invalid meter. please check Meter & Modem mapping");
								
								result.put(device, jo.toString());	
							}
						}else{
							result.put("MeterOTA Excute Error", "Meter OTA Excute Fail");
							log.error("Meter OTABatch Error - Meter OTA Excute Fail");
						}
					}else{
						result.put("OTA Error", "Have no OTA Target.");	
					}
				} catch (Exception e) {
					result.put("OTA Error", e.getMessage());
					log.error("OTABatch Error -" + e, e);
				}
			} 
			/*
			 * Modem
			 */
			else if(otaTargetType == OTATargetType.MODEM){
				try {
					List<IBatchCallable> callableList = new LinkedList<>();
					
					// INSERT START SP-681
					params.put("optversion", optVersion);
					params.put("optmodel", optModel);
					params.put("opttime", optTime);
					// INSERT END SP-681
					
					/*
					 * DCU 통해서 OTA 하는경우 
					 */
					if(mcuListMap != null && 0 < mcuListMap.size()){
						Iterator<String> it = mcuListMap.keySet().iterator();
						while(it.hasNext()){
							String mcuSysId = it.next();
							
							SORIADcuOTACallable callable = new SORIADcuOTACallable(this
									, mcuSysId
									, OTAType.MODEM_RF_BY_DCU 
									, params.get("image_identifier").toString()
									, params.get("fw_path").toString()
									, checkSum
									, mcuListMap.get(mcuSysId));
							callableList.add(callable);
						}
					}
					
					/*
					 * HES통해서 OTA 하는경우
					 */
					if(targetList != null && 0 < targetList.size()){
						for(Target target : targetList){
							SORIAModemOTACallable callable = new SORIAModemOTACallable(target, params);
							callableList.add(callable);
						}						
					}

					if(0 < callableList.size()){
//						CallableBatchExcutor cbe = null;
//						if(excuteRFMode){
//							cbe = new CallableBatchExcutor(1);	
//						}else{
//							cbe = new CallableBatchExcutor(5);
//						}

						CallableBatchExcutor cbe = new CallableBatchExcutor();
						
						boolean excuteResult = cbe.execute(otaTargetType.name(), callableList);
						
						if(excuteResult){
							int count = 1;
							List<Map<CBE_RESULT_CONSTANTS, Object>> resultList = cbe.getAllList();
							for (Map<CBE_RESULT_CONSTANTS, Object> map : resultList) {
								JSONObject jo = new JSONObject();
								jo.put("RESULT", ((CBE_STATUS_CONSTANTS) map.get(CBE_RESULT_CONSTANTS.RESULT_STATE)) == CBE_STATUS_CONSTANTS.SUCCESS  ? true : false);
								jo.put("RESULT_VALUE", map.get(CBE_RESULT_CONSTANTS.RESULT_VALUE));

								String targetId = map.get(CBE_RESULT_CONSTANTS.TARGET_ID).toString();
								// DCU 통해서 처리한 결과
								if(mcuListMap != null && 0 < mcuListMap.size() && mcuListMap.containsKey(targetId)){
									List<String> filterList = mcuListMap.get(targetId);
									for(String deviceSerial : filterList){
										log.info(count 
												+ ". TARGET_ID=" + deviceSerial
												+ ", RESULT=" + map.get(CBE_RESULT_CONSTANTS.RESULT_STATE) 
												+ ", RESULT_VALUE=" +  map.get(CBE_RESULT_CONSTANTS.RESULT_VALUE));
										
										result.put(deviceSerial, jo.toString());										
									}
								}
								// HES 통해서 처리한 결과
								else{
									log.info(count 
											+ ". TARGET_ID=" + map.get(CBE_RESULT_CONSTANTS.TARGET_ID)
											+ ", RESULT=" + map.get(CBE_RESULT_CONSTANTS.RESULT_STATE) 
											+ ", RESULT_VALUE=" +  map.get(CBE_RESULT_CONSTANTS.RESULT_VALUE));
									
									result.put((String) map.get(CBE_RESULT_CONSTANTS.TARGET_ID), jo.toString());									
								}

								count++;
							}
							
							// 잘못된 타겟(MBUS인경우??)의 경우 리스트에 추가
							for(String device : invalidTargetList){
								JSONObject jo = new JSONObject();
								jo.put("RESULT", false);
								jo.put("RESULT_VALUE", "This modem is invalid modem. please check Meter & Modem mapping");
								
								result.put(device, jo.toString());	
							}
						}else{
							result.put("Modem OTA Excute Error", "Modem OTA Excute Fail");
							log.error("OTABatch Error - Modem OTA Excute Fail");
						}
					}else{
						result.put("OTA Error", "Have no OTA Target.");	
					}
				} catch (Exception e) {
					result.put("OTA Error", e.getMessage());
					log.error("OTABatch Error -" + e, e);
				}
			} else{
				throw new FMPException("Empty device");
			}

			log.debug("### cmdMultiFirmwareOTA [" + otaTargetType + "] Result = " + result);
			
		} catch (Exception e) {
			log.error("cmdMultiFirmwareOTA Error - " + e, e);
			
			JSONObject jo = new JSONObject();
			jo.put("RESULT", false);
			jo.put("RESULT_VALUE", "cmdMultiFirmwareOTA Fail. - " + e.getMessage());
			
			result.put(deviceList.toString(), jo.toString());	
		}
		
		return result;
	}

	/**
	 * MCU Get Log
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param count
	 *            get count
	 * @return event list
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public String cmdMcuGetLog(String mcuId, int count) throws FMPMcuException, Exception {
		log.debug("cmdMcuGetLog MCU[" + mcuId + "], COUNT[" + count + "]");

		Target target = CmdUtil.getTarget(mcuId);
		/*String _count = FMPProperty.getProperty("soria.mcu.geteventlog.count");
		if (_count != "") {
			count = Integer.parseInt(_count);
		}*/
		Vector<SMIValue> datas = new Vector<SMIValue>();
		if (target.getNameSpace() != null && target.getNameSpace().equals("SP")) {
			datas.add(DataUtil.getSMIValue(target.getNameSpace(), new INT(count)));
		} else {
			log.error("Null or Unsupported Namespace");
			throw new Exception("cmdMcuGetLog supports only namespace SP.");
		}

		Object[] params = new Object[] { target, "cmdGetEvent", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}
		
		byte[] stream = null;
		SMIValue[] smiValues1 = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[] ) {
			smiValues1 = (SMIValue[])obj;
			log.debug("obj instanceof SMIValue[" + smiValues1.length + "]");
		}else {
			log.error("Unknown Return Value ( Class="+ obj.getClass().getName() + ")");
			throw new Exception("Unknown Return Value");
		}
		
		StringBuffer resultBuffer =  new StringBuffer();
		MIBUtil mibUtil = MIBUtil.getInstance(target.getNameSpace());
		for(int index = 0 ; index < smiValues1.length; index++){
			Object o = smiValues1[index].getVariable();
			if ( o instanceof OCTET) {
				stream =  ((OCTET) o).getValue();
			} else {
				log.error("Unknown Return Value ( Class="+ o.getClass().getName() + ")");
				throw new Exception("Unknown Return Value");
			}
	
			if ( stream.length < 25) {
				log.error("Data Length is too Short");
				throw new Exception("Data Length is too Short");	
			}
			
			try {
				IoBuffer iobuf = IoBuffer.allocate(stream.length);
				iobuf.put(stream,0,stream.length);
				iobuf.position(0);
				int listnum = 0;
				int rec = stream.length/25;
				int rec_cnt=0;
				
				//while( iobuf.hasRemaining() && iobuf.position() + 25 >= iobuf.limit()) {
				while( iobuf.hasRemaining() ) {
					//log.debug("counter = " + listnum + ", position =" + iobuf.position() + ", limit  = " + iobuf.limit());
					log.debug("counter = " + listnum + ", position =" + iobuf.position() + ", limit  = " + iobuf.limit());
					rec_cnt++;
					if ( rec_cnt>rec ){
						log.debug("Data Length is wrong.length[" + stream.length + "]");
						break;
					}
					ArrayList<SMIValue> smiValuesList = new ArrayList<SMIValue>();
					UINT srcMcuId = new UINT();
					OID code = new OID();
					BYTE srcType = new BYTE(); 
					HEX srcId = new HEX(8); 
					TIMESTAMP timeStamp = new TIMESTAMP(7); 
					WORD cnt = new WORD();
		
					srcMcuId.decode(null, iobuf, 4);
					code.decode(null, iobuf, 3);
					srcType.decode(null, iobuf, 1);
					srcId.decode(null, iobuf, 8);
					timeStamp.decode(null, iobuf, 7);
					cnt.decode(null, iobuf, 2);
					String eventName = mibUtil.getName(code.toString()) == null ? "" :  mibUtil.getName(code.toString());
					String result= "# " + eventName + ", " + getEventSrcTypeStr(srcType) + ", " + 
							srcId.toString()+ ", " +  timeStamp.toString()+", " +
							cnt.toString() + "\n";
					log.debug("SVCH=" + result);
					int k = 0;
					while( k < cnt.getValue() && iobuf.hasRemaining()){
		
						SMIValue simv = new SMIValue();
						simv.decode(target.getNameSpace(), iobuf, 0);
						smiValuesList.add(simv);		
						log.debug("simvalue[" + k + "]=" +  simv.toString());
						k++;
					}
		
					SMIValue[] smiValues  = smiValuesList.toArray(new SMIValue[0]);
					String res = "";
					String record = "";
	
					resultBuffer.append(result);
					
					if (smiValues != null && smiValues.length != 0) {
						for (int i = 0; i < smiValues.length; i++) {
	
							String oid = smiValues[i].getOid().toString();
							String name = mibUtil.getName(oid) == null ? "" :  mibUtil.getName(oid);
							
							//SP-723
							String value = smiValues[i].getVariable() == null ? "" : smiValues[i].getVariable().toString();
							
							value =  value.matches("\\p{Print}*") ? 
									value : Hex.decode(value.getBytes());
							record =name + ", " + oid +", "+ value + "\n";
							res = new StringBuilder(res).append(record).toString();
	
						}
						resultBuffer.append(res);
						resultBuffer.append(" \n");
						
					} else {
						log.debug("smiValues is null");
					}
					listnum ++;
				}
				log.debug(resultBuffer.toString());
			} catch (Exception e){
				log.error(e,e);
				throw e;
			}
		
	}
		return resultBuffer.toString();

	}



	public String getEventSrcTypeStr(BYTE type){
		String srcTypeStr = "";
		byte btype = (byte)(type.getValue() & 0xff);
		switch(btype){
			case 0x01 : 
				srcTypeStr = "FEP";
				break;
			case 0x02 : 
				srcTypeStr = "DataConcentrator";
				break;
			case 0x03 : srcTypeStr = "OAM PC (SERIAL)";
				break;
			case 0x04 : 
				srcTypeStr = "OAM PDA (FR)";
				break;
			case 0x05 : 
				srcTypeStr = "Mobile unit(GSM/CDMA)";
				break;
			case 0x06 : 
				srcTypeStr = "Master modem";
				break;
			case 0x07 : 
				srcTypeStr = "RF - Router modem";
				break;
			case 0x08 : 
				srcTypeStr = "RF - End modem";
				break;
			case 0x09 : 
				srcTypeStr = "RF - Expansion unit";
				break;
			case 0x14 : 
				srcTypeStr = "PLC - modem";
				break;
			case 0x16 :
				srcTypeStr = "Serial - modem(RS232,RS485)";
				break;
			default :
				srcTypeStr = type.toString();

		}
		return srcTypeStr;
	}
	/**
	 * MCU Get Schedule
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param name
	 *            Schedule Name
	 * @return schdule list
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public Map<String, Object> cmdMcuGetSchedule(String mcuId, String name) throws FMPMcuException, Exception {
		log.debug("cmdMcuGetSchedule MCU[" + mcuId + "], Name[" + name + "]");

		Target target = CmdUtil.getTarget(mcuId);
		Vector<SMIValue> datas = null;

		if (name != null && !"".equals(name)) {
			datas = new Vector<SMIValue>();
			datas.add(DataUtil.getSMIValue(target.getNameSpace(), new STRING(name)));
		}

		Object[] params = new Object[] { target, "cmdGetSchedule", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		Map<String, Object> result = new HashMap<String, Object>();		
		try{
		if (smiValues != null && smiValues.length != 0) {
			for (int i = 0; i < smiValues.length; i += 4) {
				if ("metering".equals(smiValues[i + 0].getVariable().toString())) {
						result.put("metering_name", smiValues[i + 0] == null? "" : smiValues[i + 0].getVariable().toString());
						result.put("metering_suspend", smiValues[i + 1] == null? "" : smiValues[i + 1].getVariable().toString());
						result.put("metering_condition", smiValues[i + 2] == null? "" : smiValues[i + 2].getVariable().toString());
						result.put("metering_task", smiValues[i + 3] == null? "" : smiValues[i + 3].getVariable().toString());
				} else if ("recovery".equals(smiValues[i + 0].getVariable().toString())) {
						result.put("recovery_name", smiValues[i + 0] == null? "" : smiValues[i + 0].getVariable().toString());
						result.put("recovery_suspend", smiValues[i + 1] == null? "" : smiValues[i + 1].getVariable().toString());
						result.put("recovery_condition", smiValues[i + 2] == null? "" : smiValues[i + 2].getVariable().toString());
						result.put("recovery_task", smiValues[i + 3] == null? "" : smiValues[i + 3].getVariable().toString());
				} else if ("upgrade".equals(smiValues[i + 0].getVariable().toString())) {
						result.put("upgrade_name", smiValues[i + 0] == null? "" : smiValues[i + 0].getVariable().toString());
						result.put("upgrade_suspend", smiValues[i + 1] == null? "" : smiValues[i + 1].getVariable().toString());
						result.put("upgrade_condition", smiValues[i + 2] == null? "" : smiValues[i + 2].getVariable().toString());
						result.put("upgrade_task", smiValues[i + 3] == null? "" : smiValues[i + 3].getVariable().toString());
				} else if ("upload".equals(smiValues[i + 0].getVariable().toString())) {
						result.put("upload_name", smiValues[i + 0] == null? "" : smiValues[i + 0].getVariable().toString());
						result.put("upload_suspend", smiValues[i + 1] == null? "" : smiValues[i + 1].getVariable().toString());
						result.put("upload_condition", smiValues[i + 2] == null? "" : smiValues[i + 2].getVariable().toString());
						result.put("upload_task", smiValues[i + 3] == null? "" : smiValues[i + 3].getVariable().toString());
				}
			}
		} else {
			log.debug("smiValues is null");
		}
		}catch (Exception ce){
			log.error(ce,ce);
		}
		

		return result;
	}

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
	@Override
	public void cmdMcuSetSchedule(String mcuId, String[][] args) throws FMPMcuException, Exception {
		log.debug("cmdMcuSetSchedule(" + mcuId + ") args.length(" + args.length + ")");

		Target target = CmdUtil.getTarget(mcuId);
		Vector<SMIValue> datas = null;

		if (args.length == 0) {
			log.debug("no schedule");
			return;
		}

		datas = new Vector<SMIValue>();
		for (int i = 0; i < args.length; i++) {
			datas.add(DataUtil.getSMIValueByObject(target.getNameSpace(), "stringEntry", args[i][0]));
			if (args[i][1] != null && !"0".equals(args[i][1])) {
				datas.add(DataUtil.getSMIValueByObject(target.getNameSpace(), "boolEntry", "1"));
			} else {
				datas.add(DataUtil.getSMIValueByObject(target.getNameSpace(), "boolEntry", "0"));
			}
			datas.add(DataUtil.getSMIValueByObject(target.getNameSpace(), "stringEntry", args[i][2]));
			datas.add(DataUtil.getSMIValueByObject(target.getNameSpace(), "stringEntry", args[i][3]));
		}

		Object[] params = new Object[] { target, "cmdSetSchedule", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}
	}
	
	/**
	 * CmdMeterParamGet/Set 에 들어가는 value 값과 동일한 형태의 JSON으로 만들어준다.
	 * 
	 * @param map
	 * @return
	 */
	@SuppressWarnings("unchecked")
    public String meterParamMapToJSON(Map map) {
        StringBuffer rStr = new StringBuffer();
        Iterator<String> keys = map.keySet().iterator();
        String keyVal = null;
        rStr.append("[{");
        while (keys.hasNext()) {
            keyVal = (String) keys.next();
            rStr.append("\""+keyVal+"\":");
            rStr.append("\""+map.get(keyVal)+"\"");
            if (keys.hasNext()) {
                rStr.append(",");
            }
        }
        rStr.append("}]");
        return rStr.toString();
    }

	/**
	 * MCU Get Property
	 * get : retry interval value or other(name==property key)
	 */
	@Override
	public Map<String, Object> cmdMcuGetProperty(String mcuId, String name) throws FMPMcuException, Exception {
		log.debug("cmdMcuGetProperty MCU[" + mcuId + "], Name[" + name + "]");
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("cmdResult", "FAIL on cmdMcuGetProperty");
		
		Target target = CmdUtil.getTarget(mcuId);
		Vector<SMIValue> datas = null;

		if (name != null && !"".equals(name)) {
			datas = new Vector<SMIValue>();
			//datas.add(DataUtil.getSMIValue(target.getNameSpace(), new STRING(name)));
			datas.add(DataUtil.getSMIValueByObject(target.getNameSpace(), "stringEntry", name));
		}
		
		Object[] params = new Object[] { target, "cmdGetProperty", datas };
		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector" };
		
		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));		
		} 
		
		// 결과 처리
		SMIValue[] smiValues = null;
		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");			
		}
				
		if (smiValues != null && smiValues.length != 0) {		
			for(SMIValue sv : smiValues){
                // expected result [ {oid=*.*.*, variable=octet(*key-name*)}, {oid=*.*.*, variable=octet(*key-value*)} ]
                // example [ {oid=1.11.0, variable=octet(network.retry.default)}, {oid=1.11.0, variable=octet(0)} ]
                String oidStr = sv.getOid().value;
				String varStr = sv.getVariable().toString();
				if(result.containsKey(oidStr)){
					result.put(result.get(oidStr).toString(), varStr);
				}else{
					result.put(oidStr, varStr);
				}				
				// log.debug("cmdMcuGetProperty RESULT["+sv.getOid().value+"]["+sv.getVariable().toString()+"]");
			}
		}else{
			log.debug("smiValue is null");
			result.put("cmdResult", "smiValue is null");
		}
					
		result.put("cmdResult", "SUCCESS on cmdMcuGetProperty");
		return result;
	}

	
	/**
	 * MCU cmdStdGet - CSQ
	 */
	public Map<String, Object> cmdMcuStdGet(String mcuId, String oid) throws FMPMcuException, NumberFormatException, Exception {
		log.debug("cmdStdGet(" + mcuId + "," + oid + ")");

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("cmdResult", "FAIL on cmdMcuStdGet");
		
		Target target = CmdUtil.getTarget(mcuId);
		Vector<SMIValue> datas = null;
		
		if (oid != null && !"".equals(oid)) {
			datas = new Vector<SMIValue>();
			datas.add(DataUtil.getSMIValueByObject(target.getNameSpace(), "oidEntry", oid));
		}
		
		Object[] params = new Object[] { target, "cmdStdGet", datas };
		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (NumberFormatException e) {
			log.error("FAIL on cmdMcuStdGet - NumberFormatException");
			throw new Exception("ID should be number format");
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		// 결과 처리
		SMIValue[] smiValues = null;
		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");			
		}
				
		if (smiValues != null && smiValues.length != 0) {		
			for(SMIValue sv : smiValues){
				
				String oidStr = sv.getOid().value;
				String varStr = sv.getVariable().toString();
				if (result.containsKey(oidStr)) {
					result.put(result.get(oidStr).toString(), varStr);
				} else {
					result.put(oidStr, varStr);
				}				
			}
		} else {
			log.debug("smiValue is null");
			result.put("cmdResult", "smiValue is null");
		}
					
		result.put("cmdResult", "SUCCESS on cmdMcuStdGet");
		return result;
	}
	
	/**
	 * MCU - cmdGetNMSInformation
	 */
	public Map<String, Object> cmdGetMcuNMSInformation(String mcuId) throws FMPMcuException, NumberFormatException, Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		
		Target target = CmdUtil.getTarget(mcuId);
		MCU mcu = mcuDao.get(mcuId);
		
		if (mcu == null || target == null) {
			result.put("cmdResult", "FAIL - Invalid DCU ID");
			return result;
		}
		
		JpaTransactionManager txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");
		TransactionStatus txStatus = null;
		String useTransStr = FMPProperty.getProperty("cmd.getmcunmsinformation.use.transaction", "true");
		boolean useTrans = true;
		if ( "false".equals(useTransStr)){
			useTrans = false;
		}
		
		Vector<SMIValue> datas = null;
		Object obj = null;
		Object[] params = new Object[] { target, "cmdGetNMSInformation", null };
		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector" };
		
		try {
			obj = invoke(params, types);
		} catch (NumberFormatException e) {
			log.error("FAIL on cmdMcuStdGet - NumberFormatException");
			throw new Exception("ID should be number format");
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}
		
		// 결과 처리
		try {
			SMIValue[] smiValues = null;

			if (obj instanceof Integer) {
				log.error("Error Code Return");
				throw makeMcuException(((Integer) obj).intValue());
			} else if (obj instanceof SMIValue[]) {
				smiValues = (SMIValue[]) obj;
			} else {
				log.error("Unknown Return Value");
				throw new Exception("Unknown Return Value");
			}
			
			if (smiValues != null && smiValues.length != 0) {
				// For Section (S)
				for (SMIValue smiValue : smiValues) {
					if (smiValue.getVariable() instanceof OPAQUE) {
						OPAQUE mdv = (OPAQUE) smiValue.getVariable();
						obj = mdv.getValue();
						
						if (obj instanceof modemSPNMSEntry) {
							try {
								txStatus = null;
								if ( useTrans ){
									txStatus = txManager.getTransaction(null);
								}
								modemSPNMSEntry value = (modemSPNMSEntry) obj;
								String moSPId = value.getMoSPId().toHexString();
								String moSPParentNodeId = value.getMoSPParentNodeId().toHexString();
	
								int moSPRssi = value.getMoSPRssi().getValue();
								if (moSPRssi > 127) {
									moSPRssi = (256 - moSPRssi) * -1;
								}
	
								int moSPLQI = value.getMoSPLQI().getValue();
								int moSP_Etx = value.getMoSPEtx().getValue();
								String moSPEtx = "0x" + Integer.toHexString(moSP_Etx);
								
								int moSPCpuUsage = value.getMoSPCpuUsage().getValue();
								int moSPMemoryUsage = value.getMoSPMemoryUsage().getValue();
								long moSPTxDataPacketSize = value.getMoSPTxDataPacketSize().getValue();
								int moSPHopCount = value.getMoSPHopCount().getValue();
								
								Modem modem = modemDao.get(moSPId);
								
								if (modem != null) {
									ModemType targetModemType = modem.getModemType();
									
									if(targetModemType != null) {
										if (targetModemType.equals(ModemType.SubGiga)) {
											SubGiga subGigaModem = null;
											
											try {
												subGigaModem = (SubGiga) modem;
												subGigaModem.setMcuId(mcu.getId()); // 모뎀이 바라보고있는 MCU를 갱신한다.
											} catch (Exception e) {
												log.error("DEVICE_SERIAL [" + moSPId + "] MODEM AND MODEM_TYPE is mismatch.");
											}
											
											if (subGigaModem != null) {
												try {
													Modem modemParent = modemDao.get(moSPParentNodeId);
													subGigaModem.setModem(modemParent);
												} catch (Exception e) {
													log.info("[SKIP] No matching ParentNodeId.");
												}
												
												subGigaModem.setRssi(moSPRssi);
												// set - LQI
												// set - Etx
												// set - CpuUsage
												// set - MemoryUsage
												// set - Hop count
												subGigaModem.setRfPower(moSPTxDataPacketSize);
												subGigaModem.setHopsToBaseStation(moSPHopCount);
												modemDao.update(subGigaModem);
												
												log.info("=== DATA LOG (S) ===");
												log.info("### SubGiga ###");
												log.info("getMoSPId : " + moSPId);
												log.info("moSPParentNodeId : " + moSPParentNodeId);
												log.info("moSPRssi : " + moSPRssi);
												log.info("moSPLQI : " + moSPLQI);
												log.info("moSPEtx : " + moSPEtx);
												log.info("moSPCpuUsage : " + moSPCpuUsage);
												log.info("moSPMemoryUsage : " + moSPMemoryUsage);
												log.info("moSPTxDataPacketSize : " + moSPTxDataPacketSize);
												log.info("moSPHopCount : " + moSPHopCount);
												log.info("=== DATA LOG (E) ===");
												
											}
										} else if (targetModemType.equals(ModemType.MMIU)) {
											MMIU mmiuModem = null;
											
											try {
												mmiuModem = (MMIU) modem;
												mmiuModem.setMcuId(mcu.getId()); // 모뎀이 바라보고있는 MCU를 갱신한다.
											} catch (Exception e) {
												log.error("DEVICE_SERIAL [" + moSPId + "] MODEM AND MODEM_TYPE is mismatch.");
											}
											
											if (mmiuModem != null) {
												try {
													Modem modemParent = modemDao.get(moSPParentNodeId);
													mmiuModem.setModem(modemParent);
												} catch (Exception e) {
													log.info("[SKIP] No matching ParentNodeId.");
												}
												
												// set - RSSI
												// set - LQI
												// set - Etx
												// set - CpuUsage 
												// set - MemoryUsage
												mmiuModem.setRfPower(moSPTxDataPacketSize);
												modemDao.update(mmiuModem);
												
												log.info("=== DATA LOG (S) === ");
												log.info("### MMIU ###");
												log.info("getMoSPId : " + moSPId);
												log.info("moSPParentNodeId : " + moSPParentNodeId);
												log.info("moSPRssi : " + moSPRssi);
												log.info("moSPLQI : " + moSPLQI);
												log.info("moSPEtx : " + moSPEtx);
												log.info("moSPCpuUsage : " + moSPCpuUsage);
												log.info("moSPMemoryUsage : " + moSPMemoryUsage);
												log.info("moSPTxDataPacketSize : " + moSPTxDataPacketSize);
												log.info("=== DATA LOG (E) === ");
											}
										} else {
											log.debug("'Refesh' is supported only for SubGiga, MMIU.");
											result.put("cmdResult", "FAIL - 'Refesh' is supported only for SubGiga, MMIU.");
											//return result;
											throw new Exception("FAIL - 'Refesh' is supported only for SubGiga, MMIU.");
										}
									} else {
										log.error("DEVICE_SERIAL [" + moSPId + "] MODEM_TYPE is NULL.");
									}
								} else {
									log.error("DEVICE_SERIAL [" + moSPId + "] - Non-existent Modem Data in DB");
								}
								if (txStatus != null){
									txManager.commit(txStatus);
									log.debug("commit : [" + moSPId + "]");
									txStatus = null;
								}
							}catch (Exception ee){
								log.error(ee,ee);
								if ( txStatus != null){
									txManager.rollback(txStatus);
									txStatus = null;
								}
								throw new Exception(ee);
							}
						}// if (obj instanceof modemSPNMSEntry) 
					}
				} // For Section (E)
			} else {
				log.error("smiValue is null");
				result.put("cmdResult", "FAIL - smiValue is null");
				return result;
			}
		} catch (Exception e) {
			log.error(e, e);
			result.put("cmdResult", e);
			return result;
		}
		
		result.put("cmdResult", "SUCCESS");
		return result;
	}
	
	/**
	 * cmdMcuSetProperty
	 * set : mcu retry interval time
	 * @param key  Argument Name
	 * @param keyValue Each Argument Value (mappping : key[N]=>keyValue[N])
	 */
	@Override
	public Map<String, Object> cmdMcuSetProperty(String mcuId, String[] key, String[] keyValue) throws FMPMcuException, Exception {
		log.debug("cmdMcuSetProperty(" + mcuId + ") args.length(" + key.length + ")");
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("cmdResult", "FAIL on cmdMcuSetProperty");
		
		Target target = CmdUtil.getTarget(mcuId);
		Vector<SMIValue> datas = null;

		if(key.length == 0 || keyValue.length == 0) {
			log.debug("no argument to set the property");
			result.put("cmdResult", "Argument has no data for MCUSetProperty");
			return result;
		}
		
		datas = new Vector<SMIValue>();		
		for(int k=0; k<key.length; k++){
			// key를 입력한 다음에 해당 key의 value를 순서대로 입력해야 집중기에 정상적으로 전달됨
			datas.add(DataUtil.getSMIValueByObject(target.getNameSpace(), "stringEntry", key[k]));
			datas.add(DataUtil.getSMIValueByObject(target.getNameSpace(), "stringEntry", keyValue[k]));
		}
		
		
		Object[] params = new Object[] { target, "cmdSetProperty", datas };
		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		// 결과 처리
		SMIValue[] smiValues = null;
		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");			
		}
		
		if (smiValues != null && smiValues.length != 0) {
			for(SMIValue sv : smiValues){
				// test result 
				result.put(sv.getOid().value, sv.getVariable().toString());
				log.debug("cmdMcuSetProperty RESULT["+sv.getOid().value+"]["+sv.getVariable().toString()+"]");
			}
		}else{
			log.debug("smiValue is null");
			result.put("cmdResult", "smiValue is null");
		}
					
		result.put("cmdResult", "SUCCESS on cmdMcuSetProperty");
		return result;
	}

	/**
	 * Call EventUtil.sendEvent as command
	 * WEB에서 EclipseLink를 사용하지 않으니, FEP으로 던짐.
	 * @param logId
	 * @param eventStatus
	 * @return
	 * @throws Exception
	 */
	public String cmdSendEventByFep(long logId, String eventStatusName) throws Exception{
		EventAlertLogDao ealdao = DataUtil.getBean(EventAlertLogDao.class);
		EventAlertLog event = ealdao.get(logId);
		EventStatus eventStatus = EventStatus.valueOf(eventStatusName);
		String cmdResult = null;
		if (event == null) {
			cmdResult = "EventAlert[" + logId + "] doesn't exist";
			log.warn(cmdResult);
			return cmdResult;
		}

		event.setStatus(eventStatus);
		EventAlert rule = event.getEventAlert();

		if (rule ==null) {
			cmdResult = "EventAlert[" + rule.getName() + "] not found";
			log.warn(cmdResult);
			return cmdResult;
		}

		event.setOccurCnt(event.getOccurCnt()+1);

		MonitorType monitor = rule.getMonitor();
		
		JpaTransactionManager txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");
		TransactionStatus txStatus = null;
		DefaultTransactionDefinition txDefine = new DefaultTransactionDefinition();
		try {
			txStatus = txManager.getTransaction(txDefine);
			if(monitor == MonitorType.Save  || monitor == MonitorType.SaveAndMonitor) {
				event = EventUtil.saveEventAlertLog(event);
			}
			if(monitor == MonitorType.SaveAndMonitor)
				EventUtil.sendNotification(event);
			txManager.commit(txStatus);
			cmdResult = "SUCCESS";
		} catch (Exception e){
			log.error(e,e);
			if (txStatus != null) txManager.rollback(txStatus);
			cmdResult = "ERROR on Transaction";
		}
				
		return cmdResult;
	}
	

	/**
	 * NI 프로토콜을 통한 커맨드 전송 : SNMP Trap On/Off 설정 모뎀아이디, 요청타입(get/set),
	 * set인경우 설정할 값
	 **/
	public Map<String, String> cmdSnmpTrapOnOff(String mdsId, String requestType, String status) throws Exception {
		log.debug("## NI command - SNMP Trap On/Off ["+mdsId+"],["+requestType+"],["+status+"]");
		// 결과 맵
		Map map = new HashMap<String, String>();
		SnmpTrapOnOff resultObj = null;

		// 커맨드 전송할 모뎀 조회
		Modem modem = modemDao.get(Integer.parseInt(mdsId));

		try {
			/**
			 * Ni Proxy
			 */
			HashMap niParam = new HashMap();
			niParam.put("trapStatus", status);
			CommandNIProxy niProxy = new CommandNIProxy();

			if(requestType.equals("GET")){
				resultObj = (SnmpTrapOnOff)niProxy.execute(modem, NIAttributeId.SnmpTrapOnOff_GET, niParam);
			}else{
				resultObj = (SnmpTrapOnOff)niProxy.execute(modem, NIAttributeId.SnmpTrapOnOff, niParam);
			}

		} catch (Exception e) {
			log.error(e, e);
		}

		// return map 작성
		if(resultObj!=null){
			if(resultObj.getStatus() == null){
				map.put("cmdResult", "Reponse of SnmpTrapOnOff-Command is invalid");
			}else {
				map.put("trapStatus", resultObj.toString());
				map.put("cmdResult", "Execution OK");
			}
		}else{
			map.put("cmdResult", "Failed to sending a command in CommandNIProxy.");
		}
		return map;
	}
	
	/**
	 * NI 프로토콜을 통한 커맨드 전송 : Raw ROM Access 설정 모뎀아이디, 요청타입(get/set),
	 * set인경우 설정할 값
	 **/
	public Map<String, String> cmdRawROMAccess(String mdsId, String requestType, Object[] arrModemRomAddr, Object[] arrModemValid, String[] arrCert) throws Exception {
		
		log.debug("## NI command - Raw ROM Access ["+mdsId+"],["+requestType+"]");
		
		// 결과 맵
		Map map = new HashMap<String, String>();
		RawRomAccess resultObj = null;

		Target target = new Target();
		// 커맨드 전송할 모뎀 조회
		Modem modem = modemDao.get(Integer.parseInt(mdsId));
		
		CommandNIProxy niProxy = new CommandNIProxy();
		
		int packetMaxLen = 250;
		int packetAddrLen = 4;
		int packetLen = 2;
		
		int certMaxLen = 244;		
		int certPaddingLen = 6;
		
		try {
			
			for(int i = 0; i < arrCert.length; i++) {
				
				int modemRomAddr = DataUtil.getIntTo4Byte((byte[])arrModemRomAddr[i]); // rom address
				byte[] modemValid = (byte[])arrModemValid[i]; // valid check value
				String certData	= arrCert[i];
				int certLen = certData.getBytes().length;
				
				ByteBuffer certBuffer = ByteBuffer.allocate(certLen); // 인증서 buffer
				certBuffer.put(certData.getBytes());
				certBuffer.clear();
				
				int loop = (int) Math.ceil((double)(certLen + certPaddingLen) / (double)certMaxLen); // 244		
				int packetSize = 0;			
				int paddingSize = 0;
				int nextAddr = 0;
				
				ByteBuffer packet;
				
				for(int l = 0; l < loop; l++) {
					
					if(l == 0) {
						
						packetSize = (certLen > certMaxLen - certPaddingLen) ? certMaxLen - certPaddingLen : certLen; // 인증서 길이	
						paddingSize = packetAddrLen + packetLen + certPaddingLen;
						
						byte[] cert = new byte[packetSize];
						certBuffer.get(cert); // packet size 만큼 인증서 가져오기						
						packet = ByteBuffer.allocate(packetSize + paddingSize);
						
						packet.put(DataUtil.get4ByteToInt(modemRomAddr)); // rom address
						packet.put(DataUtil.get2ByteToInt(packetSize + certPaddingLen)); // 검증코드(4) + 개인키길이(2) + 인증서(n)
						packet.put(modemValid); // 검증코드 (4) 
						packet.put(DataUtil.get2ByteToInt(cert.length)); // 인증서 길이(2)
						packet.put(cert); // 인증서(n)
						
					} else {
						
						packetSize = (certLen > certMaxLen) ? certMaxLen : certLen;
						paddingSize = packetAddrLen + packetLen;
						
						byte[] cert = new byte[packetSize];
						certBuffer.get(cert); // packet size 만큼 인증서 가져오기
						
						packet = ByteBuffer.allocate(packetSize + paddingSize);
						
						packet.put(DataUtil.get4ByteToInt(modemRomAddr + nextAddr)); // rom address						
						packet.put(DataUtil.get2ByteToInt(cert.length)); // 인증서 길이(2)
						packet.put(cert); // 인증서(n)
					}
					
					certLen = certLen - packetSize;
					nextAddr += packetSize + certPaddingLen;
					
					/**
					 * Ni Proxy
					 */
					HashMap niParam = new HashMap();					
					niParam.put("data", packet.array());
					
					if(requestType.equals("GET")) {
						resultObj = (RawRomAccess)niProxy.execute(modem, NIAttributeId.RawROMAccess_GET, niParam);
					} else {
						resultObj = (RawRomAccess)niProxy.execute(modem, NIAttributeId.RawROMAccess, niParam);
					}
				}				
			}

		} catch (Exception e) {
			log.error(e, e);
		}

		// return map 작성
		if(resultObj!=null){
			if(resultObj.getData() == null){
				map.put("cmdResult", "Reponse of RawRomAccess-Command is invalid");
			}else {
				map.put("cmdResult", "Execution OK");
			}
		}else{
			map.put("cmdResult", "Failed to sending a command in CommandNIProxy.");
		}
		return map;
	}
	
	/**
	 * NI 프로토콜을 통한 커맨드 전송 : Watch Dog TEST 설정 모뎀아이디
	 * TEST용으로만 사용.
	 **/
	public Map<String, String> cmdWatchdogTest(String mdsId) throws Exception {
		log.debug("## NI command - Watch Log ["+mdsId+"]");
		// 결과 맵
		Map map = new HashMap<String, String>();
		WatchdogTest resultObj = null;
		
		// 커맨드 전송할 모뎀 조회
		Modem modem = modemDao.get(Integer.parseInt(mdsId));

		try {
			/**
			 * Ni Proxy
			 */
			HashMap niParam = new HashMap();
			CommandNIProxy niProxy = new CommandNIProxy();
			resultObj = (WatchdogTest)niProxy.execute(modem, NIAttributeId.WatchdogTest, niParam);

		} catch (Exception e) {
			log.error(e, e);
		}

		// return map 작성
		if(resultObj!=null){
			map.put("cmdResult", "Execution OK");
			
		}else{
			map.put("cmdResult", "Failed to sending a command in CommandNIProxy.");
		}
		return map;
	}
	
	/**
	 * NI Protocol : APN(get),
	 * 
	 **/
	public Map<String, String> cmdGetApn(String mdsId) throws Exception {
		log.debug("## NI command - APN ["+mdsId+"]");
		
		Map map = new HashMap<String, String>();
		Apn resultObj = null;

		//
		Modem modem = modemDao.get(Integer.parseInt(mdsId));

		try {
			/**
			 * Ni Proxy
			 */
			HashMap niParam = new HashMap();
			//niParam.put("baudRate", rateValue);
			CommandNIProxy niProxy = new CommandNIProxy();

			// requestType = "GET" only
			resultObj = (Apn)niProxy.execute(modem, NIAttributeId.APN, niParam);
		} catch (Exception e) {
			log.error(e, e);
		}

		// return map 작성
		if(resultObj!=null){
			if(resultObj.getApn() == null){
				map.put("cmdResult", "Reponse of APN-Command is invalid");
			}else {
				map.put("apn", resultObj.toString());
				map.put("cmdResult", "Execution OK");
			}
		}else{
			map.put("cmdResult", "Failed to sending a command in CommandNIProxy.");
		}
		return map;
	}

	/**
	 * NI Protocol - SET or GET metering interval of MODEM
	 * @param modemId  modem.id
	 * @param requestType  request [get/set]
	 * @param interval  interval value to set
     * @return [get : interval data, set : result status(hexa code)]
     */
	public Map<String, String> cmdMeteringInterval(String modemId, String requestType, int interval) throws Exception{
		log.debug("## NI command - Metering Interval ["+modemId+"],["+requestType+"],["+interval+"]");

		// define result map
		Map map = new HashMap<String, String>();
		MeteringInterval resultObj = null;

		// 커맨드 전송할 모뎀 조회, Find modem object by modem.id from modem table
		Modem modem = modemDao.get(Integer.parseInt(modemId));

		try {
			/**
			 * Ni Proxy - command invoke indirectly using ni proxy
			 */
			HashMap niParam = new HashMap();
			niParam.put("interval", interval);
			CommandNIProxy niProxy = new CommandNIProxy();

			if(requestType.equals("GET")){
				resultObj = (MeteringInterval)niProxy.execute(modem, NIAttributeId.MeteringInterval_GET, niParam);
			}else{
                if(interval < 60 || interval > 21600){
                    map.put("cmdResult", "[Invalid Parameter] MeteringInterval (60~21600 seconds) ");
					return map;
                }else{
                    resultObj = (MeteringInterval)niProxy.execute(modem, NIAttributeId.MeteringInterval, niParam);
                }
			}

		} catch (Exception e) {
			log.error(e, e);
		}

		// put the result data to return map
		if(resultObj!=null){
			if(resultObj.getInterval() < 0){
				map.put("cmdResult", "Reponse of MeteringInterval-Command is invalid");
			}else {
				map.put("interval", ""+resultObj.getIntervalAsTime());
				map.put("intervalStatus", ""+resultObj.getStatusString());
				map.put("cmdResult", "Execution OK");
			}
		}else{
			map.put("cmdResult", "Failed to sending a command in CommandNIProxy.");
		}
		return map;
	}


	/**
	 * NI Protocol - SET or GET push retry count
	 * @param modemId  modem.id
	 * @param requestType  request [get/set]
	 * @param retry  number of retry
	 * @return  retry count
	 */
	public Map<String, String> cmdRetryCount(String modemId, String requestType, int retry) throws Exception {
		log.debug("## NI command - Retry Count ["+modemId+"],["+requestType+"],["+retry+"]");

		// define result map
		Map map = new HashMap<String, String>();
		RetryCount resultObj = null;

		// 커맨드 전송할 모뎀 조회, Find modem object by modem.id from modem table
		Modem modem = modemDao.get(Integer.parseInt(modemId));

		try {
			/**
			 * Ni Proxy - command invoke indirectly using ni proxy
			 */
			HashMap niParam = new HashMap();
			niParam.put("retryCount", retry);
			CommandNIProxy niProxy = new CommandNIProxy();

			if(requestType.equals("GET")){
				resultObj = (RetryCount)niProxy.execute(modem, NIAttributeId.RetryCount_GET, niParam);
			}else{
				if(retry < 0  || retry > 10){
                    map.put("cmdResult", "[Invalid Parameter] RetryCount (0~10) ");
                    return map;
                }else{
                    resultObj = (RetryCount)niProxy.execute(modem, NIAttributeId.RetryCount, niParam);
                }
			}

		} catch (Exception e) {
			log.error(e, e);
		}

		// put the result data to return map
		if(resultObj!=null){
			if(resultObj.getRetryCount() < 0){
				map.put("cmdResult", "Reponse of RetryCount-Command is invalid");
			}else {
				map.put("retryCount", ""+resultObj.getRetryCount());
				map.put("cmdResult", "Execution OK");
			}
		}else{
			map.put("cmdResult", "Failed to sending a command in CommandNIProxy.");
		}
		return map;
	}

	// INSERT START SP-179
	// UPDATE START SP-633
	@Override
	public MeterData cmdGetMeteringData(String mcuId, String meterId, String modemId, String nOption, String fromDate,
			String toDate, String[] modemArray) throws FMPMcuException, Exception {
		
		if (modemArray == null) {
			log.debug("cmdGetMetringData(" + mcuId + "," + meterId + "," + modemId + "," + nOption + "," + fromDate + ","
				+ toDate + ")");
		} else {
			log.debug("cmdGetMetringData(" + mcuId + "," + meterId + "," + modemId + "," + nOption + "," + fromDate + ","
					+ toDate + ", ModemList[" + modemArray.length + "])");			
		}
			
		Target target = null;
		Meter meter = null;
		JpaTransactionManager txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");
		TransactionStatus txStatus = null;
		String command = "cmdGetMeteringData";

		try {
			txStatus = txManager.getTransaction(null);
			meter = meterDao.get(meterId);
			Modem modem = meter.getModem();

			ModemType modemType = modem.getModemType();
			target = CmdUtil.getTarget(modem);
			// MCU mcu = mcuDao.get(mcuId);
			mcuId = target.getTargetId();
			DeviceModel model = meter.getModel();

			log.debug("meterModel=" + model.getId());
			// log.debug("meterVendor="+model.getDeviceVendor().getName());
			int lpInterval = meter.getLpInterval();

			txManager.commit(txStatus);

			int[] offsetCount = CmdUtil.convertOffsetCount(model, lpInterval, modemType, fromDate, toDate);

			Vector<SMIValue> datas = new Vector<SMIValue>();

			SMIValue smiValue = null;

			if ("SP".equals(target.getNameSpace())) {
				
				if (modemArray != null) {	// DEVICE_TYPE = MCU
					smiValue = DataUtil.getSMIValue(target.getNameSpace(), new INT(0));  //////////////count???????????????
					datas.add(smiValue);

					smiValue = DataUtil.getSMIValue(target.getNameSpace(), new INT(0x52));// FILTER_DATA_AFTER_TIME
					datas.add(smiValue);
					smiValue = DataUtil.getSMIValueByObject(target.getNameSpace(), "stringEntry", fromDate.substring(0, 8));
					datas.add(smiValue);

					smiValue = DataUtil.getSMIValue(target.getNameSpace(), new INT(0x53));// FILTER_DATA_BEFORE_TIME
					datas.add(smiValue);
					smiValue = DataUtil.getSMIValueByObject(target.getNameSpace(), "stringEntry", toDate.substring(0, 8));
					datas.add(smiValue);
					int useMeter = Integer.parseInt(FMPProperty.getProperty("cmd.getmeteringdata.use.meter" , "0"));

					for (String mid : modemArray) {
						//smiValue = DataUtil.getSMIValue(target.getNameSpace(), new INT(0x02));// filter type
						if ( useMeter==1 ){
							smiValue = DataUtil.getSMIValue(target.getNameSpace(), new INT(0x01));// FILTER_METERID
						}else {
							smiValue = DataUtil.getSMIValue(target.getNameSpace(), new INT(0x02));// filter MODEM
						}
						datas.add(smiValue);
						smiValue = DataUtil.getSMIValueByObject(target.getNameSpace(), "stringEntry", mid);// filter type
						datas.add(smiValue);
						log.debug("Device=" + mid);
					}
				} else {
					//smiValue = DataUtil.getSMIValue(target.getNameSpace(),
					//		new INT(Integer.parseInt(nOption == null || nOption.equals("") ? "0" : nOption)));
					//datas.add(smiValue);
					smiValue = DataUtil.getSMIValue(target.getNameSpace(), new INT(0));  //////////////count???????????????
					datas.add(smiValue);

					smiValue = DataUtil.getSMIValue(target.getNameSpace(), new INT(0x52));// FILTER_DATA_AFTER_TIME
					datas.add(smiValue);
					smiValue = DataUtil.getSMIValueByObject(target.getNameSpace(), "stringEntry", fromDate.substring(0, 8));
					datas.add(smiValue);

					smiValue = DataUtil.getSMIValue(target.getNameSpace(), new INT(0x53));// FILTER_DATA_BEFORE_TIME
					datas.add(smiValue);
					smiValue = DataUtil.getSMIValueByObject(target.getNameSpace(), "stringEntry", toDate.substring(0, 8));
					datas.add(smiValue);
				
					smiValue = DataUtil.getSMIValue(target.getNameSpace(), new INT(0x01));// FILTER_METERID
					datas.add(smiValue);
					smiValue = DataUtil.getSMIValueByObject(target.getNameSpace(), "stringEntry", meterId); // meterID
					datas.add(smiValue);
				}
				
			}

			Object[] params = new Object[] { target, command, // UPDATE SP-116
					datas };

			String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };
			
			long startLongTime = System.currentTimeMillis();
			Object obj = null;
			try {
				obj = invoke(params, types);
			} catch (Exception e) {
				log.error(e, e);
				throw new Exception(makeMessage(e.getMessage()));
			}
			long endLongTime = System.currentTimeMillis();

			SMIValue[] smiValues = null;

			if (obj instanceof Integer) {
				log.error("Error Code Return");
				throw makeMcuException(((Integer) obj).intValue());
			} else if (obj instanceof SMIValue[]) {
				smiValues = (SMIValue[]) obj;
				if (smiValues.length > 0) {
					log.debug("smiValues.length = " + smiValues.length);
					smiValue = smiValues[0];
				} else {
					log.error("smiValue size 0");
					throw new FMPMcuException("no value exist");
				}
			} else {
				log.error("Unknown Return Value");
				throw new FMPMcuException("Unknown Return Value");
			}

			if (modemArray != null) {
				log.debug("SMIValue Return Class=" + smiValue.getVariable().getClass().getName());

				smiValues = (SMIValue[]) obj;

				for ( int j = 0; j < smiValues.length; j++ ){
					smiValue = smiValues[j];
					log.debug("smiValue[" + j + "] = " + smiValue.toString());
					
					if (smiValue.getVariable() instanceof OPAQUE) {

						OPAQUE mdv = (OPAQUE) smiValue.getVariable();
						log.debug("Get Meter : return ClassName[" + mdv.getClsName() + "] MIB[" + mdv.getMIBName() + "]");

						obj = mdv.getValue();
						if (obj instanceof meterDataEntry) {
							meterDataEntry value = (meterDataEntry) obj;

							log.debug("meterDataEntry [" + value.toString() + "]");
							
							int dataLen = 7 + value.getMdData().getValue().length;
							ByteArray ba = new ByteArray();
							ba.append(value.getMdID().getValue());
							ba.append(value.getMdSerial().getValue());
							ba.append(value.getMdType().encode());
							ba.append(value.getMdServiceType().encode());
							ba.append(value.getMdVendor().encode());
							ba.append(value.getDataCount().encode());
							ba.append(DataUtil.get2ByteToInt(true, dataLen)); // Timestamp.length+Data.length
							ba.append(value.getMdTime().encode());
							ba.append(value.getMdData().getValue());
					
							MDData mdData = new MDData(new WORD(1));
							mdData.setMcuId(mcuId);
							mdData.setTotalLength(ba.toByteArray().length);
							mdData.setMdData(ba.toByteArray());						
							
							putServiceData(target, mdData, startLongTime, endLongTime);
						}

					}

				}
				
				return null;
			}
			
			txStatus = txManager.getTransaction(null);
			meter = meterDao.get(meterId);
			MeterData ed = new MeterData();
			MeterDataParser edp = null;
			DeviceModel deviceModel = meter.getModel();

			if (deviceModel == null)
				deviceModel = meter.getModem().getModel();

			DeviceConfig deviceConfig = deviceModel.getDeviceConfig();

			if (deviceConfig.getOndemandParserName() != null && !"".equals(deviceConfig.getOndemandParserName()))
				edp = (MeterDataParser) Class.forName(deviceConfig.getOndemandParserName()).newInstance();
			else
				edp = (MeterDataParser) Class.forName(deviceConfig.getParserName()).newInstance();

			edp.setMeter(meter);
			edp.setMeteringTime(DateTimeUtil.getDateString(new Date()));
			edp.setOnDemand(true);
			ed.setMeterId(meterId);

			log.debug("SMIValue Return Class=" + smiValue.getVariable().getClass().getName());

			smiValues = (SMIValue[]) obj;

			for ( int j = 0; j < smiValues.length; j++ ){
				smiValue = smiValues[j];
				log.debug("smiValue[" + j + "] = " + smiValue.toString());
				
				if (smiValue.getVariable() instanceof OPAQUE) {

					OPAQUE mdv = (OPAQUE) smiValue.getVariable();
					log.debug("Get Meter : return ClassName[" + mdv.getClsName() + "] MIB[" + mdv.getMIBName() + "]");

					obj = mdv.getValue();
					if (obj instanceof meterDataEntry) {
						meterDataEntry value = (meterDataEntry) obj;

						// meter id
						OCTET ocMeterId = new OCTET(20);
						ocMeterId.setIsFixed(true);
						String meterSerial = meter.getMdsId() == null ? "" : meter.getMdsId();
						byte[] bMeterid = DataUtil.fillCopy(meterSerial.getBytes(), (byte) 0x20, 20);
						ocMeterId.setValue(bMeterid);
						value.setMdSerial(ocMeterId);

						edp.parse(value.getMdData().getValue());
						ed.setTime(value.getMdTime().toString());
						ed.setType(value.getMdType().toString());
						ed.setVendor(value.getMdVendor().toString());
						ed.setServiceType(value.getMdServiceType().toString());
					} else if (obj instanceof meterLPEntry) {
						meterLPEntry value = (meterLPEntry) obj;
						ByteArrayOutputStream bao = new ByteArrayOutputStream();
						byte[] bx = value.getMlpData().getValue();
						byte[] timestamp = new byte[7];
						log.debug("bx: " + Hex.decode(bx));

						int dataCnt = value.getMlpDataCount().getValue();
						if (dataCnt == 1) {
							System.arraycopy(bx, 0, timestamp, 0, timestamp.length);
							bao.write(bx, timestamp.length, bx.length - timestamp.length);
						} else {
							int pos = 0;
							byte[] blen = new byte[2];
							int ilen = 0;

							for (int i = 0; i < dataCnt; i++) {
								//
								System.arraycopy(bx, pos, blen, 0, blen.length);
								DataUtil.convertEndian(blen);
								ilen = DataUtil.getIntTo2Byte(blen);
								pos += blen.length;
								//
								System.arraycopy(bx, pos, timestamp, 0, timestamp.length);
								pos += timestamp.length;
								//
								bao.write(bx, pos, ilen - timestamp.length);
								pos += ilen - timestamp.length;
							}
						}
						log.debug("bao: " + Hex.decode(bao.toByteArray()));
						edp.parse(bao.toByteArray());
						ed.setTime(new TIMESTAMP(timestamp).getValue());
						ed.setType(value.getMlpType().toString());
						ed.setVendor(value.getMlpVendor().toString());
						ed.setServiceType(value.getMlpServiceType().toString());
						bao.flush();
						bao.close();
					}
					// before setParser, clear ed.map
					com.aimir.fep.meter.data.MeterData.Map map = new com.aimir.fep.meter.data.MeterData.Map();
					ed.setMap(map);
					//
					ed.setParser(edp);
					MDData mdData = new MDData(new WORD(1));
					mdData.setMcuId(mcuId);
					
					byte[] raw = mdv.encode();
					byte[] bx = new byte[raw.length - 2];
					System.arraycopy(raw, 2, bx, 0, bx.length);
					mdData.setMdData(bx);
					
					putServiceData(target, mdData, startLongTime, endLongTime);
				}

				if (smiValue.getVariable() instanceof OCTET) {

					OCTET ocMeterId = new OCTET(20);
					ocMeterId.setIsFixed(true);
					String meterSerial = meter.getMdsId() == null ? "" : meter.getMdsId();
					byte[] bMeterid = DataUtil.fillCopy(meterSerial.getBytes(), (byte) 0x20, 20);
					ocMeterId.setValue(bMeterid);

					OCTET ocModemId = new OCTET(8);
					ocModemId.setIsFixed(true);
					byte[] bModemid = Hex.encode(modemId);
					ocModemId.setValue(bModemid);

					OCTET stream = (OCTET) smiValue.getVariable();
					byte[] data = stream.getValue();

					String currTime = null;
					try {
						currTime = TimeUtil.getCurrentTime();
					} catch (Exception e) {
					}
					meterDataEntry value = new meterDataEntry();
					value.setDataCount(1);
					value.setMdData(new OCTET(data));
					value.setMdID(ocModemId);
					value.setMdSerial(ocMeterId);
					value.setMdServiceType(new BYTE(Integer.parseInt(CommonConstants.getDataSvcCode(DataSVC.Electricity))));
					value.setMdTime(new TIMESTAMP(currTime));
					value.setMdType(new BYTE(Integer.parseInt(CommonConstants.getModemTypeCode(ModemType.MMIU))));
					value.setMdVendor(new BYTE(MeterVendor.LSIS.getCode()[0].intValue()));

					int dataLen = 7 + value.getMdData().getValue().length;
					ByteArray ba = new ByteArray();
					ba.append(value.getMdID().getValue());
					ba.append(value.getMdSerial().getValue());
					ba.append(value.getMdType().encode());
					ba.append(value.getMdServiceType().encode());
					ba.append(value.getMdVendor().encode());
					ba.append(value.getDataCount().encode());
					ba.append(DataUtil.get2ByteToInt(true, dataLen)); // Timestamp.length+Data.length
					ba.append(value.getMdTime().encode());
					ba.append(value.getMdData().getValue());

					edp.parse(data);
					ed.setTime(value.getMdTime().toString());
					ed.setType(value.getMdType().toString());
					ed.setVendor(value.getMdVendor().toString());
					ed.setServiceType(value.getMdServiceType().toString());
					ed.setParser(edp);
					MDData mdData = new MDData(new WORD(1));
					mdData.setMcuId(mcuId);
					mdData.setTotalLength(ba.toByteArray().length);
					mdData.setMdData(ba.toByteArray());

					putServiceData(target, mdData, startLongTime, endLongTime);
				}
			/*
			 * MDHistoryData mdHistoryData = new MDHistoryData();
			 * mdHistoryData.setEntryCount(1); mdHistoryData.setMcuId(mcuId);
			 * mdHistoryData.setMdData(mdv.encode());
			 * mdsMain.save(mdHistoryData, true);
			 */
			}
			// INSERT START SP-441
			// if ( edp instanceof DLMSKaifa){
	        //    DLMSKaifaMDSaver saver = (DLMSKaifaMDSaver)DataUtil.getBean(DLMSKaifaMDSaver.class);
	        //    saver.saveMeterData(meter, (DLMSKaifa)edp);		
			// }
			// INSERT END SP-441
			txManager.commit(txStatus);
			return ed;
		} catch (Exception e) {
			log.error(e, e);
			if (txManager != null && !txStatus.isCompleted())
				txManager.rollback(txStatus);
			throw new Exception(e);
		}
	}
	
	// SP-629
    private void putServiceData(Target target, MDData mdData, long startLongTime, long endLongTime) throws Exception {
        boolean kafkaEnable = Boolean.parseBoolean(FMPProperty.getProperty("kafka.enable"));
        ProcessorHandler handler = DataUtil.getBean(ProcessorHandler.class);
        
        if (kafkaEnable) {
            String nameSpace = "SP";
            
            //통신 로그 저장 로직
            Message commLog = new Message();
            commLog.setNameSpace(nameSpace == null? "":nameSpace);
            commLog.setData(mdData.getMdData());
            commLog.setDataType(ProcessorHandler.SERVICE_MEASUREMENTDATA);
            commLog.setSenderIp(target.getIpv6Addr());
            commLog.setSenderId(target.getTargetId());
            commLog.setReceiverId(DataUtil.getFepIdString());
            commLog.setSendBytes(FMPProtocolHandler.SENDCONTROLDATASIZE); //ENQ+ACK
            commLog.setRcvBytes(commLog.getData().length+FMPProtocolHandler.RECVCONTROLDATASIZE);//included EOT that received
            commLog.setStartDateTime(DateTimeUtil.getDateString(startLongTime));//Start Time은 sessionOpen 시 지정
            commLog.setEndDateTime(DateTimeUtil.getDateString(endLongTime));
            commLog.setProtocolType(target.getProtocol());
            log.debug("startTime["+commLog.getStartDateTime()+"] endTime["+commLog.getEndDateTime()+"]");
            log.debug("startLongTime["+startLongTime+"] endLongTime["
                    +endLongTime+"]");
            if(endLongTime - startLongTime > 0) {
                commLog.setTotalCommTime((int)(endLongTime - startLongTime));
            }
            else {
                commLog.setTotalCommTime(0);
            }
            MDLogger mdlog = new MDLogger();
            String filename = mdlog.writeObject(mdData);
            commLog.setFilename(filename);
            handler.putServiceData(ProcessorHandler.SERVICE_MEASUREMENTDATA, commLog); 
        }
        else {
            handler.putServiceData(ProcessorHandler.SERVICE_MEASUREMENTDATA, mdData); 
        }
    }
    
	@Override
	public MeterData cmdGetROMRead(String mcuId, String meterId, String modemId, String nOption, String fromDate,
			String toDate) throws FMPMcuException, Exception {
		log.debug("cmdGetROMRead(" + mcuId + "," + meterId + "," + modemId + "," + nOption + "," + fromDate + ","
				+ toDate + ")");

		Target target = null;
		Meter meter = null;
		RomRead resultObj = null;
		JpaTransactionManager txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");
		TransactionStatus txStatus = null;
		Map map = new HashMap<String, String>();
		byte[][] resultData = null;

		try {
			txStatus = txManager.getTransaction(null);
			meter = meterDao.get(meterId);
			Modem modem = meter.getModem();

			ModemType modemType = modem.getModemType();
			//target = CmdUtil.getTarget(modem);
			target = CmdUtil.getNullBypassTarget(modem);
		    if(target == null){
		    	throw new Exception("Can not found target. please check Meter & Modem information.");
		    }
		    
			// MCU mcu = mcuDao.get(mcuId);
			mcuId = target.getTargetId();
			DeviceModel model = meter.getModel();
			
			int  modemPort =  meter.getModemPort() == null ? 0 : meter.getModemPort().intValue();
			if ( modemPort > 5 || modemPort < 0 ){
				log.debug("modemPort:" + modemPort + " is not support");
				throw new Exception("modemPort:" + modemPort + " is not support");
			}
			
			log.debug("meterModel=" + model.getId());
			// log.debug("meterVendor="+model.getDeviceVendor().getName());
			int lpInterval = meter.getLpInterval();

			txManager.commit(txStatus);
			txStatus = null;

			
			int[] offsetCount = CmdUtil.convertOffsetCount(model, lpInterval, modemType, fromDate, toDate);

			Vector<SMIValue> datas = new Vector<SMIValue>();

			try {
				/**
				 * Ni Proxy
				 */
				HashMap niParam = new HashMap();
				if ("SP".equals(target.getNameSpace())) {
					niParam.put("type",2);
					RomReadDataReq req = new RomReadDataReq();
					req.setPollData(modemPort + 1, offsetCount[0], offsetCount[1] );	// DLMS Load profile
					niParam.put("romReadData", req);
				}
				//niParam.put("baudRate", rateValue);
				CommandNIProxy niProxy = new CommandNIProxy();

				// requestType = "GET" only
				resultObj = (RomRead)niProxy.execute(target, NIAttributeId.ROMRead, niParam, null);
			} catch (Exception e) {
				log.debug(e,e);
				throw new Exception(makeMessage(e.getMessage()));
			}

			if(resultObj!=null){
				resultData = resultObj.getRomData();
			}

			txStatus = txManager.getTransaction(null);
			meter = meterDao.get(meterId);
			MeterData ed = new MeterData();
			MeterDataParser edp = null;
			DeviceModel deviceModel = meter.getModel();

			if (deviceModel == null)
				deviceModel = meter.getModem().getModel();

			DeviceConfig deviceConfig = deviceModel.getDeviceConfig();

			if (deviceConfig.getOndemandParserName() != null && !"".equals(deviceConfig.getOndemandParserName()))
				edp = (MeterDataParser) Class.forName(deviceConfig.getOndemandParserName()).newInstance();
			else
				edp = (MeterDataParser) Class.forName(deviceConfig.getParserName()).newInstance();

			edp.setMeter(meter);
			edp.setMeteringTime(DateTimeUtil.getDateString(new Date()));
			edp.setOnDemand(true);
			ed.setMeterId(meterId);
			if ( edp instanceof 	DLMSKaifa ) {
				((DLMSKaifa)edp).setModemId(modem.getDeviceSerial());			
				((DLMSKaifa)edp).setModemPort(modemPort);
			}
			int dataCnt = resultData.length;
			int bufLen = 0;							// INSERT SP-487
			for (int i=0; i<dataCnt; i++)
			{
				edp.parse((byte[])resultData[i]);
				bufLen += resultData[i].length;		// INSERT SP-487
			}
			ed.setParser(edp);
			// UPDATE START SP-487
//			if ( edp instanceof DLMSKaifa){
//	            DLMSKaifaMDSaver saver = (DLMSKaifaMDSaver)DataUtil.getBean(DLMSKaifaMDSaver.class);
//	            saver.saveMeterData(meter, (DLMSKaifa)edp);
//		
//	            modem.setLastLinkTime(meter.getLastReadDate());
//	            modemDao.update(modem);	
//			}
			ByteBuffer buf = ByteBuffer.allocate(bufLen);
			buf.clear();
			for (int i=0; i<dataCnt; i++)
			{
				buf.put((byte[])resultData[i]);
			}			
			saveMeteringDataByQueue(mcuId, meterId, modemId, buf.array());
			// UPDATE END SP-487
			txManager.commit(txStatus);
			return ed;
		} catch (Exception e) {
			log.error(e, e);
			if (txManager != null && !txStatus.isCompleted())
				txManager.rollback(txStatus);
			throw new Exception(e);
		}
	}
	// INSERT END SP-179
	
    // INSERT SP-117
	// for Kaifa
	public Map cmdMeterRelay(String mcuId, String meterId, int nOption) 
			throws FMPMcuException, Exception {
		log.debug("cmdMeterRelay(" + mcuId + "," + meterId + "," + nOption + ")");

		Target target = null;
		Vector<SMIValue> datas = new Vector<SMIValue>();
		Map result = null;
		Map resultTmp = null;

		SMIValue smiValue = null;
		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;

		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");
			txStatus = txManager.getTransaction(null);

			Meter meter = meterDao.get(meterId);
			Modem modem = meter.getModem();
			target = CmdUtil.getNullBypassTarget(modem);
		    if(target == null){
		    	throw new Exception("Can not found target. please check Meter & Modem information.");
		    }
		    
			if (target.getNameSpace() != null && target.getNameSpace().equals("SP")) {
				String param = null;
//				DeviceConfig deviceConfig = meter.getModel().getDeviceConfig();
//				if (deviceConfig == null)
//					deviceConfig = meter.getModem().getModel().getDeviceConfig();
//				MeterDataParser edp = null;
//				if (deviceConfig.getOndemandParserName() != null && !"".equals(deviceConfig.getOndemandParserName()))
//					edp = (MeterDataParser) Class.forName(deviceConfig.getOndemandParserName()).newInstance();
//				else
//					edp = (MeterDataParser) Class.forName(deviceConfig.getParserName()).newInstance();
//
//				edp.setMeter(meter);
				
				String obisCode = this.convertObis(OBIS.RELAY_STATUS.getCode());
				int classId = DLMS_CLASS.RELAY_CLASS.getClazz();
				int attrId = DLMS_CLASS_ATTR.REGISTER_ATTR02.getAttr();
				String accessRight = null;
				String dataType = "Boolean";
				String value = null;

				if (nOption == OnDemandOption.READ_OPTION_RELAY.getCode() ) {
					accessRight = "RO";
					value = "";
				}else if ( nOption == OnDemandOption.WRITE_OPTION_RELAYON.getCode() ){
					accessRight = "ACTION";
					value = "true";
				}else if ( nOption == OnDemandOption.WRITE_OPTION_RELAYOFF.getCode() ){
					accessRight = "ACTION";
					value = "false";
				}
				log.debug("obisCode => " + obisCode + ", classId => " + classId + ", attributeId => " + attrId);
				//byte[] rawdata = null;
				if ( nOption == OnDemandOption.READ_OPTION_RELAY.getCode() ) {
					param = obisCode+"|"+classId+"|"+attrId+"|"+accessRight+"|"+dataType+"|"+value;
					// -> UPDATE START 2016/08/24 SP117
					// result = cmdMeterParamGet(modem.getDeviceSerial(),param);
					// log.debug("cmdMeterparamGet result=[" + result.toString() + "]");
			    	// TTTT Map<String,Object> tmpresult = cmdMeterParamGetWithOption(modemId,param, "XXXX");
					result = cmdMeterParamGetWithOption( modem.getDeviceSerial(), param, "relaystatusall" );
					log.debug("cmdMeterParamGetWithOption result=[" + result.toString() + "]");
					// <- UPDATE END   2016/08/24 SP117
					//rawdata = (byte[])result.get("rawdata");
				} else {
					// WRITE_OPTION_RELAYON || WRITE_OPTION_RELAYOFF
					param = obisCode+"|"+classId+"|"+attrId+"|"+accessRight+"|"+dataType+"|"+value;
					//result = cmdMeterParamSet(modem.getDeviceSerial(),param);
					result = cmdMeterParamAct(modem.getDeviceSerial(),param);
					log.debug("cmdMeterParamAct result=[" + result.toString() + "]");
					//rawdata = (byte[])result.get("rawdata");
				}
				//edp.parse(rawdata);
				//DLMSKaifa kaifa = (DLMSKaifa) edp;
				//result = kaifa.getRelayStatus();
			}
			return result;
		} catch (Exception e) {
			log.error("[MCU_ID = " + mcuId + "][METER_ID = " + meterId + "] " + e, e);
			throw new Exception(e.getMessage());
		} finally {
			try {
				txManager.commit(txStatus);
			} catch (Exception e) {
				log.error(e, e);
				// if (txManager != null && !txStatus.isCompleted())
				// txManager.rollback(txStatus);
			}
		}
	}
	
	// for Kaifa
	public Map cmdMeterRelayLoadCtrl(String mcuId, String meterId) 
			throws FMPMcuException, Exception {
		log.debug("cmdMeterRelayLoadCtrl(" + mcuId + "," + meterId + ")");

		Target target = null;
		Vector<SMIValue> datas = new Vector<SMIValue>();
		Map result = null;
		Map resultTmp = null;

		SMIValue smiValue = null;
		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;

		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");
			txStatus = txManager.getTransaction(null);

			Meter meter = meterDao.get(meterId);
			Modem modem = meter.getModem();
			target = CmdUtil.getNullBypassTarget(modem);
		    if(target == null){
		    	throw new Exception("Can not found target. please check Meter & Modem information.");
		    }
		    
			if (target.getNameSpace() != null && target.getNameSpace().equals("SP")) {
				String param = null;
//				DeviceConfig deviceConfig = meter.getModel().getDeviceConfig();
//				if (deviceConfig == null)
//					deviceConfig = meter.getModem().getModel().getDeviceConfig();
//				MeterDataParser edp = null;
//				if (deviceConfig.getOndemandParserName() != null && !"".equals(deviceConfig.getOndemandParserName()))
//					edp = (MeterDataParser) Class.forName(deviceConfig.getOndemandParserName()).newInstance();
//				else
//					edp = (MeterDataParser) Class.forName(deviceConfig.getParserName()).newInstance();
//
//				edp.setMeter(meter);
				
				String obisCode = this.convertObis(OBIS.RELAY_STATUS.getCode());
				int classId = DLMS_CLASS.RELAY_CLASS.getClazz();
				int attrId = DLMS_CLASS_ATTR.REGISTER_ATTR03.getAttr();
				String accessRight = "RO";
				String dataType = "Enum";
				String value = "";
				log.debug("obisCode => " + obisCode + ", classId => " + classId + ", attributeId => " + attrId);
				byte[] rawdata = null;
				param = obisCode+"|"+classId+"|"+attrId+"|"+accessRight+"|"+dataType+"|"+value;
				result = cmdMeterParamGet(modem.getDeviceSerial(),param);
				log.debug("cmdMeterparamGet result=[" + result.toString() + "]");
//				rawdata = (byte[])result.get("rawdata");
//				edp.parse(rawdata);
//				DLMSKaifa kaifa = (DLMSKaifa) edp;
//				result = kaifa.getRelayStatus();
			}
			return result;
		} catch (Exception e) {
			log.error("[MCU_ID = " + mcuId + "][METER_ID = " + meterId + "] " + e, e);
			throw new Exception(e.getMessage());
		} finally {
			try {
				txManager.commit(txStatus);
			} catch (Exception e) {
				log.error(e, e);
				// if (txManager != null && !txStatus.isCompleted())
				// txManager.rollback(txStatus);
			}
		}
	}
    // INSERT SP-117
	
	// INSERT START SP-193
	public String cmdSendEvent(String eventAlertName, String  target, String activatorId, String[][] params) throws Exception{
		String cmdResult = null;

		try{
            EventUtil.sendEvent(eventAlertName,
            		TargetClass.valueOf(target),
            		activatorId,
            		params);
			cmdResult = "SUCCESS";
		} catch (Exception e){
			log.error(e,e);
			cmdResult = "ERROR on Transaction";
		}
				
		return cmdResult;
	}
	// INSERT END SP-193
	
	public void cmdSendEvent2(String eventAlertName, String activatorType, String activatorId, int supplierId) throws Exception {
		log.info("eventAlertName : " + eventAlertName + ", activatorType : " + ", activatorId : " + ", supplierId : " + supplierId);
		
		try {
			EventUtil.sendEvent(eventAlertName, activatorType, activatorId, supplierId);
		} catch (Exception e) {
			log.error(e, e);
		}
	}
	
	/**
	 * NI Protocol Command :  0x2002 Modem Reset Time(set),
	 **/
	public Map<String, String> cmdModemResetTime(String modemId, String requestType, int resetTime) throws Exception {
		log.debug("## NI command - Modem Reset Time ["+modemId+"],["+requestType+"],["+resetTime+"]");
		//
		Map map = new HashMap<String, String>();
		ModemResetTime resultObj = null;

		Modem modem = modemDao.get(Integer.parseInt(modemId));

		try {
			/**
			 * Ni Proxy
			 */
			HashMap niParam = new HashMap();
			niParam.put("resetTime", resetTime);
			CommandNIProxy niProxy = new CommandNIProxy();

			if(requestType.equals("SET")){
				resultObj = (ModemResetTime)niProxy.execute(modem, NIAttributeId.ModemResetTime, niParam);
			}
		} catch (Exception e) {
			log.error(e, e);
		}

		// return map 작성
		if(resultObj!=null && resultObj.getStatus() != null ){
			map.put("status", resultObj.toString());
			map.put("cmdResult", "Execution OK");
		}else{
			map.put("cmdResult", "Failed to sending a command in CommandNIProxy.");
		}
		return map;
	}
	
	/**
	 * NI Protocol Command :  0x2003 Modem Mode(set/get)
	 **/
	public Map<String, String> cmdModemMode(String modemId, String requestType, int mode ) throws Exception {
		log.debug("## NI command - Modem Mode ["+modemId+"],["+requestType+"],["+mode+"]");
		//
		Map map = new HashMap<String, String>();
		ModemMode resultObj = null;

		Modem modem = modemDao.get(Integer.parseInt(modemId));
		
		try {
			/**
			 * Ni Proxy
			 */
			HashMap niParam = new HashMap();
			niParam.put("mode", mode);
			CommandNIProxy niProxy = new CommandNIProxy();

			if(requestType.equals("GET")){
				resultObj = (ModemMode)niProxy.execute(modem, NIAttributeId.ModemMode_GET, niParam);
			}
			else {
				resultObj = (ModemMode)niProxy.execute(modem, NIAttributeId.ModemMode, niParam);
			}
		} catch (Exception e) {
			log.error(e, e);
		}

		// return map 작성
		if(resultObj!=null && resultObj.getStatus() != null ){
			map.put("modemMode", resultObj.toString());
			map.put("cmdResult", "Execution OK");
		}else{
			map.put("cmdResult", "Failed to sending a command in CommandNIProxy.");
		}
		return map;
	}
	/**
	 * NI Protocol Command :  0x2010 SNMP Sever IPv6/Port(set/get)
	 **/
	public Map<String, String> cmdSnmpServerIpv6Port(String modemId, String requestType, int type , String ipAddress, String port) throws Exception {
		log.debug("## NI command - Snmp Server IPV6 Port ["+modemId+"],["+requestType+"],["+type+"],["+ipAddress+"],["+port+"]");
		//
		Map map = new HashMap<String, String>();
		ModemPortInformation resultObj = null;

		Target target = new Target();

		Modem modem = modemDao.get(Integer.parseInt(modemId));

		try {
			/**
			 * Ni Proxy
			 */
			HashMap niParam = new HashMap();
			niParam.put("type", new Integer(type));
			niParam.put("ipAddress", ipAddress);
			niParam.put("port", port);
			CommandNIProxy niProxy = new CommandNIProxy();

			if(requestType.equals("GET")){
				resultObj = (ModemPortInformation)niProxy.execute(modem, NIAttributeId.ModemPortInformation_GET, niParam);
			}
			else {
				resultObj = (ModemPortInformation)niProxy.execute(modem, NIAttributeId.ModemPortInformation, niParam);
			}
		} catch (Exception e) {
			log.error(e, e);
		}

		// return map 작성
		if(resultObj!=null){
			map.put("SnmpSeverIpPort", resultObj.toString());
			map.put("cmdResult", "Execution OK");
		}else{
			map.put("cmdResult", "Failed to sending a command in CommandNIProxy.");
		}
		return map;
	}

	/**
	 * NI Protocol Command :  0x2011 Alarm/Event Command ON_OFF(set/get)
	 **/
	public Map<String, String> cmdAlarmEventCommandOnOff(String modemId, String requestType, int count , String cmds) throws Exception {
		log.debug("## NI command - Alarm/Event Command ON_OFF ["+modemId+"],["+requestType+"],["+count+"]");
		//
		Map map = new HashMap<String, String>();
		AlarmEventCommandOnOff resultObj = null;

		Modem modem = modemDao.get(Integer.parseInt(modemId));

		try {
			/**
			 * Ni Proxy
			 */
			HashMap niParam = new HashMap();
			niParam.put("count", count);
        	Gson gson = new Gson();
        	AlarmEventCmd  cmdarray[] = (AlarmEventCmd[]) gson.fromJson(cmds, AlarmEventCmd[].class);
        	niParam.put("cmds", cmdarray);
			CommandNIProxy niProxy = new CommandNIProxy();

			if(requestType.equals("GET")){
				resultObj = (AlarmEventCommandOnOff)niProxy.execute(modem, NIAttributeId.Alarm_EventCommandON_OFF_GET, niParam);
			}
			else {
				resultObj = (AlarmEventCommandOnOff)niProxy.execute(modem, NIAttributeId.Alarm_EventCommandON_OFF, niParam);
			}
		} catch (Exception e) {
			log.error(e, e);
		}

		// return map 작성
		if(resultObj!=null && resultObj.getStatus() != null ) {
			map.put("AlarmEventCommand", resultObj.toString());
			map.put("cmdResult", "Execution OK");
		}else{
			map.put("cmdResult", "Failed to sending a command in CommandNIProxy.");
		}
		return map;
	}
	
	/**
	 * NI Protocol Command : 0x2013 Transmit Frequency(set/get)
	 **/
	public Map<String, String> cmdTransmitFrequency(String modemId, String requestType, int second) throws Exception {
		log.debug("## NI command - Transmit Frequency ["+modemId+"],["+requestType+"],["+second+"]");
		// define result map
		Map map = new HashMap<String, String>();
		TransmitFrequency resultObj = null;
		// target modem
		Modem modem = modemDao.get(Integer.parseInt(modemId));

		try {
			/**
			 * Ni Proxy
			 */
			HashMap niParam = new HashMap();
			niParam.put("second", second);
			CommandNIProxy niProxy = new CommandNIProxy();

			if(requestType.equals("GET")){
				resultObj = (TransmitFrequency)niProxy.execute(modem, NIAttributeId.TransmitFrequency_GET, niParam);
			}
			else {
                if(second < 60 || second > 21600){
                    map.put("cmdResult", "[Invalid Parameter] TransmitFrequency (60~21600 seconds) ");
                    return map;
                }else{
                    resultObj = (TransmitFrequency)niProxy.execute(modem, NIAttributeId.TransmitFrequency, niParam);
                }
			}

		} catch (Exception e) {
			log.error(e, e);
		}

		// return map 작성
		if(resultObj!=null && resultObj.getStatus() != null ){
			if(resultObj.getTransmitFrequency() < 0){
                map.put("cmdResult", "Reponse of TransmitFrequency-Command is invalid");
            }else{
                map.put("transmitFrequency", resultObj.toString());
                map.put("frequency", resultObj.getFrequencyAsTime());
                map.put("frequencyStatus", resultObj.getStatusString());
                map.put("cmdResult", "Execution OK");
            }
		}else{
			map.put("cmdResult", "Failed to sending a command in CommandNIProxy.");
		}
		return map;
	}
    
    /**
     * Get Load Profile throught DLMS.
     * for Kaifa
     * 
     * modemId is deviceSerial
     * fromDate and toDate format : yyyymmddhhMMss
     * 								When you input empty, date will be set today
     * 
     * @param modemId
     * @param fromDate
     * @param toDate
     * @return Map<String,Object>
     * @throws Exception
     */
    public Map<String,Object> cmdGetLoadProfile(String modemId, String fromDate, String toDate) throws Exception{
    	Map<String,Object> result = new HashMap<String,Object>();
    	
    	log.debug("cmdGetLoadProfile ["+ modemId + "][" + fromDate + "][" +toDate +"]");
		
		String obisCode = this.convertObis(OBIS.ENERGY_LOAD_PROFILE.getCode());
		int classId = DLMS_CLASS.PROFILE_GENERIC.getClazz();
		int attrId = DLMS_CLASS_ATTR.PROFILE_GENERIC_ATTR02.getAttr();
		
		Map<String,String> valueMap = eventLogValueByRange(fromDate,toDate);
		Map<String,String> paramMap = new HashMap<String,String>();
		String value = meterParamMapToJSON(valueMap);
		
		log.debug("obisCode => " + obisCode + ", classId => " + classId + ", attributeId => " + attrId);
		String param = obisCode+"|"+classId+"|"+attrId+"|null|null|"+value;
    	Map<String,Object> tmpresult = cmdMeterParamGetWithOption(modemId,param, "ondemand");
		
    	if ( tmpresult.get("rawData") != null ) {	
    		result.put("lprawdata",tmpresult.get("rawData"));
    		log.debug("LoadProfile RAWDATA = " +  Hex.decode((byte[])tmpresult.get("rawData")));
    	}else {
    		throw new Exception("No Data");
    	}
    	if ( tmpresult.get("channelData") != null){
    		result.put("channelData",tmpresult.get("channelData"));
    		log.debug("ChannelData = " +  tmpresult.get("channelData"));
    	}
		OBIS cumulatives[] =  new OBIS [4];
		cumulatives[0] = OBIS.CUMULATIVE_ACTIVEENERGY_IMPORT;
		cumulatives[1] = OBIS.CUMULATIVE_ACTIVEENERGY_EXPORT;
		cumulatives[2] = OBIS.CUMULATIVE_REACTIVEENERGY_IMPORT;
		cumulatives[3] = OBIS.CUMULATIVE_REACTIVEENERGY_EXPORT; 
		for ( int i = 0; i < cumulatives.length; i++){
	    	if ( tmpresult.get(cumulatives[i].name()) != null){
				 result.put(cumulatives[i].name(), 
						 tmpresult.get(cumulatives[i].name()));
	    	}
	    	String keyUnit = cumulatives[i].name() + "_UNIT";
	    	if ( tmpresult.get(keyUnit) != null){
				 result.put(keyUnit, 
						 tmpresult.get(keyUnit));
	    	}
		}
		 return result;
    }
    /**
     * Get Load Profile Channel 1,2,3,4 throught DLMS.
     * for Kaifa
     * 
     * modemId is deviceSerial
     * modemPort is Channel
     * fromDate and toDate format : yyyymmddhhMMss
     * 								When you input empty, date will be set today
     * 
     * @param modemId
     * @param modemPort
     * @param fromDate
     * @param toDate
     * @return
     * @throws Exception
     */

    public Map<String,Object> cmdGetLoadProfileChannel(String modemId, int modemPort, String fromDate, String toDate) throws Exception{
    	Map<String,Object> result = new HashMap<String,Object>();
    	
    	log.debug("cmdGetLoadProfile ["+ modemId + "][" + modemPort +  "]["  +  fromDate + "][" +toDate +"]");
		
    	String obisCode = this.convertObis(OBIS.MBUSMASTER_LOAD_PROFILE.getCode());
		int classId = DLMS_CLASS.PROFILE_GENERIC.getClazz();
		int attrId = DLMS_CLASS_ATTR.PROFILE_GENERIC_ATTR02.getAttr();
		
		Map<String,String> valueMap = eventLogValueByRange(fromDate,toDate);
		Map<String,String> paramMap = new HashMap<String,String>();
		String value = meterParamMapToJSON(valueMap);
		
		log.debug("obisCode => " + obisCode + ", classId => " + classId + ", attributeId => " + attrId);
		String param = obisCode+"|"+classId+"|"+attrId+"|null|null|"+value;
    	Map<String,Object> tmpresult = cmdMeterParamGetWithOption(modemId,param, "ondemand");
		
    	if ( tmpresult.get("rawData") != null ) {	
    		result.put("lprawdata",tmpresult.get("rawData"));
    		log.debug("LoadProfile RAWDATA = " +  Hex.decode((byte[])tmpresult.get("rawData")));
    	}else {
    		throw new Exception("No Data");
    	}
    	if ( tmpresult.get("channelData") != null){
    		result.put("channelData",tmpresult.get("channelData"));
    		log.debug("ChannelData = " +  tmpresult.get("channelData"));
    	}
    	return result;
    }

  	/**
  	 * SMS Protocol
  	 **/
    @Override
	public String sendSMS(
			String commandName, 
			String messageType, 
			String mobliePhNum, 
			String euiId, 
			String commandCode, 
			List<String> paramList,
			String cmdMap
			) throws Exception {
		HashMap<String, Object> condition = new HashMap<String, Object>();

		String materialForHashCode = null;
		String hashCode = null;
		String result = "";

		/** Logic For Hash Code (S) **/
		materialForHashCode = messageType + euiId;

		// SHA-1 Hash Func.
		byte[] buffers = encryptSha1(materialForHashCode);
		hashCode = Base64Utils.encodeToString(buffers);
		/** Logic For Hash Code (E) **/

		condition.put("commandName", commandName);
		condition.put("euiId", euiId);
		condition.put("messageType", messageType);
		condition.put("mobliePhNum", mobliePhNum);
		condition.put("commandCode", commandCode);
		condition.put("hashCode", hashCode);

		log.debug("condition : " + condition + ", " + "paramList : " + paramList + ", cmdMap : " + cmdMap);
		
		String smsClassPath = FMPProperty.getProperty("smsClassPath","SMS_Client");

		if(smsClassPath.equals("SMS_Client")){
			SMS_Client sms_Client = new SMS_Client();
			return sms_Client.execute(condition, paramList, cmdMap);
		}else if(Class.forName(smsClassPath).newInstance() instanceof SMSServiceClient){
			SMSServiceClient obj = (SMSServiceClient) Class.forName(smsClassPath).newInstance();
			return obj.execute(condition, paramList, cmdMap);
		}else{
			throw new Exception("SMS Client["+smsClassPath+"] is not available.");
		}
		
	}
      
      // SHA-1
      public static byte[] encryptSha1(String input) throws NoSuchAlgorithmException {
          MessageDigest mDigest = MessageDigest.getInstance("SHA1");
          byte[] result = mDigest.digest(input.getBytes());
          
//          StringBuffer stringBuffer = new StringBuffer();
//          for (int i = 0; i < result.length; i++) {
//         	 stringBuffer.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
//          }
//          log.info(stringBuffer.toString());
          
          return result;
      }
      
     /**
      * Command  : cmdMeterParamGetWithOption
      * @param modemId
      * @return
      * @throws Exception 
      */
     public Map<String,Object> cmdMeterParamGetWithOption(String modemId, String param, String option) throws Exception{
     	Map<String,Object> result = new HashMap<String,Object>();
     	
     	log.debug("cmdMeterParamGet(" + modemId + ")");
 		Target target = CmdUtil.getNullBypassTarget(modemDao.get(modemId));
 		
	    if(target != null){
			log.debug("Target Info => " + target.toString());
	 		log.debug("param => " + param);
	 		log.debug("option => " + option);
	 		
	 		
	 		/**
	 		 * Bypass Request
	 		 */
	 		if (target.getNameSpace().equals("NG")) { // IRAQ
	 		} else if (target.getNameSpace().equals("SP")) { // SORIA
	 			Map<String, Object> params = new HashMap<String, Object>();
	 			params.put("paramGet", param);
	 			params.put("option", option);
	 			
	 			BypassClient bypassClient = new BypassClient(target);
	 			bypassClient.setParams(params);
	 			
	 			// UPDATE START SP-519
	 			BypassResult bypassResult = null;
				int useNiBypass = Integer.parseInt(FMPProperty.getProperty("soria.protocol.modem.nibypass.use" , "0"));
	 			if ( useNiBypass > 0 && "ondemand".equals(option)){
	 				bypassResult = bypassClient.excuteNiBypass("cmdMeterParamGet");
	 			}
	 			else {
	 				bypassResult = bypassClient.excute("cmdMeterParamGet");
	 			}
	 			// UPDATE END  SP-519
	 			log.debug("[cmdMeterParamGet] Excute Result = " + bypassResult.toString());
	 			
	 			if(bypassResult.getResultValue() instanceof Map) {
	 				log.debug("Result is Map");
	 				result = (Map<String, Object>) bypassResult.getResultValue();
	 			} else {
	 				Map<String,Object> map = new HashMap<String,Object>();
	 				result.put("RESULT_VALUE", bypassResult.getResultValue());
	 			}
	 		}	    	
	    }else{
	    	result.put("RESULT_VALUE", "Can not found target. please check Meter & Modem information.");
	    }
 		
 		return result;
     }

     /**
      * Command 실행 : cmdMeterParamSet
      * @param modemId
      * @return
      * @throws Exception 
      */
     public Map<String,Object> cmdMeterParamSetWithOption(String modemId, String param, String option) throws Exception{
     	Map<String,Object> result = new HashMap<String,Object>();
     	
     	log.debug("cmdMeterParamSet(" + modemId + ")");
         Target target = CmdUtil.getNullBypassTarget(modemDao.get(modemId));
         if(target != null){
      		log.debug("Target Info => " + target.toString());
     		log.debug("param => " + param);
     		/**
     		 * Bypass Request
     		 */
     		if (target.getNameSpace().equals("NG")) { // IRAQ

     		} else if (target.getNameSpace().equals("SP")) { // SORIA
     			Map<String, Object> params = new HashMap<String, Object>();
     			params.put("paramSet", param);
     			params.put("option", option);
     			
     			BypassClient bypassClient = new BypassClient(target);
     			bypassClient.setParams(params);
     			
     			BypassResult bypassResult = bypassClient.excute("cmdMeterParamSet");
     			log.debug("[cmdMeterParamSet] Excute Result = " + bypassResult.toString());
     			
     			if(bypassResult.getResultValue() instanceof Map) {
     				result = (Map<String, Object>) bypassResult.getResultValue();
     			} else {
     				Map<String,Object> map = new HashMap<String,Object>();
     				result.put("RESULT_VALUE", bypassResult.getResultValue());
     			}
     		}        	 
         }else{
 	    	result.put("RESULT_VALUE", "Can not found target. please check Meter & Modem information.");
 	    }
 		
 		return result;
     }
     
     
 	/**
 	 * NI Protocol Command :  Modem IP Information (set/get)
 	 **/
 	public Map<String, String> cmdModemIpInformation(String modemId, String requestType, int targetType, int ipType , String ipAddress) throws Exception {
 		log.debug("## NI command - Modem IP Information ["+modemId+"],["+requestType+"],["+targetType+"],["+ipType+"],["+ipAddress+"]");
 		//
 		Map map = new HashMap<String, String>();
 		ModemIpInformation resultObj = null;

 		Modem modem = modemDao.get(Integer.parseInt(modemId));

 		try {
 			/**
 			 * Ni Proxy
 			 */
 			HashMap niParam = new HashMap();
 			niParam.put("targetType", targetType);
 			niParam.put("ipType", ipType);
 			niParam.put("ipAddress", ipAddress);
 			CommandNIProxy niProxy = new CommandNIProxy();

 			if(requestType.equals("GET")){
 				resultObj = (ModemIpInformation)niProxy.execute(modem, NIAttributeId.ModemIpInformation_GET, niParam);
 			}
 			else {
 				resultObj = (ModemIpInformation)niProxy.execute(modem, NIAttributeId.ModemIpInformation, niParam);
 			}
 		} catch (Exception e) {
 			log.error(e, e);
 		}

 		// return map 작성
 		if(resultObj!=null && resultObj.getStatus() != null ){
 			map.put("ModemIpInformation", resultObj.toString());
 			map.put("cmdResult", "Execution OK");
 		}else{
 			map.put("cmdResult", "Failed to sending a command in CommandNIProxy.");
 		}
 		return map;
 	}
 	/**
 	 * NI Protocol Command :  Modem IP Information (set/get)
 	 **/
 	public Map<String, String> cmdModemPortInformation(String modemId, String requestType, int targetType, String port ) throws Exception {
 		log.debug("## NI command - Modem Port Information ["+modemId+"],["+requestType+"],["+targetType+"],["+port+"]");
 		//
 		Map map = new HashMap<String, String>();
 		ModemPortInformation resultObj = null;

 		Modem modem = modemDao.get(Integer.parseInt(modemId));

 		try {
 			/**
 			 * Ni Proxy
 			 */
 			HashMap niParam = new HashMap();
 			niParam.put("targetType", targetType);
 			niParam.put("port", port);
 			CommandNIProxy niProxy = new CommandNIProxy();

 			if(requestType.equals("GET")){
 				resultObj = (ModemPortInformation)niProxy.execute(modem, NIAttributeId.ModemPortInformation_GET, niParam);
 			}
 			else {
 				resultObj = (ModemPortInformation)niProxy.execute(modem, NIAttributeId.ModemPortInformation, niParam);
 			}
 		} catch (Exception e) {
 			log.error(e, e);
 		}

 		// return map 작성
 		if(resultObj!=null && resultObj.getStatus() != null){
 			map.put("ModemPortInformation", resultObj.toString());
 			map.put("cmdResult", "Execution OK");
 		}else{
 			map.put("cmdResult", "Failed to sending a command in CommandNIProxy.");
 		}
 		return map;
 	}
 	
    @Override
	public void cmdSetKMSNetworkKey(String mcuId, int keyIndex, String keyStream)
			throws FMPMcuException, Exception {

		log.debug("cmdSetKMSNetworkKey(" + mcuId + "," + keyIndex + "," + keyStream + ")");

		Target target = CmdUtil.getTarget(mcuId);

		Vector<SMIValue> datas = new Vector<SMIValue>();

		datas.add(DataUtil.getSMIValue(new INT(keyIndex)));
		datas.add(DataUtil.getSMIValue(new STREAM(keyStream)));

		Object[] params = new Object[] { target, "cmdSetKMSNetworkKey", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}
	}
    
    // INSERT START SP-443
 	public void cmdDCUMeterTimeSync(String modemId, String meterId ) throws FMPMcuException, Exception {
 		log.debug("cmdDCUMeterTimeSync(" + modemId + "," + meterId + ")");

 		Target target = null;
 		Meter meter = null;
 		String command = "cmdOndemand";

 		try {
 			meter = meterDao.get(meterId);
 			Modem modem = meter.getModem();
 			target = CmdUtil.getTarget(modem);
		    if(target == null){
		    	throw new Exception("Can not found target. please check Meter & Modem information.");
		    }
		    
 			Vector<SMIValue> datas = new Vector<SMIValue>();
 			SMIValue smiValue = null;

 			if ("SP".equals(target.getNameSpace())) {
				smiValue = DataUtil.getSMIValue(target.getNameSpace(),
						new INT(OnDemandOption.WRITE_OPTION_TIMESYNC.getCode()));		// 8
				datas.add(smiValue);
				smiValue = DataUtil.getSMIValue(target.getNameSpace(), new INT(0));		// 0
				datas.add(smiValue);
				smiValue = DataUtil.getSMIValue(target.getNameSpace(), new INT(0));		// 0
				datas.add(smiValue); 				
 				smiValue = DataUtil.getSMIValue(target.getNameSpace(), new INT(0x01));	// FILTER_METERID
// 				smiValue = DataUtil.getSMIValue(target.getNameSpace(), new INT(0x02));	// FILTER_MODEMID
 				datas.add(smiValue);
 				smiValue = DataUtil.getSMIValueByObject(target.getNameSpace(), "stringEntry", meterId); // meterID
// 				smiValue = DataUtil.getSMIValueByObject(target.getNameSpace(), "stringEntry", modemId); // modemID
 				datas.add(smiValue);
 		
 			}

 			Object[] params = new Object[] { target, command, datas };

 			String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

 			Object obj = null;
 			try {
 				obj = invoke(params, types);
 			} catch (Exception e) {
 				log.error(e, e);
 				throw new Exception(makeMessage(e.getMessage()));
 			}

 			SMIValue[] smiValues = null;

 			if (obj instanceof Integer) {
 				log.error("Error Code Return");
 				throw makeMcuException(((Integer) obj).intValue());
 			} else if (obj instanceof SMIValue[]) {
 				smiValues = (SMIValue[]) obj;
 				if (smiValues.length > 0) {
 					log.debug("smiValues.length = " + smiValues.length);
 					smiValue = smiValues[0];
 				} else {
 					log.error("smiValue size 0");
 					throw new FMPMcuException("no value exist");
 				}
 			} else {
 				log.error("Unknown Return Value");
 				throw new FMPMcuException("Unknown Return Value");
 			}

 		} catch (Exception e) {
 			log.error(e, e);
 			throw new Exception(e);
 		}
 	}
    // INSERT END SP-443

    // INSERT START SP-556
 	public void cmdDCUModemTimeSync(Modem[] modems) throws FMPMcuException, Exception {
 		log.debug("cmdDCUModemTimeSync(Modem[" + modems.length + "])");

 		Target target = null;
 		String command = "cmdDmdNiSet";

 		try {
	 			
			target = CmdUtil.getTarget(modems[0]);
			if (target == null) {
				throw new Exception("Can not found target. please check Modem information.");
			}

 			int timeout = Integer.parseInt(FMPProperty.getProperty("protocol.response.timeout", "180"));
 	        int maxNoOfModemsInDcu = Integer.parseInt(FMPProperty.getProperty("soria.meter.synctime.modem.max", "15"));

			Vector<SMIValue> datas = new Vector<SMIValue>();
			SMIValue smiValue = null;

			if ("SP".equals(target.getNameSpace())) {
				smiValue = DataUtil.getSMIValue(target.getNameSpace(), new UINT(timeout));	// timeoutSec
				datas.add(smiValue);
				smiValue = DataUtil.getSMIValue(target.getNameSpace(), new BYTE(1));		// noOfAttributes
				datas.add(smiValue); 				
				smiValue = DataUtil.getSMIValue(target.getNameSpace(), new INT(0x2004));	// attrId
				datas.add(smiValue);
 					
 				ByteArrayOutputStream stream = new ByteArrayOutputStream();
 	 			smiValue = DataUtil.getSMIValue(target.getNameSpace(), new STREAM(stream.toByteArray())); // Attribute Params
 	 			datas.add(smiValue);
				 					
 				for (int i = 0; i < modems.length; i++) { 						
 						log.debug("Modem Serial[" + i + "]" + modems[i].getDeviceSerial());			
 	 	 				smiValue = DataUtil.getSMIValue(target.getNameSpace(), new INT(0x02));	// FILTER_MODEMID
 	 					datas.add(smiValue);
 						String modemId = modems[i].getDeviceSerial();
 						smiValue = DataUtil.getSMIValueByObject(target.getNameSpace(), "stringEntry", modemId); // modemID
 						datas.add(smiValue);
				}
			}

			Object[] params = new Object[] { target, command, datas };
 			String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };
 			Object obj = null;

 			try {
 				obj = invoke(params, types);
 			} catch (Exception e) {
 				log.error(e, e);
 				throw new Exception(makeMessage(e.getMessage()));
 			}

 			SMIValue[] smiValues = null;

 			if (obj instanceof Integer) {
 				log.error("Error Code Return");
 				throw makeMcuException(((Integer) obj).intValue());
 			} else if (obj instanceof SMIValue[]) {
 				smiValues = (SMIValue[]) obj;
 				if (smiValues.length > 0) {
 					log.debug("smiValues.length = " + smiValues.length);
 				} else {
 					log.error("smiValue size 0");
 					throw new FMPMcuException("no value exist");
 				}
 			} else {
 				log.error("Unknown Return Value");
 				throw new FMPMcuException("Unknown Return Value");
 			}


 			if (smiValues.length < 3) {
				log.error("Execute command failed.Result length less than 3. ");
				throw new FMPMcuException("Execute command failed"); 				
 			}			
			for ( int j = 0; j < smiValues.length; j++ ){
				smiValue = smiValues[j];
				log.debug("SMIValue Return Class=" + smiValue.getVariable().getClass().getName());
				log.debug("smiValue[" + j + "] = " + smiValue.toString());
				if ((j==0) && (!smiValue.getVariable().toString().equals(modems[0].getDeviceSerial()))) {
					log.debug("Modem id is different. request[" + modems[0].getDeviceSerial() + 
							"] result[" + smiValue.getVariable().toString() + "]" );
				}
				else if ((j==1) && (Integer.parseInt(smiValue.getVariable().toString()) != ErrorCode.IF4ERR_NOERROR )) {
					log.debug("Return error code. [" + smiValue.getVariable().toString() + "]");
					throw new FMPMcuException("COULD NOT GET VALUE");	// INSERT 2017.3.10 SP-543
				}
				else if (j==2) {
					byte[] res = ((OCTET) smiValue.getVariable()).getValue();
					if (res == null) {
						log.debug("Result Stream Data is NULL.");
					} else {						
						int attrCount = DataFormat.hex2dec(res, 0, 2);
						int offset = 2;
						log.debug("Attribute Id Count [" + attrCount + "]");
						for(int n=0; n < attrCount; n++) {
							String attrDataID = Hex.decode(DataFormat.select(res, offset, 2));
							offset = offset + 2;
							int attrDataLen = DataFormat.hex2dec(res, offset, 2);
							offset = offset + 2;
							byte[] attrDataVal = DataFormat.select(res, offset, attrDataLen);
							offset = offset + attrDataLen;
							log.debug("Attribute Data [" + n+1 + 
									"] : AttributeId[" + attrDataID + 
									"] : attrDataVal[" + attrDataVal.toString() +
									"], Length[" + attrDataLen + 
									"]");
						}
					}
				}
			} 			
				
 		} catch (Exception e) {
 			log.error(e, e);
 			throw new Exception(e);
 		}
 	}
    // INSERT END SP-556

    // INSERT START SP-476
 	@Override
 	// UPDATE START SP-681
// 	public MeterData cmdDmdNiGetRomRead(String mcuId, String meterId, String modemId, String fromDate, String toDate)
//		throws FMPMcuException, Exception {
 	public MeterData cmdDmdNiGetRomRead(String mcuId, String meterId, String modemId, String fromDate, String toDate, int pollType)
		throws FMPMcuException, Exception {
 	// UPDATE END SP-681

		log.debug("cmdDmdNiGetRomRead(" +mcuId + "," + meterId + "," + modemId + "," +  fromDate + "," + toDate + "," + pollType + ")");

		int responseTimeout = Integer.valueOf(FMPProperty.getProperty("protocol.dtls.response.timeout","180"));		

		Target target = null;
 		Meter meter = null;
 		String command = "cmdDmdNiGet";
		JpaTransactionManager txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");
		TransactionStatus txStatus = null;
		List<byte[]> resultList = new ArrayList<byte[]>();
		
 		try {
 			meter = meterDao.get(meterId);
 			Modem modem = meter.getModem();
 			target = CmdUtil.getTarget(modem);
		    if(target == null){
		    	throw new Exception("Can not found target. please check Meter & Modem information.");
		    }
		    
		    ModemType modemType = modem.getModemType();
		    DeviceModel model = meter.getModel();
		    int lpInterval = meter.getLpInterval();
			int  modemPort =  meter.getModemPort() == null ? 0 : meter.getModemPort().intValue();
			if ( modemPort > 5 || modemPort < 0 ){
				log.debug("modemPort:" + modemPort + " is not support");
				throw new Exception("modemPort:" + modemPort + " is not support");
			}
			
 			Vector<SMIValue> datas = new Vector<SMIValue>();
 			SMIValue smiValue = null;
 			ByteArrayOutputStream stream = null;

 			if ("SP".equals(target.getNameSpace())) {
 				smiValue = DataUtil.getSMIValue(target.getNameSpace(), new UINT(responseTimeout));    	// timeout
				datas.add(smiValue);
				smiValue = DataUtil.getSMIValue(target.getNameSpace(), new BYTE(0x01));    				// Attribute count
				datas.add(smiValue);
 				smiValue = DataUtil.getSMIValue(target.getNameSpace(), new WORD(0xC201)); 				// Attribute ID RomRead(0xC201)
 				datas.add(smiValue);

 				int[] offsetCount = CmdUtil.convertOffsetCount(model, lpInterval, modemType, fromDate, toDate);
 				
 				stream = new ByteArrayOutputStream();
 				
 				// UPDATE START SP-681
// 				stream.write(DataUtil.getByteToInt(2));								// RomRead Type=2
//				stream.write(DataUtil.getByteToInt(1));								// Poll Data Count = 1
//				stream.write(DataUtil.getByteToInt(modemPort + 1));					// Poll Data - Type
//				stream.write(DataUtil.get2ByteToInt(offsetCount[0]));				// Poll Data - Offset
//				stream.write(DataUtil.get2ByteToInt(offsetCount[1]));				// Poll Data - Count

 				stream.write(DataUtil.getByteToInt(pollType));						// RomRead Type
				stream.write(DataUtil.getByteToInt(1));								// Poll Data Count = 1
				stream.write(DataUtil.getByteToInt(modemPort + 1));					// Poll Data - Type
 				if (pollType == 0) {
 					// SP is not use
 				}
 				else if (pollType == 1) {
 					// SP is not use
 				}
 				else if (pollType == 2) {
 					// Interval Metering Data Read
 					// Poll Information(Interval)
					stream.write(DataUtil.get2ByteToInt(offsetCount[0]));				// Poll Data - Offset
					stream.write(DataUtil.get2ByteToInt(offsetCount[1]));				// Poll Data - Count 					
 				}
 				else if (pollType == 3) {
 					// Interval Metering Data Read with timestamp
 					// Poll Information(Timestamp)
 					
 					if ((fromDate != null) && (fromDate.length()>=14)) {
 						stream.write(DataUtil.get2ByteToInt(Integer.parseInt(fromDate.substring(0,4))));	// YYYY
 						stream.write(DataUtil.getByteToInt(Integer.parseInt(fromDate.substring(4,6))));		// MM
 						stream.write(DataUtil.getByteToInt(Integer.parseInt(fromDate.substring(6,8))));		// DD
 						stream.write(DataUtil.getByteToInt(Integer.parseInt(fromDate.substring(8,10))));	// hh
 						stream.write(DataUtil.getByteToInt(Integer.parseInt(fromDate.substring(10,12))));	// mm
 						stream.write(DataUtil.getByteToInt(Integer.parseInt(fromDate.substring(12,14))));	// ss
 					} else {
 						log.debug("fromDate format is incorrect.");
 						throw new Exception("fromDate format is incorrect.");
 					}
 					if ((toDate != null) && (toDate.length()>=14)) {
 						stream.write(DataUtil.get2ByteToInt(Integer.parseInt(toDate.substring(0,4))));	// YYYY
 						stream.write(DataUtil.getByteToInt(Integer.parseInt(toDate.substring(4,6))));	// MM
 						stream.write(DataUtil.getByteToInt(Integer.parseInt(toDate.substring(6,8))));	// DD
 						stream.write(DataUtil.getByteToInt(Integer.parseInt(toDate.substring(8,10))));	// hh
 						stream.write(DataUtil.getByteToInt(Integer.parseInt(toDate.substring(10,12))));	// mm
 						stream.write(DataUtil.getByteToInt(Integer.parseInt(toDate.substring(12,14))));	// ss
 					} else {
 						log.debug("toDate format is incorrect.");
 						throw new Exception("toDate format is incorrect.");
 					}
 					
 				}
 				// UPDATE END SP-681
 				
 				smiValue = DataUtil.getSMIValue(target.getNameSpace(), new STREAM(stream.toByteArray())); // Attribute Params
 				datas.add(smiValue);
 								
 				smiValue = DataUtil.getSMIValue(target.getNameSpace(), new INT(0x02));	// FILTER_MODEMID
 				datas.add(smiValue);
 				smiValue = DataUtil.getSMIValueByObject(target.getNameSpace(), "stringEntry", modemId); // modemID
 				datas.add(smiValue);
 		
 			}

 			Object[] params = new Object[] { target, command, datas };

 			String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

 			Object obj = null;
 			try {
 				obj = invoke(params, types);
 			} catch (Exception e) {
 				log.error(e, e);
 				throw new Exception(makeMessage(e.getMessage()));
 			}

 			SMIValue[] smiValues = null;

 			if (obj instanceof Integer) {
 				log.error("Error Code Return");
 				throw makeMcuException(((Integer) obj).intValue());
 			} else if (obj instanceof SMIValue[]) {
 				smiValues = (SMIValue[]) obj;
 				if (smiValues.length > 0) {
 					log.debug("smiValues.length = " + smiValues.length);
 				} else {
 					log.error("smiValue size 0");
 					throw new FMPMcuException("no value exist");
 				}
 			} else {
 				log.error("Unknown Return Value");
 				throw new FMPMcuException("Unknown Return Value");
 			}


 			if (smiValues.length < 3) {
				log.error("Execute command failed.Result length less than 3. ");
				throw new FMPMcuException("Execute command failed"); 				
 			}			
			for ( int j = 0; j < smiValues.length; j++ ){
				smiValue = smiValues[j];
				log.debug("SMIValue Return Class=" + smiValue.getVariable().getClass().getName());
				log.debug("smiValue[" + j + "] = " + smiValue.toString());
				if ((j==0) && (!smiValue.getVariable().toString().equals(modemId))) {
					log.debug("Modem id is different. request[" + modemId + 
							"] result[" + smiValue.getVariable().toString() + "]" );
				}
				else if ((j==1) && (Integer.parseInt(smiValue.getVariable().toString()) != ErrorCode.IF4ERR_NOERROR )) {
					log.debug("Return error code. [" + smiValue.getVariable().toString() + "]");
					throw new FMPMcuException("COULD NOT GET VALUE");	// INSERT 2017.3.10 SP-543
				}
				else if (j==2) {
					byte[] res = ((OCTET) smiValue.getVariable()).getValue();
					if (res == null) {
						log.debug("Result Stream Data is NULL.");
					} else {						
						int attrCount = DataFormat.hex2dec(res, 0, 2);
						int offset = 2;
						log.debug("Attribute Id Count [" + attrCount + "]");
						for(int n=0; n < attrCount; n++) {
							String attrDataID = Hex.decode(DataFormat.select(res, offset, 2));
							offset = offset + 2;
							int attrDataLen = DataFormat.hex2dec(res, offset, 2);
							offset = offset + 2;
							byte[] attrDataVal = DataFormat.select(res, offset, attrDataLen);
							offset = offset + attrDataLen;
							log.debug("Attribute Data [" + n+1 + 
									"] : AttributeId[" + attrDataID + 
									"], Length[" + attrDataLen + 
									"]");
							// NI protocol header (5byte)
							// Type(0x02:Interval Metering DataRead)
							// Total Poll Data Count (1byte)
							// Poll Data - Type (0x01:DLMS Load profile)
							// Poll Data - Length (2byte)  
							String type = Hex.decode(DataFormat.select(attrDataVal, 0, 1));
							int offset2 = 1;
							int pollCount = DataFormat.hex2dec(attrDataVal, offset2, 1);
							offset2 = offset2 + 1;

							log.debug("NI protocol header : Type[" + type + 
									"] , Total count[" + pollCount + 
									"]");
							
							ByteBuffer buf = ByteBuffer.allocate(attrDataLen-5);
							buf.clear();
							for(int m=0; m < pollCount; m++) {
								String pollDataType = Hex.decode(DataFormat.select(attrDataVal, offset2, 1));
								offset2 = offset2 + 1;
								int pollDataLen = DataFormat.hex2dec(attrDataVal, offset2, 2);
								offset2 = offset2 + 2;
								byte[] pollDataVal = DataFormat.select(attrDataVal, offset2, pollDataLen);
								offset2 = offset2 + pollDataLen;
								buf.put(pollDataVal);								

								log.debug("NI protocol Poll Data [" + m+1 + 
										"] : Type[" + pollDataType + 
										"] , Length[" + pollDataLen + 
										"]");
							}
							resultList.add(buf.array());
						}
					}
				}
			} 			
 			
			txStatus = txManager.getTransaction(null);
			meter = meterDao.get(meterId);
			MeterData ed = new MeterData();
			MeterDataParser edp = null;
			DeviceModel deviceModel = meter.getModel();

			if (deviceModel == null)
				deviceModel = meter.getModem().getModel();

			DeviceConfig deviceConfig = deviceModel.getDeviceConfig();

			if (deviceConfig.getOndemandParserName() != null && !"".equals(deviceConfig.getOndemandParserName()))
				edp = (MeterDataParser) Class.forName(deviceConfig.getOndemandParserName()).newInstance();
			else
				edp = (MeterDataParser) Class.forName(deviceConfig.getParserName()).newInstance();

			edp.setMeter(meter);
			edp.setMeteringTime(DateTimeUtil.getDateString(new Date()));
			edp.setOnDemand(true);
			ed.setMeterId(meterId);
			if ( edp instanceof 	DLMSKaifa ) {
				((DLMSKaifa)edp).setModemId(modem.getDeviceSerial());			
				((DLMSKaifa)edp).setModemPort(modemPort);
			}			          

			for (int i=0; i<resultList.size(); i++)
			{
				edp.parse(resultList.get(i));
				ed.setParser(edp);
				saveMeteringDataByQueue(mcuId, meterId, modemId, resultList.get(i));            
			}
			
            modem.setLastLinkTime(meter.getLastReadDate());
            modemDao.update(modem);	

			txManager.commit(txStatus);
			return ed;					
 		 			
 			
 		} catch (Exception e) {
 			log.error(e, e);
 			throw new Exception(e);
 		}
 	}    
    // INSERT END SP-476 
 	// INSERT START SP-487
 	public void saveMeteringDataByQueue(String mcuId, String meterId, String modemId, byte[] data)  
 			throws Exception {
		try {
			
			Meter meter = meterDao.get(meterId);
 			Modem modem = meter.getModem();	
 			DeviceModel model = meter.getModel();
			
			OCTET ocMeterId = new OCTET(20);
			ocMeterId.setIsFixed(true);
			byte[] bMeterid = DataUtil.fillCopy(meterId.getBytes(), (byte) 0x20, 20);
			ocMeterId.setValue(bMeterid);
	
			OCTET ocModemId = new OCTET(8);
			ocModemId.setIsFixed(true);
			byte[] bModemid = Hex.encode(modemId);
			ocModemId.setValue(bModemid);
	
			String currTime = null;
			try {
				currTime = TimeUtil.getCurrentTime();
			} catch (Exception e) {
			}
			meterDataEntry value = new meterDataEntry();
			value.setDataCount(1);
			value.setMdData(new OCTET(data));
			value.setMdID(ocModemId);
			value.setMdSerial(ocMeterId);
			value.setMdServiceType(new BYTE(DataSVC.Electricity.getCode()));
			value.setMdTime(new TIMESTAMP(currTime));
			value.setMdType(new BYTE(Integer.parseInt(CommonConstants.getModemTypeCode(modem.getModemType()))));

			MeterVendor vendor = MeterVendor.valueOf(model.getDeviceVendor().getName());			
			value.setMdVendor(new BYTE(vendor.getCode()[0]));
	
			log.debug("saveMeteringDataByQueue(" +mcuId + "," + meterId + "," + modemId + ")" );			
			log.debug("setMdTime(" + currTime + ")" );			
			log.debug("setMdVendor(" + model.getDeviceVendor() + 
					"," + model.getDeviceVendorId() +
					"," + model.getDeviceVendor().getCode() +
					"," + model.getDeviceVendor().getName() +
					"," + vendor.getCode()[0] +
					"," + vendor.getName() +
					")" );			
			
			
			int dataLen = 7 + value.getMdData().getValue().length;
			ByteArray ba = new ByteArray();
			ba.append(value.getMdID().getValue());
			ba.append(value.getMdSerial().getValue());
			ba.append(value.getMdType().encode());
			ba.append(value.getMdServiceType().encode());
			ba.append(value.getMdVendor().encode());
			ba.append(value.getDataCount().encode());
			ba.append(DataUtil.get2ByteToInt(true, dataLen)); // Timestamp.length+Data.length
			ba.append(value.getMdTime().encode());
			ba.append(value.getMdData().getValue());
	
			MDData mdData = new MDData(new WORD(1));
			mdData.setMcuId(mcuId);
			mdData.setTotalLength(ba.toByteArray().length);
			mdData.setMdData(ba.toByteArray());
			ProcessorHandler handler = DataUtil.getBean(ProcessorHandler.class);
	        handler.putServiceData(ProcessorHandler.SERVICE_MEASUREMENTDATA, mdData); 		
		} catch (Exception e) {
 			log.error(e, e);
 			throw new Exception(e);			
		}
 	}
 	// INSERT END SP-487

    // INSERT START SP-643
 	public void cmdDmdNiSetMSKToModem(String meterId) throws FMPMcuException, Exception {
		log.debug("cmdDmdNiSetMSKToModem("  + meterId + " )");

 		Target target = null;
 		String command = "cmdDmdNiSet";
		int responseTimeout = Integer.valueOf(FMPProperty.getProperty("protocol.dtls.response.timeout","180"));		
		double fwVer = Double.parseDouble(FMPProperty.getProperty("pana.modem.fw.ver"));
 		try {
			Meter meter = meterDao.get(meterId);
 			Modem modem = meter.getModem();
 			 				
			target = CmdUtil.getTarget(modem);
			if (target == null) {
				throw new Exception("Can not found target. please check Modem information.");
			}
 			
			if (Double.parseDouble(modem.getFwVer()) < fwVer) {
 				throw new Exception("modem firmware version is old.[" + modem.getFwVer() + "]");
 			}
 			
			Vector<SMIValue> datas = new Vector<SMIValue>();
			SMIValue smiValue = null;

			// timeout
			smiValue = DataUtil.getSMIValue(target.getNameSpace(), new UINT(responseTimeout));
			datas.add(smiValue);
			
			// Attribute count
			smiValue = DataUtil.getSMIValue(target.getNameSpace(), new BYTE(0x02));
			datas.add(smiValue);
			
			// 1 Attribute ID Meter Shared Key (0xC005)
			smiValue = DataUtil.getSMIValue(target.getNameSpace(), new WORD(0xC005)); 				
			datas.add(smiValue);
			
			// 1 Attribute params
			GeneralFrame gframe = new GeneralFrame();
            OacServerApi api  = new OacServerApi();
            HashMap<String,String> sharedKey = null;
            sharedKey = api.getPanaMeterSharedKey(modem.getDeviceSerial(), meterId);
			if (sharedKey == null) {
				throw new Exception("Can not get Meter Shared Key.");
			}
			
            String masterKey = sharedKey.get("MasterKey");
            String unicastKey = sharedKey.get("UnicastKey");
            String multicastKey = sharedKey.get("MulticastKey");
            String authKey = sharedKey.get("AuthenticationKey");
                
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			stream.write(Hex.encode(masterKey));
			stream.write(Hex.encode(multicastKey));
			stream.write(Hex.encode(unicastKey));
			stream.write(Hex.encode(authKey));
                			
 			smiValue = DataUtil.getSMIValue(target.getNameSpace(), new STREAM(stream.toByteArray()));
 			datas.add(smiValue);
			 					
			// 2 Attribute ID Modem Schedule Run (0x6001)
			smiValue = DataUtil.getSMIValue(target.getNameSpace(), new WORD(0x6001)); 				
			datas.add(smiValue);

			// 2 Attribute params
			ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
			stream2.write(DataUtil.getByteToInt(3));				// Modem Schedule Type = 3 (Meter shared key)
			stream2.write(DataUtil.get4ByteToInt(0));				// Count

			smiValue = DataUtil.getSMIValue(target.getNameSpace(), new STREAM(stream2.toByteArray()));
			datas.add(smiValue);						

 			// Filters
			smiValue = DataUtil.getSMIValue(target.getNameSpace(), new INT(0x02));	// FILTER_MODEMID
			datas.add(smiValue);
			smiValue = DataUtil.getSMIValueByObject(target.getNameSpace(), "stringEntry", modem.getDeviceSerial()); // modemID
			datas.add(smiValue);

			Object[] params = new Object[] { target, command, datas };
 			String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };
 			Object obj = null;

 			obj = invoke(params, types);
 			SMIValue[] smiValues = null;

 			if (obj instanceof Integer) {
 				log.error("Error Code Return");
 				throw makeMcuException(((Integer) obj).intValue());
 			} else if (obj instanceof SMIValue[]) {
 				smiValues = (SMIValue[]) obj;
 				if (smiValues.length > 0) {
 					log.debug("smiValues.length = " + smiValues.length);
 				} else {
 					log.error("smiValue size 0");
 					throw new FMPMcuException("no value exist");
 				}
 			} else {
 				log.error("Unknown Return Value");
 				throw new FMPMcuException("Unknown Return Value");
 			}


 			if (smiValues.length < 3) {
				log.error("Execute command failed.Result length less than 3. ");
				throw new FMPMcuException("Execute command failed"); 				
 			}			
			for ( int j = 0; j < smiValues.length; j++ ){
				smiValue = smiValues[j];
				log.debug("SMIValue Return Class=" + smiValue.getVariable().getClass().getName());
				log.debug("smiValue[" + j + "] = " + smiValue.toString());
				if ((j==0) && (!smiValue.getVariable().toString().equals(modem.getDeviceSerial()))) {
					log.debug("Modem id is different. request[" + modem.getDeviceSerial() + 
							"] result[" + smiValue.getVariable().toString() + "]" );
				}
				else if ((j==1) && (Integer.parseInt(smiValue.getVariable().toString()) != ErrorCode.IF4ERR_NOERROR )) {
					log.debug("Return error code. [" + smiValue.getVariable().toString() + "]");
					throw new FMPMcuException("COULD NOT GET VALUE");
				}
				else if (j==2) {
					byte[] res = ((OCTET) smiValue.getVariable()).getValue();
					if (res == null) {
						log.debug("Result Stream Data is NULL.");
					} else {						
						int attrCount = DataFormat.hex2dec(res, 0, 2);
						int offset = 2;
						log.debug("Attribute Id Count [" + attrCount + "]");
						for(int n=0; n < attrCount; n++) {
							String attrDataID = Hex.decode(DataFormat.select(res, offset, 2));
							offset = offset + 2;
							int attrDataLen = DataFormat.hex2dec(res, offset, 2);
							offset = offset + 2;
							byte[] attrDataVal = DataFormat.select(res, offset, attrDataLen);
							offset = offset + attrDataLen;
							log.debug("Attribute Data [" + n+1 + 
									"] : AttributeId[" + attrDataID + 
									"] : attrDataVal[" + attrDataVal.toString() +
									"], Length[" + attrDataLen + 
									"]");
						}
					}
				}
			} 			
								
 		} catch (Exception e) {
 			log.error(e, e);
 			throw new Exception(e);
 		}
 	}
 	
 	// INSERT STARt SP-681 	
	public Map<String, String> cmdExecDmdNiCommand(String modemId, String requestType, String attributeId, String param ) throws Exception {
		log.debug("cmdExecDmdNiCommand ["+modemId+"],["+requestType+"],["+ attributeId +"],[" + param +"]");

		Map map = new HashMap<String, String>();
		String resultString = "";
		List<String> resultList = new ArrayList<String>();
 		Target target = null;

		String command = "";
 		if(requestType.equals("GET")){ 	
 			command = "cmdDmdNiGet";
 		}
 		else {
 			command = "cmdDmdNiSet";
 		}
 		
 		int responseTimeout = Integer.valueOf(FMPProperty.getProperty("protocol.dtls.response.timeout","180"));		
 		try {
 			Modem modem = modemDao.get(Integer.parseInt(modemId));
			target = CmdUtil.getTarget(modem);
			if (target == null) {
				throw new Exception("Can not found target. please check Modem information.");
			}
 			
			Vector<SMIValue> datas = new Vector<SMIValue>();
			SMIValue smiValue = null;

			// timeout
			smiValue = DataUtil.getSMIValue(target.getNameSpace(), new UINT(responseTimeout));
			datas.add(smiValue);
			
			// Attribute count
			smiValue = DataUtil.getSMIValue(target.getNameSpace(), new BYTE(0x01));
			datas.add(smiValue);
			
			// Attribute ID (□□□□)
			byte[] hexid = Hex.encode(attributeId);
			smiValue = DataUtil.getSMIValue(target.getNameSpace(), new WORD(hexid)); 				
			//smiValue = DataUtil.getSMIValue(target.getNameSpace(), new STREAM(hexid)); 				
			datas.add(smiValue);
			
			// Attribute params
			byte[] hexparam = Hex.encode(param);                			
 			smiValue = DataUtil.getSMIValue(target.getNameSpace(), new STREAM(hexparam));
 			datas.add(smiValue);

 			// Filters
			smiValue = DataUtil.getSMIValue(target.getNameSpace(), new INT(0x02));	// FILTER_MODEMID
			datas.add(smiValue);
			smiValue = DataUtil.getSMIValueByObject(target.getNameSpace(), "stringEntry", modem.getDeviceSerial()); // modemID
			datas.add(smiValue);

			Object[] params = new Object[] { target, command, datas };
 			String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };
 			Object obj = null;

 			obj = invoke(params, types);
 			SMIValue[] smiValues = null;

 			if (obj instanceof Integer) {
 				log.error("Error Code Return");
 				throw makeMcuException(((Integer) obj).intValue());
 			} else if (obj instanceof SMIValue[]) {
 				smiValues = (SMIValue[]) obj;
 				if (smiValues.length > 0) {
 					log.debug("smiValues.length = " + smiValues.length);
 				} else {
 					log.error("smiValue size 0");
 					throw new FMPMcuException("no value exist");
 				}
 			} else {
 				log.error("Unknown Return Value");
 				throw new FMPMcuException("Unknown Return Value");
 			}


 			if (smiValues.length < 3) {
				log.error("Execute command failed.Result length less than 3. ");
				throw new FMPMcuException("Execute command failed"); 				
 			}			
			for ( int j = 0; j < smiValues.length; j++ ){
				smiValue = smiValues[j];
				log.debug("SMIValue Return Class=" + smiValue.getVariable().getClass().getName());
				log.debug("smiValue[" + j + "] = " + smiValue.toString());
				if ((j==0) && (!smiValue.getVariable().toString().equals(modem.getDeviceSerial()))) {
					log.debug("Modem id is different. request[" + modem.getDeviceSerial() + 
							"] result[" + smiValue.getVariable().toString() + "]" );
					resultString="Execution failed. Return modem id is different.";
				}
				else if ((j==1) && (Integer.parseInt(smiValue.getVariable().toString()) != ErrorCode.IF4ERR_NOERROR )) {
					log.debug("Return error code. [" + smiValue.getVariable().toString() + "]");
					//throw new FMPMcuException("COULD NOT GET VALUE");
					resultString="Execution failed. Return error code.";
				}
				else if (j==2) {
					byte[] res = ((OCTET) smiValue.getVariable()).getValue();
					if (res == null) {
						log.debug("Result Stream Data is NULL.");
						resultString = "Execution OK. Result data is null.";
					} else {						
						resultString = "Execution OK";
						int attrCount = DataFormat.hex2dec(res, 0, 2);
						int offset = 2;
						log.debug("Attribute Id Count [" + attrCount + "]");
						map.put("AttributeIdCount", String.valueOf(attrCount));
						for(int n=0; n < attrCount; n++) {
							String attrDataID = Hex.decode(DataFormat.select(res, offset, 2));
							offset = offset + 2;
							int attrDataLen = DataFormat.hex2dec(res, offset, 2);
							offset = offset + 2;
							byte[] attrDataVal = DataFormat.select(res, offset, attrDataLen);
							offset = offset + attrDataLen;
							log.debug("Attribute Data [" + n+1 + 
									"] : AttributeId[" + attrDataID + 
									"] : attrDataVal[" + Hex.decode(attrDataVal) +
									"] : Length[" + attrDataLen + 
									"]");
							
							JSONObject jo = new JSONObject();
							jo.put("AttributeId", attrDataID);
							jo.put("Length", String.valueOf(attrDataLen));
//							jo.put("Value", attrDataVal.toString());
							jo.put("Value", Hex.decode(attrDataVal));
							
							map.put("AttributeData", jo.toString());								
						}
					}
				}
			} 			
								
 		} catch (Exception e) {
 			log.error(e, e);
 			resultString="Execution failed.";
 		}

		// return map 
 		map.put("cmdResult", resultString);
		return map;
	}	

 	// INSERT END SP-681
	/**
 	 * SP-677
 	 * @param mcuId
 	 * @param meterIds array of mdsId
 	 * @param modemId
 	 * @param fromDate
 	 * @param toDate
 	 * @return
 	 * @throws FMPMcuException
 	 * @throws Exception
 	 */
 	public Map<String, Object> cmdDmdNiGetRomReadMulti(String mcuId, Integer modemPort, String[] meterIds, String[] modemIds, String fromDate, String toDate)
 			throws FMPMcuException, Exception {
    	Map<String,Object> result = new HashMap<String,Object>();
		JpaTransactionManager txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");
		TransactionStatus txStatus = null;
		ArrayList<MeterData> retList = new  ArrayList<MeterData>();

		Target target = null;
 		Meter meter = null;
 		String command = "cmdDmdNiGet";
 		
		if ( meterIds.length == 0 ){
			log.error("No Meters");
			throw new Exception("No Meters");
		}
		
 		StringBuffer modemsBuff = new StringBuffer();
 		for ( int i = 0; i < modemIds.length; i++){
 			if ( modemsBuff.length() == 0 )
 				modemsBuff.append(",");
 			modemsBuff.append(modemIds[i]+",");
 		}
 		
		log.debug("cmdDmdNiGetRomReadMulti(mcuId[" +mcuId + "] modemPort[" + modemPort + "] modems["+ modemsBuff.toString() + "] from[" +  fromDate + "] to[" + toDate + "]");
		
		int responseTimeout = Integer.valueOf(FMPProperty.getProperty("protocol.dtls.response.timeout","180"));	
		int[] offsetCount = null;

		// get first meter and check namespace, get modemType, model, interval
		meter = meterDao.get(meterIds[0]);
		Modem modem = meter.getModem();
		target = CmdUtil.getTarget(modem);

		if (!"SP".equals(target.getNameSpace())) {
			log.error(target.getNameSpace() + " is not supported.");
			throw new Exception(target.getNameSpace() + " is not supported.");
		}

		ModemType modemType = modem.getModemType();
		DeviceModel model = meter.getModel();
		int lpInterval = meter.getLpInterval();
		offsetCount = CmdUtil.convertOffsetCount(model, lpInterval, modemType, fromDate, toDate);
	
 		try {
 			Vector<SMIValue> datas = new Vector<SMIValue>();
 			SMIValue smiValue = null;
 			ByteArrayOutputStream stream = null;
 			String modems[] = new String[meterIds.length];
 			
 			smiValue = DataUtil.getSMIValue(target.getNameSpace(), new UINT(responseTimeout));    	// timeout
			datas.add(smiValue);
			smiValue = DataUtil.getSMIValue(target.getNameSpace(), new BYTE(0x01));    				// Attribute count
			datas.add(smiValue);
 			smiValue = DataUtil.getSMIValue(target.getNameSpace(), new WORD(0xC201)); 				// Attribute ID RomRead(0xC201)
 			datas.add(smiValue);
							
			stream = new ByteArrayOutputStream();
			stream.write(DataUtil.getByteToInt(2));								// RomRead Type=2
			stream.write(DataUtil.getByteToInt(1));								// Poll Data Count = 1
			stream.write(DataUtil.getByteToInt(modemPort + 1));					// Poll Data - Type
			stream.write(DataUtil.get2ByteToInt(offsetCount[0]));				// Poll Data - Offset
			stream.write(DataUtil.get2ByteToInt(offsetCount[1]));				// Poll Data - Count
			
			smiValue = DataUtil.getSMIValue(target.getNameSpace(), new STREAM(stream.toByteArray())); // Attribute Params
			datas.add(smiValue);
			for ( int i = 0; i < modemIds.length; i++ ){					
				smiValue = DataUtil.getSMIValue(target.getNameSpace(), new INT(0x02));	// FILTER_MODEMID
				datas.add(smiValue);
				smiValue = DataUtil.getSMIValueByObject(target.getNameSpace(), "stringEntry", modemIds[i]); // modemID
				datas.add(smiValue);	
			}		

 			Object[] params = new Object[] { target, command, datas };

 			String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };

 			Object obj = null;
 			try {
 				obj = invoke(params, types);
 			} catch (Exception e) {
 				log.error(e, e);
 				throw new Exception(makeMessage(e.getMessage()));
 			}

 			SMIValue[] smiValues = null;

 			if (obj instanceof Integer) {
 				log.error("Error Code Return");
 				throw makeMcuException(((Integer) obj).intValue());
 			} else if (obj instanceof SMIValue[]) {
 				smiValues = (SMIValue[]) obj;
 				if (smiValues.length > 0) {
 					log.debug("smiValues.length = " + smiValues.length);
 				} else {
 					log.error("smiValue size 0");
 					throw new FMPMcuException("no value exist");
 				}
 			} else {
 				log.error("Unknown Return Value");
 				throw new FMPMcuException("Unknown Return Value");
 			}

 			if (smiValues.length < 3) {
 				log.error("Execute command failed.Result length less than 3. ");
 				throw new FMPMcuException("Execute command failed"); 				
 			}

 			for ( int i = 0; i < smiValues.length / 3; i++ ){
 				List<byte[]> resultList = new ArrayList<byte[]>();
 				boolean error = false;
 				String modemId = null;
 				String meterId = null;
 				txStatus = null;
 				// get data by each modem
 				for ( int j = 0; j < 3; j++){
 					if ( i*3+j >= smiValues.length){
 						log.error("invalid simValue size( not Multiples of 3 )");
 		 				throw new FMPMcuException("Execute command failed");
 					}
 					smiValue = smiValues[i*3+j];
 					log.debug("SMIValue Return Class=" + smiValue.getVariable().getClass().getName() + ", smiValue[" + i*3+j + "] = " + smiValue.toString());
 					
 					if ( j == 0 ){ 
 						modemId = smiValue.getVariable().toString();
 						boolean found = false;
 						for ( int l = 0; l < modemIds.length; l++){
 							if( modemId.equals(modemIds[l])){
 								meterId = meterIds[l];
 								log.debug("MdsId[" + meterId + "] return Data ");
 								found = true;
 								break;
 							}
 						}
 						if ( !found ){
 							log.debug("Modem id is different. request[" + modemsBuff.toString() + 
 									"] result[" + smiValue.getVariable().toString() + "]" );
 						}
 					}
 					else if (j==1) {
 						result.put(meterId, smiValue.getVariable().toString());
 						if  (Integer.parseInt(smiValue.getVariable().toString()) != ErrorCode.IF4ERR_NOERROR ){
 							log.debug("Return error code. [" + smiValue.getVariable().toString() + "]");
 							error = true;
 						}
 					}
 					else if (j==2) {
 						if ( !error ){ 
 							byte[] res = ((OCTET) smiValue.getVariable()).getValue();
 							if (res == null) {
 								log.debug("Result Stream Data is NULL.");
 							} else {						
 								int attrCount = DataFormat.hex2dec(res, 0, 2);
 								int offset = 2;
 								log.debug("Attribute Id Count [" + attrCount + "]");
 								for(int n=0; n < attrCount; n++) {
 									String attrDataID = Hex.decode(DataFormat.select(res, offset, 2));
 									offset = offset + 2;
 									int attrDataLen = DataFormat.hex2dec(res, offset, 2);
 									offset = offset + 2;
 									byte[] attrDataVal = DataFormat.select(res, offset, attrDataLen);
 									offset = offset + attrDataLen;
 									log.debug("Attribute Data [" + n+1 + 
 											"] : AttributeId[" + attrDataID + 
 											"], Length[" + attrDataLen + 
 											"]");
 									// NI protocol header (5byte)
 									// Type(0x02:Interval Metering DataRead)
 									// Total Poll Data Count (1byte)
 									// Poll Data - Type (0x01:DLMS Load profile)
 									// Poll Data - Length (2byte)  
 									String type = Hex.decode(DataFormat.select(attrDataVal, 0, 1));
 									int offset2 = 1;
 									int pollCount = DataFormat.hex2dec(attrDataVal, offset2, 1);
 									offset2 = offset2 + 1;

 									log.debug("NI protocol header : Type[" + type + 
 											"] , Total count[" + pollCount + 
 											"]");

 									ByteBuffer buf = ByteBuffer.allocate(attrDataLen-5);
 									buf.clear();
 									for(int m=0; m < pollCount; m++) {
 										String pollType = Hex.decode(DataFormat.select(attrDataVal, offset2, 1));
 										offset2 = offset2 + 1;
 										int pollDataLen = DataFormat.hex2dec(attrDataVal, offset2, 2);
 										offset2 = offset2 + 2;
 										byte[] pollDataVal = DataFormat.select(attrDataVal, offset2, pollDataLen);
 										offset2 = offset2 + pollDataLen;
 										buf.put(pollDataVal);								

 										log.debug("NI protocol Poll Data [" + m+1 + 
 												"] : Type[" + pollType + 
 												"] , Length[" + pollDataLen + 
 												"]");
 									}
 									resultList.add(buf.array());
 								}
 							}
 						}
 					}
 				}// for ( int j = 0; j < 3; j++){				
 				try {
 					if ( modemId != null && resultList.size() > 0 ){

 						txStatus = txManager.getTransaction(null);
 						
 						meter = meterDao.get(meterId);
 						modem = meter.getModem();
 						MeterData ed = new MeterData();
 						MeterDataParser edp = null;
 						DeviceModel deviceModel = meter.getModel();

 						if (deviceModel == null)
 							deviceModel = meter.getModem().getModel();

 						DeviceConfig deviceConfig = deviceModel.getDeviceConfig();

 						if (deviceConfig.getOndemandParserName() != null && !"".equals(deviceConfig.getOndemandParserName()))
 							edp = (MeterDataParser) Class.forName(deviceConfig.getOndemandParserName()).newInstance();
 						else
 							edp = (MeterDataParser) Class.forName(deviceConfig.getParserName()).newInstance();

 						edp.setMeter(meter);
 						edp.setMeteringTime(DateTimeUtil.getDateString(new Date()));
 						edp.setOnDemand(true);
 						ed.setMeterId(meterId);
 						if ( edp instanceof 	DLMSKaifa ) {
 							((DLMSKaifa)edp).setModemId(modem.getDeviceSerial());			
 							((DLMSKaifa)edp).setModemPort(modemPort);
 						}			          

 						for (int k =0; k<resultList.size(); k++)
 						{
 							edp.parse(resultList.get(k));
 							ed.setParser(edp);
 							saveMeteringDataByQueue(mcuId, meterId, modemId, resultList.get(k));            
 						}

 //						modem.setLastLinkTime(meter.getLastReadDate());
 //						modemDao.update(modem);	

 						txManager.commit(txStatus);
 						retList.add(ed);						 			
 					}
 				} catch (Exception e) {
 		 			log.error(e, e);
 		 			if (txStatus != null ){
 		 				txManager.rollback(txStatus);
 		 			}
 				}
 			}// for ( int i = 0; i < smiValues.length / 3; i++
 		}catch (Exception ex){
 			log.error(ex,ex);
 			throw ex;
 		}
 		
 		if ( retList.size() > 0 ){
 			result.put("RESULT", true);
 		}
 		else {
 			result.put("RESULT", false);
 		}	
 		return result;
 	}
 	
 	/**
	 * MCU Get Schedule
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param name
	 *            Schedule Name
	 * @return schdule list
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public Map<String, Object> cmdMcuGetSchedule_(String mcuId, String name) throws FMPMcuException, Exception {
		log.debug("cmdMcuGetSchedule MCU[" + mcuId + "], Name[" + name + "]");

		Target target = CmdUtil.getTarget(mcuId);
		Vector<SMIValue> datas = null;

		if (name != null && !"".equals(name)) {
			datas = new Vector<SMIValue>();
			datas.add(DataUtil.getSMIValue(target.getNameSpace(), new STRING(name)));
		}

		Object[] params = new Object[] { target, "cmdGetSchedule", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		SMIValue[] smiValues = null;

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		} else if (obj instanceof SMIValue[]) {
			smiValues = (SMIValue[]) obj;
		} else {
			log.error("Unknown Return Value");
			throw new Exception("Unknown Return Value");
		}

		Map<String, Object> result = new HashMap<String, Object>();		
		try{
		if (smiValues != null && smiValues.length != 0) {
			// schedule test
			int cnt = 0;
			for (int i = 0; i < smiValues.length; i += 4) {
				result.put("name"+cnt, smiValues[i + 0] == null? "" : smiValues[i + 0].getVariable().toString());
				result.put("suspend"+cnt, smiValues[i + 1] == null? "" : smiValues[i + 1].getVariable().toString());
				result.put("condition"+cnt, smiValues[i + 2] == null? "" : smiValues[i + 2].getVariable().toString());
				result.put("task"+cnt, smiValues[i + 3] == null? "" : smiValues[i + 3].getVariable().toString());
				cnt++;
			}
		} else {
			log.debug("smiValues is null");
		}
		}catch (Exception ce){
			log.error(ce,ce);
		}
		

		return result;
	}

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
	@Override
	public void cmdMcuSetSchedule_(String mcuId, String[][] args) throws FMPMcuException, Exception {
		log.debug("cmdMcuSetSchedule(" + mcuId + ") args.length(" + args.length + ")");

		Target target = CmdUtil.getTarget(mcuId);
		Vector<SMIValue> datas = null;

		if (args.length == 0) {
			log.debug("no schedule");
			return;
		}

		datas = new Vector<SMIValue>();
		for (int i = 0; i < args.length; i++) {
			log.debug("Schedule_Name: " + args[i][0]);
			log.debug("Schedule_Suspend: " + args[i][1]);
			log.debug("Schedule_Condition: " + args[i][2]);
			log.debug("Schedule_Task: " + args[i][3]);
			datas.add(DataUtil.getSMIValueByObject(target.getNameSpace(), "stringEntry", args[i][0]));
			if (args[i][1] != null && !"0".equals(args[i][1])) {
				datas.add(DataUtil.getSMIValueByObject(target.getNameSpace(), "boolEntry", "1"));
			} else {
				datas.add(DataUtil.getSMIValueByObject(target.getNameSpace(), "boolEntry", "0"));
			}
			datas.add(DataUtil.getSMIValueByObject(target.getNameSpace(), "stringEntry", args[i][2]));
			datas.add(DataUtil.getSMIValueByObject(target.getNameSpace(), "stringEntry", args[i][3]));
		}

		Object[] params = new Object[] { target, "cmdSetSchedule", datas };

		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector" };

		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}
	}
	
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
	@Override
	public void cmdMcuDeleteSchedule(String mcuId, String scheduleName) throws FMPMcuException, Exception {
		log.debug("cmdMcuSetSchedule(" + mcuId + ") schedule name(" + scheduleName + ")");

		Target target = CmdUtil.getTarget(mcuId);
		Vector<SMIValue> datas = null;

		datas = new Vector<SMIValue>();
		datas.add(DataUtil.getSMIValueByObject(target.getNameSpace(), "stringEntry", scheduleName));
		Object[] params = new Object[] { target, "cmdDeleteSchedule", datas };
		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector" };
		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}
	}
	
	/**
	 * MCU Execute Schedule
	 *
	 * @param mcuId
	 *            MCU ID
	 * @param args
	 *            Execute Schedule 
	 * @throws FMPMcuException,
	 *             Exception
	 */
	@Override
	public void cmdMcuExecuteSchedule(String mcuId, String scheduleName) throws FMPMcuException, Exception {
		log.debug("cmdMcuExecuteSchedule(" + mcuId + ") schedule name(" + scheduleName + ")");

		Target target = CmdUtil.getTarget(mcuId);
		Vector<SMIValue> datas = null;

		datas = new Vector<SMIValue>();
		datas.add(DataUtil.getSMIValueByObject(target.getNameSpace(), "stringEntry", scheduleName));
		Object[] params = new Object[] { target, "cmdExecuteSchedule", datas };
		String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector" };
		Object obj = null;
		try {
			obj = invoke(params, types);
		} catch (Exception e) {
			log.error(e, e);
			throw new Exception(makeMessage(e.getMessage()));
		}

		if (obj instanceof Integer) {
			log.error("Error Code Return");
			throw makeMcuException(((Integer) obj).intValue());
		}
	}

    @Override
    public meterDataEntry2[] cmdGetMeterList(String mcuId, int nOption, String[] meterId)
            throws FMPMcuException, Exception {
        StringBuffer buffer = new StringBuffer();
        buffer.append("cmdGetMeterList(").append(mcuId).append(" , ");
        buffer.append(nOption).append(" , ");
        for (int i = 0; i < meterId.length; i++) {
            if (i == meterId.length - 1)
                buffer.append(meterId[i]).append(")");
            else
                buffer.append(meterId[i]).append(" , ");
        }

        log.debug(buffer.toString());

        Target target = CmdUtil.getTarget(mcuId);

        Vector<SMIValue> datas = new Vector<SMIValue>();
        SMIValue smiValue = null;

        for (int i = 0; i < meterId.length; i++) {
            smiValue = DataUtil.getSMIValue(new INT(nOption));
            datas.add(smiValue);
            //smiValue = DataUtil.getSMIValue(new STRING(meterId[i]));
            smiValue = DataUtil.getSMIValueByObject("stringEntry", meterId[i]);
            datas.add(smiValue);
        }

        Object[] params = new Object[] { target, "cmdGetMeterList", datas };

        String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector" };

        Object obj = null;
        try {
            obj = invoke(params, types);
        } catch (Exception e) {
            log.error(e, e);
            throw new Exception(makeMessage(e.getMessage()));
        }

        SMIValue[] smiValues = null;

        if (obj instanceof Integer) {
            log.error("Error Code Return : " + obj);
            throw makeMcuException(((Integer) obj).intValue());
        } else if (obj instanceof SMIValue[]) {
            log.info(" Get SMI Value ");
            smiValues = (SMIValue[]) obj;
        } else {
            log.error("Unknown Return Value");
            throw new Exception("Unknown Return Value");
        }

        log.debug("return value count [" + smiValues.length + "]");
        meterDataEntry2[] value = new meterDataEntry2[smiValues.length];
        
        try {
            for (int i = 0; i < smiValues.length; i++) {
                try {
                    OPAQUE mdv = (OPAQUE) smiValues[i].getVariable();
                    log.debug("Get Meter : return Class Name = " + mdv.getClsName()+" , value = "+mdv.toString());

                    if (mdv.getValue() instanceof meterDataEntry2) {
                        value[i] = (meterDataEntry2)mdv.getValue();
                    }
                } catch (Exception ex) {
                    log.error(ex, ex);
                }
            }

        } catch (Exception e) {
            log.error(e,e);
        }

        return value;
    }

    @Override
    public Hashtable cmdReqNodeUpgradeEVN(String mcuId, int upgradeType, int controlCode, String imageKey,
            String imageUrl, String checkSum, int[] filter, String[] filterValue) throws FMPMcuException, Exception {
        Target target = CmdUtil.getTarget(mcuId);
        
        StringBuffer buf = new StringBuffer();
        buf.append("MCUID[" + mcuId + " imageKey "+imageKey+"]\n " +
                "fileUrl[" + imageUrl + "]\n " +
                "upgradeCheckSum[" + checkSum + "]" + " targetId[" + target.getTargetId() +"]");
        
        log.debug(buf.toString());
        
        Vector<SMIValue> datas = new Vector<SMIValue>();
     
        datas.add(DataUtil.getSMIValue(new BYTE(upgradeType)));                
        datas.add(DataUtil.getSMIValue(new UINT(controlCode))); 
        datas.add(DataUtil.getSMIValueByObject("stringEntry", imageKey));
        datas.add(DataUtil.getSMIValueByObject("stringEntry", imageUrl));
        datas.add(DataUtil.getSMIValueByObject("stringEntry", checkSum));
        
        if(filter == null || filter.length < 1 || filterValue.length < 1) {
            log.error("Invalid filter Data");
            throw new Exception("Invalid filter Data");
        }           

        for(int i=0; i<filter.length; i++) {
           datas.add(DataUtil.getSMIValue(new INT(filter[i])));
           datas.add(DataUtil.getSMIValueByObject("stringEntry", filterValue[i])); 
        }        
        
        Object[] params = 
               new Object[] 
               { 
                   target, 
                   "cmdReqNodeUpgrade", 
                   datas 
               };
        
        String[] types = 
                new String[] 
                { 
                   TARGET_SRC, 
                   "java.lang.String",
                   "java.util.Vector" 
                };
        
        Object obj = null;
        try {
            obj = invoke(params, types);            
        } catch (Exception e) {
            log.debug("cmdReqNodeUpgrade ERROR : "+e);
            log.error(e, e);
            throw new Exception(makeMessage(e.getMessage()));
        } 
        
        SMIValue[] smiValues = null;
        
        if (obj instanceof Integer) {
            throw makeMcuException(((Integer) obj).intValue());            
        }
        else if (obj instanceof SMIValue[])
        {
            smiValues = (SMIValue[]) obj;
        }
       
        Hashtable res = null;
        if (smiValues != null && smiValues.length != 0)
        {
            res = CmdUtil.convSMItoMOP(smiValues[0]);
        }
        else
        {
            log.debug("smiValues is null");
        }

        return res;
    }

    @Override
    public void cmdCtrlUpgradeRequestEVN(String mcuId, int requestId, int opCode) throws FMPMcuException, Exception {
        Target target = CmdUtil.getTarget(mcuId);
        
        StringBuffer buf = new StringBuffer();
        buf.append("MCUID[" + mcuId + " requestId "+requestId+"]\n " +
                   "opCode[" + opCode + "]");
           
        log.debug(buf.toString());
           
        Vector<SMIValue> datas = new Vector<SMIValue>();
                           
        datas.add(DataUtil.getSMIValue(new UINT(requestId))); 
        datas.add(DataUtil.getSMIValue(new BYTE(opCode)));
        
        Object[] params = 
               new Object[] 
                { 
                   target, 
                   "cmdCtrlUpgradeRequest", 
                   datas 
                };
        
        String[] types = 
               new String[] 
                { 
                   TARGET_SRC, 
                   "java.lang.String",
                   "java.util.Vector", 
                };
        

        Object obj = null;
        try
        {
            obj = invoke(params, types);
        }
        catch (Exception e)
        {
            log.error(e,e);
            throw new Exception(makeMessage(e.getMessage()));
        }

        if (obj instanceof Integer)
        {
            log.error("Error Code Return");

            throw makeMcuException(((Integer) obj).intValue());
        }
    }

    @Override
    public Hashtable cmdCreateTunnel(String mcuId, String meterId, int msgTimeout, int tunnelTimeout)
            throws FMPMcuException, Exception {
        log.debug("cmdCreateTunnel("+mcuId+"," + meterId +"," + msgTimeout + "," + tunnelTimeout+ ")");
        
        Target target = CmdUtil.getTarget(mcuId);
         
         Vector<SMIValue> datas = new Vector<SMIValue>();
         
         datas.add(DataUtil.getSMIValueByObject("stringEntry", meterId));         
         datas.add(DataUtil.getSMIValue(new INT(msgTimeout))); 
         datas.add(DataUtil.getSMIValue(new INT(tunnelTimeout)));
         
         Object[] params = 
                new Object[] 
                { 
                    target, 
                    "cmdCreateTunnel", 
                    datas 
                };
          
          String[] types = 
                 new String[] 
                 { 
                    TARGET_SRC, 
                    "java.lang.String",
                    "java.util.Vector" 
                 };
          
          Object obj = null;
          try {
              obj = invoke(params, types);
          } catch (Exception e) {
              log.error(e, e);
              throw new Exception(makeMessage(e.getMessage()));
          }

          SMIValue[] smiValues = null;
          
          if (obj instanceof Integer) {
              throw makeMcuException(((Integer) obj).intValue());           
          }
          else if (obj instanceof SMIValue[])
          {
              smiValues = (SMIValue[]) obj;
          }
        
          Hashtable res = null;
          if (smiValues != null && smiValues.length != 0)
          {
              res = CmdUtil.convSMItoMOP(smiValues[0]);
          }
          else
          {
              log.debug("smiValues is null");
          }

          return res;
    }

    @Override
    public Hashtable cmdConnectByPass(String mcuId, int portNumber, String[] param) throws FMPMcuException, Exception {
        Target target = CmdUtil.getTarget(mcuId);
        target.setPort(portNumber);
        target.setProtocol(Protocol.BYPASS);
        
        log.debug("cmdConnectByPass("+portNumber + ")");
        log.debug("mcuId("+mcuId + ")");
        log.debug("targetId("+target.getTargetId() + ")");
        
        HashMap<String, String> map = new HashMap<String, String>();
         Object obj = null;
        
        try
        {   
            for(int i=0; i<param.length; i++) 
            {
                String[] split = param[i].split(",");
                map.put(split[0], split[1]);
            }
            
            obj = invokeBypass(target, CmdUtil.getTarget(mcuId), map);          
                        
        } catch(Exception ex) {
            log.error(ex,ex);
            throw new Exception(makeMessage(ex.getMessage()));
        }
        
        SMIValue[] smiValues = null;
        if (obj instanceof Integer)
        {
            log.error("Error Code Return");
            throw makeMcuException(((Integer) obj).intValue());
        }
        else if (obj instanceof SMIValue[])
        {
            smiValues = (SMIValue[]) obj;
        }
       
        Hashtable res = null;
        if (smiValues != null && smiValues.length != 0)
        {
            res = CmdUtil.convSMItoMOP(smiValues[0]);
        }
        else
        {
            log.debug("smiValues is null");
        }

        return res;
    }

    @Override
    public void cmdDeleteTunnel(String mcuId, String meterId) throws FMPMcuException, Exception {
        Target target = CmdUtil.getTarget(mcuId);
        
        log.debug("cmdDeleteTunnel("+mcuId+"," + meterId+ ")");
        
        Vector<SMIValue> datas = new Vector<SMIValue>();
        
       datas.add(DataUtil.getSMIValueByObject("stringEntry", meterId));
       
       Object[] params = 
               new Object[] 
               { 
                   target, 
                   "cmdDeleteTunnel", 
                   datas 
               };
         
         String[] types = 
                new String[] 
                { 
                   TARGET_SRC, 
                   "java.lang.String",
                   "java.util.Vector" 
                };
         
         Object obj = null;
         try
         {
             obj = invoke(params, types);
         }
         catch (Exception e)
         {
             log.error(e,e);
             throw new Exception(makeMessage(e.getMessage()));
         }

         if (obj instanceof Integer)
         {
             log.error("Error Code Return");
             throw makeMcuException(((Integer) obj).intValue());
         }
    }

    @Override
    public modemG3Entry[] cmdGetManagedG3ModemList(String mcuId, int nOption, String[] modemId)
            throws FMPMcuException, Exception {
        StringBuffer buffer = new StringBuffer();
        buffer.append("cmdGetMeterList(").append(mcuId).append(" , ");
        buffer.append(nOption).append(" , ");
        for (int i = 0; i < modemId.length; i++) {
            if (i == modemId.length - 1)
                buffer.append(modemId[i]).append(")");
            else
                buffer.append(modemId[i]).append(" , ");
        }
        
        log.debug(buffer.toString());
        
        Target target = CmdUtil.getTarget(mcuId);

        Vector<SMIValue> datas = new Vector<SMIValue>();
        SMIValue smiValue = null;

        for (int i = 0; i < modemId.length; i++) {
            smiValue = DataUtil.getSMIValue(new INT(nOption));
            datas.add(smiValue);
            smiValue = DataUtil.getSMIValueByObject("stringEntry", modemId[i]);
            datas.add(smiValue);
        }
        
        Object[] params = new Object[] { target, "cmdGetManagedG3ModemList", datas };

        String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector" };
        
        Object obj = null;
        try {
            obj = invoke(params, types);
        } catch (Exception e) {
            log.error(e, e);
            throw new Exception(makeMessage(e.getMessage()));
        }

        SMIValue[] smiValues = null;
        
        if (obj instanceof Integer) {
            log.error("Error Code Return : " + obj);
            throw makeMcuException(((Integer) obj).intValue());
        } else if (obj instanceof SMIValue[]) {
            log.info(" Get SMI Value ");
            smiValues = (SMIValue[]) obj;
        } else {
            log.error("Unknown Return Value");
            throw new Exception("Unknown Return Value");
        }
        
        log.debug("return value count [" + smiValues.length + "]");
        modemG3Entry[] value = new modemG3Entry[smiValues.length];
        
        try {
            for (int i = 0; i < smiValues.length; i++) {
                OPAQUE mdv = (OPAQUE) smiValues[i].getVariable();
                log.debug("Get G3Modem : return Class Name = " + mdv.getClsName()+" , value = "+mdv.toString());
                
                if (mdv.getValue() instanceof modemG3Entry) {
                    value[i] = (modemG3Entry)mdv.getValue();
                }
            }
        }catch (Exception e) {
            log.error(e,e);
        }
        
        return value;
    }

    @Override
    public boolean cmdTCPTrigger(String cmd, String ipAddr) throws Exception {
        log.info("[cmdTCPTrigger] Source Command: " + cmd + " IP: " + ipAddr);
        boolean isConnect = false;
        
        TCPTriggerClient tcpTriggerClient = new TCPTriggerClient();
        isConnect = tcpTriggerClient.cmdTCPTrigger(cmd, ipAddr);
        return isConnect;
    }
    
    // INSERT START SP-758
	/**
 	 * @param modemId array of deviceserial
 	 * @return
 	 * @throws FMPMcuException
 	 * @throws Exception
 	 */	
	public Map<String, Object> cmdExecDmdNiCommandMulti(String requestType, String attributeId, String param, String[] modemIds) throws Exception {
		Map<String,Object> result = new HashMap<String,Object>();
		String resultString = "";
 		Target target = null;

		String command = "";
 		if(requestType.equals("GET")){ 	
 			command = "cmdDmdNiGet";
 		}
 		else {
 			command = "cmdDmdNiSet";
 		}
 		
 		StringBuffer modemsBuff = new StringBuffer();
 		for ( int i = 0; i < modemIds.length; i++){
 			if ( modemsBuff.length() == 0 )
 				modemsBuff.append(",");
 			modemsBuff.append(modemIds[i]+",");
 		}
 		
		log.debug("cmdExecDmdNiCommandMulti ["+requestType+"],["+ attributeId +"],[" + param +"],["+ modemsBuff.toString() + "]");

 		int responseTimeout = Integer.valueOf(FMPProperty.getProperty("protocol.dtls.response.timeout","180"));		
 		try {
 			Modem modem = modemDao.get(modemIds[0]);
			target = CmdUtil.getTarget(modem);
			if (!"SP".equals(target.getNameSpace())) {
				log.error(target.getNameSpace() + " is not supported.");
				throw new Exception(target.getNameSpace() + " is not supported.");
			}
 			
			Vector<SMIValue> datas = new Vector<SMIValue>();
			SMIValue smiValue = null;

			// timeout
			smiValue = DataUtil.getSMIValue(target.getNameSpace(), new UINT(responseTimeout));
			datas.add(smiValue);
			
			// Attribute count
			smiValue = DataUtil.getSMIValue(target.getNameSpace(), new BYTE(0x01));
			datas.add(smiValue);
			
			// Attribute ID (□□□□)
			byte[] hexid = Hex.encode(attributeId);
			smiValue = DataUtil.getSMIValue(target.getNameSpace(), new WORD(hexid)); 				
			//smiValue = DataUtil.getSMIValue(target.getNameSpace(), new STREAM(hexid)); 				
			datas.add(smiValue);
			
			// Attribute params
			byte[] hexparam = Hex.encode(param);                			
 			smiValue = DataUtil.getSMIValue(target.getNameSpace(), new STREAM(hexparam));
 			datas.add(smiValue);

 			// Filters
			for ( int i = 0; i < modemIds.length; i++ ){					
				smiValue = DataUtil.getSMIValue(target.getNameSpace(), new INT(0x02));	// FILTER_MODEMID
				datas.add(smiValue);
				smiValue = DataUtil.getSMIValueByObject(target.getNameSpace(), "stringEntry", modemIds[i]); // modemID
				datas.add(smiValue);	
			}		 			

			Object[] params = new Object[] { target, command, datas };
 			String[] types = new String[] { TARGET_SRC, "java.lang.String", "java.util.Vector", };
 			Object obj = null;

 			obj = invoke(params, types);
 			SMIValue[] smiValues = null;

 			if (obj instanceof Integer) {
 				log.error("Error Code Return");
 				throw makeMcuException(((Integer) obj).intValue());
 			} else if (obj instanceof SMIValue[]) {
 				smiValues = (SMIValue[]) obj;
 				if (smiValues.length > 0) {
 					log.debug("smiValues.length = " + smiValues.length);
 				} else {
 					log.error("smiValue size 0");
 					throw new FMPMcuException("no value exist");
 				}
 			} else {
 				log.error("Unknown Return Value");
 				throw new FMPMcuException("Unknown Return Value");
 			}


 			if (smiValues.length < 3) {
				log.error("Execute command failed.Result length less than 3. ");
				throw new FMPMcuException("Execute command failed"); 				
 			}
 			
 			for ( int i = 0; i < smiValues.length / 3; i++ ){
 				boolean error = false;
 				String modemId = null;
 				String meterId = null;
 				Map map = new HashMap<String, String>();

 				// get data by each modem
 				for ( int j = 0; j < 3; j++){
 					if ( i*3+j >= smiValues.length){
 						log.error("invalid simValue size( not Multiples of 3 )");
 		 				throw new FMPMcuException("Execute command failed");
 					}
 					smiValue = smiValues[i*3+j];
 					log.debug("SMIValue Return Class=" + smiValue.getVariable().getClass().getName() + ", smiValue[" + i*3+j + "] = " + smiValue.toString());
 					
 					if ( j == 0 ){ 
 						modemId = smiValue.getVariable().toString();
 						boolean found = false;
 						for ( int l = 0; l < modemIds.length; l++){
 							if( modemId.equals(modemIds[l])){
 								map.put("ModemId", modemId);
 								found = true;
 								break;
 							}
 						}
 						if ( !found ){
 							log.debug("Modem id is different. request[" + modemsBuff.toString() + 
 									"] result[" + smiValue.getVariable().toString() + "]" );
 						}
 					}
 					else if (j==1) {
 						if  (Integer.parseInt(smiValue.getVariable().toString()) != ErrorCode.IF4ERR_NOERROR ){
 							log.debug("Return error code. [" + smiValue.getVariable().toString() + "]");
 							error = true;
 						}
 					}
 					else if (j==2) {
 						if ( !error ){
 							byte[] res = ((OCTET) smiValue.getVariable()).getValue();
 							if (res == null) {
 								log.debug("Result Stream Data is NULL.");
 								resultString = "Execution OK. Result data is null.";
 							} else {						
 								resultString = "Execution OK";
 								int attrCount = DataFormat.hex2dec(res, 0, 2);
 								int offset = 2;
 								log.debug("Attribute Id Count [" + attrCount + "]");
 								map.put("AttributeIdCount", String.valueOf(attrCount));
 								for(int n=0; n < attrCount; n++) {
 									String attrDataID = Hex.decode(DataFormat.select(res, offset, 2));
 									offset = offset + 2;
 									int attrDataLen = DataFormat.hex2dec(res, offset, 2);
 									offset = offset + 2;
 									byte[] attrDataVal = DataFormat.select(res, offset, attrDataLen);
 									offset = offset + attrDataLen;
 									log.debug("Attribute Data [" + n+1 + 
 											"] : AttributeId[" + attrDataID + 
 											"] : attrDataVal[" + Hex.decode(attrDataVal) +
 											"] : Length[" + attrDataLen + 
 											"]");
 									
 									JSONObject jo = new JSONObject();
 									jo.put("AttributeId", attrDataID);
 									jo.put("Length", String.valueOf(attrDataLen));
 									jo.put("Value", Hex.decode(attrDataVal));
 									
 									map.put("AttributeData#"+(n+1), jo.toString());	 							
 								}						
 							
 							}
 						}
 					}
 				}// for ( int j = 0; j < 3; j++){
 				
 				if ( map.size() > 0 ){
 					result.put(modemId,(Object)map);
 				}
 				else {
 					log.debug("result map is none.");
 				}
 			}// for ( int i = 0; i < smiValues.length / 3; i++ 			 			
 											
 		} catch (Exception e) {
 			log.error(e, e);
 			resultString="Execution failed.";
 		}

		// return map 
 		result.put("cmdDesc", resultString);
 		
 		if ( result.size() > 0 ){
 			result.put("RESULT", true);
 		}
 		else {
 			result.put("RESULT", false);
 		}	 		
 		
 		
		return result;
	}


	
	@Override
	public Hashtable cmdLineModemEVN(String cmd, String modemId, String[] param)
			throws FMPMcuException, Exception {
		
		log.info("[CmdLineModemEVN] Source Command: " + cmd + " modemId: " + modemId);
		Target target = null;
		JpaTransactionManager txManager = null;
        TransactionStatus txStatus = null;
        Modem modem = null;
        
        HashMap<String, String> map = new HashMap<String, String>();
		Object obj = null;
		 
        try {
        	
        	map.put("cmd", cmd);
        	for(int i=0; i<param.length; i++) 
			{
				String[] split = param[i].split(",");
				map.put(split[0], split[1]);
			}
        	
        	txmanager = (JpaTransactionManager) DataUtil.getBean("transactionManager");

            txStatus = txManager.getTransaction(null);		
			modem = modemDao.get(Integer.parseInt(modemId)); 
	        target = CmdUtil.getTarget(modem); 
	        target.setProtocol(Protocol.BYPASS);
	        target.setTargetId("0");
	        target.setTargetType(McuType.UNKNOWN);
	        
	        commandProxy = new CommandProxy();
	    	obj = commandProxy.executeByPass(target, null, map);
        }
        catch (Exception e) {
            if (txManager != null)
                txManager.rollback(txStatus);
            throw e;
        }
        
        SMIValue[] smiValues = null;

        if (obj instanceof Integer)
        {
            log.error("Error Code Return");
            throw makeMcuException(((Integer) obj).intValue());
        }
        else if (obj instanceof SMIValue[])
        {
            smiValues = (SMIValue[]) obj;
        }
        else
        {
            log.error("Unknown Return Value");
            throw new Exception("Unknown Return Value");
        }

        Hashtable res = new Hashtable();
        if (smiValues != null && smiValues.length != 0)
        {
            for (int i = 0; i < smiValues.length; i++)
            {
                res.putAll(CmdUtil.convSMItoMOP(smiValues[i]));
            }
        }
        else
        {
            log.debug("smiValues is null");
        }

        return res;
	}		
	
	
	// INSERT END SP-758
	
}
