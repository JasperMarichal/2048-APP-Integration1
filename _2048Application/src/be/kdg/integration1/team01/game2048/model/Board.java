package be.kdg.integration1.team01.game2048.model;

import java.util.ArrayList;
import java.util.Random;

public class Board {
    private int size;
    private ArrayList<Block> blocks;

    public Board(int size) {
        this.size = size;
        this.blocks = new ArrayList<>();
    }

    public int getSize() {return size;}
    public void setSize(int size) {this.size = size;}
    public ArrayList<Block> getBlocks() {return blocks;}
    public void setBlocks(ArrayList<Block> blocks) {this.blocks = blocks;}

    public Block[][] getArray() {
        Block[][] arr = new Block[size][size];
        for(Block block : blocks) {
            arr[block.getPositionX()][block.getPositionY()] = block;
        }
        return arr;
    }

    public void fromArray(Block[][] blocksArray) {
        blocks.clear();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                if(blocksArray[x][y] == null) continue;
                blocks.add(blocksArray[x][y]);
            }
        }
    }

    /**
     * This function converts the board into a 2D array of blocks,
     * with coordinates relative to the sliding direction.
     * This "general array" is used so that we can have a general algorithm
     * for sliding the blocks in any of the four directions.
     * ---------------------------------------------------------------- <br>
     * The array's coordinates will look like (S = size of the board):
     * (0, S-1) (1, S-1) ... (x, S-1) ... (S-1, S-1)
     * (0, S-2) (1, S-2)                                |
     * ...                                              | Slide
     * (0, y)                (x, y)                     | Direction
     * ...                                              V
     * (0, 0)   (1, 0)   ... (x, 0)   ... (S-1, 0)
     * @param slideDirection the direction the board will be slided in
     * @return the general 2D array of Blocks
     */
    public Block[][] getGeneralArray(Direction slideDirection) {
        Block[][] arr = new Block[size][size];
        for(Block block : blocks) {
            int generalX = switch (slideDirection) {
                case UP -> (size-1)-block.getPositionX();
                case RIGHT -> (size-1)-block.getPositionY();
                case DOWN -> block.getPositionX();
                case LEFT -> block.getPositionY();
            };
            int generalY = switch (slideDirection) {
                case UP -> block.getPositionY();
                case RIGHT -> (size-1)-block.getPositionX();
                case DOWN -> (size-1)-block.getPositionY();
                case LEFT -> block.getPositionX();
            };
            arr[generalX][generalY] = block;
        }
        return arr;
    }

    /**
     * Updates the blocks on the board from the provided general array
     * @param generalArray the updated general array
     * @param slideDirection the direction the blocks in the array were slided in
     */
    public void updateFromGeneralArray(Block[][] generalArray, Direction slideDirection) {
        blocks.clear();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                if(generalArray[x][y] == null) continue;
                // Update the coordinates of the block (reverse generalization)
                generalArray[x][y].setPositionX(switch (slideDirection) {
                    case UP -> (size-1)-x;
                    case RIGHT -> (size-1)-y;
                    case DOWN -> x;
                    case LEFT -> y;
                });
                generalArray[x][y].setPositionY(switch (slideDirection) {
                    case UP -> y;
                    case RIGHT -> (size-1)-x;
                    case DOWN -> (size-1)-y;
                    case LEFT -> x;
                });
                // add the block to the list
                blocks.add(generalArray[x][y]);
            }
        }
    }

    public void addBlocksRandomly(int value, int numberOfBlocks) {
        Random rng = new Random();
        Block[][] blocksArray = getArray();
        //New method
        //find all empty cells
        ArrayList<int[]> emptyCells = new ArrayList<>();

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                if(blocksArray[x][y] == null) {
                    emptyCells.add(new int[]{x, y});
                }
            }
        }
        //pick <numberOfBlocks> of these cells to fill in
        for (int i = 0; i < numberOfBlocks; i++) {
            if(emptyCells.size() == 0) break;
            int r = rng.nextInt(0, emptyCells.size());
            int[] pos = emptyCells.get(r);
            blocksArray[pos[0]][pos[1]] = new Block(value, pos[0], pos[1]);
            emptyCells.remove(r);
        }

        //Old method
//        int rx, ry;
//        while (numberOfBlocks > 0) {
//            rx = rng.nextInt(0, size);
//            ry = rng.nextInt(0, size);
//            if(blocksArray[rx][ry] == null) {
//                blocksArray[rx][ry] = new Block(value, rx, ry);
//                numberOfBlocks--;
//            }
//        }
        fromArray(blocksArray);
    }

    public static String formattedBoardFromArray(Block[][] blocksArray) {
        String horizontalLine = ":" + "-".repeat(6* blocksArray.length - 1) + ":\n";
        StringBuilder brd = new StringBuilder(horizontalLine);
        for (int y = 0; y < blocksArray.length; y++) {
            for (int x = 0; x < blocksArray.length; x++) {
                if(blocksArray[x][y] == null) {
                    brd.append("|%5s".formatted(" "));
                }else {
                    brd.append("|%5d".formatted(blocksArray[x][y].getValue()));
                }
            }
            brd.append("|\n");
            brd.append(horizontalLine);
        }
        return brd.toString();
    }
    @Override
    public String toString() {
//        return  ":-------------------------:\n" +
//                "|     |      |      |     |\n" +
//                ":-------------------------:\n" +
//                "|     |      |      |     |\n" +
//                ":-------------------------:\n" +
//                "|     |      | 2    |     |\n" +
//                ":-------------------------:\n" +
//                "| 2   |      |      |     |\n" +
//                ":-------------------------:\n";
        return formattedBoardFromArray(getArray());
    }
}
