import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class BlackJack {
	int playerMoney = 1000;
	int currentBet = 0;
	String resultMessage = "";
	boolean gameOverHandled = true;
	
	JButton playButton = new JButton("Start Game");
	
	JLabel moneyLabel = new JLabel();
	JLabel betLabel = new JLabel("Enter your bet:");
	JTextField betField = new JTextField(5);
	JButton betButton = new JButton("Place Bet");

	JButton playAgainButton = new JButton("Play Again");


    private class Card {
        String value;
        String type;

        Card(String value, String type) {
            this.value = value;
            this.type = type;
        }

        public String toString() {
            return value + "-" + type;
        }

        public int getValue() {
            if ("AJQK".contains(value)) { //A J Q K
                if (value.equals("A")) {
                    return 11;
                }
                return 10;
            }
            return Integer.parseInt(value); //2-10
        }

        public boolean isAce() {
            return value.equals("A");
        }

        public String getImagePath() {
            return "./cards/" + toString() + ".png";
        }
        
    }

    ArrayList<Card> deck;
    Random random = new Random(); //shuffle deck

    //dealer
    Card hiddenCard;
    ArrayList<Card> dealerHand;
    int dealerSum;
    int dealerAceCount;

    //player
    ArrayList<Card> playerHand;
    int playerSum;
    int playerAceCount;

    //window
    int boardHeight = 700;
    int boardWidth = boardHeight*5/3;
    

    int cardWidth = 110; //ratio should 1/1.4
    int cardHeight = 154;

    JFrame frame = new JFrame("Black Jack");
    JPanel gamePanel = new JPanel() {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            try {
                //draw hidden card
            	if (hiddenCard != null) {
            	    Image hiddenCardImg = new ImageIcon(getClass().getResource("./cards/Back-Red.png")).getImage();
            	    if (!stayButton.isEnabled()) {
            	        hiddenCardImg = new ImageIcon(getClass().getResource(hiddenCard.getImagePath())).getImage();
            	    }
            	    g.drawImage(hiddenCardImg, (boardWidth/2)-120, 20, cardWidth, cardHeight, null);
            	}


                //draw dealer's hand
            	if (dealerHand != null) {
            	    for (int i = 0; i < dealerHand.size(); i++) {
            	        Card card = dealerHand.get(i);
            	        Image cardImg = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
            	        g.drawImage(cardImg, cardWidth + (boardWidth/2)-115 + (cardWidth + 5) * i, 20, cardWidth, cardHeight, null);
            	    }
            	}


                //draw player's hand
            	if (playerHand != null) {
            	    for (int i = 0; i < playerHand.size(); i++) {
            	        Card card = playerHand.get(i);
            	        Image cardImg = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
            	        g.drawImage(cardImg, 200 + (cardWidth + 5) * i, 420, cardWidth, cardHeight, null);
            	    }
            	}


                if (!stayButton.isEnabled() && !gameOverHandled) {
                	playAgainButton.setVisible(true);
                	
                    dealerSum = reduceDealerAce();
                    playerSum = reducePlayerAce();
                    
                    if (playerSum > 21) {
                    	resultMessage = "Burst, You Lose!";
                    	playerMoney -= currentBet;
                    }
                    else if (dealerSum > 21) {
                    	resultMessage = "Dealer Burst, You Win!";
                    	playerMoney += currentBet;
                    }
                    else if (playerSum == dealerSum) resultMessage = "Tie!";
                    else if (playerSum > dealerSum) {
                    	resultMessage = "You Win!";
                    	playerMoney += currentBet;
                    }
                    else {
                    	resultMessage = "You Lose!";
                    	playerMoney -= currentBet;
                    }
                    
                    
                    
                    if (resultMessage != null && !resultMessage.isEmpty()) {
                    g.setFont(new Font("Arial", Font.PLAIN, 30));
                    g.setColor(Color.white);
                    g.drawString("Dealer: " + dealerSum, 20, 270);
                    g.drawString("Player: " + playerSum, 20, 300);
                    g.drawString(resultMessage, 220, 250);
                    }
                    
                    gameOverHandled = true;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    JPanel buttonPanel = new JPanel();
    JButton hitButton = new JButton("Hit");
    JButton stayButton = new JButton("Stay");

    BlackJack() {
    	gameOverHandled = true;
    	resultMessage = "";
    	playAgainButton.setVisible(false);

        frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gamePanel.setLayout(new BorderLayout());
        gamePanel.setBackground(new Color(53, 101, 77));
        frame.add(gamePanel);

//        moneyLabel.setText("Money: $" + playerMoney);
//        moneyLabel.setForeground(Color.WHITE);
//        gamePanel.add(moneyLabel, BorderLayout.NORTH);

        
//        startGame();
        
        buttonPanel.add(playButton);
        playButton.setFocusable(false);
        
        

		betLabel.setVisible(false);
		betField.setVisible(false);
		betButton.setVisible(false);
		hitButton.setEnabled(false);
		stayButton.setEnabled(false);
		playAgainButton.setVisible(false);

        
        
        
        betField.setFocusable(true);
        buttonPanel.add(betLabel);
        buttonPanel.add(betField);
        buttonPanel.add(betButton);
        buttonPanel.add(hitButton);
        buttonPanel.add(stayButton);
        playAgainButton.setVisible(false);
        buttonPanel.add(playAgainButton);

        hitButton.setEnabled(false);
        stayButton.setEnabled(false);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        
        
        playButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				gameOverHandled = false;
				playButton.setVisible(false); 
//		        betLabel.setVisible(true);
//		        betField.setVisible(true);
//		        betButton.setVisible(true);
		        
		        startGame();
		        
		        hitButton.setEnabled(true);
		        stayButton.setEnabled(true);
		        
		        gamePanel.repaint();
		        
			}
		});
        
        hitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Card card = deck.remove(deck.size()-1);
                playerSum += card.getValue();
                playerAceCount += card.isAce() ? 1 : 0;
                playerHand.add(card);
                if (reducePlayerAce() > 21) { //A + 2 + J --> 1 + 2 + J
                    hitButton.setEnabled(false);
                    stayButton.setEnabled(false);
                }
            	gamePanel.repaint();
            }
        });

        stayButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	hitButton.setEnabled(false);
            	stayButton.setEnabled(false);

            	while (dealerSum < 17) {
            	    Card card = deck.remove(deck.size() - 1);
            	    dealerSum += card.getValue();
            	    dealerAceCount += card.isAce() ? 1 : 0;
            	    dealerHand.add(card);
            	}

            	moneyLabel.setText("Money: $" + playerMoney);
            	playAgainButton.setVisible(true);
            	gamePanel.repaint();

            }
        });

