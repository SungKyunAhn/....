/**
 * AddBalanceWS.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.fep.meter.prepayment;

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
import com.aimir.dao.device.EnergyMeterDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.OperationLogDao;
import com.aimir.dao.device.WaterMeterDao;
import com.aimir.dao.prepayment.AddBalanceWSChargingDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.ContractChangeLogDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.PrepaymentLogDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.dao.system.TariffTypeDao;
import com.aimir.fep.command.ws.server.CommandWS;
import com.aimir.fep.command.ws.server.ResponseMap;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.sms.SendSMS;
import com.aimir.model.device.Meter;
import com.aimir.model.device.OperationLog;
import com.aimir.model.prepayment.AddBalanceWSCharging;
import com.aimir.model.system.Code;
import com.aimir.model.system.Contract;
import com.aimir.model.system.ContractChangeLog;
import com.aimir.model.system.PrepaymentLog;
import com.aimir.model.system.Supplier;
import com.aimir.model.system.TariffType;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.DecimalUtil;
import com.aimir.util.StringUtil;

/**
 * AddBalanceWS.java Description 
 * <p>
 * <pre>
 * Date          Version     Author   Description
 * 2011. 6. 28   v1.0        김상연   선불연계 - 과금 충전 시 연계
 * 2011. 8. 12   v1.0        문동규   클래스명 수정
 * 2013. 2. 20   v1.0        문동규   PrepaymentChargeManager.addBalanceCharging() 에 동일한 충전로직이 있음.(충전화면에서 사용)
 *                                    충전로직 수정사항이 있을 경우 이 부분도 동일하게 수정해야 함.
 * </pre>
 */

@WebService(serviceName="AddBalanceWS")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
@Service
public class AddBalanceWS {
	
    protected static Log log = LogFactory.getLog(AddBalanceWS.class);
    
    @Autowired
    protected MeterDao meterDao;

    @Autowired
    protected EnergyMeterDao energyMeterDao;

    @Autowired
    protected WaterMeterDao waterMeterDao;

    @Autowired
    protected ContractDao contractDao;

    @Autowired
    protected ContractChangeLogDao contractChangeLogDao;

    @Autowired
    protected SupplierDao supplierDao;
    
    @Autowired
    protected TariffTypeDao tariffTypeDao;
    
    @Autowired
    protected AddBalanceWSChargingDao addBalanceWSChargingDao;
    
    @Autowired
    protected PrepaymentLogDao prepaymentLogDao;

    @Autowired
    protected CodeDao codeDao;
    
    @Autowired
    protected OperationLogDao operationLogDao;
    
