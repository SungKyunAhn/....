/**
 * PrepaymentSetWS.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.fep.meter.prepayment;

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

import com.aimir.dao.device.MeterDao;
import com.aimir.dao.prepayment.PrepaymentSetWSChangeCreditDao;
import com.aimir.dao.prepayment.PrepaymentSetWSChangeInfoDao;
import com.aimir.dao.prepayment.PrepaymentSetWSChangeParamDao;
import com.aimir.dao.prepayment.PrepaymentSetWSRestartAccountDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.ContractChangeLogDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.PrepaymentAuthDeviceDao;
import com.aimir.dao.system.PrepaymentLogDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.PrepaymentProperty;
import com.aimir.model.prepayment.PrepaymentSetWSChangeCredit;
import com.aimir.model.prepayment.PrepaymentSetWSChangeInfo;
import com.aimir.model.prepayment.PrepaymentSetWSChangeParam;
import com.aimir.model.prepayment.PrepaymentSetWSRestartAccount;
import com.aimir.model.system.Contract;
import com.aimir.model.system.ContractChangeLog;
import com.aimir.model.system.PrepaymentAuthDevice;
import com.aimir.model.system.Supplier;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.StringUtil;

/**
 * PrepaymentSetWS.java Description
 *
 *
 * Date          Version     Author   Description
 * 2011. 7. 26.  v1.0        문동규   선불연계 - 선불 관련 환경 설정
 *
 */

@WebService(serviceName="PrepaymentSetWS")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
@Service
public class PrepaymentSetWS {

    protected static Log log = LogFactory.getLog(PrepaymentSetWS.class);

    @Autowired
    protected MeterDao meterDao;

    @Autowired
    protected ContractDao contractDao;

    @Autowired
    protected SupplierDao supplierDao;

    @Autowired
    protected ContractChangeLogDao contractChangeLogDao;

    @Autowired
    protected CodeDao codeDao;

    @Autowired
    protected PrepaymentSetWSChangeParamDao prepaymentSetWSChangeParamDao;

    @Autowired
    protected PrepaymentSetWSChangeCreditDao prepaymentSetWSChangeCreditDao;

    @Autowired
    protected PrepaymentSetWSRestartAccountDao prepaymentSetWSRestartAccountDao;

    @Autowired
    protected PrepaymentSetWSChangeInfoDao prepaymentSetWSChangeInfoDao;

    @Autowired
    protected PrepaymentLogDao prepaymentLogDao;

    @Autowired
    protected PrepaymentAuthDeviceDao prepaymentAuthDeviceDao;

