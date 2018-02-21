/*
 * @(#)Util.java       1.0 04/07/23 *
 * Copyright (c) 2004-2005 NuriTelecom, Inc.
 * All rights reserved. *
 * This software is the confidential and proprietary information of
 * Nuritelecom, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Nuritelecom.
 *
 */
package com.aimir.fep.util;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.IPUtil;


/**
 * @author Park Yeon Kyoung
 *
 * Common Utility Class.
 */
public class Util {
    private static Log _log = LogFactory.getLog(Util.class);

	/**
	 * Code Transform Method.<p>
	 * unicode ->to Hangul. <p>
	 */
	public static String toHangul(String s) {

		if(s==null)
			return null;
		String result = new String();

		try {
			result = new String(s.getBytes("8859_1"),"KSC5601");
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}

		return result;

	}


	/**
	 * Code Transform Method.<p>
	 * Convert KSC5601 into unicode <p>
	 */
	public static String fromHangul(String str) {

		String change = null;
		try {
			 change = new String(str.getBytes("KSC5601"),"8859_1");
		} catch(UnsupportedEncodingException e) {
			 System.err.println(e.getMessage());
		}

		return change;

	}

	/**
	 * Check Null String,If String is null, Return "".<p>
	 */
	public static String nullCheck(String str) {

		if( str == null )
			return "";
		else if( str.length() == 0 )
			return "";
		else if( str.equals("null") )
			return "";
		else
			return str;

	}


	/**
	 * Check Null String,If String is null, Return true.<p>
	 * @param str
	 * @return
	 */
	public static boolean isNullCheck(String str) {

		if(str == null)
			return true;
		else if(str.length() == 0)
			return true;
		else if(str.equals("null"))
			return true;
		else
			return false;

	}


	/**
	 * String to change double type .<p>
	 * @param strData
	 * @return
	 */
	public static double getDouble( String strData ) {
		return Double.parseDouble( strData );
	}


	/**
	 * String to change double type.<p>
	 * @param strData
	 * @return
	 */
	public static double toDouble(String strData) {
		double ret = 0.000;
		try {
			ret = Double.parseDouble(strData);
		}catch (NumberFormatException e){
			ret = 0.000;
		}catch(Exception e) {
			ret = 0.000;
		}
		return ret;
	}


	/**
	 * String to change int type.<p>
	 * @param strData
	 * @return
	 */
	public static int toInt(String strData){
		int ret = 0;
		try{
			ret = Integer.parseInt(strData);
		}catch (NumberFormatException e){
			ret = 0;
		}catch(Exception e) {
			ret = 0;
		}
			return ret;
		}


	/**
	 * Get parsing String to length.
	 */
	public static String GetString( String des, int length ) {

		if(des.length() > length)
			return ( des.substring( 0, (length-1) ) + ".." ) ;
		else
			return des;

	}


	public static String getPost(String des, int length) {

		StringBuffer str = new StringBuffer(des);
		int i = 0;

		if(des.length() > length) {
			return ( des.substring( 0, (length-1) ) + ".." ) ;
		}
		else if(des.length() < length) {
			while(i < length-des.length())
				str.append(' ');
			return str.toString();
		}
		else {
			return des;
		}
	}


	/**
	 * Special character (delim).<p>
	 * same as StringTokenizer.<p>
	 */
	public static String GetStringToken( String source, String delim, int index ) {

		if((source != null) && !source.equals("")) {
			String tokenis = "";
			int startP = 0, endP = 0;

			for(int i=0; (i<index) && (endP>=0); i++ ) {
				endP = source.indexOf( delim, startP );
				if(endP > 0)
					tokenis = source.substring( startP, endP );
				else
					tokenis = source.substring( startP );

				startP = endP + 1;
			}
			return tokenis;
		} else {
			return "";
		}
	}


	public static String Filtering( String M ) {
		if(M == null)
			return "";
		else
			return ( M );
	}


	public static String NullFiltering( String M ) {
		if((M == null) || M.equals( "" ))
			return "&nbsp;";
		else
			return M;
	}

	public static int StringToInt( String str ) {
		try {
			return Integer.parseInt( str );
		} catch( Exception E ) {
			return 0;
		}
	}


