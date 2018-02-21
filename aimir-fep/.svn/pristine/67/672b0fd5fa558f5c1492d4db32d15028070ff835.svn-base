/*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
*/
package com.aimir.fep.protocol.smsp.client.sms;

import java.io.IOException;
import java.util.Date;

import org.apache.log4j.BasicConfigurator;
import org.jsmpp.InvalidResponseException;
import org.jsmpp.PDUException;
import org.jsmpp.bean.AlertNotification;
import org.jsmpp.bean.BindType;
import org.jsmpp.bean.DataCodings;
import org.jsmpp.bean.DataSm;
import org.jsmpp.bean.DeliverSm;
import org.jsmpp.bean.DeliveryReceipt;
import org.jsmpp.bean.ESMClass;
import org.jsmpp.bean.MessageType;
import org.jsmpp.bean.NumberingPlanIndicator;
import org.jsmpp.bean.RegisteredDelivery;
import org.jsmpp.bean.SMSCDeliveryReceipt;
import org.jsmpp.bean.TypeOfNumber;
import org.jsmpp.extra.NegativeResponseException;
import org.jsmpp.extra.ProcessRequestException;
import org.jsmpp.extra.ResponseTimeoutException;
import org.jsmpp.session.BindParameter;
import org.jsmpp.session.DataSmResult;
import org.jsmpp.session.MessageReceiverListener;
import org.jsmpp.session.SMPPSession;
import org.jsmpp.session.Session;
import org.jsmpp.util.AbsoluteTimeFormatter;
import org.jsmpp.util.InvalidDeliveryReceiptException;
import org.jsmpp.util.TimeFormatter;

public class Test02 {
    private static TimeFormatter timeFormatter = new AbsoluteTimeFormatter();
    protected String trnx_Id;
    protected String eui_Id;

    public static void main(String[] args) {
    	
    	String MSISDN = "47580014006951"; // +47580014006951
    	String contents = "S0/I8Ka1ON24b18ESyVri6OLadaVw=52#";
    	byte[] sendMessage = contents.getBytes();
    	
    	String smscServer = "smsc1.com4.no";
		String smscPort = "9000"; 
		String smscUserName = "validerams"; 
		String smscPassword = "U91nDBr"; 
		String hesPhonenumber  = "47580014013024";	// +47580014013024
        String messageId = null;
        SMPPSession session = new SMPPSession();
        try {
        	session.connectAndBind(smscServer, Integer.parseInt(smscPort), new BindParameter(BindType.BIND_TRX, smscUserName, smscPassword, "cp",
                    TypeOfNumber.UNKNOWN, NumberingPlanIndicator.UNKNOWN, null));
        } catch (IOException e) {
            System.err.println("Failed connect and bind to host");
            e.printStackTrace();
        }
        
        // send Message
        try {
            final RegisteredDelivery registeredDelivery = new RegisteredDelivery();
            registeredDelivery.setSMSCDeliveryReceipt(SMSCDeliveryReceipt.SUCCESS_FAILURE);
            
            messageId = session.submitShortMessage("CMT", TypeOfNumber.UNKNOWN,
                    NumberingPlanIndicator.ISDN, hesPhonenumber, TypeOfNumber.INTERNATIONAL,
                    NumberingPlanIndicator.ISDN, MSISDN, new ESMClass(), (byte) 0, (byte) 1,
                    timeFormatter.format(new Date()), null, new RegisteredDelivery(SMSCDeliveryReceipt.SUCCESS_FAILURE),
                    (byte) 0, DataCodings.ZERO, (byte) 0, sendMessage);
            
            
            System.out.println("Message submitted, message_id is " + messageId);

        } catch (PDUException e) {
            // Invalid PDU parameter
            System.err.println("Invalid PDU parameter");
            e.printStackTrace();
        } catch (ResponseTimeoutException e) {
            // Response timeout
            System.err.println("Response timeout");
            e.printStackTrace();
        } catch (InvalidResponseException e) {
            // Invalid response
            System.err.println("Receive invalid respose");
            e.printStackTrace();
        } catch (NegativeResponseException e) {
            // Receiving negative response (non-zero command_status)
            System.err.println("Receive negative response");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("IO error occur");
            e.printStackTrace();
        }

        // receive Message
        BasicConfigurator.configure();

        // Set listener to receive deliver_sm
        session.setMessageReceiverListener(new MessageReceiverListener() {
        	
            public void onAcceptDeliverSm(DeliverSm deliverSm) throws ProcessRequestException {
            	String receivedMessageId = null;
            	
                if (MessageType.SMSC_DEL_RECEIPT.containedIn(deliverSm.getEsmClass())) {
                    // delivery receipt
                    try {
                        DeliveryReceipt delReceipt = deliverSm.getShortMessageAsDeliveryReceipt();
                        long id = Long.parseLong(delReceipt.getId()) & 0xffffffff;
                        receivedMessageId = Long.toString(id, 16).toUpperCase();
                        
                        // HEX to DECIMAL (S)
        				String digits = "0123456789ABCDEF";
        				receivedMessageId = receivedMessageId.toUpperCase();
        				int val = 0;
        				for (int i = 0; i < receivedMessageId.length(); i++) {
        					char c = receivedMessageId.charAt(i);
        					int d = digits.indexOf(c);
        					val = 16 * val + d;
        				}
        				receivedMessageId = String.valueOf(val);
        				// HEX to DECIMAL (E)
                        
                        
                        System.out.println("receivedMessageId '" + receivedMessageId + "' : " + delReceipt);
                    } catch (InvalidDeliveryReceiptException e) {
                        System.err.println("receive faild");
                        e.printStackTrace();
                    }
                } else {
                	// receivedMessageId
                    System.out.println("Receiving message : " + new String(deliverSm.getShortMessage()));
                }
            }

            public void onAcceptAlertNotification(AlertNotification alertNotification) {
                System.out.println("onAcceptAlertNotification");
            }

            public DataSmResult onAcceptDataSm(DataSm dataSm, Session source) throws ProcessRequestException {
                System.out.println("onAcceptDataSm");
                return null;
            }
        });

        try {
            Thread.sleep(60000);	// wait 60 second
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // unbind(disconnect)
        session.unbindAndClose();

        System.out.println("finish!");
    }

}