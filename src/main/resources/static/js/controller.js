var tabs = $( "#tabs" ).tabs();
var ul = tabs.find( "ul" );

tabs.on( "click", "span.ui-icon-close", function() {
    var panelId = $( this ).closest( "li" ).remove().attr( "aria-controls" );
    quitChatroom(panelId);
    var chatroomid = panelId.substring(8);
    $( "#tab" + panelId ).remove();
    $( "#" + panelId ).remove();
    $( "#own" + panelId).remove();
    $( '#joined' + chatroomid).remove();
    tabs.tabs( "refresh" );
});

function quitChatroom(id) {
	$.ajax({
		url: 'chatrooms/' + id.substring(8),
		type: 'DELETE'
	});
};

function seeJoiner(chatroomname, id) {
	var chatroomname = chatroomname;
	var chatroomid = id;
	if (id.length > 9) 
		chatroomid = id.substring(11);
	$.ajax({
		url: 'chatrooms/' + chatroomid + '/users',
		type: 'GET'
		})
		.done(function( data ) {
			var result = " :: ";
			$.each(data, function(i, item) {
				if (item.nickname != null)
					result += item.nickname + " :: ";
			});
			sweetAlert(chatroomname + ' :: Users', result);
		})
		.fail(function( data, textStatus, xhr) {
			if (data.responseText == 'Session is end!') {
				sessionEnd();
			} else {
				sweetAlert("ERROR!", data.responseText, textStatus);
			}
		});
};

function getMessages(id) {
	var chatroomid = id.substring(11);

	var element = document.getElementById(chatroomid + 'table');
	while (element.rows.length > 0) {
		element.deleteRow(0);
	}
	$.ajax({
		url: 'chatrooms/' + chatroomid + '/messages',
		type: 'GET'
	})
	.done(function( data, textStatus, xhr ) {
		var myuserid = $( '#myuserid' ).val();
		$.each(data, function(i, item) {
			if (item.receiverid == 0)
				addMessage(chatroomid, "** " + item.sendernickname + " **    " + item.messagebody, item.senderid, item.receiverid, item.sendernickname, '000000');
			else if (item.receiverid == myuserid || item.senderid == myuserid)
				addMessage(chatroomid, "** " + item.sendernickname + " **    " + item.messagebody, item.senderid, item.receiverid, item.sendernickname, 'FF0000');
		});
	});
};

function joinChatroom(id) {
	$.post("chatrooms/" + id.substring(1) + "/users", {
	})
	
	.done(function( data, textStatus, xhr ) {
		makeNewTab(data);
		makeNewTable(data);
		subscribeChatroom(id.substring(1));
		addJoinedChatroom(data.chatroomid, data.chatroomname);
		swal({
			title: "OK",
			text: "Join Chatroom Success",
			type: "success"
		})
	})
	.fail(function( data, textStatus, xhr ) {
		if (data.responseText == 'Session is end!') {
			sessionEnd();
		} else {
			sweetAlert("ERROR!", data.responseText, textStatus);
		}
	});
};

function addMessage(id, data, senderid, receiverid, nickname, color) {
	var element = document.getElementById(id + 'table');
	$('<tr> <td> <a class="for-whisper" href="#whisper-modal" role="button" data-nickname="' + nickname + '" data-senderid="' + senderid + '" data-receiverid="' + receiverid + '" data-chatroomid="' + id + '" data-toggle="modal"> <font color="' + color + '"> ' + data + '</font></a> </td> </tr>').appendTo(element);
	var obj = document.getElementById("div" + id);
	obj.scrollTop = obj.scrollHeight;
};

function refreshWhisper() {
	var element = $('#whisper-modal');
	var chatroomid = $('#chatroomidwhisper').val();
	var senderid = $('#senderid').val();
	var receiverid = $('#receiverid').val();
	var sendernickname = $('#sendernickname').val();
	var element = $("#whisperarea");
	element.val("");

	$.ajax({
		url: 'chatrooms/' + chatroomid + '/messages/whisper/' + senderid,
		type: 'GET'
	})
	.done(function( data, textStatus, xhr ) {
		$.each(data, function(i, item) {
			addWhisper(chatroomid, "** " + item.sendernickname + " **    " + item.messagebody, item.senderid, item.receiverid);
		});
	});
}

