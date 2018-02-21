package com.aimir.fep.bypass.actions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.session.IoSession;

import com.aimir.fep.bypass.BypassDevice;
import com.aimir.fep.protocol.fmp.client.Client;
import com.aimir.fep.protocol.fmp.datatype.SMIValue;
import com.aimir.fep.protocol.fmp.datatype.STREAM;
import com.aimir.fep.protocol.fmp.frame.service.CommandData;
import com.aimir.fep.util.CRCUtil;
import com.aimir.fep.util.DataUtil;
import com.aimir.util.DateTimeUtil;

public class CommandAction_NL extends CommandAction {
    private static Log log = LogFactory.getLog(CommandAction_NL.class);
    
    @Override
    public void executeBypass(byte[] bs, IoSession session) throws Exception
    {
        
    }
    
    @Override
    public void execute(String cmd, SMIValue[] smiValues, IoSession session) 
    throws Exception
    {
        try {
            BypassDevice bd = (BypassDevice)session.getAttribute(session.getRemoteAddress());
            // GG 101.2.1 모뎀/미터 식별 응답이 오면 커맨드를 실행한다.
            if (cmd.equals("cmdIdentifyDevice")) {
                bd.setModemId(smiValues[0].getVariable().toString());
                bd.setMeterId(smiValues[1].getVariable().toString());
                
                // cmdOTAStart(session);
                // cmdOndemandMetering(session);
                //cmdFactorySetting(session);
                //cmdReadModemConfiguration(session);
                //cmdRelayDisconnect(session);
                //cmdRelayReconnect(session);
                //cmdRelayStatus(session);
                //cmdResetModem(session);
                cmdSetApn(session);
                //cmdSetBypassStart(session);
                //cmdSetMeteringInterval(session);
                //cmdSetModemResetInterval(session);
                //cmdSetServerIpPort(session);
                //cmdSetTime(session);
                //cmdUploadMeteringData(session);
            }
            else if (cmd.equals("cmdOTAStart")) {
                log.debug("modemId[" + bd.getModemId() + "] meterId[" + bd.getMeterId() + "]");
                bd.setModemModel(smiValues[0].getVariable().toString());
                bd.setFwVersion(smiValues[1].getVariable().toString());
                bd.setBuildno(smiValues[2].getVariable().toString());
                bd.setHwVersion(smiValues[3].getVariable().toString());
                bd.setPacket_size(Integer.parseInt(smiValues[4].getVariable().toString()));
                
                // TODO 위 정보를 모뎀에 갱신한다.
                cmdSendImage(session);
            }
            else if (cmd.equals("cmdSendImage")) {
                cmdSendImage(session);
            }
            else if (cmd.equals("cmdOTAEnd")) {
                // 상태값을 받아서 실패하면 다시 시도하도록 한다.
                int status = Integer.parseInt(smiValues[0].getVariable().toString());
            }
            else if (cmd.equals("cmdReadModemConfiguration")) {
                // 모뎀 설정 정보를 갱신한다.
            }
            else if (cmd.equals("cmdRelayStatus")) {
                int status = Integer.parseInt(smiValues[0].getVariable().toString());
                setMeterStatus(((BypassDevice)session.getAttribute(session.getRemoteAddress())).getMeterId(), status);
            }
            else if (cmd.equals("cmdRelayDisconnect")) {
                int status = Integer.parseInt(smiValues[0].getVariable().toString());
                setMeterStatus(((BypassDevice)session.getAttribute(session.getRemoteAddress())).getMeterId(), status);
            }
            else if (cmd.equals("cmdRelayReconnect")) {
                int status = Integer.parseInt(smiValues[0].getVariable().toString());
                setMeterStatus(((BypassDevice)session.getAttribute(session.getRemoteAddress())).getMeterId(), status);
            }
        }
        catch (Exception e) {
            log.error(e, e);
        }
    }
    
	@Override
	public CommandData executeBypassClient(byte[] frame, IoSession session) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
    
