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
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import java.awt.Graphics2D;

public class World extends JPanel{
	ArrayList<Bot> objects;
	Timer timer;
	int delay = 10;
	Random rand = new Random();
	Bot[][] Map = new Bot[Constant.world_scale[0]][Constant.world_scale[1]];
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
	int[] botpos = new int [2];
	Bot for_set = null;
	JButton save_button = new JButton("Save");
	JButton show_brain_button = new JButton("Show brain");
	JButton render_button = new JButton("Render: on");
	JButton record_button = new JButton("Record: off");
	JSlider skip_slider = new JSlider(1, 10, 2);
	boolean sh_brain = false;
	boolean rec = false;
	double[][] oxygen_map;
	double[][] org_map;
	double[][] mnr_map = new double[Constant.world_scale[0]][Constant.world_scale[1]];
	double count_ox = -1;
	double count_org = -1;
	double count_mnr = -1;
	int gas_draw_type = 0;
	public World() {
		setLayout(null);
		timer = new Timer(delay, new BotListener());
		objects = new ArrayList<Bot>();
		setBackground(new Color(255, 255, 255));
		addMouseListener(new BotListener());
		addMouseMotionListener(new BotListener());
		stop_button.addActionListener(new start_stop());
		stop_button.setBounds(W - 300, 125, 125, 35);
        add(stop_button);
        skip_slider.setBounds(W - 170, 125, 125, 35);
        skip_slider.setPaintLabels(true);
        skip_slider.setMajorTickSpacing(1);
        add(skip_slider);
        //
        JButton predators_button = new JButton("Predators");
        predators_button.addActionListener(new change_draw_type(0));
		predators_button.setBounds(W - 300, 190, 95, 20);
        add(predators_button);
        JButton energy_button = new JButton("Energy");
        energy_button.addActionListener(new change_draw_type(2));
		energy_button.setBounds(W - 200, 190, 95, 20);
        add(energy_button);
        JButton clan_button = new JButton("Clans");
		clan_button.setBounds(W - 200, 215, 95, 20);
		clan_button.addActionListener(new change_draw_type(3));
        add(clan_button);
        JButton age_button = new JButton("Age");
        age_button.addActionListener(new change_draw_type(4));
		age_button.setBounds(W - 100, 190, 95, 20);
        add(age_button);
        JButton color_button = new JButton("Color");
        color_button.addActionListener(new change_draw_type(1));
		color_button.setBounds(W - 300, 215, 95, 20);
        add(color_button);
        //
        JButton none_button = new JButton("None");
        none_button.addActionListener(new change_gas_draw_type(0));
        none_button.setBounds(W - 300, 280, 95, 20);
        add(none_button);
        JButton ox_button = new JButton("Oxygen");
        ox_button.addActionListener(new change_gas_draw_type(1));
        ox_button.setBounds(W - 200, 280, 95, 20);
        add(ox_button);
        JButton org_button = new JButton("Organics");
        org_button.addActionListener(new change_gas_draw_type(2));
        org_button.setBounds(W - 100, 280, 95, 20);
        add(org_button);
        JButton mnr_button = new JButton("Minerals");
        mnr_button.addActionListener(new change_gas_draw_type(3));
        mnr_button.setBounds(W - 300, 305, 95, 20);
        add(mnr_button);
        //
        JButton select_button = new JButton("Select");
        select_button.addActionListener(new select());
		select_button.setBounds(W - 300, 505, 95, 20);
        add(select_button);
        JButton set_button = new JButton("Set");
        set_button.addActionListener(new set());
        set_button.setBounds(W - 200, 505, 95, 20);
        add(set_button);
        JButton remove_button = new JButton("Remove");
        remove_button.addActionListener(new remove());
        remove_button.setBounds(W - 100, 505, 95, 20);
        add(remove_button);
        //
        //save_button.addActionListener(new remove());
        save_button.setBounds(W - 300, 425, 125, 20);
        save_button.setEnabled(false);
        add(save_button);
        //
        show_brain_button.addActionListener(new shbr());
        show_brain_button.setBounds(W - 170, 425, 125, 20);
        show_brain_button.setEnabled(false);
        add(show_brain_button);
        JTextField for_save = new JTextField();
        for_save.setBounds(W - 300, 465, 250, 20);
        add(for_save);
        JTextField for_load = new JTextField();
        for_load.setBounds(W - 300, 565, 250, 20);
        add(for_load);
        JButton load_bot_button = new JButton("Load bot");
        //load_bot_button.addActionListener(new remove());
        load_bot_button.setBounds(W - 300, 590, 125, 20);
        add(load_bot_button);
        JButton load_world_button = new JButton("Load world");
        //load_world_button.addActionListener(new remove());
        load_world_button.setBounds(W - 170, 590, 125, 20);
        add(load_world_button);
        JButton new_population_button = new JButton("New population");
        new_population_button.addActionListener(new nwp());
        new_population_button.setBounds(W - 300, 635, 125, 20);
        add(new_population_button);
        //
        render_button.addActionListener(new rndr());
        render_button.setBounds(W - 300, 660, 125, 20);
        add(render_button);
        //
        record_button.addActionListener(new rcrd());
        record_button.setBounds(W - 170, 660, 125, 20);
        add(record_button);
        JButton kill_button = new JButton("Kill all");
        kill_button.addActionListener(new kill_all());
        kill_button.setBounds(W - 170, 635, 125, 20);
        add(kill_button);
		//newPopulation();
        
        oxygen_map = new double[Constant.world_scale[0]][Constant.world_scale[1]];
        org_map = new double[Constant.world_scale[0]][Constant.world_scale[1]];
		for (int x = 0; x < Constant.world_scale[0]; x++) {
			for (int y = 0; y < Constant.world_scale[1]; y++) {
				oxygen_map[x][y] = Constant.starting_ox;
				org_map[x][y] = Constant.starting_org;
				Map[x][y] = null;
			}
		}
        
		timer.start();
	}
	public void paintComponent(Graphics canvas) {
		super.paintComponent(canvas);
		canvas.setColor(gray);
		canvas.fillRect(0, 0, W, H);
		canvas.setColor(new Color(255, 255, 255));
		canvas.fillRect(0, 0, Constant.world_scale[0] * Constant.size, Constant.world_scale[1] * Constant.size);
		if (render) {
			if (gas_draw_type != 0) {
				for (int x = 0; x < Constant.world_scale[0]; x++) {
					for (int y = 0; y < Constant.world_scale[1]; y++) {
						if (gas_draw_type == 1) {
							double ox = oxygen_map[x][y];
							if (ox > 0.5) {
								ox = 0.5;
							}
							canvas.setColor(new Color(255 - (int)(ox * 255), 255 - (int)(ox * 255), 255));
							canvas.fillRect(x * Constant.size, y * Constant.size, Constant.size, Constant.size);
							if (org_map[x][y] >= 500) {
								canvas.setColor(new Color(90, 0, 0));
								canvas.fillRect(x * Constant.size, y * Constant.size, Constant.size, Constant.size);
							}
						}else if (gas_draw_type == 2){
							int gr = border(255 - (int)(org_map[x][y] / 500 * 255), 255, 0);
							canvas.setColor(new Color(gr, gr, gr));
							canvas.fillRect(x * Constant.size, y * Constant.size, Constant.size, Constant.size);
						}else if (gas_draw_type == 3) {
							canvas.setColor(new Color(255 - (int)(mnr_map[x][y] / 1000 * 255), 255 - (int)(mnr_map[x][y] / 1000 * 255), 255));
							canvas.fillRect(x * Constant.size, y * Constant.size, Constant.size, Constant.size);
						}
					}
				}
			}
			for(Bot b: objects) {
				b.Draw(canvas, draw_type);
			}
		}
		canvas.setColor(black);
		canvas.setFont(new Font("arial", Font.BOLD, 18));
		canvas.drawString("Main: ", W - 300, 20);
		canvas.drawString("version 1.9", W - 300, 40);
		canvas.drawString("steps: " + String.valueOf(steps), W - 300, 60);
		canvas.drawString("objects: " + String.valueOf(obj_count) + ", bots: " + String.valueOf(b_count), W - 300, 80);
		if (draw_type == 0) {
			txt = "predators view";
		}else if (draw_type == 1) {
			txt = "color view";
		}else if (draw_type == 2) {
			txt = "energy view";
		}else if (draw_type == 3) {
			txt = "clans view";
		}else if (draw_type == 4){
			txt = "age view";
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
		canvas.drawString("Background render types:", W - 300, 275);
		canvas.drawString("Selection:", W - 300, 340);
		canvas.drawString("enter name:", W - 300, 460);
		canvas.drawString("Mouse functions:", W - 300, 500);
		canvas.drawString("Load:", W - 300, 540);
		canvas.drawString("enter name:", W - 300, 560);
		canvas.drawString("Controls:", W - 300, 630);
		canvas.drawString("Oxygen: " + String.valueOf(count_ox), W - 300, 700);
		canvas.drawString("Organics: " + String.valueOf(count_org), W - 300, 720);
		canvas.drawString("Minerals: " + String.valueOf(count_mnr), W - 300, 740);
		if (selection != null) {
			canvas.drawString("energy: " + String.valueOf(selection.energy) + ", : " + String.valueOf(0), W - 300, 360);
			canvas.drawString("age: " + String.valueOf(selection.age), W - 300, 380);
			canvas.drawString("position: " + "[" + String.valueOf(selection.xpos) + ", " + String.valueOf(selection.ypos) + "]", W - 300, 400);
			canvas.drawString("color: " + "(" + String.valueOf(selection.color.getRed()) + ", " + String.valueOf(selection.color.getGreen()) + ", " + String.valueOf(selection.color.getBlue()) + ")", W - 300, 420);
			canvas.setColor(new Color(90, 90, 90, 90));
			canvas.fillRect(0, 0, W - 300, 1080);
			canvas.setColor(new Color(255, 0, 0));
			canvas.fillRect(selection.xpos * Constant.size, selection.ypos * Constant.size, Constant.size, Constant.size);
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
		if (rec && steps % 25 == 0) {
			try {
				BufferedImage buff = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);//хищники-кислород
				Graphics2D g2d = buff.createGraphics();
				g2d.setColor(Color.WHITE);
				g2d.fillRect(0, 0, 1000, 1000);
				for (int x = 0; x < Constant.world_scale[0]; x++) {
					for (int y = 0; y < Constant.world_scale[1]; y++) {
						double ox = oxygen_map[x][y];
						if (ox > 0.5) {
							ox = 0.5;
						}
						g2d.setColor(new Color(255 - (int)(ox * 255), 255 - (int)(ox * 255), 255));
						g2d.fillRect(x * Constant.size, y * Constant.size, Constant.size, Constant.size);
						if (org_map[x][y] >= Constant.org_die_level) {
							g2d.setColor(new Color(90, 0, 0));
							g2d.fillRect(x * Constant.size, y * Constant.size, Constant.size, Constant.size);
						}
					}
				}
				for(Bot b: objects) {
					b.Draw(g2d, 0);
				}
				g2d.dispose();
				//
				BufferedImage buff4 = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);//хищники-органика
				g2d = buff4.createGraphics();
				g2d.setColor(Color.WHITE);
				g2d.fillRect(0, 0, 1000, 1000);
				for (int x = 0; x < Constant.world_scale[0]; x++) {
					for (int y = 0; y < Constant.world_scale[1]; y++) {
						int gr = border(255 - (int)(org_map[x][y] / Constant.org_die_level * 255), 255, 0);
						g2d.setColor(new Color(gr, gr, gr));
						g2d.fillRect(x * Constant.size, y * Constant.size, Constant.size, Constant.size);
					}
				}
				for(Bot b: objects) {
					b.Draw(g2d, 0);
				}
				g2d.dispose();
				//
				BufferedImage buff2 = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);//энергия
				g2d = buff2.createGraphics();
				g2d.setColor(Color.WHITE);
				g2d.fillRect(0, 0, 1000, 1000);
				for (int x = 0; x < Constant.world_scale[0]; x++) {
					for (int y = 0; y < Constant.world_scale[1]; y++) {
						double ox = oxygen_map[x][y];
						if (ox > 0.5) {
							ox = 0.5;
						}
						g2d.setColor(new Color(255 - (int)(ox * 255), 255 - (int)(ox * 255), 255));
						g2d.fillRect(x * Constant.size, y * Constant.size, Constant.size, Constant.size);
						if (org_map[x][y] >= Constant.org_die_level) {
							g2d.setColor(new Color(90, 0, 0));
							g2d.fillRect(x * Constant.size, y * Constant.size, Constant.size, Constant.size);
						}
					}
				}
				for(Bot b: objects) {
					b.Draw(g2d, 2);
				}
				g2d.dispose();
				//
				BufferedImage buff3 = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);//цвет
				g2d = buff3.createGraphics();
				g2d.setColor(Color.WHITE);
				g2d.fillRect(0, 0, 1000, 1000);
				for (int x = 0; x < Constant.world_scale[0]; x++) {
					for (int y = 0; y < Constant.world_scale[1]; y++) {
						double ox = oxygen_map[x][y];
						if (ox > 0.5) {
							ox = 0.5;
						}
						g2d.setColor(new Color(255 - (int)(ox * 255), 255 - (int)(ox * 255), 255));
						g2d.fillRect(x * Constant.size, y * Constant.size, Constant.size, Constant.size);
						if (org_map[x][y] >= Constant.org_die_level) {
							g2d.setColor(new Color(90, 0, 0));
							g2d.fillRect(x * Constant.size, y * Constant.size, Constant.size, Constant.size);
						}
					}
				}
				for(Bot b: objects) {
					b.Draw(g2d, 1);
				}
				g2d.dispose();
				//
				ImageIO.write(buff, "png", new File("record/predators-oxygen/screen" + String.valueOf(steps / 25)+ ".png"));
				ImageIO.write(buff2, "png", new File("record/energy/screen" + String.valueOf(steps / 25)+ ".png"));
				ImageIO.write(buff3, "png", new File("record/color/screen" + String.valueOf(steps / 25)+ ".png"));
				ImageIO.write(buff4, "png", new File("record/predators-org/screen" + String.valueOf(steps / 25)+ ".png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public void newPopulation() {
		steps = 0;
		objects = new ArrayList<Bot>();
		Map = new Bot[Constant.world_scale[0]][Constant.world_scale[1]];
		oxygen_map = new double[Constant.world_scale[0]][Constant.world_scale[1]];
		org_map = new double[Constant.world_scale[0]][Constant.world_scale[1]];
		mnr_map = new double[Constant.world_scale[0]][Constant.world_scale[1]];
		for (int x = 0; x < Constant.world_scale[0]; x++) {
			for (int y = 0; y < Constant.world_scale[1]; y++) {
				oxygen_map[x][y] = Constant.starting_ox;
				org_map[x][y] = Constant.starting_org;
				mnr_map[x][y] = 0;
				Map[x][y] = null;
			}
		}
		for (int i = 0; i < Constant.starting_bot_count; i++) {
			while(true){
				int x = rand.nextInt(Constant.world_scale[0]);
				int y = rand.nextInt(Constant.world_scale[1]);
				if (Map[x][y] == null) {
					Bot new_bot = new Bot(
						x,
						y,
						new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)),
						1000,
						Map,
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
			if (e.getX() <= Constant.world_scale[0] * Constant.size && e.getY() <= Constant.world_scale[1] * Constant.size) {
				botpos[0] = e.getX() / Constant.size;
				botpos[1] = e.getY() / Constant.size;
				count_ox = oxygen_map[botpos[0]][botpos[1]];
				count_org = org_map[botpos[0]][botpos[1]];
				count_mnr = mnr_map[botpos[0]][botpos[1]];
				if (mouse == 0) {//select
					if (Map[botpos[0]][botpos[1]] != null && Map[botpos[0]][botpos[1]].state == 0) {
						for(Bot b: objects) {
							if (b.xpos == botpos[0] && b.ypos == botpos[1]) {
								selection = b;
								save_button.setEnabled(true);
								show_brain_button.setEnabled(true);
							}
						}
					}else {
						selection = null;
						save_button.setEnabled(false);
						show_brain_button.setEnabled(false);
						sh_brain = false;
					}
				}else if (mouse == 1) {//set
					if (for_set != null) {
						if (Map[botpos[0]][botpos[1]] == null) {
							objects.add(for_set);
							Map[botpos[0]][botpos[1]] = for_set;
						}
					}
				}else {//remove
					if (Map[botpos[0]][botpos[1]] != null) {
						for(Bot b: objects) {
							if (b.xpos == botpos[0] && b.ypos == botpos[1]) {
								b.energy = 0;
								b.killed = 1;
								Map[botpos[0]][botpos[1]] = null;
							}
						}
					}
				}
			}else {
				count_ox = -1;
				count_org = -1;
				count_mnr = -1;
			}
		}
		public void mouseDragged(MouseEvent e) {
			if (e.getX() <= Constant.world_scale[0] * Constant.size && e.getY() <= Constant.world_scale[1] * Constant.size) {
				botpos[0] = e.getX() / Constant.size;
				botpos[1] = e.getY() / Constant.size;
				count_ox = oxygen_map[botpos[0]][botpos[1]];
				count_org = org_map[botpos[0]][botpos[1]];
				count_mnr = mnr_map[botpos[0]][botpos[1]];
				if (mouse == 1) {//set
					if (for_set != null) {
						if (Map[botpos[0]][botpos[1]] == null) {
							objects.add(for_set);
							Map[botpos[0]][botpos[1]] = for_set;
						}
					}
				}else if (mouse == 2) {//remove
					if (Map[botpos[0]][botpos[1]] != null) {
						for(Bot b: objects) {
							if (b.xpos == botpos[0] && b.ypos == botpos[1]) {
								b.energy = 0;
								b.killed = 1;
								Map[botpos[0]][botpos[1]] = null;
							}
						}
					}
				}
			}else {
				count_ox = -1;
				count_org = -1;
				count_mnr = -1;
			}
		}
		public void actionPerformed(ActionEvent e) {
			if (!pause) {
				for (int i = 0; i < skip_slider.getValue(); i++) {
					steps++;
					b_count = 0;
					obj_count = 0;
					org_count = 0;
					ListIterator<Bot> bot_iterator = objects.listIterator();
					while (bot_iterator.hasNext()) {
						Bot next_bot = bot_iterator.next();
						next_bot.Update(bot_iterator, oxygen_map, org_map, mnr_map);
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
						if (selection.killed == 1 || Map[selection.xpos][selection.ypos] == null || selection.state != 0){
							selection = null;
							save_button.setEnabled(false);
							show_brain_button.setEnabled(false);
							sh_brain = false;
						}
					}
					ListIterator<Bot> iterator = objects.listIterator();
					while (iterator.hasNext()) {
						Bot next_bot = iterator.next();
						if (next_bot.killed == 1) {
							iterator.remove();
						}
					}
					oxygen();
					//minerals();
				}
			}
			repaint();
		}
	}
	public void minerals() {
		int ymin = Constant.world_scale[1] - Constant.world_scale[1] / 8 * 2;
		for (int i = 0; i < 100; i++) {
			int x = rand.nextInt(Constant.world_scale[0]);
			int y = rand.nextInt(ymin, Constant.world_scale[1]);
			mnr_map[x][y] += rand.nextInt(10, 30);
			if (mnr_map[x][y] > 1000) {
				mnr_map[x][y] = 1000;
			}
		}
		double[][] new_map = new double[Constant.world_scale[0]][Constant.world_scale[1]];
		for (int x = 0; x < Constant.world_scale[0]; x++) {
			for (int y = 0; y < Constant.world_scale[1]; y++) {
				if (mnr_map[x][y] > 0) {
					
				}
			}
		}
		mnr_map = new_map;
	}
	public void oxygen() {
		double[][] new_map = new double[Constant.world_scale[0]][Constant.world_scale[1]];
		for (int x = 0; x < Constant.world_scale[0]; x++) {
			for (int y = 0; y < Constant.world_scale[1]; y++) {
				if (oxygen_map[x][y] >= 0.009) {
					oxygen_map[x][y] *= 0.99;
					int count = 1;
					for (int i = 0; i < 8; i++) {
						int[] f = {x, y};
						int[] pos = get_rotate_position(i, f);
						if (pos[1] >= 0 && pos[1] < Constant.world_scale[1]) {
							count++;
						}
					}
					double ox = oxygen_map[x][y] / count;
					new_map[x][y] += ox;
					if (new_map[x][y] > 1) {
						new_map[x][y] = 1;
					}
					for (int i = 0; i < 8; i++) {
						int[] f = {x, y};
						int[] pos = get_rotate_position(i, f);
						if (pos[1] >= 0 && pos[1] < Constant.world_scale[1]) {
							new_map[pos[0]][pos[1]] += ox;
							if (new_map[pos[0]][pos[1]] > 1) {
								new_map[pos[0]][pos[1]] = 1;
							}
						}
					}
				}else {
					new_map[x][y] += oxygen_map[x][y];
					if (new_map[x][y] > 1) {
						new_map[x][y] = 1;
					}
				}
			}
		}
		oxygen_map = new_map;
	}
	public int[] get_rotate_position(int rot, int[] sp){
		int[] pos = new int[2];
		pos[0] = (sp[0] + Constant.movelist[rot][0]) % Constant.world_scale[0];
		pos[1] = sp[1] + Constant.movelist[rot][1];
		if (pos[0] < 0) {
			pos[0] = Constant.world_scale[0] - 1;
		}else if(pos[0] >= Constant.world_scale[0]) {
			pos[0] = 0;
		}
		return(pos);
	}
	public int sector(int y) {
		int sec = y / (Constant.world_scale[1] / 8);
		if (sec > 7) {
			sec = 7;
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
	private class change_draw_type implements ActionListener{//смена режима отрисовки(берется из параметра)
		int number;
		private change_draw_type(int new_number){
			number = new_number;
		}
		public void actionPerformed(ActionEvent e) {
			draw_type = number;
		}
	}
	private class change_gas_draw_type implements ActionListener{//смена режима отрисовки(берется из параметра)
		int number;
		private change_gas_draw_type(int new_number){
			number = new_number;
		}
		public void actionPerformed(ActionEvent e) {
			gas_draw_type = number;
		}
	}
	private class start_stop implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			pause = !pause;
			if (pause) {
				stop_button.setText("Start");
			}else {
				stop_button.setText("Stop");
			}
		}
	}
	private class select implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			mouse = 0;
		}
	}
	private class set implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			mouse = 1;
		}
	}
	private class remove implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			mouse = 2;
		}
	}
	private class nwp implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			newPopulation();
		}
	}
	private class rndr implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			render = !render;
			if (render) {
				render_button.setText("Render: on");
			}else {
				render_button.setText("Render: off");
			}
		}
	}
	private class rcrd implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			rec = !rec;
			if (rec) {
				record_button.setText("Record: on");
			}else {
				record_button.setText("Record: off");
			}
		}
	}
	private class shbr implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			sh_brain = !sh_brain;
			if (pause == false) {
				pause = true;
			}else if (sh_brain == false) {
				pause = false;
			}
		}
	}
	private class kill_all implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			steps = 0;
			objects = new ArrayList<Bot>();
			Map = new Bot[Constant.world_scale[0]][Constant.world_scale[1]];//0 - none, 1 - bot, 2 - organics
		}
	}
}
