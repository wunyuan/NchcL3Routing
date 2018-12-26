package org.nchc;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.json.JSONObject;
import org.nchc.communication.CommunicationHandler;
import org.nchc.data.PeerData;

/**
 * Servlet implementation class L3RoutingWebEntry
 */
@WebServlet("/L3RoutingWebEntry")
public class L3RoutingWebEntry extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public L3RoutingWebEntry() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
	//	CommunicationHandler cm = new CommunicationHandler();
	//	String peerDataListStr = cm.getPeerData(new ArrayList<PeerData>());	

		RequestDispatcher jspPage = request.getRequestDispatcher("/NchcL3RoutingWebUI/index.jsp");
		
		String timeStamp = getCurrentTimeStamp();
	//	request.setAttribute("peerData", peerDataListStr);
		request.setAttribute("timeStamp", timeStamp);
		jspPage.forward(request, response);

		
		
		//doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//doGet(request, response);
		

		String quaggaIp = request.getParameter("quaggaip");
		String account = request.getParameter("sshaccount");
		String pwd = request.getParameter("sshpwd");
		
		
		String refreshFlag = request.getParameter("refreshflag");
		String loginFlag = request.getParameter("loginflag");
		String mgnFlag = request.getParameter("mgnflag");
		String timeStamp = request.getParameter("timeStamp");
		System.out.println(timeStamp);
		
		CommunicationHandler cm = null;
		
		if(loginFlag != null) {
				
			boolean match = false;
			Cookie [] cookieSet = request.getCookies();

			for (Cookie ck : cookieSet) {
				String ckn = ck.getName().toString();

				if(ckn.equals("quaggaIp#"+timeStamp)) {				
					match = true;
					break;
				}
			}	
			
			if(!match) {
				Cookie qIpCookie = new Cookie("quaggaIp#"+timeStamp, quaggaIp);
				Cookie sACookie = new Cookie("sshAccount#"+timeStamp, account);
				Cookie sPCookie = new Cookie("sshPwd#"+timeStamp, pwd);					
				response.addCookie(qIpCookie);
				response.addCookie(sACookie);
				response.addCookie(sPCookie);	
			}
					
			cm = new CommunicationHandler(quaggaIp, account, pwd);
		}
			
		
		if(mgnFlag != null) {
			
			Cookie [] cookieSet = request.getCookies();
			
			for(Cookie ck : cookieSet) {
				String ckn = ck.getName().toString();

				if( ckn.equals("quaggaIp#"+timeStamp)) {
					quaggaIp = ck.getValue();
				}
				if( ckn.equals("sshAccount#"+timeStamp)) {
					account = ck.getValue();
				}
				if( ckn.equals("sshPwd#"+timeStamp)) {
					pwd = ck.getValue();
				}
			}
			
			
			cm = new CommunicationHandler(quaggaIp, account, pwd);
			String peerID = request.getParameter("peerip");
			String cmd = request.getParameter("command");
			String site = "10.10.10.1";
			String port = "2605";
			String bgpID = request.getParameter("peerbgpid");
			
			String result = cm.setQuaggaPeerConnection(site, port, peerID, bgpID, cmd);
			System.out.println(result);					
		}
		
		if(refreshFlag != null) {
			
			Cookie [] cookieSet = request.getCookies();
			
			for(Cookie ck : cookieSet) {
				String ckn = ck.getName().toString();

				if( ckn.equals("quaggaIp#"+timeStamp)) {
					quaggaIp = ck.getValue();
				}
				if( ckn.equals("sshAccount#"+timeStamp)) {
					account = ck.getValue();
				}
				if( ckn.equals("sshPwd#"+timeStamp)) {
					pwd = ck.getValue();
				}
			}
			cm = new CommunicationHandler(quaggaIp, account, pwd);
		}

		
		handleAjaxRequest(cm, request, response);
	}

	private String getCurrentTimeStamp() {
		// TODO Auto-generated method stub
		
		Date createTime = new Date();


		Calendar cl=Calendar.getInstance();
		cl.setTime(createTime);
		int hour = cl.get(Calendar.HOUR_OF_DAY);
		int minute = cl.get(Calendar.MINUTE);
		int second = cl.get(Calendar.SECOND);
		String timeStamp = Integer.toString(hour)+ Integer.toString(minute)+ Integer.toString(second);
		
		return timeStamp;
	}

	private void handleAjaxRequest(CommunicationHandler cm, HttpServletRequest request, HttpServletResponse response) throws IOException {
		// TODO Auto-generated method stub
		
		response.setContentType("application/json");
		
		try {
			String peerDataListStr = cm.getPeerData(new ArrayList<PeerData>());	
			JSONObject peerDataJso;
			peerDataJso = new JSONObject(peerDataListStr);
			PrintWriter writer = response.getWriter();
			writer.println(peerDataJso);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
