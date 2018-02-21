package com.aimir.fep.meter.parser.MX2Table;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.aimir.fep.meter.file.DisplayItemSettingCSV;
import com.aimir.fep.meter.file.DisplayItemSettingCSV.Finder;
import com.aimir.fep.util.Hex;
/**
 * Display Item Setting Value Object Builder Class
 * @author jiae
 * csv 파일을 파인더를 사용해서 읽어옴
 */
public class DisplayItemSettingBuilder {
	//CSV 에서 필요한 값들에 대한 Key
	static final String KEY_DISPLAY_ITEM_SELECTION = "Display_Item_Selection";
	static final String KEY_NORMAL_MODE = "Normal";
	static final String KEY_ALTERNATE_MODE = "Alternate";
	static final String KEY_TEST_MODE = "Test";
	
	static final String KEY_MODE = "Mode";
	static final String KEY_SEQNO = "Seq_No";
	static final String KEY_DISPLAY_ID = "Display_ID";
	static final String KEY_DISPLAY_ITEM = "Display_Item";
	
	//각 테이블 개수 (한개짜리는 제외)
	public static final int CNT_NORMAL_DISPLAY_ITEM = 40;
	public static final int CNT_ALTERNATE_DISPLAY_ITEM = 40;
	public static final int CNT_TEST_DISPLAY_ITEM = 20;
	public static final int CNT_NORMAL_DISPLAY_ID = 20;
	public static final int CNT_ALTERNATE_DISPLAY_ID = 20;
	public static final int CNT_TEST_DISPLAY_ID = 20;
	
	//테이블당 데이터 사이즈
	public static final int LEN_NORMAL_DISPLAY_ITEM  = 80;
	public static final int LEN_ALTERNATE_DISPLAY_ITEM = 80;
	public static final int LEN_TEST_DISPLAY_ITEM = 40;
	public static final int LEN_NORMAL_DISPLAY_ID = 60;
	public static final int LEN_ALTERNATE_DISPLAY_ID = 60;
	public static final int LEN_TEST_DISPLAY_ID = 60;


	/**
	 * @param csv
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public DisplayItemSetting build(DisplayItemSettingCSV csv) throws Exception{
		//csv 파일 정보를 읽어 DisplayItemSetting 객체를 생성한다.
		
		DisplayItemSetting displayItemSetting = new DisplayItemSetting();
	
		Finder f = csv.getFinder();

		//Normal Mode Display Items Select
		displayItemSetting.setNormalDisplayItemsSelect(parseNormalDisplayItemSelection(f));
		System.out.println(Hex.getHexDump(displayItemSetting.getNormalDisplayItemsSelect()));
		
		//Alternate Mode Display Items Select
		displayItemSetting.setAlternateDisplayItemsSelect(parseAlternateDisplayItemSelection(f));
		System.out.println(Hex.getHexDump(displayItemSetting.getAlternateDisplayItemsSelect()));
		
		//Test Mode Display Items Select
		displayItemSetting.setTestDisplayItemsSelect(parseTestDisplayItemSelection(f));
		System.out.println(Hex.getHexDump(displayItemSetting.getTestDisplayItemsSelect()));
		
		//Normal Mode DisplayId Set1
		displayItemSetting.setNormalDisplayIdSet1(parseNormalDisplayId(f).get(0));
		System.out.println(Hex.getHexDump(displayItemSetting.getNormalDisplayIdSet1()));
		
		//Normal Mode DisplayId Set2
		displayItemSetting.setNormalDisplayIdSet2(parseNormalDisplayId(f).get(1));
		System.out.println(Hex.getHexDump(displayItemSetting.getNormalDisplayIdSet2()));
		
		//Alternate Mode DisplayId Set1
		displayItemSetting.setAlternateDisplayIdSet1(parseAlternateDisplayId(f).get(0));
		System.out.println(Hex.getHexDump(displayItemSetting.getAlternateDisplayIdSet1()));
		
		//Alternate Mode DisplayId Set2
		displayItemSetting.setAlternateDisplayIdSet2(parseAlternateDisplayId(f).get(1));
		System.out.println(Hex.getHexDump(displayItemSetting.getAlternateDisplayIdSet2()));
		
		//Test Mode DisplayId
		displayItemSetting.setTestDisplayId(parseTestDisplayId(f));
		System.out.println(Hex.getHexDump(displayItemSetting.getTestDisplayId()));
		
		return displayItemSetting;
	}


	/**
	 * @param f
	 * @return
	 * @throws ParseException 
	 */
	private List<DisplayItemsSelect> parseNormalDisplayItemSelection(Finder f) throws ParseException {
		List<DisplayItemsSelect> dtsList = new ArrayList<DisplayItemsSelect>();
		
		DisplayItemsSelect dts = new DisplayItemsSelect();
		
		List<Map<String, String>> dtsData = f.findOf(KEY_DISPLAY_ITEM_SELECTION).getData();
		
		if(dtsData!=null) {
			for(Map<String,String> map : dtsData){
				if(map.get(KEY_MODE).equals(KEY_NORMAL_MODE)) {
					dts.setDisplayItem(map.get(KEY_DISPLAY_ITEM));
					dtsList.add(dts);
					dts = new DisplayItemsSelect();
				}
			}
		}
		return dtsList;
	}
	
