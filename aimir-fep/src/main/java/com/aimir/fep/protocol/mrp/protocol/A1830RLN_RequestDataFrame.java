package com.aimir.fep.protocol.mrp.protocol;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;

import com.aimir.fep.protocol.fmp.datatype.BYTE;
import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.protocol.fmp.datatype.TIMESTAMP;
import com.aimir.fep.protocol.mrp.command.frame.RequestDataFrame;
import com.aimir.fep.util.ByteArray;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

public class A1830RLN_RequestDataFrame extends RequestDataFrame{
	
    @SuppressWarnings("unused")
    private static Log log = LogFactory.getLog(A1830RLN_RequestDataFrame.class);
    public byte kind;
    public byte meter_cmd;
    public byte before_txd_control;
    public int len;
    public int meter_length;
    public OCTET msg = null;
    public int cksofs = -1;
    public boolean write = false;    

    public int send_count;
    public int retry_flag;
    public TIMESTAMP time_of_transmission = new TIMESTAMP();
    
    private int length;
	// ctrl의 toggleBit : 패킷이 중복되지 않도록 전환 비트를 설정한다. 0x20 <-> 0x00
	private static byte toggleBit = 0x00;
    
	/**
	 * goggleBit의 경우 5 번째 비트를 설정한다. 0010 0000(0x20)<br>
	 * xor 비트 연산을 하여 토글 되는 효과를 볼 수 있다.
	 * 
	 * @return 0x00 or 0x20 값이 토글(switching) 된다.
	 */
	private static byte getToggleBit() {
		toggleBit = (byte) ((byte) toggleBit ^ 0x20);
		return toggleBit;
	}

	/**
	 * ANSI C12.22 Header 정의부분 (자세한 내용은 프로토콜 문서 참조)
	 * 
	 * @return length 를 제외한 Head부분
	 */
	private static byte[] getHeader() {
		byte[] rtnByte = new byte[4];
		rtnByte[0] = ANSI.STP;
		rtnByte[1] = ANSI.REV_OPTICAL;
		rtnByte[2] = getToggleBit();
		rtnByte[3] = 0x00;
		return rtnByte;
	}

	/**
	 * Int 형 변수를 Word16 형태로 변환하는 기능을 한다.<br>
	 * Word16은 16bit 형식의 byte형 변수라고 생각하면된다. Word16형식이 없기때문에 8bit Byte형 변수를
	 * 배열(size 2)형태로 리턴한다. Shift 하여 8bit씩 나누어 배열에 각각 저장한다. 8비트에 벗어난 비트들은 byte로
	 * 캐스팅하면서 자동으로 소멸한다.
	 * 
	 * @param number
	 *            변환할 int형 변수
	 * @return int형 값을 8비트씩 2개로 나누어 리턴한다.(Word16 은 16비트형 변수)
	 */
	private static byte[] intToWord16(int number) {
		byte[] rtnByte = new byte[2];
		rtnByte[0] = (byte) (number >> 8);
		rtnByte[1] = (byte) (number);
		return rtnByte;
	}

	/**
	 * char 타입을 Word16으로 변환.
	 * 
	 * @param c
	 *            type of char
	 * @return bytes
	 * @see intToWord16
	 */
	private static byte[] charToWord16(char c) {
		byte[] rtnByte = new byte[2];
		rtnByte[0] = (byte) (c >> 8);
		rtnByte[1] = (byte) (c);
		return rtnByte;
	}

	/**
	 * 헤더프레임과 데이터프레임, crc 프레임을 합친다.
	 * 
	 * @param dataFrame
	 * @return
	 */
	private static byte[] carryFrame(byte[] dataFrame) {

		ByteArray headerFrame = new ByteArray();

		// head
		headerFrame.append(getHeader());

		// length : 데이터 프레임의 길이를 설정한다.
		headerFrame.append(intToWord16(dataFrame.length));

		// 데이터 프레임을 추가한다.
		headerFrame.append(dataFrame);

		// 데이터 프레임 사이즈
		int frame_size = headerFrame.toByteArray().length;

		// crc
		char crc = ANSI.crc(frame_size, (byte) 0, headerFrame.toByteArray());
		headerFrame.append(charToWord16(crc));

		return headerFrame.toByteArray();
	}

	/**
	 * 데이터 프레임을 입력받아 해더에 싣고 버퍼를 리턴한다.
	 * 
	 * @param dataFrame
	 * @return
	 */
	private static IoBuffer getBuffer(byte[] dataFrame) {
		// 헤더프레임과 데이터프레임, crc 프레임을 합친다. |Heder|..data..|crc|
		byte[] allFrame = carryFrame(dataFrame);

		// 완성된 데이터와 헤더를 합쳐 프레임을 생성한다.
		IoBuffer buf = IoBuffer.allocate(allFrame.length);
		buf.put(allFrame);
		buf.flip();
		return buf;
	}
    
	/**
	 * ANSI Protocol Identification Service<br>
	 * 서비스에 필요한 프레임을 설정한다.
	 * 
	 * @return
	 * @see MX2_AMR_Communication_Specification.pdf:46p
	 */
	public static IoBuffer getIdent() {

		byte[] dataFrame = new byte[] { ANSI.IDENT };

		// 헤더프레임과 데이터프레임, crc 프레임을 합친다. |Heder|..data..|crc|
		byte[] allFrame = carryFrame(dataFrame);

		// 완성된 데이터와 헤더를 합쳐 프레임을 생성한다.
		IoBuffer buf = IoBuffer.allocate(allFrame.length);
		buf.put(allFrame);
		buf.flip();

		return buf;
	}
	
    

