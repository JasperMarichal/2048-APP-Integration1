package be.kdg.integration1.team01.game2048.model;

import static be.kdg.integration1.team01.game2048.manager.WireframesManager.displayWireframe;

import java.util.ArrayList;
import java.util.Scanner;

public class Game {
    private int currentScore;
    private ArrayList<Turn> turns;
    private Board board;
    private Player currentPlayer;
    private boolean running;

    public Game(int boardSize, Player player) {
        this.currentScore = 0;
        this.turns = new ArrayList<Turn>();
        this.board = new Board(boardSize);
        this.running = false;
        setCurrentPlayer(player);
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public int getCurrentScore() {return currentScore;}
    public void setCurrentScore(int currentScore) {this.currentScore = currentScore;}

    public ArrayList<Turn> getTurns() {
        return turns;
    }

    public void setTurns(ArrayList<Turn> turns) {
        this.turns = turns;
    }

    public Board getBoard() {return board;}
    public void setBoard(Board board) {this.board = board;}

    public boolean isRunning() {
        return running;
    }

    public void makeMove(Direction slideDirection) {
        //TODO: update board, end turn etc.
    }

    public int play() {
        Scanner keyboard = new Scanner(System.in);

        String command;

        displayWireframe(Wireframe.GAMEBOARD, this); // Show initial board state

        running = true;
        do {
            System.out.print("> ");
            command = keyboard.nextLine().toUpperCase();
            processCommand(command);

            if (running) {
                displayWireframe(Wireframe.GAMEBOARD, this);
            }
        }while (running);
        // Print game results
        System.out.printf("Thanks for playing, %s!\nEnd score: %d\nTime: %d seconds\n", getCurrentPlayer().getName(), getCurrentScore(), 200);

        //TODO: calculate time, save score when leaderboard is implemented
        System.out.print("Do you wish to save this score? (y/N) ");
        if(keyboard.nextLine().equalsIgnoreCase("Y")) {
            //TODO: save score
        }

        //Ask player if they want to play again unless they already entered the new game command
        if(command.equalsIgnoreCase("N")) {
            return 1;
        }else {
            System.out.print("Do you want to play again? (y/N) ");
            if(keyboard.nextLine().equalsIgnoreCase("Y")) {
                return 2;
            }
            return 0;
        }
    }

    public void processCommand(String command) {
        if (command == null || command.isEmpty()) return;
        switch (command.toUpperCase()) {
            case "Q", "N" -> running = false;
            case "L" -> processCommand(displayWireframe(Wireframe.LEADERBOARD, this, true));
            case "H" -> processCommand(displayWireframe(Wireframe.COMMANDS, this, true));
            case "R" -> processCommand(displayWireframe(Wireframe.RULES, this, true));
            case "0", "W", "UP" -> makeMove(Direction.UP);
            case "1", "D", "RIGHT" -> makeMove(Direction.RIGHT);
            case "2", "S", "DOWN" -> makeMove(Direction.DOWN);
            case "3", "A", "LEFT" -> makeMove(Direction.LEFT);
            default -> System.err.println("Invalid command, type 'H' to see the list of commands.");
        }
    }
}
