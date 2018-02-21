package com.aimir.fep.meter.prepaymentForSA.mbean;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jws.WebParam;
import javax.jws.WebResult;

import org.apache.activemq.command.ActiveMQQueue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.CircuitBreakerStatus;
import com.aimir.constants.CommonConstants.ContractStatus;
import com.aimir.constants.CommonConstants.MeterStatus;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.prepayment.AddBalanceWSChargingDao;
import com.aimir.dao.prepayment.GetBalanceWSGetInfoDao;
import com.aimir.dao.prepayment.VendorCasherDao;
import com.aimir.dao.prepayment.VerifyPrepaymentCustomerWSDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.ContractChangeLogDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.CustomerDao;
import com.aimir.dao.system.DepositHistoryDao;
import com.aimir.dao.system.GroupDao;
import com.aimir.dao.system.GroupMemberDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.OperatorDao;
import com.aimir.dao.system.PrepaymentLogDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.dao.system.TariffEMDao;
import com.aimir.dao.system.TariffTypeDao;
import com.aimir.dao.system.TariffWMDao;
import com.aimir.esapi.AimirAuthenticator;
import com.aimir.fep.command.ws.server.CommandWS;
import com.aimir.fep.meter.prepaymentForSA.response.BalanceInformation;
import com.aimir.fep.meter.prepaymentForSA.response.CommonResponseOfWS;
import com.aimir.fep.meter.prepaymentForSA.response.CustomerInformation;
import com.aimir.fep.meter.prepaymentForSA.response.VendorInformation;
import com.aimir.fep.meter.prepaymentForSA.response.VendorPaymentInformation;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.IHDEventMessageUtil;
import com.aimir.fep.util.sms.SendSMS;
import com.aimir.model.device.Meter;
import com.aimir.model.prepayment.AddBalanceWSCharging;
import com.aimir.model.prepayment.DepositHistory;
import com.aimir.model.prepayment.GetBalanceWSGetInfo;
import com.aimir.model.prepayment.VendorCasher;
import com.aimir.model.prepayment.VerifyPrepaymentCustomerWS;
import com.aimir.model.system.Code;
import com.aimir.model.system.Contract;
import com.aimir.model.system.ContractChangeLog;
import com.aimir.model.system.Customer;
import com.aimir.model.system.Location;
import com.aimir.model.system.Operator;
import com.aimir.model.system.PrepaymentLog;
import com.aimir.model.system.Supplier;
import com.aimir.model.system.TariffEM;
import com.aimir.model.system.TariffType;
import com.aimir.model.system.TariffWM;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.DecimalUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeUtil;

@Service
@Scope("prototype")
public class PrepaymentForSAMBean {
	protected static Log log = LogFactory.getLog(PrepaymentForSAMBean.class);
	
    @Autowired
    protected MeterDao meterDao;

    @Autowired
    protected ContractDao contractDao;
    
    @Autowired
    protected CustomerDao customerDao;

    @Autowired
    protected ContractChangeLogDao contractChangeLogDao;

    @Autowired
    protected SupplierDao supplierDao;
    
    @Autowired
    protected TariffTypeDao tariffTypeDao;
    
    @Autowired
    protected TariffEMDao tariffEMDao;
    
    @Autowired
    protected TariffWMDao tariffWMDao;
    
    @Autowired
    protected AddBalanceWSChargingDao addBalanceWSChargingDao;

    @Autowired
    protected PrepaymentLogDao prepaymentLogDao;

    @Autowired
    protected DepositHistoryDao depositHistoryDao;

    @Autowired
    protected CodeDao codeDao;
    
    @Autowired
    protected LocationDao loctionDao;
    
    @Autowired
    protected GroupDao groupDao;
    
    @Autowired
    protected GroupMemberDao groupMemberDao;
    
    @Autowired
    protected GetBalanceWSGetInfoDao getBalanceWSGetInfoDao;
    
    @Autowired
    protected VerifyPrepaymentCustomerWSDao verifyPrepaymentCustomerWSDao;
    
    @Autowired
    protected VendorCasherDao vendorCasherDao;
    
    @Autowired
    protected OperatorDao operatorDao;
    
