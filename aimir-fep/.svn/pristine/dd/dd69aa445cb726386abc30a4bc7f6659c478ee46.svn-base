package com.aimir.fep.protocol.mrp.client;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.textline.TextLineDecoder;
import org.apache.mina.filter.codec.textline.TextLineEncoder;


/**
 * {@link ProtocolProvider} implementation of FEP MRP(AiMiR and Meter Protocol).
 * 
 * @author Yeon Kyoung Park
 * @version $Rev: 1 $, $Date: 2008-01-05 15:59:15 +0900 $,
 */
public class SMSClientProtocolProvider
{
    // Codec factory is also usually a singleton.
    private static ProtocolCodecFactory CODEC_FACTORY = 
        new ProtocolCodecFactory()
    {
        public ProtocolDecoder getDecoder(IoSession session) throws Exception
        {
            return new TextLineDecoder();
        	//return new SMSCumulativeTextDecoder();
        }

        public ProtocolEncoder getEncoder(IoSession session) throws Exception
        {
            return new TextLineEncoder();
        }
    };

    /**
     * inherited method from ProtocolProvider
     *
     * @return codefactory <code>ProtocolCodecFactory</code>
     */
    public ProtocolCodecFactory getCodecFactory()
    {
        return CODEC_FACTORY;
    }

    /**
     * inherited method from ProtocolProvider
     *
     * @return handler <code>ProtocolHandler</code>
     */
    public IoHandler getHandler()
    {
        return new SMSClientProtocolHandler();
    }

}
