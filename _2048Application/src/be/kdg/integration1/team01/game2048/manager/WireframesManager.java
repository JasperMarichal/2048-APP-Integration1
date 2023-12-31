package be.kdg.integration1.team01.game2048.manager;

import be.kdg.integration1.team01.game2048.model.LeaderboardEntry;
import be.kdg.integration1.team01.game2048.model.Wireframe;
import be.kdg.integration1.team01.game2048.model.Game;

import static be.kdg.integration1.team01.game2048._2048Application.leaderboard;

public class WireframesManager {

    public static void displayWireframe(Wireframe wf, Game activeSession) {
        String wireframe = switch (wf) {
            case WELCOME -> {
                yield   " _ _ _  ___  _    ___  ___  __ __  ___   ___  ___   ___  ___   __   ___ \n" +
                        "| | | || __>| |  |  _>| . ||  \\  \\| __> |_ _|| . | <_  >|   | /. | < . >\n" +
                        "| | | || _> | |_ | <__| | ||     || _>   | | | | |  / / | / |/_  .|/ . \\\n" +
                        "|__/_/ |___>|___|`___/`___'|_|_|_||___>  |_| `___' <___>`___'  |_| \\___/\n" +
                        "                                                                        \n" +
                        " ___   ___  ___   ___  ___  ___  ___  ___  ___  ___                     \n" +
                        "/  _> | __>|_ _| / __>|_ _|| . || . \\|_ _|| __>| . \\                    \n" +
                        "| <_/\\| _>  | |  \\__ \\ | | |   ||   / | | | _> | | |                    \n" +
                        "`____/|___> |_|  <___/ |_| |_|_||_\\_\\ |_| |___>|___/\n" +
                        " \n" +
                        "<Press Enter to start a game> ";
            }
            case RULES -> {
                yield   " ______          _                                             \n" +
                        "(_____ \\        | |                                            \n" +
                        " _____) ) _   _ | |  _____   ___    ____   _____   ____  _____ \n" +
                        "|  __  / | | | || | | ___ | /___)  |  _ \\ (____ | / _  || ___ |\n" +
                        "| |  \\ \\ | |_| || | | ____||___ |  | |_| |/ ___ |( (_| || ____|\n" +
                        "|_|   |_||____/  \\_)|_____)(___/   |  __/ \\_____| \\___ ||_____)\n" +
                        "                                   |_|           (_____|      \n" +
                        "\n" +
                        "- Use 0 to move up\n" +
                        "- Use 1 to move right\n" +
                        "- Use 2 to move down\n" +
                        "- Use 3 to move left\n" +
                        "- Join tiles to get 2048\n" +
                        "- Join the same tiles\n" +
                        "- Join higher numbers to get a high score\n" +
                        "- Leave the higher number in a corner\n" +
                        "- Join smaller numbers\n" +
                        "- Repeat joining titles until you have 2048\n" +
                        "- Game is over when there are no more tiles that can be matched \n";
            }
            case COMMANDS -> {
                yield   " _____                                           _     \n" +
                        "/  __ \\                                         | |    \n" +
                        "| /  \\/ ___  _ __ ___  _ __ ___   __ _ _ __   __| |___ \n" +
                        "| |    / _ \\| '_ ` _ \\| '_ ` _ \\ / _` | '_ \\ / _` / __|\n" +
                        "| \\__/\\ (_) | | | | | | | | | | | (_| | | | | (_| \\__ \\\n" +
                        " \\____/\\___/|_| |_| |_|_| |_| |_|\\__,_|_| |_|\\__,_|___/\n" +
                        "Here are the commands you can use:\n" +
                        "Q - to quit the game \n" +
                        "L - to open a leaderboard page (Usage: L [A][F <player_name>] )\n" +
                        "H - to open a commands page \n" +
                        "R - to open a Rules page \n" +
                        "N - to start a new game\n" +
                        "0 - to move the tiles up\n" +
                        "1 - to move the tiles right\n" +
                        "2 - to move the tiles down\n" +
                        "3 - to move the tiles left\n";
            }
            case LEADERBOARD -> {
                StringBuilder formattedEntries = new StringBuilder();
                int i = 0;
                for(LeaderboardEntry entry : leaderboard) {
                    formattedEntries.append(" %20s  %,16d  %8d  %10s\n".formatted(entry.getPlayerName(), entry.getScore(), entry.getDuration().getSeconds(), entry.getStartDate().toLocalDate()));
                    i++;
                }
                for (int j = i; j < 5; j++) {
                    formattedEntries.append(" %20s  %16s  %8s  %10s\n".formatted("-", "-", "-", "-"));
                }
                yield   " _                    _           _                         _ \n" +
                        "| |                  | |         | |                       | |\n" +
                        "| |     ___  __ _  __| | ___ _ __| |__   ___   __ _ _ __ __| |\n" +
                        "| |    / _ \\/ _` |/ _` |/ _ \\ '__| '_ \\ / _ \\ / _` | '__/ _` |\n" +
                        "| |___|  __/ (_| | (_| |  __/ |  | |_) | (_) | (_| | | | (_| |\n" +
                        "\\_____/\\___|\\__,_|\\__,_|\\___|_|  |_.__/ \\___/ \\__,_|_|  \\__,_|\n" +
                        "Name:                 Score:            Time(s):  Date: \n" + formattedEntries;
            }
            case GAMEBOARD -> {
                if (activeSession == null) {
                    yield "ERROR: Cannot display board without a game-session provided.";
                } else {
                    yield   "Score: %,d  Turn: %d  %s\n".formatted(activeSession.getCurrentScore()
                            , activeSession.getTurns().size()
                            , activeSession.getGameId() > 0 ? "Save#"+activeSession.getGameId() : "No save") +
                            activeSession.getBoard().toString() +
                            "Type 0 or 1 or 2 or 3 to slide the tiles up/right/down/left or use WASD controls!\n" +
                            "Otherwise H for “Help” or Q to stop the game\n";
                }
            }
        };
        System.out.println(wireframe);
    }
}
