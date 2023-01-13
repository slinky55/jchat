
import javax.swing.*;
import java.awt.*;


public class JChat {

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setTitle("JChat");
        frame.setSize(300, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JMenuBar menu = new JMenuBar();
        menu.add(new JButton("Logout"));

        JPanel chat = new JPanel();

        JPanel b = new JPanel();
        b.add(new JTextField(15));
        b.add(new JButton("Send"));

        frame.getContentPane().add(BorderLayout.NORTH, menu);
        frame.getContentPane().add(BorderLayout.CENTER, chat);
        frame.getContentPane().add(BorderLayout.SOUTH, b);

        frame.setVisible(true);
    }
}
