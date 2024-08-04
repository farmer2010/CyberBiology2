#################################################################
######################СЮДА ЛУЧШЕ НЕ ХОДИТЬ!######################
#################################################################

import pygame_widgets
from pygame_widgets.button import Button
from pygame_widgets.widget import WidgetHandler
from pygame_widgets.textbox import TextBox
import pygame
pygame.init()

W = pygame.display.Info().current_w
H = pygame.display.Info().current_h
screen = pygame.display.set_mode([W, H], pygame.FULLSCREEN)
input_name = None
save_button = None
show_brain_button = None
pause_button = None

def update():#для сохранения и загрузки мира
    return(load_name.getText())

def init(inp):#создать ссылки для передачи данных между частями программы
    global brain
    global mouse
    global draw_type
    global pause
    global render_brain
    draw_type = inp[0]
    pause = inp[1]
    render_brain = inp[2]
    mouse = inp[3]
    brain = inp[4]

def f1():#смена типа отрисовки
    draw_type[0] = 0
def f2():
    draw_type[0] = 1
def f3():
    draw_type[0] = 2
def f4():
    draw_type[0] = 3
def f5():
    draw_type[0] = 4
def f6():
    draw_type[0] = 5

def mouse1():#сменить режим мыши
    mouse[0] = "set"
def mouse2():
    mouse[0] = "remove"
def mouse3():
    mouse[0] = "select"

def show():#функция кнопки "show brain"
    stop()
    render_brain[0] = not render_brain[0]

def stop():#функция кнопки остановки симуляции
    pause[0] = True
    global pause_button
    if pause_button != None:
        WidgetHandler.removeWidget(pause_button)
        pause_button = None
    global start_button
    start_button = Button(
        screen,
        W - 300,
        120,
        250,
        35,
        text='Start',
        fontSize=24,
        margin=20,
        inactiveColour=(0, 128, 255),
        hoverColour=(0, 140, 255),
        pressedColour=(0, 100, 220),
        radius=0,
        onClick=start
    )

def save_bot(brain):#сохранить мозг бота
    file = open(f"Saved Objects/{input_name.getText()}.dat", "w")
    for i in range(8):
        save_brain = ""
        for x in range(64):
            save_brain += str(brain[i][x]) + " "
        file.write(save_brain + "\n")
    file.close()

def load_bot():#загрузить бота
    global brain
    brain[0] = [0 for i in range(8)]
    try:
        file = open(f"Saved Objects/{load_name.getText()}.dat", "r")
        botcode = file.readlines()
        for i in range(8):
            brain[0][i] = botcode[i].split("\n")[0].split(" ")
            for x in range(64):
                brain[0][i][x] = int(brain[0][i][x])
        file.close()
    except Exception as e:
        print(e)
    

def start():#функция кнопки включения симуляции
    pause[0] = False
    WidgetHandler.removeWidget(start_button)
    global pause_button
    pause_button = Button(
        screen,
        W - 300,
        120,
        250,
        35,
        text='Stop',
        fontSize=24,
        margin=20,
        inactiveColour=(0, 128, 255),
        hoverColour=(0, 140, 255),
        pressedColour=(0, 100, 220),
        radius=0,
        onClick=stop
        )

def set_bot_buttons(obj):#создать кнопки в меню выбранного существа
    global save_button
    global input_name
    global show_brain_button
    save_button = Button(
        screen,
        W - 300,
        520,
        120,
        35,
        text='Save',
        fontSize=24,
        margin=20,
        inactiveColour=(0, 128, 255),
        hoverColour=(0, 140, 255),
        pressedColour=(0, 100, 220),
        radius=0,
        onClick=save_bot,
        onClickParams=([obj.brain])
        )
    input_name = TextBox(
        screen,
        W - 300,
        560,
        250,
        35,
        fontSize=24,
        colour=(60, 60, 60),
        borderColour=(0, 128, 255),
        textColour=(0, 0, 0),
        radius=10,
        borderThickness=5
        )
    show_brain_button = Button(
        screen,
        W - 175,
        520,
        120,
        35,
        text='Show brain',
        fontSize=24,
        margin=20,
        inactiveColour=(0, 128, 255),
        hoverColour=(0, 140, 255),
        pressedColour=(0, 100, 220),
        radius=0,
        onClick=show
        )

