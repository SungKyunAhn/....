package com.aimir.fep.bypass.actions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.session.IoSession;

import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.constants.CommonConstants.TR_STATE;
import com.aimir.dao.device.AsyncCommandLogDao;
import com.aimir.dao.device.AsyncCommandParamDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.system.DeviceModelDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.bypass.BypassDevice;
import com.aimir.fep.protocol.fmp.client.Client;
import com.aimir.fep.protocol.fmp.datatype.SMIValue;
import com.aimir.fep.protocol.fmp.datatype.STREAM;
import com.aimir.fep.protocol.fmp.frame.service.CommandData;
import com.aimir.fep.util.CRCUtil;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.model.device.AsyncCommandLog;
import com.aimir.model.device.AsyncCommandParam;
import com.aimir.model.device.Modem;
import com.aimir.model.device.SubGiga;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.Supplier;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;
import com.aimir.util.DateTimeUtil;

public class CommandAction_SG extends CommandAction {
    private static Log log = LogFactory.getLog(CommandAction_SG.class);
    
    @Override
    public void executeBypass(byte[] frame, IoSession session) throws Exception
    {
        
    }
    
    @Override
    public void execute(String cmd, SMIValue[] smiValues, IoSession session) 
    throws Exception
    {
        try {
            BypassDevice bd = (BypassDevice)session.getAttribute(session.getRemoteAddress());
            String nameSpace = (String)session.getAttribute("nameSpace");
            
            // GG 101.2.1 모뎀/미터 식별 응답이 오면 커맨드를 실행한다.
            if (cmd.equals("cmdIdentifyDevice")) {
                bd.setModemId(smiValues[0].getVariable().toString());
                
                // 비동기 내역을 조회한다.
                AsyncCommandLogDao acld = DataUtil.getBean(AsyncCommandLogDao.class);
                Set<Condition> condition = new HashSet<Condition>();
                condition.add(new Condition("deviceId", new Object[]{bd.getModemId()}, null, Restriction.EQ));
                condition.add(new Condition("state", new Object[]{TR_STATE.Waiting.getCode()}, null, Restriction.EQ));
                List<AsyncCommandLog> acllist = acld.findByConditions(condition);
                
                log.debug("ASYNC_SIZE[" + acllist.size () + "]");
                if (acllist.size() > 0) {
                    // 명령 건수가 1개여야 한다. 어차피 여러개의 명령 요청이 있더라도 한번 하고 나면 끊어지기 때문에 연속으로 처리할 수 없다.
                    // 전부 처리된 것으로 변경한다.
                    AsyncCommandLog acl = null;
                    for (int i = 0; i < acllist.size(); i++) {
                        acl = acllist.get(i);
                        if (i == 0)
                            acl.setState(TR_STATE.Success.getCode());
                        else
                            acl.setState(TR_STATE.Terminate.getCode());
                        acld.update(acl);
                    }
                    
                    acl = acllist.get(0);
                    
                    condition = new HashSet<Condition>();
                    condition.add(new Condition("id.trId", new Object[]{acl.getTrId()}, null, Restriction.EQ));
                    condition.add(new Condition("id.mcuId", new Object[]{acl.getMcuId()}, null, Restriction.EQ));
                    AsyncCommandParamDao acpd = DataUtil.getBean(AsyncCommandParamDao.class);
                    List<AsyncCommandParam> acplist = acpd.findByConditions(condition);
                    
                    for (AsyncCommandParam p : acplist.toArray(new AsyncCommandParam[0])) {
                        bd.addArg(p.getParamValue());
                    }
                    Method method = this.getClass().getMethod(acl.getCommand(), IoSession.class);
                    method.invoke(this, session);
                }
                else {
                    // 실행할 명령이 없으면 EOT 호출하고 종료
                    // session.write(new ControlDataFrame(ControlDataConstants.CODE_EOT));
                    // cmdFactorySetting(session);
                    // cmdMeterTimeSync(session);
                    
                    //자계기인 경우 추가 -> 모계기 테스트시 아래 4줄 주석 처리.
                    bd.addArg("/home/aimir3/자계기_OTA_이미지.bin");
                    bd.addArg((byte)0x02);
                    bd.addArg("2483670351C8DF70"); 
                    bd.addArg("2483670051C8DF61");   
                    
                    cmdOTAStart(session);
                    // cmdReadModemConfiguration(session);
                    // cmdReadNMSInformation(session);
                    // cmdSetApn(session);
                    // cmdSetBypassStart(session);
                    // cmdSetMeteringInterval(session);
                    // cmdSetModemResetInterval(session);
                    // cmdSetServerIpPort(session);
                    // cmdSetTime(session);
                    // cmdUploadMeteringData(session);
                    // cmdResetModem(session);
                }
            }
            else if (cmd.equals("cmdOTAStart")) {
                log.debug("modemId[" + bd.getModemId() + "]");
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
                
                if (status == 0x01 && bd.getRetry() < 3) {
                    bd.setRetry(bd.getRetry()+1);
                    bd.setOffset(0);
                    cmdOTAStart(session);
                }
            }
            else if (cmd.equals("cmdReadModemConfiguration")) {
                // 모뎀 설정 정보를 갱신한다.
                cmdReadModemConfigurationResponse(nameSpace, smiValues);
            }
            else if (cmd.equals("cmdReadNMSInformation")) {
                // 모뎀 설정 정보를 갱신한다.
                cmdReadNMSInformationResponse(nameSpace, smiValues);
            }
            else if (cmd.equals("cmdOBISListUp")) {
                cmdOBISListUpResponse(smiValues);
            }
            else if (cmd.equals("cmdOBISAdd")) {
                int result = Integer.parseInt(smiValues[0].getVariable().toString());
            }
            else if (cmd.equals("cmdOBISRemove")) {
                int result = Integer.parseInt(smiValues[0].getVariable().toString());
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
        // ota 비동기 이력의 인자에서 파일 경로를 가져온다.
        ByteArrayOutputStream out = null;
        FileInputStream in = null;
        try {
            BypassDevice bd = (BypassDevice)session.getAttribute(session.getRemoteAddress());
            
            String filename = "/home/aimir3/모계기_OTA_이미지.bin";
            byte imageType = 0x01;
            
            if (bd.getArgs().size() >= 2) {
                filename = (String)bd.getArgs().get(0);
                imageType = (Byte)bd.getArgs().get(1);
            }
            
            // BypassDevice의 args에서 파일명을 가져와야 한다.
            File file = new File(filename);
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
            
            params.add(DataUtil.getSMIValueByObject(ns, "cmdModemFwImageType", Byte.toString(imageType)));
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
        
        // 전송이 끝나면 종료 명령을 보낸다.
        if (bd.getOffset() == bd.getFw_bin().length) {
            bd.getFw_in().close();
            
            byte imageType = 0x01;
            
            if (bd.getArgs().size() >= 2) {
                imageType = (Byte)bd.getArgs().get(1);
            }
            
            if (imageType == 0x02 || imageType == 0x03) {
                List<SMIValue> params = new ArrayList<SMIValue>();
                params.add(DataUtil.getSMIValueByObject(ns, "cmdUpgradeCount", Integer.toString(bd.getArgs().size()-2)));
                for (int i = 2; i < bd.getArgs().size(); i++) {
                    params.add(DataUtil.getSMIValueByObject(ns, "cmdUpgradeModemID", (String)bd.getArgs().get(i)));
                }
                sendCommand(session, "cmdOTAEnd", params);
            }
            else
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
        String modemId = "2483670051C8DF33";
        
        if (bd.getArgs().size() > 0)  {
            modemId = (String)bd.getArgs().get(0);
        }
        
        String ns = (String)session.getAttribute("nameSpace");
        List<SMIValue> params = new ArrayList<SMIValue>();
        params.add(DataUtil.getSMIValueByObject(ns, "cmdResetModemID", modemId));
        sendCommand(session, "cmdResetModem", params);
    }
    
    public void cmdFactorySetting(IoSession session) throws Exception
    {
        BypassDevice bd = (BypassDevice)session.getAttribute(session.getRemoteAddress());
        int code = 0x0314;
        String modemId = "240101075352869D";
        
        if (bd.getArgs().size() > 0)  {
            modemId = (String)bd.getArgs().get(0);
        }
        
        String ns = (String)session.getAttribute("nameSpace");
        List<SMIValue> params = new ArrayList<SMIValue>();
        params.add(DataUtil.getSMIValueByObject(ns, "cmdFactorySettingModemID", modemId));
        params.add(DataUtil.getSMIValueByObject(ns, "cmdFactorySettingCode", Integer.toString(code)));
        sendCommand(session, "cmdFactorySetting", params);
    }
    
    public void cmdMeterTimeSync(IoSession session) throws Exception
    {
        BypassDevice bd = (BypassDevice)session.getAttribute(session.getRemoteAddress());
        String modemId = "249C5D0653C0B30C";
        
        if (bd.getArgs().size() > 0)  {
            modemId = (String)bd.getArgs().get(0);
        }
        
        String ns = (String)session.getAttribute("nameSpace");
        List<SMIValue> params = new ArrayList<SMIValue>();
        params.add(DataUtil.getSMIValueByObject(ns, "cmdMeterTimeSyncModemID", modemId));
        
        sendCommand(session, "cmdMeterTimeSync", params);
    }
    
    public void cmdReadModemConfiguration(IoSession session) throws Exception
    {
        sendCommand(session, "cmdReadModemConfiguration", null);
    }
    
    private void cmdReadModemConfigurationResponse(String nameSpace, SMIValue[] smiValues) throws Exception
    {
        if (smiValues != null && smiValues.length == 21) {
            Modem parentModem = validateParentModem(nameSpace, smiValues);
            
            byte[] childinfo = smiValues[20].getVariable().toString().getBytes();
            
            byte[] bx = new byte[2];
            int pos = 0;
            System.arraycopy(childinfo, pos, bx, 0, bx.length);
            pos += bx.length;
            
            int count = DataUtil.getIntTo2Byte(bx);
            for (int i = 0; i < count; i++) {
                validateChildModem(childinfo, pos, nameSpace, parentModem);
            }
        }
        else {
            log.warn("Response is null or less than 21");
        }
    }
    
    private Modem validateParentModem(String nameSpace, SMIValue[] smiValues) {
        String pmodemId = smiValues[0].getVariable().toString();
        String installedDate = smiValues[1].getVariable().toString();
        int resetInterval = Integer.parseInt(smiValues[2].getVariable().toString());
        int meteringInterval = Integer.parseInt(smiValues[3].getVariable().toString());     
        int meteringTimeRange = Integer.parseInt(smiValues[4].getVariable().toString());
        String serverIp = smiValues[5].getVariable().toString();
        String serverPort = smiValues[6].getVariable().toString();        
        String apnAddress = smiValues[7].getVariable().toString();
        String apnId = smiValues[8].getVariable().toString();
        String apnPassword = smiValues[9].getVariable().toString();
        String modelName = smiValues[10].getVariable().toString();        
        String fwVer = smiValues[11].getVariable().toString();
        String buildNumber = smiValues[12].getVariable().toString();
        String hwVer = smiValues[13].getVariable().toString();
        String simNumber = smiValues[14].getVariable().toString();     
        String phoneNumber = smiValues[15].getVariable().toString();   
        String strFrequency = smiValues[16].getVariable().toString();
        String strBandwidth = smiValues[17].getVariable().toString();
        int panId = Integer.parseInt(smiValues[18].getVariable().toString());
        long rfPower = Long.parseLong(smiValues[19].getVariable().toString());
        
        if(fwVer != null && !"".equals(fwVer)){
            fwVer = DataUtil.getVersionString(Integer.parseInt(fwVer));
        }
        if(hwVer != null && !"".equals(hwVer)){
            hwVer = DataUtil.getVersionString(Integer.parseInt(hwVer));
        }
        
        ModemType modemType = ModemType.SubGiga;
        
        DeviceModelDao deviceModelDao = DataUtil.getBean(DeviceModelDao.class);
        DeviceModel modemModel = deviceModelDao.findByCondition("name", modelName);

        SubGiga modem = null;
        try {
            // 단위가 같이 오기 때문에 정수만 추출한다.
            StringTokenizer st = new StringTokenizer(strFrequency, " ");
            int frequency = Integer.parseInt(st.nextToken());
            st = new StringTokenizer(strBandwidth, " ");
            int bandwidth = Integer.parseInt(st.nextToken());
            
            ModemDao modemDao = DataUtil.getBean(ModemDao.class);
            SupplierDao supplierDao = DataUtil.getBean(SupplierDao.class);
            LocationDao locationDao = DataUtil.getBean(LocationDao.class);
            
            // get modem
            modem = (SubGiga) modemDao.get(pmodemId);
            Supplier supplier = supplierDao.getAll().get(0);
            
            if (modem == null) {
                modem = new SubGiga();                
                modem.setDeviceSerial(pmodemId);
                modem.setInstallDate(DateTimeUtil.getDST(supplier.getTimezone().getName(), installedDate));
                modem.setSupplier(supplier);
                modem.setModemType(modemType.name());
                modem.setLocation(locationDao.getAll().get(0));   
                if(modemModel != null){
                    modem.setModel(modemModel); 
                }
                if (phoneNumber != null && !"".equals(phoneNumber))
                    modem.setPhoneNumber(phoneNumber);
                modem.setSimNumber(simNumber);
                modem.setFwRevision(buildNumber);
                modem.setFwVer(fwVer);
                modem.setHwVer(hwVer);
                modem.setProtocolVersion("1.2");
                modem.setProtocolType(Protocol.SMS.name());
                modem.setNameSpace(nameSpace);
                modem.setRfPower(rfPower);
                modem.setFrequency(frequency);
                modem.setBandWidth(bandwidth);
                modem.setPanId(panId);
                modem.setResetInterval(resetInterval);
                modem.setMeteringInterval(meteringInterval);
                modem.setMeteringTimeRange(meteringTimeRange);
                modem.setApnAddress(apnAddress);
                modem.setApnId(apnId);
                modem.setApnPassword(apnPassword);
                modem.setIpAddr(serverIp);
                modem.setListenPort(Integer.parseInt(serverPort));
                modemDao.add(modem);
            }else{
                if(modemModel != null){
                    modem.setModel(modemModel); 
                } 
                if (phoneNumber != null && !"".equals(phoneNumber))
                    modem.setPhoneNumber(phoneNumber);
                modem.setSimNumber(simNumber);
                modem.setFwRevision(buildNumber);
                modem.setFwVer(fwVer);
                modem.setHwVer(hwVer);
                modem.setProtocolVersion("1.2");
                modem.setProtocolType(Protocol.SMS.name());
                modem.setNameSpace(nameSpace);
                modem.setRfPower(rfPower);
                modem.setFrequency(frequency);
                modem.setBandWidth(bandwidth);
                modem.setPanId(panId);
                modem.setResetInterval(resetInterval);
                modem.setMeteringInterval(meteringInterval);
                modem.setMeteringTimeRange(meteringTimeRange);
                modem.setApnAddress(apnAddress);
                modem.setApnId(apnId);
                modem.setApnPassword(apnPassword);
                modem.setIpAddr(serverIp);
                modem.setListenPort(Integer.parseInt(serverPort));
                modemDao.update(modem);
            }
        }
        catch (Exception e) {
            log.warn(e, e);
        }
        return modem;
    }
    
    private String validateChildModem(byte[] src, int pos, String nameSpace, Modem p)
    {
        // index
        byte[] bx = new byte[2];
        System.arraycopy(src, pos, bx, 0, bx.length);
        pos += bx.length;
        int idx = DataUtil.getIntTo2Byte(bx);
        
        // EUI
        bx = new byte[8];
        System.arraycopy(src, pos, bx, 0, bx.length);
        pos += bx.length;
        String newModemId = Hex.decode(bx);
        
        // reset interval
        bx = new byte[2];
        System.arraycopy(src, pos, bx, 0, bx.length);
        pos += bx.length;
        int resetInterval = DataUtil.getIntTo2Byte(bx);
        
        // metering interval
        bx = new byte[2];
        System.arraycopy(src, pos, bx, 0, bx.length);
        pos += bx.length;
        int meteringInterval = DataUtil.getIntTo2Byte(bx);
        
        // time range
        bx = new byte[1];
        System.arraycopy(src, pos, bx, 0, bx.length);
        pos += bx.length;
        int meteringTimeRange = DataUtil.getIntToBytes(bx);
        
        // model name
        bx = new byte[20];
        System.arraycopy(src, pos, bx, 0, bx.length);
        pos += bx.length;
        String modelName = new String(bx);
        
        // fw version
        bx = new byte[2];
        System.arraycopy(src, pos, bx, 0, bx.length);
        pos += bx.length;
        String fwVer = (int)bx[0] + "." + (int)bx[1];
        
        // build number
        bx = new byte[2];
        System.arraycopy(src, pos, bx, 0, bx.length);
        pos += bx.length;
        String buildNumber = Integer.toString(DataUtil.getIntTo2Byte(bx));
        
        // hw version
        bx = new byte[2];
        System.arraycopy(src, pos, bx, 0, bx.length);
        pos += bx.length;
        String hwVer = (int)bx[0] + "." + (int)bx[1];
        
        // meter  type
        bx = new byte[1];
        System.arraycopy(src, pos, bx, 0, bx.length);
        pos += bx.length;
        int meterType = DataUtil.getIntToBytes(bx);
        
        if(fwVer != null && !"".equals(fwVer)){
            fwVer = DataUtil.getVersionString(Integer.parseInt(fwVer));
        }
        if(hwVer != null && !"".equals(hwVer)){
            hwVer = DataUtil.getVersionString(Integer.parseInt(hwVer));
        }
        
        ModemType modemType = ModemType.SubGiga;
        
        DeviceModelDao deviceModelDao = DataUtil.getBean(DeviceModelDao.class);
        ModemDao modemDao = DataUtil.getBean(ModemDao.class);
        SupplierDao supplierDao = DataUtil.getBean(SupplierDao.class);
        LocationDao locationDao = DataUtil.getBean(LocationDao.class);
        DeviceModel modemModel = deviceModelDao.findByCondition("name", modelName);

        try {
            // get modem
            SubGiga modem = (SubGiga) modemDao.get(newModemId);
            Supplier supplier = supplierDao.getAll().get(0);
            
            if (modem == null) {
                modem = new SubGiga();                
                modem.setDeviceSerial(newModemId);
                modem.setInstallDate(DateTimeUtil.getDateString(new Date()));
                modem.setSupplier(supplier);
                modem.setModemType(modemType.name());
                modem.setLocation(locationDao.getAll().get(0));   
                if(modemModel != null){
                    modem.setModel(modemModel); 
                }
                modem.setFwRevision(buildNumber);
                modem.setFwVer(fwVer);
                modem.setHwVer(hwVer);
                modem.setProtocolVersion("1.2");
                modem.setProtocolType(Protocol.SMS.name());
                modem.setNameSpace(nameSpace);
                modem.setResetInterval(resetInterval);
                modem.setMeteringInterval(meteringInterval);
                modem.setMeteringTimeRange(meteringTimeRange);
                modem.setModem(p);
                modemDao.add(modem);
            }else{
                if(modemModel != null){
                    modem.setModel(modemModel); 
                } 
                modem.setFwRevision(buildNumber);
                modem.setFwVer(fwVer);
                modem.setHwVer(hwVer);
                modem.setProtocolVersion("1.2");
                modem.setProtocolType(Protocol.SMS.name());
                modem.setNameSpace(nameSpace);
                modem.setResetInterval(resetInterval);
                modem.setMeteringInterval(meteringInterval);
                modem.setMeteringTimeRange(meteringTimeRange);
                modem.setModem(p);
                modemDao.update(modem);
            }
        }
        catch (Exception e) {
            log.error(e, e);
        }
        
        return newModemId;
    }
    
    public void cmdReadNMSInformation(IoSession session) throws Exception
    {
        sendCommand(session, "cmdReadNMSInformation", null);
    }
    
    private void cmdReadNMSInformationResponse(String nameSpace, SMIValue[] smiValues) throws Exception
    {
        if (smiValues != null && smiValues.length == 1) {
            byte[] src = smiValues[0].getVariable().toString().getBytes();
            byte[] bx = new byte[2];
            int pos = 0;
            System.arraycopy(src, pos, bx, 0, bx.length);
            pos += bx.length;
            
            int count = DataUtil.getIntTo2Byte(bx);
            for (int i = 0; i < count; i++) {
                validateModem(src, pos, nameSpace);
            }
        }
        else {
            log.warn("Response is null or less than 1");
        }
    }
    
    private String validateModem(byte[] src, int pos, String nameSpace)
    {
        // index
        byte[] bx = new byte[2];
        System.arraycopy(src, pos, bx, 0, bx.length);
        pos += bx.length;
        int idx = DataUtil.getIntTo2Byte(bx);
        
        // EUI
        bx = new byte[8];
        System.arraycopy(src, pos, bx, 0, bx.length);
        pos += bx.length;
        String newModemId = Hex.decode(bx);
        
        // last commtime
        bx = new byte[7];
        System.arraycopy(src, pos, bx, 0, bx.length);
        pos += bx.length;
        String lastCommDate =  DataUtil.getTimeStamp(bx);
        
        // parent eui
        bx = new byte[8];
        System.arraycopy(src, pos, bx, 0, bx.length);
        pos += bx.length;
        String peui = Hex.decode(bx);
        
        // rssi
        bx = new byte[1];
        System.arraycopy(src, pos, bx, 0, bx.length);
        pos += bx.length;
        int rssi = DataUtil.getIntToBytes(bx);
        
        ModemType modemType = ModemType.SubGiga;
        
        try {
            ModemDao modemDao = DataUtil.getBean(ModemDao.class);
            SupplierDao supplierDao = DataUtil.getBean(SupplierDao.class);
            LocationDao locationDao = DataUtil.getBean(LocationDao.class);
            
            // get modem
            SubGiga modem = (SubGiga) modemDao.get(newModemId);
            Supplier supplier = supplierDao.getAll().get(0);
            Modem p = modemDao.get(peui);
            
            if (modem == null) {
                modem = new SubGiga();                
                modem.setDeviceSerial(newModemId);
                modem.setSupplier(supplier);
                modem.setModemType(modemType.name());
                modem.setLocation(locationDao.getAll().get(0));   
                modem.setLastLinkTime(DateTimeUtil.getDST(supplier.getTimezone().getName(), lastCommDate));
                modem.setProtocolVersion("1.2");
                modem.setProtocolType(Protocol.SMS.name());
                modem.setNameSpace(nameSpace);
                modem.setRssi(rssi);
                modem.setModem(p);
                modemDao.add(modem);
            }else{
                modem.setLastLinkTime(DateTimeUtil.getDST(supplier.getTimezone().getName(), lastCommDate));
                modem.setModem(p);
                modem.setRssi(rssi);
                modemDao.update(modem);
            }
        }
        catch (Exception e) {
            log.error(e, e);
        }
        
        return newModemId;
    }
    
    public void cmdSetTime(IoSession session) throws Exception
    {
        BypassDevice bd = (BypassDevice)session.getAttribute(session.getRemoteAddress());
        String timestamp = DateTimeUtil.getDateString(new Date());
        
        String ns = (String)session.getAttribute("nameSpace");
        List<SMIValue> params = new ArrayList<SMIValue>();
        params.add(DataUtil.getSMIValueByObject(ns, "cmdSetTimeModemID", bd.getModemId()));
        params.add(DataUtil.getSMIValueByObject(ns, "cmdSetTimestamp", timestamp));
        sendCommand(session, "cmdSetTime", params);
    }
    
    public void cmdSetModemResetInterval(IoSession session) throws Exception
    {
        BypassDevice bd = (BypassDevice)session.getAttribute(session.getRemoteAddress());
        int interval = 60;
        String modemId = "240101075352869D";
        
        if (bd.getArgs().size() > 1) {
            modemId = (String)bd.getArgs().get(0);
            interval = (Integer)bd.getArgs().get(1);
        }
        String ns = (String)session.getAttribute("nameSpace");
        List<SMIValue> params = new ArrayList<SMIValue>();
        params.add(DataUtil.getSMIValueByObject(ns, "cmdModemResetModemID", modemId));
        params.add(DataUtil.getSMIValueByObject(ns, "cmdModemResetIntervalMinute", Integer.toString(interval)));
        sendCommand(session, "cmdModemResetInterval", params);
    }
    
    public void cmdSetMeteringInterval(IoSession session) throws Exception
    {
        BypassDevice bd = (BypassDevice)session.getAttribute(session.getRemoteAddress());
        int interval = 15;
        int timerange = 1;
        String modemId = "240101075352869D";
        
        if (bd.getArgs().size() > 2) {
            modemId = (String)bd.getArgs().get(0);
            interval = (Integer)bd.getArgs().get(1);
            timerange = (Integer)bd.getArgs().get(2);
        }
        String ns = (String)session.getAttribute("nameSpace");
        List<SMIValue> params = new ArrayList<SMIValue>();
        params.add(DataUtil.getSMIValueByObject(ns, "cmdSetMeteringIntervalModemID", modemId));
        params.add(DataUtil.getSMIValueByObject(ns, "cmdSetMeteringIntervalMinute", Integer.toString(interval)));
        params.add(DataUtil.getSMIValueByObject(ns, "cmdSetMeteringTimeRange", Integer.toString(timerange)));
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
        String meterId = null;
        int timeout = 10;
        
        if (bd.getArgs().size() > 1) {
            meterId = (String)bd.getArgs().get(0);
            timeout = (Integer)bd.getArgs().get(1);
        }
        
        String ns = (String)session.getAttribute("nameSpace");
        List<SMIValue> params = new ArrayList<SMIValue>();
        params.add(DataUtil.getSMIValueByObject(ns, "cmdSetBypassStartMeterID", meterId));
        params.add(DataUtil.getSMIValueByObject(ns, "cmdSetBypassStartTimeout", Integer.toString(timeout)));
        sendCommand(session, "cmdSetBypassStart", params);
        
        // TODO 바이패스 KMP 명령을 처리할 수 있도록 해야 한다.
        byte[] kmp = new byte[]{(byte)0x80, 0x3F, 0x10, 0x01, 0x03, (byte)0xE9, 0x7C, (byte)0xD4, 0x0D};
        Thread.sleep(5000);
        session.write(kmp);
    }
    
    public void cmdOBISListUp(IoSession session) throws Exception
    {
        BypassDevice bd = (BypassDevice)session.getAttribute(session.getRemoteAddress());
        String modemId = "240101075352869D";
        
        if (bd.getArgs().size() > 0) {
            modemId = (String)bd.getArgs().get(0);
        }
        
        String ns = (String)session.getAttribute("nameSpace");
        List<SMIValue> params = new ArrayList<SMIValue>();
        params.add(DataUtil.getSMIValueByObject(ns, "cmdOBISListUpModemID", modemId));
        sendCommand(session, "cmdOBISListUp", params);
    }
    
    private void cmdOBISListUpResponse(SMIValue[] smiValues) throws Exception
    {
        if (smiValues != null && smiValues.length == 0) {
            int obiscnt = Integer.parseInt(smiValues[0].getVariable().toString());
            int idx = 0;
            byte[] obisCode = null;
            int selAccessLen = 0;
            byte[] selAccessData = null;
            for (int i = 1; i < obiscnt*4+1; ) {
                idx = Integer.parseInt(smiValues[i++].getVariable().toString());
                log.debug("IDX[" + idx + "]");
                obisCode = smiValues[i++].getVariable().toString().getBytes();
                log.debug("OBIS_CODE[" + Hex.decode(obisCode) + "]");
                selAccessLen = Integer.parseInt(smiValues[i++].getVariable().toString());
                log.debug("SEL_ACCESS_LEN[" + selAccessLen + "]");
                selAccessData = smiValues[i++].getVariable().toString().getBytes();
                log.debug("SEL_ACCESS_DATA[" + Hex.decode(selAccessData) + "]");
            }
        }
        else {
            log.warn("Response is null or less than 1");
        }
    }
    
    public void cmdOBISAdd(IoSession session) throws Exception
    {
        BypassDevice bd = (BypassDevice)session.getAttribute(session.getRemoteAddress());
        String modemId = "240101075352869D";
        byte[] obisCode = new byte[]{};
        int len = 0;
        byte[] data = new byte[] {};
        
        if (bd.getArgs().size() == 4) {
            modemId = (String)bd.getArgs().get(0);
            obisCode = (byte[])bd.getArgs().get(1);
            len = (Integer)bd.getArgs().get(2);
            data = (byte[])bd.getArgs().get(3);
        }
        String ns = (String)session.getAttribute("nameSpace");
        List<SMIValue> params = new ArrayList<SMIValue>();
        params.add(DataUtil.getSMIValueByObject(ns, "cmdOBISAddModemID", modemId));
        params.add(DataUtil.getSMIValueByObject(ns, "cmdOBISAddCode", obisCode));
        params.add(DataUtil.getSMIValueByObject(ns, "cmdOBISAddAccessLength", len));
        params.add(DataUtil.getSMIValueByObject(ns, "cmdOBISAddAccessData", data));
        sendCommand(session, "cmdOBISAdd", params);
    }
    
    public void cmdOBISRemove(IoSession session) throws Exception
    {
        BypassDevice bd = (BypassDevice)session.getAttribute(session.getRemoteAddress());
        String modemId = "240101075352869D";
        int idx = 0;
        if (bd.getArgs().size() == 2) {
            modemId = (String)bd.getArgs().get(0);
            idx = (Integer)bd.getArgs().get(1);
        }
        
        String ns = (String)session.getAttribute("nameSpace");
        List<SMIValue> params = new ArrayList<SMIValue>();
        params.add(DataUtil.getSMIValueByObject(ns, "cmdOBISRemoveModemID", modemId));
        params.add(DataUtil.getSMIValueByObject(ns, "cmdOBISRemoveIndex", idx));
        sendCommand(session, "cmdOBISRemove", params);
    }

	@Override
	public CommandData execute(HashMap<String, String> params, IoSession session, Client client) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
