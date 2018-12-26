var peerDataJson = $('#peerstatusscript').data('peerdata');
 
autoCreatPeerStatusTable(peerDataJson.peerData);

function autoCreatPeerStatusTable(peerJsonArray) {
	
	$.each(peerJsonArray, function(index, jsonObject){
		var peerid = jsonObject.peerID;
		var peerstate = jsonObject.state;
		var peerbgpid = jsonObject.bgpID;
		var appendStr = "<tr><td>"+ peerid+ "</td><td>"+ peerbgpid+ "</td><td>"+ peerstate+ "</td></tr>";
		$('#peerstatustable tbody').append(appendStr);

	});
	
}