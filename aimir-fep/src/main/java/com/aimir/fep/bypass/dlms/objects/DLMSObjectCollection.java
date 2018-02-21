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

/** 
 Collection of DLMS objects.
*/
public class DLMSObjectCollection extends java.util.ArrayList<DLMSObject> implements java.util.List<DLMSObject>
{
    private static final long serialVersionUID = 1L;
    private Object Parent;
    
    /**
     * Constructor.
     */
    public DLMSObjectCollection()
    {        
    }

     /**
     * Constructor.
     */
    public DLMSObjectCollection(Object parent)
    {
        Parent = parent;
    }

    public final Object getParent()
    {
        return Parent;
    }
    final void setParent(Object value)
    {
        Parent = value;
    }     

    public final DLMSObjectCollection getObjects(ObjectType type)
    {
    	DLMSObjectCollection items = new DLMSObjectCollection();
        for (DLMSObject it : this)
        {
            if (it.getObjectType() == type)
            {
                items.add(it);
            }
        }
        return items;
    }

    public final DLMSObjectCollection getObjects(ObjectType[] types)
    {
    	DLMSObjectCollection items = new DLMSObjectCollection();
        for (DLMSObject it : this)
        {
            for(ObjectType type : types)
            {
                if (type == it.getObjectType())
                {
                    items.add(it);
                    break;
                }
            }
        }
        return items;
    }

    public final DLMSObject findByLN(com.aimir.fep.bypass.dlms.enums.ObjectType type, String ln)
    {
        for (DLMSObject it : this)
        {
            if ((type == com.aimir.fep.bypass.dlms.enums.ObjectType.ALL || it.getObjectType() == type) && it.getLogicalName().trim().equals(ln))
            {
                return it;
            }
        }
        return null;
    }

    public final DLMSObject findBySN(int sn)
    {
        for (DLMSObject it : this)
        {
            if (it.getShortName() == sn)
            {
                return it;
            }
        }
        return null;
    }

    @Override
    public final boolean add(DLMSObject item)
    {
        boolean ret = super.add(item);
        if (this.getParent() != null && item.getParent() == null)
        {
            item.setParent(this);
        }
        return ret;
    }
    
    public @Override String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (DLMSObject it : this)
        {
            if (sb.length() != 1)
            {
                sb.append(", ");
            }
            sb.append(it.getName().toString());
        }
        sb.append(']');
        return sb.toString();
    }
}