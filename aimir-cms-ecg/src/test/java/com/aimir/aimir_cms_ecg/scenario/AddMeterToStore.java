package com.aimir.aimir_cms_ecg.scenario;

import java.net.URL;

import org.junit.Test;

import com.aimir.cms.constants.CMSConstants.ErrorType;
import com.aimir.cms.model.AuthCred;
import com.aimir.cms.model.MeterEnt;
import com.aimir.cms.ws.client.CmsWS;
import com.aimir.cms.ws.client.CmsWS_Service;
import com.aimir.cms.ws.client.MeterCheckResp;

public class AddMeterToStore {

    @Test
    public void test_addMeterToStore() throws Exception {
    	
    	AuthCred authCred = new AuthCred();
    	//authCred.setUserName("operator");
    	//authCred.setPassword("Password");
    	authCred.setUserName("admin");
    	authCred.setPassword("1234");
    	authCred.setMessageId(new Long(1234567890));
    	authCred.setMessageTimestamp("20140619000000");
    	authCred.setClientId("172.22.1.176");
 	
    	
        URL wsdlURL = CmsWS_Service.WSDL_LOCATION;
        CmsWS_Service ss = new CmsWS_Service(wsdlURL);
        CmsWS port = ss.getCmsWSPort(); 
        
      
        MeterEnt meterEnt = new MeterEnt();
        meterEnt.setMeterSerialNo("0375756744");
        meterEnt.setMake("MC001");
        
        MeterCheckResp meterCheckResp = port.meterCheck(authCred, meterEnt);
        if(meterCheckResp.getErrorParam().getErrorId() == ErrorType.Error.getIntValue()){

            MeterEnt meterEnt2 = new MeterEnt();
            meterEnt2.setMeterSerialNo("0375756744");
            meterEnt2.setMake("MC001");
            meterEnt2.setBatchNo("301300000000001");
            meterEnt2.setModel("MO452");
        	
        	port.meterImport(authCred, meterEnt2);
        }

    }
}
