package sct;

import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;

public class OptionsPanel extends JPanel {
	Random rand = new Random();
	Timer timer;
	//
	public World world;
	public int panel_type;
	//
	JTextField width = new JTextField();
	JTextField height = new JTextField();
	JTextField energy_for_life = new JTextField();
	JTextField energy_for_multiply = new JTextField();
	JTextField energy_for_auto_multiply = new JTextField();
	JTextField max_energy = new JTextField();
	JSlider mut_chance_slider = new JSlider(0, 100, Constant.child_mutation_chance);
	JSlider parent_mut_chance_slider = new JSlider(0, 100, Constant.parent_mutation_chance);
	JRadioButton index_button = new JRadioButton("set parent index to 0", Constant.upd_parent_index);
	JRadioButton age_button = new JRadioButton("set parent age to maximum", Constant.upd_parent_age);
	JRadioButton organics_button = new JRadioButton("enable organics", Constant.allow_organics);
	JRadioButton organics_no_fall_button = new JRadioButton("no fall", false);
	JRadioButton organics_fall_button = new JRadioButton("fall for first time", true);
	JRadioButton organics_always_fall_button = new JRadioButton("always fall", false);
	JRadioButton organics_sand_fall_button = new JRadioButton("sand fall", false);
	JTextField sun_levels = new JTextField();
	JTextField minerals_levels = new JTextField();
	JRadioButton draw_rotate_button = new JRadioButton("draw rotate", Constant.draw_rotate);
	JRadioButton organics_level_button = new JRadioButton("enable organics level", false);
	JTextField organics_die_level = new JTextField();
	//
	public OptionsPanel(World w, int new_panel_type) {
		world = w;
		panel_type = new_panel_type;
		//
		setLayout(null);
		//
		if (panel_type == 0) {
			JPanel panel1 = new JPanel();
			panel1.setLayout(new BoxLayout(panel1, BoxLayout.PAGE_AXIS));
			panel1.setBounds(0, 0, 300, 1080);
			add(panel1);
			//
			JPanel panel2 = new JPanel();
			panel2.setLayout(new BoxLayout(panel2, BoxLayout.PAGE_AXIS));
			panel2.setBounds(300, 0, 300, 1080);
			add(panel2);
			//
			panel1.add(new JLabel("MAIN SETTINGS:"));
			//
			panel1.add(new JLabel("world width:"));
			width.setText(String.valueOf(Constant.W));
			width.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
			panel1.add(width);
			//
			panel1.add(new JLabel("world height:"));
			height.setText(String.valueOf(Constant.H));
			height.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
			panel1.add(height);
			//
			panel1.add(new JLabel("WASTE OF ENERGY:"));
			//
			panel1.add(new JLabel("energy for life:"));
			energy_for_life.setText(String.valueOf(Constant.energy_for_life));
			energy_for_life.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
			panel1.add(energy_for_life);
			//
			panel1.add(new JLabel("energy for multiply:"));
			energy_for_multiply.setText(String.valueOf(Constant.energy_for_multiply));
			energy_for_multiply.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
			panel1.add(energy_for_multiply);
			//
			panel1.add(new JLabel("energy for auto multiply:"));
			energy_for_auto_multiply.setText(String.valueOf(Constant.energy_for_auto_multiply));
			energy_for_auto_multiply.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
			panel1.add(energy_for_auto_multiply);
			//
			panel1.add(new JLabel("maximum energy:"));
			max_energy.setText(String.valueOf(Constant.max_energy));
			max_energy.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
			panel1.add(max_energy);
			//
			panel1.add(new JLabel("MULTIPLY/MUTATION:"));
			//
			panel1.add(new JLabel("child chance of mutation:"));
			mut_chance_slider.setPaintLabels(true);
			mut_chance_slider.setMajorTickSpacing(25);
			panel1.add(mut_chance_slider);
			//
			panel1.add(new JLabel("parent chance of mutation:"));
			parent_mut_chance_slider.setPaintLabels(true);
			parent_mut_chance_slider.setMajorTickSpacing(25);
			panel1.add(parent_mut_chance_slider);
			//
			panel1.add(index_button);
			//
			panel1.add(age_button);
			//
			panel1.add(new JLabel("ORGANICS:"));
			//
			panel1.add(organics_button);
			//
			panel1.add(new JLabel("type of fall organics:"));
			ButtonGroup group = new ButtonGroup();
			panel1.add(organics_no_fall_button);
			group.add(organics_no_fall_button);
			//
			panel1.add(organics_fall_button);
			group.add(organics_fall_button);
			//
			panel1.add(organics_always_fall_button);
			group.add(organics_always_fall_button);
			//
			panel1.add(organics_sand_fall_button);
			group.add(organics_sand_fall_button);
			//
			panel1.add(new JLabel("SUN/MINERALS:"));
			//
			panel1.add(new JLabel("sun levels:"));
			String str = "";
			for (int i = 0; i < Constant.photo_list.length; i++) {
				str += String.valueOf(Constant.photo_list[i]) + ";";
			}
			sun_levels.setText(str);
			sun_levels.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
			panel1.add(sun_levels);
			//
			panel1.add(new JLabel("minerals levels:"));
			str = "";
			for (int i = 0; i < Constant.minerals_list.length; i++) {
				str += String.valueOf(Constant.minerals_list[i]) + ";";
			}
			minerals_levels.setText(str);
			minerals_levels.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
			panel1.add(minerals_levels);
			//
			panel1.add(new JLabel("DRAW:"));
			//
			panel1.add(draw_rotate_button);
			//
			//
			//
			panel2.add(new JLabel("ORGANICS LEVEL:"));
			//
			panel2.add(organics_level_button);
			//
			panel2.add(new JLabel("organics die level:"));
			organics_die_level.setText("800");
			organics_die_level.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
			panel2.add(organics_die_level);
		}else if (panel_type == 1) {
		}
		//
	}
	//
	public void paintComponent(Graphics canvas) {
		super.paintComponent(canvas);
		canvas.setColor(new Color(0, 0, 0));
		canvas.setFont(new Font("arial", Font.BOLD, 18));
		canvas.drawString(String.valueOf(world.settings_panel_index + 1) + "/" + String.valueOf(world.settings_panels.length), 825, 1060);
		canvas.fillRect(300, 0, 2, 1080);
		canvas.fillRect(600, 0, 2, 1080);
		canvas.fillRect(900, 0, 2, 1080);
		canvas.fillRect(1200, 0, 2, 1080);
		canvas.fillRect(1500, 0, 2, 1080);
		if (panel_type == 0) {
			canvas.drawString("Main settings:", 0, 20);
		}else if (panel_type == 1) {
		}
	}
	//
	public JTextField get_JTextField(int index) {
		return((JTextField) getComponent(index));
	}
	public JSlider get_JSlider(int index) {
		return((JSlider) getComponent(index));
	}
	public JRadioButton get_JRadioButton(int index) {
		return((JRadioButton) getComponent(index));
	}
	//
	private class Listener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			
		}
	}
}