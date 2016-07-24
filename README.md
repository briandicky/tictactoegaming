# Tic-Tac-Toe gaming
It's a tic‐tac‐toe “on‐line” game written by java.

## Server side:
Every player will be executed by two threads.
First thread, ServerThread, after player connect to the server it will create a server thread for this player. Then the thread will check player's commnad continuely, if player enter commands it will responese to player and do correspond instruction.
Second thread, Player thread, after user type "start" command server will start to execute the tictactoe game. Then this thread will judge the user commands is work or not and win or lose or euqal.

## Client side:
There is two modes that could be chosen, one is p.k. to another user, the other is p.k. AI.
First mode, p.k. to another user, if player click the lattice then it will send the position to server side. Then server side will response some message, including win/lose/equal and who is playing now.
Second mode, p.k. to AI, that is designed player(USER) moving first. But it still have some problem, one of that, AI is not smart enough it will not prevent user to win the game.

## How to run this program:
First, start the cmd(Win)/terminal(Linux) then compile all *.java file by using 
```
javac *.java
```
It will create all the *.class file.

Second, create the server program by typing
```
java TicTacToeServer
```

Third, create the client program by typing
```
java TicTacToeClient
```

Final, follow the hint of program, it will be OK.
