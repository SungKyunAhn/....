package com.aimir.fep.protocol.rapa;

import java.util.List;

public class BillingFrame extends MessageBody {

   	private List<BillDumpPacket> BillDumpPacketList;	// dump packet

	public List<BillDumpPacket> getBillDumpPacketList() {
		return BillDumpPacketList;
	}

	public void setBillDumpPacketList(List<BillDumpPacket> billDumpPacketList) {
		BillDumpPacketList = billDumpPacketList;
	}	
}
