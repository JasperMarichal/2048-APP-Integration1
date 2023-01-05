package be.kdg.integration1.team01.game2048.manager;

import be.kdg.integration1.team01.game2048.model.*;

import java.sql.*;
import java.util.ArrayList;

public class SaveManager {
    //SaveGame methods
    public static long saveBoard(Connection connection, Board board, Game game) {
        try {
            if(game.getGameId() <= 0){
                PreparedStatement insertAttempt = connection.prepareStatement(
                        """
                        INSERT INTO int_board (
                        board_size)
                        VALUES (?);
                        """
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
            }
            else if(game.getGameId() > 0){
                PreparedStatement getBoard = connection.prepareStatement(
                        """
                        SELECT ib.board_id
                        FROM int_board ib
                        JOIN int_games ig on ib.board_id = ig.board_id
                        WHERE ib.board_id = ig.board_id
                        AND ig.game_id = ?;
                        """
                );
                getBoard.setLong(1, game.getGameId());
                ResultSet boardEntry = getBoard.executeQuery();
                if (boardEntry.next()) {
                        long id = boardEntry.getLong(1);
                        for (Block block : board.getBlocks()) {
                            saveBlock(connection, block, id);
                        }
                        return id;
                }
                else{
                    throw new SQLException("Save game: Failed to save board to database");
                }
            }else{
                throw new SQLException("Save game: Failed to save board to database");
            }
        } catch (SQLException e) {
            System.err.println("Save game: Failed to save board to database");
            e.printStackTrace();
            return -1;
        }
    }
    public static boolean saveGame(Connection connection, Game game) {
        try {
            deleteBlocks(connection, game);
            long board_id = saveBoard(connection, game.getBoard(), game);
            if(board_id == -1){
                throw new SQLException("Save game: Failed to save board to database");
            }
            if(game.getGameId() <= 0){
            PreparedStatement insertAttempt = connection.prepareStatement(
                    """
                            INSERT INTO int_games (
                            player_name
                            , current_score
                            , current_turn
                            , board_id)
                            VALUES (?, ?, ?, ?);
                            """
            );
            insertAttempt.setString(1, game.getCurrentPlayer().getName());
            insertAttempt.setInt(2, game.getCurrentScore());
            insertAttempt.setInt(3, game.getTurns().size());
            insertAttempt.setLong(4, board_id);
            insertAttempt.executeUpdate();
            return true;
            }
            else if(game.getGameId() > 0){
                PreparedStatement insertAttempt = connection.prepareStatement(
                        """
                                UPDATE int_games SET (
                                player_name
                                , current_score
                                , current_turn
                                , board_id)
                                = (?, ?, ?, ?)
                                WHERE game_id = ?;
                                """
                );
                insertAttempt.setString(1, game.getCurrentPlayer().getName());
                insertAttempt.setInt(2, game.getCurrentScore());
                insertAttempt.setInt(3, game.getTurns().size());
                insertAttempt.setLong(4, board_id);
                insertAttempt.setInt(5, game.getGameId());
                insertAttempt.executeUpdate();
                return true;
            }
            else{
                throw new SQLException("Save game: Failed to save game to database");
            }
        } catch (SQLException e) {
            System.err.println("Game: Failed to save game to database");
            e.printStackTrace();
            return false;
        }
    }
    public static boolean deleteBlocks(Connection connection, Game game){
        //This method is used to delete previous blocks used in the game board.
        try {
            PreparedStatement deleteAttempt = connection.prepareStatement(
                    """
                            DELETE FROM int_blocks b WHERE b.board_id
                            IN (SELECT g.board_id FROM int_games g
                            WHERE g.game_id = ?);
                            """
            );
            deleteAttempt.setInt(1,game.getGameId());
            deleteAttempt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Block: Failed to delete blocks from database");
            e.printStackTrace();
            return false;
        }
    }
    public static boolean saveBlock(Connection connection, Block block, long board_id) {
        try {
            PreparedStatement insertAttempt = connection.prepareStatement(
                    """
                            INSERT INTO int_blocks (
                            block_value
                            , block_x
                            , block_y
                            , board_id)
                            VALUES (?, ?, ?, ?);
                           """
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

    //LoadGame methods
    public static Game loadGame(Connection connection, long gameId) {
        try {

            PreparedStatement getGame = connection.prepareStatement(
                    """ 
                            SELECT game_id
                            ,current_score
                            ,current_turn
                            ,board_id
                            ,player_name
                            FROM int_games WHERE game_id = ?;
                        """
            );
            getGame.setLong(1, gameId);
            ResultSet gameEntry = getGame.executeQuery();

            if (gameEntry.next()) {
                return new Game(gameEntry.getInt(1)
                        ,gameEntry.getInt(2)
                        , new ArrayList<>()
                        , loadBoard(connection, gameEntry.getLong(4))
                        , PlayerManager.findPlayerByName(gameEntry.getString(5))
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
                            board_size
                            FROM int_board
                            WHERE board_id = ?;
                        """
            );
            getBoard.setLong(1, boardId);
            ResultSet boardEntry = getBoard.executeQuery();

            if (boardEntry.next()) {
                return new Board(boardEntry.getInt(1),loadBlock(connection, boardId));
            }

        } catch (SQLException e) {
            System.err.println("Load board: Loading of the board failed");
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<Block> loadBlock(Connection connection, long boardId){
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

            ArrayList<Block> blockArrayList = new ArrayList<>();

            while(blockEntry.next()) {
                blockArrayList.add(new Block(blockEntry.getInt(1)
                ,blockEntry.getInt(2)
                ,blockEntry.getInt(3)));
            }
            return blockArrayList;

        }catch (SQLException e) {
            System.err.println("Load Block; Loading of the block failed");
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<Long> getSaveGamesOfPlayer(Connection databaseConnection, Player player) {
        try {
            PreparedStatement getSaveGamesOfPlayer = databaseConnection.prepareStatement(
                    """
                        SELECT g.game_id
                        FROM int_players p
                        JOIN int_games g on p.player_name = g.player_name
                        WHERE p.player_name = ?;
                        """
            );
            getSaveGamesOfPlayer.setString(1, player.getName());
            ResultSet blockEntry = getSaveGamesOfPlayer.executeQuery();

            ArrayList<Long> Arr = new ArrayList<>();

            while(blockEntry.next()) {
                Arr.add(blockEntry.getLong(1));
            }
            return Arr;

        }catch (SQLException e) {
            System.err.println("GetSaveGamesOfPlayer; Loading of the SaveGamesOfPlayer failed");
            e.printStackTrace();
        }

        return new ArrayList<>();
    }
}
