package com.aimir.fep.meter.file;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * csv파일을 key, value 로 이루어진 Map새성.
 * @author kskim
 *
 */
public class CSV implements java.io.Serializable, ConfigFile {

	private static final long serialVersionUID = 4644180550737504561L;

	/**
	 * @author kskim
	 */
	public class Finder implements java.io.Serializable {
		private static final long serialVersionUID = 2580572133564575296L;
		Element e;
		int index = 0;
		public Map<String,List<Map<String,String>>> linerData = null;
		Finder(Element e){
			this.e = e;
		}
		public Finder findOf(String key){
			int index = e.liner.indexOf(key);
			if(index >= 0)
				this.index = index;
			return this;
		}
		public Finder nextOf(String key){
			for(int i = this.index;i<e.liner.size();i++){
				if(e.liner.get(i).equals(key)){
					this.index = i;
					break;
				}
			}
			return this;	
		}
		public Finder next(){
			this.index++;
			return this;
		}
		public List<Map<String,String>> getData(){
			String key = ""+this.index;
			if(e.linerData.containsKey(key))
				return e.linerData.get(key);
			else
				return null;
		}
		public String getValue(String key) throws Exception{
			if(e.keyValue.containsKey(key))
				return e.keyValue.get(key);
			else
				throw new Exception("can not found key - " + key);
		}
		public boolean containsKey(String key){
			return e.keyValue.containsKey(key);
		}
		public boolean containsLiner(String key){
			if(e.liner.indexOf(key)>=0)
				return true;
			else
				return false;
		}
	}
	/**
	 * @author kskim
	 */
	public class Element implements java.io.Serializable {
		private static final long serialVersionUID = 2186702925870586275L;
		private Map<String,String> keyValue = new HashMap<String,String>();
		public List<String> liner = new ArrayList<String>();
		public Map<String,List<Map<String,String>>> linerData = new HashMap<String,List<Map<String,String>>>();
		
		public void put(String key, String value){
			this.keyValue.put(keygen(key), value);
		}
		
		public String keygen(String key){
			return key.replaceAll("[^a-zA-Z0-9 ]", "").trim().replaceAll(" ", "_");
		}
		
		public void addLiner(String key){
			this.liner.add(keygen(key));
		}
		
		public Map<String,String> makeInnerMap(){
			Map<String,String> e = null;
			List<Map<String,String>> l = null;
			
			String key = String.format("%d",liner.size()-1);
			if(linerData.containsKey(key)){
				l = linerData.get(key);
			}else{
				l = new ArrayList<Map<String,String>>();
				linerData.put(key, l);
			}
			e = new HashMap<String,String>();
			l.add(e);
			return e;
		}
	}
	
	private Element element = new Element();	
	private Finder finder = new Finder(element);
	private String csvString=null;
	
	/**
	 * @param fileBinary
	 * @throws IOException
	 */
	public CSV(byte[] fileBinary) throws IOException {
		
		this.csvString = new String(fileBinary);
		
		BufferedReader reader = new BufferedReader(new StringReader(csvString));
		
		String str;
				
		boolean isHead = true;
		String[] head = null;
		int headCnt = 0;
		
		while((str = reader.readLine()) != null){
			if(str.length()>0){
				String[] split = str.split(",");
				if(split.length==2){
					element.put(split[0], split[1]);
				}else if(split.length>2){
					if(headCnt != split.length)
						isHead = true;
					
					if(isHead){
						head = split;
						isHead=false;
						headCnt = split.length;
					}else{
						Map<String,String> m = element.makeInnerMap();
						for(int i=0;i<head.length;i++){
							m.put(element.keygen(head[i]), split[i]);
						}
					}
				}else{
					isHead = true;
					element.addLiner(split[0]);
				}
			}
		}
	}
	
	/**
	 * @return
	 */
	public Finder getFinder(){
		return this.finder;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		StringBuilder sb = new StringBuilder();

		for(String key : this.element.keyValue.keySet()){
			sb.append(String.format("%s:\"%s\"\n", key,this.element.keyValue.get(key)));
		}

		for(int i=0;i<this.element.liner.size();i++){
			sb.append(String.format("<%s>\n",this.element.liner.get(i)));
			List<Map<String,String>> l = this.element.linerData.get(""+i);
			if(l==null)
				continue;
			for(Map<String,String> m : l){
				sb.append("[");
				for(String key : m.keySet()){
					sb.append(String.format("%s:\"%s\",", key,m.get(key)));
				}
				sb.append("]\n");
			}
		}

		return sb.toString();
		
	}

	public String getCsvString() {
		return csvString;
	}
}

