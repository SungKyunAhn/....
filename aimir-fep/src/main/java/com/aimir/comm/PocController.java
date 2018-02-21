package com.aimir.comm;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.californium.core.CoapClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.device.NetworkInfoLogDao;
import com.aimir.fep.meter.parser.DLMSEtypeTable.DLMSEtypeTable;
import com.aimir.fep.meter.parser.DLMSEtypeTable.DLMSEtypeTag;
import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.model.device.MCU;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.device.NetworkInfoLog;
import com.aimir.util.Condition;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.Condition.Restriction;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Controller("/poc")
@Transactional
public class PocController {
    private static Log log = LogFactory.getLog(PocController.class);
    private final int MODEM_PORT = 65363;
    
    @Autowired
    MCUDao mcuDao; 
    
    @Autowired
    ModemDao modemDao;
    
    @Autowired
    NetworkInfoLogDao nlogDao;
    
    @Autowired
    MeterDao meterDao;
    
    private String[] getAddrHops(String targetNode) {
        String addr = "";
        String hops = "";
        String targetType = "Modem";
        
        MCU mcu = mcuDao.get(targetNode);
        if (mcu == null) {
            Modem modem = modemDao.get(targetNode);
            
            if (modem != null) {
                addr = modem.getIpAddr();
                
                //if (modem.getModem() != null)
                    //hops = modem.getModem().getNameSpace();
            }
        }
        else {
            targetType = "MCU";
            addr = mcu.getIpv6Addr();
        }
        
        return new String[]{addr, hops, targetType};
    }
    
    @RequestMapping(method=RequestMethod.GET, value="/ping/{ant}/{temperature}/{weather}/{targetNode}/{count}/{packetSize}")
    @Transactional(propagation=Propagation.REQUIRES_NEW, rollbackFor=Exception.class)
    public ResponseEntity<String> ping_icmp(@PathVariable double ant, 
            @PathVariable double temperature,
            @PathVariable String weather,
            @PathVariable String targetNode, 
            @PathVariable int count, @PathVariable int packetSize) {
        log.info("ANTENNA[" + ant + "] TEMP[" + temperature + 
                "] WEATHER[" + weather + "] TARGET_NODE[" + targetNode + 
                "] COUNT[" + count + "] PACKET_SIZE[" + packetSize + "]");
        double avgRtt = 0.0;
        double avgTtl = 0.0;
        double loss = 0.0;
        int rcvPacketSize = 0;
        String addr = null;
        ResponseEntity<String> ni = null;
        String targetType = "Modem";
        
        /*
        ICMP icmp = new ICMP();
        Map<Integer, Map<String, Object>> result = null;
        try {
            String[] addrHops = getAddrHops(targetNode);
            
            addr = addrHops[0];
            targetType = addrHops[2];
            
            if (addr != null && !"".equals(addr)) {
                int tempCount = count + 1;
                result = icmp.ping(addr, tempCount, packetSize);
                // network info
                
                if (targetType.equals("Modem"))
                    ni = getNetworkInfoRetry(targetNode);
            }
            else {
                loss = 100.0;
                avgRtt = -1;
            }
        }
        catch (Exception e) {
            log.error(e, e);
        }
        if (result != null) {
            int key = 0;
            for (Iterator<Integer> i = result.keySet().iterator(); i.hasNext(); ) {
                key = (int)i.next();
                if (key == 1) {
                    continue;
                }
                if (result.get(key).get("rcvPacketSize") != null) {
                    rcvPacketSize += (Integer)result.get(key).get("rcvPacketSize");
                }
                
                avgRtt += (Double)result.get(key).get("rtt");
                avgTtl += (Integer)result.get(key).get("ttl");
            }
            int res_cnt = result.size() - 1;
            log.info("TRY_CNT[" + count+ "] RES_CNT[" + res_cnt + 
                    "] RTT[" + avgRtt + "] TTL[" + avgTtl + "] RCV_PACKET_SIZE[" + rcvPacketSize + "]");
            
            if (res_cnt <= 0) res_cnt = 1;
            avgRtt /= (double)res_cnt;
            avgTtl /= (double)res_cnt;
            DecimalFormat df = new DecimalFormat("#.00");
            avgRtt = Double.parseDouble(df.format(avgRtt));
            
            int totalRcvPacketSize = (packetSize+8) * count;
            int lossRcvPacketSize = totalRcvPacketSize - rcvPacketSize;
            loss = ((double)lossRcvPacketSize / (double)totalRcvPacketSize) * 100.0;
            if (loss < 0.0) loss *= -1.0;
            loss = Double.parseDouble(df.format(loss));
        }
        else {
            loss = 100;
            avgRtt = -1;
        }
        
        NetworkInfoLog nlog = new NetworkInfoLog();
        nlog.setAntAttenuation(ant);
        nlog.setTargetNode(targetNode);
        nlog.setCommand("ICMP");
        nlog.setDateTime(DateTimeUtil.getDateString(new Date()));
        nlog.setTemperature(temperature);
        nlog.setWeather(weather);
        nlog.setLoss(loss);
        nlog.setTtl(avgTtl);
        nlog.setRtt(avgRtt);
        nlog.setPacketSize(packetSize);
        nlog.setIpAddr(addr);
        
        if (targetType.equals("Modem") && ni != null && ni.getStatusCode() == HttpStatus.OK) {
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(ni.getBody());
            JsonObject obj = element.getAsJsonObject();
            nlog.setRssi(obj.get("rssi").getAsDouble());
            nlog.setTxPower(obj.get("txPower").getAsDouble());
            nlog.setLinkBudget(obj.get("linkBudget").getAsDouble());
            nlog.setHops(obj.get("parentNode").getAsString());
        }
        
        log.info(nlog.toJSONString());
        nlogDao.add(nlog);
        */
        return null; //return new ResponseEntity<String>(nlog.toJSONString(), HttpStatus.OK);
    }
    
