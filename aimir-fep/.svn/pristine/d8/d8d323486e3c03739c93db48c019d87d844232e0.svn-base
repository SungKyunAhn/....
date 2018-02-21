package com.aimir.fep.meter.parser;

import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.meter.parser.ModbusInverterTable.ModbusInverterN700EVariable.MODBUS_HYUNDAI_CODE;
import com.aimir.fep.meter.parser.ModbusInverterTable.ModebusInverterCommonTable;
import com.aimir.fep.protocol.emnv.exception.EMnVSystemException;
import com.aimir.fep.protocol.emnv.exception.EMnVSystemException.EMnVExceptionReason;
import com.aimir.fep.protocol.emnv.frame.EMnVConstants;
import com.aimir.fep.protocol.emnv.frame.EMnVConstants.EMnVMeteringDataType;
import com.aimir.fep.protocol.emnv.frame.EMnVConstants.EMnVModebusVendorType;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.util.TimeUtil;

public class ModbusEMnVHyundai extends MeterDataParser implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(ModbusEMnVHyundai.class);
	private EMnVMeteringDataType meteringDataType; // Billing or Load Profile
	private LPData[] lpData = null;
	private EMnVModebusVendorType vendorType;
	private String stationId;
	LinkedHashMap<MODBUS_HYUNDAI_CODE, Map<String, Object>> result = new LinkedHashMap<MODBUS_HYUNDAI_CODE, Map<String, Object>>();

	@Override
	public void parse(byte[] data) throws Exception {

		// Inverter는 LP타임이 없으므로 파싱 시간을 기준으로 삼는다.
		Calendar cal = Calendar.getInstance();
		String parsingTime = TimeUtil.getCurrentTime();

		// 로그 확인 편하도록....
		log.info("    ");
		log.info("    ");
		log.info("    ");
		log.info("################ HYUNDAI Inverter 로그확인 시작 ({}) #########################", parsingTime);
		log.info("ModbusEMnV-HYUNDAI parse:[{}] [{}]", meteringDataType != null ? meteringDataType.name() : "", Hex.decode(data));

		int pos = 0;
		int dataCount = 0;

		// Vendor Type
		byte[] vendorType_byte = new byte[EMnVConstants.MODBUS_VENDOR_TYPE_LEN];
		System.arraycopy(data, pos, vendorType_byte, 0, vendorType_byte.length);
		vendorType = EMnVModebusVendorType.getItem(vendorType_byte[0]);
		pos += EMnVConstants.MODBUS_VENDOR_TYPE_LEN;
		log.info("[PROTOCOL][MODBUS] VENDOER_TYPE(1):[{}] ==> HEX=[{}]", vendorType, Hex.decode(vendorType_byte));

		// Station id
		byte[] station_byte = new byte[EMnVConstants.MODBUS_STATION_BYTE_LEN];
		System.arraycopy(data, pos, station_byte, 0, station_byte.length);
		stationId = Hex.getHexDump(station_byte).trim();
		pos += EMnVConstants.MODBUS_STATION_BYTE_LEN;
		log.info("[PROTOCOL][MODBUS] STATION_BYTE(1):[{}] ==> HEX=[{}]", stationId, Hex.decode(station_byte));

		// Data count
		byte[] dataCount_byte = new byte[EMnVConstants.MODBUS_DATA_COUNT_LEN];
		System.arraycopy(data, pos, dataCount_byte, 0, dataCount_byte.length);
		dataCount = DataUtil.getIntTo2Byte(dataCount_byte);
		pos += EMnVConstants.MODBUS_DATA_COUNT_LEN;
		log.info("[PROTOCOL][MODBUS] DATA_COUNT  (2):[{}] ==> HEX=[{}]", dataCount, Hex.decode(dataCount_byte));

		log.info("총 MODBUS_HYUNDAI_CODE 갯수 ==> {}", dataCount);

		// 가짜 LP타임 저장
		parsingTime = new ModebusInverterCommonTable().resetLpTime(cal);
		setMCodeResult(MODBUS_HYUNDAI_CODE.DATE, parsingTime);

		// Data parsing
		for (int i = 0; i < dataCount; i++) {
			log.info("-----------------------------------------------------------------------");
			// ADDRESS
			byte[] address = new byte[EMnVConstants.MODBUS_ADDRESS_LEN];
			System.arraycopy(data, pos, address, 0, address.length);
			pos += EMnVConstants.MODBUS_ADDRESS_LEN;
			MODBUS_HYUNDAI_CODE mCode = MODBUS_HYUNDAI_CODE.getItem(Hex.decode(address));

			// DATA
			byte[] mData = new byte[EMnVConstants.MODBUS_DATA_LEN];
			System.arraycopy(data, pos, mData, 0, mData.length);
			pos += EMnVConstants.MODBUS_DATA_LEN;

			Object value = null;

			// 정의되지 않은 값이 들어온경우
			if (mCode == null) {
				log.info("[{}] Unknown CODE !!! [{}] ==> {}={}, HEX=[{}]", new Object[] { i, Hex.decode(address), "?", value, Hex.decode(mData) });
				continue;
			}

			switch (mCode) {
			case OUTPUT_FREQUENCY_MONITOR:
				value = DataUtil.getIntTo2Byte(mData);
				break;
			case OUTPUT_CURRENT_MONITOR:
				value = DataUtil.getIntTo2Byte(mData);
				break;
			case OUTPUT_VOLTAGE_MONITOR:
				value = DataUtil.getIntTo2Byte(mData);
				break;
			case ROTATION_DIRECTION_MONITOR:
				value = Hex.decode(mData);
				break;
			case PID_FEEDBACK_MONITOR:
				value = Hex.decode(mData);
				break;
			case INTELLIGENT_INPUT_TERMINAL_MONITOR:
				value = Hex.decode(mData);
				break;
			case INTELLIGENT_OUTPUT_TERMINAL_MONITOR:
				value = Hex.decode(mData);
				break;
			case SCALED_OUTPUT_FREQUENCY_MONITOR:
				value = Hex.decode(mData);
				break;				
			case POWER_CONSUMPTION_MONITOR:
				value = Hex.decode(mData);
				break;
			case ACCUMULATED_TIME_MONITOR_DURING_RUN_HR:
				value = Hex.decode(mData);
				break;
			case ACCUMULATED_TIME_MONITOR_DURING_RUN_MIN:
				value = Hex.decode(mData);
				break;
			case DC_LINK_VOLTAGE_MONITOR:
				value = Hex.decode(mData);
				break;

			default:
				throw new EMnVSystemException(EMnVExceptionReason.UNKNOWN_ADDR);
			}

			setMCodeResult(mCode, value);

			log.info("[{}][PROTOCOL][METERING_DATA] MODBUS_HYUNDAI_CODE[{}] ==> {}={}, HEX=[{}]", new Object[] { i, Hex.decode(address), mCode.name(), value, Hex.decode(mData) });
		}

		log.debug("### MODBUS_HYUNDAI_CODE TOTAL ==> {}", result.toString());
		log.info("### Number_of_MODBUS={} Last POS={}", new Object[] { dataCount, pos });

		setInverterInfo();
	}

	private void setMCodeResult(MODBUS_HYUNDAI_CODE mCode, Object value) {
		Map<String, Object> mMap = new HashMap<String, Object>();
		mMap.put("VALUE", value);
		if (mCode.getUnitConst() != null) {
			mMap.put("UNIT_CONST", mCode.getUnitConst());
		}
		if (mCode.getUnit() != null) {
			mMap.put("UNIT", mCode.getUnit());
		}

		result.put(mCode, mMap);
	}

	public void setInverterInfo() {
		try {
			// 인버터 아이디 생성
			String meterId = getCreateInverterId();
			meter.setMdsId(meterId);

			// Inverter는 시간정보가 없기때문에 현재 시간을 미터시간으로 설정함.
			meterTime = TimeUtil.getCurrentTime().substring(0, 12);
		} catch (Exception e) {
			log.error("ERROR - ", e);
		}
	}

	/**
	 * 인버터 아이디 생성 ~!! 모뎀아이디 + "-" + 제조사코드 + Station Address
	 * 
	 * @return
	 */
	public String getCreateInverterId() {
		String modemId = meter.getModem().getDeviceSerial();
		String venderId = String.format("%02d", vendorType.getValue());
		String inverterId = modemId + "-" + venderId + stationId;

		log.info("MDSID = {}, modem id = {}, vendor id = {}, station id = {}", new Object[] { inverterId, modemId, venderId, stationId });

		return inverterId;
	}

	/**
	 * System.out.println() 으로 HEX 출력.
	 * 
	 * @param data
	 * @param tPos
	 * @param showLength
	 */
	public void printHexByteString(byte[] data, int tPos, int showLength) {
		int loggingLenth = (data.length - tPos) < showLength ? data.length - tPos : showLength;

		byte[] logging = new byte[showLength];
		System.arraycopy(data, tPos, logging, 0, loggingLenth);
		log.info("### SHOW HEX POS[" + tPos + "] 부터 " + loggingLenth + "byte ==> " + Hex.getHexDump(logging));
	}

	public void postParse() {
		setLPData();
	}

	public LPData[] getLPData() {

		return lpData;
	}

	public void setLPData() {
		try {
			Map<String, LPData> lpDataMap = new HashMap<String, LPData>();
			Map<String, Object> mCodeMap = null;
			double outputCurrent = 0.0;
			double outputFrequency = 0.0;
			double outputVoltate = 0.0;

			// 일단 출력 주파수를 lp로 지정한다. 차후 아닐경우 수정할것.
			Double lp = 0.0;
			Double lpValue = 0.0;
			LPData lpEl = null;

			// 출력 주파수
			mCodeMap = result.get(MODBUS_HYUNDAI_CODE.OUTPUT_FREQUENCY_MONITOR);
			if (mCodeMap != null) {
				outputFrequency = (double) (Integer) mCodeMap.get("VALUE");
				lp = outputFrequency;
				if (mCodeMap.get("UNIT_CONST") != null) {
					outputFrequency = outputFrequency * Double.parseDouble((String) mCodeMap.get("UNIT_CONST"));
					lpValue = outputFrequency;
				}
			}
			// 출력 전류
			mCodeMap = result.get(MODBUS_HYUNDAI_CODE.OUTPUT_CURRENT_MONITOR);
			if (mCodeMap != null) {
				outputCurrent = (double) (Integer) mCodeMap.get("VALUE");
				if (mCodeMap.get("UNIT_CONST") != null) {
					outputCurrent = outputCurrent * Double.parseDouble((String) mCodeMap.get("UNIT_CONST"));
				}
			}
			// 출력 전압
			mCodeMap = result.get(MODBUS_HYUNDAI_CODE.OUTPUT_VOLTAGE_MONITOR);
			if (mCodeMap != null) {
				outputVoltate = (double) (Integer) mCodeMap.get("VALUE");
				if (mCodeMap.get("UNIT_CONST") != null) {
					outputVoltate = outputVoltate * Double.parseDouble((String) mCodeMap.get("UNIT_CONST"));
				}
			}

			lpEl = new LPData((result.get(MODBUS_HYUNDAI_CODE.DATE)).get("VALUE").toString(), lp, lpValue);
			lpEl.setCh(new Double[] { outputCurrent, outputFrequency, outputVoltate });
			lpDataMap.put(lpEl.getDatetime(), lpEl);

			lpData = new LPData[1];
			lpData[0] = lpEl;

			log.info("######################## LpData.length:" + lpData.length);
		} catch (Exception e) {
			log.error("ERROR-", e);
		}
	}

	@Override
	public byte[] getRawData() {
		return null;
	}

	@Override
	public int getLength() {
		return 0;
	}

	@Override
	public Double getMeteringValue() {
		return null;
	}

	@Override
	public String toString() {
		return null;
	}

	@Override
	public LinkedHashMap<?, ?> getData() {
		return null;
	}

	@Override
	public int getFlag() {
		return 0;
	}

	@Override
	public void setFlag(int flag) {
		meteringDataType = EMnVMeteringDataType.getItem(DataUtil.getByteToInt(flag));
	}

	public String getMeterID() {
		return this.meter.getMdsId();
	}

}
