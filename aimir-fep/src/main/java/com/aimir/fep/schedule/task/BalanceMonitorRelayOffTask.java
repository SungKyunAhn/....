package com.aimir.fep.schedule.task;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.MeterStatus;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.dao.device.OperationLogDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.ContractChangeLogDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.LanguageDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.command.ws.server.ResponseMap;
import com.aimir.fep.meter.AbstractMDSaver;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.sms.SendSMS;
import com.aimir.model.device.MCU;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.device.OperationLog;
import com.aimir.model.system.Code;
import com.aimir.model.system.Contract;
import com.aimir.model.system.ContractChangeLog;
import com.aimir.model.system.DecimalPattern;
import com.aimir.model.system.Language;
import com.aimir.model.system.Supplier;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.DecimalUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

/**
 * 잔액을 체크하여 0이하가 되면 cut off를 실시하고 SMS로 통보한다.
 * ECG / EDH 에 적용
 * 
 * @author elevas
 *
 */
@Service
public class BalanceMonitorRelayOffTask {

    protected static Log log = LogFactory.getLog(BalanceMonitorRelayOffTask.class);
    
    @Resource(name="transactionManager")
    JpaTransactionManager txmanager;
    
    @Autowired
    ContractDao contractDao;
    
    private boolean isNowRunning = false;

    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"/config/spring.xml"}); 
        DataUtil.setApplicationContext(ctx);
        
        BalanceMonitorRelayOffTask task = ctx.getBean(BalanceMonitorRelayOffTask.class);
        task.execute();
        System.exit(0);
    }

    public void execute() {
        if(isNowRunning){
            log.info("########### BalanceMonitorV2Task is already running...");
            return;
        }
        isNowRunning = true;
        
        log.info("############################# Balance Monitor Relay Off Scheduler Start ##################################");
        
        Map<String, List<Contract>> grpContracts = getContractGroup();
        
        String mcuId = null;
        ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 10, 15, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());
        for (Iterator<String> i = grpContracts.keySet().iterator(); i.hasNext();) {
            mcuId = i.next();
            try {
                executor.execute(new RelayOffThread(mcuId, grpContracts.get(mcuId)));
                // new RelayOffThread(mcuId, grpContracts.get(mcuId)).run();
            }
            catch (Exception e) {
                log.error(e, e);
            }
        }
        try {
           executor.shutdown();
           while (!executor.isTerminated()) {
           }
        }
        catch (Exception e) {}
        
        // contract 잔액정보 업데이트
        // 잔액과 임계치 비교
        // 임계치보다 작으면 고객 통보
        // 잔액이 0 이고 emergency available 이 false, emergency credit 이 수동인 경우 차단
        // 차단후 고객 통보
        // 잔액이 0 이고 emergency available 이 true, emergency credit 이 자동인 경우 고객정보를 emergency credit 모드로 업데이트
        // emergnecy credit 모드를 고객 통보
        
        log.info("############################# Balance Monitor Relay Off Scheduler End ##################################");
        
        isNowRunning = false;
    }
    
    private Map<String, List<Contract>> getContractGroup() {
        TransactionStatus txstatus = null;
        
        try {
            txstatus = txmanager.getTransaction(null);
            
            Map<String, List<Contract>> grpContracts = new HashMap<String, List<Contract>>();
            // 선불 계약정보
            Set<Condition> conditions = new HashSet<Condition>();
            // 선불고객이고 잔액이 0이하인 고객인 전부 가져온다. 미터 상태가 Cuff Off가 아닌것.
            conditions.add(new Condition("creditType", new Object[]{"c"}, null, Restriction.ALIAS));
            conditions.add(new Condition("c.name", new Object[]{"prepay", "emergency credit"}, null, Restriction.IN));
            conditions.add(new Condition("currentCredit", new Object[] {0.0}, null, Restriction.LE));
            // conditions.add(new Condition("meter", new Object[]{"m"}, null, Restriction.ALIAS));
            // conditions.add(new Condition("m.meterStatus", new Object[]{"s"}, null, Restriction.ALIAS));
            // conditions.add(new Condition("s.name", new Object[] {"CutOff"}, null, Restriction.NOT));
            
            List<Contract> contracts = contractDao.findByConditions(conditions);
            
            if (contracts == null || contracts.size() <= 0) {
                log.info("Available Contract is not exist");
                isNowRunning = false;
                log.info("############################# Balance Monitor Scheduler End ##################################");
                return grpContracts;
            }
            
            MCU mcu = null;
            List<Contract> contractList = null;
            Modem modem = null;
            int[] contractIds = new int[]{40692,40407,43181,41728,43428,42812,43654,43244,42168,41751,43857,42599};
            boolean isDuplicated = false;
            log.info("RelayOff_SIZE[" + contracts.size() + "]");
            for (Contract contract : contracts) {
                isDuplicated = false;
                for (int c : contractIds) {
                    if (contract.getId() == c) {
                        isDuplicated = true;
                        break;
                    }
                }
                
                if (!isDuplicated && contract.getMeter() != null && contract.getMeter().getModem() != null) {
                    modem = contract.getMeter().getModem();
                    if (modem.getMcu() != null) {
                        mcu = contract.getMeter().getModem().getMcu();
                        
                        contractList = grpContracts.get(mcu.getSysID());
                        if (contractList == null) {
                            contractList = new ArrayList<Contract>();
                        }
                        contractList.add(contract);
                        grpContracts.put(mcu.getSysID(), contractList);
                    }
                    else {
                        if (modem.getModemType() == ModemType.MMIU) {
                            contractList = grpContracts.get(modem.getDeviceSerial());
                            if (contractList == null) {
                                contractList = new ArrayList<Contract>();
                            }
                            contractList.add(contract);
                            grpContracts.put(modem.getDeviceSerial(), contractList);
                        }
                    }
                }
            }
            
            return grpContracts;
        }
        finally {
            if (txstatus != null) txmanager.commit(txstatus);
        }
    }
}

