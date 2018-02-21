package com.aimir.fep.protocol.rapa;

public final class Fixing
{
	public static enum ProtocolType
	{
		G, E, A;
		
		private String fullName;

		
		public final String getFullName()
		{
			return this.fullName;
		}
	}
}
