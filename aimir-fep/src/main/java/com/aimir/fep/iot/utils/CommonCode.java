package com.aimir.fep.iot.utils;

import java.math.BigInteger;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class CommonCode {
	public enum CSE_ID {
		ThingPlug("ThingPlug");
		
		private final String value; 
		CSE_ID(String value){
			this.value = value;
		}
		public String getValue(){return value;}
	}
	
	public enum RESOURCE_TYPE {
		UNKNOW("0", "UNKNOW", null, null),
		ACCESS_CONTROL_POLICY("1", "accessControlPolicy", new BigInteger[] {new BigInteger("23")}, null),
		AE("2", "AE", new BigInteger[] {new BigInteger("23"), new BigInteger("3"), new BigInteger("9"), new BigInteger("1"), new BigInteger("15")}, null),
		CONTAINER("3", "container", new BigInteger[] {new BigInteger("4"), new BigInteger("23"), new BigInteger("3")}, null),
		CONTENT_INSTANCE("4", "contentInstance", null, null), 
		CSE_BASE("5", "CSEBase", new BigInteger[] {new BigInteger("16"), new BigInteger("14"), new BigInteger("2"), new BigInteger("3"), new BigInteger("9"), new BigInteger("1"), new BigInteger("23"), new BigInteger("12"), new BigInteger("10"), new BigInteger("22"), new BigInteger("21"), new BigInteger("17"), new BigInteger("6"), new BigInteger("18"), new BigInteger("11"), new BigInteger("19")}, null),
		DELIVERY("6", "delivery", new BigInteger[] {new BigInteger("23")}, null),
		EVENT_CONFIG("7", "eventConfig", new BigInteger[] {new BigInteger("23")}, null),
		EXEC_INSTANCE("8", "execInstance", new BigInteger[] {new BigInteger("23")}, null),
		GROUP("9", "mgroup", new BigInteger[] {new BigInteger("23")}, null),
		REAL_GROUP("9", "group", new BigInteger[] {new BigInteger("23")}, null),
		LOCATION_POLICY("10", "locationPolicy", new BigInteger[] {new BigInteger("23")}, null),
		M2M_SERVICE_SUBSCRIPTION_PROFILE("11", "m2mServiceSubscriptionProfile", new BigInteger[] {new BigInteger("23"), new BigInteger("20")}, null),
		MGMT_CMD("12", "mgmtCmd", new BigInteger[] {new BigInteger("23"), new BigInteger("8")}, null),
		MGMT_OBJ("13", "mgmtObj", new BigInteger[] {new BigInteger("23")}, null),
		NODE("14", "node", new BigInteger[] {new BigInteger("23")}, new BigInteger[] {new BigInteger("1003"), new BigInteger("1006"), new BigInteger("1004"), new BigInteger("1005"), new BigInteger("1001"), new BigInteger("1002"), new BigInteger("1007"), new BigInteger("1008"), new BigInteger("1009"), new BigInteger("1010"), new BigInteger("1011"), new BigInteger("1012")}),
		POLLING_CHANNEL("15", "pollingChannel", null, null),
		REMOTE_CSE("16", "remoteCSE", new BigInteger[] {new BigInteger("2"), new BigInteger("3"), new BigInteger("12"), new BigInteger("9"), new BigInteger("1"), new BigInteger("23"), new BigInteger("15"), new BigInteger("18"), new BigInteger("10014")}, null),
		REQUEST("17", "request", new BigInteger[] {new BigInteger("23")}, null),
		SCHEDULE("18", "schedule", new BigInteger[] {new BigInteger("23")}, null),
		SERVICE_SUBSCRIBED_APP_RULE("19", "serviceSubscribedAppRule", new BigInteger[] {new BigInteger("23")}, null),
		SERVICE_SUBSCRIBED_NODE("20", "serviceSubscribedNode", new BigInteger[] {new BigInteger("23")}, null),
		STATS_COLLECT("21", "statsCollect", new BigInteger[] {new BigInteger("23")}, null),
		STATS_CONFIG("22", "statsConfig", new BigInteger[] {new BigInteger("7"), new BigInteger("23")}, null),
		SUBSCRIPTION("23", "subscription", new BigInteger[] {new BigInteger("18")}, null),
		ACCESS_CONTROL_POLICY_ANNC("10001", "accessControlPolicyAnnc", null, null),
		AE_ANNC("10002", "AEAnnc", null, null),
		CONTAINER_ANNC("10003", "containerAnnc", null, null),
		CONTENT_INSTANCE_ANNC("10004", "contentInstanceAnnc", null, null),
		GROUP_ANNC("10009", "groupAnnc", null, null),
		LOCATION_POLICY_ANNC("10010", "locationPolicyAnnc", null, null),
		MGMT_OBJ_ANNC("10013", "mgmtObjAnnc", null, null),
		NODE_ANNC("10014", "nodeAnnc", null, null),
		REMOTE_CSE_ANNC("10016", "remoteCSEAnnc", null, null),
		SCHEDULE_ANNC("10018", "scheduleAnnc", null, null),
		
		SUBSCRIPTION_PENDING("90000001", "MMP_SUBSCRIPTION_PENDING_TBL", null, null),
		API_LOG("90000002", "MMP_API_CALL_LOG_TBL", null, null);
		
		private final String value;
		private final String name;
		private final BigInteger[] childResourceTypeArray;
		private final BigInteger[] childMgmtDefinitionArray;
		
		RESOURCE_TYPE(String value, String name, BigInteger[] childResourceTypeArray, BigInteger[] childMgmtDefinitionArray){
			this.value = value;
			this.name = name;
			this.childResourceTypeArray = childResourceTypeArray;
			this.childMgmtDefinitionArray = childMgmtDefinitionArray;
		}
		public BigInteger getValue(){return new BigInteger(value);}
		public String getName(){return name;}
		public BigInteger[] getChildResourceTypeArray(){return childResourceTypeArray;}
		public BigInteger[] getChildMgmtDefinitionArray(){return childMgmtDefinitionArray;}
		
		public static RESOURCE_TYPE getThis(BigInteger value) {
			RESOURCE_TYPE resourceType = null;
			for(RESOURCE_TYPE type : RESOURCE_TYPE.values()) {
				if(value.equals(type.getValue())) {
					resourceType = type;
					break;
				}
			}
			return resourceType;
		}
	}
	
	public enum MEMBER_TYPE {
		ACCESS_CONTROL_POLICY("1", "accessControlPolicy"),
		AE("2", "AE"),
		CONTAINER("3", "container"),
		CONTENT_INSTANCE("4", "contentInstance"), 
		CSE_BASE("5", "CSEBase"),
		DELIVERY("6", "delivery"),
		EVENT_CONFIG("7", "eventConfig"),
		EXEC_INSTANCE("8", "execInstance"),
		GROUP("9", "group"),
		LOCATION_POLICY("10", "locationPolicy"),
		M2M_SERVICE_SUBSCRIPTION_PROFILE("11", "m2mServiceSubscriptionProfile"),
		MGMT_CMD("12", "mgmtCmd"),
		MGMT_OBJ("13", "mgmtObj"),
		NODE("14", "node"),
		POLLING_CHANNEL("15", "pollingChannel"),
		REMOTE_CSE("16", "remoteCSE"),
		REQUEST("17", "request"),
		SCHEDULE("18", "schedule"),
		SERVICE_SUBSCRIBED_APP_RULE("19", "serviceSubscribedAppRule"),
		SERVICE_SUBSCRIBED_NODE("20", "serviceSubscribedNode"),
		STATS_COLLECT("21", "statsCollect"),
		STATS_CONFIG("22", "statsConfig"),
		SUBSCRIPTION("23", "subscription"),
		MIXED("24", "mixed");
		
		private final String value;
		private final String name;
		
		MEMBER_TYPE(String value, String name){
			this.value = value;
			this.name = name;
		}
		public BigInteger getValue(){return new BigInteger(value);}
		public String getName(){return name;}
		
		public static MEMBER_TYPE getThis(BigInteger value) {
			MEMBER_TYPE memberType = null;
			for(MEMBER_TYPE type : MEMBER_TYPE.values()) {
				if(value.equals(type.getValue())) {
					memberType = type;
					break;
				}
			}
			return memberType;
		}
	}
	
	public enum CSE_TYPE {
		IN_CSE("1"),
		MN_CSE("2"),
		ASN_CSE("3");
		
		private final String value;
		CSE_TYPE(String value){
			this.value = value;
		}
		public BigInteger getValue() {return new BigInteger(value);}
	}

	public enum CONSISTENCY_STRATEGY {
		ABANDON_MEMBER("1"),
		ABANDON_GROUP("2"),
		SET_MIXED("3");
		
		private final String value;
		CONSISTENCY_STRATEGY(String value){
			this.value = value;
		}
		public BigInteger getValue() {return new BigInteger(value);}
	}
	
	public enum EXEC_STATUS {
		INITIATED("1"),
		PENDING("2"),
		FINISHED("3"),
		CANCELLING("4"),
		CANCELLED("5"),
		STATUS_NON_CANCELLABLE("6");
		
		private final String value;
		EXEC_STATUS(String value){
			this.value = value;
		}
		public BigInteger getValue() {return new BigInteger(value);}
	}
	
	public enum EXEC_RESULT{
		STATUS_REQUEST_UNSUPPORTED("1"),
		STATUS_REQUEST_DENIED("2"),
		STATUS_CANCELLATION_DENIED("3"),
		STATUS_INTERNAL_ERROR("4"),
		STATUS_INVALID_ARGUMENTS("5"),
		STATUS_RESOURCES_EXCEEDED("6"),
		STATUS_FILE_TRANSFER_FAILED("7"),
		STATUS_FILE_TRANSFER_SERVER_AUTHENTICATION_FAILURE("8"),
		STATUS_UNSUPPORTED_PROTOCOL("9"),
		STATUS_UPLOAD_FAILED("10"),
		STATUS_FILE_TRANSFER_FAILED_MULTICAST_GROUP_UNABLE_JOIN("11"),
		STATUS_FILE_TRANSFER_FAILED_SERVER_CONTACT_FAILED("12"),
		STATUS_FILE_TRANSFER_FAILED_FILE_ACCESS_FAILED("13"),
		STATUS_FILE_TRANSFER_FAILED_DOWNLOAD_INCOMPLETE("14"),
		STATUS_FILE_TRANSFER_FAILED_FILE_CORRUPTED("15"),
		STATUS_FILE_TRANSFER_FILE_AUTHENTICATION_FAILURE("16"),
		//STATUS_FILE_TRANSFER_FAILED("17"),
		//STATUS_FILE_TRANSFER_SERVER_AUTHENTICATION_FAILURE("18"),
		STATUS_FILE_TRANSFER_WINDOW_EXCEEDED("19"),
		STATUS_INVALID_UUID_FORMAT("20"),
		STATUS_UNKNOWN_EXECUTION_ENVIRONMENT("21"),
		STATUS_DISABLED_EXECUTION_ENVIRONMENT("22"),
		STATUS_EXECUTION_ENVIRONMENT_MISMATCH("23"),
		STATUS_DUPLICATE_DEPLOYMENT_UNIT("24"),
		STATUS_SYSTEM_RESOURCES_EXCEEDED("25"),
		STATUS_UNKNOWN_DEPLOYMENT_UNIT("26"),
		STATUS_INVALID_DEPLOYMENT_UNIT_STATE("27"),
		STATUS_INVALID_DEPLOYMENT_UNIT_UPDATE_DOWNGRADE_DISALLOWED("28"),
		STATUS_INVALID_DEPLOYMENT_UNIT_UPDATE_UPGRADE_DISALLOWED("29"),
		STATUS_INVALID_DEPLOYMENT_UNIT_UPDATE_VERSION_EXISTS("30");
		
		private final String value;
		EXEC_RESULT(String value){
			this.value = value;
		}
		public BigInteger getValue() {return new BigInteger(value);}
	}
	
	public enum STATUS{
		SUCCESSFUL("1"),
		FAILURE("2"),
		IN_PROCESS("3");
		
		private final String value;
		STATUS(String value){
			this.value = value;
		}
		public BigInteger getValue() {return new BigInteger(value);}
	}
	
	public enum MGMT_DEFINITION {
		FIRMWARE("1001", "firmware", new BigInteger[] {new BigInteger("23")}),
		SOFTWARE("1002", "software", new BigInteger[] {new BigInteger("23")}),
		MEMORY("1003", "memory", new BigInteger[] {new BigInteger("23")}),
		AREA_NWK_INFO("1004", "areaNwkInfo", new BigInteger[] {new BigInteger("23")}),
		AREA_NWK_DEVICE_INFO("1005", "areaNwkDeviceInfo", new BigInteger[] {new BigInteger("23")}),
		BATTERY("1006", "battery", new BigInteger[] {new BigInteger("23")}),
		DEVICE_INFO("1007", "deviceInfo", new BigInteger[] {new BigInteger("23")}),
		DEVICE_CAPABILITY("1008", "deviceCapability", new BigInteger[] {new BigInteger("23")}),
		REBOOT("1009", "reboot", new BigInteger[] {new BigInteger("23")}),
		EVENT_LOG("1010", "eventLog", new BigInteger[] {new BigInteger("23")}),
		CMDH_POLICY("1011", "cmdhPolicy", null),
		ACTIVE_CMDH_POLICY("1012", "activeCmdhPolicy", null),
		CMDH_DEFAULTS("1013", "cmdhDefaults", null),
		CMDH_DEF_EC_VALUE("1014", "cmdhDefEcValue", null),
		CMDH_EC_DEF_PARAM_VALUES("1015", "cmdhEcDefParamValues", null),
		CMDH_LIMITS("1016", "cmdhLimits", null),
		CMDH_NETWORK_ACCESS_RULES("1017", "cmdhNetworkAccessRules", null),
		CMDH_NW_ACCESS_RULE("1018", "cmdhNwAccessRule", null),
		CMDH_BUFFER("1019", "cmdhBuffer", null),
		UNSPECIFIED("0", "Unspecified", null);
		
		private final String value;
		private final String name;
		private final BigInteger[] childResourceTypeArray;
		
		MGMT_DEFINITION(String value, String name, BigInteger[] childResourceTypeArray){
			this.value = value;
			this.name = name;
			this.childResourceTypeArray = childResourceTypeArray;
		}
		public BigInteger getValue() {return new BigInteger(value);}
		public String getName() {return name;}
		public BigInteger[] getChildResourceTypeArray(){return childResourceTypeArray;}
		
		public static MGMT_DEFINITION getThis(BigInteger value) {
			MGMT_DEFINITION mgmtDefinition = null;
			for(MGMT_DEFINITION definition : MGMT_DEFINITION.values()) {
				if(value.equals(definition.getValue())) {
					mgmtDefinition = definition;
					break;
				}
			}
			return mgmtDefinition;
		}
	}
	
	public enum CMD_TYPE {
		RESET("1"),
		REBOOT("2"),
		UPLOAD("3"),
		DOWNLOAD("4"),
		SOFTWARE_INSTALL("5"),
		SOFTWARE_UNINSTALL("6"),
		SOFTWARE_UPDATE("7"),
		
		PROFILE_UPDATE("90000001"),
		FIRMWARE_UPGRADE("90000002"),
		DEVICE_APP_INSTALL("90000003");
		
		private final String value;
		CMD_TYPE(String value) {
			this.value = value;
		}
		public String getValue() {return value;}
	}	
	
	public enum MGMT_ACCESS_TYPE {
		POLLING("polling"), 
		HTTP("HTTP"), 
		COAP("COAP"), 
		MQTT("MQTT"), 
		SMS("SMS");
		
		private final String value;
		MGMT_ACCESS_TYPE(String value){
			this.value = value;
		}
		public String getValue() {return value;}
	}	
	
	public enum EXEC_MODE {
		IMMEDIATE_ONCE("1"), 
		IMMEDIATE_AND_REPEATEDLY("2"), 
		RANDOM_ONCE("3"),
		RANDOM_AND_REPEATEDLY("4");
		
		private final String value;
		EXEC_MODE(String value){
			this.value = value;
		}
		public BigInteger getValue() {return new BigInteger(value);}
	}
	
	public enum BOOLEAN {
		TRUE("true"), 
		FALSE("false");
		
		private final String value;
		BOOLEAN(String value){
			this.value = value;
		}
		public Boolean getValue() {return Boolean.parseBoolean(value);}
	}
	
	public enum RESOURCE_STATUS {
		CHILD_CREATED("1", "childCreated"),
		CHILD_DELETED("2", "childDeleted"),
		UPDATED("3", "updated"),
		DELETED("4", "deleted"),
		CHILD_UPDATED("90000001", "childUpdated");

		private final String value;
		private final String name;
		RESOURCE_STATUS(String value, String name) {
			this.value = value;
			this.name = name;
		}

		public BigInteger getValue() {return new BigInteger(value);}
		public String getName() {return name;}
		
		public static boolean isValidation(List<BigInteger> values) {
			boolean validation = false;
			for (int i=0; i<values.size(); i++) {
				BigInteger value = values.get(i);
				validation = false;
				
				for(RESOURCE_STATUS type : RESOURCE_STATUS.values()) {
					if(value.equals(type.getValue())) {
						validation = true;
					}
				}
				
				if (!validation) return validation;
				
			}
			return validation;
		}
	}	
	
	public enum PENDING_NOTIFICATION {
		SEND_LATEST("1"),
		SEND_ALL_PENDING("2");

		private final String value;
		PENDING_NOTIFICATION(String value) {
			this.value = value;
		}

		public BigInteger getValue() {return new BigInteger(value);}
		
		public static boolean isValidation(BigInteger value) {
			boolean validation = false;
			for(PENDING_NOTIFICATION type : PENDING_NOTIFICATION.values()) {
				if(value.equals(type.getValue())) {
					validation = true;
				}
			}
			return validation;
		}
	}
	
	public enum NOTIFICATION_CONTENT_TYPE {
		MODIFIED_ATTRIBUTES("1"),
		WHOLE_RESOURCE("2"),
		REFERENCE_ONLY("3");

		private final String value;
		NOTIFICATION_CONTENT_TYPE(String value) {
			this.value = value;
		}
		
		public BigInteger getValue() {return new BigInteger(value);}
		
		public static boolean isValidation(BigInteger value) {
			boolean validation = false;
			for(NOTIFICATION_CONTENT_TYPE type : NOTIFICATION_CONTENT_TYPE.values()) {
				if(value.equals(type.getValue())) {
					validation = true;
				}
			}
			return validation;
		}
	}
	
	public enum FILTER_USAGE {
		DISCOVERY_CRITERIA("1"),
		EVENT_NOTIFICATION_CRITERIA("2");

		private final String value;
		FILTER_USAGE(String value) {
			this.value = value;
		}
		
		public BigInteger getValue() {return new BigInteger(value);}
		
		public static boolean isValidation(BigInteger value) {
			boolean validation = false;
			for(FILTER_USAGE type : FILTER_USAGE.values()) {
				if(value.equals(type.getValue())) {
					validation = true;
				}
			}
			return validation;
		}
	}
	
	public enum RESULT_CONTENT {
		NOTHING("0"),
		ATTRIBUTES("1"),
		HIERARCHICAL_ADDRESS("2"),
		HIERARCHICAL_ADDRESS_AND_ATTRIBUTES("3"),
		ATTRIBUTES_AND_CHILD_RESOURCES("4"),
		ATTRIBUTES_AND_CHILD_RESOURCE_REFERENCES("5"),
		CHILD_RESOURCE_REFERENCES("6"),
		ORIGINAL_RESOURCE("7"),
		CHILD_RESOURCES("90000001");

		private final String value;
		RESULT_CONTENT(String value) {
			this.value = value;
		}
		
		public BigInteger getValue() {return new BigInteger(value);}
		
		public static boolean isValidation(BigInteger value) {
			boolean validation = false;
			for(RESULT_CONTENT type : RESULT_CONTENT.values()) {
				if(value.equals(type.getValue())) {
					validation = true;
				}
			}
			return validation;
		}
	}
	
	public enum RSC {
		EMPTY(""),
		ACCEPTED("1000"),
		OK("2000"), 
		CREATED("2001"), 
		DELETED("2002"), 
		CHANGED("2004"), 
		BAD_REQUEST("4000"),
		NOT_FOUND("4004"),
		OPERATION_NOT_ALLOWED("4005"),
		REQUEST_TIMEOUT("4008"),
		SUBSCRIPTION_CREATOR_HAS_NO_PRIVILEGE("4101"),
		CONTENTS_UNACCEPTABLE("4102"),
		ACCESS_DENIED("4103"),
		GROUP_REQUEST_IDENTIFIER_EXISTS("4104"),
		CONFLICT("4105"),
		INTERNAL_SERVER_ERROR("5000"),
		NOT_IMPLEMENTED("5001"),
		TARGET_NOT_REACHABLE("5103"),
		NO_PRIVILEGE("5105"),
		ALREADY_EXISTS("5106"),
		TARGET_NOT_SUBSCRIBABLE("5203"),
		SUBSCRIPTION_VERIFICATION_INITIATION_FAILED("5204"),
		SUBSCRIPTION_HOST_HAS_NO_PRIVILEGE("5205"),
		NON_BLOCKING_REQUEST_NOT_SUPPORTED("5206"),
		EXTENAL_OBJECT_NOT_REACHABLE("6003"),
		EXTENAL_OBJECT_NOT_FOUND("6005"),
		MAX_NUMBERF_OF_MEMBER_EXCEEDED("6010"),
		MEMBER_TYPE_INCONSISTENT("6011"),
		MGMT_SESSION_CANNOT_BE_ESTABLISHED("6020"),
		MGMT_SESSION_ESTABLISHMENT_TIMEOUT("6021"),
		INVALID_CMDTYPE("6022"),
		INVALID_ARGUMENTS("6023"),
		INSUFFICIENT_ARGUMENTS("6024"),
		MGMT_CONVERSION_ERROR("6025"),
		MGMT_CANCELATION_FAILURE("6026"),
		ALREADY_COMPLETE("6028"),
		COMMAND_NOT_CANCELLABLE("6029");
		
		private final String value;
		RSC(String value){
			this.value = value;
		}
		public String getValue() {return value;}
	}
	
	public enum SHORT_NAMES {
		RESOURCE_TYPE("resourceType", "rty"),
		LABELS("labels", "lbl"),
		ACCESSCONTROLPOLICYIDS("accessControlPolicyIDs", "acpi"),
		ANNOUNCEDATTRIBUTE("announcedAttribute", "aa"),
		ANNOUNCETO("announceTo", "at"),
		CREATIONTIME("creationTime", "ct"),
		EXPIRATIONTIME("expirationTime", "et"),
		LASTMODIFIEDTIME("lastModifiedTime", "lt"),
		PARENTID("parentID", "pi"),
		RESOURCEID("resourceID", "ri"),
		STATETAG("stateTag", "st"),
		RESOURCENAME("resourceName", "rn"),
		PRIVILEGES("privileges", "pv"),
		SELFPRIVILEGES("selfPrivileges", "pvs"),
		APP_ID("App-ID", "api"),
		AE_ID("AE-ID", "aei"),
		APPNAME("appName", "apn"),
		POINTOFACCESS("pointOfAccess", "poa"),
		ONTOLOGYREF("ontologyRef", "or"),
		NODELINK("nodeLink", "nl"),
		CREATOR("creator", "cr"),
		MAXNROFINSTANCES("maxNrOfInstances", "mni"),
		MAXBYTESIZE("maxByteSize", "mbs"),
		MAXINSTANCEAGE("maxInstanceAge", "mia"),
		CURRENTNROFINSTANCES("currentNrOfInstances", "cni"),
		CURRENTBYTESIZE("currentByteSize", "cbs"),
		LATEST("latest", "la"),
		LOCATIONID("locationID", "li"),
		CONTENTINFO("contentInfo", "cnf"),
		CONTENTSIZE("contentSize", "cs"),
		CONTENT("content", "con"),
		CSETYPE("cseType", "cst"),
		CSE_ID("CSE-ID", "csi"),
		SUPPORTEDRESOURCETYPE("supportedResourceType", "srt"),
		NOTIFICATIONCONGESTIONPOLICY("notificationCongestionPolicy", "ncp"),
		SOURCE("source", "sr"),
		TARGET("target", "tg"),
		LIFESPAN("lifespan", "ls"),
		EVENTCAT("eventCat", "ec"),
		DELIVERYMETADATA("deliveryMetaData", "dmd"),
		AGGREGATEDREQUEST("aggregatedRequest", "arq"),
		EVENTID("eventID", "evi"),
		EVENTTYPE("eventType", "evt"),
		EVENSTART("evenStart", "evs"),
		EVENTEND("eventEnd", "eve"),
		OPERATIONTYPE("operationType", "opt"),
		DATASIZE("dataSize", "ds"),
		EXECSTATUS("execStatus", "exs"),
		EXECRESULT("execResult", "exr"),
		EXECDISABLE("execDisable", "exd"),
		EXECTARGET("execTarget", "ext"),
		EXECMODE("execMode", "exm"),
		EXECFREQUENCY("execFrequency", "exf"),
		EXECDELAY("execDelay", "exy"),
		EXECNUMBER("execNumber", "exn"),
		EXECREQARGS("execReqArgs", "exra"),
		EXECENABLE("execEnable", "exe"),
		MEMBERTYPE("memberType", "mt"),
		CURRENTNROFMEMBERS("currentNrOfMembers", "cnm"),
		MAXNROFMEMBERS("maxNrOfMembers", "mnm"),
		MEMBERID("memberID", "mid"),
		MEMBERSACCESSCONTROLPOLICYIDS("membersAccessControlPolicyIDs", "macp"),
		MEMBERTYPEVALIDATED("memberTypeValidated", "mtv"),
		CONSISTENCYSTRATEGY("consistencyStrategy", "csy"),
		GROUPNAME("groupName", "gn"),
		LOCATIONSOURCE("locationSource", "los"),
		LOCATIONUPDATEPERIOD("locationUpdatePeriod", "lou"),
		LOCATIONTARGETID("locationTargetId", "lot"),
		LOCATIONSERVER("locationServer", "lor"),
		LOCATIONCONTAINERID("locationContainerID", "loi"),
		LOCATIONCONTAINERNAME("locationContainerName", "lon"),
		LOCATIONSTATUS("locationStatus", "lost"),
		SERVICEROLES("serviceRoles", "svr"),
		DESCRIPTION("description", "dc"),
		CMDTYPE("cmdType", "cmt"),
		MGMTDEFINITION("mgmtDefinition", "mgd"),
		OBJECTIDS("objectIDs", "obis"),
		OBJECTPATHS("objectPaths", "obps"),
		NODEID("nodeID", "ni"),
		HOSTEDCSELINK("hostedCSELink", "hcl"),
		CSEBASE("CSEBase", "cb"),
		M2M_EXT_ID("M2M-Ext-ID", "mei"),
		TRIGGER_RECIPIENT_ID("Trigger-Recipient-ID", "tri"),
		REQUESTREACHABILITY("requestReachability", "rr"),
		ORIGINATOR("originator", "og"),
		METAINFORMATION("metaInformation", "mi"),
		REQUESTSTATUS("requestStatus", "rs"),
		OPERATIONRESULT("operationResult", "ol"),
		OPERATION("operation", "opn"),
		REQUESTID("requestID", "rid"),
		SCHEDULEELEMENT("scheduleElement", "se"),
		DEVICEIDENTIFIER("deviceIdentifier", "di"),
		STATSCOLLECTID("statsCollectID", "sci"),
		COLLECTINGENTITYID("collectingEntityID", "cei"),
		COLLECTEDENTITYID("collectedEntityID", "cdi"),
		STATUS("status", "ss"),
		STATSRULESTATUS("statsRuleStatus", "srs"),
		STATMODEL("statModel", "sm"),
		COLLECTPERIOD("collectPeriod", "cp"),
		EVENTNOTIFICATIONCRITERIA("eventNotificationCriteria", "enc"),
		EXPIRATIONCOUNTER("expirationCounter", "exc"),
		NOTIFICATIONURI("notificationURI", "nu"),
		NOTIFICATIONFORWARDINGURI("notificationForwardingURI", "nfu"),
		BATCHNOTIFY("batchNotify", "bn"),
		RATELIMIT("rateLimit", "rl"),
		PRESUBSCRIPTIONNOTIFY("preSubscriptionNotify", "psn"),
		PENDINGNOTIFICATION("pendingNotification", "pn"),
		NOTIFICATIONSTORAGEPRIORITY("notificationStoragePriority", "nsp"),
		LATESTNOTIFY("latestNotify", "ln"),
		NOTIFICATIONCONTENTTYPE("notificationContentType", "nct"),
		NOTIFICATIONEVENTCAT("notificationEventCat", "nec"),
		SUBSCRIBERURI("subscriberURI", "su"),
		VERSION("version", "vr"),
		URL("URL", "url"),
		UPDATE("update", "ud"),
		UPDATESTATUS("updateStatus", "uds"),
		INSTALL("install", "in"),
		UNINSTALL("uninstall", "un"),
		INSTALLSTATUS("installStatus", "ins"),
		ACTIVATE("activate", "act"),
		DEACTIVATE("deactivate", "dea"),
		ACTIVATESTATUS("activateStatus", "acts"),
		MEMAVAILABLE("memAvailable", "mma"),
		MEMTOTAL("memTotal", "mmt"),
		AREANWKTYPE("areaNwkType", "ant"),
		LISTOFDEVICES("listOfDevices", "ldv"),
		DEVID("devId", "dvd"),
		DEVTYPE("devType", "dvt"),
		AREANWKID("areaNwkId", "awi"),
		SLEEPINTERVAL("sleepInterval", "sli"),
		SLEEPDURATION("sleepDuration", "sld"),
		LISTOFNEIGHBORS("listOfNeighbors", "lnh"),
		BATTERYLEVEL("batteryLevel", "btl"),
		BATTERYSTATUS("batteryStatus", "bts"),
		DEVICELABEL("deviceLabel", "dlb"),
		MANUFACTURER("manufacturer", "man"),
		MODEL("model", "mod"),
		DEVICETYPE("deviceType", "dty"),
		FWVERSION("fwVersion", "fwv"),
		SWVERSION("swVersion", "swv"),
		HWVERSION("hwVersion", "hwv"),
		CAPABILITYNAME("capabilityName", "can"),
		ATTACHED("attached", "att"),
		CAPABILITYACTIONSTATUS("capabilityActionStatus", "cas"),
		ENABLE("enable", "ena"),
		DISABLE("disable", "dis"),
		CURRENTSTATE("currentState", "cus"),
		REBOOT("reboot", "rbo"),
		FACTORYRESET("factoryReset", "far"),
		LOGTYPEID("logTypeId", "lgt"),
		LOGDATA("logData", "lgd"),
		LOGACTIONSTATUS("logActionStatus", "lgs"),
		LOGSTART("logStart", "lga"),
		LOGSTOP("logStop", "lgo"),
		NAME("name", "nam"),
		MGMTLINK("mgmtLink", "cmlk"),
		ORDER("order", "od"),
		DEFECVALUE("defEcValue", "dev"),
		REQUESTORIGIN("requestOrigin", "ror"),
		REQUESTCONTEXT("requestContext", "rct"),
		REQUESTCONTEXTNOTIFICATION("requestContextNotification", "rcn"),
		REQUESTCHARACTERISTICS("requestCharacteristics", "rch"),
		APPLICABLEEVENTCATEGORIES("applicableEventCategories", "aecs"),
		APPLICABLEEVENTCATEGORY("applicableEventCategory", "aec"),
		DEFAULTREQUESTEXPTIME("defaultRequestExpTime", "dqet"),
		DEFAULTRESULTEXPTIME("defaultResultExpTime", "dset"),
		DEFAULTOPEXECTIME("defaultOpExecTime", "doet"),
		DEFAULTRESPPERSISTENCE("defaultRespPersistence", "drp"),
		DEFAULTDELAGGREGATION("defaultDelAggregation", "dda"),
		LIMITSEVENTCATEGORY("limitsEventCategory", "lec"),
		LIMITSREQUESTEXPTIME("limitsRequestExpTime", "lqet"),
		LIMITSRESULTEXPTIME("limitsResultExpTime", "lset"),
		LIMITSOPEXECTIME("limitsOpExecTime", "loet"),
		LIMITSRESPPERSISTENCE("limitsRespPersistence", "lrp"),
		LIMITSDELAGGREGATION("limitsDelAggregation", "lda"),
		TARGETNETWORK("targetNetwork", "ttn"),
		MINREQVOLUME("minReqVolume", "mrv"),
		BACKOFFPARAMETERS("backOffParameters", "bop"),
		OTHERCONDITIONS("otherConditions", "ohc"),
		MAXBUFFERSIZE("maxBufferSize", "mbfs"),
		STORAGEPRIORITY("storagePriority", "sgp"),
		APPLICABLECREDIDS("applicableCredIDs", "aci"),
		ALLOWEDAPP_IDS("allowedApp-IDs", "aai"),
		ALLOWEDAES("allowedAEs", "aae");
		
		private final String longName;
		private final String shortName;
		
		SHORT_NAMES(String longName, String shortName){
			this.longName = longName;
			this.shortName = shortName;
		}
		public String getLongName(){return longName;}
		public String getShortName(){return shortName;}
		
		public static String Short2LongName(String shortName) {
			for(SHORT_NAMES shortNameTmp : SHORT_NAMES.values()) {
				if(shortName.equals(shortNameTmp.getShortName())) {
					return shortNameTmp.getLongName();
				}
			}
			return "";
		}
		
	}
	
	public enum CALLER_FG {
		DEVICE("O07001"),
		USER("O07002"),
		TOPIC("O07003");

		private final String value;
		CALLER_FG(String value) { this.value = value; }
		public String getValue() { return value; }
	}
	
	public enum CALL_TYPE {
		CONTENT_INSTANCE_CREATE("O06001"),
		CONTENT_INSTANCE_RETRIEVE("O06002"),
		EXEC_INSTANCE_CREATE("O06011"),
		EXEC_INSTANCE_RETRIEVE("O06012"),
		EXEC_INSTANCE_UPDATE("O06013");

		private final String value;
		CALL_TYPE(String value) { this.value = value; }
		public String getValue() { return value; }
	}
	
	public enum KEY_TYPE {
		DKEY("1", "dKey"),
		AKEY("2", "aKey"),
		UKEY("3", "uKey");

		private final String value;
		private final String name;
		KEY_TYPE(String value, String name) {
			this.value = value;
			this.name = name;
		}

		public BigInteger getValue() {return new BigInteger(value);}
		public String getName() {return name;}
		
		public static boolean isValidation(List<BigInteger> values) {
			boolean validation = false;
			for (int i=0; i<values.size(); i++) {
				BigInteger value = values.get(i);
				validation = false;
				
				for(KEY_TYPE type : KEY_TYPE.values()) {
					if(value.equals(type.getValue())) {
						validation = true;
					}
				}
				
				if (!validation) return validation;
				
			}
			return validation;
		}
	}
	
	
	public enum DCU_TYPE {
		ND("00"), 
		IN_DOOR("10"), 
		OUT_DOOR("20");
		
		private final String value;
		DCU_TYPE(String value){
			this.value = value;
		}
		public String getValue() {return value;}
	}
	
	
	public enum DCU_KIND {
		ND("00"), 
		ZIGBEE("10"), 
		PLC("20"),
		HYBRID("30"),
		SUBGIGA("40");
		private final String value;
		DCU_KIND(String value){
			this.value = value;
		}
		public String getValue() {return value;}
	}
	
	public enum DEVICE_TYPE {
		SERVER("1"),
		DCU("2"),
		SENSOR("3");
		
		private final String value;
		DEVICE_TYPE(String value){
			this.value = value;
		}
		public String getValue() {return value;}
	}
	
	public enum NETWORK_PROTOCOL {
		ND("0"), 
		ETHERNET("1"), 
		GPRS("2"),
		TWOG("3"),
		LTE("4"),
		PLC("5"),
		PSTN("6"),
		ZIGBEE("7"),
		SUBGIGA("8");
		private final String value;
		NETWORK_PROTOCOL(String value){
			this.value = value;
		}
		public String getValue() {return value;}
	}
	
	public enum SENSOR_TYPE {
		ND("00"), 
		WEARABLE("01");
		private final String value;
		SENSOR_TYPE(String value){
			this.value = value;
		}
		public String getValue() {return value;}
	}
	
	public enum SENSOR_KIND {
		ND("0"), 
		ETHERNET("1"), 
		GPRS("2"),
		TWOG("3"),
		LTE("4"),
		PLC("5"),
		PSTN("6"),
		ZIGBEE("7"),
		SUBGIGA("8");
		private final String value;
		SENSOR_KIND(String value){
			this.value = value;
		}
		public String getValue() {return value;}
	}

	public enum CNTR_TYPE {
		DCU_CONF("dcuConf"), 
		SENSOR_INFO("sensorInfo"), 
		LOCATION("location"),
		HEART_RATE("heartRate"),
		RSSI("rssi"),
		WD_INFO("wdInfo"),
		BLE_INFO("bleInfo");

		private final String value;
		CNTR_TYPE(String value){
			this.value = value;
		}
		public String getValue() {return value;}
		
		public static CNTR_TYPE getCntrType(String value) {
			CNTR_TYPE cntrType = null;
			for(CNTR_TYPE type : CNTR_TYPE.values()) {
				if(value.equals(type.getValue())) {
					cntrType = type;
					break;
				}
			}
			return cntrType;
		}
	}
	
	public enum EVENT_TYPE {
		UNKNOWN("unknown", "0000"),
		NOMAL("nomal", "0001"),
		SOS_CALL("soscall", "EW001"), 
		ABN_HRM("abnormalhrm","EW002"),
		LOW_BATT("lowbattery", "AW001"),
		WD_TAKEOFF("wdtakeoff","AW002"),
		ABN_USAGE("abnormalusage","EM002"),
		OUTOF_LOC("outofloc","AW003"),
		HRM_STOP("hrmstop","EW003"),
		//2016 11 30 차병준 낙석 발생 추가
		TAKE_OFF("takeoff","EW005"),
		CHARGING("charging","EW006"),
		BEACON("beacon","EW007"),
		FALLING_ROCK("fallingrock","EW004");
				
		
		
		
		private final String name;
		private final String code;
		EVENT_TYPE(String name, String code) {
			this.name = name;
			this.code = code;
		}

		public String getName() {return name;}
		public String getCode() {return code;}
	}
	
	public enum HRM_STATUS_C1002 {
		NORMAL("1"), 
		LOW_HRM("2"), 
		HIGH_HRM("3"),
		STOP_HRM("4");
		
		private final String value;
		HRM_STATUS_C1002(String value){
			this.value = value;
		}
		public String getValue() {return value;}
	}
}
