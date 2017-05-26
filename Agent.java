
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
	//corners of the 5x5 view in relation to the whole map
	static Position lefttop = null; 
	static Position righttop = null;
	static Position leftbottom = null;
	static Position rightbottom = null;
	//current position in the whole map
	static Position current = null;
	//the block right in front of the agents current position in relation to the direction/whole map
	static Position infront = null;
	//the positions of the treasure, key, axe in the whole map
	static Position treasure = null;
	static Position seeKey = null;
	static Position seeAxe = null;
	//last action made by the agent
	static char lastMove = 0;
	private static int dirn;
	//EAST = 0;
	//NORTH = 1;
	//WEST = 2;
	//SOUTH = 3;
	//the whole map, where the views will be stored in
	private static char[][] map = new char[155][155];

	boolean on_raft = false;
	private Random random = new Random();

	public static boolean isValidMove(char map[][], int x, int y){
		boolean move = false;
		int dynholder = dynHeld;
		char ch =  map[x][y];
		
		if (ch == 'T' && axe) {
			move = true;
		}
		if (ch == '-' && key) {
			move = true;
		}
		if (ch == 'T' && !(axe) && dynHeld>=1){
		move = true;
		}
		if (ch == '-' && !(key) && dynHeld>=1){
		move = true;
		 }
		if (ch == '*' && dynHeld>=1){
		move = true;
		}
		if (ch == '~' && raft){
			move = true;
		}
		if (ch == ' ' || ch == 'a'||ch == 'd' || ch == '$'|| ch == 'k'){
			move = true;
		}
		return move;
	}
	
	public boolean reachGoal(char map[][], int x, int y, int xg, int yg){
		boolean reachable = false;
		map[x][y]
		
		return reachable; 
	}
	
	
	public char get_action(char map[][]) {
		char action = 0;
		char ch = 0;
		ch = map[infront.x][infront.y];
		//weights for moves
		int $ = 100;
		int k = 10;
		int a = 10;
		int F = -1;
		int L = -3;
		int R = -3;
		int uknown = 5;

		// while agent hasn't found the treasure randomly walk around
		if(treasure == null) {
			//plan best rout 
			/* if(seeKey != null){
				//go to key
			}
			if(seeAxe != null){
				//go to axe
			}
			*/
			
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
			} 
			else if (ch == ' ') {
				char[] commands = new char[] { 'F', 'L', 'R', 'F', 'F', 'F' };
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
	//to view the 5x5 map
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
	//initialize the whole map with 0's
		for (int k = 0; k < 155; k++) {
			for (int z = 0; z < 155; z++) {
				map[k][z] = ',';
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
				
				//if the agent is in its starting position ie the agent hasnt moved yet
				if (lastMove == 0) {
					dirn = 1; //let his starting position be north
					current = new Position(77, 77); //start the agent in the center of the 155x155 map
					infront = new Position(76, 77);
					for (i = 0; i < 5; i++) {
						for (j = 0; j < 5; j++) {
							map[75 + i][75 + j] = view[i][j];
							//if agent sees the key, treasure of axe note its position in the map
							if (view[i][j] == '$')treasure = new Position(i, j);
							if (view[i][j] == 'a')seeAxe = new Position(i, j);
							if (view[i][j] == 'k')seeKey = new Position(i, j);
						}
					}//the initial corners of the view (facing north)
					lefttop = new Position(75, 75);
					righttop = new Position(75, 79);
					leftbottom = new Position(79, 75);
					rightbottom = new Position(79, 79);
				
				//if the agent has made a move ie action
				} else {
					int[] dirAdd = getDirNum(dirn); 
					Position holder = null;
					//if the agents last move was forward
					if (lastMove == 'F') {
						int x = lefttop.x; //the left top corner (of the 5x5 view) in relation to the map 
						int y = lefttop.y; //in relation to the direction of the last move that was made
						int[] mult = dirMult(dirn);
						
						//if the agent is facing east or west
						if (dirn == 0 || dirn == 2) {
							for (i = 0; i < 5; i++) {
								//load the new view into the map
								map[x + (i*mult[0])][y + (mult[1])] = view[0][i];
								//if the new view has a key, axe or treasure note the position
								if (view[0][i] == '$') treasure = new Position(x + (i*mult[0]), y + (mult[1]));
								if (view[0][i] == 'a') seeAxe = new Position(x + (i*mult[0]), y + (mult[1]));
								if (view[0][i] == 'k') seeKey = new Position(x + (i*mult[0]), y + (mult[1]));
							}
							//set the new corners of the 5x5 view in relation to the map
							lefttop.setPosition(x, y + (mult[1]));
							righttop.setPosition(x + (4*mult[0]), y + (mult[1]));
							rightbottom.setPosition(x + (4*mult[0]), y - (3*mult[1]));
							leftbottom.setPosition(x, y - (3*mult[1]));
							//set the new current position and the new block in front of the agent in relation to the map
							current.setPosition(x + (2*mult[0]), y - (mult[1]));
							infront.setPosition(x + (2*mult[0]), y);
							
						}
						
					//if the agent is facing north or south
					else {
							for (j = 0; j < 5; j++) {
								//load the new view into the map
								map[x + (mult[0])][y + (j*mult[1])] = view[0][j];
								//if the new view has a key, axe or treasure note the position
								if (view[0][j] == '$') treasure = new Position(x + (mult[0]), y + (j*mult[1]));
								if (view[0][j] == 'a') seeAxe = new Position(x + (mult[0]), y + (j*mult[1]));
								if (view[0][j] == 'k') seeKey = new Position(x + (mult[0]), y + (j*mult[1]));
								
							}
							//set the new corners of the 5x5 view in relation to the map
							lefttop.setPosition(x + (mult[0]), y);
							righttop.setPosition(x + (mult[0]), y + (4*mult[1]));
							rightbottom.setPosition(x - (3*mult[0]), y + (4*mult[1]));
							leftbottom.setPosition(x - (3*mult[0]), y);
							//set the new current position and the new block in front of the agent in relation to the map
							current.setPosition(x - (mult[0]), y + (2*mult[1]));
							infront.setPosition(x, y + (2*mult[1]));
					}
						
						x = current.x; //the new coordinates of the agents current position in the map
						y = current.y;
						//if the agent is where a key, axe, dynamite or the treasure was 
						//note that the agent now has the corresponding item 
						//and make the position empty in the map
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
						//if the agent is where a wall, door, or tree was make that spot empty now
						else if (map[x][y] == 'T' || map[x][y] == '-' || map[x][y] == '*') {
							map[x][y] = ' ';
						}
						//if the agent was on water and now on land take the raft away
						else if (map[x][y] == ' ' && map[x-dirAdd[0]][y-dirAdd[1]] == '~') {
							raft = false;
						}

					//is the agents last move was to turn right
					} else if (lastMove == 'R') {
						int x = current.x;
						int y = current.y;
						dirn = (dirn + 3) % 4; //change the direction the agent is facing
						//switch the corners of the 5x5 view to the new direction in the map
						holder = lefttop;
						lefttop = righttop;
						righttop = rightbottom;
						rightbottom = leftbottom;
						leftbottom = holder;
						//get the new position in front of the agent
						infront.setPosition(x+dirAdd[0], y+dirAdd[1]);
					
					//if the agents last move was to turn left
					} else if (lastMove == 'L') {
						int x = current.x;
						int y = current.y;
						dirn = (dirn + 1) % 4; //change the direction the agent is facing
						//switch the corners of the 5x5 view to the new direction in the map
						holder = lefttop;
						lefttop = leftbottom;
						leftbottom = rightbottom;
						rightbottom = righttop;
						righttop = holder;
						//get the new position in front of the agent
						infront.setPosition(x+dirAdd[0], y+dirAdd[1]);
					
					//if the agents last move was to cut down a tree 
					} else if (lastMove == 'C' && map[infront.x][infront.y] == 'T') {
						map[infront.x][infront.y] = ' '; 
						raft = true; //the agent has a raft now
						
					//if the agents last move was to use some dynamite
					} else if (lastMove == 'B' && dynHeld>0) {
						dynHeld--; 
						//if there was a wall, door, or tree get rid of it
						if ((map[infront.x][infront.y] == 'T' || map[infront.x][infront.y] == '-'
								|| map[infront.x][infront.y] == '*')) { 
							map[infront.x][infront.y] = ' ';
						}
					
					//if the agents last move was to unlock a door
					} else if (lastMove == 'U' && map[infront.x][infront.y] == '-') {
						map[infront.x][infront.y] = ' '; //open the door
					}
				}
				//agent.print_map(map);
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
	
	//used to get the block in front of the agent from the agents current position/direction
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
	
	//used to get the new corners of the 5x5 view and the new current position of the agent
	//from the last left corner in relation to the direction
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

//class to get and use the coordinates of the map
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