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

import com.aimir.fep.bypass.dlms.enums.MessageType;
import com.aimir.fep.bypass.dlms.enums.ServiceType;

public class SendDestinationAndMethod
{
    private ServiceType Service;
    private String Destination;
    private MessageType Message;
    
    public final ServiceType getService()
    {
        return Service;
    }
    public final void setService(ServiceType value)
    {
        Service = value;
    }
    
    public final String getDestination()
    {
        return Destination;
    }
    public final void setDestination(String value)
    {
        Destination = value;
    }
    
    public final MessageType getMessage()
    {
        return Message;
    }
    public final void setMessage(MessageType value)
    {
        Message = value;
    }
}