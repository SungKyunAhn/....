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
 * Reserved for internal use.
 */
class ApplicationContextName
{
    /**
     * Reserved for internal use.
     */    
    public boolean useLN = false;
    
    /**
     * Reserved for internal use.
     * @param data 
     */
    void codeData(java.nio.ByteBuffer data)
    {        
        //Application context name tag
        data.put((byte) 0xA1);
        //Len
        data.put((byte) 0x09);
        data.put((byte) 0x06);
        data.put((byte) 0x07);
        if (useLN)
        {
            data.put(Common.LogicalNameObjectID);
        }
        else
        {
            data.put(Common.ShortNameObjectID);
        }        
    }

    /**
     * Reserved for internal use.
     * @param buff
     * @throws Exception 
     */
    void encodeData(java.nio.ByteBuffer buff)
    {        
        int tag = Common.unsignedByteToInt(buff.get());
        if (tag != 0xA1)
        {
            throw new DLMSException("Invalid tag.");
        }
        //Get length.
        int len = Common.unsignedByteToInt(buff.get());
        if (buff.limit() - buff.position() < len)
        {
            throw new DLMSException("Encoding failed. Not enought data.");
        }
        buff.get();
        //Get length.
        Common.unsignedByteToInt(buff.get());
        useLN = Common.compare(buff, Common.LogicalNameObjectID);
        if (!useLN)
        {
            buff.position(buff.position() + Common.LogicalNameObjectID.length);
        }
    }
};