package com.aimir.fep.protocol.nip.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioDatagramAcceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aimir.fep.protocol.nip.frame.GeneralFrame;
import com.aimir.fep.protocol.nip.server.NiProtocolHandler;
import com.aimir.fep.util.Hex;
import com.aimir.util.DateTimeUtil;

/**
 * 모뎀의 NIP 통신을 위한 어댑터. 2016.07.18 사용안함.
 * @author elevas
 *
 */
public class NiAdapterBack {
    private static Log log = LogFactory.getLog(NiAdapterBack.class);
    
    @Autowired
    NiProtocolHandler handler;
//    NiHandler handler;
    
    public NiAdapterBack() {
    }
    
    public void start(int port) throws Exception {
        // UDP 통신
        //NioDatagramAcceptor acceptor = new NioDatagramAcceptor();
        IoAcceptor acceptor = new NioDatagramAcceptor();
        acceptor.setHandler(new NiProtocolHandler(false, this.getClass().getSimpleName() + ":" + DateTimeUtil.getCurrentDateTimeByFormat(null)));
        DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();
        chain.addLast("logger", new LoggingFilter());
        chain.addLast("codec", new ProtocolCodecFilter(new NiCodecFilter()));
        chain.addLast("Executor", new ExecutorFilter(Executors.newCachedThreadPool()));
        //DatagramSessionConfig dcfg = acceptor.getSessionConfig();
        //dcfg.setReuseAddress(true);
        try {
            acceptor.bind(new InetSocketAddress(port));
            log.info(acceptor.toString());
        } catch (IOException e) {
            log.error(e);
        }
    }
}

@Service(value="niHandler")
class NiHandler extends IoHandlerAdapter {
    private static Log log = LogFactory.getLog(NiHandler.class);
    
    public void sessionOpened(IoSession session) {
        // 검침데이타나 이벤트 또는 커맨드 실행을 위해 오픈한다.
        // ModemInfomation 커맨드를 무조건 실행한다.
    }
    
    public void messageReceived(IoSession session, Object msg) throws Exception {
        log.debug(Hex.decode((byte[])msg));
        
        GeneralFrame frame = new GeneralFrame();
        frame.decode((byte[])msg);
        
        // multi frame인지 구분
        // paylog가 무엇인지 구분
    }
}

class NiCodecFilter implements ProtocolCodecFactory {

    @Override
    public ProtocolDecoder getDecoder(IoSession arg0) throws Exception {
        return new NiDecoder();
    }

    @Override
    public ProtocolEncoder getEncoder(IoSession arg0) throws Exception {
        return new NiEncoder();
    }
}

class NiDecoder extends CumulativeProtocolDecoder {
    
    @Override
    protected boolean doDecode(IoSession session, IoBuffer in,
            ProtocolDecoderOutput out) throws Exception {
        byte[] bx = new byte[in.limit()];
        in.get(bx);
        in.position(bx.length);
        out.write(bx);
        
        return true;
    }
}

class NiEncoder extends ProtocolEncoderAdapter {

    @Override
    public void encode(IoSession session, Object msg, ProtocolEncoderOutput out)
            throws Exception {
        if (msg instanceof IoBuffer) {
            //out.write(((GeneralFrame)msg).encode(null));
        	out.write(msg);
        }
    }
}
