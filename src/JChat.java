
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.sql.Array;
import java.util.ArrayList;

public class JChat {
    ArrayList<Thread> connectionThreads = new ArrayList<>();

    String chatLog = "";

    private JFrame frame = new JFrame();

    private JMenuBar menuBar = new JMenuBar();
    private JButton connect = new JButton("Connect to host");
    private JButton host = new JButton("Host channel");

    private JTextArea chatView = new JTextArea();

    private JPanel messageBar = new JPanel();
    private JButton sendButton = new JButton("Send");
    private JTextField messageField = new JTextField(16);

    private ServerSocket serverSocket;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    JChat() {
        frame.setTitle("jchat");
        frame.setSize(300, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        menuBar.add(connect);

        chatView.setEditable(false);

        messageBar.add(messageField);
        messageBar.add(sendButton);

        frame.add(BorderLayout.NORTH, menuBar);
        frame.add(BorderLayout.CENTER, chatView);
        frame.add(BorderLayout.SOUTH, messageBar);

        frame.setVisible(true);

        sendButton.addActionListener(e -> {
            out.println(messageField.getText());
            chatLog += ("You: " + messageField.getText() + "\n");
            chatView.setText(chatLog);
            out.flush();
            messageField.setText("");
        });

        host.addActionListener(e -> {
            try {
                serverSocket = new ServerSocket(7575);
                socket = serverSocket.accept();
                chatLog += "User connected!\n";
                chatView.setText(chatLog);
                out = new PrintWriter(socket.getOutputStream());
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                new Thread(() -> {
                    String msg;
                    try {
                        msg = in.readLine();
                        while (msg != null) {
                            chatLog += ("Them: " + msg + "\n");
                            chatView.setText(chatLog);
                            msg = in.readLine();
                        }
                        chatLog += "User disconnected!\n";
                        chatView.setText(chatLog);
                        out.close();
                        socket.close();
                        serverSocket.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }).start();
            } catch (IOException ex) {
                chatView.setText("Failed to start server\n" + ex.getMessage());
            }
        });

        connect.addActionListener(e -> {
            try {
                socket = new Socket("127.0.0.1", 7575);
                chatLog += "Connected!\n";
                chatView.setText(chatLog);
                out = new PrintWriter(socket.getOutputStream());
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                new Thread(() -> {
                    String msg;
                    try {
                        msg = in.readLine();
                        while (msg != null) {
                            chatLog += ("Them: " + msg + "\n");
                            chatView.setText(chatLog);
                            msg = in.readLine();
                        }
                        chatLog += "User disconnected!\n";
                        chatView.setText(chatLog);
                        out.close();
                        socket.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }).start();
            } catch (IOException ex) {
                chatView.setText("Failed to connect to host\n" + ex.getMessage());
            }
        });
    }

    public static void main(String[] args) {
        new JChat();
    }
}
