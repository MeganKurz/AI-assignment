
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
	static char lastMove = 0;
	final static int EAST = 0;
	final static int NORTH = 1;
	final static int WEST = 2;
	final static int SOUTH = 3;
	private static int dirn;
	private static char[][] map = new char[155][155];

	boolean on_raft = false;
	private Random random = new Random();

	public char get_action(char view[][]) {
		char action = 0;
		char ch = 0;
		// TreeMap<Character, Integer> possMoves = new TreeMap<Character,
		// Integer>();
		// legal moves

		ch = view[1][2];

		if (ch == 'T' && axe) {
			action = 'C';
		}
		// if (ch == 'T' && !(axe) && dynHeld>=1){
		// action = 'B';
		// dynHeld--;
		// }
		else if (ch == '-' && key) {
			action = 'U';
		}
		// if (ch == '-' && !(key) && dynHeld>=1){
		// action = 'B';
		// dynHeld--;
		// }
		// if (ch == '*' && dynHeld>=1){
		// action = 'B';
		// dynHeld--;
		// }
		else if (ch == '~' && !raft) {
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
				int[] additives = getDirNum(dirn);
				int addx = additives[0];
				int addy = additives[1];
				if (lastMove == 0) {
					dirn = 1;
					current = new Position(77, 77);
					for (i = 0; i < 5; i++) {
						for (j = 0; j < 5; j++) {
							map[75 + i][75 + j] = view[i][j];
							if (view[i][j] == '$') {
								Position gold = new Position(i, j);
							}
						}
					}
					lefttop = new Position(75, 75);
					righttop = new Position(75, 79);
					leftbottom = new Position(79, 75);
					rightbottom = new Position(79, 79);
					map[77][77] = 'p';
					Position finish = new Position(77, 77);
				} else {
					Position holder = null;
					if (lastMove == 'F') {
						int x = lefttop.x;
						int y = lefttop.y;
						if (dirn == 0) {
							for (i = 0; i < 5; i++) {
								map[x + i][y + 1] = view[0][i];
								if (view[0][i] == '$') {
									Position gold = new Position(x + i, y + 1);
								}
							}
							lefttop.setPosition(x, y + 1);
							righttop.setPosition(x + 4, y + 1);
							rightbottom.setPosition(x + 4, y - 3);
							leftbottom.setPosition(x, y - 3);
							current.setPosition(x + 2, y - 1);
						} else if (dirn == 1) {
							for (j = 0; j < 5; j++) {
								map[x - 1][y + j] = view[0][j];
								if (view[0][j] == '$') {
									Position gold = new Position(x - 1, y + j);
								}
							}
							lefttop.setPosition(x - 1, y);
							righttop.setPosition(x - 1, y + 4);
							rightbottom.setPosition(x + 3, y + 4);
							leftbottom.setPosition(x + 3, y);
							current.setPosition(x + 1, y + 2);
						} else if (dirn == 2) {
							for (i = 0; i < 5; i++) {
								map[x - i][y - 1] = view[0][i];
								if (view[0][i] == '$') {
									Position gold = new Position(x - i, y - 1);
								}
							}
							lefttop.setPosition(x, y - 1);
							righttop.setPosition(x - 4, y - 1);
							rightbottom.setPosition(x - 4, y + 3);
							leftbottom.setPosition(x, y + 3);
							current.setPosition(x - 2, y + 1);
						} else if (dirn == 3) {
							for (j = 0; j < 5; j++) {
								map[x + 1][y - j] = view[0][j];
								if (view[0][j] == '$') {
									Position gold = new Position(x + 1, y - j);
								}
							}
							lefttop.setPosition(x + 1, y);
							righttop.setPosition(x + 1, y - 4);
							rightbottom.setPosition(x - 3, y - 4);
							leftbottom.setPosition(x - 3, y);
							current.setPosition(x - 1, y - 2);
						}

						x = current.x;
						y = current.y;
						if (map[x][y] == 'k') {
							map[x][y] = ' ';
							key = true;
						}
						if (map[x][y] == 'a') {
							map[x][y] = ' ';
							axe = true;
						}
						if (map[x][y] == 'd') {
							map[x][y] = ' ';
							dynHeld++;
						}
						if (map[x][y] == '$') {
							map[x][y] = ' ';
							gold = true;
						}
						if (map[x][y] == 'T' || map[x][y] == '-' || map[x][y] == '*') {
							map[x][y] = ' ';
						}
						if (map[x][y] == ' ' && map[x][y] == '~') {
							raft = false;
						}

					} else if (lastMove == 'R') {
						dirn = (dirn + 3) % 4;
						holder = lefttop;
						lefttop = righttop;
						righttop = rightbottom;
						rightbottom = leftbottom;
						leftbottom = holder;
					} else if (lastMove == 'L') {
						dirn = (dirn + 1) % 4;
						holder = lefttop;
						lefttop = leftbottom;
						leftbottom = rightbottom;
						rightbottom = righttop;
						righttop = holder;
					} else if (lastMove == 'C' &&  map[current.x+addx][current.y+addy] == 'T') {
						map[current.x + addx][current.y + addy] = ' ';
						raft = true;
					} else if (lastMove == 'B' && ( map[current.x+addx][current.y+addy] == 'T'
							|| map[current.x+addx][current.y+addy] == '-' ||  map[current.x+addx][current.y+addy] == '*')) {
						dynHeld--;
						map[current.x + addx][current.y + addy] = ' ';

					} else if (lastMove == 'U' && map[current.x + addx][current.y + addy] == '-') {
							map[current.x+addx][current.y+addy] = ' ';
						}
				}

				action = agent.get_action(view);
				lastMove = action;
				out.write(action);
			}
		} catch (IOException e) {
			System.out.println("Lost connection to port: " + port);
			System.exit(-1);
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
			}
		}
	}


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