package team.proiectextins.domain;

import java.time.LocalDate;

public class FriendRequestDTO extends Entity<Tuple<Long, Long>> {
    private String from;
    private String to;
    private String status;
    private LocalDate date;

    public FriendRequestDTO(String from, String to, String status, LocalDate date) {
        super();
        this.from = from;
        this.to = to;
        this.status = status;
        this.date = date;
    }

    public String getFrom() {
        return from;
    }
    public String getTo() {
        return to;
    }

    public void setFrom(String from) {
        this.from = from;
    }
    public void setTo(String to) {
        this.from = to;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
