package com.aimir.fep.protocol.mrp.client.sms;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.McuType;
import com.aimir.constants.CommonConstants.MeterCommand;
import com.aimir.constants.CommonConstants.OperatorType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.constants.CommonConstants.TR_OPTION;
import com.aimir.constants.CommonConstants.TR_STATE;
import com.aimir.dao.device.AsyncCommandLogDao;
import com.aimir.dao.device.AsyncCommandParamDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.device.OperationListDao;
import com.aimir.fep.bypass.Bypass;
import com.aimir.fep.protocol.fmp.client.Client;
import com.aimir.fep.protocol.fmp.client.FMPClientResource;
import com.aimir.fep.protocol.fmp.client.FMPClientResourceFactory;
import com.aimir.fep.protocol.fmp.client.gprs.TCPTriggerClient;
import com.aimir.fep.protocol.fmp.client.resource.ts.TerminalServerPort;
import com.aimir.fep.protocol.fmp.common.CircuitTarget;
import com.aimir.fep.protocol.fmp.common.SMSTarget;
import com.aimir.fep.protocol.fmp.common.Target;
import com.aimir.fep.protocol.fmp.datatype.BYTE;
import com.aimir.fep.protocol.fmp.datatype.HEX;
import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.protocol.fmp.datatype.SMIValue;
import com.aimir.fep.protocol.fmp.datatype.UINT;
import com.aimir.fep.protocol.fmp.datatype.WORD;
import com.aimir.fep.protocol.fmp.frame.ControlDataFrame;
import com.aimir.fep.protocol.fmp.frame.ErrorCode;
import com.aimir.fep.protocol.fmp.frame.GeneralDataConstants;
import com.aimir.fep.protocol.fmp.frame.GeneralDataFrame;
import com.aimir.fep.protocol.fmp.frame.ServiceDataConstants;
import com.aimir.fep.protocol.fmp.frame.ServiceDataFrame;
import com.aimir.fep.protocol.fmp.frame.service.AlarmData;
import com.aimir.fep.protocol.fmp.frame.service.CommandData;
import com.aimir.fep.protocol.fmp.frame.service.EventData;
import com.aimir.fep.protocol.fmp.frame.service.MDData;
import com.aimir.fep.protocol.fmp.frame.service.RMDData;
import com.aimir.fep.protocol.fmp.frame.service.ServiceData;
import com.aimir.fep.protocol.fmp.processor.ProcessorHandler;
import com.aimir.fep.protocol.mrp.protocol.SMS_Handler;
import com.aimir.fep.protocol.mrp.client.SMSClientProtocolHandler;
import com.aimir.fep.protocol.mrp.client.SMSClientProtocolProvider;
import com.aimir.fep.protocol.mrp.command.frame.sms.RequestFrame;
import com.aimir.fep.protocol.mrp.exception.MRPError;
import com.aimir.fep.protocol.mrp.exception.MRPException;
import com.aimir.fep.util.ByteArray;
import com.aimir.fep.util.CmdUtil;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.FrameUtil;
import com.aimir.fep.util.Hex;
import com.aimir.fep.util.MIBUtil;
import com.aimir.model.device.AsyncCommandLog;
import com.aimir.model.device.AsyncCommandParam;
import com.aimir.model.device.CommLog;
import com.aimir.model.device.MMIU;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.device.OperationList;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.serial.SerialAddress;
import org.apache.mina.transport.serial.SerialConnector;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionStatus;

/**
 * MMIU SMS Packet Client
 * 
 * The solution is to ensure that you have the correct RXTX library files
 * installed in the correct folders. There are two files: rxtxComm.jar and
 * rxtxSerial.dll, which should be installed as follows:
 * 
 * C:\Program Files\Java\jre6\bin\rxtxSerial.dll C:\Program
 * Files\Java\jre6\lib\ext\RXTXComm.jar
 * 
 * Many 64-bit Windows machines have both 32-bit and 64-bit browsers and 32-bit
 * and 64-bit Java. On such machines, the 64-bit libraries should be installed
 * as shown above. The correct path for the 32-bit RXTX libraries is as follows:
 * 
 * C:\Program Files(x86)\Java\jre6\bin\rxtxSerial.dll C:\Program
 * Files(x86)\Java\jre6\lib\ext\RXTXComm.jar
 * 
 * The installer package on the Machine Science web site attempts to detect
 * whether you have a 32-bit or a 64-bit machine, and install the files as
 * needed. If this fails, the solution is to download and install the files
 * manually.
 * 
 * The 32-bit RXTX files are here:
 * 
 * rxtxSerial.dll RXTXComm.jar
 * 
 * The 64-bit RXTX files are here:
 * 
 * rxtxSerial.dll RXTXComm.jar
 * 
 * @author Y.K Park (goodjob@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2008-11-06 10:30:15 +0900 $,
 */
public class SMSMMIUClient implements Client {
	private ModemDao modemDao = DataUtil.getBean(ModemDao.class);
	private OperationListDao operationListDao = DataUtil.getBean(OperationListDao.class);
	private AsyncCommandLogDao asyncCommandLogDao = DataUtil.getBean(AsyncCommandLogDao.class);
	private AsyncCommandParamDao asyncCommandParamDao = DataUtil.getBean(AsyncCommandParamDao.class);
	private Log log = LogFactory.getLog(SMSMMIUClient.class);
	private SMSTarget target = null;
	private Boolean isSmsTerminal = null;
	private String protocolVersion = "";
	private int CONNECT_TIMEOUT = Integer.parseInt(FMPProperty.getProperty("protocol.connection.timeout", 30 + "")); // seconds
	private IoConnector connector = null;
	private SMSClientProtocolProvider provider = null;
	private IoSession session = null;
	private long MAX_MCUID = 4294967295L;
	private ProcessorHandler logProcessor = null;
	private Integer activatorType = new Integer(FMPProperty.getProperty("protocol.system.FEP", "1"));
	private Integer targetType = new Integer(FMPProperty.getProperty("protocol.system.MCU", "2"));
	private Integer protocolType = new Integer(FMPProperty.getProperty("protocol.type.SMS", CommonConstants.getProtocolCode(Protocol.SMS) + ""));

	/**
	 * constructor
	 */
	public SMSMMIUClient() {
	}

	/**
	 * constructor
	 * 
	 * @param target
	 *            <code>SMSTarget</code> target
	 */
	public SMSMMIUClient(SMSTarget target) {
		this.target = target;
	}

	public synchronized void connect(boolean meterConnect) throws Exception {
		connect();
	}

	/**
	 * connect to Target
	 *
	 * @throws Exception
	 */
	@SuppressWarnings("static-access")
	private synchronized void connect() throws Exception {
		int connMode = FMPClientResource.TERMINAL_SERVER;
		try {
			connMode = Integer.parseInt(FMPProperty.getProperty("protocol.circuit.connection.mode", FMPClientResource.TERMINAL_SERVER + ""));
		} catch (Exception ex) {
			throw new Exception("Can not find Circuit Connection Mode");
		}

		log.debug("SMSClient Circuit Connection Mode[" + connMode + "]");

		if (connMode == FMPClientResource.TERMINAL_SERVER) {
			FMPClientResource resource = FMPClientResourceFactory.getResource(connMode);
			tsconnect(resource);
		} else if (connMode == FMPClientResource.SERIAL_PORT) {
			serialconnect();
		} else {
			throw new Exception(" Not Support Curcuit Connection Mode[" + connMode + "]");
		}
	}