    @Autowired
    protected CommandWS gw;

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
     * @return
     * @throws Exception
     */
    @WebMethod
    public @WebResult(name="response") String charging(
    		@WebParam(name="supplierName") String supplierName, 
            @WebParam(name="dateTime") String dateTime, 
            @WebParam(name="contractNumber") String contractNumber,
            @WebParam(name="mdsId") String mdsId, 
            @WebParam(name="accountId") String accountId,
            @WebParam(name="amount") Double amount, 
            @WebParam(name="powerLimit") Double powerLimit, 
            @WebParam(name="tariffCode") Integer tariffCode, 
            @WebParam(name="source") String source, 
            @WebParam(name="encryptionKey") String encryptionKey) throws Exception {
    	
    	String rtnStr = "";
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

        log.info(sb.toString());

        // mandatory data check
        if (StringUtil.nullToBlank(supplierName).isEmpty() || StringUtil.nullToBlank(dateTime).isEmpty() || 
                StringUtil.nullToBlank(contractNumber).isEmpty() || StringUtil.nullToBlank(mdsId).isEmpty() ||
                StringUtil.nullToBlank(accountId).isEmpty() || StringUtil.nullToBlank(amount).isEmpty() ||
                StringUtil.nullToBlank(powerLimit).isEmpty() || StringUtil.nullToBlank(tariffCode).isEmpty()) {
            return "fail : mandatory data is required";
        }

        JpaTransactionManager txManager = null;
        TransactionStatus txStatus = null;

        try {
            txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");
            txStatus = txManager.getTransaction(null);

            // 웹서비스로그 저장
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

            addBalanceWSChargingDao.add(addBalanceWSCharging);

            Meter meter = meterDao.get(mdsId);

            Contract contract = null;
            Map<String, Object> conditionMap = new HashMap<String, Object>();

            conditionMap.put("contractNumber", contractNumber);
            conditionMap.put("supplierName", supplierName);
            conditionMap.put("mdsId", mdsId);

            if (!StringUtil.nullToBlank(encryptionKey).isEmpty()) {
                conditionMap.put("keyNum", encryptionKey);
            }

            // select Contract
            List<Contract> listCont = contractDao.getPrepaymentContract(conditionMap);

            if (listCont != null && listCont.size() > 0) {
                contract = listCont.get(0);
            } else {
                txManager.commit(txStatus);
                return "fail : invalid contract info";
            }
            
            boolean isCuffOff = false;    // 차단여부

            Double currentCredit = new Double(StringUtil.nullToZero(contract.getCurrentCredit())) + amount;
            
            if(currentCredit > 0) {
	            // CommandOperationUtil 생성 후 개발 부분 Start
	            if (meter.getPrepaymentMeter() != null && meter.getPrepaymentMeter()) { // Prepayment Meter 인 경우 온라인 충전
	
	            	//
	            } else { // Soft Credit 인 경우 충전
	                ResultStatus status = ResultStatus.FAIL;
	                String modelName = meter.getModel().getName();
	                
	
	                // 차단되어 있는지 체크
	                if (meter.getMeterStatus() != null &&
	           			 (meter.getMeterStatus().getCode().equals(CommonConstants.getMeterStatusByName(MeterStatus.CutOff.name()).getCode()))) {
	                    isCuffOff = true;
	                }
	
	                if (isCuffOff) {
	                    try {
	                        status = ResultStatus.SUCCESS;
	                        
	                        ResponseMap responseMap = gw.relayValveOn(meter.getModem().getMcu().getSysID(), meter.getMdsId());
	                        Object[] values = responseMap.getResponse().values().toArray(new Object[0]);
	                        
	                        for (Object o : values) {
	                            rtnStr += (String)o + " ";
	                            
	                            if (((String)o).contains("failReason")) {
	                                status = ResultStatus.FAIL;
	                                break;
	                            }
	                        }
	                    } catch (Exception e) {
	                        rtnStr = e.getMessage();
	                    }
	                    
	                    if(status == ResultStatus.SUCCESS) {
	                    	//미터 차단 해지 메세지
	                    	
	                    	//CASE : SMS 
	                    	SMSNotification(contract, amount, currentCredit, isCuffOff);
	                    }
	                    
	                    Code operationCode = codeDao.getCodeIdByCodeObject("8.1.4");
	                    Supplier supplier = supplierDao.getSupplierByName(supplierName);
	
	                    if (operationCode != null) {
	                        String currDateTime = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");
	                        OperationLog log = new OperationLog();
	
	                        log.setOperatorType(1);//operator
	                        log.setOperationCommandCode(operationCode);
	                        log.setYyyymmdd(currDateTime.substring(0,8));
	                        log.setHhmmss(currDateTime.substring(8,14));
	                        log.setYyyymmddhhmmss(currDateTime);
	                        log.setDescription("");
	                        log.setErrorReason(status.name());
	                        log.setResultSrc("");
	                        log.setStatus(status.getCode());
	                        log.setTargetName(mdsId);
	                        log.setTargetTypeCode(meter.getMeterType());
	                        //log.setUserId(CommandOperator.WEBSERVICE.getOperatorName());
	                        log.setSupplier(supplier);
	                        operationLogDao.add(log);
	                    }
	                }
	            }
            }
            // CommandOperationUtil 생성 후 개발 부분 End

            TariffType tariffType = tariffTypeDao.findByCondition("code", tariffCode);
//            Integer chargedCredit = contract.getChargedCredit() + amount;
            Integer lastChargeCnt = new Integer(StringUtil.nullToZero(contract.getLastChargeCnt())) + 1;
            Code keyCode = null;
            // insert ContractChangeLog
            addContractChangeLog(contract, "lastTokenDate", contract.getLastTokenDate(), dateTime);
            addContractChangeLog(contract, "lastTokenId", contract.getLastTokenId(), accountId);
            addContractChangeLog(contract, "contractDemand", contract.getContractDemand(), powerLimit.toString());
            addContractChangeLog(contract, "chargedCredit", contract.getChargedCredit(), amount.toString());
            addContractChangeLog(contract, "currentCredit", contract.getCurrentCredit(), currentCredit.toString());
            addContractChangeLog(contract, "lastChargeCnt", contract.getLastChargeCnt(), lastChargeCnt.toString());
            addContractChangeLog(contract, "tariffIndex", (contract.getTariffIndex() != null) ? contract.getTariffIndex().getCode() : null, (tariffType != null) ? tariffCode : null);
            addContractChangeLog(contract, "contractIndex", contract.getContractIndex(), tariffCode);

            if (!StringUtil.nullToBlank(source).isEmpty()) {
                keyCode = codeDao.getCodeIdByCodeObject(source);
                addContractChangeLog(contract, "keyType", contract.getKeyType().getCode(), keyCode.getCode());
            }

            contract.setLastTokenDate(dateTime);
            contract.setLastTokenId(accountId);
            contract.setContractDemand(powerLimit);
            contract.setChargedCredit(amount);
            contract.setCurrentCredit(currentCredit);
            contract.setLastChargeCnt(lastChargeCnt);
            contract.setTariffIndex(tariffType);
            contract.setContractIndex(tariffCode);

            if (!StringUtil.nullToBlank(source).isEmpty()) {
                contract.setKeyType(keyCode);
            }

            // update Contract
            contractDao.update(contract);

            // insert PrepaymentLog
            PrepaymentLog prepaymentLog = new PrepaymentLog();

            prepaymentLog.setCustomer(contract.getCustomer());
            prepaymentLog.setContract(contract);
            prepaymentLog.setKeyNum(encryptionKey);
            prepaymentLog.setKeyType(keyCode);
            prepaymentLog.setChargedCredit(amount);
            prepaymentLog.setLastTokenDate(dateTime);
            prepaymentLog.setLastTokenId(accountId);
            Integer emergencyYn = null;
            if (contract.getEmergencyCreditAvailable() != null) {
                emergencyYn = (contract.getEmergencyCreditAvailable()) ? 1 : 0;
            }
            prepaymentLog.setEmergencyCreditAvailable(emergencyYn);
            prepaymentLog.setPowerLimit(powerLimit);
            prepaymentLog.setBalance(currentCredit);
            prepaymentLog.setLocation(contract.getLocation());
            prepaymentLog.setTariffIndex(contract.getTariffIndex());

            prepaymentLogDao.add(prepaymentLog);
            
            // Notification 통보 프레임 워크 생성 후 개발 Start
            if(!isCuffOff) {
            	SMSNotification(contract, amount, currentCredit, isCuffOff);
            }
            // Notification 통보 프레임 워크 생성 후 개발 End

            txManager.commit(txStatus);

            return "success";
        } catch (Exception e) {

            if (txManager != null) {
                txManager.rollback(txStatus);
            }

            e.printStackTrace();

            log.error(e);

            return "fail : " + e.getMessage();
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
    
    /**
     * method name : SMSNotification
     * method Desc : Charge에 대한 SMS 통보
     * @param contract, amount, isValid
     */
    private void SMSNotification(Contract contract, Double amount, Double currentCredit, Boolean isCuffOff) {
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
				if(isCuffOff) {
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
				
				//Properties prop = new Properties();
    			//prop.load(getClass().getClassLoader().getResourceAsStream("config/fmp.properties"));
				
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
