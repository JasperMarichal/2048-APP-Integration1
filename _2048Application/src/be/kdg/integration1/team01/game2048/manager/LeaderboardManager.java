package be.kdg.integration1.team01.game2048.manager;

import be.kdg.integration1.team01.game2048.model.LeaderboardEntry;
import org.postgresql.util.PGInterval;

import java.sql.*;
import java.time.Duration;

import static be.kdg.integration1.team01.game2048._2048Application.leaderboard;

public class LeaderboardManager {

    public static Duration convertPGIntervalToDuration(PGInterval interval) {
        // Discards months and years
        return Duration.ofSeconds(
            interval.getWholeSeconds() +
            interval.getMinutes() * 60L +
            interval.getHours() * 3600L + // 60 * 60
            interval.getDays() * 86400L // 60 * 60 * 24
        );
    }

    /**
     * Saves a given attempt to the leaderboard (database)
     * @param connection database connection
     * @param attempt the LeaderboardEntry resulting from a finished Game
     * @return true if it was a success, false if it failed
     */
    public static boolean saveAttempt(Connection connection, LeaderboardEntry attempt) {
        try {
            PreparedStatement insertAttempt = connection.prepareStatement(
                "INSERT INTO int_leaderboard VALUES (?, ?, ?, ?)"
            );
            insertAttempt.setInt(1, attempt.getScore());
            insertAttempt.setString(2, attempt.getPlayerName());
            insertAttempt.setDate(3, Date.valueOf(attempt.getStartDate().toLocalDate()));
            insertAttempt.setObject(4, new PGInterval(0, 0, 0, 0, 0, attempt.getDuration().getSeconds()));
            insertAttempt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Leaderboard: Failed to save attempt to database");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Fetches the scores (of all players).
     * It either gets only the top 5 scores or it gets the full leaderboard.
     * The results are not returned, rather they are placed in the public static LeaderboardEntry
     * arraylist stored in the main _2048Application class (_2048Application.leaderboard) for ease of access.
     * @param connection database connection
     * @param fetchAll set to true if all entries should be fetched, false if only the top 5
     */
    public static void fetchTopScores(Connection connection, boolean fetchAll) {
        try {
            PreparedStatement selectTopScores = connection.prepareStatement(
            "SELECT score, player_name, start_date, duration FROM int_leaderboard\n" +
                "ORDER BY score DESC, duration, start_date" +
                (fetchAll ? ";" : "\nFETCH NEXT 5 ROWS ONLY;")
            );

            ResultSet topScores = selectTopScores.executeQuery();
            leaderboard.clear();
            while (topScores.next()) {
                leaderboard.add(new LeaderboardEntry(
                    topScores.getInt(1)
                    , topScores.getString(2)
                    , topScores.getDate(3).toLocalDate().atStartOfDay()
                    , convertPGIntervalToDuration((PGInterval) topScores.getObject("duration"))
                ));
            }
        } catch (SQLException e) {
            System.err.println("Leaderboard: Failed to fetch top scores!");
            e.printStackTrace();
        }
    }

    /**
     * Fetches the scores of a specific player.
     * It either gets only the top 5 scores or it gets the full leaderboard.
     * The results are not returned, rather they are placed in the public static LeaderboardEntry
     * arraylist stored in the main _2048Application class (_2048Application.leaderboard) for ease of access.
     * @param connection database connection
     * @param playerName the name of the player whose scores need to be fetched
     * @param fetchAll set to true if all entries should be fetched, false if only the top 5
     */
    public static void fetchTopAttemptsOfPlayer(Connection connection, String playerName, boolean fetchAll) {
        try {
            PreparedStatement selectTopScores = connection.prepareStatement(
            "SELECT score, player_name, start_date, duration FROM int_leaderboard\n" +
                "WHERE player_name ILIKE ? \n" +
                "ORDER BY score DESC, duration, start_date" +
                (fetchAll ? ";" : "\nFETCH NEXT 5 ROWS ONLY;")
            );

            selectTopScores.setString(1, playerName);

            ResultSet topScores = selectTopScores.executeQuery();
            leaderboard.clear();
            while (topScores.next()) {
                leaderboard.add(new LeaderboardEntry(
                        topScores.getInt(1)
                        , topScores.getString(2)
                        , topScores.getDate(3).toLocalDate().atStartOfDay()
                        , convertPGIntervalToDuration((PGInterval) topScores.getObject("duration"))
                ));
            }
        } catch (SQLException e) {
            System.err.println("Leaderboard: Failed to fetch top scores!");
            e.printStackTrace();
        }
    }
}
