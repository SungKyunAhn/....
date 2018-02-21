/**
 * GetBalanceWS.java Copyright NuriTelecom Limited 2011
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

import net.sf.json.JSONObject;

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
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.PrepaymentLogDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.PrepaymentProperty;
import com.aimir.model.prepayment.GetBalanceWSGetHistory;
import com.aimir.model.prepayment.GetBalanceWSGetInfo;
import com.aimir.model.system.Contract;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.StringUtil;

/**
 * GetBalanceWS.java Description
 *
 *
 * Date          Version     Author   Description
 * 2011. 7. 22.  v1.0        문동규   선불연계 - 잔액 및 사용량 정보 취득
 *
 */

@WebService(serviceName="GetBalanceWS")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
@Service
public class GetBalanceWS {

    protected static Log log = LogFactory.getLog(GetBalanceWS.class);

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

    /**
     * method name : getInfo
     * method Desc : 현재 잔액 정보 요청
     * 
     * @param supplierName      Utility ID - 공급사 아이디
     * @param contractNumber    Contract ID - 고객의 계약번호
     * @param mdsId             Meter Serial Number - 미터 시리얼 번호
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
    public @WebResult(name="response") HashMap<String, Object> getInfo(
            @WebParam(name="supplierName") String supplierName,
            @WebParam(name="contractNumber") String contractNumber,
            @WebParam(name="mdsId") String mdsId) throws Exception {

        HashMap<String, Object> rtnMap = new HashMap<String, Object>();
        Map<String, Object> map = new HashMap<String, Object>();
        StringBuilder sb = new StringBuilder();

        sb.append("\n supplierName[" + supplierName + "]");
        sb.append("\n contractNumber[" + contractNumber + "]");
        sb.append("\n mdsId[" + mdsId + "]");

        log.info(sb.toString());

        // mandatory data check
        if (StringUtil.nullToBlank(supplierName).isEmpty() || StringUtil.nullToBlank(contractNumber).isEmpty() || 
                StringUtil.nullToBlank(mdsId).isEmpty()) {
            rtnMap.put("result", "fail : mandatory data is required");
            return rtnMap;
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

            getBalanceWSGetInfoDao.add(getBalanceWSGetInfo);

            Map<String, Object> conditionMap = new HashMap<String, Object>();

            conditionMap.put("contractNumber", contractNumber);
            conditionMap.put("supplierName", supplierName);
            conditionMap.put("mdsId", mdsId);

            List<Map<String, Object>> listCont = contractDao.getPrepaymentContractBalanceInfo(conditionMap);

            if (listCont != null && listCont.size() > 0) {
                map = listCont.get(0);
                rtnMap.put("result", "success");
            } else {
                rtnMap.put("result", "fail : invalid contract info");
                txManager.commit(txStatus);
                return rtnMap;
            }

            String reconvPaymentMode = reconversionPaymentMode((String)map.get("creditTypeCode"));
            map.put("paymentMode", reconvPaymentMode);
            // TODO - 은미애 과장님 함수 호출하여 가져옴
            map.put("remainingDate", 0);

            switch((Integer)map.get("switchStatus")) {
                case 0 :
                    map.put("switchStatus", CircuitBreakerStatus.Deactivation.name());
                    break;

                case 1 :
                    map.put("switchStatus", CircuitBreakerStatus.Activation.name());
                    break;

                case 2 :
                    map.put("switchStatus", CircuitBreakerStatus.Standby.name());
                    break;
            }

            rtnMap.put("data", map);
            txManager.commit(txStatus);

            JSONObject jsonObject = JSONObject.fromObject(rtnMap);
            log.info("Return Data:" + jsonObject.toString());
            
            return rtnMap;
        } catch (Exception e) {

            if (txManager != null) {
                txManager.rollback(txStatus);
            }

            e.printStackTrace();

            log.error(e);

            rtnMap.put("result", "fail : " + e.getMessage());

            return rtnMap;
        }
    }

    /**
     * method name : getHistory
     * method Desc : 잔액 충전 내역 조회
     *
     * @param supplierName      Utility ID - 공급사 아이디
     * @param contractNumber    Contract ID - 고객의 계약번호
     * @return list :
     *           (공통 Data)
     *           supplierName      Utility ID - 공급사 아이디
     *           dateTime          Date & Time of snapshot - 현재 날짜
     *           contractNumber    Contract ID - 고객의 계약번호
     *           mdsId             Meter Serial Number - 미터 시리얼 번호
     *           paymentMode       Payment Mode - 현재 과금 모드 (선불, 후불, Emergency Credit)
     *           currentCredit     Credit Status - 현재 잔액
     *           emergencyYn       Emergency Credit Available - Emergency credit 가능여부
     *           creditStatus      Low credit status Y/N - low credit or not
     *           remainingDate     Credit date remaning - 현재 잔액으로 사용 가능한 일수
     *           switchStatus      Supply Status - Relay switch 상태 (공급차단/재개)
     *           (History Data 최근 5건)
     *           lastTokenDate     Credit Date & Time - 충전 날짜
     *           lastTokenId       Account ID - 충전 아이디
     *           chargedCredit     Amount - 충전액
     *           powerLimit        Power Limit(kWh) - 전력 사용량
     *           keyType           source - 충전 방법 (온라인 결제, 카드 등등)
     * @throws Exception
     */
    @WebMethod
    public @WebResult(name="response") HashMap<String, Object> getHistory(
            @WebParam(name="supplierName") String supplierName,
            @WebParam(name="contractNumber") String contractNumber) throws Exception {
        HashMap<String, Object> rtnMap = new HashMap<String, Object>();
//        List<Map<String, Object>> rtnList = new ArrayList<Map<String, Object>>();
//        Map<String, Object> map = new HashMap<String, Object>();
        StringBuilder sb = new StringBuilder();

        sb.append("\n supplierName[" + supplierName + "]");
        sb.append("\n contractNumber[" + contractNumber + "]");

        log.info(sb.toString());

        // mandatory data check
        if (StringUtil.nullToBlank(supplierName).isEmpty() || StringUtil.nullToBlank(contractNumber).isEmpty()) {
            rtnMap.put("result", "fail : mandatory data is required");
            return rtnMap;
        }

        JpaTransactionManager txManager = null;
        TransactionStatus txStatus = null;

        try {

            txManager = (JpaTransactionManager)DataUtil.getBean("transactionManager");
            txStatus = txManager.getTransaction(null);

            // 웹서비스로그 저장
            GetBalanceWSGetHistory getBalanceWSGetHistory = new GetBalanceWSGetHistory();
            getBalanceWSGetHistory.setSupplierName(supplierName);
            getBalanceWSGetHistory.setContractNumber(contractNumber);
            
            getBalanceWSGetHistoryDao.add(getBalanceWSGetHistory);
            
            Contract contract = null;

            Map<String, Object> conditionMap = new HashMap<String, Object>();

            conditionMap.put("contractNumber", contractNumber);
            conditionMap.put("supplierName", supplierName);

            List<Contract> listCont = contractDao.getPrepaymentContract(conditionMap);

            if (listCont != null && listCont.size() > 0) {
                contract = listCont.get(0);
                rtnMap.put("result", "success");
            } else {
                rtnMap.put("result", "fail : invalid contract info");
                txManager.commit(txStatus);
                return rtnMap;
            }

            // Select PrepaymentLog
//            List<PrepaymentLog> logList = getPrepaymentLogList(contract);
            
            List<Map<String, Object>> logList = prepaymentLogDao.getPrepaymentChargeHistoryList(conditionMap);

            String dateTime = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");
            String reconvPaymentMode = reconversionPaymentMode(contract.getCreditType().getCode());

            for (Map<String, Object> map : logList) {
                map.put("dateTime", dateTime);
                map.put("paymentMode", reconvPaymentMode);
                // TODO - 은미애 과장님 함수 호출하여 가져옴
                map.put("remainingDate", 0);

                switch((Integer)map.get("switchStatus")) {
                    case 0 :
                        map.put("switchStatus", CircuitBreakerStatus.Deactivation.name());
                        break;

                    case 1 :
                        map.put("switchStatus", CircuitBreakerStatus.Activation.name());
                        break;

                    case 2 :
                        map.put("switchStatus", CircuitBreakerStatus.Standby.name());
                        break;
                }
            }

            rtnMap.put("data", logList);
            txManager.commit(txStatus);

            JSONObject jsonObject = JSONObject.fromObject(rtnMap);
            log.info("Return Data:" + jsonObject.toString());

            return rtnMap;
        } catch (Exception e) {

            if (txManager != null) {
                txManager.rollback(txStatus);
            }

            e.printStackTrace();

            log.error(e);

            rtnMap.put("result", "fail : " + e.getMessage());
            return rtnMap;
        }
    }

