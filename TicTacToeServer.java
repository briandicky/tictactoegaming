import java.io.*;
import java.net.*;
import java.util.concurrent.locks.*;

public class TicTacToeServer 
{	
	static Socket client1;
	static Socket client2;
	static int Client1W = 0, Client1L = 0, Client2W = 0, Client2L = 0;
	static int signal = 0;
	static Lock lock = new ReentrantLock();
	static Condition threadCond = lock.newCondition();
	
	public static void main(String[] args) throws InterruptedException {		
		BufferedReader recvClient1, recvClient2;
	    PrintWriter sendClient1 = null, sendClient2 = null;
	    String nameClient1 = null, nameClient2 = null;
		ServerSocket server = null;
		
		try {
			server = new ServerSocket(5678);
			System.out.println("Tic Tac Toe Server is Running");
			
			/* wait for client1 to connect*/
			client1 = server.accept();
			recvClient1 = new BufferedReader(new InputStreamReader(client1.getInputStream()));
			sendClient1 = new PrintWriter(client1.getOutputStream(),true);
			nameClient1 = recvClient1.readLine();
			new ServerThread(client1);
			
			/* wait for client2 to connect*/
			client2 = server.accept();
			recvClient2 = new BufferedReader(new InputStreamReader(client2.getInputStream()));
			sendClient2 = new PrintWriter(client2.getOutputStream(),true);
			nameClient2 = recvClient2.readLine();
			new ServerThread(client2);
		} catch (IOException e1) {
			/*do nothing*/
		}
        
        try {
            while( true ) {
            	/*send client2 record to client1*/
            	sendClient1.println(nameClient2);
            	sendClient1.println(Client2W);
            	sendClient1.println(Client2L);
            	
            	/*send client1 record to client2*/
            	sendClient2.println(nameClient1);
            	sendClient2.println(Client1W);
            	sendClient2.println(Client1L);
            	
            	Game game = new Game();
            	Game.Player playerX = null;
    			Game.Player playerO = null;
            	
    			/*if client1 input start, set client1 mark = X*/
    			lock.lock();
    			while( signal != 1 )
    				threadCond.await();
    			
				playerX = game.new Player(client1, 'X');
    			lock.unlock();
    			
    			/*if client2 input start, set client2 mark = O*/
    			lock.lock();
    			while( signal != 2 )
    				threadCond.await();
    			
				playerO = game.new Player(client2, 'O');
    			lock.unlock();

    			/*set each opponent, then client1 go move first*/
				playerX.setOpponent(playerO);
				playerO.setOpponent(playerX);
				game.currentPlayer = playerX;
				
				/*start game with thread*/
				playerX.start();
				playerO.start();
            	
				while( Thread.activeCount() > 3 );
				
				signal = 0;
            } 
        } finally {
        	try {
				server.close();
			} catch (IOException e) {
				/*do nothing*/
			}
        }
	}
}