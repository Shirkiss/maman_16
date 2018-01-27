import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by shir.cohen on 1/27/2018.
 */
public class Order implements Serializable {

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
