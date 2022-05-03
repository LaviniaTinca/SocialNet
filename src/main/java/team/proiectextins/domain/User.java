package team.proiectextins.domain;


//package domain;

import team.proiectextins.utils.events.Event;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User extends Entity<Long> implements Event {
    private String firstName;
    private String lastName;
    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private List<User> friendList = new ArrayList<>();


    public User(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public User(String firstName, String lastName, String uName, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = uName;
        this.password = password;
    }


    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<User> getFriendList() {
        return friendList;
    }

    public void setFriendList(List<User> friendList) {
        this.friendList = friendList;
    }

    public void addFriend(User friend) {
        this.friendList.add(friend);
    }

    public void removeFriend(User friend) {
        this.friendList.remove(friend);
    }

    @Override
    public String toString() {
        return MessageFormat.format("ID: {0}  First Name: {1}\n\t\tLast Name: {2}\n", id, firstName, lastName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        User user = (User) o;
        return firstName.equals(user.firstName) && lastName.equals(user.lastName) && friendList.equals(user.friendList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), firstName, lastName, friendList);
    }
}
