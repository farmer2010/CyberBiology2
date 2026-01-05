package sct;
import java.io.IOException;
import java.util.HashMap;

public class Compilator {
	static HashMap<String, int[]> command_length = new HashMap<>();//0 - номер команды, 1 - параметры, 2 - переходы
	String[] console = new String[] {" ", " ", " ", " ", " "};
	//
	public Compilator() {
		command_length.put("rotate",           new int[] {23, 1, 0});
		command_length.put("change_rotate",    new int[] {24, 1, 0});
		command_length.put("pht",              new int[] {25, 0, 0});
		command_length.put("move",             new int[] {26, 1, 0});
		command_length.put("move_abs",         new int[] {27, 0, 0});
		command_length.put("attack",           new int[] {28, 1, 0});
		command_length.put("attack_abs",       new int[] {29, 0, 0});
		command_length.put("see",              new int[] {30, 1, 5});
		command_length.put("see_abs",          new int[] {31, 0, 5});
		command_length.put("give",             new int[] {34, 1, 0});
		command_length.put("give_abs",         new int[] {35, 0, 0});
		command_length.put("my_enr",           new int[] {36, 1, 2});
		command_length.put("my_mnr",           new int[] {37, 1, 2});
		command_length.put("mnr",              new int[] {38, 0, 0});
		command_length.put("pht+",             new int[] {39, 0, 2});
		command_length.put("mnr+",             new int[] {40, 0, 2});
		command_length.put("multiply",         new int[] {41, 1, 0});
		command_length.put("multiply_abs",     new int[] {42, 0, 0});
		command_length.put("my_xpos",          new int[] {43, 1, 2});
		command_length.put("my_ypos",          new int[] {44, 1, 2});
		command_length.put("my_age",           new int[] {45, 1, 2});
		command_length.put("unif_distrib",     new int[] {46, 1, 0});
		command_length.put("unif_distrib_abs", new int[] {47, 0, 0});
		command_length.put("goto",             new int[] {48, 0, 1});
	}
	//
	public int[] compilate(String code) {
		console = new String[] {" ", " ", " ", " ", " "};
		//
		String[] lines = code.split("\n");
		//
		HashMap<String, Integer> transitions = new HashMap<>();
		//
		boolean error = false;
		//
		int ind = 0;
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i].split(";")[0];
			String[] l = line.split(" ");
			//
			if (l[0].equals("point")) {
				transitions.put(l[1], ind);
			}else {
				if (command_length.containsKey(l[0])) {
					ind += 1 + command_length.get(l[0])[1] + command_length.get(l[0])[2];
					if (l.length - 1 != command_length.get(l[0])[1] + command_length.get(l[0])[2]) {
						console[0] = "Error in line " + String.valueOf(i) + ":";
						console[1] = line;
						String h = "";
						for (int k = 0; k < line.length() - 1; k++) {
							h += " ";
						}
						console[2] =  h + "^";
						console[3] = "Command '" + l[0] + "' has " + String.valueOf(command_length.get(l[0])[1] + command_length.get(l[0])[2]) + " parameters,";
						console[4] = "but " + String.valueOf(l.length - 1) + " were given";
						error = true;
						break;
					}
				}else {
					console[0] = "Error in line " + String.valueOf(i) + ":";
					console[1] = line;
					console[2] = "^";
					console[3] = "No command named '" + l[0] + "'";
					error = true;
					break;
				}
			}
		}
		//
		int genome[] = new int[64];
		//
		ind = 0;
		for (int i = 0; i < lines.length; i++) {
			if (!error) {
				String line = lines[i].split(";")[0];
				String[] l = line.split(" ");
				if (!l[0].equals("point")) {
					genome[ind] = command_length.get(l[0])[0];
					for (int j = 0; j < command_length.get(l[0])[1]; j++) {
						try {
							genome[ind + 1 + j] = Integer.parseInt(l[j + 1]);
						}catch(NumberFormatException e) {
							console[0] = "Error in line " + String.valueOf(i) + ":";
							console[1] = line;
							String h = "";
							int f = 0;
							for (int g = 0; g < j + 1; g++) {
								f += l[g].length() + 1;
							}
							for (int k = 0; k < f; k++) {
								h += " ";
							}
							console[2] = h + "^";
							console[3] = "This parameter must be an Integer,";
							console[4] = "not String";
							error = true;
							break;
						}
					}
					for (int j = 0; j < command_length.get(l[0])[2]; j++) {
						if (transitions.containsKey(l[j + 1 + command_length.get(l[0])[1]])) {
							genome[ind + 1 + j + command_length.get(l[0])[1]] = transitions.get(l[j + 1 + command_length.get(l[0])[1]]);
						}else {
							console[0] = "Error in line " + String.valueOf(i) + ":";
							console[1] = line;
							String h = "";
							int f = 0;
							for (int g = 0; g < j + 1; g++) {
								f += l[g].length() + 1;
							}
							for (int k = 0; k < f; k++) {
								h += " ";
							}
							console[2] = h + "^";
							console[3] = "No transition named '" + l[j + 1 + command_length.get(l[0])[1]] + "'";
							error = true;
							break;
						}
					}
					ind += 1 + command_length.get(l[0])[1] + command_length.get(l[0])[2];
				}
			}else {
				break;
			}
		}
		//
		if (!error) {
			console[0] = "File successfully compilated";
			console[1] = "Length: " + String.valueOf(ind) + "/64";
		}else {
			genome = new int[64];
		}
		//
		return(genome);
	}
}
