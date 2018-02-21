/**
 * (@)# ModebusDefaultInverterTable.java
 *
 * 2015. 10. 23.
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
package com.aimir.fep.meter.parser.ModbusInverterTable;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.fep.meter.parser.ModbusInverterTable.ModbusInverterDefaultVariable.MODBUS_DEFAULT_CODE;
import com.aimir.fep.protocol.emnv.frame.EMnVConstants;
import com.aimir.fep.util.DataUtil;

/**
 * @author nuri
 *
 */
public class ModebusDefaultInverterTable {
	private static Logger log = LoggerFactory.getLogger(ModebusDefaultInverterTable.class);
	Map<String, Object> obisMap = new LinkedHashMap<String, Object>();

	public Object getData() {
		return obisMap;
	}

	public Map<String, Object> setData(int pos, byte[] data, int dataCount) throws Exception {
		log.info("총 MODBUS_DEFAULT_CODE 갯수 ==> {}", dataCount);
		for (int i = 0; i < dataCount; i++) {
			// Data time
			byte[] dataTime_byte = new byte[EMnVConstants.MODEBUS_DATA_TIME_LEN];
			System.arraycopy(data, pos, dataTime_byte, 0, dataTime_byte.length);
			pos += EMnVConstants.MODEBUS_DATA_TIME_LEN;
			obisMap.put(MODBUS_DEFAULT_CODE.DATE.name() + "-" + i, DataUtil.getEMnvDate6Byte(dataTime_byte)); //날짜

			byte[] frequency_byte = new byte[EMnVConstants.MODEBUS_FREQUENCY_LEN];
			System.arraycopy(data, pos, frequency_byte, 0, frequency_byte.length);
			pos += EMnVConstants.MODEBUS_FREQUENCY_LEN;
			obisMap.put(MODBUS_DEFAULT_CODE.OUTPUT_FREQUENCY.name() + "-" + i, DataUtil.getIntTo2Byte(frequency_byte)); //주파수				

			byte[] voltage_byte = new byte[EMnVConstants.MODEBUS_VOLTAGE_LEN];
			System.arraycopy(data, pos, voltage_byte, 0, voltage_byte.length);
			pos += EMnVConstants.MODEBUS_VOLTAGE_LEN;
			obisMap.put(MODBUS_DEFAULT_CODE.OUTPUT_VOLTAGE.name() + "-" + i, DataUtil.getIntTo2Byte(voltage_byte)); //전압

			byte[] current_byte = new byte[EMnVConstants.MODEBUS_CURRENT_LEN];
			System.arraycopy(data, pos, current_byte, 0, current_byte.length);
			pos += EMnVConstants.MODEBUS_CURRENT_LEN;
			obisMap.put(MODBUS_DEFAULT_CODE.OUTPUT_CURRENT.name() + "-" + i, DataUtil.getIntTo2Byte(current_byte)); //전류
		}

		log.info("[PROTOCOL][METERING_DATA] SET_DATA => {}", obisMap.toString());
		return obisMap;
	}
}
