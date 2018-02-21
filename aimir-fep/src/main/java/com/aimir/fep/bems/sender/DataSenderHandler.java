package com.aimir.fep.bems.sender;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

/**
 * MeteringDataSender 통신 Handler
 * @author kskim
 */
public class DataSenderHandler implements IoHandler {

	private static final int IDLE_CNT = 3;

	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		// TODO Auto-generated method stub
		System.out.println(message);

	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception {
		if(session.getIdleCount(IdleStatus.BOTH_IDLE)>=IDLE_CNT){
			session.closeNow();
		}

	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		// TODO Auto-generated method stub

	}

    @Override
    public void inputClosed(IoSession session) throws Exception {
    	session.closeNow();
    }

}