	private synchronized void tsconnect(FMPClientResource resource) throws Exception {
		connector = new NioSocketConnector();
		provider = new SMSClientProtocolProvider();
		if (!connector.getFilterChain().contains(getClass().getName()))
			connector.getFilterChain().addLast(getClass().getName(), new ProtocolCodecFilter(provider.getCodecFactory()));
		connector.setHandler(provider.getHandler());

		SocketAddress socketAddr = null;
		String ipaddr = null;
		int port = 1;

		Object resourceObj = null;

		log.debug("##### SMSClient TS : Acquire ###### locId=" + target.getLocation());
		if (target.getLocation() != null && !"".equals(target.getLocation().getName())) {
			resourceObj = resource.acquire(target.getLocation().getId());
		} else {
			resourceObj = resource.acquire();
		}
		TerminalServerPort tsport = (TerminalServerPort) resourceObj;
		ipaddr = tsport.getIpAddr();
		port = tsport.getPort();

		socketAddr = new InetSocketAddress(ipaddr, port);
		log.debug("SMSClient TS : ipaddr[" + ipaddr + "] port[" + port + "]");
		log.debug("SMSClient: " + resource.getActiveResourcesString());
		log.debug("SMSClient: " + resource.getIdleResourcesString());

		int rty = 0;
		for (;;) {
			rty++;
			try {
				connector.setConnectTimeoutMillis(CONNECT_TIMEOUT * 1000);
				ConnectFuture future = connector.connect(socketAddr);
				future.awaitUninterruptibly();

				if (!future.isConnected()) {
					throw new Exception("not yet");
				}

				session = future.getSession();
				log.debug("SESSION CONNECTED[" + session.isConnected() + "]");

				break;
			} catch (Exception e) {
				if (rty < 2) {
					try {
						Thread.currentThread().sleep(5 * 1000);
					} catch (Exception ignore) {
					}
					continue;
				}
				if (resource != null && resourceObj != null) {
					try {
						resource.release(resourceObj);
						log.debug("SMSClient: " + resource.getActiveResourcesString());
						log.debug("SMSClient: " + resource.getIdleResourcesString());
					} catch (Exception ignoreEx) {
					}
				}
				log.error("Failed to connect. host[" + ipaddr + "] port[" + port + "]", e);
				throw e;
			}
		}

		if (session == null)
			throw new Exception("Failed to connect. host[" + target.getIpaddr() + "] port[" + target.getPort() + "]");

		SMSClientProtocolHandler handler = (SMSClientProtocolHandler) session.getHandler();
		try {
			handler.setMRPResource(resource, resourceObj);
			handler.setResponseTimeout(target.getTimeout());
			log.debug("*******   session :" + session);
			log.debug("******* (CircuitTarget)target :" + ((CircuitTarget) target).getPhoneNumber());
			handler.initSMS(session, (CircuitTarget) target);
			log.debug("SMSClient initCircuit End");

		} catch (Exception ex) {
			log.error(ex, ex);
			close();
			throw ex;
		}
	}

	private synchronized void serialconnect() throws Exception {
		log.debug("Serial Mode!!");
		connector = new SerialConnector();
		provider = new SMSClientProtocolProvider();
		if (!connector.getFilterChain().contains(getClass().getName()))
			connector.getFilterChain().addLast(getClass().getName(), new ProtocolCodecFilter(provider.getCodecFactory()));
		connector.setHandler(provider.getHandler());

		SerialAddress serialAddress = null;
		//name
		String name = FMPProperty.getProperty("protocol.serial.deviceName");
		//bauds
		int bauds = Integer.parseInt(FMPProperty.getProperty("protocol.serial.bauds"));

		//DataBits
		SerialAddress.DataBits dataBits = SerialAddress.DataBits.DATABITS_8;
		if (FMPProperty.getProperty("protocol.serial.databits") == null || FMPProperty.getProperty("protocol.serial.databits").length() == 0) {
			dataBits = SerialAddress.DataBits.DATABITS_8;
		} else if (Integer.parseInt(FMPProperty.getProperty("protocol.serial.databits")) == 5) {
			dataBits = SerialAddress.DataBits.DATABITS_5;
		} else if (Integer.parseInt(FMPProperty.getProperty("protocol.serial.databits")) == 6) {
			dataBits = SerialAddress.DataBits.DATABITS_6;
		} else if (Integer.parseInt(FMPProperty.getProperty("protocol.serial.databits")) == 7) {
			dataBits = SerialAddress.DataBits.DATABITS_7;
		} else if (Integer.parseInt(FMPProperty.getProperty("protocol.serial.databits")) == 8) {
			dataBits = SerialAddress.DataBits.DATABITS_8;
		} else {
			throw new Exception("Please Check Serial DataBits or Configuration");
		}

		//StopBits
		SerialAddress.StopBits stopBits = SerialAddress.StopBits.BITS_1;
		if (FMPProperty.getProperty("protocol.serial.stopbits") == null || FMPProperty.getProperty("protocol.serial.stopbits").length() == 0) {
			stopBits = SerialAddress.StopBits.BITS_1;
		} else if (Integer.parseInt(FMPProperty.getProperty("protocol.serial.stopbits")) == 5) {
			stopBits = SerialAddress.StopBits.BITS_1_5;
		} else if (Integer.parseInt(FMPProperty.getProperty("protocol.serial.stopbits")) == 1) {
			stopBits = SerialAddress.StopBits.BITS_1;
		} else if (Integer.parseInt(FMPProperty.getProperty("protocol.serial.stopbits")) == 2) {
			stopBits = SerialAddress.StopBits.BITS_2;
		} else {
			throw new Exception("Please Check Serial StopBits or Configuration");
		}

		//parity
		SerialAddress.Parity parity = SerialAddress.Parity.NONE;
		if (FMPProperty.getProperty("protocol.serial.parity") == null || FMPProperty.getProperty("protocol.serial.parity").length() == 0) {
			parity = SerialAddress.Parity.NONE;
		} else if ("even".equals(FMPProperty.getProperty("protocol.serial.parity"))) {
			parity = SerialAddress.Parity.EVEN;
		} else if ("mark".equals(FMPProperty.getProperty("protocol.serial.parity"))) {
			parity = SerialAddress.Parity.MARK;
		} else if ("none".equals(FMPProperty.getProperty("protocol.serial.parity"))) {
			parity = SerialAddress.Parity.NONE;
		} else if ("odd".equals(FMPProperty.getProperty("protocol.serial.parity"))) {
			parity = SerialAddress.Parity.ODD;
		} else {
			throw new Exception("Please Check Serial parity or Configuration");
		}

		//flow
		SerialAddress.FlowControl flow = SerialAddress.FlowControl.NONE;
		if (FMPProperty.getProperty("protocol.serial.flow") == null || FMPProperty.getProperty("protocol.serial.flow").length() == 0) {
			flow = SerialAddress.FlowControl.NONE;
		} else if ("none".equals(FMPProperty.getProperty("protocol.serial.flow"))) {
			flow = SerialAddress.FlowControl.NONE;
		} else {
			throw new Exception("Please Check Serial Flow Control or Configuration");
		}
		serialAddress = new SerialAddress(name, bauds, dataBits, stopBits, parity, flow);

		log.info("SerialAddress dev[" + name + "] baud[" + bauds + "] databit[" + dataBits + "] stopbit[" + stopBits + "] parity[" + parity + "] flow[" + flow + "]");

		int rty = 0;
		for (;;) {
			rty++;
			try {
				connector.setConnectTimeoutMillis(CONNECT_TIMEOUT * 1000);
				ConnectFuture future = connector.connect(serialAddress);
				future.awaitUninterruptibly();

				if (!future.isConnected()) {
					throw new Exception("not yet");
				}

				session = future.getSession();
				log.debug("SESSION CONNECTED[" + session.isConnected() + "]");

				break;
			} catch (Exception e) {
				if (rty < 2) {
					try {
						Thread.currentThread().sleep(5 * 1000);
					} catch (Exception ignore) {
					}
					continue;
				}
				log.error("Failed to connect. dev[" + name + "] baud[" + bauds + "] databit[" + dataBits + "] stopbit[" + stopBits + "] parity[" + parity + "] flow[" + flow + "]", e);
				throw e;
			}
		}

		if (session == null)
			throw new Exception("Failed to connect. dev[" + name + "] baud[" + bauds + "] databit[" + dataBits + "] stopbit[" + stopBits + "] parity[" + parity + "] flow[" + flow + "]");

		//MRPClientProtocolHandler handler = 
		//    (MRPClientProtocolHandler)session.getHandler();
		SMSClientProtocolHandler handler = (SMSClientProtocolHandler) session.getHandler();
		try {
			//handler.setMRPResource(resource,resourceObj);
			handler.setResponseTimeout(target.getTimeout());
			log.debug("*******   session :" + session);
			log.debug("******* (CircuitTarget)target :" + ((CircuitTarget) target).getPhoneNumber());
			handler.initSMS(session, (CircuitTarget) target);
			log.debug("SMSClient initCircuit End");

		} catch (Exception ex) {
			log.error(ex, ex);
			close();
			throw ex;
		}
	}

