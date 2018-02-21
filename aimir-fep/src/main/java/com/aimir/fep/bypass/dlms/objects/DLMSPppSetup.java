//
// --------------------------------------------------------------------------
//  Gurux Ltd
// 
//
//
// Filename:        $HeadURL$
//
// Version:         $Revision$,
//                  $Date$
//                  $Author$
//
// Copyright (c) Gurux Ltd
//
//---------------------------------------------------------------------------
//
//  DESCRIPTION
//
// This file is a part of Gurux Device Framework.
//
// Gurux Device Framework is Open Source software; you can redistribute it
// and/or modify it under the terms of the GNU General Public License 
// as published by the Free Software Foundation; version 2 of the License.
// Gurux Device Framework is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of 
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
// See the GNU General Public License for more details.
//
// More information of Gurux products: http://www.gurux.org
//
// This code is licensed under the GNU General Public License v2. 
// Full text may be retrieved at http://www.gnu.org/licenses/gpl-2.0.txt
//---------------------------------------------------------------------------

package com.aimir.fep.bypass.dlms.objects;

import com.aimir.fep.bypass.dlms.DLMSClient;
import com.aimir.fep.bypass.dlms.enums.DataType;
import com.aimir.fep.bypass.dlms.enums.ObjectType;
import com.aimir.fep.bypass.dlms.internal.Common;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DLMSPppSetup extends DLMSObject implements DLMSBase
{
    private DLMSPppSetupIPCPOption[] IPCPOptions;
    private String PHYReference;
    private DLMSPppSetupLcpOption[] LCPOptions;
    private byte[] UserName;
    private byte[] Password;

    private PppAuthenticationType m_Authentication;
    
    public final PppAuthenticationType getAuthentication()
    {
        return m_Authentication;
    }
    public final void setAuthentication(PppAuthenticationType value)
    {
        m_Authentication = value;
    }    
    
    /** 
    PPP authentication procedure user name.
   */    
    public final byte[] getUserName()
    {
        return UserName;
    }

    public final void setUserName(byte[] value)
    {
        UserName = value;
    }

    /** 
     PPP authentication procedure password.
    */

    public final byte[] getPassword()
    {
        return Password;
    }
    public final void setPassword(byte[] value)
    {
        Password = value;
    }
    
    /**  
     Constructor.
    */
    public DLMSPppSetup()
    {
        super(ObjectType.PPP_SETUP);
    }

    /**  
     Constructor.

     @param ln Logican Name of the object.
    */
    public DLMSPppSetup(String ln)
    {
        super(ObjectType.PPP_SETUP, ln, 0);
    }

    /**  
     Constructor.

     @param ln Logican Name of the object.
     @param sn Short Name of the object.
    */
    public DLMSPppSetup(String ln, int sn)
    {
        super(ObjectType.PPP_SETUP, ln, sn);
    }

    public final String getPHYReference()
    {
        return PHYReference;
    }
    public final void setPHYReference(String value)
    {
        PHYReference = value;
    }

    public final DLMSPppSetupLcpOption[] getLCPOptions()
    {
        return LCPOptions;
    }
    public final void setLCPOptions(DLMSPppSetupLcpOption[] value)
    {
        LCPOptions = value;
    }

    public final DLMSPppSetupIPCPOption[] getIPCPOptions()
    {
        return IPCPOptions;
    }
    public final void setIPCPOptions(DLMSPppSetupIPCPOption[] value)
    {
        IPCPOptions = value;
    }   

    @Override
    public Object[] getValues()
    {
        String str = "";
        if (UserName != null)
        {
            str = new String(UserName);
        }
        if (Password != null)
        {
            str += " " + new String(Password);
        }
        return new Object[] {getLogicalName(), getPHYReference(), getLCPOptions(), getIPCPOptions(), str};
    }
    
    /*
     * Returns collection of attributes to read.
     * 
     * If attribute is static and already read or device is returned HW error it is not returned.
     */
    @Override
    public int[] getAttributeIndexToRead()
    {
        java.util.ArrayList<Integer> attributes = new java.util.ArrayList<Integer>();
        //LN is static and read only once.
        if (LogicalName == null || LogicalName.compareTo("") == 0)
        {
            attributes.add(1);
        }   
        //PHYReference
        if (!isRead(2))
        {
            attributes.add(2);
        }
        //LCPOptions
        if (!isRead(3))
        {
            attributes.add(3);
        }
        //IPCPOptions
        if (!isRead(4))
        {
            attributes.add(4);
        }
        //PPPAuthentication
        if (!isRead(5))
        {
            attributes.add(5);
        }   
        return toIntArray(attributes);
    }
    
    /*
     * Returns amount of attributes.
     */  
    @Override
    public int getAttributeCount()
    {
        return 5;
    }
    
    /*
     * Returns amount of methods.
     */ 
    @Override
    public int getMethodCount()
    {
        return 0;
    }    
    
    @Override
    public DataType getDataType(int index)
    {
        if (index == 1)
        {
            return DataType.OCTET_STRING;
        }
        if (index == 2)
        {
            return DataType.OCTET_STRING;
        }
        if (index == 3)
        {
            return DataType.ARRAY;
        }
        if (index == 4)
        {
            return DataType.ARRAY;
        }
        if (index == 5)
        {
            return DataType.STRUCTURE;
        } 
        throw new IllegalArgumentException("getDataType failed. Invalid attribute index.");
    }
     
    /*
     * Returns value of given attribute.
     */    
    @Override
    public Object getValue(int index, int selector, Object parameters)
    {
        if (index == 1)
        {
            return getLogicalName();
        }        
        if (index == 2)
        {
            return PHYReference;
        }
        if (index == 3)
        {
            ByteArrayOutputStream data = new ByteArrayOutputStream();            
            data.write((byte)DataType.ARRAY.getValue());
            if (LCPOptions == null)
            {
                data.write(0);
            }
            else
            {
                data.write((byte)LCPOptions.length);
                try 
                {               
                    for (DLMSPppSetupLcpOption it : LCPOptions)
                    {
                        data.write((byte)DataType.STRUCTURE.getValue());
                        data.write((byte)3);
                        Common.setData(data, DataType.UINT8, it.getType());                    
                        Common.setData(data, DataType.UINT8, it.getLength());
                        Common.setData(data, Common.getValueType(it.getData()), it.getData());
                    }
                } 
                catch (Exception ex) 
                {                        
                    throw new RuntimeException(ex.getMessage());
                }
                
            }
            return data.toByteArray();
        }
        if (index == 4)
        {
            ByteArrayOutputStream data = new ByteArrayOutputStream();
            data.write((byte)DataType.ARRAY.getValue());
            if (IPCPOptions == null)
            {
                data.write(0);
            }
            else
            {
                data.write((byte)IPCPOptions.length);
                try
                {
                    for (DLMSPppSetupIPCPOption it : IPCPOptions)
                    {
                        data.write((byte)DataType.STRUCTURE.getValue());
                        data.write((byte)3);
                        Common.setData(data, DataType.UINT8, it.getType());
                        Common.setData(data, DataType.UINT8, it.getLength());
                        Common.setData(data, Common.getValueType(it.getData()), it.getData());
                    }
                } 
                catch (Exception ex) 
                {                        
                    throw new RuntimeException(ex.getMessage());
                }
            }
            return data.toByteArray();
        }
        else if (index == 5)
        {
            ByteArrayOutputStream data = new ByteArrayOutputStream();
            data.write((byte)DataType.STRUCTURE.getValue());
            data.write(2);
            try
            {
                Common.setData(data, DataType.OCTET_STRING, UserName);
                Common.setData(data, DataType.OCTET_STRING, Password);
            } 
            catch (Exception ex) 
            {                        
                throw new RuntimeException(ex.getMessage());
            }
            return data.toByteArray();
        }
        throw new IllegalArgumentException("GetValue failed. Invalid attribute index.");
    }
    
    /*
     * Set value of given attribute.
     */
    @Override
    public void setValue(int index, Object value)
    {
        if (index == 1)
        {
            super.setValue(index, value);            
        } 
        else if (index == 2)
        {
            if (value instanceof String)
            {
                PHYReference = value.toString();
            }
            else
            {
                PHYReference = DLMSClient.changeType((byte[])value, DataType.OCTET_STRING).toString();
            }
        }
        else if (index == 3)
        {
            java.util.ArrayList<DLMSPppSetupLcpOption> items = new java.util.ArrayList<DLMSPppSetupLcpOption>();
            if (value instanceof Object[])
            {
                for (Object item : (Object[])value)
                {
                    DLMSPppSetupLcpOption it = new DLMSPppSetupLcpOption();
                    it.setType(DLMSPppSetupLcpOptionType.forValue((Integer) Array.get(item, 0)));
                    it.setLength((Integer)Array.get(item, 1));
                    it.setData(Array.get(item, 2));
                    items.add(it);
                }
            }
            LCPOptions = items.toArray(new DLMSPppSetupLcpOption[items.size()]);
        }
        else if (index == 4)
        {
            java.util.ArrayList<DLMSPppSetupIPCPOption> items = new java.util.ArrayList<DLMSPppSetupIPCPOption>();
            if (value instanceof Object[])
            {
                for (Object item : (Object[])value)
                {
                    DLMSPppSetupIPCPOption it = new DLMSPppSetupIPCPOption();
                    it.setType(DLMSPppSetupIPCPOptionType.forValue((Integer)Array.get(item, 0)));
                    it.setLength((Integer)Array.get(item, 1));
                    it.setData(Array.get(item, 2));
                    items.add(it);
                }
            }
            IPCPOptions = items.toArray(new DLMSPppSetupIPCPOption[items.size()]);
        }
        else if (index == 5)
        {
            UserName = (byte[])((Object[])value)[0];
            Password = (byte[])((Object[])value)[1];
        }
        else
        {
            throw new IllegalArgumentException("GetValue failed. Invalid attribute index.");
        }
    }
}