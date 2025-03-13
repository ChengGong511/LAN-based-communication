package chat_system;

import chat_sever.Sever;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ClientReaderThread extends Thread {
    private Socket socket;
    private DataInputStream dis;
    private ClientChatFrame win;
    public ClientReaderThread(Socket socket,ClientChatFrame win ) {

        this.socket = socket;
        this.win = win;
    }

    public void run() {
        try {
            //读取管道消息，1.在线人数更新的消息2.群发消息 3.私聊消息
             dis = new DataInputStream(socket.getInputStream());
            while (true) {
                int type = dis.readInt();//读取一个整数，代表消息类型
                switch (type) {
                    case 1:
                        //服务端发来的在线人数更新的消息
                        updateClientOnLineUserList();

                        break;
                    case 2:
                        //服务端发来的群发消息
                        getMsgToWin();
                        break;
                    case 3:
                        //私聊消息
                        //读取消息内容，再私聊转发到指定客户端
                        break;

                }
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
    }


    private void getMsgToWin()throws IOException {
       //获取群聊消息
        String msg=dis.readUTF();
        //将消息显示到客户端的消息框
        win.setMsgToWin(msg);
    }

    private void updateClientOnLineUserList() throws IOException {
        //1.读取多少个在线用户
        int size = dis.readInt();
        //2.循环控制读取多少个在线用户
        String[] onLineUser = new String[size];
        for (int i = 0; i < size; i++) {
            String nickName = dis.readUTF();
            //4.将每个用户名添加到数组中
            onLineUser[i] = nickName;
        }
       //5.将集合容器中的在线用户名称显示到客户端的在线用户列表
        win.updateOnlineUsers(onLineUser);

    }
}



