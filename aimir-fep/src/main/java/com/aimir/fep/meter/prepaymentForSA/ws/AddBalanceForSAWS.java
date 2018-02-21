/**
 * AddBalanceWS.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.fep.meter.prepaymentForSA.ws;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.MeterStatus;
import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.OperationLogDao;
import com.aimir.dao.prepayment.AddBalanceWSChargingDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.ContractChangeLogDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.GroupDao;
import com.aimir.dao.system.GroupMemberDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.PrepaymentLogDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.dao.system.TariffEMDao;
import com.aimir.dao.system.TariffTypeDao;
import com.aimir.fep.command.ws.server.CommandWS;
import com.aimir.fep.command.ws.server.ResponseMap;
import com.aimir.fep.meter.prepaymentForSA.response.CommonResponseOfWS;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.IHDEventMessageUtil;
import com.aimir.fep.util.sms.SendSMS;
import com.aimir.model.device.Meter;
import com.aimir.model.device.OperationLog;
import com.aimir.model.prepayment.AddBalanceWSCharging;
import com.aimir.model.system.Code;
import com.aimir.model.system.Contract;
import com.aimir.model.system.ContractChangeLog;
import com.aimir.model.system.PrepaymentLog;
import com.aimir.model.system.Supplier;
import com.aimir.model.system.TariffEM;
import com.aimir.model.system.TariffType;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.DecimalUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeUtil;

/**
 * AddBalanceWS.java Description 
 * <p>
 * <pre>
 * Date          Version     Author   Description
 * 2011. 6. 28   v1.0        김상연   선불연계 - 과금 충전 시 연계
 * 2011. 8. 12   v1.0        문동규   클래스명 수정
 * 2012. 1. 1    V1.1        은미애   파라메터 추가 (Auth Code : 권한 코드, Municipality Code : 지방자치 코드) For South Africa
 * </pre>
 */

/**
 * AddBalanceForSAWS.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2012. 3. 14.   v1.0       enj         
 *
 */
@WebService(serviceName="AddBalanceForSAWS")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
@Service
public class AddBalanceForSAWS {
	
    protected static Log log = LogFactory.getLog(AddBalanceForSAWS.class);
    
    @Autowired
    protected MeterDao meterDao;

    @Autowired
    protected ContractDao contractDao;

    @Autowired
    protected ContractChangeLogDao contractChangeLogDao;

    @Autowired
    protected SupplierDao supplierDao;
    
    @Autowired
    protected TariffTypeDao tariffTypeDao;
    
    @Autowired
    protected TariffEMDao tariffEMDao;
    
    @Autowired
    protected AddBalanceWSChargingDao addBalanceWSChargingDao;

    @Autowired
    protected PrepaymentLogDao prepaymentLogDao;

    @Autowired
    protected CodeDao codeDao;
    
    @Autowired
    protected OperationLogDao operationLogDao;
    
    @Autowired
    protected LocationDao loctionDao;
    
    @Autowired
    protected GroupDao groupDao;
    
    @Autowired
    protected GroupMemberDao groupMemberDao;
    
	@Autowired
	CommandWS commandGw;
	
	@Autowired
	IHDEventMessageUtil ihdEventMessageUtil;