class RelayOffThread implements Runnable {
    private static Log log = LogFactory.getLog(RelayOffThread.class);
    
    JpaTransactionManager txmanager = (JpaTransactionManager)DataUtil.getBean("transactionManager");
    
    private String mcuId;
    private List<Contract> contractList;
    Properties messageProp = null;
    
    RelayOffThread(String mcuId, List<Contract> contractList) {
        this.mcuId = mcuId;
        this.contractList = contractList;
    }

    @Override
    public void run() {
        log.info("########## start thread MCU[" + mcuId + "] Contract_size[" + contractList.size() + "] ##########");
        // TransactionStatus txstatus = txmanager.getTransaction(null);
        String mcuStatus = null;
        for (Contract c : contractList) {
            mcuStatus = checkBalance(c.getId());
            // 집중기 상태가 연결이 안되는 경우 종료한다.
            if (mcuStatus != null && mcuStatus.equals("Can't connect to DCU")) {                
                log.warn("[MCU:" + mcuId + " Contract:" + c.getContractNumber() + "] break checking balance");
                break;
            }
        }
        // txmanager.commit(txstatus);
        log.info("########## End thread MCU[" + mcuId + "] Contract_size[" + contractList.size() + "] ##########");
    }
    
    private Properties getMessageProp(Supplier supplier){
        try {
            if(messageProp == null){
                messageProp = new Properties();
                LanguageDao languageDao = DataUtil.getBean(LanguageDao.class);
                Language la = languageDao.get(supplier.getLangId());
                String lang = (la.getCode_2letter() == null) ? "en" : la.getCode_2letter();
                InputStream ips = getClass().getClassLoader().getResourceAsStream("lang/message_"+ lang +".properties");
                if(ips == null){
                    ips = getClass().getClassLoader().getResourceAsStream("message_en.properties");             
                }
                messageProp.load(ips);          
            }           
        } catch (Exception e) {
            log.debug(e);
        }
        
        return this.messageProp;
    }
    
