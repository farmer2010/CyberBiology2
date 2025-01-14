package sct;
import java.io.File;

import javax.swing.*;

public class Main{
	public static void main(String[] args) {
		new File("record/predators-oxygen").mkdirs();
		new File("record/energy").mkdirs();
		new File("record/color").mkdirs();
		new File("record/predators-org").mkdirs();
		//new File("record/predators-mnr").mkdirs();
		//
		JFrame frame = new JFrame("Cyber biology 2 oxygen 2");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(new World());
		frame.setSize(1920, 1080);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setUndecorated(true);
		frame.setVisible(true);
	}

}