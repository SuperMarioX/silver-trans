## silver-trans是什么？
silver-trans是一个文件传输服务,

## silver-trans能干什么？

#### 1.使用命令操作，在服务器之间通过TCP传输文件
  
|          命令      |                 | 
| ----------------- |:-------------------|
| connect ip:port   | 连接到主机|
| disconnect        | 断开连接|
| exit              | 退出程序|
| pwd               | 显示当前路径|
| cd                | 进入目录|
| ls                | 显示当前目录文件列表|
| get               | 获取文件|


#### 2.使用API方式调用，在服务器之间通过TCP传输文件

```java
TransApi.getFile(src, dst);
TransApi.sentFile(src, dst);
```

#### 3.通过浏览器上传和下载文件


***

[![Build Status](https://travis-ci.org/luangeng/silver-trans.svg?branch=master)](https://travis-ci.org/luangeng/silver-trans)
