package com.aimir.fep.bypass.actions;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.session.IoSession;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.MeterStatus;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.fep.protocol.fmp.client.Client;
import com.aimir.fep.protocol.fmp.datatype.SMIValue;
import com.aimir.fep.protocol.fmp.datatype.UINT;
import com.aimir.fep.protocol.fmp.frame.GeneralDataConstants;
import com.aimir.fep.protocol.fmp.frame.ServiceDataConstants;
import com.aimir.fep.protocol.fmp.frame.ServiceDataFrame;
import com.aimir.fep.protocol.fmp.frame.service.CommandData;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FrameUtil;
import com.aimir.fep.util.MIBUtil;
import com.aimir.model.device.AsyncCommandParam;
import com.aimir.model.device.Meter;
import com.aimir.model.system.Code;
import com.aimir.model.system.Contract;

public abstract class CommandAction {
    private static Log log = LogFactory.getLog(CommandAction.class);
    
    /**
     * IF4 1.2 명령 실행. NameSpace로 구분된 인스턴스로 응답을 처리한다.
     * @param session
     * @param cd
     */
    public void execute(IoSession session, CommandData cd) throws Exception {
        String ns = (String)session.getAttribute("nameSpace");
        MIBUtil mu = MIBUtil.getInstance(ns);
        
        String cmd = mu.getName(cd.getCmd().getValue());
        log.debug("TID[" + cd.getTid() + "]");
        log.debug("OID[" + cd.getCmd().getValue() + "]");
        log.debug("CMD[" + cmd + "]");
        for (SMIValue s : cd.getSMIValue()) {
            log.debug("ARG[" + s.getVariable().toString() + "]");
        }
        if (cd.getErrCode().getValue() != 0x00) {
            log.debug("### Receive Error Code ==> ERROR[" + cd.getErrCode().getValue() + "]");
        }
        else
        {
            execute(cmd, cd.getSMIValue(), session);
        }
    }
    
    protected void setMeterStatus(String meterId, int status) {
        JpaTransactionManager txmanager = DataUtil.getBean(JpaTransactionManager.class);
        MeterDao meterDao = DataUtil.getBean(MeterDao.class);
        
        TransactionStatus txstatus = null;
        try {
            txstatus = txmanager.getTransaction(null);
            Meter meter = meterDao.get(meterId);
            
            if (meter == null) {
                log.warn("METER[" + meterId + "] is not existed!");
                return;
            }
            log.debug("METER[" + meterId + "] STATUS[" + status + "]");
            //if (status == 0x01 || status == 0x08) {
            if (status == 1 || status == 8) {
                meter.setMeterStatus(CommonConstants.getMeterStatusByName(MeterStatus.CutOff.name()));
                // meterDao.update(meter);
                
                Contract contract = meter.getContract();
                if(contract != null && contract.getStatus().getCode().equals(CommonConstants.ContractStatus.NORMAL.getCode())) {
                    CodeDao codeDao = DataUtil.getBean(CodeDao.class);
                    Code pauseCode = codeDao.getCodeIdByCodeObject(CommonConstants.ContractStatus.PAUSE.getCode());
                    contract.setStatus(pauseCode);
                    //ContractDao contractDao = DataUtil.getBean(ContractDao.class);
                    //contractDao.updateStatus(contract.getId(), pauseCode);
                }
            }
            else {
                meter.setMeterStatus(CommonConstants.getMeterStatusByName(MeterStatus.Normal.name()));
                // meterDao.update(meter);
                
                Contract contract = meter.getContract();
                if(contract != null && contract.getStatus().getCode().equals(CommonConstants.ContractStatus.PAUSE.getCode())) {
                    CodeDao codeDao = DataUtil.getBean(CodeDao.class);
                    Code pauseCode = codeDao.getCodeIdByCodeObject(CommonConstants.ContractStatus.NORMAL.getCode());
                    contract.setStatus(pauseCode);
                    //ContractDao contractDao = DataUtil.getBean(ContractDao.class);
                    //contractDao.updateStatus(contract.getId(), pauseCode);
                }
            }
            txmanager.commit(txstatus);
        }
        catch (Exception e) {
            log.error(e, e);
            if (txstatus != null) txmanager.rollback(txstatus);
        }
    }
    
