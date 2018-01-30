import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * RestaurantClientFrame.java
 * Purpose: Let's the user get the menu from the server and make an order
 *
 * @author Shir Cohen
 */
class RestaurantClientFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JPanel menuItemsPanel;
    private JButton submitButton;
    private JTextField nameField;
    private JTextField phoneField;
    private JTextField addressField;
    private HashMap<MenuItem, Integer> orderItems;
    private StringBuilder orderDetails;
    private RestaurantClientController restaurantClientController;
    private ArrayList<MenuItem> menuItems;


    RestaurantClientFrame(String host, int port) {
        super("Shir's Restaurant");

        //Setting card layout
        cardLayout = new CardLayout();

        //creating the main panel that other panels will be added to
        mainPanel = new JPanel(cardLayout);
        mainPanel.setPreferredSize(new Dimension(500, 750));
        mainPanel.setBorder(new EmptyBorder(40, 45, 40, 45));

        //creating the Start panel with GridBagLayout layout
        JPanel startPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        startPanel.setBorder(new EmptyBorder(100, 100, 400, 100));
        JLabel startLabel = new JLabel("<html><b>Welcome to Shir's restaurant!</b></html>");
        JButton getMenu = new JButton("Get menu");
        restaurantClientController = new RestaurantClientController(host, port);
        menuItems = new ArrayList<>();
        getMenu.addActionListener(
                e -> {
                    menuItems = restaurantClientController.getMenu();
                    if (menuItems == null) {
                        showError(); //show popup error if the menu didn't successfully received
                    } else
                        showMenu(menuItems); //show menu items panel to the user
                }
        );
        startPanel.add(startLabel);
        startPanel.add(getMenu);
        mainPanel.add(startPanel, "start");
        add(mainPanel);
        cardLayout.show(mainPanel, "start");
    }

    /**
     * Show error popup if something went wrong
     */
    private void showError() {
        JOptionPane.showMessageDialog(null, "Something went wrong!\nPlease try again!"
                , "Error",
                JOptionPane.ERROR_MESSAGE, null);
    }

    /**
     * Show the menu items panel to the user
     *
     * @param menuItems list of all menu items
     */
    private void showMenu(ArrayList<MenuItem> menuItems) {
        BorderLayout layout = new BorderLayout(10, 10);
        JPanel placeOrderPanel = new JPanel(layout);
        menuItemsPanel = new JPanel(new GridLayout(0, 1));
        JScrollPane scrollPane = new JScrollPane(menuItemsPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));

        //create menuItem panel to each menu item and add it to the menuItemsPanel
        for (MenuItem item : menuItems) {
            MenuItemPanel menuPanel = new MenuItemPanel(item);
            menuItemsPanel.add(menuPanel);
        }

        placeOrderPanel.add(scrollPane);
        submitButton = new JButton("Continue");
        submitButton.addActionListener(
                e -> showOrderSummary() //show order summary popup to the user
        );
        placeOrderPanel.add(submitButton, BorderLayout.SOUTH);
        mainPanel.add(placeOrderPanel, "menuItems");
        cardLayout.show(mainPanel, "menuItems");
    }

    /**
     * Show popup with order summary
     */
    private void showOrderSummary() {
        orderItems = new HashMap<>();
        orderDetails = new StringBuilder();
        //calculate the final price based on the items price and quantity
        Double priceSum = 0.0;

        //go over all the menu item panels and collect information about the selection
        for (Component currentPanel : menuItemsPanel.getComponents()) {
            if (currentPanel instanceof MenuItemPanel) {
                //put in the hush all items with quantity larger than 0
                if (((MenuItemPanel) currentPanel).getItemQuantity() != 0) {
                    //save MenuItem and quantity on HashMap
                    orderItems.put(((MenuItemPanel) currentPanel).getMenuItem(), ((MenuItemPanel) currentPanel).getItemQuantity());
                    //calculate price priceSum of all items
                    priceSum += ((MenuItemPanel) currentPanel).getItemQuantity() * ((MenuItemPanel) currentPanel).getItemPrice();
                    //build order description string
                    orderDetails.append(((MenuItemPanel) currentPanel).getItemDescription()).append(" - quantity: ").append(((MenuItemPanel) currentPanel).getItemQuantity()).append("\n");
                }
            }
        }
        Object stringArray[] = {"Send order", "Edit order"}; //popup buttons
        //show popup to the user
        int answer = JOptionPane.showOptionDialog(null, "Please review your order before submitting\n\nYour order:\n"
                        + orderDetails + "\nTotal price: " + priceSum, "One last confirm",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, stringArray,
                stringArray[0]);
        if (answer == JOptionPane.YES_OPTION) {
            showUserDetailsPanel(); //show the user the details panel
        } else
            orderItems.clear();
    }

    /**
     * Show user details panel
     */
    private void showUserDetailsPanel() {
        JPanel submitPanel = new JPanel(new GridLayout(0, 1, 20, 20));
        JPanel detailsPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        JPanel summaryPanel = new JPanel();
        JTextArea summary = new JTextArea(70, 20);
        summary.setEditable(false);
        summary.setText(orderDetails.toString());
        submitPanel.add(summaryPanel);
        summaryPanel.add(summary);
        JLabel nameLabel = new JLabel("Name: ");
        nameField = new JTextField();
        JLabel phoneLabel = new JLabel("Phone: ");
        phoneField = new JTextField();
        JLabel addressLabel = new JLabel("Address: ");
        addressField = new JTextField();
        detailsPanel.add(nameLabel);
        detailsPanel.add(nameField);
        detailsPanel.add(phoneLabel);
        detailsPanel.add(phoneField);
        detailsPanel.add(addressLabel);
        detailsPanel.add(addressField);
        JPanel submitButtonPanel = new JPanel();

        submitPanel.add(detailsPanel);
        submitButton = new JButton("Place order");
        submitButton.addActionListener(
                e -> {
                    //submit the order to the server
                    boolean successSendingOrder = restaurantClientController.submitOrderItems(orderItems,
                            nameField.getText(), phoneField.getText(), addressField.getText());
                    if (successSendingOrder) {
                        //get order information from the server
                        Order order = restaurantClientController.getOrderDataFromServer();
                        if (order != null)
                            showOrder(order); //show the order details that was received from the server
                        else
                            showError(); //show error popup
                    }
                }
        );
        submitButtonPanel.add(submitButton);
        submitButtonPanel.setBorder(new EmptyBorder(30, 50, 30, 30));
        submitPanel.setBorder(new EmptyBorder(100, 50, 100, 50));
        submitPanel.add(submitButtonPanel);
        mainPanel.add(submitPanel, "details");
        cardLayout.show(mainPanel, "details");
    }

    /**
     * Show the order to the user
     *
     * @param order details to show to the user
     */
    private void showOrder(Order order) {
        StringBuilder orderDetails = new StringBuilder();
        for (MenuItem item : order.getOrderItems().keySet()) {
            String itemKey = item.getItemDescription();
            String quantity = order.getOrderItems().get(item).toString();
            orderDetails.append(itemKey).append(" - quantity: ").append(quantity).append("\n");
        }
        Object stringArray[] = {"Order more", "Exit"}; //popup buttons
        int a = JOptionPane.showOptionDialog(null, "We successfully got your order!\n\nYour order:\n"
                        + orderDetails + "\n\nYour details:\n" + order.getName() + "\n" + order.getPhone()
                        + "\n" + order.getAddress(), "Your order is on it's way to you!",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, stringArray, stringArray[0]);
        if (a == JOptionPane.NO_OPTION || a == JOptionPane.CLOSED_OPTION) {
            restaurantClientController.closeConnection(); //close the connection
            System.exit(0);
        } else if (a == JOptionPane.YES_OPTION)
            showMenu(menuItems); //order more
    }
}