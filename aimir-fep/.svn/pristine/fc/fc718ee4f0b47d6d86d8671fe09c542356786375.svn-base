package com.aimir.fep.meter.parser.MX2Table;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <p>MX2 장비에서 사용하는 Table 의 공통 기능.</p>
 * <p>Object 자체를 Serial 변경하며, Serial 데이터로 Instance 를 생성한다.</p>
 * <p>생성된 Instance 는 해당 Object(상속받은) 로 캐스팅해서 사용한다.</p>
 * @author kskim
 *
 */
@SuppressWarnings("serial")
public abstract class CommonTable implements java.io.Serializable {
	
	/**
	 * bcd byte array 를 String 으로 변환
	 * @param data
	 * @return
	 */
	protected String bcdToString(byte[] data){
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			sb.append(String.format("%02x", data[i]));
		}
		return sb.toString();
	}
	
	/**
	 * BCD Type 의 byte array 를 Date 으로 변환해준다.
	 * 
	 * @param dateTime
	 * @param pattern
	 * @return
	 * @throws ParseException
	 */
	@SuppressWarnings("unused")
	protected Date bcdToDate(byte[] dateTime, String pattern)
			throws ParseException {
		String bcdString = bcdToString(dateTime);
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
		Date date = dateFormat.parse(bcdString);
		return date;
	}
	
	/**
	 * BCD Type 의 byte array 를 String date 로 변환.
	 * @param dateTime
	 * @param pattern
	 * @return
	 */
	protected String bcdToDateString(byte[] dateTime, String pattern){
		try {
			Date date = bcdToDate(dateTime,pattern);
			SimpleDateFormat toFormat = new SimpleDateFormat(pattern);
			return toFormat.format(date);
		} catch (ParseException e) {
			return null;
		}
		
	}
	
	
	/**
	 * 날짜 스트링을 입력받아 다른 날짜 포멧으로 변환한다.
	 * 
	 * @param dateString
	 *            날짜 스트링
	 * @param strFromFormat
	 *            날짜 스트링의 포멧
	 * @param strToFormat
	 *            변경할 포멧
	 * @return
	 */
	protected String convertDateFormat(String dateString, String strFromFormat,
			String strToFormat) {
		SimpleDateFormat fromformat = new SimpleDateFormat(strFromFormat);
		SimpleDateFormat toFormat = new SimpleDateFormat(strToFormat);
		try {
			Date date = fromformat.parse(dateString);
			return toFormat.format(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return dateString;
		}
	}
	
	protected int byteArrayToInt(byte[] byteArray, int pos, int len){
		byte[] bytes = new byte[len];
		System.arraycopy(byteArray, pos, bytes, 0, len);
		return byteArrayToInt(bytes);
	}
	
	/**
	 * Byte Array 를 int 로 변환해준다. 4byte 초과일경우 변환하지 않고 -1 리턴
	 * 
	 * @param byteArray
	 * @return
	 */
	protected int byteArrayToInt(byte[] byteArray) {
		int MAXIMUM_LENGTH = 4;
		if (byteArray.length > MAXIMUM_LENGTH)
			return -1;

		// 버퍼 생성.
		ByteBuffer buf = ByteBuffer.allocate(MAXIMUM_LENGTH);

		int skip = MAXIMUM_LENGTH - byteArray.length;

		// 4byte 미만일경우 차이만큼 이동해서 쓴다.
		buf.position(skip);
		buf.put(byteArray);
		buf.flip();

		// int 로 형변환 한다.
		int n = buf.getInt();
		if(n == 65535) {//0xFFFF인 경우 데이터가 유효하지 않은 것으로 처리한다.
			n = 1;
		}
		return n;
	}

	///////////////////////////////////////////////////////////////
	// 시리얼 관련
	///////////////////////////////////////////////////////////////
	
	/**
	 * 시리얼 데이터로 새로운 인스턴스(Object)를 생성한다.
	 * @deprecated
	 * @param serialObject
	 * @return
	 */
	public static Object newInstance(byte[] serialObject){
		ByteArrayInputStream bis = new ByteArrayInputStream(serialObject);
		ObjectInput in;
		try {
			in = new ObjectInputStream(bis);
			Object o = in.readObject();
			bis.close();
			in.close();
			return o;
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * this Object 를 시리얼 데이터(byte array)로 변환한다.
	 * @deprecated
	 * @return this to byte array
	 */
	public byte[] toByteArray(){
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			//object 를 byte array 로 만들고
			ObjectOutput out = new ObjectOutputStream(bos);
			out.writeObject(this);
			byte[] b = bos.toByteArray();
			out.close();
			bos.close();
			return b;
		} catch (Exception e) {
			return null;
		}
	}
}
