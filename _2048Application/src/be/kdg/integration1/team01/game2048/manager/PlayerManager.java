package be.kdg.integration1.team01.game2048.manager;

import be.kdg.integration1.team01.game2048._2048Application;
import be.kdg.integration1.team01.game2048.model.Player;

import java.sql.*;
import java.util.ArrayList;

import static be.kdg.integration1.team01.game2048._2048Application.players;

public class PlayerManager {

    public static ArrayList<Player> updatePlayersList(Connection connection) {
        ArrayList<Player> updatedList = new ArrayList<>();
        try {
            Statement retrievePlayers = connection.createStatement();
            ResultSet allPlayers = retrievePlayers.executeQuery("SELECT player_name FROM int_players;");
            while (allPlayers.next()) {
                updatedList.add(new Player(allPlayers.getString(1)));
            }
        } catch (SQLException e) {
            System.err.println("Error while updating cached players list.");
            e.printStackTrace();
        }
        return updatedList;
    }

    public static void saveNewPlayer(Connection connection, Player player) {
        try {
            PreparedStatement insertPlayer = connection.prepareStatement("insert into int_players values(?);");
            insertPlayer.setString(1, player.getName());
            insertPlayer.executeUpdate();
            players.add(player);
        } catch (SQLException e) {
            System.err.println("Error while trying to save new player.");
            e.printStackTrace();
        }
    }

    /**
     * Finds the Player with the name given
     * @param playerName The name of the player to find.
     * @return a valid <b>Player</b> object if it is in the cached players list,
     * <b>null</b> if there is no player with that name found
     */
    public static Player findPlayerByName(String playerName) {
        Player player = null;
        for (Player p : players) {
            if (p.getName().equals(playerName)) {
                player = p;
                break;
            }
        }
        return player;
    }

    public static Player selectPlayerOrCreateNew(Connection connection) {
        Player player = null;
        do {
            System.out.print("Enter your name: ");
            String playerName = _2048Application.keyboard.nextLine().trim();
            if (playerName.isEmpty()) {
                System.err.println("Your name cannot be empty! Please choose a name.");
            } else if (playerName.length() > 20) {
                System.err.println("Your player name cannot be longer than 20 characters. Please pick a new one.");
            } else {
                players = updatePlayersList(connection);
                player = findPlayerByName(playerName);
                if (player == null) {
                    System.out.println("Create new player "+playerName+"? (y/N): ");
                    if (_2048Application.keyboard.nextLine().equalsIgnoreCase("Y")) {
                        player = new Player(playerName);
                        saveNewPlayer(connection, player);
                    }
                }else {
                    System.out.println("Do you wish to play as the existing player "+playerName+"? (Y/n): ");
                    if (_2048Application.keyboard.nextLine().equalsIgnoreCase("N")) {
                        player = null;
                    }
                }
            }
        }while (player == null);
        return player;
    }
}
