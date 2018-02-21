package com.aimir.fep.protocol.fmp.client.lan;

import java.net.InetSocketAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.aimir.constants.CommonConstants;
import com.aimir.fep.meter.parser.plc.PLCData;
import com.aimir.fep.meter.parser.plc.PLCDataConstants;
import com.aimir.fep.meter.parser.plc.PLCDataFrame;
import com.aimir.fep.protocol.fmp.client.Client;
import com.aimir.fep.protocol.fmp.client.PLCClientProtocolHandler;
import com.aimir.fep.protocol.fmp.client.PLCClientProtocolProvider;
import com.aimir.fep.protocol.fmp.common.LANTarget;
import com.aimir.fep.protocol.fmp.common.Target;
import com.aimir.fep.protocol.fmp.datatype.UINT;
import com.aimir.fep.protocol.fmp.frame.ErrorCode;
import com.aimir.fep.protocol.fmp.frame.GeneralDataConstants;
import com.aimir.fep.protocol.fmp.frame.ServiceDataFrame;
import com.aimir.fep.protocol.fmp.frame.service.AlarmData;
import com.aimir.fep.protocol.fmp.frame.service.CommandData;
import com.aimir.fep.protocol.fmp.frame.service.EventData;
import com.aimir.fep.protocol.fmp.frame.service.MDData;
import com.aimir.fep.protocol.fmp.frame.service.RMDData;
import com.aimir.fep.protocol.fmp.processor.ProcessorHandler;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.model.device.CommLog;
import com.aimir.util.TimeUtil;

/**
 * MCU PLC TCP Packet Client
 * @author kaze
 * 2009. 7. 6.
 */
public class LANPLCClient implements Client
{
    private Log log = LogFactory.getLog(LANPLCClient.class);
    private LANTarget target = null;
    private int CONNECT_TIMEOUT = Integer.parseInt(FMPProperty.getProperty("Protocol.connection.timeout", 30+"")); // seconds
    private IoConnector connector = null;
    private PLCClientProtocolProvider provider = null;
    private IoSession session = null;
    private ProcessorHandler plcProcessor = null;
    private ProcessorHandler logProcessor = null;
    private Integer activatorType = new Integer(
            FMPProperty.getProperty("Protocol.SystemType.FEP","1"));
    private Integer targetType = new Integer(
            FMPProperty.getProperty("Protocol.SystemType.MCU","2"));
    private Integer protocolType = new Integer(
            FMPProperty.getProperty("Protocol.Type.PLC","9"));


    /**
	 * @return the plcProcessor
	 */
	public ProcessorHandler getPlcProcessor() {
		return plcProcessor;
	}

	/**
	 * @param plcProcessor the plcProcessor to set
	 */
	public void setPlcProcessor(ProcessorHandler plcProcessor) {
		this.plcProcessor = plcProcessor;
	}

	/**
     * constructor
     */
    public LANPLCClient()
    {
        init();
    }

    /**
     * constructor
     * @param target <code>TcpTarget</code> target
     */
    public LANPLCClient(LANTarget target)
    {
        this.target = target;
        init();
    }

    /**
     * initialize
     * create IoProtocolConnector
     * create FMPClientProtocolProvider
     */
    private void init()
    {
        connector = new NioSocketConnector();
        provider = new PLCClientProtocolProvider();
        if (!connector.getFilterChain().contains(getClass().getName())) {
			connector.getFilterChain().addLast(getClass().getName(),
                    new ProtocolCodecFilter(provider.getCodecFactory()));
		}
        connector.setHandler(provider.getHandler());
    }

    public synchronized void connect(boolean meterConnect) throws Exception
    {
    	connect();
    }

    /**
     * connect to Target and wait ENQ
     *
     * @throws Exception
     */
    public synchronized void connect() throws Exception
    {
        if(session != null && session.isConnected()) {
			return;
		}

        for( ;; )
        {
            try
            {
                connector.setConnectTimeoutMillis(CONNECT_TIMEOUT*1000);
                ConnectFuture future = connector.connect(new InetSocketAddress(
                        target.getIpAddr(),
                        target.getPort()));
                future.awaitUninterruptibly();

                if (!future.isConnected()) {
                    throw new Exception("not yet");
                }

                session = future.getSession();
                log.debug("SESSION CONNECTED[" + session.isConnected() + "]");

                break;
            }
            catch( Exception e )
            {
            	/*
                try { LookupUtil.updateMCUNetworkStatus(
                        target.getMcuId(),"0");
                }catch(Exception ex){log.error(ex,ex);}
                */
                log.error( "Failed to connect. host["
                        + target.getIpAddr()+"] port["
                        + target.getPort()+"]",e );
                throw e;
            }
        }

        if(session == null) {
			throw new Exception("Failed to connect. host["
                        + target.getIpAddr()+"] port["
                        + target.getPort()+"]");
		}

        PLCClientProtocolHandler handler =
            (PLCClientProtocolHandler)session.getHandler();
        handler.setResponseTimeout(target.getTimeout());
        log.debug("Handler timeout set["+ target.getTimeout() + "]");
        /*
        try
        {
            handler.waitENQ();
        }catch(Exception ex)
        {
            close();
            throw ex;
        }
        */
    }