	/**
	 * set Target
	 *
	 * @param target
	 *            <code>SMSTarget</code> target
	 */
	public void setTarget(Target target) throws Exception {
		if (!(target instanceof SMSTarget))
			throw new Exception("not supported target");
		this.target = (SMSTarget) target;
	}

	private void saveCommLog(CommLog commLog) {
		try {
			if (this.logProcessor != null)
				this.logProcessor.putServiceData(ProcessorHandler.LOG_COMMLOG, commLog);
			else
				log.warn("Log Processor not registered");
		} catch (Exception ex) {
			log.error("save Communication Log failed", ex);
		}
	}

	public void setLogProcessor(ProcessorHandler logProcessor) {
		this.logProcessor = logProcessor;
	}

	private void saveAsyncCommandLog(AsyncCommandLog log) {
		asyncCommandLogDao.add(log);
	}

	/**
	 * request command data to Target and response
	 *
	 * @param command
	 *            <code>CommandData</code> request command
	 * @return response <code>ServiceData</code> response
	 * @throws Exception
	 */
	public CommandData sendCommand(CommandData command) throws Exception {
		log.info("SMSClient::sendCommand(" + command + ")");
		JpaTransactionManager txmanager = DataUtil.getBean(JpaTransactionManager.class);
		TransactionStatus txstatus = null;
		try {
		    txstatus = txmanager.getTransaction(null);
			isSmsTerminal = Boolean.parseBoolean(FMPProperty.getProperty("sms.Terminal", "false"));
			if (isSmsTerminal) {
				if (session == null || !session.isConnected())
					connect();
			}

			CommLog commLog = new CommLog();
			ServiceDataFrame frame = new ServiceDataFrame();
			frame.setSvc(GeneralDataConstants.SVC_C);
			// long mcuId = Long.parseLong(target.getMcuId());
			// frame.setMcuId(new UINT(mcuId));
			// if(mcuId > MAX_MCUID)
			// throw new Exception("mcuId is too Big: max["
			// + MAX_MCUID+"]");
			command.setAttr(ServiceDataConstants.C_ATTR_REQUEST);
			command.setTid(FrameUtil.getCommandTid());
			command.setMcuId(target.getTargetId());

			log.debug("target.getTargetId() " + target.getTargetId());

			SMSClientProtocolHandler client = null;

			if (isSmsTerminal) {
				client = (SMSClientProtocolHandler) session.getHandler();
			}

			log.debug("Target Meter Model[" + target.getMeterModel() + "]");
			log.debug("target.getMobileId() " + target.getMobileId());
			log.debug("target.getMeterModel() " + target.getMeterModel());
			log.debug("target.getGroupNumber() " + target.getGroupNumber());
			log.debug("target.getMemberNumber() " + target.getMemberNumber());
			log.debug("target.getMcuSwVersion() " + target.getFwVer());

			SMS_Handler handler = new SMS_Handler();
			String operationCode = "";

			Modem modem = modemDao.get(command.getMcuId());
			if (modem != null && modem.getSupplierId() != null) {
				protocolVersion = modem.getProtocolVersion();
			}

			handler.setModemNumber(target.getMobileId());
			handler.setIsSmsTerminal(isSmsTerminal);
			handler.setProtocolVersion(protocolVersion);
			log.info("protocolVersion [" + protocolVersion + "]");

			log.info("SMSClient log 1");

			MIBUtil mu = null;
			if (target.getNameSpace() != null && !"".equals(target.getNameSpace())) {
				mu = MIBUtil.getInstance(target.getNameSpace());
			} else {
				mu = MIBUtil.getInstance();
			}

			String commandOID = command.getCmd().toString();
			String commandName = mu.getName(commandOID);
			log.info("### Command Name = [" + commandName + "], OID = [" + commandOID + "]");

			if (target.getNameSpace() != null && !"".equals(target.getNameSpace())) {
				operationCode = MIBUtil.getInstance(target.getNameSpace()).getName(command.getCmd().toString());
			} else {
				operationCode = MIBUtil.getInstance().getName(command.getCmd().toString());
			}
			log.debug(command);

//			Properties prop = new Properties();
//			prop.load(getClass().getClassLoader().getResourceAsStream("config/fmp.properties"));
			
			Properties prop = FMPProperty.getProperties();
			String nameSpace = target.getNameSpace();
			String commandNameGG="";
			/**
			 *  Gana ECG용
			 */
			if (protocolVersion != null && protocolVersion.equals("0102") && nameSpace.equals("GG")) {
				//Ip 의 경우 . 대신 ,로 구분한다. ex) 127,0,0,1
				log.info("Send SMS Namespace = " + nameSpace);
				String smsIpAddr = prop.getProperty("GG.sms.ipAddr") == null ? "" : prop.getProperty("GG.sms.ipAddr");
				String smsPort = prop.getProperty("GG.sms.port") == null ? "" : prop.getProperty("GG.sms.port");

				command.setIpAddr(smsIpAddr);

				SMIValue[] smiValue = command.getSMIValue();

				AsyncCommandLog asyncCommandLog = new AsyncCommandLog();
				long trId = 0;
				Long result = 0L; // asyncCommandLogDao.getMaxTrId(target.getTargetId());
				if (result != null) {
					// trId = result.intValue() + 1;
					trId = System.currentTimeMillis();
				}
				asyncCommandLog.setTrId(trId);
				asyncCommandLog.setMcuId(target.getTargetId());
				asyncCommandLog.setDeviceType(McuType.MMIU.name());
				asyncCommandLog.setDeviceId(target.getTargetId());

				String cmd = null;
				if (smiValue[0].getVariable() instanceof OCTET) {
					cmd = ((OCTET) smiValue[0].getVariable()).toString();
					if (cmd.equals(RequestFrame.CMD_SETCUTOFF)) {
						cmd = ((OCTET) smiValue[3].getVariable()).toString();
						if (cmd.equals("01")) {
							smiValue[0] = DataUtil.getSMIValue(nameSpace, new OCTET("relayOff"));
							log.info(((OCTET) smiValue[0].getVariable()).toString());
						} else if (cmd.equals("02")) {
							smiValue[0] = DataUtil.getSMIValue(nameSpace, new OCTET("relayOn"));
							log.info(((OCTET) smiValue[0].getVariable()).toString());
						} else if (cmd.equals("03")) {
							smiValue[0] = DataUtil.getSMIValue(nameSpace, new OCTET("relayStatus"));
							log.info(((OCTET) smiValue[0].getVariable()).toString());
						}
					}
				} else if (smiValue[0].getVariable() instanceof HEX) {
					cmd = ((HEX) smiValue[0].getVariable()).toString();
					if (cmd.equals(RequestFrame.CMD_SETCUTOFF)) {
						cmd = ((HEX) smiValue[3].getVariable()).toString();
						if (cmd.equals("01")) {
							smiValue[0] = DataUtil.getSMIValue(nameSpace, new OCTET("relayOff"));
							log.info(((HEX) smiValue[0].getVariable()).toString());
						} else if (cmd.equals("02")) {
							smiValue[0] = DataUtil.getSMIValue(nameSpace, new OCTET("relayOn"));
							log.info(((HEX) smiValue[0].getVariable()).toString());
						} else if (cmd.equals("03")) {
							smiValue[0] = DataUtil.getSMIValue(nameSpace, new OCTET("relayStatus"));
							log.info(((HEX) smiValue[0].getVariable()).toString());
						}
					}
				}
				commandNameGG=ggCommand(smiValue[0]);
				asyncCommandLog.setCommand(commandNameGG);
				asyncCommandLog.setTrOption(TR_OPTION.ASYNC_OPT_RETURN_DATA_SAVE.getCode());
				asyncCommandLog.setOperator(OperatorType.OPERATOR.name());
				asyncCommandLog.setState(TR_STATE.Waiting.getCode());
				asyncCommandLog.setCreateTime(TimeUtil.getCurrentTime());
				asyncCommandLog.setRequestTime(TimeUtil.getCurrentTime());
				asyncCommandLog.setLastTime(null);
				saveAsyncCommandLog(asyncCommandLog);

				log.info("CMD[" + cmd + "]");
				if (cmd.equals(RequestFrame.CMD_ONDEMAND) && smiValue.length >= 4) {
					String[] param = new String[2];
					param[0] = ((OCTET) smiValue[3].getVariable()).toString();//fromDate
					param[1] = ((OCTET) smiValue[4].getVariable()).toString();//endDate

					Set<Meter> meterSet = modem.getMeter();

					Iterator iterator = meterSet.iterator();
					List<String> operationList = new ArrayList<String>();
					operationList.add(MeterCommand.ON_DEMAND_METERING.getCode());

					while (iterator.hasNext()) {
						Meter meter = (Meter) iterator.next();
						OperationList op = operationListDao.getOperationListByModelId(meter.getModelId(), operationList).get(0);
						Set<OperationList> opList = new HashSet<OperationList>();
						opList.add(op);

						meter.getModel().setOperationList(opList);
						log.info("param0 : " + param[0]);
						log.info("param1 : " + param[1]);

						int[] offsetCnt = CmdUtil.convertStartStopIndex(meter.getLpInterval(), param[0], param[1]);
						
						Integer num = 0;
						Integer maxNum = asyncCommandParamDao.getMaxNum(modem.getDeviceSerial(), trId);
						if (maxNum != null)
							num = maxNum + 1;

						for (int i = 0; i < offsetCnt.length; i++) {
							AsyncCommandParam asyncCommandParam = new AsyncCommandParam();
							asyncCommandParam.setMcuId(target.getTargetId());
							asyncCommandParam.setNum(num);
							asyncCommandParam.setParamType("String");
							asyncCommandParam.setParamValue(StringUtil.nullToBlank(offsetCnt[i]));
							asyncCommandParam.setTrId(trId);

							asyncCommandParamDao.add(asyncCommandParam);
							num += 1;
						}
					}
				}

				CommandData commandGG = new CommandData();
				commandGG.append((SMIValue) DataUtil.getSMIValue(nameSpace, new OCTET(RequestFrame.CMD_GPRSCONNECT_OID)));
				commandGG.append(smiValue[1]);
				commandGG.append(smiValue[2]);
				commandGG.append((SMIValue) DataUtil.getSMIValue(nameSpace, new OCTET(smsIpAddr)));
				commandGG.append((SMIValue) DataUtil.getSMIValue(nameSpace, new OCTET(smsPort)));
				command = commandGG;
			}
			/**
			 *  Iraq MOE용
			 */
			else if (protocolVersion != null && protocolVersion.equals("0102") && nameSpace.equals("GD")) {
				log.info("Send SMS Namespace = " + nameSpace);
				String smsIpAddr = prop.getProperty("GD.sms.ipAddr") == null ? "" : prop.getProperty("GD.sms.ipAddr");
				String smsPort = prop.getProperty("GD.sms.port") == null ? "" : prop.getProperty("GD.sms.port");

				command.setIpAddr(smsIpAddr);
				SMIValue[] smiValue = command.getSMIValue();

				Long maxTrId = asyncCommandLogDao.getMaxTrId(modem.getDeviceSerial());
				String trnxId;
				if (maxTrId != null) {
					trnxId = String.format("%08d", maxTrId.intValue() + 1);
				} else {
					trnxId = "00000001";
				}

				Map<String, String> param = new HashMap<String, String>();
				
				
				String cmd = null;
				
//                log.debug("command oid="+mu.getOid(cmdName));
//        		command.setCmd(mu.getMIBNodeByName(cmdName).getOid());
        		
        		
				if(commandName == null || commandName.equals("")){
					cmd = ((OCTET) smiValue[0].getVariable()).toString();
					if (cmd.equals(RequestFrame.CMD_SETCUTOFF)) {

					}else if (cmd.equals(RequestFrame.CMD_GETCUTOFF)) {

					}else if(cmd.equals(RequestFrame.CMD_ONDEMAND)) {  // On-Demand Metering
						commandName = "cmdOndemandMetering";
					}
				}
				
//				if (smiValue[0].getVariable() instanceof OCTET) {
//					cmd = ((OCTET) smiValue[0].getVariable()).toString();
//					if (cmd.equals(RequestFrame.CMD_SETCUTOFF)) {
//
//					}else if(cmd.equals(RequestFrame.CMD_ONDEMAND) && smiValue.length >= 4) {  // On-Demand Metering
//
//						commandName = "cmdOndemandMetering";
//					}
//				} else if (smiValue[0].getVariable() instanceof HEX) {
//					cmd = ((HEX) smiValue[0].getVariable()).toString();
//					if (cmd.equals(RequestFrame.CMD_SETCUTOFF)) {
//
//					}
//				}
				
				log.info("CMD[" + cmd + "] commandName[" + commandName + "]");

				CommandData commandGD = new CommandData();
				commandGD.append((SMIValue) DataUtil.getSMIValue(nameSpace, new OCTET(RequestFrame.CMD_GPRSCONNECT_OID)));
				
				/*
				 * READ_OPTION_RELAY     (3),   // Relay switch table read - iraq support
        		 * WRITE_OPTION_RELAYON  (4),   // Relay switch on - iraq support
        	     * WRITE_OPTION_RELAYOFF (5),   // Relay switch off - iraq support
        	     * WRITE_OPTION_TIMESYNC (8),   // Timesync (Er000002 Non Clear) - iraq support
				 */
				if (commandName.equals("cmdOnDemandMeter")) { // Relay Status : 3, Relay On : 4, Relay Off : 5 
					if (smiValue[1].getVariable().toString().equals("3")) {
						commandName = "cmdRelayStatus";  
						commandGD.append(smiValue[0]);  // seq
						commandGD.append(smiValue[1]);  // type
					} else if (smiValue[1].getVariable().toString().equals("4")) {
						commandName = "cmdRelayReconnect";
						commandGD.append(smiValue[0]);  // seq
						commandGD.append(smiValue[1]);  // type
					} else if(smiValue[1].getVariable().toString().equals("5")){
						commandName = "cmdRelayDisconnect";
						commandGD.append(smiValue[0]);  // seq
						commandGD.append(smiValue[1]);  // type

					}else if(smiValue[1].getVariable().toString().equals("8")){
						commandName = "cmdSetMeterTime";
						commandGD.append(smiValue[0]);  // seq
						commandGD.append(smiValue[1]);  // type

					} else {  
						log.debug("### 알수없는 command 실행됨.");

					}
				} else if(commandName.equals("cmdOndemandMetering")){  // cmdOndemandMetering
					/**
					 * 만일 모뎀하나에 미터가 여러개 물려있을경우도 생각해볼것. Protocol 수정되어야함.
					 */
					Set<Meter> meterSet = modem.getMeter();
					Meter[] meter = meterSet.toArray(new Meter[0]);
					
					int[] startStopIndex = CmdUtil.convertStartStopIndex(meter[0].getLpInterval()
							, ((OCTET) smiValue[3].getVariable()).toString(), ((OCTET) smiValue[4].getVariable()).toString());
					param.put("start_idx", String.valueOf(startStopIndex[0]));
					param.put("stop_idx", String.valueOf(startStopIndex[1]));
					
					commandGD.append(smiValue[1]);  // seq
					commandGD.append(smiValue[2]);  // type
				}/*else if (commandName.equals("cmdSetMeterTime")) { // Meter Time Sync
					commandGD.append(smiValue[0]);  // seq
					commandGD.append(smiValue[1]);  // type
				}*/

				commandGD.append((SMIValue) DataUtil.getSMIValue(nameSpace, new OCTET(smsIpAddr)));
				commandGD.append((SMIValue) DataUtil.getSMIValue(nameSpace, new OCTET(smsPort)));
				command = commandGD;
				
				
				/*
				 * 비동기 명령 저장 : SMS발송보다 먼저 저장함.
				 */
				saveAsyncCommandForGD(modem.getDeviceSerial(), Long.parseLong(trnxId), commandName, param, TimeUtil.getCurrentTime());

			}

								
			// Check 'SMS SKIP' (check skip condition for no-sms modem)
			Boolean isSmsSkip = false;			
			if(nameSpace.equals("GG")){			
				// SMS를 지원하지 않는 GPRS에 대응하기 위해, 문자를 전송하지 않음.
				// GPRS 모뎀의 fwver을 이용하여 이를 구분한다.
				String smsSkipVersion = prop.getProperty("GG.sms.skip.fwver") == null ? "" : prop.getProperty("GG.sms.skip.fwver");				
				String smsSkipDefault = prop.getProperty("GG.sms.skip.default") == null ? "false" : prop.getProperty("GG.sms.skip.default").toLowerCase();
																
				MMIU mmiu = (MMIU) modem;
				String fw_ver = mmiu.getFwVer();
				
				log.info("#SMSMMIUClient: GG.sms.skip info [targetFW:"+fw_ver+"],"
						+ "[smsSkipVersion:"+smsSkipVersion+"],[smsSkipDefault:"+smsSkipDefault+"]");
				
				// fmp에 지정된 기준버전과 현재 타겟의 버전 비교
				// 모뎀버전이 null이거나 프로퍼티 버전이 null인경우에 대비하여 default를 사용.
				if(fw_ver!=null && !"".equals(smsSkipVersion)){
					double propSkipVer = Double.parseDouble(smsSkipVersion);
					double propFwVer = Double.parseDouble(fw_ver);
					if(propFwVer >= propSkipVer){
						log.info("#SMSMMIUClient: Skip Sending SMS (targetFW >= smsSkipVersion)");
						isSmsSkip = true;
					}else{
						log.info("#SMSMMIUClient: Send SMS (targetFW < smsSkipVersion)");
						//do nothing, keep continue
					}
				}else if(smsSkipDefault!=null && smsSkipDefault.equals("true")){
					log.info("#SMSMMIUClient: Skip Sending SMS (default or null)");
					isSmsSkip = true;
				}else{
					log.info("##SMSMMIUClient: Send SMS (Condition is normal)");
					//do nothing, keep continue
				}
			}			
			
			// SMS를 보내지 않는 조건이 충족되면, isSmsSkip을 true로 바꾸고,
			// Commlog를 생략하고 return함.			
			if(isSmsSkip){
				CommandData cmdData = new CommandData();
				cmdData.setCnt(new WORD(1));				
				//cmdData.append(DataUtil.getSMIValue(new OCTET(98)));
				cmdData.append(DataUtil.getSMIValue(new OCTET(1)));
				//cmdData.append(DataUtil.getSMIValue(new OCTET("105.9")));
				//cmdData.append(new SMIValue(new OID("1.11.0"), new STRING("SMS SKIP - Wait for the connection from modem")));
				TCPTriggerClient tcpTriggerClient = new TCPTriggerClient();
				boolean triggerUse = Boolean.parseBoolean(FMPProperty.getProperty("tcp.trigger.use", "false"));
				if(triggerUse == true){
					if (! tcpTriggerClient.cmdTCPTrigger(commandNameGG, modem.getIpAddr())){
						throw new Exception("Fail to create TCP socket");
					}
				}else{
					log.info("No use TCP Trigger");
				}
				log.debug("SMSMMIUClient::sendCommand::end Wait Response");
				txmanager.commit(txstatus);
				
				
				return cmdData;
			}
			
			ServiceData sd;
			log.info("SMSClient log 2");
			String startTime = TimeUtil.getCurrentTime();
			log.info("SMSClient log startTime " + startTime);
			commLog.setStartDate(startTime.substring(0, 8));
			log.info("SMSClient log  startTime.substring(0, 8) " + startTime.substring(0, 8));
			commLog.setStartTime(startTime.substring(8, 14));
			commLog.setStartDateTime(startTime);
			log.info("SMSClient log startTime.substring(8, 14) " + startTime.substring(8, 14));
			commLog.setProtocolCode(CommonConstants.getProtocol(protocolType + ""));
			log.info("SMSClient log CommonConstants.getProtocol(protocolType)" + CommonConstants.getProtocol(protocolType + ""));
			commLog.setSenderTypeCode(CommonConstants.getSenderReceiver(activatorType + ""));
			log.info("SMSClient log CommonConstants.getSenderReceiver(activatorType+)" + CommonConstants.getSenderReceiver(activatorType + ""));
			commLog.setSenderId(DataUtil.getFepIdString());
			log.info("SMSClient log " + DataUtil.getFepIdString());
			commLog.setReceiverTypeCode(CommonConstants.getSenderReceiver(targetType + ""));
			log.info("SMSClient log CommonConstants.getSenderReceiver(targetType+) " + CommonConstants.getSenderReceiver(targetType + ""));
			commLog.setReceiverId(target.getTargetId());
			log.info("SMSClient log target.getTargetId() " + target.getTargetId());
			commLog.setOperationCode(operationCode);
			log.info("SMSClient log MIBUtil.getInstance().getName(command.getCmd().toString())" + operationCode);
			long s = System.currentTimeMillis();
						
			log.debug("SMSMMIUClient::sendCommand::start Wait Response");
			sd = handler.execute(client, session, command);

			log.debug("SMSMMIUClient::sendCommand::end Wait Response");

			long e = System.currentTimeMillis();
			if (sd == null)
				return null;
			log.debug("Received Response TID : " + ((CommandData) sd).getTid());
			commLog.setEndTime(TimeUtil.getCurrentTime());
			commLog.setSendBytes((int) handler.getSendBytes());
			commLog.setRcvBytes(sd.getTotalLength());
			commLog.setUnconPressedRcvBytes(sd.getTotalLength());
			if (((CommandData) sd).getErrCode().getValue() > 0) {
				commLog.setCommResult(0);
				commLog.setDescr(ErrorCode.getMessage(((CommandData) sd).getErrCode().getValue()));
			} else {
				commLog.setCommResult(1);
			}
			commLog.setTotalCommTime((int) (e - s));
			commLog.setSvcTypeCode(CommonConstants.getHeaderSvc("C"));
			commLog.setSuppliedId(target.getSupplierId());
			log.info(commLog.toString());

			// saveCommLog(commLog);
			txmanager.commit(txstatus);
			return (CommandData) sd;
		} catch (Exception ex) {
		    if (txstatus != null && !txstatus.isCompleted()) txmanager.rollback(txstatus);
			log.error("sendCommand failed : command[" + command + "]", ex);
			throw ex;
		} finally {
			if (isSmsTerminal) {
				close();
			}
		}
	}

