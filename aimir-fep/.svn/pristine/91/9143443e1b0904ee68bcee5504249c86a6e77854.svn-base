/**
 * (@)# EMnVCommandBatch.java
 *
 * 2015. 7. 29.
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.constants.CommonConstants.MeteringType;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.mvm.DayEMDao;
import com.aimir.dao.mvm.LpEMDao;
import com.aimir.dao.mvm.MonthEMDao;
import com.aimir.fep.meter.AbstractMDSaver;
import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.meter.parser.DLMSEMnVGtype;
import com.aimir.fep.meter.parser.MeterDataParser;
import com.aimir.fep.meter.parser.DLMSKepcoTable.LPComparator;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.EnergyMeter;
import com.aimir.model.device.Meter;
import com.aimir.model.mvm.LpEM;
import com.aimir.util.CalendarUtil;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;
import com.aimir.util.DateTimeUtil;

/**
 * @author nuri
 *
 */
public class EMnVLPRecoveryBatch {
	private static Logger log = LoggerFactory.getLogger(EMnVLPRecoveryBatch.class);

	private int CORE_POOL_SIZE;
	private int MAXIMUM_POOL_SIZE;
	private int KEEP_ALIVE_TIME;
	private TimeUnit KEEP_ALIVE_TIME_UNIT;
	private TimeUnit AWAIT_TIME_OUT_TIME_UNIT;
	private int AWAIT_TIME_OUT;
	private boolean isNowRunning = false;
	List<Map<String, String>> successList = new LinkedList<Map<String, String>>();
	List<Map<String, String>> failList = new LinkedList<Map<String, String>>();
	List<Map<String, String>> skipList = new LinkedList<Map<String, String>>();
	ThreadPoolExecutor executor = null;

	private String fileName;
	private List<String> targetList;
	ApplicationContext ctx;
	JpaTransactionManager txManager;
	TransactionStatus txStatus;

	public static void main(String[] args) {
		EMnVLPRecoveryBatch batch = new EMnVLPRecoveryBatch();
		batch.init();
		batch.execute();
	}

	// Test 코드
	public void executeTest() {
		execute();
	}

	/*
	 * 초기화
	 */
	public void init() {
		//CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors();		
		CORE_POOL_SIZE = 9; // 10개부터는 db connection 에러남.
		MAXIMUM_POOL_SIZE = 9; // CORE_POOL_SIZE보다 작으면 에러남

		KEEP_ALIVE_TIME = 5 * 120;
		KEEP_ALIVE_TIME_UNIT = TimeUnit.MINUTES;

		AWAIT_TIME_OUT = 5 * 60;
		AWAIT_TIME_OUT_TIME_UNIT = TimeUnit.MINUTES;

		fileName = "EMnVLPRecoveryBatch_list.txt";
	}