    private void saveCommLog(CommLog commLog)
    {
        try
        {
            if(this.logProcessor != null) {
				this.logProcessor.putServiceData(ProcessorHandler.LOG_COMMLOG, commLog);
			}
			else {
				log.warn("Log Processor not registered");
			}
        } catch(Exception ex)
        {
            log.error("save Communication Log failed",ex);
        }
    }

    private void savePLCData(PLCData responsePd)
    {
        try
        {
            if(this.plcProcessor != null) {
				this.plcProcessor.putServiceData(ProcessorHandler.LOG_COMMLOG, responsePd);
			}
			else {
				log.warn("PLC Processor not registered");
			}
        } catch(Exception ex)
        {
            log.error("save PLC Data failed",ex);
        }
    }

    public void setLogProcessor(ProcessorHandler logProcessor)
    {
        this.logProcessor = logProcessor;
    }

    /**
     * set Target
     *
     * @param target <code>TcpTarget</code> target
     */
    public void setTarget(Target target) throws Exception
    {
        if(!(target instanceof LANTarget)) {
			throw new Exception("not supported target");
		}
        this.target = (LANTarget)target;
    }

    /**
     * request command data to Target and response
     *
     * @param pdf <code>CommandData</code> request command
     * @return response <code>ServiceData</code> response
     * @throws Exception
     */
    public PLCDataFrame sendCommand(PLCDataFrame pdf)
        throws Exception
    {
        //ProtocolSession session = connect();
        if(session == null || !session.isConnected()) {
			connect();
		}
        CommLog commLog = new CommLog();
        //TODO mcuid의 Max값을 체크할 필요가 있을까?
//        long mcuId = Long.parseLong(target.getMcuId());
//        if(mcuId > MAX_MCUID)
//            throw new Exception("mcuId is too Big: max["
//                    + MAX_MCUID+"]");
        //pd.setAttr(ServiceDataConstants.C_ATTR_REQUEST);
        //pd.setTid(FrameUtil.getCommandTid());

        PLCClientProtocolHandler handler =
            (PLCClientProtocolHandler)session.getHandler();
        PLCData responsePd=null;
        String startTime = TimeUtil.getCurrentTime();
        commLog.setStartDate(startTime.substring(0,8));
        commLog.setStartTime(startTime.substring(8,14));

        commLog.setProtocolCode(CommonConstants.getProtocol(protocolType+""));
        commLog.setSenderTypeCode(CommonConstants.getSenderReceiver(activatorType+""));
        commLog.setSenderId(DataUtil.getFepIdString());

        commLog.setReceiverTypeCode(CommonConstants.getSenderReceiver(targetType+""));
        commLog.setReceiverId(target.getTargetId());

        long s = System.currentTimeMillis();
        commLog.setSendBytes(send(pdf));

        //각각의 명령어 쌍에 맞는 응답을 가져옴
        if(pdf.getCommand()==PLCDataConstants.COMMAND_B) {
        	responsePd = handler.getResponse(session,PLCDataConstants.COMMAND_b);
        }
        else if(pdf.getCommand()==PLCDataConstants.COMMAND_C) {
        	responsePd = handler.getResponse(session,PLCDataConstants.COMMAND_b);
        }
        else if(pdf.getCommand()==PLCDataConstants.COMMAND_D) {
        	responsePd = handler.getResponse(session,PLCDataConstants.COMMAND_d);
        }
        else if(pdf.getCommand()==PLCDataConstants.COMMAND_E) {
        	responsePd = handler.getResponse(session,PLCDataConstants.COMMAND_e);
        }
        else if(pdf.getCommand()==PLCDataConstants.COMMAND_F) {
        	responsePd = handler.getResponse(session,PLCDataConstants.COMMAND_f);
        }
        else if(pdf.getCommand()==PLCDataConstants.COMMAND_G) {
        	responsePd = handler.getResponse(session,PLCDataConstants.COMMAND_g);
        }
        else if(pdf.getCommand()==PLCDataConstants.COMMAND_H) {
        	responsePd = handler.getResponse(session,PLCDataConstants.COMMAND_h);
        }
        else if(pdf.getCommand()==PLCDataConstants.COMMAND_I) {
        	responsePd = handler.getResponse(session,PLCDataConstants.COMMAND_i);
        }
        else if(pdf.getCommand()==PLCDataConstants.COMMAND_J) {
        	responsePd = handler.getResponse(session,PLCDataConstants.COMMAND_j);
        }
        else if(pdf.getCommand()==PLCDataConstants.COMMAND_M) {
        	responsePd = handler.getResponse(session,PLCDataConstants.COMMAND_m);
        }
        else if(pdf.getCommand()==PLCDataConstants.COMMAND_N) {
        	responsePd = handler.getResponse(session,PLCDataConstants.COMMAND_m);
        }

        long e = System.currentTimeMillis();
        if(responsePd == null) {
			return null;
		}
        log.debug("Received Response COMMAND : "+ ((PLCData)responsePd).getCommand());
        commLog.setOperationCode("PLCCommand."+String.valueOf((char)pdf.getCommand()));
        commLog.setEndTime(TimeUtil.getCurrentTime());
        commLog.setRcvBytes(responsePd.getTotalLength());
        commLog.setUnconPressedRcvBytes(responsePd.getTotalLength());        
        commLog.setTotalCommTime((int)(e-s));        
        commLog.setSvcTypeCode(CommonConstants.getDataSvc(responsePd.getServiceType()+""));
        if(((PLCDataFrame)responsePd).getErrCode() > 0)
        {
        	commLog.setCommResult(0);
            commLog.setDescr(ErrorCode.getMessage(((PLCData)responsePd).getErrCode()));
        }
        else
        {
            commLog.setCommResult(1);
        }
        log.info(commLog.toString());
        saveCommLog(commLog);
        savePLCData(responsePd);
        return responsePd;
    }
    
