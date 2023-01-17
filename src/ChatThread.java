import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatThread implements Runnable {
    public Socket socket;
    public BufferedReader in;
    public PrintWriter out;

    public String chatLog = "";

    public boolean connected = false;

    ChatThread(Socket socket, BufferedReader in, PrintWriter out) {
        this.socket = socket;
        this.in = in;
        this.out = out;

        new Thread(this).start();
    }

    @Override
    public void run() {
        String msg;
        try {
            msg = in.readLine();
            while (msg != null) {
                chatLog += ("Them: " + msg + "\n");
                msg = in.readLine();
            }
            chatLog += "User disconnected!\n";
            out.close();
            connected = false;
        } catch (Exception ex) {
            chatLog += ex.getMessage() + "\n";
        }
    }
}
