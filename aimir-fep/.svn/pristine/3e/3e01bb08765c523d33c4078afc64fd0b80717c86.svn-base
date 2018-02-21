package com.aimir.fep.protocol.fmp.log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.AMUMDHistoryData;

/**
 * File AMU Metering Logger
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 2. 24. 오후 5:10:32$
 */
public class AMUMDLogger extends MessageLogger {
	
    private Log log = LogFactory.getLog(AMUMDLogger.class);

    /**
     * Constructs 
     */ 
    public AMUMDLogger() throws IOException { 
        super();
    } 

    
	@Override
	public String writeObject(Serializable obj) {
		
		try
        {
			if (obj instanceof AMUMDHistoryData) {
            	AMUMDHistoryData mdhd = (AMUMDHistoryData)obj;
                File f = null;
                f = new File(logDirName,"AMUMDLog-" + mdhd.getSourceAddr() + "-"
                        +System.currentTimeMillis()+".log");
                ObjectOutputStream os = new ObjectOutputStream(
                    new BufferedOutputStream(new FileOutputStream(f)));
                os.writeObject(mdhd);
                os.close();
            }
        } catch (Exception e) {
            log.error("********" + getClass().getName()
                    + " write() Failed *********",e);
        }
		return null;
    }


	@Override
	public void backupObject(Serializable obj) {
		
	}
}