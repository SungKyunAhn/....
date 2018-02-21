//
// 이 파일은 JAXB(JavaTM Architecture for XML Binding) 참조 구현 2.2.8-b130911.1802 버전을 통해 생성되었습니다. 
// <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>를 참조하십시오. 
// 이 파일을 수정하면 소스 스키마를 재컴파일할 때 수정 사항이 손실됩니다. 
// 생성 날짜: 2016.05.11 시간 05:24:31 PM KST 
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

import org.apache.commons.collections.IterableMap;
import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.aimir.fep.util.DataUtil;


/**
 * <p>anonymous complex type에 대한 Java 클래스입니다.
 * 
 * <p>다음 스키마 단편이 이 클래스에 포함되는 필요한 콘텐츠를 지정합니다.
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
 *                   &lt;element name="Day_ID" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                   &lt;element name="Time_Bucket" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                   &lt;element name="Start_Time" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="Script_Logical_Name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="Script_Selector" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
@XmlType(name = "", propOrder = {
    "recordList"
})
@XmlRootElement(name = "Capture_objects")
public class DayProfileTable {

    @XmlElement(name = "RecordList")
    protected List<DayProfileTable.RecordList> recordList;

    /**
     * Gets the value of the recordList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the recordList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRecordList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DayProfileTable.RecordList }
     * 
     * 
     */
    public List<DayProfileTable.RecordList> getRecordList() {
        if (recordList == null) {
            recordList = new ArrayList<DayProfileTable.RecordList>();
        }
        return this.recordList;
    }


    /**
     * <p>anonymous complex type에 대한 Java 클래스입니다.
     * 
     * <p>다음 스키마 단편이 이 클래스에 포함되는 필요한 콘텐츠를 지정합니다.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="Day_ID" type="{http://www.w3.org/2001/XMLSchema}int"/>
     *         &lt;element name="Time_Bucket" type="{http://www.w3.org/2001/XMLSchema}int"/>
     *         &lt;element name="Start_Time" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="Script_Logical_Name" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="Script_Selector" type="{http://www.w3.org/2001/XMLSchema}int"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "dayID",
        "timeBucket",
        "startTime",
        "scriptLogicalName",
        "scriptSelector"
    })
    public static class RecordList {

        @XmlElement(name = "Day_ID")
        protected int dayID;
        @XmlElement(name = "Time_Bucket")
        protected int timeBucket;
        @XmlElement(name = "Start_Time", required = true)
        protected String startTime;
        @XmlElement(name = "Script_Logical_Name", required = true)
        protected String scriptLogicalName;
        @XmlElement(name = "Script_Selector")
        protected int scriptSelector;

        /**
         * dayID 속성의 값을 가져옵니다.
         * 
         */
        public int getDayID() {
            return dayID;
        }

        /**
         * dayID 속성의 값을 설정합니다.
         * 
         */
        public void setDayID(int value) {
            this.dayID = value;
        }

        /**
         * timeBucket 속성의 값을 가져옵니다.
         * 
         */
        public int getTimeBucket() {
            return timeBucket;
        }

        /**
         * timeBucket 속성의 값을 설정합니다.
         * 
         */
        public void setTimeBucket(int value) {
            this.timeBucket = value;
        }

        /**
         * startTime 속성의 값을 가져옵니다.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getStartTime() {
            return startTime;
        }

        /**
         * startTime 속성의 값을 설정합니다.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setStartTime(String value) {
            this.startTime = value;
        }

        /**
         * scriptLogicalName 속성의 값을 가져옵니다.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getScriptLogicalName() {
            return scriptLogicalName;
        }

        /**
         * scriptLogicalName 속성의 값을 설정합니다.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setScriptLogicalName(String value) {
            this.scriptLogicalName = value;
        }

        /**
         * scriptSelector 속성의 값을 가져옵니다.
         * 
         */
        public int getScriptSelector() {
            return scriptSelector;
        }

        /**
         * scriptSelector 속성의 값을 설정합니다.
         * 
         */
        public void setScriptSelector(int value) {
            this.scriptSelector = value;
        }
        
        @Override
        public String toString() {
           	return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
        }
        
    }

	public void addRecordList(RecordList dayRecordList1) {
        if (recordList == null) {
            getRecordList().add(dayRecordList1);
        }else{
        	recordList.add(dayRecordList1);
        }
	}
	
	public byte[] getBytes(){
		/*
			<RecordList>
				<Day_ID>1</Day_ID>
				<Time_Bucket>1</Time_Bucket>
				<Start_Time>00:00:00</Start_Time>
				<Script_Logical_Name>0.0.10.0.100.255</Script_Logical_Name>
				<Script_Selector>1</Script_Selector>
			</RecordList> 
		 */
		HashMap<Integer, List<RecordList>> listMap = new LinkedHashMap<Integer, List<RecordList>>();
		for(RecordList rList : getRecordList()){
			if(listMap.containsKey(rList.getDayID())){
				listMap.get(rList.getDayID()).add(rList);
			}else{
				List<RecordList> newList = new LinkedList<DayProfileTable.RecordList>();
				newList.add(rList);
				listMap.put(rList.getDayID(), newList);				
			}
		}

		byte[] results = new byte[]{0x01, DataUtil.getByteToInt(listMap.size())};  // array, length
		
		Set<Integer> set = listMap.keySet();
		Iterator<Integer> it = set.iterator();
		while(it.hasNext()){
			int key = it.next();
			
			results = DataUtil.append(results, new byte[]{0x02, 0x02}); // structure, length
			results = DataUtil.append(results, new byte[]{0x11, DataUtil.getByteToInt(key)}); // UINT8, Day_ID
			
			List<RecordList> recordList = listMap.get(key);
			results = DataUtil.append(results, new byte[]{0x01, DataUtil.getByteToInt(recordList.size())}); // array, record Length
			for(RecordList record : recordList){
				results = DataUtil.append(results, new byte[]{0x02, 0x03});  // structure, length
				
				String[] times = record.getStartTime().split(":");
				byte[] startTime = new byte[6];
				startTime[0] = (byte)0x09;
				startTime[1] = (byte)0x04;
				startTime[2] = (byte)DataUtil.getByteToInt(Integer.parseInt(times[0]));
				startTime[3] = (byte)DataUtil.getByteToInt(Integer.parseInt(times[1]));
				startTime[4] = (byte)DataUtil.getByteToInt(Integer.parseInt(times[2]));
				startTime[5] = (byte)0x00;
				results = DataUtil.append(results, startTime);  // Start_Time
				
				String[] obisCode = record.getScriptLogicalName().replace(".", ":").split(":");
				byte[] obisArray = new byte[]{};
				for(String s : obisCode){
					obisArray = DataUtil.append(obisArray, new byte[]{DataUtil.getByteToInt(Integer.parseInt(s))});
				}
				
				byte[] logicalName = new byte[2];
				logicalName[0] = (byte)0x09;
				logicalName[1] = (byte)0x06;
				results = DataUtil.append(results, logicalName);
				results = DataUtil.append(results, obisArray); // Script_Logical_Name
				
				results = DataUtil.append(results, new byte[]{0x12});
				results = DataUtil.append(results, DataUtil.get2ByteToInt(record.getScriptSelector()));  // Script_Selector
			}
		}
		
		return results;
	}

}