    @Autowired
    protected LocationDao locationDao;

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
            log.error("RETURN:ER100[Supplier Name is null.]");
            return commonResponseOfWS;
        }
        if(StringUtil.nullToBlank(dateTime).isEmpty()) {
            commonResponseOfWS.setRtnStatus(false);
            commonResponseOfWS.setTransactionId(transactionId);
            commonResponseOfWS.setErrorCode("ER100");
            commonResponseOfWS.setErrorDescription("DateTime is null.");
            log.error("RETURN:ER100[DateTime is null.]");
            return commonResponseOfWS;

        }
        if(StringUtil.nullToBlank(contractNumber).isEmpty()) {
            commonResponseOfWS.setRtnStatus(false);
            commonResponseOfWS.setTransactionId(transactionId);
            commonResponseOfWS.setErrorCode("ER100");
            commonResponseOfWS.setErrorDescription("Contract Number is null.");
            log.error("RETURN:ER100[Contract Number is null]");
            return commonResponseOfWS;

        } 
        if(StringUtil.nullToBlank(accountId).isEmpty()) {
            commonResponseOfWS.setRtnStatus(false);
            commonResponseOfWS.setTransactionId(transactionId);
            commonResponseOfWS.setErrorCode("ER100");
            commonResponseOfWS.setErrorDescription("Transaction Number is null.");
            log.error("RETURN:ER100[Transaction Number is null]");
            return commonResponseOfWS;

        } 
        if(StringUtil.nullToBlank(amount).isEmpty()) {
            commonResponseOfWS.setRtnStatus(false);
            commonResponseOfWS.setTransactionId(transactionId);
            commonResponseOfWS.setErrorCode("ER100");
            commonResponseOfWS.setErrorDescription("Amount is null.");
            log.error("RETURN:ER100[Amount is null.]");
            return commonResponseOfWS;

        } 
        if(StringUtil.nullToBlank(municipalityCode).isEmpty()) {
            commonResponseOfWS.setRtnStatus(false);
            commonResponseOfWS.setTransactionId(transactionId);
            commonResponseOfWS.setErrorCode("ER100");
            commonResponseOfWS.setErrorDescription("Municipality Code is null.");
            log.error("RETURN:ER100[Municipality Code is null.]");
            return commonResponseOfWS;

        }
 
        // 날짜 유효성 체크
        if(dateTime.length() != 14 || !TimeUtil.checkDate(dateTime)) {
            commonResponseOfWS.setRtnStatus(false);
            commonResponseOfWS.setTransactionId(transactionId);
            commonResponseOfWS.setErrorCode("ER101");
            commonResponseOfWS.setErrorDescription("DateTime has wrong data format or data type.");
            log.error("RETURN:ER101[DateTime has wrong data format or data type]");
            return commonResponseOfWS;

        }

        // 숫자 체크
        if(!StringUtil.isDigit(String.valueOf(amount))) {
            commonResponseOfWS.setRtnStatus(false);
            commonResponseOfWS.setTransactionId(transactionId);
            commonResponseOfWS.setErrorCode("ER102");
            commonResponseOfWS.setErrorDescription("You must enter only numbers(0-9) in the Amount field.");
            log.error("RETURN:ER102[You must enter only numbers(0-9) in hte Amount field.]");
            return commonResponseOfWS;

        }

        // amount가 0원일 경우,
        if(amount.equals(0d) || amount.toString().length() == 0) {
            commonResponseOfWS.setRtnStatus(false);
            commonResponseOfWS.setTransactionId(transactionId);
            commonResponseOfWS.setErrorCode("ER100");
            commonResponseOfWS.setErrorDescription("Amount is zero.");
            log.error("RETURN:ER100[Amount is zero.]");
            return commonResponseOfWS;

        }

        JpaTransactionManager txmanager = null;
        TransactionStatus txstatus = null;
        try {
            txmanager = (JpaTransactionManager)DataUtil.getBean("transactionManager");
            txstatus = txmanager.getTransaction(null);
            Code municipalityCD = codeDao.getCodeIdByCodeObject(municipalityCode);
            
            if(municipalityCD == null || municipalityCD.getCode() == null ) {
                commonResponseOfWS.setRtnStatus(false);
                commonResponseOfWS.setTransactionId(transactionId);
                commonResponseOfWS.setErrorCode("ER104");
                commonResponseOfWS.setErrorDescription("The Municipality Code does not exist.");
                log.error("RETURN:ER104[The Municipality Code not exist.]");
                return commonResponseOfWS;
            }
        }
        finally {
            if (txstatus != null) txmanager.commit(txstatus);
        }
        
        
        // if transaction id is not empty transaction ID check if the transaction id is the number or not
        if(!StringUtil.nullToBlank(transactionId).isEmpty() && !StringUtil.isDigit(transactionId)) {
            commonResponseOfWS.setRtnStatus(false);
            commonResponseOfWS.setTransactionId(transactionId);
            commonResponseOfWS.setErrorCode("ER110");
            commonResponseOfWS.setErrorDescription("You must enter only numbers(0-9) in the transaction ID.");
            log.error("RETURN:ER110[You must enter only numbers(0-9) in the transaction ID.]");
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
    	    Boolean isNullMeter = false;
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
                log.info("search Meter");
                if(!StringUtil.nullToBlank(mdsId).isEmpty()) {
                	log.info("getMetert mdsId is not Empty");
                     meter = meterDao.get(mdsId);
                     log.info("getMetert");
                     if(meter==null) {
                    	 isNullMeter = true;
                    	 log.info("isNullMeter : " + isNullMeter);
                     }
                } else { // Meter Serial Number가 존재 하지 않을 경우는 계약에서 미터정보를 취득한다.
                	log.info("getMetert mdsId is Empty");
					if(contract.getMeterId() != null) {
						log.info("contract.getMeterId() + " + contract.getMeterId());
						isNullMeter = false;
						log.info("isNullMeter : " + isNullMeter);
					} else {
						log.info("contract.getMeter()");
						meter = meterDao.get( contract.getMeter() == null ? "0" : contract.getMeter().getMdsId());
						log.info("getMetert");
						if(meter==null) {
							isNullMeter = true;
							log.info("isNullMeter : " + isNullMeter);
						}else {
							isNullMeter = false;
							log.info("isNullMeter : " + isNullMeter);
						}
					}
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
                    //txManager.commit(txStatus);
                    log.error("RETURN:ER106[This Customer's credit type is post pay.]");
                    return commonResponseOfWS;
    
                }
    
                // 해지된 고객인지 체크
                if("2.1.3".equals(status.getCode())) {
                    commonResponseOfWS.setRtnStatus(false);
                    commonResponseOfWS.setTransactionId(transactionId);
                    commonResponseOfWS.setErrorCode("ER107");
                    commonResponseOfWS.setErrorDescription("This Customer has a terminated contract.");
                    //txManager.commit(txStatus);
                    log.error("RETURN:ER107[This Customer has a terminated contract.]");
                    return commonResponseOfWS;
    
                }
            } else {// 계약 정보 무
                commonResponseOfWS.setRtnStatus(false);
                commonResponseOfWS.setTransactionId(transactionId);
                commonResponseOfWS.setErrorCode("ER103");
                commonResponseOfWS.setErrorDescription("The requested Contract Info does not exist.");
                //txManager.commit(txStatus);
                log.error("RETURN:ER103[The requested Contract Info does not exist.]");
                return commonResponseOfWS;
            }
            
            if(isNullMeter == true) {
                // 미터가 존재하지 않을 경우
                commonResponseOfWS.setRtnStatus(false);
                commonResponseOfWS.setTransactionId(transactionId);
                commonResponseOfWS.setErrorCode("ER109");
                commonResponseOfWS.setErrorDescription("The requested Contract does not have a meter.");
                //txManager.commit(txStatus);
                log.error("RETURN:ER109[The requested Contract does not have a meter.]");
                return commonResponseOfWS;
            }
            
            return null;
        } catch(Exception e) {
        	log.error(e,e);
        	return null;
        } finally {
        	log.info("finally");
            txManager.commit(txStatus);
        }
	}
	
    public CommonResponseOfWS charging(
    		String supplierName, 
            String dateTime, 
            String contractNumber,
            String mdsId, 
            String accountId,
            Double amount, 
            Double powerLimit,
            Integer tariffCode,
            String source, 
            String encryptionKey,
            String authCode,
            String municipalityCode,
            String transactionId) throws Exception {
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
        sb.append("\n transaction ID[" + transactionId + "]");

        log.info(sb.toString());

        // balance webservice logging
        addBalanceWSCharging(municipalityCode, supplierName, dateTime, contractNumber, 
                mdsId, accountId, amount, powerLimit, tariffCode, source, encryptionKey,
                authCode, transactionId);
        
        log.debug("validate start");
        CommonResponseOfWS commonResponseOfWS = 
                validate(supplierName, transactionId, dateTime, contractNumber, accountId, amount, municipalityCode);
        
        if (commonResponseOfWS != null) return commonResponseOfWS;
        else commonResponseOfWS = new CommonResponseOfWS();
        try{
        	commonResponseOfWS = validateContract(contractNumber, supplierName, mdsId, encryptionKey, transactionId);
        }catch(Exception e) {
        	log.error(e,e);
        }
        if (commonResponseOfWS != null) return commonResponseOfWS;
        else commonResponseOfWS = new CommonResponseOfWS();
        
        log.debug("validate end");
        JpaTransactionManager txManager = null;
        TransactionStatus txStatus = null;

        try {
            txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");
            txStatus = txManager.getTransaction(null);
            log.info("addbalancewscharging start");
            // Municipality Code의 존재 체크
            Code municipalityCD = codeDao.getCodeIdByCodeObject(municipalityCode);
           
            Contract contract = contractDao.findByCondition("contractNumber", contractNumber);
            //Charging이 Allowed 인지 체크 필요
        	Boolean chargeAvailable = contract.getChargeAvailable();
        	if(chargeAvailable == null || chargeAvailable == false) {
	        	txManager.commit(txStatus);
	        	commonResponseOfWS.setRtnStatus(false);
	        	commonResponseOfWS.setTransactionId(transactionId);
	        	commonResponseOfWS.setErrorCode("ER105");
	        	commonResponseOfWS.setErrorDescription("Contract's Charging is not 'Allow'.");
	        	return commonResponseOfWS;
        	}
            
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
            
            Integer credirTypeId = contract.getCreditTypeCodeId();
            Code creditType = codeDao.get(credirTypeId);
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

            Map<String, Object> smsInfo = getSMSInformation(contract, amount, currentCredit, false);
            if(smsInfo != null) {
            	Map<String, Object> smsParams = new HashMap<String, Object>();
	            smsParams.put("command", "sendSMS");
	            smsParams.put("smsMsg", smsInfo.get("smsMsg"));
	            smsParams.put("smsYn", true);
	            smsParams.put("mobileNo", smsInfo.get("MOBILENO"));
	            smsParams.put("contractId", contract.getId());
	            activemqProcess(smsParams);
            }

            relayOn(currentCredit, contractNumber, mdsId, amount);
            
            commonResponseOfWS.setRtnStatus(true);
            commonResponseOfWS.setTransactionId(transactionId);
            commonResponseOfWS.setErrorCode("");
            commonResponseOfWS.setErrorDescription("");
            log.info("addbalancewscharging End");
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
    	Double rateRebalancingLevy = 0.0;
    	
        TariffType tariffType = tariffTypeDao.get(contract.getTariffIndexId()); 
        Integer tariffTypeCode = tariffType.getCode();
        Map<String, Object> tariffParam = new HashMap<String, Object>();

        tariffParam.put("tariffTypeCode", tariffTypeCode);
        tariffParam.put("tariffIndex", tariffType);
        tariffParam.put("searchDate", yyyymmdd);
        
        Integer serviceTypeCodeId = contract.getServiceTypeCodeId();
        Code serviceType = codeDao.get(serviceTypeCodeId);
        if("3.1".equals(serviceType.getCode())) {
        	List<TariffEM> tariffEMList = tariffEMDao.getApplyedTariff(tariffParam);
            if (tariffEMList != null && tariffEMList.size() > 0) {
                rateRebalancingLevy = tariffEMList.get(0).getRateRebalancingLevy(); 
                if (rateRebalancingLevy == null)
                    return 0.0;
                else
                    return rateRebalancingLevy;
            }
            else
                return 0.0;
        } else if("3.2".equals(serviceType.getCode())) {
        	List<TariffWM> tariffWMList = tariffWMDao.getApplyedTariffForPrepay(tariffParam);
            if (tariffWMList != null && tariffWMList.size() > 0) {
                rateRebalancingLevy = tariffWMList.get(0).getRateRebalancingLevy();
                if (rateRebalancingLevy == null)
                    return 0.0;
                else
                    return rateRebalancingLevy;
            }
            else
                return 0.0;
        }
        
        return rateRebalancingLevy;
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
                
                Map<String,Object> condition = new HashMap<String, Object>();
                condition.put("contractNumber", contractNumber);
                Map<String, Object> requestInfo = contractDao.getRequestDataForUSSDPOS(condition);

                Integer meterStatusCodeId = Integer.parseInt(StringUtil.nullToZero(requestInfo.get("meterStatusCodeId")));
                Code meterStatus = codeDao.get(meterStatusCodeId);
                Integer statusCodeId = Integer.parseInt(StringUtil.nullToZero(requestInfo.get("statusCodeId")));
                Code status = codeDao.get(statusCodeId);
                String mcuId = requestInfo.get("sysId") == null ? null : (String)requestInfo.get("sysId");
                mdsId = requestInfo.get("mdsId") == null ? null : (String)requestInfo.get("mdsId");
                String saverName = requestInfo.get("saverName") == null ? null : (String)requestInfo.get("saverName");
                log.info("saver name : " + saverName);
                
                log.debug("meter status: " + meterStatus.getCode());
                log.debug("constant status: " + CommonConstants.getMeterStatusByName(MeterStatus.CutOff.name()).getCode());
                // 차단되어 있는지 체크
                if (meterStatus != null &&
                     ( (meterStatus.getCode().equals(CommonConstants.getMeterStatusByName(MeterStatus.CutOff.name()).getCode())) 
                     || (status.getCode().equals("2.1.1"))) ) {
                    
                    isCutOff = true;
                }
                log.debug("<<<<<<<<<<<<<<<<<<<<<cutoff : " + isCutOff);
                if (isCutOff) {
                	Map<String,Object> params = new HashMap<String, Object>();
                	params.put("mdsId",mdsId);
	        		params.put("mcuId",mcuId);
	        		params.put("saverName",saverName);
	        		params.put("command","relayValveOn");
	        		
	        		Contract contract = new Contract();
	        		contract.setId(requestInfo.get("contractId") == null ? null : (Integer)requestInfo.get("contractId"));
	        		contract.setSupplierId(requestInfo.get("supplierId") == null ? null : (Integer)requestInfo.get("supplierId"));
	        		
                	Map<String, Object> map = getSMSInformation(contract, amount, currentCredit,isCutOff);
                	if(map != null) {
                		params.put("mobileNo", map.get("MOBILENO"));
    	        		params.put("contractId",contract.getId());
    	        		params.put("smsYn",true);
    	        		params.put("saverName",saverName);
    	        		params.put("smsMsg",map.get("smsMsg"));
                	} else {
    	        		params.put("smsYn",false);
                	}

                	activemqProcess(params);
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

    private void activemqProcess(Map<String, Object> params) {
    	String mqURL = FMPProperty.getProperty("activemq.broker.url","tcp://localhost:61616");
    	log.info("mqURL : " + mqURL);
    	String[] mqURLList = {mqURL};
    	if(mqURL.contains(",")) {
    		mqURL = mqURL.substring(mqURL.indexOf("(")+1,mqURL.indexOf(")"));
    		mqURLList = mqURL.split(",");
    	}
    	
    	Connection connection = null;
    	Session session = null;
    	for (int i = 0; i < mqURLList.length; i++) {
    		try {
	    		ConnectionFactory connectionFactory = new org.apache.activemq.ActiveMQConnectionFactory(mqURLList[i]);
	    		Destination destination = new ActiveMQQueue("ServiceData.CommandData");
	    		connection = connectionFactory.createConnection();
	    		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

	    		log.debug("activemq session and connection open");

	    		MessageProducer producer = session.createProducer(destination);
	    		producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
	    		MapMessage message = session.createMapMessage();
	    		message.setString("command",(String)params.get("command"));
	    		message.setBoolean("smsYn",(Boolean)params.get("smsYn"));
	    		
	    		if(params.get("mcuId") != null) {
	    			message.setString("mcuId",(String)params.get("mcuId"));
	    		}
	    		if(params.get("mdsId") != null) {
	    			message.setString("mdsId",(String)params.get("mdsId"));
	    		}
	    		if(params.get("saverName") != null) {
	    			message.setString("saverName",(String)params.get("saverName"));
	    		}
	    		if((Boolean)params.get("smsYn") && params.get("smsMsg") != null) {
	    			message.setString("smsMsg",(String)params.get("smsMsg"));
	    		}
	    		if(params.get("contractId") != null) {
	    			message.setInt("contractId",(Integer)params.get("contractId"));
	    		}
	    		if(params.get("mobileNo") != null) {
	    			message.setString("mobileNo",(String)params.get("mobileNo"));
	    		}
	    		
	    		log.debug("Sending message: " + message.toString());
	  	      	producer.send(message);
	    		log.debug("command message send");
	  	      	Thread.sleep(1000);
	  	      	log.debug("activemq session and connection close");
	  	      	break;
    		}
    		catch(Exception e) {
    			log.warn(e,e);
    		}
    		finally {
		        if (session != null) {
		            try {
		                session.close();
		            }
		            catch (Exception e) {}
		        }
		        if (connection != null) {
		            try {
		                connection.close();
		            }
		            catch (Exception e) {}
		        }
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
     * method name : getSMSInformation
     * method Desc : SMS에 필요한 고객 정보를 검색
     * @param contract, amount, currentCredit, isCutOff
     */
    private Map<String, Object> getSMSInformation(Contract contract, Double amount, Double currentCredit, Boolean isCutOff) {
        JpaTransactionManager txManager = null;
        TransactionStatus txStatus = null;

        try {
            txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");
            txStatus = txManager.getTransaction(null);
            Map<String, Object> returnMap = null;
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
				returnMap = contractInfo.get(0);
				returnMap.put("smsMsg", text);
				returnMap.put("MOBILENO", mobileNo);
			}
			txManager.commit(txStatus);
			return returnMap;
    	} catch (Exception e) {
    		log.warn(e,e);
    		txManager.commit(txStatus);
		}
        return null;
    }   

    /**
     * method name : SMSNotification
     * method Desc : Charge에 대한 SMS 통보
     * @param contract, amount, isCutOff
     */
    @Deprecated
    private void SMSNotification(Contract contract, Double amount, Double currentCredit, Boolean isCutOff) {
        JpaTransactionManager txManager = null;
        TransactionStatus txStatus = null;

        try {
            txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");
            txStatus = txManager.getTransaction(null);

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
					//contractDao.updateSmsNumber(contract.getId(), messageId);
				}
			}
			txManager.commit(txStatus);
    	} catch (Exception e) {
    		log.warn(e,e);

    		if (txManager != null) {
    		    try {
    		        txManager.rollback(txStatus);
    		    }
    		    catch (Exception t) {}
    		}
		}
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
    
    public BalanceInformation getInfo(
            String supplierName,
            String contractNumber,
            String mdsId,
            String transactionId) throws Exception {

        Map<String, Object> map = new HashMap<String, Object>();
        StringBuilder sb = new StringBuilder();
        BalanceInformation balanceInform = new BalanceInformation();
        CommonResponseOfWS commonResponseOfWS = new CommonResponseOfWS();
        sb.append("\n supplierName[" + supplierName + "]");
        sb.append("\n contractNumber[" + contractNumber + "]");
        sb.append("\n mdsId[" + mdsId + "]");
        sb.append("\n transaction ID[" + transactionId + "]");

        log.info(sb.toString());

        // mandatory data check
        if (StringUtil.nullToBlank(supplierName).isEmpty()) {
        	commonResponseOfWS.setRtnStatus(false);
        	commonResponseOfWS.setTransactionId(transactionId);
        	commonResponseOfWS.setErrorCode("ER100");
        	commonResponseOfWS.setErrorDescription("Supplier Name is null.");
        	balanceInform.setCommonResponseOfWS(commonResponseOfWS);
        	log.error("RETURN:ER100[Supplier Name is null.]");
        	return balanceInform;

        } else if(StringUtil.nullToBlank(contractNumber).isEmpty()) {
        	commonResponseOfWS.setRtnStatus(false);
        	commonResponseOfWS.setTransactionId(transactionId);
        	commonResponseOfWS.setErrorCode("ER100");
        	commonResponseOfWS.setErrorDescription("Contract Number is null.");
        	balanceInform.setCommonResponseOfWS(commonResponseOfWS);
        	log.error("RETURN:ER100[Contract Number is null.]");
        	return balanceInform;
        }

        // if transaction id is not empty transaction ID check if the transaction id is the number or not
        if(!StringUtil.nullToBlank(transactionId).isEmpty() && !StringUtil.isDigit(transactionId)) {
        	commonResponseOfWS.setRtnStatus(false);
        	commonResponseOfWS.setTransactionId(transactionId);
        	commonResponseOfWS.setErrorCode("ER110");
        	commonResponseOfWS.setErrorDescription("You must enter only numbers(0-9) in the transaction ID.");
        	balanceInform.setCommonResponseOfWS(commonResponseOfWS);
        	log.error("RETURN:ER110[You must enter only numbers(0-9) in the transaction ID.]");
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
            	log.error("RETURN:ER103[The requested Contract Info does not exist.]");
            	return balanceInform;
            }
            
            //Contract contract = (Contract)contractDao.getContractIdByContractNo(contractNumber).get(0);

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
                String chargingAvailable = map.get("chargingAvailable") == null ? "false" : map.get("chargingAvailable").toString(); 
                if("1".equals(chargingAvailable) || "true".equals(chargingAvailable)) {
                	chargingAvailable = "true";
                } else {
                	chargingAvailable = "false";
                }
                
                if("false".equals(chargingAvailable)) {
                	commonResponseOfWS.setRtnStatus(false);
                	commonResponseOfWS.setTransactionId(transactionId);
                	commonResponseOfWS.setErrorCode("ER105");
    	        	commonResponseOfWS.setErrorDescription("Contract's Charging is not 'Allow'.");
                	balanceInform.setCommonResponseOfWS(commonResponseOfWS);
                	txManager.commit(txStatus);
                	log.error("RETURN:ER105[Contract's Charging is not 'Allow'.]");
                	return balanceInform;
                }
                
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
            	log.error("RETURN:ER103[The requested Contract Info does not exist.]");
            	return balanceInform;
            }

            txManager.commit(txStatus);

            return balanceInform;
        } catch (Exception e) {

            if (txManager != null) {
                txManager.rollback(txStatus);
            }

            //e.printStackTrace();
            log.error(e,e);
            
        	commonResponseOfWS.setRtnStatus(false);
        	commonResponseOfWS.setTransactionId(transactionId);
        	commonResponseOfWS.setErrorCode("EXCEPTION");
        	commonResponseOfWS.setErrorDescription(e.getMessage());
        	balanceInform.setCommonResponseOfWS(commonResponseOfWS);
        	
            return balanceInform;
        }
    }    
    
    /**
     * method name : verify<br/>
     * method Desc : 고객 인증
     *
     * @param supplierName     Utility ID - 공급사 아이디
     * @param customerNumber   Customer Number - 고객번호
     * @param transactionId    Transaction ID - 처리 아이디
     *   
     * @return
     * @throws Exception
     */
    @Transactional(value = "transactionManager", propagation=Propagation.REQUIRES_NEW)
    public @WebResult(name="response") CustomerInformation verify(
    		@WebParam(name="supplierName") String supplierName, 
            @WebParam(name="customerNumber") String customerNumber,
            @WebParam(name="transactionId") String transactionId) throws Exception {

        Contract contract = null;
        StringBuilder sb = new StringBuilder();
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        CustomerInformation customerInformation = new CustomerInformation();
        CommonResponseOfWS commonResponseOfWS = new CommonResponseOfWS();
        sb.append("\nRequest data : supplier Name[" + supplierName + "]");
        sb.append("\nRequest data : customer Number[" + customerNumber + "]");
        sb.append("\nRequest data : transaction ID[" + transactionId + "]");

        log.info(sb.toString());

        // mandatory data check
        if(StringUtil.nullToBlank(supplierName).isEmpty()) {
        	commonResponseOfWS.setRtnStatus(false);
        	commonResponseOfWS.setTransactionId(transactionId);
        	commonResponseOfWS.setErrorCode("ER100");
        	commonResponseOfWS.setErrorDescription("Supplier Name is null.");
        	customerInformation.setCommonResponseOfWS(commonResponseOfWS);
        	log.error("RETURN:ER100[Supplier Name is null]");
        	return customerInformation;
        }else if(StringUtil.nullToBlank(customerNumber).isEmpty()) {
        	commonResponseOfWS.setRtnStatus(false);
        	commonResponseOfWS.setTransactionId(transactionId);
        	commonResponseOfWS.setErrorCode("ER100");
        	commonResponseOfWS.setErrorDescription("Customer Number is null.");
        	customerInformation.setCommonResponseOfWS(commonResponseOfWS);
        	log.error("RETURN:ER100[Customer Number is null.]");
        	return customerInformation;
        }

        // if transaction id is not empty transaction ID check if the transaction id is the number or not
        if(!StringUtil.nullToBlank(transactionId).isEmpty() && !StringUtil.isDigit(transactionId)) {
        	commonResponseOfWS.setRtnStatus(false);
        	commonResponseOfWS.setTransactionId(transactionId);
        	commonResponseOfWS.setErrorCode("ER110");
        	commonResponseOfWS.setErrorDescription("You must enter only numbers(0-9) in the transaction ID.");
        	customerInformation.setCommonResponseOfWS(commonResponseOfWS);
        	log.error("RETURN:ER110[You must enter only numbers(0-9) in the transaction ID.]");
        	return customerInformation;
        }

        try {
        	List<Contract> list = contractDao.getContractIdByCustomerNo(customerNumber, supplierName);

        	if(list.size() == 0) {
            	commonResponseOfWS.setRtnStatus(false);
            	commonResponseOfWS.setTransactionId(transactionId);
            	commonResponseOfWS.setErrorCode("ER103");
            	commonResponseOfWS.setErrorDescription("The requested Contract Info does not exist.");
            	customerInformation.setCommonResponseOfWS(commonResponseOfWS);
            	log.error("RETURN:ER103[The requested Contract Info does not exist.]");
            	return customerInformation;
        	}

        	// 고객 이름 취득
        	String customerName = list.get(0).getCustomer().getName();
 
        	List<String> contractList = new ArrayList<String>();
        	for(Object obj : list) {
        		String contractNumber = ((Contract)obj).getContractNumber();

                // 계약 정보 존재 체크
                conditionMap.put("contractNumber", contractNumber);
                conditionMap.put("supplierName", supplierName);

                // select Contract
                List<Contract> listCont = contractDao.getPrepaymentContract(conditionMap);

                if (listCont != null && listCont.size() > 0) { // 계약 정보 유
                    contract = listCont.get(0);
                    Code creditType = codeDao.get(contract.getCreditTypeCodeId());
                    Code status = codeDao.get(contract.getSicCodeId());
                    
                    // 후불 고객인지 체크
                    if(Code.POSTPAY.equals(creditType.getCode())) {
                    	commonResponseOfWS.setRtnStatus(false);
                    	commonResponseOfWS.setTransactionId(transactionId);
                    	commonResponseOfWS.setErrorCode("ER108");
                    	commonResponseOfWS.setErrorDescription("This Customer doesn't have a prepayment contract.");
                    	customerInformation.setCommonResponseOfWS(commonResponseOfWS);
                    	log.info("CONTINUE:ER108[This Customer doesn't have a prepayment contract.]");
                    	continue;
                    	//return customerInformation;
                    }
                    log.info("creditType complete");
                    
                    // 해지된 고객인지 체크
                    if("2.1.3".equals(status.getCode())) {
                    	commonResponseOfWS.setRtnStatus(false);
                    	commonResponseOfWS.setTransactionId(transactionId);
                    	commonResponseOfWS.setErrorCode("ER107");
                    	commonResponseOfWS.setErrorDescription("ContractNumber : [" + contractNumber + "] " + "This Customer has a terminated contract.");
                    	customerInformation.setCustomerNumber(customerNumber);
                    	customerInformation.setCommonResponseOfWS(commonResponseOfWS);
                    	log.info("CONTINUE:ER107[ContractNumber : [" + contractNumber + "] " + "This Customer has a terminated contract.]");
                    	continue;
                    	//return customerInformation;
                    }
                    log.info("status complete");
                    // 웹서비스로그 저장
                    VerifyPrepaymentCustomerWS verifyPrepaymentCustomerWS = new VerifyPrepaymentCustomerWS();
                    verifyPrepaymentCustomerWS.setSupplierName(supplierName);
                    verifyPrepaymentCustomerWS.setCustomerNumber(customerNumber);
                    verifyPrepaymentCustomerWS.setTransactionId(transactionId);
                    verifyPrepaymentCustomerWS.setWriteDate(TimeUtil.getCurrentTimeMilli());
                    verifyPrepaymentCustomerWSDao.add(verifyPrepaymentCustomerWS);

                    log.info("\nReturn Data: contract Number : [" + contractNumber + "]");
                    contractList.add(contractNumber);

                } else {// 계약 정보 무
                	commonResponseOfWS.setRtnStatus(false);
                	commonResponseOfWS.setTransactionId(transactionId);
                	commonResponseOfWS.setErrorCode("ER103");
                	commonResponseOfWS.setErrorDescription("The requested Contract Info does not exist.");
                	customerInformation.setCommonResponseOfWS(commonResponseOfWS);
                	log.info("RETURN:ER103[The requested Contract Info does not exist.]");
                	return customerInformation;
                }
        	}
 
        	if(contractList.size() !=0 ) {
        		commonResponseOfWS.setRtnStatus(true);
        		commonResponseOfWS.setTransactionId(transactionId);
            	commonResponseOfWS.setErrorCode("");
            	commonResponseOfWS.setErrorDescription("");
            	customerInformation.setCommonResponseOfWS(commonResponseOfWS);
            	customerInformation.setCustomerName(customerName);
            	customerInformation.setContractNumber(contractList.get(0));
            	log.info("\nReturn Data: customer Name : [" + customerName + "]");
        	}
 
        	return customerInformation;

        } catch (Exception e) {
            log.error(e);
        	commonResponseOfWS.setRtnStatus(false);
        	commonResponseOfWS.setTransactionId(transactionId);
        	commonResponseOfWS.setErrorCode("EXCEPTION");
        	commonResponseOfWS.setErrorDescription(e.getMessage());
        	customerInformation.setCommonResponseOfWS(commonResponseOfWS);
        	return customerInformation;
        }
    }
    
    /**
     * method name : getVendorDetail<br/>
     * method Desc : POS용 고객 인증
     *
     * @param terminalId    Cashier ID
     * @param pinNo   		Cashier Login Password
     *   
     * @return
     * @throws Exception
     */
    public @WebResult(name="response") VendorInformation getVendorDetail(
    		@WebParam(name="terminalId") String terminalId, 
            @WebParam(name="pinNo") String pinNo) throws Exception {

        StringBuilder sb = new StringBuilder();
        VendorInformation vendorInformation = new VendorInformation();
        CommonResponseOfWS commonResponseOfWS = new CommonResponseOfWS();
        sb.append("\nRequest data : Terminal ID [" + terminalId + "]");
        sb.append("\nRequest data : PIN No [" + pinNo + "]");

        log.info(sb.toString());

        // 1. 필수값 체크(mandatory data check)
        if(StringUtil.nullToBlank(terminalId).isEmpty()) {
        	vendorInformation = vendorInfoErrorData(false,"ER100","MSISDN/Terminal ID is null");
        	return vendorInformation;
        }

        JpaTransactionManager txManager = null;
        TransactionStatus txStatus = null;
        String errorMessage = "NotError";
        try {
	        txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");
	        txStatus = txManager.getTransaction(null);

        	//2. 유효한 CasherId 인지 체크
        	if(!vendorCasherDao.isVaildCasherForSA(terminalId)) {
        		txManager.commit(txStatus);
        		vendorInformation = vendorInfoErrorData(false,"ER103","MSISDN/Terminal ID does not exist");
            	return vendorInformation;
        	}
        	
        	
        	AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        	String hashedPw = instance.hashPassword(pinNo, terminalId);
        	
        	Map<String, Object> condition = new HashMap<String, Object>();
        	condition.put("casherId", terminalId.trim());
        	condition.put("password", hashedPw.trim());
        	List<VendorCasher> list = vendorCasherDao.getCasher(condition);

        	//3. 비밀번호가 틀린경우
        	if(list == null || list.size() == 0) {
        		txManager.commit(txStatus);
        		vendorInformation = vendorInfoErrorData(false,"ER102","Incorrect Cashier ID or Pin");
            	return vendorInformation;
        	}
        	
        	//4. cahserId와 password가 일치하는 cashier가 여러개 발견된 경우.
        	if(list.size() > 1) {
        		txManager.commit(txStatus);
        		vendorInformation = vendorInfoErrorData(false,"ER104","CashierId is duplicate");
            	return vendorInformation;
        	}

        	VendorCasher cashier = list.get(0);
        	Operator vendor = operatorDao.get(cashier.getVendorId());
        	if(vendor != null) {
        		commonResponseOfWS.setRtnStatus(true);
            	commonResponseOfWS.setErrorCode("");
            	commonResponseOfWS.setErrorDescription("");
            	vendorInformation.setCommonResponseOfWS(commonResponseOfWS);
	        	vendorInformation.setAccount(vendor.getLoginId());
	        	vendorInformation.setVendorName(vendor.getName());
	        	vendorInformation.setAvailableBalance(vendor.getDeposit());
	        	vendorInformation.setCashierName(cashier.getName());
	        	txManager.commit(txStatus);	        	
        	} else {
        		txManager.commit(txStatus);
        		vendorInformation = vendorInfoErrorData(false,"ER103","Vendor dose not exist");
            	return vendorInformation;
        	}
        	log.info("\nReturn Data: Account : [" + vendor.getLoginId() + "]");
        	log.info("\nReturn Data: VendorName : [" + vendor.getName() + "]");
        	log.info("\nReturn Data: AvailableBalance : [" + vendor.getDeposit() + "]");
        	log.info("\nReturn Data: CashierName : [" + cashier.getName() + "]");

        	return vendorInformation;
        	
        } catch (Exception e) {
        	log.error(e,e);
        	errorMessage = e.getMessage();
            if (txManager != null) {
                try {
                    txManager.rollback(txStatus);
                } catch (Exception e1) {}
            }
            
            if(!"NotError".equals(errorMessage)) {
            	vendorInformation = new VendorInformation();
                commonResponseOfWS = new CommonResponseOfWS();
            	commonResponseOfWS.setRtnStatus(false);
            	commonResponseOfWS.setErrorCode("EXCEPTION");
            	commonResponseOfWS.setErrorDescription(errorMessage);
            	vendorInformation.setCommonResponseOfWS(commonResponseOfWS);
            }
        	return vendorInformation;
        }
    }
    
    /**
     * method name : getVendorMakePayment<br/>
     * method Desc : POS용 충전
     *  
     * @param customerAccount       Customer prepaid contract nr
     * @param customerBarcode       Barcode linked to customer
     * @param amountPayable   		Amount to buy for electricty
     * @param cashierId   		    Cashier ID
     * @param vendorAccount   		Vendor ID
     * 
     * @return
     * @throws Exception
     */
    public @WebResult(name="response") VendorPaymentInformation getVendorMakePayment(
    		@WebParam(name="customerAccount") String customerAccount, 
            @WebParam(name="customerBarcode") String customerBarcode,
            @WebParam(name="amountPayale") Double amountPayable,
            @WebParam(name="cashierId") String cashierId,
            @WebParam(name="vendorAccount") String vendorAccount) throws Exception {
        Contract contract = null;
        Customer customer = null;
        Meter meter = null;
        Location location = null;
        TariffType tariffType = null;
        Supplier supplier = null;
        List<VendorCasher> vendorCasher = null;
        StringBuilder sb = new StringBuilder();
        VendorPaymentInformation vendorPaymentInformation = new VendorPaymentInformation();
        CommonResponseOfWS commonResponseOfWS = new CommonResponseOfWS();
        
        sb.append("\nRequest data : Customer Account [" + customerAccount + "]");
        sb.append("\nRequest data : Customer Barcode [" + customerBarcode + "]");
        sb.append("\nRequest data : Amount Payale 	 [" + amountPayable + "]");
        sb.append("\nRequest data : Cashier ID 	     [" + cashierId + "]");
        sb.append("\nRequest data : Vendor Account 	 [" + vendorAccount + "]");

        log.info(sb.toString());

        // 1. 필수값 체크(mandatory data check)
        if(StringUtil.nullToBlank(customerAccount).isEmpty() && StringUtil.nullToBlank(customerBarcode).isEmpty()) {
        	vendorPaymentInformation = vendorPaymentInfoErrorData(false, "ER100", "Customer Account and Customer Barcode are null");
        	return vendorPaymentInformation;
        } else if(StringUtil.nullToBlank(amountPayable).isEmpty()) {
        	vendorPaymentInformation = vendorPaymentInfoErrorData(false, "ER100", "Amount Payale ID is null");
        	return vendorPaymentInformation;
        } else if(StringUtil.nullToBlank(cashierId).isEmpty()) {
        	vendorPaymentInformation = vendorPaymentInfoErrorData(false, "ER100", "Cashier ID is null");
        	return vendorPaymentInformation;
        } else if(StringUtil.nullToBlank(vendorAccount).isEmpty()) {
        	vendorPaymentInformation = vendorPaymentInfoErrorData(false, "ER100", "Vendor Account is null");
        	return vendorPaymentInformation;
        }

        JpaTransactionManager txManager = null;
        TransactionStatus txStatus = null;
        try{
	        txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");
	        txStatus = txManager.getTransaction(null);
        	Map<String,Object> condition = new HashMap<String, Object>();
        	condition.put("contractNo", customerAccount.trim());
        	condition.put("barcode", customerBarcode.trim());
        	List<Contract> contractList = contractDao.getContractForSAWSPOS(condition);

        	//2. 바코드 or 계약번호를 통해 계약 유효성 체크
        	if(contractList == null || contractList.size() < 1) {
	        	txManager.commit(txStatus);
	        	vendorPaymentInformation = vendorPaymentInfoErrorData(false, "ER103", "Customer account does not exist");
            	return vendorPaymentInformation;
        	}
        	
        	//3. 바코드 or 계약번호가 중복으로 들어갈 경우를 위해 바코드 체크
        	if(contractList.size() > 1) {
	        	txManager.commit(txStatus);
	        	vendorPaymentInformation = vendorPaymentInfoErrorData(false, "ER104", "Customer account or barcode are duplicate");
            	return vendorPaymentInformation;
        	}

        	contract = contractList.get(0);
        	customer = customerDao.get(contract.getCustomerId());
        	meter = meterDao.get(contract.getMeterId());
        	supplier = supplierDao.get(contract.getSupplierId());
        	location = locationDao.get(contract.getLocationId());
        	tariffType = tariffTypeDao.get(contract.getTariffIndexId());
        	if(customer == null) {
	        	txManager.commit(txStatus);
	        	vendorPaymentInformation = vendorPaymentInfoErrorData(false, "ER103", "Customer account does not exist");
            	return vendorPaymentInformation;
        	}
        	
        	//Emergency credit이거나 prepay 고객인지 체크
        	Code creditType = contract.getCreditType();
        	if(creditType == null || !(Code.EMERGENCY_CREDIT.equals(creditType.getCode()) ||  Code.PREPAYMENT.equals(creditType.getCode()))) {
	        	txManager.commit(txStatus);
	        	vendorPaymentInformation = vendorPaymentInfoErrorData(false, "ER105", "Contract's credit type is not 'Emergency type' or 'Prepay type'.");
            	return vendorPaymentInformation;
        	}
        	
        	//계약의 공급 상태가 Temporary Pause 이거나 Normal 인지 체크
        	Code status = contract.getStatus();
        	if(status == null || !(ContractStatus.NORMAL.getCode().equals(status.getCode()) || ContractStatus.PAUSE.getCode().equals(status.getCode()))) {
	        	txManager.commit(txStatus);
	        	vendorPaymentInformation = vendorPaymentInfoErrorData(false, "ER105", "Contract's supply status is not 'Normal' or 'Temporary pause'.");
            	return vendorPaymentInformation;
        	}
        	
        	//Charging이 Allowed 인지 체크 필요
        	Boolean chargeAvailable = contract.getChargeAvailable();
        	if(chargeAvailable == null || chargeAvailable == false) {
	        	txManager.commit(txStatus);
	        	vendorPaymentInformation = vendorPaymentInfoErrorData(false, "ER105", "Contract's Charging is not 'Allow'.");
            	return vendorPaymentInformation;
        	}

        	Map<String,Object> conditionMap = new HashMap<String, Object>();
        	if(cashierId != null) {
        		cashierId = cashierId.trim();
        	}
        	if(vendorAccount != null) {
        		vendorAccount = vendorAccount.trim();
        	}
        	conditionMap.put("casherId", cashierId);
        	conditionMap.put("vendorAccount", vendorAccount);
        	vendorCasher = vendorCasherDao.getCasherByName(conditionMap);

        	if(vendorCasher.size() > 1) {
	        	txManager.commit(txStatus);
	        	vendorPaymentInformation = vendorPaymentInfoErrorData(false, "ER104", "Cashier ID is duplicate");
            	return vendorPaymentInformation;
        	}
        	
        	if(vendorCasher.size() == 0) {
	        	txManager.commit(txStatus);
	        	vendorPaymentInformation = vendorPaymentInfoErrorData(false, "ER103", "Cashier does not exist");
            	return vendorPaymentInformation;
        	}

        	txManager.commit(txStatus);
        } catch(Exception e) {
        	log.error(e,e);
        	txManager.commit(txStatus);
        	commonResponseOfWS.setRtnStatus(false);
        	commonResponseOfWS.setErrorCode("EXCEPTION");
        	commonResponseOfWS.setErrorDescription(e.getMessage());
        	vendorPaymentInformation.setCommonResponseOfWS(commonResponseOfWS);
        	return vendorPaymentInformation;
        }

        String errorMessage = "NotError";
        try {
	        txStatus = txManager.getTransaction(null);
        	Operator vendor = operatorDao.get(vendorCasher.get(0).getVendorId());
        	
        	if(vendor.getDeposit() == null || vendor.getDeposit() <= amountPayable) {
        		txManager.commit(txStatus);
	        	vendorPaymentInformation = vendorPaymentInfoErrorData(false, "ER105", "Insufficient Quota Balance");
            	return vendorPaymentInformation;
        	}
        	
        	String lastTokenDate = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");
        	Integer daysFromCharge = 0;
            String lastChargeDate = StringUtil.nullToBlank(contract.getLastTokenDate());
            if (lastChargeDate != null && !lastChargeDate.equals("")) {
                daysFromCharge = TimeUtil.getDayDuration(lastChargeDate, lastTokenDate);
            } else {
                daysFromCharge = 0;
            }
            
            // 미수긂이 있으면 비율에 따라 납부한 후에 잔액을 충전한다.
            Double arrearsRate = getArrearsRate(contract, lastTokenDate.substring(0, 8));
            Double chargedArrears = 0.0;
            Double currentArrears = 0.0;
            Double chargedCredit = amountPayable;
            if (contract.getCurrentArrears() != null && contract.getCurrentArrears() > 0) {
                currentArrears = contract.getCurrentArrears();
                chargedArrears = amountPayable * arrearsRate / 100.0;

                // 현재 미수금보다 납부액이 크면 현재 미수금으로 대체한다.
                if (chargedArrears > currentArrears)
                    chargedArrears = currentArrears;

                currentArrears -= chargedArrears;
                chargedCredit = amountPayable - chargedArrears;
            }

        	Double currentCredit = Double.parseDouble(StringUtil.nullToZero(contract.getCurrentCredit()))
        			+ Double.parseDouble(StringUtil.nullToZero(chargedCredit));
        	Integer lastChargeCnt = new Integer(StringUtil.nullToZero(contract.getLastChargeCnt())) + 1;

            // insert ContractChangeLog
            addContractChangeLog(contract, "lastTokenDate", contract.getLastTokenDate(), lastTokenDate);
            addContractChangeLog(contract, "chargedCredit", contract.getChargedCredit(), amountPayable.toString());
            addContractChangeLog(contract, "currentCredit", contract.getCurrentCredit(), currentCredit.toString());
            addContractChangeLog(contract, "lastChargeCnt", contract.getLastChargeCnt(), lastChargeCnt.toString());

            if (contract.getCurrentArrears() != null && contract.getCurrentArrears() > 0 && chargedArrears > 0)
                addContractChangeLog(contract, "currentArrears", contract.getCurrentArrears(), currentArrears.toString());

            log.debug("Complete add ContractChangeLog");

            //update Contract
        	contract.setLastTokenDate(lastTokenDate);
        	contract.setCurrentCredit(currentCredit);
        	contract.setChargedCredit(amountPayable);
        	contract.setLastChargeCnt(lastChargeCnt);
            contract.setCurrentArrears(currentArrears);
        	contractDao.update(contract);
        	log.debug("Complete update Contract");

            //update operator
        	vendor.setDeposit(vendor.getDeposit()-amountPayable);
        	operatorDao.update(vendor);
            log.debug("Complete update Operator");
            
            // 웹서비스로그 저장
            AddBalanceWSCharging addBalanceCharging = new AddBalanceWSCharging();

            addBalanceCharging.setSupplierName(supplier.getName());
            addBalanceCharging.setDateTime(lastTokenDate);
            addBalanceCharging.setContractNumber(contract.getContractNumber());
            addBalanceCharging.setMdsId(meter.getMdsId());
            addBalanceCharging.setAccountId(contract.getContractNumber());
            addBalanceCharging.setAmount(amountPayable);
            addBalanceCharging.setPowerLimit(contract.getContractDemand());

            addBalanceWSChargingDao.add(addBalanceCharging);

            // insert PrepaymentLog
            PrepaymentLog prepaymentLog = new PrepaymentLog();

            prepaymentLog.setCustomer(customer);
            prepaymentLog.setContract(contract);
            prepaymentLog.setChargedCredit(chargedCredit);
            prepaymentLog.setChargedArrears(chargedArrears);
            prepaymentLog.setLastTokenDate(lastTokenDate);
            prepaymentLog.setOperator(vendor);
            Integer emergencyYn = null;
            if (contract.getEmergencyCreditAvailable() != null) {
                emergencyYn = (contract.getEmergencyCreditAvailable()) ? 1 : 0;
            }
            prepaymentLog.setEmergencyCreditAvailable(emergencyYn);
            prepaymentLog.setBalance(currentCredit);
            prepaymentLog.setLocation(location);
            prepaymentLog.setTariffIndex(tariffType);
            prepaymentLog.setVendorCasher(vendorCasher.get(0));
            prepaymentLog.setDaysFromCharge(daysFromCharge);
            prepaymentLog.setLastTokenId(String.valueOf(prepaymentLogDao.getNextVal()));
            prepaymentLogDao.add(prepaymentLog);
            
            //insert deposit
            DepositHistory dh = new DepositHistory();
            dh.setOperator(vendor);
            dh.setContract(contract);
            dh.setCustomer(customer);
            dh.setMeter(meter);
            dh.setChangeDate(lastTokenDate);
            dh.setChargeCredit(amountPayable);
            dh.setDeposit(vendor.getDeposit());
            dh.setPrepaymentLog(prepaymentLog);

            depositHistoryDao.add(dh);
        	txManager.commit(txStatus);

            log.debug("Complete add PrepaymentLog");

            Map<String, Object> smsInfo = getSMSInformation(contract, amountPayable, currentCredit, false);
            if(smsInfo != null) {
            	Map<String, Object> smsParams = new HashMap<String, Object>();
	            smsParams.put("command", "sendSMS");
	            smsParams.put("smsMsg", smsInfo.get("smsMsg"));
	            smsParams.put("smsYn", true);
	            smsParams.put("mobileNo", smsInfo.get("MOBILENO"));
	            smsParams.put("contractId", contract.getId());
	            activemqProcess(smsParams);
            }

            relayOn(currentCredit, contract.getContractNumber(), meter.getMdsId(), amountPayable);

    		commonResponseOfWS.setRtnStatus(true);
        	commonResponseOfWS.setErrorCode("");
        	commonResponseOfWS.setErrorDescription("");
        	vendorPaymentInformation.setCommonResponseOfWS(commonResponseOfWS);
        	vendorPaymentInformation.setCustomerName(customer.getName());
        	vendorPaymentInformation.setCustomerBalance(contract.getCurrentCredit());
        	vendorPaymentInformation.setTransactionNumber(prepaymentLog.getId());
        	vendorPaymentInformation.setTransactionTime(lastTokenDate);
        	vendorPaymentInformation.setAvailableBalance(vendor.getDeposit());
        	log.info("\nReturn Data: Customer Name 		: [" + customer.getName() + "]");
        	log.info("\nReturn Data: CustomerBalance 	: [" + contract.getCurrentCredit() + "]");
        	log.info("\nReturn Data: TransactionNumber  : [" + prepaymentLog.getId() + "]");
        	log.info("\nReturn Data: TransactionTime    : [" + lastTokenDate + "]");
        	log.info("\nReturn Data: AvailableBalance   : [" + vendor.getDeposit() + "]");

        } catch (Exception e) {
        	log.error(e,e);
        	errorMessage = e.getMessage();
        	log.error(e);
            if (txManager != null) {
                try {
                    txManager.rollback(txStatus);
                } catch (Exception e1) {}
            }
        }

        if(!"NotError".equals(errorMessage)) {
            vendorPaymentInformation = new VendorPaymentInformation();
            commonResponseOfWS = new CommonResponseOfWS();
        	commonResponseOfWS.setRtnStatus(false);
        	commonResponseOfWS.setErrorCode("EXCEPTION");
        	commonResponseOfWS.setErrorDescription(errorMessage);
        	vendorPaymentInformation.setCommonResponseOfWS(commonResponseOfWS);
        }

        return vendorPaymentInformation;
    }
    
    public VendorPaymentInformation vendorPaymentInfoErrorData(Boolean status, String errorCode, String errorDescr) throws Exception {
    	VendorPaymentInformation vendorPaymentInformation = new VendorPaymentInformation();
    	CommonResponseOfWS commonResponseOfWS = new CommonResponseOfWS();
    	commonResponseOfWS.setRtnStatus(status);
    	commonResponseOfWS.setErrorCode(errorCode);
    	commonResponseOfWS.setErrorDescription(errorDescr);
    	vendorPaymentInformation.setCommonResponseOfWS(commonResponseOfWS);
    	log.error("ERROR CODE ["+errorCode+"], ERROR MESSAGE ["+errorDescr+"]");
    	return vendorPaymentInformation;
    }
    
    public VendorInformation vendorInfoErrorData(Boolean status, String errorCode, String errorDescr) throws Exception {
    	VendorInformation vendorInformation = new VendorInformation();
    	CommonResponseOfWS commonResponseOfWS = new CommonResponseOfWS();
    	commonResponseOfWS.setRtnStatus(status);
    	commonResponseOfWS.setErrorCode(errorCode);
    	commonResponseOfWS.setErrorDescription(errorDescr);
    	vendorInformation.setCommonResponseOfWS(commonResponseOfWS);
    	log.error("ERROR CODE ["+errorCode+"], ERROR MESSAGE ["+errorDescr+"]");
    	return vendorInformation;
    }
}
