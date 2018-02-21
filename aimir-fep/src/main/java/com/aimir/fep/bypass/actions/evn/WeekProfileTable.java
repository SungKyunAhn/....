//
// 이 파일은 JAXB(JavaTM Architecture for XML Binding) 참조 구현 2.2.8-b130911.1802 버전을 통해 생성되었습니다. 
// <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>를 참조하십시오. 
// 이 파일을 수정하면 소스 스키마를 재컴파일할 때 수정 사항이 손실됩니다. 
// 생성 날짜: 2016.05.11 시간 05:36:04 PM KST 
//

package com.aimir.fep.bypass.actions.evn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.aimir.fep.util.DataUtil;

/**
 * <p>
 * anonymous complex type에 대한 Java 클래스입니다.
 * 
 * <p>
 * 다음 스키마 단편이 이 클래스에 포함되는 필요한 콘텐츠를 지정합니다.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RecordList" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Week_Profile_Name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="MON" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                   &lt;element name="TUE" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                   &lt;element name="WED" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                   &lt;element name="THU" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                   &lt;element name="FRI" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                   &lt;element name="SAT" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                   &lt;element name="SUN" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "recordList" })
@XmlRootElement(name = "Capture_objects")
public class WeekProfileTable {

	@XmlElement(name = "RecordList")
	protected List<WeekProfileTable.RecordList> recordList;

	/**
	 * Gets the value of the recordList property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the recordList property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getRecordList().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link WeekProfileTable.RecordList }
	 * 
	 * 
	 */
	public List<WeekProfileTable.RecordList> getRecordList() {
		if (recordList == null) {
			recordList = new ArrayList<WeekProfileTable.RecordList>();
		}
		return this.recordList;
	}

	/**
	 * <p>
	 * anonymous complex type에 대한 Java 클래스입니다.
	 * 
	 * <p>
	 * 다음 스키마 단편이 이 클래스에 포함되는 필요한 콘텐츠를 지정합니다.
	 * 
	 * <pre>
	 * &lt;complexType>
	 *   &lt;complexContent>
	 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *       &lt;sequence>
	 *         &lt;element name="Week_Profile_Name" type="{http://www.w3.org/2001/XMLSchema}string"/>
	 *         &lt;element name="MON" type="{http://www.w3.org/2001/XMLSchema}int"/>
	 *         &lt;element name="TUE" type="{http://www.w3.org/2001/XMLSchema}int"/>
	 *         &lt;element name="WED" type="{http://www.w3.org/2001/XMLSchema}int"/>
	 *         &lt;element name="THU" type="{http://www.w3.org/2001/XMLSchema}int"/>
	 *         &lt;element name="FRI" type="{http://www.w3.org/2001/XMLSchema}int"/>
	 *         &lt;element name="SAT" type="{http://www.w3.org/2001/XMLSchema}int"/>
	 *         &lt;element name="SUN" type="{http://www.w3.org/2001/XMLSchema}int"/>
	 *       &lt;/sequence>
	 *     &lt;/restriction>
	 *   &lt;/complexContent>
	 * &lt;/complexType>
	 * </pre>
	 * 
	 * 
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "", propOrder = { "weekProfileName", "mon", "tue", "wed", "thu", "fri", "sat", "sun" })
	public static class RecordList {

		@XmlElement(name = "Week_Profile_Name", required = true)
		protected String weekProfileName;
		@XmlElement(name = "MON")
		protected int mon;
		@XmlElement(name = "TUE")
		protected int tue;
		@XmlElement(name = "WED")
		protected int wed;
		@XmlElement(name = "THU")
		protected int thu;
		@XmlElement(name = "FRI")
		protected int fri;
		@XmlElement(name = "SAT")
		protected int sat;
		@XmlElement(name = "SUN")
		protected int sun;

		/**
		 * weekProfileName 속성의 값을 가져옵니다.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getWeekProfileName() {
			return weekProfileName;
		}

		/**
		 * weekProfileName 속성의 값을 설정합니다.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setWeekProfileName(String value) {
			this.weekProfileName = value;
		}

		/**
		 * mon 속성의 값을 가져옵니다.
		 * 
		 */
		public int getMON() {
			return mon;
		}

		/**
		 * mon 속성의 값을 설정합니다.
		 * 
		 */
		public void setMON(int value) {
			this.mon = value;
		}

		/**
		 * tue 속성의 값을 가져옵니다.
		 * 
		 */
		public int getTUE() {
			return tue;
		}

		/**
		 * tue 속성의 값을 설정합니다.
		 * 
		 */
		public void setTUE(int value) {
			this.tue = value;
		}

		/**
		 * wed 속성의 값을 가져옵니다.
		 * 
		 */
		public int getWED() {
			return wed;
		}

		/**
		 * wed 속성의 값을 설정합니다.
		 * 
		 */
		public void setWED(int value) {
			this.wed = value;
		}

		/**
		 * thu 속성의 값을 가져옵니다.
		 * 
		 */
		public int getTHU() {
			return thu;
		}

		/**
		 * thu 속성의 값을 설정합니다.
		 * 
		 */
		public void setTHU(int value) {
			this.thu = value;
		}

		/**
		 * fri 속성의 값을 가져옵니다.
		 * 
		 */
		public int getFRI() {
			return fri;
		}

		/**
		 * fri 속성의 값을 설정합니다.
		 * 
		 */
		public void setFRI(int value) {
			this.fri = value;
		}

		/**
		 * sat 속성의 값을 가져옵니다.
		 * 
		 */
		public int getSAT() {
			return sat;
		}

		/**
		 * sat 속성의 값을 설정합니다.
		 * 
		 */
		public void setSAT(int value) {
			this.sat = value;
		}

		/**
		 * sun 속성의 값을 가져옵니다.
		 * 
		 */
		public int getSUN() {
			return sun;
		}

		/**
		 * sun 속성의 값을 설정합니다.
		 * 
		 */
		public void setSUN(int value) {
			this.sun = value;
		}

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
		}
	}

	public void addRecordList(RecordList weekRecordList1) {
		if (recordList == null) {
			getRecordList().add(weekRecordList1);
		} else {
			recordList.add(weekRecordList1);
		}
	}

	public byte[] getBytes() {
		/*
		<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
		<Capture_objects>
			<RecordList>
				<Week_Profile_Name>W1</Week_Profile_Name>
				<MON>1</MON>
				<TUE>1</TUE>
				<WED>1</WED>
				<THU>1</THU>
				<FRI>1</FRI>
				<SAT>1</SAT>
				<SUN>1</SUN>
			</RecordList>
			<RecordList>
				<Week_Profile_Name>W2</Week_Profile_Name>
				<MON>2</MON>
				<TUE>2</TUE>
				<WED>2</WED>
				<THU>2</THU>
				<FRI>2</FRI>
				<SAT>2</SAT>
				<SUN>2</SUN>
			</RecordList>
		</Capture_objects>
		 */

		/*
		7EA0460002007F0398948F
		E6E600
		C10142
		0014	00000D0000FF 08 00
		01 02 
		02 08
			09 02 5731
			11 01
			11 01
			11 01
			11 01
			11 01
			11 01
			11 01
		02 08
			09 02 5732
			11 02
			11 02
			11 02
			11 02
			11 02
			11 02
			11 02
		EE2B7E		 
		 */

		HashMap<String, List<RecordList>> listMap = new LinkedHashMap<String, List<RecordList>>();
		for (RecordList rList : getRecordList()) {
			if (listMap.containsKey(rList.getWeekProfileName())) {
				listMap.get(rList.getWeekProfileName()).add(rList);
			} else {
				List<RecordList> newList = new LinkedList<WeekProfileTable.RecordList>();
				newList.add(rList);
				listMap.put(rList.getWeekProfileName(), newList);
			}
		}

		byte[] results = new byte[] { 0x01, DataUtil.getByteToInt(listMap.size()) }; // array, length

		Set<String> set = listMap.keySet();
		Iterator<String> it = set.iterator();
		while (it.hasNext()) {
			String key = it.next();

			List<RecordList> recordList = listMap.get(key);
			for (RecordList record : recordList) {
				results = DataUtil.append(results, new byte[] { 0x02, 0x08 }); // structure, length

				byte[] weekProfileName = new byte[2];
				weekProfileName[0] = (byte) 0x09;
				weekProfileName[1] = (byte) 0x02;
				results = DataUtil.append(results, weekProfileName);
				results = DataUtil.append(results, record.getWeekProfileName().getBytes()); // Week_Profile_Name

				results = DataUtil.append(results, new byte[] { 0x11, DataUtil.getByteToInt(record.getMON()) }); // Mon Day
				results = DataUtil.append(results, new byte[] { 0x11, DataUtil.getByteToInt(record.getTUE()) }); // Tue Day
				results = DataUtil.append(results, new byte[] { 0x11, DataUtil.getByteToInt(record.getWED()) }); // Wed Day
				results = DataUtil.append(results, new byte[] { 0x11, DataUtil.getByteToInt(record.getTHU()) }); // Thu Day
				results = DataUtil.append(results, new byte[] { 0x11, DataUtil.getByteToInt(record.getFRI()) }); // Fri Day
				results = DataUtil.append(results, new byte[] { 0x11, DataUtil.getByteToInt(record.getSAT()) }); // Sat Day
				results = DataUtil.append(results, new byte[] { 0x11, DataUtil.getByteToInt(record.getSUN()) }); // Sun Day				
			}
		}

		return results;

	}

}
