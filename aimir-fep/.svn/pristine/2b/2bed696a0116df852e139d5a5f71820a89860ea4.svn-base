package com.aimir.fep.command.ws.data;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class ApplicationFaultAdapter extends
		XmlAdapter<ApplicationFault, ApplicationFaultException> {
	@Override
	public ApplicationFault marshal(ApplicationFaultException param) throws java.lang.Exception {
		return new ApplicationFault(param.getFaultCode(), param.getSummary(), param.getDetails(), param.isTemporary());
	}

	@Override
	public ApplicationFaultException unmarshal(ApplicationFault param)
			throws java.lang.Exception {
		return new ApplicationFaultException(param.getFaultCode(), param.getSummary(), param.getDetails(), param.isTemporary());
	}
}