    @RequestMapping(method=RequestMethod.GET, value="/coap/{ant}/{temperature}/{weather}/{targetNode}/{count}")
    @Transactional(propagation=Propagation.REQUIRES_NEW, rollbackFor=Exception.class)
    public ResponseEntity<String> ping_coap(@PathVariable double ant, 
            @PathVariable double temperature,
            @PathVariable String weather,
            @PathVariable String targetNode, 
            @PathVariable int count) {
        log.info("ANTENNA[" + ant + "] TEMP[" + temperature + 
                "] WEATHER[" + weather + "] TARGET_NODE[" + targetNode + 
                "] COUNT[" + count + "]");
        double avgRtt = 0.0;
        double loss = 0.0;
        String addr = null;
        ResponseEntity<String> ni = null;
        String targetType = "Modem";
        
        String[] addrHops = getAddrHops(targetNode);
        
        addr = addrHops[0];
        targetType = addrHops[2];
        int _count = 0;
        
        if (addr != null && !"".equals(addr)) {
            CoapClient client = new CoapClient("coap://["+addr+"]:5683");
            long startTime = 0;
            for (int i = 0; i < count; i++) {
                startTime = System.currentTimeMillis();
                try {
                    if (!client.ping(5000)) {
                        log.info("FALSE URI[" +client.getURI().toString() + "]");
                        if (client.getEndpoint() != null) {
                            log.info(client.getEndpoint().getAddress());
                        }
                        loss++;
                    }
                    else _count++;
                }
                catch (Exception e) {
                    log.error(e, e);
                    loss++;
                }
                avgRtt += (System.currentTimeMillis() - startTime);
            }
            
            if (_count == 0) _count = 1;
            
            loss = (loss / (double)count) * 100.0;
            avgRtt /= _count;
            
            DecimalFormat df = new DecimalFormat("#.00");
            loss = Double.parseDouble(df.format(loss));
            avgRtt = Double.parseDouble(df.format(avgRtt));
            // client.shutdown();
            
            log.info("TRY_CNT[" + count + "] RES_CNT[" + _count + "] LOSS[" + loss + "] RTT[" + avgRtt + "]");
            
            if (targetType.equals("Modem"))
                ni = getNetworkInfoRetry(targetNode);
        }
        else {
            loss = 100.0;
            avgRtt = -1;
        }
        
        NetworkInfoLog nlog = new NetworkInfoLog();
        nlog.setAntAttenuation(ant);
        nlog.setTargetNode(targetNode);
        nlog.setCommand("COAP");
        nlog.setDateTime(DateTimeUtil.getDateString(new Date()));
        nlog.setTemperature(temperature);
        nlog.setWeather(weather);
        nlog.setLoss(loss);
        nlog.setCoapResponseTime(avgRtt);
        nlog.setIpAddr(addr);
        
        if (targetType.equals("Modem") && ni != null && ni.getStatusCode() == HttpStatus.OK) {
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(ni.getBody());
            JsonObject obj = element.getAsJsonObject();
            nlog.setRssi(obj.get("rssi").getAsDouble());
            nlog.setTxPower(obj.get("txPower").getAsDouble());
            nlog.setLinkBudget(obj.get("linkBudget").getAsDouble());
            nlog.setHops(obj.get("parentNode").getAsString());
        }
        
        log.info(nlog.toJSONString());
        nlogDao.add(nlog);
        return new ResponseEntity<String>(nlog.toJSONString(), HttpStatus.OK);
    }
    
