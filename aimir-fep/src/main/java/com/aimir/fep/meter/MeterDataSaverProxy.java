/**
 * (@)# MeterDataSaverProxy.java
 *
 * 2016. 6. 8.
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
package com.aimir.fep.meter;

import com.aimir.fep.meter.entry.IMeasurementData;

/**
 * 추상클래스인 AbstractMDSaver의 save() 메서드가 protected로 되어 있기 때문에 다른 패키지에서 호출을 할수 없어서
 * 만든 클래스
 * 
 * @author simhanger
 *
 */
public class MeterDataSaverProxy {

	public boolean save(AbstractMDSaver saver, IMeasurementData mData) throws Exception {
		return saver.save(mData);
	}
}
