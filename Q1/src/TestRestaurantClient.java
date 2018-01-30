import javax.swing.*;

/**
 * TestRestaurantClient.java
 * Purpose: Get menu items from the server and create an order
 *
 * @author Shir Cohen
 */
class TestRestaurantClient {

    public static void main(String[] args) {
        RestaurantClientFrame application = new RestaurantClientFrame("localhost", 3333);
        application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        application.pack();
        application.setVisible(true);
    }
}
