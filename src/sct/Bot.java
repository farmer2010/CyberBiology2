package sct;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Random;
import java.util.ListIterator;

public class Bot{
	ArrayList<Bot> objects;
	Random rand = new Random();
	private int x;
	private int y;
	public int xpos;
	public int ypos;
	public Color color;
	public double energy;
	public int minerals;
	public int mul = 100;
	public int killed = 0;
	public Bot[][] map;
	public int[][] mul_map;
	private World world;
	public int[] commands = new int[64];
	public int index = 0;
	public int age = (int)(Constant.max_age);
	public int state = 0;//бот или органика
	public int rotate = rand.nextInt(8);
	private int[][] movelist = {
		{0, -1},
		{1, -1},
		{1, 0},
		{1, 1},
		{0, 1},
		{-1, 1},
		{-1, 0},
		{-1, -1}
	};
	public int c_red = -1;
	public int c_green = -1;
	public int c_blue = -1;
	public int[][] pred_colors = new int[][] {//цвет в режиме отрисовки хищников
		{-1, -1, -1},
		{-1, -1, -1},
		{-1, -1, -1},
		{-1, -1, -1},
		{-1, -1, -1},
		{-1, -1, -1},
		{-1, -1, -1},
		{-1, -1, -1},
		{-1, -1, -1},
		{-1, -1, -1}
	};
	//
	public int mutations = 0;
	public int generation = 0;
	public Bot(int new_xpos, int new_ypos, Color new_color, double new_energy, World new_world) {
		xpos = new_xpos;
		ypos = new_ypos;
		x = new_xpos * Constant.bot_scale;
		y = new_ypos * Constant.bot_scale;
		color = new_color;
		energy = new_energy;
		minerals = 0;
		world = new_world;
		objects = world.objects;
		map = world.Map;
		mul_map = world.mul_map;
		for (int i = 0; i < 64; i++) {
			commands[i] = rand.nextInt(64);
		}
	}
	public void Draw(Graphics canvas, int draw_type) {
		if (state == 0) {//рисуем бота
			if (Constant.draw_frame) {
				canvas.setColor(new Color(0, 0, 0));
				canvas.fillRect(x, y, Constant.bot_scale, Constant.bot_scale);
			}
			if (draw_type == 0) {//режим отрисовки хищников
				if (c_red == -1 || c_green == -1 || c_blue == -1) {
					canvas.setColor(new Color(128, 128, 128));
				}else {
					canvas.setColor(new Color(c_red, c_green, c_blue));
				}
			}else if (draw_type == 1) {//цвета
				canvas.setColor(color);
			}else if (draw_type == 2) {//энергии
				int g = 255 - (int)(energy / 1000.0 * 255.0);
				if (g > 255) {
					g = 255;
				}else if (g < 0) {
					g = 0;
				}
				canvas.setColor(new Color(255, g, 0));
			}else if (draw_type == 3) {//минералов
				canvas.setColor(Constant.gradient(new Color(0, 255, 0), new Color(0, 255, 255), minerals / 1000.0));
			}else if (draw_type == 4) {//возраста
				canvas.setColor(Constant.gradient(new Color(0, 0, 255), new Color(255, 255, 0), age / (Constant.max_age * 1.0)));
			}else if (draw_type == 5) {//вещества
				canvas.setColor(Constant.gradient(new Color(255, 40, 40), new Color(40, 184, 255), mul / 150.0));
			}
			if (Constant.draw_frame) {
				canvas.fillRect(x + 1, y + 1, Constant.bot_scale - 2, Constant.bot_scale - 2);
			}else {
				canvas.fillRect(x, y, Constant.bot_scale, Constant.bot_scale);
			}
			if (Constant.draw_rotate) {
				canvas.setColor(new Color(0, 0, 0));
				int b = Constant.bot_scale / 2;
				canvas.drawLine(x + b, y + b, x + b + movelist[rotate][0] * (b - 1), y + b + movelist[rotate][1] * (b - 1));
			}
		}else {//рисуем органику
			if (Constant.draw_frame) {
				canvas.setColor(new Color(0, 0, 0));
				canvas.fillRect(x + 1, y + 1, Constant.bot_scale - 2, Constant.bot_scale - 2);
				canvas.setColor(new Color(100, 100, 100));
				canvas.fillRect(x + 2, y + 2, Constant.bot_scale - 4, Constant.bot_scale - 4);
			}else {
				if (Constant.bot_scale <= 2) {
					canvas.setColor(new Color(100, 100, 100));
					canvas.fillRect(x, y, 1, 1);
				}else {
					canvas.setColor(new Color(100, 100, 100));
					canvas.fillRect(x + 1, y + 1, Constant.bot_scale - 2, Constant.bot_scale - 2);
				}
			}
		}
	}
	public int Update(ListIterator<Bot> iterator) {
		if (killed == 0) {
			if (state == 0) {//ботbot_in_sector();
				energy -= Constant.energy_for_life;
				age--;
				minerals += Constant.minerals_list[sector(Constant.minerals_list.length)];
				update_commands(iterator);
				if (energy <= 0) {
					die();
					return(0);
				}else if (energy > Constant.max_energy) {
					energy = Constant.max_energy;
				}
				if (energy >= Constant.energy_for_auto_multiply) {//автоматическое деление
					multiply(rotate, iterator);
				}
				if (age <= 0) {
					if (Constant.allow_organics) {
						drop();
						state = 1;
					}else {
						die();
					}
					return(0);
				}
				if (minerals > 1000) {
					minerals = 1000;
				}
			}else if (state == 1) {//падающая органика
				if (Constant.org_fall_type != 0) {
					move(4);
				}
				if (Constant.org_fall_type == 1) {
					int[] pos = get_rotate_position(4);
					if (pos[1] > 0 && pos[1] < Constant.H) {
						if (map[pos[0]][pos[1]] != null) {
							state = 2;
						}
					}
				}
				if (Constant.org_fall_type == 3) {
					int[] pos = get_rotate_position(4);
					if (pos[1] > 0 && pos[1] < Constant.H && map[pos[0]][pos[1]] != null) {
						int[] pos_left = get_rotate_position(5);//клетка слева снизу
						int[] pos_right = get_rotate_position(3);//клетка справа снизу
						if (map[pos_left[0]][pos_left[1]] == null && map[pos_right[0]][pos_right[1]] != null) {//сыпаться влево
							move(5);
						}else if (map[pos_left[0]][pos_left[1]] != null && map[pos_right[0]][pos_right[1]] == null) {//сыпаться вправо
							move(3);
						}else if (map[pos_left[0]][pos_left[1]] == null && map[pos_right[0]][pos_right[1]] == null) {//сыпаться в случайную сторону
							move(3 + rand.nextInt(2) * 2);
						}
					}
				}
			}
			if (world.steps % 10 == 0) {
				world.mul_count += mul;
			}
		}
		return(0);
	}
	public void update_commands(ListIterator<Bot> iterator) {//мозг
		for (int i = 0; i < 5; i++) {
			int command = commands[index];
			if (command == 23) {//повернуться
				rotate += commands[(index + 1) % 64] % 8;
				rotate %= 8;
				index += 2;
				index %= 64;
			}else if (command == 24) {//сменить направление
				rotate = commands[(index + 1) % 64] % 8;
				index += 2;
				index %= 64;
			}else if (command == 25) {//фотосинтез
				if (Constant.photo_list[sector(Constant.photo_list.length)] > 0) {
					energy += Constant.photo_list[sector(Constant.photo_list.length)];
					go_color(new Color(0, 160, 0));
				}
				index += 1;
				index %= 64;
				break;
			}else if (command == 26) {//походить относительно
				int sens = move(commands[(index + 1) % 64] % 8);
				if (sens == 1) {
					energy -= 1;
				}
				index += 2;
				index %= 64;
				break;
			}else if(command == 27) {//походить абсолютно
				int sens = move(rotate);
				if (sens == 1) {
					energy -= 1;
				}
				index += 1;
				index %= 64;
				break;
			}else if (command == 28) {//атаковать относительно
				attack(commands[(index + 1) % 64] % 8);
				index += 2;
				index %= 64;
				break;
			}else if (command == 29) {//атаковать абсолютно
				attack(rotate);
				index += 1;
				index %= 64;
				break;
			}else if (command == 30) {//посмотреть относительно
				int rot = commands[(index + 1) % 64] % 8;
				index = commands[(index + 2 + see(rot)) % 64];
			}else if (command == 31) {//посмотреть абсолютно
				index = commands[(index + 1 + see(rotate)) % 64];
			}else if (command == 34 || command == 50) {//отдать ресурсы относительно
				give(commands[(index + 1) % 64] % 8);
				index += 2;
				index %= 64;
				break;
			}else if (command == 35 || command == 52) {//отдать ресурсы абсолютно
				give(rotate);
				index += 1;
				index %= 64;
				break;
			}else if (command == 36) {//сколько у меня энергии
				int ind = commands[(index + 1) % 64] * 15;
				if (energy >= ind) {
					index = commands[(index + 2) % 64];
				}else {
					index = commands[(index + 3) % 64];
				}
			}else if (command == 37) {//сколько у меня минералов
				int ind = commands[(index + 1) % 64] * 15;
				if (minerals >= ind) {
					index = commands[(index + 2) % 64];
				}else {
					index = commands[(index + 3) % 64];
				}
			}else if (command == 38) {//преобразовать минералы в энергию
				if (minerals > 0) {
					go_color(new Color(0, 0, 255));
				}
				energy += minerals * 4;
				minerals = 0;
				index += 1;
				index %= 64;
				break;
			}else if (command == 39) {//есть ли фотосинтез
				if (Constant.photo_list[sector(Constant.photo_list.length)] > 0) {
					index = commands[(index + 1) % 64];
				}else {
					index = commands[(index + 2) % 64];
				}
			}else if (command == 40) {//есть ли приход минералов
				if (Constant.minerals_list[sector(Constant.minerals_list.length)] > 0) {
					index = commands[(index + 1) % 64];
				}else {
					index = commands[(index + 2) % 64];
				}
			}else if (command == 41) {//поделиться относительно
				multiply(commands[(index + 1) % 64] % 8, iterator);
				index += 2;
				index %= 64;
				break;
			}else if (command == 42) {//поделиться абсолютно
				multiply(rotate, iterator);
				index += 1;
				index %= 64;
				break;
			}else if (command == 43) {//какая моя позиция x
				double ind = commands[(index + 1) % 64] / 63.0;
				if (xpos * 1.0 / Constant.W >= ind) {
					index = commands[(index + 2) % 64];
				}else {
					index = commands[(index + 3) % 64];
				}
			}else if (command == 44) {//какая моя позиция y
				double ind = commands[(index + 1) % 64] / 63.0;
				if (ypos * 1.0 / Constant.H >= ind) {
					index = commands[(index + 2) % 64];
				}else {
					index = commands[(index + 3) % 64];
				}
			}else if (command == 45) {//какой мой возраст
				int ind = commands[(index + 1) % 64] * 15;
				if (age >= ind) {
					index = commands[(index + 2) % 64];
				}else {
					index = commands[(index + 3) % 64];
				}
			}else if (command == 46) {//равномерное распределение ресурсов относительно
				give2(commands[(index + 1) % 64] % 8);
				index += 2;
				index %= 64;
				break;
			}else if (command == 47) {//равномерное распределение ресурсов абсолютно
				give2(rotate);
				index += 1;
				index %= 64;
				break;
			}else if (command == 48) {//безусловный переход
				index = commands[(index + 1) % 64];
			}else if (command == 49) {//собирать вещество относительно
				collect(commands[(index + 1) % 64] % 8);
				index += 2;
				index %= 64;
				break;
			}else if (command == 51) {//собирать вещество абсолютно
				collect(rotate);
				index += 1;
				index %= 64;
				break;
			}else if (command == 53) {//сколько вещества относительно
				int res = count_mul(commands[(index + 1) % 64] % 8, commands[(index + 2) % 64]);
				index = commands[(index + 3 + res) % 64];
			}else if (command == 54) {//сколько вещества абсолютно
				int res = count_mul(rotate, commands[(index + 1) % 64]);
				index = commands[(index + 2 + res) % 64];
			}else if (command == 55) {//сколько вещества у меня
				int ind = commands[(index + 1) % 64] * 15;
				if (mul >= ind) {
					index = commands[(index + 2) % 64];
				}else {
					index = commands[(index + 3) % 64];
				}
			}else {
				index += commands[index];
				index %= 64;
			}
		}
	}
	public int count_mul(int rot, int ind) {
		int[] pos = get_rotate_position(rot);
		if (pos[1] >= 0 && pos[1] < Constant.H) {
			if (mul_map[pos[0]][pos[1]] >= ind) {
				return(0);
			}else {
				return(1);
			}
		}else {
			return(2);
		}
	}
	public void collect(int rot){
		int[] pos = get_rotate_position(rot);
		if (pos[1] >= 0 && pos[1] < Constant.H) {
			mul += mul_map[pos[0]][pos[1]];
			mul_map[pos[0]][pos[1]] = 0;
		}
	}
	public int see(int rot) {
		int[] pos = get_rotate_position(rot);
		if (pos[1] >= 0 && pos[1] < Constant.H) {
			if (map[pos[0]][pos[1]] == null) {
				return(1);//если ничего
			}else if (map[pos[0]][pos[1]].state == 0) {
				if (is_relative(commands, map[pos[0]][pos[1]].commands)) {
					return(3);//если родственник
				}else {
					return(2);//если враг
				}
			}else {
				return(4);
			}
		}else {
			return(0);//если граница
		}
	}
	public void give(int rot) {
		int[] pos = get_rotate_position(rot);
		if (pos[1] >= 0 && pos[1] < Constant.H) {
			if (map[pos[0]][pos[1]] != null) {
				if (map[pos[0]][pos[1]].state == 0) {
					Bot relative = map[pos[0]][pos[1]];
					if (relative.killed == 0) {
						relative.energy += energy / 4;
						relative.minerals += minerals / 4;
						energy -= energy / 4;
						minerals -= minerals / 4;
					}
				}
			}
		}
	}
	public void give2(int rot) {
		int[] pos = get_rotate_position(rot);
		if (pos[1] >= 0 && pos[1] < Constant.H) {
			if (map[pos[0]][pos[1]] != null) {
				if (map[pos[0]][pos[1]].state == 0) {
					Bot relative = map[pos[0]][pos[1]];
					if (relative.killed == 0) {
						double enr = relative.energy + energy;
						int mnr = relative.minerals + minerals;
						relative.energy = enr / 2;
						relative.minerals = mnr / 2;
						energy = enr / 2;
						minerals = mnr / 2;
					}
				}
			}
		}
	}
	public void attack(int rot) {
		int[] pos = get_rotate_position(rot);
		if (pos[1] >= 0 && pos[1] < Constant.H) {
			if (map[pos[0]][pos[1]] != null) {
				Bot victim = map[pos[0]][pos[1]];
				if (victim != null) {
					energy += victim.energy;
					mul += victim.mul;
					victim.mul = 0;
					victim.die();
					go_color(new Color(255, 0, 0));
				}
			}
		}
	}
	public void attack2(int rot, int strength) {
		int[] pos = get_rotate_position(rot);
		if (pos[1] >= 0 && pos[1] < Constant.H) {
			if (map[pos[0]][pos[1]] != null) {
				Bot victim = map[pos[0]][pos[1]];
				if (victim != null) {
					mul = victim.mul;
					victim.mul = 0;
					if (victim.energy >= strength) {
						energy += strength;
						victim.energy -= strength;
					}else {
						energy += victim.energy;
						victim.die();
					}
					go_color(new Color(255, 0, 0));
				}
			}
		}
	}
	public int move(int rot) {
		int[] pos = get_rotate_position(rot);
		if (pos[1] >= 0 && pos[1] < Constant.H) {
			if (map[pos[0]][pos[1]] == null) {
				Bot self = map[xpos][ypos];
				map[xpos][ypos] = null;
				xpos = pos[0];
				ypos = pos[1];
				x = xpos * Constant.bot_scale;
				y = ypos * Constant.bot_scale;
				map[xpos][ypos] = self;
				return(1);
			}
		}
		return(0);
	}
	public void multiply(int rot, ListIterator<Bot> iterator) {
		int[] pos = get_rotate_position(rot);
		if (pos[1] >= 0 && pos[1] < Constant.H) {
			if (map[pos[0]][pos[1]] == null && mul >= 100) {
				energy -= Constant.energy_for_multiply;
				if (energy <= 0) {
					die();
				}else { 
					Color new_color = color;
					int new_genr = generation + 1;
					int new_mut = mutations;
					int[] new_brain = new int[64];
					for (int i = 0; i < 64; i++) {
						new_brain[i] = commands[i];
					}
					if (rand.nextInt(100) < Constant.child_mutation_chance) {//мутация
						new_color = new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
						new_brain[rand.nextInt(64)] = rand.nextInt(64);
						new_mut++;
					}
					if (rand.nextInt(100) < Constant.parent_mutation_chance) {//мутация родителя
						color = new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
						commands[rand.nextInt(64)] = rand.nextInt(64);
					}
					if (Constant.upd_parent_index) index = 0;
					if (Constant.upd_parent_age) age = Constant.max_age;
					Bot new_bot = new Bot(pos[0], pos[1], new_color, energy / 2, world);
					new_bot.minerals = minerals / 2;
					new_bot.mul = mul / 2;
					if (mul / 2 * 2 < mul) {
						mul = mul / 2 + 1;
					}else {
						mul = mul / 2;
					}
					energy /= 2;
					minerals /= 2;
					new_bot.generation = new_genr;
					new_bot.mutations = new_mut;
					new_bot.commands = new_brain;
					map[pos[0]][pos[1]] = new_bot;
					iterator.add(new_bot);
				}
			}
		}
	}
	public void die() {
		killed = 1;
		map[xpos][ypos] = null;
		energy = 0;
		drop();
	}
	public void drop() {
		int c = mul / 9;
		for (int j = 0; j < 8; j++) {
			int[] pos = Constant.get_rotate_position(j, new int[] {xpos, ypos});
			if (pos[1] >= 0 && pos[1] < Constant.H && map[pos[0]][pos[1]] == null) {
				mul_map[pos[0]][pos[1]] += c;
			}else {
				mul_map[xpos][ypos] += c;
			}
		}
		mul_map[xpos][ypos] += c;
		mul_map[xpos][ypos] += mul - c * 9;
		mul = 0;
	}
	public void go_color(Color c) {//цвет режима отрисовки хищников
		int red_count = 0;                  //есть буфер на 10 цветов. При выполнении функции цвет "с" добавляется в начало буфера, сдвигая остальные. Самый поздний исчезает
		int green_count = 0;                //Для получения цвета просто усредняем буфер(кроме тех, где стоит -1. Это начальное значение, обозначающее, что цвета там нет)
		int blue_count = 0;                 //бот с цветом(-1, -1, -1), рисуется серым.
		int count = 1;
		for (int i = 8; i >= 0; i--) {
			pred_colors[i + 1][0] = pred_colors[i][0];
			pred_colors[i + 1][1] = pred_colors[i][1];
			pred_colors[i + 1][2] = pred_colors[i][2];
			if (pred_colors[i][0] != -1) {
				red_count += pred_colors[i][0];
				green_count += pred_colors[i][1];
				blue_count += pred_colors[i][2];
				count++;
			}
		}
		pred_colors[0][0] = c.getRed();
		pred_colors[0][1] = c.getGreen();
		pred_colors[0][2] = c.getBlue();
		red_count += c.getRed();
		green_count += c.getGreen();
		blue_count += c.getBlue();
		if (c_red != -1 && c_green != -1 && c_blue != -1) {
			c_red = red_count / count;
			c_green = green_count / count;
			c_blue = blue_count / count;
		}else {
			c_red = c.getRed();
			c_green = c.getGreen();
			c_blue = c.getBlue();
		}
	}
	public boolean is_relative(int[] brain1, int[] brain2) {
		int errors = 0;
		for (int i = 0; i < 64; i++) {
			if (brain1[i] != brain2[i]) {
				errors += 1;
			}
			if (errors > 1) {
				return(false);
			}
		}
		return(errors < 2);
	}
	public int[] get_rotate_position(int rot){
		int[] pos = new int[2];
		pos[0] = (xpos + movelist[rot][0]) % Constant.W;
		pos[1] = ypos + movelist[rot][1];
		if (pos[0] < 0) {
			pos[0] = Constant.W - 1;
		}else if(pos[0] >= Constant.W) {
			pos[0] = 0;
		}
		return(pos);
	}
	public int sector(int num) {
		int sec = ypos / (Constant.H / num);
		if (sec > num - 1) {
			sec = num - 1;
		}
		return(sec);
	}
	public int border(int number, int border1, int border2) {
		if (number > border1) {
			number = border1;
		}else if (number < border2) {
			number = border2;
		}
		return(number);
	}
}
