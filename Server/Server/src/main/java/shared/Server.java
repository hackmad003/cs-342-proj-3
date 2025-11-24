package shared;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

//////////////////
// SERVER CLASS //
//////////////////
public class Server {
    
    int count = 1;
    public ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
    TheServer server;
    private Consumer<Serializable> callback;
    private int port;  
    
    ////////////
    // SERVER //
    ////////////
    public Server(Consumer<Serializable> call, int port) { 
        callback = call;
        this.port = port;  
        server = new TheServer();
        server.start();
    }
    
    //////////////////////
    // THE SERVER CLASS //
    //////////////////////
    public class TheServer extends Thread {
        
        public void run() {
            
            try (ServerSocket mysocket = new ServerSocket(port);) {  
                callback.accept("Server is waiting for a client on port " + port + "!");
                
                while(true) {
                    // Accept connections (same as before)
                    Socket clientSocket = mysocket.accept();
                    
                    // Check capacity
                    boolean canAccept = false;
                    synchronized(clients) {
                        if (clients.size() < 8) {
                            canAccept = true;
                        }
                    }
                    
                    if (!canAccept) {
                        callback.accept("Server at maximum capacity (" + clients.size() + "/8). Rejecting connection...");
                        clientSocket.close();
                        callback.accept("Rejected client connection - server full");
                        continue;
                    }
                    
                    ClientThread c = new ClientThread(clientSocket, count);
                    
                    synchronized(clients) {
                        clients.add(c);
                    }
                    
                    callback.accept("Client has connected to server: client #" + count + " (Total: " + clients.size() + "/8)");
                    c.start();
                    count++;
                }
            } catch(Exception e) {
                callback.accept("Server socket did not launch on port " + port);
                e.printStackTrace();
            }
        }
    }
    
    ////////////////////////
    // CLIENT THREAD CLASS //
    ////////////////////////
    class ClientThread extends Thread {
        
        Socket connection;
        int count;
        ObjectInputStream in;
        ObjectOutputStream out;
        Deck deck;
        
        ClientThread(Socket s, int count) {
            this.connection = s;
            this.count = count;
            this.deck = new Deck();
        }
        
        /////////
        // RUN //
        /////////
        public void run() {
            
            try {
                out = new ObjectOutputStream(connection.getOutputStream());
                in = new ObjectInputStream(connection.getInputStream());
                connection.setTcpNoDelay(true);
            } catch(Exception e) {
                callback.accept("Streams not open for client #" + count);
                removeClient();
                return;
            }
            
            callback.accept("Client #" + count + " is ready to play");
            
            // Game loop for this client
            while(true) {
                try {
                    PokerInfo clientInfo = (PokerInfo) in.readObject();
                    
                    // Handle different message types
                    String msgType = clientInfo.getMessageType();
                    
                    if (msgType.equals("PLACE_BETS")) {
                        handleBetting(clientInfo);
                    }
                    else if (msgType.equals("PLAYER_DECISION")) {
                        handlePlayerDecision(clientInfo);
                    }
                    else if (msgType.equals("DISCONNECT")) {
                        callback.accept("Client #" + count + " disconnected");
                        removeClient();
                        break;
                    }
                    
                } catch(Exception e) {
                    callback.accept("Connection lost with client #" + count);
                    removeClient();
                    break;
                }
            }
            
            // Close connection
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch(Exception e) {
                // Ignore
            }
        }
        
        /////////////////////
        // REMOVE CLIENT   //
        /////////////////////
        private void removeClient() {
            synchronized(clients) {
                clients.remove(this);
                int currentCount = clients.size();
                callback.accept("Client #" + count + " removed. (Total: " + currentCount + "/8)");
                
                callback.accept("CLIENT_COUNT_UPDATE:" + currentCount);
            }
        }
        
