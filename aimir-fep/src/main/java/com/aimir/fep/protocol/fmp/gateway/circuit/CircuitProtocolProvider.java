package com.aimir.fep.protocol.fmp.gateway.circuit;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 * {@link ProtocolProvider} for Circuit Data
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2006-10-16 15:59:15 +0900 $,
 */
public class CircuitProtocolProvider
{

    private CircuitListener listener = null;

    /**
     * Constructor
     */
    public CircuitProtocolProvider(CircuitListener listener)
    {
        this.listener = listener;
    }

    // Codec factory is also usually a singleton.
    private static ProtocolCodecFactory CODEC_FACTORY = 
        new ProtocolCodecFactory()
    {
        public ProtocolDecoder getDecoder(IoSession session) throws Exception
        {
            return new CircuitDecoder();
        }

        public ProtocolEncoder getEncoder(IoSession session) throws Exception
        {
            return new CircuitEncoder();
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
        return new CircuitProtocolHandler(this.listener);
    }
}
