/**
 * (@)# MCUScanningBatch.java
 *
 * 2014. 12. 15.
 *
 * Copyright (c) 2013 NURITELECOM, Inc.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * NURITELECOM, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with NURITELECOM, Inc.
 *
 * For more information on this product, please see
 * http://www.nuritelecom.co.kr
 *
 */
package com.aimir.fep.tool;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.aimir.dao.device.MCUDao;
import com.aimir.fep.command.conf.DefaultConf;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.MCU;

/**
 * @author nuri
 *
 */
public class MCUScanningBatch {
	private static Log log = LogFactory.getLog(MCUScanningBatch.class);
	private static int searchType = 0; // 0: all, 1: sid, 2: ip
	List<String> targetList;
	List<Map<String, String>> successList = new LinkedList<Map<String, String>>();
	List<Map<String, String>> failList = new LinkedList<Map<String, String>>();
	ThreadPoolExecutor executor = null;

	private void setTargetList() {
		InputStream fileInputStream = getClass().getClassLoader().getResourceAsStream("mcu_scanning_list.txt");
		if (fileInputStream != null) {
			targetList = new LinkedList<String>();

			Scanner scanner = new Scanner(fileInputStream);
			while (scanner.hasNextLine()) {
				targetList.add("'" + scanner.nextLine().trim() + "'");
			}
			log.info("Search Target (" + targetList.size() + ") ===> " + targetList.toString());
		} else {
			log.info("Target list file not found");
			if (searchType != 0) {
				log.info("Please check your \"mcu_scanning_list.txt\" file.");
				System.exit(0);
			}
		}
	}

	@SuppressWarnings("rawtypes")
	private void mcuUnitScanningStart() {
		long startTime = System.currentTimeMillis();

		try {
			setTargetList();

			ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[] { "/config/spring.xml" });
			DataUtil.setApplicationContext(ctx);

			/*
			 * MCU Search by SID or IP
			 */
			List<MCU> mList = new LinkedList<MCU>();
			mList = ctx.getBean(MCUDao.class).getMcusByTargetList(searchType, targetList);
			List<String> mcuList = new LinkedList<String>();

			if (mList != null && 0 < mList.size()) {
				for (MCU m : mList) {
					mcuList.add(m.getSysID());
				}
				log.info("MCU_SID Total List (" + mList.size() + ") ===> " + mcuList.toString());

				log.debug("MCU ==> [" + mcuList.toString() + "]");

				// MCU Property
				DefaultConf defaultConf = DefaultConf.getInstance();
				Hashtable props = defaultConf.getDefaultProperties("MCU");

				String[] propertys = new String[props.size()];
				Iterator it = props.keySet().iterator();
				for (int i = 0; it.hasNext(); i++) {
					propertys[i] = (String) it.next();
				}

				// MCU List
				Collection<CallableTask> callList = new LinkedList<CallableTask>();
				for (String mcuSid : mcuList) {
					callList.add(new CallableTask(mcuSid, propertys));
				}

				// Excute Job.
				executor = new ThreadPoolExecutor(100, 100, 1, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());
				List<Future<Map<String, String>>> futureL = executor.invokeAll(callList);

				TimeUnit.SECONDS.sleep(5);

				for (Future<Map<String, String>> resultFuture : futureL) {
					if (resultFuture.isDone()) {
						Map<String, String> ht = resultFuture.get(2, TimeUnit.SECONDS);

						if (ht.get("error_create") != null || ht.get("STATUS") != null) { // Scanning Fail.
							failList.add(ht);
						} else { // Scanning Success.
							successList.add(ht);
						}
					} else if (resultFuture.isCancelled()) {
						// log.debug("FAIL : " + ((CallableTask)resultFuture).getMcuId());
						Map<String, String> cancelMap = resultFuture.get(2, TimeUnit.SECONDS);
						failList.add(cancelMap);
					} else {
						Map<String, String> cancelMap = new HashMap<String, String>();
						cancelMap.put("ERROR", resultFuture.toString());
						failList.add(cancelMap);
					}
				}

				/**
				 * Printing
				 */
				log.info("=========== SCAN SUCCESS LIST (" + successList.size() + ") ==========");
				if (successList != null && 0 < successList.size()) {
					for (Map<String, String> sMap : successList) {
						log.info(sMap.toString()); // 파일 생성
					}
				} else {
					log.info("There is no list of successful."); // 파일 생성
				}
				log.info("========================================");

				log.info("===========  SCAN FAIL LIST (" + failList.size() + ")==========");
				if (failList != null && 0 < failList.size()) {
					for (Map<String, String> fMap : failList) {
						log.info(fMap.toString()); // 파일 생성
					}
				} else {
					log.info("There is no list of failure."); // 파일 생성
				}
				log.info("========================================");

				long endTime = System.currentTimeMillis();
				log.info("MCU Scanning finished - Elapse Time : " + (endTime - startTime) / 1000.0f + "s");
			}else {
				log.info("MCU List is null. please check your MCU list or Serach Type.");
			}
		} catch (Exception e) {
			log.error(e, e);
		} finally {
			if (executor != null) {
				executor.shutdown();
				try {
					if (!executor.awaitTermination(3, TimeUnit.SECONDS)) {
						executor.shutdownNow();
						if (!executor.awaitTermination(3, TimeUnit.SECONDS)) {
							System.err.println("Pool did not terminate");
						}
					}
				} catch (InterruptedException ie) {
					executor.shutdownNow();
					Thread.currentThread().interrupt();
				}
			}
		}
		log.info("END.");
		System.exit(0);
	}

	public static void main(String[] args) {
		for (int i = 0; i < args.length; i += 2) {
			String nextArg = args[i];

			if (nextArg.startsWith("-searchType")) {
				searchType = Integer.parseInt(args[i + 1]);
			}
		}

		log.info("Search Type Info ==> 0: ALL, 1: MCU_SID, 2: MCU_IP_ADDRESS");
		log.info("MCU SearchType = " + searchType);

		MCUScanningBatch batch = new MCUScanningBatch();
		batch.mcuUnitScanningStart();
	}
}

class CallableTask implements Callable<Map<String, String>> {
	private static Log log = LogFactory.getLog(CallableTask.class);

	String mcuId;
	String[] property;

	public CallableTask(String mcuId, String[] property) {
		this.mcuId = mcuId;
		this.property = property;
	}

	public String getMcuId() {
		return mcuId;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> call() throws Exception {
		log.debug("[" + this.mcuId + "] Scanning Starting...");

		Map<String, String> result = new LinkedHashMap<String, String>();
		result.put("SCANNING_MCU_SID", this.mcuId);

		try {
			CommandGW cgw = DataUtil.getBean(CommandGW.class);
			Map<String, String> scanResult = cgw.cmdMcuScanning(mcuId, property);

			if (scanResult == null) {
				result.put("STATUS", "SCAN_FAIL");
			} else {
				Set<String> it = scanResult.keySet();
				for (String key : it) {
					result.put(key, scanResult.get(key));
					log.info("key => [" + key + "] value => [" + scanResult.get(key) + "]");
				}
			}
			log.debug("Scanning result ==> " + result.toString());
		} catch (NullPointerException e) {
			result.put("error_create", e.getMessage());
		} catch (Exception e) {
			result.put("error_create", e.getMessage());
		}

		return result;
	}
}
