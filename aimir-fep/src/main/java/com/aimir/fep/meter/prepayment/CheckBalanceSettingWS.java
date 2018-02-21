/**
 * CheckBalanceSettingWS.java Copyright NuriTelecom Limited 2011
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
import com.aimir.dao.prepayment.CheckBalanceSettingWSModifyDao;
import com.aimir.dao.system.ContractChangeLogDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.PrepaymentLogDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.Meter;
import com.aimir.model.prepayment.CheckBalanceSettingWSModify;
import com.aimir.model.system.Contract;
import com.aimir.model.system.ContractChangeLog;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.StringUtil;

/**
 * CheckBalanceSettingWS.java Description
 *
 *
 * Date          Version     Author   Description
 * 2011. 7. 19.  v1.0        문동규   선불연계 - 잔액 체크 주기 설정
 *
 */

@WebService(serviceName="CheckBalanceSettingWS")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
@Service
public class CheckBalanceSettingWS {

    protected static Log log = LogFactory.getLog(CheckBalanceSettingWS.class);

    @Autowired
    protected MeterDao meterDao;

    @Autowired
    protected ContractDao contractDao;

    @Autowired
    protected ContractChangeLogDao contractChangeLogDao;

    @Autowired
    protected SupplierDao supplierDao;

    @Autowired
    protected CheckBalanceSettingWSModifyDao checkBalanceSettingWSModifyDao;

    @Autowired
    protected PrepaymentLogDao prepaymentLogDao;

