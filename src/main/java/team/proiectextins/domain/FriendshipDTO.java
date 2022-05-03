package team.proiectextins.domain;


public class FriendshipDTO extends Entity<Tuple<Long, Long>> {
    private final String firstName;
    private final String lastName;
    private final String date;

    public FriendshipDTO(String firstName, String lastName, String date) {
        super();
        this.firstName = firstName;
        this.lastName = lastName;
        this.date = date;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getDate() {
        return date;
    }

    @Override
    public String toString() {
        return firstName + " | " + lastName + " | " + date;
    }
}
