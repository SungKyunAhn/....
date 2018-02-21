// INSERT SP-121
package com.aimir.fep.protocol.security;

import java.net.InetSocketAddress;
import com.aimir.util.DateTimeUtil;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.model.device.AuthDelay;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionStatus;

import com.aimir.dao.device.AuthDelayDao;

public class AuthDelayCheck{
	
	private static Log log = LogFactory.getLog(AuthDelayCheck.class);
	
	private static AuthDelayDao authdelayDao = DataUtil.getBean(AuthDelayDao.class);

    private static JpaTransactionManager txManager = 
            (JpaTransactionManager)DataUtil.getBean("transactionManager");
    
    private static Integer authLimitCnt =  Integer.valueOf(FMPProperty.getProperty("protocol.security.delay.limit.count","3"));
    private static Integer authLimitTime =  Integer.valueOf(FMPProperty.getProperty("protocol.security.delay.limit.time","60"));	// second

    public static void updateAuthDelay(boolean authresult, InetSocketAddress sockaddr)
    {
        TransactionStatus txStatus = null;
        try {
        	String ipaddress = sockaddr.getAddress().toString();
        	AuthDelay data = authdelayDao.getAuthDelay(ipaddress);
        	if (data == null) {
        		log.info("AuthDelay is not exist.");
        		if ( authresult == true) {
        			return;
        		}
        	}
    		txStatus = txManager.getTransaction(null);
    		if ( authresult == false) {
    			// authentication fail
            	String currTime = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");
    			// add
	    		if (data==null) {
	    			log.info("AuthDelay new record add. IP["+ipaddress+"]");
	    			data = new AuthDelay();
	    			data.setIpAddress(ipaddress);
	    			data.setErrorCnt(1);
	    			data.setLastDate(currTime);
	    		}  		
	    		// update 
	    		else {
	    			data.setErrorCnt(data.getErrorCnt()+1);    			
	    			data.setLastDate(currTime);
	    		}
    		} else {
    			// authentication success
    			data.setErrorCnt(0);
    			data.setLastDate(null);
    		}
    		authdelayDao.saveOrUpdate(data);
            txManager.commit(txStatus);
        }
        catch (Exception e) {
            log.error(e, e);
            if (txManager != null)
                txManager.rollback(txStatus);
        }
    	      	    	
    }

    public static boolean isAuthDelay(InetSocketAddress sockaddr)
    throws Exception
    {
    	TransactionStatus txStatus = null;
        try {
        	String ipaddress = sockaddr.getAddress().toString();
        	AuthDelay authdelay = authdelayDao.getAuthDelay(ipaddress, authLimitCnt);
        	if (authdelay == null) {
        		log.info("Authentication permit: ip[" + ipaddress +"]");
        		return true;
        	}
        	Long lastTime = DateTimeUtil.getDateFromYYYYMMDDHHMMSS(authdelay.getLastDate()).getTime();
        	Long currTime = 
            		DateTimeUtil.getDateFromYYYYMMDDHHMMSS(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss")).getTime();
        	Long interval = Math.abs(currTime-lastTime)/1000; // sec
        	Integer limitTime = ( (authdelay.getErrorCnt()/authLimitCnt) >= 5 ? authLimitTime*5 : authLimitTime*(authdelay.getErrorCnt()/authLimitCnt));
        	log.info("Authentication ip["+ipaddress+"],limitTime:" + limitTime + " sec, curr-lastdate:" + interval + " sec");
        	if (interval <= limitTime ) {
        		log.info("Authentication forbit: ip["+ipaddress+"], permit is after " + (limitTime-interval) + " sec");
        		return false;
        	}
            
    		log.info("Authentication permit: ip[" + ipaddress +"]");
        	return true;
        }
        catch (Exception e) {
            log.error(e, e);
            throw e;
        }    	    	    	
    }
}


