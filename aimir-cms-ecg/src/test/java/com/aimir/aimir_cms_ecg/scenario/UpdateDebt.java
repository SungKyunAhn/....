package com.aimir.aimir_cms_ecg.scenario;

import java.net.URL;

import org.junit.Test;

import com.aimir.cms.constants.CMSConstants.ErrorType;
import com.aimir.cms.model.AuthCred;
import com.aimir.cms.model.DebtEnt;
import com.aimir.cms.ws.client.CmsWS;
import com.aimir.cms.ws.client.CmsWS_Service;
import com.aimir.cms.ws.client.GetDebtResp;
import com.aimir.cms.ws.client.UpdateDebtResp;

public class UpdateDebt {

    @Test
    public void test_updateDebt() throws Exception {

    	AuthCred authCred = new AuthCred();
    	//authCred.setUserName("operator");
    	//authCred.setPassword("Password");
    	authCred.setUserName("admin");
    	authCred.setPassword("1234");
    	authCred.setMessageId(new Long(1234567890));
    	authCred.setMessageTimestamp("20140619000000");
    	authCred.setClientId("172.22.1.176");
    	
    	DebtEnt debtEnt1 = new DebtEnt();
    	debtEnt1.setCustomerId("290255139-01");    	
    	
        URL wsdlURL = CmsWS_Service.WSDL_LOCATION;
        CmsWS_Service ss = new CmsWS_Service(wsdlURL);
        CmsWS port = ss.getCmsWSPort(); 
        
        GetDebtResp getDebtResp = port.getDebt(authCred, debtEnt1);     
        if(getDebtResp.getErrorParam().getErrorId() == ErrorType.NoError.getIntValue()){
            
        	DebtEnt debtEnt2 = new DebtEnt();
        	debtEnt2.setCustomerId("290255139-01");
        	debtEnt2.setDebtType("VA120");
        	debtEnt2.setDebtAmount(756.34);
        	
        	UpdateDebtResp updateDebtResp = port.updateDebt(authCred, debtEnt2);
        }        
    }
}
