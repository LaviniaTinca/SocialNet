package team.proiectextins.utils.events;


import team.proiectextins.domain.Message;

public class MessageChangeEvent implements Event {
    private final MessageEventType type;
    private final Message data;
    private Message oldData;

    public MessageChangeEvent(MessageEventType type, Message data) {
        this.type = type;
        this.data = data;
    }

    public MessageChangeEvent(MessageEventType type, Message data, Message oldData) {
        this.type = type;
        this.data = data;
        this.oldData = oldData;
    }

    public MessageEventType getType() {
        return type;
    }

    public Message getData() {
        return data;
    }

    public Message getOldData() {
        return oldData;
    }
}
