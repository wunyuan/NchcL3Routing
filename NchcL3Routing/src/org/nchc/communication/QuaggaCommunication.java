package org.nchc.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;

import org.nchc.data.PeerData;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;

public class QuaggaCommunication {
	
	
	private Session session = null;
	
	public abstract class MyUserInfo implements UserInfo, UIKeyboardInteractive {
		
		public String getPassword() { return null; }
		public boolean promptYesNo(String str) { return true; }
		public String getPassphrase() { return null; }
		public boolean promptPassphrase(String message) { return false; }
		public boolean promptPassword(String message) { return false; }
		public void showMessage(String message) { }
		public String [] promptKeyboardInteractive(String destination,
				String name,
				String instruction,
				String [] prompt,
				boolean [] echo) {
			return null;
		}
		
	}
	
	
	public void createSession( String account, String pwd, String ip, int port) throws JSchException {
		
		JSch jsch = new JSch();
			
		session = jsch.getSession(account, ip, port);
		session.setPassword(pwd);
			
		UserInfo userInfo = new MyUserInfo() {};
		session.setUserInfo(userInfo);
		session.connect();
			
	}
	
	
	public ArrayList<PeerData> loginToQuaggaToGetPeerData( String telnetSite, String cmd ) throws JSchException, IOException {
		
		ArrayList<PeerData> peerDataList = new ArrayList<PeerData>();
		
		Channel channel = session.openChannel("exec");
		
			

		((ChannelExec) channel).setCommand(telnetSite);
			
		BufferedReader br = new BufferedReader(new InputStreamReader(channel.getInputStream()));
		PrintStream ps = new PrintStream(channel.getOutputStream());

		channel.connect();
		ps.println("sdnip");
		ps.println(cmd);
		ps.flush();
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ps.close();
		channel.disconnect();
		peerDataList = fetchPeerData(br);
	
				
		return peerDataList;
		
	}


	private ArrayList<PeerData> fetchPeerData(BufferedReader br) {
		// TODO Auto-generated method stub	
		
		ArrayList<PeerData> PeerDataList = new ArrayList<PeerData>();
		
		try {	
			
			String test = null;
			String peerID = null;
			String peerState = null;
			String bgpID = null;
			while( (test = br.readLine()) != null ) {
				String matchStr = "BGP neighbor is ";
				String matchStr2 = " remote AS ";
				System.out.println(test);
				if(test.contains(matchStr)) {
					String[] strArr = test.split(",");					
					peerID = strArr[0].substring(matchStr.length(), strArr[0].length());
					bgpID = strArr[1].substring(matchStr2.length(), strArr[1].length());
					while( (test = br.readLine()) != null ) {
						matchStr = "  BGP state = ";
						System.out.println(test);
						if(test.contains(matchStr)) {
							strArr = test.split(",");					
							peerState = strArr[0].substring(matchStr.length(), strArr[0].length());
							PeerDataList.add(new PeerData(peerID, peerState, bgpID));
							break;
						}
					};				
				}		
			};
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.print(e);
		}	
		
		
		
		return PeerDataList;
	}
	
	public Session getSession() {
		return this.session;
	}
	
	
	public String loginToQuaggaToConfigure( String telnetSite, String peerID, String bgpID, String cmd ) throws JSchException, IOException {
		
		String result = "";
		
		Channel channel = session.openChannel("exec");		

		((ChannelExec) channel).setCommand(telnetSite);
			
		BufferedReader br = new BufferedReader(new InputStreamReader(channel.getInputStream()));
		PrintStream ps = new PrintStream(channel.getOutputStream());

		channel.connect();
		ps.println("sdnip");
		ps.println("enable");
		ps.println("show run");
		ps.flush();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String rtBgpID = fetchRouterBgpID(br);
		if(!rtBgpID.equals("")) {
			
			ps.println("configure terminal");
			ps.println("router bgp "+ rtBgpID);
			
			if(cmd.equals("connect")) {
				
				ps.println("neighbor "+ peerID + " remote-as "+bgpID);
				ps.println("neighbor "+ peerID + " ebgp-multihop 255");
				ps.println("neighbor "+ peerID + " advertisement-interval 5");
				ps.println("neighbor "+ peerID + " timers connect 5");

				result = "connect execution done!";
				
			} else {
				
				String win = "no neighbor "+ peerID + " remote-as "+bgpID;
				System.out.println("ready to do: "+win);
				ps.println(win);
				result = "disconnect execution done!";
			}
			
			ps.println("wr");
			ps.flush();
			
		}

		ps.close();
		channel.disconnect();
		
		return result;
		
	}


	private String fetchRouterBgpID(BufferedReader br) {
		// TODO Auto-generated method stub
		
		String routerBgpID = "";

		try {	
			
			String test = null;
			while( (test = br.readLine()) != null ) {
				String matchStr = "router bgp ";
				System.out.println(test);
				if(test.contains(matchStr)) {				
					routerBgpID = test.substring(matchStr.length(), test.length());
					break;
						
				}		
			};
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.print(e);
		}	
		
		
		return routerBgpID;
	}

}