	/**
	 * @param f
	 * @return
	 * @throws ParseException 
	 */
	private List<DisplayItemsSelect> parseAlternateDisplayItemSelection(Finder f) throws ParseException {
		List<DisplayItemsSelect> dtsList = new ArrayList<DisplayItemsSelect>();
		
		DisplayItemsSelect dts = new DisplayItemsSelect();
		
		List<Map<String, String>> dtsData = f.findOf(KEY_DISPLAY_ITEM_SELECTION).getData();
		
		if(dtsData!=null) {
			for(Map<String,String> map : dtsData){
				if(map.get(KEY_MODE).equals(KEY_ALTERNATE_MODE)) {
					dts.setDisplayItem(map.get(KEY_DISPLAY_ITEM));
					dtsList.add(dts);
					dts = new DisplayItemsSelect();
				}
			}
		}
		return dtsList;
	}
	
	/**
	 * @param f
	 * @return
	 * @throws ParseException 
	 */
	private List<DisplayItemsSelect> parseTestDisplayItemSelection(Finder f) throws ParseException {
		List<DisplayItemsSelect> dtsList = new ArrayList<DisplayItemsSelect>();
		
		DisplayItemsSelect dts = new DisplayItemsSelect();
		
		List<Map<String, String>> dtsData = f.findOf(KEY_DISPLAY_ITEM_SELECTION).getData();
		
		if(dtsData!=null) {
			for(Map<String,String> map : dtsData){
				if(map.get(KEY_MODE).equals(KEY_TEST_MODE)) {
					dts.setDisplayItem(map.get(KEY_DISPLAY_ITEM));
					dtsList.add(dts);
					dts = new DisplayItemsSelect();
				}
			}
		}
		return dtsList;
	}
	
	/**
	 * @param f
	 * @return
	 * @throws ParseException 
	 */
	private List<List<DisplayIdSet>> parseNormalDisplayId(Finder f) throws ParseException {
		List<DisplayIdSet> setList1 = new ArrayList<DisplayIdSet>();
		List<DisplayIdSet> setList2 = new ArrayList<DisplayIdSet>();
		List<List<DisplayIdSet>> returnData = new ArrayList<List<DisplayIdSet>>();
		
		DisplayIdSet set1 = new DisplayIdSet();
		DisplayIdSet set2 = new DisplayIdSet();
		
		List<Map<String, String>> dtsData = f.findOf(KEY_DISPLAY_ITEM_SELECTION).getData();
		
		if(dtsData!=null) {
			for(Map<String,String> map : dtsData){
				if(map.get(KEY_MODE).equals(KEY_NORMAL_MODE) 
						&& Integer.parseInt(map.get(KEY_SEQNO)) <= 20 ) {
					set1.setDisplayId(map.get(KEY_DISPLAY_ID));
					setList1.add(set1);
					set1 = new DisplayIdSet();
				} else if(map.get(KEY_MODE).equals(KEY_NORMAL_MODE)
						&& Integer.parseInt(map.get(KEY_SEQNO)) <= 40){
					set2.setDisplayId(map.get(KEY_DISPLAY_ID));
					setList2.add(set2);
					set2 = new DisplayIdSet();
				}
			}
		}
		
		returnData.add(setList1);
		returnData.add(setList2);
		
		return returnData;
	}
	
