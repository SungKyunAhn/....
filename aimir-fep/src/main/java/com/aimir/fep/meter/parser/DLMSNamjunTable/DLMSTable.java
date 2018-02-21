package com.aimir.fep.meter.parser.DLMSNamjunTable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.command.conf.DLMSMeta.CONTROL_STATE;
import com.aimir.fep.command.conf.DLMSMeta.LOAD_CONTROL_STATUS;
import com.aimir.fep.meter.parser.DLMSNamjunTable.DLMSVARIABLE.DLMS_CLASS;
import com.aimir.fep.meter.parser.DLMSNamjunTable.DLMSVARIABLE.DLMS_CLASS_ATTR;
import com.aimir.fep.meter.parser.DLMSNamjunTable.DLMSVARIABLE.DLMS_TAG_TYPE;
import com.aimir.fep.meter.parser.DLMSNamjunTable.DLMSVARIABLE.ENERGY_LOAD_PROFILE;
import com.aimir.fep.meter.parser.DLMSNamjunTable.DLMSVARIABLE.EVENT_LOG;
import com.aimir.fep.meter.parser.DLMSNamjunTable.DLMSVARIABLE.OBIS;
import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.model.device.Meter;

public class DLMSTable {
	private static Log log = LogFactory.getLog(DLMSTable.class);

	private DLMSHeader dlmsHeader = new DLMSHeader();
	private List<DLMSTag> dlmsTags = new ArrayList<DLMSTag>();
	private Meter meter;
	
	public DLMSHeader getDlmsHeader() {
		return dlmsHeader;
	}

	public void setDlmsHeader(DLMSHeader dlmsHeader) {
		this.dlmsHeader = dlmsHeader;
	}

	public List<DLMSTag> getDlmsTags() {
		return dlmsTags;
	}

	public void setDlmsTags(List<DLMSTag> dlmsTags) {
		this.dlmsTags = dlmsTags;
	}

	public void addDlmsTag(DLMSTag tag) {
		this.dlmsTags.add(tag);
	}

	public void setObis(String obisCode) {
		this.dlmsHeader.setObis(obisCode);
	}

	public void setClazz(int clazz) {
		this.dlmsHeader.setClazz(clazz);
	}

	public void setAttr(int attr) {
		this.dlmsHeader.setAttr(attr);
	}

	public void setLength(int length) {
		this.dlmsHeader.setLength(length);
	}
	
	public void setMeter(Meter meter) {
		this.meter = meter;
	}

	public void parseDlmsTag(byte[] data) throws Exception {

		int len = 0;
		int pos = 0;
		DLMSTag dlmsTag = null;
		byte[] bx = null;

		while (pos != data.length) {
			//       log.debug("LEN[" + data.length + "] POS[" + pos + "]");
			dlmsTag = new DLMSTag();
			dlmsTag.setTag(data[pos]);
			pos += 1;

			/*
			 * BitString, OctetString, VisibleString 태그에 대해서만 data[1] 바이트를
			 * 확장 길이로 사용하고 데이타에 대한 길이이다.
			 * 단, 0x80 으로 and연산으로 0x80 이 되면 0x7F 로 and을 하여 길이를 구한다.
			 */
			if (data.length > pos) {
				if (dlmsTag.getTag() != DLMS_TAG_TYPE.Boolean && (data[pos] & 0x80) == 0x80) {
					len = (data[pos] & 0x7F);
					pos += 1;
				} else {
					if (dlmsTag.getTag() == DLMS_TAG_TYPE.OctetString || dlmsTag.getTag() == DLMS_TAG_TYPE.VisibleString) {
						len = DataUtil.getIntToByte(data[pos]);
						pos += 1;
					} else if (dlmsTag.getTag() == DLMS_TAG_TYPE.BitString) {
						int bitLen = DataUtil.getIntToByte(data[pos]);

						if (bitLen > 0) {
							if (bitLen % 8 == 0) {
								len = bitLen / 8;
							} else {
								len = (bitLen / 8) + 1;
							}
						} else {
							len = 0;
						}
						pos += 1;
					} else {
						len = dlmsTag.getTag().getLen();
					}
				}
			} else
				continue;
			/*
			if (dlmsTag.getTag() == DLMS_TAG_TYPE.OctetString ||
			        dlmsTag.getTag() == DLMS_TAG_TYPE.VisibleString) {
			    len = DataUtil.getIntToByte(data[pos]);
			    pos += 1;
			}
			else len = dlmsTag.getTag().getLenth();
			*/

			bx = new byte[len];

			// pos와 data의 길이 합이 data.length를 넘어갈 수도 있기 때문에 비교를 한 후에 arrayindexout 예외가 발생하지 않도록 한다.
			if (pos + len > data.length)
				break;

			System.arraycopy(data, pos, bx, 0, bx.length);
			pos += bx.length;

			dlmsTag.setLength(len);
			dlmsTag.setData(bx);
			log.debug("OBIS[" + dlmsHeader.getObis().name() + "]" + dlmsTag.toString());

			// 예외로 LOAD_PROFILE이고 태그가 Array인 경우는 추가하지 않는다.
			if ((dlmsHeader.getObis() == OBIS.ENERGY_LOAD_PROFILE || dlmsHeader.getObis() == OBIS.DAILY_LOAD_PROFILE || dlmsHeader.getObis() == OBIS.MONTHLY_BILLING || dlmsHeader.getObis() == OBIS.STANDARD_EVENT || dlmsHeader.getObis() == OBIS.RELAY_EVENT || dlmsHeader.getObis() == OBIS.FRAUDDETECTIONLOGEVENT || dlmsHeader.getObis() == OBIS.TIME_CHANGE_BEFORE
					|| dlmsHeader.getObis() == OBIS.TIME_CHANGE_AFTER || dlmsHeader.getObis() == OBIS.TAMPER_EVENT)
					&& dlmsTag.getTag() == DLMS_TAG_TYPE.Array)
				continue;
			
			dlmsTags.add(dlmsTag);
		}
	}

