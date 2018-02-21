package com.aimir.fep.schedule.task;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.MCU;
import com.aimir.util.TimeUtil;

/**
 * DCU Time sync
 *
 */
@Service
public class McuTimeSyncTask 
{
    private static Log log = LogFactory.getLog(McuTimeSyncTask.class);
    
    @Autowired
    MCUDao mcuDao;
    
    @Autowired
    ModemDao modemDao;
    
    @Autowired
    MeterDao meterDao;
    
    @Resource(name="transactionManager")
    JpaTransactionManager txmanager;
    
    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"/config/spring.xml"}); 
        DataUtil.setApplicationContext(ctx);
        McuTimeSyncTask task = ctx.getBean(McuTimeSyncTask.class);
        task.execute();
        System.exit(0);
    }
    
    private List<MCU> getMCU() throws Exception {
        TransactionStatus txstatus = null;
        
        try {
            txstatus = txmanager.getTransaction(null);
            List<MCU> list = mcuDao.getAll();
            return list;
        }
        finally {
            if (txstatus != null) txmanager.commit(txstatus);
        }
    }
    
    public void execute() {
    	log.info("############################# McuTimeSync Scheduler Start ##################################");
    	try {
            List<MCU> mcuList = getMCU();
            log.info("Total MCU to need time sync ["+ mcuList.size() + "]");
            int cnt = 0;
            for (MCU m : mcuList) {
                log.info("["+ cnt++ + "] MCU[" + m.getSysID() + "] sync time Start>>>>>>>");
                syncTime(m.getSysID());
            }
        }
        catch (Exception ee) {}
    	log.info("############################# McuTimeSync Scheduler End ##################################");
    }
    
    public void syncTime(String sysID)
    {
        TransactionStatus txstatus = null;
        try {
        	CommandGW cgw = DataUtil.getBean(CommandGW.class);
        	
            txstatus = txmanager.getTransaction(null);
            
            String time = TimeUtil.getCurrentTime();
        	cgw.cmdMcuSetTime(sysID, time);
        	log.info("[DCU:" + sysID + "] SetTime[" + time + "] Success");
            
        } catch(Exception e) {
        	log.error(e,e);
        	log.warn("[DCU:" + sysID + "] SetTime Fail");
        } finally {
            if (txstatus != null && !txstatus.isCompleted())
                txmanager.commit(txstatus);
        }
    }
}
