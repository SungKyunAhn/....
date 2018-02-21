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

import java.io.UnsupportedEncodingException;

import com.aimir.fep.bypass.dlms.enums.Authentication;
import com.aimir.fep.bypass.dlms.enums.InterfaceType;
import com.aimir.fep.bypass.dlms.internal.Common;

/**
 * The services to access the attributes and methods of COSEM objects are 
 * determined on DLMS/COSEM Application layer. The services are carried by 
 * Application Protocol Data Units (APDUs). 
 * <p />In DLMS/COSEM the meter is primarily a server, and the controlling system 
 * is a client. Also unsolicited (received without a request) messages are available.
 */
class APDU
{
    byte[] password;
    Authentication authentication;
    ApplicationContextName applicationContextName = new ApplicationContextName();
    int resultValue, resultDiagnosticValue;
    DLMSTagCollection tags;
    UserInformation userInformation = new UserInformation();

    /*
     Constructor.
     */
    public APDU(DLMSTagCollection tags)
    {
        this.authentication = Authentication.NONE;
        this.tags = tags;
    }

    /**
     * UseLN
     * @param value 
     */
    void setUseLN(boolean value)
    {
        applicationContextName.useLN = value;
    }
    /**
     * UseLN
     * @return 
     */
    boolean getUseLN()
    {
        return applicationContextName.useLN;
    }
    /**
     * AssociationResult
     * @return
     * @throws Exception 
     */
    AssociationResult getResultComponent()
    {
        return AssociationResult.forValue(resultValue);
    }
    /**
     * SourceDiagnostic
     * @return
     * @throws Exception 
     */
    SourceDiagnostic getResultDiagnosticValue()
    {        
        return SourceDiagnostic.forValue(resultDiagnosticValue);
    }
    /**
     * Determines the authentication level, and password, if used.
     * @param val Authentication level
     * @param pw Password
     */
    void setAuthentication(Authentication authentication, byte[] password)
    {
        this.authentication = authentication;
        this.password = password;
    }
    /**
     * Retrieves the string that indicates the level of authentication, if any. 
     * @param data 
     */
    void getAuthenticationString(java.nio.ByteBuffer data, byte[] challenge) throws UnsupportedEncodingException
    {
        //If authentication is used.
        if (this.authentication != Authentication.NONE)
        {            
            //Add sender ACSE-requirenents field component.
            data.put((byte) 0x8A);
            data.put((byte) 2);
            data.putShort((short) 0x0780);
            data.put((byte) 0x8B);
            data.put((byte) 7);
            byte[] p = {(byte)0x60, (byte)0x85, (byte)0x74, (byte)0x05, (byte)0x08, (byte)0x02, (byte)authentication.ordinal()};
            data.put(p);
            //Add Calling authentication information.
            int len = 0;
            if (password != null)
            {
                len = password.length;
            }
            if (authentication == Authentication.LOW)
            {
                if (password != null)
                {
                    len = password.length;
                }
            }
            else
            {                    
                len = challenge.length;
            }
            data.put((byte) 0xAC);
            data.put((byte) (2 + len));
            //Add authentication information.
            data.put((byte) 0x80);
            data.put((byte) len);            
            if (challenge != null)
            {
                data.put(challenge);
            }
            else if (password != null)
            {
                data.put(password);
            }            
        }
    }
    /**
     * codeData
     * @param data
     * @param interfaceType
     */
    void codeData(java.nio.ByteBuffer data, InterfaceType interfaceType, 
            byte[] challenge) throws UnsupportedEncodingException
    {
        //AARQ APDU Tag
        data.put(Common.AARQTag);
        //Length
        int LenPos = data.position();
        data.put((byte) 0);
        ///////////////////////////////////////////
        // Add Application context name.
        applicationContextName.codeData(data);
        getAuthenticationString(data, challenge);
        userInformation.codeData(data);
        //Add extra tags...
        if (tags != null)
        {
            for(int a = 0; a < tags.size(); ++a)
            {
            	DLMSTag tag = tags.get(a);
                if (tag != null)
                {
                    //Add data ID.
                    data.put((byte) tag.getID());                    
                    //Add data len.
                    data.put((byte) tag.getData().length);
                    //Add data.
                    data.put(tag.getData());
                }
            }
        }
        data.put(LenPos, (byte) (data.position() - LenPos - 1));
    }
    /**
     * EncodeData
     * @param buff
     * @throws Exception 
     */
    void encodeData(java.nio.ByteBuffer buff)
    {
        // Get AARE tag and length
        int tag = Common.unsignedByteToInt(buff.get());
        if (tag != 0x61 && tag != 0x60 && tag != 0x81 && tag != 0x80)
        {
            throw new DLMSException("Invalid tag.");
        }
        int len = Common.unsignedByteToInt(buff.get());
        int size = buff.limit() - buff.position();
        if (len > size)
        {
            throw new DLMSException("Not enought data.");
        }
        while(buff.position() < buff.limit())
        {
            tag = Common.unsignedByteToInt(buff.get(buff.position()));
            if (tag == 0xA1)
            {
                applicationContextName.encodeData(buff);
            }
            else if (tag == 0xBE)
            {
                userInformation.encodeData(buff);
            }
            else if (tag == 0xA2) //Result
            {
                //Get tag
                Common.unsignedByteToInt(buff.get());
                //Get length.
                Common.unsignedByteToInt(buff.get());
                //Choice for result (INTEGER, universal)
                //Get tag
                Common.unsignedByteToInt(buff.get());
                //Get length.
                Common.unsignedByteToInt(buff.get());
                resultValue = Common.unsignedByteToInt(buff.get());
            }
            else if (tag == 0xA3) //SourceDiagnostic
            {
                //Get tag
                Common.unsignedByteToInt(buff.get());
                //Get length.
                Common.unsignedByteToInt(buff.get());
                // ACSE service user tag.
                //Get tag
                Common.unsignedByteToInt(buff.get());
                //Get length.
                Common.unsignedByteToInt(buff.get());
                // Result source diagnostic component.
                //Get tag
                Common.unsignedByteToInt(buff.get());
                //Get length.
                Common.unsignedByteToInt(buff.get());
                resultDiagnosticValue = Common.unsignedByteToInt(buff.get());
            }
            else if (tag == 0x8A || tag == 0x88) //Authentication.
            {
                tag = buff.get();
                //Get sender ACSE-requirenents field component.
                if (buff.get() != 2)
                {
                    throw new RuntimeException("Invalid tag.");
                }                
                int val = (buff.getShort() & 0xFFFF);
                if (val != 0x0780 && val != 0x0680)
                {
                    throw new RuntimeException("Invalid tag.");
                }
            }               
            else if (tag == 0xAA) //Server Challenge.                
            {
                tag = buff.get();
                len = buff.get();
                buff.get();
                len = Common.unsignedByteToInt(buff.get());                
            }
            else if (tag == 0x8B || tag == 0x89) //Authentication.
            {
                tag = buff.get();
                len = buff.get();
                boolean IsAuthenticationTag = len > 7;
                if (buff.get() != 0x60)
                {
                    throw new RuntimeException("Invalid tag.");
                }
                if ((buff.get() & 0xFF) != 0x85)
                {
                    throw new RuntimeException("Invalid tag.");
                }
                if (buff.get() != 0x74)
                {
                    throw new RuntimeException("Invalid tag.");
                }
                if (buff.get() != 0x05)
                {
                    throw new RuntimeException("Invalid tag.");
                }
                if (buff.get() != 0x08)
                {
                    throw new RuntimeException("Invalid tag.");
                }
                if (buff.get() != 0x02)
                {
                    throw new RuntimeException("Invalid tag.");
                }
                int tmp = buff.get();
                if (tmp < 0 || tmp > 5)
                {
                    throw new RuntimeException("Invalid tag.");
                }
                if (IsAuthenticationTag)
                {
                    authentication = Authentication.forValue(tmp);
                    int tag2 = (buff.get() & 0xFF);
                    if (tag2 != 0xAC && tag2 != 0xAA)
                    {
                        throw new RuntimeException("Invalid tag.");
                    }
                    len = buff.get();
                    //Get authentication information.
                    if ((buff.get() & 0xFF) != 0x80)
                    {
                        throw new RuntimeException("Invalid tag.");
                    }
                    len = buff.get() & 0xFF;
                    password = new byte[len];
                    buff.get(password);
                }
                else
                {
                    authentication = Authentication.NONE;                
                }
            }
            //Unknown tags.
            else
            {
                tag = Common.unsignedByteToInt(buff.get());
                len = Common.unsignedByteToInt(buff.get());
                if (tags != null)
                {
                	DLMSTag tmp = new DLMSTag();
                    tmp.setID(tag);
                    byte[] data = new byte[len];
                    buff.get(data);
                    tmp.setData(data);
                    tags.add(tmp);
                }                
            }
        }
    }
    
