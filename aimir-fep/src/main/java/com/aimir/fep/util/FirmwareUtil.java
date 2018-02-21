package com.aimir.fep.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.constants.CommonConstants.FW_OTA;
import com.aimir.constants.CommonConstants.FW_STATE;
import com.aimir.constants.CommonConstants.FW_TRIGGER;
import com.aimir.constants.CommonConstants.TR_STATE;
import com.aimir.dao.device.FirmwareHistoryDao;
import com.aimir.dao.device.FirmwareTriggerDao;
import com.aimir.fep.protocol.fmp.datatype.WORD;
import com.aimir.model.device.FirmwareHistory;
import com.aimir.util.Condition;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.Condition.Restriction;


/**
 * Firmware and File Utility Class
 *
 * @author Y.S Kim
 * @version $Rev: 1 $, $Date: 2005-12-07 15:59:15 +0900 $,
 */
public class FirmwareUtil
{
	
	private static Log log = LogFactory.getLog(FirmwareUtil.class);
    
    private static FirmwareHistoryDao fwHistoryDao = DataUtil.getBean(FirmwareHistoryDao.class);
    private static FirmwareTriggerDao fwTriggerDao = DataUtil.getBean(FirmwareTriggerDao.class);
	
	/**
	 * @param triggerId
	 * @param equipType
	 * @param equipId
	 * @param jobState
	 * @param jobErrorCode
	 */
	public static void updateTriggerAndHistory(String triggerId, int equipType, String equipId, int jobState, int jobErrorCode){
		try{
		    Set<Condition> condition = new HashSet<Condition>();
            condition.add(new Condition("id.trId", new Object[]{Long.parseLong(triggerId)}, null, Restriction.EQ));
            condition.add(new Condition("equipType", new Object[]{equipType}, null, Restriction.EQ));
            condition.add(new Condition("equipId", new Object[]{equipId}, null, Restriction.EQ));
            
            List<FirmwareHistory> fwHistoryList = fwHistoryDao.findByConditions(condition);
            if (fwHistoryList.size() == 1) {
                FirmwareHistory fwHistory = fwHistoryList.get(0);
                fwHistory.setErrorCode(jobErrorCode+"");
                fwHistory.setOtaState(FW_STATE.stateOf(jobState));
                
                fwHistoryDao.update(fwHistory);
            }
		}catch(Exception e){
			log.error(e);
		}
	}

	/**
	 * @param triggerId
	 * @param triggerStep
	 * @param triggerState
	 * OTA Event에서 호출 함
	 */
	public static void updateTriggerHistory(String triggerId, int triggerStep, int triggerState){
		
		log.info("Update TriggerHistory TRID=["+triggerId+"], TR STEP=["+FW_TRIGGER.valueOf(triggerStep).name()+"], TR STATE=["+TR_STATE.valueOf(triggerState).name()+"]");
		try{
			Set<Condition> condition = new HashSet<Condition>();
			condition.add(new Condition("id.trId", new Object[]{Long.parseLong(triggerId)}, null, Restriction.EQ));
			List<FirmwareHistory> fwHistoryList = fwHistoryDao.findByConditions(condition);
			if (fwHistoryList!= null && fwHistoryList.size() == 1) {
			    FirmwareHistory fwHistory = fwHistoryList.get(0);
			    fwHistory.setTriggerStep(FW_TRIGGER.valueOf(triggerStep));
			    fwHistory.setTriggerState(TR_STATE.valueOf(triggerState));
			    
			    fwHistoryDao.update(fwHistory);
			}
		}catch(Exception e){
			log.error(e);
		}
	}

