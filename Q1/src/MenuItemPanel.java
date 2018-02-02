import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * MenuItemPanel.java
 * Menu item panel that contain the item id, name, type, price.
 * Each panel let you select the quantity you want to order from each item.
 *
 * @author Shir Cohen
 */
class MenuItemPanel extends JPanel {

    private JComboBox quantityCombo;
    private int itemId;
    private String itemType;
    private double itemPrice;
    private String itemDescription;


    MenuItemPanel(MenuItem menuItem) {
        this.itemId = menuItem.getItemId();
        this.itemType = menuItem.getItemType();
        this.itemPrice = menuItem.getItemPrice();
        this.itemDescription = menuItem.getItemDescription();

        setLayout(new FlowLayout(FlowLayout.CENTER, 15, 0));
        TitledBorder title;
        title = BorderFactory.createTitledBorder("#" + String.valueOf(menuItem.getItemId())
                + " " + menuItem.getItemType());
        setBorder(title);
        //creating quantity dropdown with max of 8 items
        quantityCombo = new JComboBox();
        for (int i = 0; i < 8; i++)
            quantityCombo.addItem(i);

        JLabel description = new JLabel("<html><b>" + menuItem.getItemDescription() + "</b></html>");
        JLabel price = new JLabel("<html><b>" + menuItem.getItemPrice() + "</b></html>");

        add(description);
        add(price);
        add(quantityCombo);
    }

    int getItemQuantity() {
        return (quantityCombo.getSelectedIndex());
    }

    MenuItem getMenuItem() {
        return new MenuItem(getItemId(), getItemType(), getItemDescription(), getItemPrice());
    }

    double getItemPrice() {
        return itemPrice;
    }

    String getItemDescription() {
        return itemDescription;
    }

    int getItemId() {
        return itemId;
    }

    String getItemType() {
        return itemType;
    }
}
