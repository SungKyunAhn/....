package com.aimir.fep.protocol.fmp.client;


import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.fmp.common.LANTarget;
import com.aimir.fep.protocol.fmp.datatype.BYTE;
import com.aimir.fep.protocol.fmp.datatype.HEX;
import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.protocol.fmp.datatype.TIMESTAMP;
import com.aimir.fep.protocol.fmp.frame.service.AlarmData;
import com.aimir.util.TimeUtil;

/**
 * Send Alarm Test Class
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class TestAlarmClient
{
    private static Log _log = LogFactory.getLog(TestAlarmClient.class);
    
    private int tcount = 1;
    private String ip = null;
    private String port = null;

    /**
     * constructor
     */
    public TestAlarmClient(String ip, String port)
    {
        this.ip= ip;
        this.port = port;
    }

    /**
     * constructor
     *
     * @param tcount <code>int</code> number of thread 
     */
    public TestAlarmClient(int tcount, String ip, String port)
    {
        this.tcount = tcount;
        this.ip = ip;
        this.port = port;
    }

    /**
     * start to send alarm 
     *
     * @throws Exception
     */
    public void start() throws Exception
    {
        // TestAlarmListener listener = (TestAlarmListener)ctx.getBean("alarmListener");
        // listener.init();
        
        LANTarget target = new LANTarget(ip, Integer.parseInt(port));
        target.setTargetId("222222222");
        for(int i = 0 ; i < tcount ; i++)
        {
            ClientThread ct = new ClientThread(target);
            ct.run();
        }
    }

    /**
     * Client Thread
     */
    public class ClientThread
    {
        private LANTarget target;
        public ClientThread(LANTarget target)
        {
            this.target = target;
        }

        public void run()
        {
            try{
                Client client = ClientFactory.getClient(target);
                AlarmData alarm = new AlarmData();
                alarm.setVendor(new BYTE(1));
                alarm.setSrcId(new HEX("76F0AC87DBB7E45C"));
                long curTime = System.currentTimeMillis();
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(curTime);
                alarm.setMcuTimeStamp(new TIMESTAMP(TimeUtil.getFormatTime(cal)));
                cal.setTimeInMillis(curTime - 1000);
                alarm.setSensorTimeStamp(new TIMESTAMP(TimeUtil.getFormatTime(cal)));
                alarm.setAlarmMessage(new OCTET("alarm invoked"
                            +" : Smoke Detector Alarm"
                            ));
                client.sendAlarm(alarm);
                client.close();
            }catch(Exception ex) { ex.printStackTrace(); }
            _log.info("end ClientThread");
        }
    } 

    public static void main(String[] args)
    {
        TestAlarmClient mc = null;
        if(args.length != 3)
        {
            System.out.println("Usage : need tcount, ip and port args");
            System.exit(0);
        } else {
            int tcount = 1;
            try {   tcount = Integer.parseInt(args[0]); 
            }catch(Exception ex) {}
            mc = new TestAlarmClient(tcount, args[1], args[2]);
        }

        try { mc.start(); } catch(Exception ex)
        { ex.printStackTrace(); }
        
        _log.info("end Main");
    }
}
