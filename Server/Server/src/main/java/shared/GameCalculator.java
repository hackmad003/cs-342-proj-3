package shared;

import java.util.ArrayList;

/////////////////////////
// GAME CALCULATOR CLASS //
/////////////////////////
public class GameCalculator {
    
    ///////////////////////
    // CALCULATE RESULTS //
    ///////////////////////
    public static GameResult calculateGameResult(ArrayList<Card> playerHand, 
                                                   ArrayList<Card> dealerHand,
                                                   int anteBet, 
                                                   int playBet, 
                                                   int pairPlusBet) {
        
        GameResult result = new GameResult();
        
        // Check if dealer qualifies
        boolean dealerQualifies = ThreeCardLogic.dealerQualifies(dealerHand);
        result.setDealerQualifies(dealerQualifies);
        
        int antePlayWinnings = 0;
        String gameOutcome;
        String message;
        
        if (!dealerQualifies) {
            // Dealer doesn't qualify - ante bet returned, play bet pushed
            antePlayWinnings = 0;
            gameOutcome = "PUSH";
            message = "Dealer does not qualify (no Queen high). Ante and Play bets returned.";
        } else {
            // Dealer qualifies - compare hands
            int comparison = ThreeCardLogic.compareHands(dealerHand, playerHand);
            
            if (comparison == 2) {
                // Player wins
                antePlayWinnings = anteBet + playBet;
                gameOutcome = "WIN";
                message = "You beat the dealer! Won $" + antePlayWinnings + " on Ante + Play.";
            } 
            else if (comparison == 1) {
                // Dealer wins
                antePlayWinnings = -(anteBet + playBet);
                gameOutcome = "LOSE";
                message = "Dealer wins. Lost $" + Math.abs(antePlayWinnings) + " on Ante + Play.";
            }
            else {
                // Tie
                antePlayWinnings = 0;
                gameOutcome = "PUSH";
                message = "Push - Tie with dealer. Ante and Play bets returned.";
            }
        }
        
        result.setAntePlayWinnings(antePlayWinnings);
        result.setGameOutcome(gameOutcome);
        
        // Calculate Pair Plus winnings
        int pairPlusWinnings = 0;
        String pairPlusOutcome = "NONE";
        
        if (pairPlusBet > 0) {
            pairPlusWinnings = ThreeCardLogic.evalPPWinnings(playerHand, pairPlusBet);
            
            if (pairPlusWinnings > 0) {
                pairPlusOutcome = "WIN";
                message += "\nPair Plus wins $" + pairPlusWinnings + "!";
            } else {
                pairPlusOutcome = "LOSE";
                pairPlusWinnings = -pairPlusBet;
                message += "\nPair Plus loses $" + pairPlusBet + ".";
            }
        }
        
        result.setPairPlusWinnings(pairPlusWinnings);
        result.setPairPlusOutcome(pairPlusOutcome);
        
        // Calculate total winnings
        int totalWinnings = antePlayWinnings + pairPlusWinnings;
        result.setTotalWinnings(totalWinnings);
        result.setMessage(message);
        
        return result;
    }
    
    /////////////////////////
    // CALCULATE FOLD LOSS //
    /////////////////////////
    public static int calculateFoldLoss(int anteBet, int pairPlusBet) {
        return -(anteBet + pairPlusBet);
    }
    
    
    ///////////////////////
    // GAME RESULT CLASS //
    ///////////////////////
    public static class GameResult {
        private boolean dealerQualifies;
        private int antePlayWinnings;
        private int pairPlusWinnings;
        private int totalWinnings;
        private String gameOutcome;      // "WIN", "LOSE", "PUSH"
        private String pairPlusOutcome;  // "WIN", "LOSE", "NONE"
        private String message;
        
        // Getters and Setters
        public boolean isDealerQualifies() { return dealerQualifies; }
        public void setDealerQualifies(boolean dealerQualifies) { 
            this.dealerQualifies = dealerQualifies; 
        }
        
        public int getAntePlayWinnings() { return antePlayWinnings; }
        public void setAntePlayWinnings(int antePlayWinnings) { 
            this.antePlayWinnings = antePlayWinnings; 
        }
        
        public int getPairPlusWinnings() { return pairPlusWinnings; }
        public void setPairPlusWinnings(int pairPlusWinnings) { 
            this.pairPlusWinnings = pairPlusWinnings; 
        }
        
        public int getTotalWinnings() { return totalWinnings; }
        public void setTotalWinnings(int totalWinnings) { 
            this.totalWinnings = totalWinnings; 
        }
        
        public String getGameOutcome() { return gameOutcome; }
        public void setGameOutcome(String gameOutcome) { 
            this.gameOutcome = gameOutcome; 
        }
        
        public String getPairPlusOutcome() { return pairPlusOutcome; }
        public void setPairPlusOutcome(String pairPlusOutcome) { 
            this.pairPlusOutcome = pairPlusOutcome; 
        }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { 
            this.message = message; 
        }
    }
    
} //EOC