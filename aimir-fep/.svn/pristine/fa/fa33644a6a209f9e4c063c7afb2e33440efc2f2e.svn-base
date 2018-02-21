/**
 * PrepaymentChangeTariffWS.java Copyright NuriTelecom Limited 2011
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
import com.aimir.dao.prepayment.PrepaymentChangeTariffWSChangeTariffDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.ContractChangeLogDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.dao.system.TariffTypeDao;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.prepayment.PrepaymentChangeTariffWSChangeTariff;
import com.aimir.model.system.Contract;
import com.aimir.model.system.ContractChangeLog;
import com.aimir.model.system.TariffType;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.StringUtil;

/**
 * PrepaymentChangeTariffWS.java Description
 *
 *
 * Date          Version     Author   Description
 * 2011. 7. 26.  v1.0        문동규   선불연계 - 선불 요금 변경 설정
 *
 */

@WebService(serviceName="PrepaymentChangeTariffWS")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
@Service
public class PrepaymentChangeTariffWS {

    protected static Log log = LogFactory.getLog(PrepaymentChangeTariffWS.class);

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
    protected TariffTypeDao tariffTypeDao;

    @Autowired
    protected PrepaymentChangeTariffWSChangeTariffDao prepaymentChangeTariffWSChangeTariffDao;

    /**
     * method name : changeTariff
     * method Desc : 선불 요금 변경
     *
     * @param supplierName      Utility ID - 공급사 아이디
     * @param dateTime          Date & Time of request - 현재 날짜
     * @param contractNumber    Contract ID - 고객의 계약번호
     * @param mdsId             Meter Serial Number - 미터 시리얼 번호
     * @param applyDateTime     Date & Time of implementation - 적용 날짜
     * @param tariffId          Tariff ID - 변경할 Tariff ID
     * @param encryptionKey     Encryption Key - 암호화 시 인증 키
     * @return
     * @throws Exception
     */
    @WebMethod
    public @WebResult(name="response") String changeTariff(
            @WebParam(name="supplierName") String supplierName,
            @WebParam(name="dateTime") String dateTime,
            @WebParam(name="contractNumber") String contractNumber,
            @WebParam(name="mdsId") String mdsId,
            @WebParam(name="applyDateTime") String applyDateTime,
            @WebParam(name="tariffId") Integer tariffId,
            @WebParam(name="encryptionKey") String encryptionKey) throws Exception {

        StringBuilder sb = new StringBuilder();

        sb.append("\n supplierName[" + supplierName + "]");
        sb.append("\n dateTime[" + dateTime + "]");
        sb.append("\n contractNumber[" + contractNumber + "]");
        sb.append("\n mdsId[" + mdsId + "]");
        sb.append("\n applyDateTime[" + applyDateTime + "]");
        sb.append("\n tariffId[" + tariffId + "]");
        sb.append("\n encryptionKey[" + encryptionKey + "]");

        log.info(sb.toString());

        // mandatory data check
        if (StringUtil.nullToBlank(supplierName).isEmpty() || StringUtil.nullToBlank(dateTime).isEmpty() || 
                StringUtil.nullToBlank(contractNumber).isEmpty() || StringUtil.nullToBlank(mdsId).isEmpty() ||
                StringUtil.nullToBlank(applyDateTime).isEmpty() || StringUtil.nullToBlank(tariffId).isEmpty() ||
                StringUtil.nullToBlank(encryptionKey).isEmpty()) {
            return "fail : mandatory data is required";
        }

        JpaTransactionManager txManager = null;
        TransactionStatus txStatus = null;

        try {

            txManager = (JpaTransactionManager)DataUtil.getBean("transactionManager");
            txStatus = txManager.getTransaction(null);

            // 웹서비스로그 저장
            PrepaymentChangeTariffWSChangeTariff prepaymentChangeTariffWSChangeTariff = new PrepaymentChangeTariffWSChangeTariff();

            prepaymentChangeTariffWSChangeTariff.setSupplierName(supplierName);
            prepaymentChangeTariffWSChangeTariff.setDateTime(dateTime);
            prepaymentChangeTariffWSChangeTariff.setContractNumber(contractNumber);
            prepaymentChangeTariffWSChangeTariff.setMdsId(mdsId);
            prepaymentChangeTariffWSChangeTariff.setApplyDateTime(applyDateTime);
            prepaymentChangeTariffWSChangeTariff.setTariffId(tariffId);
            prepaymentChangeTariffWSChangeTariff.setEncryptionKey(encryptionKey);

            prepaymentChangeTariffWSChangeTariffDao.add(prepaymentChangeTariffWSChangeTariff);

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

            TariffType tariffType = tariffTypeDao.findByCondition("code", tariffId);

            // Insert ContractChangeLog
            addContractChangeLog(contract, applyDateTime, "tariffIndex", (contract.getTariffIndex() != null) ? contract.getTariffIndex().getCode() : null, (tariffType != null) ? tariffId : null);
            addContractChangeLog(contract, applyDateTime, "contractIndex", contract.getContractIndex(), tariffId);

            contract.setTariffIndex(tariffType);
            contract.setContractIndex(tariffId);

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