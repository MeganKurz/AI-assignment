
/*  Agent.java  
 *  Sample Agent for Text-Based Adventure Game
 *  COMP3411 Artificial Intelligence
 *  UNSW Session 1, 2017
*/
import java.util.*;
import java.io.*;
import java.net.*;

public class Agent {
	static int dynHeld = 0;
	static boolean key = false;
	static boolean axe = false;
	static boolean raft = false;
	static boolean gold = false;
	static Position lefttop = null;
	static Position righttop = null;
	static Position leftbottom = null;
	static Position rightbottom = null;
	static Position current = null;
	static Position infront = null;
	static Position treasure = null;
	static char lastMove = 0;

	private static int dirn;
	//EAST = 0;
	//NORTH = 1;
	//WEST = 2;
	//SOUTH = 3;
	
	private static char[][] map = new char[155][155];

	boolean on_raft = false;
	private Random random = new Random();

	public char get_action(char map[][]) {
		char action = 0;
		char ch = 0;
		ch = map[infront.x][infront.y];

		// while agent hasn't found the treasure randomly walk around
		if(treasure == null) {

			if (ch == 'T' && axe) {
				action = 'C';
			}
			// if (ch == 'T' && !(axe) && dynHeld>=1){
			// action = 'B';
			// }
			else if (ch == '-' && key) {
				action = 'U';
			}
			// if (ch == '-' && !(key) && dynHeld>=1){
			// action = 'B';
			// }
			// if (ch == '*' && dynHeld>=1){
			// action = 'B';
			// }
			else if ((ch == '~' && !raft)||ch == '.') {
				char[] commands = new char[] { 'L', 'R' };
				action = commands[random.nextInt(commands.length)];
			} else if (ch == ' ') {
				char[] commands = new char[] { 'F', 'L', 'R' };
				// 2 F's to put equal weight on moving forward and turning
				action = commands[random.nextInt(commands.length)];

			} else {
				char[] commands = new char[] { 'L', 'R' };
				action = commands[random.nextInt(commands.length)];
			}

	}
		else action = 'F'; 
		return action;
	}

	void print_view(char view[][]) {
		int i, j;

		System.out.println("\n+-----+");
		for (i = 0; i < 5; i++) {
			System.out.print("|");
			for (j = 0; j < 5; j++) {
				if ((i == 2) && (j == 2)) {
					System.out.print('^');
				} else {
					System.out.print(view[i][j]);
				}
			}
			System.out.println("|");
		}
		System.out.println("+-----+");
	}

	void print_map(char map[][]) {
		int i, j;
		for (i = 0; i < 155; i++) {
			System.out.print("|");
			for (j = 0; j < 155; j++) {
				if ((i == current.x) && (j == current.y)) {
					System.out.print('^');
				} 
				else {
					System.out.print(map[i][j]);
				}
			}
			System.out.println("|");
		}

	}

