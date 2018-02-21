package com.aimir.fep.bypass;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.junit.Test;

import com.aimir.fep.protocol.fmp.datatype.SMIValue;
import com.aimir.fep.protocol.fmp.datatype.UINT;
import com.aimir.fep.protocol.fmp.exception.FMPENQTimeoutException;
import com.aimir.fep.protocol.fmp.exception.FMPException;
import com.aimir.fep.protocol.fmp.frame.ControlDataConstants;
import com.aimir.fep.protocol.fmp.frame.ControlDataFrame;
import com.aimir.fep.protocol.fmp.frame.GeneralDataConstants;
import com.aimir.fep.protocol.fmp.frame.ServiceDataConstants;
import com.aimir.fep.protocol.fmp.frame.ServiceDataFrame;
import com.aimir.fep.protocol.fmp.frame.service.CommandData;
import com.aimir.fep.protocol.fmp.frame.service.ServiceData;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.FrameUtil;
import com.aimir.fep.util.Hex;
import com.aimir.fep.util.MIBUtil;
import com.aimir.model.device.AsyncCommandParam;

public class IF4_1_2_BYPASS {
    @Test
    public void testOnDemandByMeter(){
    	NioSocketConnector connector = new NioSocketConnector();
    	IF4_1_2_BYPASS_Handler handler = new IF4_1_2_BYPASS_Handler();
        connector.setHandler(handler);
        connector.getFilterChain().addLast("bypass",
                new ProtocolCodecFilter(new ProtocolCodecFactory()
                {
                    public ProtocolDecoder getDecoder(IoSession session) throws Exception
                    {
                        return new BypassDecoder();
                    }

                    public ProtocolEncoder getEncoder(IoSession session) throws Exception
                    {
                        return new BypassEncoder();
                    }
                }
                ));
    	ConnectFuture future = connector.connect(new InetSocketAddress("localhost", 8900));
        future.awaitUninterruptibly();
        try
        {
            handler.waitENQ();
        }catch(Exception ex)
        {
        }
    }
}

class IF4_1_2_BYPASS_Handler implements IoHandler {
	private static Log log = LogFactory.getLog(IF4_1_2_BYPASS_Handler.class);
	