	/**
	 * class, attr에 따라 데이타의 개수와 유형이 달라진다.
	 * 
	 * @return
	 */
	public Map<String, Object> getData() {
		Map<String, Object> ret = new LinkedHashMap<String, Object>(16, 0.75f, false);
		DLMS_CLASS clazz = dlmsHeader.getClazz();
		DLMS_CLASS_ATTR attr = dlmsHeader.getAttr();
		OBIS obis = dlmsHeader.getObis();
		//		log.debug("CLAZZ[" + clazz + "] ATTR[" + attr + "] OBIS[" + obis + "] TAG_SIZE[" + dlmsTags.size() + "]");
		switch (clazz) {
		case G3_PLC_6LoWPAN:
			switch (attr) {
			case ADP_WEAK_LQI_VALUE:
				Object o = dlmsTags.get(0).getValue();
				if (o instanceof Integer) {
					Integer val = (Integer) o;
					val = val.intValue();
					ret.put(OBIS.WEAK_LQI_VALUE.getName(), getG3_PLC_SNR(val));
				}
				break;
			default:
				break;
			}
			break;
		case DATA:
			switch (attr) {
			case DATA_ATTR01:
				if (obis == OBIS.DEVICE_INFO && dlmsTags.size() != 0) {
					ret.put(OBIS.DEVICE_INFO.getName(), dlmsTags.get(0).getOCTET().toString());
				}
				if (obis == OBIS.MANUFACTURE_SERIAL && dlmsTags.size() != 0) {
					ret.put(OBIS.MANUFACTURE_SERIAL.getName(), dlmsTags.get(0).getOCTET().toString());
				}
				if (obis == OBIS.SERVICEPOINT_SERIAL && dlmsTags.size() != 0) {
					ret.put(OBIS.SERVICEPOINT_SERIAL.getName(), dlmsTags.get(0).getOCTET().toString());
				}
				if (obis == OBIS.METER_MODEL && dlmsTags.size() != 0) {
					ret.put(OBIS.METER_MODEL.getName(), dlmsTags.get(0).getOCTET().toString());
				}
				if (obis == OBIS.FW_VERSION1 && dlmsTags.size() != 0) {
					ret.put(OBIS.FW_VERSION1.getName(), dlmsTags.get(0).getOCTET().toString());
				}
				if (obis == OBIS.FW_VERSION2 && dlmsTags.size() != 0) {
					ret.put(OBIS.FW_VERSION2.getName(), dlmsTags.get(0).getOCTET().toString());
				}
				if (obis == OBIS.LOGICAL_NUMBER && dlmsTags.size() != 0) {
					ret.put(OBIS.LOGICAL_NUMBER.getName(), dlmsTags.get(0).getOCTET().toString());
				}
				if (obis == OBIS.PHASE_TYPE && dlmsTags.size() != 0) {
					ret.put(OBIS.PHASE_TYPE.getName(), getPhaseType(dlmsTags.get(0).getOCTET()));
				}
				if (obis == OBIS.CT_RATIO_NUM && dlmsTags.size() != 0) {
					ret.put(OBIS.CT_RATIO_NUM.getName(), dlmsTags.get(0).getValue());
				}
				if (obis == OBIS.VT_RATIO_NUM && dlmsTags.size() != 0) {
					ret.put(OBIS.VT_RATIO_NUM.getName(), dlmsTags.get(0).getValue());
				}
				if (obis == OBIS.CT_RATIO_DEN && dlmsTags.size() != 0) {
					ret.put(OBIS.CT_RATIO_DEN.getName(), dlmsTags.get(0).getValue());
				}
				if (obis == OBIS.VT_RATIO_DEN && dlmsTags.size() != 0) {
					ret.put(OBIS.VT_RATIO_DEN.getName(), dlmsTags.get(0).getValue());
				}
				if (obis == OBIS.OVERAL_TRANS_NUM && dlmsTags.size() != 0) {
					ret.put(OBIS.OVERAL_TRANS_NUM.getName(), dlmsTags.get(0).getValue());
				}
				if (obis == OBIS.MEASUREMENT_STATUS && dlmsTags.size() != 0) {
					ret.put(OBIS.MEASUREMENT_STATUS.getName(), dlmsTags.get(0).getOCTET());
				}
				if (obis == OBIS.METER_STATUS && dlmsTags.size() != 0) {
					ret.put(OBIS.METER_STATUS.getName(), dlmsTags.get(0).getOCTET());
				}
				if (obis == OBIS.DRIVE_STATUS && dlmsTags.size() != 0) {
					ret.put(OBIS.DRIVE_STATUS.getName(), dlmsTags.get(0).getOCTET());
				}
			default:
				break;
			}
			break;
		case REGISTER:
			switch (attr) {
			case REGISTER_ATTR02: // value
				if (obis == OBIS.CURRENT_L1 && dlmsTags.size() != 0) {
					ret.put(OBIS.CURRENT_L1.getName(), dlmsTags.get(0).getValue());
				}
				if (obis == OBIS.CURRENT_L2 && dlmsTags.size() != 0) {
					ret.put(OBIS.CURRENT_L2.getName(), dlmsTags.get(0).getValue());
				}
				if (obis == OBIS.CURRENT_L3 && dlmsTags.size() != 0) {
					ret.put(OBIS.CURRENT_L3.getName(), dlmsTags.get(0).getValue());
				}
				if (obis == OBIS.VOLTAGE_L1 && dlmsTags.size() != 0) {
					ret.put(OBIS.VOLTAGE_L1.getName(), dlmsTags.get(0).getValue());
				}
				if (obis == OBIS.VOLTAGE_L2 && dlmsTags.size() != 0) {
					ret.put(OBIS.VOLTAGE_L2.getName(), dlmsTags.get(0).getValue());
				}
				if (obis == OBIS.VOLTAGE_L3 && dlmsTags.size() != 0) {
					ret.put(OBIS.VOLTAGE_L3.getName(), dlmsTags.get(0).getValue());
				}
				if (obis == OBIS.LASTMONTH_ACTIVEENERGY_IMPORT1 && dlmsTags.size() != 0) {
					ret.put(OBIS.LASTMONTH_ACTIVEENERGY_IMPORT1.getName(), dlmsTags.get(0).getValue());
				}
				if (obis == OBIS.LASTMONTH_ACTIVEENERGY_IMPORT2 && dlmsTags.size() != 0) {
					ret.put(OBIS.LASTMONTH_ACTIVEENERGY_IMPORT2.getName(), dlmsTags.get(0).getValue());
				}
				if (obis == OBIS.LASTMONTH_ACTIVEENERGY_EXPORT1 && dlmsTags.size() != 0) {
					ret.put(OBIS.LASTMONTH_ACTIVEENERGY_EXPORT1.getName(), dlmsTags.get(0).getValue());
				}
				if (obis == OBIS.LASTMONTH_ACTIVEENERGY_EXPORT2 && dlmsTags.size() != 0) {
					ret.put(OBIS.LASTMONTH_ACTIVEENERGY_EXPORT2.getName(), dlmsTags.get(0).getValue());
				}
				if (obis == OBIS.LASTMONTH_REACTIVEENERGY_IMPORT1 && dlmsTags.size() != 0) {
					ret.put(OBIS.LASTMONTH_REACTIVEENERGY_IMPORT1.getName(), dlmsTags.get(0).getValue());
				}
				if (obis == OBIS.LASTMONTH_REACTIVEENERGY_IMPORT2 && dlmsTags.size() != 0) {
					ret.put(OBIS.LASTMONTH_REACTIVEENERGY_IMPORT2.getName(), dlmsTags.get(0).getValue());
				}
				if (obis == OBIS.LASTMONTH_REACTIVEENERGY_EXPORT1 && dlmsTags.size() != 0) {
					ret.put(OBIS.LASTMONTH_REACTIVEENERGY_EXPORT1.getName(), dlmsTags.get(0).getValue());
				}
				if (obis == OBIS.LASTMONTH_REACTIVEENERGY_EXPORT2 && dlmsTags.size() != 0) {
					ret.put(OBIS.LASTMONTH_REACTIVEENERGY_EXPORT2.getName(), dlmsTags.get(0).getValue());
				}
				if (obis == OBIS.CUMULATIVE_ACTIVEENERGY_IMPORT1 && dlmsTags.size() != 0) {
					ret.put(OBIS.CUMULATIVE_ACTIVEENERGY_IMPORT1.getName(), dlmsTags.get(0).getValue());
				}
				if (obis == OBIS.CUMULATIVE_ACTIVEENERGY_IMPORT2 && dlmsTags.size() != 0) {
					ret.put(OBIS.CUMULATIVE_ACTIVEENERGY_IMPORT2.getName(), dlmsTags.get(0).getValue());
				}
				if (obis == OBIS.CUMULATIVE_ACTIVEENERGY_EXPORT1 && dlmsTags.size() != 0) {
					ret.put(OBIS.CUMULATIVE_ACTIVEENERGY_EXPORT1.getName(), dlmsTags.get(0).getValue());
				}
				if (obis == OBIS.CUMULATIVE_ACTIVEENERGY_EXPORT2 && dlmsTags.size() != 0) {
					ret.put(OBIS.CUMULATIVE_ACTIVEENERGY_EXPORT2.getName(), dlmsTags.get(0).getValue());
				}
				if (obis == OBIS.CUMULATIVE_REACTIVEENERGY_IMPORT1 && dlmsTags.size() != 0) {
					ret.put(OBIS.CUMULATIVE_REACTIVEENERGY_IMPORT1.getName(), dlmsTags.get(0).getValue());
				}
				if (obis == OBIS.CUMULATIVE_REACTIVEENERGY_IMPORT2 && dlmsTags.size() != 0) {
					ret.put(OBIS.CUMULATIVE_REACTIVEENERGY_IMPORT2.getName(), dlmsTags.get(0).getValue());
				}
				if (obis == OBIS.CUMULATIVE_REACTIVEENERGY_EXPORT1 && dlmsTags.size() != 0) {
					ret.put(OBIS.CUMULATIVE_REACTIVEENERGY_EXPORT1.getName(), dlmsTags.get(0).getValue());
				}
				if (obis == OBIS.CUMULATIVE_REACTIVEENERGY_EXPORT2 && dlmsTags.size() != 0) {
					ret.put(OBIS.CUMULATIVE_REACTIVEENERGY_EXPORT2.getName(), dlmsTags.get(0).getValue());
				}
				if (obis == OBIS.TOTAL_ACTIVEENERGY_IMPORT && dlmsTags.size() != 0) {
					ret.put(OBIS.TOTAL_ACTIVEENERGY_IMPORT.getName(), dlmsTags.get(0).getValue());
				}
				if (obis == OBIS.TOTAL_ACTIVEENERGY_EXPORT && dlmsTags.size() != 0) {
					ret.put(OBIS.TOTAL_ACTIVEENERGY_EXPORT.getName(), dlmsTags.get(0).getValue());
				}
				if (obis == OBIS.TOTAL_MAX_ACTIVEDEMAND_IMPORT && dlmsTags.size() != 0) {
					ret.put(OBIS.TOTAL_MAX_ACTIVEDEMAND_IMPORT.getName(), dlmsTags.get(0).getValue());
				}
				if (obis == OBIS.TOTAL_MAX_ACTIVEDEMAND_EXPORT && dlmsTags.size() != 0) {
					ret.put(OBIS.TOTAL_MAX_ACTIVEDEMAND_EXPORT.getName(), dlmsTags.get(0).getValue());
				}
				if (obis == OBIS.TOTAL_MAX_REACTIVEDEMAND_IMPORT && dlmsTags.size() != 0) {
					ret.put(OBIS.TOTAL_MAX_REACTIVEDEMAND_IMPORT.getName(), dlmsTags.get(0).getValue());
				}
				if (obis == OBIS.TOTAL_MAX_REACTIVEDEMAND_EXPORT && dlmsTags.size() != 0) {
					ret.put(OBIS.TOTAL_MAX_REACTIVEDEMAND_EXPORT.getName(), dlmsTags.get(0).getValue());
				}
				if (obis == OBIS.TOTAL_CUM_ACTIVEDEMAND_IMPORT && dlmsTags.size() != 0) {
					ret.put(OBIS.TOTAL_CUM_ACTIVEDEMAND_IMPORT.getName(), dlmsTags.get(0).getValue());
				}
				if (obis == OBIS.TOTAL_CUM_ACTIVEDEMAND_EXPORT && dlmsTags.size() != 0) {
					ret.put(OBIS.TOTAL_CUM_ACTIVEDEMAND_EXPORT.getName(), dlmsTags.get(0).getValue());
				}
				if (obis == OBIS.TOTAL_CUM_REACTIVEDEMAND_IMPORT && dlmsTags.size() != 0) {
					ret.put(OBIS.TOTAL_CUM_REACTIVEDEMAND_IMPORT.getName(), dlmsTags.get(0).getValue());
				}
				if (obis == OBIS.TOTAL_CUM_REACTIVEDEMAND_EXPORT && dlmsTags.size() != 0) {
					ret.put(OBIS.TOTAL_CUM_REACTIVEDEMAND_EXPORT.getName(), dlmsTags.get(0).getValue());
				}
				break;
			case REGISTER_ATTR03: // scaler_unit
				break;
			}
			break;
		case PROFILE_GENERIC:
			switch (attr) {
			case PROFILE_GENERIC_ATTR02: // buffer compact-array or array
				putData(dlmsTags, ret, obis);				
				break;
			case PROFILE_GENERIC_ATTR03: // capture_objects array

				for (int i = 0; i < dlmsTags.size();) {
					DLMSTag structure = dlmsTags.get(i++);
					Long classID = (Long) dlmsTags.get(i++).getValue();
					OCTET logicalName = (OCTET) dlmsTags.get(i++).getValue();
					Integer attributeIndex = (Integer) dlmsTags.get(i++).getValue();
					Long dataIndex = (Long) dlmsTags.get(i++).getValue();
					log.debug("classId=" + classID + ", logicalName=" + logicalName.toHexString() + ", attrIndex=" + attributeIndex + ", dataIndex=" + dataIndex);

				}

				//putData(dlmsTags, ret, obis);
				break;
			case PROFILE_GENERIC_ATTR04: // capture period

				Object o = dlmsTags.get(0).getValue();
				if (o instanceof Long) {
					Long val = (Long) o;
					val = val.longValue() / 60;//sec -> min
					ret.put("LpInterval", val);
				} else if (o instanceof Integer) {
					Integer val = (Integer) o;
					val = val.intValue() / 60;//sec -> min
					ret.put("LpInterval", val);
				} else {
					ret.put("LpInterval", dlmsTags.get(0).getValue());
				}

				break;
			case PROFILE_GENERIC_ATTR07: // entries_in_use
				if (dlmsTags.size() == 0)
					break;
				ret.put("Entry", dlmsTags.get(0).getValue());
				break;
			}
			break;
		case CLOCK:
			switch (attr) {
			case CLOCK_ATTR01:
				break;
			case CLOCK_ATTR02:
				if (obis == OBIS.METER_TIME && dlmsTags.size() != 0) {
					byte[] data = dlmsTags.get(0).getOCTET().getValue();
					if (data.length >= 12) {
						int year;
						try {
							year = DataFormat.getIntTo2Byte(DataUtil.select(data, 0, 2));
							int month = DataFormat.getIntToByte(data[2]);
							int day = DataFormat.getIntToByte(data[3]);
							int week = DataFormat.getIntToByte(data[4]);
							int hour = DataFormat.getIntToByte(data[5]);
							int min = DataFormat.getIntToByte(data[6]);
							DecimalFormat df = new DecimalFormat("00");
							String str = year + df.format(month) + df.format(day) + df.format(hour) + df.format(min);
							ret.put(OBIS.METER_TIME.getName(), str);
						} catch (Exception e) {
							log.warn(e, e);
						}
					}
				}
				break;
			case CLOCK_ATTR03:
				if (obis == OBIS.METER_TIME && dlmsTags.size() != 0) {
					Long longVal = (Long) dlmsTags.get(0).getValue() / 60;
					ret.put("TimeZone", longVal + "");
				}
				break;
			case CLOCK_ATTR04:
				break;
			case CLOCK_ATTR05:
				break;
			case CLOCK_ATTR06:
				break;
			case CLOCK_ATTR07:
				break;
			case CLOCK_ATTR08:
				break;
			case CLOCK_ATTR09:
				break;
			}
			break;
		case SCRIPT_TABLE:
			switch (attr) {
			case SCRIPT_TABLE_ATTR01:
				if (obis == OBIS.RELAY_STATUS && dlmsTags.size() != 0) {
					ret.put("Relay Status", LOAD_CONTROL_STATUS.getValue(Integer.parseInt(Hex.decode(dlmsTags.get(0).getData()))));
				}
				if (obis == OBIS.EXTERNAL_RELAY_STATUS && dlmsTags.size() != 0) {
					ret.put("External Relay Status", DLMSVARIABLE.EXTERNAL_RELAY_STATUS.getValue(Integer.parseInt(Hex.decode(dlmsTags.get(0).getData()))));
				}
			}
			break;
		case RELAY_CLASS:
			switch (attr) {
			case REGISTER_ATTR02:
				if (obis == OBIS.RELAY_STATUS && dlmsTags.size() != 0) {
					Boolean bool = (Boolean) dlmsTags.get(0).getValue();
					if (bool.equals(Boolean.TRUE)) {
						ret.put("LoadControlStatus", LOAD_CONTROL_STATUS.CLOSE);
						ret.put("Relay Status", LOAD_CONTROL_STATUS.CLOSE);
					} else {
						ret.put("LoadControlStatus", LOAD_CONTROL_STATUS.OPEN);
						ret.put("Relay Status", LOAD_CONTROL_STATUS.OPEN);
					}
				}
				break;
			case REGISTER_ATTR04:
				if (obis == OBIS.RELAY_STATUS && dlmsTags.size() != 0) {

					if (dlmsTags.get(0).getValue() instanceof Boolean) {
						Boolean bool = (Boolean) dlmsTags.get(0).getValue();
						if (bool.equals(Boolean.TRUE)) {
							ret.put("LoadControlStatus", LOAD_CONTROL_STATUS.CLOSE);
							ret.put("Relay Status", LOAD_CONTROL_STATUS.CLOSE);
						} else {
							ret.put("LoadControlStatus", LOAD_CONTROL_STATUS.OPEN);
							ret.put("Relay Status", LOAD_CONTROL_STATUS.OPEN);
						}
					} else if (dlmsTags.get(0).getValue() instanceof OCTET) {
						OCTET octet = (OCTET) dlmsTags.get(0).getValue();
						if (octet.getValue() != null && octet.getValue().length > 0) {
							CONTROL_STATE state = CONTROL_STATE.getValue(octet.getValue()[0] & 0xFF);
							if (state != null) {
								switch (state) {
								case Connected:
									ret.put("LoadControlStatus", LOAD_CONTROL_STATUS.CLOSE);
									ret.put("Relay Status", LOAD_CONTROL_STATUS.CLOSE);
									break;
								case Disconnected:
									ret.put("LoadControlStatus", LOAD_CONTROL_STATUS.OPEN);
									ret.put("Relay Status", LOAD_CONTROL_STATUS.OPEN);
									break;
								case ReadyForReconnection:
									ret.put("LoadControlStatus", LOAD_CONTROL_STATUS.OPEN);
									ret.put("Relay Status", LOAD_CONTROL_STATUS.OPEN);
									break;
								}
							} else {
								log.info("Unknown state=[" + octet.getValue()[0] + "]");
							}
						}
					}

					//TODO CHECK STATUS
				}
				break;
			}
			break;
		default:
			break;

		}

		log.debug("OBIS[" + obis + "] VALUE[" + ret.toString() + "]");

		return ret;
	}

