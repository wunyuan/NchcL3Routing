package org.nchc.communication;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.nchc.data.PeerData;

import com.jcraft.jsch.JSchException;

public class CommunicationHandler {
	
	private String sshIp = "";
	private String sshAccount = "";
	private String sshPwd = "";
	
	public CommunicationHandler(String sshIp, String sshAccount, String sshPwd) {
		
		this.sshIp = sshIp;
		this.sshAccount = sshAccount;
		this.sshPwd = sshPwd;
		
	}
	
	public String getPeerData(ArrayList<PeerData> peerDataList) {
	
		int port = 22;

		QuaggaCommunication quaggaConn = new QuaggaCommunication();
		JSONObject peerDataJso = new JSONObject();
		
		try {
			
			quaggaConn.createSession( sshAccount, sshPwd, sshIp, port);
			peerDataList = quaggaConn.loginToQuaggaToGetPeerData("telnet 10.10.10.1 2605", "show bgp neighbors");
			peerDataJso = peerDataListTranslateToJson(peerDataList);

			quaggaConn.getSession().disconnect();
			
		} catch (JSchException | IOException | JSONException e) {
			// TODO Auto-generated catch block
			quaggaConn.getSession().disconnect();
			System.out.println(e);
		}
		
		if(peerDataJso.length() == 0 ) {
			try {
				peerDataJso.put("error", "noData");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return peerDataJso.toString();

	}

	
	private JSONObject peerDataListTranslateToJson(ArrayList<PeerData> peerDataList) throws JSONException {
		
		JSONObject jsoheader = new JSONObject();
		JSONArray jsa = new JSONArray();
		
		for(PeerData peer : peerDataList) {
			
			JSONObject jso = new JSONObject();
			jso.put("peerID", peer.getPeerID());
			jso.put("state", peer.getState());
			jso.put("bgpID", peer.getBgpID());
			jsa.put(jso);
			
		}
		
		jsoheader.put("peerData", jsa);
		
		
		return jsoheader;
	}


	public String setQuaggaPeerConnection(String site, String sitePort, String peerID, String bgpID, String cmd) {
		// TODO Auto-generated method stub
		

		int port = 22;

		String result = "";
		QuaggaCommunication quaggaConn = new QuaggaCommunication();
		JSONObject peerDataJso = new JSONObject();
		
		try {
			
			quaggaConn.createSession( "nchclab", "nchc380", "192.168.56.101", port);
			result = quaggaConn.loginToQuaggaToConfigure("telnet "+site + " "+ sitePort, peerID, bgpID, cmd);
			//peerDataJso = peerDataListTranslateToJson(peerDataList);

			quaggaConn.getSession().disconnect();
			
		} catch (JSchException | IOException e) {
			// TODO Auto-generated catch block
			quaggaConn.getSession().disconnect();
			System.out.println(e);
		}
		
		if(peerDataJso.length() == 0 ) {
			try {
				peerDataJso.put("error", "noData");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		return result;
	}
	
}
