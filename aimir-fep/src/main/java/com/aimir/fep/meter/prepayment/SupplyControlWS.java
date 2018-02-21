/**
 * SupplyControlWS.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.fep.meter.prepayment;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import com.aimir.constants.CommonConstants.GroupType;
import com.aimir.constants.CommonConstants.LimitType;
import com.aimir.constants.CommonConstants.ScheduleType;
import com.aimir.dao.device.LoadLimitScheduleDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.prepayment.SupplyControlWSInterruptDao;
import com.aimir.dao.prepayment.SupplyControlWSRearmDao;
import com.aimir.dao.system.ContractChangeLogDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.PrepaymentLogDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.LoadLimitSchedule;
import com.aimir.model.prepayment.SupplyControlWSInterrupt;
import com.aimir.model.prepayment.SupplyControlWSRearm;
import com.aimir.model.system.Contract;
import com.aimir.model.system.ContractChangeLog;
import com.aimir.util.Condition;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.Condition.Restriction;

/**
 * SupplyControlWS.java Description
 * <p>
 * <pre>
 * Date          Version     Author   Description
 * 2011. 7. 15.  v1.0        문동규   선불연계 - 공급 차단/재개
 * </pre>
 */

@WebService(serviceName="SupplyControlWS")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
@Service
public class SupplyControlWS {

    protected static Log log = LogFactory.getLog(SupplyControlWS.class);

    @Autowired
    protected MeterDao meterDao;

    @Autowired
    protected ContractDao contractDao;

    @Autowired
    protected ContractChangeLogDao contractChangeLogDao;

    @Autowired
    protected SupplierDao supplierDao;

    @Autowired
    protected LoadLimitScheduleDao loadLimitScheduleDao;

    @Autowired
    protected SupplyControlWSInterruptDao supplyControlWSInterruptDao;

    @Autowired
    protected SupplyControlWSRearmDao supplyControlWSRearmDao;

    @Autowired
    protected PrepaymentLogDao prepaymentLogDao;

