package com.aimir.fep.trap.common;

import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aimir.dao.device.ZMUDao;
import com.aimir.model.device.ZMU;
import com.aimir.notification.Alarm;
import com.aimir.notification.FMPTrap;
import com.aimir.util.TimeUtil;

/**
 * alarm task
 *
 * @author Y.S Kim
 * @version $Rev: 1 $, $Date: 2005-12-13 15:59:15 +0900 $,
 */
@Component
public class FMPAlarmTask
{
    private static Log log = LogFactory.getLog(FMPAlarmTask.class);
    
    @Autowired
    ZMUDao zmuDao;
    
    private static String pkgName = null;
    static {
        String clsname = FMPAlarmTask.class.getName(); 
        int idx = clsname.lastIndexOf("."); 
        pkgName = clsname.substring(0,idx);
        idx = pkgName.lastIndexOf("."); 
        pkgName = pkgName.substring(0,idx+1);
    }
    private Object result = null;
    
    /**
     * constructor
     */
    public FMPAlarmTask()
    {
    }

    /**
     * find action class
     */
    @SuppressWarnings("unchecked")
	private Class getActionClass(String clsName) 
        throws Exception
    { 
        try
        {
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			if(cl == null) cl = getClass().getClassLoader();//fallback
			Class instance = cl.loadClass(clsName);
	    	return instance;
        }catch(ClassNotFoundException cnfe)
        {
            return null;
        }
    }

    /**
     * execute task
     */
    @SuppressWarnings("unchecked")
	public void access(FMPTrap trap) throws Failure
    { 
        try
        {
            // Get Alarm Data
            //IF9Gateway.getInstance().processing(trap);

            Alarm alarm = AlarmMaker.getAlarm(trap);
            log.debug("Alarm : " + alarm.toString());

            // execute Alarm
            String sourceType = trap.getSourceType(); 
            String clsName = null;
            Class cls = null;
            log.warn("can not found Action Class["+clsName+"]");
            clsName = pkgName+"actions." 
                +getAlarmActionName(sourceType);
            cls = getActionClass(clsName);

            if(cls != null)
            {
                Object obj = cls.newInstance();
                Method method = cls.getDeclaredMethod("execute",
                        new Class[] { trap.getClass(), 
                        alarm.getClass() });
                method.invoke(obj,new Object[] { trap, alarm } );
            }
            else
                log.error("can not found Action Class["+clsName+"]");

            log.debug("Alarm : " + alarm.toString());

            /*
            // set last alarm time in AlarmUnit
            String auInstName = alarm.getSystemKey();
            if(auInstName != null && auInstName.length() > 0)
            {
                IUtil.setPropertyValue(auInstName,"lastAlarmTime",
                        trap.getTimeStamp());
                IUtil.setPropertyValue(auInstName,"networkStatus",
                        "1");
            }
            */
            
            /**
             * added by D.J Park in 2006.02.14
             * set ZMU CommState And lastLinkTime
             */
            ZMU zmu = (ZMU) alarm.getAlarmMO();
            if(zmu != null)
            {
            	zmu.setCommState(1);
                String currTime = TimeUtil.getCurrentTime();
            	zmu.setLastLinkTime(currTime);
            	if(zmu.getInstallDate() == null && "".equals(zmu.getInstallDate())){
            		zmu.setInstallDate(currTime);
            	}

                try { 
                	if(zmuDao.get(zmu.getId()) != null){
                        zmuDao.update(zmu);
                	}else{
                        zmuDao.add(zmu);
                	}

                }catch(Exception ex)
                {
                    log.error(ex.getMessage(),ex);
                }
            }
        }
        catch(Exception ex)
        {
            log.error("FMPAlarmTask failed : " ,ex);
            throw new Failure();
        }
    }

    /**
     * get result
     * @return data - result object
     */
    public Object getResult()
    {
        return this.result;
    }
    
	private String getAlarmActionName(String sourceType)
	{
		StringBuffer sb = new StringBuffer();
		sb.append("A_Alarm_");
		sb.append(sourceType);
		sb.append("_Action");
		
		return sb.toString();
	}
}
