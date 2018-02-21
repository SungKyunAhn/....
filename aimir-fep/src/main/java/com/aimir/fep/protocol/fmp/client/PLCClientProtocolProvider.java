package com.aimir.fep.protocol.fmp.client;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 * {@link ProtocolProvider} implementation of FEP FMP(AiMiR and MCU Protocol).
 *
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class PLCClientProtocolProvider
{

    // Protocol handler is usually a singleton.
    //private static ProtocolHandler HANDLER =
    //private ProtocolHandler HANDLER =
    //    new FMPClientProtocolHandler();

    // Codec factory is also usually a singleton.
    private static ProtocolCodecFactory CODEC_FACTORY =
        new ProtocolCodecFactory()
    {
        public ProtocolDecoder getDecoder(IoSession session) throws Exception
        {
            return new PLCClientDecoder();
        }

        public ProtocolEncoder getEncoder(IoSession session) throws Exception
        {
            return new PLCClientEncoder();
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
        return new PLCClientProtocolHandler();
        //return HANDLER;
    }

}
