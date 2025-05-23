package sct;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Random;
import javax.swing.*;

import java.awt.Font;
//
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import java.awt.Graphics2D;

public class World extends JPanel{
	ArrayList<Bot> objects;
	Timer timer;
	int delay = 10;
	int[] world_scale = {324, 216};
	Random rand = new Random();
	Bot[][] Map = new Bot[world_scale[0]][world_scale[1]];
	Color gray = new Color(100, 100, 100);
	Color green = new Color(0, 255, 0);
	Color red = new Color(255, 0, 0);
	Color black = new Color(0, 0, 0);
	int steps = 0;
	int draw_type = 0;
	int b_count = 0;
	int obj_count = 0;
	int org_count = 0;
	String txt;
	String txt2;
	int mouse = 0;
	int W = 1920;
	int H = 1080;
	JButton stop_button = new JButton("Stop");
	boolean pause = false;
	boolean render = true;
	Bot selection = null;
	int[] botpos = new int[2];
	int[] for_set;
	JButton save_button = new JButton("Save");
	JButton show_brain_button = new JButton("Show brain");
	JButton render_button = new JButton("Render: on");
	JButton record_button = new JButton("Record: off");
	JTextField for_save = new JTextField();
	JTextField for_load = new JTextField();
	boolean sh_brain = false;
	boolean rec = false;
	double[][][] ch = new double[10][world_scale[0]][world_scale[1]];
	int gas_draw_type = 0;
	JRadioButton[] bs = new JRadioButton[11];
	JRadioButton[] drbs = new JRadioButton[10];
	public World() {
		setLayout(null);
		timer = new Timer(delay, new BotListener());
		objects = new ArrayList<Bot>();
		setBackground(new Color(255, 255, 255));
		addMouseListener(new BotListener());
		addMouseMotionListener(new BotListener());
		stop_button.addActionListener(e -> start_stop());
		stop_button.setBounds(W - 300, 125, 250, 35);
        add(stop_button);
        JButton predators_button = new JButton("Predators");
        predators_button.addActionListener(e -> change_draw_type(0));
		predators_button.setBounds(W - 300, 190, 125, 20);
        add(predators_button);
        JButton energy_button = new JButton("Energy");
        energy_button.addActionListener(e -> change_draw_type(2));
		energy_button.setBounds(W - 170, 190, 125, 20);
        add(energy_button);
        JButton age_button = new JButton("Age");
        age_button.addActionListener(e -> change_draw_type(3));
		age_button.setBounds(W - 170, 215, 125, 20);
        add(age_button);
        JButton color_button = new JButton("Color");
        color_button.addActionListener(e -> change_draw_type(1));
		color_button.setBounds(W - 300, 215, 125, 20);
        add(color_button);
        JButton gas_button = new JButton("Gas");
        gas_button.addActionListener(e -> change_draw_type(4));
        gas_button.setBounds(W - 300, 240, 125, 20);
        add(gas_button);
        JButton select_button = new JButton("Select");
        select_button.addActionListener(e -> change_mouse(0));
		select_button.setBounds(W - 300, 455, 95, 20);
        add(select_button);
        JButton set_button = new JButton("Set");
        set_button.addActionListener(e -> change_mouse(1));
        set_button.setBounds(W - 200, 455, 95, 20);
        add(set_button);
        JButton remove_button = new JButton("Remove");
        remove_button.addActionListener(e -> change_mouse(2));
        remove_button.setBounds(W - 100, 455, 95, 20);
        add(remove_button);
        //
        save_button.addActionListener(e -> save_bot());
        save_button.setBounds(W - 300, 365, 125, 20);
        save_button.setEnabled(false);
        add(save_button);
        //
        show_brain_button.addActionListener(e -> show_brain());
        show_brain_button.setBounds(W - 170, 365, 125, 20);
        show_brain_button.setEnabled(false);
        add(show_brain_button);
        //
        for_save.setBounds(W - 300, 410, 250, 20);
        add(for_save);
        //
        for_load.setBounds(W - 300, 515, 250, 20);
        add(for_load);
        //
        JButton load_bot_button = new JButton("Load bot");
        load_bot_button.addActionListener(e -> load_bot());
        load_bot_button.setBounds(W - 300, 540, 90, 20);
        add(load_bot_button);
        //
        JButton load_world_button = new JButton("Load world");
        load_world_button.addActionListener(e -> load_world());
        load_world_button.setBounds(W - 205, 540, 90, 20);
        add(load_world_button);
        //
        JButton save_world_button = new JButton("Save world");
        save_world_button.addActionListener(e -> save_world());
        save_world_button.setBounds(W - 110, 540, 90, 20);
        add(save_world_button);
        //
        JButton new_population_button = new JButton("New population");
        new_population_button.addActionListener(e -> new_population());
        new_population_button.setBounds(W - 300, 590, 125, 20);
        add(new_population_button);
        //
        render_button.addActionListener(e -> rndr());
        render_button.setBounds(W - 300, 615, 125, 20);
        add(render_button);
        //
        record_button.addActionListener(e -> rcrd());
        record_button.setBounds(W - 170, 615, 125, 20);
        add(record_button);
        //
        JButton kill_button = new JButton("Kill all");
        kill_button.addActionListener(e -> kill_all());
        kill_button.setBounds(W - 170, 590, 125, 20);
        add(kill_button);
        //
        ButtonGroup group = new ButtonGroup();
        add_radio_button(W - 300, 660, group, "no render", 0, true, bs);
        add_radio_button(W - 300, 680, group, "A", 1, false, bs);
        add_radio_button(W - 300, 700, group, "B", 2, false, bs);
        add_radio_button(W - 300, 720, group, "C", 3, false, bs);
        add_radio_button(W - 300, 740, group, "D", 4, false, bs);
        add_radio_button(W - 300, 760, group, "E", 5, false, bs);
        add_radio_button(W - 300, 780, group, "F", 6, false, bs);
        add_radio_button(W - 300, 800, group, "G", 7, false, bs);
        add_radio_button(W - 300, 820, group, "H", 8, false, bs);
        add_radio_button(W - 300, 840, group, "I", 9, false, bs);
        add_radio_button(W - 300, 860, group, "J", 10, false, bs);
        //
        ButtonGroup group2 = new ButtonGroup();
        add_radio_button(W - 150, 680, group2, "A", 0, true, drbs);
        add_radio_button(W - 150, 700, group2, "B", 1, false, drbs);
        add_radio_button(W - 150, 720, group2, "C", 2, false, drbs);
        add_radio_button(W - 150, 740, group2, "D", 3, false, drbs);
        add_radio_button(W - 150, 760, group2, "E", 4, false, drbs);
        add_radio_button(W - 150, 780, group2, "F", 5, false, drbs);
        add_radio_button(W - 150, 800, group2, "G", 6, false, drbs);
        add_radio_button(W - 150, 820, group2, "H", 7, false, drbs);
        add_radio_button(W - 150, 840, group2, "I", 8, false, drbs);
        add_radio_button(W - 150, 860, group2, "J", 9, false, drbs);
        //
		kill_all();
		timer.start();
	}
	public void add_radio_button(int x, int y, ButtonGroup g, String name, int i, boolean selected, JRadioButton[] lst) {
		JRadioButton c = new JRadioButton(name, selected);
        c.setBounds(x, y, 100, 20);
		add(c);
		g.add(c);
		lst[i] = c;
	}
	public boolean find_map_pos(int[] pos, int state) {
		if (Map[pos[0]][pos[1]] != null) {
			if (Map[pos[0]][pos[1]].state == state) {
				return(true);
			}
		}
		return(false);
	}
	public void paintComponent(Graphics canvas) {
		super.paintComponent(canvas);
		if (render) {
			if (gas_draw_type > 0) {
				for (int x = 0; x < world_scale[0]; x++) {
					for (int y = 0; y < world_scale[1]; y++) {
						if (gas_draw_type == 1) {//глюкоза
							int gr = Math.max(0, 255 - (int)(ch[0][x][y] / 1000.0 * 255));
							canvas.setColor(new Color(gr, gr, gr));
							canvas.fillRect(x * 5, y * 5, 5, 5);
						}else if (gas_draw_type == 2) {//кристаллы
							canvas.setColor(Constant.gradient(new Color(255, 255, 255), new Color(85, 255, 255), Math.min(ch[1][x][y] / 1000, 1)));
							canvas.fillRect(x * 5, y * 5, 5, 5);
						}else if (gas_draw_type == 5) {//водород
							canvas.setColor(Constant.gradient(new Color(255, 255, 255), new Color(200, 176, 250), Math.min(ch[4][x][y] / 1000, 1)));
							canvas.fillRect(x * 5, y * 5, 5, 5);
						}
						if (ch[1][x][y] > 500) {
							canvas.setColor(Constant.gradient(new Color(255, 255, 255), new Color(85, 255, 255), Math.min(ch[1][x][y] / 1000, 1)));
							canvas.fillRect(x * 5, y * 5, 5, 5);
						}
					}
				}
			}
			for(Bot b: objects) {
				b.Draw(canvas, draw_type);
			}
		}
		canvas.setColor(gray);
		canvas.fillRect(W - 300, 0, 300, 1080);
		canvas.setColor(black);
		canvas.setFont(new Font("arial", Font.BOLD, 18));
		canvas.drawString("Main: ", W - 300, 20);
		canvas.drawString("version 1.9.1", W - 300, 40);
		canvas.drawString("steps: " + String.valueOf(steps), W - 300, 60);
		canvas.drawString("objects: " + String.valueOf(obj_count) + ", bots: " + String.valueOf(b_count), W - 300, 80);
		if (draw_type == 0) {
			txt = "predators view";
		}else if (draw_type == 1) {
			txt = "color view";
		}else if (draw_type == 2) {
			txt = "energy view";
		}else if (draw_type == 3) {
			txt = "age view";
		}else if (draw_type == 4){
			txt = "A view";
		}
		canvas.drawString("render type: " + txt, W - 300, 100);
		if (mouse == 0) {
			txt2 = "select";
		}else if (mouse == 1) {
			txt2 = "set";
		}else {
			txt2 = "remove";
		}
		canvas.drawString("mouse function: " + txt2, W - 300, 120);
		canvas.drawString("Render types:", W - 300, 180);
		canvas.drawString("Selection:", W - 300, 275);
		canvas.drawString("enter name:", W - 300, 405);
		canvas.drawString("Mouse functions:", W - 300, 445);
		canvas.drawString("Load:", W - 300, 490);
		canvas.drawString("enter name:", W - 300, 510);
		canvas.drawString("Controls:", W - 300, 580);
		canvas.drawString("Gas draw type:", W - 300, 655);
		if (selection != null) {
			canvas.drawString("energy: " + String.valueOf((int)selection.energy), W - 300, 295);
			canvas.drawString("age: " + String.valueOf(selection.age), W - 300, 315);
			canvas.drawString("position: " + "[" + String.valueOf(selection.xpos) + ", " + String.valueOf(selection.ypos) + "]", W - 300, 335);
			canvas.drawString("color: " + "(" + String.valueOf(selection.color.getRed()) + ", " + String.valueOf(selection.color.getGreen()) + ", " + String.valueOf(selection.color.getBlue()) + ")", W - 300, 355);
			canvas.setColor(new Color(0, 0, 0, 200));
			canvas.fillRect(0, 0, W - 300, 1080);
			canvas.setColor(new Color(255, 0, 0));
			canvas.fillRect(selection.xpos * 5, selection.ypos * 5, 5, 5);
		}else {
			canvas.drawString("none", W - 300, 295);
		}
		if (sh_brain) {
			canvas.setColor(new Color(90, 90, 90));
			canvas.fillRect(0, 0, 360, 360);
			canvas.setColor(new Color(128, 128, 128));
			for (int x = 0; x < 8; x++) {
				for (int y = 0; y < 8; y++) {
					canvas.setColor(new Color(128, 128, 128));
					canvas.fillRect(x * 45, y * 45, 40, 40);
					canvas.setColor(new Color(0, 0, 0));
					canvas.drawString(String.valueOf(selection.commands[x + y * 8]), x * 45 + 20, y * 45 + 20);
				}
			}
		}
	}
	public void kill_all() {
		steps = 0;
		objects = new ArrayList<Bot>();
		Map = new Bot[world_scale[0]][world_scale[1]];
		ch = new double[10][world_scale[0]][world_scale[1]];
		for (int x = 0; x < world_scale[0]; x++) {
			for (int y = 0; y < world_scale[1]; y++) {
				for (int i = 0; i < 10; i++) {
					ch[i][x][y] = Constant.start_count[i];
				}
			}
		}
	}
	public void new_population() {
		kill_all();
		for (int i = 0; i < 8000; i++) {
			while(true){
				int x = rand.nextInt(world_scale[0]);
				int y = rand.nextInt(world_scale[1]);
				if (Map[x][y] == null) {
					Bot new_bot = new Bot(
						x,
						y,
						new Color(rand.nextInt(256),rand.nextInt(256), rand.nextInt(256)),
						1000,
						Map,
						ch,
						objects
					);
					objects.add(new_bot);
					Map[x][y] = new_bot;
					break;
				}
			}
		}
		repaint();
	}
	private class BotListener extends MouseAdapter implements ActionListener{
		public void mousePressed(MouseEvent e) {
			update_mouse(e, true);
		}
		public void mouseDragged(MouseEvent e) {
			update_mouse(e, false);
		}
		public void actionPerformed(ActionEvent e) {
			for (int i = 0; i < 11; i++) {
				if (bs[i].isSelected()) {
					gas_draw_type = i;
				}
			}
			if (draw_type > 3) {
				for (int i = 0; i < 10; i++) {
					if (drbs[i].isSelected()) {
						draw_type = i + 4;
					}
				}
			}
			if (!pause) {
				if (rec && steps % 25 == 0) {
					record();
				}
				steps++;
				b_count = 0;
				obj_count = 0;
				org_count = 0;
				ListIterator<Bot> bot_iterator = objects.listIterator();
				while (bot_iterator.hasNext()) {
					Bot next_bot = bot_iterator.next();
					next_bot.Update(bot_iterator);
					if (selection != null) {
						if (next_bot.xpos == selection.xpos && next_bot.ypos == selection.ypos) {
							if (next_bot != selection) {
								selection = null;
								save_button.setEnabled(false);
								show_brain_button.setEnabled(false);
								sh_brain = false;
							}
						}
					}
					obj_count++;
					if (next_bot.state != 0) {
						org_count++;
					}else {
						b_count++;
					}
				}
				if (selection != null) {
					int[] pos = {selection.xpos, selection.ypos};
					if (selection.killed == 1 || !find_map_pos(pos, 0) || selection.state != 0){
						selection = null;
						save_button.setEnabled(false);
						show_brain_button.setEnabled(false);
						sh_brain = false;
					}
				}
				for (int i = 0; i < 10; i++) {
					if (Constant.viscosity[i] > 0) {
						gas(ch[i], i);
					}
				}
				crystal(ch[1]);
				hydrogenium(ch[4], 4);
			}
			ListIterator<Bot> iterator = objects.listIterator();
			while (iterator.hasNext()) {
				Bot next_bot = iterator.next();
				if (next_bot.killed == 1) {
					iterator.remove();
				}
			}
			repaint();
		}
	}
	public void record() {
		try {
			int last = draw_type;
			draw_type = 0;
			BufferedImage buff = new BufferedImage(1920, 1080, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2d = buff.createGraphics();
			g2d.setColor(Color.WHITE);
			g2d.fillRect(0, 0, 1920, 1080);
			for(Bot b: objects) {
				b.Draw(g2d, draw_type);
			}
			g2d.dispose();
			draw_type = 2;
			BufferedImage buff2 = new BufferedImage(1920, 1080, BufferedImage.TYPE_INT_RGB);
			g2d = buff2.createGraphics();
			g2d.setColor(Color.WHITE);
			g2d.fillRect(0, 0, 1920, 1080);
			for(Bot b: objects) {
				b.Draw(g2d, draw_type);
			}
			g2d.dispose();
			draw_type = 5;
			BufferedImage buff3 = new BufferedImage(1920, 1080, BufferedImage.TYPE_INT_RGB);
			g2d = buff3.createGraphics();
			g2d.setColor(Color.WHITE);
			g2d.fillRect(0, 0, 1920, 1080);
			for(Bot b: objects) {
				b.Draw(g2d, draw_type);
			}
			g2d.dispose();
			draw_type = last;
			ImageIO.write(buff, "png", new File("record/predators/screen" + String.valueOf(steps / 25)+ ".png"));
			ImageIO.write(buff2, "png", new File("record/energy/screen" + String.valueOf(steps / 25)+ ".png"));
			ImageIO.write(buff3, "png", new File("record/color/screen" + String.valueOf(steps / 25)+ ".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void gas(double[][] gas_map, int gas_type) {//распространение кислорода
		double[][] new_map = new double[world_scale[0]][world_scale[1]];
		for (int x = 0; x < world_scale[0]; x++) {
			for (int y = 0; y < world_scale[1]; y++) {
				if (gas_map[x][y] >= 0.009) {
					gas_map[x][y] -= gas_map[x][y] * Constant.evaporation[gas_type];//испарение
					double g = gas_map[x][y] * Constant.viscosity[gas_type];
					double ox = g / 9;
					new_map[x][y] += gas_map[x][y] - g + ox;
					int count = 0;
					for (int i = 0; i < 8; i++) {
						int[] f = {x, y};
						int[] pos = Constant.get_rotate_position(i, f);
						if (pos[1] >= 0 && pos[1] < world_scale[1] && Map[pos[0]][pos[1]] == null && ch[1][pos[0]][pos[1]] < 500) {
							new_map[pos[0]][pos[1]] += ox;
						}else {
							count++;
						}
					}
					for (int i = 0; i < count; i++) {
						new_map[x][y] += ox;
					}
				}else {
					new_map[x][y] += gas_map[x][y];
				}
			}
		}
		for (int x = 0; x < world_scale[0]; x++) {
			for (int y = 0; y < world_scale[1]; y++) {
				gas_map[x][y] = new_map[x][y];
				gas_map[x][y] += Math.max(100 - gas_map[x][y], 0) * (1 / 100);
			}
		}
	}
	public void hydrogenium(double[][] gas_map, int gas_type) {
		double[][] new_map = new double[world_scale[0]][world_scale[1]];
		for (int x = 0; x < world_scale[0]; x++) {
			for (int y = 0; y < world_scale[1]; y++) {
				int[] pos = Constant.get_rotate_position(0, new int[] {x, y});
				if (pos[1] >= 0 && pos[1] < world_scale[1] && Map[pos[0]][pos[1]] == null && ch[1][pos[0]][pos[1]] < 500 && ch[gas_type][x][y] - ch[gas_type][pos[0]][pos[1]] > ch[gas_type][x][y]/10 && ch[gas_type][pos[0]][pos[1]] < 1000) {
					new_map[pos[0]][pos[1]] += gas_map[x][y] * Constant.up[gas_type];
					new_map[x][y] += gas_map[x][y] * (1 - Constant.up[gas_type]);
				}else {
					new_map[x][y] += gas_map[x][y];
				}
			}
		}
		for (int x = 0; x < world_scale[0]; x++) {
			for (int y = 0; y < world_scale[1]; y++) {
				gas_map[x][y] = new_map[x][y];
				//if (y > 135) {
				//	gas_map[x][y] += 200;
				//}
			}
		}
		gas_map[100][215] += Constant.hydro_pl;
		gas_map[200][215] += Constant.hydro_pl;
		gas_map[300][215] += Constant.hydro_pl;
	}
	public void crystal(double[][] crystal_map) {
		double[][] new_map = new double[world_scale[0]][world_scale[1]];
		for (int x = 0; x < world_scale[0]; x++) {
			for (int y = 0; y < world_scale[1]; y++) {
				new_map[x][y] = crystal_map[x][y];
			}
		}
		for (int i = 0; i < 1000; i++) {
			int x = rand.nextInt(324);
			int y = rand.nextInt(81) + 135;
			if (crystal_map[x][y] > 10 || Map[x][y] != null && rand.nextInt(3000) == 0) {
				new_map[x][y] += rand.nextInt(10, 30);
			}else {
				int count = 0;
				for (int j = 0; j < 8; j++) {
					int[] f = {x, y};
					int[] pos = Constant.get_rotate_position(j, f);
					if (pos[1] >= 0 && pos[1] < world_scale[1] && (crystal_map[pos[0]][pos[1]] > 10)) {
						count++;
					}
				}
				if (rand.nextInt(1000) < Constant.crystal_chances[count]) {
					new_map[x][y] += rand.nextInt(10, 15);
				}
			}
		}
		for (int x = 0; x < world_scale[0]; x++) {
			for (int y = 0; y < world_scale[1]; y++) {
				//crystal_map[x][y] = new_map[x][y];
			}
		}
	}
	public void update_mouse(MouseEvent e, boolean do_select) {
		if (e.getX() < W - 300) {
			botpos[0] = e.getX() / 5;
			botpos[1] = e.getY() / 5;
			if (mouse == 0) {//select
				if (do_select) {
					if (find_map_pos(botpos, 0)) {
						Bot b = Map[botpos[0]][botpos[1]];
						selection = b;
						save_button.setEnabled(true);
						show_brain_button.setEnabled(true);
					}else {
						selection = null;
						save_button.setEnabled(false);
						show_brain_button.setEnabled(false);
						sh_brain = false;
					}
				}
			}else if (mouse == 1) {//set
				if (for_set != null) {
					if (Map[botpos[0]][botpos[1]] == null) {
						if (for_set != null) {
							Bot new_bot = new Bot(botpos[0], botpos[1], new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)), 1000, Map, ch, objects);
							for (int i = 0; i < 64; i++) {
								new_bot.commands[i] = for_set[i];
							}
							objects.add(new_bot);
							Map[botpos[0]][botpos[1]] = new_bot;
						}
					}
				}
			}else {//remove
				if (Map[botpos[0]][botpos[1]] != null) {
					Bot b = Map[botpos[0]][botpos[1]];
					b.energy = 0;
					b.killed = 1;
					Map[botpos[0]][botpos[1]] = null;
				}
			}
		}
	}
	public void change_draw_type(int num) {
		draw_type = num;
	}
	public void start_stop() {
		pause = !pause;
		if (pause) {
			stop_button.setText("Start");
		}else {
			stop_button.setText("Stop");
		}
	}
	public void change_mouse(int num) {
		mouse = num;
	}
	public void rndr() {
		render = !render;
		if (render) {
			render_button.setText("Render: on");
		}else {
			render_button.setText("Render: off");
		}
	}
	public void rcrd() {
		rec = !rec;
		if (rec) {
			record_button.setText("Record: on");
		}else {
			record_button.setText("Record: off");
		}
	}
	public void show_brain() {
		sh_brain = !sh_brain;
		if (pause == false) {
			pause = true;
		}else if (sh_brain == false) {
			pause = false;
		}
	}
	public void save_bot() {
		String txt = "";
		for (int i = 0; i < 64; i++) {
			txt += String.valueOf(selection.commands[i]) + " ";
		}
		try {
	        FileWriter fileWriter = new FileWriter("saved objects/" + for_save.getText() + ".dat");
	        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
	        bufferedWriter.write(txt);
	        bufferedWriter.close();
	    } catch (IOException ex) {
	        System.out.println("Ошибка при записи в файл");
	        ex.printStackTrace();
	    }
	}
	public void load_bot() {
		try {
	        FileReader fileReader = new FileReader("saved objects/" + for_load.getText() + ".dat");
	        BufferedReader bufferedReader = new BufferedReader(fileReader);
	        String line = bufferedReader.readLine();
	        bufferedReader.close();
	        String[] l = line.split(" ");
	        for_set = new int[64];
	        for (int i = 0; i < 64; i++) {
	        	for_set[i] = Integer.parseInt(l[i]);
	        }
	    } catch (IOException ex) {
	        System.out.println("Ошибка при чтении файла");
	        ex.printStackTrace();
	    }
	}
	public void save_world() {
		try {
			FileWriter fileWriter = new FileWriter("saved worlds/" + for_load.getText() + ".dat");
		    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write(String.valueOf(steps) + ";");
			for(Bot b: objects) {//bot length - 78
				bufferedWriter.write(String.valueOf(b.energy) + ":");//0
				bufferedWriter.write(String.valueOf(b.age) + ":");//1
				bufferedWriter.write(String.valueOf(b.minerals) + ":");//2
				bufferedWriter.write(String.valueOf(b.xpos) + ":");//3
				bufferedWriter.write(String.valueOf(b.ypos) + ":");//4
				bufferedWriter.write(String.valueOf(b.rotate) + ":");//5
				bufferedWriter.write(String.valueOf(b.state) + ":");//6
				bufferedWriter.write(String.valueOf(b.c_red) + ":");//7
				bufferedWriter.write(String.valueOf(b.c_green) + ":");//8
				bufferedWriter.write(String.valueOf(b.c_blue) + ":");//9
				bufferedWriter.write(String.valueOf(b.color.getRed()) + ":");//10
				bufferedWriter.write(String.valueOf(b.color.getGreen()) + ":");//11
				bufferedWriter.write(String.valueOf(b.color.getBlue()) + ":");//12
				bufferedWriter.write(String.valueOf(b.index) + ":");//13
				for (int i = 0; i < 64; i++) {//14 - 77
					bufferedWriter.write(String.valueOf(b.commands[i]) + ":");
				}
				bufferedWriter.write(";");
			}
	        bufferedWriter.close();
	    } catch (IOException ex) {
	        System.out.println("Ошибка при записи в файл");
	        ex.printStackTrace();
	    }
	}
	public void load_world() {
		try {
	        FileReader fileReader = new FileReader("saved worlds/" + for_load.getText() + ".dat");
	        BufferedReader bufferedReader = new BufferedReader(fileReader);
	        String line = bufferedReader.readLine();
	        bufferedReader.close();
	        String[] l = line.split(";");
	        steps = Integer.parseInt(l[0]);
	        objects = new ArrayList<Bot>();
	        Map = new Bot[world_scale[0]][world_scale[1]];
	        ch = new double[10][world_scale[0]][world_scale[1]];
	    	for (int i = 1; i < l.length; i++) {
	    		String[] bot_data = l[i].split(":");
	    		Bot new_bot = new Bot(
	    			Integer.parseInt(bot_data[3]),
	    			Integer.parseInt(bot_data[4]),
	    			new Color(Integer.parseInt(bot_data[10]), Integer.parseInt(bot_data[11]), Integer.parseInt(bot_data[12])),
	    			Integer.parseInt(bot_data[0]),
	    			Map,
	    			ch,
	    			objects
	    		);
	    		new_bot.age = Integer.parseInt(bot_data[1]);
	    		new_bot.minerals = Integer.parseInt(bot_data[2]);
	    		new_bot.rotate = Integer.parseInt(bot_data[5]);
	    		new_bot.state = Integer.parseInt(bot_data[6]);
	    		new_bot.c_red = Integer.parseInt(bot_data[7]);
	    		new_bot.c_green = Integer.parseInt(bot_data[8]);
	    		new_bot.c_blue = Integer.parseInt(bot_data[9]);
	    		new_bot.index = Integer.parseInt(bot_data[13]);
	    		for (int j = 0; j < 64; j++) {
	    			new_bot.commands[j] = Integer.parseInt(bot_data[14 + j]);;
	    		}
	    		Map[Integer.parseInt(bot_data[3])][Integer.parseInt(bot_data[4])] = new_bot;
	    		objects.add(new_bot);
	    	}
	    } catch (IOException ex) {
	        System.out.println("Ошибка при чтении файла");
	        ex.printStackTrace();
	    }
	}
}
