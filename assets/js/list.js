$(document).ready(ajaxSetup());

$(function(){
    refresh();
});

// 全局Ajax设置, 用于session过期判断
function ajaxSetup() {
    $.ajaxSetup({
        timeout : 30000,
        beforeSend : function(xhr) {
            //添加ajax请求标识
            xhr.setRequestHeader("Ajax_request", "true");
        },
        complete : function(xhr, ts) {
            //xhr.statusText
            if (xhr.getResponseHeader('sessionState') == 'timeout' && xhr.status == 403) {
                //window.location.href = "login.jsp";
                console.log('jump');
            }
        }
    });
}

function down(a){
    var path = $("#path").text().trim();
    var str = path=='/' ? '' : path;
    str = 'list.action?path='+str+'/'+$(a).html();
    window.open(str);
}

function refresh(){
    var path = $("#path").text().trim();
    list(path);
}

function jump(a) {
    var str = '';
    if(a!=undefined){
        var path = $("#path").text().trim();
        if($(a).text().trim().endsWith('..')) {
            str = path + '..';
        }
        else{
            str = path=='/' ? '' : path;
            str = str+'/'+$(a).text();
        }
    }
    list(str);
}

function list(str){
    $.ajax({
        url : "/list.action",
        type : "GET",
        data : {path:str},
        dataType:"json",
        async : true,
        success : function(data) {
        $("ul").html("");
        if (data.path==''){
            $("#path").html('/&nbsp;&nbsp;');
        }else{
            $("#path").text(data.path);
            $("ul").append("<li><a href='javascript:void(0);' onclick=jump(this)>..&nbsp;&nbsp;&nbsp;&nbsp;</a></li>");
        }
        for (i in data.dirs) {
            var s="<li>"+$("#dir_ico").html()+"<a href='javascript:void(0);' onclick=jump(this)>" + data.dirs[i]+"</a></li>";
            $("ul").append(s);
        }
        for (i in data.files) {
            var str = $("#path").text()=='/' ? '' : $("#path").text();
            str = 'list.action?path='+str+'/'+data.files[i];
            var s="<li>"+$("#file_ico").html()+"<a href='"+str+"'>"+ data.files[i]+"</a></li>";
            $("ul").append(s);
        }
        }
    });
}


