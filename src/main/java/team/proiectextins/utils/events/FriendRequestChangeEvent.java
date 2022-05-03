package team.proiectextins.utils.events;

import team.proiectextins.domain.FriendRequest;

public class FriendRequestChangeEvent implements Event {

    private final FriendRequestEventType type;
    private final FriendRequest data;
    private FriendRequest oldData;

    public FriendRequestChangeEvent(FriendRequestEventType type, FriendRequest data) {
        this.type = type;
        this.data = data;
    }

    public FriendRequestChangeEvent(FriendRequestEventType type, FriendRequest data, FriendRequest oldData) {
        this.type = type;
        this.data = data;
        this.oldData = oldData;
    }

    public FriendRequestEventType getType() {
        return type;
    }

    public FriendRequest getData() {
        return data;
    }

    public FriendRequest getOldData() {
        return oldData;
    }
}
