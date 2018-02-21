package com.aimir.fep.protocol.security;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * {@link ProtocolProvider} implementation of FEP Authentication
 *
 * @author 
 * @version 
 */
@Component
public class AuthTcpProtocolProvider
{
    private static Log log = LogFactory.getLog(AuthTcpProtocolProvider.class);

   
    @Autowired
   // private FMPProtocolHandler protocolHandler;
    private AuthProtocolHandler protocolHandler;
    
    // Codec factory is also usually a singleton.
    private static ProtocolCodecFactory CODEC_FACTORY =
        new ProtocolCodecFactory()
    {
        public ProtocolDecoder getDecoder(IoSession session) throws Exception
        {
            return new AuthCumulativeDecoder();
        }

        public ProtocolEncoder getEncoder(IoSession session) throws Exception
        {
            return new AuthEncoder();
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
        //return HANDLER;
//        protocolHandler.setProtocolType(this.protocolType);
        return protocolHandler;
    }


}
