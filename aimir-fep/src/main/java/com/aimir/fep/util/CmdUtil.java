/*
 * $Id:CmdUtil.java
 *
 */

package com.aimir.fep.util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.DefaultChannel;
import com.aimir.constants.CommonConstants.McuType;
import com.aimir.constants.CommonConstants.MeterCommand;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.constants.CommonConstants.ModemNetworkType;
import com.aimir.constants.CommonConstants.ModemPowerType;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.dao.device.AsyncCommandLogDao;
import com.aimir.dao.device.AsyncCommandParamDao;
import com.aimir.dao.device.AsyncCommandResultDao;
import com.aimir.dao.device.ConverterDao;
import com.aimir.dao.device.IEIUDao;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.MMIUDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.mvm.LpEMDao;
import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.protocol.fmp.common.Target;
import com.aimir.fep.protocol.fmp.datatype.SMIValue;
import com.aimir.fep.protocol.fmp.datatype.FMPVariable;
import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.protocol.fmp.frame.service.entry.cmdHistEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.codiBindingEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.codiDeviceEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.codiEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.codiMemoryEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.codiNeighborEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.commLogEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.mcuEventEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.meterLogEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.meterTimeSyncLogEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.mobileLogEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.sensorInfoNewEntry;
import com.aimir.fep.protocol.fmp.frame.service.Entry;
import com.aimir.model.device.AsyncCommandLog;
import com.aimir.model.device.AsyncCommandLogPk;
import com.aimir.model.device.AsyncCommandParam;
import com.aimir.model.device.AsyncCommandResult;
import com.aimir.model.device.Converter;
import com.aimir.model.device.IEIU;
import com.aimir.model.device.MCU;
import com.aimir.model.device.MCUCodi;
import com.aimir.model.device.MCUCodiBinding;
import com.aimir.model.device.MCUCodiDevice;
import com.aimir.model.device.MCUCodiMemory;
import com.aimir.model.device.MCUCodiNeighbor;
import com.aimir.model.device.MMIU;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.device.OperationList;
import com.aimir.model.device.SubGiga;
import com.aimir.model.device.ZRU;
import com.aimir.model.mvm.LpEM;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.Supplier;
import com.aimir.util.Condition;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.Mask;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeUtil;
import com.aimir.util.Condition.Restriction;

import java.util.Hashtable;

/**
 * Command GW Utility
 * &author Y.S Kim
 * @version $Id: CmdUtil.java, v1.0 2005/12/04
 */

public class CmdUtil
{
    private static Log log = LogFactory.getLog(CmdUtil.class);

    private static MIBUtil mu = MIBUtil.getInstance();
    private static MMIUDao mmiuDao = DataUtil.getBean(MMIUDao.class);
    private static IEIUDao ieiuDao = DataUtil.getBean(IEIUDao.class);
    private static ConverterDao converterDao = DataUtil.getBean(ConverterDao.class);
    private static MCUDao mcuDao = DataUtil.getBean(MCUDao.class);
    private static ModemDao modemDao = DataUtil.getBean(ModemDao.class);
    private static AsyncCommandParamDao commandParamDao = DataUtil.getBean(AsyncCommandParamDao.class);
    private static AsyncCommandResultDao commandResultDao = DataUtil.getBean(AsyncCommandResultDao.class);
    private static AsyncCommandLogDao commandLogDao = DataUtil.getBean(AsyncCommandLogDao.class);
    private static LpEMDao lpEmDao = DataUtil.getBean(LpEMDao.class);
    
    private static JpaTransactionManager txManager = 
        (JpaTransactionManager)DataUtil.getBean("transactionManager");
    
    /**
     * 2007.2.21 by JS
     * @param mcuName      집중기 아이디
     * @return
     */
    public static Target getTarget(String mcuName)
    throws Exception
    {
        TransactionStatus txStatus = null;
        try {
            txStatus = txManager.getTransaction(new DefaultTransactionDefinition(TransactionDefinition.ISOLATION_READ_UNCOMMITTED));
        
            Target target = new Target();
            MCU mcu = mcuDao.get(mcuName);
            if (mcu != null) {
                if (mcu.getIpv6Addr() != null && !"".equals(mcu.getIpv6Addr()))
                    target.setIpAddr(mcu.getIpv6Addr());
                else
                    target.setIpAddr(mcu.getIpAddr());
                target.setIpv6Addr(mcu.getIpv6Addr());
                target.setTargetId(mcuName);
                McuType mcuType = McuType.UNKNOWN;
                int mcuCode = 0;
                
                if(mcu.getMcuType() != null){
                    String mcuTypeCode = mcu.getMcuType().getCode();
                    mcuCode = Integer.parseInt(mcuTypeCode.substring(mcuTypeCode.lastIndexOf(".")+1));
                }
                mcuType = CommonConstants.getMCUType(mcuCode);
                target.setTargetType(mcuType);
                target.setPhoneNumber(mcu.getSysPhoneNumber());
                target.setLocation(mcu.getLocation());
                if (Boolean.parseBoolean(FMPProperty.getProperty("protocol.ssl.use")))
                    target.setPort(mcu.getSysTlsPort() == null ? 8100:mcu.getSysTlsPort());
                else
                    target.setPort(mcu.getSysLocalPort()==null ? 8000:mcu.getSysLocalPort());
                target.setFwVer(mcu.getSysSwVersion());
                target.setProtocol(Protocol.valueOf(mcu.getProtocolType().getName()));
                target.setFwRevision(mcu.getSysSwRevision());
                target.setReceiverType("MCU");
                target.setReceiverId(mcu.getSysID());
                target.setNameSpace(mcu.getNameSpace());
                target.setTimeZoneId(mcu.getSupplier().getTimezone().getName());
            }
            else {
                Modem modem = modemDao.get(mcuName);
                target = getTarget(modem);
            }
            
            log.debug("Target Info : " + target.toString());
            if(target.getNameSpace() == null || target.getNameSpace().equals("")){
            	log.warn("[Type=" + target.getReceiverType() + ", ID=" 
            	        + target.getReceiverId() + "] Can not found NameSpace Information.!!!");
            }
            
            txManager.commit(txStatus);
            
            return target;
        }
        catch (Exception e) {
            log.error(e, e);
            if (txManager != null)
                txManager.rollback(txStatus);
            throw e;
        }
    }
    
