package com.aimir.fep.protocol.nip.frame.payload;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

public class Firmware extends PayloadFrame {
	private int targetType;

	public enum UpgradeCommand {
		  UpgradeStartRequest((int) 3)
		, UpgradeStartResponse((int) 4)
		, UpgradeData((int) 5)
		, UpgradeEndRequest((int) 6)
		, UpgradeEndResponse((int) 7)
		, UpgradeImageInstallRequest((int) 8)
		, UpgradeImageInstallResponse((int) 9);
		private int code;

		UpgradeCommand(int code) {
			this.code = code;
		}

		public int getCode() {
			return this.code;
		}
	}

	public enum ImageCode {
		NoError((byte) 0x00), CRCFail((byte) 0x01), UnknownError((byte) 0xFF);

		private byte code;

		ImageCode(byte code) {
			this.code = code;
		}

		public byte getCode() {
			return this.code;
		}
	}

	private UpgradeCommand _upgradeCommand;
	//Upgrade Start Request / Response
	private int upgradeSequenceNumber;
	private int address;

	private int length;
	private byte[] data;

	private long imageLength;
	private String crc;

	private ImageCode _imageCode;
	
	// INSERT START SP-681
	private String fwVersion;
	private String fwModel;
	
	private String installTime;
	// INSERT END SP-681

	public void setUpgradeCommand(int code) {
		for (UpgradeCommand f : UpgradeCommand.values()) {
			if (f.getCode() == code) {
				_upgradeCommand = f;
				break;
			}
		}
	}

	public void setImageCode(byte code) {
		for (ImageCode f : ImageCode.values()) {
			if (f.getCode() == code) {
				_imageCode = f;
				break;
			}
		}
	}

	public int getTargetType() {
		return targetType;
	}

	public void setTargetType(int targetType) {
		this.targetType = targetType;
	}

	public UpgradeCommand get_upgradeCommand() {
		return _upgradeCommand;
	}

	public void set_upgradeCommand(UpgradeCommand _upgradeCommand) {
		this._upgradeCommand = _upgradeCommand;
	}

	public int getUpgradeSequenceNumber() {
		return upgradeSequenceNumber;
	}

	public void setUpgradeSequenceNumber(int upgradeSequenceNumber) {
		this.upgradeSequenceNumber = upgradeSequenceNumber;
	}

	public int getAddress() {
		return address;
	}

