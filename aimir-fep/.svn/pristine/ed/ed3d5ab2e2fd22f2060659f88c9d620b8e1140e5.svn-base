package com.aimir.fep.tool;

import java.io.File;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.dao.device.ModemDao;
import com.aimir.fep.bypass.BypassRegister;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.protocol.fmp.client.sms.SMSClient;
import com.aimir.fep.protocol.fmp.common.SlideWindow;
import com.aimir.fep.protocol.fmp.frame.service.CommandData;
import com.aimir.fep.protocol.mrp.command.frame.sms.RequestFrame;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FileUtil;
import com.aimir.fep.util.FrameUtil;
import com.aimir.fep.util.Hex;
import com.aimir.model.device.Modem;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;

public class GPRSModemOTA_IF412 {
    private static Log log = LogFactory.getLog(GPRSModemOTA_IF412.class);

    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"/config/spring.xml"}); 
        DataUtil.setApplicationContext(ctx);
        
        String modelName = args[0];
        String fwVersion = args[1];
        String buildNumber = args[2];
        String hwVersion = args[3];
        String filePath = args[4];
        String[] modemSerial = null;
        if(args.length > 5){
        	modemSerial = new String[args.length - 5];
        	for(int i = 0; i < args.length - 5; i++){
        		modemSerial[i] = args[5+i];
        	}
        }

        log.info("OTA " + modelName + " and " + filePath);
        
        ModemDao modemDao = ctx.getBean(ModemDao.class);

        JpaTransactionManager txManager = null;
        TransactionStatus txStatus = null;
        List<Modem> modems = null;
        
        try {
            txManager = ctx.getBean(JpaTransactionManager.class);
            txStatus = txManager.getTransaction(null);

            Set<Condition> condition = new HashSet<Condition>();
            condition.add(new Condition("model", new Object[] {"md"}, null, Restriction.ALIAS));
            condition.add(new Condition("md.name", new Object[] {modelName}, null, Restriction.EQ));
            condition.add(new Condition("modemType", new Object[]{ModemType.MMIU}, null, Restriction.EQ));
            condition.add(new Condition("fwVer", new Object[]{fwVersion}, null, Restriction.EQ));
            condition.add(new Condition("fwRevision", new Object[]{buildNumber}, null, Restriction.EQ));
            condition.add(new Condition("hwVer", new Object[]{hwVersion}, null, Restriction.EQ));
            if(modemSerial != null && modemSerial.length > 0){
            	condition.add(new Condition("deviceSerial", new Object[]{modemSerial}, null, Restriction.IN));
            }
            
            modems = modemDao.findByConditions(condition);

            txManager.commit(txStatus);
        }
        catch (Exception e) {
            
        }
        log.info("GPRS Modem OTA Start " + modems.size());
        
        ThreadPoolExecutor executor = new ThreadPoolExecutor(100, 100, 1, TimeUnit.DAYS, new LinkedBlockingQueue<Runnable>());

        for (Modem modem : modems) {
            executor.execute(new OTAThread(modem, modelName, fwVersion, buildNumber, hwVersion, filePath));
        }
        try {
            executor.shutdown();
            while (!executor.isTerminated()) {
            }
        }
        catch (Exception e) {}
        
        log.info("Ondemand Recocvery End");
        System.exit(0);
    }
}

class OTAThread implements Runnable {
	
    private static Log log = LogFactory.getLog(OTAThread.class);
    
    Modem modem = null;
    String modelName = null; 
    String fwVersion = null; 
    String buildNumber = null; 
    String hwVersion = null;
    String filePath = null;  

    OTAThread(Modem modem, String modelName, String fwVersion, String buildNumber, String hwVersion, String filePath) {
        this.modem = modem;
        this.modelName = modelName;
        this.fwVersion = fwVersion;
        this.buildNumber = buildNumber;
        this.hwVersion = hwVersion;
        this.filePath = filePath;  
    }
    
    @Override
    public void run() {
    	
        String modemSerial = modem.getDeviceSerial();

        JpaTransactionManager txManager = DataUtil.getBean(JpaTransactionManager.class);
        CommandGW cgw = DataUtil.getBean(CommandGW.class);
        
        try {
    	    Object result = cgw.cmdSendSMS(
    	    		modemSerial,
                    RequestFrame.CMD_GPRSCONNECT_OID,
                    String.valueOf(SMSClient.getSEQ()),
                    InetAddress.getLocalHost().getHostAddress(),//BYPASS IP
                    8900+"");//BYPASS PORT    	    

    	    File f = FileUtil.getFirmwareFile(filePath);
            String fullfname = f.getPath();
            log.debug("file name :"+fullfname);
            
            byte[] data = DataFormat.readByteFromFile(f);
            byte[] crc = FrameUtil.getCRC(data);
            log.debug("file crc :"+Hex.decode(crc));
            
    	    String[][] params = new String[4][2];
    	    params[0] = new String[]{"106.1.2", f.length()+""};//- Image Length
    	    params[1] = new String[]{"106.1.3", Hex.decode(crc)};//- Image CRC
    	    params[2] = new String[]{"106.1.8", 1024+""};//- Receive Data Size
    	    params[3] = new String[]{"filePath", fullfname};//- file path
    	    
    	    Map<String, String[][]> command = new HashMap<String, String[][]>();
    	    command.put("cmdOTAStart", params);	    
			BypassRegister bs = BypassRegister.getInstance();
			bs.add(modemSerial, command);

            log.info("Modem[" + modemSerial + "] SEND SMS End");
        }
        catch (Exception e) {}
        return;
    }
}
