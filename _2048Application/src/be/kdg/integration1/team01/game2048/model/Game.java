package be.kdg.integration1.team01.game2048.model;

import java.util.ArrayList;

public class Game {
    private int currentScore;
    private ArrayList<Turn> turns;
    private Board board;

    private Player currentPlayer;

    public Game(int boardSize, Player player) {
        this.currentScore = 0;
        this.turns = new ArrayList<Turn>();
        this.board = new Board(boardSize);
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
}