//        betButton.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                try {
//                    currentBet = Integer.parseInt(betField.getText());
//                    if (currentBet <= 0 || currentBet > playerMoney) {
//                        JOptionPane.showMessageDialog(frame, "Invalid bet.");
//                        return;
//                    }
//
//                    startGame(); 
//                    hitButton.setEnabled(true);
//                    stayButton.setEnabled(true);
//                    betButton.setEnabled(false);
//                    betField.setEnabled(false);
//                    resultMessage = "";
//
//                } catch (NumberFormatException ex) {
//                    JOptionPane.showMessageDialog(frame, "Please enter a valid number.");
//                }
//                gamePanel.repaint();
//            }
//        });

        playAgainButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	if (playerMoney <= 0) {
                    JOptionPane.showMessageDialog(frame, "You're out of money!");
                    System.exit(0);
                }

                
//                betButton.setEnabled(true);
//                betButton.setVisible(true);
//                betLabel.setVisible(true);
//                betField.setEnabled(true);
//                betField.setVisible(true);
//                betField.setText("");
                playAgainButton.setVisible(false);
                
                
                dealerHand.clear();
                playerHand.clear();
                resultMessage = "";
                
                startGame();
                hitButton.setEnabled(true);
                stayButton.setEnabled(true);
                
                gamePanel.repaint();
            }
        });

        
        
        gamePanel.repaint();
    }

    public void startGame() {
    	
    	playAgainButton.setVisible(false);
    	gameOverHandled = false;
    	resultMessage = "";
    	
        //deck
        buildDeck();
        shuffleDeck();

        //dealer
        dealerHand = new ArrayList<Card>();
        dealerSum = 0;
        dealerAceCount = 0;

        hiddenCard = deck.remove(deck.size()-1); //remove card at last index
        dealerSum += hiddenCard.getValue();
        dealerAceCount += hiddenCard.isAce() ? 1 : 0;

        Card card = deck.remove(deck.size()-1);
        dealerSum += card.getValue();
        dealerAceCount += card.isAce() ? 1 : 0;
        dealerHand.add(card);




        //player
        playerHand = new ArrayList<Card>();
        playerSum = 0;
        playerAceCount = 0;

        for (int i = 0; i < 2; i++) {
            card = deck.remove(deck.size()-1);
            playerSum += card.getValue();
            playerAceCount += card.isAce() ? 1 : 0;
            playerHand.add(card);
        }
        

    }

    public void buildDeck() {
        deck = new ArrayList<Card>();
        String[] values = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
        String[] types = {"C", "D", "H", "S"};

        for (int i = 0; i < types.length; i++) {
            for (int j = 0; j < values.length; j++) {
                Card card = new Card(values[j], types[i]);
                deck.add(card);
            }
        }


    }

    public void shuffleDeck() {
        for (int i = 0; i < deck.size(); i++) {
            int j = random.nextInt(deck.size());
            Card currCard = deck.get(i);
            Card randomCard = deck.get(j);
            deck.set(i, randomCard);
            deck.set(j, currCard);
        }


    }

    public int reducePlayerAce() {
        while (playerSum > 21 && playerAceCount > 0) {
            playerSum -= 10;
            playerAceCount -= 1;
        }
        return playerSum;
    }

    public int reduceDealerAce() {
        while (dealerSum > 21 && dealerAceCount > 0) {
            dealerSum -= 10;
            dealerAceCount -= 1;
        }
        return dealerSum;
    }
    public void resetGame() {
        hitButton.setEnabled(true);
        stayButton.setEnabled(true);
        resultMessage = "";
        startGame();
        gamePanel.repaint();
    }

}
