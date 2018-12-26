package org.nchc.data;

public class PeerData {
	
	private String peerID;
	private String state;
	private String bgpID;
	
	public PeerData(String peerID, String state, String bgpID) {		
		this.peerID = peerID;
		this.state = state;	
		this.bgpID = bgpID;
	}

	public String getPeerID() {
		return peerID;
	}

	public void setPeerID(String peerID) {
		this.peerID = peerID;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getBgpID() {
		return bgpID;
	}

	public void setBgpID(String bgpID) {
		this.bgpID = bgpID;
	}
	
	

}
