/**
 *오후 3:33:03 : kaze
 *
 *이 소스는 누리텔레콤의 소유입니다. 이 소스를 무단으로 도용하면 법에 따라 처벌을 받습니다.
 * 
 */
package com.aimir.fep.command.response.action;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.aimir.fep.command.response.common.Cmd_Action;
import com.aimir.fep.protocol.fmp.datatype.BYTE;
import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.protocol.fmp.datatype.SMIValue;
import com.aimir.fep.protocol.fmp.datatype.WORD;
import com.aimir.fep.protocol.fmp.frame.ErrorCode;
import com.aimir.fep.protocol.fmp.frame.ServiceDataConstants;
import com.aimir.fep.protocol.fmp.frame.service.CommandData;
import com.aimir.fep.protocol.fmp.frame.service.ServiceData;
import com.aimir.fep.protocol.security.OacServerApi;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

/**
 * cmdGetMeterSharedKey(111.1.4) 에 대한 Action 파일
 * @author elevas
 *
 */
@Component
public class Cmd_SP_111_1_4_Action implements Cmd_Action{
	private static Log log = LogFactory.getLog(Cmd_SP_111_1_4_Action.class);	
	@Override
	public ServiceData execute(CommandData receiveCommandData)
			throws Exception {
	    SMIValue[] param = receiveCommandData.getSMIValue();
	    String mcuId = "";
	    OacServerApi api  = new OacServerApi();
	    HashMap<String,String> sharedKey = null;
	    
	    CommandData responseCommandData = receiveCommandData; 
        responseCommandData.removeSmiValues();
        responseCommandData.setAttr(ServiceDataConstants.C_ATTR_RESPONSE);
        responseCommandData.setErrCode(new BYTE(ErrorCode.IF4ERR_NOERROR));
        
        int keyCnt = 0;
	    for (int i = 0; i < param.length; i++) {
	        log.debug(new String(((OCTET)param[i].getVariable()).getValue()));
	        
	        if (i == 0)
	            mcuId = new String(((OCTET)param[i].getVariable()).getValue());
	        else {
	            sharedKey = api.getMeterSharedKey(mcuId, new String(((OCTET)param[i].getVariable()).getValue()));
	            
	            if (sharedKey != null && sharedKey.get("MasterKey") != null && !"".equals(sharedKey.get("MasterKey"))) {
    	            responseCommandData.append(DataUtil.getSMIValueByObject("stringEntry", new String(((OCTET)param[i].getVariable()).getValue())));
    	            responseCommandData.append(DataUtil.getSMIValue(new OCTET(Hex.encode(sharedKey.get("MasterKey")))));
    	            responseCommandData.append(DataUtil.getSMIValue(new OCTET(Hex.encode(sharedKey.get("MulticastKey")))));
    	            responseCommandData.append(DataUtil.getSMIValue(new OCTET(Hex.encode(sharedKey.get("UnicastKey")))));
    	            responseCommandData.append(DataUtil.getSMIValue(new OCTET(Hex.encode(sharedKey.get("AuthenticationKey")))));
    	            
    	            keyCnt++;
	            }
	        }
	    }
        
		responseCommandData.setCnt(new WORD(keyCnt*5));
		log.debug("Cmd_SP_111_1_4_Action Completed!! ");
		return responseCommandData;
	}

}
