package com.aimir.fep.protocol.security;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.californium.scandium.DTLSConnector;
import org.eclipse.californium.scandium.ErrorHandler;
import org.eclipse.californium.scandium.config.DtlsConnectorConfig;
import org.eclipse.californium.scandium.dtls.AlertMessage.AlertDescription;
import org.eclipse.californium.scandium.dtls.AlertMessage.AlertLevel;
import org.eclipse.californium.scandium.dtls.pskstore.StaticPskStore;

import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.constants.CommonConstants.ThresholdName;
import com.aimir.fep.util.EventUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.threshold.CheckThreshold;

public class DtlsConnector
{
    private static Log log = LogFactory.getLog(DtlsConnector.class);
    
    private static final String TRUST_STORE_PASSWORD = FMPProperty.getProperty("protocol.ssl.truststore.password");
    private static final String KEY_STORE_PASSWORD = FMPProperty.getProperty("protocol.ssl.keystore.password");
    private static final String KEY_STORE_LOCATION = FMPProperty.getProperty("protocol.ssl.keystore");
    private static final String TRUST_STORE_LOCATION = FMPProperty.getProperty("protocol.ssl.truststore");
    private static final String PRIVATE_KEY_PASSWORD = FMPProperty.getProperty("protocol.security.password");
    private static final String PRIVATE_ALIAS = FMPProperty.getProperty("protocol.ssl.store.private.alias");
    private static final String CA_ALIAS = FMPProperty.getProperty("protocol.ssl.store.ca.alias");
    private static final String PRIVATE_ALIAS_PANA = FMPProperty.getProperty("protocol.pana.store.private.alias");
    private static final String CA_ALIAS_PANA = FMPProperty.getProperty("protocol.pana.store.ca.alias");
    
    public static DTLSConnector newDtlsClientConnector(boolean isV4, Protocol protocol, int port, boolean isPana) {
        
    	if (isV4) {
            if (protocol == Protocol.IP || protocol == Protocol.GPRS || protocol == Protocol.LAN){
            	log.debug(" protocol: "+ protocol.name() + " /addr: "+ FMPProperty.getProperty("fep.ipv4.addr.ETH") +" /port: "+ port);
                return newDtlsClientConnector(new InetSocketAddress(FMPProperty.getProperty("fep.ipv4.addr.ETH"), port), isPana);
            }else{
            	log.debug(" protocol: "+ protocol.name() + " /addr: "+ FMPProperty.getProperty("fep.ipv4.addr.MBB") +" /port: "+ port);
                return newDtlsClientConnector(new InetSocketAddress(FMPProperty.getProperty("fep.ipv4.addr.MBB"), port), isPana);
            }
        }
        else{
        	log.debug(" protocol: "+ protocol.name() + " /addr: "+ FMPProperty.getProperty("fep.ipv6.addr") +" /port: "+ port);
            return newDtlsClientConnector(new InetSocketAddress(FMPProperty.getProperty("fep.ipv6.addr"), port), isPana);
        }
    }
    
    public static DTLSConnector newDtlsClientConnector(InetSocketAddress address, boolean isPana) {
        DTLSConnector dtlsConnector; 
        
        try {
            // load key store
            KeyStore keyStore = KeyStore.getInstance("JKS");
            InputStream in = new FileInputStream(KEY_STORE_LOCATION);
            keyStore.load(in, KEY_STORE_PASSWORD.toCharArray());
            in.close();

            // load trust store
            KeyStore trustStore = KeyStore.getInstance("JKS");
            in = new FileInputStream(TRUST_STORE_LOCATION);
            trustStore.load(in, TRUST_STORE_PASSWORD.toCharArray());
            in.close();

            // You can load multiple certificates if needed
            Certificate[] trustedCertificates = new Certificate[1];
            
            if (isPana)
                trustedCertificates[0] = trustStore.getCertificate(CA_ALIAS_PANA);
            else
                trustedCertificates[0] = trustStore.getCertificate(CA_ALIAS);

            DtlsConnectorConfig.Builder builder = 
                    new DtlsConnectorConfig.Builder(address);
            builder.setPskStore(new StaticPskStore("Client_identity", PRIVATE_KEY_PASSWORD.getBytes()));
            if (isPana) {
                builder.setIdentity((PrivateKey)keyStore.getKey(PRIVATE_ALIAS_PANA, KEY_STORE_PASSWORD.toCharArray()),
                        keyStore.getCertificateChain(PRIVATE_ALIAS_PANA), true);
            }
            else {
                builder.setIdentity((PrivateKey)keyStore.getKey(PRIVATE_ALIAS, KEY_STORE_PASSWORD.toCharArray()),
                        keyStore.getCertificateChain(PRIVATE_ALIAS), true);
            }
                    
            builder.setTrustStore(trustedCertificates);
            builder.setRetransmissionTimeout(Integer.parseInt(FMPProperty.getProperty("protocol.ssl.client.session.timeout.dtls"))*1000);
            builder.setClientOnly();
            builder.setClientAuthenticationRequired(false);
            builder.setMaxRetransmissions(3);
            
            dtlsConnector = new DTLSConnector(builder.build());
            dtlsConnector.setErrorHandler(new ErrorHandler() {
                @Override
                public void onError(InetSocketAddress peerAddress,
                        AlertLevel level, AlertDescription description) {
                    log.warn("Alert.Level[" + level.toString() + " DESCR[" + description.getDescription() + "] Peer[" + peerAddress.getHostName() + "]");
                    /*
                     * Seurity alarm has to be done with threshold */
                    try {
                        EventUtil.sendEvent("Security Alarm",
                                TargetClass.Modem, trimPort(peerAddress.getHostName()),
                                new String[][] {{"message", "Uncertificated Access"}});
                    }
                    catch (Exception e) {
                        log.error(e, e);
                    }
                    
                    // INSERT START SP-193
                    CheckThreshold.updateCount(peerAddress.getHostName(), ThresholdName.AUTHENTICATION_ERROR);
                    // INSERT END SP-193
                }
            });
            return dtlsConnector;
        } catch (GeneralSecurityException | IOException e) {
            log.error("Could not load the keystore", e);
        }
        
        return null;
    }
    
