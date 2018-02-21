package com.aimir.fep.udp;

import java.io.IOException;

public class UDPTest {

	public static void main(String[] args) {
		
		/*
		 * UDP Client Test
		 */
		//System.out.println("#### UDP Client 테스트");
		// 이름, 로컬주소, 로컬포트, 대상주소, 대상포트
//		UdpClient client1 = new UdpClient("한거", "187.1.30.111", 8009, "187.1.30.111", 8006);
//		UdpClient client2 = new UdpClient("지애", "187.1.30.111", 8009, "187.1.30.117", 8006);
//		UdpClient client3 = new UdpClient("랩뀨", "187.1.30.111", 8009, "187.1.30.113", 8006);
//		
//		Thread t1 = new Thread(client1);			
//		Thread t2 = new Thread(client2);
//		Thread t3 = new Thread(client3);
//		
//		t3.start();
//		t2.start();
//		t1.start();
		
		
		/*
		 * UDP Server Test
		 */
		System.out.println("#### UDP Server 테스트");
		try {
			UdpServer server = new UdpServer("음훼훼", 8002);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
