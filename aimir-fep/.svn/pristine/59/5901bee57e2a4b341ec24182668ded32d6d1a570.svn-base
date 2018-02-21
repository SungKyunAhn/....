/**
 * 
 */
package com.aimir.fep.tool.batch.excutor;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.util.CalendarUtil;

/**
 * @author simhanger
 *
 */
public class CallableBatchExcutor {
	private static Logger logger = LoggerFactory.getLogger(CallableBatchExcutor.class);
	private int CORE_POOL_SIZE = 5; // 10개부터는 db connection 에러남.
	private final int MAXIMUM_POOL_SIZE = 10000;  // 의미없음
	private final int KEEP_ALIVE_TIME = 1;
	private final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.MINUTES;
	private final int AWAIT_TIME_OUT = 1;
	private final TimeUnit AWAIT_TIME_OUT_TIME_UNIT = TimeUnit.MINUTES;

	private boolean isNowRunning = false;
	private List<Map<CBE_RESULT_CONSTANTS, Object>> successList = new LinkedList<Map<CBE_RESULT_CONSTANTS, Object>>();
	private List<Map<CBE_RESULT_CONSTANTS, Object>> failList = new LinkedList<Map<CBE_RESULT_CONSTANTS, Object>>();
	private ThreadPoolExecutor executor = null;

	public enum CBE_RESULT_CONSTANTS {
		TARGET_ID, RESULT_STATE, RESULT_VALUE;
	}

	public enum CBE_STATUS_CONSTANTS {
		SUCCESS, FAIL;
	}

	public CallableBatchExcutor(int corePoolSize) {
		this.CORE_POOL_SIZE = corePoolSize;
	}
	
	public boolean execute(String title, List<? extends IBatchCallable> targetList) {
		boolean result = false;

		if (isNowRunning) {
			logger.info("##### CallableBatchExcutor Task[{}] is already running...  #####", title);
			return false;
		}

		logger.debug("CallableBatchExcutor Excute - TargetList={}", Arrays.toString(targetList.toArray()));

		isNowRunning = true;

		Date startDate = new Date();
		long startTime = startDate.getTime();

		logger.info("########### START CallableBatchExcutor - {}, core size = {} ###############", CalendarUtil.getDatetimeString(startDate, "yyyy-MM-dd HH:mm:ss"), CORE_POOL_SIZE);

		if (targetList == null || targetList.size() <= 0) {
			logger.warn("Have no taget list. please check target list.");
		} else {
			successList.clear();
			failList.clear();

			/*
			 * INIT & Excute
			 */
			initCallableExcute(targetList);

			/*
			 * Logging
			 */
			logger.info(" ");
			logger.info("=========== EXCUTE SUCCESS LIST ({}) ==========", successList.size());

			int count = 1;
			if (successList != null && 0 < successList.size()) {
				for (Map<CBE_RESULT_CONSTANTS, Object> map : successList) {
					logger.info("{}. TARGET_ID={}, RESULT={}, RESULT_VALUE={}", count, map.get(CBE_RESULT_CONSTANTS.TARGET_ID), map.get(CBE_RESULT_CONSTANTS.RESULT_STATE), map.get(CBE_RESULT_CONSTANTS.RESULT_VALUE));
					count++;
				}
			} else {
				logger.info("There is no list of successful.");
			}
			logger.info("===============================================");

			count = 1;
			logger.info("=========== EXCUTE FAIL LIST ({}) =============", failList.size());
			if (failList != null && 0 < failList.size()) {
				for (Map<CBE_RESULT_CONSTANTS, Object> map : failList) {
					logger.info("{}. TARGET_ID={}, RESULT={}, RESULT_VALUE={}", count, map.get(CBE_RESULT_CONSTANTS.TARGET_ID), map.get(CBE_RESULT_CONSTANTS.RESULT_STATE), map.get(CBE_RESULT_CONSTANTS.RESULT_VALUE));
					count++;
				}

				logger.info("===============================================");
				;
				logger.info("###############################################");
			} else {
				logger.info("There is no list of failure.");
			}

		}

		long endTime = System.currentTimeMillis();
		logger.info("FINISHED - Elapse Time : {}s", (endTime - startTime) / 1000.0f);

		logger.info("########### END CallableBatchExcutor  ############");
		isNowRunning = false;

		result = true;

		return result;
	}

	private void initCallableExcute(List<? extends IBatchCallable> targetList) {
		if (targetList == null || targetList.size() <= 0) {
			logger.warn("Target List is null. please check your Targer list.");
		} else {
			try {
				// Excute Job.
				executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, new LinkedBlockingQueue<Runnable>());
				List<Future<Map<CBE_RESULT_CONSTANTS, Object>>> futureL = executor.invokeAll(targetList, 3, TimeUnit.MINUTES); // tasks, long timeout, TimeUnit unit

				for (Future<Map<CBE_RESULT_CONSTANTS, Object>> resultFuture : futureL) {
					Map<CBE_RESULT_CONSTANTS, Object> ht = resultFuture.get(AWAIT_TIME_OUT, AWAIT_TIME_OUT_TIME_UNIT); // the maximum time to wait
					if (resultFuture.isDone()) {
						if (((CBE_STATUS_CONSTANTS) ht.get(CBE_RESULT_CONSTANTS.RESULT_STATE)) == CBE_STATUS_CONSTANTS.SUCCESS) {
							successList.add(ht);
						} else {
							failList.add(ht);
						}
					} else if (resultFuture.isCancelled()) {
						logger.info("###### Future is Cancelled ==> " + ht.get("target").toString());
						failList.add(ht);
					} else {
						logger.info("###### Future is unknon canceled ==> " + ht.get("target").toString());
						failList.add(ht);
					}
				}
			} catch (Exception e) {
				logger.error("Exception-", e);
			} finally {
				if (executor != null) {
					executor.shutdown();
					try {
						if (!executor.awaitTermination(AWAIT_TIME_OUT, TimeUnit.SECONDS)) {
							executor.shutdownNow();
							if (!executor.awaitTermination(AWAIT_TIME_OUT, TimeUnit.SECONDS)) {
								logger.error("Pool did not terminate");
							}
						}
					} catch (InterruptedException ie) {
						executor.shutdownNow();
						Thread.currentThread().interrupt();
					}
				}
			}

		}
	}

	public List<Map<CBE_RESULT_CONSTANTS, Object>> getSuccessList() {
		return successList;
	}

	public List<Map<CBE_RESULT_CONSTANTS, Object>> getFailList() {
		return failList;
	}

	public List<Map<CBE_RESULT_CONSTANTS, Object>> getAllList() {
		List<Map<CBE_RESULT_CONSTANTS, Object>> allList = new LinkedList<>();
		allList.addAll(successList);
		allList.addAll(failList);
		return allList;
	}

	/**
	 * 추후 독립 배치로 돌릴때 사용.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}
