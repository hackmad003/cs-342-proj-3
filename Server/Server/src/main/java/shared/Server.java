package shared;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

public class Server {
    
    int count = 1;
    public ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
    TheServer server;
    private Consumer<Serializable> callback;
    
    public Server(Consumer<Serializable> call) {
        callback = call;
        server = new TheServer();
        server.start();
    }
    
    public class TheServer extends Thread {
        
        public void run() {
            
            try (ServerSocket mysocket = new ServerSocket(5555);) {
                callback.accept("Server is waiting for a client!");
                
                while(true) {
                    ClientThread c = new ClientThread(mysocket.accept(), count);
                    callback.accept("Client has connected to server: client #" + count);
                    clients.add(c);
                    c.start();
                    count++;
                }
            } catch(Exception e) {
                callback.accept("Server socket did not launch");
            }
        }
    }
    
    class ClientThread extends Thread {
        
        Socket connection;
        int count;
        ObjectInputStream in;
        ObjectOutputStream out;
        Deck deck; // Each client gets their own deck
        
        ClientThread(Socket s, int count) {
            this.connection = s;
            this.count = count;
            this.deck = new Deck(); // Initialize deck for this client
        }
        
        public void run() {
            
            try {
                out = new ObjectOutputStream(connection.getOutputStream());
                in = new ObjectInputStream(connection.getInputStream());
                connection.setTcpNoDelay(true);
            } catch(Exception e) {
                callback.accept("Streams not open for client #" + count);
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
                        break;
                    }
                    
                } catch(Exception e) {
                    callback.accept("Connection lost with client #" + count);
                    clients.remove(this);
                    break;
                }
            }
        }
        
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
        
        private void handlePlayerDecision(PokerInfo clientInfo) {
            try {
                if (clientInfo.isPlayerFolded()) {
                    // Player folded
                    callback.accept("Client #" + count + " folded");
                    
                    int loss = clientInfo.getAnteBet() + clientInfo.getPairPlusBet();
                    
                    PokerInfo response = new PokerInfo();
                    response.setMessageType("GAME_RESULT");
                    response.setGameResult("FOLD");
                    response.setTotalWinnings(-loss);
                    response.setMessage("You folded. Lost $" + loss);
                    
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
        
        private void evaluateGame(PokerInfo clientInfo) {
            try {
                ArrayList<Card> playerHand = clientInfo.getPlayerHand();
                ArrayList<Card> dealerHand = clientInfo.getDealerHand();
                
                int anteBet = clientInfo.getAnteBet();
                int playBet = clientInfo.getPlayBet();
                int pairPlusBet = clientInfo.getPairPlusBet();
                
                int totalWinnings = 0;
                String message = "";
                
                // Check if dealer qualifies
                boolean dealerQualifies = ThreeCardLogic.dealerQualifies(dealerHand);
                
                PokerInfo response = new PokerInfo();
                response.setMessageType("GAME_RESULT");
                response.setDealerQualifies(dealerQualifies);
                
                if (!dealerQualifies) {
                    message = "Dealer does not have Queen high. Play wager returned.";
                    totalWinnings = 0;
                    response.setGameResult("PUSH");
                } else {
                    // Compare hands (2=player wins, 1=dealer wins, 0=tie)
                    int result = ThreeCardLogic.compareHands(dealerHand, playerHand);
                    
                    if (result == 2) {
                        totalWinnings = anteBet + playBet;
                        message = "You beat the dealer! Won $" + totalWinnings;
                        response.setGameResult("WIN");
                    } 
                    else if (result == 1) {
                        totalWinnings = -(anteBet + playBet);
                        message = "Dealer wins. Lost $" + Math.abs(totalWinnings);
                        response.setGameResult("LOSE");
                    }
                    else {
                        totalWinnings = 0;
                        message = "Push - Tie with dealer.";
                        response.setGameResult("PUSH");
                    }
                }
                
                // Evaluate Pair Plus
                if (pairPlusBet > 0) {
                    int ppWinnings = ThreeCardLogic.evalPPWinnings(playerHand, pairPlusBet);
                    
                    if (ppWinnings > 0) {
                        message += "\nPair Plus wins $" + ppWinnings + "!";
                        totalWinnings += ppWinnings;
                        response.setPairPlusResult("WIN");
                    } else {
                        message += "\nPair Plus loses $" + pairPlusBet;
                        totalWinnings -= pairPlusBet;
                        response.setPairPlusResult("LOSE");
                    }
                    response.setPairPlusWinnings(ppWinnings);
                }
                
                response.setTotalWinnings(totalWinnings);
                response.setMessage(message);
                
                callback.accept("Client #" + count + " result: " + 
                               (totalWinnings >= 0 ? "Won" : "Lost") + " $" + 
                               Math.abs(totalWinnings));
                
                out.writeObject(response);
                
            } catch (Exception e) {
                callback.accept("Error evaluating game for client #" + count);
            }
        }
    }
}