	/*
	 * 하나의 OBIS에 여러 개의 태그를 가지는 경우 그 순서에 따라 데이타 유형이 결정된다.
	 */
	private void putData(List<DLMSTag> tags, Map<String, Object> ret, OBIS obis) {
		String name = null;
		int channelCount = 0;
		int channelIndex = 1;
		if (obis.equals(OBIS.ENERGY_LOAD_PROFILE) || obis.equals(OBIS.DAILY_LOAD_PROFILE) || obis.equals(OBIS.MONTHLY_BILLING)) {
			name = "";

			//			for (DLMSTag tag : tags) {
			//				if (tag.getTag().equals(DLMS_TAG_TYPE.Structure)) {
			//
			//					OCTET octet = (OCTET) tag.getValue();
			//					channelCount = DataUtil.getIntToBytes(octet.getValue());  //channelcount 는 1 빼야함
			//					channelIndex = 1;
			//				} else if (tag.getTag().equals(DLMS_TAG_TYPE.Array)) {
			//					// skip
			//				} else {
			//
			//					if (tag.getTag().equals(DLMS_TAG_TYPE.OctetString)) {
			//						name = "DateTime";
			//					} else {
			//						name = "Channel[" + channelIndex + "]";
			//						channelIndex++;
			//					}
			//					putData(ret, obis, name, tag);
			//				}
			//			}

			for (DLMSTag tag : tags) {
				if (tag.getTag().equals(DLMS_TAG_TYPE.Structure)) {
					channelIndex = 1;
				} else {
					if (tag.getTag().equals(DLMS_TAG_TYPE.OctetString)) { // Date
						name = "DateTime";
					} else if (tag.getTag().equals(DLMS_TAG_TYPE.UINT32)) { // Channel
						name = "Channel[" + channelIndex + "]";
						channelIndex++;
					} else if (tag.getTag().equals(DLMS_TAG_TYPE.UINT8)) { // Status
						name = "Status";
					} else {
						log.debug("########### ENERGY_LOAD_PROFILE : Unknowon DLMSTag !!!! ==> " + tag.toString());
					}

					putData(ret, obis, name, tag);
				}
			}

		} else if (obis.equals(OBIS.STANDARD_EVENT) || obis.equals(OBIS.FRAUDDETECTIONLOGEVENT) || obis.equals(OBIS.RELAY_EVENT)) // "Disconnector control log
		{
			for (DLMSTag tag : tags) {
				if (tag.getTag().equals(DLMS_TAG_TYPE.Structure)) {

					OCTET octet = (OCTET) tag.getValue();
					channelCount = DataUtil.getIntToBytes(octet.getValue());

				} else if (tag.getTag().equals(DLMS_TAG_TYPE.Array)) {
					// skip
				} else {

					if (tag.getTag().equals(DLMS_TAG_TYPE.OctetString)) {
						name = "EventTime";
						putData(ret, obis, name, tag);
					} else if (tag.getTag().equals(DLMS_TAG_TYPE.UINT8)) {
						name = "EventCode";
						putData(ret, obis, name, tag);
					}
				}
			}
		} else {
			for (int i = 0; i < tags.size(); i++) {
				log.debug(obis.getName() + ",tag=" + tags.get(i).getTag().name() + ", value=" + tags.get(i).getValue());
				//name = DLMSVARIABLE.getDataName(obis, i);
				putData(ret, obis, name, tags.get(i));
			}
		}
	}

