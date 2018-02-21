package com.aimir.fep.protocol.fmp.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.fmp.client.Client;
import com.aimir.fep.protocol.fmp.client.ClientFactory;
import com.aimir.fep.protocol.fmp.common.LANTarget;
import com.aimir.fep.protocol.fmp.datatype.OID;
import com.aimir.fep.protocol.fmp.frame.service.CommandData;

/**
 * Send Command Test Class
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class TestCommandClient
{
    private static Log _log = LogFactory.getLog(TestCommandClient.class);
    
    private int tcount = 1;

    /**
     * constructor
     */
    public TestCommandClient()
    {
    }

    /**
     * constructor
     *
     * @param tcount <code>int</code> number of thread 
     */
    public TestCommandClient(int tcount)
    {
        this.tcount = tcount;
    }

    /**
     * start to send command 
     *
     * @throws Exception
     */
    public void start() throws Exception
    {
        //TcpTarget target = new TcpTarget("222.117.51.231",8000);
        LANTarget target = new LANTarget("187.1.5.236",8000);
        target.setTargetId("3000");
        Client client = ClientFactory.getClient(target);
        for(int i = 0 ; i < tcount ; i++)
        {
            ClientThread ct = new ClientThread(client);
            ct.start();
        }
    }

    /**
     * Client Thread
     */
    public class ClientThread extends Thread
    {
        private Client client;
        public ClientThread(Client client)
        {
            this.client = client;
        }
        public void run()
        {
            try{
            CommandData command = new CommandData();
            command.setCmd(new OID("100.3.0"));

            /*
            SMIValue smiValue = null;
            smiValue = DataUtil.getSMIValue("sysName","MCU1"); 
            command.append(smiValue);
            smiValue = DataUtil.getSMIValue("sysDescr","MCU1 Descr"); 
            command.append(smiValue);
            flashEntry entry = new flashEntry();
            entry.setFlashTotalSize(new UINT(1024));
            entry.setFlashFreeSize(new UINT(1000));
            entry.setFlashUseSize(new UINT(24));
            smiValue = DataUtil.getSMIValue(entry.getMIBName(),entry);
            command.append(smiValue);
            */
            client.sendCommand(command);
            }catch(Exception ex) { ex.printStackTrace(); }
            _log.info("end ClientThread");
        }
    } 
    public static void main(String[] args)
    {
        TestCommandClient mc = null;
        if(args.length != 1)
        {
            mc = new TestCommandClient();
        } else {
            int tcount = 1;
            try {   tcount = Integer.parseInt(args[0]); 
            }catch(Exception ex) {}
            mc = new TestCommandClient(tcount);
        }

        try { mc.start(); } catch(Exception ex)
        { ex.printStackTrace(); }
        
        _log.info("end Main");
    }
}