    /**
     * constructor
     */
    public A1830RLN_RequestDataFrame()
    {
    }
    
    public A1830RLN_RequestDataFrame(byte before_txd_control, byte kind, byte meter_cmd, int len, int meter_length)
    {
        this.kind = kind;
        this.meter_cmd = meter_cmd;
        this.len = len;
        this.meter_length = meter_length;
        this.before_txd_control = before_txd_control;
    }
    
    public A1830RLN_RequestDataFrame(byte before_txd_control, byte kind, byte meter_cmd, OCTET msg, int len, int meter_length)
    {
        this.kind = kind;
        this.meter_cmd = meter_cmd;
        this.msg = msg;
        this.len = len;
        this.meter_length = meter_length;
        this.before_txd_control = before_txd_control;
    }
    
    public A1830RLN_RequestDataFrame(byte before_txd_control, byte kind, byte meter_cmd, OCTET msg,
                                 int cksofs, int len, boolean write, int meter_length)
    {
        this.kind = kind;
        this.meter_cmd = meter_cmd;
        this.msg = msg;
        this.len = len;
        this.meter_length = meter_length;
        this.write = write;
        this.cksofs = cksofs;
        this.before_txd_control = before_txd_control;
    }
    
	/**
	 * read 에 필요한 프레임을 생성한다.
	 * 
	 * @param tableid
	 *            MX2 장비는 2 size(byte)이 다.
	 * @param index
	 * @param count
	 * @return
	 */
	public static IoBuffer getRead(char tableid, int index, int count) {
		// 데이터 프레임 부분에 들어갈 데이터들을 정의한다.
		ByteArray dataFrame = new ByteArray();
		
		char cIndex = (char)index;
		char cCount = (char)count;

		// Read 명령
		if (count > 0) {
			dataFrame.append((byte)(ANSI.READ+1)); //인덱스 지정시 명령어는 0x31
		} else {
			dataFrame.append(ANSI.READ);
		}

		// tableid
		dataFrame.append(DataUtil.get2ByteToChar(tableid));
		
		// count 가 0이 아닐경우 인덱스와 같이 설정해준다.
		if (count > 0) {
			dataFrame.append(DataUtil.get2ByteToChar(cIndex));
			dataFrame.append(DataUtil.get2ByteToChar(cCount));
		}

		return getBuffer(dataFrame.toByteArray());
	}
	

	@Override
	public byte[] encode() throws Exception {

        byte[] cmd = new byte[len];

        int i = 0;
        byte ctl_data;
        
        if(this.meter_cmd == ANSI.IDENT || this.meter_cmd == ANSI.LOGON){
            ctl_data = 0x00;
        }else{
            if(this.before_txd_control == 0x20) 
                ctl_data = 0x00;
            else 
                ctl_data = 0x20;
        }
        
        if(kind == 1) 
            cmd[i++] = ANSI.ACK;
        
        cmd[i++] = ANSI.STP;
        cmd[i++] = ANSI.REV_OPTICAL;
        cmd[i++] = ctl_data;    
        cmd[i++] = 0x00;
        cmd[i++] = (byte)(this.meter_length >> 8);
        cmd[i++] = (byte)(this.meter_length);
        cmd[i++] = this.meter_cmd;
        
        if(this.msg != null)
        {
            System.arraycopy(this.msg.getValue(),0,cmd,i,msg.getValue().length);
            i += msg.getValue().length;
            if(write){
                cmd[i++] = ANSI.chkSum(msg.getValue(),this.cksofs,msg.getValue().length-this.cksofs);
            }
        }

        char crc = ANSI.crc(len - (2 + kind),kind,cmd);
        cmd[i++] = (byte)(crc >> 8);
        cmd[i++] = (byte)(crc);
        
        this.before_txd_control = ctl_data;

        return cmd;
        
	}
	

    /**
     * get code
     *
     * @result code <code>byte</code> code
     */
    public BYTE getService()
    {
        return this.service;
    }

    /**
     * set code
     *
     * @param code <code>byte</code> code
     */
    public void setService(BYTE service)
    {
        this.service = service;
    }

    /**
     * get arg
     *
     * @result arg <code>OCTET</code> arg
     */
    public OCTET getDestination()
    {
        return this.destination;
    }

    /**
     * set arg
     *
     * @param arg <code>OCTET</code> arg
     */
    public void setDestination(OCTET destination)
    {
        this.destination = destination;
    }
    
    public void setControl(BYTE control)
    {
        this.control = control;
    }
	
    public IoBuffer getIoBuffer() throws Exception
    {
        byte[] b = encode();
        IoBuffer buf = IoBuffer.allocate(b.length);
        buf.put(b);
        buf.flip();
        return buf;
    }
	
    /**
     * get string
     */
    public String toString()
    {
        StringBuffer strbuf = new StringBuffer();
        strbuf.append(super.toString());
        strbuf.append("CLASS["+this.getClass().getName()+"]\n");
        strbuf.append("service : " + service + "\n");
        strbuf.append("destination: " + Hex.decode(destination.getValue()) + "\n");        
        strbuf.append("source : " + source + "\n");
        strbuf.append("control : " + control + "\n");
        strbuf.append("send_data_buffer: " + Hex.decode(send_data_buffer.getValue()) + "\n");
        strbuf.append("send_count : " + send_count + "\n");
        strbuf.append("retry_flag : " + retry_flag + "\n");
        strbuf.append("time_of_transmission : " + time_of_transmission + "\n");

        return strbuf.toString();
    }
	
	public byte getTXDControl(){
        return this.before_txd_control;
    }

}
