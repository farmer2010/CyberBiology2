import json
from bot import Bot
from organics import Organics
import pygame

def save(world, objects, steps, name):
    for_save = {"Steps" : steps, "World" : world, "Objects" : []}
    for obj in objects:
        if obj.name == "organics":
            for_save["Objects"].append(
                {
                    "Name" : "organics",
                    "Energy" : obj.energy,
                    "IsFall" : obj.is_falling,
                    "Pos" : obj.pos
                }
            )
        elif obj.name == "bot":
            for_save["Objects"].append(
                {
                    "Name" : "bot",
                    "Energy" : obj.energy,
                    "Minerals" : obj.minerals,
                    "Age" : obj.age,
                    "Rotate" : obj.rotate,
                    "Index" : obj.index,
                    "Color" : obj.color,
                    "Pos" : obj.pos,
                    "Commands" : obj.commands,
                    "AttackCount" : obj.attack_count,
                    "PhotoCount" : obj.photo_count,
                    "MineralsCount" : obj.minerals_count
                }
            )
    file = open(f"Saved Worlds/{name}.json", "w")
    json.dump(for_save, file)
    file.close()

def load(name, bots):
    file = open(f"Saved Worlds/{name}.json", "r")
    ret = json.load(file)
    file.close()
    steps = ret["Steps"]
    world = ret["World"]
    objects = pygame.sprite.Group()
    for obj in ret["Objects"]:
        if obj["Name"] == "organics":
            new_organics = Organics(obj["Pos"], world, objects, energy=obj["Energy"])
            new_organics.is_falling = obj["IsFall"]
            objects.add(new_organics)
        elif obj["Name"] == "bot":
            new_bot = Bot(obj["Pos"], obj["Color"], world, objects, bots, energy=obj["Energy"])
            new_bot.index = obj["Index"]
            new_bot.commands = obj["Commands"]
            new_bot.minerals = obj["Minerals"]
            new_bot.age = obj["Age"]
            new_bot.rotate = obj["Rotate"]
            new_bot.minerals_count = obj["MineralsCount"]
            new_bot.attack_count = obj["AttackCount"]
            new_bot.photo_count = obj["PhotoCount"]
            objects.add(new_bot)
    return(steps, world, objects)
