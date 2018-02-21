package com.aimir.fep.protocol.fmp.client;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 * AMU ClientProtocolProvider
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 2. 19. 오후 6:19:39$
 */
public class AMUClientProtocolProvider {

	private static ProtocolCodecFactory CODEC_FACTORY = new ProtocolCodecFactory()
    {
        public ProtocolDecoder getDecoder(IoSession session) throws Exception
        {
            return new AMUClientDecoder();
        }

        public ProtocolEncoder getEncoder(IoSession session) throws Exception
        {
            return new AMUClientEncoder();
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
        return new AMUClientProtocolHandler();
    }
}


