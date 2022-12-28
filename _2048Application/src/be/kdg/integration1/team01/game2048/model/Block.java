package be.kdg.integration1.team01.game2048.model;

public class Block {
    private int value;
    private int positionX;
    private int positionY;

    public Block(int value, int positionX, int positionY) {
        this.value = value;
        this.positionX = positionX;
        this.positionY = positionY;
    }

    public Block(Block copyOf) {
        this.value = copyOf.getValue();
        this.positionX = copyOf.getPositionX();
        this.positionY = copyOf.getPositionY();
    }

    public int getValue() {return value;}

    public void setValue(int value) {this.value = value;}

    public int getPositionX() {return positionX;}

    public void setPositionX(int positionX) {this.positionX = positionX;}

    public int getPositionY() {return positionY;}

    public void setPositionY(int positionY) {this.positionY = positionY;}
}
