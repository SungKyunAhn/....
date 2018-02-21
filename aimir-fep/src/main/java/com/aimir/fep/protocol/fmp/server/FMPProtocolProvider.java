package com.aimir.fep.protocol.fmp.server;


import com.aimir.constants.CommonConstants;
import com.aimir.fep.util.FMPProperty;
import com.aimir.model.system.Code;

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
 * {@link ProtocolProvider} implementation of FEP FMP(AiMiR and MCU Protocol).
 *
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
@Component
public class FMPProtocolProvider
{
    private static Log log = LogFactory.getLog(FMPProtocolProvider.class);

    private Integer protocolType =
        new Integer(FMPProperty.getProperty("protocol.type.default","3")); //Default GPRS
    
    @Autowired
    private FMPProtocolHandler protocolHandler;

    // Codec factory is also usually a singleton.
    private static ProtocolCodecFactory CODEC_FACTORY =
        new ProtocolCodecFactory()
    {
        public ProtocolDecoder getDecoder(IoSession session) throws Exception
        {
            return new FMPCumulativeDecoder();
        }

        public ProtocolEncoder getEncoder(IoSession session) throws Exception
        {
            return new FMPEncoder();
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
        protocolHandler.setProtocolType(this.protocolType);
        return protocolHandler;
    }

    /**
     * set Protocol Type(1:CDMA,2:GSM,3:GPRS,4:PSTN,5:LAN)
     * @param protocolType <code>Integer</code> Protocol Type
     */
    public void setProtocolType(Integer protocolType)
    {
        this.protocolType = protocolType;
    }

    /**
     * get Protocol Type(1:CDMA,2:GSM,3:GPRS,4:PSTN,5:LAN)
     * @return protocolType <code>Integer</code> Protocol Type
     */
    public Integer getProtocolType()
    {
        return this.protocolType;
    }

    /**
     * get Protocol Type String(1:CDMA,2:GSM,3:GPRS,4:PSTN,5:LAN)
     * @return protocolType <code>String</code> Protocol Type
     */
    public String getProtocolTypeString()
    {
        int proto = this.protocolType.intValue();
        Code code = CommonConstants.getProtocol(proto+"");
        return "[" + code.getName() + "]";
    }
}