	private CommonResponseOfWS validate(String supplierName, String transactionId,
            String dateTime, String contractNumber, String accountId, Double amount, String municipalityCode) {
        CommonResponseOfWS commonResponseOfWS = new CommonResponseOfWS();
        log.info("validate arguments");
        
        // mandatory data check
        if(StringUtil.nullToBlank(supplierName).isEmpty()) {
            commonResponseOfWS.setRtnStatus(false);
            commonResponseOfWS.setTransactionId(transactionId);
            commonResponseOfWS.setErrorCode("ER100");
            commonResponseOfWS.setErrorDescription("Supplier Name is null.");
            return commonResponseOfWS;
        }
        if(StringUtil.nullToBlank(dateTime).isEmpty()) {
            commonResponseOfWS.setRtnStatus(false);
            commonResponseOfWS.setTransactionId(transactionId);
            commonResponseOfWS.setErrorCode("ER100");
            commonResponseOfWS.setErrorDescription("DateTime is null.");
            return commonResponseOfWS;

        }
        if(StringUtil.nullToBlank(contractNumber).isEmpty()) {
            commonResponseOfWS.setRtnStatus(false);
            commonResponseOfWS.setTransactionId(transactionId);
            commonResponseOfWS.setErrorCode("ER100");
            commonResponseOfWS.setErrorDescription("Contract Number is null.");
            return commonResponseOfWS;

        } 
        if(StringUtil.nullToBlank(accountId).isEmpty()) {
            commonResponseOfWS.setRtnStatus(false);
            commonResponseOfWS.setTransactionId(transactionId);
            commonResponseOfWS.setErrorCode("ER100");
            commonResponseOfWS.setErrorDescription("Transaction Number is null.");
            return commonResponseOfWS;

        } 
        if(StringUtil.nullToBlank(amount).isEmpty()) {
            commonResponseOfWS.setRtnStatus(false);
            commonResponseOfWS.setTransactionId(transactionId);
            commonResponseOfWS.setErrorCode("ER100");
            commonResponseOfWS.setErrorDescription("Amount is null.");
            return commonResponseOfWS;

        } 
        if(StringUtil.nullToBlank(municipalityCode).isEmpty()) {
            commonResponseOfWS.setRtnStatus(false);
            commonResponseOfWS.setTransactionId(transactionId);
            commonResponseOfWS.setErrorCode("ER100");
            commonResponseOfWS.setErrorDescription("Municipality Code is null.");
            return commonResponseOfWS;

        }
 
        // 날짜 유효성 체크
        if(dateTime.length() != 14 || !TimeUtil.checkDate(dateTime)) {
            commonResponseOfWS.setRtnStatus(false);
            commonResponseOfWS.setTransactionId(transactionId);
            commonResponseOfWS.setErrorCode("ER101");
            commonResponseOfWS.setErrorDescription("DateTime has wrong data format or data type.");
            return commonResponseOfWS;

        }

        // 숫자 체크
        if(!StringUtil.isDigit(String.valueOf(amount))) {
            commonResponseOfWS.setRtnStatus(false);
            commonResponseOfWS.setTransactionId(transactionId);
            commonResponseOfWS.setErrorCode("ER102");
            commonResponseOfWS.setErrorDescription("You must enter only numbers(0-9) in the Amount field.");
            return commonResponseOfWS;

        }

        // amount가 0원일 경우,
        if(amount.equals(0d) || amount.toString().length() == 0) {
            commonResponseOfWS.setRtnStatus(false);
            commonResponseOfWS.setTransactionId(transactionId);
            commonResponseOfWS.setErrorCode("ER100");
            commonResponseOfWS.setErrorDescription("Amount is zero.");
            return commonResponseOfWS;

        }

        Code municipalityCD = codeDao.getCodeIdByCodeObject(municipalityCode);
        
        if(municipalityCD == null || municipalityCD.getCode() == null ) {
            commonResponseOfWS.setRtnStatus(false);
            commonResponseOfWS.setTransactionId(transactionId);
            commonResponseOfWS.setErrorCode("ER104");
            commonResponseOfWS.setErrorDescription("The Municipality Code does not exist.");
            return commonResponseOfWS;

        }
        
        // if transaction id is not empty transaction ID check if the transaction id is the number or not
        if(!StringUtil.nullToBlank(transactionId).isEmpty() && !StringUtil.isDigit(transactionId)) {
            commonResponseOfWS.setRtnStatus(false);
            commonResponseOfWS.setTransactionId(transactionId);
            commonResponseOfWS.setErrorCode("ER110");
            commonResponseOfWS.setErrorDescription("You must enter only numbers(0-9) in the transaction ID.");
            return commonResponseOfWS;
        }
        
        return null;
    }
    
