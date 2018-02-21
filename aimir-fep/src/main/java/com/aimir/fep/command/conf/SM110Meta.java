package com.aimir.fep.command.conf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.constants.CommonConstants.CircuitBreakerStatus;
import com.aimir.constants.CommonConstants.RelaySwitchStatus;

public class SM110Meta
{
    @SuppressWarnings("unused")
	private static Log _log = LogFactory.getLog(SM110Meta.class);
    // activate off :0, activate on :1, relay on:2, relay off:3 -> 좌측 코드는 Relay switch의 상태가 아니고 operate 코드임 by eunmiae

    /* Relay Operate Code */
    // 0 : clear active_ready_to_switchonrelay_status
    // 1 : set active_ready_to_switchornrelay_status
    // 2 : switch on relay
    // 3 : switch off relay
    /* Relay Status Code */
    // 0 : relay switch status OFF
    // 1 : relay switch status ON
    // 0 : relay activate OFF
    // 1 : relay activate ON
    public static String CLEAR = "0";
    public static String OFF = "3";
    public static String ON = "2";
    public static String ACTIVATEON = "1";
    public static String ACTIVATEOFF = "0";

    public static String RELAY_STATUS_ON = "1";
    public static String RELAY_STATUS_OFF = "0";

    // Add start by eunmiae
    public static String getSwitchStatus(String code) {
    	if(code.equals(RELAY_STATUS_ON)){
//    		return CircuitBreakerStatus.Activation.name(); 
    		// modify from Issues No.8 from  Nuri - Spasa discussion points_notes from meeting 22 March.xlsx
    		// Activation -> On
    		return RelaySwitchStatus.On.name(); 
    	}else{
    		// Deactivation -> Off
//    		return CircuitBreakerStatus.Deactivation.name();
    		return RelaySwitchStatus.Off.name();
    	}
    }
    // Add end by eunmiae

    public static String getActivateStatus(String code) {
    	if(code.equals(ACTIVATEON)){
    		return CircuitBreakerStatus.Activation.name();
    	}else{
    		return CircuitBreakerStatus.Deactivation.name();
    	}
    }

//    public static String getSwitchStatus(String code) {
//    	if(code.equals(ON)){
//    		return CircuitBreakerStatus.Activation.name();
//    	}else{
//    		return CircuitBreakerStatus.Deactivation.name();
//    	}
//    }
}
