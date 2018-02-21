package com.aimir.fep.protocol.fmp.client;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.junit.Ignore;
import org.junit.Test;

import com.aimir.fep.meter.file.CSV;
import com.aimir.fep.meter.parser.MX2;
import com.aimir.fep.meter.parser.MX2Table.TOUCalendar;
import com.aimir.fep.meter.parser.MX2Table.TOUCalendarBuilder;
import com.aimir.fep.meter.parser.MX2Table.VOBuilder;
import com.aimir.fep.protocol.mrp.client.MRPClientProtocolProvider;
import com.aimir.fep.protocol.mrp.protocol.MX2_Handler;
import com.aimir.fep.util.Hex;

public class MX2Client_Test {
	private static Log log = LogFactory.getLog(MX2Client_Test.class);
	public IoSession getSession(String ipaddr, Integer port) throws Exception {
		IoConnector connector = null;
		connector = new NioSocketConnector();
		MRPClientProtocolProvider provider = new MRPClientProtocolProvider();
		if (!connector.getFilterChain().contains(getClass().getName()))
			connector.getFilterChain().addLast(getClass().getName(),
					new ProtocolCodecFilter(provider.getCodecFactory()));
		connector.setHandler(provider.getHandler());

		connector.setConnectTimeoutMillis(30 * 1000);
		ConnectFuture future = connector.connect(new InetSocketAddress(ipaddr,
				port));
		future.awaitUninterruptibly();

		if (!future.isConnected()) {
			throw new Exception("not yet");
		}

		IoSession session = future.getSession();

		return session;

	}
	
	@Ignore
	public void readMEA_Number() throws Exception{
		IoSession session = getSession("10.33.10.10",7001);
		MX2_Handler handler = new MX2_Handler();
		
		handler.init();
		
		Method m = MX2_Handler.class.getDeclaredMethod("sapReadWithHeader", IoSession.class);
		m.setAccessible(true);
		byte[] data = (byte[])m.invoke(handler, session);
		
		byte[] meaByte = new byte[18];
		
		System.arraycopy(data, 4, meaByte, 0, 18);
		
		String meaNumStr = new String(meaByte);
		
		System.out.println(String.format("[%s]", meaNumStr));
		
	}
	
	public void touSettingTest() throws Exception{
		
		File f = new File("c:/tou.csv");
		
		FileInputStream fs  = new FileInputStream(f);
		
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		
		byte[] buffer = new byte[1024];
		
		while(fs.read(buffer)!=-1){
			bo.write(buffer);
		}
		
		byte[] touBinary = bo.toByteArray();
		
		
		
		
		CSV csv = new CSV(touBinary);
		VOBuilder<?> builder = new TOUCalendarBuilder();
		
		Object touCalendar = builder.build(csv);
		
		IoSession session = getSession("10.33.10.10",7001);
		MX2_Handler handler = new MX2_Handler();
		
		handler.init();
		
		Method m = MX2_Handler.class.getDeclaredMethod("setTOUCalendar", IoSession.class,TOUCalendar.class);
		m.setAccessible(true);
		m.invoke(handler, session,touCalendar);
		
		
	}
	
	Socket socket = null;
	@Test
	public void DisplayItemSetting_Test() throws Exception{
		
//		IoSession session = getSession("10.33.10.10",7001);
//		IoSession session = getSession("187.1.30.236",7001);
		MX2_Handler handler = new MX2_Handler();
		
        
        try {
        	socket = new Socket();
        	connect();
			Method m = MX2_Handler.class.getDeclaredMethod("settingReadWithHeader", Socket.class);
			m.setAccessible(true); 
			byte[] data = (byte[])m.invoke(handler, socket);
			byte[] meaByte = new byte[18];
			
			System.arraycopy(data, 4, meaByte, 0, 18); 
			
			String meaNumStr = new String(meaByte);
			
			log.debug(Hex.getHexDump(data));
			log.debug("========================meaNumStr==========================");
			System.out.println(meaNumStr);
        } 
        finally {
        	if (socket != null) socket.close();
        }
		
	}
	
	public void connect() throws Exception
    {
        if(socket != null && socket.isConnected())
            return;

        for( ;; )
        {
            try
            {
            	socket.connect( new InetSocketAddress(
	                       "187.1.30.236",
	                        7001), 300*1000);
		
                log.info("SESSION CONNECTED[" + socket.isConnected() + "] LOCAL ADDRESS[" + socket.getLocalSocketAddress() + "]");
                
                break;
            }
            catch( Exception e )
            {
                log.error( "Failed to connect. host["
                        + "10.33.10.10"+"] port["
                        + 7001+"]",e );
                throw e;
            }
        }

        if(socket == null)
            throw new Exception("Failed to connect. host["
                        + "10.33.10.10"+"] port["
                        + 7001+"]");
    }
	
	public void connectionByIpAddr() throws Exception{
		try{
		IoSession session = getSession("10.33.10.9",7001);
		MX2_Handler handler = new MX2_Handler();
		
		handler.init();
		
		Method m = MX2_Handler.class.getDeclaredMethod("sapReadWithHeader", IoSession.class);
		m.setAccessible(true);
		byte[] resultData  = (byte[])m.invoke(handler, session);

		
		MX2 parser = new MX2();
		
		parser.parse(resultData);
		
		
		
		
		File outputFile = new File("c:/test_output.txt");
		
		FileOutputStream fo = new FileOutputStream(outputFile);
		
		fo.write(resultData);
		
		fo.flush();
		fo.close();
		
		session.closeNow();
		
		assertTrue(true);
		
		}catch(Exception e){
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	/**
	 * @throws Exception 
	 * @deprecated 주의! 데이터가 모두 삭제됨
	 */
	@Ignore
	public void clearData_Test() throws Exception{
		IoSession session = getSession("187.1.30.237",7001);
		MX2_Handler handler = new MX2_Handler();
		
		handler.init();
		//handler.clearData(session);
		handler.eventlogReadWithHeader();
	}
	
	@Ignore
	public void connectionTest() throws Exception{
		try{
		IoSession session = getSession("187.1.30.237",7001);
		MX2_Handler handler = new MX2_Handler();
		
		handler.init();
		session.closeNow();
		
		assertTrue(true);
		
		}catch(Exception e){
			e.printStackTrace();
			assertTrue(false);
		}
	}
}
