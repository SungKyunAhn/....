/**
 * GetBalanceWS.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.fep.meter.prepaymentForSA.ws;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants.CircuitBreakerStatus;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.prepayment.GetBalanceWSGetHistoryDao;
import com.aimir.dao.prepayment.GetBalanceWSGetInfoDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.PrepaymentLogDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.dao.system.TariffEMDao;
import com.aimir.dao.system.TariffWMDao;
import com.aimir.fep.meter.prepaymentForSA.response.BalanceInformation;
import com.aimir.fep.meter.prepaymentForSA.response.CommonResponseOfWS;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.prepayment.GetBalanceWSGetInfo;
import com.aimir.model.system.Contract;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeUtil;

/**
 * GetBalanceWS.java Description
 *
 *
 * Date          Version     Author   Description
 * 2011. 7. 22.  v1.0        문동규   선불연계 - 잔액 및 사용량 정보 취득
 *
 */

@WebService(serviceName="GetBalanceForSAWS")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
@Service
public class GetBalanceForSAWS {

    protected static Log log = LogFactory.getLog(GetBalanceForSAWS.class);

    @Autowired
    protected CodeDao codeDao;

    @Autowired
    protected MeterDao meterDao;

    @Autowired
    protected ContractDao contractDao;

    @Autowired
    protected SupplierDao supplierDao;

    @Autowired
    protected PrepaymentLogDao prepaymentLogDao;

    @Autowired
    protected GetBalanceWSGetInfoDao getBalanceWSGetInfoDao;

    @Autowired
    protected GetBalanceWSGetHistoryDao getBalanceWSGetHistoryDao;
    
    @Autowired
    protected TariffEMDao tariffEMDao;
    
    @Autowired
    protected TariffWMDao tariffWMDao;

