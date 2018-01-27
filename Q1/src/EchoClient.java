import java.net.*;
import java.io.*;
import javax.swing.*;

/**
 *
 * @author Shay Tavor
 */
public class EchoClient {
    
    public static void main(String[] args)
    {

        Client application = new Client("localhost",7777);
        application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        application.pack();
        application.setVisible(true);
    }
    
}
