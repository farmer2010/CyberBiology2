package sct;

import java.awt.Color;

public class Constant {
	//Вещества и их свойства
	//A - органика/глюкоза. Выбрасывается в квадрате 3*3 после смерти(энергия бота)
	//
	//B - кристаллы
	//
	public static double[] start_count = new double[] {200, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	public static double[] viscosity = new double[] {0.01, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	public static double[] evaporation = new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	public static int[] crystal_chances = new int[] {0, 10, 7, 6, 5, 4, 3, 2, 1};
	//
	//Тех. функции
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
	public static int[] get_rotate_position(int rot, int[] sp){
		int[] pos = new int[2];
		pos[0] = sp[0] + Constant.movelist[rot][0];
		pos[1] = sp[1] + Constant.movelist[rot][1];
		if (pos[0] < 0) {
			pos[0] = 323;
		}else if(pos[0] >= 324) {
			pos[0] = 0;
		}
		return(pos);
	}
	//
	public static Color gradient(Color color1, Color color2, double grad) {
		int r = (int)(color1.getRed() * (1 - grad) + color2.getRed() * grad);
		int g = (int)(color1.getGreen() * (1 - grad) + color2.getGreen() * grad);
		int b = (int)(color1.getBlue() * (1 - grad) + color2.getBlue() * grad);
		return(new Color(r, g, b));
	}
}
