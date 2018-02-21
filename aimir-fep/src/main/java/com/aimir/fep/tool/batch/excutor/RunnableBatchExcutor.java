/**
 * 
 */
package com.aimir.fep.tool.batch.excutor;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.fep.tool.batch.excutor.CallableBatchExcutor.CBE_RESULT_CONSTANTS;
import com.aimir.util.CalendarUtil;

/**
 * @author simhanger
 *
 */
public class RunnableBatchExcutor {
	private static Logger logger = LoggerFactory.getLogger(RunnableBatchExcutor.class);
	private int CORE_POOL_SIZE = 5; // 10개부터는 db connection 에러남.
	private final int MAXIMUM_POOL_SIZE = 10000;  // 의미없음
	private long KEEP_ALIVE_TIME = 1;
	private TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.MINUTES;

	private boolean isNowRunning = false;
	private ThreadPoolExecutor executor = null;
	private BatchRejectedExecutionHandler rejectedExecutionHandler;

	public RunnableBatchExcutor() {
	}

	public RunnableBatchExcutor(int corePoolSize) {
		this.CORE_POOL_SIZE = corePoolSize;
		rejectedExecutionHandler = new BatchRejectedExecutionHandler();
	}

//	public RunnableBatchExcutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit) {
//		this.CORE_POOL_SIZE = corePoolSize;
//		this.MAXIMUM_POOL_SIZE = maximumPoolSize;
//		this.KEEP_ALIVE_TIME = keepAliveTime;
//		this.KEEP_ALIVE_TIME_UNIT = unit;
//		rejectedExecutionHandler = new BatchRejectedExecutionHandler();
//	}
	
	public boolean execute(String title, List<IBatchRunnable> targetList) {
		boolean result = false;

		if (isNowRunning) {
			logger.info("##### RunnableBatchExcutor Task[{}] is already running...  #####", title);
			return false;
		}

		isNowRunning = true;

		Date startDate = new Date();
		long startTime = startDate.getTime();

		logger.info("########### START RunnableBatchExcutor : Thread Count = {}, Keep Alive Time = {}/{}  - {} ###############", CORE_POOL_SIZE, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT.name(), CalendarUtil.getDatetimeString(startDate, "yyyy-MM-dd HH:mm:ss"));

		if (targetList == null || targetList.size() <= 0) {
			logger.warn("Have no taget list. please check target list.");
		} else {
			int count = 0;
			logger.info("------ Excuted Target List. Total job = {} -----", targetList.size());
			for (IBatchRunnable job : targetList) {
				logger.info("{}. {}", ++count, job.getName());
			}
			logger.info("-----------------------------------------");

			/*
			 * INIT & Excute
			 */
			excutorStart(targetList);

			/*
			 * Logging
			 */
			logger.info(" ");
			
			if(0 < rejectedExecutionHandler.getSize()){
				count = 0;
				logger.info("=========== REJECTED EXECUTION LIST ==========");
				logger.info("------ Rejected Target List. Total job = {} -----", rejectedExecutionHandler.getSize());
				for (String failJobName : rejectedExecutionHandler.getList()) {
					logger.info("{}. {}", ++count, failJobName);
				}
				logger.info("-----------------------------------------");
				logger.info("=======================================");
				
			}
		}

		long endTime = System.currentTimeMillis();
		logger.info("FINISHED - Elapse Time : {}s", (endTime - startTime) / 1000.0f);

		logger.info("########### END RunnableBatchExcutor  ############");
		isNowRunning = false;

		result = true;

		return result;
	}

	private void excutorStart(List<IBatchRunnable> targetList) {
		if (targetList == null || targetList.size() <= 0) {
			logger.warn("Target List is null. please check your Targer list.");
		} else {
			try {
				// Excute Job.
				executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, new LinkedBlockingQueue<Runnable>(), rejectedExecutionHandler);
				for (IBatchRunnable job : targetList) {
					executor.execute(job);
				}
		        try {
		            executor.shutdown();
		            while (!executor.isTerminated()) {
		            }
		        }
		        catch (Exception e) {}

			} catch (Exception e) {
				logger.error("Exception-", e);
			} /*finally {
				if (executor != null) {
					executor.shutdown();
					try {
						if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
							executor.shutdownNow();
							if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
								logger.error("Pool did not terminate");
							}
						}
					} catch (InterruptedException ie) {
						executor.shutdownNow();
						Thread.currentThread().interrupt();
					}
				}
			}*/

		}
	}
}
