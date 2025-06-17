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
	public double temp;
	public int killed = 0;
	public Bot[][] map;
	public int[] commands = new int[64];
	public int index = 0;
	public int age = 1000;
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
	private int[] photo_list = {8, 7, 6, 5, 4, 3, 0, 0};
	private int[] world_scale = {540, 360};
	public int c_red = -1;
	public int c_green = -1;
	public int c_blue = -1;
	private double[][][] ch;
	public double[] my_ch = new double[10];
	public double[][] temp_map;
	public int[][] genes = new int[2][3];
	public Bot(int new_xpos, int new_ypos, Color new_color, double new_energy, double new_temp, Bot[][] new_map, double[][][] new_ch, double[][] new_temp_map, ArrayList<Bot> new_objects) {
		ch = new_ch;
		xpos = new_xpos;
		ypos = new_ypos;
		x = new_xpos * 3;
		y = new_ypos * 3;
		color = new_color;
		energy = new_energy;
		temp = new_temp;
		objects = new_objects;
		map = new_map;
		temp_map = new_temp_map;
		for (int i = 0; i < 64; i++) {
			commands[i] = rand.nextInt(64);
		}
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 3; j++) {
				genes[i][j] = rand.nextInt(64);
			}
		}
	}
	public void Draw(Graphics canvas, int draw_type) {
		if (state == 0) {//рисуем бота
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
			}else if (draw_type == 3) {//возраста
				canvas.setColor(Constant.gradient(new Color(0, 0, 255), new Color(255, 255, 0), (age * 1.0) / 1000));
			}else if (draw_type == 4) {//глюкозы
				canvas.setColor(Constant.gradient(new Color(128, 128, 128), new Color(255, 255, 0), Math.min(my_ch[0] / 1000, 1)));
			}else if (draw_type == 5) {//кристалла
				canvas.setColor(Constant.gradient(new Color(255, 255, 0), new Color(60, 255, 128), Math.min(my_ch[1] / 1000, 1)));
			}else if (draw_type == 8) {//водорода
				canvas.setColor(Constant.gradient(new Color(0, 255, 0), new Color(200, 176, 250), Math.min(my_ch[4] / 1000, 1)));
			}else if (draw_type == 14) {//температуры
				
			}
			canvas.fillRect(x, y, 3, 3);
		}else {//рисуем органику
			canvas.setColor(new Color(128, 128, 128));
			canvas.fillRect(x + 1, y + 1, 3, 3);
		}
	}
	public int Update(ListIterator<Bot> iterator) {
		if (killed == 0) {
			if (state == 0) {//бот
				for (int i = 0; i < 10; i++) {
					if (ch[i][xpos][ypos] > my_ch[i]) {
						double c = (ch[i][xpos][ypos] - my_ch[i]) * Constant.collect_speed[i];
						my_ch[i] += c;
						ch[i][xpos][ypos] -= c;
					}
				}
				double t = temp;
				double tm = temp_map[xpos][ypos];
				temp += (tm - t) / 30;
				temp_map[xpos][ypos] += (t - tm) / 30;
				energy -= 1;
				age -= 1;
				update_genes();
				update_commands(iterator);
				if (energy <= 0) {
					killed = 1;
					map[xpos][ypos] = null;
					die();
					return(0);
				}else if (energy > 1000) {
					energy = 1000;
				}
				if (energy >= 800) {//автоматическое деление
					multiply(rotate, iterator);
				}
				if (age <= 0) {
					die();
					killed = 1;
					map[xpos][ypos] = null;
					return(0);
				}
			}else if (state == 1) {//падающая органика
				move(4);
				int[] pos = get_rotate_position(4);
				if (pos[1] >= 0 & pos[1] < world_scale[1]) {
					if (map[pos[0]][pos[1]] != null) {
						state = 2;
					}
				}
			}else {//стоящая органика
				//
			}
		}
		return(0);
	}
	public void update_genes() {//органеллы
		for (int i = 0; i < 2; i++) {
			if (gene_condition(genes[i][1], genes[i][2]) && genes[i][0] < Constant.reactions.length) {
				reaction(genes[i][0] % 5);
			}
		}
	}
	public boolean gene_condition(int cond, int param) {
		if (cond == 0) {//энергии >= x * 15
			return(energy >= param * 15);
		}else if (cond == 1) {//энергии <= x * 15
			return(energy <= param * 15);
		}else if (cond == 2) {//возраст >= x * 15
			return(age >= param * 15);
		}else if (cond == 3) {//возраст <= x * 15
			return(age <= param * 15);
		}else if (cond == 4) {//направление > x % 8
			return(rotate > param % 8);
		}else if (cond == 5) {//направление == x % 8
			return(rotate == param % 8);
		}else if (cond == 6) {//направление < x % 8
			return(rotate < param % 8);
		}else if (cond == 7) {//xpos >= x * 5
			return(xpos >= param * 5);
		}else if (cond == 8) {//xpos <= x * 5
			return(xpos <= param * 5);
		}else if (cond == 9) {//ypos >= x * 3
			return(ypos >= param * 3);
		}else if (cond == 10) {//xpos <= x * 3
			return(ypos <= param * 3);
		}else if (cond == 11) {//"A" у меня >= x * 5
			return(my_ch[0] >= param * 5);
		}else if (cond == 12) {//"A" у меня <= x * 5
			return(my_ch[0] <= param * 5);
		}else if (cond == 13) {//соседей > x % 8
			return(count_neighbours() > param % 8);
		}else if (cond == 14) {//соседей == x % 8
			return(count_neighbours() == param % 8);
		}else if (cond == 15) {//соседей < x % 8
			return(count_neighbours() < param % 8);
		}else if (cond == 16) {//есть ли приход "A"
			return(ch[0][xpos][ypos] > my_ch[0]);
		}else {
			return(true);
		}
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
			}else if (command == 25 || command == 0) {//фотосинтез
				reaction(0);
				index += 1;
				index %= 64;
				break;
			}else if (command == 26) {//походить относительно
				int sens = move(commands[(index + 1) % 64] % 8);
				if (sens == 1) {
					energy -= 1;
					temp += 3;
				}
				index += 2;
				index %= 64;
				break;
			}else if(command == 27) {//походить абсолютно
				int sens = move(rotate);
				if (sens == 1) {
					energy -= 1;
					temp += 3;
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
			}else if (command == 37) {//
				index = commands[(index + 1) % 64];
			}else if (command == 38) {//
				index = commands[(index + 1) % 64];
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
				double ind = commands[(index + 1) % 64] / 63.0;
				if (xpos * 1.0 / world_scale[0] >= ind) {
					index = commands[(index + 2) % 64];
				}else {
					index = commands[(index + 3) % 64];
				}
			}else if (command == 44) {//какая моя позиция y
				double ind = commands[(index + 1) % 64] / 63.0;
				if (ypos * 1.0 / world_scale[1] >= ind) {
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
			}else if (command == 49 || command == 51) {//переработка глюкозы
				reaction(1);
				index += 1;
				index %= 64;
				break;
			}else if (command == 53) {//сдвинуть вещество из - под себя относительно
				move_ch(commands[(index + 1) % 64] % 8, commands[(index + 2) % 64] % 10);
				index += 3;
				index %= 64;
				break;
			}else if (command == 54) {//сдвинуть вещество из - под себя абсолютно
				move_ch(rotate, commands[(index + 1) % 64] % 10);
				index += 2;
				index %= 64;
				break;
			}else if (command == 55) {//собирать относительно
				collect(commands[(index + 1) % 64] % 8, commands[(index + 2) % 64] % 10);
				index += 3;
				index %= 64;
				break;
			}else if (command == 56) {//собирать абсолютно
				collect(rotate, commands[(index + 1) % 64] % 10);
				index += 2;
				index %= 64;
				break;
			}else {
				index += commands[index];
				index %= 64;
			}
		}
	}
	//
	//
	//
	public void collect(int rot, int ch_type) {
		int[] pos = get_rotate_position(rot);
		if (pos[1] >= 0 && pos[1] < world_scale[1]) {
			my_ch[ch_type] += Math.min(10, ch[ch_type][pos[0]][pos[1]]);
			ch[ch_type][pos[0]][pos[1]] -= Math.min(10, ch[ch_type][pos[0]][pos[1]]);
		}
	}
	public void move_ch(int rot, int ch_type) {
		int[] pos = get_rotate_position(rot);
		if (is_free(rot)) {
			ch[ch_type][pos[0]][pos[1]] += ch[ch_type][xpos][ypos];
			ch[ch_type][xpos][ypos] = 0;
			temp += 3;
		}
	}
	public int count_neighbours() {
		int c = 0;
		for (int i = 0; i < 8; i++) {
			int[] pos = get_rotate_position(i);
			if (pos[1] >= 0 && pos[1] < world_scale[1] && map[pos[0]][pos[1]] != null) {
				c++;
			}
		}
		return(c);
	}
	public int see(int rot) {
		int[] pos = get_rotate_position(rot);
		if (pos[1] >= 0 && pos[1] < world_scale[1]) {
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
	//
	public void give(int rot) {
		int[] pos = get_rotate_position(rot);
		if (pos[1] >= 0 && pos[1] < world_scale[1]) {
			if (map[pos[0]][pos[1]] != null) {
				if (map[pos[0]][pos[1]].state == 0) {
					Bot relative = map[pos[0]][pos[1]];
					if (relative.killed == 0) {
						relative.energy += energy / 4;
						energy -= energy / 4;
						temp += 2;
					}
				}
			}
		}
	}
	//
	public void give2(int rot) {
		int[] pos = get_rotate_position(rot);
		if (pos[1] >= 0 && pos[1] < world_scale[1]) {
			if (map[pos[0]][pos[1]] != null) {
				if (map[pos[0]][pos[1]].state == 0) {
					Bot relative = map[pos[0]][pos[1]];
					if (relative.killed == 0) {
						double enr = relative.energy + energy;
						relative.energy = enr / 2;
						energy = enr / 2;
						temp += 2;
					}
				}
			}
		}
	}
	//
	public void attack(int rot) {
		int[] pos = get_rotate_position(rot);
		if (pos[1] >= 0 && pos[1] < world_scale[1]) {
			if (map[pos[0]][pos[1]] != null) {
				Bot victim = map[pos[0]][pos[1]];
				if (victim != null) {
					victim.killed = 1;
					energy += victim.energy;
					victim.energy = 0;
					map[pos[0]][pos[1]] = null;
					victim.die();
					go_red();
					temp += 4;
				}
			}
		}
	}
	//
	public void attack2(int rot, int strength) {
		int[] pos = get_rotate_position(rot);
		if (pos[1] >= 0 && pos[1] < world_scale[1]) {
			if (map[pos[0]][pos[1]] != null) {
				Bot victim = map[pos[0]][pos[1]];
				if (victim != null) {
					if (victim.energy >= strength) {
						energy += strength;
						victim.energy -= strength;
						temp += 3;
					}else {
						energy += victim.energy;
						victim.energy = 0;
						victim.killed = 1;
						map[pos[0]][pos[1]] = null;
						victim.die();
						temp += 3;
					}
					go_red();
				}
			}
		}
	}
	//
	public int move(int rot) {
		int[] pos = get_rotate_position(rot);
		if (is_free(rot)) {
			Bot self = map[xpos][ypos];
			map[xpos][ypos] = null;
			xpos = pos[0];
			ypos = pos[1];
			x = xpos * 3;
			y = ypos * 3;
			map[xpos][ypos] = self;
			return(1);
		}
		return(0);
	}
	//
	public void multiply(int rot, ListIterator<Bot> iterator) {
		int[] pos = get_rotate_position(rot);
		if (is_free(rot)) {
			energy -= 150;
			if (energy <= 0) {
				killed = 1;
				map[xpos][ypos] = null;
				die();
			}else {
				Color new_color = color;
				int[] new_brain = new int[64];
				for (int i = 0; i < 64; i++) {
					new_brain[i] = commands[i];
				}
				int[][] new_genes = new int[2][3];
				for (int i = 0; i < 2; i++) {
					for (int j = 0; j < 3; j++) {
						new_genes[i][j] = genes[i][j];
					}
				}
				if (rand.nextInt(4) == 0) {//мутация
					new_color = new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
					new_brain[rand.nextInt(64)] = rand.nextInt(64);
					if (rand.nextInt(16) == 0) {
						new_genes[rand.nextInt(2)][rand.nextInt(3)] = rand.nextInt(64);
					}
				}
				Bot new_bot = new Bot(pos[0], pos[1], new_color, energy / 2, temp, map, ch, temp_map, objects);
				for (int i = 0; i < 10; i++) {
					new_bot.my_ch[i] = my_ch[i] / 2;
					my_ch[i] /= 2;
				}
				energy /= 2;
				new_bot.commands = new_brain;
				new_bot.genes = new_genes;
				map[pos[0]][pos[1]] = new_bot;
				iterator.add(new_bot);
				temp += 5;
			}
		}
	}
	//
	//
	//
	public void reaction(int reaction_type) {
		boolean inp = true;
		for (int j = 0; j < Constant.reactions[reaction_type][0][0].length; j++) {
			inp = inp && my_ch[(int)(Constant.reactions[reaction_type][0][1][j]) - 3] > Constant.reactions[reaction_type][0][0][j];
		}
		if (inp) {
			double speed = 1;
			for (int j = 0; j < Constant.reactions[reaction_type][2][0].length; j++) {
				double spmin = Constant.reactions[reaction_type][2][2][j];
				int ch_type = (int)(Constant.reactions[reaction_type][2][1][j]);
				double coeff = Constant.reactions[reaction_type][2][0][j];
				double spmax = Constant.reactions[reaction_type][2][3][j];
				double count = 0;
				if (ch_type >= 3) {
					count = my_ch[ch_type - 3];
				}else if (ch_type == 0) {
					count = photo_list[bot_in_sector()];
				}else if (ch_type == 1) {
					count = energy;
				}else if (ch_type == 2) {
					count = temp;
				}
				count = Math.min(count, spmax);
				speed *= Math.max(count * coeff, spmin);
			}
			for (int j = 0; j < Constant.reactions[reaction_type][0][0].length; j++) {
				int ch_type = (int)(Constant.reactions[reaction_type][0][1][j]);
				if (ch_type >= 3) {
					my_ch[(int)(Constant.reactions[reaction_type][0][1][j]) - 3] -= Constant.reactions[reaction_type][0][0][j] * speed;
				}else if (ch_type == 1) {
					
				}
			}
			for (int j = 0; j < Constant.reactions[reaction_type][1][0].length; j++) {
				if ((int)(Constant.reactions[reaction_type][1][1][j]) >= 3) {
					my_ch[(int)(Constant.reactions[reaction_type][1][1][j]) - 3] += Constant.reactions[reaction_type][1][0][j] * speed;
				}else if ((int)(Constant.reactions[reaction_type][1][1][j]) == 1) {
					energy += Constant.reactions[reaction_type][1][0][j] * speed;
				}
			}
			go_color(Constant.reactions_color[reaction_type]);
		}
	}
	//
	public boolean is_free(int rot) {
		int[] pos = get_rotate_position(rot);
		if (pos[1] >= 0 && pos[1] < world_scale[1]) {
			if (map[pos[0]][pos[1]] == null && ch[1][pos[0]][pos[1]] < 500) {
				return(true);
			}
		}
		return(false);
	}
	//
	public void die() {
		for (int i = 0; i < 10; i++) {
			double c = my_ch[i] / 9;
			if (i == 0) {
				c += (energy * 0.25 + 50) / 9;
			}
			for (int j = 0; j < 8; j++) {
				int[] pos = get_rotate_position(j);
				if (pos[1] >= 0 && pos[1] < world_scale[1]) {
					ch[i][pos[0]][pos[1]] += c;
				}else {
					ch[i][xpos][ypos] += c;
				}
			}
			ch[i][xpos][ypos] += c;
		}
	}
	//
	public void go_red() {
		if (c_red != -1) {
			c_red = border(c_red + 4, 255, 0);
			c_green = border(c_green - 2, 255, 0);
			c_blue = border(c_blue - 2, 255, 0);
		}else {
			c_red = 255;
			c_green = 0;
			c_blue = 0;
		}
	}
	//
	public void go_green() {
		if (c_green != -1) {
			c_red = border(c_red - 2, 255, 0);
			c_green = border(c_green + 3, 255, 0);
			c_blue = border(c_blue - 2, 255, 0);
		}else {
			c_red = 0;
			c_green = 255;
			c_blue = 0;
		}
	}
	//
	public void go_blue() {
		if (c_blue != -1) {
			c_red = border(c_red - 2, 255, 0);
			c_green = border(c_green - 2, 255, 0);
			c_blue = border(c_blue + 4, 255, 0);
		}else {
			c_red = 0;
			c_green = 0;
			c_blue = 255;
		}
	}
	//
	public void go_yellow() {
		if (c_blue != -1) {
			c_red = border(c_red + 4, 255, 0);
			c_green = border(c_green + 4, 255, 0);
			c_blue = border(c_blue - 2, 255, 0);
		}else {
			c_red = 255;
			c_green = 255;
			c_blue = 0;
		}
	}
	public void go_color(Color c) {
		if (c_red != -1 && c_green != -1 && c_blue != -1) {
			if (c_red <= c.getRed()) {
				c_red = border(c_red + 4, 255, 0);
			}else {
				c_red = border(c_red - 2, 255, 0);
			}
			if (c_green <= c.getGreen()) {
				c_green = border(c_green + 4, 255, 0);
			}else {
				c_green = border(c_green - 2, 255, 0);
			}
			if (c_blue <= c.getBlue()) {
				c_blue = border(c_blue + 4, 255, 0);
			}else {
				c_blue = border(c_blue - 2, 255, 0);
			}
		}else {
			c_red = c.getRed();
			c_green = c.getGreen();
			c_blue = c.getBlue();
		}
	}
	//
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
	//
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
	//
	public int bot_in_sector() {
		int sec = ypos / (world_scale[1] / 8);
		if (sec > 7) {
			sec = 7;
		}
		return(sec);
	}
	//
	public int border(int number, int border1, int border2) {
		if (number > border1) {
			number = border1;
		}else if (number < border2) {
			number = border2;
		}
		return(number);
	}
	//
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
