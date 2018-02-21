package com.aimir.fep.snmp;

import java.util.List;
import java.util.Vector;

import org.junit.Test;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.ScopedPDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.TransportMapping;
import org.snmp4j.UserTarget;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.AuthMD5;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.IpAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TableEvent;
import org.snmp4j.util.TableUtils;

public class Snmp4JTest {
    // default number of retries is 2
    private static final int RETRIES = 2;
    // default timeout is 500
    private static final int TIMEOUT = 5000;
    
    @Test
    public void get(int version, String ipaddr, String community, String oid) throws Exception {
        String printableVersion = new String("v1");
        // create a GenericAddress and OID object to retrieve
        // GenericAddress address = new GenericAddress("100.1.43.3");
        Address address = GenericAddress.parse("udp:" + ipaddr + "/161");
        TransportMapping transport = new DefaultUdpTransportMapping();
        transport.listen();
        Snmp session = new Snmp(transport);
        
        // create a generic target object
        Target target = null;
        // create a session to transact PDUs
        // build the PDU
        PDU pdu = null;
        
        // SNMP v1 or v2c is specified
        // Create a community-based target
        CommunityTarget ctarget = new CommunityTarget();
        ctarget.setCommunity(new OctetString(community));
        ctarget.setAddress(address);
        ctarget.setRetries(2);
        ctarget.setTimeout(1500);
        
        if (version == 2) {
            ctarget.setVersion(SnmpConstants.version2c);
            printableVersion = new String("v2c");
        }
        else {
            ctarget.setVersion(SnmpConstants.version1);
            printableVersion = new String("v1");
        }
        // assign the community based target to our generic target
        // object
        target = ctarget;
        
        pdu = new PDU();
        
        System.out.println("\n\nTesting against agent at "+ address.toString());
        System.out.println("\nUsing SNMP "+ printableVersion);
        
        // issue the synchronous request
        ResponseEvent response = null;
        // pdu.add(new VariableBinding(new OID("1.3.6.1.2.1.1.1")));
        OID _oid = new OID(oid);
        // while (true) {
            pdu.add(new VariableBinding(_oid));
            // pdu.setType(PDU.GETNEXT);
            response = session.send(pdu, target);
            
            // blocked GET request
            if (response != null)
            {
                // get succeeded extract the VB from the response PDU
                // and print out the retrieved value
                PDU responsePDU = response.getResponse();
                if (responsePDU != null) {
                    Vector var = responsePDU.getVariableBindings();
                    VariableBinding vb = null;
                    Object obj = null;
                    for (int i = 0; i < var.size(); i++) {
                        obj = var.get(i);
                        if (obj != null && obj instanceof VariableBinding) {
                            vb = (VariableBinding)obj;
                            System.out.println("OID = " + vb.getOid());
                            System.out.println(", Value = " + vb.getVariable());
                            System.out.println(", Error Status: " + response.getResponse().getErrorStatusText());
                            _oid = vb.getOid();
                            if (!_oid.startsWith(new OID(oid))) {
                                session.close();
                                return;
                            }
                        }
                    }
                }
            }
            else
            {
                // get failed - print out error message
                System.out.println("FAILED!");
                // break;
            }
        // }
        session.close();
    }
    
    public void get(int version, String ipaddr, String securityName, String passwd, String oid) throws Exception {
        String printableVersion = new String("v3");
        // create a GenericAddress and OID object to retrieve
        // GenericAddress address = new GenericAddress("100.1.43.3");
        Address address = GenericAddress.parse("udp:" + ipaddr + "/161");
        TransportMapping transport = new DefaultUdpTransportMapping();
        transport.listen();
        Snmp session = new Snmp(transport);
        
        // create a generic target object
        Target target = null;
        // create a session to transact PDUs
        // build the PDU
        PDU pdu = null;
        
        // SNMP v3
        USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(MPv3.createLocalEngineID()), 0);
        SecurityModels.getInstance().addSecurityModel(usm);
        
        UsmUser user = new UsmUser(new OctetString(securityName), AuthMD5.ID, new OctetString(passwd), null, null);
        session.getUSM().addUser(new OctetString(securityName), user);
        // user = new UsmUser("admin", "password", Session.MD5AUTHENTICATION, null, null);
        // create a secure target object with the discovered
        // engine ID and the user object just created
        
