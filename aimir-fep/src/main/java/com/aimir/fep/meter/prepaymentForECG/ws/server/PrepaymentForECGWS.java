package com.aimir.fep.meter.prepaymentForECG.ws.server;

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

import com.aimir.fep.meter.prepaymentForECG.mbean.PrepaymentForECGMBean;
import com.aimir.fep.meter.prepaymentForECG.response.BalanceInformation;
import com.aimir.fep.meter.prepaymentForECG.response.CommonResponseOfWS;
import com.aimir.fep.meter.prepaymentForECG.response.CustomerInformation;
import com.aimir.fep.meter.prepaymentForECG.response.VendorInformation;
import com.aimir.fep.meter.prepaymentForECG.response.VendorPaymentInformation;
 
@WebService(serviceName="PrepaymentForECGWS")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
@Service(value="prepaymentForECGWS")
public class PrepaymentForECGWS {
	
	protected static Log log = LogFactory.getLog(PrepaymentForECGWS.class);
	
	@Autowired 
	PrepaymentForECGMBean prepaymentForECGMBean;
    
	/**
     * method name : CommonResponseOfWS<br/>
     * method Desc : 과금 충전 시 연계
     *
     * @param supplierName      Utility ID - 공급사 아이디
     * @param dateTime          Date & Time - 충전 날짜
     * @param mdsId             Meter Serial Number - 미터 시리얼 번호
     * @param amount            Amount - 충전액
     * @param transactionId    Transaction ID - 처리 아이디
     *                  
     * @return
     * @throws Exception
     */
    @WebMethod() 
    public @WebResult(name="response") CommonResponseOfWS addBalanceForECGWS(
    		@WebParam(name="supplierName") String supplierName, 
            @WebParam(name="dateTime") String dateTime, 
            @WebParam(name="mdsId") String mdsId, 
            @WebParam(name="amount") String amountStr, 
            @WebParam(name="transactionId") String transactionId) throws Exception {
    	Double amount = 0d;
    	
    	if ( amountStr != null 	) {
    		try {
    			amount = Double.parseDouble(amountStr);
    		} catch ( Exception e ) {
    			amount = 0d;
    			//e.printStackTrace();
    			log.warn("amountStr is empty. insert zero.");
    		}
    	}
    	CommonResponseOfWS ret = prepaymentForECGMBean.charging(supplierName, 
    			dateTime, 
    			mdsId, 
    			amount, 
    			transactionId);
    	 
    	return ret;
    }
    
    /**
     * method name : getBalanceForSAWS
     * method Desc : 현재 잔액 정보 요청
     * 
     * @param supplierName      Utility ID - 공급사 아이디
     * @param mdsId             Meter Serial Number - 미터 시리얼 번호
     * @param transactionId    Transaction ID - 처리 아이디 
     * @return supplierName     Utility ID - 공급사 아이디
     *         mdsId            Meter Serial Number - 미터 시리얼 번호
     *         paymentMode      Payment Mode - 현재 과금 모드 (선불, 후불, Emergency Credit)
     *         currentCredit    Credit Status - 현재 잔액
     *         emergencyYn      Emergency Credit Available - Emergency credit 가능여부
     *         remainingDate    Credit date remaning - 현재 잔액으로 사용 가능한 일수
     *         switchStatus     Supply Status - Relay switch 상태 (공급차단/재개)
     * @throws Exception
     */
    @WebMethod
    public @WebResult(name="response") BalanceInformation getBalanceForECGWS(
            @WebParam(name="supplierName") String supplierName,
            @WebParam(name="mdsId") String mdsId,
            @WebParam(name="transactionId") String transactionId) throws Exception {
    	BalanceInformation ret = prepaymentForECGMBean.getInfo(supplierName, 
    			mdsId, 
    			transactionId);
    	
    	return ret;
    }
    
    /**
     * method name : verifyPrepaymentCustomerForSAWS<br/>
     * method Desc : 고객 인증
     *
     * @param supplierName     Utility ID - 공급사 아이디
     * @param mdsId   			 meter ID - 미터아이디
     * @param transactionId    Transaction ID - 처리 아이디
     *   
     * @return
     * @throws Exception
     */
    @WebMethod
    public @WebResult(name="response") CustomerInformation verifyPrepaymentCustomerForECGWS(
    		@WebParam(name="supplierName") String supplierName, 
            @WebParam(name="mdsId") String mdsId,
            @WebParam(name="transactionId") String transactionId) throws Exception {
    	CustomerInformation ret = prepaymentForECGMBean.verify(supplierName, 
    			mdsId, 
    			transactionId);
    	
    	return ret;
    }
    
    /**
     * method name : getVendorDetail<br/>
     * method Desc : 고객 인증
     *
     * @param username     	cashier Id
     * @param password      password
     *   
     * @return
     * @throws Exception
     */
    @WebMethod
    public @WebResult(name="response") VendorInformation getVendorDetailForECGWS(
    		@WebParam(name="username") String username, 
            @WebParam(name="password") String password) throws Exception {
    	VendorInformation ret = prepaymentForECGMBean.getVendorDetail(username, password);
    	
    	return ret;
    }
    
    /**
     * method name : getVendorMakePayment<br/>
     * method Desc : 고객 인증
     *
     * @param supplierName     Utility ID - 공급사 아이디
     * @param mdsId   	   mdsId - 미터 아이디
     * @param transactionId    Transaction ID - 처리 아이디
     *   
     * @return
     * @throws Exception
     */
    @WebMethod
    public @WebResult(name="response") VendorPaymentInformation getVendorMakePaymentForECGWS( 
            @WebParam(name="mdsId") String mdsId,
            @WebParam(name="amountPayable") Double amountPayable,
            @WebParam(name="userName")  String userName,
            @WebParam(name="vendorAccount") String vendorAccount) throws Exception {
    	VendorPaymentInformation ret = prepaymentForECGMBean.getVendorMakePayment(mdsId,amountPayable,userName,vendorAccount);
    	
    	return ret;
    }
}