    /**
     * 2011.8.9 by JS
     * 2016.06.01 this funtion is not used by subgiga modem.
     * @param modem      모뎀
     * @return
     */
    public static Target getTarget(Modem modem)
    throws Exception
    {
        
        TransactionStatus txStatus = null;
        try {
            txStatus = txManager.getTransaction(new DefaultTransactionDefinition(TransactionDefinition.ISOLATION_READ_UNCOMMITTED));

            Target target = new Target();
            modem = modemDao.get(modem.getId());
            target.setReceiverType(modem.getModemType().name());
            target.setReceiverId(modem.getDeviceSerial());
            target.setModemId(modem.getDeviceSerial());
            Supplier supplier = modem.getSupplier();
            
            if(supplier!=null)
                target.setSupplierId(supplier.getId().toString());
            
            MCU mcu = modem.getMcu();
            if (mcu != null) {
                if (mcu.getIpv6Addr() != null && !"".equals(mcu.getIpv6Addr()))
                    target.setIpAddr(mcu.getIpv6Addr());
                else
                    target.setIpAddr(mcu.getIpAddr());
                target.setTargetId(mcu.getSysID());
                target.setIpv6Addr(mcu.getIpv6Addr());
                Supplier su = mcu.getSupplier();
                if(su!=null)
                    target.setSupplierId(su.getId().toString());
                
                McuType mcuType = McuType.UNKNOWN;
                int mcuCode = 0;
                
                if(mcu.getMcuType() != null){
                    String mcuTypeCode = mcu.getMcuType().getCode();
                    mcuCode = Integer.parseInt(mcuTypeCode.substring(mcuTypeCode.lastIndexOf(".")+1));
                }
                mcuType = CommonConstants.getMCUType(mcuCode);
                target.setTargetType(mcuType);
                target.setPhoneNumber(mcu.getSysPhoneNumber());
                target.setLocation(mcu.getLocation());
                if (Boolean.parseBoolean(FMPProperty.getProperty("protocol.ssl.use")))
                    target.setPort(mcu.getSysTlsPort() == null ? 8100:mcu.getSysTlsPort());
                else
                    target.setPort(mcu.getSysLocalPort()==null ? 8000:mcu.getSysLocalPort());
                target.setFwVer(mcu.getSysSwVersion());
                target.setProtocol(Protocol.valueOf(mcu.getProtocolType().getName()));
                target.setFwRevision(mcu.getSysSwRevision());
                target.setNameSpace(mcu.getNameSpace());
                
                if (modem.getMeter() != null) {
                    for (Meter m : modem.getMeter()) {
                        if (m.getModemPort() == null || m.getModemPort() == 0) {
                            target.setMeterId(m.getMdsId());
                            target.setReceiverType(m.getMeterType().getName());
                            target.setReceiverId(m.getMdsId());
                            break;
                        }
                    }
                }
                /*
                if (modem.getMeter().size() == 1) {
                    Meter meter = modem.getMeter().iterator().next();
                    target.setReceiverType(meter.getMeterType().getName());
                    target.setReceiverId(meter.getMdsId());
                }
                */
            }
            else if(modem.getModemType() == ModemType.SubGiga){
                // ZRU mmiu = mmiuDao.get(modem.getId());
                SubGiga subgiga = (SubGiga)modem;
                
                if (subgiga.getIpv6Address() != null && !"".equals(subgiga.getIpv6Address()))
                    target.setIpAddr(subgiga.getIpv6Address());
                else
                    target.setIpAddr(subgiga.getIpAddr());
                log.debug("getIpAddr:"+subgiga.getIpAddr());
                target.setIpv6Addr(subgiga.getIpv6Address());
                
                log.debug("getIpv6Addr:"+subgiga.getIpAddr());
                target.setTargetId(subgiga.getDeviceSerial());
                log.debug("getDeviceSerial:"+subgiga.getDeviceSerial());
                // MCU doesn't have Subgiga type. and targetType is not needed. default setting.
                // 2016.06.01 by elevas
                target.setTargetType(McuType.SubGiga);
                log.debug("getModemType:"+ModemType.valueOf(subgiga.getModemType().name()));
                target.setLocation(subgiga.getLocation());
                log.debug("getLocation:"+subgiga.getLocation());
                target.setFwVer(subgiga.getFwVer());
                log.debug("getFwVer:"+subgiga.getFwVer());
                target.setFwRevision(subgiga.getFwRevision());
                log.debug("getFwRevision:" + subgiga.getFwRevision());
                target.setProtocol(subgiga.getProtocolType());
                log.debug("getProtocolType:"+subgiga.getProtocolType());
                // target.setNameSpace(mmiu.getNameSpace());
                
                if (subgiga.getMeter() != null && subgiga.getMeter().size() == 1
                        && subgiga.getProtocolType() != Protocol.IP) {
                    Meter meter = subgiga.getMeter().iterator().next();
                    target.setMeterModel(meter.getModel().getCode());
                    log.debug("METERMODEL:"+meter.getModel().getName());
                    target.setReceiverType(meter.getMeterType().getName());
                    target.setReceiverId(meter.getMdsId());
                }
            }
            else if(modem.getModemType().equals(ModemType.MMIU)){
                MMIU mmiu = mmiuDao.get(modem.getId());
                
                if (mmiu.getIpv6Address() != null && !"".equals(mmiu.getIpv6Address()))
                    target.setIpAddr(mmiu.getIpv6Address());
                else
                    target.setIpAddr(mmiu.getIpAddr());
                log.debug("getIpAddr:"+mmiu.getIpAddr());
                target.setTargetId(mmiu.getDeviceSerial());
                target.setIpv6Addr(mmiu.getIpv6Address());
                log.debug("getIpv6Addr:"+mmiu.getIpv6Address());
                log.debug("getDeviceSerial:"+mmiu.getDeviceSerial());
                target.setTargetType(McuType.valueOf(mmiu.getModemType().name()));
                log.debug("getModemType:"+McuType.valueOf(mmiu.getModemType().name()));
                target.setPhoneNumber(mmiu.getPhoneNumber());
                log.debug("getPhoneNumber:"+mmiu.getPhoneNumber());
                target.setLocation(mmiu.getLocation());
                log.debug("getLocation:"+mmiu.getLocation());
                target.setFwVer(mmiu.getFwVer());
                log.debug("getFwVer:"+mmiu.getFwVer());
                target.setFwRevision(mmiu.getFwRevision());
                log.debug("getFwRevision:" + mmiu.getFwRevision());
                target.setProtocol(mmiu.getProtocolType());
                log.debug("getProtocolType:"+mmiu.getProtocolType());
                target.setNameSpace(mmiu.getNameSpace());
                
                /*
                if (mmiu.getMeter() != null && mmiu.getMeter().size() == 1
                        && mmiu.getProtocolType() != Protocol.IP) {
                    Meter meter = mmiu.getMeter().iterator().next();
                    target.setMeterModel(meter.getModel().getCode());
                    log.debug("METERMODEL:"+meter.getModel().getName());
                    target.setReceiverType(meter.getMeterType().getName());
                    target.setReceiverId(meter.getMdsId());
                }
                */
                if (mmiu.getMeter() != null) {
                    for (Meter m : mmiu.getMeter()) {
                        if (m.getModemPort() == null || m.getModemPort() == 0) {
                            target.setMeterId(m.getMdsId());
                            target.setMeterModel(m.getModel().getCode());
                            log.debug("METERMODEL:"+m.getModel().getName());
                            target.setReceiverType(m.getMeterType().getName());
                            target.setReceiverId(m.getMdsId());
                            break;
                        }
                    }
                }
            }
            else if(modem.getModemType().equals(ModemType.IEIU)){
                IEIU ieiu = ieiuDao.get(modem.getId());

                target.setIpAddr(ieiu.getIpAddr());
                log.debug("getIpAddr:"+ieiu.getIpAddr());
                target.setTargetId(ieiu.getDeviceSerial());
                log.debug("getDeviceSerial:"+ieiu.getDeviceSerial());
                target.setTargetType(McuType.valueOf(ieiu.getModemType().name()));
                log.debug("getModemType:"+McuType.valueOf(ieiu.getModemType().name()));
                target.setPhoneNumber(ieiu.getPhoneNumber());
                log.debug("getPhoneNumber:"+ieiu.getPhoneNumber());
                target.setLocation(ieiu.getLocation());
                log.debug("getLocation:"+ieiu.getLocation());
                target.setFwVer(ieiu.getFwVer());
                log.debug("getFwVer:"+ieiu.getFwVer());
                target.setFwRevision(ieiu.getFwRevision());
                log.debug("getFwRevision:" + ieiu.getFwRevision());
                target.setProtocol(ieiu.getProtocolType());
                log.debug("getProtocolType:"+ieiu.getProtocolType());
                target.setGroupNumber(ieiu.getGroupNumber()+"");
                target.setMemberNumber(ieiu.getMemberNumber()+"");
                
                if (ieiu.getMeter() != null && ieiu.getMeter().size() == 1) {
                    Meter meter = ieiu.getMeter().iterator().next();
                    target.setMeterModel(meter.getModel().getCode());
                    log.debug("METERMODEL:"+meter.getModel().getName());
                    target.setReceiverType(meter.getMeterType().getName());
                    target.setReceiverId(meter.getMdsId());
                }
            }
            else if(modem.getModemType().equals(ModemType.Converter)){
                Converter converter = converterDao.get(modem.getId());

                target.setIpAddr(converter.getIpAddr());
                log.debug("getIpAddr:"+converter.getIpAddr());
                target.setPort(converter.getSysPort());
                log.debug("getPort:"+converter.getSysPort());
                target.setTargetId(converter.getDeviceSerial());
                log.debug("getDeviceSerial:"+converter.getDeviceSerial());
                target.setTargetType(McuType.Converter);
                log.debug("getModemType:"+modem.getModemType());
                target.setLocation(converter.getLocation());
                log.debug("getLocation:"+converter.getLocation());
                target.setFwVer(converter.getFwVer());
                log.debug("getFwVer:"+converter.getFwVer());
                target.setFwRevision(converter.getFwRevision());
                log.debug("getFwRevision:" + converter.getFwRevision());
                target.setProtocol(converter.getProtocolType());
                log.debug("getProtocolType:"+converter.getProtocolType());
                
                if (converter.getMeter() != null && converter.getMeter().size() == 1) {
                    Meter meter = converter.getMeter().iterator().next();
                    target.setMeterModel(meter.getModel().getCode());
                    log.debug("METERMODEL:"+meter.getModel().getName());
                    target.setReceiverType(meter.getMeterType().getName());
                    target.setReceiverId(meter.getMdsId());
                }
            }
            txManager.commit(txStatus);
            log.debug("Target id[" + target.getTargetId() + "] type[" + target.getTargetType() + 
                    "] ip["+target.getIpAddr()+"] port["+target.getPort()+"] protocol["+target.getProtocol()+
                    "] receiverType[" + target.getReceiverType() + "] receiverId[" + target.getReceiverId() + "], NameSpace[" + target.getNameSpace() + "]");
            
            if(target.getNameSpace() == null || target.getNameSpace().equals("")){
            	log.warn("Target id[" + target.getTargetId() + "] Can not found NameSpace Information.!!!");
            }
            
            return target;            

        }
        catch (Exception e) {
            if (txManager != null)
                txManager.rollback(txStatus);
            throw e;
        }

    }

    
    public static Target getNullBypassTarget(Meter meter)  throws Exception {
		return getNullBypassTarget(meter, meter.getModem());		
    }

