//********************************************************************
//  GameState.java              @version 1.06
//    State of one game of Klondike solitaire.
//  Copyright (c) 2012 Daniel R. Collins. All rights reserved.
//  See the bottom of this file for any licensing information.
//********************************************************************

public class GameState {
	private boolean gameOver;
	private int cardsDrawn, maxPasses, pass;
	
	private int lastMove = 0;
	public int p1 = 0;
	public int p2 = 0;
	public int p3 = 0;
	public int card1;
	public int card2;

	private Pile[] pile;        // Piles of cards in game
	final int NUM_PILES = 13;
	final int IDX_DECK = 0;
	final int IDX_WASTE = 1;
	final int IDX_FOUND = 2;
	final int IDX_TABLE = 6;

	//-----------------------------------------------------------------
	//  Constructor (blank)
	//-----------------------------------------------------------------
   public GameState(int cardsDrawn, int maxPasses){
		pass = 1;
		gameOver = false;
		this.maxPasses = maxPasses;
		this.cardsDrawn = cardsDrawn;
		pile = new Pile[NUM_PILES];		
		for (int i = 0; i < NUM_PILES; i++) {
			pile[i] = new Pile();		
		}
	}

	//-----------------------------------------------------------------
	//  Constructor (copy)
	//-----------------------------------------------------------------
   public GameState(GameState old) {
		pass = old.pass;
		gameOver = old.gameOver;
		maxPasses = old.maxPasses;
		cardsDrawn = old.cardsDrawn;
		pile = new Pile[NUM_PILES];		
		for (int i = 0; i < NUM_PILES; i++) {
			pile[i] = new Pile(old.pile[i]);		
		}
	}

	//-----------------------------------------------------------------
	//  Accessors
	//-----------------------------------------------------------------
	public boolean isOver () { return gameOver; }
	public int getcardsDrawn () { return cardsDrawn; }
	public int getMaxPasses () { return maxPasses; }
	public int getPass () { return pass; }
	public Pile deck () { return pile[IDX_DECK]; }
	public Pile waste () { return pile[IDX_WASTE]; }
	public Pile found (int i) { return pile[IDX_FOUND+i]; }
	public Pile table (int i) { return pile[IDX_TABLE+i]; }
	public Pile getPile (int i) { return pile[i]; }
	
	public int getMove() { return lastMove; }
	
	//-----------------------------------------------------------------
	//  Setup a new game
	//-----------------------------------------------------------------
	public void setupNewGame () {
		pile[IDX_DECK] = new Pile(true);
		deck().shuffle();
		for (int i = 0; i < 7; i++) {
			deck().drawToPile(table(i));
			table(i).getTopCard().setFaceUp();
			for (int j = i+1; j < 7; j++) {
				deck().drawToPile(table(j));
			}
		}			
	}

	//-----------------------------------------------------------------
	//  Check if game won
	//-----------------------------------------------------------------
	public boolean isGameWon () {
		for (int i = 2; i <= 5; i++) {
			if (pile[i].size() < 13) 
				return false;
		}
		return true;
	}
	
	//-----------------------------------------------------------------
	//  Scrubs hidden data on player view copy
	//-----------------------------------------------------------------
	public void scrubHiddenData () {

		// On first pass, scrub cards face-down in deck
		if (pass == 0) {
			for (int i = 0; i < deck().size(); i++) {
				deck().get(i).scrubData();
			}					
		}		
	
		// On any pass, scrub cards face-down in tables
		for (int i = 0; i < NUM_PILES-IDX_TABLE; i++) {
			for (int j = 0; j < table(i).size(); j++) {
				Card card = table(i).get(j);
				if (!card.isFaceUp()) {
					card.scrubData();
				}
			}		
		}	
	}

	public void setPs(int p1, int p2, int p3){
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
	}
	
	//-----------------------------------------------------------------
	//  Is integer in range (inclusive)?
	//-----------------------------------------------------------------
	boolean isInRange (int num, int low, int high) {
		return (low <= num && num <= high);	
	}
	
	//-----------------------------------------------------------------
	//  Player move call: return if move valid
	//-----------------------------------------------------------------
	public boolean playerMoveCall (int p1, int p2, int p3) {
		//setPs(0, 0, 0);
		lastMove = -1;
		if (handleMoveToDeck(p1, p2)){
			//setPs(p1, p2, 0);
			lastMove = 1;
			return true;
		}
		if (handleMoveToWaste(p1, p2)){
			//setPs(p1, p2, 0);
			lastMove = 1;
			return true;
		}
		if (handleMoveToFound(p1, p2)){
			//setPs(p1, p2, 0);
			
			
			//583-586
			if (p1 == 1) {
				lastMove = 583 + (p2-2);
			}
			else { 
				// 587 - 590
				if (p1 == 6) lastMove = 587 + (p2-2);
				// 591 - 594
				if (p1 == 7) lastMove = 591 + (p2-2);
				// 595 - 598
				if (p1 == 8) lastMove = 595 + (p2-2);
				//599 - 602
				if (p1 == 9) lastMove = 599 + (p2-2);
				// 603 - 606
				if (p1 == 10) lastMove = 603 + (p2-2);
				// 607 - 610
				if (p1 == 11) lastMove = 607 + (p2-2);
				//611 - 614
				if (p1 == 12) lastMove = 611 + (p2-2);
			}
			return true;
		}
		if (handleMoveToTable(p1, p2, p3)){
			
			
			if (p1 <= 5){
				//2-8 from waste
				if (p1 == 1) lastMove = p1+(p2-5);
				//9-15 from found 1
				if (p1 == 2) lastMove = 1+p1+p2;
				//16-22 from found 2
				if (p1 == 3) lastMove = 7+p1+p2;
				//23-29 from found 3
				if (p1 == 4) lastMove = 13+p1+p2;
				//30-36 from found 4
				if (p1 == 5) lastMove = 19+p1+p2;
			} else {
	
				//table to table
				int counter = 0;
				int move = 0;
				
				for(int i = 0; i < 7; i++) 
				{
					for (int j = 0; j < 7; j++)
					{
						if (j==i) continue;
						for (int k = 0; k < 13; k++){
							counter++;
							if ((i+6)==p1 && (j+6)==p2 && k==p3){
								move = counter;
							}
						}
					}
				}
				
				lastMove = 36 + move;
			
			}
			//setPs(p1, p2, p3);
			return true;
		}
		if (handleMoveFlipTop(p1, p2)){
			lastMove = -1;
			//setPs(p1, p2, -1);
			return true;
		}
		if (handleMoveSurrender(p1, p2)){
			lastMove = -1;
			//setPs(p1, p2, 0);
			return true;
		}
		return false;	
	}	

