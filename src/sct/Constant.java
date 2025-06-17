package sct;

import java.awt.Color;

public class Constant {
	public static double temp = 30;
	//Вещества и их свойства
	//A - органика/глюкоза. После смерти бота его энергия превращается в глюкозу.
	//
	//B - кристаллы
	//
	public static double[] start_count = new double[] {200, 0, 200, 200, 50, 0, 0, 0, 0, 0};
	public static double[] viscosity = new double[] {0.01, 0, 1, 1, 0.5, 0, 0, 0, 0, 0};
	public static double[] evaporation = new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	public static double[] up = new double[] {0, 0, 0, 0, 0.3, 0, 0, 0, 0, 0};
	public static double[] collect_speed = new double[] {0.1, 0, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1};
	public static int[] crystal_chances = new int[] {0, 10, 7, 6, 5, 4, 3, 2, 1};
	//
	public static int hydro_pl = 100;//приход водорода
	//
	//Реакции
	//
	//массив реакций: [тип реакции][трата, производство][параметры реакции][вещества]
	public static double[][][][] reactions = new double[][][][] {//типы:
		//0 - свет
		//1 - энергия
		//2 - тепло
		//3(A) - глюкоза
		//4(B) - кристалл
		//5(C) - кислород
		//6(D) - углекислый газ
		//7(E) - водород
		//8(F) - вольфрам
		//9(G) - катализатор
		//10(H) - торий
		//11(I) - яд
		//12(J) - железо
		//
		//массив: 0 - количество, 1 - тип
		//для катализаторов: 0 - ускорение за единицу катализатора, 1 - тип, 2 - минимальное значение коефф скорости, 3 - максимальное количество катализатора
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
		new double[][][] {//переработка кристалла
			new double[][] {new double[] {1, 0.1}, new double[] {4, 7}},//тратим
			new double[][] {new double[] {1, 1, 1, 2}, new double[] {6, 8, 9, 1}},//производим
			new double[][] {new double[] {}, new double[] {}, new double[] {}, new double[] {}},//катализаторы
		},
		new double[][][] {//синтез тория
			new double[][] {new double[] {1, 1, 1}, new double[] {9, 7, 6}},//тратим
			new double[][] {new double[] {1, 1}, new double[] {10, 1}},//производим
			new double[][] {new double[] {}, new double[] {}, new double[] {}, new double[] {}},//катализаторы
		},
		new double[][][] {//окисление водорода
			new double[][] {new double[] {5, 5}, new double[] {5, 7}},//тратим
			new double[][] {new double[] {3}, new double[] {1}},//производим
			new double[][] {new double[] {}, new double[] {}, new double[] {}, new double[] {}},//катализаторы
		},
	};
	public static Color[] reactions_color = new Color[]{
		new Color(0, 255, 0),
		new Color(255, 255, 0),
		new Color(87, 173, 233),
		new Color(251, 117, 255),
		new Color(0, 0, 255),
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
			pos[0] = 539;
		}else if(pos[0] >= 540) {
			pos[0] = 0;
		}
		return(pos);
	}
	//
	public static Color gradient(Color color1, Color color2, double grad) {
		int r = Math.min(Math.max((int)(color1.getRed() * (1 - grad) + color2.getRed() * grad), 0), 255);
		int g = Math.min(Math.max((int)(color1.getGreen() * (1 - grad) + color2.getGreen() * grad), 0), 255);
		int b = Math.min(Math.max((int)(color1.getBlue() * (1 - grad) + color2.getBlue() * grad), 0), 255);
		return(new Color(r, g, b));
	}
}
