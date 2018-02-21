package com.aimir.fep.trap.common;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import EDU.oswego.cs.dl.util.concurrent.Semaphore;


public class OperationTaskQueue {

	protected final Semaphore available = new Semaphore(0);

	List<OperationTask> taskList = Collections.synchronizedList(new ArrayList<OperationTask>());

	void put(OperationTask t) {
		insert(t);
		available.release();
	}

	OperationTask take() throws InterruptedException {
		available.acquire();
		return extract();
	}

	synchronized void insert(OperationTask t) {
		taskList.add(t);
	}

	synchronized OperationTask extract() {
		OperationTask t = null;

		try {
			t = (OperationTask)taskList.remove(0);
		} catch (Exception e) { }

		return t;
	}

    public int getSizeOfQueue()
    {
        return taskList.size();
    }
}
