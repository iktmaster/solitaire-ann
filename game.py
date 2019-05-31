import sys
import pygame
import solitaire

import AI as ai

game = ai.game

pygame.init()

clock = pygame.time.Clock()
screen = pygame.display.set_mode([1280, 720])

GREEN = [20, 106, 44]
BLUE = [10, 10, 235]
TEAL = [0,128,128]
BLACK = [0,0,0]

selected_cards = []

def removeHighlight():
    for card in selected_cards:
        card.highlight = False

def setHighlight(card):
    removeHighlight()
    card.highlight = True
    return [card]

def mouse_on_card(pos, card_pos):
    if pos[0] >= card_pos[0] and pos[0] <= card_pos[0] + 128:
        if pos[1] >= card_pos[1] and pos[1] <= card_pos[1] + 128:
            return True
        else:
            return False
    else:
        return False


def getImages():
    card_dict = {}
    ranks = solitaire.RANKS()
    suits = solitaire.SUITS()
    for suit in suits:
        for rank in ranks:
            name = str(rank) + "_of_" + str(suit)
            img = pygame.image.load("playing_cards/" + name + ".png").convert()
            img = pygame.transform.scale(img, (64*2, 89*2))
            card_dict[name] = img
    card_back = pygame.image.load("playing_cards/card_back.png").convert()
    card_back = pygame.transform.scale(card_back, (64*2, 89*2))
    card_dict["card_back"] = card_back
    nothing = pygame.image.load("playing_cards/nothing.png").convert()
    nothing = pygame.transform.scale(nothing, (64*2, 89*2))
    nothing.set_colorkey((255,255,255))
    card_dict["nothing"] = nothing
    return card_dict

imgs = getImages()

def draw_stock():
    pos = (25, 25)
    if len(game.stock) > 0:
        screen.blit(imgs["card_back"],pos)
    else:
        pygame.draw.ellipse(screen,[0, 0, 50],[60,85,60,60],5)

def draw_waste_pile():
    pos = (200, 25)
    offset = 25
    if len(game.waste_pile) > 2:
        cards = game.waste_pile[-3:]
        for i in range(len(cards)):
            screen.blit(imgs[str(cards[i])],(pos[0]+offset*i,pos[1]))
            cards[i].updatePos(pos[0]+offset*i, pos[1])
    elif len(game.waste_pile) > 1:
        cards = game.waste_pile[-2:]
        screen.blit(imgs[str(cards[0])],pos)
        screen.blit(imgs[str(cards[1])],(pos[0]+offset,pos[1]))
    elif len(game.waste_pile) > 0:
        cards = game.waste_pile[-1:]
        screen.blit(imgs[str(cards[0])],pos)
    else:
        screen.blit(imgs["nothing"],pos)

def draw_foundations():
    pos = (550, 25)
    offset = 175

    foundations = game.foundations
    for i in range(len(foundations)):
        if len(foundations[i]) > 0:
            card = foundations[i][:-1]
            screen.blit(imgs[str(card)],(pos[0]+offset*i, pos[1]))
            card.updatePos(pos[0]+offset*i, pos[1])
        else:
            screen.blit(imgs["nothing"],(pos[0]+offset*i, pos[1]))

def draw_tableau():
    pos = (25, 230)
    xoffset = 175
    yoffset = 25
    tableu = game.tableau
    for i in range(len(tableu)):
        cards = tableu[i]
        for j in range(len(cards)):
            card = cards[j]
            if card.faceup:
                screen.blit(imgs[str(card)],(pos[0]+xoffset*i,pos[1]+yoffset*j))
            else:
                screen.blit(imgs["card_back"],(pos[0]+xoffset*i,pos[1]+yoffset*j))
            card.updatePos(pos[0]+xoffset*i, pos[1]+yoffset*j)

def draw_highlight():
    if len(selected_cards) > 0:
        card = selected_cards[0]
        if card.highlight:
            pygame.draw.rect(screen, BLUE, pygame.Rect(card.x, card.y, 64*2, 89*2), 5)

while True:
    # --- Event logic should go here
    for event in pygame.event.get():
        if event.type == pygame.QUIT: sys.exit()

        if event.type == pygame.KEYDOWN:
            if event.key == pygame.K_SPACE:
                ai.predict(game)
            if event.key == pygame.K_RETURN:
                game = solitaire.Solitaire()
                game.setup()

        if event.type == pygame.MOUSEBUTTONDOWN:
            pass

        if event.type == pygame.MOUSEBUTTONUP:
            pos = pygame.mouse.get_pos()
            click_outside = True

            #stock
            if mouse_on_card(pos, [25,25]):
                game.draw_stock_cards()
            #waste pile
            if mouse_on_card(pos, [250, 25]):
                card = game.get_top_wastepile()
                selected_cards = setHighlight(card)
                click_outside = False
            #tableau
            for tab in game.tableau:
                for card in tab:
                    if mouse_on_card(pos, [card.x, card.y]):
                        selected_cards = setHighlight(card)
                        click_outside = False

            if click_outside:
                removeHighlight()
                selected_cards = []

    # --- Game logic should go here


    # --- Drawing code should go here
    screen.fill(GREEN)

    draw_stock()
    draw_waste_pile()
    draw_foundations()
    draw_tableau()
    draw_highlight()

    # --- Go ahead and update the screen with what we've drawn.
    pygame.display.flip()

    # --- Limit to 20 frames per second
    clock.tick(20)

pygame.quit()