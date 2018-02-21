/**
 * EmergencyCreditWS.java Copyright NuriTelecom Limited 2011
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
import com.aimir.dao.prepayment.EmergencyCreditWSStartDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.ContractChangeLogDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.PrepaymentAuthDeviceDao;
import com.aimir.dao.system.PrepaymentLogDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.Meter;
import com.aimir.model.prepayment.EmergencyCreditWSStart;
import com.aimir.model.system.Code;
import com.aimir.model.system.Contract;
import com.aimir.model.system.ContractChangeLog;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.StringUtil;

/**
 * EmergencyCreditWS.java Description
 *
 *
 * Date          Version     Author   Description
 * 2011. 7. 25.  v1.0        문동규   선불연계 - Emergency Credit
 *
 */

@WebService(serviceName="EmergencyCreditWS")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
@Service
public class EmergencyCreditWS {

    protected static Log log = LogFactory.getLog(EmergencyCreditWS.class);

    @Autowired
    protected MeterDao meterDao;

    @Autowired
    protected ContractDao contractDao;

    @Autowired
    protected ContractChangeLogDao contractChangeLogDao;

    @Autowired
    protected SupplierDao supplierDao;

    @Autowired
    protected CodeDao codeDao;

    @Autowired
    protected EmergencyCreditWSStartDao emergencyCreditWSStartDao;

    @Autowired
    protected PrepaymentLogDao prepaymentLogDao;

    @Autowired
    protected PrepaymentAuthDeviceDao prepaymentAuthDeviceDao;
    