    /*
     * OTA 시작 명령
     */
    public void cmdOTAStart(IoSession session)
    throws Exception
    {
        // TODO ota 비동기 이력의 인자에서 파일 경로를 가져와야 한다.
        ByteArrayOutputStream out = null;
        FileInputStream in = null;
        try {
            BypassDevice bd = (BypassDevice)session.getAttribute(session.getRemoteAddress());
            // BypassDevice의 args에서 파일명을 가져와야 한다.
            // File file = new File((String)bd.getArgs().get(0));
            File file = new File("D:\\Downloads\\OTA_TEST.bin");
            out = new ByteArrayOutputStream();
            in = new FileInputStream(file);
            
            int len = 0;
            byte[] b = new byte[1024];
            while ((len=in.read(b)) != -1) {
                out.write(b, 0, len);
            }
            
            long filelen = file.length();
            // sendImage 에서 사용하기 위해 바이너리를 전역 변수에 넣는다.
            bd.setFw_bin(out.toByteArray());
            // sendImage에서 바이너리를 읽어올 수 있도록 하기 위해 전역 변수에 넣는다.
            bd.setFw_in(new ByteArrayInputStream(bd.getFw_bin(), 0, bd.getFw_bin().length));
            // int crc = DataUtil.getIntTo2Byte(FrameUtil.getCRC(bd.getFw_bin()));
            byte[] crc = CRCUtil.Calculate_ZigBee_Crc(bd.getFw_bin(), (char)0x0000);
            DataUtil.convertEndian(crc);
            
            String ns = (String)session.getAttribute("nameSpace");
            List<SMIValue> params = new ArrayList<SMIValue>();
            
            params.add(DataUtil.getSMIValueByObject(ns, "cmdModemFwImageLength", Long.toString(filelen)));
            params.add(DataUtil.getSMIValueByObject(ns, "cmdModemFwImageCRC", Integer.toString(DataUtil.getIntTo2Byte(crc))));
            sendCommand(session, "cmdOTAStart", params);
        }
        finally {
            if (out != null) out.close();
            if (in != null) in.close();
        }
    }
    
    /*
     * 펌웨어 바이너리를 보내는 명령
     * 한 패킷 전송 후 응답을 받고 다음 패킷을 보내야 하므로 offset과 fw_in이 전역 변수로 선언되었다.
     */
    public void cmdSendImage(IoSession session) throws Exception
    {
        BypassDevice bd = (BypassDevice)session.getAttribute(session.getRemoteAddress());
        log.debug("offset[" + bd.getOffset() + "]");
        String ns = (String)session.getAttribute("nameSpace");
        byte[] b = new byte[bd.getPacket_size()];
        int len = -1;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        if ((len = bd.getFw_in().read(b)) != -1) {
            out.write(b, 0, len);
            List<SMIValue> params = new ArrayList<SMIValue>();
            params.add(DataUtil.getSMIValueByObject(ns, "cmdImageAddress", Integer.toString(bd.getOffset())));
            params.add(DataUtil.getSMIValueByObject(ns, "cmdImageSize", Integer.toString(len)));
            // 바이트 스트립을 문자열로 변환 후 바이트로 변환시 원본이 손상되어 STREAM을 바로 사용하도록 한다.
            params.add(new SMIValue(DataUtil.getOIDByMIBName(ns, "cmdImageData"), new STREAM(out.toByteArray())));
            bd.setOffset(bd.getOffset() + len);
            sendCommand(session, "cmdSendImage", params);
        }
        out.close();
        
        // 전송이 끝나면 종류 명령을 보낸다.
        if (bd.getOffset() == bd.getFw_bin().length) {
            bd.getFw_in().close();
            sendCommand(session, "cmdOTAEnd", null);
        }
    }
    
    public void cmdUploadMeteringData(IoSession session) throws Exception
    {
        List<SMIValue> params = new ArrayList<SMIValue>();
        sendCommand(session, "cmdUploadMeteringData", params);
    }
    