	public void setAddress(int address) {
		this.address = address;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public void newData(int cnt) {
		this.data = new byte[cnt];
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public long getImageLength() {
		return imageLength;
	}

	public void setImageLength(long imageLength) {
		this.imageLength = imageLength;
	}

	public String getCrc() {
		return crc;
	}

	public void setCrc(String crc) {
		this.crc = crc;
	}

	public ImageCode get_imageCode() {
		return _imageCode;
	}

	public void set_imageCode(ImageCode _imageCode) {
		this._imageCode = _imageCode;
	}

	// INSERT START SP-681
	public String getFwVersion() {
		return fwVersion;
	}

	public void setFwVersion(String ver) {
		this.fwVersion = ver;
	}

	public String getFwModel() {
		return fwModel;
	}

	public void setFwModel(String model) {
		this.fwModel = model;
	}
	
	public String getInstallTime() {
		return installTime;
	}

	public void setInstallTime(String time) {
		this.installTime = time;
	}	
	// INSERT END SP-681	
	
	@Override
	public void decode(byte[] bx) {
		int pos = 0;
		byte[] b = new byte[1];
		System.arraycopy(bx, pos, b, 0, b.length);
		pos += b.length;
		String frameOption = DataUtil.getBit(b[0]);
		setTargetType(Integer.parseInt(DataUtil.getBitToInt(frameOption.substring(0, 2), "%d")));
		setUpgradeCommand(Integer.parseInt(DataUtil.getBitToInt(frameOption.substring(4, 8), "%d")));

		switch (_upgradeCommand) {
		case UpgradeStartResponse:
			b = new byte[4];
			System.arraycopy(bx, pos, b, 0, b.length);

			if (Hex.decode(b).equals("00000000") || Hex.decode(b).equals("FFFFFFFF")) {
				setAddress(0);
			} else {
				setAddress(DataUtil.getIntTo4Byte(b));
			}

			break;
		case UpgradeEndResponse:
		case UpgradeImageInstallResponse:
			b = new byte[1];
			System.arraycopy(bx, pos, b, 0, b.length);
			setImageCode(b[0]);
			break;
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Firmware[");
		sb.append("targetType=" + targetType);
		sb.append(", upgradeCommand=" + _upgradeCommand);
		sb.append(", upgradeSequenceNumber=" + upgradeSequenceNumber);
		sb.append(", address=" + address);
		sb.append(", length=" + length);
		sb.append(", imageLength=" + imageLength);
		sb.append(", crc=" + crc);
		sb.append(", ImageCode=" + _imageCode);	
		
		// INSERT START SP-681
		sb.append(", FW Version=" + (fwVersion != null ? fwVersion  : ""));
		sb.append(", FW Model Name=" + (fwModel != null ? fwModel  : ""));
		sb.append(", Install Time=" + (installTime != null ? installTime  : ""));
		// INSERT END SP-681
		
		sb.append(", data=" + (getData() != null ? getData().length + "byte" : ""));
		sb.append("]");
		
		return sb.toString();
//		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	
	@Override
	public byte[] encode() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte frameOption = (byte) (DataUtil.getByteToInt(getTargetType()) | DataUtil.getByteToInt(_upgradeCommand.getCode()));
		out.write(frameOption);

		switch (_upgradeCommand) {
		case UpgradeStartRequest:
			out.write(DataUtil.get2ByteToInt(getUpgradeSequenceNumber()));

			// INSERT START SP-681
			if ((fwVersion != null) && (fwVersion.length()>0)) {
				log.debug("OTA Advanced Option. FW Version[" + fwVersion + "]");
				out.write(Hex.encode(fwVersion));
			}
			if ((fwModel != null) && (fwModel.length()>0)) {
				log.debug("OTA Advanced Option. FW Model Name[" + fwModel + "]");
				byte[] b = new byte[20];
				Arrays.fill(b, (byte)0);
				System.arraycopy(fwModel.getBytes(), 0, b, 0, fwModel.getBytes().length);
				out.write(b);
			}			
			// INSERT END SP-681
			
			break;
		case UpgradeData:
			out.write(DataUtil.get4ByteToInt(getAddress()));
			out.write(DataUtil.get2ByteToInt(getLength()));
			out.write(getData());
			break;
		case UpgradeEndRequest:
//		case UpgradeImageInstallRequest:				// DELETE SP-681
			out.write(DataUtil.get4ByteToInt(getImageLength()));
			out.write(DataUtil.readByteString(getCrc()));
			break;
			// INSERT START SP-681
		case UpgradeImageInstallRequest:
			out.write(DataUtil.get4ByteToInt(getImageLength()));
			out.write(DataUtil.readByteString(getCrc()));
			if ((installTime != null) && (installTime.length()>=10)) {
				log.debug("OTA Advanced Option. Install Time[" + installTime + "]");
				out.write(DataUtil.get2ByteToInt(Integer.parseInt(installTime.substring(0,4))));	// YYYY
				out.write(DataUtil.getByteToInt(Integer.parseInt(installTime.substring(4,6))));		// MM
				out.write(DataUtil.getByteToInt(Integer.parseInt(installTime.substring(6,8))));		// DD
				out.write(DataUtil.getByteToInt(Integer.parseInt(installTime.substring(8,10))));	// hh
			}
			break;
			// INSERT END SP-681

		default:
			break;
		}
		byte[] bx = out.toByteArray();
		//log.debug(Hex.decode(bx));
		out.close();
		return bx;
	}

	@Override
	public void setCommandFlow(byte code) {
	}

	@Override
	public void setCommandType(byte code) {
	}

	@Override
	public byte[] getFrameTid() {
		return null;
	}

	@Override
	public void setFrameTid(byte[] code) {
	}
}

