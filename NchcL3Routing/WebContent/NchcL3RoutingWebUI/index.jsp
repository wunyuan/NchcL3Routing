<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>

<link rel="stylesheet" type="text/css" href ="NchcL3RoutingWebUI/css/index.css">
<script type="text/javascript" src="NchcL3RoutingWebUI/js/jquery-3.2.1.min.js"></script>
<script type="text/javascript" src="NchcL3RoutingWebUI/js/jquery.blockUI.js"></script>

</head>
<body>


<div id="logodiv">		
		<img class="logopic" src="NchcL3RoutingWebUI/img/nchc_logo.png">		
</div>

<div id="sshlogindiv">
	<h2>Login to Quagga :<br><br><font color="red" size="2pt">* is required field</font></h2>
	<form id="sshloginform">
		<table id="sshlogintable">
			<tr>
				<td><font color = "red">*</font>IP: <input id="quaggaip" type="text" name="quaggaip"  /></td>
				<td><font color = "red">*</font>Account: <input id="sshaccount" type="text" name="sshaccount" /></td>
				<td><font color = "red">*</font>Password: <input id="sshpwd" type="text" name="sshpwd" />
				<td><input type="hidden" name="loginflag" value="dologin"></td>
				<td><input class = "timestamp" type="hidden" name="timeStamp" value="${timeStamp}"></td>				
				<td><input type="button" id="sshsubmitbutton" value="send"/></td>
			</tr>
		</table>
	</form>
</div>



<div id="inputpeerdiv" style="display:none">
	<h2>Add a Peer:<br><br><font color="red" size="2pt">* is required field</font></h2>
	<form id="addpeerform">
		<table id="inputpeertable">
			<tr>
				<td><font color = "red">*</font>Peer IP: <input id="inputpeerip" type="text" name="peerip"  /></td>
				<td>
					Command: <select name="command">
						<option>connect</option>
						<option>disconnect</option>
				 	</select>
				</td>

				<td><font color = "red">*</font>Peer BGP-ID: <input id="inputbgpid" type="text" name="peerbgpid" /></td>
				<td><input type="hidden" name="mgnflag" value="domgn"></td>
				<td><input class = "timestamp" type="hidden" name="timeStamp" value="${timeStamp}"></td>
				<td><input type="button" id="submitbutton" value="send"/></td>

			</tr>

		</table>
	</form>
</div>

<div id="peerstatusdiv" style="display:none">
	<h2 id="peerstatush2">Neighbor status table</h2>
	
	<form id="refreshform">
		<input type="hidden" name="refreshflag" value="doflash">
		<input class = "timestamp" type="hidden" name="timeStamp" value="${timeStamp}">
		<button type="button" id="imagebutton" title="refresh data"><img id="refreshimg" src="NchcL3RoutingWebUI/img/refreshIcon.png"></button>
	</form>
	<table  id="peerstatustable">
		<thead>
			<tr>
				<th>Peer ID</th>
				<th>Bgp ID</th>
				<th>Status</th>
			</tr>
		</thead>
		<tbody id = "peerstatustbody">
		</tbody>
	</table>
</div>


<script id = 'peerstatusscript' data-peerdata = '${peerData}'>
	
	$(document).ready(function() {
		$.getScript("NchcL3RoutingWebUI/js/showPeerData.js");
		
		$("#submitbutton").click(function(){
			
			var peerip = $("#inputpeerip").val();
			var bgpid = $("#inputbgpid").val();
			if(peerip == "") {
				alert("No Peer ip");
			} else if(bgpid == "") {
				alert("No Bgp ID");
			} else {
				
				$.ajax({
					
					type:"POST",
					url:" ",
					data: $("#addpeerform").serialize(),
					
					dataType:"json",
					
					success : function(resp){
						$("#peerstatustbody").empty();
						autoCreatPeerStatusTable(resp.peerData);
					},
					beforeSend:function(){
						$.blockUI({
							message: '<img src="NchcL3RoutingWebUI/img/ajax-loader.gif"/><br>please wait for processing...',
							css: {
								height:'60px'
							}
							
						});
					},
					complete:function(){
						$.unblockUI();
					},
					error:function(xhr, ajaxOptions, thrownError){
						console.log(xhr.status);
						console.log(thrownError);
					}				
				});
				
			}
			
						
		});
		
		
		
		
		$("#imagebutton").click(function(){
			
			$.ajax({
				
				type:"POST",
				url:" ",
				data: $("#refreshform").serialize(),
				
				dataType:"json",
				
				success : function(resp){
					$("#peerstatustbody").empty();
					autoCreatPeerStatusTable(resp.peerData);
				},
				beforeSend:function(){
					$.blockUI({
						message: '<img src="NchcL3RoutingWebUI/img/ajax-loader.gif"/><br>please wait for processing...',
						css: {
							height:'60px'
						}
						
					});
				},
				complete:function(){
					$.unblockUI();
				},
				error:function(xhr, ajaxOptions, thrownError){
					console.log(xhr.status);
					console.log(thrownError);
				}				
			});			
		});
		
		
		$("#sshsubmitbutton").click(function(){
			
			$.ajax({
				
				type:"POST",
				url:" ",
				data: $("#sshloginform").serialize(),
				
				dataType:"json",
				
				success : function(resp){
					$("#peerstatustbody").empty();
					autoCreatPeerStatusTable(resp.peerData);
				},
				beforeSend:function(){
					$.blockUI({
						message: '<img src="NchcL3RoutingWebUI/img/ajax-loader.gif"/><br>please wait for processing...',
						css: {
							height:'60px'
						}
						
					});
				},
				complete:function(){
					$.unblockUI();
					$("#inputpeerdiv").css({"display":"block"});
					$("#peerstatusdiv").css({"display":"block"});
				},
				error:function(xhr, ajaxOptions, thrownError){
					console.log(xhr.status);
					console.log(thrownError);
				}				
			});			
		});
		
		
		
	});
	


</script>


</body>
</html>