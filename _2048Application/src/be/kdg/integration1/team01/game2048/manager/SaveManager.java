package be.kdg.integration1.team01.game2048.manager;

import be.kdg.integration1.team01.game2048.model.*;
import org.postgresql.util.PGInterval;

import java.sql.*;
import java.util.ArrayList;

import static be.kdg.integration1.team01.game2048._2048Application.leaderboard;

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

    public static Game loadGame(Connection connection, long gameId) {
        try {

            PreparedStatement getGame = connection.prepareStatement(
                    """ 
                            SELECT current_score
                            , current_turn
                            ,board_id
                            ,player_name 
                            FROM int_games WHERE game_id = ?;
                        """
            );
            getGame.setLong(1, gameId);
            ResultSet gameEntry = getGame.executeQuery();

            if (gameEntry.next()) {
                return new Game(gameEntry.getInt(1)
                        , new ArrayList<>()
                        , loadBoard(connection, gameEntry.getLong(3))
                        , PlayerManager.findPlayerByName(gameEntry.getString(4))
                        , true);
            }

        } catch (SQLException e) {
            System.err.println("Load Game: Loading of the game failed");
            e.printStackTrace();
        }
        return null;
    }

    public static Board loadBoard(Connection connection, long boardId){
        try{
            PreparedStatement getBoard = connection.prepareStatement(
                    """ 
                            SELECT
                            board_id
                            ,board_size
                            FROM int_board
                            WHERE board_id = ?;
                        """
            );
            getBoard.setLong(1, boardId);
            ResultSet boardEntry = getBoard.executeQuery();

            if (boardEntry.next()) {
                return new Board(boardEntry.getInt(1));
            }

        } catch (SQLException e) {
            System.err.println("Load board: Loading of the board failed");
            e.printStackTrace();
        }
        return null;
    }

    public static Block loadBlock(Connection connection, long boardId){
        try {
            PreparedStatement getBlock = connection.prepareStatement(
                    """
                        SELECT 
                        block_value
                        ,block_x
                        ,block_y
                        FROM int_blocks
                        WHERE board_id = ?;
                        """
            );
            getBlock.setLong(1, boardId);
            ResultSet blockEntry = getBlock.executeQuery();

            while(blockEntry.next()) {
                return new Block(blockEntry.getInt(2)
                ,blockEntry.getInt(3)
                ,blockEntry.getInt(4));
            }

        }catch (SQLException e) {
            System.err.println("Load Block; Loading of the block failed");
            e.printStackTrace();
        }
        return null;
    }
}