    private static String trimPort(String ip)
    {
        if (ip == null) return null;
        ip = ip.replace("/", "");
        
        /*
        String result = "";
        String[] array = ip.split(":", -1);
        int len = array.length;
        boolean portFlag = false;
        
        if (len <=1) return ip;
        if ((array[len-1].length()>=5) && (Integer.parseInt(array[len-1]) >= ephemeralPort)) {
            portFlag = true;
        }
        
        if (portFlag) {
            for(int i=0; i<len-2; i++) {
                result = result + array[i] + ":";
            }
            result = result + array[len-2];
        } else {
            for(int i=0; i<len-1; i++) {
                result = result + array[i] + ":";
            }
            result = result + array[len-1];
        }
        return result;
        */
        if (ip != null && !"".equals(ip) && ip.lastIndexOf(":") != -1) {
            ip = ip.substring(0, ip.lastIndexOf(":"));
        }
        
        return ip;
    }
    
    public static DTLSConnector newDtlsServerConnector(int port, boolean isPana) {
        DTLSConnector dtlsConnector; 
        
        try {
            // load key store
            KeyStore keyStore = KeyStore.getInstance("JKS");
            InputStream in = new FileInputStream(KEY_STORE_LOCATION);
            keyStore.load(in, KEY_STORE_PASSWORD.toCharArray());
            in.close();

            // load trust store
            KeyStore trustStore = KeyStore.getInstance("JKS");
            in = new FileInputStream(TRUST_STORE_LOCATION);
            trustStore.load(in, TRUST_STORE_PASSWORD.toCharArray());
            in.close();

            // You can load multiple certificates if needed
            Certificate[] trustedCertificates = new Certificate[1];
            
            if (isPana)
                trustedCertificates[0] = trustStore.getCertificate(PRIVATE_ALIAS_PANA);
            else
                trustedCertificates[0] = trustStore.getCertificate(PRIVATE_ALIAS);

            DtlsConnectorConfig.Builder builder = 
                    new DtlsConnectorConfig.Builder(new InetSocketAddress(FMPProperty.getProperty("fep.ipv6.addr"), port));
            builder.setPskStore(new StaticPskStore("Client_identity", PRIVATE_KEY_PASSWORD.getBytes()));
            if (isPana) {
                builder.setIdentity((PrivateKey)keyStore.getKey(PRIVATE_ALIAS_PANA, KEY_STORE_PASSWORD.toCharArray()),
                        keyStore.getCertificateChain(PRIVATE_ALIAS_PANA), true);
            }
            else {
                builder.setIdentity((PrivateKey)keyStore.getKey(PRIVATE_ALIAS, KEY_STORE_PASSWORD.toCharArray()),
                        keyStore.getCertificateChain(PRIVATE_ALIAS), true);
            }
            builder.setTrustStore(trustedCertificates);
            builder.setRetransmissionTimeout(Integer.parseInt(FMPProperty.getProperty("protocol.ssl.server.session.timeout.dtls"))*1000);
            builder.setClientAuthenticationRequired(false);
            
            dtlsConnector = new DTLSConnector(builder.build());
            dtlsConnector.setErrorHandler(new ErrorHandler() {
                @Override
                public void onError(InetSocketAddress peerAddress,
                        AlertLevel level, AlertDescription description) {
                    log.warn("Alert.Level[" + level.toString() + " DESCR[" + description.getDescription() + "] Peer[" + peerAddress.getHostName() + "]");
                    // INSERT START SP-193
                    CheckThreshold.updateCount(peerAddress.getHostName(), ThresholdName.AUTHENTICATION_ERROR);
                    // INSERT END SP-193
                }
            });
            return dtlsConnector;
        } catch (GeneralSecurityException | IOException e) {
            log.error("Could not load the keystore", e);
        }
        
        return null;
    }
}
