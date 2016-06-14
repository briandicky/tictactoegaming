import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.Scanner; 
import javax.swing.*; 

public class TicTacToeClient 
{	
	private JFrame frame = new JFrame("Tic Tac Toe");
	private JPanel boardPanel = new JPanel();
    private JLabel messageLabel = new JLabel("");
    private JButton b[] = new JButton[9];
    private JButton currentb;

    private Socket socket;
    private BufferedReader recv;
    private PrintWriter send;
    
    private char icon;
    private char opponentIcon;
    
    public TicTacToeClient(Socket socket) throws Exception {
    	/*set up socket*/
    	this.socket = socket;
    	recv = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		send = new PrintWriter(socket.getOutputStream(),true); 

        /*Layout GUI*/
        messageLabel.setBackground(Color.lightGray);
        frame.getContentPane().add(messageLabel, "South");
        frame.getContentPane().add(boardPanel, "Center");
        
        /*initialization all button*/
        for( int i = 0; i < 9; i++ )
            b[i] = new JButton("");
        
        boardPanel.setLayout(new GridLayout(3, 3));
		
        for( int i = 0; i < 9; i++ ) {
        	boardPanel.add(b[i]);
            b[i].setEnabled(true);
        }
    	
        /*if mousePressed button, then talk to server*/
        for ( int i = 0; i < 9; i++ ) {
            final int j = i;
			
            b[i].addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    currentb = b[j];
                    send.println("MOVE");
                    send.println(j);
                }
            });
			
            boardPanel.add(b[i]);
        }

        /*set window visible*/
        frame.setSize(280, 350);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    /*set all button enabled*/
    public void setAllEnable() {
    	for( int i = 0; i < 9; i++ )
            b[i].setEnabled(false);
    }
    
    /*receive response from server*/
    public void play() throws Exception {
        String response;

        response = recv.readLine();
		
        if( response.equals("WELCOME") ) {
            String mark = recv.readLine();
            
            /*set player mark*/
            if( mark.equals("O") ) {
            	icon = 'O';
            	opponentIcon = 'X';
            }
            else {
            	icon = 'X';
            	opponentIcon = 'O';
            }
            frame.setTitle("Tic Tac Toe - Player " + mark);
        }
        
        while( true ) {
            response = recv.readLine();
            
            if( response.equals("VALID_MOVE") ) { //if move is legal, set button with your mark.
                messageLabel.setText("Valid move, please wait");
                currentb.setText(String.valueOf(icon));
                currentb.setEnabled(false);
            }
            else if( response.equals("OPPONENT_MOVED") ) { //opponent move
                int locatoin = Integer.parseInt(recv.readLine());
                
                b[locatoin].setText(String.valueOf(opponentIcon));
                b[locatoin].setEnabled(false);
                
                messageLabel.setText("Opponent moved, your turn");
            } 
            else if( response.equals("VICTORY") ) { //win
                messageLabel.setText("You win");
                setAllEnable();
                break;
            }
            else if( response.equals("DEFEAT") ) { //lose
                messageLabel.setText("You lose");
                setAllEnable();
                break;
            }
            else if( response.equals("TIE") ) { //tied for the game
                messageLabel.setText("Tie");
                break;
            }
            else if( response.equals("MESSAGE") ) { //other message
            	String msg = recv.readLine();
                messageLabel.setText(msg);
            }
        }
		
        send.println("QUIT");  //finished game
    }
    
    /*want to play again?*/
    boolean wantsToPlayAgain() {
        int response = JOptionPane.showConfirmDialog(frame, "Want to play again?", " ", JOptionPane.YES_NO_OPTION);
        frame.dispose();
        return response == JOptionPane.YES_OPTION;
    }

	public static void main(String[] args) throws Exception {
		Scanner scanner = new Scanner(System.in);
		
		/*set up connection*/
		Socket socket = new Socket("127.0.0.1", 5678);
		BufferedReader recv = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	    PrintWriter send = new PrintWriter(socket.getOutputStream(),true);
	    
	    /*input username*/
	    System.out.print("User name: ");
	    String name = scanner.next();
	    send.println(name);
	    
	    String W = null, L = null;
	    int check = 0;
	
		while( true ) {
			/*receive opponent record*/
			if( check == 0 ) {
				name = recv.readLine();
				W = recv.readLine();
				L = recv.readLine();
			}
			
			for( int i = 0; i < 16; i++ )
				System.out.printf("\n");
			
			System.out.println("********************************************************************************");
			System.out.println("                               waiting room                                     ");
			System.out.println("********************************************************************************");
			System.out.println("Opponent: " + name + " (win:" + W + " lose:" + L + ")");
			
			System.out.println("Command: 1.VS Player  2.VS CPU  3.bye");
			String command = scanner.next();
			
			if( command.equals("1") ) { //VS Player
				send.println("start");
				
				TicTacToeClient client = new TicTacToeClient(socket);

				client.play();
				
		        if ( !client.wantsToPlayAgain() ) {
					socket.close();
					System.exit(0);
				}
				check = 0;
			}
			else if( command.equals("2") ) { //compete V.S CPU
				TicTacToeAI temp = new TicTacToeAI();
				temp.play();
				check = 1;
			}
			else if( command.equals("3") ) { //bye
				send.println("bye");
				
				socket.close();
				System.exit(0);
			}
		}
	}
}