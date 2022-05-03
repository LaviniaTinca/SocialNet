package team.proiectextins.domain;


import java.time.LocalDateTime;
import java.util.Objects;

public class Message extends Entity<Long> {
    private Long from;
    private Long to;
    private String text;
    private LocalDateTime timestamp;
    private Long reply;

    public Message(Long from, Long to, String text, LocalDateTime timestamp) {
        this.from = from;
        this.to = to;
        this.text = text;
        this.timestamp = timestamp;
    }

    public Long getFrom() {
        return from;
    }

    public Long getTo() {
        return to;
    }

    public String getText() {
        return text;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Long getReply() {
        return reply;
    }

    public void setReply(Long reply) {
        this.reply = reply;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message)) return false;
        Message that = (Message) o;
        return Objects.equals(getFrom(), that.getFrom()) && Objects.equals(getTo(), that.getTo()) && Objects.equals(getText(), that.getText()) && Objects.equals(getTimestamp(), that.getTimestamp());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFrom(), getTo(), getText(), getTimestamp());
    }
}


