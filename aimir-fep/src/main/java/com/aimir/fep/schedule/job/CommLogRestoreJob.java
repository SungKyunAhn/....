package com.aimir.fep.schedule.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.transaction.TransactionStatus;

import com.aimir.fep.protocol.fmp.processor.CommLogProcessor;
import com.aimir.fep.util.DataUtil;

public class CommLogRestoreJob extends QuartzJobBean {
    private static Log log = LogFactory.getLog(CommLogRestoreJob.class);
    
    @Override
    protected void executeInternal(JobExecutionContext context)
            throws JobExecutionException {

        JpaTransactionManager tx = null;
        TransactionStatus txStatus = null;
        
        try {
            tx = (JpaTransactionManager)DataUtil.getBean("transactionManager");
            txStatus = tx.getTransaction(null);
            
            CommLogProcessor processor = DataUtil.getBean(CommLogProcessor.class);
            
            processor.restore();
            
            tx.commit(txStatus);
        }
        catch (Exception e) {
            log.error(e);
            if (txStatus != null) tx.rollback(txStatus);
        }
    }
}