	/**
	 * (src) oldstr->newstr .<p>
	 */
	public static String replace(String src, String oldstr, String newstr) {

		if (src == null)
				return null;

		StringBuffer dest = new StringBuffer("");
		int  len = oldstr.length();
		int  srclen = src.length();
		int  pos = 0;
		int  oldpos = 0;

		while ((pos = src.indexOf(oldstr, oldpos)) >= 0) {
			dest.append(src.substring(oldpos, pos));
			dest.append(newstr);
			oldpos = pos + len;
		}

		if (oldpos < srclen)
			dest.append(src.substring(oldpos, srclen));

		return dest.toString();

	}


	/**
	 *
	 * @param iMaxRow
	 * @param iPage
	 * @param iCnt
	 * @return
	 */
	public static boolean InsideRange(int iMaxRow, int iPage, int iCnt) {

		if(iPage*iMaxRow <= iCnt-1 && iPage*iMaxRow+iMaxRow-1 >= iCnt-1) {
			return(true);
		}
		return(false);
	}


	/**
	 *
	 * @param iMaxRow
	 * @param iPage
	 * @param iCnt
	 * @return
	 */
	public static boolean FinalPage(int iMaxRow, int iPage, int iCnt) {

		if(iPage * iMaxRow + iMaxRow >= iCnt) {
			return(true);
		}
		return(false);
	}


	/**
	 * Special character delete..  sooyee 11.14.<p>
	 */
	public static String SpecialCharDelete(String S, String T) {

		String strS = "";

		if((S != null) && !S.equals("")) {

			int len = S.length();

			for(int i = 0; len > i ; i++) {
				String temp = S.substring(i,i+1);
				if(!temp.equals(T)) {
					strS = strS+temp;
				}
			}
		}

		return strS;

	}


	public static String[] SplitBySymbol(String src, String symbol) {

		StringTokenizer ST = new StringTokenizer(src);

		String nums="";
		nums = ST.nextToken(symbol);
		int count=ST.countTokens();
		count++;
		String[] result = new String[count];
		StringTokenizer res = new StringTokenizer(src);
		int i=0;

		while (res.hasMoreTokens()) {
			result[i] = res.nextToken(symbol);
			if (i<count) i++;
		}

		return result ;

	}

	public static double Round(double x) {
		x = (int)(((x)) * 100 + 0.5)/100.0;
		return x;
	}


	public static double Round(double x , int y) {

		double temp = 10;

		if( y > 0 )
			temp = Math.pow( temp , y );

		x = (int)(x * temp + 0.5)/temp;

		return x;

	}

	public static String RoundText( double x , int y ) {

		String strFormat = "##0.00";
		for( int idx = 2 ; idx < y ; idx++ )
			strFormat = strFormat + "#";

		NumberFormat ourForm = new DecimalFormat( strFormat );
		double temp = 10;

		if( y > 0 )
			temp = Math.pow( temp , y );

		x = (int)(x * temp + 0.5)/temp;

		try {
			String aa = ourForm.format(x);
		} catch (IllegalArgumentException pe) {
			System.err.println(x + "not parseable!");
		}

		return ourForm.format(x);

	}

	public static String RoundText( String strX , int y ) {

		String strFormat = "##0.00";
		for( int idx = 2 ; idx < y ; idx++ )
			strFormat = strFormat + "#";

		NumberFormat ourForm = new DecimalFormat( strFormat );
		double x;
		double temp = 10;

		x = getDouble( strX );

		if( y > 0 )
			temp = Math.pow( temp , y );

		x = (int)(x * temp + 0.5)/temp;

		try {
			String aa = ourForm.format(x);
		} catch (IllegalArgumentException pe) {
			System.err.println(x + "not parseable!");
		}

		return ourForm.format(x);

	}

	public static void printByte(byte[] b)
	{
		try {
			for(int i = 0; ; i++)
				_log.debug("["+Integer.toHexString(b[i])+"] ");
		} catch(ArrayIndexOutOfBoundsException e) {
		}
	}


