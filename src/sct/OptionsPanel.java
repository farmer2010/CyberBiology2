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
	//type 0
	//
	//panel0
	JTextField width = new JTextField();
	JTextField height = new JTextField();
	JTextField energy_for_life = new JTextField();
	JTextField energy_for_multiply = new JTextField();
	JTextField energy_for_auto_multiply = new JTextField();
	JTextField max_energy = new JTextField();
	JRadioButton block_auto_mul_button = new JRadioButton("block auto multiply", Constant.block_auto_multiply);
	JSlider mut_chance_slider = new JSlider(0, 100, Constant.child_mutation_chance);
	JSlider parent_mut_chance_slider = new JSlider(0, 100, Constant.parent_mutation_chance);
	JRadioButton index_button = new JRadioButton("set parent index to 0", Constant.upd_parent_index);
	JRadioButton age_button = new JRadioButton("set parent age to maximum", Constant.upd_parent_age);
	JTextField attack_strength = new JTextField();
	JRadioButton organics_button = new JRadioButton("enable organics", Constant.allow_organics);
	JRadioButton organics_no_fall_button = new JRadioButton("no fall", Constant.org_fall_type == 0);
	JRadioButton organics_fall_button = new JRadioButton("fall for first time", Constant.org_fall_type == 1);
	JRadioButton organics_always_fall_button = new JRadioButton("always fall", Constant.org_fall_type == 2);
	JRadioButton organics_sand_fall_button = new JRadioButton("sand fall", Constant.org_fall_type == 3);
	JTextField sun_levels = new JTextField();
	JTextField minerals_levels = new JTextField();
	JRadioButton allow_age_button = new JRadioButton("allow age", Constant.allow_age);
	JTextField max_age = new JTextField();
	JRadioButton draw_rotate_button = new JRadioButton("draw rotate", Constant.draw_rotate);
	JRadioButton draw_frame_button = new JRadioButton("draw frame", Constant.draw_frame);
	//
	//
	//panel1
	JRadioButton divers_button = new JRadioButton("enable divers", Constant.enable_divers);
	JSlider sea_level_slider = new JSlider(0, 108, 65);
	JSlider mud_level_slider = new JSlider(0, 108, 24);
	JSlider no_pht_in_sea_slider = new JSlider(0, 1000, 1000);
	JSlider no_pht_in_mud_slider = new JSlider(0, 1000, 1000);
	JSlider move_y_slider = new JSlider(0, 1000, 0);
	JSlider land_multiply_slider = new JSlider(0, 1000, 1000);
	JSlider sea_multiply_slider = new JSlider(0, 1000, 1000);
	JSlider on_land_slider = new JSlider(0, 1000, 1000);
	JRadioButton new_genome_button = new JRadioButton("enable new genome", Constant.enable_new_genome);
	JRadioButton memory_to_child_button = new JRadioButton("inherit a memory", Constant.inherit_memory);
	JRadioButton new_energy_button = new JRadioButton("enable new energy system", Constant.enable_new_energy);
	JTextField energy_for_move = new JTextField();
	JTextField energy_for_push = new JTextField();
	JTextField energy_for_atk = new JTextField();
	JTextField energy_for_rot = new JTextField();
	JTextField energy_for_trans_res = new JTextField();
	JTextField energy_for_sens = new JTextField();
	JTextField energy_for_transitions = new JTextField();
	//
	public OptionsPanel(World w, int new_panel_type) {
		world = w;
		panel_type = new_panel_type;
		//
		setLayout(null);
		setBackground(new Color(90, 90, 90));
		setBounds(0, 0, 1620, 1080);
		//
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
		JPanel panel3 = new JPanel();
		panel3.setLayout(new BoxLayout(panel3, BoxLayout.PAGE_AXIS));
		panel3.setBounds(600, 0, 300, 1080);
		add(panel3);
		//
		if (panel_type == 0) {//базовые параметры и эксперименты
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
			panel1.add(block_auto_mul_button);
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
			panel1.add(new JLabel("ATTACK:"));
			panel1.add(new JLabel("attack strength:"));
			attack_strength.setText(String.valueOf(Constant.attack_strength));
			attack_strength.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
			panel1.add(attack_strength);
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
			panel1.add(new JLabel("AGE:"));
			//
			panel1.add(allow_age_button);
			//
			panel1.add(new JLabel("maximum age:"));
			max_age.setText(String.valueOf(Constant.max_age));
			max_age.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
			panel1.add(max_age);
			//
			panel1.add(new JLabel("DRAW:"));
			//
			panel1.add(draw_rotate_button);
			//
			panel1.add(draw_frame_button);
			//
			//panel2
			//
			panel2.add(new JLabel("ADAPTATION:"));
			//
			panel2.add(new JLabel("Divers:"));
			//
			panel2.add(divers_button);
			//
			JLabel sea_label = new JLabel("sea level: " + String.valueOf(sea_level_slider.getValue()));
			panel2.add(sea_label);
			sea_level_slider.setPaintLabels(true);
			sea_level_slider.setMajorTickSpacing(25);
			sea_level_slider.addChangeListener(e -> update_text(sea_level_slider, sea_label, "sea level: "));
			panel2.add(sea_level_slider);
			//
			JLabel mud_label = new JLabel("mud level: " + String.valueOf(mud_level_slider.getValue()));
			panel2.add(mud_label);
			mud_level_slider.setPaintLabels(true);
			mud_level_slider.setMajorTickSpacing(25);
			mud_level_slider.addChangeListener(e -> update_text(mud_level_slider, mud_label, "mud level: "));
			panel2.add(mud_level_slider);
			//
			JLabel no_pht_in_sea_label = new JLabel("block pht in sea: " + String.valueOf(no_pht_in_sea_slider.getValue()));
			panel2.add(no_pht_in_sea_label);
			no_pht_in_sea_slider.setPaintLabels(true);
			no_pht_in_sea_slider.setMajorTickSpacing(100);
			no_pht_in_sea_slider.addChangeListener(e -> update_text(no_pht_in_sea_slider, no_pht_in_sea_label, "block pht in sea: "));
			panel2.add(no_pht_in_sea_slider);
			//
			JLabel no_pht_in_mud_label = new JLabel("block pht in mud: " + String.valueOf(no_pht_in_mud_slider.getValue()));
			panel2.add(no_pht_in_mud_label);
			no_pht_in_mud_slider.setPaintLabels(true);
			no_pht_in_mud_slider.setMajorTickSpacing(100);
			no_pht_in_mud_slider.addChangeListener(e -> update_text(no_pht_in_mud_slider, no_pht_in_mud_label, "block pht in mud: "));
			panel2.add(no_pht_in_mud_slider);
			//
			JLabel move_y_label = new JLabel("force movements y: " + String.valueOf(move_y_slider.getValue()));
			panel2.add(move_y_label);
			move_y_slider.setPaintLabels(true);
			move_y_slider.setMajorTickSpacing(100);
			move_y_slider.addChangeListener(e -> update_text(move_y_slider, move_y_label, "force movements y: "));
			panel2.add(move_y_slider);
			//
			JLabel no_mul_on_land_label = new JLabel("block multiply in surface: " + String.valueOf(land_multiply_slider.getValue()));
			panel2.add(no_mul_on_land_label);
			land_multiply_slider.setPaintLabels(true);
			land_multiply_slider.setMajorTickSpacing(100);
			land_multiply_slider.addChangeListener(e -> update_text(land_multiply_slider, no_mul_on_land_label, "block multiply in surface: "));
			panel2.add(land_multiply_slider);
			//
			JLabel no_mul_in_sea_label = new JLabel("block multiply in sea: " + String.valueOf(sea_multiply_slider.getValue()));
			panel2.add(no_mul_in_sea_label);
			sea_multiply_slider.setPaintLabels(true);
			sea_multiply_slider.setMajorTickSpacing(100);
			sea_multiply_slider.addChangeListener(e -> update_text(sea_multiply_slider, no_mul_in_sea_label, "block multiply in sea: "));
			panel2.add(sea_multiply_slider);
			//
			JLabel on_land_label = new JLabel("on land at least once: " + String.valueOf(on_land_slider.getValue()));
			panel2.add(on_land_label);
			on_land_slider.setPaintLabels(true);
			on_land_slider.setMajorTickSpacing(100);
			on_land_slider.addChangeListener(e -> update_text(on_land_slider, on_land_label, "on land at least once: "));
			panel2.add(on_land_slider);
			//
			//
			panel3.add(new JLabel("NEW GENOME:"));
			//
			panel3.add(new_genome_button);
			panel3.add(memory_to_child_button);
			//
			//
			panel3.add(new JLabel("NEW ENERGY SYSTEM:"));
			//
			panel3.add(new_energy_button);
			//
			//
			panel3.add(new JLabel("energy for move:"));
			energy_for_move.setText(String.valueOf(Constant.energy_for_move));
			energy_for_move.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
			panel3.add(energy_for_move);
			//
			panel3.add(new JLabel("energy for push:"));
			energy_for_push.setText(String.valueOf(Constant.energy_for_push));
			energy_for_push.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
			panel3.add(energy_for_push);
			//
			panel3.add(new JLabel("energy for attack:"));
			energy_for_atk.setText(String.valueOf(Constant.energy_for_attack));
			energy_for_atk.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
			panel3.add(energy_for_atk);
			//
			panel3.add(new JLabel("energy for rotate:"));
			energy_for_rot.setText(String.valueOf(Constant.energy_for_rotate));
			energy_for_rot.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
			panel3.add(energy_for_rot);
			//
			panel3.add(new JLabel("energy for transfer resources:"));
			energy_for_trans_res.setText(String.valueOf(Constant.energy_for_trans_res));
			energy_for_trans_res.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
			panel3.add(energy_for_trans_res);
			//
			panel3.add(new JLabel("energy for sensors:"));
			energy_for_sens.setText(String.valueOf(Constant.energy_for_sensors));
			energy_for_sens.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
			panel3.add(energy_for_sens);
			//
			panel3.add(new JLabel("energy for transitions:"));
			energy_for_transitions.setText(String.valueOf(Constant.energy_for_transitions));
			energy_for_transitions.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
			panel3.add(energy_for_transitions);
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
	public void update_text(JSlider slider, JLabel label, String text) {
		label.setText(text + String.valueOf(slider.getValue()));
	}
	//
	private class Listener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			
		}
	}
}