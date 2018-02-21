package com.aimir.fep.tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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

import com.aimir.dao.device.MeterDao;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.meter.data.MeterData;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.MCU;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;

public class OndemandRecoveryOffset {
    private static Log log = LogFactory.getLog(OndemandRecoveryOffset.class);

    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"/config/spring.xml"}); 
        DataUtil.setApplicationContext(ctx);
        
        if ((args.length != 2 && args.length != 3 && args.length != 4) || (args[0].equals("${offset}") || args[1].equals("${count}")) ||
                (args[0] == null || args[1] == null) || ("".equals(args[0]) || "".equals(args[1]))) {
            args = new String[4];
            
            log.info("Wrong offset and count");
            System.exit(0);
        }
        
        int offset = Integer.parseInt(args[0]);
        int count = Integer.parseInt(args[1]);
        String modelId = null;
        String meterSerial = null;
        
        if(!("".equals(args[2])) && ("".equals(args[3]))) {
        	modelId = args[2];
        	log.info("Recovery offset[" + offset + "], count[" + count + "], model[" + modelId + "]");
        } else if(!("".equals(args[3]))) {
        	modelId = args[2];
        	meterSerial = args[3];
        	log.info("Recovery offset[" + offset + "], count[" + count + "], model[" + modelId + "], meterSerial[" + meterSerial + "]");
        } else {
        	log.info("Recovery offset[" + offset + "], count[" + count + "]");
        }
        
        MeterDao meterDao = ctx.getBean(MeterDao.class);
        
        MCU mcu = null;
        Modem modem = null;
        JpaTransactionManager txManager = null;
        TransactionStatus txStatus = null;
        
        Map<String, List<String[]>> mculist = new HashMap<String, List<String[]>>();
        List<String[]> mmlist = null;
        List<Meter> meters = new ArrayList<Meter>();
        
        try {
            txManager = ctx.getBean(JpaTransactionManager.class);
            txStatus = txManager.getTransaction(null);
            
            //recoveryOffset.sh offset count modelId 로 명령이 내려올 경우
            if(modelId != null && !"".equals(modelId) && ("".equals(args[3]))) {
                Set<Condition> condition = new HashSet<Condition>();
                condition.add(new Condition("modelId", new Object[]{Integer.parseInt(modelId)}, null, Restriction.EQ));
                meters = meterDao.findByConditions(condition);
                if(meters.size() <= 0 ) {
                	log.info("Wrong ModelId");
            	}
                //recoveryOffset.sh offset count modelId meterSerial로 명령이 내려올 경우
            } else if(meterSerial != null && !"".equals(meterSerial)) {
                Set<Condition> condition = new HashSet<Condition>();
                condition.add(new Condition("mdsId", new Object[]{meterSerial}, null, Restriction.EQ));
                meters = meterDao.findByConditions(condition);
                if(meters.size() <= 0 ) {
                	log.info("Wrong ModelName or MeterSerial");
            	}
            } else {
            	meters = meterDao.getAll();
            }
            
            if(meters != null || meters.size() > 0) {
	            for (Meter m : meters) {
	                modem = m.getModem();
	                if (modem != null) {
	                    mcu = modem.getMcu();
	                    
	                    if (mcu != null) {
	                        mmlist = mculist.get(mcu.getSysID());
	                        
	                        if (mmlist == null) {
	                            mmlist = new ArrayList<String[]>();
	                        }
	                        if(m.getModel() != null) {
	                        	mmlist.add(new String[]{m.getMdsId(), modem.getDeviceSerial(), m.getModel().getName()});
	                        }
	                        mculist.put(mcu.getSysID(), mmlist);
	                    }
	                }
	            }
            }
            txManager.commit(txStatus);
        }
        catch (Exception e) {
            log.error(e,e);
        }
        
        log.info("Ondemand Recocvery Offset Start " + meters.size());
        
        if(mculist.size() > 0) {
	        ThreadPoolExecutor executor = new ThreadPoolExecutor(100, 100, 1, TimeUnit.DAYS, new LinkedBlockingQueue());
	        String mcuId = null;
	        
	        for (Iterator<String> i = mculist.keySet().iterator(); i.hasNext();) {
	            mcuId = i.next();
	            executor.execute(new OndemandOffsetThread(mcuId, mculist.get(mcuId), offset, count));
	        }
	        try {
	            executor.shutdown();
	            while (!executor.isTerminated()) {
	            }
	        }
	        catch (Exception e) {}
	    }
        log.info("Ondemand Recocvery Offset End");
        System.exit(0);
    }
}

class OndemandOffsetThread implements Runnable {
    private static Log log = LogFactory.getLog(OndemandOffsetThread.class);
    
    String mcuId;
    List<String[]> mmlist;
    int offset;
    int count;

    OndemandOffsetThread(String mcuId, List<String[]> mmlist, int offset, int count) {
        this.mcuId = mcuId;
        this.mmlist = mmlist;
        this.offset = offset;
        this.count = count;
    }
    
    @Override
    public void run() {
    	String meterModel = null;
        String modemId = null;
        String meterId = null;
        CommandGW cgw = DataUtil.getBean(CommandGW.class);

        MeterData mdata = null;
        
        try {
            String[] mm = null;
            for (int i = 0; i < mmlist.size(); i++) {
                
                mm = mmlist.get(i);
                meterId = mm[0];
                modemId = mm[1];
                meterModel = mm[2];

            	if(meterModel.equals("K162M")|| meterModel.equals("K382M") || meterModel.equals("K382M AB1")){
            		mdata = cgw.cmdOnRecoveryDemandMeter(mcuId, modemId, "16", offset, count);
            	}else{
            		mdata = cgw.cmdOnRecoveryDemandMeter(mcuId, modemId, "0", offset, count);
            	}

                Map mapdata = mdata.getParser().getData();

                String key = null;
                for (Iterator<String> keyset = mapdata.keySet().iterator(); keyset.hasNext(); ) {
                    key = keyset.next();
                    log.debug(key+"="+mapdata.get(key));
                }
                }
                
            log.info("Mcu[" + mcuId + "] " + mmlist.size() + " Ondemand Offset End");
            
        }
        catch (Exception e) {}
        return;
    }
}
