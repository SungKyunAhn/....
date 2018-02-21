package com.aimir.fep.util;

/**
 * CRC 16
 * 
 * @author Yeon Kyoung Park
 * @version $Rev: 1 $, $Date: 2008-01-11 15:59:15 +0900 $,
 */
public class KepcoCRC16 {

    public static char KEPCO_CRC_POLY = 0xA001;

    /**
     * constructor
     */
    public KepcoCRC16() 
    { 
    } 
    
    public static byte[] getCRC16(byte[] pFrame, int nLength)
    {
        char ret = crc(pFrame,nLength);
        byte[] data = DataUtil.get2ByteToInt(ret);
        DataUtil.convertEndian(data);
        return data;
    }

    public static char crc(byte[] pFrame, int nLength)
    {
        int          i;
        char         nReturnCRC = 0x0000;
        char         nTempCRC;

        if(nLength == 0) 
            return (char) (~nReturnCRC);
        int k = 0;
        do 
        {
            for(i=0, nTempCRC = (char) (0xff & pFrame[k++]) ; i<8 ; i++, nTempCRC >>= 1) 
            {
                if(((nReturnCRC & 0x0001) ^ (nTempCRC & 0x0001)) > 0) 
                    nReturnCRC = (char) ((nReturnCRC >> 1) ^ KEPCO_CRC_POLY);
                else 
                    nReturnCRC >>= 1;   
            }
        } while(--nLength > 0);

        nReturnCRC  = (char) ~nReturnCRC; 
        nTempCRC = nReturnCRC;
        nReturnCRC  = (char) ((nReturnCRC << 8) | ((nTempCRC >> 8) & 0xff));

        return(nReturnCRC);
    }

}

