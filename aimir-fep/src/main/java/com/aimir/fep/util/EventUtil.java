package com.aimir.fep.util;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.regexp.RE;
import org.springframework.jms.core.BrowserCallback;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants.EventStatus;
import com.aimir.constants.CommonConstants.MonitorType;
import com.aimir.constants.CommonConstants.SeverityType;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.EventAlertDao;
import com.aimir.dao.device.EventAlertLogDao;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.model.device.EventAlert;
import com.aimir.model.device.EventAlertAttr;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.MCU;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.system.Supplier;
import com.aimir.notification.Alert;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.StringUtil;

/**
 * Event Utility Class 
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-12-07 15:59:15 +0900 $,
 */
public class EventUtil
{
    private static Log log = LogFactory.getLog(EventUtil.class);
    private static JpaTransactionManager txmanager = 
        (JpaTransactionManager)DataUtil.getBean("transactionManager");
    private static JmsTemplate activeJmsTemplate = (JmsTemplate)DataUtil.getBean("activeJmsTemplate");
    private static Topic eventTopic = (Topic)DataUtil.getBean("eventTopic");
    
    public static JpaTransactionManager getTransactionManager() {
        return txmanager;
    }
    
    private static String getEventValue(EventAlertLog event, String attr)
    {
        log.debug("getEventValue : " + attr);  

        int idx = attr.indexOf(".");
        if(idx >= 0)
        {
            attr = attr.substring(idx+1);
        }
        try
        {
            Object val = null;
            if(attr.equals("eventClassName") 
                    || attr.equals("systemKey") 
                    || attr.equals("systemName") 
                    || attr.equals("time"))
            {
                val = PropertyUtils.getProperty(event, attr);
            }
            
            if(attr.startsWith("eventAttrs"))
            {
                String eventAttr = 
                    attr.substring("eventAttrs".length()+1);
                val = event.getEventAttrValue(eventAttr);
            }
            else {
                val = PropertyUtils.getProperty(event, attr);
            }
            /*
            else
            {
                log.error("getEventValue unknown attr["+attr+"]");
            }
            */

            if(val == null)
                val = "";
            return val.toString();
        }
        catch(Exception ex)
        {
            log.error("getEventValue failed : ",ex);
        }
        return "";
    }

    private static String getAlertValue(Alert alert, String attr)
    {
        log.debug("getAlertValue : " + attr);

        int idx = attr.lastIndexOf(".");
        if(idx >= 0)
        {
            attr = attr.substring(idx+1);
        }
        try
        {
            Object val = PropertyUtils.getProperty(alert, attr);
            if(val == null)
                val = "";
            return val.toString();
        }
        catch(Exception ex)
        {
            log.error("getAlertValue failed : ",ex);
        }
        return "";
    }

    public static String buildEventMessage(EventAlertLog event) throws Exception
    {
        return buildEventMessage(event, event.getEventAlert().getMsgPattern());
    }

    public static String buildEventMessage(EventAlertLog event, 
            String format) throws Exception
    {
        log.debug("buildEventMessage("+event.getEventAlert().getName()+","+format+")");

        String msg = null;
        if(format == null || format.length() < 1)
            return format;

        msg = format;

        String pattern = "([$][(][a-zA-Z0-9.]+[)])";
        RE re = new RE(pattern);

        int pos = 0;

        String res = "";

        while(re.match(msg))
        {
            String var = re.getParen(1);
            int idx = msg.indexOf(var);
            String before = msg.substring(0,idx);
            res = res + before;
            int var_len = var.length();
            res = res + getEventValue(event,
                    var.substring(2,var_len -1));
            pos = idx+var_len;
            msg = msg.substring(pos);
            log.debug("buildMessage : msg : " + msg);
        }

        if(pos == 0)
            res = msg;
        else
            res = res + msg;

        log.debug("buildEventMessage : message : "+res); 

        return res;
    }

    public static String buildAlertMessage(Alert alert, 
            String format) throws Exception
    {
        log.debug("buildEventMessage("+alert.getFaultClassName()+","
                +format+")");

        String msg = null;
        if(format == null || format.length() < 1)
            return format;

        msg = format;

        String pattern = "([$][(][a-zA-Z0-9.]+[)])";
        RE re = new RE(pattern);

        int pos = 0;

        String res = "";

        while(re.match(msg))
        {
            String var = re.getParen(1);
            int idx = msg.indexOf(var);
            String before = msg.substring(0,idx);
            res = res + before;
            int var_len = var.length();
            res = res + getAlertValue(alert,
                    var.substring(2,var_len -1));
            pos = idx+var_len;
            msg = msg.substring(pos);
            log.debug("buildMessage : msg : " + msg);
        }

        if(pos == 0)
            res = msg;
        else
            res = res + msg;

        log.debug("buildEventMessage : message : "+res); 

        return res;
    }
    