	/**
	 * send Alarm to Target
	 *
	 * @param alarm
	 *            <code>AlarmData</code> send alarm
	 * @throws Exception
	 */
	public void sendAlarm(AlarmData alarm) throws Exception {
		if (session == null || !session.isConnected())
			connect();
		ServiceDataFrame frame = new ServiceDataFrame();
		frame.setAttrByte(GeneralDataConstants.ATTR_ACK);
		frame.setAttrByte(GeneralDataConstants.ATTR_START);
		frame.setAttrByte(GeneralDataConstants.ATTR_END);
		long mcuId = Long.parseLong(target.getTargetId());
		frame.setMcuId(new UINT(mcuId));
		if (mcuId > MAX_MCUID)
			throw new Exception("mcuId is too Big: max[" + MAX_MCUID + "]");
		frame.setSvcBody(alarm.encode());
		frame.setSvc(GeneralDataConstants.SVC_A);

		send(frame);

		log.info("sendAlarm : finished");
	}

	/**
	 * send Event to Target
	 *
	 * @param event
	 *            <code>EventData</code> event alarm
	 * @throws Exception
	 */
	public void sendEvent(EventData event) throws Exception {
		if (session == null || !session.isConnected())
			connect();
		ServiceDataFrame frame = new ServiceDataFrame();
		frame.setAttrByte(GeneralDataConstants.ATTR_ACK);
		frame.setAttrByte(GeneralDataConstants.ATTR_START);
		frame.setAttrByte(GeneralDataConstants.ATTR_END);
		long mcuId = Long.parseLong(target.getTargetId());
		frame.setMcuId(new UINT(mcuId));
		if (mcuId > MAX_MCUID)
			throw new Exception("mcuId is too Big: max[" + MAX_MCUID + "]");
		frame.setSvcBody(event.encode());
		frame.setSvc(GeneralDataConstants.SVC_E);

		send(frame);

		log.info("sendEvent : finished");
	}

