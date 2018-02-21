package com.aimir.fep;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.mina.core.session.IdleStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionStatus;

import com.aimir.dao.device.FirmwareDao;
import com.aimir.dao.device.FirmwareIssueDao;
import com.aimir.dao.device.FirmwareIssueHistoryDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.fep.bypass.decofactory.consts.HdlcConstants;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.protocol.nip.client.actions.NICommandAction;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.model.device.Firmware;
import com.aimir.model.device.FirmwareIssue;
import com.aimir.model.device.FirmwareIssueHistory;
import com.aimir.model.system.Location;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;
import com.aimir.util.DateTimeUtil;

public class Test {

	private static Logger logger = LoggerFactory.getLogger(Test.class);

	@org.junit.Test
	public void convertIdTest(){
		// MCU BYTE : D56D91B8
        byte[] mcu = DataUtil.readByteString("D56D91B8");
        System.out.println("1 - " + Hex.getHexDump(mcu));
        
        DataUtil.convertEndian(mcu);
        System.out.println("2 - " + Hex.getHexDump(mcu));
        
        //int mcuId = DataUtil.getIntTo4Byte(mcu);
        long mcuId = DataUtil.getLongToBytes(mcu);
        System.out.println("3 - " + mcuId);  // MCU[-1198428715] 
                                                     //2147483647
                                                     //3096538581
        
        long mcuIds = convertInt(mcu);
        System.out.println("4 - " + mcuIds);
	}
	
    public long convertInt(byte[] values)
    {
        long value = 0;
        value |= (values[0] & 0xff);
        value <<= 8;
        value |= (values[1] & 0xff);
        value <<= 8;
        value |= (values[2] & 0xff);
        value <<= 8;
        value |= (values[3] & 0xff);
        
        return value;
    }
	
	
	public void waitTest(){		
		try {
			System.out.println("111");
			waitForJobFinish(5);
			System.out.println("222");
		} catch (Exception e) {
			System.out.println("333");
			System.out.println(e.getMessage());
		}
		System.out.println("444");
	}
	
	boolean isCommandAciontFinished = false;
	private boolean waitForJobFinish(int commandWaitingTime) throws Exception{
		boolean result = false;
        long stime = System.currentTimeMillis();
        long ctime = 0;
        
        System.out.println("시작");
		while(!isCommandAciontFinished) {
			System.out.println("aaa");
			waitResponse();
			System.out.println("bbb");
		    ctime = System.currentTimeMillis();
		    if(((ctime - stime)/1000) > commandWaitingTime){
		    	//log.debug("HandlerName=[" + getHandlerName() + "]getResponse:: SESSION IDLE COUNT["+session.getIdleCount(IdleStatus.BOTH_IDLE)+"]");
		    	
		    	System.out.println("### 여기출력됨 - ctime=" + ctime + ", stime=" + stime + ", commandWaitingTime=" + commandWaitingTime);
		    	
		    	throw new Exception("[Timeout:"+commandWaitingTime +"]");
		     }		    	 
		    System.out.println("ccc");
		 }
		 return result;
	}
	
	private Object resMonitor = new Object();
    public void waitResponse() {
        synchronized(resMonitor) { 
            try { 
            	resMonitor.wait(500);
            } catch(InterruptedException ie) {
            	ie.printStackTrace();
            }
        }
    }
	
	
	
	
	
	
	