	/**
	 * @param triggerId
	 * @param equipId
	 * @param OTAStep
	 * @param OTAState
	 * @param errCode
	 */
	@Deprecated
	public static void updateOTAHistory(String triggerId, String equipId, FW_OTA step, FW_STATE state, String errCode){
		
		log.info("Update TriggerHistory TRID=["+triggerId+"], OTA STEP=["+step.name()+"], OTA STATE=["+state.name()+"]");
		try{
		    Set<Condition> condition = new HashSet<Condition>();
            condition.add(new Condition("id.trId", new Object[]{Long.parseLong(triggerId)}, null, Restriction.EQ));
            condition.add(new Condition("equipId", new Object[]{equipId}, null, Restriction.EQ));
            
            List<FirmwareHistory> fwHistoryList = fwHistoryDao.findByConditions(condition);
            if (fwHistoryList!= null && fwHistoryList.size() == 1) {
                FirmwareHistory fwHistory = fwHistoryList.get(0);
                fwHistory.setOtaStep(step);
                fwHistory.setOtaState(state);
                fwHistory.setErrorCode(errCode);
                
                fwHistoryDao.update(fwHistory);
            }
		}catch(Exception e){
			log.error(e);
		}
	}
	/**
	 * @param equipList
	 * @param triggerId
	 * @param triggerStep
	 * @param triggerState
	 * @param OTAStep
	 * @param OTAState
	 * @param errCode
	 */
	public static void updateReDistHistory(List equipList, String triggerId,
	        int triggerStep, int triggerState, int OTAStep, int OTAState, String errCode){

	    try {
    	    Set<Condition> condition = new HashSet<Condition>();
            condition.add(new Condition("id.trId", new Object[]{Long.parseLong(triggerId)}, null, Restriction.EQ));
            
            for (int i = 0; i < equipList.size(); i++) {
                condition.add(new Condition("equipId", new Object[]{equipList.get(i)}, null, Restriction.EQ));
                
                List<FirmwareHistory> fwHistoryList = fwHistoryDao.findByConditions(condition);
                if (fwHistoryList!= null && fwHistoryList.size() == 1) {
                    FirmwareHistory fwHistory = fwHistoryList.get(0);
                    fwHistory.setOtaStep(FW_OTA.stepOf(OTAStep));
                    fwHistory.setOtaState(FW_STATE.stateOf(OTAState));
                    fwHistory.setTriggerStep(FW_TRIGGER.valueOf(triggerStep));
                    fwHistory.setTriggerState(TR_STATE.valueOf(triggerState));
                    fwHistory.setErrorCode(errCode);
                    
                    fwHistoryDao.update(fwHistory);
                }
            }
	    }
	    catch (Exception e) {
	        log.error(e);
	    }
	}

	public static String getCurrentYYYYMMDDHHMMSS() {
		String format = "yyyyMMddHHmmss";

		return DateTimeUtil.getCurrentDateTimeByFormat(format);
	}

	/**
     * decodeCodiVer
     * @param strVer
     * @return 33 -> 2.1
     */
    public static String decodeCodiVer(String strVer)
    {
        if(!strVer.contains(".")){
            strVer=Integer.toHexString(Integer.parseInt(strVer));
            if(strVer.length()>=2){
                strVer=strVer.substring(0,1)+"."+strVer.substring(1);
            }
        }
        return strVer;
    }

    /**
     * encodeCodiVer
     * @param strVer
     * @return 2.1 -> 33
     */
    public static String encodeCodiVer(String strVer)
    {
        if(strVer.contains(".")){
            strVer=strVer.replace(".", "");
            strVer=String.valueOf(Integer.parseInt(strVer, 16));
        }
        return strVer;
    }

    /**
     * decodeCodiBuild
     * @param strBuild
     * @return 10진수를 16진수로 변환 : 19 -> 13, 09->09
     */
    public static String decodeCodiBuild(String strBuild){
    	if(Integer.parseInt(strBuild)<10) {
    		return strBuild;
    	}
        return Integer.toHexString(Integer.parseInt(strBuild));
    }

