/**
 * 
 */
package com.aimir.fep.tool;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.dao.device.MeterDao;
import com.aimir.fep.tool.batch.excutor.IBatchRunnable;
import com.aimir.fep.tool.batch.excutor.RunnableBatchExcutor;
import com.aimir.fep.tool.batch.job.CurrentLoadLimitJob;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.model.device.MCU;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.util.CalendarUtil;

/**
 * @author simhanger
 *
 */
//@Service
public class MOECommandBatchTool {
	private static Logger log = LoggerFactory.getLogger(MOECommandBatchTool.class);

	int gprsExcuterCorePoolSize = Integer.parseInt(FMPProperty.getProperty("ota.firmware.excutor.corepoolsize", "1"));
	int plcExcuterCorePoolSize = 1;  
	
	private RunnableBatchExcutor gprsExcutor;
	private RunnableBatchExcutor plcExcutor;
	
	private static MOECommandType commandType;
	private static String fileName = "MOECommandBatch_list.txt";
	private static String param1 = null;
	private static String param2 = null;
	private static int interval = 5; // SMS 보내는 주기.
	private boolean isNowRunning = false;
	private ApplicationContext ctx;

	private Map<String, List<Meter>> mcuTarget = new HashMap<String, List<Meter>>();
	private List<Meter> gprsTarget = new ArrayList<Meter>();

	public MOECommandBatchTool() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Command Type
	 */
	public enum MOECommandType {
		GET_DEMAND_PERIOD("cmdGetDemandPeriod"), SET_DEMAND_PERIOD("cmdDemandPeriod"), GET_CURRENT_LOAD_LIMIT("cmdGetCurrentLoadLimit"), SET_CURRENT_LOAD_LIMIT("cmdSetCurrentLoadLimit"), UNKNOWN("Unknown");

		private String cmd;

		private MOECommandType(String cmd) {
			this.cmd = cmd;
		}

		public String getCmd() {
			return cmd;
		}

		public static MOECommandType getItem(String cmd) {
			for (MOECommandType fc : MOECommandType.values()) {
				if (fc.cmd.equals(cmd)) {
					return fc;
				}
			}
			return UNKNOWN;
		}
	}

	private int setTargetList() {
		int targetSize = 0;
		List<String> targetList = null;
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

			targetSize = targetList.size();
			log.info("Target List({}) ===> {}", targetList.size(), targetList.toString());
		} else {
			log.info("[{}] file not found", fileName);

			/********
			 * 임시
			 */
			// targetList = new LinkedList<String>();
			// targetList.add("201501000033");
		}

		/*
		 * Target 설정
		 */
		if (targetList != null && 0 < targetList.size()) {
			// springInit();
			MeterDao meterDao = DataUtil.getBean(MeterDao.class);
			for (String mdsId : targetList) {
				Meter meter = meterDao.get(mdsId);
				if (meter != null) {
					Modem modem = meter.getModem();
					if (modem != null) {
						if (modem.getModemType() == ModemType.MMIU && modem.getProtocolType() == Protocol.SMS) {
							gprsTarget.add(meter);
						} else if (modem.getModemType() == ModemType.PLCIU && modem.getProtocolType() == Protocol.PLC) {
							MCU mcu = modem.getMcu();
							if (mcuTarget.containsKey(mcu.getSysID())) {
								mcuTarget.get(mcu.getSysID()).add(meter);
							} else {
								List<Meter> meterList = new ArrayList<Meter>();
								meterList.add(meter);
								mcuTarget.put(mcu.getSysID(), meterList);
							}
						} else {
							log.warn("{},ERROR,Unknown Target type mdsid={}, deviceSerial={}", meter.getMdsId(), mdsId, modem.getDeviceSerial());
						}
					} else {
						log.warn("{},ERROR,Target Modem is null", meter.getMdsId());
					}
				} else {
					log.warn("{},ERROR,Unknown Target Meter", mdsId);
				}
			}
		} else {
			log.warn("Have no taget list. please check target list.");
		}