	/**
	 * send Measurement Data to Target
	 *
	 * @param md
	 *            <code>MDData</code> Measurement Data
	 * @throws Exception
	 */
	public void sendMD(MDData md) throws Exception {
		if (session == null || !session.isConnected())
			connect();
		ServiceDataFrame frame = new ServiceDataFrame();
		frame.setAttrByte(GeneralDataConstants.ATTR_ACK);
		frame.setAttrByte(GeneralDataConstants.ATTR_START);
		frame.setAttrByte(GeneralDataConstants.ATTR_END);
		long mcuId = Long.parseLong(target.getTargetId());
		frame.setMcuId(new UINT(mcuId));
		if (mcuId > MAX_MCUID)
			throw new Exception("mcuId is too Big: max[" + MAX_MCUID + "]");
		frame.setSvcBody(md.encode());
		frame.setSvc(GeneralDataConstants.SVC_M);

		send(frame);

		log.info("sendMD : finished");
	}

	/**
	 * send R Measurement Data to Target
	 *
	 * @param md
	 *            <code>RMDData</code>R Measurement Data
	 * @throws Exception
	 */
	public void sendRMD(RMDData rmd) throws Exception {
		//ProtocolSession session = connect();
		if (session == null || !session.isConnected())
			connect();
		ServiceDataFrame frame = new ServiceDataFrame();
		frame.setAttrByte(GeneralDataConstants.ATTR_ACK);
		frame.setAttrByte(GeneralDataConstants.ATTR_START);
		frame.setAttrByte(GeneralDataConstants.ATTR_END);
		long mcuId = Long.parseLong(target.getTargetId());
		frame.setMcuId(new UINT(mcuId));
		if (mcuId > MAX_MCUID)
			throw new Exception("mcuId is too Big: max[" + MAX_MCUID + "]");
		frame.setSvcBody(rmd.encode());
		frame.setSvc(GeneralDataConstants.SVC_R);

		send(frame);

		log.info("sendMD : finished");
	}

