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
	private Color clan_color = new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
	public int energy;
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
	private int c_red = 0;
	private int c_green = 0;
	private int c_blue = 0;
	private int c_yellow = 0;
	private int pht_org_block = 0;//1 - фотосинтез, 2 - переработка органики
	public Bot(int new_xpos, int new_ypos, Color new_color, int new_energy, Bot[][] new_map, ArrayList<Bot> new_objects) {
		xpos = new_xpos;
		ypos = new_ypos;
		x = new_xpos * Constant.size;
		y = new_ypos * Constant.size;
		color = new_color;
		energy = new_energy;
		objects = new_objects;
		map = new_map;
		for (int i = 0; i < 64; i++) {
			commands[i] = rand.nextInt(64);
		}
	}
	public void Draw(Graphics canvas, int draw_type) {
		if (state == 0) {//рисуем бота
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
			}else if (draw_type == 3) {//кланов
				canvas.setColor(clan_color);
			}else if (draw_type == 4) {//возраста
				canvas.setColor(new Color((int)(age / 1000.0 * 255.0), (int)(age / 1000.0 * 255.0), 255 - (int)(age / 1000.0 * 255.0)));
			}
			//canvas.fillRect(x + 1, y + 1, 8, 8);
			canvas.fillRect(x, y, Constant.size, Constant.size);
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
				age--;
				energy -= Constant.energy_for_life;
				int count = bot_count() + 1;
				if (oxygen_map[xpos][ypos] >= Constant.life_ox_coeff * count) {
					oxygen_map[xpos][ypos] -= Constant.life_ox_coeff * count;
					update_commands(iterator, oxygen_map, org_map, mnr_map);
					if (energy >= 800) {//автоматическое деление
						multiply(rotate, org_map, iterator);
					}
				}
				age -= (int)(oxygen_map[xpos][ypos] * Constant.age_minus_coeff);
				if (energy <= 0) {
					die_with_organics(org_map);
					return(0);
				}else if (energy > 1000) {
					energy = 1000;
				}
				if (age <= 0 || org_map[xpos][ypos] >= Constant.org_die_level) {
					die_with_organics(org_map);
					return(0);
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
					energy += Constant.pht_energy_list[sector] * (org_map[xpos][ypos] / Constant.pht_coeff);
					c_green++;
					oxygen_map[xpos][ypos] += Constant.pht_ox_coeff * Constant.pht_energy_list[sector] * (org_map[xpos][ypos] / Constant.pht_coeff);
					if (oxygen_map[xpos][ypos] > 1) {
						oxygen_map[xpos][ypos] = 1;
					}
				}
				index += 1;
				index %= 64;
				break;
			}else if (command == 5) {//походить относительно
				int count = bot_count() + 1;
				if (oxygen_map[xpos][ypos] >= Constant.move_ox_coeff * count) {
					oxygen_map[xpos][ypos] -= Constant.move_ox_coeff * count;
					int sens = move(commands[(index + 1) % 64] % 8);
					if (sens == 1) {
						energy -= Constant.energy_for_move;
					}
				}
				index += 2;
				index %= 64;
				break;
			}else if(command == 6) {//походить абсолютно
				int count = bot_count() + 1;
				if (oxygen_map[xpos][ypos] >= Constant.move_ox_coeff * count) {
					oxygen_map[xpos][ypos] -= Constant.move_ox_coeff * count;
					move(rotate);
					energy -= Constant.energy_for_move;
				}
				index += 1;
				index %= 64;
				break;
			}else if (command == 7) {//атаковать относительно
				int count = bot_count() + 1;
				if (oxygen_map[xpos][ypos] >= Constant.attack_ox_coeff * count) {
					oxygen_map[xpos][ypos] -= Constant.attack_ox_coeff * count;
					attack(commands[(index + 1) % 64] % 8, org_map);
				}
				index += 2;
				index %= 64;
				break;
			}else if (command == 8) {//атаковать абсолютно
				int count = bot_count() + 1;
				if (oxygen_map[xpos][ypos] >= Constant.attack_ox_coeff * count) {
					oxygen_map[xpos][ypos] -= Constant.attack_ox_coeff * count;
					attack(rotate, org_map);
				}
				index += 1;
				index %= 64;
				break;
			}else if (command == 9) {//посмотреть относительно
				int rot = commands[(index + 1) % 64] % 8;
				index = commands[(index + 2 + see(rot, org_map)) % 64];
			}else if (command == 10) {//посмотреть абсолютно
				index = commands[(index + 1 + see(rotate, org_map)) % 64];
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
			}else if (command == 16) {//есть ли фотосинтез
				int sector = bot_in_sector();
				if (sector <= 5) {
					index = commands[(index + 1) % 64];
				}else {
					index = commands[(index + 2) % 64];
				}
			}else if (command == 17) {//есть ли приход минералов
				int sector = bot_in_sector();
				if (sector <= 7 & sector >= 5) {
					index = commands[(index + 1) % 64];
				}else {
					index = commands[(index + 2) % 64];
				}
			}else if (command == 18) {//поделиться относительно
				multiply(commands[(index + 1) % 64] % 8, org_map, iterator);
				index += 2;
				index %= 64;
				break;
			}else if (command == 19) {//поделиться абсолютно
				multiply(rotate, org_map, iterator);
				index += 1;
				index %= 64;
				break;
			}else if (command == 20) {//какая моя позиция x
				double ind = commands[(index + 1) % 64] / 64.0;
				if (xpos * 1.0 / Constant.world_scale[0] >= ind) {
					index = commands[(index + 2) % 64];
				}else {
					index = commands[(index + 3) % 64];
				}
			}else if (command == 21) {//какая моя позиция y
				double ind = commands[(index + 1) % 64] / 64.0;
				if (ypos * 1.0 / Constant.world_scale[1] >= ind) {
					index = commands[(index + 2) % 64];
				}else {
					index = commands[(index + 3) % 64];
				}
			}else if (command == 22) {//какой мой возраст
				int ind = commands[(index + 1) % 64] * 15;
				if (age >= ind) {
					index = commands[(index + 2) % 64];
				}else {
					index = commands[(index + 3) % 64];
				}
			}else if (command == 23) {//равномерное распределение ресурсов относительно
				give2(commands[(index + 1) % 64] % 8);
				index += 2;
				index %= 64;
				break;
			}else if (command == 24) {//равномерное распределение ресурсов абсолютно
				give2(rotate);
				index += 1;
				index %= 64;
				break;
			}else if (command == 25) {//безусловный переход
				index = commands[(index + 1) % 64];
			}else if (command == 26) {//сколько кислорода
				int ind = commands[(index + 1) % 64] / 63;
				if (oxygen_map[xpos][ypos] >= ind) {
					index = commands[(index + 2) % 64];
				}else {
					index = commands[(index + 3) % 64];
				}
			}else if (command == 27 || command == 28) {//переработка органики под собой
				if (pht_org_block != 1) {
					pht_org_block = 2;
					recycle_organics2(org_map, oxygen_map, commands[(index + 1) % 64] * 2);
				}
				index += 2;
				index %= 64;
				break;
			}else if (command == 29 || command == 30) {//переработка органики перед собой относительно
				if (pht_org_block != 1) {
					pht_org_block = 2;
					recycle_organics(commands[(index + 1) % 64] % 8, org_map, oxygen_map, commands[(index + 2) % 64] * 2);
				}
				index += 3;
				index %= 64;
				break;
			}else if (command == 31 || command == 32) {//переработка органики перед собой абсолютно
				if (pht_org_block != 1) {
					pht_org_block = 2;
					recycle_organics(rotate, org_map, oxygen_map, commands[(index + 2) % 64] * 2);
				}
				index += 2;
				index %= 64;
				break;
			}else if (command == 33) {//сколько органики подо мной
				int ind = commands[(index + 1) % 64];
				if (org_map[xpos][ypos] >= ind * 15) {
					index = commands[(index + 2) % 64];
				}else {
					index = commands[(index + 3) % 64];
				}
			}else if (command == 34) {//сколько органики передо мной относительно
				int rot = commands[(index + 1) % 64] % 8;
				int ind = commands[(index + 2) % 64];
				index = commands[(index + 3 + see_org(rot, ind, org_map)) % 64];
			}else if (command == 35) {//сколько органики передо мной абсолютно
				int ind = commands[(index + 1) % 64];
				index = commands[(index + 2 + see_org(rotate, ind, org_map)) % 64];
			}else if (command == 36) {//какое мое направление
				int ind = commands[(index + 1) % 64] % 8;
				if (rotate >= ind) {
					index = commands[(index + 2) % 64];
				}else if (rotate == ind) {
					index = commands[(index + 3) % 64];
				}else {
					index = commands[(index + 4) % 64];
				}
			}else if (command == 37) {//установить направление в случайное
				rotate = rand.nextInt(8);
				index += 1;
				index %= 64;
			}else if (command == 38) {//сколько ботов вокруг
				int ind = commands[(index + 1) % 64] % 8;
				int count = bot_count();
				if (count >= ind) {
					index = commands[(index + 2) % 64];
				}else if (count == ind) {
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
	public int see_org(int rot, int ind, double[][] org_map) {
		int[] pos = get_rotate_position(rotate);
		if (pos[1] >= 0 & pos[1] < Constant.world_scale[1]) {
			if (org_map[pos[0]][pos[1]] >= ind * 15) {
				return(0);
			}else {
				return(1);
			}
		}else {
			return(2);
		}
	}
	public int see(int rot, double[][] org_map) {
		int[] pos = get_rotate_position(rot);
		if (pos[1] >= 0 & pos[1] < Constant.world_scale[1]) {
			if (org_map[pos[0]][pos[1]] >= 500) {
				return(3);//если переизбыток органики
			}else {
				if (map[pos[0]][pos[1]] == null) {
					return(1);//если ничего
				}else if (map[pos[0]][pos[1]].state == 0) {
					return(2);//если бот
				}
			}
		}else {
			return(0);//если граница
		}
		return(0);
	}
	public int bot_count() {
		int count = 0;
		for (int i = 0; i < 8; i++) {
			int[] pos = get_rotate_position(i);
			if (pos[1] >= 0 & pos[1] < Constant.world_scale[1]) {
				if (map[pos[0]][pos[1]] != null && map[pos[0]][pos[1]].state == 0) {
					count++;
				}
			}
		}
		return(count);
	}
	public void recycle_organics2(double[][] org_map, double[][] oxygen_map, int org) {
		int[] pos = {xpos, ypos};
		if (pos[1] >= 0 & pos[1] < Constant.world_scale[1]) {
			double ox = oxygen_map[xpos][ypos];
			if (org_map[pos[0]][pos[1]] > org) {
				if (ox >= org * Constant.org_recycle_ox_coeff) {
					oxygen_map[xpos][ypos] -= org * Constant.org_recycle_ox_coeff;
					energy += org;
					org_map[pos[0]][pos[1]] -= org;
					c_yellow++;
				}
			}else {
				if (ox >= org_map[pos[0]][pos[1]] * Constant.org_recycle_ox_coeff) {
					oxygen_map[xpos][ypos] -= org_map[pos[0]][pos[1]] * Constant.org_recycle_ox_coeff;
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
		if (pos[1] >= 0 & pos[1] < Constant.world_scale[1]) {
			double ox = oxygen_map[xpos][ypos];
			if (org_map[pos[0]][pos[1]] > org) {
				if (ox >= org * Constant.org_recycle_ox_coeff) {
					oxygen_map[xpos][ypos] -= org * Constant.org_recycle_ox_coeff;
					energy += org;
					org_map[pos[0]][pos[1]] -= org;
					c_yellow++;
				}
			}else {
				if (ox >= org_map[pos[0]][pos[1]] * Constant.org_recycle_ox_coeff) {
					oxygen_map[xpos][ypos] -= org_map[pos[0]][pos[1]] * Constant.org_recycle_ox_coeff;
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
		double enr = (150) / 9;
		for (int i = 0; i < 8; i++) {
			int[] pos = get_rotate_position(i);
			if (pos[1] >= 0 & pos[1] < Constant.world_scale[1]) {
				org_map[pos[0]][pos[1]] += enr;
			}
		}
		org_map[xpos][ypos] += enr;
	}
	public void give(int rot) {
		int[] pos = get_rotate_position(rot);
		if (pos[1] >= 0 & pos[1] < Constant.world_scale[1]) {
			if (map[pos[0]][pos[1]] != null && map[pos[0]][pos[1]].state == 0) {
				Bot relative = map[pos[0]][pos[1]];
				if (relative.killed == 0) {
					relative.energy += energy / 4;
					energy -= energy / 4;
				}
			}
		}
	}
	public void give2(int rot) {
		int[] pos = get_rotate_position(rot);
		if (pos[1] >= 0 & pos[1] < Constant.world_scale[1]) {
			if (map[pos[0]][pos[1]] != null && map[pos[0]][pos[1]].state == 0) {
				Bot relative = map[pos[0]][pos[1]];
				if (relative.killed == 0) {
					int enr = relative.energy + energy;
					relative.energy = enr / 2;
					energy = enr / 2;
				}
			}
		}
	}
	public void attack(int rot, double[][] org_map) {
		int[] pos = get_rotate_position(rot);
		if (pos[1] >= 0 & pos[1] < Constant.world_scale[1]) {
			if (map[pos[0]][pos[1]] != null) {
				Bot victim = map[pos[0]][pos[1]];
				if (victim != null) {
					energy += victim.energy;
					victim.die_with_organics(org_map);
					c_red++;
				}
			}
		}
	}
	public int[] get_rotate_position(int rot){
		int[] pos = new int[2];
		pos[0] = (xpos + Constant.movelist[rot][0]) % Constant.world_scale[0];
		pos[1] = ypos + Constant.movelist[rot][1];
		if (pos[0] < 0) {
			pos[0] = Constant.world_scale[0] - 1;
		}else if(pos[0] >= Constant.world_scale[0]) {
			pos[0] = 0;
		}
		return(pos);
	}
	public int move(int rot) {
		int[] pos = get_rotate_position(rot);
		if (pos[1] >= 0 & pos[1] < Constant.world_scale[1]) {
			if (map[pos[0]][pos[1]] == null) {
				Bot self = map[xpos][ypos];
				map[xpos][ypos] = null;
				xpos = pos[0];
				ypos = pos[1];
				x = xpos * Constant.size;
				y = ypos * Constant.size;
				map[xpos][ypos] = self;
				return(1);
			}
		}
		return(0);
	}
	public void multiply(int rot, double[][] org_map, ListIterator<Bot> iterator) {
		int[] pos = get_rotate_position(rot);
		if (pos[1] >= 0 & pos[1] < Constant.world_scale[1]) {
			if (map[pos[0]][pos[1]] == null) {
				energy -= 150;
				if (energy <= 0) {
					die_with_organics(org_map);
				}else {
					Color new_color;
					if (rand.nextInt(800) == 0) {//немного меняем(с шансом 1/800 - полностью)
						new_color = new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
					}else {
						new_color = new Color(Constant.border(color.getRed() + rand.nextInt(-12, 13), 255, 0), Constant.border(color.getGreen() + rand.nextInt(-12, 13), 255, 0), Constant.border(color.getBlue() + rand.nextInt(-12, 13), 255, 0));
					}
					int[] new_brain = new int[64];
					for (int i = 0; i < 64; i++) {
						new_brain[i] = commands[i];
					}
					if (rand.nextInt(4) == 0) {//мутация
						new_brain[rand.nextInt(64)] = rand.nextInt(64);
					}
					Bot new_bot = new Bot(pos[0], pos[1], new_color, energy / 2, map, objects);
					energy /= 2;
					new_bot.commands = new_brain;
					new_bot.clan_color = clan_color;
					iterator.add(new_bot);
					map[pos[0]][pos[1]] = new_bot;
				}
			}
		}
	}
	public int bot_in_sector() {
		int sec = ypos / (Constant.world_scale[1] / 8);
		if (sec > 7) {
			sec = 7;
		}
		return(sec);
	}
}
