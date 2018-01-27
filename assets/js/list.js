//$(document).ready();
String.prototype.format = function(args) {
	var result = this;
	if (arguments.length > 0) {
		if (arguments.length == 1 && typeof (args) == "object") {
			for (var key in args) {
				if(args[key]!=undefined){
					var reg = new RegExp("({" + key + "})", "g");
					result = result.replace(reg, args[key]);
				}
			}
		}
		else {
			for (var i = 0; i < arguments.length; i++) {
				if (arguments[i] != undefined) {
					var reg = new RegExp("({[" + i + "]})", "g");
					result = result.replace(reg, arguments[i]);
				}
			}
		}
	}
	return result;
}

$(function() {
	ajaxSetup();
	list('/');
});

function ajaxSetup() {
	$.ajaxSetup({
		timeout : 30000,
		beforeSend : function(xhr) {
		//var left=$("#list_tr").position().left+$("#list_tr").width()/2-20;
		//var top=$("#list_tr").position().top + 20;
		    //$("#loading").css('left',left).css('top',top).show();
			xhr.setRequestHeader("Ajax_request", "true");
		},
		complete : function(xhr, ts) {
			if (xhr.getResponseHeader('sessionState') == 'timeout'
					&& xhr.status == 403) {
				console.log('jump');
			}
			$("#loading").fadeOut();
		}
	});
}

function jump(a) {
	var str = '';
	if (a != undefined) {
		var path = $("#path").val();
		if ($(a).text().trim().endsWith('..')) {
			str = path + '..';
		} else {
			str = path == '/' ? '' : path;
			str = str + '/' + $(a).text();
		}
	}
	list(str);
}

function list(str) {
    var tr = "<tr><td class='td1'>{0}.</td><td class='td2'>{1}</td><td class='td3'>{2}</td><td class='td4'>{3}</td></tr>";
    var index=0;
	$.ajax({
    	url : "/list.action",
    	type : "GET",
    	data : {
    		path : str
    	},
    	dataType : "json",
    	async : true,
    	success : function(data) {
    		$("table").html("");
    		if (data.path.length==0) {
    			$("#path").html('/&nbsp;&nbsp;').val('');
    		} else {
    			$("#path").text(data.path).val(data.path);
    			var s = tr.format(index, "<a href='javascript:void(0);' onclick=jump(this)>..&nbsp;&nbsp;&nbsp;</a>",'','','');
    			$("table").append(s);
    		}
    		for (i in data.dirs) {
    		    index++;
    		    var name=$("#dir_ico").html()+"<a href='javascript:void(0);' onclick=jump(this)>"+ data.dirs[i].name + "</a>";
    			var s=tr.format(index, name,"",data.dirs[i].date, $("#delete_ico").html());
    			$("table").append(s);
    		}
    		for (i in data.files) {
    		    index++;
    			var str = 'list.action?path=' + $("#path").val() + '/' + data.files[i].name;
    			var name = $("#file_ico").html()+"<a href='" + str + "'>" + data.files[i].name + "</a>";
    			var s = tr.format(index, name, data.files[i].size, data.files[i].date, $("#delete_ico").html());
    			$("table").append(s);
    		}
    	}
    });
}
