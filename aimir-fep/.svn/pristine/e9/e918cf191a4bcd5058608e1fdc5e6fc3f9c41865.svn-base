package com.aimir.fep.meter.prepaymentForSA.ws.server;

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
import org.springframework.stereotype.Service;

import com.aimir.fep.meter.prepaymentForSA.mbean.PrepaymentForSAMBean;
import com.aimir.fep.meter.prepaymentForSA.response.BalanceInformation;
import com.aimir.fep.meter.prepaymentForSA.response.CommonResponseOfWS;
import com.aimir.fep.meter.prepaymentForSA.response.CustomerInformation;
import com.aimir.fep.meter.prepaymentForSA.response.VendorInformation;
import com.aimir.fep.meter.prepaymentForSA.response.VendorPaymentInformation;
 
@WebService(serviceName="PrepaymentForSAWS")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
@Service(value="prepaymentForSAWS")
public class PrepaymentForSAWS {
	
	protected static Log log = LogFactory.getLog(PrepaymentForSAWS.class);
	
	@Autowired 
	PrepaymentForSAMBean prepaymentForSAMBean;
    
	/**
     * method name : CommonResponseOfWS<br/>
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
    @WebMethod() 
    public @WebResult(name="response") CommonResponseOfWS addBalanceForSAWS(
    		@WebParam(name="supplierName") String supplierName, 
            @WebParam(name="dateTime") String dateTime, 
            @WebParam(name="contractNumber") String contractNumber,
            @WebParam(name="mdsId") String mdsId, 
            @WebParam(name="accountId") String accountId,
            @WebParam(name="amount") String amountStr, 
            @WebParam(name="powerLimit") String powerLimitStr,
            @WebParam(name="tariffCode") Integer tariffCode,
            @WebParam(name="source") String source, 
            @WebParam(name="encryptionKey") String encryptionKey,
            @WebParam(name="authCode") String authCode,
            @WebParam(name="municipalityCode") String municipalityCode,
            @WebParam(name="transactionId") String transactionId) throws Exception {
    	Double powerLimit = 0d;
    	Double amount = 0d;
    	if ( powerLimitStr != null  ) {
    		try {
    			powerLimit = Double.parseDouble(powerLimitStr);
    		} catch ( Exception e) {
    			powerLimit = 0d;
    			//e.printStackTrace();
    			log.warn("power limit empty. insert zero.");
    		}
    	}
    	
    	if ( amountStr != null 	) {
    		try {
    			amount = Double.parseDouble(amountStr);
    		} catch ( Exception e ) {
    			amount = 0d;
    			//e.printStackTrace();
    			log.warn("amountStr is empty. insert zero.");
    		}
    	}
    	CommonResponseOfWS ret = prepaymentForSAMBean.charging(supplierName, 
    			dateTime, 
    			contractNumber, 
    			mdsId, 
    			accountId, 
    			amount, 
    			powerLimit,
    			tariffCode,
    			source, 
    			encryptionKey, 
    			authCode, 
    			municipalityCode, 
    			transactionId);
    	 
    	return ret;
    }
    
    /**
     * method name : getBalanceForSAWS
     * method Desc : 현재 잔액 정보 요청
     * 
     * @param supplierName      Utility ID - 공급사 아이디
     * @param contractNumber    Contract ID - 고객의 계약번호
     * @param mdsId             Meter Serial Number - 미터 시리얼 번호
     * @param transactionId    Transaction ID - 처리 아이디 
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
    public @WebResult(name="response") BalanceInformation getBalanceForSAWS(
            @WebParam(name="supplierName") String supplierName,
            @WebParam(name="contractNumber") String contractNumber,
            @WebParam(name="mdsId") String mdsId,
            @WebParam(name="transactionId") String transactionId) throws Exception {
    	BalanceInformation ret = prepaymentForSAMBean.getInfo(supplierName, 
    			contractNumber, 
    			mdsId, 
    			transactionId);
    	
    	return ret;
    }
    
    /**
     * method name : verifyPrepaymentCustomerForSAWS<br/>
     * method Desc : 고객 인증
     *
     * @param supplierName     Utility ID - 공급사 아이디
     * @param customerNumber   Customer Number - 고객번호
     * @param transactionId    Transaction ID - 처리 아이디
     *   
     * @return
     * @throws Exception
     */
    @WebMethod
    public @WebResult(name="response") CustomerInformation verifyPrepaymentCustomerForSAWS(
    		@WebParam(name="supplierName") String supplierName, 
            @WebParam(name="customerNumber") String customerNumber,
            @WebParam(name="transactionId") String transactionId) throws Exception {
    	CustomerInformation ret = prepaymentForSAMBean.verify(supplierName, 
    			customerNumber, 
    			transactionId);
    	
    	return ret;
    }
    
    /**
     * method name : getVendorDetail<br/>
     * method Desc : 고객 인증
     *
     * @param terminalId     	Cashier ID
     * @param pinNo   			Cashier Login Password
     *   
     * @return
     * @throws Exception
     */
    @WebMethod
    public @WebResult(name="response") VendorInformation getVendorDetail(
    		@WebParam(name="terminalId") String terminalId, 
            @WebParam(name="pinNo") String pinNo) throws Exception {
    	VendorInformation ret = prepaymentForSAMBean.getVendorDetail(terminalId, pinNo);
    	
    	return ret;
    }
    
    /**
     * method name : getVendorMakePayment<br/>
     * method Desc : 고객 인증
     *
     * @param supplierName     Utility ID - 공급사 아이디
     * @param customerNumber   Customer Number - 고객번호
     * @param transactionId    Transaction ID - 처리 아이디
     *   
     * @return
     * @throws Exception
     */
    @WebMethod
    public @WebResult(name="response") VendorPaymentInformation getVendorMakePayment(
    		@WebParam(name="customerAccount") String customerAccount, 
            @WebParam(name="customerBarcode") String CustomerBarcode,
            @WebParam(name="amountPayable") Double amountPayable,
            @WebParam(name="cashierId")  String cashierId,
            @WebParam(name="vendorAccount") String vendorAccount) throws Exception {
    	VendorPaymentInformation ret = prepaymentForSAMBean.getVendorMakePayment(customerAccount, CustomerBarcode,amountPayable,cashierId,vendorAccount);
    	
    	return ret;
    }
}
