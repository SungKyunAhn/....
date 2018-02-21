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

public class OndemandRecovery {
    private static Log log = LogFactory.getLog(OndemandRecovery.class);

    public static void main(String[] args) {
        log.info("ARG_0[" + args[0] + "] ARG_1[" + args[1] + "] ARG_2[" + args[2] + "] ARG_3[" + args[3] + "]");
        ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"/config/spring.xml"}); 
        DataUtil.setApplicationContext(ctx);
        
        if ((args.length != 2 && args.length != 3 && args.length != 4) || (args[0].equals("${startDate}") || args[1].equals("${endDate}")) ||
                (args[0] == null || args[1] == null) || ("".equals(args[0]) || "".equals(args[1]))) {
            args = new String[4];
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -1);
            args[1] = sdf.format(cal.getTime());
            cal.add(Calendar.DAY_OF_MONTH, -1);
            args[0] = sdf.format(cal.getTime());
        }
        
        String startdate = args[0];
        String enddate = args[1];
        String modelId = null;
        String meterSerial = null;
        
        if(!"".equals(args[2]) || !"".equals(args[3])) {
        	modelId = args[2];
        	meterSerial = args[3];
        	log.info("Recovery between " + startdate + " and " + enddate + ", model[" + modelId + "], meterSerial[" + meterSerial + "]");
        } else {
        	log.info("Recovery between " + startdate + " and " + enddate);
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
            txManager.setDefaultTimeout(120);
            txStatus = txManager.getTransaction(null);
            
            if(!"".equals(modelId) && modelId != null) {
                Set<Condition> condition = new HashSet<Condition>();
                condition.add(new Condition("modelId", new Object[]{Integer.parseInt(modelId)}, null, Restriction.EQ));
                meters = meterDao.findByConditions(condition);
                if(meters.size() <= 0 ) {
                	log.info("Wrong ModelId");
            	}
            } else if(!"".equals(meterSerial) && meterSerial != null) {
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
	        ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 5, 6, TimeUnit.HOURS, new LinkedBlockingQueue());
	        String mcuId = null;
	        
	        for (Iterator<String> i = mculist.keySet().iterator(); i.hasNext();) {
	            mcuId = i.next();
	            executor.execute(new OndemandThread(mcuId, mculist.get(mcuId), startdate, enddate));
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

class OndemandThread implements Runnable {
    private static Log log = LogFactory.getLog(OndemandThread.class);
    
    String mcuId;
    List<String[]> mmlist;
    String startdate;
    String enddate;

    OndemandThread(String mcuId, List<String[]> mmlist, String startdate, String enddate) {
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
                        log.info("Mcu[" + mcuId + "] " + (i+1) + "/" + mmlist.size() + " MeterModel[" + meterModel + "] Meter[" + meterId + "] yyyymmdd[" + yyyymmdd + "]" );
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
                                	}
									else if(meterModel.contains("GE")) {  // Haiti - GE I-210+c
										mdata = cgw.cmdOnDemandMeter(mcuId, meterId, modemId, "", yyyymmdd, yyyymmdd);
									}
                                	else{
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
                        	}
                        	else if(meterModel.contains("GE")){   // Haiti - GE I-210+c
								mdata = cgw.cmdOnDemandMeter(mcuId, meterId, modemId, "", yyyymmdd, yyyymmdd);
							}
                        	else{
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
