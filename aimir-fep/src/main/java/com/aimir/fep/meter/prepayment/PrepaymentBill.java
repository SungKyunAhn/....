package com.aimir.fep.meter.prepayment;

import com.aimir.model.mvm.LpEM;
import com.aimir.model.system.Contract;

public interface PrepaymentBill {
    public void saveBillingWithTariff(Contract contract, LpEM[] lastLp) throws Exception;
}
