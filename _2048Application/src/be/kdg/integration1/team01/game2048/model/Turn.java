package be.kdg.integration1.team01.game2048.model;

public class Turn {
    private int direction;

    public Turn(int direction) {
        setDirection(direction);
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }
}
