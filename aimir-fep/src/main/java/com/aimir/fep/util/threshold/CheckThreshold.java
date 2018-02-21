// INSERT SP-193
package com.aimir.fep.util.threshold;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.constants.CommonConstants.ThresholdName;
import com.aimir.dao.device.ThresholdDao;
import com.aimir.dao.device.ThresholdWarningDao;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.Threshold;
import com.aimir.model.device.ThresholdWarning;
import com.aimir.util.IPUtil;

public class CheckThreshold{
	
	private static Log log = LogFactory.getLog(CheckThreshold.class);
	
    private static ThresholdDao thresholdDao = DataUtil.getBean(ThresholdDao.class);
    private static ThresholdWarningDao ThresholdWarningDao = DataUtil.getBean(ThresholdWarningDao.class);
 
    private static JpaTransactionManager txManager = 
            (JpaTransactionManager)DataUtil.getBean("transactionManager");
    
    // INSERT START SP-285
    private static boolean trimFlag = true;
    public static void updateCount(String ip, ThresholdName name, boolean trimport)
    {
    	if (ip == null) {
    		log.info("Threshold addr is null.");
    		return;
    	}

        ip = ip.replace("/", "");
    	trimFlag = trimport;
    	updateCount(ip, name);
    	trimFlag = true;
    }
    
    public static void addThroughputWarning(String ip, Integer value, boolean trimport)
    {
    	if (ip == null) {
    		log.info("Threshold addr is null.");
    		return;
    	}

    	try {    	
	        ip = ip.replace("/", "");
	    	trimFlag = trimport;
	    	addThroughputWarning(ip, value);
	    	trimFlag = true;
    	}
    	catch (Exception e) {
    		log.error(e, e);
    	}
    }
    // INSERT END SP-285
    
    public static void updateCount(String ip, ThresholdName name)
    {
        TransactionStatus txStatus = null;
        try {
        	// UPDATE START SP-285
//        	String addr = IPUtil.formatTrim(ip);
        	String addr = null;
        	if (trimFlag) {
        		addr = IPUtil.formatTrim(ip);
        	}
        	else {
        		addr = IPUtil.format(ip);
        	}
        	// UPDATE END SP-285
        	if (addr == null) {
        		log.info("Threshold addr is null.");
        		return;
        	}
        	Threshold threshold = thresholdDao.getThresholdByname(name.name());
        	if (threshold == null) {
        		log.info("Threshold setting is not exist.");
        		return;
        	}
    		ThresholdWarning data = ThresholdWarningDao.getThresholdWarning(addr, threshold.getId());
 
    		txStatus = txManager.getTransaction(null);
    		// new
    		if (data==null) {
    			data = new ThresholdWarning();
    			data.setIpAddr(addr);
    			data.setThresholdId(threshold.getId());
    			data.setValue(1);
    		}  		
    		// update 
    		else {
    			int count = data.getValue();
    			data.setValue(count+1);    			
    		}

    		ThresholdWarningDao.saveOrUpdate(data);
    		
            txManager.commit(txStatus);
                       
        }
        catch (Exception e) {
            log.error(e, e);
            if (txManager != null)
                txManager.rollback(txStatus);
        }
    	      	    	
    }

    public static void addThroughputWarning(String ip, Integer value)
    throws Exception
    {
        TransactionStatus txStatus = null;
        try {
        	// UPDATE START SP-285
//        	String addr = IPUtil.formatTrim(ip);
        	String addr = null;
        	if (trimFlag) {
        		addr = IPUtil.formatTrim(ip);
        	}
        	else {
        		addr = IPUtil.format(ip);
        	}
        	// UPDATE END SP-285
        	Threshold threshold = thresholdDao.getThresholdByname(ThresholdName.THROUGHPUT.name());
        	if (threshold == null) {
        		log.info("Threshold setting is not exist.");
        		return;
        	}
    		ThresholdWarning data = ThresholdWarningDao.getThresholdWarning(addr, threshold.getId());
 
    		txStatus = txManager.getTransaction(null);
    		// new
    		if (data==null) {
    			data = new ThresholdWarning();
    			data.setIpAddr(addr);
    			data.setThresholdId(threshold.getId());
    			data.setValue(value);
    		}  		
    		// update 
    		else {
    			data.setValue(value);    			
    		}

    		ThresholdWarningDao.saveOrUpdate(data);
    		
            txManager.commit(txStatus);
                       
        }
        catch (Exception e) {
            log.error(e, e);
            if (txManager != null)
                txManager.rollback(txStatus);
            throw e;
        }    	
    }
    
    public static boolean isUnderThroughput(Integer value)
    throws Exception
    {
        try {
        	if(thresholdDao == null) log.info("thresholdDao is null");
        	
        	Threshold threshold = thresholdDao.getThresholdByname(ThresholdName.THROUGHPUT.name());
        	if (threshold == null) {
        		log.info("Threshold setting is not exist.");
        		return false;
        	}
        	
        	if (value < threshold.getLimit()) {
        		return true;
        	}
            
        	return false;
        }
        catch (Exception e) {
            log.error(e, e);
            throw e;
        }    	    	    	
    }

    public static void addMeterTimeGapWarning(Integer id, Integer value)
    throws Exception
    {
        TransactionStatus txStatus = null;
        try {
        	Threshold threshold = thresholdDao.getThresholdByname(ThresholdName.METER_TIME_GAP.name());
        	if (threshold == null) {
        		log.info("Threshold setting is not exist.");
        		return;
        	}
    		ThresholdWarning data = ThresholdWarningDao.getThresholdWarning(DeviceType.Meter, id, threshold.getId());
 
    		txStatus = txManager.getTransaction(null);
    		// new
    		if (data==null) {
    			data = new ThresholdWarning();
    			data.setDeviceType(DeviceType.Meter.getCode());
    			data.setDeviceId(id);
    			data.setThresholdId(threshold.getId());
    			data.setValue(value);
    		}  		
    		// update 
    		else {
    			data.setValue(value);    			
    		}

    		ThresholdWarningDao.saveOrUpdate(data);
    		
            txManager.commit(txStatus);
                       
        }
        catch (Exception e) {
            log.error(e, e);
            if (txManager != null)
                txManager.rollback(txStatus);
            throw e;
        }    	
    }
        
    public static boolean isOverMeterTimeGap(Integer value)
    throws Exception
    {
        try {
        	Threshold threshold = thresholdDao.getThresholdByname(ThresholdName.METER_TIME_GAP.name());
        	if (threshold == null) {
        		log.info("Threshold setting is not exist.");
        		return false;
        	}
        	
        	if (value > threshold.getLimit()) {
        		return true;
        	}
            
        	return false;
        }
        catch (Exception e) {
            log.error(e, e);
            throw e;
        }    	    	    	
    }       
}


