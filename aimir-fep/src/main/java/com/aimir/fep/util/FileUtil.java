package com.aimir.fep.util;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Firmware Utility Class 
 * 
 * @author Y.S Kim
 * @version $Rev: 1 $, $Date: 2005-12-07 15:59:15 +0900 $,
 */
public class FileUtil
{
    private static Log log = LogFactory.getLog(FileUtil.class);

    private static String firmwarehome = null;
    private static String uploadhome = null;
    private static String downloadhome = null;
    static {
        firmwarehome = FMPProperty.getProperty("Firmware.dir");
        uploadhome = FMPProperty.getProperty("File.upload.dir");
        downloadhome = FMPProperty.getProperty("File.download.dir");
    }

    /**
     * get file
     * @return file full name
     */
    public static File getFirmwareFile(String filename) throws Exception
    {
        File f = new File(firmwarehome+"/"+filename);
        if (f.exists())
        {
            log.debug("file "+filename+" is exist in "+firmwarehome);
            return f;
        }
        else
        {
            log.error("file "+filename+" is not exist in "+firmwarehome);
            throw new Exception("file "+filename+" is not exist in "+firmwarehome);
        }
    }

    /**
     * get file
     * @return file full name
     */
    public static File getUploadFile(String filename) throws Exception
    {
        File f = new File(uploadhome+"/"+filename);
        if (f.exists())
        {
            log.debug("file "+filename+" is exist in "+uploadhome);
            return f;
        }
        else
        {
            log.error("file "+filename+" is not exist in "+uploadhome);
            throw new Exception("file "+filename+" is not exist in "+uploadhome);
        }
    }
}