	//|| obis.equals(OBIS.TAMPER_EVENT)
	//|| obis.equals(OBIS.TIME_CHANGE_BEFORE)
	//|| obis.equals(OBIS.TIME_CHANGE_AFTER)

	private Object putData(Map<String, Object> map, OBIS obis, String dataName, DLMSTag tag) {

		try {
			switch (obis) {
			case METER_TIME:
				getOBIS_CODE_METER_TIME(map, dataName, tag);
				break;
			//case DEVICE_INFO :
			case PHASE_TYPE:
				getOBIS_CODE_DEVICE_INFO(map, dataName, tag);
				break;
			//case METER_CONSTANT_ACTIVE :
			//case METER_CONSTANT_REACTIVE :
			//    getOBIS_CODE_METER_CONSTANT_ACTIVE(map, dataName, tag);
			//    break;
			//case KEPCO_CURRENT_MAX_DEMAND :
			//    getOBIS_CODE_KEPCO_CURRENT_MAX_DEMAND(map, dataName, tag);
			//    break;
			//case KEPCO_PREVIOUS_MAX_DEMAND :
			//    getOBIS_CODE_KEPCO_PREVIOUS_MAX_DEMAND(map, dataName, tag);
			//    break;
			case POWER_FAILURE:
			case POWER_RESTORE:
			case MONTHLY_BILLING:
				getOBIS_CODE_LOAD_PROFILE(map, dataName, tag);
				break;
			case TIME_CHANGE_BEFORE:
				//getOBIS_CODE_EVENT_LOG2(map, dataName, tag);
				break;
			case TIME_CHANGE_AFTER:
				//getOBIS_CODE_EVENT_LOG2(map, dataName, tag);
				break;
			case FRAUDDETECTIONLOGEVENT:
				getOBIS_CODE_EVENT_LOG2(map, dataName, tag);
				break;
			case TAMPER_EVENT:
				//getOBIS_CODE_EVENT_LOG2(map, dataName, tag);
				break;
			case RELAY_EVENT: // Disconnector control log
				getOBIS_CODE_EVENT_LOG2(map, dataName, tag);
				break;
			case STANDARD_EVENT:
				getOBIS_CODE_EVENT_LOG2(map, dataName, tag);
				break;
			case MANUAL_DEMAND_RESET:
				//getOBIS_CODE_EVENT_LOG(map, dataName, tag);
				break;
			case ENERGY_LOAD_PROFILE:
				if("LSIQ-3PCT".equals(meter.getModel().getName()) || "LSIQ-3PCV".equals(meter.getModel().getName())) {
					getOBIS_CODE_LOAD_PROFILE_MOE(map, dataName, tag);
				}else {
					getOBIS_CODE_LOAD_PROFILE(map, dataName, tag);
				}				
				break;
			case DAILY_LOAD_PROFILE:
				getOBIS_CODE_LOAD_PROFILE(map, dataName, tag);
				break;
			case RELAY_STATUS:
				getOBIS_CODE_RELAY_STATUS(map, dataName, tag);
				break;
			case WEAK_LQI_VALUE:
				getOBIS_CODE_WEAK_LQI_VALUE(map, dataName, tag);
				break;
			}
		} catch (Exception e) {
			log.error(e, e);
			log.error("obis:" + obis + ":dataName:" + dataName + ":tag:" + tag);
		}
		return tag.getValue();
	}