$(document).on("click", ".for-whisper", function() {
	var chatroomid = $(this).data('chatroomid');
	var senderid = $(this).data('senderid');
	var receiverid = $(this).data('receiverid');
	var sendernickname = $(this).data('nickname');
	$(".modal-body #receiverid").val(receiverid);
	$(".modal-body #senderid").val(senderid);
	$(".modal-body #chatroomidwhisper").val(chatroomid);
	$(".modal-body #sendernickname").val(sendernickname);
	var element = $("#whisperarea");
	element.val("");
	
	$.ajax({
		url: 'chatrooms/' + chatroomid + '/messages/whisper/' + senderid,
		type: 'GET'
	})
	.done(function( data, textStatus, xhr ) {
		$.each(data, function(i, item) {
			addWhisper(chatroomid, "** " + item.sendernickname + " **    " + item.messagebody, item.senderid, item.receiverid);
		});
	});
	$('#whisper-modal').modal('show');
});

function addWhisper(id, data, senderid, receiverid) {
	var element = $("#whisperarea");
	element.val(element.val() + data + '\n');
	var obj = document.getElementById("whisperarea");
	obj.scrollTop = obj.scrollHeight;
};

function postWhisper(id, data, senderid, receiverid) {
	stompClient.send('/chatrooms/' + id + '/messages', {}, JSON.stringify(
			{
				'messagebody': data,
				'receiverid': senderid
			}));
	$('#whisper-form').val("");
};

function sendwhisper() {
	var whisper = $('#whisper-form').val();
	var receiverid = $('#receiverid').val(); <!-- Not use -->
	var chatroomid = $('#chatroomidwhisper').val();
	var senderid = $('#senderid').val();
	var sendernickname = $('#sendernickname').val();
	var nickname = $('#mynickname').val();
	postWhisper(chatroomid, whisper, senderid, receiverid);
};

function makeNewTable(data) {
	var element = document.getElementById('chatroom' + data.chatroomid);
	/*<![CDATA[*/
	var temp = 'onclick="javascript:seeJoiner(&quot;' + data.chatroomname + '&quot;, ' + data.chatroomid + ')"';
	
	$('<div id="div' + data.chatroomid + '" style="width:100%; height:800px; overflow:auto"> <table id="' + data.chatroomid + 'table"  width="100%" border="0" cellspacing="0" cellpadding="0"> </table> </div>' + '<form> <button id="chatroomuser" type="button" class="btn btn-default" ' + temp + '>Users</button>' + '<input name="submitmsg" type="button" id="' + data.chatroomid + '" onclick="javascript:sendMessage(this.id)" class="btn btn-default" value="Send"/>' + '<input name="msg" type="text" id="msg' + data.chatroomid + '" size="80" class="form-control"/> </form>').appendTo(element);
	/*]]>*/
}

function addJoinedChatroom(chatroomid, chatroomname) {
	var element = document.getElementById('joinedchatroom');
	var temp = 'onclick="javascript:seeJoiner(&quot;' + chatroomname + '&quot;, ' + chatroomid + ')"';
	/*<![CDATA[*/
	$('<tbody> <tr class="bg-success"> <td id="joined' + chatroomid + '" ' + temp + '>' + chatroomname + '</td> </tr> </tbody>').appendTo(element);
	/*]]>*/
}

function addChatroom(chatroomid, chatroomname) {
	var element = document.getElementById('allchatroom');
	var temp = 'onclick="javascript:joinChatroom(this.id)"';
	/*<![CDATA[*/
	$('<tbody> <tr class="bg-success"> <td class="forRight" id="c' + chatroomid + '" ' + temp + '>' + chatroomname + '</td> </tr> </tbody>').appendTo(element);
	/*]]>*/
}

function makeNewTab(data) {
    $('div#tabs ul').append(
        '<li class="tab"><a href="#chatroom' + data.chatroomid + '">' + data.chatroomname + '</a> <span class="ui-icon ui-icon-close"></span> </li>');
    $('div#tabs').append(
        '<div id="chatroom' + data.chatroomid + '"></div>');
    $('#tabs').tabs("refresh");
};