	public void execute() {
		if (isNowRunning) {
			log.info("########### EMnVLPRecoveryBatch is already running...");
			return;
		}

		isNowRunning = true;
		Date startDate = new Date();
		long startTime = startDate.getTime();

		log.info("########### CPU 갯수 = {}, File List = {}", Runtime.getRuntime().availableProcessors(), fileName);
		log.info("########### EMnVLPRecoveryBatch - {} ###############", CalendarUtil.getDatetimeString(startDate, "yyyy-MM-dd HH:mm:ss"));
		setTargetList();

		if (targetList == null || targetList.size() <= 0) {
			log.warn("Have no taget list. please check target list.");
		} else {
			try {
				ctx = new ClassPathXmlApplicationContext(new String[] { "/config/spring.xml" });
				DataUtil.setApplicationContext(ctx);

				txManager = ctx.getBean(JpaTransactionManager.class);
				txStatus = txManager.getTransaction(null);

				List<Meter> mList = new LinkedList<Meter>();
				MeterDao meterDao = DataUtil.getBean(MeterDao.class);
				Set<Condition> condition = new HashSet<Condition>();
				condition.add(new Condition("mdsId", targetList.toArray(), null, Restriction.IN));
				condition.add(new Condition("mdsId", null, null, Restriction.ORDERBY));
				mList = meterDao.findByConditions(condition);

				txManager.commit(txStatus);

				Collection<EMnVRecoveryCallable> callList = new LinkedList<EMnVRecoveryCallable>();

				int i = 1;
				for (Meter target : mList) {
					callList.add(new EMnVRecoveryCallable(i, target));
					i++;
				}

				executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, new LinkedBlockingQueue<Runnable>());
				List<Future<Map<String, String>>> futureL = executor.invokeAll(callList, 300, TimeUnit.MINUTES); // 주어진 작업을 모두 실행한다. 지정한 시간 동안 완료되지 못한 작업은 취소.

				for (Future<Map<String, String>> resultFuture : futureL) {
					
					
					try {
						Map<String, String> ht = resultFuture.get(AWAIT_TIME_OUT, AWAIT_TIME_OUT_TIME_UNIT); // Callable 객체의 작업시간 제한. 시간내에 완료되면 Done, 완료안되면 Cancelled
						if (resultFuture.isDone()) {
							if (ht.get("status").equals("fail")) { // excute Fail.
								failList.add(ht);
							} else if (ht.get("status").equals("skip")) { // skip 
								skipList.add(ht);
							} else { // excute Success.
								successList.add(ht);
							}
						} else if (resultFuture.isCancelled()) {
							log.info("###### Future is Cancelled ==> {}", ht == null ? "" : ht.get("mdsId"));
							failList.add(ht);
						} else {
							log.info("###### Future is Error ==> {}", ht == null ? "" : ht.get("mdsId"));
							Map<String, String> unknownCancelMap = new HashMap<String, String>();
							unknownCancelMap.put("ERROR", resultFuture.toString());
							failList.add(unknownCancelMap);
						}
					} catch (CancellationException ce) {
						log.info("###### Future is CancellationException - {}", ce);						
						Map<String, String> exCancelMap = new HashMap<String, String>();
						exCancelMap.put("CancellationException", ce.toString());
						failList.add(exCancelMap);
					} catch (Exception e){
						log.info("###### Future is Exception - {}", e);						
						Map<String, String> exCancelMap = new HashMap<String, String>();
						exCancelMap.put("Exception", e.toString());
						failList.add(exCancelMap);
					}
					
					

				}

				/**
				 * Logging
				 */
				log.info(" ");
				log.info("========= EXCUTE SUCCESS LIST ({}) =========", successList.size());
				int count = 1;
				if (successList != null && 0 < successList.size()) {
					for (Map<String, String> sMap : successList) {
						log.info("{}. {}", count, sMap.toString());
						count++;
					}
				} else {
					log.info("There is no list of successful.");
				}
				log.info("============================================");

				count = 1;
				log.info("=========== EXCUTE SKIP LIST ({}) ==========", skipList.size());
				if (failList != null && 0 < skipList.size()) {
					for (Map<String, String> fMap : skipList) {
						log.info("{}. {}", count, fMap.toString());
						count++;
					}
				} else {
					log.info("There is no list of skip.");
				}
				log.info("============================================");

				count = 1;
				log.info("=========== EXCUTE FAIL LIST ({}) ==========", failList.size());
				if (failList != null && 0 < failList.size()) {
					for (Map<String, String> fMap : failList) {
						log.info("{}. {}", count, fMap.toString());
						count++;
					}
				} else {
					log.info("There is no list of failure.");
				}
				log.info("============================================");

			} catch (Exception e) {
				log.error("Exception-", e);
			} finally {
				if (executor != null) {
					executor.shutdown();
					try {
						if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
							executor.shutdownNow();
							if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
								log.error("Pool did not terminate");
							}
						}
					} catch (InterruptedException ie) {
						executor.shutdownNow();
						Thread.currentThread().interrupt();
					}
				}
			}
			log.info("END.");

		}

		long endTime = System.currentTimeMillis();
		log.info("FINISHED EMnVLPRecoveryBatch - Elapse Time : {}s", (endTime - startTime) / 1000.0f);

		log.info("########### END EMnVLPRecoveryBatch ############");
		isNowRunning = false;

		System.exit(0);
	}

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
			log.info("Target List({}) ===> {}", targetList.size(), targetList.toString());
		} else {
			log.info("[{}] file not found", fileName);
		}
	}

}

class EMnVRecoveryCallable implements Callable<Map<String, String>> {
	private static Logger log = LoggerFactory.getLogger(EMnVRecoveryCallable.class);
	private int idx;
	//	private Meter meter;
	private String mdsId;
	private double activePulseConstant = 0;
	private double reActivePulseConstant = 0;
	private Double ct = 1d;
	private Double pt = 1d;
	private Double st = 1d; // 서버적용배율

	public EMnVRecoveryCallable() {
		// TODO Auto-generated constructor stub
	}

