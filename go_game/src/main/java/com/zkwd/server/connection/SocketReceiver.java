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
    private boolean closed = false;

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
        while(!s.isClosed()){
            try {
                // System.out.println("socket is waiting");
                currentMessage = in.readLine();

                synchronized(this){
                    System.out.println("socket received: " + currentMessage);
                    // wake up threads waiting for new message
                    notifyAll();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // do something when socket closes? maybe
        closed = true;
    }

    /**
     * @return the last message in the input stream.
     */
    public String getLastMessage() {
        return currentMessage;
    }

    /**
     * Hopefully, any threads calling this method begin to wait for this object.
     * When the message gets updated (another message is read), they are all woken up and the new message is sent out.
     * @return new message
     */
    public String getNextMessage() {
        try {
            System.out.println("a thread is waiting");
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

    public boolean isClosed() {
        return closed;
    }
}
