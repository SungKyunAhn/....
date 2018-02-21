package com.aimir.fep.protocol.mrp.protocol;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.parser.MX2Table.NegotiateTable;
import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.protocol.mrp.command.frame.ReceiveDataFrame;
import com.aimir.fep.protocol.mrp.exception.MRPException;
import com.aimir.fep.util.ByteArray;

/**
 * @author JiHoon Kim, jihoon@nuritelecom.com
 *
 */
public class A3RLNQ_ReceiveDataFrame extends ReceiveDataFrame
{
    private static Log log = LogFactory.getLog(A3RLNQ_ReceiveDataFrame.class);
    @SuppressWarnings("unused")
    public int cnt;
    @SuppressWarnings("unused")
    private ArrayList<byte[]> list = new ArrayList<byte[]>();
	@SuppressWarnings("unused")
	private byte[] receiveBytes = null;

    /**
     * constructor
     */
    public A3RLNQ_ReceiveDataFrame()
    {
    }
    
    public A3RLNQ_ReceiveDataFrame(byte[] receiveBytes) {
		this.receiveBytes = receiveBytes;
	}
    
	/**
	 * Identification service 요청에 대한 응답 데이터를 Frame 별로 나누고<br>
	 * 해당 테이블에 매칭 시킨다.
	 * 
	 * @return Ident Response Frame
	 * @throws MRPException
	 */
	public byte[] getIdent() throws MRPException {

		// 전체 프레임에서 데이터 부분만 추출한다.
		byte bytes[] = this.receiveBytes;
		return bytes;
/*
		// 받은 데이터들을 담을 구조체
		IdentTable identTable = new IdentTable();

		// status 패킷 요청 상태, [isss]|[bsy]|[err]|[ok]
		identTable.setStatus(bytes[0]);

		// std : ANSI protocol 버전정보 0x20 = ANSI C12.21
		identTable.setStd(bytes[1]);

		// ver : 레퍼런스 표준 버전 0x01 = ANSI C12.21-1999
		identTable.setVer(bytes[2]);

		// rev : 레퍼런스 표준 버전 (소수점뒷자리) 0x00
		identTable.setRev(bytes[3]);

		// feature : 인증서비스 티켓 0x01
		identTable.setTicket(bytes[4]);

		// auth-type : 인증서비스 타입 0x00(not supported)
		identTable.setAuth_type(bytes[5]);

		// auth-alg-id : 인증방식 0x00(DES)
		identTable.setAuth_alg_id(bytes[6]);

		// end-of-list : 끝을 나타냄 0x00
		identTable.setEnd_of_list(bytes[7]);
*/
//		return identTable;
	}
    
	/**
	 * Negotiate service 요청에 대한 응답 데이터를 Frame 별로 나누고<br>
	 * 해당 테이블에 매칭 시킨다.
	 * 
	 * @return
	 */
	public NegotiateTable getNegotiate() {

		byte bytes[] = this.receiveBytes;

		NegotiateTable negotiateTable = new NegotiateTable();

		// status 패킷 요청 상태, [isss]|[bsy]|[err]|[ok]
		negotiateTable.setStatus(bytes[0]);

		// packet-size : 최대 패킷 사이즈
		byte[] packet_size = new byte[2];
		System.arraycopy(bytes, 1, packet_size, 0, 2);
		negotiateTable.setPacket_size(packet_size);

		// nbr-packets : 한번에 전송할 패킷 갯수
		negotiateTable.setNbr_packets(bytes[3]);

		// baud-rate : 전송 속도
		negotiateTable.setBaud_rate(bytes[4]);

		return negotiateTable;
	}

    public void append(byte[] b)
    {
        log.debug("append =>"+new OCTET(b).toHexString());
        list.add(b);
        this.cnt++;
    }

    public byte[] encode() throws Exception
    {
        ByteArray array = new ByteArray();
        Iterator it = list.iterator();
        int idx = 0;
        while(it.hasNext()){
            byte[] temp = (byte[])it.next();
            if(idx == 0){
                array.append(cutHeaderTail(temp,true)); 
            }else{
                array.append(cutHeaderTail(temp,false)); 
            }
  
            idx++;
        }
        return array.toByteArray();
    }
    
    protected byte[] cutHeaderTail(byte[] org, boolean isFirst )
    {
        log.debug("org =>"+new OCTET(org).toHexString());
        byte[] ret = null;
        if(isFirst){
            ret = new byte[org.length - (15+2)];
            System.arraycopy(org, 15, ret, 0, ret.length);
        }else{
            ret = new byte[org.length - (12+2)];
            System.arraycopy(org, 12, ret, 0, ret.length);
        }

        return ret;
    }
    
    /**
     * get string
     */
    public String toString()
    {
        StringBuffer strbuf = new StringBuffer();
        try
        {        
            strbuf.append("CLASS["+this.getClass().getName()+"]\n");
            strbuf.append("service : " + new OCTET(encode()).toHexString() + "\n");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return strbuf.toString();
    }
}
