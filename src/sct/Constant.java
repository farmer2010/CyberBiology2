package sct;

public class Constant {
	public static int size = 2;
	public static int[] world_scale = {500, 500};
	public static int starting_bot_count = 45000;
	public static double starting_ox = 0.02;
	public static double starting_org = 200;
	//
	public static double org_recycle_ox_coeff = 0.00005;
	public static double life_ox_coeff = 0.002;
	public static double move_ox_coeff = 0.003;
	public static double attack_ox_coeff = 0.01;
	public static double pht_ox_coeff = 0.003;
	//
	public static int org_die_level = 800;
	public static int age_minus_coeff = 10;
	public static int pht_coeff = 400;
	public static int energy_for_life = 1;
	public static int energy_for_move = 1;
	public static int[] pht_energy_list = {10, 9, 8, 7, 6, 5, 4, 3};
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
	public static int border(int number, int border1, int border2) {
		if (number > border1) {
			number = border1;
		}else if (number < border2) {
			number = border2;
		}
		return(number);
	}
}
