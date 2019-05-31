import random
import math

def RANKS(): return [ "ace", "2", "3", "4", "5", "6", "7","8", "9", "10", "jack", "queen", "king" ]
def NUMBERS(): return [ 1,2,3,4,5,6,7,8,9,10,11,12,13 ]
def SUITS(): return [ "spades", "hearts", "diamonds", "clubs" ]

class Card:

    def __init__( self, rank, suit, faceup):
        self.rank = rank
        self.suit = suit
        self.faceup = faceup
        self.highlight = False
        self.x = 0
        self.y = 0

    def faceUp(self):
        self.faceup = True

    def updatePos(self, x, y):
        self.x = x
        self.y = y

    def num(self):
        return RANKS().index(self.rank) + 1

    def stateNumber(self):
        suit = SUITS().index(self.suit)
        rank = RANKS().index(self.rank) + 1
        return suit*13 + rank

    def __str__( self ):
        return self.rank + "_of_" + self.suit

class Deck:

    def __init__( self ):
        self.contents = []
        self.contents = [ Card( rank, suit , False) for rank in RANKS() for suit in SUITS() ]

    def shuffle(self):
        random.shuffle( self.contents )

    def draw_card(self):
        return self.contents.pop()

    def draw_cards(self, num):
        cards = []
        for i in range(0, num):
            cards.append(self.contents.pop())
        return cards

class Solitaire:

    def __init__( self ):
        self.my_deck = Deck()

        self.stock = []
        self.waste_pile = []

        self.foundations = [[],[],[],[]]
        self.tableau = []

    def setup(self):
        self.my_deck.shuffle()
        for i in range(1, 8):
            cards = self.my_deck.draw_cards(i)
            cards[len(cards)-1].faceUp()
            self.tableau.append(cards)

        self.stock = self.my_deck.contents

        #self.draw_stock_cards()

    def get_top_wastepile(self):
        return self.waste_pile[len(self.waste_pile)-1]

    def get_top_foundation(self, i):
        return self.foundations[i][len(self.foundations[i])-1]

    def isEmpty(self, pile):
        return len(pile) == 0

    def draw_stock_cards(self):
        try:
            cards = []
            #for i in range(0, 3):
            card = self.stock.pop()
            card.faceUp()
            cards.append(card)
            self.waste_pile += cards
        except:
            pass


    def move_waste_to_tab(self, pos):
        try:
            card = self.waste_pile.pop()
            self.tableau[pos].append(card)
        except:
            pass

    #TODO move empty pile
    def move_tab_to_tab(self, cards_to_move, from_pos, to_pos):
        self.tableau[to_pos].extend(cards_to_move)
        for card in cards_to_move:
            self.tableau[from_pos].remove(card)

    def move_tab_to_foun(self, from_pos, card, foundation_pos):
        self.foundations[foundation_pos] += card
        self.tableau[from_pos].remove(card)

    def move_valid(self, move):

        if (len(self.tableau[move[0]]) > 0):

            card1 = self.tableau[move[0]][len(self.tableau[move[0]])-1]
            try:
                card2 = self.tableau[move[1]][len(self.tableau[move[1]])-1]
            except:
                return False

            if (card1.num() == card2.num() - 1):
                return True

        return False

    def do_move(self, move):
        card = self.tableau[move[0]][len(self.tableau[move[0]])-1]
        self.move_tab_to_tab([card], move[0], move[1])

    def get_state(self):
        state = []

        if self.isEmpty(self.stock): state.append(0)
        else: state.append(-1)

        if self.isEmpty(self.waste_pile): state.append(0)
        else: state.append(self.get_top_wastepile().stateNumber())

        for i in range(4):
            if self.isEmpty(self.foundations[i]): state.append(0)
            else:
                card = self.get_top_foundation(i)
                state.append(card.stateNumber())

        for j in range(7):
            for x in range(13+j):
                try:
                    card = self.tableau[j][x]
                    if card.faceup:
                        state.append(card.stateNumber())
                    else:
                        state.append(-1)
                except:
                    state.append(0)

        return state

    def get_state_readable(self):
        state = self.get_state()
        readable = []
        for num in state:
            if num == -1: readable.append("??")
            elif num == 0: readable.append("--")
            else:
                suit = 0
                if (num > 13): suit = 1
                if (num > 26): suit = 2
                if (num > 39): suit = 3
                rank = (num-1) % 13
                #print(RANKS()[rank][0].upper(), SUITS()[suit], num)
                readable.append(RANKS()[rank][0].upper() + "" + SUITS()[suit][0].upper())
        return readable