/**
 * 
 * (관련용어)<br>
 * <p>FEP (Front End Processor)</p>
 * <p>FMP (FEP and MCU Protocol)</p>
 * <p>MRP (Meter Read Protocol)</p>
 * 
 */

package com.aimir.fep.adapter;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderAdapter;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.transport.socket.nio.NioProcessor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.aimir.fep.meter.parser.rdata.BPList;
import com.aimir.fep.meter.parser.rdata.LPList;
import com.aimir.fep.meter.parser.rdata.LogList;
import com.aimir.fep.meter.parser.rdata.MeteringDataRData;
import com.aimir.fep.protocol.fmp.server.FMPSslContextFactory;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.Hex;


/**
 * 제주 ICC 슈나이더외 검침데이타 연동을 위한 아답터
 *
 * 2012.07.11
 * elevas
 */
@Service
public class BemsAdapter implements BemsAdapterMBean, MBeanRegistration{
    private static Log log = LogFactory.getLog(BemsAdapter.class);

    public final static String SERVICE_DOMAIN = "Service";
    public final static String ADAPTER_DOMAIN = "Adapter";
    public final static String DATA_QUEUE = FMPProperty.getProperty("ServiceData.NDData");
    private static final int CONNECT_TIMEOUT = 30;

    // private MBeanServer server = null;

    private String fepName;
    private String commPort;
    private String relayServerIp;
    private String relayServerPort;
    private ObjectName objectName; 
    
    private IoAcceptor acceptor = null;
    private IoConnector connector = null;
    
    @Autowired
    private BemsHandler bemsHandler;
    
    public void init(String commPort, String relayServerIp, String relayServerPort)
    {
        fepName = System.getProperty("name");
        System.setProperty("fepName", fepName);
        
        this.commPort = commPort;
        this.relayServerIp = relayServerIp;
        this.relayServerPort = relayServerPort;
        
        log.info("name="+fepName+", commPort="+this.commPort+
                ", relayServerIp=" + this.relayServerIp + ", relayServerPort=" + this.relayServerPort);
        
        ExecutorService executor = Executors.newCachedThreadPool();
        acceptor = new NioSocketAcceptor(executor, new NioProcessor(executor));
        connector = new NioSocketConnector();
    }

    @Override
    public ObjectName preRegister(MBeanServer server, ObjectName name)
            throws Exception {
        if (objectName == null) 
        {
            objectName = new ObjectName(server.getDefaultDomain() 
                    + ":service=" + this.getClass().getName());
        }
        
        return this.objectName;
    }

    @Override
    public void postRegister(Boolean registrationDone) {
        // TODO Auto-generated method stub
    }

    @Override
    public void preDeregister() throws Exception {
        // TODO Auto-generated method stub
    }

    @Override
    public void postDeregister() {
        
    }

    @Override
    public void start() throws Exception {
        objectName = new ObjectName(
                ADAPTER_DOMAIN+":name="+fepName);
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        mbs.registerMBean(this, objectName);
        
        // acceptor 설정
        FMPSslContextFactory.setSslFilter(acceptor);
        acceptor.getFilterChain().addLast(this.fepName, new ProtocolCodecFilter(new BemsCodecFilter()));
        acceptor.setDefaultLocalAddress(new InetSocketAddress(Integer.parseInt(commPort)));
        acceptor.setHandler(bemsHandler);
        acceptor.bind();
        
        log.info("\t" + fepName + " FEPh is Ready for Service...\n");
    }

    @Override
    public int getPort() {
        return Integer.parseInt(commPort);
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return this.objectName.getCanonicalName();
    }
    
