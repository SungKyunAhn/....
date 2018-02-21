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
import com.aimir.fep.bypass.dlms.DLMSDateTime;
import com.aimir.fep.bypass.dlms.enums.DataType;
import com.aimir.fep.bypass.dlms.enums.ObjectType;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

public class DLMSMessageHandler extends DLMSObject implements DLMSBase
{
    private java.util.ArrayList<java.util.Map.Entry<DLMSDateTime, DLMSDateTime>> ListeningWindow;
    private String[] AllowedSenders;
    private java.util.ArrayList<java.util.Map.Entry<String, java.util.Map.Entry<Integer, DLMSScriptAction>>> SendersAndActions;

    /**  
     Constructor.
    */
    public DLMSMessageHandler()
    {
        super(ObjectType.MESSAGE_HANDLER);
        ListeningWindow = new java.util.ArrayList<java.util.Map.Entry<DLMSDateTime, DLMSDateTime>>();
        SendersAndActions = new java.util.ArrayList<java.util.Map.Entry<String, java.util.Map.Entry<Integer, DLMSScriptAction>>>();
    }

    /**  
     Constructor.

     @param ln Logican Name of the object.
    */
    public DLMSMessageHandler(String ln)
    {
        super(ObjectType.MESSAGE_HANDLER, ln, 0);
        ListeningWindow = new java.util.ArrayList<java.util.Map.Entry<DLMSDateTime, DLMSDateTime>>();
        SendersAndActions = new java.util.ArrayList<java.util.Map.Entry<String, java.util.Map.Entry<Integer, DLMSScriptAction>>>();
    }

    /**  
     Constructor.

     @param ln Logican Name of the object.
     @param sn Short Name of the object.
    */
    public DLMSMessageHandler(String ln, int sn)
    {
        super(ObjectType.MESSAGE_HANDLER, ln, sn);
        ListeningWindow = new java.util.ArrayList<java.util.Map.Entry<DLMSDateTime, DLMSDateTime>>();
        SendersAndActions = new java.util.ArrayList<java.util.Map.Entry<String, java.util.Map.Entry<Integer, DLMSScriptAction>>>();
    }

    /** 
    Listening Window.
    */
    public final java.util.ArrayList<java.util.Map.Entry<DLMSDateTime, DLMSDateTime>> getListeningWindow()
    {
        return ListeningWindow;
    }

   /** 
    List of allowed Senders.
   */
    public final String[] getAllowedSenders()
    {
        return AllowedSenders;
    }
    public final void setAllowedSenders(String[] value)
    {
        AllowedSenders = value;
    }

   /** 
    Contains the logical name of a "Script table" object and the script selector of the 
    script to be executed if an empty message is received from a match-ing sender.
   */
   public final java.util.ArrayList<java.util.Map.Entry<String, java.util.Map.Entry<Integer, DLMSScriptAction>>> getSendersAndActions()
   {
        return SendersAndActions;
   }

    @Override
    public Object[] getValues()
    {
        return new Object[] {getLogicalName(), ListeningWindow, AllowedSenders, SendersAndActions};
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
        //ListeningWindow
        if (canRead(2))
        {
            attributes.add(2);
        }
        //AllowedSenders
        if (canRead(3))
        {
            attributes.add(3);
        }
        //SendersAndActions
        if (canRead(4))
        {
            attributes.add(4);
        }
        return toIntArray(attributes);
    }
    
    /*
     * Returns amount of attributes.
     */  
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
        return 0;
    }    
    
    @Override
    public DataType getDataType(int index)
    {
        if (index == 1)
        {
            return DataType.OCTET_STRING;
        }
        //ListeningWindow
        if (index == 2)
        {
            return DataType.ARRAY;
        }
        //AllowedSenders
        if (index == 3)
        {
            return DataType.ARRAY;
        }
        //SendersAndActions
        if (index == 4)
        {
            return DataType.ARRAY;
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
            return ListeningWindow;
        }
        if (index == 3)
        {
            return AllowedSenders;
        }
        if (index == 4)
        {
            return SendersAndActions;
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
            ListeningWindow.clear();
            if (value instanceof Object[])
            {
                for(Object it : (Object[]) value)
                {
                    Object[] tmp = (Object[]) it;
                    DLMSDateTime start = (DLMSDateTime) DLMSClient.changeType((byte[]) tmp[0], DataType.DATETIME);
                    DLMSDateTime end = (DLMSDateTime) DLMSClient.changeType((byte[]) tmp[1], DataType.DATETIME);
                    ListeningWindow.add(new SimpleEntry<DLMSDateTime, DLMSDateTime>(start, end));
                }
            }

        }
        else if (index == 3)
        {
            if (value instanceof Object[])
            {
                List<String> tmp = new ArrayList<String>();
                for(Object it : (Object[]) value)
                {
                    tmp.add(new String((byte[])it));
                }
                AllowedSenders = tmp.toArray(new String[tmp.size()]);
            }
            else
            {
                AllowedSenders = new String[0];
            }
        }
        else if (index == 4)
        {
            SendersAndActions.clear();
            if (value instanceof Object[])
            {                    
                for(Object it : (Object[]) value)
                {
                    Object[] tmp = (Object[]) it;
                    String id = new String((byte[])tmp[0]);
                    Object[] tmp2 = (Object[]) tmp[1];
                    /*TODO:
                    KeyValuePair<int, DLMSScriptAction> executed_script = new KeyValuePair<int, DLMSScriptAction>(Convert.ToInt32(tmp2[1], tmp2[2]));
                    SendersAndActions.Add(new KeyValuePair<string, KeyValuePair<int, DLMSScriptAction>>(id, tmp[1] as DLMSDateTime));
                     * */
                }
            }
        }
        else
        {
            throw new IllegalArgumentException("GetValue failed. Invalid attribute index.");
        }
    }
}