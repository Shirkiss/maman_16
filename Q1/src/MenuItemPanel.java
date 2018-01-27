import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Created by shir.cohen on 1/27/2018.
 */
public class MenuItemPanel extends JPanel {

    private JComboBox qunatityCombo = new JComboBox();
    private int itemId;
    private int itemType;
    private double itemPrice;
    private String itemDescription;
    static final private String[] types = {"Starter", "Main Course", "Desert", "Drink"};


    public MenuItemPanel(MenuItem menuItem) {
        this.itemId = menuItem.getItemId();
        this.itemType = menuItem.getItemType();
        this.itemPrice = menuItem.getItemPrice();
        this.itemDescription = menuItem.getItemDescription();

        setLayout(new FlowLayout(FlowLayout.CENTER, 15, 0));
        TitledBorder title;
        title = BorderFactory.createTitledBorder("#" + String.valueOf(menuItem.getItemId()) + " " + types[menuItem.getItemType()]);
        setBorder(title);


        for (int i = 0; i < 8; i++)
            qunatityCombo.addItem(i);

        JLabel description = new JLabel("<html><b>" + menuItem.getItemDescription() + "</b></html>");
        JLabel price = new JLabel("<html><b>" + menuItem.getItemPrice() + "</b></html>");

        add(description);
        add(price);
        add(qunatityCombo);
    }

    int getItemQuantity() {
        return (qunatityCombo.getSelectedIndex());
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

    int getItemType() {
        return itemType;
    }
}
