/**
 * (@)# EMnVMDProcessor.java
 *
 * 2015. 5. 1.
 *
 * Copyright (c) 2013 NURITELECOM, Inc.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of 
 * NURITELECOM, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with NURITELECOM, Inc.
 *
 * For more information on this product, please see
 * http://www.nuritelecom.co.kr
 *
 */
package com.aimir.fep.protocol.emnv.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.aimir.fep.meter.EMnVMeterDataSaverMain;
import com.aimir.fep.protocol.emnv.frame.payload.EMnVMeteringDataFramePayLoad;
import com.aimir.fep.protocol.emnv.log.EMnVMDLogger;
import com.aimir.fep.protocol.fmp.processor.Processor;
import com.aimir.model.device.CommLog;

/**
 * @author simhanger
 *
 */
public class EMnVMDProcessor extends Processor {
	private static Logger log = LoggerFactory.getLogger(EMnVMDProcessor.class);

	@Autowired
	private EMnVMDLogger mdLogger;

	@Autowired
	private EMnVMeterDataSaverMain saverMain;

	/**
	 * 
	 */
	public EMnVMDProcessor() {
		log.debug("EMnVMDProcessor create~!!");
	}

	@Override
    public int processing(Object obj) throws Exception {

        // 로그 확인 편하도록....
        log.info("    ");
        log.info("    ");
        log.info("    ");
        log.info("############################## 로그확인 시작 ################################################");

        if (obj instanceof EMnVMeteringDataFramePayLoad) {
            EMnVMeteringDataFramePayLoad appData = (EMnVMeteringDataFramePayLoad) obj;
            saveMeasurementData(appData);
        } else {
            log.info("processing data obj does not EMnVMeteringDataFramePayLoad " + obj.getClass().getName());
            throw new Exception("Invalid data[" + obj.getClass().getName() + "] in Queue");
        }
        
        return 1;
    }
	
	public void processing(Object obj, CommLog commLog) throws Exception {

		// 로그 확인 편하도록....
		log.info("    ");
		log.info("    ");
		log.info("    ");
		log.info("############################## 로그확인 시작 ################################################");

		if (obj instanceof EMnVMeteringDataFramePayLoad) {
			EMnVMeteringDataFramePayLoad appData = (EMnVMeteringDataFramePayLoad) obj;
			saveMeasurementData(appData);
		} else {
			log.info("processing data obj does not EMnVMeteringDataFramePayLoad " + obj.getClass().getName());
			throw new Exception("Invalid data[" + obj.getClass().getName() + "] in Queue");
		}
	}

	@Override
	public void restore() throws Exception {

	}

	private void saveMeasurementData(EMnVMeteringDataFramePayLoad data) {
		log.debug("saveMeasurementData Start - getSourceAddress={}", data.getSourceAddress());

		try {
			boolean result = saverMain.save(data);
			log.debug("finished Saving {} - DeviceId={}", result ? "Success":"Fail", data.getSourceAddress());
		} catch (Exception ex) {
			log.error("saveMeasurementData failed - DeviceId=" + data.getSourceAddress(), ex);
			// 2010.08.10 백업 데이타를 MDHistoryData에서 MDData로 변경.
			mdLogger.writeObject(data);
		}
	}
}
