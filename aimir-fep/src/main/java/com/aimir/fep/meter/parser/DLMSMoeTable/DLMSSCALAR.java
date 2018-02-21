package com.aimir.fep.meter.parser.DLMSMoeTable;

public class DLMSSCALAR {
	public enum OBIS {
		 CONSUMPTION_ACTIVEENERGY_IMPORT("0100011DFF", "Consumption active energy -import for LP1"),
		 CONSUMPTION_ACTIVEENERGY_EXPORT("0100021DFF", "Consumption active energy -export for LP1"), 
		 CONSUMPTION_REACTIVEENERGY("0100031DFF", "Consumption reactive energy -import for LP1"),		 
		 CUMULATIVE_ACTIVEENERGY_IMPORT1("0100010800FF","Cumulative active energy -import1"),
		 TOTAL_REACTIVEENERGY("0100050800FF","Total reactive energy"),		 
		 CT_TOTAL_IMPORT_APPARENTENERGY("0100090800FF","Total import apparent energy"),
		 CT_TOTAL_MAXIMUN_DEMAND_A("0100010600FF","Total maxinum demand +A"),
		 CT_TOTAL_MAXIMUN_DEMAND_R("0100030600FF","Total maxinum demand +R"),
		 CT_TOTAL_APPARENT_MAXIMUM_DEMAND("0100090600FF","Total apparent maximum demand"),
		 CT_TOTAL_CUMULATIVE_DEMAND_A("0100010200FF","Total cumulative demand +A"),		 
		 CT_VOLTAGE_PHASE_B("0100340700FF","Voltage, phase B"),
		 CT_CURRENT_PHASE_B("0100330700FF","Current, phase B"),
		 CT_POWER_FACTOR_PHASE_B("0100350700FF","Current, phase B"),
		 CT_CONSUMPTION_ACTIVEENERGY("0100011D00FF","Consumption active energy import for LP1"),
		 CT_CONSUMPTION_REACTIVEENERGY("0100031D00FF","Consumption active energy import for LP1");
		 
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
		 CONSUMPTION_ACTIVEENERGY_IMPORT("0100011DFF", 0),
		 CONSUMPTION_ACTIVEENERGY_EXPORT("0100021DFF", 0), 
		 CONSUMPTION_REACTIVEENERGY("0100031DFF", 0),		 
		 CUMULATIVE_ACTIVEENERGY_IMPORT1("0100010800FF",0),
		 TOTAL_REACTIVEENERGY("0100050800FF",0),		 
		 CT_TOTAL_IMPORT_APPARENTENERGY("0100090800FF",0),
		 CT_TOTAL_MAXIMUN_DEMAND_A("0100010600FF",0),
		 CT_TOTAL_MAXIMUN_DEMAND_R("0100030600FF",0),
		 CT_TOTAL_APPARENT_MAXIMUM_DEMAND("0100090600FF",0),
		 CT_TOTAL_CUMULATIVE_DEMAND_A("0100010200FF",0),
		 CT_VOLTAGE_PHASE_B("0100340700FF",3),
		 CT_CURRENT_PHASE_B("0100330700FF",3),
		 CT_POWER_FACTOR_PHASE_B("0100350700FF",4),
		 CT_CONSUMPTION_ACTIVEENERGY("0100011D00FF",0),
		 CT_CONSUMPTION_REACTIVEENERGY("0100031D00FF",0);
		
		
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
        	if(code.isEmpty()) return 1; 
        		
        	for (OBISSACLAR obis : values()) {
        		if (obis.getCode().equals(code)) {
        			return (1.0 / Math.pow(10, obis.scalar));        			
        		}
        	}        	
        	return 1;
        }
	}
	
}