	/**
	 * @param append source of String
	 * @param str	to append
	 * @param length
	 * @return
	 */
	public static String frontAppendNStr(char append, String str, int length)
	{
		StringBuffer b = new StringBuffer("");

		try {
			if(str.length() < length)
			{
			   for(int i = 0; i < length-str.length() ; i++)
				   b.append(append);
			   b.append(str);
			}
			else
			{
				b.append(str);
			}
		} catch(Exception e) {
			_log.error("Util.frontAppendNStr : " +e.getMessage());
		}
		return b.toString();
	}


	/**
	 * print StringBuffer to HexString.<p>
	 * @param b StringBuffer
	 */
	public static void printHex(StringBuffer b)
	{
		try {
			for(int i = 0; i < b.length(); i++) {
				_log.debug(frontAppendNStr('0',Integer.toHexString(b.charAt(i)),2)+" ");
				// if(((i+1)%24) == 0); // System.out.print("\n");
			}
			_log.debug("\n");

		} catch(Exception e) {
			_log.error("Util.printHex : " +e.getMessage());
		}

	}


	/**
	 * Transform to non empty String.<p>
	 * ex) 0x80 0x49  -->"8049" <p>
	 * @param b
	 * @return
	 */
	public static String getNHexString(String b)
	{
		StringBuffer buf = new StringBuffer("");

		try {
			for(int i = 0; i < b.length(); i++) {

				buf.append(frontAppendNStr('0',Integer.toHexString(b.charAt(i)),2));
			}

		} catch(Exception e) {
			_log.error("Util.getNHexString() : " +e.getMessage());
		}
		return buf.toString();
	}


	/**
	 * Transform to with empty String.<p>
	 * For Logging <p>
	 * ex)  0x09 0x81 --> "09 81" <p>
	 * @param b StringBuffer b
	 * @return
	 */
	public static String getHexString(StringBuffer b)
	{
		StringBuffer buf = new StringBuffer("");

		try {
			for(int i = 0; i < b.length(); i++) {

				buf.append(frontAppendNStr('0',Integer.toHexString(b.charAt(i)),2)+" ");
				if(((i+1)%24) == 0) buf.append('\n');
			}

		} catch(Exception e) {
			_log.error("Util.getHexString : " +e.getMessage());
		}
		return buf.toString();
	}

    public static String getHexDump(byte[] b){
        StringBuffer buf = new StringBuffer();
        int idx = 0;
        try{
            for(int i = 0; i < b.length; i++) {
                int val = b[i];
                val = val & 0xff;
                buf.append(frontAppendNStr('0',Integer.toHexString(val),2)+" ");
                if(((i+1)%16) == 0)
                {
                    buf.append("\t  "+new String(b,idx,16) +"");
                    idx += 16;
                    buf.append('\n');
                }
            }
        }catch(Exception e){

        }
        return buf.toString();
    }

	/**
	 * Transform to with empty String. <p>
	 * For Logging <p>
	 * ex)  0x09 0x81 --> "09 81"
	 * @param b byte array b
	 * @return
	 */
	public static String getHexString(byte[] b){

		StringBuffer buf = new StringBuffer();

		try{
			for(int i = 0; i < b.length; i++) {
				int val = b[i];
				val = val & 0xff;
				buf.append(frontAppendNStr('0',Integer.toHexString(val),2)+" ");
				if(((i+1)%24) == 0) buf.append('\n');
			}
		}catch(Exception e){
			_log.error(e,e);
		}
		return buf.toString();
	}


	public static String getHexString(byte[] b, int colume){

		StringBuffer buf = new StringBuffer();

		try{
			for(int i = 0; i < b.length; i++) {
				int val = b[i];
				val = val & 0xff;
				buf.append(frontAppendNStr('0',Integer.toHexString(val),2)+" ");
				if(((i+1)%colume) == 0) buf.append('\n');
			}
		}catch(Exception e){

		}
		return buf.toString();
	}

	/**
	 * Transform to with non empty String. <p>
	 * For Logging <p>
	 * ex)  0x09 0x81 --> "0981"
	 * @param b byte array b
	 * @return
	 */
	public static String getNHexString(byte[] b){

		StringBuffer buf = new StringBuffer();

		try{
			for(int i = 0; i < b.length; i++) {
				int val = b[i];
				val = val & 0xff;
				buf.append(frontAppendNStr('0',Integer.toHexString(val),2));
			}
		}catch(Exception e){

		}
		return buf.toString();
	}