    @SuppressWarnings("unchecked")
    public static void getNotification() throws Exception
    {
        EventBrowser eb = new EventBrowser();
        activeJmsTemplate.browse("AiMiR.Event", eb);
    }
    
    public static void sendNotification(final EventAlertLog event)
        throws Exception
    {
        
        long starttime = System.currentTimeMillis();
        boolean sended = false;
        String interval = FMPProperty.getProperty("event.alert.interval");

        if (StringUtil.nullToBlank(interval).isEmpty()) {
            interval = "3000";
        }
        activeJmsTemplate.setExplicitQosEnabled(true);
        // time to live : interval + 5000
        activeJmsTemplate.setTimeToLive(Long.parseLong(interval) + 5000);
        try {
            activeJmsTemplate.send(eventTopic, new MessageCreator() {
                public Message createMessage(final Session session) throws JMSException {
                    TextMessage message = session.createTextMessage();
                    StringBuffer buf = new StringBuffer();
                    buf.append("{");
                    if (event == null) log.info("Event is null");
                    
                    if(event.getId()!=null){
                        buf.append("\"eventLogId\":\"" + event.getId() + "\",");
                    }
                    buf.append("\"activatorId\":\"" + event.getActivatorId()+"\",");
                    buf.append("\"activatorType\":\"" + event.getActivatorType().name()+"\",");
                    buf.append("\"eventAlertName\":\"" + event.getEventAlert().getName()+"\",");
                    buf.append("\"status\":\"" + event.getStatus().name()+"\",");
                    buf.append("\"severity\":\"" + event.getSeverity().name()+"\",");
                    buf.append("\"openTime\":\"" + event.getOpenTime()+"\",");
                    buf.append("\"activatorIp\":\"" + event.getActivatorIp()+"\",");
                    buf.append("\"eventOid\":\"" + event.getEventAlert().getOid()+"\",");
                    buf.append("\"eventMessage\":\"" + event.getMessage()+"\",");
                    buf.append("\"closeTime\":\"" + event.getCloseTime()+"\",");
                    buf.append("\"duration\":\"" + event.getDuration()+"\",");
                    buf.append("\"writeTime\":\"" + event.getWriteTime()+"\"");
                    
                    try {
                        if (event.getSupplier() != null) {
                            log.debug("supplierId:"+event.getSupplier().getId());
                            buf.append(",\"supplierId\":\""+event.getSupplier().getId()+"\"");
                        }                       
                    } catch (Exception e) {
                        log.error("Event getSuppliser Error - " + e);
                    }

                    try {
                        if (event.getLocation() != null) {
                            log.debug("Location[" + event.getLocation().getName() + "]");
                            buf.append(",\"location\":\""+event.getLocation().getName()+"\"");
                        }                       
                    } catch (Exception e) {
                        log.error("Event getLocation Error - " + e);
                    }
                    
                    for (EventAlertAttr eaa : event.getEventAlertAttrs().toArray(new EventAlertAttr[0]))
                        buf.append(",\"" + eaa.getAttrName() + "\":\"" + eaa.getValue() + "\"");
                    buf.append("}");
                    // message.setObject("eventMO", event.getEventMO());
                    
                    // priority default = 4
                    // 2016.06.21
                    int priority = event.getEventAlert().getSeverity().getPriority();
                    
                    log.debug("sendNotification["+message.toString()+"] Priority[" + priority + "]");
                                        
                    message.setJMSPriority(priority);
                    message.setText(buf.toString());
                    return message;
                }
            });
            sended = true;
        }
        catch (Exception e) {
            log.error(e, e);
        }
        log.debug("send alarm duration[" + (System.currentTimeMillis() - starttime) + "]");
        log.debug("send Event["+event.toString()+"] ");
    }

