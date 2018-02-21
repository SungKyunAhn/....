/**
 *오후 3:33:03 : kaze
 *
 *이 소스는 누리텔레콤의 소유입니다. 이 소스를 무단으로 도용하면 법에 따라 처벌을 받습니다.
 * 
 */
package com.aimir.fep.command.response.action;

import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import com.aimir.dao.device.MCUDao;
import com.aimir.fep.command.response.common.Cmd_Action;
import com.aimir.fep.protocol.fmp.datatype.BYTE;
import com.aimir.fep.protocol.fmp.datatype.INT;
import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.protocol.fmp.datatype.SMIValue;
import com.aimir.fep.protocol.fmp.datatype.STREAM;
import com.aimir.fep.protocol.fmp.datatype.WORD;
import com.aimir.fep.protocol.fmp.frame.ErrorCode;
import com.aimir.fep.protocol.fmp.frame.ServiceDataConstants;
import com.aimir.fep.protocol.fmp.frame.service.CommandData;
import com.aimir.fep.protocol.fmp.frame.service.ServiceData;
import com.aimir.fep.util.AESUtil;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.model.device.MCU;

/**
 * cmdGetKMSNetworkKey(111.1.6) 에 대한 Action 파일
 * @author goodjob
 *
 */
@Component
public class Cmd_SP_111_1_6_Action implements Cmd_Action{
	private static Log log = LogFactory.getLog(Cmd_SP_111_1_6_Action.class);	
	@Override
	public ServiceData execute(CommandData receiveCommandData)
			throws Exception {
	    SMIValue[] param = receiveCommandData.getSMIValue();
	    String dcuEUIId = "";
	    Integer networkKeyIndex = 2;
	    String networkKeyList = null;
	    
	    CommandData responseCommandData = receiveCommandData; 
        responseCommandData.removeSmiValues();
        responseCommandData.setAttr(ServiceDataConstants.C_ATTR_RESPONSE);
        responseCommandData.setErrCode(new BYTE(ErrorCode.IF4ERR_NOERROR));
        
        if(param == null || param.length != 1){
        	responseCommandData.setErrCode(new BYTE(ErrorCode.IF4ERR_INVALID_PARAM));
        	return responseCommandData;
        }
        
        log.debug("DCU ID="+receiveCommandData.getMcuId()+" EUI ID ="+ new String(((OCTET)param[0].getVariable()).getValue()));
        dcuEUIId = new String(((OCTET)param[0].getVariable()).getValue());

        JpaTransactionManager txmanager = (JpaTransactionManager)DataUtil.getBean("transactionManager");
        TransactionStatus txstatus = null;
        MCUDao mcuDao = DataUtil.getBean(MCUDao.class);
        
        try{
			txstatus = txmanager.getTransaction(null);

			MCU dcu = mcuDao.get(receiveCommandData.getMcuId());
			
			if(dcu != null){
				String encryptedKey = dcu.getNetworkKey();
				
				//networkKeyList = "00000000000000010000000000000002000000000000000300000000000000040000000000000005";	
				//networkKeyIndex = 2;
				
				if(encryptedKey == null || "".equals(encryptedKey)){
					
					byte[] hashKey = Hex.encode(dcuEUIId);
					networkKeyList = AESUtil.getHashcode(hashKey);
					encryptedKey = AESUtil.encrypt(networkKeyList, dcuEUIId);
					
					networkKeyIndex = getRandomNumberInRange(2, 6);
				}else{
					networkKeyList = AESUtil.decrypt(dcu.getNetworkKey(), dcuEUIId);
					networkKeyIndex = dcu.getNetworkKeyIdx();
				}
				
				dcu.setNetworkKey(encryptedKey);
				dcu.setNetworkKeyIdx(networkKeyIndex);
				mcuDao.update(dcu);
			}else{
				log.info("Return INVALID_PARAM");
	        	responseCommandData.setErrCode(new BYTE(ErrorCode.IF4ERR_INVALID_PARAM));
	        	return responseCommandData;
			}
			txmanager.commit(txstatus);
        } catch (Exception e){
        	log.error(e,e);
        	if (txstatus != null) txmanager.commit(txstatus);
        }
        
        responseCommandData.append(DataUtil.getSMIValue(new INT(networkKeyIndex)));
        responseCommandData.append(DataUtil.getSMIValue(new STREAM(networkKeyList))); //16byte*5
		responseCommandData.setCnt(new WORD(2));
		log.debug("Cmd_SP_111_1_6_Action Completed!!");
		return responseCommandData;
	}
	
	private static int getRandomNumberInRange(int min, int max) {

		if (min >= max) {
			throw new IllegalArgumentException("max must be greater than min");
		}

		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}

}
