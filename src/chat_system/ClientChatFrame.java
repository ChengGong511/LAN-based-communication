package chat_system;


import javax.swing.*;
import java.awt.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientChatFrame extends JFrame {
    private String nickname;
    private Socket socket;
    public JTextArea smsContent = new JTextArea(23, 50);
    private JTextArea smsSend = new JTextArea(4, 40);
    public  JList<String> onLineUsers = new JList<>();
    private JButton sendBn = new JButton("发送");

    public ClientChatFrame(){
        initView();
        this.setVisible(true);
    }

    public ClientChatFrame(String nickname, Socket socket) {
        this();//调用无参构造方法,初始化界面
        this.setTitle("局域网聊天室-" + nickname);
        this.socket = socket;
        //立即把客户端的这个Socket管道交给一个独立的线程，专门负责读取客户端socket从服务端收到的在线人数更新数据或者聊天消息
        new ClientReaderThread(socket,this).start();
    }

    private void initView() {
        this.setSize(700, 600);
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 关闭窗口，退出程序
        this.setLocationRelativeTo(null); // 窗口居中


        // 设置窗口背景色
        this.getContentPane().setBackground(new Color(0xf0, 0xf0, 0xf0));

        // 设置字体
        Font font = new Font("SimKai", Font.PLAIN, 14);

        // 消息内容框
        smsContent.setFont(font);
        smsContent.setBackground(new Color(0xdd, 0xdd, 0xdd));
        smsContent.setEditable(false);

        // 发送消息框
        smsSend.setFont(font);
        smsSend.setWrapStyleWord(true);
        smsSend.setLineWrap(true);

        // 在线用户列表
        onLineUsers.setFont(font);
        onLineUsers.setFixedCellWidth(120);
        onLineUsers.setVisibleRowCount(13);

        // 创建底部面板
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(0xf0, 0xf0, 0xf0));

        // 消息输入框
        JScrollPane smsSendScrollPane = new JScrollPane(smsSend);
        smsSendScrollPane.setBorder(BorderFactory.createEmptyBorder());
        smsSendScrollPane.setPreferredSize(new Dimension(500, 50));

        // 发送按钮
        sendBn.setFont(font);
        sendBn.setBackground(Color.decode("#009688"));
        sendBn.setForeground(Color.WHITE);

        // 按钮面板
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        btns.setBackground(new Color(0xf0, 0xf0, 0xf0));
        btns.add(sendBn);

        //给发送按钮添加监听器
        sendBn.addActionListener(e -> {
            //获取发送消息框的内容
            String msg = smsSend.getText();
            //把消息发送到服务端
            sendMsgToServer(msg);
            //清空发送消息框
            smsSend.setText("");
        });

        // 添加组件
        bottomPanel.add(smsSendScrollPane, BorderLayout.CENTER);
        bottomPanel.add(btns, BorderLayout.EAST);

        // 用户列表面板
        JScrollPane userListScrollPane = new JScrollPane(onLineUsers);
        userListScrollPane.setBorder(BorderFactory.createEmptyBorder());
        userListScrollPane.setPreferredSize(new Dimension(120, 500));

        // 中心消息面板
        JScrollPane smsContentScrollPane = new JScrollPane(smsContent);
        smsContentScrollPane.setBorder(BorderFactory.createEmptyBorder());

        // 添加所有组件
        this.add(smsContentScrollPane, BorderLayout.CENTER);
        this.add(bottomPanel, BorderLayout.SOUTH);
        this.add(userListScrollPane, BorderLayout.EAST);
    }

    public static void main(String[] args) {
        new ClientChatFrame();
    }
    public void updateOnlineUsers(String[] onlineUsers) {
        //把这个在线用户列表的数据模型设置到界面的JList组件上
        onLineUsers.setListData(onlineUsers);
    }
    public void setMsgToWin(String msg) {
        //把服务端发来的消息显示到客户端的消息显示框
        smsContent.append(msg);
    }
    public void sendMsgToServer(String msg) {
        //把消息发送到服务端
        try {
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            dos.writeInt(2);//代表发送的是群发消息
            dos.writeUTF(msg);
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
