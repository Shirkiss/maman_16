import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * RestaurantClientController.java
 * Purpose: the logic behind the Restaurant UI
 *
 * @author Shir Cohen
 */
class RestaurantClientController {
    private Socket socket = null;
    private ObjectOutputStream out = null;
    private ObjectInputStream in = null;
    private String hostAddress;
    private int portAddress;


    RestaurantClientController(String host, int port) {
        hostAddress = host;
        portAddress = port;
    }

    /**
     * Get from the server the menu and return it
     *
     * @return list of all menu items
     */
    ArrayList<MenuItem> getMenu() {
        sendData("Get menu");
        try {
            //getting the menu items from the server
            return (ArrayList<MenuItem>) in.readObject();
        } catch (IOException | NullPointerException | ClassNotFoundException e ) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Send a message to the server
     *
     * @param message to send to the server
     */
    private void sendData(String message) {
        try {
            socket = new Socket(hostAddress, portAddress);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            System.out.println("RestaurantClientFrame connected to the server");

            out.writeObject(message);
            out.flush();
            System.out.println("RestaurantClientFrame>> " + message);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send an order to the server
     *
     * @param orderItems that contain the menu items and the quantity
     * @param name of the person that send the order
     * @param phone of the person that send the order
     * @param address of which to send the order to
     * @return true if the order was successfully sent and false otherwise
     */
    boolean submitOrderItems(HashMap<MenuItem, Integer> orderItems, String name, String phone, String address) {
        sendData("Submit Order");
        System.out.println("RestaurantClientFrame>> Submit Order");
        Order order = new Order(orderItems, name, phone, address);
        try {
            out.writeObject(order);
            out.flush();
            System.out.println("RestaurantClientFrame>> " + order);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get the order that was received on the server
     *
     * @return the order item
     */
    Order getOrderDataFromServer() {
        try {
            return (Order) in.readObject();
        } catch (IOException | ClassNotFoundException |NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Close the connection
     **/
    void closeConnection() {
        try {
            System.out.println("RestaurantClientFrame>> Closing connection");
            out.close();
            in.close();
            socket.close();
        } catch (IOException ioException) {
            System.out.println("RestaurantClientFrame>> Failed to close connection");
            ioException.printStackTrace();
        }
    }
}