    /**
     * Server generates AARE message.
    */
    public final void generateAARE(java.nio.ByteBuffer data, Authentication authentication, 
            byte[] challenge, int maxReceivePDUSize, byte[] conformanceBlock, 
            AssociationResult result, SourceDiagnostic diagnostic)
    {
        int offset = data.position();
        // Set AARE tag and length
        data.put((byte) 0x61);
        data.put((byte) 0); //Length is updated later.
        applicationContextName.codeData(data);
        //Result
        data.put((byte) 0xA2);
        data.put((byte) 3); //len
        data.put((byte) 2); //Tag
        //Choice for result (INTEGER, universal)
        data.put((byte) 1); //Len
        data.put((byte) result.getValue()); //ResultValue
        //SourceDiagnostic
        data.put((byte) 0xA3);
        data.put((byte) 5); //len
        data.put((byte) 0xA1); //Tag
        data.put((byte) 3); //len
        data.put((byte) 2); //Tag
        //Choice for result (INTEGER, universal)
        data.put((byte) 1); //Len
        data.put((byte)diagnostic.getValue()); //diagnostic
        if (diagnostic == SourceDiagnostic.AUTHENTICATION_REQUIRED)
        {                
            //Add server ACSE-requirenents field component.
            data.put((byte)0x88);
            data.put((byte)0x02);  //Len.            
            Common.setUInt16((short) 0x0780, data);
            //Add tag.
            data.put((byte)0x89);
            data.put((byte)0x07);//Len
            data.put((byte)0x60);
            data.put((byte)0x85);
            data.put((byte)0x74);
            data.put((byte)0x05);
            data.put((byte)0x08);
            data.put((byte)0x02);
            data.put((byte) authentication.getValue());
            //Add tag.
            data.put((byte)0xAA);
            data.put((byte) (2 + challenge.length));//Len
            data.put((byte)0x80);
            data.put((byte) challenge.length);
            data.put(challenge);
        }      
        //Add User Information
        data.put((byte) 0xBE); //Tag
        data.put((byte) 0x10); //Length for AARQ user field
        data.put((byte) 0x04); //Coding the choice for user-information (Octet STRING, universal)
        data.put((byte) 0xE); //Length
        data.put(Common.InitialResponce); // Tag for xDLMS-Initiate response
        data.put((byte) 0x00); // Usage field for the response allowed component not used
        data.put((byte) 6); // DLMSVersioNumber
        data.put((byte) 0x5F);
        data.put((byte) 0x1F);
        data.put((byte) 0x04); // length of the conformance block
        data.put((byte) 0x00); // encoding the number of unused bits in the bit string
        data.put(conformanceBlock);
        Common.setUInt16((short) maxReceivePDUSize, data);
        //VAA Name VAA name (0x0007 for LN referencing and 0xFA00 for SN)
        if (getUseLN())
        {
            Common.setUInt16((short) 0x0007, data);
        }
        else
        {
            Common.setUInt16((short) 0xFA00, data);
        }
        data.put(offset + 1, (byte)(data.position() - offset - 2));
    }
}