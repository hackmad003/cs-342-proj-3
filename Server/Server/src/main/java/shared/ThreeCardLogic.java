package shared;

import java.util.ArrayList;
import java.util.Collections;

public class ThreeCardLogic {
    
    /**
     * Evaluates a poker hand and returns its rank
     * @param hand - ArrayList of 3 cards
     * @return int representing hand value:
     *         0 = High Card
     *         1 = Pair
     *         2 = Flush
     *         3 = Straight
     *         4 = Three of a Kind
     *         5 = Straight Flush
     */
    public static int evalHand(ArrayList<Card> hand) {
        if (hand.size() != 3) {
            throw new IllegalArgumentException("Hand must have exactly 3 cards");
        }
        
        boolean isFlush = checkFlush(hand);
        boolean isStraight = checkStraight(hand);
        boolean isThreeOfKind = checkThreeOfKind(hand);
        boolean isPair = checkPair(hand);
        
        // Check from highest to lowest
        if (isFlush && isStraight) {
            return 5; // Straight Flush
        }
        if (isThreeOfKind) {
            return 4; // Three of a Kind
        }
        if (isStraight) {
            return 3; // Straight
        }
        if (isFlush) {
            return 2; // Flush
        }
        if (isPair) {
            return 1; // Pair
        }
        
        return 0; // High Card
    }
    
    /**
     * Calculates Pair Plus winnings
     * @param hand - player's hand
     * @param bet - amount bet on Pair Plus
     * @return winnings (0 if hand doesn't qualify)
     */
    public static int evalPPWinnings(ArrayList<Card> hand, int bet) {
        int handValue = evalHand(hand);
        
        switch(handValue) {
            case 5: // Straight Flush
                return bet * 40;
            case 4: // Three of a Kind
                return bet * 30;
            case 3: // Straight
                return bet * 6;
            case 2: // Flush
                return bet * 3;
            case 1: // Pair
                return bet * 1;
            default: // High Card - no payout
                return 0;
        }
    }
    
    /**
     * Compares dealer's hand to player's hand
     * @param dealer - dealer's hand
     * @param player - player's hand
     * @return 0 = tie, 1 = dealer wins, 2 = player wins
     */
    public static int compareHands(ArrayList<Card> dealer, ArrayList<Card> player) {
        int dealerHandValue = evalHand(dealer);
        int playerHandValue = evalHand(player);
        
        // Compare hand types first
        if (playerHandValue > dealerHandValue) {
            return 2; // Player wins
        }
        if (dealerHandValue > playerHandValue) {
            return 1; // Dealer wins
        }
        
        // Same hand type - compare high cards
        return compareHighCards(dealer, player);
    }
    
    /**
     * Checks if dealer has at least Queen high
     * @param hand - dealer's hand
     * @return true if dealer qualifies
     */
    public static boolean dealerQualifies(ArrayList<Card> hand) {
        int handValue = evalHand(hand);
        
        // If dealer has any pair or better, they qualify
        if (handValue >= 1) {
            return true;
        }
        
        // If just high card, need Queen or better (value >= 12)
        int highCard = getHighCard(hand);
        return highCard >= 12; // 12 = Queen, 13 = King, 14 = Ace
    }
    
    // ========== HELPER METHODS ==========
    
    private static boolean checkFlush(ArrayList<Card> hand) {
        char suit = hand.get(0).getSuit();
        return hand.get(1).getSuit() == suit && hand.get(2).getSuit() == suit;
    }
    
    private static boolean checkStraight(ArrayList<Card> hand) {
        ArrayList<Integer> values = new ArrayList<>();
        for (Card c : hand) {
            values.add(c.getValue());
        }
        Collections.sort(values);
        
        // Check for consecutive values
        if (values.get(1) == values.get(0) + 1 && 
            values.get(2) == values.get(1) + 1) {
            return true;
        }
        
        // Check for Ace-low straight (A-2-3)
        if (values.get(0) == 2 && values.get(1) == 3 && values.get(2) == 14) {
            return true;
        }
        
        return false;
    }
    
    private static boolean checkThreeOfKind(ArrayList<Card> hand) {
        int val = hand.get(0).getValue();
        return hand.get(1).getValue() == val && hand.get(2).getValue() == val;
    }
    
    private static boolean checkPair(ArrayList<Card> hand) {
        int v1 = hand.get(0).getValue();
        int v2 = hand.get(1).getValue();
        int v3 = hand.get(2).getValue();
        
        return (v1 == v2) || (v1 == v3) || (v2 == v3);
    }
    
    private static int getHighCard(ArrayList<Card> hand) {
        int max = 0;
        for (Card c : hand) {
            if (c.getValue() > max) {
                max = c.getValue();
            }
        }
        return max;
    }
    
    /**
     * Compares hands when they're the same type
     * @return 0 = tie, 1 = dealer wins, 2 = player wins
     */
    private static int compareHighCards(ArrayList<Card> dealer, ArrayList<Card> player) {
        // Get sorted values (highest to lowest)
        ArrayList<Integer> dealerValues = getSortedValues(dealer);
        ArrayList<Integer> playerValues = getSortedValues(player);
        
        // Compare from highest to lowest
        for (int i = 2; i >= 0; i--) {
            if (playerValues.get(i) > dealerValues.get(i)) {
                return 2; // Player wins
            }
            if (dealerValues.get(i) > playerValues.get(i)) {
                return 1; // Dealer wins
            }
        }
        
        return 0; // Complete tie
    }
    
    private static ArrayList<Integer> getSortedValues(ArrayList<Card> hand) {
        ArrayList<Integer> values = new ArrayList<>();
        for (Card c : hand) {
            values.add(c.getValue());
        }
        Collections.sort(values);
        return values;
    }
}