	public void test1() {
		System.out.println(this.getClass().getResource(".").getPath());
		DataUtil.setApplicationContext(new ClassPathXmlApplicationContext(new String[]{"src/test/resources/config/spring.xml"}));
		
		String otaType = "GroupOTARetry";
		String firmwareVersion = "0094";
		String firmwareFileName = "NAMR-P214SR-korea-0094";
		String deviceModelName = "NAMR-P214SR";
		String locationName = "SSYS";
		String issueDate = "20161014165124";
		Firmware firmware;
		Location location;

//		TransactionStatus txstatus = null;
//		txstatus = txmanager.getTransaction(null);

        JpaTransactionManager txManager = (JpaTransactionManager)DataUtil.getBean("transactionManager");
        TransactionStatus txstatus = null;
        txstatus = txManager.getTransaction(null);
		
		LocationDao locationDao = DataUtil.getBean(LocationDao.class);
		FirmwareDao firmwareDao = DataUtil.getBean(FirmwareDao.class);
		FirmwareIssueHistoryDao firmwareIssueHistoryDao = DataUtil.getBean(FirmwareIssueHistoryDao.class);
		FirmwareIssueDao firmwareIssueDao = DataUtil.getBean(FirmwareIssueDao.class);
		
		try {
			// Firmware 정보
			Set<Condition> firmwareConditions = new HashSet<Condition>();
			firmwareConditions.add(new Condition("fwVersion", new Object[] { firmwareVersion }, null, Restriction.EQ));
			firmwareConditions.add(new Condition("fileName", new Object[] { firmwareFileName }, null, Restriction.EQ));
			firmwareConditions.add(new Condition("equipModel", new Object[] { deviceModelName }, null, Restriction.EQ));
			List<Firmware> firmwareList = firmwareDao.findByConditions(firmwareConditions);
			if (firmwareList != null && 0 < firmwareList.size()) {
				firmware = firmwareList.get(0);
				logger.debug("### Firmware info => {}", firmware.toString());
			} else {
				logger.error("Unknown Firmware. please check firmware file name at Firmware Tap in Firmware Management gadget.");
				return;
			}

			// Location 정보
			List<Location> locationList = locationDao.getLocationByName(locationName);
			if (locationList != null && 0 < locationList.size()) {
				location = locationList.get(0);
				logger.debug("### Location info => {}", location.toString());
			} else {
				logger.error("Unknown Location. please check location name.");
				return;
			}

			// Group OTA History정보
			FirmwareIssue firmwareIssue = null;
			Set<Condition> fIcondition = new HashSet<Condition>();
			fIcondition.add(new Condition("id.locationId", new Object[] { location.getId() }, null, Restriction.EQ));
			fIcondition.add(new Condition("id.firmwareId", new Object[] { Long.valueOf(firmware.getId()) }, null, Restriction.EQ));
			fIcondition.add(new Condition("id.issueDate", new Object[] { issueDate }, null, Restriction.EQ));
			List<FirmwareIssue> firmwareIssueList = firmwareIssueDao.findByConditions(fIcondition);
			if (firmwareIssueList != null && 0 < firmwareIssueList.size()) {
				firmwareIssue = firmwareIssueList.get(0);
				logger.debug("### FirmwareIssue info => {}", firmwareIssue.toString());
			} else {
				logger.error("There is no Group OTA History. Please check Task information.");
				return;
			}

			// Group OTA 목록
			String deviceId = "";
			String targetType;
			String byPass;
			boolean takeOver = false;
			String locaionId = "";
			String firmwareId = "";

			FirmwareIssueHistory firmwareIssueHistory = null;
			Set<Condition> condition = new HashSet<Condition>();
			condition.add(new Condition("id.locationId", new Object[] { firmwareIssue.getLocationId() }, null, Restriction.EQ));
			condition.add(new Condition("id.firmwareId", new Object[] { firmwareIssue.getFirmwareId() }, null, Restriction.EQ));
			condition.add(new Condition("id.issueDate", new Object[] { firmwareIssue.getIssueDate() }, null, Restriction.EQ));
			condition.add(new Condition("resultStatus", new Object[] { "Success" }, null, Restriction.NEQ));
			condition.add(new Condition("resultStatus", null, null, Restriction.NULL));

			List<FirmwareIssueHistory> firmwareIssueHistoryList = firmwareIssueHistoryDao.findByConditions(condition);
			if (firmwareIssueHistoryList != null && 0 < firmwareIssueHistoryList.size()) {

				StringBuilder sb = new StringBuilder();
				for (FirmwareIssueHistory fih : firmwareIssueHistoryList) {
					logger.debug("############# ===> DeviceId = {},  getResultStatus = {}", fih.getDeviceId(), fih.getResultStatus());
					if (fih.getResultStatus() != null && fih.getResultStatus().equals("Success")) {
						logger.info("### Device = [{}] is skip. because [{}] is aready OTA success.", fih.getDeviceId(), fih.getDeviceId());
					} else {
						sb.append(fih.getDeviceId() + ",");
					}

				}
				deviceId = sb.toString();
				deviceId = deviceId.substring(0, deviceId.length() - 1); // 콤마 제거
				targetType = firmwareIssueHistoryList.get(0).getDeviceType().name();
				byPass = firmwareIssueHistoryList.get(0).getUesBypass().toString();
				locaionId = String.valueOf(firmwareIssueHistoryList.get(0).getLocationId());
				firmwareId = String.valueOf(firmwareIssueHistoryList.get(0).getFirmwareId());

				logger.debug("### FirmwareIssueHistory info => TargetType={}, UseBypass={}, DeviceId={}", targetType, byPass, deviceId);
			} else {
				logger.error("There is no OTA Target. Please check Group OTA History.");
				return;
			}

			/*
			 * Group OTA Retry
			 * com.aimir.bo.command.OTACmdController.commandOTAStart() 참조
			 */
			if (firmwareIssueHistoryList != null && 0 < firmwareIssueHistoryList.size()) {
				logger.debug("========= > deviceId={}, targetType={}, takeOver={}, byPass={}, locationId={}, firmwareId={}", deviceId, targetType, Boolean.toString(takeOver), byPass, locaionId, firmwareId);
			} else {
				logger.error("FirmwareIssueHistory list is empty.");
			}

			
			txManager.commit(txstatus);
			
			
		} catch (Exception e) {
			if (txstatus != null) {
				txManager.rollback(txstatus);
			}
			logger.error("Task Excute transaction error - " + e, e);
			return;
		}
		if (txstatus != null) {
			txManager.commit(txstatus);
		}
	}

