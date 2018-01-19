$(document).ready(ajaxSetup());

$(function(){
    list("/");
});

// 全局Ajax设置, 用于session过期判断
function ajaxSetup() {
    $.ajaxSetup({
        timeout : 10000,
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

function list(str) {
    $.ajax({
        url : "/list.action",
        type : "GET",
        data : {path:str},
        dataType:"json",
        async : true,
        success : function(data) {
        $("#path").html(data.path);
        $("ul").html("");
        $("ul").append("<li><a href='#'>..</a></li>");
        for (i in data.list) {
            var s="<li><a href='#'>"+data.list[i]+"</a></li>";
            $("ul").append(s);
        }
        $("li a").click(function(){
            var path=this.innerHTML;
            if(path=='..'){
                list($("#path").html()+'..')
            }else{
                list(path);
            }
        });
        },
        error : function(a,b,c) {
            console.log(c);
        }

    });
}