	/**
	 * close SMSClient session
	 */
	public void close() {
		close(true);
	}

	/**
	 * close SMSClient session and wait completed
	 *
	 * @param wait
	 *            <code>boolean</code> wait
	 */
	public void close(boolean wait) {
		log.debug(wait);
		if (session != null) {
			CloseFuture future = session.closeNow();
			future.awaitUninterruptibly();
		}

		if (connector != null) {
			if (connector.getFilterChain().contains(getClass().getName()))
				connector.getFilterChain().remove(getClass().getName());
			connector.dispose();
		}

		session = null;
	}

	/**
	 * check whether connected or not
	 *
	 * @return connected <code>boolean</code>
	 */
	public boolean isConnected() {
		if (session == null)
			return false;
		return session.isConnected();
	}

	/**
	 * wait ACK
	 */
	/*
	private void waitAck(IoSession session, int sequence)
	    throws Exception
	{ 
	    log.debug("waitACK.SEQ:" + sequence);
	    MRPClientProtocolHandler handler = 
	       (MRPClientProtocolHandler)session.getHandler(); 
	    handler.waitAck(session,sequence);
	}
	*/
	/**
	 * send GeneralDataFrame to Target
	 *
	 * @param frame
	 *            <code>GeneralDataFrame</code>
	 */
	private synchronized int send(GeneralDataFrame frame) throws Exception {
		int sendBytes = 0;
		byte[] bx = frame.encode();
		byte[] mbx = null;
		IoBuffer buf = null;
		ArrayList<?> framelist = FrameUtil.makeMultiEncodedFrame(bx, session);
		session.setAttribute("sendframes", framelist);
		int lastIdx = framelist.size() - 1;
		mbx = (byte[]) framelist.get(lastIdx);
		int lastSequence = DataUtil.getIntToByte(mbx[1]);
		boolean isSetLastSequence = false;
		if ((lastIdx / GeneralDataConstants.FRAME_MAX_SEQ) < 1) {
			session.setAttribute("lastSequence", new Integer(lastSequence));
			isSetLastSequence = true;
		}
		Iterator<?> iter = framelist.iterator();
		int cnt = 0;
		int seq = 0;
		ControlDataFrame wck = null;
		while (iter.hasNext()) {
			if (!isSetLastSequence && ((lastIdx - cnt) / GeneralDataConstants.FRAME_MAX_SEQ) < 1) {
				session.setAttribute("lastSequence", new Integer(lastSequence));
				isSetLastSequence = true;
			}
			mbx = (byte[]) iter.next();
			sendBytes += mbx.length;
			seq = DataUtil.getIntToByte(mbx[1]);
			buf = IoBuffer.allocate(mbx.length);
			buf.put(mbx, 0, mbx.length);
			buf.flip();
			session.write(buf);

			if (((cnt + 1) % GeneralDataConstants.FRAME_WINSIZE) == 0) {
				log.debug("WCK : start : " + (seq - GeneralDataConstants.FRAME_WINSIZE + 1) + "end : " + seq);
				wck = FrameUtil.getWCK((seq - GeneralDataConstants.FRAME_WINSIZE + 1), seq);
				sendBytes += GeneralDataConstants.HEADER_LEN + GeneralDataConstants.TAIL_LEN + wck.getArg().getValue().length;
				//session.write(wck);
				//session.setAttribute("wck",wck);
				//waitAck(session,cnt); 
			}
			cnt++;
		}

		if ((cnt % GeneralDataConstants.FRAME_WINSIZE) != 0) {
			if (cnt > 1) {
				log.debug("WCK : start : " + (seq - (seq % GeneralDataConstants.FRAME_WINSIZE)) + "end : " + seq);
				wck = FrameUtil.getWCK(seq - (seq % GeneralDataConstants.FRAME_WINSIZE), seq);
				sendBytes += GeneralDataConstants.HEADER_LEN + GeneralDataConstants.TAIL_LEN + wck.getArg().getValue().length;
				//session.write(wck); 
				//session.setAttribute("wck",wck); 
				//waitAck(session,cnt-1); 
			}
		}

		session.removeAttribute("wck");
		return sendBytes;
	}

