package com.aimir.fep.trap.actions.GG;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.ChangeLogDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.DeviceModelDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.OperatorDao;
import com.aimir.dao.system.PrepaymentLogDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.EventUtil;
import com.aimir.fep.util.Hex;
import com.aimir.model.device.EnergyMeter;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.MMIU;
import com.aimir.model.device.Meter;
import com.aimir.model.system.Contract;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.Operator;
import com.aimir.model.system.PrepaymentLog;
import com.aimir.model.system.Supplier;
import com.aimir.notification.FMPTrap;
import com.aimir.util.Condition;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeUtil;
import com.aimir.util.Condition.Restriction;

/**
 * Event ID : GD_200.0.0 (GPRS Modem Install)
 * <br>GPRS Modem
 *
 * @author goodjob
 * @version $Rev: 1 $, $Date: 2014-08-28 10:00:00 +0900 $,
 */
@Component
public class EV_GG_200_1_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_GG_200_1_0_Action.class);
    
    @Resource(name="transactionManager")
    JpaTransactionManager txmanager;
    
    @Autowired
    ModemDao modemDao;
    
    @Autowired
    MeterDao meterDao;
    
    @Autowired
    ContractDao contractDao;
    
    @Autowired
    PrepaymentLogDao pLogDao;
    
    @Autowired
    CodeDao codeDao;
    
    @Autowired
    CommandGW commandGW;
    
    @Autowired
    ChangeLogDao clDao;
    
    @Autowired
    DeviceModelDao deviceModelDao;
    
    @Autowired
    SupplierDao supplierDao;
    
    @Autowired
    LocationDao locationDao;
    
    @Autowired
    OperatorDao operatorDao;
    
    /**
     * execute event action
     *
     * @param trap - FMP Trap(GPRS Modem Event)
     * @param event - Event Alert Log Data
     */
    public void execute(FMPTrap trap, EventAlertLog event) throws Exception
    {
        log.debug("EventName[eventModemInstall] "+" EventCode[" + trap.getCode()+"] Modem["+trap.getSourceId()+"]");
        TransactionStatus txstatus = null;
        try {
            txstatus = txmanager.getTransaction(null);
            String currentTime = TimeUtil.getCurrentTimeMilli();
            String commDate = trap.getTimeStamp();
            String newModemId = event.getEventAttrValue("evtEUI");
            String installedDate = event.getEventAttrValue("evtTime");
            
            if(installedDate == null || "".equals(installedDate) ||!installedDate.subSequence(0, 4).equals(TimeUtil.getCurrentTimeMilli().substring(0, 4))){
            	installedDate = currentTime;
            }
    
            String nameSpace = "";
            if(trap.getCode().indexOf("_") > 0){
            	nameSpace = trap.getCode().substring(0,2);
            }
            
            int resetInterval = Integer.parseInt(event.getEventAttrValue("evtResetInterval"));
            int meteringInterval = Integer.parseInt(event.getEventAttrValue("evtMeteringInterval"));        
            String serverIp = event.getEventAttrValue("evtServerIP");
            String serverPort = event.getEventAttrValue("evtServerPort");        
            String apnAddress = event.getEventAttrValue("evtAPNAddress");
            String apnId = event.getEventAttrValue("evtAPNID");
            String apnPassword = event.getEventAttrValue("evtAPNPassword");
            String modelName = event.getEventAttrValue("evtModelName");        
            String fwVer = event.getEventAttrValue("evtFwVer");
            String buildNumber = event.getEventAttrValue("evtBuildNumber");
            String hwVer = event.getEventAttrValue("evtHwVer");
            // String simNumber = event.getEventAttrValue("evtSimIMSI");     
            String simNumber = event.getEventAttrValue("evtSimNumberID");   
            String meterId = event.getEventAttrValue("evtMeterID");
            //int commStatus = Integer.parseInt((event.getEventAttrValue("evtMeterCommStatus") == null) ? "0" : (event.getEventAttrValue("evtMeterCommStatus")));
            //STS 미터용 정보
            String stsNumber = validateSTSNumber(event.getEventAttrValue("evtStsNumber"));
            String evtBalance = event.getEventAttrValue("evtBalance"); 
            String ipAddr = trap.getIpAddr();
            
            Map<String,Object> balanceInfo = null;
            String balanceDate = null;
            Double credit = null;
            log.debug("evtBalance : " + evtBalance);
            if(evtBalance != null && !"".equals(evtBalance)) {
            	balanceInfo = convertBalance(evtBalance);
            	balanceDate = (String)balanceInfo.get("balanceDate");
                credit = (Double)balanceInfo.get("credit");
            }
    
            if(fwVer != null && !"".equals(fwVer)){
                // 2 바이트 소수점 앞자리와 뒷자리로 만들어야 하나 모뎀에서 뒷자리만 사용함. HW버전은 소수 자리가 맞음.
            	fwVer = Integer.parseInt(fwVer)+"";
            }
            if(hwVer != null && !"".equals(hwVer)){
            	hwVer = DataUtil.getVersionString(Integer.parseInt(hwVer));
            }
            
            MeterType meterType = MeterType.EnergyMeter;
            ModemType modemType = ModemType.MMIU;
            
            DeviceModel modemModel = deviceModelDao.findByCondition("name", modelName);
            
         // get modem
            MMIU modem = (MMIU) modemDao.get(newModemId);
            Supplier supplier = supplierDao.getAll().get(0);
            
            Meter meter = null;
            if (modem == null) {
                modem = new MMIU();                
                modem.setDeviceSerial(newModemId);
                modem.setInstallDate(DateTimeUtil.getDST(supplier.getTimezone().getName(), installedDate));
                modem.setSupplier(supplier);
                modem.setModemType(modemType.name());
                modem.setLocation(locationDao.getAll().get(0));   
                if(modemModel != null){
                    modem.setModel(modemModel); 
                }
//                 if (phoneNumber != null && !"".equals(phoneNumber))
//                    modem.setPhoneNumber(phoneNumber);
                modem.setSimNumber(simNumber);
                modem.setFwRevision(buildNumber);
                modem.setFwVer(fwVer);
                modem.setHwVer(hwVer);
                modem.setLastLinkTime(DateTimeUtil.getDST(supplier.getTimezone().getName(), commDate));
                modem.setProtocolVersion(trap.getProtocolVersion());
                modem.setProtocolType(Protocol.SMS.name());
                modem.setNameSpace(nameSpace);
                modem.setResetInterval(resetInterval);
                modem.setMeteringInterval(meteringInterval);
                modem.setApnAddress(apnAddress);
                modem.setApnId(apnId);
                modem.setApnPassword(apnPassword);
                modem.setIpAddr(ipAddr);
                modemDao.add(modem);
                log.info("Add modem="+newModemId);
            }else{
                if(modemModel != null){
                    modem.setModel(modemModel); 
                } 
//                if (phoneNumber != null && !"".equals(phoneNumber))
//                    modem.setPhoneNumber(phoneNumber);
                modem.setSimNumber(simNumber);
                modem.setFwRevision(buildNumber);
                modem.setFwVer(fwVer);
                modem.setHwVer(hwVer);
                modem.setLastLinkTime(DateTimeUtil.getDST(supplier.getTimezone().getName(), commDate));
                modem.setProtocolVersion(trap.getProtocolVersion());
                modem.setProtocolType(Protocol.SMS.name());
                modem.setNameSpace(nameSpace);
                modem.setResetInterval(resetInterval);
                modem.setMeteringInterval(meteringInterval);
                modem.setApnAddress(apnAddress);
                modem.setApnId(apnId);
                modem.setApnPassword(apnPassword);
                modem.setIpAddr(ipAddr);
            	modemDao.update(modem);
                log.info("Update modem="+newModemId);
            }
            
            if (meterId != null && !"".equals(meterId)) {

                meter = meterDao.get(meterId);
                
                if (meter != null && !meter.getMdsId().equals(meterId)) {
                    meter.setModem(null);
                }
                
                // 미터의 모뎀과 입력받은 모뎀이 다르면 관계를 생성한다.
                meter = meterDao.get(meterId);
                if (meter != null) {
                    if (!modem.getDeviceSerial().equals(meter.getModem().getDeviceSerial())) {
                        meter.setModem(modem);
                    }
                    
                    if(stsNumber != null && !stsNumber.equals(meter.getIhdId())) {
                    	log.info("STSNumber Change [" + meter.getIhdId() + "] => [" + stsNumber +"]");
                    	meter.setIhdId(stsNumber);
                    }
                    //stsNumber가 존재하면 STS모델로 간주한다.
                    if(stsNumber != null && !"OmniPower STS".equals(meter.getModel().getName())) {
                    	log.info("Model Change [" + meter.getModel().getName() + "] => OmniPower STS Model");
                    	DeviceModel stsModel = deviceModelDao.findByCondition("name", "OmniPower STS");
                    	meter.setModel(stsModel);
                    }
                    
                    if("OmniPower STS".equals(meter.getModel().getName()) && stsNumber != null 
                    		&& meter.getContract() != null && credit != null) {
                    	//Contract의 Balance update.
                    	Contract contract = meter.getContract();
                    	Double preContractCredit = contract.getCurrentCredit() == null ? 0d : contract.getCurrentCredit();
                    	log.debug("serverCredit["+contract.getCurrentCredit()+"] => STS Credit[" + credit +"]");
                    	if(!preContractCredit.equals(credit)) {
                    		log.info("save pLog");
                    		contract.setCurrentCredit(credit);
                        	contractDao.update(contract);
                        	
                        	PrepaymentLog pLog = new PrepaymentLog();
                        	pLog.setContract(contract);
                        	pLog.setCustomer(meter.getCustomer());
                        	Operator operator = operatorDao.getOperatorByLoginId("admin");
                        	pLog.setOperator(operator);
                        	pLog.setBalance(credit);
                        	if(balanceDate != null) {
                        		pLog.setLastTokenDate(balanceDate+"00");
                        	}
                        	pLog.setLocation(contract.getLocation());
                            pLogDao.add(pLog);
                    	}
                    }
                }
                else {
                    meter = new EnergyMeter();                    
                    meter.setMdsId(meterId);
                    meter.setInstallDate(DateTimeUtil.getDST(supplier.getTimezone().getName(), installedDate));
                    meter.setMeterType(CommonConstants.getMeterTypeByName(meterType.name()));
                    meter.setLocation(locationDao.getAll().get(0));
                    meter.setSupplier(supplier);
                    meter.setModem(modem);
                    Set<Condition> condition = new HashSet<Condition>();
                    condition.add(new Condition("name", new Object[]{"OmniPower%"}, null, Restriction.LIKE));
                    
                    List<DeviceModel> meterModel = deviceModelDao.findByConditions(condition);
                    if(meterModel != null && meterModel.size() > 0){
                        meter.setModel(meterModel.get(0));
                    }
                    
                    if(stsNumber != null) {
                    	meter.setIhdId(stsNumber);
                    }

                    meter.setLpInterval(30);//TODO default 15
                    EventUtil.sendEvent("Equipment Registration",
                            TargetClass.valueOf(meterType.name()),
                            meterId,
                            currentTime, new String[][] {},
                            event);
                }
                meterDao.saveOrUpdate(meter);
            }
            else log.warn("Meter of Modem[" + newModemId + "] is NULL!");

            event.setActivatorId(newModemId);
            event.setActivatorType(TargetClass.Modem);
            event.append(EventUtil.makeEventAlertAttr("modemID",
                                                 "java.lang.String", newModemId));
            event.append(EventUtil.makeEventAlertAttr("message",
                                                 "java.lang.String",
                                                 "Modem is connected with a Meter[" + modelName + "," + meterId + "]"));
        }
        catch (Exception e) {
        	log.error(e,e);
            throw e;
        }
        finally {
            if (txstatus != null) txmanager.commit(txstatus);
        }
    }
    
    private Map<String,Object> convertBalance(String evtBalance) {
    	Map<String,Object> map = new HashMap<String,Object>();
    	int pos = 0;
        byte[] b = new byte[6];
        System.arraycopy(Hex.encode(evtBalance), pos, b, 0, b.length);
        pos += b.length;
        String balanceDate = String.format("%4d%02d%02d%02d%02d",
                DataUtil.getIntTo2Byte(new byte[]{b[0], b[1]}),
                DataUtil.getIntToByte(b[2]),
                DataUtil.getIntToByte(b[3]),
                DataUtil.getIntToByte(b[4]),
                DataUtil.getIntToByte(b[5]));
        map.put("balanceDate", balanceDate);
        log.debug("balanceDate [" + balanceDate + "]");
        
        b = new byte[4];
        System.arraycopy(Hex.encode(evtBalance), pos, b, 0, b.length);
        pos += b.length;
        log.debug(Hex.decode(b));
        long lcredit = 0l;
        if(b[0] < 0) {
        	lcredit = (((((~b[0]) & 0xff) << 8) + (((~b[1]) & 0xff) << 8) + (((~b[2]) & 0xff) << 8) + ((~b[3]) & 0xff)) + 1)*-1;
        } else {
        	lcredit = DataUtil.getIntToBytes(b);
        }

        b = new byte[1];
        System.arraycopy(Hex.encode(evtBalance), pos, b, 0, b.length);
        log.debug(Hex.decode(b));
        int dec = DataUtil.getIntToBytes(b);
        
        Double credit = (double)lcredit / Math.pow(10, dec);
        map.put("credit", credit);
        log.debug("Credit[" + credit + "]");
        
        return map;
    }
    
    private String validateSTSNumber(String stsNumber) {
    	
    	stsNumber = (stsNumber == null || "0".equals(stsNumber)) ? null : stsNumber;
    	
    	//stsNumber 가 11자리가 아니면 null로 바꾼다.
    	if(stsNumber != null && stsNumber.length() != 11) {
    		log.debug("stsNumber [" + stsNumber + "] is Wrong Number.");
    		stsNumber = null;
    	}
    	
    	return stsNumber;
    }

}
