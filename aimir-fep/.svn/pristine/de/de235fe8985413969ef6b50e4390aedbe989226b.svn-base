package com.aimir.fep.protocol.fmp.client;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.springframework.orm.jpa.JpaTransactionManager;

import com.aimir.fep.BaseTestCase;
import com.aimir.fep.bypass.BypassRegister;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.protocol.fmp.client.sms.SMSClient;
import com.aimir.fep.protocol.mrp.command.frame.sms.RequestFrame;
import com.aimir.fep.util.DataUtil;
import com.aimir.util.TimeUtil;

public class IF4_1_2_MODEM_COMMAND_SMS extends BaseTestCase{
	
    private static Log log = LogFactory.getLog(IF4_1_2_MODEM_COMMAND_SMS.class);
    
    @Test
    public void testResetModem() {
    	
        String modemSerial = "";

        JpaTransactionManager txManager = DataUtil.getBean(JpaTransactionManager.class);
        CommandGW cgw = DataUtil.getBean(CommandGW.class);
        
        try {
    	    Object result = cgw.cmdSendSMS(
    	    		modemSerial,
                    RequestFrame.CMD_GPRSCONNECT_OID,
                    String.valueOf(SMSClient.getSEQ()),
                    8900+"");//BYPASS PORT    	    
    	    
			// relay control log(명령) 등록	
    	    String[][] params = null;
    	    
    	    Map<String, String[][]> command = new HashMap<String, String[][]>();
    	    command.put("cmdResetModem", params);	    
			BypassRegister bs = BypassRegister.getInstance();
			bs.add(modemSerial, command);

            log.info("Modem[" + modemSerial + "] SEND SMS End");
        }
        catch (Exception e) {}
        return;
    }
    
    @Test
    public void testFactorySetting() {
    	
        String modemSerial = "";

        JpaTransactionManager txManager = DataUtil.getBean(JpaTransactionManager.class);
        CommandGW cgw = DataUtil.getBean(CommandGW.class);
        
        try {
    	    Object result = cgw.cmdSendSMS(
    	    		modemSerial,
                    RequestFrame.CMD_GPRSCONNECT_OID,
                    String.valueOf(SMSClient.getSEQ()),
                    8900+"");//BYPASS PORT    	    
    	    
			// relay control log(명령) 등록	
    	    String[][] params = null;
    	    
    	    Map<String, String[][]> command = new HashMap<String, String[][]>();
    	    command.put("cmdFactorySetting", params);	    
			BypassRegister bs = BypassRegister.getInstance();
			bs.add(modemSerial, command);

            log.info("Modem[" + modemSerial + "] SEND SMS End");
        }
        catch (Exception e) {}
        return;
    }
    
    @Test
    public void identifyModemMeter() {
    	
        String modemSerial = "";

        JpaTransactionManager txManager = DataUtil.getBean(JpaTransactionManager.class);
        CommandGW cgw = DataUtil.getBean(CommandGW.class);
        
        try {
    	    Object result = cgw.cmdSendSMS(
    	    		modemSerial,
                    RequestFrame.CMD_GPRSCONNECT_OID,
                    String.valueOf(SMSClient.getSEQ()),
                    8900+"");//BYPASS PORT    	    
    	    
			// relay control log(명령) 등록	
    	    String[][] params = null;
    	    
    	    Map<String, String[][]> command = new HashMap<String, String[][]>();
    	    command.put("cmdIndentifyDevice", params);	    
			BypassRegister bs = BypassRegister.getInstance();
			bs.add(modemSerial, command);

            log.info("Modem[" + modemSerial + "] SEND SMS End");
        }
        catch (Exception e) {}
        return;
    }

    
    @Test
    public void nullByPass() {
    	
        String modemSerial = "";

        JpaTransactionManager txManager = DataUtil.getBean(JpaTransactionManager.class);
        CommandGW cgw = DataUtil.getBean(CommandGW.class);
        
        try {
    	    Object result = cgw.cmdSendSMS(
    	    		modemSerial,
                    RequestFrame.CMD_GPRSCONNECT_OID,
                    String.valueOf(SMSClient.getSEQ()),
                    8900+"");//BYPASS PORT    	    
    	    
    	    String[][] params = new String[1][2];
    	    params[0] = new String[]{"105.1.2", 6000+""};//timeout
    	    
    	    Map<String, String[][]> command = new HashMap<String, String[][]>();
    	    command.put("cmdSetBypassStart", params);	    
			BypassRegister bs = BypassRegister.getInstance();
			bs.add(modemSerial, command);

            log.info("Modem[" + modemSerial + "] SEND SMS End");
        }
        catch (Exception e) {}
        return;
    }
    
