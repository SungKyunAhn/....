package com.aimir.fep.protocol.fmp.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.aimir.fep.BaseTestCase;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.protocol.fmp.exception.FMPMcuException;
import com.aimir.fep.util.DataUtil;

public class SX2 extends BaseTestCase{
	static {
        DataUtil.setApplicationContext(new ClassPathXmlApplicationContext(new String[]{"/config/spring.xml"}));
    }
    private static Log log = LogFactory.getLog(SX2.class);
    
        
    @Test
    public void setEnergyLevelTest(){
    		CommandGW cgw = new CommandGW();
    		try {
    			cgw.cmdSetEnergyLevel("3993", "0000001", "1");
    			
    		} catch (FMPMcuException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (Exception e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		
    }
}
