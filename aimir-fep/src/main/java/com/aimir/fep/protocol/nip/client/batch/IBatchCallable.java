/**
 * 
 */
package com.aimir.fep.protocol.nip.client.batch;

import java.util.Map;
import java.util.concurrent.Callable;

//import com.aimir.fep.protocol.nip.client.NotiPlug.NotiObserver;
import com.aimir.fep.protocol.nip.client.batch.CallableBatchExcutor.CBE_RESULT_CONSTANTS;

/**
 * @author simhanger
 *
 */
public interface IBatchCallable extends Callable<Map<CBE_RESULT_CONSTANTS, Object>> {
}
