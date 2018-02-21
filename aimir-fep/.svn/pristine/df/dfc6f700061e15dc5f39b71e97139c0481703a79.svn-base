package com.aimir.fep.meter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import com.aimir.fep.BaseTestCase;
import com.aimir.fep.util.Hex;

public class RMetaTest extends BaseTestCase {
    private static Log log = LogFactory.getLog(RMetaTest.class);
    
    private static final String meta = "030099000000100408000D6F00005942E406010C0C120B0F0C0C120000000000040000032F00000004000002FB000000640000568A0000006400005A12000000000000000000000000000000000F020C0C120000000C0C120A2D000000000004000002FB00010064569B00000000006400005A12000000000001006456010C0C120B0F000000010064568A0000000000000001006456A80000000000";

    @Test
    public void test() {
        try {
            MeterDataSaverWS saver = this.applicationContext.getBean(MeterDataSaverWS.class);
            saver.saveRData("0", 1, Hex.encode(meta));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
