package com.aimir.fep.meter.parser.MX2Table;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.List;

import javax.jws.WebService;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aimir.fep.protocol.mrp.protocol.MX2_DataConstants;
import com.aimir.fep.util.DataUtil;


/**
 * MX2미터 Display Item Selection 설정을위한 VO객체.
 * @author jiae
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "displayItemSetting", propOrder = {
		   "normalDisplayItemsSelect",
		   "alternateDisplayItemsSelect",
		   "testDisplayItemsSelect",
		   "normalDisplayIdSet1",
		   "normalDisplayIdSet2",
		   "alternateDisplayIdSet1",
		   "alternateDisplayIdSet2",
		   "testDisplayId"
		})
public class DisplayItemSetting implements java.io.Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8335413725926026171L;
	@XmlElement
	List<DisplayItemsSelect> normalDisplayItemsSelect;
	@XmlElement
	List<DisplayItemsSelect> alternateDisplayItemsSelect;
	@XmlElement
	List<DisplayItemsSelect> testDisplayItemsSelect;
	@XmlElement
	List<DisplayIdSet> normalDisplayIdSet1;
	@XmlElement
	List<DisplayIdSet> normalDisplayIdSet2;
	@XmlElement
	List<DisplayIdSet> alternateDisplayIdSet1;
	@XmlElement
	List<DisplayIdSet> alternateDisplayIdSet2;
	@XmlElement
	List<DisplayIdSet> testDisplayId;
	
	public byte[] getNormalDisplayItemsSelect() throws Exception {
		if(normalDisplayItemsSelect == null)
			throw new Exception("Can not found Normal Display Item");
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		for (DisplayItemsSelect t : normalDisplayItemsSelect) {
			bos.write(t.toByteArray());
		}
		byte[] data = DataUtil.fillCopy(bos.toByteArray(), (byte)0xff, DisplayItemSettingBuilder.LEN_NORMAL_DISPLAY_ITEM);
		bos.close();
		
		return data;
	}

	public void setNormalDisplayItemsSelect(
			List<DisplayItemsSelect> normalDisplayItemsSelect) {
		this.normalDisplayItemsSelect = normalDisplayItemsSelect;
	}

	public byte[] getAlternateDisplayItemsSelect() throws Exception {
		if(alternateDisplayItemsSelect == null)
			throw new Exception("Can not found Alternate Display Item");
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		for (DisplayItemsSelect t : alternateDisplayItemsSelect) {
			bos.write(t.toByteArray());
		}
		byte[] data = DataUtil.fillCopy(bos.toByteArray(), (byte)0xff, DisplayItemSettingBuilder.LEN_ALTERNATE_DISPLAY_ITEM);
		bos.close();
		
		return data;
	}

	public void setAlternateDisplayItemsSelect(
			List<DisplayItemsSelect> alternateDisplayItemsSelect) {
		this.alternateDisplayItemsSelect = alternateDisplayItemsSelect;
	}

	public byte[] getTestDisplayItemsSelect() throws Exception {
		if(testDisplayItemsSelect == null)
			throw new Exception("Can not found Test Display Item");
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		for (DisplayItemsSelect t : testDisplayItemsSelect) {
			bos.write(t.toByteArray());
		}
		byte[] data = DataUtil.fillCopy(bos.toByteArray(), (byte)0xff, DisplayItemSettingBuilder.LEN_TEST_DISPLAY_ITEM);
		bos.close();
		
		return data;
	}

	public void setTestDisplayItemsSelect(
			List<DisplayItemsSelect> testDisplayItemsSelect) {
		this.testDisplayItemsSelect = testDisplayItemsSelect;
	}

	public byte[] getNormalDisplayIdSet1() throws Exception {
		if(normalDisplayIdSet1 == null)
			throw new Exception("Can not found Normal DisplayId Set1");
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		for (DisplayIdSet t : normalDisplayIdSet1) {
			bos.write(t.toByteArray());
		}
		byte[] data = DataUtil.fillCopy(bos.toByteArray(), (byte)0xff, DisplayItemSettingBuilder.LEN_NORMAL_DISPLAY_ID);
		bos.close();
		
		return data;
	}

	public void setNormalDisplayIdSet1(List<DisplayIdSet> normalDisplayIdSet1) {
		this.normalDisplayIdSet1 = normalDisplayIdSet1;
	}

	public byte[] getNormalDisplayIdSet2() throws Exception {
		if(normalDisplayIdSet2 == null)
			throw new Exception("Can not found Normal DisplayId Set2");
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		for (DisplayIdSet t : normalDisplayIdSet2) {
			bos.write(t.toByteArray());
		}
		byte[] data = DataUtil.fillCopy(bos.toByteArray(), (byte)0xff, DisplayItemSettingBuilder.LEN_NORMAL_DISPLAY_ID);
		bos.close();
		
		return data;
	}

	public void setNormalDisplayIdSet2(List<DisplayIdSet> normalDisplayIdSet2) {
		this.normalDisplayIdSet2 = normalDisplayIdSet2;
	}

	public byte[] getAlternateDisplayIdSet1() throws Exception {
		if(alternateDisplayIdSet1 == null)
			throw new Exception("Can not found Alternate DisplayId Set1");
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
    		for (DisplayIdSet t : alternateDisplayIdSet1) {
			bos.write(t.toByteArray());
		}
		byte[] data = DataUtil.fillCopy(bos.toByteArray(), (byte)0xff, DisplayItemSettingBuilder.LEN_ALTERNATE_DISPLAY_ID);
		bos.close();
		
		return data;
	}

	public void setAlternateDisplayIdSet1(List<DisplayIdSet> alternateDisplayIdSet1) {
		this.alternateDisplayIdSet1 = alternateDisplayIdSet1;
	}

	public byte[] getAlternateDisplayIdSet2() throws Exception {
		if(alternateDisplayIdSet2 == null)
			throw new Exception("Can not found Alternate DisplayId Set2");
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		for (DisplayIdSet t : alternateDisplayIdSet2) {
			bos.write(t.toByteArray());
		}
		byte[] data = DataUtil.fillCopy(bos.toByteArray(), (byte)0xff, DisplayItemSettingBuilder.LEN_ALTERNATE_DISPLAY_ID);
		bos.close();
		
		return data;
	}

	public void setAlternateDisplayIdSet2(List<DisplayIdSet> alternateDisplayIdSet2) {
		this.alternateDisplayIdSet2 = alternateDisplayIdSet2;
	}

	public byte[] getTestDisplayId() throws Exception {
		if(testDisplayId == null)
			throw new Exception("Can not found Test DisplayId");
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		for (DisplayIdSet t : testDisplayId) {
			bos.write(t.toByteArray());
		}
		byte[] data = DataUtil.fillCopy(bos.toByteArray(), (byte)0xff, DisplayItemSettingBuilder.LEN_TEST_DISPLAY_ID);
		bos.close();
		
		return data;
	}

	public void setTestDisplayId(List<DisplayIdSet> testDisplayId) {
		this.testDisplayId = testDisplayId;
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
		tmp = makeHeader(MX2_DataConstants.TABLE_NORMAL_DISPLAY_ITEMS_SELECT, this.getNormalDisplayItemsSelect(), DisplayItemSettingBuilder.LEN_NORMAL_DISPLAY_ITEM);
		bos.write(tmp);
		
		tmp = makeHeader(MX2_DataConstants.TABLE_NORMAL_DISPLAY_ID_SET1, this.getNormalDisplayIdSet1(), DisplayItemSettingBuilder.LEN_NORMAL_DISPLAY_ID);
		bos.write(tmp);
		
		tmp = makeHeader(MX2_DataConstants.TABLE_NORMAL_DISPLAY_ID_SET2, this.getNormalDisplayIdSet2(), DisplayItemSettingBuilder.LEN_NORMAL_DISPLAY_ID);
		bos.write(tmp);
		
		tmp = makeHeader(MX2_DataConstants.TABLE_ALTERVATE_DISPLAY_ITEMS_SELECT, this.getAlternateDisplayItemsSelect(), DisplayItemSettingBuilder.LEN_ALTERNATE_DISPLAY_ITEM);
		bos.write(tmp);
		
		tmp = makeHeader(MX2_DataConstants.TABLE_ALTERVATE_DISPLAY_ID_SET1, this.getAlternateDisplayIdSet1(), DisplayItemSettingBuilder.LEN_ALTERNATE_DISPLAY_ID);
		bos.write(tmp);
		
		tmp = makeHeader(MX2_DataConstants.TABLE_ALTERVATE_DISPLAY_ID_SET2, this.getAlternateDisplayIdSet2(), DisplayItemSettingBuilder.LEN_ALTERNATE_DISPLAY_ID);
		bos.write(tmp);
		
		tmp = makeHeader(MX2_DataConstants.TABLE_TEST_DISPLAY_ITEMS_SELECT, this.getTestDisplayItemsSelect(), DisplayItemSettingBuilder.LEN_TEST_DISPLAY_ITEM);
		bos.write(tmp);
		
		tmp = makeHeader(MX2_DataConstants.TABLE_TEST_DISPLAY_ID, this.getTestDisplayId(), DisplayItemSettingBuilder.LEN_TEST_DISPLAY_ID);
		bos.write(tmp);
		
		return bos.toByteArray();
	}
	
}
