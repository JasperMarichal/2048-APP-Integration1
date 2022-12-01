package be.kdg.integration1.team01.game2048.model;

import be.kdg.integration1.team01.game2048.manager.LeaderboardManager;

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

    public int play(Connection connection) {
        Scanner keyboard = new Scanner(System.in);
        LocalDateTime start_datetime = LocalDateTime.now();

        String command;

        displayWireframe(Wireframe.GAMEBOARD, this); // Show initial board state

        running = true;
        do {
            System.out.print("> ");
            command = keyboard.nextLine().toUpperCase();
            processCommand(command, connection);

            if (running) {
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
            System.out.println(command);
            System.out.print("Do you want to play again? (y/N) ");
            if(keyboard.nextLine().equalsIgnoreCase("Y")) {
                return 2;
            }
            return 0;
        }
    }

    public void processCommand(String command, Connection connection) {
        if (command == null || command.isEmpty()) return;
        String[] args = command.toUpperCase().split(" ");
        if(args.length < 1) return;
        command = args[0];
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
                            return;
                        }
                    }
                }
                if(!fetchComplete) LeaderboardManager.fetchTopScores(connection, fetchAll);
                processCommand(displayWireframe(Wireframe.LEADERBOARD, this, true), connection);
            }
            case "H" -> processCommand(displayWireframe(Wireframe.COMMANDS, this, true), connection);
            case "R" -> processCommand(displayWireframe(Wireframe.RULES, this, true), connection);
            case "0", "W", "UP" -> makeMove(Direction.UP);
            case "1", "D", "RIGHT" -> makeMove(Direction.RIGHT);
            case "2", "S", "DOWN" -> makeMove(Direction.DOWN);
            case "3", "A", "LEFT" -> makeMove(Direction.LEFT);
            default -> System.err.println("Invalid command, type 'H' to see the list of commands.");
        }
    }
}
