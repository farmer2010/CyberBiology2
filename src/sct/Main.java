package sct;
import javax.swing.*;
//import java.awt.Dimension;

public class Main{
	public static void main(String[] args) {
		JFrame frame = new JFrame("Cyber biology 2 v1.9 unicell 2");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(new World());
		frame.setSize(1920, 1080);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setUndecorated(true);
		frame.setVisible(true);
	}
}