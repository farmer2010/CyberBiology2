import copy
import organics
from random import randint as rand
import image_factory
import pygame
from game_object import GameObject
pygame.init()

def border(number, border):
    if number > border:
        number = border
    elif number < 0:
        number = 0
    return number

class Bot(GameObject):
    def __init__(self, pos, color, world, objects, bots, energy=1000, draw_type=0, brain=[], sftype=0):
        GameObject.__init__(self, pos, image_factory.get_image(color))
        self.name = "bot"#имя
        if brain == []:
            a = [rand(0, 63) for x in range(64)]
            brain = [a for i in range(8)]
        self.killed = 0#мертв бот или нет
        self.color = color#цвет
        self.rotate = rand(0, 7)#направление
        self.energy = energy#энергия
        self.age = 1000#возраст (чем больше, тем моложе)
        self.world = world#ссылка на массив с миром
        self.objects = objects#ссылка на массив с ботами
        self.index = 0#счетчик команд
        self.type = sftype#тип
        self.brain = brain#мозг
        self.commands = self.brain[self.type]#команды
        self.minerals = 0#количество минералов
        self.attack_count = 0#красный в режиме отбражения типа питания
        self.photo_count = 0#зеленый в режиме отбражения типа питания
        self.minerals_count = 0#синий в режиме отбражения типа питания
        self.bots = bots#количество ботов(для отображения на экране)
        self.diff = [[25, 50, 50, 10] for x in range(8)]#0 - приход минералов, 1 - переработка минералов, 2 - фотосинтез, 3 - атака
        self.photo_list = [#массивы с приходом фотосинтеза и минералов в зависимости от уровня
            10,
            8,
            6,
            5,
            4,
            3
            ]
        self.minerals_list = [
            4,
            8,
            12
            ]
        self.last_draw_type = [0]
        self.change_image(draw_type)

    def next_command(self, i=1):#увеличить счетчик на...
        self.index += i
        self.index %= 64

    def bot_in_sector(self):#для фотосинтеза и минералов
        sector_len = int(self.world_scale[1] / 8)
        error = self.world_scale[1] - sector_len * 8
        sec = int(self.pos[1] / sector_len)
        if sec > 7:
            return(10)#море(будет, если высота мира нацело не делится на 8)
        return(sec)

    def change_image(self, draw_type):#сменить цвет
        if draw_type == 0:#режим отображения цвета ботов
            self.image = image_factory.get_image(self.color)
        elif draw_type == 1:#режим отображения энергии
            g = 255 - int((self.energy / 1000) * 255)
            if g < 0:
                g = 0
            try:
                self.image = image_factory.get_image((255, g, 0))
            except:
                print(g)
        elif draw_type == 2:#режим отображения минералов
            rg = 255 - int((self.minerals / 1000) * 255)
            if rg < 0:
                rg = 0
            self.image = image_factory.get_image(
                (
                    rg,
                    rg,
                    255
                    )
                )
        elif draw_type == 3:#режим отображения возраста
            self.image = image_factory.get_image(
                (
                    int((self.age / 1000) * 255),
                    int((self.age / 1000) * 255),
                    int((self.age / 1000) * 255)
                    )
                )
        elif draw_type == 4:#режим отображения типа питания(хищников)
            count = sum((self.photo_count, self.attack_count, self.minerals_count))
            if count == 0:
                R = 128
                G = 128
                B = 128
            else:
                R = int(self.attack_count / count * 255)
                G = int(self.photo_count / count * 255)
                B = int(self.minerals_count / count * 255)
            self.image = image_factory.get_image((R, G, B))
        elif draw_type == 5:#режим отображения типа
            if self.type == 0:
               self.image = image_factory.get_image((0, 0, 255))
            elif self.type == 1:
               self.image = image_factory.get_image((0, 128, 255))
            elif self.type == 2:
               self.image = image_factory.get_image((0, 255, 0))
            elif self.type == 3:
               self.image = image_factory.get_image((255, 0, 0))
            elif self.type == 4:
               self.image = image_factory.get_image((255, 0, 255))
            elif self.type == 5:
               self.image = image_factory.get_image((255, 255, 0))
            elif self.type == 6:
               self.image = image_factory.get_image((0, 255, 255))
            elif self.type == 7:
               self.image = image_factory.get_image((128, 128, 128))

    def multiply(self, draw_type, rotate, btype=0):#поделиться
        pos2 = self.get_rotate_position(rotate)#позиция, на которую смотрит бот
        if pos2[1] >= 0 and pos2[1] <= self.world_scale[1] - 1:#если бот не смотрит в стену
            if self.world[pos2[0]][pos2[1]] == "none":#если перед ботом ничего нет
                self.energy -= 150#деление требует 150 ед. энергии
                if self.energy <= 0:#если энергии не хватает, то умереть
                    self.killed = 1
                    self.world[self.pos[0]][self.pos[1]] = "none"
                    self.kill()
                else:#если энергии хватает
                    new_color = self.color
                    if rand(0, 99) == 0:#полное изменение цвета(1/100)
                        new_color = (
                            rand(0, 255),
                            rand(0, 255),
                            rand(0, 255)
                        )
                    new_brain = copy.deepcopy(self.brain)
                    new_diff = copy.deepcopy(self.diff)
                    if rand(0, 3) == 0:#шанс мутации 1/4(коэффициент 1/256)
                        t = rand(0, 3)
                        for i in range(8):
                            new_brain[i][rand(0, 63)] = rand(0, 63)
                            for g in range(4):
                                if g == t:
                                    new_diff[i][g] = border(new_diff[i][g] + rand(1, 5), 100)
                                else:
                                    new_diff[i][g] = border(new_diff[i][g] - rand(1, 5), 100)
                    new_bot = Bot(#создать нового бота
                        pos2,#позиция
                        new_color,#цвет
                        self.world,#ссылка на массив с миром
                        self.objects,#ссылка на массив с ботами
                        self.bots,#ссылка на переменную для счетчика ботов
                        energy=int(self.energy / 2),#энергия
                        draw_type=draw_type,#тип отрисовки
                        sftype=btype,#тип нового бота
                        brain=new_brain#мозг
                    )
                    #минералы и энергия распределяются равномерно между потомком и предком
                    new_bot.diff = new_diff
                    new_bot.minerals = int(self.minerals / 2)
                    self.minerals = int(self.minerals / 2)
                    self.energy = int(self.energy / 2)
                    self.objects.add(new_bot)#добавить в массив с ботами потомка
                    self.world[pos2[0]][pos2[1]] = "bot"#записать в массив с миром, в какой клетке стоит бот

    def get_rotate_position(self, rotate):#вычисление координат на которые смотрит бот
        pos = [
            (self.pos[0] + GameObject.movelist[rotate][0]) % self.world_scale[0],
            self.pos[1] + GameObject.movelist[rotate][1]
            ]
        return(pos)

    def attack(self, pos):#атаковать
        if pos[1] >= 0 and pos[1] <= self.world_scale[1] - 1:#границы
            if self.world[pos[0]][pos[1]] == "bot" or self.world[pos[0]][pos[1]] == "organics":#если есть цель
                victim = None
                for victim in self.objects:#поиск жертвы
                    if victim.pos == pos:
                        break
                    else:
                        victim = None
                if victim != None:#если есть жертва
                    self.energy += int(victim.energy * (self.diff[self.type][3] / 100))#отнять у жертвы энергию
                    victim.killed = 1#убить жертву
                    victim.kill()
                    self.attack_count += 1#бот краснеет
            self.world[pos[0]][pos[1]] = "none"

    def give(self, pos):#отдать соседу часть энергии
        friend = None
        for friend in self.objects:#поиск соседа
            if friend.pos == pos and friend.name == "bot":
                break
            else:
                friend = None
        if friend != None:#если есть сосед, отдать ему 1/4 своих ресурсов
            friend.energy += int(self.energy / 4)
            friend.minerals += int(self.minerals / 4)
            self.energy -= int(self.energy / 4)
            self.minerals -= int(self.minerals / 4)

    def give2(self, pos):#равномерное распределение ресурсов
        #поиск друга
        friend = None
        for friend in self.objects:
            if friend.pos == pos and friend.name == "bot":
                break
            else:
                friend = None
        if friend != None:
            #равномерное распределение минералов и энергии
            enr = friend.energy + self.energy
            mnr = friend.minerals + self.minerals
            friend.energy = int(enr / 2)
            friend.minerals = int(mnr / 2)
            self.energy = int(enr / 2)
            self.minerals = int(mnr / 2)

    def update_commands(self, draw_type):
        pos2 = self.get_rotate_position(self.rotate)
        for x in range(5):
            #[command] абсолютно - выполняет команду в направлении бота
            #[command] относительно - выполняет команду в направлении, указываемом остатком от деления номера следующей команды на 8
            command = self.commands[self.index]
            #--------------------------------------------------------
            if command == 23:#повернуться
                self.rotate += self.commands[(self.index + 1) % 64] % 8
                self.rotate %= 8
                self.next_command(2)
            elif command == 24:#сменить направление
                self.rotate = self.commands[(self.index + 1) % 64] % 8
                self.next_command(2)
            #--------------------------------------------------------
            elif command == 25:#фотосинтез
                sector = self.bot_in_sector()
                if sector <= 5:
                    self.energy += int(self.photo_list[sector] * (self.diff[self.type][2] / 100))
                    self.photo_count += 1
                self.next_command()
                break
            #--------------------------------------------------------
            elif command == 26:#походить относительно
                stc = self.rotate
                self.rotate = self.commands[(self.index + 1) % 64] % 8
                sensor = self.sensor(self.world, self.rotate)
                if sensor == 2:
                    self.energy -= 1
                self.move(self.world)
                self.rotate = stc
                self.next_command(2)
                break
            elif command == 27:#походить абсолютно
                sensor = self.sensor(self.world, self.rotate)
                if sensor == 2:
                    self.energy -= 1
                self.move(self.world)
                self.next_command()
                break
            #--------------------------------------------------------
            elif command == 28:#атаковать относительно
                rotate = self.commands[(self.index + 1) % 64] % 8
                pos = self.get_rotate_position(rotate)
                self.attack(pos)
                self.next_command(2)
                break
            elif command == 29:#атаковать абсолютно
                self.attack(pos2)
                self.next_command()
                break
            #--------------------------------------------------------
            elif command == 30:#посмотреть относительно
                rotate = self.commands[(self.index + 1) % 64] % 8
                sensor = self.sensor(self.world, rotate) + 1
                self.index = self.commands[(self.index + sensor) % 64]
            elif command == 31:#посмотреть абсолютно
                sensor = self.sensor(self.world, self.rotate)
                self.index = self.commands[(self.index + sensor) % 64]
            #--------------------------------------------------------
            elif command == 34 or command == 50:#отдать ресурсы относительно
                rotate = self.commands[(self.index + 1) % 64] % 8
                pos = self.get_rotate_position(rotate)
                self.give(pos)
                self.next_command(2)
                break
            elif command == 35 or command == 52:#отдать ресурсы абсолютно
                pos = self.get_rotate_position(self.rotate)
                self.give(pos)
                self.next_command()
                break
            #--------------------------------------------------------
            elif command == 36:#сколько у меня энергии
                cmd = self.commands[(self.index + 1) % 64]
                if self.energy >= cmd * 15:
                    self.index = self.commands[(self.index + 2) % 64]
                else:
                    self.index = self.commands[(self.index + 3) % 64]
            elif command == 37:#сколько у меня минералов
                cmd = self.commands[(self.index + 1) % 64]
                if self.minerals >= cmd * 15:
                    self.index = self.commands[(self.index + 2) % 64]
                else:
                    self.index = self.commands[(self.index + 3) % 64]
            #--------------------------------------------------------
            elif command == 38:#преобразовать минералы в энергию
                if self.minerals != 0:
                    self.minerals_count += 1
                self.energy += int(self.minerals * (self.diff[self.type][1] / 100))
                self.minerals = 0
                self.next_command()
                break
            #--------------------------------------------------------
            elif command == 39:#есть ли приход энергии
                sector = self.bot_in_sector()
                if sector >= 0 and sector <= 5:
                    self.index = self.commands[(self.index + 1) % 64]
                else:
                    self.index = self.commands[(self.index + 2) % 64]
            elif command == 40:#есть ли приход минералов
                sector = self.bot_in_sector()
                if sector >= 5 and sector <= 7:
                    self.index = self.commands[(self.index + 1) % 64]
                else:
                    self.index = self.commands[(self.index + 2) % 64]
            #--------------------------------------------------------
            elif command == 41:#поделиться относительно
                rotate = self.commands[(self.index + 1) % 64] % 8
                self.multiply(draw_type, rotate, self.commands[(self.index + 2) % 64] % 8)
                self.next_command(3)
                break
            elif command == 42:#поделиться абсолютно
                self.multiply(draw_type, self.rotate, self.commands[(self.index + 1) % 64] % 8)
                self.next_command(2)
                break
            #--------------------------------------------------------
            elif command == 43:#какая моя позиция(x)
                cmd = self.commands[(self.index + 1) % 64] / 64
                if self.pos[0] / self.world_scale[0] >= cmd:
                    self.index = self.commands[(self.index + 2) % 64]
                else:
                    self.index = self.commands[(self.index + 3) % 64]
            elif command == 44:#какая моя позиция(y)
                cmd = self.commands[(self.index + 1) % 64] / 64
                if self.pos[1] / self.world_scale[1] >= cmd:
                    self.index = self.commands[(self.index + 2) % 64]
                else:
                    self.index = self.commands[(self.index + 3) % 64]
            #--------------------------------------------------------
            elif command == 45:#какой мой возраст
                cmd = self.commands[(self.index + 1) % 64] * 15
                if self.age >= cmd:
                    self.index = self.commands[(self.index + 2) % 64]
                else:
                    self.index = self.commands[(self.index + 3) % 64]
            #--------------------------------------------------------
            elif command == 46:#равномерное распределение ресурсов относительно
                rotate = self.commands[(self.index + 1) % 64] % 8
                pos = self.get_rotate_position(rotate)
                self.give2(pos)
                self.next_command(2)
                break
            elif command == 47:#равномерное распределение ресурсов абсолютно
                pos = self.get_rotate_position(self.rotate)
                self.give(pos)
                self.next_command(2)
                break
            #--------------------------------------------------------
            else:#безусловный переход
                self.next_command(command)

    def update(self, draw_type):
        self.bots[0] += 1#увеличить счетчик ботов на один
        if not self.killed:#если бот не мертв:
            self.world[self.pos[0]][self.pos[1]] = "bot"
            self.age -= 1#постареть
            self.energy -= 1#уменьшить количество энергии
            sector = self.bot_in_sector()#для минералов
            if sector <= 7 and sector >= 5:#приход минералов
                self.minerals += int(self.minerals_list[sector - 5] * (self.diff[self.type][0] / 100))
            if draw_type != self.last_draw_type:#сменить режим отрисовки
                self.last_draw_type[0] = draw_type
                self.change_image(draw_type)
            self.update_commands(draw_type)#обновить мозг
            if self.energy >= 800:#если энергии > 800
                self.multiply(draw_type, self.rotate)
            if self.energy > 1000:#ограничитель количества энергии
                self.energy = 1000
            if self.minerals > 1000:#ограничитель количества минералов
                self.minerals = 1000
            if self.age <= 0:
                self.world[self.pos[0]][self.pos[1]] = "organics"#умереть от старости(органика появляется)
                self.objects.add(organics.Organics(self.pos, self.world, self.objects, self.energy))
                #self.world[self.pos[0]][self.pos[1]] = "none"
                self.killed = 1
                self.kill()
            if self.energy <= 0:#умереть от недостатка энергии(органика не появляется)
                self.world[self.pos[0]][self.pos[1]] = "none"
                self.killed = 1
                self.kill()
