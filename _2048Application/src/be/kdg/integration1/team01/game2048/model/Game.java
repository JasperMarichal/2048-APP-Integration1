package be.kdg.integration1.team01.game2048.model;

public class Game {
    private int currentScore;
    private int turn;
    private Board board;

    public Game(int currentScore, int turn, int boardSize) {
        this.currentScore = currentScore;
        this.turn = turn;
        this.board = new Board(boardSize);
    }

    public int getCurrentScore() {return currentScore;}
    public void setCurrentScore(int currentScore) {this.currentScore = currentScore;}
    public int getTurn() {return turn;}
    public void setTurn(int turn) {this.turn = turn;}
    public Board getBoard() {return board;}
    public void setBoard(Board board) {this.board = board;}
}
