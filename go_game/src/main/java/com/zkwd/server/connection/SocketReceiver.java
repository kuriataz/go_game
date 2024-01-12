package com.zkwd.server.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * This is the object that will read every message from the input stream and distribute it to any objects that might be interested in it.
 */
public class SocketReceiver implements Runnable{

    private Socket s;
    private BufferedReader in;
    private PrintWriter out;

    private String currentMessage;

    public SocketReceiver(Socket s) throws IOException {
        this.s = s;
        in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        out = new PrintWriter(s.getOutputStream(), true);
    }

    /**
     * ClientReceiver will always update the current message to the latest one in the stream,
     * so multiple classes will be able to access it without losing it.
     */
    public void run() {
        while(true){
            try {
                synchronized(this){
                    currentMessage = in.readLine();
                    notifyAll();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @return the latest message in the input stream.
     */
    public String getLastMessage() {
        return currentMessage;
    }

    /**
     * Hopefully, any threads calling this method begin to wait for this object.
     * When the message gets updated (another message is read), they are all woken up and the new message is sent out.
     * @return
     */
    public String getNextMessage() {
        try {
            // god help me
            synchronized(this) {
                wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return currentMessage;
    }

    public void send(String message) {
        out.println(message);
    }

    public void close() throws IOException {
        s.close();
    }
}
