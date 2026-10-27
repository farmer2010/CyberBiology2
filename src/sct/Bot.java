package sct;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Random;
import java.util.ListIterator;

public class Bot{
	ArrayList<Bot> objects;
	Random rand = new Random();
	public int xpos;
	public int ypos;
	public Color color;
	public double energy;
	public int minerals;
	public int killed = 0;
	public Bot[][] map;
	public World world;
	public int[] commands;
	public int[] memory = new int[32];
	private int genome_length;
	public int index = 0;
	public int age = Constant.max_age;
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
	//
	//divers
	//
	public boolean on_land = false;
	public int move_y_timer = 0;
	public Bot(int new_xpos, int new_ypos, Color new_color, double new_energy, World new_world) {
		xpos = new_xpos;
		ypos = new_ypos;
		color = new_color;
		energy = new_energy;
		minerals = 0;
		world = new_world;
		objects = world.objects;
		map = world.Map;
		if (Constant.enable_new_genome) {
			genome_length = 256;
		}else {
			genome_length = 64;
		}
		commands = new int[genome_length];
		for (int i = 0; i < genome_length; i++) {
			commands[i] = rand.nextInt(genome_length);
		}
	}
	public void Draw(Graphics canvas, int draw_type, boolean record) {
		int scale;
		int x;
		int y;
		if (!record) {
			scale = world.zoom_slider.getValue();
			x = xpos * scale - world.zoom_pos[0] * scale + (Constant.scr_W - 300) / 2;
			y = ypos * scale - world.zoom_pos[1] * scale + Constant.scr_H / 2;
		}else {
			scale = Constant.bot_scale;
			x = xpos * scale;
			y = ypos * scale;
		}
		//
		boolean draw_frame = Constant.draw_frame || record;
		if (scale < 7) {
			draw_frame = false;
		}
		boolean draw_rotate = Constant.draw_rotate && !record;
		if (scale < 5) {
			draw_rotate = false;
		}
		//
		if (state == 0) {//рисуем бота
			if (draw_frame) {
				canvas.setColor(new Color(0, 0, 0));
				canvas.fillRect(x, y, scale, scale);
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
				canvas.setColor(Constant.gradient(new Color(255, 255, 0), new Color(255, 0, 0), energy / Constant.max_energy));
			}else if (draw_type == 3) {//минералов
				canvas.setColor(Constant.gradient(new Color(0, 255, 0), new Color(0, 255, 255), minerals / 1000.0));
			}else if (draw_type == 4) {//возраста
				canvas.setColor(Constant.gradient(new Color(0, 0, 255), new Color(255, 255, 0), age / (Constant.max_age * 1.0)));
			}else if (draw_type == 5) {
				//
			}
			if (draw_frame) {
				canvas.fillRect(x + 1, y + 1, scale - 2, scale - 2);
			}else {
				canvas.fillRect(x, y, scale, scale);
			}
			if (draw_rotate) {
				canvas.setColor(new Color(0, 0, 0));
				int b = scale / 2;
				canvas.drawLine(x + b, y + b, x + b + movelist[rotate][0] * (b - 1), y + b + movelist[rotate][1] * (b - 1));
			}
		}else {//рисуем органику
			if (draw_frame) {
				canvas.setColor(new Color(0, 0, 0));
				canvas.fillRect(x + 1, y + 1, scale - 2, scale - 2);
			}
			if (draw_type == 2) {
				canvas.setColor(Constant.gradient(new Color(255, 255, 0), new Color(255, 0, 0), energy / Constant.max_energy));
			}else {
				canvas.setColor(new Color(100, 100, 100));
			}
			if (draw_frame) {
				canvas.fillRect(x + 2, y + 2, scale - 4, scale - 4);
			}else {
				if (scale <= 2) {
					canvas.fillRect(x, y, 1, 1);
				}else {
					canvas.fillRect(x + 1, y + 1, scale - 2, scale - 2);
				}
			}
		}
	}
	public int Update(ListIterator<Bot> iterator) {
		if (killed == 0) {
			if (state == 0) {//ботbot_in_sector();
				if (Constant.enable_divers && divers_sector() == 0) on_land = true;
				if (Constant.enable_new_genome) update_memory();
				//
				energy -= Constant.energy_for_life;
				if (Constant.allow_age) age--;
				minerals += Constant.minerals_list[sector(Constant.minerals_list.length)];
				//
				if (!Constant.enable_new_genome) {
					update_commands(iterator);
				}else {
					new_update_commands(iterator);
				}
				//
				if (Constant.enable_divers) {
					if (move_y_timer < 2) {
						move_y_timer++;
					}else if (move_y_timer >= 2) {
						move_y_timer = 0;
						if (rand.nextInt(0, 1000) < Constant.force_move_y) {
							killed = 1;
							map[xpos][ypos] = null;
							return(0);
						}
					}
				}
				if (energy < 1) {
					killed = 1;
					map[xpos][ypos] = null;
					return(0);
				}else if (energy > Constant.max_energy) {
					energy = Constant.max_energy;
				}
				if (energy >= Constant.energy_for_auto_multiply && !Constant.block_auto_multiply) {//автоматическое деление
					multiply(rotate, iterator);
				}
				if (age <= 0) {
					if (Constant.allow_organics) {
						state = 1;
					}else {
						killed = 1;
						map[xpos][ypos] = null;
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
			}else {//стоящая органика
				//
			}
		}
		return(0);
	}
	public void update_commands(ListIterator<Bot> iterator) {//мозг
		boolean ret = false;
		for (int i = 0; i < 5; i++) {
			int command = commands[index];
			switch (command) {
				case(23)://повернуться
					rotate += commands[(index + 1) % 64] % 8;
					rotate %= 8;
					index += 2;
					index %= 64;
					break;
				case(24)://сменить направление
					rotate = commands[(index + 1) % 64] % 8;
					index += 2;
					index %= 64;
					break;
				case(25)://фотосинтез
					photosynthesis();
					index += 1;
					index %= 64;
					ret = true;
					break;
				case(26)://походить относительно
					int sens = move(commands[(index + 1) % 64] % 8);
					if (sens == 1) {
						energy -= 1;
					}
					index += 2;
					index %= 64;
					ret = true;
					break;
				case(27)://походить абсолютно
					sens = move(rotate);
					if (sens == 1) {
						energy -= 1;
					}
					index += 1;
					index %= 64;
					ret = true;
					break;
				case(28)://атаковать относительно
					attack2(commands[(index + 1) % 64] % 8, Constant.attack_strength);
					index += 2;
					index %= 64;
					ret = true;
					break;
				case(29)://атаковать абсолютно
					attack2(rotate, Constant.attack_strength);
					index += 1;
					index %= 64;
					ret = true;
					break;
				case(30)://посмотреть относительно
					int rot = commands[(index + 1) % 64] % 8;
					index = commands[(index + 2 + see(rot)) % 64];
					break;
				case(31)://посмотреть абсолютно
					index = commands[(index + 1 + see(rotate)) % 64];
					break;
				case(34)://отдать ресурсы относительно
				case(50):
					give(commands[(index + 1) % 64] % 8);
					index += 2;
					index %= 64;
					ret = true;
					break;
				case(35)://отдать ресурсы абсолютно
				case(52):
					give(rotate);
					index += 1;
					index %= 64;
					ret = true;
					break;
				case(36)://сколько у меня энергии
					int ind = commands[(index + 1) % 64] * 15;
					if (energy >= ind) {
						index = commands[(index + 2) % 64];
					}else {
						index = commands[(index + 3) % 64];
					}
					break;
				case(37)://сколько у меня минералов
					ind = commands[(index + 1) % 64] * 15;
					if (minerals >= ind) {
						index = commands[(index + 2) % 64];
					}else {
						index = commands[(index + 3) % 64];
					}
					break;
				case(38)://преобразовать минералы в энергию
					if (minerals > 0) {
						go_color(Constant.mnr_color);
					}
					energy += minerals * 4;
					minerals = 0;
					index += 1;
					index %= 64;
					ret = true;
					break;
				case(39)://есть ли фотосинтез
					if (!Constant.enable_divers) {
						if (Constant.photo_list[sector(Constant.photo_list.length)] > 0) {
							index = commands[(index + 1) % 64];
						}else {
							index = commands[(index + 2) % 64];
						}
					}else {
						if (divers_sector() == 0) {
							index = commands[(index + 1) % 64];
						}else {
							index = commands[(index + 2) % 64];
						}
					}
					break;
				case(40)://есть ли приход минералов
					if (Constant.minerals_list[sector(Constant.minerals_list.length)] > 0) {
						index = commands[(index + 1) % 64];
					}else {
						index = commands[(index + 2) % 64];
					}
					break;
				case(41)://поделиться относительно
					multiply(commands[(index + 1) % 64] % 8, iterator);
					index += 2;
					index %= 64;
					ret = true;
					break;
				case(42)://поделиться абсолютно
					multiply(rotate, iterator);
					index += 1;
					index %= 64;
					ret = true;
					break;
				case(43)://какая моя позиция x
					if (xpos * 1.0 / Constant.W >= commands[(index + 1) % 64] / 63.0) {
						index = commands[(index + 2) % 64];
					}else {
						index = commands[(index + 3) % 64];
					}
					break;
				case(44)://какая моя позиция y
					if (ypos * 1.0 / Constant.H >= commands[(index + 1) % 64] / 63.0) {
						index = commands[(index + 2) % 64];
					}else {
						index = commands[(index + 3) % 64];
					}
					break;
				case(45)://какой мой возраст
					if (age >= commands[(index + 1) % 64] * 15) {
						index = commands[(index + 2) % 64];
					}else {
						index = commands[(index + 3) % 64];
					}
					break;
				case(46)://равномерное распределение ресурсов относительно
					give2(commands[(index + 1) % 64] % 8);
					index += 2;
					index %= 64;
					ret = true;
					break;
				case(48)://равномерное распределение ресурсов абсолютно
					give2(rotate);
					index += 1;
					index %= 64;
					ret = true;
					break;
				case(49)://безусловный переход
					index = commands[(index + 1) % 64];
					break;
				default:
					index = (index + command) % 64;
					break;
			}
			if (ret) {
				break;
			}
		}
	}
	public void new_update_commands(ListIterator<Bot> iterator) {//мозг v2
		for (int i = 0; i < 5; i++) {
			boolean ret = false;
			int command = commands[index] % 128;
			switch (command) {
				case(0)://фотосинтез
					photosynthesis();
					index += 1;
					index %= genome_length;
					ret = true;
					break;
				case(1)://повернуться по направлению
					rotate = get_param_value(commands[(index + 1) % genome_length]) % 8;
					index += 2;
					index %= genome_length;
					break;
				case(2)://повернуть на ...
					rotate += get_param_value(commands[(index + 1) % genome_length]);
					rotate %= 8;
					index += 2;
					index %= genome_length;
					break;
				case(3)://перемещение относительно
					int sens = move((rotate + get_param_value(commands[(index + 1) % genome_length])) % 8);
					if (sens == 1) {
						energy -= 1;
					}
					index += 2;
					index %= genome_length;
					ret = true;
					break;
				case(4)://перемещение абсолютно
					sens = move(get_param_value(commands[(index + 1) % genome_length]) % 8);
					if (sens == 1) {
						energy -= 1;
					}
					index += 2;
					index %= genome_length;
					ret = true;
					break;
				case(5)://атаковать относительно
					attack2((rotate + get_param_value(commands[(index + 1) % genome_length])) % 8, Constant.attack_strength);
					index += 2;
					index %= genome_length;
					ret = true;
					break;
				case(6)://атаковать абсолютно
					attack2(get_param_value(commands[(index + 1) % genome_length]) % 8, Constant.attack_strength);
					index += 2;
					index %= genome_length;
					ret = true;
					break;
				case(7)://отдать ресурсы относительно
					give((rotate + get_param_value(commands[(index + 1) % genome_length])) % 8);
					index += 2;
					index %= genome_length;
					ret = true;
					break;
				case(8)://отдать ресурсы абсолютно
					give(get_param_value(commands[(index + 1) % genome_length]) % 8);
					index += 2;
					index %= genome_length;
					ret = true;
					break;
				case(9)://равномерное распределение ресурсов относительно
					give2((rotate + get_param_value(commands[(index + 1) % genome_length])) % 8);
					index += 2;
					index %= genome_length;
					ret = true;
					break;
				case(10)://равномерное распределение ресурсов абсолютно
					give2(get_param_value(commands[(index + 1) % genome_length]) % 8);
					index += 2;
					index %= genome_length;
					ret = true;
					break;
				case(11)://поделиться относительно
					multiply((rotate + get_param_value(commands[(index + 1) % genome_length])) % 8, iterator);
					index += 2;
					index %= genome_length;
					ret = true;
					break;
				case(12)://поделиться абсолютно
					multiply(get_param_value(commands[(index + 1) % genome_length]) % 8, iterator);
					index += 2;
					index %= genome_length;
					ret = true;
					break;
				case(13)://преобразовать минералы в энергию
					if (minerals > 0) {
						go_color(Constant.mnr_color);
					}
					energy += minerals * 4;
					minerals = 0;
					index += 1;
					index %= genome_length;
					ret = true;
					break;
				case(14)://посмотреть относительно
					int rot = (rotate + get_param_value(commands[(index + 1) % genome_length])) % 8;
					index = commands[(index + 2 + see(rot)) % 64];
					break;
				case(15)://посмотреть абсолютно
					index = commands[(index + 1 + see(get_param_value(commands[(index + 1) % genome_length]) % 8)) % 64];
					break;
				case(16)://сколько у меня энергии
					if (energy >= get_param_value(commands[(index + 1) % genome_length]) * 1.0 / genome_length * Constant.max_energy) {
						index = commands[(index + 2) % genome_length];
					}else {
						index = commands[(index + 3) % genome_length];
					}
					break;
				case(17)://сколько у меня минералов
					if (minerals >= get_param_value(commands[(index + 1) % genome_length]) * 1.0 / genome_length * 1000) {
						index = commands[(index + 2) % genome_length];
					}else {
						index = commands[(index + 3) % genome_length];
					}
					break;
				case(18)://какой у меня возраст
					if (age >= get_param_value(commands[(index + 1) % genome_length]) * 1.0 / genome_length * Constant.max_age) {
						index = commands[(index + 2) % genome_length];
					}else {
						index = commands[(index + 3) % genome_length];
					}
					break;
				case(19)://какая позиция x
					if (xpos >= get_param_value(commands[(index + 1) % genome_length]) * 1.0 / genome_length * Constant.W) {
						index = commands[(index + 2) % genome_length];
					}else {
						index = commands[(index + 3) % genome_length];
					}
					break;
				case(20)://какая позиция y
					if (ypos >= get_param_value(commands[(index + 1) % genome_length]) * 1.0 / genome_length * Constant.H) {
						index = commands[(index + 2) % genome_length];
					}else {
						index = commands[(index + 3) % genome_length];
					}
					break;
				case(21)://есть ли фотосинтез
					if (!Constant.enable_divers) {
						if (Constant.photo_list[sector(Constant.photo_list.length)] > 0) {
							index = commands[(index + 1) % genome_length];
						}else {
							index = commands[(index + 2) % genome_length];
						}
					}else {
						if (divers_sector() == 0) {
							index = commands[(index + 1) % genome_length];
						}else {
							index = commands[(index + 2) % genome_length];
						}
					}
					break;
				case(22)://есть ли приход минералов
					if (Constant.minerals_list[sector(Constant.minerals_list.length)] > 0) {
						index = commands[(index + 1) % genome_length];
					}else {
						index = commands[(index + 2) % genome_length];
					}
					break;
				case(23)://толкнуть относительно
					sens = push((rotate + get_param_value(commands[(index + 1) % genome_length])) % 8);
					if (sens == 1) {
						energy -= 1;
					}
					index += 2;
					index %= genome_length;
					ret = true;
					break;
				case(24)://толкнуть абсолютно
					sens = push(get_param_value(commands[(index + 1) % genome_length]) % 8);
					if (sens == 1) {
						energy -= 1;
					}
					index += 2;
					index %= genome_length;
					ret = true;
					break;
				case(25)://значение ячейки == параметр
					if (memory[commands[(index + 1) % genome_length] % 32] == commands[(index + 2) % genome_length]) {
						index = commands[(index + 3) % genome_length];
					}else {
						index = commands[(index + 4) % genome_length];
					}
					break;
				case(26)://значение ячейки != параметр
					if (memory[commands[(index + 1) % genome_length] % 32] != commands[(index + 2) % genome_length]) {
						index = commands[(index + 3) % genome_length];
					}else {
						index = commands[(index + 4) % genome_length];
					}
					break;
				case(27)://значение ячейки > параметр
					if (memory[commands[(index + 1) % genome_length] % 32] > commands[(index + 2) % genome_length]) {
						index = commands[(index + 3) % genome_length];
					}else {
						index = commands[(index + 4) % genome_length];
					}
					break;
				case(28)://значение ячейки < параметр
					if (memory[commands[(index + 1) % genome_length] % 32] < commands[(index + 2) % genome_length]) {
						index = commands[(index + 3) % genome_length];
					}else {
						index = commands[(index + 4) % genome_length];
					}
					break;
				case(29)://значение ячейки >= параметр
					if (memory[commands[(index + 1) % genome_length] % 32] >= commands[(index + 2) % genome_length]) {
						index = commands[(index + 3) % genome_length];
					}else {
						index = commands[(index + 4) % genome_length];
					}
					break;
				case(30)://значение ячейки <= параметр
					if (memory[commands[(index + 1) % genome_length] % 32] <= commands[(index + 2) % genome_length]) {
						index = commands[(index + 3) % genome_length];
					}else {
						index = commands[(index + 4) % genome_length];
					}
					break;
				case(31)://значение ячейки == значение другой ячейки
					if (memory[commands[(index + 1) % genome_length] % 32] == memory[commands[(index + 2) % genome_length] % 32]) {
						index = commands[(index + 3) % genome_length];
					}else {
						index = commands[(index + 4) % genome_length];
					}
					break;
				case(32)://значение ячейки != значение другой ячейки
					if (memory[commands[(index + 1) % genome_length] % 32] != memory[commands[(index + 2) % genome_length] % 32]) {
						index = commands[(index + 3) % genome_length];
					}else {
						index = commands[(index + 4) % genome_length];
					}
					break;
				case(33)://значение ячейки > значение другой ячейки
					if (memory[commands[(index + 1) % genome_length] % 32] > memory[commands[(index + 2) % genome_length] % 32]) {
						index = commands[(index + 3) % genome_length];
					}else {
						index = commands[(index + 4) % genome_length];
					}
					break;
				case(34)://значение ячейки < значение другой ячейки
					if (memory[commands[(index + 1) % genome_length] % 32] < memory[commands[(index + 2) % genome_length] % 32]) {
						index = commands[(index + 3) % genome_length];
					}else {
						index = commands[(index + 4) % genome_length];
					}
					break;
				case(35)://значение ячейки >= значение другой ячейки
					if (memory[commands[(index + 1) % genome_length] % 32] >= memory[commands[(index + 2) % genome_length] % 32]) {
						index = commands[(index + 3) % genome_length];
					}else {
						index = commands[(index + 4) % genome_length];
					}
					break;
				case(36)://значение ячейки == значение другой ячейки
					if (memory[commands[(index + 1) % genome_length] % 32] <= memory[commands[(index + 2) % genome_length] % 32]) {
						index = commands[(index + 3) % genome_length];
					}else {
						index = commands[(index + 4) % genome_length];
					}
					break;
				case(37)://записать параметр в ячейку
					memory[commands[(index + 1) % genome_length] % 16 + 16] = commands[(index + 2) % genome_length];
					index += 3;
					index %= genome_length;
					break;
				case(38)://mov
					memory[commands[(index + 1) % genome_length] % 16 + 16] = memory[commands[(index + 2) % genome_length] % 32];
					index += 3;
					index %= genome_length;
					break;
				case(39)://прибавить параметр к ячейке и записать в другую ячейку
					memory[commands[(index + 1) % genome_length] % 16 + 16] = memory[commands[(index + 2) % genome_length] % 32] + commands[(index + 3) % genome_length];
					memory[commands[(index + 1) % genome_length] % 16 + 16] %= 256;
					index += 4;
					index %= genome_length;
					break;
				case(40)://вычесть ячейку из параметра и записать в другую ячейку
					memory[commands[(index + 1) % genome_length] % 16 + 16] = memory[commands[(index + 2) % genome_length] % 32] - commands[(index + 3) % genome_length];
					memory[commands[(index + 1) % genome_length] % 16 + 16] = Math.floorMod(memory[commands[(index + 1) % genome_length] % 16 + 16], 256);
					index += 4;
					index %= genome_length;
					break;
				case(41)://умножить ячейку на параметр и записать в другую ячейку
					memory[commands[(index + 1) % genome_length] % 16 + 16] = memory[commands[(index + 2) % genome_length] % 32] * commands[(index + 3) % genome_length];
					memory[commands[(index + 1) % genome_length] % 16 + 16] %= 256;
					index += 4;
					index %= genome_length;
					break;
				case(42)://разделить ячейку на параметр и записать в другую ячейку
					if (commands[(index + 3) % genome_length] != 0) {
						memory[commands[(index + 1) % genome_length] % 16 + 16] = memory[commands[(index + 2) % genome_length] % 32] / commands[(index + 3) % genome_length];
						memory[commands[(index + 1) % genome_length] % 16 + 16] %= 256;
					}else {
						memory[commands[(index + 1) % genome_length] % 16 + 16] = 0;
					}
					index += 4;
					index %= genome_length;
					break;
				case(43)://остаток от деления ячейки на параметр записать в другую ячейку
					if (commands[(index + 3) % genome_length] != 0) {
						memory[commands[(index + 1) % genome_length] % 16 + 16] = memory[commands[(index + 2) % genome_length] % 32] % commands[(index + 3) % genome_length];
						memory[commands[(index + 1) % genome_length] % 16 + 16] %= 256;
					}
					index += 4;
					index %= genome_length;
					break;
				case(44)://прибавить ячейку 2 к ячейке 3 и записать в ячейку 1
					memory[commands[(index + 1) % genome_length] % 16 + 16] = memory[commands[(index + 2) % genome_length] % 32] + memory[commands[(index + 3) % genome_length] % 32];
					memory[commands[(index + 1) % genome_length] % 16 + 16] %= 256;
					index += 4;
					index %= genome_length;
					break;
				case(45)://вычесть ячейку 2 из ячейки 3 и записать в ячейку 1
					memory[commands[(index + 1) % genome_length] % 16 + 16] = memory[commands[(index + 2) % genome_length] % 32] - memory[commands[(index + 3) % genome_length] % 32];
					memory[commands[(index + 1) % genome_length] % 16 + 16] = Math.floorMod(memory[commands[(index + 1) % genome_length] % 16 + 16], 256);
					index += 4;
					index %= genome_length;
					break;
				default:
					index = commands[(index + 1) % genome_length];
					break;
			}
			if (ret) {
				break;
			}
		}
	}
	public int get_param_value(int param) {
		if (param >= 224) {
			return(memory[param - 224]);
		}else {
			return((int)(param * 1.0 / 223 * 255));
		}
	}
	public void update_memory() {
		memory[0] = (int)(energy / Constant.max_energy * 255);    //энергия
		memory[1] = (int)(minerals / 1000.0 * 255);               //минералы
		memory[2] = (int)(age * 1.0 / Constant.max_age * 255);    //возраст
		memory[3] = (int)(xpos * 1.0 / Constant.W * 255);         //xpos
		memory[4] = (int)(ypos * 1.0 / Constant.H * 255);         //ypos
		memory[5] = rotate * 36;                                  //направление
		memory[6] = index;                                        //index
	}
	public void photosynthesis() {
		if (Constant.photo_list[sector(Constant.photo_list.length)] > 0) {
			if (Constant.enable_divers) {
				boolean p = false;
				int sec = divers_sector();
				if (sec == 0) {
					p = true;
				}else if (sec == 1 && rand.nextInt(0, 1000) < Constant.no_pht_in_sea) {
					p = true;
				}else if (sec == 2 && rand.nextInt(0, 1000) < Constant.no_pht_in_mud) {
					p = true;
				}
				if (p) {
					energy += Constant.photo_list[sector(Constant.photo_list.length)];
					go_color(Constant.pht_color);
				}
			}else {
				energy += Constant.photo_list[sector(Constant.photo_list.length)];
				go_color(Constant.pht_color);
			}
		}
	}
	public int push(int rot) {
		int[] pos = get_rotate_position(rot);
		if (pos[1] >= 0 & pos[1] < Constant.H) {
			if (map[pos[0]][pos[1]] != null) {
				if (map[pos[0]][pos[1]].move(rot) == 1) {
					return(1);
				}
			}
		}
		return(0);
	}
	public int see(int rot) {
		int[] pos = get_rotate_position(rot);
		if (pos[1] >= 0 & pos[1] < Constant.H) {
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
		if (pos[1] >= 0 & pos[1] < Constant.H) {
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
		if (pos[1] >= 0 & pos[1] < Constant.H) {
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
		if (pos[1] >= 0 & pos[1] < Constant.H) {
			if (map[pos[0]][pos[1]] != null) {
				Bot victim = map[pos[0]][pos[1]];
				if (victim != null) {
					victim.killed = 1;
					energy += victim.energy;
					map[pos[0]][pos[1]] = null;
					go_color(Constant.atk_color);
				}
			}
		}
	}
	public void attack2(int rot, double strength) {
		int[] pos = get_rotate_position(rot);
		if (pos[1] >= 0 & pos[1] < Constant.H) {
			if (map[pos[0]][pos[1]] != null) {
				Bot victim = map[pos[0]][pos[1]];
				if (victim != null) {
					if (victim.energy >= strength) {
						energy += strength;
						victim.energy -= strength;
					}else {
						energy += victim.energy;
						victim.energy = 0;
						victim.killed = 1;
						map[pos[0]][pos[1]] = null;
					}
					go_color(Constant.atk_color);
				}
			}
		}
	}
	public int move(int rot) {
		if (Constant.enable_divers && rot != 2 && rot != 6) {
			move_y_timer = 0;
		}
		int[] pos = get_rotate_position(rot);
		if (pos[1] >= 0 & pos[1] < Constant.H) {
			if (map[pos[0]][pos[1]] == null) {
				Bot self = map[xpos][ypos];
				map[xpos][ypos] = null;
				xpos = pos[0];
				ypos = pos[1];
				map[xpos][ypos] = self;
				return(1);
			}
		}
		return(0);
	}
	public int multiply(int rot, ListIterator<Bot> iterator) {
		if (Constant.enable_divers) {
			int sec = divers_sector();
			if (sec == 0) {
				if (!(rand.nextInt(0, 1000) < Constant.no_multiply_in_surface)) {
					return(0);
				}
			}else if (sec == 1) {
				if (!(rand.nextInt(0, 1000) < Constant.no_multiply_in_sea)) {
					return(0);
				}
			}else {
				if (!on_land && !(rand.nextInt(0, 1000) < Constant.on_land)) {
					return(0);
				}
			}
		}
		int[] pos = get_rotate_position(rot);
		if (pos[1] >= 0 & pos[1] < Constant.H) {
			if (map[pos[0]][pos[1]] == null) {
				energy -= Constant.energy_for_multiply;
				if (energy <= 0) {
					killed = 1;
					map[xpos][ypos] = null;
				}else { 
					Color new_color = color;
					int new_genr = generation + 1;
					int new_mut = mutations;
					int[] new_brain = new int[genome_length];
					int[] new_memory = new int[32];
					for (int i = 0; i < genome_length; i++) {
						new_brain[i] = commands[i];
					}
					for (int i = 0; i < 32; i++) {
						new_memory[i] = memory[i];
					}
					if (rand.nextInt(100) < Constant.child_mutation_chance) {//мутация
						new_color = new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
						new_brain[rand.nextInt(64)] = rand.nextInt(64);
						new_mut++;
					}
					if (rand.nextInt(100) < Constant.parent_mutation_chance) {//мутация родителя
						color = new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
						commands[rand.nextInt(genome_length)] = rand.nextInt(genome_length);
					}
					if (Constant.upd_parent_index) index = 0;
					if (Constant.upd_parent_age) age = Constant.max_age;
					Bot new_bot = new Bot(pos[0], pos[1], new_color, energy / 2, world);
					new_bot.minerals = minerals / 2;
					energy /= 2;
					minerals /= 2;
					new_bot.generation = new_genr;
					new_bot.mutations = new_mut;
					new_bot.commands = new_brain;
					new_bot.memory = new_memory;
					map[pos[0]][pos[1]] = new_bot;
					iterator.add(new_bot);
				}
			}
		}
		return(0);
	}
	public void go_color(Color c) {//цвет режима отрисовки хищников
		int red_count = 0;         //есть буфер на 10 цветов. При выполнении функции цвет "с" добавляется в начало буфера, сдвигая остальные. Самый поздний исчезает
		int green_count = 0;       //Для получения цвета просто усредняем буфер(кроме тех, где стоит -1. Это начальное значение, обозначающее, что цвета там нет)
		int blue_count = 0;        //бот с цветом(-1, -1, -1), рисуется серым.
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
	//
	public int divers_sector() {
		if (ypos < Constant.H - Constant.sea_level) {
			return(0);//land
		}else if (ypos < Constant.H - Constant.mud_level) {
			return(1);//sea
		}else {
			return(2);//mud
		}
	}
}
