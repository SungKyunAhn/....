package com.aimir.fep.protocol.nip.command;

public class ObisData {
    public int obisIndex;
    public int selectiveAccessLength;
    public String selectiveAccessData;
    public ObisCodes obisCodes;
	
    public void newObisCodes() {
        this.obisCodes = new ObisCodes();
    }
	
    public ObisCodes getObisCodes() {
        return obisCodes;
    }

    public void setObisCodes(ObisCodes obisCodes) {
        this.obisCodes = obisCodes;
    }
	
    public int getObisIndex() {
        return obisIndex;
    }

    public void setObisIndex(int obisIndex) {
        this.obisIndex = obisIndex;
    }
	
    public int getSelectiveAccessLength() {
        return selectiveAccessLength;
    }

    public void setSelectiveAccessLength(int selectiveAccessLength) {
        this.selectiveAccessLength = selectiveAccessLength;
    }

    public String getSelectiveAccessData() {
        return selectiveAccessData;
    }

    public void setSelectiveAccessData(String selectiveAccessData) {
        this.selectiveAccessData = selectiveAccessData;
    }
	
    public class ObisCodes{
        String serviceTypes;
        String classId;
        String obis;
        int attribute;
		
        public String getServiceTypes() {
            return serviceTypes;
        }

        public void setServiceTypes(String serviceTypes) {
            this.serviceTypes = serviceTypes;
        }
        
        public String getClassId() {
            return classId;
        }

        public void setClassId(String classId) {
            this.classId = classId;
        }

        public String getObis() {
            return obis;
        }

        public void setObis(String obis) {
            this.obis = obis;
        }
        
        public int getAttribute() {
            return attribute;
        }

        public void setAttribute(int attribute) {
            this.attribute = attribute;
        }
	}
}