def remove_bot_buttons():#удалить кнопки в меню выбранного существа
    global save_button
    global input_name
    global show_brain_button
    if save_button != None and input_name != None:
        WidgetHandler.removeWidget(save_button)
        WidgetHandler.removeWidget(input_name)
        WidgetHandler.removeWidget(show_brain_button)
    save_button = None
    input_name = None
    show_brain_button = None

#все остальные кнопки

bot_color_button = Button(
    screen,
    W - 300,
    200,
    250,
    35,
    text='Bot color',
    fontSize=24,
    margin=20,
    inactiveColour=(0, 128, 255),
    hoverColour=(0, 140, 255),
    pressedColour=(0, 100, 220),
    radius=0,
    onClick=f1
)
bot_energy_button = Button(
    screen,
    W - 300,
    240,
    250,
    35,
    text='Bot energy',
    fontSize=24,
    margin=20,
    inactiveColour=(0, 128, 255),
    hoverColour=(0, 140, 255),
    pressedColour=(0, 100, 220),
    radius=0,
    onClick=f2
)
bot_minerals_button = Button(
    screen,
    W - 300,
    280,
    250,
    35,
    text='Bot minerals',
    fontSize=24,
    margin=20,
    inactiveColour=(0, 128, 255),
    hoverColour=(0, 140, 255),
    pressedColour=(0, 100, 220),
    radius=0,
    onClick=f3
)
bot_age_button = Button(
    screen,
    W - 300,
    320,
    250,
    35,
    text='Bot age',
    fontSize=24,
    margin=20,
    inactiveColour=(0, 128, 255),
    hoverColour=(0, 140, 255),
    pressedColour=(0, 100, 220),
    radius=0,
    onClick=f4
)
predators_button = Button(
    screen,
    W - 300,
    360,
    250,
    35,
    text='Predators',
    fontSize=24,
    margin=20,
    inactiveColour=(0, 128, 255),
    hoverColour=(0, 140, 255),
    pressedColour=(0, 100, 220),
    radius=0,
    onClick=f5
)
type_button = Button(
    screen,
    W - 35,
    200,
    35,
    35,
    text='',
    fontSize=24,
    margin=20,
    inactiveColour=(0, 128, 255),
    hoverColour=(0, 140, 255),
    pressedColour=(0, 100, 220),
    radius=0,
    onClick=f6
)
pause_button = Button(
    screen,
    W - 300,
    120,
    250,
    35,
    text='Stop',
    fontSize=24,
    margin=20,
    inactiveColour=(0, 128, 255),
    hoverColour=(0, 140, 255),
    pressedColour=(0, 100, 220),
    radius=0,
    onClick=stop
)
set_button = Button(
    screen,
    W - 300,
    640,
    250,
    35,
    text='Set',
    fontSize=24,
    margin=20,
    inactiveColour=(0, 128, 255),
    hoverColour=(0, 140, 255),
    pressedColour=(0, 100, 220),
    radius=0,
    onClick=mouse1
    )
remove_button = Button(
    screen,
    W - 300,
    680,
    250,
    35,
    text='Remove',
    fontSize=24,
    margin=20,
    inactiveColour=(0, 128, 255),
    hoverColour=(0, 140, 255),
    pressedColour=(0, 100, 220),
    radius=0,
    onClick=mouse2
    )
select_button = Button(
    screen,
    W - 300,
    720,
    250,
    35,
    text='Select',
    fontSize=24,
    margin=20,
    inactiveColour=(0, 128, 255),
    hoverColour=(0, 140, 255),
    pressedColour=(0, 100, 220),
    radius=0,
    onClick=mouse3
    )
load_name = TextBox(
    screen,
    W - 300,
    780,
    250,
    35,
    fontSize=24,
    colour=(60, 60, 60),
    borderColour=(0, 128, 255),
    textColour=(0, 0, 0),
    radius=10,
    borderThickness=5
    )
load_button = Button(
    screen,
    W - 300,
    820,
    250,
    35,
    text='Load',
    fontSize=24,
    margin=20,
    inactiveColour=(0, 128, 255),
    hoverColour=(0, 140, 255),
    pressedColour=(0, 100, 220),
    radius=0,
    onClick=load_bot
    )