    /**
     * method name : changeParam
     * method Desc : Change Emergency Credit parameter
     *
     * @param supplierName      Utility ID - 공급사 아이디
     * @param dateTime          Date & Time of request - 현재 날짜
     * @param contractNumber    Contract ID - 고객의 계약번호
     * @param mdsId             Meter Serial Number - 미터 시리얼 번호
     * @param emergencyYn       Y/N - Emergency Credit 할지 여부
     * @param emergencyAutoYn   Auto/manual selection - 자동으로 전횐되게 할 것인지 수동으로 전환되게 할 것인지
     * @param maxDuration       Maximum Emergency Credit available - Emergency Credit 모드의 최대 기간 (날짜수)
     * @param deviceId          Device ID - 인증 장비 아이디
     * @param encryptionKey     Encryption Key - 암호화 시 인증 키
     * @return
     * @throws Exception
     */
    @WebMethod
    public @WebResult(name="response") String changeParam(
            @WebParam(name="supplierName") String supplierName,
            @WebParam(name="dateTime") String dateTime,
            @WebParam(name="contractNumber") String contractNumber,
            @WebParam(name="mdsId") String mdsId,
            @WebParam(name="emergencyYn") String emergencyYn,
            @WebParam(name="emergencyAutoYn") String emergencyAutoYn,
            @WebParam(name="maxDuration") Integer maxDuration,
            @WebParam(name="deviceId") String deviceId,
            @WebParam(name="encryptionKey") String encryptionKey) throws Exception {

        StringBuilder sb = new StringBuilder();

        sb.append("\n supplierName[" + supplierName + "]");
        sb.append("\n dateTime[" + dateTime + "]");
        sb.append("\n contractNumber[" + contractNumber + "]");
        sb.append("\n mdsId[" + mdsId + "]");
        sb.append("\n emergencyYn[" + emergencyYn + "]");
        sb.append("\n emergencyAutoYn[" + emergencyAutoYn + "]");
        sb.append("\n maxDuration[" + maxDuration + "]");
        sb.append("\n deviceId[" + deviceId + "]");
        sb.append("\n encryptionKey[" + encryptionKey + "]");

        log.info(sb.toString());

        // mandatory data check
        if (StringUtil.nullToBlank(supplierName).isEmpty() || StringUtil.nullToBlank(dateTime).isEmpty() || 
                StringUtil.nullToBlank(contractNumber).isEmpty() || StringUtil.nullToBlank(mdsId).isEmpty() ||
                StringUtil.nullToBlank(emergencyYn).isEmpty() || StringUtil.nullToBlank(emergencyAutoYn).isEmpty() ||
                StringUtil.nullToBlank(encryptionKey).isEmpty()) {
            return "fail : mandatory data is required";
        }

        JpaTransactionManager txManager = null;
        TransactionStatus txStatus = null;

        try {

            txManager = (JpaTransactionManager)DataUtil.getBean("transactionManager");
            txStatus = txManager.getTransaction(null);

            // 웹서비스로그 저장
            PrepaymentSetWSChangeParam prepaymentSetWSChangeParam = new PrepaymentSetWSChangeParam();

            prepaymentSetWSChangeParam.setSupplierName(supplierName);
            prepaymentSetWSChangeParam.setDateTime(dateTime);
            prepaymentSetWSChangeParam.setContractNumber(contractNumber);
            prepaymentSetWSChangeParam.setMdsId(mdsId);
            prepaymentSetWSChangeParam.setEmergencyYn(emergencyYn);
            prepaymentSetWSChangeParam.setEmergencyAutoYn(emergencyAutoYn);
            prepaymentSetWSChangeParam.setMaxDuration(maxDuration);
            prepaymentSetWSChangeParam.setDeviceId(deviceId);
            prepaymentSetWSChangeParam.setEncryptionKey(encryptionKey);
            
            prepaymentSetWSChangeParamDao.add(prepaymentSetWSChangeParam);

            Contract contract = null;
            Map<String, Object> conditionMap = new HashMap<String, Object>();

            conditionMap.put("contractNumber", contractNumber);
            conditionMap.put("supplierName", supplierName);
            conditionMap.put("mdsId", mdsId);
            conditionMap.put("keyNum", encryptionKey);

            List<Contract> listCont = contractDao.getPrepaymentContract(conditionMap);

            if (listCont != null && listCont.size() > 0) {
                contract = listCont.get(0);
            } else {
                txManager.commit(txStatus);
                return "fail : invalid contract info";
            }

            boolean convEmergencyYn = conversionEmergencyCreditYn(emergencyYn);
            boolean convEmergencyAutoYn = conversionEmergencyAutoYn(emergencyAutoYn);

            // insert ContractChangeLog
            addContractChangeLog(contract, "emergencyCreditAvailable", contract.getEmergencyCreditAvailable(), convEmergencyYn);
            addContractChangeLog(contract, "emergencyCreditAutoChange", contract.getEmergencyCreditAutoChange(), convEmergencyAutoYn);
            
            if (!StringUtil.nullToBlank(maxDuration).isEmpty()) {
                addContractChangeLog(contract, "emergencyCreditMaxDuration", contract.getEmergencyCreditMaxDuration(), maxDuration);
            }

            // set Contract
            contract.setEmergencyCreditAvailable(convEmergencyYn);
            contract.setEmergencyCreditAutoChange(convEmergencyAutoYn);
            if (!StringUtil.nullToBlank(maxDuration).isEmpty()) {
                contract.setEmergencyCreditMaxDuration(maxDuration);
            }

            // update Contract
            contractDao.update(contract);
            
            // update PrepaymentAuthDevice - 현재 보류. 장비 인증 확정 후 개발

            // insert PrepaymentAuthDevice
            if (!StringUtil.nullToBlank(deviceId).isEmpty()) {
                PrepaymentAuthDevice prepaymentAuthDevice = new PrepaymentAuthDevice();
                
                prepaymentAuthDevice.setAuthKey(deviceId);
                prepaymentAuthDevice.setContract(contract);
                prepaymentAuthDevice.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
                
                prepaymentAuthDeviceDao.add(prepaymentAuthDevice);
            }

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
     * method name : changeCredit
     * method Desc : Change Credit available
     *
     * @param supplierName      Utility ID - 공급사 아이디
     * @param dateTime          Date & Time of request - 현재 날짜
     * @param contractNumber    Contract ID - 고객의 계약번호
     * @param mdsId             Meter Serial Number - 미터 시리얼 번호
     * @param changeCreditYn    Y/N - Change credit yes/no
     * @param incrementYn       Absolute/Incremental - 현재 금액에서 증가할 것인지 아니면 아예 값을 변경할 것인지
     * @param credit            Value - 금액
     * @param encryptionKey     Encryption Key - 암호화 시 인증 키
     * @return
     * @throws Exception
     */
    @WebMethod
    public @WebResult(name="response") String changeCredit(
            @WebParam(name="supplierName") String supplierName,
            @WebParam(name="dateTime") String dateTime,
            @WebParam(name="contractNumber") String contractNumber,
            @WebParam(name="mdsId") String mdsId,
            @WebParam(name="changeCreditYn") String changeCreditYn,
            @WebParam(name="incrementYn") String incrementYn,
            @WebParam(name="credit") Double credit,
            @WebParam(name="encryptionKey") String encryptionKey) throws Exception {

        StringBuilder sb = new StringBuilder();

        sb.append("\n supplierName[" + supplierName + "]");
        sb.append("\n dateTime[" + dateTime + "]");
        sb.append("\n contractNumber[" + contractNumber + "]");
        sb.append("\n mdsId[" + mdsId + "]");
        sb.append("\n changeCreditYn[" + changeCreditYn + "]");
        sb.append("\n incrementYn[" + incrementYn + "]");
        sb.append("\n credit[" + credit + "]");
        sb.append("\n encryptionKey[" + encryptionKey + "]");

        log.info(sb.toString());

        // mandatory data check
        if (StringUtil.nullToBlank(supplierName).isEmpty() || StringUtil.nullToBlank(dateTime).isEmpty() || 
                StringUtil.nullToBlank(contractNumber).isEmpty() || StringUtil.nullToBlank(mdsId).isEmpty() ||
                StringUtil.nullToBlank(changeCreditYn).isEmpty() || StringUtil.nullToBlank(incrementYn).isEmpty() ||
                StringUtil.nullToBlank(credit).isEmpty()) {
            return "fail : mandatory data is required";
        }

        JpaTransactionManager txManager = null;
        TransactionStatus txStatus = null;

        try {

            txManager = (JpaTransactionManager)DataUtil.getBean("transactionManager");
            txStatus = txManager.getTransaction(null);

            // 웹서비스로그 저장
            PrepaymentSetWSChangeCredit prepaymentSetWSChangeCredit = new PrepaymentSetWSChangeCredit();

            prepaymentSetWSChangeCredit.setSupplierName(supplierName);
            prepaymentSetWSChangeCredit.setDateTime(dateTime);
            prepaymentSetWSChangeCredit.setContractNumber(contractNumber);
            prepaymentSetWSChangeCredit.setMdsId(mdsId);
            prepaymentSetWSChangeCredit.setChangeCreditYn(changeCreditYn);
            prepaymentSetWSChangeCredit.setIncrementYn(incrementYn);
            prepaymentSetWSChangeCredit.setCredit(credit);
            prepaymentSetWSChangeCredit.setEncryptionKey(encryptionKey);
            
            prepaymentSetWSChangeCreditDao.add(prepaymentSetWSChangeCredit);

            boolean convChangeCreditYn = conversionChangeCreditYn(changeCreditYn);
            boolean convIncrementYn = conversionIncrementCreditYn(incrementYn);

            // update 하지 않고 종료
            if (!convChangeCreditYn) {
                txManager.commit(txStatus);
                return "success";
            }

            Contract contract = null;
            Map<String, Object> conditionMap = new HashMap<String, Object>();

            conditionMap.put("contractNumber", contractNumber);
            conditionMap.put("supplierName", supplierName);
            conditionMap.put("mdsId", mdsId);
            if (!StringUtil.nullToBlank(encryptionKey).isEmpty()) {
                conditionMap.put("keyNum", encryptionKey);
            }

            List<Contract> listCont = contractDao.getPrepaymentContract(conditionMap);

            if (listCont != null && listCont.size() > 0) {
                contract = listCont.get(0);
            } else {
                txManager.commit(txStatus);
                return "fail : invalid contract info";
            }

            Double updateCredit = 0d;   // update 할 금액

            if (convIncrementYn) {
                updateCredit = contract.getCurrentCredit() + credit;
            } else {
                updateCredit = credit;
            }

            // Insert ContractChangeLog
            addContractChangeLog(contract, "currentCredit", contract.getCurrentCredit(), updateCredit);

            // Update Contract
            contract.setCurrentCredit(updateCredit);

            contractDao.update(contract);

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
     * method name : restartAccount
     * method Desc : Restart Accounting
     *
     * @param supplierName      Utility ID - 공급사 아이디
     * @param dateTime          Date & Time of request - 현재 날짜
     * @param contractNumber    Contract ID - 고객의 계약번호
     * @param mdsId             Meter Serial Number - 미터 시리얼 번호
     * @param restartYn         Y/N - 계정을 다시 시작할지 여부
     * @param zeroEmergencyYn   Zero credit & Em Cr Y/N - 잔액을 0으로 하고 Emergency Credit 여부 설정
     * @param rebillingYn       Restart Billing Y/N - 과금 재시작
     * @param zeroCreditYn      Zero Debts Y/N - 잔액을 0으로 함
     * @param encryptionKey     Encryption Key - 암호화 시 인증 키
     * @return
     * @throws Exception
     */
    @WebMethod
    public @WebResult(name="response") String restartAccount(
            @WebParam(name="supplierName") String supplierName,
            @WebParam(name="dateTime") String dateTime,
            @WebParam(name="contractNumber") String contractNumber,
            @WebParam(name="mdsId") String mdsId,
            @WebParam(name="restartYn") String restartYn,
            @WebParam(name="zeroEmergencyYn") String zeroEmergencyYn,
            @WebParam(name="rebillingYn") String rebillingYn,
            @WebParam(name="zeroCreditYn") String zeroCreditYn,
            @WebParam(name="encryptionKey") String encryptionKey) throws Exception {

        StringBuilder sb = new StringBuilder();

        sb.append("\n supplierName[" + supplierName + "]");
        sb.append("\n dateTime[" + dateTime + "]");
        sb.append("\n contractNumber[" + contractNumber + "]");
        sb.append("\n mdsId[" + mdsId + "]");
        sb.append("\n restartYn[" + restartYn + "]");
        sb.append("\n zeroEmergencyYn[" + zeroEmergencyYn + "]");
        sb.append("\n rebillingYn[" + rebillingYn + "]");
        sb.append("\n zeroCreditYn[" + zeroCreditYn + "]");
        sb.append("\n encryptionKey[" + encryptionKey + "]");

        log.info(sb.toString());

        // mandatory data check
        if (StringUtil.nullToBlank(supplierName).isEmpty() || StringUtil.nullToBlank(dateTime).isEmpty() || 
                StringUtil.nullToBlank(contractNumber).isEmpty() || StringUtil.nullToBlank(mdsId).isEmpty()) {
            return "fail : mandatory data is required";
        }

        JpaTransactionManager txManager = null;
        TransactionStatus txStatus = null;

        try {

            txManager = (JpaTransactionManager)DataUtil.getBean("transactionManager");
            txStatus = txManager.getTransaction(null);

            // 웹서비스로그 저장
            PrepaymentSetWSRestartAccount prepaymentSetWSRestartAccount = new PrepaymentSetWSRestartAccount();

            prepaymentSetWSRestartAccount.setSupplierName(supplierName);
            prepaymentSetWSRestartAccount.setDateTime(dateTime);
            prepaymentSetWSRestartAccount.setContractNumber(contractNumber);
            prepaymentSetWSRestartAccount.setMdsId(mdsId);
            prepaymentSetWSRestartAccount.setRestartYn(restartYn);
            prepaymentSetWSRestartAccount.setZeroEmergencyYn(zeroEmergencyYn);
            prepaymentSetWSRestartAccount.setRebillingYn(rebillingYn);
            prepaymentSetWSRestartAccount.setZeroCreditYn(zeroCreditYn);
            prepaymentSetWSRestartAccount.setEncryptionKey(encryptionKey);

            prepaymentSetWSRestartAccountDao.add(prepaymentSetWSRestartAccount);

            boolean convRestartYn = conversionRestartAccountYn(StringUtil.nullToBlank(restartYn));
            boolean convZeroEmergencyYn = conversionZeroEmergencyCreditYn(StringUtil.nullToBlank(zeroEmergencyYn));
            boolean convRebillingYn = conversionRebillingYn(StringUtil.nullToBlank(rebillingYn));
            boolean convZeroCreditYn = conversionZeroCreditYn(StringUtil.nullToBlank(zeroCreditYn));

            // update 하지 않고 종료
            if (!convRestartYn) {
                txManager.commit(txStatus);
                return "success";
            }

//            Meter meter = meterDao.get(mdsId);
            Contract contract = null;
            Map<String, Object> conditionMap = new HashMap<String, Object>();

            conditionMap.put("contractNumber", contractNumber);
            conditionMap.put("supplierName", supplierName);
            conditionMap.put("mdsId", mdsId);
            if (!StringUtil.nullToBlank(encryptionKey).isEmpty()) {
                conditionMap.put("keyNum", encryptionKey);
            }

            List<Contract> listCont = contractDao.getPrepaymentContract(conditionMap);

            if (listCont != null && listCont.size() > 0) {
                contract = listCont.get(0);
            } else {
                txManager.commit(txStatus);
                return "fail : invalid contract info";
            }

            // insert ContarctChangeLog
            if (convZeroEmergencyYn) {
                addContractChangeLog(contract, "emergencyCreditAvailable", contract.getEmergencyCreditAvailable(), Boolean.TRUE);
            }

            if (convZeroEmergencyYn || convZeroCreditYn) {
                addContractChangeLog(contract, "currentCredit", contract.getCurrentCredit(), "0");
            }

            // set Contract
            if (convZeroEmergencyYn) {
                contract.setEmergencyCreditAvailable(Boolean.TRUE);
            }
            
            if (convZeroEmergencyYn || convZeroCreditYn) {
                contract.setCurrentCredit(0d);
                // update Contract
                contractDao.update(contract);
            }

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
     * method name : changeInfo
     * method Desc : Change Energy Utility Information
     *
     * @param supplierName      Utility ID - 공급사 아이디
     * @param dateTime          Date & Time of request - 현재 날짜
     * @param contractNumber    Contract ID - 고객의 계약번호
     * @param mdsId             Meter Serial Number - 미터 시리얼 번호
     * @param changeYn          Change Energy Utility ID Y/N - Y면 공급사를 변경
     * @param newSupplierName   New Utility ID - Utility ID - text string
     * @param encryptionKey     Encryption Key - 암호화 시 인증 키
     * @return
     * @throws Exception
     */
    @WebMethod
    public @WebResult(name="response") String changeInfo(
            @WebParam(name="supplierName") String supplierName,
            @WebParam(name="dateTime") String dateTime,
            @WebParam(name="contractNumber") String contractNumber,
            @WebParam(name="mdsId") String mdsId,
            @WebParam(name="changeYn") String changeYn,
            @WebParam(name="newSupplierName") String newSupplierName,
            @WebParam(name="encryptionKey") String encryptionKey) throws Exception {

        StringBuilder sb = new StringBuilder();

        sb.append("\n supplierName[" + supplierName + "]");
        sb.append("\n dateTime[" + dateTime + "]");
        sb.append("\n contractNumber[" + contractNumber + "]");
        sb.append("\n mdsId[" + mdsId + "]");
        sb.append("\n changeYn[" + changeYn + "]");
        sb.append("\n newSupplierName[" + newSupplierName + "]");
        sb.append("\n encryptionKey[" + encryptionKey + "]");

        log.info(sb.toString());

        // mandatory data check
        if (StringUtil.nullToBlank(supplierName).isEmpty() || StringUtil.nullToBlank(dateTime).isEmpty() || 
                StringUtil.nullToBlank(contractNumber).isEmpty() || StringUtil.nullToBlank(mdsId).isEmpty()) {
            return "fail : mandatory data is required";
        }

        JpaTransactionManager txManager = null;
        TransactionStatus txStatus = null;

        try {

            txManager = (JpaTransactionManager)DataUtil.getBean("transactionManager");
            txStatus = txManager.getTransaction(null);

            // 웹서비스로그 저장
            PrepaymentSetWSChangeInfo prepaymentSetWSChangeInfo = new PrepaymentSetWSChangeInfo();

            prepaymentSetWSChangeInfo.setSupplierName(supplierName);
            prepaymentSetWSChangeInfo.setDateTime(dateTime);
            prepaymentSetWSChangeInfo.setContractNumber(contractNumber);
            prepaymentSetWSChangeInfo.setMdsId(mdsId);
            prepaymentSetWSChangeInfo.setChangeYn(changeYn);
            prepaymentSetWSChangeInfo.setNewSupplierName(newSupplierName);
            prepaymentSetWSChangeInfo.setEncryptionKey(encryptionKey);

            prepaymentSetWSChangeInfoDao.add(prepaymentSetWSChangeInfo);

            // update 하지 않고 종료
            if (!conversionChangeSupplierYn(StringUtil.nullToBlank(changeYn))) {
                txManager.commit(txStatus);
                return "success";
            }

            Contract contract = null;
            Map<String, Object> conditionMap = new HashMap<String, Object>();

            conditionMap.put("contractNumber", contractNumber);
            conditionMap.put("supplierName", supplierName);
            conditionMap.put("mdsId", mdsId);
            if (!StringUtil.nullToBlank(encryptionKey).isEmpty()) {
                conditionMap.put("keyNum", encryptionKey);
            }

            List<Contract> listCont = contractDao.getPrepaymentContract(conditionMap);

            if (listCont != null && listCont.size() > 0) {
                contract = listCont.get(0);
            } else {
                txManager.commit(txStatus);
                return "fail : invalid contract info";
            }

            Supplier newSupplier = supplierDao.findByCondition("name", newSupplierName);
            
            if (newSupplier == null) {
                txManager.commit(txStatus);
                return "fail : invalid new supplier info";
            }

            // insert ContarctChangeLog
            addContractChangeLog(contract, "supplier", contract.getSupplier().getName(), newSupplier.getName());
            
            // update Contract
            contract.setSupplier(newSupplier);
            contractDao.update(contract);

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
     * method name : conversionEmergencyCreditYn
     * method Desc : Vending System 에서 온 code 를 Aimir System code 로 변환
     *
     * @param emergencyYn   Y/N - Emergency Credit 할지 여부
     * @return
     */
    private boolean conversionEmergencyCreditYn(String emergencyYn) {
        boolean rtn = false;
        
        if (emergencyYn.equals(PrepaymentProperty.getProperty("vending.emergency.yes"))) {
            rtn = true;
        }
        
        return rtn;
    }
    
    /**
     * method name : conversionEmergencyAutoYn
     * method Desc : Vending System 에서 온 code 를 Aimir System code 로 변환
     *
     * @param emergencyAutoYn   Auto/manual selection -  자동으로 전환되게 할 것인지 수동으로 전환되게 할 것인지
     * @return
     */
    private boolean conversionEmergencyAutoYn(String emergencyAutoYn) {
        boolean rtn = false;
        
        if (emergencyAutoYn.equals(PrepaymentProperty.getProperty("vending.emergencyauto.yes"))) {
            rtn = true;
        }

        return rtn;
    }

    /**
     * method name : conversionChangeCreditYn
     * method Desc : Vending System 에서 온 code 를 Aimir System code 로 변환
     *
     * @param changeCreditYn    Y/N - Change credit yes/no
     * @return
     */
    private boolean conversionChangeCreditYn(String changeCreditYn) {
        boolean rtn = false;
        
        if (changeCreditYn.equals(PrepaymentProperty.getProperty("vending.changecredit.yes"))) {
            rtn = true;
        }
        
        return rtn;
    }

    /**
     * method name : conversionIncrementCreditYn
     * method Desc : Vending System 에서 온 code 를 Aimir System code 로 변환
     *
     * @param incrementYn   Absolute/Incremental - 현재 금액에서 증가할 것인지 아니면 아예 값을 변경할 것인지
     * @return
     */
    private boolean conversionIncrementCreditYn(String incrementYn) {
        boolean rtn = false;
        
        if (incrementYn.equals(PrepaymentProperty.getProperty("vending.incrementcredit.yes"))) {
            rtn = true;
        }
        
        return rtn;
    }

    /**
     * method name : conversionRestartAccountYn
     * method Desc : Vending System 에서 온 code 를 Aimir System code 로 변환
     *
     * @param restartYn Y/N - 계정을 다시 시작할지 여부
     * @return
     */
    private boolean conversionRestartAccountYn(String restartYn) {
        boolean rtn = false;
        
        if (restartYn.equals(PrepaymentProperty.getProperty("vending.restartaccount.yes"))) {
            rtn = true;
        }
        
        return rtn;
    }

    /**
     * method name : conversionZeroEmergencyCreditYn
     * method Desc : Vending System 에서 온 code 를 Aimir System code 로 변환
     *
     * @param zeroEmergencyYn   Zero credit & Em Cr Y/N - 잔액을 0으로 하고 Emergency Credit 여부 설정
     * @return
     */
    private boolean conversionZeroEmergencyCreditYn(String zeroEmergencyYn) {
        boolean rtn = false;
        
        if (zeroEmergencyYn.equals(PrepaymentProperty.getProperty("vending.zeroemergency.yes"))) {
            rtn = true;
        }
        
        return rtn;
    }

    /**
     * method name : conversionRebillingYn
     * method Desc : Vending System 에서 온 code 를 Aimir System code 로 변환
     *
     * @param rebillingYn   Restart Billing Y/N - 과금 재시작
     * @return
     */
    private boolean conversionRebillingYn(String rebillingYn) {
        boolean rtn = false;
        
        if (rebillingYn.equals(PrepaymentProperty.getProperty("vending.rebilling.yes"))) {
            rtn = true;
        }
        
        return rtn;
    }

    /**
     * method name : conversionZeroCreditYn
     * method Desc : Vending System 에서 온 code 를 Aimir System code 로 변환
     *
     * @param zeroCreditYn  Zero Debts Y/N - 잔액을 0으로 함
     * @return
     */
    private boolean conversionZeroCreditYn(String zeroCreditYn) {
        boolean rtn = false;
        
        if (zeroCreditYn.equals(PrepaymentProperty.getProperty("vending.zerocredit.yes"))) {
            rtn = true;
        }

        return rtn;
    }

    /**
     * method name : conversionChangeSupplierYn
     * method Desc : Vending System 에서 온 code 를 Boolean 값으로 변환
     *
     * @param changeYn  Change Energy Utility ID Y/N - Y면 공급사를 변경
     * @return
     */
    private boolean conversionChangeSupplierYn(String changeYn) {
        boolean rtn = false;
        
        if (changeYn.equals(PrepaymentProperty.getProperty("vending.changesupplier.yes"))) {
            rtn = true;
        }
        
        return rtn;
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
}