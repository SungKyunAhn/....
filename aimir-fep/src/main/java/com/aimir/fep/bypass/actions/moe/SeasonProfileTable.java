//
// 이 파일은 JAXB(JavaTM Architecture for XML Binding) 참조 구현 2.2.8-b130911.1802 버전을 통해 생성되었습니다. 
// <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>를 참조하십시오. 
// 이 파일을 수정하면 소스 스키마를 재컴파일할 때 수정 사항이 손실됩니다. 
// 생성 날짜: 2016.05.11 시간 05:18:15 PM KST 
//

package com.aimir.fep.bypass.actions.moe;

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
 *                   &lt;element name="Season_Profile_Name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="Season_Start" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="Week_Name" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
public class SeasonProfileTable {

	@XmlElement(name = "RecordList")
	protected List<SeasonProfileTable.RecordList> recordList;

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
	 * {@link SeasonProfileTable.RecordList }
	 * 
	 * 
	 */
	public List<SeasonProfileTable.RecordList> getRecordList() {
		if (recordList == null) {
			recordList = new ArrayList<SeasonProfileTable.RecordList>();
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
	 *         &lt;element name="Season_Profile_Name" type="{http://www.w3.org/2001/XMLSchema}string"/>
	 *         &lt;element name="Season_Start" type="{http://www.w3.org/2001/XMLSchema}string"/>
	 *         &lt;element name="Week_Name" type="{http://www.w3.org/2001/XMLSchema}string"/>
	 *       &lt;/sequence>
	 *     &lt;/restriction>
	 *   &lt;/complexContent>
	 * &lt;/complexType>
	 * </pre>
	 * 
	 * 
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "", propOrder = { "seasonProfileName", "seasonStart", "weekName" })
	public static class RecordList {

		@XmlElement(name = "Season_Profile_Name", required = true)
		protected String seasonProfileName;
		@XmlElement(name = "Season_Start", required = true)
		protected String seasonStart;
		@XmlElement(name = "Week_Name", required = true)
		protected String weekName;

		/**
		 * seasonProfileName 속성의 값을 가져옵니다.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getSeasonProfileName() {
			return seasonProfileName;
		}

		/**
		 * seasonProfileName 속성의 값을 설정합니다.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setSeasonProfileName(String value) {
			this.seasonProfileName = value;
		}

		/**
		 * seasonStart 속성의 값을 가져옵니다.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getSeasonStart() {
			return seasonStart;
		}

		/**
		 * seasonStart 속성의 값을 설정합니다.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setSeasonStart(String value) {
			this.seasonStart = value;
		}

		/**
		 * weekName 속성의 값을 가져옵니다.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getWeekName() {
			return weekName;
		}

		/**
		 * weekName 속성의 값을 설정합니다.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setWeekName(String value) {
			this.weekName = value;
		}

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
		}
	}

	public void addRecordList(RecordList seasonRecordList1) {
		if (recordList == null) {
			getRecordList().add(seasonRecordList1);
		} else {
			recordList.add(seasonRecordList1);
		}
	}

	public byte[] getBytes() {
		/*
			<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
			<Capture_objects>
				<RecordList>
					<Season_Profile_Name>S1</Season_Profile_Name>
					<Season_Start>01-01-XXXX 00:00:00</Season_Start>
					<Week_Name>W1</Week_Name>
				</RecordList>
				<RecordList>
					<Season_Profile_Name>S2</Season_Profile_Name>
					<Season_Start>01-04-XXXX 00:00:00</Season_Start>
					<Week_Name>W1</Week_Name>
				</RecordList>
				<RecordList>
					<Season_Profile_Name>S3</Season_Profile_Name>
					<Season_Start>01-07-XXXX 00:00:00</Season_Start>
					<Week_Name>W2</Week_Name>
				</RecordList>
				<RecordList>
					<Season_Profile_Name>S4</Season_Profile_Name>
					<Season_Start>01-10-XXXX 00:00:00</Season_Start>
					<Week_Name>W1</Week_Name>
				</RecordList>
			</Capture_objects>

		 */
		HashMap<String, List<RecordList>> listMap = new LinkedHashMap<String, List<RecordList>>();
		for (RecordList rList : getRecordList()) {
			if (listMap.containsKey(rList.getSeasonProfileName())) {
				listMap.get(rList.getSeasonProfileName()).add(rList);
			} else {
				List<RecordList> newList = new LinkedList<SeasonProfileTable.RecordList>();
				newList.add(rList);
				listMap.put(rList.getSeasonProfileName(), newList);
			}
		}

		byte[] results = new byte[] {};
		Set<String> set = listMap.keySet();
		Iterator<String> it = set.iterator();
		while (it.hasNext()) {
			String key = it.next();

			List<RecordList> recordList = listMap.get(key);
			results = DataUtil.append(results, new byte[] { 0x01, DataUtil.getByteToInt(recordList.size()) }); // array, record Length
			for (RecordList record : recordList) {
				results = DataUtil.append(results, new byte[] { 0x02, 0x03 }); // structure, length

				byte[] seasonProfileName = new byte[2];
				seasonProfileName[0] = (byte) 0x09;
				seasonProfileName[1] = (byte) 0x02;
				results = DataUtil.append(results, seasonProfileName);
				results = DataUtil.append(results, record.getSeasonProfileName().getBytes()); // Season_Profile_Name

				String seasonStart = record.getSeasonStart().replace(" ", "");
				//01-01-XXXX00:00:00
				
				byte[] startTime = new byte[14];
				startTime[0] = (byte) 0x09;
				startTime[1] = (byte) 0x0C;
				startTime[2] = (byte) 0xFF;
				startTime[3] = (byte) 0xFF;
				startTime[4] = (byte) DataUtil.getByteToInt(Integer.parseInt(seasonStart.substring(3, 5))); // 월
				startTime[5] = (byte) DataUtil.getByteToInt(Integer.parseInt(seasonStart.substring(0, 2))); // 일
				startTime[6] = (byte) 0xFF;
				startTime[7] = (byte) DataUtil.getByteToInt(Integer.parseInt(seasonStart.substring(10, 12))); // 시
				startTime[8] = (byte) DataUtil.getByteToInt(Integer.parseInt(seasonStart.substring(13, 15))); // 분
				startTime[9] = (byte) DataUtil.getByteToInt(Integer.parseInt(seasonStart.substring(16, 18))); // 초
				startTime[10] = (byte) 0xFF;
				startTime[11] = (byte) 0x80;
				startTime[12] = (byte) 0x00;
				startTime[13] = (byte) 0x00;
				results = DataUtil.append(results, startTime); // Season_Start

				byte[] weekName = new byte[2];
				weekName[0] = (byte) 0x09;
				weekName[1] = (byte) 0x02;
				results = DataUtil.append(results, weekName);
				results = DataUtil.append(results, record.getWeekName().getBytes()); // Week_Name
			}
		}

		return results;
	}

}
