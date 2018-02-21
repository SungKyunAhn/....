package com.aimir.fep.util;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Iterator;

/**
 * Byte Array Utility
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class ByteArray implements Cloneable, Serializable {
    protected LinkedList<byte[]> list;
    protected int length;
    protected boolean flat;

    /**
     * constructor
     */
    public ByteArray() 
    { 
        list = new LinkedList<byte[]>();
        length = 0;
        flat = true; 
    }

    /**
     * constructor
     *
     * @param array <code>byte[]</code> byte array
     */
    public ByteArray(byte[] array) 
    { 
        list = new LinkedList<byte[]>(); 
        length = 1; 
        flat = true; 
        list.add(array); 
    }
   
    /**
     * clone
     *
     * @return object <code>Object</code> clone object
     */
    public Object clone() 
    { 
        return new ByteArray(toByteArray()); 
    }

    /**
     * get byte array
     *
     * @return bytes <code>byte[]</code>
     */
    public byte[] toByteArray() 
    { 
        flatten(); 
        if (length == 0) return new byte[0]; 
        return ((byte[])(list.getFirst())); 
    }
                                
    /**
     * flatten
     */
    public void flatten() { 
        if (flat) return; 
        byte[] newBuf = new byte[length]; 
        int p = 0; 
        for (Iterator<byte[]> i = list.iterator(); i.hasNext(); ) 
        { 
            byte[] bi = (byte[])(i.next()); 
            System.arraycopy(bi, 0, newBuf, p, bi.length); 
            p+=bi.length;
        } 
        list.clear(); 
        list.add(newBuf); 
        flat = true; 
    }
                                        
    /**
     * append bytes
     *
     * @param buf <code>byte[]</code> byte array
     */
    public void append(byte[] buf) 
    { 
        list.add(buf); 
        length += buf.length; 
        flat = flat && (length == 1); 
    }
    
    /**
     * append bytes
     *
     * @param buf <code>byte[]</code> byte array
     */
    public void append(byte b) 
    { 
        list.add(new byte[]{b}); 
        length += 1; 
        flat = flat && (length == 1); 
    }

    /**
     * append bytes
     *
     * @param buf <code>byte[]</code> byte array
     * @param bufStart <code>int</code> start offset
     * @param bufLen <code>int</code> length
     */
    public void append(byte[] buf, int bufStart, int bufLen) 
    { 
        byte[] slice = new byte[bufLen]; 
        System.arraycopy(buf, bufStart, slice, 0, bufLen); 
        append(slice); 
    }
}
