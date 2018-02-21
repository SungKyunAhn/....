package com.aimir.fep.trap.actions.ZV;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.FMPProperty;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.MCU;
import com.aimir.model.system.Supplier;
import com.aimir.notification.FMPTrap;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeUtil;

@Component
public class EV_ZV_200_23_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_ZV_200_23_0_Action.class);
    
    @Resource(name="transactionManager")
    JpaTransactionManager txmanager;

    @Autowired
    MCUDao dcuDao;
    
    @Autowired
    SupplierDao supplierDao;
    
    @Autowired
    LocationDao locationDao;
    
    /**
     * execute event action
     *
     */
	@Override
	public void execute(FMPTrap trap, EventAlertLog event) throws Exception {
		log.debug("EventName[eventPowerFail] "+" EventCode[" + trap.getCode()+"] Modem["+trap.getSourceId()+"]");
		
        // Initialize
        String ipAddr = trap.getIpAddr();
        String sysID = trap.getSourceId();
        log.info("ipAddr["+ipAddr+"] , sysID["+sysID+"]");
        
        String protocolType = Protocol.GPRS.name();
        
        if (event.getActivatorType() != TargetClass.DCU) {
            log.warn("Activator Type is not MCU");
            return;
        }
        
        MCU mcu = new MCU();
//        JpaTransactionManager txmanager = null;
        TransactionStatus txstatus = null;
        
        try {        	
//        	txmanager = (JpaTransactionManager)DataUtil.getBean("transactionManager");   
//        	MCUDao dcuDao = DataUtil.getBean(MCUDao.class);
//        	SupplierDao supplierDao = DataUtil.getBean(SupplierDao.class);
//        	LocationDao locationDao = DataUtil.getBean(LocationDao.class);
        	
        	log.debug("txmanager["+txmanager+"] , dcuDao["+dcuDao+"] , supplierDao["+supplierDao+"] , locationDao["+locationDao+"]");
        	
        	txstatus = txmanager.getTransaction(null);
            
            mcu.setSysID(sysID);
            mcu.setIpAddr(ipAddr);
            
            // if not mcu, it's is created and installed.
            MCU existMcu = dcuDao.get(mcu.getSysID());        
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
                
                dcuDao.add(mcu);
            } else {
            	log.info("mcu["+existMcu.getSysID()+"] is existed!! - Location Id:"+existMcu.getLocation().getId()+" Location Name:"+existMcu.getLocation().getName());
            	
                existMcu.setProtocolType(CommonConstants.getProtocolByName(protocolType+""));
                existMcu.setLastCommDate(TimeUtil.getCurrentTime());
                existMcu.setProtocolVersion("0102");
                existMcu.setNameSpace("ZV");
                
                event.getEventAlert().setName("DCU Power Fail");
                mcu.setSupplier(existMcu.getSupplier());
            }
            
            event.setActivatorIp(mcu.getIpAddr());
            event.setSupplier(mcu.getSupplier());
        	
        }finally {
            if (txstatus != null) txmanager.commit(txstatus);
        }

        log.debug("DCU Power Fail Action Compelte");
	}
	
}
