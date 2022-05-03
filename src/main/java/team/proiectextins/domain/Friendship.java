package team.proiectextins.domain;


import java.time.LocalDate;

public class Friendship extends Entity<Tuple<Long, Long>> {
    private LocalDate friendshipDate;

    public Friendship() {
    }

    public Friendship(Tuple<Long, Long> request) {
        super();
    }

    /**
     * @return the date when the friendship was created
     */
    public LocalDate getFriendshipDate() {
        return friendshipDate;
    }

    public void setFriendshipDate(LocalDate friendshipDate) {
        this.friendshipDate = friendshipDate;
    }

    @Override
    public String toString() {
        return "Friendship{" +
                "id=" + id +
                ", friendshipDate=" + friendshipDate +
                '}';
    }
}


