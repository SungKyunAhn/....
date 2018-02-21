package com.aimir.fep.meter.parser.MX2Table;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.List;

import com.aimir.fep.protocol.mrp.protocol.MX2_DataConstants;
import com.aimir.fep.util.DataUtil;


/**
 * MX2미터 TOU Calendar 설정을위한 VO객체.
 * @author kskim
 */
public class TOUCalendar implements java.io.Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8335413725926026171L;

	//Activation Date
	TOUActivationDate activationDate;
	
	//Season Change
	List<TOUSeasonChange> seasonChange;
	
	//Day Pattern
	List<TOUDayPattern> dayPattern;
	
	//Fixed Recurring Holiday
	List<TOUFRHoliday> frHoliday;
	
	//Non-recurring Holiday
	List<TOUNRHoliday> nrHoliday;
	
	//End Message
	TOUEndMessage endMessage = new TOUEndMessage();

	public byte[] getActivationDate() throws Exception {
		if(activationDate==null)
			throw new Exception("Can not found Activation Date");
		
		return activationDate.toByteArray();
	}
	
	public void setActivationDate(TOUActivationDate activationDate) {
		this.activationDate = activationDate;
	}

	public byte[] getSeasonChange() throws Exception {
		if(seasonChange==null)
			throw new Exception("Can not found Season Change");
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		for (TOUSeasonChange t : seasonChange) {
			bos.write(t.toByteArray());
		}
		byte[] data = DataUtil.fillCopy(bos.toByteArray(), (byte)0xff, TOUCalendarBuilder.LEN_SEASON_CHANGE);
		bos.close();
		
		return data;
	}

	public void setSeasonChange(List<TOUSeasonChange> seasonChange) {
		this.seasonChange = seasonChange;
	}

	public byte[] getDayPattern() throws Exception {
		if(dayPattern==null)
			throw new Exception("Can not found Day Pattern");
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		for (TOUDayPattern t : dayPattern) {
			bos.write(t.toByteArray());
		}
		byte[] data = bos.toByteArray();
		bos.close();
		return data;
	}

	public void setDayPattern(List<TOUDayPattern> dayPattern) {
		this.dayPattern = dayPattern;
	}

	public byte[] getFrHoliday() throws Exception {
		if(frHoliday==null)
			throw new Exception("Can not found Fixed Recurring Holiday");
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		for (TOUFRHoliday t : frHoliday) {
			bos.write(t.toByteArray());
		}
		byte[] data = bos.toByteArray();
		bos.close();
		return data;
	}

	public void setFrHoliday(List<TOUFRHoliday> frHoliday) {
		this.frHoliday = frHoliday;
	}

	public byte[] getNrHoliday() throws Exception {
		if(nrHoliday==null)
			throw new Exception("Can not found Non Recurring Holiday");
			
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		for (TOUNRHoliday t : nrHoliday) {
			bos.write(t.toByteArray());
		}
		if(nrHoliday.size()==0) {
			final int LEN = 61;
			bos.write(DataUtil.fillBytes((byte)0xff, LEN));
		}
		byte[] data = bos.toByteArray();
		bos.close();
		return data;
	}

	public void setNrHoliday(List<TOUNRHoliday> nrHoliday) {
		this.nrHoliday = nrHoliday;
	}

	public byte[] getEndMessage() throws Exception {
		if(endMessage==null)
			throw new Exception("Can not found End Message");
		
		return endMessage.toByteArray();
	}

	public void setEndMessage(TOUEndMessage endMessage) {
		this.endMessage = endMessage;
	}
	
	
	/**
	 * 데이터에 해더 정보(Table ID, length)를 추가한다.
	 * @return
	 * @throws Exception
	 */
	private byte[] makeHeader(char tableId, byte[] data) throws Exception {
		char len = (char) data.length;
		ByteBuffer bf = ByteBuffer.allocate(data.length+4);
		bf.putChar(tableId);
		bf.putChar(len);
		bf.put(data);
		bf.flip();
		return bf.array();
	}
	
	/**
	 * 데이터를 길이만큼 잘라내어 해더정보를 생성한다.<br>
	 * 해더 정보는 테이블 id에 잘라낸 데이터의 인덱스를 더하여 구해진다.
	 * @param tableId 테이블 겟수만큼 +1 씩 증가하여 설정된다.
	 * @param data 모든 테이블의 데이터
	 * @param length 테이블 하나당 필요한 데이터 길이.
	 * @return
	 * @throws Exception
	 */
	private byte[] makeHeader(char tableId, byte[] data, int length) throws Exception {
		ByteArrayOutputStream bos=new ByteArrayOutputStream();
		int tableCnt = data.length / length;
		for(int i = 0;i<tableCnt;i++){
			byte[] tmpa=new byte[length]; 
			System.arraycopy(data, i*length, tmpa, 0, tmpa.length);
			char tabId = (char) (tableId+i);
			bos.write(makeHeader(tabId, tmpa));
		}
		return bos.toByteArray();
	}


	/**
	 * 파일 저장을위해 객체를 Binary 값으로 변환한다.<br>
	 * (파일포맷)<br>
	 * [section1] [section2] ... [checksum:2byte]<br>
	 * section = [Table Id:2byte] [length:2byte] [data: length byte]
	 * @return
	 * @throws Exception 
	 */
	public byte[] toBinary() throws Exception{
		ByteArrayOutputStream bos=new ByteArrayOutputStream();
		byte[] tmp=null;
		
		//Activation Date
		tmp = makeHeader(MX2_DataConstants.TABLE_ACTIVATION_DATE, this.getActivationDate());
		bos.write(tmp);
		
		//Season Change
		tmp = makeHeader(MX2_DataConstants.TABLE_SEASON_CHANGE, this.getSeasonChange());
		bos.write(tmp);
		
		//Day Pattern
		tmp = makeHeader(MX2_DataConstants.TABLE_DAY_PATTERN_SEASON1, this.getSeasonChange(),TOUCalendarBuilder.CNT_DAY_PATTERN);
		bos.write(tmp);
		
		//Fixed Recurring Holiday
		tmp = makeHeader(MX2_DataConstants.TABLE_FIXED_RECURRING_HOLIDAY_SET1, this.getSeasonChange(),TOUCalendarBuilder.CNT_FR_HOLIDAY);
		bos.write(tmp);
		
		//Non-recurring Holiday
		tmp = makeHeader(MX2_DataConstants.TABLE_NON_RECURRING_HOLIDAY, this.getSeasonChange(),TOUCalendarBuilder.CNT_NR_HOLIDAY);
		bos.write(tmp);
				
		//End Message
		tmp = makeHeader(MX2_DataConstants.TABLE_END_MESSAGE, this.getSeasonChange());
		bos.write(tmp);
		
		return bos.toByteArray();
	}
	
}
