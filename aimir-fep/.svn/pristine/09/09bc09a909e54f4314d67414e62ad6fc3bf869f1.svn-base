package com.aimir.fep.protocol.fmp.client;

import java.text.ParseException;
import java.util.Hashtable;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.Interface;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.fep.meter.parser.plc.PLCData;
import com.aimir.fep.meter.parser.plc.PLCDataConstants;
import com.aimir.fep.meter.parser.plc.PLCDataFrame;
import com.aimir.fep.protocol.fmp.exception.FMPException;
import com.aimir.fep.protocol.fmp.exception.FMPResponseTimeoutException;
import com.aimir.fep.protocol.fmp.processor.ProcessorHandler;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.FrameUtil;
import com.aimir.model.device.CommLog;
import com.aimir.model.system.Code;
import com.aimir.util.TimeUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

/**
 * {@link FMPProtocolHandler} implementation of FEP FMP(AiMiR and MCU Protocol).
 *
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class PLCClientProtocolHandler extends IoHandlerAdapter {
	private static Log log = LogFactory.getLog(PLCClientProtocolHandler.class);

	private Object ackMonitor = new Object();

	private Object resMonitor = new Object();
	private Hashtable response = new Hashtable();

	private int idleTime = Integer.parseInt(FMPProperty.getProperty("protocol.idle.time", "5"));
	private int retry = Integer.parseInt(FMPProperty.getProperty("protocol.retry", "3"));
	private int responseTimeout = Integer.parseInt(FMPProperty.getProperty("protocol.response.timeout", "15"));

	private FMPClientResource resource = null;
	private Object resourceObj = null;

	public final static String isActiveFMPKey = "isActiveFMP";
	private Boolean activeFMP = Boolean.TRUE;

	private boolean isSessionOpened = false;

	private PLCDataFrame previousRequestPLC;// Previous Request PLC Frame
	private Integer protocolType = 
	    new Integer(FMPProperty.getProperty("protocol.type.default", 
	            CommonConstants.getProtocolCode(Protocol.PLC)+""));
	private String startTime = null;
	private String endTime = null;
	private long startLongTime = 0;
	private long endLongTime = 0;
	private CommLog commLog = new CommLog();
	private Integer activatorType = new Integer(FMPProperty.getProperty("protocol.system.MCU", "2"));
	private Integer targetType = new Integer(FMPProperty.getProperty("protocol.system.FEP", "1"));
	private Hashtable processors = null;

	/**
	 * inherited method from ProtocolHandlerAdapter
	 */
	public void exceptionCaught(IoSession session, Throwable cause) {
		// Close connection when unexpected exception is caught.
		cause.printStackTrace();
		session.write(FrameUtil.getEOT());
		FrameUtil.waitAfterSendFrame();
		session.closeNow();
	}

	/**
	 * @param session
	 * @param pdf
	 * @throws Exception
	 */
	private void receivedPLCDataFrame(IoSession session, PLCDataFrame pdf) throws Exception {
		PLCData pd = new PLCData(pdf);
		String ipaddr = session.getRemoteAddress().toString();
		ipaddr = ipaddr.substring(ipaddr.indexOf("/") + 1, ipaddr.indexOf(":"));
		pd = PLCData.decode(pdf, ipaddr);

		if (pd != null) {
			//log.debug(getProtoName() + " ServiceData :" + pd.getType());
			//log.debug(getProtoName() + " RECEIVED SERVICE DATA \n" + pd);

			endLongTime = System.currentTimeMillis();
			endTime = TimeUtil.getCurrentTime();

			commLog.setStartDate(startTime.substring(0, 8));
			commLog.setStartTime(startTime.substring(8, 14));
			commLog.setEndTime(endTime);
			// TODO Check PLC Interface
			commLog.setInterfaceCode(CommonConstants.getInterface(Interface.IF4.getName()));
			commLog.setProtocolCode(CommonConstants.getProtocol(protocolType+""));
			commLog.setSenderTypeCode(CommonConstants.getSenderReceiver(activatorType+""));
			commLog.setSenderId((pd.getSId() != null ? pd.getSId().toString() : ""));
			commLog.setReceiverTypeCode(CommonConstants.getSenderReceiver(targetType+""));
			commLog.setReceiverId(DataUtil.getFepIdString());
			commLog.setOperationCode(pd.getType());
	        commLog.setSvcTypeCode(CommonConstants.getDataSvc(pd.getServiceType()+""));
			commLog.setSendBytes(PLCDataConstants.ACK_FRAME_TOTAL_LEN); // ACK
			commLog.setRcvBytes(pd.getTotalLength());// included EOT that received
			commLog.setUnconPressedRcvBytes(pd.getUncompressedTotalLength());
			commLog.setCommResult(1);

			//log.debug("startTime[" + startTime + "] endTime[" + endTime + "]");
			//log.debug("startLongTime[" + startLongTime + "] endLongTime[" + endLongTime + "]");
			if (endLongTime - startLongTime > 0) {
				commLog.setTotalCommTime((int)(endLongTime - startLongTime));
			}
			else {
				commLog.setTotalCommTime(0);
			}

			//log.debug(getProtoName() + " " + commLog.toString());

			// TODO 테스트를 위해 주석 처리
			// MCU로 부터 데이터가 들어올 때는 해당 정보를 담아주지만
			// Server가 명령을 내리고 response를 받아오는 구조에서는 해당 정보를 담을 필요가 있을까?
			//processingPLC(session,pd);
			//saveCommLog(commLog);

			startLongTime = endLongTime;
			startTime = endTime;

			//수신한 데이터를 command 별로 HashTable에 넣는다
			if (pd instanceof PLCData) {
				int command = pd.getCommand();
				log.debug("Put Command["+DataUtil.getPLCCommandStr(pd.getCommand())+"] Response");
				response.put("" + command, pd);
				synchronized (resMonitor) {
					resMonitor.notify();
				}
			}else {
				log.error("!!Can't Put");
			}

			//ACK를 날릴 필요가 있는 PLCDataFrame에 대해 Ack 전송
	        if(FrameUtil.isAck(pdf))
	        {
	            log.debug("send ACK - DIRECTION["+pdf.getProtocolDirection()+"] COMMAND[" + pdf.getCommand()+"]");
	            session.write(FrameUtil.getPLCACK(pdf));
	        }
		}
		else {
			log.debug(getProtoName() + " PLCData is null");
		}
	}

	/**
	 * process the service data
	 *
	 * @param pd
	 */
	private void processingPLC(PLCData pd) {
		String serviceType = pd.getType();
		ProcessorHandler handler = null;
		//TODO 테스트를 위해 주석 처리 : PLC Data를 받았을 때 해당 큐에 파일을 넣어주는 부분
		handler = (ProcessorHandler) processors.get(serviceType);
		try {
			handler.putServiceData(serviceType, pd);
		}
		catch (Exception ex) {
			log.error(ex);

		}
	}

	/**
	 * process plc data
	 */
	@SuppressWarnings("unchecked")
	private synchronized void processingPLC(IoSession session, PLCData pd) {
		// TODO 테스트를 위해 잠시 주석 처리
		processingPLC(pd);
	}

	/**
	 * inherited method from ProtocolHandlerAdapter handling GeneralDataFrame
	 *
	 * @param session
	 *            <code>IoSession</code> session
	 * @param message
	 *            <code>Object</code> decoded GeneralDataFrame
	 */
	public void messageReceived(IoSession session, Object message) {
		try {
			//log.info("messageReceived [Start]");
			if (message instanceof PLCDataFrame) {
				receivedPLCDataFrame(session, (PLCDataFrame) message);

			}
			else {
				new Exception("Recevied Object is not PLCDataFrame!");
			}
			//log.info("messageReceived [End]");
		}
		catch (Exception ex) {
			log.error("PLCClientProtocolHandler::messageReceived " + " failed", ex);
		}
	}

	public void messageSent(IoSession session, Object message) throws Exception {
		try {
			//log.info("messageSent [Start]");
			if (message instanceof PLCDataFrame) {
				PLCDataFrame pdf = (PLCDataFrame)message;
				if (pdf.getProtocolDirection() == PLCDataConstants.PROTOCOL_DIRECTION_FEP_IRM) {
					previousRequestPLC = (PLCDataFrame) message;
				}

				if (pdf.getCommand() == PLCDataConstants.COMMAND_A) {
					session.closeNow();
				}
			}
			//log.info("messageSent [End]");
		}
		catch (Exception ex) {
			log.error(" FMPProtocolHandler::MessageSent " + " failed", ex);
		}
	}

	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		log.info("IDLE COUNT : "
                + session.getIdleCount(IdleStatus.BOTH_IDLE));
        if(session.getIdleCount(IdleStatus.BOTH_IDLE) >= retry)
        {
            /** modified by D.J Park in 2006.04.10
             * if wait response ,then do not close session
             */
            if(response.values().size() < 1)
            {
            	//TODO 나도 ack라도 날려야될까?
                //session.write(FrameUtil.getEOT());
                //FrameUtil.waitAfterSendFrame();
            	session.closeNow();
            }
        }
	}

	/**
	 * inherited method from ProtocolHandlerAdapter
	 */
	public void sessionOpened(IoSession session) {
		startLongTime = System.currentTimeMillis();
		try {
		    startTime = TimeUtil.getCurrentTime();
		}
		catch (ParseException e) {
		    log.warn(e);
		}
		session.setAttribute(isActiveFMPKey, activeFMP);
		session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE, idleTime);
		isSessionOpened = true;
		log.debug("sessionOpened : " + session.getRemoteAddress());
	}

	/**
	 * inherited method from ProtocolHandlerAdapter
	 */
	public void sessionClosed(IoSession session) {
		synchronized (ackMonitor) {
			ackMonitor.notify();
		}
		synchronized (resMonitor) {
			resMonitor.notify();
		}

		session.removeAttribute("sendframes");
		session.removeAttribute("lastSequence");
		session.removeAttribute("wck");

		try {
			if (resource != null && resourceObj != null) {
				log.debug("#### FMPProtocalHandler: Resource Release ###");
				resource.release(resourceObj);
				log.debug("FMPProtocalHandler: " + resource.getActiveResourcesString());
				log.debug("FMPProtoalHandler: " + resource.getIdleResourcesString());
			}
			// if(response != null && response.values().size() > 0)
			// response.clear();
		}
		catch (Exception ex) {
			log.error(ex, ex);
		}

		isSessionOpened = false;
		log.debug("Session Closed : " + session.getRemoteAddress());
	}

	/**
	 * set command data response timeout
	 *
	 * @param responseTimeout
	 *            <code>String</code> timeout of response for request command
	 */
	public void setResponseTimeout(int responseTimeout) {
		this.responseTimeout = responseTimeout;
	}

	/**
	 * wait ACK ControlDataFrame
	 */
	public void waitAck() {
		synchronized (ackMonitor) {
			try {
				// log.debug("ACK Wait");
				ackMonitor.wait(500);
				// log.debug("ACK Received");
			}
			catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}
	}

	/**
	 * wait util received command response data
	 */
	public void waitResponse() {
		synchronized (resMonitor) {
			try {
				resMonitor.wait(500);
			}
			catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}
	}

	/**
	 * wait util received command response data and return Response
	 *
	 * @param session
	 *            <code>IoSession</code> session
	 * @param command
	 *            <code>int</code> command request id
	 * @throws FMPException
	 */
	public PLCData getResponse(IoSession session, int command) throws FMPException {
		String key = "" + command;
		long stime = System.currentTimeMillis();
		long ctime = 0;
		int waitResponseCnt = 0;
		while (session.isConnected()) {
			if (response.containsKey("" + command)) {
				Object obj = (PLCData) response.get(key);
				response.remove(key);
				if (obj == null) {
					continue;
				}
				return (PLCData) obj;
			}
			else {
				waitResponse();
				ctime = System.currentTimeMillis();

				if (((ctime - stime) / 1000) > responseTimeout) {
					log.debug("getResponse:: SESSION IDLE COUNT[" + session.getIdleCount(IdleStatus.BOTH_IDLE) + "]");
					if (session.getIdleCount(IdleStatus.BOTH_IDLE) >= retry) {
						response.remove(key);
						throw new FMPResponseTimeoutException(" tid : " + command + " Response Timeout[" + responseTimeout + "]");
					}
				}
			}
		}
		return null;
	}

	/**
	 * @return the previousRequestPLC
	 */
	public PLCDataFrame getPreviousRequestPLC() {
		return previousRequestPLC;
	}

	/**
	 * @param previousRequestPLC
	 *            the previousRequestPLC to set
	 */
	public void setPreviousRequestPLC(PLCDataFrame previousRequestPLC) {
		this.previousRequestPLC = previousRequestPLC;
	}

	/**
	 * get Protocol Type(1:CDMA,2:GSM,3:GPRS,4:PSTN,5:LAN)
	 *
	 * @return protocolType <code>Integer</code> Protocol Type
	 */
	public Integer getProtocolType() {
		return this.protocolType;
	}

	/**
	 * get Protocol Type String(1:CDMA,2:GSM,3:GPRS,4:PSTN,5:LAN)
	 *
	 * @return protocolType <code>String</code> Protocol Type
	 */
	public String getProtoName() {
		int proto = this.protocolType.intValue();

		Code code = CommonConstants.getProtocol(proto+"");
		return "[" + code.getName() + "]";
	}
}
