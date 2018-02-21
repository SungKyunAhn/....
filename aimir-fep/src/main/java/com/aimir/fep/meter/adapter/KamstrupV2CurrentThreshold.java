package com.aimir.fep.meter.adapter;

import java.text.DecimalFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.fep.meter.data.Instrument;
import com.aimir.fep.meter.parser.Kamstrup;
import com.aimir.fep.meter.parser.KamstrupV2;
import com.aimir.fep.meter.parser.MeterDataParser;
import com.aimir.fep.util.EventUtil;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.Meter;
import com.aimir.model.system.Contract;

public class KamstrupV2CurrentThreshold implements AdapterInterface {
    private static Log log = LogFactory.getLog(KamstrupV2CurrentThreshold.class);

    @Override
    public void execute(MeterDataParser parser) {
        KamstrupV2 kamstrupV2 = (KamstrupV2)parser;
        Kamstrup kamstrup = kamstrupV2.getKamstrupMeta();
        
        Meter meter = parser.getMeter();
        Contract contract = meter.getContract();
        if (contract == null) return;
        
        double threshold1 = contract.getThreshold1()==null ? 0.0:contract.getThreshold1();
        double threshold2 = contract.getThreshold2()==null ? 0.0:contract.getThreshold2();
        double threshold3 = contract.getThreshold3()==null ? 0.0:contract.getThreshold3();

        Instrument inst = kamstrup.getInstrument()[0];
        double current_a = inst.getCURR_A()==null? 0.0:inst.getCURR_A();
        double current_b = inst.getCURR_B()==null? 0.0:inst.getCURR_B();
        double current_c = inst.getCURR_C()==null? 0.0:inst.getCURR_C();
        
        log.debug("TH1[" + threshold1 + "] TH2[" + threshold2 + "] TH3[" + threshold3 + 
                "] CUR1[" + current_a + "] CUR2[" + current_b + "] CUR3[" + current_c + "]");
        
        DecimalFormat df = new DecimalFormat("0.000");
        String message = "";
        if (threshold1 > 0 && threshold1 < current_a)
            message = "CURR_A[" + df.format(current_a) + "] ";
        
        if (threshold2 > 0 && threshold2 < current_b)
            message += "CURR_B[" + df.format(current_b) + "] ";
        
        if (threshold3 > 0 && threshold3 < current_c)
            message += "CURR_C[" + df.format(current_c) + "]";
        
        if (!message.equals("")) {
            try {
                EventUtil.sendEvent("Threshold Warning",
                        TargetClass.valueOf(meter.getMeterType().getName()),
                        meter.getMdsId(),
                        parser.getMeterTime(),
                        new String[][] {{"message",message}},
                        new EventAlertLog()
                        );
            }
            catch (Exception e) {
                log.warn(e, e);
            }
        }
    }
}
