package com.aimir.fep.protocol.nip.client;

import java.net.InetAddress;
import java.util.Hashtable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.aimir.fep.protocol.fmp.frame.ControlDataFrame;
import com.aimir.fep.protocol.nip.frame.GeneralFrame;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.FrameControl_Ack;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.FrameControl_Pending;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.FrameOption_AddressType;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.FrameOption_NetworkStatus;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.FrameOption_Type;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.NetworkType;
import com.aimir.fep.protocol.nip.frame.payload.Ack;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.FrameUtil;

public class NiUdpHandler extends IoHandlerAdapter{
    private static Log log = LogFactory.getLog(NiUdpHandler.class);
    private Hashtable response = new Hashtable();
	 
    private ControlDataFrame ack = null;

    private Object msg = null;
    private Object resMonitor = new Object();
    private Object msgMonitor = new Object();

    private int responseTimeout = 15;
	 
    long key = 0;
    private static int step=1;   
 	  
    @Override
    public void exceptionCaught(IoSession session, Throwable cause)
            throws Exception
    {
        log.error(cause, cause);
        session.closeOnFlush();
    }
  
    @Override
    public void messageReceived(IoSession session, Object message)
            throws Exception
    {
        try {
            if (message instanceof IoBuffer) {
                receivedServiceData(session,message);
            }
            else {
                synchronized(msgMonitor) {
                    msg = message;
                    msgMonitor.notify();
                }
            }   
        }
        catch(Exception ex)
        {
            log.error("messageReceived "+ " failed" ,ex);
        }
    }
      
    public void receivedServiceData(IoSession session, Object message) throws Exception{
        if(response.isEmpty()){
            key =session.getId();
        }
        else {
            key++;
        }
        log.debug("[receivedServiceData]"+key);
        response.put(key,message);
          
        if(step < 3){
            session.write((byte[])message);
            step++;
        }
    }
      
    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        log.debug("[NICL][SENT]"+session.getId());
    }
  
    @Override
    public void sessionClosed(IoSession session) throws Exception {
        log.debug("[NICL][CLOSE]"+session.getId());
        log.debug("[NICL][CLOSE][Total ReadBytes] " + session.getReadBytes() + " byte(s)");
        session.closeOnFlush();
    }
  
    @Override
    public void sessionCreated(IoSession session) throws Exception {
        log.debug("[NICL][CREATE]"+session.getId());
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status)
            throws Exception {
        log.debug("[NICL][IDLE]"+session.getId());
        if (status == IdleStatus.READER_IDLE) {
        	session.closeOnFlush();
        }
    }
 
    @Override
    public void sessionOpened(IoSession session) throws Exception {
        log.debug("[NICL][OPEN]"+session.getId());
        // Set reader idle time to 10 seconds.
        // sessionIdle(...) method will be invoked when no data is read
        // for 10 seconds.
        session.getConfig().setIdleTime(IdleStatus.READER_IDLE, 15);//15초
    }
      
    public byte[] getResponse(IoSession session,long tid)
            throws Exception
    {
        long key = tid;
        long stime = System.currentTimeMillis();
        long ctime = 0;
        int waitResponseCnt = 0;
        while(session.isConnected())
        { 
//	        	System.out.println("[NICL][CONNECTED]"+session.getId());
            if(response.containsKey(key)) 
            { 
                byte[] obj = ((IoBuffer)response.get(key)).array(); 
                response.remove(key); 
                if(obj == null) 
                    continue; 
                return obj; 
            } 
            else
            {
                waitResponse();
                ctime = System.currentTimeMillis();
                if(((ctime - stime)/1000) > responseTimeout)
                {
                    log.debug("getResponse:: SESSION IDLE COUNT["+session.getIdleCount(IdleStatus.BOTH_IDLE)+"]");
                    response.remove(key); 
                    throw new Exception("[NICL][TID : " + key +"],[Response Timeout:"+responseTimeout +"]");
                }
            }
        }
        return null;
    }
      
    /**
     * wait util received command response data
     */
    public void waitResponse()
    {
        synchronized(resMonitor)
        { 
            try { resMonitor.wait(500); 
            } catch(InterruptedException ie) {ie.printStackTrace();}
        }
    }
      
    public int getAckSequence()
    {
        if(this.ack == null)
            return -1;
        return FrameUtil.getAckSequence(this.ack);
    }
    
    private byte[] ack(IoSession session) throws Exception {
        GeneralFrame frame = new GeneralFrame();
        frame._networkType = NetworkType.Ethernet;
        frame.fcPending = FrameControl_Pending.LastFrame;
        frame.fcAck = FrameControl_Ack.None;
        frame.foNetworkStatus = FrameOption_NetworkStatus.None;
        frame.foAddrType = FrameOption_AddressType.SrcDest;
        frame.foType = FrameOption_Type.Ack;
        frame.setSeqNumber(new byte[]{DataUtil.getByteToInt(0)});
        frame.setPayload(new Ack());
        
        InetAddress srcaddr = InetAddress.getByName(FMPProperty.getProperty("fep.ipv6.addr"));
        frame.setSrcAddress(srcaddr.getAddress());
        frame.setDstAddress(InetAddress.getByName(session.getRemoteAddress().toString()).getAddress());
        
        return frame.encode(null);
    }
}
