import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
 
public class TicTacToeAI 
{	
	private JFrame frame = new JFrame("Tic Tac Toe");
	private JPanel boardPanel = new JPanel();
	public JLabel messageLabel = new JLabel("");
    public JButton b[] = new JButton[9];

    private myAction action;
    public boolean isAITurn = false;
	
    public char[] table = {' ', ' ', ' ',' ', ' ', ' ',' ', ' ', ' '};
	private boolean resultGameFinished = false;
	public int numberOfBoxesLeft = 9;
    
    public TicTacToeAI() {
    	action = new myAction(this);
    	
        /*Layout GUI*/
        messageLabel.setBackground(Color.lightGray);
        frame.getContentPane().add(messageLabel, "South");
        frame.getContentPane().add(boardPanel, "Center");
        
        /*initialization all button*/
        for( int i = 0; i < 9; i++ )
            b[i] = new JButton("");
        
        boardPanel.setLayout(new GridLayout(3, 3));
    }
    
    /*check whether you can chose this button*/
    public boolean legalMove(int location) {
        if( table[location] == ' ' )
        {
        	numberOfBoxesLeft--;
        	table[location] = 'X';
            
            return true;
        }
        return false;
    }
    
    /*set all button enabled*/
    public void setAllEnable() {
    	for( int i = 0; i < 9; i++ )
            b[i].setEnabled(false);
    }
    
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
	
    /*want to play again?*/
	boolean wantsToPlayAgain() {
        int response = JOptionPane.showConfirmDialog(frame, "Want to play again?", " ", JOptionPane.YES_NO_OPTION);
        frame.dispose();
        return response == JOptionPane.YES_OPTION;
    }
    
	/*start game*/
    public void play() {
    	for( int i = 0; i < 9; i++ ) {
        	boardPanel.add(b[i]);
        	b[i].addActionListener(action);
            b[i].setEnabled(true);
        }
        
        messageLabel.setText("Your move");
        
        frame.setSize(280, 350);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		while(true) {
			if( resultGameFinished == true ) {
				if ( !wantsToPlayAgain() )
					System.exit(0);
				else
					break;
			}
			else
				System.out.println("");
		}
    }
}