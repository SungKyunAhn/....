/**
 *오후 3:38:10 : kaze
 *
 *이 소스는 누리텔레콤의 소유입니다. 이 소스를 무단으로 도용하면 법에 따라 처벌을 받습니다.
 * 
 */
package com.aimir.fep.command.response.common;

import com.aimir.fep.protocol.fmp.frame.service.CommandData;
import com.aimir.fep.protocol.fmp.frame.service.ServiceData;

/**
 * 
 * @author kaze
 *
 */
public interface Cmd_Action {
	/**
	 * 서버가 받은 커맨드에 대한 처리 로직
	 * 로직 처리 후 응답 ServiceData를 리턴함
	 * @param session
	 * @param receiveSd - 서버가 받은 커맨드 데이타
	 * @return ServiceData - 응답을 담은 서비스 데이터를 리턴해 줌
	 * @throws Exception
	 */
	public ServiceData execute(CommandData receiveCommandData) throws Exception;
}
