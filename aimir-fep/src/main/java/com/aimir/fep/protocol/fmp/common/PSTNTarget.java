package com.aimir.fep.protocol.fmp.common;

import com.aimir.constants.CommonConstants.Protocol;

/**
 * this class is representd for target information to access over PSTN PSTN
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class PSTNTarget extends CircuitTarget
{
    public PSTNTarget(String mobileId,String ipaddr, String groupId, String memberId)
    {
        super(mobileId,ipaddr,groupId,memberId);
        this.protocol = Protocol.PSTN;
    }
}
