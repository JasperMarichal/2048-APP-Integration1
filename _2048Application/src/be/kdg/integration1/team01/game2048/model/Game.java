package be.kdg.integration1.team01.game2048.model;

import be.kdg.integration1.team01.game2048.manager.LeaderboardManager;
import be.kdg.integration1.team01.game2048.manager.SaveManager;

import static be.kdg.integration1.team01.game2048.manager.WireframesManager.displayWireframe;

import java.sql.Connection;
import java.time.Duration;
import java.time.LocalDateTime;
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

    public Game(int currentScore, ArrayList<Turn> turns, Board board, Player currentPlayer, boolean running) {
        this.currentScore = currentScore;
        this.turns = turns;
        this.board = board;
        this.currentPlayer = currentPlayer;
        this.running = running;
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
        //generalize board into a 2D array
        Block[][] blocksArray = board.getGeneralArray(slideDirection);
        //simulate sliding
        slideBoard(blocksArray);
        //update board
        board.updateFromGeneralArray(blocksArray, slideDirection);
        //TODO: end turn etc.
        turns.add(new Turn(slideDirection));
        board.addBlocksRandomly(2,1);
    }

    private void slideBoard(Block[][] blocksArray) {
        for (int col = 0; col < board.getSize(); col++) {
            boolean[] alreadyCombined = new boolean[board.getSize()];
            for (int row = 1; row < board.getSize(); row++) {
                if(blocksArray[col][row] == null) continue; //skips to next cell if this cell is empty
                int newPos = row;
                // move "down" until we hit another block or the wall
                while (newPos > 0 && blocksArray[col][newPos-1] == null) {
                    newPos--;
                }
                //Actually move the block to the new position (copy to newPos remove from old)
                if(newPos != row) {
                    blocksArray[col][newPos] = blocksArray[col][row];
                    blocksArray[col][row] = null;
                }
                // If there is no block underneath we can skip to the next cell
                if(newPos == 0) continue;
                //Check if we can combine the current block with the one underneath
                // (numbers must match, and it cannot be an already combined block)
                if(blocksArray[col][newPos-1].getValue() == blocksArray[col][newPos].getValue() && !alreadyCombined[newPos-1]) {
                    //Combine the current block with the one under it
                    blocksArray[col][newPos-1].setValue(blocksArray[col][newPos-1].getValue() + blocksArray[col][newPos].getValue());
                    alreadyCombined[newPos-1] = true;
                    blocksArray[col][newPos] = null;
                }
            }
        }
    }

    public int play(Connection connection) {
        Scanner keyboard = new Scanner(System.in);
        LocalDateTime start_datetime = LocalDateTime.now();

        String command = "";

        board.addBlocksRandomly(2,2); // adds two 2's to begin with
        displayWireframe(Wireframe.GAMEBOARD, this); // Show initial board state

        running = true;
        do {
            // Ask for new command if there is no command entered
            if(command == null || command.isEmpty()) {
                System.out.print("> ");
                command = keyboard.nextLine().toUpperCase();
            }
            // The process command can return the next command in some cases
            String nextCommand = processCommand(command, connection);

            if (running) {
                command = nextCommand;
                displayWireframe(Wireframe.GAMEBOARD, this);
            }
        }while (running);
        // End game results
        Duration totalGameDuration = Duration.between(start_datetime, LocalDateTime.now());
        LeaderboardEntry finalResult = new LeaderboardEntry(getCurrentScore(), getCurrentPlayer().getName(), start_datetime, totalGameDuration);

        System.out.printf("Thanks for playing, %s!\nEnd score: %d\nTime: %d seconds\n", finalResult.getPlayerName(), finalResult.getScore(), finalResult.getDuration().getSeconds());

        System.out.print("Do you wish to save this score? (y/N) ");
        if(keyboard.nextLine().equalsIgnoreCase("Y")) {
            //TODO: save score
            if(LeaderboardManager.saveAttempt(connection, finalResult)) {
                System.out.println("Score saved!");
            }
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

    public String processCommand(String command, Connection connection) {
        if (command == null || command.isEmpty()) return "";
        String[] args = command.toUpperCase().split(" ");
        if(args.length < 1) return "";
        command = args[0];
        boolean waitToContinue = false;
        switch (command) {
            case "Q", "N" -> running = false;
            case "L" -> {
                boolean fetchAll = false;
                boolean fetchComplete = false;
                if(args.length >= 2) {
                    fetchAll = args[1].contains("A");
                    if(args[1].contains("F")) {
                        if(args.length >= 3) {
                            LeaderboardManager.fetchTopAttemptsOfPlayer(connection, args[2], fetchAll);
                            fetchComplete = true;
                        }else {
                            System.err.println("Missing argument PlayerName. Usage: L F <player_name>");
                            return null;
                        }
                    }
                }
                if(!fetchComplete) LeaderboardManager.fetchTopScores(connection, fetchAll);
                displayWireframe(Wireframe.LEADERBOARD, this);
                waitToContinue = true;
            }
            case "H" -> {
                displayWireframe(Wireframe.COMMANDS, this);
                waitToContinue = true;
            }
            case "R" -> {
                displayWireframe(Wireframe.RULES, this);
                waitToContinue = true;
            }
            case "0", "W", "UP" -> makeMove(Direction.UP);
            case "1", "D", "RIGHT" -> makeMove(Direction.RIGHT);
            case "2", "S", "DOWN" -> makeMove(Direction.DOWN);
            case "3", "A", "LEFT" -> makeMove(Direction.LEFT);
            default -> System.err.println("Invalid command, type 'H' to see the list of commands.");
        }
        if(waitToContinue) {
            Scanner keyboard = new Scanner(System.in);
            System.out.print("<Press Enter to continue>");
            return keyboard.nextLine();
        }
        return "";
    }
}
