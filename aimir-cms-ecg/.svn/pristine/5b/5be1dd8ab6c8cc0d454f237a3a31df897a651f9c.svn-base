package com.aimir.aimir_cms_ecg.scenario;

import java.net.URL;

import org.junit.Test;

import com.aimir.cms.constants.CMSConstants.ErrorType;
import com.aimir.cms.constants.CMSConstants.SearchType;
import com.aimir.cms.model.AuthCred;
import com.aimir.cms.model.CMSEnt;
import com.aimir.cms.model.ServPoint;
import com.aimir.cms.ws.client.CmsWS;
import com.aimir.cms.ws.client.CmsWS_Service;
import com.aimir.cms.ws.client.DataLoadResp;
import com.aimir.cms.ws.client.SaveAllResp;
import com.aimir.cms.ws.client.SearchResp;

public class DeEnrolment {
	
    @Test
    public void test_deEnrolment() throws Exception {

    	AuthCred authCred = new AuthCred();
    	//authCred.setUserName("operator");
    	//authCred.setPassword("Password");
    	authCred.setUserName("admin");
    	authCred.setPassword("1234");
    	authCred.setMessageId(new Long(1234567890));
    	authCred.setMessageTimestamp("20140619000000");
    	authCred.setClientId("172.22.1.176");
    	
    	SearchType searchType = SearchType.EXACT;
    	
    	CMSEnt cmsEnt1 = new CMSEnt();
    	ServPoint servPoint = new ServPoint();
    	servPoint.setServPointId("290255139-01");
    	cmsEnt1.setSerivcePoint(servPoint);
    	
    	
        URL wsdlURL = CmsWS_Service.WSDL_LOCATION;
        CmsWS_Service ss = new CmsWS_Service(wsdlURL);
        CmsWS port = ss.getCmsWSPort(); 
        
        SearchResp searchResp = port.search(authCred, searchType.getValue(), cmsEnt1);
        if(searchResp.getErrorParam().getErrorId() == ErrorType.NoError.getIntValue()){
            
            DataLoadResp dataLoadResp = port.dataLoad(authCred, cmsEnt1);
            
            if(dataLoadResp.getErrorParam().getErrorId() == ErrorType.NoError.getIntValue() ){
            	
            	CMSEnt cmsEnt = new CMSEnt();
            	cmsEnt.setSerivcePoint(dataLoadResp.getCMSEnt().getSerivcePoint());
            	cmsEnt.setCustomer(null);
            	
            	SaveAllResp saveAllResp = port.saveAll(authCred, cmsEnt);
            }

        }

    	
    	
    }
}
