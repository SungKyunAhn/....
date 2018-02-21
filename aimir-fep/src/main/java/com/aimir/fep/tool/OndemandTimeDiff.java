package com.aimir.fep.tool;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.mvm.DayEMDao;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.meter.data.MeterData;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.MCU;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.mvm.DayEM;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;
import com.aimir.util.DateTimeUtil;

public class OndemandTimeDiff {
    private static Log log = LogFactory.getLog(OndemandTimeDiff.class);

    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"/config/spring.xml"}); 
        DataUtil.setApplicationContext(ctx);
        long timeDiff = 0l;
        if (args[0] != null && !args[0].contains("timeDiff")) {
            timeDiff = Long.parseLong(args[0]);
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
            
            Set<Condition> condition = new HashSet<Condition>();
            condition.add(new Condition("timeDiff", new Object[]{timeDiff}, null, Restriction.GE));
            condition.add(new Condition("meterStatus", new Object[]{"m"}, null, Restriction.ALIAS));
            condition.add(new Condition("m.name", new Object[]{"Delete"}, null, Restriction.NOT));
            
            meters = meterDao.findByConditions(condition);
            
            condition = new HashSet<Condition>();
            condition.add(new Condition("timeDiff", new Object[]{-1*timeDiff}, null, Restriction.LE));
            condition.add(new Condition("meterStatus", new Object[]{"m"}, null, Restriction.ALIAS));
            condition.add(new Condition("m.name", new Object[]{"Delete"}, null, Restriction.NOT));
            
            meters.addAll(meterDao.findByConditions(condition));
            
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
	                        	mmlist.add(new String[]{m.getMdsId(), modem.getDeviceSerial(), m.getModel().getDeviceVendor().getName()});
	                        }
	                        mculist.put(mcu.getSysID(), mmlist);
	                    }
	                    else {
	                        if (modem.getModemType() == ModemType.MMIU) {
	                            mmlist = mculist.get(modem.getDeviceSerial());
	                            if (mmlist == null) {
	                                mmlist = new ArrayList<String[]>();
	                            }
	                            if(m.getModel() != null) {
	                                mmlist.add(new String[]{m.getMdsId(), modem.getDeviceSerial(), m.getModel().getDeviceVendor().getName()});
	                            }
	                            mculist.put(modem.getDeviceSerial(), mmlist);
	                        }
	                    }
	                }
	            }
            }
            txManager.commit(txStatus);
        }
        catch (Exception e) {
            log.error(e,e);
        }
        
        log.info("Ondemand Recocvery Start " + meters.size());
        
        if(mculist.size() > 0) {
            String yyyymmdd = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMdd");
	        ThreadPoolExecutor executor = new ThreadPoolExecutor(100, 100, 1, TimeUnit.DAYS, new LinkedBlockingQueue());
	        String mcuId = null;
	        
	        for (Iterator<String> i = mculist.keySet().iterator(); i.hasNext();) {
	            mcuId = i.next();
	            executor.execute(new OndemandTimeDiffThread(mcuId, mculist.get(mcuId), yyyymmdd, yyyymmdd));
	        }
	        try {
	            executor.shutdown();
	            while (!executor.isTerminated()) {
	            }
	        }
	        catch (Exception e) {}
	    }
        log.info("Ondemand Recocvery End");
        System.exit(0);
    }
    
}

class OndemandTimeDiffThread implements Runnable {
    private static Log log = LogFactory.getLog(OndemandThread.class);
    
    String mcuId;
    List<String[]> mmlist;
    String startdate;
    String enddate;

    OndemandTimeDiffThread(String mcuId, List<String[]> mmlist, String startdate, String enddate) {
        this.mcuId = mcuId;
        this.mmlist = mmlist;
        this.startdate = startdate;
        this.enddate = enddate;
    }
    
