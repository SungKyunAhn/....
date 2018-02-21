package com.aimir.fep.meter.parser.plc;

public class PLCDataConstants {
	public static boolean isConvert=false;// Is Convert Endian?

	public static byte SOF = (byte) 0xE1;// start of frame
	public static byte EOF = (byte) 0xE0;// end of frame

	public static int HEADER_LEN = 25;

	public static int SOF_LEN = 1;

	public static int DIR_LEN = 1;
	public static int VER_LEN = 1;
	public static int DID_LEN = 10;
	public static int SID_LEN = 10;
	public static int LENGTH_LEN = 2;
	public static int COMMAND_LEN = 1;

	public static int CRC_LEN = 2;
	public static int EOF_LEN = 1;

	public static int ERR_CODE_LEN = 1;

	public static int ACK_LEN=3;//ACK Length field(command ~ crc16) value
	public static int NAK_LEN=4;//NAK Length field(command ~ crc16) value

	public static int PORT_LEN=4;

	/*
	 * FEP -> IRM(Request or Set)
	 */
	public static byte COMMAND_A = (byte)'A'; //Acknowledge(FEP <--> IRM 공통)
	public static byte COMMAND_B = (byte)'B'; //IRM Status Request
	public static byte COMMAND_C = (byte)'C'; //IRM  Configuration Set
	public static byte COMMAND_D = (byte)'D'; //Data Request(검침 데이터 수집)
	public static byte COMMAND_E = (byte)'E'; //Data Request(최대 수요 데이터 수집)
	public static byte COMMAND_F = (byte)'F'; //Data Request(LP 데이터 수집)
	public static byte COMMAND_G = (byte)'G'; //Data Request(정전/복전 데이터 수집)
	public static byte COMMAND_H = (byte)'H'; //Data Request(통신 상황 데이터 수집)
	public static byte COMMAND_I = (byte)'I'; //Meter Status Request
	public static byte COMMAND_J = (byte)'J'; //Data Request(변압기 감시 데이터 수집)
	public static byte COMMAND_M = (byte)'M'; //변압기 감시 Configuration Data Request
	public static byte COMMAND_N = (byte)'N'; //변압기 감시 Configuration Set

	/*
	 * FEP <- IRM(Response or Trap)
	 */
	public static byte COMMAND_a = (byte)'a'; //Not Acknowledge (FEP <--> IRM 공통)
	public static byte COMMAND_b = (byte)'b'; //IRM Status Response / IRM Trap
	public static byte COMMAND_d = (byte)'d'; //Response(검침 데이터)
	public static byte COMMAND_e = (byte)'e'; //Response(최대 수요 데이터)
	public static byte COMMAND_f = (byte)'f'; //Response(LP 데이터)
	public static byte COMMAND_g = (byte)'g'; //Response(정전/복전 정보)
	public static byte COMMAND_h = (byte)'h'; //Response(통신 상황 정보)
	public static byte COMMAND_i = (byte)'i'; //Meter Status Response / Meter Trap
	public static byte COMMAND_j = (byte)'j'; //Response(변압기 감시 데이터)
	public static byte COMMAND_k = (byte)'k'; //Trap Info.(PLC 통신 오류 Trap)
	public static byte COMMAND_l = (byte)'l'; //Trap Info.(IR 통신 오류 Trap)
	public static byte COMMAND_m = (byte)'m'; //변압기 감시 Configuration Data Response

	public static byte PROTOCOL_DIRECTION_FEP_IRM = (byte) 0x01; //FEP->IRM
	public static byte PROTOCOL_DIRECTION_FEP_SERVER = (byte) 0x02; //FEP->IRM
	public static byte PROTOCOL_DIRECTION_IRM_FEP = (byte) 0x03; //IRM->FEP
	public static byte PROTOCOL_DIRECTION_IRM_TRAP = (byte) 0x04; //IRM->FEP

	public static byte PROTOCOL_VERSION_1_0 = (byte) 0x01;//1.0
	public static byte PROTOCOL_VERSION_1_1 = (byte) 0x11;//1.1

	public static byte ERR_CODE_CRC = (byte)0x01;// CRC16 Error
	public static byte ERR_CODE_FRAME = (byte)0x02;// Frame Error

	public static int ADATA_TOTAL_LEN=1;
	public static int BDATA_TOTAL_LEN=41;
	public static int DDATA_DUMP_TOTAL_LEN=92;
	public static int EDATA_DUMP_TOTAL_LEN=148;
	public static int FDATA_SUB_DUMP_TOTAL_LEN=31;
	public static int GDATA_SUB_DUMP_TOTAL_LEN=10;
	public static int HDATA_SUB_DUMP_TOTAL_LEN=15;
	public static int IDATA_DUMP_TOTAL_LEN=26;
	public static int JDATA_DUMP_TOTAL_LEN=89;
	public static int KDATA_TOTAL_LEN=1;
	public static int LDATA_TOTAL_LEN=22;
	public static int MDATA_TOTAL_LEN=18;

	public static int ACK_FRAME_TOTAL_LEN=29;
	public static int NAK_FRAME_TOTAL_LEN=30;

	//Length Field Value
	public static int ADATAREQUEST_LENGTH_VALUE=3;
	public static int BDATAREQUEST_LENGTH_VALUE=3;
	public static int CDATAREQUEST_LENGTH_VALUE=20;
	public static int DDATAREQUEST_LENGTH_VALUE=24;
	public static int EDATAREQUEST_LENGTH_VALUE=24;
	public static int FDATAREQUEST_LENGTH_VALUE=24;
	public static int GDATAREQUEST_LENGTH_VALUE=23;
	public static int HDATAREQUEST_LENGTH_VALUE=23;
	public static int IDATAREQUEST_LENGTH_VALUE=23;
	public static int JDATAREQUEST_LENGTH_VALUE=3;
	public static int MDATAREQUEST_LENGTH_VALUE=3;
	public static int NDATAREQUEST_LENGTH_VALUE=21;

	public static int METER_ID_LENGTH=20;

	//----------------
	//  Code
	//----------------
	public static int DTYPE_METERING_ALL=0x00;
	public static int DTYPE_METERING_CURR=0x01;
	public static int DTYPE_METERING_PREV=0x02;

	public static int DTYPE_DEMAND_ALL=0x00;
	public static int DTYPE_DEMAND_CURR=0x01;
	public static int DTYPE_DEMAND_PREV=0x02;

	public static int DTYPE_LP_ALL=0x00;
	public static int DTYPE_LP_NORMAL=0x01;
}
