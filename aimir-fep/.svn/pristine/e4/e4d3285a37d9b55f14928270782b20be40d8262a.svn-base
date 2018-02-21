package com.aimir.fep.protocol.fmp.client;

import java.net.InetSocketAddress;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.aimir.constants.CommonConstants.McuType;
import com.aimir.constants.CommonConstants.MeterModel;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.fep.BaseTestCase;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.protocol.fmp.command.CommandProxy;
import com.aimir.fep.protocol.fmp.common.LANTarget;
import com.aimir.fep.protocol.fmp.datatype.BYTE;
import com.aimir.fep.protocol.fmp.datatype.INT;
import com.aimir.fep.protocol.fmp.datatype.OID;
import com.aimir.fep.protocol.fmp.datatype.SMIValue;
import com.aimir.fep.protocol.fmp.exception.FMPMcuException;

import com.aimir.fep.protocol.fmp.frame.service.CommandData;
import com.aimir.fep.protocol.fmp.processor.ProcessorHandler;
import com.aimir.fep.protocol.mrp.client.MRPClientProtocolHandler;
import com.aimir.fep.protocol.mrp.client.MRPClientProtocolProvider;
import com.aimir.fep.protocol.mrp.client.lan.LANMMIUClient;

import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.fep.util.MIBUtil;


public class Mk6_Test extends BaseTestCase {
	static {
        DataUtil.setApplicationContext(new ClassPathXmlApplicationContext(new String[]{"/config/spring.xml"}));
    }
    private static Log log = LogFactory.getLog(Mk6_Test.class);
    private static String MCUID="";
    
    private static LANTarget target;
    private static ProcessorHandler handler = null;
    
    @Test
    public void ondemandTest(){
    		CommandGW cgw = new CommandGW();
    		try {
    			cgw.cmdOnDemandMeter("", "207740751", "17511", "0", "20120328000000", "20120328000000");
    			
    		} catch (FMPMcuException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (Exception e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		
    }
    
    @Ignore
    public void setUp(){
    	target = new LANTarget("187.1.30.237", 7001);//187.1.30.237
        target.setProtocol(Protocol.LAN);
        target.setTargetType(McuType.Converter);
        target.setIpAddr("187.1.30.237");
        target.setPort(7001);
        target.setTargetId("4192");
        target.setMeterModel(MeterModel.EDMI_Mk6N.getCode());
        
        /*
        try {
			handler = new ProcessorHandler(
			        DataUtil.getBean(CommLogProcessor.class),
			        ProcessorHandler.LOG_COMMLOG);
		} catch (BeansException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/

    }
    
    
    @Ignore
    public void client_mx2(){
        try {
        	
            LANMMIUClient client = (LANMMIUClient)ClientFactory.getClient(target);
            CommandData command = new CommandData();
            command.setMcuId(target.getTargetId());
            MIBUtil mu = MIBUtil.getInstance();
            command.setCmd(mu.getMIBNodeByName("cmdOnDemandMeter").getOid());
            
            SMIValue modemNumber = DataUtil.getSMIValueByObject("sensorID", "PS110-110400200");
            command.append(modemNumber);
            SMIValue noption = DataUtil.getSMIValue(new BYTE(0));
            command.append(noption);
    		SMIValue offset = DataUtil.getSMIValue(new INT(0));
            command.append(offset);
    		SMIValue count = DataUtil.getSMIValue(new INT(0));
            command.append(count);
            client.sendCommand(command);
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    @Ignore
    public void client_Test(){
    	try {
    		
    		IoConnector connector = new NioSocketConnector();
    		MRPClientProtocolProvider provider = new MRPClientProtocolProvider();
            if (!connector.getFilterChain().contains(LANMMIUClient.class.getName()))
                connector.getFilterChain().addLast(LANMMIUClient.class.getName(), 
                        new ProtocolCodecFilter(provider.getCodecFactory()));
            connector.setHandler(provider.getHandler());
            IoSession session = null;
            for( ;; )
            {
                try
                {
                    connector.setConnectTimeoutMillis(30*1000);
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
                    log.error( "Failed to connect. host["
                            + target.getIpAddr()+"] port["
                            + target.getPort()+"]",e );
                    throw e;
                }
            }
    		
            
            byte[] send = new byte[]{(byte) 0xee,0x00,0x00,0x00,0x00,0x01,0x20,0x13,0x10};
            IoBuffer buf = IoBuffer.allocate(send.length);
    		buf.put(send);
    		buf.flip();
            session.write(buf);
    		Thread.sleep(200);
            ///
            MRPClientProtocolHandler handler = (MRPClientProtocolHandler) connector.getHandler();
            byte[] read = (byte[]) handler.getMsg(session, 30, 9);
            log.debug(Hex.getHexDump(read));
			
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    @SuppressWarnings("unchecked")
    @Ignore
    public void test_login()
    {
    	try {
    		CommandProxy commandProxy = new CommandProxy();
    		
    		
    		Vector nodes = new Vector();

    		SMIValue smiValue2 = new SMIValue(new OID("104.6.0"));
    		
    		INT command = new INT(0);
    		smiValue2.setVariable(command);
    		
    		nodes.add(smiValue2);
    		nodes.add(smiValue2);
    		
    		//command
    		@SuppressWarnings("unused")
			SMIValue ssmiValue = (SMIValue) commandProxy.execute(target, "cmdOnDemandMeter", nodes);

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    
    }
}