    public static EventAlertLog makeEventAlertLog(String eventAlertName, Object inst, EventAlertAttr[] attrs) throws Exception {
        TransactionStatus txStatus = null;
        
        try {
            txStatus = txmanager.getTransaction(null);
        
            EventAlertLog event = new EventAlertLog();
    
            EventAlert eventRule = findRule(eventAlertName);
            event.setEventAlert(eventRule);
            
            if(eventRule == null)
                throw new Exception("Can not find EventAlert "
                        + "EventAlertName["+eventAlertName+"]");
            
            if(inst instanceof MCU){
                MCU mcu = (MCU) inst;
                if(mcu != null){
                    event.setActivatorId(mcu.getSysID());
                    event.setActivatorType(TargetClass.DCU);
                    event.setActivatorIp(mcu.getIpAddr());
                    event.setLocation(mcu.getLocation());
                    event.setSupplier(mcu.getSupplier());
                }
            }
            else if (inst instanceof Modem) {
                Modem modem = (Modem)inst;
                if(modem != null){
                    event.setActivatorId(modem.getDeviceSerial());
                    event.setActivatorType(TargetClass.Modem);
                    event.setActivatorIp(modem.getIpAddr());
                    event.setLocation(modem.getMcu().getLocation());
                    event.setSupplier(modem.getSupplier());
                }
            }
            // TODO 이벤트 상태 변경해야 함.
            event.setStatus(EventStatus.Open);
            event.setOpenTime(DateTimeUtil.getDateString(new Date()));
            event.setOccurCnt(new Integer(1));
    
            for(int i = 0 ; i < attrs.length ; i++)
                event.append(attrs[i]);
            String msgformat = 
                eventRule.getMsgPattern();
            event.setMessage(buildEventMessage(event,msgformat));
            
            txmanager.commit(txStatus);
            
            log.debug("EventUtil::makeEvent["+event+"]");
            return event;
        }
        catch (Exception e) {
            txmanager.rollback(txStatus);
            throw e;
        }
    }

    public static void sendEvent( String eventAlertName, 
            Object inst, EventAlertAttr[] attrs) 
    throws Exception
    {
        sendEvent(eventAlertName, inst, attrs, DateTimeUtil.getDateString(new Date()));
    }
    
	public static void sendEvent(String eventAlertName, Object inst, EventAlertAttr[] attrs, String timestamp) throws Exception {
		EventAlert eventRule = findRule(eventAlertName);
		if (eventRule == null) {
			throw new Exception("Can not find EventRule " + "EventAlertName[" + eventAlertName + "]");
		}

		EventAlertLog event = makeEventAlertLog(eventAlertName, inst, attrs);
		event.setOpenTime(timestamp);
		MonitorType monitor = eventRule.getMonitor();

		if (monitor == MonitorType.Save || monitor == MonitorType.SaveAndMonitor) {
			saveEventAlertLog(event);
		}

		if (monitor == MonitorType.SaveAndMonitor || monitor == MonitorType.AlertWindow) {
			sendNotification(event);
		}
	}
	
	public static void sendEvent(String eventAlertName, String activatorType, String activatorId, Supplier supplier) {
	    try {
            EventAlertLog event = new EventAlertLog();
            EventAlert eventRule = findRule(eventAlertName);
            MonitorType monitor = eventRule.getMonitor();
            
            event.setSupplier(supplier);
            event.setEventAlert(eventRule);
            event.setActivatorId(activatorId);
            event.setActivatorType(activatorType);
            event.setSeverity(SeverityType.Information.name());
            event.setStatus(EventStatus.Open.name());
            event.setWriteTime(DateTimeUtil.getDateString(new Date()));
            event.setOpenTime(DateTimeUtil.getDateString(new Date()));
            event.setMessage(eventAlertName);
            event.setOccurCnt(new Integer(1));
    
            if (monitor == MonitorType.Save || monitor == MonitorType.SaveAndMonitor) {
                event = saveEventAlertLog(event);
            }
    
            if (monitor == MonitorType.SaveAndMonitor || monitor == MonitorType.AlertWindow) {
                sendNotification(event);
            }
	    }
	    catch (Exception e) {
	        log.error(e, e);
	    }
	}
	
	public static void sendEvent(String eventAlertName, String activatorType, String activatorId, int supplierId) {
	    SupplierDao supplierDao = DataUtil.getBean(SupplierDao.class);
		Supplier supplier = supplierDao.get(supplierId);
		sendEvent(eventAlertName, activatorType, activatorId, supplier);
	}
	
