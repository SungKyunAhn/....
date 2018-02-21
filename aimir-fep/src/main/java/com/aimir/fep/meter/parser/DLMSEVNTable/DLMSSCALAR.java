package com.aimir.fep.meter.parser.DLMSEVNTable;

public class DLMSSCALAR {
	public enum OBIS {
		  CONSUMPTION_ACTIVEENERGY_IMPORT("0100011DFF", "Consumption active energy -import for LP1")
		, CONSUMPTION_ACTIVEENERGY_EXPORT("0100021DFF", "Consumption active energy -export for LP1")
		, CONSUMPTION_REACTIVEENERGY_IMPORT("0100031DFF", "Consumption reactive energy -import for LP1")
		, CONSUMPTION_REACTIVEENERGY_EXPORT("0100041DFF", "Consumption reactive energy -export for LP1");

		private String code;
		private String name;

		OBIS(String code, String name) {
			this.code = code;
			this.name = name;
		}

		public String getCode() {
			return this.code;
		}

		public String getName() {
			return this.name;
		}
	}

	public enum OBISSACLAR {
  		  CONSUMPTION_ACTIVEENERGY_IMPORT("0100011DFF", 0)
 		, CONSUMPTION_ACTIVEENERGY_EXPORT("0100021DFF", 0)
 		, CONSUMPTION_REACTIVEENERGY_IMPORT("0100031DFF", 0)
 		, CONSUMPTION_REACTIVEENERGY_EXPORT("0100041DFF", 0);

		private String code;
		private int scalar;

		OBISSACLAR(String code, int scalar) {
			this.code = code;
			this.scalar = scalar;
		}

		public String getCode() {
			return this.code;
		}

		public int getScalar() {
			return this.scalar;
		}

		public static double getOBISScalar(String code) {
			if (code.isEmpty())
				return 1;

			for (OBISSACLAR obis : values()) {
				if (obis.getCode().equals(code)) {
					return (1.0 / Math.pow(10, obis.scalar));
				}
			}
			return 1;
		}
	}

}