	/**
	 * @param f
	 * @return
	 * @throws ParseException 
	 */
	/*
	private List<DisplayIdSet> parseNormalDisplayIdSet2(Finder f) throws ParseException {
		List<DisplayIdSet> dtsList = new ArrayList<DisplayIdSet>();
		
		DisplayIdSet dts = new DisplayIdSet();
		
		List<Map<String, String>> dtsData = f.findOf(KEY_DISPLAY_ITEM_SELECTION).getData();
		
		if(dtsData!=null) {
			for(Map<String,String> map : dtsData){
				if(map.get(KEY_MODE).equals(KEY_NORMAL_MODE)) {
					dts.setDisplayID(map.get(KEY_DISPLAY_ID));
					dtsList.add(dts);
					dts = new DisplayIdSet();
				}
			}
		}
		dtsList.add(dts);
		return dtsList;
	}
	*/
	/**
	 * @param f
	 * @return
	 * @throws ParseException 
	 */
	private List<List<DisplayIdSet>> parseAlternateDisplayId(Finder f) throws ParseException {
		List<DisplayIdSet> setList1 = new ArrayList<DisplayIdSet>();
		List<DisplayIdSet> setList2 = new ArrayList<DisplayIdSet>();
		List<List<DisplayIdSet>> returnData = new ArrayList<List<DisplayIdSet>>();
		
		DisplayIdSet set1 = new DisplayIdSet();
		DisplayIdSet set2 = new DisplayIdSet();
		
		List<Map<String, String>> dtsData = f.findOf(KEY_DISPLAY_ITEM_SELECTION).getData();
		
		if(dtsData!=null) {
			for(Map<String,String> map : dtsData){
				if(map.get(KEY_MODE).equals(KEY_ALTERNATE_MODE) 
						&& Integer.parseInt(map.get(KEY_SEQNO)) <= 20) {
					set1.setDisplayId(map.get(KEY_DISPLAY_ID));
					setList1.add(set1);
					set1 = new DisplayIdSet();
				} else if (map.get(KEY_MODE).equals(KEY_ALTERNATE_MODE) 
						&& Integer.parseInt(map.get(KEY_SEQNO)) <= 40) {
					set2.setDisplayId(map.get(KEY_DISPLAY_ID));
					setList2.add(set2);
					set2 = new DisplayIdSet();
				}
			}
		}
		
		returnData.add(setList1);
		returnData.add(setList2);
		
		return returnData;
	}
	
	/**
	 * @param f
	 * @return
	 * @throws ParseException 
	 */
	/*
	private List<DisplayIdSet> parseAlternateDisplayIdSet2(Finder f) throws ParseException {
		List<DisplayIdSet> dtsList = new ArrayList<DisplayIdSet>();
		
		DisplayIdSet dts = new DisplayIdSet();
		
		List<Map<String, String>> dtsData = f.findOf(KEY_DISPLAY_ITEM_SELECTION).getData();
		
		if(dtsData!=null) {
			for(Map<String,String> map : dtsData){
				if(map.get(KEY_MODE).equals(KEY_ALTERNATE_MODE)) {
					dts.setDisplayID(map.get(KEY_DISPLAY_ID));
					dtsList.add(dts);
					dts = new DisplayIdSet();
				}
			}
		}
		dtsList.add(dts);
		return dtsList;
	}
	*/
	/**
	 * @param f
	 * @return
	 * @throws ParseException 
	 */
	private List<DisplayIdSet> parseTestDisplayId(Finder f) throws ParseException {
		List<DisplayIdSet> setList = new ArrayList<DisplayIdSet>();
		
		DisplayIdSet dts = new DisplayIdSet();
		
		List<Map<String, String>> dtsData = f.findOf(KEY_DISPLAY_ITEM_SELECTION).getData();
		
		if(dtsData!=null) {
			for(Map<String,String> map : dtsData){
				if(map.get(KEY_MODE).equals(KEY_TEST_MODE)) {
					dts.setDisplayId(map.get(KEY_DISPLAY_ID));
					setList.add(dts);
					dts = new DisplayIdSet();
				}
			}
		}
		return setList;
	}
	


}
