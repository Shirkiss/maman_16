import java.io.Serializable;

/**
 * MenuItem.java
 * Purpose: Create a choice question object
 *
 * @author Shir Cohen
 */
class MenuItem implements Serializable{

    private final int itemId;
    private final int itemType;
    private final String itemDescription;
    private final double itemPrice;


    MenuItem(int itemId, int itemType, String itemDescription, double itemPrice) {
        this.itemId = itemId;
        this.itemType = itemType;
        this.itemDescription = itemDescription;
        this.itemPrice = itemPrice;
    }

    int getItemId() {
        return itemId;
    }

    int getItemType() {
        return itemType;
    }

    String getItemDescription() {
        return itemDescription;
    }

    double getItemPrice() {
        return itemPrice;
    }

    @Override
    public String toString() {
        return "Name: " + itemDescription + ", Price:" + itemPrice;
    }
}
