package team.proiectextins.service;


import team.proiectextins.controller.FriendsController;
import team.proiectextins.domain.Friendship;
import team.proiectextins.domain.FriendshipDTO;
import team.proiectextins.domain.Tuple;
import team.proiectextins.domain.User;
import team.proiectextins.domain.validators.exceptions.DataException;
import team.proiectextins.domain.validators.exceptions.FriendshipException;
import team.proiectextins.domain.validators.exceptions.ValidationException;
import team.proiectextins.repository.Repository;
import team.proiectextins.utils.events.FriendRequestChangeEvent;
import team.proiectextins.utils.events.FriendRequestEventType;
import team.proiectextins.utils.events.FriendshipChangeEvent;
import team.proiectextins.utils.events.FriendshipEventType;
import team.proiectextins.utils.observer.Observable;
import team.proiectextins.utils.observer.Observer;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Service implements Observable<FriendshipChangeEvent> {
    private final Repository<Long, User> repoUser;
    private final Repository<Tuple<Long, Long>, Friendship> repoFriendship;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);

    public Service(Repository<Long, User> repoUser, Repository<Tuple<Long, Long>, Friendship> repoFriendship) {
        this.repoUser = repoUser;
        this.repoFriendship = repoFriendship;
    }

    /**
     * getter pt useri
     *
     * @return iterabil de useri
     */
    public Iterable<User> getAllUsers() {
        return repoUser.findAll();
    }

    public User getUserById(Long id) {
        return repoUser.findOne(id);
    }

    /**
     * getter pt useri dupa username
     *
     * @param userName String, dat din ui
     * @return un user cu username-ul cerut
     */
    public User getUserByUsername(String userName) {
        return repoUser.findOne(userName);
    }

    /**
     * Adauga un user
     *
     * @param firstName String
     * @param lastName  String
     *                  //* @return null if the entity is succesfully saved, otherwise returns the entity
     */
    public void addUser(String firstName, String lastName, String userName, String password) {
        repoUser.save(new User(firstName, lastName, userName, password));
    }

    /**
     * Sterge un user dupa un id dat
     *
     * @param id Long id-ul user-ului
     * @return the removed user if the user is deleted, null otherwise
     */
    public User deleteUser(Long id) {
        User removed = repoUser.delete(id);
        if (removed == null) {
            throw new ValidationException("Id-ul nu este valid!");
        }

        // remove all friendships concerning this user
        repoFriendship.findAll().forEach(friendship -> {
            if (Objects.equals(friendship.getId().getLeft(), id) ||
                    Objects.equals(friendship.getId().getRight(), id)) {
                repoFriendship.delete(friendship.getId());
            }
        });

        // stergem user-ul din listele prietenilor
        repoUser.findAll().forEach(user -> user.removeFriend(removed));

        return removed;
    }

    /**
     * modifica un user
     *
     * @param id        Long
     * @param firstName string
     * @param lastName  string
     */
    public User updateUser(Long id, String firstName, String lastName) {
        User currentUser = new User(firstName, lastName);
        currentUser.setFriendList(repoUser.findOne(id).getFriendList());
        currentUser.setId(id);

        User updated = repoUser.update(currentUser);
        if (updated != null) {
            throw new ValidationException("Id-ul nu exista!");
        }

        return currentUser;
    }

    public Iterable<Friendship> getAllFriendships() {
        return repoFriendship.findAll();
    }

    public Friendship getFriendshipByID(Tuple<Long, Long> id) {
        return repoFriendship.findOne(id);
    }

    /**
     * Adauga o noua prietenie
     *
     * @param id1 LOng -id user1
     * @param id2 Long -id user2
     * @throws FriendshipException daca prietenia exista sau id-urile nu exista
     */
    public void addFriendship(Long id1, Long id2) {
        Friendship friendship = new Friendship();
        friendship.setId(new Tuple<>(id1, id2));
        friendship.setFriendshipDate(LocalDate.now());

        if (repoUser.findOne(id1) == null || repoUser.findOne(id2) == null) {
            throw new FriendshipException("Cel putin un utilizator este inexistent!");
        }

        Friendship added = repoFriendship.save(friendship);
        if (added != null) {
            throw new FriendshipException("Prietenia exista deja!");
        }
    }

    /**
     * sterge o prietenie
     *
     * @param id1 Long -id user1
     * @param id2 Long - id user2
     * @return prietenia stearsa
     * @throws FriendshipException if the friendship or the given ids does not exist
     */
    public Friendship removeFriendship(Long id1, Long id2) {
        if (repoUser.findOne(id1) == null || repoUser.findOne(id2) == null) {
            throw new FriendshipException("Cel putin unul dintre utilizatori nu exista");
        }

        Friendship removed = repoFriendship.delete(new Tuple<>(id1, id2));
        Friendship removedReversed = repoFriendship.delete(new Tuple<>(id2, id1));

        if (removed == null && removedReversed == null) {
            throw new FriendshipException("Prietenia nu exista!");
        } else {
            notifyObservers(new FriendshipChangeEvent(FriendshipEventType.DELETE, removed));
            notifyObservers(new FriendshipChangeEvent(FriendshipEventType.DELETE, removedReversed));
        }

        return removed;
    }

    public int getSize() {
        return ((Collection<?>) repoUser.findAll()).size();
    }

    /**
     * predicat pentru filtrarea  prieteniilor dupa user
     *
     * @param userId Long -id-ul user-ului logat
     * @return predicate
     */
    public Predicate<Friendship> predicateFriendsOfAUser(Long userId) {
        return friendship -> (
                friendship.getId().getLeft().equals(userId) ||
                        friendship.getId().getRight().equals(userId)
        );
    }

    /**
     * predicat pt filtrarea prieteniilor dupa userId si luna prieteniei
     *
     * @param userId long - id-ul userului logat
     * @param month  int -luna prieteniei
     * @return predicate
     */
    public Predicate<Friendship> predicateFriendsOfAUserByMonth(Long userId, int month) {
        return friendship -> {
            LocalDate date = LocalDate.parse(friendship.getFriendshipDate().toString(), formatter);
            return ((friendship.getId().getLeft().equals(userId) ||
                    friendship.getId().getRight().equals(userId)) &&
                    date.getMonthValue() == month);
        };
    }

    /**
     * stream pentru filtrarea prieteniilor dupa user logat si un predicat variabil
     *
     * @param userId    Long id-ul userului logat
     * @param predicate Predicate
     * @return lista cu prietenii, filtrata dupa predicatul dat
     */
    public List<FriendshipDTO> getFriendsStream(long userId, Predicate<Friendship> predicate) {
        return StreamSupport.stream(repoFriendship.findAll().spliterator(), false)
                .filter(predicate)
                .map(friendship -> {
                    User friend;
                    if (friendship.getId().getLeft().equals(userId)) {
                        friend = repoUser.findOne(friendship.getId().getRight());
                    } else {
                        friend = repoUser.findOne(friendship.getId().getLeft());
                    }
                    FriendshipDTO f = new FriendshipDTO(
                            friend.getFirstName(),
                            friend.getLastName(),
                            friendship.getFriendshipDate().toString());
                    f.setId(friendship.getId());
                    return f;
                }).collect(Collectors.toList());
    }

    /**
     * filtrare useri dupa un string dat in ui search
     *
     * @param s String
     * @return lista filtrata
     */
    public List<User> getAllUsersFilterStream(String s) {
        Predicate<User> p1 = n -> n.getFirstName().toLowerCase().contains(s.toLowerCase());
        Predicate<User> p2 = n -> n.getLastName().toLowerCase().contains(s.toLowerCase());
        return StreamSupport.stream(repoUser.findAll().spliterator(), false)
                .filter(p1.or(p2))
                .collect(Collectors.toList());
    }

    /**
     * logare user
     *
     * @param id Long- id-ul userului
     * @return user
     */
    public User validateIdUser(long id) {
        User user = repoUser.findOne(id);
        if (user == null) {
            throw new ValidationException("Nu exista user cu id-ul dat!");
        }
        return user;
    }

    public void userLogin(long id, int userPassword) {
        User user = repoUser.findOne(id);
        //if (user == null || user.getPasswordHash() != userPassword) {
        //in loc de parola voi folosi tot id-ul pana setez campurile username si password in user
        if (user == null || user.getId() != userPassword) {
            throw new ValidationException("Username sau parola invalida\n!");
        }

    }

    public void validateData(int month) throws DataException {
        if (month < 1 || month > 12) {
            throw new DataException("Luna data nu este valida!");
        }
    }

    /**
     * lista de observatori
     */
    private final List<Observer<FriendshipChangeEvent>> observers = new ArrayList<>();

    @Override
    public void addObserver(Observer<FriendshipChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<FriendshipChangeEvent> e) {
        //observers.remove(e);
    }

    @Override
    public void notifyObservers(FriendshipChangeEvent t) {
        observers.forEach(x -> x.update(t));
    }
}


