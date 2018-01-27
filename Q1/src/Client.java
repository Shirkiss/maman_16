import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by shir.cohen on 1/26/2018.
 */
class Client extends JFrame {
    private Socket socket = null;
    private ObjectOutputStream out = null;
    private ObjectInputStream in = null;
    private String hostAddress;
    private int portAddress;
    private CardLayout cardLayout;
    private BorderLayout layout;
    private JPanel mainPanel;
    private JPanel menuItemsPanel;
    private JButton getMenu;
    private JButton submitButton;
    private JTextField nameField;
    private JTextField phoneField;
    private JTextField addressField;
    private JPanel placeOrderPanel;
    private HashMap<MenuItem, Integer> orderItems;
    private StringBuilder orderDetails;


    Client(String host, int port) {
        super("Client");
        hostAddress = host;
        portAddress = port;

        //Setting card layout
        cardLayout = new CardLayout();

        //creating the main panel that other panels will be added to
        mainPanel = new JPanel(cardLayout);
        mainPanel.setPreferredSize(new Dimension(500, 700));
        mainPanel.setBorder(new EmptyBorder(40, 45, 40, 45));

        //creating the Start panel with GridBagLayout layout
        JPanel startPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        startPanel.setBorder(new EmptyBorder(100, 100, 400, 100));
        JLabel startLabel = new JLabel("<html><b>Welcome to Shir's restaurant!</b></html>");
        getMenu = new JButton("Get menu");
        getMenu.addActionListener(
                e -> getMenu()
        );

        startPanel.add(startLabel);
        startPanel.add(getMenu);
        mainPanel.add(startPanel, "start");
        add(mainPanel);
        cardLayout.show(mainPanel, "start");
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

    private void getMenu() {
        orderItems = new HashMap();
        sendData("Get menu");
        try {
            //getting the menu items from the server
            ArrayList<MenuItem> arrayMenuItems = (ArrayList<MenuItem>) in.readObject();
            //presenting the menu to the client
            layout = new BorderLayout(10, 10);
            placeOrderPanel = new JPanel(layout);
            menuItemsPanel = new JPanel(new GridLayout(0, 1));
            JScrollPane scrollPane = new JScrollPane(menuItemsPanel);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));

            //create menuItem panel to each menu item and add it to the menuItemsPanel
            for (MenuItem item : arrayMenuItems) {
                MenuItemPanel menuPanel = new MenuItemPanel(item);
                menuItemsPanel.add(menuPanel);
            }

            placeOrderPanel.add(scrollPane);

            submitButton = new JButton("Continue");
            submitButton.addActionListener(
                    e -> showOrder()
            );
            placeOrderPanel.add(submitButton, BorderLayout.SOUTH);
            mainPanel.add(placeOrderPanel, "menuItems");
            cardLayout.show(mainPanel, "menuItems");

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    private void submitOrderItems() {
        sendData("Submit Order");
        System.out.println("Client>> Submit Order");
        Order order = new Order(orderItems, nameField.getText(), phoneField.getText(), addressField.getText());
        try {
            out.writeObject(order);
            out.flush();
            System.out.println("Client>> " + order);
        } catch (IOException e) {
            e.printStackTrace();
        }
        getOrderData();
    }

    private void showOrder() {
        //go over all the selected items and make a summary
        orderDetails = new StringBuilder();
        Double sum = 0.0;

        for (Component currentPanel : menuItemsPanel.getComponents()) {
            if (currentPanel instanceof MenuItemPanel) {
                // put in the hush all items with quantity larger than 0
                if (((MenuItemPanel) currentPanel).getItemQuantity() != 0) {
                    orderItems.put(((MenuItemPanel) currentPanel).getMenuItem(), ((MenuItemPanel) currentPanel).getItemQuantity());
                    sum += ((MenuItemPanel) currentPanel).getItemQuantity() * ((MenuItemPanel) currentPanel).getItemPrice();
                    orderDetails.append(((MenuItemPanel) currentPanel).getItemDescription()).append(" - quantity: ").append(((MenuItemPanel) currentPanel).getItemQuantity()).append("\n");
                }
            }
        }

        Object stringArray[] = {"Send order", "Edit order"};
        int answer = JOptionPane.showOptionDialog(null, "Please review your order before submitting\n\nYour order:\n" + orderDetails + "\nTotal price: " + sum
                , "One last confirm",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, stringArray,
                stringArray[0]);
        if (answer == JOptionPane.YES_OPTION) {
            getUserDetails();
        } else
            orderItems.clear();
    }

    private void getUserDetails() {
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
                e -> submitOrderItems()
        );
        submitButtonPanel.add(submitButton);
        submitButtonPanel.setBorder(new EmptyBorder(30, 50, 30, 30));
        submitPanel.setBorder(new EmptyBorder(100, 50, 100, 50));
        submitPanel.add(submitButtonPanel);
        mainPanel.add(submitPanel, "details");
        cardLayout.show(mainPanel, "details");
    }

    private void getOrderData() {
        try {
            Order order = (Order) in.readObject();
            StringBuilder orderDetails = new StringBuilder();
            Double sum = 0.0;
            for (MenuItem item : order.getOrderItems().keySet()) {
                String itemKey = item.getItemDescription();
                String quantity = order.getOrderItems().get(item).toString();
                orderDetails.append(itemKey).append(" - quantity: ").append(quantity).append("\n");
                sum += item.getItemPrice() * Double.parseDouble(quantity);
            }

            Object stringArray[] = {"Order more", "Exit"};
            int a = JOptionPane.showOptionDialog(null, "We successfully got your order!\n\nYour order:\n" + orderDetails + "Total price: " + sum
                            + "\n\nYour details:\n" + order.getName() + "\n" + order.getPhone() + "\n" + order.getAddress(), "Your order is on it's way!",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, stringArray,
                    stringArray[0]);
            if (a == JOptionPane.NO_OPTION || a == JOptionPane.CLOSED_OPTION){
                closeConnection();
                System.exit(0);
            }
            else if (a == JOptionPane.YES_OPTION)
                getMenu();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void closeConnection() {
        try {
            System.out.println("Client>> Closing connection");
            out.close();
            in.close();
            socket.close();
        } catch (IOException ioException) {
        }
    }
}
