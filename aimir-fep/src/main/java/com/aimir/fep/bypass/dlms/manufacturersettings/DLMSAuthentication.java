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

import com.aimir.fep.bypass.dlms.enums.Authentication;

/** 
 Authentication class is used to give authentication iformation to the server.
*/
public class DLMSAuthentication
{
    private boolean m_Selected;
    private Object m_ClientID;
    private Authentication m_Type = Authentication.NONE;
    private byte[] m_Password;

    public DLMSAuthentication()
    {
    }

    @Override
    public String toString()
    {
        return getType().toString();
    }

    /** 
     Constructor.

     @param type Authentication type
     @param clientID Client Id.
    */
    public DLMSAuthentication(Authentication type, Object clientID)
    {
        this(type, null, clientID);
    }

    /** 
     Constructor.

     @param type Authentication type
     @param pw Used password.
     @param clientID Client Id.
    */
    public DLMSAuthentication(Authentication type, byte[] pw, Object clientID)
    {
        setType(type);
        setPassword(pw);
        setClientID(clientID);
    }

    /** 
     Is authentication selected.
    */

    public final boolean getSelected()
    {
        return m_Selected;
    }
    public final void setSelected(boolean value)
    {
        m_Selected = value;
    }

    /** 
     Authentication type
    */
    public final Authentication getType()
    {
        return m_Type;
    }
    public final void setType(Authentication value)
    {
        m_Type = value;
    }

    /** 
     Client address.
    */
    public final Object getClientID()
    {
        return m_ClientID;
    }
    public final void setClientID(Object value)
    {
        m_ClientID = value;
    }

    /** 
     Used password.
    */
    public final byte[] getPassword()
    {
        return m_Password;
    }
    public final void setPassword(byte[] value)
    {
        m_Password = value;
    }
}