    public static EventAlertAttr makeEventAlertAttr(String attrName,
            String attrType,String value)
    {
        EventAlertAttr attr = new EventAlertAttr();
        attr.setAttrName(attrName);
        attr.setAttrType(attrType);
        if(value == null)
            attr.setValue("");
        else
            attr.setValue(value);

        return attr;
    }

    private static EventAlert findRule(String eventAlertName) throws Exception 
    {
        log.debug("EventAlertName[" + eventAlertName + "]");
        
        return DataUtil.getBean(EventAlertDao.class).findByCondition("name", eventAlertName);
        
    }
    
    /**
     * eventAlertName : 이벤트 알람 명
     * activatorType: 이벤트 소스(MCU, Meter, Modem...)
     * activatorId : 이벤트 소스 아이디 (MCU 아이디, 계량기 아이디...)
     * eventClassId : 이벤트 종류
     * attrs : 속성 new String[][] {{"meterID", "2039293293"},{"mcuID", "20392"}}
     */
    public static void sendEvent(String eventAlertName, TargetClass activatorType,
                                 String activatorId, String[][] attrs)
    throws Exception
    {
        sendEvent(eventAlertName, activatorType, activatorId, DateTimeUtil.getDateString(new Date()), attrs, new EventAlertLog());
    }
    
    
    /**
     * eventAlertName : 이벤트 알람 명
     * activatorType: 이벤트 소스(MCU, Meter, Modem...)
     * activatorId : 이벤트 소스 아이디 (MCU 아이디, 계량기 아이디...)
     * eventClassId : 이벤트 종류
     * attrs : 속성 new String[][] {{"meterID", "2039293293"},{"mcuID", "20392"}}
     */
    public static void sendEvent(String eventAlertName, TargetClass activatorType,
                                 String activatorId, String[][] attrs, EventAlertLog event)
    throws Exception
    {
        sendEvent(eventAlertName, activatorType, activatorId, DateTimeUtil.getDateString(new Date()), attrs, event);
    }
    
    
    /**
     * eventAlertName : 이벤트 알람 명
     *activatorType: 이벤트 소스(MCU, Meter, Modem...)
     * activatorId : 이벤트 소스 아이디 (MCU 아이디, 계량기 아이디...)
     * timestamp : 이벤트 발생 시간
     * eventClassId : 이벤트 종류
     * attrs : 속성 new String[][] {{"meterID", "2039293293"},{"mcuID", "20392"}}
     */
    public static void sendEvent(String eventAlertName, TargetClass activatorType,
                                 String activatorId, String timestamp,
                                 String[][] attrs, EventAlertLog event) throws Exception {
        event.setActivatorId(activatorId);
        event.setActivatorType(activatorType);
        
        if(activatorType == TargetClass.DCU){
            MCU mcu = DataUtil.getBean(MCUDao.class).get(activatorId);
            if(mcu != null){
                event.setActivatorIp(mcu.getIpAddr());
                event.setLocation(mcu.getLocation());
                event.setSupplier(mcu.getSupplier());
            }
        } else if (activatorType == TargetClass.Modem || activatorType == TargetClass.Converter
                || activatorType == TargetClass.LTE || activatorType == TargetClass.SubGiga
                || activatorType == TargetClass.IEIU || activatorType == TargetClass.MMIU) {
            Modem modem = DataUtil.getBean(ModemDao.class).get(activatorId);
            if(modem != null){
                event.setActivatorIp(modem.getIpAddr());
                event.setSupplier(modem.getSupplier());
                if (modem.getLocation() != null)
                    event.setLocation(modem.getLocation());
                else if (modem.getMcu() != null && modem.getMcu().getLocation() != null)
                    event.setLocation(modem.getMcu().getLocation());
            }
            else log.warn("Modem[" + activatorId + "] is NULL");
        } else if (activatorType.name().endsWith("Meter")) {
            Meter meter = DataUtil.getBean(MeterDao.class).get(activatorId);
            if(meter != null){
                event.setActivatorIp(activatorId);
                event.setLocation(meter.getLocation());
                event.setSupplier(meter.getSupplier());
            }
        }

        EventAlert rule = findRule(eventAlertName);
        
        if (rule ==null) {
            log.warn("EventAlert[" + eventAlertName + "] not found");
            return;
        }
        event.setEventAlert(rule);
        event.setSeverity(rule.getSeverity());
        if (event.getStatus() == null)
            event.setStatus(EventStatus.Open);
        event.setOccurCnt(1);
        // dst 적용
        event.setOpenTime(DateTimeUtil.getDST(null,timestamp));
        if (event.getStatus() == EventStatus.Cleared || event.getStatus() == EventStatus.ClearedManually)
            event.setCloseTime(event.getOpenTime());
        event.setWriteTime(DateTimeUtil.getDST(null, DateTimeUtil.getCurrentDateTimeByFormat(null)));

        log.debug("Event TimeStamp(Open Time) = " + timestamp);
        log.debug("Event WriteTime=[" + event.getWriteTime() + "], Open Time=[" + event.getOpenTime() + "], Close Time=[" + event.getCloseTime() + "]");
        
        for (int i = 0; i < attrs.length; i++) {
            event.append(makeEventAlertAttr(attrs[i][0], "java.lang.String", attrs[i][1]));
        }
        
        String msg = buildEventMessage(event, rule.getMsgPattern());
        event.setMessage(msg);
        log.debug("buildEventMessage with MOINSTANCE: "+msg);
        
 //       log.debug("event : " + event.toString());
        
        MonitorType monitor = rule.getMonitor();
        
        if(monitor == MonitorType.Save  || monitor == MonitorType.SaveAndMonitor) {
            event = saveEventAlertLog(event);
        }
        if(monitor == MonitorType.SaveAndMonitor || monitor == MonitorType.AlertWindow)
            sendNotification(event);
    }
    
