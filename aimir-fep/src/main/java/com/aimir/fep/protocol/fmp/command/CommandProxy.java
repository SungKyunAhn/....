package com.aimir.fep.protocol.fmp.command;

import java.io.File;
import java.util.HashMap;
import java.util.Vector;

import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.aimir.constants.CommonConstants.McuType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.fep.bypass.actions.CommandAction;
import com.aimir.fep.meter.parser.plc.BDataRequest;
import com.aimir.fep.meter.parser.plc.NDataRequest;
import com.aimir.fep.meter.parser.plc.PLCDataConstants;
import com.aimir.fep.meter.parser.plc.PLCDataFrame;
import com.aimir.fep.protocol.fmp.client.Client;
import com.aimir.fep.protocol.fmp.client.ClientFactory;
import com.aimir.fep.protocol.fmp.client.bypass.BYPASSClient;
import com.aimir.fep.protocol.fmp.client.plc.PLCClient;
import com.aimir.fep.protocol.fmp.common.BypassTarget;
import com.aimir.fep.protocol.fmp.common.CDMATarget;
import com.aimir.fep.protocol.fmp.common.GPRSTarget;
import com.aimir.fep.protocol.fmp.common.GSMTarget;
import com.aimir.fep.protocol.fmp.common.LANTarget;
import com.aimir.fep.protocol.fmp.common.PLCTarget;
import com.aimir.fep.protocol.fmp.common.SMSTarget;
import com.aimir.fep.protocol.fmp.common.Target;
import com.aimir.fep.protocol.fmp.datatype.OID;
import com.aimir.fep.protocol.fmp.datatype.SMIValue;
import com.aimir.fep.protocol.fmp.frame.ErrorCode;
import com.aimir.fep.protocol.fmp.frame.service.CommandData;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.FileUtil;
import com.aimir.fep.util.MIBUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.session.IoSession;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Command Proxy MBean which processing event from MCU
 *
 * @author Y.S Kim
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class CommandProxy implements CommandProxyMBean,
       MBeanRegistration
{
    private static Log log = LogFactory.getLog(CommandProxy.class);
    private ObjectName objectName = null;
    private static final int MAX_COUNT = 
        Integer.parseInt(FMPProperty.getProperty("client.max.count"));
    private static Object lock = new Object();
    private static int connCount = 0;
    private static int waitCount = 0;
    private Client client = null;

    public CommandProxy() {
    }

    /**
     * method of interface MBeanRegistration
     *
     * @param server <code>MBeanServer</code> MBeanServer
     * @param name <code>ObjectName</code> MBean Object Name
     * @throws java.lang.Exception
     */
    public ObjectName preRegister(MBeanServer server,
            ObjectName name) throws java.lang.Exception
    {
        if (name == null)
        {
            name = new ObjectName(server.getDefaultDomain()
                    + ":service=" + this.getClass().getName());
        }

        this.objectName = name;

        return name;
    }

    /**
     * method of interface MBeanRegistration
     *
     * @param registrationDone <code>Boolean</code>
     * @throws java.lang.Exception
     */
    public void postRegister(Boolean registrationDone) { }
    /**
     * method of interface MBeanRegistration
     *
     * @throws java.lang.Exception
     */
    public void preDeregister() throws java.lang.Exception { }
    /**
     * method of interface MBeanRegistration
     *
     * @throws java.lang.Exception
     */
    public void postDeregister() { }

    /**
     * start CommandProxy
     *
     * @throws java.lang.Exception
     */
    public void start() throws Exception
    {
        log.debug(this.objectName + " start");
    }

    /**
     * get Client
     *
     * @param target <code>Hashtable</code> MCU Target Information
     * @return client <code>Client</code> Client interface
     */
    private Client getClient(Target target)
        throws Exception
    {
        /*
         * Code 1 : CDMA
         * Code 2 : GSM
         * Code 3 : GPRS
         * Code 4 : PSTN
         * Code 5 : LAN
         */
        String targetId = target.getTargetId();
        McuType targetType = target.getTargetType();
        String ipAddr = target.getIpAddr();
        String ipv6Addr = target.getIpv6Addr();
        int listenPort = target.getPort();
        String sysPhoneNumber = target.getPhoneNumber();
        String groupNumber = target.getGroupNumber();
        String memberNumber = target.getMemberNumber();
        int targetMeterModel = target.getMeterModel();
        String supplierId = target.getSupplierId();
        String receiverType = target.getReceiverType();
        String receiverId = target.getReceiverId();
        String nameSpace = target.getNameSpace();
        
        switch (target.getProtocol()) {
        	case REVERSEGPRS:
        		target = new GPRSTarget(ipAddr, listenPort);
        		target.setProtocol(Protocol.REVERSEGPRS);
                break;
        	case IP :
            case GPRS :
                target = new GPRSTarget(ipAddr, listenPort);
                break;
            case GSM :
                target = new GSMTarget(sysPhoneNumber,ipAddr,groupNumber,memberNumber);
                break;
            case PLC :
                target = new PLCTarget(ipAddr, listenPort);
                break;
            case LAN :
                target = new LANTarget(ipAddr, listenPort);
                break;
            case CDMA :
                target = new CDMATarget(sysPhoneNumber,ipAddr,groupNumber,memberNumber);
            case PSTN :
                log.error("PSTN Protocol is not supported");
                throw new Exception("PSTN Protocol is not supported " + target.getProtocol());
            case SMS :
                target = new SMSTarget(sysPhoneNumber,ipAddr,groupNumber,memberNumber);
                break;
            case BYPASS :
            	target = new BypassTarget(ipAddr, target.getPort(), Protocol.BYPASS);
            	break;
            default :
                log.error("Unknown Protocol Type :" + target.getProtocol());
                throw new Exception("Unknown Communication Type" + target.getProtocol());
        }
        target.setTargetId(targetId);
        target.setTargetType(targetType);        
        target.setMeterModel(targetMeterModel);
        target.setSupplierId(supplierId);
        target.setReceiverType(receiverType);
        target.setReceiverId(receiverId);
        target.setNameSpace(nameSpace);
        
        int waitNum = 0;
        // 2010.03.31
        lock(waitNum);

        client = ClientFactory.getClient(target);
        // client.connect();
        return client;
    }

    private static void lock(int waitNum) throws InterruptedException {
        synchronized(lock) {
            if (connCount == MAX_COUNT) {
                waitNum = waitCount++;
                while (connCount == MAX_COUNT) {
                    log.debug("Waiting number[" + waitNum + "], wait count[" + waitCount + "]");
                    lock.wait();
                }
            }
            connCount++;
            log.debug("Connecting number[" + waitNum + "], wait count[" + waitCount + "], conn count[" + connCount + "]");
        }
    }

    private static void release() {
        synchronized(lock) {
            log.debug("Release connection count[" + connCount + "]");
            if (waitCount > 0 || connCount > 0) {
                lock.notify();
                if (waitCount > 0)
                    waitCount--;
                if (connCount > 0)
                    connCount--;
            }
        }
    }

    /**
     * Execute Command
     *
     * @param target <code>Hashtable</code> MCU Target Information
     * @param cmdName <code>String</code> Command Name
     * @param attrs <code>Collection</code> Command Parameters
     * @param values <code>Collection</code> Command Values
     * @return name <code>SMIValues</code> SMIValues
     */
    public Object execute(Target target, String cmdName,
            Vector attrs, Vector values) throws Exception
    {
        CommandData cd = null;
        try {
            String ns = target.getNameSpace();
            MIBUtil mu = MIBUtil.getInstance(ns);

            getClient(target);

            CommandData command = new CommandData();
            log.debug("command name="+cmdName);
            log.debug("command oid="+mu.getOid(cmdName));
    		command.setCmd(mu.getMIBNodeByName(cmdName).getOid());
        	

            if (attrs != null)
            {
                SMIValue smiValue = null;

                boolean flag = false;
                if (values != null && values.size() == attrs.size())
                {
                    flag = true;
                }

                for (int i = 0; i < attrs.size(); i++)
                {
                    try
                    {
                        if (flag)
                        {
                            smiValue =
                                DataUtil.getSMIValueByObject(ns, (String)attrs.elementAt(i),
                                    values.elementAt(i));
                        }
                        else
                        {
                            smiValue =
                                DataUtil.getSMIValueByObject(ns, (String)attrs.elementAt(i), null);
                        }
                    }
                    catch (Exception e)
                    {
                        smiValue = null;
                        log.error("Convert Error [attr="+attrs.elementAt(i)+
                                ",value="+values.elementAt(i)+"]");
                        log.error("Exception:"+e);
                    }

                    if (smiValue != null)
                    {
                        command.append(smiValue);
                    }
                }
            }
            cd = client.sendCommand(command);
        }
        finally {
            release();
        }

        /**
         * if GSM Curcuit is possible,
         * when GPRS Communication is failed,
         * try GSM Curcuit communication
        CommandData cd = null;
        try { cd = client.sendCommand(command);
        }catch(java.net.ConnectException cex)
        {
            String protocolType = (String) target.get("protocolType");
            if(protocolType == null || !protocolType.equals("3"))
                throw ex;
            target.set("ProtocolType","2");
            client = getClient(target);
            cd = client.sendCommand(command);
        }
        */

        if (cd == null) {
            log.debug("Return Command Data is NULL");
            return new Integer(ErrorCode.IF4ERR_RETURN_DATA_EMPTY);
        }
        else if (cd.getErrCode().getValue() != 0)
        {
            log.debug("ErrorCode="+cd.getErrCode().getValue());
            return new Integer(cd.getErrCode().getValue());
        }
        else
        {
            log.debug("No Error");
            return cd.getSMIValue();
        }
    }

    /**
     * Execute Command
     *
     * @param target <code>Hashtable</code> MCU Target Information
     * @param cmdName <code>String</code> Command Name
     * @param attrs <code>Collection</code> SMIValues
     * @param values <code>Collection</code> Command Values
     * @return name <code>SMIValues</code> SMIValues
     */
    public Object execute(Target target, String cmdName, Vector datas)
    throws Exception
    {
        CommandData cd = null;
        CommandData command = null;
        try {
            MIBUtil mu = null;
            if(target.getNameSpace() != null && !"".equals(target.getNameSpace())){
            	mu = MIBUtil.getInstance(target.getNameSpace());
            }else{
            	mu = MIBUtil.getInstance();
            }

            getClient(target);

            command = new CommandData();

            log.debug("command NameSpace="+target.getNameSpace());
            log.debug("command name="+cmdName);
            
            try{
                log.debug("command oid="+mu.getOid(cmdName));
        		command.setCmd(mu.getMIBNodeByName(cmdName).getOid());
            }catch(Exception e){
            	log.info(String.format("'%s' Command is not defined in the OID List(command.mib). set the default '0.0'",cmdName));
            	OID oid = new OID();
            	oid.setValue("0.0");
            	command.setCmd(oid);
            }
            log.debug("smiValues datas="+datas);

            if (datas != null)
            {
                for (int i = 0; i < datas.size(); i++)
                {
                	Object obj = datas.elementAt(i);
                	if(obj instanceof SMIValue){
                		command.append((SMIValue) obj);
                	}else{
                		command.append(obj);
                	}
                }
            }
            cd = client.sendCommand(command);
        }
        finally {
            release();
        }

        if (cd != null) {
        	
        	// kskim. 2012-01-13
        	if(cd.getResponse()!=null){
        		return cd.getResponse();
        	}
        	
            if (cd.getErrCode().getValue() != 0)
            {
                log.debug("ErrorCode="+cd.getErrCode().getValue());
                return new Integer(cd.getErrCode().getValue());
            }
            else
            {
                log.debug("No Error");
                return cd.getSMIValue();
            }
        }
        else {
            log.warn("command return data is null for [" + command + "]");
            return new Integer(ErrorCode.IF4ERR_RETURN_DATA_EMPTY);
        }
    }

    public static void main(String args[]) {
        try {
            Target target = new Target();
            target.setProtocol(Protocol.LAN);
            target.setTargetType(McuType.DCU);
            target.setIpAddr("187.1.5.33");
            target.setPort(9000);
            target.setTargetId("4192");
            // target.put("locId", "");
            // target.put("swVersion", "");
            // target.put("meterModel", "");
            ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"/config/spring.xml"});
            CommandProxy proxy = new CommandProxy();

            PLCDataFrame bDataReq = new BDataRequest(PLCDataConstants.SOF, PLCDataConstants.PROTOCOL_DIRECTION_FEP_IRM,PLCDataConstants.PROTOCOL_VERSION_1_0,new String("DCU"), new String("SERVER"), PLCDataConstants.BDATAREQUEST_LENGTH_VALUE);
            proxy.execute(target, bDataReq);
            //CDataRequest cDataReq = new CDataRequest(PLCDataConstants.SOF, PLCDataConstants.PROTOCOL_DIRECTION_FEP_IRM,PLCDataConstants.PROTOCOL_VERSION_1_0,new OCTET("DCU"), new OCTET("SERVER"), PLCDataConstants.CDATAREQUEST_LENGTH_VALUE,"187.1.5.80/0:0",9000,"20050811130501");
            //proxy.execute(target, cDataReq);
            //DDataRequest dDataReq = new DDataRequest(PLCDataConstants.SOF, PLCDataConstants.PROTOCOL_DIRECTION_FEP_IRM, PLCDataConstants.PROTOCOL_VERSION_1_1, new OCTET("DCU"), new OCTET("SERVER"), PLCDataConstants.DDATAREQUEST_LENGTH_VALUE,PLCDataConstants.DTYPE_METERING_ALL,"");
            //proxy.execute(target, dDataReq);
            //EDataRequest eDataReq = new EDataRequest(PLCDataConstants.SOF, PLCDataConstants.PROTOCOL_DIRECTION_FEP_IRM, PLCDataConstants.PROTOCOL_VERSION_1_1, new OCTET("DCU"), new OCTET("SERVER"), PLCDataConstants.EDATAREQUEST_LENGTH_VALUE,PLCDataConstants.DTYPE_DEMAND_ALL,"");
            //proxy.execute(target, eDataReq);
            //FDataRequest fDataReq = new FDataRequest(PLCDataConstants.SOF, PLCDataConstants.PROTOCOL_DIRECTION_FEP_IRM, PLCDataConstants.PROTOCOL_VERSION_1_1, new OCTET("DCU"), new OCTET("SERVER"), PLCDataConstants.FDATAREQUEST_LENGTH_VALUE,PLCDataConstants.DTYPE_LP_ALL,"");
            //proxy.execute(target, fDataReq);
            //GDataRequest gDataReq = new GDataRequest(PLCDataConstants.SOF, PLCDataConstants.PROTOCOL_DIRECTION_FEP_IRM, PLCDataConstants.PROTOCOL_VERSION_1_1, new OCTET("DCU"), new OCTET("SERVER"), PLCDataConstants.GDATAREQUEST_LENGTH_VALUE,"");
            //proxy.execute(target, gDataReq);
            //HDataRequest hDataReq = new HDataRequest(PLCDataConstants.SOF, PLCDataConstants.PROTOCOL_DIRECTION_FEP_IRM, PLCDataConstants.PROTOCOL_VERSION_1_1, new OCTET("DCU"), new OCTET("SERVER"), PLCDataConstants.HDATAREQUEST_LENGTH_VALUE,"");
            //proxy.execute(target, hDataReq);

            //IDataRequest iDataReq = new IDataRequest(PLCDataConstants.SOF, PLCDataConstants.PROTOCOL_DIRECTION_FEP_IRM, PLCDataConstants.PROTOCOL_VERSION_1_1, new OCTET("DCU"), new OCTET("SERVER"), PLCDataConstants.IDATAREQUEST_LENGTH_VALUE,"");
            //proxy.execute(target, iDataReq);
            //JDataRequest jDataReq = new JDataRequest(PLCDataConstants.SOF, PLCDataConstants.PROTOCOL_DIRECTION_FEP_IRM, PLCDataConstants.PROTOCOL_VERSION_1_1, new OCTET("DCU"), new OCTET("SERVER"), PLCDataConstants.JDATAREQUEST_LENGTH_VALUE);
            //proxy.execute(target, jDataReq);
            //MDataRequest mDataReq = new MDataRequest(PLCDataConstants.SOF, PLCDataConstants.PROTOCOL_DIRECTION_FEP_IRM, PLCDataConstants.PROTOCOL_VERSION_1_1, new OCTET("DCU"), new OCTET("SERVER"), PLCDataConstants.MDATAREQUEST_LENGTH_VALUE);
            //proxy.execute(target, mDataReq);
            NDataRequest nDataReq = new NDataRequest(PLCDataConstants.SOF, PLCDataConstants.PROTOCOL_DIRECTION_FEP_IRM, PLCDataConstants.PROTOCOL_VERSION_1_1, new String("DCU"), new String("SERVER"), PLCDataConstants.NDATAREQUEST_LENGTH_VALUE,"TID",150,100,50,100,1,120);
            proxy.execute(target, nDataReq);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Execute PLC Command
     * @param target
     * @param pdf PLC Requeset Data Frame
     * @return Success : Return PLC Data Frame, Error : Return errCode , null response : Return 999
     * @throws Exception
     */
    public Object execute(Target target, PLCDataFrame pdf)
    throws Exception
    {
        PLCDataFrame responsePdf = null;
        try {
            getClient(target);
            responsePdf = ((PLCClient)client).sendCommand(pdf);
        }
        finally {
            release();
        }

        if (responsePdf != null) {
            log.debug("Response PDF: "+responsePdf);
            if (responsePdf.getErrCode() != 0)
            {
                log.debug("ErrorCode="+responsePdf.getErrCode());
                return new Integer(responsePdf.getErrCode());
            }
            else
            {
                return responsePdf;
            }
        }
        else {
            log.warn("PLCDataFrame data is null for [" + DataUtil.getPLCCommandStr(pdf.getCommand()) + "]");
            return new Integer(999);
        }
    }
    
    /**
     * Execute Firmware Update Command
     *
     * @param target <code>Hashtable</code> MCU Target Information
     * @param filename <code>String</code> SMIValues
     * @param installType <code>Integer</code> Command Values
     * @param rsvTime <code>String</code> Command Values
     * @return object <code>SMIValues</code> SMIValues
     */
    public Object executeFirmwareUpdate(Target target, String filename,
            Integer installType, String rsvTime) throws Exception
    {
        CommandData cd = null;
        try {
            MIBUtil mu = MIBUtil.getInstance();

            getClient(target);

            log.debug("install file["+filename+"]");

            File f = FileUtil.getFirmwareFile(filename);
            String fullfname = f.getPath();
            log.debug("file name :"+fullfname);

            CommandData command = DataUtil.getFirmwareCommandData(fullfname,
                    installType.intValue(), rsvTime);

            cd = client.sendCommand(command);
        }
        finally {
            release();
        }

        if (cd == null) {
            log.debug("Return Command Data is NULL");
            return new Integer(ErrorCode.IF4ERR_RETURN_DATA_EMPTY);
        }
        else if (cd.getErrCode().getValue() != 0)
        {
            log.debug("ErrorCode="+cd.getErrCode().getValue());
            return new Integer(cd.getErrCode().getValue());
        }
        else
        {
            log.debug("No Error");
            return cd.getSMIValue();
        }
    }

    /**
     * Execute Firmware Update Notification Command
     *
     * @param target <code>Hashtable</code> MCU Target Information
     * @return object <code>SMIValues</code> SMIValues
     */
    public Object notifyFirmwareUpdate(Target target, String serverIp) throws Exception
    {
        CommandData cd = null;
        try {
            getClient(target);

            CommandData command = DataUtil.getFirmwareCommand(serverIp);
            cd = client.sendCommand(command);
        }
        finally {
            release();
        }

        if (cd == null) {
            log.debug("Return Command Data is NULL");
            return new Integer(ErrorCode.IF4ERR_RETURN_DATA_EMPTY);
        }
        else if (cd.getErrCode().getValue() != 0)
        {
            log.debug("ErrorCode="+cd.getErrCode().getValue());
            return new Integer(cd.getErrCode().getValue());
        }
        else
        {
            log.debug("No Error");
            return cd.getSMIValue();
        }
    }

    /**
     * Execute Upload File Command
     *
     * @param target <code>Hashtable</code> MCU Target Information
     * @param filename <code>String</code> SMIValues
     * @return object <code>SMIValues</code> SMIValues
     */
    public Object executePutFile(Target target, String filename)
        throws Exception
    {
        CommandData cd = null;
        try {
            MIBUtil mu = MIBUtil.getInstance();

            getClient(target);

            log.debug("put file["+filename+"]");

            File f = FileUtil.getUploadFile(filename);
            String fullfname = f.getPath();
            log.debug("file name :"+fullfname);

            CommandData command = DataUtil.getPutFileCommandData(fullfname,
                    filename);
            cd = client.sendCommand(command);
        }
        finally {
            release();
        }

        if (cd == null) {
            log.debug("Return Command Data is NULL");
            return new Integer(ErrorCode.IF4ERR_RETURN_DATA_EMPTY);
        }
        else if (cd.getErrCode().getValue() != 0)
        {
            log.debug("ErrorCode="+cd.getErrCode().getValue());
            return new Integer(cd.getErrCode().getValue());
        }
        else
        {
            log.debug("No Error");
            return cd.getSMIValue();
        }
    }
    
    public Object executeByPass(Target target, Target newtarget, HashMap<String, String> params)
    	throws Exception
    {
    	CommandData cd = null;
    	
    	try
    	{   getClient(target);
    		BYPASSClient bypassClient = (BYPASSClient)client;
    		bypassClient.onlyConnect();
    		
    		Thread.sleep(5000);
    		
    		CommandAction commandAction = null;
    		
    		try {
    			log.debug("CommandAction : " + target.getNameSpace());
    			commandAction = (CommandAction) Class.forName("com.aimir.fep.bypass.actions.CommandAction_" + target.getNameSpace()).newInstance();
    		} catch (Exception e) {
    			throw new Exception("CommandAction Execute Error.", e);
    		}
    		
    		if(client.isConnected()) {
				IoSession session = bypassClient.getSession();
				cd = commandAction.execute(params, session, client);
			}
    		
    	} catch(Exception e) {
    		log.error(e, e);
    		throw e;
    	} finally {
    		   release();
    	}
    	
        if (cd == null) {
            log.debug("Return Command Data is NULL");
            return new Integer(ErrorCode.IF4ERR_RETURN_DATA_EMPTY);
        }
        else if (cd.getErrCode().getValue() != 0)
        {
            log.debug("ErrorCode="+cd.getErrCode().getValue());
            return new Integer(cd.getErrCode().getValue());
        }
        else
        {
            log.debug("No Error // cd.getSMIValue() size : "+cd.getSMIValue().length);
            return cd.getSMIValue();
        }
    }

    /**
     * Execute Download File Command
     *
     * @param target <code>Hashtable</code> MCU Target Information
     * @param filename <code>String</code> SMIValues
     * @return object <code>SMIValues</code> SMIValues
     */
    public Object executeGetFile(Target target, String filename)
        throws Exception
    {
        CommandData cd = null;
        try {
            MIBUtil mu = MIBUtil.getInstance();

            getClient(target);

            log.debug("get file["+filename+"]");

            CommandData command = DataUtil.getGetFileCommandData(filename);
            cd = client.sendCommand(command);
        }
        finally {
            release();
        }

        if (cd == null) {
            log.debug("Return Command Data is NULL");
            return new Integer(ErrorCode.IF4ERR_RETURN_DATA_EMPTY);
        }
        else if (cd.getErrCode().getValue() != 0)
        {
            log.debug("ErrorCode="+cd.getErrCode().getValue());
            return new Integer(cd.getErrCode().getValue());
        }
        else
        {
            log.debug("No Error");
            DataUtil.saveDownloadFile(filename, cd);
            return cd.getSMIValue();
        }
    }

    /**
     * get CommandProxy ObjectName String
     *
     * @return name <code>String</code> objectName String
     */
    public String getName() {
        return this.objectName.toString();
    }

    /**
     * stop CommandProxy
     */
    public void stop()
    {
        log.debug(this.objectName + " stop");
    }

    public void closeClient() {
        client.close();
    }
}
