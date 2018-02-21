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
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.McuType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.command.conf.DefaultConf;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.EventUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.MCU;
import com.aimir.model.system.Code;
import com.aimir.model.system.Location;
import com.aimir.notification.FMPTrap;
import com.aimir.util.TimeUtil;

/**
 * Event ID : 200.3.0 Processing Class
 *
 * @author Y.S Kim
 * @version $Rev: 1 $, $Date: 2005-12-13 15:59:15 +0900 $,
 */
@Component
public class EV_200_3_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_200_3_0_Action.class);
    
    @Resource(name="transactionManager")
    JpaTransactionManager txmanager;

    @Autowired
    MCUDao mcuDao;

    @Autowired
    LocationDao locationDao;

    @Autowired
	SupplierDao supplierDao;

    /**
     * execute event action
     *
     * @param trap - FMP Trap(MCU Event)
     * @param event - Event Alert Log Data
     */
    public void execute(FMPTrap trap, EventAlertLog event) throws Exception
    {
        log.debug("EventCode[" + trap.getCode() +"] MCU["+trap.getMcuId()+"]");

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
            String hwVersion = event.getEventAttrValue("sysHwVersion");
            String swVersion = event.getEventAttrValue("sysSwVersion");
    
            log.debug("sysEtherType[" + sysEtherType + "]");
            int etherType = -1;
            int mcuType = Integer.parseInt(event.getEventAttrValue("sysType"));
    
            MCU mcu = mcuDao.get(mcuId);
            Code mcuTypeCode = null;
            if(mcu != null){
                mcuTypeCode = mcu.getMcuType();
                event.append(EventUtil.makeEventAlertAttr("equipType",
                        "java.lang.String",mcuTypeCode.getName()));
            }else{
                event.append(EventUtil.makeEventAlertAttr("equipType",
                        "java.lang.String",CommonConstants.getMCUType(mcuType).name()));
            }
    
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
    
    
            String eventOid = trap.getCode();
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
    
            // if not mcu, it's is created and installed.
            if (mcu == null) {
    
                // make dummy mcu mo
                mcu = new MCU();
    
                McuType mcuTypeEnum = CommonConstants.getMCUType(mcuType);
                mcu.setSysID(mcuId);
                mcu.setMcuType(CommonConstants.getMcuTypeByName(mcuTypeEnum.name()));
                mcu.setIpAddr(ipAddr);
                mcu.setNetworkStatus(1);
                mcu.setInstallDate(TimeUtil.getCurrentTime());
                mcu.setLastCommDate(TimeUtil.getCurrentTime());
                mcu.setSysPhoneNumber(sysPhoneNumber);
                if(CommonConstants.getProtocolByName(protocolType+"")!=null) {
                	mcu.setProtocolType(CommonConstants.getProtocolByName(protocolType+""));
                }
                mcu.setSysHwVersion(hwVersion);
                mcu.setSysSwVersion(swVersion);
    
                List<Location> locations = null;
            	String defaultLocName = FMPProperty.getProperty("loc.default.id");
            	if(defaultLocName != null && !"".equals(defaultLocName)){
            		locations = locationDao.getLocationByName(defaultLocName);
    
            	}else{
            		locations = locationDao.getParents();
            	}
                if(locations != null && locations.size() > 0){
                    mcu.setLocation(locations.get(0));
                }
                //Set Default Supplier
                Integer defaultSupplierId = FMPProperty.getProperty("supplier.default.id")!=null ? Integer.parseInt(FMPProperty.getProperty("supplier.default.id")):null;
                if(defaultSupplierId!=null && mcu.getSupplier()==null) {
                	if(supplierDao.get(defaultSupplierId)!=null) {
    	            	log.info("MCU["+mcu.getSysID()+"] Set Default Supplier["+supplierDao.get(defaultSupplierId).getId()+"]");
    	            	mcu.setSupplier(supplierDao.get(defaultSupplierId));
                	}else {
                		log.info("MCU["+mcu.getSysID()+"] Default Supplier["+defaultSupplierId+"] is not Exist In DB, Set First Supplier["+supplierDao.getAll().get(0).getId()+"]");
                    	mcu.setSupplier(supplierDao.getAll().get(0));
                	}
                }else {
                	log.info("MCU["+mcu.getSysID()+"] Default Supplier is Not Exist In Properties, Set First Supplier["+supplierDao.getAll().get(0).getId()+"]");
                	mcu.setSupplier(supplierDao.getAll().get(0));
                }
                mcuDao.add(mcu);
    
                try {
                    EventUtil.sendEvent("Equipment Registration",
                                        TargetClass.DCU,
                                        mcuId,
                                        trap.getTimeStamp(),
                                        new String[][] {},
                                        event
                    );
                }catch(Exception e) {
                    log.error("can't send event["+e.getMessage()+"]");
                }
    
    
            }else{
                mcu.setIpAddr(ipAddr);
                mcu.setNetworkStatus(1);
                if (mcu.getInstallDate() == null || "".equals(mcu.getInstallDate()))
                    mcu.setInstallDate(TimeUtil.getCurrentTime());
                mcu.setLastCommDate(TimeUtil.getCurrentTime());
                mcu.setSysPhoneNumber(sysPhoneNumber);
                if(CommonConstants.getProtocolByName(protocolType+"")!=null) {
                	mcu.setProtocolType(CommonConstants.getProtocolByName(protocolType+""));
                }
                mcu.setSysHwVersion(hwVersion);
                mcu.setSysSwVersion(swVersion);
            	mcuDao.update(mcu);
            }
        }
        finally {
            if (txstatus != null) txmanager.commit(txstatus);
        }

        // Get MCU Information
        try {
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
            /*
            CommandGW gw = new CommandGW();
            gw.cmdMcuScanning(mcu.getSysID(), property.toArray(new String[0]));
            */
        }
        catch (Exception e) {
            log.warn("Can't scan mcu information", e);
        }

        // send mcu info to update server
        /*
        FirmwareUtil.sendMcuInfo(mcuId, mcuType, ipAddr,
                                 Integer.parseInt(listenPort),
                                 swVersion, hwVersion, sysName, name,
                                 installDate, locId,
                                 sysPhoneNumber,
                                 AIMIRProperty.getProperty("firmware.action.update"));
        */
       log.debug("MCU Startup Event Action Compelte");
    }
}
