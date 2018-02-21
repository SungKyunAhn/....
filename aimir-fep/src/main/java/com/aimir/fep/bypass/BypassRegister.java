package com.aimir.fep.bypass;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.session.IoSession;

import com.aimir.model.system.MeterProgram;

/**
 * Bypass 명령을 등록/실행/삭제(Life Time) 을 관리하는 역할. <br>
 * <p>Bypass 명령을 LogDB에 저장하여, 모뎀과 세션이 맺어지게 되면 Log정보를 읽어 명령을 처리하는 방식이다.</p>
 * @author kskim
 */
public class BypassRegister implements java.io.Serializable {

	private static final long serialVersionUID = -2271635485229599250L;

	private static Log log = LogFactory.getLog(BypassRegister.class);

	static private BypassRegister instance;
	
	public BypassRegister(){
	}
	
	/**
	 * 싱글톤 패턴 인스턴스 생성
	 * 
	 * @return
	 */
	static public BypassRegister getInstance() {
		if (instance == null)
			instance = new BypassRegister();
		return instance;
	}
	
	/**
	 * Register 에 등록된 명령이 있으면 실행 시킨다.
	 * @param modemSerial 
	 * @param session
	 * @throws Exception
	 */
	public void execute(String modemSerial, IoSession session) throws Exception{
		Bypass bypass = BypassFactory.create(modemSerial);
		bypass.execute(session, modemSerial, null);
		
	}
	
	/**
	 * bypass 명령을 등록시킨다.
	 * @param modemSerial 
	 * @param session
	 * @throws Exception
	 */
	public void add(String modemSerial, MeterProgram meterPro) throws Exception {
		Bypass bypass = BypassFactory.create(modemSerial);
		bypass.register(meterPro);
	}
	
	/**
	 * bypass 명령을 등록시킨다. //if4 1.2 command
	 * @param modemSerial 
	 * @param session
	 * @throws Exception
	 */
	public void add(String modemSerial, Map<String, String[][]> command) throws Exception {
		Bypass bypass = BypassFactory.create(modemSerial);
		bypass.register(command);
	}
	
	
}
