package com.aimir.fep.meter.parser.MX2Table;

import java.io.ByteArrayInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.MeterData;
import com.aimir.fep.util.DataUtil;

/**
 * Meter Info<br>
 * <p>
 * 본 Class 의 기본 기능은 포맷에 맞는 Byte Array <=> Object로 변환하는 기능.
 * </p>
 * 
 * @author kskim
 * @see <참고문서><br>
 *      <ul>
 *      <li>MX2_AMR_Communication_Specification-2011-06-16_Signed.pdf</li>
 *      <li>NAMR_P213GP(2011)_Protocol.doc</li>
 *      </ul>
 */
@SuppressWarnings("serial")
public class MX2MeterInfo extends CommonTable {
	private static Log log = LogFactory.getLog(MX2MeterInfo.class);
	
	public static String _1P2W="1P2W",_1P3W="1P3W",_3P3W="3P3W",_3P4W="3P4W";
	public static int n1P2W=0,n1P3W=1,n3P3W=2,n3P4W=3;
	

	// size 16 이기 때문에 남는 곳은 byte 0x00 으로 채운다.
	private final byte[] MeterModel = ("Mitsubishi-MX2" + (new String(
			new byte[] { 0x00, 0x00 }))).getBytes();

	private MeterData meterData;
	
	private String meterSerial;
	private String meterTime;
	
	/**
	 * 0 = 1P2W, 1 = 1P3W, 2 = 3P3W, 3 = 3P4W
	 */
	private String phaseWires;
	
	private String referenceVoltage;
	private String referenceFrequency;
	private String basicCurrent;
	private String maximumCurrent;
	
	
	//Phase Angle voltage
	private Double phaseAngleVa = 0d; //미터에서 올려주지 않는다.
	private Double phaseAngleVb = 0d;
	private Double phaseAngleVc = 0d;
	
	//Phase Angle current 
	private Double phaseAngleIa = 0d;
	private Double phaseAngleIb = 0d;
	private Double phaseAngleIc = 0d;

	private final int LEN_TOTAL = 44;
	private final int LEN_METER_SERIAL = 16;
	private final int LEN_METER_TIME = 7;

	/**
	 * MX2MeterInfo<br>
	 * 
	 * @param meterInfo
	 * @throws Exception
	 */
	public MX2MeterInfo(byte[] data) {
		meterData = new MeterData();

		ByteArrayInputStream bis = new ByteArrayInputStream(data);
		try {
			
			// Meter Model
			byte[] meterModel = new byte[16];
			bis.read(meterModel);
			//모델 저장하는 곳이 없어 일단 제외.
			//this.meterData.setVendor(new String(meterModel));
			
			// Meter Serial  - mx2 meter id는 Table Size 가 4byte 이기때문에 뒤에 내용은 필요 없다.
			byte[] fixMeterSerial = new byte[4];
			bis.read(fixMeterSerial);
			bis.skip(12); // FIXME : 뒤에 내용 필요없음 프로토콜 수정 필요
			
			
			// meter id는 7자리까지 사용됨으로 맨 앞에것은 삭제한다. MX2_AMR_Communication_Specification 문서 3페이지 참고.
			String strMeterSerial = bcdToString(fixMeterSerial);
			String meterSeiral = strMeterSerial.substring(1, strMeterSerial.length());

			this.meterData.setMeterId(meterSeiral);
			this.meterSerial = meterSeiral;
			
			// Meter Time
			byte[] meterTime = new byte[6];
			bis.read(meterTime);
			//원래는 7byte가 오는데 뒤에 ww 는 사용하지 않기때문에 빼준다.
			bis.skip(1);
			String yymmddhhmmss = bcdToDateString(meterTime, "yyMMddHHmmss");
			this.meterData.setTime(yymmddhhmmss);
			
			this.meterTime=yymmddhhmmss;
			this.phaseWires = String.valueOf(bis.read());
			this.referenceVoltage = String.valueOf(bis.read());
			this.referenceFrequency = String.valueOf(bis.read());
			this.basicCurrent = String.valueOf(bis.read());
			this.maximumCurrent = String.valueOf(bis.read());
			log.debug("lmax[" + maximumCurrent + "]");
			
			// Phase Angle voltage
			byte[] angleFrame = new byte[2]; // angle 값들의 size 는 2이기때문에 영역을 잡아둔다.
			bis.skip(4); // 테이블 첫 값중 4사이즈까지는 필요없기때문에 스킵
			this.setPhaseAngleVa(0d);//mx2는 A값이 없다 0으로 설정
			
			bis.read(angleFrame);//사이즈 2만큼 읽는다.
			this.setPhaseAngleVb(DataUtil.getIntTo2Byte(angleFrame)*0.01);//mx2는 A값이 없다 0으로 설정
			
			bis.read(angleFrame);//사이즈 2만큼 읽는다.
			this.setPhaseAngleVc(DataUtil.getIntTo2Byte(angleFrame)*0.01);//mx2는 A값이 없다 0으로 설정
			
			
			//Phase Angle current
			bis.skip(2); // 테이블 첫 값중 2사이즈까지는 필요없기때문에 스킵
			
			bis.read(angleFrame);//사이즈 2만큼 읽는다.
			this.setPhaseAngleIa(DataUtil.getIntTo2Byte(angleFrame)*0.01);//mx2는 A값이 없다 0으로 설정
			
			bis.read(angleFrame);//사이즈 2만큼 읽는다.
			this.setPhaseAngleIb(DataUtil.getIntTo2Byte(angleFrame)*0.01);//mx2는 A값이 없다 0으로 설정
			
			bis.read(angleFrame);//사이즈 2만큼 읽는다.
			this.setPhaseAngleIc(DataUtil.getIntTo2Byte(angleFrame)*0.01);//mx2는 A값이 없다 0으로 설정
		
			
			bis.close();
			log.debug(this.meterData);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.debug(e);
		}
	}