	/**
	 * Transform to with empty String. <p>
	 * For Logging <p>
	 * ex)  0x09 0x81 --> "09 81"
	 * @param b StringBuffer b
	 * @return
	 */
	public static String getHexString(String b)
	{
		StringBuffer buf = new StringBuffer();

		try {
			for(int i = 0; i < b.length(); i++) {
		        int val = b.charAt(i);
		        val = val & 0xff;
				buf.append(frontAppendNStr('0',Integer.toHexString(val),2)+" ");
				if(((i+1)%24) == 0) buf.append('\n');
			}

		} catch(Exception e) {
			_log.error("Util.getHexString : " +e.getMessage());
		}
		return buf.toString();
	}


	/**
	 * character -> hex string. <p>
	 * @param c
	 * @return
	 */
	public static String getHexString(char c) {

		StringBuffer buf = new StringBuffer();

		try {
			buf.append(frontAppendNStr('0',Integer.toHexString(c),2));
		} catch(ArrayIndexOutOfBoundsException e) {
			_log.error("Util.getHexString() : "+e.getMessage());
		}
		return buf.toString();
	}


	/**
	 * character print to hex string. <p>
	 * @param b
	 */
	public static void printChar(char[] b) {

		try {
			for(int i = 0; ; i++)
				System.out.print("["+Integer.toHexString(b[i])+"] ");
		} catch(ArrayIndexOutOfBoundsException e) {
		}
	}


	/**
	 * @param b byte
	 * @return
	 */
	public static String byteToString(byte[] b) {

		String convt = new String();
		String temp = new String(b);

		for(int i = 0; i < temp.length(); i++) {
			convt += (Byte.toString(b[i]));
		}
		return convt;
	}


	/**
	 * @param value
	 * @return
	 */
	public static int intToUnsingedInt(int value) {
		if(value < 0)
			return value*(-1);
		else
			return value;
	}


	/**
	 * @return
	 */
	public static String currYymmdd1(){

		String dateString = "";

		try {
			dateString = DateTimeUtil.getDateString(new Date()).substring(0, 8);

		}catch(Exception e){
			_log.error("Util.currYymmdd() : "+e.getMessage());
		}
		return dateString;
	}

	/**
	 * Current minutes.<p>
	 * @return
	 */
	public static int currMin(){

		String dateString = "";
		int min = 0;

		try {
			dateString = DateTimeUtil.getDateString(new Date());
			min = Integer.parseInt(dateString.substring(10,12));
		}catch(Exception e){
			_log.error("Util.currYymmdd() : "+e.getMessage());
		}
		return min;
	}


	/**
	 * Current hour.<p>
	 * @return
	 */
	public static int currHour(){

		String dateString = "";
		int hour = 0;

		try {
			dateString = DateTimeUtil.getDateString(new Date());
			hour = Integer.parseInt(dateString.substring(8,10));
		}catch(Exception e){
			_log.error("Util.currYymmdd() : "+e.getMessage());
		}
		return hour;
	}

	/**
	 * @return
	 */
	public static String currYymmddHHMM(){

		String dateString = "";

		try {
			dateString = DateTimeUtil.getDateString(new Date());

		}catch(Exception e){
			_log.error("Util.currYymmdd() : "+e.getMessage());
		}
		return dateString;
	}


	/**
	 * @return
	 */
	public static String currYymmddHHMMss(){

		String dateString = "";

		try {
			dateString = DateTimeUtil.getDateString(new Date());

		}catch(Exception e){
			_log.error("Util.currYymmdd() : "+e.getMessage());
		}
		return dateString;
	}

	public static String millisecond2Date(long atime) {
		String dateString = "";

		try {
			dateString = DateTimeUtil.getDateString(atime);

		}catch(Exception e){
			_log.error("Util.millisecond2Date() : "+e.getMessage());
		}
		return dateString;
	}

