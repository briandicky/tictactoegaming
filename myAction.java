import java.awt.event.*;
import java.util.*;

public class myAction implements ActionListener 
{
	private TicTacToeAI Game;

	/*constructor*/
    public myAction(TicTacToeAI game) {
    	Game = game;
    }

	public void actionPerformed(ActionEvent e) {
		Game.numberOfBoxesLeft--;
		
		/*player choose one button, then set that button to X*/
		for( int i = 0; i < 9; i++ ) {
            if( e.getSource().equals(Game.b[i]) ) {
            	Game.b[i].setText("X");
            	Game.b[i].setEnabled(false);
            	Game.table[i] = 'X';
                Game.isAITurn = true;
            }
        }
		
		/*check whether win*/
		if( Game.Winner() ) {
			Game.messageLabel.setText("You win");
			Game.setAllEnable();
			return;
		}
        
		/*check whether tie*/
    	if( Game.tableFilledUp() ) {
    		Game.messageLabel.setText("Tie");
        	return;
    	}
    	
    	/*AI move*/
    	Random random = new Random();
    	while( true ) {
    		int location = random.nextInt(9);
    		
			if( Game.table[location] == ' ' ) {
    			Game.b[location].setText("O");
    			Game.b[location].setEnabled(false);
                
    			Game.numberOfBoxesLeft--;
    			Game.table[location] = 'O';
                
    			Game.isAITurn = false;
                break;
    		}
    	}
    	
    	/*check whether win*/
    	if( Game.Winner() ) {
    		Game.messageLabel.setText("AI win");
    		Game.setAllEnable();
    		return;
    	}
    	
    	/*check whether tie*/
    	if( Game.tableFilledUp() ) {
    		Game.messageLabel.setText("Tie");
    		return;
    	}
    	Game.messageLabel.setText("Your move");
	}

}
