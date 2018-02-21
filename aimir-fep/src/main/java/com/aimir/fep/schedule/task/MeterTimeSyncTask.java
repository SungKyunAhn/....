package com.aimir.fep.schedule.task;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.aimir.fep.command.ws.server.ResponseMap;
import com.aimir.fep.meter.AbstractMDSaver;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.Meter;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;

/**
 * 모뎀 정보를 가져온다. 나중에 버전 정보가 없는 모뎀만 조회하여 스캐닝하는 기능 추가해야 함.
 * @author elevas
 *
 */
@Service
public class MeterTimeSyncTask 
{
    private static Log log = LogFactory.getLog(MeterTimeSyncTask.class);
    
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
        MeterTimeSyncTask task = ctx.getBean(MeterTimeSyncTask.class);
        log.info("args.len[" + args.length + "] val[" + args[0] + "]");
        if (args[0] != null && !args[0].contains("timeDiff")) {
            task.execute(args);
        }
        System.exit(0);
    }
    
    private List<Meter> getMeter(long timeDiff) throws Exception {
        TransactionStatus txstatus = null;
        
        try {
            txstatus = txmanager.getTransaction(null);
            Set<Condition> condition = new HashSet<Condition>();
            condition.add(new Condition("timeDiff", new Object[]{timeDiff}, null, Restriction.GE));
            condition.add(new Condition("meterStatus", new Object[]{"m"}, null, Restriction.ALIAS));
            condition.add(new Condition("m.name", new Object[]{"Delete"}, null, Restriction.NOT));
            
            List<Meter> list = meterDao.findByConditions(condition);
            
            condition = new HashSet<Condition>();
            condition.add(new Condition("timeDiff", new Object[]{-1*timeDiff}, null, Restriction.LE));
            condition.add(new Condition("meterStatus", new Object[]{"m"}, null, Restriction.ALIAS));
            condition.add(new Condition("m.name", new Object[]{"Delete"}, null, Restriction.NOT));
            
            list.addAll(meterDao.findByConditions(condition));
            
            return list;
        }
        finally {
            if (txstatus != null) txmanager.commit(txstatus);
        }
    }
    
    public void execute(String[] args) {
        log.info("Start Meter Time Sync timeDiff[" + args[0] + "]");
        
        long _timeDiff = Long.parseLong(args[0]);
        
        try {
            List<Meter> meterlist = getMeter(_timeDiff);
            log.info("Total Meter to need time sync ["+ meterlist.size() + "]");
            int cnt = 0;
            for (Meter m : meterlist) {
                log.info(cnt++ + "] Meter[" + m.getMdsId() + "] sync time");
                syncTime(m.getMdsId());
            }
        }
        catch (Exception ee) {}
    }
    
    public ResponseMap syncTime(String meterId) throws Exception
    {
        TransactionStatus txstatus = null;
        try {
            txstatus = txmanager.getTransaction(null);
            Meter meter = meterDao.get(meterId);
            ResponseMap map = new ResponseMap();
            if (meter.getModel() != null && meter.getModem() != null) {
                Class clazz = Class.forName(meter.getModel().getDeviceConfig().getSaverName());
                AbstractMDSaver saver = (AbstractMDSaver)DataUtil.getBean(clazz);
                
                Map<String, String> response = new HashMap<String, String>();
                if (meter.getModem().getMcu() == null)
                    response.put("Response", saver.syncTime(meter.getModem().getDeviceSerial(), meterId));
                else
                    response.put("Response", saver.syncTime(meter.getModem().getMcu().getSysID(), meterId));
                
                map.setResponse(response);
            }
            return map;
        }
        finally {
            if (txstatus != null && !txstatus.isCompleted())
                txmanager.commit(txstatus);
        }
    }
}
