package com.aimir.fep.trap.actions.SG;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.system.DeviceModelDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.EventUtil;
import com.aimir.fep.util.Hex;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.Modem;
import com.aimir.model.device.SubGiga;
import com.aimir.model.system.Supplier;
import com.aimir.notification.FMPTrap;
import com.aimir.util.DateTimeUtil;

/**
 * Event ID : SG_202.1.0 (Subgiga network Information)
 * <br>Subgiga Modem
 *
 * @author elevas
 * @version $Rev: 1 $, $Date: 2014-08-28 10:00:00 +0900 $,
 */
@Service
public class EV_SG_202_1_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_SG_202_1_0_Action.class);
    
    @Resource(name="transactionManager")
    JpaTransactionManager txmanager;
    
    @Autowired
    ModemDao modemDao;
    
    @Autowired
    DeviceModelDao deviceModelDao;
    
    @Autowired
    SupplierDao supplierDao;
    
    @Autowired
    LocationDao locationDao;
    
    /**
     * execute event action
     *
     * @param trap - FMP Trap(GPRS Modem Event)
     * @param event - Event Alert Log Data
     */
    public void execute(FMPTrap trap, EventAlertLog event) throws Exception
    {
        log.debug("EventName[eventNetworkInformation] "+" EventCode[" + trap.getCode()+"] Modem["+trap.getSourceId()+"]");
        
        TransactionStatus txstatus = null;
        
        try {
            txstatus = txmanager.getTransaction(null);
            
            String nameSpace = "";
            if(trap.getCode().indexOf("_") > 0){
                nameSpace = trap.getCode().substring(0,2);
            }
            
            byte[] src = Hex.encode(event.getEventAttrValue("evtNetworkTable.hex"));
            byte[] bx = new byte[2];
            int pos = 0;
            System.arraycopy(src, pos, bx, 0, bx.length);
            pos += bx.length;
            
            DataUtil.convertEndian(bx);
            int count = DataUtil.getIntTo2Byte(bx);
            String newModemId = "";
            for (int i = 0; i < count; i++) {
                if (i != 0)
                    newModemId += ",";
                newModemId += validateModem(src, pos, nameSpace, trap.getProtocolVersion());
                pos += 26;
            }
                
            event.setActivatorId(trap.getSourceId());
            event.setActivatorType(TargetClass.SubGiga);
            event.append(EventUtil.makeEventAlertAttr("modemID",
                                     "java.lang.String", newModemId));
            event.append(EventUtil.makeEventAlertAttr("message",
                                    "java.lang.String",
                                    "Network Information "));
        }
        finally {
            if (txstatus != null) txmanager.commit(txstatus);
        }
    }

    private String validateModem(byte[] src, int pos, String nameSpace, String protocolVer)
    {
        // index
        byte[] bx = new byte[2];
        System.arraycopy(src, pos, bx, 0, bx.length);
        pos += bx.length;
        DataUtil.convertEndian(bx);
        int idx = DataUtil.getIntTo2Byte(bx);
        log.debug("IDX[" + idx + "]");
        
        // EUI
        bx = new byte[8];
        System.arraycopy(src, pos, bx, 0, bx.length);
        pos += bx.length;
        String newModemId = Hex.decode(bx);
        log.debug("EUI[" + newModemId + "]");
        
        // last commtime
        bx = new byte[7];
        System.arraycopy(src, pos, bx, 0, bx.length);
        pos += bx.length;
        String lastCommDate =  DataUtil.getTimeStamp(bx);
        log.debug("LAST_COMM_DATE[" + lastCommDate + "]");
        
        // parent eui
        bx = new byte[8];
        System.arraycopy(src, pos, bx, 0, bx.length);
        pos += bx.length;
        String peui = Hex.decode(bx);
        log.debug("PEUI[" + peui + "]");
        
        // rssi
        bx = new byte[1];
        System.arraycopy(src, pos, bx, 0, bx.length);
        pos += bx.length;
        int rssi = DataUtil.getIntToBytes(bx);
        log.debug("RSSI[" + rssi + "]");
        
        ModemType modemType = ModemType.SubGiga;
        
        try {
            // get modem
            Modem modem = modemDao.get(newModemId);
            Supplier supplier = supplierDao.getAll().get(0);
            Modem p = modemDao.get(peui);
            
            if (p == null) {
                p = new SubGiga();                
                p.setDeviceSerial(peui);
                p.setInstallDate(DateTimeUtil.getDST(supplier.getTimezone().getName(), lastCommDate));
                p.setSupplier(supplier);
                p.setModemType(ModemType.SubGiga.name());
                p.setLocation(locationDao.getAll().get(0));   
                p.setLastLinkTime(DateTimeUtil.getDST(supplier.getTimezone().getName(), lastCommDate));
                p.setProtocolVersion(protocolVer);
                p.setProtocolType(Protocol.SMS.name());
                p.setNameSpace(nameSpace);
                modemDao.add(p);
            }
            
            if (modem == null) {
                SubGiga subgiga = new SubGiga();                
                subgiga.setDeviceSerial(newModemId);
                subgiga.setInstallDate(DateTimeUtil.getDST(supplier.getTimezone().getName(), lastCommDate));
                subgiga.setSupplier(supplier);
                subgiga.setModemType(modemType.name());
                subgiga.setLocation(locationDao.getAll().get(0));   
                subgiga.setLastLinkTime(DateTimeUtil.getDST(supplier.getTimezone().getName(), lastCommDate));
                subgiga.setProtocolVersion(protocolVer);
                subgiga.setProtocolType(Protocol.SMS.name());
                subgiga.setNameSpace(nameSpace);
                subgiga.setRssi(rssi);
                subgiga.setModem(p);
                modemDao.add(subgiga);
            }else{
                if (modem instanceof SubGiga) {
                    SubGiga subgiga = (SubGiga)modem;
                    subgiga.setLastLinkTime(DateTimeUtil.getDST(supplier.getTimezone().getName(), lastCommDate));
                    subgiga.setModem(p);
                    subgiga.setRssi(rssi);
                    modemDao.update(subgiga);
                }
            }
        }
        catch (Exception e) {
            log.error(e, e);
        }
        
        return newModemId;
    }
}
