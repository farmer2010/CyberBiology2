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
	//массивы, таймер, класс генератора случайных чисел
	ArrayList<Bot> objects;
	Random rand = new Random();
	Timer timer;
	Bot[][] Map = new Bot[Constant.W][Constant.H];
	//переменные
	int steps = 0;//
	int b_count = 0;//
	int obj_count = 0;//
	int draw_type = 0;//
	int mouse = 0;//
	int delay = 10;//
	//
	boolean pause = false;//
	boolean render = true;//
	boolean sh_brain = false;//
	boolean settigs = false;//
	boolean rec = false;//
	Bot selection = null;//
	int[] botpos = new int[2];//
	int[] for_set;//
	//кнопки
	JButton stop_button = new JButton("Stop");
	JButton save_button = new JButton("Save");
	JButton show_brain_button = new JButton("Show brain");
	JButton render_button = new JButton("Render: on");
	JButton record_button = new JButton("Record: off");
	JButton back_button = new JButton("< Back");
	JButton next_button = new JButton("Next >");
	JButton settings_button = new JButton("Settings");
	JButton st_back_button = new JButton("< Back");
	JButton st_next_button = new JButton("Next >");
	JTextField for_save = new JTextField();
	JTextField for_load = new JTextField();
	//интерфейс
	Panel[] panels = new Panel[4];
	OptionsPanel[] settings_panels = new OptionsPanel[3];
	//
	int panel_index = 0;
	int settings_panel_index = 0;
	public World() {
		setLayout(null);
		timer = new Timer(delay, new BotListener());
		objects = new ArrayList<Bot>();
		setBackground(new Color(90, 90, 90));
		addMouseListener(new BotListener());
		addMouseMotionListener(new BotListener());
		//
		for (int i = 0; i < panels.length; i++) {
			panels[i] = new Panel(this, i);
			panels[i].setBackground(new Color(90, 90, 90));
			panels[i].setLayout(null);
	    	panels[i].setBounds(Constant.display_W - 300, 0, 300, 1080);
		}
		//
		back_button.addActionListener(e -> back());
        back_button.setBounds(Constant.display_W - 300, 1040, 125, 20);
        back_button.setEnabled(false);
        add(back_button);
        //
        next_button.addActionListener(e -> next());
        next_button.setBounds(Constant.display_W - 125, 1040, 125, 20);
        add(next_button);
		//
		add_buttons_to_panel0();
        add(panels[0]);
        //
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
		canvas.setColor(new Color(255, 255, 255));
		canvas.fillRect(0, 0, Constant.W * 10, Constant.H * 10);
		if (render) {
			for(Bot b: objects) {
				b.Draw(canvas, draw_type);
			}
		}
		if (selection != null) {
			canvas.setColor(new Color(0, 0, 0, 200));
			canvas.fillRect(0, 0, Constant.display_W - 300, 1080);
			canvas.setColor(new Color(255, 0, 0));
			canvas.fillRect(selection.xpos * 10, selection.ypos * 10, 10, 10);
		}
		if (sh_brain) {
			canvas.setFont(new Font("arial", Font.BOLD, 18));
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
	public void new_population() {
		kill_all();
		//
		for (int i = 0; i < Constant.starting_bots; i++) {
			while(true){
				int x = rand.nextInt(Constant.W);
				int y = rand.nextInt(Constant.H);
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
	public void kill_all() {
		Constant.W = Integer.parseInt(panels[1].get_OptionsPanel(2).get_JTextField(0).getText());
		Constant.H = Integer.parseInt(panels[1].get_OptionsPanel(2).get_JTextField(1).getText());
		Constant.starting_bots = Integer.parseInt(panels[1].get_OptionsPanel(2).get_JTextField(2).getText());
		Constant.energy_for_life = Integer.parseInt(panels[1].get_OptionsPanel(2).get_JTextField(3).getText());
		Constant.energy_for_multiply = Integer.parseInt(panels[1].get_OptionsPanel(2).get_JTextField(4).getText());
		Constant.energy_for_auto_multiply = Integer.parseInt(panels[1].get_OptionsPanel(2).get_JTextField(5).getText());
		Constant.max_energy = Integer.parseInt(panels[1].get_OptionsPanel(2).get_JTextField(6).getText());
		Constant.child_mutation_chance = panels[1].get_OptionsPanel(2).get_JSlider(7).getValue();
		Constant.parent_mutation_chance = panels[1].get_OptionsPanel(2).get_JSlider(8).getValue();
		Constant.color_range = Integer.parseInt(panels[1].get_OptionsPanel(2).get_JTextField(9).getText());
		Constant.allow_organics = panels[1].get_OptionsPanel(2).get_JRadioButton(10).isSelected();
		if (panels[1].get_OptionsPanel(2).get_JRadioButton(11).isSelected()) {
			Constant.org_fall_type = 0;
		}else if (panels[1].get_OptionsPanel(2).get_JRadioButton(12).isSelected()) {
			Constant.org_fall_type = 1;
		}else if (panels[1].get_OptionsPanel(2).get_JRadioButton(13).isSelected()) {
			Constant.org_fall_type = 2;
		}else if (panels[1].get_OptionsPanel(2).get_JRadioButton(14).isSelected()) {
			Constant.org_fall_type = 3;
		}
		Constant.full_color_change_without_mul = panels[1].get_OptionsPanel(2).get_JRadioButton(15).isSelected();
		Constant.color_change_without_mul = panels[1].get_OptionsPanel(2).get_JRadioButton(16).isSelected();
		//
		Constant.full_color_change_with_mul = panels[1].get_OptionsPanel(2).get_JRadioButton(18).isSelected();
		Constant.color_change_with_mul = panels[1].get_OptionsPanel(2).get_JRadioButton(19).isSelected();
		//
		Constant.full_color_change_chance = panels[1].get_OptionsPanel(2).get_JSlider(21).getValue();
		Constant.max_age = (double)(panels[1].get_OptionsPanel(2).get_JSlider(22).getValue());
		Constant.draw_rotate = panels[1].get_OptionsPanel(2).get_JRadioButton(23).isSelected();
		//
		steps = 0;
		objects = new ArrayList<Bot>();
		Map = new Bot[Constant.W][Constant.H];
	}
	private class BotListener extends MouseAdapter implements ActionListener{
		public void mousePressed(MouseEvent e) {
			if (e.getX() < Constant.display_W - 300) {
				botpos[0] = e.getX() / 10;
				botpos[1] = e.getY() / 10;
				update_mouse(botpos, false);
			}
		}
		public void mouseDragged(MouseEvent e) {
			if (e.getX() < Constant.display_W - 300) {
				botpos[0] = e.getX() / 10;
				botpos[1] = e.getY() / 10;
				update_mouse(botpos, true);
			}
		}
		public void actionPerformed(ActionEvent e) {
			if (!pause) {
				steps++;
				b_count = 0;
				obj_count = 0;
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
					if (next_bot.state == 0) {
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
	public void update_mouse(int[] pos, boolean block_select) {
		if (mouse == 0 && !block_select) {//select
			if (find_map_pos(pos, 0)) {
				Bot b = Map[pos[0]][pos[1]];
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
				if (Map[pos[0]][pos[1]] == null) {
					if (for_set != null) {
						Bot new_bot = new Bot(pos[0], pos[1], new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)), 1000, Map, objects);
						for (int i = 0; i < 64; i++) {
							new_bot.commands[i] = for_set[i];
						}
						new_bot.energy = panels[2].get_JSlider(3).getValue();
						new_bot.minerals = panels[2].get_JSlider(4).getValue();
						new_bot.age = panels[2].get_JSlider(5).getValue();
						new_bot.color = new Color(panels[2].get_JSlider(0).getValue(), panels[2].get_JSlider(1).getValue(), panels[2].get_JSlider(2).getValue());
						int s = 0;
						if (panels[2].get_JRadioButton(7).isSelected()) {
							s = 1;
						}else if (panels[2].get_JRadioButton(8).isSelected()) {
							s = 2;
						}
						new_bot.state = s;
						objects.add(new_bot);
						Map[pos[0]][pos[1]] = new_bot;
					}
				}
			}
		}else if (mouse == 2){//remove
			if (Map[pos[0]][pos[1]] != null) {
				Bot b = Map[pos[0]][pos[1]];
				b.energy = 0;
				b.killed = 1;
				Map[pos[0]][pos[1]] = null;
			}
		}
	}
	public void change_draw_type(int num) {
		draw_type = num;
	}
	public void back() {
		remove(panels[panel_index]);
		panel_index--;
		next_button.setEnabled(true);
		if (panel_index <= 0) {
			panel_index = 0;
			back_button.setEnabled(false);
		}else {
			back_button.setEnabled(true);
		}
		add(panels[panel_index]);
	}
	public void next() {
		remove(panels[panel_index]);
		panel_index++;
		back_button.setEnabled(true);
		if (panel_index >= panels.length - 1) {
			panel_index = panels.length - 1;
			next_button.setEnabled(false);
		}else {
			next_button.setEnabled(true);
		}
		add(panels[panel_index]);
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
			new_population();
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
	            for (int i = 0; i < panels[2].getComponentCount(); i++) {
	            	panels[2].getComponent(i).setVisible(true);
	            }
	        } catch (IOException ex) {
	            System.out.println("Ошибка при чтении файла");
	            ex.printStackTrace();
	        }
		}
	}
	private class save_world implements ActionListener{
		public void actionPerformed(ActionEvent e) {
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
	}
	private class load_world implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			try {
	            FileReader fileReader = new FileReader("saved worlds/" + for_load.getText() + ".dat");
	            BufferedReader bufferedReader = new BufferedReader(fileReader);
	 
	            String line = bufferedReader.readLine();
	 
	            bufferedReader.close();
	            
	            String[] l = line.split(";");
	            steps = Integer.parseInt(l[0]);
	            objects = new ArrayList<Bot>();
	    		Map = new Bot[162][108];//0 - none, 1 - bot, 2 - organics
	    		
	    		for (int i = 1; i < l.length; i++) {
	    			String[] bot_data = l[i].split(":");
	    			Bot new_bot = new Bot(
	    				Integer.parseInt(bot_data[3]),
	    				Integer.parseInt(bot_data[4]),
	    				new Color(Integer.parseInt(bot_data[10]), Integer.parseInt(bot_data[11]), Integer.parseInt(bot_data[12])),
	    				Integer.parseInt(bot_data[0]),
	    				Map,
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
	//
	public void add_buttons_to_panel0() {
		stop_button.addActionListener(new start_stop());
		stop_button.setBounds(0, 125, 250, 35);
        panels[0].add(stop_button);
        //
        JButton predators_button = new JButton("Predators");
        predators_button.addActionListener(e -> change_draw_type(0));
		predators_button.setBounds(0, 190, 125, 20);
		panels[0].add(predators_button);
        //
        JButton energy_button = new JButton("Energy");
        energy_button.addActionListener(e -> change_draw_type(2));
		energy_button.setBounds(130, 190, 125, 20);
		panels[0].add(energy_button);
        //
        JButton minerals_button = new JButton("Minerals");
		minerals_button.setBounds(0, 215, 125, 20);
		minerals_button.addActionListener(e -> change_draw_type(3));
		panels[0].add(minerals_button);
        //
        JButton age_button = new JButton("Age");
        age_button.addActionListener(e -> change_draw_type(4));
		age_button.setBounds(130, 215, 125, 20);
		panels[0].add(age_button);
        //
        JButton color_button = new JButton("Color");
        color_button.addActionListener(e -> change_draw_type(1));
		color_button.setBounds(0, 240, 125, 20);
		panels[0].add(color_button);
        //
        JButton select_button = new JButton("Select");
        select_button.addActionListener(new select());
		select_button.setBounds(0, 455, 95, 20);
		panels[0].add(select_button);
        //
        JButton set_button = new JButton("Set");
        set_button.addActionListener(new set());
        set_button.setBounds(100, 455, 95, 20);
        panels[0].add(set_button);
        //
        JButton remove_button = new JButton("Remove");
        remove_button.addActionListener(new remove());
        remove_button.setBounds(200, 455, 95, 20);
        panels[0].add(remove_button);
        //
        save_button.addActionListener(new save_bot());
        save_button.setBounds(0, 365, 125, 20);
        save_button.setEnabled(false);
        panels[0].add(save_button);
        //
        show_brain_button.addActionListener(new shbr());
        show_brain_button.setBounds(130, 365, 125, 20);
        show_brain_button.setEnabled(false);
        panels[0].add(show_brain_button);
        //
        for_save.setBounds(0, 410, 250, 20);
        panels[0].add(for_save);
        //
        for_load.setBounds(0, 515, 250, 20);
        panels[0].add(for_load);
        //
        JButton load_bot_button = new JButton("Load bot");
        load_bot_button.addActionListener(new load_bot());
        load_bot_button.setBounds(0, 540, 95, 20);
        panels[0].add(load_bot_button);
        //
        JButton load_world_button = new JButton("Load world");
        load_world_button.addActionListener(new load_world());
        load_world_button.setBounds(100, 540, 95, 20);
        panels[0].add(load_world_button);
        //
        JButton save_world_button = new JButton("Save world");
        save_world_button.addActionListener(new save_world());
        save_world_button.setBounds(200, 540, 95, 20);
        panels[0].add(save_world_button);
        //
        JButton new_population_button = new JButton("New population");
        new_population_button.addActionListener(new nwp());
        new_population_button.setBounds(0, 590, 125, 20);
        panels[0].add(new_population_button);
        //
        render_button.addActionListener(new rndr());
        render_button.setBounds(0, 615, 125, 20);
        panels[0].add(render_button);
        //
        record_button.addActionListener(new rcrd());
        record_button.setBounds(130, 615, 125, 20);
        panels[0].add(record_button);
        //
        JButton kill_button = new JButton("Kill all");
        kill_button.addActionListener(e -> kill_all());
        kill_button.setBounds(130, 590, 125, 20);
        panels[0].add(kill_button);
	}
}
