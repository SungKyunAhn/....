package com.aimir.fep.protocol.fmp.log;

import java.io.BufferedOutputStream;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.fep.trap.common.FMPAlarmTask;
import com.aimir.fep.trap.common.FMPEventTask;
import com.aimir.notification.FMPTrap;

/**
 * NEventLogger.java
 * Logger which logs to a file
 * 
 * @author goodjob (goodjob@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2014-08-25 10:00:00 +0900 $,
 */
@Transactional
public class NEventLogger extends MessageLogger { 
    private Log log = LogFactory.getLog(NEventLogger.class);

    @Autowired
    FMPAlarmTask alarmTask;
    
    @Autowired
    FMPEventTask eventTask;
    
    /**
     * Constructs a FileEventLogger object
     */ 
    public NEventLogger() throws IOException { 
        super();
    } 

    @Override
    public String writeObject(Serializable obj) {

        try 
        { 
            FMPTrap trap = (FMPTrap)obj;
            File f = null;
            f = new File(logDirName,"FMPEventLog-"
                    + trap.getMcuId()+"-"
                    + trap.getCode()+"-"
                    +System.currentTimeMillis()+".log");
            ObjectOutputStream os = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(f)));
            os.writeObject(trap);
            os.close();
        } catch (Exception e) {     
            log.error("********" + getClass().getName() 
                    + " write() Failed *********",e); 
        } 
        return null;
    }
    
    @Override
    public void backupObject(Serializable obj) {

        try 
        { 
            FMPTrap trap = (FMPTrap)obj;
            File f = null;
            f = new File(getBackupDir(),"FMPEventLog-"
                    + trap.getMcuId()+"-"
                    + trap.getCode()+"-"
                    +System.currentTimeMillis()+".log");
            ObjectOutputStream os = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(f)));
            os.writeObject(trap);
            os.close();
        } catch (Exception e) {     
            log.error("********" + getClass().getName() 
                    + " backup() Failed *********",e); 
        } 
    }
    
    /**
     * MessageLogger의 sendLog를 사용하면 안된다.
     * ECS에서 알람과 이벤트 처리를 통합했다.
     * 2010.08.10
     */
    public void sendLog(final Serializable data) {
        // 2010.08.10 ECS의 메시지 처리를 통합함.
        // Vector rules = MetricUtil.findRule(trap);
        try {
            FMPTrap trap = (FMPTrap)data;
            if (trap.getCode().indexOf("206.") > -1) {
                // log.debug("TRAP[" + trap + "]");
                alarmTask.access(trap);
            }
            else {
                // log.debug("TRAP[" + trap + "]");
                eventTask.access(trap);
            }
        }
        catch (Exception e) {
        	log.error(e,e);
        }
    }
}

