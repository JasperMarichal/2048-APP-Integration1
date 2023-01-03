package be.kdg.integration1.team01.game2048.model;

import be.kdg.integration1.team01.game2048.manager.LeaderboardManager;
import be.kdg.integration1.team01.game2048.manager.SaveManager;

import java.sql.Connection;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;

import static be.kdg.integration1.team01.game2048.manager.WireframesManager.displayWireframe;

public class Game {
    private int gameId;
    private int currentScore;
    private ArrayList<Turn> turns;
    private Board board;
    private Player currentPlayer;
    private boolean running;

    public Game(int boardSize, Player player) {
        this.currentScore = 0;
        this.turns = new ArrayList<>();
        this.board = new Board(boardSize);
        this.running = false;
        setCurrentPlayer(player);
    }

    public Game(int gameId, int currentScore, ArrayList<Turn> turns, Board board, Player currentPlayer, boolean running) {
        this.gameId = gameId;
        this.currentScore = currentScore;
        this.turns = turns;
        this.board = board;
        this.currentPlayer = currentPlayer;
        this.running = running;
    }

    public int getGameId() {
        return gameId;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public int getCurrentScore() {return currentScore;}

    public ArrayList<Turn> getTurns() {
        return turns;
    }

    public Board getBoard() {return board;}

    public void makeMove(Direction slideDirection) {
        //generalize board into a 2D array
        Block[][] blocksArray = board.getGeneralArray(slideDirection);
        //simulate sliding on array and increase score
        currentScore += Board.slideGeneralArray(blocksArray);
        //update board
        board.updateFromGeneralArray(blocksArray, slideDirection);
        //End turn
        turns.add(new Turn(slideDirection));
        //Generate a new random block on the board
        board.addBlocksRandomly(2, 1);
    }


    public int play(Connection connection, boolean isBeginning) {
        Scanner keyboard = new Scanner(System.in);
        LocalDateTime start_datetime = LocalDateTime.now();

        String command = "";

        if(isBeginning) board.addBlocksRandomly(2,2); // adds two 2's to begin with
        displayWireframe(Wireframe.GAMEBOARD, this); // Show initial board state

        running = true;
        do {
            // Ask for new command if there is no command entered
            if(command == null || command.isEmpty()) {
                System.out.print("> ");
                command = keyboard.nextLine().toUpperCase();
            }
            // processCommand can return the next command in some cases
            String nextCommand = processCommand(command, connection);
            //LOSE CONDITION CHECK
            //if board is full and if the board cannot be slided in any direction
            if(board.getBlocks().size() == board.getSize()*board.getSize() && !board.isSlideable()) {
                running = false;
            }

            if (running) {
                command = nextCommand;
                displayWireframe(Wireframe.GAMEBOARD, this);
            }
        }while (running);

        //CHECK IF THE GAME ENDED OR IT CAN BE CONTINUED LATER
        if(board.isSlideable()) {
            //GAME CAN BE CONTINUED
            if(getGameId() > 0) {
                System.out.print("Update game save (otherwise this session's progress will be lost)? (Y/n) ");
            }else {
                System.out.print("Do you wish to Save the Game (so you can continue later)? (Y/n) ");
            }
            if(!keyboard.nextLine().equalsIgnoreCase("N")) {
                if(SaveManager.saveGame(connection, this)) {
                    System.out.println("Game saved successfully! \n(To continue it later select \"Load Save\" from the Main Menu.)\n");
                }else {
                    System.err.println("ERROR: Failed to save the game!\n");
                }
            }
        }else {
            //GAME OVER
            // Calculate game results
            Duration totalGameDuration = Duration.between(start_datetime, LocalDateTime.now());
            LeaderboardEntry finalResult = new LeaderboardEntry(getCurrentScore(), getCurrentPlayer().getName(), start_datetime, totalGameDuration);

            // Display final game results
            System.out.println(board.toString());
            System.out.printf("Thanks for playing, %s!\nEnd score: %d\nTime: %d seconds\n", finalResult.getPlayerName(), finalResult.getScore(), finalResult.getDuration().getSeconds());

            System.out.print("Do you wish to save this score? (y/N) ");
            if(keyboard.nextLine().equalsIgnoreCase("Y")) {
                if(LeaderboardManager.saveAttempt(connection, finalResult)) {
                    System.out.println("Score saved!");
                }
            }
        }

        //Ask player if they want to return to the menu unless they already entered the new game command
        if(command.equalsIgnoreCase("N")) {
            return 2;
        }else {
            System.out.print("Do you want to return to the Main Menu? (y/N) ");
            if(keyboard.nextLine().equalsIgnoreCase("Y")) {
                return 1;
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