    /**
     * method name : start
     * method Desc : Emergency Credit 시작
     * 
     * @param supplierName      Utility ID - 공급사 아이디
     * @param dateTime          Date & Time of request - 현재 날짜
     * @param contractNumber    Contract ID - 고객의 계약번호
     * @param mdsId             Meter Serial Number - 미터 시리얼 번호
     * @param applyDateTime     Date & Time of implementation - 적용 날짜
     * @param deviceId          Device ID - 인증 장비 아이디 (모바일번호, IHD번호)
     * @param encryptionKey     Encryption Key - 암호화 시 인증 키
     * @return
     * @throws Exception
     */
    @WebMethod
    public @WebResult(name="response") String start(
            @WebParam(name="supplierName") String supplierName,
            @WebParam(name="dateTime") String dateTime,
            @WebParam(name="contractNumber") String contractNumber,
            @WebParam(name="mdsId") String mdsId,
            @WebParam(name="applyDateTime") String applyDateTime,
            @WebParam(name="deviceId") String deviceId,
            @WebParam(name="encryptionKey") String encryptionKey) throws Exception {

        StringBuilder sb = new StringBuilder();

        sb.append("\n supplierName[" + supplierName + "]");
        sb.append("\n dateTime[" + dateTime + "]");
        sb.append("\n contractNumber[" + contractNumber + "]");
        sb.append("\n mdsId[" + mdsId + "]");
        sb.append("\n applyDateTime[" + applyDateTime + "]");
        sb.append("\n deviceId[" + deviceId + "]");
        sb.append("\n encryptionKey[" + encryptionKey + "]");

        log.info(sb.toString());

        // mandatory data check
        if (StringUtil.nullToBlank(supplierName).isEmpty() || StringUtil.nullToBlank(dateTime).isEmpty() || 
                StringUtil.nullToBlank(contractNumber).isEmpty() || StringUtil.nullToBlank(mdsId).isEmpty() ||
                StringUtil.nullToBlank(applyDateTime).isEmpty() || StringUtil.nullToBlank(deviceId).isEmpty()) {
            return "fail : mandatory data is required";
        }

        JpaTransactionManager txManager = null;
        TransactionStatus txStatus = null;

        try {

            txManager = (JpaTransactionManager)DataUtil.getBean("transactionManager");
            txStatus = txManager.getTransaction(null);

            // 웹서비스로그 저장
            EmergencyCreditWSStart emergencyCreditWSStart = new EmergencyCreditWSStart();
            
            emergencyCreditWSStart.setSupplierName(supplierName);
            emergencyCreditWSStart.setDateTime(dateTime);
            emergencyCreditWSStart.setContractNumber(contractNumber);
            emergencyCreditWSStart.setMdsId(mdsId);
            emergencyCreditWSStart.setApplyDateTime(applyDateTime);
            emergencyCreditWSStart.setDeviceId(deviceId);
            emergencyCreditWSStart.setEncryptionKey(encryptionKey);
            
            emergencyCreditWSStartDao.add(emergencyCreditWSStart);

            Meter meter = meterDao.get(mdsId);
            Map<String, Object> conditionMap = new HashMap<String, Object>();

            conditionMap.put("contractNumber", contractNumber);
            conditionMap.put("supplierName", supplierName);
            conditionMap.put("mdsId", mdsId);
            conditionMap.put("mobileDeviceId", deviceId);
            conditionMap.put("emergencyCreditYn", Boolean.TRUE);

            if (!StringUtil.nullToBlank(encryptionKey).isEmpty()) {
                conditionMap.put("keyNum", encryptionKey);
            }

            /* check PrepaymentAuthDevice - 현재 보류. 장비 인증 확정 후 개발
            Contract contract = getPrepaymentContractByDeviceId(conditionMap);

            if (contract == null || contract.getId() == null) {
                txManager.commit(txStatus);
                return "fail : invalid contract info";
            }*/

            // TODO - 장비 인증 없이 Contract 조회 start
            Contract contract = null;

            List<Contract> listCont = contractDao.getPrepaymentContract(conditionMap);

            if (listCont != null && listCont.size() > 0) {
                contract = listCont.get(0);
            } else {
                txManager.commit(txStatus);
                return "fail : invalid contract info";
            }
            // 장비 인증 없이 Contract 조회 end

            Code code = codeDao.findByCondition("code", "2.2.2");   // creditType:Emergency Credit

            // Insert ContractChangeLog
            addContractChangeLog(contract, applyDateTime, "emergencyCreditStartTime", contract.getEmergencyCreditStartTime(), applyDateTime);
            addContractChangeLog(contract, applyDateTime, "creditType", contract.getCreditType().getCode(), code.getCode());
            
            contract.setEmergencyCreditStartTime(applyDateTime);
            contract.setCreditType(code);

            contractDao.update(contract);
            
            // CommandOperationUtil 생성 후 개발 부분 Start
            if (meter.getPrepaymentMeter()) { // Prepayment Meter 인 경우 온라인 충전

            } else { // Soft Credit 인 경우 충전

            }

            // Notification 통보 프레임 워크 생성 후 개발 Start

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
     * method name : getPrepaymentContractByDeviceId
     * method Desc :
     *
     * @param conditionMap
     * @return
     */
//    private Contract getPrepaymentContractByDeviceId(Map<String, Object> conditionMap) {
//        Contract contract = null;
//
//        String contractNumber = (String) conditionMap.get("contractNUmber");
//        String supplierName = (String) conditionMap.get("supplierName");
//        String mdsId = (String) conditionMap.get("mdsId");
//        String mobileDeviceId = (String) conditionMap.get("mobileDeviceId");
//        String keyNum = (String) conditionMap.get("keyNum");
//
//        Set<Condition> set = new HashSet<Condition>();
//        Condition condition = new Condition();
//
//        condition.setField("authKey");
//        condition.setValue(new String[] { mobileDeviceId });
//        condition.setRestrict(Restriction.EQ);
//        set.add(condition);
//
//        condition = new Condition();
//        condition.setField("contract");
//        condition.setValue(new Object[] { "c" });
//        condition.setRestrict(Restriction.ALIAS);
//        set.add(condition);
//
//        condition = new Condition();
//        condition.setField("c.supplier");
//        condition.setValue(new Object[] { "s" });
//        condition.setRestrict(Restriction.ALIAS);
//        set.add(condition);
//
//        condition = new Condition();
//        condition.setField("c.meter");
//        condition.setValue(new Object[] { "m" });
//        condition.setRestrict(Restriction.ALIAS);
//        set.add(condition);
//
//        condition = new Condition();
//        condition.setField("c.contractNumber");
//        condition.setValue(new String[] { contractNumber });
//        condition.setRestrict(Restriction.EQ);
//        set.add(condition);
//
//        condition = new Condition();
//        condition.setField("s.name");
//        condition.setValue(new String[] { supplierName });
//        condition.setRestrict(Restriction.EQ);
//        set.add(condition);
//
//        condition = new Condition();
//        condition.setField("m.mdsId");
//        condition.setValue(new String[] { mdsId });
//        condition.setRestrict(Restriction.EQ);
//        set.add(condition);
//
//        condition = new Condition();
//        condition.setField("c.contractNumber");
//        condition.setValue(new Object[] { contractNumber });
//        condition.setRestrict(Restriction.EQ);
//        set.add(condition);
//
//        if (StringUtil.nullToBlank(keyNum).length() > 0) {
//            condition = new Condition();
//            condition.setField("c.keyNum");
//            condition.setValue(new Object[] { keyNum });
//            condition.setRestrict(Restriction.EQ);
//            set.add(condition);
//        }
//
//        condition = new Condition();
//        condition.setField("c.emergencyCreditAvailable");
//        condition.setValue(new Object[] { Boolean.TRUE });
//        condition.setRestrict(Restriction.EQ);
//        set.add(condition);
//
//        List<PrepaymentAuthDevice> list = prepaymentAuthDeviceDao.findByConditions(set);
//
//        if (list != null && list.size() > 0) {
//            contract = list.get(0).getContract();
//        }
//
//        return contract;
//    }

    /**
     * method name : addContractChangeLog
     * method Desc : ContractChangeLog 에 데이터 insert
     *
     * @param contract
     * @param field
     * @param beforeValue
     * @param afterValue
     */
    private void addContractChangeLog(Contract contract, String startDateTime, String field, String beforeValue, String afterValue) {
        
        // ContractChangeLog Insert
        ContractChangeLog contractChangeLog = new ContractChangeLog();

        contractChangeLog.setContract(contract);
        contractChangeLog.setCustomer(contract.getCustomer());
        contractChangeLog.setStartDatetime(startDateTime);
        contractChangeLog.setChangeField(field);
        contractChangeLog.setBeforeValue(beforeValue);
        contractChangeLog.setAfterValue(afterValue);
//        contractChangeLog.setOperator(operator);
        contractChangeLog.setWriteDatetime(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
//        contractChangeLog.setDescr(descr);

        contractChangeLogDao.add(contractChangeLog);
    }
}