    /**
     * TagName에 있는 태크에 대한 레벨을 요청한다. 현재는 0:off, 1:on만 있음.
     * TagName은 ','로 된 문자열이므로 토큰하여 전송해야 한다.
     */
    @Override
    public void cmdDR(String tagName, int level) {
        IoSession session = null;
        try
        {
            if (!connector.getFilterChain().contains(this.fepName))
                connector.getFilterChain().addLast(this.fepName, new ProtocolCodecFilter(new BemsCodecFilter()));
            
            connector.setHandler(new BemsHandler());
            connector.setConnectTimeoutMillis(CONNECT_TIMEOUT*1000);
            IoFuture future = connector.connect(
                    new InetSocketAddress(relayServerIp, Integer.parseInt(relayServerPort)));
            future.awaitUninterruptibly();

            if (!future.isDone()) {
                throw new Exception("not yet");
            }

            session = future.getSession();
            log.debug("SESSION CONNECTED[" + session.isConnected() + "]");
            // dr 패킷 전송
            CommandData cmd = new CommandData();
            // TagName 토큰한다.
            if (tagName != null && !"".equals(tagName)) {
                StringTokenizer st = new StringTokenizer(tagName, ",");
                while (st.hasMoreTokens()) {
                    cmd.setTagName(st.nextToken());
                    cmd.setLevel(level);
                    future = session.write(cmd);
                }
            }
            
            future.awaitUninterruptibly();
            if (future.isDone());
            // 세션 닫기
            future = session.closeNow();
            future.awaitUninterruptibly();
            if (future.isDone());
        }
        catch( Exception e )
        {
            /*
            try { LookupUtil.updateMCUNetworkStatus(
                    target.getMcuId(),"0");
            }catch(Exception ex){log.error(ex,ex);}
            */
            log.error( "Failed to connect. host["
                    + relayServerIp+"] port["
                    + relayServerPort+"]",e );
        }
    }
    
    public static void main(String[] args) {
        String commPort = "8000";
        String relayServerIp = null;
        String relayServerPort = null;

        if (args.length < 1 ) {
            log.info("Usage:");
            log.info("FepAapter -DfepName AdapterName -jmxPort AdapterPort -commPort CommunicationPort -relayServerIp RelayServerIp -relayServerPort RelayServerPort");
            return;
        }

        for (int i=0; i < args.length; i+=2) {

            String nextArg = args[i];

            if (nextArg.startsWith("-commPort")) {
                commPort = new String(args[i+1]);
            }
            else if (nextArg.startsWith("-relayServerIp")) {
                relayServerIp = new String(args[i+1]);
            }
            else if (nextArg.startsWith("-relayServerPort")) {
                relayServerPort = new String(args[i+1]);
            }
        }

        DataUtil.setApplicationContext(new ClassPathXmlApplicationContext(new String[]{"/config/spring.xml"}));
        BemsAdapter fep = (BemsAdapter)DataUtil.getBean(BemsAdapter.class);
        fep.init(commPort, relayServerIp, relayServerPort);

        try {
            fep.start();
        }
        catch (Exception e) {
            log.error(e);
            System.exit(1);
        }
    }
}

@Service
class BemsHandler extends IoHandlerAdapter {
    private static Log log = LogFactory.getLog(BemsHandler.class);

    @Autowired
    private JmsTemplate activeJmsTemplate;
    
    public void messageReceived(IoSession session, Object message) throws Exception {
        // Queue 전송 후 종료
        if (message instanceof MeteringData) {
            final MeteringData data = (MeteringData)message;
            
            activeJmsTemplate.send(BemsAdapter.DATA_QUEUE, new MessageCreator() {
                public Message createMessage(Session session) throws JMSException {
                    return session.createObjectMessage(data);
                }
            });
        }
    }
    
    public void exceptionCaught(IoSession session, Throwable cause) {
        cause.printStackTrace();
        log.error(cause);
    }
    
    public void sessionOpened(IoSession session)
    {
        log.debug("connected" + session.getRemoteAddress().toString());
    }
}

class BemsCodecFilter implements ProtocolCodecFactory {

