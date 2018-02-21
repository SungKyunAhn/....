package com.aimir.comm;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderAdapter;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.proxy.utils.ByteUtilities;
import org.apache.mina.transport.socket.DatagramSessionConfig;
import org.apache.mina.transport.socket.nio.NioDatagramAcceptor;
import org.snmp4j.PDU;
import org.snmp4j.smi.VariableBinding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants.EventStatus;
import com.aimir.constants.CommonConstants.Interface;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.system.DeviceModelDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.EventUtil;
import com.aimir.fep.util.Hex;
import com.aimir.model.device.Converter;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.Meter;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.Supplier;
import com.aimir.util.DateTimeUtil;

import java.util.concurrent.Executors;

/**
 * Hello App!
 *
 */
public class App 
{
    private static Log log = LogFactory.getLog(App.class);
    
    public static Properties fmpProp = null;
    
    public static void main( String[] args )
    {
        log.info( "Hello App!" );
        String commPort = null;

        if (args.length < 1 ) {
            log.info("Usage:");
            log.info("App -DcommName CommName -commPort CommunicationPort");
            return;
        }

        for (int i=0; i < args.length; i+=2) {

            String nextArg = args[i];

            if (nextArg.startsWith("-commPort")) {
                commPort = new String(args[i+1]);
            }
        }

        App app = new App();
        
        DataUtil.setApplicationContext(new ClassPathXmlApplicationContext(new String[]{"/config/spring.xml"}));
        fmpProp = new Properties();
        try {
            fmpProp.load(app.getClass().getResourceAsStream("/config/fmp.properties"));
        } catch (IOException e) {
            log.error(e);
        }
        
        // UDP 통신
        NioDatagramAcceptor acceptor = new NioDatagramAcceptor();
        acceptor.setHandler(DataUtil.getBean(AppHandler.class));
        DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();
        chain.addLast("logger", new LoggingFilter());
        chain.addLast("codec", new ProtocolCodecFilter(new AppCodecFilter()));
        chain.addLast("Executor", new ExecutorFilter(Executors.newCachedThreadPool()));
        DatagramSessionConfig dcfg = acceptor.getSessionConfig();
        dcfg.setReuseAddress(true);
        try {
            acceptor.bind(new InetSocketAddress(Integer.parseInt(commPort)));
        } catch (IOException e) {
            log.error(e);
        }
    }
}

@Service
class AppHandler extends IoHandlerAdapter {
    private static Log log = LogFactory.getLog(AppHandler.class);
    private static Log doorLog = LogFactory.getLog("doorevent");
    
    @Autowired
    private ModemDao modemDao;
    
    @Autowired
    private DeviceModelDao deviceModelDao;
    
    @Autowired
    private SupplierDao supplierDao;
    
