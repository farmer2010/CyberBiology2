package sct;
import java.io.File;

import javax.swing.*;
//import java.awt.Dimension;

public class Main{
	public static void main(String[] args) {
		new File("record/predators").mkdirs();
		new File("record/energy").mkdirs();
		new File("record/color").mkdirs();
		new File("record/clans").mkdirs();
		//
		new File("saved objects").mkdirs();
		new File("saved worlds").mkdirs();
		//
		JFrame frame = new JFrame("Cyber biology 2 v2.0 pre-release 1");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(new World());
		frame.setSize(1920, 1080);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setUndecorated(true);
		frame.setVisible(true);
	}
}