    /**
     * encodeCodiBuild
     * @param strBuild
     * @return 16진수를 10진수로 변환 : 13 -> 19, 09->09
     */
    public static String encodeCodiBuild(String strBuild){
    	if(Integer.parseInt(strBuild)<10) {
    		return strBuild;
    	}
        return String.valueOf(Integer.parseInt(strBuild, 16));
    }
	/**
	 * @param gmtEntry
	 * @return
	 */
	public static String getTimeStampFromGmtEntry(String gmtEntry){
		String gmtYear = "gmtYear: ";
		String gmtMon = "gmtMon: ";
		String gmtDay = "gmtDay: ";
		String gmtHour = "gmtHour: ";
		String gmtMin = "gmtMin: ";
		String gmtSec = "gmtSec: ";
		String _year = gmtEntry.substring(gmtEntry.indexOf(gmtYear)+gmtYear.length(),
				gmtEntry.indexOf(gmtMon)).trim();
		String _mon = gmtEntry.substring(gmtEntry.indexOf(gmtMon)+gmtMon.length(),
				gmtEntry.indexOf(gmtDay)).trim();
		String _day = gmtEntry.substring(gmtEntry.indexOf(gmtDay)+gmtDay.length(),
				gmtEntry.indexOf(gmtHour)).trim();
		String _hour = gmtEntry.substring(gmtEntry.indexOf(gmtHour)+gmtHour.length(),
				gmtEntry.indexOf(gmtMin)).trim();
		String _min = gmtEntry.substring(gmtEntry.indexOf(gmtMin)+gmtMin.length(),
				gmtEntry.indexOf(gmtSec)).trim();
		String _sec = gmtEntry.substring(gmtEntry.indexOf(gmtSec)+gmtSec.length()).trim();

		log.debug("YEAR[" + _year + "] MONTH[" + _mon + "] DAY[" + _day +
		                                                       "] HOUR[" + _hour + "] MIN[" + _min + "] SEC[" + _sec + "]");

		int year = Integer.parseInt(_year);
		int month = Integer.parseInt(_mon);
		int day = Integer.parseInt(_day);
		int hour = Integer.parseInt(_hour);
		int min = Integer.parseInt(_min);
		int sec = Integer.parseInt(_sec);

		log.debug("YEAR[" + year + "] MONTH[" + month + "] DAY[" + day + "]" +
				"HOUR[" + hour + "] MINUTE[" + min + "] SECOND[" + sec + "]");

		String timestamp = year + (month < 10? "0"+month:""+month) +
		(day < 10? "0"+day:""+day) + (hour < 10? "0"+hour:hour) +
		(min < 10? "0"+min:""+min) + (sec < 10? "0"+sec:sec);
		return timestamp;
	}

	/**
	 * @param date
	 * @param Sec
	 * @return
	 */
	public static String getAddSecYYYYMMDDHHMMSS(String date,int Sec) {
		try {
			SimpleDateFormat sdf = new  SimpleDateFormat("yyyyMMddHHmmss");
			Calendar cal = Calendar.getInstance();
			cal.setTime(sdf.parse(date));
			cal.add(Calendar.SECOND, Sec);
			return sdf.format(cal.getTime());
		}
		catch (Exception e) {
			return null;
		}
	}

	/**
	 * @param sec
	 * @return
	 */
	public static String getSecToTime(int sec){
		int day, hour, min=0;
		min = sec / 60;
		sec %= 60;
		hour = min / 60;
		min %= 60;
		day = hour / 24;
		hour %= 24;
		if(day>0){
			return day+" Day "+hour+" Hour "+min+" Min "+sec+" Sec";
		}else if(hour>0){
			return hour+" Hour "+min+" Min "+sec+" Sec";
		}else if(min >0){
			return min+" Min "+sec+" Sec";
		}else {
			return sec+" Sec";
		}
	}


	public static String getHWVersion(WORD word){
		return word.decodeVersion();
	}
	
	public static String getFWVersion(WORD word) {
		return word.decodeVersion();
	}
	
	public static String getFWBuild(WORD word) {
		return word.getValue()+"";
	}

}