    private void addBalanceWSCharging(String municipalityCode, String supplierName, String dateTime, String contractNumber,
            String mdsId, String accountId, Double amount, Double powerLimit, Integer tariffCode, String source,
            String encryptionKey, String authCode, String transactionId) throws Exception {
        JpaTransactionManager txManager = null;
        TransactionStatus txStatus = null;
        
        try {
            txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");
            txStatus = txManager.getTransaction(null);
            log.info("addbalancewscharging start");
            // 웹서비스로그 저장
            Code municipalityCD = codeDao.getCodeIdByCodeObject(municipalityCode);
            
            AddBalanceWSCharging addBalanceWSCharging = new AddBalanceWSCharging();
            addBalanceWSCharging.setSupplierName(supplierName);
            addBalanceWSCharging.setDateTime(dateTime);
            addBalanceWSCharging.setContractNumber(contractNumber);
            addBalanceWSCharging.setMdsId(mdsId);
            addBalanceWSCharging.setAccountId(accountId);
            addBalanceWSCharging.setAmount(amount);
            addBalanceWSCharging.setPowerLimit(powerLimit);
            addBalanceWSCharging.setTariffCode(tariffCode);
            addBalanceWSCharging.setSource(source);
            addBalanceWSCharging.setEncryptionKey(encryptionKey);
            addBalanceWSCharging.setAuthCode(authCode);
            addBalanceWSCharging.setMunicipalityCode(municipalityCD);
            addBalanceWSCharging.setTransactionId(transactionId);
            addBalanceWSCharging.setWriteDate(TimeUtil.getCurrentTimeMilli());
    
            addBalanceWSChargingDao.add(addBalanceWSCharging);
        }
        finally {
            txManager.commit(txStatus);
        }
    }
    
    private CommonResponseOfWS validateContract(String contractNumber, String supplierName,
            String mdsId, String encryptionKey, String transactionId) throws Exception {
        log.info("validate contract");
        
        JpaTransactionManager txManager = null;
        TransactionStatus txStatus = null;
        
        try {
            txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");
            txStatus = txManager.getTransaction(null);
            CommonResponseOfWS commonResponseOfWS = new CommonResponseOfWS();
            
            Contract contract = null;
            Meter meter = null;
            Code creditType=null;
            Code status = null;
            Map<String, Object> conditionMap = new HashMap<String, Object>();
    
            // 계약 정보 존재 체크
            conditionMap.put("contractNumber", contractNumber);
            conditionMap.put("supplierName", supplierName);
            if(!StringUtil.nullToBlank(mdsId).isEmpty()) {
                conditionMap.put("mdsId", mdsId);
            }
    
            if (!StringUtil.nullToBlank(encryptionKey).isEmpty()) {
                conditionMap.put("keyNum", encryptionKey);
            }
            log.info("getContract Info start");
            // select Contract
            List<Contract> listCont = contractDao.getPrepaymentContract(conditionMap);
            log.info("getContract Info complete");
            if (listCont != null && listCont.size() > 0) { // 계약 정보 유
                contract = listCont.get(0);
                log.info("getMetert");
                if(!StringUtil.nullToBlank(mdsId).isEmpty()) {
                     meter = meterDao.get(mdsId);
                } else { // Meter Serial Number가 존재 하지 않을 경우는 계약에서 미터정보를 취득한다.
                     meter = meterDao.get( contract.getMeter() == null ? "0" : contract.getMeter().getMdsId());
                }
                log.info("creditType, status start");
                creditType = codeDao.get(contract.getCreditTypeCodeId());
                status = codeDao.get(contract.getStatusCodeId());
                log.info("creditType, status complete");
                
                // 후불 고객인지 체크
                if(Code.POSTPAY.equals(creditType.getCode())) {
                    commonResponseOfWS.setRtnStatus(false);
                    commonResponseOfWS.setTransactionId(transactionId);
                    commonResponseOfWS.setErrorCode("ER106");
                    commonResponseOfWS.setErrorDescription("This Customer's credit type is post pay.");
                    txManager.commit(txStatus);
                    return commonResponseOfWS;
    
                }
    
                // 해지된 고객인지 체크
                if("2.1.3".equals(status.getCode())) {
                    commonResponseOfWS.setRtnStatus(false);
                    commonResponseOfWS.setTransactionId(transactionId);
                    commonResponseOfWS.setErrorCode("ER107");
                    commonResponseOfWS.setErrorDescription("This Customer has a terminated contract.");
                    txManager.commit(txStatus);
                    return commonResponseOfWS;
    
                }
            } else {// 계약 정보 무
                commonResponseOfWS.setRtnStatus(false);
                commonResponseOfWS.setTransactionId(transactionId);
                commonResponseOfWS.setErrorCode("ER103");
                commonResponseOfWS.setErrorDescription("The requested Contract Info does not exist.");
                txManager.commit(txStatus);
                return commonResponseOfWS;
            }
    
            if(meter == null) {
                // 미터가 존재하지 않을 경우
                commonResponseOfWS.setRtnStatus(false);
                commonResponseOfWS.setTransactionId(transactionId);
                commonResponseOfWS.setErrorCode("ER109");
                commonResponseOfWS.setErrorDescription("The requested Contract does not have a meter.");
                txManager.commit(txStatus);
                return commonResponseOfWS;
            }
            
            return null;
        }
        finally {
            txManager.commit(txStatus);
        }
    }
    
