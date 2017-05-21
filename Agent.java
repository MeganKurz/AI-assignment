/*  Agent.java 
 *  Sample Agent for Text-Based Adventure Game
 *  COMP3411 Artificial Intelligence
 *  UNSW Session 1, 2017
*/

import java.util.*;
import java.io.*;
import java.net.*;

public class Agent {
	int dynHeld = 0;
	boolean key = false;
	boolean axe = false;
	boolean raft = false;
	boolean gold = false;
	boolean on_raft = false;
	boolean treasure= false;
	
	private char[][] map = new char[155][155];

   public char get_action( char view[][] ) {
	 char action = 0;
	 char ch= 0;
	//TreeMap<Character, Integer> possMoves = new TreeMap<Character, Integer>();
	   //legal moves
	 
	 
			 ch=view[1][2];	
			 
			 if (ch == 'T' && axe){
				  action = 'C';
			   }
			   //if (ch == 'T' && !(axe) && dynHeld>=1){
			   //   action = 'B';
			   //   dynHeld--;
			   // }
			   if (ch == '-' && key){
				  action = 'U'; 
			      }
			  // if (ch == '-' && !(key) && dynHeld>=1){
			  //   action = 'B';
			  //	   dynHeld--;
			  // }
			  // if (ch == '*' &&  dynHeld>=1){
			 //	   action = 'B';
			 //	   dynHeld--;
			 // }
			   if (ch == '~' && raft){
				   action = 'F';			   
			   }
			   if (ch == ' '){
				   action = 'F';
			   } 
			   else action ='L';
		
	   
	   
	   return action;
   }

   void print_view( char view[][] )
   {
      int i,j;

      System.out.println("\n+-----+");
      for( i=0; i < 5; i++ ) {
         System.out.print("|");
         for( j=0; j < 5; j++ ) {
            if(( i == 2 )&&( j == 2 )) {
               System.out.print('^');
            }
            else {
               System.out.print( view[i][j] );
            }
         }
         System.out.println("|");
      }
      System.out.println("+-----+");
   }

   public static void main( String[] args )
   {
      InputStream in  = null;
      OutputStream out= null;
      Socket socket   = null;
      Agent  agent    = new Agent();
      char   view[][] = new char[5][5];
      char   action   = 'F';
      int port;
      int ch;
      int i,j;
      char lastMove = 0;
      
      if( args.length < 2 ) {
         System.out.println("Usage: java Agent -p <port>\n");
         System.exit(-1);
      }

      port = Integer.parseInt( args[1] );

      try { // open socket to Game Engine
         socket = new Socket( "localhost", port );
         in  = socket.getInputStream();
         out = socket.getOutputStream();
      }
      catch( IOException e ) {
         System.out.println("Could not bind to port: "+port);
         System.exit(-1);
      }

      try { // scan 5-by-5 window around current location
         while( true ) {
            for( i=0; i < 5; i++ ) {
               for( j=0; j < 5; j++ ) {
                  if( !(( i == 2 )&&( j == 2 ))) {
                     ch = in.read();
                     if( ch == -1 ) {
                        System.exit(-1);
                     }
                     view[i][j] = (char) ch;
                  }
               }
            }
            agent.print_view( view ); // COMMENT THIS OUT BEFORE SUBMISSION
            if(lastMove == 0){
            	for( i=0; i < 5; i++ ) {
                    for( j=0; j < 5; j++ ) {
                       if( !(( i == 2 )&&( j == 2 ))) {
                          
                       }
                    }
                 }
            }
            action = agent.get_action( view );
            out.write( action );
         }
      }
      catch( IOException e ) {
         System.out.println("Lost connection to port: "+ port );
         System.exit(-1);
      }
      finally {
         try {
            socket.close();
         }
         catch( IOException e ) {}
      }
   }
}


class Position{
	int x;
	int y;

public Position(int x,int y){
	this.x = x;
	this.y = y;
}
	
void setPosition(int x, int y){
	this.x = x;
	this.y = y;
}

int compareX(Position p1, Position p2){
	int dif = p1.x - p2.x;
	return dif;
}

int compareY(Position p1, Position p2){
	int dif = p1.y - p2.y;
	return dif;
}

}