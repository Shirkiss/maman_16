import javax.swing.*;

/**
 * @author Shir Cohen
 */
public class EchoClient {

    public static void main(String[] args) {
        Client application = new Client("localhost", 3333);
        application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        application.pack();
        application.setVisible(true);
    }
}
