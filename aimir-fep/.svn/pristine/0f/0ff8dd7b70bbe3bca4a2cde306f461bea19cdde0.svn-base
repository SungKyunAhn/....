package com.aimir.fep.command.ws.datatype;

/**
 * <p>
 * Application Fault code information
 * 
 * <p>
 * FaultCode.xlsm
 * 
 * 
 */
public enum FaultCode {
	/**
	 * 
	 */
	FC_105(105, "Data reference", "The requested meter value was not found.",
			"The requested meter value could not be found in the database. "
					+ "The reason for this may be that the meter value could "
					+ "not be collected and therefore does not exist in the "
					+ "database or that it have been collected but not yet "
					+ "registered in the database. Meter values will also be "
					+ "deleted when exceeding 12 months age, in which case "
					+ "they cannot be found in the database.\r\n"
					+ "Please make sure that the requested meter value is not "
					+ "more than 12 months old.", true, null), //
	FC_108(108, "Data reference", "The order could not be added.",
			"The reference id is already in use.", false, null), //
	FC_109(109, "Data reference", "The requested location does not exist.",
			"The location id refers to a non-existing location.", false,
			"This differs from 112 in that the location has never existed."), //
	FC_111(
			111,
			"Data reference",
			"The location has no meter number.",
			"The location id refers to a location with a missing meter number.",
			false, null), //
	FC_112(112, "Data reference", "The location has been logically deleted.",
			"The location id refers to a location that has been removed.",
			false,
			"This differs from 109 in that the location has been logically deleted."), //
	FC_113(113, "Data reference", "The meter does not match the location.",
			"The location id is connected to a different meter number.", false,
			null), //
	FC_123(
			123,
			"Generic",
			"An unhandled error occurred",
			"The operation could not be executed due to an error.",
			true,
			"This is a generic error that is used for all type of errors that "
					+ "are not handled as part of this solution. It shall never"
					+ " be used if an exception occurred in which case fault "
					+ "code 103 should be used together with a stacktrace of "
					+ "the exception."), //
	FC_100(100, "Parameter", "Invalid Parameter",
			"The request failed due to incorrect parameters. This can occur if "
					+ "an empty parameter (null) is sent, the parameter has "
					+ "wrong data format or data type.", true, null), //
	FC_104(
			104,
			"Parameter",
			"Invalid meter reading time specified.",
			"The time specified for the requested meter reading is invalid. "
					+ "This may happen if the requested meter value refers to a "
					+ "reading time that is not in the scope for the frequency "
					+ "of meter readings, e.g. a meter reading is done every "
					+ "whole hour but a reading at 13.22 was requested.\r\n"
					+ "Please make sure that the requested meter value time "
					+ "corresponds with the frequency of meter readings set in "
					+ "the system.", false,
			"Used for example in ODR for historical readings "
					+ "(getHistoricalReading)."), //
	FC_114(
			114,
			"Parameter",
			"The order could not be added",
			"Invalid execution time, it must either be nil or set to a future time.",
			false, null), //
	FC_106(106, "Process",
			"The order was executed before it could be cancelled.", "", false,
			null), //
	FC_107(107, "Process", "The order has already been cancelled.", "", false,
			null), //
	FC_110(
			110,
			"Process",
			"The order could not be added.",
			"An order has already been added for this location at the specified time.",
			false,
			"Differs from 115 in that it's also date/time dependant "
					+ "(used for example in ODR to make sure that only one "
					+ "order is placed for one location at a specific date/time)"), //
	FC_115(
			115,
			"Process",
			"The order could not be added.",
			"An active order has already been added for this location.",
			false,
			"Differs from 110 in that it is not date/time dependant "
					+ "(used for example in POO where only one order can exist "
					+ "for one specific location at a time)."), //
	FC_117(
			117,
			"Process",
			"The order could not be added.",
			"It is not possible to perform on-demand readings for water meters.",
			false,
			"Differs from 122 in that this fault code is only valid in ODR."), //
	FC_118(118, "Process", "The order could not be added.",
			"Only electricity locations assigned to the system having meters "
					+ "with circuit breakers are supported.", false, null), //
	FC_121(121, "Process", "Invalid state change",
			"This meter is already in power on state.", false, null), //
	FC_122(122, "Process", "Invalid location type",
			"The location id refers to a location of a type that is not "
					+ "supported in the system.", false,
			"Differs from 117 in that this fault code is used for location "
					+ "types that are not handled at all in the system"), //
	FC_124(124, "Process", "Invalid state change",
			"This meter is already in power off state.", false, null), //
	FC_125(125, "Process", "Invalid state change",
			"This meter is already in deblock state.", false, null), //
	FC_103(103, "System", "Internal Server Error", "", true,
			"Stacktrace must be sent in this case."), //
	FC_101(101, "Transaction", "DB Connection Error", "DB Connection error.",
			false, null), //
	FC_102(102, "Transaction", "Transaction Error",
			"Transaction time out/rollback.", true, null), //
	FC_116(116, "Transaction", "The meter could not be reached.",
			"Differs from 120 in that this fault code is used when the meter "
					+ "could not be reached and an \"energy\" alert was found "
					+ "by the system for this location.", false, null), //
	FC_119(
			119,
			"Transaction",
			"The request could not be executed.",
			"The request could not be executed within the specified time limit.",
			true, null), //
	FC_120(120, "Transaction", "The meter could not be reached.",
			"There was no answer from the meter.", true,
			"Differs from 116 in that this fault code is used when the meter "
					+ "could not be reached and there was no \"energy\" alert "
					+ "found by the system for this location."), //
	FC_126(126, "Data reference", "The location has no meter.",
			"The location id refers to a location without an installed meter.",
			false,
			"This differs from 111 in that the location lacks a meter completely"),

