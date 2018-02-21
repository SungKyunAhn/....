package com.aimir.fep.trap.actions.SP;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.fep.util.Util;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.MCU;
import com.aimir.model.device.Modem;
import com.aimir.model.device.SubGiga;
import com.aimir.notification.FMPTrap;
import com.aimir.util.IPUtil;

/**
 * Event ID : 240.1.0 evtInstallModem
 *
 * @author Elevas Park
 * @version $Rev: 1 $, $Date: 2016-05-24 15:59:15 +0900 $,
 */
@Service
public class EV_SP_240_1_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_SP_240_1_0_Action.class);
    
    @Autowired
    MCUDao mcuDao;
    
    @Autowired
    LocationDao locationDao;
    
    @Autowired
    SupplierDao supplierDao;
    
    @Autowired
    CodeDao codeDao;
    
    @Autowired
    ModemDao modemDao;
    
    /**
     * execute event action
     *
     * @param trap - FMP Trap(MCU Event)
     * @param event - Event Data
     */
    public void execute(FMPTrap trap, EventAlertLog event) throws Exception
    {
        log.debug("EventCode[" + trap.getCode()
                +"] MCU["+trap.getMcuId()+"]");

        // Initialize
        String mcuId = trap.getMcuId();
        String ipAddr = IPUtil.format(trap.getIpAddr());
        MCU mcu = mcuDao.get(mcuId);

        log.debug("IP[" + ipAddr + "]");
        String modemId = event.getEventAttrValue("moSPId");
        log.debug("MODEM_ID[" + modemId + "]");
        int resetTime = Integer.parseInt(event.getEventAttrValue("moSPResetTime"));
        log.debug("MODEM_RESET_TIME[" + resetTime + "]");
        String nodeKind = event.getEventAttrValue("moNodeKind");
        log.debug("NODE_KIND[" + nodeKind + "]");
        String fwVer =  Hex.decode(DataUtil.get2ByteToInt(Integer.parseInt(event.getEventAttrValue("moSPFwVer"))));
        fwVer = Double.parseDouble(fwVer.substring(0, 2) + "." + fwVer.substring(2, 4)) + "";
        log.debug("FW_VER[" + fwVer + "]");
        String fwBuild = event.getEventAttrValue("moSPFwBuild");
        log.debug("FW_BUILD[" + fwBuild + "]");
        String hwVer = Hex.decode(DataUtil.get2ByteToInt(Integer.parseInt(event.getEventAttrValue("moSPHwVer"))));
        hwVer = Double.parseDouble(hwVer.substring(0, 2) + "." + hwVer.substring(2, 4)) + "";
        log.debug("HW_VER[" + hwVer + "]");
        boolean status = Boolean.parseBoolean(event.getEventAttrValue("moSPStatus"));
        log.debug("STATUS[" + status + "]");
        boolean mode = Boolean.parseBoolean(event.getEventAttrValue("moSPMode"));
        log.debug("MODE[" + mode + "]");
        String lastOnLine = event.getEventAttrValue("moSPLastOnLine");
        log.debug("LAST_ON_LINE[" + lastOnLine + "]");
        String lastOffLine = event.getEventAttrValue("moSPLastOffLine");
        log.debug("LAST_OFF_LINE[" + lastOffLine + "]");
        String installDate = event.getEventAttrValue("moSPInstall");
        log.debug("INSTALL_DATE[" + installDate + "]");
        
        // if not mcu, it's is created and installed.
        Modem modem = modemDao.get(modemId);
        MCU oldMcu = null;
        if (modem == null) {
            /*
            try {
                EventUtil.sendEvent("Equipment Registration",
                                    TargetClass.MCU,
                                    mcuId,
                                    trap.getTimeStamp(),
                                    new String[][] {},
                                    event                
                );                
            }catch(Exception e) {
                log.error("can't send event["+e.getMessage()+"]",e);
            }
            */
            modem = new SubGiga();

            modem.setDeviceSerial(modemId);
            modem.setModemType(ModemType.SubGiga.name());
            modem.setSupplier(mcu.getSupplier());
            modem.setLocation(mcu.getLocation());
            modem.setInstallDate(installDate);
            modem.setLastLinkTime(trap.getTimeStamp());
            modem.setMcu(mcu);
            modem.setFwVer(fwVer);
            modem.setFwRevision(fwBuild);
            modem.setHwVer(hwVer);
            modem.setProtocolType(Protocol.IP.name());
            modem.setNameSpace("SP");
            modem.setProtocolVersion("0102");
            
            if (mcu.getMacAddr() != null && !"".equals(mcu.getMacAddr())) {
                ((SubGiga)modem).setIpv6Address(Util.getIPv6(mcu.getIpv6Addr(), modemId));
            }
            modemDao.add(modem);
        }else{
            modem.setSupplier(mcu.getSupplier());
            modem.setLocation(mcu.getLocation());
            if (modem.getInstallDate() == null)
                modem.setInstallDate(installDate);
            modem.setLastLinkTime(trap.getTimeStamp());
            modem.setFwVer(fwVer);
            modem.setFwRevision(fwBuild);
            modem.setHwVer(hwVer);
            modem.setNameSpace("SP");
            modem.setProtocolVersion("0102");
            
            if (mcu.getMacAddr() != null && !"".equals(mcu.getMacAddr())) {
                ((SubGiga)modem).setIpv6Address(Util.getIPv6(mcu.getIpv6Addr(), modemId));
            }
            
            if (modem.getMcu() == null || (modem.getMcu() != null && !modem.getMcu().getSysID().equals(mcu.getSysID()))) {
                oldMcu = modem.getMcu();
                modem.setMcu(mcu);
            }
            
            modemDao.update(modem);
        }
        
        event.setActivatorIp(modem.getDeviceSerial());
        // event.setActivatorType(TargetClass.Modem);
        event.setSupplier(modem.getSupplier());
        event.setLocation(modem.getLocation());
        
        // oldMcu가 null이 아니면 삭제 명령을 보낸다.
    
        log.debug("Modem Install Event Action Compelte");
    }
}
