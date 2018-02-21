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
import com.aimir.fep.bypass.dlms.Command;
import com.aimir.fep.bypass.dlms.DLMSClient;
import com.aimir.fep.bypass.dlms.DLMSServerBase;
import com.aimir.fep.bypass.dlms.enums.AccessMode;
import com.aimir.fep.bypass.dlms.enums.DataType;
import com.aimir.fep.bypass.dlms.enums.MethodAccessMode;
import com.aimir.fep.bypass.dlms.enums.ObjectType;
import com.aimir.fep.bypass.dlms.internal.Common;
import com.aimir.fep.bypass.dlms.manufacturersettings.DLMSAuthentication;
import com.aimir.fep.bypass.dlms.manufacturersettings.DLMSAttributeSettings;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DLMSAssociationShortName extends DLMSObject implements DLMSBase
{
    Object m_AccessRightsList;
    DLMSObjectCollection m_ObjectList;
    String m_SecuritySetupReference;

    /**  
     Constructor.
    */
    public DLMSAssociationShortName()
    {
        this("0.0.40.0.0.255", 0xFA00);
    }

    /**  
     Constructor.

     @param ln Logican Name of the object.
     @param sn Short Name of the object.
    */
    public DLMSAssociationShortName(String ln, int sn)
    {
        super(ObjectType.ASSOCIATION_SHORT_NAME, ln, sn);
        m_ObjectList = new DLMSObjectCollection(this);        
    }

    public final DLMSObjectCollection getObjectList()
    {
        return m_ObjectList;
    }    

    public final Object getAccessRightsList()
    {
        return m_AccessRightsList;
    }
    public final void setAccessRightsList(Object value)
    {
        m_AccessRightsList = value;
    }

    public final String getSecuritySetupReference()
    {
        return m_SecuritySetupReference;
    }
    public final void setSecuritySetupReference(String value)
    {
        m_SecuritySetupReference = value;
    }

    @Override
    public Object[] getValues()
    {
        return new Object[] {getLogicalName(), getObjectList(), getAccessRightsList(), getSecuritySetupReference()};
    }
        
    /*
    * Invokes method.
    * 
     @param index Method index.
    */
    @Override
    public byte[][] invoke(Object sender, int index, Object parameters)
    {
        //Check reply_to_HLS_authentication
        if (index == 8)
        {
            DLMSServerBase s = (DLMSServerBase) sender;
            if (s == null)
            {
                throw new IllegalArgumentException("sender");
            }
            //Get server Challenge.
            ByteArrayOutputStream challenge = new ByteArrayOutputStream();
            ByteArrayOutputStream CtoS = new ByteArrayOutputStream();
            //Find shared secret
            for(DLMSAuthentication it : s.getAuthentications())
            {
                if (it.getType() == s.getAuthentication())
                {
                    try 
                    {
                        CtoS.write(it.getPassword());
                        challenge.write(it.getPassword());
                        challenge.write(s.getStoCChallenge());
                    }
                    catch (IOException ex) 
                    {
                        throw new RuntimeException(ex.getMessage());
                    }
                    break;
                }
            }            
            byte[] serverChallenge = DLMSServerBase.chipher(s.getAuthentication(), 
                    challenge.toByteArray());
            byte[] clientChallenge = (byte[])parameters;
            int[] pos = new int[1];
            if (Common.compare(clientChallenge, pos, serverChallenge))
            {
                try 
                {
                    CtoS.write(s.getCtoSChallenge());                    
                } 
                catch (IOException ex) 
                {
                    throw new RuntimeException(ex.getMessage());
                }
                return s.acknowledge(Command.WriteResponse, 0, 
                        DLMSServerBase.chipher(s.getAuthentication(), CtoS.toByteArray()), 
                        DataType.OCTET_STRING);
            }
            else
            {
                //Return error.
                return s.serverReportError(Command.MethodRequest, 5);
            }            
        }
        else
        {
            throw new IllegalArgumentException("Invoke failed. Invalid attribute index.");
        }
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
        //ObjectList is static and read only once.
        if (!isRead(2))
        {
            attributes.add(2);
        }
        //AccessRightsList is static and read only once.
        if (!isRead(3))
        {
            attributes.add(3);
        }
        //SecuritySetupReference is static and read only once.
        if (!isRead(4))
        {
            attributes.add(4);
        }
        return toIntArray(attributes);
    }
    
    @Override
    public int getAttributeCount()
    {
        return 4;
    }
    
    /*
     * Returns amount of methods.
     */ 
    @Override
    public int getMethodCount()
    {
        return 8;
    }  
    
    private void getAccessRights(DLMSObject item, ByteArrayOutputStream data) 
            throws RuntimeException, UnsupportedEncodingException, ParseException, 
            IOException            
    {        
        data.write((byte)DataType.STRUCTURE.getValue());
        data.write((byte) 3);
        Common.setData(data, DataType.UINT16, item.getShortName());
        data.write((byte) DataType.ARRAY.getValue());
        data.write((byte) item.getAttributes().size());
        for (DLMSAttributeSettings att : item.getAttributes())
        {
            data.write((byte)DataType.STRUCTURE.getValue()); //attribute_access_item
            data.write((byte)3);
            Common.setData(data, DataType.INT8, att.getIndex());
            Common.setData(data, DataType.ENUM, att.getAccess().getValue());
            Common.setData(data, DataType.NONE, null);
        }
        data.write((byte)DataType.ARRAY.getValue());
        data.write((byte)item.getMethodAttributes().size());
        for (DLMSAttributeSettings it : item.getMethodAttributes())
        {
            data.write((byte)DataType.STRUCTURE.getValue()); //attribute_access_item
            data.write((byte)2);
            Common.setData(data, DataType.INT8, it.getIndex());
            Common.setData(data, DataType.ENUM, it.getMethodAccess().getValue());
        }        
    }
    
    @Override
    public DataType getDataType(int index)
    {
        if (index == 1)
        {
            return DataType.OCTET_STRING;
        }
        else if (index == 2)
        {
            return DataType.ARRAY;                  
        }  
        else if (index == 3)
        {
            return DataType.ARRAY;
        }  
        else if (index == 4)
        {
            return DataType.OCTET_STRING;
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
        else if (index == 2)
        {
            int cnt = m_ObjectList.size();
            try
            {
                ByteArrayOutputStream data = new ByteArrayOutputStream();
                data.write((byte)DataType.ARRAY.getValue());
                //Add count            
                Common.setObjectCount(cnt, data);
                if (cnt != 0)
                {
                    for (DLMSObject it : m_ObjectList)
                    {
                        data.write((byte) DataType.STRUCTURE.getValue());
                        data.write((byte) 4); //Count
                        Common.setData(data, DataType.INT16, it.getShortName()); //base address.
                        Common.setData(data, DataType.UINT16, it.getObjectType().getValue()); //ClassID
                        Common.setData(data, DataType.UINT8, 0); //Version
                        Common.setData(data, DataType.OCTET_STRING, it.getLogicalName()); //LN
                    }
                    if (m_ObjectList.findBySN(this.getShortName()) == null)
                    {
                        data.write((byte) DataType.STRUCTURE.getValue());
                        data.write((byte) 4); //Count
                        Common.setData(data, DataType.INT16, this.getShortName()); //base address.
                        Common.setData(data, DataType.UINT16, this.getObjectType().getValue()); //ClassID
                        Common.setData(data, DataType.UINT8, 0); //Version
                        Common.setData(data, DataType.OCTET_STRING, this.getLogicalName()); //LN
                    }
                }
                return data.toByteArray();
            }
            catch(Exception ex)
            {
                Logger.getLogger(DLMSAssociationShortName.class.getName()).log(Level.SEVERE, null, ex);
                throw new RuntimeException(ex.getMessage());
            }                        
        }  
        else if (index == 3)
        {
            boolean lnExists = m_ObjectList.findBySN(this.getShortName()) != null;
            //Add count        
            int cnt = m_ObjectList.size();
            if (!lnExists)
            {
                ++cnt;
            }            
            ByteArrayOutputStream data = new ByteArrayOutputStream();            
            data.write((byte)DataType.ARRAY.getValue());                        
            Common.setObjectCount(cnt, data);            
            try
            {
                for(DLMSObject it : m_ObjectList)
                {
                    getAccessRights(it, data);
                }
                if (!lnExists)
                {
                    getAccessRights(this, data);
                }
            }
            catch(Exception ex)
            {
                Logger.getLogger(DLMSAssociationShortName.class.getName()).log(Level.SEVERE, null, ex);
                throw new RuntimeException(ex.getMessage());
            }                       
            return data.toByteArray();
        }  
        else if (index == 4)
        {
            
        }                
        throw new IllegalArgumentException("GetValue failed. Invalid attribute index.");
    }
    
    void updateAccessRights(Object[] buff)
    {        
        for (Object access : buff)
        {            
            int sn = Common.intValue(Array.get(access, 0));
            DLMSObject obj = m_ObjectList.findBySN(sn);    
            if (obj != null)
            {            
                for (Object attributeAccess : (Object[]) Array.get(access, 1))
                {                          
                    int id = Common.intValue(Array.get(attributeAccess, 0));
                    int tmp = Common.intValue(Array.get(attributeAccess, 1));
                    AccessMode mode = AccessMode.forValue(tmp);
                    obj.setAccess(id, mode);
                }                
                for (Object methodAccess : (Object[]) Array.get(access, 2))
                {
                    int id = ((Number)((Object[]) methodAccess)[0]).intValue();
                    int tmp = ((Number)((Object[]) methodAccess)[1]).intValue();
                    MethodAccessMode mode = MethodAccessMode.forValue(tmp);
                    obj.setMethodAccess(id, mode);
                }
            }
        }
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
            m_ObjectList.clear();
            if (value != null)
            {
                for(Object item : (Object[])value)
                {
                    int sn = ((Number)Array.get(item, 0)).intValue() & 0xFFFF;
                    ObjectType type = ObjectType.forValue(((Number)Array.get(item, 1)).intValue());
                    int version = ((Number)Array.get(item, 2)).intValue();
                    String ln = DLMSObject.toLogicalName((byte[]) Array.get(item, 3));
                    DLMSObject obj = com.aimir.fep.bypass.dlms.DLMSClient.createObject(type);        
                    obj.setLogicalName(ln);
                    obj.setShortName(sn);
                    obj.setVersion(version);
                    m_ObjectList.add(obj);
                }               
            }
        }  
        else if (index == 3)
        {
            if (value == null)
            {
                for(DLMSObject it : m_ObjectList)
                {
                    for(int pos = 1; pos != it.getAttributeCount(); ++pos)
                    {
                        it.setAccess(pos, AccessMode.NO_ACCESS);
                    }
                }
            }
            else
            {
                updateAccessRights((Object[]) value);
            }
        }  
        else if (index == 4)
        {  
            if (value instanceof String)
            {
                m_SecuritySetupReference = value.toString();
            }
            else if (value != null)
            {
                m_SecuritySetupReference = DLMSClient.changeType((byte[]) value, DataType.OCTET_STRING).toString();
            }
        }  
        else
        {
            throw new IllegalArgumentException("GetValue failed. Invalid attribute index.");
        }
    }
}