package team.proiectextins.domain;


import java.time.LocalDate;

public class FriendRequest extends Entity<Tuple<Long, Long>> {
    private Status status;
    private LocalDate date;
    private Long from;
    private Long to;

    public Long getFrom() {
        return id.getLeft();
    }

    public Long getTo() {
        return id.getRight();
    }

    public FriendRequest() {
    }

    public FriendRequest(Tuple<Long, Long> request, Status pending, LocalDate now, Long from) {
        //super();
        this.from = id.getLeft();
    }

    public Status getStatus() {
        return status;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "FriendRequest{" +
                "from=" + id.getLeft() +
                ", to=" + id.getRight() +
                ", status=" + status +
                ", date=" + date +
                '}';
    }
}