        // replace the generic target object with our new secure
        // target
        UserTarget starget = new UserTarget();
        starget.setAddress(address);
        starget.setRetries(1);
        starget.setTimeout(5000);
        starget.setSecurityLevel(SecurityLevel.AUTH_NOPRIV);
        starget.setSecurityName(new OctetString(securityName));
        starget.setVersion(SnmpConstants.version3);
        printableVersion = new String("v3");
        
        target = starget;
        
        pdu = new ScopedPDU();
        
        System.out.println("\n\nTesting against agent at "+ address.toString());
        System.out.println("\nUsing SNMP "+ printableVersion);
        
        // issue the synchronous request
        ResponseEvent response = null;
        // pdu.add(new VariableBinding(new OID("1.3.6.1.2.1.1.1")));
        OID _oid = new OID(oid);
        while (true) {
            pdu.add(new VariableBinding(_oid));
            // pdu.setType(PDU.GETNEXT);
            response = session.send(pdu, target);
            
            // blocked GET request
            if (response != null)
            {
                // get succeeded extract the VB from the response PDU
                // and print out the retrieved value
                PDU responsePDU = response.getResponse();
                if (responsePDU != null) {
                    Vector var = responsePDU.getVariableBindings();
                    VariableBinding vb = null;
                    Object obj = null;
                    for (int i = 0; i < var.size(); i++) {
                        obj = var.get(i);
                        if (obj != null && obj instanceof VariableBinding) {
                            vb = (VariableBinding)obj;
                            System.out.println("OID = " + vb.getOid());
                            System.out.println(", Value = " + vb.getVariable());
                            System.out.println(", Error Status: " + response.getResponse().getErrorStatusText());
                            _oid = vb.getOid();
                            if (!_oid.startsWith(new OID(oid))) {
                                session.close();
                                return;
                            }
                        }
                    }
                }
            }
            else
            {
                // get failed - print out error message
                System.out.println("FAILED!");
                break;
            }
        }
        session.close();
    }
    
    public void get_old(int version, String ipaddr, String community, String oid) throws Exception {
        String printableVersion = new String("v1");
        // create a GenericAddress and OID object to retrieve
        // GenericAddress address = new GenericAddress("100.1.43.3");
        Address address = GenericAddress.parse("udp:" + ipaddr + "/161");
        TransportMapping transport = new DefaultUdpTransportMapping();
        transport.listen();
        Snmp session = new Snmp(transport);

        // create a generic target object
        Target target = null;
        // create a session to transact PDUs
        // build the PDU
        PDU pdu = null;

        if (version == 3)
        {
            // SNMP v3
            USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(MPv3.createLocalEngineID()), 0);
            SecurityModels.getInstance().addSecurityModel(usm);
            UsmUser user = new UsmUser(new OctetString("prtgadmin"), AuthMD5.ID, new OctetString("gksfks12!@"), null,
                    null);
            session.getUSM().addUser(new OctetString("prtgadmin"), user);
            // user = new UsmUser("admin", "password", Session.MD5AUTHENTICATION, null, null);
            // create a secure target object with the discovered
            // engine ID and the user object just created

            // replace the generic target object with our new secure
            // target
            UserTarget starget = new UserTarget();
            starget.setAddress(address);
            starget.setRetries(1);
            starget.setTimeout(5000);
            starget.setSecurityLevel(SecurityLevel.AUTH_NOPRIV);
            starget.setSecurityName(new OctetString("prtgadmin"));
            starget.setVersion(SnmpConstants.version3);
            printableVersion = new String("v3");

            target = starget;

            pdu = new ScopedPDU();
        }
        else
        {
            // SNMP v1 or v2c is specified
            // Create a community-based target
            CommunityTarget ctarget = new CommunityTarget();
            ctarget.setCommunity(new OctetString(community));
            ctarget.setAddress(address);
            ctarget.setRetries(2);
            ctarget.setTimeout(1500);

            if (version == 2) {
                ctarget.setVersion(SnmpConstants.version2c);
                printableVersion = new String("v2c");
            }
            else {
                ctarget.setVersion(SnmpConstants.version1);
                printableVersion = new String("v1");
            }
            // assign the community based target to our generic target
            // object
            target = ctarget;
            
            pdu = new PDU();
        }
        
        System.out.println("\n\nTesting against agent at "+ address.toString());
        System.out.println("\nUsing SNMP "+ printableVersion);

        // issue the synchronous request
        ResponseEvent response = null;
        // pdu.add(new VariableBinding(new OID("1.3.6.1.2.1.1.1")));
        OID _oid = new OID(oid);
        while(true) {
            pdu.add(new VariableBinding(new OID(_oid)));
            // pdu.setType(PDU.GETNEXT);
            response = session.getNext(pdu, target);
    
            // blocked GET request
            if (response != null)
            {
                // get succeeded extract the VB from the response PDU
             // and print out the retrieved value
                PDU responsePDU = response.getResponse();
                if (responsePDU != null) {
                    Vector var = responsePDU.getVariableBindings();
                    VariableBinding vb = null;
                    Object obj = null;
                    for (int i = 0; i < var.size(); i++) {
                        obj = var.get(i);
                        if (obj != null && obj instanceof VariableBinding) {
                            vb = (VariableBinding)obj;
                            System.out.println("OID = " + vb.getOid());
                            System.out.println(", Value = " + vb.getVariable());
                            System.out.println(", Error Status: " + response.getResponse().getErrorStatusText());
                            _oid = vb.getOid();
                            if (!_oid.toString().startsWith(oid)) {
                                session.close();
                                return;
                            }
                        }
                    }
                 }
             }
             else
             {
                 // get failed - print out error message
                 System.out.println("FAILED!");
                 break;
             }
         }
         session.close();
    }
    
    public void getTable(int version, String ipaddr, String community, String oid,
            String lowIdx, String upperIdx) throws Exception {
        String printableVersion = new String("v1");
        // create a GenericAddress and OID object to retrieve
        // GenericAddress address = new GenericAddress("100.1.43.3");
        Address address = GenericAddress.parse("udp:" + ipaddr + "/161");
        TransportMapping transport = new DefaultUdpTransportMapping();
        transport.listen();
        Snmp session = new Snmp(transport);
        
        // create a generic target object
        Target target = null;
        // create a session to transact PDUs
        
        if (version == 3)
        {
            // SNMP v3
            USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(MPv3.createLocalEngineID()), 0);
            SecurityModels.getInstance().addSecurityModel(usm);
            
            UsmUser user = new UsmUser(new OctetString("admin"), AuthMD5.ID, new OctetString("password"), null, null);
            session.getUSM().addUser(new OctetString("admin"), user);
            // user = new UsmUser("admin", "password", Session.MD5AUTHENTICATION, null, null);
            // create a secure target object with the discovered
            // engine ID and the user object just created
            
            // replace the generic target object with our new secure
            // target
            UserTarget starget = new UserTarget();
            starget.setAddress(address);
            starget.setRetries(1);
            starget.setTimeout(5000);
            starget.setSecurityLevel(SecurityLevel.AUTH_NOPRIV);
            starget.setSecurityName(new OctetString("admin"));
            starget.setVersion(SnmpConstants.version3);
            printableVersion = new String("v3");
            
            target = starget;
            
        }
        else
        { 
            // SNMP v1 or v2c is specified
            // Create a community-based target
            CommunityTarget ctarget = new CommunityTarget();
            ctarget.setCommunity(new OctetString(community));
            ctarget.setAddress(address);
            ctarget.setRetries(2);
            ctarget.setTimeout(1500);
            
            if (version == 2) {
                ctarget.setVersion(SnmpConstants.version2c);
                printableVersion = new String("v2c");
            }
            else {
                ctarget.setVersion(SnmpConstants.version1);
                printableVersion = new String("v1");
            }
            // assign the community based target to our generic target
            // object
            target = ctarget;
            
        }
        
        System.out.println("\n\nTesting against agent at "+ address.toString());
        System.out.println("\nUsing SNMP "+ printableVersion);
        
        TableUtils tu = new TableUtils(session, new DefaultPDUFactory());
        List telist = tu.getTable(target, new OID[]{new OID(oid)}, new OID(lowIdx), new OID(upperIdx));
        
        TableEvent te = null;
        VariableBinding[] vbs = null;
        for (int i = 0; i < telist.size(); i++) {
            te = (TableEvent)telist.get(i);
            vbs = te.getColumns();
            for (int j = 0; j < vbs.length; j++) {
                System.out.println(vbs[j].toString());
            }
        }
        session.close();
    }

    public void discoverall() {
        String ip = "187.1.30.";
        for (int i = 1; i <= 255; i++) {
            try {
                discover(ip + i);
            }
            catch (Exception e) {
                // log.error();
            }
        }
    }
    
    public void discover(String ipaddr) {
        // default to SNMP v1
        int version = 3;
        String printableVersion = new String("v1");
        // make a GenericAddress for the broadcast
        GenericAddress address = new GenericAddress(new IpAddress(ipaddr));
        // use the sysDescr object for discovery
        OID oid = new OID("1.3.6.1.2.1.1.1.0");
         
        // create a PDU
        PDU pdu = new PDU();
        VariableBinding vb = new VariableBinding();
        // add our discovery object to a new variable binding
        vb.setOid(oid);
        pdu.add(vb);
        // create a generic target object
        Target target = null;
        // create a session to transact PDUs
        Snmp session = new Snmp();
        if (version == 3)
        {
             // SNMP v3
             UserTarget starget = new UserTarget();
             UsmUser user = null;
             byte[] engineID = {1,5,6,7};
             // discover the engine ID at the specified address
             engineID = session.discoverAuthoritativeEngineID(address, TIMEOUT);
             // synchronize with the engine using MD5 authentication
             // session.synchronizeWithEngine(engineID, address, "MD5", "MD5UserAuthPassword", Session.MD5AUTHENTICATION);
             // create a User object with MD5 authentication and no
             // privacy
             // Optionally, MD5 with privacy or SHA authentication with
             // or without privacy could be specified at this stage
             user = new UsmUser(new OctetString("MD5"), AuthMD5.ID, new OctetString("abcd1234"), null, null);
             // create a secure target object with the discovered
             // engine ID and the user object just created
             starget.setAddress(address);
             starget.setRetries(1);
             starget.setTimeout(5000);
             starget.setVersion(SnmpConstants.version3);
             starget.setSecurityLevel(SecurityLevel.AUTH_NOPRIV);
             starget.setSecurityName(new OctetString("MD5"));
             // replace the generic target object with our new secure
             // target
             target = starget;
         }
         else
         { 
             // SNMP v1 or v2c is specified
             // Create a community-based target
             CommunityTarget ctarget = new CommunityTarget();
             ctarget.setCommunity(new OctetString("public"));
             ctarget.setAddress(address);
             ctarget.setRetries(2);
             ctarget.setTimeout(1500);
             ctarget.setVersion(SnmpConstants.version2c);
             
             target = ctarget;
         }
         // set the snmp version
         switch (version)
         {
             case 1 :
                 target.setVersion(SnmpConstants.version1);
                 printableVersion = new String("v1");
                 break;
             case 2 :
                 target.setVersion(SnmpConstants.version2c);
                 printableVersion = new String("v2c");
                 break;
             case 3 :
                 target.setVersion(SnmpConstants.version3);
                 printableVersion = new String("v3");
                 break;
             default :
                 target.setVersion(SnmpConstants.version1);
                 break;
         }
         //set the number of retries to a predefined value
         // this will cause continual retries at the interval specified
         // by the timeout
         // create a listener
         Discover discoverer = new Discover();
         try
         {
             session.get(pdu, target, null, discoverer);
             Thread.sleep(300); // will run for about 3 seconds!
         }
         catch (Exception ie)
         {
         }
    }
    
    class Discover implements ResponseListener {
        @Override
        public void onResponse(ResponseEvent pduEvent) {
            PDU response = pduEvent.getResponse();
            if (response != null)
            {
                // log response
                System.out.println("Discovered agent at: " + pduEvent.getPeerAddress());
            }
        }
    }
    
    public static void main(String[] args) {
        Snmp4JTest test = new Snmp4JTest();
        try {
            /*
            int version = Integer.parseInt(args[0]);
            if (version == 1 || version == 2)
                test.get(version, args[1], args[2], args[3]);
            else if (version == 3)
                test.get(version, args[1], args[2], args[3], args[4]);
                */
            test.get(2, "187.1.30.1", "public", "1.3.6.1.2.1.1.1");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
