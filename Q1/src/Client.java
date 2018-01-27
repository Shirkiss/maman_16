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
    private GridBagConstraints constraints;
    private GridBagLayout layout;
    private JPanel mainPanel;
    private JPanel menuItemsPanel;
    private JButton getMenu;
    private JButton submitButton;
    private JTextField nameField;
    private JTextField phoneField;
    private JTextField addressField;
    private JPanel placeOrderPanel;


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
        JPanel startPanel = new JPanel(new GridLayout(2,1,10,10));
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
        sendData("Get menu");
        try {
            //getting the menu items from the server
            ArrayList<MenuItem> arrayMenuItems = (ArrayList<MenuItem>) in.readObject();
            layout = new GridBagLayout();
            placeOrderPanel = new JPanel(layout);
            constraints = new GridBagConstraints();
            constraints.fill = GridBagConstraints.BOTH;
            //presenting the menu to the client
            menuItemsPanel = new JPanel(new GridLayout(arrayMenuItems.size() + 1, 1));
            JScrollPane scrollPane = new JScrollPane(menuItemsPanel);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));
            //create menuItem panel to each menu item and add it to the menuItemsPanel
            for (MenuItem item : arrayMenuItems) {
                MenuItemPanel menuPanel = new MenuItemPanel(item);
                menuItemsPanel.add(menuPanel);
            }
            constraints.weighty = 1;
            constraints.weightx = 1000;
            addComponent(scrollPane, 0, 0, 1, 1);

            JPanel detailsPanel = new JPanel(new GridLayout(3, 2, 10, 10));
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
            constraints.weighty = 0;
            constraints.weightx = 0;

            addComponent(detailsPanel, 1, 0, 1, 1);


            submitButton = new JButton("Place order");
            submitButton.addActionListener(
                    e -> submitOrderItems()
            );
            addComponent(submitButton, 2, 0, 1, 1);

            mainPanel.add(placeOrderPanel, "menuItems");
            cardLayout.show(mainPanel, "menuItems");

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     * Add component to the Grid Bag Layout
     *
     * @param row
     * @param column
     * @param width
     * @param height
     */
    private void addComponent(Component component, int row, int column, int width, int height) {
        constraints.gridx = column;
        constraints.gridy = row;
        constraints.gridwidth = width;
        constraints.gridheight = height;
        layout.setConstraints(component, constraints);
        placeOrderPanel.add(component);
    }

    private void submitOrderItems() {
        sendData("Submit Order");
        System.out.println("Client>> Submit Order");
        try {
            //inform the server on the action
            HashMap<MenuItem, Integer> orderItems = new HashMap();
            for (Component currentPanel : menuItemsPanel.getComponents()) {
                if (currentPanel instanceof MenuItemPanel) {
                    // send all items with quantity larger than 0
                    if (((MenuItemPanel) currentPanel).getItemQuantity() != 0)
                        orderItems.put(((MenuItemPanel) currentPanel).getMenuItem(), ((MenuItemPanel) currentPanel).getItemQuantity());
                }
            }
            Order order = new Order(orderItems, nameField.getText(), phoneField.getText(), addressField.getText());

            out.writeObject(order);
            out.flush();
            System.out.println("Client>> " + order);
        } catch (IOException e) {
            e.printStackTrace();
        }
        getOrderData();
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
            if (a == JOptionPane.NO_OPTION || a == JOptionPane.CLOSED_OPTION)
                System.exit(0);
            else if (a == JOptionPane.YES_OPTION)
                getMenu();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void closeConnection() {
        try {
            out.close();
            in.close();
            socket.close();
        } catch (IOException ioException) {
        }
    }

    private ArrayList<MenuItem> getMenuData() throws IOException, ClassNotFoundException {
        return (ArrayList<MenuItem>) in.readObject();
    }

//    private void processMessage(String message)
//    {
//        if (message.equals("you requested a menu"))
//        {
//            String menu = in.readLine();
//            System.out.println(menu);
//        }
//    }
}
