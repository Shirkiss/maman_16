import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by shir.cohen on 1/26/2018.
 */
class Client extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JPanel menuItemsPanel;
    private JButton submitButton;
    private JTextField nameField;
    private JTextField phoneField;
    private JTextField addressField;
    private HashMap<MenuItem, Integer> orderItems;
    private StringBuilder orderDetails;
    private ClientControl clientControl;
    private ArrayList<MenuItem> menuItems;


    Client(String host, int port) {
        super("Client");

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
        clientControl = new ClientControl(host, port);
        menuItems = new ArrayList<>();
        getMenu.addActionListener(
                e -> {
                    menuItems = clientControl.getMenu();
                    if (menuItems == null) {
                        showError();
                    } else
                        showMenu(menuItems);
                }
        );

        startPanel.add(startLabel);
        startPanel.add(getMenu);
        mainPanel.add(startPanel, "start");
        add(mainPanel);
        cardLayout.show(mainPanel, "start");
    }

    private void showError() {
        JOptionPane.showMessageDialog(null, "Something went wrong!\nPlease try again!"
                , "Error",
                JOptionPane.ERROR_MESSAGE, null);
    }

    private void showMenu(ArrayList<MenuItem> menuItems) {
        //presenting the menu to the client
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
                e -> showOrderSummary()
        );
        placeOrderPanel.add(submitButton, BorderLayout.SOUTH);
        mainPanel.add(placeOrderPanel, "menuItems");
        cardLayout.show(mainPanel, "menuItems");
    }


    private void showOrderSummary() {
        orderItems = new HashMap<>();
        //go over all the selected items and make a summary
        orderDetails = new StringBuilder();
        Double priceSum = 0.0;

        for (Component currentPanel : menuItemsPanel.getComponents()) {
            if (currentPanel instanceof MenuItemPanel) {
                // put in the hush all items with quantity larger than 0
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

        Object stringArray[] = {"Send order", "Edit order"};
        int answer = JOptionPane.showOptionDialog(null, "Please review your order before submitting\n\nYour order:\n" + orderDetails + "\nTotal price: " + priceSum
                , "One last confirm",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, stringArray,
                stringArray[0]);
        if (answer == JOptionPane.YES_OPTION) {
            showUserDetailsPanel();
        } else
            orderItems.clear();
    }

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
                    boolean successSendingOrder = clientControl.submitOrderItems(orderItems, nameField.getText(), phoneField.getText(), addressField.getText());
                    if (successSendingOrder) {
                        Order order = clientControl.getOrderDataFromServer();
                        if (order != null)
                            showOrder(order);
                        else
                            showError();
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

    private void showOrder(Order order) {
        StringBuilder orderDetails = new StringBuilder();
        for (MenuItem item : order.getOrderItems().keySet()) {
            String itemKey = item.getItemDescription();
            String quantity = order.getOrderItems().get(item).toString();
            orderDetails.append(itemKey).append(" - quantity: ").append(quantity).append("\n");
        }
        Object stringArray[] = {"Order more", "Exit"};
        int a = JOptionPane.showOptionDialog(null, "We successfully got your order!\n\nYour order:\n" + orderDetails
                        + "\n\nYour details:\n" + order.getName() + "\n" + order.getPhone() + "\n" + order.getAddress(), "Your order is on it's way!",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, stringArray,
                stringArray[0]);
        if (a == JOptionPane.NO_OPTION || a == JOptionPane.CLOSED_OPTION) {
            clientControl.closeConnection();
            System.exit(0);
        } else if (a == JOptionPane.YES_OPTION)
            showMenu(menuItems);
    }
}