		return targetSize;
	}

	/*
	 * 
	 * 테스트 코드 . 추후 삭제할것.
	 * 
	 * 
	 */
		public void executeTest() {
			commandType = MOECommandType.GET_CURRENT_LOAD_LIMIT;
			param1 = "0";
			param2 = "0";
	
			fileName = "file:E:/00_workspace/07_aimir_moe/01_svn/IF4_V12/aimir-fep/src/main/resources/MOECommandBatch_list.txt";
	
			execute();
		}

	public void execute() {
		if (isNowRunning) {
			log.info("########### MOE CommandBatch Task[{}] is already running...", commandType);
			return;
		}

		/*
		 * Excutor 세팅
		 *  - PLC Excutor는 Bypass 특성상 여러대를 할수가 없어서 집중기 1대당 미터 1대씩 실행하도록 한다.
		 */
		gprsExcutor = new RunnableBatchExcutor(gprsExcuterCorePoolSize);
		plcExcutor = new RunnableBatchExcutor(plcExcuterCorePoolSize);

		isNowRunning = true;
		Date startDate = new Date();
		long startTime = startDate.getTime();

		log.info("########### START MOE Command[{}] - {} ###############", new Object[] { commandType, CalendarUtil.getDatetimeString(startDate, "yyyy-MM-dd HH:mm:ss") });

		if (0 < setTargetList()) {
			switch (commandType) {
			case GET_CURRENT_LOAD_LIMIT:
				currentLoadLimit(commandType);
				break;
			case SET_CURRENT_LOAD_LIMIT:
				if (param1 == null || param2 == null) {
					log.warn("Please check input Options.");
					log.warn("ex) -f [Meter List file name] -commandType cmdOnDemand -param1 [judgeTime] -param2 [threshold]");
				} else {
					// setCurrentLoadLimit(param1, param2, smsInterval);
					currentLoadLimit(commandType);
				}
				break;
			case UNKNOWN:

				break;
			default:
				break;
			}
		}

		long endTime = System.currentTimeMillis();
		log.info("FINISHED MOE Command[{}] - Elapse Time : {}s", commandType, (endTime - startTime) / 1000.0f);

		log.info("########### END MOE CommandBatch Task[{}] ############", commandType);
		isNowRunning = false;

		System.exit(0);
	}

	/**
	 * Get Current load limit Command
	 */
	private void currentLoadLimit(MOECommandType commandType) {

		/*
		 * GPRS Modem 인경우
		 */
		if (gprsTarget != null && 0 < gprsTarget.size()) {
			List<IBatchRunnable> targetList = new ArrayList<IBatchRunnable>();
			for (Meter meter : gprsTarget) {
				targetList.add(new CurrentLoadLimitJob(ctx, commandType, meter, null, interval, param1, param2));
			}

			gprsExcutor.execute("[" + commandType.name() + "] CommandBatch for GPRS Modem", targetList);
		}

		/*
		 * PLC Modem 인경우 집중기별로 처리.
		 */
		Iterator<String> it = mcuTarget.keySet().iterator();
		while (it.hasNext()) {
			String mcuId = it.next();
			List<Meter> meterList = mcuTarget.get(mcuId);

			List<IBatchRunnable> targetList = new ArrayList<IBatchRunnable>();
			for (Meter meter : meterList) {
				targetList.add(new CurrentLoadLimitJob(ctx, commandType, meter, mcuId, 0, param1, param2));
			}

			plcExcutor.execute("[" + commandType.name() + "] CommandBatch for PLC Modem", targetList);
		}
	}

	private void springInit() throws Exception {
		ctx = new ClassPathXmlApplicationContext(new String[] { "/config/spring-moecommandbatch.xml" });
		DataUtil.setApplicationContext(ctx);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		for (int i = 0; i < args.length; i += 2) {
			String nextArg = args[i];

			if (nextArg.startsWith("-commandType")) {
				commandType = MOECommandType.getItem(args[i + 1]);
			} else if (nextArg.startsWith("-interval")) {
				interval = (args[i + 1]).equals("${interval}") ? 5 : Integer.parseInt(args[i + 1]);
			} else if (nextArg.startsWith("-param1")) {
				param1 = (args[i + 1]).equals("${param1}") ? null : args[i + 1];
			} else if (nextArg.startsWith("-param2")) {
				param2 = (args[i + 1]).equals("${param2}") ? null : args[i + 1];
			} else {
				log.error("Please input Options.");
				log.error("ex) -f [file name] -commandType [command name] -param1 [first parameter] -param2 [second parameter]");
				System.exit(0);
			}
		}

		if (commandType == null || commandType.equals("")) {
			log.error("Please Check Command name.");
			System.exit(0);
		}

		MOECommandBatchTool batch = new MOECommandBatchTool();
		try {
			batch.springInit();
			batch.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}