package com.aimir.fep.protocol.emnv.log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.fep.meter.data.MDHistoryData;
import com.aimir.fep.protocol.fmp.frame.service.MDData;
import com.aimir.fep.protocol.fmp.log.MessageLogger;

public class EMnVMDLogger extends MessageLogger {
	private static Logger log = LoggerFactory.getLogger(EMnVMDLogger.class);

	/**
	 * Constructs a FileMDLogger object
	 */
	private EMnVMDLogger() throws IOException {
		super();
		logDirName = "db/md";
	}

	@Override
    public String writeObject(Serializable obj) {
	    log.debug("EMnVMDLogger() Object ==> {}", obj.toString());
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
                f = new File(logDirName,"EMnVMDLog-" + mcuId + "-"
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
                    log.error(e.getMessage(), e);
                }
            }
        }
        return null;
    }
	
	@Override
	public void backupObject(Serializable obj) {

		try {
			if (obj instanceof MDHistoryData) {
				MDHistoryData mdhd = (MDHistoryData) obj;
				File f = null;
				f = new File(getBackupDir(), "EMnVMDLog-" + mdhd.getMcuId() + "-" + UUID.randomUUID() + ".log");
				ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(f)));
				os.writeObject(mdhd);
				os.close();
			}
		} catch (Exception e) {
			log.error("********" + getClass().getName() + " backup() Failed *********", e);
		}
	}
}
