package be.kdg.integration1.team01.game2048.model;

public class Block {
    private int number;
    private int positionX;
    private int positionY;

    public Block(int number, int positionX, int positionY) {
        this.number = number;
        this.positionX = positionX;
        this.positionY = positionY;
    }

    public int getNumber() {return number;}

    public void setNumber(int number) {this.number = number;}

    public int getPositionX() {return positionX;}

    public void setPositionX(int positionX) {this.positionX = positionX;}

    public int getPositionY() {return positionY;}

    public void setPositionY(int positionY) {this.positionY = positionY;}
}
