package com.aimir.fep.protocol.fmp.client;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import com.aimir.fep.protocol.fmp.server.FMPSslContextFactory;

public class SSLClientTest {
    private static Log log = LogFactory.getLog(SSLClientTest.class);
    
    private static class TrustAnyone implements X509TrustManager {
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s)
        throws CertificateException {
            
        }
        
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s)
        throws CertificateException {
            
        }
        
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }
    
    TrustManager[] trustManagers = new TrustManager[] { new TrustAnyone() };
    
    @SuppressWarnings("unused")
	private int writeMessage(Socket socket, String message) throws Exception {
        byte request[] = message.getBytes("UTF-8");
        socket.getOutputStream().write(request);
        return request.length;
    }
    
    Object lock = new Object();
    
    private Socket getClientSocket(boolean ssl) throws Exception {
        if (ssl) {
            SSLContext ctx = FMPSslContextFactory.createFMPClientSslContext();
            return ctx.getSocketFactory().createSocket();
        }
        
        return new Socket("187.1.20.27", 3939);
    }
    
    private void testMessageSentIsCalled(boolean useSSL) throws Exception {
        Socket socket = getClientSocket(useSSL);
        //int bytesSent = 0;
        
        // bytesSent += writeMessage(socket, "test-1\n");
        
        if (useSSL) {
            // Test renegotiation
            SSLSocket ss = (SSLSocket)socket;
            ss.setEnabledProtocols(new String[]{"SSLv3"});
            ss.connect(new InetSocketAddress("187.1.20.27", 3939));
            // ss.connect(new InetSocketAddress("localhost", 8001));
            String[] str = ss.getSession().getValueNames();
            for (String s : str) {
                log.info(s);
            }
            // ss.getSession().invalidate();
            // ss.setEnabledCipherSuites(new String[]{"SSL_DH_anon_EXPORT_WITH_RC4_40_MD5"});
            log.info("Client MODE[" + ss.getUseClientMode() + "]");
            // ss.startHandshake();
        }
        else {
            // socket.bind(new InetSocketAddress("187.1.20.27", 3939));
        }
        /*
        byte[] header = new byte[15];
        socket.getInputStream().read(header);
        log.info(Hex.decode(header));
        
        bytesSent += writeMessage(socket, "test-2\n");
        
        int[] response = new int[bytesSent];
        for (int i = 0; i < response.length; i++) {
            response[i] = socket.getInputStream().read();
        }
        
        if (useSSL) {
            // Read SSL close notify.
            while (socket.getInputStream().read() >= 0) {
                continue;
            }
        }
        */
        while (true) {
            Thread.sleep(1000);
        }
        
        // socket.close();
        /*
        NioSocketConnector connector = new NioSocketConnector();
        FMPSslContextFactory.setSslFilter(connector);
        connector.getFilterChain().addLast("codec", 
                new ProtocolCodecFilter(
                        new ProtocolEncoderAdapter(){
                            @Override
                            public void encode(IoSession session,
                                    Object message, ProtocolEncoderOutput out)
                                    throws Exception {
                                // TODO Auto-generated method stub
                            }
                        },
                        new ProtocolDecoderAdapter() {
                            @Override
                            public void decode(IoSession session, IoBuffer in,
                                    ProtocolDecoderOutput out) throws Exception {
                                log.info("Received [" + session.getRemoteAddress()
                                        + "] : "+in.limit()+" :"+in.getHexDump());
                                
                                byte[] bx = new byte[in.limit()];
                                in.get(bx,0,bx.length);
                                out.write(bx);
                                return;
                            }
        }));
        
        connector.setHandler(new IoHandlerAdapter() {
            public void messageReceived(IoSession session, Object message ) {
                log.info(Hex.decode((byte[])message));
                if (Hex.decode((byte[])message).contains("5E0080040000000005010000D18D")) {
                    synchronized(lock) {
                        lock.notify();
                    }
                }
            }
        });
        
        ConnectFuture future = connector.connect(new InetSocketAddress("187.1.20.27", 3939));
        // ConnectFuture future = connector.connect(new InetSocketAddress("localhost", 8001));
        future.awaitUninterruptibly();
        
        if (!future.isConnected()) {
            throw new Exception("not yet");
        }
        IoSession session = future.getSession();
        log.debug("SESSION CONNECTED[" + session.isConnected() + "]");
        
        synchronized(lock) {
            lock.wait();
        }
        
        connector.dispose();
        */
    }
    
    @Test
    public void testMessageSentIsCalled_With_SSL() throws Exception {
        testMessageSentIsCalled(true);
    }
}
