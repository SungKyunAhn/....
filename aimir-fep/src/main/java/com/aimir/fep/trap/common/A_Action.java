package com.aimir.fep.trap.common;

import com.aimir.notification.Alarm;
import com.aimir.notification.FMPTrap;


/**
 * Alarm Process Action Interface
 *
 * @author Y.S Kim
 * @version $Rev: 1 $, $Date: 2005-12-13 15:59:15 +0900 $,
 */
public interface A_Action
{
    /**
     * execute alarm action
     *
     * @param trap - FMP Trap(MCU Alarm)
     * @param alarm - Alarm Data
     */
    public void execute(FMPTrap trap, Alarm alarm) throws Exception;
}
