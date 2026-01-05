package sct;

public class Constant {
	static int display_W = 1920;
	static int display_H = 1080;
	//
	static int W = 162;
	static int H = 108;
	static int starting_bots = 1000;
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
	static double max_age = 1000;
	//
	static boolean draw_rotate = true;
}
