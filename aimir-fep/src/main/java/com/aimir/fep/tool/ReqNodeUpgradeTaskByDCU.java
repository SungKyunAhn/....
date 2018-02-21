package com.aimir.fep.tool;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
//import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Service;
//import org.springframework.transaction.TransactionStatus;

import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.Modem;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.device.MCUDao;

@Service
public class ReqNodeUpgradeTaskByDCU implements Runnable {

    private static Log log = LogFactory.getLog(ReqNodeUpgradeTaskByDCU.class);
	ApplicationContext ctx;
	private static String fileName = "ReqNodeUpgradeTaskList.txt";
	private List<String> targetList;
	private String _mcuId;
	
    @Autowired
    ModemDao modemDao;

    @Autowired
    MCUDao mcuDao;
    
    //@Resource(name="transactionManager")
    //JpaTransactionManager txmanager;

    private static String _upgradeType;
    private static String _control;
    private static String _imageKey;
    private static String _imageUrl;
    private static String _upgradeCheckSum;
    private static int _threadCount;
    private static String _fwVersion;
    private static String _fwBuild;    
    
    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"/config/spring-fep-schedule.xml"}); 
        DataUtil.setApplicationContext(ctx);

		log.debug("ARG_0[" + args[0] + "] ARG_1[" + args[1] + "] ARG_2[" + args[2] + "] ARG_3[" + args[3] + 
				"] ARG_4[" + args[4] + "] ARG_5[" + args[5] + "] ARG_6[" + args[6] + "] ARG_7[" + args[7] + "]");		
		
    	_threadCount = Integer.parseInt(args[0]);
		_upgradeType = args[1];
		_control = args[2];
		_imageKey = args[3];        
		_imageUrl = args[4];                
		_upgradeCheckSum = args[5]; 
		_fwVersion = args[6]; 
		_fwBuild = args[7]; 
    	        
        ReqNodeUpgradeTaskByDCU task = new ReqNodeUpgradeTaskByDCU();
    	task.setTargetList();
		  	
		ExecutorService pool = Executors.newFixedThreadPool(_threadCount);
        try
        {
    		for(String mcuId: task.getTargetList()){
    			task.set_mcuId(mcuId);
    	        pool.execute(task);
    		}
    		pool.shutdown();
        } catch (Exception ex)
        {
        	pool.shutdown();
            log.error("failed ", ex);
        }
    }


	@Override
	public void run() {

		ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[] { "/config/spring-fep-schedule.xml" });
		DataUtil.setApplicationContext(ctx);
		
        //TransactionStatus txstatus = null;
        //txstatus = txmanager.getTransaction(null);
        List<String> modemList = new ArrayList<String>();
        
		CommandGW commandGW = (CommandGW)DataUtil.getBean(CommandGW.class);
		ModemDao modemDao = DataUtil.getBean(ModemDao.class);
		Set<Condition> condition = new HashSet<Condition>();
		//condition.add(new Condition("mcu", new Object[] { "mcu" }, null, Restriction.ALIAS));
		condition.add(new Condition("mcu.sysID", new Object[] { _mcuId }, null, Restriction.EQ));
		condition.add(new Condition("fwVer", new Object[] { _fwVersion }, null, Restriction.NEQ));
		condition.add(new Condition("fwRevision", new Object[] { _fwBuild }, null, Restriction.LT));
    	List<Modem> modems = modemDao.findByConditions(condition);
    	
    	for(Modem modem :  modems){
    		modemList.add(modem.getDeviceSerial());
    	}

        try {
        	commandGW.cmdReqNodeUpgrade(_mcuId, 
        			(int)Integer.decode(_upgradeType), 
        			(int)Integer.decode(_control), 
        			_imageKey, 
        			_imageUrl, 
        			_upgradeCheckSum, 
        			modemList); 
            
        }
        catch (Exception e) {
            //if (txstatus != null) txmanager.rollback(txstatus);
            log.error(e, e);
            return;
        }
        //if (txstatus != null) txmanager.commit(txstatus);
        
        log.info("ReqNodeUpgradeTaskByDCU ");
		
	}    
	
	// file에 기재된 device_serial 정보를 읽어온다.
	private void setTargetList() {
		InputStream fileInputStream = getClass().getClassLoader().getResourceAsStream(fileName);
		if (fileInputStream != null) {
			targetList = new LinkedList<String>();

			@SuppressWarnings("resource")
			Scanner scanner = new Scanner(fileInputStream);
			while (scanner.hasNextLine()) {
				String target = scanner.nextLine().trim();
				if (!target.equals("")) {
					targetList.add(target);
				}
			}
			log.info("Target List:" + "(" + targetList.size() + ")" + targetList.toString());
		} else {
			log.info("File not found");
		}
	}

	private List<String> getTargetList()
	{
		return this.targetList;
	}

	public String get_mcuId() {
		return _mcuId;
	}

	public void set_mcuId(String _mcuId) {
		this._mcuId = _mcuId;
	}
}
