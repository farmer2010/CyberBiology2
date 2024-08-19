import pygame
pygame.init()

def border(number, border, border2=0):
    if number < border2:
        number = border2
    elif number > border:
        number = border
    return(number)

def get_image(color):
    img = pygame.Surface((10, 10))
    img.fill((0, 0, 0))
    pygame.draw.rect(img, color, (1, 1, 8, 8))
    #shadow_color = (
    #    border(color[0] - 50, 255),
    #    border(color[1] - 50, 255),
    #    border(color[2] - 50, 255)
    #)
    #color2 = (
    #    border(color[0] + 50, 255),
    #    border(color[1] + 50, 255),
    #    border(color[2] + 50, 255)
    #)
    #pygame.draw.rect(img, color2, (1, 1, 8, 1))
    #pygame.draw.rect(img, color2, (1, 1, 1, 8))
    #pygame.draw.rect(img, shadow_color, (8, 1, 1, 8))
    #pygame.draw.rect(img, shadow_color, (1, 8, 8, 1))
    return(img)

def get_organics_image():
    img = pygame.Surface((10, 10))
    img.fill((0, 0, 255))
    pygame.draw.rect(img, (0, 0, 0), (1, 1, 8, 8))
    pygame.draw.rect(img, (128, 128, 128), (2, 2, 6, 6))
    img.set_colorkey((0, 0, 255))
    return(img)
