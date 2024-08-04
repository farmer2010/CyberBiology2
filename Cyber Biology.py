import buttons
import pygame_widgets
from random import randint as rand
import bot
import pygame
pygame.init()

keep_going = True
pause = [False]
render_brain = [False]
mouse = ["select"]
botcode = [None]
bots = [0]
white = (255, 255, 255)
grey = (90, 90, 90)
render = 1

W = pygame.display.Info().current_w
H = pygame.display.Info().current_h
screen = pygame.display.set_mode([W, H], pygame.FULLSCREEN)
description = "Cyber Biology 2"
pygame.display.set_caption(description)
objects = pygame.sprite.Group()
world_scale = [
    int((W - 300) / 10),
    int(H / 10)
    ]
#world_scale = [60, 60]
world = [["none" for y in range(world_scale[1])]for x in range(world_scale[0])]
draw_type = [0]
font = pygame.font.SysFont(None, 24)
buttons.init([draw_type, pause, render_brain, mouse, botcode])
timer = pygame.time.Clock()
selection = None
input_name = None
save_button = None

def render_text(text, pos, color=(0, 0, 0), size=24, centerx=False, centery=False):
    font = pygame.font.SysFont(None, size)
    text_img = font.render(text, True, color)
    text_rect = text_img.get_rect()
    if centerx:
        text_rect.centerx = pos[0]
    else:
        text_rect.x = pos[0]
    if centery:
        text_rect.centery = pos[1]
    else:
        text_rect.y = pos[1]
    screen.blit(text_img, text_rect)

def draw_brain(brain):
    pygame.draw.rect(screen, grey, (0, 0, 360, 360))
    for x in range(64):
        pos2 = [x % 8, x // 8]
        pos = [pos2[0] * 45, pos2[1] * 45]
        pygame.draw.rect(screen, (128, 128, 128), (pos[0], pos[1], 40, 40))
        render_text(str(brain[x]), (pos[0] + 20, pos[1] + 20), centerx=True, centery=True)

def mouse_function():
    global selection
    mousepos = pygame.mouse.get_pos()
    botpos = [
        int(mousepos[0] / 10),
        int(mousepos[1] / 10)
        ]
    if mouse[0] == "select":
        if botpos[0] < world_scale[0]:
            for u in objects:
                if u.name == "bot":
                    if u.pos == botpos:
                        if selection != u:
                            buttons.set_bot_buttons(u)
                            selection = u
                            break
                    else:
                        buttons.remove_bot_buttons()
                        selection = None
                else:
                    buttons.remove_bot_buttons()
                    selection = None
    elif mouse[0] == "remove":
        if botpos[0] < world_scale[0]:
            for u in objects:
                if u.pos == botpos:
                    u.kill()
                    break
            world[botpos[0]][botpos[1]] = "none"
    elif mouse[0] == "set":
        if botpos[0] < world_scale[0]:
            if botcode[0] != None:
                if world[botpos[0]][botpos[1]] == "none":
                    new_bot = bot.Bot(botpos.copy(), (0, 0, 255), world, objects, bots)
                    new_bot.commands = botcode[0].copy()
                    objects.add(new_bot)
                    world[botpos[0]][botpos[1]] = "bot"

screen.fill(white)
steps = 0
steps2 = 0
mousedown = 0

while keep_going:
    steps2 += 1
    steps2 %= 60
    events = pygame.event.get()
    for event in events:
        if event.type == pygame.QUIT:
            keep_going = False
        if event.type == pygame.MOUSEBUTTONDOWN:
            mousedown = 1
        if event.type == pygame.MOUSEBUTTONUP:
            mousedown = 0
        if event.type == pygame.KEYDOWN:
            if event.key == pygame.K_ESCAPE:
                keep_going = False
            if event.key == pygame.K_F1:
                steps = 0
                for i in objects:
                    i.kill()
                    world[i.pos[0]][i.pos[1]] = "none"
                for x in range(1000):
                    while True:
                        color = (
                            rand(0, 255),
                            rand(0, 255),
                            rand(0, 255)
                            )
                        pos = [
                            rand(0, world_scale[0] - 1),
                            rand(0, world_scale[1] - 1)
                            ]
                        if world[pos[0]][pos[1]] == "none":
                            objects.add(bot.Bot(pos, color, world, objects, bots))
                            world[pos[0]][pos[1]] = "bot"
                            break
            if event.key == pygame.K_F2:
                render = not render
    if not pause[0]:
        steps += 1
        bots[0] = 0
        objects.update(draw_type[0])
    if mousedown:
        mouse_function()#обработка нажатий мыши
    if render:#отрисовка
        screen.fill(grey)
        pygame.draw.rect(screen, white, (0, 0, world_scale[0] * 10, world_scale[1] * 10))
        objects.draw(screen)
        pygame_widgets.update(events)
        if draw_type[0] == 0:
            txt = "Bot color view"
        elif draw_type[0] == 1:
            txt = "Bot energy view"
        elif draw_type[0] == 2:
            txt = "Bot minerals view"
        elif draw_type[0] == 3:
            txt = "Bot age view"
        else:
            txt = "Predators view"
        render_text("Main:", (W - 300, 0))
        render_text("Steps: " + str(steps) + ", fps: " + str(int(timer.get_fps())), (W - 300, 20))
        render_text("Objects: " + str(len(objects)) + ", bots: " + str(bots[0]), (W - 300, 40))
        render_text("Render type: " + txt, (W - 300, 60))
        render_text("Render types:", (W - 300, 160))
        render_text("Selection:", (W - 300, 400))
        render_text("Mouse:", (W - 300, 600))
        render_text("Mouse function: " + mouse[0], (W - 300, 80))
        render_text("Load:", (W - 300, 760))
        if selection == None:
            render_brain[0] = False
            render_text("None", (W - 300, 420))
        else:
            if render_brain[0]:
                draw_brain(selection.commands)
            if int(steps2 // 30) % 2:
                pygame.draw.rect(screen, (255, 0, 0), selection.rect)
            render_text("Energy: " + str(selection.energy), (W - 300, 420))
            render_text("Minerals: " + str(selection.minerals), (W - 300, 440))
            render_text("Age: " + str(selection.age), (W - 300, 460))
            render_text("Position: " + str(selection.pos), (W - 300, 480))
            if world[selection.pos[0]][selection.pos[1]] != "bot":
                buttons.remove_bot_buttons()
                selection = None
        pygame.display.update()
    timer.tick(240)
pygame.quit()