    private CommandData command(String ns, String cmdName, List<?> params){
        MIBUtil mu = MIBUtil.getInstance(ns);
        CommandData command = new CommandData();
        command.setCmd(mu.getMIBNodeByName(cmdName).getOid());
        if (params != null) {
            for (int i = 0; i < params.size(); i++) {
                Object obj = params.get(i);
                if (obj instanceof SMIValue) {
                    command.append((SMIValue) obj);
                } else if(obj instanceof AsyncCommandParam){
                    AsyncCommandParam param = (AsyncCommandParam) obj;
                    SMIValue smiValue;
                    try {
                        smiValue = DataUtil.getSMIValueByOid(ns, param.getParamType(), param.getParamValue());
                        command.append(smiValue);
                    } catch (Exception e) {
                        log.error(e,e);
                    }

                }
            }
        }
        return command;
    }
    
    
    public ServiceDataFrame makeSendCommand(String nameSpace, String cmdName, List<?> params){
    	
        ServiceDataFrame frame = new ServiceDataFrame();
        CommandData command = null;
        try {
            command = command(nameSpace, cmdName, params);
            command.setAttr(ServiceDataConstants.C_ATTR_REQUEST);
            command.setTid(FrameUtil.getCommandTid());           

            long mcuId = 0L;
            frame.setMcuId(new UINT(mcuId));
            frame.setSvc(GeneralDataConstants.SVC_C);
            frame.setAttr((byte)(GeneralDataConstants.ATTR_START | GeneralDataConstants.ATTR_END));
            frame.setSvcBody(command.encode());
        }
        catch(Exception ex)
        {
            log.error(ex, ex);
            if(!command.getCmd().toString().equals("198.3.0"))
            {
                log.error("sendCommand failed : command["+command+"]",ex);
            } else {
                log.error("sendCommand failed : command["+command.getCmd()
                        +"]",ex);
            }
        }

        return frame;    
            
    }
    
    public void sendCommand(IoSession session, String cmdName, List<?> params)
            throws Exception
    {
        CommandData command = null;
        try {
            String ns = (String)session.getAttribute("nameSpace");
            command = command(ns, cmdName, params);
            command.setAttr(ServiceDataConstants.C_ATTR_REQUEST);
            command.setTid(FrameUtil.getCommandTid());
            
            ServiceDataFrame frame = new ServiceDataFrame();
            long mcuId = 0L;
            frame.setMcuId(new UINT(mcuId));
            frame.setSvc(GeneralDataConstants.SVC_C);
            frame.setAttr((byte)(GeneralDataConstants.ATTR_START | GeneralDataConstants.ATTR_END));
            frame.setSvcBody(command.encode());
            session.write(frame);
            
            log.debug("#### [" + cmdName + "] SEND_COMMAND = " + command.toString()); 
        }
        catch(Exception ex)
        {
            log.error(ex, ex);
            if(!command.getCmd().toString().equals("198.3.0"))
            {
                log.error("sendCommand failed : command["+command+"]",ex);
            } else {
                log.error("sendCommand failed : command["+command.getCmd()
                        +"]",ex);
            }
            throw ex;
        }
    }
    public abstract void execute(String cmd, SMIValue[] smiValues, IoSession session) throws Exception;
    
    /**
     * 결과를 받아오기 위한 것이다.
     * @param frame
     * @param session
     * @throws Exception
     */
    public abstract void executeBypass(byte[] frame, IoSession session) throws Exception;
    
    /**
     * MOE에서 Bypass을 통해 결과를 받아오기 위한 것이다.
     * @param frame
     * @param session
     * @throws Exception
     */
    public abstract CommandData executeBypassClient(byte[] frame, IoSession session) throws Exception;
    
    /**
     * 공통실행
     * execute
     * @param frame
     * @param session
     * @throws Exception
     */
    public abstract CommandData execute(HashMap<String, String> params, IoSession session, Client client) throws Exception;
}
