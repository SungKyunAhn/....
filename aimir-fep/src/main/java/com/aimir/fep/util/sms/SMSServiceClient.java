package com.aimir.fep.util.sms;

import java.util.HashMap;
import java.util.List;

public interface SMSServiceClient {
	
	public String execute(HashMap<String, Object> condition, List<String> paramList, String cmdMap) throws Exception;
}
