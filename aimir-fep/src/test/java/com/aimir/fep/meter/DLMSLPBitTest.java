package com.aimir.fep.meter;

import com.aimir.fep.meter.parser.DLMSNamjunTable.DLMSVARIABLE;

public class DLMSLPBitTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {


		DLMSLPBitTest test = new DLMSLPBitTest();
		byte[] bitByte = new byte[]{(byte) 0x80};
		System.out.println(test.getLP_STATUS(bitByte));

	}
	
	
    public String getLP_STATUS(byte[] value){
        StringBuffer str = new StringBuffer("");
        int byte0 = value[0] & 0xFF;
        for(int i = 0; i < 8; i++){
        	if((byte0 & (1 << (7-i))) > 0){
        		str.append(DLMSVARIABLE.LP_STATUS_BIT[i]+", \n");
        	}
        }
        return str.toString();
    }

}
