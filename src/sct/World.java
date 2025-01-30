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
	int size = 25;
	Timer timer;
	int delay = 10;
	Random rand = new Random();
	Bot[][] Map = new Bot[162][108];
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
	int[] for_set;
	JButton save_button = new JButton("Save");
	JButton show_brain_button = new JButton("Show brain");
	JButton render_button = new JButton("Render: on");
	JButton record_button = new JButton("Record: off");
	JTextField for_save = new JTextField();
	JTextField for_load = new JTextField();
	JSlider atk = new JSlider(0, 1000, 0);
	JSlider dist = new JSlider(0, 1000, 0);
	JSlider atk_right = new JSlider(0, 1000, 0);
	JSlider see_block = new JSlider(0, 1, 0);
	boolean sh_brain = false;
	boolean rec = false;
	public World() {
		setLayout(null);
		timer = new Timer(delay, new BotListener());
		objects = new ArrayList<Bot>();
		setBackground(new Color(255, 255, 255));
		addMouseListener(new BotListener());
		addMouseMotionListener(new BotListener());
		stop_button.addActionListener(new start_stop());
		stop_button.setBounds(W - 300, 125, 250, 35);
        add(stop_button);
        JButton predators_button = new JButton("Predators");
        predators_button.addActionListener(new dr1());
		predators_button.setBounds(W - 300, 190, 125, 20);
        add(predators_button);
        JButton energy_button = new JButton("Energy");
        energy_button.addActionListener(new dr3());
		energy_button.setBounds(W - 170, 190, 125, 20);
        add(energy_button);
        JButton minerals_button = new JButton("Minerals");
		minerals_button.setBounds(W - 300, 215, 125, 20);
		minerals_button.addActionListener(new dr4());
        add(minerals_button);
        JButton age_button = new JButton("Age");
        age_button.addActionListener(new dr5());
		age_button.setBounds(W - 170, 215, 125, 20);
        add(age_button);
        JButton color_button = new JButton("Color");
        color_button.addActionListener(new dr2());
		color_button.setBounds(W - 300, 240, 125, 20);
        add(color_button);
        JButton select_button = new JButton("Select");
        select_button.addActionListener(new select());
		select_button.setBounds(W - 300, 455, 95, 20);
        add(select_button);
        JButton set_button = new JButton("Set");
        set_button.addActionListener(new set());
        set_button.setBounds(W - 200, 455, 95, 20);
        add(set_button);
        JButton remove_button = new JButton("Remove");
        remove_button.addActionListener(new remove());
        remove_button.setBounds(W - 100, 455, 95, 20);
        add(remove_button);
        //
        save_button.addActionListener(new save_bot());
        save_button.setBounds(W - 300, 365, 125, 20);
        save_button.setEnabled(false);
        add(save_button);
        //
        show_brain_button.addActionListener(new shbr());
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
        load_bot_button.addActionListener(new load_bot());
        load_bot_button.setBounds(W - 300, 540, 125, 20);
        add(load_bot_button);
        //
        JButton load_world_button = new JButton("Load world");
        //load_world_button.addActionListener(new remove());
        load_world_button.setBounds(W - 170, 540, 125, 20);
        add(load_world_button);
        JButton new_population_button = new JButton("New population");
        new_population_button.addActionListener(new nwp());
        new_population_button.setBounds(W - 300, 590, 125, 20);
        add(new_population_button);
        //
        render_button.addActionListener(new rndr());
        render_button.setBounds(W - 300, 615, 125, 20);
        add(render_button);
        //
        record_button.addActionListener(new rcrd());
        record_button.setBounds(W - 170, 615, 125, 20);
        add(record_button);
        JButton kill_button = new JButton("Kill all");
        kill_button.addActionListener(new kill_all());
        kill_button.setBounds(W - 170, 590, 125, 20);
        add(kill_button);
        //
        atk.setBounds(W - 300, 660, 250, 40);
        atk.setPaintLabels(true);
        atk.setMajorTickSpacing(100);
        add(atk);
        //
        atk_right.setBounds(W - 300, 720, 250, 40);
        atk_right.setPaintLabels(true);
        atk_right.setMajorTickSpacing(100);
        add(atk_right);
        //
        dist.setBounds(W - 300, 780, 250, 40);
        dist.setPaintLabels(true);
        dist.setMajorTickSpacing(100);
        add(dist);
        //
        see_block.setBounds(W - 300, 840, 250, 40);
        see_block.setPaintLabels(true);
        see_block.setMajorTickSpacing(1);
        add(see_block);
		timer.start();
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
		//for (int x = 0; x < 162; x++) {
		//	for (int y = 0; y < 108; y++) {
		//		if (Map[x][y] == 1) {
		//			canvas.setColor(green);
		//			canvas.fillRect(x * 10, y * 10, 10, 10);
		//		}else if (Map[x][y] == 2){
		//			canvas.setColor(red);
		//			canvas.fillRect(x * 10, y * 10, 10, 10);
		//		}
		//	}
		//}
		if (render) {
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
			txt = "minerals view";
		}else {
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
		canvas.drawString("Selection:", W - 300, 275);
		canvas.drawString("enter name:", W - 300, 405);
		canvas.drawString("Mouse functions:", W - 300, 445);
		canvas.drawString("Load:", W - 300, 490);
		canvas.drawString("enter name:", W - 300, 510);
		canvas.drawString("Controls:", W - 300, 580);
		canvas.drawString(String.valueOf(atk.getValue()), W - 40, 680);
		canvas.drawString(String.valueOf(atk_right.getValue()), W - 40, 740);
		canvas.drawString(String.valueOf(dist.getValue()), W - 40, 800);
		canvas.drawString("Attack:", W - 300, 655);
		canvas.drawString("Attack right:", W - 300, 715);
		canvas.drawString("Energy distribution:", W - 300, 775);
		canvas.drawString("Block colonies:", W - 300, 835);
		if (selection != null) {
			canvas.drawString("energy: " + String.valueOf(selection.energy) + ", minerals: " + String.valueOf(selection.minerals), W - 300, 295);
			canvas.drawString("age: " + String.valueOf(selection.age), W - 300, 315);
			canvas.drawString("position: " + "[" + String.valueOf(selection.xpos) + ", " + String.valueOf(selection.ypos) + "]", W - 300, 335);
			canvas.drawString("color: " + "(" + String.valueOf(selection.color.getRed()) + ", " + String.valueOf(selection.color.getGreen()) + ", " + String.valueOf(selection.color.getBlue()) + ")", W - 300, 355);
			canvas.setColor(new Color(90, 90, 90, 90));
			canvas.fillRect(0, 0, W - 300, 1080);
			canvas.setColor(new Color(255, 0, 0));
			canvas.fillRect(selection.xpos * 10, selection.ypos * 10, 10, 10);
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
	}
	public void newPopulation() {
		steps = 0;
		objects = new ArrayList<Bot>();
		Map = new Bot[162][108];//0 - none, 1 - bot, 2 - organics
		for (int i = 0; i < 1000; i++) {
			while(true){
				int x = rand.nextInt(162);
				int y = rand.nextInt(108);
				if (Map[x][y] == null) {
					Bot new_bot = new Bot(
						x,
						y,
						new Color(rand.nextInt(256),rand.nextInt(256), rand.nextInt(256)),
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
			if (e.getX() < W - 300) {
				botpos[0] = e.getX() / 10;
				botpos[1] = e.getY() / 10;
				if (mouse == 0) {//select
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
				}else if (mouse == 1) {//set
					if (for_set != null) {
						if (Map[botpos[0]][botpos[1]] == null) {
							if (for_set != null) {
								Bot new_bot = new Bot(botpos[0], botpos[1], new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)), 1000, Map, objects);
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
		public void mouseDragged(MouseEvent e) {
			if (e.getX() < W - 300) {
				botpos[0] = e.getX() / 10;
				botpos[1] = e.getY() / 10;
				if (mouse == 1) {//set
					if (for_set != null) {
						if (Map[botpos[0]][botpos[1]] == null) {
							if (for_set != null) {
								Bot new_bot = new Bot(botpos[0], botpos[1], new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)), 1000, Map, objects);
								for (int i = 0; i < 64; i++) {
									new_bot.commands[i] = for_set[i];
								}
								objects.add(new_bot);
								Map[botpos[0]][botpos[1]] = new_bot;
							}
						}
					}
				}else if (mouse == 2) {//remove
					if (Map[botpos[0]][botpos[1]] != null) {
						Bot b = Map[botpos[0]][botpos[1]];
						b.energy = 0;
						b.killed = 1;
						Map[botpos[0]][botpos[1]] = null;
					}
				}
			}
		}
		public void actionPerformed(ActionEvent e) {
			if (!pause) {
				steps++;
				b_count = 0;
				obj_count = 0;
				org_count = 0;
				ListIterator<Bot> bot_iterator = objects.listIterator();
				while (bot_iterator.hasNext()) {
					Bot next_bot = bot_iterator.next();
					next_bot.Update(bot_iterator, atk.getValue(), atk_right.getValue(), dist.getValue(), see_block.getValue());
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
			}
			//if (steps >= 1000) {
			//	newPopulation();
			//}
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
	private class dr1 implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			draw_type = 0;
		}
	}
	private class dr2 implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			draw_type = 1;
		}
	}
	private class dr3 implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			draw_type = 2;
		}
	}
	private class dr4 implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			draw_type = 3;
		}
	}
	private class dr5 implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			draw_type = 4;
		}
	}
	private class dr6 implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			draw_type = 5;
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
			Map = new Bot[162][108];//0 - none, 1 - bot, 2 - organics
		}
	}
	private class save_bot implements ActionListener{
		public void actionPerformed(ActionEvent e) {
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
	}
	private class load_bot implements ActionListener{
		public void actionPerformed(ActionEvent e) {
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
	}
}
