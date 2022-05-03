package team.proiectextins.utils.events;


import team.proiectextins.domain.Friendship;

public class FriendshipChangeEvent implements Event {
    private final FriendshipEventType type;
    private final Friendship data;
    private Friendship oldData;

    public FriendshipChangeEvent(FriendshipEventType type, Friendship data) {
        this.type = type;
        this.data = data;
    }

    public FriendshipChangeEvent(FriendshipEventType type, Friendship data, Friendship oldData) {
        this.type = type;
        this.data = data;
        this.oldData = oldData;
    }

    public FriendshipEventType getType() {
        return type;
    }

    public Friendship getData() {
        return data;
    }

    public Friendship getOldData() {
        return oldData;
    }
}
