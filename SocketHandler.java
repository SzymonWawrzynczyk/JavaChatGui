package SocketTut;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class SocketHandler {
    private Socket socket;
    private Scanner in;
    private PrintWriter out;
    private boolean  is_logged_in;
    public SocketHandler(Socket socket) {
        this.socket = socket;
        try {
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true /* autoFlush */);
            is_logged_in=false;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Scanner getIn() {
        return in;
    }
    public PrintWriter getOut() {
        return out;
    }

    public boolean getIs_logged_in(){
        return is_logged_in;
    }

    public void set_logged_in(){
        is_logged_in=true;
    }
}
