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

package com.aimir.fep.bypass.dlms.manufacturersettings;

public class ServerAddress
{
    private Object privatePhysicalAddress;
    private HDLCAddressType privateHDLCAddress = HDLCAddressType.DEFAULT;
    private String privateFormula;
    private int privateLogicalAddress;
    private boolean privateSelected;

    /** 
     Constructor.
    */
    public ServerAddress()
    {
    }

    /** 
     Constructor.
    */
    public ServerAddress(HDLCAddressType address, Object value, boolean enabled)
    {
        setHDLCAddress(address);
        setPhysicalAddress(value);
    }

    /** 
     Is server address enabled
    */
    public final boolean getSelected()
    {
        return privateSelected;
    }
    public final void setSelected(boolean value)
    {
        privateSelected = value;
    }

    public final HDLCAddressType getHDLCAddress()
    {
        return privateHDLCAddress;
    }
    public final void setHDLCAddress(HDLCAddressType value)
    {
        privateHDLCAddress = value;
    }

    public final String getFormula()
    {
        return privateFormula;
    }
    public final void setFormula(String value)
    {
        privateFormula = value;
    }

    public final Object getPhysicalAddress()
    {
        return privatePhysicalAddress;
    }
    public final void setPhysicalAddress(Object value)
    {
        privatePhysicalAddress = value;
    }

    public final int getLogicalAddress()
    {
        return privateLogicalAddress;
    }
    public final void setLogicalAddress(int value)
    {
        privateLogicalAddress = value;
    }
}