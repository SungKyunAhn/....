/**
 * 
 */
package com.aimir.fep.tool.batch.job;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.fep.protocol.fmp.common.Target;
import com.aimir.fep.tool.batch.excutor.IBatchRunnable;

/**
 * @author simhanger
 *
 */
public class MeterOTARunnableJobForEVN implements IBatchRunnable {
	private static Logger logger = LoggerFactory.getLogger(MeterOTARunnableJobForEVN.class);
	private Target target;
	private Map<String, Object> params;

	public MeterOTARunnableJobForEVN(Target target, Map<String, Object> params) {
		this.target = target;
		this.params = params;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.aimir.fep.tool.batch.excutor.IBatchRunnable#getName()
	 */
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.aimir.fep.tool.batch.excutor.IBatchRunnable#printResult(java.lang.String, com.aimir.constants.CommonConstants.ResultStatus, java.lang.String)
	 */
	@Override
	public void printResult(String title, ResultStatus status, String desc) {
		// TODO Auto-generated method stub

	}

}
