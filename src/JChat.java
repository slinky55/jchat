
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.TreeMap;

public class JChat {
    /*
    List to hold chat threads
     */
    TreeMap<String, ChatThread> chatThreads = new TreeMap<>();
    String currentChat = "";

    private JFrame frame = new JFrame();

    /*
    Menu bar
     */
    private JMenuBar menuBar = new JMenuBar();
    private JMenu connectionMenu = new JMenu("Connect");
    private JMenuItem connectViaIp = new JMenuItem("Connect via IP");

    private JMenu chatsMenu = new JMenu("Chats");

    /*
    Connection menu
     */
    private JPanel connectPanel = new JPanel();
    private JTextField ipField = new JTextField(16);
    private JTextField portField = new JTextField(4);
    private JButton connectButton = new JButton("Connect");


    /*
    Chat view
     */
    private JTextArea chatView = new JTextArea();
    private JPanel messageBar = new JPanel();
    private JButton sendButton = new JButton("Send");
    private JTextField messageField = new JTextField(16);

    /*
    Networking
     */
    private ServerSocket serverSocket;
    private Socket socket;
    JChat() {
        frame.setSize(300, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setTitle("jchat");

        menuBar.add(connectionMenu);
        menuBar.add(chatsMenu);
        connectionMenu.add(connectViaIp);

        frame.add(menuBar, BorderLayout.NORTH);

        chatView.setEditable(false);

        messageBar.add(messageField);
        messageBar.add(sendButton);

        frame.add(BorderLayout.CENTER, chatView);
        frame.add(BorderLayout.SOUTH, messageBar);

        frame.setVisible(true);

        chatView.setVisible(false);
        messageBar.setVisible(false);

        // Connection screen
        frame.add(connectPanel, BorderLayout.CENTER);
        connectPanel.add(ipField);
        connectPanel.add(portField);
        connectPanel.add(connectButton);
        connectPanel.setVisible(false);

        // Listen for connections
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(7575);
                while (true) {
                    socket = serverSocket.accept();

                    ChatThread chat = chatThreads.put(socket.getInetAddress().toString(),
                            new ChatThread(
                                    socket,
                                    new BufferedReader(new InputStreamReader(socket.getInputStream())),
                                    new PrintWriter(socket.getOutputStream())
                            )
                    );

                    JMenuItem menuItem = chatsMenu.add(new JMenuItem(socket.getInetAddress().toString()));
                    chat.menuItem = menuItem;

                    menuItem.addActionListener(e1 -> {
                        currentChat = menuItem.getText();

                        connectPanel.setVisible(false);

                        chatView.setVisible(true);
                        messageBar.setVisible(true);

                        chatView.setText(chatThreads.get(currentChat).chatLog);
                        messageField.setText("");
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        // GUI update thread
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!currentChat.equals("")) {
                    chatView.setText(chatThreads.get(currentChat).chatLog);
                }
            }
        }).start();

        // Check for disconnects
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (String key : chatThreads.keySet()) {
                    if (!chatThreads.get(key).connected) {
                        chatsMenu.remove(chatThreads.get(key).menuItem);
                        chatThreads.remove(key);
                    }
                }
            }
        }).start();

        connectViaIp.addActionListener(e -> {
            connectPanel.setVisible(true);

            chatView.setVisible(false);
            messageBar.setVisible(false);
        });

        connectButton.addActionListener(e -> {
            try {
                Socket newSocket = new Socket(ipField.getText(), Integer.parseInt(portField.getText()));

                ChatThread chat = chatThreads.put(newSocket.getInetAddress().toString(),
                        new ChatThread(
                                newSocket,
                                new BufferedReader(new InputStreamReader(newSocket.getInputStream())),
                                new PrintWriter(newSocket.getOutputStream())
                        )
                );

                JMenuItem menuItem = chatsMenu.add(new JMenuItem(newSocket.getInetAddress().toString()));
                chat.menuItem = menuItem;

                menuItem.addActionListener(e1 -> {
                    currentChat = menuItem.getText();

                    connectPanel.setVisible(false);

                    chatView.setVisible(true);
                    messageBar.setVisible(true);

                    chatView.setText(chatThreads.get(currentChat).chatLog);
                    messageField.setText("");
                });

                ipField.setText("");
                portField.setText("");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        sendButton.addActionListener(e -> {
            ChatThread c = chatThreads.get(currentChat);
            c.chatLog += ("You: " + messageField.getText() + "\n");
            chatView.setText(c.chatLog);
            c.out.flush();
            c.out.println(messageField.getText());
            messageField.setText("");
        });
    }

    public static void main(String[] args) {
        new JChat();
    }
}
