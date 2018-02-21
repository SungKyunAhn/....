/**
 * ChangeCreditTypeWS.java Copyright NuriTelecom Limited 2011
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
import com.aimir.dao.prepayment.ChangeCreditTypeWSChangeDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.ContractChangeLogDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.PrepaymentLogDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.PrepaymentProperty;
import com.aimir.model.prepayment.ChangeCreditTypeWSChange;
import com.aimir.model.system.Code;
import com.aimir.model.system.Contract;
import com.aimir.model.system.ContractChangeLog;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.StringUtil;

/**
 * ChangeCreditTypeWS.java Description
 *
 *
 * Date          Version     Author   Description
 * 2011. 7. 18.   v1.0       문동규   선불연계 - 고객의 과금방식 변경 연계
 *
 */

@WebService(serviceName="ChangeCreditTypeWS")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
@Service
public class ChangeCreditTypeWS {

    protected static Log log = LogFactory.getLog(ChangeCreditTypeWS.class);

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
    protected ChangeCreditTypeWSChangeDao changeCreditTypeWSChangeDao;

    @Autowired
    protected PrepaymentLogDao prepaymentLogDao;

    /**
     * method name : change
     * method Desc : 과금방식 변경
     *
     * @param supplierName      Utility ID - 공급사 아이디
     * @param dateTime          Date & Time of request - 현재 날짜
     * @param contractNumber    Contract ID - 고객의 계약번호
     * @param mdsId             Meter Serial Number - 미터 시리얼 번호
     * @param applyDateTime     Date & Time of implementation - 적용 날짜
     * @param paymentMode       Payment Mode - 현재 과금 모드(선불, 후불)
     * @param disconnFunc       Disconnection functionality - enable, disable or no change
     * @param creditDisplayFunc Credit display functionality - enable, disable or no change
     * @param encryptionKey     Encryption Key - 암호화 시 인증 키
     * @return
     * @throws Exception
     */
    @WebMethod
    public @WebResult(name="response") String change(
            @WebParam(name="supplierName") String supplierName,
            @WebParam(name="dateTime") String dateTime,
            @WebParam(name="contractNumber") String contractNumber,
            @WebParam(name="mdsId") String mdsId,
            @WebParam(name="applyDateTime") String applyDateTime,
            @WebParam(name="paymentMode") String paymentMode,
            @WebParam(name="disconnFunc") String disconnFunc,
            @WebParam(name="creditDisplayFunc") String creditDisplayFunc,
            @WebParam(name="encryptionKey") String encryptionKey) throws Exception {

        StringBuilder sb = new StringBuilder();

        sb.append("\n supplierName[" + supplierName + "]");
        sb.append("\n dateTime[" + dateTime + "]");
        sb.append("\n contractNumber[" + contractNumber + "]");
        sb.append("\n mdsId[" + mdsId + "]");
        sb.append("\n applyDateTime[" + applyDateTime + "]");
        sb.append("\n paymentMode[" + paymentMode + "]");
        sb.append("\n disconnFunc[" + disconnFunc + "]");
        sb.append("\n creditDisplayFunc[" + creditDisplayFunc + "]");
        sb.append("\n encryptionKey[" + encryptionKey + "]");

        log.info(sb.toString());

        // mandatory data check
        if (StringUtil.nullToBlank(supplierName).isEmpty() || StringUtil.nullToBlank(dateTime).isEmpty() || 
                StringUtil.nullToBlank(contractNumber).isEmpty() || StringUtil.nullToBlank(mdsId).isEmpty() ||
                StringUtil.nullToBlank(applyDateTime).isEmpty() || StringUtil.nullToBlank(paymentMode).isEmpty() ||
                StringUtil.nullToBlank(disconnFunc).isEmpty() || StringUtil.nullToBlank(creditDisplayFunc).isEmpty() ||
                StringUtil.nullToBlank(encryptionKey).isEmpty()) {
            return "fail : mandatory data is required";
        }

        JpaTransactionManager txManager = null;
        TransactionStatus txStatus = null;

        try {

            txManager = (JpaTransactionManager)DataUtil.getBean("transactionManager");
            txStatus = txManager.getTransaction(null);

            // 웹서비스로그 저장
            ChangeCreditTypeWSChange changeCreditTypeWSChange = new ChangeCreditTypeWSChange();

            changeCreditTypeWSChange.setSupplierName(supplierName);
            changeCreditTypeWSChange.setDateTime(dateTime);
            changeCreditTypeWSChange.setContractNumber(contractNumber);
            changeCreditTypeWSChange.setMdsId(mdsId);
            changeCreditTypeWSChange.setApplyDateTime(applyDateTime);
            changeCreditTypeWSChange.setPaymentMode(paymentMode);
            changeCreditTypeWSChange.setDisconnFunc(disconnFunc);
            changeCreditTypeWSChange.setCreditDisplayFunc(creditDisplayFunc);
            changeCreditTypeWSChange.setEncryptionKey(encryptionKey);
            
            changeCreditTypeWSChangeDao.add(changeCreditTypeWSChange);

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

//            // CommandOperationUtil 생성 후 개발 부분 Start
//            if (meter.getPrepaymentMeter()) { // Prepayment Meter 인 경우 온라인 충전
//
//            } else { // Soft Credit 인 경우 충전
//
//            }
//            // CommandOperationUtil 생성 후 개발 부분 End

            String convPaymentMode = conversionPaymentMode(paymentMode);

            // Insert ContractChangeLog
            addContractChangeLog(contract, applyDateTime, "creditType", contract.getCreditType().getCode(), convPaymentMode);
            
            Code code = codeDao.findByCondition("code", convPaymentMode);

            // Update Contract
            contract.setCreditType(code);
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
     * method name : conversionPaymentMode
     * method Desc : Vending System 에서 온 code 를 Aimir System code 로 변환
     *
     * @param paymentMode   Payment Mode - 현재 과금 모드 (선불, 후불)
     * @return
     */
    private String conversionPaymentMode(String paymentMode) {
        String rtn = null;
        
        if (paymentMode.equals(PrepaymentProperty.getProperty("vending.credittype.postpay"))) {
            rtn = "2.2.0";  // postpay
        } else if (paymentMode.equals(PrepaymentProperty.getProperty("vending.credittype.prepay"))) {
            rtn = "2.2.1";  // prepay
        } else if (paymentMode.equals(PrepaymentProperty.getProperty("vending.credittype.emergency"))) {
            rtn = "2.2.2";  // emergency
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
