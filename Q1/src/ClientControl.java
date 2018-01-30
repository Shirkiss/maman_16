import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by shir.cohen on 1/30/2018.
 */
public class ClientControl {
    private Socket socket = null;
    private ObjectOutputStream out = null;
    private ObjectInputStream in = null;
    private String hostAddress;
    private int portAddress;


    ClientControl(String host, int port) {
        hostAddress = host;
        portAddress = port;
    }

    ArrayList<MenuItem> getMenu() {
        sendData("Get menu");
        try {
            //getting the menu items from the server
            return (ArrayList<MenuItem>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void sendData(String message) {
        try {
            socket = new Socket(hostAddress, portAddress);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            System.out.println("Client connected to the server");

            out.writeObject(message);
            out.flush();
            System.out.println("Client>> " + message);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    boolean submitOrderItems(HashMap<MenuItem, Integer> orderItems, String name, String phone, String address) {
        sendData("Submit Order");
        System.out.println("Client>> Submit Order");
        Order order = new Order(orderItems, name, phone, address);
        try {
            out.writeObject(order);
            out.flush();
            System.out.println("Client>> " + order);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    Order getOrderDataFromServer() {
        try {
            return (Order) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    void closeConnection() {
        try {
            System.out.println("Client>> Closing connection");
            out.close();
            in.close();
            socket.close();
        } catch (IOException ioException) {
            System.out.println("Client>> Failed to close connection");
            ioException.printStackTrace();
        }
    }
}
