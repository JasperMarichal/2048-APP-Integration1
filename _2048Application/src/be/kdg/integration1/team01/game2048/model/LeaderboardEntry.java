package be.kdg.integration1.team01.game2048.model;

import java.time.LocalDateTime;

public class LeaderboardEntry {
    private int score;
    private String playerName;
    private LocalDateTime startDate;
    private LocalDateTime duration;

    public LeaderboardEntry(int score, String playerName, LocalDateTime startDate, LocalDateTime duration) {
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

    public LocalDateTime getDuration() {return duration;}

    public void setDuration(LocalDateTime duration) {this.duration = duration;}
}
