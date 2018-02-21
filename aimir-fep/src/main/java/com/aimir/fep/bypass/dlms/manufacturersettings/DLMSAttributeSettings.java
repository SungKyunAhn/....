/*
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
*/

package com.aimir.fep.bypass.dlms.manufacturersettings;

import com.aimir.fep.bypass.dlms.enums.MethodAccessMode;
import com.aimir.fep.bypass.dlms.enums.AccessMode;

public class DLMSAttributeSettings
{
    private int privateMinimumVersion;
    private ObisValueItemCollection privateValues = new ObisValueItemCollection();
    private boolean privateStatic;
    private com.aimir.fep.bypass.dlms.enums.DataType privateUIType = com.aimir.fep.bypass.dlms.enums.DataType.NONE;
    private MethodAccessMode privateMethodAccess = MethodAccessMode.NO_ACCESS;
    private AccessMode privateAccess = AccessMode.READ_WRITE;
    private com.aimir.fep.bypass.dlms.enums.DataType privateType = com.aimir.fep.bypass.dlms.enums.DataType.NONE;
    private AttributeCollection privateParent;
    private String privateName;
    private int privateIndex;
    private int privateOrder;

    /** 
     Constructor.
    */
    public DLMSAttributeSettings()
    {
        
    }

    /** 
     Constructor.
    */
    public DLMSAttributeSettings(int index)
    {
        this();
        setIndex(index);
    }

    /*
     * Copy settings.
     */
    public final void copyTo(DLMSAttributeSettings target)
    {
        target.setName(this.getName());
        target.setIndex(getIndex());
        target.setType(getType());
        target.setUIType(getUIType());
        target.setAccess(getAccess());
        target.setStatic(getStatic());
        target.setValues(getValues());
        target.setOrder(getOrder());
        target.setMinimumVersion(getMinimumVersion());
    }

    /** 
     Attribute name.
    */
    public final String getName()
    {
        return privateName;
    }
    public final void setName(String value)
    {
        privateName = value;
    }

    /** 
     Attribute Index.
    */
    public final int getIndex()
    {
        return privateIndex;
    }
    public final void setIndex(int value)
    {
        privateIndex = value;
    }

    /*
     * Parent collection.
     */
    public final AttributeCollection getParent()
    {
        return privateParent;
    }
    public final void setParent(AttributeCollection value)
    {
        privateParent = value;
    }

    /** 
     Attribute data type.
    */
    public final com.aimir.fep.bypass.dlms.enums.DataType getType()
    {
        return privateType;
    }
    public final void setType(com.aimir.fep.bypass.dlms.enums.DataType value)
    {
        privateType = value;
    }

    /** 
     Data type that user ẃant's to see.
    */
    public final com.aimir.fep.bypass.dlms.enums.DataType getUIType()
    {
        return privateUIType;
    }
    public final void setUIType(com.aimir.fep.bypass.dlms.enums.DataType value)
    {
        privateUIType = value;
    }

    /*
     * Attribute access mode.
     */
    public final AccessMode getAccess()
    {
        return privateAccess;
    }
    public final void setAccess(AccessMode value)
    {
        privateAccess = value;
    }

    /*
     * Method access mode.
     */
    public final MethodAccessMode getMethodAccess()
    {
        return privateMethodAccess;
    }
    public final void setMethodAccess(MethodAccessMode value)
    {
        privateMethodAccess = value;
    }

    /*
     * Is attribute static. If it is static it is needed to read only once.
     */
    public final boolean getStatic()
    {
        return privateStatic;
    }
    public final void setStatic(boolean value)
    {
        privateStatic = value;
    }

    /** 
     Attribute values.
    */
    public final ObisValueItemCollection getValues()
    {
        return privateValues;
    }
    public final void setValues(ObisValueItemCollection value)
    {
        privateValues = value;
    }

    /** 
     Read order.
     * 
     * User read order to set read order of attributes.
    */
    public final int getOrder()
    {
        return privateOrder;
    }
    public final void setOrder(int value)
    {
        privateOrder = value;
    }

    /** 
     Minimum version where this attribute is implemented.
    */
    public final int getMinimumVersion()
    {
        return privateMinimumVersion;
    }
    public final void setMinimumVersion(int value)
    {
        privateMinimumVersion = value;
    }
}