    /**
     * method name : reconversionPaymentMode
     * method Desc : Aimir System code 를 Vending System code 로 변환
     *
     * @param creditTypeCode    현재 과금 모드 (선불, 후불, Emergency Credit)
     * @return
     */
    private String reconversionPaymentMode(String creditTypeCode) {
        String rtn = null;
        
        if (creditTypeCode.equals("2.2.0")) {   // postpay
            rtn = PrepaymentProperty.getProperty("vending.credittype.postpay");
        } else if (creditTypeCode.equals("2.2.1")) {    // prepay
            rtn = PrepaymentProperty.getProperty("vending.credittype.prepay");
        } else if (creditTypeCode.equals("2.2.2")) {    // emergency
            rtn = PrepaymentProperty.getProperty("vending.credittype.emergency");
        }

        return rtn;
    }

    /**
     * method name : getPrepaymentLogList
     * method Desc : PrepaymentLog 데이터를 최근 5건 조회.
     *
     * @param contract
     * @return
     */
//    private List<PrepaymentLog> getPrepaymentLogList(Contract contract) {
//        List<PrepaymentLog> logList = null;
//
//        Set<Condition> setLog = new HashSet<Condition>();
//        Condition conditionLog = new Condition();
//        conditionLog.setField("contract");
//        conditionLog.setValue(new Object[] { "c" });
//        conditionLog.setRestrict(Restriction.ALIAS);
//        setLog.add(conditionLog);
//
//        conditionLog = new Condition();
//        conditionLog.setField("customer");
//        conditionLog.setValue(new Object[] { "s" });
//        conditionLog.setRestrict(Restriction.ALIAS);
//        setLog.add(conditionLog);
//
//        conditionLog = new Condition();
//        conditionLog.setField("c.id");
//        conditionLog.setValue(new Object[]{contract.getId()});
//        conditionLog.setRestrict(Restriction.EQ);
//        setLog.add(conditionLog);
//        
//        conditionLog = new Condition();
//        conditionLog.setField("s.id");
//        conditionLog.setValue(new Object[]{contract.getCustomer().getId()});
//        conditionLog.setRestrict(Restriction.EQ);
//        setLog.add(conditionLog);
//
//        conditionLog = new Condition();
//        conditionLog.setField("lastTokenDate");
//        conditionLog.setRestrict(Restriction.ORDERBYDESC);
//        setLog.add(conditionLog);
//
//        conditionLog = new Condition();
//        conditionLog.setValue(new Object[]{0});
//        conditionLog.setRestrict(Restriction.FIRST);
//        setLog.add(conditionLog);
//
//        conditionLog = new Condition();
//        conditionLog.setValue(new Object[]{5});
//        conditionLog.setRestrict(Restriction.MAX);
//        setLog.add(conditionLog);
//
//        logList = prepaymentLogDao.findByConditions(setLog);
//
//        return logList;
//    }
}