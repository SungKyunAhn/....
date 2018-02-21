package com.aimir.fep.meter.link;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aimir.constants.CommonConstants.EventStatus;
import com.aimir.constants.CommonConstants.SeverityType;
import com.aimir.dao.device.EventAlertDao;
import com.aimir.fep.util.EventUtil;
import com.aimir.model.device.EventAlert;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.MeterEventLog;

@Service
public class JsgtMeterEventLink implements MeterEventLink {
    private static Log log = LogFactory.getLog(JsgtMeterEventLink.class);

    @Autowired
    EventAlertDao eaDao;
    
    @Override
    public void execute(Object obj) {
        MeterEventLog meterEventLog = (MeterEventLog)obj;
        
        if (!meterEventLog.getMeterEventId().equals("STE.GE.SM110.1") && !meterEventLog.getMeterEventId().equals("STE.GE.SM110.2")) {
            EventAlertLog event = new EventAlertLog();
            event.setActivatorId(meterEventLog.getActivatorId());
            event.setActivatorType(meterEventLog.getActivatorType());
            event.setOpenTime(meterEventLog.getOpenTime());
            event.setMessage(meterEventLog.getMessage());
            event.setStatus(EventStatus.Acknowledged);
            event.setSeverity(SeverityType.Information);
            
            EventAlert ea = eaDao.findByCondition("name", "Equipment Notification");
            event.setEventAlert(ea);
            
            // 배터리 저전압
            if (meterEventLog.getMeterEventId().equals("STS.GE.SM110.20"))
                event.append(EventUtil.makeEventAlertAttr("mRID", "java.lang.String", "2.2.1.149"));
            // Demand Reset 탐지
            else if (meterEventLog.getMeterEventId().equals("STE.GE.SM110.20"))
                event.append(EventUtil.makeEventAlertAttr("mRID", "java.lang.String", "3.8.1.61"));
            // 미터 RAM 고장
            else if (meterEventLog.getMeterEventId().equals("STS.GE.SM110.13"))
                event.append(EventUtil.makeEventAlertAttr("mRID", "java.lang.String", "3.18.1.199"));
            // 미터 ROM 고장
            else if (meterEventLog.getMeterEventId().equals("STS.GE.SM110.14"))
                event.append(EventUtil.makeEventAlertAttr("mRID", "java.lang.String", "3.18.1.220"));
            // 테스트 모드 시작
            else if (meterEventLog.getMeterEventId().equals("STE.GE.SM110.32"))
                event.append(EventUtil.makeEventAlertAttr("mRID", "java.lang.String", "3.22.19.242"));
            // 테스트 모드 종료
            else if (meterEventLog.getMeterEventId().equals("STE.GE.SM110.33"))
                event.append(EventUtil.makeEventAlertAttr("mRID", "java.lang.String", "3.22.19.242"));
            // 공급 전압 저전압
            else if (meterEventLog.getMeterEventId().equals("MFE.GE.SM110.16"))
                event.append(EventUtil.makeEventAlertAttr("mRID", "java.lang.String", "6.38.1.150"));
            else if (meterEventLog.getMeterEventId().equals("STS.GE.SM110.16"))
                event.append(EventUtil.makeEventAlertAttr("mRID", "java.lang.String", "2.36.1.29"));

            log.debug(event.toString());
            try {
                EventUtil.sendNotification(event);
            }
            catch (Exception e) {
                log.error(e);
            }
        }
    }
}