	private final String CRLF = "\r\n";
	private final char CTRLZ = 0x1A;
	private final char DQUATA = 0x22;

	@Deprecated
	public synchronized CommandData sendCommand1(CommandData command) throws Exception {
		int connMode = 1;
		FMPClientResource resource = FMPClientResourceFactory.getResource(connMode);
		log.debug("##### SMSClient TS : Acquire ###### locId=" + target.getLocation());
		Object resourceObj = null;
		if (target.getLocation() != null && !"".equals(target.getLocation().getName())) {
			resourceObj = resource.acquire(target.getLocation().getId());
		} else {
			resourceObj = resource.acquire();
		}
		TerminalServerPort tsport = (TerminalServerPort) resourceObj;
		String ipaddr = tsport.getIpAddr();
		int port = tsport.getPort();

		Socket socket = null;
		InputStream in = null;
		OutputStream out = null;
		CommandData cd = null;
		try {
			socket = new Socket();
			socket.connect(new InetSocketAddress(ipaddr, port));
			in = socket.getInputStream();
			out = socket.getOutputStream();

			// ATZ
			String ATZ = FMPProperty.getProperty("protocol.circuit.command.ATZ", "ATZ");
			out.write(FrameUtil.getByteBuffer(ATZ + CRLF).array());
			readOK(in);

			/*out.write(FrameUtil.getByteBuffer("AT+CMGL="+DQUATA+"ALL"+DQUATA+CRLF).array());
			String message = readOK(in);
			StringTokenizer st = new StringTokenizer(message,"+CMGL: ");
			
			while(st.hasMoreTokens()){
			    String listMessage = (String)st.nextToken();
			    int sInx = listMessage.indexOf(",");
			    if (sInx == -1) continue;
			    int index = Integer.parseInt(listMessage.substring(0,sInx));
			    out.write(FrameUtil.getByteBuffer("AT+CMGD="+index+"\r\n").array());
			    readOK(in);
			}*/
			int index = 40;
			for (int i = index; i > 0; i--) {
				out.write(FrameUtil.getByteBuffer("AT+CMGD=" + i + "\r\n").array());
				readOK(in);
				//log.debug("["+message+"]");
			}

			cd = execute(command, in, out, target.getMobileId());
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (Exception e) {
				}
			}
			if (socket != null) {
				try {
					socket.close();
				} catch (Exception e) {
				}
			}
		}

