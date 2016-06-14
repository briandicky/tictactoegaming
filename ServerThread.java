import java.io.*;
import java.net.*;

public class ServerThread extends Thread 
{
	private Socket socket;
	private BufferedReader recv;
	
	public ServerThread(Socket socket) throws IOException {
		this.socket = socket;
		recv = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
		start();
	}

	public void run() {
		String command;
		
		while( true ) {
			try {
				System.out.println(" ");
				/*if players are playing game, can not receive command*/
				if( TicTacToeServer.signal == 0 ) {
					command = recv.readLine();
					if( command.equals("start") ) {
						TicTacToeServer.lock.lock();
						TicTacToeServer.signal++;
						
						TicTacToeServer.threadCond.signal();
						TicTacToeServer.lock.unlock();
					}
				}
			} catch (IOException e) {
				/*do nothing*/
			}
		}
	}
}