    public static Target getNullBypassTarget(Modem modem)  throws Exception{
    	return getNullBypassTarget(null, modem);
    }
    
    private static Target getNullBypassTarget(Meter meter, Modem modem)  throws Exception
    {        
        TransactionStatus txStatus = null;
        try {
            txStatus = txManager.getTransaction(new DefaultTransactionDefinition(TransactionDefinition.ISOLATION_READ_UNCOMMITTED));

            Target target = new Target();
            modem = modemDao.get(modem.getId());
            target.setReceiverType(modem.getModemType().name());
            target.setReceiverId(modem.getDeviceSerial());
            target.setModemId(modem.getDeviceSerial());
            Supplier supplier = modem.getSupplier();
            
            if(supplier!=null)
                target.setSupplierId(supplier.getId().toString());
            
            if(modem.getModemType() == ModemType.SubGiga){
                SubGiga subgiga = (SubGiga)modem;
                
                log.debug("# SubGiga IpvAddr:"+ subgiga.getIpAddr());
                log.debug("# SubGiga Ipv6Addr:"+ subgiga.getIpv6Address());
                
                if (subgiga.getIpv6Address() != null && !"".equals(subgiga.getIpv6Address())){
                    target.setIpAddr(subgiga.getIpv6Address());                    
                    target.setIpv6Addr(subgiga.getIpv6Address());
                }else{
                    target.setIpAddr(subgiga.getIpAddr());
                	target.setIpv6Addr(subgiga.getIpAddr());
                }
                
                log.debug("# TARGET IpvAddr:"+target.getIpAddr());
                log.debug("# TARGET Ipv6Addr:"+target.getIpv6Addr());
                
                target.setTargetId(subgiga.getDeviceSerial());
                log.debug("getDeviceSerial:"+subgiga.getDeviceSerial());
                // MCU doesn't have Subgiga type. and targetType is not needed. default setting.
                // 2016.06.01 by elevas
                target.setTargetType(McuType.SubGiga);
                log.debug("getModemType:"+ModemType.valueOf(subgiga.getModemType().name()));
                target.setLocation(subgiga.getLocation());
                log.debug("getLocation:"+subgiga.getLocation());
                target.setFwVer(subgiga.getFwVer());
                log.debug("getFwVer:"+subgiga.getFwVer());
                target.setFwRevision(subgiga.getFwRevision());
                log.debug("getFwRevision:" + subgiga.getFwRevision());
                target.setProtocol(subgiga.getProtocolType());
                log.debug("getProtocolType:"+subgiga.getProtocolType());
                target.setNameSpace(subgiga.getNameSpace());
                
                log.debug("## subgiga.getMeter().size() => " + subgiga.getMeter().size());
                
                /*
                 * 미터가 파라미터로 넘어올경우 - 모뎀과 맵핑되어있고 마스터미터인 경우만 처리
                 */
                Iterator<Meter> iterator = subgiga.getMeter().iterator();
                if(meter != null){
                    while(iterator.hasNext()){
                    	Meter tempMeter = iterator.next();
                    	if(meter.getMdsId().equals(tempMeter.getMdsId())){
                    	    target.setMeterId(meter.getMdsId());
                            target.setMeterModel(meter.getModel().getCode());
                            log.debug("METER_ID: " + meter.getMdsId() + ", METERMODEL: " + meter.getModel().getName());
                    		break;
                    	}
                    }                	
                }
                /*
                 * 모뎀만 파라미터로 넘어올 경우 - 모뎀에 물려있는 첫번째 마스터 미터를 대상으로 처리함.
                 */                
                else{
                	while(iterator.hasNext()){
                		Meter tempMeter = iterator.next();
                		if (tempMeter.getModemPort() != null && 0 < tempMeter.getModemPort()){
                			log.debug("This meter is slave (m-bus) meter. modemport"+ tempMeter.getModemPort());
                		}else{
                            target.setMeterId(tempMeter.getMdsId());
                            target.setMeterModel(tempMeter.getModel().getCode());
                            log.debug("This meter is master meter. METER_ID="+ tempMeter.getMdsId() + ", METERMODEL=" + tempMeter.getModel().getName());
                            break;
                		}                		
                	}                	
                }
            }
            else if(modem.getModemType().equals(ModemType.MMIU)){
                MMIU mmiu = (MMIU)modem;
                
                if (mmiu.getIpv6Address() != null && !"".equals(mmiu.getIpv6Address())){
                    target.setIpAddr(mmiu.getIpv6Address());                    
                    target.setIpv6Addr(mmiu.getIpv6Address());
                }else{
                    target.setIpAddr(mmiu.getIpAddr());
                	target.setIpv6Addr(mmiu.getIpAddr());
                }
                
                log.debug("# TARGET IpvAddr:"+target.getIpAddr());
                log.debug("# TARGET Ipv6Addr:"+target.getIpv6Addr());
                
                target.setTargetId(mmiu.getDeviceSerial());
                log.debug("getDeviceSerial:"+mmiu.getDeviceSerial());
                target.setTargetType(McuType.valueOf(mmiu.getModemType().name()));
                log.debug("getModemType:"+McuType.valueOf(mmiu.getModemType().name()));
                target.setPhoneNumber(mmiu.getPhoneNumber());
                log.debug("getPhoneNumber:"+mmiu.getPhoneNumber());
                target.setLocation(mmiu.getLocation());
                log.debug("getLocation:"+mmiu.getLocation());
                target.setFwVer(mmiu.getFwVer());
                log.debug("getFwVer:"+mmiu.getFwVer());
                target.setFwRevision(mmiu.getFwRevision());
                log.debug("getFwRevision:" + mmiu.getFwRevision());
                target.setProtocol(mmiu.getProtocolType());
                log.debug("getProtocolType:"+mmiu.getProtocolType());
                target.setNameSpace(mmiu.getNameSpace());
                log.debug("getNameSpace:"+mmiu.getNameSpace());

                log.debug("## mmiu.getMeter().size() => " + mmiu.getMeter().size());
                
                /*
                 * 미터가 파라미터로 넘어올경우 - 모뎀과 맵핑되어있고 마스터미터인 경우만 처리
                 */
                Iterator<Meter> iterator = mmiu.getMeter().iterator();
                if(meter != null){
                    while(iterator.hasNext()){
                    	Meter tempMeter = iterator.next();
                    	if(meter.getMdsId().equals(tempMeter.getMdsId())){
                    	    target.setMeterId(meter.getMdsId());
                            target.setMeterModel(meter.getModel().getCode());
                            log.debug("METER_ID: " + meter.getMdsId() + ", METERMODEL: " + meter.getModel().getName());
                    		break;
                    	}
                    }                	
                }
                /*
                 * 모뎀만 파라미터로 넘어올 경우 - 모뎀에 물려있는 첫번째 마스터 미터를 대상으로 처리함.
                 */                
                else{
                	while(iterator.hasNext()){
                		Meter tempMeter = iterator.next();
                		if (tempMeter.getModemPort() != null && 0 < tempMeter.getModemPort()){
                			log.debug("This meter is slave (m-bus) meter. modemport"+ tempMeter.getModemPort());
                		}else{
                            target.setMeterId(tempMeter.getMdsId());
                            target.setMeterModel(tempMeter.getModel().getCode());
                            log.debug("This meter is master meter. METER_ID="+ tempMeter.getMdsId() + ", METERMODEL=" + tempMeter.getModel().getName());
                            break;
                		}                		
                	}                	
                }
            }
            else if(modem.getModemType().equals(ModemType.ZRU)){
            	ZRU zru = (ZRU)modem;
                
                log.debug("# ZRU IpvAddr:"+ zru.getIpAddr());
                target.setIpAddr(zru.getIpAddr());
                target.setTargetId(zru.getDeviceSerial());
                log.debug("getDeviceSerial:"+zru.getDeviceSerial());
                // MCU doesn't have Subgiga type. and targetType is not needed. default setting.
                // 2016.06.01 by elevas
                target.setTargetType(McuType.ZRU);
                log.debug("getModemType:"+ModemType.valueOf(zru.getModemType().name()));
                target.setLocation(zru.getLocation());
                log.debug("getLocation:"+zru.getLocation());
                target.setFwVer(zru.getFwVer());
                log.debug("getFwVer:"+zru.getFwVer());
                target.setFwRevision(zru.getFwRevision());
                log.debug("getFwRevision:" + zru.getFwRevision());
                target.setProtocol(zru.getProtocolType());
                log.debug("getProtocolType:"+zru.getProtocolType());
                target.setNameSpace(zru.getNameSpace());
                
                log.debug("## zru.getMeter().size() => " + zru.getMeter().size());
                
                /*
                 * 미터가 파라미터로 넘어올경우 - 모뎀과 맵핑되어있고 마스터미터인 경우만 처리
                 */
                Iterator<Meter> iterator = zru.getMeter().iterator();
                if(meter != null){
                    while(iterator.hasNext()){
                    	Meter tempMeter = iterator.next();
                    	if(meter.getMdsId().equals(tempMeter.getMdsId())){
                    	    target.setMeterId(meter.getMdsId());
                            target.setMeterModel(meter.getModel().getCode());
                            log.debug("METER_ID: " + meter.getMdsId() + ", METERMODEL: " + meter.getModel().getName());
                    		break;
                    	}
                    }                	
                }
                /*
                 * 모뎀만 파라미터로 넘어올 경우 - 모뎀에 물려있는 첫번째 마스터 미터를 대상으로 처리함.
                 */                
                else{
                	while(iterator.hasNext()){
                		Meter tempMeter = iterator.next();
                		if (tempMeter.getModemPort() != null && 0 < tempMeter.getModemPort()){
                			log.debug("This meter is slave (m-bus) meter. modemport"+ tempMeter.getModemPort());
                		}else{
                            target.setMeterId(tempMeter.getMdsId());
                            target.setMeterModel(tempMeter.getModel().getCode());
                            log.debug("This meter is master meter. METER_ID="+ tempMeter.getMdsId() + ", METERMODEL=" + tempMeter.getModel().getName());
                            break;
                		}                		
                	}                	
                }
            }
            else if(modem.getModemType().equals(ModemType.Converter)){
                Converter converter = (Converter)modem;

                target.setIpAddr(converter.getIpAddr());
                log.debug("getIpAddr:"+converter.getIpAddr());
                target.setPort(converter.getSysPort());
                log.debug("getPort:"+converter.getSysPort());
                target.setTargetId(converter.getDeviceSerial());
                log.debug("getDeviceSerial:"+converter.getDeviceSerial());
                target.setTargetType(McuType.Converter);
                log.debug("getModemType:"+modem.getModemType());
                target.setLocation(converter.getLocation());
                log.debug("getLocation:"+converter.getLocation());
                target.setFwVer(converter.getFwVer());
                log.debug("getFwVer:"+converter.getFwVer());
                target.setFwRevision(converter.getFwRevision());
                log.debug("getFwRevision:" + converter.getFwRevision());
                target.setProtocol(converter.getProtocolType());
                log.debug("getProtocolType:"+converter.getProtocolType());
                
                if (converter.getMeter() != null && converter.getMeter().size() == 1) {
                    Meter converterMeter = converter.getMeter().iterator().next();
                    target.setMeterId(converterMeter.getMdsId());
                    target.setMeterModel(converterMeter.getModel().getCode());
                    log.debug("METER_ID: " + converterMeter.getMdsId() + ", METERMODEL: " + converterMeter.getModel().getName());
                }
            }
            txManager.commit(txStatus);
            log.debug("Target Info = " +  (target != null ? target.toString() : "target is Null."));         
            if(target.getNameSpace() == null || target.getNameSpace().equals("")){
            	log.warn("Target id[" + target.getTargetId() + "] Can not found NameSpace Information.!!!");
            }
            
            return target;            

        }
        catch (Exception e) {
            if (txManager != null)
                txManager.rollback(txStatus);
            throw e;
        }

    }
    
    
    
