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

import com.aimir.fep.bypass.dlms.enums.ObjectType;
import com.aimir.fep.bypass.dlms.*;
import com.aimir.fep.bypass.dlms.enums.DataType;
import com.aimir.fep.bypass.dlms.internal.Common;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DLMSRegisterMonitor extends DLMSObject implements DLMSBase
{
    private DLMSActionSet[] m_Actions;
    private DLMSMonitoredValue m_MonitoredValue;
    private Object[] m_Thresholds;
    /**  
     Constructor.
    */
    public DLMSRegisterMonitor()
    {
        super(ObjectType.REGISTER_MONITOR);
        this.setThresholds(new Object[0]);            
        this.setMonitoredValue(new DLMSMonitoredValue());
        this.setActions(new DLMSActionSet[0]);
    }

    /**  
     Constructor.

     @param ln Logican Name of the object.
    */
    public DLMSRegisterMonitor(String ln)
    {
        super(ObjectType.REGISTER_MONITOR, ln, 0);
        this.setThresholds(new Object[0]);            
        this.setMonitoredValue(new DLMSMonitoredValue());
        this.setActions(new DLMSActionSet[0]);
    }

    /**  
     Constructor.

     @param ln Logican Name of the object.
     @param sn Short Name of the object.
    */
    public DLMSRegisterMonitor(String ln, int sn)
    {
        super(ObjectType.REGISTER_MONITOR, ln, sn);
        this.setThresholds(new Object[0]);            
        this.setMonitoredValue(new DLMSMonitoredValue());
        this.setActions(new DLMSActionSet[0]);
    }

    public final Object[] getThresholds()
    {
        return m_Thresholds;
    }
    public final void setThresholds(Object[] value)
    {
        m_Thresholds = value;
    }

    public final DLMSMonitoredValue getMonitoredValue()
    {
        return m_MonitoredValue;
    }
    final void setMonitoredValue(DLMSMonitoredValue value)
    {
        m_MonitoredValue = value;
    }

    public final DLMSActionSet[] getActions()
    {
        return m_Actions;
    }
    public final void setActions(DLMSActionSet[] value)
    {
        m_Actions = value;
    }

    @Override
    public Object[] getValues()
    {
        return new Object[] {getLogicalName(), getThresholds(), getMonitoredValue(), getActions()};
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
        //Thresholds
        if (!isRead(2))
        {
            attributes.add(2);
        }
        //MonitoredValue
        if (!isRead(3))
        {
            attributes.add(3);
        }
        //Actions
        if (!isRead(4))
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
        if (index == 2)
        {
            return super.getDataType(index);
        }
        if (index == 3)
        {
            return DataType.ARRAY;
        }
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
            return getThresholds();
        }
        if (index == 3)
        {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            stream.write((byte)DataType.STRUCTURE.getValue());
            stream.write(3);
            try 
            {
                Common.setData(stream, DataType.UINT16, m_MonitoredValue.getObjectType().getValue()); //ClassID            
                Common.setData(stream, DataType.OCTET_STRING, m_MonitoredValue.getLogicalName()); //LN
                Common.setData(stream, DataType.INT8, m_MonitoredValue.getAttributeIndex());
            }
            catch (Exception ex) 
            {
                Logger.getLogger(DLMSRegisterMonitor.class.getName()).log(Level.SEVERE, null, ex);
                throw new RuntimeException(ex.getMessage());
            }
            return stream.toByteArray();            
        }
        if (index == 4)
        {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            stream.write((byte)DataType.STRUCTURE.getValue());
            if (m_Actions == null)
            {
                stream.write(0);
            }
            else
            {
                stream.write(m_Actions.length);
                try 
                {
                    for(DLMSActionSet it : m_Actions)
                    {
                        stream.write((byte)DataType.STRUCTURE.getValue());
                        stream.write(2);
                        stream.write((byte)DataType.STRUCTURE.getValue());
                        stream.write(2);
                        Common.setData(stream, DataType.OCTET_STRING, it.getActionUp().getLogicalName()); //LN
                        Common.setData(stream, DataType.UINT16, it.getActionUp().getScriptSelector()); //ScriptSelector
                        stream.write((byte)DataType.STRUCTURE.getValue());
                        stream.write(2);
                        Common.setData(stream, DataType.OCTET_STRING, it.getActionDown().getLogicalName()); //LN
                        Common.setData(stream, DataType.UINT16, it.getActionDown().getScriptSelector()); //ScriptSelector
                    }
                }
                catch (Exception ex) 
                {
                    Logger.getLogger(DLMSRegisterMonitor.class.getName()).log(Level.SEVERE, null, ex);
                    throw new RuntimeException(ex.getMessage());
                }
            }
            return stream.toByteArray();
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
            setThresholds((Object[]) value);
        }
        else if (index == 3)
        {
            if (getMonitoredValue() == null)
            {
                setMonitoredValue(new DLMSMonitoredValue());
            }
            getMonitoredValue().setObjectType(ObjectType.forValue(((Number)Array.get(value, 0)).intValue()));
            getMonitoredValue().setLogicalName(DLMSClient.changeType((byte[])Array.get(value, 1), DataType.OCTET_STRING).toString());
            getMonitoredValue().setAttributeIndex(((Number)Array.get(value, 2)).intValue());
        }
        else if (index == 4)
        {
            setActions(new DLMSActionSet[0]);
            if (value != null)
            {
                List<DLMSActionSet> items = new ArrayList<DLMSActionSet>();
                for (Object action_set : (Object[])value)
                {
                    DLMSActionSet set = new DLMSActionSet();                        
                    Object target = Array.get(action_set, 0);
                    set.getActionUp().setLogicalName(DLMSClient.changeType((byte[])Array.get(target, 0), DataType.OCTET_STRING).toString());
                    set.getActionUp().setScriptSelector(((Number)Array.get(target, 1)).intValue());
                    target = Array.get(action_set, 1);
                    set.getActionDown().setLogicalName(DLMSClient.changeType((byte[])Array.get(target, 0), DataType.OCTET_STRING).toString());
                    set.getActionDown().setScriptSelector(((Number)Array.get(target, 1)).intValue());
                    items.add(set);
                }
                setActions(items.toArray(new DLMSActionSet[items.size()]));
            }
        }
        else
        {
            throw new IllegalArgumentException("GetValue failed. Invalid attribute index.");
        }
    }
}