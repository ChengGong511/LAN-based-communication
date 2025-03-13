# 项目简介
本项目为局域网聊天系统，包含服务端与客户端两部分，通过Socket实现消息收发。服务端提供统一的端口监听，客户端连接后即可进行群聊或私聊。

## 项目结构
- Sever.java — 服务端入口，启动后监听指定端口  
- APP.java — 客户端入口，运行后弹出登录界面  
- SeverReaderThread.java — 服务端读取并转发消息的线程  
- ClientReaderThread.java — 客户端读取服务端消息的线程  

## 运行方式
1. 先启动 `chat_sever.Sever` 等待客户端连接  
2. 运行 `APP` 打开客户端登录界面  
3. 输入昵称后登录，进入聊天窗口即可群发消息  

如需修改服务器IP或端口，请分别查看：  
- Constant.java 中的 `SERVER_IP` 和 `SERVER_PORT`  
- Constant.java 中的 `PORT`  