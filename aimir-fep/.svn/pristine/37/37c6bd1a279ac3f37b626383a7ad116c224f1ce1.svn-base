package com.aimir.fep.schedule.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.MeterStatus;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.command.ws.server.ResponseMap;
import com.aimir.fep.meter.AbstractMDSaver;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.model.device.Meter;
import com.aimir.model.system.Code;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;


@Service
public class SetModemModeTask 
{
    private static Log log = LogFactory.getLog(SetModemModeTask.class);
    
    @Autowired
    MCUDao mcuDao;
    
    @Autowired
    ModemDao modemDao;
    
    @Autowired
    MeterDao meterDao;
    
    @Resource(name="transactionManager")
    JpaTransactionManager txmanager;

	long _timeDiffThreshold = 504576000;
    int  _maxThreadWorker = 10;
    long _timeout = 3600;
    int	 _setMode = 1;			// PUSH:0x00, POLLING:0x01
    boolean _isAllData = false;    
    
    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"/config/spring-fep-schedule.xml"}); 
        DataUtil.setApplicationContext(ctx);
        SetModemModeTask task = ctx.getBean(SetModemModeTask.class);
        log.info("======================== SetModemModeTask start. ========================");
        task.execute(args);
        log.info("======================== SetModemModeTask end. ========================");
        System.exit(0);
    }

    private List<Meter> getMeter(long diffThreshold) throws Exception {
        
        try {
            Set<Condition> condition = new HashSet<Condition>();           
            Code comError = CommonConstants.getMeterStatusByName(MeterStatus.CommError.name());
            Code powerDown = CommonConstants.getMeterStatusByName(MeterStatus.PowerDown.name());
            
            // Factory status
            condition = new HashSet<Condition>();
            condition.add(new Condition("timeDiff", new Object[]{diffThreshold}, null, Restriction.GE));
            condition.add(new Condition("meterStatus", new Object[]{comError.getId()}, null, Restriction.NE));
            condition.add(new Condition("meterStatus", new Object[]{powerDown.getId()}, null, Restriction.NE));
            
            List<Meter> tmplist = meterDao.findByConditions(condition);
            List<Meter> list = new ArrayList<Meter>();
            list.addAll(tmplist);
        	log.debug("########## " + diffThreshold + "<= meter.timeDiff" + " Count[" + tmplist.size() + "] ##########" );
            for (Meter m : tmplist) {
            	log.debug("Meter[" + (m.getMdsId()== null ? "" : m.getMdsId()) + "] difftime[" + m.getTimeDiff() + "]");
            }            
            
            condition = new HashSet<Condition>();
            condition.add(new Condition("timeDiff", new Object[]{-1*diffThreshold}, null, Restriction.LE));
            condition.add(new Condition("meterStatus", new Object[]{comError.getId()}, null, Restriction.NE));
            condition.add(new Condition("meterStatus", new Object[]{powerDown.getId()}, null, Restriction.NE));
            
            tmplist.clear();
            tmplist = meterDao.findByConditions(condition);
            list.addAll(tmplist);
        	log.debug("########## " + "meter.timeDiff <= " + -1*diffThreshold + " Count[" + tmplist.size() + "] ##########" );
            for (Meter m : tmplist) {
            	log.debug("Meter[" + (m.getMdsId()== null ? "" : m.getMdsId()) + "] difftime[" + m.getTimeDiff() + "]");
            }                     
            
            return list;
        }
        catch (Exception e) {
        	log.debug(e.getMessage());
        	throw e;
        }
        finally {
        }
    }
    
    public void execute(String[] args) {

        
        TransactionStatus txstatus = null;
        
        _timeDiffThreshold = Long.parseLong(FMPProperty.getProperty("soria.task.setmodemmode.diffthreshold", "504576000"));        
        _maxThreadWorker = Integer.parseInt(FMPProperty.getProperty("soria.task.setmodemmode.maxworker", "10"));
        _timeout = Integer.parseInt(FMPProperty.getProperty("soria.task.setmodemmode.timeout", "3600"));
        _setMode = Integer.parseInt(FMPProperty.getProperty("soria.task.setmodemmode.mode", "1"));
        if (Integer.parseInt(FMPProperty.getProperty("soria.task.setmodemmode.alldata", "0")) == 1) {
        	_isAllData = true;
        }
        else {
        	_isAllData = false;
        }
        log.info("Start set ModemMode maxThreadWorker[" + _maxThreadWorker + "]");

        try {
        	txstatus = txmanager.getTransaction(null);

        	List<Meter> meterlist = new ArrayList<Meter>();
        	if (_isAllData) {
        		meterlist = meterDao.getAll();
        	}
        	else {
        		meterlist = getMeter(_timeDiffThreshold);
        	}
        	
        	List<Meter> rflist = new ArrayList<Meter>();
        	for (Meter m : meterlist) {
        		if((m.getModem() != null)&&(m.getModem().getModemType() == ModemType.SubGiga)) {
        			rflist.add(m);
        		}        		
        	}
        	
            log.info("Total Meter to need set ModemMode ["+ rflist.size() + "]");
            int cnt = 0;

            ExecutorService pool = Executors.newFixedThreadPool(_maxThreadWorker);
            setModemModeThread threads[] = new setModemModeThread[rflist.size()];
    		int	i = 0;
            
            for (Meter m : rflist) {
                log.info(cnt++ + ": Meter[" + m.getMdsId() + "] Modem[" + m.getModem().getDeviceSerial() + "] set ModemMode");
                threads[i] = new setModemModeThread(m.getMdsId());
    			pool.execute(threads[i]);
    			i++;
            }

            log.info("ExecutorService shutdown.");
            pool.shutdown();
            log.info("ExecutorService awaitTermination. [" + _timeout + "]sec");
            pool.awaitTermination(_timeout, TimeUnit.SECONDS);

        }
        catch (Exception ee) {}
        finally {
            if (txstatus != null && !txstatus.isCompleted())
                txmanager.commit(txstatus);
        }
    }
    
    protected class setModemModeThread extends Thread {
    	String meterId;
    	Meter meter = null;
    	CommandGW commandGw = DataUtil.getBean(CommandGW.class);
    	
    	public setModemModeThread(String id) {
    		try {
	    		meterId = id;
	            meter = meterDao.get(meterId);
            }
            catch (Exception ee) {}
    	}
    	public void run() {
    		try {
	            if (meter.getModem() != null) {
	            	log.info("ThreadID[" + Thread.currentThread().getId() + 
	            			"] cmdModemMode() thread start. Meter[" + meterId + "] Modem[" + meter.getModem().getDeviceSerial() + "]");
	
	            	// requestType="SET"
	            	// mode = 1 (PUSH:0x00,POLLING:0x01)
	            	commandGw.cmdModemMode(String.valueOf(meter.getModemId()), "SET", _setMode );
	            	
	            	log.info("ThreadID[" + Thread.currentThread().getId() + 
	            			"] cmdModemMode() thread end. Meter[" + meterId + "] Modem[" + meter.getModem().getDeviceSerial() + "]");
	            }
	            else {
	            	log.debug("Meter[" + meterId + "] getModem() is null.");
	            }
    		}
            catch(Exception e) {
            	log.debug(e,e);
            }
    	}
    }    
}