		return cd;
	}

	private CommandData execute(CommandData command, InputStream in, OutputStream out, String mobileId) throws Exception {
		CommandData commandData = null;
		ByteArray ba = new ByteArray();
		String cmd = command.getCmd().getValue();
		SMIValue[] smiValue = null;
		smiValue = command.getSMIValue();
		int seq = 0;
		String type = null;
		String[] param = null;
		int failCode = 0;

		if (smiValue != null && smiValue.length > 0) {
			if (smiValue[0].getVariable() instanceof OCTET)
				cmd = ((OCTET) smiValue[0].getVariable()).toString();
			else if (smiValue[0].getVariable() instanceof HEX)
				cmd = ((HEX) smiValue[0].getVariable()).toString();
		}

		log.debug("==============SMS_Handler start cmd:" + cmd + "================");

		for (int i = 0; smiValue != null && i < smiValue.length; i++) {
			log.debug(smiValue[i]);
		}

		if (cmd.equals(RequestFrame.CMD_ONDEMAND)) {

			if (smiValue != null && smiValue.length >= 3) {

				seq = Integer.parseInt(((OCTET) smiValue[1].getVariable()).toString());
				type = ((OCTET) smiValue[2].getVariable()).toString();

				if (smiValue.length >= 5) {
					param = new String[2];
					param[0] = ((OCTET) smiValue[3].getVariable()).toString().substring(2, 8);
					param[1] = ((OCTET) smiValue[4].getVariable()).toString().substring(2, 8);
				}
			}

			RequestFrame reqFrame = new RequestFrame((byte) seq, type, RequestFrame.CMD_ONDEMAND, param);
			sendSMS(out, in, mobileId, reqFrame.encode());

			String mdTime = null;
			int currValue = 0;

			commandData = new CommandData();
		} else if (cmd.equals(RequestFrame.CMD_OTA)) {
			if (smiValue != null && smiValue.length >= 3) {
				seq = Integer.parseInt(((OCTET) smiValue[1].getVariable()).toString());
				type = ((OCTET) smiValue[2].getVariable()).toString();

				if (smiValue.length >= 4) {
					param = new String[1];
					param[0] = ((OCTET) smiValue[3].getVariable()).toString();
				}
			}

			RequestFrame reqFrame = new RequestFrame((byte) seq, type, RequestFrame.CMD_OTA, param);
			sendSMS(out, in, mobileId, reqFrame.encode());

			commandData = new CommandData();
			commandData.setErrCode(new BYTE(failCode));
			commandData.setCnt(new WORD(0));
		} else if (cmd.equals(RequestFrame.CMD_DOTA)) {
			if (smiValue != null && smiValue.length >= 3) {
				seq = Integer.parseInt(((OCTET) smiValue[1].getVariable()).toString());
				type = ((OCTET) smiValue[2].getVariable()).toString();

				if (smiValue.length >= 7) {
					param = new String[4];
					param[0] = ((OCTET) smiValue[3].getVariable()).toString();
					param[1] = ((OCTET) smiValue[4].getVariable()).toString();
					param[2] = ((OCTET) smiValue[5].getVariable()).toString();
					param[3] = ((OCTET) smiValue[6].getVariable()).toString();
				}
			}

			RequestFrame reqFrame = new RequestFrame((byte) seq, type, RequestFrame.CMD_DOTA, param);
			sendSMS(out, in, mobileId, reqFrame.encode());

			commandData = new CommandData();
			commandData.setErrCode(new BYTE(failCode));
			commandData.setCnt(new WORD(0));
		} else if (cmd.equals(RequestFrame.CMD_BYPASS)) {
			// kskim 2012-02-02
			if (smiValue != null && smiValue.length >= 3) {
				seq = Integer.parseInt(((OCTET) smiValue[1].getVariable()).toString());
				type = ((OCTET) smiValue[2].getVariable()).toString();
			}
			if (smiValue.length >= 4) {
				param = new String[1];
				param[0] = ((OCTET) smiValue[3].getVariable()).toString();
			}
			RequestFrame reqFrame = new RequestFrame((byte) seq, type, RequestFrame.CMD_BYPASS, param);
			sendSMS(out, in, mobileId, reqFrame.encode());
			//ResponseFrame resFrame = request(session, reqFrame);
			commandData = new CommandData();
			commandData.append(DataUtil.getSMIValue(new OCTET(Bypass.OPEN)));
			commandData.setCnt(new WORD(1));
		} else if (cmd.equals(RequestFrame.CMD_METERTIMESYNC)) {
			if (smiValue != null && smiValue.length >= 3) {
				seq = Integer.parseInt(((OCTET) smiValue[1].getVariable()).toString());
				type = ((OCTET) smiValue[2].getVariable()).toString();
			}
			if (smiValue.length >= 4) {
				param = new String[1];
				param[0] = ((OCTET) smiValue[3].getVariable()).toString();//timestamp
			}

			//CM21  cmdMeterTimeSync    Meter TimeSync 명령   EX) CM21,00110408010000
			RequestFrame reqFrame = new RequestFrame((byte) seq, type, RequestFrame.CMD_METERTIMESYNC, param);
			//ResponseFrame resFrame = request(session, reqFrame);
			sendSMS(out, in, mobileId, reqFrame.encode());

			commandData = new CommandData();
		}

		else {
			throw new MRPException(MRPError.ERR_INVALID_PARAM, "Invalid parameters");
		}

		log.debug("==============SMSMMIUClient end ================");
		return commandData;
	}

	private String readOK(InputStream in) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(in));

		String res = "";
		while ((res += br.readLine()).indexOf("OK") == -1)
			;
		log.debug(res);
		return res;
	}

	private String readInput(InputStream in) throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int b = -1;
		int c = '>';
		while ((b = in.read()) != c) {
			out.write(b);
		}
		return new String(out.toByteArray());
	}

	private String read(InputStream in) throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] b = new byte[1000];
		int len = in.read(b);
		out.write(b, 0, len);
		return new String(out.toByteArray());
	}

	private void sendSMS(OutputStream out, InputStream in, String mobileId, byte[] sendMessage) throws Exception {
		log.debug("<<<<<<<<<<<<<<<<<<<<<< Start Send SMS");
		String message = null;

		out.write(FrameUtil.getByteBuffer("AT+CMGF=1\r\n").array());//set text mode
		readOK(in);

		out.write(FrameUtil.getByteBuffer("AT+CSDH=1\r\n").array());//set text mode
		readOK(in);

		out.write(FrameUtil.getByteBuffer("AT+CSMP=49,167,0,0\r\n").array());//set text mode
		readOK(in);

		out.write(FrameUtil.getByteBuffer("AT+CMGS=" + mobileId + "\r\n").array());
		message = readInput(in);
		log.debug(message);

		log.debug(new String(sendMessage));
		out.write(sendMessage);
		out.write(FrameUtil.getByteBuffer(CTRLZ + "\r\n").array());
		message = read(in);

		// message = new String(read(in, CTRLZ));
		// log.debug(message);
		// Thread.sleep(1000);
		log.debug("AT+CMGS RESULT=" + message);
		//              }
		//          }
		log.debug("<<<<<<<<<<<<<<<<<<<<<< End Send SMS");
		;
	}

	private String ggCommand(SMIValue cmdValue) {

		String cmd = null;

		if (cmdValue.getVariable() instanceof OCTET) {
			cmd = ((OCTET) cmdValue.getVariable()).toString();
		} else if (cmdValue.getVariable() instanceof HEX) {
			cmd = ((HEX) cmdValue.getVariable()).toString();
		}

		log.info("CMD[" + cmd + "]");
		if (cmd.equals(RequestFrame.CMD_ONDEMAND)) {
			return "cmdOndemandMetering";
		} else if (cmd.equals("CM02")) {
			return "cmdRelayStatus";
		} else if (cmd.equals("relayOn")) {
			return "cmdRelayReconnect";
		} else if (cmd.equals("relayOff")) {
			return "cmdRelayDisconnect";
		} else if (cmd.equals(RequestFrame.CMD_METERTIMESYNC)) {
			return "cmdSetMeterTime";
		}
		return "unKnown";
	}

	private void saveAsyncCommandForGD(String deviceSerial, Long trId, String cmd, Map<String, String> param, String currentTime) {
		AsyncCommandLog asyncCommandLog = new AsyncCommandLog();
		asyncCommandLog.setTrId(trId);
		asyncCommandLog.setMcuId(deviceSerial);
		asyncCommandLog.setDeviceType(McuType.MMIU.name());
		asyncCommandLog.setDeviceId(deviceSerial);
		asyncCommandLog.setCommand(cmd);
		asyncCommandLog.setTrOption(TR_OPTION.ASYNC_OPT_RETURN_DATA_SAVE.getCode());
		asyncCommandLog.setState(1);
		asyncCommandLog.setOperator(OperatorType.OPERATOR.name());
		asyncCommandLog.setCreateTime(currentTime);
		asyncCommandLog.setRequestTime(currentTime);
		asyncCommandLog.setLastTime(null);
		asyncCommandLogDao.add(asyncCommandLog);

		Integer num = 0;
		if (param != null && param.size() > 0) {
			//parameter가 존재할 경우.
			Integer maxNum = asyncCommandParamDao.getMaxNum(deviceSerial, trId);

			if (maxNum != null) {
				num = maxNum + 1;
			}

			Iterator<String> iter = param.keySet().iterator();
			while (iter.hasNext()) {
				String key = iter.next();

				AsyncCommandParam asyncCommandParam = new AsyncCommandParam();
				asyncCommandParam.setMcuId(deviceSerial);
				asyncCommandParam.setNum(num);
				asyncCommandParam.setParamType(key);
				asyncCommandParam.setParamValue((String) param.get(key));
				asyncCommandParam.setTrId(trId);

				asyncCommandParamDao.add(asyncCommandParam);
				num += 1;
			}
		}
	}
}
