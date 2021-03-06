// 替换字符串{0} {1} {2} 为参数中的值
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

var listData;

$(function() {
	ajaxSetup();
	list('/');
});

function ajaxSetup() {
	$.ajaxSetup({
		timeout : 30000,
		beforeSend : function(xhr) {
		    $("#loading").show();
			xhr.setRequestHeader("Token", getCookie("token"));
		},
		complete : function(xhr, ts) {
			if (xhr.getResponseHeader('auth') == 'forbidden' && xhr.status == 401) {
			    auth();
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
			str = path + '/' + $(a).text();
		}
	}
	list(str);
}

function download(a){
}

function view(a){
    var path = $("#path").val();
    var str = path + '/' + a;
    window.open("view.html?path="+str);
}

function del(){

}

function list(str) {
    var tr = "<tr><td class='td0'>{0}.</td><td class='td1'>{1}</td><td class='td2'>{2}</td><td class='td3'>{3}</td></tr>";
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
    	    listData = data;
    		$("table").html("");
    		$("table").append(tr.format("No","Title","Size","Last Update Date"));
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
    			var s=tr.format(index, name,"",data.dirs[i].date, '');
    			$("table").append(s);
    		}
    		for (i in data.files) {
    		    index++;
    			var str = 'list.action?path=' + $("#path").val() + '/' + data.files[i].name+'&token='+getCookie('token');
    			var name = $("#file_ico").html()+"<a href='" + str + "'>" + data.files[i].name + "</a>";
    			var view = "<a onclick=view(listData.files[i].name)>View</a>";
    			var s = tr.format(index, name, data.files[i].size, data.files[i].date, view);
    			$("table").append(s);
    		}
    	},
    	error : function(a,b,c){
    	    alert(a.responseText);
    	}
    });
}

function getCookie(c_name) {
    if (document.cookie.length>0) {
        c_start=document.cookie.indexOf(c_name + "=");
        if (c_start!=-1) {
            c_start=c_start + c_name.length+1;
            c_end=document.cookie.indexOf(";",c_start);
            if (c_end==-1) {c_end=document.cookie.length;}
            return unescape(document.cookie.substring(c_start,c_end));
        }
    }
    return ""
}

function setCookie(c_name,value,expiredays) {
    if (expiredays==null) {
        expiredays = "";
    } else {
        var exdate=new Date();
        exdate.setDate(exdate.getDate()+expiredays);
        expiredays = "; expires="+exdate.toGMTString();
    }
    document.cookie=c_name+ "=" +escape(value)+ expiredays;
}

function auth(){
     auth=prompt('Password:',"");
     if (auth!=null && auth!="") {
        auth = auth+parseInt(new Date().getTime()/100000);
         $.ajax({
             	url : "/auth.action",
             	type : "POST",
             	data : hex_md5(auth),
             	dataType : "text",
             	async : true,
             	success : function(data) {
             	    setCookie("token", data, 1);
             	    location.reload();
             	  },
             	error:function(a,b,c){
             	    alert(c)
             	}
             	});
     }
}