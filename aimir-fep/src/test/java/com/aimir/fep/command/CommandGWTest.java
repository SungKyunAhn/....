package com.aimir.fep.command;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Ignore;
import org.junit.Test;

import com.aimir.fep.BaseTestCase;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.protocol.fmp.frame.service.entry.sensorInfoNewEntry;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

public class CommandGWTest extends BaseTestCase {
    final String MCUID="3420";
    
    private static Log log = LogFactory.getLog(CommandGWTest.class);
    
    @Ignore
    public void test_ondemand() throws Exception {
        DataUtil.setApplicationContext(this.applicationContext);
        String[] meterIds = new String[]{"17472736", "17829905"};
        String[] modemIds = new String[]{"000D6F0000DE1DD8", "000D6F00003140AA"};
        CommandGW gw = DataUtil.getBean(CommandGW.class);
        gw.cmdOnDemandMeter(MCUID, meterIds[1], modemIds[1], "0", "20130917", "20130917");
    }
    
    @Test
    public void test_cmdgetmodeminfo() throws Exception {
        DataUtil.setApplicationContext(this.applicationContext);
        CommandGW gw = DataUtil.getBean(CommandGW.class);
        sensorInfoNewEntry entry = gw.cmdGetModemInfoNew("000D6F00003140AA");
        // log.info(entry.toString());
        byte[] bFW = entry.getSensorFwVersion().encode();
        DataUtil.convertEndian(bFW);
        String fw = String.format("%d.%d", (int)bFW[0], (int)bFW[1]);
        byte[] bHW = entry.getSensorHwVersion().encode();
        DataUtil.convertEndian(bHW);
        String hw = String.format("%d.%d", (int)bHW[0], (int)bHW[1]);
        int build = entry.getSensorFwBuild().getValue();
        String otaLastUpdate = entry.getSensorLastOTATime().getValue();
        String meterModel = new String(entry.getSensorModel().getValue()).trim();
        log.info("FW[" + fw + "] HW[" + hw + "] build[" + build + "] meterModel[" + meterModel + "] otaUpdate[" + otaLastUpdate + "]");
    }
}
