package com.aimir.fep.command;

import java.net.URL;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Ignore;
import org.junit.Test;

import com.aimir.fep.command.ws.client.CommandWS;
import com.aimir.fep.command.ws.client.CommandWS_Service;
import com.aimir.fep.command.ws.client.Response;
import com.aimir.fep.command.ws.client.ResponseMap;
import com.aimir.fep.meter.data.MeterData;
import com.aimir.fep.protocol.fmp.frame.service.Entry;

public class CommandWSTest {
    final String MCUID="3420";
    
    private static Log log = LogFactory.getLog(CommandWSTest.class);
    
    private static final QName SERVICE_NAME = new QName("http://mbean.command.fep.aimir.com/", "CommandWS");
    
    @Test
    public void test_ondemand() throws Exception {
        String[] meterIds = new String[]{"17472736", "17829905"};
        String[] modemIds = new String[]{"000D6F0000DE1DD8", "000D6F00003140AA"};
        new OndemandThread(meterIds[0], modemIds[0]).run();
    }
    
    @Ignore
    public void test_ondemandthread() throws Exception {
        ThreadPoolExecutor executor = 
                new ThreadPoolExecutor(10, 50, 10, TimeUnit.SECONDS, new LinkedBlockingQueue());
        
        String[] meterIds = new String[]{"00008350017545", "00008350017630"};
        String[] modemIds = new String[]{"000D6F0000EAC82F", "000D6F0000EAC853"};
        
        for (int i = 0; i < meterIds.length; i++) {
            log.info(meterIds[i] + "=" + modemIds[i]);
            executor.execute(new OndemandThread(meterIds[i], modemIds[i]));
        }
        
        executor.shutdown();
        while (!executor.awaitTermination(10, TimeUnit.SECONDS)) ;
    }
    
    @Ignore
    public void test_mcugetenv() throws Exception {
        URL wsdlURL = CommandWS_Service.WSDL_LOCATION;
        CommandWS_Service ss = new CommandWS_Service(wsdlURL);
        CommandWS port = ss.getCommandWSPort(); 
        List<Entry> entries = port.cmdMcuGetEnvironment(MCUID);
        for (Entry e: entries.toArray(new Entry[0])) {
            log.info(e.toString());
        }
    }
    
    @Ignore
    public void test_mcuunitscan() throws Exception {
        URL wsdlURL = CommandWS_Service.WSDL_LOCATION;
        CommandWS_Service ss = new CommandWS_Service(wsdlURL);
        CommandWS port = ss.getCommandWSPort(); 
        ResponseMap rmap = port.cmdMcuDiagnosis(MCUID);
        List<ResponseMap.Response.Entry> entries = rmap.getResponse().getEntry();
        for (ResponseMap.Response.Entry e:entries.toArray(new ResponseMap.Response.Entry[0])) {
            log.info(e.getKey() + "=" + e.getValue());
        }
    }
    
    @Ignore
    public void test_sendsms() throws Exception {
        URL wsdlURL = CommandWS_Service.WSDL_LOCATION;
        CommandWS_Service ss = new CommandWS_Service(wsdlURL);
        CommandWS port = ss.getCommandWSPort(); 
        Object obj = port.cmdSendSMS("000D6F0000EAC82F", null);
    }
    
    class OndemandThread implements Runnable {
        URL wsdlURL = CommandWS_Service.WSDL_LOCATION;
        
        private String meterId;
        private String modemId;
        
        public OndemandThread(String meterId, String modemId) {
            this.meterId = meterId;
            this.modemId = modemId;
        }

        @Override
        public void run() {
            try {
                wsdlURL = new URL("http://187.1.10.58:8080/services/CommandWS?wsdl");
                CommandWS_Service ss = new CommandWS_Service(wsdlURL);
                CommandWS port = ss.getCommandWSPort();  
                
                MeterData data = port.cmdOnDemandMeter2(MCUID, meterId, modemId, "0", "20130917", "20130917");
                MeterData.Map.Entry entry = null;
                for(Object obj : data.getMap().getEntry().toArray()) {
                    entry = (MeterData.Map.Entry)obj;
                    log.info(entry.getKey()+"="+entry.getValue());
                }
            }
            catch (Exception e) {
                log.error(e,e);
            }
        }
        
    }
}