	public EMnVRecoveryCallable(int idx, Meter meter) {
		this.idx = idx;
		//		this.meter = meter;
		this.mdsId = meter.getMdsId();

		if (activePulseConstant == 0.0) {
			activePulseConstant = meter.getPulseConstant();
		}

		if (reActivePulseConstant == 0.0) {
			reActivePulseConstant = meter.getPulseConstant();
		}

		EnergyMeter eMeter = (EnergyMeter) meter;
		if (eMeter != null && eMeter.getCt() != null && eMeter.getCt() > 0) {
			ct = eMeter.getCt();
		}

		if (eMeter != null && eMeter.getPt() != null && eMeter.getPt() > 0) {
			pt = eMeter.getPt();
		}

		st = ct * pt; // 서버 적용배율
		log.debug("MDS_ID={}, CT={}, PT={}, ST={}", mdsId, ct, pt, st);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> call() throws Exception {
		Map<String, String> result = new LinkedHashMap<String, String>();
		result.put("mdsId", mdsId);
		result.put("aDate", "");
		result.put("bDate", "");
		result.put("gap", "");
		result.put("result_time", "");
		result.put("p1", "false");
		result.put("p2", "false");
		result.put("status", "success");

		JpaTransactionManager txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");
		TransactionStatus txStatus = txManager.getTransaction(null);

		try {
			log.debug("");
			log.debug(idx + ". 시작 #################### 미터 아이디 = " + mdsId + " ##########################");
			String aDate = ""; // 2015년의 최초 시간
			String bDate = ""; // 2015년의 최초 시간보다 먼저인 시간중 가장 큰 시간
			String resultDate = ""; // 수정될 시간

			/*
			 * 목록출력
			 */
			LpEMDao lpEMDao = DataUtil.getBean(LpEMDao.class);
			HashSet<Condition> condition = new LinkedHashSet<Condition>();

			condition.add(new Condition("id.mdevId", new Object[] { mdsId }, null, Restriction.EQ));
			condition.add(new Condition("id.yyyymmddhh", new Object[] { "2015%" }, null, Restriction.LIKE));
			condition.add(new Condition("id.yyyymmddhh", null, null, Condition.Restriction.ORDERBY));
			condition.add(new Condition("id.channel", null, null, Condition.Restriction.ORDERBY));

			List<LpEM> lpEMList = lpEMDao.findByConditions(condition);
			int compareChannel = 1;
			log.debug("2015년의 최초 시간 ==> " + lpEMList.get(compareChannel).getYyyymmddhh());
			log.debug("비교채널a = " + lpEMList.get(compareChannel).getChannel());
			aDate = lpEMList.get(compareChannel).getYyyymmddhh();

			if (lpEMList.get(compareChannel).getValue_00() != null) {
				aDate = aDate + "00";
			} else if (lpEMList.get(compareChannel).getValue_15() != null) {
				aDate = aDate + "15";
			} else if (lpEMList.get(compareChannel).getValue_30() != null) {
				aDate = aDate + "30";
			} else if (lpEMList.get(compareChannel).getValue_45() != null) {
				aDate = aDate + "45";
			}
			result.put("aDate", aDate);

			condition.clear();
			condition.add(new Condition("id.mdevId", new Object[] { mdsId }, null, Restriction.EQ));
			condition.add(new Condition("id.yyyymmddhh", new Object[] { aDate.substring(0, 10) }, null, Restriction.LT));
			condition.add(new Condition("id.yyyymmddhh", null, null, Condition.Restriction.ORDERBYDESC));
			condition.add(new Condition("id.channel", null, null, Condition.Restriction.ORDERBY));

			lpEMList = lpEMDao.findByConditions(condition);

			if (lpEMList != null && 0 < lpEMList.size()) {
				log.debug("2015년의 최초 시간보다 먼저인 시간중 가장 큰 시간 ==> " + lpEMList.get(compareChannel).getYyyymmddhh());
				log.debug("비교채널b = " + lpEMList.get(compareChannel).getChannel());
				bDate = lpEMList.get(compareChannel).getYyyymmddhh();
				if (lpEMList.get(compareChannel).getValue_00() != null) {
					bDate = bDate.substring(0, 10) + "00";
				}
				if (lpEMList.get(compareChannel).getValue_15() != null) {
					bDate = bDate.substring(0, 10) + "15";
				}
				if (lpEMList.get(compareChannel).getValue_30() != null) {
					bDate = bDate.substring(0, 10) + "30";
				}
				if (lpEMList.get(compareChannel).getValue_45() != null) {
					bDate = bDate.substring(0, 10) + "45";
				}

				result.put("bDate", bDate);

				SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMddHHmmss");
				Calendar aCal = Calendar.getInstance();
				Calendar bCal = Calendar.getInstance();
				Calendar resultCal = Calendar.getInstance();

				aCal.setTime(DateTimeUtil.getDateFromYYYYMMDDHHMMSS(aDate));
				bCal.setTime(DateTimeUtil.getDateFromYYYYMMDDHHMMSS(bDate));

				/*
				 * 차이산출
				 */
				long dateGap = aCal.getTimeInMillis() - bCal.getTimeInMillis();

				int tempSec = (int) (dateGap / 1000);
				int time = tempSec / 3600;
				int min = tempSec % 3600 / 60;
				int sec = tempSec % 3600 % 60 % 60;

				log.debug("틀어진 날짜 차이 : " + time + "시간 " + min + "분 " + sec + "초");
				result.put("gap", time + "시간 " + min + "분 " + sec + "초");

				resultCal.setTimeInMillis(bCal.getTimeInMillis() + dateGap);
				resultCal.add(Calendar.MINUTE, -15);
				resultDate = dateFormatter.format(resultCal.getTime());
				log.debug("결과 시간 => " + resultDate);
				result.put("result_time", resultDate);

				/**
				 * LP 생성
				 */
				//List<LPData> bLPDataList = new LinkedList<LPData>();

				//datetime=[20150730101500],lp=[5259441.0],lpValue=[5259.441],ch1=[0.0000],ch2=[0.0000],ch3=[0.0000],ch4=[0.0000],ch5=[5259.4410],ch6=[0.0000],ch7=[733.9270],ch8=[5333.4350],flag=[0],status=[000001000000000000000000]
				//datetime=[201506271915]  ,lp=[null],lpValue=[null],ch1=[0.0442],ch2=[0.0000],ch3=[0.5882],ch4=[0.5950],ch5=[0.0000],ch6=[10.3972],ch7=[51.9044],ch8=[0.0000],flag=[0],status=[null]			

				//			_lpData.setCh(new Double[] { active, laggingReactive, leadingReactive, apparentEnergy
				//					, lpData[i + 1].getCh()[0], lpData[i + 1].getCh()[1], lpData[i + 1].getCh()[2], lpData[i + 1].getCh()[3]});

				Map<String, Object> bLpMap = new HashMap<String, Object>();
				for (LpEM lpEm : lpEMList) {
					int channel = lpEm.getChannel();

					if (channel == 0 || channel == 98 || channel == 100) {
						continue;
					}

					String partDate = lpEm.getYyyymmddhh();

					if (lpEm.getValue_45() != null) {
						partDate = partDate.substring(0, 10) + "45";
						if (!bLpMap.containsKey(partDate)) {
							LPData lp = new LPData();

							resultCal = Calendar.getInstance();
							bCal = Calendar.getInstance();
							bCal.setTime(DateTimeUtil.getDateFromYYYYMMDDHHMMSS(partDate));
							resultCal.setTimeInMillis(bCal.getTimeInMillis() + dateGap);
							resultCal.add(Calendar.MINUTE, -15);
							resultDate = dateFormatter.format(resultCal.getTime());

							lp.setDatetime(resultDate);
							//						log.debug("LPDate : {} => {}", partDate, resultDate);
							lp.setCh(new Double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 });
							bLpMap.put(partDate, lp);
						}

						LPData lp = (LPData) bLpMap.get(partDate);
						lp.getCh()[channel - 1] = lpEm.getValue_45();

						if (channel == 1) {
							lp.setLp(lpEm.getValue_45() * activePulseConstant / st);
							lp.setLpValue(lpEm.getValue_45());
						}

						//						log.debug("Ch={}, value45={}", channel, lpEm.getValue_45());
					}
					if (lpEm.getValue_30() != null) {
						partDate = partDate.substring(0, 10) + "30";
						if (!bLpMap.containsKey(partDate)) {
							LPData lp = new LPData();
							resultCal = Calendar.getInstance();
							bCal = Calendar.getInstance();
							bCal.setTime(DateTimeUtil.getDateFromYYYYMMDDHHMMSS(partDate));
							resultCal.setTimeInMillis(bCal.getTimeInMillis() + dateGap);
							resultCal.add(Calendar.MINUTE, -15);
							resultDate = dateFormatter.format(resultCal.getTime());

							lp.setDatetime(resultDate);
							//							log.debug("LPDate : {} => {}", partDate, resultDate);
							lp.setCh(new Double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 });
							bLpMap.put(partDate, lp);
						}

						LPData lp = (LPData) bLpMap.get(partDate);
						lp.getCh()[channel - 1] = lpEm.getValue_30();

						if (channel == 1) {
							lp.setLp(lpEm.getValue_30() * activePulseConstant / st);
							lp.setLpValue(lpEm.getValue_30());
						}

						//					log.debug("Ch={}, value30={}", channel, lpEm.getValue_30());
					}
					if (lpEm.getValue_15() != null) {
						partDate = partDate.substring(0, 10) + "15";
						if (!bLpMap.containsKey(partDate)) {
							LPData lp = new LPData();
							resultCal = Calendar.getInstance();
							bCal = Calendar.getInstance();
							bCal.setTime(DateTimeUtil.getDateFromYYYYMMDDHHMMSS(partDate));
							resultCal.setTimeInMillis(bCal.getTimeInMillis() + dateGap);
							resultCal.add(Calendar.MINUTE, -15);
							resultDate = dateFormatter.format(resultCal.getTime());

							lp.setDatetime(resultDate);
							//							log.debug("LPDate : {} => {}", partDate, resultDate);
							lp.setCh(new Double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 });
							bLpMap.put(partDate, lp);
						}

						LPData lp = (LPData) bLpMap.get(partDate);
						lp.getCh()[channel - 1] = lpEm.getValue_15();

						if (channel == 1) {
							lp.setLp(lpEm.getValue_15() * activePulseConstant / st);
							lp.setLpValue(lpEm.getValue_15());
						}

						//						log.debug("Ch={}, value15={}", channel, lpEm.getValue_15());
					}
					if (lpEm.getValue_00() != null) {
						partDate = partDate.substring(0, 10) + "00";
						if (!bLpMap.containsKey(partDate)) {
							LPData lp = new LPData();
							resultCal = Calendar.getInstance();
							bCal = Calendar.getInstance();
							bCal.setTime(DateTimeUtil.getDateFromYYYYMMDDHHMMSS(partDate));
							resultCal.setTimeInMillis(bCal.getTimeInMillis() + dateGap);
							resultCal.add(Calendar.MINUTE, -15);
							resultDate = dateFormatter.format(resultCal.getTime());

							lp.setDatetime(resultDate);
							//						log.debug("LPDate : {} => {}", partDate, resultDate);
							lp.setCh(new Double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 });
							bLpMap.put(partDate, lp);
						}

						LPData lp = (LPData) bLpMap.get(partDate);
						lp.getCh()[channel - 1] = lpEm.getValue_00();

						if (channel == 1) {
							lp.setLp(lpEm.getValue_00() / st * activePulseConstant); // 34 10000
							lp.setLpValue(lpEm.getValue_00());
						}

						//					log.debug("Ch={}, value00={}", channel, lpEm.getValue_00());
					}
				}

