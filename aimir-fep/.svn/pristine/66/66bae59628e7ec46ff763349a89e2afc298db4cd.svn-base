package com.aimir.fep.meter.parser.MX2Table;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.aimir.fep.meter.file.CSV;
import com.aimir.fep.meter.file.CSV.Finder;

/**
 * SummerTime Value Object Builder Class
 * @author kskim
 */
public class SummerTimeBuilder implements VOBuilder<SummerTime>{
	// Keys
	private static final String KEY_SET1 = "Set1";
	private static final String KEY_SET2 = "Set1";
	private static final String KEY_SET3 = "Set1";
	
	
	// Columns Name
	private static final String[] COL_SUMMER_TIME_SETTING = new String[]{"No","StartDate","EndDate"};

	
	/**
	 * CSV 파일을 읽어 SummerTime 생성.
	 * @param csv
	 * @return
	 * @throws ParseException 
	 */
	public SummerTime build(CSV csv) throws ParseException{
		SummerTime summerTime = new SummerTime();
		
		Finder f = csv.getFinder();
		
		//Set1
		List<SummerTimeDateSet> dateSet1 = getSet(f,KEY_SET1);
		
		//Set2
		List<SummerTimeDateSet> dateSet2 = getSet(f,KEY_SET2);
		
		//Set3
		List<SummerTimeDateSet> dateSet3 = getSet(f,KEY_SET3);
		
		summerTime.setDateSet1(dateSet1);
		summerTime.setDateSet2(dateSet2);
		summerTime.setDateSet3(dateSet3);
		
		return summerTime;
	}
	
	public List<SummerTimeDateSet> getSet(Finder f,String key) throws ParseException {
		List<Map<String, String>> list = f.findOf(key).getData();

		SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHH");

		List<SummerTimeDateSet> dateSets = new ArrayList<SummerTimeDateSet>();
		
		if (list != null)
			for (Map<String, String> m : list) {

				SummerTimeDateSet dateSet = new SummerTimeDateSet();
				String startDate = m.get(COL_SUMMER_TIME_SETTING[1]);
				String endDate = m.get(COL_SUMMER_TIME_SETTING[2]);

				dateSet.setStartDate(sdf.parse(startDate));
				dateSet.setEndDate(sdf.parse(endDate));
				
				dateSets.add(dateSet);
			}
		return dateSets;
	}
}