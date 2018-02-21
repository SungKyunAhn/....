package com.aimir.fep.sms;

import java.io.IOException;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsmpp.InvalidResponseException;
import org.jsmpp.PDUException;
import org.jsmpp.bean.Alphabet;
import org.jsmpp.bean.BindType;
import org.jsmpp.bean.ESMClass;
import org.jsmpp.bean.GeneralDataCoding;
import org.jsmpp.bean.MessageClass;
import org.jsmpp.bean.NumberingPlanIndicator;
import org.jsmpp.bean.RegisteredDelivery;
import org.jsmpp.bean.SMSCDeliveryReceipt;
import org.jsmpp.bean.TypeOfNumber;
import org.jsmpp.extra.NegativeResponseException;
import org.jsmpp.extra.ResponseTimeoutException;
import org.jsmpp.session.BindParameter;
import org.jsmpp.session.SMPPSession;
import org.jsmpp.util.AbsoluteTimeFormatter;
import org.jsmpp.util.TimeFormatter;
import org.junit.Test;

public class SMPPSubmitTest {
    private static Log logger = LogFactory.getLog(SMPPSubmitTest.class);
    
    private static TimeFormatter timeFormatter = new AbsoluteTimeFormatter();
    
    @Test
    public void test() {
        SMPPSession session = new SMPPSession();
        try {
            session.connectAndBind("10.228.37.34", 3800, new BindParameter(BindType.BIND_TX, 
                    "edh", "edh@", "cp", TypeOfNumber.UNKNOWN, NumberingPlanIndicator.UNKNOWN, null));
        } catch (IOException e) {
            logger.error("Failed connect and bind to host", e);
        }
        
        try {
            /*
            session.submitShortMessage(serviceType, sourceAddrTon, sourceAddrNpi, sourceAddr, 
                    destAddrTon, destAddrNpi, destinationAddr, esmClass, 
                    protocolId, priorityFlag, scheduleDeliveryTime, 
                    validityPeriod, registeredDelivery, replaceIfPresentFlag, 
                    dataCoding, smDefaultMsgId, shortMessage, optionalParameters);
                    */
            String messageId = session.submitShortMessage("CMT", TypeOfNumber.INTERNATIONAL,
                    NumberingPlanIndicator.UNKNOWN, "941", TypeOfNumber.INTERNATIONAL, 
                    NumberingPlanIndicator.UNKNOWN, "50931744070", new ESMClass(), 
                    (byte)0, (byte)1,  timeFormatter.format(new Date()), null, 
                    new RegisteredDelivery(SMSCDeliveryReceipt.SUCCESS_FAILURE), (byte)0, 
                    new GeneralDataCoding(Alphabet.ALPHA_DEFAULT, MessageClass.CLASS1, false), 
                    (byte)0, "jSMPP simplify SMPP on Java platform".getBytes());
            logger.info("Message submitted, message_id is " + messageId);
        } catch (PDUException e) {
            // Invalid PDU parameter
            logger.error("Invalid PDU parameter", e);
        } catch (ResponseTimeoutException e) {
            // Response timeout
            logger.error("Response timeout", e);
        } catch (InvalidResponseException e) {
            // Invalid response
            logger.error("Receive invalid response", e);
        } catch (NegativeResponseException e) {
            // Receiving negative response (non-zero command_status)
            logger.error("Receive negative response", e);
        } catch (IOException e) {
            logger.error("IO error occur", e);
        }
        
        session.unbindAndClose();
    }

}