    /**
     * method name : checkBalance
     * method Desc : 잔액을 체크해서 0일 경우 Emergency Credit Mode 로 변경, 혹은 차단한다.
     *
     * @param contract
     */
    private String checkBalance(int contractId) {
        TransactionStatus txstatus = null;
        try {
            txstatus = txmanager.getTransaction(null);
            txmanager.setDefaultTimeout(1800);
            log.info("TxDefaultTimeout[" + txmanager.getDefaultTimeout() + "]");
            ContractDao contractDao = DataUtil.getBean(ContractDao.class);
            SupplierDao supplierDao = DataUtil.getBean(SupplierDao.class);
            
            Contract contract = contractDao.get(contractId);
            Meter meter = contract.getMeter();
            Code meterStatus = meter.getMeterStatus();
            meterStatus.getCode();
            Modem modem = meter.getModem();
            Supplier supplier = supplierDao.get(meter.getSupplierId());
            Code meterType = meter.getMeterType();
            meterType.getCode();
            Code creditType = contract.getCreditType();
            creditType.getCode();
            Code contractStatus = contract.getStatus();
            if (contractStatus != null)
                contractStatus.getCode();
            
            String mcuStatus = null;
            String meterSaver = null;
            
            if (meter.getModel() != null && meter.getModel().getDeviceConfig() != null
                    && meter.getModel().getDeviceConfig().getSaverName() != null) {
                meterSaver = meter.getModel().getDeviceConfig().getSaverName();
            }
            else {
                txmanager.commit(txstatus);
                return "Meter Saver not exist";
            }
            
            MCU mcu = null;
            if (modem != null) {
                mcu = modem.getMcu();
                
                if (mcu == null) {
                    mcu = new MCU();
                    mcu.setSysID(modem.getDeviceSerial());
                }
                mcu.getSysID();
            }
            
            Double credit = (contract.getCurrentCredit() == null ? 0d : contract.getCurrentCredit());     // 잔액
            ResultStatus status = ResultStatus.FAIL;
            boolean isCutOff = false;     // 미터 차단 실행 여부
    
            txmanager.commit(txstatus);
            
            log.debug("[DCU:" + mcu.getSysID() + " METER:" + meter.getMdsId() + "] credit : [" + credit + "], PrepaymentThreshold : [" + contract.getPrepaymentThreshold() + "]" );
            
            // 잔액이 0 일 경우
            if (credit <= 0) {
                log.debug("[DCU:" + mcu.getSysID() + " METER:" + meter.getMdsId() + "] is credit <= 0");
                if (Code.PREPAYMENT.equals(creditType.getCode()) 
                        && (contract.getEmergencyCreditAutoChange() == null
                        || !contract.getEmergencyCreditAutoChange())) {
                    // 잔액이 0 이하 이고 emergency credit 이 수동인 경우 차단
    
                    // 미터가 차단되어 있는지 체크
                    // 이 값은 아래 relay off가 성공하고 미터 상태가 이미 cut off 가 아닐때 sms를 보내도록 하기 위한 것이다.
                    if (meterStatus != null && meterStatus.getCode() != null && 
                        (meterStatus.getName().equals(MeterStatus.CutOff.name()) 
                                || meterStatus.getName().equals(MeterStatus.Delete.name()))) {
                        // isCutOff = true;
                        return null;
                    }
    
                    // Relay Switch CmdOperationUtil 호출
                    try {
                        if (meter != null && modem != null && mcu != null) {
                            // txstatus = txmanager.getTransaction(null);
                            Map<String, Object> result = relayValveOff(mcu.getSysID(), meter.getMdsId(), meterSaver).getResponse();
                            // txmanager.commit(txstatus);
                            
                            Object[] values = result.values().toArray(new Object[0]);
                            
                            JsonParser jparser = new JsonParser();
                            JsonArray ja = null;
                            for (Object o : values) {
                                ja = jparser.parse((String)o).getAsJsonArray();
                                for (int i = 0; i < ja.size(); i++) {
                                    if (ja.get(i).getAsJsonObject().get("name").getAsString().equals("Result") || 
                                            ja.get(i).getAsJsonObject().get("value").getAsString().contains("SUCCESS") ||
                                            ja.get(i).getAsJsonObject().get("value").getAsString().contains("Relays disconnected by command")) {
                                        status = ResultStatus.SUCCESS;
                                        break;
                                    }
                                    else if (ja.get(i).getAsJsonObject().get("value").getAsString().equals("Can't connect to DCU")) {
                                        mcuStatus = "Can't connect to DCU";
                                        break;
                                    }
                                    else if (ja.get(i).getAsJsonObject().get("value").getAsString().contains("IF4ERR")) {
                                        status = ResultStatus.FAIL;
                                        break;
                                    }
                                    else {
                                        log.warn("[DCU:" + mcu.getSysID() + " METER:" + meter.getMdsId() + "] RelayValveOff Fail - " + ja.get(i).getAsJsonObject().get("value").getAsString());
                                    }
                                }
                                if (status == ResultStatus.SUCCESS) break;
                            }
                            
                            log.debug("[DCU:" + mcu.getSysID() + " METER:" + meter.getMdsId() + "] Relay Off Status [" + status + "]");
                            
                            // Operation Log에 기록
                            saveOperationLog(supplier, meterType, meter.getMdsId(), "balance-schedule", status.getCode(), status.name(), "relayValveOff");
                        }
                    }
                    catch (Exception e) {
                        status = ResultStatus.FAIL;
                        log.error("[DCU:" + mcu.getSysID() + " METER:" + meter.getMdsId() + "] Relay Off Status [" + status + "]", e);                        
                    }
                    
                        
                    // message event 시작 : 미터 차단이 성공했을 때
                    if(status == ResultStatus.SUCCESS) {
                        /*
                         * Your supply was blocked. Please recharge your account to resume supply.
                         */
                        if (!isCutOff){
                            //SMSNotification(contract, supplier.getCd(), "Your supply was blocked. Please recharge your account to resume supply.");
                            SMSNotification(contract, supplier.getCd());
                        }
                    }
                        
                } else if (contract.getEmergencyCreditAutoChange() != null && contract.getEmergencyCreditAutoChange()) {
                    // 잔액이 0 이하 이고 emergency credit 이 자동인 경우 고객정보를 emergency credit 모드로 업데이트
                    if(contract.getEmergencyCreditStartTime() == null  || "".equals(contract.getEmergencyCreditStartTime())) {
                        //EmergencyCreditType으로 변경
                        log.info("[DCU:" + mcu.getSysID() + " METER:" + meter.getMdsId() 
                                + "] ContractId["+contract.getId()+"] is change EmergencyCreditType");
                        CodeDao codeDao = DataUtil.getBean(CodeDao.class);
                        Code newCreditType = codeDao.findByCondition("code", Code.EMERGENCY_CREDIT);
                        changeCreditType(contract, "creditType", creditType, newCreditType);
                    } else {
                        //EmergencyDuration을 체크 후 기간이 지났을 경우 EmernencyCredit을 Manual로 변경
                        Boolean isEmergencyCreditContract = checkEmergencyDuration(contract);
                        if(!isEmergencyCreditContract) {
                            //Manual로 변경
                            log.info("[DCU:" + mcu.getSysID() + " METER:" + meter.getMdsId() 
                                    + "] ContractId["+contract.getId()+"] is change PrepayType");
                            CodeDao codeDao = DataUtil.getBean(CodeDao.class);
                            Code newCreditType = codeDao.findByCondition("code", Code.PREPAYMENT);
                            changeCreditType(contract, "creditType", creditType, newCreditType);
                        }
                    }
                }
            }
            // 잔액이 0보다 크고 미터 상태가 cut off 이거나 계약 상태가 임시중단 상태이면 relay on 시도한다.
            else {              
                String suspended = CommonConstants.ContractStatus.SUSPENDED.getCode();
                String pauseCode = CommonConstants.ContractStatus.PAUSE.getCode();
                boolean cutoffnotsuspended = meterStatus != null && meterStatus.getName().equals(MeterStatus.CutOff.name());
                cutoffnotsuspended &= (contractStatus != null && !contractStatus.getCode().equals(suspended));
                
                if (cutoffnotsuspended || (contractStatus != null && contractStatus.getCode().equals(pauseCode))) {
                      isCutOff = true;
                }
                
                log.debug("[DCU:" + mcu.getSysID() + " METER:" + meter.getMdsId() 
                        + "] is 0 < credit and [isCutOff = " + isCutOff + "]");
                
                if (isCutOff) {
                    try {
                        //txstatus = txmanager.getTransaction(null);
                        Map<String, Object> result = relayValveOn(mcu.getSysID(), meter.getMdsId(), meterSaver).getResponse();
                        //txmanager.commit(txstatus);
                        
                        Object[] values = result.values().toArray(new Object[0]);
                        
                        JsonParser jparser = new JsonParser();
                        JsonArray ja = null;
                        for (Object o : values) {
                            ja = jparser.parse((String)o).getAsJsonArray();
                            for (int i = 0; i < 6; i++) {
                                if (ja.get(i).getAsJsonObject().get("name").getAsString().equals("Result") || 
                                        ja.get(i).getAsJsonObject().get("name").getAsString().equalsIgnoreCase("SUCCESS")) {
                                    status = ResultStatus.SUCCESS;
                                    break;
                                }
                                else if (ja.get(i).getAsJsonObject().get("value").getAsString().equals("Can't connect to DCU")) {
                                    mcuStatus = "Can't connect to DCU";
                                    break;
                                }
                                else if (ja.get(i).getAsJsonObject().get("value").getAsString().equals("FAIL")) {
                                    status = ResultStatus.FAIL;
                                    break;
                                }
                                else{
                                    log.warn("[DCU:" + mcu.getSysID() + " METER:" + meter.getMdsId() + "] RelayValveOn Fail - " + ja.get(i).getAsJsonObject().get("value").getAsString());
                                }
                            }
                            if (status == ResultStatus.SUCCESS) break;
                        }
                        
                        log.debug("[DCU:" + mcu.getSysID() + " METER:" + meter.getMdsId() + "] Relay On Status [" + status + "]");
                        
                        // Operation Log에 기록
                        saveOperationLog(supplier, meterType, meter.getMdsId(), "balance-schedule", status.getCode(), status.name(), "relayValveOn");
                    }
                    catch (Exception e) {
                        status = ResultStatus.FAIL;
                        log.error("[DCU:" + mcu.getSysID() + " METER:" + meter.getMdsId() + "] Relay On Status [" + status + "]", e);                        
                    }
                }
            }
            
            log.info("[DCU:" + mcu.getSysID() + " METER:" + meter.getMdsId() + "] End check balance for [Contract :" + contract.getContractNumber() + "]");
            
            return mcuStatus;
        }
        catch (Exception e) {
            if (txstatus != null && !txstatus.isCompleted()) {
                try {
                    txmanager.rollback(txstatus);
                }
                catch (Exception ee) {}
            }
            log.error("[" + contractId + "] Balance Check Exception", e);
        }
        return null;
    }
    