    public static LPData[] getPreviousLp(String meterId, String yyyymmdd)
    {
        List<LpEM> list = null;
        Set<Condition> set = new HashSet<Condition>();
        LPData[] lpData = null;
        
        TransactionStatus txStatus = null;
        try {
            txStatus = txManager.getTransaction(new DefaultTransactionDefinition(TransactionDefinition.ISOLATION_READ_UNCOMMITTED));
        
            Condition condition1 = new Condition("id.mdevId",new Object[] {meterId}, null,  Restriction.EQ);
            Condition condition2 = new Condition("yyyymmdd",new Object[] {TimeUtil.getPreDay(yyyymmdd,-1).substring(0,8)}, null,  Restriction.EQ);
            set.add(condition1);
            set.add(condition2);
            
            list = lpEmDao.getLpEMsByListCondition(set);
            
            txManager.commit(txStatus);
            
            if(list != null && list.size() > 0){
                lpData = new LPData[list.size()];
                int i = 0;
                for(LpEM lp : list){

                    if(lp.getChannel() == DefaultChannel.Usage.getCode()){
                        lpData[i++] = new LPData();
                        lpData[i++].setDatetime(lp.getYyyymmddhh());
                        lpData[i++].setCh(new Double[]{lp.getValue_00()});
                        lpData[i++].setLp(lp.getValue()+lp.getValue_00());
                        lpData[i++].setV(new Double[]{lp.getValue_00()});
                    }

                }
            }
        }
        catch (Exception e) {
            txManager.rollback(txStatus);
        }
        return lpData;
    }


