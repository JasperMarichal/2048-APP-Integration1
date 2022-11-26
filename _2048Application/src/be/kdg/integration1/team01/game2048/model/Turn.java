package be.kdg.integration1.team01.game2048.model;

public class Turn {
    private Direction direction;

    public Turn(Direction direction) {
        setDirection(direction);
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }
}