function insertContent(content) {
    var activeTab = $("#tabs").tabs('option', 'active');   
    activeTab += 1;   
    $("#tab-" + activeTab).append(content);
}

$('#createchatroom').click(function() {
	var chatroomname = $('#chatroomname').val();				
	$.post("chatrooms", {
		chatroomname: chatroomname
	})
	.done(function( data, textStatus, xhr ) {
		makeNewTab(data);
		makeNewTable(data);
		addJoinedChatroom(data.chatroomid, data.chatroomname);
		subscribeChatroom(data.chatroomid);
		swal({
			title: "OK",
			text: "Create Chatroom Success",
			type: "success"
		})
	})
	.fail(function( data, textStatus, xhr ) {
		if (data.responseText == 'Session is end!') {
			sessionEnd();
		} else {
			sweetAlert("ERROR!", data.responseText, textStatus);
		}
	});
});

function sessionEnd() {
	swal({
		title: "Error",
		text: "Session is end! return to Login",
		type: "error"
	},
	function() {
		window.location.replace("http://10.10.44.71:9090/");
	});
};

$('#nicknamechange').click(function() {
	var nickname = $('#nickname').val();				
	$.ajax({
		type: 'PUT',
		url: 'users/' + nickname,
	})
	.done(function( data, textStatus, xhr ) {
		swal({
			title: "OK",
			text: "Nickname changed successfully to " + data.nickname,
			type: "success"
		})
	})
	.fail(function( data, textStatus, xhr ) {
		if (data.responseText == 'Session is end!') {
			sessionEnd();
		} else {
			sweetAlert("ERROR!", data.responseText, textStatus);
		}
	});
});

$('.signout').click(function() {
	$.post("signout", {
	})
	.done(function( data, textStatus, xhr ) {
		swal({
			title: "OK",
			text: "Sign out",
			type: "success"
		},
		function() {
			window.location.replace("http://10.10.44.71:9090/");
		});
	})
	.fail(function( data, textStatus, xhr ) {
		sweetAlert("ERROR!", data.responseText, textStatus);
	});
});

var stompClient = null;

function sendMessage(id) {
	var message = document.getElementById('msg' + id).value;
	stompClient.send('/chatrooms/' + id + '/messages', {}, JSON.stringify({'messagebody': message}));
	$( '#msg' + id ).val("");
};

function connect() {
	var socket = new SockJS('http://10.10.44.71:9090/websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({
        }, function (frame) {
        console.log('Connected: ' + frame);
		initialize();
		subscribeChatroomList();
    });
}

function subscribeChatroom(chatroomid) {
	stompClient.subscribe('/chatrooms/' + chatroomid, function (message) {
		var messagebody = JSON.parse(message.body).messagebody;
		var receiverid = JSON.parse(message.body).receiverid;
		var senderid = JSON.parse(message.body).senderid;
		var nickname = JSON.parse(message.body).sendernickname;
		var chatroomid = JSON.parse(message.body).chatroomid;
		var myuserid = $( '#myuserid' ).val();
		/*<![CDATA[*/
		if (receiverid == 0) {
			addMessage(chatroomid, "** " + nickname + " **    " + messagebody, senderid, receiverid, nickname, '100000');
		} else if (receiverid == myuserid) {
			addWhisper(chatroomid, "** " + nickname + " **    " + messagebody, senderid, receiverid);
			addMessage(chatroomid, "** " + nickname + " **    " + messagebody, senderid, receiverid, nickname, 'FF0000');
		} else if (senderid == myuserid && receiverid != 0) {
			addMessage(chatroomid, "** " + nickname + " **    " + messagebody, senderid, receiverid, nickname, 'FF0000');
		}
		/*]]>*/
	});
}

function deleteChatroom(id) {
    $( '#c' + id).remove();
}

function subscribeChatroomList() {
	stompClient.subscribe('/chatrooms/change', function (data) {
		if (JSON.parse(data.body).isexist == true) {
			addChatroom(JSON.parse(data.body).chatroomid, JSON.parse(data.body).chatroomname);
		} else {
			deleteChatroom(JSON.parse(data.body).chatroomid);
		}
	});
}

$( document ).ready(function(){
	connect();
});
