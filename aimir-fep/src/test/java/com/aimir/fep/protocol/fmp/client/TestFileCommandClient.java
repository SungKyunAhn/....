package com.aimir.fep.protocol.fmp.client;

import com.aimir.fep.protocol.fmp.client.Client;
import com.aimir.fep.protocol.fmp.client.ClientFactory;
import com.aimir.fep.protocol.fmp.common.GPRSTarget;
import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.protocol.fmp.datatype.OID;
import com.aimir.fep.protocol.fmp.datatype.SMIValue;
import com.aimir.fep.protocol.fmp.frame.service.CommandData;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.MIBUtil;
import com.aimir.util.TimeUtil;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ByteArrayOutputStream;
import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Send Command Test Class
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class TestFileCommandClient
{
    private static Log _log = LogFactory.getLog(TestFileCommandClient.class);
    
    private String filename = null;
    @SuppressWarnings("unused")
	private String fname = null;
    private int DEFAULT_READ_LENGTH = 1024;
    private File file = null;
    /**
     * constructor
     */
    public TestFileCommandClient(String fileName)
    {
        this.filename = fileName;
        int idx = fileName.lastIndexOf("/");
        if(idx > 0)
            fname = fileName.substring(idx+1);
        else
            fname = fileName;
        file = new File(filename);
    }

    private byte[] readFile() throws Exception { 
        InputStream is = new FileInputStream(file); 
        try { 
            ByteArrayOutputStream baos = 
                new ByteArrayOutputStream((int) file.length()); 
            // initial size 
            byte[] buffer = new byte[DEFAULT_READ_LENGTH]; 
            int bytesRead = is.read(buffer, 0, buffer.length); 
            while (bytesRead > 0) { 
                baos.write(buffer, 0, bytesRead); 
                bytesRead = is.read(buffer, 0, buffer.length); 
            } 
            return baos.toByteArray(); 
        } finally 
        { 
            if (is != null) 
            { 
                is.close(); 
            } 
        } 
    }
    public static SMIValue getStringSMIValue(String value) 
    { 
        SMIValue smiValue = new SMIValue();
        smiValue.setOid(new OID("1.11.0"));
        OCTET variable = new OCTET(value);
        smiValue.setVariable(variable);
        return smiValue;
     }


    @SuppressWarnings("unused")
	private long getFilePermission()
    {
        long permval = 777;
        try
        {
            String command = "ls -al "+filename;
            Process p = Runtime.getRuntime().exec(command);
            StringBuffer sb = new StringBuffer();
            int len = 0;
            byte[] bx = new byte[128];
            String line =null;
            while((len = p.getInputStream().read(bx)) > 0)
            {
                line = new String(bx,0,len);
                sb.append(line);
            }
            line = sb.toString();
            String perm = line.substring(1,line.indexOf(" ")+1);
            long res = 0;
            long tmp = 0;
            long sum = 0;
            for(int i = 0 ; i < perm.length() ; i++)
            {
                tmp = 0;
                if(perm.charAt(i) != '-')
                {
                    tmp = ((3-((i+1)%3))%3)*2; 
                    if(tmp == 0) 
                        tmp = 1; 
                } 
                sum+=tmp; 
                if(((i+1)%3) == 0 ) 
                { 
                    res=res*10; 
                    res += sum; 
                    sum = 0; 
                }
            }
            permval = res;
        }
        catch(Exception ex) {}

        return permval;
    }


    @SuppressWarnings("unused")
	private SMIValue getSMIValueFileData() throws Exception
    {
        SMIValue smiValue = new SMIValue();
        smiValue.setOid(new OID("1.12.0"));
        byte[] bx = readFile();
        OCTET variable = new OCTET(bx);
        smiValue.setVariable(variable);
        _log.info("getSMIValueFileData length["
                + bx.length+"]");
        return smiValue;
    }

    @SuppressWarnings("unused")
	private void getSMIValueFileDatas(CommandData command) 
        throws Exception
    {
        byte[] bx = readFile();
        int divsize = 50000;
        int seq = bx.length / divsize;
        int remain = bx.length % divsize;
        byte[] bxx = null;
        for(int i = 0 ; i < seq ; i++)
        {
            bxx = new byte[divsize];
            System.arraycopy(bx,i*divsize,bxx,0,bxx.length);
            command.append(DataUtil.getSMIValue(new OCTET(bxx)));
        }

        if(remain > 0)
        {
            bxx = new byte[remain];
            System.arraycopy(bx,seq*divsize,bxx,0,bxx.length);
            command.append(DataUtil.getSMIValue(new OCTET(bxx)));
        }

        _log.info("getSMIValueFileData length["
                + bx.length+"]");
    }

    /**
     * start to send command 
     *
     * @throws Exception
     */
    public void cmdInstallFile() throws Exception
    {
        if(!file.exists())
        {
            throw new Exception("File["+filename+"] does not exist");
        }
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis()+(1000*60*60));
        CommandData command = DataUtil.getFirmwareCommandData(
                filename,1,TimeUtil.getFormatTime(cal)); 

        GPRSTarget target = new GPRSTarget("187.1.5.235",8000);
        target.setTargetId("2000");
        Client client = ClientFactory.getClient(target);
        command = client.sendCommand(command);
        _log.info("command:"+command);

    }

    private void makeFile(String downloadfile, CommandData command) 
        throws Exception
    {
        String mcuId = command.getMcuId();
        String dfile = downloadfile.substring(
                downloadfile.lastIndexOf("/")+1);
        String ddir = FMPProperty.getProperty("file.download.dir");
        dfile = ddir+"/"+mcuId+"."+dfile;
        _log.info("download file["+dfile+"]");
        File file = new File(dfile);
        MIBUtil mibUtil = MIBUtil.getInstance();
        if(command.getErrCode().getValue() > 0)
        {
            throw new Exception("Command["
                    +mibUtil.getName(command.getCmd().getValue())
                    +"]  failed : ERROR["+command.getErrCode()
                    +"]");
        }
        SMIValue[] smiValues = command.getSMIValue();
        _log.info("filesize["+
                smiValues[0].getVariable().toString()+"]");
        _log.info("fileMode["+
                smiValues[1].getVariable().toString()+"]");
        _log.info("fileTime["+
                smiValues[2].getVariable().toString()+"]");

        FileOutputStream fos = new FileOutputStream(file); 
        byte[] bx = null;
        for(int i = 3 ; i < smiValues.length ; i++)
        {
            bx = ((OCTET)smiValues[i].getVariable()).getValue();
            fos.write(bx,0,bx.length);
            fos.flush();
        }
        fos.close();
    }

    public void cmdGetFile() throws Exception
    {
        String downloadfile = "/app/sw/agent";
        //String downloadfile = "/app/sw/nfs/URBAN-X2-v1.0-H.tar.gz";
        /*
        CommandData command = new CommandData();
        MIBUtil mibUtil = MIBUtil.getInstance();
        command.setCmd(mibUtil.getOid("cmdGetFile"));
        //File Name
        command.append(DataUtil.getStringSMIValue(downloadfile));
        */
        CommandData command = DataUtil.getGetFileCommandData(
                downloadfile);

        GPRSTarget target = new GPRSTarget("187.1.5.235",8000);
        target.setTargetId("2000");
        Client client = ClientFactory.getClient(target);
        command = client.sendCommand(command);
        _log.info("command length["
                +command.getSMIValue().length+"]");
        makeFile(downloadfile,command);
    }

    public void cmdPutFile() throws Exception
    {
        String uploadfile = "/home/aimir/mcu/upload/test.txt";
        CommandData command = 
            DataUtil.getPutFileCommandData(uploadfile);
        GPRSTarget target = new GPRSTarget("187.1.5.235",8000);
        target.setTargetId("2000");
        Client client = ClientFactory.getClient(target);
        _log.info("command["+command+"]");
        command = client.sendCommand(command);
        _log.info("command["+command+"]");
    }

    public static void main(String[] args)
    {
        try 
        {
            TestFileCommandClient mc = null;
            if(args.length < 1)
            {
                _log.info("Usage : java "
                        +" TestFileCommandClient ["
                    +"filename]");
                System.exit(-1);
            }    
            mc = new TestFileCommandClient(args[0]);
            //mc.cmdInstallFile();
            mc.cmdGetFile();
            //mc.cmdPutFile();
        } catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
