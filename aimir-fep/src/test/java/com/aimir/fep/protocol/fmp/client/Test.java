package com.aimir.fep.protocol.fmp.client;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Test
{
	
	
    public static void main(String[] args)
    {
    	byte[] a = new byte[]{0x01,0x02};
    	byte[] b = new byte[]{0x03,0x04};
    	
    	byte[] c = new byte[a.length+b.length];
    	
    	System.arraycopy(a, 0, c, 0, a.length);
    	System.arraycopy(b, 0, c, a.length, b.length);
    	
    	System.out.println(c);
	}
    
public static Double genPowerFactor(byte[] byteArray,int pos, int len) {
		
		byte[] bytes = new byte[len];
		System.arraycopy(byteArray, pos, bytes, 0, len);
		
		ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
    	
		return ((double) byteBuffer.getShort())*0.01;
	}
}
