package com.aimir.fep.protocol.fmp.server;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.filterchain.IoFilterEvent;
import org.apache.mina.core.session.IoEventType;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.executor.ExecutorFilter;

import com.aimir.fep.protocol.fmp.frame.ControlDataFrame;
import com.aimir.fep.protocol.fmp.frame.GeneralDataFrame;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FrameUtil;

/**
 * I/O Thread Pool
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */

public class FMPIoThreadPoolFilter extends ExecutorFilter
{
    private static Log log = LogFactory.getLog(FMPIoThreadPoolFilter.class);
    private byte[] lengthField = new byte[4];

    public void dataRead(IoFilter.NextFilter
            nextFilter, IoSession session, IoBuffer buf)
    {
        //log.debug(this.getClass().getName() + " : " + "dataRead");
        boolean isValidFrameLen = FrameUtil.checkLength(buf);
        boolean isValidFrameCrc = FrameUtil.checkCRC(buf);
        if(!isValidFrameLen || !isValidFrameCrc)
        {
            int seq = DataUtil.getIntToByte(buf.get(1));
            lengthField[0] = buf.get(3);
            lengthField[1] = buf.get(4);
            lengthField[2] = buf.get(5);
            lengthField[3] = buf.get(6);
            DataUtil.convertEndian(lengthField);
            int length = DataUtil.getIntTo4Byte(lengthField);
            log.info("it is invalid frame");
            log.info("isValidFrameLen : " + isValidFrameLen);
            log.info("isValidFrameCrc : " + isValidFrameCrc);
            log.info("Received Data : "+buf.getHexDump()); 
            try
            {
                ControlDataFrame frame = FrameUtil.getNAK(buf.get(1));
                byte[] bx = frame.encode(); 
                IoBuffer bbuf = IoBuffer.allocate(bx.length); 
                bbuf.put(bx,0,bx.length); 
                bbuf.put(FrameUtil.getCRC(bx));
                bbuf.flip(); 
                session.write(bbuf,null);
            } catch(Exception ex)
            {
                log.error("FMPIoThreadPoolFilter send NAK failed",ex);
            }
            return;
        }
        // buf.acquire();
        // fireEvent(nextFilter,session,IoEventType.MESSAGE_RECEIVED,buf);
        fireEvent(new IoFilterEvent(nextFilter, IoEventType.MESSAGE_RECEIVED, session, buf));
    }
}
