package sct;

import java.awt.Color;

public class Constant {
	//Вещества и их свойства
	//A - органика/глюкоза. После смерти бота его энергия превращается в глюкозу.
	//
	//B - кристаллы
	//
	public static double[] start_count = new double[] {200, 0, 200, 200, 50, 0, 0, 0, 0, 0};
	public static double[] viscosity = new double[] {0.01, 0, 1, 1, 0.5, 0, 0, 0, 0, 0};
	public static double[] evaporation = new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	public static int[] crystal_chances = new int[] {0, 10, 7, 6, 5, 4, 3, 2, 1};
	public static double[] up = new double[] {0, 0, 0, 0, 0.5, 0, 0, 0, 0, 0};
	public static double[] collect_speed = new double[] {0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1};
	//
	//Реакции
	//
	//массив реакций: [тип реакции][трата, производство][параметры реакции][вещества]
	public static double[][][][] reactions = new double[][][][] {//типы: 
		//0 - свет
		//1 - энергия
		//2 - тепло
		//3 - глюкоза
		//4 - кристалл
		//5 - кислород
		//6 - углекислый газ
		//7 - водород
		//8 - вольфрам
		//9 - катализатор
		//10 - торий
		//11 - яд
		//12 - железо
		//
		//массив: 0 - количество, 1 - тип, 2 - минимальное значение коефф скорости, 3 - максимальное количество катализатора
		new double[][][] {//фотосинтез
			new double[][] {new double[] {0.2}, new double[] {7}},//тратим
			new double[][] {new double[] {1, 5}, new double[] {3, 5}},//производим
			new double[][] {new double[] {1}, new double[] {0}, new double[] {1}, new double[] {100}},//катализаторы
		},
		new double[][][] {//гликолиз
			new double[][] {new double[] {5}, new double[] {3}},//тратим
			new double[][] {new double[] {4}, new double[] {1}},//производим
			new double[][] {new double[] {}, new double[] {}, new double[] {}, new double[] {}},//катализаторы
		},
	};
	public static Color[] reactions_color = new Color[]{
		new Color(0, 255, 0),
		new Color(255, 255, 0),
	};
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
