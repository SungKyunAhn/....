package com.aimir.fep.bypass;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionStatus;

import com.aimir.dao.device.ModemDao;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.system.DeviceModel;

/**
 * 모델 이름으로 구현된 actions Package 의 Class를 생성한다. <br>
 * <p>현재 package 하위 의 actions package 중에서 모델 이름으로 된 Class를 찾아 생성한다.</p> 
 * 
 * @author kskim
 */
public class BypassFactory implements java.io.Serializable{
	private static final long serialVersionUID = 2632407117454173604L;
	private static Log log = LogFactory.getLog(BypassFactory.class);
	
	/**
	 * 모뎀 의 모델 이름을 읽어와 모델명과 동일한 Class 를 찾아 생성하고 없으면 null을 리턴한다.
	 * @param modemSerial
	 * @return
	 */
	public static Bypass create(String modemSerial){
		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		String deviceName=null;
		try {
			txManager = DataUtil.getBean(JpaTransactionManager.class);
			txStatus = txManager.getTransaction(null);			
			
			//모델 정보를 읽어온다.
			ModemDao modemDao = DataUtil.getBean(ModemDao.class);
			Modem modem = modemDao.get(modemSerial);
			Set<Meter> meters = modem.getMeter();
			Meter meter = meters.iterator().next();
			DeviceModel deviceModel = meter.getModel();
			
			String protocolVersion = "";
			String nameSpace = "";
			if(modem != null){
				protocolVersion = modem.getProtocolVersion();
				nameSpace = modem.getNameSpace();
			}			

			//Bypass 객체의 하위 impl package 에 있는 모델명 객체 명을 준비한다.  
			deviceName = deviceModel.getName();
			StringBuilder packageName = new StringBuilder();
			packageName.append(Bypass.class.getPackage().getName());
			packageName.append(".actions.");
			
			if(protocolVersion != null && !protocolVersion.equals("0101")){
				packageName.append("Command_");
				packageName.append(protocolVersion+"_");
				packageName.append(nameSpace);
				packageName.append("_Action");
			}else{
				packageName.append(deviceName);
				packageName.append("Action");
			}


			//객체를 생성
			Class<?> businessClass = Class.forName(packageName.toString());
			Bypass bypass = (Bypass) businessClass.newInstance();
			bypass.setModem(modem);
			bypass.setMeter(meter);
			return bypass;
		} catch (Exception e) {
			log.error("Check the '"+"packageName"+".java' file.");
			log.error(e);
			return null;
		}
	}
}
