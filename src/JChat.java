
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.*;
import java.util.ArrayDeque;
import java.util.ArrayList;

public class JChat {

    ArrayDeque<Message> outgoing = new ArrayDeque<>();
    ArrayDeque<Message> incoming = new ArrayDeque<>();

    public boolean connected = false;

    // UI Objects
    public JFrame frame = new JFrame();

    public JMenuBar menuBar = new JMenuBar();
    public JButton connect = new JButton("Connect to host");
    public JButton host = new JButton("Host channel");

    public JPanel chatView = new JPanel();
    public JTextField counter = new JTextField(3);

    public JPanel messageBar = new JPanel();
    public JButton sendButton = new JButton("Send");
    public JTextField messageField = new JTextField(16);

    public ServerSocket serverSocket;
    public Socket socket;

    JChat() {
        frame.setTitle("jchat");
        frame.setSize(300, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        menuBar.add(host);
        menuBar.add(connect);

        chatView.add(counter);

        messageBar.add(messageField);
        messageBar.add(sendButton);

        frame.add(BorderLayout.NORTH, menuBar);
        frame.add(BorderLayout.CENTER, chatView);
        frame.add(BorderLayout.SOUTH, messageBar);

        frame.setVisible(true);

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                outgoing.add(new Message(messageField.getText()));
                messageField.setText("");
            }
        });
    }

    public void run() {
        while (true) {
            counter.setText(String.valueOf(outgoing.size()));

            for (Message m : outgoing) {
                System.out.println(m.text);
                outgoing.remove(m);
            }
        }
    }
}