	private void getOBIS_CODE_WEAK_LQI_VALUE(Map<String, Object> map, String dataName, DLMSTag tag) throws Exception {
		//log.debug("DATA_NAME[" + dataName + "] VALUE[" + (tag.getValue() instanceof OCTET? Hex.decode(((OCTET)tag.getValue()).getValue()):tag.getValue()) + "]");
		String key = dataName;
		for (int cnt = 0;; cnt++) {
			key = dataName + "-" + cnt;
			if (!map.containsKey(key)) {
				log.debug("DATA_NAME[" + key + "] VALUE[" + tag.getValue() + "]");
				map.put(key, tag.getValue());
				break;
			}
		}
	}

	private void getOBIS_CODE_RELAY_STATUS(Map<String, Object> map, String dataName, DLMSTag tag) throws Exception {
		//log.debug("DATA_NAME[" + dataName + "] VALUE[" + (tag.getValue() instanceof OCTET? Hex.decode(((OCTET)tag.getValue()).getValue()):tag.getValue()) + "]");
		String key = dataName;
		for (int cnt = 0;; cnt++) {
			key = dataName + "-" + cnt;
			if (!map.containsKey(key)) {
				log.debug("DATA_NAME[" + key + "] VALUE[" + tag.getValue() + "]");
				map.put(key, tag.getValue());
				break;
			}
		}
	}

