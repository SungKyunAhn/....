package com.aimir.fep.util;

import java.io.BufferedInputStream;
import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DiffGeneratorUtil
{
    private static Log log = LogFactory.getLog(DiffGeneratorUtil.class);

    private static final String DIFFPROC_WIN = "bsdiff.exe";
    private static final String DIFFPROC_LINUX = "bsdiff";
    private static final String[] COMMAND_WIN = {"cmd", "/C"};
    private static final String[] COMMAND_LINUX = {"/bin/sh","-c"};
    private static final long MAX_PROCESS_RUNNING_TIME = 30 * 1000;
    
    public static String makeDiff(String oldFileName, String newFileName, String diffFileName) throws Exception{

        String processDir = FMPProperty.getProperty("firmware.tooldir"); 
        String comm[] = new String[3];

        if(System.getProperty("os.name").toLowerCase().indexOf("window")>-1) {
            comm[0] = COMMAND_WIN[0];
            comm[1] = COMMAND_WIN[1];
            comm[2] = DIFFPROC_WIN + " " + oldFileName + " " + newFileName + " " + diffFileName;
        } else {
            comm[0] = COMMAND_LINUX[0];
            comm[1] = COMMAND_LINUX[1];
            comm[2] = DIFFPROC_LINUX + " " + oldFileName + " " + newFileName + " " + diffFileName;
        }

        StringBuffer ret = new StringBuffer();
        long start = System.currentTimeMillis();
        try {
            //Start process
            Process ls_proc = Runtime.getRuntime().exec(comm, null, new File(processDir));
            //Get input and error streams
            BufferedInputStream ls_in = new BufferedInputStream(ls_proc.getInputStream());
            BufferedInputStream ls_err = new BufferedInputStream(ls_proc.getErrorStream());
            boolean end = false;
            while (!end) {
                int c = 0;
                while ((ls_err.available() > 0) && (++c <= 1000)) {
                    ret.append(ls_err.read());
                }
                c = 0;
                while ((ls_in.available() > 0) && (++c <= 1000)) {
                    ret.append(ls_in.read());
                }
                try {
                    ls_proc.exitValue();
                    //if the process has not finished, an exception is thrown
                    //else
                    while (ls_err.available() > 0)
                        ret.append(ls_err.read());
                    while (ls_in.available() > 0)
                        ret.append(ls_in.read());
                    end = true;
                }
                catch (IllegalThreadStateException ex) {
                    //Process is running
                }
                //The process is not allowed to run longer than given time.
                if (System.currentTimeMillis() - start > MAX_PROCESS_RUNNING_TIME) {
                    ls_proc.destroy();
                    end = true;
                    ret.append("!!!! Process has timed out, destroyed !!!!!");
                    new Exception("Process has timed out, destroyed");
                }
                try {
                    Thread.sleep(50);
                }
                catch (InterruptedException ie) {}
            }
        }
        catch (Exception e) {
            ret.append("Error: " + e);
            log.error( ret.toString(),e);
            throw new Exception(e.getMessage());
        }
        
        log.debug( ret.toString() );

        return newFileName;
    }
}
