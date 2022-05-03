package team.proiectextins.domain;


import java.util.Objects;

public class MessageDTO extends Entity<Long> {
    private String from;
    private String to;
    private String message;
    private String timestamp;
    private String reply;

    public MessageDTO() {
    }

    public MessageDTO(String from, String to, String message, String timestamp) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MessageDTO)) return false;
        if (!super.equals(o)) return false;
        MessageDTO that = (MessageDTO) o;
        return Objects.equals(getFrom(), that.getFrom()) && Objects.equals(getTo(), that.getTo()) && Objects.equals(getMessage(), that.getMessage()) && Objects.equals(getTimestamp(), that.getTimestamp()) && Objects.equals(getReply(), that.getReply());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getFrom(), getTo(), getMessage(), getTimestamp(), getReply());
    }

    @Override
    public String toString() {
        return "Message{" +
                "from=" + from +
                ", to=" + to +
                '}';
    }
}