	private void getOBIS_CODE_LOAD_PROFILE_MOE(Map<String, Object> map, String dataName, DLMSTag tag) throws Exception {
		
		if (dataName != null && dataName.equals("DateTime")) {
			if((map.size() % 14) == 0) {
				byte[] data = tag.getData();
				if (data.length == 12) {
					int year = DataFormat.getIntTo2Byte(DataFormat.select(data, 0, 2));
					int month = DataFormat.getIntToByte(data[2]);
					int day = DataFormat.getIntToByte(data[3]);
					int week = DataFormat.getIntToByte(data[4]);
					int hour = DataFormat.getIntToByte(data[5]);
					int min = DataFormat.getIntToByte(data[6]);
					DecimalFormat df = new DecimalFormat("00");
					String str = year + df.format(month) + df.format(day) + df.format(hour) + df.format(min);
					String key = dataName;
					for (int cnt = 0;; cnt++) {
						key = dataName + "-" + cnt;
						if (!map.containsKey(key)) {
							log.debug("DATA_NAME[" + key + "] VALUE[" + str + "]");
							map.put(key, str);
							break;
						}
					}
				}
			}
		}else if (dataName.equals(ENERGY_LOAD_PROFILE.Status.toString())) {
			byte[] data = tag.getData();
			int value = DataUtil.getIntToBytes(data);

			String binaryString = Integer.toBinaryString(value);

			while (binaryString.length() % 8 != 0) {
				binaryString = "0" + binaryString;
			}
			String key = dataName;
			for (int cnt = 0;; cnt++) {
				key = dataName + "-" + cnt;
				if (!map.containsKey(key)) {
					log.debug("DATA_NAME[" + key + "] VALUE[" + binaryString + "]");
					map.put(key, binaryString);
					break;
				}
			}
		}else {
			String key = dataName;
			for (int cnt = 0;; cnt++) {
				key = dataName + "-" + cnt;
				if (!map.containsKey(key)) {
					log.debug("DATA_NAME[" + key + "] VALUE[" + tag.getValue() + "]");
					map.put(key, tag.getValue());
					break;
				}
			}
		}
	}
	
	private void getOBIS_CODE_LOAD_PROFILE(Map<String, Object> map, String dataName, DLMSTag tag) throws Exception {
		//log.debug("DATA_NAME[" + dataName + "] VALUE[" + (tag.getValue() instanceof OCTET? Hex.decode(((OCTET)tag.getValue()).getValue()):tag.getValue()) + "]");

		if (dataName != null && dataName.equals("DateTime")) {
			byte[] data = tag.getData();
			if (data.length == 12) {
				int year = DataFormat.getIntTo2Byte(DataFormat.select(data, 0, 2));
				int month = DataFormat.getIntToByte(data[2]);
				int day = DataFormat.getIntToByte(data[3]);
				int week = DataFormat.getIntToByte(data[4]);
				int hour = DataFormat.getIntToByte(data[5]);
				int min = DataFormat.getIntToByte(data[6]);
				DecimalFormat df = new DecimalFormat("00");
				String str = year + df.format(month) + df.format(day) + df.format(hour) + df.format(min);
				String key = dataName;
				for (int cnt = 0;; cnt++) {
					key = dataName + "-" + cnt;
					if (!map.containsKey(key)) {
						log.debug("DATA_NAME[" + key + "] VALUE[" + str + "]");
						map.put(key, str);
						break;
					}
				}
			}
		} else if (dataName.equals(ENERGY_LOAD_PROFILE.Status.toString())) {
			byte[] data = tag.getData();
			int value = DataUtil.getIntToBytes(data);

			String binaryString = Integer.toBinaryString(value);

			while (binaryString.length() % 8 != 0) {
				binaryString = "0" + binaryString;
			}
			String key = dataName;
			for (int cnt = 0;; cnt++) {
				key = dataName + "-" + cnt;
				if (!map.containsKey(key)) {
					log.debug("DATA_NAME[" + key + "] VALUE[" + binaryString + "]");
					map.put(key, binaryString);
					break;
				}
			}
		} else {
			String key = dataName;
			for (int cnt = 0;; cnt++) {
				key = dataName + "-" + cnt;
				if (!map.containsKey(key)) {
					log.debug("DATA_NAME[" + key + "] VALUE[" + tag.getValue() + "]");
					map.put(key, tag.getValue());
					break;
				}
			}
		}
	}