	//-----------------------------------------------------------------
	//  Handle move to deck (recycle waste, start new pass)
	//-----------------------------------------------------------------
	boolean handleMoveToDeck (int src, int dst) {
		if (dst == 0) {
			if (src != 1) return false;
			if (waste().isEmpty()) return false;
			if (deck().isEmpty() && pass < maxPasses) {
				waste().flipWholePileFaceDown(deck());
				pass++;
				return true;
			}		
		}
		return false;
	}

	//-----------------------------------------------------------------
	//  Handle move to waste (turn cards from deck)
	//-----------------------------------------------------------------
	boolean handleMoveToWaste (int src, int dst) {
		if (dst == 1) {
			if (src != 0) return false;
			if (deck().isEmpty()) return false;
			for (int i = 0; i < cardsDrawn && !deck().isEmpty(); i++) {
				deck().drawToPile(waste());
				waste().getTopCard().setFaceUp();
			}
			return true;
		}
		return false;
	}

	//-----------------------------------------------------------------
	//  Handle move to found (one card from waste or table)
	//-----------------------------------------------------------------
	boolean handleMoveToFound (int src, int dst) {
		if (isInRange(dst, 2, 5)) {
			if (!(src == 1 || isInRange(src, 6, 12))) return false;
			if (pile[src].isEmpty()) return false;
			Card card = pile[src].getTopCard();
			if (!card.isFaceUp()) return false;

			// Ace to empty foundation
			if (card.getRank() == 1 && pile[dst].isEmpty()) {
				pile[src].drawToPile(pile[dst]);
				return true;
			}

			// Same suit, one more rank
			if (pile[dst].isEmpty()) return false;
			Card destCard = pile[dst].getTopCard();
			if ((card.getSuit() == destCard.getSuit())
					&& (card.getRank() == destCard.getRank()+1)) {
				pile[src].drawToPile(pile[dst]);
				return true;
			}
		}
		return false;
	}

	//-----------------------------------------------------------------
	//  Handle move to table (any non-deck, possibly more than one)
	//-----------------------------------------------------------------
	boolean handleMoveToTable (int src, int dst, int idx) {
		if (isInRange(dst, 6, 12)) {
			if (src == 0 || src == dst) return false;
			if (pile[src].isEmpty()) return false;
			if (isInRange(src, 1, 5)) idx = pile[src].size()-1; // one card
			if (!isInRange(idx, 0, pile[src].size()-1)) return false;
			Card card = pile[src].get(idx);
			if (!card.isFaceUp()) return false;
			
			// King to empty tableau
			if (card.getRank() == 13 && pile[dst].isEmpty()) {
				//card1 = Integer.parseInt(card.toString());
				//card2 = 0;
				pile[src].moveSubpileToPile(idx, pile[dst]);
				//lastMove = src + idx;
				return true;
			}

			// Reverse color, one less rank
			if (pile[dst].isEmpty()) return false;
			Card destCard = pile[dst].getTopCard();
			if ((card.isBlack() != destCard.isBlack())
					&& (card.getRank() == destCard.getRank()-1)) {
				//card1 = Integer.parseInt(card.toString());
				//card2 = Integer.parseInt(destCard.toString());
				pile[src].moveSubpileToPile(idx, pile[dst]);
				return true;
			}
		}
		return false;
	}

	//-----------------------------------------------------------------
	//  Handle move flip top card (coded by src == dst)
	//-----------------------------------------------------------------
	boolean handleMoveFlipTop (int src, int dst) {
		if (dst == src) {
			if (!isInRange(src, 6, 12)) return false;
			if (pile[src].isEmpty()) return false;
			if (pile[src].getTopCard().isFaceUp()) return false;
			pile[src].getTopCard().setFaceUp();
			return true;
		}	
		return false;
	}
	
	//-----------------------------------------------------------------
	//  Handle surrender game
	//-----------------------------------------------------------------
	boolean handleMoveSurrender (int src, int dst) {
		if (dst == 0 && src == 0) {
			gameOver = true;
			return true;
		}
		return false;
	}
}

