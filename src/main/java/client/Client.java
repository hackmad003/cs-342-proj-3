package client;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.function.Consumer;

public class Client extends Thread {
    
    Socket socketClient;
    ObjectOutputStream out;
    ObjectInputStream in;
    private Consumer<Serializable> callback;
    private String ip;   // ADD THIS
    private int port;    // ADD THIS
    
    public Client(Consumer<Serializable> call) {
        callback = call;
        this.ip = "127.0.0.1";  // Default
        this.port = 5555;       // Default
    }
    
    
    // ADD THIS CONSTRUCTOR
    public Client(Consumer<Serializable> call, String ip, int port) {
        callback = call;
        this.ip = ip;
        this.port = port;
    }
    
    
    public void run() {
        
        try {
            socketClient = new Socket(ip, port); // USE VARIABLES
            out = new ObjectOutputStream(socketClient.getOutputStream());
            in = new ObjectInputStream(socketClient.getInputStream());
            socketClient.setTcpNoDelay(true);
        } catch(Exception e) {
            callback.accept("Cannot connect to server");
            return;
        }
        
        callback.accept("Connected to server!");
        
        // Listen for messages from server
        while(true) {
            try {
                PokerInfo message = (PokerInfo) in.readObject();
                callback.accept(message); // Send to GUI
            } catch(Exception e) {
                callback.accept("Connection to server lost");
                break;
            }
        }
    }
    
    public void send(PokerInfo data) {
        try {
            out.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    
    
 // ADD THIS METHOD
    public void disconnect() {
        try {
            // Send disconnect message
            PokerInfo disconnectInfo = new PokerInfo();
            disconnectInfo.setMessageType("DISCONNECT");
            out.writeObject(disconnectInfo);
            
            // Close streams
            if (in != null) in.close();
            if (out != null) out.close();
            if (socketClient != null) socketClient.close();
            
        } catch (Exception e) {
            System.out.println("Error disconnecting: " + e.getMessage());
        }
    }
    
    
    
    
    
    
} //EOC