    public void cmdResetModem(IoSession session) throws Exception
    {
        BypassDevice bd = (BypassDevice)session.getAttribute(session.getRemoteAddress());
        int delayTime = 10;
        
        if (bd.getArgs().size() > 0) {
            delayTime = (Integer)bd.getArgs().get(0);
        }
        String ns = (String)session.getAttribute("nameSpace");
        List<SMIValue> params = new ArrayList<SMIValue>();
        params.add(DataUtil.getSMIValueByObject(ns, "cmdResetModemDelayTime", Integer.toString(delayTime)));
        sendCommand(session, "cmdResetModem", params);
    }
    
    public void cmdFactorySetting(IoSession session) throws Exception
    {
        BypassDevice bd = (BypassDevice)session.getAttribute(session.getRemoteAddress());
        int code = 0x0314;
        
        if (bd.getArgs().size() > 0) {
            code = (Integer)bd.getArgs().get(0);
        }
        String ns = (String)session.getAttribute("nameSpace");
        List<SMIValue> params = new ArrayList<SMIValue>();
        params.add(DataUtil.getSMIValueByObject(ns, "cmdFactorySettingCode", Integer.toString(code)));
        sendCommand(session, "cmdFactorySetting", params);
    }
    
    public void cmdReadModemConfiguration(IoSession session) throws Exception
    {
        sendCommand(session, "cmdReadModemConfiguration", null);
    }
    
    public void cmdSetTime(IoSession session) throws Exception
    {
        String timestamp = DateTimeUtil.getDateString(new Date());
        
        String ns = (String)session.getAttribute("nameSpace");
        List<SMIValue> params = new ArrayList<SMIValue>();
        params.add(DataUtil.getSMIValueByObject(ns, "cmdSetTimestamp", timestamp));
        sendCommand(session, "cmdSetTime", params);
    }
    
    public void cmdSetModemResetInterval(IoSession session) throws Exception
    {
        BypassDevice bd = (BypassDevice)session.getAttribute(session.getRemoteAddress());
        int interval = 60;
        
        if (bd.getArgs().size() > 0) {
            interval = (Integer)bd.getArgs().get(0);
        }
        String ns = (String)session.getAttribute("nameSpace");
        List<SMIValue> params = new ArrayList<SMIValue>();
        params.add(DataUtil.getSMIValueByObject(ns, "cmdModemResetIntervalMinute", Integer.toString(interval)));
        sendCommand(session, "cmdModemResetInterval", params);
    }
    
    public void cmdSetMeteringInterval(IoSession session) throws Exception
    {
        BypassDevice bd = (BypassDevice)session.getAttribute(session.getRemoteAddress());
        int interval = 15;
        
        if (bd.getArgs().size() > 0) {
            interval = (Integer)bd.getArgs().get(0);
        }
        String ns = (String)session.getAttribute("nameSpace");
        List<SMIValue> params = new ArrayList<SMIValue>();
        params.add(DataUtil.getSMIValueByObject(ns, "cmdSetMeteringIntervalMinute", Integer.toString(interval)));
        sendCommand(session, "cmdSetMeteringInterval", params);
    }
    
    public void cmdSetServerIpPort(IoSession session) throws Exception
    {
        BypassDevice bd = (BypassDevice)session.getAttribute(session.getRemoteAddress());
        String ip = "187.1.10.58";
        int port = 8000;
        
        if (bd.getArgs().size() > 1) {
            ip = (String)bd.getArgs().get(0);
            port = (Integer)bd.getArgs().get(1);
        }
        String ns = (String)session.getAttribute("nameSpace");
        List<SMIValue> params = new ArrayList<SMIValue>();
        params.add(DataUtil.getSMIValueByObject(ns, "cmdSetServerIp", ip));
        params.add(DataUtil.getSMIValueByObject(ns, "cmdSetServerPort", Integer.toString(port)));
        sendCommand(session, "cmdSetServerIpPort", params);
    }
    