    public void sendPLCDataFrame(PLCDataFrame pdf) throws Exception
    {
		try {
			if (session == null || !session.isConnected())
				connect();		
			PLCClientProtocolHandler handler =
	            (PLCClientProtocolHandler)session.getHandler();
			send(pdf);
			log.info("sendPLCDataFrame : finished");
		} catch (Exception ex) {
			throw ex;
		} finally {
			close();
		}
    }

    /**
     * close TCPClient session
     */
    public void close()
    {
        close(false);
    }

    /**
     * close TCPClient session and wait completed
     *
     * @param immediately <code>boolean</code> wait
     */
    public void close(boolean immediately)
    {
        if(session != null) {
            if (session.isConnected()) {
				session.getCloseFuture().awaitUninterruptibly();
			}
            session.closeNow();
        }

        if(connector != null) {
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
    public boolean isConnected()
    {
        if(session == null) {
			return false;
		}
        return session.isConnected();
    }

    /**
     * send GeneralDataFrame to Target
     *
     * @param frame <code>GeneralDataFrame</code>
     */
    private synchronized int send(PLCDataFrame pdf)
        throws Exception
    {
        int sendBytes = 0;
        session.write(pdf);
        sendBytes = PLCDataConstants.SOF_LEN+PLCDataConstants.HEADER_LEN+(pdf.getData()!=null ? pdf.getData().length:0)+PLCDataConstants.CRC_LEN+PLCDataConstants.EOF_LEN;
        return sendBytes;
    }

	/* (non-Javadoc)
	 * @see nuri.aimir.moa.protocol.fmp.client.Client#sendCommand(nuri.aimir.moa.protocol.fmp.frame.service.CommandData)
	 */
	public CommandData sendCommand(CommandData commandData) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see nuri.aimir.moa.protocol.fmp.client.Client#sendAlarm(nuri.aimir.moa.protocol.fmp.frame.service.AlarmData)
	 */
	public void sendAlarm(AlarmData alarmData) throws Exception {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see nuri.aimir.moa.protocol.fmp.client.Client#sendEvent(nuri.aimir.moa.protocol.fmp.frame.service.EventData)
	 */
	public void sendEvent(EventData eventData) throws Exception {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see nuri.aimir.moa.protocol.fmp.client.Client#sendMD(nuri.aimir.moa.protocol.fmp.frame.service.MDData)
	 */
	public void sendMD(MDData mdData) throws Exception {
		// TODO Auto-generated method stub

	}
	
	/**
     * send R Measurement Data to Target 
     *
     * @param md <code>RMDData</code>R Measurement Data
     * @throws Exception
     */
    public void sendRMD(RMDData rmd) throws Exception
    {
    }
}