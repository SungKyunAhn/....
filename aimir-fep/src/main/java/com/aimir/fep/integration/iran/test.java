package com.aimir.fep.integration.iran;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class test {
	public static void main(String[] args) {
		
		InputStream inputStream;
		try {
			inputStream = new FileInputStream("D:\\test-files\\b.txt");
			System.out.println(inputStream);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
