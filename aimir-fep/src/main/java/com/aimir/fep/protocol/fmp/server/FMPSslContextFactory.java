/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package com.aimir.fep.protocol.fmp.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.Certificate;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.filter.ssl.SslFilter;

import com.aimir.fep.util.FMPProperty;

/**
 * Factory to create a FMP SSLContext.
 *
 * @author The Apache MINA Project (dev@mina.apache.org)
 * @version $Rev: 576647 $, $Date: 2007-09-17 19:41:29 -0600 (Mon, 17 Sep 2007) $
 */
public class FMPSslContextFactory {
    private static Log log = LogFactory.getLog(FMPSslContextFactory.class);
    
    /**
     * Protocol to use.
     */
    private static final String PROTOCOL = FMPProperty.getProperty("protocol.ssl.protocol");

    private static final String KEY_MANAGER_FACTORY_ALGORITHM;
    
    static {
        String algorithm = Security.getProperty("ssl.KeyManagerFactory.algorithm");
        if (algorithm == null) {
            algorithm = "SunX509";
        }

        KEY_MANAGER_FACTORY_ALGORITHM = algorithm;
    }

    public static SSLContext createFMPServerSslContext()
    throws GeneralSecurityException, IOException {
        // Create keystore
        KeyStore ks = KeyStore.getInstance("JKS");
        InputStream in = null;
        try {
            //in = FMPSslContextFactory.class.getResourceAsStream("/" + FMPProperty.getProperty("protocol.ssl.keystore"));
        	in = new FileInputStream(FMPProperty.getProperty("protocol.ssl.keystore"));
        	ks.load(in, FMPProperty.getProperty("protocol.ssl.keystore.password").toCharArray());
        	Certificate cert = ks.getCertificate(FMPProperty.getProperty("protocol.ssl.store.private.alias"));
        	if ( cert != null)
        		log.info(cert.toString());
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignored) {}
            }
        }

        // Set up key manager factory to use our key store
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KEY_MANAGER_FACTORY_ALGORITHM);
        kmf.init(ks, FMPProperty.getProperty("protocol.ssl.keystore.password").toCharArray());

        // Set up trust Maneger
        TrustManagerFactory  tmf = null;
        
        if (FMPProperty.getProperty("protocol.ssl.truststore.password").length() > 0 &&
        		FMPProperty.getProperty("protocol.ssl.truststore").length() > 0) {
        	KeyStore ts = KeyStore.getInstance("JKS");
        	try {
        		//in = FMPSslContextFactory.class.getResourceAsStream("/" + FMPProperty.getProperty("protocol.ssl.truststore"));
        		in = new FileInputStream(FMPProperty.getProperty("protocol.ssl.truststore"));
        		ts.load(in, FMPProperty.getProperty("protocol.ssl.truststore.password").toCharArray());
        	}
        	finally {
        		if (in != null) {
        			try {
        				in.close();
        			} catch (IOException ignored) {}
        		}
        	}  
        	tmf = TrustManagerFactory.getInstance(KEY_MANAGER_FACTORY_ALGORITHM);
        	tmf.init(ts);
        } 
        
        // Initialize the SSLContext to work with our key managers.
        SSLContext sslContext = SSLContext.getInstance(PROTOCOL);
        sslContext.getServerSessionContext().setSessionTimeout(
                Integer.parseInt(FMPProperty.getProperty("protocol.ssl.server.session.timeout")));
        // sslContext.init(kmf.getKeyManagers(), 
        //		tmf == null ? FMPTrustManagerFactory.X509_MANAGERS :tmf.getTrustManagers(), null);
        sslContext.init(kmf.getKeyManagers(), FMPTrustManagerFactory.X509_MANAGERS, null);
        return sslContext;
    }
   
    public static SSLContext createFMPClientSslContext()
    throws GeneralSecurityException, IOException {
        // Create keystore
        KeyStore ks = KeyStore.getInstance("JKS");
        InputStream in = null;
        try {
            in = new FileInputStream(FMPProperty.getProperty("protocol.ssl.truststore"));
            ks.load(in, FMPProperty.getProperty("protocol.ssl.truststore.password").toCharArray());
            // log.info(ks.getCertificate(FMPProperty.getProperty("protocol.ssl.store.alias.tls")).toString());
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignored) {}
            }
        }

        // Set up key manager factory to use our key store
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KEY_MANAGER_FACTORY_ALGORITHM);
        kmf.init(ks, FMPProperty.getProperty("protocol.ssl.truststore.password").toCharArray());
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(KEY_MANAGER_FACTORY_ALGORITHM);
        tmf.init(ks);

        // Initialize the SSLContext to work with our key managers.
        SSLContext sslContext = SSLContext.getInstance(PROTOCOL);
        sslContext.getClientSessionContext().setSessionTimeout(
                Integer.parseInt(FMPProperty.getProperty("protocol.ssl.client.session.timeout")));
        for (TrustManager tm : tmf.getTrustManagers()) {
            log.info(((X509TrustManager)tm).getAcceptedIssuers()[0].getPublicKey().getFormat());
        }
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        return sslContext;
    }

    public static void setSslFilter(IoAcceptor acceptor) throws GeneralSecurityException, IOException {
        SslFilter sslFilter = null;
        boolean useSSL = Boolean.parseBoolean(FMPProperty.getProperty("protocol.ssl.use"));
        if (useSSL) {
            sslFilter = new SslFilter(createFMPServerSslContext());
            acceptor.getFilterChain().addLast("sslFilter", sslFilter);
        }
    }
   
    public static void setSslFilter(IoConnector connector) throws GeneralSecurityException, IOException {
        SslFilter sslFilter = null;
        boolean useSSL = Boolean.parseBoolean(FMPProperty.getProperty("protocol.ssl.use"));
        if (useSSL) {
            sslFilter = new SslFilter(createFMPClientSslContext());
            sslFilter.setEnabledProtocols(new String[]{PROTOCOL});
            sslFilter.setUseClientMode(true);
            connector.getFilterChain().addLast("sslFilter", sslFilter);
        }
    }
}