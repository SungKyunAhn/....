package com.aimir.fep.protocol.snmp;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.PDU;
import org.snmp4j.asn1.BER;
import org.snmp4j.asn1.BERInputStream;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OctetString;

import com.aimir.util.TimeUtil;

/**
 * @author 2016-05-02 Hansejin
 */
public class SnmpTrapDecoder extends CumulativeProtocolDecoder {
    private static Logger logger = LoggerFactory.getLogger(SnmpTrapDecoder.class);

    /**
     * decode input stream
     * @param ioSession
     *            <code>ProtocolSession</code> session
     * @param ioBuffer
     *            <code>ByteBuffer</code> input stream
     * @param protocolDecoderOutput
     *            <code>ProtocolDecoderOutput</code> save decoding frame
     * @throws Exception
     */
    @Override
    protected boolean doDecode(IoSession ioSession, IoBuffer ioBuffer, ProtocolDecoderOutput protocolDecoderOutput) throws Exception {
        logger.info("@@ Received [{} SessionId = {}] : LIMIT[{}] POSITION[{}] CAPACITY[{}]"
                , new Object[]{ioSession.getRemoteAddress(), ioSession.getId(), ioBuffer.limit(), ioBuffer.position(), ioBuffer.capacity()});

        ByteBuffer pduBuffer = ioBuffer.buf();
        // Decode the bytes using SNMP4J API's
        PDU pdu = new PDU();
        try {
            if (ioSession.getAttribute("startLongTime") == null) {
                ioSession.setAttribute("startLongTime", System.currentTimeMillis());
                ioSession.setAttribute("startTime", TimeUtil.getCurrentTime());
            }

            BERInputStream berStream = new BERInputStream(pduBuffer);
            BER.MutableByte mutableByte = new BER.MutableByte();
            int length = BER.decodeHeader(berStream, mutableByte);
            int startPos = (int)berStream.getPosition();

            if (mutableByte.getValue() != BER.SEQUENCE) {
                String txt = "SNMPv2c PDU must start with a SEQUENCE";
                throw new IOException(txt);
            }
            Integer32 version = new Integer32();
            version.decodeBER(berStream);

            // decode community string
            OctetString securityName = new OctetString();
            securityName.decodeBER(berStream);

            // decode the remaining PDU
            pdu.decodeBER(berStream);

            // log
            logger.info("@@ Version [{}] ", version);
            logger.info("@@ SecurityName [{}] ", securityName);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        protocolDecoderOutput.write(pdu);
        return true;
    }

}
