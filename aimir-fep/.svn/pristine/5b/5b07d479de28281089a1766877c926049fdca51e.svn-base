package com.aimir.fep.bems.sender;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


/**
 * (BEMS)Metering Data transfer object
 * @author kskim
 */
public class MeteringDTO {
	
	//size 8
	private byte[] yyyymmdd = new byte[8];
	
	//size 2
	private byte[] hh = new byte[2];
	
	//size 2
	private byte[] mm = new byte[2];
	
	//Daylight Saving Time 사용여부 size 1, 0:사용안함,  1:DST적용  (Daylight Saving Time)
	private byte dst = (byte)'0';
	
	//검침 에너지 유형, size 1, 1:전력, 2:가스, 3:수도, 4:유량
//	private byte energyType;
	
	//검침 유형, size 1,  0:정기검침, 1:온디멘드
	private byte meteringType=(byte)'0';
	
	//MID 미터기 고유 ID, size 20  빈공간은 앞쪽부터 여백(space)으로 채움
	private byte[] meterSerial = new byte[20];
	
	//검침 데이터 유형, size 3
	private byte[] meterChannel = new byte[4];
	
	//검침 값 float type, size 9
	private byte[] meteringData = new byte[9];
	
	private byte[] cumulativeData = new byte[12];
	
	
	//처리시간, size 14
//	private byte[] processTime = new byte[14];

	
	
	
	public byte[] getYyyymmdd() {
		return yyyymmdd;
	}
	
	public void setYyyymmdd(byte[] yyyymmdd) {
		copyFixArray(yyyymmdd,this.yyyymmdd);
	}




	public byte[] getHh() {
		return hh;
	}




	public void setHh(byte[] hh) {
		copyFixArray(hh,this.hh);
	}




	public byte[] getMm() {
		return mm;
	}




	public void setMm(byte[] mm) {
		copyFixArray(mm,this.mm);
	}




	public byte getDst() {
		return dst;
	}




	public void setDst(byte dst) {
		this.dst = dst;
	}




//	public byte getEnergyType() {
//		return energyType;
//	}
//
//
//
//
//	public void setEnergyType(byte energyType) {
//		this.energyType = energyType;
//	}




	public byte getMeteringType() {
		return meteringType;
	}




	public void setMeteringType(byte meteringType) {
		this.meteringType = meteringType;
	}




	public byte[] getMeterSerial() {
		return meterSerial;
	}




	public void setMeterSerial(byte[] meterSerial) {
		copyFixArray(meterSerial,this.meterSerial);
	}




	public byte[] getMeterChannel() {
		return meterChannel;
	}




	public void setMeterChannel(byte[] meterChannel) {
		copyFixArray(meterChannel,this.meterChannel);
	}




	public byte[] getMeteringData() {
		return meteringData;
	}




	public void setMeteringData(byte[] meteringData) {
		copyFixArray(meteringData,this.meteringData);
	}




//	public byte[] getProcessTime() {
//		return processTime;
//	}
//
//
//
//
//	public void setProcessTime(byte[] processTime) {
//		copyFixArray(processTime,this.processTime);
//	}


	public byte[] getCumulativeData() {
		return cumulativeData;
	}
	
	
	public void setCumulativeData(byte[] cumulativeData) {
		copyFixArray(cumulativeData,this.cumulativeData);
	}

	/**
	 * 바이트 배열을 복사 하되 dest 객체 size 에 맞도록 복사한다.
	 * @param src
	 * @param dest
	 */
	public void copyFixArray(byte[] src,byte[] dest) {
		if(src==null){
			return;
		}
		
		if(src.length<dest.length) {
			System.arraycopy(src, 0, dest, 0, src.length);
		}else{
			System.arraycopy(src, 0, dest, 0, dest.length);
		}
	}

	/**
	 * 전송용 직렬 데이터 배열.
	 * @return
	 */
	public byte[] getBytes(){
		ByteArrayOutputStream bis = new ByteArrayOutputStream();
		
		try {
			bis.write(getYyyymmdd());
			bis.write(getHh());
			bis.write(getMm());
			bis.write(getDst());
//			bis.write(getEnergyType());
			bis.write(getMeteringType());
			bis.write(getMeterSerial());
			bis.write(getMeterChannel());
			bis.write(getMeteringData());
			bis.write(getCumulativeData());
//			bis.write(getProcessTime());
			bis.flush();
		} catch (IOException e) {
			return null;
		}
		
		return bis.toByteArray();
	}
}
