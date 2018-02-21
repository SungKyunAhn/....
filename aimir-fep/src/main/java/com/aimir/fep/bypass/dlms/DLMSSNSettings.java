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

package com.aimir.fep.bypass.dlms;

import com.aimir.fep.bypass.dlms.internal.Common;

/** 
 DLMSSNSettings contains commands for retrieving and setting the Short Name 
 settings of the server, shortly said SN referencing support.
*/
public class DLMSSNSettings
{
    /** 
     Settings.
    */
    byte[] conformanceBlock;

    /** 
     Constructor.
    */
    DLMSSNSettings(byte[] conformanceBlock)
    {
        this.conformanceBlock = conformanceBlock;
    }

    void copyTo(DLMSSNSettings target)
    {
        target.conformanceBlock = this.conformanceBlock;
    }

    /** 
     Checks, whether data can be read from the server.
    */
    public final boolean getRead()
    {
        return Common.getBits(conformanceBlock[0], 0x10);
    }
    public final void setRead(boolean value)
    {
        conformanceBlock[0] = Common.setBits(conformanceBlock[0], 0x10, value);
    }

    /** 
     Checks, whether data can be written to the server.
    */
    public final boolean getWrite()
    {
        return Common.getBits(conformanceBlock[0], 0x8);
    }
    public final void setWrite(boolean value)
    {
        conformanceBlock[0] = Common.setBits(conformanceBlock[0], 0x8, value);
    }

    public final boolean getUnconfirmedWrite()
    {
        return Common.getBits(conformanceBlock[0], 0x4);
    }
    public final void setUnconfirmedWrite(boolean value)
    {
        conformanceBlock[0] = Common.setBits(conformanceBlock[0], 0x4, value);
    }

    public final boolean getInformationReport()
    {
        return Common.getBits(conformanceBlock[1], 0x1);
    }
    public final void setInformationReport(boolean value)
    {
        conformanceBlock[1] = Common.setBits(conformanceBlock[1], 0x1, value);
    }

    public final boolean getMultipleReferences()
    {
        return Common.getBits(conformanceBlock[1], 0x2);
    }
    public final void setMultipleReferences(boolean value)
    {
        conformanceBlock[1] = Common.setBits(conformanceBlock[1], 0x2, value);
    }

    public final boolean getParameterizedAccess()
    {
        return Common.getBits(conformanceBlock[2], 0x20);
    }
    public final void setParameterizedAccess(boolean value)
    {
        conformanceBlock[2] = Common.setBits(conformanceBlock[2], 0x20, value);
    }
}