    public ProtocolEncoder getEncoder(IoSession session) throws Exception {
        // TODO Auto-generated method stub
        return new AppEncoder();
    }

    public ProtocolDecoder getDecoder(IoSession session) throws Exception {
        return new AppDecoder();
    }
}

class AppEncoder extends ProtocolEncoderAdapter {
    Log log = LogFactory.getLog(AppEncoder.class);
    
    @Override
    public void encode(IoSession session, Object message,
            ProtocolEncoderOutput out) throws Exception {
        if (message instanceof CommandData) {
            CommandData cmd = (CommandData)message;
            // TAG NAME:48bytes, LEN:8bytes
            IoBuffer buf = IoBuffer.allocate(56);
            buf.put(DataUtil.getFixedLengthByte(cmd.getTagName(), 48));
            buf.putDouble(cmd.getLevel());
            buf.flip();
            out.write(buf);
        }
    }
    
}

class AppDecoder extends ProtocolDecoderAdapter {
    Log log = LogFactory.getLog(AppDecoder.class);
    
    private byte[] METERIDLEN = new byte[1];
    private byte[] METERID = null;
    private byte[] CHANNELCOUNT = new byte[1];
    private byte[] BPCOUNT = new byte[1];
    private byte[] LASTTIME = new byte[5];
    private byte[] BASETIME = new byte[5];
    private byte[] LASTVALUE = new byte[8];
    private byte[] BASEVALUE = new byte[8];
    private byte[] BASEPULSE = null;
    
    private byte[] LPCOUNT = new byte[1];
    private byte[] LPTIME = new byte[5];
    private byte[] LPVALUE = new byte[4];
    private byte[] LOADPROFILE = null;
    
    private byte[] LOGCATEGORYCOUNT = new byte[1];
    private byte[] CATEGORY = new byte[1];
    private byte[] VALUELENGTH = new byte[1];
    private byte[] LOGTIME = new byte[5];
    private byte[] LOGCATEGORY = null;
    
    ByteArrayOutputStream bos = null;
    
