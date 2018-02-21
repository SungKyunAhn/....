package com.aimir.cms.validator;

import com.aimir.cms.exception.CMSException;
import com.aimir.cms.model.AuthCred;
import com.aimir.cms.model.DebtEnt;

public class GetDebtReqParameterValidator {

    public static void validator( AuthCred authCred, DebtEnt debtEnt)
			 throws com.aimir.cms.exception.CMSException {    
    	
        if(authCred == null) {
            throw new CMSException("AuthCred is null");
        }
        
        if (authCred.getUserName() == null) {
            throw new CMSException("UserName is empty");
        }
        
        if (authCred.getPassword() == null) {
            throw new CMSException("Password is empty");
        }
        
        if (authCred.getMessageId() == null) {
            throw new CMSException("MessageID is empty");
        }
        
        if (authCred.getMessageTimestamp() == null) {
            throw new CMSException("MessageTimestamp is empty");
        }
        
        if (authCred.getClientId() == null) {
            throw new CMSException("ClientID is empty");
        }
        
    	if(debtEnt == null){
    		throw new CMSException("DebtEnt is null");    		
    	}
    	
    	if(debtEnt.getCustomerId() == null || "".equals(debtEnt.getCustomerId())){
    		throw new CMSException("CustomerID is empty");    
    	}
    	
    	/*
    	if(debtEnt.getDebtType() == null || "".equals(debtEnt.getDebtType())){
    		throw new CMSException("Debt_type is empty"); 
    	}
    	
    	if(debtEnt.getDebtAmount() == null){
    		throw new CMSException("Debt_amount is empty"); 
    	}
    	
    	if(debtEnt.getInstallmentNo() == null){
    		throw new CMSException("installment_no is empty"); 
    	}
    	
    	if(debtEnt.getDebtStatus() == null || "".equals(debtEnt.getDebtStatus())){
    		throw new CMSException("Debt_status is empty"); 
    	}
    	*/
    }
	
}
