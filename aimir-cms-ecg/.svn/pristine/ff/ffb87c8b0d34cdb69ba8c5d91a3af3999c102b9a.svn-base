package com.aimir.aimir_cms_ecg.scenario;

import java.net.URL;

import javax.xml.namespace.QName;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.aimir.cms.constants.CMSConstants.ErrorType;
import com.aimir.cms.constants.CMSConstants.SearchType;
import com.aimir.cms.model.AuthCred;
import com.aimir.cms.model.CMSEnt;
import com.aimir.cms.model.CustEnt;
import com.aimir.cms.ws.client.CmsWS;
import com.aimir.cms.ws.client.CmsWS_Service;
import com.aimir.cms.ws.client.SaveAllResp;
import com.aimir.cms.ws.client.SearchResp;
import com.aimir.dao.system.SupplierDao;

public class AddCustomer {

    private static final QName SERVICE_NAME = new QName("http://mbean.cms.aimir.com/", "CmsWS");
    
    @Autowired
    protected SupplierDao supplierDao;
	
    @Test
    public void test_addCustomer() throws Exception {

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
    	CustEnt custEnt = new CustEnt();
    	custEnt.setCustomerId("290255139-01");
    	cmsEnt.setCustomer(custEnt);   
    	
    	
        URL wsdlURL = CmsWS_Service.WSDL_LOCATION;
        CmsWS_Service ss = new CmsWS_Service(wsdlURL);
        CmsWS port = ss.getCmsWSPort(); 
        
        SearchResp searchResp = port.search(authCred, searchType.getValue(), cmsEnt);
        if(searchResp.getErrorParam().getErrorId() == ErrorType.Error.getIntValue()){
        	custEnt.setCustomerId("290255139-01");
        	custEnt.setSurname("KIBI");
        	custEnt.setOtherNames("CHRISTINE ANNE");
        	custEnt.setIdNo("11123473");
        	custEnt.setIdType("National ID");
        	custEnt.setAddress1("37 Liberation Road");
        	custEnt.setAddress2("Osu");
        	custEnt.setAddress3("Accra East, Accra");
        	custEnt.setTelephone1("0505122122");
        	custEnt.setTelephone2("0");
        	custEnt.setTelephone3("0505133133");
        	custEnt.setFax("0");
        	custEnt.setEmail("kibi@gmail.com");
        	custEnt.setTaxRefNo("A003551234R");
        	custEnt.setExist(false);
        	
        	SaveAllResp saveAllResp = port.saveAll(authCred, cmsEnt);

        }
	
    }
}
