// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com

import java.io.*;
import client.*;
import common.*;

/**
 * This class constructs the UI for a chat client.  It implements the
 * chat interface in order to activate the display() method.
 * Warning: Some of the code here is cloned in ServerConsole
 *
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @version July 2000
 */
public class ClientConsole implements ChatIF
{
  //Class variables *************************************************

  /**
   * The default port to connect on.
   */
  final public static int DEFAULT_PORT = 5555;

  //Instance variables **********************************************

  /**
   * The instance of the client that created this ConsoleChat.
   */
  ChatClient client;


  //Constructors ****************************************************

  /**
   * Constructs an instance of the ClientConsole UI.
   *
   * @param host The host to connect to.
   * @param port The port to connect on.
   */
  public ClientConsole(String host, int port)
  {
    try
    {
      client= new ChatClient(host, port, this);
    }
    catch(IOException exception)
    {
      System.out.println("Error: Can't setup connection! Terminating client.");
      System.exit(1);
    }
  }


  //Instance methods ************************************************

  /**
   * This method waits for input from the console.  Once it is
   * received, it sends it to the client's message handler.
   */
  public void accept()
  {
    try
    {
      BufferedReader fromConsole =
              new BufferedReader(new InputStreamReader(System.in));
      String message;
      while (true)
      {
        message = fromConsole.readLine();
        if (message != null && message.startsWith("#"))
        {
          handleCommand(message);
        }
        else
        {
          client.handleMessageFromClientUI(message);
        }
      }
    }
    catch (Exception ex)
    {
      System.out.println
              ("Unexpected error while reading from console!");
    }
  }

  /**
   * Handles commands starting with '#'
   *
   * @param command The command to process
   */
  private void handleCommand(String command)
  {
    String[] parts = command.split("\\s+", 2);
    String cmd = parts[0].toLowerCase();

    switch (cmd)
    {
      case "#quit":
        try
        {
          client.closeConnection();
          display("Connection closed. Client terminated.");
          System.exit(0);
        }
        catch (IOException e)
        {
          display("Error: Could not close connection.");
          System.exit(1);
        }
        break;
      case "#logoff":
        if (client.isConnected())
        {
          try
          {
            client.setDeliberateLogoff(true); // Set flag for logoff
            client.closeConnection();
          }
          catch (IOException e)
          {
            display("Error: Could not log off.");
          }
        }
        else
        {
          display("Error: Not connected.");
        }
        break;
      case "#sethost":
        if (!client.isConnected())
        {
          if (parts.length > 1)
          {
            client.setHost(parts[1]);
            display("Host set to " + parts[1]);
          }
          else
          {
            display("Error: Host name required.");
          }
        }
        else
        {
          display("Error: Cannot set host while connected.");
        }
        break;
      case "#setport":
        if (!client.isConnected())
        {
          if (parts.length > 1)
          {
            try
            {
              int port = Integer.parseInt(parts[1]);
              client.setPort(port);
              display("Port set to " + port);
            }
            catch (NumberFormatException e)
            {
              display("Error: Invalid port number.");
            }
          }
          else
          {
            display("Error: Port number required.");
          }
        }
        else
        {
          display("Error: Cannot set port while connected.");
        }
        break;
      case "#login":
        if (!client.isConnected())
        {
          try
          {
            client.setDeliberateLogoff(false); // Reset flag for login
            client.openConnection();
            display("Connected to " + client.getHost() + ":" + client.getPort());
          }
          catch (IOException e)
          {
            display("Error: Could not connect to server.");
          }
        }
        else
        {
          display("Error: Already connected.");
        }
        break;
      case "#gethost":
        display("Current host: " + client.getHost());
        break;
      case "#getport":
        display("Current port: " + client.getPort());
        break;
      default:
        display("Error: Unknown command.");
    }
  }

  /**
   * This method overrides the method in the ChatIF interface.  It
   * displays a message onto the screen.
   *
   * @param message The string to be displayed.
   */
  public void display(String message)
  {
    System.out.println("> " + message);
  }


  //Class methods ***************************************************

  /**
   * This method is responsible for the creation of the Client UI.
   * The host to connect to args[0]
   */
  public static void main(String[] args)
  {
    String host = "";
    int port = DEFAULT_PORT;  //The port number

    try
    {
      host = args[0];
    }
    catch(ArrayIndexOutOfBoundsException e)
    {
      host = "localhost";
    }

    try
    {
      port=Integer.parseInt(args[1]);
    }
    catch(ArrayIndexOutOfBoundsException e){
      port=DEFAULT_PORT;
    }
    catch (NumberFormatException e){
      port=DEFAULT_PORT;
    }
    System.out.println("Connecting to " + host + " on port " + port + "...");
    ClientConsole chat= new ClientConsole(host, port);
    chat.accept();  //Wait for console data
  }
}
//End of ConsoleChat class