	private String NAME_SPACE = "GG";
	private Object enqMonitor = new Object();
    private boolean isReceivedENQ = false;
    private int enqTimeout = Integer.parseInt(
            FMPProperty.getProperty("protocol.enq.timeout","3"));
	@Override
    public void sessionCreated(IoSession session) throws Exception {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void sessionOpened(IoSession session) throws Exception {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void sessionClosed(IoSession session) throws Exception {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void sessionIdle(IoSession session, IdleStatus status)
            throws Exception {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void exceptionCaught(IoSession session, Throwable cause)
            throws Exception {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void messageReceived(IoSession session, Object message)
            throws Exception {
		log.info(message);
		if (message instanceof ControlDataFrame) {
		    ControlDataFrame cdf = (ControlDataFrame)message;
		    byte code = cdf.getCode();
	        log.info("Control Frame Code=["+code+"]");
	        if(code == ControlDataConstants.CODE_ENQ)
	        {
	            // 확장 처리해야 함.
	            byte[] args = cdf.getArg().getValue();
	            log.info("ENQ[" + Hex.decode(args) + "]");
	            // enq 버전이 1.1 인 경우 frame size와 window size를 session에 저장한다.
	            if (args[0] == 0x01 && args[1] == 0x02) {
	                int frameMaxLen = DataUtil.getIntTo2Byte(new byte[] {args[3], args[2]});
	                int frameWinSize = DataUtil.getIntToByte(args[4]);
	                session.setAttribute("frameMaxLen", frameMaxLen);
	                session.setAttribute("frameWinSize", frameWinSize);
	                log.info("ENQ V1.2 Frame Size[" + frameMaxLen + "] Window Size[" + frameWinSize + "]");
	                
	                ControlDataFrame negrFrame = FrameUtil.getNEG(NAME_SPACE);
	            	log.info("isConnected[" + session.isConnected() + "]");
					session.write(negrFrame);
	            }
	            else {
	                session.setAttribute("frameMaxLen", GeneralDataConstants.FRAME_MAX_LEN);
	                session.setAttribute("frameWinSize", GeneralDataConstants.FRAME_WINSIZE);
	            }
	        }
	        else if (code == ControlDataConstants.CODE_NEGR) {
	        	log.info("negr received");
	        }
		}
		else if (message instanceof ServiceDataFrame) {
			ServiceDataFrame sdf = (ServiceDataFrame)message;
			ServiceData sd = ServiceData.decode(NAME_SPACE, sdf, session.getRemoteAddress().toString());
			MIBUtil mu = MIBUtil.getInstance(NAME_SPACE);
			
			if (sd instanceof CommandData) {
				CommandData cd = (CommandData)sd;
				String cmd = mu.getName(cd.getCmd().getValue());
				log.info("TID[" + cd.getTid() + "]");
				log.info("CMD[" + cmd + "]");
				for (SMIValue s : cd.getSMIValue()) {
					log.info("MSG[" + s + "]");
				}
				
				response(cmd, session);
			}
		}
		else  if (message instanceof byte[]){
		    byte[] in = (byte[])message;
		    log.info("BypassFrame[" + Hex.decode(in) + "]");
		    // response
		    session.write(new byte[]{(byte)0x06});
		}
    }

	private void response(String cmd, IoSession session)
	throws Exception
	{
	    // response
        if (cmd.equals("cmdIdentifyDevice")) {
            List<SMIValue> params = new ArrayList<SMIValue>();
            params.add(DataUtil.getSMIValueByObject(NAME_SPACE, "cmdModemID", "3572470503250630"));
            params.add(DataUtil.getSMIValueByObject(NAME_SPACE, "cmdMeterID", "18981848"));
            CommandData cd = command("cmdIdentifyDevice", params);
            log.info(Hex.decode(cd.encode()));
            response(session, cd);
        }
        else if (cmd.equals("cmdOTAStart")) {
            List<SMIValue> params = new ArrayList<SMIValue>();
            params.add(DataUtil.getSMIValueByObject(NAME_SPACE, "cmdModemModel", "NAMR-P116GP"));
            params.add(DataUtil.getSMIValueByObject(NAME_SPACE, "cmdModemFwVer", "1"));
            params.add(DataUtil.getSMIValueByObject(NAME_SPACE, "cmdModemBuildNo", "1"));
            params.add(DataUtil.getSMIValueByObject(NAME_SPACE, "cmdModemHwVer", "512"));
            params.add(DataUtil.getSMIValueByObject(NAME_SPACE, "cmdModemFwSize", "988"));
            CommandData cd = command("cmdOTAStart", params);
            response(session, cd);
        }
        else if (cmd.equals("cmdSendImage")) {
            CommandData cd = command("cmdSendImage", null);
            response(session, cd);
        }
        else if (cmd.equals("cmdOTAEnd")) {
            List<SMIValue> params = new ArrayList<SMIValue>();
            params.add(DataUtil.getSMIValueByObject(NAME_SPACE, "cmdUpgradeStatus", "0"));
            CommandData cd = command("cmdOTAEnd", params);
            response(session, cd);
            session.write(new ControlDataFrame(ControlDataConstants.CODE_EOT));
        }
        else if (cmd.equals("cmdModemResetInterval")) {
            CommandData cd = command("cmdModemResetInterval", null);
            response(session, cd);
            session.write(new ControlDataFrame(ControlDataConstants.CODE_EOT));
        }
        else if (cmd.equals("cmdSetApn")) {
            CommandData cd = command("cmdSetApn", null);
            response(session, cd);
            session.write(new ControlDataFrame(ControlDataConstants.CODE_EOT));
        }
        else if (cmd.equals("cmdSetMeteringInterval")) {
            CommandData cd = command("cmdSetMeteringInterval", null);
            response(session, cd);
            session.write(new ControlDataFrame(ControlDataConstants.CODE_EOT));
        }
        else if (cmd.equals("cmdSetServerIpPort")) {
            CommandData cd = command("cmdSetServerIpPort", null);
            response(session, cd);
            session.write(new ControlDataFrame(ControlDataConstants.CODE_EOT));
        }
        else if (cmd.equals("cmdFactorySetting")) {
            CommandData cd = command("cmdFactorySetting", null);
            response(session, cd);
            session.write(new ControlDataFrame(ControlDataConstants.CODE_EOT));
        }
        else if (cmd.equals("cmdSetBypassStart")) {
        }
	}
	
	@Override
    public void messageSent(IoSession session, Object message) throws Exception {
	    // TODO Auto-generated method stub
	    
    }
	
	public void waitENQ() throws FMPException
    {
        /*
        synchronized(enqMonitor)
        {
            if(isReceivedENQ) {
                return;    
            } else {
                try{ 
                    //log.debug("enqMonitor Wait");
                    enqMonitor.wait();
                    //log.debug("enqMonitor Received");
                }catch(InterruptedException ie)
                { ie.printStackTrace(); }
            }
        }
        */
        log.debug("wait start");
        int waitEnqCnt = 0;
        while(!isReceivedENQ)
        {
            synchronized(enqMonitor)
            {
                try {enqMonitor.wait(500); }
                catch(Exception ex) {}
            }
            waitEnqCnt++;
            if((waitEnqCnt / 2) > enqTimeout)
            { 
                throw new FMPENQTimeoutException(
                        "ENQ Wait Timeout[" +enqTimeout +"]");
            }
        }
        log.debug("wait end");
    }
	
    private CommandData command(String cmdName, List<?> params){
		MIBUtil mu = MIBUtil.getInstance(NAME_SPACE);
		CommandData command = new CommandData();
		command.setCmd(mu.getMIBNodeByName(cmdName).getOid());
		if (params != null) {
			for (int i = 0; i < params.size(); i++) {
				Object obj = params.get(i);
				log.info(i);
				if (obj instanceof SMIValue) {
					command.append((SMIValue) obj);
				} else if(obj instanceof AsyncCommandParam){
					AsyncCommandParam param = (AsyncCommandParam) obj;
					SMIValue smiValue;
					try {
						smiValue = DataUtil.getSMIValueByOid(NAME_SPACE, param.getParamType(), param.getParamValue());
						command.append(smiValue);
					} catch (Exception e) {
						log.error(e,e);
					}

				}
			}
		}
		return command;
	}
	
	private void response(IoSession session, CommandData command)
	        throws Exception
    {
        try {
        	command.setAttr(ServiceDataConstants.C_ATTR_RESPONSE);
            command.setTid(FrameUtil.getCommandTid());
            
            ServiceDataFrame frame = new ServiceDataFrame();
            long mcuId = 0L;
            frame.setMcuId(new UINT(mcuId));
            frame.setSvc(GeneralDataConstants.SVC_C);
            frame.setAttr((byte)(GeneralDataConstants.ATTR_START | GeneralDataConstants.ATTR_END));
            frame.setSvcBody(command.encode());
            session.write(frame);
            /*
            sd = handler.getResponse(session,command.getTid().getValue());
            long e = System.currentTimeMillis();
            if(sd == null) 
                return null;
            log.debug("Received Response TID : " 
                    + ((CommandData)sd).getTid());

            return (CommandData)sd;
            */
        }
        catch(Exception ex)
        {
        	log.error(ex, ex);
            if(!command.getCmd().toString().equals("198.3.0"))
            {
                log.error("sendCommand failed : command["+command+"]",ex);
            } else {
                log.error("sendCommand failed : command["+command.getCmd()
                        +"]",ex);
            }
            throw ex;
        }
    }

    @Override
    public void inputClosed(IoSession arg0) throws Exception {
        // TODO Auto-generated method stub
        
    }
}
