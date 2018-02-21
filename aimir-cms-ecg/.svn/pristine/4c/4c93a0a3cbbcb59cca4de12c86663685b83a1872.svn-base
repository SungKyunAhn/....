package com.aimir.cms.validator;

import com.aimir.cms.constants.CMSConstants.ErrorType;
import com.aimir.cms.exception.CMSException;
import com.aimir.cms.model.AuthCred;
import com.aimir.cms.model.DebtEnt;

public class AddDebtReqParameterValidator {

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
    	else {
    	    if (debtEnt.getDebtType().equals("CD001")
    	            || debtEnt.getDebtType().equals("CD002") 
    	            || debtEnt.getDebtType().equals("CD003") 
    	            || debtEnt.getDebtType().equals("CD004"))
    	    {
    	        
    	    }
    	    else
    	    {
    	        throw new CMSException(ErrorType.Error.getIntValue(), "Invalid Debt Category"); 
    	    }
    	}
    	
    	if(debtEnt.getDebtAmount() == null){
    		debtEnt.setDebtAmount(0.0);
    	}
    	
    	if(debtEnt.getDebtRef() == null) {
    	    throw new CMSException(ErrorType.Error.getIntValue(), "Debt_ref is empty");
    	}
    	
    	if(debtEnt.getInstallmentDueDate() == null){
    		throw new CMSException(ErrorType.Error.getIntValue(), "installment_due_date is empty"); 
    	}
    	
    	if(debtEnt.getDebtStatus() == null || "".equals(debtEnt.getDebtStatus())){
    		throw new CMSException(ErrorType.Error.getIntValue(), "Debt_status is empty"); 
    	}

    }
	
}