				// LP배열로 만들고, 날짜별로 정렬
				LPData[] lplist = new LPData[bLpMap.size()];
				Iterator<String> it = bLpMap.keySet().iterator();
				int temp = 0;
				while (it.hasNext()) {
					lplist[temp] = (LPData) bLpMap.get(it.next());
					temp++;
				}

				Arrays.sort(lplist, LPComparator.TIMESTAMP_ORDER);

				/*
				 * Date 수정
				 */
				MeterDao meterDao = DataUtil.getBean(MeterDao.class);
				MeterDataParser parser = (MeterDataParser) Class.forName("com.aimir.fep.meter.parser.DLMSEMnVGtype").newInstance();
				parser.setMeter(meterDao.findByCondition("mdsId", mdsId)); // 한 트렌젝션안에서 meter객체를 사용하기위함. 

				int startLpListParam = 0; // 시작 lp. 
				log.info("mdsId - {}", mdsId);
				log.info("Start - lplist[0]:" + lplist[startLpListParam]);
				log.info("Start - lplist[0].getDatetime():" + lplist[startLpListParam].getDatetime());

				String startlpdate = lplist[startLpListParam].getDatetime();
				String lpdatetime = startlpdate;
				Calendar cal = Calendar.getInstance();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				cal.setTime(sdf.parse(startlpdate));
				List<Double>[] chValue = new ArrayList[lplist[startLpListParam].getCh().length];
				List<Integer> flag = new ArrayList<Integer>();
				double baseValue = lplist[startLpListParam].getCh()[0];
// - i=1578, lpdatetime=20150714154500, startlpdate=20150715164500
				for (int i = startLpListParam; i < lplist.length; i++) {
					if (!lpdatetime.equals(lplist[i].getDatetime())) {
						saveLPData(chValue, flag, startlpdate, baseValue, (DLMSEMnVGtype) parser);

						startlpdate = lplist[i].getDatetime();
						
						//log.info("i={}, lpdatetime={}, startlpdate={}", i, lpdatetime, startlpdate);
						lpdatetime = startlpdate;
						
						baseValue = lplist[i].getLpValue() == null ? 0.0 : lplist[i].getLpValue();
						flag = new ArrayList<Integer>();
						chValue = new ArrayList[lplist[i].getCh().length];
					}
					flag.add(lplist[i].getFlag());

					for (int ch = 0; ch < chValue.length; ch++) {
						if (chValue[ch] == null)
							chValue[ch] = new ArrayList<Double>();

						if (ch + 1 <= lplist[i].getCh().length)
							chValue[ch].add(lplist[i].getCh()[ch]);
						else
							chValue[ch].add(0.0);
					}
					cal.add(Calendar.MINUTE, parser.getMeter().getLpInterval());
					lpdatetime = sdf.format(cal.getTime());
				}