	public static String getDateDay(long atime) {
		String dateString = "";
		String day = "";

		try {
			dateString = DateTimeUtil.getDateString(atime);

			day = dateString.substring(6, 8);

		}catch(Exception e){
			_log.error("Util.getDateDay() : "+e.getMessage());
		}

		return day;
	}

	public static String getDateMonth(long atime, int flag)
	{
		String dateString = "";
		String date = "";
		int year = 0;
		int month = 0;

		try {
			dateString = DateTimeUtil.getDateString(atime);

			year  = Integer.parseInt(dateString.substring(0, 4));
			month = Integer.parseInt(dateString.substring(4, 6));

			if(flag == 0) {			/* prev month */
				if(month == 1)
				{
					date = new String(
						new Integer(year-1).toString()) +
						new String(new Integer(12).toString());
				} else {
					date = new String(
						new Integer(year).toString()) +
						new String(new Integer(month-1).toString());
				}
			} else {
				date = dateString;
			}
		}catch(Exception e){
			_log.error("Util.getDateMonth() : "+e.getMessage());
		}

		return date;
	}

	public static String getDateMonthByFill(long atime, int flag)
	{
		String dateString = "";
		String date = "";
		int year = 0;
		int month = 0;
		String tmp="";

		try {
			dateString = DateTimeUtil.getDateString(atime);

			year  = Integer.parseInt(dateString.substring(0, 4));
			month = Integer.parseInt(dateString.substring(4, 6));

			if(flag == 0) {			/* prev month */
				if(month == 1)
				{
					date = new String(
						new Integer(year-1).toString()) +
						new String(new Integer(12).toString());
				} else {
					if( month <= 10 )
					{
						tmp = 	new String("0") +
								new String(new Integer(month-1).toString());
					} else {
						tmp = 	new String(new Integer(month-1).toString());
					}
					date = new String(new Integer(year).toString())
						+ tmp;
				}
			} else {
				date = dateString;
			}
		}catch(Exception e){
			_log.error("Util.getDateMonth() : "+e.getMessage());
		}

		return date;
	}

	/**
	 * ex) 2004-08-08 00:04 -> 2004-08-08 00:15<p>
	 * ex) 2004-08-08 00:00 -> 2004-08-08 00:00<p>
	 * ex) 2004-08-08 00:15 -> 2004-08-08 00:15<p>
	 * ex) 2004-08-08 00:16 -> 2004-08-08 00:30<p>
	 * @param yymmddHHMM
	 * @return
	 */
	public static String getQuaterYymmddhhmm(String yymmddHHMM, int interval) throws Exception {

		String dateString = "";

		try{
			if(yymmddHHMM == null || yymmddHHMM.equals(""))
				throw new Exception("INPUT NULL");
			if(yymmddHHMM.length() < 12)
				throw new Exception("Length Error");

			Calendar c = Calendar.getInstance();

			int yy  = Integer.parseInt(yymmddHHMM.substring(0,4));
			int mm  = Integer.parseInt(yymmddHHMM.substring(4,6));
			int day = Integer.parseInt(yymmddHHMM.substring(6,8));
			int HH  = Integer.parseInt(yymmddHHMM.substring(8,10));
			int MM  = Integer.parseInt(yymmddHHMM.substring(10,12));

			if(((MM !=0) && (MM%interval!=0)))
				MM = (MM+interval) - (MM+interval)%interval;

			c.set(yy, mm-1, day, HH, MM,0);
			dateString =  DateTimeUtil.getDateString(c.getTime());

		}catch(NumberFormatException e){
			throw new Exception("Util.getQuaterYymmdd() : "+e.getMessage());
		}
		return dateString;

	}


	/**
	 * Get Minute
	 *
	 * @return  to time - from time
	 */
	public static long getDuration(String from, String to) throws Exception {

		long duration = 0;
		long ftime = Util.getMilliTimes(from);
		long ttime = Util.getMilliTimes(to);

		duration  = (ttime-ftime)/1000/60;

		return duration;
	}


