package com.aimir.fep.command.mbean;

public class OSValidator {

	private static String OS = System.getProperty("os.name").toLowerCase();
	private String OSValue = "";
	
	public OSValidator(){

		if (isWindows()) {
			setOSValue("win");
		}if (isUnix()) {	// Unix or Linux or CentOS
			setOSValue("unix");
		}
	}

	public String getOSValue() {
		return OSValue;
	}

	public void setOSValue(String oSValue) {
		OSValue = oSValue;
	}

	public static boolean isWindows() {
		return (OS.indexOf("win") >= 0);
	}

	public static boolean isUnix() {
		return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0);
	}
}
