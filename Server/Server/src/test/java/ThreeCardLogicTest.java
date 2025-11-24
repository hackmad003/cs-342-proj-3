
import org.junit.jupiter.api.Test;

import shared.Card;
import shared.ThreeCardLogic;

import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

public class ThreeCardLogicTest {
    
    // Helper method to create a hand
    private ArrayList<Card> createHand(char suit1, int val1, char suit2, int val2, char suit3, int val3) {
        ArrayList<Card> hand = new ArrayList<>();
        hand.add(new Card(suit1, val1));
        hand.add(new Card(suit2, val2));
        hand.add(new Card(suit3, val3));
        return hand;
    }
    
    // ========== evalHand() Tests ==========
    
    @Test
    @DisplayName("Test Straight Flush")
    public void testStraightFlush() {
        ArrayList<Card> hand = createHand('H', 5, 'H', 6, 'H', 7);
        assertEquals(5, ThreeCardLogic.evalHand(hand), "Should be Straight Flush");
    }
    
    @Test
    @DisplayName("Test Three of a Kind")
    public void testThreeOfKind() {
        ArrayList<Card> hand = createHand('H', 8, 'D', 8, 'C', 8);
        assertEquals(4, ThreeCardLogic.evalHand(hand), "Should be Three of a Kind");
    }
    
    @Test
    @DisplayName("Test Straight")
    public void testStraight() {
        ArrayList<Card> hand = createHand('H', 9, 'D', 10, 'C', 11);
        assertEquals(3, ThreeCardLogic.evalHand(hand), "Should be Straight");
    }
    
    @Test
    @DisplayName("Test Ace-Low Straight")
    public void testAceLowStraight() {
        ArrayList<Card> hand = createHand('H', 14, 'D', 2, 'C', 3);
        assertEquals(3, ThreeCardLogic.evalHand(hand), "Should be Ace-Low Straight");
    }
    
    @Test
    @DisplayName("Test Flush")
    public void testFlush() {
        ArrayList<Card> hand = createHand('S', 2, 'S', 7, 'S', 12);
        assertEquals(2, ThreeCardLogic.evalHand(hand), "Should be Flush");
    }
    
    @Test
    @DisplayName("Test Pair")
    public void testPair() {
        ArrayList<Card> hand = createHand('H', 5, 'D', 5, 'C', 9);
        assertEquals(1, ThreeCardLogic.evalHand(hand), "Should be Pair");
    }
    
    @Test
    @DisplayName("Test High Card")
    public void testHighCard() {
        ArrayList<Card> hand = createHand('H', 2, 'D', 7, 'C', 11);
        assertEquals(0, ThreeCardLogic.evalHand(hand), "Should be High Card");
    }
    
    @Test
    @DisplayName("Test Invalid Hand Size")
    public void testInvalidHandSize() {
        ArrayList<Card> hand = new ArrayList<>();
        hand.add(new Card('H', 2));
        hand.add(new Card('D', 5));
        
        assertThrows(IllegalArgumentException.class, () -> {
            ThreeCardLogic.evalHand(hand);
        }, "Should throw exception for hand with less than 3 cards");
    }
    
    // ========== evalPPWinnings() Tests ==========
    
    @Test
    @DisplayName("Test Pair Plus - Straight Flush Payout")
    public void testPPStraightFlush() {
        ArrayList<Card> hand = createHand('D', 10, 'D', 11, 'D', 12);
        assertEquals(400, ThreeCardLogic.evalPPWinnings(hand, 10), 
                    "Straight Flush should pay 40 to 1");
    }
    
    @Test
    @DisplayName("Test Pair Plus - Three of a Kind Payout")
    public void testPPThreeOfKind() {
        ArrayList<Card> hand = createHand('H', 7, 'D', 7, 'C', 7);
        assertEquals(300, ThreeCardLogic.evalPPWinnings(hand, 10), 
                    "Three of a Kind should pay 30 to 1");
    }
    
    @Test
    @DisplayName("Test Pair Plus - Straight Payout")
    public void testPPStraight() {
        ArrayList<Card> hand = createHand('H', 4, 'D', 5, 'C', 6);
        assertEquals(60, ThreeCardLogic.evalPPWinnings(hand, 10), 
                    "Straight should pay 6 to 1");
    }
    
    @Test
    @DisplayName("Test Pair Plus - Flush Payout")
    public void testPPFlush() {
        ArrayList<Card> hand = createHand('C', 3, 'C', 7, 'C', 13);
        assertEquals(30, ThreeCardLogic.evalPPWinnings(hand, 10), 
                    "Flush should pay 3 to 1");
    }
    
    @Test
    @DisplayName("Test Pair Plus - Pair Payout")
    public void testPPPair() {
        ArrayList<Card> hand = createHand('H', 9, 'D', 9, 'C', 4);
        assertEquals(10, ThreeCardLogic.evalPPWinnings(hand, 10), 
                    "Pair should pay 1 to 1");
    }
    
    @Test
    @DisplayName("Test Pair Plus - No Payout for High Card")
    public void testPPHighCard() {
        ArrayList<Card> hand = createHand('H', 2, 'D', 8, 'C', 13);
        assertEquals(0, ThreeCardLogic.evalPPWinnings(hand, 10), 
                    "High Card should not pay");
    }
    
    // ========== compareHands() Tests ==========
    
