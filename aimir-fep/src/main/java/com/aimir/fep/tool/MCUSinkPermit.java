package com.aimir.fep.tool;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.aimir.dao.device.MCUDao;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.MCU;

public class MCUSinkPermit {
    private static Log log = LogFactory.getLog(MCUSinkPermit.class);

    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"/config/spring.xml"}); 
        DataUtil.setApplicationContext(ctx);
        
        String propName="codiPermit";
        String cmd = args[0];
        String mcuId = args[1];
        String propValue = null;
        
        if (args.length == 3)
            propValue = args[2];
        
        CommandGW cgw = DataUtil.getBean(CommandGW.class);
        if (mcuId.equalsIgnoreCase("all")) {
            MCUDao mcuDao = ctx.getBean(MCUDao.class);
            List<MCU> list = mcuDao.getAll();
            
            for (MCU m : list) {
                cmdStdSet(cgw, cmd, m.getSysID(), propName, propValue);
            }
        }
        else {
            cmdStdSet(cgw, cmd, mcuId, propName, propValue);
        }
        
        System.exit(0);
    }
    
    public static void cmdStdSet(CommandGW cgw, String cmd, String mcuId,
            String propName, String propValue) {
        try {
            if (cmd.equalsIgnoreCase("set")) {
                cgw.cmdStdSet(mcuId, propName, propValue);
            }
            else {
                Hashtable table = cgw.cmdStdGet(mcuId, propName);
                
                Object key = null;
                Object value = null;
                for (Iterator i = table.keySet().iterator(); i.hasNext(); ) {
                    key = i.next();
                    value = table.get(key);
                    log.info("Key[" + key + "] Value[" + value + "]");
                    if (Integer.parseInt((String)value) == 0) {
                        cgw.cmdStdSet(mcuId, propName, 255+"");
                    }
                }
            }
        }
        catch (Exception e) {
            log.error(e, e);
        }
    }
}
