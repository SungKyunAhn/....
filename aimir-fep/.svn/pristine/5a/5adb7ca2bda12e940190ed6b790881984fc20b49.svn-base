package com.aimir.fep.meter.parser.multical401CompatTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.parser.MBusTable.BaseRecordParent;
import com.aimir.fep.meter.parser.MBusTable.Control;
import com.aimir.fep.meter.parser.MBusTable.ControlInformation;
import com.aimir.fep.meter.parser.MBusTable.FixedDataHeader;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

public class BaseRecord extends BaseRecordParent{
	private static Log log = LogFactory.getLog(BaseRecord.class);

	public BaseRecord(byte[] data) {
		super(data);
		try{
			parse(data);
		}catch(Exception e){
			e.printStackTrace();
			log.error("BaseRecord Parsiong Error: "+e.getMessage());
		}
	}

	public void parse(byte[] data) throws Exception
    {
		byte[] rawData = null;
		byte[] START = new byte[1];
		byte[] LENGTH = new byte[1];
		byte[] CONTROL = new byte[1];
		byte[] ADDRESS = new byte[1];
		byte[] CONTROLINFORMATION = new byte[1];
		byte[] FIXEDDATAHEADER = new byte[12];
		byte[] DATABLOCKS =null;
		byte[] CHECKSUM = new byte[1];
		byte[] STOP = new byte[1];

		int pos=0;
		rawData=data;

		//--------------------------
		//  Start Field
		//--------------------------
		System.arraycopy(rawData, pos, START, 0, START.length);
        pos += START.length;
        start1 = DataUtil.getIntToBytes(START);
        log.debug("START1[" + start1 + "]");
        if(start1==0x68){
        	//--------------------------
    		//  Length Field
    		//--------------------------
            System.arraycopy(rawData, pos, LENGTH, 0, LENGTH.length);
            pos += LENGTH.length;
            length1 = DataUtil.getIntToBytes(LENGTH);
            log.debug("LENGTH[" + length1 + "]");

            //--------------------------
    		//  Length Field
    		//--------------------------
            System.arraycopy(rawData, pos, LENGTH, 0, LENGTH.length);
            pos += LENGTH.length;
            length2 = DataUtil.getIntToBytes(LENGTH);
            log.debug("LENGTH[" + length2 + "]");

            //--------------------------
    		//  Start Field
    		//--------------------------
            System.arraycopy(rawData, pos, START, 0, START.length);
            pos += START.length;
            start2 = DataUtil.getIntToBytes(START);
            log.debug("START[" + start2 + "]");
            if(start2!=0x68){
            	log.error("BaseRecord Start2 Field["+start2+"] - Invalid Exception");
            }

            //--------------------------
    		//  Control Field
    		//--------------------------
            System.arraycopy(rawData, pos, CONTROL, 0, CONTROL.length);
            pos += CONTROL.length;
            control = new Control(CONTROL);
            log.debug("CONTROL[" + control.getControlName() + "]");
            if(control.getControl()!=0x53 && control.getControl()!=0x73  && control.getControl()!=0x08 && control.getControl()!=0x18 && control.getControl()!=0x28 && control.getControl()!=0x38){
            	log.error("BaseRecord Control Field["+control.getControl()+"] Invalid Exception");
            }

            //--------------------------
    		//  Address Field
    		//--------------------------
            System.arraycopy(rawData, pos, ADDRESS, 0, ADDRESS.length);
            pos += ADDRESS.length;
            address = DataUtil.getIntToBytes(ADDRESS);
            log.debug("ADDRESS[" + address + "]");

            //--------------------------
    		//  Control Information Field
    		//--------------------------
            System.arraycopy(rawData, pos, CONTROLINFORMATION, 0, CONTROLINFORMATION.length);
            pos += CONTROLINFORMATION.length;
            controlInformation = new ControlInformation(CONTROLINFORMATION);
            log.debug("CONTROLINFORMATION[" + controlInformation.getControlInfomationString() + "] MODE[" + controlInformation.getMode() + "]");

            //--------------------------
    		//  Fixed Data Header Field
    		//--------------------------
            System.arraycopy(rawData, pos, FIXEDDATAHEADER, 0, FIXEDDATAHEADER.length);
            pos += FIXEDDATAHEADER.length;
            fixedDataHeader = new FixedDataHeader(FIXEDDATAHEADER, controlInformation.getMode());

            //--------------------------
    		//  DATABLOCKS Data Format Field
    		//--------------------------
            //Set UserData Length
            DATABLOCKS= new byte[(length2-15)];
            System.arraycopy(rawData, pos, DATABLOCKS, 0, DATABLOCKS.length);
            pos += DATABLOCKS.length;
            dataBlocks = new DataBlocks(0,DATABLOCKS, controlInformation, DataBlocks.DATABLOCK_CASE_BASE);

            //--------------------------
    		//  Check Sum Field
    		//--------------------------
            System.arraycopy(rawData, pos, CHECKSUM, 0, CHECKSUM.length);
            pos += CHECKSUM.length;
            checkSum = DataUtil.getIntToBytes(CHECKSUM);
            log.debug("CHECKSUM[" + checkSum + "]");
            int calCheckSum=CONTROL[0]+ADDRESS[0]+CONTROLINFORMATION[0];
            for(int i=0;i<FIXEDDATAHEADER.length;i++){
            	calCheckSum+=FIXEDDATAHEADER[i];
            }
            for(int i=0;i<DATABLOCKS.length;i++){
            	calCheckSum+=DATABLOCKS[i];
            }

            if((calCheckSum&0xFF)!=checkSum){
            	throw new Exception("BaseRecord CheckSum Field["+checkSum+"] calCheckSum Field["+calCheckSum+"] Invalid Exception");
            }

            //--------------------------
    		//  Stop Field
    		//--------------------------
            System.arraycopy(rawData, pos, STOP, 0, STOP.length);
            pos += STOP.length;
            stop = DataUtil.getIntToBytes(STOP);
            log.debug("STOP[" + stop + "]");
            if(stop!=0x16){
            	throw new Exception("BaseRecord Stop Field["+stop+"] - Invalid Exception");
            }
        }else if(start1==0xFF){
        	pos = START.length+LENGTH.length+LENGTH.length+START.length+CONTROL.length;
            //--------------------------
    		//  Address Field
    		//--------------------------
            System.arraycopy(rawData, pos, ADDRESS, 0, ADDRESS.length);
            pos += ADDRESS.length;
            address = DataUtil.getIntToBytes(ADDRESS);
            log.debug("ADDRESS[" + address + "]");


            pos += CONTROLINFORMATION.length;
            //--------------------------
    		//  Fixed Data Header Field
    		//--------------------------
            System.arraycopy(rawData, pos, FIXEDDATAHEADER, 0, FIXEDDATAHEADER.length);
            pos += FIXEDDATAHEADER.length;
            if(!"FFFFFFFFFFFFFFFFFFFFFFFF".equals(Hex.decode(FIXEDDATAHEADER))){
	            fixedDataHeader = new FixedDataHeader(FIXEDDATAHEADER, 1);
	            log.debug("IDENFICATION["+fixedDataHeader.getIdentificationNumber()+"]");
            }
        	log.error("BaseRecord Start Field["+start1+"] - Master/Slave Communication Error!");
        }
        else{
        	log.error("BaseRecord Start Field["+start1+"] - Invalid Exception");
        }
    }
}
