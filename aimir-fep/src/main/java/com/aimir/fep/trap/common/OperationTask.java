package com.aimir.fep.trap.common;

import EDU.oswego.cs.dl.util.concurrent.Latch;

/** get/set operation
 */
public abstract class OperationTask implements Runnable {

	protected Failure exception = null;			// to relay out
	protected final Latch done = new Latch();	// status indicator

	/** Operation body
	 */
	abstract public void access() throws Failure; // get or set
	

	abstract public Object getResult();

	public final void run() {
		try  { 
			access(); 
		} catch (Failure ex) { 
			setException(ex); 
		} finally { 
			done.release(); 
		}
	}
	

	final void awaitCompletion() throws InterruptedException {
		done.acquire();
	}


	public final boolean isDone(long msecs) throws InterruptedException {
		return done.attempt(msecs);
	}

	synchronized final Failure getException() { 
		return exception; 
	}

	synchronized final void setException(Failure f) { 
		exception = f; 
	}
}
