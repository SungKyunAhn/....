package com.aimir.fep.protocol.emnv.frame.payload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

public class ZigBeeModemInfo implements IModemInfo {
	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(ZigBeeModemInfo.class);

	private ModemType modemType = ModemType.ZigBee;
	private final int totalLength = 83;
	private String deviceId;

	private byte[] fwVersion; // 10
	private byte[] hwVersion; // 10
	private byte[] channel; // 1 , ZigBee Channel
	private byte[] panId; // 2 , ZigBee Pan ID
	private byte[] prodCompany; //20, 제작사 / 문자열 – xxxxxxxxxxxxxxxx(20자리) / (상위부터 값을 채우고 하위 bytes가 없을시 나머지는 0을 채운다.)
	private byte[] prodDate; //20, 제조년월일 / 문자열 – xxxxxxxxxxxxxxxx(20자리) / (상위부터 값을 채우고 하위 bytes가 없을시 나머지는 0을 채운다.)
	private byte[] prodNumber; //20, 제조번호 / 문자열 – xxxxxxxxxxxxxxxx(20자리) / (상위부터 값을 채우고 하위 bytes가 없을시 나머지는 0을 채운다.)

	public ZigBeeModemInfo() {
		fwVersion = new byte[10];
		hwVersion = new byte[10];
		channel = new byte[1];
		panId = new byte[2];
		prodCompany = new byte[20];
		prodDate = new byte[20];
		prodNumber = new byte[20];
	}

	public ZigBeeModemInfo(String sourceAddress) {
		// TODO Auto-generated constructor stub
	}

	public byte[] getFwVersion() {
		return fwVersion;
	}

	public byte[] getHwVersion() {
		return hwVersion;
	}

	public byte[] getChannel() {
		return channel;
	}

	public byte[] getPanId() {
		return panId;
	}

	public byte[] getProdCompany() {
		return prodCompany;
	}

	public byte[] getProdDate() {
		return prodDate;
	}

	public byte[] getProdNumber() {
		return prodNumber;
	}

	@Override
	public int getTotalLength() {
		return totalLength;
	}

	@Override
	public void decode(byte[] data) {
		try {
			log.debug("[PROTOCOL][MODEM_INFO][{}] ZIGBEE_MODEM_INFO ({}):[{}] ==> {}", new Object[] { modemType.name(), data.length, "", Hex.decode(data) });

			System.arraycopy(data, 0, fwVersion, 0, 10);
			System.arraycopy(data, 10, hwVersion, 0, 10);
			System.arraycopy(data, 20, channel, 0, 1);
			System.arraycopy(data, 21, panId, 0, 2);

			System.arraycopy(data, 23, prodCompany, 0, 20);
			prodCompany = DataUtil.trim0x00Byte(prodCompany);

			System.arraycopy(data, 43, prodDate, 0, 20);
			prodDate = DataUtil.trim0x00Byte(prodDate);

			System.arraycopy(data, 63, prodNumber, 0, 20);
			prodNumber = DataUtil.trim0x00Byte(prodNumber);

			log.debug("[PROTOCOL][MODEM_INFO][{}] ZIGBEE_MODEM_INFO ({}):[{}] ==> {}", new Object[] { modemType.name(), data.length, "", toString() });
		} catch (Exception e) {
			log.debug("ZigBeeModemInfo decode error - {}", e);
		}

	}

	@Override
	public byte[] encode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ModemType getModemType() {
		return modemType;
	}

	@Override
	public String getDeviceId() {
		return deviceId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ZigBeeModemInfo [modemType=");
		builder.append(modemType);
		builder.append(", totalLength=");
		builder.append(totalLength);
		builder.append(", deviceId=");
		builder.append(deviceId);
		builder.append(", fwVersion=");
		builder.append(new String(fwVersion));
		builder.append(", hwVersion=");
		builder.append(new String(hwVersion));
		builder.append(", channel=");
		builder.append(new String(channel));
		builder.append(", panId=");
		builder.append(DataUtil.getIntToByte(panId[0]));
		builder.append(", prodCompany=");
		builder.append(new String(prodCompany));
		builder.append(", prodDate=");
		builder.append(new String(prodDate));
		builder.append(", prodNumber=");
		builder.append(new String(prodNumber));
		builder.append("]");
		return builder.toString();
	}

}
