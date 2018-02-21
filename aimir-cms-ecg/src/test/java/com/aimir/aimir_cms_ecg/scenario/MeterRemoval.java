package com.aimir.aimir_cms_ecg.scenario;

import java.net.URL;

import org.junit.Test;

import com.aimir.cms.constants.CMSConstants.ErrorType;
import com.aimir.cms.constants.CMSConstants.SearchType;
import com.aimir.cms.model.AuthCred;
import com.aimir.cms.model.CMSEnt;
import com.aimir.cms.model.MeterEnt;
import com.aimir.cms.model.ServPoint;
import com.aimir.cms.model.TariffEnt;
import com.aimir.cms.ws.client.CmsWS;
import com.aimir.cms.ws.client.CmsWS_Service;
import com.aimir.cms.ws.client.DataLoadResp;
import com.aimir.cms.ws.client.SaveAllResp;
import com.aimir.cms.ws.client.SearchResp;

public class MeterRemoval {

    @Test
    public void test_meterRemoval() throws Exception {

    	AuthCred authCred = new AuthCred();
    	//authCred.setUserName("operator");
    	//authCred.setPassword("Password");
    	authCred.setUserName("admin");
    	authCred.setPassword("1234");
    	authCred.setMessageId(new Long(1234567890));
    	authCred.setMessageTimestamp("20140619000000");
    	authCred.setClientId("172.22.1.176");
    	
    	SearchType searchType = SearchType.EXACT;
    	
    	CMSEnt cmsEnt = new CMSEnt();
    	ServPoint servPoint = new ServPoint();
    	servPoint.setServPointId("290255139-01");
    	cmsEnt.setSerivcePoint(servPoint);
    	
    	
        URL wsdlURL = CmsWS_Service.WSDL_LOCATION;
        CmsWS_Service ss = new CmsWS_Service(wsdlURL);
        CmsWS port = ss.getCmsWSPort(); 
        
        SearchResp searchResp = port.search(authCred, searchType.getValue(), cmsEnt);
        
        if(searchResp.getErrorParam().getErrorId() == ErrorType.NoError.getIntValue()){
            
            DataLoadResp dataLoadResp = port.dataLoad(authCred, cmsEnt);
            
            if(dataLoadResp.getErrorParam().getErrorId() == ErrorType.NoError.getIntValue()){
                
                MeterEnt meterEnt = new MeterEnt();
                meterEnt.setMeterSerialNo("0375756744");
                meterEnt.setMake("MC001");

            	
                servPoint.setServPointId("290255139-01");
            	servPoint.setAddress1("37 Liberation Road");
            	servPoint.setAddress2("Osu");
            	servPoint.setAddress3("Accra East, Accra");
            	servPoint.setGeoCode("02-04-100-030-060-1200");
            	servPoint.setMeter(null);
            	
            	TariffEnt tariff = new TariffEnt();
            	tariff.setTariffCode("E10");
            	tariff.setTariffGroup(1);
            	servPoint.setTariff(tariff);
            	
            	servPoint.setExist(true);
            	
            	SaveAllResp saveAllResp = port.saveAll(authCred, cmsEnt);
            }

        	
        }

    }
}
