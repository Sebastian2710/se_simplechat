// This file is analogous to ClientConsole for the server side.

import java.io.*;
import common.*;
/**
 * This class constructs the UI for the chat server console. It implements the
 * ChatIF interface to activate the display() method.
 */
public class ServerConsole implements ChatIF {
    // Instance variables
    EchoServer server;

    // Constructors
    /**
     * Constructs an instance of the ServerConsole.
     *
     * @param server The EchoServer instance.
     */
    public ServerConsole(EchoServer server) {
        this.server = server;
    }

    // Instance methods
    /**
     * This method waits for input from the console. Once received, it echoes it to the server's console
     * and sends it to all clients.
     */
    public void accept() {
        try {
            BufferedReader fromConsole = new BufferedReader(new InputStreamReader(System.in));
            String message;
            while (true) {
                message = fromConsole.readLine();
                if (message.startsWith("#")) {
                    handleCommand(message);
                } else {
                    server.sendToAllClients("SERVER MSG> " + message);
                }
            }
        } catch (Exception ex) {
            System.out.println("Unexpected error while reading from console!");
        }
    }

    private void handleCommand(String command) {
        String[] parts = command.split("\\s+", 2);
        String cmd = parts[0].toLowerCase();

        switch (cmd) {
            case "#quit":
                display("Shutting down server...");
                try { server.close(); }
                catch (IOException e) { display("Error closing."); }
                System.exit(0);
                break;
            case "#close":
                try { server.close(); display("Server closed."); }
                catch (IOException e) { display("Error closing server."); }
                break;
            case "#setport":
                if (server.isListening() || server.getClientConnections().length > 0) {
                    display("Cannot change port while clients are connected or while server is listening.");
                } else if (parts.length > 1) {
                    try { server.setPort(Integer.parseInt(parts[1])); display("Port set to " + parts[1]); }
                    catch (NumberFormatException e) { display("Invalid port."); }
                } else { display("Port required."); }
                break;
            case "#start":
                if (server.isListening()) {
                    display("Error: Server running.");
                } else {
                    try { server.listen(); display("Server started."); }
                    catch (IOException e) { display("Error starting server."); }
                }
                break;
            case "#getport":
                display("Port: " + server.getPort());
                break;
            default:
                display("Unknown command.");
        }
    }
    /**
     * This method overrides the method in the ChatIF interface. It displays a message onto the screen.
     *
     * @param message The string to be displayed.
     */
    public void display(String message) {
        System.out.println("> " + message);
    }

    public static void main(String[] args) {
        int port = EchoServer.DEFAULT_PORT;
        if (args.length > 0) {
            try { port = Integer.parseInt(args[0]); }
            catch (NumberFormatException e) { System.out.println("Invalid port. Using default."); }
        }
        EchoServer server = new EchoServer(port);
        ServerConsole console = new ServerConsole(server);
        try { server.listen(); }
        catch (Exception e) { System.out.println("ERROR - Could not listen!"); }
        console.accept();
    }
}