    public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out)
            throws Exception {
        log.debug(in.getHexDump() + " " + in.remaining());
        
        log.info("IO_BUFFER_POS[" + in.position() + "] IN_REMAIN[" + in.remaining() + "]");
        
        byte[] bx = new byte[in.remaining()];
        in.get(bx);
        
        // Array 인덱싱 에러가 발생하여 나머지 바이트가 버퍼링된 것 뒤에 붙인다.
        if (bos != null) {
            bos.write(bx);
            bx = bos.toByteArray();
        }
        
        // 데이타의 현재 위치
        int pos = 0;
        // 데이타의 마지막 위치
        int lastpos = 0;
        try {
            while (pos < bx.length) {
                System.arraycopy(bx, pos, METERIDLEN, 0, METERIDLEN.length);
                pos += METERIDLEN.length;
                
                METERID = new byte[DataUtil.getIntToBytes(METERIDLEN)];
                System.arraycopy(bx, pos, METERID, 0, METERID.length);
                pos += METERID.length;
                
                System.arraycopy(bx, pos, CHANNELCOUNT, 0, CHANNELCOUNT.length);
                pos += CHANNELCOUNT.length;
                int chCount = DataUtil.getIntToBytes(CHANNELCOUNT);
                
                System.arraycopy(bx, pos, BPCOUNT, 0, BPCOUNT.length);
                pos += BPCOUNT.length;
                int bpCount = DataUtil.getIntToBytes(BPCOUNT);
                
                // Base Pulse 데이타 길이를 구한다.
                int bplen = ((LASTTIME.length+BASETIME.length) + ((LASTVALUE.length+BASEVALUE.length)*chCount)) * bpCount;
                BASEPULSE = new byte[bplen];
                System.arraycopy(bx, pos, BASEPULSE, 0, BASEPULSE.length);
                pos += BASEPULSE.length;
                
                System.arraycopy(bx, pos, LPCOUNT, 0, LPCOUNT.length);
                pos += LPCOUNT.length;
                int lpCount = DataUtil.getIntToBytes(LPCOUNT);
                
                int lplen = (LPTIME.length + (LPVALUE.length*chCount))*lpCount;
                LOADPROFILE = new byte[lplen];
                System.arraycopy(bx, pos, LOADPROFILE, 0, LOADPROFILE.length);
                pos += LOADPROFILE.length;
                
                System.arraycopy(bx, pos, LOGCATEGORYCOUNT, 0, LOGCATEGORYCOUNT.length);
                pos += LOGCATEGORYCOUNT.length;
                int logCategoryCount = DataUtil.getIntToBytes(LOGCATEGORYCOUNT);
                log.debug("LOGCOUNT[" + logCategoryCount + "]");
                
                // 로그의 길이를 구한다.
                int loglen = 0;
                for (int i = 0, valueLen = 0; i < logCategoryCount; i++, valueLen = 0) {
                    loglen += CATEGORY.length;
                    System.arraycopy(bx, pos+loglen, VALUELENGTH, 0, VALUELENGTH.length);
                    loglen += VALUELENGTH.length;
                    valueLen = DataUtil.getIntToBytes(VALUELENGTH);
                    log.debug("LOG_VALUE_LEN[" + valueLen + "]");
                    
                    loglen += LOGTIME.length;
                    
                    loglen += valueLen;
                }
                LOGCATEGORY = new byte[loglen];
                log.debug("LOGLEN[" + loglen + "]");
                
                System.arraycopy(bx, pos, LOGCATEGORY, 0, LOGCATEGORY.length);
                pos += LOGCATEGORY.length;
                log.debug("LOG[" + Hex.decode(LOGCATEGORY)+"]");
                
                MeteringData mdata = new MeteringData();
                mdata.parsingPayLoad(DataUtil.getString(METERID).trim(), 
                        chCount, bpCount, BASEPULSE, lpCount, LOADPROFILE, logCategoryCount, LOGCATEGORY);
                
                out.write(mdata);
                bos = null;
                lastpos = pos;
                
                log.info("BUFFER_LEN[" + bx.length + "] BUFFER_POS[" + lastpos + "]");
                // buf.shrink();
            }
        }
        catch (java.lang.ArrayIndexOutOfBoundsException e) {
            // byte 배열값을 읽어오다가 에러가 발생하면 첫 위치부터 나머지 바이트들을 스트림에 저장하여 이후에 들어오는 값들과 합친다.
            // log.error(e);
            // 인덱싱 에러가 발생한 프레임의 첫바이트부터 나머지 길이를 구한다.
            byte[] remain = new byte[bx.length - lastpos];
            System.arraycopy(bx, lastpos, remain, 0, remain.length);
            bos = new ByteArrayOutputStream();
            bos.write(remain);
            log.info("BUFFER_LEN[" + bx.length + "] BUFFER_POS[" + lastpos + "] BUFFER_REMAIN[" + remain.length + "]");
        }
    }
}

class MeteringData implements Serializable {
    /**
     * Generated serial ID
     */
    private static final long serialVersionUID = 5312130947420930964L;

    private static Log log = LogFactory.getLog(MeteringDataRData.class);
    
    private String meterId;
    private int chCount;
    private int bpCount;
    private List<BPList> bpLists = new ArrayList<BPList>();
    
    private int lpCount;
    private List<LPList> lpLists = new ArrayList<LPList>();
    private String lpTime;
    private Float lpValue;
    private int logCategoryCount;
    private List<LogList> logCategories = new ArrayList<LogList>();   
        