    public static void sendEvent(long logId, EventStatus eventStatus) throws Exception {
        EventAlertLogDao ealdao = DataUtil.getBean(EventAlertLogDao.class);
        EventAlertLog event = ealdao.get(logId);
        if (event == null) {
            log.warn("EventAlert[" + logId + "] doesn't exist");
            return;
        }
        
        event.setStatus(eventStatus);
        EventAlert rule = event.getEventAlert();
        
        if (rule ==null) {
            log.warn("EventAlert[" + rule.getName() + "] not found");
            return;
        }
        
        event.setOccurCnt(event.getOccurCnt()+1);
        
        MonitorType monitor = rule.getMonitor();
        
        if(monitor == MonitorType.Save  || monitor == MonitorType.SaveAndMonitor) {
            event = saveEventAlertLog(event);
        }
        if(monitor == MonitorType.SaveAndMonitor || monitor == MonitorType.AlertWindow)
            sendNotification(event);
    }
    
    public static EventAlertLog saveEventAlertLog(EventAlertLog eventAlertLog) throws ParseException {
    	try {
    		EventAlertLogDao ealdao = DataUtil.getBean(EventAlertLogDao.class);
            
    		if (eventAlertLog.getSupplier() == null) {
    			// Set Default Supplier
    			String supplierName = new String(FMPProperty.getProperty("default.supplier.name").getBytes("8859_1"), "UTF-8");
    			log.debug("Supplier Name[" + supplierName + "]");
    			SupplierDao supplierDao = DataUtil.getBean(SupplierDao.class);
    			Supplier supplier = supplierName != null ? supplierDao.getSupplierByName(supplierName) : null;
    			eventAlertLog.setSupplier(supplier);
    		}
            // 이벤트 상태가 종료이면 이전 이벤트 알람을 찾아서 변경하고 없으면 새로 생성한다.
            if (eventAlertLog.getStatus() == EventStatus.Cleared) {
                List<EventAlertLog> list = ealdao.getOpenEventAlertLog(eventAlertLog.getActivatorType().name(),
                        eventAlertLog.getActivatorId(), eventAlertLog.getEventAlert().getId());
                
    			if (list.size() != 0) {
                    EventAlertLog eal = null;
                    EventAlertLog recent = null;
                    long min = 0;
                    long sec = 0;
    				for (int i = 0; i < list.size(); i++) {
    					eal = list.get(i);
                        if (eventAlertLog.getOpenTime() != null && !"".equals(eventAlertLog.getOpenTime())
                                && eal.getOpenTime().compareTo(eventAlertLog.getOpenTime()) < 0)
                            eal.setCloseTime(eventAlertLog.getOpenTime());
                        else eal.setCloseTime(DateTimeUtil.getDateString(new Date()));
                        sec = (DateTimeUtil.getDateFromYYYYMMDDHHMMSS(eal.getCloseTime()).getTime() - 
                                DateTimeUtil.getDateFromYYYYMMDDHHMMSS(eal.getOpenTime()).getTime())/1000;
                        min = sec / 60;
                        sec = sec % 60;
                        eal.setDuration(String.format("%dm %02ds", min, sec));
                        eal.setStatus(eventAlertLog.getStatus());
                        if (eventAlertLog.getMessage() != null && !"".equals(eventAlertLog.getMessage()))
                            eal.setMessage(eventAlertLog.getMessage());
                        else eal.setMessage(eventAlertLog.getEventAlert().getName());
                        ealdao.update_requires_new(eal);
                        
                        if (recent == null || recent.getOpenTime().compareTo(eal.getOpenTime()) < 0) recent = eal;
                    }
                    return recent;
    			} else {
                    // open 이벤트가 없으므로 close로만 처리하고 open시간은 지운다.
                    eventAlertLog.setCloseTime(eventAlertLog.getOpenTime());
                    eventAlertLog.setDuration("0m 0s");
                    // eventAlertLog.setOpenTime("");
                }
    		} else if (eventAlertLog.getStatus() == EventStatus.Open || eventAlertLog.getStatus() == EventStatus.OpenManually) {
                eventAlertLog.setCloseTime("");
                eventAlertLog.setDuration("");
    		} else if (eventAlertLog.getStatus() == EventStatus.ClearedManually) {
                eventAlertLog.setCloseTime(DateTimeUtil.getDateString(new Date()));
                long min = 0;
                long sec = 0;
                sec = (DateTimeUtil.getDateFromYYYYMMDDHHMMSS(eventAlertLog.getCloseTime()).getTime() - 
                        DateTimeUtil.getDateFromYYYYMMDDHHMMSS(eventAlertLog.getOpenTime()).getTime())/1000;
                min = sec / 60;
                sec = sec % 60;
                eventAlertLog.setDuration(String.format("%dm %02ds", min, sec));
            }
            
            eventAlertLog.setWriteTime(DateTimeUtil.getDateString(new Date()));
            
            if (eventAlertLog.getId() != null && eventAlertLog.getId() != 0)
                ealdao.update(eventAlertLog);
            else {
            	// eventAlertLog.setId(TimeUtil.getCurrentLongTime());
            	ealdao.add_requires_new(eventAlertLog);
                
                /*
                EventAlertAttrDao eaaDao = DataUtil.getBean(EventAlertAttrDao.class);
                for (EventAlertAttr eaa : eventAlertLog.getEventAlertAttrs().toArray(new EventAlertAttr[0])) {
                    eaa.setEventAlertLog(eventAlertLog);
                    log.debug(eaa.toString());
                    eaaDao.add(eaa);
                    // eventAlertLog.remove(eaa.getAttrName());
                    // eventAlertLog.append(eaa);
                }
                */
            }
		} catch (Exception e) {
			log.error(e, e);
		}
        
        return eventAlertLog;
    }
    
