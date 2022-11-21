package be.kdg.integration1.team01.game2048.model;

import java.time.Duration;
import java.time.LocalDateTime;

public class LeaderboardEntry {
    private int score;
    private String playerName;
    private LocalDateTime startDate;
    private Duration duration;

    public LeaderboardEntry(int score, String playerName, LocalDateTime startDate, Duration duration) {
        this.score = score;
        this.playerName = playerName;
        this.startDate = startDate;
        this.duration = duration;
    }

    public int getScore() {return score;}

    public void setScore(int score) {this.score = score;}

    public String getPlayerName() {return playerName;}

    public void setPlayerName(String playerName) {this.playerName = playerName;}

    public LocalDateTime getStartDate() {return startDate;}

    public void setStartDate(LocalDateTime startDate) {this.startDate = startDate;}

    public Duration getDuration() {return duration;}

    public void setDuration(Duration duration) {this.duration = duration;}
}
