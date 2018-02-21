package com.aimir.fep.bems.sender;

import java.net.InetSocketAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

public abstract class DataSender {
	protected static Log log = LogFactory.getLog(DataSender.class);
	
    //Connection time out(milli seconds)
	protected final Long CONN_TIMEOUT=1000L;
	
    //target 정보
    protected String hostname = null;
    protected Integer port = null;
	
    protected NioSocketConnector connector = null;
    protected IoSession session = null;
	
    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public DataSender(){
        //접속 client 생성
        connector = new NioSocketConnector();
        connector.setConnectTimeoutMillis(CONN_TIMEOUT);
        connector.setHandler(new DataSenderHandler());
    }
	public boolean send(IoBuffer buff){
		if(this.session.isConnected()){
            WriteFuture future = this.session.write(buff);
            future.awaitUninterruptibly();
            if(future.isDone()){
                return true;
            }else{
            	log.error(future.getException());
            }
        }
        return false;
	}
	
	/**
	 * Object 객체를 전송하도록 추상화한 메소드. 구현시 사용되는 DTO객체를 byte 로 변환하여 전송하도록 한다. 
	 * @param obj
	 * @return
	 */
	public abstract boolean send(Object obj);
	
	/**
	 * @return
	 */
	public boolean connect() {
		if(this.session!=null && this.session.isConnected())
			return true;
		
	    ConnectFuture cf = connector.connect(new InetSocketAddress(
	            this.hostname, this.port));

	    cf.awaitUninterruptibly();

	    if (!cf.isConnected()) {
	        log.info(String.format("Cannot Connect. host=%s, port=%d",this.hostname,this.port));
	        return false;
	    }

	    this.session = cf.getSession();
	    log.info(String.format("Connected. host=%s, port=%d",this.hostname,this.port));
	    return true;
	}
	
	/**
	 * @return
	 */
	public boolean disconnect(){
	    if(this.session!=null && this.session.isConnected()){
	        CloseFuture cf = this.session.closeNow();
	        cf.awaitUninterruptibly();
			
	        log.info(String.format("Sended. size=%d Byte",this.session.getWrittenBytes()));
			
	        if(cf.isClosed()){
	            log.info(String.format("Disconnected. host=%s, port=%d",this.hostname,this.port));
	            return true;
	        }
	        else {
	            log.info(String.format("Disconnect fail. host=%s, port=%d",this.hostname,this.port));
	            return false;
	        }
	    }
	    return true;
	}
}
