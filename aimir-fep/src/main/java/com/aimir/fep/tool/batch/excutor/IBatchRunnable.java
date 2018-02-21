/**
 * 
 */
package com.aimir.fep.tool.batch.excutor;

import com.aimir.constants.CommonConstants.ResultStatus;

/**
 * @author simhanger
 *
 */
public interface IBatchRunnable extends Runnable {
	public String getName();

	public void printResult(String title, ResultStatus status, String desc);
}