    @Test
    @DisplayName("Test Player Wins with Better Hand Type")
    public void testPlayerWinsBetterHand() {
        ArrayList<Card> dealer = createHand('H', 2, 'D', 7, 'C', 11); // High Card
        ArrayList<Card> player = createHand('S', 5, 'S', 8, 'S', 12);  // Flush
        
        assertEquals(2, ThreeCardLogic.compareHands(dealer, player), 
                    "Player should win with Flush vs High Card");
    }
    
    @Test
    @DisplayName("Test Dealer Wins with Better Hand Type")
    public void testDealerWinsBetterHand() {
        ArrayList<Card> dealer = createHand('H', 8, 'D', 8, 'C', 8);  // Three of a Kind
        ArrayList<Card> player = createHand('H', 5, 'D', 5, 'C', 9);  // Pair
        
        assertEquals(1, ThreeCardLogic.compareHands(dealer, player), 
                    "Dealer should win with Three of a Kind vs Pair");
    }
    
    @Test
    @DisplayName("Test Player Wins with Higher Cards (Same Hand Type)")
    public void testPlayerWinsHighCards() {
        ArrayList<Card> dealer = createHand('H', 5, 'D', 5, 'C', 9);  // Pair of 5s
        ArrayList<Card> player = createHand('S', 10, 'D', 10, 'C', 3); // Pair of 10s
        
        assertEquals(2, ThreeCardLogic.compareHands(dealer, player), 
                    "Player should win with higher pair");
    }
    
    @Test
    @DisplayName("Test Dealer Wins with Higher Cards (Same Hand Type)")
    public void testDealerWinsHighCards() {
        ArrayList<Card> dealer = createHand('H', 2, 'D', 8, 'C', 14); // Ace High
        ArrayList<Card> player = createHand('S', 3, 'D', 7, 'C', 13);  // King High
        
        assertEquals(1, ThreeCardLogic.compareHands(dealer, player), 
                    "Dealer should win with Ace high vs King high");
    }
    
    @Test
    @DisplayName("Test Tie - Exact Same Hand Values")
    public void testTie() {
        ArrayList<Card> dealer = createHand('H', 7, 'D', 7, 'C', 3);
        ArrayList<Card> player = createHand('S', 7, 'C', 7, 'D', 3);
        
        assertEquals(0, ThreeCardLogic.compareHands(dealer, player), 
                    "Should be a tie with identical hand values");
    }
    
    // ========== dealerQualifies() Tests ==========
    
    @Test
    @DisplayName("Test Dealer Qualifies with Queen High")
    public void testDealerQualifiesQueenHigh() {
        ArrayList<Card> hand = createHand('H', 2, 'D', 8, 'C', 12); // Queen High
        assertTrue(ThreeCardLogic.dealerQualifies(hand), 
                  "Dealer should qualify with Queen high");
    }
    
    @Test
    @DisplayName("Test Dealer Qualifies with King High")
    public void testDealerQualifiesKingHigh() {
        ArrayList<Card> hand = createHand('H', 3, 'D', 7, 'C', 13); // King High
        assertTrue(ThreeCardLogic.dealerQualifies(hand), 
                  "Dealer should qualify with King high");
    }
    
    @Test
    @DisplayName("Test Dealer Qualifies with Ace High")
    public void testDealerQualifiesAceHigh() {
        ArrayList<Card> hand = createHand('H', 5, 'D', 9, 'C', 14); // Ace High
        assertTrue(ThreeCardLogic.dealerQualifies(hand), 
                  "Dealer should qualify with Ace high");
    }
    
    @Test
    @DisplayName("Test Dealer Qualifies with Pair")
    public void testDealerQualifiesWithPair() {
        ArrayList<Card> hand = createHand('H', 4, 'D', 4, 'C', 7); // Pair
        assertTrue(ThreeCardLogic.dealerQualifies(hand), 
                  "Dealer should qualify with any pair");
    }
    
    @Test
    @DisplayName("Test Dealer Does Not Qualify with Jack High")
    public void testDealerDoesNotQualifyJackHigh() {
        ArrayList<Card> hand = createHand('H', 2, 'D', 8, 'C', 11); // Jack High
        assertFalse(ThreeCardLogic.dealerQualifies(hand), 
                   "Dealer should not qualify with Jack high");
    }
    
    @Test
    @DisplayName("Test Dealer Does Not Qualify with 10 High")
    public void testDealerDoesNotQualify10High() {
        ArrayList<Card> hand = createHand('H', 3, 'D', 7, 'C', 10); // 10 High
        assertFalse(ThreeCardLogic.dealerQualifies(hand), 
                   "Dealer should not qualify with 10 high");
    }
    
    // ========== Edge Cases ==========
    

    
    @Test
    @DisplayName("Test Straight Flush Beats Three of a Kind")
    public void testStraightFlushBeatsThreeOfKind() {
        ArrayList<Card> dealer = createHand('H', 9, 'D', 9, 'C', 9);  // Three of a Kind
        ArrayList<Card> player = createHand('S', 3, 'S', 4, 'S', 5);  // Straight Flush
        
        assertEquals(2, ThreeCardLogic.compareHands(dealer, player), 
                    "Straight Flush should beat Three of a Kind");
    }
    
    @Test
    @DisplayName("Test Multiple Bets on Pair Plus")
    public void testMultiplePairPlusBets() {
        ArrayList<Card> hand = createHand('D', 6, 'D', 7, 'D', 8); // Straight Flush
        
        assertEquals(200, ThreeCardLogic.evalPPWinnings(hand, 5), 
                    "Bet of $5 should return $200");
        assertEquals(1000, ThreeCardLogic.evalPPWinnings(hand, 25), 
                    "Bet of $25 should return $1000");
    }
}