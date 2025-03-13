package chat_sever;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Sever {
    //定义一个集合容器，存储所有客户端连接的管道
    //使用map集合，键是客户端的socket管道，值是客户端的昵称
    public static final Map<Socket,String> onLineSockets= new HashMap<>();
    public static void main(String[] args) {
        System.out.println("服务端启动");
        try {
            //1.注册端口
            ServerSocket serverSocket=new ServerSocket(Constant.PORT);
            //2.子线程负责接收客户端连接
            while(true){
                //3.调用accept方法，等待客户端连接
                System.out.println("等待客户端连接");
                Socket socket=serverSocket.accept();
                //把这个管道交给一个独立的线程来处理，以便于主线程继续接收客户端连接
                new SeverReaderThread(socket).start();

                System.out.println("有客户端连接");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