    public static EventAlertLog findOpenAlert(EventAlertLog event)
    throws Exception 
    {        
        log.debug(    "findOpenAlert conditions: ActivatorType["+event.getActivatorType()+                    
        "] ActivatorId["+event.getActivatorId()+ "] EventAlertName[" + event.getEventAlert().getName() + 
        "] Status["+event.getStatus()+"]");   
        
        //TODO IMPLEMENT
        /*
        try        
        {            
            FMProxy proxy = FMProxy.getInstance();
            FaultMgrLocal mgr = proxy.createFaultMgr();
            StringBuffer where = new StringBuffer();
            where.append(" instanceKey='" + mo + "' ");
            where.append(" and status=" + status);
            
            if (faultClassName!=null) 
            {
                where.append(" and faultClassName='" + faultClassName + "' ");
            }
            
            if( pattern != null )            
            {
                where.append(" and message like '%" + pattern + "%' ");
            }
            
            if(faultClassName != null && faultClassName.equals("Threshold"))            
            {
                where.append(" and thresholdValue=" + thresholdValue);
            }
            return mgr.getOpenAlert(where.toString());
        } 
        catch(Exception e) 
        {            
            log.error(e);
            throw e;
        }
        */
        return null;
    } 
}

class EventBrowser implements BrowserCallback {
    private static Log log = LogFactory.getLog(EventBrowser.class);
    
    @Override
    public Object doInJms(Session session, QueueBrowser browser)
            throws JMSException {
        // TODO Auto-generated method stub
        MapMessage msg = null;
        while (browser.getEnumeration().hasMoreElements()) {
            msg = (MapMessage)browser.getEnumeration().nextElement();
            log.info(msg.getString("location"));
        }
        return null;
    }
    
}