    private Boolean checkEmergencyDuration(Contract contract) {
        Boolean isEmergencyCredit = true;
        
        String emergencyCreditStartTime = contract.getEmergencyCreditStartTime();
        Integer emergencyCreditDuration = contract.getEmergencyCreditMaxDuration();
        log.info("Contract[" + contract.getContractNumber() + "] emergencyType contractId["+contract.getId()+"], startTime[" + emergencyCreditStartTime+"], duration["+emergencyCreditDuration+"]");

        try {
            long afterDurationDate = emergencyCreditStartTime == null ? 0 : Long.parseLong(TimeUtil.getAddedDay(emergencyCreditStartTime, emergencyCreditDuration));
            long today = Long.parseLong(TimeUtil.getCurrentTime());
            if(afterDurationDate < today) {
                isEmergencyCredit = false;
            } else {
                isEmergencyCredit = true;
            }
        } catch (Exception e) {
            log.error(e,e);
        }
        return isEmergencyCredit;
    }

    /**
     * method name : changeCreditType
     * method Desc : ContractChangeLog 에 데이터 insert
     *
     * @param contract
     * @param field
     * @param beforeValue
     * @param afterValue
     */
    private void changeCreditType(Contract contract, String field, Code oldCreditType, Code newCreditType) {
        TransactionStatus txstatus = null;
        try {
            txstatus = txmanager.getTransaction(null);
            log.info("[Contract[" + contract.getContractNumber() + "] newCreditType[" + newCreditType.getName() + "], oldCreditType[" + oldCreditType.getName() + "]");
            // ContractChangeLog Insert
            
            ContractDao contractDao = DataUtil.getBean(ContractDao.class);
            Contract emergencyContract = contract;
            emergencyContract.setCreditType(newCreditType);
            
            if(oldCreditType != null && Code.EMERGENCY_CREDIT.equals(oldCreditType.getCode()) 
                    && !Code.EMERGENCY_CREDIT.equals(newCreditType.getCode())) {
                contract.setEmergencyCreditAutoChange(null);
                contract.setEmergencyCreditMaxDuration(null);
                contract.setEmergencyCreditStartTime(null);
            } else {
                emergencyContract.setEmergencyCreditStartTime(TimeUtil.getCurrentTime());
            }
            
            contractDao.update(emergencyContract);
            ContractChangeLogDao contractChangeLogDao = DataUtil.getBean(ContractChangeLogDao.class);
            ContractChangeLog contractChangeLog = new ContractChangeLog();
    
            contractChangeLog.setContract(contract);
            contractChangeLog.setCustomer(contract.getCustomer());
            contractChangeLog.setStartDatetime(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
            contractChangeLog.setChangeField(field);
    
            if (oldCreditType == null) {
                contractChangeLog.setBeforeValue(null);
            } else {
                contractChangeLog.setBeforeValue(StringUtil.nullToBlank(oldCreditType));
            }
    
            if (newCreditType == null) {
                contractChangeLog.setAfterValue(null);
            } else {
                contractChangeLog.setAfterValue(StringUtil.nullToBlank(newCreditType));
            }
    
    //        contractChangeLog.setOperator(operator);
            contractChangeLog.setWriteDatetime(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
    //        contractChangeLog.setDescr(descr);
    
            contractChangeLogDao.add(contractChangeLog);
        }
        catch (Exception e) {
            log.warn("Change Credit Type Exception - Contract[" + contract.getContractNumber() + "]", e);
        }
        finally {
            if (txstatus != null) {
                try {
                    txmanager.commit(txstatus);
                }
                catch (Exception te) {}
            }
        }
    }

    private void saveOperationLog(Supplier supplier, Code targetTypeCode, String targetName, String userId, Integer status, String errorReason, String command){
        TransactionStatus txstatus = null;
        try {
            txstatus = txmanager.getTransaction(null);
            OperationLogDao operationLogDao = DataUtil.getBean(OperationLogDao.class);
            CodeDao codeDao = DataUtil.getBean(CodeDao.class);
            
            Code operationCode = null;
            if(command.equals("relayValveOff")){
            	operationCode = codeDao.getCodeIdByCodeObject("8.1.10"); // 8.1.10 Relay Off
            }else if(command.equals("relayValveOn")){
            	operationCode = codeDao.getCodeIdByCodeObject("8.1.9");  // 8.1.9 Relay On
            }else{
            	operationCode = codeDao.getCodeIdByCodeObject("8.1.4");  // 8.1.4 Relay Switch
            }
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            Calendar today = Calendar.getInstance();
            String currDateTime = sdf.format(today.getTime());
    
            OperationLog opLog = new OperationLog();
    
            opLog.setOperatorType(1);//operator
            opLog.setOperationCommandCode(operationCode);
            opLog.setYyyymmdd(currDateTime.substring(0,8));
            opLog.setHhmmss(currDateTime.substring(8,14));
            opLog.setYyyymmddhhmmss(currDateTime);
            opLog.setDescription("");
            opLog.setErrorReason(errorReason);
            opLog.setResultSrc("");
            opLog.setStatus(status);
            opLog.setTargetName(targetName);
            opLog.setTargetTypeCode(targetTypeCode);
            opLog.setUserId(userId);
            opLog.setSupplier(supplier);
            operationLogDao.add(opLog);
            
            log.debug("[Save OperationLog complete METER:" + targetName + "] ==> " + opLog.toString());
        }
        catch (Exception e) {
            log.warn("[" + targetName + "] Save Operation Exception", e);
        }
        finally {
            if (txstatus != null) {
                try {
                    txmanager.commit(txstatus);
                }
                catch (Exception te) {}
            }
        }
    }
    
    /**
     * method name : SMSNotification
     * method Desc : Charge에 대한 SMS 통보
     * @param contract, message
     */
    //private void SMSNotification(Contract contract, DecimalPattern cdFormat, String message) {
    private void SMSNotification(Contract contract, DecimalPattern cdFormat) {
        log.debug("["+ contract.getContractNumber() +"] Try to Send SMS Notification...");
        TransactionStatus txstatus = null;
        try {
            txstatus = txmanager.getTransaction(null);
            CodeDao codeDao = DataUtil.getBean(CodeDao.class);
            ContractDao contractDao = DataUtil.getBean(ContractDao.class);
            SupplierDao supplierDao = DataUtil.getBean(SupplierDao.class);
            Supplier supplier = supplierDao.get(contract.getSupplierId());
            
            Map<String, Object> condition = new HashMap<String, Object>();
            condition.put("prepayCreditId", codeDao.getCodeIdByCode("2.2.1"));
            condition.put("emergencyICreditId", codeDao.getCodeIdByCode("2.2.2"));
            condition.put("smsYn", true);
            condition.put("contractId", contract.getId());
            List<Map<String, Object>> contractInfo = contractDao.getContractSMSYN(condition);
            
            if(contractInfo.size() > 0) {
                log.info("MOBILE_NO[" + contractInfo.get(0).get("MOBILENO") + "]");
                String mobileNo = contractInfo.get(0).get("MOBILENO").toString().replace("-", "");
                String currentCredit = contractInfo.get(0).get("CURRENTCREDIT") == null ? "0" : contractInfo.get(0).get("CURRENTCREDIT").toString();
                
                DecimalFormat cdf = DecimalUtil.getDecimalFormat(cdFormat);
                String text = null;
//                text =  message
//                        + "\n Customer Name : " + contractInfo.get(0).get("CUSTOMERNAME")
//                        + "\n Supply Type : " + contractInfo.get(0).get("SERVICETYPE")
//                        + "\n Current Credit : " +  cdf.format(Double.parseDouble(currentCredit)).toString();
                
                text = getMessageProp(supplier).getProperty("aimir.sms.meter.cutoff.msg.meterId").replace("$METERID", contractInfo.get(0).get("METERID").toString())
                        + "\n " + getMessageProp(supplier).getProperty("aimir.sms.customer.name") + " : " +  contractInfo.get(0).get("CUSTOMERNAME")
                        + "\n " + getMessageProp(supplier).getProperty("aimir.sms.supplier.type") + " : " + contractInfo.get(0).get("SERVICETYPE")
                        + "\n " + getMessageProp(supplier).getProperty("aimir.sms.credit.current") + " : " +  cdf.format(Double.parseDouble(currentCredit)).toString();      
            
//                Properties prop = new Properties();
//                prop.load(getClass().getClassLoader().getResourceAsStream("config/fmp.properties"));
                
                Properties prop = FMPProperty.getProperties();
                String smsClassPath = prop.getProperty("smsClassPath");
                SendSMS obj = (SendSMS) Class.forName(smsClassPath).newInstance();
                
                Method m = obj.getClass().getDeclaredMethod("send", String.class, String.class, Properties.class);
                String messageId = (String) m.invoke(obj, mobileNo, text, prop);
                
                if(!"".equals(messageId)) {
                	log.info("contractId [ "+ contract.getId() +"],	SMS messageId [" + messageId + "]");
//                    contract.setSmsNumber(messageId);
//                    contractDao.updateSmsNumber(contract.getId(), messageId);
                } 
            }
            txmanager.commit(txstatus);
        } catch (Exception e) {         
            log.warn("[" + contract.getContractNumber() +  "] Send SMS Notification Exception",e);
            
            if (txstatus != null) {
                try {
                    txmanager.rollback(txstatus);
                }
                catch (Exception te) {}
            }
        }
    }
    
    public ResponseMap relayValveOn(String mcuId, String meterId, String saverName) throws Exception
    {
        Class clazz = Class.forName(saverName);
        AbstractMDSaver saver = (AbstractMDSaver)DataUtil.getBean(clazz);
        
        ResponseMap map = new ResponseMap();
        Map<String, String> response = new HashMap<String, String>();
        response.put("Response", saver.relayValveOn(mcuId, meterId));
        map.setResponse(response);
        return map;
    }

    public ResponseMap relayValveOff(String mcuId, String meterId, String meterSaver) throws Exception 
    {
        Class clazz = Class.forName(meterSaver);
        AbstractMDSaver saver = (AbstractMDSaver)DataUtil.getBean(clazz);
        
        ResponseMap map = new ResponseMap();
        Map<String, String> response = new HashMap<String, String>();
        response.put("Response", saver.relayValveOff(mcuId, meterId));
        map.setResponse(response);
        return map;
    }
}