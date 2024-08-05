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
	public int type = 0;
	public int[][] commands = new int[8][65];
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
		4,
		8,
		12
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
	private int sector_len = world_scale[1] / 8;
	public int[] diff = {0, 0, 0, 0, 0};//приход минералов, переработка минералов, атака, перемещение, Фотосинтез
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
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 65; j++) {
				commands[i][j] = rand.nextInt(64);
			}
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
				if (c_red + c_green + c_blue == 0) {
					r = 128;
					g = 128;
					b = 128;
				}else {
					r = (int)((c_red * 1.0) / (c_red + c_green + c_blue) * 255.0);
					g = (int)((c_green * 1.0) / (c_red + c_green + c_blue) * 255.0);
					b = (int)((c_blue * 1.0) / (c_red + c_green + c_blue) * 255.0);
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
			}else if (draw_type == 5) {//типа
				if (type == 0) {
					canvas.setColor(new Color(0, 0, 255));
				}else if (type == 1) {
					canvas.setColor(new Color(0, 128, 255));
				}else if (type == 2) {
					canvas.setColor(new Color(0, 255, 0));
				}else if (type == 3) {
					canvas.setColor(new Color(255, 0, 0));
				}else if (type == 4) {
					canvas.setColor(new Color(0, 0, 255));
				}else if (type == 5) {
					canvas.setColor(new Color(255, 0, 255));
				}else if (type == 6) {
					canvas.setColor(new Color(255, 255, 0));
				}else if (type == 7) {
					canvas.setColor(new Color(128, 128, 128));
				}
			}
			canvas.fillRect(x + 1, y + 1, 8, 8);
		}else {//рисуем органику
			canvas.setColor(new Color(0, 0, 0));
			canvas.fillRect(x + 1, y + 1, 8, 8);
			canvas.setColor(new Color(128, 128, 128));
			canvas.fillRect(x + 2, y + 2, 6, 6);
		}
	}
	public int Update(ListIterator<Bot> iterator) {
		if (killed == 0) {
			if (state == 0) {//бот
				int sector = bot_in_sector();
				energy -= 1;
				age -= 1;
				if (sector <= 7 && sector >= 5 && commands[type][64] % 2 == 0) {
					if (diff[0] == 0) {
						diff[0] = 1;
						minerals += minerals_list[sector - 5];
					}else {
						minerals += (int)(minerals_list[sector - 5] * (diff[0] / (diff[0] + diff[1] + diff[2] + diff[3] + diff[4])));
						diff[0]++;
					}
				}
				update_commands(iterator);
				if (energy <= 0) {
					killed = 1;
					map[xpos][ypos] = 0;
					return(0);
				}else if (energy > 1000) {
					energy = 1000;
				}
				if (energy >= 800) {//автоматическое деление
					multiply(rotate, 0, iterator);
				}
				if (age <= 0) {
					state = 1;
					state2 = 2;
					map[xpos][ypos] = 2;
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
	public void update_commands(ListIterator<Bot> iterator) {//мозг
		for (int i = 0; i < 5; i++) {
			int command = commands[type][index];
			if (command == 23) {//повернуться
				rotate += commands[type][(index + 1) % 64] % 8;
				rotate %= 8;
				index += 2;
				index %= 64;
			}else if (command == 24) {//сменить направление
				rotate = commands[type][(index + 1) % 64] % 8;
				index += 2;
				index %= 64;
			}else if (command == 25) {//фотосинтез
				int sector = bot_in_sector();
				if (sector <= 5) {
					if (diff[2] == 0) {
						diff[2] = 1;
						energy += photo_list[sector];
					}else {
						energy += (int)(photo_list[sector] * (diff[2] / (diff[0] + diff[1] + diff[2] + diff[3] + diff[4])));
						diff[2]++;
					}
					c_green += 1;
				}
				index += 1;
				index %= 64;
				break;
			}else if (command == 26) {//походить относительно
				int sens = 0;
				if (diff[4] == 0) {
					diff[4] = 1;
					sens = move(commands[type][(index + 1) % 64] % 8);
				}else {
					if (rand.nextInt(1000) < (int)((diff[4] / (diff[0] + diff[1] + diff[2] + diff[3] + diff[4])) * 1000.0)) {
						sens = move(commands[type][(index + 1) % 64] % 8);
						diff[4]++;
					}
				}
				if (sens == 1) {
					energy -= 1;
				}
				index += 2;
				index %= 64;
				break;
			}else if(command == 27) {//походить абсолютно
				int sens = 0;
				if (diff[4] == 0) {
					diff[4] = 1;
					sens = move(rotate);
				}else {
					if (rand.nextInt(1000) < (int)((diff[4] / (diff[0] + diff[1] + diff[2] + diff[3] + diff[4])) * 1000.0)) {
						sens = move(rotate);
						diff[4]++;
					}
				};
				if (sens == 1) {
					energy -= 1;
				}
				index += 1;
				index %= 64;
				break;
			}else if (command == 28) {//атаковать относительно
				attack(commands[type][(index + 1) % 64] % 8);
				index += 2;
				index %= 64;
				break;
			}else if (command == 29) {//атаковать абсолютно
				attack(rotate);
				index += 1;
				index %= 64;
				break;
			}else if (command == 30) {//посмотреть относительно
				int[] pos = get_rotate_position(commands[type][(index + 1) % 64] % 8);
				if (pos[1] > 0 & pos[1] < world_scale[1]) {
					if (map[pos[0]][pos[1]] == 0) {
						index = commands[type][(index + 3) % 64];//если ничего
					}else if (map[pos[0]][pos[1]] == 1) {
						Bot b = find(pos);
						if (b != null) {
							if (is_relative(color, b.color)) {
								index = commands[type][(index + 4) % 64];//если родственник
							}else {
								index = commands[type][(index + 3) % 64];//если враг
							}
						}else {
							index = commands[type][(index + 2) % 64];//если ничего
						}
					}else if (map[pos[0]][pos[1]] == 2) {
						index = commands[type][(index + 6) % 64];//если органика
					}
				}else {
					index = commands[type][(index + 2) % 64];//если граница
				}
			}else if (command == 31) {//посмотреть абсолютно
				int[] pos = get_rotate_position(rotate);
				if (pos[1] > 0 & pos[1] < world_scale[1]) {
					if (map[pos[0]][pos[1]] == 0) {
						index = commands[type][(index + 2) % 64];//если ничего
					}else if (map[pos[0]][pos[1]] == 1) {
						Bot b = find(pos);
						if (b != null) {
							if (is_relative(color, b.color)) {
								index = commands[type][(index + 4) % 64];//если родственник
							}else {
								index = commands[type][(index + 3) % 64];//если враг
							}
						}else {
							index = commands[type][(index + 2) % 64];//если ничего
						}
					}else if (map[pos[0]][pos[1]] == 2) {
						index = commands[type][(index + 5) % 64];//если органика
					}
				}else {
					index = commands[type][(index + 1) % 64];//если граница
				}
			}else if (command == 34 | command == 50) {//отдать ресурсы относительно
				give(commands[type][(index + 1) % 64] % 8);
				index += 2;
				index %= 64;
				break;
			}else if (command == 35 | command == 52) {//отдать ресурсы абсолютно
				give(rotate);
				index += 1;
				index %= 64;
				break;
			}else if (command == 36) {//сколько у меня энергии
				int ind = commands[type][(index + 1) % 64] * 15;
				if (energy >= ind) {
					index = commands[type][(index + 2) % 64];
				}else {
					index = commands[type][(index + 3) % 64];
				}
			}else if (command == 37) {//сколько у меня минералов
				int ind = commands[type][(index + 1) % 64] * 15;
				if (minerals >= ind) {
					index = commands[type][(index + 2) % 64];
				}else {
					index = commands[type][(index + 3) % 64];
				}
			}else if (command == 38) {//преобразовать минералы в энергию
				if (minerals > 0) {
					c_blue++;
				}
				if (diff[1] == 0) {
					diff[1] = 1;
					energy += minerals;
				}else {
					energy += (int)(minerals * (diff[1] / (diff[0] + diff[1] + diff[2] + diff[3] + diff[4])));
					diff[1]++;
				}
				minerals = 0;
				index += 1;
				index %= 64;
				break;
			}else if (command == 39) {//есть ли фотосинтез
				int sector = bot_in_sector();
				if (sector <= 5) {
					index = commands[type][(index + 1) % 64];
				}else {
					index = commands[type][(index + 2) % 64];
				}
			}else if (command == 40) {//есть ли приход минералов
				int sector = bot_in_sector();
				if (sector <= 7 & sector >= 5) {
					index = commands[type][(index + 1) % 64];
				}else {
					index = commands[type][(index + 2) % 64];
				}
			}else if (command == 41) {//поделиться относительно
				multiply(commands[type][(index + 1) % 64] % 8, commands[type][(index + 2) % 64] % 8, iterator);
				index += 3;
				index %= 64;
				break;
			}else if (command == 42) {//поделиться абсолютно
				multiply(rotate, commands[type][(index + 1) % 64] % 8, iterator);
				index += 2;
				index %= 64;
				break;
			}else if (command == 43) {//какая моя позиция x
				double ind = commands[type][(index + 1) % 64] / 64.0;
				if (xpos * world_scale[0] >= ind) {
					index = commands[type][(index + 2) % 64];
				}else {
					index = commands[type][(index + 3) % 64];
				}
			}else if (command == 44) {//какая моя позиция y
				double ind = commands[type][(index + 1) % 64] / 64.0;
				if (ypos * world_scale[1] >= ind) {
					index = commands[type][(index + 2) % 64];
				}else {
					index = commands[type][(index + 3) % 64];
				}
			}else if (command == 45) {//какой мой возраст
				int ind = commands[type][(index + 1) % 64] * 15;
				if (age >= ind) {
					index = commands[type][(index + 2) % 64];
				}else {
					index = commands[type][(index + 3) % 64];
				}
			}else if (command == 46) {//равномерное распределение ресурсов относительно
				give2(commands[type][(index + 1) % 64] % 8);
				index += 2;
				index %= 64;
				break;
			}else if (command == 47) {//равномерное распределение ресурсов абсолютно
				give2(rotate);
				index += 1;
				index %= 64;
				break;
			}else if (command == 48) {//безусловный переход
				index = commands[type][(index + 1) % 64];
			}else {
				index += command;
				index %= 64;
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
					if (diff[3] == 0) {
						diff[3] = 1;
						energy += victim.energy;
					}else {
						energy += (int)(victim.energy * (diff[3] / (diff[0] + diff[1] + diff[2] + diff[3] + diff[4])));
						diff[3]++;
					}
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
	public boolean is_relative(Color color1, Color color2) {
		boolean is_red = (color1.getRed() - 20 < color2.getRed()) && (color1.getRed() + 20 > color2.getRed());
		boolean is_green = (color1.getGreen() - 20 < color2.getGreen()) && (color1.getGreen() + 20 > color2.getGreen());
		boolean is_blue = (color1.getBlue() - 20 < color2.getBlue()) && (color1.getBlue() + 20 > color2.getBlue());
		return(is_red && is_green && is_blue);
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
	public void multiply(int rot, int new_type, ListIterator<Bot> iterator) {
		int[] pos = get_rotate_position(rot);
		if (pos[1] > 0 & pos[1] < world_scale[1]) {
			if (map[pos[0]][pos[1]] == 0) {
				energy -= 150;
				if (energy <= 0) {
					killed = 1;
					map[xpos][ypos] = 0;
				}else {
					map[pos[0]][pos[1]] = 1; 
					Color new_color;
					if (rand.nextInt(100) == 0) {
						new_color = new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
					}else {
						new_color = new Color(border(color.getRed() + rand.nextInt(-12, 12), 255, 0), border(color.getGreen() + rand.nextInt(-12, 12), 255, 0), border(color.getBlue() + rand.nextInt(-12, 12), 255, 0));
					}
					int[][] new_brain = new int[8][65];
					for (int i = 0; i < 8; i++) {
						for (int j = 0; j < 65; j++) {
							new_brain[i][j] = commands[i][j];
						}
					}
					if (rand.nextInt(4) == 0) {//мутация
						for (int i = 0; i < 8; i++) {
							new_brain[i][rand.nextInt(65)] = rand.nextInt(64);
						}
					}
					Bot new_bot = new Bot(pos[0], pos[1], new_color, energy / 2, map, objects);
					new_bot.minerals = minerals / 2;
					energy /= 2;
					minerals /= 2;
					new_bot.commands = new_brain;
					new_bot.type = new_type;
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