    /**
     * method name : interrupt<b/>
     * method Desc : 공급 차단
     *
     * @param supplierName      Utility ID - 공급사 아이디
     * @param dateTime          Date & Time - 충전 날짜
     * @param contractNumber    Contract ID - 고객의 계약번호
     * @param mdsId             Meter Serial Number - 미터 시리얼 번호
     * @param applyDateTime     Date & Time for interruption - 시작 시간
     * @param powerDelay        Delay kWh - 차단에 도달하는 kWh
     * @param encryptionKey     Encryption Key - 암호화 시 인증 키
     * @return
     * @throws Exception
     */
    @WebMethod
    public @WebResult(name="response") String interrupt(
            @WebParam(name="supplierName") String supplierName,
            @WebParam(name="dateTime") String dateTime,
            @WebParam(name="contractNumber") String contractNumber,
            @WebParam(name="mdsId") String mdsId,
            @WebParam(name="applyDateTime") String applyDateTime,
            @WebParam(name="powerDelay") Double powerDelay,
            @WebParam(name="encryptionKey") String encryptionKey) throws Exception {

        StringBuilder sb = new StringBuilder();

        sb.append("\n supplierName[" + supplierName + "]");
        sb.append("\n dateTime[" + dateTime + "]");
        sb.append("\n contractNumber[" + contractNumber + "]");
        sb.append("\n mdsId[" + mdsId + "]");
        sb.append("\n applyDateTime[" + applyDateTime + "]");
        sb.append("\n powerDelay[" + powerDelay + "]");
        sb.append("\n encryptionKey[" + encryptionKey + "]");

        log.info(sb.toString());

        if (StringUtil.nullToBlank(supplierName).isEmpty() || StringUtil.nullToBlank(dateTime).isEmpty() || 
                StringUtil.nullToBlank(contractNumber).isEmpty() || StringUtil.nullToBlank(mdsId).isEmpty() ||
                StringUtil.nullToBlank(applyDateTime).isEmpty() || StringUtil.nullToBlank(powerDelay).isEmpty() ||
                StringUtil.nullToBlank(encryptionKey).isEmpty()) {
            return "fail : mandatory data is required";
        }
        JpaTransactionManager txManager = null;
        TransactionStatus txStatus = null;

        try {

            txManager = (JpaTransactionManager)DataUtil.getBean("transactionManager");
            txStatus = txManager.getTransaction(null);

            // 웹서비스로그 저장
            SupplyControlWSInterrupt supplyControlWSInterrupt = new SupplyControlWSInterrupt();

            supplyControlWSInterrupt.setSupplierName(supplierName);
            supplyControlWSInterrupt.setDateTime(dateTime);
            supplyControlWSInterrupt.setContractNumber(contractNumber);
            supplyControlWSInterrupt.setMdsId(mdsId);
            supplyControlWSInterrupt.setApplyDateTime(applyDateTime);
            supplyControlWSInterrupt.setPowerDelay(powerDelay);
            supplyControlWSInterrupt.setEncryptionKey(encryptionKey);

            supplyControlWSInterruptDao.add(supplyControlWSInterrupt);

            Contract contract = new Contract();
            Map<String, Object> conditionMap = new HashMap<String, Object>();
            
            conditionMap.put("contractNumber", contractNumber);
            conditionMap.put("supplierName", supplierName);
            conditionMap.put("mdsId", mdsId);
            conditionMap.put("keyNum", encryptionKey);
            
            List<Contract> listCont = contractDao.getPrepaymentContract(conditionMap);

            if (listCont != null && listCont.size() > 0) {
                contract = listCont.get(0);
            } else {
                return "fail : invalid contract info";
            }

            LoadLimitSchedule loadLimitSchedule = new LoadLimitSchedule();
            List<LoadLimitSchedule> listLimit = selectLoadLimitSchedule(conditionMap);

            if (listLimit != null && listLimit.size() > 0) {    // update
                loadLimitSchedule = listLimit.get(0);
                
                loadLimitSchedule.setLimit(powerDelay);
                loadLimitSchedule.setStartTime(applyDateTime);
                
                loadLimitScheduleDao.update(loadLimitSchedule);
            } else {    // insert
                loadLimitSchedule = new LoadLimitSchedule();
                
                loadLimitSchedule.setTargetType(GroupType.Meter.name());
                loadLimitSchedule.setTarget(mdsId);
                loadLimitSchedule.setScheduleType(ScheduleType.Date.name());
                loadLimitSchedule.setLimitType(LimitType.Usage.name());
                loadLimitSchedule.setLimit(powerDelay);
                loadLimitSchedule.setCreateTime(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
                loadLimitSchedule.setStartTime(applyDateTime);

                loadLimitScheduleDao.add(loadLimitSchedule);
            }

            // insert ContractChangeLog
            addContractChangeLog(contract, applyDateTime, "prepaymentPowerDelay", contract.getPrepaymentPowerDelay(), powerDelay);
            
            // update Contract
            contract.setPrepaymentPowerDelay(powerDelay);
            contractDao.update(contract);
            
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
     * method name : rearm
     * method Desc : 공급 재개
     *
     * @param supplierName      Utility ID - 공급사 아이디
     * @param dateTime          Date & Time - 충전 날짜
     * @param contractNumber    Contract ID - 고객의 계약번호
     * @param mdsId             Meter Serial Number - 미터 시리얼 번호
     * @param rearmDateTime     Date & Time for re-arm - 다시 re-arm 하는 시간
     * @param encryptionKey     Encryption Key - 암호화 시 인증 키
     * @return
     * @throws Exception
     */
    @WebMethod
    public @WebResult(name="response") String rearm(
            @WebParam(name="supplierName") String supplierName,
            @WebParam(name="dateTime") String dateTime,
            @WebParam(name="contractNumber") String contractNumber,
            @WebParam(name="mdsId") String mdsId,
            @WebParam(name="rearmDateTime") String rearmDateTime,
            @WebParam(name="encryptionKey") String encryptionKey) throws Exception {

        StringBuilder sb = new StringBuilder();

        sb.append("\n supplierName[" + supplierName + "]");
        sb.append("\n dateTime[" + dateTime + "]");
        sb.append("\n contractNumber[" + contractNumber + "]");
        sb.append("\n mdsId[" + mdsId + "]");
        sb.append("\n rearmDateTime[" + rearmDateTime + "]");
        sb.append("\n encryptionKey[" + encryptionKey + "]");

        log.info(sb.toString());

        // mandatory data check
        if (StringUtil.nullToBlank(supplierName).isEmpty() || StringUtil.nullToBlank(dateTime).isEmpty() || 
                StringUtil.nullToBlank(contractNumber).isEmpty() || StringUtil.nullToBlank(mdsId).isEmpty() ||
                StringUtil.nullToBlank(rearmDateTime).isEmpty() || StringUtil.nullToBlank(encryptionKey).isEmpty()) {
            return "fail : mandatory data is required";
        }

        JpaTransactionManager txManager = null;
        TransactionStatus txStatus = null;

        try {

            txManager = (JpaTransactionManager)DataUtil.getBean("transactionManager");
            txStatus = txManager.getTransaction(null);

            // 웹서비스로그 저장
            SupplyControlWSRearm supplyControlWSRearm = new SupplyControlWSRearm();

            supplyControlWSRearm.setSupplierName(supplierName);
            supplyControlWSRearm.setDateTime(dateTime);
            supplyControlWSRearm.setContractNumber(contractNumber);
            supplyControlWSRearm.setMdsId(mdsId);
            supplyControlWSRearm.setRearmDateTime(rearmDateTime);
            supplyControlWSRearm.setEncryptionKey(encryptionKey);

            supplyControlWSRearmDao.add(supplyControlWSRearm);

            Map<String, Object> conditionMap = new HashMap<String, Object>();
            
            conditionMap.put("contractNumber", contractNumber);
            conditionMap.put("supplierName", supplierName);
            conditionMap.put("mdsId", mdsId);
            conditionMap.put("keyNum", encryptionKey);
            
            List<Contract> listCont = contractDao.getPrepaymentContract(conditionMap);

            if (listCont == null || listCont.size() <= 0) {
                return "fail : invalid contract info";
            }

            // LoadLimitSchedule
            LoadLimitSchedule loadLimitSchedule = new LoadLimitSchedule();
            List<LoadLimitSchedule> listLimit = selectLoadLimitSchedule(conditionMap);

            if (listLimit != null && listLimit.size() > 0) {    // update
                loadLimitSchedule = listLimit.get(0);
                
                loadLimitSchedule.setEndTime(rearmDateTime);
                
                loadLimitScheduleDao.update(loadLimitSchedule);
            } else {    // not exist
                return "fail : matching data is not exist";
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
     * method name : modifyLoadLimitSchedule
     * method Desc : LoadLimitSchedule 에 데이터가 존재하면 Update, 없으면 insert
     *
     * @param conditionMap
     */
    private List<LoadLimitSchedule> selectLoadLimitSchedule(Map<String, Object> conditionMap) {
        List<LoadLimitSchedule> limitList = null;

//        String contractNumber = (String)conditionMap.get("contractNumber");
//        String supplierName = (String)conditionMap.get("supplierName");
        String mdsId = (String)conditionMap.get("mdsId");
//        String keyNum = (String)conditionMap.get("keyNum");

//        Double powerDelay = (Double)conditionMap.get("powerDelay");
//        String applyDateTime = (String)conditionMap.get("applyDateTime");
        
//        LoadLimitSchedule loadLimitSchedule = new LoadLimitSchedule();
        
        Set<Condition> setLimit = new HashSet<Condition>();
        Condition conditionLimit = new Condition();
        conditionLimit.setField("targetType");
        conditionLimit.setValue(new Object[]{GroupType.Meter});
        conditionLimit.setRestrict(Restriction.EQ);
        setLimit.add(conditionLimit);

        conditionLimit = new Condition();
        conditionLimit.setField("target");
        conditionLimit.setValue(new String[]{mdsId});
        conditionLimit.setRestrict(Restriction.EQ);
        setLimit.add(conditionLimit);

        conditionLimit = new Condition();
        conditionLimit.setField("scheduleType");
        conditionLimit.setValue(new Object[]{ScheduleType.Date});
        conditionLimit.setRestrict(Restriction.EQ);
        setLimit.add(conditionLimit);

        conditionLimit = new Condition();
        conditionLimit.setField("endTime");
        conditionLimit.setRestrict(Restriction.NULL);
        setLimit.add(conditionLimit);

        limitList = loadLimitScheduleDao.findByConditions(setLimit);
        return limitList;
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
    private void addContractChangeLog(Contract contract, String startDateTime, String field, Object beforeValue, Object afterValue) {

        // ContractChangeLog Insert
        ContractChangeLog contractChangeLog = new ContractChangeLog();

        contractChangeLog.setContract(contract);
        contractChangeLog.setCustomer(contract.getCustomer());
        contractChangeLog.setStartDatetime(startDateTime);
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