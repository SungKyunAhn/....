/**
 *오후 3:33:03 : kaze
 *
 *이 소스는 누리텔레콤의 소유입니다. 이 소스를 무단으로 도용하면 법에 따라 처벌을 받습니다.
 * 
 */
package com.aimir.fep.command.response.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.aimir.fep.command.response.common.Cmd_Action;
import com.aimir.fep.protocol.fmp.datatype.BYTE;
import com.aimir.fep.protocol.fmp.datatype.SMIValue;
import com.aimir.fep.protocol.fmp.datatype.WORD;
import com.aimir.fep.protocol.fmp.frame.ErrorCode;
import com.aimir.fep.protocol.fmp.frame.ServiceDataConstants;
import com.aimir.fep.protocol.fmp.frame.service.CommandData;
import com.aimir.fep.protocol.fmp.frame.service.ServiceData;
import com.aimir.fep.util.DataUtil;

/**
 * cmdMcuDiagnosis에 대한 Action 파일
 * @author kaze
 *
 */
@Component
public class Cmd_100_1_0_Action implements Cmd_Action{
	private static Log log = LogFactory.getLog(Cmd_100_1_0_Action.class);	
	@Override
	public ServiceData execute(CommandData receiveCommandData)
			throws Exception {
		CommandData responseCommandData = receiveCommandData; 
		responseCommandData.removeSmiValues();
		responseCommandData.setAttr(ServiceDataConstants.C_ATTR_RESPONSE);
		responseCommandData.setErrCode(new BYTE(ErrorCode.IF4ERR_NOERROR));
        
		SMIValue smi = DataUtil.getSMIValueByObject("streamEntry","00000000000000000200");
		responseCommandData.append(smi);
		responseCommandData.setCnt(new WORD(1));
		log.debug("Cmd_100_1_0_Action Completed!! ");
		return responseCommandData;
	}

}
