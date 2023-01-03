package be.kdg.integration1.team01.game2048.model;

import java.time.Duration;
import java.time.LocalDateTime;

public class LeaderboardEntry {
    private final int score;
    private final String playerName;
    private final LocalDateTime startDate;
    private final Duration duration;

    public LeaderboardEntry(int score, String playerName, LocalDateTime startDate, Duration duration) {
        this.score = score;
        this.playerName = playerName;
        this.startDate = startDate;
        this.duration = duration;
    }

    public int getScore() {return score;}

    public String getPlayerName() {return playerName;}

    public LocalDateTime getStartDate() {return startDate;}

    public Duration getDuration() {return duration;}
}