    @Override
    public void run() {
    	String meterModel = null;
        String modemId = null;
        String meterId = null;
        Set<Condition> condition = null;
        String value = null;
        JpaTransactionManager txManager = DataUtil.getBean(JpaTransactionManager.class);
        DayEMDao dayemDao = DataUtil.getBean(DayEMDao.class);
        CommandGW cgw = DataUtil.getBean(CommandGW.class);
        
        TransactionStatus txStatus = null;
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar cal = Calendar.getInstance();
        String yyyymmdd = null;
        
        List<DayEM> dayem = null;
        MeterData mdata = null;
        
        try {
            String[] mm = null;
            for (int i = 0; i < mmlist.size(); i++) {
                cal.setTime(sdf.parse(startdate));
                
                mm = mmlist.get(i);
                meterId = mm[0];
                modemId = mm[1];
                meterModel = mm[2];
                
                while ((yyyymmdd = sdf.format(cal.getTime())).compareTo(enddate) <= 0) {
                    try {
                        log.info("Mcu[" + mcuId + "] " + (i+1) + "/" + mmlist.size() + " Meter[" + meterId + "] yyyymmdd[" + yyyymmdd + "]");
                        condition = new HashSet<Condition>();
                        condition.add(new Condition("id.mdevType", new Object[]{DeviceType.Meter}, null, Restriction.EQ));
                        condition.add(new Condition("id.mdevId", new Object[]{meterId}, null, Restriction.EQ));
                        condition.add(new Condition("id.dst", new Object[]{0}, null, Restriction.EQ));
                        condition.add(new Condition("id.yyyymmdd", new Object[]{yyyymmdd}, null, Restriction.EQ));
                        condition.add(new Condition("id.channel", new Object[]{1}, null, Restriction.EQ));
                        
                        txStatus = txManager.getTransaction(null);
                        dayem = dayemDao.findByConditions(condition);
                        txManager.commit(txStatus);
                        
                        if (dayem.size() > 0) {
                            for (int v = 0; v < 24; v++) {
                                value = BeanUtils.getProperty(dayem.get(0), String.format("value_%02d", v));
                                
                                if (value == null) {

                                	if(meterModel.contains("Kamstrup")){
                                        mdata = cgw.cmdOnDemandMeter(mcuId, meterId, modemId,
                                                "16", yyyymmdd, yyyymmdd);
                                	}else{
                                        mdata = cgw.cmdOnDemandMeter(mcuId, meterId, modemId,
                                                "0", yyyymmdd, yyyymmdd);
                                	}

                                    Map mapdata = mdata.getParser().getData();
                                    
                                    String key = null;
                                    for (Iterator keyset = mapdata.keySet().iterator(); keyset.hasNext(); ) {
                                        key = (String)keyset.next();
                                        log.debug(key+"="+mapdata.get(key));
                                    }
                                    break;
                                }
                            }
                        }
                        else {
                        	if(meterModel.contains("Kamstrup")){
                                mdata = cgw.cmdOnDemandMeter(mcuId, meterId, modemId,
                                        "16", yyyymmdd, yyyymmdd);
                        	}else{
                                mdata = cgw.cmdOnDemandMeter(mcuId, meterId, modemId,
                                        "0", yyyymmdd, yyyymmdd);
                        	}

                            Map mapdata = mdata.getParser().getData();
                            
                            String key = null;
                            for (Iterator<String> keyset = mapdata.keySet().iterator(); keyset.hasNext(); ) {
                                key = keyset.next();
                                log.debug(key+"="+mapdata.get(key));
                            }
                        }
                    }
                    catch (Exception ne) {
                        if (ne.getMessage().contains("DCU") || ne.getMessage().contains("dcu")) {
                            log.warn("MCU is not connected. so skip [" + mcuId + "]");
                            return;
                        }
                    }
                    cal.add(Calendar.DAY_OF_MONTH, 1);
                }
            }
            log.info("Mcu[" + mcuId + "] " + mmlist.size() + " Ondemand End");
        }
        catch (Exception e) {}
        return;
    }
}
