/**
 * 
 */
package com.aimir.fep.tool.batch.excutor;

import java.util.Map;
import java.util.concurrent.Callable;

import com.aimir.fep.tool.batch.excutor.CallableBatchExcutor.CBE_RESULT_CONSTANTS;

//import com.aimir.fep.protocol.nip.client.NotiPlug.NotiObserver;

/**
 * @author simhanger
 *
 */
public interface IBatchCallable extends Callable<Map<CBE_RESULT_CONSTANTS, Object>> {
}
