package sct;

import java.awt.Color;

public class Constant {
	static int display_W = 1920;
	static int display_H = 1080;
	//
	static int scr_W = 1920;
	static int scr_H = 1080;
	static int bot_scale = 10;
	static int W = (scr_W - 300) / bot_scale;
	static int H = scr_H / bot_scale;
	static int starting_bots = W*H/17;
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
	static int max_age = 1500;
	static boolean upd_parent_index = false;
	static boolean upd_parent_age = false;
	static int[] minerals_list = {0, 0, 0, 0, 0, 1, 2, 3};
	static int[] photo_list = {10, 8, 6, 5, 4, 3, 0, 0};
	
	//
	static boolean draw_rotate = false;
	static boolean draw_frame = true;
	//
	public static Color gradient(Color color1, Color color2, double grad) {
		int r = Math.min(Math.max((int)(color1.getRed() * (1 - grad) + color2.getRed() * grad), 0), 255);
		int g = Math.min(Math.max((int)(color1.getGreen() * (1 - grad) + color2.getGreen() * grad), 0), 255);
		int b = Math.min(Math.max((int)(color1.getBlue() * (1 - grad) + color2.getBlue() * grad), 0), 255);
		return(new Color(r, g, b));
	}
}