    public void messageReceived(IoSession session, Object message) throws Exception {
        TransactionStatus txStatus = null;
        try {
            if (message instanceof LocatingData) {
                LocatingData data = (LocatingData)message;
                log.info("Serial Number : " + data.getSerialNumber());
                log.info("Device Name : " + data.getDeviceName());
                log.info("MAC Address : " + data.getMacAddress());
                log.info("IP Address : " + data.getIpAddress());
                log.info("Port : " + data.getPort());
                log.info("FW Ver : " + data.getFwVer());
                Converter converter = (Converter)modemDao.get(data.getSerialNumber());
                
                String supplierName = new String(App.fmpProp.getProperty("default.supplier.name").getBytes("8859_1"), "UTF-8");
                
                if (converter == null) {
                    log.info("must create converter modem!");
                    converter = new Converter();
                    converter.setModemType(ModemType.Converter.name());
                    converter.setDeviceSerial(data.getSerialNumber());
                    converter.setIpAddr(data.getIpAddress());
                    converter.setSysPort(data.getPort());
                    converter.setMacAddr(data.getMacAddress());
                    converter.setFwVer(data.getFwVer());
                    converter.setLastLinkTime(DateTimeUtil.getDateString(new Date()));
                    converter.setInstallDate(converter.getLastLinkTime());
                    converter.setSysName(data.getDeviceName());
                    converter.setProtocolType(Protocol.LAN.name());
                    converter.setInterfaceType(Interface.Unknown);
                    
                    Supplier supplier = supplierDao.getSupplierByName(supplierName);
                    
                    converter.setSupplier(supplier);
                    
                    //TODO 모델명 확인해서 수정요망
                    List<DeviceModel> deviceModelList = 
                        deviceModelDao.getDeviceModelByName(supplier.getId(), "Converter");
                    
                    if (deviceModelList.size() == 1) 
                        converter.setModel(deviceModelList.get(0));
                    
                    modemDao.add(converter);
                }
                else {
                    if (converter.getSysName() == null || !converter.getSysName().equals(data.getDeviceName()))
                        converter.setSysName(data.getDeviceName());
                    if (converter.getIpAddr() == null || !converter.getIpAddr().equals(data.getIpAddress()))
                        converter.setIpAddr(data.getIpAddress());
                    if (converter.getSysPort() == null || !converter.getSysPort().equals(data.getPort()))
                        converter.setSysPort(data.getPort());
                    if (converter.getMacAddr() == null || !converter.getMacAddr().equals(data.getMacAddress()))
                        converter.setMacAddr(data.getMacAddress());
                    if (converter.getFwVer() == null || !converter.getFwVer().equals(data.getFwVer()))
                        converter.setFwVer(data.getFwVer());
                    
                    converter.setLastLinkTime(DateTimeUtil.getDateString(new Date()));
                    
                    modemDao.update(converter);
                }
            }
            else if (message instanceof EventData) {
                EventData ed = (EventData)message;
                log.info("Serial Number : " + ed.getSerialNumber());
                log.info("Event Time : " + ed.getEventTime());
                log.info("Event Id : " + ed.getEventId());
                log.info("Event Status : " + ed.getEventStatus());
                doorLog.info(String.format("[%s] Door %s, code=%d",
                		ed.getEventTime(),
                		ed.getEventStatus()==1? "Open":"Close",
                		ed.getEventStatus()
                		));
                
                
                Converter converter = (Converter)modemDao.get(ed.getSerialNumber());
                EventAlertLog event = new EventAlertLog();
                event.setStatus(ed.getEventStatus()==1? EventStatus.Open:EventStatus.Cleared);
                
                if (converter.getMeter().size() == 0) {
                    EventUtil.sendEvent("Door Alarm", TargetClass.Converter, ed.getSerialNumber(),
                            ed.getEventTime(), new String[][]{{"doorState", ed.getEventStatus()==1? "Open":"Close"}},
                            event);
                }
                else {
                    Meter meter = converter.getMeter().iterator().next();
                    EventUtil.sendEvent("Door Alarm", TargetClass.valueOf(meter.getMeterType().getName()),
                            meter.getMdsId(), ed.getEventTime(),  new String[][]{{"doorState", ed.getEventStatus()==1? "Open":"Close"}},
                            event);
                }
            }
            else if (message instanceof PDU) {
                PDU pdu = (PDU)message;
                Vector<VariableBinding> vbs = pdu.getVariableBindings();
                for (VariableBinding vb : vbs) {
                    log.info("String : " + vb.toString() + ", Value : " + vb.toValueString());
                }
            }
        }
        catch (Exception e) {
            log.error(e, e);
        }
    }
    
    public void exceptionCaught(IoSession session, Throwable cause) {
        log.error(cause);
    }
}

class AppCodecFilter implements ProtocolCodecFactory {

    public ProtocolEncoder getEncoder(IoSession session) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public ProtocolDecoder getDecoder(IoSession session) throws Exception {
        return new AppDecoder();
    }
}

class AppDecoder extends ProtocolDecoderAdapter {
    Log log = LogFactory.getLog(AppDecoder.class);
    
    public void decode1(IoSession session, IoBuffer in, ProtocolDecoderOutput out)
    throws Exception {
        log.info(in.getHexDump());
        ByteBuffer buf = in.buf();
        // in.skip(in.limit());
        
        // buf.flip();
        log.info("POS[" + buf.position()+"]");
        byte[] header = new byte[4];
        buf.get(header);
        log.info("POS[" + buf.position()+"]");
        buf.position(0);
    }
    
