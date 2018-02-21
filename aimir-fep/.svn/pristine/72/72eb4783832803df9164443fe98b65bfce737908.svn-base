package com.aimir.fep.protocol.smsp.client.sms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsmpp.bean.AlertNotification;
import org.jsmpp.bean.DataSm;
import org.jsmpp.bean.DeliverSm;
import org.jsmpp.bean.DeliveryReceipt;
import org.jsmpp.bean.MessageType;
import org.jsmpp.extra.ProcessRequestException;
import org.jsmpp.session.DataSmResult;
import org.jsmpp.session.MessageReceiverListener;
import org.jsmpp.session.Session;
import org.jsmpp.util.InvalidDeliveryReceiptException;

import com.aimir.dao.device.AsyncCommandResultDao;
import com.aimir.fep.protocol.smsp.server.SMPPAdapter;
import com.aimir.fep.util.DataUtil;

/** 
 * SMS_Receiver 
 * 
 * @version     1.0  2016.07.23 
 * @author		Sung Han LIM 
 */

public class SMPP_Listener implements MessageReceiverListener {
	private static Log logger = LogFactory.getLog(SMPP_Listener.class);
	private static AsyncCommandResultDao commandResultDao = DataUtil.getBean(AsyncCommandResultDao.class);
	private String messageId = null;
	private static boolean listenerStatus;
	private SMPPAdapter smppAdapter;
	private SMPP_Submitter smpp_Submitter;
	
	public SMPP_Listener(SMPP_Submitter smpp_Submitter) {
		this.smpp_Submitter = smpp_Submitter;
	}

	public SMPP_Listener(SMPPAdapter smppAdapter) {
		this.smppAdapter = smppAdapter;
	}
	
    public void onAcceptDeliverSm(DeliverSm deliverSm) throws ProcessRequestException {
    	
        if (MessageType.SMSC_DEL_RECEIPT.containedIn(deliverSm.getEsmClass())) {
            try {
                DeliveryReceipt delReceipt = deliverSm.getShortMessageAsDeliveryReceipt();
                
                // response - message id-+
                long id = Long.parseLong(delReceipt.getId()) & 0xffffffff;
                messageId = Long.toString(id, 16).toUpperCase();
                
                // HEX to DECIMAL (S)
				String digits = "0123456789ABCDEF";
				messageId = messageId.toUpperCase();
				int val = 0;
				for (int i = 0; i < messageId.length(); i++) {
					char c = messageId.charAt(i);
					int d = digits.indexOf(c);
					val = 16 * val + d;
				}
				messageId = String.valueOf(val);
				setMessageId(messageId);
				// HEX to DECIMAL (E)
                
                logger.debug("Receiving delivery receipt for message '" + messageId + " ' from " + deliverSm.getSourceAddr() + " to " + deliverSm.getDestAddress() + " : " + delReceipt);
            } catch (InvalidDeliveryReceiptException e) {
            	logger.debug("Failed getting delivery receipt");
                e.printStackTrace();
            }
        } else { // this message is regular short message
        	String euiId = smpp_Submitter.getEui_Id();
        	String result = new String(deliverSm.getShortMessage());
        	messageId = getMessageId();
        	
        	logger.info("====================================");
			logger.info("Received MSG [" + messageId + "]");
			logger.info("Received Result [" + result + "]");
			logger.info("====================================");
        	
			// SMS 응답받은 결과 저장 로직 (S)
			// TODO
			// SMS 응답받은 결과 저장 로직 (E) 
        } 
    }
    
    public void onAcceptAlertNotification(AlertNotification alertNotification) {
    }
    
    public DataSmResult onAcceptDataSm(DataSm dataSm, Session source) throws ProcessRequestException {
        return null;
    }
    
    public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	
	public boolean getListnerStatus() {
		return listenerStatus;
	}

	public void setListnerStatus(boolean listenerStatus) {
		this.listenerStatus = listenerStatus;
	}
}