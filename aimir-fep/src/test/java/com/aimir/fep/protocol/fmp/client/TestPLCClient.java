/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.aimir.fep.protocol.fmp.client;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.util.ArrayList;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.fep.meter.parser.plc.FDataRequest;
import com.aimir.fep.meter.parser.plc.PLCDataConstants;
import com.aimir.fep.protocol.fmp.client.PLCClientProtocolProvider;
import com.aimir.fep.protocol.fmp.datatype.FMPVariable;
import com.aimir.fep.protocol.fmp.frame.service.ServiceData;
import com.aimir.fep.util.FrameUtil;
import com.aimir.fep.util.Hex;

/**
 * A {@link ProtocolDecoder} which decodes a text line into a string.
 *
 * @author The Apache MINA Project (dev@mina.apache.org)
 * @version $Rev: 683500 $, $Date: 2008-08-07 06:02:01 +0200 (Thu, 07 Aug 2008) $,
 */
public class TestPLCClient {

	private final static Logger log = LoggerFactory.getLogger(TestPLCClient.class);
	private static final String name = "Test";
	private static IoSession session = null;
	private static IoConnector connector = null;

	public static void main3(String args[]) {
		//byte[] bx= Hex.encode("01114443550000000000000053455256455200000000180046010000000000000000000000000000000000000000");
		byte[] bx= Hex.encode("0301534552564552000000004443550000000000000041006601313233343536373839303132333435363738390001000000020000000100090706010B2502090706010B25020300000004000000050000000600000006");
		log.debug("CRC : "+Hex.decode(FrameUtil.getCRC(bx, 0, bx.length)));

	}

	public static void main2(String args[]) {
		File emptyFile = new File("kaze.txt");
		try {
			emptyFile.createNewFile();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {
		PLCClientProtocolProvider provider = null;
		if (args.length != 2) {
			System.out.println(TestPLCClient.class.getName() + " <hostname> <port>");
			return;
		}

		// Create TCP/IP connector.
		connector = new NioSocketConnector();

		// Set connect timeout.
		connector.setConnectTimeoutMillis(30 * 1000L);

		// Start communication.
		connector = new NioSocketConnector();
		provider = new PLCClientProtocolProvider();
		connector.getFilterChain().addLast(name, new ProtocolCodecFilter(provider.getCodecFactory()));
		connector.setHandler(provider.getHandler());
		ConnectFuture cf = null;
		for (;;) {
			try {
				cf = connector.connect(new InetSocketAddress(args[0], Integer.parseInt(args[1])));
				cf.awaitUninterruptibly();

				if (!cf.isConnected()) {
					throw new Exception("not yet");
				}

				session = cf.getSession();
				log.debug("SESSION CONNECTED[" + session.isConnected() + "]");

				break;
			}
			catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}

		// Wait for the connection attempt to be finished.
		cf.awaitUninterruptibly();
		cf.getSession().getCloseFuture().awaitUninterruptibly();
		IoSession session = cf.getSession();

		FDataRequest fDataRequest = new FDataRequest(PLCDataConstants.SOF, 
		        PLCDataConstants.PROTOCOL_DIRECTION_FEP_IRM,
		        PLCDataConstants.PROTOCOL_VERSION_1_1,
		        "DCU",
		        "SERVER",
		        PLCDataConstants.FDATAREQUEST_LENGTH_VALUE,
		        PLCDataConstants.DTYPE_METERING_CURR,"");
		session.write(fDataRequest);

		// connector.dispose();
		//int n = -1;
		String in = null;
		// 명령 프롬프스 상태에서 문자를 입력받기 위해서 아래와 같이 사용합니다.
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		while (true) {
			try {
				in = br.readLine();
				if (in.equals("quit")) {
					cf.getSession().close(true);
				}
				else {
					session.write(in);
				}
			}
			catch (IOException e) {
				continue;
			}
		}
	}

	public static void main1(String args[]) {
		try {
			FDataRequest fDataRequest = new FDataRequest(PLCDataConstants.SOF,
			        PLCDataConstants.PROTOCOL_DIRECTION_FEP_IRM, 
			        PLCDataConstants.PROTOCOL_VERSION_1_1, 
			        "DCU", "SERVER", 
			        PLCDataConstants.FDATAREQUEST_LENGTH_VALUE,
			        PLCDataConstants.DTYPE_METERING_CURR,"");
			byte[] testByte = fDataRequest.encode();

			log.debug("Encode : "+Hex.decode(testByte));
			/*
			IoBuffer = IoBuffer.allocate(testByte.length);

			log.debug(fDataRequest.decode());
			*/
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static byte[] toBytes(Object object) {
		java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
		try {
			java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(baos);
			oos.writeObject(object);
		}
		catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return baos.toByteArray();
	}

    /**
     * packing Object
     *
     * @param obj <code>Object</code> object
     * @return bytes <code>byte[]</code> byte array
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
	public static byte[] pack(Object obj) throws Exception
    {
        ByteArrayOutputStream bao =
            new ByteArrayOutputStream();
        Class<?> cl = obj.getClass();
        Class[] classes = getSuperClasses(cl);
        byte[] clsbytes;
        for(int i = classes.length - 1; i >= 0 ; i--)
        {
        	log.debug("classname : "+classes[i]);
            clsbytes = _pack(obj,classes[i]);
            bao.write(clsbytes,0,clsbytes.length);
        }

        return bao.toByteArray();
    }

    /**
     * get Super Classes
     *
     * @param cls <code>Class</code> class
     */
    @SuppressWarnings("unchecked")
	private static Class[] getSuperClasses(Class<?> cls)
    {
        ArrayList<Class<?>> al = new ArrayList<Class<?>>();

        while(cls != null)
        {
            log.debug("getSuperClasses : " + cls.getName());
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
     * @param cls <code>Class</code> class
     * @return bytes <code>byte[]</code> byte array
     */
    public static byte[] _pack(Object obj, Class<?> cls) throws Exception
    {
        ByteArrayOutputStream bao =
            new ByteArrayOutputStream();
        log.debug("pack(Object obj, Class "+cls.getName()+")");
        Field[] fl = cls.getDeclaredFields();
        Field f;
        for(int i = 0 ; i < fl.length ; i++)
        {
            f = fl[i];
            Class<?> c = f.getType();
            Object val = null;

            try {
                val = f.get(obj);
            }catch(IllegalAccessException e) {
                //e.printStackTrace();
            }

            log.debug(f.getName()+" : "+ c.getName() +" : " + val);

            if(val instanceof Byte)
            {
                bao.write(((Byte)val).byteValue());
            }
            else if(val instanceof byte[])
            {
                byte[] bx = (byte[])val;
                log.debug("Hex : "+Hex.decode(bx));
                if(bx != null)
                    bao.write(bx,0,bx.length);
            }
            else if(val instanceof FMPVariable )
            {
                byte[] bx = ((FMPVariable)val).encode();
                log.debug("Hex : "+Hex.decode(bx));
                if(bx.length == 1)
                    bao.write(bx[0]);
                else if(bx.length > 1)
                    bao.write(bx,0,bx.length);
            }
            else if(val instanceof ServiceData )
            {
                byte[] bx = pack(val);
                log.debug("Hex : "+Hex.decode(bx));
                bao.write(bx,0,bx.length);
            }
        }

        return bao.toByteArray();
    }
}