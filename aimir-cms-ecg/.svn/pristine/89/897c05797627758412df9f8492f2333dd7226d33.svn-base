package com.aimir.cms.validator;

import com.aimir.cms.exception.CMSException;
import com.aimir.cms.model.AuthCred;
import com.aimir.cms.model.CMSEnt;

public class DataLoadReqParameterValidator {

    public static void validator( AuthCred authCred, CMSEnt cmsEnt)
			 throws com.aimir.cms.exception.CMSException {       	
    	
    	if(cmsEnt.getCustomer() == null && cmsEnt.getSerivcePoint() == null){
    		throw new CMSException("DataItem is empty");   
    	}
    	
    	if(cmsEnt.getCustomer() != null ){
    		
	    	if(cmsEnt.getCustomer().getCustomerId() == null 
	    			|| "".equals(cmsEnt.getCustomer().getCustomerId())){
	    		throw new CMSException("Customer Id is empty"); 
	    	}
    	}
    	
    	if(cmsEnt.getSerivcePoint() != null){
    		if(cmsEnt.getSerivcePoint().getServPointId() == null
    				|| "".equals(cmsEnt.getSerivcePoint().getServPointId())){
    			throw new CMSException("ServPointID is empty");
    		}
    	}
    	
    }
	
}
