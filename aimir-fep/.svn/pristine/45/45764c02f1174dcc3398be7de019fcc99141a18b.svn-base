package com.aimir.fep.bypass;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;

public class CreateFrame {
	final byte CODE_EOT=0x05;

	public static void main(String[] args) throws Exception{
		CreateFrame cf = new CreateFrame();
		cf.frameOfGPRS("3539430412249280");
	}
	
	public void frameOfGPRS(String modemSerial) throws IOException{
		//bypass 모드를 실행할때 GPRS 모뎀에서 보내는 프레임 생성.
		ByteArrayOutputStream bis = new ByteArrayOutputStream();
		
		//header
		bis.write(CODE_EOT);
		
		//convert modem ID
		BigInteger biModemSerial = new BigInteger(modemSerial,16);
		byte[] modemId = biModemSerial.toByteArray();
		
		//length
		Integer length = modemId.length;
		byte[] bLength = integerTobyte(length);
		bis.write(bLength);
		
		//set modem ID
		bis.write(modemId);
	
		bis.flush();
		
		BigInteger b = new BigInteger(bis.toByteArray());
		String bs = b.toString(16);
		if(bs.length() % 2 != 0){
			bs = '0'+bs;
		}
			
		System.out.println(bs.length() % 2);
	}
	
	/**
	 * Integer is converted as byte array of 2 size<br>
	 * little-endian type.
	 * @param n
	 * @return
	 */
	public byte[] integerTobyte(Integer n){
		byte[] b = new byte[2];
		
		b[0] = n.byteValue();
		b[1] = (byte) (n >> 8);
		
		return b;
	}
}
