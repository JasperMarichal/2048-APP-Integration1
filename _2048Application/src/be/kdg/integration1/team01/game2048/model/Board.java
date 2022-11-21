package be.kdg.integration1.team01.game2048.model;

import java.util.ArrayList;

public class Board {
    private int size;
    private ArrayList<Block> block;

    public Board(int size) {
        this.size = size;
        this.block = new ArrayList<>();
    }

    public int getSize() {return size;}
    public void setSize(int size) {this.size = size;}
    public ArrayList<Block> getBlock() {return block;}
    public void setBlock(ArrayList<Block> block) {this.block = block;}
}