    /**
     * method name : charging<br/>
     * method Desc : 과금 충전 시 연계
     *
     * @param supplierName      Utility ID - 공급사 아이디
     * @param dateTime          Date & Time - 충전 날짜
     * @param contractNumber    Contract ID - 고객의 계약번호
     * @param mdsId             Meter Serial Number - 미터 시리얼 번호
     * @param accountId         Account ID - 충전 아이디
     * @param amount            Amount - 충전액
     * @param powerLimit        Power Limit(kWh) - 전력 사용량
     * @param tariffCode        Tariff Code - 과금 분류 코드(요금 코드)
     * @param source            source - 충전 방식(온라인, 카드)<b/>
     *                          값이 있으면 Contract.keyType 에 저장 
     * @param encryptionKey     Encryption Key - 암호화 시 인증 키<b/>
     *                          값이 있으면 Contract 조회 시 Contract.keyNum 값 체크
     * @param authCode          권한 코드
     * @param municipalityCode  지방 자치 코드
     * @param transactionId    Transaction ID - 처리 아이디
     *                  
     * @return
     * @throws Exception
     */
    @WebMethod
    public @WebResult(name="response") CommonResponseOfWS charging(
    		@WebParam(name="supplierName") String supplierName, 
            @WebParam(name="dateTime") String dateTime, 
            @WebParam(name="contractNumber") String contractNumber,
            @WebParam(name="mdsId") String mdsId, 
            @WebParam(name="accountId") String accountId,
            @WebParam(name="amount") Double amount, 
            @WebParam(name="powerLimit") Double powerLimit,
            @WebParam(name="tariffCode") Integer tariffCode,
            @WebParam(name="source") String source, 
            @WebParam(name="encryptionKey") String encryptionKey,
            @WebParam(name="authCode") String authCode,
            @WebParam(name="municipalityCode") String municipalityCode,
            @WebParam(name="transactionId") String transactionId) throws Exception {
        StringBuilder sb = new StringBuilder();

        sb.append("\n supplierName[" + supplierName + "]");
        sb.append("\n dateTime[" + dateTime + "]");
        sb.append("\n contractNumber[" + contractNumber + "]");
        sb.append("\n mdsId[" + mdsId + "]");
        sb.append("\n accountId[" + accountId + "]");
        sb.append("\n amount[" + amount + "]");
        sb.append("\n powerLimit[" + powerLimit + "]");
        sb.append("\n tariffCode[" + tariffCode + "]");         
        sb.append("\n source[" + source + "]");
        sb.append("\n encryptionKey[" + encryptionKey + "]");
        sb.append("\n authCode[" + authCode + "]");
        sb.append("\n municipalityCode[" + municipalityCode + "]");
        sb.append("\nRequest data : transaction ID[" + transactionId + "]");
        
        log.info(sb.toString());

        // balance webservice logging
        addBalanceWSCharging(municipalityCode, supplierName, dateTime, contractNumber, 
                mdsId, accountId, amount, powerLimit, tariffCode, source, encryptionKey,
                authCode, transactionId);
        
        CommonResponseOfWS commonResponseOfWS = 
                validate(supplierName, transactionId, dateTime, contractNumber, accountId, amount, municipalityCode);
        
        if (commonResponseOfWS != null) return commonResponseOfWS;
        else commonResponseOfWS = new CommonResponseOfWS();
        
        commonResponseOfWS = validateContract(contractNumber, supplierName, mdsId, encryptionKey, transactionId);
        
        if (commonResponseOfWS != null) return commonResponseOfWS;
        else commonResponseOfWS = new CommonResponseOfWS();
        
        JpaTransactionManager txManager = null;
        TransactionStatus txStatus = null;

        try {
            txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");
            txStatus = txManager.getTransaction(null);
            log.info("addbalancewscharging start");
            // Municipality Code의 존재 체크
            Code municipalityCD = codeDao.getCodeIdByCodeObject(municipalityCode);
           
            Contract contract = contractDao.findByCondition("contractNumber", contractNumber);
            Meter meter = contract.getMeter();
            
            // 미수긂이 있으면 비율에 따라 납부한 후에 잔액을 충전한다.
            Double arrearsRate = getArrearsRate(contract, dateTime.substring(0, 8));
            Double chargedArrears = 0.0;
            Double currentArrears = 0.0;
            if (contract.getCurrentArrears() != null && contract.getCurrentArrears() > 0) {
                currentArrears = contract.getCurrentArrears();
                chargedArrears = amount * arrearsRate / 100.0;

                // 현재 미수금보다 납부액이 크면 현재 미수금으로 대체한다.
                if (chargedArrears > currentArrears)
                    chargedArrears = currentArrears;
                
                currentArrears -= chargedArrears;
                amount -= chargedArrears;
            }
            
            Double currentCredit = new Double(StringUtil.nullToZero(contract.getCurrentCredit())) + amount;
            log.debug("<<<<<<<<<<<<<<<<<<<<<after : " + currentCredit);

            Integer lastChargeCnt = new Integer(StringUtil.nullToZero(contract.getLastChargeCnt())) + 1;
            Code keyCode = null;
            // insert ContractChangeLog
            log.info("start addContractChangeLog");
            addContractChangeLog(contract, "lastTokenDate", contract.getLastTokenDate(), dateTime);
            addContractChangeLog(contract, "lastTokenId", contract.getLastTokenId(), accountId);
            addContractChangeLog(contract, "chargedCredit", contract.getChargedCredit(), amount.toString());
            addContractChangeLog(contract, "currentCredit", contract.getCurrentCredit(), currentCredit.toString());
            addContractChangeLog(contract, "lastChargeCnt", contract.getLastChargeCnt(), lastChargeCnt.toString());
            
            if (contract.getCurrentArrears() != null && contract.getCurrentArrears() > 0 && chargedArrears > 0)
                addContractChangeLog(contract, "currentArrears", contract.getCurrentArrears(), currentArrears);
            
            if (!StringUtil.nullToBlank(source).isEmpty()) {
                keyCode = codeDao.getCodeIdByCodeObject(source);
                
                if ( keyCode != null && contract.getKeyTypeCodeId() != null) {
                    Code keyType = codeDao.get(contract.getKeyTypeCodeId());
                    addContractChangeLog(contract, "keyType", keyType.getCode(), keyCode.getCode());
                }
            }
            log.info("complete addContractChangeLog");
            
            Code creditType = contract.getCreditType();
            Code creditTypeCode = null;

            // Emergency Credit Mode 이고 잔액이 0 초과인 경우.
            if (StringUtil.nullToBlank(contract.getCreditType().getCode()).equals("2.2.2") && currentCredit > 0d) {
                creditTypeCode = codeDao.findByCondition("code", "2.2.1");  // 선불모드코드
                contract.setCreditType(creditTypeCode);
                addContractChangeLog(contract, "creditType", creditType.getCode(), creditTypeCode.getCode());
            }

            contract.setLastTokenDate(dateTime);
            contract.setLastTokenId(accountId);
            contract.setChargedCredit(amount);
            contract.setCurrentCredit(currentCredit);
            contract.setLastChargeCnt(lastChargeCnt);
            contract.setCurrentArrears(currentArrears);

            if (!StringUtil.nullToBlank(source).isEmpty()) {
                contract.setKeyType(keyCode);
            }
            
            // update Contract
            contractDao.update(contract);
            addPrepyamentLog(contract, encryptionKey, source, keyCode, amount, 
                    dateTime, transactionId, powerLimit, currentCredit, 
                    authCode, municipalityCD, accountId, chargedArrears);
            
            txManager.commit(txStatus);
            log.info("contract update complete");
            
            SMSNotification(contract, amount, currentCredit, false);
            // Notification 통보 프레임 워크 생성 후 개발 End

            relayOn(currentCredit, contractNumber, mdsId, amount);
            
            commonResponseOfWS.setRtnStatus(true);
            commonResponseOfWS.setTransactionId(transactionId);
            commonResponseOfWS.setErrorCode("");
            commonResponseOfWS.setErrorDescription("");
            return commonResponseOfWS;
        }
        catch (Exception e) {
            if (txManager != null) {
                try {
                    txManager.rollback(txStatus);
                } catch (Exception e1) {}
            }

            log.error(e, e);
            commonResponseOfWS.setRtnStatus(false);
            commonResponseOfWS.setTransactionId(transactionId);
            commonResponseOfWS.setErrorCode("EXCEPTION");
            commonResponseOfWS.setErrorDescription(e.getMessage());
            return commonResponseOfWS;
        }
    }
    