	public void abstTest() {

		ConcurrentHashMap<String, NICommandAction> commandActionMap = new ConcurrentHashMap<String, NICommandAction>();
		try {
			commandActionMap.put("cmdModemOTAStart_SP", (NICommandAction) Class.forName("com.aimir.fep.protocol.nip.client.actions.NI_cmdModemOTAStart_Action_SP").newInstance());
			// Sample - 추후 필요시 사용할것
			commandActionMap.put("cmdSample1_SP", (NICommandAction) Class.forName("com.aimir.fep.protocol.nip.client.actions.NI_Sample1_Action_SP").newInstance());
			commandActionMap.put("cmdSample2_GD", (NICommandAction) Class.forName("com.aimir.fep.protocol.nip.client.actions.NI_Sample2_Action_GD").newInstance());

		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void otatest() {
		String openTime = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");
		//		EV_SP_200_64_0_Action action = new EV_SP_200_64_0_Action();
		//		action.makeEvent(TargetClass.EnergyMeter, target.getMeterId(), TargetClass.EnergyMeter, openTime, "HES");
		//		action.updateOTAHistory(target.getMeterId(), DeviceType.Meter, openTime);
		//		

	}

	public void testtiem() {
		String message = "Firmware Updated. Current Modem Time = " + DateTimeUtil.getCurrentDateTimeByFormat1("yyyyMMddHHmmss");
		System.out.println(message);
	}

	public void testaaa() {

		int[] result = HdlcConstants.getSRCount((byte) 0x1E);
	}

	public void testt() {
		String str = "07E0 08 11 03 08 00 00 FF800000";

		try {
			String result = DataUtil.getDateTimeByDLMS_OCTETSTRING12(DataUtil.readByteString(str));

			System.out.println("==> " + result);
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public void test() {
		CommandGW gw = new CommandGW();
		try {
			gw.cmdGetMeterFWVersion("TopologyTest_1");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
