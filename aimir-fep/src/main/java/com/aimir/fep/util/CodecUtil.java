package com.aimir.fep.util;

import java.util.ArrayList;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;

import com.aimir.fep.protocol.fmp.datatype.BYTE;
import com.aimir.fep.protocol.fmp.datatype.FMPNonFixedVariable;
import com.aimir.fep.protocol.fmp.datatype.FMPVariable;
import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.protocol.fmp.datatype.TIMESTAMP;
import com.aimir.fep.protocol.fmp.frame.service.ServiceData;
import com.aimir.fep.protocol.fmp.frame.service.entry.meterDataEntry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;

/**
 * Java Object encode/decode
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class CodecUtil
{
    public int align = 4;
    private static Log log = LogFactory.getLog(CodecUtil.class);


    /**
     * get Super Classes
     *
     * @param cls <code>Class</code> class
     */
    private static Class[] getSuperClasses(Class cls)
    {
        ArrayList al = new ArrayList();
        Class scls= null;
        while(cls != null)
        { 
            //log.debug("getSuperClasses : " + cls.getName());
            al.add(cls);
            cls = cls.getSuperclass();
            if(cls.getName().equals("Object")
                    || cls.getName().equals("java.lang.Object"))
                break;
        }
        return (Class[])al.toArray(new Class[0]);
    }

    /**
     * packing Object
     *
     * @param obj <code>Object</code> object
     * @return bytes <code>byte[]</code> byte array
     * @throws Exception
     */
    public static byte[] pack(Object obj) throws Exception
    {
        ByteArrayOutputStream bao =
            new ByteArrayOutputStream();
        Class cl = obj.getClass();
        Class[] classes = getSuperClasses(cl);
        byte[] clsbytes;
        for(int i = classes.length - 1; i >= 0 ; i--)
        {
            clsbytes = _pack(obj,classes[i]);
            bao.write(clsbytes,0,clsbytes.length);
        }

        return bao.toByteArray();
    }

    /**
     * packing Object
     *
     * @param obj <code>Object</code> object
     * @param cls <code>Class</code> class
     * @return bytes <code>byte[]</code> byte array
     */
    public static byte[] _pack(Object obj, Class cls) throws Exception
    {
        ByteArrayOutputStream bao =
            new ByteArrayOutputStream();
        //log.debug("pack(Object obj, Class "+cls.getName()+")");
        Field[] fl = cls.getDeclaredFields();
        Field f;
        for(int i = 0 ; i < fl.length ; i++)
        {
            f = fl[i];
            Class c = f.getType();
            Object val = null;
            int ival ;
            try {
                val = f.get(obj);
            }catch(IllegalAccessException e) {
                //e.printStackTrace();
            }

            //log.debug(f.getName()+" : "+ c.getName() +" : " + val);

            if(val instanceof Byte)
            {
                bao.write(((Byte)val).byteValue());
            }
            else if(val instanceof byte[])
            {
                byte[] bx = (byte[])val;
                if(bx != null)
                    bao.write(bx,0,bx.length);
            }
            else if(val instanceof FMPVariable )
            {
                byte[] bx = ((FMPVariable)val).encode();
                if(bx.length == 1)
                    bao.write(bx[0]);
                else if(bx.length > 1)
                    bao.write(bx,0,bx.length);
            }
            else if(val instanceof ServiceData )
            {
                byte[] bx = pack(val);
                bao.write(bx,0,bx.length);
            }
        }

        return bao.toByteArray();
    }

    /**
     * unpacking Object
     *
     * @param className <code>String</code> Class name
     * @param bytebuff <code>ByteBuffer</code> byte buffer
     * @return object <code>Object</code>
     * @throws Exception
     */
    public static Object unpack(String ns, String className,
            IoBuffer bytebuff) throws Exception
    {
        if (className.equals("com.aimir.fep.protocol.fmp.frame.service.entry.meterDataEntry")) {
            meterDataEntry entry = new meterDataEntry();
            byte[] bx = bytebuff.array();
            
            int pos = 0;
            byte[] b = new byte[entry.getMdID().getLen()];
            System.arraycopy(bx, pos, b, 0, b.length);
            pos += b.length;
            entry.setMdID(new OCTET(b));
            
            b = new byte[entry.getMdSerial().getLen()];
            System.arraycopy(bx, pos, b, 0, b.length);
            pos += b.length;
            entry.setMdSerial(new OCTET(b));
            
            b = new byte[1];
            System.arraycopy(bx, pos, b, 0, b.length);
            pos += b.length;
            entry.setMdType(new BYTE(b[0]));
            
            b = new byte[1];
            System.arraycopy(bx, pos, b, 0, b.length);
            pos += b.length;
            entry.setMdServiceType(new BYTE(b[0]));
            
            b = new byte[1];
            System.arraycopy(bx, pos, b, 0, b.length);
            pos += b.length;
            entry.setMdVendor(new BYTE(b[0]));
            
            b = new byte[2];
            System.arraycopy(bx, pos, b, 0, b.length);
            pos += b.length;
            DataUtil.convertEndian(b);
            entry.setDataCount(DataUtil.getIntTo2Byte(b));
            
            b = new byte[2];
            System.arraycopy(bx, pos, b, 0, b.length);
            pos += b.length;
            DataUtil.convertEndian(b);
            int len = DataUtil.getIntTo2Byte(b);
            
            b = new byte[entry.getMdTime().getLen()];
            System.arraycopy(bx, pos, b, 0, b.length);
            pos += b.length;
            entry.setMdTime(new TIMESTAMP(b));
            
            b = new byte[len - 7];
            System.arraycopy(bx, pos, b, 0, b.length);
            pos += b.length;
            entry.setMdData(new OCTET(b));
            
            return entry;
        }
        else {
            Class cl = null;
            try { cl = Class.forName(className);
            }catch(Exception e) {
                log.warn("unpack className["+className+"]");
                e.printStackTrace();
            }
    
            Object obj = null;
    
            try { obj = cl.newInstance();
            } catch(Exception ex) {
                log.warn("unpack className["+className+"]");
                ex.printStackTrace();
            }
    
            Class[] classes = getSuperClasses(cl);
    
            for(int i = classes.length - 1; i >= 0 ; i--) 
                _unpack(ns,classes[i],obj,bytebuff);
    
            return obj;
        }
    }

    /**
     * unpacking Object
     *
     * @param cl <code>Class</code> Class
     * @param obj <code>Object</code> Object
     * @param bytebuff <code>ByteBuffer</code> byte buffer
     * @throws Exception
     */
    private static void _unpack(String ns, Class cl, Object obj, 
            IoBuffer bytebuff) throws Exception
    {
        Field[] fl = cl.getDeclaredFields();
        Field f;
        byte[] btmp;

        for(int i = 0 ; i < fl.length ; i++)
        {
            f = fl[i];
            Class c = f.getType();
            Object val = null;
            int ival;
            long lval;

            try{ val = f.get(obj);
            }catch(Exception ex) { 
                //ex.printStackTrace(); 
            }
            //log.debug(f.getName()+" : "+ c.getName()+" : " + val);

            if(val instanceof Byte) {
                f.setByte(obj,bytebuff.get());
            } 
            else if(val instanceof FMPNonFixedVariable)
            {
                FMPNonFixedVariable afv = (FMPNonFixedVariable)val;
                afv.decode(ns,bytebuff);
            } 
            else if(val instanceof FMPVariable)
            {
                FMPVariable av = (FMPVariable)val;
                av.decode(ns,bytebuff,0);
            }
            /*
            else if(val instanceof FMPNonFixedVariable)
            {
                FMPNonFixedVariable afv = (FMPNonFixedVariable)val;
                boolean isFixed = afv.getIsFixed();
                int avflen = afv.getLen();
                FMPNonFixedVariable oval = (FMPNonFixedVariable)
                    afv.getFMPVariableObject(afv.getSyntax());
                oval.setLen(avflen);
                oval.setIsFixed(isFixed);
                //log.debug("val:"+val.getClass().getName());
                //log.debug("avflen :" + avflen);
                //log.debug("isFixed :" + isFixed);
                oval.decode(bytebuff);
                //log.debug("oval lenghth:" + oval.getLen());
                f.set(obj,oval);
            } else if(val instanceof FMPVariable)
            {
                FMPVariable av = (FMPVariable)val;
                FMPVariable oval = 
                    av.getFMPVariableObject(av.getSyntax());
                oval.decode(bytebuff,0);
                f.set(obj,oval);
            }
            */
            else if(val instanceof ServiceData) 
            {
                f.set(obj,unpack(ns,val.getClass().getName(),bytebuff));
            }
        }
    }

    /**
     * get size of object
     *
     * @param obj <code>Object</code> Object
     * @return size <code>int</code>
     * @throws Exception
     */
    public static int sizeOf(Object obj) throws Exception
    {
        int size = 0;
        Class cl = obj.getClass();
        Class[] classes = getSuperClasses(cl);
        byte[] clsbytes;
        for(int i = classes.length - 1; i >= 0 ; i--)
        {
            size += _sizeOf(obj,classes[i]);
        }

        return size;
    }

    /**
     * get size of object
     *
     * @param obj <code>Object</code> Object
     * @return cls <code>Class</code> Class
     * @return size <code>int</code>
     * @throws Exception
     */
    public static int _sizeOf(Object obj, Class cls) throws Exception
    {
        int size = 0;

        Field[] fl = cls.getDeclaredFields();
        Field f;
        for(int i = 0 ; i < fl.length ; i++)
        {
            f = fl[i];
            Class c = f.getType();
            Object val = null;
            int ival ;
            try {
                val = f.get(obj);
            }catch(IllegalAccessException e) {
                //e.printStackTrace();
            }

            if(val instanceof Byte)
            {
                size++;
            }
            else if(val instanceof byte[])
            {
                byte[] bx = (byte[])val;
                size+=bx.length;
            }
            else if(val instanceof FMPNonFixedVariable)
            {
                size+=((FMPNonFixedVariable)val).getLen();
            }
            else if(val instanceof FMPVariable)
            {
                byte[] bx = ((FMPVariable)val).encode();
                size+=bx.length;
            }
            /*
            else if(val instanceof WORD )
            {
                size+=2;
            }
            else if(val instanceof OCTET )
            {
                byte[] bx = ((OCTET)val).encode();
                size+=bx.length;
            }
            else if(val instanceof OPAQUE )
            {
                byte[] bx = ((OPAQUE)val).encode();
                size+=bx.length;
            }
            */
            else if(val instanceof ServiceData )
            {
                byte[] bx = pack(val);
                size+=bx.length;
            }
        }

        return size;
    }

    private static void printBytes(byte bytes)
    { 
        log.debug("" + bytes);
    }
    private static void printBytes(byte[] bytes)
    {
        for(int i = 0 ; i < bytes.length ; i++)
        {
            log.debug(bytes[i] + " ");
        }
    }

    private static void printBytes(IoBuffer buffer)
    {
        while(buffer.hasRemaining())
        {
            log.debug(buffer.get() + " ");
        }
        buffer.rewind();
    }

	/**
	 * C STRUCTURE in byte array => JAVA OBJECT
	 */
	public static Object unpack(String ns, String className, byte[] src) {

		log.debug("UNPACK 1: "+ className);

		if (className.equals("com.aimir.fep.protocol.fmp.frame.service.entry.meterDataEntry")) {
            meterDataEntry entry = new meterDataEntry();
            byte[] bx = src;
            
            int pos = 0;
            byte[] b = new byte[8];
            System.arraycopy(bx, pos, b, 0, b.length);
            pos += b.length;
            entry.setMdID(new OCTET(b));
            log.debug(Hex.decode(entry.getMdID().getValue()));
            
            b = new byte[20];
            System.arraycopy(bx, pos, b, 0, b.length);
            pos += b.length;
            entry.setMdSerial(new OCTET(b));
            log.debug(Hex.decode(entry.getMdSerial().getValue()));
            
            b = new byte[1];
            System.arraycopy(bx, pos, b, 0, b.length);
            pos += b.length;
            entry.setMdType(new BYTE(b[0]));
            
            b = new byte[1];
            System.arraycopy(bx, pos, b, 0, b.length);
            pos += b.length;
            entry.setMdServiceType(new BYTE(b[0]));
            
            b = new byte[1];
            System.arraycopy(bx, pos, b, 0, b.length);
            pos += b.length;
            entry.setMdVendor(new BYTE(b[0]));
            
            b = new byte[2];
            System.arraycopy(bx, pos, b, 0, b.length);
            pos += b.length;
            DataUtil.convertEndian(b);
            entry.setDataCount(DataUtil.getIntTo2Byte(b));
            
            b = new byte[2];
            System.arraycopy(bx, pos, b, 0, b.length);
            pos += b.length;
            DataUtil.convertEndian(b);
            int len = DataUtil.getIntTo2Byte(b);
            log.debug("LEN[" + len + "]");
            
            b = new byte[7];
            System.arraycopy(bx, pos, b, 0, b.length);
            pos += b.length;
            entry.setMdTime(new TIMESTAMP(b));
            
            b = new byte[len - 7];
            System.arraycopy(bx, pos, b, 0, b.length);
            pos += b.length;
            entry.setMdData(new OCTET(b));
            
            return entry;
        }
		else {
    		Class cl=null;
    		try{
    			cl=Class.forName(className);
    		} catch (ClassNotFoundException e) { 
                log.info(e.getMessage());
    		}
    
    		Object obj=null;
    		try{
    			obj=cl.newInstance();
    		} catch (InstantiationException e) {
                log.info(e.getMessage());
    		} catch (IllegalAccessException e) {
                log.info(e.getMessage());
    		} 
    
            try { _unpack(ns,cl,obj,src);
            }catch(Exception ex) { ex.printStackTrace(); return null;}
    
    
    		return obj;
		}
	}

	private static void _unpack(String ns, Class cl, Object obj, byte[] src) 
        throws Exception
    {
		Field[] fl=cl.getDeclaredFields();
		Field f;
		//int len, pad, pos;
		int len,pos;

		len=0;
		for (int i=0; i<fl.length; i++) {
			f=fl[i];
			Class c = f.getType();
			Object val=null;
			int ival;
			long lval;

			try{
				val=f.get(obj);
			}catch (IllegalAccessException e) { 
			}

			//pad=(align - (len%align))%align;

			if (val instanceof Integer) {

				//pos=len+pad;
				pos=len;

				ival = 
					((src[pos+3] & 0xff) << 0) +
					((src[pos+2] & 0xff) << 8) +
					((src[pos+1] & 0xff) << 16) +
					((src[pos+0] & 0xff) << 24);

				try {
					f.setInt(obj,ival);	
				} catch (IllegalAccessException e) { 
				}
				len=pos+4;
			} else if(val instanceof Float) {
				pos=len;

				ival = 
					((src[pos+3] & 0xff) << 0) +
					((src[pos+2] & 0xff) << 8) +
					((src[pos+1] & 0xff) << 16) +
					((src[pos+0] & 0xff) << 24);
				
				try {
					f.setFloat(obj, Float.intBitsToFloat(ival));
				} catch (IllegalAccessException e) { }

				len=pos+4;
			} else if(val instanceof Double) {
				//pos=len+pad;
				pos=len;

				lval = 
					(((long)src[pos+7] & 0xff) << 0) +
					(((long)src[pos+6] & 0xff) << 8) +
					(((long)src[pos+5] & 0xff) << 16) +
					(((long)src[pos+4] & 0xff) << 24) +
					(((long)src[pos+3] & 0xff) << 32) +
					(((long)src[pos+2] & 0xff) << 40) +
					(((long)src[pos+1] & 0xff) << 48) +
					(((long)src[pos+0] & 0xff) << 56);

				try {
		       		f.setDouble(obj, Double.longBitsToDouble(lval));
				} catch (IllegalAccessException e) { }

				len=pos+8;
			} else if(val instanceof Byte) {
				pos=len;

				try {
		       		f.setByte(obj, src[pos]);
				} catch (IllegalAccessException e) { }

				len=pos+1;
			} else if(val instanceof byte[]) {
				pos=len;

				byte[] bx=(byte[])val;
				for (int ix=0; ix<bx.length; ix++){
					bx[ix]=src[pos+ix];
				}
				len=pos+bx.length;
            } else if(val instanceof FMPNonFixedVariable)
            {
                pos=len;
                FMPNonFixedVariable afv = (FMPNonFixedVariable)val;
                len+=afv.decode(ns,src,pos);
            } else if(val instanceof FMPVariable)
            {
                pos=len;
                FMPVariable av = (FMPVariable)val;
                len+=av.decode(ns,src,pos);
			} else {
				// error
			}
		}
	}

	/**
	 * C STRUCTURE in byte array => JAVA OBJECT
	 */
	public static Object unpack(String ns, String className, byte[] src,
            int position) {

		log.debug("UNPACK 2: "+ className);
		log.debug("DATA[" + Hex.getHexDump(src) + "] POS[" + position + "]");
		
		if (className.equals("com.aimir.fep.protocol.fmp.frame.service.entry.meterDataEntry")) {
            meterDataEntry entry = new meterDataEntry();
            byte[] bx = src;
            
            byte[] b = new byte[entry.getMdID().getLen()];
            System.arraycopy(bx, position, b, 0, b.length);
            position += b.length;
            entry.setMdID(new OCTET(b));
            
            b = new byte[entry.getMdSerial().getLen()];
            System.arraycopy(bx, position, b, 0, b.length);
            position += b.length;
            entry.setMdSerial(new OCTET(b));
            
            b = new byte[1];
            System.arraycopy(bx, position, b, 0, b.length);
            position += b.length;
            entry.setMdType(new BYTE(b[0]));
            
            b = new byte[1];
            System.arraycopy(bx, position, b, 0, b.length);
            position += b.length;
            entry.setMdServiceType(new BYTE(b[0]));
            
            b = new byte[1];
            System.arraycopy(bx, position, b, 0, b.length);
            position += b.length;
            entry.setMdVendor(new BYTE(b[0]));
            
            b = new byte[2];
            System.arraycopy(bx, position, b, 0, b.length);
            position += b.length;
            DataUtil.convertEndian(b);
            entry.setDataCount(DataUtil.getIntTo2Byte(b));
            
            b = new byte[2];
            System.arraycopy(bx, position, b, 0, b.length);
            position += b.length;
            DataUtil.convertEndian(b);
            int len = DataUtil.getIntTo2Byte(b);
            
            b = new byte[entry.getMdTime().getLen()];
            System.arraycopy(bx, position, b, 0, b.length);
            position += b.length;
            entry.setMdTime(new TIMESTAMP(b));
            
            b = new byte[len - 7];
            System.arraycopy(bx, position, b, 0, b.length);
            position += b.length;
            entry.setMdData(new OCTET(b));
            
            return entry;
        }
		else {
    		Class cl=null;
    		try{
    			cl=Class.forName(className);
    		} catch (ClassNotFoundException e) { 
    			log.warn(e.toString());
                return null;
    		}
    
    		Object obj=null;
    		try{
    			obj=cl.newInstance();
    		} catch (InstantiationException e) { 
                log.warn(e.toString());
                return null;
    		} catch (IllegalAccessException e) {
                log.warn(e.toString());
                return null;
    		}
    
            try {
    		    _unpack(ns,cl,obj, src, position);
            }catch(Exception ex)
            {
                log.error(ex);
                obj = null;
            }
    		return obj;
		}
	}

	private static void _unpack(String ns, Class cl, Object obj, byte[] src,
            int position) throws Exception 
    {
		Field[] fl=cl.getDeclaredFields();
		Field f;
		int len=0,pos=0;

		len=position;
		for (int i=0; i<fl.length; i++) {
		    log.debug("SRCLEN[" + src.length + "] POS[" + pos + "] LEN[" + len + "]");
            if(src.length <= len)
                return;
			f=fl[i];
			Class c = f.getType();
			Object val=null;
			int ival;
			long lval;

			try{
				val=f.get(obj);
                log.debug("Field Type[" + val.getClass().getName()+"]");
			}catch (IllegalAccessException e) { 
				log.warn(e,e);
			}

			if (val instanceof Integer) {
				pos=len;

				ival = 
					((src[pos+3] & 0xff) << 0) +
					((src[pos+2] & 0xff) << 8) +
					((src[pos+1] & 0xff) << 16) +
					((src[pos+0] & 0xff) << 24);

				try {
					f.setInt(obj,ival);	
				} catch (IllegalAccessException e) {}
				len=pos+4;
			} else if(val instanceof Float) {
				pos=len;

				ival = 
					((src[pos+3] & 0xff) << 0) +
					((src[pos+2] & 0xff) << 8) +
					((src[pos+1] & 0xff) << 16) +
					((src[pos+0] & 0xff) << 24);
				
				try {
					f.setFloat(obj, Float.intBitsToFloat(ival));
				} catch (IllegalAccessException e) { }

				len=pos+4;
			} else if(val instanceof Double) {
				pos=len;

				lval = 
					(((long)src[pos+7] & 0xff) << 0) +
					(((long)src[pos+6] & 0xff) << 8) +
					(((long)src[pos+5] & 0xff) << 16) +
					(((long)src[pos+4] & 0xff) << 24) +
					(((long)src[pos+3] & 0xff) << 32) +
					(((long)src[pos+2] & 0xff) << 40) +
					(((long)src[pos+1] & 0xff) << 48) +
					(((long)src[pos+0] & 0xff) << 56);

				try {
		       		f.setDouble(obj, Double.longBitsToDouble(lval));
				} catch (IllegalAccessException e) { }

				len=pos+8;
			} else if(val instanceof Byte) {
				pos=len;

				try {
		       		f.setByte(obj, src[pos]);
				} catch (IllegalAccessException e) { }

				len=pos+1;
			} else if(val instanceof byte[]) {
				pos=len;

				byte[] bx=(byte[])val;
				for (int ix=0; ix<bx.length; ix++){
					bx[ix]=src[pos+ix];
				}
				len=pos+bx.length;
            } else if(val instanceof FMPNonFixedVariable)
            {
                pos=len;
                FMPNonFixedVariable afv = (FMPNonFixedVariable)val;
                len+=afv.decode(ns,src,pos);
            } else if(val instanceof FMPVariable)
            {
                pos=len;
                FMPVariable av = (FMPVariable)val;
                len+=av.decode(ns,src,pos);
			} else {
				// error
				log.warn("_unpack : Unknown Data Type ");
			}
		}
	}
}

