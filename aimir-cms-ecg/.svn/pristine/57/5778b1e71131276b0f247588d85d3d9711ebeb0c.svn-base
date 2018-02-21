package com.aimir.cms.validator;

import com.aimir.cms.constants.CMSConstants.ErrorType;
import com.aimir.cms.exception.CMSException;
import com.aimir.cms.model.AuthCred;
import com.aimir.cms.model.MeterEnt;

public class MeterImportReqParameterValidator {

    public static void validator( AuthCred authCred, MeterEnt meterEnt)
			 throws com.aimir.cms.exception.CMSException {   	
    	
    	if(meterEnt == null){
    		throw new CMSException(ErrorType.Error.getIntValue(), "MeterEnt is null");    		
    	}
    	
    	if(meterEnt.getMeterSerialNo() == null || "".equals(meterEnt.getMeterSerialNo())){
    		throw new CMSException(ErrorType.Error.getIntValue(), "Meter_serial_no is empty");    
    	}
    	
    	if(meterEnt.getMake() == null || "".equals(meterEnt.getMake())){
    		throw new CMSException(ErrorType.Error.getIntValue(), "Meter Make is empty"); 
    	}
    	else {
    	    if (meterEnt.getMake().equals("MC013")) {
    	        if (meterEnt.getModel() != null && 
    	                (meterEnt.getModel().equals("ML001") 
    	                        || meterEnt.getModel().equals("ML002") 
    	                        || meterEnt.getModel().equals("ML053")
    	                        || meterEnt.getModel().equals("ML058")
    	                        || meterEnt.getModel().equals("ML046")
    	                        || meterEnt.getModel().equals("ML059"))) {
    	            
    	        }
    	        else {
    	            throw new CMSException(ErrorType.Error.getIntValue(), "Invalid Meter Make or Model"); 
    	        }
    	    }
    	    else {
    	        throw new CMSException(ErrorType.Error.getIntValue(), "Invalid Meter Make or Model"); 
    	    }
    	}
    	
    	if(meterEnt.getBatchNo() == null || "".equals(meterEnt.getBatchNo())){
    		throw new CMSException(ErrorType.Error.getIntValue(), "Batch_no is empty"); 
    	}

    }
	
}