    @Test
    public void cmdSetTime() {
    	
        String modemSerial = "";

        JpaTransactionManager txManager = DataUtil.getBean(JpaTransactionManager.class);
        CommandGW cgw = DataUtil.getBean(CommandGW.class);
        
        try {
    	    Object result = cgw.cmdSendSMS(
    	    		modemSerial,
                    RequestFrame.CMD_GPRSCONNECT_OID,
                    String.valueOf(SMSClient.getSEQ()),
                    8900+"");//BYPASS PORT    	    
    	    
    	    String[][] params = new String[1][2];
    	    params[0] = new String[]{"102.1.2", TimeUtil.getCurrentTimeMilli()};//set time
    	    
    	    Map<String, String[][]> command = new HashMap<String, String[][]>();
    	    command.put("cmdSetTime", params);	    
			BypassRegister bs = BypassRegister.getInstance();
			bs.add(modemSerial, command);

            log.info("Modem[" + modemSerial + "] SEND SMS End");
        }
        catch (Exception e) {}
        return;
    }
    
    
    @Test
    public void cmdModemResetInterval() {
    	
        String modemSerial = "";

        JpaTransactionManager txManager = DataUtil.getBean(JpaTransactionManager.class);
        CommandGW cgw = DataUtil.getBean(CommandGW.class);
        
        try {
    	    Object result = cgw.cmdSendSMS(
    	    		modemSerial,
                    RequestFrame.CMD_GPRSCONNECT_OID,
                    String.valueOf(SMSClient.getSEQ()),
                    8900+"");//BYPASS PORT    	    
    	    
    	    String[][] params = new String[1][2];
    	    params[0] = new String[]{"102.2.2", 1440+""};//1 day reset interval (min)
    	    
    	    Map<String, String[][]> command = new HashMap<String, String[][]>();
    	    command.put("cmdModemResetInterval", params);	    
			BypassRegister bs = BypassRegister.getInstance();
			bs.add(modemSerial, command);

            log.info("Modem[" + modemSerial + "] SEND SMS End");
        }
        catch (Exception e) {}
        return;
    }
    
    @Test
    public void cmdSetMeteringInterval() {
    	
        String modemSerial = "";

        JpaTransactionManager txManager = DataUtil.getBean(JpaTransactionManager.class);
        CommandGW cgw = DataUtil.getBean(CommandGW.class);
        
        try {
    	    Object result = cgw.cmdSendSMS(
    	    		modemSerial,
                    RequestFrame.CMD_GPRSCONNECT_OID,
                    String.valueOf(SMSClient.getSEQ()),
                    8900+"");//BYPASS PORT    	    
    	    
    	    String[][] params = new String[1][2];
    	    params[0] = new String[]{"102.3.2", 60+""};//60 min reset interval (min)
    	    
    	    Map<String, String[][]> command = new HashMap<String, String[][]>();
    	    command.put("cmdSetMeteringInterval", params);	    
			BypassRegister bs = BypassRegister.getInstance();
			bs.add(modemSerial, command);

            log.info("Modem[" + modemSerial + "] SEND SMS End");
        }
        catch (Exception e) {}
        return;
    }
    
    @Test
    public void cmdSetServerIpPort() {
    	
        String modemSerial = "";

        JpaTransactionManager txManager = DataUtil.getBean(JpaTransactionManager.class);
        CommandGW cgw = DataUtil.getBean(CommandGW.class);
        
        try {
    	    Object result = cgw.cmdSendSMS(
    	    		modemSerial,
                    RequestFrame.CMD_GPRSCONNECT_OID,
                    String.valueOf(SMSClient.getSEQ()),
                    8900+"");//BYPASS PORT    	    
    	    
    	    String[][] params = new String[2][2];
    	    params[0] = new String[]{"103.1.2", ""};//ip
    	    params[1] = new String[]{"103.1.3", ""};//port
    	    Map<String, String[][]> command = new HashMap<String, String[][]>();
    	    command.put("cmdSetApn", params);	    
			BypassRegister bs = BypassRegister.getInstance();
			bs.add(modemSerial, command);

            log.info("Modem[" + modemSerial + "] SEND SMS End");
        }
        catch (Exception e) {}
        return;
    }
    
    @Test
    public void cmdSetApn() {
    	
        String modemSerial = "";

        JpaTransactionManager txManager = DataUtil.getBean(JpaTransactionManager.class);
        CommandGW cgw = DataUtil.getBean(CommandGW.class);
        
        try {
    	    Object result = cgw.cmdSendSMS(
    	    		modemSerial,
                    RequestFrame.CMD_GPRSCONNECT_OID,
                    String.valueOf(SMSClient.getSEQ()),
                    8900+"");//BYPASS PORT    	    
    	    
    	    String[][] params = new String[3][2];
    	    params[0] = new String[]{"103.2.2", ""};//apn address
    	    params[1] = new String[]{"103.2.3", ""};//apn id
    	    params[2] = new String[]{"103.2.4", ""};//apn password
    	    Map<String, String[][]> command = new HashMap<String, String[][]>();
    	    command.put("cmdSetApn", params);	    
			BypassRegister bs = BypassRegister.getInstance();
			bs.add(modemSerial, command);

            log.info("Modem[" + modemSerial + "] SEND SMS End");
        }
        catch (Exception e) {}
        return;
    }
    
}
