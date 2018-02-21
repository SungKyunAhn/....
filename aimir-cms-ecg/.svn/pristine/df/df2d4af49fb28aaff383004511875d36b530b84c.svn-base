package com.aimir.cms.validator;

import com.aimir.cms.constants.CMSConstants.ErrorType;
import com.aimir.cms.exception.CMSException;
import com.aimir.cms.model.AuthCred;
import com.aimir.cms.model.DebtEnt;

public class UpdateDebtReqParameterValidator {

    public static void validator( AuthCred authCred, DebtEnt debtEnt)
			 throws com.aimir.cms.exception.CMSException {    
    	
    	if(debtEnt == null){
    		throw new CMSException(ErrorType.Error.getIntValue(), "DebtEnt is null");    		
    	}
    	
    	if(debtEnt.getCustomerId() == null || "".equals(debtEnt.getCustomerId())){
    		throw new CMSException(ErrorType.Error.getIntValue(), "CustomerID is empty");    
    	}
    	if(debtEnt.getDebtType() == null || "".equals(debtEnt.getDebtType())){
    		throw new CMSException(ErrorType.Error.getIntValue(), "Debt_type is empty"); 
    	}
    	if(debtEnt.getDebtStatus() == null || "".equals(debtEnt.getDebtStatus())){
            throw new CMSException(ErrorType.Error.getIntValue(), "Debt_status is empty"); 
        }
    	if (debtEnt.getDebtAmount() == null)
    	    debtEnt.setDebtAmount(0.0);
    	
    	if (debtEnt.getDebtRef() == null || "".equals(debtEnt.getDebtRef()))
    	    throw new CMSException(ErrorType.Error.getIntValue(), "Debt_ref is empty");
    	
    	if(debtEnt.getInstallmentDueDate() == null || "".equals(debtEnt.getInstallmentDueDate())){
            throw new CMSException(ErrorType.Error.getIntValue(), "installment_no is empty"); 
        }
    	/*
    	if(debtEnt.getDebtAmount() == null){
    		throw new CMSException("Debt_amount is empty"); 
    	*/
    }
	
}
