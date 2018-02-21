package com.aimir.fep.protocol.fmp.gateway.circuit.relay;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

import com.aimir.fep.protocol.fmp.gateway.circuit.CircuitListener;

/**
 * {@link ProtocolProvider} for Circuit Data Relay Service
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2006-10-16 15:59:15 +0900 $,
 */
public class RelayProtocolProvider
{

    private CircuitListener listener = null;

    public RelayProtocolProvider(CircuitListener listener)
    {
        this.listener = listener;
    }

    // Codec factory is also usually a singleton.
    private static ProtocolCodecFactory CODEC_FACTORY = 
        new ProtocolCodecFactory()
    {
        public ProtocolDecoder getDecoder(IoSession session) throws Exception
        {
            return new RelayDecoder();
        }

        public ProtocolEncoder getEncoder(IoSession session) throws Exception
        {
            return new RelayEncoder();
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
        return new RelayProtocolHandler(this.listener);
    }
}
