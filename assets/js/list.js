$(document).ready(ajaxSetup());

$(function(){
    list();
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
    var str = $("#path").html()=='/' ? '' : $("#path").html();
    str = 'list.action?path='+str+'/'+$(a).html();
    window.open(str);
}

function list(a) {
    var str = '';
    if(a!=undefined){
        if($(a).html().endsWith('...')) {
            str = $("#path").html() + '...';
        }
        else{
            str = $("#path").html()=='/' ? '' : $("#path").html();
            str = str+'/'+$(a).html();
        }
    }
    $.ajax({
        url : "/list.action",
        type : "GET",
        data : {path:str},
        dataType:"json",
        async : true,
        success : function(data) {
        $("ul").html("");
        if (data.path==''){
            $("#path").html('/');
        }else{
            $("#path").html(data.path);
            $("ul").append("<li><a href='javascript:void(0);' onclick=list(this)>...</a></li>");
        }
        for (i in data.dirs) {
            var s="<li><a href='javascript:void(0);' onclick=list(this)>" + data.dirs[i]+"</a></li>";
            $("ul").append(s);
        }
        for (i in data.files) {
        var str = $("#path").html()=='/' ? '' : $("#path").html();
            str = 'list.action?path='+str+'/'+data.files[i];
             var s="<li><a href='"+str+"'>"+ data.files[i]+"</a></li>";
             $("ul").append(s);
        }
        }
    });
}