    private double getArrearsRate(Contract contract, String yyyymmdd) {
        TariffType tariffType = tariffTypeDao.get(contract.getTariffIndexId()); 
        Integer tariffTypeCode = tariffType.getCode();
        Map<String, Object> tariffParam = new HashMap<String, Object>();

        tariffParam.put("tariffTypeCode", tariffTypeCode);
        tariffParam.put("tariffIndex", tariffType);
        tariffParam.put("searchDate", yyyymmdd);
        
        List<TariffEM> tariffEMList = tariffEMDao.getApplyedTariff(tariffParam);
        Double rateRebalancingLevy = null;
        if (tariffEMList != null && tariffEMList.size() > 0) {
            rateRebalancingLevy = tariffEMList.get(0).getRateRebalancingLevy(); 
            if (rateRebalancingLevy == null)
                return 0.0;
            else
                return rateRebalancingLevy;
        }
        else
            return 0.0;
    }

    private void relayOn(double currentCredit, String contractNumber, String mdsId, double amount) {
        JpaTransactionManager txManager = null;
        TransactionStatus txStatus = null;

        try {
            txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");
            txStatus = txManager.getTransaction(null);
            log.info("if cut off, relay start");
            
            if(currentCredit > 0) {
                log.debug("post");
                
                boolean isCutOff = false;
                String rtnStr = "";
                ResultStatus resultStatus = ResultStatus.FAIL;
                
                Contract contract = contractDao.findByCondition("contractNumber", contractNumber);
                Meter meter = contract.getMeter();
                Code meterStatus = meter.getMeterStatus();
                String mcuId = meter.getModem().getMcu().getSysID();
                
                log.debug("meter status: " + meterStatus.getCode());
                log.debug("constant status: " + CommonConstants.getMeterStatusByName(MeterStatus.CutOff.name()).getCode());
                // 차단되어 있는지 체크
                if (meterStatus != null &&
                     ( (meterStatus.getCode().equals(CommonConstants.getMeterStatusByName(MeterStatus.CutOff.name()).getCode())) 
                     || (contract.getStatus().getCode().equals("2.1.1"))) ) {
                    
                    isCutOff = true;
                }
                log.debug("<<<<<<<<<<<<<<<<<<<<<cutoff : " + isCutOff);
                if (isCutOff) {
                    try {
                        resultStatus = ResultStatus.SUCCESS;
                        ResponseMap responseMap = commandGw.relayValveOn(mcuId, mdsId);
                        Object[] values = responseMap.getResponse().values().toArray(new Object[0]);
                        
                        for (Object o : values) {
                            rtnStr += (String)o + " ";
                            
                            if (rtnStr.contains("failReason")) {
                                resultStatus = ResultStatus.FAIL;
                                break;
                            }
                        }
                    } catch (Exception e) {
                        rtnStr = e.getMessage();
                    } 
    
                    if(resultStatus == ResultStatus.SUCCESS) {
                        //미터 차단 해지 메세지
                        
                        //CASE : IHD
                        //IHD 그룹인지 판별할때 필요
                        log.debug("meter.getMdsId() : " + mdsId);
                        Map<String, Object> conditionMapGroup = new HashMap<String,Object>();
                        Integer groupId = groupMemberDao.getGroupIdbyMember(mdsId);
                        conditionMapGroup.put("groupId", groupId.toString());
                        if(!("".equals(groupId)) || groupId != 0) {
                            Map<String, Object> conditionMap = new HashMap<String, Object>();
                            conditionMap.put("gropuId", groupId);
                            String groupType = groupDao.getGroupTypeByGroup(conditionMapGroup);
                            log.debug("groupId : " + groupId +", groupType : " + groupType);
                            //IHD 그룹인 경우
                            if("IHD".equals(groupType)) {
                                log.debug("send IHD Event Message");
                                // 에너지 공급을 재개 하였습니다.
                                ihdEventMessageUtil.getEventMessage(mdsId, "Restore Power",
                                        "Your balance is now available. The power is supplied again.");
                            }
                        }
                        
                        //CASE : SMS 
                        SMSNotification(contract, amount, currentCredit, isCutOff);
                    }
                    
                    Code operationCode = codeDao.getCodeIdByCodeObject("8.1.4");
    
                    if (operationCode != null) {
                        String currDateTime = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");
                        OperationLog log = new OperationLog();
    
                        log.setOperatorType(1);//operator
                        log.setOperationCommandCode(operationCode);
                        log.setYyyymmdd(currDateTime.substring(0,8));
                        log.setHhmmss(currDateTime.substring(8,14));
                        log.setYyyymmddhhmmss(currDateTime);
                        log.setDescription("");
                        log.setErrorReason(resultStatus.name());
                        log.setResultSrc("");
                        log.setStatus(resultStatus.getCode());
                        log.setTargetName(mdsId);
                        log.setTargetTypeCode(meter.getMeterType());
                        //log.setUserId(CommandOperator.WEBSERVICE.getOperatorName());
                        log.setSupplier(meter.getSupplier());
                        operationLogDao.add(log);
                    }
                    log.debug("<<<<<<<<<<<<<<<<<<<<<end : ");
                }
            }
            // CommandOperationUtil 생성 후 개발 부분 End
            txManager.commit(txStatus);
        }
        catch (Exception e) {
            log.warn(e, e);
            
            if (txStatus != null) {
                try {
                    txManager.rollback(txStatus);
                }
                catch (Exception t) {}
            }
        }
    }
    