    public static String getMIBPropertyName(String name)
    {
        log.debug("PropertyName[" + name + "]");

        return name;
    }

    public static Hashtable<String, String> convMCUSMItoMOP(SMIValue smiValue) throws Exception
    {
        try
        {
            MIBUtil mibUtil = MIBUtil.getInstance();

            String oid = smiValue.getOid().getValue();
            String mopName = mibUtil.getName(oid);
            Hashtable<String, String> table = new Hashtable<String, String>();
            FMPVariable fmpv = smiValue.getVariable();
            if (fmpv != null)
            {
                // log.debug("FMPVariable="+fmpv.toString());
                /*
                 * if(fmpv instanceof OCTET){ mop.setValue(((OCTET)fmpv).toHexString());
                 * }else{ mop.setValue(fmpv.toString()); }
                 */
                if (mopName.equals("sysLocation")) {
                    String loc = new String(fmpv.encode(), "EUC-KR");
                    if (loc != null && loc.length() > 2)
                        table.put(mopName, loc.substring(2));
                }
                else
                    table.put(mopName, fmpv.toString());
            }
            return table;
        }
        catch (Exception e)
        {
            log.error(e, e);
            throw new Exception("Convert SMI to MOPROPERTY Fail");
        }
    }

    public static Hashtable<String, String> convSMItoMOP(SMIValue smiValue)
        throws Exception
    {
        try
        {
            String oid = smiValue.getOid().getValue();
            String name = mu.getName(oid);
            Hashtable<String, String> table = new Hashtable<String, String>();
            FMPVariable fmpv = smiValue.getVariable();
            if (fmpv != null)
            {
                table.put(name, fmpv.toString());

            }
            return table;
        }
        catch (Exception e)
        {
            log.error(e,e);
            throw new Exception("Convert SMI to Hashtable Fail");
        }
    }

    public static String getBooleanValue(String propValue)
    {
        if (propValue.toLowerCase().equals("true"))
        {
            return String.valueOf(1);
        }
        else
        {
            return String.valueOf(0);
        }
    }

    public static String getMaskValue(String propValue)
    {
        Mask mask = new Mask();
        if (propValue.toLowerCase().equals("all"))
        {
            mask.setBitAll();
        }
        else
        {
            String[] bits = propValue.split("\\|");
            for (int i = 0; i < bits.length; i++)
            {
                mask.setBit(Integer.parseInt(bits[i]));
            }
        }
        return String.valueOf(mask.getMask());
    }

    public static String getValueString(String value)
    {
        return value;
    }
    
    public static byte[] getBytes(Vector<?> v)
    {
        byte[] b = new byte[v.size()];
        for (int i = 0; i < v.size(); i++)
        {
            b[i] = (byte) ((Integer) v.elementAt(i)).byteValue();
        }
        return b;
    }

    public static SMIValue[] getModemSMIValue(Modem modem) throws Exception
    {
        try
        {
            ArrayList<SMIValue> res = new ArrayList<SMIValue>();

            SMIValue sv = null;
            if (modem == null)
            {
                log.error("sensor is null");
                return null;
            }
            
            log.debug(modem.toString());
            
            sv = convMOPtoSMI("sensorId", modem.getDeviceSerial());
            if (sv != null)
            {
                log.debug("smiValue["+sv.toString()+"]");
                res.add(sv);
            }
            sv = convMOPtoSMI("sensorType",CommonConstants.getModemTypeCode(modem.getModemType()));
            if (sv != null)
            {
                log.debug("smiValue["+sv.toString()+"]");
                res.add(sv);
            }
            /*
             * TODO 모뎀에 여러 미터가 있을 수 있어서 ...
            sv = convMOPtoSMI(sensor,sensor.getProperty("unitType"));
            if (sv != null)
            {
                log.debug("smiValue["+sv.toString()+"]");
                res.add(sv);
            }
            sv = convMOPtoSMI(sensor,sensor.getProperty("unitSerial"));
            if (sv != null)
            {
                log.debug("smiValue["+sv.toString()+"]");
                res.add(sv);
            }
            sv = convMOPtoSMI("sensorServiceType",sensor.getProperty("svcType"));
            if (sv != null)
            {
                log.debug("smiValue["+sv.toString()+"]");
                res.add(sv);
            }
            */

            return (SMIValue[]) res.toArray(new SMIValue[0]);

        }
        catch (Exception e)
        {
            log.error(e,e);
            throw new Exception("Internal Server Error:"+e.getMessage());
        }
    }

    public static SMIValue convMOPtoSMI(String propName, String propValue)
        throws Exception
    {
        try
        {
            if (propName != null)
            {
                return DataUtil.getSMIValueByObject(propName, propValue);
            }
            else
            {
                return null;
            }
        }
        catch (Exception e)
        {
            log.error(e,e);
            throw new Exception("Internal Server Error:"+e.getMessage());
        }
    }

    public static FileInfo getFileInfo(cmdHistEntry e)
    {
        FileInfo fi = new FileInfo();
        fi.setFileDate(e.getCmdHistDate().toString());
        fi.setFileName(e.getCmdHistFileName().toString());
        fi.setFileSize(Long.parseLong(e.getCmdHistFileSize().toString()));
        return fi;
    }

