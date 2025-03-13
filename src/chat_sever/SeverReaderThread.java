package chat_sever;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

public class SeverReaderThread extends Thread {
    private Socket socket;

    public SeverReaderThread(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            //读取管道消息，1.登录消息，包含昵称 2.群发消息 3.私聊消息
            //客户端声明协议发送消息
            //比如客户端先发1，代表登录消息，然后发送昵称
            //客户端先发2，代表群发消息，然后发送消息内容
            //先从Socket管道中接收客户端发送消息的类型
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            while (true) {
                int type = dis.readInt();//读取一个整数，代表消息类型
                switch (type) {
                    case 1:
                        //登录消息
                        //读取昵称,再更新全部在线客户端在线人数列表
                        String nickName = dis.readUTF();
                        //把登录成功的客户端socket存储到集合容器中
                        Sever.onLineSockets.put(socket, nickName);
                        //更新全部在线客户端在线人数列表
                        updateClientOnLineUserList();
                        break;
                    case 2:
                        //群发消息
                        //读取消息内容，再群发到所有在线客户端
                        String msg = dis.readUTF();
                        sendMsgToAllClient(msg);
                        break;
                    case 3:
                        //私聊消息
                        //读取消息内容，再私聊转发到指定客户端
                        break;

                }
            }

        } catch (Exception e) {
            System.out.println("客户端下线了" + socket.getInetAddress());
            //客户端下线了，从集合容器中删除这个socket
            Sever.onLineSockets.remove(socket);
            //更新全部在线客户端在线人数列表
            updateClientOnLineUserList();

        }
    }


    private void sendMsgToAllClient(String msg) {
        //把消息拼装成协议格式，再转发给全部在线客户端
        StringBuilder sb = new StringBuilder();
        //1.获取消息的发送者昵称
        String nickName = Sever.onLineSockets.get(socket);
        //2.拿到当前时间
        LocalDateTime time = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss EEE a");
        String now = dtf.format(time);
        //3.拼装消息
        String msgResult = sb.append(nickName).append(" ").append(now).append("\r\n")
                .append(msg).append("\r\n").toString();
        for (Socket socket : Sever.onLineSockets.keySet()) {
            try {
                //3.把集合容器中的在线用户名称全部转发给在线socket管道
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                dos.writeInt(2);
                dos.writeUTF(msgResult);
                dos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateClientOnLineUserList() {
        //更新全部在线客户端在线人数列表
        //拿到全部在线客户端的用户名称，把这些名称全部转发给在线socket管道
        //1.拿到当前全部在线用户名称
        Collection<String> onLineUsers = Sever.onLineSockets.values();
        //2.把这些名称全部转发给在线socket管道
        for (Socket socket : Sever.onLineSockets.keySet()) {
            try {
                //3.把集合容器中的在线用户名称全部转发给在线socket管道
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                dos.writeInt(1);//1 代表客户端接收的是在线用户列表，2代表群发消息，3代表私聊消息
                dos.writeInt(onLineUsers.size());//在线用户数量
                for (String name : onLineUsers) {
                    dos.writeUTF(name);
                }
                dos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