    @RequestMapping(method=RequestMethod.GET, value="/metering/{ant}/{temperature}/{weather}/{targetNode}/{count}")
    @Transactional(propagation=Propagation.REQUIRES_NEW, rollbackFor=Exception.class)
    public ResponseEntity<String> getMeteringData(@PathVariable double ant, 
            @PathVariable double temperature,
            @PathVariable String weather,
            @PathVariable String targetNode, 
            @PathVariable int count) {
        DatagramSocket socket = null;
        double avgRtt = 0.0;
        double loss = 0.0;
        String addr = null;
        long startTime = 0;
        byte[] result = null;
        String obisValue = null;
        ResponseEntity<String> ni = null;
        String targetType = "Modem";
        
        try {
            String[] addrHops = getAddrHops(targetNode);
            
            addr = addrHops[0];
            targetType = addrHops[2];
            int _count = 0;
            
            if (targetType.equals("Modem") && addr != null && !"".equals(addr)) {
                socket = new DatagramSocket();
                startTime = System.currentTimeMillis();
                socket.connect(InetAddress.getByName(addr), MODEM_PORT);
                socket.setSoTimeout(10000);
                byte[] b = new byte[16];
                DatagramPacket rcv = new DatagramPacket(b, b.length);
                
                for (int i = 0; i < count; i++) {
                    try {
                        socket.send(new DatagramPacket(new byte[]{0x00, 0x00, 0x5E, 0x34, 0x00, 0x01}, 6));
                        rcv = new DatagramPacket(b, b.length);
                        socket.receive(rcv);
                        _count++;
                    }
                    catch (IOException e) {
                        loss++;
                    }
                    avgRtt += (System.currentTimeMillis() - startTime);
                    startTime = System.currentTimeMillis();
                }
                result = rcv.getData();
                socket.disconnect();
                log.info(Hex.decode(b));
                List<DLMSEtypeTag> tags = getDLMSTag(b);
                obisValue = Double.toString((Long)tags.get(0).getValue() / 100.0);
                
                DecimalFormat df = new DecimalFormat("#.00");
                if (_count == 0) _count = 1;
                avgRtt /= _count;
                loss = (loss / (double)count) * 100.0;
                
                loss = Double.parseDouble(df.format(loss));
                avgRtt = Double.parseDouble(df.format(avgRtt));
                
                if (targetType.equals("Modem"))
                    ni = getNetworkInfoRetry(targetNode);
            }
            else {
                avgRtt = -1;
                loss = 100.0;
            }
        }
        catch (Exception e) {
            log.error(e, e);
            avgRtt = -1;
            loss = 100.0;
        }
        finally {
            if (socket != null) {
                try {
                    socket.close();
                }
                catch (Exception e) {}
            }
        }
        
        NetworkInfoLog nlog = new NetworkInfoLog();
        nlog.setAntAttenuation(ant);
        nlog.setTargetNode(targetNode);
        nlog.setCommand("OBIS");
        nlog.setDateTime(DateTimeUtil.getDateString(new Date()));
        nlog.setTemperature(temperature);
        nlog.setWeather(weather);
        nlog.setLoss(loss);
        nlog.setObisCode("0.0.94.52.0.1");
        nlog.setObisResult(Hex.decode(result));
        nlog.setObisResponseTime(avgRtt);
        nlog.setObisValue(obisValue);
        nlog.setIpAddr(addr);
        
        if (targetType.equals("Modem") && ni != null && ni.getStatusCode() == HttpStatus.OK) {
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(ni.getBody());
            JsonObject obj = element.getAsJsonObject();
            nlog.setRssi(obj.get("rssi").getAsDouble());
            nlog.setTxPower(obj.get("txPower").getAsDouble());
            nlog.setLinkBudget(obj.get("linkBudget").getAsDouble());
            nlog.setHops(obj.get("parentNode").getAsString());
        }
        
        log.info(nlog.toJSONString());
        nlogDao.add(nlog);
        
        // target type이 modem이면 
        return new ResponseEntity<String>(nlog.toJSONString(), HttpStatus.OK);
    }
    
