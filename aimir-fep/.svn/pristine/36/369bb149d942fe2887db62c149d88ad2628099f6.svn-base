package com.aimir.fep.trap.actions;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.McuType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.command.conf.DefaultConf;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.FMPProperty;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.MCU;
import com.aimir.model.system.Code;
import com.aimir.model.system.Supplier;
import com.aimir.notification.FMPTrap;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeUtil;

/**
 * Event ID : 200.1.0 Processing Class
 *
 * @author Y.S Kim
 * @version $Rev: 1 $, $Date: 2005-12-13 15:59:15 +0900 $,
 */
@Service
public class EV_200_1_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_200_1_0_Action.class);
    
    @Resource(name="transactionManager")
    JpaTransactionManager txmanager;

    @Autowired
    MCUDao mcuDao;
    
    @Autowired
    LocationDao locationDao;
    
    @Autowired
    SupplierDao supplierDao;
    
    @Autowired
    CodeDao codeDao;
    
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

        MCU mcu = null;
        TransactionStatus txstatus = null;
        try {
            // Initialize
            String mcuId = trap.getMcuId();
            String ipAddr = event.getEventAttrValue("ethIpAddr");
    
            if(ipAddr == null || ipAddr.startsWith("/") || ipAddr.startsWith("\n")
                    || ipAddr.equals("127.0.0.1") || ipAddr.equals("localhost")){
                ipAddr = trap.getIpAddr();
            }
            
            String sysPhoneNumber = event.getEventAttrValue("sysPhoneNumber");
            String listenPort = event.getEventAttrValue("sysLocalPort");
            String mobileType = event.getEventAttrValue("sysMobileType");
            String mobileMode = event.getEventAttrValue("sysMobileMode");
            String sysEtherType = event.getEventAttrValue("sysEtherType");
            
            String sysHwVersion = "2.0";
            String sysSwVersion = "3.1";
            String sysSwRevision = FMPProperty.getProperty("mcu.revision.install");
            
            log.debug("sysEtherType[" + sysEtherType + "]");
            int etherType = -1;
            int mcuType = Integer.parseInt(event.getEventAttrValue("sysType"));
            log.debug("MCUTYPE["+mcuType+"]");
            if(sysEtherType != null && sysEtherType.length() > 0){
                etherType = Integer.parseInt(sysEtherType);
            }
            String protocolType = "";
    
            if(etherType >= 0 && etherType < 2){
                protocolType = Protocol.LAN.name();
            }else{
                protocolType = CommonConstants.getProtocolType(mobileType, mobileMode);
            }
    
            // make Target
            Hashtable<String, String> target = new Hashtable<String, String>();
            target.put("id",mcuId);
            target.put("protocolType",protocolType);
            target.put("listenPort",listenPort);
            target.put("ipAddr",ipAddr);
            target.put("sysPhoneNumber",sysPhoneNumber);
    
            if (event.getActivatorType() != TargetClass.DCU) {
                log.warn("Activator Type is not MCU");
                return;
            }
            
            mcu = new MCU();
    
            // Log
            StringBuffer logBuf = new StringBuffer();
            logBuf.append("ipAddr[" + ipAddr +
                          "] sysPhoneNumber["+ sysPhoneNumber +
                          "] sysType[" + mcuType +
                          "] mobileType[" + mobileType +
                          "] mobileMode[" + mobileMode +
                          "] protocolType[" + protocolType +
                          "] listenPort[" + listenPort + "]");
            log.debug(logBuf.toString());
    
            mcu.setSysID(mcuId);
            mcu.setIpAddr(ipAddr);
            //TODO code format error
            McuType mcuTypeEnum = CommonConstants.getMCUType(mcuType);
     
            mcu.setMcuType(CommonConstants.getMcuTypeByName(mcuTypeEnum.name()));
            mcu.setNetworkStatus(1);
            mcu.setInstallDate(TimeUtil.getCurrentTime());
            mcu.setLastCommDate(TimeUtil.getCurrentTime());
            mcu.setSysPhoneNumber(sysPhoneNumber);
            mcu.setSysLocalPort(new Integer(listenPort));
            mcu.setProtocolType(CommonConstants.getProtocolByName(protocolType+""));
            mcu.setSysHwVersion(sysHwVersion);
            mcu.setSysSwVersion(sysSwVersion);
            mcu.setSysSwRevision(sysSwRevision);
            
            
            
            // if not mcu, it's is created and installed.
            MCU existMcu = mcuDao.get(mcu.getSysID());        
            if (existMcu == null) {
                log.info("mcu["+mcu.getSysID()+"] is not existed!!");
                //Set Default Location            
                String defaultLocName = FMPProperty.getProperty("loc.default.name");            
                if(defaultLocName != null && !"".equals(defaultLocName)){               
                    if(locationDao.getLocationByName(StringUtil.toDB(defaultLocName))!=null && locationDao.getLocationByName(StringUtil.toDB(defaultLocName)).size()>0) {
                        log.info("MCU["+mcu.getSysID()+"] Set Default Location["+locationDao.getLocationByName(StringUtil.toDB(defaultLocName)).get(0).getId()+"]");
                        mcu.setLocation(locationDao.getLocationByName(StringUtil.toDB(defaultLocName)).get(0));
                    }else {
                        log.info("MCU["+mcu.getSysID()+"] Default Location["+defaultLocName+"] is Not Exist In DB, Set First Location["+locationDao.getAll().get(0).getId()+"]");
                        mcu.setLocation(locationDao.getAll().get(0));   
                    }
                }else{
                    log.info("MCU["+mcu.getSysID()+"] Default Location is Not Exist In Properties, Set First Location["+locationDao.getAll().get(0).getId()+"]");
                    mcu.setLocation(locationDao.getAll().get(0));  
                }                       
                
                //Set Default Supplier
                String supplierName = new String(FMPProperty.getProperty("default.supplier.name").getBytes("8859_1"), "UTF-8");
                log.debug("Supplier Name[" + supplierName + "]");
                Supplier supplier = supplierName !=null ? supplierDao.getSupplierByName(supplierName):null;
                
                if(supplier !=null && supplier.getId() != null && mcu.getSupplier()==null) {
                    mcu.setSupplier(supplier);
                }else {
                    log.info("MCU["+mcu.getSysID()+"] Default Supplier is Not Exist In Properties, Set First Supplier["+supplierDao.getAll().get(0).getId()+"]");
                    mcu.setSupplier(supplierDao.getAll().get(0));
                }
    
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
                Code status = codeDao.findByCondition("code", CommonConstants.McuStatus.Normal.getCode());
                if (status != null)
                    mcu.setMcuStatus(status);
                
                mcuDao.add(mcu);
            }else{          
                log.info("mcu["+existMcu.getSysID()+"] is existed!! - Location Id:"+existMcu.getLocation().getId()+" Location Name:"+existMcu.getLocation().getName());         
                existMcu.setIpAddr(mcu.getIpAddr());
                existMcu.setSysLocalPort(mcu.getSysLocalPort());
                existMcu.setSysPhoneNumber(mcu.getSysPhoneNumber());
                existMcu.setProtocolType(CommonConstants.getProtocolByName(protocolType+""));
                existMcu.setSysHwVersion(sysHwVersion);
                existMcu.setSysSwVersion(sysSwVersion);
                // existMcu.setSysSwRevision(sysSwRevision);
                existMcu.setLastCommDate(TimeUtil.getCurrentTime());
                
                Code status = codeDao.findByCondition("code", CommonConstants.McuStatus.Normal.getCode());
                if (status != null)
                    existMcu.setMcuStatus(status);
                
                if (existMcu.getInstallDate() == null || "".equals(existMcu.getInstallDate()))
                    existMcu.setInstallDate(TimeUtil.getCurrentTime());
                // 업데이트를 호출하지 않더라도 갱신이 된다.
            }
            
            event.setActivatorIp(mcu.getIpAddr());
            event.setSupplier(mcu.getSupplier());
            event.setLocation(mcu.getLocation());
        }
        finally{
            if (txstatus != null) txmanager.commit(txstatus);
        }
    
        String hwVersion = mcu.getSysHwVersion();
        String swVersion = mcu.getSysSwVersion();

        //CmdOperationUtil.updateMO(mcuMO, eventOid, true);

        // Get MCU Infomation
        try {
            log.debug("hwVersion["+hwVersion+"], swVersion["+swVersion+"], mcuType: "+mcu.getMcuType());
            DefaultConf defaultConf = DefaultConf.getInstance();
            Hashtable props = defaultConf.getDefaultProperties("MCU");

            log.debug("props size=" + props.size());
            List<String> property = new ArrayList<String>();
            Iterator it = props.keySet().iterator();
            for (Iterator i = props.keySet().iterator(); it.hasNext(); ) {
                try {
                    // property[i] = (mibUtil.getOid(key)).getValue();
                    property.add((String)it.next());
                } catch (Exception e) {
                }
            }
            CommandGW gw = new CommandGW();
            gw.cmdMcuScanning(mcu.getSysID(), property.toArray(new String[0]));
        }
        catch (Exception e) {
            log.warn("Can't scan mcu information", e);
        }

        log.debug("MCU Install Event Action Compelte");

    }
}