	private void getOBIS_CODE_EVENT_LOG(Map<String, Object> map, String dataName, DLMSTag tag) throws Exception {
		//log.debug("DATA_NAME[" + dataName + "] VALUE[" + (tag.getValue() instanceof OCTET? Hex.decode(((OCTET)tag.getValue()).getValue()):tag.getValue()) + "]");
		for (EVENT_LOG el : EVENT_LOG.values()) {
			if (dataName.startsWith(el.name()))

				log.debug("DATA_NAME[" + dataName + "] VALUE[" + makeDateTime4week(tag.getData()) + "]");
			map.put(dataName, makeDateTime4week(tag.getData()));
			// else map.put(dataName, tag.getValue()); 
		}
	}

	private void getOBIS_CODE_EVENT_LOG2(Map<String, Object> map, String dataName, DLMSTag tag) throws Exception {
		//log.debug("DATA_NAME[" + dataName + "] VALUE[" + (tag.getValue() instanceof OCTET? Hex.decode(((OCTET)tag.getValue()).getValue()):tag.getValue()) + "]");

		if (dataName != null && dataName.equals("EventTime")) {
			String str = makeDateTime4week(tag.getData());
			String key = "";
			for (int cnt = 0;; cnt++) {
				key = dataName + "-" + cnt;
				if (!map.containsKey(key)) {
					log.debug("DATA_NAME[" + key + "] VALUE[" + str + "]");
					map.put(key, str);
					break;
				}
			}
		}
		if (dataName != null && dataName.equals("EventCode") && tag.getValue() instanceof Integer) {
			String str = "";
			String key = "";
			for (EVENT_LOG el : EVENT_LOG.values()) {
				if (el.getFlag() == ((Integer) tag.getValue()).intValue()) {
					str = el.getMsg();
				}
			}
			for (int cnt = 0;; cnt++) {
				key = dataName + "-" + cnt;

				if (!map.containsKey(key)) {
					log.debug("DATA_NAME[" + key + "] VALUE[" + str + "]");
					map.put(key, str);
					break;
				}
			}
		}
	}

