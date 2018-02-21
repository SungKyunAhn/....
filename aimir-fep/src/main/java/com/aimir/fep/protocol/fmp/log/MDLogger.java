package com.aimir.fep.protocol.fmp.log;

import java.io.BufferedOutputStream;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.File;

import com.aimir.fep.meter.data.MDHistoryData;
import com.aimir.fep.protocol.fmp.frame.service.MDData;

/**
 * FileMDLogger.java
 *
 * Logger which logs to a file
 *
 * Created: Thu Dec 09 15:51:37 1999
 *
 * @author  Dongjin Park
 * @version 0.1
 */
public class MDLogger extends MessageLogger {
    private static Log log = LogFactory.getLog(MDLogger.class);
    
    /**
     * Constructs a FileMDLogger object
     */
    public MDLogger() throws IOException {
        super();
        // 2017.03.24 SP-629
        logDirName = "db/md";
    }

    @Override
    public String writeObject(Serializable obj) {
        ObjectOutputStream out = null;
        try
        {
            String mcuId = null;
            // 2017.03.24 SP-629
            if (obj instanceof MDData) {
                MDData mdData = (MDData)obj;
                mcuId = mdData.getMcuId();
            }
            else if (obj instanceof MDHistoryData) {
                MDHistoryData mdhd = (MDHistoryData)obj;
                mcuId = mdhd.getMcuId();
            }
            
            if (mcuId != null) {
                File f = null;
                f = new File(logDirName,"MDLog-" + mcuId + "-"
                        +UUID.randomUUID()+".log");
                out = new ObjectOutputStream(
                    new BufferedOutputStream(new FileOutputStream(f)));
                out.writeObject(obj);
                return f.getAbsolutePath();
            }
            else {
                log.warn("Serializable is not MDData or MDHistory");
            }
        }
        catch (Exception e) {
            log.error("********" + getClass().getName()
                    + " write() Failed *********",e);
        }
        finally {
            if (out != null) {
                try {
                    out.close();
                }
                catch (Exception e) {
                    log.error(e, e);
                }
            }
        }
        return null;
    }

    @Override
    public void backupObject(Serializable obj) {

        try
        {
            if (obj instanceof MDHistoryData) {
                MDHistoryData mdhd = (MDHistoryData)obj;
                File f = null;
                f = new File(getBackupDir(),"MDLog-" + mdhd.getMcuId() + "-"
                        +System.currentTimeMillis()+".log");
                ObjectOutputStream os = new ObjectOutputStream(
                    new BufferedOutputStream(new FileOutputStream(f)));
                os.writeObject(mdhd);
                os.close();
            }
        }
        catch (Exception e) {
            log.error("********" + getClass().getName() + " backup() Failed *********", e);
        }
    }
}

