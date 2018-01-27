import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by shir.cohen on 1/25/2018.
 */
public class RestaurantServer extends JFrame {
    private JTextField enterField;
    private JTextArea displayArea;
    private ObjectOutputStream output;
    private ObjectInputStream input;

    public RestaurantServer() {
        super("RestaurantServer");
        enterField = new JTextField();
        enterField.setEditable(false);
        enterField.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        sendData(e.getActionCommand());
                    }
                }
        );
        add(enterField, BorderLayout.NORTH);
        displayArea = new JTextArea();
        add(new JScrollPane(displayArea), BorderLayout.CENTER);

        setSize(300,150);
        setVisible(true);
    }

//    public void runServer()
//    {
//        try {
//            server = new ServerSocket(7777,100);
//            while (true)
//            {
//                try
//                {
//                    waitForConnection();
//                    getStreams();
//                    processConnection();
//                } catch (EOFException eofException)
//                {
//                    displayMessage("\nServer terminated connection");
//                }
//            }
//        }
//    }

    private void sendData(String message)
    {
        try
        {
            output.writeObject("SERVER>> " +message);
            output.flush();
            displayMessage("\nServer >> " + message);
        }
        catch (IOException ioException)
        {
            displayArea.append("\nError writing object");
        }
    }

    private void displayMessage(final String messageToDisplay)
    {
        SwingUtilities.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                        displayArea.append(messageToDisplay);
                    }
                }
        );
    }
}