	public static void main(String[] args) {
		InputStream in = null;
		OutputStream out = null;
		Socket socket = null;
		Agent agent = new Agent();
		char view[][] = new char[5][5];
		char action = 'F';
		int port;
		int ch;
		int i, j;

		for (int k = 0; k < 155; k++) {
			for (int z = 0; z < 155; z++) {
				map[k][z] = 0;
			}
		}

		if (args.length < 2) {
			System.out.println("Usage: java Agent -p <port>\n");
			System.exit(-1);
		}

		port = Integer.parseInt(args[1]);

		try { // open socket to Game Engine
			socket = new Socket("localhost", port);
			in = socket.getInputStream();
			out = socket.getOutputStream();
		} catch (IOException e) {
			System.out.println("Could not bind to port: " + port);
			System.exit(-1);
		}

		try { // scan 5-by-5 window around current location
			while (true) {
				for (i = 0; i < 5; i++) {
					for (j = 0; j < 5; j++) {
						if (!((i == 2) && (j == 2))) {
							ch = in.read();
							if (ch == -1) {
								System.exit(-1);
							}
							view[i][j] = (char) ch;
						}
					}
				}
				agent.print_view(view); // COMMENT THIS OUT BEFORE SUBMISSION
				
				
				if (lastMove == 0) {
					dirn = 1;
					current = new Position(77, 77);
					infront = new Position(76, 77);
					for (i = 0; i < 5; i++) {
						for (j = 0; j < 5; j++) {
							map[75 + i][75 + j] = view[i][j];
							if (view[i][j] == '$') {
								treasure = new Position(i, j);
							}
						}
					}
					lefttop = new Position(75, 75);
					righttop = new Position(75, 79);
					leftbottom = new Position(79, 75);
					rightbottom = new Position(79, 79);
					map[77][77] = 'p';

				} else {
					int[] dirAdd = getDirNum(dirn);
					Position holder = null;
					if (lastMove == 'F') {
						int x = lefttop.x;
						int y = lefttop.y;
						int[] mult = dirMult(dirn);
						if (dirn == 0 || dirn == 2) {
							for (i = 0; i < 5; i++) {
								map[x + (i*mult[0])][y + (mult[1])] = view[0][i];
								if (view[0][i] == '$') {
									treasure = new Position(x + (i*mult[0]), y + (1*mult[1]));
								}
							}
							lefttop.setPosition(x, y + (mult[1]));
							righttop.setPosition(x + (4*mult[0]), y + (mult[1]));
							rightbottom.setPosition(x + (4*mult[0]), y - (3*mult[1]));
							leftbottom.setPosition(x, y - (3*mult[1]));
							current.setPosition(x + (2*mult[0]), y - (mult[1]));
							infront.setPosition(x + (2*mult[0]), y);
							
						}
					else {
							for (j = 0; j < 5; j++) {
								map[x + (mult[0])][y + (j*mult[1])] = view[0][j];
								if (view[0][j] == '$') {
									treasure = new Position(x + (mult[0]), y + (j*mult[1]));
								}
							}
							lefttop.setPosition(x + (mult[0]), y);
							righttop.setPosition(x + (mult[0]), y + (4*mult[1]));
							rightbottom.setPosition(x - (3*mult[0]), y + (4*mult[1]));
							leftbottom.setPosition(x - (3*mult[0]), y);
							current.setPosition(x - (mult[0]), y + (2*mult[1]));
							infront.setPosition(x, y + (2*mult[1]));
					}

						x = current.x;
						y = current.y;
						if (map[x][y] == 'k') {
							map[x][y] = ' ';
							key = true;
						}
						else if (map[x][y] == 'a') {
							map[x][y] = ' ';
							axe = true;
						}
						else if (map[x][y] == 'd') {
							map[x][y] = ' ';
							dynHeld++;
						}
						else if (map[x][y] == '$') {
							map[x][y] = ' ';
							gold = true;
						}
						else if (map[x][y] == 'T' || map[x][y] == '-' || map[x][y] == '*') {
							map[x][y] = ' ';
						}
						else if (map[x][y] == ' ' && map[x][y] == '~') {
							raft = false;
						}

					} else if (lastMove == 'R') {
						int x = current.x;
						int y = current.y;
						dirn = (dirn + 3) % 4;
						holder = lefttop;
						lefttop = righttop;
						righttop = rightbottom;
						rightbottom = leftbottom;
						leftbottom = holder;
						infront.setPosition(x+dirAdd[0], y+dirAdd[1]);
						
					} else if (lastMove == 'L') {
						int x = current.x;
						int y = current.y;
						dirn = (dirn + 1) % 4;
						holder = lefttop;
						lefttop = leftbottom;
						leftbottom = rightbottom;
						rightbottom = righttop;
						righttop = holder;
						infront.setPosition(x+dirAdd[0], y+dirAdd[1]);
						
					} else if (lastMove == 'C' && map[infront.x][infront.y] == 'T') {
						map[infront.x][infront.y] = ' ';
						raft = true;
					} else if (lastMove == 'B') {
						dynHeld--;
						if ((map[infront.x][infront.y] == 'T' || map[infront.x][infront.y] == '-'
								|| map[infront.x][infront.y] == '*')) {
							map[infront.x][infront.y] = ' ';
						}
					} else if (lastMove == 'U' && map[infront.x][infront.y] == '-') {
						map[infront.x][infront.y] = ' ';
					}
				}
				agent.print_map(map);
				action = agent.get_action(map);
				lastMove = action;
				out.write(action);
			}
		} catch (

		IOException e) {
			System.out.println("Lost connection to port: " + port);
			System.exit(-1);
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
			}
		}
	}
	
	//to get the block infront from th current position
	public static int[] getDirNum(int dir) {
		int[] results = new int[2];
		if (dir == 0) {
			results[0] = 0;
			results[1] = 1;

		} else if (dir == 1) {
			results[0] = -1;
			results[1] = 0;

		} else if (dir == 2) {
			results[0] = 0;
			results[1] = -1;

		} else {
			results[0] = 1;
			results[1] = 0;

		}
		return results;
	}
	
	
	public static int[] dirMult(int dir){
		int[] results = new int[2];
		if(dir == 0){
			results[0] = 1;
			results[1] = 1;
		}
		else if(dir == 2){
			results[0] = -1;
			results[1] = -1;
		}
		else if(dir == 1){
			results[0] = -1;
			results[1] = 1;
		}
		else{
			results[0] = 1;
			results[1] = -1;
		}
		
		return results;
	}
		

}

class Position {
	int x;
	int y;

	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}

	void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}

	int compareX(Position p1, Position p2) {
		int dif = p1.x - p2.x;
		return dif;
	}

	int compareY(Position p1, Position p2) {
		int dif = p1.y - p2.y;
		return dif;
	}

}