    public static FileInfo getFileInfo(mcuEventEntry e)
    {
        FileInfo fi = new FileInfo();
        fi.setFileDate(e.getMcuEvnetDate().toString());
        fi.setFileName(e.getMcuEventFileName().toString());
        fi.setFileSize(Long.parseLong(e.getMcuEventFileSize().toString()));
        return fi;
    }

    public static FileInfo getFileInfo(commLogEntry e)
    {
        FileInfo fi = new FileInfo();
        fi.setFileDate(e.getCommLogDate().toString());
        fi.setFileName(e.getCommLogFileName().toString());
        fi.setFileSize(Long.parseLong(e.getCommLogFileSize().toString()));
        return fi;
    }

    public static FileInfo getFileInfo(meterLogEntry e)
    {
        FileInfo fi = new FileInfo();
        fi.setFileDate(e.getMeterLogDate().toString());
        fi.setFileName(e.getMeterLogFileName().toString());
        fi.setFileSize(Long.parseLong(e.getMeterLogFileSize().toString()));
        return fi;
    }

    public static FileInfo getFileInfo(mobileLogEntry e)
    {
        FileInfo fi = new FileInfo();
        fi.setFileDate(e.getMobileLogDate().toString());
        fi.setFileName(e.getMobileLogFileName().toString());
        fi.setFileSize(Long.parseLong(e.getMobileLogFileSize().toString()));
        return fi;
    }

    public static FileInfo getFileInfo(meterTimeSyncLogEntry e)
    {
        FileInfo fi = new FileInfo();
        fi.setFileDate(e.getMeterTimeSyncLogDate().toString());
        fi.setFileName(e.getMeterTimeSyncLogFileName().toString());
        fi.setFileSize(Long.parseLong(e.getMeterTimeSyncLogFileSize().toString()));
        return fi;
    }

    public static FileInfo getFileInfo(Entry e)
    {
        log.debug("Entry="+e);
        if (e instanceof cmdHistEntry)
        {
            return getFileInfo((cmdHistEntry) e);
        }
        else if (e instanceof meterLogEntry)
        {
            return getFileInfo((meterLogEntry) e);
        }
        else if (e instanceof mcuEventEntry)
        {
            return getFileInfo((mcuEventEntry) e);
        }
        else if (e instanceof commLogEntry)
        {
            return getFileInfo((commLogEntry) e);
        }
        else if (e instanceof mobileLogEntry)
        {
            return getFileInfo((mobileLogEntry) e);
        }
        else if (e instanceof meterTimeSyncLogEntry)
        {
            return getFileInfo((meterTimeSyncLogEntry) e);
        }
        else
        {
            log.error("Unknown Log List");
            return null;
        }
    }

    public static int getMeterPulseConst(Meter meter)
    {
        int pulseConst = 1000;
        try
        {
            if(meter != null)
            {
                if(MeterType.valueOf(meter.getMeterType().getName()) == MeterType.EnergyMeter)
                {
                    double dval = 1.0/meter.getPulseConstant();
                    pulseConst = (new Double(dval)).intValue();
                }
                else
                {
                    pulseConst = meter.getPulseConstant().intValue();
                }
            }
        }catch(Exception ex) { }

        return pulseConst;
    }

    public static String getYymmddhhmmss(byte[] b, int offset, int len)
        throws Exception {

        /* formatter */
        DecimalFormat formatter1 = new DecimalFormat("0000");
        /* formatter */
        DecimalFormat formatter2 = new DecimalFormat("00");

        int blen = b.length;
        if(blen-offset < 6) {
            throw new Exception("YYMMDDHHMMSS FORMAT ERROR : "+(blen-offset));
        }
        if(len != 6) {
            throw new Exception("YYMMDDHHMMSS LEN ERROR : "+len);
        }

        int idx = offset;

        int yy = DataFormat.hex2unsigned8(b[idx++]);
        int mm = DataFormat.hex2unsigned8(b[idx++]);
        int dd = DataFormat.hex2unsigned8(b[idx++]);
        int hh = DataFormat.hex2unsigned8(b[idx++]);
        int MM = DataFormat.hex2unsigned8(b[idx++]);
        int ss = DataFormat.hex2unsigned8(b[idx++]);

        StringBuffer ret = new StringBuffer();

        int currcen = (Integer.parseInt(TimeUtil.getCurrentTime().substring(0,4))/100)*100;

        int year   = yy;
        if(year != 0){
            year = yy + currcen;
        }

        ret.append(formatter1.format(year));
        ret.append(formatter2.format(mm));
        ret.append(formatter2.format(dd));
        ret.append(formatter2.format(hh));
        ret.append(formatter2.format(MM));
        ret.append(formatter2.format(ss));

        return ret.toString();
    }

    public static boolean isAsynch(Modem modem)
    throws Exception
    {
        if (modem.getModemType() == ModemType.ZEUPLS) {
            com.aimir.model.device.ZEUPLS zeupls = (com.aimir.model.device.ZEUPLS)modem;
            if (zeupls.getPowerType() == ModemPowerType.Battery && 
                    zeupls.getNetworkType() ==  ModemNetworkType.FFD)
                return true;
        }
        else if (modem.getModemType() == ModemType.ZBRepeater) {
            com.aimir.model.device.ZBRepeater repeater = (com.aimir.model.device.ZBRepeater)modem;
            if (repeater.getPowerType() == ModemPowerType.Battery &&
                    repeater.getNetworkType() == ModemNetworkType.FFD)
                return true;
        }
        return false;
    }

    public static int[] convertStartStopIndex(int lpInterval, String fromDate, String toDate) throws Exception
    {
        int startIndex = 0;
        int stopIndex = 0;
        
        
        if(fromDate.substring(8, 14).equals("000000") && (fromDate.equals(toDate) )){
            toDate = toDate.substring(0, 8) + "235959";
        }
        
        Calendar today = Calendar.getInstance();
        String now = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMdd") + "235959";
        today.setTime(DateTimeUtil.getDateFromYYYYMMDDHHMMSS(now));
        Calendar from = Calendar.getInstance();
        from.setTime(DateTimeUtil.getDateFromYYYYMMDD(fromDate));
        Calendar to = Calendar.getInstance();
        to.setTime(DateTimeUtil.getDateFromYYYYMMDDHHMMSS(toDate));
        
        log.debug("today: " + DateTimeUtil.getFormatTime(today));
        log.debug("from: " + DateTimeUtil.getFormatTime(from));
        log.debug("to: "  + DateTimeUtil.getFormatTime(to));
        
        while(today.after(to)){
            to.add(Calendar.DAY_OF_YEAR, 1);
            startIndex++;
            System.out.println(startIndex);
        }
        startIndex = startIndex * ((24 * 60) / lpInterval);  // 일수 X ((하루를분으로환산) / lpInterval)
        
        while(today.after(from)){
            from.add(Calendar.DAY_OF_YEAR, 1);
            stopIndex++;
        }
        stopIndex = stopIndex * ((24 * 60) / lpInterval);  // 일수 X ((하루를분으로환산) / lpInterval) 
        
        log.debug("StartIndex[" + startIndex + "] StopIndex[" + stopIndex + "]");
        
        return new int[] {startIndex, stopIndex};
    }
    