        ////////////////////
        // HANDLE BETTING //
        ////////////////////
        private void handleBetting(PokerInfo clientInfo) {
            try {
                callback.accept("Client #" + count + " placed bets - Ante: $" + 
                               clientInfo.getAnteBet() + ", Pair Plus: $" + 
                               clientInfo.getPairPlusBet());
                
                // Shuffle and deal cards
                deck.shuffle();
                
                ArrayList<Card> playerHand = new ArrayList<>();
                ArrayList<Card> dealerHand = new ArrayList<>();
                
                for (int i = 0; i < 3; i++) {
                    playerHand.add(deck.dealCard());
                    dealerHand.add(deck.dealCard());
                }
                
                // Create response
                PokerInfo response = new PokerInfo();
                response.setMessageType("DEAL_CARDS");
                response.setPlayerHand(playerHand);
                response.setDealerHand(dealerHand);
                response.setAnteBet(clientInfo.getAnteBet());
                response.setPairPlusBet(clientInfo.getPairPlusBet());
                response.setMessage("Cards dealt! Play or fold?");
                
                out.writeObject(response);
                
            } catch (Exception e) {
                callback.accept("Error dealing cards to client #" + count);
            }
        }
        
        
        ////////////////////////////
        // HANDLE PLAYER DECISION //
        ////////////////////////////
        private void handlePlayerDecision(PokerInfo clientInfo) {
            try {
                if (clientInfo.isPlayerFolded()) {
                    // Player folded
                    callback.accept("Client #" + count + " folded");
                    
                    int loss = GameCalculator.calculateFoldLoss(
                        clientInfo.getAnteBet(), 
                        clientInfo.getPairPlusBet()
                    );
                    
                    PokerInfo response = new PokerInfo();
                    response.setMessageType("GAME_RESULT");
                    response.setGameResult("FOLD");
                    response.setTotalWinnings(loss);
                    response.setMessage("You folded. Lost $" + Math.abs(loss));
                    response.setDealerHand(clientInfo.getDealerHand());
                    response.setPlayerHand(clientInfo.getPlayerHand());
                    
                    out.writeObject(response);
                    
                } else {
                    // Player chose to play
                    callback.accept("Client #" + count + " playing with $" + 
                                   clientInfo.getPlayBet());
                    
                    evaluateGame(clientInfo);
                }
                
            } catch (Exception e) {
                callback.accept("Error processing player decision for client #" + count);
            }
        }
        
        ///////////////////
        // EVALUATE GAME //
        ///////////////////
        private void evaluateGame(PokerInfo clientInfo) {
            try {
                ArrayList<Card> playerHand = clientInfo.getPlayerHand();
                ArrayList<Card> dealerHand = clientInfo.getDealerHand();
                
                int anteBet = clientInfo.getAnteBet();
                int playBet = clientInfo.getPlayBet();
                int pairPlusBet = clientInfo.getPairPlusBet();
                
                GameCalculator.GameResult gameResult = GameCalculator.calculateGameResult(
                    playerHand, dealerHand, anteBet, playBet, pairPlusBet
                );
                
                PokerInfo response = new PokerInfo();
                response.setMessageType("GAME_RESULT");
                response.setDealerQualifies(gameResult.isDealerQualifies());
                response.setDealerHand(dealerHand);
                response.setPlayerHand(playerHand);
                response.setGameResult(gameResult.getGameOutcome());
                response.setPairPlusResult(gameResult.getPairPlusOutcome());
                response.setPairPlusWinnings(gameResult.getPairPlusWinnings());
                response.setTotalWinnings(gameResult.getTotalWinnings());
                response.setMessage(gameResult.getMessage());
                
                callback.accept("Client #" + count + " result: " + 
                               (gameResult.getTotalWinnings() >= 0 ? "Won" : "Lost") + " $" + 
                               Math.abs(gameResult.getTotalWinnings()));
                
                out.writeObject(response);
                
            } catch (Exception e) {
                callback.accept("Error evaluating game for client #" + count);
            }
        }
    }

} //EOC