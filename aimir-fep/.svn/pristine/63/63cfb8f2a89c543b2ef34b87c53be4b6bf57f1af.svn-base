package com.aimir.fep.bypass;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.service.IoService;
import org.apache.mina.core.service.IoServiceListener;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.protocol.fmp.frame.ControlDataConstants;
import com.aimir.fep.protocol.fmp.frame.ControlDataFrame;
import com.aimir.fep.protocol.fmp.frame.GeneralDataConstants;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.FrameUtil;
import com.aimir.fep.util.Hex;

public class BypassService implements IoServiceListener {
	private static Log log = LogFactory.getLog(BypassService.class);
	
	private final int IDLE_TIME = Integer.parseInt(FMPProperty.getProperty(
            "protocol.idle.time","5"));
	private final int CNT_RETRY = Integer.parseInt(FMPProperty.getProperty(
            "protocol.retry","3"));
	private final int BYPASS_WAIT_TIME = Integer.parseInt(FMPProperty.getProperty(
            "protocol.bypass.waittime","3")) ;
	
	private void bypassService(IoSession session, String modemSerial) {
		try {
			Thread.sleep(BYPASS_WAIT_TIME*1000);
			log.info("====[Bypass Open]====");
			BypassRegister bs = BypassRegister.getInstance();
			
			final String fmodemSerial = modemSerial;
			// bs.excute(modemSerial, session);
		} catch (Exception e) {
			log.error(e, e);
		} finally {
			
		}
	}
	
	private void sendACK(IoSession session) {

		log.info("Send ACK");
		try {
			ControlDataFrame frame = new ControlDataFrame(
					ControlDataConstants.CODE_ACK);
			byte[] bx = frame.encode();
			byte[] crc = FrameUtil.getCRC(bx);

			IoBuffer ib = IoBuffer.allocate(bx.length + crc.length);
			ib.put(bx);
			ib.put(crc);
			ib.flip();

			session.write(ib).addListener(new IoFutureListener<WriteFuture>() {

				@Override
				public void operationComplete(WriteFuture future) {
					IoSession  session = future.getSession();
					if(session.containsAttribute("modemSerial")){
						String modemSerial = (String) session.getAttribute("modemSerial");
						bypassService(session,modemSerial);
					}
				}				
			});
			
		} catch (Exception e) {
			log.error(e, e);
		}
	}
	
	private void sendENQ(IoSession session) throws Exception{
		
		log.info("Send ENQ");
		
		ControlDataFrame frame = new ControlDataFrame(
                ControlDataConstants.CODE_ENQ);
        byte[] bx = frame.encode();
        byte[] crc = FrameUtil.getCRC(bx);
		
		IoBuffer ib = IoBuffer.allocate(bx.length+crc.length);
		ib.put(bx);
		ib.put(crc);
		ib.flip();
		session.write(ib).addListener(new IoFutureListener<IoFuture>() {

			@Override
			public void operationComplete(IoFuture future) {
				log.debug("sended ENQ");
				
				IoSession session = future.getSession();
				session.read().addListener(new IoFutureListener<ReadFuture>() {

					@Override
					public void operationComplete(ReadFuture  future) {

						byte[] receiveData=(byte[])future.getMessage();
						IoSession  session = future.getSession();
						
						if(receiveData[8]==ControlDataConstants.CODE_NEG){
				            byte[] args = null;
							try {args = DataUtil.select(receiveData, 11, 9);} catch (Exception e2) {}
				            if(args != null){
				                log.info("NEG[" + Hex.decode(args) + "]");
				            }

				            // enq 버전이 1.2 인 경우 frame size와 window size를 session에 저장한다.
				            if (args[0] == 0x01 && args[1] == 0x02) {
				                int frameMaxLen = DataUtil.getIntTo2Byte(new byte[] {args[3], args[2]});
				                int frameWinSize = DataUtil.getIntToByte(args[4]);
				                String nameSpace = "";
								try {nameSpace = new String(DataUtil.select(args, 5, 2));} catch (Exception e1) {}
								
								setSessionAttr(session, frameMaxLen, frameWinSize, nameSpace);
				                log.info("NEG V1.2 Frame Size[" + frameMaxLen + "] Window Size[" + frameWinSize + "] NameSpace["+nameSpace+"]");
				                
				                try {
				                	ControlDataFrame negrFrame = FrameUtil.getNEGR();
									session.write(negrFrame);
								} catch (Exception e1) {
									e1.printStackTrace();
								}
				                try {Thread.sleep(100);} catch (InterruptedException e) {}

				                /*
								try {
					                //if4 version, namespace별로 action분기
					                String packageName = "com.aimir.fep.bypass.actions.";
					                String className="Command_";
					                className = className + Hex.decode(DataUtil.select(args, 0, 2)) + nameSpace + "_Action";
									Bypass action = (Bypass)DataUtil.getBean(Class.forName(packageName+className));
					                // action.excute(session);
								} catch (Exception e) {
									log.error(e,e);
								}	
								*/
				            }
				            else {
				                session.setAttribute("frameMaxLen", GeneralDataConstants.FRAME_MAX_LEN);
				                session.setAttribute("frameWinSize", GeneralDataConstants.FRAME_WINSIZE);
				                ControlDataFrame negrFrame = FrameUtil.getNEGR();
				                negrFrame.setArg(new OCTET(new byte[]{ControlDataConstants.NEG_R_UNSUPPORTED_VERSION}));
				                try {
									session.write(negrFrame);
									
									if(session.containsAttribute("modemSerial")){
										String modemSerial = (String) session.getAttribute("modemSerial");
										bypassService(session,modemSerial);
									}
								} catch (Exception e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
				                try {Thread.sleep(100);} catch (InterruptedException e) {}
				                session.closeNow();
				            }
						}
						
						if(receiveData[0]==ControlDataConstants.CODE_EOT){
							int len = DataUtil.getIntTo2Byte(new byte[]{receiveData[2],receiveData[1]}); 
							
							byte[] modemId = new byte[len];
							System.arraycopy(receiveData, 3, modemId, 0, len);
							String modemSerial = Hex.decode(modemId);
							session.setAttribute("modemSerial", modemSerial);
							log.debug("Received Target ID::["+modemSerial+"]");
							sendACK(session);
						}
					}
					
				});
				
			}
			
		});
	}	
	
	private void setSessionAttr(IoSession session, int frameMaxLen, int frameWinSize, String nameSpace){
        session.setAttribute("frameMaxLen", frameMaxLen);
        session.setAttribute("frameWinSize", frameWinSize);
        session.setAttribute("nameSpace", frameWinSize);
	}

	@Override
	public void serviceActivated(IoService service) throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public void serviceIdle(IoService service, IdleStatus idleStatus)
			throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public void serviceDeactivated(IoService service) throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		sendENQ(session);
	}

	@Override
	public void sessionDestroyed(IoSession session) throws Exception {
		// TODO Auto-generated method stub

	}

    @Override
    public void sessionClosed(IoSession arg0) throws Exception {
        // TODO Auto-generated method stub
        
    }

}