	public static long getMilliTimes(String yymmddhhmmss) throws Exception {

		try{
			Calendar c = Calendar.getInstance();

			if(yymmddhhmmss.length() < 10)
				throw new Exception("Wrong Format!");

			int yy  = Integer.parseInt(yymmddhhmmss.substring(0,4));
			int mm  = Integer.parseInt(yymmddhhmmss.substring(4,6));
			int day = Integer.parseInt(yymmddhhmmss.substring(6,8));
			int HH  = Integer.parseInt(yymmddhhmmss.substring(8,10));

			int MM  = 0;
			if(yymmddhhmmss.length() >= 12)
			    MM  = Integer.parseInt(yymmddhhmmss.substring(10,12));
			int ss  = 0;
			if(yymmddhhmmss.length() == 14)
				ss  = Integer.parseInt(yymmddhhmmss.substring(12,14));

			if(mm < 1 || mm > 12)
				throw new Exception("Month Wrong Format!");
			if(day < 1 || day > 31)
				throw new Exception("Day Wrong Format !");
			if(HH < 0 || HH > 24)
				throw new Exception("Hour Wrong Format !");
			if(MM < 0 || MM > 59)
				throw new Exception("Minutes Wrong Format !");
			if(ss < 0 || ss > 59)
				throw new Exception("Second Wrong Format !");

			/* why mm-1 because month start from 0 */
			c.set(yy, mm-1, day, HH, MM,ss);
			return c.getTimeInMillis();

		}catch(Exception e){
			throw new Exception("Util.getMilliTimes() : "+yymmddhhmmss+"->"+e.getMessage());
		}

	}
	/**
	 * ex) 2004-08-08 00:00 -> 2004-08-07 24:00<p>
	 * @param yymmddHHMM
	 * @param min (minutes)
	 * @return
	 *
	 */
	public static String addMinYymmdd(String yymmddHHMM,int min)
							throws Exception {

		String dateString = new String();
		StringBuffer b = new StringBuffer();

		try{
			Calendar c = Calendar.getInstance();

			if(yymmddHHMM.length() < 12)
				throw new Exception("Wrong Format!");

			int yy  = Integer.parseInt(yymmddHHMM.substring(0,4));
			int mm  = Integer.parseInt(yymmddHHMM.substring(4,6));
			int day = Integer.parseInt(yymmddHHMM.substring(6,8));
			int HH  = Integer.parseInt(yymmddHHMM.substring(8,10));
			int MM  = Integer.parseInt(yymmddHHMM.substring(10,12));

			if(mm < 1 || mm > 12)
				throw new Exception("Month Wrong Format!");
			if(day < 1 || day > 31)
				throw new Exception("Day Wrong Format !");
			if(HH < 0 || HH > 24)
				throw new Exception("Hour Wrong Format !");
			if(MM < 0 || MM > 59)
				throw new Exception("Minutes Wrong Format !");

			/* why mm-1 because month start from 0 */
			c.set(yy, mm-1, day, HH, MM+min,0);
			dateString =  DateTimeUtil.getDateString(c.getTime());

            /*
			if(dateString.substring(8,12).equals("0000")){

				yy  = Integer.parseInt(dateString.substring(0,4));
				mm  = Integer.parseInt(dateString.substring(4,6));
				day = Integer.parseInt(dateString.substring(6,8));
				HH  = Integer.parseInt(dateString.substring(8,10));
				MM  = Integer.parseInt(dateString.substring(10,12));

				c.set(yy, mm-1, day-1, 0, 0,0);
				dateString =  formatter.format(c.getTime());
				b.append(dateString.substring(0,8));
				b.append("2400");
				dateString = b.toString();
			}*/

		}catch(Exception e){
			throw new Exception("Util.addMinYymmdd() : "+e.getMessage());
		}
		return dateString;
	}

	/**
	 * ex) 2009-04-11 <p>
	 * @param yyyymmdd
	 * @param date
	 * @return
	 *
	 */
	public static String addDateYyyymmdd(String yyyymmdd, int date)
							throws Exception {

		String dateString = new String();
		StringBuffer b = new StringBuffer();

		try{
			Calendar c = Calendar.getInstance();

			if(yyyymmdd.length() < 8)
				throw new Exception("Wrong Format!");

			int yy  = Integer.parseInt(yyyymmdd.substring(0,4));
			int mm  = Integer.parseInt(yyyymmdd.substring(4,6));
			int day = Integer.parseInt(yyyymmdd.substring(6,8));

			if(mm < 1 || mm > 12)
				throw new Exception("Month Wrong Format!");
			if(day < 1 || day > 31)
				throw new Exception("Day Wrong Format !");

			/* why mm-1 because month start from 0 */
			c.set(yy, mm-1, day+date, 0, 0,0);
			dateString =  DateTimeUtil.getDateString(c.getTime()).substring(0,8);

		}catch(Exception e){
			throw new Exception("Util.addDateYyyymmdd() : "+e.getMessage());
		}
		return dateString;
	}