    public static int[] convertOffsetCount(DeviceModel model, int lpInterval, ModemType modemType, String fromDate, String toDate)
    throws Exception
    {
        int nOffset = 0;
        int nCount = 0;

        OperationList op = null;
        int paramType = 0;
        for (Iterator i = model.getOperationList().iterator(); i.hasNext(); ) {
            op = (OperationList)i.next();
            if (CommonConstants.getMeterCommand(op.getOperationCode().getCode()) == MeterCommand.ON_DEMAND_METERING) {
                if (op.getParamType() != null) {
                    paramType = op.getParamType();
                    break;
                }
            }
        }
        
        if (!"".equals(fromDate) && !"".equals(toDate)) {
            Calendar today = Calendar.getInstance();
            Calendar from = Calendar.getInstance();
            Calendar to = Calendar.getInstance();
            
            int amount = 1;
            int field = Calendar.DAY_OF_YEAR;
            
            // Ondemand paramType = 0 인 경우 일 수로 계산한다.
            if (paramType == 0) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                today.setTime(sdf.parse(DateTimeUtil.getDateString(new Date()).substring(0,8)));
                from.setTime(sdf.parse(fromDate.substring(0,8)));
                to.setTime(sdf.parse(toDate.substring(0,8)));
    
                /*
                if (today.before(to)) {
                    throw new Exception("request toDate[" + toDate + "] must not be after today");
                }
    
                if (!today.after(from)) {
                    throw new Exception("request fromDate[" + fromDate + "] must be before today");
                }
                */
                log.debug("today: "+today);
                log.debug("from: "+from);
                log.debug("to: "+to);
            }
            // paramType == 1인 경우 한시간 주기로 시작일의 offset 과 종료일과의 개수를 계산한다.
            else if (paramType == 1) {
                amount = 1; // lpInterval; 주기단위가 없다. 시간단위
                field = Calendar.HOUR;
                
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
                today.setTime(sdf.parse(DateTimeUtil.getDateString(new Date()).substring(0,12)));
                today.add(field, -amount);
                
                if (fromDate.length() < 12) {
                    fromDate = StringUtil.endAppendNStr('0', fromDate, 12);
                }
                from.setTime(sdf.parse(fromDate.substring(0, 12)));
                
                if ((toDate.length() == 14 && toDate.substring(8, 14).equals("000000")) ||
                        (toDate.length() == 12 && toDate.substring(8, 12).equals("0000"))) {
                    toDate = toDate.substring(0, 8);
                }
                
                if (toDate.length() == 8) {
                    toDate += "2359";
                }
                else if (toDate.length() == 10) {
                    toDate += "59";
                }
                to.setTime(sdf.parse(toDate.substring(0, 12)));
                
                log.debug("today: "+today);
                log.debug("from: "+from);
                log.debug("to: "+to);
            }
            // paramType == 2인 경우 lpInterval 주기로 시작일의 offset 과 종료일과의 개수를 계산한다.
            // paramType == 2인 경우는 fromDate와 toDate가 분까지 있는 것으로 만들어야 한다.
            else if (paramType == 2) {
                amount = lpInterval; 
                field = Calendar.MINUTE;
                
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
                today.setTime(sdf.parse(DateTimeUtil.getDateString(new Date()).substring(0,12)));
                today.add(field, -amount);
                
                if (fromDate.length() < 12) {
                    fromDate = StringUtil.endAppendNStr('0', fromDate, 12);
                }
                from.setTime(sdf.parse(fromDate.substring(0, 12)));
                
                if ((toDate.length() == 14 && toDate.substring(8, 14).equals("000000")) ||
                        (toDate.length() == 12 && toDate.substring(8, 12).equals("0000"))) {
                    toDate = toDate.substring(0, 8);
                }
                
                if (toDate.length() == 8) {
                    toDate += "2359";
                }
                else if (toDate.length() == 10) {
                    toDate += "59";
                }
                to.setTime(sdf.parse(toDate.substring(0, 12)));
                
                log.debug("today: "+today);
                log.debug("from: "+from);
                log.debug("to: "+to);
            }
            
            while (today.after(from))  {
                from.add(field, amount);
                nOffset++;
            }

            while (today.after(to)) {
                to.add(field, amount);
                nCount++;
            }
            nCount = nOffset - nCount + 1;
        }
        