	public Double getPhaseAngleVa() {
		return phaseAngleVa;
	}

	public void setPhaseAngleVa(Double phaseAngleVa) {
		this.phaseAngleVa = phaseAngleVa;
	}

	public Double getPhaseAngleVb() {
		return phaseAngleVb;
	}

	public void setPhaseAngleVb(Double phaseAngleVb) {
		this.phaseAngleVb = phaseAngleVb;
	}

	public Double getPhaseAngleVc() {
		return phaseAngleVc;
	}

	public void setPhaseAngleVc(Double phaseAngleVc) {
		this.phaseAngleVc = phaseAngleVc;
	}

	public Double getPhaseAngleIa() {
		return phaseAngleIa;
	}

	public void setPhaseAngleIa(Double phaseAngleIa) {
		this.phaseAngleIa = phaseAngleIa;
	}

	public Double getPhaseAngleIb() {
		return phaseAngleIb;
	}

	public void setPhaseAngleIb(Double phaseAngleIb) {
		this.phaseAngleIb = phaseAngleIb;
	}

	public Double getPhaseAngleIc() {
		return phaseAngleIc;
	}

	public void setPhaseAngleIc(Double phaseAngleIc) {
		this.phaseAngleIc = phaseAngleIc;
	}

	public String getMeterSerial() {
		return meterSerial;
	}

	public String getMeterTime() {
		return meterTime;
	}

	public String getPhaseWires() {
		return phaseWires;
	}

	public String getReferenceVoltage() {
		return referenceVoltage;
	}

	public String getReferenceFrequency() {
		return referenceFrequency;
	}

	public String getBasicCurrent() {
		return basicCurrent;
	}

	public String getMaximumCurrent() {
		return maximumCurrent;
	}

	public void setMeterData(MeterData meterData) {
		this.meterData = meterData;
	}

	/**
	 * @return meter data
	 */
	public MeterData getMeterData() {
		return this.meterData;
	}

	/**
	 * Data Filed Type<br>
	 * Value : 0x4D54("MT")<br>
	 * Size : 2(Binary)<br>
	 * 
	 * @return
	 */
	public byte[] getDataFieldType() {
		return "MT".getBytes();
	}

	/**
	 * @return the meterModel
	 */
	public String getMeterModel() {
		return "Mitsubishi-MX2";
	}

}