    /**
     * method name : getInfo
     * method Desc : 현재 잔액 정보 요청
     * 
     * @param supplierName      Utility ID - 공급사 아이디
     * @param contractNumber    Contract ID - 고객의 계약번호
     * @param mdsId             Meter Serial Number - 미터 시리얼 번호
     * @param transactionId    Transaction ID - 처리 아이디 
     * @return supplierName     Utility ID - 공급사 아이디
     *         contractNumber   Contract ID - 고객의 계약번호
     *         mdsId            Meter Serial Number - 미터 시리얼 번호
     *         paymentMode      Payment Mode - 현재 과금 모드 (선불, 후불, Emergency Credit)
     *         currentCredit    Credit Status - 현재 잔액
     *         emergencyYn      Emergency Credit Available - Emergency credit 가능여부
     *         remainingDate    Credit date remaning - 현재 잔액으로 사용 가능한 일수
     *         switchStatus     Supply Status - Relay switch 상태 (공급차단/재개)
     * @throws Exception
     */
    @WebMethod
    public @WebResult(name="response") BalanceInformation getInfo(
            @WebParam(name="supplierName") String supplierName,
            @WebParam(name="contractNumber") String contractNumber,
            @WebParam(name="mdsId") String mdsId,
            @WebParam(name="transactionId") String transactionId) throws Exception {

        Map<String, Object> map = new HashMap<String, Object>();
        StringBuilder sb = new StringBuilder();
        BalanceInformation balanceInform = new BalanceInformation();
        CommonResponseOfWS commonResponseOfWS = new CommonResponseOfWS();
        sb.append("\n supplierName[" + supplierName + "]");
        sb.append("\n contractNumber[" + contractNumber + "]");
        sb.append("\n mdsId[" + mdsId + "]");
        sb.append("\nRequest data : transaction ID[" + transactionId + "]");

        log.info(sb.toString());

        // mandatory data check
        if (StringUtil.nullToBlank(supplierName).isEmpty()) {
        	commonResponseOfWS.setRtnStatus(false);
        	commonResponseOfWS.setTransactionId(transactionId);
        	commonResponseOfWS.setErrorCode("ER100");
        	commonResponseOfWS.setErrorDescription("Supplier Name is null.");
        	balanceInform.setCommonResponseOfWS(commonResponseOfWS);
        	return balanceInform;

        } else if(StringUtil.nullToBlank(contractNumber).isEmpty()) {
        	commonResponseOfWS.setRtnStatus(false);
        	commonResponseOfWS.setTransactionId(transactionId);
        	commonResponseOfWS.setErrorCode("ER100");
        	commonResponseOfWS.setErrorDescription("Contract Number is null.");
        	balanceInform.setCommonResponseOfWS(commonResponseOfWS);
        	return balanceInform;
        }

        // if transaction id is not empty transaction ID check if the transaction id is the number or not
        if(!StringUtil.nullToBlank(transactionId).isEmpty() && !StringUtil.isDigit(transactionId)) {
        	commonResponseOfWS.setRtnStatus(false);
        	commonResponseOfWS.setTransactionId(transactionId);
        	commonResponseOfWS.setErrorCode("ER110");
        	commonResponseOfWS.setErrorDescription("You must enter only numbers(0-9) in the transaction ID.");
        	balanceInform.setCommonResponseOfWS(commonResponseOfWS);
        	return balanceInform;
        }

        JpaTransactionManager txManager = null;
        TransactionStatus txStatus = null;

        try {
            txManager = (JpaTransactionManager)DataUtil.getBean("transactionManager");
            txStatus = txManager.getTransaction(null);

            // 웹서비스로그 저장
            GetBalanceWSGetInfo getBalanceWSGetInfo = new GetBalanceWSGetInfo();
            
            getBalanceWSGetInfo.setSupplierName(supplierName);
            getBalanceWSGetInfo.setContractNumber(contractNumber);
            getBalanceWSGetInfo.setMdsId(mdsId);
            getBalanceWSGetInfo.setTransactionId(transactionId);
            getBalanceWSGetInfo.setWriteDate(TimeUtil.getCurrentTimeMilli());

            getBalanceWSGetInfoDao.add(getBalanceWSGetInfo);
           
            List<Object> list = contractDao.getContractIdByContractNo(contractNumber);
            if(list.size() == 0) {
            	commonResponseOfWS.setRtnStatus(false);
            	commonResponseOfWS.setTransactionId(transactionId);
            	commonResponseOfWS.setErrorCode("ER103");
            	commonResponseOfWS.setErrorDescription("The requested Contract Info does not exist.");
            	balanceInform.setCommonResponseOfWS(commonResponseOfWS);
            	txManager.commit(txStatus);
            	return balanceInform;
            }
            
            Contract contract = (Contract)contractDao.getContractIdByContractNo(contractNumber).get(0);

            // 선불 잔액 갱신 로직 호출 - DaoImpl에 있는 선불계산 관련 소스들을 스케줄러로 분리함에 따라 주석처리.
            /*
            if(CommonConstants.MeterType.EnergyMeter.getServiceType().equals(contract.getServiceTypeCode())) {
                tariffEMDao.saveEmBillingDailyWithTariffEM(contract);
            } else if(CommonConstants.MeterType.WaterMeter.getServiceType().equals(contract.getServiceTypeCode())) {
                tariffWMDao.saveWMChargeUsingDailyUsage(contract);
            }
             */
            
            Map<String, Object> conditionMap = new HashMap<String, Object>();

            conditionMap.put("contractNumber", contractNumber);
            conditionMap.put("supplierName", supplierName);
            conditionMap.put("mdsId", mdsId);

            List<Map<String, Object>> listCont = contractDao.getPrepaymentContractBalanceInfo(conditionMap);

            if (listCont != null && listCont.size() > 0) {
                map = listCont.get(0);

                commonResponseOfWS.setRtnStatus(true);
            	commonResponseOfWS.setTransactionId(transactionId);
            	commonResponseOfWS.setErrorCode("");
            	commonResponseOfWS.setErrorDescription("");
                balanceInform.setContractNumber((String)map.get("contractNumber"));
                balanceInform.setCurrentCredit((Double)map.get("currentCredit"));
                balanceInform.setMdsId((String)map.get("mdsId"));
                balanceInform.setSupplierName((String)map.get("supplierName"));
                balanceInform.setPaymentMode(codeDao.getCodeIdByCodeObject((String)map.get("creditTypeCode")).getName());
                balanceInform.setCommonResponseOfWS(commonResponseOfWS);
//                		reconversionPaymentMode((String)map.get("creditTypeCode")));

                switch((Integer)map.get("switchStatus")) {
	                case 0 :
	                    balanceInform.setSwitchStatus(CircuitBreakerStatus.Deactivation.name());
	                    break;
	                case 1 :
	                	balanceInform.setSwitchStatus(CircuitBreakerStatus.Activation.name());
	                    break;
	                case 2 :
	                	balanceInform.setSwitchStatus(CircuitBreakerStatus.Standby.name());
	                    break;
                }
                // TODO - 은미애 과장님 함수 호출하여 가져옴
//              map.put("remainingDate", 0);
                log.info("\nReturn Data: contract Number : [" + balanceInform.getContractNumber() + "]");
                log.info("\nReturn Data: current Credit : [" + balanceInform.getCurrentCredit() + "]");
                log.info("\nReturn Data: Meter Device Serial ID : [" + balanceInform.getMdsId() + "]");
                log.info("\nReturn Data: Supplier Name : [" + balanceInform.getSupplierName() + "]");
                log.info("\nReturn Data: Switch Status : [" + balanceInform.getSwitchStatus() + "]");

            } else {                
            	commonResponseOfWS.setRtnStatus(false);
            	commonResponseOfWS.setTransactionId(transactionId);
            	commonResponseOfWS.setErrorCode("ER103");
            	commonResponseOfWS.setErrorDescription("The requested Contract Info does not exist.");
            	balanceInform.setCommonResponseOfWS(commonResponseOfWS);
            	txManager.commit(txStatus);
            	return balanceInform;
            }

            txManager.commit(txStatus);

            return balanceInform;
        } catch (Exception e) {
            log.error(e, e);
            if (txManager != null) {
                try {
                    txManager.rollback(txStatus);
                }
                catch (Exception t) {}
            }
            
        	commonResponseOfWS.setRtnStatus(false);
        	commonResponseOfWS.setTransactionId(transactionId);
        	commonResponseOfWS.setErrorCode("EXCEPTION");
        	commonResponseOfWS.setErrorDescription(e.getMessage());
        	balanceInform.setCommonResponseOfWS(commonResponseOfWS);
        	
            return balanceInform;
        }
    }
}