	public static String decimalformat(double d, int n){

		NumberFormat nf = NumberFormat.getInstance();
		nf.setMinimumFractionDigits(n);
		nf.setMaximumFractionDigits(n);
		return nf.format(d);
	}

	public static String spiltStr1(String str) {

		String[] s = new String[4];
		try {
			s = SplitBySymbol(str,".");
			return s[0];
		} catch(Exception e) {
			return "";
		}
	}

	public static String spiltStr2(String str) {

		String[] s = new String[4];
		try {
			s = SplitBySymbol(str,".");
			return s[1];
		} catch(Exception e) {
			return "";
		}
	}

	public static String spiltStr3(String str) {

		String[] s = new String[4];
		try {
			s = SplitBySymbol(str,".");
			return s[2];
		} catch(Exception e) {
			return "";
		}
	}

	public static String spiltStr4(String str) {

		String[] s = new String[4];
		try {
			s = SplitBySymbol(str,".");
			return s[3];
		} catch(Exception e) {
			return "";
		}
	}



	/**
	 * Compare data with pattern. <p>
	 * @param sb - data
	 * @param pattern
	 * @return
	 */
	public static boolean match(StringBuffer sb, String pattern) {

		String substr = "";

		try {
			if((sb == null) || (sb.length() < pattern.length()) ) {
				return false;
			}

			for(int i = 0; i < sb.length()-pattern.length()+1; i++) {
				substr = sb.substring(i, i+pattern.length());
				if(substr.equals(pattern)) {
					return true;
				}
			}
		} catch (Exception e) {
			//logger.warn(e);
		}

		return false;

	}


	/**
	 * Compare data with pattern.<p>
	 * @param data
	 * @param pattern
	 * @return
	 */

	public static boolean match(String data, String pattern) {

		String substr = "";

		try {
			if((data == null) || (data.length() < pattern.length()) ) {
				return false;
			}

			for(int i = 0; i < data.length()-pattern.length()+1; i++) {
				substr = data.substring(i, i+pattern.length());
				if(substr.equals(pattern)) {
					return true;
				}
			}
		} catch (Exception e) {
			//logger.warn(e);
		}

		return false;

	}

	public static Calendar getCalendar(String datetime) {

		Calendar cal = Calendar.getInstance();

		int year = 0, month = 1, day = 0, hour = 0, min = 0, sec = 0, msec = 0;
		if (datetime.length() >= 4)
			year = Integer.parseInt(datetime.substring(0, 4));
		if (datetime.length() >= 6)
			month = Integer.parseInt(datetime.substring(4, 6));
		if (datetime.length() >= 8)
			day = Integer.parseInt(datetime.substring(6, 8));
		if (datetime.length() >= 10)
			hour = Integer.parseInt(datetime.substring(8, 10));
		if (datetime.length() >= 12)
			min = Integer.parseInt(datetime.substring(10, 12));
		if (datetime.length() >= 14)
			sec = Integer.parseInt(datetime.substring(12, 14));
		if (datetime.length() >= 17)
			msec = Integer.parseInt(datetime.substring(14, 17));

		cal.set(year, month - 1, day, hour, min, sec);
		cal.set(Calendar.MILLISECOND, msec);

		return cal;
	}


	public static String getYymmddhhmm(int yy, int mm, int dd, int hh, int min)
							throws Exception {

		String date = new String();

		if(yy < 0)
			throw new Exception("YEAR FORMAT ERROR");
		if(dd < 0)
			throw new Exception("DAY FORMAT ERROR");
		if(hh < 0 || hh > 24)
			throw new Exception("HOUR FORMAT ERROR");
		if(min < 0 || min > 59)
			throw new Exception("MIN FORMAT ERROR");

		Calendar cal = Calendar.getInstance();

		cal.set(yy,mm-1,dd,hh,min);
		date = Util.getCalToString(cal);
		return date;
	}