    private void addPrepyamentLog(Contract contract, String encryptionKey, String source, Code keyCode,
            Double amount, String dateTime, String transactionId, Double powerLimit, Double currentCredit, String authCode,
            Code municipalityCD, String accountId, Double chargedArrears)throws Exception {
        
        // insert PrepaymentLog
        PrepaymentLog prepaymentLog = new PrepaymentLog();

        prepaymentLog.setCustomer(contract.getCustomer());
        prepaymentLog.setContract(contract);
        prepaymentLog.setKeyNum(encryptionKey);
        if (!StringUtil.nullToBlank(source).isEmpty()) {
             prepaymentLog.setKeyType(keyCode);
        }
        prepaymentLog.setChargedCredit(amount);
        prepaymentLog.setLastTokenDate(dateTime);
        prepaymentLog.setLastTokenId(transactionId);
        Integer emergencyYn = null;
        if (contract.getEmergencyCreditAvailable() != null) {
            emergencyYn = (contract.getEmergencyCreditAvailable()) ? 1 : 0;
        }
        prepaymentLog.setEmergencyCreditAvailable(emergencyYn);
        prepaymentLog.setPowerLimit(powerLimit);
        prepaymentLog.setBalance(currentCredit);
        prepaymentLog.setAuthCode(authCode);
        prepaymentLog.setMunicipalityCode(municipalityCD);
        prepaymentLog.setDescr(accountId); // for Ghana
        prepaymentLog.setChargedArrears(chargedArrears);
        prepaymentLog.setLocation(contract.getLocation());
        prepaymentLog.setTariffIndex(contract.getTariffIndex());
        prepaymentLogDao.add(prepaymentLog);
        log.info("prepaymentLog insert complete");
    }
    
