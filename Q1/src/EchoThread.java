/*
 * EchoThread.java
 *
 * Author: Shay Tavor, shay.tavor@gmail.com
 *
 * A single thread that handles a single client
 */


import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Objects;

/**
 * @author Shay Tavor
 */
public class EchoThread extends Thread {
    private final List<MenuItem> MenuItems;
    private Socket socket = null;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    EchoThread(Socket s, List<MenuItem> menu) {
        this.MenuItems = menu;
        socket = s;
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        String input;
        try {
            input = (String) in.readObject();
            if (Objects.equals(input, "Get menu")) {
                out.writeObject(MenuItems);
                out.flush();
            } else if (Objects.equals(input, "Submit Order")) {
                Order order = (Order) in.readObject();
                out.writeObject(order);
                out.flush();
            }
            closeConnection();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void closeConnection() {
        try {
            System.out.println("Server>> Closing connection");
            out.close();
            in.close();
            socket.close();
        } catch (IOException ioException) {
        }
    }
}

