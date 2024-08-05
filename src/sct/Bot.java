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
	public int energy;
	public int minerals;
	public int killed = 0;
	public int[][] map;
	public int[] commands = new int[64];
	private int index = 0;
	public int age = 1000;
	public int state = 0;//бот или органика
	public int state2 = 1;//что ставить в массив с миром
	private int rotate = rand.nextInt(8);
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
	private int[] minerals_list = {
		1,
		2,
		3
	};
	private int[] photo_list = {
		10,
		8,
		6,
		5,
		4,
		3
	};
	private int[] world_scale = {162, 108};
	private int c_red = 0;
	private int c_green = 0;
	private int c_blue = 0;
	private int c_yellow = 0;
	private int sector_len = world_scale[1] / 8;
	private int defense = rand.nextInt(200);
	public Bot(int new_xpos, int new_ypos, Color new_color, int new_energy, int[][] new_map, ArrayList<Bot> new_objects) {
		xpos = new_xpos;
		ypos = new_ypos;
		x = new_xpos * 10;
		y = new_ypos * 10;
		color = new_color;
		energy = new_energy;
		minerals = 0;
		objects = new_objects;
		map = new_map;
		for (int i = 0; i < 64; i++) {
			commands[i] = rand.nextInt(64);
		}
		//world_scale[0] = map.length;
		//world_scale[1] = map[0].length;
	}
	public void Draw(Graphics canvas, int draw_type) {
		if (state == 0) {//рисуем бота
			canvas.setColor(new Color(0, 0, 0));
			canvas.fillRect(x, y, 10, 10);
			if (draw_type == 0) {//режим отрисовки хищников
				int r = 0;
				int g = 0;
				int b = 0;
				int all = c_red + c_green + c_blue + c_yellow;
				if (all == 0) {
					r = 128;
					g = 128;
					b = 128;
				}else {
					int y = (int)((c_yellow * 1.0) / all * 255.0);
					r = max((int)(c_red * 1.0 / all * 255.0), y);
					g = max((int)(c_green * 1.0 / all * 255.0), y);
					b = (int)((c_blue * 1.0) / all * 255.0);
				}
				canvas.setColor(new Color(r, g, b));
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
				int rg = 255 - (int)(minerals / 1000.0 * 255.0);
				if (rg > 255) {
					rg = 255;
				}else if (rg < 0) {
					rg = 0;
				}
				canvas.setColor(new Color(rg, rg, 255));
			}else if (draw_type == 4) {//возраста
				canvas.setColor(new Color((int)(age / 1000.0 * 255.0), (int)(age / 1000.0 * 255.0), (int)(age / 1000.0 * 255.0)));
			}else if (draw_type == 5) {//защиты
				canvas.setColor(new Color(255 - (int)(defense / 800.0 * 255), (int)(defense / 800.0 * 255), 0));
			}
			canvas.fillRect(x + 1, y + 1, 8, 8);
		}else {//рисуем органику
			canvas.setColor(new Color(0, 0, 0));
			canvas.fillRect(x + 1, y + 1, 8, 8);
			canvas.setColor(new Color(128, 128, 128));
			canvas.fillRect(x + 2, y + 2, 6, 6);
		}
	}
	public int Update(ListIterator<Bot> iterator, int[][] organics_map) {
		if (killed == 0) {
			if (state == 0) {//бот
				int sector = bot_in_sector();
				energy -= 1;
				age -= 1;
				if (sector <= 7 & sector >= 5) {
					minerals += minerals_list[sector - 5];
				}
				update_commands(iterator, organics_map);
				if (energy <= 0) {
					killed = 1;
					map[xpos][ypos] = 0;
					return(0);
				}else if (energy > 1000) {
					energy = 1000;
				}
				if (energy >= 800) {//автоматическое деление
					multiply(rotate, iterator);
				}
				if (age <= 0) {
					die_with_organics(organics_map);
					return(0);
				}
				if (rand.nextInt(1001) < border(organics_map[xpos][ypos] - defense, 1000, 0)) {
					die_with_organics(organics_map);
					return(0);
				}
				if (minerals > 1000) {
					minerals = 1000;
				}
			}else if (state == 1) {//падающая органика
				move(4);
				int[] pos = get_rotate_position(4);
				if (pos[1] > 0 & pos[1] < world_scale[1]) {
					if (map[pos[0]][pos[1]] != 0) {
						state = 2;
					}
				}
			}else {//стоящая органика
				//
			}
		}
		return(0);
	}
	public void update_commands(ListIterator<Bot> iterator, int[][] organics_map) {//мозг
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
				int sector = bot_in_sector();
				if (sector <= 5) {
					energy += photo_list[sector];
					c_green += 1;
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
				int[] pos = get_rotate_position(commands[(index + 1) % 64] % 8);
				if (pos[1] > 0 & pos[1] < world_scale[1]) {
					if (map[pos[0]][pos[1]] == 0) {
						index = commands[(index + 3) % 64];//если ничего
					}else if (map[pos[0]][pos[1]] == 1) {
						Bot b = find(pos);
						if (b != null) {
							if (is_relative(commands, b.commands)) {
								index = commands[(index + 4) % 64];//если родственник
							}else {
								index = commands[(index + 3) % 64];//если враг
							}
						}else {
							index = commands[(index + 2) % 64];//если ничего
						}
					}else if (map[pos[0]][pos[1]] == 2) {
						index = commands[(index + 6) % 64];//если органика
					}
				}else {
					index = commands[(index + 2) % 64];//если граница
				}
			}else if (command == 31) {//посмотреть абсолютно
				int[] pos = get_rotate_position(rotate);
				if (pos[1] > 0 & pos[1] < world_scale[1]) {
					if (map[pos[0]][pos[1]] == 0) {
						index = commands[(index + 2) % 64];//если ничего
					}else if (map[pos[0]][pos[1]] == 1) {
						Bot b = find(pos);
						if (b != null) {
							if (is_relative(commands, b.commands)) {
								index = commands[(index + 4) % 64];//если родственник
							}else {
								index = commands[(index + 3) % 64];//если враг
							}
						}else {
							index = commands[(index + 2) % 64];//если ничего
						}
					}else if (map[pos[0]][pos[1]] == 2) {
						index = commands[(index + 5) % 64];//если органика
					}
				}else {
					index = commands[(index + 1) % 64];//если граница
				}
			}else if (command == 34 | command == 50) {//отдать ресурсы относительно
				give(commands[(index + 1) % 64] % 8);
				index += 2;
				index %= 64;
				break;
			}else if (command == 35 | command == 52) {//отдать ресурсы абсолютно
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
					c_blue++;
				}
				energy += minerals * 4;
				minerals = 0;
				index += 1;
				index %= 64;
				break;
			}else if (command == 39) {//есть ли фотосинтез
				int sector = bot_in_sector();
				if (sector <= 5) {
					index = commands[(index + 1) % 64];
				}else {
					index = commands[(index + 2) % 64];
				}
			}else if (command == 40) {//есть ли приход минералов
				int sector = bot_in_sector();
				if (sector <= 7 & sector >= 5) {
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
				double ind = commands[(index + 1) % 64] / 64.0;
				if (xpos * world_scale[0] >= ind) {
					index = commands[(index + 2) % 64];
				}else {
					index = commands[(index + 3) % 64];
				}
			}else if (command == 44) {//какая моя позиция y
				double ind = commands[(index + 1) % 64] / 64.0;
				if (ypos * world_scale[1] >= ind) {
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
			}else if (command == 49) {//переработка органики абсолютно
				recycle_organics(rotate, organics_map);
				index += 1;
				index %= 64;
				break;
			}else if (command == 51) {//переработка органики относительно
				recycle_organics(commands[(index + 1) % 64] % 8, organics_map);
				index += 2;
				index %= 64;
				break;
			}else if (command == 53) {//переработка органики под собой
				recycle_organics2(organics_map);
				index += 1;
				index %= 64;
				break;
			}else if (command == 54) {//сколько органики передо мной
				int[] pos = get_rotate_position(rotate);
				if (pos[1] > 0 & pos[1] < world_scale[1]) {
					if (organics_map[pos[0]][pos[1]] >= commands[(index + 1) % 64] * 15) {
						index = commands[(index + 2) % 64];
					}else {
						index = commands[(index + 3) % 64];
					}
				}
			}else {
				index += command;
				index %= 64;
			}
		}
	}
	public void recycle_organics(int rot, int[][] organics_map) {
		int[] pos = get_rotate_position(rot);
		if (pos[1] > 0 & pos[1] < world_scale[1]) {
			if (organics_map[pos[0]][pos[1]] > 150) {
				energy += 150;
				organics_map[pos[0]][pos[1]] -= 150;
				c_yellow++;
			}else if (organics_map[pos[0]][pos[1]] > 0){
				energy += organics_map[pos[0]][pos[1]];
				organics_map[pos[0]][pos[1]] = 0;
				c_yellow++;
			}
		}
	}
	public void recycle_organics2(int[][] organics_map) {
		int[] pos = {xpos, ypos};
		if (pos[1] > 0 & pos[1] < world_scale[1]) {
			if (organics_map[pos[0]][pos[1]] > 150) {
				energy += 150;
				organics_map[pos[0]][pos[1]] -= 150;
				c_yellow++;
			}else if (organics_map[pos[0]][pos[1]] > 0){
				energy += organics_map[pos[0]][pos[1]];
				organics_map[pos[0]][pos[1]] = 0;
				c_yellow++;
			}
		}
	}
	public void give(int rot) {
		int[] pos = get_rotate_position(rot);
		if (pos[1] > 0 & pos[1] < world_scale[1]) {
			if (map[pos[0]][pos[1]] == 1) {
				Bot relative = find(pos);
				if (relative.killed == 0) {
					relative.energy += energy / 4;
					relative.minerals += minerals / 4;
					energy -= energy / 4;
					minerals -= minerals / 4;
				}
			}
		}
	}
	public void give2(int rot) {
		int[] pos = get_rotate_position(rot);
		if (pos[1] > 0 & pos[1] < world_scale[1]) {
			if (map[pos[0]][pos[1]] == 1) {
				Bot relative = find(pos);
				if (relative.killed == 0) {
					int enr = relative.energy + energy;
					int mnr = relative.minerals + minerals;
					relative.energy = enr / 2;
					relative.minerals = mnr / 2;
					energy = enr / 2;
					minerals = mnr / 2;
				}
			}
		}
	}
	public void attack(int rot) {
		int[] pos = get_rotate_position(rot);
		if (pos[1] > 0 & pos[1] < world_scale[1]) {
			if (map[pos[0]][pos[1]] != 0) {
				Bot victim = find(pos);
				if (victim != null) {
					victim.killed = 1;
					energy += victim.energy;
					map[pos[0]][pos[1]] = 0;
					c_red++;
				}
			}
		}
	}
	public Bot find(int[] pos) {//только если есть сосед
		for (Bot b: objects) {
			if (b.killed == 0 & b.xpos == pos[0] & b.ypos == pos[1]) {
				return(b);
			}
		}
		return(null);
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
		pos[0] = (xpos + movelist[rot][0]) % world_scale[0];
		pos[1] = ypos + movelist[rot][1];
		if (pos[0] < 0) {
			pos[0] = 161;
		}else if(pos[0] >= world_scale[0]) {
			pos[0] = 0;
		}
		return(pos);
	}
	public int move(int rot) {
		int[] pos = get_rotate_position(rot);
		if (pos[1] > 0 & pos[1] < world_scale[1]) {
			if (map[pos[0]][pos[1]] == 0) {
				map[xpos][ypos] = 0;
				xpos = pos[0];
				ypos = pos[1];
				x = xpos * 10;
				y = ypos * 10;
				map[xpos][ypos] = state2;
				return(1);
			}
		}
		return(0);
	}
	public void multiply(int rot, ListIterator<Bot> iterator) {
		int[] pos = get_rotate_position(rot);
		if (pos[1] > 0 & pos[1] < world_scale[1]) {
			if (map[pos[0]][pos[1]] == 0) {
				energy -= 150;
				if (energy <= 0) {
					killed = 1;
					map[xpos][ypos] = 0;
				}else {
					map[pos[0]][pos[1]] = 1; 
					Color new_color = color;
					int new_defense = defense;
					int[] new_brain = new int[64];
					for (int i = 0; i < 64; i++) {
						new_brain[i] = commands[i];
					}
					if (rand.nextInt(4) == 0) {//мутация
						new_color = new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
						new_brain[rand.nextInt(64)] = rand.nextInt(64);
						new_defense = border(new_defense + rand.nextInt(21) - 10, 800, 0);
					}
					Bot new_bot = new Bot(pos[0], pos[1], new_color, energy / 2, map, objects);
					new_bot.minerals = minerals / 2;
					energy /= 2;
					minerals /= 2;
					new_bot.commands = new_brain;
					new_bot.defense = new_defense;
					iterator.add(new_bot);
				}
			}
		}
	}
	public int bot_in_sector() {
		int sec = ypos / sector_len;
		if (sec > 7) {
			sec = 10;
		}
		return(sec);
	}
	public void die_with_organics(int[][] organics_map) {
		killed = 1;
		map[xpos][ypos] = 0;
		int enr = energy / 9;
		for (int i = 0; i < 8; i++) {
			int[] pos = get_rotate_position(i);
			if (pos[1] > 0 & pos[1] < world_scale[1]) {
				organics_map[pos[0]][pos[1]] += enr;
				if (organics_map[pos[0]][pos[1]] > 1000) {
					organics_map[pos[0]][pos[1]] = 1000;
				}
			}else {
				organics_map[xpos][ypos] += enr;
			}
		}
		organics_map[xpos][ypos] += enr;
		if (organics_map[xpos][ypos] > 1000) {
			organics_map[xpos][ypos] = 1000;
		}
	}
	public int border(int number, int border1, int border2) {
		if (number > border1) {
			number = border1;
		}else if (number < border2) {
			number = border2;
		}
		return(number);
	}
	public int max(int number1, int number2) {//максимальное из двух чисел
		if (number1 > number2) {
			return(number1);
		}else if (number2 > number1) {
			return(number2);
		}else {
			return(number1);
		}
	}
}