    @RequestMapping(method=RequestMethod.GET, value="/network2/{targetNode}")
    @Transactional(propagation=Propagation.REQUIRES_NEW, rollbackFor=Exception.class)
    public ResponseEntity<String> getNetworkInfoRetry(@PathVariable String targetNode) {
        ResponseEntity<String> network = getNetworkInfo(targetNode);
        if (network.getStatusCode() == HttpStatus.BAD_REQUEST)
            network = getNetworkInfo(targetNode);
        
        return network;
    }
    
    @RequestMapping(method=RequestMethod.GET, value="/network/{targetNode}")
    @Transactional(propagation=Propagation.REQUIRES_NEW, rollbackFor=Exception.class)
    public ResponseEntity<String> getNetworkInfo(String targetNode) {
        DatagramSocket socket = null;
        log.info("TARGET_NODE[" + targetNode + "]");
        try {
            String[] addrHops = getAddrHops(targetNode);
            String addr = addrHops[0];
            
            socket = new DatagramSocket();
            socket.connect(InetAddress.getByName(addr), MODEM_PORT);
            socket.send(new DatagramPacket(new byte[]{0x00, 0x00, 0x5E, 0x34, 0x00, 0x02}, 6));
            socket.setSoTimeout(10000);
            byte[] b = new byte[32];
            DatagramPacket rcv = new DatagramPacket(b, b.length);
            socket.receive(rcv);
            b = rcv.getData();
            log.info(Hex.decode(b));
            socket.disconnect();
            
            DecimalFormat df = new DecimalFormat("#.####");
            List<DLMSEtypeTag> tags = getDLMSTag(b);
            // 0:structure, 1:rssi, 2:tx power, 3:link budget, 4:parent node
            double rssi = (Integer)tags.get(1).getValue();
            if (rssi > 127) {
                rssi -= 256;
            }
            double txPower = (Integer)tags.get(2).getValue();
            if (txPower > 127) {
                txPower -= 256;
            }
            double linkBudget = Double.parseDouble(df.format((Float)tags.get(3).getValue()));
            String parentNode = ((OCTET)tags.get(4).getValue()).toHexString();
            log.info("RSSI[" + rssi + "] TX_POWER[" + txPower + 
                    "] LINK_BUDGET[" + linkBudget + "] parentNode[" + parentNode + "]");
            
            if (parentNode != null && "000000FFFE000001".equals(parentNode)) {
                parentNode = "0";
                Modem child = modemDao.get(targetNode);
                child.setModem(null);
                modemDao.update(child);
            }
            else {
                Modem child = modemDao.get(targetNode);
                Modem parent = modemDao.get(parentNode);
                child.setModem(parent);
                modemDao.update(child);
            }
            
            StringBuffer buf = new StringBuffer();
            buf.append("{");
            buf.append("\"rssi\":\""+ rssi + "\"");
            buf.append(",\"txPower\":\""+ txPower + "\"");
            buf.append(",\"linkBudget\":\""+ linkBudget + "\"");
            buf.append(",\"parentNode\":\""+ parentNode + "\"");
            buf.append("}");
            
            return new ResponseEntity<String>(buf.toString(), HttpStatus.OK);
        }
        catch (Exception e) {
            log.error(e, e);
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        finally {
            if (socket != null) {
                try {
                    socket.close();
                }
                catch (Exception e) {}
            }
        }
    }
    
    @RequestMapping(method=RequestMethod.GET, value="/ondemand/{meterId}/{syyyymmddhhmm}/{eyyyymmddhhmm}")
    public ResponseEntity<String> cmdOnDemand(@PathVariable String meterId,
            @PathVariable String syyyymmddhhmm, @PathVariable String eyyyymmddhhmm) {
        DatagramSocket socket = null;
        log.info("METER[" + meterId + "]");
        try {
            Meter meter = meterDao.get(meterId);
            String addr = meter.getModem().getIpAddr();
            
            byte[] indexCount = getLpIndexCount(meter.getLpInterval(), syyyymmddhhmm, eyyyymmddhhmm);
            
            socket = new DatagramSocket();
            socket.connect(InetAddress.getByName(addr), MODEM_PORT);
            socket.send(new DatagramPacket(new byte[]{0x00, 0x00, 0x5E, 0x34, 0x00, 0x03,
                    indexCount[0], indexCount[1]}, 8));
            socket.setSoTimeout(10000);
            /*
            byte[] b = new byte[4096];
            DatagramPacket rcv = new DatagramPacket(b, b.length);
            socket.receive(rcv);
            b = new byte[rcv.getLength()];
            System.arraycopy(rcv.getData(), 0, b, 0, b.length);
            log.info(Hex.decode(b));
            */
            socket.disconnect();
            /*
            PocMDParser parser = new PocMDParser();
            parser.parse(b);
            saver.save(parser);
            */
            return new ResponseEntity<String>(HttpStatus.OK);
        }
        catch (Exception e) {
            log.error(e, e);
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        finally {
            if (socket != null) {
                try {
                    socket.close();
                }
                catch (Exception e) {}
            }
        }
    }
    
    @RequestMapping(method=RequestMethod.GET, value="/networklog/{command}/{startDate}/{endDate}")
    @Transactional(propagation=Propagation.REQUIRES_NEW, rollbackFor=Exception.class)
    public ResponseEntity<String> getNetworkInfoLog(@PathVariable String command,
            @PathVariable String startDate, @PathVariable String endDate,
            @RequestParam("page") int page, @RequestParam("limit") int limit) {
        log.info("COMMAND[" + command + "] START_DATE[" + startDate + 
                "] END_DATE[" + endDate + "] PAGE[" + page + "] LIMIT[" + limit + "]");
        try {
            Set<Condition> condition = new HashSet<Condition>();
            if (command != null && !"All".equalsIgnoreCase(command))
                condition.add(new Condition("id.command", new Object[]{command}, null, Restriction.EQ));
            condition.add(new Condition("id.dateTime", new Object[]{startDate+"000000", endDate+"235959"}, null, Restriction.BETWEEN));
            
            List<Object> total = nlogDao.findTotalCountByConditions(condition);
            NetworkInfoLog[] logs = nlogDao.list(command, startDate, endDate, page, limit);
            
            StringBuffer buf = new StringBuffer();
            buf.append("{");
            buf.append("\"total\":\"" + total.get(0) + "\"");
            buf.append(",\"list\":[");
            NetworkInfoLog n = null;
            for (int i = 0; i < logs.length; i++) {
                n = logs[i];
                if (i != 0) buf.append(",");
                buf.append(n.toJSONString());
            }
            buf.append("]}");
            
            return new ResponseEntity<String>(buf.toString(), HttpStatus.OK);
        }
        catch (Exception e) {
            log.error(e, e);
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    public byte[] getLpIndexCount(int lpInterval, String syyyymmddhhmm, String eyyyymmddhhmm)
    throws ParseException
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
        Calendar current = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        
        end.setTime(sdf.parse(eyyyymmddhhmm));
        int index = 0;
        for (; ;index++) {
            if (end.compareTo(current) >= 0)
                break;
            else
                end.add(Calendar.MINUTE, lpInterval);
        }
        
        if (index > 0) index--;
        
        Calendar start = Calendar.getInstance();
        start.setTime(sdf.parse(syyyymmddhhmm));
        end.setTime(sdf.parse(eyyyymmddhhmm));
        int count = 0;
        for (;;count++) {
            if (start.compareTo(end) >= 0)
                break;
            else
                start.add(Calendar.MINUTE, 15);
        }
        count++;
        
        log.info("INDEX[" + index + "] COUNT[" + count + "]");
        return new byte[]{DataUtil.getByteToInt(index), DataUtil.getByteToInt(count)};
    }
    
    private List<DLMSEtypeTag> getDLMSTag(byte[] data) throws Exception {
        log.debug("DLMS parse:"+Hex.decode(data));

        String obisCode = "";
        int clazz = 0;
        int attr = 0;

        int pos = 0;
        int len = 0;
        // DLMS Header OBIS(6), CLASS(2), ATTR(1), LENGTH(2)
        // DLMS Tag Tag(1), DATA or LEN/DATA (*)
        byte[] OBIS = new byte[6];
        byte[] CLAZZ = new byte[2];
        byte[] ATTR = new byte[1];
        byte[] LEN = new byte[2];
        byte[] TAGDATA = null;
        
        DLMSEtypeTable dlms = null;
        while ((pos+OBIS.length) < data.length) {
            log.debug("POS[" + pos + "] Data.LEN[" + data.length + "]");
            dlms = new DLMSEtypeTable();
            System.arraycopy(data, pos, OBIS, 0, OBIS.length);
            pos += OBIS.length;
            obisCode = Hex.decode(OBIS);
            log.debug("OBIS[" + obisCode + "]");
            dlms.setObis(obisCode);
            
            System.arraycopy(data, pos, CLAZZ, 0, CLAZZ.length);
            pos += CLAZZ.length;
            clazz = DataUtil.getIntToBytes(CLAZZ);
            log.debug("CLASS[" + clazz + "]");
            dlms.setClazz(clazz);
            
            System.arraycopy(data, pos, ATTR, 0, ATTR.length);
            pos += ATTR.length;
            attr = DataUtil.getIntToBytes(ATTR);
            log.debug("ATTR[" + attr + "]");
            dlms.setAttr(attr);

            System.arraycopy(data, pos, LEN, 0, LEN.length);
            pos += LEN.length;
            len = DataUtil.getIntTo2Byte(LEN);
            log.debug("LENGTH[" + len + "]");
            dlms.setLength(len);

            TAGDATA = new byte[len];
            if (pos + TAGDATA.length <= data.length) {
                System.arraycopy(data, pos, TAGDATA, 0, TAGDATA.length);
                pos += TAGDATA.length;
            }
            else {
                System.arraycopy(data, pos, TAGDATA, 0, data.length-pos);
                pos += data.length-pos;
            }
            
            
            dlms.parseDlmsTag(TAGDATA);
        }
        
        return dlms.getDlmsTags();
    }
}