    /**
     * method name : addContractChangeLog
     * method Desc : ContractChangeLog 에 데이터 insert
     *
     * @param contract
     * @param field
     * @param beforeValue
     * @param afterValue
     */
    private void addContractChangeLog(Contract contract, String field, Object beforeValue, Object afterValue) {
        
        // ContractChangeLog Insert
        ContractChangeLog contractChangeLog = new ContractChangeLog();

        contractChangeLog.setContract(contract);
        contractChangeLog.setCustomer(contract.getCustomer());
        contractChangeLog.setStartDatetime(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
        contractChangeLog.setChangeField(field);

        if (beforeValue == null) {
            contractChangeLog.setBeforeValue(null);
        } else {
            contractChangeLog.setBeforeValue(StringUtil.nullToBlank(beforeValue));
        }

        if (afterValue == null) {
            contractChangeLog.setAfterValue(null);
        } else {
            contractChangeLog.setAfterValue(StringUtil.nullToBlank(afterValue));
        }

//        contractChangeLog.setOperator(operator);
        contractChangeLog.setWriteDatetime(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
//        contractChangeLog.setDescr(descr);

        contractChangeLogDao.add(contractChangeLog);
    }
    
    /**
     * method name : SMSNotification
     * method Desc : Charge에 대한 SMS 통보
     * @param contract, amount, isCutOff
     */
    private void SMSNotification(Contract contract, Double amount, Double currentCredit, Boolean isCutOff) {
    	try {
	    	Map<String, Object> condition = new HashMap<String, Object>();
			condition.put("prepayCreditId", codeDao.getCodeIdByCode("2.2.1"));
			condition.put("emergencyICreditId", codeDao.getCodeIdByCode("2.2.2"));
			condition.put("smsYn", true);
			condition.put("contractId", contract.getId());
			List<Map<String, Object>> contractInfo = contractDao.getContractSMSYN(condition);
			
			if(contractInfo.size() > 0) {
				String mobileNo = contractInfo.get(0).get("MOBILENO").toString().replace("-", "");
				
				Supplier supplier = supplierDao.get(contract.getSupplierId());
				DecimalFormat cdf = DecimalUtil.getDecimalFormat(supplier.getCd());
				String text = null;
				if(isCutOff) {
					text =  "Your balance is now available. The power is supplied again."
							+ "\n Supply Type : " + contractInfo.get(0).get("SERVICETYPE")
							+ "\n Charge Amount : " + amount.toString()
							+ "\n Current Credit : " +  cdf.format(currentCredit).toString();
				} else {
					text = "Customer Name : " + contractInfo.get(0).get("CUSTOMERNAME")
							+ "\n Supply Type : " + contractInfo.get(0).get("SERVICETYPE")
							+ "\n Charge Amount : " + amount.toString()
							+ "\n Current Credit : " +  cdf.format(currentCredit).toString();
				}

//				Properties prop = new Properties();
//    			prop.load(getClass().getClassLoader().getResourceAsStream("config/fmp.properties"));
				Properties prop = FMPProperty.getProperties();
				String smsClassPath = prop.getProperty("smsClassPath");
				SendSMS obj = (SendSMS) Class.forName(smsClassPath).newInstance();
				
				Method m = obj.getClass().getDeclaredMethod("send", String.class, String.class, Properties.class);
				String messageId = (String) m.invoke(obj, mobileNo, text, prop);
				
				if(!"".equals(messageId)) {
					log.info("contractId [ "+ contract.getId() +"],	SMS messageId [" + messageId + "]");
//					contractDao.updateSmsNumber(contract.getId(), messageId);
				}
			}
    	} catch (Exception e) {
    		log.warn(e,e);
		}
    }
}
