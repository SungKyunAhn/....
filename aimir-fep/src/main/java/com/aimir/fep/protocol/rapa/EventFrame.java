package com.aimir.fep.protocol.rapa;

import java.util.List;

public class EventFrame extends MessageBody {
	
	private List<EventDumpPacket> eventDumpPacketList;	// dump packet

	public List<EventDumpPacket> getEventDumpPacketList() {
		return eventDumpPacketList;
	}

	public void setEventDumpPacketList(List<EventDumpPacket> eventDumpPacketList) {
		this.eventDumpPacketList = eventDumpPacketList;
	}
}