	/**
	 * DLMS 12bytes OCTET 시간 포맷 년 : 0,1 월 : 2 일 : 3 시 : 5 분 : 6
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	private String makeDateTime4week(byte[] data) throws Exception {
		int year = DataFormat.getIntTo2Byte(DataFormat.select(data, 0, 2));
		int month = DataFormat.getIntToByte(data[2]);
		int day = DataFormat.getIntToByte(data[3]);
		int hour = DataFormat.getIntToByte(data[5]);
		int min = DataFormat.getIntToByte(data[6]);
		int sec = DataFormat.getIntToByte(data[7]);

		DecimalFormat df = new DecimalFormat("00");
		String str = ":date=" + year + df.format(month) + df.format(day) + df.format(hour) + df.format(min) + df.format(sec);

		return str;
	}

	public Double getG3_PLC_SNR(int val) {
		int min = 0;
		int min_value = -10;
		double steps = 0.25;
		double cal = 0d;

		cal = (val - min) * steps + min_value;
		return cal;
	}

	public void getOBIS_CODE_METER_TIME(Map<String, Object> map, String dataName, DLMSTag tag) throws Exception {
		log.debug("DATA_NAME[" + dataName + "] VALUE[" + (tag.getValue() instanceof OCTET ? Hex.decode(((OCTET) tag.getValue()).getValue()) : tag.getValue()) + "]");
	}

	public String getPhaseType(OCTET octet) {
		String str = "";
		int value = DataUtil.getIntToBytes(octet.getValue());

		//checked for single phase, three phase, but ct, ct/vt didn't checked
		switch (value) {
		case 1:
			str = "Single Phase ";
			break;
		case 3:
			str = "Three Phase ";
			break;
		case 4:
			str = "Poly Phase ";
			break;
		}
		return str;
	}

	public void getOBIS_CODE_DEVICE_INFO(Map<String, Object> map, String dataName, DLMSTag tag) throws Exception {

		String str = "";
		if (dataName.equals(OBIS.PHASE_TYPE.name())) {
			int value = DataUtil.getIntToBytes(((OCTET) tag.getValue()).getValue());

			for (int i = 4; i < 8; i++) {
				if ((value >> i & 1) == 1) {
					if (i == 4) {
						str = "1-phase,2-wire," + (value & 0x0F);
						map.put(dataName, str);

					} else if (i == 5) {
						str = "1-phase,3-wire," + (value & 0x0F);
						map.put(dataName, str);

					} else if (i == 6) {
						str = "3-phase,3-wire," + (value & 0x0F);
						map.put(dataName, str);

					} else if (i == 7) {
						str = "3-phase,4-wire," + (value & 0x0F);
						map.put(dataName, str);

					}
				}
			}

		}
		/*
		else if (dataName.equals(KEPCO_METER_INFO.MeterDate.name())) {
		    byte[] data = tag.getData();
		    DecimalFormat df = new DecimalFormat("00");
		    int year = DataFormat.getIntTo2Byte(DataFormat.select(data, 0, 2));
		    int month = DataFormat.getIntToByte(data[2]);
		    int day = DataFormat.getIntToByte(data[3]);
		    int hour = DataFormat.getIntToByte(data[4]);
		    int min = DataFormat.getIntToByte(data[5]);
		    int sec = DataFormat.getIntToByte(data[6]);
		    int week = DataFormat.getIntToByte(data[7]);
		    str = year + df.format(month) + df.format(day) + df.format(hour)
		            + df.format(min) + df.format(sec); //  + " " + week; 주는 표현하지 않는다.
		    map.put(dataName, str);
		}
		else if (dataName.equals(DEVICE_INFO.MeterStatusError.name())) {
		    int value = DataUtil.getIntToBytes(((OCTET)tag.getValue()).getValue());
		    String binaryString = Integer.toBinaryString(value);
		    while (binaryString.length() % 8 != 0) {
		        binaryString = "0" + binaryString;
		    }

		    str = binaryString;
		    map.put(dataName, str);
		}
		else if (dataName.equals(DEVICE_INFO.MeterStatusCaution.name())) {
		    int value = DataUtil.getIntToBytes(((OCTET)tag.getValue()).getValue());
		    String binaryString = Integer.toBinaryString(value);
		    while (binaryString.length() % 8 != 0) {
		        binaryString = "0" + binaryString;
		    }
		    str = binaryString;
		    map.put(dataName, str);
		}
		else if (dataName.equals(DEVICE_INFO.RecentReadLoadProfileDate.name())) {
		    byte[] data = tag.getData();
		    DecimalFormat df = new DecimalFormat("00");
		    int year = DataFormat.getIntTo2Byte(DataFormat.select(data, 0, 2));
		    int month = DataFormat.getIntToByte(data[2]);
		    int day = DataFormat.getIntToByte(data[3]);
		    int hour = DataFormat.getIntToByte(data[4]);
		    int min = DataFormat.getIntToByte(data[5]);
		    str = year + df.format(month) + df.format(day) + df.format(hour)
		            + df.format(min);
		    map.put(dataName, str);
		}
		else if (dataName.equals(DEVICE_INFO.LastReadInfo.name())) {
		    byte[] data = tag.getData();
		    int value = DataUtil.getIntToByte(data[1]);
		    String binaryString = Integer.toBinaryString(value);
		    while (binaryString.length() % 8 != 0) {
		        binaryString = "0" + binaryString;
		    }
		    int year = DataFormat.getIntTo2Byte(DataFormat.select(data, 2, 2));
		    int month = DataFormat.getIntToByte(data[4]);
		    int day = DataFormat.getIntToByte(data[5]);
		    int hour = DataFormat.getIntToByte(data[6]);
		    int min = DataFormat.getIntToByte(data[7]);
		    int sec = DataFormat.getIntToByte(data[8]);
		    int week = DataFormat.getIntToByte(data[9]);
		    DecimalFormat df = new DecimalFormat("00");

		    str = "reason:" + binaryString + ":date=" + year + df.format(month)
		            + df.format(day) + df.format(hour) + df.format(min)
		            + df.format(sec); //  + " " + week; 주는 제거한다.
		    map.put(dataName, str);
		}
		else {
		    map.put(dataName, tag.getValue());
		}
		*/

	}

	public static String getLP_STATUS(byte[] value) {
		StringBuffer str = new StringBuffer("");
		int byte0 = value[0] & 0xFF;
		for (int i = 0; i < 8; i++) {
			if ((byte0 & (1 << (7 - i))) > 0) {
				str.append(DLMSVARIABLE.LP_STATUS_BIT[i] + ", \n");
			}
		}
		return str.toString();
	}

	public static String getOBIS_MEASUREMENT_STATUS(OCTET octet) {
		StringBuffer str = new StringBuffer("");
		byte[] value = octet.getValue();

		int byte0 = value[7] & 0xFF;
		int byte1 = value[6] & 0xFF;
		int byte2 = value[5] & 0xFF;
		int byte3 = value[4] & 0xFF;
		int byte4 = value[3] & 0xFF;
		int byte5 = value[2] & 0xFF;

		//0 1 byte is reserved

		for (int i = 0; i < 8; i++) {
			if ((byte0 & (1 << (7 - i))) > 0) {
				str.append(DLMSVARIABLE.MEASUREMENT_STATUS_BYTE_0[i] + ", \n");
			}
		}
		for (int i = 0; i < 8; i++) {
			if ((byte1 & (1 << (7 - i))) > 0) {
				str.append(DLMSVARIABLE.MEASUREMENT_STATUS_BYTE_1[i] + ", \n");
			}
		}
		for (int i = 0; i < 8; i++) {
			if ((byte2 & (1 << (7 - i))) > 0) {
				str.append(DLMSVARIABLE.MEASUREMENT_STATUS_BYTE_2[i] + ", \n");
			}
		}
		for (int i = 0; i < 8; i++) {
			if ((byte3 & (1 << (7 - i))) > 0) {
				str.append(DLMSVARIABLE.MEASUREMENT_STATUS_BYTE_3[i] + ", \n");
			}
		}
		for (int i = 0; i < 8; i++) {
			if ((byte4 & (1 << (7 - i))) > 0) {
				str.append(DLMSVARIABLE.MEASUREMENT_STATUS_BYTE_4[i] + ", \n");
			}
		}
		for (int i = 0; i < 8; i++) {
			if ((byte5 & (1 << (7 - i))) > 0) {
				str.append(DLMSVARIABLE.MEASUREMENT_STATUS_BYTE_5[i] + ", \n");
			}
		}
		return str.toString();
	}

	public static String getOBIS_FUNCTION_STATUS(OCTET octet) {
		StringBuffer str = new StringBuffer("");
		byte[] value = octet.getValue();

		int byte0 = value[1] & 0xFF;
		int byte1 = value[0] & 0xFF;

		for (int i = 0; i < 8; i++) {
			if ((byte0 & (1 << (7 - i))) > 0) {
				str.append(DLMSVARIABLE.FUNCTION_STATUS_BYTE_0[i] + ", \n");
			}
		}
		for (int i = 0; i < 8; i++) {
			if ((byte1 & (1 << (7 - i))) > 0) {
				str.append(DLMSVARIABLE.FUNCTION_STATUS_BYTE_1[i] + ", \n");
			}
		}

		return str.toString();
	}

	public static String getOBIS_DRIVE_STATUS(OCTET octet) {
		StringBuffer str = new StringBuffer("");
		byte[] value = octet.getValue();

		int byte0 = value[0] & 0xFF;

		for (int i = 0; i < 8; i++) {
			if ((byte0 & (1 << (7 - i))) > 0) {
				str.append(DLMSVARIABLE.DRIVE_STATUS_BYTE_0[i] + ", \n");
			}
		}

		return str.toString();
	}
}