    /**
     * method name : modify
     * method Desc : 잔액 체크 주기 설정
     *
     * @param supplierName      Utility ID - 공급사 아이디
     * @param dateTime          Date & Time of request - 현재 날짜
     * @param contractNumber    Contract ID - 고객의 계약번호
     * @param mdsId             Meter Serial Number - 미터 시리얼 번호
     * @param notiPeriod        Notification Period - 통보 주기
     * @param notiInterval      Notification Interval - 통보 간격
     * @param notiTime          Notification Time - 통보 주기
     * @param mon               Notification Weekly Monday - 월요일 통보여부
     * @param tue               Notification Weekly Tuesday - 화요일 통보여부
     * @param wed               Notification Weekly Wednesday - 수요일 통보여부
     * @param thu               Notification Weekly Thursday - 목요일 통보여부
     * @param fri               Notification Weekly Friday - 금요일 통보여부
     * @param sat               Notification Weekly Saturday - 토요일 통보여부
     * @param sun               Notification Weekly Sunday - 일요일 통보여부
     * @param threshold         Threshold (XX%) - XX% 이상 소진 시 통보
     * @param mobileDeviceId    Mobile Device ID - 장비 아이디
     * @param encryptionKey     Encryption Key - 암호화 시 인증 키
     * @return
     * @throws Exception
     */
    @WebMethod
    public @WebResult(name="response") String modify(
            @WebParam(name="supplierName") String supplierName,
            @WebParam(name="dateTime") String dateTime,
            @WebParam(name="contractNumber") String contractNumber,
            @WebParam(name="mdsId") String mdsId,
            @WebParam(name="notiPeriod") Integer notiPeriod,
            @WebParam(name="notiInterval") Integer notiInterval,
            @WebParam(name="notiTime") Integer notiTime,
            @WebParam(name="mon") Boolean mon,
            @WebParam(name="tue") Boolean tue,
            @WebParam(name="wed") Boolean wed,
            @WebParam(name="thu") Boolean thu,
            @WebParam(name="fri") Boolean fri,
            @WebParam(name="sat") Boolean sat,
            @WebParam(name="sun") Boolean sun,
            @WebParam(name="threshold") Integer threshold,
            @WebParam(name="mobileDeviceId") String mobileDeviceId,
            @WebParam(name="encryptionKey") String encryptionKey) throws Exception {

        StringBuilder sb = new StringBuilder();

        sb.append("\n supplierName[" + supplierName + "]");
        sb.append("\n dateTime[" + dateTime + "]");
        sb.append("\n contractNumber[" + contractNumber + "]");
        sb.append("\n mdsId[" + mdsId + "]");
        sb.append("\n notiPeriod[" + notiPeriod + "]");
        sb.append("\n notiInterval[" + notiInterval + "]");
        sb.append("\n notiTime[" + notiTime + "]");
        sb.append("\n mon[" + mon + "]");
        sb.append("\n tue[" + tue + "]");
        sb.append("\n wed[" + wed + "]");
        sb.append("\n thu[" + thu + "]");
        sb.append("\n fri[" + fri + "]");
        sb.append("\n sat[" + sat + "]");
        sb.append("\n sun[" + sun + "]");
        sb.append("\n threshold[" + threshold + "]");
        sb.append("\n mobileDeviceId[" + mobileDeviceId + "]");
        sb.append("\n encryptionKey[" + encryptionKey + "]");

        log.info(sb.toString());

        // mandatory data check
        if (StringUtil.nullToBlank(supplierName).isEmpty() || StringUtil.nullToBlank(dateTime).isEmpty() || 
                StringUtil.nullToBlank(contractNumber).isEmpty() || StringUtil.nullToBlank(mdsId).isEmpty() ||
                StringUtil.nullToBlank(notiPeriod).isEmpty() || StringUtil.nullToBlank(notiInterval).isEmpty() || 
                StringUtil.nullToBlank(notiTime).isEmpty() || StringUtil.nullToBlank(mobileDeviceId).isEmpty() || 
                StringUtil.nullToBlank(encryptionKey).isEmpty()) {
            return "fail : mandatory data is required";
        }

        JpaTransactionManager txManager = null;
        TransactionStatus txStatus = null;

        try {

            txManager = (JpaTransactionManager)DataUtil.getBean("transactionManager");
            txStatus = txManager.getTransaction(null);

            // 웹서비스로그 저장
            CheckBalanceSettingWSModify checkBalanceSettingWSModify = new CheckBalanceSettingWSModify();
            
            checkBalanceSettingWSModify.setSupplierName(supplierName);
            checkBalanceSettingWSModify.setDateTime(dateTime);
            checkBalanceSettingWSModify.setContractNumber(contractNumber);
            checkBalanceSettingWSModify.setMdsId(mdsId);
            checkBalanceSettingWSModify.setNotificationPeriod(notiPeriod);
            checkBalanceSettingWSModify.setNotificationInterval(notiInterval);
            checkBalanceSettingWSModify.setNotificationTime(notiTime);
            checkBalanceSettingWSModify.setNotificationWeeklyMon(mon);
            checkBalanceSettingWSModify.setNotificationWeeklyTue(tue);
            checkBalanceSettingWSModify.setNotificationWeeklyWed(wed);
            checkBalanceSettingWSModify.setNotificationWeeklyThu(thu);
            checkBalanceSettingWSModify.setNotificationWeeklyFri(fri);
            checkBalanceSettingWSModify.setNotificationWeeklySat(sat);
            checkBalanceSettingWSModify.setNotificationWeeklySun(sun);
            checkBalanceSettingWSModify.setThreshold(threshold);
            checkBalanceSettingWSModify.setMobileDeviceId(mobileDeviceId);
            checkBalanceSettingWSModify.setEncryptionKey(encryptionKey);
            
            checkBalanceSettingWSModifyDao.add(checkBalanceSettingWSModify);

            Meter meter = meterDao.get(mdsId);
            Contract contract = null;
            Map<String, Object> conditionMap = new HashMap<String, Object>();

            conditionMap.put("contractNumber", contractNumber);
            conditionMap.put("supplierName", supplierName);
            conditionMap.put("mdsId", mdsId);
            conditionMap.put("keyNum", encryptionKey);

            // check PrepaymentAuthDevice - 현재 보류. 장비 인증 확정 후 개발
            
            List<Contract> listCont = contractDao.getPrepaymentContract(conditionMap);

            if (listCont != null && listCont.size() > 0) {
                contract = listCont.get(0);
            } else {
                return "fail : invalid contract info";
            }

            // CommandOperationUtil 생성 후 개발 부분 Start
            if (meter.getPrepaymentMeter()) { // Prepayment Meter 인 경우 온라인 충전

            } else { // Soft Credit 인 경우 충전

            }
            // CommandOperationUtil 생성 후 개발 부분 End

            // insert ContractChangeLog
            if (!StringUtil.nullToBlank(threshold).isEmpty()) {
                addContractChangeLog(contract, "prepaymentThreshold", contract.getPrepaymentThreshold(), threshold);
            }

            // Contract
            contract.setNotificationPeriod(notiPeriod);
            contract.setNotificationInterval(notiInterval);
            contract.setNotificationTime(notiTime);
            contract.setNotificationWeeklyMon(mon);
            contract.setNotificationWeeklyTue(tue);
            contract.setNotificationWeeklyWed(wed);
            contract.setNotificationWeeklyThu(thu);
            contract.setNotificationWeeklyFri(fri);
            contract.setNotificationWeeklySat(sat);
            contract.setNotificationWeeklySun(sun);
            
            if (!StringUtil.nullToBlank(threshold).isEmpty()) {
                contract.setPrepaymentThreshold(threshold);
            }

            contractDao.update(contract);

            // 통보안함 - 추후 재확인

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
}