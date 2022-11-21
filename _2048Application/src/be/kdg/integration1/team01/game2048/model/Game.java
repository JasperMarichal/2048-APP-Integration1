package be.kdg.integration1.team01.game2048.model;

public class Game {
    private int currentScore;
    private int turn;

    public Game(int currentScore, int turn) {
        this.currentScore = currentScore;
        this.turn = turn;
    }

    public int getCurrentScore() {return currentScore;}

    public void setCurrentScore(int currentScore) {this.currentScore = currentScore;}

    public int getTurn() {return turn;}

    public void setTurn(int turn) {this.turn = turn;}
}