    public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out)
            throws Exception {
        log.info(in.getHexDump() + " " + in.remaining());
        
        if (in.remaining() < 5) {
            in.reset();
            return;
        }
        
        // SNMP 또는 Locating 정보인지 구분
        byte[] header = new byte[4];
        in.get(header);
        
        // Locating
        if (ByteUtilities.asHex(header).equalsIgnoreCase("F1AAAA01")) {
            List<LocatingData> list = new ArrayList<LocatingData>();
            byte[] id = new byte[1];
            byte[] len = new byte[1];
            byte[] v;
            
            LocatingData data = new LocatingData();
            while (in.remaining() != 0) {
                // id
                in.get(id);
                // len
                in.get(len);
                // var
                v = new byte[(int)len[0]];
                in.get(v);
                
                switch (id[0]) {
                case 1 : data.setSerialNumber(new String(v));
                         break;
                case 2 : data.setDeviceName(new String(v));
                         break;
                case 3 : String mac = Hex.decode(v);
                         data.setMacAddress(mac.substring(0,2)+"-" +
                                 mac.substring(2, 4) + "-" +
                                 mac.substring(4, 6) + "-" +
                                 mac.substring(6, 8) + "-" +
                                 mac.substring(8, 10) + "-" +
                                 mac.substring(10));
                         break;
                case 4 : data.setIpAddress(DataUtil.getIntToByte(v[0])+"."+
                                           DataUtil.getIntToByte(v[1])+"."+
                                           DataUtil.getIntToByte(v[2])+"."+
                                           DataUtil.getIntToByte(v[3]));
                         break;
                case 5 : data.setPort(DataUtil.getIntToBytes(v));
                         break;
                case 6 : data.setFwVer(DataUtil.getIntToByte(v[0])+"."+DataUtil.getIntToByte(v[1]));
                }
            }
            out.write(data);
        }
        // Door Open
        else if (ByteUtilities.asHex(header).equalsIgnoreCase("F1AAAA02")) {
            byte[] id = new byte[1];
            byte[] len = new byte[1];
            byte[] v;
            
            EventData data = new EventData();
            while (in.remaining() != 0) {
                // id
                in.get(id);
                // len
                in.get(len);
                // var
                v = new byte[(int)len[0]];
                in.get(v);
                
                switch (id[0]) {
                case 1 : data.setSerialNumber(new String(v));
                         break;
                case 2 : String eventTime = null;
                         eventTime = DataUtil.getIntTo2Byte(new byte[]{v[0], v[1]})+"";
                         eventTime += ((int)v[2] < 10? "0":"") + (int)v[2];
                         eventTime += ((int)v[3] < 10? "0":"") + (int)v[3];
                         eventTime += ((int)v[4] < 10? "0":"") + (int)v[4];
                         eventTime += ((int)v[5] < 10? "0":"") + (int)v[5];
                         eventTime += ((int)v[6] < 10? "0":"") + (int)v[6];
                         data.setEventTime(eventTime);
                         break;
                case 3 : data.setEventId((int)v[0]);
                         break;
                case 4 : data.setEventStatus((int)v[0]);
                         break;
                }
            }
            out.write(data);
        }
        /*
        else {
            // 처음으로 돌아가서 SNMP 처리를 한다.
            in.position(0);
            // Decode the bytes using SNMP4j API's
            PDU pdu = new PDU();
            try {
                BERInputStream berStream = new BERInputStream(in.buf());
                BER.MutableByte mutableByte = new BER.MutableByte();
                int length = BER.decodeHeader(berStream, mutableByte);
                log.info("LENGTH[" + length + "]");
                int startPos = (int)berStream.getPosition();
                log.info("START POS[" + startPos + "]");
                
                if (mutableByte.getValue() != BER.SEQUENCE) {
                    throw new IOException("SNMPv2c PDU must start with a SEQUENCE");
                }
                
                Integer32 version = new Integer32();
                version.decodeBER(berStream);
                
                // decode community string
                OctetString securityName = new OctetString();
                securityName.decodeBER(berStream);
                
                // decode the remaining PDU
                pdu.decodeBER(berStream);
                log.info("PDU - " + pdu);
                out.write(pdu);
                in.skip(in.remaining());
            }
            catch (Exception e) {
                log.error(e);
            }
        } */
    }
}

class LocatingData {
    private String deviceName;
    private String serialNumber;
    private String macAddress;
    private String ipAddress;
    private int port;
    private String fwVer;
    
    public String getDeviceName() {
        return deviceName;
    }
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
    public String getSerialNumber() {
        return serialNumber;
    }
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
    public String getMacAddress() {
        return macAddress;
    }
    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }
    public String getIpAddress() {
        return ipAddress;
    }
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    public int getPort() {
        return port;
    }
    public void setPort(int port) {
        this.port = port;
    }
    public String getFwVer() {
        return fwVer;
    }
    public void setFwVer(String fwVer) {
        this.fwVer = fwVer;
    }
    
}

class EventData {
    private String serialNumber;
    private String eventTime;
    private int eventId;
    private int eventStatus;
    
    public String getSerialNumber() {
        return serialNumber;
    }
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
    public String getEventTime() {
        return eventTime;
    }
    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }
    public int getEventId() {
        return eventId;
    }
    public void setEventId(int eventId) {
        this.eventId = eventId;
    }
    public int getEventStatus() {
        return eventStatus;
    }
    public void setEventStatus(int eventStatus) {
        this.eventStatus = eventStatus;
    }
}
