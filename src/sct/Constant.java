package sct;

import java.awt.Color;

public class Constant {
	static int display_W = 1920;
	static int display_H = 1080;
	//
	static int scr_W = 1920;
	static int scr_H = 1080;
	static int bot_scale = 2;
	static int W = (scr_W - 300) / bot_scale;
	static int H = scr_H / bot_scale;
	static int starting_bots = W*H/25;//17
	static int starting_mul = 4;
	//
	static double energy_for_life = 1;
	static double energy_for_multiply = 150;
	static double energy_for_auto_multiply = 800;
	static double max_energy = 1000;
	static int child_mutation_chance = 25;
	static int parent_mutation_chance = 0;
	static int full_color_change_chance = 1;
	static boolean full_color_change_without_mul = false;
	static boolean color_change_without_mul = false;//true - change, false - not change
	static boolean full_color_change_with_mul = true;
	static boolean color_change_with_mul = false;//true - change, false - not change
	static int color_range = 12;
	static boolean allow_organics = true;
	static int org_fall_type = 1;//0 - не падает, 1 - падает, но останавливается, 2 - всегда падает, 3 - всегда падает и сыпется
	static int max_age = 5000;
	static boolean upd_parent_index = false;
	static boolean upd_parent_age = false;
	static int[] minerals_list = {0, 0, 0, 0, 0, 1, 2, 3};
	static int[] photo_list = {10, 8, 6, 5, 4, 3, 0, 0};
	//
	static boolean draw_rotate = false;
	static boolean draw_frame = false;
	//
	//adaptation
	//divers
	static boolean enable_divers = false;
	static int sea_level = 0;
	static int mud_level = 0;
	static boolean block_auto_multiply = false;
	static int no_multiply_in_surface = 0;
	static int no_multiply_in_sea = 0;
	static int no_pht_in_sea = 0;
	static int no_pht_in_mud = 0;
	static int on_land = 0;
	static int force_move_y = 0;
	//
	//полезные функции
	//
	public static int[][] movelist = {
		{0, -1},
		{1, -1},
		{1, 0},
		{1, 1},
		{0, 1},
		{-1, 1},
		{-1, 0},
		{-1, -1}
	};
	//
	public static Color gradient(Color color1, Color color2, double grad) {
		grad = Math.min(Math.max(grad, 0), 1);
		int r = Math.min(Math.max((int)(color1.getRed() * (1 - grad) + color2.getRed() * grad), 0), 255);
		int g = Math.min(Math.max((int)(color1.getGreen() * (1 - grad) + color2.getGreen() * grad), 0), 255);
		int b = Math.min(Math.max((int)(color1.getBlue() * (1 - grad) + color2.getBlue() * grad), 0), 255);
		return(new Color(r, g, b));
	}
	//
	public static int[] get_rotate_position(int rot, int[] sp){
		int[] pos = new int[2];
		pos[0] = sp[0] + Constant.movelist[rot][0];
		pos[1] = sp[1] + Constant.movelist[rot][1];
		if (pos[0] < 0) {
			pos[0] = Constant.W - 1;
		}else if(pos[0] >= Constant.W) {
			pos[0] = 0;
		}
		return(pos);
	}
}
