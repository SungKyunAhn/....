package com.aimir.fep.iot.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.aimir.fep.iot.domain.common.RSCException;

@Component
public class CommonUtil {
private static final Log logger = LogFactory.getLog(CommonUtil.class);
	
	
	private static MessageSource messageSource;

	public static final int COUNT_PER_PAGE_COMMON = 20;
	public static final int COUNT_PER_PAGE_SEARCH = 20;
	public static final int COUNT_PER_PAGE_SEARCHAPP = 9;
	public static final int COUNT_PER_PAGE_POPUP = 10;
	public static final int PAGE_BLOCK_COUNT = 10;
	public static final String GLOBAL_PUBLIC_TOPIC_CODE = "T010000001";
	public static final String FTP_SERVER_ID = "F020000002";

	public static final String REDIRECT_TO_URL = "/IoTPortal/main/redirectToPage";
	public static final String REDIRECT_TO_FUNCTION_PAGE = "pp/common/redirectToFunction";
	public static final String REDIRECT_TO_PAGE = "pp/common/redirectToPage";
	public static final String LINE_SEPERATOR = "\n";
	
	//public static boolean RUN_D_API = true;
	public static boolean D_STATUS_ALL_TRUE = false;
	public static boolean RUN_VLRIFY_FLAG = false;
	public static boolean STOP_API_ACTION = false;

	public static final HashMap<String,String> cert_types
	= new HashMap<String,String>(){
	/**
		 *
		 */
		private static final long serialVersionUID = 6261806771750977413L;

	{
		put("device","K01001");
		put("user","K01002");
		put("server","K01003");
	}};
	
	public static final String ALL_ADMIN_GROUP = "G020000001";
	public static final String B2C_ADMIN_GROUP = "G020000002";
	public static final String B2B_ADMIN_GROUP = "G020000003";
	
	public static final String ALL_USER_GROUP 	= "G020000100";
	public static final String B2C_USER_GROUP 	= "G020000101";
	public static final String B2C_DEV_GROUP 	= "G020000102";
	public static final String B2B_USER_GROUP 	= "G020000103";
	
	public static MessageSource getMessageSource() {
		return messageSource;
	}
	public static void setMessageSource(MessageSource messageSource) {
		CommonUtil.messageSource = messageSource;
	}
	
