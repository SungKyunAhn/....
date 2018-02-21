package com.aimir.fep.protocol.mrp.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderAdapter;
import org.apache.mina.filter.codec.ProtocolDecoderException;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

/**
 * Decodes MRP GeneralDataFrame into input stream
 * 
 * @author Yeon Kyoung Park
 * @version $Rev: 1 $, $Date: 2007-01-05 15:59:15 +0900 $,
 */
public class MRPClientDecoder extends ProtocolDecoderAdapter
{
    private Log log = LogFactory.getLog(getClass());

    /**
     * decode input stream
     *
     * @param session <code>IoSession</code> session
     * @param in <code>IoBuffer</code> input stream
     * @param out <code>ProtocolDecoderOutput</code> save decoding frame 
     * @throws  ProtocolViolationException
     */
    public void decode(IoSession session, IoBuffer in,
                       ProtocolDecoderOutput out )
            throws ProtocolDecoderException
    {
        try
        { 
            //in = in.limit(512);
            //log.debug("Received [" + session.getRemoteAddress()
            //        + "] : "+in.limit()+" :"+in.getHexDump());

            //boolean isActiveMRP = ((Boolean)session.getAttribute(
            //            MRPClientProtocolHandler.isActiveKey))
            //    .booleanValue();
            //log.debug("decode isActiveMRP["+isActiveMRP+"]");

            //if(isActiveMRP)
            //{
            //log.debug("limit:"+in.limit());
                byte[] bx = new byte[in.limit()];
                in.get(bx,0,bx.length);
                out.write(bx);
                //in.free();
                //return;
            //}
            

        } catch(Exception ex)
        {
            log.error("MRPDecoder::decode failed : ", ex);
            throw new ProtocolDecoderException(ex.getMessage());
        }
    }
}
