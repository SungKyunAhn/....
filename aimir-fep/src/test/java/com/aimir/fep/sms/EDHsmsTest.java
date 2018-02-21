package com.aimir.fep.sms;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import com.aimir.fep.util.sms.SendSMSEDH;

public class EDHsmsTest {
    private static Log log = LogFactory.getLog(EDHsmsTest.class);
    
    @Test
    public void test_sms() throws Exception {
        SendSMSEDH sms = new SendSMSEDH();
        Properties prop = new Properties();
        prop.put("prepay.sms.baseUrl", "http://bulksms.vsms.net:5567/eapi/submission/send_sms/2/2.0?dca=7bit&");
        prop.put("prepay.sms.id", "abdhaiti");
        prop.put("prepay.sms.pass", "master25");
        String currentmsg = "EDH pention vil office"
                + "\n Nom Du Client : RayKim"
                + "\n Metre no. compteur : 61483375"
                + "\n Courant Credit : 100.0";
        
        String alertmsg = "Votre limite de credit est arrive a terme"
                + "\n Nom Du Client : RayKim"
                + "\n Metre no. compteur : 61483375"
                + "\n Courant Credit : 100.0";
        
        String blockmsg = "Votre alimentation etait bloque. s`il vous plait rechargez votre compte."
                + "\n Nom Du Client : RayKim"
                + "\n Metre no. compteur : 61483375"
                + "\n Courant Credit : 0.0";
        String chargemsg = "Votre balance est maintenant disponible. Le courant est retabli."
                            + "\n Type Client : Electricity"
                            + "\n Montant Charger : 1000.0"
                            + "\n Courant Credit : 1200.0";
        log.info(sms.send("50931744070", currentmsg, prop)); //50931744070
        // log.info(sms.send("50946822362", currentmsg, prop)); //50931744070
    }
}
