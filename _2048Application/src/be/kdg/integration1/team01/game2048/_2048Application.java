package be.kdg.integration1.team01.game2048;

import be.kdg.integration1.team01.game2048.manager.PlayerManager;
import be.kdg.integration1.team01.game2048.manager.WireframesManager;
import be.kdg.integration1.team01.game2048.model.*;

import java.sql.*;
import java.util.ArrayList;
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

        WireframesManager.displayWireframe(Wireframe.WELCOME, null);
        keyboard.nextLine(); //Press enter to begin

        Player player = null;
        int endMode = 1; //0 = quit, 1 = ask for player name again, 2 = same player, start new game
        do {
            if(endMode == 1) {
                player = PlayerManager.selectPlayerOrCreateNew(databaseConnection);
            }

            //GameSession object
            Game gameState = new Game(4, player);

            //Start the new game
            endMode = gameState.play();
        }while (endMode != 0);

        try {
            databaseConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Player findPlayerByName(String playerName) {
        // Find player with given name if it already exists otherwise return null
        Player player = null;
        for (Player p : players) {
            if (p.getName().equals(playerName)) {
                player = p;
                break;
            }
        }
        return player;
    }

    public static Connection initializeDatabase() {
        try {
            System.out.println("INFO: Connecting to database...");
            Connection connection = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/ascii01"
                    ,"postgres"
                    ,"Student_1234");

            // Create tables if they are missing:
            Statement prepareDB = connection.createStatement();
            // table: INT_players
            prepareDB.executeUpdate(
                "create table if not exists INT_players(" +
                "    player_name varchar(20) constraint pk_players PRIMARY KEY" +
                ");"
            );
            // table: INT_leaderboard
            prepareDB.executeUpdate(
                "create table if not exists INT_leaderboard (" +
                "    score numeric\n" +
                "    ,player_name varchar(20) constraint fk_player_name references INT_players(player_name)" +
                "    ,start_date date constraint nn_start_date not null\n" +
                "    ,duration interval constraint nn_duration not null\n" +
                ");"
            );
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