	/**
	 * getRequest
	 * @return
	 */
	public static HttpServletRequest getRequest() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		return request;
	}

	/**
	 * setLocale
	 * @param sLocale
	 * @return
	 */
	public static boolean setLocale(String sLocale) {
	  HttpServletRequest request = getRequest();
	  Locale locale = null;
	  try {
		  logger.debug("###########################################");
		  if ("ko".equals(sLocale)) {
			  locale = Locale.KOREAN;
		  } else if ("en".equals(sLocale)) {
			  locale = Locale.ENGLISH;
//		  } else if ("zh".equals(sLocale)) {
//			  locale = Locale.CHINESE;
		  } else {
			  locale = request.getLocale();
		  }
		  HttpSession session = request.getSession(true);
		  //session.setAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, locale);


	  } catch (Exception ex){
		  return false;
	  }
	  return true;
	 }

	/**
	 * getLocale
	 * @return
	 */
	public static Locale getLocale(){
		HttpServletRequest request = getRequest();
		HttpSession session = request.getSession(false);
		Locale locale = null;
		if(session != null){
			//locale = (Locale)session.getAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
			if(locale != null){
				logger.debug("######################################");
				logger.debug("Locale From Session");
				logger.debug("######################################");
			}
		}
		
		return locale;
	}
	
	/**
	 * getLocale
	 * @param request
	 * @return
	 */
	public static Locale getLocale(HttpServletRequest request){
		HttpSession session = request.getSession(false);
		Locale locale = null;
		if(session != null){
			//locale = (Locale)session.getAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
			if(locale != null){
				logger.debug("######################################");
				logger.debug("Locale From Session");
				logger.debug("######################################");
			}
		}
		
		return locale;
	}

	/**
	 * getMessage
	 * @param sMsgCode
	 * @param request
	 * @return
	 */
	public static String getMessage (String sMsgCode, HttpServletRequest request) {
		Locale locale = getLocale(request);
		if(locale == null){
			locale = Locale.KOREAN;
		}

		String sMessage = messageSource.getMessage(sMsgCode, null, locale);
		return sMessage;
	}
	
	/**
	 * getMessage
	 * @param sMsgCode
	 * @param sLocale
	 * @return
	 */
	public static String getMessage (String sMsgCode, String sLocale) {
		Locale locale = Locale.KOREAN;
		if(! isEmpty(sLocale)){
			sLocale = sLocale.toLowerCase();
			if(! "ko".equals(sLocale)){
				if(sLocale.equals("en")){
					locale = Locale.ENGLISH;
//				} else if (sLocale.equals("zh")){
//					locale = Locale.CHINESE;
				}
			}
		}
		String sMessage = messageSource.getMessage(sMsgCode, null, locale);
		return sMessage;
	}
	
	/**
	 * getMessage
	 * @param sMsgCode
	 * @return
	 */
	public static String getMessage (String sMsgCode) {
		Locale locale = getLocale();

		String sMessage = messageSource.getMessage(sMsgCode, null, locale);
		return sMessage;
	}

	/**
	 * getMessage
	 * @param sMsgCode
	 * @param arr
	 * @return
	 */
	public static String getMessage (String sMsgCode, Object[] arr) {
		Locale locale = getLocale();

		String sMessage = messageSource.getMessage(sMsgCode, arr, locale);
		return sMessage;
	}

	/**
	 * isNull
	 * @param str
	 * @return
	 */
	public static boolean isNull(String str){
		if(str == null)
			return true;

		return false;
	}
	
	/**
	 * isNull
	 * @param obj
	 * @return
	 */
	public static boolean isNull(Object obj){
		if(obj == null)
			return true;

		return false;
	}
	
	/**
	 * isEmpty
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str){
		if(str == null || str.isEmpty())
			return true;
		
		return false;
	}

	/**
	 * isEmpty
	 * @param obj
	 * @return
	 */
	public static boolean isEmpty(Object obj){
		if(obj == null)
			return true;

		return false;
	}

	/**
	 * isEmpty
	 * @param obj
	 * @return
	 */
	public static boolean isEmpty(Object[] obj){
		if(obj == null || obj.length == 0)
			return true;

		return false;
	}

	/**
	 * nvl
	 * @param sTarget
	 * @param sReplace
	 * @return
	 */
	public static String nvl(String sTarget, String sReplace){
		if(sTarget == null || sTarget.isEmpty()){
			return sReplace;
		}
		return sTarget;
	}
	
	/**
	 * objToXml
	 * @param c
	 * @param o
	 */
	public static void objToXml(Class c, Object o) {
		try {
			JAXBContext context = JAXBContext.newInstance(c);

			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			m.marshal(o, System.out);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * getRedirectToPage
	 * @param sTargetUrl
	 * @param model
	 * @return
	 */
	public static String getRedirectToPage( String sTargetUrl,
											Model model) {

		Set<Entry<String,Object>> setParam = model.asMap().entrySet();
		Iterator<Entry<String,Object>> iterParam = setParam.iterator();
		Map<String, Object> mapParam = new HashMap<String, Object>();
		while(iterParam.hasNext()){
			Entry<String,Object> entryParam = iterParam.next();
			if(entryParam.getKey().indexOf("VO") >= 0){
				Map<String, Object> mapVO = ConverObjectToMap(entryParam.getValue());
				mapParam.putAll(mapVO);
			} else {
				mapParam.put(entryParam.getKey(), entryParam.getValue());
			}
		}
		model.addAttribute("redirectURL", sTargetUrl);
		model.addAttribute("parameterMap", mapParam);

		return CommonUtil.REDIRECT_TO_PAGE;
	}
	
	/**
	 * ConverObjectToMap
	 * @param obj
	 * @return
	 */
	public static Map<String, Object> ConverObjectToMap(Object obj){
		try {
			Field[] fields = obj.getClass().getDeclaredFields();
			Map<String, Object> resultMap = new HashMap<String, Object>();
			for(int i=0; i<=fields.length-1;i++){
				fields[i].setAccessible(true);
				resultMap.put(fields[i].getName(), fields[i].get(obj));
			}
			return resultMap;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getUrl(String url) {
		return PropertiesUtil.get("openapi", url);
	}
	
	/**
	 * getConfig
	 * @param sCfgCode
	 * @return
	 */
	public static String getConfig(String sCfgCode){
		return getMessage(sCfgCode, "root");
	}
	
	/**
	 * valueOf
	 * @param l
	 * @return
	 */
	public static long valueOf(String l){
		long ret = 0;
		try {
			ret = Long.valueOf(l);
		} catch (NumberFormatException e) {
		}
		
		return ret;
	}
	
	/**
	 * unMarshalFromXmlString
	 * @param cls
	 * @param in
	 * @return
	 */
	public static Object unMarshalFromXmlString(Class cls, String in){
		Object obj = null;
		StringReader sr = new StringReader(in);
		
		try {
			JAXBContext context = JAXBContext.newInstance(cls);
			Unmarshaller msh =  context.createUnmarshaller();
			obj = msh.unmarshal(sr);
		} catch (Exception e) {
			return null;
		}
		
		return obj;
	}
	
	/**
	 * getNow
	 * @return
	 */
	public static String getNow(){
		return getNow("yyyy.MM.dd HH:mm:ss.SSS");
	}
	
	/**
	 * getNow
	 * @param format
	 * @return
	 */
	public static String getNow(String format){
		String now = "";
		
		SimpleDateFormat simpleFomat = new SimpleDateFormat(format);
		Date currentTime = new Date();
		now = simpleFomat.format(currentTime);
		
		return now;
	}
	
	/**
	 * isSimulatorRemoteCSECSEID
	 * @param remoteCSECSEID
	 * @return
	 */
	public static boolean isSimulatorRemoteCSECSEID(String remoteCSECSEID) {
		 
		String scl_trim = remoteCSECSEID.substring(0,9);
		if(scl_trim.equals("9.9.999.9")) {
			return true;
		}else{
			return false;	
		}
	}
	
	/**
	 * isDouble
	 * @param str
	 * @return
	 */
	public static boolean isDouble(String str){
		try {
			Double.parseDouble(str);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
	
	/**
	 * getRandNum
	 * @param max
	 * @param min
	 * @return
	 */
	public static int getRandNum(int max, int min){
		return (int) Math.floor( Math.random() * (max - min + 1) + min );
	}
	

	/**
	 * parseDate
	 * @param dateValue
	 * @return
	 */
	public static Date parseDate(String dateValue){
		return parseDate(dateValue, "yyyy.MM.dd HH:mm:ss.SSS");
	}
	
	/**
	 * parseDate
	 * @param dateValue
	 * @param format
	 * @return
	 */
	public static Date parseDate(String dateValue, String format){
		boolean parseError = false;
		Date ret = null;
		
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
			ret = sdf.parse(dateValue);
		} catch (ParseException e) {
			parseError = true;
		}
		
		if(parseError){
			sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
			
			try {
				ret = sdf.parse(dateValue);
			} catch (ParseException e) {
				return null;
			}
		}
		
		return ret;
	}
	

	/**
	 * checkElapseTime
	 * @param dateString
	 * @param diff
	 * @return
	 */
	public static int checkElapseTime(String dateString, long diff){
		return checkElapseTime(dateString, diff, "yyyy.MM.dd HH:mm:ss.SSS");
	}
	
	
	/**
	 * checkElapseTime
	 * @param dateString
	 * @param diff
	 * @param format
	 * @return
	 */
	public static int checkElapseTime(String dateString, long diff, String format){
		int result = 0;
		
		Date now =  new Date();
		Date date = parseDate(dateString, format);
		
		long getDiff = now.getTime() - date.getTime();
		
		if(diff == getDiff) 		result = 0;
		else if(diff < getDiff)	result = 1;
		else if(diff > getDiff)	result = -1;
		
		return result;
	}
	

	/**
	 * marshalToXmlString
	 * @param obj
	 * @return
	 */
	public static String marshalToXmlString(Object obj){
		
		return marshalToXmlString(obj, false);
	}
	
	/**
	 * marshalToXmlString
	 * @param obj
	 * @param isJaxbFragment
	 * @return
	 */
	public static String marshalToXmlString(Object obj, boolean isJaxbFragment){
		StringWriter sw = new StringWriter();
		
		try {
			
			JAXBContext context = JAXBContext.newInstance(obj.getClass());
			Marshaller msh = context.createMarshaller();
			msh.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			msh.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
			msh.setProperty(Marshaller.JAXB_FRAGMENT, isJaxbFragment);
			msh.marshal(obj, sw);
		} catch (Exception e) {
			return null;
		}
		
		return sw.toString();
	}
	
	/**
	 * objectToJsonString
	 * @param obj
	 * @return
	 */
	public static String objectToJsonString(Object obj){
		
		String jsonStr = null;
		
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			jsonStr = mapper.writeValueAsString(obj);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return jsonStr;
	}
	
	/**
	 * isInvalidFormat
	 * @param in
	 * @param format
	 * @return
	 */
	public static boolean isInvalidFormat(String in, String format){
		SimpleDateFormat simpleDataFormat = new SimpleDateFormat(format);
		try {
			simpleDataFormat.parse(in);
		} catch (ParseException e) {
			return true;
		}
		return false;
	}
	
	/**
	 * isInteger
	 * @param str
	 * @return
	 */
	public static boolean isInteger(String str){
		try {
			Integer.parseInt(str);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
		
	}
	
	/**
	 * isZero
	 * @param str
	 * @return
	 */
	public static boolean isZero(String str){
		try {
			if(Integer.parseInt(str) == 0) return true;
		} catch (NumberFormatException e) {}
		
		return false;
	}
	
	/**
	 * isMinus
	 * @param str
	 * @return
	 */
	public static boolean isMinus(String str){
		try {
			if(Integer.parseInt(str) < 0) return true;
		} catch (NumberFormatException e) {}
		
		return false;
	}
	
	/**
	 * isMinus
	 * @param in
	 * @return
	 */
	public static boolean isMinus(int in){
		if(in < 0) return true;
		
		return false;
	}
	
    /**
     * checkFormatTell
     * @param tell1
     * @param tell2
     * @param tell3
     * @return
     */
    public static boolean checkFormatTell(String tell1, String tell2, String tell3) {
                 
         String[] check = {"02", "031", "032", "033", "041", "042", "043", "051", "052", "053", "054", "055", "061", 
                                 "062", "063", "070", "080", "0505"};
         String temp = tell1 + tell2 + tell3;
                 
         for(int i=0; i < temp.length(); i++){
                if (temp.charAt(i) < '0' || temp.charAt(i) > '9')       
                        return false;                   
         }
                 
         for(int i = 0; i < check.length; i++){
                 if(tell1.equals(check[i])) break;                       
                 if(i == check.length - 1) return false;
         }
                 
         if(tell2.charAt(0) == '0') return false; 
                
         if(tell1.equals("02")){
                 if(tell2.length() != 3 && tell2.length() !=4) return false;
                 if(tell3.length() != 4) return false;                         
         }else{
                 if(tell2.length() != 3) return false;
                 if(tell3.length() != 4) return false;
         }
         
         return true;
    }
        
    /**
     * checkFormatTell
     * @param tellNumber
     * @return
     */
    public static boolean checkFormatTell(String tellNumber) {
         
         String temp1;
         String temp2;
         String temp3;
         String tell = tellNumber;
         
         tell = tell.replace("-", "");  
         
         if(tell.length() < 9 || tell.length() > 11  || tell.charAt(0) != '0') return false;
                 
         if(tell.charAt(1) =='2'){
                 temp1 = tell.substring(0,2);
                 if(tell.length() == 9){
                         temp2 = tell.substring(2,5);
                         temp3 = tell.substring(5,9);
                 }else if(tell.length() == 10){
                         temp2 = tell.substring(2,6);
                         temp3 = tell.substring(6,10);
                 }else
                         return false;  
         } else if(tell.substring(0,4).equals("0505")){
                 if(tell.length() != 11) return false;
                 temp1 = tell.substring(0,4);
                 temp2 = tell.substring(4,7);
                 temp3 = tell.substring(7,11);
         } else {
                 if(tell.length() != 10) return false;
                 temp1 = tell.substring(0,3);
                 temp2 = tell.substring(3,6);
                 temp3 = tell.substring(6,10); 
         }
                                 
         return checkFormatTell(temp1, temp2, temp3);
    }
        
    /**
     * checkFormatCell
     * @param cell1
     * @param cell2
     * @param cell3
     * @return
     */
    public static boolean checkFormatCell(String cell1, String cell2, String cell3) {
         String[] check = {"010", "011", "016", "017", "018", "019"};
         String temp = cell1 + cell2 + cell3;
         
         for(int i=0; i < temp.length(); i++){
                if (temp.charAt(i) < '0' || temp.charAt(i) > '9')
                        return false;
         }
                         
         for(int i = 0; i < check.length; i++){
             if(cell1.equals(check[i])) break;
             if(i == check.length - 1) return false;
         }
                 
         if(cell2.charAt(0) == '0') return false;
                
         if(cell2.length() != 3 && cell2.length() !=4) return false;
         if(cell3.length() != 4) return false;
                                 
         return true;
    }
         
    /**
     * checkFormatCell
     * @param cellNumber
     * @return
     */
    public static boolean checkFormatCell(String cellNumber) {
                 
         String temp1;
         String temp2;
         String temp3;
        
         String cell = cellNumber;
         cell = cell.replace("-", "");          
         
         if(cell.length() < 10 || cell.length() > 11  || cell.charAt(0) != '0') return false;
         
         if(cell.length() == 10){
                 temp1 = cell.substring(0,3);
                 temp2 = cell.substring(3,6);
                 temp3 = cell.substring(6,10);
         }else{
                 temp1 = cell.substring(0,3);
                 temp2 = cell.substring(3,7);
                 temp3 = cell.substring(7,11);
         }
                 
         return checkFormatCell(temp1, temp2, temp3);
    }
         
    /**
     * checkFormatMail
     * @param mail1
     * @param mail2
     * @return
     */
    public static boolean checkFormatMail(String mail1, String mail2) {
                 
         int count = 0;
                 
         for(int i = 0; i < mail1.length(); i++){
                 if(mail1.charAt(i) <= 'z' && mail1.charAt(i) >= 'a') continue;         
                 else if(mail1.charAt(i) <= 'Z' && mail1.charAt(i) >= 'A') continue;    
                 else if(mail1.charAt(i) <= '9' && mail1.charAt(i) >= '0') continue;    
                 else if(mail1.charAt(i) == '-' && mail1.charAt(i) == '_') continue;    
                 else return false;
         }
         
         for(int i = 0; i < mail2.length(); i++){       
                 if(mail2.charAt(i) <= 'z' && mail2.charAt(i) >= 'a') continue;
                 else if(mail2.charAt(i) == '.'){ count++;  continue;}
                 else return false;
         }
         
         if(count == 1) return true;
         else  return false;     
                 
    }
        
    /**
     * checkFormatMail
     * @param mail
     * @return
     */
    public static boolean checkFormatMail(String mail) {
                 
         String[] temp = mail.split("@");
         
         if(temp.length == 2) return checkFormatMail(temp[0], temp[1]);
         else return false;
    }
    
    /**
     * checkUrl
     * @param url
     * @return
     */
    public static boolean checkUrl(String url) {
    	if (url.startsWith("http://")) {
    		return true;
    	} else if (url.startsWith("https://")) {
    		return true;
    	}
    	
    	return false;
    }
    
    /**
     * checkUrl
     * @param urls
     * @return
     */
    public static boolean checkUrl(List<String> urls) {
    	for (int i=0; i<urls.size(); i++) {
    		String url = urls.get(i);
	    	if (url.startsWith("http://")) {
	    		return true;
	    	} else if (url.startsWith("https://")) {
	    		return true;
	    	}
    	}
    	
    	return false;
    }
    
    /**
     * getURL
     * @param req
     * @return
     */
    public static String getURL(HttpServletRequest req) {

        String scheme = req.getScheme();
        String serverName = req.getServerName();
        int serverPort = req.getServerPort();
        String contextPath = req.getContextPath();
        //String servletPath = req.getServletPath();
        String pathInfo = req.getPathInfo();
        //String queryString = req.getQueryString();

        StringBuffer url =  new StringBuffer();
        url.append(scheme).append("://").append(serverName);

        if ((serverPort != 80) && (serverPort != 443)) {
            url.append(":").append(serverPort);
        }

//        url.append(contextPath).append(servletPath);
        url.append(contextPath);

        if (pathInfo != null) {
            url.append(pathInfo);
        }
//        if (queryString != null) {
//            url.append("?").append(queryString);
//        }
        return url.toString();
    }
    
    /**
     * getObjectLength
     * @param obj
     * @return
     */
    public static int getObjectLength(Object obj) {
    	int objLength = 0;
    	try {
    		ByteArrayOutputStream bos = new ByteArrayOutputStream();
    		ObjectOutput out = new ObjectOutputStream(bos);   
    		
			out.writeObject(obj);
			byte[] objBytes = bos.toByteArray();
	    	objLength = objBytes.length;
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	return objLength;
    }
    
    /**
     * seqIDToResourceID
     * @param seqPrefix
     * @param seqID
     * @return
     */
    public static String seqIDToResourceID(String seqPrefix, Long seqID) {
    	return seqPrefix + String.format("%020d", seqID);
    }
    
    public static String seqIDToResourceID(String seqPrefix, String seqID) {
    	return seqPrefix + seqID;
    }
    
    /**
     * getNowTimestamp
     * @return
     */
    public static String getNowTimestamp() {
    	String format = "yyyy-MM-dd'T'HH:mm:ssXXX";
    	String now = "";
		
		SimpleDateFormat simpleFomat = new SimpleDateFormat(format);
		Date currentTime = new Date();
		now = simpleFomat.format(currentTime);
		
		return now;
    }
    
    public static String getCurrentDate() {
    	//String format = "yyyy-MM-dd HH:mm:ss";
    	String format = "MMddHHmmyy";
    	String now = "";
		
		SimpleDateFormat simpleFomat = new SimpleDateFormat(format);
		Date currentTime = new Date();
		now = simpleFomat.format(currentTime);
		
		return now;
    }
	/**
	 * timestampToDate
	 * @param timestamp
	 * @return
	 * @throws RSCException
	 */
	public static Date timestampToDate(String timestamp) throws RSCException {
		
		if (CommonUtil.isEmpty(timestamp)) return null;
		
		if(timestamp.contains("T")) 
			timestamp = timestamp.replaceAll("T", "").replaceAll("-", "").replaceAll(":", "");
		
		//String format = "yyyy-MM-dd'T'HH:mm:ssXXX";
		String format = "yyyyMMddHHmmss";
		
		SimpleDateFormat simpleFomat = new SimpleDateFormat(format);
		
		Date date = null;
		
		try {
			date = simpleFomat.parse(timestamp);
		} catch (ParseException e) {
			throw new RSCException(CommonCode.RSC.BAD_REQUEST, e.getMessage());
		}
		
		return date;
	}
	
    /**
     * getExpirationTime
     * @return
     */
    public static String getExpirationTime() {
    	return "9999-12-31T00:00:00+00:00";
    }
    
    /**
     * getMoveTimeBySeconds
     * @param seconds
     * @return
     */
	public static String getMoveTimeBySeconds(int seconds) {
		SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, seconds);
		
		return sdformat.format(cal.getTime());
	}
	
	/**
	 * getMoveTimestampBySeconds
	 * @param seconds
	 * @return
	 */
	public static String getMoveTimestampBySeconds(int seconds) {
		SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, seconds);
		
		return sdformat.format(cal.getTime());
	}
	
	public static boolean getExpirationTimeValidation(String et) throws RSCException {
		if(isEmpty(et))
			return true;
		
		String format = "yyyyMMddHHmmss";
		String expirationTime = null;
		
		expirationTime = et.replaceAll("-", "").replaceAll(":", "").replaceAll("T", "");
		expirationTime = expirationTime.substring(0,  format.length());
		SimpleDateFormat simpleFomat = new SimpleDateFormat(format);
		
		try {
			Date nowDate = new Date();
			Date expirationDate = simpleFomat.parse(expirationTime);
			
			if(expirationDate.getTime() < nowDate.getTime()) {
				return false;
			}
			
			return true;
		}catch(Exception e) {
			throw new RSCException(CommonCode.RSC.BAD_REQUEST, e.getMessage());
		}
	}
}
