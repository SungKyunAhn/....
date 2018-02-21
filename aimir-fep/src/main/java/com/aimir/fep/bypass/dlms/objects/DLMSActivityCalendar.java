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
import com.aimir.fep.bypass.dlms.internal.Common;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class DLMSActivityCalendar extends DLMSObject implements DLMSBase
{
    private String CalendarNameActive;
    private String CalendarNamePassive;
    private DLMSSeasonProfile[] SeasonProfileActive;
    private DLMSWeekProfile[] WeekProfileTableActive;
    private DLMSDayProfile[] DayProfileTableActive;
    private DLMSSeasonProfile[] SeasonProfilePassive;    
    private DLMSDayProfile[] DayProfileTablePassive;
    private DLMSWeekProfile[] WeekProfileTablePassive;
    private DLMSDateTime Time;
    
    /**  
     Constructor.
    */
    public DLMSActivityCalendar()
    {
        this("0.0.13.0.0.255");
    }

    /**  
     Constructor.

     @param ln Logican Name of the object.
    */
    public DLMSActivityCalendar(String ln)
    {
        this(ln, (short) 0);
    }

    /**  
     Constructor.

     @param ln Logican Name of the object.
     @param sn Short Name of the object.
    */
    public DLMSActivityCalendar(String ln, int sn)
    {
        super(ObjectType.ACTIVITY_CALENDAR, ln, sn);
    }
    
    public final String getCalendarNameActive()
    {
        return CalendarNameActive;
    }
    public final void setCalendarNameActive(String value)
    {
        CalendarNameActive = value;
    }

    public final DLMSSeasonProfile[] getSeasonProfileActive()
    {
        return SeasonProfileActive;
    }
    public final void setSeasonProfileActive(DLMSSeasonProfile[] value)
    {
        SeasonProfileActive = value;
    }

    public final DLMSWeekProfile[] getWeekProfileTableActive()
    {
        return WeekProfileTableActive;
    }
    public final void setWeekProfileTableActive(DLMSWeekProfile[] value)
    {
        WeekProfileTableActive = value;
    }

    public final DLMSDayProfile[] getDayProfileTableActive()
    {
        return DayProfileTableActive;
    }
    public final void setDayProfileTableActive(DLMSDayProfile[] value)
    {
        DayProfileTableActive = value;
    }

    public final String getCalendarNamePassive()
    {
        return CalendarNamePassive;
    }
    public final void setCalendarNamePassive(String value)
    {
        CalendarNamePassive = value;
    }

    public final DLMSSeasonProfile[] getSeasonProfilePassive()
    {
        return SeasonProfilePassive;
}
    public final void setSeasonProfilePassive(DLMSSeasonProfile[] value)
    {
        SeasonProfilePassive = value;
    }

    public final DLMSWeekProfile[] getWeekProfileTablePassive()
    {
        return WeekProfileTablePassive;
    }
    public final void setWeekProfileTablePassive(DLMSWeekProfile[] value)
    {
        WeekProfileTablePassive = value;
    }

    public final DLMSDayProfile[] getDayProfileTablePassive()
    {
        return DayProfileTablePassive;
    }
    public final void setDayProfileTablePassive(DLMSDayProfile[] value)
    {
        DayProfileTablePassive = value;
    }

    public final DLMSDateTime getTime()
    {
        return Time;
    }
    public final void setTime(DLMSDateTime value)
    {
        Time = value;
    }

    @Override
    public Object[] getValues()
    {
        return new Object[] {getLogicalName(), getCalendarNameActive(), getSeasonProfileActive(), getWeekProfileTableActive(), getDayProfileTableActive(), getCalendarNamePassive(), getSeasonProfilePassive(), getWeekProfileTablePassive(), getDayProfileTablePassive(), getTime()};
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
        //CalendarNameActive
        if (canRead(2))
        {
            attributes.add(2);
        }            
        //SeasonProfileActive
        if (canRead(3))
        {
            attributes.add(3);
        } 

        //WeekProfileTableActive
        if (canRead(4))
        {
            attributes.add(4);
        } 
        //DayProfileTableActive
        if (canRead(5))
        {
            attributes.add(5);
        } 
        //CalendarNamePassive
        if (canRead(6))
        {
            attributes.add(6);
        } 
        //SeasonProfilePassive
        if (canRead(7))
        {
            attributes.add(7);
        }
        //WeekProfileTablePassive
        if (canRead(8))
        {
            attributes.add(8);
        }
        //DayProfileTablePassive
        if (canRead(9))
        {
            attributes.add(9);
        }
        //Time.
        if (canRead(10))
        {
            attributes.add(10);
        }                
        return toIntArray(attributes);
    }
    
    /*
     * Returns amount of attributes.
     */  
    @Override
    public int getAttributeCount()
    {
        return 10;
    }
    
    /*
     * Returns amount of methods.
     */ 
    @Override
    public int getMethodCount()
    {
        return 1;
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
            return DataType.ARRAY;
        }
        if (index == 6)
        {
            return DataType.OCTET_STRING;
        }        
        if (index == 7)
        {
            return DataType.ARRAY;
        }
        if (index == 8)
        {
            return DataType.ARRAY;
        }
        if (index == 9)
        {
            return DataType.ARRAY;
        }
        if (index == 10)
        {
            return DataType.DATETIME;
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
            return DLMSClient.changeType(Common.getBytes(getCalendarNameActive()), DataType.OCTET_STRING);
        }
        if (index == 3)
        {
            ByteArrayOutputStream data = new ByteArrayOutputStream();
            data.write((byte)DataType.ARRAY.getValue());
            if (getSeasonProfileActive() == null)
            {
                //Add count            
                Common.setObjectCount(0, data);
            }
            else
            {
                int cnt = getSeasonProfileActive().length;
                //Add count            
                Common.setObjectCount(cnt, data);
                try
                {
                    for (DLMSSeasonProfile it :  getSeasonProfileActive())
                    {
                        data.write((byte)DataType.STRUCTURE.getValue());
                        data.write(3);
                        Common.setData(data, DataType.OCTET_STRING, Common.getBytes(it.getName()));
                        Common.setData(data, DataType.OCTET_STRING, it.getStart());
                        Common.setData(data, DataType.OCTET_STRING, Common.getBytes(it.getWeekName()));
                    }
                }
                catch(Exception ex)
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
            if (getWeekProfileTableActive() == null)
            {
                //Add count            
                Common.setObjectCount(0, data);
            }
            else
            {
                int cnt = getWeekProfileTableActive().length;
                //Add count            
                Common.setObjectCount(cnt, data);
                try
                {
                    for (DLMSWeekProfile it : getWeekProfileTableActive())
                    {
                        data.write((byte)DataType.ARRAY.getValue());
                        data.write(8);
                        Common.setData(data, DataType.OCTET_STRING, Common.getBytes(it.getName()));
                        Common.setData(data, DataType.UINT8, it.getMonday());
                        Common.setData(data, DataType.UINT8, it.getTuesday());
                        Common.setData(data, DataType.UINT8, it.getWednesday());
                        Common.setData(data, DataType.UINT8, it.getThursday());
                        Common.setData(data, DataType.UINT8, it.getFriday());
                        Common.setData(data, DataType.UINT8, it.getSaturday());
                        Common.setData(data, DataType.UINT8, it.getSunday());
                    }
                }
                catch(Exception ex)
                {
                    throw new RuntimeException(ex.getMessage());           
                }
            }
            return data.toByteArray();
        }
        if (index == 5)
        {
            ByteArrayOutputStream data = new ByteArrayOutputStream();
            data.write((byte)DataType.ARRAY.getValue());
            if (getDayProfileTableActive() == null)
            {
                //Add count            
                Common.setObjectCount(0, data);
            }
            else
            {
                int cnt = getDayProfileTableActive().length;
                //Add count            
                Common.setObjectCount(cnt, data);
                try
                {
                    for (DLMSDayProfile it : getDayProfileTableActive())
                    {
                        data.write((byte)DataType.STRUCTURE.getValue());
                        data.write(2);
                        Common.setData(data, DataType.UINT8, it.getDayId());
                        data.write((byte)DataType.ARRAY.getValue());
                        //Add count            
                        Common.setObjectCount(it.getDaySchedules().length, data);                        
                        for(DLMSDayProfileAction action : it.getDaySchedules())
                        {
                            data.write((byte)DataType.STRUCTURE.getValue());
                            data.write(3);
                            Common.setData(data, DataType.TIME, action.getStartTime());                            
                            Common.setData(data, DataType.OCTET_STRING, Common.getBytes(action.getScriptLogicalName()));
                            Common.setData(data, DataType.UINT16, action.getScriptSelector());
                        }
                    }
                }
                catch(Exception ex)
                {
                    throw new RuntimeException(ex.getMessage());           
                }
            }
            return data.toByteArray();
        }
        if (index == 6)
        {
            return DLMSClient.changeType(Common.getBytes(getCalendarNamePassive()), DataType.OCTET_STRING);
        }
        //
        if (index == 7)
        {
            ByteArrayOutputStream data = new ByteArrayOutputStream();
            data.write((byte)DataType.ARRAY.getValue());
            if (getSeasonProfilePassive() == null)
            {
                //Add count            
                Common.setObjectCount(0, data);
            }
            else
            {
                int cnt = getSeasonProfilePassive().length;
                try
                {
                    //Add count            
                    Common.setObjectCount(cnt, data);
                    for (DLMSSeasonProfile it : getSeasonProfilePassive())
                    {
                        data.write((byte)DataType.STRUCTURE.getValue());
                        data.write(3);
                        Common.setData(data, DataType.OCTET_STRING, Common.getBytes(it.getName()));
                        Common.setData(data, DataType.OCTET_STRING, it.getStart());
                        Common.setData(data, DataType.OCTET_STRING, Common.getBytes(it.getWeekName()));
                    }
                }
                catch(Exception ex)
                {
                    throw new RuntimeException(ex.getMessage());           
                }
            }
            return data.toByteArray();
        }
        if (index == 8)
        {
            ByteArrayOutputStream data = new ByteArrayOutputStream();
            data.write((byte)DataType.ARRAY.getValue());
            if (getWeekProfileTablePassive() == null)
            {
                //Add count            
                Common.setObjectCount(0, data);
            }
            else
            {
                int cnt = getWeekProfileTablePassive().length;
                //Add count            
                Common.setObjectCount(cnt, data);
                try
                {
                    for(DLMSWeekProfile it : getWeekProfileTablePassive())
                    {
                        data.write((byte)DataType.ARRAY.getValue());
                        data.write(8);
                        Common.setData(data, DataType.OCTET_STRING, Common.getBytes(it.getName()));
                        Common.setData(data, DataType.UINT8, it.getMonday());
                        Common.setData(data, DataType.UINT8, it.getTuesday());
                        Common.setData(data, DataType.UINT8, it.getWednesday());
                        Common.setData(data, DataType.UINT8, it.getThursday());
                        Common.setData(data, DataType.UINT8, it.getFriday());
                        Common.setData(data, DataType.UINT8, it.getSaturday());
                        Common.setData(data, DataType.UINT8, it.getSunday());
                    }
                }
                catch(Exception ex)
                {
                    throw new RuntimeException(ex.getMessage());           
                }
            }
            return data.toByteArray();
        }
        if (index == 9)
        {
            ByteArrayOutputStream data = new ByteArrayOutputStream();
            data.write((byte)DataType.ARRAY.getValue());
            if (getDayProfileTablePassive() == null)
            {
                //Add count            
                Common.setObjectCount(0, data);
            }
            else
            {
                int cnt = getDayProfileTablePassive().length;
                //Add count            
                Common.setObjectCount(cnt, data);
                try
                {
                    for(DLMSDayProfile it : getDayProfileTablePassive())
                    {
                        data.write(DataType.STRUCTURE.getValue());
                        data.write(2);
                        Common.setData(data, DataType.UINT8, it.getDayId());
                        data.write(DataType.ARRAY.getValue());
                        //Add count            
                        Common.setObjectCount(it.getDaySchedules().length, data);
                        for(DLMSDayProfileAction action : it.getDaySchedules())
                        {
                            data.write(DataType.STRUCTURE.getValue());
                            data.write(3);
                            Common.setData(data, DataType.TIME, action.getStartTime());
                            Common.setData(data, DataType.OCTET_STRING, action.getScriptLogicalName());
                            Common.setData(data, DataType.UINT16, action.getScriptSelector());
                        }
                    }
                }
                catch(Exception ex)
                {
                    throw new RuntimeException(ex.getMessage());           
                }
            }
            return data.toByteArray();
        }
        if (index == 10)
        {
            return getTime();
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
            setCalendarNameActive(DLMSClient.changeType((byte[])value, DataType.STRING).toString());
        }
        else if (index == 3)
        {
            setSeasonProfileActive(null);
            if (value != null)
            {
                List<DLMSSeasonProfile> items = new ArrayList<DLMSSeasonProfile>();
                for(Object item : (Object[])value)
                {
                    DLMSSeasonProfile it = new DLMSSeasonProfile();
                    it.setName(DLMSClient.changeType((byte[]) Array.get(item, 0), DataType.STRING).toString());
                    it.setStart((DLMSDateTime) DLMSClient.changeType((byte[])Array.get(item, 1), DataType.DATETIME));
                    it.setWeekName(DLMSClient.changeType((byte[]) Array.get(item, 2), DataType.STRING).toString());
                    items.add(it);
                }
                setSeasonProfileActive(items.toArray(new DLMSSeasonProfile[items.size()]));
            }
        }
        else if (index == 4)
        {
            setWeekProfileTableActive(null);
            if (value != null)
            {
                List<DLMSWeekProfile> items = new ArrayList<DLMSWeekProfile>();
                for (Object item : (Object[]) value)
                {
                    DLMSWeekProfile it = new DLMSWeekProfile();
                    it.setName(DLMSClient.changeType((byte[])Array.get(item, 0), DataType.STRING).toString());
                    it.setMonday(((Number) Array.get(item, 1)).intValue());
                    it.setTuesday(((Number) Array.get(item, 2)).intValue());
                    it.setWednesday(((Number) Array.get(item, 3)).intValue());
                    it.setThursday(((Number) Array.get(item, 4)).intValue());
                    it.setFriday(((Number) Array.get(item, 5)).intValue());
                    it.setSaturday(((Number) Array.get(item, 6)).intValue());
                    it.setSunday(((Number) Array.get(item, 7)).intValue());
                    items.add(it);
                }
                setWeekProfileTableActive(items.toArray(new DLMSWeekProfile[items.size()]));
            }
        }
        else if (index == 5)
        {
            setDayProfileTableActive(null);
            if (value != null)
            {
                List<DLMSDayProfile> items = new ArrayList<DLMSDayProfile>();
                for (Object item : (Object[])value)
                {
                    DLMSDayProfile it = new DLMSDayProfile();
                    it.setDayId(((Number) Array.get(item, 0)).intValue());
                    List<DLMSDayProfileAction> actions = new ArrayList<DLMSDayProfileAction>();
                    for (Object it2 : (Object[])Array.get(item, 1))
                    {
                        DLMSDayProfileAction ac = new DLMSDayProfileAction();
                        ac.setStartTime((DLMSDateTime)DLMSClient.changeType((byte[])Array.get(it2, 0), DataType.TIME));
                        ac.setScriptLogicalName(DLMSClient.changeType((byte[])Array.get(it2, 1), DataType.OCTET_STRING).toString());
                        ac.setScriptSelector(((Number)Array.get(it2, 2)).intValue());
                        actions.add(ac);
                    }
                    it.setDaySchedules(actions.toArray(new DLMSDayProfileAction[actions.size()]));
                    items.add(it);
                }
                setDayProfileTableActive(items.toArray(new DLMSDayProfile[items.size()]));
            }
        }                
        else if (index == 6)
        {
            setCalendarNamePassive(DLMSClient.changeType((byte[])value, DataType.STRING).toString());
        }
        else if (index == 7)
        {
            setSeasonProfilePassive(null);
            if (value != null)
            {
                List<DLMSSeasonProfile> items = new ArrayList<DLMSSeasonProfile>();
                for(Object item : (Object[])value)
                {
                    DLMSSeasonProfile it = new DLMSSeasonProfile();
                    it.setName(DLMSClient.changeType((byte[])Array.get(item, 0), DataType.STRING).toString());
                    it.setStart((DLMSDateTime)DLMSClient.changeType((byte[])Array.get(item, 1), DataType.DATETIME));
                    it.setWeekName(DLMSClient.changeType((byte[])Array.get(item, 2), DataType.STRING).toString());
                    items.add(it);
                }
                setSeasonProfilePassive(items.toArray(new DLMSSeasonProfile[items.size()]));
            }
        }
        else if (index == 8)
        {
            setWeekProfileTablePassive(null);
            if (value != null)
            {
                List<DLMSWeekProfile> items = new ArrayList<DLMSWeekProfile>();
                for (Object item : (Object[])value)
                {
                    DLMSWeekProfile it = new DLMSWeekProfile();
                    it.setName(DLMSClient.changeType((byte[])Array.get(item, 0), DataType.STRING).toString());
                    it.setMonday(((Number) Array.get(item, 1)).intValue());
                    it.setTuesday(((Number) Array.get(item, 2)).intValue());
                    it.setWednesday(((Number) Array.get(item, 3)).intValue());
                    it.setThursday(((Number) Array.get(item, 4)).intValue());
                    it.setFriday(((Number) Array.get(item, 5)).intValue());
                    it.setSaturday(((Number) Array.get(item, 6)).intValue());
                    it.setSunday(((Number) Array.get(item, 7)).intValue());
                    items.add(it);
                }
                setWeekProfileTablePassive(items.toArray(new DLMSWeekProfile[items.size()]));
            }
        }
        else if (index == 9)
        {
            setDayProfileTablePassive(null);
            if (value != null)
            {
                List<DLMSDayProfile> items = new ArrayList<DLMSDayProfile>();
                for (Object item : (Object[])value)
                {
                    DLMSDayProfile it = new DLMSDayProfile();
                    it.setDayId(((Number) Array.get(item, 0)).intValue());
                    List<DLMSDayProfileAction> actions = new ArrayList<DLMSDayProfileAction>();
                    for (Object it2 :  (Object[])Array.get(item, 1))
                    {
                        DLMSDayProfileAction ac = new DLMSDayProfileAction();
                        ac.setStartTime((DLMSDateTime)DLMSClient.changeType((byte[])Array.get(it2, 0), DataType.TIME));
                        ac.setScriptLogicalName(DLMSClient.changeType((byte[])Array.get(it2, 1), DataType.STRING).toString());
                        ac.setScriptSelector(((Number)Array.get(it2, 2)).intValue());
                        actions.add(ac);
                    }
                    it.setDaySchedules(actions.toArray(new DLMSDayProfileAction[actions.size()]));
                    items.add(it);
                }
                setDayProfileTablePassive(items.toArray(new DLMSDayProfile[items.size()]));
            }
        }
        else if (index == 10)
        {
            if (value instanceof DLMSDateTime)
            {
                setTime((DLMSDateTime)value);
            }
            else
            {
                setTime((DLMSDateTime)DLMSClient.changeType((byte[])value, DataType.DATETIME));
            }
        }
        else
        {
            throw new IllegalArgumentException("GetValue failed. Invalid attribute index.");
        }
    }
}