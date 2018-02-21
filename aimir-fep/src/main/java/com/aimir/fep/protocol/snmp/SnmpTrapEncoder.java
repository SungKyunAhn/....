package com.aimir.fep.protocol.snmp;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 2016-05-02 Hansejin
 */
public class SnmpTrapEncoder extends ProtocolEncoderAdapter {
    private static Logger logger = LoggerFactory.getLogger(SnmpTrapEncoder.class);

    @Override
    public void encode(IoSession ioSession, Object obj, ProtocolEncoderOutput protocolEncoderOutput) throws Exception {
        if(obj instanceof SnmpConstants.SnmpActionType){
            logger.info("### SnmpTrapEncoder::session.close ###");
            ioSession.closeNow();

        }else {
            logger.info("### SnmpTrapEncoder::not a member of snmp type ###");
            ioSession.closeNow();
        }

    }
}