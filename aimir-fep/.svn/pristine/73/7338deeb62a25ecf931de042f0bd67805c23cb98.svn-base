package com.aimir.fep.meter.prepaymentForSA.ws;

import java.util.ArrayList;
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

import com.aimir.dao.prepayment.VerifyPrepaymentCustomerWSDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.CustomerDao;
import com.aimir.fep.meter.prepaymentForSA.response.CommonResponseOfWS;
import com.aimir.fep.meter.prepaymentForSA.response.CustomerInformation;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.prepayment.VerifyPrepaymentCustomerWS;
import com.aimir.model.system.Code;
import com.aimir.model.system.Contract;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeUtil;

@WebService(serviceName="VerifyPrepaymentCustomerForSAWS")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
@Service
public class VerifyPrepaymentCustomerForSAWS {
	
    protected static Log log = LogFactory.getLog(VerifyPrepaymentCustomerForSAWS.class);

    @Autowired
    protected ContractDao contractDao;
    
    @Autowired
    protected CustomerDao customerDao;
   
    @Autowired
    protected VerifyPrepaymentCustomerWSDao verifyPrepaymentCustomerWSDao;

    /**
     * method name : verify<br/>
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
    public @WebResult(name="response") CustomerInformation verify(
    		@WebParam(name="supplierName") String supplierName, 
            @WebParam(name="customerNumber") String customerNumber,
            @WebParam(name="transactionId") String transactionId) throws Exception {

        Contract contract = null;
        StringBuilder sb = new StringBuilder();
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        CustomerInformation customerInformation = new CustomerInformation();
        CommonResponseOfWS commonResponseOfWS = new CommonResponseOfWS();
        sb.append("\nRequest data : supplier Name[" + supplierName + "]");
        sb.append("\nRequest data : customer Number[" + customerNumber + "]");
        sb.append("\nRequest data : transaction ID[" + transactionId + "]");

        log.info(sb.toString());

        // mandatory data check
        if(StringUtil.nullToBlank(supplierName).isEmpty()) {
        	commonResponseOfWS.setRtnStatus(false);
        	commonResponseOfWS.setTransactionId(transactionId);
        	commonResponseOfWS.setErrorCode("ER100");
        	commonResponseOfWS.setErrorDescription("Supplier Name is null.");
        	customerInformation.setCommonResponseOfWS(commonResponseOfWS);
        	return customerInformation;
        }else if(StringUtil.nullToBlank(customerNumber).isEmpty()) {
        	commonResponseOfWS.setRtnStatus(false);
        	commonResponseOfWS.setTransactionId(transactionId);
        	commonResponseOfWS.setErrorCode("ER100");
        	commonResponseOfWS.setErrorDescription("Customer Number is null.");
        	customerInformation.setCommonResponseOfWS(commonResponseOfWS);
        	return customerInformation;
        }

        // if transaction id is not empty transaction ID check if the transaction id is the number or not
        if(!StringUtil.nullToBlank(transactionId).isEmpty() && !StringUtil.isDigit(transactionId)) {
        	commonResponseOfWS.setRtnStatus(false);
        	commonResponseOfWS.setTransactionId(transactionId);
        	commonResponseOfWS.setErrorCode("ER110");
        	commonResponseOfWS.setErrorDescription("You must enter only numbers(0-9) in the transaction ID.");
        	customerInformation.setCommonResponseOfWS(commonResponseOfWS);
        	return customerInformation;
        }

        JpaTransactionManager txManager = null;
        TransactionStatus txStatus = null;
        
        try {
            txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");
            txStatus = txManager.getTransaction(null);
            
        	List<Contract> list = contractDao.getContractIdByCustomerNo(customerNumber, supplierName);

        	if(list.size() == 0) {
            	commonResponseOfWS.setRtnStatus(false);
            	commonResponseOfWS.setTransactionId(transactionId);
            	commonResponseOfWS.setErrorCode("ER103");
            	commonResponseOfWS.setErrorDescription("The requested Contract Info does not exist.");
            	customerInformation.setCommonResponseOfWS(commonResponseOfWS);
            	return customerInformation;
        	}

        	// 고객 이름 취득
        	String customerName = list.get(0).getCustomer().getName();
 
        	List<String> contractList = new ArrayList<String>();
        	for(Object obj : list) {
        		String contractNumber = ((Contract)obj).getContractNumber();

                // 계약 정보 존재 체크
                conditionMap.put("contractNumber", contractNumber);
                conditionMap.put("supplierName", supplierName);

                // select Contract
                List<Contract> listCont = contractDao.getPrepaymentContract(conditionMap);

                if (listCont != null && listCont.size() > 0) { // 계약 정보 유
                    contract = listCont.get(0);

                    // 후불 고객인지 체크
                    if(Code.POSTPAY.equals(contract.getCreditType().getCode())) {
                    	commonResponseOfWS.setRtnStatus(false);
                    	commonResponseOfWS.setTransactionId(transactionId);
                    	commonResponseOfWS.setErrorCode("ER108");
                    	commonResponseOfWS.setErrorDescription("This Customer doesn't have a prepayment contract.");
                    	customerInformation.setCommonResponseOfWS(commonResponseOfWS);
                    	continue;
                    	//return customerInformation;
                    }

                    // 해지된 고객인지 체크
                    if("2.1.3".equals(contract.getStatus().getCode())) {
                    	commonResponseOfWS.setRtnStatus(false);
                    	commonResponseOfWS.setTransactionId(transactionId);
                    	commonResponseOfWS.setErrorCode("ER107");
                    	commonResponseOfWS.setErrorDescription("ContractNumber : [" + contractNumber + "] " + "This Customer has a terminated contract.");
                    	customerInformation.setCustomerNumber(customerNumber);
                    	customerInformation.setCommonResponseOfWS(commonResponseOfWS);
                    	continue;
                    	//return customerInformation;
                    }
                    // 웹서비스로그 저장
                    VerifyPrepaymentCustomerWS verifyPrepaymentCustomerWS = new VerifyPrepaymentCustomerWS();
                    verifyPrepaymentCustomerWS.setSupplierName(supplierName);
                    verifyPrepaymentCustomerWS.setCustomerNumber(customerNumber);
                    verifyPrepaymentCustomerWS.setTransactionId(transactionId);
                    verifyPrepaymentCustomerWS.setWriteDate(TimeUtil.getCurrentTimeMilli());
                    verifyPrepaymentCustomerWSDao.add(verifyPrepaymentCustomerWS);

                    log.info("\nReturn Data: contract Number : [" + contractNumber + "]");
                    contractList.add(contractNumber);

                } else {// 계약 정보 무
                	commonResponseOfWS.setRtnStatus(false);
                	commonResponseOfWS.setTransactionId(transactionId);
                	commonResponseOfWS.setErrorCode("ER103");
                	commonResponseOfWS.setErrorDescription("The requested Contract Info does not exist.");
                	customerInformation.setCommonResponseOfWS(commonResponseOfWS);
                	return customerInformation;
                }
        	}
 
        	if(contractList.size() !=0 ) {
        		commonResponseOfWS.setRtnStatus(true);
        		commonResponseOfWS.setTransactionId(transactionId);
            	commonResponseOfWS.setErrorCode("");
            	commonResponseOfWS.setErrorDescription("");
            	customerInformation.setCommonResponseOfWS(commonResponseOfWS);
            	customerInformation.setCustomerName(customerName);
            	customerInformation.setContractNumber(contractList.get(0));
            	log.info("\nReturn Data: customer Name : [" + customerName + "]");
        	}
 
        	txManager.commit(txStatus);
        	
        	return customerInformation;

        } catch (Exception e) {
            log.error(e, e);
            
            if (txManager != null) {
                try {
                    txManager.rollback(txStatus);
                }
                catch (Exception t) {}
            }
        	commonResponseOfWS.setRtnStatus(false);
        	commonResponseOfWS.setTransactionId(transactionId);
        	commonResponseOfWS.setErrorCode("EXCEPTION");
        	commonResponseOfWS.setErrorDescription(e.getMessage());
        	customerInformation.setCommonResponseOfWS(commonResponseOfWS);
        	return customerInformation;
        }
    }
}