	public static String getCalToString(Calendar cal) {

		String result = "";
		String tmp = "";

		tmp += cal.get(Calendar.YEAR);
        if(tmp.length() < 4) {
            while(tmp.length() < 4)
            {
                tmp = "0" + tmp;
            }
        }
        result += tmp;
        tmp = "";

		tmp += cal.get(Calendar.MONTH)+1;
		if(tmp.length() == 1) {
			result += "0";
		}
        result += tmp;
		tmp = "";

		tmp += cal.get(Calendar.DAY_OF_MONTH);
		if(tmp.length() == 1) {
			result += "0";
		}
        result += tmp;
		tmp = "";

		tmp += cal.get(Calendar.HOUR_OF_DAY);
		if(tmp.length() == 1) {
			result += "0";
		}
        result += tmp;
		tmp = "";

		tmp += cal.get(Calendar.MINUTE);
		if(tmp.length() == 1) {
			result += "0";
		}
        result += tmp;

		return result;

	}
	/**
	 * Get date from formatted "yyyyMMddHHmmss"
	 * Jeong Hun
	 *
	 * @return - java.util.Date
	 */
	public static Date getDate(String yyyyMMddHHmmss) {
		Calendar cal = getCalendar(yyyyMMddHHmmss);

		return cal.getTime();
	}
    public static boolean check65536(double a)
    {
        if(a==65536.0)
        {
            return false;
        }
        return true;
    }
    
	/**
	 * Get Second
	 *
	 * @return  to time - from time
	 */
	public static long getDurationSec(String from, String to) throws Exception {

		long duration = 0;
		long ftime = Util.getMilliTimes(from);
		long ttime = Util.getMilliTimes(to);

		duration  = (ttime-ftime)/1000;

		return duration;
	}

	/**
     * make ipv5 for mcu and modem with mcu mac address
     * @param type MCU or Modem
     * @param macAddr mac address of MCU
     * @param modemId 
     */
    public static String getIPv6(DeviceType type, String macAddr, String modemId) 
    throws Exception {
        if (macAddr == null || macAddr.length() != 17)
            throw new Exception ("MAC_ADDR[" + macAddr + "] is wrong");
        if (modemId != null && modemId.length() != 16)
            throw new Exception ("MODEM_ID[" + modemId + "] is wrong");
        
        macAddr = macAddr.replaceAll("-", "");
        String ipv6 = "FD00";
        ipv6 = ipv6 + ":" + macAddr.substring(0, 4) 
        + ":" + macAddr.substring(4, 8) + ":" + macAddr.substring(8, 12);
        
        if (type == DeviceType.MCU) {
            ipv6 = ipv6 + ":0:0:0:1";
        }
        else if (type == DeviceType.Modem && modemId != null && modemId.length() == 16) {
            ipv6 = ipv6 + ":" + modemId.substring(0, 4) + ":" + modemId.substring(4, 8)
            + ":" + modemId.substring(8, 12) + ":" + modemId.substring(12, 16);
        }
        
        return IPUtil.format(ipv6);
    }
    
    /**
     * make ipv5 for mcu and modem with mcu mac address
     * @param type MCU or Modem
     * @param macAddr mac address of MCU
     * @param modemId 
     */
    public static String getIPv6(String dcuIpV6, String modemId) 
    throws Exception {
        if (dcuIpV6 != null && !"".equals(dcuIpV6)) {
            int fromIdx = 0;
            for (int i = 0; i < 4; i++) {
                fromIdx = dcuIpV6.indexOf(":", fromIdx);
                fromIdx++;
            }
            
            String ipv6 = dcuIpV6.substring(0, fromIdx);
                
            ipv6 = ipv6 + modemId.substring(0, 4) + ":" + modemId.substring(4, 8)
                + ":" + modemId.substring(8, 12) + ":" + modemId.substring(12, 16);
            
            return IPUtil.format(ipv6);
        }
        return "";
    }
}
