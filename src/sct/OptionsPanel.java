package sct;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.*;

public class OptionsPanel extends JPanel {
	Random rand = new Random();
	Timer timer;
	//
	public World world;
	public int panel_type;
	//
	public OptionsPanel(World w, int new_panel_type) {
		world = w;
		panel_type = new_panel_type;
		//
		setLayout(null);
		//
		if (panel_type == 0) {
			JTextField width = new JTextField();
			width.setBounds(100, 25, 40, 20);
			width.setText("162");
			add(width);
			//
			JTextField height = new JTextField();
			height.setBounds(160, 25, 40, 20);
			height.setText("108");
			add(height);
			//
			JTextField bot_count = new JTextField();
			bot_count.setBounds(120, 45, 40, 20);
			bot_count.setText("1000");
			add(bot_count);
			//
			JTextField energy_for_life = new JTextField();
			energy_for_life.setBounds(130, 85, 40, 20);
			energy_for_life.setText("1");
			add(energy_for_life);
			//
			JTextField energy_for_multiply = new JTextField();
			energy_for_multiply.setBounds(180, 105, 40, 20);
			energy_for_multiply.setText("150");
			add(energy_for_multiply);
			//
			JTextField energy_for_auto_multiply = new JTextField();
			energy_for_auto_multiply.setBounds(220, 125, 40, 20);
			energy_for_auto_multiply.setText("800");
			add(energy_for_auto_multiply);
			//
			JTextField max_energy = new JTextField();
			max_energy.setBounds(165, 145, 40, 20);
			max_energy.setText("1000");
			add(max_energy);
			//
			JSlider mut_chance_slider = new JSlider(0, 100, 25);
			mut_chance_slider.setBounds(0, 210, 200, 40);
			mut_chance_slider.setPaintLabels(true);
			mut_chance_slider.setMajorTickSpacing(25);
			add(mut_chance_slider);
			//
			JSlider parent_mut_chance_slider = new JSlider(0, 100, 0);
			parent_mut_chance_slider.setBounds(0, 280, 200, 40);
			parent_mut_chance_slider.setPaintLabels(true);
			parent_mut_chance_slider.setMajorTickSpacing(25);
			add(parent_mut_chance_slider);
			//
			JTextField color_range = new JTextField();
			color_range.setBounds(110, 575, 40, 20);
			color_range.setText("12");
			add(color_range);
			//
			JRadioButton organics_button = new JRadioButton("allow organics", true);
			organics_button.setBounds(0, 620, 150, 20);
			add(organics_button);
			//
			ButtonGroup group = new ButtonGroup();
			JRadioButton organics_no_fall_button = new JRadioButton("no fall", false);
			organics_no_fall_button.setBounds(0, 670, 150, 20);
			add(organics_no_fall_button);
			group.add(organics_no_fall_button);
			//
			JRadioButton organics_fall_button = new JRadioButton("fall for first time", true);
			organics_fall_button.setBounds(0, 690, 150, 20);
			add(organics_fall_button);
			group.add(organics_fall_button);
			//
			JRadioButton organics_always_fall_button = new JRadioButton("always fall", false);
			organics_always_fall_button.setBounds(0, 710, 150, 20);
			add(organics_always_fall_button);
			group.add(organics_always_fall_button);
			//
			JRadioButton organics_sand_fall_button = new JRadioButton("sand fall", false);
			organics_sand_fall_button.setBounds(0, 730, 150, 20);
			add(organics_sand_fall_button);
			group.add(organics_sand_fall_button);
			//
			JRadioButton full_change_color_without_mutation = new JRadioButton("complete color change", false);
			full_change_color_without_mutation.setBounds(0, 350, 200, 20);
			add(full_change_color_without_mutation);
			//
			ButtonGroup group2 = new ButtonGroup();
			JRadioButton little_change_color_without_mutation = new JRadioButton("change color a little", false);
			little_change_color_without_mutation.setBounds(0, 370, 200, 20);
			add(little_change_color_without_mutation);
			group2.add(little_change_color_without_mutation);
			//
			JRadioButton no_change_color_without_mutation = new JRadioButton("don't change color", true);
			no_change_color_without_mutation.setBounds(0, 390, 200, 20);
			add(no_change_color_without_mutation);
			group2.add(no_change_color_without_mutation);
			//
			JRadioButton full_change_color_with_mutation = new JRadioButton("complete color change", true);
			full_change_color_with_mutation.setBounds(0, 440, 200, 20);
			add(full_change_color_with_mutation);
			//
			ButtonGroup group3 = new ButtonGroup();
			JRadioButton little_change_color_with_mutation = new JRadioButton("change color a little", false);
			little_change_color_with_mutation.setBounds(0, 460, 200, 20);
			add(little_change_color_with_mutation);
			group3.add(little_change_color_with_mutation);
			//
			JRadioButton no_change_color_with_mutation = new JRadioButton("don't change color", true);
			no_change_color_with_mutation.setBounds(0, 480, 200, 20);
			add(no_change_color_with_mutation);
			group3.add(no_change_color_with_mutation);
			//
			JSlider change_color_chance_slider = new JSlider(0, 1000, 1);
			change_color_chance_slider.setBounds(0, 530, 200, 40);
			change_color_chance_slider.setPaintLabels(true);
			change_color_chance_slider.setMajorTickSpacing(200);
			add(change_color_chance_slider);
			//
			JSlider max_age_slider = new JSlider(0, 5000, 1000);
			max_age_slider.setBounds(0, 800, 200, 40);
			max_age_slider.setPaintLabels(true);
			max_age_slider.setMajorTickSpacing(1000);
			add(max_age_slider);
			//
			JRadioButton draw_rotate = new JRadioButton("draw rotate", true);
			draw_rotate.setBounds(0, 870, 150, 20);
			add(draw_rotate);
		}
		//
		//timer = new Timer(10, new Listener());
		//timer.start();
	}
	//
	public void paintComponent(Graphics canvas) {
		super.paintComponent(canvas);
		canvas.setColor(new Color(0, 0, 0));
		canvas.setFont(new Font("arial", Font.BOLD, 18));
		canvas.drawString(String.valueOf(world.options_panel_index + 1) + "/" + String.valueOf(world.options_panels.length), 140, 1000);
		if (panel_type == 0) {
			canvas.drawString("Main settings:", 0, 20);
			canvas.drawString("world size:         x", 0, 40);
			canvas.drawString("starting bots:", 0, 60);
			canvas.drawString("Waste of energy:", 0, 80);
			canvas.drawString("energy for life:", 0, 100);
			canvas.drawString("energy for multiply:", 0, 120);
			canvas.drawString("energy for auto multiply:", 0, 140);
			canvas.drawString("energy maximum:", 0, 160);
			canvas.drawString("Multiply/mutation:", 0, 180);
			canvas.drawString("child mutation chance: " + String.valueOf(get_JSlider(7).getValue()) + "%", 0, 200);
			canvas.drawString("parent mutation chance: " + String.valueOf(get_JSlider(8).getValue()) + "%", 0, 270);
			canvas.drawString("change color without mutation:", 0, 340);
			canvas.drawString("change color when mutation:", 0, 430);
			canvas.drawString("full color change chance: 1/" + String.valueOf(get_JSlider(21).getValue()), 0, 520);
			canvas.drawString("color range:", 0, 590);
			canvas.drawString("Organics:", 0, 610);
			canvas.drawString("organics actions:", 0, 660);
			canvas.drawString("Age:", 0, 770);
			canvas.drawString("maximum age: " + String.valueOf(get_JSlider(22).getValue()), 0, 790);
			canvas.drawString("Draw:", 0, 860);
		}else if (panel_type == 1) {
			canvas.drawString("Change genome commands:", 0, 20);
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