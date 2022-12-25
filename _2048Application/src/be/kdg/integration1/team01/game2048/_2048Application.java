package be.kdg.integration1.team01.game2048;

import be.kdg.integration1.team01.game2048.manager.PlayerManager;
import be.kdg.integration1.team01.game2048.manager.SaveManager;
import be.kdg.integration1.team01.game2048.manager.WireframesManager;
import be.kdg.integration1.team01.game2048.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class _2048Application {

    public static ArrayList<LeaderboardEntry> leaderboard;
    public static ArrayList<Player> players;
    public static Scanner keyboard;
    public static void main(String[] args) {
        keyboard = new Scanner(System.in);

        Connection databaseConnection = initializeDatabase();
        if(databaseConnection == null) {
            return;
        }

        players = PlayerManager.updatePlayersList(databaseConnection);
        leaderboard = new ArrayList<>();

        WireframesManager.displayWireframe(Wireframe.WELCOME, null);
        keyboard.nextLine(); //Press enter to begin

        Player player = null;
        int endMode = 1; //0 = quit game, 1 = return to main menu, 2 = keep same player, start a new game

        int currentMenu;
        do {
            if(endMode == 2) {
                currentMenu = 1;
            }else {
                currentMenu = selectFromMenu("Main Menu", new String[]{"New Game", "Load Save"}, "Quit Game");
            }

            switch (currentMenu) {
                case 0 -> endMode = 0;
                case 1 -> {
                    //NEW GAME
                    //do not ask for player name in case of endMode 2, unless the player is somehow null.
                    if(player == null || endMode != 2) {
                        player = PlayerManager.selectPlayerOrCreateNew(databaseConnection);
                    }

                    Game gameSession = new Game(4, player);
                    endMode = gameSession.play(databaseConnection, true);
                }
                case 2 -> {
                    //LOAD SAVE
                    player = PlayerManager.selectExistingPlayer(databaseConnection);
                    if(player == null) continue; //Player failed to select an existing player

                    //Fetch a list of the saved games that this player has (database gameIds)
                    ArrayList<Long> playerSaves = SaveManager.getSaveGamesOfPlayer(databaseConnection, player);

                    //Fill the menu with the options
                    String[] options = new String[playerSaves.size()];
                    for (int i = 0; i < playerSaves.size(); i++) {
                        options[i] = "Save #"+playerSaves.get(i);
                    }
                    //Let player select which of their save games they want to load
                    int saveGameToLoad = selectFromMenu("Select Save To Load", options,"Cancel") - 1;
                    if(saveGameToLoad == -1) continue; //Player selected "Cancel"

                    Game gameSession = SaveManager.loadGame(databaseConnection, playerSaves.get(saveGameToLoad));
                    if(gameSession == null) continue; //Game failed to load
                    //Continue the game
                    endMode = gameSession.play(databaseConnection, false);
                }
            }
        }while (endMode != 0);

        try {
            databaseConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int selectFromMenu(String menuName, String[] menuOptions, String zeroOptionName) {
        int selectedOption = -1;
        do {
            System.out.println("\n"+menuName+"\n");
            for (int i = 0; i < menuOptions.length; i++) {
                System.out.printf("%d - %s\n", i+1, menuOptions[i]);
            }
            System.out.println("\n0 - "+zeroOptionName);
            System.out.print("Select option> ");
            try {
                selectedOption = keyboard.nextInt();
                keyboard.nextLine();
            }catch (InputMismatchException ignored) {
                keyboard.nextLine();
                System.out.println(menuName+": Select the desired option by typing in its number.");
            }

            if(selectedOption > menuOptions.length || selectedOption < 0) {
                System.out.println(menuName+": Invalid option!");
                selectedOption = -1;
            }
        }while (selectedOption == -1);
        return selectedOption;
    }

    public static Connection initializeDatabase() {
        try {
            System.out.println("INFO: Connecting to database...");
            Connection connection = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/ascii01"
                ,"postgres"
                ,"Student_1234"
            );

            // Create tables if they are missing:
            Statement prepareDB = connection.createStatement();
            // table: INT_players
            prepareDB.executeUpdate(
                    """
                            CREATE TABLE IF NOT EXISTS INT_players(
                                player_name varchar(20) constraint pk_players PRIMARY KEY
                            );"""
            );
            // table: INT_leaderboard
            prepareDB.executeUpdate(
                    """
                            CREATE TABLE IF NOT EXISTS INT_leaderboard (
                                score numeric
                                ,player_name varchar(20) constraint fk_player_name references INT_players(player_name)
                                ,start_date date constraint nn_start_date not null
                                ,duration interval constraint nn_duration not null
                            );"""
            );
            // table: INT_board
            prepareDB.executeUpdate(
                    """
                            CREATE TABLE IF NOT EXISTS INT_board(
                                  board_id bigserial constraint pk_board_id primary key
                                  ,board_size numeric
                              );"""
            );
            // table: INT_blocks
            prepareDB.executeUpdate(
                    """
                            CREATE TABLE IF NOT EXISTS INT_blocks(
                                  block_id bigserial constraint pk_block_id primary key
                                  ,block_value numeric not null
                                  ,block_x numeric(1) constraint nn_block_x not null
                                  ,block_y numeric(1) constraint nn_block_y not null
                                  ,board_id bigserial constraint fk_board_id references INT_board(board_id)
                              );"""
            );
            // table: INT_games
            prepareDB.executeUpdate(
                    """
                            CREATE TABLE IF NOT EXISTS INT_games(
                                 game_id bigserial constraint pk_game_id primary key
                                 ,player_name varchar(20) constraint fk_player_name references INT_players(player_name)
                                 ,current_score numeric
                                 ,current_turn numeric
                                 ,board_id bigserial constraint fk_board_id references INT_board(board_id)
                             );"""
            );
            // TEST LEADERBOARD VALUES
            prepareDB.executeUpdate("DELETE FROM INT_leaderboard WHERE player_name IN ('hello', 'aaa', 'DemO');");
            prepareDB.executeUpdate("insert into int_players values('hello') on conflict do nothing;");
            prepareDB.executeUpdate("INSERT INTO int_leaderboard values (4637 ,'hello' ,current_date , interval '342 seconds') on conflict do nothing;");
            prepareDB.executeUpdate("INSERT INTO int_leaderboard values (6443 ,'hello' ,current_date , interval '634 seconds') on conflict do nothing;");
            prepareDB.executeUpdate("INSERT INTO int_leaderboard values (124 ,'hello' ,current_date , interval '53 seconds') on conflict do nothing;");
            prepareDB.executeUpdate("INSERT INTO int_leaderboard values (2341 ,'hello' ,current_date , interval '123 seconds') on conflict do nothing;");
            prepareDB.executeUpdate("INSERT INTO int_leaderboard values (1231 ,'hello' ,current_date , interval '32 seconds') on conflict do nothing;");
            prepareDB.executeUpdate("INSERT INTO int_leaderboard values (234 ,'hello' ,current_date , interval '64 seconds') on conflict do nothing;");
            prepareDB.executeUpdate("insert into int_players values('aaa') on conflict do nothing;");
            prepareDB.executeUpdate("INSERT INTO int_leaderboard values (5474 ,'aaa' ,current_date , interval '213 seconds') on conflict do nothing;");
            prepareDB.executeUpdate("INSERT INTO int_leaderboard values (5474 ,'aaa' ,current_date , interval '123 seconds') on conflict do nothing;");
            prepareDB.executeUpdate("INSERT INTO int_leaderboard values (5634 ,'aaa' ,current_date , interval '213 seconds') on conflict do nothing;");
            prepareDB.executeUpdate("insert into int_players values('DemO') on conflict do nothing;");
            prepareDB.executeUpdate("INSERT INTO int_leaderboard values (2345 ,'DemO' ,current_date , interval '65 seconds') on conflict do nothing;");
            prepareDB.executeUpdate("INSERT INTO int_leaderboard values (2345 ,'DemO' ,current_date - interval '2 days', interval '65 seconds') on conflict do nothing;");
            // return active database connection for the program to use
            System.out.println("INFO: Successfully connected to database!");
            return connection;
        } catch (SQLException e) {
            System.err.println("ERROR: Failed to connect to database!");
            e.printStackTrace();
            return null;
        }
    }
}
