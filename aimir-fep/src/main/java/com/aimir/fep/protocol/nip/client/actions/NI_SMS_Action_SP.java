package com.aimir.fep.protocol.nip.client.actions;

import com.aimir.dao.device.AsyncCommandLogDao;
import com.aimir.dao.device.AsyncCommandParamDao;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author HanSeJin
 */
@Service
public class NI_SMS_Action_SP {

    private static Log log = LogFactory.getLog(NI_SMS_Action_SP.class);

    @Autowired
    AsyncCommandLogDao asyncCommandLogDao;

    @Autowired
    AsyncCommandParamDao asyncCommandParamDao;

    public String saveAsyncCommandForMBB(){


        return null;
    }

    //

}
