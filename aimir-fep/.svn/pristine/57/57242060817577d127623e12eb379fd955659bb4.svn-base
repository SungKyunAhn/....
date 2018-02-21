package com.aimir.fep.meter.parser.MX2Table;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import org.junit.Test;

import com.aimir.fep.meter.file.CSV;

public class TOUCalendarTest {
	
	@Test
	public void readTest() throws Exception{
		File csvFile = new File("c:/TOU_Setting.csv");
		
		FileInputStream fis = new FileInputStream(csvFile);
		
		byte[] buffer = new byte[1024];
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		while(fis.read(buffer)!=-1){
			bos.write(buffer);
		}
		
		//System.out.println(new String(bos.toByteArray()));
		
		TOUCalendarBuilder tb = new TOUCalendarBuilder();
		TOUCalendar tou = tb.build(new CSV(bos.toByteArray()));

		List<TOUDayPattern> dayPattern = tou.dayPattern;
		for (TOUDayPattern touDayPattern : dayPattern) {
			byte[] dayp = touDayPattern.toByteArray();
			for (byte b : dayp) {
				System.out.print(String.format("%02x ", b));
			}
			System.out.print("\r\n");
		}
		
		
	}

}