    public void parsingPayLoad(String meterId, int chCount, int bpCount, 
            byte[] bpPayload, int lpCount, byte[] lpPayload, 
            int logCount, byte[] logPayload) throws Exception
    {
        int pos = 0;
        this.meterId = meterId;
        this.chCount = chCount;
        this.bpCount = bpCount;
        this.lpCount = lpCount;
        
        log.debug("METER_ID["+meterId+"]");
        log.debug("CH_COUNT[" + chCount + "]");
        log.debug("BP_COUNT[" + bpCount + "]");
        log.debug("LP_COUNT[" + lpCount + "]");
        
        for(int i = 0; i < bpCount ; i++) {
            BPList bpList = new BPList(pos, bpPayload);
            pos = bpList.parsingPayLoad(chCount, true);
            bpLists.add(bpList);
        }       
        
        pos = 0;
        log.debug("LP_COUNT[" + lpCount + "]");
        for(int i = 0; i < lpCount; i++) {
            LPList lpList = new LPList(pos, lpPayload);
            pos = lpList.parsingPayLoad(chCount, true);
            lpLists.add(lpList);
        }
        
        pos = 0;
        log.debug("LOG_COUNT[" + logCount + "]");
        for (int i=0; i < logCount; i++) {
            LogList logList = new LogList(pos, logPayload);
            pos = logList.parsingPayLoad();
            logCategories.add(logList);
        }
    }

    public String getMeterId() {
        return meterId;
    }

    public void setMeterId(String meterId) {
        this.meterId = meterId;
    }

    /**
     * @return the chCount
     */
    public int getChCount() {
        return chCount;
    }

    /**
     * @param chCount the chCount to set
     */
    public void setChCount(int chCount) {
        this.chCount = chCount;
    }

    /**
     * @return the bpCount
     */
    public int getBpCount() {
        return bpCount;
    }

    /**
     * @param bpCount the bpCount to set
     */
    public void setBpCount(int bpCount) {
        this.bpCount = bpCount;
    }

    /**
     * @return the bpLists
     */
    public List<BPList> getBpLists() {
        return bpLists;
    }

    /**
     * @param bpLists the bpLists to set
     */
    public void setBpLists(List<BPList> bpLists) {
        this.bpLists = bpLists;
    }

    /**
     * @return the lpCount
     */
    public int getLpCount() {
        return lpCount;
    }

    /**
     * @param lpCount the lpCount to set
     */
    public void setLpCount(int lpCount) {
        this.lpCount = lpCount;
    }

    /**
     * @return the lpLists
     */
    public List<LPList> getLpLists() {
        return lpLists;
    }

    /**
     * @param lpLists the lpLists to set
     */
    public void setLpLists(List<LPList> lpLists) {
        this.lpLists = lpLists;
    }

    /**
     * @return the lpTime
     */
    public String getLpTime() {
        return lpTime;
    }

    /**
     * @param lpTime the lpTime to set
     */
    public void setLpTime(String lpTime) {
        this.lpTime = lpTime;
    }

    /**
     * @return the lpValue
     */
    public Float getLpValue() {
        return lpValue;
    }

    /**
     * @param lpValue the lpValue to set
     */
    public void setLpValue(Float lpValue) {
        this.lpValue = lpValue;
    }

    /**
     * @return the logCategoryCount
     */
    public int getLogCategoryCount() {
        return logCategoryCount;
    }

    /**
     * @param logCategoryCount the logCategoryCount to set
     */
    public void setLogCategoryCount(int logCategoryCount) {
        this.logCategoryCount = logCategoryCount;
    }

    /**
     * @return the logList
     */
    public List<LogList> getLogCategories() {
        return logCategories;
    }

    /**
     * @param logList the logCategories to set
     */
    public void setLogCategories(List<LogList> logCategories) {
        this.logCategories = logCategories;
    }   
}

class CommandData implements Serializable {
    private static final long serialVersionUID = -875727500432266841L;
    
    private String tagName;
    private int level;
    
    public String getTagName() {
        return tagName;
    }
    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
    public int getLevel() {
        return level;
    }
    public void setLevel(int level) {
        this.level = level;
    }
}