        log.debug("nOffset[" + nOffset + "] nCount[" + nCount + "]");
        return new int[] {nOffset, nCount};
    }
       

    /**
     * @param trId          Transaction ID
     * @param mcuId         MCU id
     * @param deviceType    Device Type (ZRU, ZEUPLS, EnergyMeter ClassName)
     * @param deviceId      Device ID
     * @param command       Command
     * @param trOption      Transaction Option
     *                      0x01 : ASYNC_OPT_RETURN_CODE_EVT
     *                      0x02 : ASYNC_OPT_RESULT_DATA_EVT
     *                      0x10 : ASYNC_OPT_RETURN_CODE_SAVE
     *                      0x20 : ASYNC_OPT_RESULT_DATA_SAVE
     * @param day           Save Option(1 ~ 255)
     * @param initNice       (-2 ~ 3. Default 0)
     * @param initTry
     * @param requestTime
     * @param args
     * @param serviceType   1:NMS 2:MTR
     * @param operator
     * @return
     * @throws Exception
     */
    public static boolean createAsyncTr(long trId, String mcuId, String deviceType,
            String deviceId, String command, int trOption, int day,
            int initNice, int initTry, String requestTime, String[][] args,
            int serviceType, String operator)
    throws Exception
    {
        AsyncCommandLog commandLog = new AsyncCommandLog();
        commandLog.setTrId(trId);
        commandLog.setMcuId(mcuId);
        commandLog.setDeviceType(deviceType);
        commandLog.setDeviceId(deviceId);
        commandLog.setCommand(command);
        commandLog.setTrOption(trOption);
        commandLog.setDay(day);
        commandLog.setInitNice(initNice);
        commandLog.setInitTry(initTry);
        commandLog.setRequestTime(requestTime);
        commandLog.setState(0x01);
        commandLog.setOperator(operator);

        commandLogDao.add(commandLog);
        
        if(args!= null){
            List<AsyncCommandParam> params = new ArrayList<AsyncCommandParam>();
            for (int i = 0; i < args.length; i++) {
                AsyncCommandParam param = new AsyncCommandParam();
                
                param.setTrId(trId);
                param.setMcuId(mcuId);
                param.setNum(i);
                param.setParamType(args[i][0]);
                param.setParamValue(args[i][1]);
                
                params.add(param);
                commandParamDao.add(param);
            }
   
        }

        return true;
    }

    /**
     * @param trId
     * @param mcuId
     * @param curNice
     * @param curTry
     * @param queue
     * @param createTime
     * @param lastTime
     * @param state         Transaction State (0x01:Wating, 0x02:Running, 0x04:Terminate, 0x08:Delete)
     * @param errorCode     Error Code(IF4ERR)
     * @param eventType     Event Type (0x01 : Code Event, 0x02 : Data Event)
     * @param resultCnt
     * @param oid
     * @param len
     * @param data
     * @throws Exception
     */
    public static void updateAyncTr(long trId, String mcuId, int curNice,
            int curTry, int queue, String createTime, String lastTime, int state,
            int errorCode, int eventType, int resultCnt,
            String[] oid, int[] len, FMPVariable[] data)
    throws Exception
    {
        AsyncCommandLogPk pk = new AsyncCommandLogPk();
        pk.setTrId(trId);
        pk.setMcuId(mcuId);
        
        AsyncCommandLog commandLog = commandLogDao.get(pk);
        if(commandLog == null){
            commandLog = new AsyncCommandLog();
            commandLog.setId(pk);
        }
        commandLog.setTrId(trId);
        commandLog.setMcuId(mcuId);
        commandLog.setCurNice(curNice);
        commandLog.setCurTry(curTry);
        commandLog.setQueue(queue);
        commandLog.setCreateTime(createTime);
        commandLog.setLastTime(lastTime);
        commandLog.setState(state);
        commandLog.setErrorCode(errorCode);
        commandLog.setEventType(eventType);
        commandLog.setResultCnt(resultCnt);

        commandLogDao.saveOrUpdate(commandLog);
        
        List<AsyncCommandResult> list = new ArrayList<AsyncCommandResult>();
        for (int i = 0; i < oid.length; i++) {
            if (data[i] instanceof OCTET) {
                AsyncCommandResult result = new AsyncCommandResult();
                result.setTrId(trId);
                result.setMcuId(mcuId);
                result.setNum(i);
                result.setOid(oid[i]);
                result.setLength(new Long(len[i]));
                result.setData(((OCTET)data[i]).getValue());
                
                list.add(result);
                commandResultDao.add(result);
            }
        }

    }

    public static MCUCodiMemory makeCodiMemory(codiMemoryEntry cme) {
        if (cme != null) {
            MCUCodiMemory mcm = new MCUCodiMemory();            

            mcm.setCodiAddressTableSize(cme.getCodiBindingTableSize().getValue());
            mcm.setCodiNeighborTableSize(cme.getCodiNeighborTableSize().getValue());
            mcm.setCodiRouteTableSize(cme.getCodiRouteTableSize().getValue());
            mcm.setCodiMaxHops(cme.getCodiMaxHops().getValue());
            mcm.setCodiPacketBufferCount(cme.getCodiPacketBufferCount().getValue());
           
            return mcm;
        }
        
        return null;
    }
    
    public static MCUCodiNeighbor makeCodiNeighbor(codiNeighborEntry cne) {
        if (cne != null) {
            MCUCodiNeighbor mcn = new MCUCodiNeighbor();
            
            mcn.setCodiNeighborIndex(cne.getCodiNeighborIndex().getValue());
            mcn.setCodiNeighborShortId(cne.getCodiNeighborShortID().getValue());
            mcn.setCodiNeighborLqi(cne.getCodiNeighborLqi().getValue());
            mcn.setCodiNeighborInCost(cne.getCodiNeighborInCost().getValue());
            mcn.setCodiNeighborOutCost(cne.getCodiNeighborOutCost().getValue());
            mcn.setCodiNeighborAge(cne.getCodiNeighborAge().getValue());
            mcn.setCodiNeighborId(cne.getCodiNeighborID().getValue());
            
            return mcn;
        }
        
        return null;
    }
    
    public static MCUCodiBinding makeCodiBinding(codiBindingEntry cbe) {
        if (cbe != null) {
            MCUCodiBinding mcb = new MCUCodiBinding();
            
            mcb.setCodiBindIndex(cbe.getCodiBindIndex().getValue());
            mcb.setCodiBindType(cbe.getCodiBindType().getValue());
            mcb.setCodiBindLocal(cbe.getCodiBindLocal().getValue());
            mcb.setCodiBindRemote(cbe.getCodiBindRemote().getValue());
            mcb.setCodiBindID(cbe.getCodiBindID().getValue());
            mcb.setCodiLastHeard(cbe.getCodiLastHeard().getValue());
            
            return mcb;
        }
        
        return null;
    }
    
    public static MCUCodiDevice makeCodiDevice(codiDeviceEntry cde) {
        if (cde != null) {
            MCUCodiDevice mcd = new MCUCodiDevice();
            
            mcd.setCodiDevice(cde.getCodiDevice().toString());
            mcd.setCodiBaudRate(cde.getCodiBaudRate().getValue());
            mcd.setCodiParityBit(cde.getCodiParityBit().getValue());
            mcd.setCodiDataBit(cde.getCodiDataBit().getValue());
            mcd.setCodiStopBit(cde.getCodiStopBit().getValue());
            mcd.setCodiRtsCts(cde.getCodiRtsCts().getValue());
            
            return mcd;
        }
        
        return null;
    }
    
    public static MCUCodi makeCodi(codiEntry ce)
    {
        if(ce != null){
            MCUCodi codi = new MCUCodi();
            
            codi.setCodiIndex(ce.getCodiIndex().getValue());
            codi.setCodiString(ce.getCodiID().getValue());
            codi.setCodiType(ce.getCodiType().getValue());
            codi.setCodiShortID(ce.getCodiShortID().getValue());
            String ver = Hex.decode(ce.getCodiFwVer().encode());
            codi.setCodiFwVer(String.format("%s.%s",  ver.substring(0,1), ver.substring(1)));
            ver = Hex.decode(ce.getCodiHwVer().encode());
            codi.setCodiHwVer(String.format("%s.%s", ver.substring(0,1), ver.substring(1)));
            // ver = Hex.decode(ce.getCodiZAIfVer().encode());
            // codi.setCodiZAIfVer(String.format("%s.%s", ver.substring(0, 1), ver.substring(1)));
            codi.setCodiZAIfVer(ce.getCodiZAIfVer().getValue());
            // ver = Hex.decode(ce.getCodiZZIfVer().encode());
            // codi.setCodiZZIfVer(String.format("%s.%s", ver.substring(0, 1), ver.substring(1)));
            codi.setCodiZZIfVer(ce.getCodiZZIfVer().getValue());
            codi.setCodiFwBuild(Hex.decode(ce.getCodiFwBuild().encode()));
            codi.setCodiResetKind(ce.getCodiResetKind().getValue());
            codi.setCodiAutoSetting(new Boolean(ce.getCodiAutoSetting().getValue() == 0 ? false:true));
            codi.setCodiChannel(ce.getCodiChannel().getValue());
            codi.setCodiPanID(ce.getCodiPanID().toString());
            codi.setCodiExtPanId(ce.getCodiExtPanID().toString());
            codi.setCodiRfPower(ce.getCodiRfPower().getValue());
            codi.setCodiTxPowerMode(ce.getCodiTxPowerMode().getValue());
            codi.setCodiPermit(ce.getCodiPermit().getValue());
            codi.setCodiEnableEncrypt(ce.getCodiEnableEncrypt().getValue());
            codi.setCodiLinkKey(ce.getCodiLineKey().toString());
            codi.setCodiNetworkKey(ce.getCodiNetworkKey().toString());
            
            return codi;
        }
        
        return null;
    }

    public static Modem makeModem(sensorInfoNewEntry spe, String mcuId)
    {
        Modem modem = new Modem();

        if (mcuId != null)
        {
            MCU mcu = new MCU();
            mcu.setSysID(mcuId);
            modem.setMcu(mcu);
        }
        
        modem.setDeviceSerial(spe.getSensorID().toString());
        modem.setLastLinkTime(spe.getSensorLastConnect().toString());
        modem.setInstallDate(spe.getSensorInstallDate().toString());
        modem.setCommState(spe.getSensorState().getValue());
        modem.setHwVer(spe.getSensorHwVersion().decodeVersion()+"");
        modem.setFwVer(spe.getSensorFwVersion().decodeVersion()+"");
        modem.setFwRevision((spe.getSensorFwBuild().getValue() < 9? "0":"")+spe.getSensorFwBuild().getValue());

        return modem;
    }
}
