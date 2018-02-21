package com.aimir.fep.trap.common;

import java.lang.reflect.Method;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants.EventStatus;
import com.aimir.constants.CommonConstants.MonitorType;
import com.aimir.dao.device.EventAlertDao;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.EventUtil;
import com.aimir.model.device.EventAlert;
import com.aimir.model.device.EventAlertLog;
import com.aimir.notification.FMPTrap;

/**
 * event task
 *
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-12-13 15:59:15 +0900 $,
 */
@Component
@Transactional(value = "transactionManager")
public class FMPEventTask
{
    private static Log log = LogFactory.getLog(FMPEventTask.class);
    private static String pkgName = null;
	@Autowired
    EventAlertDao eventAlertDao;

	@Autowired
	EventMaker eventMaker;
	
    static {
        String clsname = FMPEventTask.class.getName(); 
        int idx = clsname.lastIndexOf("."); 
        pkgName = clsname.substring(0,idx);
        idx = pkgName.lastIndexOf("."); 
        pkgName = pkgName.substring(0,idx+1);
    }
    private Object result = null;

    /**
     * constructor
     */
    public FMPEventTask()
    {
    }

    /**
     * find action class
     */
    @SuppressWarnings("unchecked")
	private Class getActionClass(String clsName) throws Exception
    { 
        try
        {          
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			if(cl == null) cl = getClass().getClassLoader();//fallback
			Class instance = cl.loadClass(clsName);
	    	return instance;

        }catch(ClassNotFoundException cnfe)
        {
            log.error("ClassNotFound : "+clsName);
            return null;
        }
    }

    /**
     * execute task
     */
    public void access(FMPTrap trap) throws Failure
    { 
        try
        {
            // Get Event Data
            EventAlertLog event = eventMaker.getEventAlertLog(trap);
            EventAlert eventAlert = event.getEventAlert();
            log.debug("Event : " + event.toString());
            log.debug("EventAlert : " + eventAlert);
            
            // execute Event
            String eventCode = trap.getCode(); 
            String clsName = "";
            Class<?> cls = null;
            if(eventCode != null)
            {
                clsName = pkgName + "actions." + (trap.getNameSpace() == null || trap.getNameSpace().equals("") ? "" : (trap.getNameSpace() + ".")) + getEventActionName(eventCode);
                // log.warn("can not found Action Class["+clsName+"]");
                cls = getActionClass(clsName);
                log.debug("Event task Instance Class name["+clsName+"]");
            }

            // OID에 해당하는 Action을 찾아서 execute 한다.
            // EventAlertLog 정보가 갱신된다.
            if(cls != null)
            {
                Object obj = DataUtil.getBean(cls); // cls.newInstance();
                Method method = cls.getDeclaredMethod("execute",
                        new Class[] { trap.getClass(), 
                        event.getClass() });
                method.invoke(obj,new Object[] { trap, event } );
            }
            
            
            /*
             * Trap에서 받아온 값을 기준으로 저장할지 안할지 결정해야할 경우 사용합니다.
             * NoMonitor을 사용할 경우 configuraion이 바뀌므로 status에 NoSaveEvent를 추가하여 사용하였습니다.
             * 참고: EV_GV_200_2_0_Action.java
            */
            if(event.getStatus() == EventStatus.NoSaveEvent){
            	log.info("Event is not saved");
            	return;
            }
            
            
            log.debug("EventAlert : " + eventAlert);
            // event.setEventAlert(eventAlert);
            event.setMessage(EventUtil.buildEventMessage(event));
            if (event.getMessage() == null || "".equals(event.getMessage())) {
                event.setMessage(event.getEventAlert().getName());
            }
            // Save Event 
            // int monitor = 1;
            // String searchdate = DateTimeUtil.getDateString(new Date());
            // try {
            	//TODO !!!!!!!!!!!!!!!!!!!!!!!!!!!대량정전 로직 구현 필요
            	/*
                monitor = Integer.parseInt(
                        NASProperty.getPropertyValueString(rule,
                            "monitor"));                

                if(event.getInstanceKey().startsWith("NURI#ZRU/") &&
                   (event.getEventClassName().equals("PowerAlarm"))){
                    
                    boolean isPlannedPO = false;
                    String meter_id = event.getInstanceKey().substring(9);

                    
                    
                    PowerOutageMgrDelegate poMgr = new PowerOutageMgrDelegate();
                    
                    Map<String, String> map = new LinkedHashMap<String, String>( 16, 0.74f, false );
                    map.put("m.meter_id:=:str", meter_id);
                    map.put("g.starttime:>:LongTime", searchdate);
                    map.put("g.endtime:<:LongTime", searchdate);
                    
                    isPlannedPO 
                        = poMgr.isPlannedPowerOutage(map,event.getLocationName());

                    if(isPlannedPO){
                        monitor = 0;//no save, no send event
                    }
                }
                */ //TODO !!!!!!!!!!!!!!!!!!!!!!!!!!!대량정전 로직 구현 필요
                
            // }catch(Exception ex) {log.warn("planed po check error :",ex);}
            // log.debug("Rule monitor["+monitor+"]");
            // if(monitor == 1 || monitor == 3) {
            //    EventAlertLog eventAlertLog = new EventAlertLog();
            //    eventAlertLog.setActivatorId(event.getSystemKey());
            //    eventAlertLog.setActivatorIp(event.getSystemIpAddr());
                //eventAlertLog.setActivatorTypeCode(activatorTypeCode);
                //eventAlertLog.setCloseTime(event.getTime());
            //    eventAlertLog.setDuration(event.getTimes()+"");
                //eventAlertLog.setLocation(event.getLocationCode());
            //    eventAlertLog.setMessage(event.getMessage());
                //eventAlertLog.setOpenTime(event.get);
                //eventAlertLog.setStatusCode(event.getStatus());
                //eventAlertLog.setTypeCode(typeCode);
            //    eventAlertLog.setWriteTime(searchdate);
            //    eventAlertLogDao.add(eventAlertLog);
            // }
            // if(monitor > 1)
            //    sendEvent(event);
            
            MonitorType monitor = event.getEventAlert().getMonitor();
            
            log.debug("### Event : " + event.toString());
            
            if (event.getId() == null || event.getId() == 0) {
                if(monitor == MonitorType.Save  || monitor == MonitorType.SaveAndMonitor) {
                    event = EventUtil.saveEventAlertLog(event);
                }
            }
            
            log.debug("Event : " + event.toString());
            
            if(MonitorType.SaveAndMonitor == monitor || MonitorType.AlertWindow == monitor)
                EventUtil.sendNotification(event);
        }
        catch(Exception ex)
        {
            log.error(ex ,ex);
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
    
	private String getEventActionName(String oid)
	{
		StringBuffer sb = new StringBuffer();
		sb.append("EV_");
		sb.append(oid.replaceAll("\\.","_"));
		sb.append("_Action");
		
		return sb.toString();
	}
}
