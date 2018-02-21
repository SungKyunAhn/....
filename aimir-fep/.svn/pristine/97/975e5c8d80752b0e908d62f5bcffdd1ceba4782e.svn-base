package com.aimir.fep.sms;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

public class SpasaSMSTest {
    private static Log log = LogFactory.getLog(SpasaSMSTest.class);
    
    @Test
    public void test_sms() throws Exception {
        try {
            HttpClient client = new HttpClient();
            GetMethod method = new GetMethod("http://www.xml2sms.gsm.co.za");
            
            HostConfiguration proxy = new HostConfiguration();
            proxy.setProxy("196.13.63.133", 3128);
            
            int status  = client.executeMethod(proxy, method);
            
            log.debug("status : "+status);
        }
        catch (Exception e) {
            log.error(e, e);
        }
    }
}
