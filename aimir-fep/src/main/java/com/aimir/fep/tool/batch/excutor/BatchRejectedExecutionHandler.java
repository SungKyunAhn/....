/**
 * 
 */
package com.aimir.fep.tool.batch.excutor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author simhanger
 *
 */
public class BatchRejectedExecutionHandler implements RejectedExecutionHandler {
	private List<String> rejectList;

	public BatchRejectedExecutionHandler() {
		rejectList = new ArrayList<String>();
	}

	@Override
	public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
		rejectList.add(((IBatchRunnable) r).getName());
	}

	public int getSize() {
		return rejectList.size();
	}

	public List<String> getList() {
		return rejectList;
	}
}
