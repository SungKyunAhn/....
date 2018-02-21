package com.aimir.fep.meter.parser.multical401CompatTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.parser.MBusTable.ControlInformation;
import com.aimir.fep.meter.parser.MBusTable.DIF;
import com.aimir.fep.meter.parser.MBusTable.DataBlock;
import com.aimir.fep.meter.parser.MBusTable.VIF;
import com.aimir.fep.util.Hex;

public class DataBlocks extends com.aimir.fep.meter.parser.MBusTable.DataBlocks{
	private static Log log = LogFactory.getLog(DataBlocks.class);

	public DataBlocks(int hourCnt, byte[] rawData,
			ControlInformation controlInformation, int dataBlockCase) {
		super(hourCnt, rawData, controlInformation, dataBlockCase);
		dataBlock = new DataBlock[24];
		dataNames = new String[] { "RECORD ENERGY", "RECORD WATER",
				"RECORD HOUR COUNTER", "RECORD FORWARD TEMPERATURE",
				"RECORD RETURN TEMPERATURE", "RECORD F-R TEMPERATURE",
				"RECORD POWER", "RECORD FLOW", "RECORD READ ENERGY",
				"RECORD READ WATER", "RECORD READ DATE", "END", "COSTUMER NO.",
				"PEAK POWER", "INFO", "TAR2", "TL2", "TAR3", "TL3", "IN A",
				"IN B", "PROGRAM NO.", "CONFIGURATION", "DATE" };
		parse();
	}

	private void parse(){
		switch (dataBlockCase) {
		case DATABLOCK_CASE_BASE:
		case DATABLOCK_CASE_LP:
			parseData();
			break;
		}
	}

	private void parseData() {
		int pos=0;
		byte[] DIF = new byte[1];
		byte[] VIF = new byte[1];
		DIF dif = null;
		VIF vif = null;
		byte[] DATA = null;
		try{
			for(int i=0;(i<dataNames.length&&rawData.length>pos );i++){
				System.arraycopy(rawData, pos, DIF, 0, DIF.length);
				//----------------------
				//LP Data Null
				//----------------------
				if("FF".equals(Hex.decode(DIF))){
					if(i<10){
						pos+=6;
					}
					else if(i==10){
						pos+=4;
					}
					else if(i==11){
						pos+=1;
					}else if(i==12){
						pos+=6;
					}else{
						pos+=4;
					}
					dataBlock[i]= new DataBlock();
					log.debug("==== HourCnt["+hourCnt+"] Idx["+i+"] "+dataNames[i]+" IS EMPTY! =====");
				}else{
					//-------------------------
					//M-Bus Original Format
					//-------------------------
					if(i<11){
						System.arraycopy(rawData, pos, DIF, 0, DIF.length);
				        pos += DIF.length;
						dif=new DIF(DIF);

						System.arraycopy(rawData, pos, VIF, 0, VIF.length);
				        pos += VIF.length;
				        vif=new VIF(VIF);

						DATA=new byte[dif.getDataLength()];
						System.arraycopy(rawData, pos, DATA, 0, DATA.length);
				        pos += DATA.length;

				        dataBlock[i]= new DataBlock(controlInformation, DIF, VIF, DATA, dataNames[i]);
					}
					//-------------------------
					//Kamstrup Modified Format
					//-------------------------
					else{
						if(i==11){
							//END : 1byte
							dif.setDataLength(1);
							dif.setDataType("BCD");

							DATA=new byte[dif.getDataLength()];
							System.arraycopy(rawData, pos, DATA, 0, DATA.length);
					        pos += DATA.length;
							dataBlock[i]= new DataBlock(controlInformation, DATA , dataNames[i]);
						}
						else if(i==12){
							//COSTUMER NO. : 6byte
							dif.setDataLength(6);
							dif.setDataType("BCD");

							DATA=new byte[dif.getDataLength()];
							System.arraycopy(rawData, pos, DATA, 0, DATA.length);
					        pos += DATA.length;
							dataBlock[i]= new DataBlock(controlInformation, DATA , dataNames[i]);
						}
						else{
							//ETC : 4byte
							dif.setDataLength(4);
							dif.setDataType("BCD");

							DATA=new byte[dif.getDataLength()];
							System.arraycopy(rawData, pos, DATA, 0, DATA.length);
					        pos += DATA.length;
							dataBlock[i]= new DataBlock(controlInformation, DATA , dataNames[i]);
						}
					}
				}
			}
		}catch(Exception e){
			log.error(""+e.getMessage());
			e.printStackTrace();
		}
	}
}
