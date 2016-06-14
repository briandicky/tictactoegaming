import java.io.*;
import java.net.Socket;

class Game 
{
	private char[] table = {' ', ' ', ' ',' ', ' ', ' ',' ', ' ', ' '};
	private boolean resultGameFinished = false;
    private int numberOfBoxesLeft = 9;
    Player currentPlayer;

    /*test all possible outcomes*/
    public boolean Winner() {
    	for( int i = 0; i <= 6; i++ ) {
    		if( resultGameFinished == false ) {
				if(i == 0)
					checkWin(i, i + 4, i + 8);
				if(i <= 2)
					checkWin(i, i + 3, i + 6);
				if( i == 0 || i == 3 || i == 6 )
					checkWin(i, i + 1, i + 2);
				if( i == 2 )
					checkWin(i, i + 2, i + 4);
			}
    	}
    	return resultGameFinished;
    }
    
    /*check whether win*/
    public void checkWin(int x, int y, int z) {
		if( table[x] == 'X' && table[y] == 'X' && table[z] == 'X' )
			resultGameFinished = true;

		if( table[x] == 'O' && table[y] == 'O' && table[z] == 'O' )
			resultGameFinished = true;
	}

    /*check all buttons are selected over*/
    public boolean tableFilledUp() {
    	if( numberOfBoxesLeft ==  0 )
    		return true;
    	else
    		return false;
    }

    /*check whether you can chose this button*/
    public boolean legalMove(int location, Player player) {
        if( player == currentPlayer && table[location] == ' ' ) {
        	numberOfBoxesLeft--;
        	table[location] = player.mark;
        	
            currentPlayer = currentPlayer.opponent;
            currentPlayer.otherPlayerMoved(location);
            
            return true;
        }
        return false;
    }

    public class Player extends Thread {
        char mark;
        Player opponent;
        Socket socket;
        BufferedReader recv;
        PrintWriter send;

        /*constructor*/
        public Player(Socket socket, char mark) {
            this.socket = socket;
            this.mark = mark;
            
            try {
            	recv = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                send = new PrintWriter(socket.getOutputStream(),true);

                send.println("WELCOME");
                send.println(mark);
                
                send.println("MESSAGE");
                send.println("Waiting for opponent to connect");
            } catch (IOException e) {
                System.out.println("Player died: " + e);
            }
        }

        /*set opponent*/
        public void setOpponent(Player opponent) {
            this.opponent = opponent;
        }

        public void otherPlayerMoved(int location) {
        	send.println("OPPONENT_MOVED");
        	send.println(location);
        	
        	if( Winner() ) {
        		send.println("DEFEAT");
        		if( socket.equals(TicTacToeServer.client1) )
        			TicTacToeServer.Client1L++;
        		else
        			TicTacToeServer.Client2L++;
        	}
        	if( tableFilledUp() )
        		send.println("TIE");
        }

        public void run() {
            try {
            	send.println("MESSAGE");
            	send.println("All players connected");

                //tell the first player that it is her turn.
                if( mark == 'X' ) {
                	send.println("MESSAGE");
                	send.println("Your move");
                }

                while( true ) {
                    String command = recv.readLine();
                    if( command.equals("MOVE") ) { //player move 
                        int location = Integer.parseInt(recv.readLine());
                        if( legalMove(location, this) ) { //check whether is legal 
                        	send.println("VALID_MOVE");
                        	
                        	/*check whether win*/
                        	if( Winner() ) {
                        		send.println("VICTORY");
                            	if( socket.equals(TicTacToeServer.client1) )
                        			TicTacToeServer.Client1W++;
                        		else
                        			TicTacToeServer.Client2W++;
                        	}
                        	
                        	/*check whether tie*/
                        	if( tableFilledUp() )
                        		send.println("TIE");
                        }
                        else { //move illegal
                        	send.println("MESSAGE");
                        	send.println("?");
                        }
                    }
                    else if( command.equals("QUIT") )  //finished game
                        return;
                }
            } catch (IOException e) {
                System.out.println("Player died: " + e);
            }
        }
    }
}