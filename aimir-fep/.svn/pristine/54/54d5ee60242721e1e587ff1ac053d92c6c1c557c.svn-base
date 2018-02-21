package com.aimir.fep.protocol.snmp;

/**
 * @author  2016-05-02 Hansejin
 */
public class SnmpConstants {


    public enum SnmpActionType {
        TRAP(0), GET(1), NEXT(2), WALK(3), SET(4), UNKNOWN(99);

        private int type;

        private SnmpActionType(int type) {
            this.type = type;
        }

        public int getValue() {
            return type;
        }

        public static SnmpActionType getItem(int value) {
            for (SnmpActionType fc : SnmpActionType.values()) {
                if (fc.type == value) {
                    return fc;
                }
            }
            return UNKNOWN;
        }
    }



}
