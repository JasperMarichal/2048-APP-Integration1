package be.kdg.integration1.team01.game2048;

import be.kdg.integration1.team01.game2048.model.*;

import java.util.ArrayList;
import java.util.Scanner;

public class _2048Application {

    private static ArrayList<LeaderboardEntry> leaderboard;
    private static ArrayList<Player> players;
    private static Game gameState;
    public static void main(String[] args) {
        Scanner keyboard = new Scanner(System.in);

        leaderboard = new ArrayList<>();
        players = new ArrayList<>();
        //load players
        players.add(new Player("me"));
        // select player or create new one
        int selectedPlayer = 0;
        gameState = new Game(4, selectedPlayer);
    }
}
