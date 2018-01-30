import java.io.Serializable;
import java.util.HashMap;

/**
 * Order.java
 * An object that represent a restaurant order. .
 * On each order item there is an HashMap of items and quantity.
 * Also there is a details on the owner of the order: name, phone and address.
 * *
 *
 * @author Shir Cohen
 */
class Order implements Serializable {

    private final HashMap<MenuItem, Integer> orderItems;
    private final String name;
    private final String phone;
    private final String address;

    Order(HashMap<MenuItem, Integer> orderItems, String name, String phone, String address) {
        this.orderItems = orderItems;
        this.name = name;
        this.phone = phone;
        this.address = address;
    }

    HashMap<MenuItem, Integer> getOrderItems() {
        return orderItems;
    }

    String getName() {
        return name;
    }

    String getPhone() {
        return phone;
    }

    String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return "Order: " + orderItems + ", delivered to: " + name + ", phone:" + phone + ", address: " + address;
    }

}
