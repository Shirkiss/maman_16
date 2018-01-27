import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 *
 * @author Shay Tavor
 */
public class MultipleClientsServer {
    public static void main(String[] args)
    {
        List<MenuItem> menu;
        menu = getMenuFromFile("C:\\Users\\shir.cohen\\Desktop\\studies\\Java\\maman16\\src\\menu.txt");
        ServerSocket srv;
        try {
            srv = new ServerSocket(7777);
            System.out.println("Server>> Server ready");
            Socket socket;

            while(true)
            {
                socket = srv.accept();
                new EchoThread(socket, menu).start();
            }
        }
        catch(InterruptedIOException e)
        {
            System.out.println("Time out");
        }
        catch(IOException e)
        {
            e.printStackTrace();
            System.exit(1);
        }

    }

    private static List<MenuItem> getMenuFromFile(String path)
    {
        List<MenuItem> MenuItems = new ArrayList<>();
        try (Scanner input = new Scanner(new File(path))) {
            while (input.hasNext()) // more data to read
            {
                String line = input.nextLine();
                String[] arrayLine = line.split(",");
                int itemId = Integer.parseInt(arrayLine[0]);
                int itemType = Integer.parseInt(arrayLine[1]);
                String itemDescription = arrayLine[2];
                double itemPrice = Double.parseDouble(arrayLine[3]);
                MenuItems.add(new MenuItem(itemId, itemType,itemDescription,itemPrice));
            }
            input.close();
        }      catch (NoSuchElementException elementException)
        {
            System.err.println("File improperly formed. Terminating.");
        }
        catch (IllegalStateException stateException)
        {
            System.err.println("Error reading from file. Terminating.");
        }
        catch ( IOException e) {
            System.err.println("Error processing file. Terminating.");
            System.exit(1);
        }
        return MenuItems;
    }


}
