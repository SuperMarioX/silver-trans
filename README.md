## silver-trans是什么？[![Build Status](https://travis-ci.org/luangeng/silver-trans.svg?branch=master)](https://travis-ci.org/luangeng/silver-trans)
silver-trans是一个文件服务器，也可以用来在主机之间传输文件。

## silver-trans能干什么？

##### 1.使用命令操作，在主机之间通过TCP传输文件
  
|          命令      |                 | 
| ----------------- |:-------------------|
| connect ip:port   | 连接到主机|
| disconnect        | 断开连接|
| exit              | 退出程序|
| pwd               | 显示当前路径|
| cd                | 进入目录|
| ls                | 显示当前目录文件列表|
| get               | 获取文件|


##### 2.使用API方式调用，在主机之间通过TCP传输文件

```java
TransApi.getFile(src, dst);
TransApi.sentFile(src, dst);
```

##### 3.通过浏览器上传文件到主机或从主机下载
<img src="https://github.com/luangeng/Test/blob/master/silver-trans.png" alt="silver-trans" align=center />


## silver-trans如何使用？
你可以直接运行启动脚本作为文件服务器，也可以添加为依赖使用API调用， It's up to you.


## 依赖
* [Netty](https://github.com/netty/netty)
* [resumable.js](https://github.com/23/resumable.js)
* [Gson](https://github.com/google/gson)

***
