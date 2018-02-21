package com.aimir.fep.trap.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author <a href="mailto:jaehwang@nuritelecom.com">Jae-Hwang Kim</a>
 */
public class ScheduledOperation {

	static Log log = LogFactory.getLog(ScheduledOperation.class);

	protected final OperationTaskQueue tasks = new OperationTaskQueue();

	//final private ThreadGroup threadGroup;
	private ThreadGroup threadGroup = null;
	private Thread[] threads = null;
    private String threadGroupName;
	
	public static int threadGroupNo = 0;
	
	synchronized private int getThreadGroupNo() {
		return threadGroupNo++;
	}

    private String getThreadGroupName() {
        return threadGroupName;
    }

	public ScheduledOperation() {
		this(1);
	}
	
	public ScheduledOperation(int thrnum) {
		threadGroup = 
			new ThreadGroup("ScheduledOperation "+ getThreadGroupNo()) {
				public void uncaughtException(Thread t, Throwable e) {
					log.warn("Thread "+t+" died: "+e);
				}
			};
		
		threads = new Thread[thrnum];

		for(int i = 0; i < thrnum; i++) {
			threads[i] = new Thread(threadGroup, new Runnable() {
                //long startTime = 0;
                //long takeEndTime = 0;
                //long takeTime = 0;
                //long runEndTime = 0;
                //long runTime = 0;
                //long totalTime = 0;

				public void run() {
					try {
						OperationTask t;
						for ( ; ; ) {
                            t = tasks.take();
							if(t == null) {
								log.debug("ScheduledOperation: task is null");
								continue;
							}
							t.run();
						}
					} catch (InterruptedException ex) {
						log.debug("ScheduledOperation: return");
						log.error("ScheduledOperation: return",ex);
                        ex.printStackTrace();
						//return;
					} catch (Exception e) { // die
						log.warn("ScheduledOperation: "+e);
					}
				}
			});

			threads[i].start();
		}
	}

    public ScheduledOperation(int thrnum,String threadGroupName)
    {
        this.threadGroupName = threadGroupName;
        threadGroup =
            new ThreadGroup("ScheduledOperation "+ getThreadGroupNo()
                    + ":" + getThreadGroupName())
            {
                public void uncaughtException(Thread t, Throwable e) {
                    log.warn("Thread "+t+" died: "+e);
                }
            };

        threads = new Thread[thrnum];

        for(int i = 0; i < thrnum; i++) {
            threads[i] = new Thread(threadGroup, new Runnable() {
                public void run() {
                    try {
                        OperationTask t;
                        for ( ; ; ) {
                            t = tasks.take();
                            if(t == null) {
                                log.debug("ScheduledOperation: task is null");
                                continue;
                            }
                            t.run();
                        }
                    } catch (InterruptedException ex) {
                        log.debug("ScheduledOperation: return");
						log.error("ScheduledOperation: return",ex);
                        ex.printStackTrace();
                        //return;
                    } catch (Exception e) { // die
                        log.warn("ScheduledOperation: "+e);
                    }
                }
            });

            threads[i].start();
        }
	}

	public void stop () {
		if (threadGroup!=null) {
			// threadGroup.interrupt();
			for (int i=0; threads!=null && i<threads.length; i++) {
				if (threads[i]!=null && 
									Terminator.terminate(threads[i], 1000)) {
					threads[i] = null;
				} else {
					log.warn("stop: fail: "+threads[i]);
				}
			}
			threadGroup.destroy();
			threadGroup = null;
		}
	}

	/**  OperationTaskQueue�� OperationTask�� �߰��ϰ�, OperationTask�� 
	 * ������ �Ŀ� �����Ѵ�.
	 */
	public void putOperationTask(OperationTask t) throws Failure {

		tasks.put(t);

		try {
			t.awaitCompletion();
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt(); // propagate
			throw new Failure(); 				// convert to failure exception
		} catch (Exception e) {
			log.warn("putOperationTask: "+e);
		}

		Failure f = t.getException();
		if (f != null) {
			throw f;
		}
	}

	/**  OperationTaskQueue�� OperationTask�� �߰��� �ϰ� ��� �����Ѵ�.
	 * Operation�� ���� ���δ� OperationTask�� <code>isDone</code>�޼ҵ带
	 * ���� �� �� �ִ�.
	 */
	public void putOperationTaskNoWait(OperationTask t) throws Failure {
		tasks.put(t);
	}

    public int getSizeOfQueue()
    {
        return tasks.getSizeOfQueue();
    }
}
