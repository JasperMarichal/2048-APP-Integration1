package be.kdg.integration1.team01.game2048.manager;

import be.kdg.integration1.team01.game2048.model.*;

import java.sql.*;

public class SaveManager {
    public static long saveBoard(Connection connection, Board board) {
        try {
            PreparedStatement insertAttempt = connection.prepareStatement(
                    "INSERT INTO int_board (board_size) VALUES (?)"
                    , PreparedStatement.RETURN_GENERATED_KEYS
            );
            insertAttempt.setInt(1, board.getSize());
            insertAttempt.executeUpdate();

            try (ResultSet generatedKeys = insertAttempt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long id = generatedKeys.getLong(1);
                    for (Block block : board.getBlocks()) {
                        saveBlock(connection, block, id);
                    }
                    return id;
                }
                else{
                throw new SQLException("Save game: Failed to save board to database");
                }
            }

        } catch (SQLException e) {
            System.err.println("Save game: Failed to save board to database");
            e.printStackTrace();
            return -1;
        }
    }
    public static boolean saveGame(Connection connection, Game game) {
        try {
            long board_id = saveBoard(connection, game.getBoard());
            if(board_id == -1){
                throw new SQLException("Save game: Failed to save board to database");
            }
            PreparedStatement insertAttempt = connection.prepareStatement(
                    "INSERT INTO int_games (player_name, current_score, current_turn, board_id) VALUES (?, ?, ?, ?)"
            );
            insertAttempt.setString(1, game.getCurrentPlayer().getName());
            insertAttempt.setInt(2, game.getCurrentScore());
            insertAttempt.setInt(3, game.getTurns().size());
            insertAttempt.setLong(4, board_id);
            insertAttempt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Game: Failed to save game to database");
            e.printStackTrace();
            return false;
        }
    }
    public static boolean saveBlock(Connection connection, Block block, long board_id) {
        try {

            PreparedStatement insertAttempt = connection.prepareStatement(
                    "INSERT INTO int_blocks (block_value, block_x, block_y, board_id) VALUES (?, ?, ?, ?)"
            );
            insertAttempt.setInt(1, block.getValue());
            insertAttempt.setInt(2, block.getPositionX());
            insertAttempt.setInt(3, block.getPositionY());
            insertAttempt.setLong(4, board_id);
            insertAttempt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Block: Failed to save block to database");
            e.printStackTrace();
            return false;
        }
    }
}