				saveLPData(chValue, flag, startlpdate, baseValue, (DLMSEMnVGtype) parser);

				log.debug("### 1 saveLPData 완료 ####");
				result.put("p1", "true");

				/**
				 * 데이터 복구후 OLD 데이터 삭제 bDate(2015년의 최초 시간보다 먼저인 시간중 가장 큰 시간) 보다
				 * 작은데이터(오래된 데이터)는 모두 삭제
				 */
				// 1. LP_EM 삭제 - bDate보다 작은데이터(오래된 데이터)는 모두 삭제
				lpEMDao.oldLPDelete(mdsId, bDate);

				// 2. DAY_EM 삭제
				DayEMDao dayEMDao = DataUtil.getBean(DayEMDao.class);
				dayEMDao.oldLPDelete(mdsId, bDate.substring(0, 8));

				// 3. MONTH_EM 삭제
				MonthEMDao monthEMDao = DataUtil.getBean(MonthEMDao.class);
				monthEMDao.oldLPDelete(mdsId, bDate.substring(0, 6));

				log.debug("### 2 deleteLPData 완료 ####");
				result.put("p2", "true");
			} else {
				result.put("status", "skip");
			}

			log.debug(idx + ". 끝 #################### 미터 아이디 = " + mdsId + " ##########################");

			txManager.commit(txStatus);
		} catch (NullPointerException e) {
			if (txStatus != null) {
				txManager.rollback(txStatus);
			}
			log.error("Exception-", e);
			result.put("status", "fail");
		} catch (Exception e) {
			if (txStatus != null) {
				txManager.rollback(txStatus);
			}
			log.error("Exception-", e);
			result.put("status", "fail");
		} finally {
			txStatus = null;
			txManager = null;
		}

		return result;
	}

	public void saveLPData(List<Double>[] chValue, List<Integer> flag, String startlpdate, double baseValue, DLMSEMnVGtype parser) throws Exception {
		double[][] _lplist = new double[chValue.length][chValue[0].size()];
		for (int ch = 0; ch < _lplist.length; ch++) {
			for (int j = 0; j < _lplist[ch].length; j++) {
				if (chValue[ch].get(j) != null)
					_lplist[ch][j] = chValue[ch].get(j);
				else
					_lplist[ch][j] = 0.0;
			}
		}
		int[] _flag = new int[chValue[0].size()];
		for (int j = 0; j < _flag.length; j++) {
			_flag[j] = flag.get(j);
		}

		AbstractMDSaver saver = (AbstractMDSaver) DataUtil.getBean(Class.forName("com.aimir.fep.meter.saver.DLMSEMnVGtypeSaver"));
		saver.saveLPDataP(MeteringType.OnDemand, startlpdate.substring(0, 8), startlpdate.substring(8) + "00", _lplist, _flag, baseValue, parser.getMeter(), DeviceType.Modem, parser.getMeter().getModem().getDeviceSerial(), DeviceType.Meter, parser.getMeterID());
	}

}