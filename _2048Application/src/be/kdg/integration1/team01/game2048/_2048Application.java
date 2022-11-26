package be.kdg.integration1.team01.game2048;

import be.kdg.integration1.team01.game2048.model.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

public class _2048Application {

    private static ArrayList<LeaderboardEntry> leaderboard;
    private static ArrayList<Player> players;
    private static Game gameState;
    public static void main(String[] args) {
        Scanner keyboard = new Scanner(System.in);

        Connection databaseConnection = initializeDatabase();
        if(databaseConnection == null) {
            return;
        }

        leaderboard = new ArrayList<>();
        players = new ArrayList<>();
        //load players
        players.add(new Player("me"));
        // select player or create new one
        int selectedPlayer = 0;
        gameState = new Game(4, players.get(selectedPlayer));


        try {
            databaseConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