	FC_127(127, "Data reference", "The location already exist.",
			"The location already exist.", false, null), //
	FC_128(128, "Data reference", "The meter already exist.",
			"The meter already exist.", false, null), //
	FC_129(129, "Data reference", "The terminal already exist.",
			"The terminal already exist.", false, null), //
	FC_130(130, "Data reference", "The requested meter does not exist.",
			"The meter id refers to a non-existing meter.", false, null), //
	FC_131(131, "Data reference", "The meter has been logically deleted.",
			"The meter id refers to a meter that has been removed.", false,
			"This differs from 130 in that the meter has been logically deleted"), //
	FC_132(132, "Data reference", "The requested terminal does not exist.",
			"The terminal id refers to a non-existing meter.", false, null), //
	FC_133(133, "Data reference", "The terminal has been logically deleted.",
			"The terminal id refers to a terminal that has been removed.",
			false,
			"This differs from 132 in that the terminal has been logically deleted"), //
	FC_134(134, "Data reference", "The location have active relation.",
			"The location have active relation.", false, null), //
	FC_135(135, "Data reference", "The meter have active relation.",
			"The meter have active relation.", false, null), //
	FC_136(136, "Data reference", "The terminal have active relation.",
			"The terminal have active relation.", false, null), //
	FC_137(137, "Data reference", "The meter have active relation with location.",
			"The meter have active relation with location.", false, null),
	FC_138(138, "Data reference", "The meter and terminal can't make relation with wrong type.",
			"The meter and terminal can't make relation with wrong type.", false, null),
	FC_139(139, "Data reference", "The location and meter can't make relation with wrong type.",
			"The location and meter can't make relation with wrong type.", false, null),
	FC_140(140, "Data reference", "The meter is not electricity meter",
			"The meter is not electricity meter", false, null);
	
	int code;
	String type;
	String summary;
	String details;
	boolean temporary;
	String note;

	private FaultCode(int code, String type, String summary, String details,
			boolean temporary, String note) {
		this.code = code;
		this.type = type;
		this.summary = summary;
		this.details = details;
		this.temporary = temporary;
		this.note = note;
	}

	public int getCode() {
		return code;
	}

	public String getType() {
		return type;
	}

	public String getSummary() {
		return summary;
	}

	public String getDetails() {
		return details;
	}

	public boolean isTemporary() {
		return temporary;
	}

	public String getNote() {
		return note;
	}

	public static FaultCode getFaultCode(int code) {
		for (FaultCode os : FaultCode.values()) {
			if (os.getCode() == code) {
				return os;
			}
		}
		return null;
	}
}