    public void cmdSetApn(IoSession session) throws Exception
    {
        BypassDevice bd = (BypassDevice)session.getAttribute(session.getRemoteAddress());
        String apnAddress = "test";
        String apnId = "test";
        String apnPassword = "test";
        
        if (bd.getArgs().size() > 2) {
            apnAddress = (String)bd.getArgs().get(0);
            apnId = (String)bd.getArgs().get(1);
            apnPassword = (String)bd.getArgs().get(2);
        }
        String ns = (String)session.getAttribute("nameSpace");
        List<SMIValue> params = new ArrayList<SMIValue>();
        params.add(DataUtil.getSMIValueByObject(ns, "cmdSetApnAddress", apnAddress));
        params.add(DataUtil.getSMIValueByObject(ns, "cmdSetApnID", apnId));
        params.add(DataUtil.getSMIValueByObject(ns, "cmdSetApnPassword", apnPassword));
        sendCommand(session, "cmdSetApn", params);
    }
    
    public void cmdSetBypassStart(IoSession session) throws Exception
    {
        BypassDevice bd = (BypassDevice)session.getAttribute(session.getRemoteAddress());
        int timeout = 10;
        
        if (bd.getArgs().size() > 0) {
            timeout = (Integer)bd.getArgs().get(0);
        }
        
        String ns = (String)session.getAttribute("nameSpace");
        List<SMIValue> params = new ArrayList<SMIValue>();
        params.add(DataUtil.getSMIValueByObject(ns, "cmdSetBypassStartTimeout", Integer.toString(timeout)));
        sendCommand(session, "cmdSetBypassStart", params);
        
        // TODO 바이패스 KMP 명령을 처리할 수 있도록 해야 한다.
        byte[] kmp = new byte[]{(byte)0x80, 0x3F, 0x10, 0x01, 0x03, (byte)0xE9, 0x7C, (byte)0xD4, 0x0D};
        Thread.sleep(5000);
        session.write(kmp);
    }
    
    public void cmdOndemandMetering(IoSession session) throws Exception
    {
        BypassDevice bd = (BypassDevice)session.getAttribute(session.getRemoteAddress());
        int offset = 0;
        int count = 1;
        
        if (bd.getArgs().size() > 1) {
            offset = (Integer)bd.getArgs().get(0);
            count = (Integer)bd.getArgs().get(1);
        }
        String ns = (String)session.getAttribute("nameSpace");
        List<SMIValue> params = new ArrayList<SMIValue>();
        params.add(DataUtil.getSMIValueByObject(ns, "cmdOndemandOffset", Integer.toString(offset)));
        params.add(DataUtil.getSMIValueByObject(ns, "cmdOndemandCount", Integer.toString(count)));
        sendCommand(session, "cmdOndemandMetering", params);
    }
    
    public void cmdRelayStatus(IoSession session) throws Exception
    {
        BypassDevice bd = (BypassDevice)session.getAttribute(session.getRemoteAddress());
        
        String ns = (String)session.getAttribute("nameSpace");
        sendCommand(session, "cmdRelayStatus", null);
    }
    
    public void cmdRelayDisconnect(IoSession session) throws Exception
    {
        BypassDevice bd = (BypassDevice)session.getAttribute(session.getRemoteAddress());
        int timeout = 5;
        
        if (bd.getArgs().size() > 0) {
            timeout = (Integer)bd.getArgs().get(0);
        }
        String ns = (String)session.getAttribute("nameSpace");
        List<SMIValue> params = new ArrayList<SMIValue>();
        params.add(DataUtil.getSMIValueByObject(ns, "cmdRelayDisconnectTimeout", Integer.toString(timeout)));
        sendCommand(session, "cmdRelayDisconnect", params);
    }
    
    public void cmdRelayReconnect(IoSession session) throws Exception
    {
        BypassDevice bd = (BypassDevice)session.getAttribute(session.getRemoteAddress());
        int timeout = 5;
        
        if (bd.getArgs().size() > 0) {
            timeout = (Integer)bd.getArgs().get(0);
        }
        String ns = (String)session.getAttribute("nameSpace");
        List<SMIValue> params = new ArrayList<SMIValue>();
        params.add(DataUtil.getSMIValueByObject(ns, "cmdRelayReconnectTimeout", Integer.toString(timeout)));
        sendCommand(session, "cmdRelayReconnect", params);
    }

	@Override
	public CommandData execute(HashMap<String, String> params, IoSession session, Client client) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
