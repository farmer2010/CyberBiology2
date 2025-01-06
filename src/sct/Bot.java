package sct;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Random;
import java.util.ListIterator;

public class Bot{
	ArrayList<Bot> objects;
	Random rand = new Random();
	private int size = 2;
	private int x;
	private int y;
	public int xpos;
	public int ypos;
	public Color color;
	public int energy;
	public int organics;
	public int minerals;
	public int killed = 0;
	public Bot[][] map;
	public int[] commands = new int[64];
	private int index = 0;
	public int age = 1000;
	public int state = 0;//бот или органика
	public int state2 = 1;//что ставить в массив с миром
	private int rotate = rand.nextInt(8);
	public int memory = 0;
	public int type;
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
		9,
		8,
		7,
		6,
		5,
		4,
		3
	};
	private int[] world_scale = {500, 500};
	private int c_red = 0;
	private int c_green = 0;
	private int c_blue = 0;
	private int c_yellow = 0;
	private int sector_len = world_scale[1] / 8;
	private int pht_org_block = 0;//1 - фотосинтез, 2 - переработка органики
	public Bot(int new_xpos, int new_ypos, Color new_color, int new_energy, Bot[][] new_map, ArrayList<Bot> new_objects) {
		xpos = new_xpos;
		ypos = new_ypos;
		x = new_xpos * size;
		y = new_ypos * size;
		color = new_color;
		energy = new_energy;
		organics = 0;
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
			//canvas.setColor(new Color(0, 0, 0));
			//canvas.fillRect(x, y, 10, 10);
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
					r = Math.max((int)(c_red * 1.0 / all * 255.0), y);
					g = Math.max((int)(c_green * 1.0 / all * 255.0), y);
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
				canvas.setColor(new Color((int)(age / 1000.0 * 255.0), (int)(age / 1000.0 * 255.0), 255 - (int)(age / 1000.0 * 255.0)));
			}
			//canvas.fillRect(x + 1, y + 1, 8, 8);
			canvas.fillRect(x, y, size, size);
		}else {//рисуем органику
			canvas.setColor(new Color(0, 0, 0));
			canvas.fillRect(x + 1, y + 1, 8, 8);
			canvas.setColor(new Color(128, 128, 128));
			canvas.fillRect(x + 2, y + 2, 6, 6);
		}
	}
	public int Update(ListIterator<Bot> iterator, double[][] oxygen_map, double[][] org_map, double[][] mnr_map) {
		if (killed == 0) {
			if (state == 0) {//бот
				int sector = bot_in_sector();
				age--;
				energy--;
				int count = 1;
				for (int i = 0; i < 8; i++) {
					int[] pos = get_rotate_position(i);
					if (pos[1] > 0 & pos[1] < world_scale[1]) {
						if (map[pos[0]][pos[1]] != null && map[pos[0]][pos[1]].state == 0) {
							count++;
						}
					}
				}
				if (oxygen_map[xpos][ypos] >= 0.002 * count) {
					oxygen_map[xpos][ypos] -= 0.002 * count;
					update_commands(iterator, oxygen_map, org_map, mnr_map);
					if (energy >= 800) {//автоматическое деление
						multiply(rotate, org_map, iterator);
					}
				}
				age -= (int)(oxygen_map[xpos][ypos] * 10);
				//if (sector <= 7 & sector >= 5) {
				//	minerals += minerals_list[sector - 5];
				//}
				if (energy <= 0) {
					die_with_organics(org_map);
					return(0);
				}else if (energy > 1000) {
					energy = 1000;
				}
				if (age <= 0 || org_map[xpos][ypos] >= 800) {
					die_with_organics(org_map);
					return(0);
				}
				if (minerals > 1000) {
					minerals = 1000;
				}
			}else {//стоящая органика
				//
			}
		}
		return(0);
	}
	public void update_commands(ListIterator<Bot> iterator, double[][] oxygen_map, double[][] org_map, double[][] mnr_map) {//мозг
		for (int i = 0; i < 5; i++) {
			int command = commands[index];
			if (command == 0) {//повернуться
				rotate += commands[(index + 1) % 64] % 8;
				rotate %= 8;
				index += 2;
				index %= 64;
			}else if (command == 1) {//сменить направление
				rotate = commands[(index + 1) % 64] % 8;
				index += 2;
				index %= 64;
			}else if (command == 2 || command == 3 || command == 4) {//фотосинтез
				int sector = bot_in_sector();
				if (sector <= 7 && pht_org_block != 2) {
					pht_org_block = 1;
					energy += photo_list[sector] * (org_map[xpos][ypos] / 300);
					c_green += 1;
					oxygen_map[xpos][ypos] += 0.003 * photo_list[sector] * (org_map[xpos][ypos] / 300);
					//co2_map[xpos][ypos] -= 0.001;
					if (oxygen_map[xpos][ypos] > 1) {
						oxygen_map[xpos][ypos] = 1;
					}
				}
				index += 1;
				index %= 64;
				break;
			}else if (command == 5) {//походить относительно
				int count = 1;
				for (i = 0; i < 8; i++) {
					int[] pos = get_rotate_position(i);
					if (pos[1] > 0 & pos[1] < world_scale[1]) {
						if (map[pos[0]][pos[1]] != null && map[pos[0]][pos[1]].state == 0) {
							count++;
						}
					}
				}
				if (oxygen_map[xpos][ypos] >= 0.003 * count) {
					oxygen_map[xpos][ypos] -= 0.003 * count;
					int sens = move(commands[(index + 1) % 64] % 8);
					if (sens == 1) {
						energy -= 1;
					}
				}
				index += 2;
				index %= 64;
				break;
			}else if(command == 6) {//походить абсолютно
				int count = 1;
				for (i = 0; i < 8; i++) {
					int[] pos = get_rotate_position(i);
					if (pos[1] > 0 & pos[1] < world_scale[1]) {
						if (map[pos[0]][pos[1]] != null && map[pos[0]][pos[1]].state == 0) {
							count++;
						}
					}
				}
				if (oxygen_map[xpos][ypos] >= 0.003 * count) {
					oxygen_map[xpos][ypos] -= 0.003 * count;
					move(rotate);
					energy -= 1;
				}
				index += 1;
				index %= 64;
				break;
			}else if (command == 7) {//атаковать относительно
				int count = 1;
				for (i = 0; i < 8; i++) {
					int[] pos = get_rotate_position(i);
					if (pos[1] > 0 & pos[1] < world_scale[1]) {
						if (map[pos[0]][pos[1]] != null && map[pos[0]][pos[1]].state == 0) {
							count++;
						}
					}
				}
				if (oxygen_map[xpos][ypos] >= 0.001 * count) {
					oxygen_map[xpos][ypos] -= 0.001 * count;
					attack(commands[(index + 1) % 64] % 8);
				}
				index += 2;
				index %= 64;
				break;
			}else if (command == 8) {//атаковать абсолютно
				int count = 1;
				for (i = 0; i < 8; i++) {
					int[] pos = get_rotate_position(i);
					if (pos[1] > 0 & pos[1] < world_scale[1]) {
						if (map[pos[0]][pos[1]] != null && map[pos[0]][pos[1]].state == 0) {
							count++;
						}
					}
				}
				if (oxygen_map[xpos][ypos] >= 0.001 * count) {
					oxygen_map[xpos][ypos] -= 0.001 * count;
					attack(rotate);
				}
				index += 1;
				index %= 64;
				break;
			}else if (command == 9) {//посмотреть относительно
				int[] pos = get_rotate_position(commands[(index + 1) % 64] % 8);
				if (pos[1] > 0 & pos[1] < world_scale[1]) {
					if (map[pos[0]][pos[1]] == null) {
						index = commands[(index + 3) % 64];//если ничего
					}else if (map[pos[0]][pos[1]].state == 0) {
						Bot b = map[pos[0]][pos[1]];
						index = commands[(index + 4) % 64];//если бот
					}
				}else {
					index = commands[(index + 2) % 64];//если граница
				}
			}else if (command == 10) {//посмотреть абсолютно
				int[] pos = get_rotate_position(rotate);
				if (pos[1] > 0 & pos[1] < world_scale[1]) {
					if (map[pos[0]][pos[1]] == null) {
						index = commands[(index + 2) % 64];//если ничего
					}else if (map[pos[0]][pos[1]].state == 0) {
						Bot b = map[pos[0]][pos[1]];
						index = commands[(index + 3) % 64];//если бот
					}
				}else {
					index = commands[(index + 1) % 64];//если граница
				}
			}else if (command == 11 | command == 12) {//отдать ресурсы относительно
				give(commands[(index + 1) % 64] % 8);
				index += 2;
				index %= 64;
				break;
			}else if (command == 13 | command == 14) {//отдать ресурсы абсолютно
				give(rotate);
				index += 1;
				index %= 64;
				break;
			}else if (command == 15) {//сколько у меня энергии
				int ind = commands[(index + 1) % 64] * 15;
				if (energy >= ind) {
					index = commands[(index + 2) % 64];
				}else {
					index = commands[(index + 3) % 64];
				}
			}else if (command == 16) {//сколько у меня минералов
				int ind = commands[(index + 1) % 64] * 15;
				if (minerals >= ind) {
					index = commands[(index + 2) % 64];
				}else {
					index = commands[(index + 3) % 64];
				}
			}else if (command == 17) {//преобразовать минералы в энергию
				if (minerals > 0) {
					c_blue++;
				}
				energy += minerals * 4;
				minerals = 0;
				index += 1;
				index %= 64;
				break;
			}else if (command == 18) {//есть ли фотосинтез
				int sector = bot_in_sector();
				if (sector <= 5) {
					index = commands[(index + 1) % 64];
				}else {
					index = commands[(index + 2) % 64];
				}
			}else if (command == 19) {//есть ли приход минералов
				int sector = bot_in_sector();
				if (sector <= 7 & sector >= 5) {
					index = commands[(index + 1) % 64];
				}else {
					index = commands[(index + 2) % 64];
				}
			}else if (command == 20) {//поделиться относительно
				multiply(commands[(index + 1) % 64] % 8, org_map, iterator);
				index += 2;
				index %= 64;
				break;
			}else if (command == 21) {//поделиться абсолютно
				multiply(rotate, org_map, iterator);
				index += 1;
				index %= 64;
				break;
			}else if (command == 22) {//какая моя позиция x
				double ind = commands[(index + 1) % 64] / 64.0;
				if (xpos * world_scale[0] >= ind) {
					index = commands[(index + 2) % 64];
				}else {
					index = commands[(index + 3) % 64];
				}
			}else if (command == 23) {//какая моя позиция y
				double ind = commands[(index + 1) % 64] / 64.0;
				if (ypos * world_scale[1] >= ind) {
					index = commands[(index + 2) % 64];
				}else {
					index = commands[(index + 3) % 64];
				}
			}else if (command == 24) {//какой мой возраст
				int ind = commands[(index + 1) % 64] * 15;
				if (age >= ind) {
					index = commands[(index + 2) % 64];
				}else {
					index = commands[(index + 3) % 64];
				}
			}else if (command == 25) {//равномерное распределение ресурсов относительно
				give2(commands[(index + 1) % 64] % 8);
				index += 2;
				index %= 64;
				break;
			}else if (command == 26) {//равномерное распределение ресурсов абсолютно
				give2(rotate);
				index += 1;
				index %= 64;
				break;
			}else if (command == 27) {//безусловный переход
				index = commands[(index + 1) % 64];
			}else if (command == 28) {//сколько кислорода
				int ind = commands[(index + 1) % 64] / 63;
				if (oxygen_map[xpos][ypos] >= ind) {
					index = commands[(index + 2) % 64];
				}else {
					index = commands[(index + 3) % 64];
				}
			}else if (command == 29 || command == 30) {//переработка органики под собой
				if (pht_org_block != 1) {
					pht_org_block = 2;
					recycle_organics2(org_map, oxygen_map, commands[(index + 1) % 64] * 2);
				}
				index += 2;
				index %= 64;
				break;
			}else if (command == 31 || command == 32) {//переработка органики перед собой относительно
				if (pht_org_block != 1) {
					pht_org_block = 2;
					recycle_organics(commands[(index + 1) % 64] % 8, org_map, oxygen_map, commands[(index + 2) % 64] * 2);
				}
				index += 3;
				index %= 64;
				break;
			}else if (command == 33 || command == 34) {//переработка органики перед собой абсолютно
				if (pht_org_block != 1) {
					pht_org_block = 2;
					recycle_organics(rotate, org_map, oxygen_map, commands[(index + 2) % 64] * 2);
				}
				index += 2;
				index %= 64;
				break;
			}else if (command == 35) {//сколько органики подо мной
				int ind = commands[(index + 1) % 64] / 63;
				if (org_map[xpos][ypos] / 1000 >= ind) {
					index = commands[(index + 2) % 64];
				}else {
					index = commands[(index + 3) % 64];
				}
			}else if (command == 36) {//сколько органики передо мной относительно
				int[] pos = get_rotate_position(commands[(index + 1) % 64] % 8);
				if (pos[1] > 0 & pos[1] < world_scale[1]) {
					int ind = commands[(index + 2) % 64] / 63;
					if (org_map[pos[0]][pos[1]] / 1000 >= ind) {
						index = commands[(index + 3) % 64];
					}else {
						index = commands[(index + 4) % 64];
					}
				}else {
					index = commands[(index + 5) % 64];
				}
			}else if (command == 37) {//сколько органики передо мной абсолютно
				int[] pos = get_rotate_position(rotate);
				if (pos[1] > 0 & pos[1] < world_scale[1]) {
					int ind = commands[(index + 1) % 64] / 63;
					if (org_map[pos[0]][pos[1]] / 1000 >= ind) {
						index = commands[(index + 2) % 64];
					}else {
						index = commands[(index + 3) % 64];
					}
				}else {
					index = commands[(index + 4) % 64];
				}
			}else if (command == 38) {//какое мое направление
				int ind = commands[(index + 1) % 64] % 8;
				if (rotate >= ind) {
					index = commands[(index + 2) % 64];
				}else if (rotate == ind) {
					index = commands[(index + 3) % 64];
				}else {
					index = commands[(index + 4) % 64];
				}
			
			}else {
				index += command;
				index %= 64;
			}
		}
	}
	public void recycle_organics2(double[][] org_map, double[][] oxygen_map, int org) {
		int[] pos = {xpos, ypos};
		if (pos[1] > 0 & pos[1] < world_scale[1]) {
			double ox = oxygen_map[xpos][ypos];
			if (org_map[pos[0]][pos[1]] > org) {
				if (ox >= org * 0.00005) {
					oxygen_map[xpos][ypos] -= org * 0.00005;
					energy += org;
					org_map[pos[0]][pos[1]] -= org;
					c_yellow++;
				}
			}else {
				if (ox >= org_map[pos[0]][pos[1]] * 0.00005) {
					oxygen_map[xpos][ypos] -= org_map[pos[0]][pos[1]] * 0.00005;
					energy += org_map[pos[0]][pos[1]];
					if (org_map[pos[0]][pos[1]] != 0) {
						c_yellow++;
					}
					org_map[pos[0]][pos[1]] = 0.0;
				}
			}
		}
	}
	public void recycle_organics(int rot, double[][] org_map, double[][] oxygen_map, int org) {
		int[] pos = get_rotate_position(rot);
		if (pos[1] > 0 & pos[1] < world_scale[1]) {
			double ox = oxygen_map[xpos][ypos];
			if (org_map[pos[0]][pos[1]] > org) {
				if (ox >= org * 0.00005) {
					oxygen_map[xpos][ypos] -= org * 0.00005;
					energy += org;
					org_map[pos[0]][pos[1]] -= org;
					c_yellow++;
				}
			}else {
				if (ox >= org_map[pos[0]][pos[1]] * 0.00005) {
					oxygen_map[xpos][ypos] -= org_map[pos[0]][pos[1]] * 0.00005;
					energy += org_map[pos[0]][pos[1]];
					if (org_map[pos[0]][pos[1]] > 0) {
						c_yellow++;
					}
					org_map[pos[0]][pos[1]] = 0.0;
				}
			}
		}
	}
	public void die_with_organics(double[][] org_map) {//умереть с появлением органики
		killed = 1;
		map[xpos][ypos] = null;
		double enr = (energy + 150) / 9;
		for (int i = 0; i < 8; i++) {
			int[] pos = get_rotate_position(i);
			if (pos[1] > 0 & pos[1] < world_scale[1]) {
				org_map[pos[0]][pos[1]] += enr;
				if (org_map[pos[0]][pos[1]] > 1000) {
					org_map[pos[0]][pos[1]] = 1000;
				}
			}
		}
		org_map[xpos][ypos] += enr;
		if (org_map[xpos][ypos] > 1000) {
			org_map[xpos][ypos] = 1000;
		}
	}
	public void give(int rot) {
		int[] pos = get_rotate_position(rot);
		if (pos[1] > 0 & pos[1] < world_scale[1]) {
			if (map[pos[0]][pos[1]] != null && map[pos[0]][pos[1]].state == 0) {
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
	public void give2(int rot) {
		int[] pos = get_rotate_position(rot);
		if (pos[1] > 0 & pos[1] < world_scale[1]) {
			if (map[pos[0]][pos[1]] != null && map[pos[0]][pos[1]].state == 0) {
				Bot relative = map[pos[0]][pos[1]];
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
			if (map[pos[0]][pos[1]] != null) {
				Bot victim = map[pos[0]][pos[1]];
				if (victim != null) {
					energy += victim.energy;
					map[pos[0]][pos[1]] = null;
					victim.killed = 1;
					c_red++;
				}
			}
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
		pos[0] = (xpos + movelist[rot][0]) % world_scale[0];
		pos[1] = ypos + movelist[rot][1];
		if (pos[0] < 0) {
			pos[0] = world_scale[0] - 1;
		}else if(pos[0] >= world_scale[0]) {
			pos[0] = 0;
		}
		return(pos);
	}
	public int move(int rot) {
		int[] pos = get_rotate_position(rot);
		if (pos[1] > 0 & pos[1] < world_scale[1]) {
			if (map[pos[0]][pos[1]] == null) {
				Bot self = map[xpos][ypos];
				map[xpos][ypos] = null;
				xpos = pos[0];
				ypos = pos[1];
				x = xpos * size;
				y = ypos * size;
				map[xpos][ypos] = self;
				return(1);
			}
		}
		return(0);
	}
	public void multiply(int rot, double[][] org_map, ListIterator<Bot> iterator) {
		int[] pos = get_rotate_position(rot);
		if (pos[1] > 0 & pos[1] < world_scale[1]) {
			if (map[pos[0]][pos[1]] == null) {
				energy -= 150;
				if (energy <= 0) {
					die_with_organics(org_map);
				}else {
					Color new_color;
					if (rand.nextInt(800) == 0) {//немного меняем(с шансом 1/100 - полностью)
						new_color = new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
					}else {
						new_color = new Color(border(color.getRed() + rand.nextInt(-12, 13), 255, 0), border(color.getGreen() + rand.nextInt(-12, 13), 255, 0), border(color.getBlue() + rand.nextInt(-12, 13), 255, 0));
					}
					int[] new_brain = new int[64];
					for (int i = 0; i < 64; i++) {
						new_brain[i] = commands[i];
					}
					if (rand.nextInt(4) == 0) {//мутация
						new_brain[rand.nextInt(64)] = rand.nextInt(64);
					}
					Bot new_bot = new Bot(pos[0], pos[1], new_color, energy / 2, map, objects);
					new_bot.minerals = minerals / 2;
					energy /= 2;
					minerals /= 2;
					new_bot.commands = new_brain;
					iterator.add(new_bot);
					map[pos[0]][pos[1]] = new_bot;
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
	public int border(int number, int border1, int border2) {
		if (number > border1) {
			number = border1;
		}else if (number < border2) {
			number = border2;
		}
		return(number);
	}
}
